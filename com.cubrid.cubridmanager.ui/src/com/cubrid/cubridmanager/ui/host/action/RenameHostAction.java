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
package com.cubrid.cubridmanager.ui.host.action;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.ui.broker.editor.internal.BrokerIntervalSettingManager;
import com.cubrid.cubridmanager.ui.common.navigator.CubridHostNavigatorView;
import com.cubrid.cubridmanager.ui.host.dialog.RenameHostDialog;
import com.cubrid.cubridmanager.ui.spi.persist.CMGroupNodePersistManager;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;

/**
 * RenameHostAction Description
 * 
 * @author Kevin.Wang
 * @version 1.0 - 2013-1-15 created by Kevin.Wang
 */
public class RenameHostAction extends SelectionAction {
	private static final Logger LOGGER = LogUtil.getLogger(RenameHostAction.class);
	public static final String ID = RenameHostAction.class.getName();

	public RenameHostAction(Shell shell, String text, ImageDescriptor icon) {
		this(shell, null, text, icon);
	}

	public RenameHostAction(Shell shell, ISelectionProvider provider,
			String text, ImageDescriptor icon) {
		super(shell, provider, text, icon);
		this.setId(ID);
	}

	public boolean allowMultiSelections() {
		return false;
	}

	public boolean isSupported(Object obj) {
		return ActionSupportUtil.isSupportSingleSelection(obj,
				new String[]{NodeType.SERVER });
	}

	public void run() {
		Object[] obj = this.getSelectedObj();
		if (!isSupported(obj)) {
			setEnabled(false);
			return;
		}

		CubridServer server = (CubridServer) obj[0];

		doRun(server);
	}

	public void run(CubridServer server) {
		doRun(server);
	}

	private void doRun(CubridServer server) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		RenameHostDialog dlg = new RenameHostDialog(shell, server);
		int ret = dlg.open();
		if (ret == IDialogConstants.OK_ID) {
			server.setLabel(dlg.getNewName());
			server.getServerInfo().setServerName(dlg.getNewName());
			CubridNavigatorView navigatorView = CubridNavigatorView.getNavigatorView(CubridHostNavigatorView.ID);
			TreeViewer treeViewer = navigatorView == null ? null : navigatorView.getViewer();
			if (treeViewer == null) {
				LOGGER.error("Error: Can't find the navigator view:" + CubridHostNavigatorView.ID);
				return;
			}

			// Fire all the databases which are in the server logout
			List<ICubridNode> children = server.getChildren();
			if (children != null) {
				for (ICubridNode child : children) {
					if (!NodeType.DATABASE_FOLDER.equals(child.getType())) {
						continue;
					}

					List<ICubridNode> childList = child.getChildren();
					if (childList == null) {
						continue;
					}

					for (ICubridNode node : childList) {
						if (!NodeType.DATABASE.equals(node.getType())) {
							continue;
						}

						CubridDatabase database = (CubridDatabase) node;
						database.setLogined(false);
						CubridNodeManager.getInstance().fireCubridNodeChanged(
								new CubridNodeChangedEvent(database, CubridNodeChangedEventType.DATABASE_LOGOUT));
					}
				}
			}

			// Refresh the tree view
			server.removeAllChild();
			if (server.getLoader() != null) {
				server.getLoader().setLoaded(false);
			}
			treeViewer.refresh(server, true);
			treeViewer.expandToLevel(server, 1);
			treeViewer.setSelection(null, true);
			treeViewer.setSelection(new StructuredSelection(server), true);
			setEnabled(false);

			// Save the data
			CMHostNodePersistManager.getInstance().addServer(
					server.getServerInfo().getHostAddress(),
					server.getServerInfo().getHostMonPort(),
					server.getServerInfo().getUserName(),
					server.getServerInfo());
			QueryOptions.removePref(server.getServerInfo());
			BrokerIntervalSettingManager.getInstance().removeAllBrokerIntervalSettingInServer(server.getLabel());
			CMGroupNodePersistManager.getInstance().saveAllGroupNode();
			CMHostNodePersistManager.getInstance().saveServers();

			ActionManager.getInstance().fireSelectionChanged(getSelection());
			LayoutManager.getInstance().fireSelectionChanged(getSelection());
			CubridNodeManager.getInstance().fireCubridNodeChanged(
					new CubridNodeChangedEvent(server, CubridNodeChangedEventType.SERVER_DISCONNECTED));
		}
	}
}
