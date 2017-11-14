/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search
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

package com.cubrid.common.ui.common;

import org.eclipse.osgi.util.NLS;

import com.cubrid.common.ui.CommonUIPlugin;

public class Messages extends NLS {
	static {
		NLS.initializeMessages(CommonUIPlugin.PLUGIN_ID + ".common.Messages", Messages.class);
	}

	public static String aboutMessage;
	public static String addNewGroup;
	public static String auoCheckUpdate;
	public static String brokerLogTopMergeDialogMessages;
	public static String brokerLogTopMergeDialogTitle;
	public static String brokerLogTopMergeExcelFullNameLabel;
	public static String brokerLogTopMergeExcelNameLabel;
	public static String brokerLogTopMergeExcelPathLabel;
	public static String brokerLogTopMergeOpenBtn;
	public static String brokerLogTopMergePartionLineDescription;
	public static String brokerLogTopMergePartionLineLabel;
	public static String brokerLogTopMergeQLabel;
	public static String brokerLogTopMergeResLabel;
	public static String btnAdd;
	public static String btnAsDefault;
	public static String btnBottom;
	public static String btnBrowse;
	public static String btnCancel;
	public static String btnCheckNewInfo;
	public static String btnClose;
	public static String btnInstall;
	public static String btnCompare;
	public static String btnDelete;
	public static String btnDown;
	public static String btnUpdateJdbc;
	public static String btnEdit;
	public static String btnFind;
	public static String btnIncludingLog;
	public static String btnJdbcAttr;
	public static String btnNameFilter;
	public static String btnNoticeDialogClose;
	public static String btnOK;
	public static String btnSave;
	public static String btnRest;
	public static String btnRunInDebugMode;
	public static String btnRunQuery;
	public static String btnSavePassword;
	public static String btnSearchTooltip;
	public static String btnStop;
	public static String btnTop;
	public static String btnUp;
	public static String btnIsAutoCompleteKeyword;
	public static String btnIsAutoCompleteTablesOrColumns;
	public static String grpUseDashboard;
	public static String btnUseDashboardHost;
	public static String btnUseDashboardDatabase;
	public static String cfmOverrideJdbc;
	public static String chkNoticeDialogSeen;
	public static String colFilePath;
	public static String columnElapsedTime;
	public static String columnStatus;
	public static String commentColumn;
	public static String jdbcDriverDownloadSuccessMsg;
	public static String cubridNewInfoEditorName;
	public static String cubridNoticeEditorName;
	public static String cubridNoticeIgnoreButtonLbl;
	public static String defaultGroupNodeName;
	public static String editGroup;
	public static String errCannotOpenExternalBrowser;
	public static String errCannotOpenInternalBrowser;
	public static String errCommonTip;
	public static String errDeleteJdbcConn;
	public static String errDeleteJdbcQuery;
	public static String errDeleteJdbcServer;
	public static String errDesc;
	public static String errDirNoExist;
	public static String errEditorNameEmpty;
	public static String errEditorNameTooLong;
	public static String errFileNotExist;
	public static String errFileNameIsEmpty;
	public static String errCreateFile;
	public static String errIlegalURL;
	public static String errInvalidWorkspace;
	public static String errNoDatabaseSelected;
	public static String errNoDir;
	public static String errNoReadable;
	public static String errNoSelectFile;
	public static String errNoSelectWorkspace;
	public static String errNoSupportDriver;
	public static String errNoSupported;
	public static String errNotFindClass;
	public static String errNoWorkspace;
	public static String errNoWritable;
	public static String errOIDValue1;
	public static String errOIDValue2;
	public static String errReportBugs;
	public static String errSelectOverTwoDb;
	public static String errSelectNonRunningDb;
	public static String errTooBigAttachment;
	public static String errWorkspaceUsed;
	public static String failCount;
	public static String failedErrorMessage;
	public static String failedSQL;
	public static String failedSQLlineNumber;
	public static String fileName;
	public static String groupNameExisted;
	public static String groupNameInvalid;
	public static String grpAttachment;
	public static String grpAttribute;
	public static String grpBrokerInfo;
	public static String grpDbInfo;
	public static String grpDesc;
	public static String grpIsAutoCompleteOnQueryeditor;
	public static String grpLayoutApplication;
	public static String iPColumn;
	public static String javaUrlColumn;
	public static String jdbcManagePageName;
	public static String jdbcOptionsErrMsg;
	public static String jobRestoringEditor;
	public static String labelGroupName;
	public static String labelManagerGroups;
	public static String lblColor0;
	public static String lblColor1;
	public static String lblColor2;
	public static String lblColor3;
	public static String lblColor4;
	public static String lblColor5;
	public static String lblConnectionTimeout;
	public static String lblDatabase;
	public static String lblDbIp;
	public static String lblDbJdbcVersion;
	public static String lblDbPassword;
	public static String lblDbUserName;
	public static String lblDeSelectAll;
	public static String lblEditorName;
	public static String lblExpandAll;
	public static String lblFilePath;
	public static String lblHost;
	public static String lblInvertSort;
	public static String lblJdbcAdvancedBasic;
	public static String lblJdbcAttr;
	public static String lblJDBCVersion;
	public static String lblLastModify;
	public static String lblLocation;
	public static String lblLoginBrokerPort;
	public static String lblLoginDatabaseName;
	public static String lblLoginServerName;
	public static String lblNameFilter;
	public static String lblNormalSort;
	public static String lblOIDValue;
	public static String lblQueryTimeout;
	public static String lblSavedPath;
	public static String lblSchemaComparisonBase;
	public static String lblSchemaComparisonTarget;
	public static String lblSearch;
	public static String lblSearchDesc;
	public static String lblSelectAll;
	public static String lblServerOS;
	public static String lblServerVersion;
	public static String lblTreeFilter;
	public static String lblUnExpandAll;
	public static String lblWorkspace;
	public static String lblZeroDateTimeBehavior;
	public static String maximizeWindowOnStartUp;
	public static String autoShowSchemaMiniInfo;
	public static String menuColor0;
	public static String menuColor1;
	public static String menuColor2;
	public static String menuColor3;
	public static String menuColor4;
	public static String menuColor5;
	public static String menuOther;
	public static String menuSwitchWorkspace;
	public static String msgAddingChildren;
	public static String msgAssignName;
	public static String msgCheckNewVersionUrl;
	public static String msgConfirmSwitch;
	public static String msgCubridHelpSiteUrl;
	public static String msgCubridHomePageUrl;
	public static String msgCubridJdbcInfoUrl;
	public static String msgCubridOnlineForumUrl;
	public static String msgCubridProjectSiteUrl;
	public static String msgCubridToolsNewFeatures;
	public static String msgCubridToolsSiteUrl;
	public static String msgExportConnection;
	public static String msgExportConnectionFailed;
	public static String msgFilterSettingDialog;
	public static String titleViewSQLLog;
	public static String lblLine;
	public static String lblSql;
	public static String lblError;
	// [TOOLS-2425]Support shard broker
	public static String msgChooseShardIdDialog;

