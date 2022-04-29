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
package com.cubrid.cubridmanager.ui.host.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfos;
import com.cubrid.cubridmanager.core.broker.task.GetBrokerConfParameterTask;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.model.ConfConstants;
import com.cubrid.cubridmanager.core.common.model.EnvInfo;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.model.ServerType;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.common.task.CommonQueryTask;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.common.task.GetCMConfParameterTask;
import com.cubrid.cubridmanager.core.common.task.GetCMUserListTask;
import com.cubrid.cubridmanager.core.common.task.GetCubridConfParameterTask;
import com.cubrid.cubridmanager.core.common.task.GetEnvInfoTask;
import com.cubrid.cubridmanager.core.common.task.MonitoringTask;
import com.cubrid.cubridmanager.core.common.task.UpdateCMUserTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.database.task.GetDatabaseListTask;
import com.cubrid.cubridmanager.ui.host.Messages;
import com.cubrid.cubridmanager.ui.mondashboard.preference.MonitorDashboardPreference;
import com.cubrid.cubridmanager.ui.spi.Version;
import com.cubrid.cubridmanager.ui.spi.model.loader.CubridDatabasesFolderLoader;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;
import com.cubrid.jdbc.proxy.manage.ServerJdbcVersionMapping;

/**
 * Connect host executor
 *
 * @author pangqiren
 * @version 1.0 - 2010-1-6 created by pangqiren
 */
public class ConnectHostExecutor extends TaskExecutor {
	private static final Logger LOGGER = LogUtil.getLogger(ConnectHostExecutor.class);
	boolean isContinue = false;
	private final List<DatabaseInfo> authDatabaseList = new ArrayList<DatabaseInfo>();
	private final List<DatabaseInfo> allDatabaseInfoList = new ArrayList<DatabaseInfo>();
	private final Display display;
	private final Shell shell;
	private ServerInfo serverInfo;
	private final boolean showErrMsg;
	private String errMsg;
	private boolean isTest = false;
	private boolean isCheckJdbc = true;
	private boolean willExitRunLoop = false;
	private boolean successRunLoop = false;
	private Thread workThread;

	public ConnectHostExecutor(Shell shell, ServerInfo serInfo, boolean showErrMsg) {
		this(shell, serInfo, showErrMsg, false);
	}

	public ConnectHostExecutor(Shell shell, ServerInfo serInfo, boolean showErrMsg, boolean isTest) {
		this.serverInfo = serInfo;
		this.shell = shell;
		this.display = shell.getDisplay();
		this.showErrMsg = showErrMsg;
		this.isTest = isTest;
		initTasks();
	}

	private void initTasks() {
		MonitoringTask monitoringTask = serverInfo.getMonitoringTask();
		addTask(monitoringTask);

		GetEnvInfoTask getEnvInfoTask = new GetEnvInfoTask(serverInfo);
		addTask(getEnvInfoTask);

		GetDatabaseListTask getDatabaseListTask = new GetDatabaseListTask(serverInfo);
		addTask(getDatabaseListTask);

		GetCMConfParameterTask getCMConfParameterTask = new GetCMConfParameterTask(serverInfo);
		addTask(getCMConfParameterTask);

		CommonQueryTask<BrokerInfos> getBrokerTask = new CommonQueryTask<BrokerInfos>(
				serverInfo, CommonSendMsg.getCommonSimpleSendMsg(),
				new BrokerInfos());
		addTask(getBrokerTask);

		GetCMUserListTask getUserInfoTask = new GetCMUserListTask(serverInfo);
		addTask(getUserInfoTask);

		UpdateCMUserTask updateTask = new UpdateCMUserTask(serverInfo);
		updateTask.setCmUserName(serverInfo.getUserName());
		addTask(updateTask);

		GetCubridConfParameterTask getCubridConfParameterTask = new GetCubridConfParameterTask(serverInfo);
		addTask(getCubridConfParameterTask);

		GetBrokerConfParameterTask getBrokerConfParameterTask = new GetBrokerConfParameterTask(serverInfo);
		addTask(getBrokerConfParameterTask);
	}

