package com.github.hbq969.code.common.lock.spi.redis.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author : hbq969@gmail.com
 * @description : 分布式锁redis实现配置属性类
 * @createTime : 2023/8/25 11:37
 */
@ConfigurationProperties(prefix = "lock.spi.redis")
@Data
public class LockRedisProperties {

  /**
   * 是否启用分布式锁
   */
  private boolean enabled = false;

  /**
   * redis密码
   */
  private String password;

  /**
   * redis 线程池配置
   */
  private Pool pool;

  /**
   * 单机版redis配置
   */
  private Standard standard;

  /**
   * 哨兵模式redis配置
   */
  private Sentinel sentinel;

  /**
   * 集群模式redis配置
   */
  private Cluster cluster;
}
