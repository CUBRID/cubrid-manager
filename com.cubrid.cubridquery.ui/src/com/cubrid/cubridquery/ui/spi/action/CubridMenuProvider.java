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
package com.cubrid.cubridquery.ui.spi.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;

import com.cubrid.common.ui.common.action.OIDNavigatorAction;
import com.cubrid.common.ui.common.action.ShowHiddenElementsAction;
import com.cubrid.common.ui.compare.data.action.DataCompareWizardAction;
import com.cubrid.common.ui.compare.schema.action.SchemaCompareWizardAction;
import com.cubrid.common.ui.cubrid.database.erwin.action.ExportERwinAction;
import com.cubrid.common.ui.cubrid.database.erwin.action.ImportERwinAction;
import com.cubrid.common.ui.cubrid.table.action.ExportTableDefinitionAction;
import com.cubrid.common.ui.cubrid.table.action.ExportWizardAction;
import com.cubrid.common.ui.cubrid.table.action.ImportWizardAction;
import com.cubrid.common.ui.cubrid.user.action.AddUserAction;
import com.cubrid.common.ui.cubrid.user.action.DeleteUserAction;
import com.cubrid.common.ui.cubrid.user.action.EditUserAction;
import com.cubrid.common.ui.query.action.DatabaseQueryNewAction;
import com.cubrid.common.ui.query.control.DatabaseNavigatorMenu;
import com.cubrid.common.ui.schemacomment.action.SchemaCommentInstallAction;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.MenuProvider;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.cubridquery.ui.common.action.PropertyAction;
import com.cubrid.cubridquery.ui.common.control.CQDatabaseNavigatorMenu;
import com.cubrid.cubridquery.ui.connection.action.NewQueryConnAction;
import com.cubrid.cubridquery.ui.connection.action.OpenQueryConnAction;
import com.cubrid.cubridquery.ui.connection.action.ViewDatabaseVersionAction;

/**
 *
 * CUBRID Query browser menu provider
 *
 * @author pangqiren
 * @version 1.0 - 2010-11-8 created by pangqiren
 */
public class CubridMenuProvider extends
		MenuProvider {
	protected final DatabaseNavigatorMenu databaseNavigatorMenu = new CQDatabaseNavigatorMenu();
	/**
	 * Build the context menu and menubar menu according to the selected cubrid
	 * node
	 *
	 * @param manager the parent menu manager
	 * @param node the ICubridNode object
	 */
	public void buildMenu(IMenuManager manager, ICubridNode node) {
		String type = node.getType();
		if (NodeType.DATABASE.equals(type)) {
			buildDatabaseMenu(manager);
		} else if (NodeType.GROUP.equals(type)) {
			addActionToManager(manager, getAction(NewQueryConnAction.ID));
			addActionToManager(manager, getAction(OpenQueryConnAction.ID));
			manager.add(new Separator());
			super.buildMenu(manager, node);
		} else if (NodeType.USER_FOLDER.equals(type)) {
			addActionToManager(manager, getAction(AddUserAction.ID));
		} else if (NodeType.USER.equals(type)) {
			addActionToManager(manager, getAction(EditUserAction.ID));
			addActionToManager(manager, getAction(DeleteUserAction.ID));
		} else {
			super.buildMenu(manager, node);
		}
		if (node.isContainer() && ShowHiddenElementsAction.isSupportedNode(node)) {
			manager.add(new Separator());
			IAction action = getAction(ShowHiddenElementsAction.ID);
			ActionManager.addActionToManager(manager, action);
		}
		manager.update(true);
	}

	/**
	 *
	 * Build the database menu
	 *
	 * @param manager the parent menu manager
	 */
	private void buildDatabaseMenu(IMenuManager manager) {
		// Query Editor & Comparing Schema
		manager.add(new Separator());
		addActionToManager(manager, getAction(DatabaseQueryNewAction.ID));
		manager.add(new Separator());
		ActionManager.addActionToManager(manager, SchemaCompareWizardAction.ID);
		ActionManager.addActionToManager(manager, DataCompareWizardAction.ID);
		manager.add(new Separator());
		ActionManager.addActionToManager(manager, ExportERwinAction.ID);
		ActionManager.addActionToManager(manager, ImportERwinAction.ID);
		// Export & Import Actions
		manager.add(new Separator());
		addActionToManager(manager, getAction(ExportWizardAction.ID));
		addActionToManager(manager, getAction(ImportWizardAction.ID));
		manager.add(new Separator());
		addActionToManager(manager, getAction(ExportTableDefinitionAction.ID));
		manager.add(new Separator());

		// Install Schema Comment
		addActionToManager(manager, getAction(SchemaCommentInstallAction.ID));
		manager.add(new Separator());

		addActionToManager(manager, getAction(ViewDatabaseVersionAction.ID));
		addActionToManager(manager, getAction(OIDNavigatorAction.ID));
		addActionToManager(manager, getAction(PropertyAction.ID));
	}

	/**
	 * @return the databaseNavigatorMenu
	 */
	public DatabaseNavigatorMenu getDatabaseNavigatorMenu() {
		return databaseNavigatorMenu;
	}


}
