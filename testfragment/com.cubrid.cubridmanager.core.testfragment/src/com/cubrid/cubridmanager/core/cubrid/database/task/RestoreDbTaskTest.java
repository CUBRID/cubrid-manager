package com.cubrid.cubridmanager.core.cubrid.database.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;

public class RestoreDbTaskTest extends SetupEnvTestCase {

    public void testSend() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/database/task/test.message/restoredb_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // case 1
        RestoreDbTask task = new RestoreDbTask(serverInfo);
        task.setDbName("testdb2");
        task.setDate("backuptime");
        task.setLevel("0");
        task.setPartial(false);
        task.setPathName("C:\\CUBRID\\databases\\testdb\\backup\\testdb2_backup_lv0");
        task.setRecoveryPath("none");
        assertEquals(msg, task.getRequest());
        // case 2
        task.setPartial(true);
        msg = msg.replaceFirst("partial:n", "partial:y");
        assertEquals(msg, task.getRequest());
    }

    public void testReceive() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/database/task/test.message/restoredb_receive");
        String msg = Tool.getFileContent(filepath);
        TreeNode node = MessageUtil.parseResponse(msg);
        RestoreDbTask task = new RestoreDbTask(serverInfo);
        task.setResponse(node);
        assertTrue(task.isSuccess());
    }

    public void testRestoreLevel0() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.restoredb.001.req.txt>");

        RestoreDbTask task = new RestoreDbTask(serverInfo);
        task.setDbName("demodb");
        task.setDate("backuptime");
        task.setLevel("0");
        task.setPartial(false);
        task.setPathName("/opt/frameworks/cubrid/databases/demodb/backup/demodb_backup_lv0");
        task.setRecoveryPath("none");

        task.execute();

        assertNull(task.getErrorMsg());
    }

    public void testRestoreActiveDB() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.restoredb.002.req.txt>");

        RestoreDbTask task = new RestoreDbTask(serverInfo);
        task.setDbName("activedb");
        task.setDate("backuptime");
        task.setLevel("0");
        task.setPartial(false);
        task.setPathName("/opt/frameworks/cubrid/databases/activedb/backup/activedb_backup_lv0");
        task.setRecoveryPath("none");

        task.execute();

        assertNotNull(task.getErrorMsg());
    }

    public void testRestoreNotExistDB() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.restoredb.003.req.txt>");

        RestoreDbTask task = new RestoreDbTask(serverInfo);
        task.setDbName("notexistdb");
        task.setDate("backuptime");
        task.setLevel("0");
        task.setPartial(false);
        task.setPathName(
                "/opt/frameworks/cubrid/databases/notexistdb/backup/notexistdb_backup_lv0");
        task.setRecoveryPath("none");

        task.execute();

        assertNotNull(task.getErrorMsg());
    }

    public void testRestoreNotExistBackupFile() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.restoredb.004.req.txt>");

        RestoreDbTask task = new RestoreDbTask(serverInfo);
        task.setDbName("demodb");
        task.setDate("backuptime");
        task.setLevel("0");
        task.setPartial(false);
        task.setPathName("/opt/frameworks/cubrid/databases/demodb/backup/backup_lv0_notexist");
        task.setRecoveryPath("none");

        task.execute();

        assertNotNull(task.getErrorMsg());
    }
}
