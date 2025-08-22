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

import java.util.Map;
import junit.framework.TestCase;

/**
 * Test the type of dbStatDumpData
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-3-23 created by lizhiqiang
 */
public class DbStatDumpDataTest extends TestCase {
    private DbStatDumpData bean;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        bean = new DbStatDumpData();
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getTaskName()} .
     */
    public final void testGetTaskName() {
        assertEquals("statdump", bean.getTaskName());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#DbStatDumpData(com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData)}
     * .
     */
    public final void testDbStatDumpDataDbStatDumpData() {
        bean.setNum_btree_inserts("bean.numBtreeInserts");
        DbStatDumpData bean_2 = new DbStatDumpData(bean);
        assertNotSame(bean, bean_2);
        assertEquals("bean.numBtreeInserts", bean.getNum_btree_inserts());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#copy_from(com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData)}
     * .
     */
    public final void testCopy_from() {
        DbStatDumpData bean_2 = new DbStatDumpData();
        bean_2.setNum_btree_deletes("bean_2.numBtreeDeletes");
        bean.copy_from(bean_2);
        assertNotSame(bean, bean_2);
        assertEquals("bean_2.numBtreeDeletes", bean.getNum_btree_deletes());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getDelta(com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData,
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData)} .
     */
    public final void testGetDeltaDbStatDumpDataDbStatDumpData() {
        DbStatDumpData bean_1 = new DbStatDumpData();
        DbStatDumpData bean_2 = new DbStatDumpData();
        bean_1.setNum_btree_deletes("100");
        bean_2.setNum_btree_deletes("90");

        bean_1.setNum_btree_inserts("100");
        bean_2.setNum_btree_inserts("90");

        bean_1.setNum_btree_updates("100");
        bean_2.setNum_btree_updates("90");

        bean_1.setNum_data_page_dirties("100");
        bean_2.setNum_data_page_dirties("90");

        bean_1.setNum_data_page_fetches("100");
        bean_2.setNum_data_page_fetches("90");

        bean_1.setNum_data_page_ioreads("100");
        bean_2.setNum_data_page_ioreads("90");

        bean_1.setNum_data_page_iowrites("100");
        bean_2.setNum_data_page_iowrites("90");

        bean.getDelta(bean_1, bean_2);
        assertEquals("10", bean.getNum_btree_deletes());

        bean_1.setNum_data_page_iowrites("100");
        bean_2.setNum_data_page_iowrites("a");
        bean.getDelta(bean_1, bean_2);
        assertEquals("0", bean.getNum_data_page_iowrites());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getDelta(com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData,
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData,
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData)}.
     */
    public final void testGetDeltaDbStatDumpDataDbStatDumpDataDbStatDumpData() {
        DbStatDumpData bean_1 = new DbStatDumpData();
        DbStatDumpData bean_2 = new DbStatDumpData();
        DbStatDumpData bean_3 = new DbStatDumpData();

        bean_1.setNum_btree_deletes("100");
        bean_2.setNum_btree_deletes("90");
        bean_3.setNum_btree_deletes("80");

        bean.getDelta(bean_1, bean_2, bean_3);
        assertEquals("10", bean.getNum_btree_deletes());

        bean_1.setNum_data_page_iowrites("100");
        bean_2.setNum_data_page_iowrites("a");
        bean_3.setNum_data_page_iowrites("100");
        bean.getDelta(bean_1, bean_2);
        assertEquals("0", bean.getNum_data_page_iowrites());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getDelta(com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData,
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData,
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData, float)}.
     */
    public final void testGetDeltaDbStatDumpDataDbStatDumpDataDbStatDumpDataFloat() {
        DbStatDumpData bean_1 = new DbStatDumpData();
        DbStatDumpData bean_2 = new DbStatDumpData();
        DbStatDumpData bean_3 = new DbStatDumpData();

        bean_1.setNum_btree_deletes("100");
        bean_2.setNum_btree_deletes("90");
        bean_3.setNum_btree_deletes("80");

        bean.getDelta(bean_1, bean_2, bean_3, 0.5f);
        assertEquals("20", bean.getNum_btree_deletes());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getStatus()} .
     */
    public final void testGetStatus() {
        bean.setStatus("success");
        assertTrue(bean.getStatus());
        bean.setStatus("failue");
        assertFalse(bean.getStatus());

        bean.setStatus(null);
        assertFalse(bean.getStatus());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNote()} .
     */
    public final void testGetNote() {
        bean.setNote("note");
        assertEquals("note", bean.getNote());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getDbname()} .
     */
    public final void testGetDbname() {
        bean.setDbname("name");
        assertEquals("name", bean.getDbname());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_file_removes()} .
     */
    public final void testGetNum_file_removes() {
        bean.setNum_file_removes("num_file_removes");
        assertEquals("num_file_removes", bean.getNum_file_removes());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_file_creates()} .
     */
    public final void testGetNum_file_creates() {
        bean.setNum_file_creates("numFileCreates");
        assertEquals("numFileCreates", bean.getNum_file_creates());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_file_ioreads()} .
     */
    public final void testGetNum_file_ioreads() {
        bean.setNum_file_ioreads("numFileIoreads");
        assertEquals("numFileIoreads", bean.getNum_file_ioreads());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_file_iowrites()} .
     */
    public final void testGetNum_file_iowrites() {
        bean.setNum_file_iowrites("numFileIowrites");
        assertEquals("numFileIowrites", bean.getNum_file_iowrites());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_file_iosynches()} .
     */
    public final void testGetNum_file_iosynches() {
        bean.setNum_file_iosynches("numFileIosynches");
        assertEquals("numFileIosynches", bean.getNum_file_iosynches());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_data_page_fetches()} .
     */
    public final void testGetNum_data_page_fetches() {
        bean.setNum_data_page_fetches("numDataPageFetches");
        assertEquals("numDataPageFetches", bean.getNum_data_page_fetches());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_data_page_dirties()} .
     */
    public final void testGetNum_data_page_dirties() {
        bean.setNum_data_page_dirties("numDataPageDirties");
        assertEquals("numDataPageDirties", bean.getNum_data_page_dirties());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_data_page_ioreads()} .
     */
    public final void testGetNum_data_page_ioreads() {
        bean.setNum_data_page_ioreads("numDataPageIoreads");
        assertEquals("numDataPageIoreads", bean.getNum_data_page_ioreads());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_data_page_iowrites()} .
     */
    public final void testGetNum_data_page_iowrites() {
        bean.setNum_data_page_iowrites("numDataPageIowrites");
        assertEquals("numDataPageIowrites", bean.getNum_data_page_iowrites());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_log_page_ioreads()} .
     */
    public final void testGetNum_log_page_ioreads() {
        bean.setNum_log_page_ioreads("numLogPageIoreads");
        assertEquals("numLogPageIoreads", bean.getNum_log_page_ioreads());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_log_page_iowrites()} .
     */
    public final void testGetNum_log_page_iowrites() {
        bean.setNum_log_page_iowrites("numLogPageIowrites");
        assertEquals("numLogPageIowrites", bean.getNum_log_page_iowrites());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_log_append_records()} .
     */
    public final void testGetNum_log_append_records() {
        bean.setNum_log_append_records("numLogAppendRecords");
        assertEquals("numLogAppendRecords", bean.getNum_log_append_records());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_log_archives()} .
     */
    public final void testGetNum_log_archives() {
        bean.setNum_log_archives("numLogArchives");
        assertEquals("numLogArchives", bean.getNum_log_archives());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_log_checkpoints()} .
     */
    public final void testGetNum_log_checkpoints() {
        bean.setNum_log_checkpoints("numLogCheckpoints");
        assertEquals("numLogCheckpoints", bean.getNum_log_checkpoints());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_page_locks_acquired()} .
     */
    public final void testGetNum_page_locks_acquired() {
        bean.setNum_page_locks_acquired("numPageLocksAcquired");
        assertEquals("numPageLocksAcquired", bean.getNum_page_locks_acquired());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_object_locks_acquired()}
     * .
     */
    public final void testGetNum_object_locks_acquired() {
        bean.setNum_object_locks_acquired("numObjectLocksAcquired");
        assertEquals("numObjectLocksAcquired", bean.getNum_object_locks_acquired());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_page_locks_converted()}
     * .
     */
    public final void testGetNum_page_locks_converted() {
        bean.setNum_page_locks_converted("numPageLocksConverted");
        assertEquals("numPageLocksConverted", bean.getNum_page_locks_converted());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_object_locks_converted()}
     * .
     */
    public final void testGetNum_object_locks_converted() {
        bean.setNum_object_locks_converted("numObjectLocksConverted");
        assertEquals("numObjectLocksConverted", bean.getNum_object_locks_converted());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_page_locks_re_requested()}
     * .
     */
    public final void testGetNum_page_locks_re_requested() {
        bean.setNum_page_locks_re_requested("numPageLocksReRequested");
        assertEquals("numPageLocksReRequested", bean.getNum_page_locks_re_requested());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_object_locks_re_requested()}
     * .
     */
    public final void testGetNum_object_locks_re_requested() {
        bean.setNum_object_locks_re_requested("numObjectLocksReRequested");
        assertEquals("numObjectLocksReRequested", bean.getNum_object_locks_re_requested());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_page_locks_waits()} .
     */
    public final void testGetNum_page_locks_waits() {
        bean.setNum_page_locks_waits("numPageLocksWaits");
        assertEquals("numPageLocksWaits", bean.getNum_page_locks_waits());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_object_locks_waits()} .
     */
    public final void testGetNum_object_locks_waits() {
        bean.setNum_object_locks_waits("numObjectLocksWaits");
        assertEquals("numObjectLocksWaits", bean.getNum_object_locks_waits());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_tran_commits()} .
     */
    public final void testGetNum_tran_commits() {
        bean.setNum_tran_commits("numTranCommits");
        assertEquals("numTranCommits", bean.getNum_tran_commits());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_tran_rollbacks()} .
     */
    public final void testGetNum_tran_rollbacks() {
        bean.setNum_tran_rollbacks("numTranRollbacks");
        assertEquals("numTranRollbacks", bean.getNum_tran_rollbacks());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_tran_savepoints()} .
     */
    public final void testGetNum_tran_savepoints() {
        bean.setNum_tran_savepoints("numTranSavepoints");
        assertEquals("numTranSavepoints", bean.getNum_tran_savepoints());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_tran_start_topops()} .
     */
    public final void testGetNum_tran_start_topops() {
        bean.setNum_tran_start_topops("numTranStartTopops");
        assertEquals("numTranStartTopops", bean.getNum_tran_start_topops());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_tran_end_topops()} .
     */
    public final void testGetNum_tran_end_topops() {
        bean.setNum_tran_end_topops("numTranEndTopops");
        assertEquals("numTranEndTopops", bean.getNum_tran_end_topops());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_tran_interrupts()} .
     */
    public final void testGetNum_tran_interrupts() {
        bean.setNum_tran_interrupts("numTranInterrupts");
        assertEquals("numTranInterrupts", bean.getNum_tran_interrupts());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_btree_inserts()} .
     */
    public final void testGetNum_btree_inserts() {
        bean.setNum_btree_inserts("numBtreeInserts");
        assertEquals("numBtreeInserts", bean.getNum_btree_inserts());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_btree_deletes()} .
     */
    public final void testGetNum_btree_deletes() {
        bean.setNum_btree_deletes("numBtreeDeletes");
        assertEquals("numBtreeDeletes", bean.getNum_btree_deletes());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_btree_updates()} .
     */
    public final void testGetNum_btree_updates() {
        bean.setNum_btree_updates("numBtreeUpdates");
        assertEquals("numBtreeUpdates", bean.getNum_btree_updates());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_network_requests()} .
     */
    public final void testGetNum_network_requests() {
        bean.setNum_network_requests("numNetworkRequests");
        assertEquals("numNetworkRequests", bean.getNum_network_requests());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_query_selects()} .
     */
    public final void testGetNum_query_selects() {
        bean.setNum_query_selects("numQuerySelects");
        assertEquals("numQuerySelects", bean.getNum_query_selects());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_query_inserts()} .
     */
    public final void testGetNum_query_inserts() {
        bean.setNum_query_inserts("numQueryInserts");
        assertEquals("numQueryInserts", bean.getNum_query_inserts());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_query_deletes()} .
     */
    public final void testGetNum_query_deletes() {
        bean.setNum_query_deletes("numQueryDeletes");
        assertEquals("numQueryDeletes", bean.getNum_query_deletes());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_query_updates()} .
     */
    public final void testGetNum_query_updates() {
        bean.setNum_query_updates("numQueryUpdates");
        assertEquals("numQueryUpdates", bean.getNum_query_updates());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_query_sscans()} .
     */
    public final void testGetNum_query_sscans() {
        bean.setNum_query_sscans("numQuerySscans");
        assertEquals("numQuerySscans", bean.getNum_query_sscans());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_query_iscans()} .
     */
    public final void testGetNum_query_iscans() {
        bean.setNum_query_iscans("numQueryIscans");
        assertEquals("numQueryIscans", bean.getNum_query_iscans());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_query_lscans()} .
     */
    public final void testGetNum_query_lscans() {
        bean.setNum_query_lscans("numQueryLscans");
        assertEquals("numQueryLscans", bean.getNum_query_lscans());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_query_setscans()} .
     */
    public final void testGetNum_query_setscans() {
        bean.setNum_query_setscans("numQuerySetscans");
        assertEquals("numQuerySetscans", bean.getNum_query_setscans());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_query_methscans()} .
     */
    public final void testGetNum_query_methscans() {
        bean.setNum_query_methscans("numQueryMethscans");
        assertEquals("numQueryMethscans", bean.getNum_query_methscans());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_query_nljoins()} .
     */
    public final void testGetNum_query_nljoins() {
        bean.setNum_query_nljoins("numQueryNljoins");
        assertEquals("numQueryNljoins", bean.getNum_query_nljoins());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_query_mjoins()} .
     */
    public final void testGetNum_query_mjoins() {
        bean.setNum_query_mjoins("numQueryMjoins");
        assertEquals("numQueryMjoins", bean.getNum_query_mjoins());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getNum_query_objfetches()} .
     */
    public final void testGetNum_query_objfetches() {
        bean.setNum_query_objfetches("numQueryObjfetches");
        assertEquals("numQueryObjfetches", bean.getNum_query_objfetches());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getData_page_buffer_hit_ratio()}
     * .
     */
    public final void testGetData_page_buffer_hit_ratio() {
        bean.setData_page_buffer_hit_ratio("dataPageBufferHitRatio");
        assertEquals("dataPageBufferHitRatio", bean.getData_page_buffer_hit_ratio());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData#getDiagStatusResultMap()} .
     */
    public final void testGetDiagStatusResultMap() {
        Map<IDiagPara, String> map = bean.getDiagStatusResultMap();
        assertFalse(map.isEmpty());
        assertEquals("0", map.get(DbStatDumpEnum.num_file_removes));

        assertEquals(DbStatDumpEnum.num_file_removes.getName(), "num_file_removes");

        DbStatDumpData bean_1 = new DbStatDumpData();
        DbStatDumpData bean_2 = new DbStatDumpData();
        bean.getDelta(bean_1, bean_2);

        assertFalse(map.isEmpty());
    }

    public void testGetDelta() {
        DbStatDumpData a = new DbStatDumpData();
        DbStatDumpData b = new DbStatDumpData();

        new DbStatDumpData().getDelta(a, b);

        a = new DbStatDumpData();
        a.setNum_btree_deletes("1");
        b = new DbStatDumpData();
        b.setNum_btree_deletes("1");
        new DbStatDumpData().getDelta(a, b);

        a = new DbStatDumpData();
        a.setNum_btree_inserts("1");
        b = new DbStatDumpData();
        b.setNum_btree_inserts("1");
        new DbStatDumpData().getDelta(a, b);

        a = new DbStatDumpData();
        a.setNum_file_iowrites("1");
        b = new DbStatDumpData();
        b.setNum_file_iowrites("1");
        new DbStatDumpData().getDelta(a, b);

        a = new DbStatDumpData();
        a.setNum_page_locks_acquired("1");
        b = new DbStatDumpData();
        b.setNum_page_locks_acquired("1");
        new DbStatDumpData().getDelta(a, b);

        a = new DbStatDumpData();
        a.setNum_query_deletes("2");
        b = new DbStatDumpData();
        b.setNum_query_deletes("2");
        new DbStatDumpData().getDelta(a, b);
    }
}
