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
package com.cubrid.cubridmanager.core.monitoring.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Status template target config information
 *
 * @author by lizhiqiang 2009-4-29
 */
public class TargetConfigInfo {

    private String[] server_query_open_page;
    private String[] server_query_opened_page;
    private String[] server_query_slow_query;
    private String[] server_query_full_scan;
    private String[] server_conn_cli_request;
    private String[] server_conn_aborted_clients;
    private String[] server_conn_conn_req;
    private String[] server_conn_conn_reject;
    private String[] server_buffer_page_write;
    private String[] server_buffer_page_read;
    private String[] server_lock_deadlock;
    private String[] server_lock_request;
    private String[] cas_st_request;
    private String[] cas_st_transaction;
    private String[] cas_st_active_session;
    private String[] cas_st_query;
    private String[] cas_st_long_query;
    private String[] cas_st_long_tran;
    private String[] cas_st_error_query;

    private final List<String[]> list = new ArrayList<String[]>();;

    public String[] getServer_query_open_page() {
        return server_query_open_page == null ? null : (String[]) (server_query_open_page.clone());
    }

    /**
     * set server_query_open_page
     *
     * @param serverQueryOpenPage String[]
     */
    public void setServer_query_open_page(String[] serverQueryOpenPage) {
        this.server_query_open_page =
                serverQueryOpenPage == null ? null : (String[]) (serverQueryOpenPage.clone());
        if (null != serverQueryOpenPage) {
            String[] strings = new String[3];
            strings[0] = "server_query_open_page";
            strings[1] = serverQueryOpenPage[0];
            strings[2] = serverQueryOpenPage[1];
            list.add(strings);
        }
    }

    public String[] getServer_query_opened_page() {
        return server_query_opened_page == null
                ? null
                : (String[]) (server_query_opened_page.clone());
    }

    /**
     * Set server_query_opened_page
     *
     * @param serverQueryOpenedPage String[]
     */
    public void setServer_query_opened_page(String[] serverQueryOpenedPage) {
        this.server_query_opened_page =
                serverQueryOpenedPage == null ? null : (String[]) (serverQueryOpenedPage.clone());
        if (null != serverQueryOpenedPage) {
            String[] strings = new String[3];
            strings[0] = "server_query_opened_page";
            strings[1] = serverQueryOpenedPage[0];
            strings[2] = serverQueryOpenedPage[1];
            list.add(strings);
        }
    }

    public String[] getServer_query_slow_query() {
        return server_query_slow_query == null
                ? null
                : (String[]) (server_query_slow_query.clone());
    }

    /**
     * Set server_query_slow_query
     *
     * @param serverQuerySlowQuery String[]
     */
    public void setServer_query_slow_query(String[] serverQuerySlowQuery) {
        this.server_query_slow_query =
                serverQuerySlowQuery == null ? null : (String[]) (serverQuerySlowQuery.clone());
        if (null != serverQuerySlowQuery) {
            String[] strings = new String[3];
            strings[0] = "server_query_slow_query";
            strings[1] = serverQuerySlowQuery[0];
            strings[2] = serverQuerySlowQuery[1];
            list.add(strings);
        }
    }

    public String[] getServer_query_full_scan() {
        return server_query_full_scan == null ? null : (String[]) (server_query_full_scan.clone());
    }

    /**
     * Set erver_query_full_scan
     *
     * @param serverQueryFullScan String[]
     */
    public void setServer_query_full_scan(String[] serverQueryFullScan) {
        this.server_query_full_scan =
                serverQueryFullScan == null ? null : (String[]) (serverQueryFullScan.clone());
        if (null != serverQueryFullScan) {
            String[] strings = new String[3];
            strings[0] = "server_query_full_scan";
            strings[1] = serverQueryFullScan[0];
            strings[2] = serverQueryFullScan[1];
            list.add(strings);
        }
    }

    public String[] getServer_conn_cli_request() {
        return server_conn_cli_request == null
                ? null
                : (String[]) (server_conn_cli_request.clone());
    }

    /**
     * set server_conn_cli_request
     *
     * @param serverConnCliRequest String[]
     */
    public void setServer_conn_cli_request(String[] serverConnCliRequest) {
        this.server_conn_cli_request =
                serverConnCliRequest == null ? null : (String[]) (serverConnCliRequest.clone());
        if (null != serverConnCliRequest) {
            String[] strings = new String[3];
            strings[0] = "server_conn_cli_request";
            strings[1] = serverConnCliRequest[0];
            strings[2] = serverConnCliRequest[1];
            list.add(strings);
        }
    }

    public String[] getServer_conn_aborted_clients() {
        return server_conn_aborted_clients == null
                ? null
                : (String[]) (server_conn_aborted_clients.clone());
    }

