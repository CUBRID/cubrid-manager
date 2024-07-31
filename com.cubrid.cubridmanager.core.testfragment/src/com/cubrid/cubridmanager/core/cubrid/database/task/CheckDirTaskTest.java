package com.cubrid.cubridmanager.core.cubrid.database.task;

import com.cubrid.cubridmanager.core.JsonObjectUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;

public class CheckDirTaskTest extends SetupEnvTestCase {

    public void testCheckDirNotExists() {

        CheckDirTask task = new CheckDirTask(serverInfo);

        task.setUsingSpecialDelimiter(false);
        // task:checkdir
        // token:8ec1ab8a91333c78c9e6cdb7dd8bf452e103ddfeface7072c47a07a0b1f66f6f7926f07dd201b6aa
        // dir:C:\CUBRID\databases\notExistDir1
        // dir:C:\CUBRID\databases\notExistDir2
        //
        // task:checkdir
        // status:success
        // note:none
        // noexist:C:\MyDev\tools\CUBRID\databases\notExistDir1
        // noexist:C:\MyDev\tools\CUBRID\databases\notExistDir2
        task.setDirectory(
                new String[] {
                    serverPath
                            + getPathSeparator()
                            + "databases"
                            + getPathSeparator()
                            + "notExistDir1",
                    serverPath
                            + getPathSeparator()
                            + "databases"
                            + getPathSeparator()
                            + "notExistDir2"
                });
        task.execute();

        assertEquals(null, task.getErrorMsg());
        System.out.println("task.getErrorMsg()=" + task.getErrorMsg());

        String[] noExistArray = task.getNoExistDirectory();
        assertEquals(noExistArray.length, 2);
        System.out.println(
                "task.getNoExistDirectory()=" + JsonObjectUtil.object2json(noExistArray));

        assertTrue(task.isSuccess());
        System.out.println("task.isSuccess()=" + task.isSuccess());

        // exception case
        task = new CheckDirTask(serverInfo);
        assertTrue(task.getNoExistDirectory() == null);
    }

    public void testCheckDirExists() {

        CheckDirTask task = new CheckDirTask(serverInfo);

        task.setUsingSpecialDelimiter(false);
        // task:checkdir
        // token:8ec1ab8a91333c78c9e6cdb7dd8bf452e103ddfeface7072c47a07a0b1f66f6f7926f07dd201b6aa
        // dir:C:\CUBRID\databases
        // dir:C:\CUBRID\databases\demodb
        //
        // task:checkdir
        // status:success
        // note:none
        task.setDirectory(
                new String[] {
                    serverPath + getPathSeparator() + "databases",
                    serverPath
                            + getPathSeparator()
                            + "databases"
                            + getPathSeparator()
                            + "demodb11111"
                });
        task.execute();

        System.out.println("task.getErrorMsg()=" + task.getErrorMsg());
        assertEquals(null, task.getErrorMsg());

        String[] noExistArray = task.getNoExistDirectory();
        System.out.println(
                "task.getNoExistDirectory()=" + JsonObjectUtil.object2json(noExistArray));
        assertEquals(noExistArray.length > 0, true);

        System.out.println("task.isSuccess()=" + task.isSuccess());
        assertTrue(task.isSuccess());
    }

    public void testCheckDirHalf() {

        CheckDirTask task = new CheckDirTask(serverInfo);

        task.setUsingSpecialDelimiter(false);
        // task:checkdir
        // token:8ec1ab8a91333c78c9e6cdb7dd8bf452e103ddfeface7072c47a07a0b1f66f6f7926f07dd201b6aa
        // dir:C:\CUBRID\databases\demodb
        // dir:C:\CUBRID\databases\notExistDir2
        //
        // task:checkdir
        // status:success
        // note:none

        task.setDirectory(new String[] {databaseInfo.getDbDir()});
        task.execute();

        System.out.println("task.getErrorMsg()=" + task.getErrorMsg());
        assertEquals(null, task.getErrorMsg());

        String[] noExistArray = task.getNoExistDirectory();
        System.out.println(
                "task.getNoExistDirectory()=" + JsonObjectUtil.object2json(noExistArray));
        assertTrue(noExistArray == null);

        System.out.println("task.isSuccess()=" + task.isSuccess());
        assertTrue(task.isSuccess());
    }

    public void testCheckDirUnacceptableDirectory() {

        CheckDirTask task = new CheckDirTask(serverInfo);

        task.setUsingSpecialDelimiter(false);

        task.setDirectory(
                new String[] {
                    serverPath + getPathSeparator() + "databases\\?",
                    serverPath + getPathSeparator() + "databases" + getPathSeparator() + "*",
                    serverPath
                            + getPathSeparator()
                            + "databases"
                            + getPathSeparator()
                            + " both_space "
                });
        task.execute();

        System.out.println("task.getErrorMsg()=" + task.getErrorMsg());
        assertEquals(null, task.getErrorMsg());

        String[] noExistArray = task.getNoExistDirectory();
        System.out.println(
                "task.getNoExistDirectory()=" + JsonObjectUtil.object2json(noExistArray));
        assertEquals(noExistArray.length, 3);

        System.out.println("task.isSuccess()=" + task.isSuccess());
        assertTrue(task.isSuccess());
    }

    public void testCheckDirBulkDirs() {

        CheckDirTask task = new CheckDirTask(serverInfo);

        task.setUsingSpecialDelimiter(false);

        String[] dirs = new String[1000];
        for (int i = 0; i < dirs.length; i++) {
            dirs[i] =
                    serverPath
                            + getPathSeparator()
                            + "databases"
                            + getPathSeparator()
                            + "notExistDir"
                            + i;
        }

        task.setDirectory(dirs);
        task.execute();

        System.out.println("task.getErrorMsg()=" + task.getErrorMsg());
        assertEquals(null, task.getErrorMsg());

        String[] noExistArray = task.getNoExistDirectory();
        System.out.println(
                "task.getNoExistDirectory()=" + JsonObjectUtil.object2json(noExistArray));
        assertEquals(noExistArray.length, dirs.length);

        System.out.println("task.isSuccess()=" + task.isSuccess());
        assertTrue(task.isSuccess());
    }
}
