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
package com.cubrid.cubridmanager.core.mondashboard.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The host status information in HA mode
 *
 * @author pangqiren
 * @version 1.0 - 2010-6-9 created by pangqiren
 */
public class HAHostStatusInfo {

    private String ip;
    private String hostName;
    private String priority;
    private HostStatusType statusType = HostStatusType.UNKNOWN;
    private int cpuUsage;
    private int memUsage;
    private int ioWait;
    private HAHostStatusInfo masterHostStatusInfo;
    private List<HAHostStatusInfo> slaveHostStatusInfoList = new ArrayList<HAHostStatusInfo>();
    private List<HADatabaseStatusInfo> dbStatusList = new ArrayList<HADatabaseStatusInfo>();

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public HostStatusType getStatusType() {
        return statusType;
    }

    public void setStatusType(HostStatusType statusType) {
        this.statusType = statusType;
    }

    public List<HADatabaseStatusInfo> getDbStatusList() {
        return dbStatusList;
    }

    public void setDbStatusList(List<HADatabaseStatusInfo> dbStatusList) {
        this.dbStatusList = dbStatusList;
    }

    /**
     * Add HADatabaseStatus
     *
     * @param haDbStatus The HADatabaseStatus
     */
    public void addHADatabaseStatus(HADatabaseStatusInfo haDbStatus) {
        dbStatusList.add(haDbStatus);
    }

    /**
     * get host's cpu usage.
     *
     * @return the cpuUsage
     */
    public int getCpuUsage() {
        return cpuUsage;
    }

    /** @param cpuUsage the cpuUsage to set */
    public void setCpuUsage(int cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    /**
     * get host memory usage.
     *
     * @return the memUsage
     */
    public int getMemUsage() {
        return memUsage;
    }

    /** @param memUsage the memUsage to set */
    public void setMemUsage(int memUsage) {
        this.memUsage = memUsage;
    }

    /**
     * get host io wait
     *
     * @return the ioWait
     */
    public int getIoWait() {
        return ioWait;
    }

    /** @param ioWait the ioWait to set */
    public void setIoWait(int ioWait) {
        this.ioWait = ioWait;
    }

    public HAHostStatusInfo getMasterHostStatusInfo() {
        return masterHostStatusInfo;
    }

    public void setMasterHostStatusInfo(HAHostStatusInfo masterHostStatusInfo) {
        this.masterHostStatusInfo = masterHostStatusInfo;
    }

    public List<HAHostStatusInfo> getSlaveHostStatusInfoList() {
        return slaveHostStatusInfoList;
    }

    public void setSlaveHostStatusInfoList(List<HAHostStatusInfo> slaveHostStatusInfoList) {
        this.slaveHostStatusInfoList = slaveHostStatusInfoList;
    }
}
