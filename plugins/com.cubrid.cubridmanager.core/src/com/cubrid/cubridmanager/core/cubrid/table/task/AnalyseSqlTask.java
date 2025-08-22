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

import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.cubridmanager.core.Messages;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;

/**
 * This task <code>AnalyseSqlTask</code>is responsible to analyse the select SQL and get the column
 * name and column type
 *
 * @author robin
 * @version 1.0 - 2009-5-20 created by robin
 */
public class AnalyseSqlTask extends JDBCTask {
    private static final Logger LOGGER = LogUtil.getLogger(AnalyseSqlTask.class);
    private List<String> sqls = null;
    private final List<Map<String, String>> result;
    private PreparedStatement pStmt; // NOPMD

    public AnalyseSqlTask(DatabaseInfo dbInfo) {
        super("AnalyseSqlTask", dbInfo);
        result = new ArrayList<Map<String, String>>();
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
            connection.setAutoCommit(false);
            if (sqls == null) {
                return;
            }
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < sqls.size(); i++) {
                sb.append(sqls.get(i));
                if (i != sqls.size() - 1) {
                    sb.append(" UNION ALL ");
                }
            }
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sb.toString());

            CUBRIDResultSetMetaDataProxy resultSetMeta =
                    (CUBRIDResultSetMetaDataProxy) rs.getMetaData();

            for (int i = 1; i < resultSetMeta.getColumnCount() + 1; i++) {
                // "Name", "Data type", "Shared", "Default"
                Map<String, String> map = new HashMap<String, String>();
                String type = resultSetMeta.getColumnTypeName(i);
                int presion = resultSetMeta.getPrecision(i);
                int scale = resultSetMeta.getScale(i);
                if (type != null && type.equalsIgnoreCase("CLASS")) {
                    String tableName = resultSetMeta.getTableName(i);
                    String colName = resultSetMeta.getColumnName(i);
                    DBAttribute bean = getColAttr(tableName, colName);

                    if (bean == null
                            || bean.getDomainClassName() == null
                            || bean.getDomainClassName().equals("")) {
                        type = "OBJECT";
                    } else {
                        type = bean.getDomainClassName();
                    }
                } else if (type != null
                        && (type.equalsIgnoreCase("CHAR")
                                || type.equalsIgnoreCase("VARCHAR")
                                || type.equalsIgnoreCase("NCHAR")
                                || type.equalsIgnoreCase("BIT")
                                || type.toUpperCase(Locale.getDefault()).indexOf("VARYING") >= 0)) {
                    type += "(" + presion + ")";
                } else if (type != null && type.equalsIgnoreCase("NUMERIC")) {
                    type += "(" + presion + "," + scale + ")";
                }

                map.put("0", resultSetMeta.getColumnName(i));
                map.put("1", type);
                map.put("2", "");
                map.put("3", "");
                map.put("4", "");
                result.add(map);
            }
        } catch (SQLException e) {
            this.errorMsg = e.getMessage();
        } finally {
            finish();
        }
    }

    /**
     * Add sqls
     *
     * @param sql String
     */
    public void addSqls(String sql) {
        if (sqls == null) {
            sqls = new ArrayList<String>();
        }
        this.sqls.add(sql);
    }

    public List<Map<String, String>> getResult() {
        return result;
    }

    /**
     * Get column attribute
     *
     * @param className String
     * @param colName String
     * @return DBAttribute
     */
    private DBAttribute getColAttr(String className, String colName) {
        DBAttribute dbAttribute = null;
        pStmt = null;
        ResultSet rs = null;
        if (errorMsg != null && errorMsg.trim().length() > 0) {
            return dbAttribute;
        }

        try {
            if (connection == null || connection.isClosed()) {
                errorMsg = Messages.error_getConnection;
                return dbAttribute;
            }

            String sql =
                    "SELECT attr_name, class_name, attr_type, def_order,"
                            + " from_class_name, from_attr_name, data_type, prec, scale,"
                            + " domain_class_name, default_value, is_nullable"
                            + " FROM db_attribute"
                            + " WHERE class_name=? AND attr_name=?";

            // [TOOLS-2425]Support shard broker
            sql = DatabaseInfo.wrapShardQuery(databaseInfo, sql);

            pStmt = connection.prepareStatement(sql);
            pStmt.setString(1, className);
            pStmt.setString(2, colName);
            rs = pStmt.executeQuery();
            if (rs.next()) {
                dbAttribute = new DBAttribute();
                dbAttribute.setName(rs.getString("attr_name"));
                dbAttribute.setType(rs.getString("data_type"));
                String attrType = rs.getString("attr_type");
                String domainClassName = rs.getString("domain_class_name");
                if (attrType.equalsIgnoreCase("SHARED")) {
                    dbAttribute.setShared(true);
                } else {
                    dbAttribute.setShared(false);
                }
                dbAttribute.setDomainClassName(domainClassName);
                dbAttribute.setDefault(rs.getString("default_value"));
                String isNull = rs.getString("is_nullable");
                if (isNull.equalsIgnoreCase("YES")) {
                    dbAttribute.setNotNull(false);
                } else {
                    dbAttribute.setNotNull(true);
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            QueryUtil.freeQuery(pStmt, rs);
        }

        return dbAttribute;
    }
}
