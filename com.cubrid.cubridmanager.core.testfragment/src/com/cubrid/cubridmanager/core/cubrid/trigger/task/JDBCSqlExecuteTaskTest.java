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
package com.cubrid.cubridmanager.core.cubrid.trigger.task;

import com.cubrid.cubridmanager.core.SetupJDBCTestCase;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * TODO: how to write comments The purpose of the class Known bugs The development/maintenance
 * history of the class Document applicable invariants The concurrency strategy
 *
 * <p>JDBCSqlExecuteTaskTest Description
 *
 * @author Administrator
 * @version 1.0 - 2010-12-21 created by Administrator
 */
public class JDBCSqlExecuteTaskTest extends SetupJDBCTestCase {
    JDBCSqlExecuteTask jdbcTask;
    String testTableName = "testTable";
    String testTriggerName = "testTrigger";

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        createTestTable();
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.trigger.task.JDBCSqlExecuteTask#execute()} .
     */
    public void testExecuteOne() {
        String taskName = "Add trigger \"" + testTriggerName + "\"";
        String createSQL =
                "CREATE TRIGGER \""
                        + testTriggerName
                        + "\" BEFORE INSERT ON \""
                        + testTableName
                        + "\" EXECUTE REJECT";
        jdbcTask = new JDBCSqlExecuteTask(taskName, databaseInfo, createSQL);
        jdbcTask.execute();
        // assertNull(jdbcTask.getErrorMsg());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.trigger.task.JDBCSqlExecuteTask#execute()} .
     */
    public void testExecuteTwo() {
        String taskName = "Add trigger \"" + testTriggerName + "\"";
        String testTableName = "noExistTable";
        String createSQL =
                "CREATE TRIGGER \""
                        + testTriggerName
                        + "\" BEFORE INSERT ON \""
                        + testTableName
                        + "\" EXECUTE REJECT";
        jdbcTask = new JDBCSqlExecuteTask(taskName, databaseInfo, createSQL);
        jdbcTask.execute();
        assertNotNull(jdbcTask.getErrorMsg());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.trigger.task.JDBCSqlExecuteTask#execute()} .
     */
    public void testExecuteThree() {
        String taskName = "Add trigger \"" + testTriggerName + "\"";
        String testTableName = "noExistTable";
        String createSQL =
                "CREATE TRIGGER \""
                        + testTriggerName
                        + "\" BEFORE INSERT ON \""
                        + testTableName
                        + "\" EXECUTE REJECT";
        jdbcTask = new JDBCSqlExecuteTask(taskName, databaseInfo, createSQL);
        Connection connection = jdbcTask.getConnection();
        try {
            connection.close();
            jdbcTask.execute();
            assertNotNull(jdbcTask.getErrorMsg());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.trigger.task.JDBCSqlExecuteTask#JDBCSqlExecuteTask(java.lang.String,
     * com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo)} .
     */
    public void testJDBCSqlExecuteTaskStringDatabaseInfo() {
        String taskName = "Add trigger \"" + testTriggerName + "\"";
        jdbcTask = new JDBCSqlExecuteTask(taskName, databaseInfo);
    }

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

    private boolean dropTestTrigger() {
        String sql = "DROP TRIGGER \"" + testTriggerName + "\"";
        return executeDDL(sql);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        dropTestTable();
        dropTestTrigger();
    }
}
