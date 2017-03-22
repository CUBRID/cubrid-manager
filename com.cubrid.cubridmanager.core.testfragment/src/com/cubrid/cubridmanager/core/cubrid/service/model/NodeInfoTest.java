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
package com.cubrid.cubridmanager.core.cubrid.service.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;

import junit.framework.TestCase;



public class NodeInfoTest extends TestCase{
	
	public void testNodeInfo(){
		NodeType type = NodeType.MASTER;
		NodeInfo nodeInfo = new NodeInfo(type);
		assertEquals(type, nodeInfo.getType());
		
		String serviceName = "www.cubrid.org";
		List<String> databases = new ArrayList<String>();
		String dbName = "demodb";
		String hostName = "HA Master";
		String ip = "127.0.0.1";
		String brokerInfo = "30000:ON";
		String cmConnectName = "dev-cub-ha-001.ncl";
		String status = "Master";
		int port = 8001;
		String userName = "admin";
		ServerInfo serverInfo = CMHostNodePersistManager.getInstance().getServerInfo(ip,
				port, userName);
		Set<NodeInfo> nodeSet = new HashSet<NodeInfo>();
		nodeInfo.setServiceName(serviceName);
		assertEquals(nodeInfo.getServiceName(), serviceName);
		nodeInfo.setDatabases(databases);
		List<String> databases2 = nodeInfo.getDatabases();
		assertEquals(databases2, databases);
		nodeInfo.addDatabase(dbName);
		assertTrue(databases2.contains(dbName));
		assertTrue(nodeInfo.findDb(dbName));
		nodeInfo.setHostName(hostName);
		assertEquals(nodeInfo.getHostName(), hostName);
		nodeInfo.setIp(ip);
		assertEquals(nodeInfo.getIp(), ip);
		nodeInfo.setBrokerInfo(brokerInfo);
		assertEquals(nodeInfo.getBrokerInfo(), brokerInfo);
		nodeInfo.setCmConnectName(cmConnectName);
		assertEquals(nodeInfo.getCmConnectName(), cmConnectName);
		nodeInfo.setStatus(status);
		assertEquals(nodeInfo.getStatus(), status);
		nodeInfo.setServerInfo(serverInfo);
		assertEquals(nodeInfo.getServerInfo(), serverInfo);
		
		nodeInfo.setRemoteBrokers(nodeSet);
		assertEquals(nodeInfo.getRemoteBrokers(), nodeSet);
		nodeInfo.AddRemoteBroker(nodeInfo);
		assertFalse(nodeInfo.getRemoteBrokers().contains(nodeInfo));
		NodeInfo brokerNode = new NodeInfo(NodeType.BROKER);
		nodeInfo.AddRemoteBroker(brokerNode);
		assertTrue(nodeInfo.getRemoteBrokers().contains(brokerNode));
		
		NodeInfo cloneNode = nodeInfo.clone(nodeInfo);
		assertFalse(cloneNode.equals(nodeInfo));
		assertEquals(nodeInfo.clone(nodeInfo).toString(), nodeInfo.toString());
		
		DbLocationInfo dbLocalInfo = new DbLocationInfo();
		dbLocalInfo.setDbName("demodb");
		List<DbLocationInfo> dbLocationInfoList = new ArrayList<DbLocationInfo>();
		dbLocationInfoList.add(dbLocalInfo);
		nodeInfo.setDbLocationInfoList(dbLocationInfoList);
		assertEquals(nodeInfo.getDbLocationInfoList(), dbLocationInfoList);
	}
	
}
