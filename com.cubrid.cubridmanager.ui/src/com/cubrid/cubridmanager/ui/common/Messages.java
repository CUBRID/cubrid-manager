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

package com.cubrid.cubridmanager.ui.common;

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
        NLS.initializeMessages(
                CubridManagerUIPlugin.PLUGIN_ID + ".common.Messages", Messages.class);
    }
    // common button message
    public static String btnOK;
    public static String btnCancel;
    public static String btnDetail;
    public static String btnRefresh;
    public static String btnDelete;
    public static String btnFind;
    public static String btnAdd;
    public static String btnEdit;
    public static String btnClose;
    // common title message
    public static String titleConfirm;
    public static String titleError;
    public static String titleWarning;
    public static String titleSuccess;

    // user management dialog related message
    public static String tblColumnUserId;
    public static String tblColumnDbAuth;
    public static String tblColumnBrokerAuth;
    public static String tblColumnMonitorAuth;
    public static String titleUserManagementDialog;
    public static String msgUserManagementDialog;
    public static String msgUserManagementList;
    public static String msgDeleteUserConfirm;
    public static String lblUserId;
    public static String titleAddUser;
    public static String titleEditUser;
    public static String msgAddUser;
    public static String msgEidtUser;
    public static String lblDbAuth;
    public static String lblBrokerAuth;
    public static String lblMonitorAuth;
    public static String errPassword;
    public static String errUserExist;
    public static String errUserIdLength;
    public static String errUserId;
    public static String tblColumnDbName;
    public static String tblColumnConnected;
    public static String tblColumnDbUser;
    public static String tblColumnBrokerIP;
    public static String tblColumnBrokerPort;
    public static String msgDbAuthList;
    public static String titleDbAuth;
    public static String msgDbAuth;
    public static String errDbAuth;
    // properties dialog related message
    public static String grpConnectInformation;
    public static String grpServerType;
    public static String grpService;
    public static String grpAutoDatabase;
    public static String tabItemGeneral;
    public static String tabItemAdvanceOptions;
    public static String grpGeneralPara;
    public static String tblColumnParameterName;
    public static String tblColumnValueType;
    public static String tblColumnParameterValue;
    public static String tblColumnParameterType;
    public static String grpGeneral;
    public static String grpDiagnositics;
    public static String msgChangeCMParaSuccess;
    public static String msgChangeServiceParaSuccess;
    public static String msgChangeServerParaSuccess;
    public static String lblServerType;
    public static String setCMParameterTaskName;
    public static String setCubridParameterTaskName;
    public static String grpDataBuffer;
    public static String grpSortBuffer;
    public static String grpLogBuffer;
    public static String grpOthersProperties;
    // properties dialog server parameter error
    public static String errDataBufferPages;
    public static String errDataBufferSize;
    public static String errSortBufferPages;
    public static String errSortBufferSize;
    public static String errLogBufferPages;
    public static String errLogBufferSize;
    public static String errLockEscalation;
    public static String errLockTimeout;
    public static String errDeadLock;
    public static String errCheckpoint;
    public static String errCubridPortId;
    public static String errMaxClients;
    public static String errYesNoParameter;
    public static String errBackupVolumeMaxSize;
    public static String errCsqlHistoryNum;
    public static String errGroupCommitInterval;
    public static String errIndexScanInOidBuffPage;
    public static String errIndexScanInOidBuffPageFloat;
    public static String errInsertExeMode;
    public static String errLockTimeOutMessageType;
    public static String errQueryCachMode;
    public static String errTempFileMemorySize;
    public static String errThreadStackSize;
    public static String errOnlyInteger;
    public static String errUnfillFactor;
    public static String errOnlyFloat;
    public static String errParameterValue;
    public static String errHaPortId;
    public static String errFormatDataBufferSize;
    public static String errFormatSortBufferSize;
    public static String errFormatLogBufferSize;

    // property dialog cm parameter error
    public static String errCmPort;
    public static String errMonitorInterval;
    public static String errServerLongQueryTime;
    // common messages
    public static String msgConfirmExistTitle;
    public static String msgConfirmExistFile;
    public static String msgExistConfirm;
    public static String msgExistConfirmWithJob;

    // BrokerParameterProperty
    public static String deleteBtnName;
    public static String editBtnName;
    public static String addBtnName;
    public static String refreshUnit;
    public static String refreshEnvOnLbl;
    public static String refreshEnvTitle;
    public static String portOfBrokerLst;
    public static String nameOfBrokerLst;
    public static String brokerLstGroupName;
    public static String generalInfoGroupName;
    public static String refreshEnvOfTap;
    public static String brokerLstOfTap;
    public static String editActionTxt;
    public static String addActionTxt;
    public static String delActionTxt;
    public static String restartBrokerMsg;
    public static String errMasterShmId;
    public static String errMasterShmIdSamePort;
    public static String setBrokerConfParametersTaskName;

    // HA property
    public static String grpHACommon;
    public static String grpHAHostUser;
    public static String lblHostName;
    public static String lblUserName;
    public static String lblHACopySyncMode;
    public static String colHostName;
    public static String colUserName;
    public static String colHACopySyncMode;
    public static String tipUserName;
    public static String tipSyncMode;
    public static String errHAPortId;
    public static String errHAMemSize;
    public static String errHostName;
    public static String errUserName;
    public static String errSyncMode;
    public static String msgSetHAConfSuccess;
    public static String setHAConfTaskName;

    // start service
    public static String msgStartServiceWithJob;
    public static String msgStartServiceInHost;
    // stop service
    public static String msgStopServiceWithJob;
    public static String msgStopServiceInHost;

    // HA related
    public static String errNoSupportInHA;

    public static String loadConfParaTaskName;
    public static String loadUserInfoTaskName;
    public static String updateUserTaskName;
    public static String delUserTaskName;

    public static String msgConfirmStopService;

    // status line
    public static String msgDatabaseNum;
    public static String msgUserNum;
    public static String msgBrokerNum;
    public static String msgBrokerSqlLogNum;
    public static String msgBrokerAccessLogNum;
    public static String msgBrokerErrorLogNum;
    public static String msgDbServerLogNum;

    // New Query Editor Dialog
    public static String titleNewQueryDialog;
    public static String msgNewQueryDialog;
    public static String msgStartDb;

    // ToolTip
    public static String tipIP;
    public static String tipPort;
    public static String tipUser;
    public static String tipJDBC;
    public static String tipDbStatusRunning;
    public static String tipDbStatusStopped;
    public static String tipBrokerPort;
    public static String tipBrokerAccessMode;
    public static String tipBrokerStatusON;
    public static String tipBrokerStatusOFF;
    public static String tipBrokerAccessModeNotWorking;

    public static String lblFromFile;
    public static String lblBrowser;
    public static String msgImportServer;
    public static String msgExportServer;
    public static String msgErrorMissingImportFile;
    public static String msgErrorMissingImportServer;
    public static String msgErrorMissingExportFile;
    public static String msgConfirmOverwriteFile;
    public static String msgErrorMissingExportServer;
    public static String msgInfoSame;
    public static String tlImportServer;
    public static String tlExportServer;
    public static String btnSelectAll;
    public static String btnUnSelectAll;
    public static String exportServerSelectPathMsg;
    public static String exportServerSuccessMsg;
    public static String importServerSuccessMsg;
    public static String taskNameImportServer;
    public static String taskNameExportServer;

    public static String columnHeaderSelected;
    public static String columnHeaderServerInfo;
    public static String columnHeaderServerAddress;
    public static String columnHeaderServerPort;
    public static String columnHeaderServerJdbcVersion;
    public static String columnHeaderServerUserName;
    public static String columnHeaderServerUserPassword;
    public static String columnHeaderServerAutoSave;

    public static String passwordAutoSaveYes;
    public static String passwordAutoSaveNo;

    public static String titleSetEditorConfig;
    public static String labBackground;
    public static String msgSetEditorConfig;

    /*Host status*/
    public static String lblServerMaster;
    public static String lblServerSlave;
    public static String lblServerReplica;
    public static String lblServerUnknow;
}
