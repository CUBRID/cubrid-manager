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
 * Test HAHostStatusInfo
 *
 * @author pangqiren
 * @version 1.0 - 2010-6-9 created by pangqiren
 */
public class HAHostStatusInfoTest extends TestCase {

    private HAHostStatusInfo bean;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        bean = new HAHostStatusInfo();
        bean.setHostName("dbms3");
        bean.setIp("192.168.1.89");
        bean.setStatusType(HostStatusType.MASTER);
        bean.setPriority("1");
        bean.setCpuUsage(50);
        bean.setMemUsage(60);
        bean.setIoWait(80);
        bean.setMasterHostStatusInfo(new HAHostStatusInfo());
        bean.setDbStatusList(new ArrayList<HADatabaseStatusInfo>());
        HADatabaseStatusInfo dbStatus = new HADatabaseStatusInfo();
        dbStatus.setDbName("dbName");
        bean.addHADatabaseStatus(dbStatus);
        bean.setSlaveHostStatusInfoList(new ArrayList<HAHostStatusInfo>());
    }

    public void testGetIp() {
        assertEquals(bean.getHostName(), "dbms3");
    }

    public void testGetHostName() {
        assertEquals(bean.getIp(), "192.168.1.89");
    }

    public void testGetPriority() {
        assertEquals(bean.getPriority(), "1");
    }

    public void testGetStatusType() {
        assertEquals(bean.getStatusType(), HostStatusType.MASTER);
    }

    public void testGetCpuUsage() {
        assertEquals(bean.getCpuUsage(), 50);
    }

    public void testGetMemUsage() {
        assertEquals(bean.getMemUsage(), 60);
    }

    public void testGetIoWait() {
        assertEquals(bean.getIoWait(), 80);
    }

    public void testGetMasterHostStatusInfo() {
        assertTrue(bean.getMasterHostStatusInfo() != null);
    }

    public void testGetSlaveHostStatusInfoList() {
        assertTrue(bean.getSlaveHostStatusInfoList() != null);
    }

    public void testGetDbStatusList() {
        assertTrue(bean.getDbStatusList().size() > 0);
    }
}
