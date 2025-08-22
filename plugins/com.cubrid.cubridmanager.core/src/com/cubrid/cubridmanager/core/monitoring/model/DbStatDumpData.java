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

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.cubridmanager.core.common.model.IModel;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.TreeMap;
import org.slf4j.Logger;

/**
 * A class that extends IModel and responsible for the task of "statdump"
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-3-23 created by lizhiqiang
 */
public class DbStatDumpData implements IModel {
    private static final Logger LOGGER = LogUtil.getLogger(DiagStatusResult.class);
    private String status;
    private String note;
    private String dbname;
    private String num_file_removes;
    private String num_file_creates;
    private String num_file_ioreads;
    private String num_file_iowrites;
    private String num_file_iosynches;

    private String num_data_page_fetches;
    private String num_data_page_dirties;
    private String num_data_page_ioreads;
    private String num_data_page_iowrites;

    private String num_log_page_ioreads;
    private String num_log_page_iowrites;
    private String num_log_append_records;
    private String num_log_archives;
    private String num_log_checkpoints;

    private String num_page_locks_acquired;
    private String num_object_locks_acquired;
    private String num_page_locks_converted;
    private String num_object_locks_converted;
    private String num_page_locks_re_requested;
    private String num_object_locks_re_requested;
    private String num_page_locks_waits;
    private String num_object_locks_waits;

    private String num_tran_commits;
    private String num_tran_rollbacks;
    private String num_tran_savepoints;
    private String num_tran_start_topops;
    private String num_tran_end_topops;
    private String num_tran_interrupts;

    private String num_btree_inserts;
    private String num_btree_deletes;
    private String num_btree_updates;

    private String num_query_selects;
    private String num_query_inserts;
    private String num_query_deletes;
    private String num_query_updates;
    private String num_query_sscans;
    private String num_query_iscans;
    private String num_query_lscans;
    private String num_query_setscans;
    private String num_query_methscans;
    private String num_query_nljoins;
    private String num_query_mjoins;
    private String num_query_objfetches;

    private String num_network_requests;
    private String data_page_buffer_hit_ratio;

    private TreeMap<IDiagPara, String> diagStatusResultMap;

    /* (non-Javadoc)
     * @see com.cubrid.cubridmanager.core.common.model.IModel#getTaskName()
     */
    public String getTaskName() {
        return "statdump";
    }

    public DbStatDumpData() {
        num_file_removes = "0";
        num_file_creates = "0";
        num_file_ioreads = "0";
        num_file_iowrites = "0";
        num_file_iosynches = "0";

        num_data_page_fetches = "0";
        num_data_page_dirties = "0";
        num_data_page_ioreads = "0";
        num_data_page_iowrites = "0";

        num_log_page_ioreads = "0";
        num_log_page_iowrites = "0";
        num_log_append_records = "0";
        num_log_archives = "0";
        num_log_checkpoints = "0";

        num_page_locks_acquired = "0";
        num_object_locks_acquired = "0";
        num_page_locks_converted = "0";
        num_object_locks_converted = "0";
        num_page_locks_re_requested = "0";
        num_object_locks_re_requested = "0";
        num_page_locks_waits = "0";
        num_object_locks_waits = "0";

        num_tran_commits = "0";
        num_tran_rollbacks = "0";
        num_tran_savepoints = "0";
        num_tran_start_topops = "0";
        num_tran_end_topops = "0";
        num_tran_interrupts = "0";

        num_btree_inserts = "0";
        num_btree_deletes = "0";
        num_btree_updates = "0";

        num_query_selects = "0";
        num_query_inserts = "0";
        num_query_deletes = "0";
        num_query_updates = "0";
        num_query_sscans = "0";
        num_query_iscans = "0";
        num_query_lscans = "0";
        num_query_setscans = "0";
        num_query_methscans = "0";
        num_query_nljoins = "0";
        num_query_mjoins = "0";
        num_query_objfetches = "0";

        num_network_requests = "0";
        data_page_buffer_hit_ratio = "0";

        diagStatusResultMap = new TreeMap<IDiagPara, String>();
        putVauleInMap();
    }

    public DbStatDumpData(DbStatDumpData clone) {
        super();
        num_file_removes = clone.num_file_removes;
        num_file_creates = clone.num_file_creates;
        num_file_ioreads = clone.num_file_ioreads;
        num_file_iowrites = clone.num_file_iowrites;
        num_file_iosynches = clone.num_file_iosynches;

        num_data_page_fetches = clone.num_data_page_fetches;
        num_data_page_dirties = clone.num_data_page_dirties;
        num_data_page_ioreads = clone.num_data_page_ioreads;
        num_data_page_iowrites = clone.num_data_page_iowrites;

        num_log_page_ioreads = clone.num_log_page_ioreads;
        num_log_page_iowrites = clone.num_log_page_iowrites;
        num_log_append_records = clone.num_log_append_records;
        num_log_archives = clone.num_log_archives;
        num_log_checkpoints = clone.num_log_checkpoints;

        num_page_locks_acquired = clone.num_page_locks_acquired;
        num_object_locks_acquired = clone.num_object_locks_acquired;
        num_page_locks_converted = clone.num_page_locks_converted;
        num_object_locks_converted = clone.num_object_locks_converted;
        num_page_locks_re_requested = clone.num_page_locks_re_requested;
        num_object_locks_re_requested = clone.num_object_locks_re_requested;
        num_page_locks_waits = clone.num_page_locks_waits;
        num_object_locks_waits = clone.num_object_locks_waits;

        num_tran_commits = clone.num_tran_commits;
        num_tran_rollbacks = clone.num_tran_rollbacks;
        num_tran_savepoints = clone.num_tran_savepoints;
        num_tran_start_topops = clone.num_tran_start_topops;
        num_tran_end_topops = clone.num_tran_end_topops;
        num_tran_interrupts = clone.num_tran_interrupts;

        num_btree_inserts = clone.num_btree_inserts;
        num_btree_deletes = clone.num_btree_deletes;
        num_btree_updates = clone.num_btree_updates;

        num_query_selects = clone.num_query_selects;
        num_query_inserts = clone.num_query_inserts;
        num_query_deletes = clone.num_query_deletes;
        num_query_updates = clone.num_query_updates;
        num_query_sscans = clone.num_query_sscans;
        num_query_iscans = clone.num_query_iscans;
        num_query_lscans = clone.num_query_lscans;
        num_query_setscans = clone.num_query_setscans;
        num_query_methscans = clone.num_query_methscans;
        num_query_nljoins = clone.num_query_nljoins;
        num_query_mjoins = clone.num_query_mjoins;
        num_query_objfetches = clone.num_query_objfetches;

        num_network_requests = clone.num_network_requests;
        data_page_buffer_hit_ratio = clone.data_page_buffer_hit_ratio;
    }

