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
package com.cubrid.common.ui.spi.util;

import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.query.format.SqlFormattingStrategy;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.DbAuth;
import com.cubrid.cubridmanager.core.cubrid.table.model.SchemaDDL;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetAllAttrTask;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetDbAuthTask;
import java.util.List;
import org.slf4j.Logger;

/**
 * SQLGenerateUtils to generate SQLs like select,create,insert,update,delete and etc.
 *
 * @author Kevin Cao
 * @version 1.0 - 2011-3-1 created by Kevin Cao
 */
public final class SQLGenerateUtils {
    private static final Logger LOGGER = LogUtil.getLogger(SQLGenerateUtils.class);

    private SQLGenerateUtils() {
        // do nothing.
    }

    /**
     * Get create table SQL
     *
     * @param schemaNode DefaultSchemaNode
     * @return String
     */
    public static String getCreateSQL(DefaultSchemaNode schemaNode) {
        if (schemaNode == null || schemaNode.getDatabase() == null) {
            return null;
        }
        return getCreateSQL(schemaNode.getDatabase().getDatabaseInfo(), schemaNode.getName());
    }

    /**
     * Get create table SQL
     *
     * @param schemaNode DefaultSchemaNode
     * @return String
     */
    public static String getCreateSQL(DatabaseInfo databaseInfo, String tableName) {
        if (databaseInfo == null || tableName == null) {
            return "";
        }
        SchemaDDL schemaDDL = new SchemaDDL(null, databaseInfo);
        SchemaInfo schemaInfo = databaseInfo.getSchemaInfo(tableName);
        if (schemaInfo == null) {
            //			CommonUITool.openErrorBox(Messages.bind(Messages.errGetSchemaInfo, tableName));
            //			LOGGER.debug("Can't get the SchemaInfo:" + tableName);
            return "";
        }
        String sql = schemaDDL.getSchemaDDL(schemaInfo) + StringUtil.NEWLINE;
        return sql;
    }

    /**
     * Create select statement SQL
     *
     * @param schemaNode DefaultSchemaNode
     * @return String
     */
    public static String getDeleteSQL(DefaultSchemaNode schemaNode) {
        StringBuffer sql = new StringBuffer();
        if (schemaNode != null) {
            CubridDatabase db = schemaNode.getDatabase();
            DatabaseInfo dbInfo = db.getDatabaseInfo();
            GetAllAttrTask task = new GetAllAttrTask(dbInfo);
            task.setClassName(schemaNode.getName());
            task.getAttrList();
            if (task.getErrorMsg() != null) {
                return "";
            }
            List<DBAttribute> allAttrList = task.getAllAttrList();
            sql.append("DELETE FROM ")
                    .append(QuerySyntax.escapeKeyword(schemaNode.getName()))
                    .append(" \r\n	WHERE ");
            for (DBAttribute attr : allAttrList) {
                sql.append(" ")
                        .append(QuerySyntax.escapeKeyword(attr.getName()))
                        .append(" = ? AND");
            }
            sql = new StringBuffer(sql.substring(0, sql.length() - 3)).append(';');
        }
        return sql.toString();
    }

