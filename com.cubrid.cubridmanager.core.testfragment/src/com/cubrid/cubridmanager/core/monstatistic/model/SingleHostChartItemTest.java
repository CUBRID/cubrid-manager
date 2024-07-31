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

import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil.MetricType;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil.StatisticType;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil.TimeType;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

public class SingleHostChartItemTest extends TestCase {

    public void testSingleHostChartItem() {
        String nodeId = "demo";
        StatisticType type = StatisticType.OS;
        SingleHostChartItem singleHostChartItem = new SingleHostChartItem(nodeId, type);

        String brokerName = "query_editor";
        singleHostChartItem.setBrokerName(brokerName);
        assertEquals(singleHostChartItem.getBrokerName(), brokerName);

        String dtype = TimeType.DAILY.getType();
        singleHostChartItem.setDType(dtype);
        assertEquals(singleHostChartItem.getDType(), dtype);

        String dbName = "demodb";
        singleHostChartItem.setDbName(dbName);
        assertEquals(singleHostChartItem.getDbName(), dbName);

        List<String> metricList = new ArrayList<String>();
        String metric = MetricType.OS_CPU_IDLE.getMetric();
        String metric2 = MetricType.OS_CPU_USER.getMetric();
        metricList.add(metric);
        singleHostChartItem.setMetricList(metricList);
        assertEquals(singleHostChartItem.getMetricList(), metricList);
        assertTrue(singleHostChartItem.getMetricList().contains(metric));
        singleHostChartItem.addMetric(metric2);
        assertTrue(singleHostChartItem.getMetricList().contains(metric2));

        assertFalse(singleHostChartItem.isMultiHost());

        assertNotNull(singleHostChartItem.getName());

        assertEquals(singleHostChartItem.getNodeId(), nodeId);

        int series = 10;
        singleHostChartItem.setSeries(series);
        assertEquals(singleHostChartItem.getSeries(), series);

        assertEquals(singleHostChartItem.getType(), type);
        singleHostChartItem.setType(type);

        String volName = "demodb_lgat";
        singleHostChartItem.setVolName(volName);
        assertEquals(singleHostChartItem.getVolName(), volName);
    }
}