    /**
     * Get the clone value from the given object
     *
     * @param clone DiagStatusResult
     */
    public void copy_from(DbStatDumpData clone) {
        num_file_removes = clone.num_file_removes;
        num_file_creates = clone.num_file_creates;
        num_file_ioreads = clone.num_file_ioreads;
        num_file_iowrites = clone.num_file_iowrites;
        num_file_iosynches = clone.num_file_iosynches;

        num_data_page_fetches = clone.num_data_page_fetches;
        num_data_page_dirties = clone.num_data_page_dirties;
        num_data_page_ioreads = clone.num_data_page_ioreads;
        num_data_page_iowrites = clone.num_data_page_iowrites;

        num_log_page_ioreads = clone.num_log_page_ioreads;
        num_log_page_iowrites = clone.num_log_page_iowrites;
        num_log_append_records = clone.num_log_append_records;
        num_log_archives = clone.num_log_archives;
        num_log_checkpoints = clone.num_log_checkpoints;

        num_page_locks_acquired = clone.num_page_locks_acquired;
        num_object_locks_acquired = clone.num_object_locks_acquired;
        num_page_locks_converted = clone.num_page_locks_converted;
        num_object_locks_converted = clone.num_object_locks_converted;
        num_page_locks_re_requested = clone.num_page_locks_re_requested;
        num_object_locks_re_requested = clone.num_object_locks_re_requested;
        num_page_locks_waits = clone.num_page_locks_waits;
        num_object_locks_waits = clone.num_object_locks_waits;

        num_tran_commits = clone.num_tran_commits;
        num_tran_rollbacks = clone.num_tran_rollbacks;
        num_tran_savepoints = clone.num_tran_savepoints;
        num_tran_start_topops = clone.num_tran_start_topops;
        num_tran_end_topops = clone.num_tran_end_topops;
        num_tran_interrupts = clone.num_tran_interrupts;

        num_btree_inserts = clone.num_btree_inserts;
        num_btree_deletes = clone.num_btree_deletes;
        num_btree_updates = clone.num_btree_updates;

        num_query_selects = clone.num_query_selects;
        num_query_inserts = clone.num_query_inserts;
        num_query_deletes = clone.num_query_deletes;
        num_query_updates = clone.num_query_updates;
        num_query_sscans = clone.num_query_sscans;
        num_query_iscans = clone.num_query_iscans;
        num_query_lscans = clone.num_query_lscans;
        num_query_setscans = clone.num_query_setscans;
        num_query_methscans = clone.num_query_methscans;
        num_query_nljoins = clone.num_query_nljoins;
        num_query_mjoins = clone.num_query_mjoins;
        num_query_objfetches = clone.num_query_objfetches;

        num_network_requests = clone.num_network_requests;
        data_page_buffer_hit_ratio = clone.data_page_buffer_hit_ratio;
    }

    /**
     * Gets the delta by two bean of DiagStatusResult
     *
     * @param dsrA DbStatDumpData
     * @param dsrB DbStatDumpData
     */
    public void getDelta(DbStatDumpData dsrA, DbStatDumpData dsrB) {

        num_file_removes = getDeltaLong(dsrA.num_file_removes, dsrB.num_file_removes);
        num_file_creates = getDeltaLong(dsrA.num_file_creates, dsrB.num_file_creates);
        num_file_ioreads = getDeltaLong(dsrA.num_file_ioreads, dsrB.num_file_ioreads);
        num_file_iowrites = getDeltaLong(dsrA.num_file_iowrites, dsrB.num_file_iowrites);
        num_file_iosynches = getDeltaLong(dsrA.num_file_iosynches, dsrB.num_file_iosynches);

        num_data_page_fetches =
                getDeltaLong(dsrA.num_data_page_fetches, dsrB.num_data_page_fetches);
        num_data_page_dirties =
                getDeltaLong(dsrA.num_data_page_dirties, dsrB.num_data_page_dirties);
        num_data_page_ioreads =
                getDeltaLong(dsrA.num_data_page_ioreads, dsrB.num_data_page_ioreads);
        num_data_page_iowrites =
                getDeltaLong(dsrA.num_data_page_iowrites, dsrB.num_data_page_iowrites);

        num_log_page_ioreads = getDeltaLong(dsrA.num_log_page_ioreads, dsrB.num_log_page_ioreads);
        num_log_page_iowrites =
                getDeltaLong(dsrA.num_log_page_iowrites, dsrB.num_log_page_iowrites);
        num_log_append_records =
                getDeltaLong(dsrA.num_log_append_records, dsrB.num_log_append_records);
        num_log_archives = getDeltaLong(dsrA.num_log_archives, dsrB.num_log_archives);
        num_log_checkpoints = getDeltaLong(dsrA.num_log_checkpoints, dsrB.num_log_checkpoints);

        num_page_locks_acquired =
                getDeltaLong(dsrA.num_page_locks_acquired, dsrB.num_page_locks_acquired);
        num_object_locks_acquired =
                getDeltaLong(dsrA.num_object_locks_acquired, dsrB.num_object_locks_acquired);
        num_page_locks_converted =
                getDeltaLong(dsrA.num_page_locks_converted, dsrB.num_page_locks_converted);
        num_object_locks_converted =
                getDeltaLong(dsrA.num_object_locks_converted, dsrB.num_object_locks_converted);
        num_page_locks_re_requested =
                getDeltaLong(dsrA.num_page_locks_re_requested, dsrB.num_page_locks_re_requested);
        num_object_locks_re_requested =
                getDeltaLong(
                        dsrA.num_object_locks_re_requested, dsrB.num_object_locks_re_requested);
        num_page_locks_waits = getDeltaLong(dsrA.num_page_locks_waits, dsrB.num_file_removes);
        num_object_locks_waits =
                getDeltaLong(dsrA.num_object_locks_waits, dsrB.num_object_locks_waits);

        num_tran_commits = getDeltaLong(dsrA.num_tran_commits, dsrB.num_tran_commits);
        num_tran_rollbacks = getDeltaLong(dsrA.num_tran_rollbacks, dsrB.num_tran_rollbacks);
        num_tran_savepoints = getDeltaLong(dsrA.num_tran_savepoints, dsrB.num_tran_savepoints);
        num_tran_start_topops =
                getDeltaLong(dsrA.num_tran_start_topops, dsrB.num_tran_start_topops);
        num_tran_end_topops = getDeltaLong(dsrA.num_tran_end_topops, dsrB.num_tran_end_topops);
        num_tran_interrupts = getDeltaLong(dsrA.num_tran_interrupts, dsrB.num_tran_interrupts);

        num_btree_inserts = getDeltaLong(dsrA.num_btree_inserts, dsrB.num_btree_inserts);
        num_btree_deletes = getDeltaLong(dsrA.num_btree_deletes, dsrB.num_btree_deletes);
        num_btree_updates = getDeltaLong(dsrA.num_btree_updates, dsrB.num_btree_updates);

        num_query_selects = getDeltaLong(dsrA.num_query_selects, dsrB.num_query_selects);
        num_query_inserts = getDeltaLong(dsrA.num_query_inserts, dsrB.num_query_inserts);
        num_query_deletes = getDeltaLong(dsrA.num_query_deletes, dsrB.num_query_deletes);
        num_query_updates = getDeltaLong(dsrA.num_query_updates, dsrB.num_query_updates);
        num_query_sscans = getDeltaLong(dsrA.num_query_sscans, dsrB.num_query_sscans);
        num_query_iscans = getDeltaLong(dsrA.num_query_iscans, dsrB.num_query_iscans);
        num_query_lscans = getDeltaLong(dsrA.num_query_lscans, dsrB.num_query_lscans);
        num_query_setscans = getDeltaLong(dsrA.num_query_setscans, dsrB.num_query_setscans);
        num_query_methscans = getDeltaLong(dsrA.num_query_methscans, dsrB.num_query_methscans);
        num_query_nljoins = getDeltaLong(dsrA.num_query_nljoins, dsrB.num_query_nljoins);
        num_query_mjoins = getDeltaLong(dsrA.num_query_mjoins, dsrB.num_query_mjoins);
        num_query_objfetches = getDeltaLong(dsrA.num_query_objfetches, dsrB.num_query_objfetches);

        num_network_requests = getDeltaLong(dsrA.num_network_requests, dsrB.num_network_requests);
        data_page_buffer_hit_ratio = dsrA.data_page_buffer_hit_ratio;
        putVauleInMap();
    }

