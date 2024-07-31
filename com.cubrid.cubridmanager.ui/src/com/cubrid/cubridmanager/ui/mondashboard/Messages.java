/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *  - Neither the name of the <ORGANIZATION> nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

package com.cubrid.cubridmanager.ui.mondashboard;

import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import org.eclipse.osgi.util.NLS;

/**
 * This is message bundle classes and provide convenience methods for manipulating messages.
 *
 * @author pangqiren
 * @version 1.0 - 2010-6-1 created by pangqiren
 */
public class Messages extends NLS {

    static {
        NLS.initializeMessages(
                CubridManagerUIPlugin.PLUGIN_ID + ".mondashboard.Messages", Messages.class);
    }

    public static String msgConfirmDeleteDashboard;
    public static String msgConfirmDeleteHost;
    public static String msgConfirmDeleteDatabase;
    public static String msgConfirmDeleteBroker;

    // AddDashboardDialog
    public static String titleAddDashboardDialog;
    public static String msgAddDashboardDialog;
    public static String titleEditDashboardDialog;
    public static String msgEditDashboardDialog;
    public static String grpGeneralInfo;
    public static String lblDashboardName;
    public static String lblDashboardInfo;
    public static String colIP;
    public static String colPort;
    public static String colServerType;
    public static String colServerStatus;
    public static String colName;
    public static String colStatus;
    public static String colType;
    public static String btnAdd;
    public static String btnDelete;
    public static String errDashboardName;
    public static String errDashboardNameExist;
    public static String errHostAndDbList;

    // SetHostInfoPage
    public static String titleAddDashboardWizard;
    public static String titileHostInfoPage;
    public static String msgHostInfoPage;
    public static String grpHostInfo;
    public static String lblNickName;
    public static String colNickName;
    public static String lblIPAddress;
    public static String lblPort;
    public static String lblUserName;
    public static String lblPassword;
    public static String btnConnect;
    public static String errIPAddress;
    public static String errPort;
    public static String errPassword;
    public static String errNotSupportServer;

    // SelectDatabasePage
    public static String titleSelectDbPage;
    public static String msgSelectDbPage;
    public static String grpSelectDb;
    public static String lblDbName;
    public static String lblDbaPassword;
    public static String btnHAMode;
    public static String btnAddDb;
    public static String colDbName;
    public static String colDbStatus;
    public static String lblDbListInfo;
    public static String errDbList;
    public static String errDbExist;
    public static String errDbaPassowrd;
    public static String btnAddHADb;
    public static String tipBtnAddHaDb;
    public static String confirmMsgAddActive;
    public static String confirmMsgAddStandby;

    // SelectBrokerPage
    public static String titleSelectBrokerPage;
    public static String msgSelectBrokerPage;
    public static String grpSelectBroker;
    public static String lblBrokerName;
    public static String lblBrokerPort;
    public static String lblBrokerStatus;
    public static String lblAccessMode;
    public static String lblBrokerListInfo;
    public static String colBrokerName;
    public static String colBrokerPort;
    public static String colBrokerStatus;
    public static String colAccessMode;
    public static String btnAddBroker;
    public static String errBrokerExist;

    // AddHADatabaseDialog
    public static String titleAddHADbDialog;
    public static String msgAddStadnbyDbDialog;
    public static String msgAddActiveDbDialog;
    public static String errDbNoExist;

    // DatabaseLogListDialog
    public static String titleDbLogListDialog;
    public static String msgDbLogListDialog;
    public static String lblDbLogListInfo;
    public static String colDbLog;
    public static String jobGetLogList;

    public static String msgVerifyPwdDialog;

    public static String jobConnectHost;
    public static String jobConnectDatabase;

