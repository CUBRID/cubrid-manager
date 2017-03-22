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
package com.cubrid.cubridmanager.ui.mondashboard.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Display;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.progress.TaskJobExecutor;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfo;
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
import com.cubrid.cubridmanager.core.mondashboard.model.HAHostStatusInfo;
import com.cubrid.cubridmanager.core.mondashboard.task.GetHeartbeatNodeInfoTask;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.host.Messages;
import com.cubrid.cubridmanager.ui.host.dialog.ChangePasswordDialog;
import com.cubrid.cubridmanager.ui.mondashboard.dialog.ConnectHostNodeDialog;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataProvider;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.BrokerNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.DatabaseNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.mondashboard.preference.MonitorDashboardPreference;
import com.cubrid.cubridmanager.ui.spi.Version;
import com.cubrid.cubridmanager.ui.spi.model.loader.CubridDatabasesFolderLoader;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;
import com.cubrid.cubridmanager.ui.spi.util.HAUtil;
import com.cubrid.jdbc.proxy.manage.ServerJdbcVersionMapping;

/**
 * 
 * Connect host job executor
 * 
 * @author pangqiren
 * @version 1.0 - 2010-1-6 created by pangqiren
 */
public class ConnectHostJobExecutor extends
		TaskJobExecutor {

	boolean isContinue = false;
	private final List<DatabaseInfo> authDatabaseList = new ArrayList<DatabaseInfo>();
	private final List<DatabaseInfo> allDatabaseInfoList = new ArrayList<DatabaseInfo>();
	private ServerInfo serverInfo;
	private final HostNode hostNode;
	private HAHostStatusInfo haHostStatusInfo;
	private final boolean wasConnected;
	private final boolean isPing;
	private String errorMsg = null;

	/**
	 * The constructor
	 * 
	 * @param hostNode
	 * @param serInfo
	 * @param isPing
	 */
	public ConnectHostJobExecutor(HostNode hostNode, ServerInfo serInfo,
			boolean isPing) {
		this.serverInfo = serInfo;
		this.hostNode = hostNode;
		wasConnected = serInfo.isConnected();
		this.isPing = isPing;

		GetHeartbeatNodeInfoTask getHeartbeatNodeInfoTask = new GetHeartbeatNodeInfoTask(
				serverInfo);
		getHeartbeatNodeInfoTask.setAllDb(false);
		List<String> dbList = new ArrayList<String>();
		if (hostNode.getDbNodeList() != null) {
			for (DatabaseNode dbNode : hostNode.getDbNodeList()) {
				dbList.add(dbNode.getDbName());
			}
		}
		getHeartbeatNodeInfoTask.setDbList(dbList);

		if (!wasConnected) {
			MonitoringTask monitoringTask = serverInfo.getMonitoringTask();
			monitoringTask.setTimeout(DataProvider.TIME_OUT_MILL);
			GetEnvInfoTask getEnvInfoTask = new GetEnvInfoTask(serverInfo);
			GetDatabaseListTask getDatabaseListTask = new GetDatabaseListTask(
					serverInfo);
			GetCMConfParameterTask getCMConfParameterTask = new GetCMConfParameterTask(
					serverInfo);
			CommonQueryTask<BrokerInfos> getBrokerTask = new CommonQueryTask<BrokerInfos>(
					serverInfo, CommonSendMsg.getCommonSimpleSendMsg(),
					new BrokerInfos());
			GetCMUserListTask getUserInfoTask = new GetCMUserListTask(
					serverInfo);
			UpdateCMUserTask updateTask = new UpdateCMUserTask(serverInfo);
			updateTask.setCmUserName(serverInfo.getUserName());
			GetCubridConfParameterTask getCubridConfParameterTask = new GetCubridConfParameterTask(
					serverInfo);
			GetBrokerConfParameterTask getBrokerConfParameterTask = new GetBrokerConfParameterTask(
					serverInfo);
			addTask(monitoringTask);
			addTask(getEnvInfoTask);
			addTask(getDatabaseListTask);
			addTask(getCMConfParameterTask);
			addTask(getBrokerTask);
			addTask(getUserInfoTask);
			addTask(updateTask);
			addTask(getCubridConfParameterTask);
			addTask(getBrokerConfParameterTask);
		}
		addTask(getHeartbeatNodeInfoTask);
	}

	/**
	 * Cancel to connect host
	 */
	public void cancel() {
		super.cancel();
		if (serverInfo != null && taskList.size() > 1 && !wasConnected) {
			ServerManager.getInstance().setConnected(
					serverInfo.getHostAddress(), serverInfo.getHostMonPort(),
					serverInfo.getUserName(), false);
		}
	}

	/**
	 * 
	 * Disconnect host
	 * 
	 */
	private void disConnect() {
		if (serverInfo != null && taskList.size() > 1 && !wasConnected) {
			ServerManager.getInstance().setConnected(
					serverInfo.getHostAddress(), serverInfo.getHostMonPort(),
					serverInfo.getUserName(), false);
		}
	}

	/**
	 * 
	 * Change admin user password
	 * 
	 */
	private void changePassword() {
		if (serverInfo.isConnected() && serverInfo.getUserName() != null
				&& serverInfo.getUserName().equals("admin")
				&& serverInfo.getUserPassword().equals("admin")) {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					ChangePasswordDialog dialog = new ChangePasswordDialog(
							null, true);
					dialog.setServerInfo(serverInfo);
					if (dialog.open() != IDialogConstants.OK_ID) {
						isContinue = false;
					}
				}
			});
		}
	}

	/**
	 * 
	 * Construct the CM user authorization information
	 * 
	 * @param task the UpdateCMUserTask object
	 * @param isRunUpdateCmUserTask whether update CM user task
	 */
	private void updateCMUserAuthInfo(ITask task, boolean isRunUpdateCmUserTask) {
		if (isRunUpdateCmUserTask && serverInfo != null
				&& serverInfo.isConnected()) {
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
					dbBrokerPortList.add(QueryOptions.getBrokerIp(databaseInfo)
							+ "," + databaseInfo.getBrokerPort());
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
	 * 
	 * Connect host
	 * 
	 * @param task The MonitoringTask
	 * @return IStatus
	 */
	private IStatus connectHost(MonitoringTask task) {
		MonitorDashboardPreference monPref = new MonitorDashboardPreference();
		
		while (!serverInfo.isConnected() && isContinue) {
			serverInfo = task.connectServer(Version.releaseVersion,
					monPref.getHAHeartBeatTimeout());
			final String msg = task.getErrorMsg();
			if (this.isPing && msg != null && msg.length() > 0) {
				return Status.CANCEL_STATUS;
			}
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					if (msg != null && msg.length() > 0) {
						ConnectHostNodeDialog dialog = new ConnectHostNodeDialog(
								null, hostNode, msg);
						if (dialog.open() == IDialogConstants.OK_ID) {
							serverInfo.setHostMonPort(Integer.parseInt(dialog.getPort()));
							serverInfo.setUserPassword(dialog.getPassword());
						} else {
							isContinue = false;
						}
					}
				}
			});
		}
		if (serverInfo.isConnected()) {
			CMHostNodePersistManager.getInstance().addServer(serverInfo.getHostAddress(),
					serverInfo.getHostMonPort(), serverInfo.getUserName(),
					serverInfo);
		}
		changePassword();
		if (!isContinue) {
			disConnect();
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}

	/**
	 * Execute to connect host
	 * 
	 * @param monitor the IProgressMonitor
	 * @return <code>true</code> if successful;<code>false</code>otherwise;
	 */
	@SuppressWarnings("unchecked")
	public IStatus exec(final IProgressMonitor monitor) {
		monitor.subTask(Messages.bind(
				com.cubrid.cubridmanager.ui.mondashboard.Messages.jobConnectHost,
				serverInfo.getHostAddress()));
		isContinue = true;
		boolean isRunUpdateCmUserTask = false;
		BrokerInfos brokerInfos = null;
		errorMsg = null;
		for (ITask task : taskList) {
			if (task instanceof MonitoringTask) {
				IStatus status = connectHost((MonitoringTask) task);
				if (Status.OK_STATUS != status) {
					return status;
				}
			} else if ((task instanceof UpdateCMUserTask)) {
				updateCMUserAuthInfo(task, isRunUpdateCmUserTask);
			} else if ((task instanceof GetHeartbeatNodeInfoTask)
					&& !CompatibleUtil.isSupportHA(serverInfo)) {
				continue;
			} else {
				task.execute();
			}
			final String msg = task.getErrorMsg();
			if (monitor.isCanceled()) {
				disConnect();
				return Status.CANCEL_STATUS;
			}
			if (!hostNode.isConnecting()) {
				return Status.CANCEL_STATUS;
			}
			if (msg != null && msg.length() > 0) {
				disConnect();
				if (isPing) {
					errorMsg = msg;
					return Status.CANCEL_STATUS;
				}
				return new Status(IStatus.ERROR,
						CubridManagerUIPlugin.PLUGIN_ID, msg);
			}

			if (task instanceof GetEnvInfoTask) {
				GetEnvInfoTask getEnvInfoTask = (GetEnvInfoTask) task;
				EnvInfo envInfo = getEnvInfoTask.loadEnvInfo();
				serverInfo.setEnvInfo(envInfo);
				String clientVersion = Version.buildVersionId.substring(0,
						Version.buildVersionId.lastIndexOf("."));
				if (!CompatibleUtil.isSupportCMServer(
						serverInfo, clientVersion)) {
					disConnect();
					errorMsg = Messages.bind(
							Messages.errNoSupportServerVersion, clientVersion);
					if (isPing) {
						return Status.CANCEL_STATUS;
					}
					return new Status(IStatus.ERROR,
							CubridManagerUIPlugin.PLUGIN_ID, errorMsg);
				}
				if (!serverInfo.validateJdbcVersion(serverInfo.getJdbcDriverVersion())) {
					disConnect();
					if (ServerJdbcVersionMapping.JDBC_SELF_ADAPTING_VERSION.equals(serverInfo.getJdbcDriverVersion())) {
						errorMsg = Messages.errNoSupportDriver;
					} else {
						errorMsg = Messages.errSelectSupportDriver;
					}
					if (isPing) {
						return Status.CANCEL_STATUS;
					}
					return new Status(IStatus.ERROR,
							CubridManagerUIPlugin.PLUGIN_ID, errorMsg);
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
			} else if (task instanceof CommonQueryTask) {
				CommonQueryTask<BrokerInfos> getBrokerTask = (CommonQueryTask<BrokerInfos>) task;
				brokerInfos = getBrokerTask.getResultModel();
				if (serverInfo != null) {
					serverInfo.setBrokerInfos(brokerInfos);
				}
			} else if (task instanceof GetCMUserListTask) {
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
					List<DatabaseInfo> databaseInfoList = serverInfo.getLoginedUserInfo().getDatabaseInfoList();
					if (databaseInfoList != null) {
						authDatabaseList.addAll(databaseInfoList);
					}
					isRunUpdateCmUserTask = CubridDatabasesFolderLoader.filterDatabaseList(
							serverInfo, allDatabaseInfoList, authDatabaseList);
					if (isRunUpdateCmUserTask) {
						serverInfo.getLoginedUserInfo().setDatabaseInfoList(
								authDatabaseList);
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
			} else if (task instanceof GetHeartbeatNodeInfoTask) {
				GetHeartbeatNodeInfoTask getHeartbeatNodeInfoTask = (GetHeartbeatNodeInfoTask) task;
				haHostStatusInfo = getHeartbeatNodeInfoTask.getHostStatusInfo(serverInfo.getHostAddress());
			}
			if (monitor.isCanceled() || !isContinue) {
				disConnect();
				return Status.CANCEL_STATUS;
			}
		}
		return Status.OK_STATUS;
	}

	/**
	 * Notification that the create replication job has completed execution.
	 * 
	 * @param event the event details
	 */
	public void done(final IJobChangeEvent event) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				done(event.getResult());
			}
		});
	}

	/**
	 * 
	 * Finish the task executor
	 * 
	 * @param status IStatus
	 */
	public void finish(final IStatus status) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				done(status);
			}
		});
	}

	/**
	 * 
	 * Finish the task executor
	 * 
	 * @param status IStatus
	 */
	private void done(final IStatus status) {

		if (!hostNode.isConnecting()) {
			hostNode.setConnected(false);
			return;
		}
		if (status == Status.OK_STATUS) {
			hostNode.setPort(String.valueOf(serverInfo.getHostMonPort()));
			hostNode.setPassword(serverInfo.getUserPassword());
			hostNode.setHostStatusInfo(haHostStatusInfo);
			hostNode.setConnected(true);

			if (haHostStatusInfo == null) {
				haHostStatusInfo = HAUtil.getHAHostStatusInfo(serverInfo);
			}

			for (DatabaseNode dn : hostNode.getDbNodeList()) {
				dn.setHaDatabaseStatus(HAUtil.getHADatabaseStatusInfo(
						dn.getDbName(), haHostStatusInfo, serverInfo));
			}

			for (BrokerNode brokerNode : hostNode.getBrokerNodeList()) {
				BrokerInfo brokerInfo = HAUtil.getBrokerInfo(
						serverInfo.getBrokerInfos(), brokerNode.getBrokerName());
				brokerNode.setBrokerInfo(brokerInfo);
			}
		} else {
			hostNode.setConnected(wasConnected);
			if (errorMsg != null) {
				hostNode.setErrorMsg(errorMsg);
			}
		}
		hostNode.setConnecting(false);

	}
}
