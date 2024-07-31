/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.cubridmanager.core.cubrid.trigger.task;

import com.cubrid.common.core.common.model.Trigger;
import com.cubrid.cubridmanager.core.SetupJDBCTestCase;
import com.cubrid.cubridmanager.core.Tool;
import java.util.List;

public class MultiTriggerTest extends SetupJDBCTestCase {
    String testTable1 = "testtable1";
    String testTrigger1 = "testtrigger1";

    private void createTestTableAndTrigger(String fileName) throws Exception {
        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/trigger/task/test.create.trigger/"
                                + fileName);
        String msg = Tool.getFileContent(filepath);
        String createSuperSQL1 = msg;
        String[] strs = msg.split(";");
        if (createSuperSQL1 != null) {
            for (String str : strs) {
                if (!str.trim().equals("")) {
                    executeDDL(str);
                }
            }
        }
    }

    // before insert+Reject
    public void test0() throws Exception {
        createTestTableAndTrigger("drop.trigger0.txt");
        createTestTableAndTrigger("trigger0.txt");
        GetTriggerListTask task = new GetTriggerListTask(serverInfo);
        task.setDbName(testDbName);
        task.execute();
        assertTrue(task.isSuccess());
        List<Trigger> triggerList = task.getTriggerInfoList();
        //		Trigger testTrigger1 = getTestTrigger(triggerList, "testtrigger1");
        Trigger testTrigger2 = getTestTrigger(triggerList, "testtrigger2");
        Trigger testTrigger3 = getTestTrigger(triggerList, "testtrigger3");
        Trigger testTrigger4 = getTestTrigger(triggerList, "testtrigger4");
        Trigger testTrigger5 = getTestTrigger(triggerList, "testtrigger5");
        Trigger testTrigger6 = getTestTrigger(triggerList, "testtrigger6");
        Trigger testTrigger7 = getTestTrigger(triggerList, "testtrigger7");
        Trigger testTrigger8 = getTestTrigger(triggerList, "testtrigger8");
        Trigger testTrigger9 = getTestTrigger(triggerList, "testtrigger9");

        //		assertEquals(null, testTrigger1.getConditionTime());
        assertEquals(null, testTrigger2.getConditionTime());
        assertEquals(null, testTrigger3.getConditionTime());
        assertEquals(null, testTrigger4.getConditionTime());
        assertEquals(null, testTrigger5.getConditionTime());
        assertEquals(null, testTrigger6.getConditionTime());
        assertEquals(null, testTrigger7.getConditionTime());
        assertEquals(null, testTrigger8.getConditionTime());
        assertEquals(null, testTrigger9.getConditionTime());

        //		assertEquals("BEFORE", testTrigger1.getActionTime());
        assertEquals("AFTER", testTrigger2.getActionTime());
        assertEquals("DEFERRED", testTrigger3.getActionTime());
        assertEquals("AFTER", testTrigger4.getActionTime());
        assertEquals("AFTER", testTrigger5.getActionTime());
        assertEquals("DEFERRED", testTrigger6.getActionTime());
        assertEquals("DEFERRED", testTrigger7.getActionTime());
        assertEquals("AFTER", testTrigger8.getActionTime());
        assertEquals("DEFERRED", testTrigger9.getActionTime());
    }

    /**
     * get trigger by name
     *
     * @param triggerList
     */
    private Trigger getTestTrigger(List<Trigger> triggerList, String triggerName) {
        for (Trigger trigger : triggerList) {
            if (trigger.getName().equals(triggerName.toLowerCase())) {
                return trigger;
            }
        }
        return null;
    }

    @Override
    protected void tearDown() throws Exception {
        dropTestTableAndTrigger();

        super.tearDown();
    }

    private boolean dropTestTableAndTrigger() throws Exception {
        String sql = "drop trigger \"" + testTrigger1 + "\"";
        executeDDL(sql);
        sql = "drop table \"" + testTable1 + "\"";
        return executeDDL(sql);
    }
}
