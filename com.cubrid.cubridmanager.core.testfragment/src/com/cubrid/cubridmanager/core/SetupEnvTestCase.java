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
package com.cubrid.cubridmanager.core;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.cubridmanager.core.broker.task.GetBrokerConfParameterTask;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.model.ConfConstants;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.common.model.EnvInfo;
import com.cubrid.cubridmanager.core.common.model.OnOffType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.model.ServerType;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.common.socket.ClientSocket;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.common.task.CommonTaskName;
import com.cubrid.cubridmanager.core.common.task.CommonUpdateTask;
import com.cubrid.cubridmanager.core.common.task.GetCMConfParameterTask;
import com.cubrid.cubridmanager.core.common.task.GetCMUserListTask;
import com.cubrid.cubridmanager.core.common.task.GetCubridConfParameterTask;
import com.cubrid.cubridmanager.core.common.task.GetEnvInfoTask;
import com.cubrid.cubridmanager.core.common.task.MonitoringTask;
import com.cubrid.cubridmanager.core.common.task.UpdateCMUserTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.UserSendObj;
import com.cubrid.cubridmanager.core.cubrid.database.task.CreateDbTask;
import com.cubrid.cubridmanager.core.cubrid.database.task.GetDatabaseListTask;
import com.cubrid.cubridmanager.core.cubrid.database.task.LoginDatabaseTask;
import com.cubrid.cubridmanager.core.cubrid.jobauto.task.BackupPlanTask;
import com.cubrid.cubridmanager.core.cubrid.jobauto.task.DelBackupPlanTask;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.cubridmanager.core.cubrid.user.task.UpdateAddUserTask;
import com.cubrid.cubridmanager.core.utils.ModelUtil.YesNoType;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;
import com.cubrid.jdbc.proxy.manage.JdbcClassLoaderFactory;

/**
 *
 * Set up the test env. All test cases which test the tasks extends from
 * SocketTask will extend it.
 *
 * @author pangqiren
 * @version 1.0 - 2010-1-5 created by pangqiren
 */
