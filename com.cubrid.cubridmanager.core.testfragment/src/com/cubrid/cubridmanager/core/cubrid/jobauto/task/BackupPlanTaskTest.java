package com.cubrid.cubridmanager.core.cubrid.jobauto.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.common.model.OnOffType;
import com.cubrid.cubridmanager.core.utils.ModelUtil.YesNoType;

/**
 * Test add and edit backupinfo
 *
 * @author lizhiqiang Apr 3, 2009
 */
public class BackupPlanTaskTest extends SetupEnvTestCase {

    /*
     * Test add backupplan
     *  @throws Exception
     */
    public void testAddBackupPlanSend() throws Exception {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;
        String id = "ccc";
        BackupPlanTask task = new BackupPlanTask(BackupPlanTask.ADD_BACKUP_INFO, serverInfo);
        task.setDbname(testDbName);
        task.setBackupid(id);
        task.setPath(databaseInfo.getDbDir() + serverInfo.getPathSeparator() + "backup");
        task.setPeriodType("Weekly");
        task.setPeriodDate("Tuesday");
        task.setTime("0906");
        task.setLevel("2");
        task.setArchivedel(OnOffType.ON);
        task.setUpdatestatus(OnOffType.ON);
        task.setStoreold(OnOffType.ON);
        task.setOnoff(OnOffType.ON);
        task.setZip(YesNoType.Y);
        task.setCheck(YesNoType.Y);
        task.setMt("5");
        task.setBknum("1");
        task.execute();
        task.setUsingSpecialDelimiter(false);
        // compare
        assertTrue(task.isSuccess());
        if (task.isSuccess()) deleteBackupPlan(id);
    }

    /*
     *Tests edit backupplan
     *
     * @throws Exception
     */
    public void testEditBackupPlanSend() throws Exception {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;
        String id = "iii";
        if (this.addBackupPlan(id)) {
            BackupPlanTask task = new BackupPlanTask(BackupPlanTask.SET_BACKUP_INFO, serverInfo);
            task.setDbname(testDbName);
            task.setBackupid(id);
            task.setPath(databaseInfo.getDbDir() + serverInfo.getPathSeparator() + "backup");
            task.setPeriodType("Weekly");
            task.setPeriodDate("Tuesday");
            task.setTime("0706");
            task.setLevel("2");
            task.setArchivedel(OnOffType.ON);
            task.setUpdatestatus(OnOffType.ON);
            task.setStoreold(OnOffType.ON);
            task.setOnoff(OnOffType.ON);
            task.setZip(YesNoType.Y);
            task.setCheck(YesNoType.Y);
            task.setMt("5");
            task.setBknum("1");
            task.execute();
            assertTrue(task.isSuccess());
            if (task.isSuccess()) {
                this.deleteBackupPlan(id);
            }
        }
    }

    public void testAddBackupPlan() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<jobauto.addbackupplanlist.001.req.txt>");

        BackupPlanTask task = new BackupPlanTask("addbackupinfo", serverInfo);
        task.setDbname("demodb");
        task.setBackupid("bak_monthly");
        task.setPath("/opt/frameworks/cubrid/databases/demodb/backup");
        task.setPeriodType("Monthly");
        task.setPeriodDate("1");
        task.setTime("1230");
        task.setLevel("0");
        task.setArchivedel(OnOffType.ON);
        task.setUpdatestatus(OnOffType.ON);
        task.setStoreold(OnOffType.ON);
        task.setOnoff(OnOffType.ON);
        task.setZip(YesNoType.Y);
        task.setCheck(YesNoType.Y);
        task.setMt("0");
        task.setBknum("1");
        task.execute();

