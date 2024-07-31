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
package com.cubrid.cubridmanager.core.replication.model;

import com.cubrid.cubridmanager.core.Messages;

/**
 * CUBRID Replication parameter constants
 *
 * @author wuyingshi
 * @version 1.0 - 2009-8-26 created by wuyingshi
 */
public final class ReplicationParamConstants {

    public static final String PERF_POLL_INTERVAL = "perf_poll_interval";
    public static final String SIZE_OF_LOG_BUFFER = "size_of_log_buffer";
    public static final String SIZE_OF_CACHE_BUFFER = "size_of_cache_buffer";
    public static final String SIZE_OF_COPYLOG = "size_of_copylog";
    public static final String INDEX_REPLICATION = "index_replication";
    public static final String FOR_RECOVERY = "for_recovery";
    public static final String LOG_APPLY_INTERVAL = "log_apply_interval";
    public static final String RESTART_INTERVAL = "restart_interval";

    private static String[][] replicationParameters = {
        {PERF_POLL_INTERVAL, "int(v>=10&&v<=60)", "10", Messages.tip_perf_poll_interval},
        {SIZE_OF_LOG_BUFFER, "int(v>=100&&v<=1000)", "500", Messages.tip_size_of_log_buffer},
        {SIZE_OF_CACHE_BUFFER, "int(v>=100&&v<=500)", "100", Messages.tip_size_of_cache_buffer},
        {SIZE_OF_COPYLOG, "int(v>=1000&&v<=10000)", "5000", Messages.tip_size_of_copylog},
        {INDEX_REPLICATION, "bool(Y|N)", "N", Messages.tip_index_replication},
        {FOR_RECOVERY, "bool(Y|N)", "N", Messages.tip_for_recovery},
        {LOG_APPLY_INTERVAL, "int(v>=0&&v<=600)", "0", Messages.tip_log_apply_interval},
        {RESTART_INTERVAL, "int(v>=1&&v<=60)", "60", Messages.tip_restart_interval}
    };

    /**
     * Get all replication parameters
     *
     * @return String[][]
     */
    public static String[][] getReplicationParameters() {
        String copy[][] = new String[replicationParameters.length][];
        for (int i = 0; i < replicationParameters.length; i++) {
            copy[i] = (String[]) replicationParameters[i].clone();
        }
        return copy;
    }

    private ReplicationParamConstants() {}
}
