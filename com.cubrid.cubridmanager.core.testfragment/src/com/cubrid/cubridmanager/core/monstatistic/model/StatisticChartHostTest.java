/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.cubridmanager.core.monstatistic.model;

import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil.MetricType;
import junit.framework.TestCase;

public class StatisticChartHostTest extends TestCase {

    public void testStatisticChartHost() {
        String cubridServerId = "monitor_statistic_01";
        StatisticChartHost statisticChartHost = new StatisticChartHost(cubridServerId);
        assertEquals(statisticChartHost.getCubridServerId(), cubridServerId);

        String brokerName = "query_editor";
        statisticChartHost.setBrokerName(brokerName);
        assertEquals(statisticChartHost.getBrokerName(), brokerName);

        String dbName = "demodb";
        statisticChartHost.setDbName(dbName);
        assertEquals(statisticChartHost.getDbName(), dbName);

        String ip = "127.0.0.1";
        statisticChartHost.setIp(ip);
        assertEquals(statisticChartHost.getIp(), ip);

        int port = 8001;
        statisticChartHost.setPort(port);
        assertEquals(statisticChartHost.getPort(), port);

        String user = "admin";
        statisticChartHost.setUser(user);
        assertEquals(statisticChartHost.getUser(), user);

        String password = "123456";
        statisticChartHost.setPassword(password);
        assertEquals(statisticChartHost.getPassword(), password);

        String metric = MetricType.OS_CPU_IDLE.getMetric();
        statisticChartHost.setMetric(metric);
        assertEquals(statisticChartHost.getMetric(), metric);

        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setHostAddress(ip);
        serverInfo.setHostMonPort(port);
        statisticChartHost.setServerInfo(serverInfo);
        assertEquals(statisticChartHost.getServerInfo(), serverInfo);

        String volName = "demodb_lgat";
        statisticChartHost.setVolName(volName);
        assertEquals(statisticChartHost.getVolName(), volName);

        StatisticChartHost statisticChartHost2 = new StatisticChartHost(ip, port, user, password);
        assertEquals(statisticChartHost2.getIp(), ip);
        assertEquals(statisticChartHost2.getPort(), port);
        assertEquals(statisticChartHost2.getUser(), user);
        assertEquals(statisticChartHost2.getPassword(), password);
    }
}
