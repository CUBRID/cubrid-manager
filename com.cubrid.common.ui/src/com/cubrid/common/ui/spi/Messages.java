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
package com.cubrid.common.ui.spi;

import org.eclipse.osgi.util.NLS;

import com.cubrid.common.ui.CommonUIPlugin;

/**
 * 
 * This is message bundle classes and provide convenience methods for
 * manipulating messages.
 * 
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class Messages extends
		NLS {

	static {
		NLS.initializeMessages(CommonUIPlugin.PLUGIN_ID + ".spi.Messages",
				Messages.class);
	}

	public static String unsupportedJRETitle;
	public static String unsupportedJRE;
	public static String productNameCM;
	public static String productNameCQB;
	public static String viewNameCM;
	public static String viewNameCQB;
	public static String titleWarning;
	public static String titleError;
	public static String titleConfirm;
	public static String titleInformation;
	public static String btnYes;
	public static String btnNo;
	public static String btnAlwaysYes;
	public static String btnOk;
	public static String msgRunning;
	public static String msgContextMenuCopy;
	public static String msgContextMenuPaste;
	// loader related message
	public static String msgTablesFolderName;
	public static String msgViewsFolderName;
	public static String msgSystemTableFolderName;
	public static String msgSystemViewFolderName;
	public static String msgSpFolderName;
	public static String msgTriggerFolderName;
	public static String msgSerialFolderName;
	public static String msgFunctionFolderName;
	public static String msgProcedureFolderName;
	public static String msgTableColumnsFolderName;
	public static String msgTableIndexesFolderName;
	public static String msgUserFolderName;

	// action related message
	// common action
	public static String undoActionName;
	public static String redoActionName;
	public static String copyActionName;
	public static String copyAllActionName;
	public static String cutActionName;
	public static String findReplaceActionName;
	public static String pasteActionName;
	public static String formatActionName;
	public static String sqlPstmtActionName;
	public static String oidNavigatorActionName;
	public static String createSqlCodePhpActionName;
	public static String createSqlCodeJavaActionName;
	public static String exportConnections;
	public static String compareSchema;
	public static String compareSchemaERXml;
	public static String exportSchemaERXml;
	public static String openActionName;
	public static String serviceDashboardActionName;

	// table and view
	public static String tableNewActionName;
	public static String preparedTableDataMenuName;
	public static String pstmtOneDataActionName;
	public static String pstmtMultiDataActionName;
	public static String selectByOnePstmtDataActionName;
	public static String selectByMultiPstmtDataActionName;
	public static String insertOneByPstmtActionName;
	public static String insertMultiByPstmtActionName;
	public static String tableDeleteAllActionName;
	public static String truncateTableActionName;
	public static String deleteAllRecordsActionName;
	public static String createLikeTableActionName;
	public static String tableSelectCountActionName;
	public static String tableSelectActionName;
	public static String tableInsertActionName;
	public static String toolsActionName;
	public static String tableExportActionName;
	public static String tableImportActionName;
	public static String tableRenameActionName;
	public static String viewRenameActionName;
	public static String tableEditActionName;
	public static String createViewActionName;
	public static String editViewActionName;
	public static String propertyViewActionName;
	public static String tableDropActionName;
	public static String viewDropActionName;
	public static String updateStatisticsActionName;
	public static String copyCreateSQLToClipboardActionName;
	public static String copyInsertPstmtToClipboardActionName;
	public static String copySelectPstmtToClipboardActionName;
	public static String copySelectStmtToClipboardActionName;
	public static String copyDDLToClipboardMenuName;
	public static String viewDataMenuName;
	public static String inputDataMenuName;
	public static String copySQLMenuName;
	public static String copyGrantToClipboardActionName;
	public static String columnSelectSqlActionName;
	public static String columnSelectCountActionName;
	public static String copyDeleteStmtToClipboardAction;
	public static String copyUpdateStmtToClipboardAction;
	// trigger
	public static String dropTriggerActionName;
	public static String newTriggerActionName;
	public static String alterTriggerActionName;
	// serial
	public static String deleteSerialActionName;
	public static String createSerialActionName;
	public static String editSerialActionName;

	// procedure
	public static String addFunctionActionName;
	public static String editFunctionActionName;
	public static String deleteFunctionActionName;
	public static String addProcedureActionName;
	public static String editProcedureActionName;
	public static String deleteProcedureActionName;

	// query editor
	public static String queryNewActionName;
	public static String queryNewActionNameBig;
	public static String queryOpenActionName;
	public static String queryOpenActionNameBig;
	public static String queryCopyActionName;
	public static String showSchemaActionName;
	public static String sqlQuickHelpActionName;
	public static String titleAssignNameAction;

	//navigator view action
	public static String collapseAllActionName;
	public static String filterSettingActionName;
	public static String hiddenElementActionName;
	public static String showAllActionName;
	public static String copySQLToFileAction;
	public static String tableToJavaCodeAction;
	public static String tableToPhpCodeAction;
	public static String tableMoreName;
	public static String queryNewCustomActionName;
	public static String renameColumnAction;

	public static String topGroupAction;
	public static String topGroupItemAction;
	public static String groupSettingAction;
	public static String groupNodeProperty;

	public static String reportBugAction;
	public static String reportBugActionBig;

	//navigation toolbar
	public static String msgExpandAction;
	public static String msgUnExpandAction;
	public static String msgQuickTabAction;

	public static String inputMethodActionName;

	//BrokerLogTopMergeProgress
	public static String brokerLogTopMergeProgressSuccess;

	public static String tablesDetailInfoPartProgressTaskName;

	//table cell editor
	public static String msgFileExist;
	public static String errFileDelete;
	public static String errFileRename;

	public static String btnText;
	public static String btnImage;
	public static String btnSetNull;
	public static String btnImport;
	public static String btnExport;
	public static String titleInsertField;
	public static String msgExportFieldData;
	public static String msgExportSuccess;
	public static String lblSize;
	public static String errCharset;
	public static String msgLoadIncomplete ;
	public static String errDataInvalid;
	public static String titleEditData;
	public static String titleViewData;
	public static String msgEditData;
	public static String msgViewData;
	
	public static String helpActionName;
	public static String gotoLineActionName;
	public static String reformatColumsAliasActionName;
	public static String schemaCommentInstallActionName;
	public static String changeShardActionName;
	public static String schemaCompareWizardActionName;
	public static String dataCompareWizardActionName;
	public static String schemaDesignerActionName;
	public static String installMigrationActionName;
	public static String installMigrationToolkitActionName;
	public static String launchManagerActionName;
	public static String launchBrowserActionName;
	public static String openCMViewActionName;
	public static String openCQBViewActionName;
	public static String lblMakeSelectQueryGrp;
	public static String lblMakeSelectQuery;
	public static String lblMakeSelectPstmtQuery;
	public static String lblMakeUpdateQuery;
	public static String lblMakeDeleteQuery;
	public static String lblMakeInsertQuery;
	public static String lblMakeCreateQueryGrp;
	public static String lblMakeCreateQuery;
	public static String lblMakeCloneQuery;
	
	public static String tipRecommendDriver;
	public static String tipNoSupportJdbcVersion;
	public static String errCommonTip;
	public static String errLockNoUseTemporary;
	
	public static String btnBinary;
	public static String btnHex;
	public static String btnOpenByExternalProgram;
	public static String titleviewFieldContent;
	public static String noDataExport;
	public static String confirmDataChanged;
	public static String btnOK;
	public static String btnCancel;
	public static String titleSuccess;

	public static String msgConnectBrokerFailure;
		
	public static String msgConfirmLogoutConnwithJob;
	public static String msgConfirmDeleteConnwithJob;
	
	public static String moreNodeLabel;

}