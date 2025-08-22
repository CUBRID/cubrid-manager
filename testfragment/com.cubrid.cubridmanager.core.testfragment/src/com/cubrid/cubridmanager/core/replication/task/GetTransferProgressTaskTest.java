package com.cubrid.cubridmanager.core.replication.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import com.cubrid.cubridmanager.core.replication.model.TransFileProgressInfo;
import java.util.Map;

public class GetTransferProgressTaskTest extends SetupEnvTestCase {

    public void testSend() throws Exception {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/replication/task/test.message/GetTransferProgress_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // composite message
        GetTransferProgressTask task = new GetTransferProgressTask(serverInfo);
        task.setPid("2563");
        // compare
        assertEquals(msg, task.getRequest());
    }

    public void testReceive() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/replication/task/test.message/GetTransferProgress_receive");
        String msg = Tool.getFileContent(filepath);
        // case 1
        TreeNode node = MessageUtil.parseResponse(msg);
        GetTransferProgressTask task = new GetTransferProgressTask(serverInfo);
        task.setResponse(node);
        TransFileProgressInfo progressInfo = task.getProgressInfo();
        assertEquals(progressInfo.getTransferStatus(), "success");
        assertEquals(progressInfo.getTransferNote(), "none");
        assertEquals(progressInfo.getSourceDir(), "/tmp");
        assertEquals(progressInfo.getDestHost(), "192.168.1.220");
        assertEquals(
                progressInfo.getDestDir(),
                "/home/biaozhang/new_build_cubrid32/databases/target_dir");
        assertEquals(progressInfo.getFileNum(), "2");
        Map<String, String> map = progressInfo.getFileProgressMap();
        assertEquals(map.get("biaodb_src_bk0v000"), "100%");
        assertEquals(map.get("biaodb_src_bkvinf"), "100%");
        // case 2
        String msg1 = msg.replaceFirst("transfer_status:success", "transfer_status:failure");
        msg1 = msg1.replaceFirst("transfer_note:none", "transfer_note:sorry");
        node = MessageUtil.parseResponse(msg1);
        task.setResponse(node);
        progressInfo = task.getProgressInfo();
        assertEquals(progressInfo.getTransferStatus(), "failure");
        assertEquals(progressInfo.getTransferNote(), "sorry");
        // case 3
        String msg2 = msg.replaceFirst("transfer_status:success", "transfer_status:transfering");
        node = MessageUtil.parseResponse(msg2);
        task.setResponse(node);
        progressInfo = task.getProgressInfo();
        assertEquals(progressInfo.getTransferStatus(), "transfering");
        // case 4
        msg = msg.replaceFirst("open:progress_file_list", "open:aa");
        node = MessageUtil.parseResponse(msg);
        task.setResponse(node);
        task.getProgressInfo();
        msg = msg.replaceFirst("open:aa", "");
        msg = msg.replaceFirst("close:progress_file_list", "");
        node = MessageUtil.parseResponse(msg);
        task.setResponse(node);
        task.getProgressInfo();
        // exception case1
        task.setResponse(null);
        assertTrue(task.getProgressInfo() == null);
        // exception case2
        task.setResponse(node);
        task.setErrorMsg("has error");
        assertTrue(task.getProgressInfo() == null);
    }

    public void test() throws Exception {
        if (!isConnectRealEnv) {
            return;
        }
        GetTransferProgressTask task = new GetTransferProgressTask(serverInfo);
        task.setPid("1827");
        task.execute();
        // compare
        assertEquals(true, task.isSuccess());
    }
}
