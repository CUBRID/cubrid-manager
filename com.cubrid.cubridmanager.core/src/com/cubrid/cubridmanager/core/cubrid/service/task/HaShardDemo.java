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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfo;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfoList;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfos;
import com.cubrid.cubridmanager.core.broker.model.BrokerStatusInfos;
import com.cubrid.cubridmanager.core.broker.task.GetBrokerConfParameterTask;
import com.cubrid.cubridmanager.core.broker.task.GetBrokerStatusInfosTask;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.common.task.GetCubridConfParameterTask;
import com.cubrid.cubridmanager.core.common.task.GetDatabasesParameterTask;
import com.cubrid.cubridmanager.core.common.task.GetHAConfParameterTask;
import com.cubrid.cubridmanager.core.common.task.MonitoringTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.database.task.GetDatabaseListTask;
import com.cubrid.cubridmanager.core.cubrid.service.model.BrokerNode;
import com.cubrid.cubridmanager.core.cubrid.service.model.DbLocationInfo;
import com.cubrid.cubridmanager.core.cubrid.service.model.HaNode;
import com.cubrid.cubridmanager.core.cubrid.service.model.NodeInfo;
import com.cubrid.cubridmanager.core.cubrid.service.model.NodeType;
import com.cubrid.cubridmanager.core.cubrid.service.model.ShardNode;
import com.cubrid.cubridmanager.core.mondashboard.task.GetHeartbeatNodeInfoTask;
import com.cubrid.cubridmanager.core.shard.model.Shard;
import com.cubrid.cubridmanager.core.shard.model.ShardConnection;
import com.cubrid.cubridmanager.core.shard.model.Shards;
import com.cubrid.cubridmanager.core.shard.task.GetShardConfTask;
import com.cubrid.cubridmanager.core.shard.task.GetShardStatusTask;

public class HaShardDemo {
	public static HaShardDemo getInstance() {
		return new HaShardDemo();
	}

	/**
	 * For HA, right now, we cannot get the host name from CMS, so need
	 * specified by user.
	 *
	 * @param ip
	 * @param port
	 * @param userName
	 * @param password
	 * @param serviceName
	 * @param connectName
	 * @param nodeName Node name that registered in /etc/hosts.
	 * @return
	 */
	public void registerServiceAndBuildInfo(String ip, int port, String userName, String password,
			String serviceName, String connectName, String nodeName) {
		HaShardManager haShardManager = HaShardManager.getInstance();
		//build server info
		ServerInfo serverInfo = ServerManager.getInstance().getServer(ip, port, userName);
		if (serverInfo == null) {
			serverInfo = new ServerInfo();
			serverInfo.setHostAddress(ip);
			serverInfo.setHostMonPort(port);
			serverInfo.setHostJSPort(port + 1);
			serverInfo.setUserName(userName);
		}
		serverInfo.setServerName(connectName);
		serverInfo.setUserPassword(password);
		serverInfo.setJdbcDriverVersion("Auto Detect");

		// connect to server
		addServer(serverInfo);
		MonitoringTask monitoringTask = serverInfo.getMonitoringTask();
		String clientVersion = "9.3.0";
		serverInfo = monitoringTask.connectServer(clientVersion, 1000);
		if (serverInfo.isConnected()) {
			addServer(serverInfo);
			GetDatabasesParameterTask getDatabasesParameterTask = new GetDatabasesParameterTask(
					serverInfo);
			getDatabasesParameterTask.execute();
			if (!getDatabasesParameterTask.isSuccess()) {
				return;
			}
			List<Map<String, String>> dbParamMapList = getDatabasesParameterTask.getConfParameters();
			List<DbLocationInfo> dbLocationInfoList = new ArrayList<DbLocationInfo>();
			CMServiceAnalysisUtil.addDbLocaltionInfos(dbParamMapList, dbLocationInfoList);

			// get required info from server and build NodeInfo
			NodeInfo info = getRequiredInfo(serverInfo, dbLocationInfoList);
			if (info != null) {
				info.setDbLocationInfoList(dbLocationInfoList);
				info.setServiceName(serviceName);
				info.setCmConnectName(connectName);
				info.setIp(ip);
				info.setHostName(nodeName);
				info.setServerInfo(serverInfo);
				if (info instanceof ShardNode) {
					Shards shards = ((ShardNode) info).getShards();
					Shard shard = shards.getShardList().get(0);
					List<String> connections = shard.getShardConnectionFile().getConnections();
					for (String s : connections) {
						String[] ar = s.split(",");
						if (ip.equals(ar[2]) || nodeName.equals(ar[2])) {
							((ShardNode) info).setSeverStatus("Shard #" + ar[0]);//???
							((ShardNode) info).genStatus();//???
						}
					}
				}

				haShardManager.add(info);
			}
		} else {
			removeServer(serverInfo);
		}
	}

