/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.cubridmanager.ui.service.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridGroupNode;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.part.CubridEditorPart;
import com.cubrid.common.ui.spi.progress.CommonTaskJobExec;
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
import com.cubrid.cubridmanager.core.common.task.GetEnvInfoTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.database.task.GetDatabaseListTask;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfo;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoList;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.VolumeType;
import com.cubrid.cubridmanager.core.monitoring.model.HostStatData;
import com.cubrid.cubridmanager.core.monitoring.model.HostStatDataProxy;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.common.action.StartServiceAction;
import com.cubrid.cubridmanager.ui.common.action.StopServiceAction;
import com.cubrid.cubridmanager.ui.host.action.AddHostAction;
import com.cubrid.cubridmanager.ui.host.action.ConnectHostAction;
import com.cubrid.cubridmanager.ui.host.action.CubridServerExportAction;
import com.cubrid.cubridmanager.ui.host.action.CubridServerImportAction;
import com.cubrid.cubridmanager.ui.host.action.DeleteHostAction;
import com.cubrid.cubridmanager.ui.host.action.DisConnectHostAction;
import com.cubrid.cubridmanager.ui.host.action.EditHostAction;
import com.cubrid.cubridmanager.ui.service.Messages;
import com.cubrid.cubridmanager.ui.spi.persist.CMGroupNodePersistManager;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;

/**
 * ServiceDashboardEditor Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-3-5 created by Kevin.Wang
 */
public class ServiceDashboardEditor extends CubridEditorPart {
	public static final String ID = "com.cubrid.cubridmanager.ui.service.editor.ServiceDashboardEditor";

	private TreeViewer serviceTreeViewer;
	private Object inputList;
//	private Label infoLabel;
	private ToolItem refreshItem;
	private ToolItem importItem;
	private ToolItem exportItem;
	private MenuItem refreshMenuItem;
	private MenuItem connectMenuItem;
	private MenuItem disConnectMenuItem;
	private MenuItem addHostMenuItem;
	private MenuItem editHostMenuItem;
	private MenuItem deleteHostMenuItem;
	private MenuItem startServiceMenuItem;
	private MenuItem stopServiceMenuItem;

	private Map<String, ServiceDashboardInfo> ServiceDashboardInfoMap = new HashMap<String, ServiceDashboardInfo>();
	private ServiceDashboardInfo sDashInfo;

