/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: -
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. - Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. - Neither the name of the <ORGANIZATION> nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.cubrid.common.core.metadesc.model;

import com.cubrid.common.core.schemacomment.model.SchemaComment;
import junit.framework.TestCase;

public class TestSchemaComment extends TestCase {
    public void testGetSet() {
        SchemaComment meta = new SchemaComment();
        meta.setTable("table");
        meta.setColumn("column");
        meta.setDescription("description");
        assertTrue(meta.isTable());
        assertFalse(meta.isColumn());
        assertEquals(meta.getTable(), "table");
        assertEquals(meta.getColumn(), "column");
        assertEquals(meta.getDescription(), "description");

        meta = new SchemaComment();
        meta.setTable("table");
        meta.setColumn(null);
        meta.setDescription("description");
        assertFalse(meta.isTable());
        assertTrue(meta.isColumn());
        assertEquals(meta.getTable(), "table");
        assertNull(meta.getColumn());
        assertEquals(meta.getDescription(), "description");
    }
}
