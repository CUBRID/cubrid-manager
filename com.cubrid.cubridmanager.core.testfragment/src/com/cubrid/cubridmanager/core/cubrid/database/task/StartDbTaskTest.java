package com.cubrid.cubridmanager.core.cubrid.database.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.common.task.CommonTaskName;
import com.cubrid.cubridmanager.core.common.task.CommonUpdateTask;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;

public class StartDbTaskTest extends
		SetupEnvTestCase {

	public void testStartDbDemo() {

		if (StringUtil.isEqual(
				SystemParameter.getParameterValue("useMockTest"), "n"))
			return;

		System.out.println("<database.startdb.001.req.txt>");

		CommonUpdateTask task = new CommonUpdateTask(
				CommonTaskName.START_DB_TASK_NAME,
				CMHostNodePersistManager.getInstance().getServerInfo(host, monport, userName),
				CommonSendMsg.getCommonDatabaseSendMsg());

		task.setDbName("demodb");
		task.execute();

		assertTrue(task.isSuccess());
		assertNull(task.getErrorMsg());

	}

	public void testStartDbNotExist() {

		if (StringUtil.isEqual(
				SystemParameter.getParameterValue("useMockTest"), "n"))
			return;

		System.out.println("<database.startdb.002.req.txt>");

		CommonUpdateTask task = new CommonUpdateTask(
				CommonTaskName.START_DB_TASK_NAME,
				CMHostNodePersistManager.getInstance().getServerInfo(host, monport, userName),
				CommonSendMsg.getCommonDatabaseSendMsg());

		task.setDbName("notexistdb");
		task.execute();

		assertFalse(task.isSuccess());
		assertNotNull(task.getErrorMsg());

	}

	public void testStartDbEmpty() {

		if (StringUtil.isEqual(
				SystemParameter.getParameterValue("useMockTest"), "n"))
			return;

		System.out.println("<database.startdb.003.req.txt>");

		CommonUpdateTask task = new CommonUpdateTask(
				CommonTaskName.START_DB_TASK_NAME,
				CMHostNodePersistManager.getInstance().getServerInfo(host, monport, userName),
				CommonSendMsg.getCommonDatabaseSendMsg());

		task.setDbName(null);
		task.execute();

		assertFalse(task.isSuccess());
		assertNotNull(task.getErrorMsg());

	}

}
