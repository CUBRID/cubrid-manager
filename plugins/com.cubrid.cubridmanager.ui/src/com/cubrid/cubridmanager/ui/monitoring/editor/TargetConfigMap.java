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
package com.cubrid.cubridmanager.ui.monitoring.editor;

import java.util.EnumMap;

/**
 * Stores all the target configuration info
 *
 * @author lizhiqiang
 * @version 1.0 - 2009-5-7 created by lizhiqiang
 */
public final class TargetConfigMap {
    private static final String DB_CATEGORY = "database";
    private static final String CAS_CATEGORY = "broker";
    private static final String QUERY_CATEGORY = "server_query";
    private static final String CONN_CATEGORY = "server_connection";
    private static final String BUFFER_CATEGORY = "server_buffer";
    private static final String LOCK_CATEGORY = "server_lock";
    private static final String BROKER_CATEGORY = "broker";
    private static final String SHOE_OPENED_PAGE = "opened_page";
    private static final String SHOW_SLOW_QUERY = "slow_query";
    private static final String SHOW_FULL_SCAN = "full_scan";
    private static final String SHOW_CLI_REQUEST = "client_request";
    private static final String SHOW_ABOUTED_CLIENTS = "aborted_clients";
    private static final String SHOW_CONN_REQ = "conn_request";
    private static final String SHOW_CONN_REJ = "conn_rejected";
    private static final String SHOW_PAGE_WRITE = "buffer_page_write";
    private static final String SHOW_PAGE_READ = "buffer_page_read";
    private static final String SHOW_LOCK_DEAD_LOCK = "deadlock";
    private static final String SHOW_LOCK_REQUEST = "lock_request";
    private static final String SHOW_ST_REQUEST = "request per second"; // "Requests/Sec";
    private static final String SHOW_ST_TRANSATION =
            "transaction per second"; // "Transactions/Sec";
    private static final String SHOW_ST_ACTIVE_SESSION = "active session count";
    private static final String SHOW_ST_QUERY = "query per second"; // "Queries/Sec"
    private static final String SHOW_ST_LONG_QUERY = "long query count";
    private static final String SHOW_ST_LONG_TRAN = "long transaction count";
    private static final String SHOW_ST_ERR_QUERY = "error query count";

    private final EnumMap<EnumTargetConfig, TargetConfig> enumMap;
    private static TargetConfigMap instance = null;

    /** The Constructor */
    private TargetConfigMap() {
        enumMap = new EnumMap<EnumTargetConfig, TargetConfig>(EnumTargetConfig.class);
        init();
    }

