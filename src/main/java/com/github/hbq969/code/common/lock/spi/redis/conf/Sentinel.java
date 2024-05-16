package com.github.hbq969.code.common.lock.spi.redis.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author : hbq969@gmail.com
 * @description : redis哨兵配置类
 * @createTime : 2023/8/25 11:42
 */
@ConfigurationProperties(prefix = "lock.spi.redis.sentinel")
@Data
public class Sentinel {

  /**
   * 哨兵中定义的redis主节点名称
   */
  private String master = "mymaster";

  /**
   * 哨兵节点信息, [ip1:port,ip2:port]
   */
  private String nodes = "localhost:16379";
}
