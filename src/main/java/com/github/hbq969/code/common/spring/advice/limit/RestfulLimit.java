package com.github.hbq969.code.common.spring.advice.limit;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author : hbq969@gmail.com
 * @description : 接口限流标记注解
 * @createTime : 2023/9/8 13:50
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RestfulLimit {

  /**
   * restful接口调用限制值
   *
   * @return
   */
  int value() default 50;

  /**
   * 采样时间值
   *
   * @return
   */
  long time() default 1;

  /**
   * 采样时间单位
   *
   * @return
   */
  TimeUnit unit() default TimeUnit.SECONDS;
}
