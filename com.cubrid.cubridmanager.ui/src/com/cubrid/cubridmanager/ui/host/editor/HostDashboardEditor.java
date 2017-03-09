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
package com.cubrid.cubridmanager.ui.host.editor;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.DateUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.preference.GeneralPreference;
import com.cubrid.common.ui.common.preference.ShowDashboardDialog;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.TableContentProvider;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.part.CubridEditorPart;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.CommonTaskJobExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.ITaskExecutorInterceptor;
import com.cubrid.common.ui.spi.progress.JobFamily;
import com.cubrid.common.ui.spi.progress.TaskJobExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfo;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfoList;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfos;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.common.model.EnvInfo;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.task.CommonQueryTask;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.common.task.GetCubridConfParameterTask;
import com.cubrid.cubridmanager.core.common.task.GetEnvInfoTask;
import com.cubrid.cubridmanager.core.common.task.SetCubridConfParameterTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.database.task.GetDatabaseListTask;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfo;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoList;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.VolumeType;
import com.cubrid.cubridmanager.core.monitoring.model.HostStatData;
import com.cubrid.cubridmanager.core.monitoring.model.HostStatDataProxy;
import com.cubrid.cubridmanager.core.utils.CoreUtils;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.broker.editor.BrokersStatusContentProvider;
import com.cubrid.cubridmanager.ui.broker.editor.BrokersStatusLabelProvider;
import com.cubrid.cubridmanager.ui.host.Messages;
import com.cubrid.cubridmanager.ui.host.action.HostDashboardEditorInput;

/**
 *
 * The HostStatusEditor
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-10-11 created by Kevin.Wang
 */
