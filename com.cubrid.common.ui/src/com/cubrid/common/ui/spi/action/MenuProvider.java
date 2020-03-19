/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.spi.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.Util;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.action.GroupPropertyAction;
import com.cubrid.common.ui.cubrid.procedure.action.AddFunctionAction;
import com.cubrid.common.ui.cubrid.procedure.action.AddProcedureAction;
import com.cubrid.common.ui.cubrid.procedure.action.DeleteFunctionAction;
import com.cubrid.common.ui.cubrid.procedure.action.DeleteProcedureAction;
import com.cubrid.common.ui.cubrid.procedure.action.EditFunctionAction;
import com.cubrid.common.ui.cubrid.procedure.action.EditProcedureAction;
import com.cubrid.common.ui.cubrid.serial.action.CreateSerialAction;
import com.cubrid.common.ui.cubrid.serial.action.DeleteSerialAction;
import com.cubrid.common.ui.cubrid.serial.action.EditSerialAction;
import com.cubrid.common.ui.cubrid.table.action.ColumnSelectCountAction;
import com.cubrid.common.ui.cubrid.table.action.ColumnSelectSqlAction;
import com.cubrid.common.ui.cubrid.table.action.CreateLikeTableAction;
import com.cubrid.common.ui.cubrid.table.action.CreateViewAction;
import com.cubrid.common.ui.cubrid.table.action.DeleteTableAction;
import com.cubrid.common.ui.cubrid.table.action.DropTableAction;
import com.cubrid.common.ui.cubrid.table.action.DropViewAction;
import com.cubrid.common.ui.cubrid.table.action.EditTableAction;
import com.cubrid.common.ui.cubrid.table.action.EditViewAction;
import com.cubrid.common.ui.cubrid.table.action.ExportTableDefinitionAction;
import com.cubrid.common.ui.cubrid.table.action.ExportWizardAction;
import com.cubrid.common.ui.cubrid.table.action.ImportDataFromFileAction;
import com.cubrid.common.ui.cubrid.table.action.ImportWizardAction;
import com.cubrid.common.ui.cubrid.table.action.InsertOneByPstmtAction;
import com.cubrid.common.ui.cubrid.table.action.NewTableAction;
import com.cubrid.common.ui.cubrid.table.action.PropertyViewAction;
import com.cubrid.common.ui.cubrid.table.action.PstmtMultiDataAction;
import com.cubrid.common.ui.cubrid.table.action.PstmtOneDataAction;
import com.cubrid.common.ui.cubrid.table.action.RenameTableAction;
import com.cubrid.common.ui.cubrid.table.action.SelectByMultiPstmtDataAction;
import com.cubrid.common.ui.cubrid.table.action.SelectByOnePstmtDataAction;
import com.cubrid.common.ui.cubrid.table.action.ShowSchemaEditorAction;
import com.cubrid.common.ui.cubrid.table.action.TableSelectAllAction;
import com.cubrid.common.ui.cubrid.table.action.TableSelectCountAction;
import com.cubrid.common.ui.cubrid.table.action.TableToJavaCodeAction;
import com.cubrid.common.ui.cubrid.table.action.TableToPhpCodeAction;
import com.cubrid.common.ui.cubrid.table.action.TruncateTableAction;
import com.cubrid.common.ui.cubrid.table.action.UpdateStatisticsAction;
import com.cubrid.common.ui.cubrid.table.action.makequery.MakeCloneQueryAction;
import com.cubrid.common.ui.cubrid.table.action.makequery.MakeCreateQueryAction;
import com.cubrid.common.ui.cubrid.table.action.makequery.MakeDeleteQueryAction;
import com.cubrid.common.ui.cubrid.table.action.makequery.MakeInsertQueryAction;
import com.cubrid.common.ui.cubrid.table.action.makequery.MakeSelectPstmtQueryAction;
import com.cubrid.common.ui.cubrid.table.action.makequery.MakeSelectQueryAction;
import com.cubrid.common.ui.cubrid.table.action.makequery.MakeUpdateQueryAction;
import com.cubrid.common.ui.cubrid.trigger.action.AlterTriggerAction;
import com.cubrid.common.ui.cubrid.trigger.action.DropTriggerAction;
import com.cubrid.common.ui.cubrid.trigger.action.NewTriggerAction;
import com.cubrid.common.ui.query.action.DatabaseQueryNewAction;
import com.cubrid.common.ui.query.control.DatabaseNavigatorMenu;
import com.cubrid.common.ui.schemacomment.action.SchemaCommentInstallAction;
import com.cubrid.common.ui.spi.Messages;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;

