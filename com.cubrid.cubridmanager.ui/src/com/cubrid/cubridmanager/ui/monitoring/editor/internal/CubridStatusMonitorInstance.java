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
package com.cubrid.cubridmanager.ui.monitoring.editor.internal;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.slf4j.Logger;

/**
 * This type is responsible for loading the instance of StatusMonInstanceData
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-4-1 created by lizhiqiang
 */
public final class CubridStatusMonitorInstance {

    private static final Logger LOGGER = LogUtil.getLogger(CubridStatusMonitorInstance.class);
    private static final CubridStatusMonitorInstance INSTANCE = new CubridStatusMonitorInstance();
    private final Map<String, StatusMonInstanceData> map =
            new HashMap<String, StatusMonInstanceData>();
    private static final byte[] BYTE_ARRAY_DEFAULT_DEFAULT = new byte[0];

    // Constructor
    private CubridStatusMonitorInstance() {
        // do nothing
    }

    /**
     * Get the sole instance of this type.
     *
     * @return the sole instance of this type
     */
    public static CubridStatusMonitorInstance getInstance() {
        return INSTANCE;
    }

    /**
     * Add a new instance of StatusMonInstanceData into map
     *
     * @param name the name as new added instance of StatusMonInstanceData
     * @param data the instance of StatusMonInstanceData
     * @return false if there is the same key in the map, true or else.
     */
    public boolean addData(String name, StatusMonInstanceData data) {
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (key.equals(name)) {
                return false;
            }
        }
        map.put(name, data);
        return true;
    }

    /**
     * Update a exist instance of StatusMonInstanceData into map
     *
     * @param name the name as new added instance of StatusMonInstanceData
     * @param data the instance of StatusMonInstanceData
     */
    public void updateData(String name, StatusMonInstanceData data) {
        map.put(name, data);
    }

    /**
     * Remove a instance from the instance of StatusMonInstanceData
     *
     * @param name the name which is prepared for removing from instance of StatusMonInstanceData
     */
    public void removeData(String name) {
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (key.equals(name)) {
                map.remove(key);
            }
        }
    }

    /**
     * Get the value from the instance based on the given key
     *
     * @param name the name which is the key
     * @return the instance of StatusMonInstanceData
     */
    public StatusMonInstanceData getData(String name) {
        if (name == null || "".equals(name)) {
            return null;
        }
        for (Map.Entry<String, StatusMonInstanceData> entry : map.entrySet()) {
            String key = entry.getKey();
            if (name.equals(key)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Get the map which includes the instances of StatusMonInstanceData
     *
     * @return the copy of map
     */
    public Map<String, StatusMonInstanceData> getDataMap() {
        Map<String, StatusMonInstanceData> newMap = new HashMap<String, StatusMonInstanceData>();
        for (Map.Entry<String, StatusMonInstanceData> entry : map.entrySet()) {
            String key = entry.getKey();
            StatusMonInstanceData value = entry.getValue();
            newMap.put(key, value);
        }
        return newMap;
    }

    /**
     * Save the info of StatusMonInstanceData relevant to key into preference
     *
     * @param key the key relevant to saving instance of StatusMonInstanceData
     */
    public void saveSetting(String key) {
        synchronized (this) {
            StatusMonInstanceData data = map.get(key);
            ObjectOutputStream objectOutputStream;
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            try {
                objectOutputStream = new ObjectOutputStream(byteStream);
                objectOutputStream.writeObject(data);
                objectOutputStream.close();

                byte[] bytes = byteStream.toByteArray();
                IEclipsePreferences preference =
                        new InstanceScope().getNode(CubridManagerUIPlugin.PLUGIN_ID);
                preference.putByteArray(key, bytes);
                preference.flush();
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage());
            }
        }
    }

    /**
     * Get the info of StatusMonInstanceData relevant to key from preference
     *
     * @param key the key relevant to saving instance of StatusMonInstanceData
     * @return the instance of StatusMonInstanceData
     */
    public StatusMonInstanceData loadSetting(String key) {
        StatusMonInstanceData data = null;
        synchronized (this) {
            IEclipsePreferences preference =
                    new InstanceScope().getNode(CubridManagerUIPlugin.PLUGIN_ID);
            byte[] bytes = preference.getByteArray(key, BYTE_ARRAY_DEFAULT_DEFAULT);
            if (bytes.length > 0) {
                try {
                    ObjectInputStream inputStream =
                            new ObjectInputStream(new ByteArrayInputStream(bytes));
                    data = (StatusMonInstanceData) inputStream.readObject();
                    inputStream.close();
                } catch (Exception ex) {
                    LOGGER.error(ex.getMessage());
                }
            }
        }
        return data;
    }

    /**
     * Remove the info of StatusMonInstanceData relevant to key from preference
     *
     * @param key the key relevant to saving instance of StatusMonInstanceData
     */
    public void removeSetting(String key) {
        synchronized (this) {
            IEclipsePreferences preference =
                    new InstanceScope().getNode(CubridManagerUIPlugin.PLUGIN_ID);
            preference.remove(key);
            try {
                preference.flush();
            } catch (BackingStoreException ex) {
                LOGGER.error(ex.getMessage());
            }
        }
    }
}
