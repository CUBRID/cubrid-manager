/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the <ORGANIZATION> nor the names of its contributors
 *   may be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */
package com.cubrid.common.ui.query.tuner;

import com.cubrid.common.core.queryplan.StructQueryPlan;
import com.cubrid.common.core.util.DateUtil;
import com.cubrid.common.ui.query.control.ColumnInfo;
import com.cubrid.common.ui.query.control.QueryInfo;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

/**
 * QueryRecord Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-4-11 created by Kevin.Wang
 */
public class QueryRecord implements Cloneable {
    public static final DateFormat DATE_FORMAT =
            DateUtil.getDateFormat("MM-dd HH:mm:ss", Locale.ENGLISH);

    private long startTime;
    private long stopTime;
    private String query;
    private StructQueryPlan queryPlan;
    private Map<String, String> statistics;
    private final Date createDate;
    private String name;

    private int threadExecResult;
    private QueryInfo queryInfo;

    private Exception errorException;
    private List<ColumnInfo> columnInfoList;
    private List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();

    /**
     * The constructor
     *
     * @param query
     * @param startTime
     * @param stopTime
     */
    public QueryRecord(String query, long startTime, Date createDate) {
        this(query, startTime, -1, createDate, null);
    }

    /**
     * The constructor
     *
     * @param query
     * @param startTime
     * @param stopTime
     */
    public QueryRecord(String query, long startTime, long stopTime, Date createDate) {
        this(query, startTime, -stopTime, createDate, null);
    }

    /**
     * The constructor
     *
     * @param query
     * @param startTime
     * @param stopTime
     * @param errorException
     */
    public QueryRecord(
            String query,
            long startTime,
            long stopTime,
            Date createDate,
            Exception errorException) {
        this.query = query;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.errorException = errorException;
        this.createDate = createDate;
    }

    /** @return the errorException */
    public Exception getErrorException() {
        return errorException;
    }

    /** @param errorException the errorException to set */
    public void setErrorException(Exception errorException) {
        this.errorException = errorException;
    }

    /** @return the startTime */
    public long getStartTime() {
        return startTime;
    }

    /** @return the stopTime */
    public long getStopTime() {
        return stopTime;
    }

    /** @return the query */
    public String getQuery() {
        return query;
    }

    /** @return the queryPlan */
    public StructQueryPlan getQueryPlan() {
        return queryPlan;
    }

    /** @param queryPlan the queryPlan to set */
    public void setQueryPlan(StructQueryPlan queryPlan) {
        this.queryPlan = queryPlan;
    }

    /** @param startTime the startTime to set */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /** @param stopTime the stopTime to set */
    public void setStopTime(long stopTime) {
        this.stopTime = stopTime;
    }

    /** @param query the query to set */
    public void setQuery(String query) {
        this.query = query;
    }

    /** @return the threadExecResult */
    public int getThreadExecResult() {
        return threadExecResult;
    }

    /** @param threadExecResult the threadExecResult to set */
    public void setThreadExecResult(int threadExecResult) {
        this.threadExecResult = threadExecResult;
    }

    /** @return the statistics */
    public Map<String, String> getStatistics() {
        return statistics;
    }

    /** @param statistics the statistics to set */
    public void setStatistics(Map<String, String> statistics) {
        this.statistics = statistics;
    }

    /** @return the dataList */
    public List<Map<String, String>> getDataList() {
        return dataList;
    }

    /** @param dataList the dataList to set */
    public void setDataList(List<Map<String, String>> dataList) {
        this.dataList = dataList;
    }

    /** @return the queryInfo */
    public QueryInfo getQueryInfo() {
        return queryInfo;
    }

    /** @param queryInfo the queryInfo to set */
    public void setQueryInfo(QueryInfo queryInfo) {
        this.queryInfo = queryInfo;
    }

    /** @return the columnInfoList */
    public List<ColumnInfo> getColumnInfoList() {
        return columnInfoList;
    }

    /** @param columnInfoList the columnInfoList to set */
    public void setColumnInfoList(List<ColumnInfo> columnInfoList) {
        this.columnInfoList = columnInfoList;
    }

    public List<Map<String, String>> getPageData() {
        return dataList;
    }

    /** @return the createDate */
    public Date getCreateDate() {
        return createDate;
    }

    /** @return the name */
    public String getName() {
        if (name == null) {
            name = getTimeStamp();
        }
        return name;
    }

    public String getTimeStamp() {
        return DATE_FORMAT.format(getCreateDate());
    }

    /** @param name the name to set */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Clone a object. It not a deep clone
     *
     * @return QueryRecord
     */
    public QueryRecord clone() {
        QueryRecord queryRecord = null;
        try {
            queryRecord = (QueryRecord) super.clone();
        } catch (CloneNotSupportedException e) {
        }
        if (queryPlan != null) {
            queryRecord.setQueryPlan(queryPlan.clone());
        }

        if (statistics != null) {
            LinkedHashMap<String, String> cloneStatistics = new LinkedHashMap<String, String>();
            for (Entry<String, String> entry : statistics.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                cloneStatistics.put(key, value);
            }
            queryRecord.setStatistics(cloneStatistics);
        }
        if (queryInfo != null) {
            QueryInfo clonedInfo = queryInfo.clone();
            queryRecord.setQueryInfo(clonedInfo);
        }
        if (columnInfoList != null) {
            List<ColumnInfo> cloneList = new ArrayList<ColumnInfo>();
            for (ColumnInfo columnInfo : columnInfoList) {
                cloneList.add(columnInfo.clone());
            }
            queryRecord.setColumnInfoList(columnInfoList);
        }

        return queryRecord;
    }
}
