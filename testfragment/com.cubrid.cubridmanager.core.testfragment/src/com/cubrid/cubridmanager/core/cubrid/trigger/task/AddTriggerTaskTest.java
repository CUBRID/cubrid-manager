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
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import com.cubrid.cubridmanager.core.utils.ModelUtil.TriggerActionTime;
import com.cubrid.cubridmanager.core.utils.ModelUtil.TriggerConditionTime;
import com.cubrid.cubridmanager.core.utils.ModelUtil.TriggerEvent;
import com.cubrid.cubridmanager.core.utils.ModelUtil.TriggerStatus;

public class AddTriggerTaskTest extends SetupEnvTestCase {
    String dbname = "demodb";
    String triggerName = "test";
    TriggerConditionTime conditiontime = TriggerConditionTime.BEFORE;
    TriggerEvent eventType = TriggerEvent.INSERT;
    Trigger.TriggerAction action = Trigger.TriggerAction.INVALIDATE_TRANSACTION;
    String eventTarget = "athlete";
    TriggerActionTime actionTime = TriggerActionTime.AFTER;
    TriggerStatus status = TriggerStatus.ACTIVE;
    String priority = "0.0";

    private AddTriggerTask task;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        task = new AddTriggerTask(serverInfo);
    }

    public void testSend() throws Exception {
        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/trigger/task/test.message/addtrigger_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // composite message
        AddTriggerTask task = new AddTriggerTask(serverInfo);
        task.setDbName(dbname);
        task.setTriggerName(triggerName);
        task.setConditionTime(conditiontime);
        task.setEventType(eventType);
        task.setAction(action, null);
        task.setEventTarget(eventTarget);
        task.setActionTime(actionTime);
        task.setCondition("1=1");
        task.setStatus(status);
        task.setPriority(priority);
        // compare
        assertEquals(msg, task.getRequest());

        filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/trigger/task/test.message/addtrigger_send2");
        msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // composite message
        task = new AddTriggerTask(serverInfo);
        task.setDbName(dbname);
        task.setTriggerName("update_monitor");
        task.setConditionTime(conditiontime);
        task.setEventType(TriggerEvent.UPDATE);
        task.setAction(Trigger.TriggerAction.PRINT, "test");
        task.setEventTarget(eventTarget);
        task.setActionTime(actionTime);
        task.setCondition("if 1=1");
        task.setStatus(status);
        task.setPriority("0.02");
        // compare
        assertEquals(msg, task.getRequest());

        filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/trigger/task/test.message/addtrigger_send3");
        msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // composite message
        task = new AddTriggerTask(serverInfo);
        task.setDbName(dbname);
        task.setTriggerName("update_monitor");
        task.setConditionTime(conditiontime);
        task.setEventType(TriggerEvent.INSERT);
        task.setAction(Trigger.TriggerAction.REJECT, "test");
        task.setEventTarget(eventTarget);
        task.setActionTime(actionTime);
        task.setCondition("if 1=1");
        task.setStatus(status);
        task.setPriority("0.02");
        // compare
        assertEquals(msg, task.getRequest());
    }

    public void testReceive() throws Exception {
        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/trigger/task/test.message/addtrigger_receive");
        String msg = Tool.getFileContent(filepath);
        TreeNode node = MessageUtil.parseResponse(msg);

        task.setResponse(node);
        boolean success = task.isSuccess();
        assertTrue(success);

        filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/trigger/task/test.message/addtrigger_receive_error");
        msg = Tool.getFileContent(filepath);

        node = MessageUtil.parseResponse(msg);
        task.setResponse(node);
        boolean failure = task.isSuccess();
        assertFalse(failure);

        // assertEquals(" invalid use of keyword 'test', expecting { name }.",
        //		task.getErrorMsg());
        task.setCondition("");
        task.setAction(Trigger.TriggerAction.OTHER_STATEMENT, "test");
    }
}
