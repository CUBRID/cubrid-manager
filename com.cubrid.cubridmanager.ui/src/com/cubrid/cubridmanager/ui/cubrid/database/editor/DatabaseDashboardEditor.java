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
package com.cubrid.cubridmanager.ui.cubrid.database.editor;

import static com.cubrid.common.core.util.NoOp.noOp;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.common.preference.GeneralPreference;
import com.cubrid.common.ui.common.preference.ShowDashboardDialog;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.TableContentProvider;
import com.cubrid.common.ui.spi.TableLabelProvider;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultCubridNode;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.part.CubridEditorPart;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.progress.TaskJobExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.broker.model.ApplyServerInfo;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfo;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfoList;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfos;
import com.cubrid.cubridmanager.core.broker.model.BrokerStatusInfos;
import com.cubrid.cubridmanager.core.broker.task.GetBrokerStatusInfosTask;
import com.cubrid.cubridmanager.core.broker.task.RestartBrokerTask;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.task.CommonQueryTask;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.lock.DatabaseLockInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.lock.DatabaseTransaction;
import com.cubrid.cubridmanager.core.cubrid.database.model.lock.DbLotEntry;
import com.cubrid.cubridmanager.core.cubrid.database.model.lock.DbLotInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.lock.LockHolders;
import com.cubrid.cubridmanager.core.cubrid.database.model.lock.LockInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.transaction.DbTransactionList;
import com.cubrid.cubridmanager.core.cubrid.database.model.transaction.Transaction;
import com.cubrid.cubridmanager.core.cubrid.database.task.CheckFileTask;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfo;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoList;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.GetAddVolumeStatusInfo;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.VolumeType;
import com.cubrid.cubridmanager.core.logs.model.BrokerLogInfos;
import com.cubrid.cubridmanager.core.logs.model.LogContentInfo;
import com.cubrid.cubridmanager.core.logs.model.LogInfo;
import com.cubrid.cubridmanager.core.logs.task.GetLogListTask;
import com.cubrid.cubridmanager.core.monitoring.model.DbProcStat;
import com.cubrid.cubridmanager.core.monitoring.model.DbProcStatEnum;
import com.cubrid.cubridmanager.core.monitoring.model.DbProcStatProxy;
import com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData;
import com.cubrid.cubridmanager.core.monitoring.model.HostStatData;
import com.cubrid.cubridmanager.core.monitoring.model.HostStatDataProxy;
import com.cubrid.cubridmanager.core.monitoring.model.HostStatEnum;
import com.cubrid.cubridmanager.core.monitoring.model.IDiagPara;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;
import com.cubrid.cubridmanager.ui.cubrid.database.dialog.ExportDashboardDialog;
import com.cubrid.cubridmanager.ui.cubrid.database.dialog.KillTransactionDialog;
import com.cubrid.cubridmanager.ui.cubrid.dbspace.dialog.AddVolumeDialog;
import com.cubrid.cubridmanager.ui.logs.editor.LogEditorPart;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;

/**
 * Database dashboard editor
 *
 * @author fulei
 *
 * @version 1.0 - 2012-9-25 created by fulei
 */

