package com.github.hbq969.code.common.spring.advice.log;


import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface LogSet {
    boolean printIn() default true;

    boolean printResult() default true;
}
