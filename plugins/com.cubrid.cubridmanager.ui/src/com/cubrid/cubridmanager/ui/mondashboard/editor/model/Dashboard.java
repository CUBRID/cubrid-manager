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

import com.cubrid.cubridmanager.core.common.model.PropertyChangeProvider;
import java.util.ArrayList;
import java.util.List;

/**
 * The top model object with ActiveNode and Standby node
 *
 * @author pangqiren
 * @version 1.0 - 2010-5-31 created by pangqiren
 */
public class Dashboard extends PropertyChangeProvider {

    private static final long serialVersionUID = 3257847701181051954L;
    public static final String PROP_STRUCTURE = "PROP_STRUCTURE";
    public static final String PROP_NAME = "PROP_NAME";
    private String name;
    protected List<HostNode> childNodeList = new ArrayList<HostNode>();
    private List<ClientNode> clientNodeList = new ArrayList<ClientNode>();
    private final List<BrokerDBListNode> brokerDBListNodeList = new ArrayList<BrokerDBListNode>();

    private final DashboardConnectionManager connectionManager =
            new DashboardConnectionManager(this);

    /** notify editpart to refresh */
    public void refresh() {
        fireStructureChange(PROP_STRUCTURE, childNodeList);
    }

    /**
     * Add host node
     *
     * @param node HostNode
     * @return node to be added or already exists node.
     */
    public HostNode addNode(HostNode node) {
        if (childNodeList.contains(node)) {
            return childNodeList.get(childNodeList.indexOf(node));
        } else {
            childNodeList.add(node);
            fireStructureChange(PROP_STRUCTURE, childNodeList);
            return node;
        }
    }

    /**
     * Remove the node
     *
     * @param node HostNode
     */
    public void removeNode(HostNode node) {
        if (childNodeList.contains(node)) {
            for (DatabaseNode dbNode : node.getDbNodeList()) {
                dbNode.removeAllInputsAndOutputs();
            }
            childNodeList.remove(node);
            fireStructureChange(PROP_STRUCTURE, childNodeList);
        }
    }

    /**
     * Get child node list
     *
     * @return List<HostNode>
     */
    public List<HostNode> getHostNodeList() {
        List<HostNode> list = new ArrayList<HostNode>();
        list.addAll(childNodeList);
        return list;
    }

    /**
     * set host node list.
     *
     * @param childNodeList List<HostNode>
     */
    public void setChildNodeList(List<HostNode> childNodeList) {
        if (null == childNodeList) {
            this.childNodeList.clear();
            fireStructureChange(PROP_STRUCTURE, null);
        } else {
            boolean changed = false;
            // remove old nodes.
            List<HostNode> tempList = new ArrayList<HostNode>();
            tempList.addAll(this.childNodeList);
            for (HostNode hn : tempList) {
                if (!childNodeList.contains(hn)) {
                    for (DatabaseNode dbNode : hn.getDbNodeList()) {
                        hn.removeDbNode(dbNode);
                    }
                    this.childNodeList.remove(hn);
                    changed = true;
                }
            }
            // add new nodes.
            for (HostNode hn : childNodeList) {
                // if already exists, continue.
                if (this.childNodeList.contains(hn)) {
                    continue;
                } else {
                    this.childNodeList.add(hn);
                    changed = true;
                }
            }
            if (changed) {
                fireStructureChange(PROP_STRUCTURE, childNodeList);
            }
        }
    }

    public String getName() {
        return name;
    }

    /**
     * Node's name chanaged.
     *
     * @param name String
     */
    public void setName(String name) {
        if (this.name == null && name == null) {
            return;
        } else if (this.name != null && this.name.equals(name)) {
            return;
        }
        String old = this.name;
        this.name = name;
        this.firePropertyChange(PROP_NAME, old, name);
    }

    /**
     * get host node by host name.
     *
     * @param hostName String
     * @return HostNode that has hostName
     */
    HostNode getHostNodeByName(String hostName) {
        for (HostNode hn : this.getHostNodeList()) {
            if (hn.getHostStatusInfo() != null
                    && hostName != null
                    && (hostName.equals(hn.getHostStatusInfo().getHostName())
                            || hostName.equals(hn.getHostStatusInfo().getIp()))) {
                return hn;
            }
        }
        return null;
    }

    /**
     * Get client node list.
     *
     * @return the clientNodeList
     */
    public List<ClientNode> getClientNodeList() {
        ClientNode[] list = clientNodeList.toArray(new ClientNode[] {});
        for (ClientNode cn : list) {
            BrokerNode brokerNode = cn.getBrokerNode();
            if (!childNodeList.contains(brokerNode.getParent())
                    || !brokerNode.getParent().getBrokerNodeList().contains(brokerNode)) {
                clientNodeList.remove(cn);
            }
        }
        return clientNodeList;
    }

