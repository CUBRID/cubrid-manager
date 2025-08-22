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

package com.cubrid.cubridmanager.core.monitoring.model;

import com.cubrid.common.core.util.LogUtil;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;

/**
 * A class that include all the target messages.
 *
 * <p>DiagStatusResult Description
 *
 * @author cn12978
 * @version 1.0 - 2009-5-14 created by cn12978
 */
public class DiagStatusResult {

    private static final Logger LOGGER = LogUtil.getLogger(DiagStatusResult.class);
    private String cas_mon_req;
    private String cas_mon_act_session;
    private String cas_mon_tran;
    private String cas_mon_query;
    private String cas_mon_long_query;
    private String cas_mon_long_tran;
    private String cas_mon_error_query;
    private String server_query_open_page;
    private String server_query_opened_page;
    private String server_query_slow_query;
    private String server_query_full_scan;
    private String server_conn_cli_request;
    private String server_conn_aborted_clients;
    private String server_conn_conn_req;
    private String server_conn_conn_reject;
    private String server_buffer_page_write;
    private String server_buffer_page_read;
    private String server_lock_deadlock;
    private String server_lock_request;

    private Map<String, String> diagStatusResultMap;

    /** Initialize the status result */
    public void initStatusResult() {
        cas_mon_req = "0";
        cas_mon_tran = "0";
        cas_mon_act_session = "0";
        cas_mon_query = "0";
        cas_mon_long_query = "0";
        cas_mon_long_tran = "0";
        cas_mon_error_query = "0";
        server_query_open_page = "0";
        server_query_opened_page = "0";
        server_query_slow_query = "0";
        server_query_full_scan = "0";
        server_conn_cli_request = "0";
        server_conn_aborted_clients = "0";
        server_conn_conn_req = "0";
        server_conn_conn_reject = "0";
        server_buffer_page_write = "0";
        server_buffer_page_read = "0";
        server_lock_deadlock = "0";
        server_lock_request = "0";
    }

    public DiagStatusResult() {
        cas_mon_req = "0";
        cas_mon_tran = "0";
        cas_mon_act_session = "0";
        cas_mon_query = "0";
        cas_mon_long_query = "0";
        cas_mon_long_tran = "0";
        cas_mon_error_query = "0";
        server_query_open_page = "0";
        server_query_opened_page = "0";
        server_query_slow_query = "0";
        server_query_full_scan = "0";
        server_conn_cli_request = "0";
        server_conn_aborted_clients = "0";
        server_conn_conn_req = "0";
        server_conn_conn_reject = "0";
        server_buffer_page_write = "0";
        server_buffer_page_read = "0";
        server_lock_deadlock = "0";
        server_lock_request = "0";

        diagStatusResultMap = new HashMap<String, String>();
    }

    public DiagStatusResult(DiagStatusResult clone) {
        cas_mon_req = clone.cas_mon_req;
        cas_mon_tran = clone.cas_mon_tran;
        cas_mon_act_session = clone.cas_mon_act_session;
        cas_mon_query = clone.cas_mon_query;
        cas_mon_long_query = clone.cas_mon_long_query;
        cas_mon_long_tran = clone.cas_mon_long_tran;
        cas_mon_error_query = clone.cas_mon_error_query;
        server_query_open_page = clone.server_query_open_page;
        server_query_opened_page = clone.server_query_opened_page;
        server_query_slow_query = clone.server_query_slow_query;
        server_query_full_scan = clone.server_query_full_scan;
        server_conn_cli_request = clone.server_conn_cli_request;
        server_conn_aborted_clients = clone.server_conn_aborted_clients;
        server_conn_conn_req = clone.server_conn_conn_req;
        server_conn_conn_reject = clone.server_conn_conn_reject;
        server_buffer_page_write = clone.server_buffer_page_write;
        server_buffer_page_read = clone.server_buffer_page_read;
        server_lock_deadlock = clone.server_lock_deadlock;
        server_lock_request = clone.server_lock_request;
    }

    /**
     * Get the clone value from the given object
     *
     * @param clone DiagStatusResult
     */
    public void copy_from(DiagStatusResult clone) {
        cas_mon_req = clone.cas_mon_req;
        cas_mon_tran = clone.cas_mon_tran;
        cas_mon_act_session = clone.cas_mon_act_session;
        cas_mon_query = clone.cas_mon_query;
        cas_mon_long_query = clone.cas_mon_long_query;
        cas_mon_long_tran = clone.cas_mon_long_tran;
        cas_mon_error_query = clone.cas_mon_error_query;
        server_query_open_page = clone.server_query_open_page;
        server_query_opened_page = clone.server_query_opened_page;
        server_query_slow_query = clone.server_query_slow_query;
        server_query_full_scan = clone.server_query_full_scan;
        server_conn_cli_request = clone.server_conn_cli_request;
        server_conn_aborted_clients = clone.server_conn_aborted_clients;
        server_conn_conn_req = clone.server_conn_conn_req;
        server_conn_conn_reject = clone.server_conn_conn_reject;
        server_buffer_page_write = clone.server_buffer_page_write;
        server_buffer_page_read = clone.server_buffer_page_read;
        server_lock_deadlock = clone.server_lock_deadlock;
        server_lock_request = clone.server_lock_request;
    }

