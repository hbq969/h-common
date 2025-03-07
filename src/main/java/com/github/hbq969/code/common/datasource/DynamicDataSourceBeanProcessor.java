package com.github.hbq969.code.common.datasource;

import com.github.hbq969.code.common.datasource.context.ThreadLocalPolicy;
import com.github.hbq969.code.common.spring.context.SpringContext;
import com.github.hbq969.code.common.utils.AnnotationUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.Optional;

/**
 * @author : hbq969@gmail.com
 * @description : 动态数据源动态转发拦截器
 * @createTime : 17:53:48, 2023.03.28, 周二
 */
@Slf4j
public class DynamicDataSourceBeanProcessor implements BeanPostProcessor {

    @Autowired
    private SpringContext context;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object target, String beanName)
            throws BeansException {
        Class<?> clz = AopProxyUtils.ultimateTargetClass(target);
        DS ds = org.springframework.core.annotation.AnnotationUtils.findAnnotation(clz, DS.class);
        // 类上找不到DS注解
        if (Objects.isNull(ds)) {
            Class<?>[] interfaces = clz.getInterfaces();
            Optional<Class<?>> ip = AnnotationUtils
                    .getAnnotationFromInterface(interfaces, DS.class);
            // 接口上找到DS注解
            if (ip.isPresent()) {
                if (log.isDebugEnabled()) {
                    log.debug("{} 满足多数据源接口级别的@DS增强", beanName);
                }
                ds = org.springframework.core.annotation.AnnotationUtils.findAnnotation(ip.get(), DS.class);
                ClassLoader loader = clz.getClassLoader();
                return createJdkProxy(target, beanName, clz, ds, interfaces, loader);
            }
            // 该类上任意接口都找不到DS注解
            else {
                return target;
            }
        }
        // 类上找到注解
        else {
            if (log.isDebugEnabled()) {
                log.debug("{} 满足多数据源类级别的@DS增强", beanName);
            }
            // 判断是否已经被JDK动态代理，因为Proxy代理类是没法被继承子类化的
            if (AopUtils.isJdkDynamicProxy(target) || StringUtils.contains(clz.getName(), "$Proxy")) {
                ClassLoader loader = clz.getClassLoader();
                Class<?>[] interfaces = clz.getInterfaces();
                return createJdkProxy(target, beanName, clz, ds, interfaces, loader);
            } else {
                Enhancer enhancer = new Enhancer();
                enhancer.setSuperclass(clz);
                DS cds = ds;
                enhancer.setCallback((MethodInterceptor) (obj, method, args, mp) -> {
                    try {
                        if (method.isAnnotationPresent(DS.class)) {
                            DS mds = method.getAnnotation(DS.class);
                            log.debug("方法[{}.{}(...)]同时满足@DS，类[{}]和方法[{}]增强，优先使用方法增强", beanName,
                                    method.getName(), cds.value().getSimpleName(), mds.value().getSimpleName());
                            return method.invoke(target, args);
                        }
                        log.debug("方法[{}.{}(...)]满足类级别的@DS增强", beanName, method.getName());
                        DynamicDataSourceAspect.setContext(context, cds, args, clz, method);
                        return method.invoke(target, args);
                    } finally {
                        ThreadLocalPolicy.remove();
                    }
                });
                return enhancer.create();
            }
        }
    }

    private Object createJdkProxy(Object target, String beanName, Class<?> clz, DS ds, Class<?>[] interfaces, ClassLoader loader) {
        return Proxy.newProxyInstance(loader, interfaces, (proxy, method, args) -> {
            try {
                // 方法标记交给aspect处理
                if (method.isAnnotationPresent(DS.class)) {
                    DS mds = org.springframework.core.annotation.AnnotationUtils.getAnnotation(method, DS.class);
                    log.debug("方法[{}.{}(...)]同时满足@DS，接口[{}]和方法[{}]增强，优先使用方法增强", beanName,
                            method.getName(), ds.value().getSimpleName(), mds.value().getSimpleName());
                    return method.invoke(target, args);
                }
                log.debug("方法[{}.{}(...)]满足接口级别的@DS增强", beanName, method.getName());
                DynamicDataSourceAspect.setContext(context, ds, args, clz, method);
                return method.invoke(target, args);
            } finally {
                ThreadLocalPolicy.remove();
            }
        });
    }
}
