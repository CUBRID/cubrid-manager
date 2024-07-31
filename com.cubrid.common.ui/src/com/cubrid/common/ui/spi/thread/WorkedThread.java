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
package com.cubrid.common.ui.spi.thread;

import java.util.ArrayList;
import java.util.List;

/**
 * Working thread object, after start thread by this thread object, this thread is always running
 * until dispose
 *
 * @author pangqiren
 * @version 1.0 - 2010-8-21 created by pangqiren
 */
public class WorkedThread extends Thread {

    private boolean isRunning = false;
    private List<Runnable> runnableObjList = null;
    private boolean isDisposed = false;

    public WorkedThread() {
        // empty
    }

    public WorkedThread(String threadName) {
        super(threadName);
    }

    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Set running
     *
     * @param isRunning boolean
     */
    public void setRunning(boolean isRunning) {
        synchronized (this) {
            this.isRunning = isRunning;
            if (this.isRunning) {
                notifyAll();
            }
        }
    }

    public List<Runnable> getRunnableObjList() {
        return runnableObjList;
    }

    public void setRunnableObjList(List<Runnable> runnableObjList) {
        this.runnableObjList = runnableObjList;
    }

    /**
     * Add Runnable object
     *
     * @param runnableObj Runnable
     */
    public void addRunnableObj(Runnable runnableObj) {
        if (runnableObjList == null) {
            runnableObjList = new ArrayList<Runnable>();
        }
        runnableObjList.add(runnableObj);
    }

    /** @see java.lang.Thread#run() */
    public void run() {
        synchronized (this) {
            try {
                while (!isDisposed) {
                    if (isRunning) {
                        if (runnableObjList != null && !runnableObjList.isEmpty()) {
                            for (Runnable runnableObj : runnableObjList) {
                                runnableObj.run();
                            }
                        }
                        setRunnableObjList(null);
                        isRunning = false;
                    } else {
                        wait();
                    }
                }
            } catch (InterruptedException e) {
                isRunning = false;
            }
            isRunning = false;
        }
    }

    /** Dispose this thread */
    public void dispose() {
        isDisposed = true;
        setRunning(true);
    }

    /**
     * Return whether this thread was disposed
     *
     * @return boolean
     */
    public boolean isDisposed() {
        return this.isDisposed;
    }
}
