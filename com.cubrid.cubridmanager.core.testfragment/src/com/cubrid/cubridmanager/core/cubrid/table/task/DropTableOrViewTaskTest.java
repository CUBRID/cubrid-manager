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
 * Test DropTableOrViewByJDBCTask
 *
 * @author pangqiren
 * @version 1.0 - 2010-1-5 created by pangqiren
 */
public class DropTableOrViewTaskTest extends SetupJDBCTestCase {

    private String testTableName = "testDropTableTask";
    private String testViewName = "testDropViewTask";

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

    private boolean createTestView() {
        String sql =
                "CREATE VIEW \""
                        + testViewName
                        + "\"(\"name\" VARCHAR,\"id\" INTEGER,\"password\" db_password,\"direct_groups\" SET,\"groups\" SET,\"authorization\" db_authorization,\"triggers\" SEQUENCE) AS select * from db_user";
        return executeDDL(sql);
    }

    public void testDropTable() throws Exception {
        if (createTestTable()) {
            // test normal case
            DropTableOrViewTask task = new DropTableOrViewTask(databaseInfo);
            task.setTableName(new String[] {testTableName});
            task.execute();
            assertTrue(task.isSuccess());
            // test exception case: connection is closed
            task.execute();
            // test exception case: have error
            task.setErrorMsg("error");
            task.execute();
            // test exception case: no table
            task = new DropTableOrViewTask(databaseInfo);
            task.setTableName(new String[] {"noexist"});
            task.execute();
            assertFalse(task.isSuccess());
        }
    }

    public void testDropView() throws Exception {
        if (createTestView()) {
            // test normal case
            DropTableOrViewTask task = new DropTableOrViewTask(databaseInfo);
            task.setViewName(new String[] {testViewName});
            task.execute();
            assertTrue(task.isSuccess());
        }
    }
}
