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
package com.cubrid.cubridmanager.core.replication.task;

import com.cubrid.common.core.util.CipherUtils;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import com.cubrid.cubridmanager.core.replication.model.DistributorInfo;
import com.cubrid.cubridmanager.core.replication.model.MasterInfo;
import com.cubrid.cubridmanager.core.replication.model.ReplicationInfo;
import com.cubrid.cubridmanager.core.replication.model.SlaveInfo;

/**
 * This task is responsible to get replication information
 *
 * @author pangqiren
 * @version 1.0 - 2009-9-27 created by pangqiren
 */
public class GetReplicationInfoTask extends SocketTask {
    private static final String[] SEND_MSG_ITEMS =
            new String[] {
                "task",
                "token",
                "dist_db_name",
                "dist_dba_pass",
                CIPHER_CHARACTER + "dist_dba_pass",
                "mode"
            };

    private String distDbName = "";

    /**
     * The constructor
     *
     * @param serverInfo
     */
    public GetReplicationInfoTask(ServerInfo serverInfo) {
        super("cmtask_get_repl_info", serverInfo, SEND_MSG_ITEMS);
    }

    /**
     * Set distributor database name
     *
     * @param dbName the database name
     */
    public void setDistDbName(String dbName) {
        this.setMsgItem("dist_db_name", dbName);
        distDbName = dbName;
    }

    /**
     * Set dba password
     *
     * @param password the password
     */
    public void setDbaPassword(String password) {
        if (CompatibleUtil.isSupportCipher(serverInfo.getServerVersionKey())) {
            this.setMsgItem(CIPHER_CHARACTER + "dist_dba_pass", CipherUtils.encrypt(password));
        } else {
            this.setMsgItem("dist_dba_pass", password);
        }
    }

    /**
     * Set the database running status
     *
     * @param isRunning whether it is running
     */
    public void setRunningMode(boolean isRunning) {
        if (isRunning) {
            this.setMsgItem("mode", "C");
        } else {
            this.setMsgItem("mode", "S");
        }
    }

    /**
     * Get replication information
     *
     * @return the ReplicationInfo obj
     */
    public ReplicationInfo getReplicationInfo() {
        TreeNode response = getResponse();
        if (response == null || (this.getErrorMsg() != null && getErrorMsg().trim().length() > 0)) {
            return null;
        }
        ReplicationInfo replInfo = new ReplicationInfo();

        DistributorInfo distInfo = new DistributorInfo();
        String agentPort = response.getValue("agent_port");
        String copyLogPath = response.getValue("copy_log_path");
        String trailLogPath = response.getValue("trail_log_path");
        String errorLogPath = response.getValue("error_log_path");
        String delayTimeLogSize = response.getValue("delay_time_log_size");
        String retryConnect = response.getValue("retry_connect");
        distInfo.setDistDbName(distDbName);
        distInfo.setAgentPort(agentPort);
        distInfo.setCopyLogPath(copyLogPath);
        distInfo.setTrailLogPath(trailLogPath);
        distInfo.setErrorLogPath(errorLogPath);
        distInfo.setDelayTimeLogSize(delayTimeLogSize);
        distInfo.setRestartReplWhenError(retryConnect.equalsIgnoreCase("Y"));
        replInfo.setDistInfo(distInfo);

        MasterInfo masterInfo = new MasterInfo();
        String masterDbName = response.getValue("master_db_name");
        String masterIP = response.getValue("master_ip");
        String replServerPort = response.getValue("repl_server_port");
        masterInfo.setMasterDbName(masterDbName);
        masterInfo.setMasterIp(masterIP);
        masterInfo.setReplServerPort(replServerPort);
        replInfo.addMasterInfo(masterInfo);

        SlaveInfo slaveInfo = new SlaveInfo();
        String slaveName = response.getValue("slave_db_name");
        String slaveIP = response.getValue("slave_ip");
        String userId = response.getValue("slave_userid");
        String password = response.getValue("slave_pass");
        slaveInfo.setSlaveDbName(slaveName);
        slaveInfo.setSlaveIP(slaveIP);
        slaveInfo.setDbUser(userId);
        slaveInfo.setPassword(password);
        replInfo.addSlaveInfo(slaveInfo);

        return replInfo;
    }
}
