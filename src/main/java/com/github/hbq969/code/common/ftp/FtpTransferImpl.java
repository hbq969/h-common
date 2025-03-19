package com.github.hbq969.code.common.ftp;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.extra.ftp.Ftp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class FtpTransferImpl implements FileTransfer<FtpProxy> {
    private final int size;

    private List<FtpProxy> ftps = new ArrayList<>();

    private AtomicLong cnt = new AtomicLong(0);

    private int retryWaitTimeoutSec;

    public FtpTransferImpl(List<FtpConfig> ftpConfigs, int retryWaitTimeoutSec) {
        this.retryWaitTimeoutSec = retryWaitTimeoutSec;
        for (FtpConfig ftpConfig : ftpConfigs) {
            Ftp ftp = new Ftp(ftpConfig.getHost(), ftpConfig.getPort(), ftpConfig.getUser(), ftpConfig.getPassword(),
                    ftpConfig.getFtpExtraConfig().getCharset(), ftpConfig.getFtpExtraConfig().getServerLanguageCode(),
                    ftpConfig.getFtpExtraConfig().getSystemKey(), ftpConfig.getFtpExtraConfig().getMode());
            this.ftps.add(new FtpProxy(ftp, ftpConfig));
        }
        this.size = this.ftps.size();
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
        int idx = (int) (cnt.get() % this.size);
        FtpProxy proxy = this.ftps.get(idx);
        boolean isConnected = true;
        try {
            if (log.isDebugEnabled()) {
                log.debug("尝试ftp连接[{}]: {}", idx, proxy.getConfig());
            }
            proxy.login();
        } catch (Exception e) {
            isConnected = false;
            log.error(String.format("ftp[%d]连接异常: %s, 等待%d秒后尝试连接其他地址。", idx, proxy.getConfig(), retryWaitTimeoutSec), e);
        }
        if (!isConnected || !proxy.getTarget().getClient().isConnected()) {
            try {
                TimeUnit.SECONDS.sleep(retryWaitTimeoutSec);
            } catch (InterruptedException ex) {
            }
            cnt.incrementAndGet();
            connect();
        }
    }

    @Override
    public void disconnect() {
        for (FtpProxy ftp : this.ftps) {
            ftp.logout();

        }
    }

    @Override
    public String pwd() throws FileTransferException {
        try {
            return getClient().getTarget().pwd();
        } catch (Exception e) {
            throw new FileTransferException(e);
        }
    }

    @Override
    public void cd(String path) throws FileTransferException {
        try {
            getClient().getTarget().cd(path);
        } catch (Exception e) {
            throw new FileTransferException(e);
        }
    }

    @Override
    public void rm(String path) throws FileTransferException {
        try {
            getClient().getTarget().delFile(path);
        } catch (Exception e) {
            throw new FileTransferException(e);
        }
    }

    @Override
    public void rename(String oldPath, String newPath) throws FileTransferException {
        try {
            getClient().getTarget().getClient().rename(oldPath, newPath);
        } catch (IOException e) {
            throw new FileTransferException(e);
        }
    }

    @Override
    public void mkdir(String path) throws FileTransferException {
        try {
            getClient().getTarget().mkdir(path);
        } catch (Exception e) {
            throw new FileTransferException(e);
        }
    }

    @Override
    public void rmdir(String path) throws FileTransferException {
        try {
            getClient().getTarget().getClient().rmd(path);
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
            getClient().getTarget().upload(destDir, destFilename, localFile);
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
            getClient().getTarget().download(remoteDir, remoteFilename, localFile);
        } catch (Exception e) {
            throw new FileTransferException(e);
        }
    }
}
