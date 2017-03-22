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
package com.cubrid.cubridmanager.ui.replication.editor;

import java.util.ArrayList;
import java.util.List;

import com.cubrid.common.ui.spi.progress.TaskGroup;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.common.task.CommonTaskName;
import com.cubrid.cubridmanager.core.common.task.CommonUpdateTask;
import com.cubrid.cubridmanager.core.common.task.GetCubridConfParameterTask;
import com.cubrid.cubridmanager.core.common.task.MonitoringTask;
import com.cubrid.cubridmanager.core.common.task.SetCubridConfParameterTask;
import com.cubrid.cubridmanager.core.cubrid.database.task.BackupDbTask;
import com.cubrid.cubridmanager.core.cubrid.database.task.GetDatabaseListTask;
import com.cubrid.cubridmanager.core.replication.task.CancelTransFileTask;
import com.cubrid.cubridmanager.core.replication.task.ChangeReplTablesTask;
import com.cubrid.cubridmanager.core.replication.task.CreateDistributorTask;
import com.cubrid.cubridmanager.core.replication.task.CreateSlaveDbTask;
import com.cubrid.cubridmanager.core.replication.task.GetReplAgentStatusTask;
import com.cubrid.cubridmanager.core.replication.task.GetReplServerStatusTask;
import com.cubrid.cubridmanager.core.replication.task.GetTransferProgressTask;
import com.cubrid.cubridmanager.core.replication.task.SetReplicationParamTask;
import com.cubrid.cubridmanager.core.replication.task.StartReplServerTask;
import com.cubrid.cubridmanager.core.replication.task.StartReplicationAgentTask;
import com.cubrid.cubridmanager.core.replication.task.StopReplServerTask;
import com.cubrid.cubridmanager.core.replication.task.StopReplicationAgentTask;
import com.cubrid.cubridmanager.core.replication.task.TransFileTask;
import com.cubrid.cubridmanager.core.utils.ModelUtil.YesNoType;
import com.cubrid.cubridmanager.ui.replication.Messages;
import com.cubrid.cubridmanager.ui.replication.editor.model.DistributorNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.MasterNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.Node;
import com.cubrid.cubridmanager.ui.replication.editor.model.SlaveNode;

/**
 * create replication utilities
 * 
 * @author pangqiren
 * @version 1.0 - 2009-12-24 created by pangqiren
 */
public final class CreateReplicationUtil {

	private CreateReplicationUtil() {
		//empty
	}