    /**
     * Gets the delta by two bean of DiagStatusResult
     *
     * @param dsrA DiagStatusResult
     * @param dsrB DiagStatusResult
     */
    public void getDelta(DiagStatusResult dsrA, DiagStatusResult dsrB) {
        try {
            cas_mon_req =
                    String.valueOf(
                            Long.parseLong(dsrA.getCas_mon_req())
                                    - Long.parseLong(dsrB.getCas_mon_req()));
        } catch (NumberFormatException ee) {
            cas_mon_req = "0";
        }
        try {
            cas_mon_query =
                    String.valueOf(
                            Long.parseLong(dsrA.getCas_mon_query())
                                    - Long.parseLong(dsrB.getCas_mon_query()));

        } catch (NumberFormatException ee) {
            cas_mon_query = "0";
        }
        try {
            cas_mon_tran =
                    String.valueOf(
                            Long.parseLong(dsrA.getCas_mon_tran())
                                    - Long.parseLong(dsrB.getCas_mon_tran()));

        } catch (NumberFormatException ee) {
            cas_mon_tran = "0";
        }

        cas_mon_act_session = dsrA.cas_mon_act_session;

        try {
            cas_mon_long_query =
                    String.valueOf(
                            Long.parseLong(dsrA.cas_mon_long_query)
                                    - Long.parseLong(dsrB.cas_mon_long_query));

        } catch (NumberFormatException ee) {
            cas_mon_long_query = "0";
        }

        try {
            cas_mon_long_tran =
                    String.valueOf(
                            Long.parseLong(dsrA.cas_mon_long_tran)
                                    - Long.parseLong(dsrB.cas_mon_long_tran));

        } catch (NumberFormatException ee) {
            cas_mon_long_tran = "0";
        }

        try {
            cas_mon_error_query =
                    String.valueOf(
                            Long.parseLong(dsrA.cas_mon_error_query)
                                    - Long.parseLong(dsrB.cas_mon_error_query));

        } catch (NumberFormatException ee) {
            cas_mon_error_query = "0";
        }

        try {
            server_query_open_page =
                    String.valueOf(
                            Integer.parseInt(dsrA.server_query_open_page)
                                    - Integer.parseInt(dsrB.server_query_open_page));

        } catch (NumberFormatException ee) {
            server_query_open_page = "0";
        }
        try {
            server_query_opened_page =
                    String.valueOf(
                            Integer.parseInt(dsrA.server_query_opened_page)
                                    - Integer.parseInt(dsrB.server_query_opened_page));

        } catch (NumberFormatException ee) {
            server_query_opened_page = "0";
        }
        try {
            server_query_slow_query =
                    String.valueOf(
                            Integer.parseInt(dsrA.server_query_slow_query)
                                    - Integer.parseInt(dsrB.server_query_slow_query));

        } catch (NumberFormatException ee) {
            server_query_slow_query = "0";
        }
        try {
            server_query_full_scan =
                    String.valueOf(
                            Integer.parseInt(dsrA.server_query_full_scan)
                                    - Integer.parseInt(dsrB.server_query_full_scan));

        } catch (NumberFormatException ee) {
            server_query_full_scan = "0";
        }
        try {
            server_conn_cli_request =
                    String.valueOf(
                            Integer.parseInt(dsrA.server_conn_cli_request)
                                    - Integer.parseInt(dsrB.server_conn_cli_request));

        } catch (NumberFormatException ee) {
            server_conn_cli_request = "0";
        }
        try {
            server_conn_aborted_clients =
                    String.valueOf(
                            Integer.parseInt(dsrA.server_conn_aborted_clients)
                                    - Integer.parseInt(dsrB.server_conn_aborted_clients));

        } catch (NumberFormatException ee) {
            server_conn_aborted_clients = "0";
        }
        try {
            server_conn_conn_req =
                    String.valueOf(
                            Integer.parseInt(dsrA.server_conn_conn_req)
                                    - Integer.parseInt(dsrB.server_conn_conn_req));

        } catch (NumberFormatException ee) {
            server_conn_conn_req = "0";
        }
        try {
            server_conn_conn_reject =
                    String.valueOf(
                            Integer.parseInt(dsrA.server_conn_conn_reject)
                                    - Integer.parseInt(dsrB.server_conn_conn_reject));

        } catch (NumberFormatException ee) {
            server_conn_conn_reject = "0";
        }
        try {
            server_buffer_page_write =
                    String.valueOf(
                            Integer.parseInt(dsrA.server_buffer_page_write)
                                    - Integer.parseInt(dsrB.server_buffer_page_write));

        } catch (NumberFormatException ee) {
            server_buffer_page_write = "0";
        }
        try {
            server_buffer_page_read =
                    String.valueOf(
                            Integer.parseInt(dsrA.server_buffer_page_read)
                                    - Integer.parseInt(dsrB.server_buffer_page_read));

        } catch (NumberFormatException ee) {
            server_buffer_page_read = "0";
        }
        try {
            server_lock_deadlock =
                    String.valueOf(
                            Integer.parseInt(dsrA.server_lock_deadlock)
                                    - Integer.parseInt(dsrB.server_lock_deadlock));

        } catch (NumberFormatException ee) {
            server_lock_deadlock = "0";
        }
        try {
            server_lock_request =
                    String.valueOf(
                            Integer.parseInt(dsrA.server_lock_request)
                                    - Integer.parseInt(dsrB.server_lock_request));

        } catch (NumberFormatException ee) {
            server_lock_request = "0";
        }

        diagStatusResultMap.put("cas_mon_req", cas_mon_req);
        diagStatusResultMap.put("cas_mon_tran", cas_mon_tran);
        diagStatusResultMap.put("cas_mon_act_session", cas_mon_act_session);
        diagStatusResultMap.put("cas_mon_query", cas_mon_query);
        diagStatusResultMap.put("cas_mon_long_query", cas_mon_long_query);
        diagStatusResultMap.put("cas_mon_long_tran", cas_mon_long_tran);
        diagStatusResultMap.put("cas_mon_error_query", cas_mon_error_query);
        diagStatusResultMap.put("server_query_open_page", server_query_open_page);
        diagStatusResultMap.put("server_query_opened_page", server_query_opened_page);
        diagStatusResultMap.put("server_query_slow_query", server_query_slow_query);
        diagStatusResultMap.put("server_query_full_scan", server_query_full_scan);
        diagStatusResultMap.put("server_conn_cli_request", server_conn_cli_request);
        diagStatusResultMap.put("server_conn_aborted_clients", server_conn_aborted_clients);
        diagStatusResultMap.put("server_conn_conn_req", server_conn_conn_req);
        diagStatusResultMap.put("server_conn_conn_reject", server_conn_conn_reject);
        diagStatusResultMap.put("server_buffer_page_write", server_buffer_page_write);
        diagStatusResultMap.put("server_buffer_page_read", server_buffer_page_read);
        diagStatusResultMap.put("server_lock_deadlock", server_lock_deadlock);
        diagStatusResultMap.put("server_lock_request", server_lock_request);
    }

