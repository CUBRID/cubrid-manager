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
 * Test the type of brokerStatDumpData
 *
 * <p>BrokerDiagDataTest Description
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-3-23 created by lizhiqiang
 */
public class BrokerDiagDataTest extends TestCase {
    //   SetupEnvTestCase {
    private BrokerDiagData bean;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        bean = new BrokerDiagData();
        //		CommonQueryTask<BrokerDiagData> task = new CommonQueryTask<BrokerDiagData>(
        //				serverInfo,
        //				CommonSendMsg.getGetBrokerStatusItems(),
        //				new BrokerDiagData());
        //		task.setBName("query_editor");
        //		task.execute();
        //		BrokerDiagData result =  task.getResultModel();
        //		assertEquals(result.getBname(), "query_editor");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData#getTaskName()} .
     */
    public final void testGetTaskName() {
        assertEquals("getbrokerdiagdata", bean.getTaskName());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData#BrokerDiagData(com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData)}
     * .
     */
    public final void testBrokerDiagDataBrokerDiagData() {
        bean.setCas_mon_req("cas_mon_req");
        BrokerDiagData newBean = new BrokerDiagData(bean);
        assertNotSame(newBean, bean);
        assertEquals(bean.getCas_mon_req(), newBean.getCas_mon_req());
        assertNotNull(newBean.getDiagStatusResultMap());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData#copy_from(com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData)}
     * .
     */
    public final void testCopy_from() {
        bean.setCas_mon_req("cas_mon_req");
        BrokerDiagData newBean = new BrokerDiagData();
        newBean.copyFrom(bean);
        assertNotSame(newBean, bean);
        assertEquals(bean.getCas_mon_req(), newBean.getCas_mon_req());
        assertNotNull(newBean.getDiagStatusResultMap());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData#getDelta(com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData,
     * com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData)} .
     */
    public final void testGetDeltaBrokerDiagDataBrokerDiagData() {
        BrokerDiagData bean_1 = new BrokerDiagData();
        BrokerDiagData bean_2 = new BrokerDiagData();
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

        bean_1.setCas_mon_session("55");
        bean_2.setCas_mon_session("55");

        bean_1.setCas_mon_active("55");
        bean_2.setCas_mon_active("55");

        bean.getDelta(bean_1, bean_2);
        assertEquals("1", bean.getCas_mon_req());
        assertEquals("1", bean.getCas_mon_query());
        assertEquals("1", bean.getCas_mon_tran());
        assertEquals("1", bean.getCas_mon_long_tran());
        assertEquals("1", bean.getCas_mon_long_query());
        assertEquals("1", bean.getCas_mon_error_query());
        assertEquals("54", bean.getCas_mon_act_session());
        assertEquals("55", bean.getCas_mon_session());
        assertEquals("55", bean.getCas_mon_active());

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

        bean_1.setCas_mon_session("55");
        bean_2.setCas_mon_session("a");

        bean_1.setCas_mon_active("55");
        bean_2.setCas_mon_active("a");

        bean.getDelta(bean_1, bean_2);
        assertEquals("0", bean.getCas_mon_req());
        assertEquals("0", bean.getCas_mon_query());
        assertEquals("0", bean.getCas_mon_tran());
        assertEquals("0", bean.getCas_mon_long_tran());
        assertEquals("0", bean.getCas_mon_long_query());
        assertEquals("0", bean.getCas_mon_error_query());
        assertEquals("54", bean.getCas_mon_act_session());
        assertEquals("55", bean.getCas_mon_session());
        assertEquals("55", bean.getCas_mon_active());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData#getDelta(com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData,
     * com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData,
     * com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData)} .
     */
    public final void testGetDeltaBrokerDiagDataBrokerDiagDataBrokerDiagData() {
        BrokerDiagData bean_1 = new BrokerDiagData();
        BrokerDiagData bean_2 = new BrokerDiagData();
        BrokerDiagData bean_3 = new BrokerDiagData();

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

        bean_1.setCas_mon_session("55");
        bean_2.setCas_mon_session("55");
        bean_3.setCas_mon_session("54");

        bean_1.setCas_mon_active("55");
        bean_2.setCas_mon_active("55");
        bean_3.setCas_mon_active("54");

        bean.getDelta(bean_1, bean_2, bean_3);

        assertEquals("1", bean.getCas_mon_req());
        assertEquals("1", bean.getCas_mon_query());
        assertEquals("2", bean.getCas_mon_tran());
        assertEquals("1", bean.getCas_mon_long_tran());
        assertEquals("1", bean.getCas_mon_long_query());
        assertEquals("1", bean.getCas_mon_error_query());
        assertEquals("51", bean.getCas_mon_act_session());
        assertEquals("55", bean.getCas_mon_session());
        assertEquals("55", bean.getCas_mon_active());

        bean_1.setCas_mon_req("a");
        bean_2.setCas_mon_req("53");
        bean_3.setCas_mon_req("52");

        bean.getDelta(bean_1, bean_2, bean_3);
        assertEquals(bean.getCas_mon_req(), "0");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData#getDelta(com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData,
     * com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData,
     * com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData, float)} .
     */
    public final void testGetDeltaBrokerDiagDataBrokerDiagDataBrokerDiagDataFloat() {
        BrokerDiagData bean_1 = new BrokerDiagData();
        BrokerDiagData bean_2 = new BrokerDiagData();
        BrokerDiagData bean_3 = new BrokerDiagData();

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

        bean_1.setCas_mon_session("55");
        bean_2.setCas_mon_session("55");
        bean_3.setCas_mon_session("54");

        bean_1.setCas_mon_active("55");
        bean_2.setCas_mon_active("55");
        bean_3.setCas_mon_active("54");

        bean.getDelta(bean_1, bean_2, bean_3, 0.5f);

        assertEquals("2", bean.getCas_mon_req());
        assertEquals("2", bean.getCas_mon_query());
        assertEquals("4", bean.getCas_mon_tran());
        assertEquals("2", bean.getCas_mon_long_tran());
        assertEquals("2", bean.getCas_mon_long_query());
        assertEquals("2", bean.getCas_mon_error_query());
        assertEquals("51", bean.getCas_mon_act_session());
        assertEquals("55", bean.getCas_mon_session());
        assertEquals("55", bean.getCas_mon_active());

        bean_1.setCas_mon_req("a");
        bean_2.setCas_mon_req("53");
        bean_3.setCas_mon_req("52");

        bean.getDelta(bean_1, bean_2, bean_3, 0.5f);
        assertEquals(bean.getCas_mon_req(), "0");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData#getStatus()} .
     */
    public final void testGetStatus() {
        bean.setStatus("success");
        assertTrue(bean.getStatus());
        bean.setStatus("failue");
        assertFalse(bean.getStatus());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData#getNote()} .
     */
    public final void testGetNote() {
        bean.setNote("note");
        assertEquals("note", bean.getNote());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData#getBname()} .
     */
    public final void testGetBname() {
        bean.setBname("brokerName");
        assertEquals("brokerName", bean.getBname());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData#getCas_mon_req()} .
     */
    public final void testGetCas_mon_req() {
        bean.setCas_mon_req("casMonReq");
        assertEquals("casMonReq", bean.getCas_mon_req());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData#getCas_mon_act_session()} .
     */
    public final void testGetCas_mon_act_session() {
        bean.setCas_mon_act_session("casMonActSession");
        assertEquals("casMonActSession", bean.getCas_mon_act_session());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData#getCas_mon_tran()} .
     */
    public final void testGetCas_mon_tran() {
        bean.setCas_mon_tran("casMonTran");
        assertEquals("casMonTran", bean.getCas_mon_tran());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData#getCas_mon_query()} .
     */
    public final void testGetCas_mon_query() {
        bean.setCas_mon_query("casMonQuery");
        assertEquals("casMonQuery", bean.getCas_mon_query());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData#getCas_mon_long_query()} .
     */
    public final void testGetCas_mon_long_query() {
        bean.setCas_mon_long_query("casMonLongQuery");
        assertEquals("casMonLongQuery", bean.getCas_mon_long_query());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData#getCas_mon_long_tran()} .
     */
    public final void testGetCas_mon_long_tran() {
        bean.setCas_mon_long_tran("casMonLongTran");
        assertEquals("casMonLongTran", bean.getCas_mon_long_tran());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData#getCas_mon_error_query()} .
     */
    public final void testGetCas_mon_error_query() {
        bean.setCas_mon_error_query("casMonErrorQuery");
        assertEquals("casMonErrorQuery", bean.getCas_mon_error_query());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData#getDiagStatusResultMap()} .
     */
    public final void testGetDiagStatusResultMap() {
        Map<IDiagPara, String> map = bean.getDiagStatusResultMap();
        assertFalse(map.isEmpty());
        assertEquals("0", map.get(BrokerDiagEnum.RPS));

        BrokerDiagData bean_1 = new BrokerDiagData();
        BrokerDiagData bean_2 = new BrokerDiagData();
        bean.getDelta(bean_1, bean_2);

        assertFalse(map.isEmpty());
    }
    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData#addBroker()} .
     */
    public final void testAddBroker() {
        BrokerDiagData brokerDiagData = new BrokerDiagData();
        bean.addBroker(brokerDiagData);
        assertFalse(bean.getBrokerList().isEmpty());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData#getSubBrokerByName(String
     * bname)} .
     */
    public final void testGetSubBrokerByNameBname() {
        bean.getSubBrokerByName(null);
        assertTrue(bean.getBrokerList().isEmpty());
        BrokerDiagData brokerDiagData = new BrokerDiagData();
        brokerDiagData.setBname("brokerName");
        bean.addBroker(brokerDiagData);
        BrokerDiagData bdd = bean.getSubBrokerByName("brokerName");
        assertEquals("brokerName", bdd.getBname());
        bdd = bean.getSubBrokerByName("aaaa");
        assertNull(bdd.getBname());
    }
}
