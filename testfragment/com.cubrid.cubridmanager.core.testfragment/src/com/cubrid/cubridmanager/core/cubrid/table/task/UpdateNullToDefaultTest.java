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
package com.cubrid.cubridmanager.core.cubrid.table.task;

import com.cubrid.cubridmanager.core.SetupJDBCTestCase;

/**
 * Test the type of UpdateNull2Def
 *
 * <p>UpdateNull2DefTest Description
 *
 * @author lizhiqiang
 * @version 1.0 - 2009-9-17 created by lizhiqiang
 */
public class UpdateNullToDefaultTest extends SetupJDBCTestCase {
    private String testTableName = "testUpdateNull2Def";

    private boolean createTestTable() {
        String sql =
                "create table \""
                        + testTableName
                        + "\" ("
                        + "code integer auto_increment,"
                        + "name character varying(40),"
                        + "age integer"
                        + ")";
        return executeDDL(sql);
    }

    private boolean dropTestTable() {
        String sql = "drop table \"" + testTableName + "\"";
        return executeDDL(sql);
    }

    private boolean insertData(int count) {
        String sql =
                "insert into \""
                        + testTableName
                        + "\" "
                        + "(\"code\", \"name\") "
                        + "values (12, 'test')";
        for (int i = 0; i < count; i++) {
            int returnValue = executeUpdate(sql);
            if (returnValue == -1) {
                return false;
            }
        }
        return true;
    }

    public void testUpdate() {
        UpdateNullToDefault un2d = new UpdateNullToDefault(databaseInfo);
        un2d.setTable(testTableName);
        un2d.setColumn("age");
        un2d.setDefaultValue("10");
        un2d.execute();
        assertTrue(un2d.isSuccess());
        un2d.setErrorMsg("errorMsg");
        un2d.execute();
        un2d.setErrorMsg(null);
        un2d.execute();
        un2d.cancel();
        un2d.execute();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createTestTable();
        insertData(5);
    }

    @Override
    protected void tearDown() throws Exception {
        dropTestTable();
        super.tearDown();
    }
}
