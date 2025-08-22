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

import junit.framework.TestCase;

/**
 * Test the type of DbProcStat
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-6-4 created by lizhiqiang
 */
public class DbProcStatTest extends TestCase {
    //	SetupEnvTestCase {
    DbProcStat bean;

    /**
     * setUp method
     *
     * @exception Exception an instance of Exception
     */
    protected void setUp() throws Exception {
        super.setUp();
        bean = new DbProcStat();
        //						final CommonQueryTask<DbProcStat> task = new CommonQueryTask<DbProcStat>(
        //								serverInfo, CommonSendMsg.getCommonSimpleSendMsg(), bean);
        //						task.execute();
        //
        //						bean = task.getResultModel();
        //				      System.out.println(bean.getStatus());
        //				      System.out.println(bean.getDbProcLst());

    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbProcStat#DbProcStat()} .
     */
    public void testDbProcStat() {
        assertNotNull(bean.getDbProcLst());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbProcStat#getTaskName()} .
     */
    public void testGetTaskName() {
        assertEquals("getdbprocstat", bean.getTaskName());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbProcStat#addDbstat(com.cubrid.cubridmanager.core.monitoring.model.DbSysStatTest)}
     * .
     */
    public void testAddDbstat() {
        bean.addDbstat(null);
        assertNotNull(bean.getDbProcLst());
        DbSysStat dsp = new DbSysStat();
        bean.addDbstat(dsp);
        assertEquals(2, bean.getDbProcLst().size());
    }

    /**
     * Test method for {@link com.cubrid.cubridmanager.core.monitoring.model.DbProcStat#getDbname()}
     * .
     */
    public void testGetDbname() {
        bean.setDbname("dbName");
        assertEquals("dbName", bean.getDbname());
    }

    /**
     * Test method for {@link com.cubrid.cubridmanager.core.monitoring.model.DbProcStat#getStatus()}
     * .
     */
    public void testGetStatus() {
        bean.setStatus("success");
        assertTrue(bean.getStatus());
        bean.setStatus("dfa");
        assertFalse(bean.getStatus());
    }

    /**
     * Test method for {@link com.cubrid.cubridmanager.core.monitoring.model.DbProcStat#getNote()} .
     */
    public void testGetNote() {
        bean.setNote("note");
        assertEquals("note", bean.getNote());
    }

    /**
     * Test method for {@link com.cubrid.cubridmanager.core.monitoring.model.DbProcStat#copyFrom()}
     */
    public void testCopyFrom() {
        DbProcStat clone = new DbProcStat();
        clone.setDbname("dbname");
        clone.addDbstat(new DbSysStat());
        bean.copyFrom(clone);
        assertEquals("dbname", bean.getDbname());
        assertNotNull(bean.getDbProcLst());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbProcStat#clearDbstat()}
     */
    public void testClearDbstat() {
        DbSysStat dsp = new DbSysStat();
        bean.addDbstat(dsp);
        bean.clearDbstat();
        assertTrue(bean.getDbProcLst().isEmpty());
    }
}
