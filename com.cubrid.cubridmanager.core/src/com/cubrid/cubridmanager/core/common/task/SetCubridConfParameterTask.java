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

import com.cubrid.cubridmanager.core.common.model.ConfComments;
import com.cubrid.cubridmanager.core.common.model.ConfConstants;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This task is responsible to set cubrid.conf configuration parameter
 *
 * @author pangqiren
 * @version 1.0 - 2009-5-5 created by pangqiren
 */
public class SetCubridConfParameterTask extends SocketTask {
    private static final String[] SENDED_MSG_ITEMS =
            new String[] {"task", "token", "confname", "confdata"};

    /**
     * @param taskName
     * @param serverInfo
     */
    public SetCubridConfParameterTask(ServerInfo serverInfo) {
        super("setsysparam", serverInfo, SENDED_MSG_ITEMS);
        this.setMsgItem("confname", "cubridconf");
    }

    /**
     * Set the configure parameters
     *
     * @param confParameters Map<String, Map<String, String>> The given map that stored configure
     *     parameter
     */
    public void setConfParameters(Map<String, Map<String, String>> confParameters) {
        List<String> confDatas = new ArrayList<String>();
        confDatas.add("");
        confDatas.add("#");
        ConfComments.addComments(confDatas, ConfComments.cubridCopyrightComments);
        confDatas.add("");
        confDatas.add("#");
        confDatas.add("# $Id$");
        confDatas.add("#");
        confDatas.add("# cubrid.conf");
        confDatas.add("#");
        confDatas.add("# For complete information on parameters, see the CUBRID");
        confDatas.add("# Database Administration Guide chapter on System Parameters");
        confDatas.add("");
        // add service section
        Map<String, String> map = confParameters.get(ConfConstants.SERVICE_SECTION_NAME);
        if (map != null) {
            confDatas.add(ConfComments.getComments(ConfConstants.SERVICE_SECTION));
            confDatas.add(ConfConstants.SERVICE_SECTION);
            confDatas.add("");

            Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                String key = entry.getKey();
                String value = entry.getValue();
                if (key != null && value != null && key.length() > 0 && value.length() > 0) {
                    ConfComments.addComments(confDatas, ConfComments.getComments(key));
                    confDatas.add(key + "=" + value);
                    confDatas.add("");
                }
            }
        }
        confDatas.add("");
        // add common section
        map = confParameters.get(ConfConstants.COMMON_SECTION_NAME);
        boolean isChangeHaModeValueInDb = false;
        if (map != null) {
            // if ha_mode value is off or no in common section,this value can not be on or yes in
            // database server section
            String haModeValueInComm = map.get(ConfConstants.HA_MODE);
            isChangeHaModeValueInDb =
                    haModeValueInComm == null
                            || haModeValueInComm.equalsIgnoreCase("no")
                            || haModeValueInComm.equalsIgnoreCase("off");

            ConfComments.addComments(
                    confDatas, ConfComments.getComments(ConfConstants.COMMON_SECTION));
            confDatas.add(ConfConstants.COMMON_SECTION);
            confDatas.add("");
            Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                String key = entry.getKey();
                String value = entry.getValue();
                if (key != null && value != null && key.length() > 0 && value.length() > 0) {
                    ConfComments.addComments(confDatas, ConfComments.getComments(key));
                    confDatas.add(key + "=" + value);
                    confDatas.add("");
                }
            }
        }
        // add some database section
        Iterator<Map.Entry<String, Map<String, String>>> it = confParameters.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Map<String, String>> entry = it.next();
            String sectionName = entry.getKey();
            if (sectionName.equals(ConfConstants.COMMON_SECTION_NAME)
                    || sectionName.equals(ConfConstants.SERVICE_SECTION_NAME)) {
                continue;
            }
            Map<String, String> databaseParameterMap = entry.getValue();
            if (databaseParameterMap == null) {
                continue;
            }
            if (databaseParameterMap.isEmpty()) {
                continue;
            }
            confDatas.add("");
            confDatas.add(sectionName);

            Iterator<Map.Entry<String, String>> dbParaIt =
                    databaseParameterMap.entrySet().iterator();
            while (dbParaIt.hasNext()) {
                Map.Entry<String, String> dbParaEntry = dbParaIt.next();
                String key = dbParaEntry.getKey();
                String value = dbParaEntry.getValue();
                if (key != null && value != null && key.length() > 0 && value.length() > 0) {
                    ConfComments.addComments(confDatas, ConfComments.getComments(key));
                    if (key.equals(ConfConstants.HA_MODE) && isChangeHaModeValueInDb) {
                        value = "off";
                    }
                    confDatas.add(key + "=" + value);
                    confDatas.add("");
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