	protected void addServer(ServerInfo serverInfo) {
		/*ServerManager.addServer(serverInfo.getHostAddress(),
				serverInfo.getHostMonPort(), serverInfo.getUserName(), serverInfo);*/
	}

	protected void removeServer(ServerInfo serverInfo) {
		/*ServerManager.removeServer(serverInfo.getHostAddress(),
				serverInfo.getHostMonPort(), serverInfo.getUserName());*/
	}

	protected NodeInfo getRequiredInfo(ServerInfo serverInfo,
			List<DbLocationInfo> dbLocationInfoList) {
		NodeInfo info = null;
		//"getallsysparam"/"cubrid.conf"
		GetCubridConfParameterTask getCubridConfParameterTask = new GetCubridConfParameterTask(
				serverInfo);
		getCubridConfParameterTask.execute();
		if (!getCubridConfParameterTask.isSuccess()) {
			return null;
		}
		Map<String, Map<String, String>> cubConfParas = getCubridConfParameterTask.getConfParameters();
		String haMode = cubConfParas.get("common").get("ha_mode");

		if ("on".equals(haMode) || "replica".equals(haMode)) {
			info = getHaNodeInfo(serverInfo, haMode);
		} else if (CompatibleUtil.isAfter920(serverInfo)){
			info = getShardNodeIndo92(serverInfo);
			if (info == null && CMServiceAnalysisUtil.isAccessedByRemoteHost(dbLocationInfoList)) {
				info = getBrokerNodeInfo(serverInfo);
			}
		} else {
			GetShardStatusTask getShardStatusTask = new GetShardStatusTask(serverInfo, null);
			getShardStatusTask.execute();
			if (getShardStatusTask.isSuccess()) {//check shard
				info = getShardNodeIndo(serverInfo);
				((ShardNode) info).setShardsStatus(getShardStatusTask.getShardsStatus());
			} else if (CMServiceAnalysisUtil.isAccessedByRemoteHost(dbLocationInfoList)) {
				info = getBrokerNodeInfo(serverInfo);
			}
		}

		return info;
	}