	public void createPartControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.None);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		composite.setLayout(layout);

		Composite buttonComp = new Composite(composite, SWT.None);
		buttonComp.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, -1, 24));

		Composite dataComp = new Composite(composite, SWT.None);
		dataComp.setLayout(new FillLayout());
		dataComp.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH,
				1, 1, -1, -1));

		/*Create button*/
		createButtonComposite(buttonComp);

		serviceTreeViewer = new TreeViewer(dataComp, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.MULTI);
		serviceTreeViewer.getTree().setHeaderVisible(true);
		serviceTreeViewer.getTree().setLinesVisible(true);

		ColumnViewerToolTipSupport.enableFor(serviceTreeViewer, ToolTip.NO_RECREATE);

		final TreeColumn hostColumn = new TreeColumn(serviceTreeViewer.getTree(), SWT.LEFT);
		hostColumn.setText(Messages.columnGroupOrHost);
		hostColumn.setWidth(200);
		serviceTreeViewer.setSorter(ServiceDashboardEditorSorter.Name_ASC);
		setColumnSorter(hostColumn, ServiceDashboardEditorSorter.Name_ASC,
				ServiceDashboardEditorSorter.Name_DESC);

		/*TreeColumn statusColumn = new TreeColumn(serviceTreeViewer.getTree(), SWT.LEFT);
		statusColumn.setText(Messages.columnStatus);
		statusColumn.setWidth(100);*/

		final TreeColumn ipColumn = new TreeColumn(serviceTreeViewer.getTree(), SWT.LEFT);
		ipColumn.setText(Messages.columnAddress);
		ipColumn.setWidth(100);
		setColumnSorter(ipColumn, ServiceDashboardEditorSorter.Address_ASC,
				ServiceDashboardEditorSorter.Address_DESC);

		TreeColumn portColumn = new TreeColumn(serviceTreeViewer.getTree(), SWT.LEFT);
		portColumn.setText(Messages.columnPort);
		portColumn.pack();
		setColumnSorter(portColumn, ServiceDashboardEditorSorter.Port_ASC,
				ServiceDashboardEditorSorter.Port_DESC);

		TreeColumn userColumn = new TreeColumn(serviceTreeViewer.getTree(), SWT.LEFT);
		userColumn.setText(Messages.columnUser);
		userColumn.pack();
		setColumnSorter(userColumn, ServiceDashboardEditorSorter.User_ASC,
				ServiceDashboardEditorSorter.User_DESC);

		TreeViewerColumn volumnColumnData = new TreeViewerColumn(serviceTreeViewer, SWT.LEFT);
		volumnColumnData.getColumn().setText(Messages.columnData);
		volumnColumnData.getColumn().setToolTipText(Messages.columnDataTip);
		volumnColumnData.getColumn().pack();
		setColumnSorter(volumnColumnData.getColumn(), ServiceDashboardEditorSorter.Data_ASC,
				ServiceDashboardEditorSorter.Data_DESC);

		TreeViewerColumn volumnColumnIndex = new TreeViewerColumn(serviceTreeViewer, SWT.LEFT);
		volumnColumnIndex.getColumn().setText(Messages.columnIndex);
		volumnColumnIndex.getColumn().setToolTipText(Messages.columnIndexTip);
		volumnColumnIndex.getColumn().pack();
		setColumnSorter(volumnColumnIndex.getColumn(), ServiceDashboardEditorSorter.Index_ASC,
				ServiceDashboardEditorSorter.Index_DESC);

		TreeViewerColumn volumnColumnTemp = new TreeViewerColumn(serviceTreeViewer, SWT.LEFT);
		volumnColumnTemp.getColumn().setText(Messages.columnTemp);
		volumnColumnTemp.getColumn().setToolTipText(Messages.columnTempTip);
		volumnColumnTemp.getColumn().pack();
		setColumnSorter(volumnColumnTemp.getColumn(), ServiceDashboardEditorSorter.Temp_ASC,
				ServiceDashboardEditorSorter.Temp_DESC);

		TreeViewerColumn volumnColumnGeneric = new TreeViewerColumn(serviceTreeViewer, SWT.LEFT);
		volumnColumnGeneric.getColumn().setText(Messages.columnGeneric);
		volumnColumnGeneric.getColumn().setToolTipText(Messages.columnGenericTip);
		volumnColumnGeneric.getColumn().pack();
		setColumnSorter(volumnColumnGeneric.getColumn(), ServiceDashboardEditorSorter.Generic_ASC,
				ServiceDashboardEditorSorter.Generic_DESC);

		TreeColumn tpsColumn = new TreeColumn(serviceTreeViewer.getTree(), SWT.LEFT);
		tpsColumn.setText(Messages.columnTps);
		tpsColumn.setWidth(50);
		setColumnSorter(tpsColumn, ServiceDashboardEditorSorter.Tps_ASC,
				ServiceDashboardEditorSorter.Tps_DESC);

		TreeColumn qpsColumn = new TreeColumn(serviceTreeViewer.getTree(), SWT.LEFT);
		qpsColumn.setText(Messages.columnQps);
		qpsColumn.setWidth(50);
		setColumnSorter(qpsColumn, ServiceDashboardEditorSorter.Qps_ASC,
				ServiceDashboardEditorSorter.Qps_DESC);

		TreeColumn errorColumn = new TreeColumn(serviceTreeViewer.getTree(), SWT.LEFT);
		errorColumn.setText(Messages.columnErrorQ);
		errorColumn.setWidth(50);
		setColumnSorter(errorColumn, ServiceDashboardEditorSorter.ErrorQ_ASC,
				ServiceDashboardEditorSorter.ErrorQ_DESC);

		TreeColumn memColumn = new TreeColumn(serviceTreeViewer.getTree(), SWT.LEFT);
		memColumn.setText(Messages.columnMemory);
		memColumn.setWidth(100);
		setColumnSorter(memColumn, ServiceDashboardEditorSorter.Memory_ASC,
				ServiceDashboardEditorSorter.Memory_DESC);

		TreeColumn diskColumn = new TreeColumn(serviceTreeViewer.getTree(), SWT.LEFT);
		diskColumn.setText(Messages.columnDisk);
		diskColumn.setWidth(60);
		setColumnSorter(diskColumn, ServiceDashboardEditorSorter.Disk_ASC,
				ServiceDashboardEditorSorter.Disk_DESC);

		TreeColumn cpuColumn = new TreeColumn(serviceTreeViewer.getTree(), SWT.LEFT);
		cpuColumn.setText(Messages.columnCpu);
		cpuColumn.pack();
		setColumnSorter(cpuColumn, ServiceDashboardEditorSorter.Cpu_ASC,
				ServiceDashboardEditorSorter.Cpu_DESC);

		TreeColumn dbColumn = new TreeColumn(serviceTreeViewer.getTree(), SWT.LEFT);
		dbColumn.setText(Messages.columnDbOnOff);
		dbColumn.setWidth(80);
		setColumnSorter(dbColumn, ServiceDashboardEditorSorter.DbStatus_ASC,
				ServiceDashboardEditorSorter.DbStatus_DESC);

		TreeColumn verColumn = new TreeColumn(serviceTreeViewer.getTree(), SWT.LEFT);
		verColumn.setText(Messages.columnVersion);
		verColumn.setWidth(70);
		setColumnSorter(verColumn, ServiceDashboardEditorSorter.Version_ASC,
				ServiceDashboardEditorSorter.Version_DESC);

		TreeColumn brokerPortColumn = new TreeColumn(serviceTreeViewer.getTree(), SWT.LEFT);
		brokerPortColumn.setText(Messages.columnBrokerPort);
		brokerPortColumn.setWidth(120);
		setColumnSorter(brokerPortColumn, ServiceDashboardEditorSorter.BrokerPort_ASC,
				ServiceDashboardEditorSorter.BrokerPort_DESC);

		serviceTreeViewer.setContentProvider(new ServiceDashboardContentProvider());
		serviceTreeViewer.setLabelProvider(new ServiceDashboardLabelProvider());

		addCellTip(volumnColumnData, 1);
		addCellTip(volumnColumnIndex, 2);
		addCellTip(volumnColumnTemp, 3);
		addCellTip(volumnColumnGeneric, 4);

		serviceTreeViewer.getTree().addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e) {
			}

			public void mouseDown(MouseEvent e) {
			}

			public void mouseDoubleClick(MouseEvent e) {
				CubridServer[] servers = getSelectedServer();
				if (servers.length == 1) {
					if (!servers[0].isConnected()) {
						ActionManager manager = ActionManager.getInstance();
						final ConnectHostAction connectHostAction = (ConnectHostAction) manager.getAction(ConnectHostAction.ID);
						connectHostAction.doRun(getSelectedServer());
						loadAllData();
					}
				}
			}
		});

		initMenu(serviceTreeViewer.getTree());
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

		if (input != null && input.getToolTipText() != null) {
			setTitleToolTip(input.getToolTipText());
		}

		String title = this.getPartName();
		if (title != null) {
			setPartName(title);
		}

		CubridNodeManager.getInstance().addCubridNodeChangeListener(this);
	}

	private void createButtonComposite(Composite parent) {
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 2;
		layout.marginWidth = 5;
		parent.setLayout(layout);

		ToolBar toolbar = new ToolBar(parent, SWT.RIGHT | SWT.WRAP | SWT.FLAT);
		toolbar.setLayoutData(CommonUITool.createGridData(
				GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));

		refreshItem = new ToolItem(toolbar, SWT.PUSH);
		refreshItem.setText(Messages.btnRefresh);
		refreshItem.setImage(CubridManagerUIPlugin.getImage("icons/action/refresh.png"));
		refreshItem.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				if (!CommonUITool.openConfirmBox(Messages.msgRefreshConfirm)) {
					return;
				}
				loadAllData();
			}
		});

		ActionManager manager = ActionManager.getInstance();
		final CubridServerImportAction importAction = (CubridServerImportAction) manager.getAction(CubridServerImportAction.ID);
		importItem = new ToolItem(toolbar, SWT.PUSH);
		importItem.setText(Messages.btnImport);
		importItem.setImage(importAction.getImageDescriptor().createImage());
		importItem.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				importAction.run();
			}
		});

		final CubridServerExportAction exportAction = (CubridServerExportAction) manager.getAction(CubridServerExportAction.ID);
		exportItem = new ToolItem(toolbar, SWT.PUSH);
		exportItem.setText(Messages.btnExport);
		exportItem.setImage(exportAction.getImageDescriptor().createImage());
		exportItem.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				exportAction.run();
			}
		});

