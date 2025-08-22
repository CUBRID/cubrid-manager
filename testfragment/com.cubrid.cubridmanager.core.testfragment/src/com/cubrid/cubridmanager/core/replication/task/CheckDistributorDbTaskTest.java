package com.cubrid.cubridmanager.core.replication.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;

public class CheckDistributorDbTaskTest extends SetupEnvTestCase {

    public void testSend() throws Exception {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/replication/task/test.message/CheckDistributorDb_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // case 1
        CheckDistributorDbTask task = new CheckDistributorDbTask(serverInfo);
        task.setDistDbName("distdb");
        task.setDbaPassword("123456");
        task.setRunningMode(false);
        assertEquals(msg, Tool.decryptContent(serverInfo, task.getRequest()));
        // case 2
        task.setRunningMode(true);
        msg = msg.replaceFirst("mode:S", "mode:C");
        assertEquals(msg, Tool.decryptContent(serverInfo, task.getRequest()));
    }

    public void testReceive() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/replication/task/test.message/CheckDistributorDb_receive");
        // case 1
        String msg = Tool.getFileContent(filepath);
        TreeNode node = MessageUtil.parseResponse(msg);
        CheckDistributorDbTask task = new CheckDistributorDbTask(serverInfo);
        task.setResponse(node);
        assertTrue(task.isDistributorDb());
        // case 2
        msg = msg.replaceFirst("is_distdb:Y", "is_distdb:N");
        node = MessageUtil.parseResponse(msg);
        task.setResponse(node);
        assertFalse(task.isDistributorDb());
        msg = msg.replaceFirst("is_distdb:N", "");
        node = MessageUtil.parseResponse(msg);
        task.setResponse(node);
        assertFalse(task.isDistributorDb());
        // exception case1
        task.setResponse(null);
        assertFalse(task.isDistributorDb());
        // exception case2
        task.setResponse(node);
        task.setErrorMsg("has error");
        assertFalse(task.isDistributorDb());
    }

    public void test() throws Exception {
        if (!isConnectRealEnv) {
            return;
        }
        CheckDistributorDbTask task = new CheckDistributorDbTask(serverInfo);
        task.setDistDbName("dist_biaodb");
        task.setDbaPassword("123456");
        task.setRunningMode(true);
        task.execute();
        // compare
        assertEquals(true, task.isSuccess());
    }
}
