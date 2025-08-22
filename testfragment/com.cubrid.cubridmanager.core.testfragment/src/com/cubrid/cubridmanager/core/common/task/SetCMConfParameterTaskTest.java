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
import com.cubrid.cubridmanager.core.common.model.ConfConstants;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test SetCMConfParameterTask class
 *
 * @author pangqiren
 * @version 1.0 - 2010-1-5 created by pangqiren
 */
public class SetCMConfParameterTaskTest extends SetupEnvTestCase {

    public void testSend() throws Exception {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;
        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/common/task/test.message/setcmconfpara_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // composite message
        SetCMConfParameterTask task = new SetCMConfParameterTask(serverInfo);
        Map<String, String> map = new HashMap<String, String>();
        map.put(ConfConstants.CM_PORT, "8001");
        map.put(ConfConstants.ALLOW_USER_MULTI_CONNECTION, "YES");
        map.put(ConfConstants.SERVER_LONG_QUERY_TIME, "10");
        map.put(ConfConstants.MONITOR_INTERVAL, "5");

        task.setConfParameters(map);
        // compare
        // assertEquals(msg, task.getRequest());

    }

    public void testSend2() throws Exception {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;
        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/common/task/test.message/setcmconfpara_send2");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // composite message
        SetCMConfParameterTask task = new SetCMConfParameterTask(serverInfo);
        Map<String, String> map = new HashMap<String, String>();
        map.put(ConfConstants.CM_PORT, "8001");
        map.put(ConfConstants.ALLOW_USER_MULTI_CONNECTION, "YES");
        map.put(ConfConstants.SERVER_LONG_QUERY_TIME, "10");
        map.put(ConfConstants.MONITOR_INTERVAL, "5");
        map.put(ConfConstants.BROKER_PORT, "33000");
        map.put(ConfConstants.DB_VOLUME_SIZE, "512M");
        map.put(ConfConstants.LOG_VOLUME_SIZE, "512M");

        task.setConfParameters(map);
        // compare
        // assertEquals(msg, task.getRequest());

    }

    public void testReceive() throws Exception {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;
        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/common/task/test.message/setcmconfpara_receive");
        String msg = Tool.getFileContent(filepath);

        TreeNode node = MessageUtil.parseResponse(msg);
        // compare
        assertEquals("success", node.getValue("status"));
    }

    public void testSetContent() throws Exception {
        SetCMConfParameterTask task = new SetCMConfParameterTask(serverInfo);
        List<String> list = new ArrayList<String>();
        list.add("abc");
        list.add("def");
        task.setConfContents(list);
    }
}
