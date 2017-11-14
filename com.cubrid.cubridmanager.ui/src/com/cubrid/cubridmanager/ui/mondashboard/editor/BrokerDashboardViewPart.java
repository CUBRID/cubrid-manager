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
package com.cubrid.cubridmanager.ui.mondashboard.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
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
import com.cubrid.cubridmanager.core.broker.task.RestartBrokerTask;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.broker.Messages;
import com.cubrid.cubridmanager.ui.broker.editor.internal.BrokerSatusTablePart;
import com.cubrid.cubridmanager.ui.broker.editor.internal.BrokerStatusAsColumn;
import com.cubrid.cubridmanager.ui.broker.editor.internal.BrokerStatusBasicColumn;
import com.cubrid.cubridmanager.ui.broker.editor.internal.BrokerStatusJqColumn;
import com.cubrid.cubridmanager.ui.broker.editor.internal.BrokerStatusSettingDlg;
import com.cubrid.cubridmanager.ui.broker.editor.internal.BrokerTblColumnSetHelp;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataChangedEvent;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataGenerator;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataGeneratorPool;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataProvider;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataUpdateListener;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.BrokerNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HANode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HostNode;

/**
 * A editor part which is responsible for showing the status of a single broker
 * 
 * 
 * @author lizhiqiang
 * @version 1.0 - 2009-5-18 created by lizhiqiang
 */
