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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.progress.JobFamily;
import com.cubrid.common.ui.spi.progress.TaskGroup;
import com.cubrid.common.ui.spi.progress.TaskJobExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.task.CommonTaskName;
import com.cubrid.cubridmanager.core.common.task.CommonUpdateTask;
import com.cubrid.cubridmanager.core.common.task.MonitoringTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.database.task.BackupDbTask;
import com.cubrid.cubridmanager.core.cubrid.database.task.GetDatabaseListTask;
import com.cubrid.cubridmanager.core.replication.model.ReplicationInfo;
import com.cubrid.cubridmanager.core.replication.model.TransFileProgressInfo;
import com.cubrid.cubridmanager.core.replication.task.CancelTransFileTask;
import com.cubrid.cubridmanager.core.replication.task.ChangeReplTablesTask;
import com.cubrid.cubridmanager.core.replication.task.CreateSlaveDbTask;
import com.cubrid.cubridmanager.core.replication.task.GetReplAgentStatusTask;
import com.cubrid.cubridmanager.core.replication.task.GetTransferProgressTask;
import com.cubrid.cubridmanager.core.replication.task.SetReplicationParamTask;
import com.cubrid.cubridmanager.core.replication.task.StopReplicationAgentTask;
import com.cubrid.cubridmanager.core.replication.task.TransFileTask;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.mondashboard.preference.MonitorDashboardPreference;
import com.cubrid.cubridmanager.ui.replication.Messages;
import com.cubrid.cubridmanager.ui.replication.editor.CreateReplicationUtil;
import com.cubrid.cubridmanager.ui.spi.Version;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;

/**
 * 
 * Change slave database information wizard
 * 
 * @author wuyingshi
 * @version 1.0 - 2009-9-17 created by wuyingshi
 */
