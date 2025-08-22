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
 * Test QueryPlanInfoHelp
 *
 * @author sq
 * @version 1.0 - 2010-1-4 created by sq
 */
public class QueryPlanInfoHelpTest extends TestCase {
    private QueryPlanInfoHelp bean;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        bean = new QueryPlanInfoHelp();
        QueryPlanInfo queryPlanInfo = new QueryPlanInfo();
        queryPlanInfo.setDetail("10:20");
        queryPlanInfo.setPeriod("DAY");
        bean.setQueryPlanInfo(queryPlanInfo);

        bean.setTime("10:20");
        bean.getDetailTimeForInstance();

        bean.setDetail("setDetail");
        bean.setQuery_id("setQuery_id");
        bean.setPeriod("setPeriod");
        bean.setQuery_string("getQuery_string");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfoHelp#getQueryPlanInfo()} .
     */
    public void testGetQueryPlanInfo() {
        assertEquals(bean.getQueryPlanInfo().getClass(), QueryPlanInfo.class);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfoHelp#buildMsg()} .
     */
    public void testBuildMsgNoUser() {
        String testMsg =
                "query_id:setQuery_id"
                        + "\nperiod:ONE"
                        + "\ndetail:EVERYDAY 10:20"
                        + "\nquery_string:getQuery_string"
                        + "\n";
        String msg = bean.buildMsg(false);
        assertEquals(msg, testMsg);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfoHelp#buildMsg()} .
     */
    public void testBuildMsgWithUser() {
        bean.setUserName("dba");
        bean.setUserPwd("123456");
        String testMsg2 =
                "query_id:setQuery_id"
                        + "\nusername:dba"
                        + "\nuserpass:123456"
                        + "\nperiod:ONE"
                        + "\ndetail:EVERYDAY 10:20"
                        + "\nquery_string:getQuery_string"
                        + "\n";
        String msg2 = bean.buildMsg(true);
        assertEquals(msg2, testMsg2);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfoHelp#getDetailTimeFromInstance()}
     * .
     */
    public void testGetTime() {
        assertEquals(bean.getDetailTimeFromInstance(), "10:20");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfoHelp#getHour()} .
     */
    public void testGetHour() {
        assertEquals(bean.getHour(), 10);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfoHelp#getMinute()} .
     */
    public void testGetMinute() {
        assertEquals(bean.getMinute(), 20);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfoHelp#getDbname()} .
     */
    public void testGetDbname() {
        bean.setDbname("dbName");
        assertEquals(bean.getDbname(), "dbName");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfoHelp#getDetail()} .
     */
    public void testGetDetail() {
        assertEquals(bean.getDetail(), "EVERYDAY");

        QueryPlanInfo queryPlanInfo = new QueryPlanInfo();
        queryPlanInfo.setDetail("10:20");
        queryPlanInfo.setPeriod("WEEK");
        bean.setQueryPlanInfo(queryPlanInfo);
        bean.setDetail("sunday");
        assertEquals("sunday", bean.getDetail());
        bean.setQueryPlanInfo(queryPlanInfo);
        bean.setDetail("monday");
        assertEquals("monday", bean.getDetail());
        bean.setDetail("tuesday");
        assertEquals("tuesday", bean.getDetail());
        bean.setDetail("wednesday");
        assertEquals("wednesday", bean.getDetail());
        bean.setDetail("thursday");
        assertEquals("thursday", bean.getDetail());
        bean.setDetail("friday");
        assertEquals("friday", bean.getDetail());
        bean.setDetail("saturday");
        assertEquals("saturday", bean.getDetail());
        queryPlanInfo.setPeriod("DAY");
        bean.setDetail("dfdf");
        assertEquals("", bean.getDetail());
        queryPlanInfo.setPeriod("ONE");
        bean.setDetail("2009/08/02");
        assertEquals("2009-08-02", bean.getDetail());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfoHelp#getPeriod()} .
     */
    public void testGetPeriod() {
        assertEquals(bean.getPeriod(), "Special");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfoHelp#getQuery_id()} .
     */
    public void testGetQuery_id() {
        assertEquals(bean.getQuery_id(), "setQuery_id");
        bean.setPeriod("Monthly");
        bean.getPeriod();
        bean.setPeriod("Weekly");
        bean.getPeriod();
        bean.setPeriod("Daily");
        bean.getPeriod();
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfoHelp#getQuery_string()} .
     */
    public void testGetQuery_string() {
        assertEquals(bean.getQuery_string(), "getQuery_string");
    }
}
