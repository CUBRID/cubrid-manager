/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
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

import com.cubrid.common.configuration.jdbc.IJDBCConnectionChangedObserver;
import com.cubrid.common.configuration.jdbc.IJDBCInfoChangedSubject;
import com.cubrid.common.configuration.jdbc.JDBCChangingManager;
import com.cubrid.common.core.util.ApplicationUtil;
import com.cubrid.common.core.util.CipherUtils;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.common.persist.ConnectionInfo;
import com.cubrid.common.ui.query.editor.EditorConstance;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.DatabaseEditorConfig;
import com.cubrid.common.ui.spi.persist.CubridJdbcManager;
import com.cubrid.common.ui.spi.persist.PersistUtils;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.xml.IXMLMemento;
import com.cubrid.cubridmanager.core.common.xml.XMLMemento;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.graphics.RGB;
import org.slf4j.Logger;

/**
 * Database node persist manager
 *
 * @author pangqiren
 * @version 1.0 - 2011-4-1 created by pangqiren
 */
public final class CMDBNodePersistManager implements IJDBCInfoChangedSubject {

    private static final Logger LOGGER = LogUtil.getLogger(CMDBNodePersistManager.class);

    private static final String DATABASE_XML_CONTENT = "CUBRID_DATABASES";
    private Map<String, DBParameter> databaseMap = null;
    private static CMDBNodePersistManager instance;

    private List<IJDBCConnectionChangedObserver> observers =
            new ArrayList<IJDBCConnectionChangedObserver>();

    private CMDBNodePersistManager() {}

    /**
     * Return the only DbNodePersistManager
     *
     * @return DbNodePersistManager
     */
    public static CMDBNodePersistManager getInstance() {
        synchronized (CMDBNodePersistManager.class) {
            if (instance == null) {
                instance = new CMDBNodePersistManager();
                JDBCChangingManager.getInstance().registerSubject(instance);
                JDBCChangingManager.getInstance()
                        .registerObservor(new CMConnectionChangingObserver());

                instance.init();
            }
        }
        return instance;
    }

    /** Initial the persist manager */
    protected void init() {
        synchronized (this) {
            databaseMap = new HashMap<String, DBParameter>();
            loadDatabases();
        }
    }

    /** Load added databases from plugin preference */
    protected void loadDatabases() {
        synchronized (this) {
            IXMLMemento memento =
                    PersistUtils.getXMLMemento(
                            ApplicationUtil.CM_UI_PLUGIN_ID, DATABASE_XML_CONTENT);
            loadDatabases(memento);
        }
    }

    /**
     * Load added databases from workspace path
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
            String serverPath = settingPath + File.separator + "com.cubrid.cubridmanager.ui.prefs";
            PreferenceStore preference = new PreferenceStore(serverPath);
            int size = databaseMap.size();
            try {
                preference.load();
                String xmlString = preference.getString(DATABASE_XML_CONTENT);
                if (xmlString == null || xmlString.trim().length() == 0) {
                    return false;
                }
                ByteArrayInputStream in = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));
                IXMLMemento memento = XMLMemento.loadMemento(in);
                loadDatabases(memento);
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
            boolean isImported = size != databaseMap.size();
            if (isImported) {
                saveDatabases();
            }
            return isImported;
        }
    }

    /**
     * Load added databases from xml memento
     *
     * @param memento IXMLMemento
     */
    protected void loadDatabases(IXMLMemento memento) {
        IXMLMemento[] children = memento == null ? null : memento.getChildren("database");
        for (int i = 0; children != null && i < children.length; i++) {
            String address = children[i].getString("address");
            String port = children[i].getString("port");
            String dbName = children[i].getString("dbName");
            String dbUser = children[i].getString("dbUser");
            String encryptPassword = children[i].getString("dbPassword");
            String dbPassword = "";
            if (!StringUtil.isEmpty(encryptPassword)) {
                dbPassword = CipherUtils.decrypt(encryptPassword);
            }

            String jdbcAttrs = children[i].getString("jdbcAttrs");
            if (jdbcAttrs == null) jdbcAttrs = "";
            boolean savePassword = children[i].getBoolean("savePassword");

            String key = getMapKey(dbUser, dbName, address, port);
            DBParameter dbParameter = databaseMap.get(key);
            if (dbParameter == null) {
                dbParameter =
                        new DBParameter(
                                dbName, address, port, dbUser, dbPassword, jdbcAttrs, savePassword);
                databaseMap.put(key, dbParameter);
            }

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

            QueryOptions.putEditorConfig(dbUser, dbName, address, port, null, editorConfig, true);
        }
    }

