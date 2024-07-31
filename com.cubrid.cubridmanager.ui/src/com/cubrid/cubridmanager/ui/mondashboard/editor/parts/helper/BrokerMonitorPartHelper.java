/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the <ORGANIZATION> nor the names of its contributors
 *   may be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */
package com.cubrid.cubridmanager.ui.mondashboard.editor.parts.helper;

import com.cubrid.cubridmanager.core.broker.model.BrokerInfo;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataChangedEvent;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataGenerator;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataGeneratorPool;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataProvider;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataUpdateListener;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.BrokerNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HANode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.spi.util.HAUtil;

/**
 * It is responsible to generate the broker node related data
 *
 * @author pangqiren
 * @version 1.0 - 2010-8-19 created by pangqiren
 */
public class BrokerMonitorPartHelper implements DataUpdateListener {

    private BrokerNode brokerNode;
    private DataGenerator generator;

    /**
     * Set BrokerNode
     *
     * @param brokerNode The BrokerNode
     */
    public void setBrokerNode(BrokerNode brokerNode) {
        this.brokerNode = brokerNode;
    }

    /** Generate the DataUpdateListener */
    public void activate() {
        if (brokerNode == null) {
            return;
        }
        HostNode hostNode = brokerNode.getParent();
        String generatorName =
                hostNode.getUserName() + "@" + hostNode.getIp() + ":" + hostNode.getPort();
        generator =
                DataGeneratorPool.getInstance().getDataGenerator(generatorName, new DataProvider());
        generator.addDataUpdateListener(this);
    }

    /** Deregister self as a PropertyChangeListener from Model. */
    public void deactivate() {
        if (generator != null) {
            generator.removeDataUpdateListener(this);
        }
    }

    public HANode getModel() {
        return brokerNode;
    }

    /**
     * Perform the update data
     *
     * @param dataChangedEvent the given event including newest data
     */
    public void performUpdate(DataChangedEvent dataChangedEvent) {

        if (!brokerNode.getParent().isConnected()) {
            brokerNode.setBrokerInfo(null);
            brokerNode.setBrokerDiagData(null);
            brokerNode.setBrokerStatusInfos(null);
            return;
        }

        String brokerName = brokerNode.getBrokerName();
        BrokerInfo brokerInfo =
                HAUtil.getBrokerInfo(
                        dataChangedEvent.getBrokerInfosMap().get(brokerName), brokerName);
        brokerNode.setBrokerInfo(brokerInfo);
        brokerNode.setBrokerDiagData(dataChangedEvent.getBrokerDiagDataMap().get(brokerName));
        brokerNode.setBrokerStatusInfos(dataChangedEvent.getBrokerStatusInfosMap().get(brokerName));
    }
}
