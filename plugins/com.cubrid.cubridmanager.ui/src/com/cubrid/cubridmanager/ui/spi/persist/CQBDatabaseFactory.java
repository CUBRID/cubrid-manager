/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.cubridmanager.ui.spi.persist;

import com.cubrid.common.configuration.jdbc.IJDBCConnecInfo;
import com.cubrid.common.core.util.DateUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridNodeLoader;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNodeLoader;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.persist.CubridJdbcManager;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.cubridmanager.ui.spi.model.loader.CQBDbConnectionLoader;
import com.cubrid.jdbc.proxy.manage.JdbcClassLoaderFactory;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * The CubridDatabaseFactory Description : CubridDatabaseFactory Author :Kevin.Wang Create date :
 * 2014-2-26
 */
public class CQBDatabaseFactory {

    /**
     * Create database object
     *
     * @param connInfo
     * @return
     */
    public static CubridDatabase createDatabase(IJDBCConnecInfo connInfo) {
        if (connInfo == null || StringUtil.isEmpty(connInfo.getConName())) {
            return null;
        }
        /*Create a new object*/
        String connName = getUniqueName(connInfo.getConName());
        String dbId = connName + ICubridNodeLoader.NODE_SEPARATOR + connName;

        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setServerName(connName);
        serverInfo.setHostAddress(connInfo.getHost());
        serverInfo.setUserName(connInfo.getDbName() + "@" + connInfo.getHost());
        if (!StringUtil.isEmpty(connInfo.getVersion())) {
            serverInfo.setJdbcDriverVersion(connInfo.getVersion());
        } else if (!StringUtil.isEmpty(connInfo.getDriverFileName())) {
            serverInfo.setServerVersion(
                    CubridJdbcManager.getInstance()
                            .getDriverFileByVersion(connInfo.getDriverFileName()));
        } else {
            serverInfo.setServerVersion("");
        }

        CubridServer server = new CubridServer(connName, connName, null, null);
        server.setServerInfo(serverInfo);
        server.setType(NodeType.SERVER);

        DatabaseInfo dbInfo = new DatabaseInfo(connInfo.getDbName(), serverInfo);
        dbInfo.setBrokerIP(connInfo.getHost());
        dbInfo.setBrokerPort(String.valueOf(connInfo.getPort()));
        dbInfo.setCharSet(connInfo.getCharset());
        dbInfo.setJdbcAttrs(connInfo.getJDBCAttrs());

        DbUserInfo userInfo = new DbUserInfo();
        userInfo.setDbName(connInfo.getDbName());
        userInfo.setName(connInfo.getConUser());
        userInfo.setNoEncryptPassword(connInfo.getConPassword());

        dbInfo.setAuthLoginedDbUserInfo(userInfo);

        CubridDatabase database = new CubridDatabase(dbId, connName);

        database.setDatabaseInfo(dbInfo);
        database.setServer(server);
        database.setStartAndLoginIconPath("icons/navigator/database_start_connected.png");
        database.setStartAndLogoutIconPath("icons/navigator/database_start_disconnected.png");

        CubridNodeLoader loader = new CQBDbConnectionLoader();
        loader.setLevel(ICubridNodeLoader.FIRST_LEVEL);
        database.setLoader(loader);

        return database;
    }

    /**
     * Get the unique name
     *
     * @param connName
     * @return
     */
    private static String getUniqueName(String connName) {
        String tempName = connName;
        String tempId = tempName + ICubridNodeLoader.NODE_SEPARATOR + tempName;
        while (CQBDBNodePersistManager.getInstance().getDatabase(tempId) != null) {
            tempName =
                    connName
                            + "["
                            + (DateUtil.getDatetimeString(new Date(), DateUtil.TIME_FORMAT))
                            + "]";
            tempId = tempName + ICubridNodeLoader.NODE_SEPARATOR + tempName;
        }
        return tempName;
    }

