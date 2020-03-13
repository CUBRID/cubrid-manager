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
package com.cubrid.cubridmanager.ui.spi.action;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.Util;
import org.slf4j.Logger;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.common.action.ConnectionUrlExportAction;
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
import com.cubrid.common.ui.er.action.OpenSchemaEditorAction;
import com.cubrid.common.ui.query.action.DatabaseQueryNewAction;
import com.cubrid.common.ui.query.control.DatabaseNavigatorMenu;
import com.cubrid.common.ui.schemacomment.action.SchemaCommentInstallAction;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.MenuProvider;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.broker.action.RestartBrokerAction;
import com.cubrid.cubridmanager.ui.broker.action.ShowBrokerEnvStatusAction;
import com.cubrid.cubridmanager.ui.broker.action.ShowBrokerStatusAction;
import com.cubrid.cubridmanager.ui.broker.action.StartBrokerAction;
import com.cubrid.cubridmanager.ui.broker.action.StartBrokerEnvAction;
import com.cubrid.cubridmanager.ui.broker.action.StopBrokerAction;
import com.cubrid.cubridmanager.ui.broker.action.StopBrokerEnvAction;
import com.cubrid.cubridmanager.ui.common.action.PropertyAction;
import com.cubrid.cubridmanager.ui.common.control.CMDatabaseNavigatorMenu;
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
import com.cubrid.cubridmanager.ui.host.action.ConnectHostAction;
import com.cubrid.cubridmanager.ui.host.action.EditBrokerConfigAction;
import com.cubrid.cubridmanager.ui.host.action.EditCmConfigAction;
import com.cubrid.cubridmanager.ui.host.action.EditCubridConfigAction;
import com.cubrid.cubridmanager.ui.host.action.EditHAConfigAction;
import com.cubrid.cubridmanager.ui.host.action.ExportBrokerConfigAction;
import com.cubrid.cubridmanager.ui.host.action.ExportCmConfigAction;
import com.cubrid.cubridmanager.ui.host.action.ExportCubridConfigAction;
import com.cubrid.cubridmanager.ui.host.action.ExportHAConfigAction;
import com.cubrid.cubridmanager.ui.host.action.HostDashboardAction;
import com.cubrid.cubridmanager.ui.host.action.ImportBrokerConfigAction;
import com.cubrid.cubridmanager.ui.host.action.ImportCmConfigAction;
import com.cubrid.cubridmanager.ui.host.action.ImportCubridConfigAction;
import com.cubrid.cubridmanager.ui.host.action.ImportHAConfigAction;
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
import com.cubrid.cubridmanager.ui.mondashboard.action.AddMonitorDashboardAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.DeleteMonitorDashboardAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.EditMonitorDashboardAction;
import com.cubrid.cubridmanager.ui.mondashboard.action.OpenMonitorDashboardAction;
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
import com.cubrid.cubridmanager.ui.replication.action.ChangeSlaveDbAction;
import com.cubrid.cubridmanager.ui.replication.action.ConfigureReplicationParamAction;
import com.cubrid.cubridmanager.ui.replication.action.MonitorReplicationPerfAction;
import com.cubrid.cubridmanager.ui.replication.action.StartReplicationAgentAction;
import com.cubrid.cubridmanager.ui.replication.action.StartReplicationServerAction;
import com.cubrid.cubridmanager.ui.replication.action.StartSlaveDbAction;
import com.cubrid.cubridmanager.ui.replication.action.StopReplicationAgentAction;
import com.cubrid.cubridmanager.ui.replication.action.StopReplicationServerAction;
import com.cubrid.cubridmanager.ui.replication.action.StopSlaveDbAction;
import com.cubrid.cubridmanager.ui.replication.action.ViewReplicationAction;
import com.cubrid.cubridmanager.ui.replication.action.ViewReplicationErrorLogAction;
import com.cubrid.cubridmanager.ui.shard.action.ShowShardStatusAction;
import com.cubrid.cubridmanager.ui.shard.action.StartShardAction;
import com.cubrid.cubridmanager.ui.shard.action.StartShardEnvAction;
import com.cubrid.cubridmanager.ui.shard.action.StopShardAction;
import com.cubrid.cubridmanager.ui.shard.action.StopShardEnvAction;
import com.cubrid.cubridmanager.ui.spi.Messages;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;

