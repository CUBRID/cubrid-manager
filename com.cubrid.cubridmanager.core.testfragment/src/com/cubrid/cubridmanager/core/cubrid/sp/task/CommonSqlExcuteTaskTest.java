package com.cubrid.cubridmanager.core.cubrid.sp.task;

import com.cubrid.cubridmanager.core.SetupJDBCTestCase;
import com.cubrid.cubridmanager.core.common.task.CommonSQLExcuterTask;
import com.cubrid.cubridmanager.core.cubrid.table.model.ClassInfo;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetAllClassListTask;

public class CommonSqlExcuteTaskTest extends SetupJDBCTestCase {

    public void testCommonSqlExcuteTask() {

        String tableName = "assdfafa";
        GetAllClassListTask classTask = new GetAllClassListTask(databaseInfo);
        classTask.setTableName(tableName);
        classTask.getClassInfoTaskExcute();
        ClassInfo classInfo = classTask.getClassInfo();

        CommonSQLExcuterTask task1 = new CommonSQLExcuterTask(databaseInfo);
        if (classInfo == null) {
            task1.addSqls("create table " + tableName);
            task1.addCallSqls(
                    "call change_owner ('" + tableName + "','public') on class db_authorizations");
        } else {
            task1.addCallSqls(
                    "call change_owner ('" + tableName + "','public') on class db_authorizations");
        }
        task1.addSqls("create table " + "tableName");
        task1.addCallSqls(
                "call change_owner ('" + "tableName" + "','public') on class db_authorizations");
        task1.execute();
        // assertEquals(null, task1.getErrorMsg());

        task1 = new CommonSQLExcuterTask(databaseInfo);
        task1.addSqls("drop table assdfafa");
        task1.addSqls("drop table tableName");
        task1.execute();
        System.out.println(task1.getErrorMsg());
        assertEquals(null, task1.getErrorMsg());
        task1.setErrorMsg("err");
        task1.execute();
        final CommonSQLExcuterTask task3 = new CommonSQLExcuterTask(databaseInfo);
        task3.cancel();
        task3.execute();
    }
}
