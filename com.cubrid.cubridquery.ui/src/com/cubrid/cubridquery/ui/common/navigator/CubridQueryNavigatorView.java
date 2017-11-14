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
package com.cubrid.cubridquery.ui.common.navigator;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.contexts.IContextService;

import com.cubrid.common.core.util.StringUtil;
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
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.persist.PersistUtils;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.ui.spi.persist.CQBDBNodePersistManager;
import com.cubrid.cubridmanager.ui.spi.persist.CQBGroupNodePersistManager;
import com.cubrid.cubridquery.ui.CubridQueryUIPlugin;
import com.cubrid.cubridquery.ui.common.Messages;
import com.cubrid.cubridquery.ui.common.action.ChangeShardAction;
import com.cubrid.cubridquery.ui.common.action.RefreshAction;
import com.cubrid.cubridquery.ui.common.navigator.dnd.GroupNodeDnDHandler;
import com.cubrid.cubridquery.ui.common.navigator.dnd.ItemNodeDnDHandler;
import com.cubrid.cubridquery.ui.connection.action.CloseQueryConnAction;
import com.cubrid.cubridquery.ui.connection.action.ConnectionUrlImportAction;
import com.cubrid.cubridquery.ui.connection.action.CopyQueryConnAction;
import com.cubrid.cubridquery.ui.connection.action.DeleteQueryConnAction;
import com.cubrid.cubridquery.ui.connection.action.EditQueryConnAction;
import com.cubrid.cubridquery.ui.connection.action.NewQueryConnAction;
import com.cubrid.cubridquery.ui.connection.action.OpenQueryConnAction;
import com.cubrid.cubridquery.ui.connection.action.PasteQueryConnAction;
import com.cubrid.cubridquery.ui.connection.action.RenameConnectionAction;
import com.cubrid.cubridquery.ui.spi.CubridNodeTypeManager;
import com.cubrid.cubridquery.ui.spi.action.CubridActionBuilder;

