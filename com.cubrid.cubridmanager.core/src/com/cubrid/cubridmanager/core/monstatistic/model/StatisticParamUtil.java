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
package com.cubrid.cubridmanager.core.monstatistic.model;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.Messages;
import java.util.EnumSet;
import java.util.Set;

/**
 * Utility class for API "get_mon_statistic".
 *
 * @author Santiago Wang
 * @version 1.0 - 2013-08-20 created by Santiago Wang
 */
public class StatisticParamUtil {

    private static final Set<MetricType> METRIC_DB_CPU_SET =
            EnumSet.of(MetricType.DB_CPU_KERNEL, MetricType.DB_CPU_USER);
    private static final Set<MetricType> METRIC_DB_MEM_SET =
            EnumSet.of(MetricType.DB_MEM_PHY, MetricType.DB_MEM_VIR);
    private static final Set<MetricType> METRIC_DB_APP_SET =
            EnumSet.of(MetricType.DB_QPS, MetricType.DB_TPS);
    private static final Set<MetricType> METRIC_DB_IO_SET =
            EnumSet.of(MetricType.DB_IO_READ, MetricType.DB_IO_WRITE);
    private static final Set<MetricType> METRIC_DB_PAGE_SET =
            EnumSet.of(
                    MetricType.DB_HIT_RATIO, MetricType.DB_FETCH_PAGES, MetricType.DB_DIRTY_PAGES);
    private static final Set<MetricType> METRIC_DB_HA_SET =
            EnumSet.of(
                    MetricType.DB_HA_COPY_DELAY_PAGE, MetricType.DB_HA_COPY_DELAY_ESTIMATED,
                    MetricType.DB_HA_APPLY_DELAY_PAGE, MetricType.DB_HA_APPLY_DELAY_ESTIMATED);
    private static final Set<MetricType> METRIC_DB_SPACE_SET = EnumSet.of(MetricType.DB_FREESPACE);
    private static final Set<MetricType> METRIC_OS_CPU_SET =
            EnumSet.of(
                    MetricType.OS_CPU_IDLE,
                    MetricType.OS_CPU_IOWAIT,
                    MetricType.OS_CPU_KERNEL,
                    MetricType.OS_CPU_USER);
    private static final Set<MetricType> METRIC_OS_MEM_SET =
            EnumSet.of(MetricType.OS_MEM_PHY_FREE, MetricType.OS_MEM_SWAP_FREE);
    private static final Set<MetricType> METRIC_OS_SPACE_SET = EnumSet.of(MetricType.OS_DISK_FREE);
    private static final Set<MetricType> METRIC_DB_SET = EnumSet.noneOf(MetricType.class);

    static {
        METRIC_DB_SET.addAll(METRIC_DB_CPU_SET);
        METRIC_DB_SET.addAll(METRIC_DB_MEM_SET);
        METRIC_DB_SET.addAll(METRIC_DB_APP_SET);
        METRIC_DB_SET.addAll(METRIC_DB_IO_SET);
        METRIC_DB_SET.addAll(METRIC_DB_PAGE_SET);
        METRIC_DB_SET.addAll(METRIC_DB_HA_SET);
        METRIC_DB_SET.addAll(METRIC_DB_SPACE_SET);
    }

    private static final Set<MetricType> METRIC_DB_VOL_SET = EnumSet.of(MetricType.VOL_FREESPACE);
    private static final Set<MetricType> METRIC_BROKER_SET =
            EnumSet.of(
                    MetricType.BROKER_TPS,
                    MetricType.BROKER_QPS,
                    MetricType.BROKER_LONG_T,
                    MetricType.BROKER_LONG_Q,
                    MetricType.BROKER_REQ,
                    MetricType.BROKER_ERR_Q,
                    MetricType.BROKER_JQ);
    private static final Set<MetricType> METRIC_OS_SET = EnumSet.noneOf(MetricType.class);

