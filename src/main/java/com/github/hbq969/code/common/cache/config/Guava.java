package com.github.hbq969.code.common.cache.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author : hbq969@gmail.com
 * @description : Guava缓存扩展配置
 * @createTime : 14:33:13, 2023.04.01, 周六
 */
@ConfigurationProperties(prefix = "spring.cache.ext.guava")
@Data
public class Guava {

  /**
   * 是否启用
   */
  private boolean enable = false;

  /**
   * 缓存最大容量
   */
  private int maxCapacity = 5000;

  /**
   * 缓存初始化容量
   */
  private int initialCapacity = (int) (maxCapacity * 0.3f);

  /**
   * 缓存并发查询线程数
   */
  private int concurrencyLevel = 4;

  /**
   * 是否允许值为空
   */
  private boolean allowNullValues = true;
}
