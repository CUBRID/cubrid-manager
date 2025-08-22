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
import java.util.List;

/**
 * Change replicated tables
 *
 * @author pangqiren
 * @version 1.0 - 2009-9-14 created by pangqiren
 */
public class ChangeReplTablesTask extends SocketTask {
    private static final String[] SEND_MSG_ITEMS =
            new String[] {
                "task",
                "token",
                "dist_db_name",
                "dist_db_pass",
                "master_db_name",
                "master_db_id",
                "master_db_pass",
                "class_name_list",
                "all",
                "none"
            };

    /**
     * The constructor
     *
     * @param serverInfo
     */
    public ChangeReplTablesTask(ServerInfo serverInfo) {
        super("cmtask_make_group", serverInfo, SEND_MSG_ITEMS);
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
     * Set distributor database password
     *
     * @param password the password
     */
    public void setDistdbPassword(String password) {
        setMsgItem("dist_db_pass", password);
    }

    /**
     * Set master database name
     *
     * @param mdbName the master db name
     */
    public void setMdbName(String mdbName) {
        setMsgItem("master_db_name", mdbName);
    }

    /**
     * Set master database user id
     *
     * @param mdbUserId the user id
     */
    public void setMdbUserId(String mdbUserId) {
        setMsgItem("master_db_id", mdbUserId);
    }

    /**
     * Set master database password
     *
     * @param mdbPass the password
     */
    public void setMdbPass(String mdbPass) {
        setMsgItem("master_db_pass", mdbPass);
    }

    /**
     * Set replicated all classes
     *
     * @param isReplAll whether replicate all
     */
    public void setReplAllClasses(boolean isReplAll) {
        if (isReplAll) {
            setMsgItem("all", "Y");
        } else {
            setMsgItem("all", "N");
        }
    }

    /**
     * Set replicate none classes
     *
     * @param isNone whether there is no classes
     */
    public void setReplNoneClasses(boolean isNone) {
        if (isNone) {
            setMsgItem("none", "Y");
        } else {
            setMsgItem("none", "N");
        }
    }

    /**
     * Set replicated classes
     *
     * @param classNameList the className list
     */
    public void setReplicatedClasses(List<String> classNameList) {
        StringBuffer buffer = new StringBuffer("");
        if (classNameList != null) {
            for (int i = 0; i < classNameList.size(); i++) {
                buffer.append(classNameList.get(i));
                if (i != classNameList.size() - 1) {
                    buffer.append(",");
                }
            }
        }
        setMsgItem("class_name_list", buffer.toString());
    }
}