    static {
        METRIC_OS_SET.addAll(METRIC_OS_CPU_SET);
        METRIC_OS_SET.addAll(METRIC_OS_MEM_SET);
        METRIC_OS_SET.addAll(METRIC_OS_SPACE_SET);
    }

    private static final String[] DTYPES = new String[] {"daily", "weekly", "monthly", "yearly"};
    private static final String[] DB_SEND_MSG =
            new String[] {"task", "token", "metric", "dtype", "dbname"};
    private static final String[] DB_VOL_SEND_MSG =
            new String[] {"task", "token", "metric", "dtype", "dbname", "volname"};
    private static final String[] BROKER_SEND_MSG =
            new String[] {"task", "token", "metric", "dtype", "bname"};
    private static final String[] OS_SEND_MSG = new String[] {"task", "token", "metric", "dtype"};
    /*Solution 1: used to check whether one metric is related to percentage data*/
    /*private static final String[] METRIC_PERCENT = new String[]{
    "db_cpu_kernel", "db_cpu_user", "db_hit_ratio", "os_cpu_idle",
    "os_cpu_iowait", "os_cpu_kernel", "os_cpu_user", };*/

    /**
     * This enum provide the data types for monitor statistic.
     *
     * @author Santiago Wang
     * @version 1.0 - 2013-6-25 created by Santiago Wang
     */
    public enum StatisticType {
        DB(METRIC_DB_SET, Messages.msgDataDb),
        DB_VOL(METRIC_DB_VOL_SET, Messages.msgDataDbVol),
        BROKER(METRIC_BROKER_SET, Messages.msgDataBroker),
        OS(METRIC_OS_SET, Messages.msgDataOs);

        private final Set<MetricType> metricTypeSet;
        private final String message;

        StatisticType(Set<MetricType> metricSet, String message) {
            this.metricTypeSet = metricSet;
            this.message = message;
        }

        public String getMessage() {
            return this.message;
        }

        public Set<MetricType> getMetricTypeSet() {
            return EnumSet.copyOf(metricTypeSet);
        }

        public static StatisticType getEnumByMessage(String message) {
            if (StringUtil.isEmpty(message)) {
                return null;
            }
            for (StatisticType type : StatisticType.class.getEnumConstants()) {
                if (type.getMessage().equals(message)) {
                    return type;
                }
            }
            return null;
        }
    }

    public enum TimeType {
        DAILY("daily", Messages.msgTimeDaily),
        WEEKLY("weekly", Messages.msgTimeWeekly),
        MONTHLY("monthly", Messages.msgTimeMonthly),
        YEARLY("yearly", Messages.msgTimeYearly);

        private final String type;
        private final String message;

        TimeType(String type, String message) {
            this.type = type;
            this.message = message;
        }

        public String getType() {
            return type;
        }

        public String getMessage() {
            return message;
        }

        public static TimeType getEnumByMessage(String message) {
            if (StringUtil.isEmpty(message)) {
                return null;
            }
            for (TimeType timeType : TimeType.class.getEnumConstants()) {
                if (timeType.getMessage().equals(message)) {
                    return timeType;
                }
            }
            return null;
        }

        public static TimeType getEnumByType(String type) {
            if (StringUtil.isEmpty(type)) {
                return null;
            }
            for (TimeType timeType : TimeType.class.getEnumConstants()) {
                if (timeType.getType().equals(type)) {
                    return timeType;
                }
            }
            return null;
        }
    }

