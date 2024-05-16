package com.github.hbq969.code.common.encrypt.ext.config;


import com.github.hbq969.code.common.encrypt.ext.exception.EncryptRequestException;

import java.lang.annotation.*;

/**
 * @author hbq969@gmail.com
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Decrypt {

  /**
   * 加密算法
   *
   * @return
   */
  Algorithm algorithm() default Algorithm.AES;

  /**
   * 请求参数一定要是加密内容
   */
  boolean required() default true;

  /**
   * 请求数据时间戳校验时间差 超过(当前时间-指定时间)的数据认定为伪造 注意应用程序需要捕获 {@link EncryptRequestException} 异常
   */
  long timeout() default 3000;
}
