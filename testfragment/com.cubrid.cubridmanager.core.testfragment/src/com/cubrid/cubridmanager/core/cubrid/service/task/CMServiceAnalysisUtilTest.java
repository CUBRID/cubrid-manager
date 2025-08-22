package com.cubrid.cubridmanager.core.cubrid.service.task;

import com.cubrid.cubridmanager.core.cubrid.service.model.DbLocationInfo;
import com.cubrid.cubridmanager.core.cubrid.service.model.NodeType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;

public class CMServiceAnalysisUtilTest extends TestCase {
    public void testCMServiceAnalysisUtil() {
        // test isLocalHost()
        String host = "127.0.0.1";
        String host2 = "localhost";
        String host3 = "192.168.1.60";
        String host4 = "www.cubrid.org";
        assertTrue(CMServiceAnalysisUtil.isLocalHost(host));
        assertTrue(CMServiceAnalysisUtil.isLocalHost(host2));
        assertFalse(CMServiceAnalysisUtil.isLocalHost(host3));
        assertFalse(CMServiceAnalysisUtil.isLocalHost(host4));

        // test isIp()
        assertTrue(CMServiceAnalysisUtil.isIp("127.0.0.1"));
        assertTrue(CMServiceAnalysisUtil.isIp("192.168.1.60"));
        assertFalse(CMServiceAnalysisUtil.isIp("192.168.1.256"));
        assertFalse(CMServiceAnalysisUtil.isIp("www.cubrid.org"));

        // test addDbLocaltionInfos()
        List<DbLocationInfo> dbLocationInfoList = new ArrayList<DbLocationInfo>();
        Map<String, String> dbParamMap = new HashMap<String, String>();
        dbParamMap.put("db-name", "demodb");
        dbParamMap.put("vol-path", "/home1/demo/CUBRID/databases/demodb");
        dbParamMap.put("db-host", "dev-cub-ha-002.ncl:dev-cub-ha-004.ncl");
        dbParamMap.put("log-path", "/home1/demo/CUBRID/databases/demodb");
        dbParamMap.put("lob-base-path", "file:/home1/demo/CUBRID/databases/demodb/lob");

        List<Map<String, String>> dbParamMapList = new ArrayList<Map<String, String>>();
        dbParamMapList.add(dbParamMap);
        CMServiceAnalysisUtil.addDbLocaltionInfos(dbParamMapList, dbLocationInfoList);
        assertTrue(dbLocationInfoList.size() > 0);
        DbLocationInfo dbLocalInfo = dbLocationInfoList.get(0);
        assertNotNull(dbLocalInfo);
        assertEquals(dbLocalInfo.getDbName(), "demodb");

        // test convertHaStatToNodeType()
        NodeType type1 = CMServiceAnalysisUtil.convertHaStatToNodeType("master");
        NodeType type2 = CMServiceAnalysisUtil.convertHaStatToNodeType("slave");
        NodeType type3 = CMServiceAnalysisUtil.convertHaStatToNodeType("replica");
        assertEquals(type1, NodeType.MASTER);
        assertEquals(type2, NodeType.SLAVE);
        assertEquals(type3, NodeType.REPLICA);

        // test isAccessedByRemoteHost()
        assertTrue(CMServiceAnalysisUtil.isAccessedByRemoteHost(dbLocationInfoList));
    }
}