    /**
     * set server_conn_aborted_clients
     *
     * @param serverConnAbortedClients String[]
     */
    public void setServer_conn_aborted_clients(String[] serverConnAbortedClients) {
        this.server_conn_aborted_clients =
                serverConnAbortedClients == null
                        ? null
                        : (String[]) (serverConnAbortedClients.clone());
        if (null != serverConnAbortedClients) {
            String[] strings = new String[3];
            strings[0] = "server_conn_aborted_clients";
            strings[1] = serverConnAbortedClients[0];
            strings[2] = serverConnAbortedClients[1];
            list.add(strings);
        }
    }

    public String[] getServer_conn_conn_req() {
        return server_conn_conn_req == null ? null : (String[]) (server_conn_conn_req.clone());
    }

    /**
     * set server_conn_conn_req
     *
     * @param serverConnConnReq String[]
     */
    public void setServer_conn_conn_req(String[] serverConnConnReq) {
        this.server_conn_conn_req =
                serverConnConnReq == null ? null : (String[]) (serverConnConnReq.clone());
        if (null != serverConnConnReq) {
            String[] strings = new String[3];
            strings[0] = "server_conn_conn_req";
            strings[1] = serverConnConnReq[0];
            strings[2] = serverConnConnReq[1];
            list.add(strings);
        }
    }

    public String[] getServer_conn_conn_reject() {
        return server_conn_conn_reject == null
                ? null
                : (String[]) (server_conn_conn_reject.clone());
    }

    /**
     * Set server_conn_conn_reject
     *
     * @param serverConnConnReject String[]
     */
    public void setServer_conn_conn_reject(String[] serverConnConnReject) {
        this.server_conn_conn_reject =
                serverConnConnReject == null ? null : (String[]) (serverConnConnReject.clone());
        if (null != serverConnConnReject) {
            String[] strings = new String[3];
            strings[0] = "server_conn_conn_reject";
            strings[1] = serverConnConnReject[0];
            strings[2] = serverConnConnReject[1];
            list.add(strings);
        }
    }

    public String[] getServer_buffer_page_write() {
        return server_buffer_page_write == null
                ? null
                : (String[]) (server_buffer_page_write.clone());
    }
    /**
     * Set server_buffer_page_write
     *
     * @param serverBufferPageWrite String[]
     */
    public void setServer_buffer_page_write(String[] serverBufferPageWrite) {
        this.server_buffer_page_write =
                serverBufferPageWrite == null ? null : (String[]) (serverBufferPageWrite.clone());
        if (null != serverBufferPageWrite) {
            String[] strings = new String[3];
            strings[0] = "server_buffer_page_write";
            strings[1] = serverBufferPageWrite[0];
            strings[2] = serverBufferPageWrite[1];
            list.add(strings);
        }
    }

    public String[] getServer_buffer_page_read() {
        return server_buffer_page_read == null
                ? null
                : (String[]) (server_buffer_page_read.clone());
    }
    /**
     * set server_buffer_page_read
     *
     * @param serverBufferPageRead String[]
     */
    public void setServer_buffer_page_read(String[] serverBufferPageRead) {
        this.server_buffer_page_read =
                serverBufferPageRead == null ? null : (String[]) (serverBufferPageRead.clone());
        if (null != serverBufferPageRead) {
            String[] strings = new String[3];
            strings[0] = "server_buffer_page_read";
            strings[1] = serverBufferPageRead[0];
            strings[2] = serverBufferPageRead[1];
            list.add(strings);
        }
    }

    public String[] getServer_lock_deadlock() {
        return server_lock_deadlock == null ? null : (String[]) (server_lock_deadlock.clone());
    }
    /**
     * set server_lock_deadlock
     *
     * @param serverLockDeadlock String[]
     */
    public void setServer_lock_deadlock(String[] serverLockDeadlock) {
        this.server_lock_deadlock =
                serverLockDeadlock == null ? null : (String[]) (serverLockDeadlock.clone());
        if (null != serverLockDeadlock) {
            String[] strings = new String[3];
            strings[0] = "server_lock_deadlock";
            strings[1] = serverLockDeadlock[0];
            strings[2] = serverLockDeadlock[1];
            list.add(strings);
        }
    }

    public String[] getServer_lock_request() {
        return server_lock_request == null ? null : (String[]) (server_lock_request.clone());
    }
    /**
     * Set server_lock_request
     *
     * @param serverLockRequest String[]
     */
    public void setServer_lock_request(String[] serverLockRequest) {
        this.server_lock_request =
                serverLockRequest == null ? null : (String[]) (serverLockRequest.clone());
        if (null != serverLockRequest) {
            String[] strings = new String[3];
            strings[0] = "server_lock_request";
            strings[1] = serverLockRequest[0];
            strings[2] = serverLockRequest[1];
            list.add(strings);
        }
    }