    /** Initializes the fields */
    private void init() {

        TargetConfig openPage = new TargetConfig();
        openPage.setName("server_query_open_page");
        openPage.setTransName(openPage.getName());
        openPage.setDisplayName(SHOE_OPENED_PAGE);
        openPage.setTopCategory(DB_CATEGORY);
        openPage.setCategory(QUERY_CATEGORY);
        openPage.setMonitorName("mon_cub_query_open_page");
        openPage.setMagnification(1);
        openPage.setChartTitle("DB Open Page");
        enumMap.put(EnumTargetConfig.SERVER_QUERY_OPEN_PAGE, openPage);

        TargetConfig openedPage = new TargetConfig();
        openedPage.setName("server_query_opened_page");
        openedPage.setTransName(openedPage.getName());
        openedPage.setDisplayName(SHOE_OPENED_PAGE);
        openedPage.setTopCategory(DB_CATEGORY);
        openedPage.setCategory(QUERY_CATEGORY);
        openedPage.setMonitorName("mon_cub_query_opened_page");
        openedPage.setMagnification(1);
        openedPage.setChartTitle("DB Opened Page");
        enumMap.put(EnumTargetConfig.SERVER_QUERY_OPENED_PAGE, openedPage);

        TargetConfig slowQuery = new TargetConfig();
        slowQuery.setName("server_query_slow_query");
        slowQuery.setTransName(slowQuery.getName());
        slowQuery.setDisplayName(SHOW_SLOW_QUERY);
        slowQuery.setTopCategory(DB_CATEGORY);
        slowQuery.setCategory(QUERY_CATEGORY);
        slowQuery.setMonitorName("mon_cub_query_full_scan");
        slowQuery.setMagnification(1);
        slowQuery.setChartTitle("DB Slow Query");
        enumMap.put(EnumTargetConfig.SERVER_QUERY_SLOW_QUERY, slowQuery);

        TargetConfig fullScan = new TargetConfig();
        fullScan.setName("server_query_full_scan");
        fullScan.setTransName(fullScan.getName());
        fullScan.setDisplayName(SHOW_FULL_SCAN);
        fullScan.setTopCategory(DB_CATEGORY);
        fullScan.setCategory(QUERY_CATEGORY);
        fullScan.setMonitorName("mon_cub_query_full_scan");
        fullScan.setMagnification(1);
        fullScan.setChartTitle("DB Full Scan");
        enumMap.put(EnumTargetConfig.SERVER_QUERY_FULL_SCAN, fullScan);

        TargetConfig abortedClients = new TargetConfig();
        abortedClients.setName("server_conn_aborted_clients");
        abortedClients.setTransName(abortedClients.getName());
        abortedClients.setDisplayName(SHOW_ABOUTED_CLIENTS);
        abortedClients.setTopCategory(DB_CATEGORY);
        abortedClients.setCategory(CONN_CATEGORY);
        abortedClients.setMonitorName("mon_cub_conn_aborted_clients");
        abortedClients.setMagnification(1);
        abortedClients.setChartTitle("DB Aborted Clients");
        enumMap.put(EnumTargetConfig.SERVER_CONN_ABORTED_CLIENTS, abortedClients);

        TargetConfig clientRequest = new TargetConfig();
        clientRequest.setName("server_conn_cli_request");
        clientRequest.setTransName(clientRequest.getName());
        clientRequest.setDisplayName(SHOW_CLI_REQUEST);
        clientRequest.setTopCategory(DB_CATEGORY);
        clientRequest.setCategory(CONN_CATEGORY);
        clientRequest.setMonitorName("mon_cub_conn_cli_request");
        clientRequest.setMagnification(1);
        clientRequest.setChartTitle("DB Client Request");
        enumMap.put(EnumTargetConfig.SERVER_CONN_CLI_REQUEST, clientRequest);

        TargetConfig connReq = new TargetConfig();
        connReq.setName("server_conn_conn_req");
        connReq.setTransName(connReq.getName());
        connReq.setDisplayName(SHOW_CONN_REQ);
        connReq.setTopCategory(DB_CATEGORY);
        connReq.setCategory(CONN_CATEGORY);
        connReq.setMonitorName("mon_cub_conn_conn_req");
        connReq.setMagnification(1);
        connReq.setChartTitle("DB Connection Request");
        enumMap.put(EnumTargetConfig.SERVER_CONN_CONN_REQ, connReq);

        TargetConfig connRejected = new TargetConfig();
        connRejected.setName("server_conn_conn_reject");
        connRejected.setTransName(connRejected.getName());
        connRejected.setDisplayName(SHOW_CONN_REJ);
        connRejected.setTopCategory(DB_CATEGORY);
        connRejected.setCategory(CONN_CATEGORY);
        connRejected.setMonitorName("mon_cub_conn_conn_reject");
        connRejected.setMagnification(1);
        connRejected.setChartTitle("DB Connection Reject");
        enumMap.put(EnumTargetConfig.SERVER_CONN_CONN_REJ, connRejected);

        TargetConfig pageWrite = new TargetConfig();
        pageWrite.setName("server_buffer_page_write");
        pageWrite.setTransName(pageWrite.getName());
        pageWrite.setDisplayName(SHOW_PAGE_WRITE);
        pageWrite.setTopCategory(DB_CATEGORY);
        pageWrite.setCategory(BUFFER_CATEGORY);
        pageWrite.setMonitorName("mon_cub_buffer_page_write");
        pageWrite.setMagnification(1);
        pageWrite.setChartTitle("DB Buffer Write");
        enumMap.put(EnumTargetConfig.SERVER_BUFFER_PAGE_WRITE, pageWrite);

        TargetConfig pageRead = new TargetConfig();
        pageRead.setName("server_buffer_page_read");
        pageRead.setTransName(pageRead.getName());
        pageRead.setDisplayName(SHOW_PAGE_READ);
        pageRead.setTopCategory(DB_CATEGORY);
        pageRead.setCategory(BUFFER_CATEGORY);
        pageRead.setMonitorName("mon_cub_buffer_page_read");
        pageRead.setMagnification(1);
        pageRead.setChartTitle("DB Buffer Read");
        enumMap.put(EnumTargetConfig.SERVER_BUFFER_PAGE_READ, pageRead);

        TargetConfig deadlock = new TargetConfig();
        deadlock.setName("server_lock_deadlock");
        deadlock.setTransName(deadlock.getName());
        deadlock.setDisplayName(SHOW_LOCK_DEAD_LOCK);
        deadlock.setTopCategory(DB_CATEGORY);
        deadlock.setCategory(LOCK_CATEGORY);
        deadlock.setMonitorName("mon_cub_lock_deadlock");
        deadlock.setMagnification(1);
        deadlock.setChartTitle("DB Deadlock");
        enumMap.put(EnumTargetConfig.SERVER_LOCK_DEADLOCK, deadlock);

        TargetConfig lockRequest = new TargetConfig();
        lockRequest.setName("server_lock_request");
        lockRequest.setTransName(pageWrite.getName());
        lockRequest.setDisplayName(SHOW_LOCK_REQUEST);
        lockRequest.setTopCategory(DB_CATEGORY);
        lockRequest.setCategory(LOCK_CATEGORY);
        lockRequest.setMonitorName("mon_cub_lock_request");
        lockRequest.setMagnification(1);
        lockRequest.setChartTitle("DB Lock Request");
        enumMap.put(EnumTargetConfig.SERVER_LOCK_REQUEST, lockRequest);

        TargetConfig casRequestSec = new TargetConfig();
        casRequestSec.setName("cas_st_request"); // cas_request_sec
        casRequestSec.setTransName(casRequestSec.getName());
        casRequestSec.setDisplayName(SHOW_ST_REQUEST);
        casRequestSec.setTopCategory(CAS_CATEGORY);
        casRequestSec.setCategory(BROKER_CATEGORY);
        casRequestSec.setMonitorName("cas_mon_req");
        casRequestSec.setMagnification(1);
        casRequestSec.setChartTitle("Broker Request per Second");
        enumMap.put(EnumTargetConfig.CAS_ST_REQUEST, casRequestSec);

        TargetConfig casActiveSession = new TargetConfig();
        casActiveSession.setName("cas_st_active_session"); // cas_active_session
        casActiveSession.setTransName(casActiveSession.getName());
        casActiveSession.setDisplayName(SHOW_ST_ACTIVE_SESSION);
        casActiveSession.setTopCategory(CAS_CATEGORY);
        casActiveSession.setCategory(BROKER_CATEGORY);
        casActiveSession.setMonitorName("cas_mon_act_session");
        casActiveSession.setMagnification(1);
        casActiveSession.setChartTitle("Broker Active Session Count");
        enumMap.put(EnumTargetConfig.CAS_ST_ACTIVE_SESSION, casActiveSession);

        TargetConfig casTransactionSec = new TargetConfig();
        casTransactionSec.setName("cas_st_transaction"); // cas_transaction_sec
        casTransactionSec.setTransName(casTransactionSec.getName());
        casTransactionSec.setDisplayName(SHOW_ST_TRANSATION);
        casTransactionSec.setTopCategory(CAS_CATEGORY);
        casTransactionSec.setCategory(BROKER_CATEGORY);
        casTransactionSec.setMonitorName("cas_mon_tran");
        casTransactionSec.setMagnification(1);
        casTransactionSec.setChartTitle("Broker Transaction per Second");
        enumMap.put(EnumTargetConfig.CAS_ST_TRANSACTION, casTransactionSec);

        TargetConfig casQuerySec = new TargetConfig();
        casQuerySec.setName("cas_st_query"); // cas_query_sec
        casQuerySec.setTransName(casQuerySec.getName());
        casQuerySec.setDisplayName(SHOW_ST_QUERY);
        casQuerySec.setTopCategory(CAS_CATEGORY);
        casQuerySec.setCategory(BROKER_CATEGORY);
        casQuerySec.setMonitorName("cas_mon_query");
        casQuerySec.setMagnification(1);
        casQuerySec.setChartTitle("Broker Query per Second");
        enumMap.put(EnumTargetConfig.CAS_ST_QUERY, casQuerySec);

        TargetConfig casStLongQuery = new TargetConfig();
        casStLongQuery.setName("cas_st_long_query"); // cas_st_long_query
        casStLongQuery.setTransName(casStLongQuery.getName());
        casStLongQuery.setDisplayName(SHOW_ST_LONG_QUERY);
        casStLongQuery.setTopCategory(CAS_CATEGORY);
        casStLongQuery.setCategory(BROKER_CATEGORY);
        casStLongQuery.setMonitorName("cas_mon_long_query");
        casStLongQuery.setMagnification(1);
        casStLongQuery.setChartTitle("Broker Long Query Count");
        enumMap.put(EnumTargetConfig.CAS_ST_LONG_QUERY, casStLongQuery);

        TargetConfig casStLongTran = new TargetConfig();
        casStLongTran.setName("cas_st_long_tran"); // cas_st_long_tran
        casStLongTran.setTransName(casStLongTran.getName());
        casStLongTran.setDisplayName(SHOW_ST_LONG_TRAN);
        casStLongTran.setTopCategory(CAS_CATEGORY);
        casStLongTran.setCategory(BROKER_CATEGORY);
        casStLongTran.setMonitorName("cas_mon_long_tran");
        casStLongTran.setMagnification(1);
        casStLongTran.setChartTitle("Broker Long Transaction Count");
        enumMap.put(EnumTargetConfig.CAS_ST_LONG_TRAN, casStLongTran);

        TargetConfig casStErrorQuery = new TargetConfig();
        casStErrorQuery.setName("cas_st_error_query"); // cas_st_error_query
        casStErrorQuery.setTransName(casStErrorQuery.getName());
        casStErrorQuery.setDisplayName(SHOW_ST_ERR_QUERY);
        casStErrorQuery.setTopCategory(CAS_CATEGORY);
        casStErrorQuery.setCategory(BROKER_CATEGORY);
        casStErrorQuery.setMonitorName("cas_mon_error_query");
        casStErrorQuery.setMagnification(1);
        casStErrorQuery.setChartTitle("Broker Error Query Count");
        enumMap.put(EnumTargetConfig.CAS_ST_ERROR_QUERY, casStErrorQuery);
    }

