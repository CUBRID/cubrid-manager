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
package com.cubrid.cubridmanager.core.cubrid.service.model;

import com.cubrid.common.core.util.StringUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DbLocationInfo { // FIXME description

    private String dbName;
    private String volPath;
    private final List<String> dbHosts = new ArrayList<String>();
    private String logPath;
    private String lobBasePath;

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getVolPath() {
        return volPath;
    }

    public void setVolPath(String volPath) {
        this.volPath = volPath;
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public String getLobBasePath() {
        return lobBasePath;
    }

    public void setLobBasePath(String lobBasePath) {
        this.lobBasePath = lobBasePath;
    }

    public List<String> getDbHosts() {
        return dbHosts;
    }

    public boolean addDbHost(String host) {
        if (StringUtil.isEmpty(host) || dbHosts.contains(host)) {
            return false;
        }

        dbHosts.add(host);
        return true;
    }

    public boolean addAllDbHosts(String[] hosts) {
        if (hosts == null) {
            return false;
        }

        dbHosts.addAll(Arrays.asList(hosts));
        return true;
    }

    public boolean findHost(String host) {
        return dbHosts.contains(host);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("dbname=").append(dbName).append(",");
        sb.append("vol_path=").append(volPath).append(",");
        sb.append("db_hosts=").append(dbHosts).append(",");
        sb.append("log_path=").append(logPath).append(",");
        sb.append("lob_base_path=").append(lobBasePath);

        return sb.toString();
    };
}
