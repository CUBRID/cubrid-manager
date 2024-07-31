package com.cubrid.cubridmanager.core.replication.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import com.cubrid.cubridmanager.core.replication.model.ReplicationInfo;

public class GetReplicationInfoTaskTest extends SetupEnvTestCase {

    public void testSend() throws Exception {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/replication/task/test.message/GetReplicationInfo_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        GetReplicationInfoTask task = new GetReplicationInfoTask(serverInfo);
        task.setDistDbName("dist_biaodb");
        task.setDbaPassword("123456");
        task.setRunningMode(true);
        // case 1
        assertEquals(msg, Tool.decryptContent(serverInfo, task.getRequest()));
        // case 2
        task.setRunningMode(false);
        msg = msg.replaceFirst("mode:C", "mode:S");
        assertEquals(msg, Tool.decryptContent(serverInfo, task.getRequest()));
    }

    public void testReceive() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;
        // case 1
        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/replication/task/test.message/GetReplicationInfo_receive");
        String msg = Tool.getFileContent(filepath);
        TreeNode node = MessageUtil.parseResponse(msg);
        GetReplicationInfoTask task = new GetReplicationInfoTask(serverInfo);
        task.setResponse(node);
        ReplicationInfo replInfo = task.getReplicationInfo();
        assertEquals(replInfo.getDistInfo().getAgentPort(), "6443");
        assertEquals(
                replInfo.getDistInfo().getCopyLogPath(),
                "/home/biaozhang/cubrid/databases/dist_biaodb");
        assertEquals(
                replInfo.getDistInfo().getTrailLogPath(),
                "/home/biaozhang/cubrid/databases/dist_biaodb");
        assertEquals(
                replInfo.getDistInfo().getErrorLogPath(),
                "/home/biaozhang/cubrid/databases/dist_biaodb");
        assertEquals(replInfo.getDistInfo().getDelayTimeLogSize(), "1090");
        assertTrue(replInfo.getDistInfo().isRestartReplWhenError());
        assertEquals(replInfo.getMasterList().get(0).getMasterDbName(), "src_biaodb");
        assertEquals(replInfo.getMasterList().get(0).getMasterIp(), "192.168.1.132");
        assertEquals(replInfo.getMasterList().get(0).getReplServerPort(), "5555");
        assertEquals(replInfo.getSlaveList().get(0).getSlaveDbName(), "dest_biaodb");
        assertEquals(replInfo.getSlaveList().get(0).getSlaveIP(), "192.168.1.212");
        assertEquals(replInfo.getSlaveList().get(0).getDbUser(), "dba");
        assertEquals(replInfo.getSlaveList().get(0).getPassword(), "123456");

        // exception case1
        task.setResponse(null);
        assertTrue(task.getReplicationInfo() == null);
        // exception case2
        task.setResponse(node);
        task.setErrorMsg("has error");
        assertTrue(task.getReplicationInfo() == null);
    }

    public void test() throws Exception {
        if (!isConnectRealEnv) {
            return;
        }
        GetReplicationInfoTask task = new GetReplicationInfoTask(serverInfo);
        task.setDistDbName("dist_biaodb");
        task.setDbaPassword("123456");

        task.execute();
        // compare
        assertEquals(true, task.isSuccess());
    }
}