    /**
     * Gets the instance of TargetConfigMap
     *
     * @return TargetConfigMap the instance of this object
     */
    public static TargetConfigMap getInstance() {
        synchronized (TargetConfigMap.class) {
            if (instance == null) {
                instance = new TargetConfigMap();
            }
        }
        return instance;
    }

    /**
     * Gets the value of enumMap
     *
     * @return EnumMap<EnumTargetConfig, TargetConfig>
     */
    public EnumMap<EnumTargetConfig, TargetConfig> getMap() {
        return enumMap;
    }

    /**
     * Gets the value of dbCategory
     *
     * @return DB_CATEGORY
     */
    public String getDbCategory() {
        return DB_CATEGORY;
    }

    /**
     * Gets the value of casCategory
     *
     * @return CAS_CATEGORY
     */
    public String getCasCategory() {
        return CAS_CATEGORY;
    }

    /**
     * Gets the value of queryCategory
     *
     * @return QUERY_CATEGORY
     */
    public String getQueryCategory() {
        return QUERY_CATEGORY;
    }

    /**
     * Gets the value of connCategory
     *
     * @return CONN_CATEGORY
     */
    public String getConnCategory() {
        return CONN_CATEGORY;
    }

    /**
     * Gets the value of bufferCategory
     *
     * @return BUFFER_CATEGORY
     */
    public String getBufferCategory() {
        return BUFFER_CATEGORY;
    }

