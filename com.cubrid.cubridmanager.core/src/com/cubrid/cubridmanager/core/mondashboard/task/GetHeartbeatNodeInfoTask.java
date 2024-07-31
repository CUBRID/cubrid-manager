/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import com.cubrid.cubridmanager.core.mondashboard.model.DBStatusType;
import com.cubrid.cubridmanager.core.mondashboard.model.DbProcessStatusInfo;
import com.cubrid.cubridmanager.core.mondashboard.model.HADatabaseStatusInfo;
import com.cubrid.cubridmanager.core.mondashboard.model.HAHostStatusInfo;
import com.cubrid.cubridmanager.core.mondashboard.model.HostStatusType;
import com.cubrid.cubridmanager.core.mondashboard.model.ProcessStatusType;
import com.cubrid.cubridmanager.core.mondashboard.model.SyncModeType;
import java.util.ArrayList;
import java.util.List;

/**
 * Get heartbeat node information
 *
 * @author pangqiren
 * @version 1.0 - 2010-6-9 created by pangqiren
 */
public class GetHeartbeatNodeInfoTask extends SocketTask {

    private static final String[] SEND_MSG_ITEMS =
            new String[] {"task", "token", "dbmodeall", "dblist"};
    private List<HAHostStatusInfo> hostStatusList;

    /**
     * The constructor
     *
     * @param serverInfo
     */
    public GetHeartbeatNodeInfoTask(ServerInfo serverInfo) {
        super("heartbeatlist", serverInfo, SEND_MSG_ITEMS);
    }

    /**
     * Set whether to get all databases HA information
     *
     * @param isAll boolean
     */
    public void setAllDb(boolean isAll) {
        if (isAll) {
            this.setMsgItem("dbmodeall", "y");
        } else {
            this.setMsgItem("dbmodeall", "n");
        }
    }

    /**
     * Set database list
     *
     * @param dbList List<String>
     */
    public void setDbList(List<String> dbList) {
        StringBuffer strBuffer = new StringBuffer();
        for (int i = 0; dbList != null && i < dbList.size(); i++) {
            strBuffer.append(dbList.get(i));
            if (dbList.size() - 1 != i) {
                strBuffer.append(",");
            }
        }
        setMsgItem("dblist", strBuffer.toString());
    }

    /** Execute to send message */
    public void execute() {
        if (CompatibleUtil.isSupportHA(serverInfo)) {
            super.execute();
        }
    }

