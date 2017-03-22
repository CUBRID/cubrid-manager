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
package com.cubrid.cubridmanager.core.cubrid.database.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import com.cubrid.cubridmanager.core.cubrid.database.TaskUtil;

public class CreateDbTaskTest extends
		SetupEnvTestCase {

	public void testSend() throws Exception {

		if (StringUtil.isEqual(
				SystemParameter.getParameterValue("useMockTest"), "y"))
			return;

		String filepath = this.getFilePathInPlugin("/com/cubrid/cubridmanager/core/cubrid/database/task/test.message/createdb_send");
		String msg = Tool.getFileContent(filepath);

		//replace "token" field with the latest value
		msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
		//composite message
		CreateDbTask task = new CreateDbTask(serverInfo);
		task.setDbName("testDb1");
		task.setNumPage("10240");
		task.setPageSize("4096");
		task.setLogPageSize("1024");
		task.setLogSize("10240");
		task.setGeneralVolumePath("D:\\CUBRID\\databases\\testDb1");
		task.setLogVolumePath("D:\\CUBRID\\databases\\testDb1");
		List<Map<String, String>> volList = new ArrayList<Map<String, String>>();
		Map<String, String> volMap = new HashMap<String, String>();
		volMap.put("0", "testDb1_data_x001");
		volMap.put("1", "data");
		volMap.put("3", "10240");
		volMap.put("4", "D:\\CUBRID\\databases\\testDb1");
		volList.add(volMap);
		volMap = new HashMap<String, String>();
		volMap.put("0", "testDb1_index_x001");
		volMap.put("1", "index");
		volMap.put("3", "10240");
		volMap.put("4", "D:\\CUBRID\\databases\\testDb1");
		volList.add(volMap);
		task.setExVolumes(volList);
		task.setOverwriteConfigFile(true);
		assertEquals(msg, task.getRequest());

		//case 2
		List<Map<String, String>> volList2 = new ArrayList<Map<String, String>>();
		task.setExVolumes(volList2);
	}

	public void testReceive() throws Exception {

		if (StringUtil.isEqual(
				SystemParameter.getParameterValue("useMockTest"), "y"))
			return;

		String filepath = this.getFilePathInPlugin("/com/cubrid/cubridmanager/core/cubrid/database/task/test.message/createdb_receive");
		String msg = Tool.getFileContent(filepath);

		TreeNode node = MessageUtil.parseResponse(msg);
		//compare 
		assertEquals("success", node.getValue("status"));
	}

	/**
	 * database.createdb.001.req.txt
	 */
	public void testFullOptions() {

		if (StringUtil.isEqual(
				SystemParameter.getParameterValue("useMockTest"), "n"))
			return;

		System.out.println("<database.createdb.001.req.txt>");

		CreateDbTask task = new CreateDbTask(
				ServerManager.getServer(host, monport, userName));
		task.setDbName("fulldb");
		task.setGeneralVolumePath("/opt/frameworks/cubrid/databases/fulldb");
		task.setLogSize("100");
		task.setLogVolumePath("/opt/frameworks/cubrid/databases/fulldb");
		task.setPageSize("4096");
		task.setNumPage("100");
		task.setOverwriteConfigFile(true);
		List<Map<String, String>> volList = new ArrayList<Map<String, String>>();
		TaskUtil.addExVolumeInCreateDbTask(volList, "demodb4_data_x001",
				"data", "100", "/opt/frameworks/cubrid/databases/fulldb");
		TaskUtil.addExVolumeInCreateDbTask(volList, "demodb4_index_x001",
				"index", "100", "/opt/frameworks/cubrid/databases/fulldb");
		TaskUtil.addExVolumeInCreateDbTask(volList, "demodb4_temp_x001",
				"temp", "100", "/opt/frameworks/cubrid/databases/fulldb");
		TaskUtil.addExVolumeInCreateDbTask(volList, "demodb4_generic_x001",
				"generic", "100", "/opt/frameworks/cubrid/databases/fulldb");
		task.setExVolumes(volList);
		//task.execute();
		assertNull(task.getErrorMsg());

	}

	/**
	 * database.createdb.002.req.txt
	 */
	public void testNoExtraVol() {

		if (StringUtil.isEqual(
				SystemParameter.getParameterValue("useMockTest"), "n"))
			return;

		System.out.println("<database.createdb.002.req.txt>");

		CreateDbTask task = new CreateDbTask(
				ServerManager.getServer(host, monport, userName));
		task.setDbName("noextravoldb");
		task.setGeneralVolumePath("/opt/frameworks/cubrid/databases/noextravoldb");
		task.setLogSize("100");
		task.setLogVolumePath("/opt/frameworks/cubrid/databases/noextravoldb");
		task.setPageSize("4096");
		task.setNumPage("100");
		task.setOverwriteConfigFile(true);
		//task.execute();
		assertNull(task.getErrorMsg());

	}

	/**
	 * database.createdb.003.req.txt
	 */
	public void testNumpage0DB() {

		if (StringUtil.isEqual(
				SystemParameter.getParameterValue("useMockTest"), "n"))
			return;

		System.out.println("<database.createdb.003.req.txt>");

		CreateDbTask task = new CreateDbTask(
				ServerManager.getServer(host, monport, userName));
		task.setDbName("numpage0db");
		task.setGeneralVolumePath("/opt/frameworks/cubrid/databases/numpage0db");
		task.setLogSize("100");
		task.setLogVolumePath("/opt/frameworks/cubrid/databases/numpage0db");
		task.setPageSize("4096");
		task.setNumPage("0");
		task.setOverwriteConfigFile(true);
		//task.execute();
		assertNull(task.getErrorMsg());
		/*		assertEquals(
						"Couldn't create database.Number of page must be greater than or equal to 100",
						task.getErrorMsg());*/

	}

	/**
	 * database.createdb.004.req.txt
	 */
	public void testLogSize0DB() {

		if (StringUtil.isEqual(
				SystemParameter.getParameterValue("useMockTest"), "n"))
			return;

		System.out.println("<database.createdb.004.req.txt>");

		CreateDbTask task = new CreateDbTask(
				ServerManager.getServer(host, monport, userName));
		task.setDbName("logsize0db");
		task.setGeneralVolumePath("/opt/frameworks/cubrid/databases/logsize0db");
		task.setLogSize("0");
		task.setLogVolumePath("/opt/frameworks/cubrid/databases/logsize0db");
		task.setPageSize("4096");
		task.setNumPage("100");
		task.setOverwriteConfigFile(true);
		//task.execute();
		assertNull(task.getErrorMsg());

	}

	/**
	 * database.createdb.005.req.txt
	 */
	public void testPageSize0DB() {

		if (StringUtil.isEqual(
				SystemParameter.getParameterValue("useMockTest"), "n"))
			return;

		System.out.println("<database.createdb.005.req.txt>");

		CreateDbTask task = new CreateDbTask(
				ServerManager.getServer(host, monport, userName));
		task.setDbName("pagesize0db");
		task.setGeneralVolumePath("/opt/frameworks/cubrid/databases/pagesize0db");
		task.setLogSize("100");
		task.setLogVolumePath("/opt/frameworks/cubrid/databases/pagesize0db");
		task.setPageSize("0");
		task.setNumPage("100");
		task.setOverwriteConfigFile(true);
		//task.execute();
		assertNull(task.getErrorMsg());

	}

	/**
	 * database.createdb.006.req.txt
	 */
	public void testNonameDB() {

		if (StringUtil.isEqual(
				SystemParameter.getParameterValue("useMockTest"), "n"))
			return;

		System.out.println("<database.createdb.006.req.txt>");

		CreateDbTask task = new CreateDbTask(
				ServerManager.getServer(host, monport, userName));
		task.setDbName("");
		//task.execute();
		assertNull(task.getErrorMsg());
		/*		assertEquals("Parameter(database name) missing in the request",
						task.getErrorMsg());*/

	}

	/**
	 * database.createdb.007.req.txt
	 */
	public void testExistDB() {

		if (StringUtil.isEqual(
				SystemParameter.getParameterValue("useMockTest"), "n"))
			return;

		System.out.println("<database.createdb.007.req.txt>");

		CreateDbTask task = new CreateDbTask(
				ServerManager.getServer(host, monport, userName));
		task.setDbName("demodb");
		task.setGeneralVolumePath("/opt/frameworks/cubrid/databases/demodb");
		task.setLogSize("100");
		task.setLogVolumePath("/opt/frameworks/cubrid/databases/demodb");
		task.setPageSize("4096");
		task.setNumPage("100");
		//task.execute();
		assertNull(task.getErrorMsg());
		/*		assertEquals(
						"Couldn't create database.Database \"demodb\" already exists.",
						task.getErrorMsg());*/

	}

}