	protected NodeInfo getHaNodeInfo(ServerInfo serverInfo, String haMode) {
		//"heartbeatlist"
		GetHeartbeatNodeInfoTask getHeartbeatNodeInfoTask = new GetHeartbeatNodeInfoTask(serverInfo);
		getHeartbeatNodeInfoTask.setAllDb(true);
		getHeartbeatNodeInfoTask.execute();
		if (!getHeartbeatNodeInfoTask.isSuccess()) {
			return null;
		}
		String status = getHeartbeatNodeInfoTask.getCurrentHostStatus();
		NodeType type = CMServiceAnalysisUtil.convertHaStatToNodeType(status);
		HaNode nodeInfo = null;
		if (type != NodeType.NORMAL) {
			nodeInfo = new HaNode(type);
			nodeInfo.buildStatus("ON");
			nodeInfo.setHostStatusInfoList(getHeartbeatNodeInfoTask.getHAHostStatusList());
		} else if ("on".equals(haMode)) {
			nodeInfo = new HaNode(NodeType.SLAVE);
			nodeInfo.buildStatus("OFF");
		} else if ("replica".equals(haMode)) {
			nodeInfo = new HaNode(NodeType.REPLICA);
			nodeInfo.buildStatus("OFF");
		}

		//"getallsysparam"/"cubrid_ha.conf"
		GetHAConfParameterTask getHAConfParameterTask = new GetHAConfParameterTask(serverInfo);
		getHAConfParameterTask.execute();

		BrokerInfos brokerInfos = new BrokerInfos();
		GetBrokerStatusInfosTask<BrokerInfos> getBrokerStatusInfosTask = new GetBrokerStatusInfosTask<BrokerInfos>(
				serverInfo, CommonSendMsg.getGetBrokerStatusItems(), brokerInfos);
		getBrokerStatusInfosTask.execute();
		if (!getHAConfParameterTask.isSuccess() || !getBrokerStatusInfosTask.isSuccess()) {
			return null;
		}

		brokerInfos = getBrokerStatusInfosTask.getResultModel();
		if (brokerInfos != null) {
			nodeInfo.setBrokerInfoList(brokerInfos.getBorkerInfoList());
		}

		Map<String, Map<String, String>> haConfParas = getHAConfParameterTask.getConfParameters();
		Map<String, String> haCommonConf = haConfParas.get("[common]");
		//		String haNodeStr = haCommonConf.get("ha_node_list").substring("cubrid@".length());
		//		List<String> haNodeList = Arrays.asList(haNodeStr.split(":"));
		//		String replicaStr = haCommonConf.get("ha_replica_list");
		//		List<String> replicaNodeList = Arrays.asList(replicaStr.substring("cubrid@".length()).split(":"));

		String dbList = haCommonConf.get("ha_db_list");
		if (dbList.indexOf(",") > -1) {
			String[] dbAr = dbList.split(",");
			for (String s : dbAr) {
				if (s.trim().length() > 0) {
					nodeInfo.addDatabase(s);
				}
			}
		} else {
			nodeInfo.addDatabase(dbList);
		}

		return nodeInfo;
	}

	protected NodeInfo getShardNodeIndo(ServerInfo serverInfo) {
		ShardNode nodeInfo = new ShardNode();
		Shards shards = new Shards();
		GetShardConfTask<Shards> getShardConfTask = new GetShardConfTask<Shards>(serverInfo, shards);
		getShardConfTask.execute();

		if (!getShardConfTask.isSuccess()) {
			return null;
		}
		getShardConfTask.loadDataToModel();
		serverInfo.setShards(shards);

		List<Shard> shardList = shards.getShardList();
		for (int i = 0; shardList != null && i < shardList.size(); i++) {
			Shard shard = shardList.get(i);
			String dbName = shard.getValue("SHARD_DB_NAME");
			nodeInfo.addDatabase(dbName);

			ShardConnection shardConnection = new ShardConnection();
			shard.setShardConnectionFile(shardConnection);
			GetShardConfTask<ShardConnection> getShardConnectionConfTask = new GetShardConfTask<ShardConnection>(
					serverInfo, shardConnection);
			getShardConnectionConfTask.execute();
			getShardConnectionConfTask.loadDataToModel();
		}

		GetDatabaseListTask getDatabaseListTask = new GetDatabaseListTask(serverInfo);
		getDatabaseListTask.execute();
		List<DatabaseInfo> databaseInfoList = getDatabaseListTask.loadDatabaseInfo();
		int shardDbCnt = nodeInfo.getDatabases().size();
		int matchedCnt = 0;
		for (DatabaseInfo dbInfo : databaseInfoList) {
			for (String dbName : nodeInfo.getDatabases()) {
				if (dbInfo.getDbName().equals(dbName)) {
					if (DbRunningType.CS.equals(dbInfo.getRunningType())) {
						nodeInfo.setDbStatus(dbName, "ON");
					} else {
						nodeInfo.setDbStatus(dbName, "OFF");
					}
					matchedCnt++;
					if (shardDbCnt <= matchedCnt) {
						break;
					} else {
						continue;
					}
				}
			}
		}

		nodeInfo.setShards(shards);

		return nodeInfo;
	}
	
