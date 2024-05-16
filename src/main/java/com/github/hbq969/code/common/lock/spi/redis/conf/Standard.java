package com.github.hbq969.code.common.lock.spi.redis.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author : hbq969@gmail.com
 * @description : redis单机版配置类
 * @createTime : 2023/8/25 13:36
 */
@ConfigurationProperties(prefix = "lock.spi.redis.standard")
@Data
public class Standard {

  /**
   * ip地址
   */
  private String host = "localhost";

  /**
   * 端口
   */
  private int port = 6379;
}