/**
 * This menu provider provide the context menu and Action menu of menu bar
 * according to the selected object
 *
 * @author pangqiren
 * @version 1.0 - 2010-1-7 created by pangqiren
 */
public class MenuProvider implements
		IMenuProvider {

	/**
	 * Build the context menu and menubar menu according to the selected cubrid
	 * node
	 *
	 * @param manager the parent menu manager
	 * @param node the ICubridNode object
	 */
	public void buildMenu(IMenuManager manager, ICubridNode node) {
		// fill Action Menu according to node type
		String type = node.getType();
		if (NodeType.STORED_PROCEDURE_FOLDER.equals(type)) {
			addActionToManager(manager, getAction(AddFunctionAction.ID));
			addActionToManager(manager, getAction(AddProcedureAction.ID));
		} else if (NodeType.STORED_PROCEDURE_FUNCTION_FOLDER.equals(type)) {
			addActionToManager(manager, getAction(AddFunctionAction.ID));
		} else if (NodeType.STORED_PROCEDURE_FUNCTION.equals(type)) {
			addActionToManager(manager, getAction(EditFunctionAction.ID));
			addActionToManager(manager, getAction(DeleteFunctionAction.ID));
		} else if (NodeType.STORED_PROCEDURE_PROCEDURE_FOLDER.equals(type)) {
			addActionToManager(manager, getAction(AddProcedureAction.ID));
		} else if (NodeType.STORED_PROCEDURE_PROCEDURE.equals(type)) {
			addActionToManager(manager, getAction(EditProcedureAction.ID));
			addActionToManager(manager, getAction(DeleteProcedureAction.ID));
		} else if (NodeType.TRIGGER_FOLDER.equals(type)) { // trigger
			addActionToManager(manager, getAction(NewTriggerAction.ID));
		} else if (NodeType.TRIGGER.equals(type)) { // trigger instance
			addActionToManager(manager, getAction(AlterTriggerAction.ID));
			addActionToManager(manager, getAction(DropTriggerAction.ID));
		} else if (NodeType.SERIAL_FOLDER.equals(type)) {
			addActionToManager(manager, getAction(CreateSerialAction.ID));
		} else if (NodeType.SERIAL.equals(type)) {
			addActionToManager(manager, getAction(EditSerialAction.ID));
			addActionToManager(manager, getAction(DeleteSerialAction.ID));
		} else if (NodeType.SYSTEM_TABLE.equals(type)) {
			buildSystemTableMenu(manager);
		} else if (NodeType.SYSTEM_VIEW.equals(type)) {
			buildSystemViewMenu(manager);
		} else if (NodeType.TABLE_FOLDER.equals(type)) {
			addActionToManager(manager, getAction(NewTableAction.ID));
			manager.add(new Separator());

			addActionToManager(manager, getAction(ExportTableDefinitionAction.ID));
			manager.add(new Separator());

			// Install Schema Comment
			if (node instanceof DefaultSchemaNode
					&& !CompatibleUtil.isCommentSupports(((DefaultSchemaNode) node).getDatabase().getDatabaseInfo())) {
				addActionToManager(manager, getAction(SchemaCommentInstallAction.ID));
				manager.add(new Separator());
			}

			if (!Util.isWindows()) {
				IMenuManager perparedMenu = new MenuManager(Messages.preparedTableDataMenuName);
				manager.add(perparedMenu);
				addActionToManager(perparedMenu, getAction(PstmtOneDataAction.ID));
				addActionToManager(perparedMenu, getAction(PstmtMultiDataAction.ID));
				manager.add(new Separator());
			}
			// Export & Import Actions
			addActionToManager(manager, getAction(ExportWizardAction.ID));
			addActionToManager(manager, getAction(ImportWizardAction.ID));
			manager.add(new Separator());
		} else if (NodeType.USER_PARTITIONED_TABLE_FOLDER.equals(type)) { // partition table
			buildUserTableMenu(manager, node);
		} else if (NodeType.USER_PARTITIONED_TABLE.equals(type)) { // partition table/subtable
			buildPartitionedTableMenu(manager);
		} else if (NodeType.USER_TABLE.equals(type)) {
			buildUserTableMenu(manager, node);
		} else if (NodeType.USER_VIEW.equals(type)) { // User schema/View instance
			buildUserViewMenu(manager);
		} else if (NodeType.VIEW_FOLDER.equals(type)) {
			addActionToManager(manager, getAction(CreateViewAction.ID));
		} else if (NodeType.TABLE_COLUMN.equals(type)) {
			if (!Util.isWindows()) {
				addActionToManager(manager, getAction(ColumnSelectSqlAction.ID));
				addActionToManager(manager, getAction(ColumnSelectCountAction.ID));
			}
		} else if (NodeType.GROUP.equals(type)) {
			addActionToManager(manager, getAction(GroupPropertyAction.ID));
			manager.add(new Separator());
		}

		manager.update(true);
	}

	/**
	 * Build the partitioned table menu
	 *
	 * @param manager IMenuManager
	 */
	public void buildPartitionedTableMenu(IMenuManager manager) {
		addActionToManager(manager, getAction(TableSelectAllAction.ID));
		addActionToManager(manager, getAction(TableSelectCountAction.ID));
		manager.add(new Separator());
		addActionToManager(manager, getAction(UpdateStatisticsAction.ID));
		manager.add(new Separator());
	}

	/**
	 *
	 * Build the system view menu
	 *
	 * @param manager IMenuManager
	 */
	public void buildSystemViewMenu(IMenuManager manager) {
		if (!Util.isWindows()) {
			addActionToManager(manager, getAction(DatabaseQueryNewAction.ID));
			manager.add(new Separator());
			addActionToManager(manager, getAction(TableSelectAllAction.ID));
			addActionToManager(manager, getAction(TableSelectCountAction.ID));
			manager.add(new Separator());
		}
//		addActionToManager(manager, getAction(ShowSchemaEditorAction.ID));
		addActionToManager(manager, getAction(PropertyViewAction.ID));
		manager.add(new Separator());
	}

	/**
	 *
	 * Build the system table menu
	 *
	 * @param manager IMenuManager
	 */
	public void buildSystemTableMenu(IMenuManager manager) {
		if (!Util.isWindows()) {
			addActionToManager(manager, getAction(DatabaseQueryNewAction.ID));
			manager.add(new Separator());
			addActionToManager(manager, getAction(TableSelectAllAction.ID));
			addActionToManager(manager, getAction(TableSelectCountAction.ID));
			manager.add(new Separator());
		}
//		addActionToManager(manager, getAction(ShowSchemaEditorAction.ID));
//		manager.add(new Separator());
	}

	/**
	 * Construct the user view related actions
	 *
	 * @param manager the parent IMenuManager
	 */
	public void buildUserViewMenu(IMenuManager manager) {
		if (!Util.isWindows()) {
			addActionToManager(manager, getAction(DatabaseQueryNewAction.ID));
			manager.add(new Separator());
			addActionToManager(manager, getAction(TableSelectAllAction.ID));
			addActionToManager(manager, getAction(TableSelectCountAction.ID));
			manager.add(new Separator());
		}
		
		IAction renameTableAction = getAction(RenameTableAction.ID);
		renameTableAction.setText(com.cubrid.common.ui.spi.Messages.viewRenameActionName);
		renameTableAction.setImageDescriptor(CommonUIPlugin.getImageDescriptor("icons/action/view_rename.png"));
		addActionToManager(manager, renameTableAction);
		addActionToManager(manager, getAction(EditViewAction.ID));

		IAction viewDropAction = getAction(DropViewAction.ID);
		addActionToManager(manager, viewDropAction);
		manager.add(new Separator());

//		addActionToManager(manager, getAction(ShowSchemaEditorAction.ID));
//		manager.add(new Separator());
	}

	/**
	 * Construct user table related actions
	 *
	 * @param manager the parent IMenuManager
	 * @param node The ICubridNode object
	 */
	public void buildUserTableMenu(IMenuManager manager, ICubridNode node) {
		DatabaseInfo dbInfo = ((ISchemaNode) node).getDatabase().getDatabaseInfo();

		if (!Util.isWindows()) {
			// SELECT GROUP
			IMenuManager selectSqlMenu = new MenuManager(Messages.lblMakeSelectQueryGrp);
			manager.add(selectSqlMenu);
			// SELECT
			addActionToManager(selectSqlMenu, getAction(MakeSelectQueryAction.ID));
			// Parameterized SELECT
			addActionToManager(selectSqlMenu, getAction(MakeSelectPstmtQueryAction.ID));
			// Parameterized INSERT
			addActionToManager(manager, getAction(MakeInsertQueryAction.ID));
			// Parameterized UPDATE
			addActionToManager(manager, getAction(MakeUpdateQueryAction.ID));
			// Parameterized DELETE
			addActionToManager(manager, getAction(MakeDeleteQueryAction.ID));
			// CREATE GROUP
			IMenuManager createSqlMenu = new MenuManager(Messages.lblMakeCreateQueryGrp);
			manager.add(createSqlMenu);
			addActionToManager(createSqlMenu, getAction(MakeCreateQueryAction.ID));
			addActionToManager(createSqlMenu, getAction(MakeCloneQueryAction.ID));

			manager.add(new Separator());
		}
		
		// Schema to Code Actions
		addActionToManager(manager, getAction(TableToJavaCodeAction.ID));
		addActionToManager(manager, getAction(TableToPhpCodeAction.ID));
		addActionToManager(manager, getAction(ExportTableDefinitionAction.ID));
		manager.add(new Separator());

		// Install Schema Comment
		if (node instanceof CubridDatabase
				&& !CompatibleUtil.isCommentSupports(((CubridDatabase) node).getDatabaseInfo())) {
			addActionToManager(manager, getAction(SchemaCommentInstallAction.ID));
			manager.add(new Separator());
		}

		if (!Util.isWindows()) {
			// View data
			IMenuManager viewDataMenu = new MenuManager(Messages.viewDataMenuName);
			manager.add(viewDataMenu);
			addActionToManager(viewDataMenu, getAction(TableSelectAllAction.ID));
			addActionToManager(viewDataMenu, getAction(SelectByOnePstmtDataAction.ID));
			addActionToManager(viewDataMenu, getAction(SelectByMultiPstmtDataAction.ID));
			viewDataMenu.add(new Separator());
			addActionToManager(viewDataMenu, getAction(TableSelectCountAction.ID));

			// Input data
			IMenuManager inputDataMenu = new MenuManager(Messages.inputDataMenuName);
			manager.add(inputDataMenu);
			addActionToManager(inputDataMenu, getAction(InsertOneByPstmtAction.ID));
			addActionToManager(inputDataMenu, getAction(ImportDataFromFileAction.ID));

			// addActionToManager(manager, getAction(RunSQLFileAction.ID));
			manager.add(new Separator());
		}
		
		// Export & Import Actions
		addActionToManager(manager, getAction(ExportWizardAction.ID));
		addActionToManager(manager, getAction(ImportWizardAction.ID));
		manager.add(new Separator());

		// Manage table
		addActionToManager(manager, getAction(EditTableAction.ID));
		IAction renameTableAction = getAction(RenameTableAction.ID);
		renameTableAction.setText(com.cubrid.common.ui.spi.Messages.tableRenameActionName);
		renameTableAction.setImageDescriptor(CommonUIPlugin.getImageDescriptor("icons/action/table_rename.png"));
		addActionToManager(manager, renameTableAction);
		IMenuManager moreMenu = new MenuManager(Messages.tableMoreName);
		addActionToManager(moreMenu, getAction(DeleteTableAction.ID));
		IAction action = getAction(DeleteTableAction.ID);
		if (CompatibleUtil.isSupportTruncateTable(dbInfo)) {
			if (action != null) {
				action.setText(Messages.deleteAllRecordsActionName);
			}
			addActionToManager(moreMenu, getAction(TruncateTableAction.ID));
		} else {
			if (action != null) {
				action.setText(Messages.tableDeleteAllActionName);
			}
		}
		IAction tableDropAction = getAction(DropTableAction.ID);
		addActionToManager(moreMenu, tableDropAction);
		moreMenu.add(new Separator());
		if (CompatibleUtil.isSupportCreateTableLike(dbInfo)) {
			addActionToManager(moreMenu, getAction(CreateLikeTableAction.ID));
		}
		manager.add(moreMenu);
	}

	/**
	 *
	 * Get registered action by action ID
	 *
	 * @param id the action id
	 * @return the action object
	 */
	public IAction getAction(String id) {
		return ActionManager.getInstance().getAction(id);
	}

	/**
	 *
	 * Add action to menu manager
	 *
	 * @param manager the menu manager object
	 * @param action the action object
	 */
	protected void addActionToManager(IMenuManager manager, IAction action) {
		if (action != null) {
			manager.add(action);
		}
	}

	public DatabaseNavigatorMenu getDatabaseNavigatorMenu() {
		return new DatabaseNavigatorMenu();
	}
}
