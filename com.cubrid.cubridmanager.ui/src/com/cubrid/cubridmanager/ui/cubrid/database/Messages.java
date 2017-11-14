/*
 * Copyright (C) 2008 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.cubridmanager.ui.cubrid.database;

import org.eclipse.osgi.util.NLS;

import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;

/**
 * Message bundle classes. Provides convenience methods for manipulating
 * messages.
 *
 * @author pangqiren 2009-3-2
 *
 */
public class Messages extends
		NLS {

	static {
		NLS.initializeMessages(CubridManagerUIPlugin.PLUGIN_ID
				+ ".cubrid.database.Messages", Messages.class);
	}
	public static String btnBrowse;
	public static String msgSelectDir;
	public static String msgSelectFile;
	// database related message
	public static String msgConfirmStopDatabase;
	public static String msgConfirmLogoutDatabase;
	public static String msgConfirmLogoutDBwithJob;
	public static String msgConfirmDeleteDBwithJob;
	public static String titleCreateDbDialog;

	// create database general information page
	public static String titleWizardPageGeneral;
	public static String msgWizardPageGeneral;
	public static String grpGeneralInfo;
	public static String lblDbName;
	public static String lblPageSize;
	public static String lblLogPageSize;
	public static String grpCharsetInfo;
	public static String lblUserDefinedCharset;
	public static String errUserDefinedCharset;
	public static String grpGenericVolInfo;
	public static String lblVolSize;
	public static String lblGenericVolPath;
	public static String grpLogVolInfo;
	public static String lblLogVolPath;
	public static String grpAutoStartInfo;
	public static String btnAutoStart;
	public static String lblAutoStartInfo;
	public static String errDbName;
	public static String errDbNameLength;
	public static String errDbExist;
	public static String errGenericVolSize;
	public static String errGenericVolPath;
	public static String errLogSize;
	public static String errLogVolPath;
	public static String lblLocale;
	public static String msgLocaleNotice;
	public static String msgLocaleManual;
	public static String msgLocaleManualUrl91;
	public static String msgLocaleManualUrl92;
	public static String msgLocaleManualUrl93;
	public static String createDBFailedMsg;
	// create database additional volume information page
	public static String titleWizardPageAdditional;
	public static String msgWizardPageAdditional;
	public static String grpAddtionalVolInfo;
	public static String lblVolName;
	public static String lblVolPath;
	public static String lblVolType;
	public static String btnAddVolume;
	public static String msgVolumeList;
	public static String tblColumnVolName;
	public static String tblColumnVolType;
	public static String tblColumnVolSize;
	public static String tblColumnVolPath;
	public static String btnDelVolume;
	public static String errVolumePath;
	public static String errVolumeSize;
	public static String errNoIndexAndDataVolume;
	// create database automation adding volume information page
	public static String titleWizardPageAuto;
	public static String msgWizardPageAuto;
	public static String grpVolPurposeData;
	public static String btnUsingAuto;
	public static String lblOutOfSpaceWarning;
	public static String lblExtensionPage;
	public static String grpVolPurposeIndex;
	public static String errDataOutOfSpace;
	public static String errDataVolumeSize;
	public static String errDataVolumePageNum;
	public static String errIndexOutOfSpace;
	public static String errIndexVolumeSize;
	public static String errIndexVolumePageNum;
	// create database set dba password page
	public static String titleWizardPageSetDbaPass;
	public static String msgWizardPageSetDbaPass;
	public static String grpSetPassword;
	public static String lblPassword;
	public static String lblPasswordConfirm;
	public static String errPassword;
	public static String errPasswordConfirm;
	public static String errNotEqualPass;
	// create database database information page
	public static String titleWizardPageDbInfo;
	public static String msgWizardPageDbInfo;
	public static String msgDbInfoList;
	public static String msgCreateDbrearJobName;
	public static String msgCreateDBComplete;
	public static String msgExistDbFound;
	// backup database dialog related message
	public static String titleBackupDbDialog;
	public static String grpBackuInfo;
	public static String grpBackupHistoryInfo;
	public static String lblBackupLevel;
	public static String lblBackupDir;
	public static String lblParallelBackup;
	public static String btnCheckConsistency;
	public static String btnDeleteLog;
	public static String btnCompressVol;
	public static String btnSafeBackup;
	public static String msgBackupHistoryList;
	public static String tblColumnBackupLevel;
	public static String tblColumnBackupDate;
	public static String tblColumnSize;
	public static String tblColumnBackupPath;
	public static String msgConfirmBackupDb;
	public static String titleSuccess;
	public static String msgBackupSuccess;
	public static String errBackupDir;
	public static String errParallerBackup;
	public static String errVolumeName;
	// unload database dialog related message
	public static String titleUnloadDbDialog;
	public static String msgUnloadDbDialog;
	public static String grpDbInfo;
	public static String lblTargetDbName;
	public static String lblTargetDir;
	public static String grpUnloadTarget;
	public static String grpSchema;
	public static String grpData;
	public static String btnAll;
	public static String btnSelectedTables;
	public static String btnNotInclude;
	public static String grpUnloadOption;
	public static String btnUseDelimite;
	public static String btnIncludeRef;
	public static String btnPrefix;
	public static String btnHashFile;
	public static String btnNumOfCachedPage;
	public static String btnNumOfInstances;
	public static String btnLoFileCount;
	public static String msgSuccessUnload;
	public static String errTargetDir;
	public static String errNoTable;
	public static String errPrefix;
	public static String errHashFile;
	public static String errNumOfCachedPage;
	public static String errNumOfInstances;
	public static String errLoFileCount;
	public static String msgUnloadDbRearJobName;
	// load database related message
	public static String titleLoadDbDialog;
	public static String msgLoadDbDialog;
	public static String lblUserName;
	public static String lblPassWord;
	public static String grpLoadFile;
	public static String btnSelectFileFromList;
	public static String tblColumnLoadType;
	public static String tblColumnPath;
	public static String tblColumnDate;
	public static String btnSelectFileFromRemoteSys;
	public static String btnSelectFileFromLocalSys;
	public static String btnLoadSchema;
	public static String btnLoadObj;
	public static String btnLoadIndex;
	public static String btnLoadTrigger;
	public static String grpLoadOption;
	public static String btnCheckSyntax;
	public static String btnInsCount;
	public static String btnNoUseOid;
	public static String btnNoUseStatistics;
	public static String btnUseErrorFile;
	public static String btnIgnoreClassFile;
	public static String errLoadSchema;
	public static String errLoadOjbects;
	public static String errLoadIndex;
	public static String errLoadTrigger;
	public static String errLoadFileFromSys;
	public static String errLoadFileFromList;
	public static String errInsertCount;
	public static String errControlFile;
	public static String errClassFile;
	public static String errNoSelectedPath;
	// restore database related message
	public static String titleRestoreDbDialog;
	public static String msgRestoreDbDialog;
	public static String grpDbName;
	public static String lblDbNameRestore;
	public static String grpRestoredDate;
	public static String grpRecoveryPath;
	public static String btnSelectDateAndTime;
	public static String btnBackupTime;
	public static String btnRestoreDate;
	public static String lblDate;
	public static String lblTime;
	public static String btnLevel2File;
	public static String btnLevel1File;
	public static String btnLevel0File;
	public static String btnShowBackupInfo;
	public static String grpPartialRecovery;
	public static String btnPerformPartialRecovery;
	public static String btnUserDefinedRecoveryPath;
	public static String errYear;
	public static String errMonth;
	public static String errDay;
	public static String errHour;
	public static String errMinute;
	public static String errSecond;
	public static String errLevel2File;
	public static String errLevel1File;
	public static String errLevel0File;
	public static String errRecoveryPath;
	public static String msgRestoreSuccess;
	public static String msgRestoreDBRearJobName;
	public static String grpRestoredData;
	public static String btnSelectBackupInfo;
	public static String btnDbPath;
	public static String grpDbPath;
	public static String errNoSelectBackupInfo;
	public static String errBackupFileNoExist;
	// rename database dialog message
	public static String titleRenameDbDialog;
	public static String msgRenameDbDialog;
	public static String lblNewDbName;
	public static String btnForceDelBackupVolume;
	public static String btnExtendedVolumePath;
	public static String btnRenameIndiVolume;
	public static String tblColumnCurrVolName;
	public static String tblColumnNewVolName;
	public static String tblColumnCurrDirPath;
	public static String tblColumnNewDirPath;
	public static String errExtendedVolPath;
	public static String msgRenameDBRearJobName;
	public static String msgRenameDBComplete;
	// Backup db volume information dialog
	public static String titleBackupDbVolInfoDialog;
	public static String msgBackupDbVolInfoDialog;
	//Backup db dialog
	public static String msgBackupDBRearJobName;
	// unload db result dialog realted message
	public static String titleUnloadDbResultDialog;
	public static String msgUnloadDbResultDialog;
	public static String tblColumnTable;
	public static String tblColumnRowCount;
	public static String tblColumnProgress;
	// create directory dialog related message
	public static String titleCreateDirDialog;
	public static String msgCreateDirDialog;
	public static String msgDirList;
	// load database result dialog related message
	public static String titleLoadDbResultDialog;
	public static String msgLoadDbResultDialog;
	public static String msgLoadDBRearJobName;
	// login database dialog related message
	public static String titleLoginDbDialog;
	public static String msgLoginDbDialog;
	public static String msgLoginQueryEditorDialog;
	public static String lblDbUserName;
	public static String lblLoginServerName;
	public static String lblLoginDatabaseName;
	public static String lblLoginBrokerPort;
	public static String lblDbPassword;
	public static String lblDbJdbcVersion;
	public static String grpBrokerInfo;
	public static String grpAddeHosts;
	public static String lblAddedHost;
	public static String lblDbIp;
	public static String lblBackground;
	public static String grpAdvancedJDBC;
	public static String lblComment;
	public static String lblDescComment;

	public static String errUserName;
	public static String errUnauthorizedUser;
	// overide file dialog related message
	public static String titleOverideFileDialog;
	public static String msgOverrideFileDialog;
	public static String msgOverrideFileList;
	public static String tblColumnFile;
	public static String btnConnectSave;

	// copy database
	public static String msgSelectDB;
	public static String errInputLogDirectory;
	public static String errInput;
	public static String errInputTargetDb;
	public static String warnYesNoOverWrite;
	public static String errDesitinationDbExist;
	public static String errNotEnoughSpace;
	public static String errDatabaseLength;
	public static String warnYesNoCopyDb;
	public static String warnYesNoCopyDbSpaceOver;
	public static String msgCopyDBComplete;
	public static String msgCopyDbRearJobName;
	// transaction
	public static String killTransactionName;
	public static String menuKillTransaction;

	// check database
	public static String titleCheckDbDialog;
	public static String btnRepair;
	public static String grpCheckDescInfo;
	public static String lblCheckDescInfo;
	public static String lblCheckDbName;
	public static String msgCheckDbDialog;
	public static String msgCheckSuccess;
	public static String msgCheckDbRearName;
	// Compact Database
	public static String titleCompactDbDialog;
	public static String grpCompactDescInfo;
	public static String lblCompactDescInfo;
	public static String msgCompactDbDialog;
	public static String msgQuestionSure;
	public static String lblCompactDbName;
	public static String msgCompactConfirm;
	public static String msgCompactSuccess;
	public static String titleFailure;
	public static String errCompactInfo;
	public static String msgCompactDbRearJobName;
	public static String titleCompactDbVerboseDialog;
	public static String msgCompactDbVerboseDialog;
	public static String msgVerboseBtn;
	// copy database
	public static String titleCopyDbDialog;
	public static String msgCopyDbDialog;
	public static String grpDbSourceName;
	public static String lblSrcDbName;
	public static String lblSrcDbPathName;
	public static String lblSrcLogPathName;
	public static String grpDbDestName;
	public static String lblDescDbName;
	public static String lblDescDbPathName;
	public static String lblDescLogPathName;
	public static String tblColumnCurrentVolName;
	public static String tblColumnCopyNewVolName;
	public static String tblColumnCopyNewDirPath;
	public static String btnCopyVolume;
	public static String errCopyInputTitle;
	public static String errCopyDbName;
	public static String errCopyNameTitle;
	public static String errCopyName;
	public static String btnDeleteSrcDb;
	public static String btnReplaceDb;
	public static String lblCopyFreeDiskSize;
	public static String lblCopyDbSize;
	public static String msgCopyShellText;
	public static String btnBrowseName;
	public static String lblVolumePathName;

	// directory
	public static String msgDirectoryDbDialog;
	// delete dababase
	public static String titleDeleteDbDialog;
	public static String msgDeleteDbDialog;
	public static String lblDeleteDbName;
	public static String btnDelBakup;
	public static String tblColDelDbVolName;
	public static String tblColDelDbVolPath;
	public static String tblColDelDbChangeDate;
	public static String tblColDelDbVolType;
	public static String tblColDelDbTotalSize;
	public static String tblColDelDbRemainSize;
	public static String tblColDelDbVolSize;
	public static String btnDeldbPath;
	public static String msgDeleteDbConfirm;
	public static String lblVolumeInfomation;
	// param dump
	public static String titleParamDumpDialog;
	public static String msgParamDumpDialog;
	public static String btnBoth;
	public static String grpParamDumpDescInfo;
	public static String lblParamDumpDescInfo;
	public static String lblParamDumpDbName;
	public static String tblTtlParamName;
	public static String tblTtlServer;
	public static String tblTtlClient;
	// plan dump
	public static String titlePlanDumpDialog;
	public static String msgPlanDumpDialog;
	public static String btnDrop;
	public static String grpPlanDumpDescInfo;
	public static String lblPlanDumpDescInfo;
	public static String lblPlanDumpDbName;
	// Kill Transaction
	public static String grpTransactionInfo;
	public static String lblTransactionUserName;
	public static String lblTransactionHostName;
	public static String lblTransactionProcessId;
	public static String lblTransactionProgramName;
	public static String lblTransactionKillType;
	public static String titleKillTransactionDialog;
	public static String itemKillOnly;
	public static String itemKillSameName;
	public static String itemKillSameHost;
	public static String itemKillSameProgram;
	public static String msgKillTransactionDialog;
	public static String msgKillOnlyConfirm;
	public static String msgKillSameUserConfirm;
	public static String msgKillSameHostConfirm;
	public static String msgKillSameProgramConfirm;
	public static String msgKillSuccess;
	// lockinfoDetail
	public static String titleLockInfoDetailDialog;
	public static String tblColTranIndex;
	public static String tblColGrantedMode;
	public static String tblColCount;
	public static String tblColNsubgranules;
	public static String grpBlockedHolder;
	public static String tblColLockTranIndex;
	public static String tblColLockGrantedMode;
	public static String tblColLockCount;
	public static String tblColLockBlockedMode;
	public static String tblColLockStartWaitingAt;
	public static String tblColLockWaitForNsecs;
	public static String grpLockWaiter;
	public static String tblColWaiterTranIndex;
	public static String tblColWaiterBlockedMode;
	public static String tblColWaiterStartWaitingAt;
	public static String tblColWaiterWaitForNsecs;
	public static String msgLockInfoDetailDialog;
	public static String lblObjectId;
	public static String lblObjectType;
	public static String grpLockHolders;
	// LockInfo
	public static String titleLockInfoDialog;
	public static String tabItemClientInfo;
	public static String tabItemObjectLock;
	public static String grpLockSetting;
	public static String grpClientsCur;
	public static String tblColLockInfoIndex;
	public static String tblColLockInfoPname;
	public static String tblColLockInfoUid;
	public static String tblColLockInfoHost;
	public static String tblColLockInfoPid;
	public static String tblColLockInfoIsolationLevel;
	public static String tblColLockInfoTimeOut;
	public static String grpLockTable;
	public static String tblColLockInfoOid;
	public static String tblColLockInfoObjectType;
	public static String tblColLockInfoMode;
	public static String tblColLockInfoNumHolders;
	public static String tblColLockInfoNumBlockedHolders;
	public static String tblColLockInfoNumWaiters;
	public static String msgLockInfoDialog;
	public static String lblLockEscalation;
	public static String lblRunInterval;
	public static String lblCurrentLockedObjNum;
	public static String lblMaxLockedObjNum;
	// new directory
	public static String titleCreateNewDialog;
	public static String msgCreateNewDirInformation;
	public static String tblColDirectoryVolume;
	public static String msgCreateNewDialog;
	// Optimize db
	public static String lblOptimizeDbName;
	public static String lblOptimizeClassName;
	public static String grpOptimizeDesc;
	public static String lblOptimizeDesc;
	public static String msgOptimizeDbInformation;
	public static String titleOptimizeDbDialog;
	public static String errOptimizeSuccess;
	public static String errOptimizeFail;
	public static String msgAllClass;
	// Transaction
	public static String titleTransactionDialog;
	public static String grpTransaction;
	public static String tblColTranInfoTranIndex;
	public static String tblColTranInfoUserName;
	public static String tblColTranInfoHost;
	public static String tblColTranInfoProcessId;
	public static String tblColTranInfoProgramName;
	public static String msgTransactionDialog;
	public static String lblActiveTransaction;
	// Delete Db Confirm
	public static String titleDeleteDbConfirmDialog;
	public static String msgDeleteDbConfirmDialog;
	public static String msgInputDbaPassword;
	public static String msgErrorPassword;
	public static String msgErrorAuth;
	public static String msgCheckPassword;
	// start database
	public static String msgStartDbWithJob;
	//stop database
	public static String msgStopDbWithJob;

	public static String getDbSpaceInfoTaskName;
	public static String loadDbBackupInfoTaskName;
	public static String loadDbUnloadInfoTaskName;
	public static String startDbTaskName;
	public static String stopDbTaskName;
	public static String loadAllClassTaskName;
	public static String delDbTaskName;
	public static String loginDbTaskName;
	public static String loadBackupVolInfo;
	public static String btnSavePassword;

	//multiple database login
	public static String multiDatabaseLoginDialogTitle;
	public static String multiDatabaseLoginDialogMessages;
	public static String multiDatabaseLoginDialogColumnHostAddress;
	public static String multiDatabaseLoginDialogColumnDbName;
	public static String multiDatabaseLoginDialogColumnUser;
	public static String multiDatabaseLoginDialogColumnErrMsg;
	public static String multiDatabaseLoginDialogColumnStatus;
	public static String multiDatabaseLoginDialogStatusLogin;
	public static String multiDatabaseLoginDialogStatusNotLogin;
	public static String multiDatabaseLoginDialogEditLabel;
	public static String multiDatabaseLoginDialogClose;

	//database dashbord
	public static String databaseTableHitRatioColumnLabel;
	public static String volumnTableLastModifyTimeColumnLabel;
	public static String volumnTableFreesizeColumnLabel;
	public static String volumnTableTotalsizeColumnLabel;
	public static String volumnTableVolumnNameColumnLabel;
	public static String volumnTableVolumnTypeColumnLabel;
	public static String volumnTableUsedAndRemainedSizeColumnLabel;
	public static String brokerTableLastConnectTimeColumnLabel;
	public static String brokerTableIDColumnTip;
	public static String brokerTableLQSColumnTip;
	public static String brokerTablePIDColumnTip;
	public static String exportDashboardDialogBtnExport;
	public static String exportDashboardDialogTitle;
	public static String exportDashboardDialogMessage;
	public static String exportDashboardDialogErrMessage;
	public static String exportDashboardDialogErrMessage2;
	public static String exportDashboardSuccessMessage;
	public static String exportDashboardFailedMessage;
	public static String exportDashboardDialogLblFileName;
	public static String exportDashboardDBTableTitle;
	public static String exportDashboardVolumnTableTitle;
	public static String exportDashboardBrokerTableTitle;
	public static String exportDashboardLockTableTitle;
	public static String exportDashboardMenuRestartServer;
	public static String exportDashboardMenuShowSQLLog;
	public static String exportDashboardMenuShowSlowQueryLog;
	public static String exportDashboardOpenSQLLogErrMsg;
	public static String exportDashboardLoadDbWork;
	public static String exportDashboardLoadVolumnWork;
	public static String exportDashboardLoadBrokerWork;
	public static String exportDashboardLoadLcokAndTransactionWork;
	public static String exportDashboardLoadDataError;
	public static String exportDashboardLoadDataError1;

	public static String databaseDashboardStartAutoRefreshTooltip;
	public static String databaseDashboardStopAutoRefreshTooltip;
	public static String databaseDashboardAutoRefreshConfTooltip;
	public static String databaseDashboardSettingTooltip;
	public static String databaseDashboardExportToExcel;

	public static String msgTestConnSuccess;
	public static String msgWhetherStartDB;
	public static String confirmLoaddbWillBeChangedPassword;

}