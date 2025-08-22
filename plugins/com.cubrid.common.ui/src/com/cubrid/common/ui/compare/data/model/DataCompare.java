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
package com.cubrid.common.ui.compare.data.model;

import com.cubrid.common.core.common.model.SchemaInfo;

public class DataCompare {
    private String tableName;
    private boolean existsTarget;
    private long recordsSource;
    private long recordsTarget;
    private long matches;
    private long notExists;
    private long notMatches;
    private long progressPosition;
    private SchemaInfo schemaInfo;
    private boolean use;
    private boolean refreshed;
    private boolean sameSchema;

    public boolean isRefreshed() {
        return refreshed;
    }

    public void setRefreshed(boolean refreshed) {
        this.refreshed = refreshed;
    }

    public void increaseMatches() {
        matches++;
    }

    public void increaseNotMatches() {
        notMatches++;
    }

    public void increaseNotExists() {
        notExists++;
    }

    public void increaseProgress() {
        progressPosition++;
    }

    public boolean isUse() {
        return use;
    }

    public void setUse(boolean use) {
        this.use = use;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean isExistsTarget() {
        return existsTarget;
    }

    public void setExistsTarget(boolean existsTarget) {
        this.existsTarget = existsTarget;
    }

    public long getRecordsSource() {
        return recordsSource;
    }

    public void setRecordsSource(long recordsSource) {
        this.recordsSource = recordsSource;
    }

    public long getRecordsTarget() {
        return recordsTarget;
    }

    public void setRecordsTarget(long recordsTarget) {
        this.recordsTarget = recordsTarget;
    }

    public long getMatches() {
        return matches;
    }

    public void setMatches(long matches) {
        this.matches = matches;
    }

    public long getNotExists() {
        return notExists;
    }

    public void setNotExists(long notExists) {
        this.notExists = notExists;
    }

    public long getNotMatches() {
        return notMatches;
    }

    public void setNotMatches(long notMatches) {
        this.notMatches = notMatches;
    }

    public long getProgressPosition() {
        return progressPosition;
    }

    public void setProgressPosition(long progressPosition) {
        this.progressPosition = progressPosition;
    }

    public SchemaInfo getSchemaInfo() {
        return schemaInfo;
    }

    public void setSchemaInfo(SchemaInfo schemaInfo) {
        this.schemaInfo = schemaInfo;
    }

    /** @return the isSameSchema */
    public boolean isSameSchema() {
        return sameSchema;
    }

    /** @param isSameSchema the isSameSchema to set */
    public void setSameSchema(boolean isSameSchema) {
        this.sameSchema = isSameSchema;
    }
}
