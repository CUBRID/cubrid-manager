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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cubrid.common.core.util.ArrayUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.broker.model.ApplyServerInfo;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfo;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfoList;
import com.cubrid.cubridmanager.core.broker.model.BrokerStatusInfos;
import com.cubrid.cubridmanager.core.cubrid.service.task.CMServiceAnalysisUtil;

public class BrokerNode extends
		NodeInfo { // FIXME description
	private BrokerInfoList brokerInfoList;
	private Map<String, BrokerStatusInfos> brokerStatusMap;
	private static final String LOCALHOST = "localhost";

	public BrokerNode() {
		super(NodeType.BROKER);
	}

	public void setBrokerInfoList(BrokerInfoList brokerInfoList) {
		this.brokerInfoList = brokerInfoList;
	}

	public BrokerInfoList getBrokerInfoList() {
		return brokerInfoList;
	}

	public void setBrokerStatusMap(Map<String, BrokerStatusInfos> brokerStatusMap) {
		this.brokerStatusMap = brokerStatusMap;
	}

	public Map<String, BrokerStatusInfos> getBrokerStatusMap() {
		return brokerStatusMap;
	}

	public void addBrokerStatus(String brokerPort, BrokerStatusInfos brokerStatus) {
		if (brokerPort == null || brokerStatus == null) {
			return;
		}
		if (brokerStatusMap == null) {
			brokerStatusMap = new HashMap<String, BrokerStatusInfos>();
		}
		brokerStatusMap.put(brokerPort, brokerStatus);
	}

	public String getBrokerInfo() {
		genBrokerInfo();
		return super.getBrokerInfo();
	}

	public Set<NodeInfo> getRemoteBrokers() {
		return null;
	}

	public void setRemoteBrokers(Set<NodeInfo> remoteBrokers) {
		//do nothing
	}

	public void AddRemoteBroker(NodeInfo node) {
		//do nothing
	}

	public void genBrokerInfo() {
		StringBuilder sb = new StringBuilder();
		if (brokerInfoList != null) {
			int count = 0;
			for (BrokerInfo brokerInfo : brokerInfoList.getBrokerInfoList()) {
				if (brokerInfo != null) {
					//sb.append(brokerInfo.getName()).append("[");
					sb.append(brokerInfo.getPort()).append("(");
					sb.append(brokerInfo.getAccess_mode()).append(")");
					sb.append(":");
					sb.append(brokerInfo.getState()).append(", ");
					count++;
				}
			}
			if (count > 0) {
				sb.delete(sb.length() - 2, sb.length());
			}
		}
		super.setBrokerInfo(sb.toString());
		super.setStatus(sb.toString());
	}

	public Set<String> getHostSet() {
		Set<String> hostSet = new HashSet<String>();
		for (BrokerStatusInfos bStatusInfos : brokerStatusMap.values()) {
			List<ApplyServerInfo> asInfoList = bStatusInfos.getAsinfo();
			if (asInfoList == null) {
				continue;
			}
			for (ApplyServerInfo asInfo : asInfoList) {
				String dbHost = asInfo.getAsDbHost();
				if (!StringUtil.isEmpty(dbHost) && !CMServiceAnalysisUtil.isLocalHost(dbHost)) {
					hostSet.add(dbHost);
				}
			}
		}

		return hostSet;
	}

	/*public Set<String> getDbSet(){
		Set<String> dbSet = new HashSet<String>();
		for(BrokerStatusInfos bStatusInfos : brokerStatusMap.values()){
			List<ApplyServerInfo> asInfoList = bStatusInfos.getAsinfo();
			if(asInfoList == null){
				continue;
			}
			for(ApplyServerInfo asInfo : asInfoList){
				String dbHost = asInfo.getAs_dbhost();
				String dbName = asInfo.getAs_dbname();
				if(!StringUtil.isEmpty(dbName) && !isLocalHost(dbHost)){
					dbSet.add(dbName);
				}
			}
		}

		return dbSet;
	}*/

	public Map<List<String>, Set<String>> getAccessedHostDbMap() {
		List<DbLocationInfo> dbLocationInfoList = getDbLocationInfoList();
		if (brokerStatusMap == null || brokerStatusMap.isEmpty() || dbLocationInfoList == null
				|| dbLocationInfoList.isEmpty()) {
			return null;
		}
		Map<List<String>, Set<String>> accessedHostDbMap = new HashMap<List<String>, Set<String>>();
		for (BrokerStatusInfos bStatusInfos : brokerStatusMap.values()) {
			List<ApplyServerInfo> asInfoList = bStatusInfos.getAsinfo();
			if (asInfoList == null) {
				continue;
			}
			for (ApplyServerInfo asInfo : asInfoList) {
				String dbHost = asInfo.getAsDbHost();
				String dbName = asInfo.getAsDbName();
				if (StringUtil.isEmpty(dbName) || StringUtil.isEmpty(dbHost)) {
					continue;
				}
				List<String> hostList = null;
				if (CMServiceAnalysisUtil.isLocalHost(dbHost)) {
					hostList = new ArrayList<String>();
					hostList.add(BrokerNode.LOCALHOST);
				} else {
					for (DbLocationInfo dbLocalInfo : dbLocationInfoList) {
						if (dbName.equals(dbLocalInfo.getDbName())) {
							if (dbLocalInfo.findHost(dbHost)) {
								hostList = dbLocalInfo.getDbHosts();
							}
						}
					}
				}

				Set<String> dbSet = accessedHostDbMap.get(hostList);
				if (dbSet != null) {
					dbSet.add(dbName);
				} else {
					dbSet = new HashSet<String>();
					dbSet.add(dbName);
					accessedHostDbMap.put(hostList, dbSet);
				}
			}
		}

		return accessedHostDbMap;
	}

	public Set<String> getAccessedHosts() {
		if (brokerStatusMap == null || brokerStatusMap.isEmpty()) {
			return null;
		}
		Set<String> accessedHosts = new HashSet<String>();
		for (BrokerStatusInfos bStatusInfos : brokerStatusMap.values()) {
			List<ApplyServerInfo> asInfoList = bStatusInfos.getAsinfo();
			if (asInfoList == null) {
				continue;
			}
			for (ApplyServerInfo asInfo : asInfoList) {
				String dbHost = asInfo.getAsDbHost();
				if (StringUtil.isEmpty(dbHost) || CMServiceAnalysisUtil.isLocalHost(dbHost)) {
					continue;
				}
				accessedHosts.add(dbHost);
			}
		}

		return accessedHosts;
	}

	public String getAccessedDbInfo() {
		Map<List<String>, Set<String>> accessedHostDbMap = getAccessedHostDbMap();
		if (accessedHostDbMap == null || accessedHostDbMap.isEmpty()) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		for (List<String> hosts : accessedHostDbMap.keySet()) {
			for (String dbName : accessedHostDbMap.get(hosts)) {
				if (hosts.contains(LOCALHOST)) {
					sb.append(dbName).append(",");
				} else if (hosts.size() > 1) {
					sb.append(dbName).append("@");
					sb.append("[").append(ArrayUtil.collectionToCSString(hosts)).append("],");
				} else {
					sb.append(dbName).append("@");
					sb.append(ArrayUtil.collectionToCSString(hosts)).append(",");
				}
			}
		}
		if (sb.length() > 0) {
			sb.delete(sb.length() - 1, sb.length());
		}

		return sb.toString();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.getIp()).append(" - ");
		sb.append(super.getServiceName()).append(" - ");
		sb.append(getBrokerInfo()).append(" - ");
		sb.append(super.getCmConnectName()).append(" - ");
		sb.append(getAccessedDbInfo()).append(" - ");
		sb.append(ArrayUtil.collectionToCSString(getAccessedHosts()));
		return sb.toString();
	}

}
