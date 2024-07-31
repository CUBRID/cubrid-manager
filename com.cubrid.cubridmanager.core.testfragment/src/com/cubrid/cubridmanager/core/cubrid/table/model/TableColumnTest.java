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

import junit.framework.TestCase;

/**
 * Test the type of DbColumn
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-12-14 created by lizhiqiang
 */
public class TableColumnTest extends TestCase {
    private TableColumn dbColumn;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        dbColumn = new TableColumn();
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.table.model.TableColumn#getColumnName()} .
     */
    public void testGetColumnName() {
        dbColumn.setColumnName("columnName");
        assertEquals("columnname", dbColumn.getColumnName());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.table.model.TableColumn#getTypeName()} .
     */
    public void testGetTypeName() {
        dbColumn.setTypeName("typeName");
        assertEquals("typename", dbColumn.getTypeName());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.table.model.TableColumn#getPrecision()} .
     */
    public void testGetPrecision() {
        dbColumn.setPrecision(10);
        assertEquals(10, dbColumn.getPrecision());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.table.model.TableColumn#getScale()} .
     */
    public void testGetScale() {
        dbColumn.setScale(100);
        assertEquals(100, dbColumn.getScale());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.table.model.TableColumn#getOrdinalPosition()} .
     */
    public void testGetOrdinalPosition() {
        dbColumn.setOrdinalPosition(1000);
        assertEquals(1000, dbColumn.getOrdinalPosition());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.table.model.TableColumn#getSubElementTypeName()} .
     */
    public void testGetSubElementTypeName() {
        dbColumn.setSubElementTypeName("subElementTypeName");
        assertEquals("subElementTypeName".toLowerCase(), dbColumn.getSubElementTypeName());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.table.model.TableColumn#isPrimaryKey()} .
     */
    public void testIsPrimaryKey() {
        dbColumn.setPrimaryKey(true);
        assertTrue(dbColumn.isPrimaryKey());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.table.model.TableColumn#compareTo(com.cubrid.cubridmanager.core.cubrid.table.model.TableColumn)}
     * .
     */
    public void testCompareTo() {
        TableColumn another = new TableColumn();
        another.setOrdinalPosition(10);
        dbColumn.setOrdinalPosition(10);
        int result = dbColumn.compareTo(another);
        assertEquals(0, result);

        dbColumn.setOrdinalPosition(5);
        result = dbColumn.compareTo(another);
        assertTrue(result < 0);
        dbColumn.setOrdinalPosition(15);
        result = dbColumn.compareTo(another);
        assertTrue(result > 0);
    }
    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.table.model.TableColumn#equals(Object object)} .
     */
    public void testEquals() {
        TableColumn another = new TableColumn();
        another.setColumnName("");
        assertFalse(dbColumn.equals(another));
        dbColumn.setColumnName("columnName");
        assertFalse(dbColumn.equals(another));

        another.setColumnName("columnName");
        another.setPrimaryKey(true);
        assertFalse(dbColumn.equals(another));
        dbColumn.setPrimaryKey(false);
        assertFalse(dbColumn.equals(another));

        dbColumn.setPrimaryKey(true);

        another.setOrdinalPosition(1);
        assertFalse(dbColumn.equals(another));

        dbColumn.setOrdinalPosition(1);
        another.setPrecision(1);
        assertFalse(dbColumn.equals(another));
        dbColumn.setPrecision(1);

        another.setScale(1);
        assertFalse(dbColumn.equals(another));

        dbColumn.setScale(1);
        another.setSubElementTypeName("subElementTypeName");
        assertFalse(dbColumn.equals(another));
        dbColumn.setSubElementTypeName("Name");
        assertFalse(dbColumn.equals(another));

        dbColumn.setSubElementTypeName("subElementTypeName");
        another.setTypeName("typeName");
        assertFalse(dbColumn.equals(another));
        dbColumn.setTypeName("");
        assertFalse(dbColumn.equals(another));
        dbColumn.setTypeName("typeName");
        assertTrue(dbColumn.equals(another));
    }

    public void testHashCode() {
        dbColumn.setColumnName("columnName");
        dbColumn.hashCode();
    }
}
