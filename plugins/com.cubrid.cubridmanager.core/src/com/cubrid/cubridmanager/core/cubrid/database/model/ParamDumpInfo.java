/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *  - Neither the name of the <ORGANIZATION> nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

package com.cubrid.cubridmanager.core.cubrid.database.model;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible to store param dump content.
 *
 * @author wuyingshi
 * @version 1.0 - 2010-3-24 created by wuyingshi
 */
public class ParamDumpInfo {

    private String dbName;
    private Map<String, String> clientData;
    private Map<String, String> serverData;

    /**
     * get task name.
     *
     * @return String
     */
    public String getTaskName() {
        return "paramdump";
    }

    /**
     * get the dbName.
     *
     * @return String
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * set the dbName.
     *
     * @param dbName String
     */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /**
     * Get the client data.
     *
     * @return Map<String, String> The map of client data
     */
    public Map<String, String> getClientData() {
        if (clientData == null) {
            return new HashMap<String, String>();
        }
        return clientData;
    }

    /**
     * Add a pair of key and value to client data
     *
     * @param key String
     * @param value String
     */
    public void addClientData(String key, String value) {
        if (clientData == null) {
            clientData = new HashMap<String, String>();
        }
        this.clientData.put(key, value);
    }

    /**
     * Get the server data
     *
     * @return Map<String, String> The map of server data
     */
    public Map<String, String> getServerData() {
        if (serverData == null) {
            return new HashMap<String, String>();
        }
        return serverData;
    }

    /**
     * Add a pair of key and value to server data
     *
     * @param key String
     * @param value String
     */
    public void addServerData(String key, String value) {
        if (serverData == null) {
            serverData = new HashMap<String, String>();
        }
        this.serverData.put(key, value);
    }

    /**
     * set the client data
     *
     * @param clientData the clientData to set
     */
    public void setClientData(Map<String, String> clientData) {
        this.clientData = clientData;
    }

    /**
     * set the server data
     *
     * @param serverData the serverData to set
     */
    public void setServerData(Map<String, String> serverData) {
        this.serverData = serverData;
    }
}
