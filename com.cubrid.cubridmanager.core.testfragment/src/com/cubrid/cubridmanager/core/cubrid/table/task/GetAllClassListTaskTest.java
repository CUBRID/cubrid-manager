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
import com.cubrid.cubridmanager.core.cubrid.table.model.ClassInfo;
import java.util.List;

/**
 * test getting all tables of a database
 *
 * @author moulinwang
 * @version 1.0 - 2009-7-1 created by moulinwang
 */
public class GetAllClassListTaskTest extends SetupJDBCTestCase {

    public void testGetAllClassListTaskTest() {

        // test getSchema method
        // test normal case
        GetAllClassListTask task = new GetAllClassListTask(databaseInfo);
        List<ClassInfo> allClassInfoList = task.getSchema(false, true);
        boolean found = false;
        for (ClassInfo c : allClassInfoList) {
            if (c.getClassName().equals("_db_attribute")) {
                found = true;
                break;
            }
        }
        assertTrue(found);

        task = new GetAllClassListTask(databaseInfo);
        allClassInfoList = task.getSchema(false, false);
        found = false;
        for (ClassInfo c : allClassInfoList) {
            if (c.getClassName().equals("db_attribute")) {
                found = true;
                break;
            }
        }
        assertTrue(found);

        // test exception case:connection is closed
        task.getSchema(false, false);
        // test exception case: have error
        task.setErrorMsg("error");
        task.getSchema(false, false);

        // test getAllClassInfoList method
        // test normal case
        task = new GetAllClassListTask(databaseInfo);
        allClassInfoList = task.getAllClassInfoList();
        found = false;
        for (ClassInfo c : allClassInfoList) {
            if (c.getClassName().equals("db_attribute")) {
                found = true;
                break;
            }
        }
        assertTrue(found);

        // test exception case:connection is closed
        task.getAllClassInfoList();
        // test exception case: have error
        task.setErrorMsg("error");
        task.getAllClassInfoList();

        // test getClassInfoTaskExcute method
        // test normal case
        task = new GetAllClassListTask(databaseInfo);
        task.setTableName("Db_attribute");
        task.getClassInfoTaskExcute();
        ClassInfo c = task.getClassInfo();
        found = false;
        if (c.getClassName().equals("db_attribute")) {
            found = true;
        }
        assertTrue(found);

        // test exception case:connection is closed
        task.getClassInfoTaskExcute();
        // test exception case: have error
        task.setErrorMsg("error");
        task.getClassInfoTaskExcute();

        // test exception case: no table
        task = new GetAllClassListTask(databaseInfo);
        task.getClassInfoTaskExcute();
    }
}
