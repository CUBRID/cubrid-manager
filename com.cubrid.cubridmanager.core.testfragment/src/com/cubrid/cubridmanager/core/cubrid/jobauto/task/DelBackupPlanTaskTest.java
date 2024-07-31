package com.cubrid.cubridmanager.core.cubrid.jobauto.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;

public class DelBackupPlanTaskTest extends SetupEnvTestCase {

    /*
     * Test delete backupplan
     *  @throws Exception
     */
    public void testDelBackupPlanSend() throws Exception {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;
        String id = "ddd";
        if (addBackupPlan(id)) {
            DelBackupPlanTask task = new DelBackupPlanTask(serverInfo);
            task.setDbname(testDbName);
            task.setBackupid(id);
            task.execute();
            // compare
            assertTrue(task.isSuccess());
        }
    }

    public void testDelBackupPlanNotExistBackupId() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<jobauto.delbackupplan.001.req.txt>");

        DelBackupPlanTask task = new DelBackupPlanTask(serverInfo);
        task.setDbname("demodb");
        task.setBackupid("notexistbackupid");
        task.execute();

        assertTrue(task.isSuccess());
        assertNull(task.getErrorMsg());
    }

    public void testDelBackupPlanNotExistDb() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<jobauto.delbackupplan.002.req.txt>");

        DelBackupPlanTask task = new DelBackupPlanTask(serverInfo);
        task.setDbname("notexistdb");
        task.setBackupid("bak_daily");
        task.execute();

        assertTrue(task.isSuccess());
        assertNull(task.getErrorMsg());
    }

    public void testDelBackupPlanNullDb() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<jobauto.delbackupplan.003.req.txt>");

        DelBackupPlanTask task = new DelBackupPlanTask(serverInfo);
        task.setDbname(null);
        task.setBackupid("bak_daily");
        task.execute();

        assertFalse(task.isSuccess());
        assertNotNull(task.getErrorMsg());
        assertEquals("Parameter(database name) missing in the request", task.getErrorMsg());
    }

    public void testDelBackupPlan() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<jobauto.delbackupplan.004.req.txt>");

        DelBackupPlanTask task = new DelBackupPlanTask(serverInfo);
        task.setDbname("demodb");
        task.setBackupid("bak_daily");
        task.execute();

        assertTrue(task.isSuccess());
        assertNull(task.getErrorMsg());
    }
}
