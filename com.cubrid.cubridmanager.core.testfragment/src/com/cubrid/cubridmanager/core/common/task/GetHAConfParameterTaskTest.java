package com.cubrid.cubridmanager.core.common.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import java.util.List;

public class GetHAConfParameterTaskTest extends SetupEnvTestCase {

    public void testSend() throws Exception {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;
        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/common/task/test.message/gethaconfpara_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        GetHAConfParameterTask task = new GetHAConfParameterTask(serverInfo);
        // compare
        assertEquals(msg, task.getRequest());
    }

    public void testReceive() throws Exception {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;
        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/common/task/test.message/gethaconfpara_receive");
        String msg = Tool.getFileContent(filepath);

        TreeNode node = MessageUtil.parseResponse(msg);
        GetHAConfParameterTask task = new GetHAConfParameterTask(serverInfo);
        task.setResponse(node);
        task.getConfParameters();
        assertEquals("success", node.getValue("status"));
        // case 2
        msg = msg.replaceFirst("open:conflist", "open:conflist1");
        node = MessageUtil.parseResponse(msg);
        task.setResponse(node);
        task.getConfParameters();
        // case 3
        msg = msg.replaceFirst("open:conflist1", "");
        msg = msg.replaceFirst("close:conflist", "");
        node = MessageUtil.parseResponse(msg);
        task.setResponse(node);
        task.getConfParameters();
        // case 4
        filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/common/task/test.message/gethaconfpara_receive2");
        msg = Tool.getFileContent(filepath);

        node = MessageUtil.parseResponse(msg);
        GetHAConfParameterTask task2 = new GetHAConfParameterTask(serverInfo);
        task2.setResponse(node);
        task2.getConfParameters();
        // exception case1
        task.setResponse(null);
        task.getConfParameters();
        // exception case2
        task.setResponse(node);
        task.setErrorMsg("has error");
        task.getConfParameters();
    }

    public void testGetContents() throws Exception {
        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/common/task/test.message/gethaconfpara_receive");
        String msg = Tool.getFileContent(filepath);

        TreeNode node = MessageUtil.parseResponse(msg);
        GetHAConfParameterTask task = new GetHAConfParameterTask(serverInfo);
        task.setResponse(node);
        task.getConfContents();
        assertEquals("success", node.getValue("status"));

        task.setResponse(null);
        List<String> list = task.getConfContents();
        assertNull(list);
    }
}