/**
 * This menu provider provide the context menu and menu bar menu according to
 * the selected object
 *
 * @author pangqiren
 * @version 1.0 - 2010-1-7 created by pangqiren
 */
public class CubridMenuProvider extends MenuProvider {
	private static final Logger LOGGER = LogUtil.getLogger(CubridMenuProvider.class);
	protected final DatabaseNavigatorMenu databaseNavigatorMenu = new CMDatabaseNavigatorMenu();
	/**
	 * Build the context menu and menubar menu according to the selected cubrid
	 * node
	 *
	 * @param manager the parent menu manager
	 * @param node the ICubridNode object
	 */
	public void buildMenu(IMenuManager manager, ICubridNode node) {
		// fill Action Menu according to node type
		if (node == null) {
			LOGGER.error("ICubridNode is a null.");
			return;
		}

		String type = node.getType();
		if (CubridNodeType.SERVER.equals(type)) {
			addActionToManager(manager, getAction(HostDashboardAction.ID));
			addActionToManager(manager, getAction(ViewServerVersionAction.ID));

//			#1 Disable the HA/Shard wizard
//			if (CompatibleUtil.isSupportNewHAConfFile(node.getServer().getServerInfo())) {
//				manager.add(new Separator());
//				addActionToManager(manager, getAction(EasyHAAction.ID));
//			}

//			#1 Disable the HA/Shard wizard
//			if (node.getServer() != null
//					&& node.getServer().getServerInfo() != null
//					&& CompatibleUtil.isSupportShard(node.getServer().getServerInfo())) {
//				manager.add(new Separator());
//				addActionToManager(manager, getAction(AddShardAction.ID));
//			}

			manager.add(new Separator());

			addActionToManager(manager, getAction(PropertyAction.ID));
			addActionToManager(manager, getAction(UnifyHostConfigAction.ID));
			IMenuManager configMenu = new MenuManager(Messages.confActionGroupName);
			manager.add(configMenu);
			addActionToManager(configMenu, getAction(EditCubridConfigAction.ID));
			addActionToManager(configMenu, getAction(ImportCubridConfigAction.ID));
			addActionToManager(configMenu, getAction(ExportCubridConfigAction.ID));
			configMenu.add(new Separator());
			addActionToManager(configMenu, getAction(EditBrokerConfigAction.ID));
			addActionToManager(configMenu, getAction(ImportBrokerConfigAction.ID));
			addActionToManager(configMenu, getAction(ExportBrokerConfigAction.ID));
			configMenu.add(new Separator());
			addActionToManager(configMenu, getAction(EditCmConfigAction.ID));
			addActionToManager(configMenu, getAction(ImportCmConfigAction.ID));
			addActionToManager(configMenu, getAction(ExportCmConfigAction.ID));
			if (CompatibleUtil.isSupportNewHAConfFile(node.getServer().getServerInfo())) {
				configMenu.add(new Separator());
				addActionToManager(configMenu, getAction(EditHAConfigAction.ID));
				addActionToManager(configMenu, getAction(ImportHAConfigAction.ID));
				addActionToManager(configMenu, getAction(ExportHAConfigAction.ID));
			}
			manager.add(new Separator());
		} else if (CubridNodeType.DATABASE_FOLDER.equals(type)) {
			addActionToManager(manager, getAction(CreateDatabaseAction.ID));
			manager.add(new Separator());
//			addActionToManager(manager, getAction(OpenSchemaEditorAction.ID));
//			manager.add(new Separator());
			addActionToManager(manager, getAction(PropertyAction.ID));
			manager.add(new Separator());
		} else if (CubridNodeType.DATABASE.equals(type)) {
			buildDatabaseMenu(manager, node);
		} else if (CubridNodeType.USER_FOLDER.equals(type)) {
			addActionToManager(manager, getAction(AddUserAction.ID));
		} else if (CubridNodeType.USER.equals(type)) {
			addActionToManager(manager, getAction(EditUserAction.ID));
			addActionToManager(manager, getAction(DeleteUserAction.ID));
		} else if (CubridNodeType.BACKUP_PLAN_FOLDER.equals(type)) {
			addActionToManager(manager, getAction(AddBackupPlanAction.ID));
			addActionToManager(manager, getAction(BackupErrLogAction.ID));
		} else if (CubridNodeType.BACKUP_PLAN.equals(type)) {
			addActionToManager(manager, getAction(EditBackupPlanAction.ID));
			addActionToManager(manager, getAction(DeleteBackupPlanAction.ID));
		} else if (CubridNodeType.QUERY_PLAN_FOLDER.equals(type)) {
			addActionToManager(manager, getAction(AddQueryPlanAction.ID));
			addActionToManager(manager, getAction(QueryLogAction.ID));
		} else if (CubridNodeType.QUERY_PLAN.equals(type)) {
			addActionToManager(manager, getAction(EditQueryPlanAction.ID));
			addActionToManager(manager, getAction(DeleteQueryPlanAction.ID));
		} else if (CubridNodeType.DBSPACE_FOLDER.equals(type)) {
			addActionToManager(manager, getAction(AddVolumeAction.ID));
			manager.add(new Separator());
			addActionToManager(manager, getAction(SetAutoAddVolumeAction.ID));
			addActionToManager(manager, getAction(AutoAddVolumeLogAction.ID));
			manager.add(new Separator());
			addActionToManager(manager, getAction(DatabaseStatusViewAction.ID));
		} else if (CubridNodeType.GENERIC_VOLUME_FOLDER.equals(type)) {
			addActionToManager(manager, getAction(AddVolumeAction.ID));
			addActionToManager(manager, getAction(SpaceFolderViewAction.ID));
		} else if (CubridNodeType.DATA_VOLUME_FOLDER.equals(type)) {
			addActionToManager(manager, getAction(AddVolumeAction.ID));
			addActionToManager(manager, getAction(SpaceFolderViewAction.ID));
		} else if (CubridNodeType.INDEX_VOLUME_FOLDER.equals(type)) {
			addActionToManager(manager, getAction(AddVolumeAction.ID));
			addActionToManager(manager, getAction(SpaceFolderViewAction.ID));
		} else if (CubridNodeType.TEMP_VOLUME_FOLDER.equals(type)) {
			addActionToManager(manager, getAction(AddVolumeAction.ID));
			addActionToManager(manager, getAction(SpaceFolderViewAction.ID));
		} else if (CubridNodeType.ACTIVE_LOG_FOLDER.equals(type)) {
			addActionToManager(manager, getAction(SpaceFolderViewAction.ID));
		} else if (CubridNodeType.ARCHIVE_LOG_FOLDER.equals(type)) {
			addActionToManager(manager, getAction(SpaceFolderViewAction.ID));
		} else if (CubridNodeType.GENERIC_VOLUME.equals(type)) {
			addActionToManager(manager, getAction(SpaceInfoViewAction.ID));
		} else if (CubridNodeType.DATA_VOLUME.equals(type)) {
			addActionToManager(manager, getAction(SpaceInfoViewAction.ID));
		} else if (CubridNodeType.INDEX_VOLUME.equals(type)) {
			addActionToManager(manager, getAction(SpaceInfoViewAction.ID));
		} else if (CubridNodeType.TEMP_VOLUME.equals(type)) {
			addActionToManager(manager, getAction(SpaceInfoViewAction.ID));
		} else if (CubridNodeType.BROKER_FOLDER.equals(type)) {
			if (StartBrokerEnvAction.isSupportedNode(node)) {
				addActionToManager(manager, getAction(StartBrokerEnvAction.ID));
			}
			if (StopBrokerEnvAction.isSupportedNode(node)) {
				addActionToManager(manager, getAction(StopBrokerEnvAction.ID));
			}
			manager.add(new Separator());
			addActionToManager(manager, getAction(ShowBrokerEnvStatusAction.ID));
			manager.add(new Separator());
			addActionToManager(manager, getAction(EditBrokerConfigAction.ID));
			addActionToManager(manager, getAction(PropertyAction.ID));
			manager.add(new Separator());
		} else if (CubridNodeType.BROKER.equals(type)) {
			if (StartBrokerAction.isSupportedNode(node)) {
				addActionToManager(manager, getAction(StartBrokerAction.ID));
			}
			if (StopBrokerAction.isSupportedNode(node)) {
				addActionToManager(manager, getAction(StopBrokerAction.ID));
			}
			if (RestartBrokerAction.isSupportedNode(node)) {
				addActionToManager(manager, getAction(RestartBrokerAction.ID));
			}
			manager.add(new Separator());
			addActionToManager(manager, getAction(ShowBrokerStatusAction.ID));
			manager.add(new Separator());
			addActionToManager(manager, getAction(PropertyAction.ID));
			manager.add(new Separator());
		} else if (CubridNodeType.SHARD_FOLDER.equals(type)) {
			if (StartShardEnvAction.isSupportedNode(node)) {
				addActionToManager(manager, getAction(StartShardEnvAction.ID));
			}
			if (StopShardEnvAction.isSupportedNode(node)) {
				addActionToManager(manager, getAction(StopShardEnvAction.ID));
			}
			manager.add(new Separator());
			addActionToManager(manager, getAction(ShowShardStatusAction.ID));
			manager.add(new Separator());
			addActionToManager(manager, getAction(PropertyAction.ID));
			manager.add(new Separator());
		} else if (CubridNodeType.SHARD.equals(type)) {
			if (StartShardAction.isSupportedNode(node)) {
				addActionToManager(manager, getAction(StartShardAction.ID));
			}
			if (StopShardAction.isSupportedNode(node)) {
				addActionToManager(manager, getAction(StopShardAction.ID));
			}
			manager.add(new Separator());
			addActionToManager(manager, getAction(PropertyAction.ID));
		} else if (CubridNodeType.MONITOR_FOLDER.equals(type)) {
			//status monitor action
			if (CompatibleUtil.isSupportBrokerOrDBStatusMonitor(node.getServer().getServerInfo())) {
				addActionToManager(manager, getAction(AddMonitorInstanceAction.ID));
				manager.add(new Separator());
				addActionToManager(manager, getAction(ShowBrokerMonitorHistoryAction.ID));
				addActionToManager(manager, getAction(ShowDatabaseMonitorHistoryAction.ID));
			} else {
				addActionToManager(manager, getAction(AddStatusMonitorTemplateAction.ID));
			}
			//system monitor action
			if (CompatibleUtil.isSupportSystemMonitor(node.getServer().getServerInfo())) {
				manager.add(new Separator());
				addActionToManager(manager, getAction(ShowHostSystemMonitorHistoryAction.ID));
				addActionToManager(manager, getAction(ShowDbSystemMonitorHistoryAction.ID));
			}
			//monitor statistic action
			if (CompatibleUtil.isSupportMonitorStatistic(node.getServer().getServerInfo())){
				manager.add(new Separator());
				addActionToManager(manager, getAction(AddMonitorStatisticPageAction.ID));
				addActionToManager(manager, getAction(CheckMonitorIntervalAction.ID));
			}
		/*} else if (CubridNodeType.STATUS_MONITOR_FOLDER.equals(type)) {
			if (CompatibleUtil.isSupportBrokerOrDBStatusMonitor(node.getServer().getServerInfo())) {
				addActionToManager(manager, getAction(AddMonitorInstanceAction.ID));
				manager.add(new Separator());
				addActionToManager(manager, getAction(ShowBrokerMonitorHistoryAction.ID));
				addActionToManager(manager, getAction(ShowDatabaseMonitorHistoryAction.ID));
			} else {
				addActionToManager(manager, getAction(AddStatusMonitorTemplateAction.ID));
			}
		} else if (CubridNodeType.MONITOR_STATISTIC_FOLDER.equals(type)) {
			addActionToManager(manager, getAction(AddMonitorStatisticPageAction.ID));
			addActionToManager(manager, getAction(CheckMonitorIntervalAction.ID));*/
		} else if (CubridNodeType.MONITOR_STATISTIC_PAGE.equals(type)) {
			addActionToManager(manager, getAction(OpenMonitorStatisticPageAction.ID));
			addActionToManager(manager, getAction(DeleteMonitorStatisticPageAction.ID));
			if (node.getParent() == null) {
				manager.add(new Separator());
				addActionToManager(manager, getAction(AddMonitorDashboardAction.ID));
				addActionToManager(manager, getAction(AddMonitorStatisticPageAction.ID));
			}
		} else if (CubridNodeType.STATUS_MONITOR_TEMPLATE.equals(type)) {
			addActionToManager(manager, getAction(ShowStatusMonitorAction.ID));
			if (CompatibleUtil.isSupportBrokerOrDBStatusMonitor(node.getServer().getServerInfo())) {
				if (!Messages.msgDbStatusMonitorName.equals(node.getLabel())
						&& !Messages.msgBrokerStatusMonitorName.equals(node.getLabel())) {
					addActionToManager(manager, getAction(EditMonitorInstanceAction.ID));
					addActionToManager(manager, getAction(DelMonitorInstanceAction.ID));
				}
			} else {
				manager.add(new Separator());
				addActionToManager(manager, getAction(EditStatusMonitorTemplateAction.ID));
				addActionToManager(manager, getAction(DeleteStatusMonitorTemplateAction.ID));
			}
		/*} else if (CubridNodeType.SYSTEM_MONITOR_FOLDER.equals(type)) {
			if (CompatibleUtil.isSupportSystemMonitor(node.getServer().getServerInfo())) {
				addActionToManager(manager, getAction(ShowHostSystemMonitorHistoryAction.ID));
				addActionToManager(manager, getAction(ShowDbSystemMonitorHistoryAction.ID));
			}*/
		} else if (CubridNodeType.SYSTEM_MONITOR_TEMPLATE.equals(type)) {
			addActionToManager(manager, getAction(ShowSystemMonitorAction.ID));
		} else if (CubridNodeType.LOGS_BROKER_ACCESS_LOG_FOLDER.equals(type)) {
			addActionToManager(manager, getAction(RemoveAllAccessLogAction.ID));
		} else if (CubridNodeType.LOGS_BROKER_ACCESS_LOG.equals(type)) {
			addActionToManager(manager, getAction(LogViewAction.ID));
			addActionToManager(manager, getAction(RemoveLogAction.ID));
			manager.add(new Separator());
			addActionToManager(manager, getAction(LogPropertyAction.ID));
		} else if (CubridNodeType.LOGS_BROKER_ERROR_LOG_FOLDER.equals(type)) {
			addActionToManager(manager, getAction(RemoveAllErrorLogAction.ID));
		} else if (CubridNodeType.LOGS_BROKER_ERROR_LOG.equals(type)) {
			addActionToManager(manager, getAction(LogViewAction.ID));
			addActionToManager(manager, getAction(RemoveLogAction.ID));
			manager.add(new Separator());
			addActionToManager(manager, getAction(LogPropertyAction.ID));
		} else if (CubridNodeType.LOGS_BROKER_ADMIN_LOG.equals(type)) {
			addActionToManager(manager, getAction(LogViewAction.ID));
			addActionToManager(manager, getAction(ResetAdminLogAction.ID));
			manager.add(new Separator());
			addActionToManager(manager, getAction(LogPropertyAction.ID));
		} else if (CubridNodeType.BROKER_SQL_LOG_FOLDER.equals(type)) {
			addActionToManager(manager, getAction(RemoveAllScriptLogAction.ID));
			addActionToManager(manager, getAction(AnalyzeSqlLogAction.ID));
		} else if (CubridNodeType.BROKER_SQL_LOG.equals(type)) {
			addActionToManager(manager, getAction(LogViewAction.ID));
			manager.add(new Separator());
			addActionToManager(manager, getAction(AnalyzeSqlLogAction.ID));
			addActionToManager(manager, getAction(ExecuteSqlLogAction.ID));
			addActionToManager(manager, getAction(RemoveLogAction.ID));
			manager.add(new Separator());
			addActionToManager(manager, getAction(LogPropertyAction.ID));
		} else if (CubridNodeType.LOGS_SERVER_DATABASE_LOG.equals(type)) {
			addActionToManager(manager, getAction(LogViewAction.ID));
			addActionToManager(manager, getAction(RemoveLogAction.ID));
			manager.add(new Separator());
			addActionToManager(manager, getAction(LogPropertyAction.ID));
		} else if (CubridNodeType.LOGS_SERVER_DATABASE_FOLDER.equals(type)) {
			addActionToManager(manager, getAction(RemoveAllDbLogAction.ID));
		} else if (CubridNodeType.LOGS_MANAGER_ACCESS_LOG.equals(type)
				|| CubridNodeType.LOGS_MANAGER_ERROR_LOG.equals(type)) {
			addActionToManager(manager, getAction(ManagerLogViewAction.ID));
			addActionToManager(manager, getAction(RemoveAllManagerLogAction.ID));
		} else if (CubridNodeType.MONITOR_DASHBOARD.equals(type)) {
			addActionToManager(manager, getAction(OpenMonitorDashboardAction.ID));
			addActionToManager(manager, getAction(EditMonitorDashboardAction.ID));
			addActionToManager(manager, getAction(DeleteMonitorDashboardAction.ID));
			manager.add(new Separator());
			addActionToManager(manager, getAction(AddMonitorDashboardAction.ID));
			addActionToManager(manager, getAction(AddMonitorStatisticPageAction.ID));
			manager.add(new Separator());
		} else if (NodeType.GROUP.equals(type)) {
			addActionToManager(manager, getAction(ConnectHostAction.ID));
			manager.add(new Separator());
			addActionToManager(manager, getAction(AddHostAction.ID));
			manager.add(new Separator());
			super.buildMenu(manager, node);
		} else {
			super.buildMenu(manager, node);
		}

		// This menu hide if it hasn't any hidden menu node.
		if (node.isContainer() && ShowHiddenElementsAction.isSupportedNode(node)) {
			manager.add(new Separator());
			ActionManager.addActionToManager(manager,
					ActionManager.getInstance().getAction(
							ShowHiddenElementsAction.ID));
		}
		manager.update(true);
	}