	/**
	 * 
	 * Fill in the related tasks of creating master to executor according master
	 * information
	 * 
	 * @param master MasterNode
	 * @param slaveList List<SlaveNode>
	 * @param executor CreateReplicationJobExecutor
	 * @return null or error message
	 */
	public static String createMasterTaskGroup(MasterNode master,
			List<SlaveNode> slaveList, CreateReplicationJobExecutor executor) {
		TaskGroup taskGroup = new TaskGroup(
				IConstants.REPL_GROUP_NAME_CREATE_MASTER);
		taskGroup.setTarget(master.getDbName());
		HostNode masterHost = (HostNode) master.getParent();
		final String ip = masterHost.getIp();
		final String port = masterHost.getPort();
		final String userName = masterHost.getUserName();
		final String password = masterHost.getPassword();
		boolean isConnected = ServerManager.getInstance().isConnected(ip,
				Integer.parseInt(port), userName);
		ServerInfo serverInfo = new ServerInfo();
		if (isConnected) {
			serverInfo = ServerManager.getInstance().getServer(ip,
					Integer.parseInt(port), userName);
			if (!serverInfo.getLoginedUserInfo().isAdmin()) {
				return Messages.bind(Messages.errInvalidUser, ip);
			}
		} else {
			serverInfo.setHostAddress(ip);
			serverInfo.setHostMonPort(Integer.parseInt(port));
			serverInfo.setHostJSPort(Integer.parseInt(port) + 1);
			serverInfo.setUserName(userName);
			serverInfo.setUserPassword(password);

			MonitoringTask monitoringTask = new MonitoringTask(serverInfo);
			taskGroup.addTask(monitoringTask);
		}

		GetReplServerStatusTask getReplServerStatusTask = new GetReplServerStatusTask(
				serverInfo);
		getReplServerStatusTask.setDbName(master.getDbName());
		taskGroup.addTask(getReplServerStatusTask);

		StopReplServerTask stopReplServerTask = new StopReplServerTask(
				serverInfo);
		stopReplServerTask.setDbName(master.getDbName());
		taskGroup.addTask(stopReplServerTask);

		GetCubridConfParameterTask getCubridConfParameterTask = new GetCubridConfParameterTask(
				serverInfo);
		getCubridConfParameterTask.putData("dbName", master.getDbName());
		taskGroup.addTask(getCubridConfParameterTask);

		SetCubridConfParameterTask setCubridConfParameterTask = new SetCubridConfParameterTask(
				serverInfo);
		taskGroup.addTask(setCubridConfParameterTask);

		GetDatabaseListTask getDatabaseListTask = new GetDatabaseListTask(
				serverInfo);
		getDatabaseListTask.putData("dbName", master.getDbName());
		taskGroup.addTask(getDatabaseListTask);

		CommonUpdateTask stopDbTask = new CommonUpdateTask(
				CommonTaskName.STOP_DB_TASK_NAME, serverInfo,
				CommonSendMsg.getCommonDatabaseSendMsg());
		stopDbTask.setDbName(master.getDbName());
		taskGroup.addTask(stopDbTask);

		CommonUpdateTask startDbTask = new CommonUpdateTask(
				CommonTaskName.START_DB_TASK_NAME, serverInfo,
				CommonSendMsg.getCommonDatabaseSendMsg());
		startDbTask.setDbName(master.getDbName());
		taskGroup.addTask(startDbTask);

		BackupDbTask backupDbTask = new BackupDbTask(serverInfo);
		backupDbTask.setDbName(master.getDbName());
		backupDbTask.setLevel("0");
		backupDbTask.setVolumeName(master.getDbName() + "_bk0v000");
		backupDbTask.setBackupDir(master.getDbPath());
		backupDbTask.setRemoveLog(false);
		backupDbTask.setCheckDatabaseConsist(true);
		backupDbTask.setThreadCount(String.valueOf(0));
		backupDbTask.setZiped(false);
		backupDbTask.setSafeReplication(false);
		taskGroup.addTask(backupDbTask);

		CancelTransFileTask cancelTransFileTask = new CancelTransFileTask(
				serverInfo);
		taskGroup.addTask(cancelTransFileTask);

		for (int i = 0; i < slaveList.size(); i++) {
			SlaveNode slave = slaveList.get(i);
			HostNode slaveHost = (HostNode) slave.getParent();
			TransFileTask transFileTask = new TransFileTask(serverInfo);
			transFileTask.setMasterDbDir(master.getDbPath());
			List<String> fileList = new ArrayList<String>();
			fileList.add(master.getDbName() + "_bk0v000");
			fileList.add(master.getDbName() + "_bkvinf");
			transFileTask.setBackupFileList(fileList);
			transFileTask.setSlaveDbHost(slaveHost.getIp());
			transFileTask.setSlaveCmServerPort(String.valueOf(Integer.parseInt(slaveHost.getPort()) + 1));
			transFileTask.setSlaveDbDir(slave.getDbPath());
			taskGroup.addTask(transFileTask);

			GetTransferProgressTask getTransferProgressTask = new GetTransferProgressTask(
					serverInfo);
			taskGroup.addTask(getTransferProgressTask);
		}

		executor.addGroupTask(taskGroup);
		return null;
	}

