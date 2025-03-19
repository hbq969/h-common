package com.github.hbq969.code.common.ftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class ChannelSftpProxy {
    private Session session;
    private ChannelSftp target;
    private FtpConfig config;

    public ChannelSftpProxy(Session session, FtpConfig config) {
        this.session = session;
        this.config = config;
    }

    public void connect() throws JSchException {
        this.session.connect(config.getConnectTimeoutMills());
        this.target = (ChannelSftp) session.openChannel("sftp");
        this.target.connect(config.getConnectTimeoutMills());
        if (log.isDebugEnabled()) {
            log.debug("sftp连接成功, {}", config);
        }
    }

    public void disconnect() throws JSchException {
        if (this.target != null) {
            this.target.disconnect();
        }
        if (this.session != null) {
            this.session.disconnect();
        }
        if (log.isDebugEnabled()) {
            log.debug("sftp断开连接成功。{}", config);
        }
    }
}