    /**
     * Gets the delta by three bean of DiagStatusResult
     *
     * @param dsrA DiagStatusResult
     * @param dsrB DiagStatusResult
     * @param dsrC DiagStatusResult
     */
    public void getDelta(DiagStatusResult dsrA, DiagStatusResult dsrB, DiagStatusResult dsrC) {
        cas_mon_req =
                getDeltaLong(
                        dsrA, "Cas_mon_req", dsrA.cas_mon_req, dsrB.cas_mon_req, dsrC.cas_mon_req);
        cas_mon_query =
                getDeltaLong(
                        dsrA,
                        "Cas_mon_query",
                        dsrA.cas_mon_query,
                        dsrB.cas_mon_query,
                        dsrC.cas_mon_query);
        cas_mon_tran =
                getDeltaLong(
                        dsrA,
                        "Cas_mon_tran",
                        dsrA.cas_mon_tran,
                        dsrB.cas_mon_tran,
                        dsrC.cas_mon_tran);

        cas_mon_act_session = dsrA.cas_mon_act_session;

        cas_mon_long_query =
                getDeltaLong(
                        dsrA,
                        "Cas_mon_long_query",
                        dsrA.cas_mon_long_query,
                        dsrB.cas_mon_long_query,
                        dsrC.cas_mon_long_query);
        cas_mon_long_tran =
                getDeltaLong(
                        dsrA,
                        "Cas_mon_long_tran",
                        dsrA.cas_mon_long_tran,
                        dsrB.cas_mon_long_tran,
                        dsrC.cas_mon_long_tran);
        cas_mon_error_query =
                getDeltaLong(
                        dsrA,
                        "Cas_mon_error_query",
                        dsrA.cas_mon_error_query,
                        dsrB.cas_mon_error_query,
                        dsrC.cas_mon_error_query);
        server_query_open_page =
                getDeltaInt(
                        dsrA,
                        "Server_query_open_page",
                        dsrA.server_query_open_page,
                        dsrB.server_query_open_page,
                        dsrC.server_query_open_page);
        server_query_opened_page =
                getDeltaInt(
                        dsrA,
                        "Server_query_opened_page",
                        dsrA.server_query_opened_page,
                        dsrB.server_query_opened_page,
                        dsrC.server_query_opened_page);
        server_query_slow_query =
                getDeltaInt(
                        dsrA,
                        "Server_query_slow_query",
                        dsrA.server_query_slow_query,
                        dsrB.server_query_slow_query,
                        dsrC.server_query_slow_query);
        server_query_full_scan =
                getDeltaInt(
                        dsrA,
                        "Server_query_full_scan",
                        dsrA.server_query_full_scan,
                        dsrB.server_query_full_scan,
                        dsrC.server_query_full_scan);
        server_conn_cli_request =
                getDeltaInt(
                        dsrA,
                        "Server_conn_cli_request",
                        dsrA.server_conn_cli_request,
                        dsrB.server_conn_cli_request,
                        dsrC.server_conn_cli_request);
        server_conn_aborted_clients =
                getDeltaInt(
                        dsrA,
                        "Server_conn_aborted_clients",
                        dsrA.server_conn_aborted_clients,
                        dsrB.server_conn_aborted_clients,
                        dsrC.server_conn_aborted_clients);
        server_conn_conn_req =
                getDeltaInt(
                        dsrA,
                        "Server_conn_conn_req",
                        dsrA.server_conn_conn_req,
                        dsrB.server_conn_conn_req,
                        dsrC.server_conn_conn_req);
        server_conn_conn_reject =
                getDeltaInt(
                        dsrA,
                        "Server_conn_conn_reject",
                        dsrA.server_conn_conn_reject,
                        dsrB.server_conn_conn_reject,
                        dsrC.server_conn_conn_reject);
        server_buffer_page_write =
                getDeltaInt(
                        dsrA,
                        "Server_buffer_page_write",
                        dsrA.server_buffer_page_write,
                        dsrB.server_buffer_page_write,
                        dsrC.server_buffer_page_write);
        server_buffer_page_read =
                getDeltaInt(
                        dsrA,
                        "Server_buffer_page_read",
                        dsrA.server_buffer_page_read,
                        dsrB.server_buffer_page_read,
                        dsrC.server_buffer_page_read);
        server_lock_deadlock =
                getDeltaInt(
                        dsrA,
                        "Server_lock_deadlock",
                        dsrA.server_lock_deadlock,
                        dsrB.server_lock_deadlock,
                        dsrC.server_lock_deadlock);
        server_lock_request =
                getDeltaInt(
                        dsrA,
                        "Server_lock_request",
                        dsrA.server_lock_request,
                        dsrB.server_lock_request,
                        dsrC.server_lock_request);

        diagStatusResultMap.put("cas_mon_req", cas_mon_req);
        diagStatusResultMap.put("cas_mon_tran", cas_mon_tran);
        diagStatusResultMap.put("cas_mon_act_session", cas_mon_act_session);
        diagStatusResultMap.put("cas_mon_query", cas_mon_query);
        diagStatusResultMap.put("cas_mon_long_query", cas_mon_long_query);
        diagStatusResultMap.put("cas_mon_long_tran", cas_mon_long_tran);
        diagStatusResultMap.put("cas_mon_error_query", cas_mon_error_query);
        diagStatusResultMap.put("server_query_open_page", server_query_open_page);
        diagStatusResultMap.put("server_query_opened_page", server_query_opened_page);
        diagStatusResultMap.put("server_query_slow_query", server_query_slow_query);
        diagStatusResultMap.put("server_query_full_scan", server_query_full_scan);
        diagStatusResultMap.put("server_conn_cli_request", server_conn_cli_request);
        diagStatusResultMap.put("server_conn_aborted_clients", server_conn_aborted_clients);
        diagStatusResultMap.put("server_conn_conn_req", server_conn_conn_req);
        diagStatusResultMap.put("server_conn_conn_reject", server_conn_conn_reject);
        diagStatusResultMap.put("server_buffer_page_write", server_buffer_page_write);
        diagStatusResultMap.put("server_buffer_page_read", server_buffer_page_read);
        diagStatusResultMap.put("server_lock_deadlock", server_lock_deadlock);
        diagStatusResultMap.put("server_lock_request", server_lock_request);
    }

