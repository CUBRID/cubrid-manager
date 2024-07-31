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
package com.cubrid.cubridmanager.ui.spi.persist;

import com.cubrid.common.core.util.ArrayUtil;
import com.cubrid.common.core.util.CipherUtils;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.MonitorStatistic;
import com.cubrid.common.ui.spi.persist.PersistUtils;
import com.cubrid.cubridmanager.core.common.xml.IXMLMemento;
import com.cubrid.cubridmanager.core.common.xml.XMLMemento;
import com.cubrid.cubridmanager.core.monstatistic.model.MultiHostChartItem;
import com.cubrid.cubridmanager.core.monstatistic.model.SingleHostChartItem;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticChartHost;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticChartItem;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil.StatisticType;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Monitor Statistic persist manager
 *
 * @author Santiago Wang
 * @version 1.0 - 2013-07-30 created by Santiago Wang
 */
public final class MonitorStatisticPersistManager {

    private static final String MONITOR_STATISTIC_XML_CONTENT = "CUBRID_MONITOR_STATISTIC";
    private Map<String, List<MonitorStatistic>> monitorStatisticMap = null;
    private static MonitorStatisticPersistManager instance;
    private final String iconPath = "icons/navigator/status_item.png";
    // more than the limit of 32 chars for host node
    private final String HOST_ID_FOR_MULTI_HOST = "multi-host monitor statistic in dashboard";

    private MonitorStatisticPersistManager() {
        init();
    }

    /**
     * Return the only MonitorStatisticPersistManager
     *
     * @return MonitorStatisticPersistManager
     */
    public static MonitorStatisticPersistManager getInstance() {
        synchronized (MonitorStatisticPersistManager.class) {
            if (instance == null) {
                instance = new MonitorStatisticPersistManager();
            }
        }
        return instance;
    }

    /** Initial the persist manager */
    protected void init() {
        synchronized (this) {
            monitorStatisticMap = new HashMap<String, List<MonitorStatistic>>();
            loadMonitorStatistic();
        }
    }

    /** Load MonitorStatistic nodes */
    protected void loadMonitorStatistic() {
        synchronized (this) {
            IXMLMemento memento =
                    PersistUtils.getXMLMemento(
                            CubridManagerUIPlugin.PLUGIN_ID, MONITOR_STATISTIC_XML_CONTENT);
            if (memento != null) {
                IXMLMemento[] monitorsPerHost = memento.getChildren("monitorsPerHost");
                for (int i = 0; i < monitorsPerHost.length; i++) {
                    boolean isMultiHost = monitorsPerHost[i].getBoolean("isMultihost");
                    String hostId = HOST_ID_FOR_MULTI_HOST;
                    if (!isMultiHost) {
                        hostId = monitorsPerHost[i].getString("hostId");
                    }
                    IXMLMemento[] children = monitorsPerHost[i].getChildren("monitor");
                    List<MonitorStatistic> monitorStatisticList = new ArrayList<MonitorStatistic>();
                    for (int j = 0; j < children.length; j++) {
                        String id = children[j].getString("name");
                        String label = id;
                        if (!isMultiHost && id.indexOf("@") != -1) {
                            label = id.substring(0, id.indexOf("@"));
                        }
                        MonitorStatistic node = new MonitorStatistic(id, label, iconPath);
                        node.setType(CubridNodeType.MONITOR_STATISTIC_PAGE);
                        node.setMultiHost(isMultiHost);

                        IXMLMemento[] chartChildren = children[j].getChildren("chart");
                        for (int k = 0; chartChildren != null && k < chartChildren.length; k++) {
                            String typeStr = chartChildren[k].getString("type");
                            StatisticType type = StatisticType.valueOf(typeStr);
                            int series = chartChildren[k].getInteger("series");
                            String dType = chartChildren[k].getString("dtype");
                            if (!isMultiHost) {
                                SingleHostChartItem item = new SingleHostChartItem(id, type);
                                item.setSeries(series);
                                item.setDType(dType);
                                String metrics = chartChildren[k].getString("metrics");
                                String[] metricAr = metrics.split(",");
                                for (String metric : metricAr) {
                                    item.addMetric(metric);
                                }

                                String dbName = null;
                                switch (type) {
                                    case DB:
                                        dbName = chartChildren[k].getString("dbname");
                                        item.setDbName(dbName);
                                        break;
                                    case DB_VOL:
                                        dbName = chartChildren[k].getString("dbname");
                                        String volName = chartChildren[k].getString("volname");
                                        item.setDbName(dbName);
                                        item.setVolName(volName);
                                        break;
                                    case BROKER:
                                        String brokerName = chartChildren[k].getString("bname");
                                        item.setBrokerName(brokerName);
                                        break;
                                    case OS:
                                        break;
                                    default:
                                        break;
                                }
                                node.addStatisticItem(item);
                            } else {
                                IXMLMemento[] hostChildren = chartChildren[k].getChildren("host");
                                MultiHostChartItem item = new MultiHostChartItem(id, type);
                                item.setSeries(series);
                                item.setDType(dType);
                                for (int m = 0;
                                        hostChildren != null && m < hostChildren.length;
                                        m++) {
                                    String cubridServerId =
                                            hostChildren[m].getString("cubridServerId");
                                    String ip = hostChildren[m].getString("ip");
                                    int port = hostChildren[m].getInteger("port");
                                    String user = hostChildren[m].getString("user");
                                    String pass = hostChildren[m].getString("password");

                                    StatisticChartHost host;
                                    if (cubridServerId != null) {
                                        host = new StatisticChartHost(cubridServerId);
                                    } else {
                                        host =
                                                new StatisticChartHost(
                                                        ip, port, user, CipherUtils.decrypt(pass));
                                    }

                                    String metric = hostChildren[m].getString("metric");
                                    host.setMetric(metric);
                                    String dbName = null;
                                    switch (type) {
                                        case DB:
                                            dbName = hostChildren[m].getString("dbname");
                                            host.setDbName(dbName);
                                            break;
                                        case DB_VOL:
                                            dbName = hostChildren[m].getString("dbname");
                                            String volName = hostChildren[m].getString("volname");
                                            host.setDbName(dbName);
                                            host.setVolName(volName);
                                            break;
                                        case BROKER:
                                            String brokerName = hostChildren[m].getString("bname");
                                            host.setBrokerName(brokerName);
                                            break;
                                        case OS:
                                            break;
                                        default:
                                            break;
                                    }

                                    item.addStatisticChartHost(host);
                                }
                                node.addStatisticItem(item);
                            }
                        }
                        monitorStatisticList.add(node);
                    }
                    monitorStatisticMap.put(hostId, monitorStatisticList);
                }
            }
        }
    }