	/**
	 * Construct database related actions
	 *
	 * @param manager the parent IMenuManager
	 * @param node the ICubridNode object
	 */
	private void buildDatabaseMenu(IMenuManager manager, ICubridNode node) {
		if (LoginDatabaseAction.isSupportedNode(node)) {
			addActionToManager(manager, getAction(LoginDatabaseAction.ID));
		}
		if (LogoutDatabaseAction.isSupportedNode(node)) {
			addActionToManager(manager, getAction(LogoutDatabaseAction.ID));
		}
		manager.add(new Separator());
		addActionToManager(manager, getAction(EditDatabaseLoginAction.ID));
		addActionToManager(manager, getAction(ConnectionUrlExportAction.ID));
		manager.add(new Separator());
		if (!Util.isWindows()) {
			addActionToManager(manager, getAction(DatabaseQueryNewAction.ID));
			manager.add(new Separator());
		}
		addActionToManager(manager, getAction(ShowDatabaseDashboardAction.ID));
		manager.add(new Separator());
		ActionManager.addActionToManager(manager, SchemaCompareWizardAction.ID);
		ActionManager.addActionToManager(manager, DataCompareWizardAction.ID);
//		manager.add(new Separator());
//		addActionToManager(manager, getAction(OpenSchemaEditorAction.ID));
		manager.add(new Separator());
		addActionToManager(manager, getAction(ExportERwinAction.ID));
		addActionToManager(manager, getAction(ImportERwinAction.ID));
		manager.add(new Separator());
		CubridDatabase database = (CubridDatabase) node;
		if (database.isDistributorDb()) {
			buildReplicationMenu(manager);
			manager.add(new Separator());
		}

		// Export & Import Actions
		manager.add(new Separator());
		addActionToManager(manager, getAction(ExportWizardAction.ID));
		addActionToManager(manager, getAction(ImportWizardAction.ID));
		manager.add(new Separator());
		addActionToManager(manager, getAction(ExportTableDefinitionAction.ID));

		// Install Schema Comment
		if (node instanceof CubridDatabase
				&& !CompatibleUtil.isCommentSupports(((CubridDatabase) node).getDatabaseInfo())) {
			addActionToManager(manager, getAction(SchemaCommentInstallAction.ID));
			manager.add(new Separator());
		}

//		manager.add(new Separator());
//		addActionToManager(manager, getAction(RunSQLFileAction.ID));

		// Database Management Actions
		manager.add(new Separator());
		IMenuManager dbManMenu = new MenuManager(
				Messages.dbManMenu,
				CubridManagerUIPlugin.getImageDescriptor("icons/navigator/database_man.png"),
				null);
		manager.add(dbManMenu);
		if (StartDatabaseAction.isSupportedNode(node)) {
			addActionToManager(manager, getAction(StartDatabaseAction.ID));
		}
		if (StopDatabaseAction.isSupportedNode(node)) {
			addActionToManager(manager, getAction(StopDatabaseAction.ID));
		}

		addActionToManager(dbManMenu, getAction(LoadDatabaseAction.ID));
		addActionToManager(dbManMenu, getAction(UnloadDatabaseAction.ID));
		dbManMenu.add(new Separator());
		addActionToManager(dbManMenu, getAction(OptimizeAction.ID));
		addActionToManager(dbManMenu, getAction(CompactDatabaseAction.ID));
		addActionToManager(dbManMenu, getAction(CheckDatabaseAction.ID));
		dbManMenu.add(new Separator());
		addActionToManager(dbManMenu, getAction(CopyDatabaseAction.ID));
		addActionToManager(dbManMenu, getAction(RenameDatabaseAction.ID));
		dbManMenu.add(new Separator());
		addActionToManager(dbManMenu, getAction(BackupDatabaseAction.ID));
		addActionToManager(dbManMenu, getAction(RestoreDatabaseAction.ID));
		dbManMenu.add(new Separator());
		addActionToManager(dbManMenu, getAction(DeleteDatabaseAction.ID));

		IMenuManager dbInfoMenu = new MenuManager(
				Messages.dbInfoMenu,
				CubridManagerUIPlugin.getImageDescriptor("icons/navigator/database_info.png"),
				null);
		manager.add(dbInfoMenu);
		addActionToManager(dbInfoMenu, getAction(LockInfoAction.ID));
		addActionToManager(dbInfoMenu, getAction(TransactionInfoAction.ID));

		if (CompatibleUtil.isSupportPlanAndParamDump(database.getServer().getServerInfo())) {
			dbInfoMenu.add(new Separator());
			addActionToManager(dbInfoMenu, getAction(PlanDumpAction.ID));
			addActionToManager(dbInfoMenu, getAction(ParamDumpAction.ID));
		}

		dbInfoMenu.add(new Separator());
		addActionToManager(dbInfoMenu, getAction(OIDNavigatorAction.ID));

		manager.add(new Separator());
		addActionToManager(manager, getAction(PropertyAction.ID));
	}

