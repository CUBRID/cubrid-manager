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
package com.cubrid.cubridmanager.core.mondashboard.task;

import com.cubrid.common.core.util.CipherUtils;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;

/**
 * Check the database user password
 *
 * @author pangqiren
 * @version 1.0 - 2010-6-28 created by pangqiren
 */
public class VerifyDbUserPasswordTask extends SocketTask {

    private static final String[] SEND_MSG_ITEMS =
            new String[] {
                "task",
                "token",
                "dbname",
                "dbuser",
                CIPHER_CHARACTER + "dbuser",
                "dbpasswd",
                CIPHER_CHARACTER + "dbpasswd"
            };

    /**
     * The constructor
     *
     * @param serverInfo
     */
    public VerifyDbUserPasswordTask(ServerInfo serverInfo) {
        super("userverify", serverInfo, SEND_MSG_ITEMS);
    }

    /**
     * Set database name
     *
     * @param dbName String the database name
     */
    public void setDbName(String dbName) {
        this.setMsgItem("dbname", dbName);
    }

    /**
     * Set database user
     *
     * @param dbUser String the database user
     */
    public void setDbUser(String dbUser) {
        if (CompatibleUtil.isSupportCipher(serverInfo.getServerVersionKey())) {
            this.setMsgItem(CIPHER_CHARACTER + "dbuser", CipherUtils.encrypt(dbUser));
        } else {
            this.setMsgItem("dbuser", dbUser);
        }
    }

    /**
     * Set database password
     *
     * @param dbPassword String the password of database
     */
    public void setDbPassword(String dbPassword) {
        if (CompatibleUtil.isSupportCipher(serverInfo.getServerVersionKey())) {
            this.setMsgItem(CIPHER_CHARACTER + "dbpasswd", CipherUtils.encrypt(dbPassword));
        } else {
            this.setMsgItem("dbpasswd", dbPassword);
        }
    }

    /**
     * Check whether the password is valid
     *
     * @return The boolean
     */
    public boolean isValidPassword() {
        TreeNode response = getResponse();
        if (response == null || (this.getErrorMsg() != null && getErrorMsg().trim().length() > 0)) {
            return false;
        }
        return true;
    }
}
