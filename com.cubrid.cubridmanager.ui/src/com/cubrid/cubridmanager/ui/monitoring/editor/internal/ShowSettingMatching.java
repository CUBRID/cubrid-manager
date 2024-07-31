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
package com.cubrid.cubridmanager.ui.monitoring.editor.internal;

import com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagEnum;
import com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpEnum;
import org.eclipse.swt.graphics.RGB;

/**
 * This type is responsible for make the key and the relevant instance of ShowSetting
 * matching.Generally,the fields of instance of ShowSetting will be set.
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-4-6 created by lizhiqiang
 */
public final class ShowSettingMatching {
    // Constructor
    private ShowSettingMatching() {
        // do nothing
    }

    /**
     * The general method which make the key and the relevant instance of ShowSetting matching based
     * on the different monitor type.
     *
     * @param key the series name
     * @param showSetting the instance of ShowSetting relevant the series name
     * @param type the monitor type
     */
    public static void match(String key, ShowSetting showSetting, MonitorType type) {
        switch (type) {
            case BROKER:
                matchBrokerDiag(key, showSetting, true);
                break;
            case DATABASE:
                matchDbDump(key, showSetting);
                break;
            default:
        }
    }

    /**
     * The general method which make the key and the relevant instance of ShowSetting matching based
     * on the different monitor type.
     *
     * @param key the series name
     * @param showSetting the instance of ShowSetting relevant the series name
     * @param type the monitor type
     * @param isNewBrokerDiag boolean
     */
    public static void match(
            String key, ShowSetting showSetting, MonitorType type, boolean isNewBrokerDiag) {
        switch (type) {
            case BROKER:
                matchBrokerDiag(key, showSetting, isNewBrokerDiag);
                break;
            case DATABASE:
                matchDbDump(key, showSetting);
                break;
            default:
        }
    }

    /**
     * Make the key and the relevant instance of ShowSetting matching when the monitor type is
     * broker monitor.
     *
     * @param key the series name
     * @param showSetting the instance of ShowSetting relevant the series name
     * @param isNewBrokerDiag boolean
     */
    private static void matchBrokerDiag(
            String key, ShowSetting showSetting, boolean isNewBrokerDiag) {
        if (BrokerDiagEnum.RPS.getName().equals(key)) {
            showSetting.setSeriesRgb(new RGB(255, 0, 0));
        } else if (BrokerDiagEnum.QPS.getName().equals(key)) {
            showSetting.setChecked(true);
            showSetting.setSeriesRgb(new RGB(0, 255, 0));
        } else if (BrokerDiagEnum.TPS.getName().equals(key)) {
            showSetting.setChecked(true);
            showSetting.setSeriesRgb(new RGB(0, 0, 255));
        } else if (BrokerDiagEnum.ACTIVE_SESSION.getName().equals(key) && !isNewBrokerDiag) {
            showSetting.setChecked(true);
            showSetting.setSeriesRgb(new RGB(210, 105, 30));
        } else if (BrokerDiagEnum.SESSION.getName().equals(key) && isNewBrokerDiag) {
            showSetting.setChecked(true);
            showSetting.setSeriesRgb(new RGB(210, 105, 30));
        } else if (BrokerDiagEnum.ACTIVE.getName().equals(key) && isNewBrokerDiag) {
            showSetting.setSeriesRgb(new RGB(30, 105, 210));
        } else if (BrokerDiagEnum.LONG_Q.getName().equals(key)) {
            showSetting.setSeriesRgb(new RGB(100, 149, 237));
        } else if (BrokerDiagEnum.ERR_Q.getName().equals(key)) {
            showSetting.setSeriesRgb(new RGB(218, 165, 32));
        } else if (BrokerDiagEnum.LONG_T.getName().equals(key)) {
            showSetting.setSeriesRgb(new RGB(176, 224, 230));
        } else {
            showSetting.setSeriesRgb(new RGB(173, 255, 47));
        }
    }

