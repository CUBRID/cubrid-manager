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
package com.cubrid.cubridmanager.ui.broker.editor.internal;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.cubridmanager.core.common.xml.IXMLMemento;
import com.cubrid.cubridmanager.core.common.xml.XMLMemento;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import java.io.ByteArrayInputStream;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.slf4j.Logger;

/**
 * This type is responsible for recording the setting result that need showing in the broker
 * environment status view BrokerEnvStatusSettingResult
 *
 * @author Administrator
 * @version 1.0 - 2010-3-3 created by Administrator
 */
public final class BrokerTblColumnSetHelp {

    private static final Logger LOGGER = LogUtil.getLogger(BrokerTblColumnSetHelp.class);
    private static BrokerTblColumnSetHelp instance;
    private static Object[] obj = new Object[0];

    /**
     * An enumeration used for saving or loading
     *
     * @author lizhiqiang
     * @version 1.0 - 2010-3-15 created by lizhiqiang
     */
    public enum StatusColumn {
        BrokerEnvStatusColumn,
        BrokerStatusAsColumn,
        BrokerStatusJqColumn,
        BrokerStatusBasicColumn
    };

    /**
     * Get the sole instance
     *
     * @return the instance of this type
     */
    public static BrokerTblColumnSetHelp getInstance() {
        synchronized (obj) {
            if (instance == null) {
                instance = new BrokerTblColumnSetHelp();
            }
        }
        return instance;
    }

    /**
     * Load broker interval settings from plugin preference
     *
     * @param <T> the generic type which is the sub type of IColumnSetting
     * @param statusColumn the instance of StatusColumn enumeration
     * @param ts the instance of generic array
     */
    public <T extends IColumnSetting> void loadSetting(StatusColumn statusColumn, T[] ts) {
        synchronized (this) {
            IEclipsePreferences preference =
                    new InstanceScope().getNode(CubridManagerUIPlugin.PLUGIN_ID);
            String xmlString = preference.get(statusColumn.name(), "");
            if (xmlString != null && xmlString.length() > 0) {
                try {
                    ByteArrayInputStream in = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));
                    IXMLMemento memento = XMLMemento.loadMemento(in);

                    IXMLMemento[] children = memento.getChildren(statusColumn.name());
                    for (IXMLMemento child : children) {
                        for (T t : ts) {
                            t.setValue(child.getInteger(t.getNick()));
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
            }
        }
    }

    /**
     * Save broker interval to plugin preference
     *
     * @param <T> the generic type which is the sub type of IColumnSetting
     * @param statusColumn the instance of StatusColumn enumeration
     * @param ts the instance of generic array
     */
    public <T extends IColumnSetting> void saveSetting(StatusColumn statusColumn, T[] ts) {
        synchronized (this) {
            try {
                XMLMemento memento = XMLMemento.createWriteRoot(statusColumn.name());
                IXMLMemento child = memento.createChild(statusColumn.name());
                for (T column : ts) {
                    child.putString(column.getNick(), String.valueOf(column.getValue()));
                }
                String xmlString = memento.saveToString();
                IEclipsePreferences preference =
                        new InstanceScope().getNode(CubridManagerUIPlugin.PLUGIN_ID);
                preference.put(statusColumn.name(), xmlString);
                preference.flush();
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }
    }
}