	public static String msgFunctionNum;
	public static String msgJdbcManagementDialog;
	public static String msgJdbcOptionDialog;
	public static String msgJdbcOptionKeyDuplicated;
	public static String msgLoading;
	public static String msgLoadingChildren;
	public static String msgNewFeatures;
	public static String msgNotFoundResult;
	public static String msgOIDNavigatorDialog;
	public static String msgProcedureNum;
	public static String msgReportBugDialog;
	public static String msgRestoreEditorStatus;
	public static String msgSchemaComparison;
	public static String msgSearchButton;
	public static String msgSearchKeyword;
	public static String msgSelectAttachment;
	public static String msgSelectWorksapce;
	public static String msgSelectWorkspaceDialog;
	public static String msgSerialNum;
	public static String msgSPNum;
	public static String msgSuccessReportBugs;
	public static String msgSysTableNum;
	public static String msgSysViewNum;
	public static String msgToggleExitConfirm;
	public static String msgTriggerNum;
	public static String msgUserTableNum;
	public static String msgUserViewNum;
	public static String msgZeroDateTimeBehavior1;
	public static String msgZeroDateTimeBehavior2;
	public static String msgZeroDateTimeBehavior3;
	public static String nameColumn;
	public static String nameExportConnectionTask;
	public static String passwordColumn;
	public static String phpUrlColumn;
	public static String portColumn;
	public static String prefTitleGeneral;
	public static String runSQLConfirm;
	public static String runSQLDialogCheckBtnDescription;
	public static String runSQLDialogDatabaseLabel;
	public static String runSQLDialogErrMsg1;
	public static String runSQLDialogErrMsg2;
	public static String runSQLDialogExcelPathLabel;
	public static String runSQLDialogFilePathLabel;
	public static String runSQLDialogLabelCommitCount;
	public static String runSQLDialogLabelCommitCountTooltip;
	public static String runSQLDialogLabelThreadCount;
	public static String runSQLDialogLabelThreadCountTooltip;
	public static String runSQLDialogMessage;
	public static String runSQLDialogTitle;
	public static String runSQLOpenBtn;
	public static String runSQLExportDialogErrMessage;
	public static String runSQLExportDialogMessage;
	public static String runSQLExportDialogTitle;
	public static String runSQLExportSuccessMessage;
	public static String runSQLFileAction;
	public static String runSQLFileName;
	public static String runSQLLoadingFileErrorMessage;
	public static String runSQLLoadingFileProgressTitle;
	public static String runSQLSelectFiles;
	public static String runSQLStatusFinished;
	public static String runSQLStatusRunning;
	public static String runSQLStatusStopped;
	public static String runSQLStatusWaiting;
	public static String runSQLStatusFailed;
	public static String sheetNameConnections;
	public static String shellAssignName;
	public static String showToolTipActionName;
	public static String SQLCounts;
	public static String subTaskDownload;
	public static String successCount;
	public static String tabSearch;
	public static String taskDownload;
	public static String tblColDriverVersion;
	public static String tblColJarPath;
	public static String tblColJdbcAttrName;
	public static String tblColJdbcAttrValue;
	public static String tipFind;
	public static String tipNoSupportJdbcVersion;
	public static String titleAboutDialog;
	public static String titleAssignName;
	public static String titleConfirm;
	public static String titleExitConfirm;
	public static String titleExportConnection;
	public static String titleFilterSettingDialog;
	public static String errGetConnectionFailed;
	
