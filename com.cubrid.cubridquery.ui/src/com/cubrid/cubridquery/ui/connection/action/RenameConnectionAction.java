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
package com.cubrid.cubridquery.ui.connection.action;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
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
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.ui.spi.persist.CQBDBNodePersistManager;
import com.cubrid.cubridmanager.ui.spi.persist.CQBGroupNodePersistManager;
import com.cubrid.cubridquery.ui.common.navigator.CubridQueryNavigatorView;
import com.cubrid.cubridquery.ui.connection.dialog.RenameConnectionDialog;

/**
 * Rename Connection Action
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-1-16 created by Kevin.Wang
 */
public class RenameConnectionAction extends
		SelectionAction {
	private static final Logger LOGGER = LogUtil.getLogger(RenameConnectionAction.class);
	public static final String ID = RenameConnectionAction.class.getName();

	/**
	 * The constructor
	 *
	 * @param shell
	 * @param text
	 * @param icon
	 */
	public RenameConnectionAction(Shell shell, String text, ImageDescriptor icon) {
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
	public RenameConnectionAction(Shell shell, ISelectionProvider provider,
			String text, ImageDescriptor icon) {
		super(shell, provider, text, icon);
		this.setId(ID);
	}

	/**
	 * Sets this action support to select multi-object
	 *
	 * @see org.eclipse.jface.action.IAction.ISelectionAction
	 * @return boolean
	 */
	public boolean allowMultiSelections() {
		return false;
	}

	/**
	 * Sets this action support this object
	 *
	 * @see org.eclipse.jface.action.IAction.ISelectionAction
	 * @param obj Object
	 * @return boolean
	 */
	public boolean isSupported(Object obj) {
		return ActionSupportUtil.isSupportSingleSelection(obj,
				new String[]{NodeType.DATABASE });
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		Object[] obj = this.getSelectedObj();
		if (!isSupported(obj)) {
			setEnabled(false);
			return;
		}

		CubridDatabase database = (CubridDatabase) obj[0];

		doRun(database);
	}

	/**
	 * Run
	 *
	 * @param server
	 */
	public void run(CubridDatabase database) {
		doRun(database);
	}

	/**
	 * Perform rename Table
	 *
	 * @param cubridDatabase
	 * @param table
	 */
	private void doRun(CubridDatabase database) {

		RenameConnectionDialog dlg = new RenameConnectionDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				database);
		int ret = dlg.open();
		if (ret == IDialogConstants.OK_ID) {
			/*Fire the database logout*/
			try {
				CubridDatabase orignDatabase = database.clone();
				CubridNodeManager.getInstance().fireCubridNodeChanged(
						new CubridNodeChangedEvent(orignDatabase,
								CubridNodeChangedEventType.DATABASE_LOGOUT));
			} catch (CloneNotSupportedException e) {
				LOGGER.error(e.getMessage());
			}

			database.setLabel(dlg.getNewName());
			database.setUniqueName(dlg.getNewName());
			database.setLogined(false);

			CubridNavigatorView navigatorView = CubridNavigatorView.getNavigatorView(CubridQueryNavigatorView.ID);
			TreeViewer treeViewer = navigatorView == null ? null
					: navigatorView.getViewer();
			if (treeViewer == null) {
				LOGGER.error("Error: Can't find the navigator view:"
						+ CubridQueryNavigatorView.ID);
				return;
			}
			// Refresh the tree view
			database.removeAllChild();
			treeViewer.refresh(database, true);
			treeViewer.expandToLevel(database, 1);
			setEnabled(false);

			// Save the data
			ServerInfo preServerInfo = (database == null || database.getServer() == null) ? null
					: database.getServer().getServerInfo();
			QueryOptions.removePref(preServerInfo);
			CQBGroupNodePersistManager.getInstance().saveAllGroupNode();
			CQBDBNodePersistManager.getInstance().saveDatabases();

			ActionManager.getInstance().fireSelectionChanged(getSelection());
			LayoutManager.getInstance().fireSelectionChanged(getSelection());

		}
	}
}
