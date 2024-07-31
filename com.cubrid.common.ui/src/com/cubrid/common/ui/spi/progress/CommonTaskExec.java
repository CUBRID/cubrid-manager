/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.spi.progress;

import com.cubrid.common.core.task.AbstractUITask;
import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.spi.IMessageHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * A common type which extends the type TaakExecutor and overrides the method exec.Generally ,it can
 * be used in an action or other type of which there is no dialog
 *
 * @author lizhiqiang
 * @version 1.0 - 2009-4-16 created by lizhiqiang
 */
public class CommonTaskExec extends TaskExecutor {

    //	private ITaskExecutorInterceptor interceptor;
    private IStatus taskExeStatus;
    private final String taskName;
    private IMessageHandler messageHandler;

    //	/**
    //	 * The constructor
    //	 *
    //	 * @param taskName
    //	 * @param interceptor
    //	 */
    //	public CommonTaskExec(String taskName, ITaskExecutorInterceptor interceptor) {
    //		this.taskName = taskName;
    //		this.interceptor = interceptor;
    //	}

    /**
     * The constructor
     *
     * @param taskName
     */
    public CommonTaskExec(String taskName, IMessageHandler messageHandler) {
        this.taskName = taskName;
        this.messageHandler = messageHandler;
    }

    /**
     * The constructor
     *
     * @param taskName
     */
    public CommonTaskExec(String taskName) {
        this.taskName = taskName;
    }

    /**
     * Execute the task
     *
     * @param monitor the monitor
     * @return <code>true</code>if success;<code>false</code> otherwise
     */
    public boolean exec(final IProgressMonitor monitor) {
        if (monitor.isCanceled()) {
            return false;
        }
        if (taskName != null) {
            monitor.beginTask(taskName, IProgressMonitor.UNKNOWN);
        }
        for (final ITask task : taskList) {
            if (task instanceof AbstractUITask) {
                ((AbstractUITask) task).execute(monitor);
            } else {
                task.execute();
            }

            String errorMsg = task.getErrorMsg();
            if (messageHandler != null) {
                errorMsg = messageHandler.translate(task.getErrorMsg());
            }

            if (openErrorBox(null, errorMsg, monitor)) {
                return false;
            }
            //			Display.getDefault().syncExec(new Runnable() {
            //				public void run() {
            //					if (interceptor != null) {
            //						taskExeStatus = interceptor.postTaskFinished(task);
            //					}
            //				}
            //			});
            if (taskExeStatus != null && taskExeStatus == Status.CANCEL_STATUS) {
                return false;
            }
            if (taskExeStatus != null && taskExeStatus != Status.OK_STATUS) {
                errorMsg = taskExeStatus.getMessage();
                if (messageHandler != null) {
                    errorMsg = messageHandler.translate(taskExeStatus.getMessage());
                }
                openErrorBox(null, errorMsg, monitor);
                return false;
            }
            if (monitor.isCanceled()) {
                return false;
            }
        }
        //		Display.getDefault().syncExec(new Runnable() {
        //			public void run() {
        //				if (interceptor != null) {
        //					interceptor.completeAll();
        //				}
        //			}
        //		});
        return true;
    }
}