    /** Save added database to plug-in preference */
    public void saveDatabases() {
        synchronized (this) {
            XMLMemento memento = XMLMemento.createWriteRoot("databases");
            for (DBParameter dbParameter : databaseMap.values()) {
                String dbName = StringUtils.defaultString(dbParameter.getDbName());
                String address = StringUtils.defaultString(dbParameter.getHostAddress());
                String port = StringUtils.defaultString(dbParameter.getMonPort());
                String dbUser = StringUtils.defaultString(dbParameter.getDBUser());
                String password = StringUtils.defaultString(dbParameter.getPassword());
                String jdbcAttrs = StringUtils.defaultString(dbParameter.getJdbcAttrs());
                boolean savePassword = dbParameter.isSavePassword();

                IXMLMemento child = memento.createChild("database");
                child.putString("dbUser", dbUser);
                child.putString("dbName", dbName);
                child.putString("address", address);
                child.putString("port", port);
                child.putString("dbPassword", CipherUtils.encrypt(password));
                child.putString("savePassword", String.valueOf(savePassword));
                child.putString("jdbcAttrs", jdbcAttrs);

                DatabaseEditorConfig editorConfig =
                        QueryOptions.getEditorConfig(dbUser, dbName, address, port, null, true);
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
                    ApplicationUtil.CM_UI_PLUGIN_ID, DATABASE_XML_CONTENT, memento);
        }
    }

    private static String getMapKey(String dbUser, String dbName, String address, String port) {
        StringBuffer sb = new StringBuffer();
        sb.append(dbUser);
        sb.append("@");
        sb.append(dbName);
        sb.append("@");
        sb.append(address);
        sb.append("@");
        sb.append(port);

        return sb.toString();
    }

    /**
     * Add or modify the database editor config
     *
     * @param database
     */
    public void addDatabase(CubridDatabase database, DatabaseEditorConfig editorConfig) {
        CubridServer server = database.getServer();
        String dbUser = database.getUserName();
        String dbName = database.getName();
        String address = server.getHostAddress();
        String port = server.getMonPort();
        String dbPassword =
                database.getDatabaseInfo().getAuthLoginedDbUserInfo().getNoEncryptPassword();
        String jdbcAttrs = StringUtils.defaultString(database.getDatabaseInfo().getJdbcAttrs());

        boolean savePassword = database.isAutoSavePassword();

        synchronized (this) {
            String key = getMapKey(dbUser, dbName, address, port);
            DBParameter dbParameter =
                    new DBParameter(
                            dbName, address, port, dbUser, dbPassword, jdbcAttrs, savePassword);
            databaseMap.put(key, dbParameter);
            QueryOptions.putEditorConfig(database, editorConfig, true);
            saveDatabases();
        }
        fireAddDatabase(database.getDatabaseInfo());
    }

    /**
     * Add or modify the database editor config
     *
     * @param database
     */
    public void modifyDatabase(
            CubridDatabase oldDatabase,
            CubridDatabase newDatabase,
            DatabaseEditorConfig editorConfig) {
        CubridServer server = newDatabase.getServer();
        String dbUser = newDatabase.getUserName();
        String dbName = newDatabase.getName();
        String address = server.getHostAddress();
        String port = server.getMonPort();
        String dbPassword =
                newDatabase.getDatabaseInfo().getAuthLoginedDbUserInfo().getNoEncryptPassword();
        String jdbcAttrs = StringUtils.defaultString(newDatabase.getDatabaseInfo().getJdbcAttrs());
        boolean savePassword = newDatabase.isAutoSavePassword();

        synchronized (this) {
            String key = getMapKey(dbUser, dbName, address, port);

            DBParameter dbParameter =
                    new DBParameter(
                            dbName, address, port, dbUser, dbPassword, jdbcAttrs, savePassword);
            databaseMap.put(key, dbParameter);
            QueryOptions.putEditorConfig(newDatabase, editorConfig, true);
            saveDatabases();
        }
        if (oldDatabase != null && oldDatabase.getDatabaseInfo() != null) {
            fireModifyDatabase(oldDatabase.getDatabaseInfo(), newDatabase.getDatabaseInfo());
        }
    }

