package com.github.hbq969.code.common.encrypt.ext.config;

import com.github.hbq969.code.common.encrypt.ext.utils.AESUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * @author hbq969@gmail.com
 **/
@RefreshScope
@Data
@ConfigurationProperties(prefix = "encrypt.restful.aes")
@Slf4j
public class AESConfig {

  private volatile String charset = "utf-8";

  private volatile boolean enabled = false;

  private volatile boolean showLog = false;

  private volatile String key = AESUtil.KEY;
}
