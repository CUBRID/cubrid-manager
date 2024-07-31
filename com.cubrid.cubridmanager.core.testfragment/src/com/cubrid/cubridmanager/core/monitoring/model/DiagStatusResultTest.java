/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.cubridmanager.core.monitoring.model;

import java.util.Map;
import junit.framework.TestCase;

/**
 * Test DiagStatusResultTest Description
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-1-4 created by lizhiqiang
 */
public class DiagStatusResultTest extends TestCase {
    private DiagStatusResult bean;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        bean = new DiagStatusResult();
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult#initStatusResult()} .
     */
    public void testInitStatusResult() {
        bean.initStatusResult();
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult#copy_from(com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult)}
     * .
     */
    public void testCopy_from() {
        DiagStatusResult bean_2 = new DiagStatusResult();
        bean_2.setCas_mon_req("bean_2.cas_mon_req");
        bean.copy_from(bean_2);
        assertNotSame(bean, bean_2);
        assertEquals(bean.getCas_mon_req(), "bean_2.cas_mon_req");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult#getDelta(com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult,
     * com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult)} .
     */
    public void testGetDeltaDiagStatusResultDiagStatusResult() {
        DiagStatusResult bean_1 = new DiagStatusResult();
        DiagStatusResult bean_2 = new DiagStatusResult();

        bean_1.setCas_mon_req("54");
        bean_2.setCas_mon_req("53");

        bean_1.setCas_mon_query("54");
        bean_2.setCas_mon_query("53");

        bean_1.setCas_mon_tran("54");
        bean_2.setCas_mon_tran("53");

        bean_1.setCas_mon_long_tran("54");
        bean_2.setCas_mon_long_tran("53");

        bean_1.setCas_mon_long_query("54");
        bean_2.setCas_mon_long_query("53");

        bean_1.setCas_mon_error_query("54");
        bean_2.setCas_mon_error_query("53");

        bean_1.setCas_mon_act_session("54");
        bean_2.setCas_mon_act_session("53");

        bean_1.setServer_buffer_page_read("54");
        bean_2.setServer_buffer_page_read("53");

        bean_1.setServer_buffer_page_write("55");
        bean_2.setServer_buffer_page_write("54");

        bean_1.setServer_conn_aborted_clients("55");
        bean_2.setServer_conn_aborted_clients("54");

        bean_1.setServer_conn_cli_request("55");
        bean_2.setServer_conn_cli_request("54");

        bean_1.setServer_conn_conn_reject("55");
        bean_2.setServer_conn_conn_reject("54");

        bean_1.setServer_conn_conn_req("55");
        bean_2.setServer_conn_conn_req("54");

        bean_1.setServer_lock_deadlock("55");
        bean_2.setServer_lock_deadlock("54");

        bean_1.setServer_query_full_scan("55");
        bean_2.setServer_query_full_scan("54");

        bean_1.setServer_query_open_page("55");
        bean_2.setServer_query_open_page("54");

        bean_1.setServer_query_opened_page("55");
        bean_2.setServer_query_opened_page("54");

        bean_1.setServer_query_slow_query("55");
        bean_2.setServer_query_slow_query("54");

        bean_1.setServer_lock_request("51");
        bean_2.setServer_lock_request("54");

        bean.getDelta(bean_1, bean_2);
        assertEquals(bean.getCas_mon_req(), "1");
        assertEquals(bean.getCas_mon_query(), "1");
        assertEquals(bean.getCas_mon_tran(), "1");
        assertEquals(bean.getCas_mon_long_tran(), "1");
        assertEquals(bean.getCas_mon_long_query(), "1");
        assertEquals(bean.getCas_mon_error_query(), "1");
        assertEquals(bean.getCas_mon_act_session(), "54");
        assertEquals(bean.getServer_buffer_page_read(), "1");
        assertEquals(bean.getServer_buffer_page_write(), "1");
        assertEquals(bean.getServer_conn_aborted_clients(), "1");
        assertEquals(bean.getServer_conn_cli_request(), "1");
        assertEquals(bean.getServer_conn_conn_req(), "1");
        assertEquals(bean.getServer_lock_deadlock(), "1");
        assertEquals(bean.getServer_query_full_scan(), "1");
        assertEquals(bean.getServer_query_open_page(), "1");
        assertEquals(bean.getServer_query_opened_page(), "1");
        assertEquals(bean.getServer_query_slow_query(), "1");

        bean_1.setCas_mon_req("54");
        bean_2.setCas_mon_req("a");

        bean_1.setCas_mon_query("54");
        bean_2.setCas_mon_query("a");

        bean_1.setCas_mon_tran("54");
        bean_2.setCas_mon_tran("a");

        bean_1.setCas_mon_long_tran("54");
        bean_2.setCas_mon_long_tran("a");

        bean_1.setCas_mon_long_query("54");
        bean_2.setCas_mon_long_query("a");

        bean_1.setCas_mon_error_query("54");
        bean_2.setCas_mon_error_query("a");

        bean_1.setCas_mon_act_session("54");
        bean_2.setCas_mon_act_session("a");

        bean_1.setServer_buffer_page_read("54");
        bean_2.setServer_buffer_page_read("a");

        bean_1.setServer_buffer_page_write("55");
        bean_2.setServer_buffer_page_write("a");

        bean_1.setServer_conn_aborted_clients("55");
        bean_2.setServer_conn_aborted_clients("a");

        bean_1.setServer_conn_cli_request("55");
        bean_2.setServer_conn_cli_request("a");

        bean_1.setServer_conn_conn_reject("55");
        bean_2.setServer_conn_conn_reject("a");

        bean_1.setServer_conn_conn_req("55");
        bean_2.setServer_conn_conn_req("a");

        bean_1.setServer_lock_deadlock("55");
        bean_2.setServer_lock_deadlock("a");

        bean_1.setServer_query_full_scan("55");
        bean_2.setServer_query_full_scan("a");

        bean_1.setServer_query_open_page("55");
        bean_2.setServer_query_open_page("a");

        bean_1.setServer_query_opened_page("55");
        bean_2.setServer_query_opened_page("a");

        bean_1.setServer_query_slow_query("a");
        bean_2.setServer_query_slow_query("54");

        bean_1.setServer_lock_request("a");
        bean_2.setServer_lock_request("54");
        bean.getDelta(bean_1, bean_2);
        assertEquals(bean.getCas_mon_req(), "0");
        assertEquals(bean.getCas_mon_query(), "0");
        assertEquals(bean.getCas_mon_tran(), "0");
        assertEquals(bean.getCas_mon_long_tran(), "0");
        assertEquals(bean.getCas_mon_long_query(), "0");
        assertEquals(bean.getCas_mon_error_query(), "0");
        assertEquals(bean.getCas_mon_act_session(), "54");
        assertEquals(bean.getServer_buffer_page_read(), "0");
        assertEquals(bean.getServer_buffer_page_write(), "0");
        assertEquals(bean.getServer_conn_aborted_clients(), "0");
        assertEquals(bean.getServer_conn_cli_request(), "0");
        assertEquals(bean.getServer_conn_conn_req(), "0");
        assertEquals(bean.getServer_lock_deadlock(), "0");
        assertEquals(bean.getServer_query_full_scan(), "0");
        assertEquals(bean.getServer_query_open_page(), "0");
        assertEquals(bean.getServer_query_opened_page(), "0");
        assertEquals(bean.getServer_query_slow_query(), "0");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult#getDelta(com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult,
     * com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult,
     * com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult)} .
     */
    public void testGetDeltaDiagStatusResultDiagStatusResultDiagStatusResult() {
        DiagStatusResult bean_1 = new DiagStatusResult();
        DiagStatusResult bean_2 = new DiagStatusResult();
        DiagStatusResult bean_3 = new DiagStatusResult();

        bean_1.setCas_mon_req("54");
        bean_2.setCas_mon_req("53");
        bean_3.setCas_mon_req("52");

        bean_1.setCas_mon_query("51");
        bean_2.setCas_mon_query("53");
        bean_3.setCas_mon_query("52");

        long a = Long.MIN_VALUE + 1;
        long b = Long.MAX_VALUE - 1;
        bean_1.setCas_mon_tran(Long.toString(a));
        bean_2.setCas_mon_tran(Long.toString(b));
        bean_3.setCas_mon_tran("52");

        bean_1.setCas_mon_long_tran("51");
        bean_2.setCas_mon_long_tran("53");
        bean_3.setCas_mon_long_tran("52");

        bean_1.setCas_mon_long_query("51");
        bean_2.setCas_mon_long_query("53");
        bean_3.setCas_mon_long_query("52");

        bean_1.setCas_mon_error_query("51");
        bean_2.setCas_mon_error_query("53");
        bean_3.setCas_mon_error_query("52");

        bean_1.setCas_mon_act_session("51");
        bean_2.setCas_mon_act_session("53");
        bean_3.setCas_mon_act_session("52");

        bean_1.setServer_buffer_page_read("51");
        bean_2.setServer_buffer_page_read("53");
        bean_3.setServer_buffer_page_read("52");

        bean_1.setServer_buffer_page_write("51");
        bean_2.setServer_buffer_page_write("54");
        bean_3.setServer_buffer_page_write("53");

        bean_1.setServer_conn_aborted_clients("51");
        bean_2.setServer_conn_aborted_clients("54");
        bean_3.setServer_conn_aborted_clients("53");

        bean_1.setServer_conn_cli_request("51");
        bean_2.setServer_conn_cli_request("54");
        bean_3.setServer_conn_cli_request("53");

        bean_1.setServer_conn_conn_reject("55");
        bean_2.setServer_conn_conn_reject("54");
        bean_3.setServer_conn_conn_reject("53");

        bean_1.setServer_conn_conn_req("51");
        bean_2.setServer_conn_conn_req("54");
        bean_3.setServer_conn_conn_req("53");

        bean_1.setServer_lock_deadlock("51");
        bean_2.setServer_lock_deadlock("54");
        bean_3.setServer_lock_deadlock("53");

        bean_1.setServer_query_full_scan("51");
        bean_2.setServer_query_full_scan("54");
        bean_3.setServer_query_full_scan("53");

        bean_1.setServer_query_open_page("51");
        bean_2.setServer_query_open_page("54");
        bean_3.setServer_query_open_page("53");

        bean_1.setServer_query_opened_page("51");
        bean_2.setServer_query_opened_page("54");
        bean_3.setServer_query_opened_page("53");

        bean_1.setServer_query_slow_query("51");
        bean_2.setServer_query_slow_query("54");
        bean_3.setServer_query_slow_query("53");

        int aa = Integer.MIN_VALUE + 1;
        int bb = Integer.MAX_VALUE - 1;
        bean_1.setServer_lock_request(Integer.toString(aa));
        bean_2.setServer_lock_request(Integer.toString(bb));
        bean_3.setServer_lock_request("53");

        bean.getDelta(bean_1, bean_2, bean_3);

        assertEquals(bean.getCas_mon_req(), "1");
        assertEquals(bean.getCas_mon_query(), "1");
        assertEquals(bean.getCas_mon_tran(), "2");
        assertEquals(bean.getCas_mon_long_tran(), "1");
        assertEquals(bean.getCas_mon_long_query(), "1");
        assertEquals(bean.getCas_mon_error_query(), "1");
        assertEquals(bean.getCas_mon_act_session(), "51");
        assertEquals(bean.getServer_buffer_page_read(), "1");
        assertEquals(bean.getServer_buffer_page_write(), "1");
        assertEquals(bean.getServer_conn_aborted_clients(), "1");
        assertEquals(bean.getServer_conn_cli_request(), "1");
        assertEquals(bean.getServer_conn_conn_req(), "1");
        assertEquals(bean.getServer_lock_deadlock(), "1");
        assertEquals(bean.getServer_query_full_scan(), "1");
        assertEquals(bean.getServer_query_open_page(), "1");
        assertEquals(bean.getServer_query_opened_page(), "1");
        assertEquals(bean.getServer_query_slow_query(), "1");
        assertEquals(bean.getServer_lock_request(), "2");

        bean_1.setCas_mon_req("a");
        bean_2.setCas_mon_req("53");
        bean_3.setCas_mon_req("52");

        bean_1.setServer_lock_request("a");
        bean_2.setServer_lock_request("54");
        bean_3.setServer_lock_request("a");
        bean.getDelta(bean_1, bean_2, bean_3);
        assertEquals(bean.getCas_mon_req(), "0");
        assertEquals(bean.getServer_lock_request(), "0");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult#getDelta(com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult,
     * com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult,
     * com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult, float)} .
     */
    public void testGetDeltaDiagStatusResultDiagStatusResultDiagStatusResultFloat() {
        DiagStatusResult bean_1 = new DiagStatusResult();
        DiagStatusResult bean_2 = new DiagStatusResult();
        DiagStatusResult bean_3 = new DiagStatusResult();

        bean_1.setCas_mon_req("51");
        bean_2.setCas_mon_req("53");
        bean_3.setCas_mon_req("52");

        bean_1.setCas_mon_query("51");
        bean_2.setCas_mon_query("53");
        bean_3.setCas_mon_query("52");

        long a = Long.MIN_VALUE + 1;
        long b = Long.MAX_VALUE - 1;
        bean_1.setCas_mon_tran(Long.toString(a));
        bean_2.setCas_mon_tran(Long.toString(b));
        bean_3.setCas_mon_tran("52");

        bean_1.setCas_mon_long_tran("51");
        bean_2.setCas_mon_long_tran("53");
        bean_3.setCas_mon_long_tran("52");

        bean_1.setCas_mon_long_query("51");
        bean_2.setCas_mon_long_query("53");
        bean_3.setCas_mon_long_query("52");

        bean_1.setCas_mon_error_query("51");
        bean_2.setCas_mon_error_query("53");
        bean_3.setCas_mon_error_query("52");

        bean_1.setCas_mon_act_session("51");
        bean_2.setCas_mon_act_session("53");
        bean_3.setCas_mon_act_session("52");

        bean_1.setServer_buffer_page_read("51");
        bean_2.setServer_buffer_page_read("53");
        bean_3.setServer_buffer_page_read("52");

        bean_1.setServer_buffer_page_write("51");
        bean_2.setServer_buffer_page_write("54");
        bean_3.setServer_buffer_page_write("53");

        bean_1.setServer_conn_aborted_clients("51");
        bean_2.setServer_conn_aborted_clients("54");
        bean_3.setServer_conn_aborted_clients("53");

        bean_1.setServer_conn_cli_request("51");
        bean_2.setServer_conn_cli_request("54");
        bean_3.setServer_conn_cli_request("53");

        bean_1.setServer_conn_conn_reject("51");
        bean_2.setServer_conn_conn_reject("54");
        bean_3.setServer_conn_conn_reject("53");

        bean_1.setServer_conn_conn_req("51");
        bean_2.setServer_conn_conn_req("54");
        bean_3.setServer_conn_conn_req("53");

        bean_1.setServer_lock_deadlock("51");
        bean_2.setServer_lock_deadlock("54");
        bean_3.setServer_lock_deadlock("53");

        bean_1.setServer_query_full_scan("51");
        bean_2.setServer_query_full_scan("54");
        bean_3.setServer_query_full_scan("53");

        bean_1.setServer_query_open_page("51");
        bean_2.setServer_query_open_page("54");
        bean_3.setServer_query_open_page("53");

        bean_1.setServer_query_opened_page("51");
        bean_2.setServer_query_opened_page("54");
        bean_3.setServer_query_opened_page("53");

        bean_1.setServer_query_slow_query("51");
        bean_2.setServer_query_slow_query("54");
        bean_3.setServer_query_slow_query("53");

        int aa = Integer.MIN_VALUE + 1;
        int bb = Integer.MAX_VALUE - 1;
        bean_1.setServer_lock_request(Integer.toString(aa));
        bean_2.setServer_lock_request(Integer.toString(bb));
        bean_3.setServer_lock_request("53");

        bean.getDelta(bean_1, bean_2, bean_3, 0.5f);

        assertEquals(bean.getCas_mon_req(), "2");
        assertEquals(bean.getCas_mon_query(), "2");
        assertEquals(bean.getCas_mon_tran(), "4");
        assertEquals(bean.getCas_mon_long_tran(), "2");
        assertEquals(bean.getCas_mon_long_query(), "2");
        assertEquals(bean.getCas_mon_error_query(), "2");
        assertEquals(bean.getCas_mon_act_session(), "51");
        assertEquals(bean.getServer_buffer_page_read(), "2");
        assertEquals(bean.getServer_buffer_page_write(), "2");
        assertEquals(bean.getServer_conn_aborted_clients(), "2");
        assertEquals(bean.getServer_conn_cli_request(), "2");
        assertEquals(bean.getServer_conn_conn_req(), "2");
        assertEquals(bean.getServer_lock_deadlock(), "2");
        assertEquals(bean.getServer_query_full_scan(), "2");
        assertEquals(bean.getServer_query_open_page(), "2");
        assertEquals(bean.getServer_query_opened_page(), "2");
        assertEquals(bean.getServer_query_slow_query(), "2");
        assertEquals(bean.getServer_lock_request(), "4");

        bean_1.setCas_mon_req("a");
        bean_2.setCas_mon_req("53");
        bean_3.setCas_mon_req("52");

        bean_1.setServer_lock_request("a");
        bean_2.setServer_lock_request("54");
        bean_3.setServer_lock_request("a");
        bean.getDelta(bean_1, bean_2, bean_3, 0.5f);
        assertEquals(bean.getCas_mon_req(), "0");
        assertEquals(bean.getServer_lock_request(), "0");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult#getDiagStatusResultMap()} .
     */
    public void testGetDiagStatusResultMap() {
        Map<String, String> map = bean.getDiagStatusResultMap();
        assertTrue(map.isEmpty());

        DiagStatusResult bean_1 = new DiagStatusResult();
        DiagStatusResult bean_2 = new DiagStatusResult();
        bean.getDelta(bean_1, bean_2);

        assertFalse(map.isEmpty());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult#getCas_mon_req()} .
     */
    public void testGetCas_mon_req() {
        bean.setCas_mon_req("cas_mon_req");
        assertEquals(bean.getCas_mon_req(), "cas_mon_req");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult#getCas_mon_tran()} .
     */
    public void testGetCas_mon_tran() {
        bean.setCas_mon_tran("cas_mon_tran");
        assertEquals(bean.getCas_mon_tran(), "cas_mon_tran");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult#getCas_mon_act_session()} .
     */
    public void testGetCas_mon_act_session() {
        bean.setCas_mon_act_session("cas_mon_act_session");
        assertEquals(bean.getCas_mon_act_session(), "cas_mon_act_session");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult#getServer_query_open_page()}
     * .
     */
    public void testGetServer_query_open_page() {
        bean.setServer_query_open_page("server_query_open_page");
        assertEquals(bean.getServer_query_open_page(), "server_query_open_page");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult#getServer_query_opened_page()}
     * .
     */
    public void testGetServer_query_opened_page() {
        bean.setServer_query_opened_page("server_query_opened_page");
        assertEquals(bean.getServer_query_opened_page(), "server_query_opened_page");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult#getServer_query_slow_query()}
     * .
     */
    public void testGetServer_query_slow_query() {
        bean.setServer_query_slow_query("server_query_slow_query");
        assertEquals(bean.getServer_query_slow_query(), "server_query_slow_query");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult#getServer_query_full_scan()}
     * .
     */
    public void testGetServer_query_full_scan() {
        bean.setServer_query_full_scan("server_query_full_scan");
        assertEquals(bean.getServer_query_full_scan(), "server_query_full_scan");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult#getServer_conn_cli_request()}
     * .
     */
    public void testGetServer_conn_cli_request() {
        bean.setServer_conn_cli_request("server_conn_cli_request");
        assertEquals(bean.getServer_conn_cli_request(), "server_conn_cli_request");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult#getServer_conn_aborted_clients()}
     * .
     */
    public void testGetServer_conn_aborted_clients() {
        bean.setServer_conn_aborted_clients("server_conn_aborted_clients");
        assertEquals(bean.getServer_conn_aborted_clients(), "server_conn_aborted_clients");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult#getServer_conn_conn_req()} .
     */
    public void testGetServer_conn_conn_req() {
        bean.setServer_conn_conn_req("server_conn_conn_req");
        assertEquals(bean.getServer_conn_conn_req(), "server_conn_conn_req");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult#getServer_conn_conn_reject()}
     * .
     */
    public void testGetServer_conn_conn_reject() {
        bean.setServer_conn_conn_reject("server_conn_conn_reject");
        assertEquals(bean.getServer_conn_conn_reject(), "server_conn_conn_reject");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult#getServer_buffer_page_write()}
     * .
     */
    public void testGetServer_buffer_page_write() {
        bean.setServer_buffer_page_write("server_buffer_page_write");
        assertEquals(bean.getServer_buffer_page_write(), "server_buffer_page_write");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult#getServer_buffer_page_read()}
     * .
     */
    public void testGetServer_buffer_page_read() {
        bean.setServer_buffer_page_read("server_buffer_page_read");
        assertEquals(bean.getServer_buffer_page_read(), "server_buffer_page_read");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult#getServer_lock_deadlock()} .
     */
    public void testGetServer_lock_deadlock() {
        bean.setServer_lock_deadlock("server_lock_deadlock");
        assertEquals(bean.getServer_lock_deadlock(), "server_lock_deadlock");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult#getServer_lock_request()} .
     */
    public void testGetServer_lock_request() {
        bean.setServer_lock_request("server_lock_request");
        assertEquals(bean.getServer_lock_request(), "server_lock_request");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult#getCas_mon_query()} .
     */
    public void testGetCas_mon_query() {
        bean.setCas_mon_query("cas_mon_query");
        assertEquals(bean.getCas_mon_query(), "cas_mon_query");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult#getCas_mon_long_query()} .
     */
    public void testGetCas_mon_long_query() {
        bean.setCas_mon_long_query("cas_mon_long_query");
        assertEquals(bean.getCas_mon_long_query(), "cas_mon_long_query");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult#getCas_mon_long_tran()} .
     */
    public void testGetCas_mon_long_tran() {
        bean.setCas_mon_long_tran("cas_mon_long_tran");
        assertEquals(bean.getCas_mon_long_tran(), "cas_mon_long_tran");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DiagStatusResult#getCas_mon_error_query()} .
     */
    public void testGetCas_mon_error_query() {
        bean.setCas_mon_error_query("cas_mon_error_query");
        assertEquals(bean.getCas_mon_error_query(), "cas_mon_error_query");
    }
}
