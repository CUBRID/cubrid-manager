package com.cubrid.cubridmanager.core.cubrid.database.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.common.task.CommonTaskName;
import com.cubrid.cubridmanager.core.common.task.CommonUpdateTask;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;

public class StopDbTaskTest extends
		SetupEnvTestCase {

	public void testStopDbDemo() {

		if (StringUtil.isEqual(
				SystemParameter.getParameterValue("useMockTest"), "n"))
			return;

		System.out.println("<database.stopdb.001.req.txt>");

		CommonUpdateTask task = new CommonUpdateTask(
				CommonTaskName.STOP_DB_TASK_NAME,
				CMHostNodePersistManager.getInstance().getServerInfo(host, monport, userName),
				CommonSendMsg.getCommonDatabaseSendMsg());

		task.setDbName("demodb");
		task.execute();

		assertTrue(task.isSuccess());
		assertNull(task.getErrorMsg());

	}

	public void testStopDbNotExist() {

		if (StringUtil.isEqual(
				SystemParameter.getParameterValue("useMockTest"), "n"))
			return;

		System.out.println("<database.stopdb.002.req.txt>");

		CommonUpdateTask task = new CommonUpdateTask(
				CommonTaskName.STOP_DB_TASK_NAME,
				CMHostNodePersistManager.getInstance().getServerInfo(host, monport, userName),
				CommonSendMsg.getCommonDatabaseSendMsg());

		task.setDbName("notexistdb");
		task.execute();

		assertTrue(task.isSuccess());
		assertNull(task.getErrorMsg());

	}

	public void testStopDbNotActive() {

		if (StringUtil.isEqual(
				SystemParameter.getParameterValue("useMockTest"), "n"))
			return;

		System.out.println("<database.stopdb.003.req.txt>");

		CommonUpdateTask task = new CommonUpdateTask(
				CommonTaskName.STOP_DB_TASK_NAME,
				CMHostNodePersistManager.getInstance().getServerInfo(host, monport, userName),
				CommonSendMsg.getCommonDatabaseSendMsg());

		task.setDbName("notactivedb");
		task.execute();

		assertTrue(task.isSuccess());
		assertNull(task.getErrorMsg());

	}

	public void testStopDbEmpty() {

		if (StringUtil.isEqual(
				SystemParameter.getParameterValue("useMockTest"), "n"))
			return;

		System.out.println("<database.stopdb.004.req.txt>");

		CommonUpdateTask task = new CommonUpdateTask(
				CommonTaskName.STOP_DB_TASK_NAME,
				CMHostNodePersistManager.getInstance().getServerInfo(host, monport, userName),
				CommonSendMsg.getCommonDatabaseSendMsg());

		task.setDbName(null);
		task.execute();

		assertFalse(task.isSuccess());
		assertNotNull(task.getErrorMsg());

	}

}
