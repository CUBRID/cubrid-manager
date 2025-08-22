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
package com.cubrid.cubridmanager.core.cubrid.user.task;

import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import com.cubrid.cubridmanager.core.cubrid.database.model.UserSendObj;

/**
 * Test UpdateAddUserTask
 *
 * @author pangqiren
 * @version 1.0 - 2010-1-8 created by pangqiren
 */
public class UpdateAddUserTaskTest extends SetupEnvTestCase {

    public void testSend() throws Exception {
        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/user/task/test.message/UpdateAddUser_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // composite message
        UpdateAddUserTask task = new UpdateAddUserTask(serverInfo, true);
        UserSendObj userSendObj = new UserSendObj();
        userSendObj.setDbname("demodb");
        userSendObj.setUsername("pang");
        userSendObj.setUserpass("123456");
        userSendObj.addGroups("public");
        userSendObj.addAddmembers("wang");
        userSendObj.addRemovemembers("qi");
        userSendObj.addAuthorization("athlete", "1");
        userSendObj.addAuthorization("testdropviewtask", "1");
        task.setUserSendObj(userSendObj);

        assertEquals(msg, Tool.decryptContent(serverInfo, task.getAppendSendMsg()));

        UpdateAddUserTask task831 = new UpdateAddUserTask(serverInfo831, true);
        userSendObj = new UserSendObj();
        userSendObj.setDbname("demodb");
        userSendObj.setUsername("pang");
        userSendObj.setUserpass("123456");
        userSendObj.addGroups("public");
        userSendObj.addAddmembers("wang");
        userSendObj.addRemovemembers("qi");
        userSendObj.addAuthorization("athlete", "1");
        userSendObj.addAuthorization("testdropviewtask", "1");
        task831.setUserSendObj(userSendObj);
        assertEquals(msg, Tool.decryptContent(serverInfo831, task831.getAppendSendMsg()));
    }

    public void testReceive() throws Exception {

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/user/task/test.message/UpdateAddUser_receive");
        String msg = Tool.getFileContent(filepath);
        TreeNode node = MessageUtil.parseResponse(msg);

        UpdateAddUserTask task = new UpdateAddUserTask(serverInfo, true);
        task.setResponse(node);
        assertTrue(task.isSuccess());

        task = new UpdateAddUserTask(serverInfo831, true);
        task.setResponse(node);
        assertTrue(task.isSuccess());
    }
}
