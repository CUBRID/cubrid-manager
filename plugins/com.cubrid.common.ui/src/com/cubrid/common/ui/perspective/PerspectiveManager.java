/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.perspective;

import com.cubrid.common.core.util.ApplicationType;
import com.cubrid.common.core.util.ApplicationUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.spi.persist.PersistUtils;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

/**
 * PerspectiveManager Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2014-4-18 created by Kevin.Wang
 */
public class PerspectiveManager {
    public static final String KEY_SELECTED_PERSPECTIVE = "selected_perspective";

    private static final Map<String, IPerspectiveChangedListener> listenerMap =
            new HashMap<String, IPerspectiveChangedListener>();
    private static PerspectiveManager instance = null;
    private String activePerspectiveId;
    private boolean isInitial = false;

    private PerspectiveManager() {}

    /**
     * Return the only PerspectiveManager
     *
     * @return PerspectiveManager
     */
    public static PerspectiveManager getInstance() {
        synchronized (PerspectiveManager.class) {
            if (instance == null) {
                instance = new PerspectiveManager();
            }
        }
        return instance;
    }

    public synchronized void addPerspectiveListener(IPerspectiveChangedListener listener) {
        listenerMap.put(listener.getPerspectiveId(), listener);
    }

    public synchronized void removePerspectiveListener(IPerspectiveChangedListener listener) {
        listenerMap.remove(listener.getPerspectiveId());
    }

    public synchronized void firePerspectiveChanged(String sourceId, String targetId) {
        if (!isInitial || !sourceId.equals(targetId)) {
            isInitial = true;

            PerspectiveChangeEvent event = new PerspectiveChangeEvent(sourceId, targetId);

            IPerspectiveChangedListener listenrHide = listenerMap.get(sourceId);
            if (listenrHide != null) {
                listenrHide.hidePerspectiveHide(event);
            }

            IPerspectiveChangedListener listenrShow = listenerMap.get(targetId);
            if (listenrShow != null) {
                listenrShow.showPerspective(event);
            }

            for (IPerspectiveChangedListener listener : listenerMap.values()) {
                listener.perspectiveChanged(event);
            }
            setSelectedPerspective(targetId);
        }
    }

    public synchronized String getCurrentPerspectiveId() {
        if (activePerspectiveId != null) {
            return activePerspectiveId;
        }

        if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() == null) {
            return "";
        }

        return PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow()
                .getActivePage()
                .getPerspective()
                .getId();
    }

    public synchronized void openPerspective(String perspectiveId) {
        try {
            if (activePerspectiveId == null || activePerspectiveId.isEmpty()) {
                activePerspectiveId = PerspectiveManager.getInstance().getCurrentPerspectiveId();
            }
            String oldPerspectiveId = activePerspectiveId;
            activePerspectiveId = perspectiveId;
            PlatformUI.getWorkbench()
                    .showPerspective(
                            perspectiveId, PlatformUI.getWorkbench().getActiveWorkbenchWindow());
            PerspectiveManager.getInstance()
                    .firePerspectiveChanged(oldPerspectiveId, activePerspectiveId);
        } catch (WorkbenchException e) {
            e.printStackTrace();
        }
    }

    public synchronized ApplicationType getCurrentMode() {
        String id = getCurrentPerspectiveId();
        if (IPerspectiveConstance.CM_PERSPECTIVE_ID.equals(id)) {
            return ApplicationType.CUBRID_MANAGER;
        } else if (IPerspectiveConstance.CQB_PERSPECTIVE_ID.equals(id)) {
            return ApplicationType.CUBRID_QUERY_BROWSER;
        }

        return ApplicationUtil.getApplicationType();
    }

    public boolean isManagerMode() {
        return ApplicationType.CUBRID_MANAGER.equals(getCurrentMode());
    }

    public String getSelectedPerspective() {
        return PersistUtils.getGlobalPreferenceValue(
                CommonUIPlugin.PLUGIN_ID, KEY_SELECTED_PERSPECTIVE);
    }

    public void setSelectedPerspective(String id) {
        PersistUtils.setGlobalPreferenceValue(
                CommonUIPlugin.PLUGIN_ID, KEY_SELECTED_PERSPECTIVE, id);
    }
}
