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

import com.cubrid.common.core.task.ITask;
import java.util.ArrayList;
import java.util.List;

/**
 * This task group is a compound task group,these tasks finish a complex function together.
 *
 * @author pangqiren
 * @version 1.0 - 2009-11-24 created by pangqiren
 */
public class TaskGroup {

    private String groupName;
    private List<ITask> taskList;
    // the target object
    private Object target;

    /**
     * The constructor
     *
     * @param name
     */
    public TaskGroup(String name) {
        this.groupName = name;
    }

    /**
     * Return group name
     *
     * @return the group name
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * Set group name
     *
     * @param groupName the group name
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * Get task list
     *
     * @return the task list
     */
    public List<ITask> getTaskList() {
        return taskList;
    }

    /**
     * Set task list
     *
     * @param taskList the task list
     */
    public void setTaskList(List<ITask> taskList) {
        this.taskList = taskList;
    }

    /**
     * Add a task
     *
     * @param task the task
     */
    public void addTask(ITask task) {
        if (task == null) {
            return;
        }
        if (taskList == null) {
            taskList = new ArrayList<ITask>();
        }
        if (!taskList.contains(task)) {
            taskList.add(task);
        }
    }

    /**
     * Get target object
     *
     * @return the target object
     */
    public Object getTarget() {
        return target;
    }

    /**
     * Set target object
     *
     * @param target the target object
     */
    public void setTarget(Object target) {
        this.target = target;
    }
}
