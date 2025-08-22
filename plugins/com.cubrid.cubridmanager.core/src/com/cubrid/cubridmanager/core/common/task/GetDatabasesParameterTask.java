/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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

import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Get Databases Parameter Task
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-11-26 created by Kevin.Wang
 */
public class GetDatabasesParameterTask extends SocketTask {
    private static final String[] SENDED_MSG_ITEMS = new String[] {"task", "token", "confname"};

    /**
     * The constructor
     *
     * @param taskName
     * @param serverInfo
     */
    public GetDatabasesParameterTask(ServerInfo serverInfo) {
        super("getallsysparam", serverInfo, SENDED_MSG_ITEMS);
        this.setMsgItem("confname", "databases");
    }

    /**
     * Get databases.txt config parameters
     *
     * @return
     */
    public List<Map<String, String>> getConfParameters() {
        TreeNode response = getResponse();
        if (response == null || (this.getErrorMsg() != null && getErrorMsg().trim().length() > 0)) {
            return null;
        }
        List<Map<String, String>> confMapList = new ArrayList<Map<String, String>>();
        for (int i = 0; i < response.childrenSize(); i++) {
            TreeNode node = response.getChildren().get(i);
            if (node != null && node.getValue("open").equals("conflist")) {
                buildConf(confMapList, node);
            }
        }
        return confMapList;
    }

    /**
     * Build configure parameters
     *
     * @param confMap Map<String, String> The given confMap
     * @param node TreeNode The given object of TreeNode
     */
    private void buildConf(List<Map<String, String>> confMapList, TreeNode node) {
        String[] confData = node.getValues("confdata");
        if (confData != null && confData.length > 0) {
            for (int j = 0; j < confData.length; j++) {
                String data = confData[j].trim();
                if (data.length() == 0 || data.indexOf("#") == 0) {
                    continue;
                }
                String[] entry = data.split("\\s+");
                if (entry.length < 5) {
                    continue;
                }

                Map<String, String> confMap = new HashMap<String, String>();

                confMap.put(IConst.DATABASES_DB_NAME, entry[0]);
                confMap.put(IConst.DATABASES_VOL_PATH, entry[1]);
                confMap.put(IConst.DATABASES_DB_HOST, entry[2]);
                confMap.put(IConst.DATABASES_LOG_PATH, entry[3]);
                confMap.put(IConst.DATABASES_LOG_BASE_PATH, entry[4]);

                confMapList.add(confMap);
            }
        }
    }

    /**
     * Get databases.txt config parameters content
     *
     * @return
     */
    public List<String> getConfContents() {
        String[] confData = null;
        List<String> confContents = new ArrayList<String>();
        TreeNode response = getResponse();
        if (response == null || (this.getErrorMsg() != null && getErrorMsg().trim().length() > 0)) {
            return null;
        }
        for (int i = 0; i < response.childrenSize(); i++) {
            TreeNode node = response.getChildren().get(i);
            if (node != null && node.getValue("open").equals("conflist")) {
                confData = node.getValues("confdata");
            }
        }
        if (confData != null) {
            for (String line : confData) {
                confContents.add(line);
            }
        }
        return confContents;
    }
}
