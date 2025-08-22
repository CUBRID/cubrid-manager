package com.cubrid.cubridmanager.core.replication.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import java.util.ArrayList;
import java.util.List;

public class ChangeReplTablesTaskTest extends SetupEnvTestCase {

    public void testReplTable() throws Exception {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/replication/task/test.message/ChangeReplTables_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // composite message
        ChangeReplTablesTask task = new ChangeReplTablesTask(serverInfo);
        task.setDistdbName("distdb");
        task.setDistdbPassword("123456");
        task.setMdbName("mdb");
        task.setMdbUserId("dba");
        task.setMdbPass("123456");
        List<String> list = new ArrayList<String>();
        list.add("test1");
        list.add("test2");
        task.setReplicatedClasses(list);
        // compare
        assertEquals(msg, task.getRequest());
    }

    public void testReplAll() throws Exception {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/replication/task/test.message/ChangeReplTables_send_all");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // case 1
        ChangeReplTablesTask task = new ChangeReplTablesTask(serverInfo);
        task.setDistdbName("distdb");
        task.setDistdbPassword("123456");
        task.setMdbName("mdb");
        task.setMdbUserId("dba");
        task.setMdbPass("123456");
        task.setReplAllClasses(true);
        assertEquals(msg, task.getRequest());
        // case 2
        task.setReplAllClasses(false);
        msg = msg.replaceAll("all:Y", "all:N");
        assertEquals(msg, task.getRequest());
    }

    public void testReplNone() throws Exception {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/replication/task/test.message/ChangeReplTables_send_none");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // case 1
        ChangeReplTablesTask task = new ChangeReplTablesTask(serverInfo);
        task.setDistdbName("distdb");
        task.setDistdbPassword("123456");
        task.setMdbName("mdb");
        task.setMdbUserId("dba");
        task.setMdbPass("123456");
        task.setReplNoneClasses(true);
        assertEquals(msg, task.getRequest());
        // case 2
        task.setReplNoneClasses(false);
        msg = msg.replaceAll("none:Y", "none:N");
        assertEquals(msg, task.getRequest());
    }

    public void testReceive() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/replication/task/test.message/ChangeReplTables_receive");
        String msg = Tool.getFileContent(filepath);

        TreeNode node = MessageUtil.parseResponse(msg);
        // compare
        assertEquals("success", node.getValue("status"));
    }

    public void test1() throws Exception {
        if (!isConnectRealEnv) {
            return;
        }
        ChangeReplTablesTask task = new ChangeReplTablesTask(serverInfo);
        task.setDistdbName("dist_biaodb");
        task.setDistdbPassword("123456");
        task.setMdbName("src_biaodb");
        task.setMdbUserId("dba");
        task.setMdbPass("123456");
        List<String> list = new ArrayList<String>();
        list.add("t1");
        list.add("t2");
        task.setReplicatedClasses(list);
        task.execute();
        // compare
        assertEquals(true, task.isSuccess());
    }

    public void test2() throws Exception {
        if (!isConnectRealEnv) {
            return;
        }
        ChangeReplTablesTask task = new ChangeReplTablesTask(serverInfo);
        task.setDistdbName("dist_biaodb");
        task.setDistdbPassword("123456");
        task.setMdbName("src_biaodb");
        task.setMdbUserId("dba");
        task.setMdbPass("123456");
        task.setReplAllClasses(true);
        task.execute();
        // compare
        assertEquals(true, task.isSuccess());
    }

    public void test3() throws Exception {
        if (!isConnectRealEnv) {
            return;
        }
        ChangeReplTablesTask task = new ChangeReplTablesTask(serverInfo);
        task.setDistdbName("dist_biaodb");
        task.setDistdbPassword("123456");
        task.setMdbName("src_biaodb");
        task.setMdbUserId("dba");
        task.setMdbPass("123456");
        task.setReplNoneClasses(true);
        task.execute();
        // compare
        assertEquals(true, task.isSuccess());
    }
    /**/

}