    /**
     * Get statement SQL
     *
     * @param schemaNode DefaultSchemaNode
     * @return String
     * @see
     *     com.cubrid.common.ui.cubrid.table.action.CopyToClipboardAction#createStmtSQL(com.cubrid.common.ui.spi.model.DefaultSchemaNode)
     */
    public static String getGrantSQL(DefaultSchemaNode schemaNode) {
        CubridDatabase database = schemaNode.getDatabase();
        String tableName = schemaNode.getName();
        SchemaInfo schemaInfo = database.getDatabaseInfo().getSchemaInfo(tableName);
        if (schemaInfo == null) {
            CommonUITool.openErrorBox(Messages.bind(Messages.errGetSchemaInfo, tableName));
            LOGGER.debug("Can't get the SchemaInfo:" + tableName);
            return "";
        }

        if (schemaInfo.getOwner() == null) {
            return "";
        }
        String owner = schemaInfo.getOwner();

        GetDbAuthTask getDbAuthTask = new GetDbAuthTask(database.getDatabaseInfo());
        List<DbAuth> dbAuths = getDbAuthTask.getDbAuths(tableName);
        StringBuffer sql = new StringBuffer("");
        sql.append("/* Called only by DBA or members of DBA group */");
        sql.append(StringUtil.NEWLINE);
        sql.append("CALL change_owner('");
        sql.append(tableName);
        sql.append("', '");
        sql.append(owner);
        sql.append("') ON CLASS db_authorizations;");
        sql.append(StringUtil.NEWLINE);
        if (!dbAuths.isEmpty()) {
            sql.append("/* May Called all user who have the related grant authority */");
            sql.append(StringUtil.NEWLINE);
            for (DbAuth dbAuth : dbAuths) {
                sql.append("GRANT ");
                sql.append(dbAuth.getAuthType());
                sql.append(" ON ");
                sql.append(QuerySyntax.escapeKeyword(dbAuth.getClassName()));
                sql.append(" TO ");
                sql.append(QuerySyntax.escapeKeyword(dbAuth.getGranteeName()));
                if (dbAuth.isGrantable()) {
                    sql.append(" WITH GRANT OPTION");
                }
                sql.append(";");
                sql.append(StringUtil.NEWLINE);
            }
        }
        return sql.toString();
    }

    /**
     * Create insert prepared statement SQL
     *
     * @param schemaNode DefaultSchemaNode
     * @return String
     */
    public static String getInsertSQL(DefaultSchemaNode schemaNode) {
        StringBuffer columns = new StringBuffer("");
        StringBuffer values = new StringBuffer("");

        CubridDatabase database = schemaNode.getDatabase();
        String tableName = schemaNode.getName();
        SchemaInfo schemaInfo = database.getDatabaseInfo().getSchemaInfo(tableName);

        if (schemaInfo == null) {
            CommonUITool.openErrorBox(Messages.bind(Messages.errGetSchemaInfo, tableName));
            LOGGER.debug("Can't get the SchemaInfo:" + tableName);
            return "";
        }

        int n = schemaInfo.getAttributes().size();
        for (int i = 0; i < n; i++) {
            DBAttribute da = (DBAttribute) schemaInfo.getAttributes().get(i);
            if (values.length() > 0) {
                columns.append(", ");
                values.append(", ");
            }
            columns.append(QuerySyntax.escapeKeyword(da.getName()));
            values.append("?");
        }

        StringBuffer sql = new StringBuffer("");
        if (columns.length() > 0) {
            sql.append("INSERT INTO ");
            sql.append(QuerySyntax.escapeKeyword(tableName));
            sql.append(" (");
            sql.append(columns);
            sql.append(") ").append(StringUtil.NEWLINE).append("	VALUES (");
            sql.append(values);
            sql.append(");");
        }
        return sql.toString();
    }

    /**
     * Create select prepared statement SQL
     *
     * @param schemaNode DefaultSchemaNode
     * @return String
     */
    public static String getSelectSQLSimple(DefaultSchemaNode schemaNode) {
        DatabaseInfo databaseInfo = NodeUtil.findDatabaseInfo(schemaNode);
        if (databaseInfo == null) {
            return "";
        }

        StringBuffer columns = new StringBuffer("");
        StringBuffer wheres = new StringBuffer("");

        String tableName = schemaNode.getName();
        SchemaInfo schemaInfo = databaseInfo.getSchemaInfo(tableName);
        if (schemaInfo == null) {
            CommonUITool.openErrorBox(Messages.bind(Messages.errGetSchemaInfo, tableName));
            LOGGER.debug("Can't get the SchemaInfo:" + tableName);
            return "";
        }

        int n = schemaInfo.getAttributes().size();
        for (int i = 0; i < n; i++) {
            DBAttribute da = (DBAttribute) schemaInfo.getAttributes().get(i);
            if (columns.length() > 0) {
                columns.append(", ");
                wheres.append(" AND ");
            }
            columns.append(QuerySyntax.escapeKeyword(da.getName()));
            wheres.append(QuerySyntax.escapeKeyword(da.getName()) + "=?");
        }
        StringBuffer sql = new StringBuffer("");
        if (columns.length() > 0) {
            sql.append("SELECT ");
            sql.append(columns);
            sql.append(StringUtil.NEWLINE).append("	FROM ");
            sql.append(StringUtil.NEWLINE).append(QuerySyntax.escapeKeyword(tableName));
            sql.append(StringUtil.NEWLINE).append("	WHERE ");
            sql.append(StringUtil.NEWLINE).append("	1 = 1;");
        }
        return sql.toString();
    }