public class HostDashboardEditor extends
		CubridEditorPart {
	private static final Logger LOGGER = LogUtil.getLogger(HostDashboardEditor.class);

	public static final String ID = "com.cubrid.cubridmanager.ui.host.editor.HostDashboardEditor";
	private ServerInfo serverInfo;
	private List<DatabaseInfo> databaseInfoList;

	private Label infoLable;
	private StyledText dbServerInfoText;
	private TableViewer volumeTableViewer;
	private TableViewer brokerTableViewer;
	private TableViewer serverTableViewer;
	private Table databaseTable;
	private final List<TableEditor> checkEditors = new ArrayList<TableEditor>();

	private DataGeneratorThread dataGeneratorThread = null;
	private volatile int finishedCount = 0;
	private static int TOTAL_TASK_COUNT = 4;

	/*ToolBar Item*/
	private ToolItem refreshItem;
	private ToolItem exportItem;

	private ToolItem saveItem;
	private ToolItem settingItem;
	private boolean isDirty = false;
	private long freespaceOnStorage = -1;
	private ExpandBar bar = null;

	public void createPartControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.None);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		composite.setLayout(layout);

		Composite buttonComp = new Composite(composite, SWT.None);
		buttonComp.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, -1, 20));

		Composite dataComp = new Composite(composite, SWT.None);
		dataComp.setLayout(new FillLayout());
		dataComp.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH,
				1, 1, -1, -1));

		/*Create button*/
		createButtonComposite(buttonComp);

		/* Database server information */
		bar = new ExpandBar(dataComp, SWT.V_SCROLL);

		int index = 0;
		createVolumnInfoItem(bar, index++);
		createBrokerInfoItem(bar, index++);
		createServerStatusItem(bar, index++);
		createDBInfoItem(bar, index++);
		createServerInfoItem(bar, index++);

		loadAllData();
	}

	/**
	 * Initializes this editor with the given editor site and input.
	 *
	 * @param site the editor site
	 * @param input the editor input
	 * @exception PartInitException if this editor was not initialized
	 *            successfully
	 */
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		CubridNodeManager.getInstance().addCubridNodeChangeListener(this);

		if (input instanceof CubridServer) {
			serverInfo = ((CubridServer) input).getServerInfo();
		} else if (input instanceof HostDashboardEditorInput) {
			HostDashboardEditorInput hostDashboardEditorInput = (HostDashboardEditorInput) input;
			serverInfo = hostDashboardEditorInput.getServerInfo();
		}

		setPartName(serverInfo.getServerName());
	}

	private void createButtonComposite(Composite parent) {
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		parent.setLayout(layout);

		infoLable = new Label(parent, SWT.None);
		infoLable.setLayoutData(
				CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

		ToolBar toolbar = new ToolBar(parent, SWT.RIGHT_TO_LEFT | SWT.WRAP | SWT.FLAT);
		toolbar.setLayoutData(
				CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_END, 1, 1, -1, -1));

		settingItem = new ToolItem(toolbar, SWT.PUSH);
		settingItem.setImage(CommonUIPlugin.getImage("/icons/action/settings.png"));
		settingItem.setToolTipText(Messages.itemSetting);
		settingItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				boolean useOrNot = GeneralPreference.isUseHostDashboard();
				ShowDashboardDialog dialog = new ShowDashboardDialog(
						getSite().getShell(), ShowDashboardDialog.TYPE_HOST, useOrNot, -1);
				if (dialog.open() == IDialogConstants.OK_ID) {
					useOrNot = dialog.isUseAutoShow();
					GeneralPreference.setUseHostDashboard(useOrNot);
				}
			}
		});

		saveItem = new ToolItem(toolbar, SWT.PUSH);
		saveItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/file_save.png"));
		saveItem.setDisabledImage(CommonUIPlugin.getImage("icons/queryeditor/file_save_disabled.png"));
		saveItem.setToolTipText(Messages.saveAutoStartipLabel);
		saveItem.setEnabled(false);
		saveItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				saveAutoParam(getNewAutoStartDB());
			}
		});

		exportItem = new ToolItem(toolbar, SWT.PUSH);
		exportItem.setImage(CubridManagerUIPlugin.getImage("icons/action/conf_export.png"));
		exportItem.setToolTipText(Messages.itemExport);
		exportItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				new ExportHostStatusDialog(
						Display.getCurrent().getActiveShell(),
						HostDashboardEditor.this).open();
			}
		});

		refreshItem = new ToolItem(toolbar, SWT.PUSH);
		refreshItem.setImage(CubridManagerUIPlugin.getImage("icons/action/refresh.png"));
		refreshItem.setToolTipText(Messages.itemRefresh);
		refreshItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				loadAllData();
			}
		});
	}

	private void createServerInfoItem(ExpandBar bar, int index) {
		/* Database server information */
		ExpandItem dbServerInfoItem = new ExpandItem(bar, SWT.NONE, index);
		dbServerInfoItem.setText(Messages.titleServerInfo);

		Composite dbServerComposite = new Composite(bar, SWT.None);
		dbServerComposite.setLayout(new FillLayout());
		dbServerInfoText = new StyledText(dbServerComposite, SWT.MULTI);
		dbServerInfoText.setEditable(false);
		dbServerInfoItem.setControl(dbServerComposite);
		dbServerInfoItem.setHeight(110);
		dbServerInfoItem.setExpanded(true);
	}

	private void createDBInfoItem(ExpandBar bar, int index) {
		/* Database information */
		ExpandItem dbInfoItem = new ExpandItem(bar, SWT.NONE, index);
		dbInfoItem.setText(Messages.titleDBInfo);


		Composite scrolledComp = new Composite(bar, SWT.None);
		scrolledComp.setLayout(new FillLayout());

		databaseTable = new Table(scrolledComp, SWT.BORDER | SWT.V_SCROLL);
		databaseTable.setHeaderVisible(true);
		databaseTable.setLinesVisible(true);

		TableColumn dbNameColumn = new TableColumn(databaseTable, SWT.LEFT);
		dbNameColumn.setText(Messages.columnDB);
		dbNameColumn.setToolTipText(Messages.columnDB);
		dbNameColumn.setWidth(150);

		TableColumn dbAutoColumn = new TableColumn(databaseTable, SWT.CENTER
				| SWT.CHECK);
		dbAutoColumn.setText(Messages.columnAutoStart);
		dbAutoColumn.setToolTipText(Messages.columnAutoStart);
		dbAutoColumn.setWidth(90);

		TableColumn dbOptColumn = new TableColumn(databaseTable, SWT.CENTER);
		dbOptColumn.setText(Messages.columnDBStatus);
		dbOptColumn.setToolTipText(Messages.columnDBStatus);
		dbOptColumn.setWidth(90);

		dbInfoItem.setHeight(100);
		dbInfoItem.setExpanded(true);

		dbInfoItem.setControl(scrolledComp);
	}

	private void createVolumnInfoItem(ExpandBar bar, int index) {
		/* Database volume information */
		ExpandItem volumeInfoItem = new ExpandItem(bar, SWT.NONE, index);
		volumeInfoItem.setText(Messages.titleVolumeInfo);

		Composite volumeComposite = new Composite(bar, SWT.None);
		volumeComposite.setLayout(new FillLayout());

		volumeTableViewer = new TableViewer(volumeComposite, SWT.BORDER
				| SWT.FULL_SELECTION);
		volumeTableViewer.getTable().setHeaderVisible(true);
		volumeTableViewer.getTable().setLinesVisible(true);
		volumeTableViewer.setLabelProvider(new DBSpaceLabelProvider());
		volumeTableViewer.setContentProvider(new TableContentProvider());

		TableColumn dbNameColumn = new TableColumn(
				volumeTableViewer.getTable(), SWT.LEFT);
		dbNameColumn.setText(Messages.columnDB);
		dbNameColumn.setToolTipText(Messages.columnDB);
		dbNameColumn.setWidth(150);

		TableColumn dataColumn = new TableColumn(volumeTableViewer.getTable(),
				SWT.LEFT);
		dataColumn.setText(Messages.columnData);
		dataColumn.setToolTipText(Messages.columnDataTip);
		dataColumn.setWidth(150);

		TableColumn indexColumn = new TableColumn(volumeTableViewer.getTable(),
				SWT.LEFT);
		indexColumn.setText(Messages.columnIndex);
		indexColumn.setToolTipText(Messages.columnIndexTip);
		indexColumn.setWidth(150);

		TableColumn tempColumn = new TableColumn(volumeTableViewer.getTable(),
				SWT.LEFT);
		tempColumn.setText(Messages.columnTemp);
		tempColumn.setToolTipText(Messages.columnTempTip);
		tempColumn.setWidth(150);

		TableColumn genericColumn = new TableColumn(
				volumeTableViewer.getTable(), SWT.LEFT);
		genericColumn.setText(Messages.columnGeneric);
		genericColumn.setToolTipText(Messages.columnGenericTip);
		genericColumn.setWidth(150);

		TableColumn activeLogColumn = new TableColumn(
				volumeTableViewer.getTable(), SWT.LEFT);
		activeLogColumn.setText(Messages.columnActiveLog);
		activeLogColumn.setToolTipText(Messages.columnActiveLog);
		activeLogColumn.setWidth(90);

		TableColumn archiveLogColumn = new TableColumn(
				volumeTableViewer.getTable(), SWT.LEFT);
		archiveLogColumn.setText(Messages.columnArchiveLog);
		archiveLogColumn.setToolTipText(Messages.columnArchiveLog);
		archiveLogColumn.setWidth(90);

		volumeInfoItem.setControl(volumeComposite);

		volumeInfoItem.setHeight(100);
		volumeInfoItem.setExpanded(true);
	}

	private void createBrokerInfoItem(ExpandBar bar, int index) {
		ExpandItem brokerInfoItem = new ExpandItem(bar, SWT.NONE, index);
		brokerInfoItem.setText(Messages.titleBrokerInfo);

		Composite brokerComposite = new Composite(bar, SWT.None);
		brokerComposite.setLayout(new FillLayout());
		brokerTableViewer = new TableViewer(brokerComposite, SWT.BORDER
				| SWT.FULL_SELECTION);
		brokerTableViewer.getTable().setHeaderVisible(true);
		brokerTableViewer.getTable().setLinesVisible(true);
		brokerTableViewer.setContentProvider(new BrokersStatusContentProvider());
		BrokersStatusLabelProvider brokersStatusLabelProvider = new BrokersStatusLabelProvider();
		brokersStatusLabelProvider.setServerInfo(serverInfo);
		brokerTableViewer.setLabelProvider(brokersStatusLabelProvider);

		TableColumn tblColumn = new TableColumn(brokerTableViewer.getTable(),
				SWT.LEFT);
		tblColumn.setText(Messages.tblBrokerName);
		tblColumn.setToolTipText(Messages.tipBrokerName);

		tblColumn = new TableColumn(brokerTableViewer.getTable(), SWT.CENTER);
		tblColumn.setText(Messages.tblBrokerStatus);
		tblColumn.setToolTipText(Messages.tipBrokerStatus);

		tblColumn = new TableColumn(brokerTableViewer.getTable(), SWT.LEFT);
		tblColumn.setText(Messages.tblBrokerProcess);
		tblColumn.setToolTipText(Messages.tipBrokerProcess);

		tblColumn = new TableColumn(brokerTableViewer.getTable(), SWT.LEFT);
		tblColumn.setText(Messages.tblPort);
		tblColumn.setToolTipText(Messages.tipPort);

		tblColumn = new TableColumn(brokerTableViewer.getTable(), SWT.LEFT);
		tblColumn.setText(Messages.tblServer);
		tblColumn.setToolTipText(Messages.tipServer);

		tblColumn = new TableColumn(brokerTableViewer.getTable(), SWT.LEFT);
		tblColumn.setText(Messages.tblQueue);
		tblColumn.setToolTipText(Messages.tipQueue);

		tblColumn = new TableColumn(brokerTableViewer.getTable(), SWT.LEFT);
		tblColumn.setText(Messages.tblRequest);
		tblColumn.setToolTipText(Messages.tipRequest);

		tblColumn = new TableColumn(brokerTableViewer.getTable(), SWT.LEFT);
		tblColumn.setText(Messages.tblTps);
		tblColumn.setToolTipText(Messages.tipTps);

		tblColumn = new TableColumn(brokerTableViewer.getTable(), SWT.LEFT);
		tblColumn.setText(Messages.tblQps);
		tblColumn.setToolTipText(Messages.tipQps);

		tblColumn = new TableColumn(brokerTableViewer.getTable(), SWT.LEFT);
		tblColumn.setText(Messages.tblLongTran);
		tblColumn.setToolTipText(Messages.tipLongTran);

		tblColumn = new TableColumn(brokerTableViewer.getTable(), SWT.LEFT);
		tblColumn.setText(Messages.tblLongQuery);
		tblColumn.setToolTipText(Messages.tipLongQuery);

		tblColumn = new TableColumn(brokerTableViewer.getTable(), SWT.LEFT);
		tblColumn.setText(Messages.tblErrQuery);
		tblColumn.setToolTipText(Messages.tipErrQuery);

		brokerInfoItem.setHeight(120);
		brokerInfoItem.setExpanded(true);
		brokerInfoItem.setControl(brokerComposite);
	}

	private void createServerStatusItem(ExpandBar bar, int index) {
		/* Server information */
		ExpandItem serverInfoItem = new ExpandItem(bar, SWT.NONE, index);
		serverInfoItem.setText(Messages.titleSystemInfo);

		Composite serverComposite = new Composite(bar, SWT.None);
		serverComposite.setLayout(new FillLayout());
		serverTableViewer = new TableViewer(serverComposite, SWT.BORDER
				| SWT.FULL_SELECTION);
		serverTableViewer.getTable().setHeaderVisible(true);
		serverTableViewer.getTable().setLinesVisible(true);

		serverTableViewer.setContentProvider(new TableContentProvider());
		serverTableViewer.setLabelProvider(new ServerStatusLabelProvider());

		TableColumn cpuTypeColumn = new TableColumn(
				serverTableViewer.getTable(), SWT.LEFT);
		cpuTypeColumn.setText(Messages.columnType);
		cpuTypeColumn.setToolTipText(Messages.columnType);
		cpuTypeColumn.setWidth(90);

		TableColumn memoryColumn = new TableColumn(
				serverTableViewer.getTable(), SWT.LEFT);
		memoryColumn.setText(Messages.columnMemmory);
		memoryColumn.setToolTipText(Messages.columnMemmory);
		memoryColumn.setWidth(120);

		TableColumn freespaceColumn = new TableColumn(
				serverTableViewer.getTable(), SWT.LEFT);
		freespaceColumn.setText(Messages.columnFreespace);
		freespaceColumn.setToolTipText(Messages.tipFreespace);
		freespaceColumn.setWidth(100);

		TableColumn cpuColumn = new TableColumn(serverTableViewer.getTable(),
				SWT.LEFT);
		cpuColumn.setText(Messages.columnCpu);
		cpuColumn.setToolTipText(Messages.columnCpu);
		cpuColumn.setWidth(80);

		TableColumn tpsColumn = new TableColumn(serverTableViewer.getTable(),
				SWT.LEFT);
		tpsColumn.setText(Messages.columnTps);
		tpsColumn.setToolTipText(Messages.tipTps);
		tpsColumn.setWidth(100);

		TableColumn qpsColumn = new TableColumn(serverTableViewer.getTable(),
				SWT.LEFT);
		qpsColumn.setText(Messages.columnQps);
		qpsColumn.setToolTipText(Messages.tipQps);
		qpsColumn.setWidth(100);

		serverInfoItem.setHeight(80);
		serverInfoItem.setExpanded(true);
		serverInfoItem.setControl(serverComposite);
	}
