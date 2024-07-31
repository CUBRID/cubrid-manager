/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *  - Neither the name of the <ORGANIZATION> nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
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
package com.cubrid.common.ui.query.editor;

import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.dialog.InformationWindow;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.ui.PlatformUI;

/**
 * The QueryEditor Information Window Manager
 *
 * @author Kevin.Wang
 */
public class InfoWindowManager {
    private static InfoWindowManager instance;
    private static InformationWindow informationWindow;

    private InfoWindowManager() {
        Set<String> keyWords = new HashSet<String>();
        keyWords.add(Messages.msgCalcInfoCount);
        keyWords.add(Messages.msgCalcInfoSUM);
        keyWords.add(Messages.msgCalcInfoAVG);

        informationWindow =
                new InformationWindow(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
        informationWindow.open();
        informationWindow.updateLocation();
    }

    /**
     * Get the instance
     *
     * @return
     */
    public static InfoWindowManager getInstance() {
        synchronized (InfoWindowManager.class) {
            if (instance == null
                    && PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() != null
                    && !PlatformUI.getWorkbench()
                            .getActiveWorkbenchWindow()
                            .getShell()
                            .isDisposed()) {
                instance = new InfoWindowManager();
            }
        }
        return instance;
    }

    /**
     * Set the information visible
     *
     * @param isVisible
     */
    public static synchronized void setVisible(boolean isVisible) {
        if (informationWindow != null
                && informationWindow.getShell() != null
                && !informationWindow.getShell().isDisposed()) {
            informationWindow.getShell().setVisible(isVisible);
        }
    }

    public static boolean isVisible() {
        if (informationWindow != null
                && informationWindow.getShell() != null
                && !informationWindow.getShell().isDisposed()) {
            return informationWindow.getShell().isVisible();
        }

        return false;
    }

    /**
     * Update the content
     *
     * @param queryResultTableCalcInfo
     */
    public void updateContent(IInformationWindowNotifier notifier) {
        if (notifier != null) {
            informationWindow.updateInfo(notifier.getMessages(), notifier.getDecoratorWords());
        } else {
            informationWindow.updateInfo(null, null);
        }
    }

    /** Dispose */
    public static synchronized void dispose() {
        if (informationWindow != null
                && informationWindow.getShell() != null
                && !informationWindow.getShell().isDisposed()) {
            informationWindow.close();
        }
        informationWindow = null;
        instance = null;
    }
}
