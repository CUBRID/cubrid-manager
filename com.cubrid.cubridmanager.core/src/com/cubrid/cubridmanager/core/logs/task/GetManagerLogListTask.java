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
import com.cubrid.cubridmanager.core.logs.model.ManagerLogInfo;
import com.cubrid.cubridmanager.core.logs.model.ManagerLogInfoList;
import com.cubrid.cubridmanager.core.logs.model.ManagerLogInfos;

/**
 * A task that defined the task of "loadaccesslog"
 *
 * @author wuyingshi
 * @version 1.0 - 2009-4-3 created by wuyingshi
 */
public class GetManagerLogListTask extends SocketTask {

    private static final String[] SEND_MSG_ITEMS = new String[] {"task", "token"};

    /**
     * The constructor
     *
     * @param serverInfo
     */
    public GetManagerLogListTask(ServerInfo serverInfo) {
        super("loadaccesslog", serverInfo, SEND_MSG_ITEMS);
    }

    /**
     * The constructor
     *
     * @param serverInfo
     * @param charsetName
     */
    public GetManagerLogListTask(ServerInfo serverInfo, String charsetName) {
        super("loadaccesslog", serverInfo, SEND_MSG_ITEMS, null, charsetName);
    }

    /**
     * get result from the response.
     *
     * @return ManagerLogInfos
     */
    public ManagerLogInfos getLogContent() {
        ManagerLogInfos managerLogInfos;
        if (ServerInfo.InterfaceVersion.V2 == this.serverInfo.getInterfaceVersion()) {
            managerLogInfos = getLogContentV2();
        } else {
            managerLogInfos = getLogContentV1();
        }
        return managerLogInfos;
    }

    private ManagerLogInfos getLogContentV1() {
        TreeNode response = getResponse();
        if (response == null || (this.getErrorMsg() != null && getErrorMsg().trim().length() > 0)) {
            return null;
        }

        ManagerLogInfos managerLogInfos = new ManagerLogInfos();
        for (int i = 0; i < response.childrenSize(); i++) {
            TreeNode node = response.getChildren().get(i);

            if (node != null
                    && node.getValue("open") != null
                    && node.getValue("open").equals("accesslog")) {
                ManagerLogInfoList managerLogList = new ManagerLogInfoList(); // for
                String[] users = node.getValues("user");
                String[] tasknames = node.getValues("taskname");
                String[] times = node.getValues("time");
                if (users != null) {
                    addLogToManagerLogList(managerLogList, users, tasknames, times);
                    managerLogInfos.setAccessLog(managerLogList);
                }
            } else if (node != null
                    && node.getValue("open") != null
                    && node.getValue("open").equals("errorlog")) {
                ManagerLogInfoList managerLogList = new ManagerLogInfoList(); // for
                String[] users = null;
                users = node.getValues("user");
                String[] tasknames = node.getValues("taskname");
                String[] times = node.getValues("time");
                String[] errornotes = node.getValues("errornote");
                if (users != null) {
                    addLogToManagerLogList(managerLogList, users, tasknames, times, errornotes);
                    managerLogInfos.setErrorLog(managerLogList);
                }
            }
        }
        return managerLogInfos;
    }

    private ManagerLogInfos getLogContentV2() {
        TreeNode response = getResponse();
        if (response == null || (this.getErrorMsg() != null && getErrorMsg().trim().length() > 0)) {
            return null;
        }

        ManagerLogInfos managerLogInfos = new ManagerLogInfos();
        ManagerLogInfoList managerLogList = new ManagerLogInfoList(); // for
        ManagerLogInfoList errorManagerLogList = new ManagerLogInfoList(); // for

        String[] users = new String[response.childrenSize()];
        String[] tasknames = new String[response.childrenSize()];
        String[] times = new String[response.childrenSize()];
        String[] errorusers = new String[response.childrenSize()];
        String[] errortasknames = new String[response.childrenSize()];
        String[] errortimes = new String[response.childrenSize()];
        String[] errornotes = new String[response.childrenSize()];
        int j = 0, k = 0;
        for (int i = 0; i < response.childrenSize(); i++) {
            TreeNode node = response.getChildren().get(i);

            if (node != null
                    && node.getValue("open") != null
                    && node.getValue("open").equals("accesslog")) {
                users[j] = node.getValue("user");
                tasknames[j] = node.getValue("taskname");
                times[j] = node.getValue("time");
                ++j;
            }
            if (node != null
                    && node.getValue("open") != null
                    && node.getValue("open").equals("errorlog")) {

                errorusers[k] = node.getValue("user");
                errortasknames[k] = node.getValue("taskname");
                errortimes[k] = node.getValue("time");
                errornotes[k] = node.getValue("errornote");
                ++k;
            }
        }
        addLogToManagerLogList(managerLogList, users, tasknames, times);
        managerLogInfos.setAccessLog(managerLogList);
        addLogToManagerLogList(
                errorManagerLogList, errorusers, errortasknames, errortimes, errornotes);
        managerLogInfos.setErrorLog(errorManagerLogList);
        return managerLogInfos;
    }

    /**
     * Add a managerLogInfo to managerLogList
     *
     * @param managerLogList ManagerLogInfoList
     * @param users String[]
     * @param tasknames String[]
     * @param times String[]
     * @param errornotes String[]
     */
    private void addLogToManagerLogList(
            ManagerLogInfoList managerLogList,
            String[] users,
            String[] tasknames,
            String[] times,
            String[] errornotes) {
        for (int j = 0; j < users.length; j++) {
            ManagerLogInfo managerLogInfo = new ManagerLogInfo();
            if (users[j] != null && users[j].trim().length() > 0) {
                managerLogInfo.setUser(users[j]);
                managerLogInfo.setTaskName(tasknames[j]);
                managerLogInfo.setTime(times[j]);
                managerLogInfo.setErrorNote(errornotes[j]);
                if (managerLogInfo != null) {
                    managerLogList.addLog(managerLogInfo);
                }
            }
        }
    }

    /**
     * Add a managerLogInfo to managerLogList
     *
     * @param managerLogList ManagerLogInfoList
     * @param users String[]
     * @param tasknames String[]
     * @param times String[]
     */
    private void addLogToManagerLogList(
            ManagerLogInfoList managerLogList, String[] users, String[] tasknames, String[] times) {
        for (int j = 0; j < users.length; j++) {
            ManagerLogInfo managerLogInfo = new ManagerLogInfo();
            if (users[j] != null && users[j].trim().length() > 0) {
                managerLogInfo.setUser(users[j]);
                managerLogInfo.setTaskName(tasknames[j]);
                managerLogInfo.setTime(times[j]);
                if (managerLogInfo != null) {
                    managerLogList.addLog(managerLogInfo);
                }
            }
        }
    }
}
