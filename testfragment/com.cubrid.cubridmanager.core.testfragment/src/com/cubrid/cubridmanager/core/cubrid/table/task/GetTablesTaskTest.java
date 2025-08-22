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
import java.util.List;

/**
 * Test GetTablesTask class
 *
 * @author moulinwang
 * @version 1.0 - 2009-7-1 created by moulinwang
 */
public class GetTablesTaskTest extends SetupJDBCTestCase {

    private String testTableName = "testusertable";

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

    public void testGetTablesTask() {
        dropTestTable();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!createTestTable()) {
            return;
        }
        GetTablesTask task = new GetTablesTask(databaseInfo);
        List<String> list = task.getAllTableAndViews();
        assertTrue(list.contains(testTableName));
        assertTrue(list.contains("db_attr_setdomain_elm"));
        assertTrue(list.contains("_db_attribute"));

        task = new GetTablesTask(databaseInfo);
        list = task.getAllTables();
        assertTrue(list.contains(testTableName));
        assertTrue(list.contains("_db_attribute"));
        assertFalse(list.contains("db_attr_setdomain_elm"));

        task = new GetTablesTask(databaseInfo);
        list = task.getSystemTables();
        assertTrue(list.contains("_db_attribute"));
        assertFalse(list.contains(testTableName));
        assertFalse(list.contains("db_attr_setdomain_elm"));

        task = new GetTablesTask(databaseInfo);
        list = task.getUserTables();
        assertTrue(list.contains(testTableName));
        assertFalse(list.contains("_db_attribute"));
        assertFalse(list.contains("db_attr_setdomain_elm"));

        dropTestTable();
        task.setErrorMsg("errorMsg");
        task.getTables("");
        task.setErrorMsg(null);
        task.getTables("");
        task.cancel();
        task.getTables("");
    }
}
