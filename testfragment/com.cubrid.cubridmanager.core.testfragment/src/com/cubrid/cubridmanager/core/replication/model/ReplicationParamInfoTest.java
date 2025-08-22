package com.cubrid.cubridmanager.core.replication.model;

import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;

public class ReplicationParamInfoTest extends TestCase {

    public void testExecute() {
        ReplicationParamInfo replicationParamInfo = new ReplicationParamInfo();

        Map<String, String> paramMap = new HashMap<String, String>();
        replicationParamInfo.setParamMap(paramMap);

        replicationParamInfo.setParamValue(ReplicationParamConstants.PERF_POLL_INTERVAL, "30");
        replicationParamInfo.setParamValue(ReplicationParamConstants.SIZE_OF_LOG_BUFFER, "200");
        replicationParamInfo.setParamValue(ReplicationParamConstants.SIZE_OF_CACHE_BUFFER, "300");
        replicationParamInfo.setParamValue(ReplicationParamConstants.SIZE_OF_COPYLOG, "2000");
        replicationParamInfo.setParamValue(ReplicationParamConstants.INDEX_REPLICATION, "Y");
        replicationParamInfo.setParamValue(ReplicationParamConstants.FOR_RECOVERY, "Y");
        replicationParamInfo.setParamValue(ReplicationParamConstants.LOG_APPLY_INTERVAL, "40");
        replicationParamInfo.setParamValue(ReplicationParamConstants.RESTART_INTERVAL, "50");

        assertEquals(
                replicationParamInfo.getParamValue(ReplicationParamConstants.PERF_POLL_INTERVAL),
                "30");
        assertEquals(
                replicationParamInfo.getParamValue(ReplicationParamConstants.SIZE_OF_LOG_BUFFER),
                "200");

        assertEquals(
                replicationParamInfo.getParamValue(ReplicationParamConstants.SIZE_OF_CACHE_BUFFER),
                "300");
        assertEquals(
                replicationParamInfo.getParamValue(ReplicationParamConstants.SIZE_OF_COPYLOG),
                "2000");

        assertEquals(
                replicationParamInfo.getParamValue(ReplicationParamConstants.INDEX_REPLICATION),
                "Y");
        assertEquals(
                replicationParamInfo.getParamValue(ReplicationParamConstants.FOR_RECOVERY), "Y");

        assertEquals(
                replicationParamInfo.getParamValue(ReplicationParamConstants.LOG_APPLY_INTERVAL),
                "40");
        assertEquals(
                replicationParamInfo.getParamValue(ReplicationParamConstants.RESTART_INTERVAL),
                "50");

        assertEquals(
                replicationParamInfo.getParamMap().get(ReplicationParamConstants.RESTART_INTERVAL),
                "50");

        assertEquals(ReplicationParamConstants.getReplicationParameters().length, 8);
        replicationParamInfo.getParamValue(null);
        replicationParamInfo.setParamValue(null, null);
    }
}
