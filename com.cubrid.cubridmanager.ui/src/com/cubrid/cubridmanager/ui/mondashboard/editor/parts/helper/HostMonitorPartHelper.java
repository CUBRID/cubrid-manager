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

import com.cubrid.cubridmanager.core.mondashboard.model.HAHostStatusInfo;
import com.cubrid.cubridmanager.core.monitoring.model.HostStatEnum;
import com.cubrid.cubridmanager.core.monitoring.model.IDiagPara;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataChangedEvent;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataGenerator;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataGeneratorPool;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataProvider;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataUpdateListener;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.MondashDataResult;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HANode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.spi.util.HAUtil;
import java.util.Map;
import java.util.Set;

/**
 * This type is responsible for provide task executor for the type HostMonitorPart
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-7-1 created by lizhiqiang
 */
public final class HostMonitorPartHelper implements DataUpdateListener {

    private HostNode hostNode;
    private DataGenerator generator;

    /**
     * Set the HostNode object
     *
     * @param hn the hn to set
     */
    public void setHostNode(HostNode hn) {
        this.hostNode = hn;
    }

    /** Active this listener */
    public void active() {
        if (hostNode == null) {
            return;
        }
        DataGeneratorPool pool = DataGeneratorPool.getInstance();
        String generatorName =
                hostNode.getUserName() + "@" + hostNode.getIp() + ":" + hostNode.getPort();
        generator = pool.getDataGenerator(generatorName, new DataProvider());
        generator.addDataUpdateListener(this);
    }

    /** Deactivate this listener */
    public void deactivate() {
        if (generator != null) {
            generator.removeDataUpdateListener(this);
        }
    }

    public HANode getModel() {
        return hostNode;
    }

    /**
     * Perform the update data
     *
     * @param dataChangedEvent the given event including newest data
     */
    public void performUpdate(DataChangedEvent dataChangedEvent) {
        DataProvider dataProvider = (DataProvider) dataChangedEvent.getSource();
        if (dataProvider != null && dataProvider.getServerInfo() != null) {
            hostNode.setConnected(dataProvider.getServerInfo().isConnected());
        }

        if (!hostNode.isConnected()) {
            hostNode.setConnected(false);
            HAHostStatusInfo haHostStatusInfo = new HAHostStatusInfo();
            haHostStatusInfo.setIp(hostNode.getIp());
            hostNode.setHostStatusInfo(haHostStatusInfo);
            return;
        }

        HAHostStatusInfo haHostStatusInfo =
                HAUtil.getHostStatusInfo(
                        dataChangedEvent.getHaHostStatusInfoList(), hostNode.getIp());
        if (haHostStatusInfo == null) {
            haHostStatusInfo = HAUtil.getHAHostStatusInfo(hostNode.getServerInfo());
            if (haHostStatusInfo == null) {
                haHostStatusInfo = new HAHostStatusInfo();
                haHostStatusInfo.setIp(hostNode.getIp());
            }
        }
        Set<MondashDataResult> set = dataChangedEvent.getResultSet();
        Map<IDiagPara, String> map = null;
        for (MondashDataResult result : set) {
            if (generator.getName().equals(result.getName())) {
                map = result.getUpdateMap();
                break;
            }
        }
        int[] value = getValues(map);
        haHostStatusInfo.setCpuUsage(value[0]);
        haHostStatusInfo.setMemUsage(value[1]);
        haHostStatusInfo.setIoWait(value[2]);
        hostNode.setHostStatusInfo(haHostStatusInfo);
    }

    /**
     * Perform the update data
     *
     * @param map the given map including newest data
     * @return int[]
     */
    private int[] getValues(Map<IDiagPara, String> map) {
        int[] usageValue = new int[] {0, 0, 0};
        if (map != null) {
            for (Map.Entry<IDiagPara, String> entry : map.entrySet()) {
                if (entry.getKey() instanceof HostStatEnum) {
                    HostStatEnum hostStatEnum = (HostStatEnum) entry.getKey();
                    switch (hostStatEnum) {
                        case USER:
                            usageValue[0] += Integer.parseInt(entry.getValue());
                            break;
                        case KERNEL:
                            usageValue[0] += Integer.parseInt(entry.getValue());
                            break;
                        case MEMPHY_PERCENT:
                            usageValue[1] = Integer.parseInt(entry.getValue());
                            break;
                        case IOWAIT:
                            usageValue[2] = Integer.parseInt(entry.getValue());
                            break;
                        default:
                    }
                }
            }
        }
        return usageValue;
    }
}