//
//	public void setServerInfo(ServerInfo serverInfo) {
//
//		this.serverInfo = serverInfo;
//
//		brokerTableViewer.setContentProvider(new BrokersStatusContentProvider());
//		BrokersStatusLabelProvider brokersStatusLabelProvider = new BrokersStatusLabelProvider();
//		brokersStatusLabelProvider.setServerInfo(serverInfo);
//		brokerTableViewer.setLabelProvider(brokersStatusLabelProvider);
//
//		if (bar != null && bar.getItemCount() > 2) {
//			int height = brokerTableViewer.getTable().computeSize(SWT.DEFAULT,
//					SWT.DEFAULT).y;
//			if (height < 80) {
//				height = 80;
//			}
//			bar.getItem(1).setHeight(height);
//		}
//
//		if (dataGeneratorThread != null) {
//			dataGeneratorThread.setRunFlag(false);
//			dataGeneratorThread = null;
//		}
//	}

	public ServerInfo getServerInfo() {
		return serverInfo;
	}

	public void loadAllData() {
		if (serverInfo == null) {
			return;
		}
		finishedCount = 0;
		isDirty = false;
		updateToolBar();

		try {
			loadDbServerData();
			loadDbData();
			loadBrokerData();
			loadServerData();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			ex.printStackTrace();
		}
	}

	/**
	 * Update the toolbar status
	 *
	 * @param isAvailable
	 */
	private void updateToolBar() {
		boolean isAvailable = false;
		if(finishedCount >= TOTAL_TASK_COUNT) {
			isAvailable = true;
		}
		refreshItem.setEnabled(isAvailable);
		exportItem.setEnabled(isAvailable);

		saveItem.setEnabled(isDirty);
	}

	public void loadDbServerData() {
		final GetEnvInfoTask getEnvInfoTask = new GetEnvInfoTask(serverInfo);
		TaskJobExecutor taskJobExec = new CommonTaskJobExec(
				new ITaskExecutorInterceptor() {

					public void completeAll() {
						EnvInfo envInfo = getEnvInfoTask.loadEnvInfo();
						StringBuilder sb = new StringBuilder();

						if (envInfo != null) {
							sb.append(" ").append(
									Messages.lblHost
											+ serverInfo.getHostAddress() + ":"
											+ serverInfo.getHostMonPort()).append(
									StringUtil.NEWLINE);
							sb.append(" ").append(
									Messages.lblDBVersion
											+ envInfo.getServerVersion()).append(
									StringUtil.NEWLINE);
							sb.append(" ").append(
									Messages.lblBrokerVersion
											+ envInfo.getBrokerVersion()).append(
									StringUtil.NEWLINE);
							sb.append(" ").append(
									Messages.lblCubridPath
											+ envInfo.getRootDir()).append(
									StringUtil.NEWLINE);
							sb.append(" ").append(
									Messages.lblDBPath
											+ envInfo.getDatabaseDir()).append(
									StringUtil.NEWLINE);

						}
						setDbServerData(sb.toString());
						finishedCount ++;
						updateToolBar();
					}

					public IStatus postTaskFinished(ITask task) {
						return Status.OK_STATUS;
					}

				});

		taskJobExec.addTask(getEnvInfoTask);

		String serverName = serverInfo.getServerName();
		String jobName = Messages.taskGetServerInfo + serverName;
		JobFamily jobFamily = new JobFamily();
		jobFamily.setServerName(serverName);
		taskJobExec.schedule(jobName, jobFamily, false, Job.SHORT);
	}

	public void loadDbData() {
		final GetDatabaseListTask getDatabaseListTask = new GetDatabaseListTask(
				serverInfo);
		final GetCubridConfParameterTask getCubridConfParameterTask = new GetCubridConfParameterTask(
				serverInfo);
		TaskJobExecutor taskJobExec = new CommonTaskJobExec(
				new ITaskExecutorInterceptor() {

					public void completeAll() {
						databaseInfoList = getDatabaseListTask.loadDatabaseInfo();
						List<String> orignAutoStartDBList = getCubridConfParameterTask.getAutoStartDb(false);
						setDatabaseData(databaseInfoList, orignAutoStartDBList);
						finishedCount ++;
						updateToolBar();
						//after get database data ,load volumn data
						loadVolumeData();
					}

					public IStatus postTaskFinished(ITask task) {
						return Status.OK_STATUS;
					}
				});
		taskJobExec.addTask(getDatabaseListTask);
		taskJobExec.addTask(getCubridConfParameterTask);

		String serverName = serverInfo.getServerName();
		String jobName = Messages.taskGetDBInfo + serverName;
		JobFamily jobFamily = new JobFamily();
		jobFamily.setServerName(serverName);
		taskJobExec.schedule(jobName, jobFamily, false, Job.SHORT);
	}

	public void loadVolumeData() {
		//if no database on this server ,don't get volumn data
		if (serverInfo.getAllDatabaseList().size() == 0
				|| databaseInfoList == null) {
			return;
		}

		final List<CommonQueryTask<DbSpaceInfoList>> getVolumnTaskList = new ArrayList<CommonQueryTask<DbSpaceInfoList>>();
		if (serverInfo.getAllDatabaseList() != null) {
			for (String dbName : serverInfo.getAllDatabaseList()) {
				for (DatabaseInfo databaseInfo : databaseInfoList) {
					// only load start database volumn data
					if (dbName.equals(databaseInfo.getDbName())
							&& databaseInfo.getRunningType() == DbRunningType.CS) {
								CommonQueryTask<DbSpaceInfoList> task = new CommonQueryTask<DbSpaceInfoList>(
										serverInfo, CommonSendMsg.getCommonDatabaseSendMsg(),
										new DbSpaceInfoList());
								task.setDbName(dbName);
								getVolumnTaskList.add(task);
								break;
					}
				}
			}
		}
		//if no start database don't get volumn data
		if (getVolumnTaskList.size() == 0) {
			return;
		}
		TaskJobExecutor taskJobExec = new CommonTaskJobExec(
				new ITaskExecutorInterceptor() {

					public void completeAll() {
						List<DBVolumeSpaceInfo> dbVolumeSpaceInfoList = new ArrayList<DBVolumeSpaceInfo>();

						for (CommonQueryTask<DbSpaceInfoList> task : getVolumnTaskList) {
							DbSpaceInfoList dbSpaceInfoList = task.getResultModel();

							freespaceOnStorage = ((long) dbSpaceInfoList.getFreespace()) * 1024l * 1024l;

							DBVolumeSpaceInfo dbSpaceInfo = new DBVolumeSpaceInfo(
									dbSpaceInfoList.getDbname());
							dbSpaceInfo.setPageSize(dbSpaceInfoList.getPagesize());
							dbVolumeSpaceInfoList.add(dbSpaceInfo);

							if (dbSpaceInfoList != null) {
								for (DbSpaceInfo spaceInfo : dbSpaceInfoList.getSpaceinfo()) {
									dbSpaceInfo.addVolumeSpaceInfo(spaceInfo);
								}
							}
						}
						setVolumeData(dbVolumeSpaceInfoList);
						finishedCount ++;
						updateToolBar();
					}

					public IStatus postTaskFinished(ITask task) {
						return Status.OK_STATUS;
					}

				});

		for (CommonQueryTask<DbSpaceInfoList> task : getVolumnTaskList) {
			taskJobExec.addTask(task);
		}

		String serverName = serverInfo.getServerName();
		String jobName = Messages.taskGetVolumeInfo + serverName;
		JobFamily jobFamily = new JobFamily();
		jobFamily.setServerName(serverName);
		taskJobExec.schedule(jobName, jobFamily, false, Job.SHORT);
	}

	public void loadBrokerData() {
		BrokerInfos brokerInfos = new BrokerInfos();
		final CommonQueryTask<BrokerInfos> task = new CommonQueryTask<BrokerInfos>(
				serverInfo, CommonSendMsg.getCommonSimpleSendMsg(), brokerInfos);
		TaskJobExecutor taskJobExec = new CommonTaskJobExec(
				new ITaskExecutorInterceptor() {

					public void completeAll() {
						BrokerInfos brokerInfos = task.getResultModel();
						List<BrokerInfo> newBrokerInfoList = null;
						if (null != brokerInfos) {
							BrokerInfoList list = brokerInfos.getBorkerInfoList();
							if (list != null
									&& list.getBrokerInfoList() != null) {
								newBrokerInfoList = list.getBrokerInfoList();
							}
						}
						setBrokerData(newBrokerInfoList);
						finishedCount ++;
						updateToolBar();
					}

					public IStatus postTaskFinished(ITask task) {
						return Status.OK_STATUS;
					}
				});

		taskJobExec.addTask(task);

		String serverName = serverInfo.getServerName();
		String jobName = Messages.taskGetBrokerInfo + serverName;
		JobFamily jobFamily = new JobFamily();
		jobFamily.setServerName(serverName);
		taskJobExec.schedule(jobName, jobFamily, false, Job.SHORT);
	}

	public void loadServerData() {
		if (dataGeneratorThread != null) {
			dataGeneratorThread.setRunFlag(false);
		}
		dataGeneratorThread = new DataGeneratorThread(serverInfo, this);
		dataGeneratorThread.start();
	}

	private void setDbServerData(final String content) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				if(dbServerInfoText.isDisposed()) {
					return;
				}
				dbServerInfoText.setText(content);
				infoLable.setText(Messages.bind(Messages.lblHostInfo,
						serverInfo.getServerName()));
			}
		});
	}

	private void setVolumeData(
			final List<DBVolumeSpaceInfo> dbVolumeSpaceInfoList) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				if(volumeTableViewer.getTable().isDisposed()) {
					return;
				}
				volumeTableViewer.setInput(dbVolumeSpaceInfoList);
				volumeTableViewer.refresh();
				if (bar != null && bar.getItemCount() > 1) {
					int height = volumeTableViewer.getTable().computeSize(
							SWT.DEFAULT, SWT.DEFAULT).y;
					if (height < 80) {
						height = 80;
					}
					bar.getItem(0).setHeight(height);
				}
			}
		});
	}

	private void setDatabaseData(final List<DatabaseInfo> databaseInfoList,
			final List<String> orignAutoStartDBList) {
		for (TableEditor editor : checkEditors) {
			editor.getEditor().dispose();
		}
		checkEditors.clear();
		databaseTable.removeAll();

		if (databaseInfoList != null) {
			for (int i = 0; i < databaseInfoList.size(); i++) {
				DatabaseInfo dbInfo = databaseInfoList.get(i);
				final TableItem item = new TableItem(databaseTable, SWT.None);
				item.setText(0, dbInfo.getDbName());

				TableEditor editor = null;

				/* check box */
				editor = new TableEditor(databaseTable);
				final Button check = new Button(databaseTable, SWT.CHECK);
				check.setBackground(Display.getCurrent().getSystemColor(
						SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
				check.setData(i);
				check.setFocus();
				check.pack();
				boolean isChecked = orignAutoStartDBList.contains(dbInfo.getDbName());
				check.setSelection(isChecked);
				item.setData("isChecked", isChecked);
				editor.minimumWidth = check.getSize().x;
				editor.horizontalAlignment = SWT.CENTER;
				editor.setEditor(check, item, 1);
				checkEditors.add(editor);
				check.addSelectionListener(new SelectionListener() {
					public void widgetSelected(SelectionEvent e) {
						widgetDefaultSelected(e);
					}

					public void widgetDefaultSelected(SelectionEvent e) {
						databaseTable.getItem((Integer) check.getData()).setData(
								"isChecked", check.getSelection());
						/*Update save item*/
						Set<String> newSet = getNewAutoStartDB();
						if (newSet.size() != orignAutoStartDBList.size()) {
							isDirty = true;
						} else {
							boolean isChanged = false;
							for (String dbName : orignAutoStartDBList) {
								if (!newSet.contains(dbName)) {
									isChanged = true;
									break;
								}
							}
							isDirty = isChanged;
						}
						updateToolBar();
					}
				});

				/* Status */
				if (dbInfo.getRunningType().equals(DbRunningType.STANDALONE)) {
					item.setText(2, Messages.lblStoped);
				} else {
					item.setText(2, Messages.lblRunning);
				}
			}

			if (bar != null && bar.getItemCount() > 4) {
				int height = databaseTable.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
				if (height < 80) {
					height = 80;
				}
				bar.getItem(3).setHeight(height);
			}
		}
	}

	private Set<String> getNewAutoStartDB() {
		Set<String> set = new HashSet<String>();
		for (TableItem item : databaseTable.getItems()) {
			if ((Boolean) item.getData("isChecked")) {
				set.add(item.getText(0));
			}
		}

		return set;
	}

	private void saveAutoParam(Set<String> settingAutoStartDB) {

		GetCubridConfParameterTask getCubridConfParameterTask = new GetCubridConfParameterTask(
				serverInfo);
		getCubridConfParameterTask.execute();
		if (!getCubridConfParameterTask.isSuccess()) {
			CommonUITool.openInformationBox(
					com.cubrid.cubridmanager.ui.common.Messages.titleError,
					getCubridConfParameterTask.getErrorMsg());
			return;
		}

		List<String> cubridConfContentList = getCubridConfParameterTask.getConfContents();
		List<String> currentAutoStartDBList = getCubridConfParameterTask.getAutoStartDb(false);

		/*Merge the data*/
		for (String db : currentAutoStartDBList) {
			if (!settingAutoStartDB.contains(db)) {
				cubridConfContentList = CoreUtils.deleteDatabaseFromServiceServer(
						getCubridConfParameterTask, cubridConfContentList, db);
			}
		}
		for (String db : settingAutoStartDB) {
			if (!currentAutoStartDBList.contains(db)) {
				cubridConfContentList = CoreUtils.addDatabaseToServiceServer(
						getCubridConfParameterTask, cubridConfContentList, db);
			}
		}
		/*Update the config*/
		SetCubridConfParameterTask setParaTask = new SetCubridConfParameterTask(
				serverInfo);
		setParaTask.setConfContents(cubridConfContentList);
		CommonTaskExec taskExcutor = new CommonTaskExec(
				com.cubrid.cubridmanager.ui.common.Messages.setCubridParameterTaskName);
		taskExcutor.addTask(setParaTask);
		new ExecTaskWithProgress(taskExcutor).exec(true, true);
		if (taskExcutor.isSuccess()) {
			isDirty = false;
			updateToolBar();
			CommonUITool.openInformationBox(
					com.cubrid.cubridmanager.ui.common.Messages.titleSuccess,
					com.cubrid.cubridmanager.ui.common.Messages.msgChangeServiceParaSuccess);
		}
	}

	private void setBrokerData(final List<BrokerInfo> brokerInfoList) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				if(brokerTableViewer.getTable().isDisposed()) {
					return;
				}
 				brokerTableViewer.setInput(brokerInfoList);
				CommonUITool.packTable(brokerTableViewer.getTable(), 0, 200);

				if (bar != null && bar.getItemCount() > 2) {
					int height = databaseTable.computeSize(SWT.DEFAULT,
							SWT.DEFAULT).y;
					if (height < 80) {
						height = 80;
					}
					bar.getItem(1).setHeight(height);
				}
			}
		});
	}

	/**
	 * Disposes this view when it closed
	 */
	public void dispose() {
		synchronized (this) {
			if (dataGeneratorThread != null) {
				dataGeneratorThread.setRunFlag(false);
			}
			super.dispose();
		}
	}

	public TableViewer getServerTableViewer() {
		return serverTableViewer;
	}

	/**
	 * Perform node change event
	 */
	public void nodeChanged(CubridNodeChangedEvent event) {
		ICubridNode cubridNode = event.getCubridNode();
		CubridNodeChangedEventType eventType = event.getType();
		if (cubridNode == null || eventType == null) {
			return;
		}
		if (NodeType.SERVER.equals(cubridNode.getType())) {
			if (CubridNodeChangedEventType.SERVER_DISCONNECTED.equals(eventType)
					|| CubridNodeChangedEventType.NODE_REMOVE.equals(eventType)) {
				if (serverInfo != null
						&& StringUtil.isEqualNotIgnoreNull(
								cubridNode.getName(),
								serverInfo.getServerName())) {
					IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					if (window == null) {
						return;
					}

					window.getActivePage().closeEditor(this, true);
				}
			}
		}
	}

	public void doSave(IProgressMonitor monitor) {
		saveAutoParam(getNewAutoStartDB());
	}

	public void doSaveAs() {
	}

	public boolean isDirty() {
		return isDirty;
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public StyledText getDbServerInfoText() {
		return dbServerInfoText;
	}

	public TableViewer getVolumeTableViewer() {
		return volumeTableViewer;
	}

	public Table getDatabaseTable() {
		return databaseTable;
	}

	public TableViewer getBrokerTableViewer() {
		return brokerTableViewer;
	}

	public long getFreespaceOnStorage() {
		return freespaceOnStorage;
	}

	public void setFreespaceOnStorage(int freespaceOnStorage) {
		this.freespaceOnStorage = freespaceOnStorage;
	}

}

