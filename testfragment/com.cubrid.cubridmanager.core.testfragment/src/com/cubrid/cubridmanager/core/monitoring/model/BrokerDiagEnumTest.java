/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.cubridmanager.core.monitoring.model;

import com.cubrid.cubridmanager.core.Messages;
import junit.framework.TestCase;

/**
 * Test the enum of BrokerDiagEnum
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-9-1 created by lizhiqiang
 */
public class BrokerDiagEnumTest extends TestCase {

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagEnum#getName()}.
     */
    public void testGetName() {
        String active = BrokerDiagEnum.ACTIVE.getName();
        assertEquals(Messages.active, active);

        String rps = BrokerDiagEnum.RPS.getName();
        assertEquals(Messages.rps, rps);

        String tps = BrokerDiagEnum.TPS.getName();
        assertEquals(Messages.tps, tps);

        String qps = BrokerDiagEnum.QPS.getName();
        assertEquals(Messages.qps, qps);

        String long_q = BrokerDiagEnum.LONG_Q.getName();
        assertEquals(Messages.long_q, long_q);

        String long_t = BrokerDiagEnum.LONG_T.getName();
        assertEquals(Messages.long_t, long_t);

        String err_q = BrokerDiagEnum.ERR_Q.getName();
        assertEquals(Messages.err_q, err_q);

        String session = BrokerDiagEnum.SESSION.getName();
        assertEquals(Messages.session, session);

        String active_sesion = BrokerDiagEnum.ACTIVE_SESSION.getName();
        assertEquals(Messages.active_session, active_sesion);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagEnum#searchNick(java.lang.String)}.
     */
    public void testSearchNick() {
        BrokerDiagEnum instance = BrokerDiagEnum.searchNick(Messages.rps);
        assertSame(BrokerDiagEnum.RPS, instance);

        instance = BrokerDiagEnum.searchNick(Messages.tps);
        assertSame(BrokerDiagEnum.TPS, instance);

        instance = BrokerDiagEnum.searchNick("nono");
        assertNull(instance);
    }
}
