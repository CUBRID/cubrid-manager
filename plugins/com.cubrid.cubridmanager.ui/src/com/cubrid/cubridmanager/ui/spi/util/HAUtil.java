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
package com.cubrid.cubridmanager.ui.spi.util;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfo;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfos;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.common.model.HAConfParaConstants;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.mondashboard.model.DBStatusType;
import com.cubrid.cubridmanager.core.mondashboard.model.DbProcessStatusInfo;
import com.cubrid.cubridmanager.core.mondashboard.model.HADatabaseStatusInfo;
import com.cubrid.cubridmanager.core.mondashboard.model.HAHostStatusInfo;
import com.cubrid.cubridmanager.core.mondashboard.model.HostStatusType;
import com.cubrid.cubridmanager.core.mondashboard.model.SyncModeType;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.BrokerNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.Dashboard;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.DatabaseNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HANode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HostNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

/**
 * HA related utility class
 *
 * @author pangqiren
 * @version 1.0 - 2010-6-22 created by pangqiren
 */
public final class HAUtil {

    private HAUtil() {}

    /**
     * Merge the host node list to dashboard
     *
     * @param dashboard The Dashboard
     * @param addedHostNodeList The addedHostNodeList
     */
    public static void mergeHostNode(Dashboard dashboard, List<HostNode> addedHostNodeList) {
        for (int i = 0; i < addedHostNodeList.size(); i++) {
            HostNode addedHostNode = addedHostNodeList.get(i);
            boolean isExist = false;
            List<HostNode> hostNodeList = dashboard.getHostNodeList();
            for (int j = 0; j < hostNodeList.size(); j++) {
                HostNode hostNode = hostNodeList.get(j);
                if (addedHostNode.equals(hostNode)) {
                    isExist = true;
                    hostNode.setPassword(addedHostNode.getPassword());
                    hostNode.setHostStatusInfo(addedHostNode.getHostStatusInfo());
                    mergeHostChildNode(hostNode, addedHostNode);
                    break;
                }
            }
            if (!isExist) {
                addedHostNode.setDbNodeList(addedHostNode.getCopyedDbNodeList());
                addedHostNode.setBrokerNodeList(addedHostNode.getCopyedBrokerNodeList());
                dashboard.addNode(addedHostNode);
            }
        }
    }

    /**
     * Merge the database node
     *
     * @param hostNode The HostNode
     * @param addedHostNode The HostNode
     */
    private static void mergeHostChildNode(HostNode hostNode, HostNode addedHostNode) {

        // merge database node
        List<DatabaseNode> dbNodeList = hostNode.getDbNodeList();
        List<DatabaseNode> addedDbNodeList = addedHostNode.getCopyedDbNodeList();
        for (int i = 0; i < addedDbNodeList.size(); i++) {
            DatabaseNode addDbNode = addedDbNodeList.get(i);
            if (dbNodeList.contains(addDbNode)) {
                DatabaseNode node = dbNodeList.get(dbNodeList.indexOf(addDbNode));
                node.setConnected(addDbNode.isConnected());
                node.setDbPassword(addDbNode.getDbPassword());
                node.setHaDatabaseStatus(addDbNode.getHaDatabaseStatus());
                for (int j = 0;
                        addDbNode.getIncomingConnections() != null
                                && j < addDbNode.getIncomingConnections().size();
                        j++) {
                    node.addInput(addDbNode.getIncomingConnections().get(j));
                }
                for (int j = 0;
                        addDbNode.getOutgoingConnections() != null
                                && j < addDbNode.getOutgoingConnections().size();
                        j++) {
                    node.addOutput(addDbNode.getOutgoingConnections().get(j));
                }
            } else {
                addDbNode.setParent(hostNode);
                hostNode.addDbNode(addDbNode);
            }
        }
        // merge broker node
        List<BrokerNode> brokerNodeList = hostNode.getBrokerNodeList();
        List<BrokerNode> addedBrokerNodeList = addedHostNode.getCopyedBrokerNodeList();
        for (int i = 0; i < addedBrokerNodeList.size(); i++) {
            BrokerNode addedBrokerNode = addedBrokerNodeList.get(i);
            if (brokerNodeList.contains(addedBrokerNode)) {
                BrokerNode node = brokerNodeList.get(brokerNodeList.indexOf(addedBrokerNode));
                node.setBrokerInfo(addedBrokerNode.getBrokerInfo());

            } else {
                addedBrokerNode.setParent(hostNode);
                hostNode.addBrokerNode(addedBrokerNode);
            }
        }
    }

