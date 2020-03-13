/*
o * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.cubridmanager.ui.spi.contribution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.util.Util;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.common.action.OpenTargetAction;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.common.preference.GeneralPreference;
import com.cubrid.common.ui.cubrid.table.control.SchemaInfoEditorPart;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.query.editor.QueryUnit;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.ISelectionAction;
import com.cubrid.common.ui.spi.contribution.WorkbenchContrItem;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.MoreTablesNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.part.CubridViewPart;
import com.cubrid.common.ui.spi.persist.CubridJdbcManager;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.LayoutUtil;
import com.cubrid.cubridmanager.core.common.model.CertStatus;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.cubridmanager.core.cubrid.user.task.IsDBAUserTask;
import com.cubrid.cubridmanager.ui.broker.editor.BrokerEnvStatusView;
import com.cubrid.cubridmanager.ui.broker.editor.BrokerStatusView;
import com.cubrid.cubridmanager.ui.common.navigator.CubridHostNavigatorView;
import com.cubrid.cubridmanager.ui.cubrid.database.action.LoginDatabaseAction;
import com.cubrid.cubridmanager.ui.cubrid.database.dialog.LoginDatabaseTaskExecutor;
import com.cubrid.cubridmanager.ui.cubrid.database.editor.DatabaseDashboardEditor;
import com.cubrid.cubridmanager.ui.cubrid.jobauto.action.OpenJobAutomationInfoAction;
import com.cubrid.cubridmanager.ui.host.action.EditHostAction;
import com.cubrid.cubridmanager.ui.host.action.HostDashboardAction;
import com.cubrid.cubridmanager.ui.host.dialog.ConnectHostExecutor;
import com.cubrid.cubridmanager.ui.host.dialog.GenCertDialog;
import com.cubrid.cubridmanager.ui.logs.action.LogViewAction;
import com.cubrid.cubridmanager.ui.logs.action.ManagerLogViewAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.OpenMonitorDashboardAction;
import com.cubrid.cubridmanager.ui.monitoring.editor.BrokerStatusHistoryViewPart;
import com.cubrid.cubridmanager.ui.monitoring.editor.BrokerStatusMonitorViewPart;
import com.cubrid.cubridmanager.ui.monitoring.editor.DbStatusDumpMonitorViewPart;
import com.cubrid.cubridmanager.ui.monitoring.editor.DbStatusHistoryViewPart;
import com.cubrid.cubridmanager.ui.monitoring.editor.DbSystemMonitorHistoryViewPart;
import com.cubrid.cubridmanager.ui.monitoring.editor.DbSystemMonitorViewPart;
import com.cubrid.cubridmanager.ui.monitoring.editor.HostSystemMonitorHistoryViewPart;
import com.cubrid.cubridmanager.ui.monitoring.editor.HostSystemMonitorViewPart;
import com.cubrid.cubridmanager.ui.monitoring.editor.StatusMonitorViewPart;
import com.cubrid.cubridmanager.ui.monstatistic.action.OpenMonitorStatisticPageAction;
import com.cubrid.cubridmanager.ui.shard.editor.ShardEnvStatusView;
import com.cubrid.cubridmanager.ui.spi.Messages;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import com.cubrid.cubridmanager.ui.spi.persist.CMDBNodePersistManager;

/**
 * CUBRID Manager workbench contribution item implements
 * <code>WorkbenchContrItem</code> interface
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public final class CubridWorkbenchContrItem extends WorkbenchContrItem {
	private static final Logger LOGGER = LogUtil.getLogger(CubridWorkbenchContrItem.class);
	private static final List<String> MONITOR_VIEWID_LST = new ArrayList<String>();

	static {
		MONITOR_VIEWID_LST.add(StatusMonitorViewPart.ID);
		MONITOR_VIEWID_LST.add(BrokerStatusMonitorViewPart.ID);
		MONITOR_VIEWID_LST.add(DbStatusDumpMonitorViewPart.ID);
		MONITOR_VIEWID_LST.add(BrokerStatusHistoryViewPart.ID);
		MONITOR_VIEWID_LST.add(DbStatusHistoryViewPart.ID);
		MONITOR_VIEWID_LST.add(BrokerStatusView.ID);
		MONITOR_VIEWID_LST.add(BrokerEnvStatusView.ID);
		MONITOR_VIEWID_LST.add(HostSystemMonitorViewPart.ID);
		MONITOR_VIEWID_LST.add(DbSystemMonitorViewPart.ID);
		MONITOR_VIEWID_LST.add(HostSystemMonitorHistoryViewPart.ID);
		MONITOR_VIEWID_LST.add(DbSystemMonitorHistoryViewPart.ID);
		MONITOR_VIEWID_LST.add(ShardEnvStatusView.ID);
	}

	public void processSelectionChanged(SelectionChangedEvent event) {
		checkConnectionStatus(event.getSelection());
	}

	/**
	 * When selection changed,check whether selected server is connected
	 *
	 * @param selection the ISelection object
	 */
	private void checkConnectionStatus(ISelection selection) {
		if (selection == null || selection.isEmpty()) {
			return;
		}
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		if (!(obj instanceof ICubridNode)) {
			return;
		}
		ICubridNode cubridNode = (ICubridNode) obj;
		CubridServer cubridServer = cubridNode.getServer();
		if (cubridServer == null) {
			return;
		}
		ServerInfo serverInfo = cubridServer.getServerInfo();

		boolean needToCheck = !serverInfo.isConnected() && (!(obj instanceof CubridServer) || (cubridServer.getChildren() != null && cubridServer.getChildren().size() > 0));
		if (needToCheck) {
			boolean connectAgagin = false;
			if (CommonUITool.openConfirmBox(Messages.errCannotConnectServerReconnect)) {
				connectAgagin = true;
			}
			closeAllEditorAndViewInServer(cubridServer, false);
			cubridServer.removeAllChild();
			TreeViewer viewer = (TreeViewer) LayoutManager.getInstance().getSelectionProvider();
			viewer.refresh(cubridServer);
			CubridNodeManager.getInstance().fireCubridNodeChanged(
					new CubridNodeChangedEvent(cubridServer, CubridNodeChangedEventType.SERVER_DISCONNECTED));

			if (connectAgagin && connectHost(serverInfo, true)) {
				refreshAfterConnected(cubridServer);
			}
		}
	}

	public void processDoubleClickNavigatorEvent(DoubleClickEvent event) {
		ISelection selection = event.getSelection();
		if (selection == null || selection.isEmpty()) {
			return;
		}
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		if (!(obj instanceof ICubridNode)) {
			return;
		}
		// open the monitor dashboard view
		ICubridNode cubridNode = (ICubridNode) obj;
		if (CubridNodeType.MONITOR_DASHBOARD.equals(cubridNode.getType())) {
			ISelectionAction action = (ISelectionAction) ActionManager.getInstance().getAction(
					OpenMonitorDashboardAction.ID);
			if (action != null && action.isSupported(cubridNode)) {
				action.run();
				return;
			}
		}
		/*[TOOLS-3668] - MonitorStatistic node*/
		if (CubridNodeType.MONITOR_STATISTIC_PAGE.equals(cubridNode.getType())) {
			ISelectionAction action = (ISelectionAction) ActionManager.getInstance().getAction(
					OpenMonitorStatisticPageAction.ID);
			if (action != null && action.isSupported(cubridNode)) {
				action.run();
				return;
			}
		}
		/*CubridServer node*/
		if (cubridNode instanceof CubridServer) {
			CubridServer cubridServer = (CubridServer) cubridNode;
			if (cubridServer.isConnected()) {
				ServerInfo serverInfo = cubridServer.getServerInfo();
				//open host dashboard
				if (serverInfo != null && serverInfo.isConnected()) {
					openHostDashboard(serverInfo);
				}
			} else if (connectHost(cubridServer.getServerInfo(), true)) {
				refreshAfterConnected(cubridServer);
			} else {
				EditHostAction action = (EditHostAction) ActionManager.getInstance().getAction(EditHostAction.ID);
				if (action == null) {
					LOGGER.error("The EditHostAction is a null.");
				} else {
					action.run();
				}
			}
			return;
		}

		CubridServer server = cubridNode.getServer();
		if (server == null || !server.isConnected()) {
			return;
		}
		/*CubridDatabase node*/
		if (cubridNode instanceof CubridDatabase) {
			CubridDatabase database = (CubridDatabase) cubridNode;
			if (database.isLogined()) {
				openEditorOrView(database);
				return;
			}

			LoginDatabaseAction loginDatabaseAction = (LoginDatabaseAction) ActionManager.getInstance().getAction(
					LoginDatabaseAction.ID);
			loginDatabaseAction.doRun(new CubridDatabase[]{ database });
			return;
		}

		/*Other node*/
		if (!LayoutManager.getInstance().isUseClickOnce()) {
			if (NodeType.MORE.equals(cubridNode.getType())) {
				AbstractTreeViewer viewer = (AbstractTreeViewer) event.getSource();
				MoreTablesNode model = new MoreTablesNode(
						viewer, (DefaultSchemaNode) cubridNode);
				model.expandMoreTables();
				viewer.remove(cubridNode);
				return;
			}
			boolean useSelectQuery = ActionSupportUtil.isSupportMultiSelection(obj, new String[] {
					NodeType.USER_TABLE, NodeType.USER_VIEW,
					NodeType.SYSTEM_TABLE, NodeType.SYSTEM_VIEW,
					NodeType.USER_PARTITIONED_TABLE_FOLDER }, false);
			if (useSelectQuery) {
				if (!Util.isWindows()) {
					openSelectQuery(selection);
				}
			} else {
				openEditorOrView(cubridNode);
			}
		}

		boolean useExpandFolder = NodeType.contains(cubridNode.getType(), new String[] {
				NodeType.TABLE_FOLDER,
				NodeType.VIEW_FOLDER,
				NodeType.TRIGGER_FOLDER,
				NodeType.SERIAL_FOLDER,
				NodeType.USER_FOLDER,
				CubridNodeType.JOB_FOLDER });
		if (useExpandFolder) {
			CubridDatabase database = (CubridDatabase)cubridNode.getParent();
			CubridNavigatorView view = CubridNavigatorView.getNavigatorView(CubridHostNavigatorView.ID);
			TreeViewer treeViewer = view.getViewer();
			if (cubridNode.getType() == NodeType.TABLE_FOLDER) {
				// if not expand, expand the node and wait until all children be added
				int sleepCount = 0;
				if (!treeViewer.getExpandedState(cubridNode)) {
					treeViewer.expandToLevel(cubridNode, 1);
					while (cubridNode.getChildren().size() == 0) {
						try {
							Thread.sleep(500);
							sleepCount++;
							if (sleepCount > 5) {
								break;
							}
						} catch (Exception e) {
							LOGGER.debug("", e);
						}
					}
				}
				openTablesDetailInfoPart(database);
				return;
			}
			//if not expand ,expand the node
			//if not open child node ,edit serial from dashboard can not open edit dialog
			if (!treeViewer.getExpandedState(cubridNode)) {
				treeViewer.expandToLevel(cubridNode, 1);
			}
			if (StringUtil.isEqual(cubridNode.getType(), NodeType.VIEW_FOLDER)) {
				openViewsDetailInfoPart(database);
			} else if (StringUtil.isEqual(cubridNode.getType(), NodeType.TRIGGER_FOLDER)) {
				openTriggersDetailInfoPart(database);
			} else if (StringUtil.isEqual(cubridNode.getType(), NodeType.SERIAL_FOLDER)) {
				openSerialsDetailInfoPart(database);
			} else if (StringUtil.isEqual(cubridNode.getType(), CubridNodeType.JOB_FOLDER)) {
				openJobsDetailInfoPart(database);
			}else if (StringUtil.isEqual(cubridNode.getType(), NodeType.USER_FOLDER)) {
				openUsersDetailInfoPart(database);
			}
		}
	}

	private static void openHostDashboard(ServerInfo serverInfo) {
		HostDashboardAction action = (HostDashboardAction) ActionManager.getInstance().getAction(HostDashboardAction.ID);
		if (action == null) {
			LOGGER.error("The HostDashboardAction is a null.");
			return;
		}

		if (serverInfo != null) {
			action.doRun(serverInfo);
		} else {
			action.run();
		}
	}

	private void refreshAfterConnected(CubridServer cubridServer) {
		cubridServer.getLoader().setLoaded(false);
		CubridNavigatorView view = CubridNavigatorView.getNavigatorView(CubridHostNavigatorView.ID);
		TreeViewer treeViewer = view.getViewer();
		treeViewer.refresh(cubridServer, true);
		treeViewer.expandToLevel(cubridServer, 1);

		ActionManager.getInstance().fireSelectionChanged(treeViewer.getSelection());
		LayoutManager.getInstance().fireSelectionChanged(treeViewer.getSelection());
		CubridNodeManager.getInstance().fireCubridNodeChanged(
				new CubridNodeChangedEvent(cubridServer, CubridNodeChangedEventType.SERVER_CONNECTED));
	}

	/**
	 * openTablesDetailInfoPart
	 *
	 * @param CubridDatabase database
	 */
	public void openTablesDetailInfoPart(CubridDatabase database) {
		OpenTargetAction action = new OpenTargetAction();
		action.showTableDashboard(database);
	}

	/**
	 * openViewsDetailInfoPart
	 *
	 * @param CubridDatabase database
	 */
	public void openViewsDetailInfoPart(CubridDatabase database) {
		OpenTargetAction action = new OpenTargetAction();
		action.openViewsDetailInfoEditor(database);
	}

	/**
	 * openSerialsDetailInfoPart
	 *
	 * @param CubridDatabase database
	 */
	public void openSerialsDetailInfoPart(CubridDatabase database) {
		OpenTargetAction action = new OpenTargetAction();
		action.openSerialsDetailInfoEditor(database, null);
	}

	/**
	 * openTriggersDetailInfoPart
	 *
	 * @param CubridDatabase database
	 */
	public void openTriggersDetailInfoPart(CubridDatabase database) {
		OpenTargetAction action = new OpenTargetAction();
		action.openTriggersDetailInfoEditor(database);
	}

	/**
	 * openJobDetailInfoPart
	 *
	 * @param CubridDatabase database
	 */
	public void openJobsDetailInfoPart(CubridDatabase database) {
		OpenJobAutomationInfoAction action = new OpenJobAutomationInfoAction();
		action.openJobsDetailInfoEditor(database);
	}

	/**
	 * openJobDetailInfoPart
	 *
	 * @param CubridDatabase database
	 */
	public void openUsersDetailInfoPart(CubridDatabase database) {
		OpenTargetAction action = new OpenTargetAction();
		action.openUsersDetailInfoEditor(database);
	}

	public void openSelectQuery(ISelection selection) {
		final Object obj = ((IStructuredSelection) selection).getFirstElement();
		if (!(obj instanceof ICubridNode)) {
			return;
		}

		ISchemaNode table = (ISchemaNode) obj;
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			return;
		}

		boolean existsEditor = false;
		QueryEditorPart editor = null;
		QueryUnit input = new QueryUnit();
		input.setDatabase(table.getDatabase());
		try {
			IEditorPart editorPart = window.getActivePage().getActiveEditor();
			if (editorPart != null && editorPart instanceof QueryEditorPart) {
				QueryEditorPart queryEditorPart = (QueryEditorPart)editorPart;
				if (queryEditorPart.getSelectedDatabase() == input.getDatabase()) {
					editor = (QueryEditorPart) editorPart;
					existsEditor = true;
				}
			}

			if (editor == null) {
				editor = (QueryEditorPart) window.getActivePage().openEditor(input, QueryEditorPart.ID);
				editor.connect(table.getDatabase());
			}

			DefaultSchemaNode tableNode = (DefaultSchemaNode) obj;
			String sql = getStmtSQL(tableNode) + StringUtil.NEWLINE + StringUtil.NEWLINE;
			if (existsEditor) {
				editor.newQueryTab(sql, true);
			} else {
				editor.setQuery(sql, false, true, false);
			}
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}

	/**
	 * Connect host
	 *
	 * @param serverInfo ServerInfo
	 * @param showErrMsg boolean
	 * @return boolean
	 */
	public static boolean connectHost(ServerInfo serverInfo, boolean showErrMsg) {
		String password = serverInfo == null ? null : serverInfo.getUserPassword();
		if (password == null || password.trim().length() == 0) {
			return false;
		}

		Map<String, String> jdbcVersionMap = CubridJdbcManager.getInstance().getLoadedJdbc();
		if (jdbcVersionMap == null || jdbcVersionMap.get(serverInfo.getJdbcDriverVersion()) == null) {
			return false;
		}

		TaskExecutor taskExcutor = new ConnectHostExecutor(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), serverInfo, showErrMsg);
		new ExecTaskWithProgress(taskExcutor).busyCursorWhile();

		boolean isConnected = taskExcutor.isSuccess();
		if (isConnected) {
			// if preference use dashboard, when connect successful,open host dashboard
			if (GeneralPreference.isUseHostDashboard() && serverInfo.isConnected()) {
				openHostDashboard(serverInfo);
			}

//			if (serverInfo.isCheckCertStatus()
//					&& CertStatus.DEFAULT.equals(serverInfo.getCertStatus())) {
//				GenCertDialog dialog = new GenCertDialog(
//						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), serverInfo);
//				dialog.open();
//			}

		}

		return isConnected;
	}

	/**
	 * Connect host
	 *
	 * @param serverInfo ServerInfo
	 * @param showErrMsg boolean
	 * @return error messages String
	 */
	public static String connectHostWithErrMsg(ServerInfo serverInfo,boolean showErrMsg) {
		String password = serverInfo == null ? null : serverInfo.getUserPassword();
		if (password == null || password.trim().length() == 0) {
			return com.cubrid.cubridmanager.ui.host.Messages.errUserPasswordConnect;
		}
		Map<String, String> jdbcVersionMap = CubridJdbcManager.getInstance().getLoadedJdbc();
		if (jdbcVersionMap == null || jdbcVersionMap.get(serverInfo.getJdbcDriverVersion()) == null) {
			return com.cubrid.cubridmanager.ui.host.Messages.errNoSupportDriver;
		}
		ConnectHostExecutor taskExcutor = new ConnectHostExecutor(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), serverInfo, showErrMsg);
		new ExecTaskWithProgress(taskExcutor).busyCursorWhile();

		if (taskExcutor.isSuccess() && GeneralPreference.isUseHostDashboard() && serverInfo != null && serverInfo.isConnected()) {
			HostDashboardAction hostDashboardAction = (HostDashboardAction)ActionManager.getInstance().getAction(HostDashboardAction.ID);
			hostDashboardAction.doRun(serverInfo);
		}

		return taskExcutor.getErrMsg();
	}

	/**
	 * Connect the database
	 *
	 * @param dbInfo DatabaseInfo
	 * @param showErrMsg boolean
	 * @return boolean
	 */
	public static boolean connectDatabase(DatabaseInfo dbInfo, boolean showErrMsg) {
		if (dbInfo == null || dbInfo.getServerInfo() == null || dbInfo.getAuthLoginedDbUserInfo() == null) {
			return false;
		}
		final String USER_DBA = "dba";
		DbUserInfo dbUserInfo = dbInfo.getAuthLoginedDbUserInfo();
		String dbUser = dbUserInfo.getName();
		String dbPassword = dbUserInfo.getNoEncryptPassword();
		if (dbUser == null || dbUser.trim().length() == 0 || dbPassword == null) {
			return false;
		}

		TaskExecutor taskExcutor = new LoginDatabaseTaskExecutor(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				dbInfo.getServerInfo(), dbInfo.getDbName(), dbUser, dbPassword, showErrMsg);
		new ExecTaskWithProgress(taskExcutor).busyCursorWhile();
		boolean isSuccess = taskExcutor.isSuccess();
		if (!isSuccess) {
			LOGGER.error("login database failed");
		}

		/*For [TOOLS-3516]*/
		if (dbInfo.getAuthLoginedDbUserInfo() != null
				&& CompatibleUtil.isNeedCheckDbaAuthorityByJDBC(dbInfo)) {
			IsDBAUserTask checkTask = new IsDBAUserTask(dbInfo);
			checkTask.execute();
			if (checkTask.isSuccess()) {
				dbInfo.getAuthLoginedDbUserInfo().setDbaAuthority(
						checkTask.isDBAUser());
			} else {
				dbInfo.getAuthLoginedDbUserInfo().setDbaAuthority(
						USER_DBA.equals(dbUser.toLowerCase()));
			}
		}
		/*Modify*/
		CMDBNodePersistManager.getInstance().fireModifyDatabase(dbInfo, dbInfo);

		return taskExcutor.isSuccess();
	}

	/**
	 * Connect the database
	 *
	 * @param dbInfo DatabaseInfo
	 * @param showErrMsg boolean
	 * @return string error messages
	 */
	public static String connectDatabaseWithErrMsg(DatabaseInfo dbInfo,boolean showErrMsg) {
		if (dbInfo == null || dbInfo.getServerInfo() == null || dbInfo.getAuthLoginedDbUserInfo() == null) {
			return "";
		}

		final String USER_DBA = "dba";
		DbUserInfo dbUserInfo = dbInfo.getAuthLoginedDbUserInfo();
		String dbUser = dbUserInfo.getName();
		String dbPassword = dbUserInfo.getNoEncryptPassword();
		if (dbUser == null || dbUser.trim().length() == 0 || dbPassword == null) {
			return Messages.errMultiDatabaseLoginNoUsername;
		}

		LoginDatabaseTaskExecutor taskExcutor = new LoginDatabaseTaskExecutor(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				dbInfo.getServerInfo(), dbInfo.getDbName(), dbUser, dbPassword, showErrMsg);
		new ExecTaskWithProgress(taskExcutor).busyCursorWhile();

		/*For [TOOLS-3516]*/
		if (dbInfo.getAuthLoginedDbUserInfo() != null
				&& CompatibleUtil.isNeedCheckDbaAuthorityByJDBC(dbInfo)) {
			IsDBAUserTask checkTask = new IsDBAUserTask(dbInfo);
			checkTask.execute();
			if (checkTask.isSuccess()) {
				dbInfo.getAuthLoginedDbUserInfo().setDbaAuthority(
						checkTask.isDBAUser());
			} else {
				dbInfo.getAuthLoginedDbUserInfo().setDbaAuthority(
						USER_DBA.equals(dbUser.toLowerCase()));
			}
		}

		/*Modify*/
		CMDBNodePersistManager.getInstance().fireModifyDatabase(dbInfo, dbInfo);

		return taskExcutor.getErrMsg();
	}

	/**
	 * Open and reopen the editor or view part of this CUBRID node
	 *
	 * @param cubridNode the ICubridNode object
	 */
	public void openEditorOrView(ICubridNode cubridNode) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			return;
		}
		if (cubridNode instanceof ISchemaNode) {
			ISchemaNode schemaNode = (ISchemaNode) cubridNode;
			if (schemaNode.getDatabase() != null && !schemaNode.getDatabase().isLogined()) {
				return;
			}
		}
		//close the editor part that has been open
		String editorId = cubridNode.getEditorId();
		String viewId = cubridNode.getViewId();
		IWorkbenchPart workbenchPart = null;
		if (editorId != null && editorId.trim().length() > 0) {
			IEditorPart editorPart = LayoutUtil.getEditorPart(cubridNode, editorId);
			if (editorPart != null) {
				window.getActivePage().closeEditor(editorPart, false);
			}
		} else if (viewId != null && viewId.trim().length() > 0) {
			IViewPart viewPart = LayoutUtil.getViewPart(cubridNode, viewId);
			if (viewPart != null) {
				// monitor view part do not need to close and then open
				if (MONITOR_VIEWID_LST.contains(viewId)) {
					workbenchPart = viewPart;
					window.getActivePage().bringToTop(viewPart);
				} else {
					window.getActivePage().hideView(viewPart);
				}
			}
		}
		String nodeType = cubridNode.getType();
		ISelectionAction logViewAction = null;

		if (NodeType.contains(nodeType, new String[] {
				CubridNodeType.BROKER_SQL_LOG,
				CubridNodeType.LOGS_BROKER_ACCESS_LOG,
				CubridNodeType.LOGS_BROKER_ERROR_LOG,
				CubridNodeType.LOGS_BROKER_ADMIN_LOG,
				CubridNodeType.LOGS_SERVER_DATABASE_LOG })) {
			logViewAction = (ISelectionAction) ActionManager.getInstance().getAction(LogViewAction.ID);
			((LogViewAction) logViewAction).setCubridNode(cubridNode);
		} else if (NodeType.contains(nodeType, new String[] {
				CubridNodeType.LOGS_MANAGER_ACCESS_LOG,
				CubridNodeType.LOGS_MANAGER_ERROR_LOG })) {
			logViewAction = (ISelectionAction) ActionManager.getInstance().getAction(ManagerLogViewAction.ID);
			((ManagerLogViewAction) logViewAction).setCubridNode(cubridNode);
		}

		if (logViewAction != null && logViewAction.isSupported(cubridNode)) {
			logViewAction.run();
			return;
		}

		if (!StringUtil.isEmpty(editorId)) {
			try {
				if (cubridNode instanceof ISchemaNode) {
					CubridDatabase database = ((ISchemaNode) cubridNode).getDatabase();
					// Judge database is started and open DatabaseDashboardEditor
					if (StringUtil.isEqual(editorId, DatabaseDashboardEditor.ID)) {
						if (!DbRunningType.CS.equals(database.getRunningType())) {
							return;
						}
					}
					// if open the table schema editor,firstly load the schema
					if (StringUtil.isEqual(editorId, SchemaInfoEditorPart.ID)) {
						SchemaInfo newSchema = database.getDatabaseInfo().getSchemaInfo(
								cubridNode.getName());
						if (newSchema == null) {
							CommonUITool.openErrorBox(database.getDatabaseInfo().getErrorMessage());
							return;
						}
					}
				}

				workbenchPart = window.getActivePage().openEditor(cubridNode,
						editorId, true,
						IWorkbenchPage.MATCH_ID & IWorkbenchPage.MATCH_INPUT);
			} catch (PartInitException e) {
				LOGGER.error("", e);
			}
		} else if (viewId != null && viewId.trim().length() > 0) {
			try {
				if (MONITOR_VIEWID_LST.contains(viewId)) {
					if (workbenchPart == null) {
						String secondId = LayoutUtil.getViewSecondId(cubridNode);
						workbenchPart = window.getActivePage().showView(viewId, secondId,
								IWorkbenchPage.VIEW_CREATE | IWorkbenchPage.VIEW_ACTIVATE | IWorkbenchPage.VIEW_VISIBLE);
						window.getActivePage().bringToTop(workbenchPart);
					}
				} else {
					workbenchPart = window.getActivePage().showView(viewId);
				}
			} catch (Exception e) {
				LOGGER.error("", e);
			}
		}

		if (workbenchPart != null) {
			LayoutManager.getInstance().getTitleLineContrItem().changeTitleForViewOrEditPart(cubridNode, workbenchPart);
			LayoutManager.getInstance().getStatusLineContrItem().changeStuatusLineForViewOrEditPart(cubridNode, workbenchPart);
		}
	}

	/**
	 * ReOpen the editor or view part of this CUBRID node
	 *
	 * @param cubridNode the ICubridNode object
	 */
	public void reopenEditorOrView(ICubridNode cubridNode) {
		if (cubridNode == null) {
			return;
		}
		String editorId = cubridNode.getEditorId();
		String viewId = cubridNode.getViewId();
		if (editorId != null && editorId.trim().length() > 0) {
			IEditorPart editorPart = LayoutUtil.getEditorPart(cubridNode, editorId);
			if (editorPart != null) {
				openEditorOrView(cubridNode);
			}
		} else if (viewId != null && viewId.trim().length() > 0) {
			IViewPart viewPart = LayoutUtil.getViewPart(cubridNode, viewId);
			if (viewPart != null) {
				openEditorOrView(cubridNode);
			}
		}
	}

	/**
	 * Close all editor and view part related with this CUBRID Manager database
	 * node,not include query editor
	 *
	 * @param databaseNode the CubridDatabase object
	 * @param eventType CubridNodeChangedEventType
	 */
	public void closeAllEditorAndViewInDatabase(CubridDatabase databaseNode, CubridNodeChangedEventType eventType) {
		IWorkbenchPage page = LayoutUtil.getActivePage();
		if (page == null) {
			return;
		}

		IEditorReference[] editorRefArr = page.getEditorReferences();
		for (int i = 0; editorRefArr != null && i < editorRefArr.length; i++) {
			IEditorReference editorRef = editorRefArr[i];
			try {
				IEditorInput editorInput = editorRef.getEditorInput();
				if (!(editorInput instanceof ISchemaNode)) {
					continue;
				}
				ISchemaNode schemaNode = ((ISchemaNode) editorInput);
				ISchemaNode dbNode = schemaNode.getDatabase();
				String type = schemaNode.getType();
				boolean isDbSpaceEditor = NodeType.contains(type, new String[] {
						CubridNodeType.DATABASE,
						CubridNodeType.DBSPACE_FOLDER,
						CubridNodeType.GENERIC_VOLUME_FOLDER,
						CubridNodeType.GENERIC_VOLUME,
						CubridNodeType.DATA_VOLUME_FOLDER,
						CubridNodeType.DATA_VOLUME,
						CubridNodeType.INDEX_VOLUME_FOLDER,
						CubridNodeType.INDEX_VOLUME,
						CubridNodeType.TEMP_VOLUME_FOLDER,
						CubridNodeType.TEMP_VOLUME,
						CubridNodeType.LOG_VOLUEM_FOLDER,
						CubridNodeType.ACTIVE_LOG_FOLDER,
						CubridNodeType.ACTIVE_LOG,
						CubridNodeType.ARCHIVE_LOG_FOLDER,
						CubridNodeType.ARCHIVE_LOG });
				boolean isClose = true;
				if (isDbSpaceEditor) {
					isClose = !databaseNode.isLogined()
							|| CubridNodeChangedEventType.NODE_REMOVE == eventType
							|| CubridNodeChangedEventType.DATABASE_LOGOUT == eventType;
				}
				if (!isClose) {
					continue;
				}
				if (dbNode.getId().equals(databaseNode.getId())) {
					page.closeEditor(editorRef.getEditor(false), true);
				}
			} catch (Exception e) {
				LOGGER.error("", e);
			}
		}

		IViewReference[] viewRefArr = page.getViewReferences();
		if (viewRefArr == null || viewRefArr.length == 0) {
			return;
		}
		for (IViewReference viewRef : viewRefArr) {
			IViewPart viewPart = viewRef.getView(false);
			if (!(viewPart instanceof CubridViewPart)) {
				continue;
			}

			CubridViewPart cubridViewPart = (CubridViewPart) viewPart;
			ICubridNode cubridNode = cubridViewPart.getCubridNode();
			if (!(cubridNode instanceof ISchemaNode)) {
				continue;
			}
			ICubridNode cubridDatabaseNode = ((ISchemaNode) cubridNode).getDatabase();
			if (cubridDatabaseNode.getId().equals(databaseNode.getId())) {
				page.hideView(viewPart);
			}
		}
	}

	/**
	 * Close all editor and view part related with this CUBRID Manager server
	 * node,not include query editor
	 *
	 * @param serverNode the ICubridNode object
	 * @param isSaved the boolean value,if can be saved
	 * @return boolean whether all editors are closed
	 */
	public static boolean closeAllEditorAndViewInServer(ICubridNode serverNode,	boolean isSaved) {
		boolean isCloseAll = true;
		IWorkbenchPage page = LayoutUtil.getActivePage();
		if (page == null) {
			return isCloseAll;
		}

		IEditorReference[] editorRefArr = page.getEditorReferences();
		if (editorRefArr != null && editorRefArr.length > 0) {
			for (IEditorReference editorRef : editorRefArr) {
				try {
					IEditorInput editorInput = editorRef.getEditorInput();
					if (!(editorInput instanceof ICubridNode)) {
						continue;
					}
					ICubridNode node = ((ICubridNode) editorInput).getServer();
					if (node != null && node.getId().equals(serverNode.getId())) {
						boolean isClosed = page.closeEditor(editorRef.getEditor(false), isSaved);
						if (!isClosed) {
							isCloseAll = false;
						}
					}

				} catch (PartInitException e) {
					LOGGER.error("", e);
				}
			}
		}

		IViewReference[] viewRefArr = page.getViewReferences();
		if (viewRefArr != null && viewRefArr.length > 0) {
			for (IViewReference viewRef : viewRefArr) {
				IViewPart viewPart = viewRef.getView(false);
				if (!(viewPart instanceof CubridViewPart)) {
					continue;
				}
				CubridViewPart cubridViewPart = (CubridViewPart) viewPart;
				ICubridNode cubridNode = cubridViewPart.getCubridNode();
				if (cubridNode == null) {
					continue;
				}
				ICubridNode cubridServerNode = cubridNode.getServer();
				if (cubridServerNode.getId().equals(serverNode.getId())) {
					page.hideView(viewPart);
				}
			}
		}

		return isCloseAll;
	}
}
