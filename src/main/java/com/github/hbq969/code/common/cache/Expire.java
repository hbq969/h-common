package com.github.hbq969.code.common.cache;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author : hbq969@gmail.com
 * @description : 缓存过期标记注解
 * @createTime : 15:07:11, 2023.03.31, 周五
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Expire {

  /**
   * 过期时间单位
   *
   * @return
   */
  TimeUnit unit() default TimeUnit.MILLISECONDS;

  /**
   * 过去时间值
   *
   * @return
   */
  long time() default 300000L;

  /**
   * 可以不指定，除非存在有相同参数的方法时
   *
   * @return
   */
  String methodKey() default "";
}