/**
 * This view part is responsible for show all CUBRID database object as tree
 * structure way
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class CubridQueryNavigatorView extends CubridNavigatorView {
	public static final String ID = ID_CQB;
	private static final String COM_CUBRID_QUERYBROWSER_NAVIGATOR_SHOWGROUP = "com.cubrid.querybrowser.navigator.showgroup";

	/**
	 * Create the navigator
	 */
	protected void createNavigator() {
		tv.setSorter(new CQBNavigatorViewSorter());
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
		addConnectionListener();
	}

	/**
	 * Hook the global actions in bars
	 */
	protected void hookRetragetActions() {
		IActionBars bar = this.getViewSite().getActionBars();
		bar.setGlobalActionHandler(ActionFactory.COPY.getId(),
				ActionManager.getInstance().getAction(CopyQueryConnAction.ID));
		bar.setGlobalActionHandler(ActionFactory.PASTE.getId(),
				ActionManager.getInstance().getAction(PasteQueryConnAction.ID));
		bar.updateActionBars();
	}

	/**
	 * Build the context menu
	 *
	 * @param menuManager IMenuManager
	 */
	protected void buildPopupMenu(final IMenuManager menuManager) {
		IStructuredSelection selection = (IStructuredSelection) tv.getSelection();
		if (selection == null || selection.isEmpty()) {
			ActionManager.addActionToManager(menuManager, NewQueryConnAction.ID);
			menuManager.add(new Separator());
			return;
		}
		ICubridNode node = null;
		Object obj = selection.getFirstElement();
		if (obj instanceof ICubridNode) {
			node = (ICubridNode) obj;
		} else {
			ActionManager.addActionToManager(menuManager, NewQueryConnAction.ID);
			return;
		}

		String type = node.getType();
		if (NodeType.DATABASE.equals(type)) {
			ActionManager.addActionToManager(menuManager, OpenQueryConnAction.ID);
			ActionManager.addActionToManager(menuManager, CloseQueryConnAction.ID);
			menuManager.add(new Separator());
			ActionManager.addActionToManager(menuManager, ChangeShardAction.ID);
			menuManager.add(new Separator());
			ActionManager.addActionToManager(menuManager, NewQueryConnAction.ID);
			ActionManager.addActionToManager(menuManager, EditQueryConnAction.ID);
			ActionManager.addActionToManager(menuManager, DeleteQueryConnAction.ID);
			menuManager.add(new Separator());
			ActionManager.addActionToManager(menuManager, ConnectionUrlImportAction.ID);
			ActionManager.addActionToManager(menuManager, ConnectionUrlExportAction.ID);
			menuManager.add(new Separator());
		}

		ActionManager.getInstance().setActionsMenu(menuManager);
		if (CubridNodeTypeManager.isCanRefresh(node.getType())) {
			menuManager.add(new Separator());
			ActionManager.addActionToManager(menuManager, RefreshAction.ID);
		}
	}

	/**
	 * Build the view toolbar
	 *
	 * @param toolBarManager IToolBarManager
	 */
	protected void buildToolBar(IToolBarManager toolBarManager) {
		OpenTargetAction openObjectTabAction = (OpenTargetAction) ActionManager.getInstance().getAction(
				OpenTargetAction.ID);
		toolBarManager.add(openObjectTabAction);

		SwitchGroupModeAction grpSwitchAction = (SwitchGroupModeAction) ActionManager.getInstance().getAction(
				SwitchGroupModeAction.ID);
		grpSwitchAction.setNavigatorView(this);
		grpSwitchAction.init();
		toolBarManager.add(grpSwitchAction);

		GroupSettingAction groupSettingAction = (GroupSettingAction) ActionManager
				.getInstance().getAction(GroupSettingAction.ID);
		groupSettingAction.setNavigatorView(this);
		toolBarManager.add(groupSettingAction);

		ExpandTreeItemAction expandAction = (ExpandTreeItemAction) ActionManager
				.getInstance().getAction(ExpandTreeItemAction.ID);
		expandAction.setTargetTreeViewer(tv);
		toolBarManager.add(expandAction);

		UnExpandTreeItemAction unexpandAction = (UnExpandTreeItemAction) ActionManager
				.getInstance().getAction(UnExpandTreeItemAction.ID);
		unexpandAction.setTv(tv);
		toolBarManager.add(unexpandAction);
	}

	/**
	 * Active the navigator context
	 */
	protected void activeContext() {
		IContextService contextService = (IContextService) getSite().getService(IContextService.class);
		if (contextService != null) {
			contextService.activateContext("com.cubrid.cubridquery.contexts.navigator");
		}
	}
	/**
	 * Get all group items
	 *
	 * @return Object
	 */
	protected Object getGroupItems() {
		return CQBDBNodePersistManager.getInstance().getAllDatabase();
	}

	/**
	 * Get the cubrid group node manager
	 *
	 * @return ICubridGroupNodeManager
	 */
	public ICubridGroupNodeManager getGroupNodeManager() {
		return CQBGroupNodePersistManager.getInstance();
	}

	/**
	 *
	 * Get tooltip string
	 *
	 * @param cubridNode ICubridNode
	 * @return String
	 */
	protected String getToolTip(ICubridNode cubridNode) {
		if (cubridNode instanceof CubridDatabase) {
			CubridDatabase database = (CubridDatabase) cubridNode;
			DatabaseInfo dbInfo = database.getDatabaseInfo();

			String ip = database.getDatabaseInfo().getBrokerIP();
			String ipMsg = Messages.bind(Messages.tipIP, StringUtil.nvl(ip));

			String port = database.getDatabaseInfo().getBrokerPort();
			String portMsg = Messages.bind(Messages.tipPort, StringUtil.nvl(port));

			String userName = database.getUserName();
			String userNameMsg = Messages.bind(Messages.tipUser, StringUtil.nvl(userName));

			String jdbcVersion = database.getDatabaseInfo().getServerInfo().getJdbcDriverVersion();
			String jdbcVersionMsg = Messages.bind(Messages.tipJDBC, StringUtil.nvl(jdbcVersion));

			StringBuffer toolTipText = new StringBuffer();
			toolTipText.append(ipMsg).append(StringUtil.NEWLINE);
			toolTipText.append(portMsg).append(StringUtil.NEWLINE);

			// [TOOLS-2425]Support shard broker
			if (dbInfo.isShard()) {
				String shardIdMsg = null;
				if (dbInfo.getShardQueryType() == DatabaseInfo.SHARD_QUERY_TYPE_ID) {
					int shardId = dbInfo.getCurrentShardId();
					shardIdMsg = Messages.bind(Messages.tipShardId, shardId);
				} else {
					int shardVal = dbInfo.getCurrentShardVal();
					shardIdMsg = Messages.bind(Messages.tipShardVal, shardVal);
				}
				toolTipText.append(shardIdMsg).append(StringUtil.NEWLINE);
			}

			toolTipText.append(userNameMsg).append(StringUtil.NEWLINE);
			toolTipText.append(jdbcVersionMsg);

			return toolTipText.toString();
		}

		return "";
	}

	/**
	 * Does the node support DND.
	 *
	 * @param source the dragged node.
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
			result1 = result1 && (NodeType.DATABASE.equals(type));
			result2 = result2
					&& (NodeType.SYSTEM_TABLE.equals(type)
							|| NodeType.SYSTEM_VIEW.equals(type)
							|| NodeType.USER_TABLE.equals(type)
							|| NodeType.USER_PARTITIONED_TABLE.equals(type)
							|| NodeType.USER_VIEW.equals(type)
							|| NodeType.USER_PARTITIONED_TABLE_FOLDER.equals(type) || NodeType.TABLE_COLUMN.equals(type)
							//multipe dbquery use under type
							|| NodeType.DATABASE.equals(type)
							|| NodeType.DATABASE_FOLDER.equals(type)
							|| NodeType.GROUP.equals(type)
							|| NodeType.SERVER.equals(type)
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
		PersistUtils.setPreferenceValue(CubridQueryUIPlugin.PLUGIN_ID,
				COM_CUBRID_QUERYBROWSER_NAVIGATOR_SHOWGROUP, String.valueOf(isShowGroup));
	}

	/**
	 * Retrieves the show group option in local configuration file.
	 *
	 * @return true:show,false:don't show.
	 */
	public boolean savedIsShowGroup() {
		String show = PersistUtils.getPreferenceValue(CubridQueryUIPlugin.PLUGIN_ID, COM_CUBRID_QUERYBROWSER_NAVIGATOR_SHOWGROUP);
		if (show == null || show.trim().length() == 0) {
			return super.savedIsShowGroup();
		} else {
			return Boolean.parseBoolean(show);
		}
	}

	/**
	 * Only database node can be draged over the tree.
	 *
	 * @param target ICubridNode
	 * @return true:support.
	 */
	protected boolean dataSupportDragOver(ICubridNode target) {
		if (!NodeType.DATABASE.equals(target.getType())
				&& !NodeType.GROUP.equals(target.getType())) {
			return false;
		}
		TreeItem[] tis = tv.getTree().getSelection();
		for (TreeItem ti : tis) {
			String type = ((ICubridNode) ti.getData()).getType();
			if (!NodeType.DATABASE.equals(type)) {
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
	 *
	 * Add listener
	 *
	 */
	protected void addConnectionListener() {
		tv.getTree().addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e) {
			}

			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.F2) {
					TreeItem[] items = tv.getTree().getSelection();
					if (items == null || items.length == 0) {
						return;
					}

					if (items.length == 1) {
						Object obj = items[0].getData();
						if (obj instanceof ICubridNode) {
							ICubridNode node = (ICubridNode) obj;
							if (NodeType.DATABASE.equals(node.getType())) {
								RenameConnectionAction renameAction = (RenameConnectionAction) ActionManager.getInstance().getAction(
										RenameConnectionAction.ID);
								renameAction.run((CubridDatabase) node);
							}
						}
					}
				}
			}
		});
	}
}
