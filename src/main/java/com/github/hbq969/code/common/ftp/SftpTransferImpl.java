package com.github.hbq969.code.common.ftp;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class SftpTransferImpl implements FileTransfer<ChannelSftpProxy> {


    private final int size;

    private List<ChannelSftpProxy> sftps = new ArrayList<>();

    private AtomicLong cnt = new AtomicLong(0);

    private int retryWaitTimeoutSec;

    public SftpTransferImpl(List<FtpConfig> ftpConfigs, int retryWaitTimeoutSec) {
        this.retryWaitTimeoutSec = retryWaitTimeoutSec;
        JSch jsch = new JSch();
        Session session = null;
        for (FtpConfig ftpConfig : ftpConfigs) {
            try {
                session = jsch.getSession(ftpConfig.getUser(), ftpConfig.getHost(), ftpConfig.getPort());
                session.setPassword(ftpConfig.getPassword());
                if (ftpConfig.getSftpExtraConfig() != null) {
                    session.setConfig(ftpConfig.getSftpExtraConfig());
                }
            } catch (JSchException e) {
                log.error(String.format("初始化sftp会话连接异常 %s", ftpConfig), e);
            }
            if (session != null) {
                ChannelSftpProxy proxy = new ChannelSftpProxy(session, ftpConfig);
                this.sftps.add(proxy);
            }

        }
        this.size = sftps.size();
        if (log.isDebugEnabled()) {
            log.debug("初始化sftp成功, {} 个", this.size);
        }
    }

    @Override
    public ChannelSftpProxy getClient() {
        int idx = (int) (cnt.get() % size);
        ChannelSftpProxy proxy = this.sftps.get(idx);
        if (log.isDebugEnabled()) {
            log.debug("获取sftp代理对象[{}]: {}", idx, proxy.getConfig());
        }
        return proxy;
    }

    @Override
    public synchronized void connect() {
        int idx = (int) (cnt.get() % this.size);
        ChannelSftpProxy proxy = this.sftps.get(idx);
        try {
            if (log.isDebugEnabled()) {
                log.debug("尝试sftp连接[{}]: {}", idx, proxy.getConfig());
            }
            proxy.connect();
        } catch (JSchException e) {
            log.error(String.format("sftp[%d]连接异常: %s, 等待%d秒后尝试连接其他地址。", idx, proxy.getConfig(), retryWaitTimeoutSec), e);
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
        for (ChannelSftpProxy sftp : this.sftps) {
            try {
                sftp.disconnect();
            } catch (JSchException e) {
                log.error("sftp断开连接异常: {}", sftp.getConfig());
            }
        }
    }

    @Override
    public String pwd() throws FileTransferException {
        try {
            return getClient().getTarget().pwd();
        } catch (SftpException e) {
            throw new FileTransferException(e);
        }
    }

    @Override
    public void cd(String path) throws FileTransferException {
        try {
            if (log.isDebugEnabled()) {
                log.debug("cd {}", path);
            }
            getClient().getTarget().cd(path);
        } catch (SftpException e) {
            throw new FileTransferException(e);
        }
    }

    @Override
    public void rm(String path) throws FileTransferException {
        try {
            if (log.isDebugEnabled()) {
                log.debug("rm {}", path);
            }
            getClient().getTarget().rm(path);
        } catch (SftpException e) {
            throw new FileTransferException(e);
        }
    }

    @Override
    public void rename(String oldPath, String newPath) throws FileTransferException {
        try {
            if (log.isDebugEnabled()) {
                log.debug("rename oldPath: {}, newPath: {}", oldPath, newPath);
            }
            getClient().getTarget().rename(oldPath, newPath);
        } catch (SftpException e) {
            throw new FileTransferException(e);
        }
    }

    @Override
    public void mkdir(String path) throws FileTransferException {
        try {
            if (log.isDebugEnabled()) {
                log.debug("mkdir : {}", path);
            }
            getClient().getTarget().mkdir(path);
        } catch (SftpException e) {
            throw new FileTransferException(e);
        }
    }

    @Override
    public void rmdir(String path) throws FileTransferException {
        try {
            if (log.isDebugEnabled()) {
                log.debug("rmdir : {}", path);
            }
            getClient().getTarget().rmdir(path);
        } catch (SftpException e) {
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
            getClient().getTarget().mkdir(destDir);
            if (log.isDebugEnabled()) {
                log.debug("mkdir {} success.", destDir);
            }
        } catch (SftpException e) {
            log.debug("{} exists", destDir);
        }
        try {
            getClient().getTarget().put(localFile.getAbsolutePath(), destFile);
        } catch (SftpException e) {
            throw new FileTransferException(e);
        }
    }

    @Override
    public void get(File localFile, String remoteDir, String remoteFilename) throws FileTransferException {
        if (StringUtils.isEmpty(remoteFilename)) {
            remoteFilename = localFile.getName();
        }
        String remoteFile;
        if (remoteDir.endsWith("/")) {
            remoteFile = String.join("", remoteDir, remoteFilename);
        } else {
            remoteFile = String.join("/", remoteDir, remoteFilename);
        }
        if (log.isDebugEnabled()) {
            log.debug("download, localFile: {}, remoteFile: {}", localFile.getAbsolutePath(), remoteFile);
        }
        try {
            FileUtils.forceMkdir(new File(localFile.getParent()));
            if (log.isDebugEnabled()) {
                log.debug("mkdir {} success.", localFile.getParent());
            }
        } catch (IOException e) {
            log.debug("mkdir {} exists.", localFile.getParent());
        }
        try {
            getClient().getTarget().get(remoteFile, localFile.getAbsolutePath());
        } catch (SftpException e) {
            throw new FileTransferException(e);
        }
    }
}
