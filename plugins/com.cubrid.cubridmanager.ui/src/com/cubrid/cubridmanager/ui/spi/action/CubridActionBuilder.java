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
package com.cubrid.cubridmanager.ui.spi.action;

import com.cubrid.common.ui.CommonUIPlugin;
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
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.broker.action.RestartBrokerAction;
import com.cubrid.cubridmanager.ui.broker.action.ShowBrokerEnvStatusAction;
import com.cubrid.cubridmanager.ui.broker.action.ShowBrokerStatusAction;
import com.cubrid.cubridmanager.ui.broker.action.StartBrokerAction;
import com.cubrid.cubridmanager.ui.broker.action.StartBrokerEnvAction;
import com.cubrid.cubridmanager.ui.broker.action.StopBrokerAction;
import com.cubrid.cubridmanager.ui.broker.action.StopBrokerEnvAction;
import com.cubrid.cubridmanager.ui.common.action.CMGroupSettingAction;
import com.cubrid.cubridmanager.ui.common.action.PropertyAction;
import com.cubrid.cubridmanager.ui.common.action.QueryNewAction;
import com.cubrid.cubridmanager.ui.common.action.QueryNewCustomAction;
import com.cubrid.cubridmanager.ui.common.action.RefreshAction;
import com.cubrid.cubridmanager.ui.common.action.StartRetargetAction;
import com.cubrid.cubridmanager.ui.common.action.StartServiceAction;
import com.cubrid.cubridmanager.ui.common.action.StopRetargetAction;
import com.cubrid.cubridmanager.ui.common.action.StopServiceAction;
import com.cubrid.cubridmanager.ui.common.action.UserManagementAction;
import com.cubrid.cubridmanager.ui.common.navigator.CubridHostNavigatorView;
import com.cubrid.cubridmanager.ui.cubrid.database.action.BackupDatabaseAction;
import com.cubrid.cubridmanager.ui.cubrid.database.action.CheckDatabaseAction;
import com.cubrid.cubridmanager.ui.cubrid.database.action.CompactDatabaseAction;
import com.cubrid.cubridmanager.ui.cubrid.database.action.CopyDatabaseAction;
import com.cubrid.cubridmanager.ui.cubrid.database.action.CreateDatabaseAction;
import com.cubrid.cubridmanager.ui.cubrid.database.action.DatabaseStatusViewAction;
import com.cubrid.cubridmanager.ui.cubrid.database.action.DeleteDatabaseAction;
import com.cubrid.cubridmanager.ui.cubrid.database.action.EditDatabaseLoginAction;
import com.cubrid.cubridmanager.ui.cubrid.database.action.LoadDatabaseAction;
import com.cubrid.cubridmanager.ui.cubrid.database.action.LockInfoAction;
import com.cubrid.cubridmanager.ui.cubrid.database.action.LoginDatabaseAction;
import com.cubrid.cubridmanager.ui.cubrid.database.action.LogoutDatabaseAction;
import com.cubrid.cubridmanager.ui.cubrid.database.action.OptimizeAction;
import com.cubrid.cubridmanager.ui.cubrid.database.action.ParamDumpAction;
import com.cubrid.cubridmanager.ui.cubrid.database.action.PlanDumpAction;
import com.cubrid.cubridmanager.ui.cubrid.database.action.RenameDatabaseAction;
import com.cubrid.cubridmanager.ui.cubrid.database.action.RestoreDatabaseAction;
import com.cubrid.cubridmanager.ui.cubrid.database.action.ShowDatabaseDashboardAction;
import com.cubrid.cubridmanager.ui.cubrid.database.action.StartDatabaseAction;
import com.cubrid.cubridmanager.ui.cubrid.database.action.StopDatabaseAction;
import com.cubrid.cubridmanager.ui.cubrid.database.action.TransactionInfoAction;
import com.cubrid.cubridmanager.ui.cubrid.database.action.UnloadDatabaseAction;
import com.cubrid.cubridmanager.ui.cubrid.dbspace.action.AddVolumeAction;
import com.cubrid.cubridmanager.ui.cubrid.dbspace.action.AutoAddVolumeLogAction;
import com.cubrid.cubridmanager.ui.cubrid.dbspace.action.SetAutoAddVolumeAction;
import com.cubrid.cubridmanager.ui.cubrid.dbspace.action.SpaceFolderViewAction;
import com.cubrid.cubridmanager.ui.cubrid.dbspace.action.SpaceInfoViewAction;
import com.cubrid.cubridmanager.ui.cubrid.jobauto.action.AddBackupPlanAction;
import com.cubrid.cubridmanager.ui.cubrid.jobauto.action.AddQueryPlanAction;
import com.cubrid.cubridmanager.ui.cubrid.jobauto.action.BackupErrLogAction;
import com.cubrid.cubridmanager.ui.cubrid.jobauto.action.DeleteBackupPlanAction;
import com.cubrid.cubridmanager.ui.cubrid.jobauto.action.DeleteQueryPlanAction;
import com.cubrid.cubridmanager.ui.cubrid.jobauto.action.EditBackupPlanAction;
import com.cubrid.cubridmanager.ui.cubrid.jobauto.action.EditQueryPlanAction;
import com.cubrid.cubridmanager.ui.cubrid.jobauto.action.QueryLogAction;
import com.cubrid.cubridmanager.ui.host.action.AddHostAction;
import com.cubrid.cubridmanager.ui.host.action.ChangeManagerPasswordAction;
import com.cubrid.cubridmanager.ui.host.action.ConnectHostAction;
import com.cubrid.cubridmanager.ui.host.action.CopyHostAction;
import com.cubrid.cubridmanager.ui.host.action.CubridServerExportAction;
import com.cubrid.cubridmanager.ui.host.action.CubridServerImportAction;
import com.cubrid.cubridmanager.ui.host.action.DeleteHostAction;
import com.cubrid.cubridmanager.ui.host.action.DisConnectHostAction;
import com.cubrid.cubridmanager.ui.host.action.EasyHAAction;
import com.cubrid.cubridmanager.ui.host.action.EditBrokerConfigAction;
import com.cubrid.cubridmanager.ui.host.action.EditCmConfigAction;
import com.cubrid.cubridmanager.ui.host.action.EditCubridConfigAction;
import com.cubrid.cubridmanager.ui.host.action.EditHAConfigAction;
import com.cubrid.cubridmanager.ui.host.action.EditHostAction;
import com.cubrid.cubridmanager.ui.host.action.ExportBrokerConfigAction;
import com.cubrid.cubridmanager.ui.host.action.ExportCmConfigAction;
import com.cubrid.cubridmanager.ui.host.action.ExportCubridConfigAction;
import com.cubrid.cubridmanager.ui.host.action.ExportHAConfigAction;
import com.cubrid.cubridmanager.ui.host.action.HostDashboardAction;
import com.cubrid.cubridmanager.ui.host.action.ImportBrokerConfigAction;
import com.cubrid.cubridmanager.ui.host.action.ImportCmConfigAction;
import com.cubrid.cubridmanager.ui.host.action.ImportCubridConfigAction;
import com.cubrid.cubridmanager.ui.host.action.ImportHAConfigAction;
import com.cubrid.cubridmanager.ui.host.action.ImportHostsAction;
import com.cubrid.cubridmanager.ui.host.action.PasteHostAction;
import com.cubrid.cubridmanager.ui.host.action.RenameHostAction;
import com.cubrid.cubridmanager.ui.host.action.UnifyHostConfigAction;
import com.cubrid.cubridmanager.ui.host.action.ViewServerVersionAction;
import com.cubrid.cubridmanager.ui.logs.action.AnalyzeSqlLogAction;
import com.cubrid.cubridmanager.ui.logs.action.ExecuteSqlLogAction;
import com.cubrid.cubridmanager.ui.logs.action.LogPropertyAction;
import com.cubrid.cubridmanager.ui.logs.action.LogViewAction;
import com.cubrid.cubridmanager.ui.logs.action.ManagerLogViewAction;
import com.cubrid.cubridmanager.ui.logs.action.RemoveAllAccessLogAction;
import com.cubrid.cubridmanager.ui.logs.action.RemoveAllDbLogAction;
import com.cubrid.cubridmanager.ui.logs.action.RemoveAllErrorLogAction;
import com.cubrid.cubridmanager.ui.logs.action.RemoveAllManagerLogAction;
import com.cubrid.cubridmanager.ui.logs.action.RemoveAllScriptLogAction;
import com.cubrid.cubridmanager.ui.logs.action.RemoveLogAction;
import com.cubrid.cubridmanager.ui.logs.action.ResetAdminLogAction;
import com.cubrid.cubridmanager.ui.logs.action.TimeSetAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.AddBrokerMonitorAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.AddDatabaseMonitorAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.AddHostMonitorAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.AddMonitorDashboardAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.ClearNodeErrorMsgAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.DashboardRefreshAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.DbDashboardHistoryAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.DeleteBrokerMonitorAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.DeleteDatabaseMonitorAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.DeleteHostMonitorAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.DeleteMonitorDashboardAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.EditAliasNameAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.EditMonitorDashboardAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.HARoleChangeAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.HideHostAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.HostDashboardHistoryAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.MinimizeFigureAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.MonitorDetailAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.OpenApplyLogDBLogAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.OpenCopyLogDBLogAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.OpenDatabaseLogAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.OpenMonitorDashboardAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.OpenMonitorDashboardViewAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.ShowBrokerClientAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.ShowBrokerDabaseAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.ShowHostAction;
import com.cubrid.cubridmanager.ui.monitoring.action.AddMonitorInstanceAction;
import com.cubrid.cubridmanager.ui.monitoring.action.AddStatusMonitorTemplateAction;
import com.cubrid.cubridmanager.ui.monitoring.action.DelMonitorInstanceAction;
import com.cubrid.cubridmanager.ui.monitoring.action.DeleteStatusMonitorTemplateAction;
import com.cubrid.cubridmanager.ui.monitoring.action.EditMonitorInstanceAction;
import com.cubrid.cubridmanager.ui.monitoring.action.EditStatusMonitorTemplateAction;
import com.cubrid.cubridmanager.ui.monitoring.action.ShowBrokerMonitorHistoryAction;
import com.cubrid.cubridmanager.ui.monitoring.action.ShowDatabaseMonitorHistoryAction;
import com.cubrid.cubridmanager.ui.monitoring.action.ShowDbSystemMonitorHistoryAction;
import com.cubrid.cubridmanager.ui.monitoring.action.ShowHostSystemMonitorHistoryAction;
import com.cubrid.cubridmanager.ui.monitoring.action.ShowStatusMonitorAction;
import com.cubrid.cubridmanager.ui.monitoring.action.ShowSystemMonitorAction;
import com.cubrid.cubridmanager.ui.monstatistic.action.AddMonitorStatisticPageAction;
import com.cubrid.cubridmanager.ui.monstatistic.action.CheckMonitorIntervalAction;
import com.cubrid.cubridmanager.ui.monstatistic.action.DeleteMonitorStatisticPageAction;
import com.cubrid.cubridmanager.ui.monstatistic.action.OpenMonitorStatisticPageAction;
import com.cubrid.cubridmanager.ui.replication.action.ChangeMasterDbAction;
import com.cubrid.cubridmanager.ui.replication.action.ChangeReplicationSchemaAction;
import com.cubrid.cubridmanager.ui.replication.action.ChangeSlaveDbAction;
import com.cubrid.cubridmanager.ui.replication.action.ConfigureReplicationParamAction;
import com.cubrid.cubridmanager.ui.replication.action.CreateReplicationAction;
import com.cubrid.cubridmanager.ui.replication.action.MonitorReplicationPerfAction;
import com.cubrid.cubridmanager.ui.replication.action.StartReplicationAgentAction;
import com.cubrid.cubridmanager.ui.replication.action.StartReplicationServerAction;
import com.cubrid.cubridmanager.ui.replication.action.StartSlaveDbAction;
import com.cubrid.cubridmanager.ui.replication.action.StopReplicationAgentAction;
import com.cubrid.cubridmanager.ui.replication.action.StopReplicationServerAction;
import com.cubrid.cubridmanager.ui.replication.action.StopSlaveDbAction;
import com.cubrid.cubridmanager.ui.replication.action.ViewReplicationAction;
import com.cubrid.cubridmanager.ui.replication.action.ViewReplicationErrorLogAction;
import com.cubrid.cubridmanager.ui.service.action.ServiceDashboardAction;
import com.cubrid.cubridmanager.ui.shard.action.AddShardAction;
import com.cubrid.cubridmanager.ui.shard.action.ShowShardStatusAction;
import com.cubrid.cubridmanager.ui.shard.action.StartShardAction;
import com.cubrid.cubridmanager.ui.shard.action.StartShardEnvAction;
import com.cubrid.cubridmanager.ui.shard.action.StopShardAction;
import com.cubrid.cubridmanager.ui.shard.action.StopShardEnvAction;
import com.cubrid.cubridmanager.ui.spi.Messages;
import com.cubrid.cubridmanager.ui.spi.persist.CMPersistManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Shell;