    /** Save MonitorStatistic nodes */
    public void saveStatistic() {
        synchronized (this) {
            XMLMemento memento = XMLMemento.createWriteRoot("monitors");
            Iterator<String> keySetIterator = monitorStatisticMap.keySet().iterator();
            while (keySetIterator.hasNext()) {
                String hostId = keySetIterator.next();
                boolean isMultiHost = HOST_ID_FOR_MULTI_HOST.equals(hostId);
                IXMLMemento monsPerHostMemento = memento.createChild("monitorsPerHost");
                monsPerHostMemento.putBoolean("isMultihost", isMultiHost);
                if (!isMultiHost) {
                    monsPerHostMemento.putString("hostId", hostId);
                }
                List<MonitorStatistic> monitorStatisticList = monitorStatisticMap.get(hostId);
                if (monitorStatisticList == null) {
                    continue;
                }
                Iterator<MonitorStatistic> nodeIterator = monitorStatisticList.iterator();
                while (nodeIterator.hasNext()) {
                    MonitorStatistic node = nodeIterator.next();
                    IXMLMemento monMemento = monsPerHostMemento.createChild("monitor");
                    monMemento.putString("name", node.getId());
                    // save StatisticItem list
                    List<StatisticChartItem> statisticItemList = node.getStatisticItemList();
                    Iterator<StatisticChartItem> itemIterator = statisticItemList.iterator();
                    while (itemIterator.hasNext()) {
                        if (!node.isMultiHost()) {
                            SingleHostChartItem item = (SingleHostChartItem) itemIterator.next();
                            IXMLMemento chartMemento = monMemento.createChild("chart");
                            chartMemento.putInteger("series", item.getSeries());
                            chartMemento.putString("name", item.getName());
                            chartMemento.putString("type", item.getType().toString());
                            chartMemento.putString("dtype", item.getDType());
                            chartMemento.putString(
                                    "metrics",
                                    ArrayUtil.collectionToCSString(item.getMetricList()));
                            switch (item.getType()) {
                                case DB:
                                    chartMemento.putString("dbname", item.getDbName());
                                    break;
                                case DB_VOL:
                                    chartMemento.putString("dbname", item.getDbName());
                                    chartMemento.putString("volname", item.getVolName());
                                    break;
                                case BROKER:
                                    chartMemento.putString("bname", item.getBrokerName());
                                    break;
                                case OS:
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            MultiHostChartItem item = (MultiHostChartItem) itemIterator.next();
                            IXMLMemento chartMemento = monMemento.createChild("chart");
                            chartMemento.putInteger("series", item.getSeries());
                            chartMemento.putString("name", item.getName());
                            chartMemento.putString("type", item.getType().toString());
                            chartMemento.putString("dtype", item.getDType());
                            // save HostInfo list
                            List<StatisticChartHost> hostList = item.getHostList();
                            Iterator<StatisticChartHost> hostIterator = hostList.iterator();
                            while (hostIterator.hasNext()) {
                                StatisticChartHost host = (StatisticChartHost) hostIterator.next();
                                IXMLMemento hostMemento = chartMemento.createChild("host");
                                hostMemento.putString("cubridServerId", host.getCubridServerId());
                                hostMemento.putString("ip", host.getIp());
                                hostMemento.putInteger("port", host.getPort());
                                hostMemento.putString("user", host.getUser());
                                hostMemento.putString(
                                        "password", CipherUtils.encrypt(host.getPassword()));

                                hostMemento.putString("metric", host.getMetric());
                                switch (item.getType()) {
                                    case DB:
                                        hostMemento.putString("dbname", host.getDbName());
                                        break;
                                    case DB_VOL:
                                        hostMemento.putString("dbname", host.getDbName());
                                        hostMemento.putString("volname", host.getVolName());
                                        break;
                                    case BROKER:
                                        hostMemento.putString("bname", host.getBrokerName());
                                        break;
                                    case OS:
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    }
                }
            }
            PersistUtils.saveXMLMemento(
                    CubridManagerUIPlugin.PLUGIN_ID, MONITOR_STATISTIC_XML_CONTENT, memento);
        }
    }

    /**
     * Add MonitorStatistic node according to <i>hostId</i>. If <i>hostId</i> is not null, will add
     * a single host node for the specified host, else add a multi-host node for Dashboard tab.
     *
     * @param node MonitorStatistic
     * @param hostId Host node id
     */
    public void addMonitorStatistic(MonitorStatistic node, String hostId) {
        if (node == null) {
            return;
        }
        synchronized (this) {
            if (hostId == null) {
                hostId = HOST_ID_FOR_MULTI_HOST;
            }
            List<MonitorStatistic> monitorStatisticList = monitorStatisticMap.get(hostId);
            if (monitorStatisticList == null) {
                monitorStatisticList = new ArrayList<MonitorStatistic>();
            }
            monitorStatisticList.add(node);
            // TODO:
            monitorStatisticMap.put(hostId, monitorStatisticList);
            saveStatistic();
            CubridNodeManager.getInstance()
                    .fireCubridNodeChanged(
                            new CubridNodeChangedEvent(node, CubridNodeChangedEventType.NODE_ADD));
        }
    }

    /**
     * Remove MonitorStatistic node according to <i>hostId</i>. If <i>hostId</i> is null, will check
     * all the MonitorStatistic node belong to Dashboard tab.
     *
     * @param node
     * @param hostId
     */
    public void removeMonitorStatistic(MonitorStatistic node, String hostId) {
        if (node == null) {
            return;
        }
        synchronized (this) {
            if (hostId == null) {
                hostId = HOST_ID_FOR_MULTI_HOST;
            }
            List<MonitorStatistic> monitorStatisticList = monitorStatisticMap.get(hostId);
            if (monitorStatisticList != null) {
                monitorStatisticList.remove(node);
                saveStatistic();
                CubridNodeManager.getInstance()
                        .fireCubridNodeChanged(
                                new CubridNodeChangedEvent(
                                        node, CubridNodeChangedEventType.NODE_REMOVE));
            }
        }
    }

    /**
     * Get MonitorStatistic node list according to <i>hostId</i>. If <i>hostId</i> is not null, will
     * return the MonitorStatistic node list which node belongs to this host node, else will return
     * the MonitorStatistic node list which belongs to Dashboard tab.
     *
     * @param hostId Host node id
     * @return
     */
    public List<MonitorStatistic> getMonitorStatisticListByHostId(String hostId) {
        if (hostId == null) {
            hostId = HOST_ID_FOR_MULTI_HOST;
        }
        List<MonitorStatistic> monitorStatisticList = monitorStatisticMap.get(hostId);
        return monitorStatisticList != null
                ? monitorStatisticList
                : new ArrayList<MonitorStatistic>();
    }

    /**
     * Check whether the MonitorStatistic node id is belonged to the MonitorStatistic node list
     * specified by <i>hostId</i>. If <i>hostId</i> is null, will check all the MonitorStatistic
     * node belong to Dashboard tab.
     *
     * @param id
     * @param hostId
     * @return
     */
    public boolean isContainedById(String id, String hostId) { // FIXME extract?
        if (id == null) {
            return false;
        }
        if (hostId == null) {
            hostId = HOST_ID_FOR_MULTI_HOST;
        }
        List<MonitorStatistic> monitorStatisticList = monitorStatisticMap.get(hostId);
        if (monitorStatisticList != null) {
            for (MonitorStatistic node : monitorStatisticList) {
                if (node.getId().equals(id)) {
                    return true;
                }
            }
        }
        return false;
    }
}
