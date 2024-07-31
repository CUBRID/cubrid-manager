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
package com.cubrid.cubridmanager.ui.spi;

import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import org.eclipse.osgi.util.NLS;

/**
 * This is message bundle classes and provide convenience methods for manipulating messages.
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class Messages extends NLS {
    static {
        NLS.initializeMessages(CubridManagerUIPlugin.PLUGIN_ID + ".spi.Messages", Messages.class);
    }

    public static String titleWarning;
    public static String titleError;
    public static String titleConfirm;
    public static String btnYes;
    public static String btnNo;
    public static String btnOk;
    public static String msgRunning;
    public static String errCannotConnectServerReconnect;
    public static String msgContextMenuCopy;
    public static String msgContextMenuPaste;
    // loader related message
    public static String msgSqlLogFolderName;
    public static String msgAccessLogFolderName;
    public static String msgErrorLogFolderName;
    public static String msgAdminLogFolderName;
    public static String msgUserFolderName;
    public static String msgJobAutoFolderName;
    public static String msgDbSpaceFolderName;
    public static String msgTablesFolderName;
    public static String msgViewsFolderName;
    public static String msgSystemTableFolderName;
    public static String msgSystemViewFolderName;
    public static String msgSpFolderName;
    public static String msgTriggerFolderName;
    public static String msgSynonymFolderName;
    public static String msgSerialFolderName;
    public static String msgGenerialVolumeFolderName;
    public static String msgDataVolumeFolderName;
    public static String msgIndexVolumeFolderName;
    public static String msgTempVolumeFolderName;
    public static String msgLogVolumeFolderName;
    public static String msgActiveLogFolderName;
    public static String msgArchiveLogFolderName;
    public static String msgBackupPlanFolderName;
    public static String msgQueryPlanFolderName;
    public static String msgLogsBrokerFolderName;
    public static String msgLogsManagerFolderName;
    public static String msgLogsServerFolderName;
    public static String msgDatabaseFolderName;
    public static String msgBrokersFolderName;
    public static String msgShardsFolderName;
    public static String msgMonitorFolderName;
    public static String msgSystemMonitorFolderName;
    public static String msgMonitorStatisticFolderName;
    public static String msgHostSystemMonitorName;
    public static String msgDbSystemMonitorName;
    public static String msgStatusMonitorFolderName;
    public static String msgLogsFolderName;
    public static String msgFunctionFolderName;
    public static String msgProcedureFolderName;
    public static String errDatabaseNoExist;
    public static String errBrokerNoExist;
    public static String msgBrokerStatusMonitorName;
    public static String msgDbStatusMonitorName;
    // property page related
    public static String msgConfigureParameterPageName;
    public static String msgDetailConfigureParameter;
    public static String msgCmServerPropertyPageName;
    public static String msgBrokerPropertyPageName;
    public static String msgManagerPropertyPageName;
    public static String msgServicePropertyPageName;
    public static String msgDatabaseServerCommonPropertyPageName;
    public static String msgDatabaseServerPropertyPageName;
    public static String msgQueryPropertyPageName;
    public static String titlePropertiesDialog;
    public static String msgHAPropertyPageName;

    // action related message
    // common action
    public static String refreshActionName;
    public static String refreshActionNameBig;
    public static String openPreferenceActionName;
    public static String propertyActionName;
    public static String userManagementActionName;
    public static String startServiceActionName;
    public static String stopServiceActionName;
    public static String startActionName;
    public static String startActionNameBig;
    public static String stopActionName;
    public static String stopActionNameBig;

    // host server
    public static String renameHostActionName;
    public static String addHostActionName;
    public static String connectHostActionName;
    public static String editHostActionName;
    public static String disConnectHostActionName;
    public static String copyHostActionName;
    public static String pasteHostActionName;
    public static String changePasswordActionName;
    public static String deleteHostActionName;
    public static String hostDashBoardActionName;
    public static String viewServerVersionActionName;
    public static String viewServerVersionActionNameBig;
    public static String confActionGroupName;
    public static String editCubridConf;
    public static String imortCubridConf;
    public static String exportCubridConf;
    public static String editBrokerConf;
    public static String importBrokerConf;
    public static String exportBrokerConf;
    public static String editCmConf;
    public static String importComConf;
    public static String exportCmConf;
    public static String easyHAWizard;
    public static String editHaConf;
    public static String importHaConf;
    public static String exportHaConf;
    public static String serviceDashboardActionName;

    // database operation
    public static String dbManMenu;
    public static String dbInfoMenu;
    public static String loginDatabaseActionName;
    public static String editDatabaseLoginActionName;
    public static String logoutDatabaseActionName;
    public static String startDatabaseActionName;
    public static String stopDatabaseActionName;
    public static String backupDatabaseActionName;
    public static String restoreDatabaseActionName;
    public static String createDatabaseActionName;
    public static String copyDatabaseActionName;
    public static String databaseStatusViewActionName;
    public static String databaseDashboardViewActionName;
    public static String lockInfoActionName;
    public static String checkDatabaseActionName;
    public static String renameDatabaseActionName;
    public static String loadDatabaseActionName;
    public static String unloadDatabaseActionName;
    public static String optimizeActionName;
    public static String compactDatabaseActionName;
    public static String transactionInfoActionName;
    public static String deleteDatabaseActionName;
    public static String planDumpActionName;
    public static String paramDumpActionName;

    // db user action
    public static String deleteUserActionName;
    public static String editUserActionName;
    public static String addUserActionName;

    // log
    public static String removeAllAccessLogActionName;
    public static String removeAllErrorLogActionName;
    public static String removeAllScriptLogActionName;
    public static String removeAllLogActionName;
    public static String removeLogActionName;
    public static String logViewActionName;
    public static String timeSetActionName;
    public static String logPropertyActionName;
    public static String activityAnalyzeCasLogActionName;
    public static String activityCasLogRunActionName;
    public static String resetAdminLogActionName;
    // backup
    public static String addBackupPlanActionName;
    public static String editBackupPlanActionName;
    public static String deleteBackupPlanActionName;
    public static String backUpErrLogActionName;
    // query plan
    public static String addQueryPlanActionName;
    public static String editQueryPlanActionName;
    public static String deleteQueryPlanActionName;
    public static String queryPlanLogActionName;

    // database space
    public static String setAutoAddVolumeActionName;
    public static String setAddVolumeActionName;
    public static String autoAddVolumeLogActionName;
    public static String spaceFolderViewActionName;
    public static String spaceInfoViewActionName;

    // query execution plan
    public static String openExecutionPlanActionName;

    // monitor
    public static String addStatusMonitorActionName;
    public static String editStatusMonitorActionName;
    public static String delStatusMonitorActionName;
    public static String viewStatusMonitorActionName;
    public static String viewBrokerMonitorHistoryActionName;
    public static String viewDbMonitorHistoryActionName;
    public static String viewSystemMonitorActionName;
    public static String viewHostSysMonHistoryActionName;
    public static String viewDbSysMonHistoryActionName;
    public static String addMonStatisticPageActionName;
    public static String openMonStatisticPageActionName;
    public static String deleteMonStatisticPageActionName;
    public static String checkMonitorIntervalActionName;

    // broker
    public static String startBrokerEnvActionName;
    public static String stopBrokerEnvActionName;
    public static String startBrokerActionName;
    public static String restartBrokerActionName;
    public static String stopBrokerActionName;
    public static String showBrokersStatusActionName;
    public static String showBrokerStatusActionName;
    public static String brokerEditorPropertyActionName;
    public static String brokerLogTopMergeAction;
    public static String brokerLogParseAction;
    // replication action message
    public static String menuReplication;
    public static String createReplicationActionName;
    public static String deleteReplicationActionName;
    public static String viewReplicationActionName;
    public static String startReplicationServerActionName;
    public static String stopReplicationServerActionName;
    public static String startReplicationAgentActionName;
    public static String stopReplicationAgentActionName;
    public static String startSlaveDbActionName;
    public static String stopSlaveDbActionName;
    public static String changeSlaveDbActionName;
    public static String changeMasterDbActionName;
    public static String changeReplicationSchemaActionName;
    public static String viewReplicationErrorLogActionName;
    public static String monitorReplicationPerfActionName;
    public static String configureReplicationParaActionName;
    public static String masterDbActionGroupName;
    public static String slaveDbActionGroupName;

    // monitoring dash board
    public static String openMonitorViewActionName;
    public static String addDashboardActionName;
    public static String deleteDashboardActionName;
    public static String openDashboardActionName;
    public static String editDashboardActionName;
    public static String connectAllActionName;
    public static String addHostMonitorAction;
    public static String connectHostOrDBAction;
    public static String deleteDatabaseMonitorAction;
    public static String deleteHostMonitorAction;
    public static String hARoleChangeAction;
    public static String openApplyLogDBLogAction;
    public static String openCopyLogDBLogAction;
    public static String openDatabaseLogAction;
    public static String monitorDetailAction;
    public static String disconnecHostMonitorAction;
    public static String disconnecDatabaseMonitorAction;
    public static String addDatabaseMonitorAction;
    public static String addBrokerMonitorAction;
    public static String dashboardRefreshAction;
    public static String hostDashboardHistoryAction;
    public static String dbDashboardHistoryAction;
    public static String editNickNameAction;
    public static String showBrokerClientAction;
    public static String showBrokerDabaseAction;
    public static String minimizeFigureAction;
    public static String hideHostAction;
    public static String showHostAction;
    public static String deleteBrokerMonitorAction;
    public static String clearNodeErrorMsgAction;
    public static String importOptions;

    // import hosts
    public static String importHostsAction;
    public static String importServerAction;
    public static String exportServerAction;

    // multi database login
    public static String errMultiDatabaseLoginNoUsername;

    // unify host config action
    public static String unifyHostConfigAction;
}
