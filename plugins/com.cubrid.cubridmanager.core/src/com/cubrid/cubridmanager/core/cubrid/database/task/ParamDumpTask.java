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
package com.cubrid.cubridmanager.core.cubrid.database.task;

import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import com.cubrid.cubridmanager.core.cubrid.database.model.ParamDumpInfo;
import com.cubrid.cubridmanager.core.utils.ModelUtil.YesNoType;

/**
 * A task that defined the task of "paramdump"
 *
 * @author wuyingshi
 * @version 1.0 - 2010-3-24 created by wuyingshi
 */
public class ParamDumpTask extends SocketTask {

    private static final String[] SEND_MSG_ITEMS = new String[] {"task", "token", "dbname", "both"};

    /**
     * The constructor
     *
     * @param serverInfo
     */
    public ParamDumpTask(ServerInfo serverInfo) {
        super("paramdump", serverInfo, SEND_MSG_ITEMS);
    }

    /**
     * set dbname into msg
     *
     * @param dbName String
     */
    public void setDbName(String dbName) {
        super.setMsgItem("dbname", dbName);
    }

    /**
     * set both
     *
     * @param type YesNoType The given object of YesNoType
     */
    public void setBoth(YesNoType type) {
        super.setMsgItem("both", type.toString().toLowerCase());
    }

    /**
     * get result from the response.
     *
     * @return LogContentInfo
     */
    public ParamDumpInfo getContent() {
        TreeNode response = getResponse();
        if (response == null || (this.getErrorMsg() != null && getErrorMsg().trim().length() > 0)) {
            return null;
        }
        ParamDumpInfo paramDumpInfo = new ParamDumpInfo();
        String dbName = response.getValue("dbname");
        paramDumpInfo.setDbName(dbName);
        int pos1 = 0;
        int pos2 = 0;
        for (int i = 0; i < response.childrenSize(); i++) {
            TreeNode node = response.getChildren().get(i);
            if (node != null
                    && node.getValue("open") != null
                    && node.getValue("open").equals("client")) {
                paramDumpInfo.setClientData(node.getValueByMap());
                paramDumpInfo.getClientData().remove("service");
                pos1 = node.getValues("service")[0].lastIndexOf(":");
                pos2 = node.getValues("service")[1].lastIndexOf(":");
                paramDumpInfo.addClientData(
                        "service" + node.getValues("service")[0].substring(0, pos1),
                        node.getValues("service")[0].substring(pos1 + 1));
                paramDumpInfo.addClientData(
                        "service" + node.getValues("service")[1].substring(0, pos2),
                        node.getValues("service")[1].substring(pos2 + 1));
            }
            if (node != null
                    && node.getValue("open") != null
                    && node.getValue("open").equals("server")) {
                paramDumpInfo.setServerData(node.getValueByMap());
                paramDumpInfo.getServerData().remove("service");
                pos1 = node.getValues("service")[0].lastIndexOf(":");
                pos2 = node.getValues("service")[1].lastIndexOf(":");
                paramDumpInfo.addServerData(
                        "service" + node.getValues("service")[0].substring(0, pos1),
                        node.getValues("service")[0].substring(pos1 + 1));
                paramDumpInfo.addServerData(
                        "service" + node.getValues("service")[1].substring(0, pos2),
                        node.getValues("service")[1].substring(pos2 + 1));
            }
        }
        return paramDumpInfo;
    }
}
