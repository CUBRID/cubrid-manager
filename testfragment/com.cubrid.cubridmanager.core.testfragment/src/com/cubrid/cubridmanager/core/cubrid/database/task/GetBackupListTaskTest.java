package com.cubrid.cubridmanager.core.cubrid.database.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import java.util.List;

public class GetBackupListTaskTest extends SetupEnvTestCase {

    public void testSend() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/database/task/test.message/getbackuplist_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // composite message
        GetBackupListTask task = new GetBackupListTask(serverInfo);
        task.setDbName("demodb");
        // compare
        assertEquals(msg, task.getRequest());
    }

    public void testReceive() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/database/task/test.message/getbackuplist_receive");
        String msg = Tool.getFileContent(filepath);
        TreeNode node = MessageUtil.parseResponse(msg);

        GetBackupListTask task = new GetBackupListTask(serverInfo);
        task.setResponse(node);
        List<String> list = task.getDbBackupList();
        assertTrue(list.size() > 0);

        // exception case1
        task.setResponse(null);
        assertTrue(task.getDbBackupList() == null);
        // exception case2
        task.setResponse(node);
        task.setErrorMsg("has error");
        assertTrue(task.getDbBackupList() == null);
    }

    public void testBackupList() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.backuplist.001.req.txt>");

        GetBackupListTask task = new GetBackupListTask(serverInfo);
        task.setDbName("demodb");
        task.execute();

        assertTrue(task.isSuccess());

        List<String> backupList = task.getDbBackupList();
        assertEquals(3, backupList.size());

        assertEquals(
                "/opt/frameworks/cubrid2/databases/demodb/backup/demodb_backup_lv0",
                backupList.get(0));
        assertEquals(
                "/opt/frameworks/cubrid2/databases/demodb/backup/demodb_backup_lv1",
                backupList.get(1));
        assertEquals(
                "/opt/frameworks/cubrid2/databases/demodb/backup/demodb_backup_lv2",
                backupList.get(2));
    }

    public void testNotExistDb() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.backuplist.002.req.txt>");

        GetBackupListTask task = new GetBackupListTask(serverInfo);
        task.setDbName("notexistdb");
        task.execute();

        assertFalse(task.isSuccess());
        assertEquals(
                "Can not find the directory database(notexistdb) is located", task.getErrorMsg());
    }

    public void testBackupNullDb() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.backuplist.003.req.txt>");

        GetBackupListTask task = new GetBackupListTask(serverInfo);
        task.setDbName(null);
        task.execute();

        assertFalse(task.isSuccess());
        assertEquals("Parameter(database name) missing in the request", task.getErrorMsg());
    }
}