    /**
     * Gets the value of lockCategory
     *
     * @return LOCK_CATEGORY
     */
    public String getLockCategory() {
        return LOCK_CATEGORY;
    }

    /**
     * Gets the value of brokerCategory
     *
     * @return BROKER_CATEGORY
     */
    public String getBrokerCategory() {
        return BROKER_CATEGORY;
    }

    /**
     * Gets the value of showOpenedPage
     *
     * @return SHOE_OPENED_PAGE
     */
    public String getShowOpenedPage() {
        return SHOE_OPENED_PAGE;
    }

    /**
     * Gets the value of showSlowQuery
     *
     * @return SHOW_SLOW_QUERY
     */
    public String getShowSlowQuery() {
        return SHOW_SLOW_QUERY;
    }

    /**
     * Gets the value of showFullScan
     *
     * @return SHOW_FULL_SCAN
     */
    public String getShowFullScan() {
        return SHOW_FULL_SCAN;
    }

    /**
     * Gets the value of showCliRequest
     *
     * @return SHOW_CLI_REQUEST
     */
    public String getShowCliRequest() {
        return SHOW_CLI_REQUEST;
    }

    /**
     * Gets the value of showAboutedClients
     *
     * @return SHOW_ABOUTED_CLIENTS
     */
    public String getShowAboutedClients() {
        return SHOW_ABOUTED_CLIENTS;
    }

