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

package com.cubrid.common.core.common.model;

import static org.apache.commons.lang.StringUtils.defaultString;

import com.cubrid.common.core.util.DateUtil;
import com.cubrid.common.core.util.LogUtil;
import java.text.ParseException;
import org.slf4j.Logger;

/**
 * to store information of a column in the table schema
 *
 * @author moulinwang
 * @version 1.0 - 2009-6-5 created by moulinwang
 */
public class DBAttribute implements Cloneable {
    private static final Logger LOGGER = LogUtil.getLogger(DBAttribute.class);
    private String name;
    private String type;
    private String inherit; // it belongs to which class
    private boolean indexed;
    private boolean notNull;
    private boolean shared;
    private boolean unique;
    private String defaultValue;
    private SerialInfo autoIncrement;
    private String domainClassName;
    private boolean isClassAttribute;
    private String description;

    /*Support for CUBRID 9.1*/
    private String enumeration;
    private String collation;

    private boolean isNew = false;

    public DBAttribute(
            String name,
            String type,
            String inherit,
            boolean isIndexed,
            boolean isNotNull,
            boolean isShared,
            boolean isUnique,
            String defaultval,
            String collation) {
        this.name = name;
        this.type = type;
        this.inherit = inherit;
        this.indexed = isIndexed;
        this.notNull = isNotNull;
        this.shared = isShared;
        this.unique = isUnique;
        this.defaultValue = defaultval;
        this.collation = collation;
        resetDefault();
    }

