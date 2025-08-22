package com.cubrid.cubridmanager.core.cubrid.database.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.common.task.CommonTaskName;
import com.cubrid.cubridmanager.core.common.task.CommonUpdateTask;
import com.cubrid.cubridmanager.core.utils.ModelUtil.YesNoType;

public class DeleteDbTaskTest extends SetupEnvTestCase {

    public void testDeleteNotExistDb() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.deletedb.001.req.txt>");

        CommonUpdateTask task =
                new CommonUpdateTask(
                        CommonTaskName.DELETE_DATABASE_TASK_NAME,
                        serverInfo,
                        CommonSendMsg.getDeletedbSendMsg());
        task.setDbName("notexistdb");
        task.setDelbackup(YesNoType.Y);

        task.execute();

        assertFalse(task.isSuccess());
        assertNotNull(task.getErrorMsg());
    }

    public void testDeleteActiveDb() {
        /*
        if (StringUtil.isEqual(
        		SystemParameter.getParameterValue("useMockTest"), "n"))
        	return;

        System.out.println("<database.deletedb.002.req.txt>");

        CommonUpdateTask task = new CommonUpdateTask(
        		CommonTaskName.DELETE_DATABASE_TASK_NAME, serverInfo,
        		CommonSendMsg.getDeletedbSendMsg());
        task.setDbName("activedb");
        task.setDelbackup(YesNoType.N);

        task.execute();

        assertFalse(task.isSuccess());
        assertNotNull(task.getErrorMsg());
        */

    }

    public void testDeleteExistDb() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.deletedb.003.req.txt>");

        CommonUpdateTask task =
                new CommonUpdateTask(
                        CommonTaskName.DELETE_DATABASE_TASK_NAME,
                        serverInfo,
                        CommonSendMsg.getDeletedbSendMsg());
        task.setDbName("existdb");
        task.setDelbackup(YesNoType.Y);

        task.execute();

        assertTrue(task.isSuccess());
        assertNull(task.getErrorMsg());
    }
}
