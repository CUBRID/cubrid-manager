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
package com.cubrid.cubridmanager.core.utils;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.common.model.ConfConstants;
import com.cubrid.cubridmanager.core.common.task.GetCubridConfParameterTask;
import java.util.List;
import java.util.Map;

/**
 * Core utility
 *
 * @author pangqiren
 * @version 1.0 - 2011-4-2 created by pangqiren
 */
public final class CoreUtils {

    private CoreUtils() {}

    /**
     * Change ha_mode value from cubrid.conf, then return the changed content
     *
     * @param task GetCubridConfParameterTask
     * @param contentList List<String>
     * @param dbName String
     * @return List<String>
     */
    public static List<String> changeHAModeFromCubridConf(
            GetCubridConfParameterTask task, List<String> contentList, String dbName) {
        Map<String, Map<String, String>> confParas = task.getConfParameters();
        List<String> cubridConfContentList = contentList;
        if (cubridConfContentList == null) {
            cubridConfContentList = task.getConfContents();
        }
        if (cubridConfContentList == null) {
            return null;
        }
        Map<String, String> commonMap = confParas.get(ConfConstants.COMMON_SECTION_NAME);
        if (commonMap == null || commonMap.get(ConfConstants.HA_MODE) == null) {
            return null;
        }
        String haMode = commonMap.get(ConfConstants.HA_MODE);
        if (haMode == null || haMode.equalsIgnoreCase("off") || haMode.equalsIgnoreCase("no")) {
            return null;
        }
        Map<String, String> dbMap = confParas.get("[@" + dbName + "]");
        String dbSection = "[@" + dbName + "]";
        if (dbMap == null) {
            cubridConfContentList.add("");
            cubridConfContentList.add(dbSection);
            cubridConfContentList.add("");
            cubridConfContentList.add(ConfConstants.HA_MODE + "=off");
        } else if ("ON".equalsIgnoreCase(dbMap.get(ConfConstants.HA_MODE))
                || "YES".equalsIgnoreCase(dbMap.get(ConfConstants.HA_MODE))
                || "REPLICA".equalsIgnoreCase(dbMap.get(ConfConstants.HA_MODE))) {
            boolean isDbSection = false;
            for (int i = 0; i < cubridConfContentList.size(); i++) {
                String line = cubridConfContentList.get(i);
                if (!isDbSection) {
                    continue;
                }
                if (line.trim().equals(dbSection)) {
                    isDbSection = true;
                }
                if (line.trim().startsWith(ConfConstants.HA_MODE)) {
                    line = ConfConstants.HA_MODE + "=off";
                    cubridConfContentList.set(i, line);
                }
            }
        }
        return cubridConfContentList;
    }

    /**
     * Add the database to server in service section
     *
     * @param task GetCubridConfParameterTask
     * @param contentList List<String>
     * @param dbName String
     * @return List<String>
     */
    public static List<String> addDatabaseToServiceServer(
            GetCubridConfParameterTask task, List<String> contentList, String dbName) {
        List<String> cubridConfContentList = contentList;
        if (cubridConfContentList == null) {
            cubridConfContentList = task.getConfContents();
        }
        if (cubridConfContentList == null) {
            return null;
        }
        int serviceStartLine = 0;
        int serverInsertLine = 0;
        for (int i = 0; i < cubridConfContentList.size(); i++) {
            String data = cubridConfContentList.get(i).trim();
            if (data.length() == 0) {
                continue;
            }
            if (data.equals(ConfConstants.SERVICE_SECTION)) {
                serviceStartLine = i;
                serverInsertLine = i + 1;
                continue;
            }
            if (serviceStartLine > 0 && data.matches("^service\\s*=.*")) {
                serverInsertLine = i + 1;
                continue;
            }
            if (serviceStartLine > 0 && data.matches("^server\\s*=.*")) {
                String value = data.split("=")[1].trim();
                String[] allDbNames = value.split(",");
                for (String name : allDbNames) {
                    if (name.trim().equalsIgnoreCase(dbName)) {
                        return null;
                    }
                }
                value = value.length() == 0 ? dbName : value + "," + dbName;
                cubridConfContentList.set(i, "server=" + value);
                return cubridConfContentList;
            } else if (serviceStartLine > 0 && data.matches("^#\\s*server\\s*=.*")) {
                cubridConfContentList.set(i, "server=" + dbName);
                return cubridConfContentList;
            }
        }
        cubridConfContentList.add(serverInsertLine, "server=" + dbName);
        return cubridConfContentList;
    }

    /**
     * Rename the database in service section
     *
     * @param task GetCubridConfParameterTask
     * @param contentList List<String>
     * @param dbName String
     * @return List<String>
     */
    public static List<String> renameDatabaseFromServiceServer(
            GetCubridConfParameterTask task,
            List<String> contentList,
            String dbNameOld,
            String dbNameNew) {
        List<String> cubridConfContentList = contentList;
        if (cubridConfContentList == null) {
            cubridConfContentList = task.getConfContents();
        }
        if (cubridConfContentList == null) {
            return null;
        }
        int serviceStartLine = 0;
        for (int i = 0; i < cubridConfContentList.size(); i++) {
            String data = cubridConfContentList.get(i).trim();
            if (data.length() == 0) {
                continue;
            }
            if (data.equals(ConfConstants.SERVICE_SECTION)) {
                serviceStartLine = i;
                continue;
            }
            if (serviceStartLine > 0 && data.matches("^service\\s*=.*")) {
                continue;
            }
            if (serviceStartLine > 0 && data.matches("^server\\s*=.*")) {
                String value = data.split("=")[1].trim();
                String[] allDbNames = value.split(",");
                boolean isExist = false;
                StringBuffer strBuffer = new StringBuffer();
                for (String name : allDbNames) {
                    if (name.trim().equalsIgnoreCase(dbNameOld)) {
                        isExist = true;
                        if (!StringUtil.isEmpty(dbNameNew)) {
                            strBuffer.append(dbNameNew.trim()).append(",");
                        }
                    } else {
                        strBuffer.append(name.trim()).append(",");
                    }
                }
                if (strBuffer.length() > 0) {
                    strBuffer.deleteCharAt(strBuffer.length() - 1);
                }
                String servers = strBuffer.toString();
                if (isExist) {
                    servers = servers.length() == 0 ? "#server=" : "server=" + servers;
                    cubridConfContentList.set(i, servers);
                    return cubridConfContentList;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Delete the database from server in service section
     *
     * @param task GetCubridConfParameterTask
     * @param contentList List<String>
     * @param dbName String
     * @return List<String>
     */
    public static List<String> deleteDatabaseFromServiceServer(
            GetCubridConfParameterTask task, List<String> contentList, String dbName) {
        return renameDatabaseFromServiceServer(task, contentList, dbName, "");
    }
}