	/**
	 * 
	 * Fill in the related tasks of creating master to executor according to
	 * slave and master information,this slave node is also master node.
	 * 
	 * @param master MasterNode
	 * @param slave SlaveNode
	 * @param executor CreateReplicationJobExecutor
	 * @return null or error message
	 */
	public static String createSlaveMasterTaskGroup(MasterNode master,
			SlaveNode slave, CreateReplicationJobExecutor executor) {
		TaskGroup taskGroup = new TaskGroup(
				IConstants.REPL_GROUP_NAME_CREATE_MASTER);
		taskGroup.setTarget(master.getDbName());
		HostNode slaveHost = (HostNode) slave.getParent();
		final String ip = slaveHost.getIp();
		final String port = slaveHost.getPort();
		final String userName = slaveHost.getUserName();
		final String password = slaveHost.getPassword();
		boolean isConnected = ServerManager.getInstance().isConnected(ip,
				Integer.parseInt(port), userName);
		ServerInfo serverInfo = new ServerInfo();
		if (isConnected) {
			serverInfo = ServerManager.getInstance().getServer(ip,
					Integer.parseInt(port), userName);
			if (!serverInfo.getLoginedUserInfo().isAdmin()) {
				return Messages.bind(Messages.errInvalidUser, ip);
			}
		} else {
			serverInfo.setHostAddress(ip);
			serverInfo.setHostMonPort(Integer.parseInt(port));
			serverInfo.setHostJSPort(Integer.parseInt(port) + 1);
			serverInfo.setUserName(userName);
			serverInfo.setUserPassword(password);

			MonitoringTask monitoringTask = new MonitoringTask(serverInfo);
			taskGroup.addTask(monitoringTask);
		}

		GetReplServerStatusTask getReplServerStatusTask = new GetReplServerStatusTask(
				serverInfo);
		getReplServerStatusTask.setDbName(slave.getDbName());
		taskGroup.addTask(getReplServerStatusTask);

		StopReplServerTask stopReplServerTask = new StopReplServerTask(
				serverInfo);
		stopReplServerTask.setDbName(slave.getDbName());
		taskGroup.addTask(stopReplServerTask);

		GetCubridConfParameterTask getCubridConfParameterTask = new GetCubridConfParameterTask(
				serverInfo);
		getCubridConfParameterTask.putData("dbName", slave.getDbName());
		taskGroup.addTask(getCubridConfParameterTask);

		SetCubridConfParameterTask setCubridConfParameterTask = new SetCubridConfParameterTask(
				serverInfo);
		taskGroup.addTask(setCubridConfParameterTask);

		GetDatabaseListTask getDatabaseListTask = new GetDatabaseListTask(
				serverInfo);
		getDatabaseListTask.putData("dbName", slave.getDbName());
		taskGroup.addTask(getDatabaseListTask);

		CommonUpdateTask stopDbTask = new CommonUpdateTask(
				CommonTaskName.STOP_DB_TASK_NAME, serverInfo,
				CommonSendMsg.getCommonDatabaseSendMsg());
		stopDbTask.setDbName(slave.getDbName());
		taskGroup.addTask(stopDbTask);

		CommonUpdateTask startDbTask = new CommonUpdateTask(
				CommonTaskName.START_DB_TASK_NAME, serverInfo,
				CommonSendMsg.getCommonDatabaseSendMsg());
		startDbTask.setDbName(slave.getDbName());
		taskGroup.addTask(startDbTask);

		BackupDbTask backupDbTask = new BackupDbTask(serverInfo);
		backupDbTask.setDbName(slave.getDbName());
		backupDbTask.setLevel("0");
		backupDbTask.setVolumeName(slave.getDbName() + "_bk0v000");
		backupDbTask.setBackupDir(slave.getDbPath());
		backupDbTask.setRemoveLog(false);
		backupDbTask.setCheckDatabaseConsist(true);
		backupDbTask.setThreadCount(String.valueOf(0));
		backupDbTask.setZiped(false);
		backupDbTask.setSafeReplication(false);
		taskGroup.addTask(backupDbTask);

		executor.addGroupTask(taskGroup);
		return null;
	}