    /**
     * Gets the value of showConnReq
     *
     * @return SHOW_CONN_REQ
     */
    public String getShowConnReq() {
        return SHOW_CONN_REQ;
    }

    /**
     * Gets the value of showConnRej
     *
     * @return SHOW_CONN_REJ
     */
    public String getShowConnRej() {
        return SHOW_CONN_REJ;
    }

    /**
     * Gets the value of showPageWrite
     *
     * @return SHOW_PAGE_WRITE
     */
    public String getShowPageWrite() {
        return SHOW_PAGE_WRITE;
    }

    /**
     * Gets the value of showPageRead
     *
     * @return SHOW_PAGE_READ
     */
    public String getShowPageRead() {
        return SHOW_PAGE_READ;
    }

    /**
     * Gets the value of showLockDeadlock
     *
     * @return SHOW_LOCK_DEAD_LOCK
     */
    public String getShowLockDeadlock() {
        return SHOW_LOCK_DEAD_LOCK;
    }

    /**
     * Gets the value of showLockRequest
     *
     * @return SHOW_LOCK_REQUEST
     */
    public String getShowLockRequest() {
        return SHOW_LOCK_REQUEST;
    }

    /**
     * Gets the value of showStRequest
     *
     * @return SHOW_ST_REQUEST
     */
    public String getShowStRequest() {
        return SHOW_ST_REQUEST;
    }

    /**
     * Gets the value of showStTransaction
     *
     * @return SHOW_ST_TRANSATION
     */
    public String getShowStTransaction() {
        return SHOW_ST_TRANSATION;
    }

    /**
     * Gets the value of showStActiveSession
     *
     * @return SHOW_ST_ACTIVE_SESSION
     */
    public String getShowStActiveSession() {
        return SHOW_ST_ACTIVE_SESSION;
    }

    /**
     * Gets the value of showStQuery
     *
     * @return SHOW_ST_QUERY
     */
    public String getShowStQuery() {
        return SHOW_ST_QUERY;
    }

    /**
     * Gets the value of showStLongQuery
     *
     * @return SHOW_ST_LONG_QUERY
     */
    public String getShowStLongQuery() {
        return SHOW_ST_LONG_QUERY;
    }

    /**
     * Gets the value of showStLongTran
     *
     * @return SHOW_ST_LONG_TRAN
     */
    public String getShowStLongTran() {
        return SHOW_ST_LONG_TRAN;
    }

    /**
     * Gets the value of showStErrQuery
     *
     * @return SHOW_ST_ERR_QUERY
     */
    public String getShowStErrQuery() {
        return SHOW_ST_ERR_QUERY;
    }
}
