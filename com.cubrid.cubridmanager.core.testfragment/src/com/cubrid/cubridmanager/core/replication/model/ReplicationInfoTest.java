package com.cubrid.cubridmanager.core.replication.model;

import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

public class ReplicationInfoTest extends TestCase {

    public void testReplicationInfo() {
        ReplicationInfo replicationInfo = new ReplicationInfo();
        List<MasterInfo> masterList = new ArrayList<MasterInfo>();
        MasterInfo masterInfo = new MasterInfo();
        String masterDbName = "mdb";
        masterInfo.setMasterDbName(masterDbName);
        masterList.add(masterInfo);

        List<SlaveInfo> slaveList = new ArrayList<SlaveInfo>();
        SlaveInfo slaveInfo = new SlaveInfo();
        String sdbName = "sdb";
        slaveInfo.setSlaveDbName(sdbName);
        slaveList.add(slaveInfo);

        DistributorInfo distInfo = new DistributorInfo();
        String agentPort = "7777";
        distInfo.setAgentPort(agentPort);

        replicationInfo.setMasterList(masterList);
        replicationInfo.setSlaveList(slaveList);
        replicationInfo.setDistInfo(distInfo);

        assertEquals(replicationInfo.getMasterList().get(0).getMasterDbName(), masterDbName);
        assertEquals(replicationInfo.getSlaveList().get(0).getSlaveDbName(), sdbName);
        assertEquals(replicationInfo.getDistInfo().getAgentPort(), agentPort);

        replicationInfo.addMasterInfo(masterInfo);
        replicationInfo.setMasterList(null);
        replicationInfo.addMasterInfo(masterInfo);
        replicationInfo.addSlaveInfo(slaveInfo);
        replicationInfo.setSlaveList(null);
        replicationInfo.addSlaveInfo(slaveInfo);
    }
}
