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
package com.cubrid.cubridmanager.ui.broker.editor.internal;

/**
 * This enumeration providers the column name for broker basic info status table(class
 * BrokerStatusView)
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-3-15 created by lizhiqiang
 */
public enum BrokerStatusBasicColumn implements IColumnSetting {
    PID(0, "pid"),
    PORT(1, "port"),
    JOB_QUEUE(2, "job_queue"),
    AUTO_ADD_APPL_SERVER(3, "auto_add_appl_server"),
    SQL_LOG_MODE(4, "sql_log_mode"),
    LONG_TRANS_TIME(-1, "long_trans_time"),
    LONG_QUERY_TIME(-1, "long_query_time"),
    SESSION_TIMEOUT(7, "session_timeout"),
    KEEP_CONNECTION(8, "keep_connection"),
    ACCESS_MODE(9, "access_mode"),
    ACCESS_SESSION(10, "access_session"),
    SESSION(11, "sesssion"),
    TPS(12, "tps");

    private int value = -1;
    private String nick;

    // Constructor
    private BrokerStatusBasicColumn(int value, String nick) {
        this.value = value;
        this.nick = nick;
    }

    /**
     * Set the value of a enumeration element
     *
     * @param value the value of enumeration
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * Get the value of a enumeration element
     *
     * @return the value of enumeration
     */
    public int getValue() {
        return value;
    }

    /**
     * Get the nick of enumeration element
     *
     * @return the nick
     */
    public String getNick() {
        return nick;
    }
}
