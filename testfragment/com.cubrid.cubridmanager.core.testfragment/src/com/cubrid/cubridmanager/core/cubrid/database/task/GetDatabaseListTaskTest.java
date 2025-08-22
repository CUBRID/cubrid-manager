package com.cubrid.cubridmanager.core.cubrid.database.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.util.List;

public class GetDatabaseListTaskTest extends SetupEnvTestCase {

    public void testCommon() {

        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "n")) return;

        System.out.println("<database.startinfo.001.req.txt>");

        GetDatabaseListTask task = new GetDatabaseListTask(serverInfo);
        task.execute();

        assertTrue(task.isSuccess());

        List<DatabaseInfo> databaseInfoList = task.loadDatabaseInfo();
        assertNotNull(databaseInfoList);

        assertEquals("ndb", databaseInfoList.get(0).getDbName());
        assertEquals("/opt/frameworks/cubrid2/databases/ndb", databaseInfoList.get(0).getDbDir());
        assertEquals("demodb", databaseInfoList.get(1).getDbName());
        assertEquals(
                "/opt/frameworks/cubrid2/databases/demodb", databaseInfoList.get(1).getDbDir());
        assertEquals(DbRunningType.CS, databaseInfoList.get(1).getRunningType());
    }

    public void testData() {
        if (StringUtil.isEqual(SystemParameter.getParameterValue("useMockTest"), "y")) return;

        GetDatabaseListTask task = new GetDatabaseListTask(serverInfo);
        task.execute();
        task.setErrorMsg("err");
        task.loadDatabaseInfo();
        task.setResponse(null);
        task.loadDatabaseInfo();
    }
}
