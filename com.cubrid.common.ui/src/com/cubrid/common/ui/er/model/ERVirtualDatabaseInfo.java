/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.er.model;

import com.cubrid.cubridmanager.core.cubrid.database.model.Collation;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.util.List;

/**
 * A virtual databaseInfo model for ERD
 *
 * @author Yu Guojia
 * @version 1.0 - 2014-1-24 created by Yu Guojia
 */
public class ERVirtualDatabaseInfo extends DatabaseInfo {
    private static String dbName = "ER Design";
    private boolean isLogined = true;
    private String version = "9.2.0";
    private boolean isSupportTableComment = true;
    private String charSet = "UTF-8";
    private String collation = "utf8_bin";
    private List<Collation> collationList;
    private static ERVirtualDatabaseInfo instance;

    public static ERVirtualDatabaseInfo getInstance() {
        if (instance == null) {
            instance = new ERVirtualDatabaseInfo();
        }
        return instance;
    }

    private ERVirtualDatabaseInfo() {
        super(dbName, null);
    }

    /**
     * Get database name
     *
     * @return String The database name
     */
    @Override
    public String getDbName() {
        return dbName;
    }

    /**
     * Set database name
     *
     * @param dbName String The given database name
     */
    @Override
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /**
     * Check whether this database is HA mode
     *
     * @return <code>true</code> if it is;otherwise <code>false</code>
     */
    @Override
    public boolean isHAMode() {
        return false;
    }

    @Override
    public boolean isLogined() {
        return isLogined;
    }

    @Override
    public void setLogined(boolean isLogined) {
        this.isLogined = isLogined;
    }

    @Override
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean isSupportTableComment() {
        return isSupportTableComment;
    }

    @Override
    public void setSupportTableComment(boolean isSupportTableComment) {
        this.isSupportTableComment = isSupportTableComment;
    }

    @Override
    public String getCharSet() {
        return charSet;
    }

    @Override
    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }

    @Override
    public String getCollation() {
        return collation;
    }

    public List<Collation> getCollections() {
        if (collationList == null) {
            buildVirtualDBCollationList();
        }
        return collationList;
    }

    private void buildVirtualDBCollationList() {
        collationList = Collation.getDefaultCollations();
        Collation emptyCollation = new Collation();
        emptyCollation.setCharset("");
        emptyCollation.setName("");
        collationList.add(0, emptyCollation);
    }
}
