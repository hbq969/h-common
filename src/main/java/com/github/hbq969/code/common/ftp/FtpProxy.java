package com.github.hbq969.code.common.ftp;

import cn.hutool.extra.ftp.Ftp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Data
@AllArgsConstructor
@Slf4j
public class FtpProxy {
    private Ftp target;
    private FtpConfig config;

    public void login() {
        this.target.init();
        if (log.isDebugEnabled()) {
            log.debug("ftp连接成功, {}", config);
        }
    }

    public void logout() {
        try {
            this.target.close();
            if (log.isDebugEnabled()) {
                log.debug("ftp断开连接成功: {}", config);
            }
        } catch (IOException e) {
            log.error(String.format("ftp断开连接异常, %s", config, e));
        }
    }
}
