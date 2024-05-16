package com.github.hbq969.code.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 类拷贝
 *
 * @param <T>
 * @author
 */
public final class BeanCopyUtil<T extends Serializable> implements Serializable {

  private static final Logger logger = LoggerFactory.getLogger(BeanCopyUtil.class);

  private static final long serialVersionUID = 1L;

  private BeanCopyUtil() {
  }

  /**
   * @param source
   * @param clazz
   * @return
   */
  public static <T> List<T> copyList(List<?> source, Class<T> clazz) {
    if (source == null || source.size() == 0) {
      return Collections.emptyList();
    }
    List<T> res = new ArrayList<>(source.size());
    for (Object o : source) {
      try {
        T t = clazz.newInstance();
        res.add(t);
        BeanUtils.copyProperties(o, t);
      } catch (Exception e) {
        logger.error("copyList error", e);
      }
    }
    return res;
  }

  /**
   * 将对象属性拷贝到目标类型的同名属性字段中
   *
   * @param <T>
   * @param source
   * @param targetClazz
   * @return
   */
  public static <T> T copyProperties(Object source, Class<T> targetClazz) {

    T target = null;
    try {
      target = targetClazz.newInstance();
      BeanUtils.copyProperties(source, target);
    } catch (Exception e) {
      logger.error("copyProperties error", e);
    }
    return target;
  }

}

