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
public class MultiHostChartItem extends StatisticChartItem {
    private List<StatisticChartHost> hostList;

    public MultiHostChartItem(String nodeId, StatisticType type) {
        super(nodeId, type);
    }

    public MultiHostChartItem(String nodeId, StatisticType type, String dtype) {
        super(nodeId, type, dtype);
    }

    @Override
    public boolean isMultiHost() {
        return true;
    }

    public void addStatisticChartHost(StatisticChartHost hostItem) {
        if (hostItem == null) {
            return;
        }
        if (hostList == null) {
            hostList = new ArrayList<StatisticChartHost>();
        }
        hostList.add(hostItem);
    }

    public void setHostList(List<StatisticChartHost> hostList) {
        this.hostList = hostList;
    }

    public List<StatisticChartHost> getHostList() {
        if (hostList == null) {
            hostList = new ArrayList<StatisticChartHost>();
        }
        return hostList;
    }

    @Override
    public String getName() {
        for (StatisticChartHost hostItem : hostList) {
            MetricType metricType = MetricType.getEnumByMetric(hostItem.getMetric());
            if (metricType != null) {
                return metricType.getChartName();
            }
        }
        return "";
    }

    // TODO: pay attention to clone()
    @Override
    public Object clone() {
        MultiHostChartItem chartItem =
                new MultiHostChartItem(this.getNodeId(), this.getType(), this.getDType());
        chartItem.setSeries(this.getSeries());
        List<StatisticChartHost> hosts = new ArrayList<StatisticChartHost>();
        hosts.addAll(hostList);
        chartItem.setHostList(hosts);
        return chartItem;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof MultiHostChartItem)) {
            return false;
        }
        MultiHostChartItem chartItem = (MultiHostChartItem) obj;
        int hostListSize = this.hostList != null ? this.hostList.size() : 0;
        List<StatisticChartHost> comparedHostList = chartItem.getHostList();
        if (hostListSize != comparedHostList.size()) {
            return false;
        }
        /*else if(hostListSize == 0){
        	return true;
        }*/
        for (int i = 0; i < hostListSize; i++) {
            if (!(this.hostList.get(i) != null
                            && this.hostList.get(i).equals(comparedHostList.get(i)))
                    && !(this.hostList.get(i) == null && comparedHostList.get(i) == null)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashCode();
        int hostListSize = hostList != null ? hostList.size() : 0;
        for (int i = 0; i < hostListSize; i++) {
            hashCode += hostList.get(i) != null ? hostList.get(i).hashCode() : 0;
        }
        return hashCode;
    }
}
