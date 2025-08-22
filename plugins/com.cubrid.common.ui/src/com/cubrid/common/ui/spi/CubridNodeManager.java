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
package com.cubrid.common.ui.spi;

import com.cubrid.common.ui.perspective.PerspectiveManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.ICubridNodeChangedListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.swt.widgets.Display;

/**
 * This class is for managing all CUBRID Node in navigator tree
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public final class CubridNodeManager {

    private static CubridNodeManager instance;
    /*PerspectiveID, listenerlist*/
    private final Map<String, List<ICubridNodeChangedListener>> cubridNodeChangeListeners =
            new HashMap<String, List<ICubridNodeChangedListener>>();

    private CubridNodeManager() {}

    /**
     * Return the only CUBRID Node manager
     *
     * @return CubridNodeManager
     */
    public static CubridNodeManager getInstance() {
        synchronized (CubridNodeManager.class) {
            if (instance == null) {
                instance = new CubridNodeManager();
                instance.addCubridNodeChangeListener(ConnectionKeepAliveHandler.getInstance());
            }
        }
        return instance;
    }

    /**
     * Add CUBRID node object changed listener
     *
     * @param listener the ICubridNodeChangedListener object
     */
    public void addCubridNodeChangeListener(ICubridNodeChangedListener listener) {
        String perspectiveId = PerspectiveManager.getInstance().getCurrentPerspectiveId();
        List<ICubridNodeChangedListener> list = cubridNodeChangeListeners.get(perspectiveId);
        if (list == null) {
            list = new ArrayList<ICubridNodeChangedListener>();
            cubridNodeChangeListeners.put(perspectiveId, list);
        }
        if (!list.contains(listener)) {
            list.add(listener);
        }
    }

    /**
     * Remove CUBRID node object changed listener
     *
     * @param listener the ICubridNodeChangedListener object
     */
    public void removeCubridNodeChangeListener(ICubridNodeChangedListener listener) {
        String perspectiveId = PerspectiveManager.getInstance().getCurrentPerspectiveId();
        List<ICubridNodeChangedListener> list = cubridNodeChangeListeners.get(perspectiveId);
        if (list != null) {
            list.remove(listener);
        }
    }

    /**
     * Fire CUBRID node object changed event to all added listeners
     *
     * @param event the CubridNodeChangedEvent object
     */
    public void fireCubridNodeChanged(final CubridNodeChangedEvent event) {
        String perspectiveId = PerspectiveManager.getInstance().getCurrentPerspectiveId();
        List<ICubridNodeChangedListener> list = cubridNodeChangeListeners.get(perspectiveId);
        if (list != null) {
            for (int i = 0; i < list.size(); ++i) {
                final ICubridNodeChangedListener listener =
                        (ICubridNodeChangedListener) list.get(i);
                Display.getDefault()
                        .asyncExec(
                                new Runnable() {
                                    public void run() {
                                        listener.nodeChanged(event);
                                    }
                                });
            }
        }
    }
}
