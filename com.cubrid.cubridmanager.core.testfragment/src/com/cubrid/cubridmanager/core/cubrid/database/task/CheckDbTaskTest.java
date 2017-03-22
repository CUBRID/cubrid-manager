package com.cubrid.cubridmanager.core.cubrid.database.task;

import java.util.ArrayList;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.common.task.CommonTaskName;
import com.cubrid.cubridmanager.core.common.task.CommonUpdateTask;

public class CheckDbTaskTest extends
		SetupEnvTestCase {

	public void testExistDb() {

		if (StringUtil.isEqual(
				SystemParameter.getParameterValue("useMockTest"), "n"))
			return;

		System.out.println("<database.checkdb.001.req.txt>");

		CommonUpdateTask task = new CommonUpdateTask(
				CommonTaskName.CHECK_DATABASE_TASK_NAME,
				ServerManager.getInstance().getServer(host, monport, userName),
				CommonSendMsg.getCommonDatabaseSendMsg());
		task.setDbName("demodb");
		task.execute();

		assertTrue(task.isSuccess());
		assertNull(task.getErrorMsg());

	}

	@SuppressWarnings("static-access")
	public void testNotExistDb() {

		if (StringUtil.isEqual(
				SystemParameter.getParameterValue("useMockTest"), "n"))
			return;

		System.out.println("<database.checkdb.002.req.txt>");

		CommonUpdateTask task = new CommonUpdateTask(
				CommonTaskName.CHECK_DATABASE_TASK_NAME,
				ServerManager.getInstance().getServer(host, monport, userName),
				CommonSendMsg.getCommonDatabaseSendMsg());
		task.setDbName("notexistdb");
		task.execute();
		task.fillSet(new ArrayList<String>(), new String[]{"1", "2" });

		assertFalse(task.isSuccess());
		assertEquals(
				task.getErrorMsg(),
				"Database \"notexistdb\" is unknown, or the file \"databases.txt\" cannot be accessed.");

	}

}
