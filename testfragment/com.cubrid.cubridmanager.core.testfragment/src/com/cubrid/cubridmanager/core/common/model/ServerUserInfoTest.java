package com.cubrid.cubridmanager.core.common.model;

import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DbCreateAuthType;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

public class ServerUserInfoTest extends TestCase {

    public void testServerUserInfo() {
        // case 1
        ServerUserInfo serverUserInfo = new ServerUserInfo();
        serverUserInfo.setCasAuth(CasAuthType.AUTH_ADMIN);
        serverUserInfo.setDbCreateAuthType(DbCreateAuthType.AUTH_NONE);
        serverUserInfo.setPassword("1111");
        serverUserInfo.setStatusMonitorAuth(StatusMonitorAuthType.AUTH_MONITOR);
        serverUserInfo.setUserName("admin");
        List<DatabaseInfo> dbInfoList = new ArrayList<DatabaseInfo>();
        dbInfoList.add(new DatabaseInfo("pang", null));
        serverUserInfo.setDatabaseInfoList(dbInfoList);
        verify(serverUserInfo);

        // case 2
        serverUserInfo =
                new ServerUserInfo(
                        "admin",
                        "1111",
                        CasAuthType.AUTH_ADMIN,
                        DbCreateAuthType.AUTH_NONE,
                        StatusMonitorAuthType.AUTH_MONITOR);
        DatabaseInfo dbInfo = new DatabaseInfo("pang", null);
        serverUserInfo.getDatabaseInfo("pang");
        serverUserInfo.removeDatabaseInfo(dbInfo);
        serverUserInfo.removeAllDatabaseInfo();
        serverUserInfo.addDatabaseInfo(dbInfo);
        verify(serverUserInfo);

        serverUserInfo.getDatabaseInfo("pang");
        serverUserInfo.isAdmin();

        serverUserInfo.removeDatabaseInfo(dbInfo);
        assertTrue(serverUserInfo.getDatabaseInfo("pang") == null);

        serverUserInfo.addDatabaseInfo(dbInfo);
        serverUserInfo.removeAllDatabaseInfo();
        assertTrue(serverUserInfo.getDatabaseInfo("pang") == null);
    }

    public void verify(ServerUserInfo serverUserInfo) {
        assertTrue(serverUserInfo.getCasAuth() == CasAuthType.AUTH_ADMIN);
        assertTrue(serverUserInfo.getDbCreateAuthType() == DbCreateAuthType.AUTH_NONE);
        assertTrue(serverUserInfo.getStatusMonitorAuth() == StatusMonitorAuthType.AUTH_MONITOR);
        assertEquals(serverUserInfo.getUserName(), "admin");
        assertEquals(serverUserInfo.getPassword(), "1111");
        assertTrue(
                serverUserInfo.getDatabaseInfoList().size() == 1
                        && serverUserInfo.getDatabaseInfoList().get(0).getDbName().equals("pang"));
        serverUserInfo.setUserName(null);
        assertFalse(serverUserInfo.isAdmin());
        DatabaseInfo dbInfo = new DatabaseInfo("pang2", null);
        serverUserInfo.addDatabaseInfo(dbInfo);
        serverUserInfo.getDatabaseInfo("pang2");
        serverUserInfo.removeDatabaseInfo(dbInfo);
        serverUserInfo.addDatabaseInfo(dbInfo);
        serverUserInfo.removeAllDatabaseInfo();
    }
}
