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
package com.cubrid.cubridmanager.core.replication.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import java.util.List;
import java.util.Map;

/**
 * Test GetReplPerformanceTask
 *
 * @author pangqiren
 * @version 1.0 - 2010-1-4 created by pangqiren
 */
public class GetReplPerformanceTaskTest extends SetupEnvTestCase {

    public void testSend() throws Exception {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/replication/task/test.message/GetReplPerformance_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // composite message
        GetReplPerformanceTask task = new GetReplPerformanceTask(serverInfo);
        task.setFilePath("/home/biaozhang/cubrid/databases/distdb_pang/distdb_pang.perf");
        task.setStart("1");
        task.setEnd("100");
        // compare
        assertEquals(msg, task.getRequest());
    }

    public void testReceive() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;
        // case 1
        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/replication/task/test.message/GetReplPerformance_receive");
        String msg = Tool.getFileContent(filepath);
        TreeNode node = MessageUtil.parseResponse(msg);
        GetReplPerformanceTask task = new GetReplPerformanceTask(serverInfo);
        task.setResponse(node);
        List<Map<String, String>> list = task.loadPerformanceData();
        assertTrue(list.size() == 1);
        // case 2
        msg = msg.replaceAll("line:.*\n", "");
        node = MessageUtil.parseResponse(msg);
        task.setResponse(node);
        list = task.loadPerformanceData();
        assertTrue(list.size() == 0);
        // case 3
        msg = msg.replaceAll("open:log", "open:aa");
        node = MessageUtil.parseResponse(msg);
        task.setResponse(node);
        task.loadPerformanceData();
        msg = msg.replaceAll("open:aa", "");
        msg = msg.replaceAll("close:log", "");
        node = MessageUtil.parseResponse(msg);
        task.setResponse(node);
        task.loadPerformanceData();

        // test exception case 1
        task.setResponse(null);
        assertTrue(task.loadPerformanceData() == null);
        // test exception case 2
        task.setResponse(node);
        task.setErrorMsg("has error");
        assertTrue(task.loadPerformanceData() == null);
    }

    public void testRealEnv() throws Exception {
        if (isConnectRealEnv) {
            GetReplPerformanceTask task = new GetReplPerformanceTask(serverInfo);
            task.setFilePath("/home/pang/cubrid/databases/distdb_pang/distdb_pang.perf");
            task.setStart("1");
            task.setEnd("100");
            task.execute();
            // compare
            assertEquals(true, task.isSuccess());
        }
    }
}
