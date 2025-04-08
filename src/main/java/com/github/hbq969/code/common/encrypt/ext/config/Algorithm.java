package com.github.hbq969.code.common.encrypt.ext.config;

/**
 * @author hbq969@gmail.com
 */
public enum Algorithm {
  /**
   * 对称加密
   */
  AES,
  /**
   * 非对称加密
   */
  RSA,
  /**
   * 请求报文使用aes加密，aes的key和iv使用rsa加密
   */
  RAS
}
