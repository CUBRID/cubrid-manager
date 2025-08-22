package com.cubrid.cubridmanager.core.replication.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import com.cubrid.cubridmanager.core.replication.model.ReplicationParamConstants;
import com.cubrid.cubridmanager.core.replication.model.ReplicationParamInfo;

public class GetReplParmetersTaskTest extends SetupEnvTestCase {

    public void testSend() throws Exception {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/replication/task/test.message/GetReplicationParam_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // composite message
        GetReplicationParamTask task = new GetReplicationParamTask(serverInfo);
        task.setMasterDbName("wings_mdb");
        task.setSlaveDbName("wings_sdb");
        task.setDistDbName("wings_dist");
        task.setDistDbDbaPasswd("123456");
        task.setRunningMode(true);
        assertEquals(msg, Tool.decryptContent(serverInfo, task.getRequest()));

        task.setRunningMode(false);
        msg = msg.replaceFirst("mode:C", "mode:S");
        assertEquals(msg, Tool.decryptContent(serverInfo, task.getRequest()));
    }

    public void testReceive() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/replication/task/test.message/GetReplicationParam_receive");
        String msg = Tool.getFileContent(filepath);
        TreeNode node = MessageUtil.parseResponse(msg);
        GetReplicationParamTask task = new GetReplicationParamTask(serverInfo);
        task.setResponse(node);
        ReplicationParamInfo paramInfo = task.getReplicationParams();
        assertEquals(paramInfo.getParamValue(ReplicationParamConstants.PERF_POLL_INTERVAL), "30");
        assertEquals(paramInfo.getParamValue(ReplicationParamConstants.FOR_RECOVERY), "Y");
        // exception case1
        task.setResponse(null);
        assertTrue(task.getReplicationParams() == null);
        // exception case2
        task.setResponse(node);
        task.setErrorMsg("has error");
        assertTrue(task.getReplicationParams() == null);
    }

    public void testRealEnv() throws Exception {
        if (!isConnectRealEnv) {
            return;
        }
        GetReplicationParamTask task = new GetReplicationParamTask(serverInfo);
        task.setDistDbName("dist_biaodb");
        task.setDistDbDbaPasswd("123456");
        task.setMasterDbName("src_biaodb");
        task.setSlaveDbName("dest_biaodb");
        task.execute();
        // compare
        assertEquals(true, task.isSuccess());
    }
}
