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
 * Test the type of DbProcStatDlgt
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-6-18 created by lizhiqiang
 */
public class DbProcStatProxyTest extends TestCase {
    DbProcStatProxy bean;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        bean = new DbProcStatProxy();
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbProcStatProxy#DbProcStatDlgt()} .
     */
    public void testDbProcStatDlgt() {
        assertEquals("0", bean.getMemPhysical());
        assertEquals("0", bean.getMemVirtual());
        assertNotNull(bean.getDiagStatusResultMap());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbProcStatProxy#compute(java.lang.String,
     * com.cubrid.cubridmanager.core.monitoring.model.DbProcStat,
     * com.cubrid.cubridmanager.core.monitoring.model.DbProcStat)} .
     */
    public void testComputeStringDbProcStatDbProcStat() {
        DbProcStat beanA = new DbProcStat();
        DbSysStat dspA = new DbSysStat();
        dspA.setDbname("a");
        dspA.setCpu_user("100");
        dspA.setCpu_kernel("100");
        dspA.setMem_physical("10240");
        dspA.setMem_virtual("10240");
        beanA.addDbstat(dspA);
        DbProcStat beanB = new DbProcStat();
        DbSysStat dspB = new DbSysStat();
        dspB.setCpu_user("90");
        dspB.setCpu_kernel("90");
        dspB.setMem_physical("5160");
        dspB.setMem_virtual("5160");
        dspB.setDbname("a");
        beanB.addDbstat(dspB);

        bean.compute("a", beanA, beanB);
        assertEquals("10", bean.getDiagStatusResultMap().get(DbProcStatEnum.DELTA_USER));
        assertEquals("10", bean.getDiagStatusResultMap().get(DbProcStatEnum.DELTA_KERNEL));
        assertEquals("10", bean.getMemPhysical());
        assertEquals("10", bean.getMemVirtual());

        bean.compute("", beanA, beanB);
        assertEquals("10", bean.getMemPhysical());
        assertEquals("10", bean.getMemVirtual());

        //
        beanA = new DbProcStat();
        bean.compute("", beanA, beanB);
        assertEquals("0", bean.getDiagStatusResultMap().get(DbProcStatEnum.DELTA_USER));
        assertEquals("0", bean.getDiagStatusResultMap().get(DbProcStatEnum.DELTA_KERNEL));

        //
        dspA = new DbSysStat();
        dspA.setDbname("a");
        dspA.setCpu_user("100");
        dspA.setCpu_kernel("100");
        dspA.setMem_physical("10240");
        dspA.setMem_virtual("10240");
        beanA.addDbstat(dspA);

        beanB = new DbProcStat();
        dspB = new DbSysStat();
        dspB.setCpu_user("90");
        dspB.setCpu_kernel("90");
        dspB.setMem_physical("5160");
        dspB.setMem_virtual("5160");
        dspB.setDbname("b");
        beanB.addDbstat(dspB);
        bean.compute("", beanA, beanB);
        assertEquals("0", bean.getDiagStatusResultMap().get(DbProcStatEnum.DELTA_USER));
        assertEquals("0", bean.getDiagStatusResultMap().get(DbProcStatEnum.DELTA_KERNEL));
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbProcStatProxy#compute(java.lang.String,
     * com.cubrid.cubridmanager.core.monitoring.model.DbProcStat,
     * com.cubrid.cubridmanager.core.monitoring.model.DbProcStat,
     * com.cubrid.cubridmanager.core.monitoring.model.DbProcStat)} .
     */
    public void testComputeStringDbProcStatDbProcStatDbProcStat() {
        DbProcStat beanA = new DbProcStat();
        DbSysStat dspA = new DbSysStat();
        dspA.setDbname("a");
        dspA.setCpu_user("100");
        dspA.setCpu_kernel("100");
        dspA.setMem_physical("10240");
        dspA.setMem_virtual("10240");
        beanA.addDbstat(dspA);
        DbProcStat beanB = new DbProcStat();
        DbSysStat dspB = new DbSysStat();
        dspB.setCpu_user("90");
        dspB.setCpu_kernel("90");
        dspB.setMem_physical("5160");
        dspB.setMem_virtual("5160");
        dspB.setDbname("a");
        beanB.addDbstat(dspB);

        DbProcStat beanC = new DbProcStat();
        DbSysStat dspC = new DbSysStat();
        dspC.setCpu_user("80");
        dspC.setCpu_kernel("80");
        dspC.setMem_physical("4048");
        dspC.setMem_virtual("4048");
        dspC.setDbname("a");
        beanC.addDbstat(dspC);

        bean.compute("a", beanA, beanB, beanC);
        assertEquals("10", bean.getMemPhysical());
        assertEquals("10", bean.getMemVirtual());

        bean.compute("", beanA, beanB, beanC);
        assertEquals("10", bean.getMemPhysical());
        assertEquals("10", bean.getMemVirtual());

        //
        dspA = new DbSysStat();
        dspA.setDbname("a");
        dspA.setCpu_user("100");
        dspA.setCpu_kernel("100");
        dspA.setMem_physical("10240");
        dspA.setMem_virtual("10240");
        beanA.addDbstat(dspA);

        beanB = new DbProcStat();
        dspB = new DbSysStat();
        dspB.setCpu_user("90");
        dspB.setCpu_kernel("90");
        dspB.setMem_physical("5160");
        dspB.setMem_virtual("5160");
        dspB.setDbname("b");
        beanB.addDbstat(dspB);
        bean.compute("", beanA, beanB, beanC);
        assertEquals("0", bean.getDiagStatusResultMap().get(DbProcStatEnum.DELTA_USER));
        assertEquals("0", bean.getDiagStatusResultMap().get(DbProcStatEnum.DELTA_KERNEL));
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbProcStatProxy#getDeltaUserCpu(java.lang.String,
     * com.cubrid.cubridmanager.core.monitoring.model.DbProcStat,
     * com.cubrid.cubridmanager.core.monitoring.model.DbProcStat)} .
     */
    public void testGetDeltaUserCputStringDbProcStatDbProcStat() {
        DbProcStat beanA = new DbProcStat();
        DbSysStat dspA = new DbSysStat();
        dspA.setDbname("a");
        dspA.setCpu_user("100");
        dspA.setCpu_kernel("100");
        beanA.addDbstat(dspA);
        DbProcStat beanB = new DbProcStat();
        DbSysStat dspB = new DbSysStat();
        dspB.setCpu_user("90");
        dspB.setCpu_kernel("90");
        dspB.setDbname("a");
        beanB.addDbstat(dspB);

        DbSysStat dspA1 = new DbSysStat();
        dspA1.setDbname("b");
        dspA1.setCpu_user("200");
        dspA1.setCpu_kernel("200");
        beanA.addDbstat(dspA1);

        DbSysStat dspB1 = new DbSysStat();
        dspB1.setDbname("b");
        dspB1.setCpu_user("150");
        dspB1.setCpu_kernel("150");
        beanB.addDbstat(dspB1);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbProcStatProxy#getDeltaKernelCpu(java.lang.String,
     * com.cubrid.cubridmanager.core.monitoring.model.DbProcStat,
     * com.cubrid.cubridmanager.core.monitoring.model.DbProcStat)} .
     */
    public void testGetDeltaKernelCpuStringDbProcStatDbProcStat() {
        DbProcStat beanA = new DbProcStat();
        DbSysStat dspA = new DbSysStat();
        dspA.setDbname("a");
        dspA.setCpu_user("100");
        dspA.setCpu_kernel("100");
        beanA.addDbstat(dspA);
        DbProcStat beanB = new DbProcStat();
        DbSysStat dspB = new DbSysStat();
        dspB.setCpu_user("90");
        dspB.setCpu_kernel("90");
        dspB.setDbname("a");
        beanB.addDbstat(dspB);

        DbSysStat dspA1 = new DbSysStat();
        dspA1.setDbname("b");
        dspA1.setCpu_user("200");
        dspA1.setCpu_kernel("200");
        beanA.addDbstat(dspA1);

        DbSysStat dspB1 = new DbSysStat();
        dspB1.setDbname("b");
        dspB1.setCpu_user("150");
        dspB1.setCpu_kernel("150");
        beanB.addDbstat(dspB1);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbProcStatProxy#getCpuUserPercent(java.lang.String,
     * com.cubrid.cubridmanager.core.monitoring.model.DbProcStat,
     * com.cubrid.cubridmanager.core.monitoring.model.DbProcStat,
     * com.cubrid.cubridmanager.core.monitoring.model.DbProcStat)} .
     */
    public void testGetDeltaUserCpuStringDbProcStatDbProcStatDbProcStat() {
        DbProcStat beanA = new DbProcStat();
        DbSysStat dspA = new DbSysStat();
        dspA.setDbname("a");
        dspA.setCpu_user("100");
        dspA.setCpu_kernel("100");
        beanA.addDbstat(dspA);
        DbProcStat beanB = new DbProcStat();
        DbSysStat dspB = new DbSysStat();
        dspB.setCpu_user("90");
        dspB.setCpu_kernel("90");
        dspB.setDbname("a");
        beanB.addDbstat(dspB);

        DbProcStat beanC = new DbProcStat();
        DbSysStat dspC = new DbSysStat();
        dspC.setDbname("a");
        dspC.setCpu_user("85");
        dspC.setCpu_kernel("85");
        beanC.addDbstat(dspC);

        DbSysStat dspA1 = new DbSysStat();
        dspA1.setDbname("b");
        dspA1.setCpu_user("200");
        dspA1.setCpu_kernel("200");
        beanA.addDbstat(dspA1);

        DbSysStat dspB1 = new DbSysStat();
        dspB1.setDbname("b");
        dspB1.setCpu_user("150");
        dspB1.setCpu_kernel("150");
        beanB.addDbstat(dspB1);

        DbSysStat dspC1 = new DbSysStat();
        dspC1.setDbname("b");
        dspC1.setCpu_user("150");
        dspC1.setCpu_kernel("150");
        beanC.addDbstat(dspC1);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbProcStatProxy#getCpuKernelPercent(java.lang.String,
     * com.cubrid.cubridmanager.core.monitoring.model.DbProcStat,
     * com.cubrid.cubridmanager.core.monitoring.model.DbProcStat,
     * com.cubrid.cubridmanager.core.monitoring.model.DbProcStat)} .
     */
    public void testGetDeltaKernelCpuStringDbProcStatDbProcStatDbProcStat() {
        DbProcStat beanA = new DbProcStat();
        DbSysStat dspA = new DbSysStat();
        dspA.setDbname("a");
        dspA.setCpu_user("100");
        dspA.setCpu_kernel("100");
        beanA.addDbstat(dspA);
        DbProcStat beanB = new DbProcStat();
        DbSysStat dspB = new DbSysStat();
        dspB.setCpu_user("90");
        dspB.setCpu_kernel("90");
        dspB.setDbname("a");
        beanB.addDbstat(dspB);

        DbProcStat beanC = new DbProcStat();
        DbSysStat dspC = new DbSysStat();
        dspC.setDbname("a");
        dspC.setCpu_user("85");
        dspC.setCpu_kernel("85");
        beanC.addDbstat(dspC);

        DbSysStat dspA1 = new DbSysStat();
        dspA1.setDbname("b");
        dspA1.setCpu_user("200");
        dspA1.setCpu_kernel("200");
        beanA.addDbstat(dspA1);

        DbSysStat dspB1 = new DbSysStat();
        dspB1.setDbname("b");
        dspB1.setCpu_user("150");
        dspB1.setCpu_kernel("150");
        beanB.addDbstat(dspB1);

        DbSysStat dspC1 = new DbSysStat();
        dspC1.setDbname("b");
        dspC1.setCpu_user("150");
        dspC1.setCpu_kernel("150");
        beanC.addDbstat(dspC1);
    }
}
