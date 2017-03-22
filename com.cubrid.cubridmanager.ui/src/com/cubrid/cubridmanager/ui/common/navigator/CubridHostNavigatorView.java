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
package com.cubrid.cubridmanager.ui.common.navigator;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.contexts.IContextService;

import com.cubrid.common.ui.common.action.ConnectionUrlExportAction;
import com.cubrid.common.ui.common.action.ExpandTreeItemAction;
import com.cubrid.common.ui.common.action.GroupSettingAction;
import com.cubrid.common.ui.common.action.OpenTargetAction;
import com.cubrid.common.ui.common.action.SwitchGroupModeAction;
import com.cubrid.common.ui.common.action.UnExpandTreeItemAction;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.common.navigator.DecoratingLabelProvider;
import com.cubrid.common.ui.common.navigator.DeferredContentProvider;
import com.cubrid.common.ui.common.navigator.dnd.CubridDnDNodeHandler;
import com.cubrid.common.ui.er.action.OpenSchemaEditorAction;
import com.cubrid.common.ui.spi.ICubridGroupNodeManager;
import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.persist.PersistUtils;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfo;
import com.cubrid.cubridmanager.core.common.model.OnOffType;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.common.Messages;
import com.cubrid.cubridmanager.ui.common.action.RefreshAction;
import com.cubrid.cubridmanager.ui.common.navigator.dnd.GroupNodeDnDHandler;
import com.cubrid.cubridmanager.ui.common.navigator.dnd.ItemNodeDnDHandler;
import com.cubrid.cubridmanager.ui.host.action.AddHostAction;
import com.cubrid.cubridmanager.ui.host.action.ChangeManagerPasswordAction;
import com.cubrid.cubridmanager.ui.host.action.ConnectHostAction;
import com.cubrid.cubridmanager.ui.host.action.CopyHostAction;
import com.cubrid.cubridmanager.ui.host.action.DeleteHostAction;
import com.cubrid.cubridmanager.ui.host.action.DisConnectHostAction;
import com.cubrid.cubridmanager.ui.host.action.EditHostAction;
import com.cubrid.cubridmanager.ui.host.action.PasteHostAction;
import com.cubrid.cubridmanager.ui.host.action.RenameHostAction;
import com.cubrid.cubridmanager.ui.host.dialog.UnifyHostConfigDialogDNDController;
import com.cubrid.cubridmanager.ui.spi.action.CubridActionBuilder;
import com.cubrid.cubridmanager.ui.spi.model.CubridBroker;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeTypeManager;
import com.cubrid.cubridmanager.ui.spi.persist.CMGroupNodePersistManager;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;

