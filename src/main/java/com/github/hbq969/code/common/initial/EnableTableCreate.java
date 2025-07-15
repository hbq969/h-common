package com.github.hbq969.code.common.initial;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表创建逻辑扩展，优先级高于ScriptInitialAware.tableCreate()
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableTableCreate {
    boolean value() default true;
}
