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
package com.cubrid.cubridmanager.ui.spi.model.loader;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridNodeLoader;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ICubridNodeLoader;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfo;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfoList;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfos;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.common.task.GetCMUserListTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.database.task.GetDatabaseListTask;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.cubridmanager.ui.cubrid.database.editor.DatabaseDashboardEditor;
import com.cubrid.cubridmanager.ui.spi.persist.CMDBNodePersistManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This class is responsible to load the children of CUBRID databases folder,these children include
 * all CUBRID database
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class CubridDatabasesFolderLoader extends CubridNodeLoader {

    /**
     * Load children object for parent
     *
     * @param parent the parent node
     * @param monitor the IProgressMonitor object
     */
    public void load(ICubridNode parent, final IProgressMonitor monitor) {
        synchronized (this) {
            if (isLoaded()) {
                return;
            }
            ServerInfo serverInfo = parent.getServer().getServerInfo();
            final GetDatabaseListTask getDatabaseListTask = new GetDatabaseListTask(serverInfo);
            final GetCMUserListTask getUserInfoTask = new GetCMUserListTask(serverInfo);

            monitorCancel(monitor, new ITask[] {getUserInfoTask, getDatabaseListTask});

            getUserInfoTask.execute();
            final String msg1 = getUserInfoTask.getErrorMsg();
            if (!monitor.isCanceled() && msg1 != null && msg1.trim().length() > 0) {
                parent.removeAllChild();
                openErrorBox(msg1);
                setLoaded(true);
                return;
            }
            if (monitor.isCanceled()) {
                setLoaded(true);
                return;
            }
            List<ServerUserInfo> serverUserInfoList = getUserInfoTask.getServerUserInfoList();
            List<DatabaseInfo> oldDatabaseInfoList = null;
            if (serverInfo.getLoginedUserInfo() != null) {
                oldDatabaseInfoList = serverInfo.getLoginedUserInfo().getDatabaseInfoList();
            }
            for (int i = 0; serverUserInfoList != null && i < serverUserInfoList.size(); i++) {
                ServerUserInfo userInfo = serverUserInfoList.get(i);
                if (userInfo != null && userInfo.getUserName().equals(serverInfo.getUserName())) {
                    serverInfo.setLoginedUserInfo(userInfo);
                    break;
                }
            }
            getDatabaseListTask.execute();
            if (monitor.isCanceled()) {
                setLoaded(true);
                return;
            }
            final String msg2 = getDatabaseListTask.getErrorMsg();
            if (!monitor.isCanceled() && msg2 != null && msg2.trim().length() > 0) {
                parent.removeAllChild();
                openErrorBox(msg2);
                setLoaded(true);
                return;
            }
            List<DatabaseInfo> newDatabaseInfoList = getDatabaseListTask.loadDatabaseInfo();
            List<ICubridNode> oldNodeList = new ArrayList<ICubridNode>();
            oldNodeList.addAll(parent.getChildren());
            parent.removeAllChild();

            // Construct the database folder children
            buildDatabasesFolder(
                    parent,
                    monitor,
                    serverInfo,
                    oldDatabaseInfoList,
                    newDatabaseInfoList,
                    oldNodeList);

            Collections.sort(parent.getChildren());
            setLoaded(true);
            CubridNodeManager.getInstance()
                    .fireCubridNodeChanged(
                            new CubridNodeChangedEvent(
                                    (ICubridNode) parent,
                                    CubridNodeChangedEventType.CONTAINER_NODE_REFRESH));
        }
    }

    /**
     * Construct the database folder children
     *
     * @param parent the database folder node
     * @param monitor the IProgressMonitor
     * @param serverInfo the ServerInfo
     * @param oldDatabaseInfoList the old database information list
     * @param newDatabaseInfoList the new database information list
     * @param oldNodeList the old node list
     */
    private void buildDatabasesFolder(
            ICubridNode parent,
            final IProgressMonitor monitor,
            ServerInfo serverInfo,
            List<DatabaseInfo> oldDatabaseInfoList,
            List<DatabaseInfo> newDatabaseInfoList,
            List<ICubridNode> oldNodeList) {
        ServerUserInfo userInfo = serverInfo.getLoginedUserInfo();
        List<DatabaseInfo> authorDatabaseList = userInfo.getDatabaseInfoList();
        if (authorDatabaseList == null) {
            authorDatabaseList = new ArrayList<DatabaseInfo>();
            userInfo.setDatabaseInfoList(authorDatabaseList);
        }
        filterDatabaseList(serverInfo, newDatabaseInfoList, authorDatabaseList);
        for (int i = 0; i < authorDatabaseList.size() && !monitor.isCanceled(); i++) {
            DatabaseInfo databaseInfo = authorDatabaseList.get(i);
            DatabaseInfo newDatabaseInfo =
                    getDatabaseInfo(newDatabaseInfoList, databaseInfo.getDbName());
            if (newDatabaseInfo == null) {
                continue;
            } else {
                databaseInfo.setDbDir(newDatabaseInfo.getDbDir());
                databaseInfo.setRunningType(newDatabaseInfo.getRunningType());
                newDatabaseInfo = getDatabaseInfo(oldDatabaseInfoList, databaseInfo.getDbName());
                if (newDatabaseInfo != null) {
                    DbUserInfo dbUserInfo = newDatabaseInfo.getAuthLoginedDbUserInfo();
                    if (dbUserInfo != null && databaseInfo.getAuthLoginedDbUserInfo() != null) {
                        databaseInfo
                                .getAuthLoginedDbUserInfo()
                                .setNoEncryptPassword(dbUserInfo.getNoEncryptPassword());
                    }
                }
            }
            String name = databaseInfo.getDbName();
            String id = parent.getId() + NODE_SEPARATOR + name;
            ICubridNode databaseNode = isContained(oldNodeList, id);
            if (databaseNode == null) {
                databaseNode = new CubridDatabase(id, databaseInfo.getDbName());
                CubridDatabase database = (CubridDatabase) databaseNode;
                database.setStartAndLoginIconPath("icons/navigator/database_start_connected.png");
                database.setStartAndLogoutIconPath(
                        "icons/navigator/database_start_disconnected.png");
                database.setStopAndLogoutIconPath("icons/navigator/database_stop_disconnected.png");
                database.setStopAndLoginIconPath("icons/navigator/database_stop_connected.png");
                ICubridNodeLoader loader = new CubridDatabaseLoader();
                loader.setLevel(getLevel());
                databaseNode.setLoader(loader);
                parent.addChild(databaseNode);
                if (getLevel() == DEFINITE_LEVEL) {
                    databaseNode.getChildren(monitor);
                }
                databaseNode.setEditorId(DatabaseDashboardEditor.ID);
                ((CubridDatabase) databaseNode).setDatabaseInfo(databaseInfo);
                databaseNode.setContainer(true);
                ((CubridDatabase) databaseNode)
                        .setAutoSavePassword(
                                CMDBNodePersistManager.getInstance()
                                        .getDbSavePassword(
                                                serverInfo.getHostAddress(),
                                                String.valueOf(serverInfo.getHostMonPort()),
                                                databaseInfo.getDbName(),
                                                databaseInfo.getAuthLoginedDbUserInfo().getName()));
                String jdbcAttrs =
                        CMDBNodePersistManager.getInstance()
                                .getJdbcAttrs((CubridDatabase) databaseNode);
                databaseInfo.setJdbcAttrs(jdbcAttrs);
            } else {
                parent.addChild(databaseNode);
                databaseInfo.setLogined(((CubridDatabase) databaseNode).isLogined());
                ((CubridDatabase) databaseNode).setDatabaseInfo(databaseInfo);
                if (databaseNode.getLoader() != null && databaseNode.getLoader().isLoaded()) {
                    databaseNode.getLoader().setLoaded(false);
                    databaseNode.getChildren(monitor);
                }
            }
        }
    }

    /**
     * Return whether contain the node from id
     *
     * @param nodeList the node list
     * @param id the node id
     * @return the ICubridNode object
     */
    private ICubridNode isContained(List<ICubridNode> nodeList, String id) {
        for (int i = 0; nodeList != null && i < nodeList.size(); i++) {
            ICubridNode node = nodeList.get(i);
            if (node.getId().equals(id)) {
                return node;
            }
        }
        return null;
    }

    /**
     * Get database information from dbname
     *
     * @param databaseInfoList the database information list
     * @param dbName the database name
     * @return the DatabaseInfo object
     */
    public static DatabaseInfo getDatabaseInfo(List<DatabaseInfo> databaseInfoList, String dbName) {
        for (int i = 0; databaseInfoList != null && i < databaseInfoList.size(); i++) {
            DatabaseInfo databaseInfo = databaseInfoList.get(i);
            if (databaseInfo.getDbName().equals(dbName)) {
                return databaseInfo;
            }
        }
        return null;
    }

    /**
     * Add or delete database for author database list
     *
     * @param serverInfo the server information
     * @param databaseInfoList the database information list
     * @param authorDatabaseList the authorize database list
     * @return whether this authorDatabaseList is updated
     */
    public static boolean filterDatabaseList(
            ServerInfo serverInfo,
            List<DatabaseInfo> databaseInfoList,
            List<DatabaseInfo> authorDatabaseList) {
        if (databaseInfoList == null || authorDatabaseList == null) {
            return false;
        }
        boolean isUpdated = false;
        // Check the authorization database whether already deleted
        List<DatabaseInfo> deletedDbList = new ArrayList<DatabaseInfo>();
        for (int i = 0; i < authorDatabaseList.size(); i++) {
            DatabaseInfo databaseInfo = authorDatabaseList.get(i);
            if (databaseInfo == null) {
                continue;
            }
            DatabaseInfo dbInfo = getDatabaseInfo(databaseInfoList, databaseInfo.getDbName());
            if (dbInfo == null) {
                deletedDbList.add(databaseInfo);
                isUpdated = true;
            } else {
                databaseInfo.setDbDir(dbInfo.getDbDir());
                databaseInfo.setRunningType(dbInfo.getRunningType());
            }
        }

        List<DatabaseInfo> addedDbList = new ArrayList<DatabaseInfo>();
        boolean isAdminLogin = serverInfo.getLoginedUserInfo().isAdmin();
        // if user is admin,add all database to authorization database list
        if (isAdminLogin) {
            for (int i = 0; i < databaseInfoList.size(); i++) {
                DatabaseInfo databaseInfo = databaseInfoList.get(i);
                if (databaseInfo != null
                        && getDatabaseInfo(authorDatabaseList, databaseInfo.getDbName()) == null) {
                    addedDbList.add(databaseInfo);
                    isUpdated = true;
                }
            }
        }

        authorDatabaseList.removeAll(deletedDbList);
        authorDatabaseList.addAll(addedDbList);

        // update the admin user the latest access broker port and IP
        if (isAdminLogin) {
            String defaultBrokerPort = getDefaultPort(serverInfo);
            for (int i = 0; i < authorDatabaseList.size(); i++) {
                DatabaseInfo databaseInfo = authorDatabaseList.get(i);
                String serverBrokerPort = databaseInfo.getBrokerPort();
                if (serverBrokerPort == null
                        || serverBrokerPort.trim().length() == 0
                        || !isExistPort(serverInfo, serverBrokerPort)) {
                    serverBrokerPort = defaultBrokerPort;
                    isUpdated = true;
                }
                databaseInfo.setBrokerPort(serverBrokerPort);
                String localBrokerPort = QueryOptions.getBrokerPort(databaseInfo);
                if (localBrokerPort != null
                        && localBrokerPort.trim().length() > 0
                        && !serverBrokerPort.equals(localBrokerPort)
                        && isExistPort(serverInfo, localBrokerPort)) {
                    databaseInfo.setBrokerPort(localBrokerPort);
                    isUpdated = true;
                }

                String serverBrokerIp = databaseInfo.getBrokerIP();
                String localBrokerIp = QueryOptions.getBrokerIp(databaseInfo);
                if (!localBrokerIp.equals(serverBrokerIp)) {
                    databaseInfo.setBrokerIP(localBrokerIp);
                    isUpdated = true;
                }
                if (databaseInfo.getAuthLoginedDbUserInfo() == null) {
                    DbUserInfo userInfo =
                            new DbUserInfo(databaseInfo.getDbName(), "dba", "", "", true);
                    databaseInfo.setAuthLoginedDbUserInfo(userInfo);
                    isUpdated = true;
                }
            }
        }

        // Set charset and database ip from local preference file
        for (DatabaseInfo databaseInfo : authorDatabaseList) {
            databaseInfo.setCharSet(QueryOptions.getCharset(databaseInfo));
            DbUserInfo dbUserInfo = databaseInfo.getAuthLoginedDbUserInfo();
            if (dbUserInfo != null) {
                String password =
                        CMDBNodePersistManager.getInstance()
                                .getDbPassword(
                                        serverInfo.getHostAddress(),
                                        String.valueOf(serverInfo.getHostMonPort()),
                                        databaseInfo.getDbName(),
                                        dbUserInfo.getName());
                dbUserInfo.setNoEncryptPassword(password);
            }
        }

        return isUpdated;
    }

    /**
     * Get default port
     *
     * @param serverInfo ServerInfo
     * @return String
     */
    private static String getDefaultPort(ServerInfo serverInfo) { // FIXME extract
        String defaultBrokerPort = "30000";
        BrokerInfos brokerInfos = serverInfo.getBrokerInfos();
        if (brokerInfos != null) {
            BrokerInfoList brokerInfoList = brokerInfos.getBorkerInfoList();
            if (brokerInfoList != null) {
                List<BrokerInfo> list = brokerInfoList.getBrokerInfoList();
                for (int i = 0; list != null && i < list.size(); i++) {
                    BrokerInfo brokerInfo = list.get(i);
                    if (i == 0) {
                        defaultBrokerPort = brokerInfo.getPort();
                    }
                    if (brokerInfo.getName().equals("query_editor")) {
                        defaultBrokerPort = brokerInfo.getPort();
                        break;
                    }
                }
            }
        }
        return defaultBrokerPort;
    }

    /**
     * Return whether broker port exist
     *
     * @param serverInfo ServerInfo
     * @param brokerPort String
     * @return boolean
     */
    private static boolean isExistPort(ServerInfo serverInfo, String brokerPort) { // FIXME extract
        BrokerInfos brokerInfos = serverInfo.getBrokerInfos();
        if (brokerInfos != null) {
            BrokerInfoList brokerInfoList = brokerInfos.getBorkerInfoList();
            if (brokerInfoList != null) {
                List<BrokerInfo> list = brokerInfoList.getBrokerInfoList();
                for (int i = 0; list != null && i < list.size(); i++) {
                    BrokerInfo brokerInfo = list.get(i);
                    if (brokerPort.equals(brokerInfo.getPort())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
