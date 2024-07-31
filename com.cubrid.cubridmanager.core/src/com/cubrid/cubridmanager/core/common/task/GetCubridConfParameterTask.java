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

import com.cubrid.cubridmanager.core.common.model.ConfConstants;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Get CUBRID configuration parameter
 *
 * @author pangqiren
 * @version 1.0 - 2009-5-5 created by pangqiren
 */
public class GetCubridConfParameterTask extends SocketTask {
    private static final String[] SENDED_MSG_ITEMS = new String[] {"task", "token", "confname"};

    /**
     * The constructor
     *
     * @param taskName
     * @param serverInfo
     */
    public GetCubridConfParameterTask(ServerInfo serverInfo) {
        super("getallsysparam", serverInfo, SENDED_MSG_ITEMS);
        this.setMsgItem("confname", "cubridconf");
    }

    /**
     * Get cubrid.conf parameters map
     *
     * @return Map<String, Map<String, String>> The map that stored the configure parameters
     */
    public Map<String, Map<String, String>> getConfParameters() {
        TreeNode response = getResponse();
        if (response == null || (this.getErrorMsg() != null && getErrorMsg().trim().length() > 0)) {
            return null;
        }
        Map<String, Map<String, String>> confMap = new HashMap<String, Map<String, String>>();
        for (int i = 0; i < response.childrenSize(); i++) {
            TreeNode node = response.getChildren().get(i);
            if (node != null && node.getValue("open").equals("conflist")) {
                String[] confData = node.getValues("confdata");
                if (confData != null && confData.length > 0) {
                    buildConf(confMap, confData);
                }
            }
        }
        return confMap;
    }

    /**
     * Get all database name which are auto start
     *
     * @param isCheckService - If check the service parameter
     * @return
     */
    public List<String> getAutoStartDb(boolean isCheckService) {
        List<String> autoStartDBList = new ArrayList<String>();

        Map<String, Map<String, String>> confParas = getConfParameters();
        String services = "";
        String servers = "";
        Map<String, String> map = confParas.get(ConfConstants.SERVICE_SECTION_NAME);

        if (map != null) {
            services = map.get(ConfConstants.COMMON_SERVICE);
            servers = map.get(ConfConstants.COMMON_SERVER);
        }

        if ((isCheckService && services != null && services.indexOf("server") >= 0)
                || !isCheckService) {
            if (servers != null && servers.length() > 0) {
                String[] databases = servers.split(",");
                for (int i = 0; i < databases.length; i++) {
                    String database = databases[i].trim();
                    if (database.length() > 0) {
                        autoStartDBList.add(database);
                    }
                }
            }
        }

        return autoStartDBList;
    }

    /**
     * Build the configure information
     *
     * @param confMap Map<String, Map<String, String>> The given configure information
     * @param confData String[] The given configure data
     */
    private void buildConf(Map<String, Map<String, String>> confMap, String[] confData) {
        Map<String, String> map = null;
        for (int j = 0; j < confData.length; j++) {
            String data = confData[j].trim();
            if (data.length() == 0 || data.indexOf("#") == 0) {
                continue;
            }
            if (data.equals(ConfConstants.SERVICE_SECTION)
                    || data.equals(ConfConstants.COMMON_SECTION)
                    || data.matches("^\\[@.+\\]$")) {
                String secionName = data.trim();
                if (data.equals(ConfConstants.SERVICE_SECTION)
                        || data.equals(ConfConstants.COMMON_SECTION)) {
                    secionName = data.replaceAll("\\[", "");
                    secionName = secionName.replaceAll("\\]", "");
                }
                map = new HashMap<String, String>();
                confMap.put(secionName, map);
                continue;
            }
            String[] entry = data.split("=");
            if (entry != null && entry.length == 2 && map != null) {
                map.put(entry[0].trim(), entry[1].trim());
            }
        }
    }

    /**
     * Get cubrid.conf parameters
     *
     * @return List<String> The map that stored configure parameters
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
