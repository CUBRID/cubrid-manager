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
package com.cubrid.cubridmanager.core.monstatistic.model;

import com.cubrid.cubridmanager.core.common.model.ServerInfo;

public class StatisticChartHost {

    private String ip;
    private int port;
    private String user;
    private String password;

    private String cubridServerId;

    private ServerInfo serverInfo;

    private String metric;
    private String dbName;
    private String volName;
    private String brokerName;

    public StatisticChartHost(String ip, int port, String user, String password) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public StatisticChartHost(String cubridServerId) {
        this.cubridServerId = cubridServerId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCubridServerId() {
        return cubridServerId;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getVolName() {
        return volName;
    }

    public void setVolName(String volName) {
        this.volName = volName;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof StatisticChartHost)) {
            return false;
        }
        // first, check whether same CUBRID host node
        boolean isSameHost = false;
        StatisticChartHost hostItem = (StatisticChartHost) obj;
        if (this.cubridServerId != null) {
            // TODO: better to use HostNodePersistManager to check
            // whether cubridServerId and ip/port/user/password specify the same host
            isSameHost = this.cubridServerId.equals(hostItem.getCubridServerId());
        } else if (this.ip != null
                && this.ip.equals(hostItem.getIp())
                && this.port == hostItem.getPort()
                && this.user != null
                && this.user.equals(hostItem.getUser())
                && this.password != null
                && this.password.equals(hostItem.getPassword())) {
            isSameHost = true;
        } else if (this.cubridServerId == null
                && hostItem.getCubridServerId() == null
                && this.ip == null
                && hostItem.getIp() == null
                && this.port == hostItem.getPort()
                && this.user == null
                && hostItem.getUser() == null
                && this.password == null
                && hostItem.getPassword() == null) {
            isSameHost = true;
        }
        if (!isSameHost) {
            return false;
        }

        // second, check metric and other properties
        if (!(this.metric != null && this.metric.equals(hostItem.getMetric()))
                && !(this.metric == null && hostItem.getMetric() == null)) {
            return false;
        }
        if (!(this.dbName != null && this.dbName.equals(hostItem.getDbName()))
                && !(this.dbName == null && hostItem.getDbName() == null)) {
            return false;
        }
        if (!(this.volName != null && this.volName.equals(hostItem.getVolName()))
                && !(this.volName == null && hostItem.getVolName() == null)) {
            return false;
        }
        if (!(this.brokerName != null && this.brokerName.equals(hostItem.getBrokerName()))
                && !(this.brokerName == null && hostItem.getBrokerName() == null)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode += cubridServerId != null ? cubridServerId.hashCode() : 0;
        hashCode += ip != null ? ip.hashCode() : 0;
        hashCode += port;
        hashCode += user != null ? user.hashCode() : 0;
        hashCode += password != null ? password.hashCode() : 0;
        hashCode += metric != null ? metric.hashCode() : 0;
        hashCode += dbName != null ? dbName.hashCode() : 0;
        hashCode += volName != null ? volName.hashCode() : 0;
        hashCode += brokerName != null ? brokerName.hashCode() : 0;
        return hashCode;
    }
}
