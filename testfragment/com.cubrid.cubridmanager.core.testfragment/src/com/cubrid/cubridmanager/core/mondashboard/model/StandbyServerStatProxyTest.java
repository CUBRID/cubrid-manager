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

import junit.framework.TestCase;

/**
 * Test the type of StandbyServerStatProxy
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-6-25 created by lizhiqiang
 */
public class StandbyServerStatProxyTest extends TestCase {
    StandbyServerStatProxy bean;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        bean = new StandbyServerStatProxy();
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.mondashboard.model.StandbyServerStatProxy#compute(com.cubrid.cubridmanager.core.mondashboard.model.StandbyServerStat,
     * com.cubrid.cubridmanager.core.mondashboard.model.StandbyServerStat)} .
     */
    public void testComputeStandbyServerStatStandbyServerStat() {
        StandbyServerStat statA = new StandbyServerStat();
        statA.setDelay_time("10");
        statA.setInsert_counter("20");
        statA.setDelete_counter("20");
        statA.setUpdate_counter("20");
        statA.setFail_counter("20");
        statA.setCommit_counter("20");
        StandbyServerStat statB = new StandbyServerStat();
        statB.setInsert_counter("15");
        statB.setDelete_counter("15");
        statB.setUpdate_counter("15");
        statB.setFail_counter("15");
        statB.setCommit_counter("15");
        bean.compute(statA, statB);

        assertEquals("10", bean.getDelayTime());
        assertEquals("5", bean.getDeleteCounter());
        assertEquals("5", bean.getCommitCounter());
        assertEquals("5", bean.getFailCounter());
        assertEquals("5", bean.getInsertCounter());
        assertEquals("5", bean.getUpdateCounter());

        statA.setInsert_counter("14");
        statA.setDelete_counter("14");
        statA.setUpdate_counter("14");
        statA.setFail_counter("14");
        statA.setCommit_counter("14");

        statB.setInsert_counter("10");
        statB.setDelete_counter("10");
        statB.setUpdate_counter("10");
        statB.setFail_counter("10");
        statB.setCommit_counter("14");
        bean.compute(statA, statB);
        assertEquals("0", bean.getDelayTime());

        statA.setDelay_time("1000000000");
        statA.setInsert_counter("14");
        statA.setDelete_counter("14");
        statA.setUpdate_counter("14");
        statA.setFail_counter("14");
        statA.setCommit_counter("14");

        statB.setInsert_counter("10");
        statB.setDelete_counter("10");
        statB.setUpdate_counter("10");
        statB.setFail_counter("10");
        statB.setCommit_counter("10");
        bean.compute(statA, statB);
        assertEquals("0", bean.getDelayTime());

        statA.setDelay_time("a");
        statA.setInsert_counter("14");
        statA.setDelete_counter("14");
        statA.setUpdate_counter("14");
        statA.setFail_counter("14");
        statA.setCommit_counter("14");

        statB.setInsert_counter("10");
        statB.setDelete_counter("10");
        statB.setUpdate_counter("10");
        statB.setFail_counter("10");
        statB.setCommit_counter("10");
        bean.compute(statA, statB);
        assertEquals("0", bean.getDelayTime());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.mondashboard.model.StandbyServerStatProxy#compute(com.cubrid.cubridmanager.core.mondashboard.model.StandbyServerStat,
     * com.cubrid.cubridmanager.core.mondashboard.model.StandbyServerStat,
     * com.cubrid.cubridmanager.core.mondashboard.model.StandbyServerStat)} .
     */
    public void testComputeStandbyServerStatStandbyServerStatStandbyServerStat() {
        StandbyServerStat statA = new StandbyServerStat();
        statA.setDelay_time("10");
        statA.setInsert_counter("20");
        statA.setDelete_counter("20");
        statA.setUpdate_counter("20");
        statA.setFail_counter("20");
        statA.setCommit_counter("20");
        StandbyServerStat statB = new StandbyServerStat();
        statB.setInsert_counter("15");
        statB.setDelete_counter("15");
        statB.setUpdate_counter("15");
        statB.setFail_counter("15");
        statB.setCommit_counter("15");
        StandbyServerStat statC = new StandbyServerStat();
        statC.setInsert_counter("10");
        statC.setDelete_counter("10");
        statC.setUpdate_counter("10");
        statC.setFail_counter("10");
        statC.setCommit_counter("10");
        bean.compute(statA, statB, statC);

        assertEquals("10", bean.getDelayTime());
        assertEquals("5", bean.getDeleteCounter());
        assertEquals("5", bean.getCommitCounter());
        assertEquals("5", bean.getFailCounter());
        assertEquals("5", bean.getInsertCounter());
        assertEquals("5", bean.getUpdateCounter());

        statA.setInsert_counter("14");
        statA.setDelete_counter("14");
        statA.setUpdate_counter("14");
        statA.setFail_counter("14");
        statA.setCommit_counter("14");
        bean.compute(statA, statB, statC);
        assertEquals("10", bean.getDelayTime());
        assertEquals("5", bean.getDeleteCounter());
        assertEquals("5", bean.getCommitCounter());
        assertEquals("5", bean.getFailCounter());
        assertEquals("5", bean.getInsertCounter());
        assertEquals("5", bean.getUpdateCounter());

        statA.setInsert_counter("14");
        statA.setDelete_counter("14");
        statA.setUpdate_counter("14");
        statA.setFail_counter("14");
        statA.setCommit_counter("14");

        statB.setInsert_counter("10");
        statB.setDelete_counter("10");
        statB.setUpdate_counter("10");
        statB.setFail_counter("10");
        statB.setCommit_counter("14");
        bean.compute(statA, statB, statC);
        assertEquals("0", bean.getDelayTime());

        statA.setDelay_time("1000000000");
        statA.setInsert_counter("14");
        statA.setDelete_counter("14");
        statA.setUpdate_counter("14");
        statA.setFail_counter("14");
        statA.setCommit_counter("14");

        statB.setInsert_counter("10");
        statB.setDelete_counter("10");
        statB.setUpdate_counter("10");
        statB.setFail_counter("10");
        statB.setCommit_counter("10");
        bean.compute(statA, statB, statC);
        assertEquals("0", bean.getDelayTime());

        statA.setDelay_time("a");
        statA.setInsert_counter("14");
        statA.setDelete_counter("14");
        statA.setUpdate_counter("14");
        statA.setFail_counter("14");
        statA.setCommit_counter("14");

        statB.setInsert_counter("10");
        statB.setDelete_counter("10");
        statB.setUpdate_counter("10");
        statB.setFail_counter("10");
        statB.setCommit_counter("10");
        bean.compute(statA, statB, statC);
        assertEquals("0", bean.getDelayTime());

        long b_count = Long.MAX_VALUE - 5;
        long a_count = 5 + Long.MIN_VALUE;

        statA.setCommit_counter(Long.toString(a_count));
        statB.setCommit_counter(Long.toString(b_count));
        bean.compute(statA, statB, statC);
        assertEquals("10", bean.getCommitCounter());

        statA.setCommit_counter("a");
        statB.setCommit_counter("14");
        bean.compute(statA, statB, statC);
        assertEquals("0", bean.getCommitCounter());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.mondashboard.model.StandbyServerStatProxy#getDbname()} .
     */
    public void testGetDbname() {
        bean.setDbname("dbname");
        assertEquals("dbname", bean.getDbname());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.mondashboard.model.StandbyServerStatProxy#getStatus()} .
     */
    public void testGetStatus() {
        bean.setStatus("status");
        assertEquals("status", bean.getStatus());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.mondashboard.model.StandbyServerStatProxy#getNote()} .
     */
    public void testGetNote() {
        bean.setNote("note");
        assertEquals("note", bean.getNote());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.mondashboard.model.StandbyServerStatProxy#getDelayTime()} .
     */
    public void testGetDelayTime() {
        bean.setDelayTime("10");
        assertEquals("10", bean.getDelayTime());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.mondashboard.model.StandbyServerStatProxy#getInsertCounter()} .
     */
    public void testGetInsertCounter() {
        bean.setInsertCounter("10");
        assertEquals("10", bean.getInsertCounter());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.mondashboard.model.StandbyServerStatProxy#getUpdateCounter()} .
     */
    public void testGetUpdateCounter() {
        bean.setUpdateCounter("10");
        assertEquals("10", bean.getUpdateCounter());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.mondashboard.model.StandbyServerStatProxy#getDeleteCounter()} .
     */
    public void testGetDeleteCounter() {
        bean.setDeleteCounter("10");
        assertEquals("10", bean.getDeleteCounter());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.mondashboard.model.StandbyServerStatProxy#getCommitCounter()} .
     */
    public void testGetCommitCounter() {
        bean.setCommitCounter("10");
        assertEquals("10", bean.getCommitCounter());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.mondashboard.model.StandbyServerStatProxy#getFailCounter()} .
     */
    public void testGetFailCounter() {
        bean.setFailCounter("10");
        assertEquals("10", bean.getFailCounter());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.mondashboard.model.StandbyServerStatProxy#getStatusResultMap()}
     * .
     */
    public void testGetStatusResultMap() {
        assertNotNull(bean.getStatusResultMap());
    }
}