	/**
	 * 
	 * Fill in the related task of creating distributor to executor
	 * 
	 * @param master MasterNode
	 * @param dist DistributorNode
	 * @param executor CreateReplicationJobExecutor
	 * @return null or error message
	 */
	public static String createDistributorTaskGroup(MasterNode master,
			DistributorNode dist, CreateReplicationJobExecutor executor) {
		TaskGroup taskGroup = new TaskGroup(
				IConstants.REPL_GROUP_NAME_CREATE_DIST);
		taskGroup.setTarget(dist.getDbName());
		HostNode distdbHost = (HostNode) dist.getParent();
		HostNode masterHost = (HostNode) master.getParent();
		final String ip = distdbHost.getIp();
		final String port = distdbHost.getPort();
		final String userName = distdbHost.getUserName();
		final String password = distdbHost.getPassword();
		boolean isConnected = ServerManager.getInstance().isConnected(ip,
				Integer.parseInt(port), userName);
		ServerInfo serverInfo = new ServerInfo();
		if (isConnected) {
			serverInfo = ServerManager.getInstance().getServer(ip,
					Integer.parseInt(port), userName);
			if (!serverInfo.getLoginedUserInfo().isAdmin()) {
				return Messages.bind(Messages.errInvalidUser, ip);
			}
		} else {
			serverInfo.setHostAddress(ip);
			serverInfo.setHostMonPort(Integer.parseInt(port));
			serverInfo.setHostJSPort(Integer.parseInt(port) + 1);
			serverInfo.setUserName(userName);
			serverInfo.setUserPassword(password);

			MonitoringTask monitoringTask = new MonitoringTask(serverInfo);
			taskGroup.addTask(monitoringTask);
		}
		addCheckDbTaskGroup(serverInfo, dist.getDbName(), taskGroup);

		CreateDistributorTask createDistributorTask = new CreateDistributorTask(
				serverInfo);
		createDistributorTask.setDistDbName(dist.getDbName());
		createDistributorTask.setDistDbPath(dist.getDbPath());
		createDistributorTask.setDbaPassword(dist.getDbaPassword());
		createDistributorTask.setReplAgentPort(dist.getReplAgentPort());
		createDistributorTask.setMasterDbName(master.getDbName());
		createDistributorTask.setMasterDbIp(masterHost.getIp());
		createDistributorTask.setReplServerPort(master.getReplServerPort());
		createDistributorTask.setCopyLogPath(dist.getCopyLogPath());
		createDistributorTask.setErrorLogPath(dist.getErrorLogPath());
		createDistributorTask.setTrailLogPath(dist.getTrailLogPath());
		createDistributorTask.setDelayTimeLogSize(dist.getDelayTimeLogSize());
		createDistributorTask.setRestartRepl(dist.isRestartWhenError());
		taskGroup.addTask(createDistributorTask);

		executor.addGroupTask(taskGroup);

		return null;
	}

	/**
	 * 
	 * Fill in the related tasks of check database to executor
	 * 
	 * @param serverInfo ServerInfo
	 * @param dbName String
	 * @param taskGroup TaskGroup
	 */
	public static void addCheckDbTaskGroup(ServerInfo serverInfo,
			String dbName, TaskGroup taskGroup) {
		GetDatabaseListTask getDatabaseListTask = new GetDatabaseListTask(
				serverInfo);
		getDatabaseListTask.putData("dbName", dbName);
		taskGroup.addTask(getDatabaseListTask);

		CommonUpdateTask stopDbTask = new CommonUpdateTask(
				CommonTaskName.STOP_DB_TASK_NAME, serverInfo,
				CommonSendMsg.getCommonDatabaseSendMsg());
		stopDbTask.setDbName(dbName);
		taskGroup.addTask(stopDbTask);

		CommonUpdateTask deleteDbTask = new CommonUpdateTask(
				CommonTaskName.DELETE_DATABASE_TASK_NAME, serverInfo,
				CommonSendMsg.getDeletedbSendMsg());
		deleteDbTask.setDbName(dbName);
		deleteDbTask.setDelbackup(YesNoType.Y);
		taskGroup.addTask(deleteDbTask);
	}