	/**
	 * Cancel to connect host
	 */
	public void cancel() {
		super.cancel();
		if (serverInfo != null) {
			ServerManager.getInstance().setConnected(
					serverInfo.getHostAddress(), serverInfo.getHostMonPort(),
					serverInfo.getUserName(), false);
		}
		exitRunLoop();
	}

	/**
	 * Disconnect host
	 */
	private void disConnect() {
		if (serverInfo != null) {
			ServerManager.getInstance().setConnected(
					serverInfo.getHostAddress(), serverInfo.getHostMonPort(),
					serverInfo.getUserName(), false);
		}
	}

	/**
	 * Change admin user password
	 */
	private void changePassword() {
		if (serverInfo.isConnected() && serverInfo.getUserName() != null
				&& serverInfo.getUserName().equals("admin")
				&& serverInfo.getUserPassword().equals("admin")
				&& !isTest) {
			display.syncExec(new Runnable() {
				public void run() {
					ChangePasswordDialog dialog = new ChangePasswordDialog(null, true);
					dialog.setServerInfo(serverInfo);
					if (dialog.open() != IDialogConstants.OK_ID) {
						isContinue = false;
					}
				}
			});
		}
	}

	/**
	 * Construct the CM user authorization information
	 *
	 * @param task the UpdateCMUserTask object
	 * @param isRunUpdateCmUserTask whether update CM user task
	 */
	private void updateCMUserAuthInfo(ITask task, boolean isRunUpdateCmUserTask) {
		if (isRunUpdateCmUserTask && serverInfo != null && serverInfo.isConnected()) {
			UpdateCMUserTask updateCMUserTask = (UpdateCMUserTask) task;
			ServerUserInfo userInfo = serverInfo.getLoginedUserInfo();
			updateCMUserTask.setCasAuth(userInfo.getCasAuth().getText());
			updateCMUserTask.setDbCreator(userInfo.getDbCreateAuthType().getText());
			updateCMUserTask.setStatusMonitorAuth(userInfo.getStatusMonitorAuth().getText());

			List<String> dbNameList = new ArrayList<String>();
			List<String> dbUserList = new ArrayList<String>();
			List<String> dbPasswordList = new ArrayList<String>();
			List<String> dbBrokerPortList = new ArrayList<String>();
			if (authDatabaseList != null && !authDatabaseList.isEmpty()) {
				int size = authDatabaseList.size();
				for (int i = 0; i < size; i++) {
					DatabaseInfo databaseInfo = authDatabaseList.get(i);
					dbNameList.add(databaseInfo.getDbName());
					dbUserList.add(databaseInfo.getAuthLoginedDbUserInfo().getName());
					dbBrokerPortList.add(QueryOptions.getBrokerIp(databaseInfo) + "," + databaseInfo.getBrokerPort());
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
		}
	}

	/**
	 * Execute to connect host
	 *
	 * @param monitor the IProgressMonitor
	 * @return <code>true</code> if successful;<code>false</code>otherwise;
	 */
	public boolean exec(final IProgressMonitor monitor) {
		if (monitor.isCanceled()) {
			return false;
		}

		willExitRunLoop = false;
		successRunLoop = false;
		workThread = new Thread(new Runnable() {
			public void run() {
				successRunLoop = runLoop(monitor);
				willExitRunLoop = true;
			}
		});
		workThread.start();

		while (!willExitRunLoop) {
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				break;
			}
		}

		return successRunLoop;
	}

	@SuppressWarnings("deprecation")
	private void exitRunLoop() {
		willExitRunLoop = true;
		if (workThread != null) {
			workThread.interrupt();
			workThread.stop(new Exception("The user canceled."));
		}
	}

	private boolean runLoop(final IProgressMonitor monitor) {
		MonitorDashboardPreference monPref = new MonitorDashboardPreference();
		isContinue = true;
		boolean isRunUpdateCmUserTask = false;
		BrokerInfos brokerInfos = null;
		/*To avoid invalid thread access exception*/
		beginTask(monitor);

		for (ITask task : taskList) {
			if (task instanceof MonitoringTask) {
				if (!serverInfo.isConnected()) {
					addServerInHashMap(serverInfo);
					MonitoringTask monitoringTask = (MonitoringTask) task;
					serverInfo = monitoringTask.connectServer(
							Version.releaseVersion,
							monPref.getHAHeartBeatTimeout());
					if (serverInfo.isConnected()) {
						addServerInHashMap(serverInfo);
					} else {
						removeServerFromHashMap(serverInfo);
					}
				}
				changePassword();
			} else if ((task instanceof UpdateCMUserTask)) {
				updateCMUserAuthInfo(task, isRunUpdateCmUserTask);
			} else {
				task.execute();
			}

			final String msg = task.getErrorMsg();
			if (monitor.isCanceled()) {
				disConnect();
				return false;
			}

			if (msg != null) {
				boolean emptyMsg = StringUtil.isEmpty(msg);

				// for TOOLS-2142 logging
				try {
					if (task instanceof MonitoringTask) {
						String request = ((MonitoringTask)task).getRequest();
						char[] requestCharArray = request.toCharArray();

						int sp = request.indexOf("id:");
						if (sp != -1) {
							sp += "id:".length();
							for (int i = sp; i < requestCharArray.length; i++) {
								if (requestCharArray[i] == '\n') {
									break;
								}
								requestCharArray[i] = '*';
							}
						}

						sp = request.indexOf("password:");
						if (sp != -1) {
							sp += "password:".length();
							for (int i = sp; i < requestCharArray.length; i++) {
								if (requestCharArray[i] == '\n') {
									break;
								}
								requestCharArray[i] = '*';
							}
						}

						LOGGER.debug("CMS request : \n" + new String(requestCharArray));
						LOGGER.debug("CMS error : \n" + task.getErrorMsg());
						LOGGER.debug("CMS warn : \n" + task.getWarningMsg());
					}
				} catch (Exception e) {
					LOGGER.error("", e);
				}

				String lowerMsg = msg.toLowerCase();
				// Connection refused: connect
				if (lowerMsg.indexOf("connection refused: connect") != -1) {
					String localizedMsg = Messages.errConnectionRefused;
					if (!prepareMessage(monitor, localizedMsg)) {
						return false;
					}
				}

				//the socket "Connection reset" occasional error is unnecessary to display, and it will affect user experience.
				else if (lowerMsg.indexOf("connection reset") != -1) {
					String localizedMsg = Messages.errConnectionReset;
					if (!prepareMessage(monitor, localizedMsg)) {
						return false;
					}
				}

				// connect timed out
				else if (lowerMsg.indexOf("connect timed out") != -1) {
					String localizedMsg = Messages.errConnectTimedOut;
					if (!prepareMessage(monitor, localizedMsg)) {
						return false;
					}
				}

				// user not found
				else if (lowerMsg.indexOf("user not found") != -1) {
					String localizedMsg = Messages.errUserNotFound;
					if (!prepareMessage(monitor, localizedMsg)) {
						return false;
					}
				}

				// Incorrect password
				else if (lowerMsg.indexOf("incorrect password") != -1) {
					String localizedMsg = Messages.errUserPasswordConnect;
					if (!prepareMessage(monitor, localizedMsg)) {
						return false;
					}
				}

				// No route to host connect
				else if (lowerMsg.indexOf("no route to host connect") != -1) {
					String localizedMsg = Messages.errConnectAddress;
					if (!prepareMessage(monitor, localizedMsg)) {
						return false;
					}
				}

				// There is no error message but there have some error about brokers.
				else if (emptyMsg && StringUtil.isEqual(task.getTaskname(), BrokerInfos.TASK_NAME)) {
					String localizedMsg = Messages.errConnectionByBrokerConfig;
					if (!prepareMessage(monitor, localizedMsg)) {
						return false;
					}
				}

				else {
					String localizedMsg = Messages.bind(Messages.errConnectionFailed, msg);
					if (!prepareMessage(monitor, localizedMsg)) {
						return false;
					}
				}
			}

			if (task instanceof GetEnvInfoTask) {
				GetEnvInfoTask getEnvInfoTask = (GetEnvInfoTask) task;
				EnvInfo envInfo = getEnvInfoTask.loadEnvInfo();
				serverInfo.setEnvInfo(envInfo);
				String clientVersion = getClientVerion();
				if (!isClientSupport(clientVersion)) {
					openWarningBox(shell, Messages.bind(Messages.errNoSupportServerVersion, clientVersion), monitor);
				}

				//for multi host monitor statistic and monitor dashboard
				if(isCheckJdbc){
					String jdbcVersion = serverInfo.getJdbcDriverVersion();
					if (serverInfo.validateJdbcVersion(jdbcVersion)) {
						if (ServerJdbcVersionMapping.JDBC_SELF_ADAPTING_VERSION.equals(jdbcVersion)) {
							serverInfo.setJdbcDriverVersion(ServerInfo.getAutoDetectJdbcVersion(serverInfo.getFullServerVersionKey()));
						}
					} else {
						if (ServerJdbcVersionMapping.JDBC_SELF_ADAPTING_VERSION.equals(jdbcVersion)) {
							String latestVersion = serverInfo.getLatestJdbcVersion();
							if (latestVersion == null) {
								disConnect();
								return false;
							}
							serverInfo.setJdbcDriverVersion(latestVersion);
							openWarningBox(shell, Messages.warnNoSupportLatestDriver 
									+ "\n( Server : " + serverInfo.getFullServerVersionKey()
									+ ", JDBC Driver : " + latestVersion + " )" , monitor);
						} else {
							openWarningBox(shell, Messages.warnRecommendDriver 
									+ "\n( Server : " + serverInfo.getFullServerVersionKey() 
									+ ", JDBC Driver : " + jdbcVersion + " )", monitor);	
						}
					}
				}
			} else if (task instanceof GetDatabaseListTask) {
				GetDatabaseListTask getDatabaseListTask = (GetDatabaseListTask) task;
				List<DatabaseInfo> databaseInfoList = getDatabaseListTask.loadDatabaseInfo();
				if (databaseInfoList != null) {
					allDatabaseInfoList.addAll(databaseInfoList);
				}
			} else if (task instanceof GetCMConfParameterTask) {
				GetCMConfParameterTask getCMConfParameterTask = (GetCMConfParameterTask) task;
				Map<String, String> confParameters = getCMConfParameterTask.getConfParameters();
				ServerType serverType = ServerType.BOTH;
				if (confParameters != null) {
					String target = confParameters.get(ConfConstants.CM_TARGET);
					if (target != null) {
						if (target.indexOf("broker") >= 0 && target.indexOf("server") >= 0) {
							serverType = ServerType.BOTH;
						} else if (target.indexOf("broker") >= 0) {
							serverType = ServerType.BROKER;
						} else if (target.indexOf("server") >= 0) {
							serverType = ServerType.DATABASE;
						}
					}
					String supportMonStatistic = confParameters.get(ConfConstants.SUPPORT_MON_STATISTIC);
					if ("yes".equalsIgnoreCase(supportMonStatistic)) {
						serverInfo.setSupportMonitorStatistic(true);
					} else {
						serverInfo.setSupportMonitorStatistic(false);
					}
				}
				if (serverInfo != null) {
					serverInfo.setServerType(serverType);
				}
			} else if (task instanceof CommonQueryTask) {
				@SuppressWarnings("unchecked")
				CommonQueryTask<BrokerInfos> getBrokerTask = (CommonQueryTask<BrokerInfos>) task;
				brokerInfos = getBrokerTask.getResultModel();
				if (serverInfo != null) {
					serverInfo.setBrokerInfos(brokerInfos);
				}
			} else if (task instanceof GetCMUserListTask) {
				if (serverInfo != null && serverInfo.isConnected()) {
					GetCMUserListTask getUserInfoTask = (GetCMUserListTask) task;
					List<ServerUserInfo> serverUserInfoList = getUserInfoTask.getServerUserInfoList();
					for (int i = 0; serverUserInfoList != null && i < serverUserInfoList.size(); i++) {
						ServerUserInfo userInfo = serverUserInfoList.get(i);
						if (userInfo != null && userInfo.getUserName().equals(serverInfo.getUserName())) {
							serverInfo.setLoginedUserInfo(userInfo);
							break;
						}
					}
					List<DatabaseInfo> databaseInfoList = serverInfo.getLoginedUserInfo().getDatabaseInfoList();
					if (databaseInfoList != null) {
						authDatabaseList.addAll(databaseInfoList);
					}
					isRunUpdateCmUserTask = CubridDatabasesFolderLoader.filterDatabaseList(
							serverInfo, allDatabaseInfoList, authDatabaseList);
					if (isRunUpdateCmUserTask) {
						serverInfo.getLoginedUserInfo().setDatabaseInfoList(authDatabaseList);
					}
				}
			} else if (task instanceof GetCubridConfParameterTask) {
				GetCubridConfParameterTask getCubridConfParameterTask = (GetCubridConfParameterTask) task;
				Map<String, Map<String, String>> confParas = getCubridConfParameterTask.getConfParameters();
				if (serverInfo != null) {
					serverInfo.setCubridConfParaMap(confParas);
				}
			} else if (task instanceof GetBrokerConfParameterTask) {
				GetBrokerConfParameterTask getBrokerConfParameterTask = (GetBrokerConfParameterTask) task;
				Map<String, Map<String, String>> confParas = getBrokerConfParameterTask.getConfParameters();
				if (serverInfo != null) {
					serverInfo.setBrokerConfParaMap(confParas);
				}
			}
			if (monitor.isCanceled() || !isContinue) {
				disConnect();
				return false;
			}
		}

		return true;
	}

	private void beginTask(final IProgressMonitor monitor) {
		final String taskName = Messages.bind(Messages.connHostTaskName, serverInfo.getServerName());
		display.syncExec(new Runnable() {
			public void run() {
				monitor.beginTask(taskName, IProgressMonitor.UNKNOWN);
			}
		});
	}

	private boolean prepareMessage(final IProgressMonitor monitor, String localizedMsg) {
		if (showErrMsg) {
			if (openErrorBox(shell, localizedMsg, monitor)) {
				disConnect();
				return false;
			}
		} else {
			setErrMsg(localizedMsg);
			disConnect();
			return false;
		}

		return true;
	}

	private void removeServer(ServerInfo serverInfo) {
		CMHostNodePersistManager.getInstance().removeServer(
				serverInfo.getHostAddress(),
				serverInfo.getHostMonPort(),
				serverInfo.getUserName());
	}
	
	private void removeServerFromHashMap(ServerInfo serverInfo) {
		CMHostNodePersistManager.getInstance().removeServerFromHashMap(
				serverInfo.getHostAddress(),
				serverInfo.getHostMonPort(),
				serverInfo.getUserName());
	}
	
	private void addServerInHashMap(ServerInfo serverInfo){
		CMHostNodePersistManager.getInstance().addServerInHashMap(
				serverInfo.getHostAddress(),
				serverInfo.getHostMonPort(),
				serverInfo.getUserName(),
				serverInfo);
	}

	private void addServer(ServerInfo serverInfo){
		CMHostNodePersistManager.getInstance().addServer(
				serverInfo.getHostAddress(),
				serverInfo.getHostMonPort(),
				serverInfo.getUserName(),
				serverInfo);
	}
	
	/**
	 * Retrieves the version of client
	 *
	 * @return version
	 */
	private String getClientVerion() {
		return Version.buildVersionId.substring(0, Version.buildVersionId.lastIndexOf("."));
	}

	/**
	 * The version of server and version of client should be matched.
	 *
	 * @param clientVersion String
	 * @return true or false
	 */
	private boolean isClientSupport(String clientVersion) {
		return CompatibleUtil.isSupportCMServer(serverInfo,
				clientVersion);
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public void setCheckJdbc(boolean isCheckJdbc) {
		this.isCheckJdbc = isCheckJdbc;
	}
}
