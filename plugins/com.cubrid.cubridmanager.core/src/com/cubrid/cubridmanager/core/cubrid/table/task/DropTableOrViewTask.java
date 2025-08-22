/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.cubridmanager.core.cubrid.table.task;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.Messages;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.sql.SQLException;
import org.slf4j.Logger;

/**
 * to delete a table or view by JDBC
 *
 * @author moulinwang
 * @version 1.0 - 2009-10-21 created by moulinwang
 */
public class DropTableOrViewTask extends JDBCTask {
    private static final Logger LOGGER = LogUtil.getLogger(DropTableOrViewTask.class);
    private String[] tableName = null;
    private String[] viewName = null;

    public DropTableOrViewTask(DatabaseInfo dbInfo) {
        super("DropTable", dbInfo);
    }

    public void execute() {
        try {
            if (errorMsg != null && errorMsg.trim().length() > 0) {
                return;
            }
            if (connection == null || connection.isClosed()) {
                errorMsg = Messages.error_getConnection;
                return;
            }
            stmt = connection.createStatement();
            for (int i = 0; tableName != null && i < tableName.length; i++) {
                String sql = "DROP TABLE " + QuerySyntax.escapeKeyword(tableName[i]);
                stmt.addBatch(sql);
            }
            for (int i = 0; viewName != null && i < viewName.length; i++) {
                String sql = "DROP VIEW " + QuerySyntax.escapeKeyword(viewName[i]);
                stmt.addBatch(sql);
            }
            stmt.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            LOGGER.error("", e);
            errorMsg = e.getMessage();
            try {
                connection.rollback();
            } catch (SQLException e1) {
                LOGGER.error("", e);
                errorMsg = errorMsg + StringUtil.NEWLINE + e1.getMessage();
            }
        } finally {
            finish();
        }
    }

    /**
     * Set table names
     *
     * @param tableName The table name array
     */
    public void setTableName(String[] tableName) {
        if (tableName != null) {
            this.tableName = tableName.clone();
        }
    }

    /**
     * Set view names
     *
     * @param viewName The view name array
     */
    public void setViewName(String[] viewName) {
        if (viewName != null) {
            this.viewName = viewName.clone();
        }
    }
}
