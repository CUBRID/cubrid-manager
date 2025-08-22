package com.cubrid.cubridmanager.core.cubrid.database.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import java.util.List;

public class UnloadDatabaseTaskTest extends SetupEnvTestCase {

    public void testSend() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/database/task/test.message/unloaddb_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // case1
        UnloadDatabaseTask task = new UnloadDatabaseTask(serverInfo);
        task.setDbName("demodb");
        task.setUnloadDir("C:\\CUBRID\\DATABA~1\\demodb");
        task.setUsedHash(false, "none");
        task.setUnloadType("both");
        task.setClasses(new String[] {"athlete", "code", "event"});
        task.setIncludeRef(false);
        task.setClassOnly(true);
        task.setUsedDelimit(false);
        task.setUsedEstimate(false, "none");
        task.setUsedCache(false, "none");
        task.setUsedPrefix(false, "none");
        task.setUsedLoFile(false, "none");
        assertEquals(msg, task.getRequest());
        // case 2
        task.setUsedHash(true, "hash.txt");
        task.setIncludeRef(true);
        task.setClassOnly(false);
        task.setUsedDelimit(true);
        task.setUsedEstimate(true, "100");
        task.setUsedCache(true, "100");
        task.setUsedPrefix(true, "prefix");
        task.setUsedLoFile(true, "lofile.txt");
        assertFalse(msg.equals(task.getRequest()));
    }

    public void testReceive() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;
        // case 1
        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/database/task/test.message/unloaddb_receive");
        String msg = Tool.getFileContent(filepath);
        TreeNode node = MessageUtil.parseResponse(msg);
        UnloadDatabaseTask task = new UnloadDatabaseTask(serverInfo);
        task.setResponse(node);
        List<String> list = task.getUnloadDbResult();
        assertTrue(list.size() == 3);

        // exception case1
        task.setResponse(null);
        assertTrue(task.getUnloadDbResult() == null);
        // exception case2
        task.setResponse(node);
        task.setErrorMsg("has error");
        assertTrue(task.getUnloadDbResult() == null);
        task.setUnloadType("both");
        task.setUnloadType("schema");
        task.setUnloadType("object");
        task.setUnloadType("aaa");
        task.setErrorMsg("err");
        task.getUnloadDbResult();
    }

    public void testRealEnv() throws Exception {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")
                || !isConnectRealEnv) {
            return;
        }

        UnloadDatabaseTask task = new UnloadDatabaseTask(serverInfo);
        task.setDbName("demodb");
        task.setUnloadDir("C:\\CUBRID\\DATABA~1\\demodb");
        task.setUsedHash(false, "none");
        task.setUnloadType("both");
        task.setClasses(new String[] {"athlete", "code", "event"});
        task.setIncludeRef(false);
        task.setClassOnly(true);
        task.setUsedDelimit(false);
        task.setUsedEstimate(false, "none");
        task.setUsedCache(false, "none");
        task.setUsedPrefix(false, "none");
        task.setUsedLoFile(false, "none");
        task.execute();
        // compare
        assertTrue(task.isSuccess());
        task.getUnloadDbResult();
    }

    public void testExistDb() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.unloaddb.001.req.txt>");

        UnloadDatabaseTask task = new UnloadDatabaseTask(serverInfo);
        task.setDbName("demodb");
        task.setUnloadDir("/opt/frameworks/cubrid2/databases/demodb");
        task.setUsedHash(false, "none");
        task.setUnloadType("both");
        task.setClasses(new String[] {"athlete", "code", "event"});
        task.setIncludeRef(false);
        task.setClassOnly(true);
        task.setUsedDelimit(false);
        task.setUsedEstimate(false, "none");
        task.setUsedCache(false, "none");
        task.setUsedPrefix(false, "none");
        task.setUsedLoFile(false, "none");
        task.execute();

        assertTrue(task.isSuccess());
        assertNull(task.getErrorMsg());
        assertNotNull(task.getUnloadDbResult());

        List<String> list = task.getUnloadDbResult();
        assertEquals("code:7 (100%/0%)", list.get(0));
        assertEquals("event:422 (100%/2%)", list.get(1));
        assertEquals("athlete:6677 (100%/36%)", list.get(2));
    }

    public void testExistFullOptDb() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.unloaddb.002.req.txt>");

        UnloadDatabaseTask task = new UnloadDatabaseTask(serverInfo);
        task.setDbName("demodb");
        task.setUnloadDir("/opt/frameworks/cubrid2/databases/demodb");
        task.setUsedHash(true, "/opt/frameworks/cubrid2/databases/demodb/hashfile");
        task.setUnloadType("both");
        task.setClasses(null);
        task.setIncludeRef(true);
        task.setClassOnly(false);
        task.setUsedDelimit(true);
        task.setUsedEstimate(true, "10000");
        task.setUsedCache(true, "100");
        task.setUsedPrefix(true, "demodb");
        task.setUsedLoFile(true, "100");
        task.execute();

        assertTrue(task.isSuccess());
        assertNull(task.getErrorMsg());
        assertNotNull(task.getUnloadDbResult());

        List<String> list = task.getUnloadDbResult();
        assertEquals("stadium:141 (100%/0%)", list.get(0));
        assertEquals("code:7 (100%/0%)", list.get(1));
        assertEquals("nation:215 (100%/1%)", list.get(2));
        assertEquals(15, list.size());
    }

    public void testNotExistDb() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.unloaddb.003.req.txt>");

        UnloadDatabaseTask task = new UnloadDatabaseTask(serverInfo);
        task.setDbName("notexistdb");
        task.setUnloadDir("/opt/frameworks/cubrid2/databases/notexistdb");
        task.setUsedHash(false, "none");
        task.setUnloadType("schema");
        task.setClasses(new String[] {"athlete", "code", "event"});
        task.setIncludeRef(false);
        task.setClassOnly(true);
        task.setUsedDelimit(false);
        task.setUsedEstimate(false, "none");
        task.setUsedCache(false, "none");
        task.setUsedPrefix(false, "none");
        task.setUsedLoFile(false, "none");
        task.execute();

        assertFalse(task.isSuccess());
        assertNotNull(task.getErrorMsg());
        assertEquals(
                "unloaddb: Database \"notexistdb\" is unknown, or the file \"databases.txt\" cannot be accessed.",
                task.getErrorMsg());
        assertNull(task.getUnloadDbResult());
    }
}
