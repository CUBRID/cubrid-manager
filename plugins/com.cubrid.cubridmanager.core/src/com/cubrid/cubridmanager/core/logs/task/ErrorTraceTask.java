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
package com.cubrid.cubridmanager.core.logs.task;

import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import java.util.ArrayList;
import java.util.List;

/**
 * Trace the error log task
 *
 * @author pangqiren
 * @version 1.0 - 2011-2-24 created by pangqiren
 */
public class ErrorTraceTask extends SocketTask {

    private static final String[] SEND_MSG_ITEMS =
            new String[] {"task", "token", "logpath", "errtime", "eid"};

    /**
     * The Constructor
     *
     * @param serverInfo
     */
    public ErrorTraceTask(ServerInfo serverInfo) {
        super("errortrace", serverInfo, SEND_MSG_ITEMS);
    }

    /**
     * set the log path
     *
     * @param logpath String
     */
    public void setLogPath(String logpath) {
        super.setMsgItem("logpath", logpath);
    }

    /**
     * set the error time.
     *
     * @param errTime String
     */
    public void setErrTime(String errTime) {
        super.setMsgItem("errtime", errTime);
    }

    /**
     * set the error id
     *
     * @param errId String
     */
    public void setErrId(String errId) {
        super.setMsgItem("eid", errId);
    }

    /**
     * Get the error logs
     *
     * @return List<String>
     */
    public List<String> getErrorLogs() {
        TreeNode response = getResponse();
        if (response == null || (this.getErrorMsg() != null && getErrorMsg().trim().length() > 0)) {
            return null;
        }
        List<String> errLogList = new ArrayList<String>();
        for (int i = 0; i < response.childrenSize(); i++) {
            TreeNode node = response.getChildren().get(i);
            if (node != null
                    && node.getValue("open") != null
                    && node.getValue("open").equals("errbloc")) {
                String[] lines = node.getValues("line");
                for (int j = 0; lines != null && j < lines.length; j++) {
                    String str = lines[j];
                    if (str != null && str.trim().length() > 0) {
                        errLogList.add(str);
                    }
                }
            }
        }
        return errLogList;
    }
}
