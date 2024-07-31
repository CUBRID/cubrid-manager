/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the <ORGANIZATION> nor the names of its contributors
 *   may be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */
package com.cubrid.common.ui.spi.progress;

/**
 * A plain java that represent a job family used by the type of TaskJob
 *
 * @author lizhiqiang
 * @version 1.0 - 2009-8-17 created by lizhiqiang
 */
public class JobFamily {

    public static final String ALL_SERVER = ""; // include all servers
    public static final String ALL_DB = ""; // Within one server,include all databases
    private String serverName;
    private String dbName;

    /**
     * Get the serverName
     *
     * @return the serverName
     */
    public String getServerName() {
        return serverName;
    }

    /** @param serverName the serverName to set */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /**
     * Get the dbName
     *
     * @return the dbName
     */
    public String getDbName() {
        return dbName;
    }

    /** @param dbName the dbName to set */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /**
     * Compare to the parameter of obj, if the fields of obj equal to this fields serverName and
     * dbName,return true,or else false.
     *
     * @param obj the object
     * @return <code>true</code> if it is belonged to obj;<code>false</code> otherwise
     */
    public boolean belongsTo(Object obj) {
        boolean returnValue = false;
        if (obj instanceof JobFamily) {
            String serverName = ((JobFamily) obj).serverName;
            String dbName = ((JobFamily) obj).dbName;
            if (serverName != null && serverName.equals(ALL_SERVER)) {
                returnValue = true;
            } else if (this.serverName.equals(serverName)
                    && (dbName.equals(ALL_DB) || this.dbName.equals(dbName))) {
                returnValue = true;
            }
        }
        return returnValue;
    }
}