    /**
     * Gets the delta by three bean of DiagStatusResult
     *
     * @param dsrA DbStatDumpData
     * @param dsrB DbStatDumpData
     * @param dsrC DbStatDumpData
     */
    public void getDelta(DbStatDumpData dsrA, DbStatDumpData dsrB, DbStatDumpData dsrC) {

        num_file_removes =
                getDeltaLong(
                        dsrA,
                        "Num_file_opens",
                        dsrA.num_file_removes,
                        dsrB.num_file_removes,
                        dsrC.num_file_removes);
        num_file_creates =
                getDeltaLong(
                        dsrA,
                        "Num_file_creates",
                        dsrA.num_file_creates,
                        dsrB.num_file_creates,
                        dsrC.num_file_creates);
        num_file_ioreads =
                getDeltaLong(
                        dsrA,
                        "Num_file_ioreads",
                        dsrA.num_file_ioreads,
                        dsrB.num_file_ioreads,
                        dsrC.num_file_ioreads);
        num_file_iowrites =
                getDeltaLong(
                        dsrA,
                        "Num_file_iowrites",
                        dsrA.num_file_iowrites,
                        dsrB.num_file_iowrites,
                        dsrC.num_file_iowrites);
        num_file_iosynches =
                getDeltaLong(
                        dsrA,
                        "Num_file_iosynches",
                        dsrA.num_file_iosynches,
                        dsrB.num_file_iosynches,
                        dsrC.num_file_iosynches);

        num_data_page_fetches =
                getDeltaLong(
                        dsrA,
                        "Num_data_page_fetches",
                        dsrA.num_data_page_fetches,
                        dsrB.num_data_page_fetches,
                        dsrC.num_data_page_fetches);
        num_data_page_dirties =
                getDeltaLong(
                        dsrA,
                        "Num_data_page_dirties",
                        dsrA.num_data_page_dirties,
                        dsrB.num_data_page_dirties,
                        dsrC.num_data_page_dirties);
        num_data_page_ioreads =
                getDeltaLong(
                        dsrA,
                        "Num_data_page_ioreads",
                        dsrA.num_data_page_ioreads,
                        dsrB.num_data_page_ioreads,
                        dsrC.num_data_page_ioreads);
        num_data_page_iowrites =
                getDeltaLong(
                        dsrA,
                        "Num_data_page_iowrites",
                        dsrA.num_data_page_iowrites,
                        dsrB.num_data_page_iowrites,
                        dsrC.num_data_page_iowrites);

        num_log_page_ioreads =
                getDeltaLong(
                        dsrA,
                        "Num_log_page_ioreads",
                        dsrA.num_log_page_ioreads,
                        dsrB.num_log_page_ioreads,
                        dsrC.num_log_page_ioreads);
        num_log_page_iowrites =
                getDeltaLong(
                        dsrA,
                        "Num_log_page_iowrites",
                        dsrA.num_log_page_iowrites,
                        dsrB.num_log_page_iowrites,
                        dsrC.num_log_page_iowrites);
        num_log_append_records =
                getDeltaLong(
                        dsrA,
                        "Num_log_append_records",
                        dsrA.num_log_append_records,
                        dsrB.num_log_append_records,
                        dsrC.num_log_append_records);
        num_log_archives =
                getDeltaLong(
                        dsrA,
                        "Num_log_archives",
                        dsrA.num_log_archives,
                        dsrB.num_log_archives,
                        dsrC.num_log_archives);
        num_log_checkpoints =
                getDeltaLong(
                        dsrA,
                        "Num_log_checkpoints",
                        dsrA.num_log_checkpoints,
                        dsrB.num_log_checkpoints,
                        dsrC.num_log_checkpoints);

        num_page_locks_acquired =
                getDeltaLong(
                        dsrA,
                        "Num_page_locks_acquired",
                        dsrA.num_page_locks_acquired,
                        dsrB.num_page_locks_acquired,
                        dsrC.num_page_locks_acquired);
        num_object_locks_acquired =
                getDeltaLong(
                        dsrA,
                        "Num_object_locks_acquired",
                        dsrA.num_object_locks_acquired,
                        dsrB.num_object_locks_acquired,
                        dsrC.num_object_locks_acquired);
        num_page_locks_converted =
                getDeltaLong(
                        dsrA,
                        "Num_page_locks_converted",
                        dsrA.num_page_locks_converted,
                        dsrB.num_page_locks_converted,
                        dsrC.num_page_locks_converted);
        num_object_locks_converted =
                getDeltaLong(
                        dsrA,
                        "Num_object_locks_converted",
                        dsrA.num_object_locks_converted,
                        dsrB.num_object_locks_converted,
                        dsrC.num_object_locks_converted);
        num_page_locks_re_requested =
                getDeltaLong(
                        dsrA,
                        "Num_page_locks_re_requested",
                        dsrA.num_page_locks_re_requested,
                        dsrB.num_page_locks_re_requested,
                        dsrC.num_page_locks_re_requested);
        num_object_locks_re_requested =
                getDeltaLong(
                        dsrA,
                        "Num_object_locks_re_requested",
                        dsrA.num_object_locks_re_requested,
                        dsrB.num_object_locks_re_requested,
                        dsrC.num_object_locks_re_requested);
        num_page_locks_waits =
                getDeltaLong(
                        dsrA,
                        "Num_page_locks_waits",
                        dsrA.num_page_locks_waits,
                        dsrB.num_page_locks_waits,
                        dsrC.num_page_locks_waits);
        num_object_locks_waits =
                getDeltaLong(
                        dsrA,
                        "Num_object_locks_waits",
                        dsrA.num_object_locks_waits,
                        dsrB.num_object_locks_waits,
                        dsrC.num_object_locks_waits);

        num_tran_commits =
                getDeltaLong(
                        dsrA,
                        "Num_tran_commits",
                        dsrA.num_tran_commits,
                        dsrB.num_tran_commits,
                        dsrC.num_tran_commits);
        num_tran_rollbacks =
                getDeltaLong(
                        dsrA,
                        "Num_tran_rollbacks",
                        dsrA.num_tran_rollbacks,
                        dsrB.num_tran_rollbacks,
                        dsrC.num_tran_rollbacks);
        num_tran_savepoints =
                getDeltaLong(
                        dsrA,
                        "Num_tran_savepoints",
                        dsrA.num_tran_savepoints,
                        dsrB.num_tran_savepoints,
                        dsrC.num_tran_savepoints);
        num_tran_start_topops =
                getDeltaLong(
                        dsrA,
                        "Num_tran_start_topops",
                        dsrA.num_tran_start_topops,
                        dsrB.num_tran_start_topops,
                        dsrC.num_tran_start_topops);
        num_tran_end_topops =
                getDeltaLong(
                        dsrA,
                        "Num_tran_end_topops",
                        dsrA.num_tran_end_topops,
                        dsrB.num_tran_end_topops,
                        dsrC.num_tran_end_topops);
        num_tran_interrupts =
                getDeltaLong(
                        dsrA,
                        "Num_tran_interrupts",
                        dsrA.num_tran_interrupts,
                        dsrB.num_tran_interrupts,
                        dsrC.num_tran_interrupts);
        num_btree_inserts =
                getDeltaLong(
                        dsrA,
                        "Num_btree_inserts",
                        dsrA.num_btree_inserts,
                        dsrB.num_btree_inserts,
                        dsrC.num_btree_inserts);

        num_btree_deletes =
                getDeltaLong(
                        dsrA,
                        "Num_btree_deletes",
                        dsrA.num_btree_deletes,
                        dsrB.num_btree_deletes,
                        dsrC.num_btree_deletes);
        num_btree_updates =
                getDeltaLong(
                        dsrA,
                        "Num_btree_updates",
                        dsrA.num_btree_updates,
                        dsrB.num_btree_updates,
                        dsrC.num_btree_updates);

        num_query_selects =
                getDeltaLong(
                        dsrA,
                        "Num_query_selects",
                        dsrA.num_query_selects,
                        dsrB.num_query_selects,
                        dsrC.num_query_selects);
        num_query_inserts =
                getDeltaLong(
                        dsrA,
                        "Num_query_inserts",
                        dsrA.num_query_inserts,
                        dsrB.num_query_inserts,
                        dsrC.num_query_inserts);
        num_query_deletes =
                getDeltaLong(
                        dsrA,
                        "Num_query_deletes",
                        dsrA.num_query_deletes,
                        dsrB.num_query_deletes,
                        dsrC.num_query_deletes);
        num_query_updates =
                getDeltaLong(
                        dsrA,
                        "Num_query_updates",
                        dsrA.num_query_updates,
                        dsrB.num_query_updates,
                        dsrB.num_query_updates);
        num_query_sscans =
                getDeltaLong(
                        dsrA,
                        "Num_query_sscans",
                        dsrA.num_query_sscans,
                        dsrB.num_query_sscans,
                        dsrC.num_query_sscans);
        num_query_iscans =
                getDeltaLong(
                        dsrA,
                        "Num_query_iscans",
                        dsrA.num_query_iscans,
                        dsrB.num_query_iscans,
                        dsrC.num_query_iscans);
        num_query_lscans =
                getDeltaLong(
                        dsrA,
                        "Num_query_lscans",
                        dsrA.num_query_lscans,
                        dsrB.num_query_lscans,
                        dsrC.num_query_lscans);
        num_query_setscans =
                getDeltaLong(
                        dsrA,
                        "Num_query_setscans",
                        dsrA.num_query_setscans,
                        dsrB.num_query_setscans,
                        dsrC.num_query_setscans);
        num_query_methscans =
                getDeltaLong(
                        dsrA,
                        "Num_query_methscans",
                        dsrA.num_query_methscans,
                        dsrB.num_query_methscans,
                        dsrC.num_query_methscans);
        num_query_nljoins =
                getDeltaLong(
                        dsrA,
                        "Num_query_nljoins",
                        dsrA.num_query_nljoins,
                        dsrB.num_query_nljoins,
                        dsrC.num_query_nljoins);
        num_query_mjoins =
                getDeltaLong(
                        dsrA,
                        "Num_query_mjoins",
                        dsrA.num_query_mjoins,
                        dsrB.num_query_mjoins,
                        dsrC.num_query_mjoins);
        num_query_objfetches =
                getDeltaLong(
                        dsrA,
                        "Num_query_objfetches",
                        dsrA.num_query_objfetches,
                        dsrB.num_query_objfetches,
                        dsrC.num_query_objfetches);

        num_network_requests =
                getDeltaLong(
                        dsrA,
                        "Num_network_requests",
                        dsrA.num_network_requests,
                        dsrB.num_network_requests,
                        dsrC.num_network_requests);

        data_page_buffer_hit_ratio = dsrA.data_page_buffer_hit_ratio;

        putVauleInMap();
    }

