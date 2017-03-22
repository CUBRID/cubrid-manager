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
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.spi.progress.TaskGroup;
import com.cubrid.common.ui.spi.progress.TaskJobExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.model.ConfConstants;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.task.CommonTaskName;
import com.cubrid.cubridmanager.core.common.task.CommonUpdateTask;
import com.cubrid.cubridmanager.core.common.task.GetCubridConfParameterTask;
import com.cubrid.cubridmanager.core.common.task.MonitoringTask;
import com.cubrid.cubridmanager.core.common.task.SetCubridConfParameterTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.database.task.GetDatabaseListTask;
import com.cubrid.cubridmanager.core.replication.model.TransFileProgressInfo;
import com.cubrid.cubridmanager.core.replication.task.CancelTransFileTask;
import com.cubrid.cubridmanager.core.replication.task.GetReplAgentStatusTask;
import com.cubrid.cubridmanager.core.replication.task.GetReplServerStatusTask;
import com.cubrid.cubridmanager.core.replication.task.GetTransferProgressTask;
import com.cubrid.cubridmanager.core.replication.task.StopReplServerTask;
import com.cubrid.cubridmanager.core.replication.task.StopReplicationAgentTask;
import com.cubrid.cubridmanager.core.replication.task.TransFileTask;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.mondashboard.preference.MonitorDashboardPreference;
import com.cubrid.cubridmanager.ui.replication.Messages;
import com.cubrid.cubridmanager.ui.spi.Version;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;

/**
 * 
 * This job executor is responsible to create replication in a thread
 * 
 * @author pangqiren
 * @version 1.0 - 2009-11-24 created by pangqiren
 */