    // HostDashboardViewPart
    public static String hostCpuChartBarGroupName;
    public static String hostCpuChartSeriesGroupName;
    public static String hostIowaitChartBartGroupName;
    public static String hostIowaitChartSeriesGroupName;
    public static String hostPhysicalChartBarGroupName;
    public static String hostPhysicalChartSeriesGroupName;
    public static String hostBrokerSeriesGroupName;
    public static String hostCpuInfoGroupName;
    public static String hostUserCpuInfoLbl;
    public static String hostKernelCpuInfoLbl;
    public static String hostPhyInfoGroupName;
    public static String hostPhyTotalInfoLbl;
    public static String hostPhyUsedInfoLbl;
    public static String hostIowaitInfoGroupName;
    public static String hostIowaitInfoLbl;
    public static String hostSwapmemInfoGroupName;
    public static String hostSwapmemTotalLbl;
    public static String hostSwapmemUsedLbl;

    public static String chartSettingTxt;
    public static String hostSelectedSysInfo;
    public static String hostSelectedChartCpu;
    public static String hostSelectedChartMemory;
    public static String hostSelectedChartIowait;
    public static String hostSelectedChartBroker;
    public static String hostDashboardRecordingTooltip;
    public static String hostDashboardMonitorStartRecordTooltip;
    public static String hostTotalCpuInfoLbl;

    // DatabaseDashboardViewpart
    public static String dbCpuChartBarGroupName;
    public static String dbCpuChartSeriesGroupName;
    public static String dbPhysicalChartBarGroupName;
    public static String dbPhysicalChartSeriesGroupName;
    public static String dbCountChartBarGroupName;
    public static String dbCountChartSeriesGroupName;
    public static String dbDelayChartBarGroupName;
    public static String dbDelayChartSeriesGroupName;
    public static String dbDatabaseStatusSeriesGroupName;
    public static String dbCpuInfoGroupName;
    public static String dbUserCpuInfoLbl;
    public static String dbKernelCpuInfoLbl;
    public static String dbMemInfoGroupName;
    public static String dbMemPhyInfoLbl;
    public static String dbMemVirtualInfoLbl;
    public static String dbDelayInfoGroupName;
    public static String dbDelayInfoLbl;
    public static String dbCountInfoGroupName;
    public static String dbFailedInfoLbl;
    public static String dbCommintInfoLbl;
    public static String dbInsertInfoLbl;
    public static String dbUpdateInfoLbl;
    public static String dbDeleteInfoLbl;
    public static String dbProcessStateInfoGroupName;
    public static String dbServerProcessLbl;
    public static String dbApplyLogDbInfoLbl;
    public static String dbCopyLogDbInfoLbl;
    public static String dbSelectedSysInfo;
    public static String dbSelectedChartCpu;
    public static String dbSelectedChartMemory;
    public static String dbSelectedChartDelay;
    public static String dbSelectedChartCount;
    public static String dbSelectedChartBroker;
    public static String dbDashboardRecordingTooltip;
    public static String dbDashboardMonitorStartRecordTooltip;
    public static String dbTotalSysCpuInfoLbl;

    // DbDashboardHistoryViewPart
    public static String errDbDashboardHistorySettingTime;
    public static String dbHistoryCpuChartGrpName;
    public static String dbHistoryPhysicalChartGrpName;

    // HostDshboardHistoryViewPart
    public static String errHostDashboardHistorySettingTime;
    public static String hostHistoryCpuChartGrpName;
    public static String hostHistoryIowaitChartGrpName;
    public static String hostHistoryPhysicalChartGrpName;
    // HistroyFileHelp
    public static String wainNoHistoryFile;
    public static String changeRoleTaskName;

    // DashboardPreferencePage
    public static String dashboardPreferencePageName;
    public static String dbStatusType;
    public static String colorSettingsOfDB;

    public static String titleEditNickNameDialog;
    public static String msgEditNickNameDialog;
    public static String errEditNickName;

    public static String brokerUndefinedDBList;
    public static String brokerClientList;

    // DashboardPreferendePage-HA
    public static String haMon;
    public static String haMonHertbeatTimeout;
    public static String haMonHertbeatTimeoutMsg;
}
