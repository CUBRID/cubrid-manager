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
 * cubrid_ha.conf configuration file parameters constants
 *
 * <p>HAConfParaConstants Description
 *
 * @author pangqiren
 * @version 1.0 - 2011-9-7 created by pangqiren
 */
public interface HAConfParaConstants { // NOPMD

    public static final String HA_MODE = "ha_mode";
    public static final String HA_NODE_LIST = "ha_node_list";
    public static final String HA_PORT_ID = "ha_port_id";
    public static final String HA_REPLICA_LIST = "ha_replica_list";
    public static final String HA_PING_HOSTS = "ha_ping_hosts";
    public static final String HA_DB_LIST = "ha_db_list";
    public static final String HA_COPY_LOG_BASE = "ha_copy_log_base";
    public static final String HA_APPLY_MAX_MEM_SIZE = "ha_apply_max_mem_size";
    public static final String HA_COPY_SYNC_MODE = "ha_copy_sync_mode";
    public static final String LOG_MAX_ARCHIVES = "log_max_archives";

    /*8.4.3*/
    public static final String HA_APPLYLOGDB_IGNORE_ERROR_LIS = "ha_applylogdb_ignore_error_lis";
    public static final String HA_APPLYLOGDB_RETRY_ERROR_LIST = "ha_applylogdb_retry_error_list";

    public static final String[][] ALL_HA_CONF_PARAS = {
        {
            HAConfParaConstants.HA_MODE,
            "string(on|off|yes|no|replica)",
            "off",
            CubridConfParaConstants.PARAMETER_TYPE_SERVER,
            "version>=8.4.0",
            "false",
            ""
        },
        {
            HAConfParaConstants.HA_NODE_LIST,
            "string",
            "",
            CubridConfParaConstants.PARAMETER_TYPE_SERVER,
            "version>=8.4.0",
            "false",
            ""
        },
        {
            HAConfParaConstants.HA_PORT_ID,
            "int(v>=1024&&v<=65535)",
            "59901",
            CubridConfParaConstants.PARAMETER_TYPE_SERVER,
            "version>=8.4.0",
            "false",
            ""
        },
        {
            HA_REPLICA_LIST,
            "string",
            "",
            CubridConfParaConstants.PARAMETER_TYPE_SERVER,
            "version>=8.4.0",
            "false",
            ""
        },
        {
            HA_PING_HOSTS,
            "string",
            "",
            CubridConfParaConstants.PARAMETER_TYPE_SERVER,
            "version>=8.4.0",
            "false",
            ""
        },
        {
            HA_COPY_LOG_BASE,
            "string",
            "",
            CubridConfParaConstants.PARAMETER_TYPE_SERVER,
            "version>=8.4.0",
            "false",
            ""
        },
        {
            HA_DB_LIST,
            "string",
            "",
            CubridConfParaConstants.PARAMETER_TYPE_SERVER,
            "version>=8.4.0",
            "false",
            ""
        },
        {
            HA_APPLY_MAX_MEM_SIZE,
            "int",
            "0",
            CubridConfParaConstants.PARAMETER_TYPE_SERVER,
            "version>=8.4.0",
            "false",
            ""
        },
        {
            HA_COPY_SYNC_MODE,
            "string",
            "",
            CubridConfParaConstants.PARAMETER_TYPE_SERVER,
            "version>=8.4.0",
            "false",
            ""
        },
        {
            LOG_MAX_ARCHIVES,
            "int",
            "0",
            CubridConfParaConstants.PARAMETER_TYPE_SERVER,
            "version>=8.4.0",
            "false",
            ""
        }
    };
}
