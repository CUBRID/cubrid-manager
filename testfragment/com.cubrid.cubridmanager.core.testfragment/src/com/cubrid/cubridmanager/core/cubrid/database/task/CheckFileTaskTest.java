package com.cubrid.cubridmanager.core.cubrid.database.task;

import com.cubrid.cubridmanager.core.SetupEnvTestCase;

public class CheckFileTaskTest extends SetupEnvTestCase {

    public void testCheckDirBulkDirs() {

        CheckFileTask task = new CheckFileTask(serverInfo);
        task.setUsingSpecialDelimiter(false);
        String[] dirs = new String[2];
        for (int i = 0; i < dirs.length; i++) {
            dirs[i] =
                    serverPath
                            + getPathSeparator()
                            + "databases"
                            + getPathSeparator()
                            + "notExistDir"
                            + i;
        }
        task.setFile(dirs);
        task.execute();
        assertTrue(task.getExistFiles() == null);

        // exception case
        task = new CheckFileTask(serverInfo);
        assertTrue(task.getExistFiles() == null);
    }
}
