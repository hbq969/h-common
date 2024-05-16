package com.github.hbq969.code.common.cache.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author : hbq969@gmail.com
 * @description : ehcache扩展配置
 * @createTime : 14:23:46, 2023.04.01, 周六
 */
@ConfigurationProperties(prefix = "spring.cache.ext.ehcache")
@Data
public class Ehcache {

  /**
   * 是否启用
   */
  private boolean enable = false;

  /**
   * ehcache配置文件路径
   */
  private String xmlConfigFile = "classpath*:ehcache.xml";

  /**
   * ehcache.xml中定义的name
   */
  private String name;

  /**
   * 是否允许值为空
   */
  private boolean allowNullValues = true;
}
