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
package com.cubrid.common.ui.query.tuner;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * QueryRecordList Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-4-17 created by Kevin.Wang
 */
public class QueryRecordProject implements Cloneable {

    private static final long serialVersionUID = 3499093388272118809L;

    private String name;
    private String databaseKey;
    private DatabaseInfo databaseInfo;
    private Date createDate;
    private final List<QueryRecord> queryRecordList = new ArrayList<QueryRecord>();
    /** @return the databaseKey */
    public String getDatabaseKey() {
        return databaseKey;
    }
    /** @param databaseKey the databaseKey to set */
    public void setDatabaseKey(String databaseKey) {
        this.databaseKey = databaseKey;
    }
    /** @return the databaseInfo */
    public DatabaseInfo getDatabaseInfo() {
        return databaseInfo;
    }
    /** @param databaseInfo the databaseInfo to set */
    public void setDatabaseInfo(DatabaseInfo databaseInfo) {
        this.databaseInfo = databaseInfo;
    }
    /** @return the createDate */
    public Date getCreateDate() {
        return createDate;
    }
    /** @param createDate the createDate to set */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    /** @return the name */
    public String getName() {
        return name;
    }
    /** @param name the name to set */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Find query record by name
     *
     * @param name
     * @return
     */
    public QueryRecord findQueryRecord(String name) {
        for (QueryRecord temp : queryRecordList) {
            if (StringUtil.isEqual(name, temp.getName())) {
                return temp;
            }
        }

        return null;
    }

    /**
     * Remove query record by name
     *
     * @param name
     * @return
     */
    public boolean removeQueryRecord(String name) {
        int index = -1;
        for (int i = 0; i < queryRecordList.size(); i++) {
            QueryRecord temp = queryRecordList.get(i);
            if (StringUtil.isEqual(name, temp.getName())) {
                index = i;
                break;
            }
        }

        if (index >= 0) {
            queryRecordList.remove(index);
            return true;
        } else {
            return false;
        }
    }

    public void addQueryRecord(QueryRecord queryRecord) {
        queryRecordList.add(queryRecord);
    }

    /** @return the queryRecordList */
    public List<QueryRecord> getQueryRecordList() {
        return queryRecordList;
    }
    /**
     * Clone a object
     *
     * @return QueryRecordProject
     */
    public QueryRecordProject clone() {
        QueryRecordProject queryRecordProject = new QueryRecordProject();

        queryRecordProject.setName(name);
        queryRecordProject.setDatabaseKey(databaseKey);
        queryRecordProject.setDatabaseInfo(databaseInfo);
        queryRecordProject.setCreateDate(createDate);
        for (QueryRecord record : queryRecordList) {
            queryRecordProject.addQueryRecord(record.clone());
        }

        return queryRecordProject;
    }
}