	protected NodeInfo getShardNodeIndo92(ServerInfo serverInfo) {
		ShardNode info = null;
		GetBrokerConfParameterTask getBrokerConfParameterTask = new GetBrokerConfParameterTask(serverInfo);
		getBrokerConfParameterTask.execute();
		if (!getBrokerConfParameterTask.isSuccess()) {
			return null;
		}
		
		Shards shards = new Shards();
		Map<String, Map<String, String>> confParams = getBrokerConfParameterTask.getConfParameters();
		List<Map<String, String>> shardParamsList = new ArrayList<Map<String, String>>();
		for (Entry<String, Map<String, String>> entry: confParams.entrySet() ){
			String brokerName = entry.getKey();
			Map<String, String> params = entry.getValue();
			if (StringUtils.equalsIgnoreCase(params.get("SHARD"), "ON")){
				shardParamsList.add(params);
				Shard shard = new Shard();
				shard.setName(brokerName.toLowerCase(Locale.getDefault()));
				shard.setProperties(params);
				
				ShardConnection shardConnection = new ShardConnection();
				shard.setShardConnectionFile(shardConnection);
				GetShardConfTask<ShardConnection> getShardConnectionConfTask = new GetShardConfTask<ShardConnection>(
						serverInfo, shardConnection);
				getShardConnectionConfTask.execute();
				getShardConnectionConfTask.loadDataToModel();
				
				shard.setShardConnectionFile(shardConnection);
				shards.addShard(shard);
			}
		}
		
		if (shardParamsList.size() == 0) {
			return null;
		}
		
		info = new ShardNode();
		for (Map<String, String> params: shardParamsList) {
			String shardDbName = params.get("SHARD_DB_NAME");
			info.addDatabase(shardDbName);
		}
		
		GetDatabaseListTask getDatabaseListTask = new GetDatabaseListTask(serverInfo);
		getDatabaseListTask.execute();
		List<DatabaseInfo> databaseInfoList = getDatabaseListTask.loadDatabaseInfo();
		int shardDbCnt = info.getDatabases().size();
		int matchedCnt = 0;
		for (DatabaseInfo dbInfo : databaseInfoList) {
			for (String dbName : info.getDatabases()) {
				if (dbInfo.getDbName().equals(dbName)) {
					if (DbRunningType.CS.equals(dbInfo.getRunningType())) {
						info.setDbStatus(dbName, "ON");
					} else {
						info.setDbStatus(dbName, "OFF");
					}
					matchedCnt++;
					if (shardDbCnt <= matchedCnt) {
						break;
					} else {
						continue;
					}
				}
			}
		}

		info.setShards(shards);
		
		return info;
	}

	protected NodeInfo getBrokerNodeInfo(ServerInfo serverInfo) {
		BrokerNode nodeInfo = new BrokerNode();
		BrokerInfos brokerInfos = new BrokerInfos();
		GetBrokerStatusInfosTask<BrokerInfos> getBrokerStatusInfosTask = new GetBrokerStatusInfosTask<BrokerInfos>(
				serverInfo, CommonSendMsg.getGetBrokerStatusItems(), brokerInfos);
		getBrokerStatusInfosTask.execute();
		if (!getBrokerStatusInfosTask.isSuccess()) {
			return null;
		}
		brokerInfos = getBrokerStatusInfosTask.getResultModel();
		BrokerInfoList brokerInfoList = brokerInfos.getBorkerInfoList();
		if (!(brokerInfoList != null && brokerInfoList.getBrokerInfoList() != null && brokerInfoList.getBrokerInfoList().size() > 0)) {
			return null;
		}
		nodeInfo.setBrokerInfoList(brokerInfoList);

		//Set<String> dbHosts = new HashSet<String>();
		for (BrokerInfo brokerInfo : brokerInfoList.getBrokerInfoList()) {
			if (brokerInfo == null) {
				continue;
			}
			String brokerName = brokerInfo.getName();
			String brokerPort = brokerInfo.getPort();
			BrokerStatusInfos brokerStatusInfos = new BrokerStatusInfos();
			GetBrokerStatusInfosTask<BrokerStatusInfos> statisTask = new GetBrokerStatusInfosTask<BrokerStatusInfos>(
					serverInfo, CommonSendMsg.getGetBrokerStatusItems(), brokerStatusInfos);
			statisTask.setBrokerName(brokerName);
			statisTask.execute();
			if (!statisTask.isSuccess()) {
				continue;
			}
			brokerStatusInfos = statisTask.getResultModel();
			nodeInfo.addBrokerStatus(brokerPort, brokerStatusInfos);
		}

		return nodeInfo;
	}

	/*public void analysisRegisterHost(){
		//get host node from tree viewer and analysis HA/Shard hosts
		System.out.println("Analysis Register Hosts...");
	}*/

}
