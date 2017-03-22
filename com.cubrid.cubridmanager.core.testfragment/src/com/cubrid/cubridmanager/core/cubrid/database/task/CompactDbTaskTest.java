package com.cubrid.cubridmanager.core.cubrid.database.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.common.task.CommonTaskName;
import com.cubrid.cubridmanager.core.common.task.CommonUpdateTask;
import com.cubrid.cubridmanager.core.utils.ModelUtil.YesNoType;

public class CompactDbTaskTest extends
		SetupEnvTestCase {

	public void testActiveDb() {

		if (StringUtil.isEqual(
				SystemParameter.getParameterValue("useMockTest"), "n"))
			return;

		System.out.println("<database.compactdb.001.req.txt>");

		CommonUpdateTask task = new CommonUpdateTask(
				CommonTaskName.COMPACT_DATABASE_TASK_NANE,
				ServerManager.getInstance().getServer(host, monport, userName),
				CommonSendMsg.getCompactDbSendMsg());
		task.setDbName("activedb");
		task.setVerbose(YesNoType.Y);
		task.execute();

		assertFalse(task.isSuccess());
		assertNotNull(task.getErrorMsg());

	}

	public void testInActiveDb() {

		if (StringUtil.isEqual(
				SystemParameter.getParameterValue("useMockTest"), "n"))
			return;

		System.out.println("<database.compactdb.002.req.txt>");

		CommonUpdateTask task = new CommonUpdateTask(
				CommonTaskName.COMPACT_DATABASE_TASK_NANE,
				ServerManager.getInstance().getServer(host, monport, userName),
				CommonSendMsg.getCompactDbSendMsg());
		task.setDbName("inactivedb");
		//task.execute();

		assertTrue(task.isSuccess());
		assertNull(task.getErrorMsg());

	}

	public void testNotExistDb() {

		if (StringUtil.isEqual(
				SystemParameter.getParameterValue("useMockTest"), "n"))
			return;

		System.out.println("<database.compactdb.003.req.txt>");

		CommonUpdateTask task = new CommonUpdateTask(
				CommonTaskName.COMPACT_DATABASE_TASK_NANE,
				ServerManager.getInstance().getServer(host, monport, userName),
				CommonSendMsg.getCompactDbSendMsg());
		task.setDbName("notexistdb");
		task.execute();

		assertFalse(task.isSuccess());
		assertNotNull(task.getErrorMsg());

	}

}
