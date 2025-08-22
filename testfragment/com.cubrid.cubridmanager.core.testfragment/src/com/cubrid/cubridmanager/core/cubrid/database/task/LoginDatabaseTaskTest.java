package com.cubrid.cubridmanager.core.cubrid.database.task;

import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import java.util.ArrayList;
import java.util.List;

public class LoginDatabaseTaskTest extends SetupEnvTestCase {

    public void testSend() throws Exception {
        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/database/task/test.message/logindb_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // composite message
        LoginDatabaseTask task = new LoginDatabaseTask(serverInfo);
        task.setDbName("demodb");
        task.setCMUser("admin");
        task.setDbPassword("");
        task.setDbUser("dba");
        assertEquals(msg, Tool.decryptContent(serverInfo, task.getRequest()));
    }

    public void testReceive() throws Exception {

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/database/task/test.message/logindb_receive");
        String msg = Tool.getFileContent(filepath);
        TreeNode node = MessageUtil.parseResponse(msg);

        LoginDatabaseTask task = new LoginDatabaseTask(serverInfo);
        task.setResponse(node);
        DbUserInfo dbUserInfo = task.getLoginedDbUserInfo();
        assertEquals(dbUserInfo.getDbName(), "demodb");
        assertTrue(dbUserInfo.isDbaAuthority());

        // exception case1
        task.setResponse(null);
        assertTrue(task.getLoginedDbUserInfo() == null);

        // case 2
        filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/database/task/test.message/logindb_receive2");
        msg = Tool.getFileContent(filepath);
        node = MessageUtil.parseResponse(msg);

        LoginDatabaseTask task2 = new LoginDatabaseTask(serverInfo);
        task2.setResponse(node);
        task2.getLoginedDbUserInfo();

        // case 3
        filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/database/task/test.message/logindb_receive2");
        msg = Tool.getFileContent(filepath);
        node = MessageUtil.parseResponse(msg);

        LoginDatabaseTask task3 = new LoginDatabaseTask(serverInfo);
        task3.setResponse(node);
        task3.getLoginedDbUserInfo();
    }

    public void testSetDbUser() {
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setServerVersion("8.2.0");
        LoginDatabaseTask task = new LoginDatabaseTask(serverInfo);
        task.setDbUser("adf");
    }

    public void testSetDbPasswd() {
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setServerVersion("8.2.0");
        LoginDatabaseTask task = new LoginDatabaseTask(serverInfo);
        task.setDbPassword("adf");
    }

    public void testSetCMUser() {
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setServerVersion("8.2.0");
        LoginDatabaseTask task = new LoginDatabaseTask(serverInfo);
        task.setCMUser("user");
    }

    public void testCancel() {
        ServerInfo serverInfo = new ServerInfo();
        DatabaseInfo dbInfo = new DatabaseInfo("dbname", serverInfo);
        ServerUserInfo serverUserInfo = new ServerUserInfo();
        List<DatabaseInfo> dbInfoList = new ArrayList<DatabaseInfo>();
        dbInfoList.add(dbInfo);
        serverUserInfo.setDatabaseInfoList(dbInfoList);
        serverInfo.setLoginedUserInfo(serverUserInfo);

        LoginDatabaseTask task = new LoginDatabaseTask(serverInfo);
        task.setDbName("dbname");
        task.cancel();
        assertFalse(serverInfo.getLoginedUserInfo().getDatabaseInfo("dbname").isLogined());
    }
}
