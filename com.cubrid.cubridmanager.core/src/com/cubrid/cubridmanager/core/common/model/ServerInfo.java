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
package com.cubrid.cubridmanager.core.common.model;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cubrid.common.core.common.model.IServerSpec;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.FileUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfos;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.common.task.MonitoringTask;
import com.cubrid.cubridmanager.core.logs.model.LogInfoManager;
import com.cubrid.cubridmanager.core.mondashboard.model.HAHostStatusInfo;
import com.cubrid.cubridmanager.core.shard.model.Shards;
import com.cubrid.jdbc.proxy.manage.JdbcClassLoaderFactory;
import com.cubrid.jdbc.proxy.manage.ServerJdbcVersionMapping;

/**
 * 
 * This class is responsible to cache CUBRID Manager server information
 * 
 * ServerInfo Description
 * 
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class ServerInfo extends PropertyChangeProvider implements IServerSpec {
	public static final String PROP_BEFORE_CONNECT = "BEFORE_CONNECT";
	public static final String PROP_AFTER_CONNECT = "AFTER_CONNECT";
	public static final String PROP_BEFORE_DISCONNECT = "BEFORE_DISCONNECT";
	public static final String PROP_AFTER_DISCONNECT = "AFTER_DISCONNECT";
	// to save server information
	private String serverName = null;
	private String hostAddress = null;
	private int hostMonPort = 8001;
	private int hostJSPort = 8002;
	// to save current server login user and password
	private String userName = null;
	private String userPassword = null;
	private boolean isCheckCertStatus = true;
	// every server only have a monitoring task,it run continuously
	private MonitoringTask monitoringTask = null;
	// to save current server token
	private String hostToken = null;
	// to indicate whether the server connection is OK.
	private boolean isConnected = false;
	private String jdbcDriverVersion = null;
	// CUBRID Manager server user information
	private ServerUserInfo loginedUserInfo = null;
	private List<ServerUserInfo> serverUserInfoList = null;
	// CUBRID Server environment information
	private EnvInfo envInfo = null;
	private ServerType serverType = ServerType.BOTH;
	private LogInfoManager logInfoManager = null;
	private Map<String, Map<String, String>> cubridConfParaMap = null;
	private Map<String, Map<String, String>> brokerConfParaMap = null;
	private Map<String, Map<String, String>> haConfParaMap = null;
	private Map<String, String> cmConfParaMap = null;
	private ConflictChecking brokerConflictChecking = new ConflictChecking();
	private BrokerInfos brokerInfos = null;
	private Shards shards = null;
	private List<String> allDatabaseList = null;
	private String serverVersion = null;
	private static Map<String, Integer> availableCasCount;

	private HAHostStatusInfo haHostStatusInfo;
	// the interface version that be used to connect to cubrid manager server
	private InterfaceVersion interfaceVersion = InterfaceVersion.V1;
	// Socket time out
	private int soTimeOut = SocketTask.SOCKET_IO_TIMEOUT_MSEC;
	private boolean isSupportMonitorStatistic = false;
	// The CMS certificate status 
	private CertStatus certStatus = CertStatus.UNKNOWN;
	// The observers change the clientService in SocketTask when the interface
	// version changed in ServerInfo.
	private static final ConcurrentHashMap<ServerInfo, List<SocketTask>> observer = new ConcurrentHashMap<ServerInfo, List<SocketTask>>();

	/**
	 * Add a observer.
	 * 
	 * @param task
	 */
	public void addObserver(SocketTask task) {
		List<SocketTask> ts = observer.get(this);
		if (null == ts) {
			observer.putIfAbsent(this, new ArrayList<SocketTask>());
			ts = observer.get(this);
		}
		if (!ts.contains(task)) {
			ts.add(task);
		}
	}

	/**
	 * Remove a observer.
	 * 
	 * @param task
	 */
	public void removeObserver(SocketTask task) {
		List<SocketTask> ts = observer.get(this);
		if (null != ts) {
			ts.remove(task);
		}
	}

	private void notice() {
		List<SocketTask> ts = observer.get(this);
		if (null != ts) {
			for (SocketTask task : ts) {
				task.setServerInfo(this);
			}
		}
	}

	/**
	 * 
	 * Get host address
	 * 
	 * @return String
	 */
	public String getHostAddress() {
		return hostAddress;
	}

	/**
	 * 
	 * Set host address
	 * 
	 * @param hostAddress String The address of host
	 */
	public void setHostAddress(String hostAddress) {
		this.hostAddress = hostAddress;
	}

	/**
	 * 
	 * Get host monitor port
	 * 
	 * @return int The host monitor port
	 */
	public int getHostMonPort() {
		return hostMonPort;
	}

	/**
	 * 
	 * Set host monitor port
	 * 
	 * @param hostMonPort int The host monitor port
	 */
	public void setHostMonPort(int hostMonPort) {
		this.hostMonPort = hostMonPort;
	}

	/**
	 * 
	 * Get host JS port
	 * 
	 * @return int The host JS port
	 */
	public int getHostJSPort() {
		return hostJSPort;
	}

	/**
	 * 
	 * Set host JS port
	 * 
	 * @param hostJSPort int The host JS port
	 */
	public void setHostJSPort(int hostJSPort) {
		this.hostJSPort = hostJSPort;
	}

	/**
	 * 
	 * Get host user name
	 * 
	 * @return String The user name
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * 
	 * Set host user name
	 * 
	 * @param userName String The user name
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * 
	 * Get host user password
	 * 
	 * @return String The user password
	 */
	public String getUserPassword() {
		return userPassword;
	}

	/**
	 * 
	 * Set host user password
	 * 
	 * @param userPassword String The user password
	 */
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	/**
	 * 
	 * Get host connected token
	 * 
	 * @return String The host token
	 */
	public String getHostToken() {
		return hostToken;
	}

	/**
	 * 
	 * Set host connected token
	 * 
	 * @param hostToken String The host token
	 */
	public void setHostToken(String hostToken) {
		this.hostToken = hostToken;
	}

	/**
	 * 
	 * Return host connected status
	 * 
	 * @return boolean
	 */
	public boolean isConnected() {
		return isConnected;
	}

	/**
	 * 
	 * Set host connected status
	 * 
	 * @param isConnected boolean Whether is connected
	 */
	public void setConnected(boolean isConnected) {
		synchronized (this) {
			boolean nowIsConnected = this.isConnected;
			this.isConnected = isConnected;
			if (monitoringTask != null && nowIsConnected && !isConnected) {
				monitoringTask.stopMonitor();
				monitoringTask = null;
			}
			if (nowIsConnected && !isConnected) {
				this.firePropertyChange(PROP_BEFORE_DISCONNECT, null, this);
				disConnect();
				this.firePropertyChange(PROP_AFTER_DISCONNECT, null, this);
			} else if (!nowIsConnected && isConnected) {
				this.firePropertyChange(PROP_AFTER_CONNECT, null, this);
			}
		}
	}

	/**
	 * 
	 * Get server name
	 * 
	 * @return String The server name
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * 
	 * Set server name
	 * 
	 * @param serverName String The server name
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	/**
	 * 
	 * Get monitoring task of each server
	 * 
	 * @return MonitoringTask The monitoring task
	 */
	public MonitoringTask getMonitoringTask() {
		synchronized (this) {
			if (monitoringTask == null) {
				monitoringTask = new MonitoringTask(this);
			}
			return monitoringTask;
		}
	}

	/**
	 * 
	 * Get login user information
	 * 
	 * @return ServerUserInfo The login user info
	 */
	public ServerUserInfo getLoginedUserInfo() {
		return loginedUserInfo;
	}

	/**
	 * 
	 * Set login user information
	 * 
	 * @param loginedUser ServerUserInfo The server info of login user
	 */
	public void setLoginedUserInfo(ServerUserInfo loginedUser) {
		this.loginedUserInfo = loginedUser;
	}

	/**
	 * 
	 * Get all CUBRID Manager server user information
	 * 
	 * @return List<ServerUserInfo>
	 */
	public List<ServerUserInfo> getServerUserInfoList() {
		return serverUserInfoList;
	}

	/**
	 * 
	 * Get CUBRID Manager server user information by user name
	 * 
	 * @param userName String The user name
	 * @return ServerUserInfo The server info
	 */
	public ServerUserInfo getServerUserInfo(String userName) {
		if (serverUserInfoList != null) {
			for (int i = 0; i < serverUserInfoList.size(); i++) {
				ServerUserInfo userInfo = serverUserInfoList.get(i);
				if (userInfo != null && userInfo.getUserName().equals(userName)) {
					return userInfo;
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * Set CUBRID Manager server user information list
	 * 
	 * @param serverUserInfoList List<ServerUserInfo> The server info list
	 */
	public void setServerUserInfoList(List<ServerUserInfo> serverUserInfoList) {
		this.serverUserInfoList = serverUserInfoList;
	}

	/**
	 * 
	 * Add CUBRID Manager server user information
	 * 
	 * @param serverUserInfo ServerUserInfo Ther server info
	 */
	public void addServerUserInfo(ServerUserInfo serverUserInfo) {
		if (serverUserInfoList == null) {
			serverUserInfoList = new ArrayList<ServerUserInfo>();
		}
		if (serverUserInfo != null && !serverUserInfoList.contains(serverUserInfo)) {
			serverUserInfoList.add(serverUserInfo);
			if (serverUserInfo.getUserName().equals(userName)) {
				setLoginedUserInfo(serverUserInfo);
			}
		}
	}

	/**
	 * 
	 * Remove CUBRID Manager server user information
	 * 
	 * @param serverUserInfo ServerUserInfo The server info
	 */
	public void removeServerUserInfo(ServerUserInfo serverUserInfo) {
		if (serverUserInfoList != null) {
			serverUserInfoList.remove(serverUserInfo);
			if (serverUserInfo != null && serverUserInfo.getUserName().equals(userName)) {
				setLoginedUserInfo(null);
			}
		}
	}

	/**
	 * 
	 * Remove all server user information
	 * 
	 */
	public void removeAllServerUserInfo() {
		if (serverUserInfoList != null) {
			serverUserInfoList.clear();
		}
	}

	/**
	 * 
	 * Get CUBRID Server env information
	 * 
	 * @return EnvInfo
	 */
	public EnvInfo getEnvInfo() {
		return envInfo;
	}

	/**
	 * 
	 * Set CUBRID Server env information
	 * 
	 * @param envInfo EnvInfo The environment info
	 */
	public void setEnvInfo(EnvInfo envInfo) {
		this.envInfo = envInfo;
	}

	/**
	 * 
	 * When server disconnect,clear all resource
	 * 
	 */
	public void disConnect() {
		synchronized (this) {
			setHostToken(null);
			serverUserInfoList = null;
			allDatabaseList = null;
			setLoginedUserInfo(null);
			setEnvInfo(null);
			brokerInfos = null;
			shards = null;
			cubridConfParaMap = null;
			brokerConfParaMap = null;
			logInfoManager = null;
			brokerConflictChecking.clear();
		}
	}

	/**
	 * 
	 * Get log information manager
	 * 
	 * @return LogInfoManager The object of LogInfoManager
	 */
	public LogInfoManager getLogInfoManager() {
		synchronized (this) {
			if (logInfoManager == null) {
				logInfoManager = new LogInfoManager();
			}
			return logInfoManager;
		}
	}

	/**
	 * 
	 * Get all broker information
	 * 
	 * @return BrokerInfos The broker info
	 */
	public BrokerInfos getBrokerInfos() {
		return brokerInfos;
	}

	/**
	 * 
	 * Set broker information
	 * 
	 * @param brokerInfos BrokerInfos The broker info
	 */
	public void setBrokerInfos(BrokerInfos brokerInfos) {
		this.brokerInfos = brokerInfos;
	}

	public Shards getShards() {
		return shards;
	}

	public void setShards(Shards shards) {
		this.shards = shards;
	}

	/**
	 * 
	 * Get all database list
	 * 
	 * @return List<String> The database list that may include all the database
	 */
	public List<String> getAllDatabaseList() {
		return allDatabaseList;
	}

	/**
	 * 
	 * Set all database list
	 * 
	 * @param allDatabaseList List<String> The database list that may include
	 *        all the database
	 */
	public void setAllDatabaseList(List<String> allDatabaseList) {
		this.allDatabaseList = allDatabaseList;
	}

	/**
	 * 
	 * Remove all databases
	 * 
	 */
	public void removeAllDatabase() {
		if (this.allDatabaseList != null) {
			this.allDatabaseList.clear();
		}
	}

	/**
	 * 
	 * Add a database
	 * 
	 * @param dbName String The database name
	 */
	public void addDatabase(String dbName) {
		if (this.allDatabaseList == null) {
			this.allDatabaseList = new ArrayList<String>();
		}
		this.allDatabaseList.add(dbName);
	}

	/**
	 * Return whether the server host is local
	 * 
	 * @return boolean
	 */
	public boolean isLocalServer() {
		boolean isFlag = false;
		if (hostAddress != null && hostAddress.length() > 0) {
			InetAddress[] addrs;
			try {
				addrs = InetAddress.getAllByName(hostAddress);
				String ip = InetAddress.getLocalHost().getHostAddress();
				if (addrs.length <= 0) {
					return false;
				}
				for (int i = 0; i < addrs.length; i++) {
					if (addrs[i].getHostAddress().equals(ip)
							|| addrs[i].getHostAddress().equals("127.0.0.1")) {
						isFlag = true;
					}
				}
			} catch (UnknownHostException e) {
				isFlag = false;
			}
		}
		return isFlag;
	}

	/**
	 * Get server os info
	 * 
	 * @return OsInfoType The OS info type
	 */
	public FileUtil.OsInfoType getServerOsInfo() {
		if (envInfo == null) {
			return null;
		}
		String osInfo = envInfo.getOsInfo();
		if (StringUtil.isEmpty(osInfo)) {
			return null;
		}
		osInfo = osInfo.toUpperCase(Locale.getDefault());
		return FileUtil.OsInfoType.eval(osInfo);
	}

	/**
	 * get the path Separator of the server system
	 * 
	 * @return String The path separator
	 */
	public String getPathSeparator() {
		return FileUtil.getSeparator(getServerOsInfo());
	}

	/**
	 * 
	 * Get cubrid.conf file all parameters
	 * 
	 * @return Map<String, Map<String, String>> The CUBRID configure parameter
	 *         map
	 */
	public Map<String, Map<String, String>> getCubridConfParaMap() {
		return cubridConfParaMap;
	}

	/**
	 * 
	 * Get parameter in cubrid.conf file
	 * 
	 * @param para String The parameter name
	 * @param databaseName String The database name
	 * @return String
	 */
	public String getCubridConfPara(String para, String databaseName) {
		if (null != cubridConfParaMap) {
			Map<String, String> map = cubridConfParaMap.get("service");
			String ret = "";
			if (map != null) {
				ret = map.get(para);
				if (null != ret) {
					return ret;
				}
			}
			if (databaseName != null && databaseName.trim().length() > 0) {
				map = cubridConfParaMap.get("[@" + databaseName + "]");
				ret = map == null ? null : map.get(para);
				if (null != ret) {
					return ret;
				}
			}
			map = cubridConfParaMap.get("common");
			if (map != null) {
				ret = map.get(para);
				if (null != ret) {
					return ret;
				}
			}
			String dbBaseParas[][] = ConfConstants.getDbBaseParameters(this);
			for (String[] strs : dbBaseParas) {
				if (strs[0].equals(para)) {
					return strs[1];
				}
			}
			String dbAdvancedParameters[][] = ConfConstants.getDbAdvancedParameters(
					this, databaseName == null
							|| databaseName.trim().length() == 0);
			for (String[] strs : dbAdvancedParameters) {
				if (strs[0].equals(para)) {
					return strs[2];
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * Set cubrid.conf parameters
	 * 
	 * @param cubridConfParaMap Map<String, Map<String, String>> The CUBRID
	 *        configure parameter Map
	 */
	public void setCubridConfParaMap(
			Map<String, Map<String, String>> cubridConfParaMap) {
		this.cubridConfParaMap = cubridConfParaMap;
	}

	/**
	 * 
	 * Get cubrid_broker.conf all parameters
	 * 
	 * @return Map<String, Map<String, String>> The CUBRID configure parameter
	 *         map
	 */
	public Map<String, Map<String, String>> getBrokerConfParaMap() {
		return brokerConfParaMap;
	}

	/**
	 * 
	 * Set cubrid_broker.conf parameters
	 * 
	 * @param brokerConfParaMap Map<String, Map<String, String>> The CUBID
	 *        configure parameter map
	 */
	public void setBrokerConfParaMap(
			Map<String, Map<String, String>> brokerConfParaMap) {
		this.brokerConfParaMap = brokerConfParaMap;
		this.brokerConflictChecking.updateValue(this.brokerConfParaMap);
	}

	/**
	 * 
	 * Get cm.conf all parameters
	 * 
	 * @return Map<String, String> The cm configure parameter map
	 */
	public Map<String, String> getCmConfParaMap() {
		return cmConfParaMap;
	}

	/**
	 * 
	 * Set cubrid_broker.conf parameters
	 * 
	 * @param cmConfParaMap Map<String, String> The cm configure parameter map
	 */
	public void setCmConfParaMap(Map<String, String> cmConfParaMap) {
		this.cmConfParaMap = cmConfParaMap;
	}

	public Map<String, Map<String, String>> getHaConfParaMap() {
		return haConfParaMap;
	}

	public void setHaConfParaMap(Map<String, Map<String, String>> haConfParaMap) {
		this.haConfParaMap = haConfParaMap;
	}

	/**
	 * 
	 * Return the server type of this server
	 * 
	 * @return ServerType The server type
	 */
	public ServerType getServerType() {
		return serverType;
	}

	/**
	 * 
	 * Set the server type of this server
	 * 
	 * @param serverType ServerType The server type
	 */
	public void setServerType(ServerType serverType) {
		this.serverType = serverType;
	}

	/**
	 * 
	 * Get the JDBC Driver version info
	 * 
	 * @return String the version(format:CUBRID-JDBC-8.2.0.1147)
	 */
	public String getJdbcDriverVersion() {
		if (jdbcDriverVersion == null)
			return "";
		return jdbcDriverVersion;
	}
	
	/**
	 * Get the interface version of the manage server
	 * 
	 * @return
	 */
	public InterfaceVersion getInterfaceVersion() {
		return interfaceVersion;
	}

	/**
	 * Set the interface version of the manage server
	 * 
	 * @param interfaceVersion
	 */
	public void setInterfaceVersion(InterfaceVersion interfaceVersion) {
		this.interfaceVersion = interfaceVersion;
		this.notice();
	}

	/**
	 * 
	 * Get auto detect JDBC version by server version
	 * 
	 * <p>
	 * Before call this method,firstly register JDBC
	 * classLoader(JdbcClassLoaderFactory#registerClassLoader(String));if not
	 * registered the classLoader,will return null
	 * </p>
	 * <p>
	 * it will return the adapting version
	 * information(eg.CUBRID-JDBC-8.2.0.1147) from classLoader factory,if no
	 * adapting version information,will return null;
	 * </p>
	 * 
	 * @param serverVersion String(format:8.3.1.1015)
	 * @return JDBC version
	 * @see JdbcClassLoaderFactory#registerClassLoader(Map)
	 */
	public static String getAutoDetectJdbcVersion(String serverVersion) {
		Map<String, ClassLoader> loaders = JdbcClassLoaderFactory.getClassLoaderMap();
		final String jdbcPrefix = "CUBRID-JDBC-";
		//complete match
		String jdbcVersion = "CUBRID-JDBC-" + serverVersion;
		if (loaders.get(jdbcVersion) != null) {
			return jdbcVersion;
		}
		//Now JDBC only support to the same minor version(8.3.1.[0-9]{4,})
		//String[] parameterValue = ServerJdbcVersionMapping.getSupportedJdbcVersions(serverVersion);

		String supportedVersion = serverVersion.substring(0, serverVersion.lastIndexOf("."));
		supportedVersion = jdbcPrefix + supportedVersion + ".[0-9]{4,}";
		String[] parameterValue = new String[] { supportedVersion };

		List<String> matchedVersionList = new ArrayList<String>();
		for (String version : parameterValue) {
			Iterator<String> iterator = loaders.keySet().iterator();
			while (iterator.hasNext()) {
				String version2 = iterator.next();
				if (version2 != null && version2.matches(version)) {
					matchedVersionList.add(version2);
				}
			}
		}
		if (matchedVersionList.isEmpty()) {
			return null;
		}
		if (matchedVersionList.size() == 1) {
			return matchedVersionList.get(0);
		}

		matchedVersionList.add(jdbcVersion);
		Collections.sort(matchedVersionList, new Comparator<String>() {
			public int compare(String str1, String str2) {
				String[] strArr1 = str1.replaceAll(jdbcPrefix, "").split("\\.");
				String[] strArr2 = str2.replaceAll(jdbcPrefix, "").split("\\.");
				for (int i = 0; i < strArr1.length && i < strArr2.length; i++) {
					if (!strArr1[i].matches("^\\d+$")
							|| !strArr2[i].matches("^\\d+$")) {
						return 0;
					}
					int int1 = Integer.parseInt(strArr1[i]);
					int int2 = Integer.parseInt(strArr2[i]);
					if (int1 > int2) {
						return 1;
					} else if (int1 < int2) {
						return -1;
					}
				}
				return 0;
			}
		});
		int length = matchedVersionList.size();
		for (int i = 0; i < length; i++) {
			String version = matchedVersionList.get(i);
			if (jdbcVersion.equals(version)) {
				if (i + 1 < length) {
					return matchedVersionList.get(i + 1);
				} else if (i > 0) {
					return matchedVersionList.get(i - 1);
				}
			}
		}
		return null;
	}

	/**
	 * Validate whether this server support this JDBC version(eg.self-adapting
	 * or CUBRID-JDBC-8.2.0.1147)
	 * 
	 * <p>
	 * Before call this method,firstly register JDBC
	 * classLoader(JdbcClassLoaderFactory#registerClassLoader(String));otherwise
	 * return false;
	 * </p>
	 * 
	 * @param jdbcVersion the JDBC version info
	 * @return boolean <code>true</code>if it can be supported;
	 *         <code>false</code>otherwise
	 * @see JdbcClassLoaderFactory#registerClassLoader(Map)
	 */
	public boolean validateJdbcVersion(String jdbcVersion) {
		if (jdbcVersion == null || jdbcVersion.equals("")) {
			return false;
		}
		if (isConnected) {
			//Now JDBC only support to the same minor version(8.3.1.[0-9]{4,})
			//String[] parameterValue = ServerJdbcVersionMapping.getSupportedJdbcVersions(getServerVersionKey());
			String supportedVersion = getServerVersionKey();
			supportedVersion = "CUBRID-JDBC-" + supportedVersion + ".[0-9]{4,}";
			String[] parameterValue = new String[] { supportedVersion };

			if (ServerJdbcVersionMapping.JDBC_SELF_ADAPTING_VERSION.equals(jdbcVersion)) {
				// self-adapting jdbc version
				Map<String, ClassLoader> loaders = JdbcClassLoaderFactory.getClassLoaderMap();
				for (String version : parameterValue) {
					Iterator<String> iterator = loaders.keySet().iterator();
					while (iterator.hasNext()) {
						String version2 = iterator.next();
						if (version2 != null && version2.matches(version)) {
							return true;
						}
					}
				}
			} else {
				if (!JdbcClassLoaderFactory.getClassLoaderMap().containsKey(
						jdbcVersion)) {
					return false;
				}
				for (String version : parameterValue) {
					if (jdbcVersion.matches(version)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public String getLatestJdbcVersion() {
	    String retVersion = null;
		String[] tempString;
		int[] tempInt = {0,0,0,0};
		int[] latestVersion = {0,0,0,0};

		if (isConnected) {
			Map<String, ClassLoader> loaders = JdbcClassLoaderFactory.getClassLoaderMap();
			Iterator<String> iterator = loaders.keySet().iterator();
			while (iterator.hasNext()) {
				String version2 = iterator.next();
				tempString = version2.replaceAll("[^0-9.]","").split("\\.");

				for (int i = 0; i < tempString.length; i++) {
					tempInt[i] = Integer.valueOf(tempString[i]).intValue();
		        }

				for (int i = 0; i < tempInt.length; i++) {
					if (latestVersion[i] < tempInt[i]) {
						latestVersion = tempInt.clone();
						retVersion = version2;
						break;
					} else if (latestVersion[i] > tempInt[i]) {
						break;
					}
				}
			}
		}
		return retVersion;
	}

	/**
	 * Get server Version from "CUBRID 2008 R2.0(8.2.0.1150)" to "8.2.0"
	 * 
	 * 
	 * @return String The server version key(format:8.2.0)
	 */
	public String getServerVersionKey() {
		if (!isConnected) {
			return "";
		}
		String serverVersion = null;
		if (getEnvInfo() == null) {
			serverVersion = this.serverVersion == null ? "" : this.serverVersion;
		} else {
			serverVersion = getEnvInfo().getServerVersion();
		}
		return CompatibleUtil.parseServerVersion(serverVersion);
	}

	public void setServerVersion(String serverVersion) {
		this.serverVersion = serverVersion;
	}

	/**
	 * Get server Version from "CUBRID 2008 R2.0(8.2.0.1150)" to "8.2.0.1150"
	 * 
	 * 
	 * @return String The server version key(format:8.2.0.1150)
	 */
	public String getFullServerVersionKey() {
		if (!isConnected || getEnvInfo() == null) {
			return "";
		}
		String serverVersion = getEnvInfo().getServerVersion();
		if (serverVersion != null && !"".equals(serverVersion)) {
			String tmp = serverVersion.substring(1 + serverVersion.indexOf("("));
			return tmp.substring(0, tmp.indexOf(")"));
		}
		return "";
	}

	/**
	 * Set JDBC driver version(eg.self-adapting or CUBRID-JDBC-8.2.0.1147)
	 * 
	 * @param jdbcDriverVersion String the JDBC driver version
	 */
	public void setJdbcDriverVersion(String jdbcDriverVersion) {
		this.jdbcDriverVersion = jdbcDriverVersion;
	}

	/**
	 * 
	 * Get whether the current server support replication(after 8.2.2 and linux)
	 * 
	 * @return the value <code>0</code> supported; <code>1</code> invalid
	 *         platform;<code>2</code> invalid server type;<code>3</code> not
	 *         supported version;<code>4</code> not login
	 */
	public int isSupportReplication() {
		if (getEnvInfo() == null) {
			return 4;
		}
		if (CompatibleUtil.isWindows(getServerOsInfo())) {
			return 1;
		}
		ServerType serverType = getServerType();
		if (serverType == ServerType.BROKER) {
			return 2;
		}
		return 3;
		/*if (compareVersionKey("8.3.0") >= 0) {
			return 0;
		} else {
			return 3;
		}*/
	}

	/**
	 * 
	 * Check whether the database or the server is HA Mode. if the dbName is
	 * null, return this server HA mode, otherwise, return this database HA mode
	 * 
	 * @param dbName The String
	 * @return The boolean
	 */
	public boolean isHAMode(String dbName) {
		boolean isHAMode = false;
		String haMode = getCubridConfPara(ConfConstants.HA_MODE, null);
		if (haMode != null
				&& (haMode.equalsIgnoreCase("ON")
						|| haMode.equalsIgnoreCase("YES")
						|| haMode.equalsIgnoreCase("REPLICA"))) {
			isHAMode = true;
		} else {
			isHAMode = false;
		}
		if (dbName == null || dbName.trim().length() == 0) {
			return isHAMode;
		}
		haMode = getCubridConfPara(ConfConstants.HA_MODE, dbName);
		boolean isDbHAMode = false;
		if (haMode == null || haMode.trim().length() == 0
				|| haMode.equalsIgnoreCase("ON")
				|| haMode.equalsIgnoreCase("YES")
				|| haMode.equalsIgnoreCase("REPLICA")) {
			isDbHAMode = true;
		} else {
			isDbHAMode = false;
		}
		return isHAMode && isDbHAMode;
	}

	/**
	 * The interface version enum.
	 * 
	 * @author Tobi
	 * 
	 * @version 1.0
	 * @date 2012-10-17
	 */
	public enum InterfaceVersion {
		V1("V1", "Using socket protocol."), V2("V2", "Using http protocol.");
		private String name;
		private String description;

		public static final ArrayList<InterfaceVersion> ALL_INTERFACE_VERSION = new ArrayList<InterfaceVersion>();
		static {
			ALL_INTERFACE_VERSION.add(V2);
			ALL_INTERFACE_VERSION.add(V1);
		}

		InterfaceVersion(String name, String description) {
			this.name = name;
			this.description = description;
		}

		private boolean valid(String cmsVersion) {
			return null == cmsVersion ? false : this.name.equalsIgnoreCase(cmsVersion);
		}

		public static InterfaceVersion getInterfaceVersion(String cmsVersion) {
			if (V2.valid(cmsVersion)) {
				return V2;
			}
			if (V1.valid(cmsVersion)) {
				return V1;
			}
			// InterfaceVersion.values()[0];
			return ALL_INTERFACE_VERSION.get(0);
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}
	}

	public boolean checkBrokerPortConflicts(String value) {
		return brokerConflictChecking.checkPortConflicts(value);
	}

	public boolean checkBrokerShmIdConflicts(String value) {
		return brokerConflictChecking.checkShmIdConflicts(value);
	}

	/**
	 * Conflict checking utilities <br>
	 * for broker <br>
	 * 
	 * @author Tobi
	 * 
	 * @version 1.0
	 * @date 2012-12-5
	 */
	private final class ConflictChecking {
		private final List<String> portCache = new ArrayList<String>();
		private final List<String> shmIdCache = new ArrayList<String>();

		/**
		 * Refresh cache.
		 * 
		 * @param confParaMap
		 *            broker or shard configuration parameters
		 */
		public void updateValue(Map<String, Map<String, String>> confParaMap) {
			for (Map.Entry<String, Map<String, String>> entries : confParaMap.entrySet()) {
				Map<String, String> valueout = entries.getValue();
				for (Map.Entry<String, String> entry : valueout.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					if (key.indexOf("_PORT") > -1) {
						portCache.add(value);
					} else if (key.indexOf("_SHM_ID") > -1) {
						shmIdCache.add(value);
					}
				}
			}
		}

		public void clear() {
			portCache.clear();
			shmIdCache.clear();
		}

		public boolean checkPortConflicts(String value) {
			return portCache.contains(value);
		}

		public boolean checkShmIdConflicts(String value) {
			return shmIdCache.contains(value);
		}
	}

	public HAHostStatusInfo getHaHostStatusInfo() {
		return haHostStatusInfo;
	}

	public void setHaHostStatusInfo(HAHostStatusInfo haHostStatusInfo) {
		this.haHostStatusInfo = haHostStatusInfo;
	}

	public int getSoTimeOut() {
		return soTimeOut;
	}

	public void setSoTimeOut(int soTimeOut) {
		if(soTimeOut >= 0) {
			this.soTimeOut = soTimeOut;
		}
	}

	public boolean isSupportMonitorStatistic() {
		return isSupportMonitorStatistic;
	}

	public void setSupportMonitorStatistic(boolean isSupportMonitorStatistic) {
		this.isSupportMonitorStatistic = isSupportMonitorStatistic;
	}

	public CertStatus getCertStatus() {
		return certStatus;
	}

	public void setCertStatus(CertStatus certStatus) {
		this.certStatus = certStatus;
	}

	public boolean isCheckCertStatus() {
		return isCheckCertStatus;
	}

	public void setCheckCertStatus(boolean isCheckCertStatus) {
		this.isCheckCertStatus = isCheckCertStatus;
	}
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof ServerInfo)){
			return false;
		}
		ServerInfo serverInfo = (ServerInfo)obj;
		return serverInfo.getHostAddress().compareTo(getHostAddress()) == 0 &&
				serverInfo.getHostMonPort() == getHostMonPort() &&
				serverInfo.getUserName().compareTo(getUserName()) == 0;
	}
}