	/**
	 * 
	 * Fill in the tasks of creating distributor to executor
	 * 
	 * @param master MasterNode
	 * @param slave SlaveNode
	 * @param dist DistributorNode
	 * @param executor CreateReplicationJobExecutor
	 * @return null or error message
	 */
	public static String createDistributorTaskGroup(MasterNode master,
			SlaveNode slave, DistributorNode dist,
			CreateReplicationJobExecutor executor) {
		TaskGroup taskGroup = new TaskGroup(
				IConstants.REPL_GROUP_NAME_CREATE_DIST);
		taskGroup.setTarget(dist.getDbName());
		HostNode distdbHost = (HostNode) dist.getParent();
		HostNode slaveHost = (HostNode) slave.getParent();
		final String ip = distdbHost.getIp();
		final String port = distdbHost.getPort();
		final String userName = distdbHost.getUserName();
		final String password = distdbHost.getPassword();
		boolean isConnected = ServerManager.getInstance().isConnected(ip,
				Integer.parseInt(port), userName);
		ServerInfo serverInfo = new ServerInfo();
		if (isConnected) {
			serverInfo = ServerManager.getInstance().getServer(ip,
					Integer.parseInt(port), userName);
			if (!serverInfo.getLoginedUserInfo().isAdmin()) {
				return Messages.bind(Messages.errInvalidUser, ip);
			}
		} else {
			serverInfo.setHostAddress(ip);
			serverInfo.setHostMonPort(Integer.parseInt(port));
			serverInfo.setHostJSPort(Integer.parseInt(port) + 1);
			serverInfo.setUserName(userName);
			serverInfo.setUserPassword(password);

			MonitoringTask monitoringTask = new MonitoringTask(serverInfo);
			taskGroup.addTask(monitoringTask);
		}

		addCheckDbTaskGroup(serverInfo, dist.getDbName(), taskGroup);

		CreateDistributorTask createDistributorTask = new CreateDistributorTask(
				serverInfo);
		createDistributorTask.setDistDbName(dist.getDbName());
		createDistributorTask.setDistDbPath(dist.getDbPath());
		createDistributorTask.setDbaPassword(dist.getDbaPassword());
		createDistributorTask.setReplAgentPort(dist.getReplAgentPort());
		createDistributorTask.setMasterDbName(slave.getDbName());
		createDistributorTask.setMasterDbIp(slaveHost.getIp());
		createDistributorTask.setReplServerPort(master.getReplServerPort());
		createDistributorTask.setCopyLogPath(dist.getCopyLogPath());
		createDistributorTask.setErrorLogPath(dist.getErrorLogPath());
		createDistributorTask.setTrailLogPath(dist.getTrailLogPath());
		createDistributorTask.setDelayTimeLogSize(dist.getDelayTimeLogSize());
		createDistributorTask.setRestartRepl(dist.isRestartWhenError());
		taskGroup.addTask(createDistributorTask);

		executor.addGroupTask(taskGroup);

		return null;
	}

