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

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;

/**
 * Relationship between two tables
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-5-13 created by Yu Guojia
 */
public class Relationship extends PropertyChangeProvider {
    private static final Logger LOGGER = LogUtil.getLogger(Relationship.class);
    private ERTable primaryKeyTable;
    private ERTable foreignKeyTable;
    Map<String, String> relationMap =
            new HashMap<String, String>(); // reference column name <-->  primary column name

    /**
     * @param foreignTable
     * @param primaryKeyTable
     */
    public Relationship(String relationName, ERTable foreignTable, ERTable primaryTable) {
        super();
        this.name = relationName;
        this.primaryKeyTable = primaryTable;
        this.foreignKeyTable = foreignTable;
    }

    /**
     * Add one corresponding column relation in the foreign key. The old value will be replaced, if
     * the key exist.
     *
     * @param columnName
     * @param referredPK
     */
    public void addRelation(String columnName, String pkColName) {
        relationMap.put(columnName, pkColName);
    }

    /** @return Returns the foreignKeyTable. */
    public ERTable getForeignKeyTable() {
        return foreignKeyTable;
    }

    /** @return Returns the primaryKeyTable. */
    public ERTable getPrimaryKeyTable() {
        return primaryKeyTable;
    }

    public String getPKTableName() {
        return primaryKeyTable.getName();
    }

    /** @param sourceTable the primary key table you are connecting to */
    public void setPrimaryKeyTable(ERTable targetPrimaryKey) {
        this.primaryKeyTable = targetPrimaryKey;
    }

    /** @param sourceForeignKey the foreign key table you are connecting from */
    public void setForeignKeyTable(ERTable sourceForeignKey) {
        this.foreignKeyTable = sourceForeignKey;
    }

    @Override
    public CubridDatabase getCubridDatabase() {
        return primaryKeyTable.getCubridDatabase();
    }

    @Override
    public ERSchema getERSchema() {
        return primaryKeyTable.getERSchema();
    }

    @Override
    public void setERSchema(ERSchema erSchema) {
        // do nothing
    }

    public Collection<String> getReferencedPKs() {
        return relationMap.values();
    }

    public Set<String> getReferenceColumns() {
        return relationMap.keySet();
    }

    /**
     * Reverting search
     *
     * @param pkColName
     * @return
     */
    public String getRefColByPK(String pkColName) {
        Set<String> refCols = relationMap.keySet();
        for (String refCol : refCols) {
            if (relationMap.get(refCol).equals(pkColName)) {
                return refCol;
            }
        }
        return null;
    }

    public String getRefedPK(String referName) {
        return relationMap.get(referName);
    }

    /** @return the relationMap */
    public Map<String, String> getRelationMap() {
        return relationMap;
    }

    /** @param relationMap the relationMap to set */
    public void setRelationMap(Map<String, String> relationMap) {
        this.relationMap = relationMap;
    }

    public void setNullRef() {
        this.foreignKeyTable = null;
        this.primaryKeyTable = null;
        this.relationMap.clear();
    }

    public Relationship clone() {
        Relationship ship = null;
        try {
            ship = (Relationship) super.clone();

            HashMap map = new HashMap<String, String>();
            map.putAll(relationMap);
            ship.setRelationMap(map);
        } catch (CloneNotSupportedException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return ship;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result =
                prime * result
                        + ((foreignKeyTable == null) ? 0 : foreignKeyTable.getName().hashCode());
        result =
                prime * result
                        + ((primaryKeyTable == null) ? 0 : primaryKeyTable.getName().hashCode());
        result = prime * result + ((relationMap == null) ? 0 : relationMap.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        Relationship other = (Relationship) obj;
        if (foreignKeyTable == null) {
            if (other.foreignKeyTable != null) {
                return false;
            }
        } else if (!foreignKeyTable.getName().equals(other.foreignKeyTable.getName())) {
            return false;
        }
        if (primaryKeyTable == null) {
            if (other.primaryKeyTable != null) {
                return false;
            }
        } else if (!primaryKeyTable.getName().equals(other.primaryKeyTable.getName())) {
            return false;
        }
        if (relationMap == null) {
            if (other.relationMap != null) {
                return false;
            }
        } else {
            Set<String> keySet = relationMap.keySet();
            Collection<String> valueSet = relationMap.values();
            Set<String> otherKeySet = other.relationMap.keySet();
            Collection<String> otherValueSet = other.relationMap.values();

            if (!keySet.containsAll(otherKeySet)
                    || !otherKeySet.containsAll(keySet)
                    || !valueSet.containsAll(otherValueSet)
                    || !otherValueSet.containsAll(valueSet)) {
                return false;
            }
        }

        return true;
    }
}
