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
package com.cubrid.common.ui.spi.model;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.spi.util.CommonUITool;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

/**
 * CUBRID Node loader abstract implementation
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public abstract class CubridNodeLoader implements ICubridNodeLoader {
    private boolean isLoaded = false;
    private int level = FIRST_LEVEL;
    public static final String USERS_FOLDER_ID = "Users";
    /**
     * Return whether it has been loaded
     *
     * @return <code>true</code> if it is loaded;<code>false</code> otherwise
     */
    public boolean isLoaded() {
        return isLoaded;
    }

    /**
     * Set loaded status
     *
     * @param isLoaded whether it is loaded
     */
    public void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    /**
     * Set loaded level
     *
     * @param level the loaded depth
     */
    public void setLevel(int level) {
        this.level = level;
        if (level != FIRST_LEVEL && level != DEFINITE_LEVEL) {
            this.level = FIRST_LEVEL;
        }
    }

    /**
     * Get loaded level
     *
     * @return the loaded depth
     */
    public int getLevel() {
        return level;
    }

    /**
     * Monitoring cancel operation
     *
     * @param monitor IProgressMonitor
     * @param tasks ITask[]
     */
    public void monitorCancel(final IProgressMonitor monitor, final ITask[] tasks) {
        String name = getClass().getName() == null ? "loading" : getClass().getName();
        Thread thread =
                new Thread(name + " monitoring thread") {
                    public void run() {
                        while (!monitor.isCanceled() && !isLoaded()) {
                            try {
                                sleep(WAIT_TIME);
                            } catch (InterruptedException e) {
                            }
                        }
                        if (monitor.isCanceled() && tasks != null && tasks.length > 0) {
                            for (ITask task : tasks) {
                                if (task != null) {
                                    task.cancel();
                                }
                            }
                        }
                    }
                };
        thread.start();
    }

    /**
     * Open the error box
     *
     * @param msg the error message
     */
    protected void openErrorBox(final String msg) {
        Display display = Display.getDefault();
        display.syncExec(
                new Runnable() {
                    public void run() {
                        if (msg != null && msg.trim().length() > 0) {
                            CommonUITool.openErrorBox(msg);
                        }
                    }
                });
    }
}
