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
package com.cubrid.cubridmanager.core.cubrid.table.model;

import junit.framework.TestCase;

/**
 * Test the type of DbAuth
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-09-14 created by lizhiqiang
 */
public class DbAuthTest extends TestCase {

    private DbAuth dbAuth;
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        dbAuth = new DbAuth();
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.table.model.DbAuth#getGrantorName()}.
     */
    public void testGetGrantorName() {
        dbAuth.setGrantorName("grantorName");
        assertEquals("grantorName", dbAuth.getGrantorName());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.table.model.DbAuth#getGranteeName()}.
     */
    public void testGetGranteeName() {
        dbAuth.setGranteeName("granteeName");
        assertEquals("granteeName", dbAuth.getGranteeName());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.table.model.DbAuth#getClassName()}.
     */
    public void testGetClassName() {
        dbAuth.setClassName("className");
        assertEquals("className", dbAuth.getClassName());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.table.model.DbAuth#getAuthType()}.
     */
    public void testGetAuthType() {
        dbAuth.setAuthType("authType");
        assertEquals("authType", dbAuth.getAuthType());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.table.model.DbAuth#isGrantable()}.
     */
    public void testIsGrantable() {
        dbAuth.setGrantable("isGrantable");
        assertFalse(dbAuth.isGrantable());
        dbAuth.setGrantable("Yes");
        assertTrue(dbAuth.isGrantable());
    }
}
