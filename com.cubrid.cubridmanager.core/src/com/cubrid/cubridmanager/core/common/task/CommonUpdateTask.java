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

import com.cubrid.common.core.util.CipherUtils;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import com.cubrid.cubridmanager.core.utils.ModelUtil.YesNoType;

/**
 * The common task of update transaction. eg:drop class,copy class,back up database
 *
 * @author robin
 * @version 1.0 - 2009-6-4 created by robin
 */
public class CommonUpdateTask extends SocketTask {

    public CommonUpdateTask(String taskName, ServerInfo serverInfo, String[] sendMSGItems) {
        super(taskName, serverInfo, sendMSGItems);
    }

    public CommonUpdateTask(
            String taskName, ServerInfo serverInfo, String[] sendMSGItems, String charset) {
        super(taskName, serverInfo, sendMSGItems, charset, charset);
    }

    /**
     * set database name
     *
     * @param dbName String The given database name
     */
    public void setDbName(String dbName) {
        super.setMsgItem("dbname", dbName);
    }

    /**
     * set whether repair database for taskname:checkdb
     *
     * @param type YesNoType The given object of YesNoType
     */
    public void setRepairDb(YesNoType type) {
        super.setMsgItem("repairdb", type.toString().toLowerCase());
    }

    /**
     * Set class name for taskname:optimizedb
     *
     * @param className String The given class name
     */
    public void setClassName(String className) {
        super.setMsgItem("classname", className);
    }

    /**
     * set whether delete backup volumes for taskname:deletedb
     *
     * @param delbackup YesNoType The given object of YesNoType
     */
    public void setDelbackup(YesNoType delbackup) {
        super.setMsgItem("delbackup", delbackup.getText().toLowerCase());
    }

    /**
     * Set user name for taskname:deleteuser
     *
     * @param userName String The given user name
     */
    public void setUserName(String userName) {
        if (CompatibleUtil.isSupportCipher(serverInfo.getServerVersionKey())) {
            this.setMsgItem(CIPHER_CHARACTER + "username", CipherUtils.encrypt(userName));
        } else {
            this.setMsgItem("username", userName);
        }
    }

    /**
     * Set whether show verbose for taskname:COMPACT_DATABASE_TASK_NANE
     *
     * @param verbose YesNoType The given object of YesNoType
     */
    public void setVerbose(YesNoType verbose) {
        super.setMsgItem("verbose", verbose.getText().toLowerCase());
    }

    /**
     * Get compact database verbose result
     *
     * @return String[]
     */
    public String[] getVerboseResult() {
        TreeNode response = getResponse();
        if (response == null || (this.getErrorMsg() != null && getErrorMsg().trim().length() > 0)) {
            return null;
        }
        for (int i = 0; i < response.childrenSize(); i++) {
            TreeNode node = response.getChildren().get(i);
            if (node.getValue("open") == null) {
                continue;
            }
            if (node.getValue("open").trim().equals("log")) {
                return node.getValues("line");
            }
        }
        return new String[] {};
    }
}