    /**
     * Gets the delta by three bean of DiagStatusResult and the interval between getting the
     * instance of DiagStatusResult
     *
     * @param dsrA DiagStatusResult
     * @param dsrB DiagStatusResult
     * @param dsrC DiagStatusResult
     * @param inter float
     */
    public void getDelta(
            DiagStatusResult dsrA, DiagStatusResult dsrB, DiagStatusResult dsrC, float inter) {
        cas_mon_req =
                getDeltaLong(
                        dsrA,
                        "Cas_mon_req",
                        dsrA.cas_mon_req,
                        dsrB.cas_mon_req,
                        dsrC.cas_mon_req,
                        inter);
        cas_mon_query =
                getDeltaLong(
                        dsrA,
                        "Cas_mon_query",
                        dsrA.cas_mon_query,
                        dsrB.cas_mon_query,
                        dsrC.cas_mon_query,
                        inter);
        cas_mon_tran =
                getDeltaLong(
                        dsrA,
                        "Cas_mon_tran",
                        dsrA.cas_mon_tran,
                        dsrB.cas_mon_tran,
                        dsrC.cas_mon_tran,
                        inter);

        cas_mon_act_session = dsrA.cas_mon_act_session;

        cas_mon_long_query =
                getDeltaLong(
                        dsrA,
                        "Cas_mon_long_query",
                        dsrA.cas_mon_long_query,
                        dsrB.cas_mon_long_query,
                        dsrC.cas_mon_long_query,
                        inter);
        cas_mon_long_tran =
                getDeltaLong(
                        dsrA,
                        "Cas_mon_long_tran",
                        dsrA.cas_mon_long_tran,
                        dsrB.cas_mon_long_tran,
                        dsrC.cas_mon_long_tran,
                        inter);
        cas_mon_error_query =
                getDeltaLong(
                        dsrA,
                        "Cas_mon_error_query",
                        dsrA.cas_mon_error_query,
                        dsrB.cas_mon_error_query,
                        dsrC.cas_mon_error_query,
                        inter);
        server_query_open_page =
                getDeltaInt(
                        dsrA,
                        "Server_query_open_page",
                        dsrA.server_query_open_page,
                        dsrB.server_query_open_page,
                        dsrC.server_query_open_page,
                        inter);
        server_query_opened_page =
                getDeltaInt(
                        dsrA,
                        "Server_query_opened_page",
                        dsrA.server_query_opened_page,
                        dsrB.server_query_opened_page,
                        dsrC.server_query_opened_page,
                        inter);
        server_query_slow_query =
                getDeltaInt(
                        dsrA,
                        "Server_query_slow_query",
                        dsrA.server_query_slow_query,
                        dsrB.server_query_slow_query,
                        dsrC.server_query_slow_query,
                        inter);
        server_query_full_scan =
                getDeltaInt(
                        dsrA,
                        "Server_query_full_scan",
                        dsrA.server_query_full_scan,
                        dsrB.server_query_full_scan,
                        dsrC.server_query_full_scan,
                        inter);
        server_conn_cli_request =
                getDeltaInt(
                        dsrA,
                        "Server_conn_cli_request",
                        dsrA.server_conn_cli_request,
                        dsrB.server_conn_cli_request,
                        dsrC.server_conn_cli_request,
                        inter);
        server_conn_aborted_clients =
                getDeltaInt(
                        dsrA,
                        "Server_conn_aborted_clients",
                        dsrA.server_conn_aborted_clients,
                        dsrB.server_conn_aborted_clients,
                        dsrC.server_conn_aborted_clients,
                        inter);
        server_conn_conn_req =
                getDeltaInt(
                        dsrA,
                        "Server_conn_conn_req",
                        dsrA.server_conn_conn_req,
                        dsrB.server_conn_conn_req,
                        dsrC.server_conn_conn_req,
                        inter);
        server_conn_conn_reject =
                getDeltaInt(
                        dsrA,
                        "Server_conn_conn_reject",
                        dsrA.server_conn_conn_reject,
                        dsrB.server_conn_conn_reject,
                        dsrC.server_conn_conn_reject,
                        inter);
        server_buffer_page_write =
                getDeltaInt(
                        dsrA,
                        "Server_buffer_page_write",
                        dsrA.server_buffer_page_write,
                        dsrB.server_buffer_page_write,
                        dsrC.server_buffer_page_write,
                        inter);
        server_buffer_page_read =
                getDeltaInt(
                        dsrA,
                        "Server_buffer_page_read",
                        dsrA.server_buffer_page_read,
                        dsrB.server_buffer_page_read,
                        dsrC.server_buffer_page_read,
                        inter);
        server_lock_deadlock =
                getDeltaInt(
                        dsrA,
                        "Server_lock_deadlock",
                        dsrA.server_lock_deadlock,
                        dsrB.server_lock_deadlock,
                        dsrC.server_lock_deadlock,
                        inter);
        server_lock_request =
                getDeltaInt(
                        dsrA,
                        "Server_lock_request",
                        dsrA.server_lock_request,
                        dsrB.server_lock_request,
                        dsrC.server_lock_request,
                        inter);

        diagStatusResultMap.put("cas_mon_req", cas_mon_req);
        diagStatusResultMap.put("cas_mon_tran", cas_mon_tran);
        diagStatusResultMap.put("cas_mon_act_session", cas_mon_act_session);
        diagStatusResultMap.put("cas_mon_query", cas_mon_query);
        diagStatusResultMap.put("cas_mon_long_query", cas_mon_long_query);
        diagStatusResultMap.put("cas_mon_long_tran", cas_mon_long_tran);
        diagStatusResultMap.put("cas_mon_error_query", cas_mon_error_query);
        diagStatusResultMap.put("server_query_open_page", server_query_open_page);
        diagStatusResultMap.put("server_query_opened_page", server_query_opened_page);
        diagStatusResultMap.put("server_query_slow_query", server_query_slow_query);
        diagStatusResultMap.put("server_query_full_scan", server_query_full_scan);
        diagStatusResultMap.put("server_conn_cli_request", server_conn_cli_request);
        diagStatusResultMap.put("server_conn_aborted_clients", server_conn_aborted_clients);
        diagStatusResultMap.put("server_conn_conn_req", server_conn_conn_req);
        diagStatusResultMap.put("server_conn_conn_reject", server_conn_conn_reject);
        diagStatusResultMap.put("server_buffer_page_write", server_buffer_page_write);
        diagStatusResultMap.put("server_buffer_page_read", server_buffer_page_read);
        diagStatusResultMap.put("server_lock_deadlock", server_lock_deadlock);
        diagStatusResultMap.put("server_lock_request", server_lock_request);
    }

