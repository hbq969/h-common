package com.github.hbq969.code.common.datasource;

import cn.hutool.core.util.ArrayUtil;
import com.github.hbq969.code.common.datasource.context.AbstractLookupKeyPolicy;
import com.github.hbq969.code.common.datasource.context.ContextPolicy;
import com.github.hbq969.code.common.datasource.context.ThreadLocalPolicy;
import com.github.hbq969.code.common.spring.context.SpringContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;

/**
 * @author : hbq969@gmail.com
 * @description : 动态数据源动态转发拦截器
 * @createTime : 17:38:48, 2023.03.28, 周二
 */
@Aspect
@Slf4j
public class DynamicDataSourceAspect implements ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context=applicationContext;
    }

    @Pointcut("@annotation(DS)")
    public void dataSourcePointCut() {

    }

    @Around("dataSourcePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        try {
            getDSAnnotation(point).ifPresent(ds -> {
                Object[] args = point.getArgs();
                Class<?> clz = point.getTarget().getClass();
                MethodSignature ms = (MethodSignature) point.getSignature();
                Method m = ms.getMethod();
                setContext(context, ds, args, clz, m);
            });
            return point.proceed();
        } finally {
            ThreadLocalPolicy.remove();
        }
    }

    // DS为类的注解
    public static void setContext(ApplicationContext context, DS ds, Object[] args, Class<?> targetClass,
                                  Method m) {
        ContextPolicy ctx = getContextPolicy(ds, args, targetClass, m);
        if (Objects.nonNull(ctx)) {
            springContextBind(context, ctx);
            ThreadLocalPolicy.set(ctx);
            if (log.isDebugEnabled()) {
                log.debug("选择的数据源为: {}， 当前线程: [{}]", ctx.getDatasourceKey(),
                        Thread.currentThread().getName());
            }
        }
    }

    private static void springContextBind(ApplicationContext context, ContextPolicy ctx) {
        if (ctx instanceof AbstractLookupKeyPolicy) {
            AbstractLookupKeyPolicy akp = AbstractLookupKeyPolicy.class.cast(ctx);
            akp.setContext(context.getBean(SpringContext.class));
        }
    }

    public static ContextPolicy getContextPolicy(DS ds, Object[] args, Class<?> targetClass,
                                                 Method m) {

        Class<? extends ContextPolicy> clz = ds.context();
        ContextPolicy ctx = null;

        if (ArrayUtil.isEmpty(args)) {
            String msg = "{}.{}() 为无参方法，使用注解中定义的策略类型无参构造器创建实例";
            ctx = createContextPolicyWithNoConstructor(msg, targetClass, m, clz);
        } else {
            boolean find = false;
            for (Object arg : args) {
                if (Objects.nonNull(arg) && clz.equals(arg.getClass())) {
                    ctx = clz.cast(arg);
                    find = true;
                    break;
                }
            }
            if (!find) {
                String msg = "{}.{}(..) 中未找到注解定义的策略参数，使用注解中定义的策略类型无参构造器创建实例";
                ctx = createContextPolicyWithNoConstructor(msg, targetClass, m, clz);
            }
        }
        return ctx;
    }

    private static ContextPolicy createContextPolicyWithNoConstructor(String msg,
                                                                      Class<?> targetClass, Method m, Class<? extends ContextPolicy> clz) {
        ContextPolicy ctx;
        try {
            ctx = clz.newInstance();
            if (log.isDebugEnabled()) {
                log.debug(msg, targetClass.getName(), m.getName());
            }
        } catch (Exception e) {
            msg = MessageFormat.format("[{0}]没有无参构造器，没法实例化", clz.getName());
            throw new UnsupportedOperationException(msg);
        }
        return ctx;
    }

    /**
     * 根据类或方法获取数据源注解
     */
    private Optional<DS> getDSAnnotation(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return Optional.ofNullable(AnnotationUtils.findAnnotation(signature.getMethod(), DS.class));
    }
}
