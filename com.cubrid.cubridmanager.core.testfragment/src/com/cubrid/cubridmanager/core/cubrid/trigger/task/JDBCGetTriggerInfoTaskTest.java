/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
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

import com.cubrid.common.core.common.model.Trigger;
import com.cubrid.cubridmanager.core.SetupJDBCTestCase;

/**
 * Test the type of JDBCGetTriggerInfoTask
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-12-21 created by lizhiqiang
 */
public class JDBCGetTriggerInfoTaskTest extends SetupJDBCTestCase {

    String testTableName = "testTable";
    String testTriggerName = "testTrigger";

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        createTestTable();
        createTestTrigger();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception {
        dropTestTrigger();
        dropTestTable();
        super.tearDown();
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.trigger.task.JDBCGetTriggerInfoTask#getTriggerInfo(java.lang.String)}
     * .
     */
    public void testGetTriggerInfo() {
        JDBCGetTriggerInfoTask task = new JDBCGetTriggerInfoTask(databaseInfo);
        Trigger trigger = task.getTriggerInfo(testTriggerName.toLowerCase());
        assertNotNull(trigger);
        assertEquals(testTriggerName.toLowerCase(), trigger.getName());

        task = new JDBCGetTriggerInfoTask(databaseInfo);
        task.cancel();
        task.getTriggerInfo(testTriggerName.toLowerCase());
        assertNotNull(task.getErrorMsg());
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

    private boolean createTestTrigger() {
        String sql =
                "CREATE TRIGGER \""
                        + testTriggerName
                        + "\" BEFORE INSERT ON \""
                        + testTableName
                        + "\" EXECUTE REJECT";
        return executeDDL(sql);
    }

    private boolean dropTestTrigger() {
        String sql = "DROP TRIGGER \"" + testTriggerName + "\"";
        return executeDDL(sql);
    }
}
