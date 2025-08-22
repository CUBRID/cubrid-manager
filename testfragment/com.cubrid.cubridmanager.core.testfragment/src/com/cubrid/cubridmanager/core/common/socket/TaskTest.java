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
package com.cubrid.cubridmanager.core.common.socket;

import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import java.util.ArrayList;

public class TaskTest extends SetupEnvTestCase {

    public void testSetFieldValue() throws Exception {
        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/table/task/attribute/test.message/updateattribute");
        String msg = Tool.getFileContent(filepath);

        TreeNode node = MessageUtil.parseResponse(msg, true);
        TreeNode node2 = node.getChildren().get(0).getChildren().get(0);
        DBAttribute attribute = new DBAttribute();

        SocketTask.setFieldValue(node2, attribute);
        assertEquals(node2.getValue("name"), attribute.getName());
        assertEquals(node2.getValue("type"), attribute.getType());
        assertEquals(node2.getValue("inherit"), attribute.getInherit());
        assertEquals(StringUtil.strYN2Boolean(node2.getValue("indexed")), attribute.isIndexed());
        assertEquals(StringUtil.strYN2Boolean(node2.getValue("notnull")), attribute.isNotNull());
        assertEquals(StringUtil.strYN2Boolean(node2.getValue("shared")), attribute.isShared());
        assertEquals(StringUtil.strYN2Boolean(node2.getValue("unique")), attribute.isUnique());
        assertEquals(node2.getValue("default"), attribute.getDefault());

        SocketTask socketTask = new SocketTask("viewlog", serverInfo) {};
        socketTask.getWarningMsg();
        socketTask.setWarningMsg(null);
        socketTask.getWarningMsg();
        socketTask.setClientService(new ClientSocket(host, Integer.valueOf(port), "admin"));
        socketTask.setWarningMsg("err");
        socketTask.getWarningMsg();

        socketTask.setAppendSendMsg("msg");
        socketTask.execute();
        socketTask.setTaskname(null);
        socketTask.execute();
        socketTask.setTaskname("");
        socketTask.execute();

        socketTask.setNeedServerConnected(true);
        socketTask.setServerInfo(new ServerInfo());
        socketTask.execute();

        socketTask.getResponsedMsg();
        socketTask.setClientService(null);
        socketTask.execute();
        socketTask.getResponsedMsg();

        socketTask.setServerInfo(null);
        socketTask.execute();
        socketTask.setUsingMonPort(true);
        socketTask.setServerInfo(serverInfo);
        socketTask.setUsingMonPort(false);
        socketTask.setServerInfo(serverInfo);

        socketTask.setResponse(node2);
        socketTask.getResponse();
        String[] values1 = {"aa", "bb"};
        String[] values2 = {};
        SocketTask.fillSet(new ArrayList<String>(), null);
        SocketTask.fillSet(new ArrayList<String>(), values1);
        SocketTask.fillSet(new ArrayList<String>(), values2);

        socketTask.cancel();
        socketTask.setClientService(null);
        socketTask.cancel();
        socketTask.finish();
        socketTask.setClientService(new ClientSocket(host, Integer.valueOf(port), "admin"));
        socketTask.finish();
    }
}
