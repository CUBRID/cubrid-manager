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
import com.cubrid.cubridmanager.core.replication.model.TransFileProgressInfo;

/**
 * This task is responsible to get transfer progress
 *
 * @author pangqiren
 * @version 1.0 - 2009-12-29 created by pangqiren
 */
public class GetTransferProgressTask extends SocketTask {
    private static final String[] SEND_MSG_ITEMS = new String[] {"task", "token", "pid"};

    /**
     * The constructor
     *
     * @param serverInfo
     */
    public GetTransferProgressTask(ServerInfo serverInfo) {
        super("view_transfer_progress", serverInfo, SEND_MSG_ITEMS);
    }

    /**
     * Set pid
     *
     * @param pid the pid
     */
    public void setPid(String pid) {
        setMsgItem("pid", pid);
    }

    /**
     * Get transfer file progress information
     *
     * @return the TransFileProgressInfo obj
     */
    public TransFileProgressInfo getProgressInfo() {
        TreeNode response = getResponse();
        if (response == null || (this.getErrorMsg() != null && getErrorMsg().trim().length() > 0)) {
            return null;
        }
        TransFileProgressInfo progressInfo = new TransFileProgressInfo();
        String transferStatus = response.getValue("transfer_status");
        String transferNote = response.getValue("transfer_note");
        String sourceDir = response.getValue("src_dir");
        String destHost = response.getValue("dest_host");
        String destDir = response.getValue("dest_dir");
        String fileNum = response.getValue("num_of_file");
        progressInfo.setTransferStatus(transferStatus);
        progressInfo.setTransferNote(transferNote);
        progressInfo.setSourceDir(sourceDir);
        progressInfo.setDestHost(destHost);
        progressInfo.setDestDir(destDir);
        progressInfo.setFileNum(fileNum);
        for (int i = 0; i < response.childrenSize(); i++) {
            TreeNode node = response.getChildren().get(i);
            if (node != null
                    && node.getValue("open") != null
                    && node.getValue("open").equals("progress_file_list")) {
                progressInfo.setFileProgressMap(node.getValuesByMap());
            }
        }
        return progressInfo;
    }
}
