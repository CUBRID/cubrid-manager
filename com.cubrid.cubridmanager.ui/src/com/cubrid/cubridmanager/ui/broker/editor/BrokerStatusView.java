/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution. 
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met: 
 *
 * - Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer. 
 *
 * - Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution. 
 *
 * - Neither the name of the <ORGANIZATION> nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software without 
 *   specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE. 
 *
 */
package com.cubrid.cubridmanager.ui.broker.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.slf4j.Logger;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.part.CubridViewPart;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.broker.model.ApplyServerInfo;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfo;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfoList;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfos;
import com.cubrid.cubridmanager.core.broker.model.BrokerStatusInfos;
import com.cubrid.cubridmanager.core.broker.model.JobInfo;
import com.cubrid.cubridmanager.core.broker.task.GetBrokerStatusInfosTask;
import com.cubrid.cubridmanager.core.broker.task.RestartBrokerTask;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.broker.Messages;
import com.cubrid.cubridmanager.ui.broker.editor.internal.BrokerIntervalSetting;
import com.cubrid.cubridmanager.ui.broker.editor.internal.BrokerIntervalSettingManager;
import com.cubrid.cubridmanager.ui.broker.editor.internal.BrokerStatusAsColumn;
import com.cubrid.cubridmanager.ui.broker.editor.internal.BrokerStatusBasicColumn;
import com.cubrid.cubridmanager.ui.broker.editor.internal.BrokerStatusJqColumn;
import com.cubrid.cubridmanager.ui.broker.editor.internal.BrokerStatusSettingDlg;
import com.cubrid.cubridmanager.ui.broker.editor.internal.BrokerTblColumnSetHelp;
import com.cubrid.cubridmanager.ui.spi.model.CubridBroker;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;

/**
 * A editor part which is responsible for showing the status of a single broker
 * 
 * 
 * @author lizhiqiang
 * @version 1.0 - 2009-5-18 created by lizhiqiang
 */