/**
 *
 * The Data Generator Thread
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-10-22 created by Kevin.Wang
 */
class DataGeneratorThread extends
		Thread {
	private ServerInfo serverInfo;
	private HostDashboardEditor editorPart;

	private int startRun = 0;
	private static HostStatData diagOldOneStatusResult = new HostStatData();
	private static HostStatData diagOldTwoStatusResult = new HostStatData();
	private HostStatData diagStatusResult = new HostStatData();
	private boolean interruptReq = true;
	private HostStatDataProxy hostStatDataProxy = new HostStatDataProxy();
	private HostDataManager hostDataManager = new HostDataManager();
	private volatile boolean runFlag = true;
	private CommonQueryTask<BrokerInfos> task = null;

	/*Unit : millisecond*/
	private int smallSpaceTime = 1000;
	private int bigSpaceTime = 30000;
	private int timeout = 60 * 5 * 1000;

	public DataGeneratorThread(ServerInfo serverInfo,
			HostDashboardEditor editorPart) {
		this.serverInfo = serverInfo;
		this.editorPart = editorPart;

		BrokerInfos brokerInfos = new BrokerInfos();
		task = new CommonQueryTask<BrokerInfos>(serverInfo,
				CommonSendMsg.getCommonSimpleSendMsg(), brokerInfos);
	}

	/**
	 * Thread run method
	 */
	public void run() {
		int sleepTime = smallSpaceTime;
		long startTime = System.currentTimeMillis();

		while (runFlag) {
			// It should be stopped when it have exceed 5 minutes because of server load
			if (System.currentTimeMillis() - startTime > timeout) {
				break;
			}

			getUpdateValue(startRun);

			if (startRun == 0) {
				startRun++;
				continue;
			}

			if (startRun < 15) {
				startRun++;
			} else {
				sleepTime = bigSpaceTime;
			}

			HostStatus hostStatus = new HostStatus();
			hostStatus.setCpu(StringUtil.doubleValue(
					hostStatDataProxy.getUserPercent(), 0));
			hostStatus.setMemoryUsed(StringUtil.doubleValue(
					hostStatDataProxy.getMemPhyUsed(), 0));
			hostStatus.setMemoryPhy(StringUtil.doubleValue(
					hostStatDataProxy.getMemPhyTotal(), 0));

			task.execute();
			BrokerInfos brokerInfos = task.getResultModel();

			double qps = 0;
			double tps = 0;
			List<BrokerInfo> newBrokerInfoList = null;
			if (null != brokerInfos) {
				BrokerInfoList list = brokerInfos.getBorkerInfoList();
				if (list != null && list.getBrokerInfoList() != null) {
					newBrokerInfoList = list.getBrokerInfoList();
				}
			}
			if (newBrokerInfoList != null) {
				for (BrokerInfo brokerInfo : newBrokerInfoList) {
					tps += StringUtil.doubleValue(brokerInfo.getTran(), 0);
					qps += StringUtil.doubleValue(brokerInfo.getQuery(), 0);
				}
			}

			hostStatus.setTps(tps);
			hostStatus.setQps(qps);

			hostDataManager.addData(hostStatus);
			final List<HostStatus> serverDataList = new ArrayList<HostStatus>();

			HostStatus hostStatusNow = new HostStatus();
			hostStatusNow.setLabel(Messages.lblNow);
			hostStatusNow.setMemoryUsed(1.0d * hostDataManager.getMemeryUsed());
			hostStatusNow.setMemoryPhy(1.0d * hostDataManager.getMemoryPhy());
			hostStatusNow.setCpu(hostDataManager.getCpu());
			hostStatusNow.setQps(hostDataManager.getQps());
			hostStatusNow.setTps(hostDataManager.getTps());
			hostStatusNow.setFreespaceOnStorage(editorPart.getFreespaceOnStorage());
			serverDataList.add(hostStatusNow);

			HostStatus hostStatusAvg = new HostStatus();
			hostStatusAvg.setLabel(Messages.lbl5MinAvg);
			hostStatusAvg.setMemoryUsed(hostDataManager.getAvgMemeryUsed());
			hostStatusAvg.setMemoryPhy(1.0d * hostDataManager.getMemoryPhy());
			hostStatusAvg.setCpu(hostDataManager.getAvgCpu());
			hostStatusAvg.setQps(hostDataManager.getAvgQps());
			hostStatusAvg.setTps(hostDataManager.getAvgTps());
			hostStatusAvg.setFreespaceOnStorage(-1);
			serverDataList.add(hostStatusAvg);

			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					if (editorPart.getServerTableViewer() != null
							&& !editorPart.getServerTableViewer().getTable().isDisposed()) {
						editorPart.getServerTableViewer().setInput(
								serverDataList);
						editorPart.getServerTableViewer().refresh();
					}
				}
			});

			try {
				Thread.sleep(sleepTime);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Get update value
	 *
	 * @param startRun int
	 * @return Map<String, String>
	 */
	private void getUpdateValue(int startRun) {

		final CommonQueryTask<HostStatData> task = new CommonQueryTask<HostStatData>(
				serverInfo, CommonSendMsg.getCommonSimpleSendMsg(),
				diagStatusResult);
		task.execute();

		if (startRun == 0) {
			diagStatusResult = task.getResultModel();
			if (!diagStatusResult.getStatus()) {
				if (interruptReq) {
					hostStatDataProxy.getDiagStatusResultMap();
				} else {
					showErrorMsg(diagStatusResult);
				}
			}
		} else if (startRun == 1) {
			diagOldOneStatusResult.copyFrom(diagStatusResult);
			diagStatusResult = task.getResultModel();
			if (!diagStatusResult.getStatus()) {
				if (!interruptReq) {
					showErrorMsg(diagStatusResult);
				}
			}
			hostStatDataProxy.compute(diagStatusResult, diagOldOneStatusResult);
		} else {

			diagOldTwoStatusResult.copyFrom(diagOldOneStatusResult);
			diagOldOneStatusResult.copyFrom(diagStatusResult);
			diagStatusResult = task.getResultModel();
			if (!diagStatusResult.getStatus()) {
				if (!interruptReq) {
					showErrorMsg(diagStatusResult);
				}
			}
			hostStatDataProxy.compute(diagStatusResult, diagOldOneStatusResult,
					diagOldTwoStatusResult);

		}
	}

	/**
	 * Show error message When the response' status is not success
	 *
	 * @param diagStatusResult this instance of BrokerDiagData
	 */
	private void showErrorMsg(final HostStatData diagStatusResult) {
		interruptReq = true;
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				CommonUITool.openErrorBox(diagStatusResult.getNote());
			}
		});
	}

	public void setRunFlag(boolean runFlag) {
		this.runFlag = runFlag;
	}
}

