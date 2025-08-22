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

import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.cubridmanager.core.Messages;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.sql.SQLException;

/**
 * To delete all records in a table
 *
 * @author moulinwang
 * @version 1.0 - 2009-5-21 created by moulinwang
 */
public class DelAllRecordsTask extends JDBCTask {
    private String[] tableName;
    private String whereCondition;
    private int[] deleteRecordsCount;

    public DelAllRecordsTask(DatabaseInfo dbInfo) {
        super("DelRecords", dbInfo);
    }

    /** Execute the tasks */
    public void execute() {
        try {
            if (errorMsg != null && errorMsg.trim().length() > 0) {
                return;
            }

            if (connection == null || connection.isClosed()) {
                errorMsg = Messages.error_getConnection;
                return;
            }

            if (databaseInfo.isShard()) {
                deleteRecordsCount = new int[tableName.length];
            }

            stmt = connection.createStatement();
            for (int i = 0; i < tableName.length; i++) {
                if (databaseInfo.isShard()) {
                    deleteRecordsCount[i] = 0;
                }

                String sql = "DELETE FROM " + QuerySyntax.escapeKeyword(tableName[i]);
                if (null != whereCondition) {
                    sql = sql + " " + whereCondition;
                }

                if (databaseInfo.isShard()) {
                    sql = databaseInfo.wrapShardQuery(sql);
                    deleteRecordsCount[i] = stmt.executeUpdate(sql);
                } else {
                    stmt.addBatch(sql);
                }
            }

            if (!databaseInfo.isShard()) {
                // TODO Can replace with a single executeUpdate()?
                deleteRecordsCount = stmt.executeBatch();
            }

            connection.commit();
        } catch (SQLException e) {
            errorMsg = e.getMessage();
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

    public void setWhereCondition(String whereCondition) {
        this.whereCondition = whereCondition;
    }

    /**
     * Get the deleted record count
     *
     * @return int[]
     */
    public int[] getDeleteRecordsCount() {
        if (deleteRecordsCount != null) {
            return deleteRecordsCount.clone();
        }

        return new int[] {};
    }
}