    /**
     * Get the database password
     *
     * @param address String
     * @param port String
     * @param dbName String
     * @param dbUser String
     * @return String
     */
    public String getDbPassword(String address, String port, String dbName, String dbUser) {
        synchronized (this) {
            String key = getMapKey(dbUser, dbName, address, port);
            DBParameter dbParameter = databaseMap.get(key);
            if (dbParameter != null) {
                return StringUtils.defaultString(dbParameter.getPassword());
            }
            return "";
        }
    }

    /**
     * Get the database password
     *
     * @param address String
     * @param port String
     * @param dbName String
     * @param dbUser String
     * @return String
     */
    public boolean getDbSavePassword(String address, String port, String dbName, String dbUser) {
        synchronized (this) {
            String key = getMapKey(dbUser, dbName, address, port);
            DBParameter dbParameter = databaseMap.get(key);
            if (dbParameter != null) {
                return dbParameter.isSavePassword();
            }

            return false;
        }
    }

    /**
     * Delete the database password
     *
     * @param hostAddress String
     * @param monPort String
     * @param dbName String
     * @param dbUser String
     * @param savePassword boolean.
     */
    public void deleteDbParameter(CubridDatabase database) {

        String address = database.getServer().getHostAddress();
        String port = database.getServer().getMonPort();

        String dbName = database.getName();
        String dbUser = database.getUserName();

        synchronized (this) {
            String key = getMapKey(dbUser, dbName, address, port);
            databaseMap.remove(key);
            saveDatabases();
        }
    }

    /**
     * Update the database password
     *
     * @param address
     * @param port
     * @param dbName
     * @param dbUser
     * @param password
     */
    public void updateDbPassword(
            String address,
            String port,
            String dbName,
            String dbUser,
            String password,
            boolean isSavePassword) {
        synchronized (this) {
            String key = getMapKey(dbUser, dbName, address, port);
            DBParameter dbParameter = databaseMap.get(key);
            if (dbParameter != null) {
                dbParameter.setPassword(password);
                dbParameter.setSavePassword(isSavePassword);
                saveDatabases();
            }
        }
    }

