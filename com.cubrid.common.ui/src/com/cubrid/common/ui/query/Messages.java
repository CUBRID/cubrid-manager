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
package com.cubrid.common.ui.query;

import org.eclipse.osgi.util.NLS;

import com.cubrid.common.ui.CommonUIPlugin;

/**
 * This is message bundle classes and provide convenience methods for
 * manipulating messages.
 *
 * @author pangqiren 2009-3-2
 *
 */
public class Messages extends
		NLS {

	//query editor toolbar
	public static String open;
	public static String save;
	public static String insertCommit;
	public static String insertRollback;
	public static String saveAs;
	public static String commit;
	public static String rollback;
	public static String autoCommit;
	public static String autoCommitLabelOn;
	public static String autoCommitLabelOff;
	public static String title;
	public static String clear;
	public static String msgClear;
	public static String error;
	public static String change;
	public static String close;
	public static String closeOthers;
	public static String closeAll;
	public static String cancel;
	public static String changeShardId;
	public static String run;
	public static String btnRunThisQuery;
	public static String btnParseThisSqlmapQuery;
	public static String btnRunThisQueryPlan;
	public static String runMultiQuery;
	public static String btnYes;
	public static String btnNo;

	public static String update;
	public static String updateOk1;
	public static String deleteOk;
	public static String alterOk;
	public static String createOk;
	public static String dropOk;
	public static String updateOk2;
	public static String insertOk;
	public static String insertOkWithAutocommit;
	public static String queryOk;
	public static String queryFail;
	public static String errWhere;
	public static String queryPlanTip;
	public static String lblPlanQuery;
	public static String proRunQuery;
	public static String undoTip;
	public static String redoTip;
	public static String stopBtn;
	public static String stopBtnTip;
	public static String findNextTip;
	public static String titleCreateCode;
	public static String msgCreatedSqlPhpCode;
	public static String msgCreatedSqlJavaCode;
	public static String errCreatedSqlNotSelected;
	// editor and/or query explain

	public static String unCommentTip;
	public static String unIndentTip;
	public static String indentTip;
	public static String commentTip;
	public static String info;
	public static String transActive;
	public static String cantChangeStatus;
	public static String formatTip;
	public static String settingQueries;

	public static String findTip;
	public static String findWhat;
	public static String replaceWith;
	public static String option;
	public static String matchCase;
	public static String wrapSearch;
	public static String matchWholeWord;
	public static String direction;
	public static String up;
	public static String down;
	public static String findBtn;
	public static String replaceBtn;
	public static String replaceAllBtn;

	public static String noDbSelected;
	public static String selfDefinedDb;
	public static String indDefinedDb;
	public static String plsSelectDb;
	public static String errEditorRunning;

	public static String tooManyRecord;
	public static String skipOrNot;
	public static String queryStat;
	public static String warning;
	public static String querySeq;
	public static String queryWithoutSeq;
	public static String second;
	public static String totalRows;
	public static String delete;
	public static String confirmDelMsg;
	public static String errorHead;
	public static String copyClipBoard;
	public static String insertRecord;
	public static String oidNavigator;
	public static String detailView;
	public static String allExport;
	public static String selectExport;
	public static String runError;
	public static String showOneTimeTip;

	public static String autoCommitLabel;

	// [TOOLS-2425]Support shard broker
	public static String shardBrokerLabel;
	public static String shardBrokerAlert;
	public static String shardIdLabel;
	public static String shardValLabel;

	public static String searchUnitInstances;
	public static String pageUnitInstances;
	public static String showMultiPageConfirm;
	public static String getOid;
	public static String getOidOn;
	public static String getOidOff;
	public static String btnKeywordLowercase;
	public static String btnNoAutoUppercase;
	public static String btnWithoutPromptSave;
	public static String lblCharSet;
	public static String btnTestConnection;
	public static String changeFont;
	public static String fontExample;
	public static String restoreDefault;
	public static String brokerGrp;
	public static String brokerIP;
	public static String brokerPort;
	public static String queryTitle;
	public static String errInvalidBrokerIp;
	public static String errInvalidBrokerPort;
	public static String errInvalidCharSet;
	public static String lblDatabaseIP;
	public static String errInvalidDatabaseIp;
	public static String btnUseScientificNotation;
	public static String lblLobLoadSize;
	public static String fileSave;
	public static String overWrite;
	public static String columnCountOver;
	public static String export;

	public static String noContext;

	public static String connCloseConfirm;
	public static String changeDbConfirm;
	public static String beSure;

	public static String notSaveNull;

	public static String column;
	public static String value;
	public static String updateBtn;
	public static String closeBtn;

	public static String cfmUpdateChangedValue;
	public static String titleRowDetailDialog;
	public static String msgRowDetailDialog;
	public static String msgValueNoChanged;
	public static String msgValueNoChangedTitle;
	public static String errMsgServerNull;
	public static String exportOk;

	public static String lblColumnName;
	public static String lblColumnValue;
	public static String saveResource;
	public static String saveConfirm;

	public static String tooltip_qedit_explain_new;
	public static String tooltip_qedit_explain_open;
	public static String tooltip_qedit_explain_save;
	public static String tooltip_qedit_explain_saveas;
	public static String tooltip_qedit_explain_history_switch;
	public static String tooltip_qedit_explain_history_show_hide;
	public static String tooltip_qedit_explain_history_delete;
	public static String tooltip_qedit_explain_display_mode;
	public static String tooltip_qedit_result_show_hide;
	public static String tooltip_qedit_log_show_hide;

	public static String explain_history_delete_message;
	public static String explain_history_delete_error;

	public static String qedit_lastpage;
	public static String qedit_nextpage;

	public static String commitUpdate;
	public static String waiting_export;

	public static String tooltip_queryplanenable;
	public static String qedit_result;
	public static String qedit_logsresult;
	public static String tooltip_qedit_find;
	public static String tooltip_qedit_replace;
	public static String qedit_find;
	public static String qedit_notfound;
	public static String qedit_replace;
	public static String qedit_replaceall;
	public static String qedit_replacecomplete;

	public static String qedit_sqleditor_folder;
	public static String qedit_result_folder;
	public static String qedit_plan_folder;
	public static String qedit_plan;
	public static String qedit_plan_curfile_title;
	public static String qedit_plan_history_col1;
	public static String qedit_plan_history_col2;
	public static String qedit_plan_history_col3;
	public static String qedit_plan_history_col4;
	public static String qedit_plan_tree_simple_col1;
	public static String qedit_plan_tree_simple_col2;
	public static String qedit_plan_tree_simple_col3;
	public static String qedit_plan_tree_simple_col3_dtl;
	public static String qedit_plan_tree_simple_col5;
	public static String qedit_plan_tree_simple_col5_dtl;
	public static String qedit_plan_tree_simple_col6;
	public static String qedit_plan_tree_simple_col7;
	public static String qedit_plan_tree_simple_col8;
	public static String qedit_plan_tree_simple_col8_dtl;
	public static String qedit_plan_tree_simple_col9;
	public static String qedit_plan_filename_error;
	public static String qedit_plan_open_file_error;
	public static String qedit_plan_save_file_error;
	public static String qedit_plan_invalid_plan_file;
	public static String qedit_plan_save_change_question;
	public static String qedit_plan_save_change_question_title;
	public static String qedit_plan_clear_question;
	public static String qedit_plan_clear_question_title;
	public static String qedit_plan_tree_term_name_index;
	public static String qedit_plan_tree_term_name_join;
	public static String qedit_plan_tree_term_name_select;
	public static String qedit_plan_tree_term_name_filter;
	public static String qedit_plan_sql_copy;
	public static String qedit_plan_raw_plan_copy;
	public static String qedit_select_table_not_exist_in_db;
	public static String qedit_tip_run_query;

	public static String task_querydesc;

	public static String msgChangeConnectionInfo;
	public static String errBrokerPort;

	public static String exportDataTaskName;
	public static String updateDataTaskName;
	public static String lblColumnType;
	public static String btnImport;
	public static String btnExport;
	public static String grpColumnValue;
	public static String grpSelectFile;
	public static String msgExportData;

	public static String tipSetPstmt;
	public static String tipExecStat;

	//set encoding dialog related message
	public static String titleOpenFileDialog;
	public static String titleSaveFileDialog;
	public static String titleOpenFileDialogDetail;
	public static String msgOpenFileDialogDetail;
	public static String titleSaveFileDialogDetail;
	public static String msgSaveFileDialogDetail;
	public static String btnDefaultCharset;
	public static String btnOtherCharset;

	//DDL template
	public static String txtSelect;
	public static String txtSelectColumn;
	public static String txtInsert;
	public static String txtUpdate;
	public static String txtDelete;
	public static String msgSelect;
	public static String msgSelectColumn;
	public static String msgInsert;
	public static String msgUpdate;
	public static String msgDelete;
	public static String titleData;
	public static String titleDDL;
	public static String titleColumn;
	public static String titleIndex;

	public static String titleGetMetaDataErr;
	public static String msgGetMetaDataErr;
	public static String titleGetDataErr;
	public static String msgGetDataErr;
	//Job Name
	public static String getInfoJobName;

	//Columns table
	public static String tblColumnPK;
	public static String tblColumnName;
	public static String tblColumnMemo;
	public static String tblColumnDataType;
	public static String tblColumnAutoIncr;
	public static String tblColumnDefault;
	public static String tblColumnNotNull;
	public static String tblColumnUnique;
	public static String tblColumnShared;
	public static String tblColumnInherit;
	public static String tblColumnClass;
	//Columns view
	public static String tblColViewName;
	public static String tblColViewDataType;
	public static String tblColViewDefaultType;
	public static String tblColViewDefaultValue;
	//Index and FK
	public static String lblIndexes;
	public static String tblColumnIndexName;
	public static String tblColumnIndexRule;
	public static String tblColumnIndexType;
	public static String tblColumnOnColumns;
	public static String lblFK;
	public static String tblColumnFK;
	public static String tblColumnColumnName;
	public static String tblColumnColumns;
	public static String tblColumnDeleteRule;
	public static String tblColumnForeignTable;
	public static String tblColumnForeignColumnName;
	public static String tblColumnUpdateRule;
	public static String tblColumnCacheColumn;

	// export data
	public static String msgExportSelectedResults;
	public static String msgExportAllResults;
	public static String msgExportAllQueryResults;

	public static String errDbConnect;
	public static String msgInputSqlText;

	public static String errMsgExecuteInResult;
	public static String recentlyUsedSQLColumnRunTime;
	public static String recentlyUsedSQLColumnElapseTime;
	public static String recentlyUsedSQLColumn;
	public static String recentlyUsedSQLColumnLOG;
	public static String recentlyUsedSQLHelp;
	public static String sql_history_delete_message;
	public static String tooltip_qedit_sql_history_delete;
	public static String btn_qedit_sql_history_delete;
	public static String sql_history_delete_error;
	public static String sql_history_delete_success;
	public static String qedit_sql_history_folder;
	public static String qedit_sql_history;

	public static String tabTitleData;
	public static String tabTitleDDL;
	public static String tabTitleColumn;
	public static String tabTitleIndex;

	public static String queryHistory;
	public static String treeSelected;

	public static String batchRun;
	public static String btnAddSelectedQueryIntoFavorite;
	public static String titleBatchRunMessage;
	public static String msgBatchRunMessage;
	public static String btnBatchRun;
	public static String btnBatchPaste;
	public static String btnBatchClose;
	public static String titleBatchRunConfirm;
	public static String msgBatchRunConfirm;
	public static String msgBatchRunPasteConfirm;
	public static String msgBatchRunSqlFile;
	public static String msgBatchRunMemo;
	public static String msgBatchRunRegdate;
	public static String errBatchRunDel;
	public static String errBatchRunDelWithFile;
	public static String lblRemoveFromSqlFavorite;
	public static String lblDeleteFromSqlFavorite;
	public static String lblOpenSqlFavorite;
	public static String lblRunSqlFavorite;
	public static String msgDoYouWantExecuteSql;
	public static String errNoConnectionBatchRun;
	public static String msgDoYouWantExecuteWithNotSameCharset;
	public static String errFileNotExist;

	public static String msgDuplicatedDeleteTarget;
	public static String msgDuplicatedUpdateTarget;
	public static String errModifiedNotMoving;
	public static String errModifiedOneTable;
	public static String msgCommitEdited;
	public static String msgRollbackEdited;
	public static String msgActiveTran;
	public static String errNotEditable;
	public static String errNoPrimaryKey;
	public static String errNotInOneTable;
	public static String errSelectDatabaseFirst;
	public static String errHasNoCommit;
	public static String errCanNotSortWhenEditing;

	public static String multiDBQueryCompRefreshToolItem;
	public static String qedit_multiDBQueryComp_folder;
	public static String qedit_multiDBQueryComp_tabItem;
	public static String qedit_multiDBQueryComp_tree_dbCol;
	public static String qedit_multiDBQueryComp_tree_commentCol;
	public static String qedit_multiDBQueryComp_tree_indexCol;
	public static String qedit_multiDBQueryComp_tree_indexErr;
	public static String qedit_multiDBQueryComp_title_shell;
	public static String qedit_multiDBQueryComp_run_err;
	public static String qedit_multiDBQueryComp_noticeTitle;
	public static String qedit_multiDBQueryComp_noticeMsg;
	public static String qedit_multiDBQueryComp_noticeToolbarMsg;
	public static String qedit_multiDBQueryComp_runConfirm;
	public static String refresh;

	public static String qedit_multiSQLQueryComp_title_shell;

	//BrokerLogParserDialog
	public static String brokerLogItemTooltip;
	public static String brokerLogParserDialogTitle;
	public static String brokerLogParserDialogMessages;
	public static String brokerLogParserDialogBtnParse;
	public static String brokerLogParserDialogLabelBrokerLog;
	public static String brokerLogParserDialogLabelSQL;
	public static String brokerLogParserDialogErrMsg;

	public static String errorFormatPos;
	public static String errorToken;

	public static String errNotEditableOnStat;
	public static String errEditableOnResultTab;

	public static String msgAutocommitStateNoticeAuto;
	public static String msgAutocommitStateNoticeTranx;

	public static String errCanNotSaveASQLFile;
	public static String makeInsertFromSelectedRecord;
	public static String makeUpdateFromSelectedRecord;
	public static String canNotMakeQueryBecauseNoSelected;
	public static String makeQueryFromSelectedRecordError;
	public static String makeQueryFromSelectedRecordNotSupported;
	public static String makeQueryFromSelectedRecordNoPK;
	public static String makeQueryFromSelectedRecordHasMultipleTableNames;
	public static String gotoLineTitle;
	public static String gotoLineMessage;
	public static String gotoLineBtn;
	public static String gotoLineCancelBtn;
	public static String gotoLineError;
	public static String reformatTableAliasTitle;
	public static String reformatTableAliasMessage;
	public static String reformatTableAliasBtn;
	public static String reformatTableAliasCancelBtn;
	public static String reformatTableAliasError;
	public static String reformatTableAliasError2;
	public static String tooltipHowToExpandLogPane;
	public static String warnModifiableQueryOnAutoCommitMode;
	public static String msgQueryEach;
	public static String quickColInfoNotNull;
	public static String quickColInfoUnique;
	public static String quickColInfoDataType;
	public static String quickColInfoColumnName;
	public static String quickColInfoTableName;
	public static String quickColInfoDefaultValue;
	public static String quickColInfoAutoIncr;
	public static String quickColInfoPk;
	public static String quickColInfoIndex;

	public static String msgMaxOpenNum;

	public static String quickQueryBuilderTitle;
	public static String quickQueryBuilderLabel;
	public static String quickQueryBuilderLoading;
	public static String quickQueryBuilderBtnSelect1;
	public static String quickQueryBuilderBtnSelect2;
	public static String quickQueryBuilderBtnInsert;
	public static String quickQueryBuilderBtnUpdate;

	//query editor tab item
	public static String queryEditorAddTabItemTooltip;
	public static String queryEditorDefaultTabItemTooltip;
	public static String queryEditorTabItemName;
	public static String queryEditorTabItemTooltip;
	public static String shardMultiQueryTitle;
	public static String shardMultiQueryMessage;
	public static String shardMultiQueryStartLabel;
	public static String shardMultiQueryEndLabel;
	public static String shardMultiQueryDialogTitle;
	public static String shardMultiQueryRunButton;
	public static String shardMultiQueryCloseButton;
	public static String shardMultiQueryStartShardIdInputErrorMsg;
	public static String shardMultiQueryEndShardIdInputErrorMsg;

	//Plan display style menu
	public static String lblPlanText;
	public static String lblPlanTree;
	public static String lblPlanGraphic;

	public static String msgConfirmEditorClose;
	public static String msgConfirmEditorSave;
	public static String msgConfirmEditorNotSaved;
	public static String msgConfirmEditorExistFile;

	//filter query result
	public static String menuAll;
	public static String menuMore;
	public static String menuCaseSensitive;
	public static String menuInCaseSensitive;
	public static String menuUsingWildCards;
	public static String menuUsingRegex;
	public static String menuMatchFromStart;
	public static String menuMatchExactly;
	public static String menuMatchAnywhere;

	public static String titleFilterChooser;
	public static String lblFilterChooser;
	public static String btnSelectAll;
	public static String colColumn;

	//multiple queries data compare
	public static String diffData;
	public static String extraColumns;
	public static String extraRows;
	public static String baseTableMsg;
	public static String showHighlight;
	public static String hideHighlight;

	//Query Tuner
	public static String titleAddQueryTunerList;
	public static String titleRenameQueryTunerList;
	public static String lblQueryName;
	public static String msgQueryTunerNameEmpty;
	public static String msgQueryTunerNameExist;
	public static String btnOK;
	public static String btnCancel;

	public static String lblProject;
	public static String itemTooltipAdd;
	public static String itemTooltipRemove;
	public static String itemTooltipRename;
	public static String tabItemQueryTuner;
	public static String tabItemQueryCompare;
	public static String lblQuery;
	public static String itemTooltipCompare;
	public static String itemTooltipRemoveQuery;
	public static String itemTooltipQuery;
	public static String columnItem;
	public static String columnFetches;
	public static String columnDirties;
	public static String columnIORead;
	public static String columnIOWrite;
	public static String columnCost;
	public static String errUnselectHistory;
	public static String errCurrentQueryEmpty;
	public static String errCompareQueryEmpty;
	public static String titleQueryTuner;
	public static String lblNow;
	public static String lblLast;
	public static String itemTooltipRunAgain;
	public static String lblElapsedTime;
	public static String lblElapsedCancelTime;
	public static String lblDisConnect;
	public static String msgDisConnect;
	public static String titleAddQueryRecord;
	public static String titleRenameQueryRecord;
	public static String msgQueryRecordNameEmpty;
	public static String msgQueryRecordNameExist;
	public static String errSelectQueryForTuning;
	public static String msgQueryTunerNotSavedProject;
	public static String errNoQueryInProject;
	public static String ttSaveQueryTuning;
	public static String lblQueryTuningName;
	public static String subTitleAddQueryRecord;
	public static String subTitleRenameQueryRecord;
	public static String confirmDeleteQueryPlanOnTuner;
	public static String confirmDeleteTuningProject;
	public static String lblTuneModeOrgSql;
	public static String lblTuneModeNewSql;
	public static String lblTuneModeOrgPlan;
	public static String lblTuneModeNewPlan;
	public static String lblTuneModeResult;
	public static String ttAlertLeftShow;
	public static String ttAlertRightShow;
	public static String ttAlertLeftShowTitle;
	public static String ttAlertRightShowTitle;
	public static String ttQeToolbarTuneModeTitle;
	public static String ttQeToolbarTuneModeMSg;
	public static String lblFilterSearchOption;
	public static String lblFilterSearch;
	public static String lblSearchLimit;
	public static String lblItemRunMulti;
	public static String lblItemRefreshMulti;
	public static String ttMultiQueryTitle;
	public static String lblTuneCompareQueryBtn;
	public static String lblPlanRawBtn;
	public static String lblPlanTreeBtn;
	public static String lblPlanGraph;
	public static String lblComparePlan;
	public static String errShowDetailNoSelected;
	public static String errShowDetailMultiSelected;
	public static String errShowDetailFailed;
	public static String lblPlanEditTable;
	public static String lblPlanEditIndex;
	public static String msgPlanEditTable;
	public static String msgPlanEditIndex;
	public static String errPlanEditNotFound;
	public static String errPlanEditNoAction;

	/*ExportQueryResultDialog*/
	public static String exportDataMsgTitle;
	public static String importFileNameLBL;
	public static String lblFileCharset;
	public static String exportSelectFileERRORMSG;
	public static String errUnsupportedCharset;
	public static String exportShellTitle;
	public static String exportButtonName;
	public static String lblExportFromCache;
	public static String tipExportFromCache;

	public static String grpWhereExport;
	public static String lblFileType;
	public static String grpParsingOption;
	public static String lblThreadCount;
	public static String lblJDBCCharset;
	public static String grpDataOptions;
	public static String btnFirstLineAsCol;
	public static String grpDelimiter;
	public static String lblRow;
	public static String lblCol;
	public static String grpNullOption;
	public static String btnOther;
	public static String canceledBecauseOfExistsWithFolderName;

	public static String lblNameComma;
	public static String lblNameTab;
	public static String lblNameQuote;
	public static String lblNameEnter;

	public static String errNoConnected;
	public static String btnBrowse;
	public static String lblFilePath;
	public static String btnClose;

	public static String csvFileType;
	public static String xlsFileType;
	public static String xlsxFileType;
	public static String sqlFileType;
	public static String obsFileType;
	public static String txtFileType;
	public static String lblIndexType;

	public static String titleSelectFolderToBeExported;
	public static String msgSelectFolderToBeExported;
	public static String errDidNotSelectedQuery;
	public static String msgDoYouWantToAddAllQueryInEditor;

	public static String titleEditField;
	public static String titleViewFieldContent;
	public static String errTextTypeNotMatch;
	public static String msgEditFieldData;
	public static String msgViewFieldData;

	//QueryResultTableCalcInfo
	public static String msgCalcInfoCount;
	public static String msgCalcInfoSUM;
	public static String msgCalcInfoAVG;

	public static String msgConnectionTimeOut;
	public static String msgConnectionError;

	public static String lblSqlmapConditions;
	public static String lblSqlmapParameters;
	public static String lblSqlmapUse;
	public static String lblSqlmapCondition;
	public static String lblSqlmapName;
	public static String lblSqlmapValue;
	public static String lblSqlmapType;
	public static String mnuSqlmapAdd;
	public static String mnuSqlmapModify;
	public static String mnuSqlmapRemove;
	public static String msgSqlmapRemove;
	public static String btnSqlmapPaste;
	public static String titleSqlmapSupports;
	public static String msgSqlmapSupports;
	public static String titleSqlmapUpdated;
	public static String msgSqlmapUpdated;
	public static String titleSqlmapDefineBindValue;
	public static String msgSqlmapDefineBindValue;
	public static String lblSqlmapInputVariableName;
	public static String lblSqlmapInputVariableValue;
	public static String lblSqlmapInputVariableType;
	public static String msgSqlmapInputVariableName;
	public static String msgSqlmapInputVariableType;

	public static String errQeditNotOpenForConnectionFull;

	static {
		NLS.initializeMessages(CommonUIPlugin.PLUGIN_ID + ".query.Messages",
				Messages.class);
	}
}