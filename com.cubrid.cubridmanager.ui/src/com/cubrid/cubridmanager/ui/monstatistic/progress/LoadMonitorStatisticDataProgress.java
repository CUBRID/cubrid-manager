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
package com.cubrid.cubridmanager.ui.monstatistic.progress;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.task.MonitoringTask;
import com.cubrid.cubridmanager.core.monstatistic.model.MultiHostChartItem;
import com.cubrid.cubridmanager.core.monstatistic.model.SingleHostChartItem;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticChartHost;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticChartItem;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticData;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil;
import com.cubrid.cubridmanager.core.monstatistic.task.GetMonitorStatisticDataTask;
import com.cubrid.cubridmanager.ui.spi.Version;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;

/**
 * @author Santiago Wang
 * 
 * @version 1.0 - 2013-09-09 Santiago Wang
 */

public class LoadMonitorStatisticDataProgress implements IRunnableWithProgress {

	private static final Logger LOGGER = LogUtil.getLogger(LoadMonitorStatisticDataProgress.class);

	private static final Set<ServerInfo> disconnectedServerSet = new HashSet<ServerInfo>();
	private final List<StatisticData> EMPTY_STATISTIC_DATA_LIST = new ArrayList<StatisticData>(0);
	private final ServerInfo serverInfo;
	private final boolean isMultiHost;
	private List<StatisticChartItem> statisticItemList;
	private Map<StatisticChartItem, List<StatisticData>> statisticDataMap = new HashMap<StatisticChartItem, List<StatisticData>>();
	private String errorMsg = null;
	
	private boolean success = false;

	/**
	 * For single host.
	 * 
	 * @param serverInfo
	 */
	public LoadMonitorStatisticDataProgress(ServerInfo serverInfo) {
		this.serverInfo = serverInfo;
		this.isMultiHost = false;
	}

