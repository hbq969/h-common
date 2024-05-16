package com.github.hbq969.code.common.encrypt.ext.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * @author hbq969@gmail.com
 **/
@RefreshScope
@Data
@ConfigurationProperties(prefix = "encrypt.restful.rsa")
@Slf4j
public class RSAConfig {

  private volatile String privateKey;

  private volatile String publicKey;

  private volatile String charset = "utf-8";

  private volatile boolean enabled = false;

  private volatile boolean showLog = false;

  /**
   * 请求数据时间戳校验时间差 超过指定时间的数据认定为伪造
   */
  private volatile boolean timestampCheck = false;
}
