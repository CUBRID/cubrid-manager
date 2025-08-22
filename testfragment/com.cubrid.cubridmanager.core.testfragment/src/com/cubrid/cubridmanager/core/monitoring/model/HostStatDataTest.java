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
 * Test the type of HostStatData
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-6-4 created by lizhiqiang
 */
public class HostStatDataTest extends TestCase { // SetupEnvTestCase {

    HostStatData bean;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        bean = new HostStatData();
        //		final CommonQueryTask<HostStatData> task = new CommonQueryTask<HostStatData>(
        //				serverInfo, CommonSendMsg.getCommonSimpleSendMsg(), bean);
        //		task.execute();
        //		bean = task.getResultModel();
        //      System.out.println(bean.getCpu_user());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatData#HostStatData()} .
     */
    public void testHostStatData() {
        assertEquals("0", bean.getCpu_user());
        assertEquals("0", bean.getCpu_idle());
        assertEquals("0", bean.getCpu_kernel());
        assertEquals("0", bean.getCpu_iowait());
        assertEquals("0", bean.getMem_phy_free());
        assertEquals("0", bean.getMem_phy_total());
        assertEquals("0", bean.getMem_swap_free());
        assertEquals("0", bean.getMem_swap_total());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatData#copy_from(com.cubrid.cubridmanager.core.monitoring.model.HostStatData)}
     * .
     */
    public void testCopy_from() {
        HostStatData bean2 = new HostStatData();
        bean.copyFrom(bean2);
        assertNotSame(bean, bean2);
        assertEquals(bean.getCpu_user(), bean2.getCpu_user());
        assertEquals(bean.getCpu_idle(), bean2.getCpu_idle());
        assertEquals(bean.getCpu_kernel(), bean2.getCpu_kernel());
        assertEquals(bean.getCpu_iowait(), bean2.getCpu_iowait());
        assertEquals(bean.getMem_phy_free(), bean2.getMem_phy_free());
        assertEquals(bean.getMem_phy_total(), bean2.getMem_phy_total());
        assertEquals(bean.getMem_swap_free(), bean2.getMem_swap_free());
        assertEquals(bean.getMem_swap_total(), bean2.getMem_swap_total());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatData#getTaskName()} .
     */
    public void testGetTaskName() {
        assertEquals("gethoststat", bean.getTaskName());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatData#getStatus()} .
     */
    public void testGetStatus() {
        bean.setStatus("success");
        assertEquals(true, bean.getStatus());
        bean.setStatus("fa");
        assertFalse(bean.getStatus());
    }

    /**
     * Test method for {@link com.cubrid.cubridmanager.core.monitoring.model.HostStatData#getNote()}
     * .
     */
    public void testGetNote() {
        bean.setNote("note");
        assertEquals("note", bean.getNote());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatData#getCpu_user()} .
     */
    public void testGetCpu_user() {
        bean.setCpu_user("100");
        assertEquals("100", bean.getCpu_user());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatData#getCpu_kernel()} .
     */
    public void testGetCpu_kernel() {
        bean.setCpu_kernel("100");
        assertEquals("100", bean.getCpu_kernel());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatData#getCpu_idle()} .
     */
    public void testGetCpu_idle() {
        bean.setCpu_idle("100");
        assertEquals("100", bean.getCpu_idle());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatData#getCpu_iowait()} .
     */
    public void testGetCpu_iowait() {
        bean.setCpu_iowait("100");
        assertEquals("100", bean.getCpu_iowait());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatData#getMem_phy_total()} .
     */
    public void testGetMem_phy_total() {
        bean.setMem_phy_total("100");
        assertEquals("100", bean.getMem_phy_total());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatData#getMem_phy_free()} .
     */
    public void testGetMem_phy_free() {
        bean.setMem_phy_free("100");
        assertEquals("100", bean.getMem_phy_free());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatData#getMem_swap_total()} .
     */
    public void testGetMem_swap_total() {
        bean.setMem_swap_total("100");
        assertEquals("100", bean.getMem_swap_total());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatData#getMem_swap_free()} .
     */
    public void testGetMem_swap_free() {
        bean.setMem_swap_free("100");
        assertEquals("100", bean.getMem_swap_free());
    }
}