public class BrokerStatusView extends
		CubridViewPart {

	private static final Logger LOGGER = LogUtil.getLogger(BrokerStatusView.class);
	public static final String ID = "com.cubrid.cubridmanager.ui.broker.editor.BrokerStatusView";
	private TableViewer asTableViewer;
	private TableViewer jqTableViewer;
	private Composite composite;

	private List<ApplyServerInfo> asinfoLst;
	private List<JobInfo> jobinfoLst;
	private CubridBroker brokerNode;
	private boolean runflag = false;
	private String nodeName;
	private boolean isRunning = true;
	private boolean isFirstLoaded = true;
	private List<ApplyServerInfo> oldAsInfoLst;
	private String serverName;
	private String port;
	private List<BrokerInfo> basicInfoLst;
	private TableViewer basicTableViewer;
	private boolean isSupportNewBrokerParamPropery;

	/**
	 * Initializes this view with the given view site.
	 * 
	 * @param site the view site
	 * @exception PartInitException if this view was not initialized
	 *            successfully
	 */
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		initValue();
		String[] titleArgu = new String[]{nodeName, serverName, port };
		setPartName(Messages.bind(Messages.headTitel, titleArgu));
		assert (null != brokerNode);
		if (brokerNode.isRunning()) {
			this.setTitleImage(CubridManagerUIPlugin.getImage("icons/navigator/broker_started.png"));
		} else {
			this.setTitleImage(CubridManagerUIPlugin.getImage("icons/navigator/broker.png"));
		}
		runflag = brokerNode.isRunning();
		isSupportNewBrokerParamPropery = CompatibleUtil.isSupportNewBrokerParamPropery1(brokerNode.getServer().getServerInfo());
		//brokerNode.getServer().getServerInfo().compareVersionKey("8.2.2");
	}

	/**
	 * Initializes the parameters of this view
	 */
	public void initValue() {
		if (null == getCubridNode()
				|| !CubridNodeType.BROKER.equals(getCubridNode().getType())) {
			return;
		}
		brokerNode = (CubridBroker) getCubridNode();
		nodeName = brokerNode.getLabel().trim();
		serverName = brokerNode.getServer().getLabel();
		port = brokerNode.getServer().getMonPort();
		ServerInfo serverInfo = brokerNode.getServer().getServerInfo();
		//get basic info
		BrokerInfos brokerInfos = new BrokerInfos();
		//brokerInfos.setBname(nodeName);
		final GetBrokerStatusInfosTask<BrokerInfos> basicTask = new GetBrokerStatusInfosTask<BrokerInfos>(
				serverInfo, CommonSendMsg.getGetBrokerStatusItems(),
				brokerInfos);
		basicTask.setBrokerName(nodeName);

		//get status
		BrokerStatusInfos brokerStatusInfos = new BrokerStatusInfos();
		final GetBrokerStatusInfosTask<BrokerStatusInfos> statisTask = new GetBrokerStatusInfosTask<BrokerStatusInfos>(
				serverInfo, CommonSendMsg.getGetBrokerStatusItems(),
				brokerStatusInfos);
		statisTask.setBrokerName(nodeName);

		TaskExecutor taskExecutor = new CommonTaskExec(
				Messages.showBrokerStatusTaskName);
		taskExecutor.addTask(basicTask);
		taskExecutor.addTask(statisTask);

		new ExecTaskWithProgress(taskExecutor).exec();

		if (!taskExecutor.isSuccess()) {
			return;
		}

		brokerInfos = basicTask.getResultModel();
		if (null != brokerInfos) {
			BrokerInfoList list = brokerInfos.getBorkerInfoList();
			if (list != null && list.getBrokerInfoList() != null) {
				basicInfoLst = list.getBrokerInfoList();
			}
		}

		brokerStatusInfos = statisTask.getResultModel();
		if (brokerStatusInfos != null) {
			asinfoLst = brokerStatusInfos.getAsinfo();
			jobinfoLst = brokerStatusInfos.getJobinfo();
		}

	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 * 
	 * @param parent the parent composite
	 */
	public void createPartControl(Composite parent) {
		final ScrolledComposite scrolledComp = new ScrolledComposite(parent,
				SWT.V_SCROLL | SWT.H_SCROLL);

		composite = new Composite(scrolledComp, SWT.NONE);
		composite.setLayout(new GridLayout());

		BrokerTblColumnSetHelp bcsh = BrokerTblColumnSetHelp.getInstance();
		bcsh.loadSetting(
				BrokerTblColumnSetHelp.StatusColumn.BrokerStatusBasicColumn,
				BrokerStatusBasicColumn.values());
		bcsh.loadSetting(
				BrokerTblColumnSetHelp.StatusColumn.BrokerStatusAsColumn,
				BrokerStatusAsColumn.values());
		bcsh.loadSetting(
				BrokerTblColumnSetHelp.StatusColumn.BrokerStatusJqColumn,
				BrokerStatusJqColumn.values());
		if (isSupportNewBrokerParamPropery) {
			createBasicTable(composite);
		}
		createAsTable(composite);
		createJobTable(composite);

		makeActions();

		composite.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent event) {
				updateTableLayout();
			}
		});
		scrolledComp.setContent(composite);
		scrolledComp.setExpandHorizontal(true);
		scrolledComp.setExpandVertical(true);
		scrolledComp.setMinHeight(300);
		scrolledComp.setMinWidth(800);

		new StatusUpdate().start();
	}

	/**
	 * Create basic info table
	 * 
	 * @param comp the parent composite
	 * 
	 */
	private void createBasicTable(Composite comp) {
		final Composite basicComposite = new Composite(comp, SWT.NONE);
		GridData gdBasic = new GridData(SWT.FILL, SWT.NONE, false, false);
		basicComposite.setLayoutData(gdBasic);
		basicComposite.setLayout(new GridLayout());

		basicTableViewer = new TableViewer(basicComposite, SWT.NO_SCROLL
				| SWT.BORDER);
		Table basicTable = basicTableViewer.getTable();
		basicTable.setHeaderVisible(true);
		basicTable.setLinesVisible(true);
		GridData tblBasic = new GridData(SWT.FILL, SWT.TOP, true, false);

		tblBasic.heightHint = CommonUITool.getHeightHintOfTable(basicTable);
		basicTable.setLayoutData(tblBasic);

		TableLayout basicLayout = new TableLayout();
		setBasicLayout(basicLayout);
		basicTable.setLayout(basicLayout);
		basicTable.setBackground(basicComposite.getBackground());

		TableColumn tblColumn = new TableColumn(basicTable, SWT.CENTER);
		tblColumn.setText(Messages.tblBscPid);
		tblColumn.setToolTipText(Messages.tblBscPid);
		tblColumn = new TableColumn(basicTable, SWT.CENTER);
		tblColumn.setText(Messages.tblBscPort);
		tblColumn.setToolTipText(Messages.tblBscPort);
		tblColumn = new TableColumn(basicTable, SWT.CENTER);
		tblColumn.setText(Messages.tblBscJobQueue);
		tblColumn.setToolTipText(Messages.tblBscJobQueue);
		tblColumn = new TableColumn(basicTable, SWT.CENTER);
		tblColumn.setText(Messages.tblBscAutoAddAs);
		tblColumn.setToolTipText(Messages.tblBscAutoAddAs);
		tblColumn = new TableColumn(basicTable, SWT.CENTER);
		tblColumn.setText(Messages.tblBscSqlLogMode);
		tblColumn.setToolTipText(Messages.tblBscSqlLogMode);
		tblColumn = new TableColumn(basicTable, SWT.CENTER);
		tblColumn.setText(Messages.tblBscLongTranTime);
		tblColumn.setToolTipText(Messages.tblBscLongTranTime);
		tblColumn = new TableColumn(basicTable, SWT.CENTER);
		tblColumn.setText(Messages.tblBscLongQueryTime);
		tblColumn.setToolTipText(Messages.tblBscLongQueryTime);
		tblColumn = new TableColumn(basicTable, SWT.CENTER);
		tblColumn.setText(Messages.tblBscSessionTimeout);
		tblColumn.setToolTipText(Messages.tblBscSessionTimeout);
		tblColumn = new TableColumn(basicTable, SWT.CENTER);
		tblColumn.setText(Messages.tblBscKeepConn);
		tblColumn.setToolTipText(Messages.tblBscKeepConn);
		tblColumn = new TableColumn(basicTable, SWT.CENTER);
		tblColumn.setText(Messages.tblBscAccessMode);
		tblColumn.setToolTipText(Messages.tblBscAccessMode);

		basicTableViewer.setContentProvider(new BrokerBasicInfoContentProvider());
		ServerInfo serverInfo = cubridNode.getServer().getServerInfo();
		BrokerBasicInfoLabelProvider basicInfoLabelProvider = new BrokerBasicInfoLabelProvider();
		basicInfoLabelProvider.setServerInfo(serverInfo);
		basicTableViewer.setLabelProvider(basicInfoLabelProvider);
		basicTableViewer.setInput(basicInfoLst);
	}

	/**
	 * Set the basic info table layout based on the different column.
	 * 
	 * @param layout the table layout
	 */
	private void setBasicLayout(TableLayout layout) {
		for (BrokerStatusBasicColumn column : BrokerStatusBasicColumn.values()) {
			if (column.getValue() == -1) {
				layout.addColumnData(new ColumnWeightData(0, 0, false));
			} else {
				switch (column) {
				case PID:
					layout.addColumnData(new ColumnWeightData(7, 50, true));
					break;
				case PORT:
					layout.addColumnData(new ColumnWeightData(7, 50, true));
					break;
				default:
					layout.addColumnData(new ColumnWeightData(10, 50, true));
				}
			}
		}

	}

	/**
	 * Create app server table
	 * 
	 * @param comp the parent composite
	 * 
	 */
	private void createAsTable(Composite comp) {
		Composite asComposite = new Composite(comp, SWT.NONE);
		GridData gdAs = new GridData(SWT.FILL, SWT.FILL, true, true);
		asComposite.setLayoutData(gdAs);
		asComposite.setLayout(new GridLayout());

		asTableViewer = new TableViewer(asComposite, SWT.FULL_SELECTION
				| SWT.NO_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		Table asTable = asTableViewer.getTable();
		asTable.setHeaderVisible(true);
		asTable.setLinesVisible(true);
		asTable.setLayoutData(gdAs);

		TableLayout asLayout = new TableLayout();
		setAsLayout(asLayout);
		asTable.setLayout(asLayout);

		TableColumn tblColumn = new TableColumn(asTable, SWT.CENTER);
		tblColumn.setText(Messages.tblAsId);
		tblColumn.setToolTipText(Messages.tblAsId);
		tblColumn = new TableColumn(asTable, SWT.CENTER);
		tblColumn.setText(Messages.tblAsProcess);
		tblColumn.setToolTipText(Messages.tblAsProcess);
		tblColumn = new TableColumn(asTable, SWT.CENTER);
		tblColumn.setText(Messages.tblAsQps);
		tblColumn.setToolTipText(Messages.tblAsQps);
		tblColumn = new TableColumn(asTable, SWT.CENTER);
		tblColumn.setText(Messages.tblAsLqs);
		tblColumn.setToolTipText(Messages.tblAsLqs);
		tblColumn = new TableColumn(asTable, SWT.CENTER);
		tblColumn.setText(Messages.tblAsPort);
		tblColumn.setToolTipText(Messages.tblAsPort);
		tblColumn = new TableColumn(asTable, SWT.CENTER);
		tblColumn.setText(Messages.tblAsSize);
		tblColumn.setToolTipText(Messages.tblAsSize);
		tblColumn = new TableColumn(asTable, SWT.CENTER);
		tblColumn.setText(Messages.tblAsStatus);
		tblColumn.setToolTipText(Messages.tblAsStatus);
		tblColumn = new TableColumn(asTable, SWT.CENTER);
		tblColumn.setText(Messages.tblAsDb);
		tblColumn.setToolTipText(Messages.tblAsDb);
		tblColumn = new TableColumn(asTable, SWT.CENTER);
		tblColumn.setText(Messages.tblAsHost);
		tblColumn.setToolTipText(Messages.tblAsHost);
		tblColumn = new TableColumn(asTable, SWT.CENTER);
		tblColumn.setText(Messages.tblAsLastAccess);
		tblColumn.setToolTipText(Messages.tblAsLastAccess);
		tblColumn = new TableColumn(asTable, SWT.CENTER);
		tblColumn.setText(Messages.tblAsLct);
		tblColumn.setToolTipText(Messages.tblAsLct);
		tblColumn = new TableColumn(asTable, SWT.CENTER);
		tblColumn.setText(Messages.tblAsClientIp);
		tblColumn.setToolTipText(Messages.tblAsClientIp);
		tblColumn = new TableColumn(asTable, SWT.CENTER);
		tblColumn.setText(Messages.tblAsCur);
		tblColumn.setToolTipText(Messages.tblAsCur);

		asTableViewer.setContentProvider(new ApplyServerContentProvider());
		asTableViewer.setLabelProvider(new ApplyServerLabelProvider());
		asTableViewer.setInput(asinfoLst);

		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				IStructuredSelection selection = (IStructuredSelection) asTableViewer.getSelection();
				ApplyServerInfo as = (ApplyServerInfo) (selection.toArray()[0]);
				RestartAction restartAcion = new RestartAction(as.getAsId());
				manager.add(restartAcion);
			}
		});
		Menu contextMenu = menuManager.createContextMenu(asTableViewer.getControl());
		asTableViewer.getControl().setMenu(contextMenu);

	}

	/**
	 * Set the apply server table layout based on the different column.
	 * 
	 * @param layout the table layout
	 */
	private void setAsLayout(TableLayout layout) {
		for (BrokerStatusAsColumn column : BrokerStatusAsColumn.values()) {
			if (column.getValue() == -1) {
				layout.addColumnData(new ColumnWeightData(0, 0, false));
			} else {
				switch (column) {
				case PSIZE:
				case STATUS:
				case CLIENT_IP:
					layout.addColumnData(new ColumnWeightData(25, 25, true));
					break;
				case PORT:
					if (CompatibleUtil.isSupportBrokerPort(brokerNode.getServer().getServerInfo())) {
						layout.addColumnData(new ColumnWeightData(20, 20, true));
					} else {
						layout.addColumnData(new ColumnWeightData(0, 0, false));
					}
					break;
				case DB:
				case HOST:
					layout.addColumnData(new ColumnWeightData(30, 30, true));
					break;
				case LAST_ACCESS_TIME:
				case LAST_CONNECT_TIME:
					layout.addColumnData(new ColumnWeightData(70, 70, true));
					break;
				case SQL:
					layout.addColumnData(new ColumnWeightData(150, 150, true));
					break;
				default:
					layout.addColumnData(new ColumnWeightData(20, 20, true));
				}
			}
		}
	}

	/**
	 * Create job table composite
	 * 
	 * @param comp the composite
	 * 
	 */
	private void createJobTable(Composite comp) {
		Composite jobComposite = new Composite(comp, SWT.NONE);
		GridData gdJob = new GridData(SWT.FILL, SWT.FILL, true, true);
		jobComposite.setLayoutData(gdJob);
		jobComposite.setLayout(new GridLayout());

		final Label label = new Label(jobComposite, SWT.CENTER);
		label.setText(Messages.jobTblTitle);
		jqTableViewer = new TableViewer(jobComposite, SWT.FULL_SELECTION
				| SWT.NO_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		jqTableViewer.getTable().setHeaderVisible(true);
		jqTableViewer.getTable().setLinesVisible(true);

		TableLayout jqLayout = new TableLayout();
		setJqLayout(jqLayout);
		jqTableViewer.getTable().setLayout(jqLayout);
		jqTableViewer.getTable().setLayoutData(gdJob);

		TableColumn tblColumn = new TableColumn(jqTableViewer.getTable(),
				SWT.CENTER);
		tblColumn.setText(Messages.tblJobId);
		tblColumn.setToolTipText(Messages.tblJobId);
		tblColumn = new TableColumn(jqTableViewer.getTable(), SWT.CENTER);
		tblColumn.setText(Messages.tblJobPriority);
		tblColumn.setToolTipText(Messages.tblJobPriority);
		tblColumn = new TableColumn(jqTableViewer.getTable(), SWT.CENTER);
		tblColumn.setText(Messages.tblJobAddress);
		tblColumn.setToolTipText(Messages.tblJobAddress);
		tblColumn = new TableColumn(jqTableViewer.getTable(), SWT.CENTER);
		tblColumn.setText(Messages.tblJobTime);
		tblColumn.setToolTipText(Messages.tblJobTime);
		tblColumn = new TableColumn(jqTableViewer.getTable(), SWT.CENTER);
		tblColumn.setText(Messages.tblJobRequest);
		tblColumn.setToolTipText(Messages.tblJobRequest);

		jqTableViewer.setContentProvider(new JobContentProvider());
		jqTableViewer.setLabelProvider(new JobLabelProvider());
		jqTableViewer.setInput(jobinfoLst);
	}

	/**
	 * Set the Job queue table layout based on the different column.
	 * 
	 * @param layout the table layout
	 */
	private void setJqLayout(TableLayout layout) {
		for (BrokerStatusJqColumn column : BrokerStatusJqColumn.values()) {
			if (column.getValue() == -1) {
				layout.addColumnData(new ColumnWeightData(0, 0, false));
			} else {
				layout.addColumnData(new ColumnWeightData(10, 50, true));
			}
		}
	}

	/**
	 * This method is to create actions at tool bar
	 * 
	 */
	private void makeActions() {
		Action columnAction = new Action() {
			public void run() {
				BrokerStatusSettingDlg settingDlg = new BrokerStatusSettingDlg(
						null);
				ServerInfo serverInfo = brokerNode.getServer().getServerInfo();
				settingDlg.setServerInfo(serverInfo);
				if (settingDlg.open() == Dialog.OK) {
					refreshLayout();
				}
			}

			/**
			 * refresh table layout
			 */
			private void refreshLayout() {
				//refresh basic info table
				if (isSupportNewBrokerParamPropery) {
					TableLayout basicLayout = new TableLayout();
					setBasicLayout(basicLayout);
					basicTableViewer.getTable().setLayout(basicLayout);
					basicTableViewer.getTable().layout();
				}
				//refresh apply server table
				TableLayout asLayout = new TableLayout();
				setAsLayout(asLayout);
				asTableViewer.getTable().setLayout(asLayout);
				asTableViewer.getTable().layout();

				//refresh job queue table
				TableLayout jqLayout = new TableLayout();
				setJqLayout(jqLayout);
				jqTableViewer.getTable().setLayout(jqLayout);
				jqTableViewer.getTable().layout();
			}
		};
		columnAction.setText(Messages.columnSettingTxt);
		columnAction.setImageDescriptor(CubridManagerUIPlugin.getImageDescriptor("icons/action/setting-small.png"));

		IActionBars bars = getViewSite().getActionBars();
		IToolBarManager manager = bars.getToolBarManager();
		manager.add(columnAction);
	}

	/**
	 * An action that is an inner class in order to execute restarting a certain
	 * server
	 * 
	 * @author lizhiqiang
	 * @version 1.0 - 2009-5-22 created by lizhiqiang
	 */
	private class RestartAction extends
			Action {
		private final String serverId;

		public RestartAction(String serverId) {
			setText(Messages.bind(Messages.restartBrokerServerTip, serverId));
			this.serverId = serverId;
		}

		/**
		 * Restart the app server
		 */
		public void run() {
			if (!CommonUITool.openConfirmBox(Messages.bind(
					Messages.restartBrokerServerMsg, serverId))) {
				return;
			}
			ServerInfo serverInfo = brokerNode.getServer().getServerInfo();
			RestartBrokerTask task = new RestartBrokerTask(serverInfo);
			task.setBrokerName(brokerNode.getLabel());
			task.setApplyServerNum(serverId);

			TaskExecutor taskExecutor = new CommonTaskExec(Messages.bind(
					Messages.restartBrokerServerTaskName, serverId));
			taskExecutor.addTask(task);
			new ExecTaskWithProgress(taskExecutor).exec();

			if (!taskExecutor.isSuccess()) {
				return;
			}

			initValue();

			asTableViewer.refresh();
			jqTableViewer.refresh();

		}
	}

	/**
	 * Refresh this view
	 * 
	 */
	public void refresh() {
		ServerInfo site = brokerNode.getServer().getServerInfo();
		if (isSupportNewBrokerParamPropery) {
			refreshBasicTable(site);
		}
		BrokerStatusInfos brokerStatusInfos = new BrokerStatusInfos();
		final GetBrokerStatusInfosTask<BrokerStatusInfos> task = new GetBrokerStatusInfosTask<BrokerStatusInfos>(
				site, CommonSendMsg.getGetBrokerStatusItems(),
				brokerStatusInfos);
		task.setBrokerName(nodeName);
		task.execute();

		if (!task.isSuccess()) {
			return;
		}

		brokerStatusInfos = task.getResultModel();

		asinfoLst = brokerStatusInfos.getAsinfo();
		jobinfoLst = brokerStatusInfos.getJobinfo();

		asTableViewer.setInput(asinfoLst);
		asTableViewer.refresh();
		jqTableViewer.setInput(jobinfoLst);
		jqTableViewer.refresh();
	}

	/**
	 * refresh the basic info table viewer
	 * 
	 * @param serverInfo the instance of ServerInfo
	 */
	private void refreshBasicTable(ServerInfo serverInfo) {
		BrokerInfos brokerInfos = new BrokerInfos();
		final GetBrokerStatusInfosTask<BrokerInfos> basicTask = new GetBrokerStatusInfosTask<BrokerInfos>(
				serverInfo, CommonSendMsg.getGetBrokerStatusItems(),
				brokerInfos);
		basicTask.setBrokerName(nodeName);
		basicTask.execute();

		brokerInfos = basicTask.getResultModel();
		if (null != brokerInfos) {
			BrokerInfoList list = brokerInfos.getBorkerInfoList();
			if (list != null && list.getBrokerInfoList() != null) {
				basicInfoLst = list.getBrokerInfoList();
			}
		}
		if (basicTableViewer != null) {
			basicTableViewer.setInput(basicInfoLst);
			basicTableViewer.refresh();
		}
	}

	/**
	 * 
	 * Refresh the page content
	 * 
	 * @param isUpdateTable whether update table
	 * @param isRefreshChanged whether refresh changed
	 */
	public void refresh(boolean isUpdateTable, boolean isRefreshChanged) {
		ServerInfo site = brokerNode.getServer().getServerInfo();
		if (isSupportNewBrokerParamPropery) {
			refreshBasicTable(site);
		}
		BrokerStatusInfos brokerStatusInfos = new BrokerStatusInfos();
		final GetBrokerStatusInfosTask<BrokerStatusInfos> task = new GetBrokerStatusInfosTask<BrokerStatusInfos>(
				site, CommonSendMsg.getGetBrokerStatusItems(),
				brokerStatusInfos);
		task.setBrokerName(nodeName);
		task.execute();

		if (!task.isSuccess()) {
			return;
		}
		brokerStatusInfos = task.getResultModel();

		//job queue
		if (brokerStatusInfos != null) {
			jobinfoLst = brokerStatusInfos.getJobinfo();
			jqTableViewer.setInput(jobinfoLst);
			jqTableViewer.refresh();
		}

		//apply server
		List<ApplyServerInfo> newAsInfoLst = null;
		if (null != brokerStatusInfos) {
			newAsInfoLst = brokerStatusInfos.getAsinfo();
		}
		List<ApplyServerInfo> changedAsInfoLst = new ArrayList<ApplyServerInfo>();
		for (int i = 0; newAsInfoLst != null && i < newAsInfoLst.size(); i++) {
			ApplyServerInfo newAsInfo = newAsInfoLst.get(i);
			ApplyServerInfo changedAsInfo = newAsInfo.clone();
			for (int j = 0; oldAsInfoLst != null && j < oldAsInfoLst.size(); j++) {
				ApplyServerInfo oldAsInfo = oldAsInfoLst.get(j);
				if (newAsInfo.getAsId().equalsIgnoreCase(oldAsInfo.getAsId())) {
					long newQuery = StringUtil.intValue(newAsInfo.getAsNumQuery());
					long newTran = StringUtil.intValue(newAsInfo.getAsNumTran());
					long newLongQuery = StringUtil.longValue(newAsInfo.getAsLongQuery());
					long newLongTran = StringUtil.longValue(newAsInfo.getAsLongTran());
					long newErrQuery = StringUtil.intValue(newAsInfo.getAsErrorQuery());

					long oldQuery = StringUtil.intValue(oldAsInfo.getAsNumQuery());
					long oldTran = StringUtil.intValue(oldAsInfo.getAsNumTran());
					long oldLongQuery = StringUtil.longValue(oldAsInfo.getAsLongQuery());
					long oldLongTran = StringUtil.longValue(oldAsInfo.getAsLongTran());
					long oldErrQuery = StringUtil.intValue(oldAsInfo.getAsErrorQuery());

					long changedQuery = newQuery - oldQuery;
					long changedTran = newTran - oldTran;
					long changedLongTran = newLongTran - oldLongTran;
					long changedLongQuery = newLongQuery - oldLongQuery;
					long changedErrQuery = newErrQuery - oldErrQuery;

					changedAsInfo.setAsNumQuery(String.valueOf(changedQuery > 0 ? changedQuery
							: 0));
					changedAsInfo.setAsNumTran(String.valueOf(changedTran > 0 ? changedTran
							: 0));
					changedAsInfo.setAsLongTran(String.valueOf(changedLongTran > 0 ? changedLongTran
							: 0));
					changedAsInfo.setAsLongQuery(String.valueOf(changedLongQuery > 0 ? changedLongQuery
							: 0));
					changedAsInfo.setAsErrorQuery(String.valueOf(changedErrQuery > 0 ? changedErrQuery
							: 0));
					break;
				}
			}
			changedAsInfoLst.add(changedAsInfo);
		}
		oldAsInfoLst = newAsInfoLst;
		if (isUpdateTable) {
			if (isRefreshChanged) {
				asTableViewer.setInput(changedAsInfoLst);
			} else {
				asTableViewer.setInput(oldAsInfoLst);
			}
		}
		asTableViewer.refresh();
		//job queue
		jobinfoLst = brokerStatusInfos.getJobinfo();
		jqTableViewer.setInput(jobinfoLst);
		jqTableViewer.refresh();
	}

	/**
	 * Response to node changes
	 * 
	 * @param event the node changed event
	 */
	public void nodeChanged(CubridNodeChangedEvent event) {
		ICubridNode eventNode = event.getCubridNode();
		if (eventNode == null || brokerNode == null) {
			return;
		}
		//if it is not in the same host,return
		if (!eventNode.getServer().getId().equals(
				brokerNode.getServer().getId())) {
			return;
		}
		String type = eventNode.getType();
		if (!CubridNodeType.BROKER_FOLDER.equals(type)) {
			return;
		}
		synchronized (this) {
			String id = brokerNode.getId();
			CubridBroker currentNode = (CubridBroker) eventNode.getChild(id);
			this.brokerNode = currentNode;
			if (currentNode == null || !currentNode.isRunning()) {
				setRunflag(false);
				this.setTitleImage(CubridManagerUIPlugin.getImage("icons/navigator/broker.png"));
				if (asTableViewer != null && asTableViewer.getTable() != null
						&& !asTableViewer.getTable().isDisposed()) {
					asTableViewer.getTable().removeAll();
				}
				if (jqTableViewer != null && jqTableViewer.getTable() != null
						&& !jqTableViewer.getTable().isDisposed()) {
					jqTableViewer.getTable().removeAll();
				}
			} else {
				setRunflag(true);
				this.setTitleImage(CubridManagerUIPlugin.getImage("icons/navigator/broker_started.png"));
				refresh(true, false);
			}
		}
	}

	/**
	 * A inner class which extends the Thread and calls the refresh method
	 * 
	 * @author lizhiqiang
	 * @version 1.0 - 2009-5-30 created by lizhiqiang
	 */
	private class StatusUpdate extends
			Thread {

		/**
		 * Update status
		 */
		public void run() {
			int count = 0;
			while (isRunning) {
				String serverName = brokerNode.getServer().getLabel();
				BrokerIntervalSetting brokerIntervalSetting = BrokerIntervalSettingManager.getInstance().getBrokerIntervalSetting(
						serverName, brokerNode.getLabel());
				final int term = Integer.parseInt(brokerIntervalSetting.getInterval());
				final int timeCount = count;
				if (getRunflag() && brokerIntervalSetting.isOn() && term > 0) {
					isFirstLoaded = false;
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							if (composite != null && !composite.isDisposed()) {
								refresh(timeCount % term == 0, true);
							}
						}
					});

					try {
						if (count % term == 0) {
							count = 0;
						}
						count++;
						Thread.sleep(1000);
					} catch (Exception e) {
						LOGGER.error(e.getMessage());
					}
				} else {
					if (isFirstLoaded) {
						isFirstLoaded = false;
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								if (composite != null
										&& !composite.isDisposed()) {
									refresh(true, false);
								}
							}
						});
					}
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						LOGGER.error(e.getMessage());
					}
				}
			}
		}
	}

	/**
	 * Gets the value of runflag
	 * 
	 * @return <code>true</code> whether is running;<code>false</code>otherwise
	 */
	private boolean getRunflag() {
		synchronized (this) {
			return runflag;
		}
	}

	/**
	 * Sets the value of runflag
	 * 
	 * @param runflag whether is running
	 */
	private void setRunflag(boolean runflag) {
		synchronized (this) {
			this.runflag = runflag;
		}
	}

	/**
	 * Dispose the resource
	 */
	public void dispose() {
		isRunning = false;
		runflag = false;
		asTableViewer = null;
		super.dispose();
	}

	/**
	 * Update table layout
	 */
	private void updateTableLayout() {
		if (basicTableViewer != null) {
			TableLayout basicLayout = new TableLayout();
			setBasicLayout(basicLayout);
			basicTableViewer.getTable().setLayout(basicLayout);
			basicTableViewer.getTable().layout();
		}

		TableLayout asLayout = new TableLayout();
		setAsLayout(asLayout);
		asTableViewer.getTable().setLayout(asLayout);
		asTableViewer.getTable().layout();

		TableLayout jqLayout = new TableLayout();
		setJqLayout(jqLayout);
		jqTableViewer.getTable().setLayout(jqLayout);
		jqTableViewer.getTable().layout();
	}

}