class HostDataManager {

	/* 5 min */
	private long maxSavedTime = 5 * 60 * 1000;

	private HostStatus lastHostStatus;
	private List<HostStatus> history = new ArrayList<HostStatus>();

	public void addData(HostStatus hostStatus) {
		lastHostStatus = hostStatus;
		history.add(hostStatus);

		/* Remove the data which is time out */
		Iterator<HostStatus> iter = history.iterator();
		while (iter.hasNext()) {
			HostStatus temp = iter.next();

			if (System.currentTimeMillis() - temp.getCreateTime().getTime() > maxSavedTime) {
				iter.remove();
			}
		}
	}

	public void clearData() {
		lastHostStatus = null;
		history.clear();
	}

	public double getMemeryUsed() {
		if (lastHostStatus == null) {
			return 0;
		}
		return lastHostStatus.getMemoryUsed();
	}

	public double getAvgMemeryUsed() {
		Iterator<HostStatus> iter = history.iterator();
		double memory = 0;
		int count = 0;
		while (iter.hasNext()) {
			HostStatus temp = iter.next();

			memory += temp.getMemoryUsed();
			count++;
		}

		if (count > 0) {
			return memory / count;
		}
		return 0;
	}

	public double getMemoryPhy() {
		if (lastHostStatus == null) {
			return 0;
		}
		return lastHostStatus.getMemoryPhy();
	}

