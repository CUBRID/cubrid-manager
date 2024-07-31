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

import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.action.BrokerConfOpenFileAction;
import com.cubrid.common.ui.common.action.BrokerLogParserAction;
import com.cubrid.common.ui.common.action.BrokerLogTopMergeAction;
import com.cubrid.common.ui.common.action.GroupPropertyAction;
import com.cubrid.common.ui.common.action.OpenTargetAction;
import com.cubrid.common.ui.cubrid.user.action.AddUserAction;
import com.cubrid.common.ui.cubrid.user.action.DeleteUserAction;
import com.cubrid.common.ui.cubrid.user.action.EditUserAction;
import com.cubrid.common.ui.schemacomment.action.SchemaCommentInstallAction;
import com.cubrid.common.ui.spi.action.ActionBuilder;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.cubridmanager.ui.spi.persist.CQBPersisteManager;
import com.cubrid.cubridquery.ui.CubridQueryUIPlugin;
import com.cubrid.cubridquery.ui.common.action.CQBGroupSettingAction;
import com.cubrid.cubridquery.ui.common.action.ChangeShardAction;
import com.cubrid.cubridquery.ui.common.action.PropertyAction;
import com.cubrid.cubridquery.ui.common.action.QueryNewAction;
import com.cubrid.cubridquery.ui.common.action.QueryNewCustomAction;
import com.cubrid.cubridquery.ui.common.action.RefreshAction;
import com.cubrid.cubridquery.ui.common.navigator.CubridQueryNavigatorView;
import com.cubrid.cubridquery.ui.connection.action.CloseQueryConnAction;
import com.cubrid.cubridquery.ui.connection.action.ConnectionExportAction;
import com.cubrid.cubridquery.ui.connection.action.ConnectionImportAction;
import com.cubrid.cubridquery.ui.connection.action.ConnectionUrlImportAction;
import com.cubrid.cubridquery.ui.connection.action.CopyQueryConnAction;
import com.cubrid.cubridquery.ui.connection.action.DeleteQueryConnAction;
import com.cubrid.cubridquery.ui.connection.action.EditQueryConnAction;
import com.cubrid.cubridquery.ui.connection.action.ImportConnsAction;
import com.cubrid.cubridquery.ui.connection.action.NewQueryConnAction;
import com.cubrid.cubridquery.ui.connection.action.OpenQueryConnAction;
import com.cubrid.cubridquery.ui.connection.action.PasteQueryConnAction;
import com.cubrid.cubridquery.ui.connection.action.RenameConnectionAction;
import com.cubrid.cubridquery.ui.connection.action.ViewDatabaseVersionAction;
import com.cubrid.cubridquery.ui.spi.Messages;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Shell;

