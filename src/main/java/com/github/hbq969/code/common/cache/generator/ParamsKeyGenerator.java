package com.github.hbq969.code.common.cache.generator;

import org.springframework.cache.interceptor.SimpleKeyGenerator;

import java.lang.reflect.Method;

/**
 * @author : hbq969@gmail.com
 * @description : 缓存KEY生成器[方法参数]
 * @createTime : 15:04:36, 2023.03.31, 周五
 */
public class ParamsKeyGenerator extends SimpleKeyGenerator {

  @Override
  public Object generate(Object target, Method method, Object... params) {
    return ApiKeyGenerator.createKey(false, target, method, params);
  }
}