    public String[] getCas_st_request() {
        return cas_st_request == null ? null : (String[]) (cas_st_request.clone());
    }
    /**
     * set cas_st_request
     *
     * @param casStRequest String[]
     */
    public void setCas_st_request(String[] casStRequest) {
        this.cas_st_request = casStRequest == null ? null : (String[]) (casStRequest.clone());
        if (null != casStRequest) {
            String[] strings = new String[3];
            strings[0] = "cas_st_request";
            strings[1] = casStRequest[0];
            strings[2] = casStRequest[1];
            list.add(strings);
        }
    }

    public String[] getCas_st_transaction() {
        return cas_st_transaction == null ? null : (String[]) (cas_st_transaction.clone());
    }
    /**
     * Set cas_st_transaction
     *
     * @param casStTransaction String[]
     */
    public void setCas_st_transaction(String[] casStTransaction) {
        this.cas_st_transaction =
                casStTransaction == null ? null : (String[]) (casStTransaction.clone());
        if (null != casStTransaction) {
            String[] strings = new String[3];
            strings[0] = "cas_st_transaction";
            strings[1] = casStTransaction[0];
            strings[2] = casStTransaction[1];
            list.add(strings);
        }
    }

    public String[] getCas_st_active_session() {
        return cas_st_active_session == null ? null : (String[]) (cas_st_active_session.clone());
    }
    /**
     * Set cas_st_active_session
     *
     * @param casStActiveSession String[]
     */
    public void setCas_st_active_session(String[] casStActiveSession) {
        this.cas_st_active_session =
                casStActiveSession == null ? null : (String[]) (casStActiveSession.clone());
        if (null != casStActiveSession) {
            String[] strings = new String[3];
            strings[0] = "cas_st_active_session";
            strings[1] = casStActiveSession[0];
            strings[2] = casStActiveSession[1];
            list.add(strings);
        }
    }

    public String[] getCas_st_query() {
        return cas_st_query == null ? null : (String[]) (cas_st_query.clone());
    }
    /**
     * set cas_st_query
     *
     * @param casStQuery String[]
     */
    public void setCas_st_query(String[] casStQuery) {
        this.cas_st_query = casStQuery == null ? null : (String[]) (casStQuery.clone());
        if (null != casStQuery) {
            String[] strings = new String[3];
            strings[0] = "cas_st_query";
            strings[1] = casStQuery[0];
            strings[2] = casStQuery[1];
            list.add(strings);
        }
    }

    public String[] getCas_st_long_query() {
        return cas_st_long_query == null ? null : (String[]) (cas_st_long_query.clone());
    }
    /**
     * set cas_st_long_query
     *
     * @param casStLongQuery String[]
     */
    public void setCas_st_long_query(String[] casStLongQuery) {
        this.cas_st_long_query =
                casStLongQuery == null ? null : (String[]) (casStLongQuery.clone());
        if (null != casStLongQuery) {
            String[] strings = new String[3];
            strings[0] = "cas_st_long_query";
            strings[1] = casStLongQuery[0];
            strings[2] = casStLongQuery[1];
            list.add(strings);
        }
    }

    public String[] getCas_st_long_tran() {
        return cas_st_long_tran == null ? null : (String[]) (cas_st_long_tran.clone());
    }
    /**
     * set cas_st_long_tran
     *
     * @param casStLongTran String[]
     */
    public void setCas_st_long_tran(String[] casStLongTran) {
        this.cas_st_long_tran = casStLongTran == null ? null : (String[]) (casStLongTran.clone());
        if (null != casStLongTran) {
            String[] strings = new String[3];
            strings[0] = "cas_st_long_tran";
            strings[1] = casStLongTran[0];
            strings[2] = casStLongTran[1];
            list.add(strings);
        }
    }

    public String[] getCas_st_error_query() {
        return cas_st_error_query == null ? null : (String[]) (cas_st_error_query.clone());
    }
    /**
     * set cas_st_error_query
     *
     * @param casStErrorQuery String[]
     */
    public void setCas_st_error_query(String[] casStErrorQuery) {
        this.cas_st_error_query =
                casStErrorQuery == null ? null : (String[]) (casStErrorQuery.clone());
        if (null != casStErrorQuery) {
            String[] strings = new String[3];
            strings[0] = "cas_st_error_query";
            strings[1] = casStErrorQuery[0];
            strings[2] = casStErrorQuery[1];
            list.add(strings);
        }
    }

    public List<String[]> getList() {
        return list;
    }
}
