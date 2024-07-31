package com.cubrid.cubridmanager.core.replication.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import com.cubrid.cubridmanager.core.replication.model.ReplicationParamConstants;
import java.util.HashMap;
import java.util.Map;

public class SetReplicationParamTaskTest extends SetupEnvTestCase {

    public void testSend() throws Exception {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/replication/task/test.message/SetReplicationParam_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // composite message
        SetReplicationParamTask task = new SetReplicationParamTask(serverInfo);
        task.setMasterDbName("wings_mdb");
        task.setSlaveDbName("wings_sdb");
        task.setDistDbName("wings_dist");
        task.setDistDbDbaPasswd("123456");
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put(ReplicationParamConstants.PERF_POLL_INTERVAL, "30");
        paramMap.put(ReplicationParamConstants.SIZE_OF_LOG_BUFFER, "200");
        paramMap.put(ReplicationParamConstants.SIZE_OF_CACHE_BUFFER, "300");
        paramMap.put(ReplicationParamConstants.SIZE_OF_COPYLOG, "2000");
        paramMap.put(ReplicationParamConstants.INDEX_REPLICATION, "Y");
        paramMap.put(ReplicationParamConstants.FOR_RECOVERY, "Y");
        paramMap.put(ReplicationParamConstants.LOG_APPLY_INTERVAL, "40");
        paramMap.put(ReplicationParamConstants.RESTART_INTERVAL, "50");
        task.setParameterMap(paramMap);
        task.setRunningMode(false);
        // case 1
        assertEquals(msg, Tool.decryptContent(serverInfo, task.getRequest()));
        // case 2
        String msg1 = msg.replaceFirst("mode:S", "mode:C");
        task.setRunningMode(true);
        assertEquals(msg1, Tool.decryptContent(serverInfo, task.getRequest()));
        // exception case
        task.setParameterMap(null);
        task.setRunningMode(true);
        assertFalse(msg.equals(Tool.decryptContent(serverInfo, task.getRequest())));
    }

    public void testReceive() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/replication/task/test.message/SetReplicationParam_receive");
        String msg = Tool.getFileContent(filepath);
        TreeNode node = MessageUtil.parseResponse(msg);
        SetReplicationParamTask task = new SetReplicationParamTask(serverInfo);
        task.setResponse(node);
        // compare
        assertTrue(task.isSuccess());
        // test exception case 1
        task.setResponse(null);
        assertFalse(task.isSuccess());
    }

    public void test() throws Exception {
        if (!isConnectRealEnv) {
            return;
        }
        SetReplicationParamTask task = new SetReplicationParamTask(serverInfo);
        task.setMasterDbName("src_biaodb");
        task.setSlaveDbName("dest_biaodb");
        task.setDistDbName("dist_biaodb");
        task.setDistDbDbaPasswd("123456");
        task.execute();
        // compare
        assertEquals(true, task.isSuccess());
    }
}
