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
package com.cubrid.cubridmanager.core.cubrid.table.task;

import com.cubrid.common.core.common.model.Constraint;
import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.common.model.SerialInfo;
import com.cubrid.common.core.util.ConstraintNamingUtil;
import com.cubrid.cubridmanager.core.SetupJDBCTestCase;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * to validate whether information of all data types is read correctly <ui>
 * <li>column information;
 * <li>data type
 * <li>default or shared values </ui>
 *
 *     <p>it is tested twice, one is under the user "dba", the other is under the user "public"
 *
 * @author moulinwang
 * @version 1.0 - 2009-6-30 created by moulinwang
 */
public class GetSchemaTaskTest extends SetupJDBCTestCase {
    String testTableName = "testgetschematask";
    String sql = null;

    private boolean createTestTable() throws Exception {
        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/table/task/test.message/test_table.txt");
        String msg = Tool.getFileContent(filepath);

        String[] strs = msg.split(";");
        boolean success = true;
        if (msg != null) {
            for (String str : strs) {
                if (!str.trim().equals("")) {
                    success = executeDDL(str);
                }
            }
        }
        return success;
    }

    private boolean dropTestTable() {
        String sql = "drop table \"" + testTableName + "\"";
        return executeDDL(sql);
    }

    public void testGetSchemaTaskUnderDBA() {
        innerTestGetSchemaTask();
    }

    public void testGetSchemaTaskUnderPUBLIC() {
        String dbUser = "public";
        DbUserInfo dbUserInfo = databaseInfo.getAuthLoginedDbUserInfo();
        DbUserInfo userInfo = new DbUserInfo(databaseInfo.getDbName(), dbUser, "", "", false);
        databaseInfo.setAuthLoginedDbUserInfo(userInfo);
        innerTestGetSchemaTask();
        databaseInfo.setAuthLoginedDbUserInfo(dbUserInfo);
    }

