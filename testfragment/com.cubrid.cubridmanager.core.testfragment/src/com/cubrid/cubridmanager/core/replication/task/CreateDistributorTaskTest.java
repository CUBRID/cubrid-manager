package com.cubrid.cubridmanager.core.replication.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;

public class CreateDistributorTaskTest extends SetupEnvTestCase {

    public void testSend() throws Exception {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/replication/task/test.message/CreateDistributor_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // composite message
        CreateDistributorTask task = new CreateDistributorTask(serverInfo);
        task.setDistDbName("distdb1");
        task.setDistDbPath("/home/agent0/cubrid/databases/distdb1");
        task.setDbaPassword("123456");
        task.setReplAgentPort("5555");
        task.setMasterDbName("mdb");
        task.setMasterDbIp("192.168.1.123");
        task.setReplServerPort("66666");
        task.setCopyLogPath("/home/agent0/cubrid/databases/distdb1");
        task.setTrailLogPath("/home/agent0/cubrid/databases/distdb1");
        task.setErrorLogPath("/home/agent0/cubrid/databases/distdb1");
        task.setDelayTimeLogSize("100");
        task.setRestartRepl(true);
        // compare
        assertEquals(msg, Tool.decryptContent(serverInfo, task.getRequest()));
        msg = msg.replaceFirst("restart_flag:y", "restart_flag:n");
        task.setRestartRepl(false);
        assertTrue(msg.equals(Tool.decryptContent(serverInfo, task.getRequest())));
    }

    public void testReceive() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/replication/task/test.message/CreateDistributor_receive");
        String msg = Tool.getFileContent(filepath);

        TreeNode node = MessageUtil.parseResponse(msg);
        // compare
        assertEquals("success", node.getValue("status"));
    }

    public void test() throws Exception {
        if (!isConnectRealEnv) {
            return;
        }
        CreateDistributorTask task = new CreateDistributorTask(serverInfo);
        task.setDistDbName("distdb1");
        task.setDbaPassword("123456");
        task.setReplAgentPort("5555");
        task.setMasterDbName("src_biaodb");
        task.setMasterDbIp("192.168.1.212");
        task.setReplServerPort("66666");
        task.setDelayTimeLogSize("100");
        task.setRestartRepl(true);
        task.execute();
        // compare
        assertEquals(true, task.isSuccess());
    }
}
