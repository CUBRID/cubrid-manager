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
package org.cubrid.cubridmanager.plugin.manager;

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
import com.cubrid.common.ui.common.action.BrokerLogParserAction;
import com.cubrid.common.ui.common.action.BrokerLogTopMergeAction;
import com.cubrid.common.ui.common.action.DropDownAction;
import com.cubrid.common.ui.common.action.OpenPreferenceAction;
import com.cubrid.common.ui.common.action.OpenQueryAction;
import com.cubrid.common.ui.common.action.ReportBugAction;
import com.cubrid.common.ui.common.action.RestoreQueryEditorAction;
import com.cubrid.common.ui.common.dialog.SelectWorkspaceDialog;
import com.cubrid.common.ui.compare.data.action.DataCompareWizardAction;
import com.cubrid.common.ui.compare.schema.action.SchemaCompareWizardAction;
import com.cubrid.common.ui.cubrid.database.erwin.action.ExportERwinAction;
import com.cubrid.common.ui.cubrid.database.erwin.action.ImportERwinAction;
import com.cubrid.common.ui.cubrid.serial.action.CreateSerialAction;
import com.cubrid.common.ui.cubrid.table.action.CreateViewAction;
import com.cubrid.common.ui.cubrid.table.action.ExportWizardAction;
import com.cubrid.common.ui.cubrid.table.action.ImportWizardAction;
import com.cubrid.common.ui.cubrid.table.action.NewTableAction;
import com.cubrid.common.ui.cubrid.trigger.action.NewTriggerAction;
import com.cubrid.common.ui.cubrid.user.action.AddUserAction;
import com.cubrid.common.ui.cubrid.user.action.DeleteUserAction;
import com.cubrid.common.ui.cubrid.user.action.EditUserAction;
import com.cubrid.common.ui.er.action.OpenSchemaEditorAction;
import com.cubrid.common.ui.perspective.AbsActionAdvisor;
import com.cubrid.common.ui.query.action.DatabaseQueryNewAction;
import com.cubrid.common.ui.query.action.GotoLineAction;
import com.cubrid.common.ui.query.tuner.action.QueryTunerAction;
import com.cubrid.common.ui.schemacomment.action.SchemaCommentInstallAction;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.IActionConstants;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.ui.broker.action.StartBrokerEnvAction;
import com.cubrid.cubridmanager.ui.broker.action.StopBrokerEnvAction;
import com.cubrid.cubridmanager.ui.common.action.QueryNewAction;
import com.cubrid.cubridmanager.ui.common.action.QueryNewCustomAction;
import com.cubrid.cubridmanager.ui.common.action.QuitAction;
import com.cubrid.cubridmanager.ui.common.action.RefreshAction;
import com.cubrid.cubridmanager.ui.common.action.StartRetargetAction;
import com.cubrid.cubridmanager.ui.common.action.StartServiceAction;
import com.cubrid.cubridmanager.ui.common.action.StopServiceAction;
import com.cubrid.cubridmanager.ui.common.action.UserManagementAction;
import com.cubrid.cubridmanager.ui.cubrid.database.action.BackupDatabaseAction;
import com.cubrid.cubridmanager.ui.cubrid.database.action.CheckDatabaseAction;
import com.cubrid.cubridmanager.ui.cubrid.database.action.LoadDatabaseAction;
import com.cubrid.cubridmanager.ui.cubrid.database.action.LockInfoAction;
import com.cubrid.cubridmanager.ui.cubrid.database.action.OptimizeAction;
import com.cubrid.cubridmanager.ui.cubrid.database.action.RestoreDatabaseAction;
import com.cubrid.cubridmanager.ui.cubrid.database.action.StartDatabaseAction;
import com.cubrid.cubridmanager.ui.cubrid.database.action.StopDatabaseAction;
import com.cubrid.cubridmanager.ui.cubrid.database.action.TransactionInfoAction;
import com.cubrid.cubridmanager.ui.cubrid.database.action.UnloadDatabaseAction;
import com.cubrid.cubridmanager.ui.host.action.AddHostAction;
import com.cubrid.cubridmanager.ui.host.action.ChangeManagerPasswordAction;
import com.cubrid.cubridmanager.ui.host.action.ConnectHostAction;
import com.cubrid.cubridmanager.ui.host.action.CubridServerExportAction;
import com.cubrid.cubridmanager.ui.host.action.CubridServerImportAction;
import com.cubrid.cubridmanager.ui.host.action.DeleteHostAction;
import com.cubrid.cubridmanager.ui.host.action.DisConnectHostAction;
import com.cubrid.cubridmanager.ui.host.action.EditHostAction;
import com.cubrid.cubridmanager.ui.host.action.ImportHostsAction;
import com.cubrid.cubridmanager.ui.host.action.UnifyHostConfigAction;
import com.cubrid.cubridmanager.ui.service.action.ServiceDashboardAction;
import com.cubrid.cubridmanager.ui.spi.Version;
import com.cubrid.cubridmanager.ui.spi.action.CubridActionBuilder;
import com.cubrid.cubridmanager.ui.spi.action.CubridMenuProvider;

