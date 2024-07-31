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

import com.cubrid.cubridmanager.core.SetupJDBCTestCase;
import java.util.Locale;

/**
 * test getting record count of a table
 *
 * @author moulinwang
 * @version 1.0 - 2009-7-1 created by moulinwang
 */
public class GetRecordCountTaskTest extends SetupJDBCTestCase {
    String testTableName = "recordCountTest";
    String sql = null;
    private String testColumn = "code";

    private boolean createTestTable() {
        String sql =
                "create table \""
                        + testTableName
                        + "\" ("
                        + testColumn
                        + " integer,"
                        + "name character varying(40)  NOT NULL,"
                        + "gender character(1) ,"
                        + "nation_code character(3) "
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
                        + "(\""
                        + testColumn
                        + "\", \"name\", \"gender\", \"nation_code\") "
                        + "values (12, 'test', '1', '2')";
        for (int i = 0; i < count; i++) {
            int update = executeUpdate(sql);
            if (update == -1) {
                return false;
            }
        }
        return true;
    }

    public void testGetRecordCountTaskTest() {
        createTestTable();
        insertData(10);
        GetRecordCountTask task = new GetRecordCountTask(databaseInfo);
        int count = task.getRecordCount(testTableName, null);
        // assertEquals(10, count);

        int countWhere2 = task.getRecordCount(testTableName, " where 1=1;");
        assertEquals(-1, countWhere2);
        task.setErrorMsg("errorMsg");
        task.getRecordCount(sql);
        task.getRecordCountNoClose(sql);
        task.setErrorMsg(null);
        task.getRecordCount(sql);
        task.getRecordCountNoClose(sql);
        task.cancel();
        task.getRecordCount(sql);
        task.setErrorMsg(null);
        task.getRecordCountNoClose(sql);
    }

    public void testGetRecordCountTaskTest_2() {
        createTestTable();
        insertData(10);
        GetRecordCountTask task = new GetRecordCountTask(databaseInfo);
        int count = task.getRecordCount(testTableName, " where 1=1;");
        assertEquals(10, count);
    }

    public void testGetRecordCountStringTaskTest() {
        createTestTable();
        insertData(10);
        GetRecordCountTask task = new GetRecordCountTask(databaseInfo);
        int count = task.getRecordCount(testTableName, testColumn, null);
        assertEquals(10, count);
    }

    public void testGetRecordCountStringTaskTest_2() {
        createTestTable();
        insertData(10);
        GetRecordCountTask task = new GetRecordCountTask(databaseInfo);
        int count = task.getRecordCount(testTableName, testColumn, " where 1=1;");
        assertEquals(10, count);
    }

    public void testGetRecordCountNoClose() {
        createTestTable();
        insertData(10);
        GetRecordCountTask task = new GetRecordCountTask(databaseInfo);
        int count = task.getRecordCountNoClose(testTableName, null);
        assertEquals(10, count);
        int countWhere = task.getRecordCountNoClose(testTableName, "where 1=1");
        assertEquals(-1, countWhere);
        task.finish();
    }

    public void testGetRecordCountNoCloseString() {
        createTestTable();
        insertData(10);
        GetRecordCountTask task = new GetRecordCountTask(databaseInfo);
        String sql =
                "select count(*) from \"" + testTableName.toLowerCase(Locale.getDefault()) + "\"";
        int count = task.getRecordCountNoClose(sql);
        assertEquals(10, count);

        String sqlWhere =
                "select count(*) from \""
                        + testTableName.toLowerCase(Locale.getDefault())
                        + " where 1=1\"";
        int countWhere = task.getRecordCountNoClose(sqlWhere);
        assertEquals(-1, countWhere);
        task.finish();
    }

    @Override
    protected void tearDown() throws Exception {
        dropTestTable();
        super.tearDown();
    }
}
