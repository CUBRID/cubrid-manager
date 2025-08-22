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
package com.cubrid.cubridmanager.core.shard.model;

import com.cubrid.cubridmanager.core.common.model.OnOffType;

/**
 * Shard status
 *
 * @author Tobi
 * @version 1.0
 * @date 2013-1-8
 */
@Table(name = "ShardStatus")
public class ShardStatus {

    @Column(name = "name")
    private String name;

    // @Column(name = "type")
    // private String type;

    @Column(name = "state")
    private String status = OnOffType.ON.getText();

    @Column(name = "pid")
    private String pid;

    @Column(name = "port")
    private String port;

    @Column(name = "canceled")
    private String canceled;

    @Column(name = "req")
    private String req;

    @Column(name = "qps")
    private String qps;

    @Column(name = "tps")
    private String tps;

    @Column(name = "psize", visiable = false)
    private String psize;

    @Column(name = "active-p", visiable = false)
    private String activeP;

    @Column(name = "active-c", visiable = false)
    private String activeC;

    @Column(name = "long_tran", visiable = false, enable = false)
    private String longTran;

    @Column(name = "long_query", visiable = false, enable = false)
    private String longQuery;

    @Column(name = "error_query", visiable = false)
    private String errorQuery;

    @Column(name = "long_tran_time", visiable = false, enable = false)
    private String longTranTime;

    @Column(name = "long_query_time", visiable = false, enable = false)
    private String longQueryTime;

    @SuppressWarnings("unused")
    @Column(name = "long_t", visiable = false)
    private String longTranStr;

    @SuppressWarnings("unused")
    @Column(name = "long_q", visiable = false)
    private String longQueryStr;

    @Column(name = "access_mode", visiable = false)
    private String accessMode;

    @Column(name = "sqll", visiable = false)
    private String sqll;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // public String getType() {
    // return type;
    // }
    //
    // public void setType(String type) {
    // this.type = type;
    // }

    public String getStatus() {
        return status;
    }

    public boolean getStatusTag() {
        return OnOffType.ON.getText().equals(this.getStatus());
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPsize() {
        return psize;
    }

    public void setPsize(String psize) {
        this.psize = psize;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getActiveP() {
        return activeP;
    }

    public void setActiveP(String activeP) {
        this.activeP = activeP;
    }

    public String getActiveC() {
        return activeC;
    }

    public void setActiveC(String activeC) {
        this.activeC = activeC;
    }

    public String getReq() {
        return req;
    }

    public void setReq(String req) {
        this.req = req;
    }

    public String getLongTran() {
        return longTran;
    }

    public void setLongTran(String longTran) {
        this.longTran = longTran;
    }

    public String getLongQuery() {
        return longQuery;
    }

    public void setLongQuery(String longQuery) {
        this.longQuery = longQuery;
    }

    public String getErrorQuery() {
        return errorQuery;
    }

    public void setErrorQuery(String errorQuery) {
        this.errorQuery = errorQuery;
    }

    public String getLongTranTime() {
        return longTranTime;
    }

    public void setLongTranTime(String longTranTime) {
        this.longTranTime = longTranTime;
    }

    public String getLongQueryTime() {
        return longQueryTime;
    }

    public void setLongQueryTime(String longQueryTime) {
        this.longQueryTime = longQueryTime;
    }

    public String getAccessMode() {
        return accessMode;
    }

    public void setAccessMode(String accessMode) {
        this.accessMode = accessMode;
    }

    public String getSqll() {
        return sqll;
    }

    public void setSqll(String sqll) {
        this.sqll = sqll;
    }

    public String getCanceled() {
        return canceled;
    }

    public void setCanceled(String canceled) {
        this.canceled = canceled;
    }

    public String getQps() {
        return qps;
    }

    public void setQps(String qps) {
        this.qps = qps;
    }

    public String getTps() {
        return tps;
    }

    public void setTps(String tps) {
        this.tps = tps;
    }

    public String getLongTranStr() {
        double dVal = Double.parseDouble(longTranTime) * 1000;
        longTranTime = String.valueOf((int) (dVal + 0.5));
        return longTran + "/" + longTranTime;
    }

    public void setLongTranStr(String longTranStr) {
        this.longTranStr = longTranStr;
    }

    public String getLongQueryStr() {
        double dVal = Double.parseDouble(longQueryTime) * 1000;
        longQueryTime = String.valueOf((int) (dVal + 0.5));
        return longQuery + "/" + longQueryTime;
    }

    public void setLongQueryStr(String longQueryStr) {
        this.longQueryStr = longQueryStr;
    }
}