        assertTrue(task.isSuccess());
        assertNull(task.getErrorMsg());
    }

    public void testAddBackupPlanNullDb() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;
        System.out.println("<jobauto.addbackupplanlist.002.req.txt>");

        BackupPlanTask task = new BackupPlanTask("addbackupinfo", serverInfo);
        task.setDbname(null);
        task.setBackupid("bak_monthly");
        task.setPath("/opt/frameworks/cubrid/databases/demodb/backup");
        task.setPeriodType("Monthly");
        task.setPeriodDate("1");
        task.setTime("1230");
        task.setLevel("0");
        task.setArchivedel(OnOffType.ON);
        task.setUpdatestatus(OnOffType.ON);
        task.setStoreold(OnOffType.ON);
        task.setOnoff(OnOffType.ON);
        task.setZip(YesNoType.Y);
        task.setCheck(YesNoType.Y);
        task.setMt("0");
        task.setBknum("1");
        task.execute();

        assertFalse(task.isSuccess());
        assertNotNull(task.getErrorMsg());
        assertEquals("Parameter(database name) missing in the request", task.getErrorMsg());
    }

    public void testAddBackupPlanNotExistDb() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<jobauto.addbackupplanlist.003.req.txt>");

        BackupPlanTask task = new BackupPlanTask("addbackupinfo", serverInfo);
        task.setDbname("notexistdb");
        task.setBackupid("bak_monthly");
        task.setPath("/opt/frameworks/cubrid/databases/demodb/backup");
        task.setPeriodType("Monthly");
        task.setPeriodDate("1");
        task.setTime("1230");
        task.setLevel("0");
        task.setArchivedel(OnOffType.ON);
        task.setUpdatestatus(OnOffType.ON);
        task.setStoreold(OnOffType.ON);
        task.setOnoff(OnOffType.ON);
        task.setZip(YesNoType.Y);
        task.setCheck(YesNoType.Y);
        task.setMt("0");
        task.setBknum("1");
        task.execute();

        assertTrue(task.isSuccess());
        assertNull(task.getErrorMsg());
    }

    public void testSetBackupPlan() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<jobauto.setbackupplanlist.001.req.txt>");

        BackupPlanTask task = new BackupPlanTask("setbackupinfo", serverInfo);
        task.setDbname("demodb");
        task.setBackupid("bak_daily");
        task.setPath("/opt/frameworks/cubrid/databases/demodb/backup");
        task.setPeriodType("Daily");
        task.setPeriodDate("nothing");
        task.setTime("1230");
        task.setLevel("1");
        task.setArchivedel(OnOffType.ON);
        task.setUpdatestatus(OnOffType.OFF);
        task.setStoreold(OnOffType.ON);
        task.setOnoff(OnOffType.ON);
        task.setZip(YesNoType.Y);
        task.setCheck(YesNoType.Y);
        task.setMt("0");
        task.setBknum("1");
        task.execute();

        assertTrue(task.isSuccess());
        assertNull(task.getErrorMsg());
    }

    public void testSetBackupPlanNotExistPlan() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<jobauto.setbackupplanlist.002.req.txt>");

        BackupPlanTask task = new BackupPlanTask("setbackupinfo", serverInfo);
        task.setDbname("demodb");
        task.setBackupid("notexistplan");
        task.setPath("/opt/frameworks/cubrid/databases/demodb/backup");
        task.setPeriodType("Daily");
        task.setPeriodDate("nothing");
        task.setTime("1230");
        task.setLevel("1");
        task.setArchivedel(OnOffType.ON);
        task.setUpdatestatus(OnOffType.OFF);
        task.setStoreold(OnOffType.ON);
        task.setOnoff(OnOffType.ON);
        task.setZip(YesNoType.Y);
        task.setCheck(YesNoType.Y);
        task.setMt("0");
        task.setBknum("1");
        task.execute();

        assertTrue(task.isSuccess());
        assertNull(task.getErrorMsg());
    }

    public void testSetBackupPlanNullDb() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<jobauto.setbackupplanlist.003.req.txt>");

        BackupPlanTask task = new BackupPlanTask("setbackupinfo", serverInfo);
        task.setDbname(null);
        task.setBackupid("bak_nulldb");
        task.setPath("/opt/frameworks/cubrid/databases/demodb/backup");
        task.setPeriodType("Daily");
        task.setPeriodDate("nothing");
        task.setTime("1230");
        task.setLevel("1");
        task.setArchivedel(OnOffType.ON);
        task.setUpdatestatus(OnOffType.OFF);
        task.setStoreold(OnOffType.ON);
        task.setOnoff(OnOffType.ON);
        task.setZip(YesNoType.Y);
        task.setCheck(YesNoType.Y);
        task.setMt("0");
        task.setBknum("1");
        task.execute();

        assertFalse(task.isSuccess());
        assertNotNull(task.getErrorMsg());
        assertEquals("Parameter(?) missing in the request", task.getErrorMsg());
    }
}
