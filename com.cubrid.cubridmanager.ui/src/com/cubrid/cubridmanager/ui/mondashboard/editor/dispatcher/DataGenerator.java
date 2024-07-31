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

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.thread.ThreadPoolManager;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.BrokerNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.DatabaseNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HostNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;

/**
 * This class is responsible for getting data from server and update related
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-6-22 created by lizhiqiang
 */
public class DataGenerator extends Thread {
    private static final Logger LOGGER = LogUtil.getLogger(DataGenerator.class);
    private int interval = 1000;
    private boolean runflag;
    private final List<DataUpdateListener> listeners =
            Collections.synchronizedList(new ArrayList<DataUpdateListener>());
    private boolean isRunning;
    private final IDataProvider dataProvider;
    private final ThreadPoolManager threadPoolManager;

    public DataGenerator(String name, IDataProvider provider) {
        super(name);
        dataProvider = provider;
        threadPoolManager = new ThreadPoolManager(name);
        dataProvider.setDataGenerator(this);
    }

    /** Thread run method */
    public void run() {
        isRunning = true;
        while (runflag) {
            final DataChangedEvent dataChangedEvent = dataProvider.getUpdateValue();
            Display.getDefault()
                    .syncExec(
                            new Runnable() {
                                public void run() {
                                    if (dataProvider.isAllowUpdate()) {
                                        fireUpdate(dataChangedEvent);
                                    }
                                }
                            });

            try {
                Thread.sleep(interval);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    /**
     * Set the field of runflag
     *
     * @param runflag a flag mean whether allows update
     */
    public void setRunflag(boolean runflag) {
        this.runflag = runflag;
    }

    /**
     * Call the instance of DataUpdateListener to perform update.
     *
     * @param dataChangedEvent which includes the updated data
     */
    public void fireUpdate(DataChangedEvent dataChangedEvent) {
        synchronized (listeners) {
            for (DataUpdateListener listener : listeners) {
                listener.performUpdate(dataChangedEvent);
            }
        }
    }

    /**
     * add a instance of DataUpdateListener to make it can be update
     *
     * @param listener an instance of DataUpdateListener
     */
    public void addDataUpdateListener(DataUpdateListener listener) {
        synchronized (listeners) {
            if (!listeners.contains(listener)) {
                listeners.add(listener);
            }
        }
    }

    /**
     * remove a instance of DataUpdateListener to make it can be update
     *
     * @param listener an instance of DataUpdateListener
     */
    public void removeDataUpdateListener(DataUpdateListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
            if (listeners.isEmpty()) {
                setRunflag(false);
                HostNode hostNode = null;
                if (listener.getModel() instanceof HostNode) {
                    hostNode = (HostNode) listener.getModel();
                } else if (listener.getModel() instanceof DatabaseNode) {
                    hostNode = ((DatabaseNode) listener.getModel()).getParent();
                } else if (listener.getModel() instanceof BrokerNode) {
                    hostNode = ((BrokerNode) listener.getModel()).getParent();
                }
                if (hostNode != null) {
                    hostNode.setConnecting(false);
                }
                getThreadPoolManager().disposeAll();
                DataGeneratorPool pool = DataGeneratorPool.getInstance();
                pool.removeDataGenerator(getName());
            }
        }
    }

    /**
     * Get the interval value, which is the update frequency.
     *
     * @return the interval
     */
    public int getInterval() {
        return interval;
    }

    /**
     * Set the interval value, which is the update frequency.
     *
     * @param interval the interval to set
     */
    public void setInterval(int interval) {
        this.interval = interval;
    }

    /**
     * Get listeners copy, in case of concurrent modify and get
     *
     * @return the listeners
     */
    public List<DataUpdateListener> getListeners() {
        synchronized (listeners) {
            List<DataUpdateListener> listenerList = new ArrayList<DataUpdateListener>();
            listenerList.addAll(listeners);
            return listenerList;
        }
    }

    /**
     * Whether this loop is running
     *
     * @return the runflag
     */
    public boolean isRunflag() {
        return runflag;
    }

    /**
     * Whether this thread is running
     *
     * @return the isRunning
     */
    public boolean isRunning() {
        return isRunning;
    }

    public ThreadPoolManager getThreadPoolManager() {
        return threadPoolManager;
    }
}