    /**
     * This enum provide all the available metrics in "get_mon_statistic".
     *
     * @author Santiago Wang
     * @version 1.0 - 2013-6-25 created by Santiago Wang
     */
    public enum MetricType {
        DB_CPU_KERNEL("db_cpu_kernel", Messages.msgDbCpuKernel, Messages.msgDbCpuUsage),
        DB_CPU_USER("db_cpu_user", Messages.msgDbCpuUser, Messages.msgDbCpuUsage),
        DB_MEM_PHY("db_mem_phy", Messages.msgDbMemPhy, Messages.msgDbMemInfo),
        DB_MEM_VIR("db_mem_vir", Messages.msgDbMemVir, Messages.msgDbMemInfo),
        DB_QPS("db_qps", Messages.msgDbQps, Messages.msgDbAppInfo),
        DB_TPS("db_tps", Messages.msgDbTps, Messages.msgDbAppInfo),
        DB_HIT_RATIO("db_hit_ratio", Messages.msgDbHitRatio, Messages.msgDbPageInfo),
        DB_FETCH_PAGES("db_fetch_pages", Messages.msgDbFetchPages, Messages.msgDbPageInfo),
        DB_DIRTY_PAGES("db_dirty_pages", Messages.msgDbDirtyPages, Messages.msgDbPageInfo),
        DB_IO_READ("db_io_read", Messages.msgDbIoRead, Messages.msgDbIoInfo),
        DB_IO_WRITE("db_io_write", Messages.msgDbIoWrite, Messages.msgDbIoInfo),
        DB_HA_COPY_DELAY_PAGE(
                "db_ha_copy_delay_page", Messages.msgDbHaCopyDelayPage, Messages.msgDbHaInfo),
        DB_HA_COPY_DELAY_ESTIMATED(
                "db_ha_copy_delay_estimated",
                Messages.msgDbHaCopyDelayEstimated,
                Messages.msgDbHaInfo),
        DB_HA_APPLY_DELAY_PAGE(
                "db_ha_apply_delay_page", Messages.msgDbHaApplyDelayPage, Messages.msgDbHaInfo),
        DB_HA_APPLY_DELAY_ESTIMATED(
                "db_ha_apply_delay_estimated",
                Messages.msgDbHaApplyDelayEstimated,
                Messages.msgDbHaInfo),
        DB_FREESPACE("db_freespace", Messages.msgDbFreespace, Messages.msgDbFreeSpaceInfo),
        VOL_FREESPACE("vol_freespace", Messages.msgVolFreespace, Messages.msgDbVolFreeSpaceInfo),
        BROKER_TPS("broker_tps", Messages.msgBrokerTps, Messages.msgBrokerInfo),
        BROKER_QPS("broker_qps", Messages.msgBrokerQps, Messages.msgBrokerInfo),
        BROKER_LONG_T("broker_long_t", Messages.msgBrokerLongT, Messages.msgBrokerInfo),
        BROKER_LONG_Q("broker_long_q", Messages.msgBrokerLongQ, Messages.msgBrokerInfo),
        BROKER_REQ("broker_req", Messages.msgBrokerReq, Messages.msgBrokerInfo),
        BROKER_ERR_Q("broker_err_q", Messages.msgBrokerErrQ, Messages.msgBrokerInfo),
        BROKER_JQ("broker_jq", Messages.msgBrokerJq, Messages.msgBrokerInfo),
        OS_CPU_IDLE("os_cpu_idle", Messages.msgOsCpuIdle, Messages.msgOsCpuUsage),
        OS_CPU_IOWAIT("os_cpu_iowait", Messages.msgOsCpuIowait, Messages.msgOsCpuUsage),
        OS_CPU_KERNEL("os_cpu_kernel", Messages.msgOsCpuKernel, Messages.msgOsCpuUsage),
        OS_CPU_USER("os_cpu_user", Messages.msgOsCpuUser, Messages.msgOsCpuUsage),
        OS_MEM_PHY_FREE("os_mem_phy_free", Messages.msgOsMemPhyFree, Messages.msgOsMemInfo),
        OS_MEM_SWAP_FREE("os_mem_swap_free", Messages.msgOsMemSwapFree, Messages.msgOsMemInfo),
        OS_DISK_FREE("os_disk_free", Messages.msgOsDiskFree, Messages.msgOsSpaceInfo);

        private String metric;
        private String message;
        private String chartName;

        MetricType(String metric, String message, String chartName) {
            this.metric = metric;
            this.message = message;
            this.chartName = chartName;
        }

        public String getMetric() {
            return this.metric;
        }

