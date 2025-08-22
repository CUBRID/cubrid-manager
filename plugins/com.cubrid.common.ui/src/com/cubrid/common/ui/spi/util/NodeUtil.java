/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.spi.util;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import org.slf4j.Logger;

public class NodeUtil {
    private static final Logger LOGGER = LogUtil.getLogger(NodeUtil.class);

    public static boolean isCubridNode(Object node) {
        return (node instanceof ICubridNode);
    }

    public static boolean isNotCubridNode(Object node) {
        return !(node instanceof ICubridNode);
    }

    public static boolean isTableViewNode(ICubridNode node) {
        return NodeType.USER_TABLE.equals(node.getType())
                || NodeType.SYSTEM_TABLE.equals(node.getType())
                || NodeType.USER_VIEW.equals(node.getType())
                || NodeType.SYSTEM_VIEW.equals(node.getType());
    }

    public static boolean isTableFolderNode(ICubridNode node) {
        return NodeType.TABLE_FOLDER.equals(node.getType());
    }

    public static boolean isViewFolderNode(ICubridNode node) {
        return NodeType.VIEW_FOLDER.equals(node.getType());
    }

    public static boolean isSerialFolderNode(ICubridNode node) {
        return NodeType.SERIAL_FOLDER.equals(node.getType());
    }

    public static boolean isTriggerFolderNode(ICubridNode node) {
        return NodeType.TRIGGER_FOLDER.equals(node.getType());
    }

    public static boolean isSynonymFolderNode(ICubridNode node) {
        return NodeType.SYNONYM_FOLDER.equals(node.getType());
    }

    public static boolean isUserFolderNode(ICubridNode node) {
        return NodeType.USER_FOLDER.equals(node.getType());
    }

    public static CubridDatabase getCubridDatabase(ICubridNode node) {
        if (node == null) {
            return null;
        }

        CubridDatabase database = (CubridDatabase) node.getParent();
        return database;
    }

    /**
     * Get java connection url
     *
     * @param databaseInfo
     * @return
     */
    public static String getJavaConnectionUrl(DatabaseInfo databaseInfo) {
        if (databaseInfo == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("jdbc:cubrid:");

        appendConnectionUrl(databaseInfo, sb);

        return sb.toString();
    }

    /**
     * Get php connection url
     *
     * @param databaseInfo
     * @return
     */
    public static String getPHPConnectionUrl(DatabaseInfo databaseInfo) {
        if (databaseInfo == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("cci:cubrid:");

        appendConnectionUrl(databaseInfo, sb);

        return sb.toString();
    }

    /**
     * Append connection url and connection configuration
     *
     * @param databaseInfo
     * @param sb
     */
    private static void appendConnectionUrl(DatabaseInfo databaseInfo, StringBuilder sb) {
        sb.append(databaseInfo.getBrokerIP()).append(":");
        sb.append(databaseInfo.getBrokerPort()).append(":");
        sb.append(databaseInfo.getDbName()).append(":");

        // User
        sb.append("").append(":");
        // Passwords
        sb.append("").append(":");

        String charset = databaseInfo.getCharSet();
        String jdbcAttr = databaseInfo.getJdbcAttrs();

        if ((charset != null && charset.length() > 0)
                || (jdbcAttr != null && jdbcAttr.length() > 0)) {
            if (charset != null && charset.length() > 0) {
                sb.append("?charset=").append(charset);
                if (jdbcAttr != null && jdbcAttr.length() > 0) {
                    sb.append("&").append(jdbcAttr);
                }
            } else if (jdbcAttr != null && jdbcAttr.length() > 0) {
                sb.append("?").append(jdbcAttr);
            }
        }
    }

    public static boolean validNode(ISchemaNode schemaNode) {
        if (schemaNode == null) {
            LOGGER.error("The schemaNode is a null.");
            return false;
        }

        CubridDatabase cubridDatabase = schemaNode.getDatabase();
        if (cubridDatabase == null) {
            LOGGER.error("The cubridDatabase is a null.");
            return false;
        }

        final DatabaseInfo databaseInfo = cubridDatabase.getDatabaseInfo();
        if (databaseInfo == null) {
            LOGGER.error("The databaseInfo is a null.");
            return false;
        }

        return true;
    }

    public static DatabaseInfo findDatabaseInfo(ISchemaNode schemaNode) {
        if (schemaNode == null) {
            LOGGER.error("The schemaNode is a null.");
            return null;
        }

        CubridDatabase cubridDatabase = schemaNode.getDatabase();
        if (cubridDatabase == null) {
            LOGGER.error("The cubridDatabase is a null.");
            return null;
        }

        final DatabaseInfo databaseInfo = cubridDatabase.getDatabaseInfo();
        if (databaseInfo == null) {
            LOGGER.error("The databaseInfo is a null.");
            return null;
        }

        return databaseInfo;
    }

    public static DatabaseInfo findDatabaseInfo(CubridDatabase cubridDatabase) {
        if (cubridDatabase == null) {
            LOGGER.error("The cubridDatabase is a null.");
            return null;
        }

        final DatabaseInfo databaseInfo = cubridDatabase.getDatabaseInfo();
        if (databaseInfo == null) {
            LOGGER.error("The databaseInfo is a null.");
            return null;
        }

        return databaseInfo;
    }
}
