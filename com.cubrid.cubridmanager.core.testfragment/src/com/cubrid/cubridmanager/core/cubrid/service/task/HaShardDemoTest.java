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

import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.service.model.NodeInfo;
import com.cubrid.cubridmanager.core.cubrid.service.model.NodeType;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;

import junit.framework.TestCase;


public class HaShardDemoTest extends TestCase {
	private HaShardDemo demo;
	private HaShardManager manager;
	
	private String serviceName;
	
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
		
		serviceName = "ci";
		
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
	
	public void testHaNode() {
		try{
			demo.registerServiceAndBuildInfo(haIp, haCmPort, haCmUser, 
					haCmPass, serviceName, haConnName, haHost);
			
			NodeInfo node = manager.findByIp(haIp);
			assertTrue(NodeType.MASTER == node.getType() || NodeType.SLAVE == node.getType());
			assertEquals(node.getServiceName(), serviceName);
			assertEquals(node.getHostName(), haHost);
			assertEquals(node.getCmConnectName(), haConnName);
		}finally{
			ServerInfo serverInfo = ServerManager.getServer(haIp,
					haCmPort, haCmUser);
			CMHostNodePersistManager.getInstance().removeServer(
					serverInfo.getHostAddress(), serverInfo.getHostMonPort(),
					serverInfo.getUserName());
		}
	}
	
	public void testReplicaNode() {
		try{
			demo.registerServiceAndBuildInfo(replicaIp, replicaCmPort, replicaCmUser, 
					replicaCmPass, serviceName, replicaConnName, replicaHost);
			
			NodeInfo node = manager.findByIp(replicaIp);
			assertEquals(node.getType(), NodeType.REPLICA);
			assertEquals(node.getServiceName(), serviceName);
			assertEquals(node.getHostName(), replicaHost);
			assertEquals(node.getCmConnectName(), replicaConnName);
		}finally{
			ServerInfo serverInfo = ServerManager.getServer(replicaIp,
					replicaCmPort, replicaCmUser);
			CMHostNodePersistManager.getInstance().removeServer(
					serverInfo.getHostAddress(), serverInfo.getHostMonPort(),
					serverInfo.getUserName());
		}
	}
	
	public void testBrokerNode() {
		try{
			demo.registerServiceAndBuildInfo(brokerIp, brokerCmPort, brokerCmUser, 
					brokerCmPass, serviceName, brokerConnName, brokerHost);
			
			NodeInfo node = manager.findByIp(brokerIp);
			assertEquals(node.getType(), NodeType.BROKER);
			assertEquals(node.getServiceName(), serviceName);
			assertEquals(node.getHostName(), brokerHost);
			assertEquals(node.getCmConnectName(), brokerConnName);
		}finally{
			ServerInfo serverInfo = ServerManager.getServer(brokerIp,
					brokerCmPort, brokerCmUser);
			CMHostNodePersistManager.getInstance().removeServer(
					serverInfo.getHostAddress(), serverInfo.getHostMonPort(),
					serverInfo.getUserName());
		}
	}
	
	public void testShardNode() {
		try{
			demo.registerServiceAndBuildInfo(shardIp, shardCmPort, shardCmUser, 
					shardCmPass, serviceName, shardConnName, shardHost);
			
			NodeInfo node = manager.findByIp(shardIp);
			assertEquals(node.getType(), NodeType.SHARD);
			assertEquals(node.getServiceName(), serviceName);
			assertEquals(node.getHostName(), shardHost);
			assertEquals(node.getCmConnectName(), shardConnName);
		}finally{
			ServerInfo serverInfo = ServerManager.getServer(shardIp,
					shardCmPort, shardCmUser);
			CMHostNodePersistManager.getInstance().removeServer(
					serverInfo.getHostAddress(), serverInfo.getHostMonPort(),
					serverInfo.getUserName());
		}
	}
}
