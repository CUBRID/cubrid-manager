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
package com.cubrid.cubridquery.ui.spi.model;

import com.cubrid.common.ui.spi.model.CubridDatabase;

public class DatabaseUIWrapper implements Comparable<DatabaseUIWrapper> {
    private static final int SAME_ID = 1;
    private static final int SAME_NAME = 1 << 1;
    private static final int SAME_HOST = 1 << 2;
    private static final int SAME_PORT = 1 << 3;
    private CubridDatabase server;

    public DatabaseUIWrapper(CubridDatabase server) {
        this.server = server;
    }

    private boolean seleted = false;

    public boolean isSeleted() {
        return seleted;
    }

    public void setSeleted(boolean seleted) {
        this.seleted = seleted;
    }

    public void setSavePassword(String save) {
        server.setAutoSavePassword(Boolean.parseBoolean(save));
    }

    public boolean isAutoSavePassword() {
        return server.isAutoSavePassword();
    }

    public void setAddress(String address) {
        if (server.getDatabaseInfo() == null) return;
        server.getDatabaseInfo().setBrokerIP(address);
    }

    public String getAddress() {
        if (server.getDatabaseInfo() == null) return "";
        return server.getDatabaseInfo().getBrokerIP();
    }

    public void setJdbcDriver(String jdbcDriver) {
        if (server.getServer() == null || server.getServer().getServerInfo() == null) return;
        server.getServer().getServerInfo().setJdbcDriverVersion(jdbcDriver);
    }

    public String getJdbcDriverVersion() {
        if (server.getServer() == null || server.getServer().getServerInfo() == null) return "";
        return server.getServer().getServerInfo().getJdbcDriverVersion();
    }

    public void setPort(String port) {
        if (server.getDatabaseInfo() == null) return;
        server.getDatabaseInfo().setBrokerIP(port);
    }

    public String getPort() {
        if (server.getDatabaseInfo() == null) return "";
        return server.getDatabaseInfo().getBrokerPort();
    }

    public void setCharSet(String charset) {
        if (server.getDatabaseInfo() == null) return;
        server.getDatabaseInfo().setCharSet(charset);
    }

    public String getCharSet() {
        if (server.getDatabaseInfo() == null) return "";
        return server.getDatabaseInfo().getCharSet();
    }

    public void setUser(String name) {
        if (server.getDatabaseInfo() == null
                || server.getDatabaseInfo().getAuthLoginedDbUserInfo() == null) return;
        server.getDatabaseInfo().getAuthLoginedDbUserInfo().setName(name);
    }

    public String getUser() {
        if (server.getDatabaseInfo() == null
                || server.getDatabaseInfo().getAuthLoginedDbUserInfo() == null) return "";
        return server.getDatabaseInfo().getAuthLoginedDbUserInfo().getName();
    }

    public void setPassword(String password) {
        if (server.getDatabaseInfo() == null
                || server.getDatabaseInfo().getAuthLoginedDbUserInfo() == null) return;
        server.getDatabaseInfo().getAuthLoginedDbUserInfo().setPassword(password);
    }

    public String getPassword() {
        if (server.getDatabaseInfo() == null
                || server.getDatabaseInfo().getAuthLoginedDbUserInfo() == null) return "";
        return server.getDatabaseInfo().getAuthLoginedDbUserInfo().getPassword();
    }

    public void setDbName(String dbName) {
        if (server == null || server.getDatabaseInfo() == null) return;
        server.getDatabaseInfo().setDbName(dbName);
    }

    public String getDbName() {
        if (server == null || server.getDatabaseInfo() == null) return "";
        return server.getDatabaseInfo().getDbName();
    }

    public void setName(String name) {
        if (server == null) return;
        server.setLabel(name);
    }

    public String getName() {
        if (server == null) return "";
        return server.getLabel();
    }

    public String getId() {
        if (server == null) return "";
        return server.getId();
    }

    public CubridDatabase getDatabase() {
        return this.server;
    }

    public int compareTo(DatabaseUIWrapper obj) {
        int retVal = 0;
        retVal = (obj.getId().equals(this.getId())) ? retVal | SAME_ID : retVal;
        retVal = (obj.getName().equals(obj.getName())) ? retVal | SAME_NAME : retVal;
        retVal = (obj.getAddress().equals(getAddress())) ? retVal | SAME_HOST : retVal;
        retVal = (obj.getPort().equals(getPort())) ? retVal | SAME_PORT : retVal;
        return retVal;
    }

    public int compareTo(CubridDatabase obj) {
        try {
            boolean same =
                    (obj.getId().equals(this.getId()))
                            && (obj.getDatabaseInfo().getBrokerIP().equals(getAddress()))
                            && (obj.getDatabaseInfo().getDbName().equals(obj.getName()))
                            && (obj.getDatabaseInfo().getBrokerIP().equals(getPort()));
            return (same) ? 0 : 1;
        } catch (Exception e) {
            return 0;
        }
    }
}