    public void innerTestGetSchemaTask() {
        try {
            if (!createTestTable()) {
                dropTestTable();
                // test again
                if (!createTestTable()) {
                    fail();
                }
            }
        } catch (Exception e) {
            fail();
        }
        testTableName = testTableName.toLowerCase();
        GetSchemaTask task = new GetSchemaTask(databaseInfo, testTableName);
        task.execute();
        SchemaInfo jSchema = task.getSchema();
        DBAttribute attr = null;
        SerialInfo auto = null;

        // "smallint" smallint AUTO_INCREMENT PRIMARY KEY UNIQUE,
        attr = jSchema.getAttributes().get(0);
        assertFalse(attr.isClassAttribute());
        assertEquals("smallint", attr.getName());
        assertEquals("smallint", attr.getType());
        assertEquals(testTableName, attr.getInherit());
        assertNull(attr.getDefault());

        auto = attr.getAutoIncrement();
        assertNotNull(auto);
        assertEquals("1", auto.getMinValue());
        assertEquals("1", auto.getIncrementValue());

        // "smallint2" smallint,
        attr = jSchema.getAttributes().get(1);
        assertFalse(attr.isClassAttribute());
        assertEquals("smallint2", attr.getName());
        assertEquals("smallint", attr.getType());
        assertEquals(testTableName, attr.getInherit());
        assertNull(attr.getDefault());

        auto = attr.getAutoIncrement();
        assertNull(auto);

        // "integer" integer AUTO_INCREMENT(2,1),
        attr = jSchema.getAttributes().get(2);
        assertFalse(attr.isClassAttribute());
        assertEquals("integer", attr.getName());
        assertEquals("integer", attr.getType());
        assertEquals(testTableName, attr.getInherit());
        assertNull(attr.getDefault());

        auto = attr.getAutoIncrement();
        assertNotNull(auto);
        assertEquals("2", auto.getMinValue());
        assertEquals("1", auto.getIncrementValue());

        // "bigint" bigint,
        attr = jSchema.getAttributes().get(3);
        assertFalse(attr.isClassAttribute());
        assertEquals("bigint", attr.getName());
        assertEquals("bigint", attr.getType());
        assertEquals(testTableName, attr.getInherit());
        assertNull(attr.getDefault());

        auto = attr.getAutoIncrement();
        assertNull(auto);

        // "numeric1" numeric(15,0) AUTO_INCREMENT UNIQUE,
        attr = jSchema.getAttributes().get(4);
        assertFalse(attr.isClassAttribute());
        assertEquals("numeric1", attr.getName());
        assertEquals("numeric(15,0)", attr.getType());
        assertEquals(testTableName, attr.getInherit());
        assertNull(attr.getDefault());

        auto = attr.getAutoIncrement();
        assertNotNull(auto);
        assertEquals("1", auto.getMinValue());
        assertEquals("1", auto.getIncrementValue());

        // "numeric2" numeric(17,2) DEFAULT 12.50,
        attr = jSchema.getAttributes().get(5);
        assertFalse(attr.isClassAttribute());
        assertEquals("numeric2", attr.getName());
        assertEquals("numeric(17,2)", attr.getType());
        assertEquals(testTableName, attr.getInherit());
        assertEquals("12.50", attr.getDefault());

        auto = attr.getAutoIncrement();
        assertNull(auto);

        // "float" float DEFAULT 12 NOT NULL,
        attr = jSchema.getAttributes().get(6);
        assertFalse(attr.isClassAttribute());
        assertEquals("float", attr.getName());
        assertEquals("float", attr.getType());
        assertEquals(testTableName, attr.getInherit());
        assertEquals("12", attr.getDefault());

        auto = attr.getAutoIncrement();
        assertNull(auto);

        // "date" date DEFAULT DATE'12/12/2009',
        attr = jSchema.getAttributes().get(7);
        assertFalse(attr.isClassAttribute());
        assertEquals("date", attr.getName());
        assertEquals("date", attr.getType());
        assertEquals(testTableName, attr.getInherit());
        assertEquals("2009/12/12", attr.getDefault());

        auto = attr.getAutoIncrement();
        assertNull(auto);

        // "datetime" datetime DEFAULT DATETIME'2009-12-12 12:12:12.333',
        attr = jSchema.getAttributes().get(8);
        assertFalse(attr.isClassAttribute());
        assertEquals("datetime", attr.getName());
        assertEquals("datetime", attr.getType());
        assertEquals(testTableName, attr.getInherit());
        assertEquals("2009-12-12 12:12:12.333", attr.getDefault());

        auto = attr.getAutoIncrement();
        assertNull(auto);

        // "timestamp" timestamp DEFAULT TIMESTAMP'12/01/2009 03:07:51',
        attr = jSchema.getAttributes().get(9);
        assertFalse(attr.isClassAttribute());
        assertEquals("timestamp", attr.getName());
        assertEquals("timestamp", attr.getType());
        assertEquals(testTableName, attr.getInherit());
        assertEquals("2009-12-01 03:07:51", attr.getDefault());

        auto = attr.getAutoIncrement();
        assertNull(auto);

        // "time" time DEFAULT TIME'00:00:12',
        attr = jSchema.getAttributes().get(10);
        assertFalse(attr.isClassAttribute());
        assertEquals("time", attr.getName());
        assertEquals("time", attr.getType());
        assertEquals(testTableName, attr.getInherit());
        // assertEquals("AM 12:00:12", attr.getDefault());

        auto = attr.getAutoIncrement();
        assertNull(auto);

        // "char" character(10) DEFAULT 'aa        ',
        attr = jSchema.getAttributes().get(11);
        assertFalse(attr.isClassAttribute());
        assertEquals("char", attr.getName());
        assertEquals("character(10)", attr.getType());
        assertEquals(testTableName, attr.getInherit());
        assertEquals("'aa        '", attr.getDefault());

        auto = attr.getAutoIncrement();
        assertNull(auto);

        // "varchar" character varying(10) DEFAULT '77',
        attr = jSchema.getAttributes().get(12);
        assertFalse(attr.isClassAttribute());
        assertEquals("varchar", attr.getName());
        assertEquals("character varying(10)", attr.getType());
        assertEquals(testTableName, attr.getInherit());
        assertEquals("'77'", attr.getDefault());

        auto = attr.getAutoIncrement();
        assertNull(auto);

        // "setint" set_of(smallint) DEFAULT {1, 2},
        attr = jSchema.getAttributes().get(13);
        assertFalse(attr.isClassAttribute());
        assertEquals("setint", attr.getName());
        assertEquals("set_of(smallint)", attr.getType());
        assertEquals(testTableName, attr.getInherit());
        assertEquals("{1, 2}", attr.getDefault());

        auto = attr.getAutoIncrement();
        assertNull(auto);

        // "multisetchar" multiset_of(character varying(10)) DEFAULT {'12', '13'},
        attr = jSchema.getAttributes().get(14);
        assertFalse(attr.isClassAttribute());
        assertEquals("multisetchar", attr.getName());
        assertEquals("multiset_of(character varying(10))", attr.getType());
        assertEquals(testTableName, attr.getInherit());
        assertEquals("{'12', '13'}", attr.getDefault());

        auto = attr.getAutoIncrement();
        assertNull(auto);

        // "shared" character varying(1073741823) SHARED 'sharedvalue'
        attr = jSchema.getAttributes().get(15);
        assertFalse(attr.isClassAttribute());
        assertEquals("shared", attr.getName());
        assertEquals("character varying(1073741823)", attr.getType());
        assertEquals(testTableName, attr.getInherit());
        assertTrue(attr.isShared());
        assertEquals("'sharedvalue'", attr.getSharedValue());

        auto = attr.getAutoIncrement();
        assertNull(auto);

        // "bit" bit(1),
        attr = jSchema.getAttributes().get(16);
        assertFalse(attr.isClassAttribute());
        assertEquals("bit", attr.getName());
        assertEquals("bit(1)", attr.getType());
        assertEquals(testTableName, attr.getInherit());
        assertNull(attr.getDefault());

        auto = attr.getAutoIncrement();
        assertNull(auto);

        // "bitv" bit varying(100),
        attr = jSchema.getAttributes().get(17);
        assertFalse(attr.isClassAttribute());
        assertEquals("bitv", attr.getName());
        assertEquals("bit varying(100)", attr.getType());
        assertEquals(testTableName, attr.getInherit());
        assertNull(attr.getDefault());

        auto = attr.getAutoIncrement();
        assertNull(auto);

        // "nchar" national character(1),
        attr = jSchema.getAttributes().get(18);
        assertFalse(attr.isClassAttribute());
        assertEquals("nchar", attr.getName());
        assertEquals("national character(1)", attr.getType());
        assertEquals(testTableName, attr.getInherit());
        assertNull(attr.getDefault());

        auto = attr.getAutoIncrement();
        assertNull(auto);

        // "varnchar" national character varying(100)
        attr = jSchema.getAttributes().get(19);
        assertFalse(attr.isClassAttribute());
        assertEquals("varnchar", attr.getName());
        assertEquals("national character varying(100)", attr.getType());
        assertEquals(testTableName, attr.getInherit());
        assertNull(attr.getDefault());

        auto = attr.getAutoIncrement();
        assertNull(auto);

        //		"sequence_numeric" sequence_of(numeric(15,1)),
        attr = jSchema.getAttributes().get(20);
        assertFalse(attr.isClassAttribute());
        assertEquals("sequence_numeric", attr.getName());
        assertEquals("sequence_of(numeric(15,1))", attr.getType());
        assertEquals(testTableName, attr.getInherit());
        assertNull(attr.getDefault());

        auto = attr.getAutoIncrement();
        assertNull(auto);

        //		"set_bigint" set_of(bigint),
        attr = jSchema.getAttributes().get(21);
        assertFalse(attr.isClassAttribute());
        assertEquals("set_bigint", attr.getName());
        assertEquals("set_of(bigint)", attr.getType());
        assertEquals(testTableName, attr.getInherit());
        assertNull(attr.getDefault());

        auto = attr.getAutoIncrement();
        assertNull(auto);

        //		"multiset_float" multiset_of(float),
        attr = jSchema.getAttributes().get(22);
        assertFalse(attr.isClassAttribute());
        assertEquals("multiset_float", attr.getName());
        assertEquals("multiset_of(float)", attr.getType());
        assertEquals(testTableName, attr.getInherit());
        assertNull(attr.getDefault());

        auto = attr.getAutoIncrement();
        assertNull(auto);

        //		"sequence_nchar" sequence_of(national character(2)),
        attr = jSchema.getAttributes().get(23);
        assertFalse(attr.isClassAttribute());
        assertEquals("sequence_nchar", attr.getName());
        assertEquals("sequence_of(national character(2))", attr.getType());
        assertEquals(testTableName, attr.getInherit());
        assertNull(attr.getDefault());

        auto = attr.getAutoIncrement();
        assertNull(auto);

        //		"set_vnchar" set_of(national character varying(100)),
        attr = jSchema.getAttributes().get(24);
        assertFalse(attr.isClassAttribute());
        assertEquals("set_vnchar", attr.getName());
        assertEquals("set_of(national character varying(100))", attr.getType());
        assertEquals(testTableName, attr.getInherit());
        assertNull(attr.getDefault());

        auto = attr.getAutoIncrement();
        assertNull(auto);

        //		"set_time" set_of(time),
        attr = jSchema.getAttributes().get(25);
        assertFalse(attr.isClassAttribute());
        assertEquals("set_time", attr.getName());
        assertEquals("set_of(time)", attr.getType());
        assertEquals(testTableName, attr.getInherit());
        assertNull(attr.getDefault());

        auto = attr.getAutoIncrement();
        assertNull(auto);

        //		"multiset_date" multiset_of(date),
        attr = jSchema.getAttributes().get(26);
        assertFalse(attr.isClassAttribute());
        assertEquals("multiset_date", attr.getName());
        assertEquals("multiset_of(date)", attr.getType());
        assertEquals(testTableName, attr.getInherit());
        assertNull(attr.getDefault());

        auto = attr.getAutoIncrement();
        assertNull(auto);

        //		"sequence_datetime" sequence_of(datetime),
        attr = jSchema.getAttributes().get(27);
        assertFalse(attr.isClassAttribute());
        assertEquals("sequence_datetime", attr.getName());
        assertEquals("sequence_of(datetime)", attr.getType());
        assertEquals(testTableName, attr.getInherit());
        assertNull(attr.getDefault());

        auto = attr.getAutoIncrement();
        assertNull(auto);

        //		"set_timestamp" set_of(timestamp),
        attr = jSchema.getAttributes().get(28);
        assertFalse(attr.isClassAttribute());
        assertEquals("set_timestamp", attr.getName());
        assertEquals("set_of(timestamp)", attr.getType());
        assertEquals(testTableName, attr.getInherit());
        assertNull(attr.getDefault());

        auto = attr.getAutoIncrement();
        assertNull(auto);

        //		"multiset_monetary" multiset_of(monetary),
        attr = jSchema.getAttributes().get(29);
        assertFalse(attr.isClassAttribute());
        assertEquals("multiset_monetary", attr.getName());
        assertEquals("multiset_of(monetary)", attr.getType());
        assertEquals(testTableName, attr.getInherit());
        assertNull(attr.getDefault());

        auto = attr.getAutoIncrement();
        assertNull(auto);

        //		"sequence_bit" sequence_of(bit(2)),
        attr = jSchema.getAttributes().get(30);
        assertFalse(attr.isClassAttribute());
        assertEquals("sequence_bit", attr.getName());
        assertEquals("sequence_of(bit(2))", attr.getType());
        assertEquals(testTableName, attr.getInherit());
        assertNull(attr.getDefault());

        auto = attr.getAutoIncrement();
        assertNull(auto);

        //		"set_varbit" set_of(bit varying(120))
        attr = jSchema.getAttributes().get(31);
        assertFalse(attr.isClassAttribute());
        assertEquals("set_varbit", attr.getName());
        assertEquals("set_of(bit varying(120))", attr.getType());
        assertEquals(testTableName, attr.getInherit());
        assertNull(attr.getDefault());

        auto = attr.getAutoIncrement();
        assertNull(auto);

        List<String> ruleList = new ArrayList<String>();
        List<String> attrList = new ArrayList<String>();
        String constraintName = null;
        Constraint constraint = null;

        // CREATE  UNIQUE INDEX ON "testgetrecordcounttask"("numeric2" DESC,"float" ASC,"date"
        // DESC);
        attrList.clear();
        ruleList.clear();
        attrList.add("numeric2");
        attrList.add("float");
        attrList.add("date");
        ruleList.add("numeric2 DESC");
        ruleList.add("float ASC");
        ruleList.add("date DESC");
        constraintName = ConstraintNamingUtil.getUniqueName(testTableName, ruleList);
        constraint = jSchema.getConstraintByName(constraintName);
        assertNotNull(constraint);
        assertEquals(attrList, constraint.getAttributes());
        assertEquals(ruleList, constraint.getRules());

        // CREATE  REVERSE UNIQUE INDEX ON "testgetrecordcounttask"("integer" DESC,"bigint" ASC);
        attrList.clear();
        ruleList.clear();
        attrList.add("integer");
        attrList.add("bigint");
        ruleList.add("integer DESC");
        ruleList.add("bigint ASC");
        constraintName = ConstraintNamingUtil.getReverseUniqueName(testTableName, attrList);
        constraint = jSchema.getConstraintByName(constraintName);
        assertNotNull(constraint);
        assertEquals(attrList, constraint.getAttributes());
        assertNotSame(ruleList, constraint.getRules());
        assertEquals("integer DESC", constraint.getRules().get(0));
        assertEquals("bigint DESC", constraint.getRules().get(1));

        // CREATE  INDEX ON "testgetrecordcounttask"("smallint" DESC,"smallint2" DESC);
        attrList.clear();
        ruleList.clear();
        attrList.add("smallint");
        attrList.add("smallint2");
        ruleList.add("smallint DESC");
        ruleList.add("smallint2 DESC");
        constraintName = ConstraintNamingUtil.getIndexName(testTableName, ruleList);
        constraint = jSchema.getConstraintByName(constraintName);
        assertNotNull(constraint);
        assertEquals(attrList, constraint.getAttributes());
        assertEquals(ruleList, constraint.getRules());

        // CREATE  REVERSE INDEX ON "testgetrecordcounttask"("numeric1" ASC,"numeric2" DESC);
        attrList.clear();
        ruleList.clear();
        attrList.add("numeric1");
        attrList.add("numeric2");
        ruleList.add("numeric1 ASC");
        ruleList.add("numeric2 DESC");
        constraintName = ConstraintNamingUtil.getReverseIndexName(testTableName, attrList);
        constraint = jSchema.getConstraintByName(constraintName);
        assertNotNull(constraint);
        assertEquals(attrList, constraint.getAttributes());
        assertEquals("numeric1 DESC", constraint.getRules().get(0));
        assertEquals("numeric2 DESC", constraint.getRules().get(1));

        task.setErrorMsg("errorMsg");
        task.execute();
        task.setErrorMsg(null);
        task.execute();
        task.cancel();
        task.execute();
    }

    @Override
    protected void tearDown() throws Exception {
        dropTestTable();
        super.tearDown();
    }
}
