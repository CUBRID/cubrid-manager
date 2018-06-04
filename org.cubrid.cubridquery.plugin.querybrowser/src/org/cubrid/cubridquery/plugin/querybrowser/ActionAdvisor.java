/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search Solution.
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
package org.cubrid.cubridquery.plugin.querybrowser;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.Util;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import com.cubrid.common.core.util.ApplicationType;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.action.BrokerConfOpenFileAction;
import com.cubrid.common.ui.common.action.BrokerLogParserAction;
import com.cubrid.common.ui.common.action.BrokerLogTopMergeAction;
import com.cubrid.common.ui.common.action.ConnectionUrlExportAction;
import com.cubrid.common.ui.common.action.DropDownAction;
import com.cubrid.common.ui.common.action.OIDNavigatorAction;
import com.cubrid.common.ui.common.action.OpenPreferenceAction;
import com.cubrid.common.ui.common.action.OpenQueryAction;
import com.cubrid.common.ui.common.action.ProxyAction;
import com.cubrid.common.ui.common.action.ReportBugAction;
import com.cubrid.common.ui.common.action.RestoreQueryEditorAction;
import com.cubrid.common.ui.common.dialog.SelectWorkspaceDialog;
import com.cubrid.common.ui.compare.data.action.DataCompareWizardAction;
import com.cubrid.common.ui.compare.schema.action.SchemaCompareWizardAction;
import com.cubrid.common.ui.cubrid.database.erwin.action.ExportERwinAction;
import com.cubrid.common.ui.cubrid.database.erwin.action.ImportERwinAction;
import com.cubrid.common.ui.cubrid.procedure.action.AddFunctionAction;
import com.cubrid.common.ui.cubrid.procedure.action.AddProcedureAction;
import com.cubrid.common.ui.cubrid.serial.action.CreateSerialAction;
import com.cubrid.common.ui.cubrid.table.action.CreateViewAction;
import com.cubrid.common.ui.cubrid.table.action.ExportWizardAction;
import com.cubrid.common.ui.cubrid.table.action.ImportWizardAction;
import com.cubrid.common.ui.cubrid.table.action.NewTableAction;
import com.cubrid.common.ui.cubrid.table.action.PstmtMultiDataAction;
import com.cubrid.common.ui.cubrid.table.action.PstmtOneDataAction;
import com.cubrid.common.ui.cubrid.trigger.action.NewTriggerAction;
import com.cubrid.common.ui.er.action.OpenSchemaEditorAction;
import com.cubrid.common.ui.perspective.AbsActionAdvisor;
import com.cubrid.common.ui.query.action.GotoLineAction;
import com.cubrid.common.ui.query.tuner.action.QueryTunerAction;
import com.cubrid.common.ui.schemacomment.action.SchemaCommentInstallAction;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.IActionConstants;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridquery.ui.common.action.QueryNewAction;
import com.cubrid.cubridquery.ui.common.action.QueryNewCustomAction;
import com.cubrid.cubridquery.ui.common.action.QuitAction;
import com.cubrid.cubridquery.ui.common.action.RefreshAction;
import com.cubrid.cubridquery.ui.connection.action.CloseQueryConnAction;
import com.cubrid.cubridquery.ui.connection.action.ConnectionExportAction;
import com.cubrid.cubridquery.ui.connection.action.ConnectionImportAction;
import com.cubrid.cubridquery.ui.connection.action.ConnectionUrlImportAction;
import com.cubrid.cubridquery.ui.connection.action.DeleteQueryConnAction;
import com.cubrid.cubridquery.ui.connection.action.EditQueryConnAction;
import com.cubrid.cubridquery.ui.connection.action.ImportConnsAction;
import com.cubrid.cubridquery.ui.connection.action.NewQueryConnAction;
import com.cubrid.cubridquery.ui.connection.action.OpenQueryConnAction;
import com.cubrid.cubridquery.ui.spi.Version;
import com.cubrid.cubridquery.ui.spi.action.CubridActionBuilder;

/**
 *
 * ActiveAdvisor Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2014-4-21 created by Kevin.Wang
 */
public class ActionAdvisor extends AbsActionAdvisor {

	// standard actions provided by the workbench
	private IWorkbenchAction saveAction = null;
	private IWorkbenchAction saveasAction = null;
	private IWorkbenchAction saveAllAction = null;
	private IWorkbenchAction closeAction = null;
	private IWorkbenchAction closeAllAction = null;