	/**
	 * For multi-host.
	 * 
	 * @param serverInfo
	 */
	public LoadMonitorStatisticDataProgress() {
		this.serverInfo = null;
		this.isMultiHost = true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		if (statisticItemList == null) {
			success = false;
			return;
		}
		if (isMultiHost) {
			for (StatisticChartItem chartItem : statisticItemList) {
				if (!(chartItem instanceof MultiHostChartItem)) {
					statisticDataMap.put(chartItem, EMPTY_STATISTIC_DATA_LIST);
					continue;
				}
				MultiHostChartItem item = (MultiHostChartItem) chartItem;
				if (item.getHostList() == null
						|| item.getHostList().size() == 0) {
					statisticDataMap.put(chartItem, EMPTY_STATISTIC_DATA_LIST);
					continue;
				}
				List<StatisticData> dataList = new ArrayList<StatisticData>();
				for (StatisticChartHost host : item.getHostList()) {
					//build ServerInfo
					ServerInfo serverInfo = buildServerInfo(host);
					StatisticData statisticData = new StatisticData();
					statisticData.setServerInfo(serverInfo);
					GetMonitorStatisticDataTask<StatisticData> getStatisticDatatask = new GetMonitorStatisticDataTask<StatisticData>(
							serverInfo,
							StatisticParamUtil.getSendMsgItems(item.getType()),
							statisticData);
					getStatisticDatatask.setMetric(host.getMetric());
					getStatisticDatatask.setDateType(item.getDType());
					/*[TOOLS-3742] Initial StatisticData for GetMonitorStatisticDataTask failure*/
					statisticData.setMetric(host.getMetric());
					statisticData.setDtype(item.getDType());
					switch (item.getType()) {
					case DB:
						getStatisticDatatask.setDbName(host.getDbName());
						statisticData.setDbName(host.getDbName());
						break;
					case DB_VOL:
						getStatisticDatatask.setDbName(host.getDbName());
						getStatisticDatatask.setVolName(host.getVolName());
						statisticData.setDbName(host.getDbName());
						statisticData.setVolName(host.getVolName());
						break;
					case BROKER:
						getStatisticDatatask.setBrokerName(host.getBrokerName());
						statisticData.setbName(host.getBrokerName());
						break;
					case OS:
						break;
					default:
						break;
					}

					if (!serverInfo.isConnected()) {
						disconnectedServerSet.add(serverInfo);
						MonitoringTask monitoringTask = serverInfo.getMonitoringTask();
						serverInfo = monitoringTask.connectServer(
								Version.releaseVersion, 1000);
					}
					if (serverInfo.isConnected()) {
						addServer(serverInfo);
						getStatisticDatatask.execute();
						if (getStatisticDatatask.isSuccess()) {
							statisticData = getStatisticDatatask.getResultModel();
							dataList.add(statisticData);
						} else {
							dataList.add(statisticData);
						}
					} else {
						removeServer(serverInfo);
						dataList.add(statisticData);
					}

				}
				statisticDataMap.put(chartItem, dataList);
			}
			tearDownDisconnectedServer();
		} else { //for single host
			for (StatisticChartItem chartItem : statisticItemList) {
				if (!(chartItem instanceof SingleHostChartItem)) {
					statisticDataMap.put(chartItem, EMPTY_STATISTIC_DATA_LIST);
					continue;
				}
				SingleHostChartItem item = (SingleHostChartItem) chartItem;
				if (item.getMetricList() == null
						|| item.getMetricList().size() == 0) {
					statisticDataMap.put(chartItem, EMPTY_STATISTIC_DATA_LIST);
					continue;
				}

				List<StatisticData> dataList = new ArrayList<StatisticData>();
				for (int i = 0; i < item.getMetricList().size(); i++) {
					String metric = item.getMetricList().get(i);
					StatisticData statisticData = new StatisticData();
					statisticData.setServerInfo(serverInfo);
					GetMonitorStatisticDataTask<StatisticData> task = new GetMonitorStatisticDataTask<StatisticData>(
							serverInfo,
							StatisticParamUtil.getSendMsgItems(item.getType()),
							statisticData);

					task.setMetric(metric);
					task.setDateType(item.getDType());
					statisticData.setMetric(metric);
					statisticData.setDtype(item.getDType());
					switch (item.getType()) {
					case DB:
						task.setDbName(item.getDbName());
						statisticData.setDbName(item.getDbName());
						break;
					case DB_VOL:
						task.setDbName(item.getDbName());
						task.setVolName(item.getVolName());
						statisticData.setDbName(item.getDbName());
						statisticData.setVolName(item.getVolName());
						break;
					case BROKER:
						task.setBrokerName(item.getBrokerName());
						statisticData.setbName(item.getBrokerName());
						break;
					case OS:
						break;
					default:
						break;
					}
					task.execute();
					if (task.isSuccess()) {
						statisticData = task.getResultModel();
						dataList.add(statisticData);
					} else if (task.getErrorMsg() != null
							&& (task.getErrorMsg().indexOf("invalid token") != -1 || !serverInfo.isConnected())) {
						/*[TOOLS-3742] when invalid token or connect server failure, give out error message*/
						errorMsg = task.getErrorMsg();
						//build StatisticData with no data
						for (; i < item.getMetricList().size(); i++) {
							String metric2 = item.getMetricList().get(i);
							StatisticData data = (StatisticData) statisticData.clone();
							data.setMetric(metric2);
							dataList.add(data);
						}
						break;
					}
				}
				statisticDataMap.put(chartItem, dataList);
			}
		}
		success = true;
	}

	private void removeServer(ServerInfo serverInfo) {
		CMHostNodePersistManager.getInstance().removeServer(serverInfo.getHostAddress(),
				serverInfo.getHostMonPort(), serverInfo.getUserName());
	}

	private void addServer(ServerInfo serverInfo) {
		CMHostNodePersistManager.getInstance().addServer(serverInfo.getHostAddress(),
				serverInfo.getHostMonPort(), serverInfo.getUserName(),
				serverInfo);
	}

