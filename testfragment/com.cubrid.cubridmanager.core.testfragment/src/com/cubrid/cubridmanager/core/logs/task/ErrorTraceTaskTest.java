package com.cubrid.cubridmanager.core.logs.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;

public class ErrorTraceTaskTest extends SetupEnvTestCase {

    public void testSend() throws Exception {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/logs/task/test.message/ErrorTrace_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // composite message
        ErrorTraceTask task = new ErrorTraceTask(serverInfo);
        task.setLogPath("/home/yangming/cubrid/log/broker/sql_log/query_editor_1.sql.log");
        task.setErrId("10");
        task.setErrTime("01/31 14:13:34.358");
        // compare
        assertEquals(msg, task.getRequest());
    }

    public void testReceive() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/logs/task/test.message/ErrorTrace_receive");
        String msg = Tool.getFileContent(filepath);
        TreeNode node = MessageUtil.parseResponse(msg);
        ErrorTraceTask task = new ErrorTraceTask(serverInfo);
        task.setResponse(node);
        // compare
        assertTrue(task.getErrorLogs().size() > 0);
        task.setErrorMsg("error");
        assertNull(task.getErrorLogs());
    }
}
