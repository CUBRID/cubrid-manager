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

import java.util.List;
import java.util.Vector;

/**
 * Thread pool manager, can specify the initial count and max count, if max count is less than 0,
 * will not limit the thread count.
 *
 * @author pangqiren
 * @version 1.0 - 2010-8-20 created by pangqiren
 */
public class ThreadPoolManager {

    private int maxCount = -1;
    private Vector<WorkedThread> vector = null;
    private String poolName;

    public ThreadPoolManager(String name, int initCount, int maxCount) {
        this.maxCount = maxCount;
        poolName = name == null ? "" : name;
        vector = new Vector<WorkedThread>(maxCount < 0 ? 20 : maxCount);
        init(initCount);
    }

    public ThreadPoolManager(String name) {
        this(name, 5, -1);
    }

    /**
     * Start the count thread
     *
     * @param initCount int
     */
    private void init(int initCount) {
        for (int i = 0; i < initCount; i++) {
            WorkedThread workedThread = new WorkedThread("Thread" + i + "@" + poolName);
            vector.addElement(workedThread);
            workedThread.start();
        }
    }

    /**
     * Start thread and run <code>java.lang.Runnable#run()</code>
     *
     * @param runnableObj java.lang.Runnable
     * @throws ThreadCountOutOfBoundsException The exception
     */
    public void execute(Runnable runnableObj) throws ThreadCountOutOfBoundsException {
        synchronized (this) {
            WorkedThread workedThread = getIdleThread();
            if (workedThread == null) {
                throw new ThreadCountOutOfBoundsException("The thread pool is full.");
            } else {
                workedThread.addRunnableObj(runnableObj);
                workedThread.setRunning(true);
            }
        }
    }

    /**
     * Start thread and run <code>java.lang.Runnable#run()</code>
     *
     * @param runnableObjList List<Runnable>
     * @param isMultiThread boolean
     * @throws ThreadCountOutOfBoundsException The exception
     */
    public void execute(List<Runnable> runnableObjList, boolean isMultiThread)
            throws ThreadCountOutOfBoundsException {
        synchronized (this) {
            if (isMultiThread) {
                for (Runnable runnableObj : runnableObjList) {
                    WorkedThread workedThread = getIdleThread();
                    if (workedThread == null) {
                        throw new ThreadCountOutOfBoundsException("The thread pool is full.");
                    } else {
                        workedThread.addRunnableObj(runnableObj);
                        workedThread.setRunning(true);
                    }
                }
                return;
            }
            WorkedThread workedThread = getIdleThread();
            if (workedThread == null) {
                throw new ThreadCountOutOfBoundsException("The thread pool is full.");
            } else {
                workedThread.setRunnableObjList(runnableObjList);
                workedThread.setRunning(true);
            }
        }
    }

    /**
     * Get idle thread
     *
     * @return WorkedThread
     */
    private WorkedThread getIdleThread() {
        int i = 0;
        int count = vector.size();
        for (; i < count; i++) {
            WorkedThread workedThread = vector.get(i);
            if (!workedThread.isRunning() && !workedThread.isDisposed()) {
                return workedThread;
            }
        }
        if (maxCount < 0 || i < maxCount) {
            WorkedThread workedThread = new WorkedThread("Thread" + count + "@" + poolName);
            vector.addElement(workedThread);
            workedThread.start();
            return workedThread;
        }
        return null;
    }

    /** Dispose all thread */
    public void disposeAll() {
        for (int i = 0; i < vector.size(); i++) {
            WorkedThread workedThread = vector.get(i);
            workedThread.dispose();
        }
    }
}