    /**
     * Get the select sql no where
     *
     * @param name
     * @param allAttrList
     * @return
     */
    public static String getSelectSQLNoWhere(
            String name, List<DBAttribute> allAttrList, boolean useSemicolon) {
        if (name == null || name.length() == 0 || allAttrList == null) {
            return "";
        }

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");

        StringBuilder columns = new StringBuilder();
        for (DBAttribute attr : allAttrList) {
            //			if (attr.isClassAttribute()) {
            //				//sql.append(" class \"" + attr.getName() + "\" ,");
            //			} else {
            if (columns.length() > 0) {
                columns.append(", ");
            }
            columns.append(QuerySyntax.escapeKeyword(attr.getName()));
            //			}
        }

        if (columns.length() == 0) {
            sql.append("*");
        } else {
            sql.append(columns);
        }

        sql.append(" FROM ").append(QuerySyntax.escapeKeyword(name));
        if (useSemicolon) {
            sql.append(";");
        }

        String res = sql.toString();
        try {
            SqlFormattingStrategy formatter = new SqlFormattingStrategy();
            return formatter.format(res);
        } catch (Exception ignored) {
            return res;
        }
    }

    /**
     * Get the select sql with limit 1 to 100
     *
     * @param name
     * @param allAttrList
     * @return
     */
    public static String getSelectSQLWithLimit(String name, List<DBAttribute> allAttrList) {
        if (name == null || name.length() == 0 || allAttrList == null) {
            return "";
        }

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");

        StringBuilder columns = new StringBuilder();
        for (DBAttribute attr : allAttrList) {
            //			if (attr.isClassAttribute()) {
            //				//sql.append(" class \"" + attr.getName() + "\" ,");
            //			} else {
            if (columns.length() > 0) {
                columns.append(", ");
            }
            columns.append(QuerySyntax.escapeKeyword(attr.getName()));
            //			}
        }

        if (columns.length() == 0) {
            sql.append("*");
        } else {
            sql.append(columns);
        }

        sql.append(" FROM ").append(QuerySyntax.escapeKeyword(name));
        sql.append(" WHERE ROWNUM BETWEEN 1 AND 100;");

        String res = sql.toString();
        try {
            SqlFormattingStrategy formatter = new SqlFormattingStrategy();
            return formatter.format(res);
        } catch (Exception ignored) {
            return res;
        }
    }

    /**
     * Get the select sql with assigned limit
     *
     * @param name
     * @param allAttrList
     * @return
     */
    public static String getSelectSQLWithLimit(String name, int start, int end) {
        if (name == null || name.length() == 0) {
            return "";
        }

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * ");
        sql.append("FROM ").append(QuerySyntax.escapeKeyword(name));
        if (start > 0 && end > 0 && start <= end) {
            sql.append(" WHERE ROWNUM BETWEEN ").append(start).append(" AND ").append(end);
        }
        sql.append(";");

        String res = sql.toString();
        try {
            SqlFormattingStrategy formatter = new SqlFormattingStrategy();
            return formatter.format(res);
        } catch (Exception ignored) {
            return res;
        }
    }

    /**
     * Create select prepared statement SQL
     *
     * @param schemaNode DefaultSchemaNode
     * @return String
     */
    public static String getSelectSQL(DefaultSchemaNode schemaNode) {

        StringBuilder columns = new StringBuilder();
        StringBuilder wheres = new StringBuilder();

        CubridDatabase database = schemaNode.getDatabase();
        String tableName = schemaNode.getName();
        SchemaInfo schemaInfo = database.getDatabaseInfo().getSchemaInfo(tableName);

        if (schemaInfo == null) {
            CommonUITool.openErrorBox(Messages.bind(Messages.errGetSchemaInfo, tableName));
            LOGGER.debug("Can't get the SchemaInfo:" + tableName);
            return "";
        }

        int n = schemaInfo == null ? 0 : schemaInfo.getAttributes().size();
        for (int i = 0; i < n; i++) {
            DBAttribute da = (DBAttribute) schemaInfo.getAttributes().get(i);
            if (columns.length() > 0) {
                columns.append(", ");
                wheres.append(" AND ");
            }
            columns.append(QuerySyntax.escapeKeyword(da.getName()));
            wheres.append(QuerySyntax.escapeKeyword(da.getName())).append(" = ?");
        }
        StringBuffer sql = new StringBuffer("");
        if (columns.length() > 0) {
            sql.append("SELECT ");
            sql.append(columns);
            sql.append(StringUtil.NEWLINE).append("	FROM ");
            sql.append(QuerySyntax.escapeKeyword(tableName));
            sql.append(StringUtil.NEWLINE).append("	WHERE ");
            sql.append(wheres + ";");
        }
        return sql.toString();
    }