public class ChangeSlaveDbWizard extends
		Wizard {
	private SlaveDbInfoPage slaveDbInfoPage = null;
	private SelectTablesPage selectTablesPage = null;
	private SetReplicationParamPage setReplicationParamPage = null;
	private final CubridDatabase database;
	private ReplicationInfo replInfo = null;
	private static final String REPL_GROUP_NAME_CREATE_SLAVE = "REPL_GROUP_NAME_CREATE_SLAVE";
	private static final String REPL_GROUP_NAME_TRANSFER = "REPL_GROUP_NAME_TRANSFER";
	private WizardDialog dialog = null;

	/**
	 * The constructor
	 */
	public ChangeSlaveDbWizard(CubridDatabase database) {
		setWindowTitle(Messages.chsldbTitleChangeSlaveDbDialog);
		this.database = database;
	}

	/**
	 * Add wizard pages
	 */
	public void addPages() {
		slaveDbInfoPage = new SlaveDbInfoPage(database);
		addPage(slaveDbInfoPage);
		setReplicationParamPage = new SetReplicationParamPage();
		addPage(setReplicationParamPage);
		selectTablesPage = new SelectTablesPage();
		addPage(selectTablesPage);
		dialog = (WizardDialog) getContainer();
		dialog.addPageChangedListener(selectTablesPage);
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#canFinish()
	 * @return boolean
	 */
	public boolean canFinish() {
		return getContainer().getCurrentPage() == selectTablesPage
				&& selectTablesPage.isPageComplete();
	}

	/**
	 * Called when user clicks Finish
	 * 
	 * @return boolean
	 */
	public boolean performFinish() {
		setReplInfo((ReplicationInfo) database.getAdapter(ReplicationInfo.class));

		CreateReplicationSlaveDbJobExecutor executor = new CreateReplicationSlaveDbJobExecutor();
		boolean isValid = createTransferTaskGroup(executor);
		if (!isValid) {
			return false;
		}
		isValid = createSlaveTaskGroup(executor);
		if (!isValid) {
			return false;
		}
		JobFamily jobFamily = new JobFamily();
		jobFamily.setServerName(database.getServer().getLabel());
		jobFamily.setDbName(database.getLabel());
		executor.schedule(Messages.chsldbTitleChangeSlaveDbDialog, jobFamily,
				true, Job.SHORT);
		return false;
	}

	/**
	 * create task group of transfer master backup file to slave
	 * 
	 * @param executor CreateReplicationSlaveDbJobExecutor
	 * @return boolean
	 */
	public boolean createTransferTaskGroup(
			CreateReplicationSlaveDbJobExecutor executor) {

		final String ip = slaveDbInfoPage.getMasterHostIp();
		final String port = slaveDbInfoPage.getMasterHostPort();
		final String userName = "admin";
		final String password = slaveDbInfoPage.getMasterHostPassword();

		boolean isConnected = ServerManager.getInstance().isConnected(ip,
				Integer.parseInt(port), userName);
		ServerInfo masterServerInfo = new ServerInfo();
		String mdbName = slaveDbInfoPage.getMasterDbName();
		TaskGroup taskGroup = new TaskGroup(REPL_GROUP_NAME_TRANSFER);
		taskGroup.setTarget(mdbName);

		if (isConnected) {
			masterServerInfo = CMHostNodePersistManager.getInstance().getServerInfo(ip,
					Integer.parseInt(port), userName);
		} else {
			masterServerInfo.setHostAddress(ip);
			masterServerInfo.setHostMonPort(Integer.parseInt(port));
			masterServerInfo.setHostJSPort(Integer.parseInt(port) + 1);
			masterServerInfo.setUserName(userName);
			masterServerInfo.setUserPassword(password);

			MonitoringTask monitoringTask = new MonitoringTask(masterServerInfo);
			taskGroup.addTask(monitoringTask);
		}

		GetDatabaseListTask getDatabaseListTask = new GetDatabaseListTask(
				masterServerInfo);
		getDatabaseListTask.putData("dbName", mdbName);
		taskGroup.addTask(getDatabaseListTask);

		BackupDbTask backupDbTask = new BackupDbTask(masterServerInfo);
		backupDbTask.setDbName(mdbName);
		backupDbTask.setLevel("0");
		backupDbTask.setVolumeName(mdbName + "_bk0v000");
		backupDbTask.setRemoveLog(false);
		backupDbTask.setCheckDatabaseConsist(true);
		backupDbTask.setThreadCount(String.valueOf(0));
		backupDbTask.setZiped(false);
		backupDbTask.setSafeReplication(false);
		taskGroup.addTask(backupDbTask);

		CancelTransFileTask cancelTransFileTask = new CancelTransFileTask(
				masterServerInfo);
		taskGroup.addTask(cancelTransFileTask);

		for (int i = 0; i < replInfo.getSlaveList().size(); i++) {
			TransFileTask transFileTask = new TransFileTask(masterServerInfo);
			List<String> fileList = new ArrayList<String>();
			fileList.add(slaveDbInfoPage.getMasterDbName() + "_bk0v000");
			fileList.add(slaveDbInfoPage.getMasterDbName() + "_bkvinf");
			transFileTask.setBackupFileList(fileList);
			transFileTask.setSlaveDbHost(database.getServer().getServerInfo().getHostAddress());
			transFileTask.setSlaveCmServerPort(String.valueOf(database.getServer().getServerInfo().getHostMonPort() + 1));
			transFileTask.setSlaveDbDir(slaveDbInfoPage.getSlaveDbPath());
			taskGroup.addTask(transFileTask);

			GetTransferProgressTask getTransferProgressTask = new GetTransferProgressTask(
					masterServerInfo);
			taskGroup.addTask(getTransferProgressTask);
		}
		executor.addGroupTask(taskGroup);
		return true;
	}

	/**
	 * create slave database task group
	 * 
	 * @param executor CreateReplicationSlaveDbJobExecutor
	 * @return boolean
	 */
	public boolean createSlaveTaskGroup(
			CreateReplicationSlaveDbJobExecutor executor) {
		TaskGroup taskGroup = new TaskGroup(REPL_GROUP_NAME_CREATE_SLAVE);
		taskGroup.setTarget(slaveDbInfoPage.getSlaveDbName());

		CreateReplicationUtil.addCheckDbTaskGroup(
				database.getServer().getServerInfo(),
				slaveDbInfoPage.getSlaveDbName(), taskGroup);

		GetReplAgentStatusTask getReplAgentStatusTask = new GetReplAgentStatusTask(
				database.getServer().getServerInfo());
		getReplAgentStatusTask.setDbName(database.getLabel());
		taskGroup.addTask(getReplAgentStatusTask);
		StopReplicationAgentTask stopReplicationAgentTask = new StopReplicationAgentTask(
				database.getServer().getServerInfo());
		stopReplicationAgentTask.setDbName(database.getLabel());
		taskGroup.addTask(stopReplicationAgentTask);

		CreateSlaveDbTask createSlaveDbTask = new CreateSlaveDbTask(
				database.getServer().getServerInfo());
		createSlaveDbTask.setSlaveDbName(slaveDbInfoPage.getSlaveDbName());
		createSlaveDbTask.setSlaveDbPath(slaveDbInfoPage.getSlaveDbPath());
		createSlaveDbTask.setSlaveDbUser(slaveDbInfoPage.getSlaveDbUser());
		createSlaveDbTask.setSlaveDbPassword(slaveDbInfoPage.getSlaveDbaPassword());
		createSlaveDbTask.setMasterDbName(slaveDbInfoPage.getMasterDbName());
		createSlaveDbTask.setMasterDbPassword(slaveDbInfoPage.getMasterDbDbaPassword());
		createSlaveDbTask.setDistDbName(database.getLabel());
		createSlaveDbTask.setDistDbaPassword(database.getPassword());
		taskGroup.addTask(createSlaveDbTask);

		SetReplicationParamTask setReplicationParamTask = new SetReplicationParamTask(
				database.getServer().getServerInfo());
		setReplicationParamTask.setMasterDbName(slaveDbInfoPage.getMasterDbName());
		setReplicationParamTask.setSlaveDbName(slaveDbInfoPage.getSlaveDbName());
		setReplicationParamTask.setDistDbName(database.getLabel());
		setReplicationParamTask.setDistDbDbaPasswd(database.getPassword());
		setReplicationParamTask.setParameterMap(setReplicationParamPage.getParamMap());
		setReplicationParamTask.setRunningMode(true);
		taskGroup.addTask(setReplicationParamTask);

		ChangeReplTablesTask changeReplTablesTask = new ChangeReplTablesTask(
				database.getServer().getServerInfo());
		changeReplTablesTask.setDistdbName(database.getLabel());
		changeReplTablesTask.setDistdbPassword(database.getPassword());
		changeReplTablesTask.setMdbName(slaveDbInfoPage.getMasterDbName());
		changeReplTablesTask.setMdbUserId("dba");
		changeReplTablesTask.setMdbPass(slaveDbInfoPage.getMasterDbDbaPassword());
		if (selectTablesPage.isReplAllTables()) {
			changeReplTablesTask.setReplAllClasses(selectTablesPage.isReplAllTables());
		} else if (selectTablesPage.getReplTableList() != null
				&& selectTablesPage.getReplTableList().size() > 0) {
			changeReplTablesTask.setReplicatedClasses(selectTablesPage.getReplTableList());
		} else {
			changeReplTablesTask.setReplNoneClasses(true);
		}
		taskGroup.addTask(changeReplTablesTask);
		executor.addGroupTask(taskGroup);
		return true;
	}

	/**
	 * @return the replInfo
	 */
	public ReplicationInfo getReplInfo() {
		return replInfo;
	}

	/**
	 * @param replInfo the replInfo to set
	 */
	public void setReplInfo(ReplicationInfo replInfo) {
		this.replInfo = replInfo;
	}

	/**
	 * A common type which extends the type TaakExecutor and overrides the
	 * method exec.Generally
	 */
	private class CreateReplicationSlaveDbJobExecutor extends
			TaskJobExecutor {

		protected List<TaskGroup> groupTaskList = new ArrayList<TaskGroup>();
		private boolean isDeleteDb = false;
		private String pid = null;
		private CancelTransFileTask cancelTransFileTask = null;

		/**
		 * @see com.cubrid.common.ui.spi.progress.TaskJobExecutor#exec(org.eclipse.core.runtime.IProgressMonitor)
		 * @param monitor IProgressMonitor
		 * @return Status
		 */
		public IStatus exec(final IProgressMonitor monitor) {
			setVisible(false);
			for (int i = 0; i < groupTaskList.size(); i++) {
				TaskGroup taskGroup = groupTaskList.get(i);
				String groupName = taskGroup.getGroupName();
				String target = (String) taskGroup.getTarget();
				List<ITask> taskList = taskGroup.getTaskList();
				IStatus status = Status.OK_STATUS;
				if (groupName != null
						&& groupName.equals(REPL_GROUP_NAME_TRANSFER)) {
					status = transfer(monitor, taskList);
				}
				if (groupName != null
						&& groupName.equals(REPL_GROUP_NAME_CREATE_SLAVE)) {
					status = createSlave(target, monitor, taskList);
				}
				if (monitor.isCanceled()) {
					closeUI();
					return Status.CANCEL_STATUS;
				}
				if (status != Status.OK_STATUS) {
					return status;
				}
			}
			return Status.OK_STATUS;
		}

		/**
		 * Notification that change slave database job has completed execution.
		 * 
		 * @param event the event details
		 */
		public void done(IJobChangeEvent event) {
			if (event.getResult() == Status.OK_STATUS) {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						CommonUITool.openInformationBox(
								Messages.repparm0titleSuccess,
								Messages.chsldbMsgChangeParamSuccess);
					}
				});
				closeUI();
			}
		}

		/**
		 * @see com.cubrid.common.ui.spi.progress.TaskJobExecutor#cancel()
		 */
		public void cancel() {
			if (pid != null && cancelTransFileTask != null) {
				cancelTransFileTask.setPid(pid);
				cancelTransFileTask.execute();
				pid = null;
				cancelTransFileTask = null;
			}
			super.cancel();
		}

		/**
		 * Set connected status of server to false
		 * 
		 * @param monitoringTask MonitoringTask
		 */
		private void disConnect(MonitoringTask monitoringTask) {
			if (monitoringTask != null) {
				ServerManager.getInstance().setConnected(
						monitoringTask.getServerInfo().getHostAddress(),
						monitoringTask.getServerInfo().getHostMonPort(),
						monitoringTask.getServerInfo().getUserName(), false);
			}
		}

		/**
		 * close the UI
		 * 
		 */
		public void closeUI() {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					dialog.close();
				}
			});
		}

		/**
		 * set dialog to visible
		 * 
		 * @param isVisible boolean
		 */
		public void setVisible(final boolean isVisible) {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					dialog.getShell().setVisible(isVisible);
				}
			});
		}

		/**
		 * transfer the master database backup file
		 * 
		 * @param monitor IProgressMonitor
		 * @param taskList List<ITask>
		 * @return status
		 */
		private IStatus transfer(final IProgressMonitor monitor,
				List<ITask> taskList) {
			monitor.subTask(Messages.transFileJobName);
			MonitoringTask monitoringTask = null;
			ServerInfo serverInfo = null;
			String mdbPath = null;
			
			MonitorDashboardPreference monPref = new MonitorDashboardPreference();
			
			for (ITask task : taskList) {
				if (task instanceof MonitoringTask) {
					monitoringTask = (MonitoringTask) task;
					serverInfo = monitoringTask.getServerInfo();
					CMHostNodePersistManager.getInstance().addServer(
							serverInfo.getHostAddress(),
							serverInfo.getHostMonPort(),
							serverInfo.getUserName(), serverInfo);
					monitoringTask.connectServer(Version.releaseVersion,
							monPref.getHAHeartBeatTimeout());
				} else if (task instanceof BackupDbTask) {
					BackupDbTask backupDbTask = (BackupDbTask) task;
					if (mdbPath != null) {
						backupDbTask.setBackupDir(mdbPath);
					}
					backupDbTask.execute();
				} else if (task instanceof TransFileTask) {
					TransFileTask transFileTask = (TransFileTask) task;
					if (mdbPath != null) {
						transFileTask.setMasterDbDir(mdbPath);
					}
					transFileTask.execute();
				} else if (task instanceof GetTransferProgressTask) {
					GetTransferProgressTask getTransferProgressTask = (GetTransferProgressTask) task;
					monitor.subTask(Messages.transFileJobName);
					if (pid != null) {
						while (!isCanceled()) {
							getTransferProgressTask.setPid(pid);
							getTransferProgressTask.execute();
							TransFileProgressInfo progressInfo = getTransferProgressTask.getProgressInfo();
							if (getTransferProgressTask.isSuccess()
									&& progressInfo != null) {
								String transferStatus = progressInfo.getTransferStatus();
								String transferNote = progressInfo.getTransferNote();
								if (transferStatus != null
										&& transferStatus.equals("success")) {
									pid = null;
									cancelTransFileTask = null;
									break;
								} else if (transferStatus != null
										&& transferStatus.equals("failure")) {
									pid = null;
									cancelTransFileTask = null;
									getTransferProgressTask.setErrorMsg(transferNote);
									break;
								} else {
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
									}
								}
							} else {
								break;
							}
						}
					}
				} else if (task instanceof CancelTransFileTask) {
					cancelTransFileTask = (CancelTransFileTask) task;
				} else {
					task.execute();
				}
				if (monitor.isCanceled()) {
					disConnect(monitoringTask);
					return Status.CANCEL_STATUS;
				}
				final String msg = task.getErrorMsg();
				if (msg != null && msg.length() > 0 && !monitor.isCanceled()) {
					disConnect(monitoringTask);
					return new Status(IStatus.ERROR,
							CubridManagerUIPlugin.PLUGIN_ID, msg);
				}
				if (task instanceof GetDatabaseListTask) {
					GetDatabaseListTask getDatabaseListTask = (GetDatabaseListTask) task;
					String mdbName = (String) getDatabaseListTask.getData("dbName");
					List<DatabaseInfo> dbInfoList = getDatabaseListTask.loadDatabaseInfo();
					for (int i = 0; i < dbInfoList.size(); i++) {
						DatabaseInfo dbInfo = dbInfoList.get(i);
						if (mdbName.equalsIgnoreCase(dbInfo.getDbName())) {
							mdbPath = dbInfo.getDbDir();
						}
					}
				} else if (task instanceof TransFileTask) {
					TransFileTask transFileTask = (TransFileTask) task;
					pid = transFileTask.getTransFilePid();
				}
			}
			disConnect(monitoringTask);
			return Status.OK_STATUS;
		}

		/**
		 * create the slave database.
		 * 
		 * @param dbName String
		 * @param monitor IProgressMonitor
		 * @param taskList List<ITask>
		 * @return status
		 */
		private IStatus createSlave(String dbName,
				final IProgressMonitor monitor, List<ITask> taskList) {
			monitor.subTask(Messages.bind(Messages.createSlaveJobName, dbName));
			Display display = Display.getDefault();
			MonitoringTask monitoringTask = null;
			boolean isExecuteStopDbTask = true;
			boolean isExecuteStopAgentTask = true;
			
			MonitorDashboardPreference monPref = new MonitorDashboardPreference();
			
			for (ITask task : taskList) {
				if (task instanceof MonitoringTask) {
					monitoringTask = (MonitoringTask) task;
					ServerInfo serverInfo = monitoringTask.getServerInfo();
					CMHostNodePersistManager.getInstance().addServer(
							serverInfo.getHostAddress(),
							serverInfo.getHostMonPort(),
							serverInfo.getUserName(), serverInfo);
					monitoringTask.connectServer(Version.releaseVersion,
							monPref.getHAHeartBeatTimeout());
				} else if (task instanceof CommonUpdateTask
						&& task.getTaskname().equals(
								CommonTaskName.STOP_DB_TASK_NAME)) {
					if (isDeleteDb && isExecuteStopDbTask) {
						task.execute();
					} else {
						continue;
					}
				} else if (task instanceof StopReplicationAgentTask) {
					if (isExecuteStopAgentTask) {
						task.execute();
					} else {
						continue;
					}
				} else if (task instanceof GetReplAgentStatusTask) {
					GetReplAgentStatusTask getReplAgentStatusTask = (GetReplAgentStatusTask) task;
					getReplAgentStatusTask.execute();
					isExecuteStopAgentTask = getReplAgentStatusTask.isActive();
				} else if (task instanceof CommonUpdateTask
						&& task.getTaskname().equals(
								CommonTaskName.DELETE_DATABASE_TASK_NAME)) {
					if (isDeleteDb) {
						task.execute();
					} else {
						continue;
					}
				} else {
					task.execute();
				}
				if (monitor.isCanceled()) {
					disConnect(monitoringTask);
					return Status.CANCEL_STATUS;
				}
				final String msg = task.getErrorMsg();
				if (msg != null && msg.length() > 0 && !monitor.isCanceled()) {
					display.syncExec(new Runnable() {
						public void run() {
							setVisible(true);
						}
					});
					disConnect(monitoringTask);
					return new Status(IStatus.ERROR,
							CubridManagerUIPlugin.PLUGIN_ID, msg);
				}
				if (task instanceof GetDatabaseListTask) {
					GetDatabaseListTask getDatabaseListTask = (GetDatabaseListTask) task;
					final String sdbName = (String) getDatabaseListTask.getData("dbName");
					List<DatabaseInfo> dbInfoList = getDatabaseListTask.loadDatabaseInfo();
					boolean isDbExist = false;
					for (int i = 0; i < dbInfoList.size(); i++) {
						DatabaseInfo dbInfo = dbInfoList.get(i);
						if (dbName.equalsIgnoreCase(dbInfo.getDbName())) {
							isDbExist = true;
							display.syncExec(new Runnable() {
								public void run() {
									isDeleteDb = CommonUITool.openConfirmBox(Messages.bind(
											Messages.msgConfirmDeleteDb,
											sdbName));
								}
							});
							if (dbInfo.getRunningType() == DbRunningType.CS) {
								isExecuteStopDbTask = true;
							}
							break;
						}
					}
					if (isDbExist && !isDeleteDb) {
						disConnect(monitoringTask);
						return Status.CANCEL_STATUS;
					}
				}
			}
			disConnect(monitoringTask);
			isDeleteDb = false;
			return Status.OK_STATUS;
		}

		/**
		 * add taskGroup to groupTaskList & add task in taskGroup to taskList.
		 * 
		 * @param taskGroup TaskGroup
		 */
		public void addGroupTask(TaskGroup taskGroup) {
			if (taskGroup == null) {
				return;
			}
			groupTaskList.add(taskGroup);
			List<ITask> taskList = taskGroup.getTaskList();
			for (int i = 0; i < taskList.size(); i++) {
				super.addTask(taskList.get(i));
			}
		}
	}
}
