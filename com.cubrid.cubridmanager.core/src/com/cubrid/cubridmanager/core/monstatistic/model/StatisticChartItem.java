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

import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil.StatisticType;

/**
 * This class contains info for getting data for monitor statistic chart.
 *
 * @author Santiago Wang
 * @version 1.0 - 2013-7-29 created by Santiago Wang
 */
public abstract class StatisticChartItem implements Cloneable {
    private final String nodeId;
    private StatisticType type;
    private String dType;
    private int series = -1;
    private String name;

    public StatisticChartItem(String nodeId, StatisticType type) {
        this.nodeId = nodeId;
        this.type = type;
    }

    public StatisticChartItem(String nodeId, StatisticType type, String dType) {
        this.nodeId = nodeId;
        this.type = type;
        this.dType = dType;
    }

    public String getNodeId() {
        return nodeId;
    }

    public StatisticType getType() {
        return type;
    }

    public void setType(StatisticType type) {
        this.type = type;
    }

    /**
     * Get the date(time) type, which is used for parameter 'dtype' in CM API.
     *
     * @return dType
     */
    public String getDType() {
        return dType;
    }

    /**
     * Set the date type, which is used for parameter 'dtype' in CM API.
     *
     * @param dType
     */
    public void setDType(String dType) {
        this.dType = dType;
    }

    /**
     * Get the series number about the chart represent by this object in a monitor statistic page
     *
     * @return series
     */
    public int getSeries() {
        return series;
    }

    /**
     * Set the series number about the chart represent by this object in a monitor statistic page
     *
     * @param series
     */
    public void setSeries(int series) {
        this.series = series;
    }

    public abstract boolean isMultiHost();

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // TODO: pay attention to clone()
    @Override
    public abstract Object clone();

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof StatisticChartItem)) {
            return false;
        }
        StatisticChartItem chartItem = (StatisticChartItem) obj;
        if (!(this.nodeId != null && this.nodeId.equals(chartItem.getNodeId()))
                && !(this.nodeId == null && chartItem.getNodeId() == null)) {
            return false;
        }
        if (this.type != chartItem.getType()) {
            return false;
        }
        if (!(this.dType != null && this.dType.equals(chartItem.getDType()))
                && !(this.dType == null && chartItem.getDType() == null)) {
            return false;
        }
        if (this.series != chartItem.getSeries()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode += nodeId != null ? nodeId.hashCode() : 0;
        hashCode += type != null ? type.hashCode() : 0;
        hashCode += dType != null ? dType.hashCode() : 0;
        hashCode += series;
        return hashCode;
    }
}
