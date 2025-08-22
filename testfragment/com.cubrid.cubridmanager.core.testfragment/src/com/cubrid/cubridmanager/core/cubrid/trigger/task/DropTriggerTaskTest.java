package com.cubrid.cubridmanager.core.cubrid.trigger.task;

import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;

public class DropTriggerTaskTest extends SetupEnvTestCase {
    String dbname = "demodb";
    private DropTriggerTask task;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        task = new DropTriggerTask(serverInfo);
    }

    public void testSend() throws Exception {
        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/trigger/task/test.message/droptrigger_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // composite message

        task.setDbName(dbname);
        task.setTriggerName("update_monitor");
        // compare
        assertEquals(msg, task.getRequest());
    }

    public void testReceive() throws Exception {
        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/trigger/task/test.message/droptrigger_receive");
        String msg = Tool.getFileContent(filepath);

        TreeNode node = MessageUtil.parseResponse(msg);

        task.setResponse(node);

        boolean success = task.isSuccess();
        assertTrue(success);
    }

    public void testReceiveError() throws Exception {
        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/trigger/task/test.message/droptrigger_receive_error");
        String msg = Tool.getFileContent(filepath);

        TreeNode node = MessageUtil.parseResponse(msg);
        task.setResponse(node);

        boolean success = task.isSuccess();
        assertFalse(success);
    }
}
