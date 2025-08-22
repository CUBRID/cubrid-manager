/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *  - Neither the name of the <ORGANIZATION> nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
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
package com.cubrid.cubridmanager.core.replication.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The replication information POJO
 *
 * @author pangqiren
 * @version 1.0 - 2009-12-29 created by pangqiren
 */
public class ReplicationInfo {

    private List<MasterInfo> masterList = new ArrayList<MasterInfo>();
    private List<SlaveInfo> slaveList = new ArrayList<SlaveInfo>();
    private DistributorInfo distInfo = null;

    public List<MasterInfo> getMasterList() {
        return masterList;
    }

    public void setMasterList(List<MasterInfo> masterList) {
        this.masterList = masterList;
    }

    /**
     * Add master database information
     *
     * @param masterInfo the MasterInfo obj
     */
    public void addMasterInfo(MasterInfo masterInfo) {
        if (masterList == null) {
            masterList = new ArrayList<MasterInfo>();
        }
        masterList.add(masterInfo);
    }

    public List<SlaveInfo> getSlaveList() {
        return slaveList;
    }

    public void setSlaveList(List<SlaveInfo> slaveList) {
        this.slaveList = slaveList;
    }

    /**
     * Add slave database information
     *
     * @param slaveInfo the SlaveInfo obj
     */
    public void addSlaveInfo(SlaveInfo slaveInfo) {
        if (slaveList == null) {
            slaveList = new ArrayList<SlaveInfo>();
        }
        slaveList.add(slaveInfo);
    }

    public DistributorInfo getDistInfo() {
        return distInfo;
    }

    public void setDistInfo(DistributorInfo distInfo) {
        this.distInfo = distInfo;
    }
}
