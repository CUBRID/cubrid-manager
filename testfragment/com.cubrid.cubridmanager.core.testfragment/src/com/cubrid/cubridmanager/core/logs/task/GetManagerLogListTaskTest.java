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
package com.cubrid.cubridmanager.core.logs.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;

/**
 * Test GetManagerLogListTask
 *
 * @author cn12978
 * @version 1.0 - 2009-6-24 created by cn12978
 */
public class GetManagerLogListTaskTest extends SetupEnvTestCase {

    public void testSend() throws Exception {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/logs/task/test.message/GetManagerLogList_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // composite message
        GetManagerLogListTask task = new GetManagerLogListTask(serverInfo);
        // compare
        assertEquals(msg, task.getRequest());
        assertTrue(msg.equals(task.getRequest()));
        new GetManagerLogListTask(serverInfo, "utf-8");
    }

    public void testReceive() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/logs/task/test.message/GetManagerLogList_receive");
        String msg = Tool.getFileContent(filepath);

        TreeNode node = MessageUtil.parseResponse(msg);
        GetManagerLogListTask task = new GetManagerLogListTask(serverInfo);
        task.setResponse(node);
        task.getLogContent();
        // case 2
        msg = msg.replaceFirst("open:accesslog", "open:accesslog1");
        msg = msg.replaceFirst("open:errorlog", "open:errorlog1");
        node = MessageUtil.parseResponse(msg);
        task.setResponse(node);
        task.getLogContent();
        // case 3
        msg = msg.replaceFirst("open:accesslog1", "");
        msg = msg.replaceFirst("open:errorlog1", "");
        msg = msg.replaceFirst("close:accesslog", "");
        msg = msg.replaceFirst("close:errorlog", "");
        node = MessageUtil.parseResponse(msg);
        task.setResponse(node);
        task.getLogContent();
        // case 4
        filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/logs/task/test.message/GetManagerLogList_receive2");
        msg = Tool.getFileContent(filepath);

        node = MessageUtil.parseResponse(msg);
        GetManagerLogListTask task2 = new GetManagerLogListTask(serverInfo);
        task2.setResponse(node);
        task2.getLogContent();
        // exception case1
        task.setResponse(null);
        task.getLogContent();
        // exception case2
        task.setResponse(node);
        task.setErrorMsg("has error");
        task.getLogContent();

        //		//test isActive is true
        //		task.setResponse(node);
        //		assertTrue(task.isActive());
        //		//test isActive is false
        //		msg = msg.replaceFirst("is_active:Y", "is_active:N");
        //		node = MessageUtil.parseResponse(msg);
        //		task.setResponse(node);
        //		assertFalse(task.isActive());
        //		//test exception case 1
        //		task.setResponse(null);
        //		assertFalse(task.isActive());
        //		//test exception case 2
        //		task.setResponse(node);
        //		task.setErrorMsg("has error");
        //		assertFalse(task.isActive());
        // compare
        assertEquals("success", node.getValue("status"));
    }

    public void test() throws Exception {
        if (!isConnectRealEnv) {
            return;
        }
        GetManagerLogListTask task = new GetManagerLogListTask(serverInfo);
        task.execute();
        // compare
        assertEquals(true, task.isSuccess());
        GetManagerLogListTask task2 = new GetManagerLogListTask(serverInfo, "utf-8");
        task2.execute();
    }
}