    /**
     * Create update statement SQL
     *
     * @param schemaNode DefaultSchemaNode
     * @return String
     */
    public static String getUpdateSQL(DefaultSchemaNode schemaNode) {
        StringBuilder sql = new StringBuilder();
        if (schemaNode != null) {
            CubridDatabase db = schemaNode.getDatabase();
            DatabaseInfo dbInfo = db.getDatabaseInfo();
            GetAllAttrTask task = new GetAllAttrTask(dbInfo);
            task.setClassName(schemaNode.getName());
            task.getAttrList();
            if (task.getErrorMsg() != null) {
                return "";
            }
            List<DBAttribute> allAttrList = task.getAllAttrList();
            if (allAttrList == null || allAttrList.size() == 0) {
                return "";
            }

            sql.append("UPDATE ")
                    .append(QuerySyntax.escapeKeyword(schemaNode.getName()))
                    .append(StringUtil.NEWLINE)
                    .append(" SET ")
                    .append(StringUtil.NEWLINE);
            for (int i = 0; i < allAttrList.size(); i++) {
                DBAttribute attr = allAttrList.get(i);
                sql.append(" ").append(QuerySyntax.escapeKeyword(attr.getName())).append(" = ?");
                if (i + 1 < allAttrList.size()) {
                    sql.append(", ");
                }
                sql.append(StringUtil.NEWLINE);
            }
            sql.append("WHERE ");
            for (int i = 0; i < allAttrList.size(); i++) {
                DBAttribute attr = allAttrList.get(i);
                sql.append(" ").append(QuerySyntax.escapeKeyword(attr.getName())).append(" = ? ");
                if (i + 1 < allAttrList.size()) {
                    sql.append(StringUtil.NEWLINE).append("AND");
                }
            }
        }

        return sql.toString();
    }

    public static String generateCloneTableSql(DefaultSchemaNode schemaNode, String newName) {
        CubridDatabase database = schemaNode.getDatabase();
        String tableName = schemaNode.getName();
        SchemaInfo schemaInfo = database.getDatabaseInfo().getSchemaInfo(tableName);

        if (schemaInfo == null) {
            CommonUITool.openErrorBox(Messages.bind(Messages.errGetSchemaInfo, tableName));
            LOGGER.debug("Can't get the SchemaInfo:" + tableName);
            return "";
        }

        int columnCounts = schemaInfo == null ? 0 : schemaInfo.getAttributes().size();
        StringBuilder columns = new StringBuilder();
        if (columnCounts > 0) {
            for (int i = 0; i < columnCounts; i++) {
                DBAttribute da = (DBAttribute) schemaInfo.getAttributes().get(i);
                if (columns.length() > 0) {
                    columns.append(", ");
                }
                columns.append(QuerySyntax.escapeKeyword(da.getName()));
            }
        } else {
            columns.append("*");
        }

        StringBuffer sql = new StringBuffer();
        sql.append("CREATE TABLE ");
        sql.append(QuerySyntax.escapeKeyword(newName));
        sql.append(" AS ").append(StringUtil.NEWLINE).append("SELECT ");
        sql.append(columns);
        sql.append(StringUtil.NEWLINE).append("FROM ");
        sql.append(QuerySyntax.escapeKeyword(tableName));
        sql.append(";");

        return format(sql.toString());
    }

    public static String format(String sourceSql) {
        try {
            SqlFormattingStrategy formatter = new SqlFormattingStrategy();
            return formatter.format(sourceSql);
        } catch (Exception ignored) {
            return sourceSql;
        }
    }
}
