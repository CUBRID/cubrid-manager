/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.cubridmanager.core.cubrid.table.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupJDBCTestCase;

/**
 * @author fulei
 * @version 1.0 - 2012-12-21 created by fulei
 */
public class GetAllSchemaTaskTest extends SetupJDBCTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE \"test1\"(");
        sb.append(StringUtil.NEWLINE);
        sb.append("\"id\" integer AUTO_INCREMENT NOT NULL,");
        sb.append(StringUtil.NEWLINE);
        sb.append("\"name\" character varying(10));");
        executeDDL(sb.toString());

        sb = new StringBuilder();
        sb.append(
                "ALTER TABLE \"test1\" PARTITION BY RANGE ([name]) (PARTITION p_name VALUES LESS THAN ('1'));");
        executeDDL(sb.toString());

        sb = new StringBuilder();
        sb.append("ALTER SERIAL \"test1_ai_id\"  START WITH 1;");
        executeDDL(sb.toString());

        sb = new StringBuilder();
        sb.append(StringUtil.NEWLINE);
        sb.append("CREATE TABLE \"test2\"(");
        sb.append(StringUtil.NEWLINE);
        sb.append("\"id\" integer AUTO_INCREMENT,");
        sb.append(StringUtil.NEWLINE);
        sb.append("\"name\" character varying(10));");
        executeDDL(sb.toString());

        sb = new StringBuilder();
        sb.append("ALTER SERIAL \"test2_ai_id\"  START WITH 1;");
        executeDDL(sb.toString());

        sb = new StringBuilder();
        sb.append("ALTER TABLE \"test1\" ADD PRIMARY KEY(\"id\");");
        executeDDL(sb.toString());

        sb = new StringBuilder();
        sb.append(
                "ALTER TABLE \"test2\" ADD FOREIGN KEY (\"id\") REFERENCES \"test1\"(\"id\") ON DELETE RESTRICT ON UPDATE RESTRICT;");
        executeDDL(sb.toString());

        sb.append("");
        sb.append(StringUtil.NEWLINE);
        sb.append("");
        sb.append(StringUtil.NEWLINE);
        sb.append("");
        sb.append(StringUtil.NEWLINE);
        sb.append("");
        sb.append(StringUtil.NEWLINE);

        sb.append("");
        sb.append(StringUtil.NEWLINE);
        sb.append("");
        sb.append(StringUtil.NEWLINE);
    }

    public void testGetAllSchema() {
        GetAllSchemaTask task = new GetAllSchemaTask(databaseInfo);
        task.execute();

        assertNull(task.getErrorMsg());
    }

    protected void tearDown() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("DROP TABLE \"test2\";");
        executeDDL(sb.toString());

        sb = new StringBuilder();
        sb.append("DROP TABLE \"test1\";");
        executeDDL(sb.toString());
    }
}
