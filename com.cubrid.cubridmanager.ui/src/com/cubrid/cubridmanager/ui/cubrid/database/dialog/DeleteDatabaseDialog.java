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
package com.cubrid.cubridmanager.ui.cubrid.database.dialog;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.spi.TableViewerSorter;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.common.task.CommonTaskName;
import com.cubrid.cubridmanager.core.common.task.CommonUpdateTask;
import com.cubrid.cubridmanager.core.common.task.GetCubridConfParameterTask;
import com.cubrid.cubridmanager.core.common.task.SetCubridConfParameterTask;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfo;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoList;
import com.cubrid.cubridmanager.core.replication.task.GetReplAgentStatusTask;
import com.cubrid.cubridmanager.core.replication.task.GetReplServerStatusTask;
import com.cubrid.cubridmanager.core.replication.task.StopReplServerTask;
import com.cubrid.cubridmanager.core.replication.task.StopReplicationAgentTask;
import com.cubrid.cubridmanager.core.utils.CoreUtils;
import com.cubrid.cubridmanager.core.utils.ModelUtil.YesNoType;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;

/**
 * Delete database
 * 
 * @author robin 2009-3-16
 */
public class DeleteDatabaseDialog extends
		CMTitleAreaDialog {
	private Text dbNameText;
	private Table volumeTable;
	private CubridDatabase database = null;
	private DbSpaceInfoList dbSpaceInfo = null;
	public final static int DELETE_ID = 103;
	private Button deleteBackupVolumesButton;
	public final static int CONNECT_ID = 0;
	private TableViewer tableViewer = null;

	public DeleteDatabaseDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create dialog area content
	 * 
	 * @param parent the parent composite
	 * @return the control
	 */
	protected Control createDialogArea(Composite parent) {
		Composite parentComp = (Composite) super.createDialogArea(parent);
		Composite composite = new Composite(parentComp, SWT.NONE);

		GridLayout compLayout = new GridLayout();
		compLayout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		compLayout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		compLayout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		compLayout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(compLayout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		final Group databaseGroup = new Group(composite, SWT.NONE);
		databaseGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		databaseGroup.setLayout(layout);
		final Label databaseNameLabel = new Label(databaseGroup, SWT.NONE);
		databaseNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		databaseNameLabel.setText(Messages.lblDeleteDbName);

		dbNameText = new Text(databaseGroup, SWT.LEFT | SWT.BORDER);
		dbNameText.setEnabled(false);
		final GridData gdDbNameText = new GridData(GridData.FILL_HORIZONTAL);
		gdDbNameText.horizontalSpan = 2;
		dbNameText.setLayoutData(gdDbNameText);

		createDirectoryList(composite);

		deleteBackupVolumesButton = new Button(composite, SWT.CHECK);
		deleteBackupVolumesButton.setText(Messages.btnDelBakup);

		setTitle(Messages.titleDeleteDbDialog);
		setMessage(Messages.msgDeleteDbDialog);

		initial();
		return parentComp;
	}

	/**
	 * create the directory list
	 * 
	 * @param composite the parent composite
	 */
	private void createDirectoryList(Composite composite) {

		final CLabel volumeInformationOfLabel = new CLabel(composite, SWT.NONE);
		volumeInformationOfLabel.setText(Messages.lblVolumeInfomation);

		final String[] columnNameArr = new String[]{
				Messages.tblColDelDbVolName, Messages.tblColDelDbVolPath,
				Messages.tblColDelDbChangeDate, Messages.tblColDelDbVolType,
				Messages.tblColDelDbTotalSize, Messages.tblColDelDbRemainSize,
				Messages.tblColDelDbVolSize };
		tableViewer = CommonUITool.createCommonTableViewer(
				composite,
				new TableViewerSorter() {
					@SuppressWarnings("unchecked")
					public int compare(Viewer viewer, Object e1, Object e2) {
						if (!(e1 instanceof Map) || !(e2 instanceof Map)) {
							return 0;
						}
						int rc = 0;
						Map<String, String> map1 = (Map<String, String>) e1;
						Map<String, String> map2 = (Map<String, String>) e2;
						if (column == 5 || column == 4) {
							rc = CommonUITool.str2Int((String) map1.get(""
									+ column))
									- CommonUITool.str2Int((String) map2.get(""
											+ column));
						} else if (column == 6) {
							double r = CommonUITool.str2Double((String) map1.get(""
									+ column))
									- CommonUITool.str2Double((String) map2.get(""
											+ column));
							if (r == 0) {
								rc = 0;
							} else {
								rc = r > 0 ? 1 : -1;
							}

						} else {
							String str1 = (String) map1.get("" + column);
							String str2 = (String) map2.get("" + column);
							rc = str1.compareTo(str2);
						}
						// If descending order, flip the direction
						if (direction == DESCENDING) {
							rc = -rc;
						}
						return rc;
					}
				}, columnNameArr,
				CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, 200));
		volumeTable = tableViewer.getTable();

	}

	/**
	 * 
	 * Init the value of dialog field
	 * 
	 */
	private void initial() {

		dbNameText.setText(database.getName());
		List<Map<String, Object>> volumeTableListData = new ArrayList<Map<String, Object>>();
		for (DbSpaceInfo bean : dbSpaceInfo.getSpaceinfo()) {
			if (!bean.getType().equals("GENERIC")
					&& !bean.getType().equals("DATA")
					&& !bean.getType().equals("TEMP")
					&& !bean.getType().equals("INDEX")
					&& !bean.getType().equals("Active_log")) {
				continue;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("0", bean.getSpacename());
			map.put("1", bean.getLocation());
			map.put("2", bean.getDate());
			map.put("3", bean.getType());
			map.put("4", bean.getTotalpage() == 0 ? ""
					: (bean.getTotalpage() + ""));
			map.put("5", bean.getFreepage() == 0 ? ""
					: (bean.getFreepage() + ""));
			double mb = dbSpaceInfo.getPagesize() * bean.getTotalpage()
					/ 1048576.0;
			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(2);
			nf.setGroupingUsed(false);

			String volsize = nf.format(mb) + "";
			map.put("6", volsize);
			volumeTableListData.add(map);
		}

		tableViewer.setInput(volumeTableListData);
		for (int i = 0; i < volumeTable.getColumnCount(); i++) {
			volumeTable.getColumn(i).pack();
		}
	}

	/**
	 * 
	 * Delete the database
	 * 
	 * @return <code>true</code> if successful;<code>false</code> otherwise
	 */
	private boolean deleteDatabase() {
		TaskExecutor taskExcutor = new TaskExecutor() {
			public boolean exec(final IProgressMonitor monitor) {

				if (monitor.isCanceled()) {
					return false;
				}
				String taskName = Messages.bind(Messages.delDbTaskName,
						database.getName());
				monitor.beginTask(taskName, IProgressMonitor.UNKNOWN);
				boolean isActiveReplServer = false;
				boolean isActiveReplAgent = false;
				List<String> cubridConfContentList = null;
				for (ITask task : taskList) {
					if (task instanceof GetReplServerStatusTask) {
						GetReplServerStatusTask getReplServerStatusTask = (GetReplServerStatusTask) task;
						getReplServerStatusTask.execute();
						isActiveReplServer = getReplServerStatusTask.isActive();
					} else if (task instanceof StopReplServerTask) {
						if (isActiveReplServer) {
							task.execute();
						}
					} else if (task instanceof GetReplAgentStatusTask) {
						GetReplAgentStatusTask getReplAgentStatusTask = (GetReplAgentStatusTask) task;
						getReplAgentStatusTask.execute();
						isActiveReplAgent = getReplAgentStatusTask.isActive();
					} else if (task instanceof StopReplicationAgentTask) {
						if (isActiveReplAgent) {
							task.execute();
						}
					} else if (task instanceof SetCubridConfParameterTask) {
						if (cubridConfContentList == null) {
							continue;
						} else {
							SetCubridConfParameterTask setParaTask = (SetCubridConfParameterTask) task;
							setParaTask.setConfContents(cubridConfContentList);
							setParaTask.execute();
						}
					} else {
						task.execute();
					}
					final String msg = task.getErrorMsg();
					if (openErrorBox(getShell(), msg, monitor)) {
						return false;
					}
					if (monitor.isCanceled()) {
						return false;
					}
					if (task instanceof GetCubridConfParameterTask) {
						GetCubridConfParameterTask getCubridConfParameterTask = (GetCubridConfParameterTask) task;
						cubridConfContentList = CoreUtils.deleteDatabaseFromServiceServer(
								getCubridConfParameterTask,
								cubridConfContentList, database.getName());
					}
				}
				return true;
			}
		};
		ServerInfo serverInfo = database.getServer().getServerInfo();
		//check the replication server and agent
		if (serverInfo.isSupportReplication() == 0) {
			if (database.isDistributorDb()) {
				GetReplAgentStatusTask getReplAgentStatusTask = new GetReplAgentStatusTask(
						serverInfo);
				getReplAgentStatusTask.setDbName(database.getLabel());
				taskExcutor.addTask(getReplAgentStatusTask);

				StopReplicationAgentTask stopReplAgentTask = new StopReplicationAgentTask(
						serverInfo);
				stopReplAgentTask.setDbName(database.getLabel());
				taskExcutor.addTask(stopReplAgentTask);
			} else {
				GetReplServerStatusTask getReplServerStatusTask = new GetReplServerStatusTask(
						serverInfo);
				getReplServerStatusTask.setDbName(database.getLabel());
				taskExcutor.addTask(getReplServerStatusTask);

				StopReplServerTask stopReplServerTask = new StopReplServerTask(
						serverInfo);
				stopReplServerTask.setDbName(database.getLabel());
				taskExcutor.addTask(stopReplServerTask);
			}
		}

		CommonUpdateTask deleteDbtask = new CommonUpdateTask(
				CommonTaskName.DELETE_DATABASE_TASK_NAME, serverInfo,
				CommonSendMsg.getDeletedbSendMsg());
		deleteDbtask.setDbName(database.getName());

		if (deleteBackupVolumesButton.getSelection()) {
			deleteDbtask.setDelbackup(YesNoType.Y);
		} else {
			deleteDbtask.setDelbackup(YesNoType.N);
		}
		taskExcutor.addTask(deleteDbtask);

		GetCubridConfParameterTask getCubridConfParameterTask = new GetCubridConfParameterTask(
				serverInfo);
		taskExcutor.addTask(getCubridConfParameterTask);

		SetCubridConfParameterTask setCubridConfParameterTask = new SetCubridConfParameterTask(
				serverInfo);
		taskExcutor.addTask(setCubridConfParameterTask);

		new ExecTaskWithProgress(taskExcutor).exec(true, false);
		if (taskExcutor.isSuccess()) {
			setReturnCode(DELETE_ID);
			close();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Constrain the shell size
	 */
	protected void constrainShellSize() {
		super.constrainShellSize();
		getShell().setSize(550, 450);
		CommonUITool.centerShell(getShell());
		getShell().setText(Messages.titleDeleteDbDialog);

	}

	/**
	 * Create buttons for button bar
	 * 
	 * @param parent the parent composite
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, DELETE_ID,
				com.cubrid.cubridmanager.ui.common.Messages.btnOK, true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				com.cubrid.cubridmanager.ui.common.Messages.btnCancel, false);
	}

	/**
	 * When press button,call it
	 * 
	 * @param buttonId the button id
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId == DELETE_ID) {
			if (!verify()) {
				return;
			}
			DeleteDatabaseConfirmDialog dialog = new DeleteDatabaseConfirmDialog(
					getShell());
			dialog.setDatabase(database);
			if (dialog.open() == IDialogConstants.OK_ID) {
				deleteDatabase();
			} else {
				return;
			}
		}
		super.buttonPressed(buttonId);
	}

	/**
	 * 
	 * Verify data
	 * 
	 * @return <code>true</code> if valid;<code>false</code> otherwise
	 */
	private boolean verify() {
		setErrorMessage(null);
		return true;
	}

	public CubridDatabase getDatabase() {
		return database;
	}

	public void setDatabase(CubridDatabase database) {
		this.database = database;
	}

	public DbSpaceInfoList getDbSpaceInfo() {
		return dbSpaceInfo;
	}

	public void setDbSpaceInfo(DbSpaceInfoList dbSpaceInfo) {
		this.dbSpaceInfo = dbSpaceInfo;
	}

}