public class BrokerDashboardViewPart extends
		ViewPart implements
		DataUpdateListener {
	public static final String ID = BrokerDashboardViewPart.class.getName();
	private TableViewer asTableViewer;
	private TableViewer jqTableViewer;
	private Composite composite;
	private BrokerNode brokerNode;

	private TableViewer basicTableViewer;
	private BrokerSatusTablePart brokerTablePart;
	private ServerInfo serverInfo;
	private DataGenerator generator;

	/**
	 * Initializes this view with the given view site.
	 * 
	 * @param site the view site
	 * @exception PartInitException if this view was not initialized
	 *            successfully
	 */
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		brokerTablePart = new BrokerSatusTablePart();

	}

	/**
	 * Initializes the parameters of this view
	 * 
	 * @param brokerNode the instance of BrokerNode
	 */
	public void init(BrokerNode brokerNode) {
		this.brokerNode = brokerNode;
		String nodeName = brokerNode.getBrokerName();
		String partName = getPartName();
		HostNode hn = brokerNode.getParent();
		String postfix = " - " + nodeName + "@" + hn.getIp() + ":"
				+ hn.getPort();
		if (!partName.endsWith(postfix)) {
			setPartName(partName + postfix);
		}
		serverInfo = hn.getServerInfo();
		String generatorName = hn.getUserName() + "@" + hn.getIp() + ":"
				+ hn.getPort();
		generator = DataGeneratorPool.getInstance().getDataGenerator(
				generatorName, new DataProvider());
		generator.addDataUpdateListener(this);
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
		brokerTablePart.setServerInfo(serverInfo);
		brokerTablePart.setAppendDiag(true);
		basicTableViewer = brokerTablePart.createBasicTable(composite);

		asTableViewer = brokerTablePart.createAsTable(composite);
		jqTableViewer = brokerTablePart.createJobTable(composite);

		makeActions();

		composite.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent event) {
				updateTableLayout();
			}
		});
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

		scrolledComp.setContent(composite);
		scrolledComp.setExpandHorizontal(true);
		scrolledComp.setExpandVertical(true);
		scrolledComp.setMinHeight(300);
		scrolledComp.setMinWidth(800);
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
				ServerInfo serverInfo = brokerNode.getParent().getServerInfo();
				settingDlg.setServerInfo(serverInfo);
				settingDlg.setAppendDiag(true);
				if (settingDlg.open() == Dialog.OK) {
					refreshLayout();
				}
			}

			/**
			 * refresh table layout
			 */
			private void refreshLayout() {
				//refresh basic info table
				TableLayout basicLayout = new TableLayout();
				brokerTablePart.setBasicLayout(basicLayout);
				basicTableViewer.getTable().setLayout(basicLayout);
				basicTableViewer.getTable().layout();

				//refresh apply server table
				TableLayout asLayout = new TableLayout();
				brokerTablePart.setAsLayout(asLayout);
				asTableViewer.getTable().setLayout(asLayout);
				asTableViewer.getTable().layout();

				//refresh job queue table
				TableLayout jqLayout = new TableLayout();
				brokerTablePart.setJqLayout(jqLayout);
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
			ServerInfo serverInfo = brokerNode.getParent().getServerInfo();
			RestartBrokerTask task = new RestartBrokerTask(serverInfo);
			task.setBrokerName(brokerNode.getBrokerName());
			task.setApplyServerNum(serverId);

			TaskExecutor taskExecutor = new CommonTaskExec(Messages.bind(
					Messages.restartBrokerServerTaskName, serverId));
			taskExecutor.addTask(task);
			new ExecTaskWithProgress(taskExecutor).exec();

			if (!taskExecutor.isSuccess()) {
				return;
			}

			init(brokerNode);

			asTableViewer.refresh();
			jqTableViewer.refresh();

		}
	}

	/**
	 * Dispose the resource
	 */
	public void dispose() {
		jqTableViewer = null;
		asTableViewer = null;
		basicTableViewer = null;
		synchronized (this) {
			generator.removeDataUpdateListener(this);
		}
		super.dispose();
	}

	/**
	 * Update table layout
	 */
	private void updateTableLayout() {
		TableLayout basicLayout = new TableLayout();
		brokerTablePart.setBasicLayout(basicLayout);
		basicTableViewer.getTable().setLayout(basicLayout);
		basicTableViewer.getTable().layout();

		TableLayout asLayout = new TableLayout();
		brokerTablePart.setAsLayout(asLayout);
		asTableViewer.getTable().setLayout(asLayout);
		asTableViewer.getTable().layout();

		TableLayout jqLayout = new TableLayout();
		brokerTablePart.setJqLayout(jqLayout);
		jqTableViewer.getTable().setLayout(jqLayout);
		jqTableViewer.getTable().layout();
	}

	/**
	 * Response to the given event
	 * 
	 * @param event the instance of CubridNodeChangedEvent
	 */
	public void nodeChanged(CubridNodeChangedEvent event) {
		//do nothing

	}

	/**
	 * Call this method when this editor is focus
	 */
	public void setFocus() {
		LayoutManager.getInstance().getTitleLineContrItem().changeTitleForViewOrEditPart(
				null, this);
		LayoutManager.getInstance().getStatusLineContrItem().changeStuatusLineForViewOrEditPart(
				null, this);
	}

	/**
	 * Get the type of Model
	 * 
	 * @return brokerNode
	 */
	public HANode getModel() {
		return brokerNode;
	}

	/**
	 * Perform update UI when data change event happen
	 * 
	 * @param dataChangedEvent the instance of DataChangedEvent
	 */
	public void performUpdate(DataChangedEvent dataChangedEvent) {
		if (composite == null || composite.isDisposed()) {
			return;
		}
		if (dataChangedEvent != null) {
			String brokerName = brokerNode.getBrokerName();
			BrokerInfos brokerInfos = dataChangedEvent.getBrokerInfosMap().get(
					brokerName);
			BrokerStatusInfos brokerStatusInfos = dataChangedEvent.getBrokerStatusInfosMap().get(
					brokerName);
			BrokerDiagData brokerDiagData = dataChangedEvent.getBrokerDiagDataMap().get(
					brokerName);

			List<String> basicTableLst = new ArrayList<String>();
			if (brokerInfos == null) {
				for (int i = 0; i < 10; i++) {
					basicTableLst.add("");
				}
			} else {
				BrokerInfoList brokerInfoList = brokerInfos.getBorkerInfoList();
				if (brokerInfoList.getBrokerInfoList().isEmpty()) {
					for (int i = 0; i < 10; i++) {
						basicTableLst.add("");
					}
				}
				for (BrokerInfo bi : brokerInfoList.getBrokerInfoList()) {
					if (brokerName.equals(bi.getName())) {
						basicTableLst.add(bi.getPid());
						basicTableLst.add(bi.getPort());
						basicTableLst.add(bi.getJq());
						basicTableLst.add(bi.getAuto());
						basicTableLst.add(bi.getSqll());
						basicTableLst.add(bi.getLong_tran_time());
						basicTableLst.add(bi.getLong_query_time());
						basicTableLst.add(bi.getSes());
						basicTableLst.add(bi.getKeep_conn());
						basicTableLst.add(bi.getAccess_mode());
					}
				}
			}
			if (brokerDiagData == null) {
				for (int i = 0; i < 3; i++) {
					basicTableLst.add("");
				}
			} else {
				basicTableLst.add(brokerDiagData.getCas_mon_active());
				basicTableLst.add(brokerDiagData.getCas_mon_session());
				basicTableLst.add(brokerDiagData.getCas_mon_tran());
			}
			//test
			List<List<String>> basicColumnLst = new ArrayList<List<String>>();
			basicColumnLst.add(basicTableLst);
			basicTableViewer.setInput(basicColumnLst);
			basicTableViewer.refresh();
			List<ApplyServerInfo> asinfoLst = null;
			List<JobInfo> jobinfoLst = null;

			if (brokerStatusInfos == null) {
				asinfoLst = new ArrayList<ApplyServerInfo>();
				jobinfoLst = new ArrayList<JobInfo>();
			} else {
				asinfoLst = brokerStatusInfos.getAsinfo();
				jobinfoLst = brokerStatusInfos.getJobinfo();

				if (asinfoLst == null) {
					asinfoLst = new ArrayList<ApplyServerInfo>();

				}
				if (jobinfoLst == null) {
					jobinfoLst = new ArrayList<JobInfo>();
				}
			}
			asTableViewer.setInput(asinfoLst);
			asTableViewer.refresh();
			jqTableViewer.setInput(jobinfoLst);
			jqTableViewer.refresh();

		}
	}

}