/**
 * This class is responsible to build CUBRID Manager menu and toolbar action
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class CubridActionBuilder extends ActionBuilder {
    private static CubridActionBuilder instance;

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
        // customized actions for CUBRID Manager
        // common action
        IAction propertyAction =
                new PropertyAction(
                        shell,
                        Messages.propertyActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/property.png"));
        ActionManager.getInstance().registerAction(propertyAction);

        IAction userManagementAction =
                new UserManagementAction(
                        shell,
                        Messages.userManagementActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/menu_cmuser.png"));
        ActionManager.getInstance().registerAction(userManagementAction);

        IAction refreshAction =
                new RefreshAction(
                        shell,
                        Messages.refreshActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/refresh.png"),
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/refresh_disabled.png"));
        ActionManager.getInstance().registerAction(refreshAction);

        IAction startServiceAction =
                new StartServiceAction(
                        shell,
                        Messages.startServiceActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/host_service_start.png"),
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/host_service_start_disabled.png"));
        ActionManager.getInstance().registerAction(startServiceAction);

        IAction stopServiceAction =
                new StopServiceAction(
                        shell,
                        Messages.stopServiceActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/host_service_stop.png"),
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/host_service_stop_disabled.png"));
        ActionManager.getInstance().registerAction(stopServiceAction);

        IAction startActionBig =
                new StartRetargetAction(
                        shell,
                        Messages.startActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/menu_start.png"),
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/menu_start_disabled.png"),
                        true);
        ActionManager.getInstance().registerAction(startActionBig);

        IAction queryNewAction =
                new QueryNewAction(
                        shell,
                        com.cubrid.common.ui.spi.Messages.queryNewActionName,
                        CommonUIPlugin.getImageDescriptor("icons/action/new_query.png"),
                        CommonUIPlugin.getImageDescriptor("icons/action/new_query_disabled.png"),
                        false);
        ActionManager.getInstance().registerAction(queryNewAction);

        IAction queryNewActionBig =
                new QueryNewAction(
                        shell,
                        com.cubrid.common.ui.spi.Messages.queryNewActionName,
                        CommonUIPlugin.getImageDescriptor("icons/action/new_query_big.png"),
                        CommonUIPlugin.getImageDescriptor(
                                "icons/action/new_query_big_disabled.png"),
                        true);
        ActionManager.getInstance().registerAction(queryNewActionBig);

        IAction queryNewCustomAction =
                new QueryNewCustomAction(
                        shell,
                        com.cubrid.common.ui.spi.Messages.queryNewCustomActionName,
                        CommonUIPlugin.getImageDescriptor("icons/action/new_query.png"),
                        CommonUIPlugin.getImageDescriptor("icons/action/new_query_disabled.png"));
        ActionManager.getInstance().registerAction(queryNewCustomAction);

        // host related action
        IAction addHostAction =
                new AddHostAction(
                        shell,
                        Messages.addHostActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/host_add.png"),
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/host_add_disabled.png"),
                        false);
        ActionManager.getInstance().registerAction(addHostAction);

        // host related action(Big)
        IAction addHostActionBig =
                new AddHostAction(
                        shell,
                        Messages.addHostActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/add_host_big.png"),
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/add_host_big_disabled.png"),
                        true);
        ActionManager.getInstance().registerAction(addHostActionBig);

        IAction deleteHostAction =
                new DeleteHostAction(
                        shell,
                        Messages.deleteHostActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/host_delete.png"));
        ActionManager.getInstance().registerAction(deleteHostAction);

        IAction connectHostAction =
                new ConnectHostAction(
                        shell,
                        Messages.connectHostActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/host_connect.png"));
        ActionManager.getInstance().registerAction(connectHostAction);

        IAction editHostAction =
                new EditHostAction(
                        shell,
                        Messages.editHostActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/navigator/host.png"));
        ActionManager.getInstance().registerAction(editHostAction);

        IAction disConnectHostAction =
                new DisConnectHostAction(
                        shell,
                        Messages.disConnectHostActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/host_disconnect.png"));
        ActionManager.getInstance().registerAction(disConnectHostAction);

        IAction copyHostAction = new CopyHostAction(shell, Messages.copyHostActionName, null);
        ActionManager.getInstance().registerAction(copyHostAction);

        IAction pasteHostAction = new PasteHostAction(shell, Messages.pasteHostActionName, null);
        ActionManager.getInstance().registerAction(pasteHostAction);

        IAction changePasswordAction =
                new ChangeManagerPasswordAction(
                        shell,
                        Messages.changePasswordActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/host_change_password.png"));
        ActionManager.getInstance().registerAction(changePasswordAction);

        IAction hostDashboardAction =
                new HostDashboardAction(
                        shell,
                        Messages.hostDashBoardActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/navigator/database_dashboard.png"));
        ActionManager.getInstance().registerAction(hostDashboardAction);

        IAction viewServerVersionAction =
                new ViewServerVersionAction(
                        shell,
                        Messages.viewServerVersionActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/menu_version.png"),
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/menu_version_disabled.png"));
        ActionManager.getInstance().registerAction(viewServerVersionAction);

        IAction editCubridConfigAction =
                new EditCubridConfigAction(
                        shell,
                        Messages.editCubridConf,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/property.png"),
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/property.png"));
        ActionManager.getInstance().registerAction(editCubridConfigAction);

        IAction importCubridConfigAction =
                new ImportCubridConfigAction(
                        shell,
                        Messages.imortCubridConf,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/conf_import.png"),
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/conf_import.png"));
        ActionManager.getInstance().registerAction(importCubridConfigAction);

        IAction exportCubridConfigAction =
                new ExportCubridConfigAction(
                        shell,
                        Messages.exportCubridConf,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/conf_export.png"),
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/conf_export.png"));
        ActionManager.getInstance().registerAction(exportCubridConfigAction);

        IAction editBrokerConfigAction =
                new EditBrokerConfigAction(
                        shell,
                        Messages.editBrokerConf,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/property.png"),
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/property.png"));
        ActionManager.getInstance().registerAction(editBrokerConfigAction);

        IAction importBrokerConfigAction =
                new ImportBrokerConfigAction(
                        shell,
                        Messages.importBrokerConf,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/conf_import.png"),
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/conf_import.png"));
        ActionManager.getInstance().registerAction(importBrokerConfigAction);

        IAction exportBrokerConfigAction =
                new ExportBrokerConfigAction(
                        shell,
                        Messages.exportBrokerConf,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/conf_export.png"),
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/conf_export.png"));
        ActionManager.getInstance().registerAction(exportBrokerConfigAction);

        IAction editCmConfigAction =
                new EditCmConfigAction(
                        shell,
                        Messages.editCmConf,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/property.png"),
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/property.png"));
        ActionManager.getInstance().registerAction(editCmConfigAction);

        IAction importCmConfigAction =
                new ImportCmConfigAction(
                        shell,
                        Messages.importComConf,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/conf_import.png"),
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/conf_import.png"));
        ActionManager.getInstance().registerAction(importCmConfigAction);

        IAction exportCmConfigAction =
                new ExportCmConfigAction(
                        shell,
                        Messages.exportCmConf,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/conf_export.png"),
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/conf_export.png"));
        ActionManager.getInstance().registerAction(exportCmConfigAction);

        // TODO -KK
        IAction configHAAction =
                new EasyHAAction(
                        shell,
                        Messages.easyHAWizard,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/property.png"),
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/property.png"));
        ActionManager.getInstance().registerAction(configHAAction);

        IAction editHAConfigAction =
                new EditHAConfigAction(
                        shell,
                        Messages.editHaConf,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/property.png"),
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/property.png"));
        ActionManager.getInstance().registerAction(editHAConfigAction);

        IAction importHAConfigAction =
                new ImportHAConfigAction(
                        shell,
                        Messages.importHaConf,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/conf_import.png"),
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/conf_import.png"));
        ActionManager.getInstance().registerAction(importHAConfigAction);

        IAction exportHAConfigAction =
                new ExportHAConfigAction(
                        shell,
                        Messages.exportHaConf,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/conf_export.png"),
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/conf_export.png"));
        ActionManager.getInstance().registerAction(exportHAConfigAction);

        // database related action
        IAction createDatabaseAction =
                new CreateDatabaseAction(
                        shell,
                        Messages.createDatabaseActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/database_create.png"),
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/database_create_disabled.png"));
        ActionManager.getInstance().registerAction(createDatabaseAction);

        IAction loginDatabaseAction =
                new LoginDatabaseAction(
                        shell,
                        Messages.loginDatabaseActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/database_login.png"));
        ActionManager.getInstance().registerAction(loginDatabaseAction);

        IAction editDatabaseLoginAction =
                new EditDatabaseLoginAction(
                        shell,
                        Messages.editDatabaseLoginActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/database_login.png"));
        ActionManager.getInstance().registerAction(editDatabaseLoginAction);

        IAction logoutDatabaseAction =
                new LogoutDatabaseAction(
                        shell,
                        Messages.logoutDatabaseActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/database_logout.png"));
        ActionManager.getInstance().registerAction(logoutDatabaseAction);

        IAction startDatabaseAction =
                new StartDatabaseAction(
                        shell,
                        Messages.startDatabaseActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/database_start.png"));
        ActionManager.getInstance().registerAction(startDatabaseAction);

        IAction stopDatabaseAction =
                new StopDatabaseAction(
                        shell,
                        Messages.stopDatabaseActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/database_stop.png"));
        ActionManager.getInstance().registerAction(stopDatabaseAction);

        IAction loadDatabaseAction =
                new LoadDatabaseAction(
                        shell,
                        Messages.loadDatabaseActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/database_load.png"),
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/database_load_disabled.png"));
        ActionManager.getInstance().registerAction(loadDatabaseAction);

        IAction unloadDatabaseAction =
                new UnloadDatabaseAction(
                        shell,
                        Messages.unloadDatabaseActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/database_unload.png"),
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/database_unload_disabled.png"));
        ActionManager.getInstance().registerAction(unloadDatabaseAction);

        IAction backupDatabaseAction =
                new BackupDatabaseAction(
                        shell,
                        Messages.backupDatabaseActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/database_backup.png"),
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/database_backup_disabled.png"));
        ActionManager.getInstance().registerAction(backupDatabaseAction);

        IAction restoreDatabaseAction =
                new RestoreDatabaseAction(
                        shell,
                        Messages.restoreDatabaseActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/database_restore.png"),
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/database_restore_disabled.png"));
        ActionManager.getInstance().registerAction(restoreDatabaseAction);

        IAction renameDatabaseAction =
                new RenameDatabaseAction(
                        shell,
                        Messages.renameDatabaseActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/database_rename.png"));
        ActionManager.getInstance().registerAction(renameDatabaseAction);

        IAction copyDatabaseAction =
                new CopyDatabaseAction(
                        shell,
                        Messages.copyDatabaseActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/database_copy.png"));
        ActionManager.getInstance().registerAction(copyDatabaseAction);

        IAction databaseStatusViewAction =
                new DatabaseStatusViewAction(shell, Messages.databaseStatusViewActionName, null);
        ActionManager.getInstance().registerAction(databaseStatusViewAction);

        IAction showDatabaseDashboardAction =
                new ShowDatabaseDashboardAction(
                        shell,
                        Messages.databaseDashboardViewActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/navigator/database_dashboard.png"));
        ActionManager.getInstance().registerAction(showDatabaseDashboardAction);

        IAction planDumpAction =
                new PlanDumpAction(
                        shell,
                        Messages.planDumpActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/database_compact.png"));
        ActionManager.getInstance().registerAction(planDumpAction);

        IAction paramDumpAction =
                new ParamDumpAction(
                        shell,
                        Messages.paramDumpActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/database_compact.png"));
        ActionManager.getInstance().registerAction(paramDumpAction);

        IAction lockInfoAction =
                new LockInfoAction(
                        shell,
                        Messages.lockInfoActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/database_lockinfo.png"),
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/database_lockinfo_disabled.png"));
        ActionManager.getInstance().registerAction(lockInfoAction);

        IAction transactionInfoAction =
                new TransactionInfoAction(
                        shell,
                        Messages.transactionInfoActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/database_traninfo.png"),
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/database_traninfo_disabled.png"));
        ActionManager.getInstance().registerAction(transactionInfoAction);

        IAction deleteDatabaseAction =
                new DeleteDatabaseAction(
                        shell,
                        Messages.deleteDatabaseActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/database_delete.png"));
        ActionManager.getInstance().registerAction(deleteDatabaseAction);

        IAction checkDatabaseAction =
                new CheckDatabaseAction(
                        shell,
                        Messages.checkDatabaseActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/database_check.png"),
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/database_check_disabled.png"));
        ActionManager.getInstance().registerAction(checkDatabaseAction);

        IAction optimizeAction =
                new OptimizeAction(
                        shell,
                        Messages.optimizeActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/database_optimize.png"),
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/database_optimize_disabled.png"));
        ActionManager.getInstance().registerAction(optimizeAction);

        IAction compactDatabaseAction =
                new CompactDatabaseAction(
                        shell,
                        Messages.compactDatabaseActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/database_compact.png"));
        ActionManager.getInstance().registerAction(compactDatabaseAction);

        // database user related action
        IAction editUserAction =
                new EditUserAction(
                        shell,
                        Messages.editUserActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/user_edit.png"),
                        new CMPersistManager());
        ActionManager.getInstance().registerAction(editUserAction);

        IAction addUserAction =
                new AddUserAction(
                        shell,
                        Messages.addUserActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/user_add.png"),
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/user_add_disabled.png"));
        ActionManager.getInstance().registerAction(addUserAction);

        IAction deleteUserAction =
                new DeleteUserAction(
                        shell,
                        Messages.deleteUserActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/user_delete.png"));
        ActionManager.getInstance().registerAction(deleteUserAction);

        // job auto related action
        IAction addBackupPlanAction =
                new AddBackupPlanAction(
                        shell,
                        Messages.addBackupPlanActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/auto_backup_add.png"));
        ActionManager.getInstance().registerAction(addBackupPlanAction);

        IAction editBackupPlanAction =
                new EditBackupPlanAction(
                        shell,
                        Messages.editBackupPlanActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/auto_backup_edit.png"));
        ActionManager.getInstance().registerAction(editBackupPlanAction);

        IAction deleteBackupPlanAction =
                new DeleteBackupPlanAction(
                        shell,
                        Messages.deleteBackupPlanActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/auto_backup_delete.png"));
        ActionManager.getInstance().registerAction(deleteBackupPlanAction);

        IAction backUpErrLogAction =
                new BackupErrLogAction(
                        shell,
                        Messages.backUpErrLogActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/auto_log.png"));
        ActionManager.getInstance().registerAction(backUpErrLogAction);

        IAction addQueryPlanAction =
                new AddQueryPlanAction(
                        shell,
                        Messages.addQueryPlanActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/auto_query_add.png"));
        ActionManager.getInstance().registerAction(addQueryPlanAction);

        IAction editQueryPlanAction =
                new EditQueryPlanAction(
                        shell,
                        Messages.editQueryPlanActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/auto_query_edit.png"));
        ActionManager.getInstance().registerAction(editQueryPlanAction);

        IAction deleteQueryPlanAction =
                new DeleteQueryPlanAction(
                        shell,
                        Messages.deleteQueryPlanActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/auto_query_delete.png"));
        ActionManager.getInstance().registerAction(deleteQueryPlanAction);

        IAction queryLogAction =
                new QueryLogAction(
                        shell,
                        Messages.queryPlanLogActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/auto_log.png"));
        ActionManager.getInstance().registerAction(queryLogAction);

        // database space related action
        IAction setAutoAddVolumeAction =
                new SetAutoAddVolumeAction(
                        shell,
                        Messages.setAutoAddVolumeActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/volume_auto_add.png"));
        ActionManager.getInstance().registerAction(setAutoAddVolumeAction);

        IAction addVolumeAction =
                new AddVolumeAction(
                        shell,
                        Messages.setAddVolumeActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/volume_add.png"));
        ActionManager.getInstance().registerAction(addVolumeAction);

        IAction autoAddVolumeLogAction =
                new AutoAddVolumeLogAction(
                        shell,
                        Messages.autoAddVolumeLogActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/auto_log.png"));
        ActionManager.getInstance().registerAction(autoAddVolumeLogAction);

        IAction spaceFolderViewAction =
                new SpaceFolderViewAction(shell, Messages.spaceFolderViewActionName, null);
        ActionManager.getInstance().registerAction(spaceFolderViewAction);

        IAction spaceInfoViewAction =
                new SpaceInfoViewAction(shell, Messages.spaceInfoViewActionName, null);
        ActionManager.getInstance().registerAction(spaceInfoViewAction);

        // status monitor related action
        IAction addStatusMonitorAction =
                new AddStatusMonitorTemplateAction(
                        shell,
                        Messages.addStatusMonitorActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/status_add.png"));
        ActionManager.getInstance().registerAction(addStatusMonitorAction);

        IAction editStatusMonitorAction =
                new EditStatusMonitorTemplateAction(
                        shell,
                        Messages.editStatusMonitorActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/status_edit.png"));
        ActionManager.getInstance().registerAction(editStatusMonitorAction);

        IAction delStatusMonitorAction =
                new DeleteStatusMonitorTemplateAction(
                        shell,
                        Messages.delStatusMonitorActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/status_delete.png"));
        ActionManager.getInstance().registerAction(delStatusMonitorAction);

        // for version>8.2.2
        IAction addMonitorInstanceAction =
                new AddMonitorInstanceAction(
                        shell,
                        Messages.addStatusMonitorActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/status_add.png"));
        ActionManager.getInstance().registerAction(addMonitorInstanceAction);

        IAction showBrokerMonitorHistoryAction =
                new ShowBrokerMonitorHistoryAction(
                        shell,
                        Messages.viewBrokerMonitorHistoryActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/status_execute.png"));
        ActionManager.getInstance().registerAction(showBrokerMonitorHistoryAction);

        IAction showDbMonitorHistoryAction =
                new ShowDatabaseMonitorHistoryAction(
                        shell,
                        Messages.viewDbMonitorHistoryActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/status_execute.png"));
        ActionManager.getInstance().registerAction(showDbMonitorHistoryAction);

        IAction editMonitorInstanceAction =
                new EditMonitorInstanceAction(
                        shell,
                        Messages.editStatusMonitorActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/status_edit.png"));
        ActionManager.getInstance().registerAction(editMonitorInstanceAction);

        IAction delMonitorInstanceAction =
                new DelMonitorInstanceAction(
                        shell,
                        Messages.delStatusMonitorActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/status_delete.png"));
        ActionManager.getInstance().registerAction(delMonitorInstanceAction);

        IAction showStatusMonitorAction =
                new ShowStatusMonitorAction(
                        shell,
                        Messages.viewStatusMonitorActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/status_execute.png"));
        ActionManager.getInstance().registerAction(showStatusMonitorAction);

        IAction showSystemMonitorAction =
                new ShowSystemMonitorAction(
                        shell,
                        Messages.viewSystemMonitorActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/status_execute.png"));
        ActionManager.getInstance().registerAction(showSystemMonitorAction);

        IAction showHostSystemMonitorHistoryAction =
                new ShowHostSystemMonitorHistoryAction(
                        shell,
                        Messages.viewHostSysMonHistoryActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/status_execute.png"));
        ActionManager.getInstance().registerAction(showHostSystemMonitorHistoryAction);

        IAction showDbSystemMonitorHistoryAction =
                new ShowDbSystemMonitorHistoryAction(
                        shell,
                        Messages.viewDbSysMonHistoryActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/status_execute.png"));
        ActionManager.getInstance().registerAction(showDbSystemMonitorHistoryAction);

        IAction addMonitorStatisticPage =
                new AddMonitorStatisticPageAction(
                        shell,
                        Messages.addMonStatisticPageActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/status_add.png"));
        ActionManager.getInstance().registerAction(addMonitorStatisticPage);

        IAction deleteMonitorStatisticPage =
                new DeleteMonitorStatisticPageAction(
                        shell,
                        Messages.deleteMonStatisticPageActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/status_delete.png"));
        ActionManager.getInstance().registerAction(deleteMonitorStatisticPage);

        IAction openMonitorStatisticPage =
                new OpenMonitorStatisticPageAction(
                        shell,
                        Messages.openMonStatisticPageActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/status_execute.png"));
        ActionManager.getInstance().registerAction(openMonitorStatisticPage);

        IAction checkMonitorIntervalAction =
                new CheckMonitorIntervalAction(
                        shell,
                        Messages.checkMonitorIntervalActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/log_delete.png"));
        ActionManager.getInstance().registerAction(checkMonitorIntervalAction);

        // logs related action
        IAction removeAllAccessLogAction =
                new RemoveAllAccessLogAction(
                        shell,
                        Messages.removeAllAccessLogActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/log_delete_all.png"));
        ActionManager.getInstance().registerAction(removeAllAccessLogAction);

        IAction removeAllErrorLogAction =
                new RemoveAllErrorLogAction(
                        shell,
                        Messages.removeAllErrorLogActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/log_delete_all.png"));
        ActionManager.getInstance().registerAction(removeAllErrorLogAction);

        IAction removeAllScriptLogAction =
                new RemoveAllScriptLogAction(
                        shell,
                        Messages.removeAllScriptLogActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/log_delete_all.png"));
        ActionManager.getInstance().registerAction(removeAllScriptLogAction);

        IAction removeAllLogAction =
                new RemoveAllDbLogAction(
                        shell,
                        Messages.removeAllLogActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/log_delete_all.png"));
        ActionManager.getInstance().registerAction(removeAllLogAction);

        IAction removeLogAction =
                new RemoveLogAction(
                        shell,
                        Messages.removeLogActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/log_delete.png"));
        ActionManager.getInstance().registerAction(removeLogAction);

        IAction logViewAction =
                new LogViewAction(
                        shell,
                        Messages.logViewActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/log_view.png"));
        ActionManager.getInstance().registerAction(logViewAction);

        IAction timeSetAction =
                new TimeSetAction(
                        shell,
                        Messages.timeSetActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/auto_log.png"));
        ActionManager.getInstance().registerAction(timeSetAction);

        IAction managerLogViewAction =
                new ManagerLogViewAction(
                        shell,
                        Messages.logViewActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/log_view.png"));
        ActionManager.getInstance().registerAction(managerLogViewAction);

        IAction removeAllManagerLogAction =
                new RemoveAllManagerLogAction(
                        shell,
                        Messages.removeLogActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/log_delete.png"));
        ActionManager.getInstance().registerAction(removeAllManagerLogAction);

        IAction logPropertyAction =
                new LogPropertyAction(
                        shell,
                        Messages.logPropertyActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/log_property.png"));
        ActionManager.getInstance().registerAction(logPropertyAction);

        IAction activityAnalyzeCasLogAction =
                new AnalyzeSqlLogAction(
                        shell,
                        Messages.activityAnalyzeCasLogActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/sqllog_analysis.png"));
        ActionManager.getInstance().registerAction(activityAnalyzeCasLogAction);

        IAction activityCasLogRunAction =
                new ExecuteSqlLogAction(
                        shell,
                        Messages.activityCasLogRunActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/sqllog_execute.png"));
        ActionManager.getInstance().registerAction(activityCasLogRunAction);

        IAction resetAdminLogAction =
                new ResetAdminLogAction(shell, Messages.resetAdminLogActionName, null);
        ActionManager.getInstance().registerAction(resetAdminLogAction);

        // shard related action
        IAction addShardAction = new AddShardAction(shell);
        ActionManager.getInstance().registerAction(addShardAction);

        IAction startShardEnvAction = new StartShardEnvAction(shell);
        ActionManager.getInstance().registerAction(startShardEnvAction);

        IAction stopShardEnvAction = new StopShardEnvAction(shell);
        ActionManager.getInstance().registerAction(stopShardEnvAction);

        IAction startShardAction = new StartShardAction(shell);
        ActionManager.getInstance().registerAction(startShardAction);

        IAction stopShardAction = new StopShardAction(shell);
        ActionManager.getInstance().registerAction(stopShardAction);

        IAction showShardStatusAction = new ShowShardStatusAction(shell);
        ActionManager.getInstance().registerAction(showShardStatusAction);

        // broker related action
        IAction startBrokerEnvAction =
                new StartBrokerEnvAction(
                        shell,
                        Messages.startBrokerEnvActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/broker_group_start.png"));
        ActionManager.getInstance().registerAction(startBrokerEnvAction);

        IAction stopBrokerEnvAction =
                new StopBrokerEnvAction(
                        shell,
                        Messages.stopBrokerEnvActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/broker_group_stop.png"));
        ActionManager.getInstance().registerAction(stopBrokerEnvAction);

        IAction startBrokerAction =
                new StartBrokerAction(
                        shell,
                        Messages.startBrokerActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/broker_start.png"));
        ActionManager.getInstance().registerAction(startBrokerAction);

        IAction restartBrokerAction =
                new RestartBrokerAction(shell, Messages.restartBrokerActionName, null);
        ActionManager.getInstance().registerAction(restartBrokerAction);

        IAction stopBrokerAction =
                new StopBrokerAction(
                        shell,
                        Messages.stopBrokerActionName,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/broker_stop.png"));
        ActionManager.getInstance().registerAction(stopBrokerAction);

        IAction showBrokersStatusAction =
                new ShowBrokerEnvStatusAction(shell, Messages.showBrokersStatusActionName);
        ActionManager.getInstance().registerAction(showBrokersStatusAction);

        IAction showBrokerStatusAction =
                new ShowBrokerStatusAction(shell, Messages.showBrokerStatusActionName);
        ActionManager.getInstance().registerAction(showBrokerStatusAction);

        IAction brokerLogTopMergeAction =
                new BrokerLogTopMergeAction(Messages.brokerLogTopMergeAction);
        ActionManager.getInstance().registerAction(brokerLogTopMergeAction);

        IAction brokerLogParseAction = new BrokerLogParserAction(Messages.brokerLogParseAction);
        ActionManager.getInstance().registerAction(brokerLogParseAction);

        // replication related action
        IAction createReplicationAction =
                new CreateReplicationAction(
                        shell,
                        Messages.createReplicationActionName,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/replication_create.gif"));
        ActionManager.getInstance().registerAction(createReplicationAction);

        IAction viewReplicationAction =
                new ViewReplicationAction(shell, Messages.viewReplicationActionName, null);
        ActionManager.getInstance().registerAction(viewReplicationAction);

        IAction startReplicationServerAction =
                new StartReplicationServerAction(
                        shell, Messages.startReplicationServerActionName, null);
        ActionManager.getInstance().registerAction(startReplicationServerAction);

        IAction stopReplicationServerAction =
                new StopReplicationServerAction(
                        shell, Messages.stopReplicationServerActionName, null);
        ActionManager.getInstance().registerAction(stopReplicationServerAction);

        IAction startReplicationAgentAction =
                new StartReplicationAgentAction(
                        shell, Messages.startReplicationAgentActionName, null);
        ActionManager.getInstance().registerAction(startReplicationAgentAction);

        IAction stopReplicationAgentAction =
                new StopReplicationAgentAction(
                        shell, Messages.stopReplicationAgentActionName, null);
        ActionManager.getInstance().registerAction(stopReplicationAgentAction);

        IAction startSlaveDbAction =
                new StartSlaveDbAction(shell, Messages.startSlaveDbActionName, null);
        ActionManager.getInstance().registerAction(startSlaveDbAction);

        IAction stopSlaveDbAction =
                new StopSlaveDbAction(shell, Messages.stopSlaveDbActionName, null);
        ActionManager.getInstance().registerAction(stopSlaveDbAction);

        IAction changeSlaveDbAction =
                new ChangeSlaveDbAction(shell, Messages.changeSlaveDbActionName, null);
        ActionManager.getInstance().registerAction(changeSlaveDbAction);

        IAction changeMasterDbAction =
                new ChangeMasterDbAction(shell, Messages.changeMasterDbActionName, null);
        ActionManager.getInstance().registerAction(changeMasterDbAction);

        IAction changeReplicationSchemaAction =
                new ChangeReplicationSchemaAction(
                        shell, Messages.changeReplicationSchemaActionName, null);
        ActionManager.getInstance().registerAction(changeReplicationSchemaAction);

        IAction configureReplicationParaAction =
                new ConfigureReplicationParamAction(
                        shell, Messages.configureReplicationParaActionName, null);
        ActionManager.getInstance().registerAction(configureReplicationParaAction);

        IAction monitorReplicationPerfAction =
                new MonitorReplicationPerfAction(
                        shell, Messages.monitorReplicationPerfActionName, null);
        ActionManager.getInstance().registerAction(monitorReplicationPerfAction);

        IAction viewReplicationErrorLogAction =
                new ViewReplicationErrorLogAction(
                        shell, Messages.viewReplicationErrorLogActionName, null);
        ActionManager.getInstance().registerAction(viewReplicationErrorLogAction);

        // monitoring dash board actions
        IAction openMonitorDashboardViewAction =
                new OpenMonitorDashboardViewAction(Messages.openMonitorViewActionName);
        ActionManager.getInstance().registerAction(openMonitorDashboardViewAction);

        IAction addMonitorDashboardAction =
                new AddMonitorDashboardAction(shell, Messages.addDashboardActionName, null);
        ActionManager.getInstance().registerAction(addMonitorDashboardAction);

        IAction deleteMonitorDashboardAction =
                new DeleteMonitorDashboardAction(shell, Messages.deleteDashboardActionName, null);
        ActionManager.getInstance().registerAction(deleteMonitorDashboardAction);

        IAction openMonitorDashboardAction =
                new OpenMonitorDashboardAction(shell, Messages.openDashboardActionName, null);
        ActionManager.getInstance().registerAction(openMonitorDashboardAction);

        IAction editMonitorDashboardAction =
                new EditMonitorDashboardAction(shell, Messages.editDashboardActionName, null);
        ActionManager.getInstance().registerAction(editMonitorDashboardAction);

        IAction addHostMonitorAction =
                new AddHostMonitorAction(shell, Messages.addHostMonitorAction, null);
        ActionManager.getInstance().registerAction(addHostMonitorAction);

        IAction dashboardRefreshAction =
                new DashboardRefreshAction(shell, Messages.dashboardRefreshAction, null);
        ActionManager.getInstance().registerAction(dashboardRefreshAction);

        // context menues used by host monitor figure
        IAction monitorDetailAction =
                new MonitorDetailAction(shell, Messages.monitorDetailAction, null);
        ActionManager.getInstance().registerAction(monitorDetailAction);

        IAction addDatabaseMonitorAction =
                new AddDatabaseMonitorAction(shell, Messages.addDatabaseMonitorAction, null);
        ActionManager.getInstance().registerAction(addDatabaseMonitorAction);

        IAction addBrokerMonitorAction =
                new AddBrokerMonitorAction(shell, Messages.addBrokerMonitorAction, null);
        ActionManager.getInstance().registerAction(addBrokerMonitorAction);

        IAction deleteHostMonitorAction =
                new DeleteHostMonitorAction(shell, Messages.deleteHostMonitorAction, null);
        ActionManager.getInstance().registerAction(deleteHostMonitorAction);

        IAction hostDashboardHistoryAction =
                new HostDashboardHistoryAction(shell, Messages.hostDashboardHistoryAction, null);
        ActionManager.getInstance().registerAction(hostDashboardHistoryAction);

        // context menus for database monitor figure
        IAction deleteDatabaseMonitorAction =
                new DeleteDatabaseMonitorAction(shell, Messages.deleteDatabaseMonitorAction, null);
        ActionManager.getInstance().registerAction(deleteDatabaseMonitorAction);

        IAction hARoleChangeAction =
                new HARoleChangeAction(shell, Messages.hARoleChangeAction, null);
        ActionManager.getInstance().registerAction(hARoleChangeAction);

        IAction openApplyLogDBLogAction =
                new OpenApplyLogDBLogAction(shell, Messages.openApplyLogDBLogAction, null);
        ActionManager.getInstance().registerAction(openApplyLogDBLogAction);

        IAction openCopyLogDBLogAction =
                new OpenCopyLogDBLogAction(shell, Messages.openCopyLogDBLogAction, null);
        ActionManager.getInstance().registerAction(openCopyLogDBLogAction);

        IAction openDatabaseLogAction =
                new OpenDatabaseLogAction(shell, Messages.openDatabaseLogAction, null);
        ActionManager.getInstance().registerAction(openDatabaseLogAction);

        IAction dbDashboardHistoryAction =
                new DbDashboardHistoryAction(shell, Messages.dbDashboardHistoryAction, null);
        ActionManager.getInstance().registerAction(dbDashboardHistoryAction);

        IAction editNickNameAction =
                new EditAliasNameAction(shell, Messages.editNickNameAction, null);
        ActionManager.getInstance().registerAction(editNickNameAction);

        // context menus for broker monitor figure
        IAction showBrokerClientAction =
                new ShowBrokerClientAction(shell, Messages.showBrokerClientAction, null);
        ActionManager.getInstance().registerAction(showBrokerClientAction);
        IAction showBrokerDabaseAction =
                new ShowBrokerDabaseAction(shell, Messages.showBrokerDabaseAction, null);
        ActionManager.getInstance().registerAction(showBrokerDabaseAction);

        //
        IAction minimizeFigureAction =
                new MinimizeFigureAction(shell, Messages.minimizeFigureAction, null);
        ActionManager.getInstance().registerAction(minimizeFigureAction);

        HideHostAction hideHostAction = new HideHostAction(shell, Messages.hideHostAction, null);
        ActionManager.getInstance().registerAction(hideHostAction);

        ShowHostAction showHostAction = new ShowHostAction(shell, Messages.showHostAction, null);
        ActionManager.getInstance().registerAction(showHostAction);

        DeleteBrokerMonitorAction deleteBrokerMonitorAction =
                new DeleteBrokerMonitorAction(shell, Messages.deleteBrokerMonitorAction, null);
        ActionManager.getInstance().registerAction(deleteBrokerMonitorAction);

        ClearNodeErrorMsgAction clearNodeErrorMsgAction =
                new ClearNodeErrorMsgAction(shell, Messages.clearNodeErrorMsgAction, null);
        ActionManager.getInstance().registerAction(clearNodeErrorMsgAction);

        IAction groupSettingAction =
                new CMGroupSettingAction(
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
        groupPropertyAction.setNavigatorViewId(CubridHostNavigatorView.ID);
        ActionManager.getInstance().registerAction(groupPropertyAction);

        ImportHostsAction importHostsAction =
                new ImportHostsAction(
                        Messages.importHostsAction,
                        CommonUIPlugin.getImageDescriptor("icons/action/table_data_import.png"));
        ActionManager.getInstance().registerAction(importHostsAction);

        IAction importServerAction =
                new CubridServerImportAction(
                        shell,
                        Messages.importServerAction,
                        CommonUIPlugin.getImageDescriptor("icons/action/import_connection.png"));
        ActionManager.getInstance().registerAction(importServerAction);

        IAction exportServerAction =
                new CubridServerExportAction(
                        shell,
                        Messages.exportServerAction,
                        CommonUIPlugin.getImageDescriptor("icons/action/export_connection.png"));
        ActionManager.getInstance().registerAction(exportServerAction);

        ActionManager.getInstance().setMenuProvider(new CubridMenuProvider());

        // Big toolbar on CM
        IAction startActionTb =
                new StartRetargetAction(
                        shell,
                        Messages.startActionNameBig,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/menu_start_big.png"),
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/menu_start_big_disabled.png"),
                        true);
        ActionManager.getInstance().registerAction(startActionTb);

        IAction stopActionTb =
                new StopRetargetAction(
                        shell,
                        Messages.stopActionNameBig,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/menu_stop_big.png"),
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/menu_stop_big_disabled.png"),
                        true);
        ActionManager.getInstance().registerAction(stopActionTb);

        IAction refreshActionTb =
                new RefreshAction(
                        shell,
                        Messages.refreshActionNameBig,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/refresh_big.png"),
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/refresh_big_disabled.png"),
                        true);
        ActionManager.getInstance().registerAction(refreshActionTb);

        IAction viewServerVersionActionTb =
                new ViewServerVersionAction(
                        shell,
                        Messages.viewServerVersionActionNameBig,
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/menu_version_big.png"),
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/action/menu_version_big_disabled.png"),
                        true);
        ActionManager.getInstance().registerAction(viewServerVersionActionTb);

        // Object Info tab on the query editor
        OpenTargetAction openObjectTabAction =
                new OpenTargetAction(
                        shell,
                        com.cubrid.common.ui.spi.Messages.msgQuickTabAction,
                        CommonUIPlugin.getImageDescriptor("icons/navigator/quick_tab.png"),
                        CommonUIPlugin.getImageDescriptor(
                                "icons/navigator/quick_tab_disabled.png"));
        ActionManager.getInstance().registerAction(openObjectTabAction);

        IAction action = null;
        {
            action =
                    new SchemaCommentInstallAction(
                            shell,
                            com.cubrid.common.ui.spi.Messages.schemaCommentInstallActionName,
                            CommonUIPlugin.getImageDescriptor("icons/navigator/quick_tab.png"),
                            null);
            ActionManager.getInstance().registerAction(action);
        }
        /*Rename host name*/
        RenameHostAction renameHostAction =
                new RenameHostAction(shell, Messages.renameHostActionName, null);
        ActionManager.getInstance().registerAction(renameHostAction);

        ServiceDashboardAction serviceDashboardAction =
                new ServiceDashboardAction(
                        shell,
                        Messages.serviceDashboardActionName,
                        CommonUIPlugin.getImageDescriptor("icons/navigator/quick_tab.png"),
                        CommonUIPlugin.getImageDescriptor(
                                "icons/navigator/quick_tab_disabled.png"));
        ActionManager.getInstance().registerAction(serviceDashboardAction);
        /*Unify host config action*/
        IAction unifyHostConfigAction =
                new UnifyHostConfigAction(
                        shell,
                        Messages.unifyHostConfigAction,
                        CubridManagerUIPlugin.getImageDescriptor("icons/action/settings.png"),
                        null);
        ActionManager.getInstance().registerAction(unifyHostConfigAction);
    }
}
