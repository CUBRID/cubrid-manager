package com.cubrid.cubridmanager.core.cubrid.user;

import com.cubrid.cubridmanager.core.SetupJDBCTestCase;
import com.cubrid.cubridmanager.core.common.task.CommonQueryTask;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.common.task.CommonTaskName;
import com.cubrid.cubridmanager.core.common.task.CommonUpdateTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.UserSendObj;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfoList;
import com.cubrid.cubridmanager.core.cubrid.user.task.UpdateAddUserTask;
import java.util.List;

public class GetUserInfoTaskTest extends SetupJDBCTestCase {

    public void testUpdateMessage() {
        // test get user list
        final CommonQueryTask<DbUserInfoList> userTask =
                new CommonQueryTask<DbUserInfoList>(
                        serverInfo930,
                        CommonSendMsg.getCommonDatabaseSendMsg(),
                        new DbUserInfoList());
        userTask.setDbName(databaseInfo930.getDbName());
        userTask.execute();
        assertEquals(null, userTask.getErrorMsg());
        assertEquals(true, userTask.getResultModel() != null);

        List<DbUserInfo> userListInfo = userTask.getResultModel().getUserList();

        String newUserName = getUserName(userListInfo, "a");

        // test add user
        UpdateAddUserTask task = new UpdateAddUserTask(serverInfo930, true);
        UserSendObj userSendObj = new UserSendObj();
        userSendObj.setDbname(databaseInfo930.getDbName());

        userSendObj.setUsername(newUserName);
        userSendObj.setUserpass("123456");
        userSendObj.addGroups("public");
        task.setUserSendObj(userSendObj);
        task.execute();
        task.getUserSendObj();
        task.isSuccess();
        task.setUserName("dba");
        task.setDbName(testDbName930);

        assertEquals(null, task.getErrorMsg());

        // test edit user
        task = new UpdateAddUserTask(serverInfo930, false);
        userSendObj = new UserSendObj();
        userSendObj.setDbname(databaseInfo930.getDbName());

        userSendObj.setUsername(newUserName);
        userSendObj.setUserpass("223456");
        userSendObj.addGroups("public");
        task.setUserSendObj(userSendObj);
        task.execute();

        assertEquals(null, task.getErrorMsg());
        // test delete user
        CommonUpdateTask commonTask =
                new CommonUpdateTask(
                        CommonTaskName.DELETE_USER_TASK_NAME,
                        serverInfo930,
                        CommonSendMsg.getDeleteUserMSGItems());
        commonTask.setDbName(databaseInfo930.getDbName());
        commonTask.setUserName(newUserName);
        commonTask.execute();
        assertEquals(null, commonTask.getErrorMsg());
    }

    public String getUserName(List<DbUserInfo> userListInfo, String userName) {
        for (DbUserInfo u : userListInfo) {
            if (u.getName().equalsIgnoreCase(userName)) {
                return getUserName(userListInfo, userName + "a");
            }
        }
        return userName;
    }
}
