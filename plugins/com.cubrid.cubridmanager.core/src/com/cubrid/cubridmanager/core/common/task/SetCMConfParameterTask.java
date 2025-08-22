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
package com.cubrid.cubridmanager.core.common.task;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.cubridmanager.core.common.model.ConfComments;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This task is responsible to set cm.conf parameter
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-2 created by pangqiren
 */
public class SetCMConfParameterTask extends SocketTask {
    private static final String[] SENDED_MSG_ITEMS =
            new String[] {"task", "token", "confname", "confdata"};

    /**
     * The constructor
     *
     * @param taskName
     * @param serverInfo
     */
    public SetCMConfParameterTask(ServerInfo serverInfo) {
        super("setsysparam", serverInfo, SENDED_MSG_ITEMS);
        this.setMsgItem("confname", "cmconf");
    }

    /**
     * Set cm.conf parameters
     *
     * @param confParameters Map<String, String> The given configure parameters
     */
    public void setConfParameters(Map<String, String> confParameters) {
        List<String> confDatas = new ArrayList<String>();
        confDatas.add("");
        confDatas.add("#");
        ConfComments.addComments(confDatas, ConfComments.cubridCopyrightComments);
        confDatas.add("");
        confDatas.add("#");
        String separator = " ";
        // serverInfo.compareVersionKey("8.3.0") >= 0
        if (CompatibleUtil.isSupportNewFormatOfCMConf(serverInfo)) {
            separator = "=";
        }
        if (confParameters != null) {
            Iterator<Map.Entry<String, String>> it = confParameters.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                String key = entry.getKey();
                String value = entry.getValue();
                if (key != null && value != null && key.length() > 0 && value.length() > 0) {
                    confDatas.add(key + separator + value);
                }
            }
        }
        String[] confDataArr = new String[confDatas.size()];
        confDatas.toArray(confDataArr);
        this.setMsgItem("confdata", confDataArr);
    }

    /**
     * set value in ConfParameters
     *
     * @param confParamList List<String>
     */
    public void setConfContents(List<String> confParamList) {
        String[] confDataArray = confParamList.toArray(new String[confParamList.size()]);
        this.setMsgItem("confdata", confDataArray);
    }
}
