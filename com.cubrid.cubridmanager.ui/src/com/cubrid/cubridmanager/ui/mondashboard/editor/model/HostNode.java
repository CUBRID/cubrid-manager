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

import java.util.ArrayList;
import java.util.List;

import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.mondashboard.model.HAHostStatusInfo;

/**
 * 
 * Host node model class
 * 
 * @author pangqiren
 * @version 1.0 - 2010-6-2 created by pangqiren
 */
public class HostNode extends
		HANode {
	public final static String PROP_DB_NODES = "PROP_DB_NODES";
	public final static String PROP_BROKER_NODES = "PROP_BROKER_NODES";
	public final static String PROP_HOST_STATUS = "PROP_HOST_STATUS";
	public final static String PROP_HOST_CONNECTION_STATUS = "HOST_CONNECTION_STATUS";

	private String ip;
	private String port;
	private String userName;
	private String password;
	private boolean isConnected = false;
	private boolean visible = true;
	private HAHostStatusInfo hostStatusInfo;
	private final List<DatabaseNode> dbNodeList = new ArrayList<DatabaseNode>();
	private final List<BrokerNode> brokerNodeList = new ArrayList<BrokerNode>();

	public HostNode() {
		size.height = 90;
		size.width = 125;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<DatabaseNode> getDbNodeList() {
		return dbNodeList;
	}

	/**
	 * Set database node list
	 * 
	 * @param dbNodeList List<DatabaseNode>
	 */
	public void setDbNodeList(List<DatabaseNode> dbNodeList) {
		if (null == dbNodeList) {
			this.dbNodeList.clear();
			clearFromCopyedDbNodeList();
			fireStructureChange(PROP_DB_NODES, null);
		} else {
			List<DatabaseNode> tempList = new ArrayList<DatabaseNode>();
			tempList.addAll(this.dbNodeList);
			for (DatabaseNode dbNode : tempList) {
				if (!dbNodeList.contains(dbNode)) {
					this.dbNodeList.remove(dbNode);
				}
			}
			//add new nodes.
			for (DatabaseNode dbNode : dbNodeList) {
				if (this.dbNodeList.contains(dbNode)) {
					DatabaseNode node = this.dbNodeList.get(this.dbNodeList.indexOf(dbNode));
					node.setSize(dbNode.getSize());
					node.setLocation(dbNode.getLocation());
				} else {
					this.dbNodeList.add(dbNode);
				}
			}
			clearFromCopyedDbNodeList();
			copyedHANodeList.addAll(this.dbNodeList);
			fireStructureChange(PROP_DB_NODES, dbNodeList);
		}
	}

	/**
	 * 
	 * Add database node
	 * 
	 * @param dbNode The DatabaseNode
	 * @return dbNode to be added or already exists node.
	 */
	public DatabaseNode addDbNode(DatabaseNode dbNode) {
		if (dbNodeList.contains(dbNode)) {
			return dbNodeList.get(dbNodeList.indexOf(dbNode));
		} else {
			dbNodeList.add(dbNode);
			copyedHANodeList.remove(dbNode);
			copyedHANodeList.add(dbNode);
			fireStructureChange(PROP_DB_NODES, dbNodeList);
			return dbNode;
		}
	}

	/**
	 * Remove database node,auto remove database connections first.
	 * 
	 * @param dbNode The DatabaseNode to be removed.
	 */
	public void removeDbNode(DatabaseNode dbNode) {
		for (DatabaseNode node : dbNodeList) {
			if (node.getDbName().equals(dbNode.getDbName())) {
				dbNodeList.remove(node);
				copyedHANodeList.remove(node);
				if (hasNoChildren()) {
					setVisible(true);
				}
				fireStructureChange(PROP_DB_NODES, dbNodeList);
				break;
			}
		}
	}

	/**
	 * host node has no children.
	 * 
	 * @return host node has no children.
	 */
	private boolean hasNoChildren() {
		return brokerNodeList.isEmpty() && dbNodeList.isEmpty();
	}

	public List<BrokerNode> getBrokerNodeList() {
		return brokerNodeList;
	}

	/**
	 * 
	 * Set broker node list
	 * 
	 * @param brokerNodeList List<BrokerNode>
	 */
	public void setBrokerNodeList(List<BrokerNode> brokerNodeList) {
		if (null == brokerNodeList) {
			this.brokerNodeList.clear();
			clearFromCopyedBrokerNodeList();
		} else {
			List<BrokerNode> tempList = new ArrayList<BrokerNode>();
			tempList.addAll(this.brokerNodeList);
			for (BrokerNode brokerNode : tempList) {
				if (!brokerNodeList.contains(brokerNode)) {
					this.brokerNodeList.remove(brokerNode);

				}
			}
			//add new nodes.
			for (BrokerNode brokerNode : brokerNodeList) {
				if (this.brokerNodeList.contains(brokerNode)) {
					BrokerNode node = this.brokerNodeList.get(this.brokerNodeList.indexOf(brokerNode));
					node.setSize(brokerNode.getSize());
					node.setLocation(brokerNode.getLocation());
				} else {
					this.brokerNodeList.add(brokerNode);
				}
			}

			clearFromCopyedBrokerNodeList();
			copyedHANodeList.addAll(this.brokerNodeList);
			fireStructureChange(PROP_BROKER_NODES, brokerNodeList);
		}
	}

	/**
	 * 
	 * Add broker node
	 * 
	 * @param brokerNode The BrokerNode
	 * @return BrokerNode to be added or already exists node.
	 */
	public BrokerNode addBrokerNode(BrokerNode brokerNode) {
		if (brokerNodeList.contains(brokerNode)) {
			return brokerNodeList.get(brokerNodeList.indexOf(brokerNode));
		} else {
			brokerNodeList.add(brokerNode);
			copyedHANodeList.remove(brokerNode);
			copyedHANodeList.add(brokerNode);
			fireStructureChange(PROP_BROKER_NODES, brokerNodeList);
			return brokerNode;
		}
	}

	/**
	 * Remove broker node
	 * 
	 * @param brokerNode The BrokerNode to be removed.
	 */
	public void removeBrokerNode(BrokerNode brokerNode) {
		for (BrokerNode node : brokerNodeList) {
			if (node.equals(brokerNode)) {
				brokerNodeList.remove(node);
				copyedHANodeList.remove(node);
				if (hasNoChildren()) {
					setVisible(true);
				}
				fireStructureChange(PROP_BROKER_NODES, brokerNodeList);
				break;
			}
		}
	}

	public HAHostStatusInfo getHostStatusInfo() {
		return hostStatusInfo;
	}

	/**
	 * set host statues info and fire property change event.
	 * 
	 * @param hostStatusInfo HAHostStatusInfo
	 */
	public void setHostStatusInfo(HAHostStatusInfo hostStatusInfo) {
		this.hostStatusInfo = hostStatusInfo;
		this.firePropertyChange(PROP_HOST_STATUS, null, hostStatusInfo);
	}

	/**
	 * Check the host is connected.
	 * 
	 * @return true:connected;false:not connected.
	 */
	public boolean isConnected() {
		return isConnected && getServerInfo() != null;
	}

	/**
	 * Only fire the property changed events,does not change the server's real
	 * connection status, call this method after the server is connected or
	 * disconnected.
	 * 
	 * @param connected connections status.
	 */
	public void setConnected(boolean connected) {
		isConnected = connected && getServerInfo() != null;
		if (!isConnected) {
			HAHostStatusInfo statusInfo = new HAHostStatusInfo();
			statusInfo.setIp(getIp());
			setHostStatusInfo(statusInfo);
			for (DatabaseNode dn : dbNodeList) {
				dn.setConnected(isConnected);
			}
			for (BrokerNode brokerNode : brokerNodeList) {
				brokerNode.setBrokerInfo(null);
				brokerNode.setBrokerDiagData(null);
				brokerNode.setBrokerStatusInfos(null);
			}
		}
		this.firePropertyChange(PROP_HOST_CONNECTION_STATUS, null, isConnected);
	}

	/**
	 * Get server information, only after login,can get the server
	 * information,otherwise return null
	 * 
	 * @return The ServerInfo
	 */
	public ServerInfo getServerInfo() {
		if (ip != null && port != null && userName != null) {
			return ServerManager.getServer(ip,
					Integer.parseInt(port), userName);
		}
		return null;
	}

	/**
	 * get host's database by database name.
	 * 
	 * @param databaseName database name
	 * @return database node.
	 */
	public DatabaseNode getDBNodeByName(String databaseName) {
		for (DatabaseNode dn : this.dbNodeList) {
			if (dn.getDbName().equals(databaseName)) {
				return dn;
			}
		}
		return null;
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
		} else if (!(obj instanceof HostNode)) {
			return false;
		}
		return this.toString().equals(obj.toString());
	}

	/**
	 * Override object's hashCode method.
	 * 
	 * @return HostNode hashCode
	 */
	public int hashCode() {
		return this.toString().hashCode();
	}

	/**
	 * host node to string
	 * 
	 * @return HostNode to string
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(userName).append("@").append(ip).append(":").append(port);
		return sb.toString();
	}

	//// the below is for copy
	// when add and remove node, do not notify structure changed 
	private final List<HANode> copyedHANodeList = new ArrayList<HANode>();

	/**
	 * 
	 * Get copy child node list
	 * 
	 * @return List<HANode>
	 */
	public List<HANode> getCopyedHaNodeList() {
		return copyedHANodeList;
	}

	/**
	 * 
	 * Get copy database node list
	 * 
	 * @return List<DatabaseNode>
	 */
	public List<DatabaseNode> getCopyedDbNodeList() {
		List<DatabaseNode> copyedNodeList = new ArrayList<DatabaseNode>();
		for (HANode haNode : copyedHANodeList) {
			if (haNode instanceof DatabaseNode) {
				copyedNodeList.add((DatabaseNode) haNode);
			}
		}
		return copyedNodeList;
	}

	/**
	 * 
	 * Get copy broker node list
	 * 
	 * @return List<BrokerNode>
	 */
	public List<BrokerNode> getCopyedBrokerNodeList() {
		List<BrokerNode> copyedNodeList = new ArrayList<BrokerNode>();
		for (HANode haNode : copyedHANodeList) {
			if (haNode instanceof BrokerNode) {
				copyedNodeList.add((BrokerNode) haNode);
			}
		}
		return copyedNodeList;
	}

	/**
	 * 
	 * Clear all copy database node
	 * 
	 */
	private void clearFromCopyedDbNodeList() {
		Object[] haNodes = copyedHANodeList.toArray();
		for (Object haNode : haNodes) {
			if (haNode instanceof DatabaseNode) {
				copyedHANodeList.remove(haNode);
			}
		}
	}

	/**
	 * 
	 * Clear all copy broker node list
	 * 
	 */
	private void clearFromCopyedBrokerNodeList() {
		Object[] haNodes = copyedHANodeList.toArray();
		for (Object haNode : haNodes) {
			if (haNode instanceof BrokerNode) {
				copyedHANodeList.remove(haNode);
			}
		}
	}

	/**
	 * Set host node visible status.
	 * 
	 * @param visible boolean
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Get the host node visible status.
	 * 
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}
}
