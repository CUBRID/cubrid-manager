/*
 * Copyright (C) 2008 Search Solution Corporation. All rights reserved by Search Solution.
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

import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;

/**
 * test ClassAuthorizations model
 *
 * @author wuyingshi
 * @version 1.0 - 2009-12-30 created by wuyingshi
 */
public class ClassAuthorizationsTest extends TestCase {

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.table.model.ClassAuthorizations#ClassAuthorizations(java.lang.String,
     * int)}.
     */
    public void testClassAuthorizations() {
        ClassAuthorizations classAuthorizations = new ClassAuthorizations("name", 2 ^ 14 + 1);
        ClassAuthorizations classAuthorizations2 = new ClassAuthorizations("name", 6000);
        ClassAuthorizations classAuthorizations3 = new ClassAuthorizations("name", 2 ^ 2 + 1);
        ClassAuthorizations classAuthorizations4 = new ClassAuthorizations("name", 2 ^ 3 + 1);
        ClassAuthorizations classAuthorizations5 = new ClassAuthorizations("name", 2 ^ 4 + 1);
        ClassAuthorizations classAuthorizations6 = new ClassAuthorizations("name", 2 ^ 5 + 1);
        ClassAuthorizations classAuthorizations7 = new ClassAuthorizations("name", 2 ^ 6 + 1);
        ClassAuthorizations classAuthorizations8 = new ClassAuthorizations("name", 2 ^ 7 + 1);
        ClassAuthorizations classAuthorizations9 = new ClassAuthorizations("name", 2 ^ 8 + 1);
        ClassAuthorizations classAuthorizations10 = new ClassAuthorizations("name", 2 ^ 9 + 1);
        ClassAuthorizations classAuthorizations11 = new ClassAuthorizations("name", 2 ^ 10 + 1);
        ClassAuthorizations classAuthorizations12 = new ClassAuthorizations("name", 2 ^ 11 + 1);
        ClassAuthorizations classAuthorizations13 = new ClassAuthorizations("name", 2 ^ 12 + 1);
        ClassAuthorizations classAuthorizations14 = new ClassAuthorizations("name", 2 ^ 13 + 1);
        assertNotNull(classAuthorizations2);
        assertNotNull(classAuthorizations3);
        assertNotNull(classAuthorizations4);
        assertNotNull(classAuthorizations5);
        assertNotNull(classAuthorizations6);
        assertNotNull(classAuthorizations7);
        assertNotNull(classAuthorizations8);
        assertNotNull(classAuthorizations9);
        assertNotNull(classAuthorizations10);
        assertNotNull(classAuthorizations11);
        assertNotNull(classAuthorizations12);
        assertNotNull(classAuthorizations13);
        assertNotNull(classAuthorizations14);
        classAuthorizations.setClassName("className");
        assertEquals(classAuthorizations.getClassName(), "className");
        classAuthorizations.setSelectPriv(false);
        assertEquals(classAuthorizations.isSelectPriv(), false);
        classAuthorizations.setInsertPriv(false);
        assertEquals(classAuthorizations.isInsertPriv(), false);
        classAuthorizations.setUpdatePriv(false);
        assertEquals(classAuthorizations.isUpdatePriv(), false);
        classAuthorizations.setAlterPriv(false);
        assertEquals(classAuthorizations.isAlterPriv(), false);
        classAuthorizations.setDeletePriv(false);
        assertEquals(classAuthorizations.isDeletePriv(), false);
        classAuthorizations.setIndexPriv(false);
        assertEquals(classAuthorizations.isIndexPriv(), false);
        classAuthorizations.setExecutePriv(false);
        assertEquals(classAuthorizations.isExecutePriv(), false);
        classAuthorizations.setGrantSelectPriv(false);
        assertEquals(classAuthorizations.isGrantSelectPriv(), false);
        classAuthorizations.setGrantInsertPriv(false);
        assertEquals(classAuthorizations.isGrantInsertPriv(), false);
        classAuthorizations.setGrantUpdatePriv(false);
        assertEquals(classAuthorizations.isGrantUpdatePriv(), false);
        classAuthorizations.setGrantAlterPriv(false);
        assertEquals(classAuthorizations.isGrantAlterPriv(), false);
        classAuthorizations.setGrantDeletePriv(false);
        assertEquals(classAuthorizations.isGrantDeletePriv(), false);
        classAuthorizations.setGrantIndexPriv(false);
        assertEquals(classAuthorizations.isGrantIndexPriv(), false);
        classAuthorizations.setGrantExecutePriv(false);
        assertEquals(classAuthorizations.isGrantExecutePriv(), false);
        classAuthorizations.setAllPriv(false);
        assertEquals(classAuthorizations.isAllPriv(), false);
        classAuthorizations.isSelectPriv();
        classAuthorizations.isInsertPriv();
        classAuthorizations.isUpdatePriv();
        classAuthorizations.isAlterPriv();
        classAuthorizations.isDeletePriv();
        classAuthorizations.isIndexPriv();
        classAuthorizations.isExecutePriv();
        classAuthorizations.isGrantSelectPriv();
        classAuthorizations.isGrantInsertPriv();
        classAuthorizations.isGrantUpdatePriv();
        classAuthorizations.isGrantAlterPriv();
        classAuthorizations.isGrantDeletePriv();
        classAuthorizations.isGrantIndexPriv();
        classAuthorizations.isGrantExecutePriv();
        classAuthorizations.isAllPriv();

        assertFalse(classAuthorizations.isPriv("select"));
        assertFalse(classAuthorizations.isPriv("insert"));
        assertFalse(classAuthorizations.isPriv("update"));
        assertFalse(classAuthorizations.isPriv("alter"));
        assertFalse(classAuthorizations.isPriv("delete"));
        assertFalse(classAuthorizations.isPriv("index"));
        assertFalse(classAuthorizations.isPriv("execute"));
        assertFalse(classAuthorizations.isPriv("grant select"));
        assertFalse(classAuthorizations.isPriv("grant insert"));
        assertFalse(classAuthorizations.isPriv("grant update"));
        assertFalse(classAuthorizations.isPriv("grant alter"));
        assertFalse(classAuthorizations.isPriv("grant delete"));
        assertFalse(classAuthorizations.isPriv("grant index"));
        assertFalse(classAuthorizations.isPriv("grant execute"));
        assertFalse(classAuthorizations.isPriv("aaaa"));

        Map<String, Object> privs = new HashMap<String, Object>();
        for (int i = 1; i <= 7; i++) {
            privs.put(String.valueOf(i), true);
            assertFalse(classAuthorizations.isRevokePriv(privs));
            privs.put(String.valueOf(i), false);
        }
        privs.put("0", true);
        assertTrue(classAuthorizations.isRevokePriv(privs));

        assertFalse(classAuthorizations.isGrantPriv());
    }
}
