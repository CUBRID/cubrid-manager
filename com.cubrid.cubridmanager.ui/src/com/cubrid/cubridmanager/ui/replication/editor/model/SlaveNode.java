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
package com.cubrid.cubridmanager.ui.replication.editor.model;

import java.util.HashMap;
import java.util.Map;

/**
 * The slave model object,it store slave database information
 *
 * @author pangqiren
 * @version 1.0 - 2009-8-26 created by pangqiren
 */
public class SlaveNode extends LeafNode {

    private String dbName;
    private String dbPath;
    private String dbUser;
    private String dbPassword;
    private Map<String, String> paramMap = null;

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbPath() {
        return dbPath;
    }

    public void setDbPath(String dbPath) {
        this.dbPath = dbPath;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public Map<String, String> getParamMap() {
        return this.paramMap;
    }

    public void setParamMap(Map<String, String> paramMap) {
        this.paramMap = paramMap;
    }

    /**
     * get the parameter value.
     *
     * @param key String
     * @return value
     */
    public String getParamValue(String key) {
        if (paramMap == null || key == null) {
            return null;
        } else {
            return paramMap.get(key);
        }
    }

    /**
     * get the parameter value.
     *
     * @param key String
     * @param value String
     */
    public void setParamValue(String key, String value) {
        if (key == null || key.trim().length() <= 0 || value == null) {
            return;
        }
        if (paramMap == null) {
            paramMap = new HashMap<String, String>();
        }
        paramMap.put(key, value);
    }

    /**
     * @see com.cubrid.cubridmanager.ui.replication.editor.model.Node#isValid()
     * @return boolean
     */
    public boolean isValid() {
        if (dbName == null || dbName.trim().length() == 0) {
            return false;
        }
        if (dbPath == null || dbPath.trim().length() == 0) {
            return false;
        }
        if (dbUser == null || dbUser.trim().length() == 0) {
            return false;
        }
        if (dbPassword == null || dbPassword.trim().length() == 0) {
            return false;
        }
        return true;
    }
}
