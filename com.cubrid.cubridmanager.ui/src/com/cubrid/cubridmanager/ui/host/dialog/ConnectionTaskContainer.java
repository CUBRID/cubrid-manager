package com.cubrid.cubridmanager.ui.host.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.TableItem;

import com.cubrid.common.core.task.AbstractTask;
import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfos;
import com.cubrid.cubridmanager.core.broker.task.GetBrokerConfParameterTask;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.model.CubridManagerConfParaConstants;
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
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.host.Messages;
import com.cubrid.cubridmanager.ui.mondashboard.preference.MonitorDashboardPreference;
import com.cubrid.cubridmanager.ui.spi.Version;
import com.cubrid.cubridmanager.ui.spi.model.loader.CubridDatabasesFolderLoader;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;
import com.cubrid.jdbc.proxy.manage.ServerJdbcVersionMapping;

public class ConnectionTaskContainer implements Callable<Integer>, Runnable {
	private final List<DatabaseInfo> allDatabaseInfoList = new ArrayList<DatabaseInfo>();
	private final List<DatabaseInfo> authDatabaseList = new ArrayList<DatabaseInfo>();
	private boolean isCanceled = false;
	private boolean isFinished = false;
	private boolean isSuccess = false;
	private TableItem item;

	private ICubridNode server;

	private ServerInfo serverInfo;

	private List<ITask> tasks = new ArrayList<ITask>();

	public ConnectionTaskContainer(ICubridNode node) {
		this.server = node;
		this.serverInfo = ((CubridServer)node).getServerInfo();
		initTask(this.serverInfo);
	}

	public void addTask(ITask task) {
		if (tasks == null) {
			tasks = new ArrayList<ITask>();
		}
		tasks.add(task);
	}

