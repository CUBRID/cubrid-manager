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
package com.cubrid.cubridmanager.core.utils;

import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import com.cubrid.cubridmanager.core.cubrid.table.model.TableColumn;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Schema Util
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-3-12 created by Kevin.Wang
 */
public class SchemaUtil {

    public static List<TableColumn> getTableColumn(
            DatabaseInfo databaseInfo, Connection connection, String tableName)
            throws SQLException {

        List<TableColumn> columns = new ArrayList<TableColumn>();
        if (connection == null || connection.isClosed()) {
            return columns;
        }

        ResultSet rs = null;
        DatabaseMetaData dbmd = connection.getMetaData();

        try {
            rs = dbmd.getColumns(null, null, tableName, null);

            while (rs.next()) {
                TableColumn dbColumn = new TableColumn();
                dbColumn.setColumnName(rs.getString("column_name")); // $NON-NLS-1$	
                String type = rs.getString("type_name");
                int size = rs.getInt("column_size");
                if (DataType.isNotSupportSizeOrPrecision(type)) {
                    size = 0;
                }
                dbColumn.setTypeName(type); // $NON-NLS-1$
                dbColumn.setPrecision(size); // $NON-NLS-1$
                dbColumn.setScale(rs.getInt("decimal_digits")); // $NON-NLS-1$
                dbColumn.setOrdinalPosition(rs.getInt("ordinal_position")); // $NON-NLS-1$
                columns.add(dbColumn);
            }
        } catch (SQLException ex) {
            /*For bug TOOLS-2818*/
            PreparedStatement pstm = null;

            String sql =
                    "SELECT attr_name,class_name,attr_type,def_order,from_class_name,"
                            + "from_attr_name,data_type,prec,scale,domain_class_name,default_value,is_nullable FROM db_attribute "
                            + "WHERE class_name = ? ORDER BY def_order";
            sql = databaseInfo.wrapShardQuery(sql);

            try {
                pstm = connection.prepareStatement(sql);
                pstm.setString(1, tableName);

                rs = pstm.executeQuery();
                while (rs.next()) {
                    TableColumn dbColumn = new TableColumn();
                    dbColumn.setColumnName(rs.getString("attr_name"));
                    String type = rs.getString("data_type");
                    int size = rs.getInt("prec");
                    if (DataType.isNotSupportSizeOrPrecision(type)) {
                        size = 0;
                    }
                    dbColumn.setTypeName(type); // $NON-NLS-1$
                    dbColumn.setPrecision(size); // $NON-NLS-1$
                    dbColumn.setScale(rs.getInt("scale")); // $NON-NLS-1$
                    dbColumn.setOrdinalPosition(rs.getInt("def_order")); // $NON-NLS-1$
                    columns.add(dbColumn);
                }
            } finally {
                QueryUtil.freeQuery(pstm);
            }
        } finally {
            QueryUtil.freeQuery(rs);
        }

        return columns;
    }

    public static String getOwnerNameForUserSchema(String uniqueName) {
        int idx = uniqueName.indexOf(".");
        if (idx > 0) {
            return uniqueName.substring(0, idx);
        }
        return "";
    }

    public static String getClassNameForUserSchema(String uniqueName) {
        int idx = uniqueName.indexOf(".");
        if (idx > 0) {
            return uniqueName.substring(idx + 1);
        }
        return "";
    }
}
