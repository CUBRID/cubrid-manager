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
package com.cubrid.cubridmanager.core.mondashboard.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.model.EnvInfo;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import java.util.ArrayList;
import java.util.List;

/**
 * Test GetHeartbeatNodeInfoTask
 *
 * @author pangqiren
 * @version 1.0 - 2010-6-9 created by pangqiren
 */
public class GetHeartbeatNodeInfoTaskTest extends SetupEnvTestCase {

    public void testSend() throws Exception {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/mondashboard/task/test.message/GetHeartbeatNodeInfoTask_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // composite message
        GetHeartbeatNodeInfoTask task = new GetHeartbeatNodeInfoTask(serverInfo);
        task.setAllDb(false);
        List<String> dbList = new ArrayList<String>();
        dbList.add("demodb");
        dbList.add("tb1");
        task.setDbList(dbList);
        // compare
        assertEquals(msg, task.getRequest());

        // test special case1
        ServerInfo server = new ServerInfo();
        server.setConnected(true);
        EnvInfo envInfo = new EnvInfo();
        envInfo.setServerVersion("CUBRID 2008 R2.0(8.2.0.1150)");
        envInfo.setOsInfo("NT");
        server.setEnvInfo(envInfo);
        task = new GetHeartbeatNodeInfoTask(server);
        task.execute();
        // test special case2
        envInfo.setServerVersion("CUBRID 2008 R2.0(8.3.1.1150)");
        task.execute();
    }

    public void testReceive() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/mondashboard/task/test.message/GetHeartbeatNodeInfoTask_receive");
        String msg = Tool.getFileContent(filepath);
        TreeNode node = MessageUtil.parseResponse(msg);
        GetHeartbeatNodeInfoTask task = new GetHeartbeatNodeInfoTask(serverInfo);
        task.setResponse(node);
        // compare
        assertTrue(task.getHostStatusInfo("192.168.1.222") != null);
        assertTrue(task.getDatabaseStatusInfo("haha") == null);
        assertTrue(task.getHAHostStatusList().size() > 0);
        task.setErrorMsg("error");
    }

    public void testSetAllDb() {
        GetHeartbeatNodeInfoTask task = new GetHeartbeatNodeInfoTask(serverInfo);
        task.setAllDb(true);
        task.setAllDb(false);
    }

    public void testGetDatabaseStatusInfo() {
        GetHeartbeatNodeInfoTask task = new GetHeartbeatNodeInfoTask(serverInfo);
        task.getDatabaseStatusInfo(databaseInfo.getDbName());
    }

    public void testGetNodeName() {
        GetHeartbeatNodeInfoTask task = new GetHeartbeatNodeInfoTask(serverInfo);
        task.setAllDb(true);
        List<String> dbList = new ArrayList<String>();
        dbList.add("unittestdb");
        task.setDbList(dbList);
        task.execute();
        task.getDatabaseStatusInfo(databaseInfo.getDbName());
    }
}
