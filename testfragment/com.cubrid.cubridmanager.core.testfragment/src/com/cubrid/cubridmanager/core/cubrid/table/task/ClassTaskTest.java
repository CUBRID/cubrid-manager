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
package com.cubrid.cubridmanager.core.cubrid.table.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;

public class ClassTaskTest extends SetupEnvTestCase {
    public void testSend() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/table/task/test.message/class_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // composite message
        ClassTask task = new ClassTask(databaseInfo, null);
        task.setDbName("demodb");
        task.setClassName("_db_attribute");
        // compare
        assertEquals(msg, task.getRequest());
        task.getSchemaInfo();
        task.execute();
        task.getSchemaInfo();
    }

    public void testReceive() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/table/task/test.message/class_receive");
        String msg = Tool.getFileContent(filepath);

        TreeNode node = MessageUtil.parseResponse(msg);
        // compare
        assertEquals("success", node.getValue("status"));
        node = node.getChildren().get(0);
        assertEquals("demodb", node.getValue("dbname"));
        assertEquals("_db_attribute", node.getValue("classname"));
        assertEquals("system", node.getValue("type"));
    }

    public void testExistDbAndTable() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<table.clazz.001.req.txt>");

        ClassTask task = new ClassTask(databaseInfo);
        task.setDbName("demodb");
        task.setClassName("code");
        task.execute();

        assertTrue(task.isSuccess());
        assertNull(task.getErrorMsg());
    }

    public void testNotExistDbAndExistTable() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<table.clazz.002.req.txt>");

        ClassTask task = new ClassTask(databaseInfo);
        task.setDbName("notexist");
        task.setClassName("code");
        task.execute();

        assertFalse(task.isSuccess());
        assertEquals(
                "Failed to connect to database server, 'notexist', on the following host(s): localhost",
                task.getErrorMsg());
    }

    public void testExistDbAndNotExistTable() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<table.clazz.003.req.txt>");

        ClassTask task = new ClassTask(databaseInfo);
        task.setDbName("demodb");
        task.setClassName("notexisttbl");
        task.execute();

        assertFalse(task.isSuccess());
        assertEquals("Unknown class \"notexisttbl\".", task.getErrorMsg());
    }

    public void testExistDbAndNullTable() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<table.clazz.004.req.txt>");

        ClassTask task = new ClassTask(databaseInfo);
        task.setDbName("demodb");
        task.setClassName(null);
        task.execute();

        assertFalse(task.isSuccess());
        assertEquals("Function called with missing or invalid arguments.", task.getErrorMsg());
    }

    public void testNullDbAndNullTable() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<table.clazz.005.req.txt>");

        ClassTask task = new ClassTask(databaseInfo);
        task.setDbName(null);
        task.setClassName(null);
        task.execute();

        assertFalse(task.isSuccess());
        assertEquals("Function called with missing or invalid arguments.", task.getErrorMsg());
    }
}
