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
package com.cubrid.cubridmanager.core.cubrid.database.model.lock;

import junit.framework.TestCase;

/**
 * Test LockWaiters
 *
 * @author Administrator
 * @version 1.0 - 2010-1-11 created by Administrator
 */
public class LockWaitersTest extends TestCase {
    private LockWaiters bean;
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        bean = new LockWaiters();
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.database.model.lock.LockWaiters#getTran_index()}.
     */
    public void testGetTran_index() {
        bean.setTran_index(10);
        assertEquals(bean.getTran_index(), 10);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.database.model.lock.LockWaiters#getB_mode()}.
     */
    public void testGetB_mode() {
        bean.setB_mode("b_mode");
        assertEquals(bean.getB_mode(), "b_mode");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.database.model.lock.LockWaiters#getStart_at()}.
     */
    public void testGetStart_at() {
        bean.setStart_at("start_at");
        assertEquals(bean.getStart_at(), "start_at");
        bean.setStart_at(null);
        assertNull(bean.getStart_at());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.database.model.lock.LockWaiters#getWaitfornsec()}.
     */
    public void testGetWaitfornsec() {
        bean.setWaitfornsec("waitfornsec");
        assertEquals(bean.getWaitfornsec(), "waitfornsec");
    }
}
