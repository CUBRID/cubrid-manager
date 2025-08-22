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
package com.cubrid.cubridmanager.core.cubrid.jobauto.model;

import junit.framework.TestCase;

/**
 * Test QueryLogInfo
 *
 * @author sq
 * @version 1.0 - 2010-1-4 created by sq
 */
public class QueryLogInfoTest extends TestCase {
    private QueryLogInfo bean;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        bean = new QueryLogInfo();
        bean.setDbname("dbname");
        bean.setUsername("username");
        bean.setQuery_id("query_id");
        bean.setError_time("error_time");
        bean.setError_code("error_code");
        bean.setError_desc("error_desc");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryLogInfo#getDbname()} .
     */
    public void testGetDbname() {
        assertEquals(bean.getDbname(), "dbname");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryLogInfo#getUsername()} .
     */
    public void testGetUsername() {
        assertEquals(bean.getUsername(), "username");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryLogInfo#getQuery_id()} .
     */
    public void testGetQuery_id() {
        assertEquals(bean.getQuery_id(), "query_id");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryLogInfo#getError_time()} .
     */
    public void testGetError_time() {
        assertEquals(bean.getError_time(), "error_time");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryLogInfo#getError_code()} .
     */
    public void testGetError_code() {
        assertEquals(bean.getError_code(), "error_code");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryLogInfo#getError_desc()} .
     */
    public void testGetError_desc() {
        assertEquals(bean.getError_desc(), "error_desc");
    }
}
