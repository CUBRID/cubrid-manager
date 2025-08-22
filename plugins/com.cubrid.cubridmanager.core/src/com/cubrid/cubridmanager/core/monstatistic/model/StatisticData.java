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

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.cubridmanager.core.common.model.IModel;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil.MetricType;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil.StatisticType;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

/**
 * A class that extends IModel and is responsible for the task of "get_mon_statistic"
 *
 * @author Santiago Wang
 * @version 1.0 - 2013-6-25 created by Santiago Wang
 */
public class StatisticData implements IModel, Cloneable {
    private static final Logger LOGGER = LogUtil.getLogger(StatisticData.class);

    private StatisticType type;
    private String metric;
    private String dbName;
    private String volName;
    private String bName;
    private String dtype;
    private ServerInfo serverInfo;

    private List<Integer> data = new ArrayList<Integer>();

    /*"data" : []
    "dtype" : "monthly",
    "metric" : "os_cpu_idle",
    "note" : "none",
    "status" : "success",
    "task" : "get_mon_statistic"*/
    public String getTaskName() {
        return "get_mon_statistic";
    }

    public void setType(StatisticType type) {
        this.type = type;
    }

    public StatisticType getType() {
        if (type != null) {
            return type;
        } else if (bName != null) {
            type = StatisticType.BROKER;
        } else if (volName != null) {
            type = StatisticType.DB_VOL;
        } else if (dbName != null) {
            type = StatisticType.DB;
        } else {
            type = StatisticType.OS;
        }
        return type;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
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

    public String getbName() {
        return bName;
    }

    public void setbName(String bName) {
        this.bName = bName;
    }

    public void setDtype(String dtype) {
        this.dtype = dtype;
    }

    public String getDtype() {
        return dtype;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    /*public void setData(List<Long> data) {
    	this.data = data;
    }*/

    public List<Integer> getData() {
        return data;
    }

    public void addData(String value) {
        this.data.add(Integer.parseInt(value));
    }

    public String getDescription(boolean isMultiHost) {
        StringBuilder sb = new StringBuilder();
        MetricType metricType = MetricType.getEnumByMetric(metric);
        sb.append(metricType.getMessage());
        switch (getType()) {
            case DB:
                sb.append(":").append(dbName);
                break;
            case DB_VOL:
                sb.append(":").append(dbName);
                sb.append(":").append(volName);
                break;
            case BROKER:
                sb.append(":").append(bName);
                break;
            case OS:
                break;
            default:
                break;
        }
        if (isMultiHost && serverInfo != null) {
            if (serverInfo.getServerName() != null) {
                sb.append("@").append(serverInfo.getServerName());
            } else {
                sb.append("@").append(serverInfo.getHostAddress());
                sb.append(":").append(serverInfo.getHostMonPort());
            }
        }
        return sb.toString();
    }

    public String getSimpleDescription(boolean isMultiHost) {
        StringBuilder sb = new StringBuilder();
        MetricType metricType = MetricType.getEnumByMetric(metric);
        sb.append(metricType.getMessage());
        if (isMultiHost && serverInfo != null) {
            if (serverInfo.getServerName() != null) {
                sb.append("@").append(serverInfo.getServerName());
            } else {
                sb.append("@").append(serverInfo.getHostAddress());
                sb.append(":").append(serverInfo.getHostMonPort());
            }
        }
        return sb.toString();
    }

    @Override
    public Object clone() {
        StatisticData result = new StatisticData();
        result.setbName(bName);
        result.setDbName(dbName);
        result.setDtype(dtype);
        result.setMetric(metric);
        result.setServerInfo(serverInfo);
        result.setType(type);
        result.setVolName(volName);
        return result;
    }
}