	public static String titleExportErrorLog;
	public static String msgExportErrorLog;
	public static String msgExportErrorLogSuccess;
	public static String msgExportErrorLogFailed;
	
	/**/
	public static String queryTunerAction;

	// [TOOLS-2425]Support shard broker
	public static String titleChooseShardIdDialog;
	public static String titleChooseShardValDialog;

	public static String titleGroupSettingDialog;
	public static String titleJdbcAdvancedOptionView;
	public static String titleJdbcManagementDialog;
	public static String titleJdbcOptionDialog;
	public static String titleNoticeDialog;
	public static String titleOIDNavigatorDialog;
	public static String titleReportBugDialog;
	public static String titleResult;
	public static String titleSchemaComparison;
	public static String titleDataComparison;
	public static String titleSelectWorkspaceDialog;
	public static String titleSuccess;
	public static String titleSwitchWorkspaceDialog;
	public static String tooltipBack;
	public static String tooltipForward;
	public static String tooltipGo;
	public static String tooltipRefresh;
	public static String tooltipStop;
	public static String tooltipTabSearch;
	public static String topLevelElements;
	public static String urlSearch;
	public static String userColumn;
	public static String viewFailedSQLDialogTitle;
	public static String warningDeleteJdbc;
	public static String warnAutoShowSchemaMiniInfo;
	public static String miniSchemaCopyColumnWithComma;
	public static String miniSchemaCopyColumnWithNewline;
	public static String miniSchemaCopyDdl;
	public static String miniSchemaCopyColumnTooltip;
	public static String miniSchemaCopySelectQuery;
	public static String miniSchemaCopyInsertQuery;
	public static String miniSchemaCopyUpdateQuery;
	public static String miniSchemaCopyDdlTooltip;

	// [TOOLS-2425]Support shard broker
	public static String btnUseShardIdHint;
	public static String btnUseShardValHint;

