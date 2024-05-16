package com.github.hbq969.code.common.lock.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @author : hbq969@gmail.com
 * @description : 分布式锁注解
 * @createTime : 2023/8/25 11:06
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DistLock {

  /**
   * 锁KEY值
   *
   * @return
   */
  String lockKey() default "";

  /**
   * 锁KEY在redis中超时过期时间值
   *
   * @return
   */
  long expire() default 30;

  /**
   * 锁KEY在redis中超时过期时间单位
   *
   * @return
   */
  TimeUnit unit() default TimeUnit.SECONDS;

  /**
   * 最大重试次数
   *
   * @return
   */
  int retryCount() default 45;

  /**
   * 重试间隔时间值
   *
   * @return
   */
  long retryCycle() default 1;

  /**
   * 重试时间单位
   *
   * @return
   */
  TimeUnit retryUnit() default TimeUnit.SECONDS;
}
