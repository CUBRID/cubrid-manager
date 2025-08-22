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

/**
 * test deleting all records task
 *
 * @author moulinwang
 * @version 1.0 - 2009-7-1 created by moulinwang
 */
public class DelAllRecordsTaskTest extends SetupJDBCTestCase {
    String testTableName = "testDelAllRecordsTask";
    String sql = null;

    private boolean createTestTable() {
        String sql =
                "create table \""
                        + testTableName
                        + "\" ("
                        + "code integer,"
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
                        + "(\"code\", \"name\", \"gender\", \"nation_code\") "
                        + "values (12, 'test', '1', '2')";
        for (int i = 0; i < count; i++) {
            int update = executeUpdate(sql);
            if (update == -1) {
                return false;
            }
        }
        return true;
    }

    public void testDelAllRecordsTask() {
        boolean success = createTestTable();
        System.out.println("DelAllRecords " + success);

        DelAllRecordsTask task = new DelAllRecordsTask(databaseInfo);
        task.getDeleteRecordsCount();
        task.setTableName(new String[] {testTableName});
        task.setWarningMsg(null);
        task.execute();
        assertEquals(0, ((int[]) task.getDeleteRecordsCount())[0]);

        insertData(10);

        task = new DelAllRecordsTask(databaseInfo);
        task.setTableName(new String[] {testTableName});
        task.setWhereCondition("where code=12");
        task.execute();
        assertEquals(10, ((int[]) task.getDeleteRecordsCount())[0]);
        task.setErrorMsg("err");
        task.execute();
        task.setErrorMsg(null);
        task.execute();
        task.cancel();
        task.execute();
        task.setTableName(null);
        task.getDeleteRecordsCount();
    }

    @Override
    protected void tearDown() throws Exception {
        dropTestTable();
        super.tearDown();
    }
}
