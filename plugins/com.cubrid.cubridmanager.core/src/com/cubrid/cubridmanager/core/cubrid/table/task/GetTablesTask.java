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
package com.cubrid.cubridmanager.core.cubrid.table.task;

import com.cubrid.common.core.util.ConstantsUtil;
import com.cubrid.cubridmanager.core.Messages;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * get tables in a CUBRID database
 *
 * @author moulinwang
 * @version 1.0 - 2009-5-21 created by moulinwang
 */
public class GetTablesTask extends JDBCTask {

    public GetTablesTask(DatabaseInfo dbInfo) {
        super("GetAllTables", dbInfo);
    }

    /**
     * Get all tables and views
     *
     * @return List<String>
     */
    public List<String> getAllTableAndViews() {
        String sql = "SELECT class_name FROM db_class ORDER BY class_name ASC";

        // [TOOLS-2425]Support shard broker
        sql = databaseInfo.wrapShardQuery(sql);

        return getTables(sql);
    }

    /**
     * Get all tables
     *
     * @return List<String>
     */
    public List<String> getAllTables() {
        String sql =
                "SELECT class_name FROM db_class WHERE "
                        + "class_type='CLASS' ORDER BY class_name ASC";

        // [TOOLS-2425]Support shard broker
        sql = databaseInfo.wrapShardQuery(sql);

        return getTables(sql);
    }

    /**
     * Get the system tables
     *
     * @return List<String>
     */
    public List<String> getSystemTables() {
        String sql =
                "SELECT class_name FROM db_class WHERE is_system_class='YES'"
                        + " AND class_type='CLASS' ORDER BY class_name ASC";

        // [TOOLS-2425]Support shard broker
        sql = databaseInfo.wrapShardQuery(sql);

        return getTables(sql);
    }

    /**
     * Get the user tables
     *
     * @return List<String>
     */
    public List<String> getUserTables() {
        String sql;
        if (databaseInfo.isSupportUserSchema()) {
            sql =
                    "SELECT owner_name, class_name FROM db_class WHERE is_system_class='NO'"
                            + " AND class_type='CLASS' ORDER BY class_name ASC";
        } else {
            sql =
                    "SELECT class_name FROM db_class WHERE is_system_class='NO'"
                            + " AND class_type='CLASS' ORDER BY class_name ASC";
        }

        // [TOOLS-2425]Support shard broker
        sql = databaseInfo.wrapShardQuery(sql);

        List<String> tableList = getTables(sql);
        tableList.remove(ConstantsUtil.SCHEMA_DESCRIPTION_TABLE);
        tableList.remove(ConstantsUtil.CUNITOR_HA_TABLE);
        return tableList;
    }

    /**
     * Get the user tables not contain sub-Partition table
     *
     * @return List<String>
     */
    public List<String> getUserTablesNotContainSubPartitionTable() {
        StringBuilder sb = new StringBuilder();
        if (databaseInfo.isSupportUserSchema()) {
            sb.append("SELECT c.owner_name, c.class_name, c.class_type ");
            sb.append("FROM db_class c, db_attribute a ");
            sb.append("WHERE c.class_name=a.class_name AND c.is_system_class='NO' ");
            sb.append("AND c.owner_name=a.owner_name ");
            sb.append("AND a.from_class_name IS NULL ");
            sb.append("AND c.class_type = 'CLASS' ");
            sb.append("GROUP BY c.owner_name, c.class_name, c.class_type ");
            sb.append("ORDER BY c.owner_name, c.class_type, c.class_name");
        } else {
            sb.append("SELECT c.class_name, c.class_type ");
            sb.append("FROM db_class c, db_attribute a ");
            sb.append("WHERE c.class_name=a.class_name AND c.is_system_class='NO' ");
            sb.append("AND a.from_class_name IS NULL ");
            sb.append("AND c.class_type = 'CLASS' ");
            sb.append("GROUP BY c.class_name, c.class_type ");
            sb.append("ORDER BY c.class_type, c.class_name");
        }
        String sql = sb.toString();
        // [TOOLS-2425]Support shard broker
        sql = databaseInfo.wrapShardQuery(sql);

        List<String> tableList = getTables(sql);
        tableList.remove(ConstantsUtil.SCHEMA_DESCRIPTION_TABLE);
        tableList.remove(ConstantsUtil.CUNITOR_HA_TABLE);

        return tableList;
    }

    /**
     * Get the tables
     *
     * @param sql String
     * @return List<String>
     */
    public List<String> getTables(String sql) {
        List<String> tlist = new ArrayList<String>();
        try {
            if (errorMsg != null && errorMsg.trim().length() > 0) {
                return tlist;
            }
            if (connection == null || connection.isClosed()) {
                errorMsg = Messages.error_getConnection;
                return tlist;
            }
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                if (databaseInfo.isSupportUserSchema()) {
                    tlist.add(rs.getString("owner_name") + "." + rs.getString("class_name"));
                } else {
                    tlist.add(rs.getString("class_name"));
                }
            }
        } catch (SQLException e) {
            errorMsg = e.getMessage();
        } finally {
            finish();
        }
        return tlist;
    }
}
