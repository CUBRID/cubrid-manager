package com.cubrid.cubridmanager.core.cubrid.database.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;

public class LoadDbTaskTest extends SetupEnvTestCase {

    public void testSend() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/database/task/test.message/loaddb_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // case 1
        LoadDbTask task = new LoadDbTask(serverInfo);
        task.setDbName("testdb2");
        task.setCheckOption("both");
        task.setUsedPeriod(true, "12");
        task.setUsedPeriod(false, "none");
        task.setDbUser("dba");
        task.setUsedEstimatedSize(true, "12");
        task.setUsedEstimatedSize(false, "none");
        task.setNoUsedOid(true);
        task.setNoUsedOid(false);
        task.setNoUsedStatistics(true);
        task.setNoUsedStatistics(false);
        task.setNoUsedLog(true);
        task.setNoUsedLog(false);
        task.setSchemaPath("C:\\CUBRID\\DATABA~1\\demodb/demodb_schema");
        task.setObjectPath("C:\\CUBRID\\DATABA~1\\demodb/demodb_objects");
        task.setIndexPath("none");
        assertEquals(msg, task.getRequest());

        // exception case
        task.setCheckOption(null);
        task.setCheckOption("laod1");
        task.setUsedEstimatedSize(true, "100");
        task.setNoUsedOid(true);
        task.setNoUsedLog(true);
        task.setUsedErrorContorlFile(true, "error");
        task.setUsedErrorContorlFile(false, "error");
        task.setUsedIgnoredClassFile(true, "error");
        task.setUsedIgnoredClassFile(false, "error");
        task.setUsedIgnoredClassFile(true, "error");
        task.setUsedIgnoredClassFile(false, "error");
        assertFalse(msg.equals(task.getRequest()));

        task.setCheckOption("load");
        task.setCheckOption("syntax");
    }

    public void testReceive() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;
        // case 1
        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/database/task/test.message/loaddb_receive");
        String msg = Tool.getFileContent(filepath);
        TreeNode node = MessageUtil.parseResponse(msg);
        LoadDbTask task = new LoadDbTask(serverInfo);
        task.setResponse(node);
        assertTrue(task.getLoadResult().length > 0);
        // exception case1
        task.setResponse(null);
        assertTrue(task.getLoadResult() == null);
        // exception case2
        task.setResponse(node);
        task.setErrorMsg("has error");
        assertTrue(task.getLoadResult() == null);
    }

    public void testSuccess() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.loaddb.001.req.txt>");

        LoadDbTask task = new LoadDbTask(serverInfo);
        task.setDbName("demodb");
        task.setCheckOption("both");
        task.setUsedPeriod(false, "none");
        task.setDbUser("dba");
        task.setUsedEstimatedSize(false, "none");
        task.setNoUsedOid(false);
        task.setNoUsedLog(false);
        task.setSchemaPath("/opt/frameworks/cubrid2/databases/demodb/demodb_schema");
        task.setObjectPath("/opt/frameworks/cubrid2/databases/demodb/demodb_objects");
        task.setIndexPath("none");
        task.setUsedErrorContorlFile(false, "none");
        task.setUsedIgnoredClassFile(false, "none");

        task.execute();

        assertTrue(task.isSuccess());
        assertNull(task.getErrorMsg());
        assertNotNull(task.getLoadResult());
        assertEquals(9, task.getLoadResult().length);
        assertEquals(
                "Schema loading from /opt/frameworks/cubrid2/databases/demodb/demodb_schema finished.",
                task.getLoadResult()[3]);
        assertEquals("Total    19215 objects inserted.", task.getLoadResult()[8]);
    }

    public void testActiveDb() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.loaddb.002.req.txt>");

        LoadDbTask task = new LoadDbTask(serverInfo);
        task.setDbName("activedb");
        task.setCheckOption("both");
        task.setUsedPeriod(false, "none");
        task.setDbUser("dba");
        task.setUsedEstimatedSize(false, "none");
        task.setNoUsedOid(false);
        task.setNoUsedLog(false);
        task.setSchemaPath("/opt/frameworks/cubrid2/databases/activedb/activedb_schema");
        task.setObjectPath("/opt/frameworks/cubrid2/databases/activedb/activedb_objects");
        task.setIndexPath("none");
        task.setUsedErrorContorlFile(false, "none");
        task.setUsedIgnoredClassFile(false, "none");

        task.execute();

        assertTrue(task.isSuccess());
        assertNull(task.getErrorMsg());
        assertNotNull(task.getLoadResult());
        assertEquals(7, task.getLoadResult().length);
        assertEquals("ERROR: Serial \"event_no\" already exist.", task.getLoadResult()[2]);
        assertEquals("Error occurred during schema loading.", task.getLoadResult()[4]);
    }

    public void testWithPeriodAndEstOpt() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.loaddb.003.req.txt>");

        LoadDbTask task = new LoadDbTask(serverInfo);
        task.setDbName("demodb");
        task.setCheckOption("both");
        task.setUsedPeriod(true, "10000");
        task.setDbUser("dba");
        task.setUsedEstimatedSize(true, "5000");
        task.setNoUsedOid(false);
        task.setNoUsedLog(false);
        task.setSchemaPath("/opt/frameworks/cubrid2/databases/demodb/demodb_schema");
        task.setObjectPath("/opt/frameworks/cubrid2/databases/demodb/demodb_objects");
        task.setIndexPath("none");
        task.setUsedErrorContorlFile(false, "none");
        task.setUsedIgnoredClassFile(false, "none");

        task.execute();

        assertTrue(task.isSuccess());
        assertNull(task.getErrorMsg());
        assertNotNull(task.getLoadResult());
        assertEquals(8, task.getLoadResult().length);
        assertEquals("ERROR: Serial \"event_no\" already exist.", task.getLoadResult()[2]);
        assertEquals("Error occurred during schema loading.", task.getLoadResult()[4]);
    }
}
