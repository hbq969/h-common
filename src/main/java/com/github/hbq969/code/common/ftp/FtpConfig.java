package com.github.hbq969.code.common.ftp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.hbq969.code.common.utils.GsonUtils;
import lombok.Data;

import java.util.Properties;

@Data
public class FtpConfig {
    private String host;
    private int port = 21;
    private String user = "anonymous";
    @JsonIgnore
    private transient String password;
    private int connectTimeoutMills = 5000;
    private Properties sftpExtraConfig;
    private FtpExtraConfig ftpExtraConfig = new FtpExtraConfig();

    public FtpConfig(String host, String user, int port, String password) {
        this.host = host;
        this.user = user;
        this.port = port;
        this.password = password;
    }

    @Override
    public String toString() {
        return GsonUtils.toJson(this);
    }
}
