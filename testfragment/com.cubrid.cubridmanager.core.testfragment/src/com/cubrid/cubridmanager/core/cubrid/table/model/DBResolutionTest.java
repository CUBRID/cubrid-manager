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

import com.cubrid.common.core.common.model.DBResolution;
import junit.framework.TestCase;

/**
 * test DBResolution model
 *
 * @author wuyingshi
 * @version 1.0 - 2010-1-4 created by wuyingshi
 */
public class DBResolutionTest extends TestCase {
    /** Test DBResolution */
    public final void testDBResolution() {

        String name = "name";
        String tableName = "tableName";
        String alias = "alias";
        boolean isClassResolution = true;
        // test getters and setters
        DBResolution dbResolution = new DBResolution(name, tableName, alias);
        dbResolution.getAlias();
        dbResolution.setName(name);
        dbResolution.setTableName(tableName);
        dbResolution.setAlias(alias);
        dbResolution.setClassResolution(isClassResolution);
        assertEquals(dbResolution.getName(), name);
        assertEquals(dbResolution.getTableName(), tableName);
        assertEquals(dbResolution.getAlias(), alias);
        assertTrue(dbResolution.isClassResolution());

        // test public boolean equals(Object obj)
        assertTrue(dbResolution.equals(dbResolution));
        assertFalse(dbResolution.equals(null));
        assertFalse(dbResolution.equals("other object"));
        // test public int hashCode()
        dbResolution.hashCode();
        // test public SerialInfo clone()
        DBResolution clonedSerialInfo = dbResolution.clone();
        assertEquals(dbResolution, clonedSerialInfo);

        DBResolution resolution = new DBResolution();
        assertEquals("", resolution.getAlias());
    }
}
