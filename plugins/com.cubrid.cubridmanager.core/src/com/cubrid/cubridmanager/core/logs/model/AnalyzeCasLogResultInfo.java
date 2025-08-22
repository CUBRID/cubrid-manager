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

package com.cubrid.cubridmanager.core.logs.model;

/**
 * AnalyzeCasLogResult information model class
 *
 * @author wuyingshi
 * @version 1.0 - 2009-4-24 created by wuyingshi
 */
public class AnalyzeCasLogResultInfo {

    private String qindex = null;
    private String max = null;
    private String min = null;
    private String avg = null;
    private String cnt = null;
    private String err = null;
    private String execTime = null;
    private String queryString = "";
    private String savedFileName = null;

    /**
     * get the qindex.
     *
     * @return String
     */
    public String getQindex() {
        return qindex;
    }

    /**
     * set the qindex.
     *
     * @param qindex String
     */
    public void setQindex(String qindex) {
        this.qindex = qindex;
    }

    /**
     * get the max.
     *
     * @return String
     */
    public String getMax() {
        return max;
    }

    /**
     * set the max.
     *
     * @param max String
     */
    public void setMax(String max) {
        this.max = max;
    }

    /**
     * get the min.
     *
     * @return String
     */
    public String getMin() {
        return min;
    }

    /**
     * set the min.
     *
     * @param min String
     */
    public void setMin(String min) {
        this.min = min;
    }

    /**
     * get the avg.
     *
     * @return String
     */
    public String getAvg() {
        return avg;
    }

    /**
     * set the avg.
     *
     * @param avg String
     */
    public void setAvg(String avg) {
        this.avg = avg;
    }

    /**
     * get the cnt.
     *
     * @return String
     */
    public String getCnt() {
        return cnt;
    }

    /**
     * set the cnt.
     *
     * @param cnt String
     */
    public void setCnt(String cnt) {
        this.cnt = cnt;
    }

    /**
     * get the err.
     *
     * @return String
     */
    public String getErr() {
        return err;
    }

    /**
     * set the err.
     *
     * @param err String
     */
    public void setErr(String err) {
        this.err = err;
    }

    /**
     * get the execTime.
     *
     * @return String
     */
    public String getExecTime() {
        return execTime;
    }

    /**
     * set the execTime
     *
     * @param execTime String
     */
    public void setExecTime(String execTime) {
        this.execTime = execTime;
    }

    /**
     * get the queryString.
     *
     * @return String
     */
    public String getQueryString() {
        return queryString;
    }

    /**
     * set the queryString.
     *
     * @param queryString String
     */
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    /**
     * get the savedFileName
     *
     * @return String
     */
    public String getSavedFileName() {
        return savedFileName;
    }

    /**
     * set the savedFileName
     *
     * @param savedFileName String
     */
    public void setSavedFileName(String savedFileName) {
        this.savedFileName = savedFileName;
    }
}
