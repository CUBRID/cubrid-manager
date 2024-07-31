package com.cubrid.cubridmanager.core.common.model;

import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.jdbc.proxy.manage.ServerJdbcVersionMapping;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerInfoTest extends SetupEnvTestCase {
    public void testModelServerInfo() {

        ServerInfo bean = new ServerInfo();

        // test serverUserInfo
        ServerUserInfo serverUserInfo = new ServerUserInfo();
        serverUserInfo.setUserName("a");
        bean.addServerUserInfo(serverUserInfo);

        serverUserInfo = new ServerUserInfo();
        serverUserInfo.setUserName("b");
        bean.addServerUserInfo(serverUserInfo);

        assertTrue(bean.getServerUserInfo("a") != null);
        assertTrue(bean.getServerUserInfo("c") == null);

        bean.removeAllServerUserInfo();
        assertTrue(bean.getServerUserInfoList().size() == 0);

        ServerUserInfo userInfo = new ServerUserInfo();
        bean.setUserName(serverInfo.getLoginedUserInfo().getUserName());
        userInfo.setUserName(serverInfo.getLoginedUserInfo().getUserName());
        bean.addServerUserInfo(userInfo);
        assertTrue(bean.getLoginedUserInfo() != null);
        bean.removeServerUserInfo(userInfo);
        assertTrue(bean.getLoginedUserInfo() == null);

        // test basic information
        bean.setServerName("serverName");
        assertEquals(bean.getServerName(), "serverName");

        bean.setHostAddress("who am i");
        assertFalse(bean.isLocalServer());

        bean.setHostAddress("localhost");
        assertEquals(bean.getHostAddress(), "localhost");

        bean.setHostMonPort(11);
        assertEquals(bean.getHostMonPort(), 11);

        bean.setHostJSPort(10);
        assertEquals(bean.getHostJSPort(), 10);

        bean.setUserName("userName");
        assertEquals(bean.getUserName(), "userName");

        bean.setUserPassword("userPassword");
        assertEquals(bean.getUserPassword(), "userPassword");

        bean.setHostToken("hostToken");
        assertEquals(bean.getHostToken(), "hostToken");

        bean.setLoginedUserInfo(new ServerUserInfo());
        assertEquals(bean.getLoginedUserInfo() != null, true);

        bean.setServerUserInfoList(new ArrayList<ServerUserInfo>());
        assertEquals(bean.getServerUserInfoList().size(), 0);

        bean.setCubridConfParaMap(new HashMap<String, Map<String, String>>());
        assertEquals(bean.getCubridConfParaMap().size(), 0);

        bean.setBrokerConfParaMap(new HashMap<String, Map<String, String>>());
        assertEquals(bean.getBrokerConfParaMap().size(), 0);

        bean.setCmConfParaMap(new HashMap<String, String>());
        assertEquals(bean.getCmConfParaMap().size(), 0);

        bean.setBrokerInfos(null);
        assertEquals(bean.getBrokerInfos(), null);

        bean.setAllDatabaseList(null);
        assertEquals(bean.getAllDatabaseList(), null);

        //		assertTrue(ServerInfo.compareVersion("", null) == -2);
        //		assertTrue(ServerInfo.compareVersion("", "") == -2);
        //		assertTrue(ServerInfo.compareVersion("8.2.1", "8.2.1") == 0);

        assertFalse(bean.validateJdbcVersion("8.2.1"));
        bean.setConnected(true);
        assertFalse(bean.validateJdbcVersion("8.2.1"));
        bean.setJdbcDriverVersion(ServerJdbcVersionMapping.JDBC_SELF_ADAPTING_VERSION);
        assertTrue(
                ServerJdbcVersionMapping.JDBC_SELF_ADAPTING_VERSION.equals(
                        bean.getJdbcDriverVersion()));
        bean.setConnected(false);
        // test envInfo
        assertTrue(bean.isSupportReplication() == 4);
        assertTrue(bean.getServerOsInfo() == null);
        EnvInfo envInfo = new EnvInfo();
        envInfo.setOsInfo("NT");
        bean.setEnvInfo(envInfo);
        assertEquals(bean.getEnvInfo() == null, false);
        assertEquals(bean.getPathSeparator(), "\\");
        assertTrue(bean.isSupportReplication() == 1);

        envInfo.setOsInfo("UNIX");
        assertEquals(bean.getPathSeparator(), "/");

        envInfo.setOsInfo("LINUX");
        bean.setServerType(ServerType.BROKER);
        assertEquals(bean.getPathSeparator(), "/");
        assertTrue(bean.isSupportReplication() == 2);
        bean.setServerType(ServerType.BOTH);
        envInfo.setServerVersion("8.2.0.0203");
        assertTrue(bean.isSupportReplication() > 0);
        envInfo.setServerVersion("8.1.0.0203");
        assertTrue(bean.isSupportReplication() > 0);
        // test connected and disconnected
        assertFalse(bean.isConnected());
        bean.setConnected(true);
        assertTrue(bean.isConnected());
        bean.getMonitoringTask();
        bean.getMonitoringTask();
        bean.setConnected(false);
        bean.disConnect();

        assertTrue(bean.getLogInfoManager() != null);
        bean.setAllDatabaseList(new ArrayList<String>());
        bean.removeAllDatabase();

        bean.setHostAddress("localhost");
        assertTrue(bean.isLocalServer());

        bean.setHostAddress("192.168.1.222");
        assertFalse(bean.isLocalServer());

        bean.setConnected(true);
        envInfo.setServerVersion("CUBRID 2008 R2.1 (8.2.0.1147)");
        bean.setEnvInfo(envInfo);
        bean.setJdbcDriverVersion(ServerJdbcVersionMapping.JDBC_SELF_ADAPTING_VERSION);
        bean.getJdbcDriverVersion();
        bean.validateJdbcVersion(ServerJdbcVersionMapping.JDBC_SELF_ADAPTING_VERSION);
        // case 2
        serverInfo.isSupportReplication();
        serverInfo.isLocalServer();
        serverInfo.isConnected();
        serverInfo.getAllDatabaseList();
        serverInfo.getBrokerConfParaMap();
        serverInfo.getBrokerInfos();
        serverInfo.getCmConfParaMap();
        serverInfo.getEnvInfo();
        serverInfo.getCubridConfParaMap();
        serverInfo.getJdbcDriverVersion();
        serverInfo.getLoginedUserInfo();
        serverInfo.getLogInfoManager();
        // serverInfo.compareVersionKey("8.2.0.0987");
        serverInfo.isHAMode(null);
        serverInfo.isHAMode("demodb");
        serverInfo.isHAMode("woshishui");

        serverInfo.validateJdbcVersion("");
        serverInfo.validateJdbcVersion("CUBRID-JDBC-8.2.0.1147");
        serverInfo.getJdbcDriverVersion();

        serverInfo.isHAMode(null);
        serverInfo.isHAMode("my32db");
        assertTrue(serverInfo.getCubridConfPara("service", null) != null);
        serverInfo.getCubridConfPara("AUTO_RESTART_SERVER", "demodb");
        // get CUBRID conf parameter

        bean.setCmConfParaMap(serverInfo.getCmConfParaMap());
        bean.setCubridConfParaMap(serverInfo.getCubridConfParaMap());
        bean.getCubridConfPara("demodb", "demodb");
        bean.getCubridConfPara(ConfConstants.INTL_MBS_SUPPORT, testDbName);
        bean.getCubridConfPara("a", "b");

        bean.setServerUserInfoList(null);
        bean.removeAllServerUserInfo();

        bean.setEnvInfo(null);
        bean.getFullServerVersionKey();
        serverInfo.getFullServerVersionKey();
        ServerInfo.getAutoDetectJdbcVersion("8.3.1.1015");
        ServerInfo.getAutoDetectJdbcVersion("8.3.1.0027");
        ServerInfo.getAutoDetectJdbcVersion("8.4.1.0027");

        Map<String, Map<String, String>> haMap = new HashMap<String, Map<String, String>>();
        Map<String, String> commonMap = new HashMap<String, String>();
        commonMap.put(ConfConstants.HA_MODE, "on");
        haMap.put(ConfConstants.COMMON_SECTION, commonMap);
        bean.setHaConfParaMap(haMap);
        assertEquals(
                bean.getHaConfParaMap()
                        .get(ConfConstants.COMMON_SECTION)
                        .get(ConfConstants.HA_MODE),
                "on");
    }
}