    /**
     * Gets the delta by three bean of BrokerDiagData and the interval between getting the instance
     * of BrokerDiagData
     *
     * @param dsrA DbStatDumpData
     * @param dsrB DbStatDumpData
     * @param dsrC DbStatDumpData
     * @param inter float
     */
    public void getDelta(
            DbStatDumpData dsrA, DbStatDumpData dsrB, DbStatDumpData dsrC, float inter) {

        num_file_removes =
                getDeltaLong(
                        dsrA,
                        "Num_file_opens",
                        dsrA.num_file_removes,
                        dsrB.num_file_removes,
                        dsrC.num_file_removes,
                        inter);
        num_file_creates =
                getDeltaLong(
                        dsrA,
                        "Num_file_creates",
                        dsrA.num_file_creates,
                        dsrB.num_file_creates,
                        dsrC.num_file_creates,
                        inter);
        num_file_ioreads =
                getDeltaLong(
                        dsrA,
                        "Num_file_ioreads",
                        dsrA.num_file_ioreads,
                        dsrB.num_file_ioreads,
                        dsrC.num_file_ioreads,
                        inter);
        num_file_iowrites =
                getDeltaLong(
                        dsrA,
                        "Num_file_iowrites",
                        dsrA.num_file_iowrites,
                        dsrB.num_file_iowrites,
                        dsrC.num_file_iowrites,
                        inter);
        num_file_iosynches =
                getDeltaLong(
                        dsrA,
                        "Num_file_iosynches",
                        dsrA.num_file_iosynches,
                        dsrB.num_file_iosynches,
                        dsrC.num_file_iosynches,
                        inter);

        num_data_page_fetches =
                getDeltaLong(
                        dsrA,
                        "Num_data_page_fetches",
                        dsrA.num_data_page_fetches,
                        dsrB.num_data_page_fetches,
                        dsrC.num_data_page_fetches,
                        inter);
        num_data_page_dirties =
                getDeltaLong(
                        dsrA,
                        "Num_data_page_dirties",
                        dsrA.num_data_page_dirties,
                        dsrB.num_data_page_dirties,
                        dsrC.num_data_page_dirties,
                        inter);
        num_data_page_ioreads =
                getDeltaLong(
                        dsrA,
                        "Num_data_page_ioreads",
                        dsrA.num_data_page_ioreads,
                        dsrB.num_data_page_ioreads,
                        dsrC.num_data_page_ioreads,
                        inter);
        num_data_page_iowrites =
                getDeltaLong(
                        dsrA,
                        "Num_data_page_iowrites",
                        dsrA.num_data_page_iowrites,
                        dsrB.num_data_page_iowrites,
                        dsrC.num_data_page_iowrites,
                        inter);

        num_log_page_ioreads =
                getDeltaLong(
                        dsrA,
                        "Num_log_page_ioreads",
                        dsrA.num_log_page_ioreads,
                        dsrB.num_log_page_ioreads,
                        dsrC.num_log_page_ioreads,
                        inter);
        num_log_page_iowrites =
                getDeltaLong(
                        dsrA,
                        "Num_log_page_iowrites",
                        dsrA.num_log_page_iowrites,
                        dsrB.num_log_page_iowrites,
                        dsrC.num_log_page_iowrites,
                        inter);
        num_log_append_records =
                getDeltaLong(
                        dsrA,
                        "Num_log_append_records",
                        dsrA.num_log_append_records,
                        dsrB.num_log_append_records,
                        dsrC.num_log_append_records,
                        inter);
        num_log_archives =
                getDeltaLong(
                        dsrA,
                        "Num_log_archives",
                        dsrA.num_log_archives,
                        dsrB.num_log_archives,
                        dsrC.num_log_archives,
                        inter);
        num_log_checkpoints =
                getDeltaLong(
                        dsrA,
                        "Num_log_checkpoints",
                        dsrA.num_log_checkpoints,
                        dsrB.num_log_checkpoints,
                        dsrC.num_log_checkpoints,
                        inter);

        num_page_locks_acquired =
                getDeltaLong(
                        dsrA,
                        "Num_page_locks_acquired",
                        dsrA.num_page_locks_acquired,
                        dsrB.num_page_locks_acquired,
                        dsrC.num_page_locks_acquired,
                        inter);
        num_object_locks_acquired =
                getDeltaLong(
                        dsrA,
                        "Num_object_locks_acquired",
                        dsrA.num_object_locks_acquired,
                        dsrB.num_object_locks_acquired,
                        dsrC.num_object_locks_acquired,
                        inter);
        num_page_locks_converted =
                getDeltaLong(
                        dsrA,
                        "Num_page_locks_converted",
                        dsrA.num_page_locks_converted,
                        dsrB.num_page_locks_converted,
                        dsrC.num_page_locks_converted,
                        inter);
        num_object_locks_converted =
                getDeltaLong(
                        dsrA,
                        "Num_object_locks_converted",
                        dsrA.num_object_locks_converted,
                        dsrB.num_object_locks_converted,
                        dsrC.num_object_locks_converted,
                        inter);
        num_page_locks_re_requested =
                getDeltaLong(
                        dsrA,
                        "Num_page_locks_re_requested",
                        dsrA.num_page_locks_re_requested,
                        dsrB.num_page_locks_re_requested,
                        dsrC.num_page_locks_re_requested,
                        inter);
        num_object_locks_re_requested =
                getDeltaLong(
                        dsrA,
                        "Num_object_locks_re_requested",
                        dsrA.num_object_locks_re_requested,
                        dsrB.num_object_locks_re_requested,
                        dsrC.num_object_locks_re_requested,
                        inter);
        num_page_locks_waits =
                getDeltaLong(
                        dsrA,
                        "Num_page_locks_waits",
                        dsrA.num_page_locks_waits,
                        dsrB.num_page_locks_waits,
                        dsrC.num_page_locks_waits,
                        inter);
        num_object_locks_waits =
                getDeltaLong(
                        dsrA,
                        "Num_object_locks_waits",
                        dsrA.num_object_locks_waits,
                        dsrB.num_object_locks_waits,
                        dsrC.num_object_locks_waits,
                        inter);

        num_tran_commits =
                getDeltaLong(
                        dsrA,
                        "Num_tran_commits",
                        dsrA.num_tran_commits,
                        dsrB.num_tran_commits,
                        dsrC.num_tran_commits,
                        inter);
        num_tran_rollbacks =
                getDeltaLong(
                        dsrA,
                        "Num_tran_rollbacks",
                        dsrA.num_tran_rollbacks,
                        dsrB.num_tran_rollbacks,
                        dsrC.num_tran_rollbacks,
                        inter);
        num_tran_savepoints =
                getDeltaLong(
                        dsrA,
                        "Num_tran_savepoints",
                        dsrA.num_tran_savepoints,
                        dsrB.num_tran_savepoints,
                        dsrC.num_tran_savepoints,
                        inter);
        num_tran_start_topops =
                getDeltaLong(
                        dsrA,
                        "Num_tran_start_topops",
                        dsrA.num_tran_start_topops,
                        dsrB.num_tran_start_topops,
                        dsrC.num_tran_start_topops,
                        inter);
        num_tran_end_topops =
                getDeltaLong(
                        dsrA,
                        "Num_tran_end_topops",
                        dsrA.num_tran_end_topops,
                        dsrB.num_tran_end_topops,
                        dsrC.num_tran_end_topops,
                        inter);
        num_tran_interrupts =
                getDeltaLong(
                        dsrA,
                        "Num_tran_interrupts",
                        dsrA.num_tran_interrupts,
                        dsrB.num_tran_interrupts,
                        dsrC.num_tran_interrupts,
                        inter);
        num_btree_inserts =
                getDeltaLong(
                        dsrA,
                        "Num_btree_inserts",
                        dsrA.num_btree_inserts,
                        dsrB.num_btree_inserts,
                        dsrC.num_btree_inserts,
                        inter);

        num_btree_deletes =
                getDeltaLong(
                        dsrA,
                        "Num_btree_deletes",
                        dsrA.num_btree_deletes,
                        dsrB.num_btree_deletes,
                        dsrC.num_btree_deletes,
                        inter);
        num_btree_updates =
                getDeltaLong(
                        dsrA,
                        "Num_btree_updates",
                        dsrA.num_btree_updates,
                        dsrB.num_btree_updates,
                        dsrC.num_btree_updates,
                        inter);
        num_network_requests =
                getDeltaLong(
                        dsrA,
                        "Num_network_requests",
                        dsrA.num_network_requests,
                        dsrB.num_network_requests,
                        dsrC.num_network_requests,
                        inter);

        num_query_selects =
                getDeltaLong(
                        dsrA,
                        "Num_query_selects",
                        dsrA.num_query_selects,
                        dsrB.num_query_selects,
                        dsrC.num_query_selects,
                        inter);
        num_query_inserts =
                getDeltaLong(
                        dsrA,
                        "Num_query_inserts",
                        dsrA.num_query_inserts,
                        dsrB.num_query_inserts,
                        dsrC.num_query_inserts,
                        inter);
        num_query_deletes =
                getDeltaLong(
                        dsrA,
                        "Num_query_deletes",
                        dsrA.num_query_deletes,
                        dsrB.num_query_deletes,
                        dsrC.num_query_deletes,
                        inter);
        num_query_updates =
                getDeltaLong(
                        dsrA,
                        "Num_query_updates",
                        dsrA.num_query_updates,
                        dsrB.num_query_updates,
                        dsrB.num_query_updates,
                        inter);
        num_query_sscans =
                getDeltaLong(
                        dsrA,
                        "Num_query_sscans",
                        dsrA.num_query_sscans,
                        dsrB.num_query_sscans,
                        dsrC.num_query_sscans,
                        inter);
        num_query_iscans =
                getDeltaLong(
                        dsrA,
                        "Num_query_iscans",
                        dsrA.num_query_iscans,
                        dsrB.num_query_iscans,
                        dsrC.num_query_iscans,
                        inter);
        num_query_lscans =
                getDeltaLong(
                        dsrA,
                        "Num_query_lscans",
                        dsrA.num_query_lscans,
                        dsrB.num_query_lscans,
                        dsrC.num_query_lscans,
                        inter);
        num_query_setscans =
                getDeltaLong(
                        dsrA,
                        "Num_query_setscans",
                        dsrA.num_query_setscans,
                        dsrB.num_query_setscans,
                        dsrC.num_query_setscans,
                        inter);
        num_query_methscans =
                getDeltaLong(
                        dsrA,
                        "Num_query_methscans",
                        dsrA.num_query_methscans,
                        dsrB.num_query_methscans,
                        dsrC.num_query_methscans,
                        inter);
        num_query_nljoins =
                getDeltaLong(
                        dsrA,
                        "Num_query_nljoins",
                        dsrA.num_query_nljoins,
                        dsrB.num_query_nljoins,
                        dsrC.num_query_nljoins,
                        inter);
        num_query_mjoins =
                getDeltaLong(
                        dsrA,
                        "Num_query_mjoins",
                        dsrA.num_query_mjoins,
                        dsrB.num_query_mjoins,
                        dsrC.num_query_mjoins,
                        inter);
        num_query_objfetches =
                getDeltaLong(
                        dsrA,
                        "Num_query_objfetches",
                        dsrA.num_query_objfetches,
                        dsrB.num_query_objfetches,
                        dsrC.num_query_objfetches,
                        inter);

        num_network_requests =
                getDeltaLong(
                        dsrA,
                        "Num_network_requests",
                        dsrA.num_network_requests,
                        dsrB.num_network_requests,
                        dsrC.num_network_requests,
                        inter);
        data_page_buffer_hit_ratio = dsrA.data_page_buffer_hit_ratio;
        putVauleInMap();
    }

