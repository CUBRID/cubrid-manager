/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.core.task;

import java.util.HashMap;
import java.util.Map;

/**
 * A abstract class which implements the interface ITask.Subclass can extends this class to fulfill
 * the concrete task
 *
 * @author pangqiren
 * @version 1.0 - 2009-12-23 created by pangqiren
 */
public abstract class AbstractTask implements ITask, Runnable {

    protected String taskName = "";
    protected String errorMsg = null;
    protected String warningMsg = null;
    protected boolean isDone = false;

    // data map which store a lot of data values
    protected Map<Object, Object> dataMap = new HashMap<Object, Object>();

    /**
     * Get error message after this task execute.if it is null, this task is ok, or it has error
     *
     * @return String
     */
    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    /**
     * Get warning message after this task execute
     *
     * @return String
     */
    public String getWarningMsg() {
        return warningMsg;
    }

    public void setWarningMsg(String warningMsg) {
        this.warningMsg = warningMsg;
    }

    public String getTaskname() {
        return taskName;
    }

    public void setTaskname(String taskname) {
        this.taskName = taskname;
    }

    /**
     * Store a lot of values
     *
     * @param key String
     * @param value String
     */
    public void putData(Object key, Object value) {
        dataMap.put(key, value);
    }

    /**
     * Get stored value by key
     *
     * @param key Object
     * @return Object
     */
    public Object getData(Object key) {
        return dataMap.get(key);
    }

    /** @see java.lang.Runnable#run() */
    public void run() {
        execute();
        isDone = true;
    }

    /**
     * @see ITask#isDone()
     * @return boolean
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * get the response status code
     *
     * @return response status code
     */
    public int getStatusCode() {
        return 0;
    }
}
