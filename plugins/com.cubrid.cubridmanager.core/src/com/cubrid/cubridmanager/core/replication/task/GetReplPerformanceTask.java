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
package com.cubrid.cubridmanager.core.replication.task;

import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Get replication performance task
 *
 * @author pangqiren
 * @version 1.0 - 2009-9-7 created by pangqiren
 */
public class GetReplPerformanceTask extends SocketTask {
    private static final String[] SEND_MSG_ITEMS =
            new String[] {"task", "token", "path", "start", "end"};

    /**
     * The constructor
     *
     * @param serverInfo
     */
    public GetReplPerformanceTask(ServerInfo serverInfo) {
        super("viewlog", serverInfo, SEND_MSG_ITEMS);
    }

    /**
     * Set start value
     *
     * @param startVal the start value
     */
    public void setStart(String startVal) {
        setMsgItem("start", startVal);
    }

    /**
     * Set end value
     *
     * @param endVal the end value
     */
    public void setEnd(String endVal) {
        setMsgItem("end", endVal);
    }

    /**
     * Set file path
     *
     * @param filePath the file path
     */
    public void setFilePath(String filePath) {
        setMsgItem("path", filePath);
    }

    /**
     * Load replication performance data
     *
     * @return the preformance data map
     */
    public List<Map<String, String>> loadPerformanceData() {
        TreeNode response = getResponse();
        if (response == null || (this.getErrorMsg() != null && getErrorMsg().trim().length() > 0)) {
            return null;
        }
        List<Map<String, String>> replDataList = new ArrayList<Map<String, String>>();
        // String total = response.getValue("total");
        for (int i = 0; i < response.childrenSize(); i++) {
            TreeNode node = response.getChildren().get(i);
            if (node.getValue("open") != null && node.getValue("open").equals("log")) {
                String[] lines = node.getValues("line");
                addToReplDataList(replDataList, lines);
            }
        }
        return replDataList;
    }

    /**
     * add map to replDataList
     *
     * @param replDataList the replication data list
     * @param lines the lines
     */
    private void addToReplDataList(List<Map<String, String>> replDataList, String[] lines) {
        if (lines == null || lines.length <= 0) {
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        for (int j = 0; j < lines.length; j++) {
            String[] tempData = lines[j].split("\\s+");
            if (tempData == null || tempData.length != 8) {
                continue;
            }
            String no = tempData[0];
            if (no == null || !no.trim().matches("^\\d+$")) {
                continue;
            }
            String mdbName = tempData[1];
            String tranIndex = tempData[2];
            if (tranIndex == null || !tranIndex.trim().matches("^\\d+$")) {
                continue;
            }
            if (tempData[3] == null || !tempData[3].trim().matches("^\\d{4}/\\d{2}/\\d{2}$")) {
                continue;
            }
            String masterTime = tempData[3] + " " + tempData[4];
            String slaveTime = tempData[5] + " " + tempData[6];
            String delay = tempData[7];
            map.put("no", no);
            map.put("master_db_name", mdbName);
            map.put("tran_index", tranIndex);
            map.put("master_time", masterTime);
            map.put("slave_time", slaveTime);
            map.put("delay", delay);
            replDataList.add(map);
        }
    }
}
