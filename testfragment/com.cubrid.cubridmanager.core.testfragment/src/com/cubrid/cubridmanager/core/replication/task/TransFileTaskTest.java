package com.cubrid.cubridmanager.core.replication.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import java.util.ArrayList;
import java.util.List;

public class TransFileTaskTest extends SetupEnvTestCase {

    public void testSend() throws Exception {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/replication/task/test.message/transfile_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // composite message
        TransFileTask task = new TransFileTask(serverInfo);
        task.setMasterDbDir("/home/cubrid/databases/master");
        task.setSlaveDbHost("192.168.0.1");
        task.setSlaveDbDir("/home/cubrid/databases/slave");
        task.setSlaveCmServerPort("8001");
        List<String> fileList = new ArrayList<String>();
        fileList.add("biaodb_src_bkvinf");
        fileList.add("biaodb_src_bk0v000");
        task.setBackupFileList(fileList);
        // compare
        assertEquals(msg, task.getRequest());
    }

    public void testReceive() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/replication/task/test.message/transfile_receive");
        String msg = Tool.getFileContent(filepath);
        TreeNode node = MessageUtil.parseResponse(msg);
        TransFileTask task = new TransFileTask(serverInfo);
        task.setResponse(node);
        // compare
        assertTrue(task.isSuccess());
        assertEquals("2563", task.getTransFilePid());
        // test exception case 1
        task.setResponse(null);
        assertTrue(task.getTransFilePid() == null);
        // test exception case 2
        task.setResponse(node);
        task.setErrorMsg("has error");
        assertTrue(task.getTransFilePid() == null);
    }

    public void test() throws Exception {
        if (!isConnectRealEnv) {
            return;
        }
        TransFileTask task = new TransFileTask(serverInfo);
        task.setMasterDbDir("/home/biaozhang/cubrid/databases/dest_biaodb");
        task.setSlaveDbHost("192.168.0.2");
        task.setSlaveDbDir("anothterslavedb");
        task.setSlaveCmServerPort("8112");
        List<String> fileList = new ArrayList<String>();
        fileList.add("src_biaodb_bkvinf");
        fileList.add("src_biaodb_bk0v000");
        task.setBackupFileList(fileList);
        task.execute();
        // compare
        assertEquals(true, task.isSuccess());
    }
}
