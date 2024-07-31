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
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil.MetricType;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil.StatisticType;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains info for getting data for monitor statistic chart.
 *
 * @author Santiago Wang
 * @version 1.0 - 2013-7-29 created by Santiago Wang
 */
public class SingleHostChartItem extends StatisticChartItem {

    private List<String> metricList = new ArrayList<String>();
    private String dbName;
    private String volName;
    private String brokerName;

    public SingleHostChartItem(String nodeId, StatisticType type) {
        super(nodeId, type);
    }

    public SingleHostChartItem(String nodeId, StatisticType type, String dtype) {
        super(nodeId, type, dtype);
    }

    @Override
    public boolean isMultiHost() {
        return false;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getVolName() {
        return volName;
    }

    public void setVolName(String volName) {
        this.volName = volName;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public List<String> getMetricList() {
        if (metricList == null) {
            metricList = new ArrayList<String>();
        }
        return metricList;
    }

    public void setMetricList(List<String> metricList) {
        this.metricList = metricList;
    }

    public void addMetric(String metric) {
        if (StringUtil.isEmpty(metric)
                || !StatisticParamUtil.isSupportedMetric(super.getType(), metric)) {
            return;
        }
        if (metricList == null) {
            metricList = new ArrayList<String>();
        }

        metricList.add(metric);
    }

    @Override
    public String getName() {
        for (String metric : metricList) {
            MetricType metricType = MetricType.getEnumByMetric(metric);
            if (metricType != null) {
                return metricType.getChartName();
            }
        }
        return "";
    }

    // TODO: pay attention to clone()
    @Override
    public Object clone() {
        SingleHostChartItem chartItem =
                new SingleHostChartItem(this.getNodeId(), this.getType(), this.getDType());
        chartItem.setSeries(this.getSeries());

        List<String> metrics = new ArrayList<String>();
        metrics.addAll(this.getMetricList());
        chartItem.setMetricList(metrics);
        chartItem.setDbName(dbName);
        chartItem.setVolName(volName);
        chartItem.setBrokerName(brokerName);

        return chartItem;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof SingleHostChartItem)) {
            return false;
        }
        SingleHostChartItem chartItem = (SingleHostChartItem) obj;
        if (!(this.dbName != null && this.dbName.equals(chartItem.getDbName()))
                && !(this.dbName == null && chartItem.getDbName() == null)) {
            return false;
        }
        if (!(this.volName != null && this.volName.equals(chartItem.getVolName()))
                && !(this.volName == null && chartItem.getVolName() == null)) {
            return false;
        }
        if (!(this.brokerName != null && this.brokerName.equals(chartItem.getBrokerName()))
                && !(this.brokerName == null && chartItem.getBrokerName() == null)) {
            return false;
        }
        int metricListSize = this.metricList != null ? this.metricList.size() : 0;
        List<String> comparedMetricList = chartItem.getMetricList();
        if (metricListSize != comparedMetricList.size()) {
            return false;
        }
        for (int i = 0; i < metricListSize; i++) {
            if (!(this.metricList.get(i) != null
                            && this.metricList.get(i).equals(comparedMetricList.get(i)))
                    && !(this.metricList.get(i) == null && comparedMetricList.get(i) == null)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashCode();
        hashCode += dbName != null ? dbName.hashCode() : 0;
        hashCode += volName != null ? volName.hashCode() : 0;
        hashCode += brokerName != null ? brokerName.hashCode() : 0;
        int metricListSize = metricList != null ? metricList.size() : 0;
        for (int i = 0; i < metricListSize; i++) {
            hashCode += metricList.get(i) != null ? metricList.get(i).hashCode() : 0;
        }
        return hashCode;
    }
}
