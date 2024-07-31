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
package com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher;

import com.cubrid.cubridmanager.core.broker.model.BrokerInfos;
import com.cubrid.cubridmanager.core.broker.model.BrokerStatusInfos;
import com.cubrid.cubridmanager.core.mondashboard.model.HADatabaseStatusInfo;
import com.cubrid.cubridmanager.core.mondashboard.model.HAHostStatusInfo;
import com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Data changed event
 *
 * @author pangqiren
 * @version 1.0 - 2010-7-5 created by pangqiren
 */
public class DataChangedEvent extends EventObject {

    private static final long serialVersionUID = -437300357155033616L;
    private Set<MondashDataResult> resultSet;
    private List<HAHostStatusInfo> haHostStatusInfoList;
    private List<HADatabaseStatusInfo> dbStatusInfoList;

    private Map<String, BrokerInfos> brokerInfosMap = new HashMap<String, BrokerInfos>();
    private Map<String, BrokerStatusInfos> brokerStatusInfosMap =
            new HashMap<String, BrokerStatusInfos>();
    private Map<String, BrokerDiagData> brokerDiagDataMap = new HashMap<String, BrokerDiagData>();

    /**
     * The constructor
     *
     * @param source The Object
     * @param resultSet The Set<MondashDataResult>
     * @param haHostStatusInfoList The List<HAHostStatusInfo>
     */
    public DataChangedEvent(Object source) {
        super(source);
    }

    public Set<MondashDataResult> getResultSet() {
        return resultSet;
    }

    public void setResultSet(Set<MondashDataResult> resultSet) {
        this.resultSet = resultSet;
    }

    public List<HAHostStatusInfo> getHaHostStatusInfoList() {
        return haHostStatusInfoList;
    }

    public void setHaHostStatusInfoList(List<HAHostStatusInfo> haHostStatusInfoList) {
        this.haHostStatusInfoList = haHostStatusInfoList;
    }

    public Map<String, BrokerInfos> getBrokerInfosMap() {
        return brokerInfosMap;
    }

    public void setBrokerInfosMap(Map<String, BrokerInfos> brokerInfosMap) {
        this.brokerInfosMap = brokerInfosMap;
    }

    public Map<String, BrokerStatusInfos> getBrokerStatusInfosMap() {
        return brokerStatusInfosMap;
    }

    public void setBrokerStatusInfosMap(Map<String, BrokerStatusInfos> brokerStatusInfosMap) {
        this.brokerStatusInfosMap = brokerStatusInfosMap;
    }

    public Map<String, BrokerDiagData> getBrokerDiagDataMap() {
        return brokerDiagDataMap;
    }

    public void setBrokerDiagDataMap(Map<String, BrokerDiagData> brokerDiagDataMap) {
        this.brokerDiagDataMap = brokerDiagDataMap;
    }

    public List<HADatabaseStatusInfo> getDbStatusInfoList() {
        return dbStatusInfoList;
    }

    public void setDbStatusInfoList(List<HADatabaseStatusInfo> dbStatusInfoList) {
        this.dbStatusInfoList = dbStatusInfoList;
    }
}
