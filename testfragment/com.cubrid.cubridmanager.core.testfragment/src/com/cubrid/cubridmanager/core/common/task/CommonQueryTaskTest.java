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
package com.cubrid.cubridmanager.core.common.task;

import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.cubrid.database.model.transaction.KillTransactionList;
import com.cubrid.cubridmanager.core.mondashboard.model.StandbyServerStat;
import com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData;
import com.cubrid.cubridmanager.core.utils.ModelUtil.KillTranType;

/**
 * Test CommonQueryTask
 *
 * @author pangqiren
 * @version 1.0 - 2010-9-1 created by pangqiren
 */
public class CommonQueryTaskTest extends SetupEnvTestCase {

    public void testTask() {

        CommonQueryTask<StandbyServerStat> standbyServerStatTask =
                new CommonQueryTask<StandbyServerStat>(
                        serverInfo,
                        CommonSendMsg.getCommonDatabaseSendMsg(),
                        new StandbyServerStat(),
                        "UTF-8");
        standbyServerStatTask.setDbName(testDbName);
        standbyServerStatTask.setDbid(dbaUserName);
        standbyServerStatTask.setDbpasswd(dbaPassword);
        standbyServerStatTask.execute();
        standbyServerStatTask.getResultModel();

        CommonQueryTask<BrokerDiagData> brokerDiagDataTask =
                new CommonQueryTask<BrokerDiagData>(
                        serverInfo, CommonSendMsg.getGetBrokerStatusItems(), new BrokerDiagData());
        brokerDiagDataTask.setBName("query_editor");
        brokerDiagDataTask.execute();
        brokerDiagDataTask.getResultModel();
        brokerDiagDataTask.setBroker("query_editor");

        CommonQueryTask<KillTransactionList> task =
                new CommonQueryTask<KillTransactionList>(
                        serverInfo,
                        CommonSendMsg.getKillTransactionMSGItems(),
                        new KillTransactionList());
        task.setDbName(testDbName);
        task.setKillTranType(KillTranType.H);
        task.setKillTranParameter("localhost");
    }
}
