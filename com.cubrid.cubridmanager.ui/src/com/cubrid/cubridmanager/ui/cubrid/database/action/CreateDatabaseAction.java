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
package com.cubrid.cubridmanager.ui.cubrid.database.action;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.dialog.CMWizardDialog;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ICubridNodeLoader;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DbCreateAuthType;
import com.cubrid.cubridmanager.ui.cubrid.database.control.CreateDatabaseWizard;
import com.cubrid.cubridmanager.ui.spi.model.loader.CubridServerLoader;

/**
 * This action is responsible to create database
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class CreateDatabaseAction extends SelectionAction {
	public static final String ID = CreateDatabaseAction.class.getName();
	private TreeViewer viewer;
	private ICubridNode node;
	private CubridServer server;

	public CreateDatabaseAction(Shell shell, String text,
			ImageDescriptor enabledIcon, ImageDescriptor disabledIcon) {
		this(shell, null, text, enabledIcon, disabledIcon);
	}

	public CreateDatabaseAction(Shell shell, ISelectionProvider provider,
			String text, ImageDescriptor enabledIcon,
			ImageDescriptor disabledIcon) {
		super(shell, provider, text, enabledIcon);
		this.setId(ID);
		this.setToolTipText(text);
		this.setDisabledImageDescriptor(disabledIcon);
	}

	public boolean allowMultiSelections() {
		return false;
	}

	public boolean isSupported(Object obj) {
		if (obj instanceof ICubridNode) {
			ICubridNode node = (ICubridNode) obj;
			if (node.getServer() == null) {
				return false;
			}
			ServerUserInfo userInfo = node.getServer().getServerInfo().getLoginedUserInfo();
			if (userInfo != null && userInfo.getDbCreateAuthType() == DbCreateAuthType.AUTH_ADMIN) {
				return true;
			}
		}
		return false;
	}

	public void run() {
		Object[] obj = this.getSelectedObj();
		if (obj != null && obj.length > 0) {
			if (!isSupported(obj[0])) {
				this.setEnabled(false);
				return;
			}
			node = (ICubridNode) obj[0];
			server = node.getServer();
			ISelectionProvider provider = this.getSelectionProvider();
			viewer = (TreeViewer) provider;
		}

		String serverNameKey = node.getServer().getId() + ICubridNodeLoader.NODE_SEPARATOR + CubridServerLoader.DATABASE_FOLDER_ID;
		ICubridNode cubridServer = node.getServer().getChild(serverNameKey);
		CMWizardDialog dialog = new CMWizardDialog(getShell(), new CreateDatabaseWizard(server, viewer, cubridServer));
		dialog.setPageSize(580, 460);
		dialog.open();
	}
}