public class DatabaseDashboardEditor extends
		CubridEditorPart {

	private static final Logger LOGGER = LogUtil.getLogger(DatabaseDashboardEditor.class);
	public static final String ID = "com.cubrid.cubridmanager.ui.cubrid.database.editor.DatabaseDashboardEditor";
	private CubridDatabase database = null;

	/*ToolBar Item*/
	private ToolItem settingItem;
	private ToolItem exportItem;
	private ToolItem autoRefreshItem;
	private Label infoLable;
	private Label errMsgLable;

	private TableViewer dbInfoTableViewer;
	private TableViewer volumnInfoTableViewer;
	private TableViewer brokerInfoTableViewer;
	private TableViewer lockAndTransactionTableViewer;
	private Table lockAndTransactionTable;
	private Table brokerInfoTable;

	private boolean interruptReq;
	private final List<Map<String, String>> dbInfoListData = new ArrayList<Map<String, String>>();
	private final List<Map<String, String>> volumnInfoListData = new ArrayList<Map<String, String>>();
	private final List<Map<String, String>> brokerInfoListData = new ArrayList<Map<String, String>>();
	private final List<Map<String, String>> lockAndTransactionListData = new ArrayList<Map<String, String>>();

	private List<HashMap<String, ApplyServerInfo>> asinfoLst = new ArrayList<HashMap<String, ApplyServerInfo>>();

	private static DbProcStat dbProcOldOneStatusResult = new DbProcStat();
	private static DbProcStat dbProcOldTwoStatusResult = new DbProcStat();
	private DbProcStat dbProcStatusResult = new DbProcStat();
	private DbProcStatProxy dbProcStatProxy = new DbProcStatProxy();

	private static HostStatData hostOldOneStatusResult = new HostStatData();
	private static HostStatData hostOldTwoStatusResult = new HostStatData();
	private HostStatData hostStatusResult = new HostStatData();
	private HostStatDataProxy hostStatDataProxy = new HostStatDataProxy();

	private static DbStatDumpData diagOldOneStatusResult = new DbStatDumpData();
	private static DbStatDumpData diagOldTwoStatusResult = new DbStatDumpData();
	private DbStatDumpData diagStatusResult = new DbStatDumpData();
	private Calendar lastSec;
	private Calendar nowSec;

	private DatabaseLockInfo databaseLockInfo;
	private DbTransactionList dbTransactionList;
	private NumberFormat formater = NumberFormat.getInstance();
	private String sqlLogViewPartName = "";
	private ExpandBar bar = null;
	private boolean autoRefreshData = false;
	private int autoRefreshTime = 1;// auto refresh data time (seconds)
	private RefreshDataThread refreshDataThread;
	private boolean runflag = true;
	private Map<TableViewer, Integer> tableViewOnBarIndexMap = new HashMap<TableViewer, Integer>();

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
		if (CompatibleUtil.isSupportGetCPUAndMemoryInfo(database.getDatabaseInfo())) {
			createDatabaseComposite(bar, index++);
		}
		createVolumnComposite(bar, index++);
		createBrokerComposite(bar, index++);
		createLockAndTransactionComposite(bar, index++);

		loadData();
	}

	/**
	 * create button composite
	 *
	 * @param parent
	 */
	public void createButtonComposite(Composite parent) {
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		parent.setLayout(layout);

		Composite composite = new Composite(parent, SWT.None);
		layout.marginHeight = 0;
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(
				CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

		infoLable = new Label(composite, SWT.None);
		infoLable.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		infoLable.setText(getPartName());

		new Label(composite, SWT.None).setText("    ");

		errMsgLable = new Label(composite, SWT.None);
		errMsgLable.setLayoutData(
				CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

		ToolBar toolbar = new ToolBar(parent, SWT.RIGHT_TO_LEFT | SWT.WRAP | SWT.FLAT);
		toolbar.setLayoutData(CommonUITool.createGridData(
				GridData.HORIZONTAL_ALIGN_END, 1, 1, -1, -1));

		settingItem = new ToolItem(toolbar, SWT.PUSH);
		settingItem.setImage(CommonUIPlugin.getImage("/icons/action/settings.png"));
		settingItem.setToolTipText(Messages.databaseDashboardSettingTooltip);
		settingItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				boolean useOrNot = GeneralPreference.isUseDatabaseDashboard();
				ShowDashboardDialog dialog = new ShowDashboardDialog(
						getSite().getShell(), ShowDashboardDialog.TYPE_DB,
						useOrNot, autoRefreshTime);
				if (dialog.open() == IDialogConstants.OK_ID) {
					autoRefreshTime = dialog.getAutoRefreshSecond();
					useOrNot = dialog.isUseAutoShow();
					GeneralPreference.setUseDatabaseDashboard(useOrNot);
				}
			}
		});

		exportItem = new ToolItem(toolbar, SWT.PUSH);
		exportItem.setImage(CubridManagerUIPlugin.getImage("icons/action/conf_export.png"));
		exportItem.setToolTipText(Messages.databaseDashboardSettingTooltip);
		exportItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				List<Table> exportTableList = new ArrayList<Table>();
				if (CompatibleUtil.isSupportGetCPUAndMemoryInfo(database.getDatabaseInfo())) {
					exportTableList.add(dbInfoTableViewer.getTable());
				}
				exportTableList.add(volumnInfoTableViewer.getTable());
				exportTableList.add(brokerInfoTableViewer.getTable());
				exportTableList.add(lockAndTransactionTable);

				String excelSheetNames[] = {
						Messages.exportDashboardDBTableTitle,
						Messages.exportDashboardVolumnTableTitle,
						Messages.exportDashboardBrokerTableTitle,
						Messages.exportDashboardLockTableTitle };

				new ExportDashboardDialog(
						Display.getCurrent().getActiveShell(), exportTableList,
						database.getDatabaseInfo().getDbName(), excelSheetNames).open();
			}
		});

		new ToolItem(toolbar, SWT.SEPARATOR | SWT.VERTICAL);

		autoRefreshItem = new ToolItem(toolbar, SWT.PUSH);
		autoRefreshItem.setImage(CubridManagerUIPlugin.getImage("icons/action/refresh_disabled.png"));
		autoRefreshItem.setToolTipText(Messages.databaseDashboardStartAutoRefreshTooltip);
		autoRefreshItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (autoRefreshData) {
					autoRefreshItem.setImage(CubridManagerUIPlugin.getImage("icons/action/refresh_disabled.png"));
					autoRefreshItem.setToolTipText(Messages.databaseDashboardStartAutoRefreshTooltip);
					stopAutoRefreshData();
				} else {
					autoRefreshItem.setImage(CubridManagerUIPlugin.getImage("icons/action/refresh.png"));
					autoRefreshItem.setToolTipText(Messages.databaseDashboardStopAutoRefreshTooltip);
					startAutoRefreshData();
				}
				autoRefreshData = !autoRefreshData;
			}
		});
	}

	/**
	 * create database information composite
	 *
	 * @param bar ExpandBar
	 * @param bar index
	 */
	public void createDatabaseComposite(ExpandBar bar, int index) {
		ExpandItem dbInfoItem = new ExpandItem(bar, SWT.NONE, index);
		dbInfoItem.setText(Messages.exportDashboardDBTableTitle);

		Composite dbComposite = new Composite(bar, SWT.None);
		dbComposite.setLayout(new FillLayout());

		dbInfoTableViewer = new TableViewer(dbComposite, SWT.BORDER
				| SWT.FULL_SELECTION);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.heightHint = 16;
		dbInfoTableViewer.getTable().setLayoutData(gridData);
		dbInfoTableViewer.getTable().setHeaderVisible(true);
		dbInfoTableViewer.getTable().setLinesVisible(false);

		final TableViewerColumn columnCPU = new TableViewerColumn(
				dbInfoTableViewer, SWT.CENTER);
		columnCPU.getColumn().setWidth(60);
		columnCPU.getColumn().setText("CPU");
		columnCPU.getColumn().setToolTipText("CPU");

		final TableViewerColumn columnMemory = new TableViewerColumn(
				dbInfoTableViewer, SWT.CENTER);
		columnMemory.getColumn().setWidth(120);
		columnMemory.getColumn().setText(
				com.cubrid.cubridmanager.ui.monitoring.Messages.dbSysMemInfoGroupName);
		columnMemory.getColumn().setToolTipText(
				com.cubrid.cubridmanager.ui.monitoring.Messages.dbSysMemInfoGroupName);

		final TableViewerColumn columnQPS = new TableViewerColumn(
				dbInfoTableViewer, SWT.CENTER);
		columnQPS.getColumn().setWidth(80);
		columnQPS.getColumn().setText("QPS");
		columnQPS.getColumn().setToolTipText(
				com.cubrid.cubridmanager.ui.host.Messages.tipQps);

		final TableViewerColumn columnHitRatio = new TableViewerColumn(
				dbInfoTableViewer, SWT.CENTER);
		columnHitRatio.getColumn().setWidth(80);
		columnHitRatio.getColumn().setText("Hit Ratio");
		columnHitRatio.getColumn().setToolTipText(
				Messages.databaseTableHitRatioColumnLabel);

		final TableViewerColumn columnIo1 = new TableViewerColumn(
				dbInfoTableViewer, SWT.CENTER);
		columnIo1.getColumn().setWidth(100);
		columnIo1.getColumn().setText("Fetch pages");
		columnIo1.getColumn().setToolTipText("num_data_page_fetches");

		final TableViewerColumn columnIo2 = new TableViewerColumn(
				dbInfoTableViewer, SWT.CENTER);
		columnIo2.getColumn().setWidth(100);
		columnIo2.getColumn().setText("Dirty pages");
		columnIo2.getColumn().setToolTipText("num_data_page_dirties");

		final TableViewerColumn columnIo3 = new TableViewerColumn(
				dbInfoTableViewer, SWT.CENTER);
		columnIo3.getColumn().setWidth(100);
		columnIo3.getColumn().setText("I/O Reads");
		columnIo3.getColumn().setToolTipText("num_data_page_ioreads");

		final TableViewerColumn columnIo4 = new TableViewerColumn(
				dbInfoTableViewer, SWT.CENTER);
		columnIo4.getColumn().setWidth(100);
		columnIo4.getColumn().setText("I/O Writes");
		columnIo4.getColumn().setToolTipText("num_data_page_iowrites");

		dbInfoTableViewer.setContentProvider(new TableContentProvider());
		dbInfoTableViewer.setLabelProvider(new TableLabelProvider());
		dbInfoTableViewer.setInput(dbInfoListData);

		dbInfoItem.setControl(dbComposite);
		dbInfoItem.setHeight(80);
		dbInfoItem.setExpanded(true);

		tableViewOnBarIndexMap.put(dbInfoTableViewer, index);
	}

	/**
	 * create volumn information composite
	 *
	 * @param bar ExpandBar
	 * @param bar index
	 *
	 */
	public void createVolumnComposite(ExpandBar bar, int index) {
		ExpandItem volumnItem = new ExpandItem(bar, SWT.NONE, index);
		volumnItem.setText(Messages.exportDashboardVolumnTableTitle);

		Composite volumnComposite = new Composite(bar, SWT.NONE);
		volumnComposite.setLayout(new FillLayout());

		volumnInfoTableViewer = new TableViewer(volumnComposite, SWT.BORDER
				| SWT.FULL_SELECTION);
		volumnInfoTableViewer.getTable().setHeaderVisible(true);
		volumnInfoTableViewer.getTable().setLinesVisible(true);

		final TableViewerColumn columnVolumn = new TableViewerColumn(
				volumnInfoTableViewer, SWT.CENTER);
		columnVolumn.getColumn().setWidth(140);
		columnVolumn.getColumn().setText(
				Messages.volumnTableVolumnNameColumnLabel);
		columnVolumn.getColumn().setToolTipText(
				Messages.volumnTableVolumnNameColumnLabel);

		final TableViewerColumn columnType = new TableViewerColumn(
				volumnInfoTableViewer, SWT.CENTER);
		columnType.getColumn().setWidth(90);
		columnType.getColumn().setText(
				Messages.volumnTableVolumnTypeColumnLabel);
		columnType.getColumn().setToolTipText(
				Messages.volumnTableVolumnTypeColumnLabel);

		final TableViewerColumn columnFreeSize = new TableViewerColumn(
				volumnInfoTableViewer, SWT.RIGHT);
		columnFreeSize.getColumn().setWidth(90);
		columnFreeSize.getColumn().setText(
				Messages.volumnTableFreesizeColumnLabel);
		columnFreeSize.getColumn().setToolTipText(
				Messages.volumnTableFreesizeColumnLabel);

		final TableViewerColumn columnTotalSize = new TableViewerColumn(
				volumnInfoTableViewer, SWT.RIGHT);
		columnTotalSize.getColumn().setWidth(90);
		columnTotalSize.getColumn().setText(
				Messages.volumnTableTotalsizeColumnLabel);
		columnTotalSize.getColumn().setToolTipText(
				Messages.volumnTableTotalsizeColumnLabel);

		final TableViewerColumn lastModifyDate = new TableViewerColumn(
				volumnInfoTableViewer, SWT.CENTER);
		lastModifyDate.getColumn().setWidth(90);
		lastModifyDate.getColumn().setText(
				Messages.volumnTableLastModifyTimeColumnLabel);
		lastModifyDate.getColumn().setToolTipText(
				Messages.volumnTableLastModifyTimeColumnLabel);

		final TableViewerColumn columnLocation = new TableViewerColumn(
				volumnInfoTableViewer, SWT.LEFT);
		columnLocation.getColumn().setWidth(300);
		columnLocation.getColumn().setText(Messages.tblColumnVolPath);
		columnLocation.getColumn().setToolTipText(Messages.tblColumnVolPath);

		volumnInfoTableViewer.setContentProvider(new TableContentProvider());
		volumnInfoTableViewer.setLabelProvider(new TableLabelProvider());

		Table table = volumnInfoTableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		volumnInfoTableViewer.setInput(volumnInfoListData);
		volumnItem.setControl(volumnComposite);
		volumnItem.setHeight(130);
		volumnItem.setExpanded(true);

		Menu menu = new Menu(this.getSite().getShell(), SWT.POP_UP);
		final MenuItem itemAddVolumn = new MenuItem(menu, SWT.PUSH);
		itemAddVolumn.setText(com.cubrid.cubridmanager.ui.spi.Messages.setAddVolumeActionName);
		itemAddVolumn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				DefaultSchemaNode node = null;
				for (ICubridNode cubridNode : database.getChildren()) {
					if (cubridNode.getType().equals(
							CubridNodeType.DBSPACE_FOLDER)) {
						node = (DefaultSchemaNode) cubridNode;
					}
				}
				if (node == null) {
					return;
				}

				// Gets the status of adding volume
				GetAddVolumeStatusInfo getAddVolumeStatusInfo = new GetAddVolumeStatusInfo();
				final CommonQueryTask<GetAddVolumeStatusInfo> statusTask = new CommonQueryTask<GetAddVolumeStatusInfo>(
						database.getServer().getServerInfo(),
						CommonSendMsg.getCommonDatabaseSendMsg(),
						getAddVolumeStatusInfo);
				statusTask.setDbName(database.getLabel());
				TaskExecutor taskExecutor = new CommonTaskExec(
						com.cubrid.cubridmanager.ui.cubrid.dbspace.Messages.getVolumeInfoTaskName);
				taskExecutor.addTask(statusTask);

				CommonQueryTask<DbSpaceInfoList> spaceInfoTask = null;
				DatabaseInfo databaseInfo = database.getDatabaseInfo();
				DbSpaceInfoList dbSpaceInfoList = databaseInfo.getDbSpaceInfoList();
				int pageSize = 0;
				if (null == dbSpaceInfoList) {
					dbSpaceInfoList = new DbSpaceInfoList();
					spaceInfoTask = new CommonQueryTask<DbSpaceInfoList>(
							database.getServer().getServerInfo(),
							CommonSendMsg.getCommonDatabaseSendMsg(),
							dbSpaceInfoList);

					spaceInfoTask.setDbName(database.getLabel());
					taskExecutor.addTask(spaceInfoTask);
				}
				new ExecTaskWithProgress(taskExecutor).busyCursorWhile();
				if (spaceInfoTask == null) {
					pageSize = dbSpaceInfoList.getPagesize();
				} else {
					final DbSpaceInfoList model = ((CommonQueryTask<DbSpaceInfoList>) spaceInfoTask).getResultModel();
					pageSize = model.getPagesize();
				}

				getAddVolumeStatusInfo = statusTask.getResultModel();
				//open add volumn dialog
				AddVolumeDialog addVolumeDialog = new AddVolumeDialog(
						getSite().getShell());
				addVolumeDialog.setGetAddVolumeStatusInfo(getAddVolumeStatusInfo);
				addVolumeDialog.initPara(node);
				addVolumeDialog.setTreeViewer(CubridNavigatorView.getNavigatorView(
						"com.cubrid.cubridmanager.host.navigator").getViewer());
				addVolumeDialog.setPageSize(pageSize);
				int returnCode = addVolumeDialog.open();

				//if add volumn refresh table
				if (returnCode == IDialogConstants.OK_ID) {
					volumnInfoListData.clear();
					loadVolumnsInfo();
					volumnInfoTableViewer.refresh();
				}
			}
		});
		volumnInfoTableViewer.getTable().setMenu(menu);
		tableViewOnBarIndexMap.put(volumnInfoTableViewer, index);
	}

	/**
	 * craete broker composite
	 *
	 * @param bar
	 * @param bar index
	 *
	 */
	public void createBrokerComposite(ExpandBar bar, int index) {
		ExpandItem brokerItem = new ExpandItem(bar, SWT.NONE, index);
		brokerItem.setText(Messages.exportDashboardBrokerTableTitle);

		Composite brokerComposite = new Composite(bar, SWT.None);
		brokerComposite.setLayout(new FillLayout());

		brokerInfoTableViewer = new TableViewer(brokerComposite, SWT.BORDER
				| SWT.FULL_SELECTION);
		brokerInfoTable = brokerInfoTableViewer.getTable();
		brokerInfoTable.setHeaderVisible(true);
		brokerInfoTable.setLinesVisible(true);

		final TableViewerColumn columnVolumn = new TableViewerColumn(
				brokerInfoTableViewer, SWT.CENTER);
		columnVolumn.getColumn().setWidth(100);
		columnVolumn.getColumn().setText("Broker");
		columnVolumn.getColumn().setToolTipText(
				com.cubrid.cubridmanager.ui.host.Messages.tipBrokerName);

		final TableViewerColumn columnType = new TableViewerColumn(
				brokerInfoTableViewer, SWT.CENTER);
		columnType.getColumn().setWidth(80);
		columnType.getColumn().setText("ID");
		columnType.getColumn().setToolTipText(Messages.brokerTableIDColumnTip);

		final TableViewerColumn columnSize = new TableViewerColumn(
				brokerInfoTableViewer, SWT.CENTER);
		columnSize.getColumn().setWidth(100);
		columnSize.getColumn().setText("PID");
		columnSize.getColumn().setToolTipText(Messages.brokerTablePIDColumnTip);

		final TableViewerColumn columnQPS = new TableViewerColumn(
				brokerInfoTableViewer, SWT.CENTER);
		columnQPS.getColumn().setWidth(100);
		columnQPS.getColumn().setText("QPS");
		columnQPS.getColumn().setToolTipText(
				com.cubrid.cubridmanager.ui.host.Messages.tipQps);

		final TableViewerColumn columnFreeSize = new TableViewerColumn(
				brokerInfoTableViewer, SWT.CENTER);
		columnFreeSize.getColumn().setWidth(100);
		columnFreeSize.getColumn().setText("LQS");
		columnFreeSize.getColumn().setToolTipText(
				Messages.brokerTableLQSColumnTip);

		final TableViewerColumn columnTotalSize = new TableViewerColumn(
				brokerInfoTableViewer, SWT.CENTER);
		columnTotalSize.getColumn().setWidth(80);
		columnTotalSize.getColumn().setText(
				Messages.multiDatabaseLoginDialogColumnStatus);
		columnTotalSize.getColumn().setToolTipText(
				Messages.multiDatabaseLoginDialogColumnStatus);

		final TableViewerColumn columnLocation = new TableViewerColumn(
				brokerInfoTableViewer, SWT.CENTER);
		columnLocation.getColumn().setWidth(140);
		columnLocation.getColumn().setText(
				Messages.brokerTableLastConnectTimeColumnLabel);
		columnLocation.getColumn().setToolTipText(
				Messages.brokerTableLastConnectTimeColumnLabel);

		brokerInfoTableViewer.setContentProvider(new TableContentProvider());
		brokerInfoTableViewer.setLabelProvider(new TableLabelProvider());

		brokerInfoTableViewer.setInput(brokerInfoListData);

		brokerItem.setControl(brokerComposite);
		brokerItem.setHeight(140);
		brokerItem.setExpanded(true);

		Menu menu = new Menu(this.getSite().getShell(), SWT.POP_UP);
		final MenuItem itemRestartCAS = new MenuItem(menu, SWT.PUSH);
		itemRestartCAS.setText(Messages.exportDashboardMenuRestartServer);
		itemRestartCAS.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				int i = brokerInfoTable.getSelectionIndex();
				if (i < 0) {
					return;
				}
				String brokerName = brokerInfoTable.getItem(i).getText(0);
				String serverId = brokerInfoTable.getItem(i).getText(1);
				if (!CommonUITool.openConfirmBox(Messages.bind(
						com.cubrid.cubridmanager.ui.broker.Messages.restartBrokerServerMsg,
						serverId))) {
					return;
				}
				ServerInfo serverInfo = database.getServer().getServerInfo();
				RestartBrokerTask restartTask = new RestartBrokerTask(
						serverInfo);
				restartTask.setBrokerName(brokerName);
				restartTask.setApplyServerNum(serverId);

				TaskExecutor taskExecutor = new CommonTaskExec(
						Messages.bind(
								com.cubrid.cubridmanager.ui.broker.Messages.restartBrokerServerTaskName,
								serverId));
				taskExecutor.addTask(restartTask);
				new ExecTaskWithProgress(taskExecutor).exec();
				if (taskExecutor.isSuccess()) {
					// refresh
					brokerInfoListData.clear();
					asinfoLst.clear();
					brokerInfoTable.remove(i);
					loadBrokerInfo();
					//recompute database's qps
					long qps = 0;
					for (HashMap<String, ApplyServerInfo> brokerValueMap : asinfoLst) {
						for (Entry<String, ApplyServerInfo> entry : brokerValueMap.entrySet()) {
							ApplyServerInfo applyServerInfo = entry.getValue();
							qps += Long.valueOf(applyServerInfo.getAsNumQuery());
						}
					}
					Map<String, String> dbInfoMap = brokerInfoListData.get(0);
					dbInfoMap.put("2", Long.toString(qps));

					if (CompatibleUtil.isSupportGetCPUAndMemoryInfo(database.getDatabaseInfo())) {
						dbInfoTableViewer.refresh();
					}
					brokerInfoTableViewer.refresh();
				}
			}
		});

		final MenuItem itemShowSqlLog = new MenuItem(menu, SWT.PUSH);
		itemShowSqlLog.setText(Messages.exportDashboardMenuShowSQLLog);
		itemShowSqlLog.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent event) {
				showLogView("sql");
			}
		});

		final MenuItem itemShowSlowQueryLog = new MenuItem(menu, SWT.PUSH);
		itemShowSlowQueryLog.setText(Messages.exportDashboardMenuShowSlowQueryLog);
		itemShowSlowQueryLog.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				showLogView("slow");

			}
		});

		menu.addMenuListener(new MenuListener() {
			public void menuHidden(MenuEvent e) {

			}

			public void menuShown(MenuEvent e) {
				int i = brokerInfoTable.getSelectionIndex();
				if (i < 0) {
					Menu menu = (Menu) e.getSource();
					menu.setVisible(false);
				}
			}
		});

		brokerInfoTable.setMenu(menu);

		tableViewOnBarIndexMap.put(brokerInfoTableViewer, index);
	}

	/**
	 * create lock and transaction composite
	 *
	 * @param bar ExpandBar
	 * @param bar index
	 *
	 */
	public void createLockAndTransactionComposite(ExpandBar bar, int index) {

		ExpandItem lockAndTransactionItem = new ExpandItem(bar, SWT.NONE, index);
		lockAndTransactionItem.setText(Messages.exportDashboardLockTableTitle);

		Composite lockAndTransactionComposite = new Composite(bar, SWT.NONE);
		lockAndTransactionComposite.setLayout(new FillLayout());

		lockAndTransactionTableViewer = new TableViewer(
				lockAndTransactionComposite, SWT.BORDER | SWT.FULL_SELECTION);

		lockAndTransactionTable = lockAndTransactionTableViewer.getTable();
		lockAndTransactionTable.setHeaderVisible(true);
		lockAndTransactionTable.setLinesVisible(true);

		final TableViewerColumn columnTranIndex = new TableViewerColumn(
				lockAndTransactionTableViewer, SWT.CENTER);
		columnTranIndex.getColumn().setWidth(80);
		columnTranIndex.getColumn().setText(Messages.tblColTranInfoTranIndex);
		columnTranIndex.getColumn().setToolTipText(
				Messages.tblColTranInfoTranIndex);

		final TableViewerColumn columnUserName = new TableViewerColumn(
				lockAndTransactionTableViewer, SWT.CENTER);
		columnUserName.getColumn().setWidth(80);
		columnUserName.getColumn().setText(Messages.tblColTranInfoUserName);
		columnUserName.getColumn().setToolTipText(
				Messages.tblColTranInfoUserName);

		final TableViewerColumn columnHost = new TableViewerColumn(
				lockAndTransactionTableViewer, SWT.CENTER);
		columnHost.getColumn().setWidth(130);
		columnHost.getColumn().setText(Messages.tblColTranInfoHost);
		columnHost.getColumn().setToolTipText(Messages.tblColTranInfoHost);

		final TableViewerColumn columnPid = new TableViewerColumn(
				lockAndTransactionTableViewer, SWT.CENTER);
		columnPid.getColumn().setWidth(80);
		columnPid.getColumn().setText(Messages.tblColTranInfoProcessId);
		columnPid.getColumn().setToolTipText(Messages.tblColTranInfoProcessId);

		final TableViewerColumn columnType = new TableViewerColumn(
				lockAndTransactionTableViewer, SWT.BEGINNING);
		columnType.getColumn().setWidth(320);
		columnType.getColumn().setText(Messages.tblColLockInfoObjectType);
		columnType.getColumn().setToolTipText(Messages.tblColLockInfoObjectType);
		columnPid.getColumn().setAlignment(SWT.CENTER);

		final TableViewerColumn columnMode = new TableViewerColumn(
				lockAndTransactionTableViewer, SWT.CENTER);
		columnMode.getColumn().setWidth(100);
		columnMode.getColumn().setText(Messages.tblColLockInfoMode);
		columnMode.getColumn().setToolTipText(Messages.tblColLockInfoMode);

		lockAndTransactionTableViewer.setContentProvider(new TableContentProvider());
		lockAndTransactionTableViewer.setLabelProvider(new TableLabelProvider());

		lockAndTransactionTableViewer.setInput(lockAndTransactionListData);

		lockAndTransactionItem.setControl(lockAndTransactionComposite);
		lockAndTransactionItem.setHeight(140);
		lockAndTransactionItem.setExpanded(true);

		Menu menu = new Menu(this.getSite().getShell(), SWT.POP_UP);
		final MenuItem itemKillTran = new MenuItem(menu, SWT.PUSH);
		itemKillTran.setText(Messages.menuKillTransaction);
		itemKillTran.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				int i = lockAndTransactionTable.getSelectionIndex();
				if (i < 0) {
					return;
				}
				String pid = lockAndTransactionTable.getItem(i).getText(3);
				if (i >= 0
						&& dbTransactionList != null
						&& dbTransactionList.getTransationInfo() != null
						&& dbTransactionList.getTransationInfo().getTransactionList() != null) {
					KillTransactionDialog dlg = new KillTransactionDialog(
							Display.getCurrent().getActiveShell());
					Transaction bean = null;
					for (Transaction t : dbTransactionList.getTransationInfo().getTransactionList()) {
						if (pid.equals(t.getPid())) {
							bean = t;
						}
					}
					dlg.setTransationInfo(bean);
					dlg.setDatabase(database);
					if (dlg.open() == IDialogConstants.CANCEL_ID) {
						return;
					}
					dbTransactionList.getTransationInfo().setTransactionList(
							dlg.getKillTransactionList().getTransationInfo().getTransactionList());

					lockAndTransactionListData.clear();
					lockAndTransactionTableViewer.getTable().clearAll();
					loadTransactionInfo();
					lockAndTransactionTableViewer.refresh();
				}
			}
		});
		menu.addMenuListener(new MenuListener() {
			public void menuHidden(MenuEvent e) {

			}

			public void menuShown(MenuEvent e) {
				int i = lockAndTransactionTableViewer.getTable().getSelectionIndex();
				if (i < 0) {
					Menu menu = (Menu) e.getSource();
					menu.setVisible(false);
				}
			}
		});

		lockAndTransactionTable.setMenu(menu);
		tableViewOnBarIndexMap.put(lockAndTransactionTableViewer, index);
	}

	public void dispose() {
		stopAutoRefreshData();
	}

	/**
	 *
	 * Load data
	 *
	 * @return <code>true</code> whether it is successful;<code>false</code>
	 *         otherwise
	 */
	public boolean loadData() {

		loadVolumnsInfo();
		loadBrokerInfo();
		loadDatabaseInfo();
		loadTransactionInfo();
		return true;
	}

	private void setVolumnInfoData() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				if (volumnInfoTableViewer.getTable().isDisposed()) {
					return;
				}
				volumnInfoTableViewer.refresh();

				int height = volumnInfoTableViewer.getTable().computeSize(
						SWT.DEFAULT, SWT.DEFAULT).y;
				if (height < 130) {
					height = 130;
				}
				bar.getItem(tableViewOnBarIndexMap.get(volumnInfoTableViewer)).setHeight(
						height);
			}
		});
	}

	private void setBrokerInfoData() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				if (brokerInfoTableViewer.getTable().isDisposed()) {
					return;
				}
				brokerInfoTableViewer.refresh();

				int height = brokerInfoTableViewer.getTable().computeSize(
						SWT.DEFAULT, SWT.DEFAULT).y;
				if (height < 130) {
					height = 130;
				}
				bar.getItem(tableViewOnBarIndexMap.get(brokerInfoTableViewer)).setHeight(
						height);
			}
		});
	}

	private void setTransactionInfoData() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				if (lockAndTransactionTableViewer.getTable().isDisposed()) {
					return;
				}
				lockAndTransactionTableViewer.refresh();

				int height = lockAndTransactionTableViewer.getTable().computeSize(
						SWT.DEFAULT, SWT.DEFAULT).y;
				if (height < 130) {
					height = 130;
				}
				bar.getItem(
						tableViewOnBarIndexMap.get(lockAndTransactionTableViewer)).setHeight(
						height);
			}
		});
	}

	/**
	 * A inner class that update the data of chart in a single thread
	 *
	 * @author lizhiqiang
	 * @version 1.0 - 2010-6-17 created by lizhiqiang
	 * @version 2.0 - 2013-01-22 fulei
	 */
	public class DatabaseDataGenerator extends
			Thread {
		private TreeMap<String, String> cpuAndMemoryUpdateMap;
		private TreeMap<String, String> statisticsUpdateMap;
		private long firstSleepTime = 1000;
		private boolean finishThreadFlag = false;
		private int startRun = 0;
		private long runCount = 0;

		/**
		 * Thread run method
		 */
		public void run() {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					if (!autoRefreshItem.isDisposed()) {
						autoRefreshItem.setEnabled(false);
					}
				}
			});
			while (!finishThreadFlag) {
				//if database is stop, clear db table data and stop thread
				if (database.getRunningType() != DbRunningType.CS) {
					finishThreadFlag = true;
					asinfoLst.clear();
					dbInfoListData.clear();
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							if (dbInfoTableViewer != null
									&& !dbInfoTableViewer.getTable().isDisposed()) {
								dbInfoTableViewer.refresh();
							}
						}
					});

					return;
				}
				if (CompatibleUtil.isSupportGetCPUAndMemoryInfo(database.getDatabaseInfo())) {
					cpuAndMemoryUpdateMap = loadDatabaseInfo(startRun);
				}
				statisticsUpdateMap = getDbHitRationTaskValue(startRun);
				if (startRun <= 1) {
					startRun++;
				} else {
					if (dbInfoTableViewer != null
							&& !dbInfoTableViewer.getTable().isDisposed()) {
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								updateDabaseTableDataInfo(
										cpuAndMemoryUpdateMap,
										statisticsUpdateMap);
							}
						});
					} else if (volumnInfoTableViewer.getTable().isDisposed()) {
						//dipose then stop tread
						break;
					}
				}
				try {
					/*to quick display cpu/memeory info
					 *  sleep short time at first 10 times
					 */
					if (runCount <= 5) {
						Thread.sleep(firstSleepTime);
					} else {
						finishThreadFlag = true;
					}
				} catch (Exception e) {
					LOGGER.error(e.getMessage());
				}
				runCount++;
			}
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					if (!autoRefreshItem.isDisposed()) {
						autoRefreshItem.setEnabled(true);
					}
				}
			});
		}
	}

	/**
	 * A inner class that update the data of chart in a single thread
	 *
	 * @author lizhiqiang
	 * @version 1.0 - 2010-6-17 created by lizhiqiang
	 */
	public class RefreshDataThread extends
			Thread {
		private TreeMap<String, String> cpuAndMemoryUpdateMap;
		private TreeMap<String, String> statisticsUpdateMap;
		private int startRun = 0;
		private long runCount = 0;

		/**
		 * Thread run method
		 */
		public void run() {
			while (runflag) {
				//if database is stop, clear db table data and stop thread
				if (database.getRunningType() != DbRunningType.CS) {
					stopAutoRefreshData();
					asinfoLst.clear();
					dbInfoListData.clear();
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							if (dbInfoTableViewer != null
									&& !dbInfoTableViewer.getTable().isDisposed()) {
								dbInfoTableViewer.refresh();
							}
						}
					});
					return;
				}
				//load volumn info
				//				loadVolumnsInfo();
				//load broker info
				loadBrokerInfo();
				//load transaction info
				loadTransactionInfo();

				//load db info
				if (CompatibleUtil.isSupportGetCPUAndMemoryInfo(database.getDatabaseInfo())) {
					cpuAndMemoryUpdateMap = loadDatabaseInfo(startRun);
				}
				statisticsUpdateMap = getDbHitRationTaskValue(startRun);
				if (startRun <= 1) {
					startRun++;
				} else {
					if (dbInfoTableViewer != null
							&& !dbInfoTableViewer.getTable().isDisposed()) {
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								if (CompatibleUtil.isSupportGetCPUAndMemoryInfo(database.getDatabaseInfo())) {
									updateDabaseTableDataInfo(
											cpuAndMemoryUpdateMap,
											statisticsUpdateMap);
								}
								//								volumnInfoTableViewer.refresh();
								brokerInfoTableViewer.refresh();
								lockAndTransactionTableViewer.refresh();

							}
						});
					} else if (volumnInfoTableViewer.getTable().isDisposed()) {
						//dipose then stop tread
						break;
					}
				}

				try {
					Thread.sleep(autoRefreshTime * 1000);
				} catch (Exception e) {
					LOGGER.error(e.getMessage());
				}
				runCount++;
			}
		}
	}

	/**
	 * load volumns information
	 */
	public void loadVolumnsInfo() {
		// if database is stop, do not get data
		if (database.getRunningType() != DbRunningType.CS) {
			return;
		}
		final DbSpaceInfoList dbSpaceInfoList = new DbSpaceInfoList();
		final CommonQueryTask<DbSpaceInfoList> loadVolumnTask = new CommonQueryTask<DbSpaceInfoList>(
				database.getServer().getServerInfo(),
				CommonSendMsg.getCommonDatabaseSendMsg(), dbSpaceInfoList);
		loadVolumnTask.setDbName(database.getName());

		new Thread(new Runnable() {
			public void run() {
				loadVolumnTask.execute();
				if (!loadVolumnTask.isSuccess()) {
					loadVolumnTask.finish();
					return;
				}

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						process();
					}
				});
			}

			private void process() {
				volumnInfoListData.clear();
				DbSpaceInfoList dbSpaceInfoList = loadVolumnTask.getResultModel();
				if (dbSpaceInfoList != null) {
					long archiveSize = 0;
					for (DbSpaceInfo dbSpaceInfo : dbSpaceInfoList.getSpaceinfo()) {
						if (StringUtil.isEmpty(dbSpaceInfo.getType())) {
							continue;
						}

						if (dbSpaceInfo.getType().equals(
								VolumeType.ARCHIVE_LOG.getText())) {
							archiveSize += dbSpaceInfo.getTotalpage()
									* dbSpaceInfoList.getPagesize();
							continue;
						}

						boolean isLogVolumn = VolumeType.ACTIVE_LOG.getText().equals(
								dbSpaceInfo.getType())
								|| VolumeType.ARCHIVE_LOG.getText().equals(
										dbSpaceInfo.getType());

						Map<String, String> volumnMap = new HashMap<String, String>();
						volumnMap.put("0", dbSpaceInfo.getSpacename());
						volumnMap.put("1", dbSpaceInfo.getType());
						String freeSize = getSpaceDesc(Long.valueOf(dbSpaceInfo.getFreepage())
								* Long.valueOf(dbSpaceInfoList.getPagesize()));
						String totalSize = getSpaceDesc(Long.valueOf(dbSpaceInfo.getTotalpage())
								* Long.valueOf(dbSpaceInfoList.getPagesize()));
						if (isLogVolumn) {
							volumnMap.put("2", "-");
						} else {
							volumnMap.put("2", freeSize);
						}
						volumnMap.put("3", totalSize);
						volumnMap.put("4", dbSpaceInfo.getDate());
						volumnMap.put("5", dbSpaceInfo.getLocation());
						volumnInfoListData.add(volumnMap);
					}

					if (archiveSize > 0) {
						Map<String, String> volumnMap = new HashMap<String, String>();
						volumnMap.put("0", "Total Archives");
						volumnMap.put("1", "Archive log");
						volumnMap.put("2", "-");
						volumnMap.put("3", getSpaceDesc(archiveSize));
						volumnMap.put("4", "-");
						volumnMap.put("5", "-");
						volumnInfoListData.add(volumnMap);
					}
				}

				setVolumnInfoData();
				loadVolumnTask.finish();
			}
		}).start();
	}

	/**
	 * load broker information
	 */
	public void loadBrokerInfo() {
		// if database is stop, do not get data
		if (database.getRunningType() != DbRunningType.CS) {
			return;
		}

		// broker info tasks
		BrokerInfos brokerInfos = new BrokerInfos();
		final CommonQueryTask<BrokerInfos> brokerInfosTask = new CommonQueryTask<BrokerInfos>(
				database.getServer().getServerInfo(),
				CommonSendMsg.getCommonSimpleSendMsg(), brokerInfos);

		new Thread(new Runnable() {
			public void run() {
				brokerInfosTask.execute();
				if (!brokerInfosTask.isSuccess()) {
					brokerInfosTask.finish();
					return;
				}

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						process();
					}
				});
			}

			private void process() {
				asinfoLst.clear();
				brokerInfoListData.clear();

				BrokerInfos brokerInfos = brokerInfosTask.getResultModel();
				if (null != brokerInfos) {
					BrokerInfoList list = brokerInfos.getBorkerInfoList();
					if (list != null && list.getBrokerInfoList() != null) {
						List<BrokerInfo> newBrokerInfoList = list.getBrokerInfoList();// all broker
						for (BrokerInfo brokerInfo : newBrokerInfoList) {
							BrokerStatusInfos brokerStatusInfos = new BrokerStatusInfos();
							final GetBrokerStatusInfosTask<BrokerStatusInfos> statisTask = new GetBrokerStatusInfosTask<BrokerStatusInfos>(
									database.getServer().getServerInfo(),
									CommonSendMsg.getGetBrokerStatusItems(),
									brokerStatusInfos);
							statisTask.setBrokerName(brokerInfo.getName());
							statisTask.execute();
							brokerStatusInfos = statisTask.getResultModel();
							if (brokerStatusInfos != null) {
								List<ApplyServerInfo> applyServerInfoList = brokerStatusInfos.getAsinfo();//one broker status
								for (ApplyServerInfo applyServerInfo : applyServerInfoList) {
									if (database.getName().equalsIgnoreCase(
											applyServerInfo.getAsDbName())) {
										HashMap<String, ApplyServerInfo> valueMap = new HashMap<String, ApplyServerInfo>();
										valueMap.put(brokerInfo.getName(),
												applyServerInfo);
										asinfoLst.add(valueMap);
									}
								}
							}
							statisTask.finish();
						}
					}
				}

				//set task obejct to table view
				for (HashMap<String, ApplyServerInfo> brokerValueMap : asinfoLst) {
					for (Entry<String, ApplyServerInfo> entry : brokerValueMap.entrySet()) {
						String brokerName = entry.getKey();
						ApplyServerInfo applyServerInfo = entry.getValue();
						Map<String, String> brokerInfoMap = new HashMap<String, String>();
						brokerInfoMap.put("0", brokerName);
						brokerInfoMap.put("1", applyServerInfo.getAsId());
						brokerInfoMap.put("2", applyServerInfo.getAsPid());
						brokerInfoMap.put("3",
								applyServerInfo.getAsNumQuery());
						brokerInfoMap.put("4",
								applyServerInfo.getAsLongQuery());
						brokerInfoMap.put("5", applyServerInfo.getAsStatus());
						brokerInfoMap.put("6", applyServerInfo.getAsLct());
						brokerInfoListData.add(brokerInfoMap);
					}
				}

				setBrokerInfoData();

				brokerInfosTask.finish();
			}
		}).start();
	}

	/**
	 * load lock and transaction information
	 */
	public void loadTransactionInfo() {
		//if database is stop, do not get data
		if (database.getRunningType() != DbRunningType.CS) {
			return;
		}

		final CommonQueryTask<DatabaseLockInfo> getLockInfotask = new CommonQueryTask<DatabaseLockInfo>(
				database.getServer().getServerInfo(),
				CommonSendMsg.getCommonDatabaseSendMsg(),
				new DatabaseLockInfo());
		getLockInfotask.setDbName(database.getName());

		final CommonQueryTask<DbTransactionList> getTransactionTask = new CommonQueryTask<DbTransactionList>(
				database.getServer().getServerInfo(),
				CommonSendMsg.getCommonDatabaseSendMsg(),
				new DbTransactionList());
		getTransactionTask.setDbName(database.getName());

		new Thread(new Runnable() {
			public void run() {
				getLockInfotask.execute();
				if (!getLockInfotask.isSuccess()) {
					getLockInfotask.finish();
					return;
				}
				getTransactionTask.execute();
				if (!getTransactionTask.isSuccess()) {
					getTransactionTask.finish();
					return;
				}

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						process();
					}
				});
			}

			private void process() {
				lockAndTransactionListData.clear();

				databaseLockInfo = getLockInfotask.getResultModel();
				LockInfo lockInfo = databaseLockInfo.getLockInfo();
				if (lockInfo == null) {
					return;
				}

				DbLotInfo dbLotInfo = lockInfo.getDbLotInfo();
				if (dbLotInfo == null) {
					return;
				}

				if (dbLotInfo.getDbLotEntryList() != null) {
					//get Lock
					for (DbLotEntry dbLot : dbLotInfo.getDbLotEntryList()) {
						Map<String, String> map = new HashMap<String, String>();

						if (dbLot.getLockHoldersList() != null) {
							lockAndTransactionListData.add(map);
							map.put("4", dbLot.getOb_type());
							//get lock holder
							for (int i = 0; i < dbLot.getLockHoldersList().size(); i++) {
								LockHolders lockHolders = dbLot.getLockHoldersList().get(
										i);
								//add mode to the last mode
								String previousMode = map.get("5");
								if (i != 0 && previousMode != null) {
									previousMode += ",";
								}
								if (previousMode != null) {
									previousMode += lockHolders.getGranted_mode();
								} else {
									previousMode = lockHolders.getGranted_mode();
								}
								map.put("5", previousMode);
								//get DatabaseTransaction which id is equlas lock holder tranid
								for (DatabaseTransaction tran : lockInfo.getTransaction()) {
									if (tran.getIndex() == lockHolders.getTran_index()) {
										map.put("0",
												Integer.toString(tran.getIndex()));
										map.put("1", tran.getUid());
										map.put("2", tran.getHost());
										map.put("3", tran.getPid());
									}
								}
							}
						}
					}
				}
				setTransactionInfoData();
				getLockInfotask.finish();
				getTransactionTask.finish();
			}
		}).start();
	}

	/**
	 * start auto refresh data
	 */
	public void startAutoRefreshData() {
		stopAutoRefreshData();
		runflag = true;
		refreshDataThread = new RefreshDataThread();
		refreshDataThread.start();

	}

	public void stopAutoRefreshData() {
		runflag = false;
		if (refreshDataThread != null && !refreshDataThread.isAlive()) {
			refreshDataThread.interrupt();
			refreshDataThread = null;
		}
	}

	/**
	 * load databse information
	 */
	public void loadDatabaseInfo() {
		if (!CompatibleUtil.isSupportGetCPUAndMemoryInfo(database.getDatabaseInfo())) {
			return;
		}
		dbInfoListData.clear();

		Map<String, String> dbMap = new HashMap<String, String>();
		//first time display value of 0
		dbMap.put("0", "0%");
		dbMap.put("1", "0M /0M");
		//compute Qps
		long qps = 0;
		for (HashMap<String, ApplyServerInfo> brokerValueMap : asinfoLst) {
			for (Entry<String, ApplyServerInfo> entry : brokerValueMap.entrySet()) {
				ApplyServerInfo applyServerInfo = entry.getValue();
				qps += Long.valueOf(applyServerInfo.getAsNumQuery());
			}
		}
		dbMap.put("2", Long.toString(qps));
		dbMap.put("3", "0");
		dbInfoListData.add(dbMap);
		//start thread to compute cpu/memory information at first time or thread is stop
		new DatabaseDataGenerator().start();
	}

	/**
	 * update database table data
	 *
	 * @param cpuAndMemoryMap the given instance of TreeMap<String,String>
	 * @param statisticsMap the given instance of TreeMap<String,String>
	 */
	public void updateDabaseTableDataInfo(
			TreeMap<String, String> cpuAndMemoryMap,
			TreeMap<String, String> statisticsMap) {
		if (!CompatibleUtil.isSupportGetCPUAndMemoryInfo(database.getDatabaseInfo())) {
			return;
		}
		String deltaCpuUser = cpuAndMemoryMap.get(DbProcStatEnum.DELTA_USER.name());
		String deltaCpuKernel = cpuAndMemoryMap.get(DbProcStatEnum.DELTA_KERNEL.name());
		String hostCpuTotal = cpuAndMemoryMap.get(HostStatEnum.CPU_TOTAL.name());
		String hostMemTotal = cpuAndMemoryMap.get(HostStatEnum.MEMPHY_TOTAL.name());
		String memPhyUsed = cpuAndMemoryMap.get(DbProcStatEnum.MEM_PHYSICAL.name());
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setMaximumFractionDigits(0);
		numberFormat.setGroupingUsed(true);
		if (hostCpuTotal == null || hostMemTotal == null) {
			return;
		}

		double hostCpuTotalDouble = Double.parseDouble(hostCpuTotal);
		Long deltaCpuUserLong = Long.parseLong(deltaCpuUser == null ? "0"
				: deltaCpuUser);
		Long dletaCpuKernelLong = Long.parseLong(deltaCpuKernel == null ? "0"
				: deltaCpuKernel);

		int userPercent = 0;
		int kernelPercent = 0;
		String totalPercentString = "0%";
		if (!"0".equals(hostCpuTotal)) {
			userPercent = (int) (deltaCpuUserLong / hostCpuTotalDouble * 100 + 0.5);
			kernelPercent = (int) (dletaCpuKernelLong / hostCpuTotalDouble
					* 100 + 0.5);
			totalPercentString = Integer.toString(userPercent + kernelPercent)
					+ "%";
			cpuAndMemoryMap.put(DbProcStatEnum.USER_PERCENT.name(),
					Integer.toString(userPercent));
			cpuAndMemoryMap.put(DbProcStatEnum.KERNEL_PERCENT.name(),
					Integer.toString(kernelPercent));

			Map<String, String> databaseInfo = dbInfoListData.get(0);
			if (databaseInfo != null) {
				databaseInfo.put("0", totalPercentString);
			}
		}
		memPhyUsed = memPhyUsed == null ? "0" : memPhyUsed;
		if (hostMemTotal != null) {
			double memPhyMb = Long.parseLong(memPhyUsed) / 1024.0;//physichal memory
			double hostMemTotalDoubleMb = Long.parseLong(hostMemTotal) / 1024.0;//total memory
			memPhyUsed = numberFormat.format(memPhyMb) + "MB";
			hostMemTotal = numberFormat.format(hostMemTotalDoubleMb) + "MB";
			Map<String, String> databaseInfo = dbInfoListData.get(0);
			if (databaseInfo != null) {
				databaseInfo.put("1", memPhyUsed + " / " + hostMemTotal);
			}
		}

		Map<String, String> databaseInfo = dbInfoListData.get(0);
		if (databaseInfo != null) {
			//compute Qps
			long qps = 0;
			for (HashMap<String, ApplyServerInfo> brokerValueMap : asinfoLst) {
				for (Entry<String, ApplyServerInfo> entry : brokerValueMap.entrySet()) {
					ApplyServerInfo applyServerInfo = entry.getValue();
					qps += Long.valueOf(applyServerInfo.getAsNumQuery());
				}
			}
			databaseInfo.put("2", Long.toString(qps));

			String hitRatio = statisticsMap.get("data_page_buffer_hit_ratio");
			if (hitRatio == null) {
				hitRatio = "";
			}
			databaseInfo.put("3", hitRatio);

			//num_data_page_fetches
			String numDataPageFetches = statisticsMap.get("num_data_page_fetches");
			if (numDataPageFetches == null) {
				numDataPageFetches = "";
			}
			databaseInfo.put("4", numDataPageFetches);

			//num_data_page_dirties
			String numDataPageDirties = statisticsMap.get("num_data_page_dirties");
			if (numDataPageDirties == null) {
				numDataPageDirties = "";
			}
			databaseInfo.put("5", numDataPageDirties);

			//num_data_page_ioreads
			String numDataPageIoreads = statisticsMap.get("num_data_page_ioreads");
			if (numDataPageIoreads == null) {
				numDataPageIoreads = "";
			}
			databaseInfo.put("6", numDataPageIoreads);

			//num_data_page_iowrites
			String numDataPageIowrites = statisticsMap.get("num_data_page_iowrites");
			if (numDataPageIowrites == null) {
				numDataPageIowrites = "";
			}
			databaseInfo.put("7", numDataPageIowrites);
		}

		if (dbInfoTableViewer != null
				&& !dbInfoTableViewer.getTable().isDisposed()) {
			dbInfoTableViewer.refresh();
		}
	}

	/**
	 * load database cpu and memory info
	 *
	 * @param startRun
	 * @return result
	 */
	public TreeMap<String, String> loadDatabaseInfo(int startRun) {
		TreeMap<String, String> returnMap = getDbProcTaskValue(startRun);
		TreeMap<String, String> hostMap = getHostProcTaskValue(startRun);
		String hostCpuTotal = hostMap.get(HostStatEnum.CPU_TOTAL.name());
		String memPhyTotal = hostMap.get(HostStatEnum.MEMPHY_TOTAL.name());
		returnMap.put(HostStatEnum.CPU_TOTAL.name(), hostCpuTotal);
		returnMap.put(HostStatEnum.MEMPHY_TOTAL.name(), memPhyTotal);
		return returnMap;
	}

	/**
	 * Get the update value from dbprocstat task
	 *
	 * @param serverInfo the server info
	 * @param startRun int
	 * @return TreeMap<String, String>
	 */
	private TreeMap<String, String> getDbProcTaskValue(int startRun) {
		final CommonQueryTask<DbProcStat> dbStatTask = new CommonQueryTask<DbProcStat>(
				database.getServer().getServerInfo(),
				CommonSendMsg.getCommonDatabaseSendMsg(), dbProcStatusResult);
		dbStatTask.setDbName(database.getName());
		dbStatTask.execute();
		if (startRun == 0) {
			dbProcStatusResult = dbStatTask.getResultModel();
			if (!dbProcStatusResult.getStatus()) {
				if (interruptReq) {
					return convertMapKey(dbProcStatProxy.getDiagStatusResultMap());
				} else {
					showErrorMsg(dbProcStatusResult);
				}
			}
		} else if (startRun == 1) {
			dbProcOldOneStatusResult.copyFrom(dbProcStatusResult);
			dbProcStatusResult.clearDbstat();
			dbProcStatusResult = dbStatTask.getResultModel();
			if (!dbProcStatusResult.getStatus()) {
				if (interruptReq) {
					return convertMapKey(dbProcStatProxy.getDiagStatusResultMap());
				} else {
					showErrorMsg(dbProcStatusResult);
				}
			}
			dbProcStatProxy.compute(database.getName(), dbProcStatusResult,
					dbProcOldOneStatusResult);
		} else {
			dbProcOldTwoStatusResult.copyFrom(dbProcOldOneStatusResult);
			dbProcOldOneStatusResult.copyFrom(dbProcStatusResult);
			dbProcStatusResult.clearDbstat();
			dbProcStatusResult = dbStatTask.getResultModel();
			if (!dbProcStatusResult.getStatus()) {
				if (interruptReq) {
					return convertMapKey(dbProcStatProxy.getDiagStatusResultMap());
				} else {
					showErrorMsg(dbProcStatusResult);
				}
			}
		}
		dbStatTask.finish();
		return convertMapKey(dbProcStatProxy.getDiagStatusResultMap());
	}

	/**
	 * show sql log view at broker table
	 *
	 * @param type sql type
	 */
	public void showLogView(String type) {
		try {
			int i = brokerInfoTable.getSelectionIndex();
			if (i < 0) {
				return;
			}
			final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (window == null) {
				return;
			}
			String brokerName = brokerInfoTable.getItem(i).getText(0);
			String serverId = brokerInfoTable.getItem(i).getText(1);
			//get all log infor
			BrokerLogInfos brokerLogInfos = new BrokerLogInfos();
			final CommonQueryTask<BrokerLogInfos> task = new CommonQueryTask<BrokerLogInfos>(
					database.getDatabaseInfo().getServerInfo(),
					CommonSendMsg.getGetBrokerLogFileInfoMSGItems(),
					brokerLogInfos);
			task.setBroker(brokerName);
			task.execute();

			brokerLogInfos = task.getResultModel();
			String logFileName = brokerName + "_" + serverId + "." + type
					+ ".log";
			sqlLogViewPartName = logFileName + "@"
					+ database.getServer().getLabel() + ":"
					+ database.getServer().getMonPort();
			List<LogInfo> logInfoList = brokerLogInfos == null ? null
					: brokerLogInfos.getBrokerLogInfoList().getLogFileInfoList();
			task.finish();
			//get the current log
			LogInfo logInfo = null;
			if (logInfoList != null && !logInfoList.isEmpty()) {
				for (LogInfo logInfoInlist : logInfoList) {
					if (logFileName.equals(logInfoInlist.getName())) {
						logInfo = logInfoInlist;
						break;
					}
				}
			}
			if (logInfo == null) {
				String msg = Messages.bind(
						com.cubrid.cubridmanager.ui.logs.Messages.errLogFileNoExist,
						logFileName);
				LOGGER.error(msg);
				//CommonUITool.openErrorBox(msg);
				return;
			}
			final String filePath = logInfo.getPath();
			TaskJobExecutor taskJobExecutor = new TaskJobExecutor() {
				public IStatus exec(IProgressMonitor monitor) {
					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}
					for (ITask task : taskList) {
						task.execute();
						final String msg = task.getErrorMsg();
						if (monitor.isCanceled()) {
							return Status.CANCEL_STATUS;
						}
						if (msg != null && msg.length() > 0
								&& !monitor.isCanceled()) {
							return new Status(IStatus.ERROR,
									CubridManagerUIPlugin.PLUGIN_ID, msg);
						}
						if (task instanceof CheckFileTask) {
							CheckFileTask checkFileTask = (CheckFileTask) task;
							final String[] files = checkFileTask.getExistFiles();
							if (files == null || files.length == 0) {
								return new Status(
										IStatus.ERROR,
										CubridManagerUIPlugin.PLUGIN_ID,
										Messages.bind(
												com.cubrid.cubridmanager.ui.logs.Messages.errLogFileNoExist,
												filePath));
							}
						} else if (task instanceof GetLogListTask) {
							GetLogListTask getLogListTask = (GetLogListTask) task;
							final LogContentInfo logContentInfo = (LogContentInfo) getLogListTask.getLogContent();

							Display.getDefault().syncExec(new Runnable() {
								public void run() {
									try {
										ICubridNode logInfoNode = new DefaultCubridNode(
												"", "", "");
										IEditorPart editor = window.getActivePage().openEditor(
												logInfoNode, LogEditorPart.ID);
										((LogEditorPart) editor).setTableInfo(
												logContentInfo, true);
										((LogEditorPart) editor).setShowLogPartName(sqlLogViewPartName);
									} catch (PartInitException e) {
										LOGGER.error(e.getMessage(), e);
									}
								}
							});
						}
						if (monitor.isCanceled()) {
							return Status.CANCEL_STATUS;
						}
						task.finish();
					}
					return Status.OK_STATUS;
				}
			};

			CheckFileTask checkFileTask = new CheckFileTask(
					cubridNode.getServer().getServerInfo());
			checkFileTask.setFile(new String[]{filePath });
			taskJobExecutor.addTask(checkFileTask);

			GetLogListTask getLogListTask = new GetLogListTask(
					cubridNode.getServer().getServerInfo());
			getLogListTask.setPath(filePath);
			getLogListTask.setStart("1");
			getLogListTask.setEnd("100");
			taskJobExecutor.addTask(getLogListTask);
			String jobName = com.cubrid.cubridmanager.ui.logs.Messages.viewLogJobName
					+ " - "
					+ cubridNode.getName()
					+ "@"
					+ cubridNode.getServer().getName();
			taskJobExecutor.schedule(jobName, null, false, Job.SHORT);
		} catch (Exception e) {
			LOGGER.error(Messages.exportDashboardOpenSQLLogErrMsg, e);
			//			CommonUITool.openErrorBox(Messages.exportDashboardOpenSQLLogErrMsg);
		}
	}

	/**
	 * Get update value
	 *
	 * @param startRun int
	 * @return Map<String, String>
	 */
	private TreeMap<String, String> getDbHitRationTaskValue(int startRun) {
		final CommonQueryTask<DbStatDumpData> task = new CommonQueryTask<DbStatDumpData>(
				database.getServer().getServerInfo(),
				CommonSendMsg.getCommonDatabaseSendMsg(), diagStatusResult);
		task.setDbName(database.getName());
		task.execute();

		TreeMap<String, String> resultMap = null;
		if (startRun == 0) {
			diagStatusResult = task.getResultModel();
			return convertMapKey(diagStatusResult.getDiagStatusResultMap());
		} else if (startRun == 1) {
			lastSec = Calendar.getInstance();
			diagOldOneStatusResult.copy_from(diagStatusResult);
			diagStatusResult = task.getResultModel();
			DbStatDumpData brokerDiagDataDelta = new DbStatDumpData();
			brokerDiagDataDelta.getDelta(diagStatusResult,
					diagOldOneStatusResult);
			return convertMapKey(brokerDiagDataDelta.getDiagStatusResultMap());
		} else {
			nowSec = Calendar.getInstance();
			double interval = (double) (nowSec.getTimeInMillis() - lastSec.getTimeInMillis()) / 1000;
			lastSec = nowSec;
			diagOldTwoStatusResult.copy_from(diagOldOneStatusResult);
			diagOldOneStatusResult.copy_from(diagStatusResult);
			diagStatusResult = task.getResultModel();

			DbStatDumpData diagStatusResultDelta = new DbStatDumpData();
			diagStatusResultDelta.getDelta(diagStatusResult,
					diagOldOneStatusResult, diagOldTwoStatusResult,
					(float) interval);

			resultMap = convertMapKey(diagStatusResultDelta.getDiagStatusResultMap());
			//these filed user the data from task directly
			resultMap.put("num_data_page_fetches",
					diagStatusResult.getNum_data_page_fetches());
			resultMap.put("num_data_page_dirties",
					diagStatusResult.getNum_data_page_dirties());
			resultMap.put("num_data_page_ioreads",
					diagStatusResult.getNum_file_ioreads());
			resultMap.put("num_data_page_iowrites",
					diagStatusResult.getNum_file_iowrites());
		}
		task.finish();
		return resultMap;
	}

	/**
	 * Show error message When the response' status is not success
	 *
	 * @param diagStatusResult this instance of BrokerDiagData
	 */
	private void showErrorMsg(final DbProcStat diagStatusResult) {
		interruptReq = true;
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				errMsgLable.setForeground(ResourceManager.getColor(SWT.COLOR_RED));
				errMsgLable.setText(Messages.bind(
						Messages.exportDashboardLoadDataError1,
						diagStatusResult.getNote()));
			}
		});
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
				errMsgLable.setBackground(ResourceManager.getColor(SWT.COLOR_RED));
				errMsgLable.setText(Messages.bind(
						Messages.exportDashboardLoadDataError1,
						diagStatusResult.getNote()));
			}
		});
	}

	/**
	 * Get update value from hostStatdata task
	 *
	 * @param serverInfo the serverInfo
	 * @param startRun int
	 * @return Map<String, String>
	 */
	private TreeMap<String, String> getHostProcTaskValue(int startRun) {
		final CommonQueryTask<HostStatData> hostStattask = new CommonQueryTask<HostStatData>(
				database.getServer().getServerInfo(),
				CommonSendMsg.getCommonSimpleSendMsg(), hostStatusResult);
		hostStattask.execute();
		if (startRun == 0) {
			hostStatusResult = hostStattask.getResultModel();
			if (!hostStatusResult.getStatus()) {
				if (interruptReq) {
					hostStatDataProxy.getDiagStatusResultMap();
				} else {
					showErrorMsg(hostStatusResult);
				}
			}
		} else if (startRun == 1) {
			hostOldOneStatusResult.copyFrom(hostStatusResult);
			hostStatusResult = hostStattask.getResultModel();
			if (!hostStatusResult.getStatus()) {
				if (interruptReq) {
					return convertMapKey(hostStatDataProxy.getDiagStatusResultMap());
				} else {
					showErrorMsg(hostStatusResult);
				}
			}
			hostStatDataProxy.compute(hostStatusResult, hostOldOneStatusResult);
		} else {
			hostOldTwoStatusResult.copyFrom(hostOldOneStatusResult);
			hostOldOneStatusResult.copyFrom(hostStatusResult);
			hostStatusResult = hostStattask.getResultModel();
			if (!hostStatusResult.getStatus()) {
				if (interruptReq) {
					return convertMapKey(hostStatDataProxy.getDiagStatusResultMap());
				} else {
					showErrorMsg(hostStatusResult);
				}
			}
			hostStatDataProxy.compute(hostStatusResult, hostOldOneStatusResult,
					hostOldTwoStatusResult);

		}
		hostStattask.finish();
		return convertMapKey(hostStatDataProxy.getDiagStatusResultMap());
	}

	/**
	 * Convert the Map key value
	 *
	 * @param inputMap the instance of Map<IDiagPara,String>
	 * @return the instance of TreeMap<String, String>
	 */
	private TreeMap<String, String> convertMapKey(
			Map<IDiagPara, String> inputMap) { // FIXME extract to utility class
		TreeMap<String, String> map = new TreeMap<String, String>();
		for (Map.Entry<IDiagPara, String> entry : inputMap.entrySet()) {
			map.put(entry.getKey().getName(), entry.getValue());
		}
		return map;
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
		super.init(site, input);

		if (input instanceof CubridDatabase) {
			database = (CubridDatabase) input;
		} else if (input instanceof DefaultSchemaNode) {
			ICubridNode node = (DefaultSchemaNode) input;
			if (CubridNodeType.DATABASE_FOLDER.equals(node.getType())) {
				database = ((DefaultSchemaNode) node).getDatabase();
			}
		}
	}

	/**
	 * check whether display the type
	 *
	 * @param type
	 * @return boolean
	 */
	public boolean checkVolumnDisplayType(String type) { // FIXME extract to utility class
		return (VolumeType.INDEX.getText().equalsIgnoreCase(type)
				|| VolumeType.GENERIC.getText().equalsIgnoreCase(type)
				|| VolumeType.DATA.getText().equalsIgnoreCase(type) || VolumeType.TEMP.getText().equalsIgnoreCase(
				type)) ? true : false;
	}

	/**
	 * gete space string info
	 *
	 * @param spaceSize
	 * @return String
	 */
	private String getSpaceDesc(long spaceSize) { // FIXME extract to utility class
		StringBuilder sb = new StringBuilder();
		formater.setMaximumFractionDigits(2);
		if (spaceSize >= 1024 * 1024 * 1024) {
			sb.append(formater.format(StringUtil.convertToG(spaceSize))).append(
					" GB");
			return sb.toString();
		}
		if (spaceSize >= 1024 * 1024) {
			sb.append(formater.format(StringUtil.convertToM(spaceSize))).append(
					" MB");
			return sb.toString();
		}
		if (spaceSize >= 1024) {
			sb.append(formater.format(StringUtil.convertToK(spaceSize))).append(
					" KB");
			return sb.toString();
		}
		sb.append(spaceSize).append(" B");
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see com.cubrid.common.ui.spi.event.ICubridNodeChangedListener#nodeChanged(com.cubrid.common.ui.spi.event.CubridNodeChangedEvent)
	 */
	public void nodeChanged(CubridNodeChangedEvent event) {
		noOp();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
		noOp();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	public void doSaveAs() {
		noOp();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	public boolean isDirty() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return false;
	}
}
