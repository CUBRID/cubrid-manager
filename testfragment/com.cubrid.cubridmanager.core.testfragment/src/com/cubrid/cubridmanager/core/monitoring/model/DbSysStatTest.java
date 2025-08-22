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
 * Test the type of DbSysStat
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-6-4 created by lizhiqiang
 */
public class DbSysStatTest extends TestCase {
    DbSysStat bean;

    /** setUP */
    protected void setUp() throws Exception {
        super.setUp();
        bean = new DbSysStat();
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbSysStatTest#DbSysStat()} .
     */
    public void testDbSysStat() {
        assertEquals("0", bean.getCpu_kernel());
        assertEquals("0", bean.getCpu_user());
        assertEquals("0", bean.getMem_physical());
        assertEquals("0", bean.getMem_virtual());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbSysStatTest#copyFrom(com.cubrid.cubridmanager.core.monitoring.model.DbSysStatTest)}
     * .
     */
    public void testCopyFrom() {
        DbSysStat bean_1 = new DbSysStat();
        bean_1.setCpu_kernel("100");
        bean_1.setCpu_user("100");
        bean_1.setMem_physical("100");
        bean_1.setMem_virtual("100");
        bean.copyFrom(bean_1);
        assertNotSame(bean, bean_1);
        assertEquals(bean.getCpu_kernel(), bean_1.getCpu_kernel());
        assertEquals(bean.getCpu_user(), bean_1.getCpu_user());
        assertEquals(bean.getMem_physical(), bean_1.getMem_physical());
        assertEquals(bean.getMem_virtual(), bean_1.getMem_virtual());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbSysStatTest#getCpuUserPercent(com.cubrid.cubridmanager.core.monitoring.model.DbSysStatTest,
     * com.cubrid.cubridmanager.core.monitoring.model.DbSysStatTest)} .
     */
    public void testGetCpuPercentDbSysStatDbSysStat() {
        DbSysStat bean_0 = new DbSysStat();
        bean_0.setCpu_user("110");
        bean_0.setCpu_kernel("110");

        DbSysStat bean_1 = new DbSysStat();
        bean_1.setCpu_user("100");
        bean_1.setCpu_kernel("100");

        int percent = bean.getCpuUserPercent(bean_0, bean_1);
        assertEquals(50, percent);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbSysStatTest#getCpuUserPercent(com.cubrid.cubridmanager.core.monitoring.model.DbSysStatTest,
     * com.cubrid.cubridmanager.core.monitoring.model.DbSysStatTest,
     * com.cubrid.cubridmanager.core.monitoring.model.DbSysStatTest)} .
     */
    public void testGetCpuPercentDbSysStatDbSysStatDbSysStat() {
        DbSysStat bean_0 = new DbSysStat();
        bean_0.setCpu_user("110");
        bean_0.setCpu_kernel("110");

        DbSysStat bean_1 = new DbSysStat();
        bean_1.setCpu_user("100");
        bean_1.setCpu_kernel("100");

        DbSysStat bean_2 = new DbSysStat();
        bean_2.setCpu_user("90");
        bean_2.setCpu_kernel("90");

        int percent = bean.getCpuUserPercent(bean_0, bean_1, bean_2);
        assertEquals(50, percent);

        bean_0.setCpu_user("95");
        bean_0.setCpu_kernel("95");

        percent = bean.getCpuUserPercent(bean_0, bean_1, bean_2);
        assertEquals(50, percent);

        long nearMin = Long.MIN_VALUE + 5;
        long nearMax = Long.MAX_VALUE - 5;
        bean_0.setCpu_user(Long.toString(nearMin));
        bean_0.setCpu_kernel(Long.toString(nearMin));
        bean_1.setCpu_user(Long.toString(nearMax));
        bean_1.setCpu_kernel(Long.toString(nearMax));

        percent = bean.getCpuUserPercent(bean_0, bean_1, bean_2);
        assertEquals(50, percent);

        bean_0.setCpu_user("0");
        bean_0.setCpu_kernel("0");
        bean_1.setCpu_user("100");
        bean_1.setCpu_kernel("100");
        bean_2.setCpu_user("90");
        bean_2.setCpu_kernel("90");
        percent = bean.getCpuUserPercent(bean_0, bean_1, bean_2);
        assertEquals(50, percent);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbSysStatTest#getCpuKernelPercent(com.cubrid.cubridmanager.core.monitoring.model.DbSysStatTest,
     * com.cubrid.cubridmanager.core.monitoring.model.DbSysStatTest)} .
     */
    public void testGetKernelPercentDbSysStatDbSysStat() {
        DbSysStat bean_0 = new DbSysStat();
        bean_0.setCpu_user("110");
        bean_0.setCpu_kernel("110");

        DbSysStat bean_1 = new DbSysStat();
        bean_1.setCpu_user("100");
        bean_1.setCpu_kernel("100");

        int percent = bean.getCpuKernelPercent(bean_0, bean_1);
        assertEquals(50, percent);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbSysStatTest#getCpuUserPercent(com.cubrid.cubridmanager.core.monitoring.model.DbSysStatTest,
     * com.cubrid.cubridmanager.core.monitoring.model.DbSysStatTest,
     * com.cubrid.cubridmanager.core.monitoring.model.DbSysStatTest)} .
     */
    public void testGetKernelPercentDbSysStatDbSysStatDbSysStat() {
        DbSysStat bean_0 = new DbSysStat();
        bean_0.setCpu_user("110");
        bean_0.setCpu_kernel("110");

        DbSysStat bean_1 = new DbSysStat();
        bean_1.setCpu_user("100");
        bean_1.setCpu_kernel("100");

        DbSysStat bean_2 = new DbSysStat();
        bean_2.setCpu_user("90");
        bean_2.setCpu_kernel("90");

        int percent = bean.getCpuKernelPercent(bean_0, bean_1, bean_2);
        assertEquals(50, percent);

        bean_0.setCpu_user("95");
        bean_0.setCpu_kernel("95");

        percent = bean.getCpuKernelPercent(bean_0, bean_1, bean_2);
        assertEquals(50, percent);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbSysStatTest#getDbname()} .
     */
    public void testGetDbname() {
        bean.setDbname("dbname");
        assertEquals("dbname", bean.getDbname());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbSysStatTest#getCpu_kernel()} .
     */
    public void testGetCpu_kernel() {
        bean.setCpu_kernel("100");
        assertEquals("100", bean.getCpu_kernel());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbSysStatTest#getCpu_user()} .
     */
    public void testGetCpu_user() {
        bean.setCpu_user("100");
        assertEquals("100", bean.getCpu_user());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbSysStatTest#getMem_physical()} .
     */
    public void testGetMem_physical() {
        bean.setMem_physical("100");
        assertEquals("100", bean.getMem_physical());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbSysStatTest#getMem_virtual()} .
     */
    public void testGetMem_virtual() {
        bean.setMem_virtual("100");
        assertEquals("100", bean.getMem_virtual());
    }
    /**
     * Test method for {@link com.cubrid.cubridmanager.core.monitoring.model.DbSysStatTest#equals()}
     * .
     */
    public void testEqual() {
        bean = new DbSysStat();
        boolean returnVal = bean.equals(bean);
        assertTrue(returnVal);
        returnVal = bean.equals(null);
        assertFalse(returnVal);

        DbSysStat bean_0 = new DbSysStat();
        bean_0.setDbname("dbname");
        returnVal = bean.equals(bean_0);
        assertFalse(returnVal);

        bean.setDbname("name");
        returnVal = bean.equals(bean_0);
        assertFalse(returnVal);
        bean.setDbname("dbname");
        returnVal = bean.equals(bean_0);
        assertTrue(returnVal);
    }
}
