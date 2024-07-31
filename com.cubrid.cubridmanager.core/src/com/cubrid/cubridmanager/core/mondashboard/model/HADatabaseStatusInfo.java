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
 * The database status information in HA mode
 *
 * @author pangqiren
 * @version 1.0 - 2010-6-7 created by pangqiren
 */
public class HADatabaseStatusInfo {

    private String dbName;
    private DBStatusType statusType = DBStatusType.UNKNOWN;
    private String errorInfo;
    private int delay;
    private List<DbProcessStatusInfo> copyLogDbProcessStatusList;
    private List<DbProcessStatusInfo> applyLogDbProcessStatusList;
    private DbProcessStatusInfo dbServerProcessStatus;
    private int insertCounter;
    private int updateCounter;
    private int deleteCounter;
    private int commitCounter;
    private int failCounter;
    private int cpuUsage;
    private int memUsage;
    private HAHostStatusInfo haHostStatusInfo;

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public DBStatusType getStatusType() {
        return statusType;
    }

    public void setStatusType(DBStatusType statusType) {
        this.statusType = statusType;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public List<DbProcessStatusInfo> getCopyLogDbProcessStatusList() {
        return copyLogDbProcessStatusList;
    }

    public void setCopyLogDbProcessStatusList(
            List<DbProcessStatusInfo> copyLogDbProcessStatusList) {
        this.copyLogDbProcessStatusList = copyLogDbProcessStatusList;
    }

    /**
     * Add copy log database process status information
     *
     * @param statusInfo DbProcessStatusInfo
     */
    public void addCopyLogDbProcessStatus(DbProcessStatusInfo statusInfo) {
        if (copyLogDbProcessStatusList == null) {
            copyLogDbProcessStatusList = new ArrayList<DbProcessStatusInfo>();
        }
        copyLogDbProcessStatusList.add(statusInfo);
    }

    public List<DbProcessStatusInfo> getApplyLogDbProcessStatusList() {
        return applyLogDbProcessStatusList;
    }

    public void setApplyLogDbProcessStatusList(
            List<DbProcessStatusInfo> applyLogDbProcessStatusList) {
        this.applyLogDbProcessStatusList = applyLogDbProcessStatusList;
    }

    /**
     * Add apply log database process status information
     *
     * @param statusInfo DbProcessStatusInfo
     */
    public void addApplyLogDbProcessStatus(DbProcessStatusInfo statusInfo) {
        if (applyLogDbProcessStatusList == null) {
            applyLogDbProcessStatusList = new ArrayList<DbProcessStatusInfo>();
        }
        applyLogDbProcessStatusList.add(statusInfo);
    }

    public DbProcessStatusInfo getDbServerProcessStatus() {
        return dbServerProcessStatus;
    }

    public void setDbServerProcessStatus(DbProcessStatusInfo dbServerProcessStatus) {
        this.dbServerProcessStatus = dbServerProcessStatus;
    }

    public int getInsertCounter() {
        return insertCounter;
    }

    public void setInsertCounter(int insertCounter) {
        this.insertCounter = insertCounter;
    }

    public int getUpdateCounter() {
        return updateCounter;
    }

    public void setUpdateCounter(int updateCounter) {
        this.updateCounter = updateCounter;
    }

    public int getDeleteCounter() {
        return deleteCounter;
    }

    public void setDeleteCounter(int deleteCounter) {
        this.deleteCounter = deleteCounter;
    }

    public int getCommitCounter() {
        return commitCounter;
    }

    public void setCommitCounter(int commitCounter) {
        this.commitCounter = commitCounter;
    }

    public int getFailCounter() {
        return failCounter;
    }

    public void setFailCounter(int failCounter) {
        this.failCounter = failCounter;
    }

    public HAHostStatusInfo getHaHostStatusInfo() {
        return haHostStatusInfo;
    }

    public void setHaHostStatusInfo(HAHostStatusInfo haHostStatusInfo) {
        this.haHostStatusInfo = haHostStatusInfo;
    }

    /**
     * get database's cpu usage.
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
     * get database's memory usage.
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
}
