package com.github.hbq969.code.common.encrypt.ext.config;

import java.lang.annotation.*;

/**
 * @author hbq969@gmail.com
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Encrypt {

  /**
   * 加密算法
   *
   * @return
   */
  Algorithm algorithm() default Algorithm.AES;
}
