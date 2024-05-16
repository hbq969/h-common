package com.github.hbq969.code.common.lock.spi.redis.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author : hbq969@gmail.com
 * @description : redis线程池配置类
 * @createTime : 2023/8/25 11:44
 */
@ConfigurationProperties(prefix = "lock.spi.redis.lettuce.pool")
@Data
public class Pool {

  /**
   * 连接超时时间，秒
   */
  private long timeout = 5;
  /**
   * 最大空闲连接数
   */
  private int maxIdle;
  /**
   * 最小空闲连接数
   */
  private int minIdle;
  /**
   * 最大连接数
   */
  private int maxActive;
  /**
   * 最大等待时间，毫秒
   */
  private int maxWait;
}
