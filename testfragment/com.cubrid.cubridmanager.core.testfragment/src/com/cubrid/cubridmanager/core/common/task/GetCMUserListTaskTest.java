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
package com.cubrid.cubridmanager.core.common.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;

/**
 * Test GetCMUserListTask class
 *
 * @author pangqiren
 * @version 1.0 - 2010-1-5 created by pangqiren
 */
public class GetCMUserListTaskTest extends SetupEnvTestCase {

    public void testSend() throws Exception {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;
        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/common/task/test.message/getcmuserlist_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        GetCMUserListTask task = new GetCMUserListTask(serverInfo);
        // compare
        assertEquals(msg, task.getRequest());
        assertTrue(msg.equals(task.getRequest()));
    }

    public void testReceive() throws Exception {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;
        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/common/task/test.message/getcmuserlist_receive");
        String msg = Tool.getFileContent(filepath);

        TreeNode node = MessageUtil.parseResponse(msg);
        GetCMUserListTask task = new GetCMUserListTask(serverInfo);
        task.setResponse(node);
        task.getServerUserInfoList();

        GetEnvInfoTask envTask = new GetEnvInfoTask(serverInfo);
        envTask.setResponse(null);
        envTask.loadEnvInfo();
        // compare
        assertEquals("success", node.getValue("status"));

        // case2
        filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/common/task/test.message/getcmuserlist_receive2");
        msg = Tool.getFileContent(filepath);

        node = MessageUtil.parseResponse(msg);
        GetCMUserListTask task2 = new GetCMUserListTask(serverInfo);
        task2.setResponse(node);
        task2.getServerUserInfoList();
        // case3
        filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/common/task/test.message/getcmuserlist_receive3");
        msg = Tool.getFileContent(filepath);

        node = MessageUtil.parseResponse(msg);
        GetCMUserListTask task3 = new GetCMUserListTask(serverInfo);
        task3.setResponse(node);
        task3.getServerUserInfoList();
        // exception case1
        task.setResponse(null);
        task.getServerUserInfoList();
        // exception case2
        task.setResponse(node);
        task.setErrorMsg("has error");
        task.getServerUserInfoList();
    }
}
