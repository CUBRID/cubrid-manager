package com.cubrid.cubridmanager.core.replication.model;

import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

public class MasterInfoTest extends TestCase {

    public void testMasterInfo() {
        MasterInfo masterInfo = new MasterInfo();
        String masterIp = "192.168.1.221";
        String masterDbName = "masterdb";
        String replServerPort = "666";
        boolean isReplAllTable = true;
        List<String> replTableList = new ArrayList<String>();
        replTableList.add("table1");
        replTableList.add("table2");

        masterInfo.setMasterIp(masterIp);
        masterInfo.setMasterDbName(masterDbName);
        masterInfo.setReplServerPort(replServerPort);
        masterInfo.setReplAllTable(isReplAllTable);
        masterInfo.setReplTableList(replTableList);

        assertEquals(masterInfo.getMasterIp(), masterIp);
        assertEquals(masterInfo.getMasterDbName(), masterDbName);
        assertEquals(masterInfo.getReplServerPort(), replServerPort);
        assertEquals(masterInfo.isReplAllTable(), isReplAllTable);
        assertEquals(masterInfo.getReplTableList().size(), replTableList.size());
    }
}
