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
package com.cubrid.cubridmanager.ui.replication.control;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Shell;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.model.CasAuthType;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.common.model.OnOffType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.common.model.StatusMonitorAuthType;
import com.cubrid.cubridmanager.core.common.task.GetCMUserListTask;
import com.cubrid.cubridmanager.core.common.task.MonitoringTask;
import com.cubrid.cubridmanager.core.common.task.UpdateCMUserTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DbCreateAuthType;
import com.cubrid.cubridmanager.core.cubrid.database.task.GetDatabaseListTask;
import com.cubrid.cubridmanager.core.cubrid.database.task.LoginDatabaseTask;
import com.cubrid.cubridmanager.core.cubrid.table.model.DBClasses;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetClassListTask;
import com.cubrid.cubridmanager.ui.mondashboard.preference.MonitorDashboardPreference;
import com.cubrid.cubridmanager.ui.spi.Version;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;

/**
 * 
 * This task executor is responsible to get all classes form master database,it
 * will be used in the below scene (1)select master database tables in change
 * slave database (2)set master database information in create replication
 * 
 * @author pangqiren
 * @version 1.0 - 2009-12-4 created by pangqiren
 */
public class GetAllClassesTaskExecutor extends
		TaskExecutor {

	private final String ip;
	private final String port;
	private final String userName;
	private final String password;
	private final String dbName;
	private final String dbaPassword;
	private DBClasses dbClasses = null;;
	private final Shell shell;

	public GetAllClassesTaskExecutor(Shell shell, String ip, String port,
			String userName, String password, String dbName, String dbaPassword) {
		this.shell = shell;
		this.ip = ip;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.dbName = dbName;
		this.dbaPassword = dbaPassword;
		init();
	}

	/**
	 * initialize some values
	 */
	private void init() {
		ServerInfo serverInfo = new ServerInfo();
		boolean isConnected = ServerManager.getInstance().isConnected(ip,
				Integer.parseInt(port), userName);
		DatabaseInfo dbInfo = null;
		OnOffType status = OnOffType.OFF;
		if (isConnected) {
			serverInfo = CMHostNodePersistManager.getInstance().getServerInfo(ip,
					Integer.parseInt(port), userName);
			dbInfo = serverInfo.getLoginedUserInfo().getDatabaseInfo(dbName);
			if (dbInfo.getRunningType() == DbRunningType.CS) {
				status = OnOffType.ON;
			}
		} else {
			serverInfo.setHostAddress(ip);
			serverInfo.setHostMonPort(Integer.parseInt(port));
			serverInfo.setHostJSPort(Integer.parseInt(port) + 1);
			serverInfo.setUserName(userName);
			serverInfo.setUserPassword(password);

			CMHostNodePersistManager.getInstance().addServer(ip, Integer.parseInt(port),
					userName, serverInfo);
			MonitoringTask monitoringTask = new MonitoringTask(serverInfo);
			addTask(monitoringTask);

			GetDatabaseListTask getDatabaseListTask = new GetDatabaseListTask(
					serverInfo);
			addTask(getDatabaseListTask);

			GetCMUserListTask getUserInfoTask = new GetCMUserListTask(
					serverInfo);
			addTask(getUserInfoTask);
		}

		if (dbInfo == null || (dbInfo != null && !dbInfo.isLogined())) {
			LoginDatabaseTask loginDbTask = new LoginDatabaseTask(serverInfo);
			loginDbTask.setCMUser("admin");
			loginDbTask.setDbName(dbName);
			loginDbTask.setDbUser("dba");
			loginDbTask.setDbPassword(dbaPassword);
			addTask(loginDbTask);

			UpdateCMUserTask updateCMUserTask = new UpdateCMUserTask(serverInfo);
			updateCMUserTask.setCmUserName("admin");
			updateCMUserTask.setCasAuth(CasAuthType.AUTH_ADMIN.getText());
			updateCMUserTask.setDbCreator(DbCreateAuthType.AUTH_ADMIN.getText());
			updateCMUserTask.setStatusMonitorAuth(StatusMonitorAuthType.AUTH_ADMIN.getText());
			addTask(updateCMUserTask);
		}

		GetClassListTask getClassListTask = new GetClassListTask(serverInfo);
		getClassListTask.setDbName(dbName);
		getClassListTask.setDbStatus(status);
		addTask(getClassListTask);
	}

	/**
	 * Set connected status of server to false
	 * 
	 */
	private void disConnect() {
		ServerManager.getInstance().setConnected(ip, Integer.parseInt(port),
				userName, false);
	}

	/**
	 * @see com.cubrid.common.ui.spi.progress.TaskExecutor#exec(org.eclipse.core.runtime.IProgressMonitor)
	 * @param monitor IProgressMonitor
	 * @return boolean
	 */
	public boolean exec(final IProgressMonitor monitor) {
		if (monitor.isCanceled()) {
			return false;
		}
		boolean isConnected = false;
		OnOffType status = OnOffType.OFF;
		
		MonitorDashboardPreference monPref = new MonitorDashboardPreference();
		
		for (ITask task : taskList) {
			if (task instanceof MonitoringTask) {
				MonitoringTask monitoringTask = (MonitoringTask) task;
				monitoringTask.connectServer(Version.releaseVersion,
						monPref.getHAHeartBeatTimeout());
				isConnected = true;
			} else if (task instanceof UpdateCMUserTask) {
				UpdateCMUserTask updateCMUserTask = (UpdateCMUserTask) task;
				ServerInfo serverInfo = updateCMUserTask.getServerInfo();
				if (serverInfo != null && serverInfo.isConnected()) {
					List<String> dbNameList = new ArrayList<String>();
					List<String> dbUserList = new ArrayList<String>();
					List<String> dbPasswordList = new ArrayList<String>();
					List<String> dbBrokerPortList = new ArrayList<String>();
					ServerUserInfo userInfo = serverInfo.getLoginedUserInfo();
					if (userInfo != null) {
						List<DatabaseInfo> authDatabaseList = userInfo.getDatabaseInfoList();
						if (authDatabaseList != null
								&& !authDatabaseList.isEmpty()) {
							int size = authDatabaseList.size();
							for (int i = 0; i < size; i++) {
								DatabaseInfo databaseInfo = authDatabaseList.get(i);
								dbNameList.add(databaseInfo.getDbName());
								dbBrokerPortList.add(QueryOptions.getBrokerIp(databaseInfo)
										+ "," + databaseInfo.getBrokerPort());
								String password = databaseInfo.getAuthLoginedDbUserInfo().getNoEncryptPassword();
								if (databaseInfo.getDbName().equalsIgnoreCase(
										dbName)) {
									password = dbaPassword;
									dbUserList.add("dba");
								} else {
									dbUserList.add(databaseInfo.getAuthLoginedDbUserInfo().getName());
								}
								dbPasswordList.add(password == null ? ""
										: password);
							}
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
				}
			} else if (task instanceof GetClassListTask) {
				GetClassListTask getClassListTask = (GetClassListTask) task;
				if (status != OnOffType.OFF) {
					getClassListTask.setDbStatus(status);
				}
				getClassListTask.execute();
				dbClasses = getClassListTask.getDbClassInfo();
			} else {
				task.execute();
			}
			final String msg = task.getErrorMsg();
			if (monitor.isCanceled()) {
				if (isConnected) {
					disConnect();
				}
				return false;
			}
			if (openErrorBox(shell, msg, monitor)) {
				if (isConnected) {
					disConnect();
				}
				return false;
			}
			if (task instanceof GetDatabaseListTask) {
				GetDatabaseListTask getDatabaseListTask = (GetDatabaseListTask) task;
				List<DatabaseInfo> databaseInfoList = getDatabaseListTask.loadDatabaseInfo();
				for (int i = 0; databaseInfoList != null
						&& i < databaseInfoList.size(); i++) {
					DatabaseInfo dbInfo = databaseInfoList.get(i);
					if (dbName.equalsIgnoreCase(dbInfo.getDbName())) {
						DbRunningType type = dbInfo.getRunningType();
						if (type == DbRunningType.CS) {
							status = OnOffType.ON;
						}
					}
				}
			} else if (task instanceof GetCMUserListTask) {
				ServerInfo serverInfo = ((GetCMUserListTask) task).getServerInfo();
				if (serverInfo != null && serverInfo.isConnected()) {
					GetCMUserListTask getUserInfoTask = (GetCMUserListTask) task;
					List<ServerUserInfo> serverUserInfoList = getUserInfoTask.getServerUserInfoList();
					for (int i = 0; serverUserInfoList != null
							&& i < serverUserInfoList.size(); i++) {
						ServerUserInfo userInfo = serverUserInfoList.get(i);
						if (userInfo != null
								&& userInfo.getUserName().equals(
										serverInfo.getUserName())) {
							serverInfo.setLoginedUserInfo(userInfo);
							break;
						}
					}
				}
			}
		}
		if (isConnected) {
			disConnect();
		}
		return true;
	}

	public DBClasses getDBClasses() {
		return this.dbClasses;
	}
}