    /**
     * Merge the host node list
     *
     * @param hostNodeList The List<HostNode>
     * @param addedHostNodeList The addedHostNodeList
     */
    public static void mergeHostNode(
            List<HostNode> hostNodeList, List<HostNode> addedHostNodeList) {
        for (int i = 0; i < addedHostNodeList.size(); i++) {
            HostNode addedHostNode = addedHostNodeList.get(i);
            boolean isExist = false;
            for (int j = 0; j < hostNodeList.size(); j++) {
                HostNode hostNode = hostNodeList.get(j);
                if (addedHostNode.equals(hostNode)) {
                    isExist = true;
                    hostNode.setPassword(addedHostNode.getPassword());
                    hostNode.setHostStatusInfo(addedHostNode.getHostStatusInfo());
                    mergeCopyedHostChildNode(hostNode, addedHostNode);
                }
            }
            if (!isExist) {
                hostNodeList.add(addedHostNode);
            }
        }
    }

    /**
     * Merge the database node
     *
     * @param hostNode The HostNode
     * @param addedHostNode The HostNode
     */
    private static void mergeCopyedHostChildNode(HostNode hostNode, HostNode addedHostNode) {
        List<HANode> haNodeList = hostNode.getCopyedHaNodeList();
        // merge database node
        List<DatabaseNode> addedDbNodeList = addedHostNode.getCopyedDbNodeList();
        for (int i = 0; i < addedDbNodeList.size(); i++) {
            DatabaseNode addDbNode = addedDbNodeList.get(i);
            addDbNode.setParent(hostNode);
            if (haNodeList.contains(addDbNode)) {
                haNodeList.remove(addDbNode);
                haNodeList.add(addDbNode);
            } else {
                haNodeList.add(addDbNode);
            }
        }
        // merge broker node
        List<BrokerNode> addedBrokerNodeList = addedHostNode.getCopyedBrokerNodeList();
        for (int i = 0; i < addedBrokerNodeList.size(); i++) {
            BrokerNode addBrokerNode = addedBrokerNodeList.get(i);
            addBrokerNode.setParent(hostNode);
            if (haNodeList.contains(addBrokerNode)) {
                haNodeList.remove(addBrokerNode);
                haNodeList.add(addBrokerNode);
            } else {
                haNodeList.add(addBrokerNode);
            }
        }
    }

    private static final int WIDTH_DISTANCE = 80; // the distance between the database
    private static final int HEIGHT_DISTANCE = 80; // the distance between the database and the host
    private static final int LEFT_DISTANCE = 80; // the left distance
    private static final int TOP_DISTANCE = 80; // the top distance

    /**
     * Calculate the location
     *
     * @param hostNodeList List<HostNode>
     * @param maxY The max y point
     */
    private static void calcLocation(List<HostNode> hostNodeList, int maxY) {
        int hostX = LEFT_DISTANCE;
        int hostY = TOP_DISTANCE + maxY;
        for (HostNode hostNode : hostNodeList) {
            Dimension hostDimension = hostNode.getSize();
            int nodeHeight = 0;
            List<HANode> haNodeList = new ArrayList<HANode>();
            haNodeList.addAll(hostNode.getDbNodeList());
            haNodeList.addAll(hostNode.getBrokerNodeList());
            int size = haNodeList.size();
            if (size > 0) {
                int dbX = LEFT_DISTANCE;
                int dbY = hostY + hostDimension.height + HEIGHT_DISTANCE;
                for (int i = 0; i < size; i++) {
                    HANode haNode = haNodeList.get(i);
                    Dimension dbDimension = haNode.getSize();
                    if (dbDimension.height > nodeHeight) {
                        nodeHeight = dbDimension.height;
                    }
                    Point dbLocation = new Point(dbX, dbY);
                    haNode.setLocation(dbLocation);
                    dbX = dbX + dbDimension.width + WIDTH_DISTANCE;
                }
                hostX = dbX / 2 - hostDimension.width / 2;
            }
            hostNode.setLocation(new Point(hostX, hostY));
            if (nodeHeight == 0) {
                hostY = hostY + hostDimension.height + TOP_DISTANCE;
            } else {
                hostY = hostY + hostDimension.height + HEIGHT_DISTANCE + nodeHeight + TOP_DISTANCE;
            }
        }
    }

