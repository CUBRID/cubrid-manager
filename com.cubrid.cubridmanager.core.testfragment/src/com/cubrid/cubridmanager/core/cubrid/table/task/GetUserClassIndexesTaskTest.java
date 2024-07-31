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
import com.cubrid.cubridmanager.core.cubrid.table.model.TableIndex;
import java.util.List;

/**
 * Test the type of GetUserClassIndexesTask
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-12-9 created by lizhiqiang
 */
public class GetUserClassIndexesTaskTest extends SetupJDBCTestCase {
    String testTableName = "testGetUserClassIndexesTask";
    String testColumn = "testColumn";
    String testPkColumn = "testpk";
    String indexName = "index_on_testcolumn";
    String sql = null;

    private boolean createTestTable() {
        String sql =
                "create table \""
                        + testTableName
                        + "\" ("
                        + testPkColumn
                        + " integer,"
                        + testColumn
                        + " integer,"
                        + "CONSTRAINT pk_"
                        + testTableName
                        + "_"
                        + "testPkColumn"
                        + " PRIMARY KEY(\""
                        + testPkColumn
                        + "\")"
                        + " )";
        return executeDDL(sql);
    }

    private boolean createIndex() {
        String sql =
                "create index \""
                        + indexName
                        + "\" on \""
                        + testTableName
                        + "\"(\""
                        + testColumn
                        + "\")";
        return executeDDL(sql);
    }

    private boolean dropTestTable() {
        String sql = "drop table \"" + testTableName + "\"";
        return executeDDL(sql);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.table.task.GetUserClassIndexesTask#getIndexesNames(java.lang.String)}
     * .
     */
    public void testGetIndexesNames() {
        GetUserClassIndexesTask task = new GetUserClassIndexesTask(databaseInfo);
        List<TableIndex> indexes = task.getIndexesNames(testTableName);
        String actualIndexName = "";
        for (TableIndex dbIndex : indexes) {
            if (dbIndex.getIndexName() != null) {
                if (!dbIndex.getIndexName().startsWith("pk")) {
                    actualIndexName = dbIndex.getIndexName();
                }
            }
        }
        assertEquals(indexName, actualIndexName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createTestTable();
        createIndex();
    }

    @Override
    protected void tearDown() throws Exception {
        dropTestTable();
        super.tearDown();
    }
}
