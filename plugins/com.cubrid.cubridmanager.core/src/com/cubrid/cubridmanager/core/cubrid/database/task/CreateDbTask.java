/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.cubridmanager.core.cubrid.database.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.util.List;
import java.util.Map;

/**
 * This task is responsible to create database
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class CreateDbTask extends SocketTask {

    private static final String[] SEND_MSG_ITEMS =
            new String[] {
                "task",
                "token",
                "dbname",
                "numpage",
                "pagesize",
                "logpagesize",
                "logsize",
                "genvolpath",
                "logvolpath",
                "open",
                "close",
                "overwrite_config_file",
                "charset"
            };

    private String dbname;

    /**
     * The constructor
     *
     * @param serverInfo
     */
    public CreateDbTask(ServerInfo serverInfo) {
        super("createdb", serverInfo, SEND_MSG_ITEMS);
    }

    // TODO remove this method
    @Override
    public void execute() {
        super.execute();
        int statusCode = this.getStatusCode();
        // If the operation timed out, we first think the
        // operation is still in progress, wait 240s.
        // If it is still no result, the operation failed.
        if (504 == statusCode) {
            int total = 48;
            int count = 0;
            String msgtmp = "Create database failed.";
            GetDatabaseListTask getDatabaseListTask = new GetDatabaseListTask(this.getServerInfo());
            getDatabaseListTask.setNeedMultiSend(true);
            do {
                getDatabaseListTask.execute();
                List<DatabaseInfo> databaseInfoList = getDatabaseListTask.loadDatabaseInfo();
                if (databaseInfoList != null) {
                    for (DatabaseInfo di : databaseInfoList) {
                        if (di.getDbName().equals(this.getDbName())) {
                            msgtmp = null;
                            break;
                        }
                    }
                }
                if (null == msgtmp) {
                    break;
                }
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                    LOGGER.debug("", e);
                }
            } while (count++ < total);
            this.setErrorMsg(msgtmp);
        }
    }

    /**
     * Set database name
     *
     * @param dbName String the database name
     */
    public void setDbName(String dbName) {
        this.dbname = dbName;
        super.setMsgItem("dbname", dbName);
    }

    public String getDbName() {
        return dbname;
    }

    /**
     * Set volume page number
     *
     * @param numPage String the number of page
     */
    public void setNumPage(String numPage) {
        super.setMsgItem("numpage", numPage);
    }

    /**
     * Set volume page size
     *
     * @param pageSize String the size of page
     */
    public void setPageSize(String pageSize) {
        super.setMsgItem("pagesize", pageSize);
    }

    /**
     * Set log page size
     *
     * @param logPageSize String the page size of log
     */
    public void setLogPageSize(String logPageSize) {
        super.setMsgItem("logpagesize", logPageSize);
    }

    /**
     * Set charset
     *
     * @param charset String the database charset
     */
    public void setCharset(String charset) {
        super.setMsgItem("charset", charset);
    }

    /**
     * Set log volume size
     *
     * @param logSize String the size of log
     */
    public void setLogSize(String logSize) {
        super.setMsgItem("logsize", logSize);
    }

    /**
     * Set generic volume path
     *
     * @param volPath String the path of volume
     */
    public void setGeneralVolumePath(String volPath) {
        super.setMsgItem("genvolpath", volPath);
    }

    /**
     * Set log volume path
     *
     * @param volPath String the path of volume
     */
    public void setLogVolumePath(String volPath) {
        super.setMsgItem("logvolpath", volPath);
    }

    /**
     * Set extended volume information list
     *
     * @param volList List<Map<String,String>> the instances of volume
     */
    public void setExVolumes(List<Map<String, String>> volList) {
        StringBuilder sb = new StringBuilder("exvol");
        int len = volList.size();
        if (len > 0) {
            sb.append("\n");
        }
        for (int i = 0; i < len; i++) {
            Map<String, String> map = volList.get(i);
            String volumeName = map.get("0");
            String volumeType = map.get("1");
            String pageNumber = map.get("3");
            String volumePath = map.get("4");
            sb.append(String.format("%s:%s;%s;%s", volumeName, volumeType, pageNumber, volumePath));
            if (i != volList.size() - 1) {
                sb.append("\n");
            }
        }
        super.setMsgItem("open", sb.toString());
        super.setMsgItem("close", "exvol");
    }

    /**
     * Set whether overide conf file
     *
     * @param isOverride boolean Whether is allowed that be overrided
     */
    public void setOverwriteConfigFile(boolean isOverride) {
        super.setMsgItem("overwrite_config_file", StringUtil.yesno(isOverride));
    }
}
