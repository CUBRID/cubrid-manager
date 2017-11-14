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

import java.util.List;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.common.model.OnOffType;
import com.cubrid.cubridmanager.core.cubrid.table.model.ClassItem;
import com.cubrid.cubridmanager.core.cubrid.table.model.ClassList;
import com.cubrid.cubridmanager.core.cubrid.table.model.DBClasses;

public class GetClassListTaskTest extends
		SetupEnvTestCase {

	public void testGetClassListTaskTest() {

		if (StringUtil.isEqual(
				SystemParameter.getParameterValue("useMockTest"), "y"))
			return;

		GetClassListTask task = new GetClassListTask(serverInfo);
		task.setDbName(testDbName);
		task.setDbStatus(OnOffType.ON);
		task.execute();
		assertEquals(true, task.isSuccess());
		DBClasses db = task.getDbClassInfo();
		assertEquals(testDbName, db.getDbName());

	}

	public void testClassInfo() {

		if (StringUtil.isEqual(
				SystemParameter.getParameterValue("useMockTest"), "n"))
			return;

		System.out.println("<table.classinfo.001.req.txt>");

		GetClassListTask task = new GetClassListTask(serverInfo);
		task.setDbName("demodb");
		task.setDbStatus(OnOffType.OFF);
		task.execute();

		assertTrue(task.isSuccess());

		DBClasses classes = task.getDbClassInfo();

		ClassList classList = classes.getSystemClassList();
		List<ClassItem> list = classList.getClassList();
		assertEquals("db_root", list.get(0).getClassname());
	}

	public void testClassInfoNullDb() {

		if (StringUtil.isEqual(
				SystemParameter.getParameterValue("useMockTest"), "n"))
			return;

		System.out.println("<table.classinfo.002.req.txt>");

		GetClassListTask task = new GetClassListTask(serverInfo);
		task.setDbName(null);
		task.setDbStatus(OnOffType.OFF);
		task.execute();

		assertFalse(task.isSuccess());

	}

	public void testClassInfoNotExistDb() {

		if (StringUtil.isEqual(
				SystemParameter.getParameterValue("useMockTest"), "n"))
			return;

		System.out.println("<table.classinfo.003.req.txt>");

		GetClassListTask task = new GetClassListTask(serverInfo);
		task.setDbName("notexistdb");
		task.setDbStatus(OnOffType.OFF);
		task.execute();

		assertFalse(task.isSuccess());

	}

}