package com.cubrid.cubridmanager.core.cubrid.database.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;

public class BackupDbTaskTest extends SetupEnvTestCase {

    public void testSend() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/database/task/test.message/backupdb_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // composite message
        BackupDbTask task = new BackupDbTask(serverInfo);
        task.setDbName("demodb");
        task.setLevel("1");
        task.setVolumeName("testdb_backup_lv1");
        task.setBackupDir("C:\\CUBRID\\databases\\testdb\\backup");
        task.setRemoveLog(false);
        task.setCheckDatabaseConsist(true);
        task.setThreadCount("0");
        task.setZiped(true);
        task.setSafeReplication(false);
        // compare
        assertEquals(msg, task.getRequest());
    }

    public void testReceive() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/database/task/test.message/backupdb_receive");
        String msg = Tool.getFileContent(filepath);

        TreeNode node = MessageUtil.parseResponse(msg);
        // compare
        assertEquals("success", node.getValue("status"));
    }

    public void testBackupLevel0() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        BackupDbTask task = new BackupDbTask(serverInfo);
        task.setDbName("demodb");
        task.setLevel("0");
        task.setVolumeName("demodb_backup_lv0");
        task.setBackupDir("/opt/frameworks/cubrid/databases/demodb/backup");
        task.setRemoveLog(false);
        task.setCheckDatabaseConsist(true);
        task.setThreadCount("0");
        task.setZiped(true);
        task.setSafeReplication(false);

        task.execute();

        assertNull(task.getErrorMsg());
    }

    public void testBackupLevel1() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        BackupDbTask task = new BackupDbTask(serverInfo);
        task.setDbName("demodb");
        task.setLevel("1");
        task.setVolumeName("demodb_backup_lv1");
        task.setBackupDir("/opt/frameworks/cubrid/databases/demodb/backup");
        task.setRemoveLog(false);
        task.setCheckDatabaseConsist(true);
        task.setThreadCount("0");
        task.setZiped(true);
        task.setSafeReplication(false);

        task.execute();

        assertNull(task.getErrorMsg());
    }

    public void testBackupLevel2() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        BackupDbTask task = new BackupDbTask(serverInfo);
        task.setDbName("demodb");
        task.setLevel("2");
        task.setVolumeName("demodb_backup_lv2");
        task.setBackupDir("/opt/frameworks/cubrid/databases/demodb/backup");
        task.setRemoveLog(false);
        task.setCheckDatabaseConsist(true);
        task.setThreadCount("0");
        task.setZiped(true);
        task.setSafeReplication(false);

        task.execute();

        assertNull(task.getErrorMsg());
    }
}