	public double getCpu() {
		if (lastHostStatus == null) {
			return 0;
		}
		return lastHostStatus.getCpu();
	}

	public double getAvgCpu() {
		Iterator<HostStatus> iter = history.iterator();
		double cpu = 0;
		int count = 0;
		while (iter.hasNext()) {
			HostStatus temp = iter.next();

			cpu += temp.getCpu();
			count++;
		}

		if (count > 0) {
			return cpu / count;
		}
		return 0;
	}

	public double getTps() {
		if (lastHostStatus == null) {
			return 0;
		}
		return lastHostStatus.getTps();
	}

	public double getAvgTps() {
		Iterator<HostStatus> iter = history.iterator();
		double tps = 0;
		int count = 0;
		while (iter.hasNext()) {
			HostStatus temp = iter.next();

			tps += temp.getTps();
			count++;
		}

		if (count > 0) {
			return tps / count;
		}
		return 0;
	}

	public double getQps() {
		if (lastHostStatus == null) {
			return 0;
		}
		return lastHostStatus.getQps();
	}

	public double getAvgQps() {
		Iterator<HostStatus> iter = history.iterator();
		double qps = 0;
		int count = 0;
		while (iter.hasNext()) {
			HostStatus temp = iter.next();

			qps += temp.getQps();
			count++;
		}

		if (count > 0) {
			return qps / count;
		}
		return 0;
	}
}

