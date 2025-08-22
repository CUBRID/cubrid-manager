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
package com.cubrid.cubridmanager.core.cubrid.jobauto.model.errlog;

import junit.framework.TestCase;

/**
 * TODO: how to write comments The purpose of the class Known bugs The development/maintenance
 * history of the class Document applicable invariants The concurrency strategy
 *
 * <p>BackUpErrorLogTest Description
 *
 * @author sq
 * @version 1.0 - 2010-1-4 created by sq
 */
public class BackUpErrorLogTest extends TestCase {
    private BackUpErrorLog bean;
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        bean = new BackUpErrorLog();
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.errlog.BackUpErrorLog#getDbname()}.
     */
    public void testGetDbname() {
        bean.setDbname("dbname");
        assertEquals(bean.getDbname(), "dbname");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.errlog.BackUpErrorLog#getBackupid()}.
     */
    public void testGetBackupid() {
        bean.setBackupid("backupid");
        assertEquals(bean.getBackupid(), "backupid");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.errlog.BackUpErrorLog#getError_time()}.
     */
    public void testGetError_time() {
        bean.setError_time("error_time");
        assertEquals(bean.getError_time(), "error_time");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.errlog.BackUpErrorLog#getError_desc()}.
     */
    public void testGetError_desc() {
        bean.setError_desc("error_desc");
        assertEquals(bean.getError_desc(), "error_desc");
    }
}
