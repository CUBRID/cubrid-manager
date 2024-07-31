package com.cubrid.cubridmanager.ui.mondashboard.editor.model;

import com.cubrid.common.ui.spi.model.CubridServer;

public class CubridServerUIWrapper implements Comparable<CubridServerUIWrapper> {
    private static final int SAME_ID = 1;
    private static final int SAME_NAME = 1 << 1;
    private static final int SAME_HOST = 1 << 2;
    private static final int SAME_PORT = 1 << 3;
    private CubridServer server;

    public CubridServerUIWrapper(CubridServer server) {
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
        if (server.getServerInfo() != null) {
            server.getServerInfo().setHostAddress(address);
        }
    }

    public String getAddress() {
        if (server.getServerInfo() == null) {
            return "";
        }
        return server.getServerInfo().getHostAddress();
    }

    public void setJdbcDriver(String jdbcDriver) {
        if (server.getServerInfo() != null) {
            server.getServerInfo().setJdbcDriverVersion(jdbcDriver);
        }
    }

    public String getJdbcDriverVersion() {
        if (server.getServerInfo() == null) {
            return "";
        }
        return server.getServerInfo().getJdbcDriverVersion();
    }

    public void setPort(String port) {
        if (server.getServerInfo() != null) {
            server.getServerInfo().setHostMonPort(Integer.parseInt(port));
        }
    }

    public String getPort() {
        if (server.getServerInfo() == null) {
            return "";
        }
        return String.valueOf(server.getServerInfo().getHostMonPort());
    }

    public void setUser(String name) {
        if (server.getServerInfo() != null) {
            server.getServerInfo().setUserName(name);
        }
    }

    public String getUser() {
        if (server.getServerInfo() == null) {
            return "";
        }
        return server.getServerInfo().getUserName();
    }

    public void setPassword(String password) {
        if (server.getServerInfo() != null) {
            server.getServerInfo().setUserPassword(password);
        }
    }

    public String getPassword() {
        if (server.getServerInfo() == null) {
            return "";
        }
        return server.getServerInfo().getUserPassword();
    }

    public void setName(String name) {
        if (server == null) {
            return;
        }
        server.setLabel(name);
    }

    public String getName() {
        if (server == null) {
            return "";
        }
        return server.getLabel();
    }

    public String getId() {
        if (server == null) {
            return "";
        }
        return server.getId();
    }

    public CubridServer getServer() {
        return this.server;
    }

    public int compareTo(CubridServerUIWrapper obj) {
        int retVal = 0;
        retVal = (obj.getId().equals(this.getId())) ? retVal | SAME_ID : retVal;
        retVal = (obj.getName().equals(obj.getName())) ? retVal | SAME_NAME : retVal;
        retVal = (obj.getAddress().equals(getAddress())) ? retVal | SAME_HOST : retVal;
        retVal = (obj.getPort().equals(getPort())) ? retVal | SAME_PORT : retVal;
        return retVal;
    }

    public int compareTo(CubridServer obj) {
        boolean same =
                (obj.getId().equals(this.getId()))
                        && (obj.getHostAddress().equals(getAddress()))
                        && (obj.getName().equals(obj.getName()))
                        && (obj.getMonPort().equals(getPort()));
        return (same) ? 0 : 1;
    }

    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public int hashCode() {
        return super.hashCode();
    }
}
