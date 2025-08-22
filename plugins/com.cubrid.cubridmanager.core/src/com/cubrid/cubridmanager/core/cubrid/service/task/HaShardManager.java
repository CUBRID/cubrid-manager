/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.cubridmanager.core.cubrid.service.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.cubrid.service.model.BrokerNode;
import com.cubrid.cubridmanager.core.cubrid.service.model.NodeInfo;
import com.cubrid.cubridmanager.core.cubrid.service.model.NodeType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class HaShardManager {
    private static final HaShardManager haShardManager = new HaShardManager();
    private List<NodeInfo> list = new ArrayList<NodeInfo>();
    private boolean isSorted = true;
    private List<NodeInfo> brokerNodeList = new ArrayList<NodeInfo>();
    private boolean isBrokerListDirty = true;

    private HaShardManager() {}

    public static HaShardManager getInstance() {
        return haShardManager;
    }

    public void add(NodeInfo info) {
        if (info == null) {
            return;
        }

        int index = find(info);
        if (index == -1) {
            list.add(info);
        } else {
            list.set(index, info);
            if (NodeType.BROKER == info.getType()) {
                isBrokerListDirty = true;
            }
        }
        isSorted = false;
    }

    public boolean remove(NodeInfo info) {
        if (info == null) {
            return false;
        }
        return list.remove(info);
    }

    public int find(NodeInfo info) {
        if (info == null || info.getIp() == null) {
            return -1;
        }
        for (int i = 0; i < list.size(); i++) {
            if (info.getIp().equals(list.get(i).getIp())) {
                return i;
            }
        }
        return -1;
    }

    public NodeInfo findByIp(String ip) {
        if (StringUtil.isEmpty(ip)) {
            return null;
        }
        NodeInfo nodeInfo = null;
        for (int i = 0; i < list.size(); i++) {
            if (ip.equals(list.get(i).getIp())) {
                nodeInfo = list.get(i);
                break;
            }
        }
        return nodeInfo;
    }

    public NodeInfo findByHostName(String hostName) {
        if (StringUtil.isEmpty(hostName)) {
            return null;
        }
        NodeInfo nodeInfo = null;
        for (int i = 0; i < list.size(); i++) {
            if (hostName.equals(list.get(i).getHostName())) {
                nodeInfo = list.get(i);
                break;
            }
        }
        return nodeInfo;
    }

    public List<NodeInfo> findGroupByIp(String ip) {
        List<NodeInfo> result = null;
        NodeInfo node = findByIp(ip);
        if (node != null) {
            result = findGroupByServiceName(node.getServiceName());
        }
        return result;
    }

    public List<NodeInfo> findGroupByHostName(String hostName) {
        List<NodeInfo> result = null;
        NodeInfo node = findByHostName(hostName);
        if (node != null) {
            result = findGroupByServiceName(node.getServiceName());
        }
        return result;
    }

    public List<NodeInfo> findGroupByServiceName(String serviceName) {
        if (StringUtil.isEmpty(serviceName)) {
            return null;
        }
        List<NodeInfo> result = new ArrayList<NodeInfo>();
        for (NodeInfo node : list) {
            if (serviceName.equals(node.getServiceName())) {
                result.add(node);
            }
        }
        return result;
    }

    public List<NodeInfo> getNodeList() {
        return list;
    }

    /** Sort by service name registered for node */
    public void sort() {
        if (isSorted) {
            return;
        }
        Collections.sort(
                list,
                new Comparator<NodeInfo>() {
                    public int compare(NodeInfo node1, NodeInfo node2) {
                        int result = 0;
                        if ((result = node1.getType().compareTo(node2.getType())) != 0) {
                            return result;
                        } else if ((result =
                                        node1.getServiceName().compareTo(node2.getServiceName()))
                                != 0) {
                            return result;
                        } else if ((result = node1.getHostName().compareTo(node2.getHostName()))
                                != 0) {
                            return result;
                        }
                        return result;
                    }
                });

        isSorted = true;
    }

    public List<NodeInfo> getHaNodes() {
        List<NodeInfo> haList = new ArrayList<NodeInfo>();
        for (NodeInfo node : list) {
            if (NodeType.MASTER == node.getType()
                    || NodeType.SLAVE == node.getType()
                    || NodeType.REPLICA == node.getType()) {
                haList.add(node);
            }
        }
        return haList;
    }

    public List<NodeInfo> getShardNodes() {
        List<NodeInfo> shardList = new ArrayList<NodeInfo>();
        for (NodeInfo node : list) {
            if (NodeType.SHARD == node.getType()) {
                shardList.add(node);
            }
        }
        return shardList;
    }

    public List<NodeInfo> getBrokerNodes() {
        if (!isBrokerListDirty) {
            return brokerNodeList;
        }
        brokerNodeList.clear();
        for (NodeInfo node : list) {
            if (NodeType.BROKER == node.getType()) {
                brokerNodeList.add(node);
            }
        }
        return brokerNodeList;
    }

    public void registerBrokerHostInHaShard() {
        getBrokerNodes();
        for (NodeInfo node : brokerNodeList) {
            Set<String> hostSet = ((BrokerNode) node).getHostSet();
            if (hostSet == null) {
                continue;
            }
            for (String host : hostSet) {
                NodeInfo hostNode = null;
                if (CMServiceAnalysisUtil.isIp(host)) {
                    hostNode = findByIp(host);
                } else {
                    hostNode = findByHostName(host);
                }
                if (hostNode != null) {
                    List<NodeInfo> group = findGroupByServiceName(hostNode.getServiceName());
                    for (NodeInfo nodeInGp : group) {
                        nodeInGp.AddRemoteBroker(node);
                    }
                }
            }
        }
    }
}
