package com.cubrid.cubridmanager.core.cubrid.database.task;

import com.cubrid.cubridmanager.core.SetupEnvTestCase;

public class GetDbSizeInfoTaskTest extends SetupEnvTestCase {

    public void testSendMessage() {

        GetDbSizeTask task = new GetDbSizeTask(serverInfo);
        task.setUsingSpecialDelimiter(false);
        task.setDbName(testDbName);
        task.execute();
        int dbSize = task.getDbSize();
        assertTrue(dbSize != 0);
        // exception case
        task = new GetDbSizeTask(serverInfo);
        dbSize = task.getDbSize();
        assertTrue(dbSize == -1);
    }
}
