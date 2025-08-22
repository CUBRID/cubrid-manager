/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the <ORGANIZATION> nor the names of its contributors
 *   may be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */
package com.cubrid.cubridmanager.core.cubrid.user.task;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.sql.SQLException;
import org.slf4j.Logger;

/**
 * drop user task
 *
 * @author fulei
 * @version 1.0 - 2012-09-10 created by fulei
 */
public class DropUserTask extends JDBCTask {
    private static final Logger LOGGER = LogUtil.getLogger(DropUserTask.class);
    private String userName;

    /**
     * The constructor
     *
     * @param dbInfo
     */
    public DropUserTask(DatabaseInfo dbInfo, String userName) {
        super("DropUserTask", dbInfo);
        this.userName = userName;
    }

    public void execute() {
        try {
            dropUser();
        } catch (SQLException e) {
            errorMsg = e.getMessage();
        } finally {
            finish();
        }
    }

    /**
     * drop user
     *
     * @param userName String
     * @return Boolean result
     */
    public boolean dropUser() throws SQLException {
        final String sqlStr = "DROP USER " + QuerySyntax.escapeKeyword(userName) + ";";
        try {
            stmt = connection.createStatement();
            stmt.execute(sqlStr);
        } catch (SQLException e) {
            LOGGER.error("drop user failed", e);
            this.setErrorMsg(e.getMessage());
            return false;
        } finally {
            finish();
        }
        return true;
    }
}
