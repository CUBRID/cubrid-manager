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
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil.StatisticType;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil.TimeType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import junit.framework.TestCase;

public class StatisticDataTest extends TestCase {

    public void prepare() {
        String name = this.getClass().getName();
        name = name.substring(0, name.lastIndexOf("Test"));
        String simpleName = name.substring(name.lastIndexOf(".") + 1);
        String objName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
        System.out.println(simpleName + " " + objName + " = new " + simpleName + "();");
        try {
            List<Method> methodList = Arrays.asList(Class.forName(name).getMethods());
            Collections.sort(
                    methodList,
                    new Comparator<Method>() {
                        @Override
                        public int compare(Method o1, Method o2) {
                            String name1 = o1.getName();
                            String name2 = o2.getName();
                            if (name1.startsWith("set") || name1.startsWith("get")) {
                                name1 = name1.substring(3);
                            } else if (name1.startsWith("is")) {
                                name1 = name1.substring(2);
                            }
                            if (name2.startsWith("set") || name2.startsWith("get")) {
                                name2 = name2.substring(3);
                            } else if (name2.startsWith("is")) {
                                name2 = name2.substring(2);
                            }

                            int res = name1.compareTo(name2);
                            if (res == 0) {
                                if (o1.getName().startsWith("set")) {
                                    return -1;
                                } else if (o2.getName().startsWith("set")) {
                                    return 1;
                                }
                            }
                            return res;
                        }
                    });
            for (Method m : methodList) {
                if ("wait".equals(m.getName())
                        || "notify".equals(m.getName())
                        || "notifyAll".equals(m.getName())
                        || "toString".equals(m.getName())
                        || "hashCode".equals(m.getName())
                        || "clone".equals(m.getName())
                        || "getClass".equals(m.getName())
                        || "equals".equals(m.getName())) {
                    continue;
                }

                String mName = m.getName();
                if (m.getName().startsWith("get")) {
                    mName = mName.substring(3, 4).toLowerCase() + mName.substring(4);
                    System.out.println(
                            "assertEquals(" + objName + "." + m.getName() + "(), " + mName + ");");
                } else if (m.getName().startsWith("set")) {
                    mName = mName.substring(3, 4).toLowerCase() + mName.substring(4);
                    System.out.println(objName + "." + m.getName() + "(" + mName + ");");
                } else if (m.getName().startsWith("is")) {
                    mName = mName.substring(2, 3).toLowerCase() + mName.substring(3);
                    System.out.println(
                            "assertEquals(" + objName + "." + m.getName() + "(), " + mName + ");");
                } else {
                    System.out.println(objName + "." + m.getName() + "();");
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void testSingleHostChartItem() {
        StatisticData statisticData = new StatisticData();

        String data1 = "5523";
        String data2 = "3632";
        String data3 = "7639";
        statisticData.addData(data1);
        statisticData.addData(data2);
        statisticData.addData(data3);
        assertTrue(statisticData.getData().contains(Integer.parseInt(data1)));
        assertTrue(statisticData.getData().contains(Integer.parseInt(data2)));
        assertTrue(statisticData.getData().contains(Integer.parseInt(data3)));
        assertFalse(statisticData.getData().contains(data3));
        assertFalse(statisticData.getData().contains("2275"));

        String dbName = "demodb";
        statisticData.setDbName(dbName);
        assertEquals(statisticData.getDbName(), dbName);

        String dtype = TimeType.DAILY.getType();
        statisticData.setDtype(dtype);
        assertEquals(statisticData.getDtype(), dtype);

        String metric = MetricType.OS_CPU_IDLE.getMetric();
        statisticData.setMetric(metric);
        assertEquals(statisticData.getMetric(), metric);

        String ip = "127.0.0.1";
        int port = 8001;
        String user = "admin";
        String password = "123456";
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setHostAddress(ip);
        serverInfo.setHostMonPort(port);
        serverInfo.setUserName(user);
        serverInfo.setUserPassword(password);
        statisticData.setServerInfo(serverInfo);
        assertEquals(statisticData.getServerInfo(), serverInfo);

        assertEquals(statisticData.getTaskName(), "get_mon_statistic");

        StatisticType type = StatisticType.BROKER;
        statisticData.setType(type);
        assertEquals(statisticData.getType(), type);

        String volName = "demodb_lgat";
        statisticData.setVolName(volName);
        assertEquals(statisticData.getVolName(), volName);

        String bName = "query_editor";
        statisticData.setbName(bName);
        assertEquals(statisticData.getbName(), bName);

        assertNotNull(statisticData.getSimpleDescription(false));
        assertFalse(statisticData.getSimpleDescription(false).indexOf(bName) > -1);
        assertNotNull(statisticData.getDescription(false));
        assertTrue(statisticData.getDescription(false).indexOf(bName) > -1);
    }
}
