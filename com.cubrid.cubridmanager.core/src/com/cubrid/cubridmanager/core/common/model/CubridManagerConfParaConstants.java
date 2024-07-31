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
package com.cubrid.cubridmanager.core.common.model;

/**
 * cm.conf configuration file parameters constants
 *
 * @author pangqiren
 * @version 1.0 - 2011-9-7 created by pangqiren
 */
public interface CubridManagerConfParaConstants { // NOPMD

    public static final String CM_PORT = "cm_port";
    public static final String MONITOR_INTERVAL = "monitor_interval";
    public static final String ALLOW_USER_MULTI_CONNECTION = "allow_user_multi_connection";
    public static final String AUTO_START_BROKER = "auto_start_broker";
    public static final String EXECUTE_DIAG = "execute_diag";
    public static final String SERVER_LONG_QUERY_TIME = "server_long_query_time";
    public static final String CM_TARGET = "cm_target";
    public static final String SUPPORT_MON_STATISTIC = "support_mon_statistic";

    // manager parameter
    public static final String[][] ALL_CM_CONF_PARAS = {
        {CM_PORT, "int(v>=1024&&v<=65535)", "8001", "version>=8.2.0", "false", ""},
        {MONITOR_INTERVAL, "int(v>0)", "5", "version>=8.2.0", "false", ""},
        {ALLOW_USER_MULTI_CONNECTION, "string", "YES", "version>=8.2.0", "false", ""},
        {AUTO_START_BROKER, "string(YES|NO)", "YES", "version>=8.2.0", "false", ""},
        {EXECUTE_DIAG, "string(ON|OFF)", "OFF", "version>=8.2.0", "false", ""},
        {SERVER_LONG_QUERY_TIME, "int", "10", "version>=8.2.0", "false", ""},
        {CM_TARGET, "string(broker|server)", "broker,server", "version>=8.2.0", "false", ""}
    };
}
