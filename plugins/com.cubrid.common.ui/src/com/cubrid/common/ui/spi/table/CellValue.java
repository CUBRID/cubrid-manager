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
package com.cubrid.common.ui.spi.table;

import java.util.HashMap;
import java.util.Map;

/**
 * Table cell value
 *
 * @author pangqiren
 * @version 1.0 - 2012-7-23 created by pangqiren
 */
public class CellValue {
    private static final String KEY_FILE_CHARSET = "file_charset";
    private final Map<String, Object> properties = new HashMap<String, Object>();
    private String showValue;
    private Object value;
    private boolean hasLoadAll = true;

    public CellValue() {}

    public CellValue(Object value) {
        this(value, null);
    }

    public CellValue(Object value, String showValue) {
        this.value = value;
        this.showValue = showValue;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public Object getProperty(String name) {
        return properties.get(name);
    }

    public void putProperty(String name, Object obj) {
        properties.put(name, obj);
    }

    public String getFileCharset() {
        return (String) getProperty(KEY_FILE_CHARSET);
    }

    public void setFileCharset(String charset) {
        putProperty(KEY_FILE_CHARSET, charset);
    }

    public String getShowValue() {
        return showValue;
    }

    public void setShowValue(String showValue) {
        this.showValue = showValue;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getStringValue() {
        if (value == null) {
            return null;
        }

        if (value instanceof String) {
            return (String) value;
        }

        if (value instanceof byte[]) {
            return new String((byte[]) value);
        }

        return value.toString();
    }

    public boolean hasLoadAll() {
        return hasLoadAll;
    }

    public void setHasLoadAll(boolean hasLoadAll) {
        this.hasLoadAll = hasLoadAll;
    }

    /**
     * Return whether equal
     *
     * @param Object
     * @return boolean
     */
    public boolean equals(Object object) {
        if (!(object instanceof CellValue)) {
            return false;
        }
        Object cellValue = ((CellValue) object).getValue();
        if (cellValue == null && value == null) {
            return true;
        } else if (cellValue != null && value != null) {
            return cellValue.equals(value);
        }
        return false;
    }

    public CellValue clone() {
        try {
            return (CellValue) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public int hashCode() {
        return super.hashCode();
    }
}