/**
 *
 * The Host Status Model
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-10-22 created by Kevin.Wang
 */
class HostStatus {

	private String label;
	private double memoryUsed;
	private double memoryPhy;
	private double cpu;
	private double tps;
	private double qps;
	private long freespaceOnStorage;

	private Date createTime = new Date();

	/**
	 * The default constructor
	 */
	HostStatus() {
	}

	/**
	 * The constructor
	 *
	 * @param cpu
	 * @param memoryUsed
	 * @param memoryPhy
	 * @param tps
	 * @param qps
	 */
	HostStatus(double cpu, double memoryUsed, double memoryPhy, double tps,
			double qps) {
		this.cpu = cpu;
		this.memoryUsed = memoryUsed;
		this.memoryPhy = memoryPhy;
		this.tps = tps;
		this.qps = qps;
	}

	public long getFreespaceOnStorage() {
		return freespaceOnStorage;
	}

	public void setFreespaceOnStorage(long freespaceOnStorage) {
		this.freespaceOnStorage = freespaceOnStorage;
	}

	public double getMemoryUsed() {
		return memoryUsed;
	}

	public void setMemoryUsed(double memoryUsed) {
		this.memoryUsed = memoryUsed;
	}

	public double getMemoryPhy() {
		return memoryPhy;
	}

	public void setMemoryPhy(double memoryPhy) {
		this.memoryPhy = memoryPhy;
	}

	public double getCpu() {
		return cpu;
	}

	public void setCpu(double cpu) {
		this.cpu = cpu;
	}

	public double getTps() {
		return tps;
	}

	public void setTps(double tps) {
		this.tps = tps;
	}

	public double getQps() {
		return qps;
	}

	public void setQps(double qps) {
		this.qps = qps;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}

/**
 *
 * The Server Status LabelProvider
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-10-22 created by Kevin.Wang
 */
class ServerStatusLabelProvider implements
		ITableLabelProvider {
	private static NumberFormat formater = NumberFormat.getInstance();
	static {
		formater.setMaximumFractionDigits(2);
	}

	public void addListener(ILabelProviderListener arg0) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object arg0, String arg1) {
		return true;
	}

	public void removeListener(ILabelProviderListener arg0) {
	}

	public Image getColumnImage(Object arg0, int arg1) {
		return null;
	}

	public String getColumnText(Object obj, int index) {
		if (obj instanceof HostStatus) {
			HostStatus hostStatus = (HostStatus) obj;
			switch (index) {
			case 0:
				return hostStatus.getLabel();
			case 1:
				return formater.format(StringUtil.convertToG(new Double(
						hostStatus.getMemoryUsed()).longValue() * 1024))
						+ "GB / "
						+ formater.format(StringUtil.convertToG(new Double(
								hostStatus.getMemoryPhy()).longValue() * 1024))
						+ "GB";
			case 2:
				return hostStatus.getFreespaceOnStorage() < 0 ? "-"
						: DBSpaceLabelProvider.getSpaceDesc(hostStatus.getFreespaceOnStorage());
			case 3:
				return formater.format(hostStatus.getCpu()) + "%";
			case 4:
				return formater.format(hostStatus.getTps());
			case 5:
				return formater.format(hostStatus.getQps());
			}
		}
		return null;
	}
}

/**
 *
 * The DB Volume Space Info Model
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-10-22 created by Kevin.Wang
 */
