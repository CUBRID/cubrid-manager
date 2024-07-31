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
import com.cubrid.cubridmanager.core.logs.model.AnalyzeCasLogResultInfo;
import com.cubrid.cubridmanager.core.logs.model.AnalyzeCasLogResultList;

/**
 * A task that defined the task of "analyzecaslog"
 *
 * @author wuyingshi
 * @version 1.0 - 2009-4-3 created by wuyingshi
 */
public class GetAnalyzeCasLogTask extends SocketTask {
    private static final String[] SEND_MSG_ITEMS =
            new String[] {"task", "token", "open", "logfile", "close", "option_t"};

    /**
     * The constructor
     *
     * @param serverInfo
     */
    public GetAnalyzeCasLogTask(ServerInfo serverInfo) {
        super("analyzecaslog", serverInfo, SEND_MSG_ITEMS);
    }

    /**
     * Set the otion_t value.
     *
     * @param param String
     */
    public void setOptionT(String param) {
        super.setMsgItem("option_t", param);
    }

    /**
     * Set the logFiles values.
     *
     * @param param String[]
     */
    public void setLogFiles(String[] param) {
        super.setMsgItem("open", "logfilelist");
        super.setMsgItem("logfile", param);
        super.setMsgItem("close", "logfilelist");
    }

    /**
     * Get result from the response.
     *
     * @return {@link AnalyzeCasLogResultList}
     */
    public AnalyzeCasLogResultList getAnalyzeCasLogResultList() {
        TreeNode response = getResponse();
        if (response == null || (this.getErrorMsg() != null && getErrorMsg().trim().length() > 0)) {
            return null;
        }

        AnalyzeCasLogResultList analyzeCasLogResultList = new AnalyzeCasLogResultList();
        String resultfile = response.getValue("resultfile");
        analyzeCasLogResultList.setResultfile(resultfile);

        // TOOLS-4132 CM can't make the broker_log_top result - fixed by cmserver https api bug
        if (response != null
                && response.getValue("resultlist") != null
                && response.getValues("result") != null) {
            String[] results = response.getValues("result");
            for (int i = 0; i < results.length / 2; i++) {
                AnalyzeCasLogResultInfo analyzeCasLogResultInfo = new AnalyzeCasLogResultInfo();
                analyzeCasLogResultInfo.setQindex((response.getValues("qindex"))[i]);
                if (response.getValue("max") == null) {
                    analyzeCasLogResultInfo.setExecTime((response.getValues("exec_time"))[i]);
                } else {
                    analyzeCasLogResultInfo.setMax((response.getValues("max"))[i]);
                    analyzeCasLogResultInfo.setMin((response.getValues("min"))[i]);
                    analyzeCasLogResultInfo.setAvg((response.getValues("avg"))[i]);
                    analyzeCasLogResultInfo.setCnt((response.getValues("cnt"))[i]);
                    analyzeCasLogResultInfo.setErr((response.getValues("err"))[i]);
                }
                analyzeCasLogResultList.addResultFile(analyzeCasLogResultInfo);
            }
        }

        return analyzeCasLogResultList;
    }
}