/**
 * This view part is responsible for show all CUBRID database object as tree
 * structure way
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class CubridHostNavigatorView extends CubridNavigatorView {
	public static final String ID = ID_CM;
	private static final String CM_NAVIGATOR_SHOWGROUP = "com.cubrid.manager.navigator.showgroup";
	/**
	 * Create the navigator
	 */
	protected void createNavigator() {
		tv.setSorter(new CMNavigatorViewSorter());
		tv.setContentProvider(new DeferredContentProvider());
		tv.setLabelProvider(new DecoratingLabelProvider(new NavigatorTreeLabelProvider()));
		tv.addDoubleClickListener(LayoutManager.getInstance());
		tv.addTreeListener(new ITreeViewerListener() {
			public void treeCollapsed(TreeExpansionEvent event) {
				CommonUITool.clearExpandedElements(tv);
			}

			public void treeExpanded(TreeExpansionEvent event) {
				CommonUITool.clearExpandedElements(tv);
			}
		});
		CubridActionBuilder.init();
		hookRetragetActions();
		addHostListener();
	}

	/**
	 * Hook the global actions in bars
	 */
	protected void hookRetragetActions() {
		ActionManager manager = ActionManager.getInstance();
		IActionBars bar = this.getViewSite().getActionBars();
		bar.setGlobalActionHandler(ActionFactory.COPY.getId(), manager.getAction(CopyHostAction.ID));
		bar.setGlobalActionHandler(ActionFactory.PASTE.getId(), manager.getAction(PasteHostAction.ID));
		bar.updateActionBars();
	}

	/**
	 * Add the drag drop support of tree.
	 *
	 * @param tree the tree need to drag and drop.
	 */
	protected void addDragDropSupport(final Tree tree) {
		super.addDragDropSupport(tree);
		//DropTarget for unify host config
		UnifyHostConfigDialogDNDController.registerDragSource(tv);
	}

	/**
	 * Build the context menu
	 *
	 * @param menuManager IMenuManager
	 */
	protected void buildPopupMenu(final IMenuManager menuManager) {
		IStructuredSelection selection = (IStructuredSelection) tv.getSelection();
		if (selection == null || selection.isEmpty()) {
			ActionManager.addActionToManager(menuManager, AddHostAction.ID);
			menuManager.add(new Separator());
//			ActionManager.addActionToManager(menuManager, OpenSchemaEditorAction.ID);
//			menuManager.add(new Separator());
			return;
		}

		ICubridNode node = null;
		Object obj = selection.getFirstElement();
		if (obj instanceof ICubridNode) {
			node = (ICubridNode) obj;
		} else {
			ActionManager.addActionToManager(menuManager, AddHostAction.ID);
			return;
		}

		String type = node.getType();
		if (CubridNodeType.SERVER.equals(type)) {
			if (ConnectHostAction.isSupportedNode(obj)) {
				ActionManager.addActionToManager(menuManager, ConnectHostAction.ID);
			}
			if (DisConnectHostAction.isSupportedNode(obj)) {
				ActionManager.addActionToManager(menuManager, DisConnectHostAction.ID);
			}
			menuManager.add(new Separator());
			ActionManager.addActionToManager(menuManager, AddHostAction.ID);
			ActionManager.addActionToManager(menuManager, EditHostAction.ID);
			ActionManager.addActionToManager(menuManager, DeleteHostAction.ID);
			menuManager.add(new Separator());
//			ActionManager.addActionToManager(menuManager, OpenSchemaEditorAction.ID);
//			menuManager.add(new Separator());
			ActionManager.addActionToManager(menuManager, ConnectionUrlExportAction.ID);
			menuManager.add(new Separator());
			ActionManager.addActionToManager(menuManager, ChangeManagerPasswordAction.ID);
			menuManager.add(new Separator());
		}

		ActionManager.getInstance().setActionsMenu(menuManager);
		if (CubridNodeTypeManager.isCanRefresh(node.getType())) {
			menuManager.add(new Separator());
			ActionManager.addActionToManager(menuManager, RefreshAction.ID);
		}
	}

	/**
	 * Get tooltip string
	 *
	 * @param cubridNode ICubridNode
	 * @return String
	 */
	protected String getToolTip(ICubridNode cubridNode) {
		StringBuffer toolTipText = new StringBuffer();
		if (cubridNode instanceof CubridServer) {
			CubridServer server = (CubridServer) cubridNode;
			String ip = server.getHostAddress();
			toolTipText.append(Messages.bind(Messages.tipIP, ip == null ? "" : ip)).append("\r\n");
			String port = server.getMonPort();
			toolTipText.append(Messages.bind(Messages.tipPort, port == null ? "" : port)).append("\r\n");
			String userName = server.getUserName();
			toolTipText.append(Messages.bind(Messages.tipUser, userName == null ? "" : userName)).append("\r\n");
			String jdbcVersion = server.getJdbcDriverVersion();
			toolTipText.append(Messages.bind(Messages.tipJDBC, jdbcVersion == null ? "" : jdbcVersion));
		} else if (cubridNode instanceof CubridBroker) {
			CubridBroker broker = (CubridBroker) cubridNode;
			BrokerInfo brokerInfo = broker.getBrokerInfo();
			String brokerMode = brokerInfo.getAccess_mode() == null ?
					Messages.tipBrokerAccessModeNotWorking : brokerInfo.getAccess_mode();
			toolTipText.append(Messages.bind(Messages.tipBrokerPort, brokerInfo.getPort())).append("\r\n");
			toolTipText.append(Messages.bind(Messages.tipBrokerAccessMode, brokerMode)).append("\r\n");
			if (OnOffType.ON.getText().equalsIgnoreCase(brokerInfo.getState())) {
				toolTipText.append(Messages.tipBrokerStatusON);
			} else {
				toolTipText.append(Messages.tipBrokerStatusOFF);
			}
		}
		return toolTipText.toString();
	}

	/**
	 * Build the view toolbar
	 *
	 * @param toolBarManager IToolBarManager
	 */
	protected void buildToolBar(IToolBarManager toolBarManager) {
		ActionManager manager = ActionManager.getInstance();
		RefreshAction refreshAction = (RefreshAction) ActionManager.getInstance().getAction(RefreshAction.ID);
		toolBarManager.add(refreshAction);

		OpenTargetAction openObjectTabAction = (OpenTargetAction) manager.getAction(OpenTargetAction.ID);
		toolBarManager.add(openObjectTabAction);

		SwitchGroupModeAction grpSwitchAction = (SwitchGroupModeAction) manager.getAction(SwitchGroupModeAction.ID);
		grpSwitchAction.setNavigatorView(this);
		grpSwitchAction.init();
		toolBarManager.add(grpSwitchAction);

		GroupSettingAction groupSettingAction = (GroupSettingAction) manager.getAction(GroupSettingAction.ID);
		groupSettingAction.setNavigatorView(this);
		toolBarManager.add(groupSettingAction);

		ExpandTreeItemAction expandAction = (ExpandTreeItemAction) manager.getAction(ExpandTreeItemAction.ID);
		expandAction.setTargetTreeViewer(tv);
		toolBarManager.add(expandAction);

		UnExpandTreeItemAction unexpandAction = (UnExpandTreeItemAction) manager.getAction(UnExpandTreeItemAction.ID);
		unexpandAction.setTv(tv);
		toolBarManager.add(unexpandAction);
	}

	/**
	 * Active the navigator context
	 */
	protected void activeContext() {
		IContextService contextService = (IContextService) getSite().getService(IContextService.class);
		if (contextService != null) {
			contextService.activateContext("com.cubrid.cubridmanager.contexts.navigator");
		}
	}
	/**
	 * Get the cubrid group node manager
	 *
	 * @return ICubridGroupNodeManager
	 */
	public ICubridGroupNodeManager getGroupNodeManager() {
		return CMGroupNodePersistManager.getInstance();
	}

	/**
	 * Get all group items.
	 *
	 * @return Object
	 */
	protected Object getGroupItems() {
		return CMHostNodePersistManager.getInstance().getAllServers();
	}

	/**
	 * Does the node support DND.
	 *
	 * @param source the dragged nodes.
	 * @return support or not.
	 */
	protected boolean dataSupportDND(TreeItem[] source) {
		if (source.length < 1) {
			return false;
		}
		boolean result1 = true;
		boolean result2 = true;
		for (TreeItem ti : source) {
			if (!(ti.getData() instanceof ICubridNode)) {
				return false;
			}
			String type = ((ICubridNode) ti.getData()).getType();
			result1 = result1 && (NodeType.SERVER.equals(type));
			result2 = result2
					&& (NodeType.SYSTEM_TABLE.equals(type)
							|| NodeType.SYSTEM_VIEW.equals(type)
							|| NodeType.USER_TABLE.equals(type)
							|| NodeType.USER_PARTITIONED_TABLE.equals(type)
							|| NodeType.USER_VIEW.equals(type)
							|| NodeType.USER_PARTITIONED_TABLE_FOLDER.equals(type)
							|| NodeType.TABLE_COLUMN.equals(type)
							//multipe dbquery use under type
							|| NodeType.DATABASE.equals(type)
							|| NodeType.DATABASE_FOLDER.equals(type)
							|| NodeType.GROUP.equals(type) || NodeType.SERVER.equals(type)

					);
		}
		return result1 || result2;

	}

	/**
	 * The view does support the group node. Subclass can override this method
	 * if it does't support group nodes.
	 *
	 * @return true:support.
	 */
	protected boolean isSupportGroup() {
		return true;
	}

	/**
	 * Set the visible attribute of group nodes.
	 *
	 * @param isShowGroup true:show;false:don't show.
	 */
	public void setShowGroup(boolean isShowGroup) {
		super.setShowGroup(isShowGroup);
		PersistUtils.setPreferenceValue(CubridManagerUIPlugin.PLUGIN_ID,
				CM_NAVIGATOR_SHOWGROUP, String.valueOf(isShowGroup));
	}

	/**
	 * Retrieves the show group option in local configuration file.
	 *
	 * @return true:show,false:don't show.
	 */
	public boolean savedIsShowGroup() {
		String show = PersistUtils.getPreferenceValue(CubridManagerUIPlugin.PLUGIN_ID, CM_NAVIGATOR_SHOWGROUP);
		if (show == null || show.trim().length() == 0) {
			return super.savedIsShowGroup();
		} else {
			return Boolean.parseBoolean(show);
		}
	}

	/**
	 * Only host node can be draged over the tree.
	 *
	 * @param target ICubridNode
	 * @return true:support.
	 */
	protected boolean dataSupportDragOver(ICubridNode target) {
		if (!NodeType.SERVER.equals(target.getType()) && !NodeType.GROUP.equals(target.getType())) {
			return false;
		}
		TreeItem[] tis = tv.getTree().getSelection();
		for (TreeItem ti : tis) {
			String type = ((ICubridNode) ti.getData()).getType();
			if (!NodeType.SERVER.equals(type)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Drop handler
	 *
	 * @return the CubridDnDNodeHandler;
	 */
	protected CubridDnDNodeHandler getCubridDnDNodeHandler() {
		if (isShowGroup()) {
			return new GroupNodeDnDHandler(this);
		}
		return new ItemNodeDnDHandler(this);
	}

	/**
	 * Add listener
	 */
	protected void addHostListener() {
		tv.getTree().addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e) {
			}

			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.F2) {
					TreeItem[] items = tv.getTree().getSelection();
					if (items == null || items.length == 0) {
						return;
					}

					if (items.length != 1) {
						return;
					}

					Object obj = items[0].getData();
					if (!(obj instanceof ICubridNode)) {
						return;
					}

					ICubridNode node = (ICubridNode) obj;
					if (NodeType.SERVER.equals(node.getType())) {
						ActionManager manager = ActionManager.getInstance();
						RenameHostAction renameAction = (RenameHostAction) manager.getAction(RenameHostAction.ID);
						renameAction.run((CubridServer) node);
					}
				}
			}
		});
	}
}