/**
 *
 * ActiveAdvisor Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2014-4-21 created by Kevin.Wang
 */
public class ActionAdvisor extends AbsActionAdvisor {

	private static ActionAdvisor instance;

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

	// common actions
	private IAction preferenceAction = null;
	private IAction quitAction = null;
	private ReportBugAction reportBugAction;
	private ServiceDashboardAction serviceDashboardAction;

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

			ActionManager manager = ActionManager.getInstance();

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

			serviceDashboardAction = (ServiceDashboardAction) manager
					.getAction(ServiceDashboardAction.ID);
			if (serviceDashboardAction != null) {
				serviceDashboardAction
						.setText(com.cubrid.common.ui.spi.Messages.serviceDashboardActionName);
				// register(serviceDashboardAction);
			}

			// customized actions for CUBRID Manager
			// common action
			preferenceAction = new OpenPreferenceAction(window.getShell(),
					Messages.openPreferenceActionName, null);
			// register(preferenceAction);
			preferenceAction.setId("preferences"); // It must be needed to use a
													// Preferences Menu of an
													// Application Menu on Mac.
			manager.registerAction(preferenceAction);

			quitAction = new QuitAction(Messages.exitActionName);

			reportBugAction = (ReportBugAction) manager
					.getAction(ReportBugAction.ID);
			reportBugAction.setCurrentVersion(Version.buildVersionId);

		}

	}

	public void showToolbar(ICoolBarManager coolBarManager) {
		IToolBarManager newToolbarManager = getToolbarManaeger(coolBarManager);
		ActionManager manager = ActionManager.getInstance();

		String insertPoint = getToolbarInsertPoint(coolBarManager);
		// Add host action
		newToolbarManager.insertBefore(insertPoint,
				createItem(AddHostAction.ID_BIG));
		newToolbarManager.insertBefore(insertPoint, new Separator());

		// Start action
		newToolbarManager.insertBefore(insertPoint,
				createItem(StartRetargetAction.ID_BIG));
		newToolbarManager.insertBefore(insertPoint, new Separator());

		if (!Util.isWindows()) {
			// Open queryEditor
			newToolbarManager.insertBefore(insertPoint,
					createItem(DatabaseQueryNewAction.ID_BIG));
		}
		
		// Schema actions
		newToolbarManager.insertBefore(insertPoint, new Separator());
		DropDownAction schemaDropAction = new DropDownAction(
				Messages.schemaActionNameBig, IAction.AS_DROP_DOWN_MENU,
				Activator.getImageDescriptor("icons/toolbar/schema_big.png"));
		schemaDropAction.setDisabledImageDescriptor(Activator
				.getImageDescriptor("icons/toolbar/schema_big.png"));
		MenuManager schemaActionManager = schemaDropAction.getMenuManager();
		schemaActionManager.add(manager.getAction(NewTableAction.ID));
		schemaActionManager.add(manager.getAction(CreateViewAction.ID));
		schemaActionManager.add(manager.getAction(CreateSerialAction.ID));
		schemaActionManager.add(manager.getAction(NewTriggerAction.ID));
		ActionContributionItem schemaItems = new ActionContributionItem(
				schemaDropAction);
		schemaItems.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		newToolbarManager.insertBefore(insertPoint, schemaItems);

		newToolbarManager.insertBefore(insertPoint, new Separator());

		// Import and export
		DropDownAction dataDropAction = new DropDownAction(
				Messages.dataActionNameBig, IAction.AS_DROP_DOWN_MENU,
				Activator.getImageDescriptor("icons/toolbar/data_big.png"));
		dataDropAction.setDisabledImageDescriptor(Activator
				.getImageDescriptor("icons/toolbar/data_big.png"));
		MenuManager dataActionManager = dataDropAction.getMenuManager();
		dataActionManager.add(manager.getAction(ExportWizardAction.ID));
		dataActionManager.add(manager.getAction(ImportWizardAction.ID));
		ActionContributionItem dataItems = new ActionContributionItem(
				dataDropAction);
		dataItems.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		newToolbarManager.insertBefore(insertPoint, dataItems);

		newToolbarManager.insertBefore(insertPoint, new Separator());

		DropDownAction toolsDropAction = new DropDownAction(
				com.cubrid.common.ui.spi.Messages.toolsActionName,
				IAction.AS_DROP_DOWN_MENU,
				Activator.getImageDescriptor("icons/toolbar/qb_tools_on.png"));
		toolsDropAction.setDisabledImageDescriptor(Activator
				.getImageDescriptor("icons/toolbar/qb_tools_off.png"));
		MenuManager toolsActionManager = toolsDropAction.getMenuManager();
		toolsActionManager.add(ActionManager.getInstance().getAction(
				SchemaCompareWizardAction.ID));
		toolsActionManager.add(ActionManager.getInstance().getAction(
				DataCompareWizardAction.ID));
		toolsActionManager.add(new Separator());
		toolsActionManager.add(manager.getAction(OpenSchemaEditorAction.ID));
		toolsActionManager.add(new Separator());
		toolsActionManager.add(ActionManager.getInstance().getAction(
				ExportERwinAction.ID));
		toolsActionManager.add(ActionManager.getInstance().getAction(
				ImportERwinAction.ID));
		toolsActionManager.add(new Separator());
		// toolsActionManager.add(ActionManager.getInstance().getAction(RunSQLFileAction.ID));
		toolsActionManager.add(ActionManager.getInstance().getAction(
				UnifyHostConfigAction.ID));
		toolsActionManager.add(ActionManager.getInstance().getAction(
				QueryTunerAction.ID));
		toolsActionManager.add(new Separator());

//		toolsActionManager.add(ActionManager.getInstance().getAction(
//				LaunchBrowserAction.ID));
		// Action for CM service analysis
		// toolsActionManager.add(ActionManager.getInstance().getAction(CMServiceAnalysisAction.ID));
		ActionContributionItem toolsItem = new ActionContributionItem(
				toolsDropAction);
		toolsItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		newToolbarManager.insertBefore(insertPoint, toolsItem);
		newToolbarManager.insertBefore(insertPoint, new Separator());

		// User Management
		DropDownAction usersDropAction = new DropDownAction(
				Messages.userActionNameBig, IAction.AS_DROP_DOWN_MENU,
				Activator.getImageDescriptor("icons/toolbar/user_big.png"));
		usersDropAction.setDisabledImageDescriptor(Activator
				.getImageDescriptor("icons/toolbar/user_big.png"));
		MenuManager userActionManager = usersDropAction.getMenuManager();
		userActionManager.add(manager.getAction(UserManagementAction.ID));
		userActionManager
				.add(manager.getAction(ChangeManagerPasswordAction.ID));
		userActionManager.add(new Separator());
		userActionManager.add(manager.getAction(AddUserAction.ID));
		userActionManager.add(manager.getAction(EditUserAction.ID));
		userActionManager.add(manager.getAction(DeleteUserAction.ID));
		ActionContributionItem usersItems = new ActionContributionItem(
				usersDropAction);
		usersItems.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		newToolbarManager.insertBefore(insertPoint, usersItems);

		// Management actions
		DropDownAction manageAction = new DropDownAction(
				Messages.manageActionNameBig,
				IAction.AS_DROP_DOWN_MENU,
				Activator
						.getImageDescriptor("icons/toolbar/db_management_big.png"));
		manageAction.setDisabledImageDescriptor(Activator
				.getImageDescriptor("icons/toolbar/db_management_big.png"));

		MenuManager manageActionManager = manageAction.getMenuManager();
		manageActionManager.add(manager.getAction(UnloadDatabaseAction.ID));
		manageActionManager.add(manager.getAction(LoadDatabaseAction.ID));
		manageActionManager.add(manager.getAction(BackupDatabaseAction.ID));
		manageActionManager.add(manager.getAction(RestoreDatabaseAction.ID));
		manageActionManager.add(manager.getAction(OptimizeAction.ID));
		manageActionManager.add(manager.getAction(CheckDatabaseAction.ID));
		manageActionManager.add(new Separator());
		manageActionManager.add(manager.getAction(TransactionInfoAction.ID));
		manageActionManager.add(manager.getAction(LockInfoAction.ID));

		ActionContributionItem manageItems = new ActionContributionItem(
				manageAction);
		manageItems.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		newToolbarManager.insertBefore(insertPoint, manageItems);
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
		// MenuManager helpMenu = new MenuManager(Messages.mnu_helpMneuName,
		// IWorkbenchActionConstants.M_HELP);

		// fill in file menu
		fileMenu.add(manager.getAction(ConnectHostAction.ID));
		fileMenu.add(manager.getAction(DisConnectHostAction.ID));
		fileMenu.add(new Separator());
		fileMenu.add(manager.getAction(AddHostAction.ID));
		fileMenu.add(manager.getAction(EditHostAction.ID));
		fileMenu.add(manager.getAction(DeleteHostAction.ID));
		fileMenu.add(new Separator());
		fileMenu.add(manager.getAction(CubridServerImportAction.ID));
		fileMenu.add(manager.getAction(CubridServerExportAction.ID));
		fileMenu.add(new Separator());
		fileMenu.add(closeAction);
		fileMenu.add(closeAllAction);
		fileMenu.add(new Separator());
		if (!Util.isWindows()) {
			fileMenu.add(manager.getAction(OpenQueryAction.ID));
			fileMenu.add(saveAction);
			fileMenu.add(saveasAction);
			fileMenu.add(saveAllAction);
			fileMenu.add(new Separator());
			fileMenu.add(manager.getAction(RestoreQueryEditorAction.ID));
			fileMenu.add(new Separator());
		}
		fileMenu.add(manager.getAction(ImportHostsAction.ID));
		fileMenu.add(SelectWorkspaceDialog.getWorkspaceMenu(
				ApplicationType.CUBRID_MANAGER.getShortName(),
				Version.buildVersionId));
		fileMenu.add(new Separator());

		ActionContributionItem preferencesActionItem = new ActionContributionItem(
				preferenceAction);
		fileMenu.add(preferencesActionItem);
		if (Util.isMac()) {
			preferencesActionItem.setVisible(false);
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

		// fill in the run menu
		if (!Util.isWindows()) {
			toolsMenu.add(manager.getAction(QueryNewAction.ID));
			toolsMenu.add(manager.getAction(QueryNewCustomAction.ID));
			toolsMenu.add(new Separator());
		}
		toolsMenu.add(manager.getAction(StartServiceAction.ID));
		toolsMenu.add(manager.getAction(StopServiceAction.ID));
		toolsMenu.add(new Separator());
		toolsMenu.add(manager.getAction(StartDatabaseAction.ID));
		toolsMenu.add(manager.getAction(StopDatabaseAction.ID));
		toolsMenu.add(new Separator());
		toolsMenu.add(manager.getAction(StartBrokerEnvAction.ID));
		toolsMenu.add(manager.getAction(StopBrokerEnvAction.ID));
		toolsMenu.add(new Separator());
		toolsMenu.add(manager.getAction(ChangeManagerPasswordAction.ID));
		toolsMenu.add(manager.getAction(UserManagementAction.ID));
		toolsMenu.add(new Separator());
		toolsMenu.add(manager.getAction(BrokerLogParserAction.ID));
		toolsMenu.add(manager.getAction(BrokerLogTopMergeAction.ID));
		toolsMenu.add(new Separator());

		DatabaseInfo info = ((CubridDatabase) manager.getMenuProvider()
				.getDatabaseNavigatorMenu().getSelectedDb()).getDatabaseInfo();
		if (info != null && !CompatibleUtil.isCommentSupports(info)) {
			toolsMenu.add(manager.getAction(SchemaCommentInstallAction.ID));
		}

		toolsMenu.add(manager.getAction(ServiceDashboardAction.ID));

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
		IAction action = ActionManager.getInstance().getAction(id);
		if (action == null) {
			return null;
		}
		ActionContributionItem item = new ActionContributionItem(action);
		item.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		return item;
	}
}