    public String getCas_mon_req() {
        return cas_mon_req;
    }

    public void setCas_mon_req(String casMonReq) {
        this.cas_mon_req = casMonReq;
    }

    public String getCas_mon_tran() {
        return cas_mon_tran;
    }

    public void setCas_mon_tran(String casMonRran) {
        this.cas_mon_tran = casMonRran;
    }

    public String getCas_mon_act_session() {
        return cas_mon_act_session;
    }

    public void setCas_mon_act_session(String casMonActSession) {
        this.cas_mon_act_session = casMonActSession;
    }

    public String getServer_query_open_page() {
        return server_query_open_page;
    }

    public void setServer_query_open_page(String serverQueryOpenPage) {
        this.server_query_open_page = serverQueryOpenPage;
    }

    public String getServer_query_opened_page() {
        return server_query_opened_page;
    }

    public void setServer_query_opened_page(String serverQueryOpenedPage) {
        this.server_query_opened_page = serverQueryOpenedPage;
    }

    public String getServer_query_slow_query() {
        return server_query_slow_query;
    }

    public void setServer_query_slow_query(String serverQuerySlowQuery) {
        this.server_query_slow_query = serverQuerySlowQuery;
    }

    public String getServer_query_full_scan() {
        return server_query_full_scan;
    }

