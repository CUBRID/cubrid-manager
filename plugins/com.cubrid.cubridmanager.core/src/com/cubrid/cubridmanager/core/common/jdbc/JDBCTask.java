/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.cubridmanager.core.common.jdbc;

import com.cubrid.common.core.task.AbstractTask;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;

/**
 * This task is responsible to execute sql by JDBC
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class JDBCTask extends AbstractTask {

    private static final Logger LOGGER = LogUtil.getLogger(JDBCTask.class);

    protected DatabaseInfo databaseInfo = null;
    protected Connection connection = null;
    protected Statement stmt = null;
    protected ResultSet rs = null;
    protected volatile boolean isCancel = false;
    private boolean isSharedConnection = false;

    public JDBCTask(String taskName, DatabaseInfo dbInfo) {
        this(taskName, dbInfo, true);
    }

    public JDBCTask(String taskName, DatabaseInfo dbInfo, boolean isAutoCommit) {
        this.databaseInfo = dbInfo;
        this.taskName = taskName;
        try {
            connection = JDBCConnectionManager.getConnection(dbInfo, isAutoCommit);
        } catch (Exception e) {
            errorMsg = e.getLocalizedMessage();
        }

        this.isSharedConnection = false;
    }

    public JDBCTask(String taskName, DatabaseInfo dbInfo, Connection connection) {
        this.databaseInfo = dbInfo;
        this.taskName = taskName;
        this.connection = connection;
        this.isSharedConnection = true;
    }

    /** Send request to Server */
    public void execute() {}

    public Connection getConnection() {
        return connection;
    }

    public void cancel() {
        try {
            isCancel = true;
            if (stmt != null) {
                stmt.cancel();
            }
        } catch (SQLException e) {
            LOGGER.error("", e);
        }

        if (isSharedConnection) {
            QueryUtil.freeQuery(stmt, rs);
        } else {
            QueryUtil.freeQuery(connection, stmt, rs);
        }
    }

    /** Free JDBC connection resource */
    public void finish() {
        if (isSharedConnection) {
            QueryUtil.freeQuery(stmt, rs);
        } else {
            QueryUtil.freeQuery(connection, stmt, rs);
            connection = null;
        }
        stmt = null;
        rs = null;
    }

    public boolean isCancel() {
        return isCancel;
    }

    public void setCancel(boolean isCancel) {
        this.isCancel = isCancel;
    }

    public boolean isSuccess() {
        return this.errorMsg == null;
    }
}
