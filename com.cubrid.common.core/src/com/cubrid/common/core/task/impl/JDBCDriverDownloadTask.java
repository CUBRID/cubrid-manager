/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: -
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. - Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. - Neither the name of the <ORGANIZATION> nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.cubrid.common.core.task.impl;

import com.cubrid.common.core.task.AbstractUITask;
import com.cubrid.common.core.util.LogUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;
import org.slf4j.Logger;

/**
 * get jdbc driver list and download jdbc driver
 *
 * @author fulei
 * @version 1.0 - 2012-9-18 created by fulei
 */
public class JDBCDriverDownloadTask extends AbstractUITask {
    private static final Logger LOGGER = LogUtil.getLogger(JDBCDriverDownloadTask.class);

    HttpURLConnection connection = null;
    private final String JDBCFILEURL = "http://ftp.cubrid.org/CUBRID_Drivers/JDBC_Driver/";
    private final String JDBCLISTFILE = "filelist.txt";
    private List<String> driverList;
    private String savedPath;
    private final List<String> downloadedList = new ArrayList<String>();
    private String startupProgressMessage;
    private String downloadProgressMessage;

    public JDBCDriverDownloadTask() {}

    public JDBCDriverDownloadTask(
            List<String> driverList,
            String savedPath,
            String startupProgressMessage,
            String downloadProgressMessage) { // FIXME add description
        this.driverList = driverList;
        this.savedPath = savedPath;
        this.startupProgressMessage = startupProgressMessage;
        this.downloadProgressMessage = downloadProgressMessage;
    }

    public List<String> getJDBCFileList() throws Exception {
        List<String> fileList = new ArrayList<String>();
        URL postUrl = new URL(JDBCFILEURL + JDBCLISTFILE);
        BufferedReader reader = null;
        try {
            connection = (HttpURLConnection) postUrl.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(true); // use cache
            connection.connect();
            if (connection.getResponseCode() == 200) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    fileList.add(line);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            errorMsg = e.getMessage();
            throw e;
        } finally {
            if (reader != null) {
                reader.close();
            }
            finish();
        }
        return fileList;
    }

    /**
     * download selected jdbc drivers
     *
     * @param monitor
     */
    public void execute(IProgressMonitor monitor) {
        monitor.beginTask(startupProgressMessage, IProgressMonitor.UNKNOWN);
        try {
            for (final String driver : driverList) {
                String filePath = savedPath + File.separator + driver;
                File file = new File(filePath);
                if (file.exists()) {
                    LOGGER.debug("Driver is already exists : {}", file.getAbsolutePath());
                    downloadedList.add(filePath);
                    continue;
                }
                monitor.subTask(NLS.bind(downloadProgressMessage, driver));
                URL postUrl = new URL(JDBCFILEURL + driver);
                connection = (HttpURLConnection) postUrl.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setUseCaches(true); // use cache
                connection.connect();
                if (connection.getResponseCode() == 200) {
                    InputStream is = connection.getInputStream();
                    FileOutputStream os = null;
                    try {
                        os = new FileOutputStream(new File(filePath));
                        byte[] bytes = new byte[1024];
                        int c;
                        while ((c = is.read(bytes)) != -1) {
                            os.write(bytes, 0, c);
                        }
                    } finally {
                        if (is != null) {
                            try {
                                is.close();
                            } catch (IOException e) {
                                LOGGER.error(e.getMessage());
                            }
                        }
                        if (os != null) {
                            try {
                                os.close();
                            } catch (IOException e) {
                                LOGGER.error(e.getMessage());
                            }
                        }
                    }
                }
                downloadedList.add(filePath);
            }
        } catch (Exception e) {
            errorMsg = e.getMessage();
            LOGGER.error(e.getMessage(), e);
        } finally {
            finish();
        }
    }

    public void cancel() {
        finish();
    }

    public void finish() {
        if (connection != null) {
            connection.disconnect();
        }
    }

    public boolean isCancel() {
        return false;
    }

    public boolean isSuccess() {
        return errorMsg == null;
    }

    public List<String> getDriverList() {
        return downloadedList;
    }
}