    public DBAttribute() {
        // empty
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInherit() {
        return inherit;
    }

    public void setInherit(String inherit) {
        this.inherit = inherit;
    }

    public boolean isIndexed() {
        return indexed;
    }

    public void setIndexed(boolean indexed) {
        this.indexed = indexed;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public String getDefault() {
        return defaultValue;
    }

    public void setDefault(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getCollation() {
        return collation;
    }

    public void setCollation(String collation) {
        this.collation = collation;
    }

    /**
     * reset default value, for the default value from server client is not good for user, it should
     * be changed to a given format
     */
    public final void resetDefault() {
        if (defaultValue == null) {
            return;
        }
        if (type.equalsIgnoreCase("datetime")) {
            String value = defaultValue;
            if (value.startsWith("datetime")) {
                value = value.replace("datetime", "").trim();
            }
            if (value.startsWith("'") && value.endsWith("'")) {
                value = value.substring(1, value.length() - 1).trim();
            }
            String formatValue = DateUtil.formatDateTime(value, DateUtil.DATETIME_FORMAT);
            if (formatValue == null) {
                formatValue = value;
            }
            defaultValue = formatValue;
        } else if (type.equalsIgnoreCase("timestamp")) {
            try {
                String value = defaultValue;
                if (value.startsWith("timestamp")) {
                    value = value.replace("timestamp", "").trim();
                }
                if (value.startsWith("'") && value.endsWith("'")) {
                    value = value.substring(1, value.length() - 1).trim();
                }
                long timestamp = DateUtil.getTimestamp(value);
                defaultValue = DateUtil.getDatetimeString(timestamp, DateUtil.TIMESTAMP_FORMAT);
            } catch (ParseException e) {
                LOGGER.error(e.getMessage(), e);
            }
        } else if (type.equalsIgnoreCase("date")) {
            try {
                String value = defaultValue;
                if (value.startsWith("date")) {
                    value = value.replace("date", "").trim();
                }
                if (value.startsWith("'") && value.endsWith("'")) {
                    value = value.substring(1, value.length() - 1).trim();
                }
                long timestamp = DateUtil.getDate(value);
                defaultValue = DateUtil.getDatetimeString(timestamp, "yyyy/MM/dd");
            } catch (ParseException e) {
                LOGGER.error(e.getMessage(), e);
            }
        } else if (type.equalsIgnoreCase("time")) {
            try {
                String value = defaultValue;
                if (value.startsWith("time")) {
                    value = value.replace("time", "").trim();
                }
                if (value.startsWith("'") && value.endsWith("'")) {
                    value = value.substring(1, value.length() - 1).trim();
                }
                long timestamp = DateUtil.getTime(value);
                defaultValue = DateUtil.getDatetimeString(timestamp, "a hh:mm:ss");
            } catch (ParseException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        /* For bug TOOLS-3101*/
        /*else if ((type.toLowerCase(Locale.getDefault()).startsWith("char") || type.equalsIgnoreCase("string"))
        		&& (StringUtil.isNotEmpty(defaultValue) && defaultValue.startsWith("'")
        				&& defaultValue.endsWith("'") && defaultValue.length() > 1)) { // include character
        	// and character
        	// varying
        	defaultValue = defaultValue.substring(1, defaultValue.length() - 1).replace("''", "'");
        }*/
    }

    public SerialInfo getAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(SerialInfo autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public String getDomainClassName() {
        return domainClassName;
    }

    public void setDomainClassName(String domainClassName) {
        this.domainClassName = domainClassName;
    }

    public boolean isClassAttribute() {
        return isClassAttribute;
    }

    public void setClassAttribute(boolean isClassAttribute) {
        this.isClassAttribute = isClassAttribute;
    }

    /**
     * Override the equals method of Object
     *
     * @param obj Object the reference object with which to compare.
     * @return boolean true if this object is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof DBAttribute)) {
            return false;
        }

        DBAttribute a = (DBAttribute) obj;
        boolean equal =
                a.name == null
                        ? this.name == null
                        : a.name.toLowerCase().equals(this.name.toLowerCase());
        equal = equal && (a.type == null ? this.type == null : a.type.equals(this.type));
        equal =
                equal
                        && (a.inherit == null
                                ? this.inherit == null
                                : a.inherit.toLowerCase().equals(this.inherit.toLowerCase()));
        equal =
                equal
                        && (a.defaultValue == null
                                ? this.defaultValue == null
                                : a.defaultValue.equals(this.defaultValue));
        equal = equal && (a.notNull == this.notNull);
        equal = equal && (a.indexed == this.indexed);
        equal = equal && (a.shared == this.shared);
        equal = equal && (a.unique == this.unique);
        equal =
                equal
                        && (a.autoIncrement == null
                                ? this.autoIncrement == null
                                : a.autoIncrement.equals(this.autoIncrement));

        return equal;
    }

    /** @return int a hash code value for this object */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /** @return DBAttribute a clone of this instance. */
    public DBAttribute clone() {
        DBAttribute newAttr = null;
        try {
            newAttr = (DBAttribute) super.clone();
        } catch (CloneNotSupportedException e) {
            LOGGER.debug("", e);
        }
        if (newAttr == null) {
            return null;
        }
        if (autoIncrement == null) {
            newAttr.autoIncrement = null;
        } else {
            newAttr.autoIncrement = autoIncrement.clone();
        }
        return newAttr;
    }

    public String getSharedValue() {
        return defaultValue;
    }

    /**
     * Set the share value
     *
     * @param sharedValue String The given the shared values
     */
    public void setSharedValue(String sharedValue) {
        this.defaultValue = sharedValue;
        this.shared = true;
    }

    public String getEnumeration() {
        return enumeration;
    }

    public void setEnumeration(String enumeration) {
        this.enumeration = enumeration;
    }

    /**
     * return a description of a column
     *
     * @return
     */
    public String getDescription() {
        // It shouldn't be a null value.
        return defaultString(description, "");
    }

    /**
     * set a description of a column
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /** @return the isNew */
    public boolean isNew() {
        return isNew;
    }

    /** @param isNew the isNew to set */
    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    /**
     * Convert the fields of this object to string.
     *
     * @return String a string includes this object info
     */
    public String toString() {
        StringBuilder bf = new StringBuilder(); // FIXME use ToStringBuilder
        bf.append("attribute name:" + this.name + "\n");
        bf.append("\tdata type:" + this.type + "\n");
        bf.append("\tinherit:" + this.inherit + "\n");
        bf.append("\tNot Null:" + this.notNull + "\n");
        bf.append("\tshared:" + this.shared + "\n");
        bf.append("\tunique:" + this.unique + "\n");
        bf.append("\tdefault:" + this.defaultValue + "\n");
        bf.append("\tdescription:" + this.description + "\n");
        return bf.toString();
    }
}
