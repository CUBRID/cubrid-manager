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

import java.util.Locale;

/**
 * This type indicates the parameters of column
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-12-14 created by lizhiqiang
 */
public class TableColumn implements Comparable<TableColumn> {

    protected String columnName;
    protected String typeName;
    protected int precision; // this field also used to size
    protected int scale;
    protected int ordinalPosition;
    protected String
            subElementTypeName; // until now, this field can not be gotten because of the defects of
                                // JDBC
    protected boolean isPrimaryKey;

    /**
     * Get the columnName
     *
     * @return the columnName
     */
    public String getColumnName() {
        return columnName;
    }

    /** @param columnName the columnName to set */
    public void setColumnName(String columnName) {
        if (columnName != null) {
            this.columnName = columnName.toLowerCase(Locale.getDefault());
        }
    }

    /**
     * Get the typeName
     *
     * @return the typeName
     */
    public String getTypeName() {
        if ("decimal".equalsIgnoreCase(typeName)) {
            typeName = "numeric";
        }
        return typeName;
    }

    /** @param typeName the typeName to set */
    public void setTypeName(String typeName) {
        if (typeName != null) {
            this.typeName = typeName.toLowerCase(Locale.getDefault());
        }
    }

    /**
     * Get the precision
     *
     * @return the precision
     */
    public int getPrecision() {
        return precision;
    }

    /** @param precision the precision to set */
    public void setPrecision(int precision) {
        this.precision = precision;
    }

    /**
     * Get the scale
     *
     * @return the scale
     */
    public int getScale() {
        return scale;
    }

    /** @param scale the scale to set */
    public void setScale(int scale) {
        this.scale = scale;
    }

    /**
     * Get the ordinalPosition
     *
     * @return the ordinalPosition
     */
    public int getOrdinalPosition() {
        return ordinalPosition;
    }

    /** @param ordinalPosition the ordinalPosition to set */
    public void setOrdinalPosition(int ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
    }

    /**
     * Get the subElementTypeName
     *
     * @return the subElementTypeName
     */
    public String getSubElementTypeName() {
        return subElementTypeName;
    }

    /** @param subElementTypeName the subElementTypeName to set */
    public void setSubElementTypeName(String subElementTypeName) {
        if (subElementTypeName != null) {
            this.subElementTypeName = subElementTypeName.toLowerCase(Locale.getDefault());
        }
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
    public void setPrimaryKey(boolean isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
    }

    /**
     * Override method
     *
     * @param another the instance of TableColumn
     * @return int
     */
    public int compareTo(TableColumn another) {
        if (another == null) {
            return 0;
        }
        int thisVal = this.ordinalPosition;
        int anotherVal = another.ordinalPosition;
        return thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1);
    }

    /**
     * @see java.lang.Object#hashCode()
     * @return int
     */
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((columnName == null) ? 0 : columnName.hashCode());
        result = prime * result + (isPrimaryKey ? 1231 : 1237);
        result = prime * result + ordinalPosition;
        result = prime * result + precision;
        result = prime * result + scale;
        result =
                prime * result + ((subElementTypeName == null) ? 0 : subElementTypeName.hashCode());
        result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     * @param obj Object
     * @return boolean
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TableColumn other = (TableColumn) obj;
        if (columnName == null) {
            if (other.columnName != null) {
                return false;
            }
        } else if (!columnName.equals(other.columnName)) {
            return false;
        }
        if (isPrimaryKey != other.isPrimaryKey) {
            return false;
        }
        if (ordinalPosition != other.ordinalPosition) {
            return false;
        }
        if (precision != other.precision) {
            return false;
        }
        if (scale != other.scale) {
            return false;
        }
        if (subElementTypeName == null) {
            if (other.subElementTypeName != null) {
                return false;
            }
        } else if (!subElementTypeName.equals(other.subElementTypeName)) {
            return false;
        }
        if (typeName == null) {
            if (other.typeName != null) {
                return false;
            }
        } else if (!typeName.equals(other.typeName)) {
            return false;
        }
        return true;
    }
}
