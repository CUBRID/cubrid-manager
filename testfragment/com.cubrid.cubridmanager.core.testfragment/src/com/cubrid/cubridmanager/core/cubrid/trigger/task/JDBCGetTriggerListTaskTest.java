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
import java.util.List;

/**
 * Test JDBCGetTriggerList
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-12-21 created by lizhiqiang
 */
public class JDBCGetTriggerListTaskTest extends SetupJDBCTestCase {

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
     * com.cubrid.cubridmanager.core.cubrid.trigger.task.JDBCGetTriggerListTask#execute()} .
     */
    public void testExecute() {
        JDBCGetTriggerListTask task = new JDBCGetTriggerListTask(databaseInfo);
        task.execute();
        List<Trigger> list = task.getTriggerInfoList();
        assertTrue(list.size() > 0);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.trigger.task.JDBCGetTriggerListTask#getStatus(int)} .
     */
    public void testGetStatus() {
        String result = JDBCGetTriggerListTask.getStatus(0);
        assertEquals("INVALID", result);

        result = JDBCGetTriggerListTask.getStatus(1);
        assertEquals("INACTIVE", result);

        result = JDBCGetTriggerListTask.getStatus(2);
        assertEquals("ACTIVE", result);

        result = JDBCGetTriggerListTask.getStatus(3);
        assertEquals("", result);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.trigger.task.JDBCGetTriggerListTask#getEventType(int)} .
     */
    public void testGetEventType() {
        String result = JDBCGetTriggerListTask.getEventType(0);
        assertEquals("UPDATE", result);

        result = JDBCGetTriggerListTask.getEventType(1);
        assertEquals("STATEMENT UPDATE", result);

        result = JDBCGetTriggerListTask.getEventType(2);
        assertEquals("DELETE", result);

        result = JDBCGetTriggerListTask.getEventType(3);
        assertEquals("STATEMENT DELETE", result);

        result = JDBCGetTriggerListTask.getEventType(4);
        assertEquals("INSERT", result);

        result = JDBCGetTriggerListTask.getEventType(5);
        assertEquals("STATEMENT INSERT", result);

        result = JDBCGetTriggerListTask.getEventType(8);
        assertEquals("COMMIT", result);

        result = JDBCGetTriggerListTask.getEventType(9);
        assertEquals("ROLLBACK", result);

        result = JDBCGetTriggerListTask.getEventType(6);
        assertEquals("", result);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.trigger.task.JDBCGetTriggerListTask#getAction(int,
     * java.lang.String)} .
     */
    public void testGetAction() {
        String result = JDBCGetTriggerListTask.getAction(0, "ad");
        assertEquals("ad", result);

        result = JDBCGetTriggerListTask.getAction(2, "ad");
        assertEquals("REJECT", result);

        result = JDBCGetTriggerListTask.getAction(3, "ad");
        assertEquals("INVALIDATE TRANSACTION", result);

        result = JDBCGetTriggerListTask.getAction(4, "ad");
        assertEquals("PRINT 'ad'", result);

        result = JDBCGetTriggerListTask.getAction(5, "ad");
        assertEquals("ad", result);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.trigger.task.JDBCGetTriggerListTask#getConditionTime(int)}
     * .
     */
    public void testGetConditionTime() {
        String result = JDBCGetTriggerListTask.getConditionTime(0);
        assertEquals("", result);

        result = JDBCGetTriggerListTask.getConditionTime(1);
        assertEquals("BEFORE", result);

        result = JDBCGetTriggerListTask.getConditionTime(2);
        assertEquals("AFTER", result);

        result = JDBCGetTriggerListTask.getConditionTime(3);
        assertEquals("DEFERRED", result);
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
