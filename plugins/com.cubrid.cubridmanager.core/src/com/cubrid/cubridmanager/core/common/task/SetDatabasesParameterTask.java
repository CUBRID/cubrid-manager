/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search
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

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Set Databases Parameter Task
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-11-26 created by Kevin.Wang
 */
public class SetDatabasesParameterTask extends SocketTask {
    private static final String[] SENDED_MSG_ITEMS =
            new String[] {"task", "token", "confname", "confdata"};

    /**
     * @param taskName
     * @param serverInfo
     */
    public SetDatabasesParameterTask(ServerInfo serverInfo) {
        super("setsysparam", serverInfo, SENDED_MSG_ITEMS);
        this.setMsgItem("confname", "databases");
    }

    /**
     * Set the configure parameters
     *
     * @param confMapList
     */
    public void setConfParameters(List<Map<String, String>> confMapList) {
        List<String> confDatas = new ArrayList<String>();
        confDatas.add("#db-name \t vol-path \t  db-host \t log-path \t lob-base-path");
        String separator = "\t";
        for (Map<String, String> entry : confMapList) {
            StringBuilder sb = new StringBuilder();

            sb.append(entry.get(IConst.DATABASES_DB_NAME)).append(separator);
            sb.append(entry.get(IConst.DATABASES_VOL_PATH)).append(separator);
            sb.append(entry.get(IConst.DATABASES_DB_HOST)).append(separator);
            sb.append(entry.get(IConst.DATABASES_LOG_PATH)).append(separator);
            sb.append(entry.get(IConst.DATABASES_LOG_BASE_PATH)).append(StringUtil.NEWLINE);

            confDatas.add(sb.toString());
        }

        String[] confDataArr = new String[confDatas.size()];
        confDatas.toArray(confDataArr);
        this.setMsgItem("confdata", confDataArr);
    }
}