    /**
     * Calculate the location
     *
     * @param hostNodeList List<HostNode>
     */
    public static void calcLocation(List<HostNode> hostNodeList) {
        Point zeroLocation = new Point(0, 0);
        List<HostNode> zeroPointHostList = new ArrayList<HostNode>();
        List<HostNode> noZeroPointHostList = new ArrayList<HostNode>();
        for (HostNode hostNode : hostNodeList) {
            Point location = hostNode.getLocation();
            if (zeroLocation.equals(location)) {
                zeroPointHostList.add(hostNode);
            } else {
                noZeroPointHostList.add(hostNode);
            }
        }

        int hostMaxY = 0;
        for (HostNode hostNode : noZeroPointHostList) {
            Point hostLocation = hostNode.getLocation();
            int tY = hostLocation.y + hostNode.getSize().height;
            if (tY > hostMaxY) {
                hostMaxY = tY;
            }
            int nodeMaxY = hostLocation.y + hostNode.getSize().height + HEIGHT_DISTANCE;
            int nodeMaxX = hostLocation.x + hostNode.getSize().width;
            List<HANode> haNodeList = new ArrayList<HANode>();
            haNodeList.addAll(hostNode.getDbNodeList());
            haNodeList.addAll(hostNode.getBrokerNodeList());
            List<HANode> zeroPointChildList = new ArrayList<HANode>();
            for (HANode haNode : haNodeList) {
                if (haNode.getLocation().equals(zeroLocation)) {
                    zeroPointChildList.add(haNode);
                } else {
                    int tX = haNode.getLocation().x + haNode.getSize().width;
                    if (tX > nodeMaxX) {
                        nodeMaxX = tX;
                    }
                    tY = haNode.getLocation().y;
                    if (tY > nodeMaxY) {
                        nodeMaxY = tY;
                    }
                    tY = haNode.getLocation().y + haNode.getSize().height;
                    if (tY > hostMaxY) {
                        hostMaxY = tY;
                    }
                }
            }
            nodeMaxX = nodeMaxX + WIDTH_DISTANCE;
            for (HANode haNode : zeroPointChildList) {
                haNode.setLocation(new Point(nodeMaxX, nodeMaxY));
                nodeMaxX = nodeMaxX + haNode.getSize().width + WIDTH_DISTANCE;
            }
        }

        if (!zeroPointHostList.isEmpty()) {
            calcLocation(zeroPointHostList, hostMaxY);
        }
    }

    /**
     * Get HADatabaseStatusInfo object and handle with the status type
     *
     * @param dbName The String
     * @param haHostStatusInfo The HAHostStatusInfo
     * @param serverInfo The ServerInfo
     * @return The HADatabaseStatusInfo
     */
    public static HADatabaseStatusInfo getHADatabaseStatusInfo(
            String dbName, HAHostStatusInfo haHostStatusInfo, ServerInfo serverInfo) {

        if (haHostStatusInfo != null && haHostStatusInfo.getDbStatusList() != null) {
            for (HADatabaseStatusInfo dbStatusInfo : haHostStatusInfo.getDbStatusList()) {
                if (dbName.equals(dbStatusInfo.getDbName())) {
                    return dbStatusInfo;
                }
            }
        }
        HADatabaseStatusInfo haDbStatusInfo = new HADatabaseStatusInfo();
        haDbStatusInfo.setDbName(dbName);
        DatabaseInfo dbInfo =
                serverInfo == null || serverInfo.getLoginedUserInfo() == null
                        ? null
                        : serverInfo.getLoginedUserInfo().getDatabaseInfo(dbName);
        if (dbInfo != null
                && dbInfo.getRunningType() == DbRunningType.CS
                && !serverInfo.isHAMode(dbName)) {
            haDbStatusInfo.setStatusType(DBStatusType.CS_Mode);
        } else if (dbInfo != null && dbInfo.getRunningType() == DbRunningType.STANDALONE) {
            haDbStatusInfo.setStatusType(
                    serverInfo.isHAMode(dbName) ? DBStatusType.STOPPED_HA : DBStatusType.STOPPED);
        }
        if (haHostStatusInfo != null) {
            haDbStatusInfo.setHaHostStatusInfo(haHostStatusInfo);
            haHostStatusInfo.addHADatabaseStatus(haDbStatusInfo);
        }
        return haDbStatusInfo;
    }

    /**
     * Get HAHostStatusInfo object and handle with status type
     *
     * @param serverInfo The ServerInfo
     * @return The HAHostStatusInfo
     */
    public static HAHostStatusInfo getHAHostStatusInfo(ServerInfo serverInfo) {
        HAHostStatusInfo haHostStatusInfo = new HAHostStatusInfo();
        if (serverInfo == null) {
            return haHostStatusInfo;
        }
        haHostStatusInfo.setIp(StringUtil.getIp(serverInfo.getHostAddress()));
        if (!serverInfo.isHAMode(null)) {
            haHostStatusInfo.setStatusType(HostStatusType.NORMAL);
        }
        return haHostStatusInfo;
    }

