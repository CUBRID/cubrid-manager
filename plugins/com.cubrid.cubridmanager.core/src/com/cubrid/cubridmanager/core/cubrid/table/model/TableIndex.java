/*
 * Copyright (C) 2008 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.cubridmanager.core.cubrid.table.model;

import java.util.List;

/**
 * This type indicates the parameters of Index
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-12-13 created by lizhiqiang
 */
public class TableIndex {
    private String indexName;
    private String ownerName = "";
    private boolean isPrimaryKey;
    private boolean isForeignKey;
    private boolean isUnique;
    private boolean isReverse;
    private List<String> columns;

    /**
     * Get the index name
     *
     * @return the indexName
     */
    public String getIndexName() {
        return indexName;
    }

    /** @param indexName the indexName to set */
    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    /** @param indexName the indexName to set */
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    /**
     * Whether is primary key
     *
     * @return the isPrimaryKey
     */
    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    /** @param isPrimaryKey the isPrimaryKey to set */
    public void setPrimaryKey(String isPrimaryKey) {
        if ("YES".equalsIgnoreCase(isPrimaryKey)) {
            this.isPrimaryKey = true;
        } else {
            this.isPrimaryKey = false;
        }
    }

    /**
     * Whether is foreign key
     *
     * @return the isForeignKey
     */
    public boolean isForeignKey() {
        return isForeignKey;
    }

    /** @param isForeignKey the isForeignKey to set */
    public void setForeignKey(String isForeignKey) {
        if ("YES".equalsIgnoreCase(isForeignKey)) {
            this.isForeignKey = true;
        } else {
            this.isForeignKey = false;
        }
    }

    /**
     * Whether is Unique
     *
     * @return the isUnique
     */
    public boolean isUnique() {
        return isUnique;
    }

    /** @param isUnique the isUnique to set */
    public void setUnique(String isUnique) {
        if ("YES".equalsIgnoreCase(isUnique)) {
            this.isUnique = true;
        } else {
            this.isUnique = false;
        }
    }

    /**
     * Whether is reverse
     *
     * @return the isReverse
     */
    public boolean isReverse() {
        return isReverse;
    }

    /** @param isReverse the isReverse to set */
    public void setReverse(String isReverse) {
        if ("YES".equalsIgnoreCase(isReverse)) {
            this.isReverse = true;
        } else {
            this.isReverse = false;
        }
    }

    /**
     * Get the columns
     *
     * @return the columns
     */
    public List<String> getColumns() {
        return columns;
    }

    /** @param columns the columns to set */
    public void setColumns(List<String> columns) {
        this.columns = columns;
    }
}
