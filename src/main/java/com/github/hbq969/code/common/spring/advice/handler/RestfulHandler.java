package com.github.hbq969.code.common.spring.advice.handler;

import org.aspectj.lang.ProceedingJoinPoint;

public interface RestfulHandler {
    void before(ProceedingJoinPoint point);

    void after(ProceedingJoinPoint point, Object result);

    void exception(ProceedingJoinPoint point, Throwable e);

    /**
     * 值越小优先级越高
     *
     * @return
     */
    default int order() {
        return 0;
    }

    default boolean enabled(){
        return true;
    }
}
