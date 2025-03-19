package com.github.hbq969.code.common.ftp;

import java.io.File;

public interface FileTransfer<T> {
    T getClient();

    void connect();

    void disconnect();

    String pwd() throws FileTransferException;

    void cd(String path) throws FileTransferException;

    void rm(String path) throws FileTransferException;

    void rename(String oldPath, String newPath) throws FileTransferException;

    void mkdir(String path) throws FileTransferException;

    void rmdir(String path) throws FileTransferException;

    void put(File localFile, String destDir, String destFilename) throws FileTransferException;

    void get(File localFile, String remoteDir, String remoteFilename) throws FileTransferException;
}