	/**
	 * 
	 * Create the related task of creating slave database to executor
	 * 
	 * @param masterDb MasterNode
	 * @param distDb DistributorNode
	 * @param slaveDb SlaveNode
	 * @param executor CreateReplicationJobExecutor
	 * @return null or error message
	 */
	public static String createSlaveTaskGroup(MasterNode masterDb,
			DistributorNode distDb, SlaveNode slaveDb,
			CreateReplicationJobExecutor executor) {
		TaskGroup taskGroup = new TaskGroup(
				IConstants.REPL_GROUP_NAME_CREATE_SLAVE);
		taskGroup.setTarget(slaveDb.getDbName());
		HostNode slaveHost = (HostNode) slaveDb.getParent();
		final String ip = slaveHost.getIp();
		final String port = slaveHost.getPort();
		final String userName = slaveHost.getUserName();
		final String password = slaveHost.getPassword();
		boolean isConnected = ServerManager.getInstance().isConnected(ip,
				Integer.parseInt(port), userName);
		ServerInfo serverInfo = new ServerInfo();
		if (isConnected) {
			serverInfo = ServerManager.getInstance().getServer(ip,
					Integer.parseInt(port), userName);
			if (!serverInfo.getLoginedUserInfo().isAdmin()) {
				return Messages.bind(Messages.errInvalidUser, ip);
			}
		} else {
			serverInfo.setHostAddress(ip);
			serverInfo.setHostMonPort(Integer.parseInt(port));
			serverInfo.setHostJSPort(Integer.parseInt(port) + 1);
			serverInfo.setUserName(userName);
			serverInfo.setUserPassword(password);

			MonitoringTask monitoringTask = new MonitoringTask(serverInfo);
			taskGroup.addTask(monitoringTask);
		}
		addCheckDbTaskGroup(serverInfo, slaveDb.getDbName(), taskGroup);

		CreateSlaveDbTask createSlaveDbTask = new CreateSlaveDbTask(serverInfo);
		createSlaveDbTask.setSlaveDbName(slaveDb.getDbName());
		createSlaveDbTask.setSlaveDbPath(slaveDb.getDbPath());
		createSlaveDbTask.setSlaveDbUser(slaveDb.getDbUser());
		createSlaveDbTask.setSlaveDbPassword(slaveDb.getDbPassword());
		createSlaveDbTask.setMasterDbName(masterDb.getDbName());
		createSlaveDbTask.setMasterDbPassword(masterDb.getDbaPassword());
		createSlaveDbTask.setDistDbName(distDb.getDbName());
		createSlaveDbTask.setDistDbaPassword(distDb.getDbaPassword());
		taskGroup.addTask(createSlaveDbTask);

		SetReplicationParamTask setReplicationParamTask = new SetReplicationParamTask(
				serverInfo);
		setReplicationParamTask.setMasterDbName(masterDb.getDbName());
		setReplicationParamTask.setSlaveDbName(slaveDb.getDbName());
		setReplicationParamTask.setDistDbName(distDb.getDbName());
		setReplicationParamTask.setDistDbDbaPasswd(distDb.getDbaPassword());
		setReplicationParamTask.setParameterMap(slaveDb.getParamMap());
		setReplicationParamTask.setRunningMode(true);
		taskGroup.addTask(setReplicationParamTask);

		ChangeReplTablesTask changeReplTablesTask = new ChangeReplTablesTask(
				serverInfo);
		changeReplTablesTask.setDistdbName(distDb.getDbName());
		changeReplTablesTask.setDistdbPassword(distDb.getDbaPassword());
		changeReplTablesTask.setMdbName(masterDb.getDbName());
		changeReplTablesTask.setMdbUserId("dba");
		changeReplTablesTask.setMdbPass(masterDb.getDbaPassword());
		if (masterDb.isReplicateAll()) {
			changeReplTablesTask.setReplAllClasses(masterDb.isReplicateAll());
		} else if (masterDb.getReplicatedClassList() != null
				&& masterDb.getReplicatedClassList().size() > 0) {
			changeReplTablesTask.setReplicatedClasses(masterDb.getReplicatedClassList());
		} else {
			changeReplTablesTask.setReplNoneClasses(true);
		}
		taskGroup.addTask(changeReplTablesTask);

		executor.addGroupTask(taskGroup);
		return null;
	}

	/**
	 * 
	 * Fill in the related tasks of replication server to executor
	 * 
	 * @param master MasterNode
	 * @param executor CreateReplicationJobExecutor
	 * @return null or error message
	 */
	public static String createReplServerTaskGroup(MasterNode master,
			CreateReplicationJobExecutor executor) {
		TaskGroup taskGroup = new TaskGroup(
				IConstants.REPL_GROUP_NAME_START_REPL_SERVER);
		taskGroup.setTarget(master.getDbName());
		HostNode masterHost = (HostNode) master.getParent();
		final String ip = masterHost.getIp();
		final String port = masterHost.getPort();
		final String userName = masterHost.getUserName();
		final String password = masterHost.getPassword();
		boolean isConnected = ServerManager.getInstance().isConnected(ip,
				Integer.parseInt(port), userName);
		ServerInfo serverInfo = new ServerInfo();
		if (isConnected) {
			serverInfo = ServerManager.getInstance().getServer(ip,
					Integer.parseInt(port), userName);
			if (!serverInfo.getLoginedUserInfo().isAdmin()) {
				return Messages.bind(Messages.errInvalidUser, ip);
			}
		} else {
			serverInfo.setHostAddress(ip);
			serverInfo.setHostMonPort(Integer.parseInt(port));
			serverInfo.setHostJSPort(Integer.parseInt(port) + 1);
			serverInfo.setUserName(userName);
			serverInfo.setUserPassword(password);

			MonitoringTask monitoringTask = new MonitoringTask(serverInfo);
			taskGroup.addTask(monitoringTask);
		}

		StartReplServerTask startReplServerTask = new StartReplServerTask(
				serverInfo);
		startReplServerTask.setDbName(master.getDbName());
		startReplServerTask.setServerPort(master.getReplServerPort());
		taskGroup.addTask(startReplServerTask);

		executor.addGroupTask(taskGroup);

		return null;
	}