    public void setServer_query_full_scan(String serverQueryFullScan) {
        this.server_query_full_scan = serverQueryFullScan;
    }

    public String getServer_conn_cli_request() {
        return server_conn_cli_request;
    }

    public void setServer_conn_cli_request(String serverConnCliRequest) {
        this.server_conn_cli_request = serverConnCliRequest;
    }

    public String getServer_conn_aborted_clients() {
        return server_conn_aborted_clients;
    }

    public void setServer_conn_aborted_clients(String serverConnAbortedClients) {
        this.server_conn_aborted_clients = serverConnAbortedClients;
    }

    public String getServer_conn_conn_req() {
        return server_conn_conn_req;
    }

    public void setServer_conn_conn_req(String serverConnConnReq) {
        this.server_conn_conn_req = serverConnConnReq;
    }

    public String getServer_conn_conn_reject() {
        return server_conn_conn_reject;
    }

    public void setServer_conn_conn_reject(String serverConnConnReject) {
        this.server_conn_conn_reject = serverConnConnReject;
    }

    public String getServer_buffer_page_write() {
        return server_buffer_page_write;
    }

    public void setServer_buffer_page_write(String serverBufferPageWrite) {
        this.server_buffer_page_write = serverBufferPageWrite;
    }

    public String getServer_buffer_page_read() {
        return server_buffer_page_read;
    }