	//cubrid broker conf
	public static String cubridBrokerConfOpenFileDialogTitle;
	public static String cubridBrokerConfOpenFileDialogMessage;
	public static String cubridBrokerConfOpenFileDialogFilePathLabel;
	public static String cubridBrokerConfOpenFileDialogErrMsg;
	public static String cubridBrokerConfEditorSaveItemLabel;
	public static String cubridBrokerConfEditorSaveAsItemLabel;
	public static String cubridBrokerConfEditorCTabItemSource;
	public static String cubridBrokerConfEditorColumnPropName;
	public static String cubridBrokerConfEditorTableMenuEditAnnotation;
	public static String cubridBrokerConfEditAnnotationDialogOpenErrorMsg;
	public static String cubridBrokerConfEditAnnotationDialogErrorMsg;
	public static String cubridBrokerConfEditorSaveFailedMsg;
	public static String cubridBrokerConfEditorSaveSuccessMsg;
	public static String cubridBrokerConfEditorAddPropItemLabel;
	public static String cubridBrokerConfEditorDeletePropItemLabel;
	public static String cubridBrokerConfEditorAddBrokerConfItemLabel;
	public static String cubridBrokerConfEditorDeleteBrokerConfItemLabel;
	public static String cubridBrokerConfEditorDeletePropertyMsg;
	public static String cubridBrokerConfEditorDeleteBrokerConfMsg;
	public static String cubridBrokerConfEditorErrMsg1;
	public static String cubridBrokerConfEditorErrMsg2;
	public static String cubridBrokerConfEditorErrMsg3;
	public static String cubridBrokerConfEditorErrMsg4;
	public static String cubridBrokerConfEditorSaveAsDialogTitle;

	public static String cubridBrokerConfEditorBrokerTitle;
	
	public static String cubridBrokerConfEditorSaveConfirm;
	public static String cubridBrokerConfEditorSaveAsConfirm;
	public static String cubridBrokerConfEditorDeleteBrokerConfConfirm;
	public static String cubridBrokerConfEditorDeleteBrokerPropConfirm;
	
	//export table definition
	public static String exportTableDefinitionAction;
	public static String exportTableDefinitionTitle;
	public static String exportTableDefinitionMessage;
	public static String exportTableDefinitionExcelPathLabel;
	public static String exportTableDefinitionExcelNameLabel;
	public static String exportTableDefinitionExcelFullNameLabel;
	public static String exportTableDefinitionExcelFileCharsetLabel;
	public static String exportTableDefinitionExcelLayoutTypeLabel;
	public static String exportTableDefinitionExportSuccess;
	public static String exportTableDefinitionDialogErrMsg;
	public static String exportTableDefinitionDefaultCharset;
	public static String exportTableDefinitionExcelLayoutTypeSimple;
	public static String exportTableDefinitionExcelLayoutTypeGeneric;
	
	public static String exportTableDefinitionProgressTaskWrite;
	public static String exportTableDefinitionProgressTaskTableList;
	public static String exportTableDefinitionProgressTaskWriteTable;

	public static String msgTableCommentNotSelectedDb;
	public static String msgTableCommentNotLoginedDb;
	public static String msgTableCommentNotDBA;
	public static String msgTableCommentConfirm;
	public static String msgTableCommentAlertTitle;
	public static String msgTableCommentCancel;
	public static String msgTableCommentAlreadyInstalled;
	public static String msgTableCommentInstallNotSupport;

	// [TOOLS-2425]Support shard broker
	public static String errTableCommentCannotInstallOnShard;

	public static String errTableCommentInstall;
	public static String msgTableCommentSuccess;
	
