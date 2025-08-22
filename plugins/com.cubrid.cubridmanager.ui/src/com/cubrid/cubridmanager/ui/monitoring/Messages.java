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
package com.cubrid.cubridmanager.ui.monitoring;

import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import org.eclipse.osgi.util.NLS;

/**
 * This is message bundle classes and provide convenience methods for manipulating messages.
 *
 * @author lizhiqiang
 * @version 1.0 - 2009-3-2 created by lizhiqiang
 */
public class Messages extends NLS {

    static {
        NLS.initializeMessages(
                CubridManagerUIPlugin.PLUGIN_ID + ".monitoring.Messages", Messages.class);
    }
    // Common
    public static String btnOK;
    public static String btnCancel;

    // DiagStatusMonitorTemplate
    public static String addTitle;
    public static String editTitle;
    public static String addMessage;
    public static String editMessage;
    public static String statusMonitorList;
    public static String statusMonitorListDb;
    public static String statusMonitorListBroker;
    public static String diagCategory;
    public static String diagName;
    public static String emptyNameTxt;
    public static String errsamplingTermsTxt;
    public static String overLmtSamplingTermsTx;
    public static String emptyDescTxt;
    public static String emptyDbTxt;
    public static String hasSameName;
    public static String noSuchDb;
    public static String noTargetObj;
    public static String noPermitMonitorDb;

    public static String targetObjGroup;
    public static String addBtnTxt;
    public static String removeBtnTxt;
    public static String templateGroup;
    public static String templateName;
    public static String sammpleTerm;
    public static String templateDesc;
    public static String targetDb;

    // DeleteStatusMonitorTemplate
    public static String delStatusMonitorConfirmContent;

    // ChartCompositePart
    public static String chartSettingTxt;
    public static String seriesSelectCheckBtn;
    public static String seriesSelectColorBtnName;
    public static String seriesSelectColorBtnLbl;
    public static String msgChangeDb;
    public static String tblSeriesTtl;
    public static String errHistoryRecordFile;

    // ChartSettingDlg
    public static String chartSettingDlgTtl;
    public static String chartSettingDlgMsg;
    public static String tabItemChartTtl;
    public static String chartTtlGroupTxt;
    public static String chartTtlBgLbl;
    public static String tabItemPlotTtl;
    public static String plotAppearanceGroupTxt;
    public static String plotColorLbl;
    public static String tabItemSeriesTtl;
    public static String seriesGroupTxt;
    public static String seriesCheckBtnLbl;
    public static String seriesColorLbl;
    public static String plotBackgroudTxt;
    public static String plotDomainGridTxt;
    public static String plotRangeGridTxt;
    public static String plotDateAxisTxt;
    public static String plotNumberAxisTxt;
    public static String shlChartSetTxt;
    public static String chartTtlContentGrp;
    public static String chartTtlName;
    public static String seriesWidthLbl;
    public static String tabItemHitoryTtl;
    public static String msgHistoryPathGrp;
    public static String msgHistoryPathLbl;
    public static String btnBrowse;
    public static String msgSelectDir;
    public static String tabItemChartSelectTtl;

    // DbComboContribution
    public static String dbSelectTip;

    // FontGroup
    public static String fontGroupTxt;
    public static String fontNameLbl;
    public static String fontStyleLbl;
    public static String fontSizeLbl;
    public static String fontColorLbl;
    public static String fontChangeBtn;

    // DbStatusDumpMonitorViewPart
    public static String dbMonitorChartTtl;
    public static String dbMonitorRecordingTooltip;
    public static String dbMonitorStartRecordTooltip;
    // BrokerStatusMonitorViewPart
    public static String brokerMonitorChartTtl;
    public static String brokerMonitorRecordingTooltip;
    public static String brokerMonitorStartRecordTooltip;

    // AddMonitorInstanceDlg
    public static String addMonInsDlgTtl;
    public static String addMonInsDlgMsg;
    public static String addMonInsDlgTypeLbl;
    public static String addMonInsDlgNodeName;
    public static String addMonInsDlgShellTxt;
    public static String errorNodeNameMsg;
    public static String btnSaveMonitorSetting;

    // ShowBrokerMonitorHistoryAction
    public static String msgBrokerHistoryStatusName;
    // ShowDatabaseMonitorHistoryAction
    public static String msgDbHistoryStatusName;

    // HistoryComposite
    public static String historySelectDate;
    public static String historySelectStartTime;
    public static String historySelectEndTime;
    public static String btnHistoryQuery;

    // BrokerStatusHistoryViewPart
    public static String brokerHistoryChartTtl;
    public static String errBrokerHistorySettingTime;
    // DbStatusHistoryView
    public static String databaseHistoryChartTtl;
    public static String errDbHistorySettingTime;

    // DbSystemMonitorViewPart
    public static String msgAllDbNameInCombo;
    public static String dbSysMonRecordingTooltip;
    public static String dbSysMonStartRecordTooltip;

    // HostSystemMonitorViewPart
    public static String hostSysMonRecordingTooltip;
    public static String hostSysMonStartRecordTooltip;

    // DbSystemMonitorComp
    public static String dbSysCpuChartBarGroupName;
    public static String dbSysCpuChartSeriesGroupName;
    public static String dbSysPhysicalChartBarGroupName;
    public static String dbSysPhysicalChartSeriesGroupName;
    public static String dbSysCpuInfoGroupName;
    public static String dbSysCpuUserInfoLbl;
    public static String dbSysCpuKernelInfoLbl;
    public static String dbSysMemInfoGroupName;
    public static String dbSysMemPhysicalLbl;
    public static String dbSysMemVirtualLbl;

    public static String dbSelectedSysInfo;
    public static String dbSelectedChartCpu;
    public static String dbSelectedChartMemory;
    public static String dbSysCpuTotalInfoLbl;

    // HostSystemMonitorComp
    public static String hostSysCpuChartBarGrpName;
    public static String hostSysCpuChartSeriesGrpName;
    public static String hostSysIowaitChartBartGrpName;
    public static String hostSysIowaitChartSeriesGrpName;
    public static String hostSysPhysicalChartBarGrpName;
    public static String hostSysPhysicalChartSeriesGrpName;
    public static String hostSysCppuInfoGrpName;
    public static String hostSysCpuUserInfoLbl;
    public static String hostSysCpuKernelInfoLbl;
    public static String hostSysMemPhyInfoGrpName;
    public static String hostSysMemPhyInfoTtlLbl;
    public static String hostSysMemPhyInfoUsedLbl;
    public static String hostSysIowaitInfoGrpName;
    public static String hostSysIowaitUsedLbl;
    public static String hostSysMemSwapInfoGrpName;
    public static String hostSysMemSwapInfoTtlLbl;
    public static String hostSysMemSwapInfoUsedLbl;
    public static String hostSelectedSysInfo;
    public static String hostSelectedChartCpu;
    public static String hostSelectedChartMemory;
    public static String hostSelectedChartIowait;
    public static String addTemplateTaskName;
    public static String delTemplateTaskName;
    public static String editTemplateTaskName;
    public static String hostSysCpuTotalInfoLbl;

    // DbSystemMonitorHistoryViewPart
    public static String dbSysHistoryCpuChartGrpName;
    public static String dbSysHistoryPhysicalChartGrpName;
    public static String errDbSysMonHistorySettingTime;

    // HostSystemMonitorHistoryViewPart
    public static String hostHistoryCpuChartGrpName;
    public static String hostHistoryIowaitChartGrpName;
    public static String hostHistoryPhysicalChartGrpName;
    public static String errHostSysMonHistorySettingTime;
}
