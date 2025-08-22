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
package com.cubrid.common.ui.spi.model;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.replication.model.ReplicationInfo;

/**
 * CUBRID Database node
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class CubridDatabase extends DefaultSchemaNode {
    public static final String DATA_KEY_EDITOR_CONFIG = ".editorConfigKey";
    private String startAndLoginIconPath;
    private String startAndLogoutIconPath;
    private String stopAndLoginIconPath;
    private String stopAndLogoutIconPath;
    private boolean autoSavePassword;
    // true if this database does not exists but create by other usage
    private boolean isVirtual = false;

    /**
     * The constructor
     *
     * @param id
     * @param label
     */
    public CubridDatabase(String id, String label) {
        super(id, label, label, "");
        setType(NodeType.DATABASE);
        setDatabase(this);
        setContainer(true);
    }

    /**
     * Get running type(C/S or standalone) of CUBRID Database
     *
     * @return the DbRunningType
     */
    public DbRunningType getRunningType() {
        return getDatabaseInfo() == null ? DbRunningType.NONE : getDatabaseInfo().getRunningType();
    }

    /**
     * Set CUBRID database running type(C/S or standalone)
     *
     * @param dbRunningType the DbRunningType
     */
    public void setRunningType(DbRunningType dbRunningType) {
        if (getDatabaseInfo() != null) {
            getDatabaseInfo().setRunningType(dbRunningType);
        }
    }

    /**
     * Get logined user name
     *
     * @return the user name
     */
    public String getUserName() {
        if (getDatabaseInfo() != null) {
            return getDatabaseInfo().getAuthLoginedDbUserInfo() == null
                    ? null
                    : getDatabaseInfo().getAuthLoginedDbUserInfo().getName();
        }
        return null;
    }

    /**
     * Get logined password
     *
     * @return the password
     */
    public String getPassword() {
        if (getDatabaseInfo() != null) {
            return getDatabaseInfo().getAuthLoginedDbUserInfo() == null
                    ? null
                    : getDatabaseInfo().getAuthLoginedDbUserInfo().getNoEncryptPassword();
        }
        return null;
    }

    /**
     * Get whether it has logined
     *
     * @return <code>true</code> if it is logined;<code>false</code> otherwise
     */
    public boolean isLogined() {
        if (getDatabaseInfo() != null) {
            return getDatabaseInfo().isLogined();
        }
        return false;
    }

    /**
     * Set logined status
     *
     * @param isLogined whether it is logined
     */
    public void setLogined(boolean isLogined) {
        if (getDatabaseInfo() != null) {
            getDatabaseInfo().setLogined(isLogined);
        }
    }

    /**
     * Get whether it is distributor database
     *
     * @return <code>true</code> if it is distributor database ; <code>false</code> otherwise
     */
    public boolean isDistributorDb() {
        if (getDatabaseInfo() != null) {
            return getDatabaseInfo().isDistributorDb();
        }
        return false;
    }

    /**
     * Set whether it is distributor database
     *
     * @param isDistDb whether it is distributor database
     */
    public void setDistributorDb(boolean isDistDb) {
        if (getDatabaseInfo() != null) {
            getDatabaseInfo().setDistributorDb(isDistDb);
        }
    }

    /**
     * Get database information
     *
     * @return the database information
     */
    public DatabaseInfo getDatabaseInfo() {
        if (this.getAdapter(DatabaseInfo.class) != null) {
            return (DatabaseInfo) this.getAdapter(DatabaseInfo.class);
        }
        return null;
    }

    /**
     * Set database information
     *
     * @param databaseInfo the DatabaseInfo object
     */
    public void setDatabaseInfo(DatabaseInfo databaseInfo) {
        this.setModelObj(databaseInfo);
    }

    /**
     * Check whether CubridDatabase and DatabaseInfo objects are valid.
     *
     * @param database CubridDatabase
     */
    public static boolean hasValidDatabaseInfo(CubridDatabase database) {
        return database != null && database.getDatabaseInfo() != null;
    }

    /**
     * Get icon path of database start and login status
     *
     * @return the icon path
     */
    public String getStartAndLoginIconPath() {
        return startAndLoginIconPath;
    }

    /**
     * Set icon path of database stop and logout status
     *
     * @param startAndLoginIconPath the icon path
     */
    public void setStartAndLoginIconPath(String startAndLoginIconPath) {
        this.startAndLoginIconPath = startAndLoginIconPath;
    }

    /**
     * Get icon path of database start and logout status
     *
     * @return the icon path
     */
    public String getStartAndLogoutIconPath() {
        return startAndLogoutIconPath;
    }

    /**
     * Set icon path of database stop and logout status
     *
     * @param startAndLogoutIconPath the icon path
     */
    public void setStartAndLogoutIconPath(String startAndLogoutIconPath) {
        this.startAndLogoutIconPath = startAndLogoutIconPath;
    }

    /**
     * Get icon path of database stop and login status
     *
     * @return the icon path
     */
    public String getStopAndLoginIconPath() {
        return stopAndLoginIconPath;
    }

    /**
     * Set icon path of database stop and logout status
     *
     * @param stopAndLoginIconPath the icon path
     */
    public void setStopAndLoginIconPath(String stopAndLoginIconPath) {
        this.stopAndLoginIconPath = stopAndLoginIconPath;
    }

    /**
     * Get icon path of database stop and logout status
     *
     * @return the icon path
     */
    public String getStopAndLogoutIconPath() {
        return stopAndLogoutIconPath;
    }

    /**
     * Set icon path of database stop and logout status
     *
     * @param stopAndLogoutIconPath the icon path
     */
    public void setStopAndLogoutIconPath(String stopAndLogoutIconPath) {
        this.stopAndLogoutIconPath = stopAndLogoutIconPath;
    }

    /**
     * Get adapter object
     *
     * @param adapter the adapter
     * @return the adapter object
     */
    @SuppressWarnings("rawtypes")
    public Object getAdapter(Class adapter) {
        if (adapter == ReplicationInfo.class && getDatabaseInfo() != null) {
            return getDatabaseInfo().getReplInfo();
        }
        return super.getAdapter(adapter);
    }

    /**
     * Return whether the current object is equal the obj
     *
     * @param obj the object
     * @return <code>true</code> if they are equal;<code>false</code> otherwise
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof CubridDatabase)) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * Return the hash code value
     *
     * @return the hash code value
     */
    public int hashCode() {
        return this.getId().hashCode();
    }

    /**
     * Auto save login password.
     *
     * @return save or not save.
     */
    public boolean isAutoSavePassword() {
        return autoSavePassword;
    }

    /**
     * Set auto save or not save login password.
     *
     * @param autoSavePassword save or not save.
     */
    public void setAutoSavePassword(boolean autoSavePassword) {
        this.autoSavePassword = autoSavePassword;
    }

    /** @return the isVirtual */
    public boolean isVirtual() {
        return isVirtual;
    }

    /** @param isVirtual the isVirtual to set */
    public void setVirtual(boolean isVirtual) {
        this.isVirtual = isVirtual;
    }

    /**
     * return database label with user name in order to recognize between databases.
     *
     * @return
     */
    public String getLabelWithUser() {
        String label = getLabel();
        String userName = getUserName();
        if (StringUtil.isNotEmpty(userName)) {
            return userName + "@" + label;
        }

        return label;
    }

    /** Deep clone */
    public CubridDatabase clone() throws CloneNotSupportedException {
        CubridDatabase obj = (CubridDatabase) super.clone();
        DatabaseInfo dbInfo = null;
        if (this.getDatabaseInfo() != null) {
            dbInfo = this.getDatabaseInfo().clone();
        }
        obj.setDatabaseInfo(dbInfo);
        return obj;
    }
}
