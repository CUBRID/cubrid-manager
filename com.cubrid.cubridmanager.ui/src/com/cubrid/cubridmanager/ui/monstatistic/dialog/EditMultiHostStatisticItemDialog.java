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
package com.cubrid.cubridmanager.ui.monstatistic.dialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.monstatistic.model.MultiHostChartItem;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticChartHost;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticChartItem;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil.MetricType;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil.StatisticType;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil.TimeType;
import com.cubrid.cubridmanager.ui.host.dialog.ConnectHostExecutor;
import com.cubrid.cubridmanager.ui.monstatistic.Messages;
import com.cubrid.cubridmanager.ui.monstatistic.editor.MonitorStatisticEditor;
import com.cubrid.cubridmanager.ui.monstatistic.progress.LoadMonitorStatisticDataProgress;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;

/**
 * This type provides a dialog for user to configuration a monitor statistic
 * item in monitor statistic page
 * 
 * @author Santiago Wang
 * @version 1.0 - 2013-08-31 created by Santiago Wang
 */
public class EditMultiHostStatisticItemDialog extends
		CMTitleAreaDialog {

	private final MonitorStatisticEditor editor;
	private final CMHostNodePersistManager hostNodePersistManager = CMHostNodePersistManager.getInstance();
	private final String VALUE_DEFAULT = "";
	private StatisticType type;
	private TimeType timeType;
	private List<StatisticChartHost> hostList;

	private TableViewer hostTableViewer;
	private Button btnAddHost;
	private Button btnEditHost;
	private Button btnDeleteHost;
	private StatisticChartItem statisticChartItem;
	private boolean isNew = true;

	/**
	 * Constructor
	 * 
	 * @param parentShell
	 */
	public EditMultiHostStatisticItemDialog(Shell parentShell,
			MonitorStatisticEditor editor) {
		super(parentShell);
		this.editor = editor;
	}

	/**
	 * Creates and returns the contents of the upper part of this dialog (above
	 * the button bar).
	 * 
	 * @param parent The parent composite to contain the dialog area
	 * @return the dialog area control
	 */
	protected Control createDialogArea(Composite parent) {
		final Composite parentComp = (Composite) super.createDialogArea(parent);
		final int DEFAULT_WIDTH = 80;

		Composite compHost = new Composite(parentComp, SWT.RESIZE);
		compHost.setLayout(new FillLayout());
		compHost.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		hostTableViewer = new TableViewer(compHost, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
		hostTableViewer.getTable().setHeaderVisible(true);
		hostTableViewer.getTable().setLinesVisible(true);
		hostTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				TableItem[] tableItems = hostTableViewer.getTable().getSelection();
				StatisticChartHost hostItem = (StatisticChartHost) tableItems[0].getData();
				openAddStatisticHostDialog(hostItem, hostList.size() == 1,
						false);
			}
		});
		
		//Host Name
		final TableViewerColumn nameColumn = new TableViewerColumn(
				hostTableViewer, SWT.LEFT);
		nameColumn.getColumn().setWidth(DEFAULT_WIDTH);
		nameColumn.getColumn().setText(Messages.lblHostName);

		//IP
		final TableViewerColumn ipColumn = new TableViewerColumn(
				hostTableViewer, SWT.LEFT);
		ipColumn.getColumn().setWidth(DEFAULT_WIDTH);
		ipColumn.getColumn().setText(Messages.lblIp);

		//Port
		final TableViewerColumn portColumn = new TableViewerColumn(
				hostTableViewer, SWT.LEFT);
		portColumn.getColumn().setWidth(50);
		portColumn.getColumn().setText(Messages.lblPort);

		//Data Type
		final TableViewerColumn dataTypeColumn = new TableViewerColumn(
				hostTableViewer, SWT.LEFT);
		dataTypeColumn.getColumn().setWidth(DEFAULT_WIDTH);
		dataTypeColumn.getColumn().setText(Messages.lblDataType);

		//Time Type
		final TableViewerColumn timeTypeColumn = new TableViewerColumn(
				hostTableViewer, SWT.LEFT);
		timeTypeColumn.getColumn().setWidth(DEFAULT_WIDTH);
		timeTypeColumn.getColumn().setText(Messages.lblTimeType);

		//Database Name
		final TableViewerColumn dbNameColumn = new TableViewerColumn(
				hostTableViewer, SWT.LEFT);
		dbNameColumn.getColumn().setWidth(DEFAULT_WIDTH);
		dbNameColumn.getColumn().setText(Messages.lblDbName);

		//Database Volume Name
		final TableViewerColumn volNameColumn = new TableViewerColumn(
				hostTableViewer, SWT.LEFT);
		volNameColumn.getColumn().setWidth(DEFAULT_WIDTH);
		volNameColumn.getColumn().setText(Messages.lblVolName);

		//Broker Name
		final TableViewerColumn brokerColumn = new TableViewerColumn(
				hostTableViewer, SWT.LEFT);
		brokerColumn.getColumn().setWidth(DEFAULT_WIDTH);
		brokerColumn.getColumn().setText(Messages.lblBrokerName);

		//Metric
		final TableViewerColumn metricColumn = new TableViewerColumn(
				hostTableViewer, SWT.LEFT);
		metricColumn.getColumn().setWidth(DEFAULT_WIDTH);
		metricColumn.getColumn().setText(Messages.lblMetric);

		hostTableViewer.setContentProvider(new HostTableViewerContentProvider());
		hostTableViewer.setLabelProvider(new HostTableViewerLabelProvider());

		hostTableViewer.getTable().addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent event) {
				ActionManager.getInstance().changeFocusProvider(hostTableViewer.getTable());
			}
		});
		
		hostTableViewer.getTable().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = hostTableViewer.getTable().getSelection();
				btnEditHost.setEnabled(items.length > 0);
				btnDeleteHost.setEnabled(items.length > 0);
			}
		});

		Composite compBtn = new Composite(parentComp, SWT.RESIZE);
		GridLayout layoutCompBtn = new GridLayout(6, false);
		layoutCompBtn.marginRight = 0;
		//		layoutCompBtn.numColumns = 6;
		layoutCompBtn.marginWidth = 0;
		compBtn.setLayout(layoutCompBtn);
		compBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

		btnAddHost = new Button(compBtn, SWT.NONE);
		btnAddHost.setText(Messages.btnAddHost);
		btnAddHost.addSelectionListener(new ButtonAddHostAdapter());

		btnEditHost = new Button(compBtn, SWT.NONE);
		btnEditHost.setText(Messages.btnEditHost);
		btnEditHost.addSelectionListener(new ButtonEditHostAdapter());
		btnEditHost.setEnabled(false);

		btnDeleteHost = new Button(compBtn, SWT.NONE);
		btnDeleteHost.setText(Messages.btnDelHost);
		btnDeleteHost.addSelectionListener(new ButtonDeleteHostAdapter());
		btnDeleteHost.setEnabled(false);

		if (!isNew && statisticChartItem != null) {
			MultiHostChartItem multiHostChartItem = (MultiHostChartItem)statisticChartItem;
			type = multiHostChartItem.getType();
			timeType = TimeType.getEnumByType(multiHostChartItem.getDType());
			hostList = multiHostChartItem.getHostList();
			hostTableViewer.setInput(hostList);
		} else {
			hostList = new ArrayList<StatisticChartHost>();
		}

		return parentComp;
	}

	/**
	 * Constrain the shell size
	 */
	protected void constrainShellSize() {
		super.constrainShellSize();
		if (isNew) {
			getShell().setText(Messages.addMultiHostStatisticItemTitle);
			this.setTitle(Messages.addMultiHostStatisticItemTitle);
			this.setMessage(Messages.addStatisticItemMsg);
		} else {
			getShell().setText(Messages.editMultiHostStatisticItemTitle);
			this.setTitle(Messages.editMultiHostStatisticItemTitle);
			this.setMessage(Messages.editStatisticItemMsg);
		}
		CommonUITool.centerShell(getShell());
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 * @param parent the button bar composite
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Messages.btnOK, true);
		createButton(parent, IDialogConstants.CANCEL_ID, Messages.btnCancel,
				false);
		getButton(IDialogConstants.OK_ID).setEnabled(hostList.size() > 0);
	}

	/**
	 * Enable the "OK" button
	 */
	private void enableOk() {
		String errMsg = null;
		boolean isOkEnable = hostList.size() > 0;
		if (!isOkEnable) {
			errMsg = Messages.errNoHostMsg;
		}
		getButton(IDialogConstants.OK_ID).setEnabled(isOkEnable);
		setErrorMessage(errMsg);
	}

	public void okPressed() {
		int series = 0;
		if (!isNew) {
			series = statisticChartItem.getSeries();
		}
		statisticChartItem = new MultiHostChartItem(editor.getNodeId(), type,
				timeType.getType());
		Iterator<StatisticChartHost> it = hostList.iterator();
		while (it.hasNext()) {
			((MultiHostChartItem) statisticChartItem).addStatisticChartHost(it.next());
		}
		if (!isNew) {
			statisticChartItem.setSeries(series);
		}

		LoadMonitorStatisticDataProgress.tearDownDisconnectedServer();
		super.okPressed();
	}

	@Override
	public void cancelPressed() {
		LoadMonitorStatisticDataProgress.tearDownDisconnectedServer();
		super.cancelPressed();
	}

	public void addStatisticChartHost(StatisticChartHost host) {
		if (host == null) {
			return;
		}
		hostList.add(host);
		refreshHostTable();
	}

	public StatisticType getType() {
		return type;
	}

	public void setType(StatisticType type) {
		this.type = type;
	}

	public TimeType getTimeType() {
		return timeType;
	}

	public void setTimeType(TimeType timeType) {
		this.timeType = timeType;
	}

	public StatisticChartItem getStatisticChartItem() {
		return statisticChartItem;
	}

	public void setStatisticChartItem(StatisticChartItem statisticChartItem) {
		this.statisticChartItem = statisticChartItem;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public boolean isDuplicatedHostInfo(StatisticChartHost newHost,
			StatisticChartHost oldHost) {
		if (newHost == null) {
			return false;
		}
		if (oldHost == null) {
			if (hostList.contains(newHost)) {
				return true;
			}
		} else {
			if (hostList.contains(newHost) && !oldHost.equals(newHost)) {
				return true;
			}
		}

		return false;
	}

	private void openAddStatisticHostDialog(StatisticChartHost hostItem,
			boolean isFirstHost, boolean isNewHost) {
		EditStatisticHostDialog dialog = new EditStatisticHostDialog(
				this.getShell(), this, isNewHost);
		if (!isNewHost) {//initial data when edit host info
			dialog.init(type, timeType, hostItem);
		}
		dialog.setFirstHost(isFirstHost);
		if (!isFirstHost) {
			StatisticChartHost firstItem = hostList.get(0);
			dialog.setFirstMetric(firstItem.getMetric());
			dialog.setFirstTime(timeType);
		}
		ServerInfo serverInfo = null;
		if (hostItem != null) {//edit
			serverInfo = LoadMonitorStatisticDataProgress.buildServerInfo(hostItem);
			boolean isUnavailable = false;
			boolean isSupported = false;
			if (serverInfo == null) {
				isUnavailable = true;
			} else if (serverInfo.isConnected()) {
				isSupported = serverInfo.isSupportMonitorStatistic();
			} else {
				LoadMonitorStatisticDataProgress.addDisconnectedServer(serverInfo);
				TaskExecutor taskExcutor = new ConnectHostExecutor(getShell(),
						serverInfo, true);
				((ConnectHostExecutor) taskExcutor).setCheckJdbc(false);
				new ExecTaskWithProgress(taskExcutor).exec(true, true);

				if (taskExcutor.isSuccess()) {
					isSupported = serverInfo.isSupportMonitorStatistic();
				} else {
					isUnavailable = true;
				}
			}
			dialog.setServerInfo(serverInfo);
			if (isUnavailable) {
				dialog.setHostStatusValue(dialog.HOST_STATUS_UNAVAILABLE);
			} else if (isSupported) {
				dialog.setHostStatusValue(dialog.HOST_STATUS_OK);
			} else {
				dialog.setHostStatusValue(dialog.HOST_STATUS_UNSUPPORTED);
			}
		}

		if (dialog.open() == Dialog.OK) {
			if (hostItem != null) {
				int oldIndex = hostList.indexOf(hostItem);
				hostList.set(oldIndex, dialog.getHostItem());
			} else {
				hostList.add(dialog.getHostItem());
			}
			refreshHostTable();
		}
		enableOk();
	}

	private void refreshHostTable() {
		hostTableViewer.setInput(hostList);
		hostTableViewer.refresh();
	}

	/**
	 * Host table label provider
	 * 
	 * @author Santiago Wang
	 */
	public class HostTableViewerLabelProvider extends
			LabelProvider implements
			ITableLabelProvider {
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof StatisticChartHost) {
				StatisticChartHost hostItem = (StatisticChartHost) element;
				if (hostItem != null) {
					ServerInfo serverInfo = hostItem.getServerInfo();
					if (serverInfo == null) {
						for (CubridServer cubridServer : hostNodePersistManager.getAllServers()) {
							if (cubridServer.getId().equals(
									hostItem.getCubridServerId())) {
								serverInfo = cubridServer.getServerInfo();
								break;
							}
						}
					}
					switch (columnIndex) {
					case 0://Host Name
						return hostItem.getCubridServerId();
					case 1://IP
						return serverInfo.getHostAddress();
					case 2://Port
						return Integer.toString(serverInfo.getHostMonPort());
					case 3://Data Type
						return type.getMessage();
					case 4://Time Type
						return timeType.getMessage();
					case 5://Database Name
						return hostItem.getDbName();
					case 6://Database Volume Name
						return hostItem.getVolName();
					case 7://Broker Name
						return hostItem.getBrokerName();
					case 8://Metric
						MetricType metricType = MetricType.getEnumByMetric(hostItem.getMetric());
						return metricType != null ? metricType.getMessage()
								: VALUE_DEFAULT;
					}
				}
			}

			return null;
		}
	}

	/**
	 * Host table content provider
	 * 
	 * @author Santiago Wang
	 */
	public class HostTableViewerContentProvider implements
			IStructuredContentProvider {
		@SuppressWarnings("unchecked")
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof List) {
				List<StatisticChartHost> list = (List<StatisticChartHost>) inputElement;
				StatisticChartHost[] nodeArr = new StatisticChartHost[list.size()];
				return list.toArray(nodeArr);
			}

			return new Object[]{};
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	class ButtonAddHostAdapter extends
			SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			openAddStatisticHostDialog(null, hostList.size() == 0, true);
		}
	}

	class ButtonEditHostAdapter extends
			SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			TableItem[] tableItems = hostTableViewer.getTable().getSelection();
			if (tableItems.length != 1) {
				return;
			}
			StatisticChartHost hostItem = (StatisticChartHost) tableItems[0].getData();
			openAddStatisticHostDialog(hostItem, hostList.size() == 1, false);
		}
	}

	class ButtonDeleteHostAdapter extends
			SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			TableItem[] items = hostTableViewer.getTable().getSelection();
			if (items.length > 0) {
				if (CommonUITool.openConfirmBox(Messages.confirmRemoveSelectedHost)) {
					List<StatisticChartHost> removedHostList = new ArrayList<StatisticChartHost>();
					for (int i = 0; i < items.length; i++) {
						StatisticChartHost hostItem = (StatisticChartHost) items[i].getData();
						removedHostList.add(hostItem);
					}
					hostList.removeAll(removedHostList);
					enableOk();
					refreshHostTable();
				}
			}
		}
	}

}
