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

import java.net.InetAddress;
import java.util.List;
import java.util.Set;

import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.service.model.BrokerNode;
import com.cubrid.cubridmanager.core.cubrid.service.model.HaNode;
import com.cubrid.cubridmanager.core.cubrid.service.model.NodeInfo;
import com.cubrid.cubridmanager.core.cubrid.service.model.NodeType;
import com.cubrid.cubridmanager.core.cubrid.service.model.ShardNode;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;

import junit.framework.TestCase;


public class HaShardManagerTest extends TestCase{
	private HaShardDemo demo;
	private HaShardManager manager;
	
	private String haServiceName;
	private String shardServiceName;
	
	//for HA node test
	private String haHost;
	private String haIp;
	private String haConnName;
	private int haCmPort;
	private String haCmUser;
	private String haCmPass;
	
	//for Replica node test
	private String replicaHost;
	private String replicaIp;
	private String replicaConnName;
	private int replicaCmPort;
	private String replicaCmUser;
	private String replicaCmPass;
	
	//for Broker node test
	private String brokerHost;
	private String brokerIp;
	private String brokerConnName;
	private int brokerCmPort;
	private String brokerCmUser;
	private String brokerCmPass;
	
	//for Broker node test
	private String shardHost;
	private String shardIp;
	private String shardConnName;
	private int shardCmPort;
	private String shardCmUser;
	private String shardCmPass;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		demo = HaShardDemo.getInstance();
		assertNotNull(demo);
		manager = HaShardManager.getInstance();
		
		haServiceName = "ci_ha";
		shardServiceName = "ci_shard";
		
		haHost = SystemParameter.getParameterValue("haHost");
		haIp = getIpByHost(haHost);
		haConnName = "HA Master";
		haCmPort = SystemParameter.getParameterIntValue("haCmPort");
		haCmUser = SystemParameter.getParameterValue("haCmUser");
		haCmPass = SystemParameter.getParameterValue("haCmPass");
		
		replicaHost = SystemParameter.getParameterValue("replicaHost");
		replicaIp = getIpByHost(replicaHost);
		replicaConnName = "HA Replica 1";
		replicaCmPort = SystemParameter.getParameterIntValue("replicaCmPort");
		replicaCmUser = SystemParameter.getParameterValue("replicaCmUser");
		replicaCmPass = SystemParameter.getParameterValue("replicaCmPass");
		
		brokerHost = SystemParameter.getParameterValue("brokerHost");
		brokerIp = getIpByHost(brokerHost);
		brokerConnName = "Broker Server 1";
		brokerCmPort = SystemParameter.getParameterIntValue("brokerCmPort");
		brokerCmUser = SystemParameter.getParameterValue("brokerCmUser");
		brokerCmPass = SystemParameter.getParameterValue("brokerCmPass");
		
