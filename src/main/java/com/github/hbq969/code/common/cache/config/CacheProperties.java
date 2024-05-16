package com.github.hbq969.code.common.cache.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Objects;

/**
 * @author : hbq969@gmail.com
 * @description : Spring缓存属性配置
 * @createTime : 13:07:51, 2023.04.01, 周六
 */
@ConfigurationProperties(prefix = "spring.cache.ext")
@Data
public class CacheProperties {

  private boolean enable = true;

  /**
   * 缓存清理配置
   */
  private Clean clean;

  /**
   * 默认缓存扩展配置
   */
  private LinkedHashMap juc;

  /**
   * guava缓存扩展配置
   */
  private Guava guava;

  /**
   * ehcache缓存扩展配置
   */
  private Ehcache ehcache;

  /**
   * Caffeine缓存扩展配置
   */
  private Caffeine caffeine;

  public boolean useJuc() {
    return Objects.nonNull(juc) && juc.isEnable();
  }

  public boolean useGuava() {
    return Objects.nonNull(guava) && guava.isEnable();
  }

  public boolean useEhcache() {
    return Objects.nonNull(ehcache) && ehcache.isEnable();
  }

  public boolean useCaffeine() {
    return Objects.nonNull(caffeine) && caffeine.isEnable();
  }


}
