package com.github.hbq969.code.common.ftp;

import cn.hutool.extra.ftp.Ftp;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class FtpTransferImpl implements FileTransfer<FtpProxy> {
    private final int size;

    private List<FtpProxy> ftps = new ArrayList<>();

    private AtomicLong cnt = new AtomicLong(0);

    private volatile int maxRetry;

    private int retryWaitTimeoutSec;
    private volatile boolean disconnected = false;

    public FtpTransferImpl(List<FtpConfig> ftpConfigs, int retryWaitTimeoutSec) {
        this.retryWaitTimeoutSec = retryWaitTimeoutSec;
        for (FtpConfig ftpConfig : ftpConfigs) {
            Ftp ftp = new Ftp(ftpConfig.getHost(), ftpConfig.getPort(), ftpConfig.getUser(), ftpConfig.getPassword(),
                    ftpConfig.getFtpExtraConfig().getCharset(), ftpConfig.getFtpExtraConfig().getServerLanguageCode(),
                    ftpConfig.getFtpExtraConfig().getSystemKey(), ftpConfig.getFtpExtraConfig().getMode());
            this.ftps.add(new FtpProxy(ftp, ftpConfig));
        }
        this.size = this.ftps.size();
        this.maxRetry = this.size;
        if (log.isDebugEnabled()) {
            log.debug("初始化ftp成功, {} 个", this.size);
        }
    }

    @Override
    public FtpProxy getClient() {
        int idx = (int) (cnt.get() % size);
        FtpProxy proxy = this.ftps.get(idx);
        if (log.isDebugEnabled()) {
            log.debug("获取ftp代理对象[{}]: {}", idx, proxy.getConfig());
        }
        return proxy;
    }

    @Override
    public void connect() {
        if (disconnected)
            return;
        if (this.maxRetry == 0) {
            log.info("达到最大重连次数: {}", this.size);
            return;
        }
        int idx = (int) (cnt.get() % this.size);
        FtpProxy proxy = this.ftps.get(idx);
        boolean isConnected = true;
        try {
            if (log.isDebugEnabled()) {
                log.debug("尝试ftp连接[{}]: {}", idx, proxy.getConfig());
            }
            proxy.login();
            this.maxRetry = this.size;
        } catch (Exception e) {
            isConnected = false;
            log.error(String.format("ftp[%d]连接异常: %s, 等待%d秒后尝试连接其他地址。", idx, proxy.getConfig(), retryWaitTimeoutSec), e);
        }
        if (!isConnected || !proxy.getTarget().getClient().isConnected()) {
            if (this.size == 1) {
                return;
            }
            try {
                TimeUnit.SECONDS.sleep(retryWaitTimeoutSec);
            } catch (InterruptedException ex) {
            }
            cnt.incrementAndGet();
            connect();
            this.maxRetry--;
        }
    }

    @Override
    public void disconnect() {
        this.disconnected = true;
        for (FtpProxy ftp : this.ftps) {
            ftp.logout();
        }
    }

    @Override
    public String pwd() throws FileTransferException {
        try {
            return Optional.ofNullable(getClient().getTarget())
                    .orElseThrow(() -> new SftpException(-1, "ftp未初始化成功"))
                    .pwd();
        } catch (Exception e) {
            throw new FileTransferException(e);
        }
    }

    @Override
    public void cd(String path) throws FileTransferException {
        try {
            Optional.ofNullable(getClient().getTarget())
                    .orElseThrow(() -> new SftpException(-1, "ftp未初始化成功"))
                    .cd(path);
        } catch (Exception e) {
            throw new FileTransferException(e);
        }
    }

    @Override
    public void rm(String path) throws FileTransferException {
        try {
            Optional.ofNullable(getClient().getTarget())
                    .orElseThrow(() -> new SftpException(-1, "ftp未初始化成功"))
                    .delFile(path);
        } catch (Exception e) {
            throw new FileTransferException(e);
        }
    }

    @Override
    public void rename(String oldPath, String newPath) throws FileTransferException {
        try {
            Optional.ofNullable(getClient().getTarget())
                    .orElseThrow(() -> new SftpException(-1, "ftp未初始化成功")).getClient().rename(oldPath, newPath);
        } catch (Exception e) {
            throw new FileTransferException(e);
        }
    }

    @Override
    public void mkdir(String path) throws FileTransferException {
        try {
            Optional.ofNullable(getClient().getTarget())
                    .orElseThrow(() -> new SftpException(-1, "ftp未初始化成功"))
                    .mkdir(path);
        } catch (Exception e) {
            throw new FileTransferException(e);
        }
    }

    @Override
    public void rmdir(String path) throws FileTransferException {
        try {
            Optional.ofNullable(getClient().getTarget())
                    .orElseThrow(() -> new SftpException(-1, "ftp未初始化成功"))
                    .getClient().rmd(path);
        } catch (Exception e) {
            throw new FileTransferException(e);
        }
    }

    @Override
    public void put(File localFile, String destDir, String destFilename) throws FileTransferException {
        if (StringUtils.isEmpty(destFilename)) {
            destFilename = localFile.getName();
        }
        String destFile;
        if (destDir.endsWith("/")) {
            destFile = String.join("", destDir, destFilename);
        } else {
            destFile = String.join("/", destDir, destFilename);
        }
        if (log.isDebugEnabled()) {
            log.debug("upload, localFile: {}, remoteFile: {}", localFile.getAbsolutePath(), destFile);
        }
        try {
            Optional.ofNullable(getClient().getTarget())
                    .orElseThrow(() -> new SftpException(-1, "ftp未初始化成功"))
                    .upload(destDir, destFilename, localFile);
        } catch (Exception e) {
            throw new FileTransferException(e);
        }
    }

    @Override
    public void get(File localFile, String remoteDir, String remoteFilename) throws FileTransferException {
        if (StringUtils.isEmpty(remoteFilename)) {
            remoteFilename = localFile.getName();
        }
        String destFile;
        if (remoteDir.endsWith("/")) {
            destFile = String.join("", remoteDir, remoteFilename);
        } else {
            destFile = String.join("/", remoteDir, remoteFilename);
        }
        if (log.isDebugEnabled()) {
            log.debug("download, localFile: {}, remoteFile: {}", localFile.getAbsolutePath(), destFile);
        }
        try {
            Optional.ofNullable(getClient().getTarget())
                    .orElseThrow(() -> new SftpException(-1, "ftp未初始化成功"))
                    .download(remoteDir, remoteFilename, localFile);
        } catch (Exception e) {
            throw new FileTransferException(e);
        }
    }
}
