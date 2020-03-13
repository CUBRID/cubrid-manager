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
package com.cubrid.cubridmanager.app;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.util.Util;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;

import com.cubrid.common.core.util.ApplicationUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.common.action.AboutAction;
import com.cubrid.common.ui.common.action.CubridOnlineForumAction;
import com.cubrid.common.ui.common.action.CubridProjectSiteAction;
import com.cubrid.common.ui.common.action.DropDownAction;
import com.cubrid.common.ui.common.action.HelpDocumentAction;
import com.cubrid.common.ui.common.action.NewFeaturesAction;
import com.cubrid.common.ui.common.action.OpenPreferenceAction;
import com.cubrid.common.ui.common.action.ReportBugAction;
import com.cubrid.common.ui.common.control.SearchContributionComposite;
import com.cubrid.common.ui.external.action.InstallMigrationToolkitAction;
import com.cubrid.common.ui.perspective.IPerspectiveConstance;
import com.cubrid.common.ui.perspective.OpenCMPerspectiveAction;
import com.cubrid.common.ui.perspective.OpenCQBPerspectiveAction;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.IActionConstants;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.common.action.QuitAction;
import com.cubrid.cubridmanager.ui.host.action.ViewServerVersionAction;
import com.cubrid.cubridmanager.ui.service.action.ServiceDashboardAction;
import com.cubrid.cubridmanager.ui.spi.Version;
import com.cubrid.cubridmanager.ui.spi.action.CubridActionBuilder;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 *
 * @author pangqiren
 * @version 1.0 - 2009-12-23 created by pangqiren
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {
	private static final Logger LOGGER = LogUtil.getLogger(ApplicationActionBarAdvisor.class);
	// common actions
	private IAction preferenceAction = null;
	private IAction quitAction = null;
	//private IAction checkNewVersionAction = null;
	private IAction cubridOnlineForumAction = null;
	private IAction cubridProjectSiteAction = null;
	private AboutAction aboutAction = null;
	private AboutAction clientVersionAction = null;
	private NewFeaturesAction newFeatureAction = null;
	private ReportBugAction reportBugAction;
	private ServiceDashboardAction serviceDashboardAction;


	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	/**
	 * Instantiates the actions used in the fill methods.
	 *
	 * @see org.eclipse.ui.application.ActionBarAdvisor#makeActions(org.eclipse
	 *      .ui.IWorkbenchWindow)
	 *
	 * @param window the window containing the action bars
	 */
	protected void makeActions(IWorkbenchWindow window) {
		ActionManager manager = ActionManager.getInstance();
		CubridActionBuilder.init();

		serviceDashboardAction = (ServiceDashboardAction) manager.getAction(ServiceDashboardAction.ID);
		if (serviceDashboardAction != null) {
			serviceDashboardAction.setText(com.cubrid.common.ui.spi.Messages.serviceDashboardActionName);
			register(serviceDashboardAction);
		}

		// customized actions for CUBRID Manager
		//common action
		preferenceAction = new OpenPreferenceAction(window.getShell(),
				Messages.openPreferenceActionName, null);
		register(preferenceAction);
		preferenceAction.setId("preferences"); // It must be needed to use a Preferences Menu of an Application Menu on Mac.
		manager.registerAction(preferenceAction);

		quitAction = new QuitAction(Messages.exitActionName);

		cubridOnlineForumAction = new CubridOnlineForumAction(Messages.cubridOnlineForumActionName);
		cubridProjectSiteAction = new CubridProjectSiteAction(Messages.cubridProjectSiteActionName);

		aboutAction = new AboutAction(Messages.aboutActionName,
				Version.productName, Version.buildVersionId,
				CubridManagerAppPlugin.getImageDescriptor("icons/cubridmanager16.gif"),
				CubridManagerAppPlugin.getImageDescriptor("icons/about.gif"));
		aboutAction.setId("about"); // It must be needed to use a About Menu of an Application Menu on Mac.

		clientVersionAction = new AboutAction(Messages.clientVersionActionName,
				Version.productName, Version.buildVersionId,
				CubridManagerAppPlugin.getImageDescriptor("icons/cubridmanager16.gif"),
				CubridManagerAppPlugin.getImageDescriptor("icons/about.gif"));

		newFeatureAction = new NewFeaturesAction(com.cubrid.common.ui.common.Messages.msgNewFeatures);

		reportBugAction = (ReportBugAction) manager.getAction(ReportBugAction.ID);
		reportBugAction.setCurrentVersion(Version.buildVersionId);
	}

	/**
	 * Fills the menu bar with the main menus for the window.
	 *
	 * @param menuBar the menu bar manager
	 */
	protected void fillMenuBar(IMenuManager menuManager) {
		ActionManager manager = ActionManager.getInstance();

		MenuManager helpMenu = new MenuManager(Messages.mnu_helpMneuName, IWorkbenchActionConstants.M_HELP);
		helpMenu.add(manager.getAction(HelpDocumentAction.ID));
		// fill in help menu
		if ("ko".equals(Messages.language)) {
			helpMenu.add(newFeatureAction);
		}
		helpMenu.add(new Separator());
		helpMenu.add(reportBugAction);
		helpMenu.add(new Separator());
		helpMenu.add(cubridOnlineForumAction);
		helpMenu.add(cubridProjectSiteAction);
		helpMenu.add(new Separator());
		helpMenu.add(new GroupMarker("updates"));
		helpMenu.add(new Separator());
		helpMenu.add(manager.getAction(ViewServerVersionAction.ID));
		helpMenu.add(new Separator());

		ActionContributionItem aboutActionItem = new ActionContributionItem(aboutAction);
		helpMenu.add(aboutActionItem);
		if (Util.isMac()) {
			aboutActionItem.setVisible(false);
		}

		menuManager.add(helpMenu);
	}

	/**
	 * Fills the cool bar with the main toolbars for the window.
	 *
	 * @param coolBar the cool bar manager
	 */
	protected void fillCoolBar(ICoolBarManager coolBarManager) {
		ActionManager manager = ActionManager.getInstance();
		coolBarManager.setLockLayout(true);
		IToolBarManager toolbarManager = new ToolBarManager(SWT.FLAT | SWT.WRAP | SWT.BOTTOM);
		coolBarManager.add(new ToolBarContributionItem(toolbarManager, IActionConstants.TOOL_NEW1));


		Bundle cqbBundle = null; 
		if (!Util.isWindows()) {
			cqbBundle = Platform.getBundle(ApplicationUtil.CQB_PLUGIN_ID);
			/* Active the CQB plugin */
			if (cqbBundle != null) {
				try {
					cqbBundle.start();
				} catch (Exception e) {
					LOGGER.error(e.getMessage());
				}
			}
		}

		Bundle cmBundle = Platform.getBundle(ApplicationUtil.CM_PLUGIN_ID);
		/* Active the CM plugin */
		if (cmBundle != null) {
			try {
				cmBundle.start();
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
			}
		}

		// Change view actions
		if (cqbBundle != null) {
			DropDownAction viewAction = new DropDownAction(
					Messages.modeActionBig,
					IAction.AS_DROP_DOWN_MENU,
					CubridManagerAppPlugin.getImageDescriptor("icons/cubridmanager32.gif"));
			viewAction.setDisabledImageDescriptor(CubridManagerAppPlugin.getImageDescriptor("icons/cubridmanager32.gif"));
			MenuManager viewActionManager = viewAction.getMenuManager();
			viewActionManager.add(manager.getAction(OpenCMPerspectiveAction.ID));
			
			if (!Util.isWindows()) {
				viewActionManager.add(manager.getAction(OpenCQBPerspectiveAction.ID));
			}
			
			ActionContributionItem viewItems = new ActionContributionItem(viewAction);
			viewItems.setMode(ActionContributionItem.MODE_FORCE_TEXT);
			viewItems.setId(IPerspectiveConstance.PERSPECTIVE_ACTION_CONTRIBUTION_ID);
			toolbarManager.add(viewItems);
			toolbarManager.add(new Separator());
		}

		/*TOOLS-3988 There still is the install option after installing cmt plugin.*/
		Bundle bundle = Platform.getBundle(ApplicationUtil.CMT_PLUGIN_ID);
		if (bundle == null) {
			toolbarManager.add(new Separator());
			IAction action = ActionManager.getInstance().getAction(InstallMigrationToolkitAction.ID);
			if (action != null) {
				ActionContributionItem item = new ActionContributionItem(action);
				item.setMode(ActionContributionItem.MODE_FORCE_TEXT);
				toolbarManager.add(item);
				item.setId(IPerspectiveConstance.MIGRATION_ACTION_CONTRIBUTION_ID);
			}
		} else {
			/*Active the CMT plugin */
			try {
				bundle.start();
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
			}
		}

		// Help
		toolbarManager.add(new Separator());
		DropDownAction helpDropAction = new DropDownAction(
				Messages.helpActionNameBig,
				IAction.AS_DROP_DOWN_MENU,
				CubridManagerUIPlugin.getImageDescriptor("icons/action/help_big.png"));
		helpDropAction.setDisabledImageDescriptor(CubridManagerUIPlugin.getImageDescriptor("icons/action/help_big.png"));
		MenuManager helpActionManager = helpDropAction.getMenuManager();
		helpActionManager.add(manager.getAction(HelpDocumentAction.ID));
		if ("ko".equals(Messages.language)) {
			helpActionManager.add(newFeatureAction);
		}
		helpActionManager.add(new Separator());
		helpActionManager.add(createItem(ReportBugAction.ID));
		helpActionManager.add(new Separator());
		helpActionManager.add(manager.getAction(ViewServerVersionAction.ID));
		helpActionManager.add(clientVersionAction);

		ActionContributionItem helpItems = new ActionContributionItem(helpDropAction);
		helpItems.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		helpItems.setId(IPerspectiveConstance.HELP_ACTION_CONTRIBUTION_ID);
		toolbarManager.add(helpItems);

		ControlContribution searchContribution = new ControlContribution(
				SearchContributionComposite.class.getName()) {
			protected Control createControl(Composite parent) {
				return new SearchContributionComposite(parent, SWT.None);
			}

		};
		searchContribution.setId(IPerspectiveConstance.SEARCH_ACTION_CONTRIBUTION_ID);
		toolbarManager.add(new Separator());
		toolbarManager.add(searchContribution);
	}

	/**
	 * Create action contribution item for action for show text and icon
	 *
	 * @param id action ID
	 * @return ActionContributionItem
	 */
	private ActionContributionItem createItem(String id) {
		ActionManager manager = ActionManager.getInstance();
		IAction action = manager.getAction(id);
		if (action == null) {
			return null;
		}

		ActionContributionItem item = new ActionContributionItem(action);
		item.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		return item;
	}
}