class DBVolumeSpaceInfo {
	private String dbName;
	private long pageSize;
	private Map<String, List<DbSpaceInfo>> volumeSpaceInfoMap = new HashMap<String, List<DbSpaceInfo>>();

	DBVolumeSpaceInfo(String dbName) {
		this.dbName = dbName;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public long getPageSize() {
		return pageSize;
	}

	public void setPageSize(long pageSize) {
		this.pageSize = pageSize;
	}

	public void addVolumeSpaceInfo(DbSpaceInfo spaceInfo) {
		String type = spaceInfo.getType();
		List<DbSpaceInfo> spaceInfoList = volumeSpaceInfoMap.get(type);
		if (spaceInfoList == null) {
			spaceInfoList = new ArrayList<DbSpaceInfo>();
			volumeSpaceInfoMap.put(type, spaceInfoList);
		}
		spaceInfoList.add(spaceInfo);
	}

	public List<DbSpaceInfo> getVolumeSpaceInfo(String type) {
		return volumeSpaceInfoMap.get(type);
	}
}

/**
 *
 * The DBS pace LabelProvider
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-10-22 created by Kevin.Wang
 */
class DBSpaceLabelProvider implements
		ITableLabelProvider,
		ITableColorProvider {
	private static NumberFormat formater = NumberFormat.getInstance();

	private double noticeThreshold = 0.15;
	private double warningThreshold = 0.05;
	static {
		formater.setMaximumFractionDigits(1);
	}

	public void addListener(ILabelProviderListener arg0) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object arg0, String arg1) {
		return true;
	}

	public void removeListener(ILabelProviderListener arg0) {
	}

	public Image getColumnImage(Object arg0, int arg1) {
		return null;
	}

	public String getColumnText(Object obj, int index) {
		if (obj instanceof DBVolumeSpaceInfo) {
			DBVolumeSpaceInfo volumeSpaceInfo = (DBVolumeSpaceInfo) obj;
			switch (index) {
			case 0:
				return volumeSpaceInfo.getDbName();
			case 1:
				return getVolumeString(volumeSpaceInfo,
						VolumeType.DATA.getText());
			case 2:
				return getVolumeString(volumeSpaceInfo,
						VolumeType.INDEX.getText());
			case 3:
				return getVolumeString(volumeSpaceInfo,
						VolumeType.TEMP.getText());
			case 4:
				return getVolumeString(volumeSpaceInfo,
						VolumeType.GENERIC.getText());
			case 5:
				return getLogString(volumeSpaceInfo,
						VolumeType.ACTIVE_LOG.getText());
			case 6:
				return getLogString(volumeSpaceInfo,
						VolumeType.ARCHIVE_LOG.getText());
			}
		}
		return null;
	}

	private String getVolumeString(DBVolumeSpaceInfo volumeSpaceInfo,
			String type) { // FIXME extract
		long totalPage = 0;
		long freePage = 0;
		Date lastCreateDate = null;

		StringBuilder sb = new StringBuilder();
		List<DbSpaceInfo> dbSpaceInfoList = volumeSpaceInfo.getVolumeSpaceInfo(type);
		if (dbSpaceInfoList != null) {
			for (DbSpaceInfo dbSpaceInfo : dbSpaceInfoList) {
				totalPage += dbSpaceInfo.getTotalpage();
				freePage += dbSpaceInfo.getFreepage();
				Date createDate = null;
				try {
					createDate = DateUtil.getDateFormat("yyyyMMdd").parse(
							dbSpaceInfo.getDate());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (createDate != null
						&& (lastCreateDate == null || lastCreateDate.getTime() < createDate.getTime())) {
					lastCreateDate = createDate;
				}
			}
		}

		if (totalPage > 0) {
			sb.append(
					getSpaceDesc((totalPage - freePage)
							* volumeSpaceInfo.getPageSize())).append(" / ");
			sb.append(getSpaceDesc(totalPage * volumeSpaceInfo.getPageSize())).append(
					" / ");
			sb.append(new Double(freePage * 100.0d / totalPage).intValue()).append(
					"%");
		} else {
			sb.append("-");
		}

		return sb.toString();
	}

	public static String getSpaceDesc(long spaceSize) { // FIXME extract
		StringBuilder sb = new StringBuilder();
		if (spaceSize >= 1024 * 1024 * 1024) {
			sb.append(formater.format(StringUtil.convertToG(spaceSize))).append(
					"GB");
			return sb.toString();
		} else if (spaceSize >= 1024 * 1024) {
			sb.append(formater.format(StringUtil.convertToM(spaceSize))).append(
					"MB");
			return sb.toString();
		} else if (spaceSize >= 1024) {
			sb.append(formater.format((float) spaceSize / 1024f / 1024f)).append(
					"MB");
			return sb.toString();
		}
		sb.append(spaceSize).append("B");
		return sb.toString();
	}

	private String getLogString(DBVolumeSpaceInfo volumeSpaceInfo, String type) { // FIXME extract
		long totalPage = 0;

		List<DbSpaceInfo> dbSpaceInfoList = volumeSpaceInfo.getVolumeSpaceInfo(type);
		if (dbSpaceInfoList != null) {
			for (DbSpaceInfo dbSpaceInfo : dbSpaceInfoList) {
				totalPage += dbSpaceInfo.getTotalpage();
			}
		}

		return getSpaceDesc(totalPage * volumeSpaceInfo.getPageSize());
	}

	public Color getBackground(Object element, int columnIndex) {
		return null;
	}

	public Color getForeground(Object element, int columnIndex) {
		long totalPage = 0;
		long freePage = 0;

		String type = null;
		switch (columnIndex) {
		case 1:
			type = VolumeType.DATA.getText();
			break;
		case 2:
			type = VolumeType.INDEX.getText();
			break;
		case 3:
			type = VolumeType.TEMP.getText();
			break;
		case 4:
			type = VolumeType.GENERIC.getText();
			break;
		}

		if (type != null && element instanceof DBVolumeSpaceInfo) {
			DBVolumeSpaceInfo volumeSpaceInfo = (DBVolumeSpaceInfo) element;
			List<DbSpaceInfo> dbSpaceInfoList = volumeSpaceInfo.getVolumeSpaceInfo(type);
			if (dbSpaceInfoList != null) {
				for (DbSpaceInfo dbSpaceInfo : dbSpaceInfoList) {
					totalPage += dbSpaceInfo.getTotalpage();
					freePage += dbSpaceInfo.getFreepage();
				}
			}

			if (totalPage > 0 && freePage * 1.0d / totalPage < warningThreshold) {
				return ResourceManager.getColor(SWT.COLOR_RED);
			} else if (totalPage > 0
					&& freePage * 1.0d / totalPage < noticeThreshold) {
				return ResourceManager.getColor(SWT.COLOR_DARK_YELLOW);
			}
		}
		return null;
	}

}
