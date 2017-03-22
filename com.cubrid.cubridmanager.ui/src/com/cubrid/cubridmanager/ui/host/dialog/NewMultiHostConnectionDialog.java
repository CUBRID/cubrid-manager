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
package com.cubrid.cubridmanager.ui.host.dialog;

import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.ui.broker.editor.internal.BrokerIntervalSettingManager;
import com.cubrid.cubridmanager.ui.common.navigator.CubridHostNavigatorView;
import com.cubrid.cubridmanager.ui.host.Messages;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;
import com.cubrid.cubridmanager.ui.spi.util.HostUtils;

public class NewMultiHostConnectionDialog extends CMTitleAreaDialog {

	private List<FailedHostServerInfo> failedServerList;
	public TableViewer serverTable = null;

	public NewMultiHostConnectionDialog(Shell parentShell, List<FailedHostServerInfo> failedServerList) {
		super(parentShell);
		this.failedServerList = failedServerList;
	}

	protected Control createDialogArea(Composite parent) {
		getShell().setText(Messages.multiConnectServerDialogTitle);
		Composite parentComp = (Composite) super.createDialogArea(parent);
		setTitle(Messages.multiConnectServerDialogTitle);
		setMessage(Messages.multiConnectServerDialogMessages);

		serverTable = new TableViewer(parentComp,  SWT.SINGLE | SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true,
				true,2,1);
		serverTable.getTable().setLayoutData(gridData);
		serverTable.getTable().setHeaderVisible(true);
		serverTable.getTable().setLinesVisible(true);

		final TableViewerColumn columnHost = new TableViewerColumn(
				serverTable, SWT.CENTER);
		columnHost.getColumn().setWidth(120);
		columnHost.getColumn().setText(Messages.multiConnectServerDialogColumnHostAddress);

		final TableViewerColumn columnUser = new TableViewerColumn(
				serverTable, SWT.CENTER);
		columnUser.getColumn().setWidth(200);
		columnUser.getColumn().setText(Messages.multiConnectServerDialogColumnUser);

		final TableViewerColumn columnErrMsg = new TableViewerColumn(
				serverTable, SWT.CENTER);
		columnErrMsg.getColumn().setWidth(200);
		columnErrMsg.getColumn().setText(Messages.multiConnectServerDialogColumnErrMsg);

		final TableViewerColumn columnStatus = new TableViewerColumn(
				serverTable, SWT.CENTER);
		columnStatus.getColumn().setWidth(100);
		columnStatus.getColumn().setText(Messages.multiConnectServerDialogColumnStatus);

		serverTable.setContentProvider(new ServerListContentProvider());
		serverTable.setLabelProvider(new ServerListLabelProvider());

		serverTable.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event
				.getSelection();
				FailedHostServerInfo multiHostConnectionFailedServerInfo = (FailedHostServerInfo) selection
				.getFirstElement();
				editHost(multiHostConnectionFailedServerInfo);
			}
		});
		serverTable.setInput(failedServerList);
		MenuManager menuManager = new MenuManager();
		Menu contextMenu = menuManager.createContextMenu(serverTable.getTable());
		serverTable.getTable().setMenu(contextMenu);

		Menu menu = new Menu(getShell(), SWT.POP_UP);

		final MenuItem itemEdit = new MenuItem(menu, SWT.PUSH);
		itemEdit.setText(Messages.msgConnectHostDialog);
		itemEdit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				IStructuredSelection selection = (StructuredSelection) serverTable.getSelection();
				FailedHostServerInfo multiHostConnectionFailedServerInfo = (FailedHostServerInfo) selection
				.getFirstElement();
				editHost(multiHostConnectionFailedServerInfo);
			}
		});
		menu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent event) {
				IStructuredSelection selection = (IStructuredSelection)serverTable.getSelection();
				FailedHostServerInfo multiHostConnectionFailedServerInfo = (FailedHostServerInfo) selection
				.getFirstElement();
				if (multiHostConnectionFailedServerInfo.getCubridServer().isConnected()) {
					itemEdit.setEnabled(false);
				} else {
					itemEdit.setEnabled(true);
				}
			}
		});

		serverTable.getTable().setMenu(menu);
		return parentComp;
	}

	public void editHost(FailedHostServerInfo multiHostConnectionFailedServerInfo) {
		// get selected dbdata
		if (multiHostConnectionFailedServerInfo == null
				|| multiHostConnectionFailedServerInfo.getCubridServer().isConnected()) {
			return;
		}
		HostDialog dialog = new HostDialog(getShell(), false, false);
		ServerInfo oldServerInfo = multiHostConnectionFailedServerInfo.getCubridServer().getServerInfo();
		dialog.setServer(multiHostConnectionFailedServerInfo.getCubridServer());
		if (dialog.open() != HostDialog.CONNECT_ID) {
			return;
		}

		ServerInfo serverInfo = dialog.getServerInfo();
		if (!serverInfo.getServerName().equals(multiHostConnectionFailedServerInfo.getCubridServer().getLabel())) {
			QueryOptions.removePref(multiHostConnectionFailedServerInfo.getCubridServer().getServerInfo());
			BrokerIntervalSettingManager.getInstance().removeAllBrokerIntervalSettingInServer(
					multiHostConnectionFailedServerInfo.getCubridServer().getLabel());
		}

		if (oldServerInfo.isConnected() && !oldServerInfo.equals(serverInfo)) {
			HostUtils.processHostDisconnected(multiHostConnectionFailedServerInfo.getCubridServer());
		}

		multiHostConnectionFailedServerInfo.getCubridServer().setId(serverInfo.getServerName());
		multiHostConnectionFailedServerInfo.getCubridServer().setLabel(serverInfo.getServerName());
		multiHostConnectionFailedServerInfo.getCubridServer().setServerInfo(serverInfo);
		multiHostConnectionFailedServerInfo.getCubridServer().setAutoSavePassword(dialog.isSavePassword());
		CMHostNodePersistManager.getInstance().addServer(serverInfo.getHostAddress(),
				serverInfo.getHostMonPort(), serverInfo.getUserName(),
				serverInfo);
		CubridNodeManager.getInstance().fireCubridNodeChanged(
				new CubridNodeChangedEvent(multiHostConnectionFailedServerInfo.getCubridServer(),
						CubridNodeChangedEventType.SERVER_CONNECTED));

		multiHostConnectionFailedServerInfo.getCubridServer().getLoader().setLoaded(false);
		CubridNavigatorView view = CubridNavigatorView.getNavigatorView(CubridHostNavigatorView.ID);
		TreeViewer treeViewer = view.getViewer();
		treeViewer.refresh(multiHostConnectionFailedServerInfo.getCubridServer(), true);
		treeViewer.expandToLevel(multiHostConnectionFailedServerInfo.getCubridServer(), 1);

		ActionManager.getInstance().fireSelectionChanged(treeViewer.getSelection());
		LayoutManager.getInstance().fireSelectionChanged(treeViewer.getSelection());

		multiHostConnectionFailedServerInfo.setErrConnectionMsg("");
		serverTable.refresh();
	}

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, Messages.multiConnectServerDialogClose, false);
	}


	/**
	 *
	 * @author fulei
	 *
	 */
	class ServerListContentProvider implements
			IStructuredContentProvider {

		/**
		 * getElements
		 *
		 * @param inputElement Object
		 * @return Object[]
		 */
		@SuppressWarnings("unchecked")
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof List) {
				List<FailedHostServerInfo> list = (List<FailedHostServerInfo>) inputElement;
				FailedHostServerInfo[] nodeArr = new FailedHostServerInfo[list.size()];
				return list.toArray(nodeArr);
			}

			return new Object[]{};
		}

		/**
		 * dispose
		 */
		public void dispose() {
			// do nothing
		}

		/**
		 * inputChanged
		 *
		 * @param viewer Viewer
		 * @param oldInput Object
		 * @param newInput Object
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// do nothing
		}

	}


	/**
	 *
	 * @author fulei
	 *
	 */
	class ServerListLabelProvider extends
			LabelProvider implements
			ITableLabelProvider {

		/**
		 * getColumnImage
		 *
		 * @param element Object
		 * @param columnIndex int
		 * @return Image
		 */
		public final Image getColumnImage(Object element, int columnIndex) {

			return null;
		}
		/**
		 * getColumnText
		 *
		 * @param element Object
		 * @param columnIndex int
		 * @return String
		 */
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof FailedHostServerInfo) {
				FailedHostServerInfo multiHostConnectionFailedServerInfo
				= (FailedHostServerInfo) element;
				if (columnIndex == 0) {
					return multiHostConnectionFailedServerInfo.getCubridServer().getName();
				} else if (columnIndex == 1) {
					return multiHostConnectionFailedServerInfo.getCubridServer().getUserName();
				} else if (columnIndex == 2) {
					if (multiHostConnectionFailedServerInfo.getErrConnectionMsg() != null) {
						return multiHostConnectionFailedServerInfo.getErrConnectionMsg().replaceAll("[\r\n]", " ");
					}
					return "";
				} else if (columnIndex == 3) {
					return multiHostConnectionFailedServerInfo.getCubridServer().isConnected()?
							Messages.multiConnectServerDialogStatusConnected : Messages.multiConnectServerDialogStatusDisonnected;
				}
			}
			return null;
		}
	}
}