/**
 * This class is responsible to build CUBRID Query menu and toolbar action
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class CubridActionBuilder extends ActionBuilder {
    private static CubridActionBuilder instance;

    /**
     * Get the ApplicationHeartBeat
     *
     * @return
     */
    public static void init() {
        synchronized (CubridActionBuilder.class) {
            if (instance == null) {
                instance = new CubridActionBuilder();
            }
        }
    }

    private CubridActionBuilder() {
        makeActions(null);
    }

    /**
     * Make all actions for CUBRID Manager menu and toolbar
     *
     * @param window the workbench window
     */
    protected void makeActions(Shell shell) {
        super.makeActions(shell);

        // common action
        IAction propertyAction =
                new PropertyAction(
                        shell,
                        Messages.propertyActionName,
                        CubridQueryUIPlugin.getImageDescriptor("icons/action/property.png"));
        ActionManager.getInstance().registerAction(propertyAction);

        IAction refreshAction =
                new RefreshAction(
                        shell,
                        Messages.refreshActionName,
                        CubridQueryUIPlugin.getImageDescriptor("icons/action/refresh.png"));
        ActionManager.getInstance().registerAction(refreshAction);

        IAction queryNewAction =
                new QueryNewAction(
                        shell,
                        com.cubrid.common.ui.spi.Messages.queryNewActionName,
                        CommonUIPlugin.getImageDescriptor("icons/action/new_query.png"),
                        CommonUIPlugin.getImageDescriptor("icons/action/new_query_disabled.png"));
        ActionManager.getInstance().registerAction(queryNewAction);

        IAction queryNewCustomAction =
                new QueryNewCustomAction(
                        shell,
                        com.cubrid.common.ui.spi.Messages.queryNewCustomActionName,
                        CommonUIPlugin.getImageDescriptor("icons/action/new_query.png"),
                        CommonUIPlugin.getImageDescriptor("icons/action/new_query_disabled.png"));
        ActionManager.getInstance().registerAction(queryNewCustomAction);

        // database query connection action
        IAction addQueryConnAction =
                new NewQueryConnAction(
                        shell,
                        Messages.createConnActionName,
                        CubridQueryUIPlugin.getImageDescriptor(
                                "icons/action/connection_create.png"));
        ActionManager.getInstance().registerAction(addQueryConnAction);

        IAction dropQueryConnAction =
                new DeleteQueryConnAction(
                        shell,
                        Messages.dropConnActionName,
                        CubridQueryUIPlugin.getImageDescriptor(
                                "icons/action/connection_delete.png"));
        ActionManager.getInstance().registerAction(dropQueryConnAction);

        IAction openQueryConnAction =
                new OpenQueryConnAction(
                        shell,
                        Messages.openConnActionName,
                        CubridQueryUIPlugin.getImageDescriptor("icons/action/connection_open.png"));
        ActionManager.getInstance().registerAction(openQueryConnAction);

        IAction editQueryConnAction =
                new EditQueryConnAction(
                        shell,
                        Messages.editConnActionName,
                        CubridQueryUIPlugin.getImageDescriptor("icons/action/connection_edit.png"));
        ActionManager.getInstance().registerAction(editQueryConnAction);

        IAction closeQueryConnAction =
                new CloseQueryConnAction(
                        shell,
                        Messages.closeConnActionName,
                        CubridQueryUIPlugin.getImageDescriptor(
                                "icons/action/connection_close.png"));
        ActionManager.getInstance().registerAction(closeQueryConnAction);

        IAction copyQueryConnAction =
                new CopyQueryConnAction(shell, Messages.copyConnActionName, null);
        ActionManager.getInstance().registerAction(copyQueryConnAction);

        IAction pasteQueryConnAction =
                new PasteQueryConnAction(shell, Messages.pasteConnActionName, null);
        ActionManager.getInstance().registerAction(pasteQueryConnAction);

        IAction renameConnectionAction =
                new RenameConnectionAction(shell, Messages.pasteConnActionName, null);
        ActionManager.getInstance().registerAction(renameConnectionAction);

        IAction viewServerVersionAction =
                new ViewDatabaseVersionAction(
                        shell,
                        Messages.viewDatabaseVersionActionName,
                        CubridQueryUIPlugin.getImageDescriptor("icons/action/menu_version.png"),
                        CubridQueryUIPlugin.getImageDescriptor(
                                "icons/action/menu_version_disabled.png"));
        ActionManager.getInstance().registerAction(viewServerVersionAction);

        IAction groupSettingAction =
                new CQBGroupSettingAction(
                        shell,
                        com.cubrid.common.ui.spi.Messages.groupSettingAction,
                        CommonUIPlugin.getImageDescriptor("icons/navigator/group_edit.png"),
                        CommonUIPlugin.getImageDescriptor(
                                "icons/navigator/group_edit_disabled.png"),
                        null);
        ActionManager.getInstance().registerAction(groupSettingAction);

        GroupPropertyAction groupPropertyAction =
                new GroupPropertyAction(
                        shell, com.cubrid.common.ui.spi.Messages.groupNodeProperty, null, null);
        groupPropertyAction.setNavigatorViewId(CubridQueryNavigatorView.ID);
        ActionManager.getInstance().registerAction(groupPropertyAction);

        ImportConnsAction importConnsAction =
                new ImportConnsAction(
                        Messages.importConnsAction,
                        CommonUIPlugin.getImageDescriptor("icons/action/table_data_import.png"));
        ActionManager.getInstance().registerAction(importConnsAction);

        IAction connectionUrlImportAction =
                new ConnectionUrlImportAction(
                        shell,
                        Messages.createConnByURLActionName,
                        CubridQueryUIPlugin.getImageDescriptor(
                                "icons/action/import_connection.png"));
        ActionManager.getInstance().registerAction(connectionUrlImportAction);

        IAction connectionExportAction =
                new ConnectionExportAction(
                        shell,
                        Messages.exportServerAction,
                        CommonUIPlugin.getImageDescriptor("icons/action/export_connection.png"));
        ActionManager.getInstance().registerAction(connectionExportAction);

        IAction importServerAction =
                new ConnectionImportAction(
                        shell,
                        Messages.importServerAction,
                        CommonUIPlugin.getImageDescriptor("icons/action/import_connection.png"));
        ActionManager.getInstance().registerAction(importServerAction);

        IAction brokerLogTopMergeAction =
                new BrokerLogTopMergeAction(Messages.brokerLogTopMergeAction);
        ActionManager.getInstance().registerAction(brokerLogTopMergeAction);

        IAction brokerLogParseAction = new BrokerLogParserAction(Messages.brokerLogParseAction);
        ActionManager.getInstance().registerAction(brokerLogParseAction);

        // cubrid broker conf  edit utility
        IAction cubridBrokerConfOpenFileAction =
                new BrokerConfOpenFileAction(Messages.cubridBrokerConfOpenFileActionName);
        ActionManager.getInstance().registerAction(cubridBrokerConfOpenFileAction);

        // Object Info tab on the query editor
        OpenTargetAction openObjectTabAction =
                new OpenTargetAction(
                        shell,
                        com.cubrid.common.ui.spi.Messages.msgQuickTabAction,
                        CommonUIPlugin.getImageDescriptor("icons/navigator/quick_tab.png"),
                        CommonUIPlugin.getImageDescriptor(
                                "icons/navigator/quick_tab_disabled.png"));
        ActionManager.getInstance().registerAction(openObjectTabAction);

        // database user related action
        IAction editUserAction =
                new EditUserAction(
                        shell,
                        Messages.editUserActionName,
                        CommonUIPlugin.getImageDescriptor("icons/action/user_edit.png"),
                        new CQBPersisteManager());
        ActionManager.getInstance().registerAction(editUserAction);

        IAction addUserAction =
                new AddUserAction(
                        shell,
                        Messages.addUserActionName,
                        CommonUIPlugin.getImageDescriptor("icons/action/user_add.png"),
                        CommonUIPlugin.getImageDescriptor("icons/action/user_add_disabled.png"));
        ActionManager.getInstance().registerAction(addUserAction);

        IAction deleteUserAction =
                new DeleteUserAction(
                        shell,
                        Messages.deleteUserActionName,
                        CommonUIPlugin.getImageDescriptor("icons/action/user_delete.png"));
        ActionManager.getInstance().registerAction(deleteUserAction);

        IAction action = null;

        action =
                new SchemaCommentInstallAction(
                        shell,
                        com.cubrid.common.ui.spi.Messages.schemaCommentInstallActionName,
                        CommonUIPlugin.getImageDescriptor("icons/navigator/quick_tab.png"),
                        null);
        ActionManager.getInstance().registerAction(action);

        action =
                new ChangeShardAction(
                        shell,
                        com.cubrid.common.ui.spi.Messages.changeShardActionName,
                        CommonUIPlugin.getImageDescriptor("icons/queryeditor/change_shard_id.png"));
        ActionManager.getInstance().registerAction(action);

        ActionManager.getInstance().setMenuProvider(new CubridMenuProvider());
    }
}
