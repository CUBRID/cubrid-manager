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
 * Test the type of HostStatDataProxy
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-6-17 created by lizhiqiang
 */
public class HostStatDataDlgtTest extends TestCase {
    HostStatDataProxy bean;
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        bean = new HostStatDataProxy();
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatDataProxy#HostStatDataDlgt()}.
     */
    public void testHostStatDataDlgt() {
        assertEquals("0", bean.getUserPercent());
        assertEquals("0", bean.getKernelPercent());
        assertEquals("0", bean.getIowaitPercent());
        assertEquals("0", bean.getMemPhyTotal());
        assertEquals("0", bean.getMemPhyUsed());
        assertEquals("0", bean.getMemSwapTotal());
        assertEquals("0", bean.getMemSwapUsed());
        assertNotNull(bean.getDiagStatusResultMap());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatDataProxy#compute(com.cubrid.cubridmanager.core.monitoring.model.HostStatData,
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatData)}.
     */
    public void testComputeHostStatDataHostStatData() {
        HostStatData bean_0 = new HostStatData();
        bean_0.setCpu_user("110");
        bean_0.setCpu_idle("110");
        bean_0.setCpu_iowait("110");
        bean_0.setCpu_kernel("110");
        bean_0.setMem_phy_total("51200");
        bean_0.setMem_swap_total("51200");
        bean_0.setMem_phy_free("10240");
        bean_0.setMem_phy_free("10240");

        HostStatData bean_1 = new HostStatData();
        bean_1.setCpu_user("100");
        bean_1.setCpu_idle("100");
        bean_1.setCpu_iowait("100");
        bean_1.setCpu_kernel("100");
        bean_1.setMem_phy_total("51200");
        bean_1.setMem_swap_total("51200");
        bean_1.setMem_phy_free("10240");
        bean_1.setMem_phy_free("10240");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatDataProxy#compute(com.cubrid.cubridmanager.core.monitoring.model.HostStatData,
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatData,
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatData)}.
     */
    public void testComputeHostStatDataHostStatDataHostStatData() {}

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatDataProxy#getCpuUserPercent(com.cubrid.cubridmanager.core.monitoring.model.HostStatData,
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatData)}.
     */
    public void testGetCpuUserPercentHostStatDataHostStatData() {
        HostStatData bean_0 = new HostStatData();
        bean_0.setCpu_user("110");
        bean_0.setCpu_idle("110");
        bean_0.setCpu_iowait("110");
        bean_0.setCpu_kernel("110");

        HostStatData bean_1 = new HostStatData();
        bean_1.setCpu_user("100");
        bean_1.setCpu_idle("100");
        bean_1.setCpu_iowait("100");
        bean_1.setCpu_kernel("100");

        int percent = bean.getCpuUserPercent(bean_0, bean_1);
        assertEquals(25, percent);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatDataProxy#getCpuUserPercent(com.cubrid.cubridmanager.core.monitoring.model.HostStatData,
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatData,
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatData)}.
     */
    public void testGetCpuUserPercentHostStatDataHostStatDataHostStatData() {
        HostStatData bean_0 = new HostStatData();
        bean_0.setCpu_user("110");
        bean_0.setCpu_idle("110");
        bean_0.setCpu_iowait("110");
        bean_0.setCpu_kernel("110");

        HostStatData bean_1 = new HostStatData();
        bean_1.setCpu_user("100");
        bean_1.setCpu_idle("100");
        bean_1.setCpu_iowait("100");
        bean_1.setCpu_kernel("100");

        HostStatData bean_2 = new HostStatData();
        bean_2.setCpu_user("90");
        bean_2.setCpu_idle("90");
        bean_2.setCpu_iowait("90");
        bean_2.setCpu_kernel("90");

        int percent = bean.getCpuUserPercent(bean_0, bean_1, bean_2);
        assertEquals(25, percent);

        bean_0.setCpu_user("95");
        bean_0.setCpu_idle("95");
        bean_0.setCpu_iowait("95");
        bean_0.setCpu_kernel("95");

        percent = bean.getCpuUserPercent(bean_0, bean_1, bean_2);
        assertEquals(25, percent);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatDataProxy#getCpuKernelPercent(com.cubrid.cubridmanager.core.monitoring.model.HostStatData,
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatData)}.
     */
    public void testGetCpuKernelPercentHostStatDataHostStatData() {
        HostStatData bean_0 = new HostStatData();
        bean_0.setCpu_user("110");
        bean_0.setCpu_idle("110");
        bean_0.setCpu_iowait("110");
        bean_0.setCpu_kernel("110");

        HostStatData bean_1 = new HostStatData();
        bean_1.setCpu_user("100");
        bean_1.setCpu_idle("100");
        bean_1.setCpu_iowait("100");
        bean_1.setCpu_kernel("100");

        int percent = bean.getCpuKernelPercent(bean_0, bean_1);
        assertEquals(25, percent);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatDataProxy#getCpuKernelPercent(com.cubrid.cubridmanager.core.monitoring.model.HostStatData,
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatData,
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatData)}.
     */
    public void testGetCpuKernelPercentHostStatDataHostStatDataHostStatData() {
        HostStatData bean_0 = new HostStatData();
        bean_0.setCpu_user("110");
        bean_0.setCpu_idle("110");
        bean_0.setCpu_iowait("110");
        bean_0.setCpu_kernel("110");

        HostStatData bean_1 = new HostStatData();
        bean_1.setCpu_user("100");
        bean_1.setCpu_idle("100");
        bean_1.setCpu_iowait("100");
        bean_1.setCpu_kernel("100");

        HostStatData bean_2 = new HostStatData();
        bean_2.setCpu_user("90");
        bean_2.setCpu_idle("90");
        bean_2.setCpu_iowait("90");
        bean_2.setCpu_kernel("90");

        int percent = bean.getCpuKernelPercent(bean_0, bean_1, bean_2);
        assertEquals(25, percent);

        bean_0.setCpu_user("95");
        bean_0.setCpu_idle("95");
        bean_0.setCpu_iowait("95");
        bean_0.setCpu_kernel("95");

        percent = bean.getCpuKernelPercent(bean_0, bean_1, bean_2);
        assertEquals(25, percent);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatDataProxy#getCpuIOwaitPercent(com.cubrid.cubridmanager.core.monitoring.model.HostStatData,
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatData)}.
     */
    public void testGetCpuIOwaitPercentHostStatDataHostStatData() {
        HostStatData bean_0 = new HostStatData();
        bean_0.setCpu_user("110");
        bean_0.setCpu_idle("110");
        bean_0.setCpu_iowait("110");
        bean_0.setCpu_kernel("110");

        HostStatData bean_1 = new HostStatData();
        bean_1.setCpu_user("100");
        bean_1.setCpu_idle("100");
        bean_1.setCpu_iowait("100");
        bean_1.setCpu_kernel("100");

        int percent = bean.getCpuIOwaitPercent(bean_0, bean_1);
        assertEquals(25, percent);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatDataProxy#getCpuIOwaitPercent(com.cubrid.cubridmanager.core.monitoring.model.HostStatData,
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatData,
     * com.cubrid.cubridmanager.core.monitoring.model.HostStatData)}.
     */
    public void testGetCpuIOwaitPercentHostStatDataHostStatDataHostStatData() {
        HostStatData bean_0 = new HostStatData();
        bean_0.setCpu_user("110");
        bean_0.setCpu_idle("110");
        bean_0.setCpu_iowait("110");
        bean_0.setCpu_kernel("110");

        HostStatData bean_1 = new HostStatData();
        bean_1.setCpu_user("100");
        bean_1.setCpu_idle("100");
        bean_1.setCpu_iowait("100");
        bean_1.setCpu_kernel("100");

        HostStatData bean_2 = new HostStatData();
        bean_2.setCpu_user("90");
        bean_2.setCpu_idle("90");
        bean_2.setCpu_iowait("90");
        bean_2.setCpu_kernel("90");

        int percent = bean.getCpuIOwaitPercent(bean_0, bean_1, bean_2);
        assertEquals(25, percent);

        bean_0.setCpu_user("95");
        bean_0.setCpu_idle("95");
        bean_0.setCpu_iowait("95");
        bean_0.setCpu_kernel("95");

        percent = bean.getCpuIOwaitPercent(bean_0, bean_1, bean_2);
        assertEquals(25, percent);
    }
}
