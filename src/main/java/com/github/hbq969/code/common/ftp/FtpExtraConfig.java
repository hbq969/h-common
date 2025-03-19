package com.github.hbq969.code.common.ftp;

import cn.hutool.extra.ftp.FtpMode;
import lombok.Data;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Data
public class FtpExtraConfig {
    private Charset charset = StandardCharsets.UTF_8;
    private String serverLanguageCode;
    private String systemKey;
    private FtpMode mode;
}
