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

import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.FileUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.progress.CommonTaskJobExec;
import com.cubrid.common.ui.spi.progress.ITaskExecutorInterceptor;
import com.cubrid.common.ui.spi.progress.JobFamily;
import com.cubrid.common.ui.spi.progress.TaskJobExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.model.OnOffType;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.database.task.CheckDirTask;
import com.cubrid.cubridmanager.core.cubrid.database.task.CopyDbTask;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfo;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoList;
import com.cubrid.cubridmanager.core.utils.ModelUtil.YesNoType;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;

/**
 * The Dialog of Copying the Database in the server.
 * 
 * @author robin 2009-3-11
 */
public class CopyDatabaseDialog extends
		CMTitleAreaDialog implements
		ITaskExecutorInterceptor {
	private static final Logger LOGGER = LogUtil.getLogger(CopyDatabaseDialog.class);
	private Table copyDBVolList;
	private Text destinationDBLogPathText;
	private Text volumePathText;
	private Text destinationDBDirPathText;
	private Text destinationDBNameText;
	private Text sourceLogPathText;
	private Text sourceDBDirPathText;
	private Text sourceDBNameText;
	private CubridDatabase database = null;
	private Button copyVolButton;
	private Composite parentComp;
	private CLabel diskSpaceLabel;
	private CLabel databaseSizeLabel;
	private Button overwriteButton;
	private Button moveButton;
	private String[] newDirectories = null;
	static boolean isIndividChanged = false;
	private List<DbSpaceInfo> volumeList = null;
	private boolean isRunning = false;
	private boolean isDefaultChanged = false;
	private int dbSize;
	private DbSpaceInfoList dbSpaceInfo;
	private List<Map<String, String>> spaceInfoList = null;
	private TableViewer volumeTableViewer;

	private String destDbName;
	private boolean isLocalServer = false;

	private Button dbDirPathButton = null;
	private Button dbExtPathButton = null;
	private final TreeViewer viewer;
	private String jobName;

	private static final String KEY_DATABASE_PATH = "CopyDatabaseDialog.DATABASE_PATH_";
	private static final String KEY_EXTENDED_VOLUME_PATH = "CopyDatabaseDialog.EXTENDED_VOLUME_PATH_";
	private static final String KEY_LOG_FILE_PATH = "CopyDatabaseDialog.LOG_FILE_PATH_";

	/**
	 * The constructor
	 * 
	 * @param parentShell
	 * @param viewer
	 */
	public CopyDatabaseDialog(Shell parentShell, TreeViewer viewer) {
		super(parentShell);
		this.viewer = viewer;
	}

	/**
	 * Create dialog area content
	 * 
	 * @param parent the parent composite
	 * @return the control
	 */
	protected Control createDialogArea(Composite parent) {
		parentComp = (Composite) super.createDialogArea(parent);
		Composite composite = new Composite(parentComp, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);

		isLocalServer = database.getServer().getServerInfo().isLocalServer();

		createSourceDatabaseGroup(composite);
		createDestinationDatabaseGroup(composite);
		createDiskDescLabel(composite);
		createCopyTable(composite);

		setTitle(Messages.titleCopyDbDialog);
		setMessage(Messages.msgCopyDbDialog);
		initial();
		return parentComp;
	}

	/**
	 * 
	 * Create Source Database Group
	 * 
	 * @param composite the parent composite
	 */
	private void createSourceDatabaseGroup(Composite composite) {
		final Group sourceDBGroup = new Group(composite, SWT.NONE);
		sourceDBGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 2));
		sourceDBGroup.setText(Messages.grpDbSourceName);
		GridLayout layout = new GridLayout();
		sourceDBGroup.setLayout(layout);

		final Composite sourceDBComposite = new Composite(sourceDBGroup,
				SWT.NONE);
		sourceDBComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		final GridLayout glSourceDBComposite = new GridLayout();
		glSourceDBComposite.numColumns = 2;
		sourceDBComposite.setLayout(glSourceDBComposite);

		final Label sourceDBNameLabel = new Label(sourceDBComposite, SWT.LEFT);
		sourceDBNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		sourceDBNameLabel.setText(Messages.lblSrcDbName);

		sourceDBNameText = new Text(sourceDBComposite, SWT.BORDER);
		sourceDBNameText.setEnabled(false);
		final GridData gdSourceDBNameText = new GridData(SWT.FILL, SWT.CENTER,
				true, false);
		sourceDBNameText.setLayoutData(gdSourceDBNameText);

		final Label sourceDBDirPathLabel = new Label(sourceDBComposite,
				SWT.LEFT);
		sourceDBDirPathLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1,
				-1));
		sourceDBDirPathLabel.setText(Messages.lblSrcDbPathName);

		sourceDBDirPathText = new Text(sourceDBComposite, SWT.BORDER);
		sourceDBDirPathText.setEnabled(false);
		final GridData gdSourceDBDirPathText = new GridData(SWT.FILL,
				SWT.CENTER, true, false);
		sourceDBDirPathText.setLayoutData(gdSourceDBDirPathText);

		final Label sourceLogPathLabel = new Label(sourceDBComposite, SWT.LEFT);
		sourceLogPathLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		sourceLogPathLabel.setText(Messages.lblSrcLogPathName);

		sourceLogPathText = new Text(sourceDBComposite, SWT.BORDER);
		sourceLogPathText.setEnabled(false);
		final GridData gdSourceLogPathText = new GridData(SWT.FILL, SWT.FILL,
				true, false);
		sourceLogPathText.setLayoutData(gdSourceLogPathText);
	}

	/**
	 * Create Destination Database Group
	 * 
	 * @param composite the parent composite
	 */
	private void createDestinationDatabaseGroup(Composite composite) {

		final Group destinationDBGroup = new Group(composite, SWT.NONE);
		destinationDBGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 2, 1));
		destinationDBGroup.setText(Messages.grpDbDestName);
		final GridLayout gridLayout = new GridLayout();
		destinationDBGroup.setLayout(gridLayout);

		final Composite destinationDBComposit = new Composite(
				destinationDBGroup, SWT.LEFT);
		final GridData gdDestinationDBComposit = new GridData(SWT.FILL,
				SWT.BOTTOM, true, false);

		destinationDBComposit.setLayoutData(gdDestinationDBComposit);
		final GridLayout glDestinationDBComposite = new GridLayout();
		glDestinationDBComposite.numColumns = 3;
		destinationDBComposit.setLayout(glDestinationDBComposite);

		final Label destinationDBNameLabel = new Label(destinationDBComposit,
				SWT.LEFT);
		destinationDBNameLabel.setLayoutData(CommonUITool.createGridData(1, 1,
				-1, -1));
		destinationDBNameLabel.setText(Messages.lblDescDbName);

		destinationDBNameText = new Text(destinationDBComposit, SWT.BORDER);
		final GridData gdDestinationDBNameText = new GridData(SWT.FILL,
				SWT.CENTER, true, false);
		destinationDBNameText.setLayoutData(gdDestinationDBNameText);
		destinationDBNameText.addKeyListener(new EditListener());
		new Label(destinationDBComposit, SWT.LEFT);
		final Label destinationDBDirPathLabel = new Label(
				destinationDBComposit, SWT.LEFT);
		destinationDBDirPathLabel.setLayoutData(CommonUITool.createGridData(1, 1,
				-1, -1));
		destinationDBDirPathLabel.setText(Messages.lblDescDbPathName);

		destinationDBDirPathText = new Text(destinationDBComposit, SWT.BORDER);
		destinationDBDirPathText.setRedraw(true);
		destinationDBDirPathText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, isLocalServer ? 1 : 2, 1, -1, -1));
		destinationDBDirPathText.addKeyListener(new EditListener() {
			@Override
			public void keyReleased(KeyEvent event) {
				isDefaultChanged = true;
				super.keyReleased(event);
			}
		});

		if (isLocalServer) {
			dbDirPathButton = new Button(destinationDBComposit, SWT.PUSH);
			dbDirPathButton.setText(Messages.btnBrowseName);
			dbDirPathButton.setLayoutData(CommonUITool.createGridData(1, 1, 80,
					-1));
			dbDirPathButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					DirectoryDialog dialog = new DirectoryDialog(getShell());
					String text = destinationDBDirPathText.getText();
					if (text == null || text.trim().length() == 0) {
						text = CubridManagerUIPlugin.getPluginDialogSettings().get(
								KEY_DATABASE_PATH + database.getId());
					}
					if (text != null) {
						dialog.setFilterPath(text);
					}
					dialog.setText(Messages.msgSelectDir);
					dialog.setMessage(Messages.msgSelectDir);
					String newPath = dialog.open();
					if (newPath != null) {
						destinationDBDirPathText.setText(newPath);
						isDefaultChanged = true;
						CubridManagerUIPlugin.getPluginDialogSettings().put(
								KEY_DATABASE_PATH + database.getId(), newPath);
					}
				}
			});
		}

		final Label volumnPath = new Label(destinationDBComposit, SWT.LEFT);
		volumnPath.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		volumnPath.setText(Messages.lblVolumePathName);

		volumePathText = new Text(destinationDBComposit, SWT.BORDER);
		volumePathText.setRedraw(true);
		volumePathText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, isLocalServer ? 1 : 2, 1, -1, -1));
		volumePathText.addKeyListener(new EditListener() {
			@Override
			public void keyReleased(KeyEvent event) {
				isDefaultChanged = true;
				super.keyReleased(event);
			}
		});

		if (isLocalServer) {
			dbExtPathButton = new Button(destinationDBComposit, SWT.PUSH);
			dbExtPathButton.setText(Messages.btnBrowseName);
			dbExtPathButton.setLayoutData(CommonUITool.createGridData(1, 1, 80,
					-1));
			dbExtPathButton.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent event) {
					DirectoryDialog dialog = new DirectoryDialog(getShell());
					String text = volumePathText.getText();
					if (text == null || text.trim().length() == 0) {
						text = CubridManagerUIPlugin.getPluginDialogSettings().get(
								KEY_EXTENDED_VOLUME_PATH + database.getId());
					}
					if (text == null || text.trim().length() == 0) {
						text = CubridManagerUIPlugin.getPluginDialogSettings().get(
								KEY_DATABASE_PATH + database.getId());
					}
					if (text != null) {
						dialog.setFilterPath(text);
					}
					dialog.setText(Messages.msgSelectDir);
					dialog.setMessage(Messages.msgSelectDir);
					String newPath = dialog.open();
					if (newPath != null) {
						volumePathText.setText(newPath);
						isDefaultChanged = true;
						CubridManagerUIPlugin.getPluginDialogSettings().put(
								KEY_EXTENDED_VOLUME_PATH + database.getId(),
								newPath);
					}
				}
			});
		}

		final Label destinationDBLogPathLabel = new Label(
				destinationDBComposit, SWT.LEFT);
		destinationDBLogPathLabel.setLayoutData(CommonUITool.createGridData(1, 1,
				-1, -1));
		destinationDBLogPathLabel.setText(Messages.lblDescLogPathName);

		destinationDBLogPathText = new Text(destinationDBComposit, SWT.BORDER);
		destinationDBLogPathText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, isLocalServer ? 1 : 2, 1, -1, -1));
		destinationDBLogPathText.addKeyListener(new EditListener() {
			@Override
			public void keyReleased(KeyEvent event) {
				isDefaultChanged = true;
				super.keyReleased(event);
			}
		});

		if (isLocalServer) {
			Button folders = new Button(destinationDBComposit, SWT.PUSH);
			folders.setText(Messages.btnBrowseName);
			folders.setLayoutData(CommonUITool.createGridData(1, 1, 80, -1));
			folders.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent event) {
					DirectoryDialog dialog = new DirectoryDialog(getShell());
					String text = destinationDBLogPathText.getText();
					if (text == null || text.trim().length() == 0) {
						text = CubridManagerUIPlugin.getPluginDialogSettings().get(
								KEY_LOG_FILE_PATH + database.getId());
					}
					if (text == null || text.trim().length() == 0) {
						text = CubridManagerUIPlugin.getPluginDialogSettings().get(
								KEY_DATABASE_PATH + database.getId());
					}
					if (text != null) {
						dialog.setFilterPath(text);
					}
					dialog.setFilterPath(text);
					dialog.setText(Messages.msgSelectDir);
					dialog.setMessage(Messages.msgSelectDir);
					String newPath = dialog.open();
					if (newPath != null) {
						destinationDBLogPathText.setText(newPath);
						isDefaultChanged = true;
						CubridManagerUIPlugin.getPluginDialogSettings().put(
								KEY_LOG_FILE_PATH + database.getId(), newPath);
					}
				}
			});
		}
	}

	/**
	 * Create diskDesc label
	 * 
	 * @param composite the parent composite
	 */
	private void createDiskDescLabel(Composite composite) {

		diskSpaceLabel = new CLabel(composite, SWT.NONE);
		diskSpaceLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false));
		diskSpaceLabel.setAlignment(SWT.LEFT);

		databaseSizeLabel = new CLabel(composite, SWT.NONE);
		final GridData gdDatabaseSizeLabel = new GridData(SWT.FILL, SWT.FILL,
				false, false);
		databaseSizeLabel.setLayoutData(gdDatabaseSizeLabel);
		databaseSizeLabel.setAlignment(SWT.LEFT);
	}

	/**
	 * Create Copy Button and CopyList
	 * 
	 * @param composite the parent composite
	 */
	private void createCopyTable(Composite composite) {
		copyVolButton = new Button(composite, SWT.CHECK);
		copyVolButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));
		copyVolButton.setEnabled(false);
		copyVolButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				if (copyVolButton.getSelection()) {
					copyDBVolList.setEnabled(true);
					destinationDBDirPathText.setEnabled(false);
					volumePathText.setEnabled(false);
					if (isLocalServer) {
						dbDirPathButton.setEnabled(false);
						dbExtPathButton.setEnabled(false);
					}
				} else {
					copyDBVolList.setEnabled(false);
					copyDBVolList.setSelection(-1);
					destinationDBDirPathText.setEnabled(true);
					volumePathText.setEnabled(true);
					if (isLocalServer) {
						dbDirPathButton.setEnabled(true);
						dbExtPathButton.setEnabled(true);
					}
				}

				if (validateText()) {
					setErrorMessage(null);
					getButton(IDialogConstants.OK_ID).setEnabled(true);
				} else {
					getButton(IDialogConstants.OK_ID).setEnabled(false);
				}
			}
		});
		copyVolButton.setText(Messages.btnCopyVolume);

		final String[] columnNameArr = new String[]{
				Messages.tblColumnCurrentVolName,
				Messages.tblColumnCopyNewVolName,
				Messages.tblColumnCopyNewDirPath };

		volumeTableViewer = CommonUITool.createCommonTableViewer(composite, null,
				columnNameArr,
				CommonUITool.createGridData(GridData.FILL_BOTH, 3, 1, -1, 200));
		copyDBVolList = volumeTableViewer.getTable();
		copyDBVolList.setEnabled(false);
		volumeTableViewer.setColumnProperties(columnNameArr);
		CommonUITool.hackForYosemite(volumeTableViewer.getTable());
		
		CellEditor[] editors = new CellEditor[3];
		editors[0] = null;
		editors[1] = new TextCellEditor(copyDBVolList);
		editors[2] = new TextCellEditor(copyDBVolList);
		volumeTableViewer.setCellEditors(editors);
		volumeTableViewer.setCellModifier(new ICellModifier() {
			@SuppressWarnings("unchecked")
			public boolean canModify(Object element, String property) {
				Map<String, String> map = (Map<String, String>) element;
				String name = map.get("0");
				if (name.equals(database.getName())
						&& (property.equals(columnNameArr[0]) || property.equals(columnNameArr[1]))) {
					return false;
				}
				return true;
			}

			@SuppressWarnings("unchecked")
			public Object getValue(Object element, String property) {
				Map<String, String> map = (Map<String, String>) element;
				if (property.equals(columnNameArr[1])) {
					return map.get("1");
				} else if (property.equals(columnNameArr[2])) {
					return map.get("2");
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
					if (ValidateUtil.isValidDBName(value.toString())) {
						map.put("1", value.toString());
					} else {
						CommonUITool.openErrorBox(getShell(),
								Messages.errCopyDbName);
					}
				} else if (property.equals(columnNameArr[2])) {
					if (ValidateUtil.isValidPathName(value.toString())) {
						map.put("2", value.toString());
					} else {
						CommonUITool.openErrorBox(getShell(),
								Messages.errCopyName);
					}
				}
				isIndividChanged = true;
				volumeTableViewer.refresh();
			}
		});

		overwriteButton = new Button(composite, SWT.CHECK);
		overwriteButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));
		overwriteButton.setText(Messages.btnReplaceDb);

		moveButton = new Button(composite, SWT.CHECK);
		moveButton.setText(Messages.btnDeleteSrcDb);

	}

	/**
	 * Init the value of dialog field
	 * 
	 */
	private void initial() {

		String cubridDirectory = database.getDatabaseInfo().getDbDir();
		volumeList = dbSpaceInfo.getSpaceinfo();
		String srcLogDir = "";
		if (spaceInfoList == null) {
			spaceInfoList = new ArrayList<Map<String, String>>();
			for (DbSpaceInfo bean : volumeList) {
				Map<String, String> map = new HashMap<String, String>();
				if (bean.getType().equals("Active_log")) {
					srcLogDir = bean.getLocation();
				}
				if (!bean.getType().equals("GENERIC")
						&& !bean.getType().equals("DATA")
						&& !bean.getType().equals("TEMP")
						&& !bean.getType().equals("INDEX")) {
					continue;
				}
				map.put("0", bean.getSpacename());
				map.put("1", bean.getSpacename());
				map.put("2", cubridDirectory);
				spaceInfoList.add(map);
			}
			volumeTableViewer.setInput(spaceInfoList);
			for (int i = 0; i < copyDBVolList.getColumnCount(); i++) {
				copyDBVolList.getColumn(i).pack();
			}
		}

		srcLogDir = FileUtil.changeSeparatorByOS(srcLogDir,
				database.getServer().getServerInfo().getServerOsInfo());
		sourceDBNameText.setText(database.getName());
		sourceDBDirPathText.setText(cubridDirectory);
		sourceLogPathText.setText(srcLogDir);

		String dbDir = database.getServer().getServerInfo().getEnvInfo().getDatabaseDir();
		String logPath = CubridManagerUIPlugin.getPluginDialogSettings().get(
				KEY_LOG_FILE_PATH + database.getId());
		if (logPath == null || logPath.trim().length() == 0) {
			logPath = dbDir;
		}
		String dbDirPath = CubridManagerUIPlugin.getPluginDialogSettings().get(
				KEY_DATABASE_PATH + database.getId());
		if (dbDirPath == null || dbDirPath.trim().length() == 0) {
			dbDirPath = dbDir;
		}
		String extVolumePath = CubridManagerUIPlugin.getPluginDialogSettings().get(
				KEY_EXTENDED_VOLUME_PATH + database.getId());
		if (extVolumePath == null || extVolumePath.trim().length() == 0) {
			extVolumePath = dbDir;
		}
		destinationDBLogPathText.setText(logPath);
		destinationDBDirPathText.setText(dbDirPath);
		volumePathText.setText(extVolumePath);

		diskSpaceLabel.setText(Messages.bind(Messages.lblCopyFreeDiskSize,
				dbSpaceInfo.getFreespace() + "(MB)"));
		databaseSizeLabel.setText(Messages.bind(Messages.lblCopyDbSize, dbSize
				/ (1024 * 1024) + "(MB)"));
		isIndividChanged = false;
		isDefaultChanged = false;
	}

	/**
	 * Check the directory of server
	 * 
	 * 
	 * @return the SocketTask
	 */
	private SocketTask getCheckDirsTask() {
		StringBuffer requestMsg = new StringBuffer();
		String tmp = "";
		if (copyVolButton.getSelection()) {
			for (int i = 0; i < copyDBVolList.getItemCount(); i++) {
				TableItem ti = copyDBVolList.getItem(i);
				tmp = ti.getText(2) + "\n";
				if (requestMsg.indexOf(tmp) < 0) {
					requestMsg.append(tmp);
				}
			}
		} else {
			requestMsg.append(destinationDBDirPathText.getText()).append("\n");
			if (!volumePathText.getText().equals(
					destinationDBDirPathText.getText())) {
				requestMsg.append(volumePathText.getText()).append("\n");
			}
		}

		tmp = destinationDBLogPathText.getText() + "\n";
		if (requestMsg.indexOf(tmp) < 0) {
			requestMsg.append(tmp);
		}

		newDirectories = requestMsg.toString().split("\n");
		CheckDirTask task = new CheckDirTask(
				database.getServer().getServerInfo());
		task.setDirectory(newDirectories);
		return task;
	}

	/**
	 * Check the directory of server
	 * 
	 * 
	 * @return the SocketTask
	 */
	private SocketTask getCopyDbTask() {
		CopyDbTask task = new CopyDbTask(database.getServer().getServerInfo());
		task.setSrcdbname(sourceDBNameText.getText());
		task.setDestdbname(destinationDBNameText.getText());
		task.setDestdbpath(destinationDBDirPathText.getText());
		task.setExvolpath(volumePathText.getText());
		task.setLogpath(destinationDBLogPathText.getText());
		if (overwriteButton.getSelection()) {
			task.setOverwrite(YesNoType.Y);
		} else {
			task.setOverwrite(YesNoType.N);
		}
		if (moveButton.getSelection()) {
			task.setMove(YesNoType.Y);
		} else {
			task.setMove(YesNoType.N);
		}
		String oldVolName = null, newVolName = null, oldVolDir = null, newVolDir = null;
		if (copyVolButton.getSelection()) {
			task.setAdvanced(OnOffType.ON);
			StringBuffer openStr = new StringBuffer("volume");
			for (int i = 0; i < copyDBVolList.getItemCount(); i++) {
				TableItem ti = copyDBVolList.getItem(i);
				for (DbSpaceInfo bean : volumeList) {
					if (bean.getSpacename().equals(ti.getText(0))) {
						oldVolDir = bean.getLocation();
						break;
					}
				}
				oldVolName = ti.getText(0);
				newVolName = ti.getText(1);
				newVolDir = ti.getText(2);
				if (oldVolDir != null) {
					oldVolDir = oldVolDir.replaceAll(":", "|");
				}
				if (newVolDir != null) {
					newVolDir = newVolDir.replaceAll(":", "|");
				}
				openStr.append("\n").append(oldVolDir).append("/").append(
						oldVolName).append(":").append(newVolDir).append("/").append(
						newVolName);
			}
			task.setOpen(openStr.toString());
			task.setClose("volume");
		} else {
			task.setAdvanced(OnOffType.OFF);
		}

		return task;
	}

	/**
	 * Constrain the shell size
	 */
	protected void constrainShellSize() {
		super.constrainShellSize();
		CommonUITool.centerShell(getShell());
		getShell().setText(Messages.titleCopyDbDialog);
	}

	/**
	 * Create buttons for button bar
	 * 
	 * @param parent the parent composite
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID,
				com.cubrid.cubridmanager.ui.common.Messages.btnOK, false);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				com.cubrid.cubridmanager.ui.common.Messages.btnCancel, false);
	}

	/**
	 * When ok button press,call this method
	 */
	protected void okPressed() {
		if (!verify()) {
			return;
		}
		destDbName = destinationDBNameText.getText();
		if (!CommonUITool.openConfirmBox(parentComp.getShell(),
				Messages.warnYesNoCopyDb)) {
			return;
		}

		SocketTask checkTask = getCheckDirsTask();
		SocketTask copyTask = getCopyDbTask();

		TaskJobExecutor taskExec = new CommonTaskJobExec(this);
		taskExec.addTask(checkTask);
		taskExec.addTask(copyTask);
		JobFamily jobFamily = new JobFamily();
		String serverName = database.getServer().getName();
		String dbName = database.getName();
		jobFamily.setServerName(serverName);
		jobFamily.setDbName(dbName);

		jobName = Messages.msgCopyDbRearJobName + " - " + dbName + "@"
				+ serverName;
		taskExec.schedule(jobName, jobFamily, true, Job.SHORT);
	}

	/**
	 * 
	 * Verify data
	 * 
	 * @return <code>true</code> if valid;<code>false</code> otherwise
	 */
	private boolean verify() {
		String tmp = destinationDBNameText.getText();
		if (tmp == null || tmp.equals("")) {
			CommonUITool.openErrorBox(parentComp.getShell(),
					Messages.errInputTargetDb);
			return false;
		}

		tmp = destinationDBLogPathText.getText();
		if (tmp == null || tmp.equals("")) {
			CommonUITool.openErrorBox(parentComp.getShell(),
					Messages.errInputLogDirectory);
			return false;
		}
		/*
		 * verify overwrite database
		 */
		List<DatabaseInfo> list = database.getServer().getServerInfo().getLoginedUserInfo().getDatabaseInfoList();
		String dbName = destinationDBNameText.getText();
		boolean dbExistFlag = false;
		for (DatabaseInfo d : list) {
			if (dbName.equals(d.getDbName())) {
				dbExistFlag = true;
				break;
			}
		}

		if (overwriteButton.getSelection()) {
			if (dbExistFlag
					&& !CommonUITool.openConfirmBox(parentComp.getShell(),
							Messages.warnYesNoOverWrite)) {
				return false;
			}
		} else {
			if (dbExistFlag) {
				CommonUITool.openErrorBox(parentComp.getShell(),
						Messages.errDesitinationDbExist);
				return false;
			}
		}

		/**
		 * verify free space
		 */
		if (dbSize / (1024.0 * 1024.0) > dbSpaceInfo.getFreespace()) {
			CommonUITool.openErrorBox(parentComp.getShell(),
					Messages.errNotEnoughSpace);
			return false;
		}
		if (dbSize / (1024.0 * 1024.0) * 1.1 > dbSpaceInfo.getFreespace()
				&& !CommonUITool.openConfirmBox(parentComp.getShell(),
						Messages.warnYesNoCopyDbSpaceOver)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isHelpAvailable() {
		return true;
	}

	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}

	public int getDbSize() {
		return dbSize;
	}

	public void setDbSize(int dbSize) {
		this.dbSize = dbSize;
	}

	public DbSpaceInfoList getDbSpaceInfo() {
		return dbSpaceInfo;
	}

	public void setDbSpaceInfo(DbSpaceInfoList dbSpaceInfo) {
		this.dbSpaceInfo = dbSpaceInfo;
	}

	public CubridDatabase getDatabase() {
		return database;
	}

	public void setDatabase(CubridDatabase database) {
		this.database = database;
	}

	/**
	 * 
	 * Listener of copying volume list
	 * 
	 * @author robin 2009-3-27
	 */
	class EditListener extends
			KeyAdapter {

		/**
		 * Handle with key release event
		 * 
		 * @param event the key event
		 */
		public void keyReleased(KeyEvent event) {
			String destinationDBName = destinationDBNameText.getText();

			String pathSeparator = database.getServer().getServerInfo().getPathSeparator();
			String newpath = database.getServer().getServerInfo().getEnvInfo().getDatabaseDir()
					+ pathSeparator + destinationDBName;
			if (isIndividChanged) {
				spaceInfoList.get(0).put("1", destinationDBName);
			} else {
				spaceInfoList.get(0).put("1", destinationDBName);
				spaceInfoList.get(0).put("2", newpath);
				NumberFormat nf = NumberFormat.getInstance();
				nf.setMinimumIntegerDigits(3);

				for (int i = 1; i < spaceInfoList.size(); i++) {
					Map<String, String> map = spaceInfoList.get(i);
					map.put("1", destinationDBName + "_x" + nf.format(i));
					map.put("2", newpath);
				}

				isIndividChanged = false;
			}
			volumeTableViewer.refresh();
			if (isDefaultChanged) {
				copyDBVolList.getItem(0).setText(1, destinationDBName);
			} else {
				String dbDir = database.getServer().getServerInfo().getEnvInfo().getDatabaseDir();
				String logPath = CubridManagerUIPlugin.getPluginDialogSettings().get(
						KEY_LOG_FILE_PATH + database.getId());
				if (logPath == null || logPath.trim().length() == 0) {
					logPath = dbDir;
				}
				String dbDirPath = CubridManagerUIPlugin.getPluginDialogSettings().get(
						KEY_DATABASE_PATH + database.getId());
				if (dbDirPath == null || dbDirPath.trim().length() == 0) {
					dbDirPath = dbDir;
				}
				String extVolumePath = CubridManagerUIPlugin.getPluginDialogSettings().get(
						KEY_EXTENDED_VOLUME_PATH + database.getId());
				if (extVolumePath == null || extVolumePath.trim().length() == 0) {
					extVolumePath = dbDir;
				}
				destinationDBDirPathText.setText(dbDirPath + pathSeparator
						+ destinationDBName);
				volumePathText.setText(extVolumePath + pathSeparator
						+ destinationDBName);
				destinationDBLogPathText.setText(logPath + pathSeparator
						+ destinationDBName);
			}
			if (validateText()) {
				setErrorMessage(null);
				getButton(IDialogConstants.OK_ID).setEnabled(true);
			} else {
				getButton(IDialogConstants.OK_ID).setEnabled(false);
			}
		}
	}

	/**
	 * 
	 * Validate values of dialog field
	 * 
	 * @return <code>true</code> if valid;<code>false</code> otherwise
	 */
	public boolean validateText() {

		boolean isIndividualSel = copyVolButton.getSelection();
		String volumnPath = volumePathText.getText();
		String destinationDBLogPath = destinationDBLogPathText.getText();
		String destinationDBDirPath = destinationDBDirPathText.getText();
		String destinationDBName = destinationDBNameText.getText();
		if (destinationDBName == null || "".equals(destinationDBName)) {
			setErrorMessage(null);
			getButton(IDialogConstants.OK_ID).setEnabled(false);
			copyVolButton.setEnabled(false);
			copyDBVolList.setEnabled(false);
			return false;
		}
		if (!ValidateUtil.isValidDBName(destinationDBName)) {
			setErrorMessage(Messages.errCopyDbName);
			getButton(IDialogConstants.OK_ID).setEnabled(false);
			copyVolButton.setEnabled(false);
			return false;
		}
		if (!ValidateUtil.isValidDbNameLength(destinationDBName)) {
			setErrorMessage(Messages.bind(Messages.errDatabaseLength,
					ValidateUtil.MAX_DB_NAME_LENGTH));
			getButton(IDialogConstants.OK_ID).setEnabled(false);
			copyVolButton.setEnabled(false);
			return false;
		}
		setErrorMessage(null);
		getButton(IDialogConstants.OK_ID).setEnabled(true);
		copyVolButton.setEnabled(true);
		if (copyVolButton.getSelection()) {
			copyDBVolList.setEnabled(true);
		}

		if (!ValidateUtil.isValidPathName(destinationDBLogPath)) {
			setErrorMessage(Messages.errCopyName);
			return false;
		}
		if (!isIndividualSel) {
			if (!ValidateUtil.isValidPathName(volumnPath)) {
				setErrorMessage(Messages.errCopyName);
				return false;
			}
			if (!ValidateUtil.isValidPathName(destinationDBDirPath)) {
				setErrorMessage(Messages.errCopyName);
				return false;
			}
		}
		return true;
	}

	/**
	 * The listener of edit volume
	 * 
	 * @author robin 2009-6-4
	 */
	static class EditVolumeList implements
			Listener {

		private final Table table;
		private int curIndex = -1;
		private int newIndex = -1;
		private boolean hasChange = false;

		public EditVolumeList(Table volList) {
			table = volList;
		}

		public boolean getChanged() {
			return hasChange;
		}

		/**
		 * Handle event
		 * 
		 * @param event the event
		 */
		public void handleEvent(Event event) {
			Rectangle clientArea = table.getClientArea();
			Point pt = new Point(event.x, event.y);
			int index = table.getTopIndex();
			final TableEditor editor = new TableEditor(table);
			editor.horizontalAlignment = SWT.LEFT;
			editor.grabHorizontal = true;

			curIndex = newIndex;
			newIndex = table.getSelectionIndex();
			if (curIndex < 0 || curIndex != newIndex) {
				return;
			}
			while (index < table.getItemCount()) {
				boolean visible = false;
				final TableItem item = table.getItem(index);
				for (int i = 1; i < table.getColumnCount(); i++) {
					if (index == 0 && i == 1) {
						continue;
					}
					Rectangle rect = item.getBounds(i);
					if (rect.contains(pt)) {
						final int column = i;
						final Text text = new Text(table, SWT.MULTI);
						text.setEditable(true);
						Listener textListener = new Listener() {
							public void handleEvent(final Event event) {
								if (event.type == SWT.FocusOut) {
									if (!text.getText().equals(
											item.getText(column))) {
										hasChange = true;
										CopyDatabaseDialog.isIndividChanged = true;
										if (column == 1
												&& !ValidateUtil.isValidDBName(text.getText())) {
											CommonUITool.openErrorBox(Messages.errCopyName);
										} else if (column == 2
												&& !ValidateUtil.isValidPathName(text.getText())) {
											CommonUITool.openErrorBox(Messages.errCopyName);
										} else {
											item.setText(column, text.getText());
										}
									}
									text.dispose();
								} else if (event.type == SWT.Traverse) {
									if (event.detail == SWT.TRAVERSE_RETURN) {
										if (!text.getText().equals(
												item.getText(column))) {
											hasChange = true;
											CopyDatabaseDialog.isIndividChanged = true;
											if (column == 2
													&& !ValidateUtil.isValidDBName(text.getText())) {
												CommonUITool.openErrorBox(Messages.errCopyName);
											} else if (column == 3
													&& !ValidateUtil.isValidPathName(text.getText())) {
												CommonUITool.openErrorBox(Messages.errCopyName);
											} else {
												item.setText(column,
														text.getText());
											}
										}
									} else if (event.detail == SWT.TRAVERSE_ESCAPE) {
										text.dispose();
										event.doit = false;
									}
								}
							}
						};
						text.addListener(SWT.FocusOut, textListener);
						text.addListener(SWT.Traverse, textListener);
						editor.setEditor(text, item, i);
						text.setText(item.getText(i));
						text.selectAll();
						text.setFocus();
						return;
					}
					if (!visible && rect.intersects(clientArea)) {
						visible = true;
					}
				}
				if (!visible) {
					return;
				}
				index++;
			}
		}
	}

	/**
	 * Execute tasks
	 * 
	 * @param buttonId the button id
	 * @param tasks the task array
	 * @param cancelable whether can be canceled
	 * @param shell the shell
	 */
	public void execTask(final int buttonId, final SocketTask[] tasks,
			boolean cancelable, Shell shell) {
		final Display display = shell.getDisplay();
		isRunning = false;
		try {
			new ProgressMonitorDialog(getShell()).run(true, cancelable,
					new IRunnableWithProgress() {
						public void run(final IProgressMonitor monitor) throws InvocationTargetException,
								InterruptedException {
							monitor.beginTask(
									com.cubrid.common.ui.spi.Messages.msgRunning,
									IProgressMonitor.UNKNOWN);

							if (monitor.isCanceled()) {
								return;
							}

							isRunning = true;
							Thread thread = new Thread() {
								public void run() {
									while (!monitor.isCanceled() && isRunning) {
										try {
											sleep(1);
										} catch (InterruptedException e) {
										}
									}
									if (monitor.isCanceled()) {
										for (SocketTask t : tasks) {
											if (t != null) {
												t.cancel();
											}
										}

									}
								}
							};
							thread.start();
							if (monitor.isCanceled()) {
								isRunning = false;
								return;
							}
							for (SocketTask task : tasks) {
								if (task != null) {
									task.execute();
									final String msg = task.getErrorMsg();
									if (monitor.isCanceled()) {
										isRunning = false;
										return;
									}
									if (msg != null && msg.length() > 0
											&& !monitor.isCanceled()) {
										display.syncExec(new Runnable() {
											public void run() {
												CommonUITool.openErrorBox(
														getShell(), msg);
											}
										});
										isRunning = false;
										return;
									}
								}
								if (monitor.isCanceled()) {
									isRunning = false;
									return;
								}
							}
							if (monitor.isCanceled()) {
								isRunning = false;
								return;
							}
							if (!monitor.isCanceled()) {
								display.syncExec(new Runnable() {
									public void run() {
										if (buttonId > 0) {
											setReturnCode(buttonId);
											close();
										}
									}
								});
							}
							isRunning = false;
							monitor.done();
						}
					});
		} catch (InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (InterruptedException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public String getDestDbName() {
		return destDbName;
	}

	public void setDestDbName(String destDbName) {
		this.destDbName = destDbName;
	}

	/**
	 * When complete,call it
	 */
	public void completeAll() {
		CommonUITool.refreshNavigatorTree(viewer, database.getParent());
		ActionManager.getInstance().fireSelectionChanged(viewer.getSelection());

		CommonUITool.openInformationBox(Messages.titleSuccess,
				Messages.bind(Messages.msgCopyDBComplete, jobName));

	}

	/**
	 * When task finished,refresh UI
	 * 
	 * @param task the task
	 * @return status
	 */
	public IStatus postTaskFinished(ITask task) {
		if (task instanceof CheckDirTask) {
			String[] dirs = ((CheckDirTask) task).getNoExistDirectory();
			if (dirs == null) {
				dirs = new String[]{};
			}
			if ((newDirectories = dirs).length > 0) {
				NewDirectoryDialog dirDlg = new NewDirectoryDialog(
						parentComp.getShell(), newDirectories);
				if (dirDlg.open() == IDialogConstants.CANCEL_ID) {
					return Status.CANCEL_STATUS;
				}
			}
		}
		return Status.OK_STATUS;
	}

}
