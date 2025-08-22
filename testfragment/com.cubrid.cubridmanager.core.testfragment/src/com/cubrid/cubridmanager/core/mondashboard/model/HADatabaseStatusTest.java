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

import java.util.ArrayList;
import junit.framework.TestCase;

/**
 * Test HADatabaseStatus
 *
 * @author pangqiren
 * @version 1.0 - 2010-6-7 created by pangqiren
 */
public class HADatabaseStatusTest extends TestCase {

    private HADatabaseStatusInfo bean;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        bean = new HADatabaseStatusInfo();
        bean.setDbName("dbName");
        bean.setStatusType(DBStatusType.ACTIVE);
        bean.setErrorInfo("Error information");
        bean.setDelay(100);
        bean.setInsertCounter(200);
        bean.setUpdateCounter(300);
        bean.setDeleteCounter(400);
        bean.setCommitCounter(500);
        bean.setFailCounter(600);
        bean.setCpuUsage(700);
        bean.setMemUsage(800);
        DbProcessStatusInfo statusInfo = new DbProcessStatusInfo();
        statusInfo.setDbName("demodb1");
        bean.setDbServerProcessStatus(statusInfo);

        statusInfo = new DbProcessStatusInfo();
        statusInfo.setDbName("demodb2");
        bean.addApplyLogDbProcessStatus(statusInfo);

        statusInfo = new DbProcessStatusInfo();
        statusInfo.setDbName("demodb3");
        bean.addCopyLogDbProcessStatus(statusInfo);

        bean.setApplyLogDbProcessStatusList(new ArrayList<DbProcessStatusInfo>());
        bean.setCopyLogDbProcessStatusList(new ArrayList<DbProcessStatusInfo>());

        bean.setHaHostStatusInfo(new HAHostStatusInfo());
    }

    public void testGetDbName() {
        assertEquals(bean.getDbName(), "dbName");
    }

    public void testGetStatusType() {
        assertEquals(bean.getStatusType(), DBStatusType.ACTIVE);
    }

    public void testGetErrorInfo() {
        assertEquals(bean.getErrorInfo(), "Error information");
    }

    public void testGetDelay() {
        assertEquals(bean.getDelay(), 100);
    }

    public void testGetCopyLogDbProcessStatus() {
        assertTrue(bean.getCopyLogDbProcessStatusList().size() == 0);
        bean.addApplyLogDbProcessStatus(null);
        assertNotNull(bean.getCopyLogDbProcessStatusList());
    }

    public void testGetApplyLogDbProcessStatus() {
        assertTrue(bean.getApplyLogDbProcessStatusList().size() == 0);
    }

    public void testGetDbServerProcessStatus() {
        assertTrue(bean.getDbServerProcessStatus() != null);
    }

    public void testGetInsertCounter() {
        assertEquals(bean.getInsertCounter(), 200);
    }

    public void testGetUpdateCounter() {
        assertEquals(bean.getUpdateCounter(), 300);
    }

    public void testGetDeleteCounter() {
        assertEquals(bean.getDeleteCounter(), 400);
    }

    public void testGetCommitCounter() {
        assertEquals(bean.getCommitCounter(), 500);
    }

    public void testGetFailCounter() {
        assertEquals(bean.getFailCounter(), 600);
    }

    public void testGetCpuUsage() {
        assertEquals(bean.getCpuUsage(), 700);
    }

    public void testGetMemUsage() {
        assertEquals(bean.getMemUsage(), 800);
    }

    public void testHaHostStatusInfo() {
        assertTrue(bean.getHaHostStatusInfo() != null);
    }
}
