package com.cubrid.cubridmanager.core.cubrid.database.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import com.cubrid.cubridmanager.core.cubrid.database.model.DbUnloadInfo;
import java.util.List;

public class GetDbUnloadInfoTaskTest extends SetupEnvTestCase {

    public void testSend() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/database/task/test.message/getunloaddbinfo_send");
        String msg = Tool.getFileContent(filepath);

        // replace "token" field with the latest value
        msg = msg.replaceFirst("token:.*\n", "token:" + token + "\n");
        // composite message
        GetDbUnloadInfoTask task = new GetDbUnloadInfoTask(serverInfo);
        // compare
        assertEquals(msg, task.getRequest());
    }

    public void testReceive() throws Exception {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;
        // case 1
        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/database/task/test.message/getunloaddbinfo_receive");
        String msg = Tool.getFileContent(filepath);
        TreeNode node = MessageUtil.parseResponse(msg);
        GetDbUnloadInfoTask task = new GetDbUnloadInfoTask(serverInfo);
        task.setResponse(node);
        List<DbUnloadInfo> list = task.getDbUnloadInfoList();
        assertTrue(list.size() == 1);
        assertEquals(list.get(0).getDbName(), "demodb");
        assertTrue(list.get(0).getIndexDateList().size() == 2);
        assertTrue(list.get(0).getIndexPathList().size() == 2);
        assertTrue(list.get(0).getSchemaDateList().size() == 2);
        assertTrue(list.get(0).getSchemaPathList().size() == 2);
        assertTrue(list.get(0).getObjectDateList().size() == 2);
        assertTrue(list.get(0).getObjectPathList().size() == 2);
        assertTrue(list.get(0).getTriggerDateList().size() == 2);
        assertTrue(list.get(0).getTriggerPathList().size() == 2);
        // exception case
        // exception case1
        task.setResponse(null);
        assertTrue(task.getDbUnloadInfoList() == null);
        // exception case2
        task.setResponse(node);
        task.setErrorMsg("has error");
        assertTrue(task.getDbUnloadInfoList() == null);

        filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/database/task/test.message/getunloaddbinfo_receive2");
        msg = Tool.getFileContent(filepath);
        node = MessageUtil.parseResponse(msg);
        GetDbUnloadInfoTask task2 = new GetDbUnloadInfoTask(serverInfo);
        task2.setResponse(node);
        task2.getDbUnloadInfoList();

        filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/database/task/test.message/getunloaddbinfo_receive2");
        msg = Tool.getFileContent(filepath);
        node = MessageUtil.parseResponse(msg);
        GetDbUnloadInfoTask task3 = new GetDbUnloadInfoTask(serverInfo);
        task3.setResponse(node);
        task3.getDbUnloadInfoList();
    }

    public void testUnloadInfo() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.unloadinfo.001.req.txt>");

        GetDbUnloadInfoTask task = new GetDbUnloadInfoTask(serverInfo);
        task.execute();

        assertTrue(task.isSuccess());
        assertNull(task.getErrorMsg());

        List<DbUnloadInfo> list = task.getDbUnloadInfoList();
        DbUnloadInfo info = list.get(0);
        assertEquals("demodb", info.getDbName());
        assertEquals(0, info.getIndexDateList().size());
        assertEquals(0, info.getIndexPathList().size());
        assertEquals("2009.07.05 10:57", info.getObjectDateList().get(0));
        assertEquals(
                "/opt/frameworks/cubrid/databases/demodb/demodb_objects",
                info.getObjectPathList().get(0));
        assertEquals("2009.07.05 10:57", info.getSchemaDateList().get(0));
        assertEquals(
                "/opt/frameworks/cubrid/databases/demodb/demodb_schema",
                info.getSchemaPathList().get(0));
        assertEquals(0, info.getTriggerDateList().size());
        assertEquals(0, info.getTriggerPathList().size());

        info = list.get(1);
        assertEquals("ndb", info.getDbName());
        assertEquals("2009.07.19 13:58", info.getIndexDateList().get(0));
        assertEquals(
                "/opt/frameworks/cubrid/databases/ndb/ndb_indexes", info.getIndexPathList().get(0));
        assertEquals("2009.07.19 13:58", info.getObjectDateList().get(0));
        assertEquals(
                "/opt/frameworks/cubrid/databases/ndb/ndb_objects",
                info.getObjectPathList().get(0));
        assertEquals("2009.07.19 13:58", info.getSchemaDateList().get(0));
        assertEquals(
                "/opt/frameworks/cubrid/databases/ndb/ndb_schema", info.getSchemaPathList().get(0));
        assertEquals(0, info.getTriggerDateList().size());
        assertEquals(0, info.getTriggerPathList().size());
    }
}
