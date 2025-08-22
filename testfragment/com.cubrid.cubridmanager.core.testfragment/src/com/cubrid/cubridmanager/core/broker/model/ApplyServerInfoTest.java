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
package com.cubrid.cubridmanager.core.broker.model;

import junit.framework.TestCase;

/**
 * TODO: how to write comments The purpose of the class Known bugs The development/maintenance
 * history of the class Document applicable invariants The concurrency strategy
 *
 * <p>ApplyServerInfoTest Description
 *
 * @author lizhiqiang
 * @version 1.0 - 2009-12-30 created by lizhiqiang
 */
public class ApplyServerInfoTest extends TestCase {
    ApplyServerInfo asi;

    public void setUp() {
        asi = new ApplyServerInfo();
        asi.setAs_id("as_id");
        asi.setAs_c("as_c");
        asi.setAs_cpu("as_cpu");
        asi.setAs_ctime("2000/12/12");
        asi.setAs_cur("as_cur");
        asi.setAs_dbhost("as_dbhost");
        asi.setAs_dbname("as_dbname");
        asi.setAs_error_query("as_error_query");
        asi.setAs_lat("as_lat");
        asi.setAs_long_query("as_long_query");
        asi.setAs_lct("as_lct");
        asi.setAs_long_tran("as_long_tran");
        asi.setAs_num_query("as_num_query");
        asi.setAs_pid("as_pid");
        asi.setAs_psize("as_psize");
        asi.setAs_status("as_status");
        asi.setAs_num_tran("as_num_tran");
        asi.setAs_port("asPort");
        asi.setAs_client_ip("asClientIp");
    }

    public void testGet() {
        String temp = asi.getAs_id();
        assertEquals(temp, "as_id");
        temp = asi.getAs_c();
        assertEquals(temp, "as_c");
        temp = asi.getAs_cpu();
        assertEquals(temp, "as_cpu");
        temp = asi.getAs_ctime();
        assertEquals(temp, "2000/12/12");
        temp = asi.getAs_cur();
        assertEquals(temp, "as_cur");
        temp = asi.getAs_dbhost();
        assertEquals(temp, "as_dbhost");
        temp = asi.getAs_dbname();
        assertEquals(temp, "as_dbname");
        temp = asi.getAs_error_query();
        assertEquals(temp, "as_error_query");
        temp = asi.getAs_lat();
        assertEquals(temp, "as_lat");
        temp = asi.getAs_long_query();
        assertEquals(temp, "as_long_query");
        temp = asi.getAs_lct();
        assertEquals(temp, "as_lct");
        temp = asi.getAs_long_tran();
        assertEquals(temp, "as_long_tran");
        temp = asi.getAs_num_query();
        assertEquals(temp, "as_num_query");
        temp = asi.getAs_pid();
        assertEquals(temp, "as_pid");
        temp = asi.getAs_psize();
        assertEquals(temp, "as_psize");
        temp = asi.getAs_status();
        assertEquals(temp, "as_status");
        temp = asi.getAs_num_tran();
        assertEquals(temp, "as_num_tran");
        temp = asi.getAs_port();
        assertEquals("asPort", temp);
        temp = asi.getAs_client_ip();
        assertEquals("asClientIp", temp);
    }

    public void testClone() {
        ApplyServerInfo newAs = asi.clone();
        assertNotSame(asi, newAs);
    }

    public void testClear() {
        asi.clear();
        assertEquals("", asi.getAs_client_ip());
    }
}
