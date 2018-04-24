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
package com.cubrid.common.ui.spi.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;

import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.action.CollapseAllAction;
import com.cubrid.common.ui.common.action.ConnectionUrlExportAction;
import com.cubrid.common.ui.common.action.ExpandTreeItemAction;
import com.cubrid.common.ui.common.action.FilterSettingAction;
import com.cubrid.common.ui.common.action.HelpDocumentAction;
import com.cubrid.common.ui.common.action.HiddenElementAction;
import com.cubrid.common.ui.common.action.NoticeAction;
import com.cubrid.common.ui.common.action.OIDNavigatorAction;
import com.cubrid.common.ui.common.action.OpenQueryAction;
import com.cubrid.common.ui.common.action.ReportBugAction;
import com.cubrid.common.ui.common.action.RestoreQueryEditorAction;
import com.cubrid.common.ui.common.action.RunSQLFileAction;
import com.cubrid.common.ui.common.action.SchemaCompareAction;
import com.cubrid.common.ui.common.action.ShowHiddenElementsAction;
import com.cubrid.common.ui.common.action.SwitchGroupModeAction;
import com.cubrid.common.ui.common.action.TopGroupAction;
import com.cubrid.common.ui.common.action.TopGroupItemAction;
import com.cubrid.common.ui.common.action.UnExpandTreeItemAction;
import com.cubrid.common.ui.compare.data.action.DataCompareWizardAction;
import com.cubrid.common.ui.compare.schema.action.SchemaCompareWizardAction;
import com.cubrid.common.ui.cubrid.database.erwin.action.ExportERwinAction;
import com.cubrid.common.ui.cubrid.database.erwin.action.ImportERwinAction;
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
import com.cubrid.common.ui.cubrid.table.action.RenameColumnAction;
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
import com.cubrid.common.ui.er.action.OpenSchemaEditorAction;
import com.cubrid.common.ui.external.action.InstallMigrationToolkitAction;
import com.cubrid.common.ui.perspective.OpenCMPerspectiveAction;
import com.cubrid.common.ui.perspective.OpenCQBPerspectiveAction;
import com.cubrid.common.ui.query.action.AddQueryToFavoriteAction;
import com.cubrid.common.ui.query.action.AssignEditorNameAction;
import com.cubrid.common.ui.query.action.BatchRunAction;
import com.cubrid.common.ui.query.action.CopyAction;
import com.cubrid.common.ui.query.action.CopyAllAction;
import com.cubrid.common.ui.query.action.CopyQueryEditorAction;
import com.cubrid.common.ui.query.action.CreateSqlJavaCodeAction;
import com.cubrid.common.ui.query.action.CreateSqlPhpCodeAction;
import com.cubrid.common.ui.query.action.CutAction;
import com.cubrid.common.ui.query.action.DatabaseQueryNewAction;
import com.cubrid.common.ui.query.action.FindReplaceAction;
import com.cubrid.common.ui.query.action.GotoLineAction;
import com.cubrid.common.ui.query.action.InputMethodAction;
import com.cubrid.common.ui.query.action.PasteAction;
import com.cubrid.common.ui.query.action.QueryOpenAction;
import com.cubrid.common.ui.query.action.RedoAction;
import com.cubrid.common.ui.query.action.ReformatColumnsAliasAction;
import com.cubrid.common.ui.query.action.RunQueryAction;
import com.cubrid.common.ui.query.action.RunQueryPlanAction;
import com.cubrid.common.ui.query.action.ParseSqlmapQueryAction;
import com.cubrid.common.ui.query.action.ShowSchemaAction;
import com.cubrid.common.ui.query.action.SqlFormatAction;
import com.cubrid.common.ui.query.action.SqlPstmtAction;
import com.cubrid.common.ui.query.action.UndoAction;
import com.cubrid.common.ui.query.tuner.action.QueryTunerAction;
import com.cubrid.common.ui.query.tuner.action.QueryTunerRunAction;
import com.cubrid.common.ui.spi.Messages;