	/**
	 * 
	 * Fill in the related tasks of agent to executor
	 * 
	 * @param dist DistributorNode
	 * @param executor CreateReplicationJobExecutor
	 * @return null or error message
	 */
	public static String createAgentTaskGroup(DistributorNode dist,
			CreateReplicationJobExecutor executor) {
		TaskGroup taskGroup = new TaskGroup(
				IConstants.REPL_GROUP_NAME_START_AGENT);
		taskGroup.setTarget(dist.getDbName());
		HostNode distdbHost = (HostNode) dist.getParent();
		final String ip = distdbHost.getIp();
		final String port = distdbHost.getPort();
		final String userName = distdbHost.getUserName();
		final String password = distdbHost.getPassword();
		boolean isConnected = ServerManager.getInstance().isConnected(ip,
				Integer.parseInt(port), userName);
		ServerInfo serverInfo = new ServerInfo();
		if (isConnected) {
			serverInfo = ServerManager.getInstance().getServer(ip,
					Integer.parseInt(port), userName);
			if (!serverInfo.getLoginedUserInfo().isAdmin()) {
				return Messages.bind(Messages.errInvalidUser, ip);
			}
		} else {
			serverInfo.setHostAddress(ip);
			serverInfo.setHostMonPort(Integer.parseInt(port));
			serverInfo.setHostJSPort(Integer.parseInt(port) + 1);
			serverInfo.setUserName(userName);
			serverInfo.setUserPassword(password);

			MonitoringTask monitoringTask = new MonitoringTask(serverInfo);
			taskGroup.addTask(monitoringTask);
		}

		GetReplAgentStatusTask getReplAgentStatusTask = new GetReplAgentStatusTask(
				serverInfo);
		getReplAgentStatusTask.setDbName(dist.getDbName());
		taskGroup.addTask(getReplAgentStatusTask);

		StopReplicationAgentTask stopReplicationAgentTask = new StopReplicationAgentTask(
				serverInfo);
		stopReplicationAgentTask.setDbName(dist.getDbName());
		taskGroup.addTask(stopReplicationAgentTask);

		StartReplicationAgentTask startReplicationAgentTask = new StartReplicationAgentTask(
				serverInfo);
		startReplicationAgentTask.setDbName(dist.getDbName());
		startReplicationAgentTask.setDbaPasswd(dist.getDbaPassword());
		taskGroup.addTask(startReplicationAgentTask);

		executor.addGroupTask(taskGroup);

		return null;
	}

	/**
	 * 
	 * Get all slave of this master
	 * 
	 * @param masterNode MasterNode
	 * @param replicationsList List<List<Node>>
	 * @return null or error message
	 */
	public static List<SlaveNode> getAllSlaveOfMaster(MasterNode masterNode,
			List<List<Node>> replicationsList) {
		List<SlaveNode> slaveList = new ArrayList<SlaveNode>();
		for (int j = 0; j < replicationsList.size(); j++) {
			List<Node> replicationList = replicationsList.get(j);
			if (replicationList.size() < 3
					|| replicationList.get(0) != masterNode) {
				continue;
			}
			for (int i = 0; i < replicationList.size(); i++) {
				Node node = replicationList.get(i);
				if (node instanceof SlaveNode) {
					slaveList.add((SlaveNode) node);
				}
			}
		}
		return slaveList;
	}
}