    /**
     * Set client node list.
     *
     * @param clientNodeList the clientNodeList to set
     */
    public void setClientNodeList(List<ClientNode> clientNodeList) {
        this.clientNodeList = clientNodeList;
        this.fireStructureChange(PROP_STRUCTURE, clientNodeList);
    }

    /**
     * Add a client node of broker to dashboard
     *
     * @param cn ClientNode
     */
    public void addClientNode(ClientNode cn) {
        for (ClientNode clientNode : clientNodeList) {
            if (clientNode.getBrokerNode().equals(cn.getBrokerNode())) {
                return;
            }
        }
        clientNodeList.add(cn);
        this.fireStructureChange(PROP_STRUCTURE, clientNodeList);
    }

    /**
     * Get all child node that should displayed in dashboard
     *
     * @return All node.
     */
    public List<HANode> getAllChildNode() {
        List<HANode> result = new ArrayList<HANode>();
        for (HostNode hn : getHostNodeList()) {
            if (hn.isVisible()) {
                result.add(hn);
            }
        }
        for (ClientNode cn : getClientNodeList()) {
            if (cn.isVisible()) {
                result.add(cn);
            }
        }
        for (BrokerDBListNode bdb : getBrokerDBListNodeList()) {
            if (bdb.isVisible()) {
                result.add(bdb);
            }
        }
        for (HostNode node : getHostNodeList()) {
            result.addAll(node.getDbNodeList());
            result.addAll(node.getBrokerNodeList());
        }
        return result;
    }

    /**
     * Add broker database list node to dashboard.
     *
     * @param cn BrokerDBListNode
     */
    public void addBrokerDBListNode(BrokerDBListNode cn) {
        for (BrokerDBListNode node : brokerDBListNodeList) {
            if (node.getBrokerNode().equals(cn.getBrokerNode())) {
                return;
            }
        }
        brokerDBListNodeList.add(cn);
        this.fireStructureChange(PROP_STRUCTURE, brokerDBListNodeList);
    }

    /**
     * Get Client Node by broker.
     *
     * @param model BrokerNode
     * @return Client Node
     */
    public ClientNode getClientNodeByBroker(BrokerNode model) {
        ClientNode toBeRemoved = null;
        for (ClientNode cn : this.clientNodeList) {
            if (cn.getBrokerNode().equals(model)) {
                toBeRemoved = cn;
                break;
            }
        }
        return toBeRemoved;
    }

    /**
     * Get Broker DBList Node By Broker
     *
     * @param model BrokerNode
     * @return Broker DBList Node
     */
    public BrokerDBListNode getBrokerDBListNodeByBroker(BrokerNode model) {
        BrokerDBListNode toBeRemoved = null;
        for (BrokerDBListNode cn : this.brokerDBListNodeList) {
            if (cn.getBrokerNode().equals(model)) {
                toBeRemoved = cn;
                break;
            }
        }
        return toBeRemoved;
    }

    /**
     * get Broker DB List Node List
     *
     * @return the brokerDBListNodeList
     */
    public List<BrokerDBListNode> getBrokerDBListNodeList() {
        BrokerDBListNode[] list = brokerDBListNodeList.toArray(new BrokerDBListNode[] {});
        for (BrokerDBListNode bdb : list) {
            BrokerNode brokerNode = bdb.getBrokerNode();
            if (!childNodeList.contains(brokerNode.getParent())
                    || !brokerNode.getParent().getBrokerNodeList().contains(brokerNode)) {
                brokerDBListNodeList.remove(bdb);
            }
        }
        return brokerDBListNodeList;
    }

    /**
     * Get dashboard connection manager.
     *
     * @return the connectionManager
     */
    public DashboardConnectionManager getConnectionManager() {
        return connectionManager;
    }

    /**
     * Get All database nodes in dashboard.
     *
     * @return database node list.
     */
    public List<DatabaseNode> getAllDatabaseNode() {
        List<DatabaseNode> result = new ArrayList<DatabaseNode>();
        for (HostNode hn : this.childNodeList) {
            result.addAll(hn.getDbNodeList());
        }
        return result;
    }

    /**
     * Get All broker nodes in dashboard.
     *
     * @return broker node list.
     */
    public List<BrokerNode> getAllBrokerNode() {
        List<BrokerNode> result = new ArrayList<BrokerNode>();
        for (HostNode hn : this.childNodeList) {
            result.addAll(hn.getBrokerNodeList());
        }
        return result;
    }
}
