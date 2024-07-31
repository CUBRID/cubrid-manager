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
 * This task is responsible to create slave database
 *
 * @author pangqiren
 * @version 1.0 - 2009-9-1 created by pangqiren
 */
public class CreateSlaveDbTask extends SocketTask {
    private static final String[] SEND_MSG_ITEMS =
            new String[] {
                "task",
                "token",
                "slave_db_name",
                "backupfile_path",
                "master_db_name",
                "master_dba_pass",
                CIPHER_CHARACTER + "master_dba_pass",
                "dist_db_name",
                "dist_dba_pass",
                CIPHER_CHARACTER + "dist_dba_pass",
                "repluser_id",
                CIPHER_CHARACTER + "repluser_id",
                "repluser_passwd",
                CIPHER_CHARACTER + "repluser_passwd"
            };

    /**
     * The constructor
     *
     * @param serverInfo
     */
    public CreateSlaveDbTask(ServerInfo serverInfo) {
        super("cmtask_make_slavedb", serverInfo, SEND_MSG_ITEMS);
    }

    /**
     * Set slave database name
     *
     * @param dbName the database name
     */
    public void setSlaveDbName(String dbName) {
        setMsgItem("slave_db_name", dbName);
    }

    /**
     * Set slave database path
     *
     * @param dbPath the database path
     */
    public void setSlaveDbPath(String dbPath) {
        setMsgItem("backupfile_path", dbPath);
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
     * Set master database password
     *
     * @param password the password
     */
    public void setMasterDbPassword(String password) {
        if (CompatibleUtil.isSupportCipher(serverInfo.getServerVersionKey())) {
            this.setMsgItem(CIPHER_CHARACTER + "master_dba_pass", CipherUtils.encrypt(password));
        } else {
            this.setMsgItem("master_dba_pass", password);
        }
    }

    /**
     * Set distributor database name
     *
     * @param distdbName the distributor name
     */
    public void setDistDbName(String distdbName) {
        setMsgItem("dist_db_name", distdbName);
    }

    /**
     * Set distributor database password
     *
     * @param password the password
     */
    public void setDistDbaPassword(String password) {
        if (CompatibleUtil.isSupportCipher(serverInfo.getServerVersionKey())) {
            this.setMsgItem(CIPHER_CHARACTER + "dist_dba_pass", CipherUtils.encrypt(password));
        } else {
            this.setMsgItem("dist_dba_pass", password);
        }
    }

    /**
     * Set slave database user
     *
     * @param dbUser the database user
     */
    public void setSlaveDbUser(String dbUser) {
        if (CompatibleUtil.isSupportCipher(serverInfo.getServerVersionKey())) {
            this.setMsgItem(CIPHER_CHARACTER + "repluser_id", CipherUtils.encrypt(dbUser));
        } else {
            this.setMsgItem("repluser_id", dbUser);
        }
    }

    /**
     * Set slave database user password
     *
     * @param password the password
     */
    public void setSlaveDbPassword(String password) {
        if (CompatibleUtil.isSupportCipher(serverInfo.getServerVersionKey())) {
            this.setMsgItem(CIPHER_CHARACTER + "repluser_passwd", CipherUtils.encrypt(password));
        } else {
            this.setMsgItem("repluser_passwd", password);
        }
    }
}
