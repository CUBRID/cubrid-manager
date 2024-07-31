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
package com.cubrid.cubridmanager.ui.spi.persist;

import com.cubrid.common.core.util.CipherUtils;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.DefaultCubridNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.MonitorStatistic;
import com.cubrid.common.ui.spi.persist.PersistUtils;
import com.cubrid.cubridmanager.core.common.xml.IXMLMemento;
import com.cubrid.cubridmanager.core.common.xml.XMLMemento;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.mondashboard.editor.MonitorDashboardEditor;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.BrokerDBListNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.BrokerNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.ClientNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.Dashboard;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.DatabaseNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.draw2d.geometry.Point;

/**
 * Monitor dashboard persist manager
 *
 * @author pangqiren
 * @version 1.0 - 2011-4-1 created by pangqiren
 */
public final class MonitorDashboardPersistManager {

    private static final String MONITOR_DASHBOARD_XML_CONTENT = "CUBRID_MONITOR_DASHBOARDS";
    private List<ICubridNode> monitorDashboardList = null;
    private static MonitorDashboardPersistManager instance;

    private MonitorDashboardPersistManager() {
        init();
    }

    /**
     * Return the only MonitorDashboardPersistManager
     *
     * @return MonitorDashboardPersistManager
     */
    public static MonitorDashboardPersistManager getInstance() {
        synchronized (MonitorDashboardPersistManager.class) {
            if (instance == null) {
                instance = new MonitorDashboardPersistManager();
            }
        }
        return instance;
    }

    /** Initial the persist manager */
    protected void init() {
        synchronized (this) {
            monitorDashboardList = new ArrayList<ICubridNode>();
            loadMonitorDashboard();
        }
    }

