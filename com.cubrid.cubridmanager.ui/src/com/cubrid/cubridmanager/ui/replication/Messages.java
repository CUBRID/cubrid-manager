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
package com.cubrid.cubridmanager.ui.replication;

import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import org.eclipse.osgi.util.NLS;

/**
 * This is message bundle classes and provide convenience methods for manipulating messages.
 *
 * <p>Messages Description
 *
 * @author pangqiren
 * @version 1.0 - 2009-8-26 created by pangqiren
 */
public class Messages extends NLS {

    static {
        NLS.initializeMessages(
                CubridManagerUIPlugin.PLUGIN_ID + ".replication.Messages", Messages.class);
    }

    // replication editor related message
    public static String msgViewRepl;
    public static String msgCreateRepl;

    // The message related with host information dialog
    public static String titleSetHostInfoDialog;
    public static String msgSetHostInfoDialog;
    public static String grpHostInfo;
    public static String lblIpAddress;
    public static String lblPort;
    public static String lblUserName;
    public static String lblPassword;
    public static String errIpAddress;
    public static String errPort;
    public static String errHostExist;
    public static String errUserName;
    public static String errInvalidPlatform;
    public static String errInvalidServerType;
    public static String errInvalidUser;
    public static String errInvalidDbUser;
    public static String errValidVersion;

    // set master database information dialog
    public static String titleSetMdbInfoDialog;
    public static String titleSelectMasterDb;
    public static String msgSelectMasterDb;
    public static String errInvalidHostInfo;
    public static String grpSelectMdb;
    public static String lblDbName;
    public static String lblDbaPassword;
    public static String lblConfirmDbaPassword;
    public static String lblReplServerPort;
    public static String errNoDatabase;
    public static String errDatabaseName;
    public static String errDbaPassword;
    public static String errConfirmDbaPassword;
    public static String errPasswordNotEqual;
    public static String errReplServerPort;
    public static String titleSelectReplTables;
    public static String msgSelectReplTables;
    public static String grpSelectReplTables;
    public static String btnSelectAllTables;
    public static String tblColTableName;
    public static String errNoMdb;
    public static String errNoDb;
    public static String errPreviousPage;

    // set slave database dialog
    public static String titleSetSlaveDbDialog;
    public static String msgSetSlaveDbDialog;
    public static String msg1SetSlaveDbDialog;
    public static String grpSdbInfo;
    public static String lblDbPath;
    public static String lblDbUser;
    public static String lblDbPassword;
    public static String lblConfirmDbPassword;
    public static String grpReplParaSetting;
    public static String errDbPath;
    public static String errDbUser;
    public static String errDbExist;
    public static String errDbPassword;
    public static String errConfirmedDbPassword;

    // set distributor db information dialog
    public static String titleSetDistdbDialog;
    public static String msg1SetDistdbDialog;
    public static String msgSetDistdbDialog;
    public static String grpDistdbInfo;
    public static String grpReplAgentInfo;
    public static String lblReplAgentPort;
    public static String lblCopyLogPath;
    public static String lblTrailLogPath;
    public static String lblErrorLogPath;
    public static String lblDelayTimeLogSize;
    public static String btnRestartReplication;
    public static String errReplAgentPort;
    public static String errCopyLogPath;
    public static String errTrailLogPath;
    public static String errErrorLogPath;
    public static String errDelayTimeLogSize;

    // replication editor
    public static String lblComponentGrp;
    public static String lblSelectTool;
    public static String descSelectTool;
    public static String lblConnectionTool;
    public static String descConnectionTool;
    public static String lblReplComponent;
    public static String lblHostTool;
    public static String descHostTool;
    public static String lblMasterTool;
    public static String descMasterTool;
    public static String lblDistributorTool;
    public static String descDistributorTool;
    public static String lblSlaveTool;
    public static String descSlaveTool;
    public static String errInvalidReplDesign;
    public static String errInvalidHost;
    public static String errInvalidReplComponent;
    public static String errInvalidMasterConn1;
    public static String errInvalidMasterConn2;
    public static String errInvalidDistConn1;
    public static String errInvalidDistConn2;
    public static String errInvalidSlaveConn;
    public static String errInvalidConn1;
    public static String errInvalidConn2;

    // replication server dialog
    public static String titleStartReplServer;
    public static String titleStopReplServer;
    public static String msgReplServerDialog;
    public static String msgReplServerStarted;
    public static String msgReplServerStoped;
    public static String msgReplServerStartedSuccess;
    public static String msgReplServerStopedSuccess;
    public static String grpReplServer;
    public static String lblMdbName;

    // start&stop slave and agent database
    public static String msgSuccess;
    public static String msgStartSlaveDb;
    public static String msgStopSlaveDb;
    public static String msgStartAgent;
    public static String msgStopAgent;
    public static String msgConfirmStopDatabase;
    public static String msgConfirmStopAgent;
    public static String msgConfirmStartMasterDbAndSlaveDb;

