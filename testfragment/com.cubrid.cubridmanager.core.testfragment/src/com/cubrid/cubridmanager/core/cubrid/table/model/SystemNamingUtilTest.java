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
package com.cubrid.cubridmanager.core.cubrid.table.model;

import com.cubrid.common.core.util.ConstraintNamingUtil;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

public class SystemNamingUtilTest extends TestCase {
    public void testGetPKName() {

        List<String> attrList = new ArrayList<String>();
        attrList.add("test1");
        attrList.add("test2");
        attrList.add("test3");

        String res = ConstraintNamingUtil.getPKName("test_table", attrList);
        assertEquals("pk_test_table_test1_test2_test3", res);

        try {
            res = ConstraintNamingUtil.getPKName("test_table", null);
            assertTrue(false);
        } catch (NullPointerException e) {
            assertTrue(true);
        }

        try {
            res = ConstraintNamingUtil.getPKName(null, null);
            assertTrue(false);
        } catch (NullPointerException e) {
            assertTrue(true);
        }

        try {
            res = ConstraintNamingUtil.getPKName(null, attrList);
            assertEquals(res, "pk_null_test1_test2_test3");
        } catch (NullPointerException e) {
            assertTrue(true);
        }

        try {
            res = ConstraintNamingUtil.getPKName("test_table", new ArrayList<String>());
            assertEquals(res, "pk_test_table");
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    public void testGetFKName() {

        List<String> attrList = new ArrayList<String>();
        attrList.add("test1");
        attrList.add("test2");
        attrList.add("test3");

        String res = ConstraintNamingUtil.getFKName("test_table", attrList);
        assertEquals("fk_test_table_test1_test2_test3", res);

        try {
            res = ConstraintNamingUtil.getFKName("test_table", null);
            assertTrue(false);
        } catch (NullPointerException e) {
            assertTrue(true);
        }

        try {
            res = ConstraintNamingUtil.getFKName(null, null);
            assertTrue(false);
        } catch (NullPointerException e) {
            assertTrue(true);
        }

        try {
            res = ConstraintNamingUtil.getFKName(null, attrList);
            assertEquals(res, "fk_null_test1_test2_test3");
        } catch (NullPointerException e) {
            assertTrue(true);
        }

        try {
            res = ConstraintNamingUtil.getFKName("test_table", new ArrayList<String>());
            assertEquals(res, "fk_test_table");
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    public void testGetIndexName() {

        List<String> attrList = new ArrayList<String>();
        attrList.add("test1 desc");
        attrList.add("test2 asc");
        attrList.add("test3 DESC");

        String res = ConstraintNamingUtil.getIndexName("test_table", attrList);
        assertEquals("i_test_table_test1_d_test2_test3_d", res);

        try {
            res = ConstraintNamingUtil.getIndexName("test_table", null);
            assertTrue(false);
        } catch (NullPointerException e) {
            assertTrue(true);
        }

        try {
            res = ConstraintNamingUtil.getIndexName(null, null);
            assertTrue(false);
        } catch (NullPointerException e) {
            assertTrue(true);
        }

        try {
            res = ConstraintNamingUtil.getIndexName(null, attrList);
            assertEquals(res, "i_null_test1_d_test2_test3_d");
        } catch (NullPointerException e) {
            assertTrue(true);
        }

        try {
            res = ConstraintNamingUtil.getIndexName("test_table", new ArrayList<String>());
            assertEquals(res, "i_test_table");
        } catch (NullPointerException e) {
            assertTrue(true);
        }

        attrList.add("test4");
        try {
            // TODO: if strs.length == 1 ?
            res = ConstraintNamingUtil.getIndexName("test_table", attrList);
            assertEquals(res, "i_test_table_test1_d_test2_test3_d_test4");
        } catch (ArrayIndexOutOfBoundsException e) {
            assertTrue(true);
        }
    }

    public void testGetReverseIndexName() {

        List<String> attrList = new ArrayList<String>();
        attrList.add("test1");
        attrList.add("test2");
        attrList.add("test3");

        String res = ConstraintNamingUtil.getReverseIndexName("test_table", attrList);
        assertEquals("ri_test_table_test1_test2_test3", res);

        try {
            res = ConstraintNamingUtil.getReverseIndexName("test_table", null);
            assertTrue(false);
        } catch (NullPointerException e) {
            assertTrue(true);
        }

        try {
            res = ConstraintNamingUtil.getReverseIndexName(null, null);
            assertTrue(false);
        } catch (NullPointerException e) {
            assertTrue(true);
        }

        try {
            res = ConstraintNamingUtil.getReverseIndexName(null, attrList);
            assertEquals(res, "ri_null_test1_test2_test3");
        } catch (NullPointerException e) {
            assertTrue(true);
        }

        try {
            res = ConstraintNamingUtil.getReverseIndexName("test_table", new ArrayList<String>());
            assertEquals(res, "ri_test_table");
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    public void testGetReverseUniqueName() {

        List<String> attrList = new ArrayList<String>();
        attrList.add("test1");
        attrList.add("test2");
        attrList.add("test3");

        String res = ConstraintNamingUtil.getReverseUniqueName("test_table", attrList);
        assertEquals("ru_test_table_test1_test2_test3", res);

        try {
            res = ConstraintNamingUtil.getReverseUniqueName("test_table", null);
            assertTrue(false);
        } catch (NullPointerException e) {
            assertTrue(true);
        }

        try {
            res = ConstraintNamingUtil.getReverseUniqueName(null, null);
            assertTrue(false);
        } catch (NullPointerException e) {
            assertTrue(true);
        }

        try {
            res = ConstraintNamingUtil.getReverseUniqueName(null, attrList);
            assertEquals(res, "ru_null_test1_test2_test3");
        } catch (NullPointerException e) {
            assertTrue(true);
        }

        try {
            res = ConstraintNamingUtil.getReverseUniqueName("test_table", new ArrayList<String>());
            assertEquals(res, "ru_test_table");
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    public void testGetUniqueName() {
        List<String> ruleList = new ArrayList<String>();
        ruleList.add("attr DESC");

        String name = ConstraintNamingUtil.getUniqueName("test_table", ruleList);
        assertEquals("u_test_table_attr_d", name);
    }
}