    /**
     * Delete the database password in this server
     *
     * @param hostAddress String
     * @param monPort String
     */
    public void deleteParameter(CubridServer server) {
        String address = server.getHostAddress();
        String port = server.getMonPort();
        synchronized (this) {
            List<String> deletedKeyList = new ArrayList<String>();
            Iterator<Entry<String, DBParameter>> iterator = databaseMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, DBParameter> entry = iterator.next();
                DBParameter dbParameter = entry.getValue();
                if (StringUtil.isEqual(address, dbParameter.getHostAddress())
                        && StringUtil.isEqual(port, dbParameter.getMonPort())) {
                    deletedKeyList.add(entry.getKey());
                }
            }
            for (String key : deletedKeyList) {
                databaseMap.remove(key);
            }
            saveDatabases();
        }
    }

    /**
     * Get the database jdbc options
     *
     * @param hostAddress String
     * @param monPort String
     * @param dbName String
     * @param dbUser String
     * @return String
     */
    public String getJdbcAttrs(CubridDatabase database) {
        String address = database.getServer().getHostAddress();
        String port = database.getServer().getMonPort();

        String dbName = database.getName();
        String dbUser = database.getUserName();
        synchronized (this) {
            String key = getMapKey(dbUser, dbName, address, port);
            DBParameter dbParameter = databaseMap.get(key);
            if (dbParameter != null) {
                return StringUtils.defaultString(dbParameter.getJdbcAttrs());
            }

            return "";
        }
    }

    /**
     * Set the database jdbc options
     *
     * @param hostAddress
     * @param monPort
     * @param dbName
     * @param dbUser
     * @param attrs
     */
    public void setJdbcAttrs(CubridDatabase database, String attrs) {
        String address = database.getServer().getHostAddress();
        String port = database.getServer().getMonPort();

        String dbName = database.getName();
        String dbUser = database.getUserName();
        synchronized (this) {
            String key = getMapKey(dbUser, dbName, address, port);
            DBParameter dbParameter = databaseMap.get(key);
            if (dbParameter != null) {
                dbParameter.setJdbcAttrs(attrs);
            }
        }
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

    public void fireAddDatabase(DatabaseInfo databaseInfo) {
        if (databaseInfo == null) {
            return;
        }

        try {
            ConnectionInfo ci = getConnectionInfo(databaseInfo);
            for (IJDBCConnectionChangedObserver ob : observers) {
                ob.afterAdd(this, ci);
            }
        } catch (Exception ex) {
            // ignore
        }
    }

    public void fireModifyDatabase(DatabaseInfo oldDbInfo, DatabaseInfo newDbInfo) {
        if (oldDbInfo == null || newDbInfo == null) {
            return;
        }

        ConnectionInfo oldInfo = getConnectionInfo(oldDbInfo);
        ConnectionInfo newInfo = getConnectionInfo(newDbInfo);

        try {
            for (IJDBCConnectionChangedObserver ob : observers) {
                ob.afterModify(this, oldInfo, newInfo);
            }
        } catch (Exception ex) {
            // ignore
        }
    }

    public void fireDeleteDatabase(DatabaseInfo databaseInfo) {
        if (databaseInfo == null) {
            return;
        }

        try {
            ConnectionInfo info = getConnectionInfo(databaseInfo);
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
    public ConnectionInfo getConnectionInfo(DatabaseInfo databaseInfo) { // FIXME extract
        String user = "";
        String password = "";
        if (databaseInfo.getAuthLoginedDbUserInfo() != null) {
            user = databaseInfo.getAuthLoginedDbUserInfo().getName();
            password = databaseInfo.getAuthLoginedDbUserInfo().getNoEncryptPassword();
        }

        ServerInfo serverInfo = databaseInfo.getServerInfo();
        String version = "";
        String driverFileName = "";
        if (serverInfo != null) {
            version = serverInfo.getJdbcDriverVersion();
            driverFileName = CubridJdbcManager.getInstance().getDriverFileByVersion(version);
        }

        String connName =
                databaseInfo.getDbName()
                        + "@"
                        + databaseInfo.getBrokerIP()
                        + ":"
                        + databaseInfo.getBrokerPort();
        ConnectionInfo ci =
                new ConnectionInfo(
                        connName,
                        databaseInfo.getBrokerIP(),
                        StringUtil.intValue(databaseInfo.getBrokerPort(), -1),
                        databaseInfo.getDbName(),
                        user,
                        password,
                        driverFileName,
                        databaseInfo.getCharSet());

        return ci;
    }
}

class DBParameter {

    private String dbName;
    private String hostAddress;
    private String monPort;
    private String dbUser;
    private String password;
    private String jdbcAttrs;
    private boolean savePassword;

    public DBParameter(
            String dbName,
            String hostAddress,
            String monPort,
            String dbUser,
            String password,
            String jdbcAttrs,
            boolean savePassword) {

        this.dbName = dbName;
        this.hostAddress = hostAddress;
        this.monPort = monPort;
        this.dbUser = dbUser;
        this.password = password;
        this.jdbcAttrs = jdbcAttrs;
        this.savePassword = savePassword;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String address) {
        this.hostAddress = address;
    }

    public String getMonPort() {
        return monPort;
    }

    public void setMonPort(String port) {
        this.monPort = port;
    }

    public String getDBUser() {
        return dbUser;
    }

    public void setDBUser(String useName) {
        this.dbUser = useName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getJdbcAttrs() {
        return jdbcAttrs;
    }

    public void setJdbcAttrs(String jdbcAttrs) {
        this.jdbcAttrs = jdbcAttrs;
    }

    public boolean isSavePassword() {
        return savePassword;
    }

    public void setSavePassword(boolean savePassword) {
        this.savePassword = savePassword;
    }
}
