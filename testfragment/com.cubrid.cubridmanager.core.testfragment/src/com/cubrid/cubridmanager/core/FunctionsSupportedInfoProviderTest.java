/*
 * Copyright (C) 2008 Search Solution Corporation. All rights reserved by Search Solution.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the <ORGANIZATION> nor the names of its contributors
 *   may be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */
package com.cubrid.cubridmanager.core;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.FileUtil;
import com.cubrid.cubridmanager.core.common.model.EnvInfo;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;

/**
 * Test the type of FunctionsSupportedInfoProvider
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-12-22 created by lizhiqiang
 */
public class FunctionsSupportedInfoProviderTest extends SetupJDBCTestCase {

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#isSupportCMServer(com.cubrid.cubridmanager.core.common.model.ServerInfo,
     * java.lang.String)} .
     */
    public void testIsSupportCMServer() {
        EnvInfo envInfo = new EnvInfo();
        envInfo.setServerVersion("CUBRID 2008 R2.0(8.2.2.1150)");
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setConnected(true);
        serverInfo.setEnvInfo(envInfo);
        boolean support = CompatibleUtil.isSupportCMServer(serverInfo, "8.2.0");
        assertFalse(support);
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#isSupportLogPageSize(com.cubrid.cubridmanager.core.common.model.ServerInfo)}
     * .
     */
    public void testIsSupportLogPageSize() {
        EnvInfo envInfo = new EnvInfo();
        envInfo.setServerVersion("CUBRID 2008 R2.0(8.2.2.1150)");
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setConnected(true);
        serverInfo.setEnvInfo(envInfo);
        boolean support = CompatibleUtil.isSupportLogPageSize(serverInfo);
        assertTrue(support);
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#isSupportVerbose(com.cubrid.cubridmanager.core.common.model.ServerInfo)}
     * .
     */
    public void testIsSupportVerbose() {
        EnvInfo envInfo = new EnvInfo();
        envInfo.setServerVersion("CUBRID 2008 R2.0(8.2.0.1150)");
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setConnected(true);
        serverInfo.setEnvInfo(envInfo);
        boolean support = CompatibleUtil.isSupportVerbose(serverInfo);
        assertFalse(support);
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#isSupportNoUseStatistics(com.cubrid.cubridmanager.core.common.model.ServerInfo)}
     * .
     */
    public void testIsSupportNoUseStatistics() {
        EnvInfo envInfo = new EnvInfo();
        envInfo.setServerVersion("CUBRID 2008 R2.0(8.2.0.1150)");
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setConnected(true);
        serverInfo.setEnvInfo(envInfo);
        boolean support = CompatibleUtil.isSupportNoUseStatistics(serverInfo);
        assertFalse(support);
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#isSupportNewBrokerParamPropery1(com.cubrid.cubridmanager.core.common.model.ServerInfo)}
     * .
     */
    public void testIsSupportNewBrokerParamPropery1() {
        EnvInfo envInfo = new EnvInfo();
        envInfo.setServerVersion("CUBRID 2008 R2.0(8.2.0.1150)");
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setConnected(true);
        serverInfo.setEnvInfo(envInfo);
        boolean support = CompatibleUtil.isSupportNewBrokerParamPropery1(serverInfo);
        assertFalse(support);

        support = CompatibleUtil.isSupportNewBrokerParamPropery1(null);
        assertFalse(support);
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#isSupportCache(com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo)}
     * .
     */
    public void testIsSupportCache() {
        String version = databaseInfo.getVersion();
        boolean support = CompatibleUtil.isSupportCache(databaseInfo);
        if (version.compareTo("8.2.2") >= 0) {
            assertTrue(support);
        } else {
            assertFalse(support);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#isSupportReuseOID(com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo)}
     * .
     */
    public void testIsSupportReuseOID() {
        String version = databaseInfo.getVersion();
        boolean support = CompatibleUtil.isSupportReuseOID(databaseInfo);
        if (version.compareTo("8.2.2") >= 0) {
            assertTrue(support);
        } else {
            assertFalse(support);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#isSupportBrokerOrDBStatusMonitor(com.cubrid.cubridmanager.core.common.model.ServerInfo)}
     * .
     */
    public void testIsSupportBrokerOrDBStatusMonitor() {
        EnvInfo envInfo = new EnvInfo();
        envInfo.setServerVersion("CUBRID 2008 R2.0(8.2.2.1150)");
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setConnected(true);
        serverInfo.setEnvInfo(envInfo);
        boolean support = CompatibleUtil.isSupportBrokerOrDBStatusMonitor(serverInfo);
        assertTrue(support);

        envInfo.setServerVersion("CUBRID 2008 R2.0(8.2.0.1150)");
        support = CompatibleUtil.isSupportBrokerOrDBStatusMonitor(serverInfo);
        assertFalse(support);
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#isSupportParamNodeListAndPortId(com.cubrid.cubridmanager.core.common.model.ServerInfo)}
     * .
     */
    public void testIsSupportNewDBServerPro() {
        EnvInfo envInfo = new EnvInfo();
        envInfo.setServerVersion("CUBRID 2008 R2.0(8.2.0.1150)");
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setConnected(true);
        serverInfo.setEnvInfo(envInfo);
        boolean support = CompatibleUtil.isSupportParamNodeListAndPortId(serverInfo);
        assertFalse(support);

        envInfo.setServerVersion("CUBRID 2008 R2.0(8.2.2.1150)");
        support = CompatibleUtil.isSupportParamNodeListAndPortId(serverInfo);
        assertTrue(support);
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#isSupportPlanAndParamDump(com.cubrid.cubridmanager.core.common.model.ServerInfo)}
     * .
     */
    public void testIsSupportPlanAndParamDump() {
        EnvInfo envInfo = new EnvInfo();
        envInfo.setServerVersion("CUBRID 2008 R2.0(8.2.0.1150)");
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setConnected(true);
        serverInfo.setEnvInfo(envInfo);
        boolean support = CompatibleUtil.isSupportPlanAndParamDump(serverInfo);
        assertFalse(support);

        envInfo.setServerVersion("CUBRID 2008 R2.0(8.2.2.1150)");
        support = CompatibleUtil.isSupportPlanAndParamDump(serverInfo);
        assertTrue(support);
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#isQueryOrTransTimeUseMs(com.cubrid.cubridmanager.core.common.model.ServerInfo)}
     * .
     */
    public void testIsQueryOrTransTimeUseMs() {
        EnvInfo envInfo = new EnvInfo();
        envInfo.setServerVersion("CUBRID 2008 R2.0(8.2.0.1150)");
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setConnected(true);
        serverInfo.setEnvInfo(envInfo);
        boolean support = CompatibleUtil.isQueryOrTransTimeUseMs(serverInfo);
        assertFalse(support);

        envInfo.setServerVersion("CUBRID 2008 R2.0(8.2.2.1150)");
        support = CompatibleUtil.isQueryOrTransTimeUseMs(serverInfo);
        assertTrue(support);
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#isSupportQueryPlanWithUser(com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo)}
     * .
     */
    public void testIsSupportQueryPlanWithUser() {
        String version = databaseInfo.getVersion();
        boolean support = CompatibleUtil.isSupportQueryPlanWithUser(databaseInfo);
        if (version.compareTo("8.3.0") >= 0) {
            assertTrue(support);
        } else {
            assertFalse(support);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#isSupportRestorePath(com.cubrid.cubridmanager.core.common.model.ServerInfo)}
     * .
     */
    public void testIsSupportRestorePath() {
        EnvInfo envInfo = new EnvInfo();
        envInfo.setServerVersion("CUBRID 2008 R2.0(8.2.0.1150)");
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setConnected(true);
        serverInfo.setEnvInfo(envInfo);
        boolean support = CompatibleUtil.isSupportRestorePath(serverInfo);
        assertFalse(support);

        envInfo.setServerVersion("CUBRID 2008 R2.0(8.3.2.1150)");
        support = CompatibleUtil.isSupportRestorePath(serverInfo);
        assertTrue(support);
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#isSupportSetNull(com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo)}
     * .
     */
    public void testIsSupportSetNull() {
        String version = databaseInfo.getVersion();
        boolean support = CompatibleUtil.isSupportSetNull(databaseInfo);
        if (version.compareTo("8.3.0") >= 0) {
            assertTrue(support);
        } else {
            assertFalse(support);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#isSupportReplaceView(com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo)}
     * .
     */
    public void testIsSupportReplaceView() {
        String version = databaseInfo.getVersion();
        boolean support = CompatibleUtil.isSupportReplaceView(databaseInfo);
        if (version.compareTo("8.3.0") >= 0) {
            assertTrue(support);
        } else {
            assertFalse(support);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#isSupportCreateTableLike(com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo)}
     * .
     */
    public void testIsSupportCreateTableLike() {
        String version = databaseInfo.getVersion();
        boolean support = CompatibleUtil.isSupportCreateTableLike(databaseInfo);
        if (version.compareTo("8.3.0") >= 0) {
            assertTrue(support);
        } else {
            assertFalse(support);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#isSupportTruncateTable(com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo)}
     * .
     */
    public void testIsSupportTruncateTable() {
        String version = databaseInfo.getVersion();
        boolean support = CompatibleUtil.isSupportTruncateTable(databaseInfo);
        if (version.compareTo("8.3.0") >= 0) {
            assertTrue(support);
        } else {
            assertFalse(support);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#isSupportNewFormatOfCMConf(com.cubrid.cubridmanager.core.common.model.ServerInfo)}
     * .
     */
    public void testIsSupportNewFormatOfCMConf() {
        EnvInfo envInfo = new EnvInfo();
        envInfo.setServerVersion("CUBRID 2008 R2.0(8.2.0.1150)");
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setConnected(true);
        serverInfo.setEnvInfo(envInfo);
        boolean support = CompatibleUtil.isSupportNewFormatOfCMConf(serverInfo);
        assertFalse(support);

        envInfo.setServerVersion("CUBRID 2008 R2.0(8.3.0.1150)");
        support = CompatibleUtil.isSupportNewFormatOfCMConf(serverInfo);
        assertTrue(support);
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#isSupportGetParamDump(com.cubrid.cubridmanager.core.common.model.ServerInfo)}
     * .
     */
    public void testIsSupportGetParamDump() {
        EnvInfo envInfo = new EnvInfo();
        envInfo.setServerVersion("CUBRID 2008 R2.0(8.2.0.1150)");
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setConnected(true);
        serverInfo.setEnvInfo(envInfo);
        boolean support = CompatibleUtil.isSupportGetParamDump(serverInfo);
        assertFalse(support);

        envInfo.setServerVersion("CUBRID 2008 R2.0(8.3.2.1150)");
        support = CompatibleUtil.isSupportGetParamDump(serverInfo);
        assertTrue(support);
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#getIndexScanOIDBufferPagesValueType(com.cubrid.cubridmanager.core.common.model.ServerInfo)}
     * .
     */
    public void testGetIndexScanOIDBufferPagesValueType() {
        EnvInfo envInfo = new EnvInfo();
        envInfo.setServerVersion("CUBRID 2008 R2.0(8.2.0.1150)");
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setConnected(true);
        serverInfo.setEnvInfo(envInfo);
        String value = CompatibleUtil.getIndexScanOIDBufferPagesValueType(serverInfo);
        assertEquals("int(v>=1&&v<=16)", value);

        envInfo.setServerVersion("CUBRID 2008 R2.0(8.3.2.1150)");
        value = CompatibleUtil.getIndexScanOIDBufferPagesValueType(serverInfo);
        assertEquals("float(v>=0.05&&v<=16)", value);
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#isSupportLobVersion(com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo)}
     * .
     */
    public void testIsSupportLobVersion() {
        String version = databaseInfo.getVersion();
        boolean support = CompatibleUtil.isSupportLobVersion(databaseInfo);
        if (version.compareTo("8.3.1") >= 0) {
            assertTrue(support);
        } else {
            assertFalse(support);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#isSupportSystemMonitor(com.cubrid.cubridmanager.core.common.model.ServerInfo)}
     * .
     */
    public void testIsSupportSystemMonitor() {
        EnvInfo envInfo = new EnvInfo();
        envInfo.setServerVersion("CUBRID 2008 R2.0(8.2.0.1150)");
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setConnected(true);
        serverInfo.setEnvInfo(envInfo);
        boolean support = CompatibleUtil.isSupportSystemMonitor(serverInfo);
        assertFalse(support);

        envInfo.setServerVersion("CUBRID 2008 R2.0(8.3.2.1150)");
        support = CompatibleUtil.isSupportSystemMonitor(serverInfo);
        assertTrue(support);
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#isSupportDBSystemMonitor(com.cubrid.cubridmanager.core.common.model.ServerInfo)}
     * .
     */
    public void testIsSupportDBSystemMonitor() {
        EnvInfo envInfo = new EnvInfo();
        envInfo.setServerVersion("CUBRID 2008 R2.0(8.2.0.1150)");
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setConnected(true);
        serverInfo.setEnvInfo(envInfo);
        boolean support = CompatibleUtil.isSupportDBSystemMonitor(serverInfo);
        assertFalse(support);

        envInfo.setOsInfo(FileUtil.OsInfoType.NT.toString());
        envInfo.setServerVersion("CUBRID 2008 R2.0(8.3.2.1150)");
        support = CompatibleUtil.isSupportDBSystemMonitor(serverInfo);
        assertFalse(support);

        envInfo.setOsInfo(FileUtil.OsInfoType.LINUX.toString());
        support = CompatibleUtil.isSupportDBSystemMonitor(serverInfo);
        assertTrue(support);
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#isSupportHA(com.cubrid.cubridmanager.core.common.model.ServerInfo)}
     * .
     */
    public void testIsSupportHA() {
        EnvInfo envInfo = new EnvInfo();
        envInfo.setServerVersion("CUBRID 2008 R2.0(8.2.0.1150)");
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setConnected(true);
        serverInfo.setEnvInfo(envInfo);
        boolean support = CompatibleUtil.isSupportHA(serverInfo);
        assertFalse(support);

        envInfo.setServerVersion("CUBRID 2008 R2.0(8.3.2.1150)");
        envInfo.setOsInfo(FileUtil.OsInfoType.LINUX.toString());
        support = CompatibleUtil.isSupportHA(serverInfo);
        assertTrue(support);

        support = CompatibleUtil.isSupportHA(null);
        assertFalse(support);

        envInfo.setOsInfo(FileUtil.OsInfoType.NT.toString());
        support = CompatibleUtil.isSupportHA(serverInfo);
        assertFalse(support);
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#isNewBrokerDiag(com.cubrid.cubridmanager.core.common.model.ServerInfo)}
     * .
     */
    public void testIsNewBrokerDiag() {
        EnvInfo envInfo = new EnvInfo();
        envInfo.setServerVersion("CUBRID 2008 R2.0(8.2.0.1150)");
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setConnected(true);
        serverInfo.setEnvInfo(envInfo);
        boolean support = CompatibleUtil.isNewBrokerDiag(serverInfo);
        assertFalse(support);

        envInfo.setServerVersion("CUBRID 2008 R2.0(8.3.2.1150)");
        support = CompatibleUtil.isNewBrokerDiag(serverInfo);
        assertTrue(support);
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#isSupportBrokerPort(com.cubrid.cubridmanager.core.common.model.ServerInfo)}
     * .
     */
    public void testIsSupportBrokerPort() {
        EnvInfo envInfo = new EnvInfo();
        envInfo.setServerVersion("CUBRID 2008 R2.0(8.2.0.1150)");
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setConnected(true);
        serverInfo.setEnvInfo(envInfo);
        boolean support = CompatibleUtil.isSupportBrokerPort(serverInfo);
        assertFalse(support);

        envInfo.setServerVersion("CUBRID 2008 R2.0(8.3.2.1150)");
        envInfo.setOsInfo(FileUtil.OsInfoType.NT.toString());
        support = CompatibleUtil.isSupportBrokerPort(serverInfo);
        assertTrue(support);

        support = CompatibleUtil.isSupportBrokerPort(null);
        assertFalse(support);
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#isWindows(com.cubrid.cubridmanager.core.cubrid.table.task.ModelUtil.OsInfoType)}
     * .
     */
    public void testIsWindows() {
        boolean isWindows = CompatibleUtil.isWindows(FileUtil.OsInfoType.NT);
        assertTrue(isWindows);
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#isSupportJDBCCancel(com.cubrid.cubridmanager.core.common.model.ServerInfo)}
     * .
     */
    public void testIsSupportJDBCCancel() {
        EnvInfo envInfo = new EnvInfo();
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setConnected(true);
        serverInfo.setEnvInfo(envInfo);
        boolean support = CompatibleUtil.isSupportJDBCCancel(serverInfo);
        assertTrue(support);

        support = CompatibleUtil.isSupportJDBCCancel(null);
        assertTrue(support);

        envInfo.setOsInfo(FileUtil.OsInfoType.LINUX.toString());
        support = CompatibleUtil.isSupportJDBCCancel(serverInfo);
        assertTrue(support);

        envInfo.setOsInfo(FileUtil.OsInfoType.NT.toString());
        support = CompatibleUtil.isSupportJDBCCancel(serverInfo);
        assertFalse(support);
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#isSupportPrefixIndexLength(com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo)}
     * .
     */
    public void testIsSupportPrefixIndexLength() {
        String version = databaseInfo.getVersion();
        boolean support = CompatibleUtil.isSupportPrefixIndexLength(databaseInfo);
        if (version.compareTo("8.3.0") >= 0) {
            assertTrue(support);
        } else {
            assertFalse(support);
        }
    }

    /**
     * Test method for {@link com.cubrid.common.core.util.CompatibleUtil#isSupportCipher(String)} .
     */
    public void testIsSupportCipher() {
        boolean support = CompatibleUtil.isSupportCipher("8.4.0");
        assertTrue(support);
    }

    /**
     * Test method for {@link com.cubrid.common.core.util.CompatibleUtil#parseServerVersion(String)}
     * .
     */
    public void testParseServerVersion() {
        String version = "CUBRID 2008 R3.1 (8.3.1.0149) (Oct 22 2010 06:40:14)";
        version = CompatibleUtil.parseServerVersion(version);
        assertEquals(version, "8.3.1");
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#isSupportNLucene(ServerInfo)} .
     */
    public void testIsSupportNLucene() {
        EnvInfo envInfo = new EnvInfo();
        envInfo.setServerVersion("CUBRID 2008 R2.0(8.3.0.1150)");
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setConnected(true);
        serverInfo.setEnvInfo(envInfo);
        boolean support = CompatibleUtil.isSupportNLucene(serverInfo);
        assertFalse(support);

        envInfo.setServerVersion("CUBRID 2008 R2.0(8.4.0.1150)");
        envInfo.setOsInfo(FileUtil.OsInfoType.NT.toString());
        support = CompatibleUtil.isSupportNLucene(serverInfo);
        assertTrue(support);

        support = CompatibleUtil.isSupportNLucene(null);
        assertFalse(support);
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#isSupportNewHAConfFile(ServerInfo)} .
     */
    public void testIsSupportNewHAConfFile() {
        EnvInfo envInfo = new EnvInfo();
        envInfo.setServerVersion("CUBRID 2008 R2.0(8.3.0.1150)");
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setConnected(true);
        serverInfo.setEnvInfo(envInfo);
        boolean support = CompatibleUtil.isSupportNewHAConfFile(serverInfo);
        assertFalse(support);

        envInfo.setServerVersion("CUBRID 2008 R2.0(8.4.0.1150)");
        envInfo.setOsInfo(FileUtil.OsInfoType.NT.toString());
        support = CompatibleUtil.isSupportNewHAConfFile(serverInfo);
        assertFalse(support);

        support = CompatibleUtil.isSupportNewHAConfFile(null);
        assertFalse(support);
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#isSupportNewHABrokerParam(ServerInfo)} .
     */
    public void testIsSupportNewHAParam() {
        EnvInfo envInfo = new EnvInfo();
        envInfo.setServerVersion("CUBRID 2008 R2.0(8.3.0.1150)");
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setConnected(true);
        serverInfo.setEnvInfo(envInfo);
        boolean support = CompatibleUtil.isSupportNewHABrokerParam(serverInfo);
        assertFalse(support);

        envInfo.setServerVersion("CUBRID 2008 R2.0(8.4.0.1150)");
        envInfo.setOsInfo(FileUtil.OsInfoType.NT.toString());
        support = CompatibleUtil.isSupportNewHABrokerParam(serverInfo);
        assertFalse(support);

        support = CompatibleUtil.isSupportNewHABrokerParam(null);
        assertFalse(support);
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#GetBrokerAccessModeValue(ServerInfo) )} .
     */
    public void testGetBrokerAccessModeValue() {
        EnvInfo envInfo = new EnvInfo();
        envInfo.setServerVersion("CUBRID 2008 R2.0(8.3.0.1150)");
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setConnected(true);
        serverInfo.setEnvInfo(envInfo);
        String mode = CompatibleUtil.getBrokerAccessModeValue(serverInfo);
        assertEquals(mode, "string(RW|RO|SO)");

        envInfo.setServerVersion("CUBRID 2008 R2.0(8.4.0.1150)");
        envInfo.setOsInfo(FileUtil.OsInfoType.LINUX.toString());
        mode = CompatibleUtil.getBrokerAccessModeValue(serverInfo);
        assertEquals(mode, "string(RW|RO|SO|PHRO)");
    }

    /**
     * Test method for {@link com.cubrid.common.core.util.CompatibleUtil#getHAModeValue(ServerInfo)
     * )} .
     */
    public void testgetHAModeValue() {
        EnvInfo envInfo = new EnvInfo();
        envInfo.setServerVersion("CUBRID 2008 R2.0(8.3.0.1150)");
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setConnected(true);
        serverInfo.setEnvInfo(envInfo);
        String mode = CompatibleUtil.getHAModeValue(serverInfo);
        assertEquals(mode, "string(on|off|yes|no)");

        envInfo.setServerVersion("CUBRID 2008 R2.0(8.4.0.1150)");
        envInfo.setOsInfo(FileUtil.OsInfoType.LINUX.toString());
        mode = CompatibleUtil.getHAModeValue(serverInfo);
        assertEquals(mode, "string(on|off|yes|no|replica)");
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#getSupportedPageSize(ServerInfo) )} .
     */
    public void testgetSupportedPageSize() {
        EnvInfo envInfo = new EnvInfo();
        envInfo.setServerVersion("CUBRID 2008 R2.0(8.3.0.1150)");
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setConnected(true);
        serverInfo.setEnvInfo(envInfo);
        String[] pageSizes = CompatibleUtil.getSupportedPageSize(serverInfo);
        assertEquals(pageSizes.length, 5);

        envInfo.setServerVersion("CUBRID 2008 R2.0(8.4.0.1150)");
        pageSizes = CompatibleUtil.getSupportedPageSize(serverInfo);
        assertEquals(pageSizes.length, 3);
    }

    /**
     * Test method for {@link
     * com.cubrid.common.core.util.CompatibleUtil#getDefaultPageSize(ServerInfo) )} .
     */
    public void testgetDefaultPageSize() {
        EnvInfo envInfo = new EnvInfo();
        envInfo.setServerVersion("CUBRID 2008 R2.0(8.3.0.1150)");
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setConnected(true);
        serverInfo.setEnvInfo(envInfo);
        String pageSize = CompatibleUtil.getDefaultPageSize(serverInfo);
        assertEquals(pageSize, "4096");

        envInfo.setServerVersion("CUBRID 2008 R2.0(8.4.0.1150)");
        pageSize = CompatibleUtil.getDefaultPageSize(serverInfo);
        assertEquals(pageSize, "16384");
    }

    public void testGetConfigLogVolumeSize() {
        assertNull(CompatibleUtil.getConfigLogVolumeSize(null, null));

        System.out.println(
                "The server log_volume_size is:"
                        + CompatibleUtil.getConfigLogVolumeSize(serverInfo, "unit_test"));
    }

    public void testGetConfigGenericVolumeSize() {
        assertNull(CompatibleUtil.getConfigGenericVolumeSize(null, null));

        System.out.println(
                "The server db_volume_size is:"
                        + CompatibleUtil.getConfigGenericVolumeSize(serverInfo, "unit_test"));
    }

    public void testIsSupportEnableAccessControl() {
        assertFalse(CompatibleUtil.isSupportEnableAccessControl(null));
        assertTrue(CompatibleUtil.isSupportEnableAccessControl(serverInfo));
    }

    public void testIsSupportAccessIpControl() {
        assertFalse(CompatibleUtil.isSupportAccessIpControl(null));
        assertTrue(CompatibleUtil.isSupportAccessIpControl(serverInfo));
    }

    public void testIsSupportSizesPropInServer() {
        assertFalse(CompatibleUtil.isSupportSizesPropInServer(null));
        assertTrue(CompatibleUtil.isSupportSizesPropInServer(serverInfo));
    }

    public void testIsSupportOnlineCompactDb() {
        assertFalse(CompatibleUtil.isSupportOnlineCompactDb(null));
        assertTrue(CompatibleUtil.isSupportOnlineCompactDb(serverInfo));
    }

    public void testIsNeedCheckHAModeOnNewDb() {
        assertTrue(CompatibleUtil.isNeedCheckHAModeOnNewDb(serverInfo));
    }

    public void testIsSupportChangeColumn() {
        assertTrue(CompatibleUtil.isSupportChangeColumn(databaseInfo));
    }

    public void testIsSupportReorderColumn() {
        assertTrue(CompatibleUtil.isSupportReorderColumn(databaseInfo));
    }

    public void testIsSupportIndexScanOIDBufferSize() {
        assertTrue(CompatibleUtil.isSupportIndexScanOIDBufferSize(serverInfo));
    }
}
