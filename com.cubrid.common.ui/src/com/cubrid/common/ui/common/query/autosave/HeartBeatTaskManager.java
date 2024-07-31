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
package com.cubrid.common.ui.common.query.autosave;

import com.cubrid.common.core.util.LogUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import org.slf4j.Logger;

/**
 * Application Heart Beat Task Manager
 *
 * @author Kevin.Wang
 * @version 1.0 - Jun 4, 2012 created by Kevin.Wang
 */
public class HeartBeatTaskManager extends TimerTask { // FIXME logic code move to core module

    private static final Logger LOGGER = LogUtil.getLogger(HeartBeatTaskManager.class);

    /*Heart beat time, The unit is second*/
    public static final int BEAT_TIME = 6000;
    private static HeartBeatTaskManager instance;

    /*All the task list*/
    private List<IHeartBeatTask> tasks = new ArrayList<IHeartBeatTask>();

    /** The constructor */
    private HeartBeatTaskManager() {
        tasks.add(CheckQueryEditorTask.getInstance());
    }

    /**
     * Get the ApplicationHeartBeat
     *
     * @return
     */
    public static HeartBeatTaskManager getInstance() {
        synchronized (HeartBeatTaskManager.class) {
            if (instance == null) {
                instance = new HeartBeatTaskManager();
            }
        }
        return instance;
    }

    public void run() {
        synchronized (HeartBeatTaskManager.class) {
            for (IHeartBeatTask task : tasks) {
                task.beat();
            }
        }
    }

    /**
     * Add a task
     *
     * @param task
     */
    public void addTask(IHeartBeatTask task) {
        if (task == null) {
            return;
        }
        synchronized (HeartBeatTaskManager.class) {
            tasks.add(task);
        }
    }

    /**
     * Remove the task
     *
     * @param task
     */
    public void removeTask(IHeartBeatTask task) {
        if (task == null) {
            return;
        }
        synchronized (HeartBeatTaskManager.class) {
            tasks.remove(task);
        }
    }

    /** Clear all the task */
    public void clearAllTask() {
        synchronized (HeartBeatTaskManager.class) {
            tasks.clear();
        }
    }

    /** Stop the task */
    public boolean cancel() {
        synchronized (HeartBeatTaskManager.class) {
            for (IHeartBeatTask task : tasks) {
                task.stop();
            }
        }
        return super.cancel();
    }
}