    // replication parameters
    public static String repparm0titleReplicationParamDbDialog;
    public static String repparm0msgReplicationParamDbDialog;
    public static String repparm0grpReplicationInfo;
    public static String repparm0lblSlaveDbName;
    public static String repparm0btnConnect;
    public static String repparm0tblColumnParameterName;
    public static String repparm0tblColumnValueType;
    public static String repparm0tblColumnParameterValue;
    public static String repparm0errPerfPollInterval;
    public static String repparm0errSizeOfLogBuffer;
    public static String repparm0errSizeOfCacheBuffer;
    public static String repparm0errSizeOfCopylog;
    public static String repparm0errLogApplyInterval;
    public static String repparm0errRestartInterval;
    public static String repparm0errYesNoParameter;
    public static String repparm0errOnlyInteger;
    public static String repparm0errParameterValue;
    public static String repparm0titleSuccess;
    public static String repparm0msgChangeParamSuccess;

    // change slave database
    public static String chsldbTitleChangeSlaveDbDialog;
    public static String chsldbMsgChangeParamSuccess;
    public static String chsldb0msgChangeSlaveDbPage;
    public static String chsldb0titleChangeSlaveDbPage;
    public static String chsldb0grpSlaveDbInfo;
    public static String chsldb0grpMasterDbInfo;
    public static String chsldb0grpDistDbInfo;
    public static String chsldb0lblSlaveDbName;
    public static String chsldb0lblSlaveDbUser;
    public static String chsldb0lblSlaveDbDbaPasswd;
    public static String chsldb0lblConfirmSlaveDbDbaPasswd;
    public static String chsldb0lblSlaveDbPath;
    public static String chsldb0lblMasterHost;
    public static String chsldb0lblMasterDbPort;
    public static String chsldb0lblMasterHostUser;
    public static String chsldb0lblMasterHostPasswd;
    public static String chsldb0lblMasterDbName;
    public static String chsldb0lblMasterDbUser;
    public static String chsldb0lblMasterDbaPasswd;
    public static String chsldb0errInvalidSlaveDbName;
    public static String chsldb0errInvalidSlaveDbPath;
    public static String chsldb0errInvalidSlaveLogPath;
    public static String chsldb0errInvalidMasterDbUser;
    public static String chsldb0errInvalidMasterDbPort;
    public static String chsldb0errInvalidMasterDbaPasswd;
    public static String chsldb0errInvalidMasterHostPasswd;
    public static String chsldb1msgSetReplicationParamPage;
    public static String chsldb1titleSetReplicationParamPage;
    public static String chsldb1grpSetReplicationParam;
    public static String chsldb2msgSelectTablesPage;
    public static String chsldb2titleSelectTablesPage;
    public static String chsldb2grpSelectTablesPage;

    // change master database
    public static String chmsdb0titleChangeMasterDbDialog;
    public static String chmsdb0msgChangeMasterDbDialog;
    public static String chmsdb0grpOldMasterDbInfo;
    public static String chmsdb0grpNewMasterDbInfo;
    public static String chmsdb0grpDistDbInfo;
    public static String chmsdb0lblOldMasterDbName;
    public static String chmsdb0lblOldMasterDbHostIp;
    public static String chmsdb0lblNewMasterDbName;
    public static String chmsdb0lblNewMasterDbHostIp;
    public static String chmsdb0lblDistHostIp;
    public static String chmsdb0lblDistDbName;
    public static String chmsdb0lblDistDbDbaPasswd;
    public static String chmsdb0errInvalidOldMasterDbName;
    public static String chmsdb0errInvalidOldMasterDbHostIp;
    public static String chmsdb0errInvalidNewMasterDbName;
    public static String chmsdb0errInvalidNewMasterDbHostIp;
    public static String chmsdb0errInvalidDistHostIp;
    public static String chmsdb0errInvalidDistDbName;
    public static String chmsdb0errInvalidDistDbDbaPasswd;

    // change replicated tables
    public static String titleChangeReplTables;
    public static String titleSetDatabaseInfo;
    public static String grpMdbInfo;
    public static String msgSetDatabaseInfo;
    public static String errInvalidPort;
    public static String errInvalidPassword;
    public static String errInvalidMdbPassword;
    public static String errInvalidDistdbPassword;

    // job name
    public static String changeReplicationSchemaJobName;
    public static String createReplicationJobName;
    public static String createMasterJobName;
    public static String createDistJobName;
    public static String createSlaveJobName;
    public static String startAgentJobName;
    public static String transFileJobName;
    public static String startReplServerJobName;

    public static String msgConfirmStartReplServer;
    public static String msgConfirmStopReplServer;
    public static String msgConfirmStartAgent;
    public static String msgConfirmDeleteDb;
    public static String msgConfirmCreateRepl;
    public static String msgCreateReplicationSuccess;

    // performance monitor view
    public static String msgDelayValue;
    public static String msgSlaveTimes;
    public static String titlePerformancePart;
    // replication editor action name
    public static String editActionName;
    public static String editActionToolTip;
}
