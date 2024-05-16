package com.github.hbq969.code.common.cache.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author : hbq969@gmail.com
 * @description : java本地缓存
 * @createTime : 14:43:03, 2023.04.01, 周六
 */
@ConfigurationProperties(prefix = "spring.cache.ext.juc")
@Data
public class LinkedHashMap {

  /**
   * 是否启用
   */
  private boolean enable = true;

  /**
   * 是否允许值为空
   */
  private boolean allowNullValues = true;

  /**
   * 缓存最大容量
   */
  private int maxCapacity = 5000;
}