	// retarget actions provided by the workbench
	private IWorkbenchAction undoRetargetAction = null;
	private IWorkbenchAction redoRetargetAction = null;
	private IWorkbenchAction copyRetargetAction = null;
	private IWorkbenchAction pasteRetargetAction = null;
	private IWorkbenchAction cutRetargetAction = null;
	private IWorkbenchAction findRetargetAction = null;

	// customized action for CUBRID Manager
	// common actions
	private IAction preferenceAction = null;
	private IAction quitAction = null;

	private ReportBugAction reportBugAction;

	private static ActionAdvisor instance;

	private ActionAdvisor() {
		/* Init the actions */
		CubridActionBuilder.init();
	}

	/**
	 * Return the only DbNodePersistManager
	 *
	 * @return DbNodePersistManager
	 */
	public static ActionAdvisor getInstance() {
		synchronized (ActionAdvisor.class) {
			if (instance == null) {
				instance = new ActionAdvisor();
				instance.init();
			}
		}
		return instance;
	}

	private void init() {
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (window != null) {
			// standard actions provided by the workbench
			closeAction = ActionFactory.CLOSE.create(window);
			closeAction.setText(Messages.closeActionName);
			// register(closeAction);
			closeAllAction = ActionFactory.CLOSE_ALL.create(window);
			closeAllAction.setText(Messages.closeAllActionName);
			// register(closeAllAction);
			saveAction = ActionFactory.SAVE.create(window);
			saveAction.setText(Messages.saveActionName);
			// register(saveAction);
			saveasAction = ActionFactory.SAVE_AS.create(window);
			saveasAction.setText(Messages.saveAsActionName);
			// register(saveasAction);
			saveAllAction = ActionFactory.SAVE_ALL.create(window);
			saveAllAction.setText(Messages.saveAllActionName);
			// register(saveAllAction);

			// retarget actions provided by the workbench
			undoRetargetAction = ActionFactory.UNDO.create(window);
			undoRetargetAction
					.setText(com.cubrid.common.ui.spi.Messages.undoActionName);
			// register(undoRetargetAction);
			redoRetargetAction = ActionFactory.REDO.create(window);
			redoRetargetAction
					.setText(com.cubrid.common.ui.spi.Messages.redoActionName);
			// register(redoRetargetAction);
			copyRetargetAction = ActionFactory.COPY.create(window);
			copyRetargetAction
					.setText(com.cubrid.common.ui.spi.Messages.copyActionName);
			// register(copyRetargetAction);
			cutRetargetAction = ActionFactory.CUT.create(window);
			cutRetargetAction
					.setText(com.cubrid.common.ui.spi.Messages.cutActionName);
			// register(cutRetargetAction);
			pasteRetargetAction = ActionFactory.PASTE.create(window);
			pasteRetargetAction
					.setText(com.cubrid.common.ui.spi.Messages.pasteActionName);
			// register(pasteRetargetAction);
			findRetargetAction = ActionFactory.FIND.create(window);
			findRetargetAction
					.setText(com.cubrid.common.ui.spi.Messages.findReplaceActionName);
			// register(findRetargetAction);

			// customized actions for CUBRID Manager
			// common action

			preferenceAction = new OpenPreferenceAction(window.getShell(),
					Messages.openPreferenceActionName, null);
			// register(preferenceAction);
			preferenceAction.setId("preferences"); // It must be needed to use a
													// Preferences Menu of an
													// Application Menu on Mac.
			ActionManager.getInstance().registerAction(preferenceAction);

			quitAction = new QuitAction(Messages.exitActionName);

			reportBugAction = (ReportBugAction) ActionManager.getInstance()
					.getAction(ReportBugAction.ID);
			reportBugAction.setCurrentVersion(Version.buildVersionId);

			// Add proxy action for toolbar
			ProxyAction newConnectionAction = new ProxyAction(
					NewQueryConnAction.ID,
					com.cubrid.cubridquery.ui.spi.Messages.createConnActionNameBig,
					Activator
							.getImageDescriptor("icons/toolbar/qb_new_database_on.png"),
					Activator
							.getImageDescriptor("icons/toolbar/qb_new_database_off.png"));
			ActionManager.getInstance().registerAction(newConnectionAction);

			ProxyAction newQueryAction = new ProxyAction(
					QueryNewAction.ID,
					com.cubrid.common.ui.spi.Messages.queryNewActionNameBig,
					Activator
							.getImageDescriptor("icons/toolbar/qb_new_query_on.png"),
					Activator
							.getImageDescriptor("icons/toolbar/qb_new_query_off.png"));
			ActionManager.getInstance().registerAction(newQueryAction);

			ProxyAction newCustomQueryAction = new ProxyAction(
					QueryNewCustomAction.ID,
					com.cubrid.common.ui.spi.Messages.queryNewCustomActionName
							.replaceAll("\\.", ""),
					Activator
							.getImageDescriptor("icons/toolbar/qb_new_query_on.png"),
					Activator
							.getImageDescriptor("icons/toolbar/qb_new_query_off.png"));
			ActionManager.getInstance().registerAction(newCustomQueryAction);

			ProxyAction newTableAction = new ProxyAction(
					NewTableAction.ID,
					com.cubrid.common.ui.spi.Messages.tableNewActionName
							.replaceAll("\\.", ""),
					Activator
							.getImageDescriptor("icons/toolbar/qb_new_table_on.png"),
					Activator
							.getImageDescriptor("icons/toolbar/qb_new_table_off.png"));
			ActionManager.getInstance().registerAction(newTableAction);

			ProxyAction newViewAction = new ProxyAction(
					CreateViewAction.ID,
					com.cubrid.common.ui.spi.Messages.createViewActionName
							.replaceAll("\\.", ""),
					Activator
							.getImageDescriptor("icons/toolbar/qb_new_view_on.png"),
					Activator
							.getImageDescriptor("icons/toolbar/qb_new_view_off.png"));
			ActionManager.getInstance().registerAction(newViewAction);

			ProxyAction newSerialAction = new ProxyAction(
					CreateSerialAction.ID,
					com.cubrid.common.ui.spi.Messages.createSerialActionName
							.replaceAll("\\.", ""),
					Activator
							.getImageDescriptor("icons/toolbar/qb_new_serial_on.png"),
					Activator
							.getImageDescriptor("icons/toolbar/qb_new_serial_off.png"));
			ActionManager.getInstance().registerAction(newSerialAction);

			ProxyAction newTriggerAction = new ProxyAction(
					NewTriggerAction.ID,
					com.cubrid.common.ui.spi.Messages.newTriggerActionName
							.replaceAll("\\.", ""),
					Activator
							.getImageDescriptor("icons/toolbar/qb_new_trigger_on.png"),
					Activator
							.getImageDescriptor("icons/toolbar/qb_new_trigger_off.png"));
			ActionManager.getInstance().registerAction(newTriggerAction);

			ProxyAction importAction = new ProxyAction(
					ImportWizardAction.ID,
					com.cubrid.common.ui.spi.Messages.tableImportActionName
							.replaceAll("\\.", ""),
					Activator
							.getImageDescriptor("icons/toolbar/qb_import_on.png"),
					Activator
							.getImageDescriptor("icons/toolbar/qb_import_off.png"));
			ActionManager.getInstance().registerAction(importAction);

			ProxyAction exportAction = new ProxyAction(
					ExportWizardAction.ID,
					com.cubrid.common.ui.spi.Messages.tableExportActionName
							.replaceAll("\\.", ""),
					Activator
							.getImageDescriptor("icons/toolbar/qb_export_on.png"),
					Activator
							.getImageDescriptor("icons/toolbar/qb_export_off.png"));
			ActionManager.getInstance().registerAction(exportAction);

			ProxyAction openSchemaEditorAction = new ProxyAction(
					OpenSchemaEditorAction.ID,
					com.cubrid.common.ui.spi.Messages.schemaDesignerActionName,
					CommonUIPlugin
							.getImageDescriptor("icons/action/schema_edit_on.png"),
					CommonUIPlugin
							.getImageDescriptor("icons/action/schema_edit_off.png"));
			ActionManager.getInstance().registerAction(openSchemaEditorAction);

			ProxyAction refreshAction = new ProxyAction(
					RefreshAction.ID,
					com.cubrid.cubridquery.ui.spi.Messages.refreshActionName,
					Activator
							.getImageDescriptor("icons/toolbar/refresh_32.png"),
					Activator
							.getImageDescriptor("icons/toolbar/refresh_32_disabled.png"));
			ActionManager.getInstance().registerAction(refreshAction);

			ProxyAction reportBugProxyAction = new ProxyAction(
					ReportBugAction.ID,
					Messages.reportBugProxyAction,
					Activator
							.getImageDescriptor("icons/toolbar/qb_new_bug.png"),
					null);
			ActionManager.getInstance().registerAction(reportBugProxyAction);
		}

	}

