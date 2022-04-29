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

package com.cubrid.cubridmanager.ui.host;

import org.eclipse.osgi.util.NLS;

import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;

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
		NLS.initializeMessages(CubridManagerUIPlugin.PLUGIN_ID
				+ ".host.Messages", Messages.class);
	}
	// add host and edit host dialog related message
	public static String titleAddHostDialog;
	public static String titleConnectHostDialog;
	public static String titleEditHostDialog;
	public static String msgAddHostDialog;
	public static String msgConnectHostDialog;
	public static String msgEditHostDialog;
	public static String btnConnectHost;
	public static String btnAddHost;
	public static String btnBrowse;
	public static String msgConfirmCharset;	
	public static String btnTestConn;
	public static String msgTestConnSuccess;
	public static String msgSaveAndClose;
		
	public static String tipAddHostButton;
	public static String tipConnectHostButton1;
	public static String tipConnectHostButton2;
	public static String lblHostName;
	public static String lblAddress;
	public static String lblPort;
	public static String lblUserName;
	public static String lblPassword;
	public static String lblJdbcVersion;
	public static String errHostName;
	public static String errAddress;
	public static String errUserName;
	public static String errUserPassword;
	public static String errHostExist;
	public static String errAddressExist;
	public static String errDuplicateHost;
	public static String errPort;
	public static String errConnectionRefused;
	public static String errConnectionFailed;
	public static String errConnectionByBrokerConfig;
	public static String errUserPasswordConnect;
	public static String errConnectionReset;
	public static String errConnectTimedOut;
	public static String msgConfirmDeleteHost;
	public static String msgConfirmDisconnectHost;
	public static String msgConfirmDisconnectHostWithJob;
	public static String msgConfirmDeleteHostWithJob;
	public static String titleServerVersion;
	public static String errNoSupportDriver;
	public static String errSelectSupportDriver;
	public static String errNoSupportServerVersion;
	public static String errUserNotFound;
	public static String errConnectAddress;
	public static String btnConnectSave;
	public static String warnNoSupportLatestDriver;
	public static String warnRecommendDriver;
	
	//rename host name
	public static String lblName;
	public static String renameInvalidHostNameMSG;
	public static String renameMSGTitle;
	public static String renameDialogMSG;
	public static String renameShellTitle;
	public static String renameOKBTN;
	public static String renameCancelBTN;
	public static String renameHostDialogConfirmMsg;

	//change password dialog related message
	public static String lblOldPassword;
	public static String lblNewPassword;
	public static String lblPasswordConfirm;
	public static String titleChangePasswordDialog;
	public static String msgChangePasswordDialog;
	public static String msgChangeAdminPassword;
	public static String errOldPassword;
	public static String errNewPassword;
	public static String errPasswordConfirm;
	public static String errNotEqualPassword;
	public static String msgChangePassSuccess;

	public static String changePwdTaskName;
	public static String connHostTaskName;
	public static String btnSavePassword;
	
	public static String btnSetTimeOut;
	public static String lbl10Second;
	public static String lbl30Second;
	public static String lbl60Second;
	public static String lblNoLimit;
	//ExportConfigDialog
	public static String ttlExportShell;
	public static String ttlExportDialog;
	public static String dscExportDialog;
	public static String lblExportFilePath;
	public static String lblExportFileName;
	public static String lblExportFileType;
	public static String confFileType;
	public static String txtFileType;
	public static String lblFileCharset;
	public static String errExportSelectFile;
	public static String errExportFileType;
	public static String errUnsupportedCharset;
	public static String ttlSaveAsShell;
	public static String ttlSaveAsDialog;
	public static String dscSaveAsDialog;
	public static String msgConfirmOverrideFile;

	//ImportConfigDialog
	public static String ttlImportShell;
	public static String ttlImportDialog;
	public static String dscImportDialog;
	public static String lblImportFileName;
	public static String msgConfirmImport;
	public static String msgImportFileNoFound;
	public static String errImportSelectFile;
	public static String ttlOpenShell;
	public static String ttlOpenDialog;
	public static String dscOpenDialog;
	public static String btnOk;

	//EditConfigEditor
	public static String msgTipSaveAction;
	public static String msgTipOpenAction;
	public static String msgTipSaveasAction;

	//UtilHelp
	public static String getCmConfTaskRunning;
	public static String getBrokerConfTaskRunning;
	public static String getCubridConfTaskRunning;
	public static String getHaConfTaskRunning;

	//EditCmConfigAction,EditCubridConfigAction,EditHAConfigAction,EditBrokerConfigAction
	public static String msgEditorTooltip;

	//Import hosts
	public static String msgSelectWorkspace;
	public static String errInvalidWorkspace;
	
	// UserPasswordInputDialog
	public static String titleUserPassword;
	public static String msgInputNamePassword;
	public static String msgInvalidJdbcDriver;
	public static String msgConneted;
	public static String titleConnectionProgress;
	public static String msgClickToConnect;
	public static String colTaskName;
	public static String colTaskProgress;
	public static String colTaskDetails;

	public static String msgPasswordHelp;
	public static String errConnJdbcNotSet;

	//new multi connect server
	public static String multiConnectServerDialogTitle;
	public static String multiConnectServerDialogMessages;
	public static String multiConnectServerDialogColumnHostAddress;
	public static String multiConnectServerDialogColumnUser;
	public static String multiConnectServerDialogColumnStatus;
	public static String multiConnectServerDialogColumnErrMsg;
	public static String multiConnectServerDialogStatusConnected;
	public static String multiConnectServerDialogStatusDisonnected;
	public static String multiConnectServerDialogClose;
	
	//multi edit server 
	public static String multiEditServerDialogTitle;
	public static String multiEditServerDialogMessages;
	public static String multiEditServerDialogColumnName;
	public static String multiEditServerDialogColumnPort;
	public static String multiEditServerDialogColumnDriver;
	public static String multiEditServerDialogColumnPassword;
	public static String multiEditServerDialogColumnAutosavePassword;
	public static String multiEditServerDialogClose;
	
	public static String tblBrokerName;
	public static String tblBrokerStatus;
	public static String tblBrokerProcess;
	public static String tblPort;
	public static String tblServer;
	public static String tblQueue;
	public static String tblLongTran;
	public static String tblLongQuery;
	public static String tblErrQuery;
	public static String tblRequest;
	public static String tblAutoAdd;
	public static String tblTps;
	public static String tblQps;
	public static String tblConn;
	public static String tblSession;
	public static String tblSqllog;
	public static String tblLog;
	
	/*HostStatusEditor*/
	public static String itemRefresh;
	public static String itemSave;
	public static String itemExport;
	public static String itemSetting;
	public static String titleServerInfo;
	public static String titleDBInfo;
	public static String columnDB;
	public static String columnAutoStart;
	public static String columnDBStatus;
	public static String titleVolumeInfo;
	public static String columnData;
	public static String columnDataTip;
	public static String columnIndex;
	public static String columnIndexTip;
	public static String columnTemp;
	public static String columnTempTip;
	public static String columnGeneric;
	public static String columnGenericTip;
	public static String columnActiveLog;
	public static String columnArchiveLog;
	public static String titleBrokerInfo;
	public static String titleSystemInfo;
	public static String columnType;
	public static String columnMemmory;
	public static String columnCpu;
	public static String columnTps;
	public static String columnQps;
	public static String columnFreespace;
	public static String lblHost;
	public static String lblDBVersion;
	public static String lblBrokerVersion;
	public static String lblCubridPath;
	public static String lblDBPath;
	public static String taskGetServerInfo;
	public static String taskGetDBInfo;
	public static String taskGetVolumeInfo;
	public static String taskGetBrokerInfo;
	public static String lblRunning;
	public static String lblStoped;
	public static String lblNow;
	public static String lbl5MinAvg;
	public static String tipBrokerName;
	public static String tipBrokerStatus;
	public static String tipBrokerProcess;
	public static String tipPort;
	public static String tipServer;
	public static String tipQueue;
	public static String tipRequest;
	public static String tipTps;
	public static String tipQps;
	public static String tipFreespace;
	public static String tipLongTran;
	public static String tipLongQuery;
	public static String tipErrQuery;
	public static String lblHostInfo;
	public static String saveAutoStartipLabel;
	
	/*HA wizard*/
	public static String titleHAWizard;
	public static String msgUploading;
	public static String msgUploadingSuccess;
	public static String msgConfirmExitHAWizard;
	public static String lblKey;
	public static String lblValue;
	public static String errKeyEmpty;
	public static String errValueEmpty;
	public static String lblMaster;
	public static String lblMasterHost;
	public static String lblSlave;
	public static String lblSlaveHost;
	public static String errSelectSlaveServer;
	public static String errMasterHostEmpty;
	public static String errSlaveHostEmpty;
	public static String errMasterDBEmpty;
	public static String errSlaveDBEmpty;
	public static String errSelectDBDiff;
	public static String warnMasterPrimaryKeyNotice;
	
	public static String lblDatabase;
	public static String itemAddParameter;
	public static String itemEditParameter;
	public static String itemDeleteParameter;
	public static String errLoadCubridConf;
	public static String lblUser;
	public static String errLoadCubridHAConf;
	public static String txtServerConfirm;
	public static String txtModifyCubridConf;
	public static String txtModifyCubridHAConf;
	public static String txtAddParameters;
	public static String txtModifyParameters;
	public static String txtDeleteParameters;
	public static String msgSettingParameters;
	public static String titleSettingParameters;
	public static String descSettingHostPage;
	public static String descSettingCubridPage;
	public static String descSettingCubridHAPage;
	public static String descSettingConfirmPage;	
	public static String titleStartHAService;
	public static String msgStartHAService;
	public static String errDoStep1;
	public static String errDoStep2;
	public static String grpStep1;
	public static String grpStep2;
	public static String grpStep3;
	public static String txtStopService;
	public static String txtStartHAService;
	public static String txtStartCMSService;
	public static String txtHelp;
	public static String btnHaveDone;
	public static String msgConfirmDropParameter;
	
	public static String haStep1;
	public static String haStep2;
	public static String haStep3;
	public static String haStep4;
	public static String haStep5;
	
	//unify host config editor
	public static String unifyHostConfigEditorTitle;
	public static String unifyHostConfigDialogTitle;
	public static String unifyHostConfigDialogMessage;
	public static String unifyHostConfigDialogErrMsg;
	public static String unifyHostConfigDialogErrMsg2;
	public static String unifyHostConfigDialogHttpdConfBtnLabel;
	public static String unifyHostConfigDialogACLConfBtnLabel;
	
	public static String unifyHostConfigEditorLoadingDataMsg;
	public static String unifyHostConfigEditorLoadingDataMsg2;
	
	public static String unifyHostConfTableColumnPropName;
	public static String unifyHostBrokerConfTableTitle;
	
	public static String unifyHostCubridConfTableTitle;
	public static String confEditorTableMenuEditAnnotation;
	public static String annotationDialogOpenErrorMsg;
	
	public static String unifyHostConfigEditorSavingDataMsg;
	public static String unifyHostConfigEditorSavingDataMsg2;
	
	public static String unifyHostConfigEditorSavingErrMsg;
	public static String unifyHostConfigEditorSavingDataSuccessMsg;
	
	public static String unifyHostConfigEditorCheckErrMsg;
	public static String unifyHostConfigEditorCheckErrMsg1;
	public static String unifyHostConfigEditorCheckErrMsg2;
	public static String unifyHostConfigEditorCheckErrMsg3;
	public static String unifyHostConfigEditorCheckErrMsg4;
	public static String unifyHostConfigEditorCheckErrMsg5;
	
	public static String unifyHostConfigEditorAddColumnConfirmMsg;
	public static String unifyHostConfigEditorAddColumnMsg;
	public static String unifyHostConfigEditorDelColumnMsg;
	public static String unifyHostConfigEditorDelColumnConfirmMsg;
	public static String unifyHostConfigEditorAddCubridConfColumn;
	public static String unifyHostConfigEditorDelCubridConfColumn;

	public static String errNoLoginedServerToEditConfig;
	
	public static String titleGenCertDialog;
	public static String msgGenCertDialog;
	public static String msgGenCert;
	public static String titleRestartCMS;
	public static String msgRestartCMS;
	public static String grpOption;
	public static String lblCountry;
	public static String lblState;
	public static String lblCity;
	public static String lblOrganization;
	public static String lblEmail;
	public static String lblValid;
	public static String lblOption;
	public static String btnRemember;
	public static String btnGenerate;
	public static String btnNotGenerate;
	public static String lblValidNaverExpire;
	public static String lblValidThreeYear;
	public static String lblValidOneYear;
	public static String lblValidOneMonth;
	public static String lblValidOneWeek;
	
}
