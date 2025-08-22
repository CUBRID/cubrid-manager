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
package com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher;

import com.cubrid.cubridmanager.ui.mondashboard.editor.model.BrokerNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.DatabaseNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HostNode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This type is singleton class which contained all the threads of receiving data from CUBRID server
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-6-22 created by lizhiqiang
 */
public final class DataGeneratorPool {
    private static DataGeneratorPool instance = new DataGeneratorPool();
    private final Map<String, DataGenerator> map = new HashMap<String, DataGenerator>();

    private DataGeneratorPool() {
        // do nothing
    }

    /**
     * Get the instance of the type of DataGeneratorPool
     *
     * @return the instance of DataGeneratorPool
     */
    public static DataGeneratorPool getInstance() {
        return instance;
    }

    /**
     * Get the thread instance of DataGenerator and provide the data provider for every only
     * DataGenerator thread
     *
     * @param generatorName the generator name,generally,it consists of dashboard name and host
     *     server name
     * @param dataProvider IDataProvider
     * @return the instance of DataGenerator
     */
    public DataGenerator getDataGenerator(String generatorName, IDataProvider dataProvider) {
        synchronized (map) {
            for (Map.Entry<String, DataGenerator> entry : map.entrySet()) {
                if (generatorName.equals(entry.getKey())) {
                    return entry.getValue();
                }
            }
            DataGenerator generator = new DataGenerator(generatorName, dataProvider);
            map.put(generatorName, generator);
            generator.setRunflag(true);
            if (!generator.isRunning()) {
                generator.start();
            }
            return generator;
        }
    }

    /**
     * Remove the thread instance of DataGenerator.
     *
     * @param generatorName the generator name
     */
    public void removeDataGenerator(String generatorName) {
        synchronized (map) {
            map.remove(generatorName);
        }
    }

    /**
     * Return whether has connection
     *
     * @param ip String
     * @param port String
     * @param userName String
     * @return boolean
     */
    public boolean isHasConnection(String ip, String port, String userName) {
        Iterator<DataGenerator> it = map.values().iterator();
        while (it.hasNext()) {
            DataGenerator generator = it.next();
            List<DataUpdateListener> listenerList = generator.getListeners();
            for (DataUpdateListener listener : listenerList) {
                HostNode hostNode = null;
                if (listener.getModel() instanceof HostNode) {
                    hostNode = (HostNode) listener.getModel();
                } else if (listener.getModel() instanceof DatabaseNode) {
                    hostNode = ((DatabaseNode) listener.getModel()).getParent();
                } else if (listener.getModel() instanceof BrokerNode) {
                    hostNode = ((BrokerNode) listener.getModel()).getParent();
                }
                if (hostNode != null
                        && hostNode.isConnected()
                        && ip.equals(hostNode.getIp())
                        && port.equals(hostNode.getPort())
                        && userName.equals(hostNode.getUserName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
