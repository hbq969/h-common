package com.github.hbq969.code.common.datasource;

import com.github.hbq969.code.common.datasource.context.ContextPolicy;
import com.github.hbq969.code.common.datasource.context.DefaultPolicy;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : hbq969@gmail.com
 * @description : 动态数据源动态代理标记注解
 * @createTime : 15:38:48, 2023.03.28, 周二
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DS {

    @AliasFor("context")
    Class<? extends ContextPolicy> value() default DefaultPolicy.class;

    /**
     * 上下文实现
     *
     * @return
     */
    @AliasFor("value")
    Class<? extends ContextPolicy> context() default DefaultPolicy.class;
}
