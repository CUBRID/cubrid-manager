package com.cubrid.cubridmanager.core.replication.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;

public class CreateSlaveDbTaskTest extends SetupEnvTestCase {

    public void testSend() throws Exception {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/replication/task/test.message/CreateSlaveDb_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // composite message
        CreateSlaveDbTask task = new CreateSlaveDbTask(serverInfo);
        task.setSlaveDbName("sdb");
        task.setSlaveDbPath("/home/biaozhang/cubrid/databases/dest_biaodb");
        task.setSlaveDbUser("dba");
        task.setSlaveDbPassword("123456");
        task.setMasterDbName("mdb");
        task.setMasterDbPassword("123456");
        task.setDistDbName("dist1");
        task.setDistDbaPassword("123456");
        // compare
        assertEquals(msg, Tool.decryptContent(serverInfo, task.getRequest()));
    }

    public void testReceive() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/replication/task/test.message/CreateSlaveDb_receive");
        String msg = Tool.getFileContent(filepath);

        TreeNode node = MessageUtil.parseResponse(msg);
        // compare
        assertEquals("success", node.getValue("status"));
    }

    public void test() throws Exception {
        if (!isConnectRealEnv) {
            return;
        }
        CreateSlaveDbTask task = new CreateSlaveDbTask(serverInfo);
        task.setSlaveDbName("aslavedb");
        task.setSlaveDbPath("/home/biaozhang/cubrid/databases/aslavedb");
        task.setSlaveDbUser("dba");
        task.setSlaveDbPassword("123456");
        task.setMasterDbName("src_biaodb");
        task.setMasterDbPassword("123456");
        task.setDistDbName("distdb1");
        task.setDistDbaPassword("123456");

        task.execute();
        // compare
        assertEquals(true, task.isSuccess());
    }
}
