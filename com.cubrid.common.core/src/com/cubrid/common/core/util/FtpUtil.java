/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the <ORGANIZATION> nor the names of its contributors
 *   may be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */
package com.cubrid.common.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;

/**
 * Ftp utility
 *
 * @author pangqiren
 * @version 1.0 - 2011-5-23 created by pangqiren
 */
public class FtpUtil {
    private static final Logger LOGGER = LogUtil.getLogger(FtpUtil.class);
    private static final String SERVER = "ftp.cubrid.org";
    private static final String USER_NAME = "anonymous";
    private static final String USER_PASSWORD = "anonymous";
    private static final String DRIVER_PATH = "CUBRID_Drivers/JDBC_Driver/";
    private FTPClient ftpClient;

    /**
     * Connect the default server
     *
     * @throws IOException The exception
     */
    public void connectServer() throws IOException {
        connectServer(SERVER, USER_NAME, USER_PASSWORD, DRIVER_PATH);
    }

    /**
     * Connect the server
     *
     * @param server String
     * @param userName String
     * @param userPassword String
     * @param driverPath String
     * @throws IOException The exception
     */
    public void connectServer(
            String server, String userName, String userPassword, String driverPath)
            throws IOException {
        ftpClient = new FTPClient();
        ftpClient.connect(server);
        ftpClient.login(userName, userPassword);
        ftpClient.changeWorkingDirectory(driverPath);
        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
        ftpClient.enterLocalPassiveMode();
    }

    /**
     * Download the file
     *
     * @param fileName String
     * @param newFileName String
     * @throws IOException The exception
     */
    public void download(String fileName, String newFileName) throws IOException {
        InputStream is = null;
        FileOutputStream os = null;
        try {
            is = ftpClient.retrieveFileStream(fileName);
            os = new FileOutputStream(new File(newFileName));
            byte[] bytes = new byte[1024];
            int c;
            while ((c = is.read(bytes)) != -1) {
                os.write(bytes, 0, c);
            }
        } finally {
            FileUtil.close(is);
            FileUtil.close(os);
        }
    }

    /**
     * Upload the file
     *
     * @param fileName String
     * @param newName String
     * @throws IOException The exception
     */
    public void upload(String fileName, String newName) throws IOException {
        OutputStream os = null;
        FileInputStream is = null;
        try {
            os = ftpClient.storeFileStream(newName);
            is = new FileInputStream(new File(fileName));
            byte[] bytes = new byte[1024];
            int c;
            while ((c = is.read(bytes)) != -1) {
                os.write(bytes, 0, c);
            }
        } finally {
            FileUtil.close(is);
            FileUtil.close(os);
        }
    }

    /**
     * Get file list
     *
     * @param path String
     * @return List<String>
     * @throws IOException The exception
     */
    public List<String> getFileList(String path) throws IOException {
        List<String> list = new ArrayList<String>();
        String[] fileNames = ftpClient.listNames(path);
        if (fileNames != null) {
            list = Arrays.asList(fileNames);
        }

        return list;
    }

    /** Close the ftp server */
    public void closeServer() {
        try {
            if (ftpClient != null && ftpClient.isConnected()) {
                ftpClient.logout();
            }
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        try {
            if (ftpClient != null && ftpClient.isConnected()) {
                ftpClient.disconnect();
            }
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }
}
