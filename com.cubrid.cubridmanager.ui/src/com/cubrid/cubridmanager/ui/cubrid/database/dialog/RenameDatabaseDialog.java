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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.FileUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.TableViewerSorter;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.progress.ITaskExecutorInterceptor;
import com.cubrid.common.ui.spi.progress.JobFamily;
import com.cubrid.common.ui.spi.progress.TaskJobExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.model.EnvInfo;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.task.GetCubridConfParameterTask;
import com.cubrid.cubridmanager.core.common.task.SetCubridConfParameterTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.database.task.CheckDirTask;
import com.cubrid.cubridmanager.core.cubrid.database.task.RenameDbTask;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoList;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.VolumeType;
import com.cubrid.cubridmanager.core.utils.CoreUtils;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;

/**
 * 
 * Rename database will use this dialog to fill in the information
 * 
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class RenameDatabaseDialog extends CMTitleAreaDialog implements ModifyListener {
	private Logger LOGGER = LogUtil.getLogger(RenameDatabaseDialog.class);
	private Text databaseNameText = null;
	private CubridDatabase database = null;
	private Button exVolumePathButton;
	private Text exVolumePathText;
	private Button forceDelBackupVolumeButton;
	private Button renameVolumeButton;
	private Table volumeTable;
	private DbSpaceInfoList dbSpaceInfoList = null;
	private List<Map<String, String>> spaceInfoList = null;
	private TableViewer volumeTableViewer;
	private String extVolumePath = "";
	private boolean isCanFinished = true;
	private Button selectVolumeDirectoryButton;
	private final ITaskExecutorInterceptor iUpdateUIWithJob;
	private final static String KEY_EXTENDED_VOLUME_PATH = "RenameDatabaseDialog.EXTENDED_VOLUME_PATH_";
	private String newDBName;

	/**
	 * The constructor
	 * 
	 * @param parentShell
	 */
	public RenameDatabaseDialog(Shell parentShell,
			ITaskExecutorInterceptor iUpdateUIWithJob) {
		super(parentShell);
		this.iUpdateUIWithJob = iUpdateUIWithJob;
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
		GridLayout layout = new GridLayout();
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(gridData);

		createNewDatabaseInfoComp(composite);
		setTitle(Messages.titleRenameDbDialog);
		setMessage(Messages.msgRenameDbDialog);
		initial();
		return parentComp;
	}

	/**
	 * 
	 * Create database name group
	 * 
	 * @param parent the parent composite
	 */
	private void createNewDatabaseInfoComp(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		comp.setLayoutData(gridData);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		comp.setLayout(layout);

		Label databaseNameLabel = new Label(comp, SWT.LEFT | SWT.WRAP);
		databaseNameLabel.setText(Messages.lblNewDbName);
		gridData = new GridData();
		gridData.widthHint = 150;
		databaseNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

		databaseNameText = new Text(comp, SWT.BORDER);
		databaseNameText.setTextLimit(ValidateUtil.MAX_DB_NAME_LENGTH);
		databaseNameText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, -1, -1));

		exVolumePathButton = new Button(comp, SWT.LEFT | SWT.RADIO);
		exVolumePathButton.setText(Messages.btnExtendedVolumePath);
		exVolumePathButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		exVolumePathButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (exVolumePathButton.getSelection()) {
					exVolumePathText.setEditable(true);
					renameVolumeButton.setSelection(false);
					volumeTable.setEnabled(false);
					if (selectVolumeDirectoryButton != null) {
						ServerInfo serverInfo = database.getServer().getServerInfo();
						selectVolumeDirectoryButton.setEnabled(serverInfo != null
								&& serverInfo.isLocalServer());
					}
				} else {
					exVolumePathText.setEditable(false);
					volumeTable.setEnabled(true);
					if (selectVolumeDirectoryButton != null) {
						selectVolumeDirectoryButton.setEnabled(false);
					}
				}
			}
		});
		exVolumePathButton.setSelection(true);

		boolean isLocalServer = database.getServer().getServerInfo().isLocalServer();
		exVolumePathText = new Text(comp, SWT.BORDER);
		exVolumePathText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, isLocalServer ? 1 : 2, 1, -1, -1));

		if (isLocalServer) {
			selectVolumeDirectoryButton = new Button(comp, SWT.NONE);
			selectVolumeDirectoryButton.setText(Messages.btnBrowse);
			selectVolumeDirectoryButton.setLayoutData(CommonUITool.createGridData(
					1, 1, 80, -1));
			selectVolumeDirectoryButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					String text = exVolumePathText.getText();
					if (text == null || text.trim().length() == 0) {
						text = CubridManagerUIPlugin.getPluginDialogSettings().get(
								KEY_EXTENDED_VOLUME_PATH + database.getId());
					}
					if (text == null || text.trim().length() == 0) {
						text = extVolumePath;
					}
					DirectoryDialog dlg = new DirectoryDialog(getShell());
					if (text != null) {
						dlg.setFilterPath(text);
					}
					dlg.setText(Messages.msgSelectDir);
					dlg.setMessage(Messages.msgSelectDir);
					String dir = dlg.open();
					if (dir != null) {
						exVolumePathText.setText(dir);
						CubridManagerUIPlugin.getPluginDialogSettings().put(
								KEY_EXTENDED_VOLUME_PATH + database.getId(),
								dir);
					}
				}
			});
			selectVolumeDirectoryButton.setEnabled(true);
		}

		renameVolumeButton = new Button(comp, SWT.LEFT | SWT.RADIO);
		renameVolumeButton.setText(Messages.btnRenameIndiVolume);
		renameVolumeButton.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 3, 1, -1, -1));
		renameVolumeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (renameVolumeButton.getSelection()) {
					volumeTable.setEnabled(true);
				} else {
					volumeTable.setEnabled(false);
				}
			}
		});

		final String[] columnNameArr = new String[]{
				Messages.tblColumnCurrVolName, Messages.tblColumnNewVolName,
				Messages.tblColumnCurrDirPath, Messages.tblColumnNewDirPath };
		volumeTableViewer = CommonUITool.createCommonTableViewer(comp,
				new TableViewerSorter(), columnNameArr,
				CommonUITool.createGridData(GridData.FILL_BOTH, 3, 1, -1, 200));
		volumeTable = volumeTableViewer.getTable();
		volumeTable.setEnabled(false);
		volumeTableViewer.setColumnProperties(columnNameArr);
		CellEditor[] editors = new CellEditor[4];
		editors[0] = null;
		editors[1] = new TextCellEditor(volumeTable);
		editors[2] = null;
		editors[3] = new TextCellEditor(volumeTable);
		volumeTableViewer.setCellEditors(editors);
		volumeTableViewer.setCellModifier(new ICellModifier() {
			@SuppressWarnings("unchecked")
			public boolean canModify(Object element, String property) {
				Map<String, String> map = (Map<String, String>) element;
				String name = map.get("0");
				if (property.equals(columnNameArr[0])
						|| property.equals(columnNameArr[2])) {
					return false;
				} else if (property.equals(columnNameArr[1])
						&& name.equals(database.getName())) {
					return false;
				}
				return true;
			}

			@SuppressWarnings("unchecked")
			public Object getValue(Object element, String property) {
				Map<String, String> map = (Map<String, String>) element;
				if (property.equals(columnNameArr[1])) {
					return map.get("1");
				} else if (property.equals(columnNameArr[3])) {
					return map.get("3");
				}
				return null;
			}

			@SuppressWarnings("unchecked")
			public void modify(Object element, String property, Object value) {
				Object obj = null;
				if (element instanceof Item) {
					obj = ((Item) element).getData();
				}
				if (obj == null) {
					return;
				}
				Map<String, String> map = (Map<String, String>) obj;
				if (property.equals(columnNameArr[1])) {
					map.put("1", value.toString());
				} else if (property.equals(columnNameArr[3])) {
					map.put("3", value.toString());
				}
				volumeTableViewer.refresh();
			}
		});

		forceDelBackupVolumeButton = new Button(comp, SWT.LEFT | SWT.CHECK);
		forceDelBackupVolumeButton.setText(Messages.btnForceDelBackupVolume);
		forceDelBackupVolumeButton.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 3, 1, -1, -1));
	}

	/**
	 * Constrain the shell size
	 */
	protected void constrainShellSize() {
		super.constrainShellSize();
		CommonUITool.centerShell(getShell());
		getShell().setText(Messages.titleRenameDbDialog);
	}

	/**
	 * Create buttons for button bar
	 * 
	 * @param parent the parent composite
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID,
				com.cubrid.cubridmanager.ui.common.Messages.btnOK, true);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				com.cubrid.cubridmanager.ui.common.Messages.btnCancel, false);
	}

	/**
	 * When button press,call it
	 * 
	 * @param buttonId the button id
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			renameDb();
		} else {
			super.buttonPressed(buttonId);
		}
	}

	/**
	 * Execute task and rename database
	 */
	private void renameDb() {
		newDBName = databaseNameText.getText();
		isCanFinished = true;
		TaskJobExecutor taskExec = new TaskJobExecutor() {
			public IStatus exec(final IProgressMonitor monitor) {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						getShell().setVisible(false);
					}
				});
				if (monitor.isCanceled()) {
					cancel();
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							close();
						}
					});
					return Status.CANCEL_STATUS;
				}
				List<String> cubridConfContentList = null;
				for (ITask task : taskList) {
					if (task instanceof SetCubridConfParameterTask) {
						if (cubridConfContentList == null) {
							LOGGER.warn("cubridConfContentList is null. Skip SetCubridConfParameterTask.");
							continue;
						}

						SetCubridConfParameterTask setParaTask = (SetCubridConfParameterTask) task;
						setParaTask.setConfContents(cubridConfContentList);
					}

					task.execute();
					final String msg = task.getErrorMsg();
					if (msg != null && msg.length() > 0 && !monitor.isCanceled() && !isCanceled()) {
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								getShell().setVisible(true);
							}
						});
						return new Status(IStatus.ERROR, CubridManagerUIPlugin.PLUGIN_ID, msg);
					}

					if (isCanceled()) {
						return Status.CANCEL_STATUS;
					}

					if (task instanceof CheckDirTask) {
						CheckDirTask checkDirTask = (CheckDirTask) task;
						final String[] dirs = checkDirTask.getNoExistDirectory();
						if (dirs != null && dirs.length > 0) {
							Display.getDefault().syncExec(new Runnable() {
								public void run() {
									CreateDirDialog dialog = new CreateDirDialog(getShell());
									dialog.setDirs(dirs);
									if (dialog.open() != IDialogConstants.OK_ID) {
										isCanFinished = false;
									}
								}
							});
						}
					} else if (task instanceof GetCubridConfParameterTask) {
						GetCubridConfParameterTask getCubridConfParameterTask = (GetCubridConfParameterTask) task;
						cubridConfContentList = CoreUtils.renameDatabaseFromServiceServer(getCubridConfParameterTask,
								cubridConfContentList, database.getName(), newDBName);
					}

					if (!isCanFinished || monitor.isCanceled()) {
						cancel();
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								close();
							}
						});
						return Status.CANCEL_STATUS;
					}
				}

				return Status.OK_STATUS;
			}

			public void done(IJobChangeEvent event) {
				if (event.getResult() == Status.OK_STATUS) {
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							iUpdateUIWithJob.completeAll();
							close();
						}
					});
				}
			}
		};

		CheckDirTask checkDirTask = new CheckDirTask(database.getServer().getServerInfo());
		RenameDbTask renameDbTask = new RenameDbTask(database.getServer().getServerInfo());
		renameDbTask.setDbName(database.getLabel());
		renameDbTask.setNewDbName(newDBName);
		if (exVolumePathButton.getSelection()) {
			checkDirTask.setDirectory(new String[]{exVolumePathText.getText() });
			renameDbTask.setExVolumePath(exVolumePathText.getText());
			renameDbTask.setAdvanced(false);
		} else if (renameVolumeButton.getSelection()) {
			List<String> pathList = new ArrayList<String>();
			List<String> volumeChangedList = new ArrayList<String>();
			for (int i = 0; spaceInfoList != null && i < spaceInfoList.size(); i++) {
				Map<String, String> map = spaceInfoList.get(i);
				String oldName = map.get("0");
				String newName = map.get("1");
				String oldPath = map.get("2");
				String newPath = map.get("3");
				addVolumePath(pathList, newPath);
				oldPath = oldPath.replaceAll(":", "|");
				newPath = newPath.replaceAll(":", "|");
				volumeChangedList.add(oldPath + "/" + oldName + ":" + newPath + "/" + newName);
			}
			String[] checkedDirs = new String[pathList.size()];
			pathList.toArray(checkedDirs);
			checkDirTask.setDirectory(checkedDirs);
			renameDbTask.setAdvanced(true);
			renameDbTask.setIndividualVolume(volumeChangedList);
		}
		if (forceDelBackupVolumeButton.getSelection()) {
			renameDbTask.setForceDel(true);
		} else {
			renameDbTask.setForceDel(false);
		}

		taskExec.addTask(renameDbTask);
		GetCubridConfParameterTask getCubridConfParameterTask = new GetCubridConfParameterTask(database.getServer()
				.getServerInfo());
		taskExec.addTask(getCubridConfParameterTask);
		SetCubridConfParameterTask setCubridConfParameterTask = new SetCubridConfParameterTask(database.getServer()
				.getServerInfo());
		taskExec.addTask(setCubridConfParameterTask);

		JobFamily jobFamily = new JobFamily();
		String serverName = database.getServer().getName();
		String dbName = database.getName();
		jobFamily.setServerName(serverName);
		jobFamily.setDbName(dbName);

		String jobName = Messages.msgRenameDBRearJobName + " - " + dbName + "@"
				+ serverName;
		taskExec.schedule(jobName, jobFamily, true, Job.SHORT);
	}

	/**
	 * 
	 * Add volume path into list
	 * 
	 * @param checkedList the checked file list
	 * @param volumePath the volume path
	 */
	private void addVolumePath(List<String> checkedList, String volumePath) {
		boolean isExist = false;
		for (int i = 0; i < checkedList.size(); i++) {
			String volPath = checkedList.get(i);
			if (volumePath.equals(volPath)) {
				isExist = true;
				break;
			}
		}
		if (!isExist) {
			checkedList.add(volumePath);
		}
	}

	/**
	 * 
	 * Initial data
	 * 
	 */
	private void initial() {
		EnvInfo envInfo = database.getServer().getServerInfo().getEnvInfo();
		if (envInfo != null) {
			extVolumePath = envInfo.getDatabaseDir();
			extVolumePath = FileUtil.changeSeparatorByOS(extVolumePath,
					database.getServer().getServerInfo().getServerOsInfo());
			exVolumePathText.setText(extVolumePath);
		}
		String dir = CubridManagerUIPlugin.getPluginDialogSettings().get(
				KEY_EXTENDED_VOLUME_PATH + database.getId());
		if (dir != null && dir.trim().length() > 0) {
			extVolumePath = dir;
			exVolumePathText.setText(extVolumePath);
		}
		if (spaceInfoList == null) {
			spaceInfoList = new ArrayList<Map<String, String>>();
			if (this.dbSpaceInfoList != null) {
				List<DbSpaceInfoList.DbSpaceInfo> list = this.dbSpaceInfoList.getSpaceinfo();
				for (int i = 0; list != null && i < list.size(); i++) {
					Map<String, String> map = new HashMap<String, String>();
					DbSpaceInfoList.DbSpaceInfo spaceInfo = list.get(i);
					String type = spaceInfo.getType();
					if (!VolumeType.GENERIC.getText().equals(type)
							&& !VolumeType.DATA.getText().equals(type)
							&& !VolumeType.INDEX.getText().equals(type)
							&& !VolumeType.TEMP.getText().equals(type)) {
						continue;
					}
					map.put("0", spaceInfo.getSpacename());
					map.put("1", spaceInfo.getSpacename());
					String location = spaceInfo.getLocation();
					location = FileUtil.changeSeparatorByOS(
							location,
							database.getServer().getServerInfo().getServerOsInfo());
					map.put("2", location);
					map.put("3", location);
					spaceInfoList.add(map);
				}
			}
		}
		volumeTableViewer.setInput(spaceInfoList);
		for (int i = 0; i < volumeTable.getColumnCount(); i++) {
			volumeTable.getColumn(i).pack();
		}
		databaseNameText.addModifyListener(this);
		exVolumePathText.addModifyListener(this);
	}

	/**
	 * Listen to the modify text event
	 * 
	 * @param event the modify event
	 */
	public void modifyText(ModifyEvent event) {

		String databaseName = databaseNameText.getText();
		String volumePath = exVolumePathText.getText();
		boolean isValidDatabaseName = ValidateUtil.isValidDBName(databaseName);
		if (!isValidDatabaseName) {
			setErrorMessage(Messages.errDbName);
			setEnabled(false);
			return;
		}
		if (event.widget == databaseNameText && isValidDatabaseName) {
			String newPath = extVolumePath
					+ database.getServer().getServerInfo().getPathSeparator()
					+ databaseName;
			exVolumePathText.setText(newPath);
			int count = 1;
			for (int i = 0; spaceInfoList != null && i < spaceInfoList.size(); i++) {
				Map<String, String> map = spaceInfoList.get(i);
				String name = database.getLabel();
				if (name.equals(map.get("0"))) {
					map.put("1", databaseName);
					map.put("3", newPath);
				} else {
					NumberFormat nf = NumberFormat.getInstance();
					nf.setMinimumIntegerDigits(3);
					map.put("1", databaseName + "_x" + nf.format(count));
					map.put("3", newPath);
					count++;
				}
			}
			if (volumeTableViewer != null) {
				volumeTableViewer.refresh();
			}
		}
		boolean isValidDatabaseNameLength = ValidateUtil.isValidDbNameLength(databaseName);
		if (!isValidDatabaseNameLength) {
			setErrorMessage(Messages.bind(
					Messages.errDbNameLength,
					new String[]{String.valueOf(ValidateUtil.MAX_DB_NAME_LENGTH - 1) }));
			setEnabled(false);
			return;
		}
		DatabaseInfo databaseInfo = database.getServer().getServerInfo().getLoginedUserInfo().getDatabaseInfo(
				databaseName);
		boolean isDatabaseNameAlrExist = databaseInfo != null;
		if (isDatabaseNameAlrExist) {
			setErrorMessage(Messages.errDbExist);
			setEnabled(false);
			return;
		}
		boolean isValidVolumePath = ValidateUtil.isValidPathName(volumePath);
		if (!isValidVolumePath) {
			setErrorMessage(Messages.errExtendedVolPath);
			setEnabled(false);
			return;
		}
		setErrorMessage(null);
		setEnabled(true);
	}

	/**
	 * 
	 * Enable or disable the OK button
	 * 
	 * @param isEnabled whether it is enabled
	 */
	private void setEnabled(boolean isEnabled) {
		if (getButton(IDialogConstants.OK_ID) != null) {
			getButton(IDialogConstants.OK_ID).setEnabled(isEnabled);
		}
	}

	/**
	 * 
	 * Get added CubridDatabase
	 * 
	 * @return the CubridDatabase object
	 */
	public CubridDatabase getDatabase() {
		return database;
	}

	/**
	 * 
	 * Set edited CubridDatabase
	 * 
	 * @param database the CubridDatabase object
	 */
	public void setDatabase(CubridDatabase database) {
		this.database = database;
	}

	public void setDbSpaceInfoList(DbSpaceInfoList dbSpaceInfoList) {
		this.dbSpaceInfoList = dbSpaceInfoList;
	}
}
