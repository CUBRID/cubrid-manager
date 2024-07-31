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
import com.cubrid.cubridmanager.core.replication.model.ReplicationParamConstants;
import com.cubrid.cubridmanager.core.replication.model.ReplicationParamInfo;
import java.util.HashMap;
import java.util.Map;

/**
 * A task that defined the task of "cmtask_get_repl_params"
 *
 * @author wuyingshi
 * @version 1.0 - 2009-8-19 created by wuyingshi
 */
public class GetReplicationParamTask extends SocketTask {

    private static final String[] SEND_MSG_ITEMS =
            new String[] {
                "task",
                "token",
                "master_db_name",
                "slave_db_name",
                "dist_db_name",
                "dist_dba_pass",
                CIPHER_CHARACTER + "dist_dba_pass",
                "mode"
            };

    /**
     * The constructor
     *
     * @param serverInfo
     */
    public GetReplicationParamTask(ServerInfo serverInfo) {
        super("cmtask_get_repl_params", serverInfo, SEND_MSG_ITEMS);
    }

    /**
     * set master db name into msg
     *
     * @param param the master name
     */
    public void setMasterDbName(String param) {
        this.setMsgItem("master_db_name", param);
    }

    /**
     * set slave db name
     *
     * @param param the slave name
     */
    public void setSlaveDbName(String param) {
        super.setMsgItem("slave_db_name", param);
    }

    /**
     * set distdb name
     *
     * @param param the distdbname
     */
    public void setDistDbName(String param) {
        super.setMsgItem("dist_db_name", param);
    }

    /**
     * set distdb password
     *
     * @param param the distdb password
     */
    public void setDistDbDbaPasswd(String param) {
        if (CompatibleUtil.isSupportCipher(serverInfo.getServerVersionKey())) {
            this.setMsgItem(CIPHER_CHARACTER + "dist_dba_pass", CipherUtils.encrypt(param));
        } else {
            this.setMsgItem("dist_dba_pass", param);
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
     * get result from the response.
     *
     * @return the ReplicationParamInfo obj
     */
    public ReplicationParamInfo getReplicationParams() {
        TreeNode response = getResponse();
        if (response == null || (this.getErrorMsg() != null && getErrorMsg().trim().length() > 0)) {
            return null;
        }

        ReplicationParamInfo replicationParamInfo = new ReplicationParamInfo();
        Map<String, String> dataMap = new HashMap<String, String>();
        dataMap.put(
                ReplicationParamConstants.PERF_POLL_INTERVAL,
                response.getValue(ReplicationParamConstants.PERF_POLL_INTERVAL));
        dataMap.put(
                ReplicationParamConstants.SIZE_OF_LOG_BUFFER,
                response.getValue(ReplicationParamConstants.SIZE_OF_LOG_BUFFER));
        dataMap.put(
                ReplicationParamConstants.SIZE_OF_CACHE_BUFFER,
                response.getValue(ReplicationParamConstants.SIZE_OF_CACHE_BUFFER));
        dataMap.put(
                ReplicationParamConstants.SIZE_OF_COPYLOG,
                response.getValue(ReplicationParamConstants.SIZE_OF_COPYLOG));
        dataMap.put(
                ReplicationParamConstants.INDEX_REPLICATION,
                response.getValue(ReplicationParamConstants.INDEX_REPLICATION));
        dataMap.put(
                ReplicationParamConstants.FOR_RECOVERY,
                response.getValue(ReplicationParamConstants.FOR_RECOVERY));
        dataMap.put(
                ReplicationParamConstants.LOG_APPLY_INTERVAL,
                response.getValue(ReplicationParamConstants.LOG_APPLY_INTERVAL));
        dataMap.put(
                ReplicationParamConstants.RESTART_INTERVAL,
                response.getValue(ReplicationParamConstants.RESTART_INTERVAL));
        replicationParamInfo.setParamMap(dataMap);
        return replicationParamInfo;
    }
}
