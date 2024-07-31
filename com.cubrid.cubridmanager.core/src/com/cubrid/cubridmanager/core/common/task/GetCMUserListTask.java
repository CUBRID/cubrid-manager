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
package com.cubrid.cubridmanager.core.common.task;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.cubridmanager.core.common.model.CasAuthType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.common.model.StatusMonitorAuthType;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DbCreateAuthType;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * This task is responsible to get CUBRID Manager user information
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class GetCMUserListTask extends SocketTask {
    private static final String[] SEND_MSG_ITEMS = new String[] {"task", "token"};

    /**
     * The constructor
     *
     * @param taskName
     * @param serverInfo
     */
    public GetCMUserListTask(ServerInfo serverInfo) {
        super("getdbmtuserinfo", serverInfo, SEND_MSG_ITEMS);
    }

    /**
     * Get server user information list
     *
     * @return List<ServerUserInfo> A list stored some instance of ServerUserInfo
     */
    public List<ServerUserInfo> getServerUserInfoList() {
        TreeNode response = getResponse();
        if (response == null || (this.getErrorMsg() != null && getErrorMsg().trim().length() > 0)) {
            return null;
        }
        List<ServerUserInfo> serverUserInfoList = new ArrayList<ServerUserInfo>();
        for (int i = 0; i < response.childrenSize(); i++) {
            TreeNode node1 = response.getChildren().get(i);
            if (node1.getValue("open") == null) {
                continue;
            }
            if (node1.getValue("open").trim().equals("dblist")) {
                serverInfo.removeAllDatabase();
                String[] dbNames = node1.getValues("dbname");

                // start
                if (null == dbNames) {
                    int length = node1.childrenSize();
                    dbNames = new String[length];
                    for (int j = 0; j < length; j++) {
                        TreeNode subNode2 = node1.getChildren().get(j);
                        if (subNode2.getValue("open") == null
                                || !subNode2.getValue("open").trim().equals("dbs")) {
                            continue;
                        }
                        dbNames[j] = subNode2.getValue("dbname");
                    }
                }
                // end

                for (int m = 0; dbNames != null && m < dbNames.length; m++) {
                    serverInfo.addDatabase(dbNames[m]);
                }
            } else if (node1.getValue("open").trim().equals("userlist")) {
                buildUserInfo(serverUserInfoList, node1);
            }
        }
        return serverUserInfoList;
    }

    /**
     * Build user info
     *
     * @param serverUserInfoList List<ServerUserInfo> The given list that stored some instance of
     *     ServerUserInfo
     * @param node TreeNode
     */
    private void buildUserInfo(List<ServerUserInfo> serverUserInfoList, TreeNode node) {
        for (int j = 0; j < node.childrenSize(); j++) {
            TreeNode node1 = node.getChildren().get(j);
            if (node1.getValue("open") == null || !node1.getValue("open").trim().equals("user")) {
                continue;
            }
            String userId = node1.getValue("id");
            if (userId == null) {
                continue;
            }
            String password = node1.getValue("passwd");
            String casAuthInfo = node1.getValue("casauth");
            String dbCreater = node1.getValue("dbcreate");
            String statusMonitorAuthInfo = node1.getValue("statusmonitorauth");
            ServerUserInfo userInfo = new ServerUserInfo(userId, password);

            CasAuthType casAuthType = CasAuthType.AUTH_NONE;
            if (casAuthInfo != null
                    && casAuthInfo.trim().equals(CasAuthType.AUTH_ADMIN.getText())) {
                casAuthType = CasAuthType.AUTH_ADMIN;
            } else if (casAuthInfo != null
                    && casAuthInfo.trim().equals(CasAuthType.AUTH_MONITOR.getText())) {
                casAuthType = CasAuthType.AUTH_MONITOR;
            }
            userInfo.setCasAuth(casAuthType);

            StatusMonitorAuthType statusMonitorAuth = StatusMonitorAuthType.AUTH_NONE;
            if (statusMonitorAuthInfo != null
                    && statusMonitorAuthInfo
                            .trim()
                            .equals(StatusMonitorAuthType.AUTH_ADMIN.getText())) {
                statusMonitorAuth = StatusMonitorAuthType.AUTH_ADMIN;
            } else if (statusMonitorAuthInfo != null
                    && statusMonitorAuthInfo
                            .trim()
                            .equals(StatusMonitorAuthType.AUTH_MONITOR.getText())) {
                statusMonitorAuth = StatusMonitorAuthType.AUTH_MONITOR;
            }
            userInfo.setStatusMonitorAuth(statusMonitorAuth);
            if (userInfo.isAdmin()) {
                userInfo.setStatusMonitorAuth(StatusMonitorAuthType.AUTH_ADMIN);
            }

            if (dbCreater != null && dbCreater.equals(DbCreateAuthType.AUTH_ADMIN.getText())) {
                userInfo.setDbCreateAuthType(DbCreateAuthType.AUTH_ADMIN);
            } else {
                userInfo.setDbCreateAuthType(DbCreateAuthType.AUTH_NONE);
            }
            if (userInfo.getUserName().equals(serverInfo.getUserName())) {
                userInfo.setPassword(serverInfo.getUserPassword());
            }
            buildDbAuthInfo(node1, userInfo);
            serverUserInfoList.add(userInfo);
        }
    }

    /**
     * Create database authorization information
     *
     * @param node the TreeNode
     * @param userInfo the ServerUserInfo
     */
    private void buildDbAuthInfo(TreeNode node, ServerUserInfo userInfo) {
        for (int k = 0; k < node.childrenSize(); k++) {
            TreeNode subNode1 = node.getChildren().get(k);
            if (subNode1.getValue("open") == null
                    || !subNode1.getValue("open").trim().equals("dbauth")) {
                continue;
            }
            String[] dbNameArr = subNode1.getValues("dbname");
            String[] dbUserIdArr = subNode1.getValues("dbid");
            String[] dbPasswordArr = subNode1.getValues("dbpasswd");
            String[] dbBrokerAddressArr = subNode1.getValues("dbbrokeraddress");
            /*
             * As the old interface data format is not strict, as follows:
             * ****************************************************************
             * old format:
             * open:userlist
             * open:user
             * @id:fc921dcd5f775887b0d11859b4ad209562d214d78beac3c12ef3d968d4662378
             * @passwd:86cdee95f45773a5c823601fdf6083c81e91c1cf239a99add914ba8b1c53f9f9
             * open:dbauth
             * dbname:db1
             * @dbid:f48a619ab401fdfb2321c53e73fcd36923f57d1af229a45a5c5b3b8f122826cb
             * dbbrokeraddress:localhost,30000
             * dbname:demodb
             * @dbid:ae58889474d0ea3ed327866b1037bd0d833b52be4720c3dce3264341f2a55cdd
             * dbbrokeraddress:localhost,30000
             * close:dbauth
             * casauth:admin
             * dbcreate:admin
             * statusmonitorauth:admin
             * close:user
             * close:userlist
             * ****************************************************************
             * new format:
             *    "userlist" : [
             *       {
             *          "user" : [
             *             {
             *                "@id" : "admin",
             *                "@passwd" : "Admin",
             *                "casauth" : "admin",
             *                "dbauth" : [
             *                   {
             *                      "auth_info" : [
             *                         {
             *                            "@dbid" : "dba",
             *                            "dbbrokeraddress" : "localhost,30000",
             *                            "dbname" : "db1"
             *                         },
             *                         {
             *                            "@dbid" : "dba",
             *                            "dbbrokeraddress" : "localhost,30000",
             *                            "dbname" : "demodb"
             *                         }
             *                      ]
             *                   }
             *                ],
             *                "dbcreate" : "admin",
             *                "statusmonitorauth" : "admin"
             *             }
             *          ]
             * ****************************************************************
             * Thus here to add additional judgment operation
             */
            if (null == dbNameArr) {
                int length = subNode1.childrenSize();
                dbNameArr = new String[length];
                dbUserIdArr = new String[length];
                dbPasswordArr = new String[length];
                dbBrokerAddressArr = new String[length];
                for (int i = 0; i < length; i++) {
                    TreeNode subNode2 = subNode1.getChildren().get(i);
                    if (subNode2.getValue("open") == null
                            || !subNode2.getValue("open").trim().equals("auth_info")) {
                        continue;
                    }
                    dbNameArr[i] = subNode2.getValue("dbname");
                    dbUserIdArr[i] = subNode2.getValue("dbid");
                    dbPasswordArr[i] = subNode2.getValue("dbpasswd");
                    dbBrokerAddressArr[i] = subNode2.getValue("dbbrokeraddress");
                }
            }
            // end
            buildDatabaseInfo(dbNameArr, dbUserIdArr, dbPasswordArr, dbBrokerAddressArr, userInfo);
        }
    }

    /**
     * Create database information
     *
     * @param dbNameArr the database name array
     * @param dbUserIdArr the database user array
     * @param dbPasswordArr the database user password array
     * @param dbBrokerAddressArr the broker address array
     * @param userInfo the ServerUserInfo
     */
    private void buildDatabaseInfo(
            String[] dbNameArr,
            String[] dbUserIdArr,
            String[] dbPasswordArr,
            String[] dbBrokerAddressArr,
            ServerUserInfo userInfo) {
        for (int n = 0; dbNameArr != null && n < dbNameArr.length; n++) {
            DatabaseInfo databaseInfo = new DatabaseInfo(dbNameArr[n], serverInfo);
            DbUserInfo databaseUserInfo = new DbUserInfo();
            if (dbUserIdArr != null && dbUserIdArr.length > n) {
                databaseUserInfo.setName(dbUserIdArr[n]);
                if (dbUserIdArr[n].equals("dba")) {
                    databaseUserInfo.setDbaAuthority(true);
                }
            }
            if (dbPasswordArr != null && dbPasswordArr.length > n) {
                databaseUserInfo.setNoEncryptPassword(dbPasswordArr[n]);
            }

            if (dbBrokerAddressArr != null && dbBrokerAddressArr.length > n) {
                String dbBroker = dbBrokerAddressArr[n];
                String brokerIp = "";
                String brokerPort = "";
                String[] dbBrokerArr = dbBroker.split(",");
                if (dbBrokerArr != null && dbBrokerArr.length == 2) {
                    brokerIp = dbBrokerArr[0];
                    brokerPort = dbBrokerArr[1];
                }
                databaseInfo.setBrokerIP(brokerIp);
                databaseInfo.setBrokerPort(brokerPort);
            }
            databaseInfo.setAuthLoginedDbUserInfo(databaseUserInfo);
            databaseInfo.setSupportUserSchema(CompatibleUtil.isAfter112(databaseInfo));
            userInfo.addDatabaseInfo(databaseInfo);
        }
    }
}
