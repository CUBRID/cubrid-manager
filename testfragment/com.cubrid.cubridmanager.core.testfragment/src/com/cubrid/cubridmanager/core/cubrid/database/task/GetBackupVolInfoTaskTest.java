package com.cubrid.cubridmanager.core.cubrid.database.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;

public class GetBackupVolInfoTaskTest extends SetupEnvTestCase {

    public void testSend() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/database/task/test.message/getbackupvolinfo_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // composite message
        GetBackupVolInfoTask task = new GetBackupVolInfoTask(serverInfo);
        task.setDbName("demodb");
        task.setLevel("0");
        task.setPath("C:\\CUBRID\\databases\\demodb\\backup\\demodb_backup_lv0");
        // compare
        assertEquals(msg, task.getRequest());
    }

    public void testReceive() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;
        // case 1
        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/database/task/test.message/getbackupvolinfo_receive");
        String msg = Tool.getFileContent(filepath);
        TreeNode node = MessageUtil.parseResponse(msg);
        GetBackupVolInfoTask task = new GetBackupVolInfoTask(serverInfo);
        task.setResponse(node);
        assertTrue(task.getDbBackupVolInfo().length() > 0);
        // exception case1
        task.setResponse(null);
        assertTrue(task.getDbBackupVolInfo() == null);
        // exception case2
        task.setResponse(node);
        task.setErrorMsg("has error");
        assertTrue(task.getDbBackupVolInfo() == null);
    }

    public void testExistBackup() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.backupvolinfo.001.req.txt>");

        GetBackupVolInfoTask task = new GetBackupVolInfoTask(serverInfo);
        task.setDbName("demodb");
        task.setLevel("0");
        task.setPath("/opt/frameworks/cubrid/databases/demodb/backup/demodb_backup_lv0");

        task.execute();

        assertTrue(task.isSuccess());
        assertEquals(1124, task.getDbBackupVolInfo().length());
    }

    public void testActiveDb() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.backupvolinfo.002.req.txt>");

        GetBackupVolInfoTask task = new GetBackupVolInfoTask(serverInfo);
        task.setDbName("activedb");
        task.setLevel("0");
        task.setPath("/opt/frameworks/cubrid/databases/activedb/backup/activedb_backup_lv0");

        task.execute();

        assertFalse(task.isSuccess());
        assertEquals("Database(activedb) is active state.", task.getErrorMsg());
    }

    public void testNotExistBackup() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.backupvolinfo.003.req.txt>");

        GetBackupVolInfoTask task = new GetBackupVolInfoTask(serverInfo);
        task.setDbName("demodb");
        task.setLevel("3");
        task.setPath("/opt/frameworks/cubrid/databases/demodb/backup/demodb_backup_lv3");

        task.execute();

        assertTrue(task.isSuccess());
        assertEquals(737, task.getDbBackupVolInfo().length());
    }

    public void testNotExistDb() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.backupvolinfo.004.req.txt>");

        GetBackupVolInfoTask task = new GetBackupVolInfoTask(serverInfo);
        task.setDbName("notexistdb");
        task.setLevel("1");
        task.setPath("/opt/frameworks/cubrid/databases/notexistdb/backup/notexistdb_backup_lv0");

        task.execute();

        assertTrue(task.isSuccess());
    }
}