    /**
     * Make the key and the relevant instance of ShowSetting matching when the monitor type is
     * database monitor.
     *
     * @param key the series name
     * @param showSetting the instance of ShowSetting relevant the series name
     */
    private static void matchDbDump(String key, ShowSetting showSetting) {
        if (DbStatDumpEnum.num_file_removes.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(255, 0, 0));
        } else if (DbStatDumpEnum.num_file_creates.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(0, 255, 0));
        } else if (DbStatDumpEnum.num_file_ioreads.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(218, 165, 32));
        } else if (DbStatDumpEnum.num_file_iowrites.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(210, 105, 30));
        } else if (DbStatDumpEnum.num_file_iosynches.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(100, 149, 237));

        } else if (DbStatDumpEnum.num_data_page_fetches.name().equals(key)) {
            showSetting.setChecked(true);
            showSetting.setSeriesRgb(new RGB(0, 0, 255));
        } else if (DbStatDumpEnum.num_data_page_dirties.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(255, 182, 193));
        } else if (DbStatDumpEnum.num_data_page_ioreads.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(184, 134, 11));
        } else if (DbStatDumpEnum.num_data_page_iowrites.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(245, 222, 179));
        } else if (DbStatDumpEnum.num_log_page_ioreads.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(221, 160, 221));

        } else if (DbStatDumpEnum.num_log_page_iowrites.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(112, 128, 144));
        } else if (DbStatDumpEnum.num_log_append_records.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(255, 228, 225));
        } else if (DbStatDumpEnum.num_log_archives.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(255, 250, 205));
        } else if (DbStatDumpEnum.num_log_checkpoints.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(211, 211, 211));

        } else if (DbStatDumpEnum.num_page_locks_acquired.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(128, 128, 0));
        } else if (DbStatDumpEnum.num_page_locks_converted.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(50, 205, 50));
        } else if (DbStatDumpEnum.num_page_locks_re_requested.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(102, 205, 170));
        } else if (DbStatDumpEnum.num_page_locks_waits.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(64, 224, 208));
        } else if (DbStatDumpEnum.num_object_locks_acquired.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(144, 238, 144));
        } else if (DbStatDumpEnum.num_object_locks_converted.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(0, 250, 154));
        } else if (DbStatDumpEnum.num_object_locks_re_requested.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(47, 79, 79));
        } else if (DbStatDumpEnum.num_object_locks_waits.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(127, 255, 212));

        } else if (DbStatDumpEnum.num_tran_commits.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(0, 0, 139));
        } else if (DbStatDumpEnum.num_tran_rollbacks.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(100, 149, 237));
        } else if (DbStatDumpEnum.num_tran_savepoints.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(95, 158, 160));
        } else if (DbStatDumpEnum.num_tran_start_topops.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(30, 144, 255));
        } else if (DbStatDumpEnum.num_tran_end_topops.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(135, 206, 250));
        } else if (DbStatDumpEnum.num_tran_interrupts.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(106, 90, 205));

        } else if (DbStatDumpEnum.num_btree_inserts.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(210, 105, 30));
        } else if (DbStatDumpEnum.num_btree_deletes.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(139, 0, 139));
        } else if (DbStatDumpEnum.num_btree_updates.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(205, 92, 92));

        } else if (DbStatDumpEnum.num_query_selects.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(60, 114, 123));
        } else if (DbStatDumpEnum.num_query_inserts.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(67, 172, 255));
        } else if (DbStatDumpEnum.num_query_deletes.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(176, 172, 255));
        } else if (DbStatDumpEnum.num_query_updates.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(60, 114, 187));
        } else if (DbStatDumpEnum.num_query_sscans.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(60, 114, 69));
        } else if (DbStatDumpEnum.num_query_iscans.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(116, 114, 69));
        } else if (DbStatDumpEnum.num_query_lscans.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(150, 114, 69));
        } else if (DbStatDumpEnum.num_query_setscans.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(114, 114, 178));
        } else if (DbStatDumpEnum.num_query_methscans.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(168, 114, 178));
        } else if (DbStatDumpEnum.num_query_nljoins.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(125, 114, 178));
        } else if (DbStatDumpEnum.num_query_mjoins.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(63, 114, 178));
        } else if (DbStatDumpEnum.num_query_objfetches.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(146, 114, 178));

        } else if (DbStatDumpEnum.num_network_requests.name().equals(key)) {
            showSetting.setSeriesRgb(new RGB(255, 0, 255));
        } else if (DbStatDumpEnum.data_page_buffer_hit_ratio.name().equals(key)) {
            showSetting.setChecked(true);
            showSetting.setSeriesRgb(new RGB(255, 0, 0));
        } else {
            showSetting.setSeriesRgb(new RGB(173, 255, 47));
        }
    }
}