    /** Load monitor dash board */
    protected void loadMonitorDashboard() {
        synchronized (this) {
            IXMLMemento memento =
                    PersistUtils.getXMLMemento(
                            CubridManagerUIPlugin.PLUGIN_ID, MONITOR_DASHBOARD_XML_CONTENT);
            if (memento != null) {
                IXMLMemento[] children = memento.getChildren("dashboard");
                for (int i = 0; i < children.length; i++) {
                    Dashboard dashboard = new Dashboard();
                    String dashBoardName = children[i].getString("name");
                    dashboard.setName(dashBoardName);

                    IXMLMemento[] hostChildren = children[i].getChildren("host");
                    for (int j = 0; hostChildren != null && j < hostChildren.length; j++) {
                        // load the host node
                        String aliasName = hostChildren[j].getString("aliasname");
                        String userName = hostChildren[j].getString("username");
                        String password = hostChildren[j].getString("password");
                        String ip = hostChildren[j].getString("ip");
                        String port = hostChildren[j].getString("port");
                        String location = hostChildren[j].getString("location");
                        String visible = hostChildren[j].getString("visible");
                        HostNode hostNode = new HostNode();
                        if (aliasName == null || aliasName.trim().length() == 0) {
                            aliasName = ip + ":" + port;
                        }
                        hostNode.setName(aliasName);
                        hostNode.setIp(ip);
                        hostNode.setPort(port);
                        hostNode.setUserName(userName);
                        hostNode.setPassword(CipherUtils.decrypt(password));
                        if (location != null && location.trim().length() > 0) {
                            String[] locations = location.split(",");
                            hostNode.setLocation(
                                    new Point(
                                            Integer.parseInt(locations[0]),
                                            Integer.parseInt(locations[1])));
                        }
                        if ("true".equals(visible)) {
                            hostNode.setVisible(true);
                        } else {
                            hostNode.setVisible(false);
                        }
                        dashboard.addNode(hostNode);
                        // load the database node list
                        IXMLMemento[] databaseChildren = hostChildren[j].getChildren("database");
                        for (int k = 0;
                                databaseChildren != null && k < databaseChildren.length;
                                k++) {
                            aliasName = databaseChildren[k].getString("aliasname");
                            String dbName = databaseChildren[k].getString("dbname");
                            String dbUserName = databaseChildren[k].getString("username");
                            String dbPassword = databaseChildren[k].getString("password");
                            String dbLocation = databaseChildren[k].getString("location");

                            if (aliasName == null || aliasName.trim().length() == 0) {
                                aliasName = dbName;
                            }
                            DatabaseNode databaseNode = new DatabaseNode();
                            databaseNode.setName(aliasName);
                            databaseNode.setDbUser(dbUserName);
                            databaseNode.setDbPassword(CipherUtils.decrypt(dbPassword));
                            databaseNode.setDbName(dbName);
                            databaseNode.setParent(hostNode);
                            hostNode.addDbNode(databaseNode);

                            if (dbLocation != null && dbLocation.trim().length() > 0) {
                                String[] locations = dbLocation.split(",");
                                databaseNode.setLocation(
                                        new Point(
                                                Integer.parseInt(locations[0]),
                                                Integer.parseInt(locations[1])));
                            }
                        }
                        // load the broker node list
                        IXMLMemento[] brokerChildren = hostChildren[j].getChildren("broker");
                        for (int k = 0; brokerChildren != null && k < brokerChildren.length; k++) {
                            aliasName = brokerChildren[k].getString("aliasname");
                            String brokerName = brokerChildren[k].getString("brokername");
                            String brokerLocation = brokerChildren[k].getString("location");

                            if (aliasName == null || aliasName.trim().length() == 0) {
                                aliasName = brokerName;
                            }
                            BrokerNode brokerNode = new BrokerNode();
                            brokerNode.setName(aliasName);
                            brokerNode.setBrokerName(brokerName);
                            brokerNode.setParent(hostNode);
                            hostNode.addBrokerNode(brokerNode);
                            // load the client IP list
                            String clientIpName = brokerChildren[k].getString("client_ip_name");
                            String clientIpLocation =
                                    brokerChildren[k].getString("client_ip_location");
                            String showIpList = brokerChildren[k].getString("show_ip_list");
                            if (brokerLocation != null && brokerLocation.trim().length() > 0) {
                                String[] locations = brokerLocation.split(",");
                                brokerNode.setLocation(
                                        new Point(
                                                Integer.parseInt(locations[0]),
                                                Integer.parseInt(locations[1])));
                            }
                            String[] locations =
                                    clientIpLocation == null
                                            ? new String[] {"0", "0"}
                                            : clientIpLocation.split(",");
                            ClientNode ipListNode = new ClientNode();
                            ipListNode.setBrokerNode(brokerNode);
                            ipListNode.setLocation(
                                    new Point(
                                            Integer.parseInt(locations[0]),
                                            Integer.parseInt(locations[1])));
                            if (clientIpName != null && clientIpName.trim().length() > 0) {
                                ipListNode.setName(clientIpName);
                            }
                            if ("true".equals(showIpList)) {
                                ipListNode.setVisible(true);
                            } else {
                                ipListNode.setVisible(false);
                            }
                            dashboard.addClientNode(ipListNode);
                            // load the client database list
                            String clientDbName = brokerChildren[k].getString("client_db_name");
                            String clientDbLocation =
                                    brokerChildren[k].getString("client_db_location");
                            String showDbList = brokerChildren[k].getString("show_db_list");
                            locations =
                                    clientDbLocation == null
                                            ? new String[] {"0", "0"}
                                            : clientDbLocation.split(",");
                            BrokerDBListNode dbListNode = new BrokerDBListNode();
                            dbListNode.setBrokerNode(brokerNode);

                            dbListNode.setLocation(
                                    new Point(
                                            Integer.parseInt(locations[0]),
                                            Integer.parseInt(locations[1])));
                            if (clientDbName != null && clientDbName.trim().length() > 0) {
                                dbListNode.setName(clientDbName);
                            }
                            if ("true".equals(showDbList)) {
                                dbListNode.setVisible(true);
                            } else {
                                dbListNode.setVisible(false);
                            }
                            dashboard.addBrokerDBListNode(dbListNode);
                        }
                    }
                    DefaultCubridNode dashboardNode =
                            new DefaultCubridNode(
                                    dashBoardName,
                                    dashBoardName,
                                    "icons/navigator/status_item.png");
                    dashboardNode.setType(CubridNodeType.MONITOR_DASHBOARD);
                    dashboardNode.setEditorId(MonitorDashboardEditor.ID);
                    dashboardNode.setModelObj(dashboard);
                    monitorDashboardList.add(dashboardNode);
                }
            }
        }
    }

