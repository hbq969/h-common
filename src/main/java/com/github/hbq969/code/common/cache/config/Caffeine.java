package com.github.hbq969.code.common.cache.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author : hbq969@gmail.com
 * @description : Caffeine缓存扩展配置
 * @createTime : 18:13:39, 2023.04.01, 周六
 */
@ConfigurationProperties(prefix = "spring.cache.ext.caffeine")
@Data
public class Caffeine {

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
   * 是否允许值为空
   */
  private boolean allowNullValues = true;
}
