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
package com.cubrid.cubridmanager.core.cubrid.dbspace.model;

import junit.framework.TestCase;

/**
 * Test AddVolumeDbInfo
 *
 * @author lizhiqiang
 * @version 1.0 - 2009-12-31 created by lizhiqiang
 */
public class AddVolumeDbInfoTest extends TestCase {
    private AddVolumeDbInfo addVolumeDbInfo;
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        addVolumeDbInfo = new AddVolumeDbInfo();
        addVolumeDbInfo.setDbname("dbname");
        addVolumeDbInfo.setPath("path");
        addVolumeDbInfo.setNumberofpage("numberofpage");
        addVolumeDbInfo.setPurpose("purpose");
        addVolumeDbInfo.setVolname("volname");
        addVolumeDbInfo.setSize_need_mb("sizeNeedMb");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.dbspace.model.AddVolumeDbInfo#getDbname()}.
     */
    public final void testGetDbname() {
        assertEquals(addVolumeDbInfo.getDbname(), "dbname");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.dbspace.model.AddVolumeDbInfo#getVolname()}.
     */
    public final void testGetVolname() {
        assertEquals(addVolumeDbInfo.getVolname(), "volname");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.dbspace.model.AddVolumeDbInfo#getPurpose()}.
     */
    public final void testGetPurpose() {
        assertEquals(addVolumeDbInfo.getPurpose(), "purpose");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.dbspace.model.AddVolumeDbInfo#getPath()}.
     */
    public final void testGetPath() {
        assertEquals(addVolumeDbInfo.getPath(), "path");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.dbspace.model.AddVolumeDbInfo#getNumberofpage()}.
     */
    public final void testGetNumberofpage() {
        assertEquals(addVolumeDbInfo.getNumberofpage(), "numberofpage");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.dbspace.model.AddVolumeDbInfo#getSize_need_mb()}.
     */
    public final void testGetSize_need_mb() {
        assertEquals(addVolumeDbInfo.getSize_need_mb(), "sizeNeedMb");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.dbspace.model.AddVolumeDbInfo#getTaskName()}.
     */
    public final void testGetTaskName() {
        assertEquals(addVolumeDbInfo.getTaskName(), "addvoldb");
    }
}