//		infoLabel = new Label(parent, SWT.None);
//		infoLabel.setLayoutData(CommonUITool.createGridData(
//				GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
	}

	private void initMenu(Tree tree) {
		ActionManager manager = ActionManager.getInstance();

		Menu menu = new Menu(this.getSite().getShell(), SWT.POP_UP);
		tree.setMenu(menu);

		refreshMenuItem = new MenuItem(menu, SWT.PUSH);
		refreshMenuItem.setText(Messages.btnRefresh);
		refreshMenuItem.setImage(CubridManagerUIPlugin.getImage("icons/action/refresh.png"));
		refreshMenuItem.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				loadAllData();
			}
		});
		new MenuItem(menu, SWT.SEPARATOR);

		final ConnectHostAction connectHostAction = (ConnectHostAction) manager.getAction(ConnectHostAction.ID);
		connectMenuItem = new MenuItem(menu, SWT.PUSH);
		connectMenuItem.setText(connectHostAction.getText());
		connectMenuItem.setImage(connectHostAction.getImageDescriptor().createImage());
		connectMenuItem.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				connectHostAction.doRun(getSelectedServer());
				loadAllData();
			}
		});

		final DisConnectHostAction disConnectHostAction = (DisConnectHostAction) manager.getAction(DisConnectHostAction.ID);
		disConnectMenuItem = new MenuItem(menu, SWT.PUSH);
		disConnectMenuItem.setText(disConnectHostAction.getText());
		disConnectMenuItem.setImage(disConnectHostAction.getImageDescriptor().createImage());
		disConnectMenuItem.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				disConnectHostAction.doRun(getSelectedServer());
				loadAllData();
			}
		});
		new MenuItem(menu, SWT.SEPARATOR);

		final AddHostAction addHostAction = (AddHostAction) manager.getAction(AddHostAction.ID);
		addHostMenuItem = new MenuItem(menu, SWT.PUSH);
		addHostMenuItem.setText(addHostAction.getText());
		addHostMenuItem.setImage(addHostAction.getImageDescriptor().createImage());
		addHostMenuItem.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				addHostAction.doRun(getSelectedServer());
				loadAllData();
			}
		});

		final EditHostAction editHostAction = (EditHostAction) manager.getAction(EditHostAction.ID);
		editHostMenuItem = new MenuItem(menu, SWT.PUSH);
		editHostMenuItem.setText(editHostAction.getText());
		editHostMenuItem.setImage(editHostAction.getImageDescriptor().createImage());
		editHostMenuItem.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				editHostAction.doRun(getSelectedServer());
				loadAllData();
			}
		});

		final DeleteHostAction deleteHostAction = (DeleteHostAction) manager.getAction(DeleteHostAction.ID);
		deleteHostMenuItem = new MenuItem(menu, SWT.PUSH);
		deleteHostMenuItem.setText(deleteHostAction.getText());
		deleteHostMenuItem.setImage(deleteHostAction.getImageDescriptor().createImage());
		deleteHostMenuItem.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				deleteHostAction.doRun(getSelectedServer());
				loadAllData();
			}
		});

		new MenuItem(menu, SWT.SEPARATOR);
		final StartServiceAction startServiceAction = (StartServiceAction) manager.getAction(StartServiceAction.ID);
		startServiceMenuItem = new MenuItem(menu, SWT.PUSH);
		startServiceMenuItem.setText(startServiceAction.getText());
		startServiceMenuItem.setImage(startServiceAction.getImageDescriptor().createImage());
		startServiceMenuItem.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				startServiceAction.doRun(getSelectedServer());
				loadAllData();
			}
		});

		final StopServiceAction stopServiceAction = (StopServiceAction) manager.getAction(StopServiceAction.ID);
		stopServiceMenuItem = new MenuItem(menu, SWT.PUSH);
		stopServiceMenuItem.setText(stopServiceAction.getText());
		stopServiceMenuItem.setImage(stopServiceAction.getImageDescriptor().createImage());
		stopServiceMenuItem.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				stopServiceAction.doRun(getSelectedServer());
				loadAllData();
			}
		});
		menu.addMenuListener(new MenuListener() {
			public void menuShown(MenuEvent e) {
				updateMenuItems();
			}

			public void menuHidden(MenuEvent e) {
			}
		});
	}

	private void updateMenuItems() {
		connectMenuItem.setEnabled(false);
		disConnectMenuItem.setEnabled(false);
		startServiceMenuItem.setEnabled(false);
		stopServiceMenuItem.setEnabled(false);

		CubridServer[] servers = getSelectedServer();
		int connectedCount = 0;
		int adminCount = 0;

		for (CubridServer server : servers) {
			if (server.isConnected()) {
				connectedCount++;
			}

			if (isLoginedByAdmin(server)) {
				adminCount++;
			}
		}

		if (connectedCount == 0) {
			connectMenuItem.setEnabled(true);
		} else if (connectedCount > 0) {
			disConnectMenuItem.setEnabled(true);
		}

		if (adminCount == servers.length && servers.length == 1) {
			startServiceMenuItem.setEnabled(true);
			stopServiceMenuItem.setEnabled(true);
		}
	}

	/**
	 * Judge is login user is admin
	 *
	 * @param server
	 * @return
	 */
	private boolean isLoginedByAdmin(CubridServer server) {
		if (server != null && server.isConnected()
				&& server.getServerInfo() != null
				&& server.getServerInfo().getLoginedUserInfo() != null
				&& server.getServerInfo().getLoginedUserInfo().isAdmin()) {
			return true;
		}
		return false;
	}

	private CubridServer[] getSelectedServer() {
		Set<CubridServer> list = new LinkedHashSet<CubridServer>();
		TreeItem[] items = serviceTreeViewer.getTree().getSelection();

		for (TreeItem item : items) {
			Object obj = item.getData();
			if (obj != null) {
				if (obj instanceof ServiceDashboardInfo) {
					obj = ((ServiceDashboardInfo) obj).getServer();
				}
				if (obj instanceof CubridServer && !list.contains(obj)) {
					list.add((CubridServer) obj);
				} else if (obj instanceof CubridGroupNode) {
					CubridGroupNode node = (CubridGroupNode) obj;
					for (ICubridNode childNode : node.getChildren()) {
						if (childNode != null
								&& childNode instanceof CubridServer
								& !list.contains(childNode)) {
							list.add((CubridServer) childNode);
						}
					}
				}
			}
		}

		return list.toArray(new CubridServer[0]);
	}

	/**
	 * Load all data
	 */
	public void loadAllData() {
		ServiceDashboardInfoMap.clear();
		CubridNavigatorView navigatorView = CubridNavigatorView.findNavigationView();
		if (navigatorView != null && navigatorView.savedIsShowGroup()) {
			inputList = CMGroupNodePersistManager.getInstance().getAllGroupNodes();
			List<CubridGroupNode> groupList = loadCubridGroupData(inputList);
			serviceTreeViewer.setInput(groupList);
		} else {
			inputList = CMHostNodePersistManager.getInstance().getAllServer();
			loadServerDashboardInfo(inputList);
			serviceTreeViewer.setInput(ServiceDashboardInfoMap);
		}
		serviceTreeViewer.expandAll();
	}

	/**
	 * Load cubrid group data
	 */
	private List<CubridGroupNode> loadCubridGroupData(Object input){
		@SuppressWarnings("unchecked")
		List<CubridGroupNode> groupList = (List<CubridGroupNode>) input;
		List<CubridGroupNode> newGroupList = new ArrayList<CubridGroupNode>();
		List<CubridServer> serverList = new ArrayList<CubridServer>();
		if (groupList != null) {
			for (CubridGroupNode groupNode : groupList) {
				serverList = new ArrayList<CubridServer>();
				CubridGroupNode newGroupNode = new CubridGroupNode(groupNode.getId(),
						groupNode.getLabel(), groupNode.getIconPath());
				List<ICubridNode> nodeList = ((CubridGroupNode) groupNode).getChildren();
				for (ICubridNode node : nodeList) {
					serverList.add((CubridServer)node);
				}
				loadServerDashboardInfo(serverList);
				for (CubridServer server : serverList) {
					newGroupNode.addChild(ServiceDashboardInfoMap.get(server.getName()));
				}
				newGroupList.add(newGroupNode);
			}
		}
		return newGroupList;
	}

	/**
	 * Load Server Dashboard Info
	 */
	private void loadServerDashboardInfo(Object input){
		@SuppressWarnings("unchecked")
		List<CubridServer> serverList = (List<CubridServer>) input;
		if (serverList != null) {
			for (CubridServer server : serverList) {
				ServerInfo serverInfo = server.getServerInfo();
				String serverName = serverInfo.getServerName();
				sDashInfo = new ServiceDashboardInfo(serverName, serverName,"", server);

				if (server.isConnected() == false) {
					ServiceDashboardInfoMap.put(serverName, sDashInfo);
					continue;
				}

				loadServerVolumeData(serverInfo, serverName);
				loadServerBrokerInfo(serverInfo, serverName);
				loadServerHostInfo(serverInfo, serverName);
				loadServerDbInfo(serverInfo, serverName);
				loadServerEnvInfo(serverInfo, serverName);
				ServiceDashboardInfoMap.put(serverName, sDashInfo);
			}
		}
	}

	/**
	 * Load Server Volume Info
	 */
	private void loadServerVolumeData(final ServerInfo serverInfo, final String key){
		List<String> dbList = serverInfo.getAllDatabaseList();
		if (dbList == null || dbList.size() == 0) {
			return;
		}
		final List<CommonQueryTask<DbSpaceInfoList>> getVolumnTaskList = new ArrayList<CommonQueryTask<DbSpaceInfoList>>();
		for (String dbname : dbList) {
			CommonQueryTask<DbSpaceInfoList> task = new CommonQueryTask<DbSpaceInfoList>(
					serverInfo,CommonSendMsg.getCommonDatabaseSendMsg(), new DbSpaceInfoList());
			task.setDbName(dbname);
			getVolumnTaskList.add(task);
		}

		TaskJobExecutor taskJobExec = new CommonTaskJobExec(
			new ITaskExecutorInterceptor() {
				public void completeAll() {
					Object input = serviceTreeViewer.getInput();
					ServiceDashboardInfo sDashInfo = getSelectedDashInfo(input, serverInfo.getServerName(),
							key);
					setVolumeData(sDashInfo, getVolumnTaskList);
					serviceTreeViewer.refresh(input);
				}
				public IStatus postTaskFinished(ITask task) {
					return Status.OK_STATUS;
				}
			});

		for (CommonQueryTask<DbSpaceInfoList> task : getVolumnTaskList) {
			taskJobExec.addTask(task);
		}

		executeTask(taskJobExec, serverInfo.getServerName(), Messages.taskGetServerVolumeInfo);
	}

	/**
	 * Load Server Broker Info
	 */
	private void loadServerBrokerInfo(final ServerInfo serverInfo, final String key){
		final CommonQueryTask<BrokerInfos> task = new CommonQueryTask<BrokerInfos>(serverInfo,
				CommonSendMsg.getCommonSimpleSendMsg(), new BrokerInfos());

		TaskJobExecutor taskJobExec = new CommonTaskJobExec(
			new ITaskExecutorInterceptor() {
				public void completeAll() {
					Object input = serviceTreeViewer.getInput();
					ServiceDashboardInfo sDashInfo = getSelectedDashInfo(input, serverInfo.getServerName(),
							key);
					BrokerInfos brokerInfos = task.getResultModel();
					setBrokerData(sDashInfo, brokerInfos);
					serviceTreeViewer.refresh(input);
				}
				public IStatus postTaskFinished(ITask task) {
					return Status.OK_STATUS;
				}
			});

		taskJobExec.addTask(task);
		executeTask(taskJobExec, serverInfo.getServerName(), Messages.taskGetServerBrokerInfo);
	}

	/**
	 * Load Server Host Info
	 */
	private void loadServerHostInfo(final ServerInfo serverInfo, final String key){
		final CommonQueryTask<HostStatData> task = new CommonQueryTask<HostStatData>(
				serverInfo, CommonSendMsg.getCommonSimpleSendMsg(), new HostStatData());

		TaskJobExecutor taskJobExec = new CommonTaskJobExec(
				new ITaskExecutorInterceptor() {
					public void completeAll() {
						Object input = serviceTreeViewer.getInput();
						ServiceDashboardInfo sDashInfo = getSelectedDashInfo(input, serverInfo.getServerName(),
								key);
						HostStatData hostInfo = task.getResultModel();
						setHostData(sDashInfo, hostInfo);
						serviceTreeViewer.refresh(input);
					}
					public IStatus postTaskFinished(ITask task) {
						return Status.OK_STATUS;
					}
				});

		taskJobExec.addTask(task);
		executeTask(taskJobExec, serverInfo.getServerName(), Messages.taskGetServerHostInfo);
	}

	/**
	 * Load Server Database Info
	 */
	private void loadServerDbInfo(final ServerInfo serverInfo, final String key){
		if(serverInfo.isConnected() == false){
			return;
		}
		final GetDatabaseListTask getDatabaseListTask = new GetDatabaseListTask(serverInfo);

		TaskJobExecutor taskJobExec = new CommonTaskJobExec(
				new ITaskExecutorInterceptor() {
					public void completeAll() {
						Object input = serviceTreeViewer.getInput();
						ServiceDashboardInfo sDashInfo = getSelectedDashInfo(input, serverInfo.getServerName(),
								key);
						List<DatabaseInfo> databaseInfoList = getDatabaseListTask.loadDatabaseInfo();
						setDbData(sDashInfo, databaseInfoList);
						serviceTreeViewer.refresh(input);
					}
					public IStatus postTaskFinished(ITask task) {
						return Status.OK_STATUS;
					}
				});

		taskJobExec.addTask(getDatabaseListTask);
		executeTask(taskJobExec, serverInfo.getServerName(), Messages.taskGetServerDbInfo);
	}

	/**
	 * Load Server Environment Info
	 */
	private void loadServerEnvInfo(final ServerInfo serverInfo, final String key){
		if(serverInfo.isConnected() == false){
			return;
		}
		final GetEnvInfoTask getEnvInfoTask = new GetEnvInfoTask(serverInfo);

		TaskJobExecutor taskJobExec = new CommonTaskJobExec(
				new ITaskExecutorInterceptor() {
					public void completeAll() {
						Object input = serviceTreeViewer.getInput();
						ServiceDashboardInfo sDashInfo = getSelectedDashInfo(input, serverInfo.getServerName(),
								key);
						EnvInfo envInfo = getEnvInfoTask.loadEnvInfo();
						setEnvData(sDashInfo, envInfo);
						serviceTreeViewer.refresh(input);
					}
					public IStatus postTaskFinished(ITask task) {
						return Status.OK_STATUS;
					}
				});

		taskJobExec.addTask(getEnvInfoTask);
		executeTask(taskJobExec, serverInfo.getServerName(), Messages.taskGetServerEnvInfo);
	}

	/**
	 * Set Server Volume Data
	 */
	private void setVolumeData(ServiceDashboardInfo sDashInfo, List<CommonQueryTask<DbSpaceInfoList>> getVolumnTaskList){
		long totalPageData = 0, freePageData = 0, totalPageIndex = 0, freePageIndex = 0,
				totalPageTemp = 0, freePageTemp = 0, totalPageGeneric = 0, freePageGeneric = 0, freespaceOnStorage = 0;

		for (CommonQueryTask<DbSpaceInfoList> task : getVolumnTaskList) {
			DbSpaceInfoList dbSpaceInfoList = task.getResultModel();
			if (dbSpaceInfoList != null && task.getErrorMsg()== null) {
				freespaceOnStorage = ((long) dbSpaceInfoList.getFreespace()) * 1024l * 1024l;
				for (DbSpaceInfo spaceInfo : dbSpaceInfoList.getSpaceinfo()) {
					String type = spaceInfo.getType();
					if (type.equals(VolumeType.DATA.getText())) {
						totalPageData += spaceInfo.getTotalpage();
						freePageData += spaceInfo.getFreepage();
					}else if (type.equals(VolumeType.INDEX.getText())) {
						totalPageIndex += spaceInfo.getTotalpage();
						freePageIndex += spaceInfo.getFreepage();
					}else if (type.equals(VolumeType.TEMP.getText())) {
						totalPageTemp += spaceInfo.getTotalpage();
						freePageTemp += spaceInfo.getFreepage();
					}else if (type.equals(VolumeType.GENERIC.getText())) {
						totalPageGeneric += spaceInfo.getTotalpage();
						freePageGeneric += spaceInfo.getFreepage();
					}
				}
			}
		}
		if (totalPageData > 0)	sDashInfo.setFreeDataPerc(new Double(freePageData * 100.0d / totalPageData).intValue());
		else sDashInfo.setFreeDataPerc(-1);

		if (totalPageIndex > 0) sDashInfo.setFreeIndexPerc(new Double(freePageIndex * 100.0d / totalPageIndex).intValue());
		else sDashInfo.setFreeIndexPerc(-1);

		if (totalPageTemp > 0) sDashInfo.setFreeTempPerc(new Double(freePageTemp * 100.0d / totalPageTemp).intValue());
		else sDashInfo.setFreeTempPerc(-1);

		if (totalPageGeneric > 0) sDashInfo.setFreeGenericPerc(new Double(freePageGeneric * 100.0d / totalPageGeneric).intValue());
		else sDashInfo.setFreeGenericPerc(-1);

		sDashInfo.setFreespaceOnStorage(freespaceOnStorage);
	}

	/**
	 * Set Server Broker Data
	 */
	private void setBrokerData(ServiceDashboardInfo sDashInfo, BrokerInfos brokerInfos){
		String brokerPort = "";
		int tps = 0, qps = 0, errorQ = 0;
		if (brokerInfos != null) {
			List<BrokerInfo> newBrokerInfoList = null;
			BrokerInfoList list = brokerInfos.getBorkerInfoList();
			if (list != null && list.getBrokerInfoList() != null) {
				newBrokerInfoList = list.getBrokerInfoList();
			}
			if (newBrokerInfoList != null) {
				for (BrokerInfo brokerInfo : newBrokerInfoList) {
					brokerPort += brokerInfo.getPort() + " ";
					tps += StringUtil.doubleValue(brokerInfo.getTran(), 0);
					qps += StringUtil.doubleValue(brokerInfo.getQuery(), 0);
					errorQ += StringUtil.doubleValue(brokerInfo.getError_query(), 0);
				}
			}
			sDashInfo.setServerTps(tps);
			sDashInfo.setServerQps(qps);
			sDashInfo.setServerErrorQ(errorQ);
			sDashInfo.setBrokerPort(brokerPort);
		}
	}

	/**
	 * Set Server Host Data
	 */
	private void setHostData(ServiceDashboardInfo sDashInfo, HostStatData hostInfo){
		double memUsed = 0, memTotal = 0, cpuUsed = 0;
		if (hostInfo != null) {
			HostStatDataProxy hostStatDataProxy = new HostStatDataProxy();
			HostStatData hostInfo2 = new HostStatData();
			hostInfo2.copyFrom(hostInfo);
			hostStatDataProxy.compute(hostInfo, hostInfo2);
			memUsed = Double.parseDouble(hostStatDataProxy.getMemPhyUsed());
			memTotal = Double.parseDouble(hostStatDataProxy.getMemPhyTotal());
			cpuUsed = Double.parseDouble(hostStatDataProxy.getUserPercent());
		}
		sDashInfo.setMemUsed(memUsed);
		sDashInfo.setMemTotal(memTotal);
		sDashInfo.setCpuUsed(cpuUsed);
	}

	/**
	 * Set Server Database Info
	 */
	private void setDbData(ServiceDashboardInfo sDashInfo, List<DatabaseInfo> databaseInfoList){
		int databaseOn = 0, databaseOff = 0;
		if (databaseInfoList != null) {
			for(DatabaseInfo dbInfo : databaseInfoList){
				if(dbInfo.getRunningType()  == DbRunningType.CS)
					databaseOn += 1;
			}
			databaseOff = databaseInfoList.size() - databaseOn;
		}
		sDashInfo.setDatabaseOn(databaseOn);
		sDashInfo.setDatabaseOff(databaseOff);
	}

	/**
	 * Set Server Environment Info
	 */
	private void setEnvData(ServiceDashboardInfo sDashInfo, EnvInfo envInfo){
		String serverVersion = "", brokerVersion = "";
		if(envInfo != null){
			brokerVersion = envInfo.getBrokerVersion();
			serverVersion = brokerVersion.substring(brokerVersion.indexOf("VERSION")+8);
		}
		sDashInfo.setServerVersion(serverVersion);
	}

	/**
	 * Get Selected Dashboard Item
	 */
	private ServiceDashboardInfo getSelectedDashInfo(Object input, String serverName, String key){
		ServiceDashboardInfo sDashInfo = null;
		if (input instanceof HashMap) {
			sDashInfo = ServiceDashboardInfoMap.get(key);
			serviceTreeViewer.refresh();
		} else {
			boolean foundNode = false;
			for (Object groupNode : (List<?>) input) {
				List<ICubridNode> nodeList = ((CubridGroupNode) groupNode).getChildren();
				for (ICubridNode node : nodeList) {
					if (serverName.equals(node.getId())) {
						sDashInfo = (ServiceDashboardInfo) node;
						foundNode = true;
						break;
					}
				}
				if (foundNode == true)  break;
			}
		}
		return sDashInfo;
	}

	/**
	 * Get Tip Message on Cell
	 */
	private void addCellTip(final TreeViewerColumn column, final int type){
		column.setLabelProvider(new CellLabelProvider() {
			  @Override
			  public void update(ViewerCell cell) {
				Object element = cell.getElement();
				String columnText = "";
				if (element instanceof ServiceDashboardInfo) {
					switch(type) {
						case 1:
							int freeDataPerc = ((ServiceDashboardInfo)element).getFreeDataPerc();
							columnText = ((ServiceDashboardInfo)element).getServer().isConnected()
									&& freeDataPerc >= 0 ? freeDataPerc + "%" : "-";
							break;
						case 2:
							int freeIndexPerc = ((ServiceDashboardInfo)element).getFreeIndexPerc();
							columnText = ((ServiceDashboardInfo)element).getServer().isConnected()
									&& freeIndexPerc >=0 ? freeIndexPerc + "%" : "-";
							break;
						case 3:
							int freeTempPerc = ((ServiceDashboardInfo)element).getFreeTempPerc();
							columnText = ((ServiceDashboardInfo)element).getServer().isConnected()
									&& freeTempPerc >= 0? freeTempPerc + "%" : "-";
							break;
						case 4:
							int freeGenericPerc = ((ServiceDashboardInfo)element).getFreeGenericPerc();
							columnText = ((ServiceDashboardInfo)element).getServer().isConnected()
									&& freeGenericPerc >= 0 ? freeGenericPerc + "%" : "-";
							break;
					}
					cell.setText(columnText);
				}
			  }

			  @Override
			  public String getToolTipText(Object element) {
				  String tipText = null;
				  if (element instanceof ServiceDashboardInfo) {
						switch(type) {
							case 1:
								int freeDataPerc = ((ServiceDashboardInfo)element).getFreeDataPerc();
								String freeDataPercText = ((ServiceDashboardInfo)element).getServer().isConnected()
										&& freeDataPerc >= 0 ? freeDataPerc + "%" : "-";
								tipText = Messages.columnDataTip + " : " + freeDataPercText;
								break;
							case 2:
								int freeIndexPerc = ((ServiceDashboardInfo)element).getFreeIndexPerc();
								String freeIndexPercText = ((ServiceDashboardInfo)element).getServer().isConnected()
										&& freeIndexPerc >= 0 ? freeIndexPerc + "%" : "-";
								tipText = Messages.columnIndexTip + " : " + freeIndexPercText;
								break;
							case 3:
								int freeTempPerc = ((ServiceDashboardInfo)element).getFreeTempPerc();
								String freeTempPercText = ((ServiceDashboardInfo)element).getServer().isConnected()
										&& freeTempPerc >= 0 ? freeTempPerc + "%" : "-";
								tipText = Messages.columnTempTip + " : " + freeTempPercText;
								break;
							case 4:
								int freeGenericPerc = ((ServiceDashboardInfo)element).getFreeGenericPerc();
								String freeGenericPercText = ((ServiceDashboardInfo)element).getServer().isConnected()
										&& freeGenericPerc >= 0 ? freeGenericPerc + "%" : "-";
								tipText = Messages.columnGenericTip + " : " + freeGenericPercText;
								break;
						}
					}
			    return tipText;
			  }
		});
	}

	/**
	 * Set Sorter for Viewer Columns
	 */
	private void setColumnSorter(final TreeColumn column, final ServiceDashboardEditorSorter ascSorter,
			final ServiceDashboardEditorSorter descSorter){
		column.addSelectionListener(new SelectionAdapter() {
			boolean asc = true;
			public void widgetSelected(SelectionEvent e) {
				serviceTreeViewer.setSorter(asc ? ascSorter
						: descSorter);
				serviceTreeViewer.getTree().setSortColumn(column);
				serviceTreeViewer.getTree().setSortDirection(
						asc ? SWT.UP : SWT.DOWN);
				asc = !asc;
			}
		});
	}

	/**
	 * Execute Task Job
	 */
	private void executeTask(TaskJobExecutor taskJobExec, String serverName, String jobName){
		JobFamily jobFamily = new JobFamily();
		jobFamily.setServerName(serverName);
		taskJobExec.schedule(jobName, jobFamily, false, Job.SHORT);
	}

	/**
	 * Perform node changed
	 */
	public void nodeChanged(CubridNodeChangedEvent event) {
		ICubridNode cubridNode = event.getCubridNode();
		CubridNodeChangedEventType eventType = event.getType();
		if (cubridNode == null || eventType == null) {
			return;
		}

		if (NodeType.SERVER.equals(cubridNode.getType())) {
			if (CubridNodeChangedEventType.NODE_ADD.equals(event.getType())
					|| CubridNodeChangedEventType.NODE_REMOVE.equals(event.getType())) {
				loadAllData();
			} else {
				serviceTreeViewer.refresh(cubridNode);
			}
		}
	}

	public void doSave(IProgressMonitor monitor) {
	}

	public void doSaveAs() {
	}

	public boolean isDirty() {
		return false;
	}

	public boolean isSaveAsAllowed() {
		return false;
	}
}
