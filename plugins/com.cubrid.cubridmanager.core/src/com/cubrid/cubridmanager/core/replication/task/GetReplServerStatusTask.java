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

import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;

/**
 * This task is responsible to get replication server status
 *
 * @author pangqiren
 * @version 1.0 - 2009-10-14 created by pangqiren
 */
public class GetReplServerStatusTask extends SocketTask {
    private static final String[] SEND_MSG_ITEMS = new String[] {"task", "token", "db_name"};

    /**
     * The constructor
     *
     * @param serverInfo
     */
    public GetReplServerStatusTask(ServerInfo serverInfo) {
        super("cmtask_repl_server_status", serverInfo, SEND_MSG_ITEMS);
    }

    /**
     * Set database name
     *
     * @param dbName the database name
     */
    public void setDbName(String dbName) {
        setMsgItem("db_name", dbName);
    }

    /**
     * Get whether the replication server is active
     *
     * @return <code>true</code> if it is active;<code>false</code> otherwise
     */
    public boolean isActive() {
        TreeNode response = getResponse();
        if (response == null || (this.getErrorMsg() != null && getErrorMsg().trim().length() > 0)) {
            return false;
        }
        String str = response.getValue("is_active");
        if (str != null && str.equalsIgnoreCase("Y")) {
            return true;
        }
        return false;
    }
}