    /** Save dash boards */
    public void saveDashboard() {
        synchronized (this) {
            XMLMemento memento = XMLMemento.createWriteRoot("dashboards");
            Iterator<ICubridNode> iterator = monitorDashboardList.iterator();
            while (iterator.hasNext()) {
                ICubridNode node = (ICubridNode) iterator.next();
                if (node instanceof MonitorStatistic) {
                    continue;
                }
                Dashboard dashboard = (Dashboard) node.getAdapter(Dashboard.class);
                IXMLMemento dashboardChild = memento.createChild("dashboard");
                dashboardChild.putString("name", node.getName());
                Iterator<HostNode> nodeIte = dashboard.getHostNodeList().iterator();
                while (nodeIte.hasNext()) {
                    // save host node
                    HostNode hostNode = nodeIte.next();
                    IXMLMemento hostNodeChild = dashboardChild.createChild("host");
                    hostNodeChild.putString("aliasname", hostNode.getName());
                    hostNodeChild.putString("ip", hostNode.getIp());
                    hostNodeChild.putString("port", hostNode.getPort());
                    hostNodeChild.putString("username", hostNode.getUserName());
                    hostNodeChild.putString(
                            "password", CipherUtils.encrypt(hostNode.getPassword()));
                    hostNodeChild.putString(
                            "location", hostNode.getLocation().x + "," + hostNode.getLocation().y);
                    hostNodeChild.putString("visible", hostNode.isVisible() ? "true" : "false");
                    // save the database node list
                    List<DatabaseNode> dbNodeList = hostNode.getDbNodeList();
                    Iterator<DatabaseNode> dbNodeIte = dbNodeList.iterator();
                    while (dbNodeIte.hasNext()) {
                        DatabaseNode dbNode = (DatabaseNode) dbNodeIte.next();
                        IXMLMemento databaseNodeChild = hostNodeChild.createChild("database");
                        databaseNodeChild.putString("aliasname", dbNode.getName());
                        databaseNodeChild.putString("dbname", dbNode.getDbName());
                        databaseNodeChild.putString("username", dbNode.getDbUser());
                        databaseNodeChild.putString(
                                "password", CipherUtils.encrypt(dbNode.getDbPassword()));
                        databaseNodeChild.putString(
                                "location", dbNode.getLocation().x + "," + dbNode.getLocation().y);
                    }
                    // save broker node list
                    List<BrokerNode> brokerNodeList = hostNode.getBrokerNodeList();
                    Iterator<BrokerNode> brokerNodeIte = brokerNodeList.iterator();
                    while (brokerNodeIte.hasNext()) {
                        BrokerNode brokerNode = (BrokerNode) brokerNodeIte.next();
                        IXMLMemento brokerNodeChild = hostNodeChild.createChild("broker");
                        brokerNodeChild.putString("aliasname", brokerNode.getName());
                        brokerNodeChild.putString("brokername", brokerNode.getBrokerName());
                        brokerNodeChild.putString(
                                "location",
                                brokerNode.getLocation().x + "," + brokerNode.getLocation().y);
                        // save the client IP list
                        ClientNode ipListNode = dashboard.getClientNodeByBroker(brokerNode);
                        Point point = null;
                        String clientIpName = null;
                        if (ipListNode == null) {
                            brokerNodeChild.putString("show_ip_list", "false");
                            point = brokerNode.getClientsLocation();
                        } else {
                            brokerNodeChild.putString(
                                    "show_ip_list", ipListNode.isVisible() ? "true" : "false");
                            point = ipListNode.getLocation();
                            clientIpName = ipListNode.getName();
                        }
                        if (point != null) {
                            brokerNodeChild.putString(
                                    "client_ip_location", point.x + "," + point.y);
                        }
                        if (clientIpName != null) {
                            brokerNodeChild.putString("client_ip_name", clientIpName);
                        }
                        // save the database list
                        BrokerDBListNode dbListNode =
                                dashboard.getBrokerDBListNodeByBroker(brokerNode);
                        String clientDbName = null;
                        if (dbListNode == null) {
                            brokerNodeChild.putString("show_db_list", "false");
                            point = brokerNode.getDatabasesLocation();
                        } else {
                            brokerNodeChild.putString(
                                    "show_db_list", dbListNode.isVisible() ? "true" : "false");
                            point = dbListNode.getLocation();
                            clientDbName = dbListNode.getName();
                        }
                        if (point != null) {
                            brokerNodeChild.putString(
                                    "client_db_location", point.x + "," + point.y);
                        }
                        if (clientDbName != null) {
                            brokerNodeChild.putString("client_db_name", clientDbName);
                        }
                    }
                }
            }
            PersistUtils.saveXMLMemento(
                    CubridManagerUIPlugin.PLUGIN_ID, MONITOR_DASHBOARD_XML_CONTENT, memento);
        }
    }

    /**
     * Add monitoring dash board
     *
     * @param node The ICubridNode
     */
    public void addMonitorDashboard(ICubridNode node) {
        synchronized (this) {
            if (node != null) {
                monitorDashboardList.add(node);
                saveDashboard();
                CubridNodeManager.getInstance()
                        .fireCubridNodeChanged(
                                new CubridNodeChangedEvent(
                                        node, CubridNodeChangedEventType.NODE_ADD));
            }
        }
    }

    /**
     * Remove monitor dash board
     *
     * @param node The ICubridNode
     */
    public void removeMonitorDashboard(ICubridNode node) {
        synchronized (this) {
            if (node != null) {
                monitorDashboardList.remove(node);
                saveDashboard();
                CubridNodeManager.getInstance()
                        .fireCubridNodeChanged(
                                new CubridNodeChangedEvent(
                                        node, CubridNodeChangedEventType.NODE_REMOVE));
            }
        }
    }

    public List<ICubridNode> getMonitorDashboardList() {
        return monitorDashboardList;
    }

    /**
     * Get All HA monitor templates
     *
     * @return List<ICubridNode>
     */
    public List<ICubridNode> getAllMonitorDashboards() {
        return monitorDashboardList;
    }
}
