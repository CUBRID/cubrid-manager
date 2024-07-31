package com.cubrid.cubridmanager.core.cubrid.database.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import java.util.ArrayList;
import java.util.List;

public class RenameDbTaskTest extends SetupEnvTestCase {

    public void testSend() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/database/task/test.message/renamedb_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // case 1
        RenameDbTask task = new RenameDbTask(serverInfo);
        task.setDbName("testdb3");
        task.setNewDbName("testdb4");
        task.setAdvanced(false);
        task.setForceDel(true);
        task.setExVolumePath("C:\\CUBRID\\databases\\testdb4");
        List<String> volList = new ArrayList<String>();
        volList.add("volume1");
        volList.add("volume2");
        task.setIndividualVolume(volList);
        assertEquals(msg, task.getRequest());
        // case 2
        task.setAdvanced(true);
        task.setForceDel(false);
        assertFalse(msg.equals(task.getRequest()));
    }

    public void testReceive() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/database/task/test.message/renamedb_receive");
        String msg = Tool.getFileContent(filepath);
        TreeNode node = MessageUtil.parseResponse(msg);
        RenameDbTask task = new RenameDbTask(serverInfo);
        task.setResponse(node);
        assertTrue(task.isSuccess());
    }

    public void testRenameDbNotExist() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.renamedb.001.req.txt>");

        RenameDbTask task = new RenameDbTask(serverInfo);
        task.setDbName("notexistdb");
        task.setNewDbName("newdb");
        task.setAdvanced(false);
        task.setForceDel(true);
        task.setExVolumePath("/opt/frameworks/cubrid/databases/newdb");

        task.execute();

        assertTrue(task.isSuccess());
        assertNull(task.getErrorMsg());
    }

    public void testRenameDbExistNewDb() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.renamedb.002.req.txt>");

        RenameDbTask task = new RenameDbTask(serverInfo);
        task.setDbName("existdb");
        task.setNewDbName("newdb");
        task.setAdvanced(false);
        task.setForceDel(false);
        task.setExVolumePath("/opt/frameworks/cubrid/databases/existdb");

        task.execute();

        assertTrue(task.isSuccess());
        assertNull(task.getErrorMsg());
    }

    public void testRenameDbSuccessfully() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.renamedb.003.req.txt>");

        RenameDbTask task = new RenameDbTask(serverInfo);
        task.setDbName("existdb");
        task.setNewDbName("newdb");
        task.setAdvanced(false);
        task.setForceDel(true);
        task.setExVolumePath("/opt/frameworks/cubrid/databases/newdb");

        task.execute();

        assertTrue(task.isSuccess());
        assertNull(task.getErrorMsg());
    }

    public void testRenameDbEmptyDbName() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.renamedb.004.req.txt>");

        RenameDbTask task = new RenameDbTask(serverInfo);
        task.setDbName(null);
        task.setNewDbName("newdb");
        task.setAdvanced(false);
        task.setForceDel(true);
        task.setExVolumePath("/opt/frameworks/cubrid/databases/newdb");

        task.execute();

        assertFalse(task.isSuccess());
        assertNotNull(task.getErrorMsg());
    }

    public void testRenameDbEmptyNewDbName() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.renamedb.005.req.txt>");

        RenameDbTask task = new RenameDbTask(serverInfo);
        task.setDbName("demodb");
        task.setNewDbName(null);
        task.setAdvanced(false);
        task.setForceDel(true);
        task.setExVolumePath("/opt/frameworks/cubrid/databases/newdb");

        task.execute();

        assertFalse(task.isSuccess());
        assertNotNull(task.getErrorMsg());
    }
}
