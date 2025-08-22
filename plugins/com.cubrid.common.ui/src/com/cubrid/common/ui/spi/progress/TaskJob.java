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
package com.cubrid.common.ui.spi.progress;

import com.cubrid.common.ui.spi.Messages;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * An operation approver that implements the Type Job and executes the task by Type
 * <link>#TaskJobExecutor</link>.
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class TaskJob extends Job {
    private final List<TaskJobExecutor> taskJobExecutorList = new ArrayList<TaskJobExecutor>();
    private boolean isRunning = false;
    private JobFamily jobFamily;

    /**
     * The constructor
     *
     * @param name
     */
    public TaskJob(String name) {
        super(name);
    }

    /**
     * The constructor
     *
     * @param name
     * @param taskJobExecutor
     */
    public TaskJob(String name, TaskJobExecutor taskJobExecutor) {
        super(name);
        if (taskJobExecutor != null) {
            taskJobExecutorList.add(taskJobExecutor);
            addJobChangeListener(taskJobExecutor);
        }
    }

    /**
     * Executes this job. Returns the result of the execution.
     *
     * @param monitor the monitor to be used for reporting progress and responding to cancel. The
     *     monitor is never <code>null</code>
     * @return resulting status of the run. The result must not be <code>null</code>
     */
    protected IStatus run(final IProgressMonitor monitor) {
        monitor.beginTask(Messages.msgRunning, IProgressMonitor.UNKNOWN);
        if (monitor.isCanceled()) {
            canceling();
            return Status.CANCEL_STATUS;
        }
        isRunning = true;
        Thread thread =
                new Thread("Monitoring " + getName()) {
                    public void run() {
                        while (monitor != null && !monitor.isCanceled() && isRunning) {
                            try {
                                sleep(500);
                            } catch (InterruptedException e) {
                            }
                        }
                        if (monitor != null && monitor.isCanceled()) {
                            isRunning = false;
                            canceling();
                        }
                    }
                };
        thread.start();
        IStatus status = Status.OK_STATUS;
        for (TaskJobExecutor taskJobExecutor : taskJobExecutorList) {
            if (status != Status.OK_STATUS) {
                break;
            }
            status = taskJobExecutor.exec(monitor);
        }
        isRunning = false;
        monitor.done();
        return status;
    }

    /**
     * Calls the method of belongTo in the type of JobFamily
     *
     * @param family the family object
     * @return <code>true</code> if it is belonged to the object family; <code>false</code>
     *     otherwise
     */
    public boolean belongsTo(Object family) {
        if (jobFamily == null) {
            return false;
        }
        return jobFamily.belongsTo(family);
    }

    /**
     * Set job family object
     *
     * @param jobFamily the jobFamily to set
     */
    public void setJobFamily(JobFamily jobFamily) {
        this.jobFamily = jobFamily;
    }

    /** Cancel this execution */
    protected void canceling() {
        for (TaskJobExecutor taskJobExecutor : taskJobExecutorList) {
            taskJobExecutor.cancel();
        }
    }

    /**
     * Add task job executor
     *
     * @param taskJobExecutor The TaskJobExecutor
     */
    public void addTaskJobExecutor(TaskJobExecutor taskJobExecutor) {
        if (taskJobExecutor != null) {
            taskJobExecutorList.add(taskJobExecutor);
            addJobChangeListener(taskJobExecutor);
        }
    }
}
