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

/**
 * Get replicated table list
 *
 * @author pangqiren
 * @version 1.0 - 2009-9-14 created by pangqiren
 */
public class GetReplicatedTablesTask extends SocketTask {
    private static final String[] SEND_MSG_ITEMS =
            new String[] {
                "task",
                "token",
                "dist_db_name",
                "master_db_name",
                "slave_db_name",
                "dist_dba_pass",
                CIPHER_CHARACTER + "dist_dba_pass",
                "mode"
            };

    /**
     * The constructor
     *
     * @param serverInfo
     */
    public GetReplicatedTablesTask(ServerInfo serverInfo) {
        super("cmtask_get_repl_group_tables", serverInfo, SEND_MSG_ITEMS);
    }

    /**
     * Set distributor database name
     *
     * @param dbName the database name
     */
    public void setDistdbName(String dbName) {
        setMsgItem("dist_db_name", dbName);
    }

    /**
     * Set master database name
     *
     * @param mdbName the master name
     */
    public void setMasterdbName(String mdbName) {
        setMsgItem("master_db_name", mdbName);
    }

    /**
     * Set slave database name
     *
     * @param sdbName the slave name
     */
    public void setSlavedbName(String sdbName) {
        setMsgItem("slave_db_name", sdbName);
    }

    /**
     * Set distributor database password
     *
     * @param password the password
     */
    public void setDistdbPassword(String password) {
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
     * Get replicated tables
     *
     * @return String[] the replicated tables
     */
    public String[] getReplicatedTables() {
        TreeNode response = getResponse();
        if (response == null || (this.getErrorMsg() != null && getErrorMsg().trim().length() > 0)) {
            return null;
        }
        for (int i = 0; i < response.childrenSize(); i++) {
            TreeNode node = response.getChildren().get(i);
            if (node != null
                    && node.getValue("open") != null
                    && node.getValue("open").equals("repl_group_tablelist")) {
                return node.getValues("class_name");
            }
        }
        return new String[0];
    }

    /**
     * Get whether replicate all
     *
     * @return <code>true</code> if replicated all;<code>false</code>otherwise
     */
    public boolean isReplicateAll() {
        TreeNode response = getResponse();
        if (response == null || (this.getErrorMsg() != null && getErrorMsg().trim().length() > 0)) {
            return false;
        }
        String isReplAll = response.getValue("all_repl");
        return isReplAll != null && isReplAll.equalsIgnoreCase("Y");
    }
}
