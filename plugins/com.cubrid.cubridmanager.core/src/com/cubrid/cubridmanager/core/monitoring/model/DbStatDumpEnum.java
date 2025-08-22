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
package com.cubrid.cubridmanager.core.monitoring.model;

/**
 * This enumeration providers the names for database status dump name showing in the CUBRID MANAGER
 * Client
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-3-30 created by lizhiqiang
 */
public enum DbStatDumpEnum implements IDiagPara {
    num_file_removes,
    num_file_creates,
    num_file_ioreads,
    num_file_iowrites,
    num_file_iosynches,
    num_data_page_fetches,
    num_data_page_dirties,
    num_data_page_ioreads,
    num_data_page_iowrites,
    num_log_page_ioreads,
    num_log_page_iowrites,
    num_log_append_records,
    num_log_archives,
    num_log_checkpoints,
    num_page_locks_acquired,
    num_page_locks_converted,
    num_page_locks_re_requested,
    num_page_locks_waits,
    num_object_locks_acquired,
    num_object_locks_converted,
    num_object_locks_re_requested,
    num_object_locks_waits,
    num_tran_commits,
    num_tran_rollbacks,
    num_tran_savepoints,
    num_tran_start_topops,
    num_tran_end_topops,
    num_tran_interrupts,
    num_btree_inserts,
    num_btree_deletes,
    num_btree_updates,
    num_query_selects,
    num_query_inserts,
    num_query_deletes,
    num_query_updates,
    num_query_sscans,
    num_query_iscans,
    num_query_lscans,
    num_query_setscans,
    num_query_methscans,
    num_query_nljoins,
    num_query_mjoins,
    num_query_objfetches,
    num_network_requests,
    data_page_buffer_hit_ratio;

    public String getName() {
        return name();
    }
}