        public String getMessage() {
            return this.message;
        }

        public String getChartName() {
            return chartName;
        }

        /**
         * @param metric
         * @return
         */
        public static MetricType getEnumByMetric(String metric) {
            if (StringUtil.isEmpty(metric)) {
                return null;
            }
            for (MetricType metricType : MetricType.class.getEnumConstants()) {
                if (metricType.getMetric().equals(metric)) {
                    return metricType;
                }
            }
            return null;
        }

        /**
         * @param message
         * @return
         */
        public static MetricType getEnumByMessage(String message) {
            if (StringUtil.isEmpty(message)) {
                return null;
            }
            for (MetricType metricType : MetricType.class.getEnumConstants()) {
                if (metricType.getMessage().equals(message)) {
                    return metricType;
                }
            }
            return null;
        }
    }

    /**
     * @deprecated
     * @author Santiago Wang
     */
    public enum DbMetricType {
        CPU(METRIC_DB_CPU_SET),
        MEM(METRIC_DB_MEM_SET),
        APP(METRIC_DB_APP_SET),
        IO(METRIC_DB_IO_SET),
        PAGE(METRIC_DB_PAGE_SET),
        HA(METRIC_DB_HA_SET);

        private final Set<MetricType> metricTypeSet;

        DbMetricType(Set<MetricType> metricTypeSet) {
            this.metricTypeSet = metricTypeSet;
        }

        public Set<MetricType> getMetricTypeSet() {
            return EnumSet.copyOf(metricTypeSet);
        }
    }

    /**
     * @deprecated
     * @author Santiago Wang
     */
    public enum OsMetricType {
        CPU(METRIC_OS_CPU_SET),
        MEM(METRIC_OS_MEM_SET),
        SPACE(METRIC_OS_SPACE_SET);

        private final Set<MetricType> metricTypeSet;

        OsMetricType(Set<MetricType> metricTypeSet) {
            this.metricTypeSet = metricTypeSet;
        }

        public Set<MetricType> getMetricTypeSet() {
            return EnumSet.copyOf(metricTypeSet);
        }
    }

    public enum ChartType {
        PERCENT,
        MEMORY,
        SPACE,
        OTHERS;
    }

    /**
     * Get available data types for request parameter.
     *
     * @return
     */
    public static String[] getDateTypes() {
        return DTYPES.clone();
    }

    public static String[] getSendMsgItems(StatisticType type) {
        if (type == null) {
            return null;
        }
        String[] sendMSGItems = null;
        switch (type) {
            case DB:
                sendMSGItems = DB_SEND_MSG.clone();
                break;
            case DB_VOL:
                sendMSGItems = DB_VOL_SEND_MSG.clone();
                break;
            case BROKER:
                sendMSGItems = BROKER_SEND_MSG.clone();
                break;
            case OS:
                sendMSGItems = OS_SEND_MSG.clone();
                break;
            default:
        }
        return sendMSGItems;
    }

    public static Set<MetricType> getSupportedMetricTypes(StatisticType type) {
        if (type == null) {
            return null;
        }

        return type.getMetricTypeSet();
    }

    public static boolean isSupportedMetricType(StatisticType type, MetricType metricType) {
        if (type == null || metricType == null) {
            return false;
        }

        return type.getMetricTypeSet().contains(metricType);
    }

