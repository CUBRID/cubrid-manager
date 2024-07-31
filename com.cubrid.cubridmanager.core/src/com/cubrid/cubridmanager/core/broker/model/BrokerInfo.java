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
package com.cubrid.cubridmanager.core.broker.model;

import com.cubrid.cubridmanager.core.common.model.OnOffType;

/**
 * The plain java of BrokerInfo
 *
 * @author llizhiqiang 2009-5-19
 */
public class BrokerInfo implements Cloneable {
    // broker name
    private String name = null;
    // broker type (CAS)
    private String type = null;
    // broker process id
    private String pid = null;
    // broker port
    private String port = null;
    // applay server number
    private String as = null;
    // job queue number
    private String jq = null;
    // request number of this queue
    private String req = null;
    // auto_add_apply_server (ON or OFF)
    private String auto = null;
    // tran
    private String tran = null;
    // query
    private String query = null;
    // LONG TRAN
    private String long_tran = "0";
    // LONG QUERY
    private String long_query = "0";
    // ERROR QRY
    private String error_query = "0";
    // keep connection
    private String keep_conn = null;
    // session_timeout(ON or OFF)
    private String ses = null;
    // sql log mode (ALL or other)
    private String sqll = null;
    // log director
    private String log = null;
    // ON or OFF
    private String state = OnOffType.OFF.getText();
    private String source_env = null;
    private String access_list = null;
    private String appl_server_shm_id = null;
    // Long tran time
    private String long_tran_time = "0";
    // Long query time
    private String long_query_time = "0";
    // access_mode
    private String access_mode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getLong_tran() {
        return long_tran;
    }

    public void setLong_tran(String longTran) {
        this.long_tran = longTran;
    }

    public String getLong_query() {
        return long_query;
    }

    public void setLong_query(String longQuery) {
        this.long_query = longQuery;
    }

    public String getError_query() {
        return error_query;
    }

    public void setError_query(String errorQuery) {
        this.error_query = errorQuery;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getAs() {
        return as;
    }

    public void setAs(String as) {
        this.as = as;
    }

    public String getJq() {
        return jq;
    }

    public void setJq(String jq) {
        this.jq = jq;
    }

    public String getReq() {
        return req;
    }

    public void setReq(String req) {
        this.req = req;
    }

    public String getAuto() {
        return auto;
    }

    public void setAuto(String auto) {
        this.auto = auto;
    }

    public String getSes() {
        return ses;
    }

    public void setSes(String ses) {
        this.ses = ses;
    }

    public String getSqll() {
        return sqll;
    }

    public void setSqll(String sqll) {
        this.sqll = sqll;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSource_env() {
        return source_env;
    }

    public void setSource_env(String sourceEnv) {
        this.source_env = sourceEnv;
    }

    public String getAccess_list() {
        return access_list;
    }

    public void setAccess_list(String accessList) {
        this.access_list = accessList;
    }

    public String getAppl_server_shm_id() {
        return appl_server_shm_id;
    }

    public void setAppl_server_shm_id(String applServerShmId) {
        this.appl_server_shm_id = applServerShmId;
    }

    public String getTran() {
        return tran;
    }

    public void setTran(String tran) {
        this.tran = tran;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getKeep_conn() {
        return keep_conn;
    }

    public void setKeep_conn(String keepConn) {
        this.keep_conn = keepConn;
    }

    /** @return BrokerInfo */
    public BrokerInfo clone() {
        try {
            return (BrokerInfo) super.clone();
        } catch (CloneNotSupportedException e) {
        }
        return null;
    }

    public String getLong_tran_time() {
        return long_tran_time;
    }

    public void setLong_tran_time(String longTranTime) {
        this.long_tran_time = longTranTime;
    }

    public String getLong_query_time() {
        return long_query_time;
    }

    public void setLong_query_time(String longQueryTime) {
        this.long_query_time = longQueryTime;
    }

    public String getAccess_mode() {
        return access_mode;
    }

    public void setAccess_mode(String accessMode) {
        access_mode = accessMode;
    }
}
