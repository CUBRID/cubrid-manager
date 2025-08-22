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

public class AnalyseSqlTaskTest extends SetupJDBCTestCase {

    public void testExecute() throws Exception {
        // test normal case
        AnalyseSqlTask task = new AnalyseSqlTask(databaseInfo);
        task.addSqls("select * from db_user");
        task.execute();
        assertEquals(task.getResult().size() > 0, true);
        // test exception case: have error
        task.setErrorMsg("errorMsg");
        task.execute();
        // test exception case: the connection is close
        task.setErrorMsg(null);
        task.execute();

        // test exception case, the connection is cancel
        task.cancel();
        task.execute();

        // test exception case, no sql
        task = new AnalyseSqlTask(databaseInfo);
        task.execute();

        // test exception case, throw SQLException
        task = new AnalyseSqlTask(databaseInfo);
        task.addSqls("select * from db_user1");
        task.execute();

        // test multi sql
        task = new AnalyseSqlTask(databaseInfo);
        task.addSqls("select * from db_user");
        task.addSqls("select * from db_user");
        task.execute();
    }
}
