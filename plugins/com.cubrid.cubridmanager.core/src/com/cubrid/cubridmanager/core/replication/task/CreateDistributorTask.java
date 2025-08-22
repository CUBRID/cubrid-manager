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

/**
 * This task is responsible to create distributor
 *
 * @author pangqiren
 * @version 1.0 - 2009-9-1 created by pangqiren
 */
public class CreateDistributorTask extends SocketTask {
    private static final String[] SEND_MSG_ITEMS =
            new String[] {
                "task",
                "token",
                "dist_db_name",
                "dist_db_path",
                "dba_pass",
                CIPHER_CHARACTER + "dba_pass",
                "repl_agent_status_port",
                "master_db_name",
                "master_db_ip",
                "repl_server_port",
                "copy_log_path",
                "trail_log_path",
                "error_log_path",
                "delay_time_log_size",
                "restart_flag"
            };

    /**
     * The constructor
     *
     * @param serverInfo
     */
    public CreateDistributorTask(ServerInfo serverInfo) {
        super("cmtask_make_distdb", serverInfo, SEND_MSG_ITEMS);
    }

    /**
     * Set distributor database name
     *
     * @param dbName the database name
     */
    public void setDistDbName(String dbName) {
        setMsgItem("dist_db_name", dbName);
    }

    /**
     * Set distributor database path
     *
     * @param dbPath the database path
     */
    public void setDistDbPath(String dbPath) {
        setMsgItem("dist_db_path", dbPath);
    }

    /**
     * Set DBA password of the distributor database
     *
     * @param dbaPassword the dba password
     */
    public void setDbaPassword(String dbaPassword) {
        if (CompatibleUtil.isSupportCipher(serverInfo.getServerVersionKey())) {
            this.setMsgItem(CIPHER_CHARACTER + "dba_pass", CipherUtils.encrypt(dbaPassword));
        } else {
            this.setMsgItem("dba_pass", dbaPassword);
        }
    }

    /**
     * Set replication agent port
     *
     * @param agentPort the agent port
     */
    public void setReplAgentPort(String agentPort) {
        setMsgItem("repl_agent_status_port", agentPort);
    }

    /**
     * Set master database name
     *
     * @param mdbName the master name
     */
    public void setMasterDbName(String mdbName) {
        setMsgItem("master_db_name", mdbName);
    }

    /**
     * Set master database IP
     *
     * @param mdbIp the master ip
     */
    public void setMasterDbIp(String mdbIp) {
        setMsgItem("master_db_ip", mdbIp);
    }

    /**
     * Set replication server port
     *
     * @param serverPort the server port
     */
    public void setReplServerPort(String serverPort) {
        setMsgItem("repl_server_port", serverPort);
    }

    /**
     * Set copy log path
     *
     * @param logPath the log path
     */
    public void setCopyLogPath(String logPath) {
        setMsgItem("copy_log_path", logPath);
    }

    /**
     * Set trail log path
     *
     * @param logPath the log path
     */
    public void setTrailLogPath(String logPath) {
        setMsgItem("trail_log_path", logPath);
    }

    /**
     * Set error log path
     *
     * @param logPath the log path
     */
    public void setErrorLogPath(String logPath) {
        setMsgItem("error_log_path", logPath);
    }

    /**
     * Set delay time log size
     *
     * @param logSize the log size
     */
    public void setDelayTimeLogSize(String logSize) {
        setMsgItem("delay_time_log_size", logSize);
    }

    /**
     * Set restart replication when network error
     *
     * @param isRestartRepl whether restart repl
     */
    public void setRestartRepl(boolean isRestartRepl) {
        if (isRestartRepl) {
            setMsgItem("restart_flag", "y");
        } else {
            setMsgItem("restart_flag", "n");
        }
    }
}
