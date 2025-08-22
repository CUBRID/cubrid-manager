package com.cubrid.cubridmanager.core.replication.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;

public class GetReplicatedTablesTaskTest extends SetupEnvTestCase {

    public void testSend() throws Exception {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/replication/task/test.message/GetReplicatedTables_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // composite message
        GetReplicatedTablesTask task = new GetReplicatedTablesTask(serverInfo);
        task.setDistdbName("distdb");
        task.setMasterdbName("mdb");
        task.setSlavedbName("sdb");
        task.setDistdbPassword("123456");
        task.setRunningMode(true);
        assertEquals(msg, Tool.decryptContent(serverInfo, task.getRequest()));

        task.setRunningMode(false);
        msg = msg.replaceFirst("mode:C", "mode:S");
        assertEquals(msg, Tool.decryptContent(serverInfo, task.getRequest()));
    }

    public void testReceive() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;
        // case 1
        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/replication/task/test.message/GetReplicatedTables_receive");
        String msg = Tool.getFileContent(filepath);
        TreeNode node = MessageUtil.parseResponse(msg);
        GetReplicatedTablesTask task = new GetReplicatedTablesTask(serverInfo);
        task.setResponse(node);
        String[] tables = task.getReplicatedTables();
        assertTrue(tables.length == 2);
        boolean isReplAll = task.isReplicateAll();
        assertFalse(isReplAll);
        // case 2
        msg = msg.replaceFirst("all_repl:Y", "all_repl:N");
        node = MessageUtil.parseResponse(msg);
        task.setResponse(node);
        task.isReplicateAll();
        msg = msg.replaceFirst("all_repl:N", "");
        node = MessageUtil.parseResponse(msg);
        task.setResponse(node);
        task.isReplicateAll();
        // case 3
        msg = msg.replaceFirst("open:repl_group_tablelist", "open:aa");
        node = MessageUtil.parseResponse(msg);
        task.setResponse(node);
        task.getReplicatedTables();
        msg = msg.replaceFirst("open:aa", "");
        msg = msg.replaceFirst("close:repl_group_tablelist", "");
        node = MessageUtil.parseResponse(msg);
        task.setResponse(node);
        task.getReplicatedTables();

        // exception case1
        task.setResponse(null);
        tables = task.getReplicatedTables();
        assertTrue(tables == null);
        isReplAll = task.isReplicateAll();
        assertFalse(isReplAll);

        // exception case2
        task.setResponse(node);
        task.setErrorMsg("hasError");
        tables = task.getReplicatedTables();
        assertTrue(tables == null);
        isReplAll = task.isReplicateAll();
        assertFalse(isReplAll);
    }

    public void test() throws Exception {
        if (!isConnectRealEnv) {
            return;
        }
        GetReplicatedTablesTask task = new GetReplicatedTablesTask(serverInfo);
        task.setDistdbName("dist_biaodb");
        task.setMasterdbName("src_biaodb");
        task.setSlavedbName("dest_biaodb");
        task.setDistdbPassword("123456");
        task.execute();
        // compare
        assertEquals(true, task.isSuccess());
    }
}
