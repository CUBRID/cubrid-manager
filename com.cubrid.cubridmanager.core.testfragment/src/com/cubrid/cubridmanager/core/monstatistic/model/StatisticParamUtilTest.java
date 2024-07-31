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

import com.cubrid.cubridmanager.core.Messages;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil.DbMetricType;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil.MetricType;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil.OsMetricType;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil.StatisticType;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil.TimeType;
import java.util.Set;
import junit.framework.TestCase;

public class StatisticParamUtilTest extends TestCase {

    public void testStatisticParamUtil() {
        assertNotNull(StatisticParamUtil.getDateTypes());
        for (StatisticType type : StatisticType.values()) {
            assertNotNull(type.getMessage());
            assertNotNull(type.getMetricTypeSet());
            assertNotNull(StatisticParamUtil.getSendMsgItems(type));
            assertNotNull(StatisticParamUtil.getSupportedMetricTypes(type));
            switch (type) {
                case DB:
                    assertEquals(StatisticType.getEnumByMessage(Messages.msgDataDb), type);
                    break;
                case DB_VOL:
                    assertEquals(StatisticType.getEnumByMessage(Messages.msgDataDbVol), type);
                    break;
                case BROKER:
                    assertEquals(StatisticType.getEnumByMessage(Messages.msgDataBroker), type);
                    break;
                case OS:
                    assertEquals(StatisticType.getEnumByMessage(Messages.msgDataOs), type);
                    break;
                default:
            }
            for (MetricType metric : MetricType.values()) {
                switch (type) {
                    case DB:
                        if (metric.getMetric().startsWith("db")) {
                            assertTrue(
                                    StatisticParamUtil.isSupportedMetric(type, metric.getMetric()));
                            assertTrue(StatisticParamUtil.isSupportedMetricType(type, metric));
                        } else {
                            assertFalse(
                                    StatisticParamUtil.isSupportedMetric(type, metric.getMetric()));
                            assertFalse(StatisticParamUtil.isSupportedMetricType(type, metric));
                        }
                        break;
                    case DB_VOL:
                        if (metric.getMetric().startsWith("vol")) {
                            assertTrue(
                                    StatisticParamUtil.isSupportedMetric(type, metric.getMetric()));
                            assertTrue(StatisticParamUtil.isSupportedMetricType(type, metric));
                        } else {
                            assertFalse(
                                    StatisticParamUtil.isSupportedMetric(type, metric.getMetric()));
                            assertFalse(StatisticParamUtil.isSupportedMetricType(type, metric));
                        }
                        break;
                    case BROKER:
                        if (metric.getMetric().startsWith("broker")) {
                            assertTrue(
                                    StatisticParamUtil.isSupportedMetric(type, metric.getMetric()));
                            assertTrue(StatisticParamUtil.isSupportedMetricType(type, metric));
                        } else {
                            assertFalse(
                                    StatisticParamUtil.isSupportedMetric(type, metric.getMetric()));
                            assertFalse(StatisticParamUtil.isSupportedMetricType(type, metric));
                        }
                        break;
                    case OS:
                        if (metric.getMetric().startsWith("os")) {
                            assertTrue(
                                    StatisticParamUtil.isSupportedMetric(type, metric.getMetric()));
                            assertTrue(StatisticParamUtil.isSupportedMetricType(type, metric));
                        } else {
                            assertFalse(
                                    StatisticParamUtil.isSupportedMetric(type, metric.getMetric()));
                            assertFalse(StatisticParamUtil.isSupportedMetricType(type, metric));
                        }
                        break;
                    default:
                }
            }
        }

        for (MetricType metric : MetricType.values()) {
            assertNotNull(metric.getMessage());
            assertNotNull(metric.getMetric());
            assertNotNull(metric.getChartName());
            assertEquals(MetricType.getEnumByMessage(metric.getMessage()), metric);
            assertEquals(MetricType.getEnumByMetric(metric.getMetric()), metric);
            Set<MetricType> metricSet =
                    StatisticParamUtil.getCompatibleMetricsForDisplay(metric.getMetric());
            assertNotNull(metricSet);
            MetricType metric2 = metricSet.iterator().next();
            assertTrue(
                    StatisticParamUtil.isCompatibleMetricForDisplay(
                            metric.getMetric(), metric2.getMetric()));
            if (metric.getMetric().startsWith("db")) {
                assertEquals(StatisticParamUtil.getTypeByMetric(metric), StatisticType.DB);
                assertEquals(
                        StatisticParamUtil.getTypeByMetric(metric.getMetric()), StatisticType.DB);
            } else if (metric.getMetric().startsWith("vol")) {
                assertEquals(StatisticParamUtil.getTypeByMetric(metric), StatisticType.DB_VOL);
                assertEquals(
                        StatisticParamUtil.getTypeByMetric(metric.getMetric()),
                        StatisticType.DB_VOL);
            } else if (metric.getMetric().startsWith("broker")) {
                assertEquals(StatisticParamUtil.getTypeByMetric(metric), StatisticType.BROKER);
                assertEquals(
                        StatisticParamUtil.getTypeByMetric(metric.getMetric()),
                        StatisticType.BROKER);
            } else {
                assertEquals(StatisticParamUtil.getTypeByMetric(metric), StatisticType.OS);
                assertEquals(
                        StatisticParamUtil.getTypeByMetric(metric.getMetric()), StatisticType.OS);
            }
            if (metric.getMetric().indexOf("cpu") > -1
                    || metric.getMetric().indexOf("ratio") > -1) {
                assertTrue(StatisticParamUtil.isPercentageData(metric.getMetric()));
            } else {
                assertFalse(StatisticParamUtil.isPercentageData(metric.getMetric()));
            }
            if (metric.getMetric().indexOf("mem") > -1) {
                assertTrue(StatisticParamUtil.isMemoryData(metric.getMetric()));
            } else {
                assertFalse(StatisticParamUtil.isMemoryData(metric.getMetric()));
            }
            if (metric.getMetric().indexOf("freespace") > -1
                    || metric.getMetric().indexOf("disk") > -1) {
                assertTrue(StatisticParamUtil.isDiskData(metric.getMetric()));
            } else {
                assertFalse(StatisticParamUtil.isDiskData(metric.getMetric()));
            }
        }

        testTimeType();
        testDbMetricType();
        testOsMetricType();
    }

    public void testTimeType() {
        for (TimeType type : TimeType.values()) {
            assertNotNull(type.getMessage());
            assertNotNull(type.getType());
            switch (type) {
                case DAILY:
                    assertEquals(TimeType.getEnumByMessage(type.getMessage()), type);
                    assertEquals(TimeType.getEnumByMessage(type.getType()), type);
                    break;
                case WEEKLY:
                    assertEquals(TimeType.getEnumByMessage(type.getMessage()), type);
                    assertEquals(TimeType.getEnumByMessage(type.getType()), type);
                    break;
                case MONTHLY:
                    assertEquals(TimeType.getEnumByMessage(type.getMessage()), type);
                    assertEquals(TimeType.getEnumByMessage(type.getType()), type);
                    break;
                case YEARLY:
                    assertEquals(TimeType.getEnumByMessage(type.getMessage()), type);
                    assertEquals(TimeType.getEnumByMessage(type.getType()), type);
                    break;
                default:
            }
        }
    }

    public void testDbMetricType() {
        for (DbMetricType type : DbMetricType.values()) {
            assertNotNull(type.getMetricTypeSet());
        }
    }

    public void testOsMetricType() {
        for (OsMetricType type : OsMetricType.values()) {
            assertNotNull(type.getMetricTypeSet());
        }
    }
}