    /** Put the new value of fields to map */
    private void putVauleInMap() {

        diagStatusResultMap.put(DbStatDumpEnum.num_file_removes, num_file_removes);
        diagStatusResultMap.put(DbStatDumpEnum.num_file_creates, num_file_creates);
        diagStatusResultMap.put(DbStatDumpEnum.num_file_ioreads, num_file_ioreads);
        diagStatusResultMap.put(DbStatDumpEnum.num_file_iowrites, num_file_iowrites);
        diagStatusResultMap.put(DbStatDumpEnum.num_file_iosynches, num_file_iosynches);

        diagStatusResultMap.put(DbStatDumpEnum.num_data_page_fetches, num_data_page_fetches);
        diagStatusResultMap.put(DbStatDumpEnum.num_data_page_dirties, num_data_page_dirties);
        diagStatusResultMap.put(DbStatDumpEnum.num_data_page_ioreads, num_data_page_ioreads);
        diagStatusResultMap.put(DbStatDumpEnum.num_data_page_iowrites, num_data_page_iowrites);

        diagStatusResultMap.put(DbStatDumpEnum.num_log_page_ioreads, num_log_page_ioreads);
        diagStatusResultMap.put(DbStatDumpEnum.num_log_page_iowrites, num_log_page_iowrites);
        diagStatusResultMap.put(DbStatDumpEnum.num_log_append_records, num_log_append_records);
        diagStatusResultMap.put(DbStatDumpEnum.num_log_archives, num_log_archives);
        diagStatusResultMap.put(DbStatDumpEnum.num_log_checkpoints, num_log_checkpoints);

        diagStatusResultMap.put(DbStatDumpEnum.num_page_locks_acquired, num_page_locks_acquired);
        diagStatusResultMap.put(
                DbStatDumpEnum.num_object_locks_acquired, num_object_locks_acquired);
        diagStatusResultMap.put(DbStatDumpEnum.num_page_locks_converted, num_page_locks_converted);
        diagStatusResultMap.put(
                DbStatDumpEnum.num_object_locks_converted, num_object_locks_converted);
        diagStatusResultMap.put(
                DbStatDumpEnum.num_page_locks_re_requested, num_page_locks_re_requested);
        diagStatusResultMap.put(
                DbStatDumpEnum.num_object_locks_re_requested, num_object_locks_re_requested);
        diagStatusResultMap.put(DbStatDumpEnum.num_page_locks_waits, num_page_locks_waits);
        diagStatusResultMap.put(DbStatDumpEnum.num_object_locks_waits, num_object_locks_waits);

        diagStatusResultMap.put(DbStatDumpEnum.num_tran_commits, num_tran_commits);
        diagStatusResultMap.put(DbStatDumpEnum.num_tran_rollbacks, num_tran_rollbacks);
        diagStatusResultMap.put(DbStatDumpEnum.num_tran_savepoints, num_tran_savepoints);
        diagStatusResultMap.put(DbStatDumpEnum.num_tran_start_topops, num_tran_start_topops);
        diagStatusResultMap.put(DbStatDumpEnum.num_tran_end_topops, num_tran_end_topops);
        diagStatusResultMap.put(DbStatDumpEnum.num_tran_interrupts, num_tran_interrupts);

        diagStatusResultMap.put(DbStatDumpEnum.num_btree_inserts, num_btree_inserts);
        diagStatusResultMap.put(DbStatDumpEnum.num_btree_deletes, num_btree_deletes);
        diagStatusResultMap.put(DbStatDumpEnum.num_btree_updates, num_btree_updates);

        diagStatusResultMap.put(DbStatDumpEnum.num_query_selects, num_query_selects);
        diagStatusResultMap.put(DbStatDumpEnum.num_query_inserts, num_query_inserts);
        diagStatusResultMap.put(DbStatDumpEnum.num_query_deletes, num_query_deletes);
        diagStatusResultMap.put(DbStatDumpEnum.num_query_updates, num_query_updates);
        diagStatusResultMap.put(DbStatDumpEnum.num_query_sscans, num_query_sscans);
        diagStatusResultMap.put(DbStatDumpEnum.num_query_iscans, num_query_iscans);
        diagStatusResultMap.put(DbStatDumpEnum.num_query_lscans, num_query_lscans);
        diagStatusResultMap.put(DbStatDumpEnum.num_query_setscans, num_query_setscans);
        diagStatusResultMap.put(DbStatDumpEnum.num_query_methscans, num_query_methscans);
        diagStatusResultMap.put(DbStatDumpEnum.num_query_nljoins, num_query_nljoins);
        diagStatusResultMap.put(DbStatDumpEnum.num_query_mjoins, num_query_mjoins);
        diagStatusResultMap.put(DbStatDumpEnum.num_query_objfetches, num_query_objfetches);

        diagStatusResultMap.put(DbStatDumpEnum.num_network_requests, num_network_requests);
        diagStatusResultMap.put(
                DbStatDumpEnum.data_page_buffer_hit_ratio, data_page_buffer_hit_ratio);
    }

