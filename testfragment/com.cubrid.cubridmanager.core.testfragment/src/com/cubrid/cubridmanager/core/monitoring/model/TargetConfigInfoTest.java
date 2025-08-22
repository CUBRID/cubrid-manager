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

import java.util.List;
import junit.framework.TestCase;

/**
 * Test TargetConfigInfo
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-1-5 created by lizhiqiang
 */
public class TargetConfigInfoTest extends TestCase {
    private TargetConfigInfo bean;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        bean = new TargetConfigInfo();
        String[] test = new String[] {"10", "20"};
        bean.setServer_query_open_page(test);
        bean.setServer_query_opened_page(test);
        bean.setServer_query_slow_query(test);
        bean.setServer_query_full_scan(test);
        bean.setServer_conn_cli_request(test);
        bean.setServer_conn_aborted_clients(test);
        bean.setServer_conn_conn_req(test);
        bean.setServer_conn_conn_reject(test);
        bean.setServer_buffer_page_write(test);
        bean.setServer_buffer_page_read(test);
        bean.setServer_lock_deadlock(test);
        bean.setServer_lock_request(test);
        bean.setCas_st_request(test);
        bean.setCas_st_transaction(test);
        bean.setCas_st_active_session(test);
        bean.setCas_st_query(test);
        bean.setCas_st_long_query(test);
        bean.setCas_st_long_tran(test);
        bean.setCas_st_error_query(test);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.TargetConfigInfo#getServer_query_open_page()}
     * .
     */
    public void testGetServer_query_open_page() {
        assertEquals(bean.getServer_query_open_page()[0], "10");
        assertEquals(bean.getServer_query_open_page()[1], "20");

        bean.setServer_query_open_page(null);
        assertEquals(bean.getServer_query_open_page(), null);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.TargetConfigInfo#getServer_query_opened_page()}
     * .
     */
    public void testGetServer_query_opened_page() {
        assertEquals(bean.getServer_query_opened_page()[0], "10");
        assertEquals(bean.getServer_query_opened_page()[1], "20");

        bean.setServer_query_opened_page(null);
        assertEquals(bean.getServer_query_opened_page(), null);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.TargetConfigInfo#getServer_query_slow_query()}
     * .
     */
    public void testGetServer_query_slow_query() {
        assertEquals(bean.getServer_query_slow_query()[0], "10");
        assertEquals(bean.getServer_query_slow_query()[1], "20");

        bean.setServer_query_slow_query(null);
        assertEquals(bean.getServer_query_slow_query(), null);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.TargetConfigInfo#getServer_query_full_scan()}
     * .
     */
    public void testGetServer_query_full_scan() {
        assertEquals(bean.getServer_query_full_scan()[0], "10");
        assertEquals(bean.getServer_query_full_scan()[1], "20");
        bean.setServer_query_full_scan(null);
        assertEquals(bean.getServer_query_full_scan(), null);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.TargetConfigInfo#getServer_conn_cli_request()}
     * .
     */
    public void testGetServer_conn_cli_request() {
        assertEquals(bean.getServer_conn_cli_request()[0], "10");
        assertEquals(bean.getServer_conn_cli_request()[1], "20");

        bean.setServer_conn_cli_request(null);
        assertEquals(bean.getServer_conn_cli_request(), null);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.TargetConfigInfo#getServer_conn_aborted_clients()}
     * .
     */
    public void testGetServer_conn_aborted_clients() {
        assertEquals(bean.getServer_conn_aborted_clients()[0], "10");
        assertEquals(bean.getServer_conn_aborted_clients()[1], "20");

        bean.setServer_conn_cli_request(null);
        assertEquals(bean.getServer_conn_cli_request(), null);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.TargetConfigInfo#getServer_conn_conn_req()} .
     */
    public void testGetServer_conn_conn_req() {
        assertEquals(bean.getServer_conn_conn_req()[0], "10");
        assertEquals(bean.getServer_conn_conn_req()[1], "20");

        bean.setServer_conn_cli_request(null);
        assertEquals(bean.getServer_conn_cli_request(), null);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.TargetConfigInfo#getServer_conn_conn_reject()}
     * .
     */
    public void testGetServer_conn_conn_reject() {
        assertEquals(bean.getServer_conn_conn_reject()[0], "10");
        assertEquals(bean.getServer_conn_conn_reject()[1], "20");

        bean.setServer_conn_conn_reject(null);
        assertEquals(bean.getServer_conn_conn_reject(), null);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.TargetConfigInfo#getServer_buffer_page_write()}
     * .
     */
    public void testGetServer_buffer_page_write() {
        assertEquals(bean.getServer_buffer_page_write()[0], "10");
        assertEquals(bean.getServer_buffer_page_write()[1], "20");

        bean.setServer_buffer_page_write(null);
        assertEquals(bean.getServer_buffer_page_write(), null);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.TargetConfigInfo#getServer_buffer_page_read()}
     * .
     */
    public void testGetServer_buffer_page_read() {
        assertEquals(bean.getServer_buffer_page_read()[0], "10");
        assertEquals(bean.getServer_buffer_page_read()[1], "20");

        bean.setServer_buffer_page_read(null);
        assertEquals(bean.getServer_buffer_page_read(), null);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.TargetConfigInfo#getServer_lock_deadlock()} .
     */
    public void testGetServer_lock_deadlock() {
        assertEquals(bean.getServer_lock_deadlock()[0], "10");
        assertEquals(bean.getServer_lock_deadlock()[1], "20");

        bean.setServer_lock_deadlock(null);
        assertEquals(bean.getServer_lock_deadlock(), null);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.TargetConfigInfo#getServer_lock_request()} .
     */
    public void testGetServer_lock_request() {
        assertEquals(bean.getServer_lock_request()[0], "10");
        assertEquals(bean.getServer_lock_request()[1], "20");

        bean.setServer_lock_request(null);
        assertEquals(bean.getServer_lock_request(), null);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.TargetConfigInfo#getCas_st_request()} .
     */
    public void testGetCas_st_request() {
        assertEquals(bean.getCas_st_request()[0], "10");
        assertEquals(bean.getCas_st_request()[1], "20");

        bean.setCas_st_request(null);
        assertEquals(bean.getCas_st_request(), null);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.TargetConfigInfo#getCas_st_transaction()} .
     */
    public void testGetCas_st_transaction() {
        assertEquals(bean.getCas_st_transaction()[0], "10");
        assertEquals(bean.getCas_st_transaction()[1], "20");
        bean.setCas_st_transaction(null);
        assertEquals(bean.getCas_st_transaction(), null);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.TargetConfigInfo#getCas_st_active_session()} .
     */
    public void testGetCas_st_active_session() {
        assertEquals(bean.getCas_st_active_session()[0], "10");
        assertEquals(bean.getCas_st_active_session()[1], "20");

        bean.setCas_st_active_session(null);
        assertEquals(bean.getCas_st_active_session(), null);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.TargetConfigInfo#getCas_st_query()} .
     */
    public void testGetCas_st_query() {
        assertEquals(bean.getCas_st_query()[0], "10");
        assertEquals(bean.getCas_st_query()[1], "20");

        bean.setCas_st_query(null);
        assertEquals(bean.getCas_st_query(), null);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.TargetConfigInfo#getCas_st_long_query()} .
     */
    public void testGetCas_st_long_query() {
        assertEquals(bean.getCas_st_long_query()[0], "10");
        assertEquals(bean.getCas_st_long_query()[1], "20");

        bean.setCas_st_long_query(null);
        assertEquals(bean.getCas_st_long_query(), null);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.TargetConfigInfo#getCas_st_long_tran()} .
     */
    public void testGetCas_st_long_tran() {
        assertEquals(bean.getCas_st_long_tran()[0], "10");
        assertEquals(bean.getCas_st_long_tran()[1], "20");

        bean.setCas_st_long_tran(null);
        assertEquals(bean.getCas_st_long_tran(), null);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.TargetConfigInfo#getCas_st_error_query()} .
     */
    public void testGetCas_st_error_query() {
        assertEquals(bean.getCas_st_error_query()[0], "10");
        assertEquals(bean.getCas_st_error_query()[1], "20");

        bean.setCas_st_error_query(null);
        assertEquals(bean.getCas_st_error_query(), null);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.TargetConfigInfo#getList()} .
     */
    public void testGetList() {
        List<String[]> list = bean.getList();
        for (String[] strings : list) {
            if (strings[0].equals("server_query_open_page")) {
                assertEquals(strings[1], "10");
                assertEquals(strings[2], "20");
            }
        }
    }

    /** Test data . */
    public void testData() {
        bean.setServer_conn_conn_req(null);
        bean.getServer_conn_conn_req();
        bean.setServer_conn_aborted_clients(null);
        bean.getServer_conn_aborted_clients();
    }
}
