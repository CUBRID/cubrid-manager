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

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
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
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.ui.broker.editor.internal.BrokerIntervalSettingManager;
import com.cubrid.cubridmanager.ui.common.navigator.CubridHostNavigatorView;
import com.cubrid.cubridmanager.ui.host.dialog.HostDialog;
import com.cubrid.cubridmanager.ui.host.dialog.MultiHostEditDialog;
import com.cubrid.cubridmanager.ui.spi.contribution.CubridWorkbenchContrItem;
import com.cubrid.cubridmanager.ui.spi.persist.CMGroupNodePersistManager;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;
import com.cubrid.cubridmanager.ui.spi.util.HostUtils;

/**
 * 
 * This action is responsible to connect host
 * 
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class EditHostAction extends SelectionAction {
	private static final Logger LOGGER = LogUtil.getLogger(EditHostAction.class);
	public static final String ID = EditHostAction.class.getName();

	/**
	 * The constructor
	 * 
	 * @param shell
	 * @param text
	 * @param icon
	 */
	public EditHostAction(Shell shell, String text, ImageDescriptor icon) {
		this(shell, null, text, icon);
	}

	/**
	 * The constructor
	 * 
	 * @param shell
	 * @param provider
	 * @param text
	 * @param icon
	 */
	public EditHostAction(Shell shell, ISelectionProvider provider,
			String text, ImageDescriptor icon) {
		super(shell, provider, text, icon);
		this.setId(ID);
		this.setToolTipText(text);
	}

	/**
	 * 
	 * Return whether this action support to select multi object,if not
	 * support,this action will be disabled
	 * 
	 * @return <code>true</code> if allow multi selection;<code>false</code>
	 *         otherwise
	 */
	public boolean allowMultiSelections() {
		return true;
	}

	/**
	 * 
	 * Return whether this action support this object,if not support,this action
	 * will be disabled
	 * 
	 * @param obj the Object
	 * @return <code>true</code> if support this obj;<code>false</code>
	 *         otherwise
	 */
	public boolean isSupported(Object obj) {
		if (obj instanceof CubridServer || obj instanceof Object[]) {
			return true;
		}
		return false;
	}

	private CubridServer[] handleSelectionObj(Object[] objs) {
		Set<CubridServer> list = new LinkedHashSet<CubridServer>();
		for (Object obj : objs) {
			if (obj instanceof CubridServer) {
				list.add((CubridServer) obj);
			} else if (obj instanceof CubridGroupNode) {
				CubridGroupNode node = (CubridGroupNode) obj;
				for (ICubridNode icNode : node.getChildren()) {
					if (icNode instanceof CubridServer) {
						if (!((CubridServer) icNode).isConnected()) {
							list.add((CubridServer) icNode);
						}
					}
				}
			}
		}

		return list.toArray(new CubridServer[0]);
	}

	/**
	 * Open the host dialog and connect to host
	 */
	public void run() {
		final Object[] obj = this.getSelectedObj();
		CubridServer[] servers = handleSelectionObj(obj);
		if (servers.length <= 0) {
			setEnabled(false);
			return;
		}

		doRun(servers, false);
	}

	/**
	 * Perform do run
	 * 
	 * @param obj
	 */
	public void doRun(CubridServer[] servers, boolean actionIsConnect) {
		//multi edit
		if (servers.length > 1) {
			MultiHostEditDialog dialog = new MultiHostEditDialog(getShell(),
					Arrays.asList(servers));
			if (dialog.open() != MultiHostEditDialog.SAVE_ID) {
				return;
			}

			BrokerIntervalSettingManager brokerManager = BrokerIntervalSettingManager.getInstance();

			//new server infolist
			List<CubridServer> newServerInfoList = dialog.getNewServerList();
			for (int i = 0; i < servers.length; i++) {
				CubridServer newServer = newServerInfoList.get(i);
				CubridServer server = servers[i];
				if (server == null) {
					continue;
				}

				// if the server is not changed, ignore it.
				if (!checkServerChanges(newServer, server)) {
					continue;
				}

				ServerInfo oldServerInfo = server.getServerInfo();
				if (oldServerInfo == null) {
					LOGGER.error("oldServerInfo is null.");
					continue;
				}

				ServerInfo newServerInfo = newServer.getServerInfo();
				if (newServerInfo == null) {
					LOGGER.error("newServerInfo is null.");
					continue;
				}
				
				if (!newServerInfo.getServerName().equals(server.getLabel())) {
					QueryOptions.removePref(server.getServerInfo());
					brokerManager.removeAllBrokerIntervalSettingInServer(server.getLabel());
				}

				if (oldServerInfo.isConnected()) {
					HostUtils.processHostDisconnected(server);
				}

				server.setAutoSavePassword(newServer.isAutoSavePassword());
				server.getServerInfo().setServerName(newServerInfo.getServerName());
				server.getServerInfo().setHostAddress(newServerInfo.getHostAddress());
				server.getServerInfo().setHostMonPort(newServerInfo.getHostMonPort());
				server.getServerInfo().setJdbcDriverVersion(newServerInfo.getJdbcDriverVersion());
				server.getServerInfo().setUserName(newServerInfo.getUserName());
				if (newServer.isAutoSavePassword()) {
					server.getServerInfo().setUserPassword(newServerInfo.getUserPassword());
				} else {
					server.getServerInfo().setUserPassword(null);
				}

				server.setId(newServerInfo.getServerName());
				server.setLabel(newServerInfo.getServerName());
				CMHostNodePersistManager.getInstance().addServer(
						newServerInfo.getHostAddress(),
						newServerInfo.getHostMonPort(),
						newServerInfo.getUserName(), newServerInfo);

				CMGroupNodePersistManager.getInstance().saveAllGroupNode();
				CMHostNodePersistManager.getInstance().saveServers();

				if (server.getLoader() != null) {
					server.getLoader().setLoaded(false);
				}

				CubridWorkbenchContrItem.closeAllEditorAndViewInServer(server, false);
				CubridNavigatorView navigatorView = CubridNavigatorView.getNavigatorView(CubridHostNavigatorView.ID);
				if (navigatorView != null) {
					navigatorView.getViewer().refresh(server, true);
				}

				CubridNodeManager.getInstance().fireCubridNodeChanged(
						new CubridNodeChangedEvent(server, CubridNodeChangedEventType.SERVER_DISCONNECTED));
			}

			return;
		}

		// single edit
		HostDialog dialog = new HostDialog(getShell(), false, actionIsConnect);
		CubridServer server = null;
		try {
			server = servers[0];
			//When edit connection but haven't read the hostgroup from preference
			//it will lose the group info ,so if the server parent is empty
			//read it from hostgroup preference first
			if (server.getParent() == null) {
				CMGroupNodePersistManager.getInstance();
			}
		} catch (ClassCastException cce) {
			CubridGroupNode node = (CubridGroupNode) getSelectedObj()[0];
			for (ICubridNode icNode : node.getChildren()) {
				if (icNode instanceof CubridServer) {
					if (!((CubridServer) icNode).isConnected())
						server = (CubridServer) icNode;
				}
			}
		}

		ServerInfo oldServerInfo = server.getServerInfo();
		if (oldServerInfo == null) {
			LOGGER.error("oldServerInfo is null.");
			return;
		}

		dialog.setServer(server);
		int returnCode = dialog.open();
		if (returnCode != HostDialog.SAVE_ID && returnCode != HostDialog.CONNECT_ID) {
			return;
		}

		// Save the server info
		ServerInfo serverInfo = dialog.getServerInfo();
		if (serverInfo == null) {
			LOGGER.error("serverInfo is null.");
			return;
		}

		if (!serverInfo.getServerName().equals(server.getLabel())) {
			QueryOptions.removePref(server.getServerInfo());
			BrokerIntervalSettingManager.getInstance().removeAllBrokerIntervalSettingInServer(
					server.getLabel());
		}

		if (oldServerInfo.isConnected() && !oldServerInfo.equals(serverInfo)) {
			HostUtils.processHostDisconnected(server);
		}

		server.setId(serverInfo.getServerName());
		server.setLabel(serverInfo.getServerName());
		server.setServerInfo(serverInfo);
		server.setAutoSavePassword(dialog.isSavePassword());
		CMHostNodePersistManager.getInstance().addServer(serverInfo.getHostAddress(),
				serverInfo.getHostMonPort(), serverInfo.getUserName(),
				serverInfo);

		// Refresh the tree node
		if (returnCode == HostDialog.CONNECT_ID) {
			ISelectionProvider provider = getSelectionProvider();
			if (provider instanceof TreeViewer) {
				server.removeAllChild();
				if (server.getLoader() != null) {
					server.getLoader().setLoaded(false);
				}
				TreeViewer treeViewer = (TreeViewer) provider;
				treeViewer.refresh(server, true);
				treeViewer.expandToLevel(server, 1);
				treeViewer.setSelection(null, true);
				treeViewer.setSelection(new StructuredSelection(server), true);
				setEnabled(false);
			}
		}

		CMGroupNodePersistManager.getInstance().saveAllGroupNode();
		CMHostNodePersistManager.getInstance().saveServers();

		ActionManager.getInstance().fireSelectionChanged(getSelection());
		LayoutManager.getInstance().fireSelectionChanged(getSelection());
		CubridNodeManager.getInstance().fireCubridNodeChanged(
				new CubridNodeChangedEvent(server, CubridNodeChangedEventType.SERVER_CONNECTED));
	}

	/**
	 * Check whether the two server changes
	 * 
	 * @param server1
	 * @param server2
	 * @return
	 */
	public boolean checkServerChanges(CubridServer server1, CubridServer server2) {
		if (server1 == null || server2 == null) {
			LOGGER.error("server1={}, server2={}", server1, server2);
			return false;
		}

		ServerInfo serverInfo1 = server1.getServerInfo();
		ServerInfo serverInfo2 = server2.getServerInfo();
		if (serverInfo1 == null || serverInfo2 == null) {
			LOGGER.error("serverInfo1={}, serverInfo2={}", serverInfo1, serverInfo2);
			return false;
		}

		boolean equalServerName  = StringUtil.isEqual(serverInfo1.getServerName(), serverInfo2.getServerName());
		boolean equalHostAddress = StringUtil.isEqual(serverInfo1.getHostAddress(), serverInfo2.getHostAddress());
		boolean equalHostPort    = serverInfo1.getHostMonPort() == serverInfo2.getHostMonPort();
		boolean equalJdbcVersion = StringUtil.isEqual(serverInfo1.getJdbcDriverVersion(), serverInfo2.getJdbcDriverVersion());
		boolean equalUserName    = StringUtil.isEqual(serverInfo1.getUserName(), serverInfo2.getUserName());
		boolean equalPassword    = StringUtil.isEqual(serverInfo2.getUserPassword(), serverInfo1.getUserPassword());
		boolean equalAutoSave    = server1.isAutoSavePassword() == server2.isAutoSavePassword();
		return !(equalServerName && equalHostAddress && equalHostPort && equalJdbcVersion && equalUserName && equalPassword && equalAutoSave);
	}
}