public abstract class SetupEnvTestCase extends
		TestCase {
	private static final Logger LOGGER = LogUtil.getLogger(SetupEnvTestCase.class);
	
	// to store the latest token, use for sending message
	protected static String token = null;
	// to store the host
	protected static String host = SystemParameter.getParameterValue("hostIp");
	// the monitor port in the host, use when login
	protected static int monport = SystemParameter.getParameterIntValue("monport");
	// the job service port in the host, use when requesting job service
	protected static int jpport = SystemParameter.getParameterIntValue("jpport");
	// the monitor socket, which must be kept alive during the whole session
	protected static ClientSocket hostsocket = null;
	// the ServerInfo Object, use when initial a task
	protected static ServerInfo serverInfo = null;
	// the CUBRID Manager server version
	protected static String serverVersion = SystemParameter.getParameterValue("serverVersion");
	// the CUBRID Manager server userName
	protected static String userName = SystemParameter.getParameterValue("userName");
	protected static String passwd = SystemParameter.getParameterValue("password");

	// the test database name, use when integration testing with a real database
	protected static String testDbName = SystemParameter.getParameterValue("dbname");
	protected static String dbaUserName = "dba";
	protected static String dbaPassword = SystemParameter.getParameterValue("dbaPass");
	protected static String charset = SystemParameter.getParameterValue("charset");
	protected static String port = SystemParameter.getParameterValue("port");
	//test databaseInfo
	protected static DatabaseInfo databaseInfo = null;
	// CUBRID test server path
	protected static String serverPath = "";
	protected static String dbRootDir = "";
	protected static EnvInfo envInfo = null;

	//check whether coonect the real env
	protected static boolean isConnectRealEnv = SystemParameter.getParameterValue(
			"isConnectRealEnv").equalsIgnoreCase("y");

	/*Cubrid 8.3.1*/
	protected static String host831 = SystemParameter.getParameterValue("hostIp831");
	// the monitor port in the host, use when login
	protected static int monport831 = SystemParameter.getParameterIntValue("monport831");
	// the job service port in the host, use when requesting job service
	protected static int jpport831 = SystemParameter.getParameterIntValue("jpport831");
	// the test database name, use when integration testing with a real database
	protected static String testDbName831 = SystemParameter.getParameterValue("dbname831");
	protected static String dbaUserName831 = "dba";
	protected static String dbaPassword831 = SystemParameter.getParameterValue("dbaPass831");
	protected static String charset831 = SystemParameter.getParameterValue("charset831");
	protected static String port831 = SystemParameter.getParameterValue("port831");
	// the CUBRID Manager server userName
	protected static String userName831 = SystemParameter.getParameterValue("userName831");
	protected static String passwd831 = SystemParameter.getParameterValue("password831");
	//test databaseInfo
	protected static DatabaseInfo databaseInfo831 = null;
	// the CUBRID Manager server version
	protected static String serverVersion831 = SystemParameter.getParameterValue("serverVersion831");
	// the ServerInfo Object, use when initial a task
	protected static ServerInfo serverInfo831 = null;
	// CUBRID test server path
	protected static String serverPath831 = "";
	protected static String dbRootDir831 = "";
	protected static EnvInfo envInfo831 = null;
	protected static String token831 = null;

	/*Cubrid 9.2.0*/
	protected static String host930 = SystemParameter.getParameterValue("hostIp930");
	// the monitor port in the host, use when login
	protected static int monport930 = SystemParameter.getParameterIntValue("monport930");
	// the job service port in the host, use when requesting job service
	protected static int jpport930 = SystemParameter.getParameterIntValue("jpport930");
	// the test database name, use when integration testing with a real database
	protected static String testDbName930 = SystemParameter.getParameterValue("dbname930");
	protected static String dbaUserName930 = "dba";
	protected static String dbaPassword930 = SystemParameter.getParameterValue("dbaPass930");
	protected static String charset930 = SystemParameter.getParameterValue("charset930");
	protected static String port930 = SystemParameter.getParameterValue("port930");
	// the CUBRID Manager server userName
	protected static String userName930 = SystemParameter.getParameterValue("userName930");
	protected static String passwd930 = SystemParameter.getParameterValue("password930");
	//test databaseInfo
	protected static DatabaseInfo databaseInfo930 = null;
	// the CUBRID Manager server version
	protected static String serverVersion930 = SystemParameter.getParameterValue("serverVersion930");
	// the ServerInfo Object, use when initial a task
	protected static ServerInfo serverInfo930 = null;
//	// CUBRID test server path
	protected static String serverPath930 = "";
	protected static String dbRootDir930 = "";
	protected static EnvInfo envInfo930 = null;
	protected static String token930 = null;

	/**
	 * Set environment for subtestcases, providing the latest token, the
	 * ServerInfo object and so on
	 */
	protected void setUp() throws Exception {
		super.setUp();
		if (!this.isSetupDatabase()) {
			return;
		}
		
		if (serverInfo == null) {
			setupDatabaseInfo();
		}
		if (serverInfo831 == null) {
			setupDatabaseInfo831();
		}
		if (serverInfo930 == null) {
			setupDatabaseInfo930();
		}

	}

	protected boolean isSetupDatabase() {
		return true;
	}

	/**
	 * Setup database 8.4.0
	 *
	 */
	private void setupDatabaseInfo() {

		serverInfo = new ServerInfo();
		serverInfo.setHostAddress(host);
		serverInfo.setHostMonPort(monport);
		serverInfo.setHostJSPort(jpport);
		serverInfo.setUserName(userName);
		serverInfo.setUserPassword(passwd);
		serverInfo.setJdbcDriverVersion(serverVersion);
		CMHostNodePersistManager.getInstance().addServer(host, monport, userName, serverInfo);

		MonitoringTask monTask = new MonitoringTask(serverInfo);
		serverInfo = monTask.connectServer(serverVersion, 1000);

		// get the latest token
		token = serverInfo.getHostToken();
		//get evnInfo
		if (envInfo == null) {
			GetEnvInfoTask envTask = new GetEnvInfoTask(serverInfo);
			envTask.loadEnvInfo();
			envTask.execute();
			envInfo = envTask.loadEnvInfo();
			serverInfo.setEnvInfo(envInfo);
		}

		assertNotNull(envInfo);
		serverPath = envInfo.getRootDir();
		dbRootDir = envInfo.getDatabaseDir();
		//get server type
		GetCMConfParameterTask getCMConfParameterTask = new GetCMConfParameterTask(
				serverInfo);
		getCMConfParameterTask.execute();
		assertEquals(null, getCMConfParameterTask.getErrorMsg());
		Map<String, String> confParameters = getCMConfParameterTask.getConfParameters();
		ServerType serverType = ServerType.BOTH;
		if (confParameters != null) {
			String target = confParameters.get(ConfConstants.CM_TARGET);
			if (target != null) {
				if (target.indexOf("broker") >= 0
						&& target.indexOf("server") >= 0) {
					serverType = ServerType.BOTH;
				} else if (target.indexOf("broker") >= 0) {
					serverType = ServerType.BROKER;
				} else if (target.indexOf("server") >= 0) {
					serverType = ServerType.DATABASE;
				}
			}
		}
		if (serverInfo != null) {
			serverInfo.setServerType(serverType);
		}
		//Get database list task
		if (serverType == ServerType.DATABASE || serverType == ServerType.BOTH) {
			final GetDatabaseListTask getDatabaseListTask = new GetDatabaseListTask(
					serverInfo);
			getDatabaseListTask.execute();
			assertEquals(null, getDatabaseListTask.getErrorMsg());
			List<DatabaseInfo> databaseInfoList = getDatabaseListTask.loadDatabaseInfo();
			for (DatabaseInfo dbInfo : databaseInfoList) {
				if (dbInfo.getDbName().equals(testDbName)) {
					databaseInfo = dbInfo;
					break;
				}
			}
			if (databaseInfo == null) {
				return;
			}
		}
		//get CubridManager user list task
		GetCMUserListTask getUserInfoTask = new GetCMUserListTask(serverInfo);
		getUserInfoTask.execute();
		assertEquals(null, getUserInfoTask.getErrorMsg());
		List<ServerUserInfo> serverUserInfoList = getUserInfoTask.getServerUserInfoList();
		for (int i = 0; serverUserInfoList != null
				&& i < serverUserInfoList.size(); i++) {
			ServerUserInfo userInfo = serverUserInfoList.get(i);
			if (userInfo != null
					&& userInfo.getUserName().equals(serverInfo.getUserName())) {
				serverInfo.setLoginedUserInfo(userInfo);
				break;
			}
		}
		List<DatabaseInfo> databaseInfoList = serverInfo.getLoginedUserInfo().getDatabaseInfoList();
		String dbDir = databaseInfo.getDbDir();
		DbRunningType type = databaseInfo.getRunningType();
		for (int i = 0; databaseInfoList != null && i < databaseInfoList.size(); i++) {
			if (testDbName.equalsIgnoreCase(databaseInfoList.get(i).getDbName())) {
				databaseInfo = databaseInfoList.get(i);
				databaseInfo.setDbDir(dbDir);
				databaseInfo.setBrokerIP(host);
				databaseInfo.setBrokerPort(port);
				databaseInfo.setCharSet(charset);
				databaseInfo.setRunningType(type);
				break;
			}
		}
		//get CUBRID conf parameter
		GetCubridConfParameterTask getCubridConfParameterTask = new GetCubridConfParameterTask(
				serverInfo);
		getCubridConfParameterTask.execute();
		assertEquals(null, getCubridConfParameterTask.getErrorMsg());
		Map<String, Map<String, String>> confParas = getCubridConfParameterTask.getConfParameters();
		if (serverInfo != null) {
			serverInfo.setCubridConfParaMap(confParas);
		}

		//get broker Info
		GetBrokerConfParameterTask getBrokerConfParameterTask = new GetBrokerConfParameterTask(
				serverInfo);
		getBrokerConfParameterTask.execute();
		assertEquals(null, getBrokerConfParameterTask.getErrorMsg());
		confParas = getBrokerConfParameterTask.getConfParameters();
		if (serverInfo != null) {
			serverInfo.setBrokerConfParaMap(confParas);
		}

		//Set JDBC driver
		String filePath = this.getFilePathInPlugin("/lib/JDBC-8.3.0.1004-cubrid.jar");
		if (filePath != null) {
			String version = JdbcClassLoaderFactory.validateJdbcFile(filePath);
			if (version != null) {
				JdbcClassLoaderFactory.registerClassLoader(filePath);
				serverInfo.setJdbcDriverVersion(version);
			}
		}
		filePath = this.getFilePathInPlugin("/lib/JDBC-8.3.1.0173-cubrid.jar");
		if (filePath != null) {
			String version = JdbcClassLoaderFactory.validateJdbcFile(filePath);
			if (version != null) {
				JdbcClassLoaderFactory.registerClassLoader(filePath);
				serverInfo.setJdbcDriverVersion(version);
			}
		}
		filePath = this.getFilePathInPlugin("/lib/JDBC-8.4.0.0196-cubrid.jar");
		if (filePath != null) {
			String version = JdbcClassLoaderFactory.validateJdbcFile(filePath);
			if (version != null) {
				JdbcClassLoaderFactory.registerClassLoader(filePath);
				serverInfo.setJdbcDriverVersion(version);
			}
		}
	}

	/**
	 * * Setup database 8.3.1
	 *
	 */
	private void setupDatabaseInfo831() {
		/*Setting Cubrid 8.3.1*/
		serverInfo831 = new ServerInfo();
		serverInfo831.setHostAddress(host831);
		serverInfo831.setHostMonPort(monport831);
		serverInfo831.setHostJSPort(jpport831);
		serverInfo831.setUserName(userName831);
		serverInfo831.setUserPassword(passwd831);
		serverInfo831.setJdbcDriverVersion(serverVersion831);
		CMHostNodePersistManager.getInstance().addServer(host831, monport831, userName831,
														serverInfo831);

		MonitoringTask monTask = new MonitoringTask(serverInfo831);
		serverInfo831 = monTask.connectServer(serverVersion831, 1000);
		// get the latest token
		token831 = serverInfo831.getHostToken();
		//get evnInfo
		if (envInfo831 == null) {
			GetEnvInfoTask envTask = new GetEnvInfoTask(serverInfo831);
			envTask.loadEnvInfo();
			envTask.execute();
			envInfo831 = envTask.loadEnvInfo();
			serverInfo831.setEnvInfo(envInfo831);
		}

		assertNotNull(envInfo831);
		serverPath831 = envInfo831.getRootDir();
		dbRootDir831 = envInfo831.getDatabaseDir();
		//get server type
		GetCMConfParameterTask getCMConfParameterTask = new GetCMConfParameterTask(
				serverInfo831);
		getCMConfParameterTask.execute();
		assertEquals(null, getCMConfParameterTask.getErrorMsg());
		Map<String, String> confParameters = getCMConfParameterTask.getConfParameters();
		ServerType serverType = ServerType.BOTH;
		if (confParameters != null) {
			String target = confParameters.get(ConfConstants.CM_TARGET);
			if (target != null) {
				if (target.indexOf("broker") >= 0
						&& target.indexOf("server") >= 0) {
					serverType = ServerType.BOTH;
				} else if (target.indexOf("broker") >= 0) {
					serverType = ServerType.BROKER;
				} else if (target.indexOf("server") >= 0) {
					serverType = ServerType.DATABASE;
				}
			}
		}
		if (serverInfo831 != null) {
			serverInfo831.setServerType(serverType);
		}
		//Get database list task
		if (serverType == ServerType.DATABASE || serverType == ServerType.BOTH) {
			final GetDatabaseListTask getDatabaseListTask = new GetDatabaseListTask(
					serverInfo831);
			getDatabaseListTask.execute();
			assertEquals(null, getDatabaseListTask.getErrorMsg());
			List<DatabaseInfo> databaseInfoList = getDatabaseListTask.loadDatabaseInfo();
			for (DatabaseInfo dbInfo : databaseInfoList) {
				if (dbInfo.getDbName().equals(testDbName831)) {
					databaseInfo831 = dbInfo;
					break;
				}
			}
			if (databaseInfo831 == null) {
				return;
			}
		}

		//get CubridManager user list task
		GetCMUserListTask getUserInfoTask = new GetCMUserListTask(serverInfo831);
		getUserInfoTask.execute();
		assertEquals(null, getUserInfoTask.getErrorMsg());
		List<ServerUserInfo> serverUserInfoList = getUserInfoTask.getServerUserInfoList();
		for (int i = 0; serverUserInfoList != null
				&& i < serverUserInfoList.size(); i++) {
			ServerUserInfo userInfo = serverUserInfoList.get(i);
			if (userInfo != null
					&& userInfo.getUserName().equals(
							serverInfo831.getUserName())) {
				serverInfo831.setLoginedUserInfo(userInfo);
				break;
			}
		}
		List<DatabaseInfo> databaseInfoList = serverInfo831.getLoginedUserInfo().getDatabaseInfoList();
		String dbDir = databaseInfo831.getDbDir();
		DbRunningType type = databaseInfo831.getRunningType();
		for (int i = 0; databaseInfoList != null && i < databaseInfoList.size(); i++) {
			if (testDbName831.equalsIgnoreCase(databaseInfoList.get(i).getDbName())) {
				databaseInfo831 = databaseInfoList.get(i);
				databaseInfo831.setDbDir(dbDir);
				databaseInfo831.setBrokerIP(host831);
				databaseInfo831.setBrokerPort(port831);
				databaseInfo831.setCharSet(charset831);
				databaseInfo831.setRunningType(type);
				break;
			}
		}
		//get CUBRID conf parameter
		GetCubridConfParameterTask getCubridConfParameterTask = new GetCubridConfParameterTask(
				serverInfo831);
		getCubridConfParameterTask.execute();
		assertEquals(null, getCubridConfParameterTask.getErrorMsg());
		Map<String, Map<String, String>> confParas = getCubridConfParameterTask.getConfParameters();
		if (serverInfo831 != null) {
			serverInfo831.setCubridConfParaMap(confParas);
		}

		//get broker Info
		GetBrokerConfParameterTask getBrokerConfParameterTask = new GetBrokerConfParameterTask(
				serverInfo831);
		getBrokerConfParameterTask.execute();
		assertEquals(null, getBrokerConfParameterTask.getErrorMsg());
		confParas = getBrokerConfParameterTask.getConfParameters();
		if (serverInfo831 != null) {
			serverInfo831.setBrokerConfParaMap(confParas);
		}

		//Set JDBC driver
		String filePath = this.getFilePathInPlugin("/lib/JDBC-8.3.0.1004-cubrid.jar");
		if (filePath != null) {
			String version = JdbcClassLoaderFactory.validateJdbcFile(filePath);
			if (version != null) {
				JdbcClassLoaderFactory.registerClassLoader(filePath);
				serverInfo831.setJdbcDriverVersion(version);
			}
		}
		filePath = this.getFilePathInPlugin("/lib/JDBC-8.3.1.0173-cubrid.jar");
		if (filePath != null) {
			String version = JdbcClassLoaderFactory.validateJdbcFile(filePath);
			if (version != null) {
				JdbcClassLoaderFactory.registerClassLoader(filePath);
				serverInfo831.setJdbcDriverVersion(version);
			}
		}
	}

	/**
	 * Setup database 9.3.0
	 *
	 */
	private void setupDatabaseInfo930() throws Exception {
		serverInfo930 = new ServerInfo();
		serverInfo930.setHostAddress(host930);
		serverInfo930.setHostMonPort(monport930);
		serverInfo930.setHostJSPort(jpport930);
		serverInfo930.setUserName(userName930);
		serverInfo930.setUserPassword(passwd930);
		serverInfo930.setJdbcDriverVersion(serverVersion930);
		CMHostNodePersistManager.getInstance().addServer(host930, monport930, userName930,
														serverInfo930);

		MonitoringTask monTask = new MonitoringTask(serverInfo930);
		serverInfo930 = monTask.connectServer(serverVersion930, 1000);

		// get the latest token
		token930 = serverInfo930.getHostToken();
		//get evnInfo
		if (envInfo930 == null) {
			GetEnvInfoTask envTask = new GetEnvInfoTask(serverInfo930);
			envTask.loadEnvInfo();
			envTask.execute();
			envInfo930 = envTask.loadEnvInfo();
			serverInfo930.setEnvInfo(envInfo930);
		}

		assertNotNull(envInfo930);
		serverPath930 = envInfo930.getRootDir();
		dbRootDir930 = envInfo930.getDatabaseDir();
		//get server type
		GetCMConfParameterTask getCMConfParameterTask = new GetCMConfParameterTask(
				serverInfo930);
		getCMConfParameterTask.execute();
		assertEquals(null, getCMConfParameterTask.getErrorMsg());
		Map<String, String> confParameters = getCMConfParameterTask.getConfParameters();
		ServerType serverType = ServerType.BOTH;
		if (confParameters != null) {
			String target = confParameters.get(ConfConstants.CM_TARGET);
			if (target != null) {
				if (target.indexOf("broker") >= 0
						&& target.indexOf("server") >= 0) {
					serverType = ServerType.BOTH;
				} else if (target.indexOf("broker") >= 0) {
					serverType = ServerType.BROKER;
				} else if (target.indexOf("server") >= 0) {
					serverType = ServerType.DATABASE;
				}
			}
		}
		if (serverInfo930 != null) {
			serverInfo930.setServerType(serverType);
		}
		//Get database list task
		if (serverType == ServerType.DATABASE || serverType == ServerType.BOTH) {
			final GetDatabaseListTask getDatabaseListTask = new GetDatabaseListTask(
					serverInfo930);
			getDatabaseListTask.execute();
			assertEquals(null, getDatabaseListTask.getErrorMsg());
			List<DatabaseInfo> databaseInfoList = getDatabaseListTask.loadDatabaseInfo();
			for (DatabaseInfo dbInfo : databaseInfoList) {
				if (dbInfo.getDbName().equals(testDbName930)) {
					databaseInfo930 = dbInfo;
					break;
				}
			}
			if (databaseInfo930 == null) {
				return;
			}
		}
		//get CubridManager user list task
		GetCMUserListTask getUserInfoTask = new GetCMUserListTask(serverInfo930);
		getUserInfoTask.execute();
		assertEquals(null, getUserInfoTask.getErrorMsg());
		List<ServerUserInfo> serverUserInfoList = getUserInfoTask.getServerUserInfoList();
		for (int i = 0; serverUserInfoList != null
				&& i < serverUserInfoList.size(); i++) {
			ServerUserInfo userInfo = serverUserInfoList.get(i);
			if (userInfo != null
					&& userInfo.getUserName().equals(
							serverInfo930.getUserName())) {
				serverInfo930.setLoginedUserInfo(userInfo);
				break;
			}
		}
		List<DatabaseInfo> databaseInfoList = serverInfo930.getLoginedUserInfo().getDatabaseInfoList();
		String dbDir = databaseInfo930.getDbDir();
		DbRunningType type = databaseInfo930.getRunningType();
		for (int i = 0; databaseInfoList != null && i < databaseInfoList.size(); i++) {
			if (testDbName930.equalsIgnoreCase(databaseInfoList.get(i).getDbName())) {
				databaseInfo930 = databaseInfoList.get(i);
				databaseInfo930.setDbDir(dbDir);
				databaseInfo930.setBrokerIP(host930);
				databaseInfo930.setBrokerPort(port930);
				databaseInfo930.setCharSet(charset930);
				databaseInfo930.setRunningType(type);
				break;
			}
		}
		//get CUBRID conf parameter
		GetCubridConfParameterTask getCubridConfParameterTask = new GetCubridConfParameterTask(
				serverInfo930);
		getCubridConfParameterTask.execute();
		assertEquals(null, getCubridConfParameterTask.getErrorMsg());
		Map<String, Map<String, String>> confParas = getCubridConfParameterTask.getConfParameters();
		if (serverInfo930 != null) {
			serverInfo930.setCubridConfParaMap(confParas);
		}

		//get broker Info
		GetBrokerConfParameterTask getBrokerConfParameterTask = new GetBrokerConfParameterTask(
				serverInfo930);
		getBrokerConfParameterTask.execute();
		assertEquals(null, getBrokerConfParameterTask.getErrorMsg());
		confParas = getBrokerConfParameterTask.getConfParameters();
		if (serverInfo930 != null) {
			serverInfo930.setBrokerConfParaMap(confParas);
		}

		//Set JDBC driver
		String filePath = this.getFilePathInPlugin("/lib/JDBC-9.2.0.0155-cubrid.jar");
		if (filePath != null) {
			String version = JdbcClassLoaderFactory.validateJdbcFile(filePath);
			if (version != null) {
				JdbcClassLoaderFactory.registerClassLoader(filePath);
				serverInfo930.setJdbcDriverVersion(version);
			}
		}

	}

	protected void createTestDatabase() {
		CreateDbTask createDbTask = new CreateDbTask(serverInfo);
		createDbTask.setDbName(testDbName);
		createDbTask.setPageSize("4096");
		createDbTask.setNumPage("25600");
		createDbTask.setGeneralVolumePath(dbRootDir
				+ serverInfo.getPathSeparator() + testDbName);
		createDbTask.setLogSize("25600");
		createDbTask.setLogVolumePath(dbRootDir + serverInfo.getPathSeparator()
				+ testDbName);
		Map<String, String> volMap = new HashMap<String, String>();
		volMap.put("0", testDbName + "_data_x001");
		volMap.put("1", "data");
		volMap.put("3", "25600");
		volMap.put("4", dbRootDir + serverInfo.getPathSeparator() + testDbName);
		List<Map<String, String>> volList = new ArrayList<Map<String, String>>();
		volList.add(volMap);
		createDbTask.setExVolumes(volList);
		createDbTask.setOverwriteConfigFile(true);
		createDbTask.execute();
		assertEquals(null, createDbTask.getErrorMsg());
	}

	protected void changeDbaPassword() {
		//set dba password
		UpdateAddUserTask updateUserTask = new UpdateAddUserTask(serverInfo,
				false);
		UserSendObj userSendObj = new UserSendObj();
		userSendObj.setDbname(testDbName);
		userSendObj.setUsername(dbaUserName);
		userSendObj.setUserpass(dbaPassword);
		updateUserTask.setUserSendObj(userSendObj);
		updateUserTask.execute();
		assertEquals(null, updateUserTask.getErrorMsg());
	}

	protected void startTestDatabase() {
		if (databaseInfo.getRunningType() == DbRunningType.STANDALONE) {
			CommonUpdateTask startTask = new CommonUpdateTask(
					CommonTaskName.START_DB_TASK_NAME, serverInfo,
					CommonSendMsg.getCommonDatabaseSendMsg());
			startTask.setDbName(databaseInfo.getDbName());
			startTask.execute();
			assertEquals(null, startTask.getErrorMsg());
			databaseInfo.setRunningType(DbRunningType.CS);
		}
	}

	protected void stopTestDatabase() {
		if (databaseInfo.getRunningType() == DbRunningType.CS) {
			CommonUpdateTask startTask = new CommonUpdateTask(
					CommonTaskName.STOP_DB_TASK_NAME, serverInfo,
					CommonSendMsg.getCommonDatabaseSendMsg());
			startTask.setDbName(databaseInfo.getDbName());
			startTask.execute();
			assertEquals(null, startTask.getErrorMsg());
			databaseInfo.setRunningType(DbRunningType.STANDALONE);
		}
	}

	protected void loginTestDatabase() {
		if (databaseInfo.isLogined()) {
			return;
		}
		LoginDatabaseTask loginDatabaseTask = new LoginDatabaseTask(serverInfo);
		loginDatabaseTask.setCMUser(userName);
		loginDatabaseTask.setDbName(testDbName);
		loginDatabaseTask.setDbUser(dbaUserName);
		loginDatabaseTask.setDbPassword(dbaPassword);
		loginDatabaseTask.execute();
		assertEquals(null, loginDatabaseTask.getErrorMsg());
		DbUserInfo dbUserInfo = loginDatabaseTask.getLoginedDbUserInfo();
		dbUserInfo.setNoEncryptPassword(dbaPassword);
		databaseInfo.setLogined(true);
		databaseInfo.setAuthLoginedDbUserInfo(dbUserInfo);

		UpdateCMUserTask updateCMUserTask = new UpdateCMUserTask(serverInfo);
		updateCMUserTask.setCmUserName(userName);

		ServerUserInfo userInfo = serverInfo.getLoginedUserInfo();
		updateCMUserTask.setCasAuth(userInfo.getCasAuth().getText());
		updateCMUserTask.setDbCreator(userInfo.getDbCreateAuthType().getText());
		updateCMUserTask.setStatusMonitorAuth(userInfo.getStatusMonitorAuth().getText());
		List<String> dbNameList = new ArrayList<String>();
		List<String> dbUserList = new ArrayList<String>();
		List<String> dbPasswordList = new ArrayList<String>();
		List<String> dbBrokerPortList = new ArrayList<String>();
		List<DatabaseInfo> authDatabaseList = userInfo.getDatabaseInfoList();
		if (authDatabaseList != null && !authDatabaseList.isEmpty()) {
			int size = authDatabaseList.size();
			for (int i = 0; i < size; i++) {
				DatabaseInfo databaseInfo = authDatabaseList.get(i);
				dbNameList.add(databaseInfo.getDbName());
				dbUserList.add(databaseInfo.getAuthLoginedDbUserInfo().getName());
				dbBrokerPortList.add(databaseInfo.getBrokerIP() + ","
						+ databaseInfo.getBrokerPort());
				String password = databaseInfo.getAuthLoginedDbUserInfo().getNoEncryptPassword();
				dbPasswordList.add(password == null ? "" : password);
			}
		}
		String[] dbNameArr = new String[dbNameList.size()];
		String[] dbUserArr = new String[dbUserList.size()];
		String[] dbPasswordArr = new String[dbPasswordList.size()];
		String[] dbBrokerPortArr = new String[dbBrokerPortList.size()];
		updateCMUserTask.setDbAuth(dbNameList.toArray(dbNameArr),
				dbUserList.toArray(dbUserArr),
				dbPasswordList.toArray(dbPasswordArr),
				dbBrokerPortList.toArray(dbBrokerPortArr));

		updateCMUserTask.execute();
		assertEquals(null, updateCMUserTask.getErrorMsg());
	}

	protected void loginTestDatabase831() {
		if (databaseInfo831.isLogined()) {
			return;
		}
		LoginDatabaseTask loginDatabaseTask = new LoginDatabaseTask(
				serverInfo831);
		loginDatabaseTask.setCMUser(userName831);
		loginDatabaseTask.setDbName(testDbName831);
		loginDatabaseTask.setDbUser(dbaUserName831);
		loginDatabaseTask.setDbPassword(dbaPassword831);
		loginDatabaseTask.execute();
		assertEquals(null, loginDatabaseTask.getErrorMsg());
		DbUserInfo dbUserInfo = loginDatabaseTask.getLoginedDbUserInfo();
		dbUserInfo.setNoEncryptPassword(dbaPassword831);
		databaseInfo831.setLogined(true);
		databaseInfo831.setAuthLoginedDbUserInfo(dbUserInfo);

		UpdateCMUserTask updateCMUserTask = new UpdateCMUserTask(serverInfo831);
		updateCMUserTask.setCmUserName(userName831);

		ServerUserInfo userInfo = serverInfo831.getLoginedUserInfo();
		updateCMUserTask.setCasAuth(userInfo.getCasAuth().getText());
		updateCMUserTask.setDbCreator(userInfo.getDbCreateAuthType().getText());
		updateCMUserTask.setStatusMonitorAuth(userInfo.getStatusMonitorAuth().getText());
		List<String> dbNameList = new ArrayList<String>();
		List<String> dbUserList = new ArrayList<String>();
		List<String> dbPasswordList = new ArrayList<String>();
		List<String> dbBrokerPortList = new ArrayList<String>();
		List<DatabaseInfo> authDatabaseList = userInfo.getDatabaseInfoList();
		if (authDatabaseList != null && !authDatabaseList.isEmpty()) {
			int size = authDatabaseList.size();
			for (int i = 0; i < size; i++) {
				DatabaseInfo databaseInfo = authDatabaseList.get(i);
				dbNameList.add(databaseInfo.getDbName());
				dbUserList.add(databaseInfo.getAuthLoginedDbUserInfo().getName());
				dbBrokerPortList.add(databaseInfo.getBrokerIP() + ","
						+ databaseInfo.getBrokerPort());
				String password = databaseInfo.getAuthLoginedDbUserInfo().getNoEncryptPassword();
				dbPasswordList.add(password == null ? "" : password);
			}
		}
		String[] dbNameArr = new String[dbNameList.size()];
		String[] dbUserArr = new String[dbUserList.size()];
		String[] dbPasswordArr = new String[dbPasswordList.size()];
		String[] dbBrokerPortArr = new String[dbBrokerPortList.size()];
		updateCMUserTask.setDbAuth(dbNameList.toArray(dbNameArr),
				dbUserList.toArray(dbUserArr),
				dbPasswordList.toArray(dbPasswordArr),
				dbBrokerPortList.toArray(dbBrokerPortArr));

		updateCMUserTask.execute();
		assertEquals(null, updateCMUserTask.getErrorMsg());
	}
	
	protected void loginTestDatabase930() {
		if (databaseInfo930.isLogined()) {
			return;
		}
		LoginDatabaseTask loginDatabaseTask = new LoginDatabaseTask(serverInfo930);
		loginDatabaseTask.setCMUser(userName930);
		loginDatabaseTask.setDbName(testDbName930);
		loginDatabaseTask.setDbUser(dbaUserName930);
		loginDatabaseTask.setDbPassword(dbaPassword930);
		loginDatabaseTask.execute();
		assertEquals(null, loginDatabaseTask.getErrorMsg());
		DbUserInfo dbUserInfo = loginDatabaseTask.getLoginedDbUserInfo();
		dbUserInfo.setNoEncryptPassword(dbaPassword930);
		databaseInfo930.setLogined(true);
		databaseInfo930.setAuthLoginedDbUserInfo(dbUserInfo);

		UpdateCMUserTask updateCMUserTask = new UpdateCMUserTask(serverInfo930);
		updateCMUserTask.setCmUserName(userName930);

		ServerUserInfo userInfo = serverInfo930.getLoginedUserInfo();
		updateCMUserTask.setCasAuth(userInfo.getCasAuth().getText());
		updateCMUserTask.setDbCreator(userInfo.getDbCreateAuthType().getText());
		updateCMUserTask.setStatusMonitorAuth(userInfo.getStatusMonitorAuth().getText());
		List<String> dbNameList = new ArrayList<String>();
		List<String> dbUserList = new ArrayList<String>();
		List<String> dbPasswordList = new ArrayList<String>();
		List<String> dbBrokerPortList = new ArrayList<String>();
		List<DatabaseInfo> authDatabaseList = userInfo.getDatabaseInfoList();
		if (authDatabaseList != null && !authDatabaseList.isEmpty()) {
			int size = authDatabaseList.size();
			for (int i = 0; i < size; i++) {
				DatabaseInfo databaseInfo = authDatabaseList.get(i);
				dbNameList.add(databaseInfo.getDbName());
				dbUserList.add(databaseInfo.getAuthLoginedDbUserInfo().getName());
				dbBrokerPortList.add(databaseInfo.getBrokerIP() + ","
						+ databaseInfo.getBrokerPort());
				String password = databaseInfo.getAuthLoginedDbUserInfo().getNoEncryptPassword();
				dbPasswordList.add(password == null ? "" : password);
			}
		}
		String[] dbNameArr = new String[dbNameList.size()];
		String[] dbUserArr = new String[dbUserList.size()];
		String[] dbPasswordArr = new String[dbPasswordList.size()];
		String[] dbBrokerPortArr = new String[dbBrokerPortList.size()];
		updateCMUserTask.setDbAuth(dbNameList.toArray(dbNameArr),
				dbUserList.toArray(dbUserArr),
				dbPasswordList.toArray(dbPasswordArr),
				dbBrokerPortList.toArray(dbBrokerPortArr));

		updateCMUserTask.execute();
		assertEquals(null, updateCMUserTask.getErrorMsg());
	}

	/**
	 * This method is to find the file's path in a fragment or a plugin.
	 *
	 * @param filepath the file path in the fragment or a plugin
	 * @return the absolute file path
	 */
	public String getFilePathInPlugin(String filepath) {
		URL fileUrl = null;
		if (CubridManagerCorePlugin.getDefault() == null) {
			fileUrl = this.getClass().getResource(filepath);
		} else {
			Bundle bundle = CubridManagerCorePlugin.getDefault().getBundle();
			URL url = bundle.getResource(filepath);
			try {
				fileUrl = FileLocator.toFileURL(url);
			} catch (IOException e) {
				return null;
			}
		}
		return fileUrl == null ? null : fileUrl.getPath();
	}

	/**
	 * Get server separator
	 *
	 * @return
	 */
	public String getPathSeparator() {
		return serverInfo.getPathSeparator();
	}

	public boolean addBackupPlan(String backupPlanId) {
		BackupPlanTask task = new BackupPlanTask(
				BackupPlanTask.ADD_BACKUP_INFO, serverInfo);
		task.setDbname(testDbName);
		task.setBackupid(backupPlanId);
		task.setPath(databaseInfo.getDbDir() + serverInfo.getPathSeparator()
				+ "backup");
		task.setPeriodType("Weekly");
		task.setPeriodDate("Tuesday");
		task.setTime("0906");
		task.setLevel("2");
		task.setArchivedel(OnOffType.ON);
		task.setUpdatestatus(OnOffType.ON);
		task.setStoreold(OnOffType.ON);
		task.setOnoff(OnOffType.ON);
		task.setZip(YesNoType.Y);
		task.setCheck(YesNoType.Y);
		task.setMt("5");
		task.setBknum("9");
		task.execute();
		return task.isSuccess();
	}

	public boolean deleteBackupPlan(String backupPlanId) {
		DelBackupPlanTask task = new DelBackupPlanTask(serverInfo);
		task.setDbname(testDbName);
		task.setBackupid(backupPlanId);
		task.execute();
		return task.isSuccess();
	}
}
