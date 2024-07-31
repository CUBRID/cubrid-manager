package com.cubrid.cubridmanager.core.replication.model;

import junit.framework.TestCase;

public class DistributorInfoTest extends TestCase {

    public void testDistributorInfo() {
        DistributorInfo distributorInfo = new DistributorInfo();
        String distdbName = "distdb";
        String distdbPath = "/home/cubrid/database/distdb";
        String agentPort = "555";
        String trailLogPath = "/home/cubrid/database/traillogpath";
        String copyLogPath = "/home/cubrid/database/copylogpath";
        String errorLogPath = "/home/cubrid/database/errlogpath";
        String delayTimeLogSize = "200";
        boolean isRestartReplWhenError = true;
        boolean isStarted = false;
        distributorInfo.setDistDbName(distdbName);
        distributorInfo.setDistDbPath(distdbPath);
        distributorInfo.setAgentPort(agentPort);
        distributorInfo.setTrailLogPath(trailLogPath);
        distributorInfo.setCopyLogPath(copyLogPath);
        distributorInfo.setErrorLogPath(errorLogPath);
        distributorInfo.setDelayTimeLogSize(delayTimeLogSize);
        distributorInfo.setRestartReplWhenError(isRestartReplWhenError);
        distributorInfo.setAgentActive(isStarted);

        assertEquals(distributorInfo.getDistDbName(), distdbName);
        assertEquals(distributorInfo.getDistDbPath(), distdbPath);
        assertEquals(distributorInfo.getAgentPort(), agentPort);
        assertEquals(distributorInfo.getTrailLogPath(), trailLogPath);
        assertEquals(distributorInfo.getCopyLogPath(), copyLogPath);
        assertEquals(distributorInfo.getErrorLogPath(), errorLogPath);
        assertEquals(distributorInfo.getDelayTimeLogSize(), delayTimeLogSize);
        assertEquals(distributorInfo.isRestartReplWhenError(), isRestartReplWhenError);
        assertEquals(distributorInfo.isAgentActive(), isStarted);
    }
}
