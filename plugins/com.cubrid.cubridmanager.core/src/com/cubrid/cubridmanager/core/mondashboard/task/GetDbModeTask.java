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
package com.cubrid.cubridmanager.core.mondashboard.task;

import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import com.cubrid.cubridmanager.core.mondashboard.model.DBStatusType;
import com.cubrid.cubridmanager.core.mondashboard.model.HADatabaseStatusInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * Get database mode(active,standby)
 *
 * @author pangqiren
 * @version 1.0 - 2010-6-7 created by pangqiren
 */
public class GetDbModeTask extends SocketTask {

    private static final String[] SEND_MSG_ITEMS = new String[] {"task", "token", "dblist"};

    /**
     * The constructor
     *
     * @param serverInfo
     */
    public GetDbModeTask(ServerInfo serverInfo) {
        super("getdbmode", serverInfo, SEND_MSG_ITEMS);
    }

    /**
     * Set the database list
     *
     * @param dbList The String array
     */
    public void setDbList(List<String> dbList) {
        if (dbList == null || dbList.isEmpty()) {
            return;
        }
        StringBuffer dbNamesBuff = new StringBuffer();
        for (int i = 0; i < dbList.size(); i++) {
            dbNamesBuff.append(dbList.get(i));
            if (i != dbList.size() - 1) {
                dbNamesBuff.append(",");
            }
        }
        super.setMsgItem("dblist", dbNamesBuff.toString());
    }

    /**
     * Get database modes list
     *
     * @return The List<HADatabaseStatus>
     */
    public List<HADatabaseStatusInfo> getDbModes() {
        TreeNode response = getResponse();
        if (response == null || (this.getErrorMsg() != null && getErrorMsg().trim().length() > 0)) {
            return null;
        }
        List<HADatabaseStatusInfo> dbStatusList = new ArrayList<HADatabaseStatusInfo>();
        for (int i = 0; i < response.childrenSize(); i++) {
            TreeNode node1 = response.getChildren().get(i);
            if (node1.getValue("open") == null) {
                continue;
            }
            if (node1.getValue("open").trim().equals("dbserver")) {
                String dbName = node1.getValue("dbname");
                String serverMode = node1.getValue("server_mode");
                String error = node1.getValue("server_msg");
                HADatabaseStatusInfo dbStatus = new HADatabaseStatusInfo();
                dbStatus.setDbName(dbName);
                if (error == null || error.equals("none")) {
                    dbStatus.setStatusType(
                            DBStatusType.getType(serverMode, serverInfo.isHAMode(dbName)));
                } else {
                    dbStatus.setErrorInfo(error);
                    dbStatus.setStatusType(DBStatusType.UNKNOWN);
                }
                dbStatusList.add(dbStatus);
            }
        }
        return dbStatusList;
    }
}
