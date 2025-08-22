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
import java.util.ArrayList;
import java.util.List;

/**
 * Test UpdateStatisticsTask
 *
 * @author pangqiren
 * @version 1.0 - 2010-3-24 created by pangqiren
 */
public class UpdateStatisticsTaskTest extends SetupJDBCTestCase {

    String testTableName = "testUpdateStatistics";
    String sql = null;

    private boolean createTestTable() {
        String sql =
                "create table \""
                        + testTableName
                        + "\" ("
                        + "empno char(10) not null unique,"
                        + "empname varchar(20) not null,"
                        + "deptname varchar(20), "
                        + "hiredate date"
                        + ")"
                        + "partition by range (extract (year from hiredate)) "
                        + "(partition h2000 values less than (2000),"
                        + "partition h2003 values less than (2003),"
                        + "partition hmax values less than  maxvalue)";
        return executeDDL(sql);
    }

    private boolean dropTestTable() {
        String sql = "drop table \"" + testTableName + "\"";
        return executeDDL(sql);
    }

    public void testUpdateStatistics() {
        createTestTable();
        UpdateStatisticsTask task = new UpdateStatisticsTask(databaseInfo);
        List<String> sqlList = new ArrayList<String>();
        String sql = "ALTER TABLE " + testTableName + " ANALYZE PARTITION h2000";
        sqlList.add(sql);
        sql = "ALTER TABLE " + testTableName + " ANALYZE PARTITION h2003";
        sqlList.add(sql);
        task.setSqlList(sqlList);
        task.execute();
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
