package com.github.hbq969.code.common.cache.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author : hbq969@gmail.com
 * @description : 清理配置
 * @createTime : 16:04:48, 2023.04.02, 周日
 */
@ConfigurationProperties(prefix = "spring.cache.ext.clean")
@Data
public class Clean {

  /**
   * 是否启用定期清理
   */
  private boolean enable = true;

  /**
   * 定期清理过期的缓存
   */
  private String cron = "*/5 * * * * *";
}
