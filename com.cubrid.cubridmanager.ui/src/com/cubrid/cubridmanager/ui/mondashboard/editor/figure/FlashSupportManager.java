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
package com.cubrid.cubridmanager.ui.mondashboard.editor.figure;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.eclipse.swt.widgets.Display;

/**
 * FickerManager Description
 *
 * @author SC13425
 * @version 1.0 - 2010-8-30 created by SC13425
 */
public class FlashSupportManager {

    private final Timer timer = new Timer("Flickr thread");

    /**
     * Flicker Runner.
     *
     * @author SC13425
     * @version 1.0 - 2010-8-30 created by SC13425
     */
    private static class FlickerRunner implements Runnable {

        private final List<FlashSupport> flickerFigures = new ArrayList<FlashSupport>();

        /** flicker */
        public void run() {
            for (FlashSupport figure : flickerFigures) {
                figure.flash();
            }
        }
    }

    final FlickerRunner runnable = new FlickerRunner();

    private static FlashSupportManager flickerManager = new FlashSupportManager();

    /**
     * Get the singleton FlickManager instance.
     *
     * @return FlickerManager
     */
    public static FlashSupportManager getInstance() {
        return flickerManager;
    }

    /** Constructor. */
    private FlashSupportManager() {
        timer.schedule(
                new TimerTask() {
                    public void run() {
                        Display.getDefault().asyncExec(runnable);
                    }
                },
                0,
                600);
    }

    /**
     * Add FlickerSupport to flicker list.
     *
     * @param figure FlickerSupport
     */
    public void add(FlashSupport figure) {
        synchronized (runnable.flickerFigures) {
            if (!runnable.flickerFigures.contains(figure)) {
                runnable.flickerFigures.add(figure);
            }
        }
    }

    /**
     * Remove FlickerSupport from flicker list.
     *
     * @param figure FlickerSupport
     */
    public void remove(FlashSupport figure) {
        synchronized (runnable.flickerFigures) {
            if (runnable.flickerFigures.contains(figure)) {
                runnable.flickerFigures.remove(figure);
            }
        }
    }
}
