/*
 * Copyright (C) 2008 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.cubridmanager.core.cubrid.table.task;

import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.cubridmanager.core.Messages;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.sql.SQLException;

/**
 * Execute update statement from null to default value in a appointed column
 *
 * <p>UpdateNull2Def Description
 *
 * @author lizhiqiang
 * @version 1.0 - 2009-9-16 created by lizhiqiang
 */
public class UpdateNullToDefault extends JDBCTask {
    private String table;
    private String column;
    private String defaultValue;
    private int updateCount;

    public UpdateNullToDefault(DatabaseInfo dbInfo) {
        super("UpdateNullToDefault", dbInfo);
    }

    public void execute() {
        updateCount = update();
    }

    /**
     * Execute the update through JDBC
     *
     * @return int
     */
    private int update() {
        String sql =
                "UPDATE "
                        + QuerySyntax.escapeKeyword(table)
                        + " SET "
                        + QuerySyntax.escapeKeyword(column)
                        + "="
                        + defaultValue
                        + " WHERE "
                        + QuerySyntax.escapeKeyword(column)
                        + " IS NULL;";

        // [TOOLS-2425]Support shard broker
        if (databaseInfo.isShard()) {
            sql = databaseInfo.wrapShardQuery(sql);
        }

        try {
            if (errorMsg != null && errorMsg.trim().length() > 0) {
                return -1;
            }

            if (connection == null || connection.isClosed()) {
                errorMsg = Messages.error_getConnection;
                return -1;
            }

            stmt = connection.createStatement();
            return stmt.executeUpdate(sql);
        } catch (SQLException e) {
            errorMsg = e.getMessage();
        } finally {
            finish();
        }
        return -1;
    }

    /**
     * Gets the value of table.
     *
     * @return the table
     */
    public String getTable() {
        return table;
    }

    /** @param table the table to set */
    public void setTable(String table) {
        this.table = table;
    }

    /**
     * Gets the value of column.
     *
     * @return the column
     */
    public String getColumn() {
        return column;
    }

    /** @param column the column to set */
    public void setColumn(String column) {
        this.column = column;
    }

    /**
     * Gets the value of defaultValue.
     *
     * @return the defaultValue
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /** @param defaultValue the defaultValue to set */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Gets the value of updateCount.
     *
     * @return the updateCount
     */
    public int getUpdateCount() {
        return updateCount;
    }

    /** @param updateCount the updateCount to set */
    public void setUpdateCount(int updateCount) {
        this.updateCount = updateCount;
    }
}
