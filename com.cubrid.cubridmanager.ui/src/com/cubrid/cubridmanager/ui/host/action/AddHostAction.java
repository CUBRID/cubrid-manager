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
package com.cubrid.cubridmanager.ui.host.action;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridGroupNode;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.ui.common.navigator.CubridHostNavigatorView;
import com.cubrid.cubridmanager.ui.host.dialog.HostDialog;
import com.cubrid.cubridmanager.ui.spi.model.loader.CubridServerLoader;
import com.cubrid.cubridmanager.ui.spi.persist.CMGroupNodePersistManager;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;

/**
 * 
 * This action is responsible to add host to navigator
 * 
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class AddHostAction extends SelectionAction {
	private static final Logger LOGGER = LogUtil.getLogger(AddHostAction.class);
	public static final String ID = AddHostAction.class.getName();
	public static final String ID_BIG = AddHostAction.class.getName()+"Big";

	public AddHostAction(Shell shell, String text, ImageDescriptor enabledIcon,
			ImageDescriptor disabledIcon, boolean bigButton) {
		this(shell, null, text, enabledIcon, disabledIcon, bigButton);
	}

	public AddHostAction(Shell shell, ISelectionProvider provider, String text,
			ImageDescriptor enabledIcon, ImageDescriptor disabledIcon, boolean bigButton) {
		super(shell, provider, text, enabledIcon);
		this.setId(bigButton ? ID_BIG : ID);
		this.setToolTipText(text);
		this.setDisabledImageDescriptor(disabledIcon);
	}

	public boolean allowMultiSelections() {
		return true;
	}

	public boolean isSupported(Object obj) {
		return true;
	}

	public void run() {
		
		CubridNavigatorView navigatorView = CubridNavigatorView.getNavigatorView(CubridHostNavigatorView.ID);
		TreeViewer treeViewer = navigatorView == null ? null : navigatorView.getViewer();
		if (treeViewer == null) {
			return;
		}
		
		doRun(this.getSelectedObj());
	}
	
	public void doRun(Object[] nodes) {
		CubridGroupNode parent = getParentGroupNode(nodes);
		if (parent == null) {
			LOGGER.error("parent is null.");
			return;
		}

		HostDialog dialog = new HostDialog(getShell(), true, false);
		int returnCode = dialog.open();
		if (returnCode == HostDialog.ADD_ID || returnCode == HostDialog.CONNECT_ID) {
			CubridServer server = getServerNode(dialog);
			if (server == null) {
				LOGGER.error("server is null.");
				return;
			}
			CMHostNodePersistManager.getInstance().addServer(server);

			TreeItem item = null;
			parent.addChild(server);
			CubridNavigatorView navigatorView = CubridNavigatorView.getNavigatorView(CubridHostNavigatorView.ID);
			if (navigatorView != null) {
				boolean isShowGroup = navigatorView.isShowGroup();
				TreeViewer treeViewer = navigatorView.getViewer();
				if (isShowGroup) {
					item = new TreeItem(
							navigatorView.getTreeItemByData(parent), SWT.NONE);
				} else {
					item = new TreeItem(treeViewer.getTree(), SWT.NONE);
				}
				CMGroupNodePersistManager.getInstance().saveAllGroupNode();
				item.setText(server.getLabel());
				item.setData(server);
				treeViewer.refresh(server, true);
				treeViewer.expandToLevel(server, 1);
				treeViewer.setSelection(new StructuredSelection(server), true);

				if (returnCode == HostDialog.CONNECT_ID) {
					CubridNodeManager.getInstance().fireCubridNodeChanged(
							new CubridNodeChangedEvent(server,
									CubridNodeChangedEventType.SERVER_CONNECTED));
				}
			} else {
				dialog.closeTestServerConnection();
			}
		}
	}

	/**
	 * Get the parent group node if it is show group mode.
	 * 
	 * @return the parent group node.
	 */
	private CubridGroupNode getParentGroupNode(Object[] nodes) {
		Object[] selections = nodes;
		if ((selections.length > 0)
				&& (selections[0] instanceof CubridGroupNode)) {
			return (CubridGroupNode) selections[0];
		}
		return CMGroupNodePersistManager.getInstance().getDefaultGroup();
	}

	/**
	 * Create a new server node.
	 * 
	 * @param dialog the add server dialog.
	 * @return CubridServer.
	 */
	private CubridServer getServerNode(HostDialog dialog) {
		ServerInfo serverInfo = dialog.getServerInfo();
		CubridServer server = new CubridServer(serverInfo.getServerName(),
				serverInfo.getServerName(), "icons/navigator/host.png",
				"icons/navigator/host_connected.png");
		server.setServerInfo(serverInfo);
		server.setLoader(new CubridServerLoader());
		server.setAutoSavePassword(dialog.isSavePassword());
		return server;
	}
}