    public void setServer_buffer_page_read(String serverBufferPageRead) {
        this.server_buffer_page_read = serverBufferPageRead;
    }

    public String getServer_lock_deadlock() {
        return server_lock_deadlock;
    }

    public void setServer_lock_deadlock(String serverLockDeadlock) {
        this.server_lock_deadlock = serverLockDeadlock;
    }

    public String getServer_lock_request() {
        return server_lock_request;
    }

    public void setServer_lock_request(String serverLockRequest) {
        this.server_lock_request = serverLockRequest;
    }

    public String getCas_mon_query() {
        return cas_mon_query;
    }

    public void setCas_mon_query(String casMonQuery) {
        this.cas_mon_query = casMonQuery;
    }

    public Map<String, String> getDiagStatusResultMap() {
        return diagStatusResultMap;
    }

    public String getCas_mon_long_query() {
        return cas_mon_long_query;
    }

    public void setCas_mon_long_query(String casMonLongQuery) {
        this.cas_mon_long_query = casMonLongQuery;
    }

    public String getCas_mon_long_tran() {
        return cas_mon_long_tran;
    }

    public void setCas_mon_long_tran(String casMonLongTran) {
        this.cas_mon_long_tran = casMonLongTran;
    }

    public String getCas_mon_error_query() {
        return cas_mon_error_query;
    }

    public void setCas_mon_error_query(String casMonErrorQuery) {
        this.cas_mon_error_query = casMonErrorQuery;
    }

