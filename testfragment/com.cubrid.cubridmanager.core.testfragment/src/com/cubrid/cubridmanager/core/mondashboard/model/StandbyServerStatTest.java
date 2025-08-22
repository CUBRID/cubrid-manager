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
package com.cubrid.cubridmanager.core.mondashboard.model;

import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.common.task.CommonQueryTask;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;

/**
 * Test the type of StandbyServerStat The execute method is as followed final
 * CommonQueryTask<StandbyServerStat> task = new CommonQueryTask<StandbyServerStat>( serverInfo,
 * CommonSendMsg.getCommonDatabaseSendMsg(), bean); task.setDbName("ha_test"); task.execute(); bean
 * = task.getResultModel();
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-6-25 created by lizhiqiang
 */
public class StandbyServerStatTest extends
        //	TestCase {
        SetupEnvTestCase {
    StandbyServerStat bean;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        bean = new StandbyServerStat();
        CommonQueryTask<StandbyServerStat> task =
                new CommonQueryTask<StandbyServerStat>(
                        serverInfo, CommonSendMsg.getStandbyServerstatMsgItems(), bean);
        task.setDbName("ha_test");
        task.setDbid("dba");
        task.setDbpasswd("df");
        task.execute();
        bean = task.getResultModel();
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.mondashboard.model.StandbyServerStat#getTaskName()} .
     */
    public void testGetTaskName() {
        assertEquals("getstandbyserverstat", bean.getTaskName());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.mondashboard.model.StandbyServerStat#getDbname()} .
     */
    public void testGetDbname() {
        bean.setDbname("dbname");
        assertEquals("dbname", bean.getDbname());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.mondashboard.model.StandbyServerStat#copyFrom()} .
     */
    public void testCopyFrom() {
        StandbyServerStat another = new StandbyServerStat();
        another.setCommit_counter("10");
        another.setDelay_time("20");
        another.setDelete_counter("30");
        another.setFail_counter("50");
        another.setInsert_counter("60");
        another.setUpdate_counter("70");
        bean.copyFrom(another);
        assertEquals("10", bean.getCommit_counter());
        assertEquals("20", bean.getDelay_time());
        assertEquals("30", bean.getDelete_counter());
        assertEquals("50", bean.getFail_counter());
        assertEquals("60", bean.getInsert_counter());
        assertEquals("70", bean.getUpdate_counter());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.mondashboard.model.StandbyServerStat#getStatus()} .
     */
    public void testGetStatus() {
        bean.setStatus("success");
        assertTrue(bean.getStatus());
        bean.setStatus("ddd");
        assertFalse(bean.getStatus());
        bean.setStatus(null);
        assertFalse(bean.getStatus());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.mondashboard.model.StandbyServerStat#getNote()} .
     */
    public void testGetNote() {
        bean.setNote("note");
        assertEquals("note", bean.getNote());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.mondashboard.model.StandbyServerStat#getDelay_time()} .
     */
    public void testGetDelay_time() {
        bean.setDelay_time("10");
        assertEquals("10", bean.getDelay_time());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.mondashboard.model.StandbyServerStat#getInsert_counter()} .
     */
    public void testGetInsert_counter() {
        bean.setInsert_counter("10");
        assertEquals("10", bean.getInsert_counter());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.mondashboard.model.StandbyServerStat#getUpdate_counter()} .
     */
    public void testGetUpdate_counter() {
        bean.setUpdate_counter("10");
        assertEquals("10", bean.getUpdate_counter());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.mondashboard.model.StandbyServerStat#getDelete_counter()} .
     */
    public void testGetDelete_counter() {
        bean.setDelete_counter("10");
        assertEquals("10", bean.getDelete_counter());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.mondashboard.model.StandbyServerStat#getCommit_counter()} .
     */
    public void testGetCommit_counter() {
        bean.setCommit_counter("10");
        assertEquals("10", bean.getCommit_counter());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.mondashboard.model.StandbyServerStat#getFail_counter()} .
     */
    public void testGetFail_counter() {
        bean.setFail_counter("10");
        assertEquals("10", bean.getFail_counter());
    }
}
