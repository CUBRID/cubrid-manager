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

import com.cubrid.cubridmanager.core.broker.model.BrokerInfo;
import com.cubrid.cubridmanager.core.broker.model.BrokerStatusInfos;
import com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData;
import org.eclipse.draw2d.geometry.Point;

/**
 * Broker node mode class
 *
 * @author pangqiren
 * @version 1.0 - 2010-8-17 created by pangqiren
 */
public class BrokerNode extends HANode {

    public static final String PROP_BROKER_STATUS = "PROP_BROKER_STATUS";

    private String brokerName;
    private BrokerInfo brokerInfo;
    private BrokerStatusInfos brokerStatusInfos;
    private BrokerDiagData diagStatusResult;
    private HostNode parent;

    private final Point clientsLocation = new Point(0, 0);
    private final Point databasesLocation = new Point(0, 0);

    public BrokerNode() {
        size.height = 98;
        size.width = 125;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public BrokerInfo getBrokerInfo() {
        return brokerInfo;
    }

    /**
     * Set broker info.
     *
     * @param brokerInfo BrokerInfo
     */
    public void setBrokerInfo(BrokerInfo brokerInfo) {
        BrokerInfo old = this.brokerInfo;
        this.brokerInfo = brokerInfo;
        this.firePropertyChange(PROP_BROKER_STATUS, old, brokerInfo);
    }

    public BrokerStatusInfos getBrokerStatusInfos() {
        return brokerStatusInfos;
    }

    /**
     * Set broker status.
     *
     * @param brokerStatusInfos BrokerStatusInfos
     */
    public void setBrokerStatusInfos(BrokerStatusInfos brokerStatusInfos) {
        BrokerStatusInfos old = this.brokerStatusInfos;
        this.brokerStatusInfos = brokerStatusInfos;
        this.firePropertyChange(PROP_BROKER_STATUS, old, brokerStatusInfos);
    }

    public BrokerDiagData getBrokerDiagData() {
        return diagStatusResult;
    }

    /**
     * Set BrokerDiagData
     *
     * @param diagStatusResult BrokerDiagData
     */
    public void setBrokerDiagData(BrokerDiagData diagStatusResult) {
        BrokerDiagData old = this.diagStatusResult;
        this.diagStatusResult = diagStatusResult;
        this.firePropertyChange(PROP_BROKER_STATUS, old, diagStatusResult);
    }

    public HostNode getParent() {
        return parent;
    }

    public void setParent(HostNode parent) {
        this.parent = parent;
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
        } else if (!(obj instanceof BrokerNode)) {
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
                .append(brokerName);
        return sb.toString();
    }

    /**
     * the clients Location
     *
     * @return the clientsLocation
     */
    public Point getClientsLocation() {
        return clientsLocation.getCopy();
    }

    /** @param clientsLocation the clientsLocation to set */
    public void setClientsLocation(Point clientsLocation) {
        this.clientsLocation.x = clientsLocation.x;
        this.clientsLocation.y = clientsLocation.y;
    }

    /**
     * the databases Location
     *
     * @return the databasesLocation
     */
    public Point getDatabasesLocation() {
        return databasesLocation.getCopy();
    }

    /** @param databasesLocation the databasesLocation to set */
    public void setDatabasesLocation(Point databasesLocation) {
        this.databasesLocation.x = databasesLocation.x;
        this.databasesLocation.y = databasesLocation.y;
    }
}
