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

import com.cubrid.cubridmanager.core.broker.model.BrokerInfo;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfoList;
import com.cubrid.cubridmanager.core.mondashboard.model.HAHostStatusInfo;
import java.util.List;

public class HaNode extends NodeInfo { // FIXME description
    private List<HAHostStatusInfo> hostStatusInfoList;
    private BrokerInfoList brokerInfoList;

    public HaNode(NodeType type) {
        super(type);
    }

    public void setHostStatusInfoList(List<HAHostStatusInfo> hostStatusInfoList) {
        this.hostStatusInfoList = hostStatusInfoList;
    }

    public List<HAHostStatusInfo> getHostStatusInfoList() {
        return hostStatusInfoList;
    }

    public BrokerInfoList getBrokerInfoList() {
        return brokerInfoList;
    }

    public void setBrokerInfoList(BrokerInfoList brokerInfoList) {
        this.brokerInfoList = brokerInfoList;
    }

    public String getBrokerInfo() {
        String result = super.getBrokerInfo();
        if (result == null) {
            genBrokerInfo();
            result = super.getBrokerInfo();
        }
        return result;
    }

    public void genBrokerInfo() {
        StringBuilder sb = new StringBuilder();
        if (brokerInfoList != null) {
            int count = 0;
            for (BrokerInfo brokerInfo : brokerInfoList.getBrokerInfoList()) {
                if (brokerInfo != null) {
                    sb.append(brokerInfo.getPort()).append(":");
                    sb.append(brokerInfo.getState()).append(", ");
                    count++;
                }
            }
            if (count > 0) {
                sb.delete(sb.length() - 2, sb.length());
            }
        }
        super.setBrokerInfo(sb.toString());
    }

    public void buildStatus(String serviceStatus) {
        switch (getType()) {
            case MASTER:
                setStatus("HA Master (active)");
                break;
            case SLAVE:
                if ("ON".equalsIgnoreCase(serviceStatus)) {
                    setStatus("HA Slave (standby)");
                } else {
                    setStatus("HA Slave (standby)(OFF)");
                }
                break;
            case REPLICA:
                if ("ON".equalsIgnoreCase(serviceStatus)) {
                    setStatus("HA Replica (stop)");
                } else {
                    setStatus("HA Replica (stop)(OFF)");
                }
                break;
        }
    }

    public String toString() {
        genBrokerInfo();
        return super.toString();
    }
}
