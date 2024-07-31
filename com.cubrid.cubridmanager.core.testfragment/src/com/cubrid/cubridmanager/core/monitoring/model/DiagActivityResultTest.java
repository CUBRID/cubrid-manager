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
 * Test DiagActivityResult
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-1-4 created by lizhiqiang
 */
public class DiagActivityResultTest extends TestCase {
    private DiagActivityResult bean;
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        bean = new DiagActivityResult();
        bean = new DiagActivityResult(new DiagActivityResult());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DiagActivityResult#getEventClass()}.
     */
    public void testGetEventClass() {
        bean.setEventClass("eventClass");
        assertEquals(bean.getEventClass(), "eventClass");

        DiagActivityResult bean2 = new DiagActivityResult(bean);
        assertEquals(bean2.getEventClass(), "eventClass");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DiagActivityResult#getTextData()}.
     */
    public void testGetTextData() {
        bean.setTextData("textData");
        assertEquals(bean.getTextData(), "textData");

        DiagActivityResult bean2 = new DiagActivityResult(bean);
        assertEquals(bean2.getTextData(), "textData");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DiagActivityResult#getBinData()}.
     */
    public void testGetBinData() {
        bean.setBinData("binData");
        assertEquals(bean.getBinData(), "binData");

        DiagActivityResult bean2 = new DiagActivityResult(bean);
        assertEquals(bean2.getBinData(), "binData");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DiagActivityResult#getIntegerData()}.
     */
    public void testGetIntegerData() {
        bean.setIntegerData("integerData");
        assertEquals(bean.getIntegerData(), "integerData");
        DiagActivityResult bean2 = new DiagActivityResult(bean);
        assertEquals(bean2.getIntegerData(), "integerData");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DiagActivityResult#getTime()}.
     */
    public void testGetTime() {
        bean.setTime("time");
        assertEquals(bean.getTime(), "time");
        DiagActivityResult bean2 = new DiagActivityResult(bean);
        assertEquals(bean2.getTime(), "time");
    }
}
