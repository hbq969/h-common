package com.github.hbq969.code.common.ftp;

import cn.hutool.extra.ftp.FtpMode;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

@Slf4j
public class Examples {
    public static void main(String[] args) throws Exception {
//        Properties properties = new Properties();
//        properties.setProperty("StrictHostKeyChecking", "no");
//
//        FtpConfig ftpConfig1 = new FtpConfig("centos.orb.local", "ftpuser", 22, "123");
//        ftpConfig1.setConnectTimeoutMills(2000);
//        ftpConfig1.setSftpExtraConfig(properties);
//
//        FtpConfig ftpConfig2 = new FtpConfig("centos.orb.local", "ftpuser", 22, "123");
//        ftpConfig2.setConnectTimeoutMills(2000);
//        ftpConfig2.setSftpExtraConfig(properties);
//
//        List<FtpConfig> configs = ImmutableList.of(ftpConfig1, ftpConfig2);
//        FileTransfer<ChannelSftpProxy> transfer = new SftpTransferImpl(configs, 2);
//        transfer.connect();
//        log.info("{}", transfer.pwd());
//        transfer.put(new File("/Users/hbq/Downloads/111.png"), "/home/ftpuser/sftp", "111.png");
//        transfer.get(new File("/Users/hbq/Downloads/sftp/111.png"), "/home/ftpuser/sftp", "111.png");
//        transfer.disconnect();

        FtpExtraConfig ftpExtraConfig = new FtpExtraConfig();
        ftpExtraConfig.setCharset(StandardCharsets.UTF_8);
        ftpExtraConfig.setMode(FtpMode.Passive);

        FtpConfig ftpConfig1 = new FtpConfig("centos.orb.local", "ftpuser", 21, "123");
        ftpConfig1.setConnectTimeoutMills(2000);
        ftpConfig1.setFtpExtraConfig(ftpExtraConfig);

        FtpConfig ftpConfig2 = new FtpConfig("centos.orb.local", "ftpuser", 21, "123");
        ftpConfig2.setConnectTimeoutMills(2000);
        ftpConfig2.setFtpExtraConfig(ftpExtraConfig);

        List<FtpConfig> configs = ImmutableList.of(ftpConfig1, ftpConfig2);

        FileTransfer<FtpProxy> transfer = new FtpTransferImpl(configs, 2);
        transfer.connect();

        log.info("{}", transfer.pwd());
        log.info("{}", transfer.getClient().getTarget().ls("/"));
        transfer.put(new File("/Users/hbq/Downloads/111.png"), "/ftp", "111.png");
        transfer.get(new File("/Users/hbq/Downloads/ftp/111.png"), "/ftp", "111.png");
        transfer.disconnect();
    }
}
