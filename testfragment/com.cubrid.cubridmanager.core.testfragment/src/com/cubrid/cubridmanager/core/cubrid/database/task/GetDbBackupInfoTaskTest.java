package com.cubrid.cubridmanager.core.cubrid.database.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import com.cubrid.cubridmanager.core.cubrid.database.model.DbBackupHistoryInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DbBackupInfo;
import java.util.List;

public class GetDbBackupInfoTaskTest extends SetupEnvTestCase {

    public void testSend() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/database/task/test.message/getdbbackupinfo_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // composite message
        GetDbBackupInfoTask task = new GetDbBackupInfoTask(serverInfo);
        task.setDbName("demodb");
        // compare
        assertEquals(msg, task.getRequest());
    }

    public void testReceive() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;
        // case 1
        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/database/task/test.message/getdbbackupinfo_receive");
        String msg = Tool.getFileContent(filepath);
        TreeNode node = MessageUtil.parseResponse(msg);
        GetDbBackupInfoTask task = new GetDbBackupInfoTask(serverInfo);
        task.setResponse(node);
        DbBackupInfo dbBackupInfo = task.getDbBackupInfo();
        assertEquals(
                dbBackupInfo.getDbDir(), "/home/biaozhang/cubrid/databases/distdb_pang/backup");
        assertEquals(dbBackupInfo.getFreeSpace(), "814952");
        assertTrue(dbBackupInfo.getBackupHistoryList().size() == 1);
        assertEquals(dbBackupInfo.getBackupHistoryList().get(0).getDate(), "2010.01.08.15.11");
        assertEquals(dbBackupInfo.getBackupHistoryList().get(0).getSize(), "3159040");
        assertEquals(dbBackupInfo.getBackupHistoryList().get(0).getLevel(), "level0");
        assertEquals(
                dbBackupInfo.getBackupHistoryList().get(0).getPath(),
                "/home/biaozhang/cubrid/databases/distdb_pang/backup/distdb_pang_backup_lv0");

        // exception case1
        task.setResponse(null);
        assertTrue(task.getDbBackupInfo() == null);
        // exception case2
        task.setResponse(node);
        task.setErrorMsg("has error");
        assertTrue(task.getDbBackupInfo() == null);
        // case 2
        filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/database/task/test.message/getdbbackupinfo_receive2");
        msg = Tool.getFileContent(filepath);
        node = MessageUtil.parseResponse(msg);
        GetDbBackupInfoTask task2 = new GetDbBackupInfoTask(serverInfo);
        task2.setResponse(node);
        task2.getDbBackupInfo();

        // case 3
        filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/database/task/test.message/getdbbackupinfo_receive3");
        msg = Tool.getFileContent(filepath);
        node = MessageUtil.parseResponse(msg);
        GetDbBackupInfoTask task3 = new GetDbBackupInfoTask(serverInfo);
        task3.setResponse(node);
        task3.getDbBackupInfo();

        // case 4
        filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/database/task/test.message/getdbbackupinfo_receive4");
        msg = Tool.getFileContent(filepath);
        node = MessageUtil.parseResponse(msg);
        GetDbBackupInfoTask task4 = new GetDbBackupInfoTask(serverInfo);
        task4.setResponse(node);
        task4.getDbBackupInfo();
    }

    public void testBackupExist() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.backupdbinfo.001.req.txt>");

        GetDbBackupInfoTask task = new GetDbBackupInfoTask(serverInfo);
        task.setDbName("demodb");

        task.execute();

        assertTrue(task.isSuccess());
        assertNull(task.getErrorMsg());

        DbBackupInfo info = task.getDbBackupInfo();
        assertNotNull(info);
        assertEquals("/opt/frameworks/cubrid2/databases/demodb/backup", info.getDbDir());
        assertEquals("15216", info.getFreeSpace());

        List<DbBackupHistoryInfo> list = info.getBackupHistoryList();
        assertNotNull(list);
        assertEquals(1, list.size());

        DbBackupHistoryInfo his = list.get(0);
        assertEquals("level0", his.getLevel());
        assertEquals(
                "/opt/frameworks/cubrid2/databases/demodb/backup/demodb_backup_lv0", his.getPath());
        assertEquals("5256192", his.getSize());
        assertEquals("2009.06.26.22.37", his.getDate());
    }

    public void testNoBackup() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.backupdbinfo.002.req.txt>");

        GetDbBackupInfoTask task = new GetDbBackupInfoTask(serverInfo);
        task.setDbName("nobackupdb");

        task.execute();

        assertTrue(task.isSuccess());
        assertNull(task.getErrorMsg());

        DbBackupInfo info = task.getDbBackupInfo();
        assertNotNull(info);
        assertEquals("/opt/frameworks/cubrid2/databases/nobackupdb/backup", info.getDbDir());
        assertEquals("15216", info.getFreeSpace());

        List<DbBackupHistoryInfo> list = info.getBackupHistoryList();
        assertNotNull(list);
        assertEquals(0, list.size());
    }

    public void testNotExistDb() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.backupdbinfo.003.req.txt>");

        GetDbBackupInfoTask task = new GetDbBackupInfoTask(serverInfo);
        task.setDbName("notexistdb");

        task.execute();

        assertFalse(task.isSuccess());
        assertNotNull(task.getErrorMsg());
        assertEquals(
                "Can not find the directory database(notexistdb) is located", task.getErrorMsg());
    }
}