	/**
	 * Add replication related actions to menu manager
	 *
	 * @param parent the parent menu manager
	 */
	private void buildReplicationMenu(IMenuManager parent) {
		IMenuManager manager = new MenuManager(Messages.menuReplication);
		parent.add(manager);
		addActionToManager(manager, getAction(ViewReplicationAction.ID));
		manager.add(new Separator());

		MenuManager masterMenu = new MenuManager(Messages.masterDbActionGroupName);
		manager.add(masterMenu);
		addActionToManager(masterMenu, getAction(StartReplicationServerAction.ID));
		addActionToManager(masterMenu, getAction(StopReplicationServerAction.ID));
		manager.add(new Separator());

		if (StartReplicationAgentAction.isSupportedNode(parent)) {
			addActionToManager(manager, getAction(StartReplicationAgentAction.ID));
		}
		if (StopReplicationAgentAction.isSupportedNode(parent)) {
			addActionToManager(manager, getAction(StopReplicationAgentAction.ID));
		}
		manager.add(new Separator());

		MenuManager slaveMenu = new MenuManager(Messages.slaveDbActionGroupName);
		manager.add(slaveMenu);
		if (StartSlaveDbAction.isSupportedNode(parent)) {
			addActionToManager(slaveMenu, getAction(StartSlaveDbAction.ID));
		}
		if (StopSlaveDbAction.isSupportedNode(parent)) {
			addActionToManager(slaveMenu, getAction(StopSlaveDbAction.ID));
		}
		manager.add(new Separator());

		addActionToManager(manager, getAction(ConfigureReplicationParamAction.ID));
		addActionToManager(manager, getAction(ChangeSlaveDbAction.ID));
		manager.add(new Separator());
		addActionToManager(manager, getAction(MonitorReplicationPerfAction.ID));
		addActionToManager(manager, getAction(ViewReplicationErrorLogAction.ID));
	}

	/**
	 * @return the databaseNavigatorMenu
	 */
	public DatabaseNavigatorMenu getDatabaseNavigatorMenu() {
		return databaseNavigatorMenu;
	}

}