    /**
     * Get the long value from given the strings which should be a field of a instance of the same
     * type
     *
     * @param fieldA the field of a object
     * @param fieldB the field of a object
     * @param fieldC the field of a object
     * @param object the object of this type
     * @param fieldName the field name but the initial character should be capital
     * @return a field of this object
     */
    private String getDeltaLong(
            DiagStatusResult object,
            String fieldName,
            String fieldA,
            String fieldB,
            String fieldC) {
        String result = "";
        try {
            if (Long.parseLong(fieldA) < 0 && Long.parseLong(fieldB) > 0) {
                long partA = Long.MAX_VALUE - Long.parseLong(fieldB);
                long partB = Long.parseLong(fieldA) - Long.MIN_VALUE;
                result = String.valueOf(partA + partB);
            } else {
                result = String.valueOf(Long.parseLong(fieldA) - Long.parseLong(fieldB));
                if (Long.parseLong(result) < 0) {
                    result = String.valueOf(Long.parseLong(fieldB) - Long.parseLong(fieldC));
                    long aValue = Long.parseLong(fieldB) + Long.parseLong(result);
                    Class<?> cc = DiagStatusResult.class;
                    Method mm = cc.getMethod("set" + fieldName, new Class[] {String.class});
                    mm.invoke(object, Long.toString(aValue));
                }
            }

        } catch (NumberFormatException ee) {
            result = "0";
        } catch (SecurityException ex) {
            LOGGER.error(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            LOGGER.error(ex.getMessage());
        } catch (IllegalAccessException ex) {
            LOGGER.error(ex.getMessage());
        } catch (NoSuchMethodException ex) {
            LOGGER.error(ex.getMessage());
        } catch (InvocationTargetException ex) {
            LOGGER.error(ex.getMessage());
        }
        return result;
    }

    /**
     * Get the long value from given the strings which should be a field of a instance of the same
     * type
     *
     * @param object a field of this object
     * @param fieldName the field name but the initial character should be capital
     * @param fieldA the field of a object
     * @param fieldB the field of a object
     * @param fieldC the field of a object
     * @param inter float
     * @return String
     */
    private String getDeltaLong(
            DiagStatusResult object,
            String fieldName,
            String fieldA,
            String fieldB,
            String fieldC,
            float inter) {
        String result = "";
        try {
            long temp = 0;
            if (Long.parseLong(fieldA) < 0 && Long.parseLong(fieldB) > 0) {
                long partA = Long.MAX_VALUE - Long.parseLong(fieldB);
                long partB = Long.parseLong(fieldA) - Long.MIN_VALUE;
                temp = partA + partB;
            } else {
                temp = Long.parseLong(fieldA) - Long.parseLong(fieldB);
                if (temp < 0) {
                    temp = Long.parseLong(fieldB) - Long.parseLong(fieldC);
                    long aValue = (Long.parseLong(fieldB) + temp);
                    Class<?> cc = DiagStatusResult.class;
                    Method mm = cc.getMethod("set" + fieldName, new Class[] {String.class});
                    mm.invoke(object, Long.toString(aValue));
                }
            }
            LOGGER.debug(fieldName + "(before divided by interval) = " + temp);
            temp = (long) (temp / inter);
            result = String.valueOf(temp);
            LOGGER.debug(fieldName + "(after divided by interval) = " + result);
        } catch (NumberFormatException ee) {
            result = "0";
        } catch (SecurityException ex) {
            LOGGER.error(ex.getMessage());
        } catch (NoSuchMethodException ex) {
            LOGGER.error(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            LOGGER.error(ex.getMessage());
        } catch (IllegalAccessException ex) {
            LOGGER.error(ex.getMessage());
        } catch (InvocationTargetException ex) {
            LOGGER.error(ex.getMessage());
        }
        return result;
    }

    /**
     * Get the int value from given the strings which should be a field of a instance of the same
     * type
     *
     * @param object the object of this type
     * @param fieldName the field name but the initial character should be capital
     * @param fieldA the field of a object
     * @param fieldB the field of a object
     * @param fieldC the field of a object
     * @return String
     */
    private String getDeltaInt(
            DiagStatusResult object,
            String fieldName,
            String fieldA,
            String fieldB,
            String fieldC) {
        String result = "";
        try {
            if (Integer.parseInt(fieldA) < 0 && Integer.parseInt(fieldB) > 0) {
                int partA = Integer.MAX_VALUE - Integer.parseInt(fieldB);
                int partB = Integer.parseInt(fieldA) - Integer.MIN_VALUE;
                result = String.valueOf(partA + partB);
            } else {
                result = String.valueOf(Integer.parseInt(fieldA) - Integer.parseInt(fieldB));
                if (Integer.parseInt(result) < 0) {
                    result = String.valueOf(Integer.parseInt(fieldB) - Integer.parseInt(fieldC));
                    int aValue = Integer.parseInt(fieldB) + Integer.parseInt(result);
                    Class<?> cc = DiagStatusResult.class;
                    Method mm = cc.getMethod("set" + fieldName, new Class[] {String.class});
                    mm.invoke(object, Integer.toString(aValue));
                }
            }
        } catch (NumberFormatException ee) {
            result = "0";
        } catch (SecurityException ex) {
            LOGGER.error(ex.getMessage());
        } catch (NoSuchMethodException ex) {
            LOGGER.error(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            LOGGER.error(ex.getMessage());
        } catch (IllegalAccessException ex) {
            LOGGER.error(ex.getMessage());
        } catch (InvocationTargetException ex) {
            LOGGER.error(ex.getMessage());
        }
        return result;
    }

    /**
     * Get the int value from given the strings which should be a field of a instance of the same
     * type
     *
     * @param object a field of this object
     * @param fieldName the field name but the initial character should be capital
     * @param fieldA the field of a object
     * @param fieldB the field of a object
     * @param fieldC the field of a object
     * @param inter float
     * @return String
     */
    private String getDeltaInt(
            DiagStatusResult object,
            String fieldName,
            String fieldA,
            String fieldB,
            String fieldC,
            float inter) {
        String result = "";
        try {
            int temp = 0;
            if (Integer.parseInt(fieldA) < 0 && Integer.parseInt(fieldB) > 0) {
                int partA = Integer.MAX_VALUE - Integer.parseInt(fieldB);
                int partB = Integer.parseInt(fieldA) - Integer.MIN_VALUE;
                temp = partA + partB;
            } else {
                temp = Integer.parseInt(fieldA) - Integer.parseInt(fieldB);
                if (temp < 0) {
                    temp = Integer.parseInt(fieldB) - Integer.parseInt(fieldC);
                    int aValue = (Integer.parseInt(fieldB) + temp);
                    Class<?> cc = DiagStatusResult.class;
                    Method mm = cc.getMethod("set" + fieldName, new Class[] {String.class});
                    mm.invoke(object, Integer.toString(aValue));
                }
            }
            LOGGER.debug(fieldName + "(before divided by interval) = " + temp);
            temp = (int) (temp / inter);
            result = String.valueOf(temp);
            LOGGER.debug(fieldName + "(after divided by interval) = " + result);
        } catch (NumberFormatException ee) {
            result = "0";
        } catch (SecurityException ex) {
            LOGGER.error(ex.getMessage());
        } catch (NoSuchMethodException ex) {
            LOGGER.error(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            LOGGER.error(ex.getMessage());
        } catch (IllegalAccessException ex) {
            LOGGER.error(ex.getMessage());
        } catch (InvocationTargetException ex) {
            LOGGER.error(ex.getMessage());
        }
        return result;
    }
}
