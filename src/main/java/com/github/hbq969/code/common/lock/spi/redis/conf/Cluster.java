package com.github.hbq969.code.common.lock.spi.redis.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author : hbq969@gmail.com
 * @description : redis集群配置类
 * @createTime : 2023/8/25 13:33
 */
@ConfigurationProperties(prefix = "lock.spi.redis.cluster")
@Data
public class Cluster {

  /**
   * 集群地址，[ip1:port,ip2:port]
   */
  private String nodes;
}
