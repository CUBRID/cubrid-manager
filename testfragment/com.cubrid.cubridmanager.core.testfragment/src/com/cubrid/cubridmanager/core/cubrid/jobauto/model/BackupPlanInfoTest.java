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
 * Test BackupPlanInfo
 *
 * @author sq
 * @version 1.0 - 2010-1-4 created by sq
 */
public class BackupPlanInfoTest extends TestCase {
    private BackupPlanInfo bean;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        bean = new BackupPlanInfo();
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.BackupPlanInfo#getDbname()} .
     */
    public void testGetDbname() {
        bean.setDbname("dbname");
        assertEquals(bean.getDbname(), "dbname");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.BackupPlanInfo#getBackupid()} .
     */
    public void testGetBackupid() {
        bean.setBackupid("backupid");
        assertEquals(bean.getBackupid(), "backupid");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.BackupPlanInfo#getPath()} .
     */
    public void testGetPath() {
        bean.setPath("path");
        assertEquals(bean.getPath(), "path");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.BackupPlanInfo#getPeriod_type()} .
     */
    public void testGetPeriod_type() {
        bean.setPeriod_type("period_type");
        assertEquals(bean.getPeriod_type(), "period_type");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.BackupPlanInfo#getPeriod_date()} .
     */
    public void testGetPeriod_date() {
        bean.setPeriod_date("period_date");
        assertEquals(bean.getPeriod_date(), "period_date");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.BackupPlanInfo#getTime()} .
     */
    public void testGetTime() {
        bean.setTime("time");
        assertEquals(bean.getTime(), "time");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.BackupPlanInfo#getArchivedel()} .
     */
    public void testGetArchivedel() {
        bean.setArchivedel("archivedel");
        assertEquals(bean.getArchivedel(), "archivedel");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.BackupPlanInfo#getUpdatestatus()} .
     */
    public void testGetUpdatestatus() {
        bean.setUpdatestatus("updatestatus");
        assertEquals(bean.getUpdatestatus(), "updatestatus");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.BackupPlanInfo#getStoreold()} .
     */
    public void testGetStoreold() {
        bean.setStoreold("storeold");
        assertEquals(bean.getStoreold(), "storeold");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.BackupPlanInfo#getOnoff()} .
     */
    public void testGetOnoff() {
        bean.setOnoff("onoff");
        assertEquals(bean.getOnoff(), "onoff");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.BackupPlanInfo#getZip()} .
     */
    public void testGetZip() {
        bean.setZip("zip");
        assertEquals(bean.getZip(), "zip");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.BackupPlanInfo#getCheck()} .
     */
    public void testGetCheck() {
        bean.setCheck("check");
        assertEquals(bean.getCheck(), "check");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.BackupPlanInfo#getMt()} .
     */
    public void testGetMt() {
        bean.setMt("mt");
        assertEquals(bean.getMt(), "mt");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.jobauto.model.BackupPlanInfo#getLevel()} .
     */
    public void testGetLevel() {
        bean.setLevel("level");
        assertEquals(bean.getLevel(), "level");
    }

    public void testGetBknum() {
        bean.setBknum("9");
        assertEquals(bean.getBknum(), "9");
    }
}
