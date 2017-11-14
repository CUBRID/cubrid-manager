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
package com.cubrid.cubridmanager.ui.mondashboard.editor.model;

import java.util.ArrayList;
import java.util.List;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.broker.model.ApplyServerInfo;
import com.cubrid.cubridmanager.core.broker.model.BrokerStatusInfos;
import com.cubrid.cubridmanager.core.mondashboard.model.HADatabaseStatusInfo;
import com.cubrid.cubridmanager.core.mondashboard.model.HAHostStatusInfo;
import com.cubrid.cubridmanager.ui.spi.util.HAUtil;

/**
 * Dashboard Connection Manager
 * 
 * @author SC13425
 * @version 1.0 - 2010-8-25 created by SC13425
 */
public class DashboardConnectionManager {

	private final Dashboard dashboard;

	public DashboardConnectionManager(Dashboard dashboard) {
		this.dashboard = dashboard;
	}

	/**
	 * Refresh host to db connections and host to broker connections.
	 * 
	 */
	public void refreshHost2ChildrenConnections() {
		for (HostNode hn : dashboard.getHostNodeList()) {
			if (hn.isVisible()) {
				hn.removeConnectionsTargetNotInList(hn.getDbNodeList(),
						DatabaseNode.class);
				hn.removeConnectionsTargetNotInList(hn.getBrokerNodeList(),
						BrokerNode.class);
				for (DatabaseNode dn : hn.getDbNodeList()) {
					if (!hn.targetExists(dn)) {
						new Host2ChildConnection(hn, dn);
					}
				}
				for (BrokerNode bn : hn.getBrokerNodeList()) {
					if (!hn.targetExists(bn)) {
						new Host2ChildConnection(hn, bn);
					}
				}
			} else {
				hn.removeAllInputsAndOutputs();
			}
		}
	}

	/**
	 * Refresh all connections in dashboard.
	 * 
	 */
	public void refreshConnections() {
		//Host node to database node connections
		//Host node to broker node connections
		refreshHost2ChildrenConnections();
		//database node to database node connections
		refreshDB2DBConnections();
		//broker node to database node connections
		refreshBroker2DBConnections();
		//broker client node to broker node connections
		refreshClient2BrokerConnections();
		//broker node to broker db list node connections
		refreshBroker2DBListConnections();
	}

	/**
	 * Refresh Broker to DB Connections
	 * 
	 */
	public void refreshBroker2DBConnections() {
		List<BrokerNode> allBrokerNode = dashboard.getAllBrokerNode();
		for (BrokerNode broker : allBrokerNode) {
			//create broker to db connections
			BrokerStatusInfos brokerStatusInfos = broker.getBrokerStatusInfos();
			List<DatabaseNode> dbList2Connect = new ArrayList<DatabaseNode>();
			if (null != brokerStatusInfos
					&& null != brokerStatusInfos.getAsinfo()
					&& !brokerStatusInfos.getAsinfo().isEmpty()) {
				List<ApplyServerInfo> asList = brokerStatusInfos.getAsinfo();

				for (ApplyServerInfo asi : asList) {
					if ("IDLE".equals(asi.getAsStatus())) {
						continue;
					}
					HostNode hostNode = dashboard.getHostNodeByName(asi.getAsDbHost());
					if (hostNode == null) {
						continue;
					}
					String asDbname = asi.getAsDbName();
					if (StringUtil.isNotEmpty(asDbname)) {
						asDbname = asDbname.replaceAll("@.*", "");
					}
					DatabaseNode dbNode = hostNode.getDBNodeByName(asDbname);
					if (dbNode == null) {
						continue;
					}
					dbList2Connect.add(dbNode);
				}
			}
			//remove old connections
			broker.removeConnectionsTargetNotInList(dbList2Connect,
					DatabaseNode.class);
			//create new connections
			for (DatabaseNode db2Conn : dbList2Connect) {
				if (!broker.targetExists(db2Conn)) {
					new BrokerConnection(broker, db2Conn);
				}
			}
		}
		List<DatabaseNode> allDatabaseNode = dashboard.getAllDatabaseNode();
		for (DatabaseNode dbNode : allDatabaseNode) {
			dbNode.removeConnectionsSourceNotInList(allBrokerNode,
					BrokerNode.class);
		}
	}