	public static String exportTableDefinitionCell1;
	public static String exportTableDefinitionCell2;
	public static String exportTableDefinitionCell3;
	public static String exportTableDefinitionCell4;
	public static String exportTableDefinitionCell5;
	public static String exportTableDefinitionCell6;
	public static String exportTableDefinitionCell7;
	public static String exportTableDefinitionCell8;
	public static String exportTableDefinitionCell9;
	public static String exportTableDefinitionCell10;
	public static String exportTableDefinitionCell11;
	public static String exportTableDefinitionCell12;
	public static String exportTableDefinitionCell13;
	public static String exportTableDefinitionCell14;
	public static String exportTableDefinitionCell15;
	public static String exportTableDefinitionCell16;
	public static String exportTableDefinitionCell17;
	public static String exportTableDefinitionCell18;
	public static String exportTableDefinitionCell19;
	public static String exportTableDefinitionCell20;
	public static String exportTableDefinitionCell21;
	public static String exportTableDefinitionCell22;
	public static String exportTableDefinitionCell23;
	public static String exportTableDefinitionCell24;
	public static String exportTableDefinitionCell25;
	public static String exportTableDefinitionCell26;
	public static String exportTableDefinitionCell27;
	public static String btnConfirmRunModQueryAutoCommit;
	public static String errCanNotChooseShardId;
	
	// erwin
	public static String msgFinished;
	public static String msgGenerateInfo;
	public static String msgExportSuccess;
	public static String msgImportSchemaFromERwin;
	public static String msgAll;
	public static String errExportFailed;
	public static String errFileCannotRead;
	public static String errInvalidFile;
	public static String lblSubjectArea;
	public static String msgSubjectAll;
	public static String titleExportSchema;
	public static String titleImportSchemaFromERwin;
	public static String infoMissingSelectFile;

	public static String dashboardConfirmRefreshDataMsg;
	
	public static String titleNoticeDashboard;
	public static String titleAddHostBtn;
	public static String titleConHostBtn;
	public static String titlePreferencesBtn;
	public static String titleHelpBtn;
	public static String titleCUBRIDManager;
	public static String titleCUBRIDQuery;
	public static String titleCUBRIDMigration;
	public static String titleCommonAction;
	public static String titleReleaseNews;
	public static String titleMajorFeatures;
	public static String titleHowStart;
	public static String titleTutorials;
	public static String titleAnnouncement;
	public static String titleTechTrends;
	public static String titleUsefulLinks;
	public static String networkConnectionError;

	public static String titleChooseLaunchCMTFolder;
	public static String msgChooseLaunchCMTFolder;
	public static String errCanNotFoundCMTOnFolder;
	public static String confirmUseCMT;
	public static String confirmUseCM;
	public static String confirmUseCQB;

	public static String errSelectLoginDbToRunSQL;
	public static String expConDialogCopyBtnLabel;
	public static String expConDialogCopyErrorMsg;
	public static String expConDialogCopySuccessMsg;
	public static String msgConnectionUrlExported;
	
	public static String msgAlertUpdateJdbc;
	public static String msgAlertNoDrivers;
	
	public static String actionQueryTuner;

	public static String lblQuickViewColInfo;

	public static String restoreQueryEditorTitle;
	public static String restoreQueryEditorMessage;
	public static String restoreQueryEditorConfirm;
	public static String restoreQueryEditorRestoreFailed;
	public static String restoreQueryEditorRestoreSuccess;
	public static String restoreQueryEditorMenu;
	public static String errNoRestoreQueryEditor;

	public static String titleSqlFavorite;
	public static String titleSqlFavoriteDetail;
	public static String msgSqlFavoriteDetail;
	public static String msgDeleteFavorite;
	public static String msgConfirmAddFavorite;
	public static String errCanNotFindQueryEditor;
	public static String btnConfirmAddFile;
	public static String btnConfirmSaveFile;
	public static String btnRemoveFavorite;
	public static String btnDeleteFavoriteFile;
	public static String lblOpenFileFromFavorite;
	public static String lblRunFileFromFavorite;
	public static String lblAddFileFromFavorite;
	public static String lblSaveFileFromFavorite;
	public static String lblDeleteFileFromFavorite;
	public static String lblFileName;
	public static String lblMemo;
	public static String btnSaveToFavorite;
	public static String errCanNotSaveFileIntoFavorite;
	public static String errDuplicatedNameFavorite;
	public static String errSaveFailedFavorite;
	
	//Show dashboard dialog
	public static String lblShowDashboard;
	public static String lblAutoRefreshSecond;
	public static String lblAutoRefreshSecondUnit;
	public static String btnShowDashboard;
	public static String titleShowDashboard;
	public static String databaseDashboardAutoRefreshConfErrMsg;
}
