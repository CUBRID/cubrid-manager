package com.cubrid.cubridmanager.core.replication.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;

public class GetReplAgentStatusTaskTest extends SetupEnvTestCase {

    public void testSend() throws Exception {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/replication/task/test.message/GetReplAgentStatus_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // composite message
        GetReplAgentStatusTask task = new GetReplAgentStatusTask(serverInfo);
        task.setDbName("distdb");
        // compare
        assertEquals(msg, task.getRequest());
    }

    public void testReceive() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;
        // test correct case
        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/replication/task/test.message/GetReplAgentStatus_receive");
        String msg = Tool.getFileContent(filepath);
        GetReplAgentStatusTask task = new GetReplAgentStatusTask(serverInfo);
        // test isActive is true
        TreeNode node = MessageUtil.parseResponse(msg);
        task.setResponse(node);
        assertTrue(task.isActive());
        // test isActive is false
        msg = msg.replaceFirst("is_active:Y", "is_active:N");
        node = MessageUtil.parseResponse(msg);
        task.setResponse(node);
        assertFalse(task.isActive());

        msg = msg.replaceFirst("is_active:N", "");
        node = MessageUtil.parseResponse(msg);
        task.setResponse(node);
        assertFalse(task.isActive());
        // test exception case 1
        task.setResponse(null);
        assertFalse(task.isActive());
        // test exception case 2
        task.setResponse(node);
        task.setErrorMsg("has error");
        assertFalse(task.isActive());
    }

    public void test() throws Exception {
        if (!isConnectRealEnv) {
            return;
        }
        GetReplAgentStatusTask task = new GetReplAgentStatusTask(serverInfo);
        task.setDbName("dist_biaodb");
        task.execute();
        // compare
        assertEquals(true, task.isSuccess());
    }
}