	/**
	 * load MonitorStatisticData list
	 * 
	 * @return Catalog
	 */
	public void loadMonitorStatisticData() {
		Display display = Display.getDefault();
		display.syncExec(new Runnable() {
			public void run() {
				try {
					new ProgressMonitorDialog(null).run(true, false,
							LoadMonitorStatisticDataProgress.this);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		});
	}

	public List<StatisticChartItem> getStatisticItemList() {
		return statisticItemList;
	}

	public void setStatisticItemList(List<StatisticChartItem> statisticItemList) {
		this.statisticItemList = statisticItemList;
	}

	public void addStatisticItem(StatisticChartItem statisticItem) {
		if (statisticItem == null) {
			return;
		}
		if (statisticItemList == null) {
			statisticItemList = new ArrayList<StatisticChartItem>();
		}
		statisticItemList.add(statisticItem);
	}

	public Map<StatisticChartItem, List<StatisticData>> getStatisticDataMap() {
		return statisticDataMap;
	}

	public boolean isSuccess() {
		return success;
	}

	/**
	 * Build ServerInfo for multi-host monitor statistic.
	 * 
	 * @param hostInfo
	 * @return
	 */
	public static ServerInfo buildServerInfo(StatisticChartHost hostInfo) {
		final CMHostNodePersistManager hostNodePersistManager = CMHostNodePersistManager.getInstance();

		ServerInfo serverInfo = hostInfo.getServerInfo();
		String serverName = null;
		String ip = null;
		int port = 0;
		String username = null;
		String password = null;
		boolean isInitial = false;
		/* For multi-host monitor statistic, will not keep the connection in ServerManager.
		 * So if the ServerInfo is null or disconnected, try to get the ServerInfo from ServerManager. 
		 * If failure, then build the ServerInfo with IP/Port/User Name/Password. 
		 */
		if (serverInfo == null || !serverInfo.isConnected()) {
			CubridServer cubridServer;
			if (hostInfo.getCubridServerId() != null) {
				serverName = hostInfo.getCubridServerId();
				cubridServer = hostNodePersistManager.getServer(serverName);
				if (cubridServer != null) {
					isInitial = true;
					ip = cubridServer.getHostAddress();
					port = Integer.parseInt(cubridServer.getMonPort());
					username = cubridServer.getUserName();
					password = cubridServer.getPassword();
				}
			}
			if (!isInitial) {
				ip = hostInfo.getIp();
				serverName = ip;
				port = hostInfo.getPort();
				username = hostInfo.getUser();
				password = hostInfo.getPassword();
			}
			serverInfo = hostNodePersistManager.getServerInfo(ip, port, username);
			if (serverInfo == null) {
				//if (serverInfo == null || !serverInfo.isConnected()) {
				serverInfo = new ServerInfo();
				serverInfo.setServerName(serverName);
				serverInfo.setHostAddress(ip);
				serverInfo.setHostMonPort(port);
				serverInfo.setHostJSPort(port + 1);
				serverInfo.setUserName(username);
				serverInfo.setUserPassword(password);
			}
		}
		return serverInfo;
	}

	/**
	 * Add ServerInfo into <i>disconnectedServerSet</i>, which you want to
	 * disconect it after the operation.
	 * 
	 * @param serverInfo
	 */
	public static void addDisconnectedServer(ServerInfo serverInfo) {
		if (serverInfo != null) {
			disconnectedServerSet.add(serverInfo);
		}
	}

	/**
	 * Tear down all the ServerInfos stored in <i>disconnectedServerSet</i>.
	 */
	public static void tearDownDisconnectedServer() {
		for (ServerInfo serverInfo : disconnectedServerSet) {
			serverInfo.disConnect();
			serverInfo.setConnected(false);
			CMHostNodePersistManager.getInstance().removeServer(serverInfo.getHostAddress(), 
					serverInfo.getHostMonPort(), serverInfo.getUserName());
		}
		disconnectedServerSet.clear();
	}

	public String getErrorMsg() {
		return errorMsg;
	}
}