    public static boolean isSupportedMetric(StatisticType type, String metric) {
        if (type == null || StringUtil.isEmpty(metric)) {
            return false;
        }
        for (MetricType metricType : type.getMetricTypeSet()) {
            if (metricType.getMetric().equals(metric)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param metric
     * @return
     */
    public static Set<MetricType> getCompatibleMetricsForDisplay(String metric) {
        if (StringUtil.isEmpty(metric)) {
            return null;
        }

        MetricType metricType = MetricType.getEnumByMetric(metric);
        if (metricType == null) {
            return null;
        }
        if (METRIC_DB_CPU_SET.contains(metricType)) {
            return EnumSet.copyOf(METRIC_DB_CPU_SET);
        } else if (METRIC_DB_MEM_SET.contains(metricType)) {
            return EnumSet.copyOf(METRIC_DB_MEM_SET);
        } else if (METRIC_DB_APP_SET.contains(metricType)) {
            return EnumSet.copyOf(METRIC_DB_APP_SET);
        } else if (METRIC_DB_IO_SET.contains(metricType)) {
            return EnumSet.copyOf(METRIC_DB_IO_SET);
        } else if (METRIC_DB_PAGE_SET.contains(metricType)) {
            return EnumSet.copyOf(METRIC_DB_PAGE_SET);
        } else if (METRIC_DB_HA_SET.contains(metricType)) {
            return EnumSet.copyOf(METRIC_DB_HA_SET);
        } else if (METRIC_DB_SPACE_SET.contains(metricType)) {
            return EnumSet.copyOf(METRIC_DB_SPACE_SET);
        } else if (METRIC_DB_VOL_SET.contains(metricType)) {
            return EnumSet.copyOf(METRIC_DB_VOL_SET);
        } else if (METRIC_BROKER_SET.contains(metricType)) {
            return EnumSet.copyOf(METRIC_BROKER_SET);
        } else if (METRIC_OS_CPU_SET.contains(metricType)) {
            return EnumSet.copyOf(METRIC_OS_CPU_SET);
        } else if (METRIC_OS_MEM_SET.contains(metricType)) {
            return EnumSet.copyOf(METRIC_OS_MEM_SET);
        } else if (METRIC_OS_SPACE_SET.contains(metricType)) {
            return EnumSet.copyOf(METRIC_OS_SPACE_SET);
        }

        return null;
    }

    /**
     * @param metric1
     * @param metric2
     * @return
     */
    public static boolean isCompatibleMetricForDisplay(String metric1, String metric2) {
        if (metric1 == null || metric2 == null) {
            return false;
        }
        Set<MetricType> metricSet1 = getCompatibleMetricsForDisplay(metric1);
        if (metricSet1 == null || metricSet1.isEmpty()) {
            return false;
        }
        for (MetricType metricType : metricSet1) {
            if (metricType.getMetric().equals(metric2)) {
                return true;
            }
        }
        return false;
    }

    public static StatisticType getTypeByMetric(String metric) {
        if (StringUtil.isEmpty(metric)) {
            return null;
        }

        for (StatisticType type : StatisticType.class.getEnumConstants()) {
            for (MetricType metricType : type.getMetricTypeSet()) {
                if (metricType.getMetric().equals(metric)) {
                    return type;
                }
            }
        }
        return null;
    }

    public static StatisticType getTypeByMetric(MetricType metricType) {
        if (metricType == null) {
            return null;
        }

        for (StatisticType type : StatisticType.class.getEnumConstants()) {
            if (type.getMetricTypeSet().contains(metricType)) {
                return type;
            }
        }
        return null;
    }

    public static boolean isPercentageData(String metric) {
        if (metric == null) {
            return false;
        }
        /*solution 2: check whether metric has word "cpu" or "ratio"*/
        if (metric.indexOf("cpu") > -1 || metric.indexOf("ratio") > -1) {
            return true;
        }
        return false;
        // return contains(METRIC_PERCENT, metric);
    }

    public static boolean isMemoryData(String metric) {
        if (metric == null) {
            return false;
        }
        if (metric.indexOf("mem") > -1) {
            return true;
        }
        return false;
    }

    public static boolean isDiskData(String metric) {
        if (metric == null) {
            return false;
        }
        if (metric.indexOf("freespace") > -1 || metric.indexOf("disk") > -1) {
            return true;
        }
        return false;
    }

    /*private static boolean contains(Object[] ar, Object element) {
    	if (ar == null || element == null) {
    		return false;
    	}

    	for (Object o : ar) {
    		if (element.equals(o)) {
    			return true;
    		}
    	}
    	return false;
    }*/

}
