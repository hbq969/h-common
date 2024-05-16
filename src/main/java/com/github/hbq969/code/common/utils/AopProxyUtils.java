package com.github.hbq969.code.common.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author : hbq969@gmail.com
 * @description : Spring aop工具类
 * @createTime : 20:02:37, 2023.04.05, 周三
 */
@Slf4j
public abstract class AopProxyUtils {

  public static Class<?>[] proxiedUserInterfaces(Object proxy) {
    Class<?>[] proxyInterfaces = null;
    try {
      proxyInterfaces = org.springframework.aop.framework.AopProxyUtils.proxiedUserInterfaces(proxy);
    } catch (Exception e) {
      log.info("对象[{}]无任何用户接口", proxy);
    }
    return proxyInterfaces;
  }
}
