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
package com.cubrid.cubridmanager.ui.spi.persist;

import static com.cubrid.common.core.util.NoOp.noOp;

import com.cubrid.common.configuration.jdbc.IJDBCConnectionChangedObserver;
import com.cubrid.common.configuration.jdbc.IJDBCInfoChangedSubject;
import com.cubrid.common.configuration.jdbc.JDBCChangingManager;
import com.cubrid.common.core.util.ApplicationUtil;
import com.cubrid.common.core.util.CipherUtils;
import com.cubrid.common.core.util.FileUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.common.persist.ConnectionInfo;
import com.cubrid.common.ui.query.editor.EditorConstance;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridNodeLoader;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.DatabaseEditorConfig;
import com.cubrid.common.ui.spi.model.ICubridNodeLoader;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.persist.CubridJdbcManager;
import com.cubrid.common.ui.spi.persist.PersistUtils;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.xml.IXMLMemento;
import com.cubrid.cubridmanager.core.common.xml.XMLMemento;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.cubridmanager.ui.spi.model.loader.CQBDbConnectionLoader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.graphics.RGB;
import org.slf4j.Logger;

/**
 * Connection node persist manager
 *
 * @author pangqiren
 * @version 1.0 - 2011-4-2 created by pangqiren
 */
public final class CQBDBNodePersistManager implements IJDBCInfoChangedSubject {

    private static final Logger LOGGER = LogUtil.getLogger(CQBDBNodePersistManager.class);

    private static final String DATABASE_XML_CONTENT = "CUBRID_DATABASES";
    private static CQBDBNodePersistManager instance;
    private List<CubridDatabase> databaseList = null;

    private List<IJDBCConnectionChangedObserver> observers =
            new ArrayList<IJDBCConnectionChangedObserver>();

    private CQBDBNodePersistManager() {
        noOp();
    }

    /**
     * Return the only DbNodePersistManager
     *
     * @return DbNodePersistManager
     */
    public static CQBDBNodePersistManager getInstance() {
        synchronized (CQBDBNodePersistManager.class) {
            if (instance == null) {
                instance = new CQBDBNodePersistManager();
                JDBCChangingManager.getInstance().registerSubject(instance);
                JDBCChangingManager.getInstance()
                        .registerObservor(new CQBConnectionChangingObserver());
                instance.init();
            }
        }

        return instance;
    }

    /** Initial the persist manager */
    protected void init() {
        synchronized (this) {
            databaseList = new ArrayList<CubridDatabase>();
            loadDatabases();
        }
    }

    /** Load added host from plugin preference */
    protected void loadDatabases() {
        synchronized (this) {
            IXMLMemento memento =
                    PersistUtils.getXMLMemento(
                            ApplicationUtil.CQB_UI_PLUGIN_ID, DATABASE_XML_CONTENT);
            loadDatabases(memento, false, null);
        }
    }

