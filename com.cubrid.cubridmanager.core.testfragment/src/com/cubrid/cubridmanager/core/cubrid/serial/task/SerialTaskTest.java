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
package com.cubrid.cubridmanager.core.cubrid.serial.task;

import com.cubrid.common.core.common.model.SerialInfo;
import com.cubrid.cubridmanager.core.SetupJDBCTestCase;

public class SerialTaskTest extends
		SetupJDBCTestCase {

	public void testSerialTask() {
		//test create serial
		CreateOrEditSerialTask createOrEditSerialTask = new CreateOrEditSerialTask(
				databaseInfo);
		createOrEditSerialTask.createSerial("serial1", "1", "1", "100", "1",
				true, false, false, "10", false, null);
		assertTrue(createOrEditSerialTask.getErrorMsg() == null
				|| createOrEditSerialTask.getErrorMsg().trim().length() <= 0);

		GetSerialInfoTask getSerialInfoTask = new GetSerialInfoTask(
				databaseInfo);
		SerialInfo serialInfo = getSerialInfoTask.getSerialInfo("serial1");
		boolean isOk = serialInfo != null
				&& serialInfo.getName().equals("serial1")
				&& serialInfo.getCurrentValue().equals("1")
				&& serialInfo.getIncrementValue().equals("1")
				&& serialInfo.getMaxValue().equals("100")
				&& serialInfo.getMinValue().equals("1")
				&& serialInfo.isCyclic();
		assertTrue(isOk);

		CreateOrEditSerialTask createOrEditSerialTask2 = new CreateOrEditSerialTask(
				databaseInfo);
		createOrEditSerialTask2.createSerial("serial2", null, null, "100", "1",
				true, false, false, "100", true, null);
		CreateOrEditSerialTask createOrEditSerialTask3 = new CreateOrEditSerialTask(
				databaseInfo);
		createOrEditSerialTask3.createSerial("serial3", null, null, "100", "1",
				true, true, true, "100", true, null);
		CreateOrEditSerialTask createOrEditSerialTask4 = new CreateOrEditSerialTask(
				databaseInfo);
		createOrEditSerialTask4.createSerial("serial4", "1", "1", "100", "1",
				true, true, true, "100", true, null);
		CreateOrEditSerialTask createOrEditSerialTask5 = new CreateOrEditSerialTask(
				databaseInfo);
		createOrEditSerialTask5.createSerial("serial5", null, null, null, null,
				false, false, false, "100", true, null);
		createOrEditSerialTask5.createSerial("serial6", null, null, null, null,
				false, false, false, "100", true, null);
		createOrEditSerialTask5.createSerial("serial6", null, null, null, null,
				false, false, false, "100", true, null);

		//test edit serial
		createOrEditSerialTask = new CreateOrEditSerialTask(databaseInfo);
		createOrEditSerialTask.editSerial("serial1", "2", "2", "102", "2",
				false, false, false, "10", false, null);
		assertTrue(createOrEditSerialTask.getErrorMsg() == null
				|| createOrEditSerialTask.getErrorMsg().trim().length() <= 0);
		getSerialInfoTask = new GetSerialInfoTask(databaseInfo);
		serialInfo = getSerialInfoTask.getSerialInfo("serial1");
		isOk = serialInfo != null && serialInfo.getName().equals("serial1")
				&& serialInfo.getCurrentValue().equals("2")
				&& serialInfo.getIncrementValue().equals("2")
				&& serialInfo.getMaxValue().equals("102")
				&& serialInfo.getMinValue().equals("2")
				&& !serialInfo.isCyclic();
		assertTrue(isOk);
		createOrEditSerialTask2 = new CreateOrEditSerialTask(databaseInfo);
		createOrEditSerialTask2.editSerial("serial2", null, null, "100", "1",
				true, false, false, "100", true, null);
		createOrEditSerialTask3 = new CreateOrEditSerialTask(databaseInfo);
		createOrEditSerialTask3.editSerial("serial3", null, null, "100", "1",
				true, true, true, "100", true, null);
		createOrEditSerialTask4 = new CreateOrEditSerialTask(databaseInfo);
		createOrEditSerialTask4.editSerial("serial4", "1", "1", "100", "1",
				true, true, true, "100", true, null);
		createOrEditSerialTask5 = new CreateOrEditSerialTask(databaseInfo);
		createOrEditSerialTask5.editSerial("serial5", null, null, null, null,
				false, false, false, "100", true, null);
		createOrEditSerialTask5.editSerial("serial6", null, null, null, null,
				false, false, false, "100", true, null);
		createOrEditSerialTask5.editSerial("serial6", null, null, null, null,
				false, false, false, "100", true, null);

		//test get serial information list
		GetSerialInfoListTask getSerialInfoListTask = new GetSerialInfoListTask(
				databaseInfo);
		getSerialInfoListTask.execute();
		assertTrue(getSerialInfoListTask.getSerialInfoList().size() > 0);
		getSerialInfoListTask.setErrorMsg("err");
		getSerialInfoListTask.execute();
		getSerialInfoListTask.setErrorMsg(null);
		getSerialInfoListTask.execute();

		//test get serial information
		getSerialInfoTask = new GetSerialInfoTask(databaseInfo);
		serialInfo = getSerialInfoTask.getSerialInfo("serial1");
		assertTrue(serialInfo != null && serialInfo.getName().equals("serial1"));
		getSerialInfoTask.setErrorMsg("err");
		getSerialInfoTask.getSerialInfo("serial1");
		getSerialInfoTask.setErrorMsg(null);
		getSerialInfoTask.getSerialInfo("serial1");

		//test delete serial
		DeleteSerialTask deleteSerialTask = new DeleteSerialTask(databaseInfo);
		deleteSerialTask.deleteSerial(new String[]{"serial1", "serial2",
				"serial3", "serial4", "serial5" });
		assertTrue(createOrEditSerialTask.getErrorMsg() == null
				|| createOrEditSerialTask.getErrorMsg().trim().length() <= 0);
		getSerialInfoTask = new GetSerialInfoTask(databaseInfo);
		serialInfo = getSerialInfoTask.getSerialInfo("serial1");
		assertTrue(serialInfo == null);
		deleteSerialTask.setErrorMsg("err");
		deleteSerialTask.deleteSerial(new String[]{"serial1", "serial2",
				"serial3", "serial4", "serial5" });
		deleteSerialTask.setErrorMsg(null);
		deleteSerialTask.deleteSerial(new String[]{"serial1", "serial2",
				"serial3", "serial4", "serial5" });

	}
}
