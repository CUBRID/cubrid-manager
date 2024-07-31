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
package com.cubrid.cubridmanager.ui.broker.editor.internal;

import static com.cubrid.common.core.util.NoOp.noOp;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.cubridmanager.core.common.xml.IXMLMemento;
import com.cubrid.cubridmanager.core.common.xml.XMLMemento;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.slf4j.Logger;

/**
 * This class is responsible to persist broker interval setting information
 *
 * @author pangqiren
 * @version 1.0 - 2009-5-25 created by pangqiren
 */
public final class BrokerIntervalSettingManager {
    private static final Logger LOGGER = LogUtil.getLogger(BrokerIntervalSettingManager.class);
    private static final String CUBRID_BROKER_INTERVAL_XML_CONTENT =
            "CUBRID_BROKER_INTERVAL_SETTING";
    private static BrokerIntervalSettingManager instance = null;
    private List<BrokerIntervalSetting> brokerIntervalSettingList = null;
    private static boolean initialized = false;

    private BrokerIntervalSettingManager() {
        noOp();
    }

    /**
     * Return the only BrokerIntervalSettingManager
     *
     * @return BrokerIntervalSettingManager
     */
    public static BrokerIntervalSettingManager getInstance() {
        synchronized (BrokerIntervalSettingManager.class) {
            if (instance == null) {
                instance = new BrokerIntervalSettingManager();
            }
        }
        return instance;
    }

    /** Init the BrokerIntervalSettingManager */
    protected void init() {
        synchronized (this) {
            if (initialized) {
                return;
            }
            brokerIntervalSettingList = new ArrayList<BrokerIntervalSetting>();
            loadBrokerIntervalSettings();
            initialized = true;
        }
    }

    /** Load broker interval settings from plugin preference */
    protected void loadBrokerIntervalSettings() {
        synchronized (this) {
            IEclipsePreferences preference =
                    new InstanceScope().getNode(CubridManagerUIPlugin.PLUGIN_ID);
            String xmlString = preference.get(CUBRID_BROKER_INTERVAL_XML_CONTENT, "");
            if (xmlString != null && xmlString.length() > 0) {
                try {
                    ByteArrayInputStream in = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));
                    IXMLMemento memento = XMLMemento.loadMemento(in);
                    IXMLMemento[] children = memento.getChildren("BrokerIntervalSetting");
                    for (int i = 0; i < children.length; i++) {
                        String serverName = children[i].getString("serverName");
                        String brokerName = children[i].getString("brokerName");
                        String isOn = children[i].getString("isOn");
                        String interval = children[i].getString("interval");

                        BrokerIntervalSetting brokerInterval =
                                new BrokerIntervalSetting(
                                        serverName,
                                        brokerName,
                                        interval,
                                        isOn != null && isOn.equals("true"));
                        brokerIntervalSettingList.add(brokerInterval);
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
            }
        }
    }

    /** Save broker interval to plugin preference */
    public void saveBrokerIntervals() {
        synchronized (this) {
            if (!initialized) {
                init();
            }
            try {
                XMLMemento memento = XMLMemento.createWriteRoot("BrokerIntervalSettings");
                Iterator<BrokerIntervalSetting> iterator = brokerIntervalSettingList.iterator();
                while (iterator.hasNext()) {
                    BrokerIntervalSetting brokerInterval = (BrokerIntervalSetting) iterator.next();
                    IXMLMemento child = memento.createChild("BrokerIntervalSetting");
                    child.putString("serverName", brokerInterval.getServerName());
                    child.putString("brokerName", brokerInterval.getBrokerName());
                    child.putString("isOn", brokerInterval.isOn() ? "true" : "false");
                    child.putString("interval", brokerInterval.getInterval());
                }
                String xmlString = memento.saveToString();
                IEclipsePreferences preference =
                        new InstanceScope().getNode(CubridManagerUIPlugin.PLUGIN_ID);
                preference.put(CUBRID_BROKER_INTERVAL_XML_CONTENT, xmlString);
                preference.flush();
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    /**
     * Set broker interval setting
     *
     * @param brokerIntervalSetting the broker interval setting
     */
    public void setBrokerInterval(BrokerIntervalSetting brokerIntervalSetting) {
        synchronized (this) {
            if (!initialized) {
                init();
            }
            if (brokerIntervalSetting != null) {
                boolean isExist = false;
                for (int i = 0, n = brokerIntervalSettingList.size(); i < n; i++) {
                    BrokerIntervalSetting intervalInfo = brokerIntervalSettingList.get(i);
                    if (brokerIntervalSetting.getServerName().equals(intervalInfo.getServerName())
                            && brokerIntervalSetting
                                    .getBrokerName()
                                    .equals(intervalInfo.getBrokerName())) {
                        intervalInfo.setInterval(brokerIntervalSetting.getInterval());
                        intervalInfo.setOn(brokerIntervalSetting.isOn());
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    brokerIntervalSettingList.add(brokerIntervalSetting);
                }
                saveBrokerIntervals();
            }
        }
    }

    /**
     * Get broker interval setting
     *
     * @param serverName the server name
     * @param brokerName the broker name
     * @return the BrokerIntervalSetting object
     */
    public BrokerIntervalSetting getBrokerIntervalSetting(String serverName, String brokerName) {
        synchronized (this) {
            if (!initialized) {
                init();
            }
            for (int i = 0, n = brokerIntervalSettingList.size(); i < n; i++) {
                BrokerIntervalSetting intervalInfo = brokerIntervalSettingList.get(i);
                if (serverName.equals(intervalInfo.getServerName())
                        && brokerName.equals(intervalInfo.getBrokerName())) {
                    return intervalInfo;
                }
            }
            BrokerIntervalSetting brokerInterValSetting =
                    new BrokerIntervalSetting(serverName, brokerName, "1", false);
            setBrokerInterval(brokerInterValSetting);
            return brokerInterValSetting;
        }
    }

    /**
     * Remove broker interval setting informations in some server
     *
     * @param serverName the server name
     */
    public void removeAllBrokerIntervalSettingInServer(String serverName) {
        synchronized (this) {
            if (!initialized) {
                init();
            }
            List<BrokerIntervalSetting> deletedList = new ArrayList<BrokerIntervalSetting>();
            for (int i = 0, n = brokerIntervalSettingList.size(); i < n; i++) {
                BrokerIntervalSetting intervalInfo = brokerIntervalSettingList.get(i);
                if (serverName.equals(intervalInfo.getServerName())) {
                    deletedList.add(intervalInfo);
                }
            }
            brokerIntervalSettingList.removeAll(deletedList);
            saveBrokerIntervals();
        }
    }

    /**
     * Remove broker interval setting
     *
     * @param serverName the server name
     * @param brokerName the broker name
     */
    public void removeBrokerIntervalSetting(String serverName, String brokerName) {
        synchronized (this) {
            if (!initialized) {
                init();
            }
            for (int i = 0, n = brokerIntervalSettingList.size(); i < n; i++) {
                BrokerIntervalSetting intervalInfo = brokerIntervalSettingList.get(i);
                if (serverName.equals(intervalInfo.getServerName())
                        && brokerName.equals(intervalInfo.getBrokerName())) {
                    brokerIntervalSettingList.remove(i);
                    saveBrokerIntervals();
                    break;
                }
            }
        }
    }
}