    /**
     * Get the long value from given the strings which should be a field of a instance of the same
     * type
     *
     * @param fieldA the field of a object
     * @param fieldB the field of a object
     * @return the difference between fieldA and fieldB in value
     */
    private String getDeltaLong(String fieldA, String fieldB) {
        String result = "";
        try {
            result = String.valueOf(Long.parseLong(fieldA) - Long.parseLong(fieldB));
        } catch (NumberFormatException ee) {
            result = "0";
        }
        return result;
    }

    /**
     * Get the long value from given the strings which should be a field of a instance of the same
     * type
     *
     * @param fieldA the field of a object
     * @param fieldB the field of a object
     * @param fieldC the field of a object
     * @param object the object of this type
     * @param fieldName the field name but the initial character should be capital
     * @return a field of this object
     */
    private String getDeltaLong(
            DbStatDumpData object, String fieldName, String fieldA, String fieldB, String fieldC) {
        String result = "";
        try {
            if (Long.parseLong(fieldA) < 0 && Long.parseLong(fieldB) > 0) {
                long partA = Long.MAX_VALUE - Long.parseLong(fieldB);
                long partB = Long.parseLong(fieldA) - Long.MIN_VALUE;
                result = String.valueOf(partA + partB);
            } else {
                result = String.valueOf(Long.parseLong(fieldA) - Long.parseLong(fieldB));
                if (Long.parseLong(result) < 0) {
                    result = String.valueOf(Long.parseLong(fieldB) - Long.parseLong(fieldC));
                    long aValue = Long.parseLong(fieldB) + Long.parseLong(result);
                    Class<?> cc = DbStatDumpData.class;
                    Method mm = cc.getMethod("set" + fieldName, new Class[] {String.class});
                    mm.invoke(object, Long.toString(aValue));
                }
            }

        } catch (NumberFormatException ee) {
            result = "0";
        } catch (SecurityException ex) {
            LOGGER.error(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            LOGGER.error(ex.getMessage());
        } catch (IllegalAccessException ex) {
            LOGGER.error(ex.getMessage());
        } catch (NoSuchMethodException ex) {
            LOGGER.error(ex.getMessage());
        } catch (InvocationTargetException ex) {
            LOGGER.error(ex.getMessage());
        }
        return result;
    }

    /**
     * Get the long value from given the strings which should be a field of a instance of the same
     * type
     *
     * @param object a field of this object
     * @param fieldName the field name but the initial character should be capital
     * @param fieldA the field of a object
     * @param fieldB the field of a object
     * @param fieldC the field of a object
     * @param inter float
     * @return String
     */
    private String getDeltaLong(
            DbStatDumpData object,
            String fieldName,
            String fieldA,
            String fieldB,
            String fieldC,
            float inter) {
        String result = "";
        try {
            long temp = 0;
            if (Long.parseLong(fieldA) < 0 && Long.parseLong(fieldB) > 0) {
                long partA = Long.MAX_VALUE - Long.parseLong(fieldB);
                long partB = Long.parseLong(fieldA) - Long.MIN_VALUE;
                temp = partA + partB;
            } else {
                temp = Long.parseLong(fieldA) - Long.parseLong(fieldB);
                if (temp < 0) {
                    temp = Long.parseLong(fieldB) - Long.parseLong(fieldC);
                    long aValue = (Long.parseLong(fieldB) + temp);
                    Class<?> cc = DbStatDumpData.class;
                    Method mm = cc.getMethod("set" + fieldName, new Class[] {String.class});
                    mm.invoke(object, Long.toString(aValue));
                }
            }
            LOGGER.debug(fieldName + "(before divided by interval) = " + temp);
            temp = (long) (temp / inter);
            result = String.valueOf(temp);
            LOGGER.debug(fieldName + "(after divided by interval) = " + result);
        } catch (NumberFormatException ee) {
            result = "0";
        } catch (SecurityException ex) {
            LOGGER.error(ex.getMessage());
        } catch (NoSuchMethodException ex) {
            LOGGER.error(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            LOGGER.error(ex.getMessage());
        } catch (IllegalAccessException ex) {
            LOGGER.error(ex.getMessage());
        } catch (InvocationTargetException ex) {
            LOGGER.error(ex.getMessage());
        }
        return result;
    }

    /**
     * Get the status
     *
     * @return the status
     */
    public boolean getStatus() {
        if (status != null && "success".equals(status.trim())) {
            return true;
        }
        return false;
    }

    /** @param status the status to set */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Get the note
     *
     * @return the note
     */
    public String getNote() {
        return note;
    }

    /** @param note the note to set */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Get the dbname.
     *
     * @return the dbname
     */
    public String getDbname() {
        return dbname;
    }

    /** @param dbname the dbname to set */
    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    /**
     * Get num_file_opens
     *
     * @return the num_file_opens
     */
    public String getNum_file_removes() {
        return num_file_removes;
    }

    /** @param numFileRemoves the num_file_removes to set */
    public void setNum_file_removes(String numFileRemoves) {
        this.num_file_removes = numFileRemoves;
    }

    /**
     * Get the num_file_creates
     *
     * @return the num_file_creates
     */
    public String getNum_file_creates() {
        return num_file_creates;
    }

    /** @param numFileCreates the num_file_creates to set */
    public void setNum_file_creates(String numFileCreates) {
        num_file_creates = numFileCreates;
    }

    /**
     * Get the num_file_ioreads
     *
     * @return the num_file_ioreads
     */
    public String getNum_file_ioreads() {
        return num_file_ioreads;
    }

    /** @param numFileIoreads the num_file_ioreads to set */
    public void setNum_file_ioreads(String numFileIoreads) {
        num_file_ioreads = numFileIoreads;
    }

    /**
     * Get the num_file_iowrites
     *
     * @return the num_file_iowrites
     */
    public String getNum_file_iowrites() {
        return num_file_iowrites;
    }

    /** @param numFileIowrites the num_file_iowrites to set */
    public void setNum_file_iowrites(String numFileIowrites) {
        num_file_iowrites = numFileIowrites;
    }

    /**
     * Get the num_file_iosynches
     *
     * @return the num_file_iosynches
     */
    public String getNum_file_iosynches() {
        return num_file_iosynches;
    }

    /** @param numFileIosynches the num_file_iosynches to set */
    public void setNum_file_iosynches(String numFileIosynches) {
        num_file_iosynches = numFileIosynches;
    }

    /**
     * Get the num_data_page_fetches
     *
     * @return the num_data_page_fetches
     */
    public String getNum_data_page_fetches() {
        return num_data_page_fetches;
    }

    /** @param numDataPageFetches the num_data_page_fetches to set */
    public void setNum_data_page_fetches(String numDataPageFetches) {
        num_data_page_fetches = numDataPageFetches;
    }

    /**
     * Get the num_data_page_dirties
     *
     * @return the num_data_page_dirties
     */
    public String getNum_data_page_dirties() {
        return num_data_page_dirties;
    }

    /** @param numDataPageDirties the num_data_page_dirties to set */
    public void setNum_data_page_dirties(String numDataPageDirties) {
        num_data_page_dirties = numDataPageDirties;
    }

    /**
     * Get the num_data_page_ioreads
     *
     * @return the num_data_page_ioreads
     */
    public String getNum_data_page_ioreads() {
        return num_data_page_ioreads;
    }

    /** @param numDataPageIoreads the num_data_page_ioreads to set */
    public void setNum_data_page_ioreads(String numDataPageIoreads) {
        num_data_page_ioreads = numDataPageIoreads;
    }

    /**
     * Get the num_data_page_iowrites
     *
     * @return the num_data_page_iowrites
     */
    public String getNum_data_page_iowrites() {
        return num_data_page_iowrites;
    }

    /** @param numDataPageIowrites the num_data_page_iowrites to set */
    public void setNum_data_page_iowrites(String numDataPageIowrites) {
        num_data_page_iowrites = numDataPageIowrites;
    }

    /**
     * Get the num_log_page_ioreads
     *
     * @return the num_log_page_ioreads
     */
    public String getNum_log_page_ioreads() {
        return num_log_page_ioreads;
    }

    /** @param numLogPageIoreads the num_log_page_ioreads to set */
    public void setNum_log_page_ioreads(String numLogPageIoreads) {
        num_log_page_ioreads = numLogPageIoreads;
    }

    /**
     * Get the num_log_page_iowrites
     *
     * @return the num_log_page_iowrites
     */
    public String getNum_log_page_iowrites() {
        return num_log_page_iowrites;
    }

    /** @param numLogPageIowrites the num_log_page_iowrites to set */
    public void setNum_log_page_iowrites(String numLogPageIowrites) {
        num_log_page_iowrites = numLogPageIowrites;
    }

    /**
     * Get the num_log_append_records
     *
     * @return the num_log_append_records
     */
    public String getNum_log_append_records() {
        return num_log_append_records;
    }

    /** @param numLogAppendRecords the num_log_append_records to set */
    public void setNum_log_append_records(String numLogAppendRecords) {
        num_log_append_records = numLogAppendRecords;
    }

    /**
     * Get the num_log_archives
     *
     * @return the num_log_archives
     */
    public String getNum_log_archives() {
        return num_log_archives;
    }

    /** @param numLogArchives the num_log_archives to set */
    public void setNum_log_archives(String numLogArchives) {
        num_log_archives = numLogArchives;
    }

    /**
     * Get the num_log_checkpoints
     *
     * @return the num_log_checkpoints
     */
    public String getNum_log_checkpoints() {
        return num_log_checkpoints;
    }

    /** @param numLogCheckpoints the num_log_checkpoints to set */
    public void setNum_log_checkpoints(String numLogCheckpoints) {
        num_log_checkpoints = numLogCheckpoints;
    }

    /**
     * Get the num_page_locks_acquired
     *
     * @return the num_page_locks_acquired
     */
    public String getNum_page_locks_acquired() {
        return num_page_locks_acquired;
    }

    /** @param numPageLocksAcquired the num_page_locks_acquired to set */
    public void setNum_page_locks_acquired(String numPageLocksAcquired) {
        num_page_locks_acquired = numPageLocksAcquired;
    }

    /**
     * Get the num_object_locks_acquired
     *
     * @return the num_object_locks_acquired
     */
    public String getNum_object_locks_acquired() {
        return num_object_locks_acquired;
    }

    /** @param numObjectLocksAcquired the num_object_locks_acquired to set */
    public void setNum_object_locks_acquired(String numObjectLocksAcquired) {
        num_object_locks_acquired = numObjectLocksAcquired;
    }

    /**
     * Get the num_page_locks_converted
     *
     * @return the num_page_locks_converted
     */
    public String getNum_page_locks_converted() {
        return num_page_locks_converted;
    }

    /** @param numPageLocksConverted the num_page_locks_converted to set */
    public void setNum_page_locks_converted(String numPageLocksConverted) {
        num_page_locks_converted = numPageLocksConverted;
    }

    /**
     * Get the num_object_locks_converted
     *
     * @return the num_object_locks_converted
     */
    public String getNum_object_locks_converted() {
        return num_object_locks_converted;
    }

    /** @param numObjectLocksConverted the num_object_locks_converted to set */
    public void setNum_object_locks_converted(String numObjectLocksConverted) {
        num_object_locks_converted = numObjectLocksConverted;
    }

    /**
     * Get the num_page_locks_re_requested
     *
     * @return the num_page_locks_re_requested
     */
    public String getNum_page_locks_re_requested() {
        return num_page_locks_re_requested;
    }

    /** @param numPageLocksReRequested the num_page_locks_re_requested to set */
    public void setNum_page_locks_re_requested(String numPageLocksReRequested) {
        num_page_locks_re_requested = numPageLocksReRequested;
    }

    /**
     * Get the num_object_locks_re_requested
     *
     * @return the num_object_locks_re_requested
     */
    public String getNum_object_locks_re_requested() {
        return num_object_locks_re_requested;
    }

    /** @param numObjectLocksReRequested the num_object_locks_re_requested to set */
    public void setNum_object_locks_re_requested(String numObjectLocksReRequested) {
        num_object_locks_re_requested = numObjectLocksReRequested;
    }

    /**
     * Get the num_page_locks_waits
     *
     * @return the num_page_locks_waits
     */
    public String getNum_page_locks_waits() {
        return num_page_locks_waits;
    }

    /** @param numPageLocksWaits the num_page_locks_waits to set */
    public void setNum_page_locks_waits(String numPageLocksWaits) {
        num_page_locks_waits = numPageLocksWaits;
    }

    /**
     * Get the num_object_locks_waits
     *
     * @return the num_object_locks_waits
     */
    public String getNum_object_locks_waits() {
        return num_object_locks_waits;
    }

    /** @param numObjectLocksWaits the num_object_locks_waits to set */
    public void setNum_object_locks_waits(String numObjectLocksWaits) {
        num_object_locks_waits = numObjectLocksWaits;
    }

    /**
     * Get the num_tran_commits
     *
     * @return the num_tran_commits
     */
    public String getNum_tran_commits() {
        return num_tran_commits;
    }

    /** @param numTranCommits the num_tran_commits to set */
    public void setNum_tran_commits(String numTranCommits) {
        num_tran_commits = numTranCommits;
    }

    /**
     * Get the num_tran_rollbacks
     *
     * @return the num_tran_rollbacks
     */
    public String getNum_tran_rollbacks() {
        return num_tran_rollbacks;
    }

    /** @param numTranRollbacks the num_tran_rollbacks to set */
    public void setNum_tran_rollbacks(String numTranRollbacks) {
        num_tran_rollbacks = numTranRollbacks;
    }

    /**
     * Get the num_tran_savepoints
     *
     * @return the num_tran_savepoints
     */
    public String getNum_tran_savepoints() {
        return num_tran_savepoints;
    }

    /** @param numTranSavepoints the num_tran_savepoints to set */
    public void setNum_tran_savepoints(String numTranSavepoints) {
        num_tran_savepoints = numTranSavepoints;
    }

    /**
     * Get the num_tran_start_topops
     *
     * @return the num_tran_start_topops
     */
    public String getNum_tran_start_topops() {
        return num_tran_start_topops;
    }

    /** @param numTranStartTopops the num_tran_start_topops to set */
    public void setNum_tran_start_topops(String numTranStartTopops) {
        num_tran_start_topops = numTranStartTopops;
    }

    /**
     * Get the num_tran_end_topops
     *
     * @return the num_tran_end_topops
     */
    public String getNum_tran_end_topops() {
        return num_tran_end_topops;
    }

    /** @param numTranEndTopops the num_tran_end_topops to set */
    public void setNum_tran_end_topops(String numTranEndTopops) {
        num_tran_end_topops = numTranEndTopops;
    }

    /**
     * Get the num_tran_interrupts
     *
     * @return the num_tran_interrupts
     */
    public String getNum_tran_interrupts() {
        return num_tran_interrupts;
    }

    /** @param numTranInterrupts the num_tran_interrupts to set */
    public void setNum_tran_interrupts(String numTranInterrupts) {
        num_tran_interrupts = numTranInterrupts;
    }

    /**
     * Get the num_btree_inserts
     *
     * @return the num_btree_inserts
     */
    public String getNum_btree_inserts() {
        return num_btree_inserts;
    }

    /** @param numBtreeInserts the num_btree_inserts to set */
    public void setNum_btree_inserts(String numBtreeInserts) {
        num_btree_inserts = numBtreeInserts;
    }

    /**
     * Get the num_btree_deletes
     *
     * @return the num_btree_deletes
     */
    public String getNum_btree_deletes() {
        return num_btree_deletes;
    }

    /** @param numBtreeDeletes the num_btree_deletes to set */
    public void setNum_btree_deletes(String numBtreeDeletes) {
        num_btree_deletes = numBtreeDeletes;
    }

    /**
     * GEt the num_btree_updates
     *
     * @return the num_btree_updates
     */
    public String getNum_btree_updates() {
        return num_btree_updates;
    }

    /** @param numBtreeUpdates the num_btree_updates to set */
    public void setNum_btree_updates(String numBtreeUpdates) {
        num_btree_updates = numBtreeUpdates;
    }

    /**
     * Get the num_network_requests
     *
     * @return the num_network_requests
     */
    public String getNum_network_requests() {
        return num_network_requests;
    }

    /** @param numNetworkRequests the num_network_requests to set */
    public void setNum_network_requests(String numNetworkRequests) {
        num_network_requests = numNetworkRequests;
    }

    /**
     * Get the num_query_selects
     *
     * @return the num_query_selects
     */
    public String getNum_query_selects() {
        return num_query_selects;
    }

    /** @param numQuerySelects the num_query_selects to set */
    public void setNum_query_selects(String numQuerySelects) {
        num_query_selects = numQuerySelects;
    }

    /**
     * Get the num_query_inserts
     *
     * @return the num_query_inserts
     */
    public String getNum_query_inserts() {
        return num_query_inserts;
    }

    /** @param numQueryInserts the num_query_inserts to set */
    public void setNum_query_inserts(String numQueryInserts) {
        num_query_inserts = numQueryInserts;
    }

    /**
     * Get the num_query_deletes
     *
     * @return the num_query_deletes
     */
    public String getNum_query_deletes() {
        return num_query_deletes;
    }

    /** @param numQueryDeletes the num_query_deletes to set */
    public void setNum_query_deletes(String numQueryDeletes) {
        num_query_deletes = numQueryDeletes;
    }

    /**
     * Get the num_query_updates
     *
     * @return the num_query_updates
     */
    public String getNum_query_updates() {
        return num_query_updates;
    }

    /** @param numQueryUpdates the num_query_updates to set */
    public void setNum_query_updates(String numQueryUpdates) {
        num_query_updates = numQueryUpdates;
    }

    /**
     * Get the num_query_sscans
     *
     * @return the num_query_sscans
     */
    public String getNum_query_sscans() {
        return num_query_sscans;
    }

    /** @param numQuerySscans the num_query_sscans to set */
    public void setNum_query_sscans(String numQuerySscans) {
        num_query_sscans = numQuerySscans;
    }

    /**
     * Get the num_query_iscans
     *
     * @return the num_query_iscans
     */
    public String getNum_query_iscans() {
        return num_query_iscans;
    }

    /** @param numQueryIscans the num_query_iscans to set */
    public void setNum_query_iscans(String numQueryIscans) {
        num_query_iscans = numQueryIscans;
    }

    /**
     * Get the num_query_lscans
     *
     * @return the num_query_lscans
     */
    public String getNum_query_lscans() {
        return num_query_lscans;
    }

    /** @param numQueryLscans the num_query_lscans to set */
    public void setNum_query_lscans(String numQueryLscans) {
        num_query_lscans = numQueryLscans;
    }

    /**
     * Get the num_query_setscans
     *
     * @return the num_query_setscans
     */
    public String getNum_query_setscans() {
        return num_query_setscans;
    }

    /** @param numQuerySetscans the num_query_setscans to set */
    public void setNum_query_setscans(String numQuerySetscans) {
        num_query_setscans = numQuerySetscans;
    }

    /**
     * Get the num_query_methscans
     *
     * @return the num_query_methscans
     */
    public String getNum_query_methscans() {
        return num_query_methscans;
    }

    /** @param numQueryMethscans the num_query_methscans to set */
    public void setNum_query_methscans(String numQueryMethscans) {
        num_query_methscans = numQueryMethscans;
    }

    /**
     * Get the num_query_nljoins
     *
     * @return the num_query_nljoins
     */
    public String getNum_query_nljoins() {
        return num_query_nljoins;
    }

    /** @param numQueryNljoins the num_query_nljoins to set */
    public void setNum_query_nljoins(String numQueryNljoins) {
        num_query_nljoins = numQueryNljoins;
    }

    /**
     * Get the num_query_mjoins
     *
     * @return the num_query_mjoins
     */
    public String getNum_query_mjoins() {
        return num_query_mjoins;
    }

    /** @param numQueryMjoins the num_query_mjoins to set */
    public void setNum_query_mjoins(String numQueryMjoins) {
        num_query_mjoins = numQueryMjoins;
    }

    /**
     * Get the num_query_objfetches
     *
     * @return the num_query_objfetches
     */
    public String getNum_query_objfetches() {
        return num_query_objfetches;
    }

    /** @param numQueryObjfetches the num_query_objfetches to set */
    public void setNum_query_objfetches(String numQueryObjfetches) {
        num_query_objfetches = numQueryObjfetches;
    }

    /**
     * Get the data_page_buffer_hit_ratio
     *
     * @return the data_page_buffer_hit_ratio
     */
    public String getData_page_buffer_hit_ratio() {
        return data_page_buffer_hit_ratio;
    }

    /** @param dataPageBufferHitRatio the data_page_buffer_hit_ratio to set */
    public void setData_page_buffer_hit_ratio(String dataPageBufferHitRatio) {
        data_page_buffer_hit_ratio = dataPageBufferHitRatio;
    }

    /**
     * Get the diagStatusResultMap
     *
     * @return the diagStatusResultMap
     */
    public TreeMap<IDiagPara, String> getDiagStatusResultMap() {
        return diagStatusResultMap;
    }
}
