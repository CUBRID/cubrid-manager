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

import java.util.List;
import junit.framework.TestCase;

/**
 * Test StatusTemplateInfoTest
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-1-5 created by lizhiqiang
 */
public class StatusTemplateInfoTest extends TestCase {

    private StatusTemplateInfo bean;
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        bean = new StatusTemplateInfo();
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.StatusTemplateInfo#getName()}.
     */
    public void testGetName() {
        bean.setName("name");
        assertEquals(bean.getName(), "name");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.StatusTemplateInfo#getDesc()}.
     */
    public void testGetDesc() {
        bean.setDesc("desc");
        assertEquals(bean.getDesc(), "desc");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.StatusTemplateInfo#getDb_name()}.
     */
    public void testGetDb_name() {
        bean.setDb_name("db_name");
        assertEquals(bean.getDb_name(), "db_name");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.StatusTemplateInfo#getSampling_term()}.
     */
    public void testGetSampling_term() {
        bean.setSampling_term("sampling_term");
        assertEquals(bean.getSampling_term(), "sampling_term");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.StatusTemplateInfo#getTargetConfigInfoList()}.
     */
    public void testGetTargetConfigInfoList() {
        List<TargetConfigInfo> list = bean.getTargetConfigInfoList();
        assertTrue(list.isEmpty());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.StatusTemplateInfo#addTarget_config(com.cubrid.cubridmanager.core.monitoring.model.TargetConfigInfo)}.
     */
    public void testAddTarget_config() {
        bean.addTarget_config(new TargetConfigInfo());
        List<TargetConfigInfo> list = bean.getTargetConfigInfoList();
        assertFalse(list.isEmpty());
        bean.addTarget_config(new TargetConfigInfo());
    }
}
