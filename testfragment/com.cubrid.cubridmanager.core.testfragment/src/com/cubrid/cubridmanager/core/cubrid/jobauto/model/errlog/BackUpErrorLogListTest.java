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

import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

/**
 * Test BackUpErrorLogList
 *
 * @author sq
 * @version 1.0 - 2010-1-4 created by sq
 */
public class BackUpErrorLogListTest extends TestCase {
    private BackUpErrorLogList bean;
    private BackUpErrorLog errorLog;;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        errorLog = new BackUpErrorLog();
        errorLog.setDbname("demodb");
        errorLog.setBackupid("ccc");
        errorLog.setError_desc("descdesc");
        errorLog.setError_time("20090506");
        List<BackUpErrorLog> errorLogLst = new ArrayList<BackUpErrorLog>();
        bean = new BackUpErrorLogList();
        bean.setErrorLogList(errorLogLst);

        assertNotNull(bean.getErrorLogList());
        assertEquals(bean.getErrorLogList().size(), 0);
        bean.addError(errorLog);
        assertNotNull(bean.getErrorLogList());
        assertEquals(bean.getErrorLogList().size(), 1);
        List<BackUpErrorLog> resultLst = bean.getErrorLogList();
        BackUpErrorLog resultLog = resultLst.get(0);
        assertEquals(resultLog.getDbname(), "demodb");
        assertEquals(resultLog.getBackupid(), "ccc");
        assertEquals(resultLog.getError_desc(), "descdesc");
        assertEquals(resultLog.getError_time(), "20090506");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.errlog.BackUpErrorLogList#getTaskName()} .
     */
    public void testGetTaskName() {
        assertEquals(bean.getTaskName(), "getautobackupdberrlog");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.errlog.BackUpErrorLogList#getErrorLogList()}
     * .
     */
    public void testGetErrorLogList() {
        assertNotNull(bean.getErrorLogList());
        assertFalse(bean.getErrorLogList().isEmpty());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.errlog.BackUpErrorLogList#addError(com.cubrid.cubridmanager.core.cubrid.jobauto.model.errlog.BackUpErrorLog)}
     * .
     */
    public void testAddError() {
        bean.addError(errorLog);
        assertNotNull(bean.getErrorLogList());
        assertEquals(bean.getErrorLogList().size(), 2);
        List<BackUpErrorLog> resultLst = bean.getErrorLogList();
        BackUpErrorLog resultLog = resultLst.get(0);
        assertEquals(resultLog.getDbname(), "demodb");
        assertEquals(resultLog.getBackupid(), "ccc");
        assertEquals(resultLog.getError_desc(), "descdesc");
        assertEquals(resultLog.getError_time(), "20090506");
        // exception case
        BackUpErrorLogList errorLogBean = new BackUpErrorLogList();
        errorLogBean.addError(errorLog);
        assertEquals(errorLogBean.getErrorLogList().get(0).getDbname(), "demodb");
    }
}
