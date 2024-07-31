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
 * Test GetExecuteCasRunnerResultTask
 *
 * @author cn12978
 * @version 1.0 - 2009-6-24 created by cn12978
 */
public class GetExecuteCasRunnerResultTaskTest extends SetupEnvTestCase {

    public void testSend() throws Exception {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/logs/task/test.message/GetExecuteCasRunnerResult_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // composite message
        GetExecuteCasRunnerResultTask task = new GetExecuteCasRunnerResultTask(serverInfo);
        task.setBrokerName("query_editor");
        task.setUserName("dba");
        task.setPasswd("");
        task.setNumThread("1");
        task.setRepeatCount("1");
        task.setShowQueryResult("no");
        task.setDbName("demodb");
        task.setExecuteLogFile("yes");
        task.setLogFile("/home/newbie/cubrid-8.2.2/log/broker/sql_log/query_editor_5.sql.log");
        // compare
        assertEquals(msg, Tool.decryptContent(serverInfo, task.getRequest()));
    }

    public void testReceive() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/logs/task/test.message/GetExecuteCasRunnerResult_receive");
        String msg = Tool.getFileContent(filepath);

        TreeNode node = MessageUtil.parseResponse(msg);
        GetExecuteCasRunnerResultTask task = new GetExecuteCasRunnerResultTask(serverInfo);
        task.setResponse(node);
        task.getContent();
        // compare
        assertEquals("success", node.getValue("status"));
        // case 2
        msg = msg.replaceFirst("result_list:start", "result_list:start1");
        node = MessageUtil.parseResponse(msg);
        task.setResponse(node);
        task.getContent();
        // case 3
        msg = msg.replaceFirst("result_list:start1", "");
        msg = msg.replaceFirst("result_list:end", "");
        node = MessageUtil.parseResponse(msg);
        task.setResponse(node);
        task.getContent();
        // case 4
        filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/logs/task/test.message/GetExecuteCasRunnerResult_receive2");
        msg = Tool.getFileContent(filepath);

        node = MessageUtil.parseResponse(msg);
        GetExecuteCasRunnerResultTask task2 = new GetExecuteCasRunnerResultTask(serverInfo);
        task2.setResponse(node);
        task2.getContent();
        // exception case1
        task.setResponse(null);
        task.getContent();
        // exception case2
        task.setResponse(node);
        task.setErrorMsg("has error");
        task.getContent();
    }

    public void test() throws Exception {
        if (!isConnectRealEnv) {
            return;
        }
        GetLogListTask task = new GetLogListTask(serverInfo);
        task.execute();
        // compare
        assertEquals(true, task.isSuccess());
        GetLogListTask task2 = new GetLogListTask(serverInfo, "utf-8");
        task2.execute();
    }
}
