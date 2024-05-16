package com.github.hbq969.code.common.utils;

import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * @author : hbq969@gmail.com
 * @description : 反射相关工具方法
 * @createTime : 14:06:35, 2023.04.04, 周二
 */
public abstract class ReflectUtils {

  /**
   * 查询由cglib包装的原始类
   *
   * @param targetClass
   * @return
   */
  public static Class<?> getUnCglibSuperClass(Class<?> targetClass) {
    Assert.notNull(targetClass);
    if (ClassUtils.isCglibProxyClass(targetClass)) {
      return getUnCglibSuperClass(targetClass.getSuperclass());
    }
    return targetClass;
  }


}
