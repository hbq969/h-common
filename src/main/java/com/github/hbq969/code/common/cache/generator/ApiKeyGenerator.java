package com.github.hbq969.code.common.cache.generator;

import com.github.hbq969.code.common.cache.Expire;
import com.github.hbq969.code.common.cache.ExpireKey;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author : hbq969@gmail.com
 * @description : 缓存KEY生成器[API接口]
 * @createTime : 15:05:29, 2023.03.31, 周五
 */
public class ApiKeyGenerator extends SimpleKeyGenerator {

  @Override
  public Object generate(Object target, Method method, Object... params) {
    return createKey(true, target, method, params);
  }

  public static Object createKey(boolean hasMethod, Object target, Method method, Object[] params) {
    Expire expire = AnnotationUtils.findAnnotation(method, Expire.class);
    StringBuilder sb = new StringBuilder(200);
    sb.append(target.getClass().getName());
    if (hasMethod) {
      sb.append(method.getName());
    } else if (expire != null) {
      if (StringUtils.isNotEmpty(expire.methodKey())) {
        sb.append('.').append(expire.methodKey());
      }
    }
    sb.append("[");
    Object param;
    for (int i = 0; i < params.length; i++) {
      if (i > 0) {
        sb.append(",");
      }
      param = params[i];
      if (param == null) {
      } else if (ClassUtils.isPrimitiveArray(param.getClass())
          || ClassUtils.isPrimitiveWrapperArray(param.getClass())) {
        int len = Array.getLength(param);
        sb.append("[");
        for (int i1 = 0; i1 < len; i1++) {
          if (i1 > 0) {
            sb.append(",");
          }
          sb.append(Array.get(param, i1));
        }
        sb.append("]");
      } else if (param.getClass().isArray()) {
        Object[] array = (Object[]) param;
        for (int i1 = 0; i1 < array.length; i1++) {
          if (i1 > 0) {
            sb.append(",");
          }
          sb.append(array[i1]);
        }
      } else {
        sb.append(param);
      }
    }
    sb.append("]");
    String key = sb.toString();
    TimeUnit unit = TimeUnit.SECONDS;
    long time = -1;
    if (expire != null) {
      unit = expire.unit();
      time = expire.time();
    }
    return new ExpireKey(key, time, unit, System.currentTimeMillis());
  }
}