public class CreateReplicationJobExecutor extends
		TaskJobExecutor {

	protected List<TaskGroup> groupTaskList = new ArrayList<TaskGroup>();
	private final Shell shell;
	private final ReplicationEditor editor;
	private boolean isDeleteDb = false;
	private boolean isStartReplServer = false;
	private boolean isStartAgent = false;
	//the pid of transport file
	private String pid = null;
	private CancelTransFileTask cancelTransFileTask = null;
	private boolean isExecuteStopReplServerTask = false;

	/**
	 * The constructor
	 * 
	 * @param editor
	 */
	public CreateReplicationJobExecutor(ReplicationEditor editor) {
		this.editor = editor;
		this.shell = editor.getSite().getShell();
	}

	/**
	 * @see com.cubrid.common.ui.spi.progress.TaskJobExecutor#exec(org.eclipse.core.runtime.IProgressMonitor)
	 * @param monitor the monitor object
	 * @return the status
	 */
	public IStatus exec(final IProgressMonitor monitor) {
		List<TaskGroup> startReplSvrTaskGrpList = new ArrayList<TaskGroup>();
		List<TaskGroup> startAgentTaskGrpList = new ArrayList<TaskGroup>();
		for (int i = 0; i < groupTaskList.size(); i++) {
			TaskGroup taskGroup = groupTaskList.get(i);
			String groupName = taskGroup.getGroupName();
			String target = (String) taskGroup.getTarget();
			List<ITask> taskList = taskGroup.getTaskList();
			IStatus status = Status.OK_STATUS;
			if (IConstants.REPL_GROUP_NAME_CREATE_MASTER.equals(groupName)) {
				status = createMaster(target, monitor, taskList);
			} else if (IConstants.REPL_GROUP_NAME_CREATE_DIST.equals(groupName)) {
				status = createDistributor(target, monitor, taskList);
			} else if (IConstants.REPL_GROUP_NAME_CREATE_SLAVE.equals(groupName)) {
				status = createSlave(target, monitor, taskList);
			} else if (IConstants.REPL_GROUP_NAME_START_REPL_SERVER.equals(groupName)) {
				startReplSvrTaskGrpList.add(taskGroup);
			} else if (IConstants.REPL_GROUP_NAME_START_AGENT.equals(groupName)) {
				startAgentTaskGrpList.add(taskGroup);
			}
			if (monitor.isCanceled()) {
				setEditorDirty();
				return Status.CANCEL_STATUS;
			}
			if (status != Status.OK_STATUS) {
				setEditorDirty();
				return status;
			}
		}
		Display display = Display.getDefault();
		if (!startReplSvrTaskGrpList.isEmpty()) {
			for (TaskGroup startReplServerTaskGrp : startReplSvrTaskGrpList) {
				final String target = (String) startReplServerTaskGrp.getTarget();
				display.syncExec(new Runnable() {
					public void run() {
						isStartReplServer = CommonUITool.openConfirmBox(shell,
								Messages.bind(
										Messages.msgConfirmStartReplServer,
										target));
					}
				});
				if (!isStartReplServer) {
					continue;
				}
				List<ITask> taskList = startReplServerTaskGrp.getTaskList();
				IStatus status = startReplServer(target, monitor, taskList);
				if (monitor.isCanceled()) {
					setEditorDirty();
					return Status.CANCEL_STATUS;
				}
				if (status != Status.OK_STATUS) {
					setEditorDirty();
					return status;
				}
			}

		}
		if (!startAgentTaskGrpList.isEmpty()) {
			for (TaskGroup startAgentTaskGrp : startAgentTaskGrpList) {
				final String target = (String) startAgentTaskGrp.getTarget();
				display.syncExec(new Runnable() {
					public void run() {
						isStartAgent = CommonUITool.openConfirmBox(shell,
								Messages.bind(Messages.msgConfirmStartAgent,
										target));
					}
				});
				if (!isStartAgent) {
					continue;
				}
				List<ITask> taskList = startAgentTaskGrp.getTaskList();
				IStatus status = startAgent(target, monitor, taskList);
				if (monitor.isCanceled()) {
					setEditorDirty();
					return Status.CANCEL_STATUS;
				}
				if (status != Status.OK_STATUS) {
					setEditorDirty();
					return status;
				}
			}

		}
		return Status.OK_STATUS;
	}

	/**
	 * Notification that the create replication job has completed execution.
	 * 
	 * @param event the event details
	 */
	public void done(IJobChangeEvent event) {
		if (event.getResult() == Status.OK_STATUS) {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					CommonUITool.openInformationBox(
							com.cubrid.cubridmanager.ui.common.Messages.titleSuccess,
							Messages.msgCreateReplicationSuccess);
				}
			});
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
	 * create master database
	 * 
	 * @param dbName String
	 * @param monitor IProgressMonitor
	 * @param taskList List<ITask>
	 * @return status
	 */
	private IStatus createMaster(final String dbName,
			final IProgressMonitor monitor, List<ITask> taskList) {
		monitor.subTask(Messages.bind(Messages.createMasterJobName, dbName));
		Display display = Display.getDefault();
		MonitoringTask monitoringTask = null;
		boolean isExecuteSetParaTask = true;
		boolean isExecuteStopDbTask = true;
		boolean isExecuteStartDbTask = false;
		Map<String, Map<String, String>> confParaMaps = null;
		
		MonitorDashboardPreference monPref = new MonitorDashboardPreference();
		
		for (ITask task : taskList) {
			if (task instanceof MonitoringTask) {
				monitoringTask = (MonitoringTask) task;
				ServerInfo serverInfo = monitoringTask.getServerInfo();
				CMHostNodePersistManager.getInstance().addServer(
						serverInfo.getHostAddress(),
						serverInfo.getHostMonPort(), serverInfo.getUserName(),
						serverInfo);
				monitoringTask.connectServer(Version.releaseVersion,
						monPref.getHAHeartBeatTimeout());
			} else if (task instanceof StopReplServerTask) {
				if (isExecuteStopReplServerTask) {
					task.execute();
				} else {
					continue;
				}
			} else if (task instanceof SetCubridConfParameterTask) {
				if (isExecuteSetParaTask) {
					SetCubridConfParameterTask setCubridConfParameterTask = (SetCubridConfParameterTask) task;
					setCubridConfParameterTask.setConfParameters(confParaMaps);
					task.execute();
				} else {
					continue;
				}
			} else if (task instanceof CommonUpdateTask
					&& task.getTaskname().equals(
							CommonTaskName.STOP_DB_TASK_NAME)) {
				if (isExecuteStopDbTask) {
					task.execute();
				} else {
					continue;
				}
			} else if (task instanceof CommonUpdateTask
					&& task.getTaskname().equals(
							CommonTaskName.START_DB_TASK_NAME)) {
				if (isExecuteStartDbTask) {
					task.execute();
				} else {
					continue;
				}
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
									e.printStackTrace();
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
			if (task instanceof GetReplServerStatusTask) {
				GetReplServerStatusTask getReplServerStatusTask = (GetReplServerStatusTask) task;
				boolean isActive = getReplServerStatusTask.isActive();
				if (isActive) {
					display.syncExec(new Runnable() {
						public void run() {
							isExecuteStopReplServerTask = CommonUITool.openConfirmBox(Messages.bind(
									Messages.msgConfirmStopReplServer, dbName));
						}
					});
					if (!isExecuteStopReplServerTask) {
						disConnect(monitoringTask);
						return Status.CANCEL_STATUS;
					}
				}
			} else if (task instanceof GetCubridConfParameterTask) {
				GetCubridConfParameterTask getCubridConfParameterTask = (GetCubridConfParameterTask) task;
				confParaMaps = getCubridConfParameterTask.getConfParameters();
				String mdbName = (String) getCubridConfParameterTask.getData("dbName");
				Map<String, String> dbMap = confParaMaps.get("[@" + mdbName
						+ "]");
				if (dbMap == null) {
					Map<String, String> commonMap = confParaMaps.get(ConfConstants.COMMON_SECTION_NAME);
					if (commonMap != null
							&& commonMap.get(ConfConstants.REPLICATION) != null
							&& commonMap.get(ConfConstants.REPLICATION).equalsIgnoreCase(
									"yes")) {
						isExecuteSetParaTask = false;
					} else if (commonMap != null) {
						isExecuteSetParaTask = true;
						commonMap.put(ConfConstants.REPLICATION, "yes");
					}
				} else {
					String replStr = dbMap.get(ConfConstants.REPLICATION);
					if (replStr == null || replStr.equalsIgnoreCase("no")) {
						isExecuteSetParaTask = true;
						dbMap.put(ConfConstants.REPLICATION, "yes");
					} else {
						isExecuteSetParaTask = false;
					}
				}
			} else if (task instanceof GetDatabaseListTask) {
				GetDatabaseListTask getDatabaseListTask = (GetDatabaseListTask) task;
				String mdbName = (String) getDatabaseListTask.getData("dbName");
				List<DatabaseInfo> dbInfoList = getDatabaseListTask.loadDatabaseInfo();
				for (int i = 0; i < dbInfoList.size(); i++) {
					DatabaseInfo dbInfo = dbInfoList.get(i);
					if (mdbName.equalsIgnoreCase(dbInfo.getDbName())) {
						if (dbInfo.getRunningType() == DbRunningType.CS) {
							isExecuteStopDbTask = isExecuteSetParaTask;
						}
						isExecuteStartDbTask = dbInfo.getRunningType() == DbRunningType.STANDALONE
								|| isExecuteStopDbTask;
						break;
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
	 * create distributor database
	 * 
	 * @param dbName String
	 * @param monitor IProgressMonitor
	 * @param taskList List<ITask>
	 * @return status
	 */
	private IStatus createDistributor(String dbName,
			final IProgressMonitor monitor, List<ITask> taskList) {
		monitor.subTask(Messages.bind(Messages.createDistJobName, dbName));
		Display display = Display.getDefault();
		MonitoringTask monitoringTask = null;
		boolean isExecuteStopDbTask = true;

		MonitorDashboardPreference monPref = new MonitorDashboardPreference();
		
		for (ITask task : taskList) {
			if (task instanceof MonitoringTask) {
				monitoringTask = (MonitoringTask) task;
				ServerInfo serverInfo = monitoringTask.getServerInfo();
				CMHostNodePersistManager.getInstance().addServer(
						serverInfo.getHostAddress(),
						serverInfo.getHostMonPort(), serverInfo.getUserName(),
						serverInfo);
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
				disConnect(monitoringTask);
				return new Status(IStatus.ERROR,
						CubridManagerUIPlugin.PLUGIN_ID, msg);
			}
			if (task instanceof GetDatabaseListTask) {
				GetDatabaseListTask getDatabaseListTask = (GetDatabaseListTask) task;
				final String distdbName = (String) getDatabaseListTask.getData("dbName");
				List<DatabaseInfo> dbInfoList = getDatabaseListTask.loadDatabaseInfo();
				boolean isDbExist = false;
				for (int i = 0; i < dbInfoList.size(); i++) {
					DatabaseInfo dbInfo = dbInfoList.get(i);
					if (dbName.equalsIgnoreCase(dbInfo.getDbName())) {
						isDbExist = true;
						display.syncExec(new Runnable() {
							public void run() {
								isDeleteDb = CommonUITool.openConfirmBox(Messages.bind(
										Messages.msgConfirmDeleteDb, distdbName));
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
	 * create slave database
	 * 
	 * @param dbName String
	 * @param monitor IProgressMonitor
	 * @param taskList List<ITask>
	 * @return status
	 */
	private IStatus createSlave(String dbName, final IProgressMonitor monitor,
			List<ITask> taskList) {
		monitor.subTask(Messages.bind(Messages.createSlaveJobName, dbName));
		Display display = Display.getDefault();
		MonitoringTask monitoringTask = null;
		boolean isExecuteStopDbTask = true;
		
		MonitorDashboardPreference monPref = new MonitorDashboardPreference();
		
		for (ITask task : taskList) {
			if (task instanceof MonitoringTask) {
				monitoringTask = (MonitoringTask) task;
				ServerInfo serverInfo = monitoringTask.getServerInfo();
				CMHostNodePersistManager.getInstance().addServer(
						serverInfo.getHostAddress(),
						serverInfo.getHostMonPort(), serverInfo.getUserName(),
						serverInfo);
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
										Messages.msgConfirmDeleteDb, sdbName));
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
	 * start agent service
	 * 
	 * @param agentName String
	 * @param monitor IProgressMonitor
	 * @param taskList List<ITask>
	 * @return status
	 */
	private IStatus startAgent(String agentName,
			final IProgressMonitor monitor, List<ITask> taskList) {
		monitor.subTask(Messages.bind(Messages.startAgentJobName, agentName));
		MonitoringTask monitoringTask = null;
		boolean isExecuteStopAgentTask = true;
		
		MonitorDashboardPreference monPref = new MonitorDashboardPreference();
		
		for (ITask task : taskList) {
			if (task instanceof MonitoringTask) {
				monitoringTask = (MonitoringTask) task;
				ServerInfo serverInfo = monitoringTask.getServerInfo();
				CMHostNodePersistManager.getInstance().addServer(
						serverInfo.getHostAddress(),
						serverInfo.getHostMonPort(), serverInfo.getUserName(),
						serverInfo);
				monitoringTask.connectServer(Version.releaseVersion,
						monPref.getHAHeartBeatTimeout());
			} else if (task instanceof StopReplicationAgentTask) {
				if (isExecuteStopAgentTask) {
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
				disConnect(monitoringTask);
				return new Status(IStatus.ERROR,
						CubridManagerUIPlugin.PLUGIN_ID, msg);
			}
			if (task instanceof GetReplAgentStatusTask) {
				GetReplAgentStatusTask getReplAgentStatusTask = (GetReplAgentStatusTask) task;
				isExecuteStopAgentTask = getReplAgentStatusTask.isActive();
			}
		}
		disConnect(monitoringTask);
		return Status.OK_STATUS;
	}

	/**
	 * start replication server
	 * 
	 * @param serverName String
	 * @param monitor IProgressMonitor
	 * @param taskList List<ITask
	 * @return status
	 */
	private IStatus startReplServer(String serverName,
			final IProgressMonitor monitor, List<ITask> taskList) {
		monitor.subTask(Messages.bind(Messages.startReplServerJobName,
				serverName));
		MonitoringTask monitoringTask = null;
		
		MonitorDashboardPreference monPref = new MonitorDashboardPreference();
		
		for (ITask task : taskList) {
			if (task instanceof MonitoringTask) {
				monitoringTask = (MonitoringTask) task;
				ServerInfo serverInfo = monitoringTask.getServerInfo();
				CMHostNodePersistManager.getInstance().addServer(
						serverInfo.getHostAddress(),
						serverInfo.getHostMonPort(), serverInfo.getUserName(),
						serverInfo);
				monitoringTask.connectServer(Version.releaseVersion, 
						monPref.getHAHeartBeatTimeout());
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
		}
		disConnect(monitoringTask);
		return Status.OK_STATUS;
	}

	/**
	 * add taskGroup to groupTaskList & add task in taskGroup to taskList
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

	/**
	 * set the dirty editor.
	 * 
	 */
	private void setEditorDirty() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				if (!editor.isDisposed()) {
					editor.setEditable(true);
					editor.setDirty(true);
				}
			}
		});
	}
}
