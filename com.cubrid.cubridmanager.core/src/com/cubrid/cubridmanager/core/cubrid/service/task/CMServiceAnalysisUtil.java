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
package com.cubrid.cubridmanager.core.cubrid.service.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.cubrid.service.model.DbLocationInfo;
import com.cubrid.cubridmanager.core.cubrid.service.model.NodeType;
import java.util.List;
import java.util.Map;

public class CMServiceAnalysisUtil {

    public static boolean isLocalHost(String host) {
        if ("localhost".equals(host) || "127.0.0.1".equals(host)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isIp(String host) {
        if (StringUtil.isEmpty(host)) {
            return false;
        }
        boolean isIp = true;
        String[] ar = host.split("\\.");
        if (ar.length == 4) {
            try {
                for (String s : ar) {
                    int temp = Integer.parseInt(s);
                    if (temp < 0 || temp > 255) {
                        isIp = false;
                        break;
                    }
                }
            } catch (NumberFormatException e) {
                isIp = false;
            }
        } else {
            isIp = false;
        }
        return isIp;
    }

    /**
     * Put DbLocationInfo(s) into <i>dbLocationInfoList(List<DbLocationInfo>)</i>. DbLocationInfo is
     * constructed by the info stored in <i>dbParamMapList(List<Map<String, String>>)</i>
     *
     * @param dbParamMapList
     * @param dbLocationInfoList
     */
    public static void addDbLocaltionInfos(
            List<Map<String, String>> dbParamMapList, List<DbLocationInfo> dbLocationInfoList) {
        if (dbParamMapList == null || dbLocationInfoList == null) {
            return;
        }

        for (Map<String, String> dbParamMap : dbParamMapList) {
            if (StringUtil.isEmpty(dbParamMap.get("db-name"))) {
                continue;
            }
            DbLocationInfo dbLocalInfo = new DbLocationInfo();
            dbLocalInfo.setDbName(dbParamMap.get("db-name"));
            dbLocalInfo.setVolPath(dbParamMap.get("vol-path"));
            if (!StringUtil.isEmpty(dbParamMap.get("db-host"))) {
                String[] dbHostAr = dbParamMap.get("db-host").split(":");
                dbLocalInfo.addAllDbHosts(dbHostAr);
            }
            dbLocalInfo.setLogPath(dbParamMap.get("log-path"));
            dbLocalInfo.setLobBasePath(dbParamMap.get("lob-base-path"));
            dbLocationInfoList.add(dbLocalInfo);
        }
    }

    public static NodeType convertHaStatToNodeType(String status) {
        if ("master".equals(status)) {
            return NodeType.MASTER;
        } else if ("slave".equals(status)) {
            return NodeType.SLAVE;
        } else if ("replica".equals(status)) {
            return NodeType.REPLICA;
        } else {
            return NodeType.NORMAL;
        }
    }

    public static boolean isAccessedByRemoteHost(List<DbLocationInfo> dbLocationInfoList) {
        boolean isSupported = false;
        for (DbLocationInfo dbLocalInfo : dbLocationInfoList) {
            for (String host : dbLocalInfo.getDbHosts()) {
                if (!isLocalHost(host)) {
                    isSupported = true;
                    break;
                }
            }
        }

        return isSupported;
    }
}
