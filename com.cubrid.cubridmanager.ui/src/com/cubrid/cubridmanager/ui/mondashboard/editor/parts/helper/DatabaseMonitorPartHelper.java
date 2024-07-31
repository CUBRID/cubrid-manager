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

import com.cubrid.cubridmanager.core.mondashboard.model.HADatabaseStatusInfo;
import com.cubrid.cubridmanager.core.monitoring.model.DbProcStatEnum;
import com.cubrid.cubridmanager.core.monitoring.model.HostStatEnum;
import com.cubrid.cubridmanager.core.monitoring.model.IDiagPara;
import com.cubrid.cubridmanager.core.monitoring.model.StandbyServerStatEnum;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataChangedEvent;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataGenerator;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataGeneratorPool;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataProvider;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataUpdateListener;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.MondashDataResult;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.DatabaseNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HANode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.spi.util.HAUtil;
import java.util.Map;
import java.util.Set;

/**
 * It is responsible to generate the database node related data
 *
 * @author pangqiren
 * @version 1.0 - 2010-7-2 created by pangqiren
 */
public class DatabaseMonitorPartHelper implements DataUpdateListener {

    private DatabaseNode dbNode;
    private DataGenerator generator;

    /**
     * Set DatabaseNode
     *
     * @param dbNode The DatabaseNode
     */
    public void setDbNode(DatabaseNode dbNode) {
        this.dbNode = dbNode;
    }

    /** Generate the DataUpdateListener */
    public void activate() {
        if (dbNode == null) {
            return;
        }
        HostNode hostNode = dbNode.getParent();
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
        return dbNode;
    }

    /**
     * Perform the update data
     *
     * @param dataChangedEvent the given event including newest data
     */
    public void performUpdate(DataChangedEvent dataChangedEvent) {

        if (!dbNode.getParent().isConnected()) {
            HADatabaseStatusInfo haDatabaseStatus = new HADatabaseStatusInfo();
            haDatabaseStatus.setDbName(dbNode.getDbName());
            dbNode.setHaDatabaseStatus(haDatabaseStatus);
            return;
        }

        Set<MondashDataResult> set = dataChangedEvent.getResultSet();
        String dbName = dbNode.getDbName();
        Map<IDiagPara, String> updateMap = null;
        String hostCpuTotal = null;
        String hostMemTotal = null;
        for (MondashDataResult result : set) {
            if (dbName.equals(result.getName())) {
                updateMap = result.getUpdateMap();
            } else if (generator.getName().equals(result.getName())) {
                Map<IDiagPara, String> hostMap = result.getUpdateMap();
                if (hostMap != null) {
                    hostCpuTotal = hostMap.get(HostStatEnum.CPU_TOTAL);
                    hostMemTotal = hostMap.get(HostStatEnum.MEMPHY_TOTAL);
                }
            }
        }
        int[] value =
                getValues(
                        updateMap,
                        hostCpuTotal == null ? "0" : hostCpuTotal,
                        hostMemTotal == null ? "0" : hostMemTotal);

        HADatabaseStatusInfo haDbStatusInfo =
                HAUtil.getDatabaseStatusInfo(
                        dataChangedEvent.getHaHostStatusInfoList(),
                        dbNode.getParent().getIp(),
                        dbNode.getDbName());
        if (haDbStatusInfo == null) {
            haDbStatusInfo =
                    HAUtil.getDatabaseStatusInfo(
                            dataChangedEvent.getDbStatusInfoList(), dbNode.getDbName());
            if (null == haDbStatusInfo) {
                haDbStatusInfo = new HADatabaseStatusInfo();
                haDbStatusInfo.setDbName(dbNode.getDbName());
            }
        }

        haDbStatusInfo.setCpuUsage(value[0]);
        haDbStatusInfo.setMemUsage(value[1]);
        haDbStatusInfo.setDelay(value[2]);
        dbNode.setHaDatabaseStatus(haDbStatusInfo);
    }

    /**
     * Perform the update data
     *
     * @param map Map<IDiagPara, String>
     * @param hostCpuTotalStr String
     * @param hostMemTotalStr String
     * @return int[]
     */
    private int[] getValues(
            Map<IDiagPara, String> map, String hostCpuTotalStr, String hostMemTotalStr) {
        int[] usageValue = new int[] {0, 0, 0};
        if (map != null) {
            String deltaCpuUser = map.get(DbProcStatEnum.DELTA_USER);
            String deltaCpuKernel = map.get(DbProcStatEnum.DELTA_KERNEL);
            Long deltaCpuUserLong = Long.parseLong(deltaCpuUser == null ? "0" : deltaCpuUser);
            Long dletaCpuKernelLong = Long.parseLong(deltaCpuKernel == null ? "0" : deltaCpuKernel);

            double hostCpuTotal = Long.parseLong(hostCpuTotalStr);
            if ("0".equals(hostCpuTotalStr)) {
                usageValue[0] = 0;
            } else {
                int userPercent = (int) (deltaCpuUserLong / hostCpuTotal * 100 + 0.5);
                int kernelPercent = (int) (dletaCpuKernelLong / hostCpuTotal * 100 + 0.5);
                usageValue[0] = userPercent + kernelPercent;
            }

            String memPhyUsed = map.get(DbProcStatEnum.MEM_PHYSICAL);
            double memPhyUsedLong = Long.parseLong(memPhyUsed == null ? "0" : memPhyUsed);
            double hostMemTotal = Long.parseLong(hostMemTotalStr);
            if ("0".equals(hostMemTotalStr)) {
                usageValue[1] = 0;
            } else {
                usageValue[1] = (int) (memPhyUsedLong / hostMemTotal * 100 + 0.5);
            }
            String delayTime = map.get(StandbyServerStatEnum.DELAY_TIME);
            usageValue[2] = Integer.parseInt(delayTime == null ? "0" : delayTime);
        }

        return usageValue;
    }
}
