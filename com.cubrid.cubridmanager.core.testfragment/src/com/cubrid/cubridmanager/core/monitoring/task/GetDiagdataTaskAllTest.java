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
package com.cubrid.cubridmanager.core.monitoring.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import java.util.ArrayList;
import java.util.List;

/**
 * Test GetDiagdataTask
 *
 * @author lizhiqiang 2009-5-11
 */
public class GetDiagdataTaskAllTest extends SetupEnvTestCase {
    private GetDiagdataTask task;

    public void testSend() throws Exception {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/monitoring/task/test.message/getdiagdata_send.txt");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // composite message
        task = new GetDiagdataTask(serverInfo);
        List<String> list = new ArrayList<String>();
        String str = "cas_mon_req";
        list.add(str);
        str = "cas_mon_tran";
        list.add(str);
        str = "cas_mon_act_session";
        list.add(str);
        task.buildMsg(list);
        task.setUsingSpecialDelimiter(true);
        // compare
        assertEquals(msg.trim(), getMessage().trim());
        str = "bas_mon_act_session";
        list.add(str);
        task.buildMsg(list);
    }

    public void testReceive() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/monitoring/task/test.message/getdiagdata_receive.txt");
        String msg = Tool.getFileContent(filepath);

        TreeNode node = MessageUtil.parseResponse(msg);
        // compare
        assertEquals("DIAG_DEL:success", node.getValue("status"));
        task = new GetDiagdataTask(serverInfo);
        task.setResponse(node);
        task.getResult();
        // exception case1
        task.setResponse(null);
        task.getResult();
        // exception case2
        task.setResponse(node);
        task.setErrorMsg("has error");
        task.getResult();
    }

    public void testExistDbAndTable() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        GetDiagdataTask task = new GetDiagdataTask(serverInfo);
        task.setDbname("demodb");
        List<String> list = new ArrayList<String>();
        String str = "mon_cub_query_opened_page";
        list.add(str);
        str = "cas_mon_req";
        list.add(str);
        str = "cas_mon_tran";
        list.add(str);
        str = "cas_mon_act_session";
        list.add(str);
        task.buildMsg(list);
        task.setUsingSpecialDelimiter(true);
        task.execute();
        task.getResult();
        // compare
        assertTrue(task.isSuccess());
        assertNull(task.getErrorMsg());
    }

    private String getMessage() {
        String message = "";
        String taskName = task.getTaskname();
        if (taskName != null && taskName.length() > 0) {
            message += "task:" + taskName + "\n";
        }
        if (serverInfo.getHostToken() != null && serverInfo.getHostToken().length() > 0) {
            message += "token:" + serverInfo.getHostToken() + "\n";
        }
        return message += task.getAppendSendMsg() + "\n\n";
    }
}
