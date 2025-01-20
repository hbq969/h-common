package com.github.hbq969.code.common.log.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
    /**
     * 是否启用日志采集
     *
     * @return
     */
    boolean enabled() default true;

    /**
     * 是否采集请求体，如果是自行实现的采集策略请遵守此约定
     *
     * @return
     */
    boolean collectPostBody() default true;

    /**
     * 是否采集接口结果，如果是自行实现的采集策略请遵守此约定
     *
     * @return
     */
    boolean collectResult() default false;
}
