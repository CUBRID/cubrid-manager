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
package com.cubrid.cubridmanager.ui.common.action;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.progress.JobFamily;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.ui.common.Messages;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * Quit CUBRID Manager application
 *
 * @author pangqiren
 * @version 1.0 - 2009-5-20 created by pangqiren
 */
public class QuitAction extends Action {
    private static final Logger LOGGER = LogUtil.getLogger(QuitAction.class);
    public static final String ID = QuitAction.class.getName();
    /**
     * The constructor
     *
     * @param text
     */
    public QuitAction(String text) {
        this.setId(ID);
        setText(text);
        setToolTipText(text);
    }

    /** Quit CUBRID Manager */
    public void run() {
        boolean hasJobRunning = false;
        final JobFamily jobFamily = new JobFamily();
        jobFamily.setServerName(JobFamily.ALL_SERVER);
        Job[] jobs = Job.getJobManager().find(jobFamily);
        if (jobs.length > 0) {
            hasJobRunning = true;
        }
        boolean isExit = false;
        if (hasJobRunning) {
            isExit =
                    CommonUITool.openConfirmBox(
                            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                            Messages.msgExistConfirmWithJob);
            if (isExit) {
                Display.getDefault()
                        .asyncExec(
                                new Runnable() {
                                    public void run() {
                                        try {
                                            Job.getJobManager().cancel(jobFamily);
                                        } catch (Exception ignored) {
                                            LOGGER.error(ignored.getMessage(), ignored);
                                        }
                                    }
                                });
            }
        } else {
            isExit =
                    CommonUITool.openConfirmBox(
                            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                            Messages.msgExistConfirm);
        }
        if (isExit) {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().close();
        }
    }
}