    /**
     * Modify the database by the jdbcconnectinfo s
     *
     * @param oldCon
     * @param newCon
     * @return
     */
    public static CubridDatabase modifyDatabaseByJDBCConnectInfo(
            IJDBCConnecInfo oldCon, IJDBCConnecInfo newCon) {
        if (oldCon == null
                || StringUtil.isEmpty(oldCon.getConName())
                || newCon == null
                || StringUtil.isEmpty(newCon.getConName())) {
            return null;
        }

        String connName = newCon.getConName();
        if (!StringUtil.isEqual(oldCon.getConName(), newCon.getConName())) {
            connName = getUniqueName(newCon.getConName());
        }

        CubridDatabase database = CQBDatabaseFactory.getDatabaseJDBCConnectInfo(oldCon);
        if (database != null) {
            String newDBId = connName + ICubridNodeLoader.NODE_SEPARATOR + connName;
            database.setId(newDBId);
            database.setLabel(connName);
            if (!StringUtil.isEqualNotIgnoreNull(oldCon.getConName(), connName)) {
                if (database.getServer() != null) {
                    database.getServer().setId(connName);
                    database.getServer().setLabel(connName);
                }
            }
            if (database.getServer().getServerInfo() != null) {
                database.getServer().getServerInfo().setServerName(connName);
                database.getServer().getServerInfo().setHostAddress(newCon.getHost());
                database.getServer()
                        .getServerInfo()
                        .setUserName(newCon.getDbName() + "@" + newCon.getHost());
                if (StringUtil.isNotEmpty(newCon.getVersion())) {
                    database.getServer().getServerInfo().setJdbcDriverVersion(newCon.getVersion());
                } else {
                    try {
                        database.getServer()
                                .getServerInfo()
                                .setJdbcDriverVersion(
                                        JdbcClassLoaderFactory.getJdbcJarVersion(
                                                newCon.getDriverFileName()));
                    } catch (IOException e) {
                        // Ignore
                    }
                }
            }
            DatabaseInfo databaseInfo = database.getDatabaseInfo();
            if (databaseInfo != null) {
                databaseInfo.setBrokerIP(newCon.getHost());
                databaseInfo.setBrokerPort(String.valueOf(newCon.getPort()));
                databaseInfo.setCharSet(newCon.getCharset());
                databaseInfo.setJdbcAttrs(newCon.getJDBCAttrs());
                databaseInfo.setDbName(newCon.getDbName());

                DbUserInfo userInfo = databaseInfo.getAuthLoginedDbUserInfo();
                if (userInfo == null) {
                    userInfo = new DbUserInfo();
                }
                userInfo.setDbName(newCon.getDbName());
                userInfo.setName(newCon.getConUser());
                userInfo.setNoEncryptPassword(newCon.getConPassword());
                databaseInfo.setAuthLoginedDbUserInfo(userInfo);
            }
        }

        return database;
    }

    /**
     * Get the database by jdbc connectinfo
     *
     * @param connInfo
     * @return
     */
    public static CubridDatabase getDatabaseJDBCConnectInfo(
            IJDBCConnecInfo connInfo) { // FIXME extract
        if (connInfo == null || StringUtil.isEmpty(connInfo.getConName())) {
            return null;
        }

        String dbId =
                connInfo.getConName() + ICubridNodeLoader.NODE_SEPARATOR + connInfo.getConName();
        // Find by connection name
        CubridDatabase database = CQBDBNodePersistManager.getInstance().getDatabase(dbId);
        if (database != null) {
            return database;
        }

        // Find by dbName, address and port
        List<CubridDatabase> databaseList = CQBDBNodePersistManager.getInstance().getAllDatabase();
        for (CubridDatabase db : databaseList) {
            if (db != null
                    && db.getDatabaseInfo() != null
                    && StringUtil.isEqual(connInfo.getHost(), db.getDatabaseInfo().getBrokerIP())
                    && StringUtil.isEqual(
                            String.valueOf(connInfo.getPort()),
                            db.getDatabaseInfo().getBrokerPort())
                    && StringUtil.isEqual(connInfo.getDbName(), db.getDatabaseInfo().getDbName())) {

                return db;
            }
        }

        return database;
    }
}
