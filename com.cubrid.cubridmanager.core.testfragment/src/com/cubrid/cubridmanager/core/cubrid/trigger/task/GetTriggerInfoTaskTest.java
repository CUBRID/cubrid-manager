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
package com.cubrid.cubridmanager.core.cubrid.trigger.task;

import java.util.List;

import com.cubrid.common.core.common.model.Trigger;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import com.cubrid.cubridmanager.core.utils.ModelUtil;

public class GetTriggerInfoTaskTest extends
		SetupEnvTestCase {
	String dbname = "demodb";

	public void testSend() throws Exception {
		String filepath = this.getFilePathInPlugin("/com/cubrid/cubridmanager/core/cubrid/trigger/task/test.message/gettriggerinfo_send");
		String msg = Tool.getFileContent(filepath);

		// replace "token" field with the latest value
		msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
		// composite message
		GetTriggerListTask task = new GetTriggerListTask(serverInfo);
		task.setDbName(dbname);
		// compare
		assertEquals(msg, task.getRequest());

	}

	public void testReceive() throws Exception {
		String filepath = this.getFilePathInPlugin("/com/cubrid/cubridmanager/core/cubrid/trigger/task/test.message/gettriggerinfo_receive");
		String msg = Tool.getFileContent(filepath);

		TreeNode node = MessageUtil.parseResponse(msg);

		List<Trigger> list = ModelUtil.getTriggerList(node.getChildren().get(0));
		String action = "update resort set number_of_pools=new.number_of_pools-1 where \"name\"=obj.\"name\"";
		assertEquals(2, list.size());
		assertEquals("limit_pools", list.get(0).getName());
		assertEquals("BEFORE", list.get(0).getConditionTime());
		assertEquals("UPDATE", list.get(0).getEventType());
		assertEquals(action, list.get(0).getAction());
		assertEquals("resort", list.get(0).getTargetClass());
		assertEquals("number_of_pools", list.get(0).getTargetAttribute());
		assertEquals("new.number_of_pools>0", list.get(0).getCondition());
		assertEquals("BEFORE", list.get(0).getActionTime());
		assertEquals("ACTIVE", list.get(0).getStatus());
		assertEquals("00.01", list.get(0).getPriority());
	}
}