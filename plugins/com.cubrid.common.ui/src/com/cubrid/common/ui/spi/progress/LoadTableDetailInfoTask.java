/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.spi.progress;

import com.cubrid.common.core.common.model.TableDetailInfo;
import com.cubrid.common.core.schemacomment.SchemaCommentHandler;
import com.cubrid.common.core.schemacomment.model.SchemaComment;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCTask;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import org.slf4j.Logger;

/**
 * Load TableDetailInfoTask
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-1-8 created by Kevin.Wang
 */
public class LoadTableDetailInfoTask extends JDBCTask {
    private static final Logger LOGGER = LogUtil.getLogger(LoadTableDetailInfoTask.class);
    private CubridDatabase database;
    private String tableName;
    private TableDetailInfo tableInfo = null;

    /**
     * Load Table Detail Info Task
     *
     * @param taskName
     * @param database
     * @param tableName
     */
    public LoadTableDetailInfoTask(String taskName, CubridDatabase database, String tableName) {
        super(taskName, database.getDatabaseInfo());
        this.database = database;
        this.tableName = tableName;
    }

    public void execute() { // FIXME move this logic to core module

        if (StringUtil.isEmpty(tableName)) {
            return;
        }

        boolean isSupportUserSchema = database.getDatabaseInfo().isSupportUserSchema();

        StringBuilder sql =
                new StringBuilder()
                        .append("SELECT \n")
                        .append("    c.class_name, \n")
                        .append("    COUNT(*) AS count_column, \n")
                        .append("    CAST(SUM(\n")
                        .append("    CASE \n")
                        .append("          WHEN \n")
                        .append("              \"data_type\" = 'BIGINT' THEN 8.0 \n")
                        .append("          WHEN \n")
                        .append("              \"data_type\" = 'INTEGER' THEN 4.0 \n")
                        .append("          WHEN \n")
                        .append("              \"data_type\" = 'SMALLINT' THEN 2.0 \n")
                        .append("          WHEN \n")
                        .append("              \"data_type\" = 'FLOAT' THEN 4.0 \n")
                        .append("          WHEN \n")
                        .append("              \"data_type\" = 'DOUBLE' THEN 8.0 \n")
                        .append("          WHEN \n")
                        .append("              \"data_type\" = 'MONETARY' THEN 12.0 \n")
                        .append("          WHEN \n")
                        .append("              \"data_type\" = 'STRING' THEN a.prec \n")
                        .append("          WHEN \n")
                        .append("              \"data_type\" = 'VARCHAR' THEN a.prec \n")
                        .append("          WHEN \n")
                        .append("              \"data_type\" = 'NVARCHAR' THEN a.prec \n")
                        .append("          WHEN \n")
                        .append("              \"data_type\" = 'CHAR' THEN a.prec \n")
                        .append("          WHEN \n")
                        .append("              \"data_type\" = 'NCHAR' THEN a.prec \n")
                        .append("          WHEN \n")
                        .append("              \"data_type\" = 'TIMESTAMP' THEN 8.0 \n")
                        .append("          WHEN \n")
                        .append("              \"data_type\" = 'DATE' THEN 4.0 \n")
                        .append("          WHEN \n")
                        .append("              \"data_type\" = 'TIME' THEN 4.0 \n")
                        .append("          WHEN \n")
                        .append("              \"data_type\" = 'DATETIME' THEN 4.0 \n")
                        .append("          WHEN \n")
                        .append("              \"data_type\" = 'BIT' THEN FLOOR(prec / 8.0) \n")
                        .append("          WHEN \n")
                        .append(
                                "              \"data_type\" = 'BIT VARYING' THEN FLOOR(prec / 8.0) \n")
                        .append("          ELSE 0 \n")
                        .append("    END ) AS BIGINT) AS size_column, \n")
                        .append("    SUM(\n")
                        .append("    CASE \n")
                        .append("          WHEN \n")
                        .append("              \"data_type\" = 'STRING' THEN 1 \n")
                        .append("          WHEN \n")
                        .append("              \"data_type\" = 'VARCHAR' THEN 1 \n")
                        .append("          WHEN \n")
                        .append("              \"data_type\" = 'NVARCHAR' THEN 1 \n")
                        .append("          WHEN \n")
                        .append("              \"data_type\" = 'NCHAR' THEN 1 \n")
                        .append("          WHEN \n")
                        .append("              \"data_type\" = 'BIT VARYING' THEN 1 \n")
                        .append("          ELSE 0 \n")
                        .append("    END ) AS size_over_column, \n")
                        .append("    c.class_type, \n")
                        .append("    c.partitioned \n");
        if (isSupportUserSchema) {
            sql.append("    ,c.owner_name\n");
        }
        sql.append("FROM \n")
                .append("    db_class c, \n")
                .append("    db_attribute a \n")
                .append("WHERE \n");
        if (isSupportUserSchema) {
            sql.append("    CONCAT(c.owner_name, '.', c.class_name) = ? \n");
        } else {
            sql.append("    c.class_name = ? \n");
        }
        sql.append("    AND \n")
                .append("    c.class_name = a.class_name \n")
                .append("    AND \n")
                .append("    c.is_system_class = 'NO' \n")
                .append("    AND \n")
                .append("    c.class_type = 'CLASS' \n")
                .append("    AND \n")
                .append("    a.from_class_name IS NULL \n")
                .append("GROUP BY c.class_name;\n");
        String query = sql.toString();

        StringBuilder sqlIndex =
                new StringBuilder()
                        .append("SELECT \n")
                        .append("    c.class_name, \n")
                        .append("    SUM(\n")
                        .append("    CASE \n")
                        .append("          WHEN \n")
                        .append("              i.is_unique = 'YES' \n")
                        .append("              AND \n")
                        .append("              i.is_primary_key = 'NO' THEN 1 \n")
                        .append("          ELSE 0 \n")
                        .append("    END ) AS count_unique, \n")
                        .append("    SUM(\n")
                        .append("    CASE \n")
                        .append("          WHEN \n")
                        .append("              i.is_unique = 'YES' \n")
                        .append("              AND \n")
                        .append("              i.is_primary_key = 'YES' THEN 1 \n")
                        .append("          ELSE 0 \n")
                        .append("    END ) AS count_primary_key, \n")
                        .append(
                                "    SUM(DECODE(i.is_foreign_key, 'YES', 1, 0)) AS count_foreign_key, \n")
                        .append("    SUM(\n")
                        .append("    CASE \n")
                        .append("          WHEN \n")
                        .append("              i.is_unique = 'NO' \n")
                        .append("              AND \n")
                        .append("              i.is_primary_key = 'NO' THEN 1 \n")
                        .append("          ELSE 0 \n")
                        .append("    END ) AS count_index \n");
        if (isSupportUserSchema) {
            sqlIndex.append("    ,c.owner_name\n");
        }
        sqlIndex.append("FROM \n")
                .append("    db_class c, \n")
                .append("    db_index_key k, \n")
                .append("    db_index i \n")
                .append("WHERE \n");
        if (isSupportUserSchema) {
            sqlIndex.append("    CONCAT(c.owner_name, '.', c.class_name) = ? ");
        } else {
            sqlIndex.append("    c.class_name = ? ");
        }
        sqlIndex.append("    AND \n")
                .append("    c.class_name = k.class_name \n")
                .append("    AND \n")
                .append("    k.class_name = i.class_name \n")
                .append("    AND \n")
                .append("    k.index_name = i.index_name \n")
                .append("    AND \n")
                .append("    c.class_type = 'CLASS' \n")
                .append("    AND \n")
                .append("    c.is_system_class = 'NO' \n")
                .append("    AND \n")
                .append("    i.key_count >= 1 \n")
                .append("    AND \n")
                .append(
                        "    NOT EXISTS (SELECT 1 FROM db_partition p WHERE c.class_name = LOWER(p.partition_class_name)) \n")
                .append("GROUP BY c.class_name;\n");
        String queryIndex = sqlIndex.toString();

        // [TOOLS-2425]Support shard broker
        if (CubridDatabase.hasValidDatabaseInfo(database)) {
            query = database.getDatabaseInfo().wrapShardQuery(query);
            queryIndex = database.getDatabaseInfo().wrapShardQuery(queryIndex);
        }

        PreparedStatement pStmt = null;
        try {
            pStmt = connection.prepareStatement(query);
            pStmt.setString(1, tableName);
            rs = pStmt.executeQuery();

            while (rs.next()) {
                String tableName = rs.getString(1);
                int countColumn = rs.getInt(2);
                BigDecimal recordsSize = rs.getBigDecimal(3);
                boolean columnOverSize = rs.getInt(4) > 0;
                String classType = rs.getString(5);
                String partitioned = rs.getString(6);
                if (isSupportUserSchema) {
                    tableName = rs.getString(7) + "." + tableName;
                }

                tableInfo = new TableDetailInfo();
                tableInfo.setTableName(tableName);
                tableInfo.setColumnsCount(countColumn);
                tableInfo.setRecordsSize(recordsSize);
                tableInfo.setHasUnCountColumnSize(columnOverSize);
                tableInfo.setClassType(classType);
                tableInfo.setPartitioned(partitioned);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            QueryUtil.freeQuery(pStmt, rs);
        }

        if (tableInfo != null) {
            try {
                pStmt = connection.prepareStatement(queryIndex);
                pStmt.setString(1, tableName);
                rs = pStmt.executeQuery();
                while (rs.next()) {
                    String tableName = rs.getString(1);
                    int ukCount = rs.getInt(2);
                    int pkCount = rs.getInt(3);
                    int fkCount = rs.getInt(4);
                    int indexCount = rs.getInt(5);
                    if (isSupportUserSchema) {
                        tableName = rs.getString(6) + "." + tableName;
                    }

                    tableInfo.setTableName(tableName);
                    tableInfo.setUkCount(ukCount);
                    tableInfo.setPkCount(pkCount);
                    tableInfo.setFkCount(fkCount);
                    tableInfo.setIndexCount(indexCount);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            } finally {
                QueryUtil.freeQuery(pStmt, rs);
            }
        }
        try {
            Map<String, SchemaComment> commentMap =
                    SchemaCommentHandler.loadTableDescriptions(
                            database.getDatabaseInfo(),
                            connection,
                            database.getDatabaseInfo().isSupportUserSchema());
            if (commentMap != null && tableInfo != null) {
                SchemaComment schemaComment =
                        SchemaCommentHandler.find(commentMap, tableInfo.getTableName(), null);
                if (schemaComment != null) {
                    tableInfo.setTableDesc(schemaComment.getDescription());
                }
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        finish();
    }

    public TableDetailInfo getTableInfo() {
        return tableInfo;
    }
}