    /**
     * Get HADatabaseStatusInfo from List<HAHostStatusInfo> by IP and dbName
     *
     * @param haHostStatusInfoList The List<HAHostStatusInfo>
     * @param ip The String
     * @param dbName The String
     * @return HADatabaseStatusInfo
     */
    public static HADatabaseStatusInfo getDatabaseStatusInfo(
            List<HAHostStatusInfo> haHostStatusInfoList, String ip, String dbName) {
        if (haHostStatusInfoList == null) {
            return null;
        }

        for (int i = 0; i < haHostStatusInfoList.size(); i++) {
            HAHostStatusInfo hostStatusInfo = haHostStatusInfoList.get(i);
            if (StringUtil.isIpEqual(ip, hostStatusInfo.getIp())) {
                List<HADatabaseStatusInfo> dbStatusInfoList = hostStatusInfo.getDbStatusList();
                for (int j = 0; j < dbStatusInfoList.size(); j++) {
                    HADatabaseStatusInfo dbStatusInfo = dbStatusInfoList.get(j);
                    if (dbStatusInfo.getDbName().equals(dbName)) {
                        return dbStatusInfo;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get database status information
     *
     * @param dbStatusInfoList List<HADatabaseStatusInfo>
     * @param dbName String
     * @return HADatabaseStatusInfo
     */
    public static HADatabaseStatusInfo getDatabaseStatusInfo(
            List<HADatabaseStatusInfo> dbStatusInfoList, String dbName) {
        for (int j = 0; dbStatusInfoList != null && j < dbStatusInfoList.size(); j++) {
            HADatabaseStatusInfo dbStatusInfo = dbStatusInfoList.get(j);
            if (dbName != null && dbName.equals(dbStatusInfo.getDbName())) {
                return dbStatusInfo;
            }
        }
        return null;
    }

    /**
     * Get HAHostStatusInfo from List<HAHostStatusInfo> by IP
     *
     * @param haHostStatusInfoList The List<HAHostStatusInfo>
     * @param ip The String
     * @return HAHostStatusInfo
     */
    public static HAHostStatusInfo getHostStatusInfo(
            List<HAHostStatusInfo> haHostStatusInfoList, String ip) {
        if (haHostStatusInfoList == null) {
            return null;
        }
        for (int i = 0; i < haHostStatusInfoList.size(); i++) {
            HAHostStatusInfo hostStatusInfo = haHostStatusInfoList.get(i);
            if (StringUtil.isIpEqual(ip, hostStatusInfo.getIp())) {
                return hostStatusInfo;
            }
        }
        return null;
    }

    /**
     * Get the Copy Log DB process status information of active node from standby node
     *
     * @param dbNode DatabaseNode
     * @return DbProcessStatusInfo
     */
    public static DbProcessStatusInfo getActiveCopyLogDbProcessStatusInfo(DatabaseNode dbNode) {
        HADatabaseStatusInfo dbStatusInfo = dbNode.getHaDatabaseStatus();
        if (dbStatusInfo == null) {
            return null;
        }
        DBStatusType dbStatusType = dbStatusInfo.getStatusType();
        if (dbStatusType != DBStatusType.STANDBY && dbStatusType != DBStatusType.MAINTENANCE) {
            return null;
        }
        List<DbProcessStatusInfo> dbProcessList = dbStatusInfo.getCopyLogDbProcessStatusList();
        if (dbProcessList == null || dbProcessList.isEmpty()) {
            return null;
        }
        HAHostStatusInfo hostStatusInfo = dbNode.getParent().getHostStatusInfo();
        if (hostStatusInfo.getStatusType() != HostStatusType.SLAVE
                && hostStatusInfo.getStatusType() != HostStatusType.REPLICA) {
            return null;
        }
        HAHostStatusInfo masterHostStatusInfo = hostStatusInfo.getMasterHostStatusInfo();
        if (masterHostStatusInfo == null) {
            return null;
        }
        String masterHostName = masterHostStatusInfo.getHostName();
        for (DbProcessStatusInfo processStatusInfo : dbProcessList) {
            if (masterHostName.equals(processStatusInfo.getHostName())) {
                return processStatusInfo;
            }
        }
        return null;
    }

    /**
     * Get the Apply Log DB process status information of active node from standby node
     *
     * @param dbNode DatabaseNode
     * @return DbProcessStatusInfo
     */
    public static DbProcessStatusInfo getActiveApplyLogDbProcessStatusInfo(DatabaseNode dbNode) {
        DbProcessStatusInfo copyLogDbProcessStatusInfo =
                getActiveCopyLogDbProcessStatusInfo(dbNode);
        if (copyLogDbProcessStatusInfo == null) {
            return null;
        }
        String logPath = copyLogDbProcessStatusInfo.getLogPath();
        if (logPath == null || logPath.trim().length() == 0) {
            return null;
        }
        HADatabaseStatusInfo dbStatusInfo = dbNode.getHaDatabaseStatus();
        List<DbProcessStatusInfo> dbProcessList = dbStatusInfo.getApplyLogDbProcessStatusList();
        if (dbProcessList == null || dbProcessList.isEmpty()) {
            return null;
        }
        for (DbProcessStatusInfo processStatusInfo : dbProcessList) {
            if (logPath.equals(processStatusInfo.getLogPath())) {
                return processStatusInfo;
            }
        }
        return null;
    }

    /**
     * Get sync mode from standby database node
     *
     * @param standbyNode DatabaseNode
     * @param activeNode DatabaseNode
     * @return SyncModeType
     */
    public static SyncModeType getSyncModeType(DatabaseNode standbyNode, DatabaseNode activeNode) {
        HADatabaseStatusInfo dbStatusInfo = standbyNode.getHaDatabaseStatus();
        if (dbStatusInfo == null) {
            return null;
        }
        DBStatusType dbStatusType = dbStatusInfo.getStatusType();
        if (dbStatusType != DBStatusType.STANDBY && dbStatusType != DBStatusType.MAINTENANCE) {
            return null;
        }
        List<DbProcessStatusInfo> dbProcessList = dbStatusInfo.getCopyLogDbProcessStatusList();
        if (dbProcessList == null || dbProcessList.isEmpty()) {
            return null;
        }
        HAHostStatusInfo hostStatusInfo = dbStatusInfo.getHaHostStatusInfo();
        if (hostStatusInfo.getStatusType() != HostStatusType.SLAVE
                && hostStatusInfo.getStatusType() != HostStatusType.REPLICA) {
            return null;
        }
        HAHostStatusInfo masterHostStatusInfo = hostStatusInfo.getMasterHostStatusInfo();
        if (masterHostStatusInfo == null) {
            return null;
        }
        String masterHostName = masterHostStatusInfo.getHostName();
        String masterIp = masterHostStatusInfo.getIp();
        if (!StringUtil.isIpEqual(activeNode.getParent().getIp(), masterIp)) {
            return null;
        }
        for (DbProcessStatusInfo dbProcessStatusInfo : dbProcessList) {
            String hostName = dbProcessStatusInfo.getHostName();
            String dbName = dbProcessStatusInfo.getDbName();
            if (masterHostName.equals(hostName) && dbName.equals(activeNode.getDbName())) {
                return dbProcessStatusInfo.getMode();
            }
        }
        return null;
    }

    /**
     * Get broker information
     *
     * @param brokerInfos BrokerInfos
     * @param brokerName String
     * @return BrokerInfo
     */
    public static BrokerInfo getBrokerInfo(BrokerInfos brokerInfos, String brokerName) {
        List<BrokerInfo> brokerInfoList =
                (brokerInfos == null || brokerInfos.getBorkerInfoList() == null)
                        ? null
                        : brokerInfos.getBorkerInfoList().getBrokerInfoList();
        for (int i = 0; brokerInfos != null && i < brokerInfoList.size(); i++) {
            BrokerInfo brokerInfo = brokerInfoList.get(i);
            if (brokerInfo.getName().equals(brokerName)) {
                return brokerInfo;
            }
        }
        return null;
    }

    /**
     * Get ha_db_list of server
     *
     * @param serverInfo
     * @return
     */
    public static List<String> getAllHaDBList(ServerInfo serverInfo) {
        List<String> dbList = new ArrayList<String>();
        Map<String, Map<String, String>> haParameterMap = serverInfo.getHaConfParaMap();
        if (haParameterMap == null) {
            return dbList;
        }

        Collection<Map<String, String>> values = haParameterMap.values();
        if (values == null) {
            return dbList;
        }

        for (Map<String, String> map : values) {
            String content = map.get(HAConfParaConstants.HA_DB_LIST);
            if (StringUtil.isEmpty(content)) {
                continue;
            }

            String[] dbs = content.split(",");
            if (dbs == null) {
                continue;
            }

            for (String db : dbs) {
                if (StringUtil.isNotEmpty(db)) {
                    dbList.add(db.trim());
                }
            }
        }

        return dbList;
    }
}