/**
 *
 * This class is responsible to build CUBRID Manager menu and toolbar action
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class ActionBuilder {
	/**
	 * Initialize query making actions
	 *
	 * @param shell
	 * @param manager
	 */
	private void initMakeQueryActions(Shell shell, ActionManager manager) {
		IAction action = null;

		action = new MakeSelectQueryAction(
				MakeSelectQueryAction.ID, shell, Messages.lblMakeSelectQuery, null);
		registerAction(action);

		action = new MakeSelectPstmtQueryAction(
				MakeSelectPstmtQueryAction.ID, shell, Messages.lblMakeSelectPstmtQuery, null);
		registerAction(action);

		action = new MakeInsertQueryAction(
				MakeInsertQueryAction.ID, shell, Messages.lblMakeInsertQuery, null);
		registerAction(action);

		action = new MakeUpdateQueryAction(
				MakeUpdateQueryAction.ID, shell, Messages.lblMakeUpdateQuery, null);
		registerAction(action);

		action = new MakeDeleteQueryAction(
				MakeDeleteQueryAction.ID, shell, Messages.lblMakeDeleteQuery, null);
		registerAction(action);

		action = new MakeCreateQueryAction(
				MakeCreateQueryAction.ID, shell, Messages.lblMakeCreateQuery, null);
		registerAction(action);

		action = new MakeCloneQueryAction(
				MakeCloneQueryAction.ID, shell, Messages.lblMakeCloneQuery, null);
		registerAction(action);
	}

	/**
	 * Make all actions for CUBRID Manager menu and toolbar
	 *
	 * @param window the workbench window
	 */
	protected void makeActions(Shell shell) {
		IAction openAction = new OpenQueryAction(shell,
				Messages.openActionName, null);
		registerAction(openAction);

		// implemented actions for retarget actions
		IAction undoAction = new UndoAction(shell,
				Messages.undoActionName, null);
		registerAction(undoAction);

		IAction redoAction = new RedoAction(shell,
				Messages.redoActionName, null);
		registerAction(redoAction);

		IAction copyAction = new CopyAction(shell,
				Messages.copyActionName, null);
		registerAction(copyAction);

		IAction copyAllAction = new CopyAllAction(shell,
				Messages.copyAllActionName, null);
		registerAction(copyAllAction);

		IAction inputMethodAction = new InputMethodAction(shell,
				Messages.inputMethodActionName, null);
		registerAction(inputMethodAction);

		IAction pasteAction = new PasteAction(shell,
				Messages.pasteActionName, null);
		registerAction(pasteAction);

		IAction cutAction = new CutAction(shell,
				Messages.cutActionName, null);
		registerAction(cutAction);

		IAction findReplaceAction = new FindReplaceAction(shell,
				Messages.findReplaceActionName, null);
		registerAction(findReplaceAction);

		// common action
		IAction oidNavigatorAction = new OIDNavigatorAction(shell,
				Messages.oidNavigatorActionName, null);
		registerAction(oidNavigatorAction);

		// table schema related action
		IAction tableNewAction = new NewTableAction(
				shell,
				Messages.tableNewActionName,
				loadImage("icons/action/schema_table_add.png"),
				loadImage("icons/action/schema_table_add_disabled.png"));
		registerAction(tableNewAction);

		IAction pstmtOneDataAction = new PstmtOneDataAction(shell,
				Messages.pstmtOneDataActionName, null);
		registerAction(pstmtOneDataAction);

		IAction pstmtMultiDataAction = new PstmtMultiDataAction(
				shell, Messages.pstmtMultiDataActionName, null);
		registerAction(pstmtMultiDataAction);

		IAction selectByOnePstmtDataAction = new SelectByOnePstmtDataAction(
				shell, Messages.selectByOnePstmtDataActionName,
				null);
		registerAction(selectByOnePstmtDataAction);

		IAction selectByMultiPstmtDataAction = new SelectByMultiPstmtDataAction(
				shell, Messages.selectByMultiPstmtDataActionName,
				null);
		registerAction(selectByMultiPstmtDataAction);

		IAction insertOneByPstmtAction = new InsertOneByPstmtAction(
				shell, Messages.insertOneByPstmtActionName, loadImage("icons/action/table_record_insert.png"));
		registerAction(insertOneByPstmtAction);

		IAction insertMultiByPstmtAction = new ImportDataFromFileAction(
				shell, Messages.insertMultiByPstmtActionName, null);
		registerAction(insertMultiByPstmtAction);

		IAction tableEditAction = new EditTableAction(
				shell,
				Messages.tableEditActionName,
				loadImage("icons/action/schema_table_edit.png"));
		registerAction(tableEditAction);

		IAction createViewAction = new CreateViewAction(
				shell,
				Messages.createViewActionName,
				loadImage("icons/action/schema_view_add.png"),
				loadImage("icons/action/schema_view_add_disabled.png"));
		registerAction(createViewAction);

		IAction editViewAction = new EditViewAction(
				shell,
				Messages.editViewActionName,
				loadImage("icons/action/schema_view_edit.png"));
		registerAction(editViewAction);

		IAction propertyViewAction = new PropertyViewAction(
				shell,
				Messages.propertyViewActionName,
				loadImage("icons/action/view_property.png"));
		registerAction(propertyViewAction);

		IAction tableSelectCountAction = new TableSelectCountAction(
				shell,
				Messages.tableSelectCountActionName,
				loadImage("icons/action/table_select_count.png"));
		registerAction(tableSelectCountAction);

		IAction tableDeleteAction = new DeleteTableAction(
				shell,
				Messages.tableDeleteAllActionName,
				loadImage("icons/action/schema_table_delete.png"));
		registerAction(tableDeleteAction);

		IAction truncateTableAction = new TruncateTableAction(
				shell, Messages.truncateTableActionName, null);
		registerAction(truncateTableAction);

		IAction createLikeTableAction = new CreateLikeTableAction(
				shell, Messages.createLikeTableActionName, null);
		registerAction(createLikeTableAction);

		IAction tableSelectAllAction = new TableSelectAllAction(
				shell,
				Messages.tableSelectActionName,
				loadImage("icons/action/table_select_all.png"));
		registerAction(tableSelectAllAction);

//		IAction tableInsertAction = new InsertTableDataAction(
//				shell,
//				Messages.tableInsertActionName,
//				loadImage("icons/action/table_record_insert.png"));
//		registerAction(tableInsertAction);

		IAction tableExportAction = new ExportWizardAction(
				shell,
				Messages.tableExportActionName,
				loadImage("icons/action/export_on.png"),
				loadImage("icons/action/export_off.png"));
		registerAction(tableExportAction);

		IAction tableImportAction = new ImportWizardAction(
				shell,
				Messages.tableImportActionName,
				loadImage("icons/action/import_on.png"),
				loadImage("icons/action/import_off.png"));
		registerAction(tableImportAction);

		// export table definitions to excel file action by fulei
		IAction exportTableDefinitionAction = new ExportTableDefinitionAction(shell,
				com.cubrid.common.ui.common.Messages.exportTableDefinitionAction,
				loadImage("icons/action/export_excel.png"));
		registerAction(exportTableDefinitionAction);

		IAction tableRenameAction = new RenameTableAction(
				shell,
				Messages.tableRenameActionName,
				loadImage("icons/action/table_rename.png"));
		registerAction(tableRenameAction);

		IAction tableDropAction = new DropTableAction(shell,
				Messages.tableDropActionName, null);
		registerAction(tableDropAction);
		tableDropAction.setImageDescriptor(loadImage("icons/action/schema_table_delete.png"));

		IAction viewDropAction = new DropViewAction(shell,
				Messages.viewDropActionName, null);
		registerAction(viewDropAction);
		viewDropAction.setImageDescriptor(loadImage("icons/action/schema_view_delete.png"));

		IAction showSchemaAction = new ShowSchemaAction(shell,
				Messages.showSchemaActionName, null);
		registerAction(showSchemaAction);

		IAction updateStatisticsAction = new UpdateStatisticsAction(
				shell, Messages.updateStatisticsActionName, null);
		registerAction(updateStatisticsAction);

		TableToJavaCodeAction tableToJavaCodeAction = new TableToJavaCodeAction(
				shell,
				Messages.tableToJavaCodeAction,
				loadImage("icons/action/copy_pojo_to_clipboard.gif"));
		registerAction(tableToJavaCodeAction);

		TableToPhpCodeAction tableToPhpCodeAction = new TableToPhpCodeAction(
				shell,
				Messages.tableToPhpCodeAction,
				loadImage("icons/action/copy_pojo_to_clipboard.gif"));
		registerAction(tableToPhpCodeAction);

		IAction columnSelectSqlAction = new ColumnSelectSqlAction(
				shell, Messages.columnSelectSqlActionName, null);
		registerAction(columnSelectSqlAction);

		IAction columnSelectCountAction = new ColumnSelectCountAction(
				shell, Messages.columnSelectCountActionName, null);
		registerAction(columnSelectCountAction);
		// trigger related action
		IAction newTriggerAction = new NewTriggerAction(
				shell,
				Messages.newTriggerActionName,
				loadImage("icons/action/trigger_add.png"),
				loadImage("icons/action/trigger_add_disabled.png"));
		registerAction(newTriggerAction);

		IAction alterTriggerAction = new AlterTriggerAction(
				shell,
				Messages.alterTriggerActionName,
				loadImage("icons/action/trigger_edit.png"));
		registerAction(alterTriggerAction);

		IAction dropTriggerAction = new DropTriggerAction(
				shell,
				Messages.dropTriggerActionName,
				loadImage("icons/action/trigger_delete.png"));
		registerAction(dropTriggerAction);

		// serial related action
		IAction deleteSerialAction = new DeleteSerialAction(
				shell,
				Messages.deleteSerialActionName,
				loadImage("icons/action/serial_delete.png"));
		registerAction(deleteSerialAction);

		IAction createSerialAction = new CreateSerialAction(
				shell,
				Messages.createSerialActionName,
				loadImage("icons/action/serial_add.png"),
				loadImage("icons/action/serial_add_disabled.png"));
		registerAction(createSerialAction);

		IAction editSerialAction = new EditSerialAction(
				shell,
				Messages.editSerialActionName,
				loadImage("icons/action/serial_edit.png"));
		registerAction(editSerialAction);
		// stored procedure related action
		IAction addFunctionAction = new AddFunctionAction(
				shell,
				Messages.addFunctionActionName,
				loadImage("icons/action/function_add.png"),
				loadImage("icons/action/function_add_disabled.png"));
		registerAction(addFunctionAction);
		IAction editFunctionAction = new EditFunctionAction(
				shell,
				Messages.editFunctionActionName,
				loadImage("icons/action/procedure_edit.png"));
		registerAction(editFunctionAction);
		IAction deleteFunctionAction = new DeleteFunctionAction(
				shell,
				Messages.deleteFunctionActionName,
				loadImage("icons/action/procedure_delete.png"));
		registerAction(deleteFunctionAction);

		IAction addProcedureAction = new AddProcedureAction(
				shell,
				Messages.addProcedureActionName,
				loadImage("icons/action/procedure_add.png"),
				loadImage("icons/action/procedure_add_disabled.png"));
		registerAction(addProcedureAction);
		IAction editProcedureAction = new EditProcedureAction(
				shell,
				Messages.editProcedureActionName,
				loadImage("icons/action/procedure_edit.png"));
		registerAction(editProcedureAction);
		IAction deleteProcedureAction = new DeleteProcedureAction(
				shell,
				Messages.deleteProcedureActionName,
				loadImage("icons/action/procedure_delete.png"));
		registerAction(deleteProcedureAction);

		// query editor related action
		SqlFormatAction formatAction = new SqlFormatAction(
				shell,
				Messages.formatActionName,
				loadImage("icons/queryeditor/query_format.png"));
		registerAction(formatAction);

		// run a pstmt sql
		SqlPstmtAction sqlPstmtAction = new SqlPstmtAction(
				shell,
				Messages.sqlPstmtActionName,
				loadImage("icons/queryeditor/qe_set_param.png"));
		registerAction(sqlPstmtAction);

		CreateSqlPhpCodeAction createSqlPhpCodeAction = new CreateSqlPhpCodeAction(
				shell,
				Messages.createSqlCodePhpActionName,
				loadImage("icons/action/copy_pojo_to_clipboard.gif"));
		registerAction(createSqlPhpCodeAction);

		CreateSqlJavaCodeAction createSqlJavaCodeAction = new CreateSqlJavaCodeAction(
				shell,
				Messages.createSqlCodeJavaActionName,
				loadImage("icons/action/copy_pojo_to_clipboard.gif"));
		registerAction(createSqlJavaCodeAction);

		IAction databaseQueryNewAction = new DatabaseQueryNewAction(
				shell,
				Messages.queryOpenActionName,
				loadImage("icons/action/new_query.png"),
				loadImage("icons/action/new_query_disable.png"), false);
		registerAction(databaseQueryNewAction);

		IAction databaseQueryNewActionBig = new DatabaseQueryNewAction(
				shell,
				Messages.queryOpenActionName,
				loadImage("icons/action/new_query_big.png"),
				loadImage("icons/action/new_query_big_disable.png"), true);
		registerAction(databaseQueryNewActionBig);

		IAction queryOpenAction = new QueryOpenAction(shell,
				Messages.queryOpenActionName, null);
		registerAction(queryOpenAction);

		IAction showSchemaEditorAction = new ShowSchemaEditorAction(
				shell, Messages.showSchemaActionName, null);
		registerAction(showSchemaEditorAction);

		// navigator view action
		IAction collapseAllAction = new CollapseAllAction(
				Messages.collapseAllActionName,
				loadImage("icons/action/collapseall.gif"),
				null);
		registerAction(collapseAllAction);

		IAction filterAction = new FilterSettingAction(
				Messages.filterSettingActionName,
				loadImage("icons/action/filter.gif"),
				null);
		registerAction(filterAction);

		IAction hiddenAction = new HiddenElementAction(shell,
				Messages.hiddenElementActionName,
				loadImage("icons/action/filter.gif"));
		registerAction(hiddenAction);

		IAction showHiddenElementsAction = new ShowHiddenElementsAction(
				shell, Messages.showAllActionName, null);
		registerAction(showHiddenElementsAction);

		IAction copyQueryEditorAction = new CopyQueryEditorAction(
				Messages.queryCopyActionName,
				loadImage("icons/action/new_query.png"));
		registerAction(copyQueryEditorAction);

		IAction renameColumnAction = new RenameColumnAction(shell,
				Messages.renameColumnAction, null);
		registerAction(renameColumnAction);
		//group actions
		IAction topGroupAction = new TopGroupAction(Messages.topGroupAction,
				null, null);
		registerAction(topGroupAction);

		IAction switchGroupModeAction = new SwitchGroupModeAction(Messages.topGroupAction,
				loadImage("icons/navigator/group.png"), null);
		registerAction(switchGroupModeAction);

		IAction topGroupItemAction = new TopGroupItemAction(
				Messages.topGroupItemAction, null, null);
		registerAction(topGroupItemAction);

		//report bug action
		IAction reportBugAction = new ReportBugAction(shell,
				Messages.reportBugAction,
				loadImage("icons/action/bug.png"), false);
		registerAction(reportBugAction);

		//report bug action
		IAction reportBugActionBig = new ReportBugAction(shell,
				Messages.reportBugActionBig,
				loadImage("icons/action/bug_big.png"),
				true);
		registerAction(reportBugActionBig);

		//run sql file action by fulei
		IAction runSQLFileAction = new RunSQLFileAction(shell,
				com.cubrid.common.ui.common.Messages.runSQLFileAction,
				loadImage("icons/navigator/sql.png"));
		registerAction(runSQLFileAction);

		IAction noticeAction = new NoticeAction(
				com.cubrid.common.ui.common.Messages.titleNoticeDialog,
				null);
		registerAction(noticeAction);

		IAction queryTunerAction = new QueryTunerAction(shell,
				com.cubrid.common.ui.common.Messages.actionQueryTuner,
				loadImage("icons/action/query_tuner.png"),
				loadImage("icons/action/query_tuner.png"));
		registerAction(queryTunerAction);

		IAction queryTunerRunAction = new QueryTunerRunAction(shell,
				com.cubrid.common.ui.common.Messages.actionQueryTuner,
				loadImage("icons/action/query_tuner.png"),
				loadImage("icons/action/query_tuner.png"));
		registerAction(queryTunerRunAction);

		// new query editor action
		IAction databaseQueryNewActionTb = new DatabaseQueryNewAction(
				shell,
				Messages.queryOpenActionNameBig,
				loadImage("icons/action/new_query_big.png"),
				loadImage("icons/action/new_query_big_disabled.png"),
				true);
		registerAction(databaseQueryNewActionTb);

		// navigation tree expand/unexpand on tree navigator
		IAction expandTreeItemAction = new ExpandTreeItemAction(
				com.cubrid.common.ui.spi.Messages.msgExpandAction,
				loadImage("icons/navigator/expand.png"),
				null);
		registerAction(expandTreeItemAction);

		IAction unExpandTreeItemAction = new UnExpandTreeItemAction(
				com.cubrid.common.ui.spi.Messages.msgUnExpandAction,
				loadImage("icons/navigator/unexpand.png"),
				null);
		registerAction(unExpandTreeItemAction);

		IAction batchRunAction = new BatchRunAction(
				com.cubrid.common.ui.query.Messages.batchRun,
				loadImage("icons/queryeditor/run_batch_sql.png"));
		registerAction(batchRunAction);

		/*Assign name action*/
		IAction assignNameAction = new AssignEditorNameAction(
				Messages.titleAssignNameAction,
				loadImage("icons/action/assign_name.gif"));
		registerAction(assignNameAction);

		IAction compareSchemaAction = new SchemaCompareAction(
				shell,
				Messages.compareSchema,
				null);
		registerAction(compareSchemaAction);

		IAction exportConnectionAction = new ConnectionUrlExportAction(
				shell,
				Messages.exportConnections,
				loadImage("icons/action/export_connection.png"));
		registerAction(exportConnectionAction);

		IAction importERwinAction = new ImportERwinAction(
				shell,
				Messages.compareSchemaERXml,
				loadImage("icons/action/erw_import.png"),
				null);
		registerAction(importERwinAction);

		IAction exportERwinAction = new ExportERwinAction(
				shell,
				Messages.exportSchemaERXml,
				loadImage("icons/action/erw_export.png"),
				null);
		registerAction(exportERwinAction);

		IAction helpDocumentAction = new HelpDocumentAction(Messages.helpActionName,
				loadImage("icons/help.gif"));
		registerAction(helpDocumentAction);

		IAction gotoLineAction = new GotoLineAction(shell,
				Messages.gotoLineActionName, null);
		registerAction(gotoLineAction);

		IAction reformatColumnsAliasAction = new ReformatColumnsAliasAction(
				shell, Messages.reformatColumsAliasActionName, null);
		registerAction(reformatColumnsAliasAction);

		IAction action = null;

		// Tools
		action = new SchemaCompareWizardAction(
				shell,
				Messages.schemaCompareWizardActionName,
				loadImage("icons/action/schema_compare_wizard.png"));
		registerAction(action);

		action = new DataCompareWizardAction(
				shell,
				Messages.dataCompareWizardActionName,
				loadImage("icons/action/data_compare_wizard.png"));
		registerAction(action);

		action = new OpenSchemaEditorAction(
				shell,
				Messages.schemaDesignerActionName,
				CommonUIPlugin.getImageDescriptor("icons/action/schema_edit_on.png"));
		ActionManager.getInstance().registerAction(action);

		action = new InstallMigrationToolkitAction(
				Messages.installMigrationActionName,
				loadImage("icons/action/cubrid-logo-32.gif"));
		action.setToolTipText(Messages.installMigrationToolkitActionName);
		registerAction(action);

//		action = new LaunchManagerAction(
//				Messages.launchManagerActionName,
//				loadImage("icons/action/launch_cm.gif"));
//		registerAction(action);

//		action = new LaunchBrowserAction(
//				Messages.launchBrowserActionName,
//				loadImage("icons/action/launch_cqb.gif"));
//		registerAction(action);

		action = new OpenCMPerspectiveAction(
				Messages.openCMViewActionName,
				loadImage("icons/action/launch_cm.gif"));
		registerAction(action);

		action = new OpenCQBPerspectiveAction(
				Messages.openCQBViewActionName,
				loadImage("icons/action/launch_cqb.gif"));
		registerAction(action);

//		action = new CMServiceAnalysisAction(
//				"CM Service Analysis Demo",
//				loadImage("icons/action/launch_cqb.gif"));
//		registerAction(action);

		action = new RestoreQueryEditorAction(
				com.cubrid.common.ui.common.Messages.restoreQueryEditorMenu, null);
		registerAction(action);

		action = new RunQueryAction(shell,
				com.cubrid.common.ui.query.Messages.btnRunThisQuery,
				loadImage("icons/queryeditor/query_run.png"));
		registerAction(action);
		
		action = new ParseSqlmapQueryAction(shell,
				com.cubrid.common.ui.query.Messages.btnParseThisSqlmapQuery,
				loadImage("icons/navigator/sql.png"));
		registerAction(action);

		action = new RunQueryPlanAction(shell,
				com.cubrid.common.ui.query.Messages.btnRunThisQueryPlan,
				loadImage("icons/queryeditor/query_execution_plan.png"));
		registerAction(action);

		action = new AddQueryToFavoriteAction(shell,
				com.cubrid.common.ui.query.Messages.btnAddSelectedQueryIntoFavorite,
				loadImage("icons/navigator/favorite_query.png"));
		registerAction(action);

		// initialize query making actions
		initMakeQueryActions(shell, ActionManager.getInstance());
	}

	private ImageDescriptor loadImage(String imagePath) {
		return CommonUIPlugin.getImageDescriptor(imagePath);
	}

	private void registerAction(IAction action) {
		ActionManager.getInstance().registerAction(action);
	}
}
