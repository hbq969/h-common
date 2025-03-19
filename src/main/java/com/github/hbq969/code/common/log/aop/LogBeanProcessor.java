package com.github.hbq969.code.common.log.aop;

import cn.hutool.core.util.ArrayUtil;
import com.github.hbq969.code.common.log.api.Log;
import com.github.hbq969.code.common.spring.context.SpringContext;
import com.github.hbq969.code.common.utils.AnnotationUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.Optional;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

@Slf4j
public class LogBeanProcessor implements BeanPostProcessor {

    @Autowired
    private SpringContext context;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object target, String beanName) throws BeansException {
        try {
            Class<?> clz = AopProxyUtils.ultimateTargetClass(target);
            Log ret = findAnnotation(clz, Log.class);
            // 类上找不到注解，或注解不启用，返回不做代理
            if (Objects.isNull(ret)) {
                // 获取所有用户接口
                Class<?>[] proxyUserInterfaces = clz.getInterfaces();
                // 接口上找下是否有注解
                if (ArrayUtil.isNotEmpty(proxyUserInterfaces)) {
                    ClassLoader loader = clz.getClassLoader();
                    Optional<Class<?>> ip = AnnotationUtils
                            .getAnnotationFromInterface(proxyUserInterfaces, Log.class);
                    // 找到用户接口存在注解，则代理
                    if (ip.isPresent()) {
                        Class<?> inter = ip.get();
                        ret = org.springframework.core.annotation.AnnotationUtils.findAnnotation(inter, Log.class);
                        if(log.isDebugEnabled())
                            log.debug("{} 的接口[{}]上找到注解Log，并且是启用的。", inter);
                        InvocationHandler handler = new LogHandlerImpl(context, ret, target);
                        return Proxy.newProxyInstance(loader, new Class<?>[]{inter}, handler);
                    } else {
                        return target;
                    }
                }
                return target;
            }
            // 类上存在注解
            else {
                if (ret.enabled()) {
                    if(log.isDebugEnabled())
                        log.debug("在类[{}]上找到注解Log，并且是启用的。", clz);
                    Enhancer enhancer = new Enhancer();
                    enhancer.setSuperclass(clz);
                    enhancer.setCallback(new LogCallbackImpl(context, ret, target));
                    return enhancer.create();
                } else {
                    if(log.isDebugEnabled())
                        log.debug("在类[{}]上找到注解Log，但是没有启用。", clz);
                    return target;
                }
            }
        } catch (Throwable e) {
            log.error("处理Log注解类级别代理异常", e);
            return target;
        }
    }
}