	public Integer call() {
		ProgressBar bar = (ProgressBar)item.getData(MultiHostConnectionDialog.KEY_BAR);

		MonitorDashboardPreference monPref = new MonitorDashboardPreference();

		boolean isRunUpdateCmUserTask = false;
		BrokerInfos brokerInfos = null;
		int size = tasks.size();
		int taskIdx = 1;
		for (ITask task : tasks) {
			if (task instanceof MonitoringTask) {
				if (!serverInfo.isConnected()) {
					CMHostNodePersistManager.getInstance().addServer(serverInfo.getHostAddress(), serverInfo.getHostMonPort(),
						serverInfo.getUserName(), serverInfo);
					MonitoringTask monitoringTask = (MonitoringTask)task;
					serverInfo = monitoringTask.connectServer(Version.releaseVersion, monPref.getHAHeartBeatTimeout());
					if (serverInfo.isConnected()) {
						CMHostNodePersistManager.getInstance().addServer(serverInfo.getHostAddress(), serverInfo.getHostMonPort(),
							serverInfo.getUserName(), serverInfo);
					} else {
						CMHostNodePersistManager.getInstance().removeServer(serverInfo.getHostAddress(),
							serverInfo.getHostMonPort(), serverInfo.getUserName());
					}
				}
			} else if ((task instanceof UpdateCMUserTask)) {
				updateCMUserAuthInfo(task, isRunUpdateCmUserTask);
			} else {
				task.execute();
			}

			final String msg = task.getErrorMsg();
			if (msg != null && msg.length() > 0) {
				item.setText(3, msg);
				disConnect();
				isFinished = true;
				updateIcon();
				return 1;
			}

			if (task instanceof GetEnvInfoTask) {
				GetEnvInfoTask getEnvInfoTask = (GetEnvInfoTask)task;
				EnvInfo envInfo = getEnvInfoTask.loadEnvInfo();
				serverInfo.setEnvInfo(envInfo);
				String clientVersion = getClientVerion();
				if (!isClientSupport(clientVersion)) {
					item.setText(3, Messages.bind(Messages.errNoSupportServerVersion, clientVersion));
					disConnect();
					isFinished = true;
					updateIcon();
					return 2;
				}
				String jdbcVersion = serverInfo.getJdbcDriverVersion();
				if (serverInfo.validateJdbcVersion(jdbcVersion)) {
					if (ServerJdbcVersionMapping.JDBC_SELF_ADAPTING_VERSION.equals(jdbcVersion)) {
						serverInfo.setJdbcDriverVersion(ServerInfo.getAutoDetectJdbcVersion(serverInfo.getFullServerVersionKey()));
					}
				} else {
					if (ServerJdbcVersionMapping.JDBC_SELF_ADAPTING_VERSION.equals(jdbcVersion)) {
						item.setText(3, Messages.errNoSupportDriver);
					} else {
						item.setText(3, Messages.errSelectSupportDriver);
					}
					disConnect();
					isFinished = true;
					updateIcon();
					return 3;
				}
			} else if (task instanceof GetDatabaseListTask) {
				GetDatabaseListTask getDatabaseListTask = (GetDatabaseListTask)task;
				List<DatabaseInfo> databaseInfoList = getDatabaseListTask.loadDatabaseInfo();
				if (databaseInfoList != null) {
					allDatabaseInfoList.addAll(databaseInfoList);
				}
			} else if (task instanceof GetCMConfParameterTask) {
				GetCMConfParameterTask getCMConfParameterTask = (GetCMConfParameterTask)task;
				Map<String, String> confParameters = getCMConfParameterTask.getConfParameters();
				ServerType serverType = ServerType.BOTH;
				if (confParameters != null) {
					String target = confParameters.get(CubridManagerConfParaConstants.CM_TARGET);
					if (target != null) {
						if (target.indexOf("broker") >= 0 && target.indexOf("server") >= 0) {
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
				CommonQueryTask<BrokerInfos> getBrokerTask = (CommonQueryTask<BrokerInfos>)task;
				brokerInfos = getBrokerTask.getResultModel();
				if (serverInfo != null) {
					serverInfo.setBrokerInfos(brokerInfos);
				}
			} else if (task instanceof GetCMUserListTask) {
				if (serverInfo != null && serverInfo.isConnected()) {
					GetCMUserListTask getUserInfoTask = (GetCMUserListTask)task;
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
					isRunUpdateCmUserTask = CubridDatabasesFolderLoader.filterDatabaseList(serverInfo,
						allDatabaseInfoList, authDatabaseList);
					if (isRunUpdateCmUserTask) {
						serverInfo.getLoginedUserInfo().setDatabaseInfoList(authDatabaseList);
					}
				}
			} else if (task instanceof GetCubridConfParameterTask) {
				GetCubridConfParameterTask getCubridConfParameterTask = (GetCubridConfParameterTask)task;
				Map<String, Map<String, String>> confParas = getCubridConfParameterTask.getConfParameters();
				if (serverInfo != null) {
					serverInfo.setCubridConfParaMap(confParas);
				}
			} else if (task instanceof GetBrokerConfParameterTask) {
				GetBrokerConfParameterTask getBrokerConfParameterTask = (GetBrokerConfParameterTask)task;
				Map<String, Map<String, String>> confParas = getBrokerConfParameterTask.getConfParameters();
				if (serverInfo != null) {
					serverInfo.setBrokerConfParaMap(confParas);
				}
			} else if (task instanceof FinishTask) {
				isSuccess = true;
				server.getLoader().setLoaded(false);
				CubridNodeManager.getInstance().fireCubridNodeChanged(
					new CubridNodeChangedEvent(server, CubridNodeChangedEventType.SERVER_CONNECTED));

			}
			bar.setSelection(taskIdx++ / size * 100);
			if (isCanceled()) {
				isFinished = true;
				updateIcon();
				return 4;
			}
		}
		bar.setSelection(100);
		item.setText(3, Messages.msgConneted/* "Connected!" */);
		isFinished = true;
		return 0;
	}

	/**
	 * 
	 * Disconnect host
	 * 
	 */
	private void disConnect() {
		if (serverInfo != null) {
			ServerManager.setConnected(serverInfo.getHostAddress(), serverInfo.getHostMonPort(),
				serverInfo.getUserName(), false);
		}
	}

	private String getClientVerion() {
		return Version.buildVersionId.substring(0, Version.buildVersionId.lastIndexOf("."));
	}

	public ICubridNode getServer() {
		return server;
	}

	public ServerInfo getServerInfo() {
		return serverInfo;
	}

	public List<ITask> getTasks() {
		return tasks;
	}

	public void initTask(ServerInfo serverInfo) {
		MonitoringTask monitoringTask = serverInfo.getMonitoringTask();
		GetEnvInfoTask getEnvInfoTask = new GetEnvInfoTask(serverInfo);
		GetDatabaseListTask getDatabaseListTask = new GetDatabaseListTask(serverInfo);
		GetCMConfParameterTask getCMConfParameterTask = new GetCMConfParameterTask(serverInfo);
		CommonQueryTask<BrokerInfos> getBrokerTask = new CommonQueryTask<BrokerInfos>(serverInfo,
			CommonSendMsg.getCommonSimpleSendMsg(), new BrokerInfos());
		GetCMUserListTask getUserInfoTask = new GetCMUserListTask(serverInfo);
		UpdateCMUserTask updateTask = new UpdateCMUserTask(serverInfo);
		updateTask.setCmUserName(serverInfo.getUserName());
		GetCubridConfParameterTask getCubridConfParameterTask = new GetCubridConfParameterTask(serverInfo);
		GetBrokerConfParameterTask getBrokerConfParameterTask = new GetBrokerConfParameterTask(serverInfo);

		addTask(monitoringTask);
		addTask(getEnvInfoTask);
		addTask(getDatabaseListTask);
		addTask(getCMConfParameterTask);
		addTask(getBrokerTask);
		addTask(getUserInfoTask);
		addTask(updateTask);
		addTask(getCubridConfParameterTask);
		addTask(getBrokerConfParameterTask);
		addTask(new FinishTask());

	}

	public boolean isCanceled() {
		return isCanceled;
	}

	/**
	 * The version of server and version of client should be matched.
	 * 
	 * @param clientVersion String
	 * @return true or false
	 */
	private boolean isClientSupport(String clientVersion) {
		return CompatibleUtil.isSupportCMServer(serverInfo, clientVersion);
	}

	public boolean isFinished() {
		return isFinished;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void run() {
		call();
	}

	public void setCanceled(boolean isCanceled) {
		this.isCanceled = isCanceled;
	}

	public void setServer(ICubridNode server) {
		this.server = server;
	}

	public void setItem(TableItem item) {
		this.item = item;
	}

	public TableItem getItem() {
		return item;
	}

	/**
	 * 
	 * Construct the CM user authorization information
	 * 
	 * @param task the UpdateCMUserTask object
	 * @param isRunUpdateCmUserTask whether update CM user task
	 */
	private void updateCMUserAuthInfo(ITask task, boolean isRunUpdateCmUserTask) {
		if (isRunUpdateCmUserTask && serverInfo != null && serverInfo.isConnected()) {
			UpdateCMUserTask updateCMUserTask = (UpdateCMUserTask)task;
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
			updateCMUserTask.setDbAuth(dbNameList.toArray(dbNameArr), dbUserList.toArray(dbUserArr),
				dbPasswordList.toArray(dbPasswordArr), dbBrokerPortList.toArray(dbBrokerPortArr));
			updateCMUserTask.execute();
		}
	}

	public void updateIcon() {
		getItem().setImage(0, CubridManagerUIPlugin.getImage("icons/monitor/error.gif"));
	}
}

class FinishTask extends AbstractTask {

	public FinishTask() {
	}

	public void cancel() {

	}

	public void execute() {
		return;
	}

	public void finish() {
	}

	public boolean isCancel() {
		return false;
	}

	public boolean isSuccess() {
		return true;
	}

}