	/**
	 * Refresh Broker to DB Connections
	 * 
	 */
	public void refreshBroker2DBListConnections() {
		for (HostNode hn : dashboard.getHostNodeList()) {
			for (BrokerNode broker : hn.getBrokerNodeList()) {
				//create broker to db list connections
				BrokerDBListNode bdbNode = dashboard.getBrokerDBListNodeByBroker(broker);
				if (bdbNode == null) {
					continue;
				}
				if (bdbNode.isVisible() && !broker.targetExists(bdbNode)) {
					new BrokerConnection(broker, bdbNode);
				} else if (!bdbNode.isVisible()) {
					bdbNode.removeAllInputsAndOutputs();
				}
			}
		}
	}

	/**
	 * Refresh Broker to DB Connections
	 * 
	 */
	public void refreshClient2BrokerConnections() {
		for (HostNode hn : dashboard.getHostNodeList()) {
			for (BrokerNode broker : hn.getBrokerNodeList()) {
				//create client to broker connections
				ClientNode clientNode = dashboard.getClientNodeByBroker(broker);
				if (null == clientNode) {
					continue;
				}
				if (clientNode.isVisible() && !broker.sourceExists(clientNode)) {
					new BrokerConnection(clientNode, broker);
				} else if (!clientNode.isVisible()) {
					clientNode.removeAllInputsAndOutputs();
				}
			}
		}
	}

	/**
	 * Refresh DB to DB connections.
	 * 
	 */
	public void refreshDB2DBConnections() {
		List<DatabaseNode> allDatabaseNode = dashboard.getAllDatabaseNode();
		List<DatabaseNode> standByList = new ArrayList<DatabaseNode>();
		for (DatabaseNode dbNode : allDatabaseNode) {
			if (standByList.contains(dbNode)) {
				continue;
			}
			HADatabaseStatusInfo haDatabaseStatus = dbNode.getHaDatabaseStatus();
			if (haDatabaseStatus == null) {
				dbNode.removeAllHAConnections();
				continue;
			}
			HAHostStatusInfo haHostStatusInfo = haDatabaseStatus.getHaHostStatusInfo();
			if (haHostStatusInfo == null) {
				dbNode.removeAllHAConnections();
				continue;
			}
			//Is standby db
			if (haHostStatusInfo.getMasterHostStatusInfo() != null
					&& (haHostStatusInfo.getSlaveHostStatusInfoList() == null || haHostStatusInfo.getSlaveHostStatusInfoList().size() == 0)) {
				HostNode master = dashboard.getHostNodeByName(haHostStatusInfo.getMasterHostStatusInfo().getHostName());
				if (null == master) {
					dbNode.removeAllHAConnections();
					continue;
				}
				DatabaseNode activeNode = master.getDBNodeByName(dbNode.getDbName());
				if (null == activeNode) {
					dbNode.removeAllHAConnections();
					continue;
				}
				if (null == HAUtil.getSyncModeType(dbNode, activeNode)) {
					dbNode.removeAllHAConnections();
					continue;
				}
				if (!dbNode.sourceExists(activeNode)) {
					new MonitorConnection(activeNode, dbNode);
				}
				//handled.add(activeNode);
				continue;
			}
			//Is active db
			if (haHostStatusInfo.getMasterHostStatusInfo() == null
					&& haHostStatusInfo.getSlaveHostStatusInfoList() != null
					&& haHostStatusInfo.getSlaveHostStatusInfoList().size() > 0) {
				//get the standby nodes of the active node
				standByList.clear();
				for (HAHostStatusInfo hostStatus : haHostStatusInfo.getSlaveHostStatusInfoList()) {
					HostNode slave = dashboard.getHostNodeByName(hostStatus.getHostName());
					if (slave == null) {
						continue;
					}
					DatabaseNode standbyNode = slave.getDBNodeByName(dbNode.getDbName());
					if (standbyNode == null) {
						continue;
					}
					if (null == HAUtil.getSyncModeType(standbyNode, dbNode)) {
						continue;
					}
					standByList.add(standbyNode);
				}
				dbNode.removeConnectionsTargetNotInList(standByList,
						DatabaseNode.class);
				for (DatabaseNode standbyNode : standByList) {
					if (!dbNode.targetExists(standbyNode)) {
						new MonitorConnection(dbNode, standbyNode);
					}
				}
				continue;
			}
			//other db
			dbNode.removeAllHAConnections();
		}
	}
}
