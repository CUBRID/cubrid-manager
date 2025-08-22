package com.cubrid.cubridmanager.core.cubrid.sp.task;

import com.cubrid.cubridmanager.core.SetupJDBCTestCase;
import com.cubrid.cubridmanager.core.common.task.CommonSQLExcuterTask;
import com.cubrid.cubridmanager.core.cubrid.sp.model.SPType;

public class GetSPInfoListTaskTest extends SetupJDBCTestCase {

    public void testCommonSqlExcuteTask() {

        CommonSQLExcuterTask task1 = new CommonSQLExcuterTask(databaseInfo);
        task1.addSqls(
                "CREATE FUNCTION \"sssssss\"(\"sss\" CHAR) RETURN CHAR AS LANGUAGE JAVA NAME '111.d(java.sql.Timestamp) return java.lang.String'");
        task1.execute();
        assertEquals(null, task1.getErrorMsg());

        GetSPInfoListTask task = new GetSPInfoListTask(databaseInfo);
        task.setSpName("sssssss");
        task.setSpType(SPType.FUNCTION);
        task.execute();
        assertTrue(task.getSPInfoList().size() == 1);
        assertEquals(false, task.isCancel());

        task = new GetSPInfoListTask(databaseInfo);
        task.setSpType(SPType.FUNCTION);
        task.execute();
        assertTrue(task.getSPInfoList().size() == 1);
        assertEquals(false, task.isCancel());

        task = new GetSPInfoListTask(databaseInfo);
        task.execute();
        assertTrue(task.getSPInfoList().size() == 1);
        assertEquals(false, task.isCancel());

        task1 = new CommonSQLExcuterTask(databaseInfo);
        task1.addSqls(
                "CREATE PROCEDURE \"aaaa\"(\"a\" BIGINT,\"v\" OUT BIGINT,\"r\" INOUT BIGINT) AS LANGUAGE JAVA NAME 'a.a(java.lang.String,java.lang.String,java.lang.String)'");
        task1.execute();

        final GetSPInfoListTask task2 = new GetSPInfoListTask(databaseInfo);
        task2.setSpName("aaaa");
        task2.setSpType(SPType.PROCEDURE);
        task2.execute();

        task1 = new CommonSQLExcuterTask(databaseInfo);
        task1.addSqls("drop FUNCTION \"sssssss\"");
        task1.execute();
        assertEquals(null, task1.getErrorMsg());

        task1 = new CommonSQLExcuterTask(databaseInfo);
        task1.addSqls("drop PROCEDURE \"aaaa\"");
        task1.execute();

        task.setErrorMsg("err");
        task.execute();

        final GetSPInfoListTask task3 = new GetSPInfoListTask(databaseInfo);
        task3.cancel();
        task3.execute();
    }
}
