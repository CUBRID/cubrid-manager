package com.cubrid.cubridmanager.core.replication.model;

import junit.framework.TestCase;

public class SlaveInfoTest extends TestCase {

    public void testSlaveInfo() {
        SlaveInfo slaveInfo = new SlaveInfo();
        String slaveDbName = "slaveDb";
        String slaveIP = "192.168.1.221";
        String slaveDbPath = "/home/cubrid/database/slavedb";
        String dbUser = "dba";
        String password = "123456";
        ReplicationParamInfo paramInfo = new ReplicationParamInfo();
        paramInfo.setParamValue(ReplicationParamConstants.PERF_POLL_INTERVAL, "30");

        slaveInfo.setSlaveDbName(slaveDbName);
        slaveInfo.setSlaveIP(slaveIP);
        slaveInfo.setSlaveDbPath(slaveDbPath);
        slaveInfo.setDbUser(dbUser);
        slaveInfo.setPassword(password);
        slaveInfo.setParamInfo(paramInfo);

        assertEquals(slaveInfo.getSlaveDbName(), slaveDbName);
        assertEquals(slaveInfo.getSlaveIP(), slaveIP);
        assertEquals(slaveInfo.getSlaveDbPath(), slaveDbPath);
        assertEquals(slaveInfo.getDbUser(), dbUser);
        assertEquals(slaveInfo.getPassword(), password);
        assertEquals(
                slaveInfo
                        .getParamInfo()
                        .getParamValue(ReplicationParamConstants.PERF_POLL_INTERVAL),
                "30");
    }
}
