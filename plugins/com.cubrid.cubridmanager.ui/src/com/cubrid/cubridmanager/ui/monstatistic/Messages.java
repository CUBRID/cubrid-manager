/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.cubridmanager.ui.monstatistic;

import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import org.eclipse.osgi.util.NLS;

/**
 * This is message bundle classes and provide convenience methods for manipulating messages.
 *
 * @author Santiago Wang
 * @version 1.0 - 2013-08-01 created by Santiago Wang
 */
public class Messages extends NLS {

    static {
        NLS.initializeMessages(
                CubridManagerUIPlugin.PLUGIN_ID + ".monstatistic.Messages", Messages.class);
    }
    // Button
    public static String btnOK;
    public static String btnCancel;
    public static String btnCleanSelected;
    public static String btnAddHost;
    public static String btnEditHost;
    public static String btnDelHost;
    public static String btnEditInterval;
    public static String btnAdd;
    public static String btnEdit;
    public static String btnDelete;
    public static String btnSelect;
    public static String btnSelectAll;
    public static String btnDeselectAll;
    public static String btnRefresh;
    public static String btnShowDetail;
    public static String btnBackToViewMode;
    public static String btnGoToEditMode;

    // Time Type
    public static String msgTimeDaily;
    public static String msgTimeWeekly;
    public static String msgTimeMonthly;
    public static String msgTimeYearly;

    // Label
    public static String lblDataType;
    public static String lblTimeType;
    public static String lblDbName;
    public static String lblVolName;
    public static String lblBrokerName;
    public static String lblMetric;
    public static String lblHostName;
    public static String lblIp;
    public static String lblPort;
    public static String lblHost;
    public static String lblStatus;
    public static String lblPageName;
    public static String lblInterval;
    public static String lblChartTime;
    public static String lblChartValue;

    public static String msgGpCpuUsage;
    public static String msgGpMemory;
    public static String msgGpApplication;
    public static String msgGpIo;
    public static String msgGpPages;
    public static String msgGpHa;
    public static String msgGpVolSpace;
    public static String msgGpBrokerInfo;
    public static String msgRefreshTime;

    // Monitor Statistic
    public static String confirmStatisticPageDeleteWarn;

    // AddMultiHostStatisticItemDialog
    public static String msgHostUnvailable;
    public static String msgHostUnsupported;
    public static String msgHostOk;
    public static String addMultiHostStatisticItemTitle;
    public static String addStatisticItemMsg;
    public static String editMultiHostStatisticItemTitle;
    public static String editStatisticItemMsg;
    public static String confirmRemoveSelectedHost;
    public static String errNoHostMsg;

    // AddSingleHostStatisticItemDialog
    public static String addSingleHostStatisticItemTitle;
    public static String editSingleHostStatisticItemTitle;
    public static String errNoMetricMsg;
    public static String errNoAvailableDbMsg;
    public static String errNoAvailableVolMsg;
    public static String errNoAvailableBrokerMsg;

    // AddStatisticHostDialog
    public static String addStatisticHostTitle;
    public static String addNewHostItemMsg;
    public static String editStatisticHostTitle;
    public static String editHostItemMsg;
    public static String errNoAvailableHost;
    public static String errDuplicateHost;
    public static String errNeedEnableMonStatistic;

    // AddStatisticPageDialog
    public static String addMonitorStatisticPageTitle;
    public static String addMonitorStatisticMsg;
    public static String errInvalidPageNameMsg;
    public static String errDuplicatePageNameMsg;
    public static String needEnableMonitorStatisticMsg;

    // EditMonitorIntervalDialog
    public static String msgChangeMonIntervalSuccess;
    public static String checkMonIntervalTitle;
    public static String checkMonIntervalMsg;
    public static String errInvalidIntervalMsg;
    public static String confirmChangeMonInterval;

    // MonitorStatisticDetailViewPart
    public static String monStatisticDetailViewTitle;
    public static String msgGpChartInfo;
    public static String msgMaxValue;
    public static String msgMinValue;
    public static String monStatisticDetailChartTitle;

    // MonitorStatisticEditor
    public static String msgChartGroupName;
    public static String RefreshTooltip;
    public static String ShowDetailTooltip;
    public static String confirmStatisticChartRemoveWarn;
    public static String confirmMultiStatisticChartRemoveWarn;
}
