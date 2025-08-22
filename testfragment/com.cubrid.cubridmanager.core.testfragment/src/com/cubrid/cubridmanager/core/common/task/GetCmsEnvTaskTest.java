package com.cubrid.cubridmanager.core.common.task;

import com.cubrid.cubridmanager.core.SetupEnvTestCase;

public class GetCmsEnvTaskTest extends SetupEnvTestCase {
    public void testGetVersion() {
        GetCmsEnvTask task = new GetCmsEnvTask(null);
        assertNull(task.getVersion());

        task = new GetCmsEnvTask(null);
        task.execute();
        assertNull(task.getVersion());

        task = new GetCmsEnvTask(serverInfo);
        assertNull(task.getVersion());

        task = new GetCmsEnvTask(serverInfo);
        task.execute();
        assertTrue(task.getVersion().indexOf("8.4.0") != -1);

        task = new GetCmsEnvTask(serverInfo);
        task.execute();
        task.setErrorMsg("Error msg");
        assertNull(task.getVersion());
    }
}