    /**
     * Load added host from file preference
     *
     * @param workspacePath String
     * @return boolean whether imported
     */
    public boolean loadDatabases(String workspacePath) {
        synchronized (this) {
            String settingPath =
                    workspacePath
                            + File.separator
                            + ".metadata"
                            + File.separator
                            + ".plugins"
                            + File.separator
                            + "org.eclipse.core.runtime"
                            + File.separator
                            + ".settings"
                            + File.separator;
            String serverPath = settingPath + File.separator + "com.cubrid.cubridquery.ui.prefs";
            PreferenceStore preference = new PreferenceStore(serverPath);
            int size = databaseList.size();
            try {
                preference.load();
                String xmlString = preference.getString(DATABASE_XML_CONTENT);
                if (xmlString == null || xmlString.trim().length() == 0) {
                    return false;
                }
                ByteArrayInputStream in = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));
                IXMLMemento memento = XMLMemento.loadMemento(in);
                loadDatabases(memento, true, settingPath);
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
            boolean isImported = size != databaseList.size();
            if (isImported) {
                saveDatabases();
            }
            return isImported;
        }
    }

    /**
     * Return whether this server has been existed and exclude this server
     *
     * @param address the ip address
     * @param port the port
     * @param server the CubridServer object
     * @return <code>true</code> if contain this server;<code>false</code> otherwise
     */
    public boolean isContainedByHostAddress(
            String address, String port, CubridDatabase server) { // FIXME extract
        for (int i = 0; i < databaseList.size(); i++) {
            CubridDatabase serv = databaseList.get(i);
            if (server != null && server.getId().equals(serv.getId())) {
                continue;
            }
            DatabaseInfo serverInfo = serv.getDatabaseInfo();
            if (serverInfo.getBrokerIP().equals(address)
                    && serverInfo.getBrokerPort().equals(port)) {
                return true;
            }
        }
        return false;
    }

    public List<CubridDatabase> parseDatabaseFromXML(File file) { // FIXME extract?
        List<CubridDatabase> list = new ArrayList<CubridDatabase>();

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            IXMLMemento xmlContent = XMLMemento.loadMemento(fis);

            IXMLMemento[] children = xmlContent == null ? null : xmlContent.getChildren("database");
            for (int i = 0; children != null && i < children.length; i++) {
                String name = children[i].getString("name");
                String dbName = children[i].getString("dbName");
                String brokerIp = children[i].getString("brokerIp");
                String brokerPort = children[i].getString("brokerPort");
                String charset = children[i].getString("charset");
                String jdbcAttrs = children[i].getString("jdbcAttrs");
                String dbUser = children[i].getString("dbUser");
                String dbPassword = children[i].getString("dbPassword");
                boolean savePassword = children[i].getBoolean("savePassword");
                String jdbcDriver = children[i].getString("jdbcDriver");

                // [TOOLS-2425]Support shard broker
                Boolean isShardObj = children[i].getBoolean("isShard");
                boolean isShard = isShardObj == null ? false : isShardObj;
                String shardQueryTypeStr = children[i].getString("shardQueryType");
                int shardQueryType = StringUtil.intValue(shardQueryTypeStr, 0);

                ServerInfo serverInfo = new ServerInfo();
                serverInfo.setServerName(name);
                serverInfo.setHostAddress(brokerIp);
                serverInfo.setHostMonPort(Integer.parseInt(brokerPort));
                serverInfo.setHostJSPort(Integer.parseInt(brokerPort) + 1);
                serverInfo.setUserName(dbName + "@" + brokerIp);
                serverInfo.setJdbcDriverVersion(jdbcDriver);

                CubridServer server = new CubridServer(name, name, null, null);
                server.setServerInfo(serverInfo);
                server.setType(NodeType.SERVER);

                DatabaseInfo dbInfo = new DatabaseInfo(dbName, serverInfo);
                dbInfo.setBrokerIP(brokerIp);
                dbInfo.setBrokerPort(brokerPort);
                dbInfo.setCharSet(charset);
                dbInfo.setJdbcAttrs(jdbcAttrs);
                dbInfo.setRunningType(DbRunningType.CS);

                // [TOOLS-2425]Support shard broker
                dbInfo.setShard(isShard);
                dbInfo.setShardQueryType(shardQueryType);

                String decDbPassword = null;
                if (dbPassword != null && dbPassword.trim().length() > 0) {
                    decDbPassword = CipherUtils.decrypt(dbPassword);
                }
                DbUserInfo dbUserInfo =
                        new DbUserInfo(dbName, dbUser, dbPassword, decDbPassword, false);
                dbInfo.setAuthLoginedDbUserInfo(dbUserInfo);

                String dbId = name + ICubridNodeLoader.NODE_SEPARATOR + name;

                DatabaseEditorConfig editorConfig = null;
                IXMLMemento editorConfigElement = children[i].getChild("editorConfig");
                if (editorConfigElement != null) {
                    editorConfig = new DatabaseEditorConfig();
                    String strBGPos = editorConfigElement.getString("purpose-code");
                    if (strBGPos == null) {
                        strBGPos = "0";
                    }
                    int bgPos = StringUtil.intValue(strBGPos, 0);
                    RGB background = EditorConstance.getRGBByPos(bgPos);
                    editorConfig.setBackGround(background);
                    editorConfig.setDatabaseComment(
                            editorConfigElement.getString("database-comment"));
                }

                CubridDatabase database = new CubridDatabase(dbId, name);
                database.setServer(server);
                database.setStartAndLoginIconPath("icons/navigator/database_start_connected.png");
                database.setStartAndLogoutIconPath(
                        "icons/navigator/database_start_disconnected.png");
                database.setDatabaseInfo(dbInfo);
                CubridNodeLoader loader = new CQBDbConnectionLoader();
                loader.setLevel(ICubridNodeLoader.FIRST_LEVEL);
                database.setLoader(loader);
                database.setAutoSavePassword(savePassword);

                /*Save DatabaseEditorConfig to database object*/
                if (editorConfig != null) {
                    database.setData(CubridDatabase.DATA_KEY_EDITOR_CONFIG, editorConfig);
                }
                list.add(database);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            FileUtil.close(fis);
        }

        return list;
    }

    /**
     * Load databases from xml memento
     *
     * @param memento IXMLMemento
     * @param isLoadOptions boolean
     * @param optionPath String
     */
    protected void loadDatabases(IXMLMemento memento, boolean isLoadOptions, String optionPath) {
        // when import connections, load the global preference
        if (isLoadOptions) {
            QueryOptions.load(optionPath, null);
        }
        IXMLMemento[] children = memento == null ? null : memento.getChildren("database");
        for (int i = 0; children != null && i < children.length; i++) {
            String name = children[i].getString("name");
            String dbName = children[i].getString("dbName");
            String dbUser = children[i].getString("dbUser");
            String dbPassword = children[i].getString("dbPassword");
            String jdbcDriver = children[i].getString("jdbcDriver");
            boolean savePassword = children[i].getBoolean("savePassword");

            // [TOOLS-2425]Support shard broker
            Boolean isShardObj = children[i].getBoolean("isShard");
            boolean isShard = isShardObj == null ? false : isShardObj;
            String shardQueryTypeStr = children[i].getString("shardQueryType");
            int shardQueryType = StringUtil.intValue(shardQueryTypeStr, 0);
            if (shardQueryType == 0) {
                shardQueryType = DatabaseInfo.SHARD_QUERY_TYPE_VAL;
            }

            String brokerIp = children[i].getString("brokerIp");
            String brokerPort = children[i].getString("brokerPort");
            String charset = children[i].getString("charset");
            String jdbcAttrs = children[i].getString("jdbcAttrs");

            DatabaseEditorConfig editorConfig = null;
            IXMLMemento editorConfigElement = children[i].getChild("editorConfig");
            if (editorConfigElement != null) {
                editorConfig = new DatabaseEditorConfig();
                String strBGPos = editorConfigElement.getString("purpose-code");
                if (strBGPos == null) {
                    strBGPos = "0";
                }
                int bgPos = StringUtil.intValue(strBGPos, 0);
                RGB background = EditorConstance.getRGBByPos(bgPos);
                editorConfig.setBackGround(background);
                editorConfig.setDatabaseComment(editorConfigElement.getString("database-comment"));
            }

            ServerInfo serverInfo = new ServerInfo();
            serverInfo.setServerName(name);
            serverInfo.setHostAddress(brokerIp);
            serverInfo.setHostMonPort(Integer.parseInt(brokerPort));
            serverInfo.setHostJSPort(Integer.parseInt(brokerPort) + 1);
            serverInfo.setUserName(dbName + "@" + brokerIp);
            serverInfo.setJdbcDriverVersion(jdbcDriver);

            CubridServer server = new CubridServer(name, name, null, null);
            server.setServerInfo(serverInfo);
            server.setType(NodeType.SERVER);

            DatabaseInfo dbInfo = new DatabaseInfo(dbName, serverInfo);
            dbInfo.setBrokerIP(brokerIp);
            dbInfo.setBrokerPort(brokerPort);
            dbInfo.setCharSet(charset);
            dbInfo.setJdbcAttrs(jdbcAttrs);
            dbInfo.setRunningType(DbRunningType.CS);

            // [TOOLS-2425]Support shard broker
            dbInfo.setShard(isShard);
            dbInfo.setShardQueryType(shardQueryType);

            String decDbPassword = null;
            if (dbPassword != null && dbPassword.trim().length() > 0) {
                decDbPassword = CipherUtils.decrypt(dbPassword);
            }
            DbUserInfo dbUserInfo =
                    new DbUserInfo(dbName, dbUser, dbPassword, decDbPassword, false);
            dbInfo.setAuthLoginedDbUserInfo(dbUserInfo);

            String dbId = name + ICubridNodeLoader.NODE_SEPARATOR + name;
            if (this.getDatabase(dbId) != null) {
                continue;
            }
            CubridDatabase database = new CubridDatabase(dbId, name);
            database.setServer(server);
            database.setStartAndLoginIconPath("icons/navigator/database_start_connected.png");
            database.setStartAndLogoutIconPath("icons/navigator/database_start_disconnected.png");
            database.setDatabaseInfo(dbInfo);
            CubridNodeLoader loader = new CQBDbConnectionLoader();
            loader.setLevel(ICubridNodeLoader.FIRST_LEVEL);
            database.setLoader(loader);
            database.setAutoSavePassword(savePassword);
            if (isLoadOptions) {
                QueryOptions.load(optionPath, serverInfo);
            }
            /*Save the DatabaseEditorConfig to the memory*/
            QueryOptions.putEditorConfig(database, editorConfig, false);

            databaseList.add(database);
            fireAddDatabase(database);
        }
    }

    /** Save added database to plug-in preference */
    public void saveDatabases() {
        synchronized (this) {
            XMLMemento memento = XMLMemento.createWriteRoot("databases");
            Iterator<CubridDatabase> iterator = databaseList.iterator();
            while (iterator.hasNext()) {
                CubridDatabase database = (CubridDatabase) iterator.next();
                DatabaseInfo dbInfo = database.getDatabaseInfo();
                ServerInfo serverInfo = dbInfo.getServerInfo();
                DbUserInfo dbUserInfo = dbInfo.getAuthLoginedDbUserInfo();

                IXMLMemento child = memento.createChild("database");
                child.putString("name", database.getLabel());
                child.putString("dbName", dbInfo.getDbName());
                child.putString("dbUser", dbUserInfo.getName());
                if (database.isAutoSavePassword()) {
                    child.putString(
                            "dbPassword", CipherUtils.encrypt(dbUserInfo.getNoEncryptPassword()));
                } else {
                    child.putString("dbPassword", "");
                }
                child.putString("savePassword", String.valueOf(database.isAutoSavePassword()));

                // [TOOLS-2425]Support shard broker
                child.putString("isShard", String.valueOf(dbInfo.isShard()));
                child.putString("shardQueryType", String.valueOf(dbInfo.getShardQueryType()));

                child.putString("jdbcDriver", serverInfo.getJdbcDriverVersion());
                child.putString("brokerIp", dbInfo.getBrokerIP());
                child.putString("brokerPort", dbInfo.getBrokerPort());
                child.putString("charset", dbInfo.getCharSet());
                child.putString("jdbcAttrs", dbInfo.getJdbcAttrs());

                /*Save the database editor config*/
                DatabaseEditorConfig editorConfig = QueryOptions.getEditorConfig(database, false);
                if (editorConfig != null) {
                    IXMLMemento editorConfigChild = child.createChild("editorConfig");
                    editorConfigChild.putString(
                            "database-comment",
                            editorConfig.getDatabaseComment() == null
                                    ? ""
                                    : editorConfig.getDatabaseComment());

                    if (editorConfig.getBackGround() != null) {
                        RGB background = editorConfig.getBackGround();
                        int bgPos = EditorConstance.getBGPos(background);
                        editorConfigChild.putInteger("purpose-code", bgPos);
                    }
                }
            }
            PersistUtils.saveXMLMemento(
                    ApplicationUtil.CQB_UI_PLUGIN_ID, DATABASE_XML_CONTENT, memento);
        }
    }

    public void saveServer(List<CubridDatabase> servers, String path) {
        synchronized (this) {
            XMLMemento memento = XMLMemento.createWriteRoot("databases");
            Iterator<CubridDatabase> iterator = servers.iterator();
            while (iterator.hasNext()) {
                CubridDatabase db = (CubridDatabase) iterator.next();
                DatabaseInfo dbInfo = db.getDatabaseInfo();
                IXMLMemento child = memento.createChild("database");
                child.putString("name", db.getLabel());
                child.putString("dbName", dbInfo.getDbName());
                child.putString("brokerPort", dbInfo.getBrokerPort());
                child.putString("brokerIp", dbInfo.getBrokerIP());
                child.putString("charset", dbInfo.getCharSet());
                child.putString("jdbcAttrs", dbInfo.getJdbcAttrs());
                child.putString("dbUser", db.getUserName());
                child.putBoolean("savePassword", false);
                child.putString("jdbcDriver", db.getServer().getJdbcDriverVersion());
                /*Save the database editor config*/
                DatabaseEditorConfig config = QueryOptions.getEditorConfig(db, false);
                if (config != null) {
                    IXMLMemento editorConfigChild = child.createChild("editorConfig");
                    editorConfigChild.putString(
                            "database-comment",
                            config.getDatabaseComment() == null ? "" : config.getDatabaseComment());

                    if (config.getBackGround() != null) {
                        RGB background = config.getBackGround();
                        int bgPos = EditorConstance.getBGPos(background);
                        editorConfigChild.putInteger("purpose-code", bgPos);
                    }
                }
            }
            FileOutputStream fout = null;
            try {
                fout = new FileOutputStream(path);
                fout.write(memento.getContents());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Add database
     *
     * @param database the CubridDatabase object
     * @param silence if true the event will not be triggered.
     */
    public void addDatabase(CubridDatabase database, boolean silence) {
        synchronized (this) {
            if (database != null) {
                databaseList.add(database);
                saveDatabases();
                CubridNodeManager.getInstance()
                        .fireCubridNodeChanged(
                                new CubridNodeChangedEvent(
                                        database, CubridNodeChangedEventType.NODE_ADD));
                if (!silence) {
                    fireAddDatabase(database);
                }
            }
        }
    }

    /**
     * Remove database
     *
     * @param database the CubridDatabase object
     */
    public void removeDatabase(CubridDatabase database) {
        synchronized (this) {
            if (database != null) {
                databaseList.remove(database);
                saveDatabases();
                CubridNodeManager.getInstance()
                        .fireCubridNodeChanged(
                                new CubridNodeChangedEvent(
                                        database, CubridNodeChangedEventType.NODE_REMOVE));
                fireDeleteDatabase(database);
            }
        }
    }

    /** Remove all databases */
    public void removeAllDatabase() {
        synchronized (this) {
            for (int i = 0; i < databaseList.size(); i++) {
                CubridDatabase database = databaseList.get(i);
                databaseList.remove(database);
                CubridNodeManager.getInstance()
                        .fireCubridNodeChanged(
                                new CubridNodeChangedEvent(
                                        database, CubridNodeChangedEventType.NODE_REMOVE));
                fireDeleteDatabase(database);
            }
            saveDatabases();
        }
    }

    /**
     * Reload all databases
     *
     * @return CubridDatabase List
     */
    public List<CubridDatabase> reloadDatabases() {
        loadDatabases();
        return databaseList;
    }

    /**
     * Get database by id
     *
     * @param id the database id
     * @return the CubridDatabase object
     */
    public CubridDatabase getDatabase(String id) {
        for (int i = 0; i < databaseList.size(); i++) {
            CubridDatabase database = databaseList.get(i);
            if (database.getId().equals(id)) {
                return database;
            }
        }
        return null;
    }

    /**
     * Get All databases
     *
     * @return all CubridDatabase objects
     */
    public List<CubridDatabase> getAllDatabase() {
        return databaseList;
    }

    /**
     * Add Observer
     *
     * @param obv IJDBCConnectionChangedObserver
     */
    public void addObservor(IJDBCConnectionChangedObserver ob) {
        if (!observers.contains(ob)) {
            observers.add(ob);
        }
    }

    public void fireAddDatabase(CubridDatabase database) {
        if (database == null) {
            return;
        }

        try {
            ConnectionInfo ci = getConnectionInfo(database);
            for (IJDBCConnectionChangedObserver ob : observers) {
                ob.afterAdd(this, ci);
            }
        } catch (Exception ex) {
            // ignore
        }
    }

    public void fireModifyDatabase(ConnectionInfo oldInfo, ConnectionInfo newInfo) {
        if (oldInfo == null || newInfo == null) {
            return;
        }

        try {
            for (IJDBCConnectionChangedObserver ob : observers) {
                ob.afterModify(this, oldInfo, newInfo);
            }
        } catch (Exception ex) {
            // ignore
        }
    }

    public void fireDeleteDatabase(CubridDatabase database) {
        if (database == null) {
            return;
        }

        try {
            ConnectionInfo info = getConnectionInfo(database);
            for (IJDBCConnectionChangedObserver ob : observers) {
                ob.afterDelete(this, info);
            }
        } catch (Exception ex) {
            // ignore
        }
    }

    /**
     * Retrives a connection information instance.
     *
     * @param database CubridDatabase
     * @return ConnectionInfo
     */
    public ConnectionInfo getConnectionInfo(CubridDatabase database) { // FIXME extract
        DatabaseInfo dbInfo = database.getDatabaseInfo();
        String user = "";
        String password = "";
        if (dbInfo.getAuthLoginedDbUserInfo() != null) {
            user = dbInfo.getAuthLoginedDbUserInfo().getName();
            password = dbInfo.getAuthLoginedDbUserInfo().getNoEncryptPassword();
        }

        String version =
                dbInfo.getServerInfo() == null ? "" : dbInfo.getServerInfo().getJdbcDriverVersion();
        String driverFileName = CubridJdbcManager.getInstance().getDriverFileByVersion(version);

        ConnectionInfo ci =
                new ConnectionInfo(
                        database.getName(),
                        dbInfo.getBrokerIP(),
                        StringUtil.intValue(dbInfo.getBrokerPort(), -1),
                        dbInfo.getDbName(),
                        user,
                        password,
                        driverFileName,
                        dbInfo.getCharSet());
        return ci;
    }
}
