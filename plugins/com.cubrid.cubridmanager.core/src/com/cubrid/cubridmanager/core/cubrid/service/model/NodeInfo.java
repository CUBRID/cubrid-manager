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
package com.cubrid.cubridmanager.core.cubrid.service.model;

import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NodeInfo implements Cloneable { // FIXME description
    private String serviceName;
    private List<String> databases;
    private String hostName;
    private String ip;
    private String brokerInfo;
    private String cmConnectName;
    private String status;
    private ServerInfo serverInfo;
    // Note: for user, can only register host and port info, later need consider it.
    private Set<NodeInfo> remoteBrokers;
    private List<DbLocationInfo> dbLocationInfoList;
    private final NodeType type;

    NodeInfo(NodeType type) {
        this.type = type;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public List<String> getDatabases() {
        return databases;
    }

    public void setDatabases(List<String> databases) {
        this.databases = databases;
    }

    public boolean addDatabase(String dbName) {
        boolean isSuccess = false;
        if (databases == null) {
            databases = new ArrayList<String>();
            databases.add(dbName);
            isSuccess = true;
        } else if (!findDb(dbName)) {
            databases.add(dbName);
            isSuccess = true;
        }
        return isSuccess;
    }

    public boolean findDb(String dbName) {
        if (databases == null) {
            return false;
        }
        for (String s : databases) {
            if (s.equals(dbName)) {
                return true;
            }
        }
        return false;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String nodeName) {
        this.hostName = nodeName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getBrokerInfo() {
        return brokerInfo;
    }

    public void setBrokerInfo(String brokerInfo) {
        this.brokerInfo = brokerInfo;
    }

    public String getCmConnectName() {
        return cmConnectName;
    }

    public void setCmConnectName(String cmConnectName) {
        this.cmConnectName = cmConnectName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public Set<NodeInfo> getRemoteBrokers() {
        return remoteBrokers;
    }

    public void setRemoteBrokers(Set<NodeInfo> remoteBrokers) {
        this.remoteBrokers = remoteBrokers;
    }

    public void AddRemoteBroker(NodeInfo node) {
        if (node != null && NodeType.BROKER == node.getType()) {
            if (remoteBrokers == null) {
                remoteBrokers = new HashSet<NodeInfo>();
            }
            remoteBrokers.add(node);
        }
    }

    public List<DbLocationInfo> getDbLocationInfoList() {
        return dbLocationInfoList;
    }

    public void setDbLocationInfoList(List<DbLocationInfo> dbLocationInfoList) {
        this.dbLocationInfoList = dbLocationInfoList;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(serviceName).append(" - ");
        if (databases != null && databases.size() > 0) {
            for (String dbName : databases) {
                sb.append(dbName).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(" - ");
        sb.append(hostName).append(" - ");
        sb.append(ip).append(" - ");
        if (remoteBrokers != null && remoteBrokers.size() > 0) {
            sb.append("Local[").append(brokerInfo).append("], ");
            sb.append("Remote[");
            for (NodeInfo node : remoteBrokers) {
                sb.append(node.getIp()).append(":").append(node.getBrokerInfo()).append(", ");
            }
            sb.delete(sb.length() - 2, sb.length()).append("]").append(" - ");

        } else {
            sb.append(brokerInfo).append(" - ");
        }
        sb.append(cmConnectName).append(" - ");
        sb.append(status);
        return sb.toString();
    }

    public NodeInfo clone(NodeInfo node) {
        try {
            return (NodeInfo) super.clone();
        } catch (CloneNotSupportedException e) {
        }
        return null;
    }

    public NodeType getType() {
        return type;
    }
}
