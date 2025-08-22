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
package com.cubrid.cubridmanager.ui.mondashboard.editor.model;

import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.mondashboard.model.DBStatusType;
import com.cubrid.cubridmanager.core.mondashboard.model.HADatabaseStatusInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * Database node model class
 *
 * @author pangqiren
 * @version 1.0 - 2010-6-2 created by pangqiren
 */
public class DatabaseNode extends HANode {
    public static final String PROP_DB_STATUS = "PROP_DB_STATUS";
    private String dbName;
    private String dbUser;
    private String dbPassword;
    private boolean isConnected = false;
    private HADatabaseStatusInfo haDatabaseStatus = null;
    private HostNode parent;

    public DatabaseNode() {
        size.height = 98;
        size.width = 125;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public HADatabaseStatusInfo getHaDatabaseStatus() {
        return haDatabaseStatus;
    }

    /**
     * set database status to database node.
     *
     * @param haDatabaseStatus HADatabaseStatus
     */
    public void setHaDatabaseStatus(HADatabaseStatusInfo haDatabaseStatus) {
        this.haDatabaseStatus = haDatabaseStatus;
        this.firePropertyChange(PROP_DB_STATUS, null, haDatabaseStatus);
    }

    /**
     * Get database status type
     *
     * @return The DBStatusType
     */
    public DBStatusType getDbStatusType() {
        if (haDatabaseStatus != null) {
            return haDatabaseStatus.getStatusType();
        }
        return DBStatusType.UNKNOWN;
    }

    public HostNode getParent() {
        return parent;
    }

    public void setParent(HostNode parent) {
        this.parent = parent;
    }

    /**
     * Get database information
     *
     * @return The DatabaseInfo
     */
    public DatabaseInfo getDatabaseInfo() {
        if (getParent() == null
                || getParent().getServerInfo() == null
                || getParent().getServerInfo().getLoginedUserInfo() == null) {
            return null;
        } else {
            return getParent().getServerInfo().getLoginedUserInfo().getDatabaseInfo(dbName);
        }
    }

    /**
     * Only fire the property changed events,does not change the server's real connection status,
     * call this method after the server is connected or disconnected.
     *
     * @param connected boolean
     */
    public void setConnected(boolean connected) {
        isConnected = connected;
        if (!isConnected && !getParent().isConnected()) {
            HADatabaseStatusInfo haDatabaseStatus = new HADatabaseStatusInfo();
            haDatabaseStatus.setDbName(getDbName());
            setHaDatabaseStatus(haDatabaseStatus);
        }
        this.firePropertyChange(DatabaseNode.PROP_DB_STATUS, null, connected);
    }

    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Override object's equals method.
     *
     * @param obj Object.
     * @return boolean
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof DatabaseNode)) {
            return false;
        }
        return this.toString().equals(obj.toString());
    }

    /**
     * Override object's hashCode method.
     *
     * @return DatabaseNode hashCode
     */
    public int hashCode() {
        return this.toString().hashCode();
    }

    /**
     * database node to string
     *
     * @return DatabaseNode to string
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(parent.getUserName())
                .append("@")
                .append(parent.getIp())
                .append(":")
                .append(parent.getPort())
                .append("/")
                .append(dbName);
        return sb.toString();
    }

    /**
     * get ha outgoing connections
     *
     * @return ha outgoing connections
     */
    public List<MonitorConnection> getHAOutgoingConnections() {
        List<MonitorConnection> result = new ArrayList<MonitorConnection>();
        for (HANodeConnection conn : this.outputs) {
            if (conn instanceof MonitorConnection) {
                result.add((MonitorConnection) conn);
            }
        }
        return result;
    }

    /**
     * get ha incoming connections.
     *
     * @return ha incoming connections.
     */
    public List<MonitorConnection> getHAIncomingConnections() {
        List<MonitorConnection> result = new ArrayList<MonitorConnection>();
        for (HANodeConnection conn : this.inputs) {
            if (conn instanceof MonitorConnection) {
                result.add((MonitorConnection) conn);
            }
        }
        return result;
    }

    /** Remove all ha connections. Do not fire any events. */
    public void removeAllHAConnections() {
        List<HANodeConnection> tempList = new ArrayList<HANodeConnection>();
        tempList.addAll(outputs);
        tempList.addAll(inputs);
        for (HANodeConnection conn : tempList) {
            if (conn instanceof MonitorConnection) {
                conn.setSource(null);
                conn.setTarget(null);
            }
        }
    }
}