		shardHost = SystemParameter.getParameterValue("shardHost");
		shardIp = getIpByHost(shardHost);
		shardConnName = "Shard 1";
		shardCmPort = SystemParameter.getParameterIntValue("shardCmPort");
		shardCmUser = SystemParameter.getParameterValue("shardCmUser");
		shardCmPass = SystemParameter.getParameterValue("shardCmPass");
	}
	
	private String getIpByHost(String host) throws Exception {
		InetAddress address = InetAddress.getByName(host); 
		return address.getHostAddress();
	}
	
	// test ha/shard separately because of use only 3 node to install cubrid
	// we suppose that: HA/shard node on the same machine
	public void testHa(){
		HaShardManager manager = HaShardManager.getInstance();
		assertNotNull(manager);
		
		HaNode haNode = new HaNode(NodeType.SLAVE);
		haNode.setIp(haIp);
		haNode.setHostName(haHost);
		haNode.setServiceName(haServiceName);
		
		HaNode replicaNode = new HaNode(NodeType.REPLICA);
		replicaNode.setIp(replicaIp);
		replicaNode.setServiceName(haServiceName);
		
		BrokerNode brokerNode = new BrokerNode();
		brokerNode.setIp(brokerIp);
		brokerNode.setHostName(brokerHost);
		brokerNode.setServiceName(haServiceName);
		
		manager.add(brokerNode);
		manager.add(replicaNode);
		manager.add(haNode);
		
		List<NodeInfo> nodeList = manager.getNodeList();
		assertTrue(nodeList.contains(haNode));
		assertTrue(nodeList.contains(replicaNode));
		assertTrue(nodeList.contains(brokerNode));
		int index = manager.find(brokerNode);
		assertEquals(nodeList.get(index), brokerNode);
		
		BrokerNode brokerNode2 = new BrokerNode();
		String brokerIp2 = "127.0.0.1";
		brokerNode2.setIp(brokerIp2);
		assertTrue(manager.find(brokerNode2) == -1);
		
		assertEquals(manager.findByIp(brokerIp), brokerNode);
		assertNull(manager.findByIp(brokerIp2));
		
		assertEquals(manager.findByHostName(haHost), haNode);
		assertNull(manager.findByHostName("localhost"));
		
		
		List<NodeInfo> haGroups = manager.findGroupByIp(haIp);
		assertTrue(haGroups.size() == 3);
		assertTrue(haGroups.contains(haNode));
		assertTrue(haGroups.contains(replicaNode));
		assertTrue(haGroups.contains(brokerNode));
		
		List<NodeInfo> haGroups2 = manager.findGroupByHostName(haHost);
		assertTrue(haGroups2.size() == 3);
		assertTrue(haGroups2.contains(haNode));
		assertTrue(haGroups2.contains(replicaNode));
		assertTrue(haGroups2.contains(brokerNode));
		
		List<NodeInfo> shardGroups = manager.findGroupByServiceName(shardServiceName);
		assertTrue(shardGroups.size() == 0);
		
		List<NodeInfo> haList = manager.getHaNodes();
		assertTrue(haList.size() == 2);
		assertTrue(haList.contains(haNode));
		assertTrue(haList.contains(replicaNode));
		assertFalse(haList.contains(brokerNode));
		
		List<NodeInfo> shardList = manager.getShardNodes();
		assertTrue(shardList.size() == 0);
		
		List<NodeInfo> brokerList = manager.getBrokerNodes();
		assertTrue(brokerList.size() == 1);
		assertTrue(brokerList.contains(brokerNode));
		assertFalse(brokerList.contains(haNode));
		
		int haIndex = manager.find(haNode);
		int brokerIndex = manager.find(brokerNode);
		assertTrue(haIndex > brokerIndex);
		manager.sort();
		int haIndex2 = manager.find(haNode);
		int brokerIndex2 = manager.find(brokerNode);
		assertTrue(haIndex2 < brokerIndex2);
		
		//test remove method
		manager.remove(brokerNode);
		assertTrue(manager.find(brokerNode) == -1);
		assertNull(manager.findByIp(brokerIp));
		
		testRegisterBrokerHostInHaShard(manager);
	}
	
	public void testShard(){
		HaShardManager manager = HaShardManager.getInstance();
		assertNotNull(manager);
		
		HaNode replicaNode = new HaNode(NodeType.REPLICA);
		replicaNode.setIp(replicaIp);
		replicaNode.setServiceName(haServiceName);
		
		ShardNode shardNode = new ShardNode();
		shardNode.setIp(shardIp);
		shardNode.setServiceName(shardServiceName);
		
		BrokerNode brokerNode = new BrokerNode();
		brokerNode.setIp(brokerIp);
		brokerNode.setHostName(brokerHost);
		brokerNode.setServiceName(haServiceName);
		
		manager.add(brokerNode);
		manager.add(shardNode);
		manager.add(replicaNode);
		
		List<NodeInfo> nodeList = manager.getNodeList();
		assertTrue(nodeList.contains(replicaNode));
		assertTrue(nodeList.contains(shardNode));
		assertTrue(nodeList.contains(brokerNode));
		int index = manager.find(brokerNode);
		assertEquals(nodeList.get(index), brokerNode);
		
		BrokerNode brokerNode2 = new BrokerNode();
		String brokerIp2 = "127.0.0.1";
		brokerNode2.setIp(brokerIp2);
		assertTrue(manager.find(brokerNode2) == -1);
		
		assertEquals(manager.findByIp(shardIp), shardNode);
		assertNull(manager.findByIp(brokerIp2));
		
		List<NodeInfo> haGroups = manager.findGroupByIp(replicaIp);
		assertTrue(haGroups.size() == 2);
		assertTrue(haGroups.contains(replicaNode));
		assertTrue(haGroups.contains(brokerNode));
		assertFalse(haGroups.contains(shardNode));
		
		List<NodeInfo> shardGroups = manager.findGroupByServiceName(shardServiceName);
		assertTrue(shardGroups.size() == 1);
		assertTrue(shardGroups.contains(shardNode));
		assertFalse(shardGroups.contains(replicaNode));
		
		List<NodeInfo> haList = manager.getHaNodes();
		assertTrue(haList.size() == 1);
		assertTrue(haList.contains(replicaNode));
		assertFalse(haList.contains(brokerNode));
		
		List<NodeInfo> shardList = manager.getShardNodes();
		assertTrue(shardList.size() == 1);
		assertTrue(shardList.contains(shardNode));
		assertFalse(shardList.contains(brokerNode));
		
		List<NodeInfo> brokerList = manager.getBrokerNodes();
		assertTrue(brokerList.size() == 1);
		assertTrue(brokerList.contains(brokerNode));
		assertFalse(brokerList.contains(shardNode));
		
		int haIndex = manager.find(replicaNode);
		int shardIndex = manager.find(shardNode);
		assertTrue(haIndex > shardIndex);
		manager.sort();
		int haIndex2 = manager.find(replicaNode);
		int shardIndex2 = manager.find(shardNode);
		assertTrue(haIndex2 < shardIndex2);
		
		//test remove method
		manager.remove(shardNode);
		assertTrue(manager.find(shardNode) == -1);
		assertNull(manager.findByIp(shardIp));
		
		manager.add(shardNode);
		
		manager.remove(brokerNode);
		testRegisterBrokerHostInHaShard(manager);
	}
	
	public void testRegisterBrokerHostInHaShard(HaShardManager manager){
		try {
			HaShardDemo demo = new HaShardDemo();
			demo.registerServiceAndBuildInfo(brokerIp, brokerCmPort, brokerCmUser,
					brokerCmPass, haServiceName, brokerConnName,
					brokerHost);
			manager.registerBrokerHostInHaShard();
			BrokerNode brokerNode = (BrokerNode) manager.findByIp(brokerIp);
			Set<String> hostSet = brokerNode.getHostSet();
			if (hostSet != null && hostSet.contains(haHost)) {
				HaNode haNode = (HaNode) manager.findByHostName(haHost);
				assertTrue(haNode.getRemoteBrokers().contains(brokerNode));
			}
		} finally {
			ServerInfo serverInfo = ServerManager.getServer(brokerIp,
					brokerCmPort, brokerCmUser);
			if (serverInfo != null) {
				CMHostNodePersistManager.getInstance().removeServer(
						serverInfo.getHostAddress(),
						serverInfo.getHostMonPort(), serverInfo.getUserName());
			}
		}
	}
}
