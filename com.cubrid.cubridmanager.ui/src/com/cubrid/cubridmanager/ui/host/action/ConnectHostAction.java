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
package com.cubrid.cubridmanager.ui.host.action;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridGroupNode;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.cubridmanager.ui.common.navigator.CubridHostNavigatorView;
import com.cubrid.cubridmanager.ui.host.dialog.FailedHostServerInfo;
import com.cubrid.cubridmanager.ui.host.dialog.NewMultiHostConnectionDialog;
import com.cubrid.cubridmanager.ui.spi.contribution.CubridWorkbenchContrItem;

/**
 * Connect host action
 *
 * @author pangqiren
 * @version 1.0 - 2011-6-17 created by pangqiren
 */
public class ConnectHostAction extends SelectionAction {
	private static final Logger LOGGER = LogUtil.getLogger(ConnectHostAction.class);
	public static final String ID = ConnectHostAction.class.getName();

	public ConnectHostAction(Shell shell, String text, ImageDescriptor icon) {
		this(shell, null, text, icon);
	}

	public ConnectHostAction(Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
		super(shell, provider, text, icon);
		this.setId(ID);
		this.setToolTipText(text);
	}

	/**
	 * Return whether this action support to select multi object, if not support,this action will be disabled
	 *
	 * @return <code>true</code> if allow multi selection;<code>false</code> otherwise
	 */
	public boolean allowMultiSelections() {
		return true;
	}

	/**
	 * Return whether this action support this object,if not support,this action will be disabled
	 *
	 * @param obj the Object
	 * @return <code>true</code> if support this obj;<code>false</code> otherwise
	 */
	public boolean isSupported(Object obj) {
		return true;
	}

	/**
	 * Return whether this action support this object,if not support,this action will be disabled
	 *
	 * @param obj the Object
	 * @return <code>true</code> if support this obj;<code>false</code> otherwise
	 */
	public static boolean isSupportedNode(Object obj) {
		if (obj instanceof CubridServer) {
			CubridServer server = (CubridServer)obj;
			return !server.isConnected();
		} else if (obj instanceof CubridGroupNode) {
			return true;
		}
		if (obj instanceof Object[]) {
			return true;
		}

		return false;
	}

	/**
	 * Open the host dialog and connect to host
	 */
	public void run() {
		final Object[] obj = this.getSelectedObj();
		if (obj == null || obj.length <= 0) {
			setEnabled(false);
			return;
		}

		// For bug TOOLS-2644
		CubridServer[] servers = handleSelectionObj(obj);
		doRun(servers);
	}

	public void doRun(CubridServer[] servers) {
		if (servers.length == 0) {
			return;
		}

		if (servers.length == 1) {
			CubridServer server = servers[0];
			if (server.isAutoSavePassword() && CubridWorkbenchContrItem.connectHost(server.getServerInfo(), true)) {
				server.getLoader().setLoaded(false);

				CubridNavigatorView view = CubridNavigatorView.getNavigatorView(CubridHostNavigatorView.ID);
				if (view == null) {
					LOGGER.error("view is null.");
					return;
				}
				TreeViewer treeViewer = view.getViewer();
				treeViewer.refresh(server, true);
				treeViewer.expandToLevel(server, 1);

				ActionManager.getInstance().fireSelectionChanged(treeViewer.getSelection());
				LayoutManager.getInstance().fireSelectionChanged(treeViewer.getSelection());
				CubridNodeChangedEvent event = new CubridNodeChangedEvent(
						server, CubridNodeChangedEventType.SERVER_CONNECTED);
				CubridNodeManager.getInstance().fireCubridNodeChanged(event);
			} else {
				EditHostAction editHostAction = (EditHostAction)ActionManager.getInstance().getAction(EditHostAction.ID);
				if (editHostAction != null) {
					editHostAction.doRun(servers, true);
				}
			}
		} else {
			List<FailedHostServerInfo> failedServerList = new ArrayList<FailedHostServerInfo>();
			for (CubridServer object : servers) {
				CubridServer server = (CubridServer)object;
				String errMsg = null;
				if (server.isAutoSavePassword()) {
					errMsg = CubridWorkbenchContrItem.connectHostWithErrMsg(server.getServerInfo(), false);
				} else {
					errMsg = "Incorrect or missing password.";
				}

				if (errMsg == null) {
					server.getLoader().setLoaded(false);
					CubridNavigatorView view = CubridNavigatorView.getNavigatorView(CubridHostNavigatorView.ID);
					TreeViewer treeViewer = view.getViewer();
					treeViewer.refresh(server, true);
					treeViewer.expandToLevel(server, 1);

					ActionManager.getInstance().fireSelectionChanged(treeViewer.getSelection());
					LayoutManager.getInstance().fireSelectionChanged(treeViewer.getSelection());
					CubridNodeManager.getInstance().fireCubridNodeChanged(
					new CubridNodeChangedEvent(server, CubridNodeChangedEventType.SERVER_CONNECTED));
				} else {
					failedServerList.add(new FailedHostServerInfo(server,errMsg));
				}
			}

			if (failedServerList.size() > 0) {
				NewMultiHostConnectionDialog dialog = new NewMultiHostConnectionDialog(getShell(), failedServerList);
				dialog.open();
			}
		}
	}

	private CubridServer[] handleSelectionObj(Object[] objs) {
		Set<CubridServer> list = new LinkedHashSet<CubridServer>();
		for (Object obj : objs) {
			if (obj instanceof CubridServer && !((CubridServer) obj).isConnected()) {
				list.add((CubridServer) obj);
			} else if (obj instanceof CubridGroupNode) {
				CubridGroupNode node = (CubridGroupNode) obj;
				for (ICubridNode childNode : node.getChildren()) {
					if (childNode instanceof CubridServer && !((CubridServer) childNode).isConnected()) {
						list.add((CubridServer) childNode);
					}
				}
			}
		}

		return list.toArray(new CubridServer[0]);
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
	}
}
