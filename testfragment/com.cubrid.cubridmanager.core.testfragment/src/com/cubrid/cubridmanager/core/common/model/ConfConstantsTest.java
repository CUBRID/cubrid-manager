/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: -
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. - Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. - Neither the name of the <ORGANIZATION> nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.cubrid.cubridmanager.core.common.model;

import com.cubrid.cubridmanager.core.SetupEnvTestCase;

/**
 * Test ConfConstants
 *
 * @author pangqiren
 * @version 1.0 - 2010-6-24 created by pangqiren
 */
public class ConfConstantsTest extends SetupEnvTestCase {

    public void testGetDbBaseParameters() {
        assertTrue(ConfConstants.getDbBaseParameters(serverInfo).length > 0);
        assertTrue(ConfConstants.getDbBaseParameters(serverInfo831).length > 0);
    }

    public void testGetDbAdvancedParameters() {
        assertTrue(ConfConstants.getDbAdvancedParameters(serverInfo, false).length > 0);
    }

    public void testGetCmParameters() {
        assertTrue(ConfConstants.getCmParameters().length > 0);
    }

    public void testGetBrokerParameters() {
        assertTrue(ConfConstants.getBrokerParameters(serverInfo).length > 0);
    }

    public void testGetHAParameters() {
        assertTrue(ConfConstants.getHAConfParameters().length > 0);
    }

    public void testIsDefaultBrokerParameter() {
        assertFalse(ConfConstants.isDefaultBrokerParameter("abc"));
        assertTrue(ConfConstants.isDefaultBrokerParameter(ConfConstants.APPL_SERVER_PORT));
        assertTrue(ConfConstants.isDefaultBrokerParameter(ConfConstants.ACCESS_LIST));
        assertTrue(ConfConstants.isDefaultBrokerParameter(ConfConstants.ACCESS_LOG));
        assertTrue(ConfConstants.isDefaultBrokerParameter(ConfConstants.LOG_BACKUP));
        assertTrue(ConfConstants.isDefaultBrokerParameter(ConfConstants.SQL_LOG_MAX_SIZE));
        assertTrue(ConfConstants.isDefaultBrokerParameter(ConfConstants.MAX_STRING_LENGTH));
        assertTrue(ConfConstants.isDefaultBrokerParameter(ConfConstants.SOURCE_ENV));
        assertTrue(ConfConstants.isDefaultBrokerParameter(ConfConstants.STATEMENT_POOLING));
        assertTrue(ConfConstants.isDefaultBrokerParameter(ConfConstants.LONG_QUERY_TIME));
        assertTrue(ConfConstants.isDefaultBrokerParameter(ConfConstants.LONG_TRANSACTION_TIME));
        assertTrue(ConfConstants.isDefaultBrokerParameter(ConfConstants.CCI_PCONNECT));
        assertTrue(ConfConstants.isDefaultBrokerParameter(ConfConstants.SELECT_AUTO_COMMIT));
        assertTrue(ConfConstants.isDefaultBrokerParameter(ConfConstants.ACCESS_MODE));
        assertTrue(ConfConstants.isDefaultBrokerParameter(ConfConstants.PREFERRED_HOSTS));
    }

    public void testGetBrokerParametersOnLowerServerVersion() {
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setServerVersion("8.2.0");
        String[][] brokerParams = ConfConstants.getBrokerParameters(serverInfo);
        assertEquals(23, brokerParams.length);
    }

    public void testGetDbAdvancedParametersOnLowerServerVersion() {
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setServerVersion("8.2.0");
        String[][] advancedParams = ConfConstants.getDbAdvancedParameters(serverInfo, false);
        assertEquals(39, advancedParams.length);
    }
}
