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
package com.cubrid.cubridmanager.core.cubrid.user.model;

import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;

/**
 * Test DbUserInfo
 *
 * @author lizhiqiang
 * @version 1.0 - 2009-12-30 created by lilzhiqinag
 */
public class DbUserInfoTest extends TestCase {
    private DbUserInfo bean;
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        bean = new DbUserInfo();
        bean.setDbName("dbName");
        bean.setName("name");
        bean.setPassword("password");
        bean.setNoEncryptPassword("noEncryptPassword");
        bean.addGroups(new UserGroup());
        bean.addAuthorization(new HashMap<String, String>());
        bean.setDbaAuthority(true);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo#getDbName()}.
     */
    public void testGetDbName() {
        assertEquals(bean.getDbName(), "dbName");
    }

    /**
     * Test method for {@link com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo#getName()}.
     */
    public void testGetName() {
        bean.setName("Name");
        assertEquals(bean.getName(), "name");
        bean.setName(null);
        assertEquals(bean.getName(), "name");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo#getPassword()}.
     */
    public void testGetPassword() {
        assertEquals(bean.getPassword(), "password");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo#getGroups()}.
     */
    public void testGetGroups() {
        assertEquals(bean.getGroups() instanceof UserGroup, true);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo#getAuthorization()}.
     */
    @SuppressWarnings("unchecked")
    public void testGetAuthorization() {
        assertEquals(bean.getAuthorization() instanceof Map, true);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo#isDbaAuthority()}.
     */
    public void testIsDbaAuthority() {
        assertEquals(bean.isDbaAuthority(), true);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo#getNoEncryptPassword()}.
     */
    public void testGetNoEncryptPassword() {
        assertEquals(bean.getNoEncryptPassword(), "noEncryptPassword");
    }
    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo#DbUserInfo(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, boolean)}.
     */
    public void testDbUserInfoStringStringStringStringBoolean() {
        DbUserInfo dui =
                new DbUserInfo(
                        "new_dbName", "new_name", "new_password", "new_noEncryptPassword", true);
        assertEquals(dui.getDbName(), "new_dbName");
        assertEquals(dui.getName(), "new_name");
        assertEquals(dui.getPassword(), "new_password");
        assertEquals(dui.getNoEncryptPassword(), "new_noEncryptPassword");
        assertTrue(dui.isDbaAuthority());
    }
}