	public void showToolbar(ICoolBarManager coolBarManager) {
		IToolBarManager newToolbarManager = getToolbarManaeger(coolBarManager);
		ActionManager manager = ActionManager.getInstance();

		String insertPoint = getToolbarInsertPoint(coolBarManager);

		newToolbarManager.insertBefore(insertPoint,
				createItem(NewQueryConnAction.ID));
		newToolbarManager.insertBefore(insertPoint,
				(createItem(QueryNewAction.ID)));

		newToolbarManager.insertBefore(insertPoint, (new Separator()));
		newToolbarManager.insertBefore(insertPoint,
				(createItem(RefreshAction.ID)));

		newToolbarManager.insertBefore(insertPoint, (new Separator()));
		newToolbarManager.insertBefore(insertPoint,
				(createItem(NewTableAction.ID)));
		newToolbarManager.insertBefore(insertPoint,
				(createItem(CreateViewAction.ID)));

		newToolbarManager.insertBefore(insertPoint, (new Separator()));
		newToolbarManager.insertBefore(insertPoint,
				(createItem(ImportWizardAction.ID)));
		newToolbarManager.insertBefore(insertPoint,
				(createItem(ExportWizardAction.ID)));

		newToolbarManager.insertBefore(insertPoint, (new Separator()));
		DropDownAction toolsDropAction = new DropDownAction(
				com.cubrid.common.ui.spi.Messages.toolsActionName,
				IAction.AS_DROP_DOWN_MENU,
				Activator.getImageDescriptor("icons/toolbar/qb_tools_on.png"));
		toolsDropAction.setDisabledImageDescriptor(Activator
				.getImageDescriptor("icons/toolbar/qb_tools_off.png"));
		MenuManager toolsActionManager = toolsDropAction.getMenuManager();
		toolsActionManager.add(manager.getAction(SchemaCompareWizardAction.ID));
		toolsActionManager.add(manager.getAction(DataCompareWizardAction.ID));
		toolsActionManager.add(new Separator());
		toolsActionManager.add(manager.getAction(OpenSchemaEditorAction.ID));
		toolsActionManager.add(new Separator());
		toolsActionManager.add(manager.getAction(ExportERwinAction.ID));
		toolsActionManager.add(manager.getAction(ImportERwinAction.ID));
		toolsActionManager.add(new Separator());
		toolsActionManager.add(manager.getAction(QueryTunerAction.ID));

		ActionContributionItem toolsItem = new ActionContributionItem(
				toolsDropAction);
		toolsItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		newToolbarManager.insertBefore(insertPoint, toolsItem);
		newToolbarManager.insertBefore(insertPoint, new Separator());

		newToolbarManager.update(true);
		coolBarManager.update(true);
	}