    /**
     * Get HADatabaseStatusInfo of dbName
     *
     * @param dbName The String
     * @return HADatabaseStatusInfo
     */
    public HADatabaseStatusInfo getDatabaseStatusInfo(String dbName) {
        if (hostStatusList == null) {
            getHAHostStatusList();
        }
        if (hostStatusList == null) {
            return null;
        }
        for (int i = 0; i < hostStatusList.size(); i++) {
            HAHostStatusInfo hostStatusInfo = hostStatusList.get(i);
            if (StringUtil.isIpEqual(serverInfo.getHostAddress(), hostStatusInfo.getIp())) {
                List<HADatabaseStatusInfo> dbStatusInfoList = hostStatusInfo.getDbStatusList();
                for (int j = 0; j < dbStatusInfoList.size(); j++) {
                    HADatabaseStatusInfo dbStatusInfo = dbStatusInfoList.get(j);
                    if (dbStatusInfo.getDbName().equals(dbName)) {
                        return dbStatusInfo;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get HAHostStatusInfo of ip
     *
     * @param ip The String
     * @return HAHostStatusInfo
     */
    public HAHostStatusInfo getHostStatusInfo(String ip) {
        if (hostStatusList == null) {
            getHAHostStatusList();
        }
        if (hostStatusList == null) {
            return null;
        }
        for (int i = 0; i < hostStatusList.size(); i++) {
            HAHostStatusInfo hostStatusInfo = hostStatusList.get(i);
            if (StringUtil.isIpEqual(ip, hostStatusInfo.getIp())) {
                return hostStatusInfo;
            }
        }
        return null;
    }

    /**
     * Get host status list
     *
     * @return The List<HAHostStatus>
     */
    public List<HAHostStatusInfo> getHAHostStatusList() {
        TreeNode response = getResponse();
        if (response == null || (this.getErrorMsg() != null && getErrorMsg().trim().length() > 0)) {
            return null;
        }
        String currentHostName = response.getValue("currentnode");
        String currentHostStatus = response.getValue("currentnodestate");
        HAHostStatusInfo currentHaHostStatus = null;
        hostStatusList = new ArrayList<HAHostStatusInfo>();
        for (int i = 0; i < response.childrenSize(); i++) {
            TreeNode node = response.getChildren().get(i);
            if (node.getValue("open") == null) {
                continue;
            }
            if (node.getValue("open").trim().equals("hanodelist")) {
                currentHaHostStatus = buildHaNodeList(node, hostStatusList, currentHostName);
            } else if (node.getValue("open").trim().equals("hadbinfolist")) {
                buildHaDbInfoList(node, currentHaHostStatus);
            }
        }
        if (currentHaHostStatus != null) {
            currentHaHostStatus.setStatusType(HostStatusType.getType(currentHostStatus));
        }
        return hostStatusList;
    }

    /**
     * Parse the node and build HAHostStatus object
     *
     * @param parent The TreeNode
     * @param hostStatusList The List<HAHostStatus>
     * @param currentHostName The String
     * @return The HAHostStatus
     */
    private HAHostStatusInfo buildHaNodeList(
            TreeNode parent, List<HAHostStatusInfo> hostStatusList, String currentHostName) {
        HAHostStatusInfo reHaHostStatus = null;
        List<HAHostStatusInfo> slaveHostStatusInfoList = new ArrayList<HAHostStatusInfo>();
        HAHostStatusInfo masterHostStatusInfo = null;
        for (int i = 0; i < parent.childrenSize(); i++) {
            TreeNode node = parent.getChildren().get(i);
            if (node.getValue("open") == null) {
                continue;
            }
            if (node.getValue("open").trim().equals("node")) {
                String hostName = node.getValue("hostname");
                String ip = node.getValue("ip");
                String priority = node.getValue("priority");
                String state = node.getValue("state");
                HAHostStatusInfo haHostStatus = new HAHostStatusInfo();
                haHostStatus.setHostName(hostName);
                haHostStatus.setIp(ip);
                haHostStatus.setPriority(priority);
                haHostStatus.setStatusType(HostStatusType.getType(state));
                if (hostName.equals(currentHostName)) {
                    reHaHostStatus = haHostStatus;
                }
                if (haHostStatus.getStatusType() == HostStatusType.MASTER) {
                    masterHostStatusInfo = haHostStatus;
                } else if (haHostStatus.getStatusType() == HostStatusType.SLAVE
                        || haHostStatus.getStatusType() == HostStatusType.REPLICA) {
                    slaveHostStatusInfoList.add(haHostStatus);
                }
                hostStatusList.add(haHostStatus);
            }
        }
        // set master and slave relation
        for (int i = 0; i < slaveHostStatusInfoList.size(); i++) {
            slaveHostStatusInfoList.get(i).setMasterHostStatusInfo(masterHostStatusInfo);
        }
        if (masterHostStatusInfo != null) {
            masterHostStatusInfo.setSlaveHostStatusInfoList(slaveHostStatusInfoList);
        }
        return reHaHostStatus;
    }

    /**
     * Parse the node and build HAHostStatus object
     *
     * @param parent The TreeNode
     * @param currentHaHostStatus The HAHostStatus
     */
    private void buildHaDbInfoList(TreeNode parent, HAHostStatusInfo currentHaHostStatus) {
        for (int i = 0; i < parent.childrenSize(); i++) {
            TreeNode node = parent.getChildren().get(i);
            if (node.getValue("open") == null) {
                continue;
            }
            if (node.getValue("open").trim().equals("server")) {
                buildHaServerList(node, currentHaHostStatus);
            }
        }
    }

    /**
     * Parse the node and build HADatabaseStatus object
     *
     * @param parent The TreeNode
     * @param currentHaHostStatus The HAHostStatus
     */
    private void buildHaServerList(TreeNode parent, HAHostStatusInfo currentHaHostStatus) {
        HADatabaseStatusInfo dbStatus = new HADatabaseStatusInfo();
        for (int i = 0; i < parent.childrenSize(); i++) {
            TreeNode node = parent.getChildren().get(i);
            if (node.getValue("open") == null) {
                continue;
            }
            if (node.getValue("open").trim().equals("dbmode")) {
                String dbName = node.getValue("dbname");
                String serverMode = node.getValue("server_mode");
                String error = node.getValue("server_msg");
                dbStatus.setDbName(dbName);
                if (error != null && !error.equals("none")) {
                    dbStatus.setErrorInfo(error);
                }
                dbStatus.setStatusType(
                        DBStatusType.getType(serverMode, serverInfo.isHAMode(dbName)));
                dbStatus.setHaHostStatusInfo(currentHaHostStatus);
                currentHaHostStatus.addHADatabaseStatus(dbStatus);
            } else if (node.getValue("open").trim().equals("dbprocinfo")) {
                String dbName = node.getValue("dbname");
                String pid = node.getValue("pid");
                String state = node.getValue("state");
                DbProcessStatusInfo dbProcessStatusInfo = new DbProcessStatusInfo();
                dbProcessStatusInfo.setDbName(dbName);
                dbProcessStatusInfo.setPid(pid);
                dbProcessStatusInfo.setProcessStatus(ProcessStatusType.getType(state));
                dbStatus.setDbServerProcessStatus(dbProcessStatusInfo);
            } else if (node.getValue("open").trim().equals("applylogdb")) {
                buildProcessList(node, dbStatus, "applylogdb");
            } else if (node.getValue("open").trim().equals("copylogdb")) {
                buildProcessList(node, dbStatus, "copylogdb");
            }
        }
    }

    /**
     * Parse the node and build DbProcessStatusInfo object
     *
     * @param parent The TreeNode
     * @param dbStatus The HADatabaseStatusInfo
     * @param type The String
     */
    private void buildProcessList(TreeNode parent, HADatabaseStatusInfo dbStatus, String type) {
        for (int i = 0; i < parent.childrenSize(); i++) {
            TreeNode node = parent.getChildren().get(i);
            if (node.getValue("open") == null) {
                continue;
            }
            if (node.getValue("open").trim().equals("element")) {
                String hostName = node.getValue("hostname");
                String dbName = node.getValue("dbname");
                String pid = node.getValue("pid");
                String state = node.getValue("state");
                String logPath = node.getValue("logpath");
                String mode = node.getValue("mode");
                DbProcessStatusInfo logDbProcessStatus = new DbProcessStatusInfo();
                logDbProcessStatus.setHostName(hostName);
                logDbProcessStatus.setDbName(dbName);
                logDbProcessStatus.setPid(pid);
                logDbProcessStatus.setLogPath(logPath);
                logDbProcessStatus.setProcessStatus(ProcessStatusType.getType(state));
                logDbProcessStatus.setMode(SyncModeType.getType(mode));
                if ("applylogdb".equals(type)) {
                    dbStatus.addApplyLogDbProcessStatus(logDbProcessStatus);
                } else if ("copylogdb".equals(type)) {
                    dbStatus.addCopyLogDbProcessStatus(logDbProcessStatus);
                }
            }
        }
    }

    public String getCurrentHostStatus() {
        TreeNode response = getResponse();
        if (response == null || this.isSuccess() != true) {
            return null;
        }
        String currentHostStatus = response.getValue("currentnodestate");
        return currentHostStatus;
    }

    public String getCurrentHostName() {
        TreeNode response = getResponse();
        if (response == null || this.isSuccess() != true) {
            return null;
        }
        String currentHostName = response.getValue("currentnode");
        return currentHostName;
    }

    /**
     * Return error msg
     *
     * @return String
     */
    public String getErrorMsg() {
        return null;
    }
}