	public void showMenu(IMenuManager menuManager) {
		ActionManager manager = ActionManager.getInstance();

		MenuManager fileMenu = new MenuManager(Messages.mnu_fileMenuName,
				IActionConstants.MENU_FILE);
		MenuManager editMenu = new MenuManager(Messages.mnu_editMenuName,
				IActionConstants.MENU_EDIT);
		MenuManager toolsMenu = new MenuManager(Messages.mnu_toolsMenuName,
				IActionConstants.MENU_TOOLS);
		MenuManager actionMenu = new MenuManager(Messages.mnu_actionMenuName,
				IActionConstants.MENU_ACTION);
		// fill in file menu
		fileMenu.add(manager.getAction(OpenQueryConnAction.ID));
		fileMenu.add(manager.getAction(CloseQueryConnAction.ID));
		fileMenu.add(new Separator());
		fileMenu.add(manager.getAction(NewQueryConnAction.ID));
		fileMenu.add(manager.getAction(EditQueryConnAction.ID));
		fileMenu.add(manager.getAction(DeleteQueryConnAction.ID));
		fileMenu.add(new Separator());
		fileMenu.add(manager.getAction(ConnectionUrlImportAction.ID));
		fileMenu.add(manager.getAction(ConnectionUrlExportAction.ID));
		fileMenu.add(new Separator());
		fileMenu.add(manager.getAction(ConnectionImportAction.ID));
		fileMenu.add(manager.getAction(ConnectionExportAction.ID));
		fileMenu.add(new Separator());
		fileMenu.add(closeAction);
		fileMenu.add(closeAllAction);
		fileMenu.add(new Separator());
		fileMenu.add(manager.getAction(OpenQueryAction.ID));
		fileMenu.add(saveAction);
		fileMenu.add(saveasAction);
		fileMenu.add(saveAllAction);
		fileMenu.add(new Separator());
		fileMenu.add(manager.getAction(RestoreQueryEditorAction.ID));
		fileMenu.add(new Separator());
		fileMenu.add(manager.getAction(ImportConnsAction.ID));
		fileMenu.add(SelectWorkspaceDialog.getWorkspaceMenu(
				ApplicationType.CUBRID_QUERY_BROWSER.getShortName(),
				Version.buildVersionId));
		fileMenu.add(new Separator());

		ActionContributionItem preferencesActionItem = new ActionContributionItem(
				preferenceAction);
		fileMenu.add(preferencesActionItem);
		if (Util.isMac()) {
		}

		fileMenu.add(manager.getAction(RefreshAction.ID));
		fileMenu.add(new Separator());
		fileMenu.add(quitAction);
		fileMenu.add(new Separator());

		// fill in edit menu
		editMenu.add(undoRetargetAction);
		editMenu.add(redoRetargetAction);
		editMenu.add(new Separator());
		editMenu.add(copyRetargetAction);
		editMenu.add(cutRetargetAction);
		editMenu.add(pasteRetargetAction);
		editMenu.add(new Separator());
		editMenu.add(findRetargetAction);
		editMenu.add(manager.getAction(GotoLineAction.ID));

		// fill in the tools menu
		toolsMenu.add(manager.getAction(QueryNewAction.ID));
		toolsMenu.add(manager.getAction(QueryNewCustomAction.ID));
		toolsMenu.add(new Separator());
		toolsMenu.add(manager.getAction(OIDNavigatorAction.ID));
		toolsMenu.add(new Separator());
		toolsMenu.add(manager.getAction(NewTableAction.ID));
		toolsMenu.add(manager.getAction(CreateViewAction.ID));
		toolsMenu.add(new Separator());
		toolsMenu.add(manager.getAction(CreateSerialAction.ID));
		toolsMenu.add(new Separator());
		toolsMenu.add(manager.getAction(NewTriggerAction.ID));
		toolsMenu.add(new Separator());
		toolsMenu.add(manager.getAction(AddFunctionAction.ID));
		toolsMenu.add(manager.getAction(AddProcedureAction.ID));
		toolsMenu.add(new Separator());
		toolsMenu.add(manager.getAction(ExportWizardAction.ID));
		toolsMenu.add(manager.getAction(ImportWizardAction.ID));
//		toolsMenu.add(new Separator());
//		fileMenu.add(manager.getAction(OpenSchemaEditorAction.ID));
		fileMenu.add(new Separator());
		IMenuManager perparedMenu = new MenuManager(
				com.cubrid.common.ui.spi.Messages.preparedTableDataMenuName);
		toolsMenu.add(perparedMenu);
		perparedMenu.add(manager.getAction(PstmtOneDataAction.ID));
		perparedMenu.add(manager.getAction(PstmtMultiDataAction.ID));
		toolsMenu.add(new Separator());
		toolsMenu.add(manager.getAction(BrokerLogParserAction.ID));
		toolsMenu.add(manager.getAction(BrokerLogTopMergeAction.ID));
		toolsMenu.add(manager.getAction(BrokerConfOpenFileAction.ID));

		DatabaseInfo info = ((CubridDatabase) manager.getMenuProvider()
				.getDatabaseNavigatorMenu().getSelectedDb()).getDatabaseInfo();
		if (info != null && !CompatibleUtil.isCommentSupports(info)) {
			toolsMenu.add(new Separator());
			toolsMenu.add(manager.getAction(SchemaCommentInstallAction.ID));
		}

		menuManager.insertBefore(getMenuInsertPoint(menuManager), fileMenu);
		menuManager.insertBefore(getMenuInsertPoint(menuManager), editMenu);
		menuManager.insertBefore(getMenuInsertPoint(menuManager), toolsMenu);
		menuManager.insertBefore(getMenuInsertPoint(menuManager), actionMenu);

		menuManager.update(true);

	}

	/**
	 *
	 * Create action contribution item for action for show text and icon
	 *
	 * @param id
	 *            action ID
	 * @return ActionContributionItem
	 */
	private ActionContributionItem createItem(String id) {
		IAction action = ActionManager.getInstance().getAction(
				id + ProxyAction.POSTFIX_ID);
		if (action == null) {
			return null;
		}
		ActionContributionItem item = new ActionContributionItem(action);
		item.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		return item;
	}
}
