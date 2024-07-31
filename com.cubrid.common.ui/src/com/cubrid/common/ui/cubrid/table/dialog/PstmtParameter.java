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
package com.cubrid.common.ui.cubrid.table.dialog;

import java.util.HashMap;
import java.util.Map;

/**
 * Prepared statement parameter model class
 *
 * @author pangqiren
 * @version 1.0 - 2010-7-20 created by pangqiren
 */
public class PstmtParameter implements Cloneable {

    private static final String DATE_PATTERN = "date_pattern";
    private static final String IS_FILE_VALUE = "is_file_value";

    private String paramName;
    private int paramIndex;
    private String dataType;
    private Object paramValue;
    // if the parameter value is file, it's charSet
    private String charSet;
    private String fileColumnName;

    private Map<String, Object> properties = new HashMap<String, Object>();

    public PstmtParameter(String paramName, int paramIndex, String dataType, Object paramValue) {
        this.paramName = paramName;
        this.paramIndex = paramIndex;
        this.dataType = dataType;
        this.paramValue = paramValue;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public int getParamIndex() {
        return paramIndex;
    }

    public void setParamIndex(int paramIndex) {
        this.paramIndex = paramIndex;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Object getParamValue() {
        return paramValue;
    }

    public void setParamValue(Object paramValue) {
        this.paramValue = paramValue;
    }

    public String getStringParamValue() {
        if (paramValue == null) {
            return null;
        }
        if (paramValue instanceof String) {
            return (String) paramValue;
        }
        return paramValue.toString();
    }

    public String getCharSet() {
        return charSet;
    }

    public void setDatePattern(String pattern) {
        properties.put(DATE_PATTERN, pattern);
    }

    public String getDatePattern() {
        return (String) properties.get(DATE_PATTERN);
    }

    public boolean isFileValue() {
        if (properties.containsKey(IS_FILE_VALUE)) {
            return (Boolean) properties.get(IS_FILE_VALUE);
        }
        return false;
    }

    public void setFileValue(boolean isFileValue) {
        properties.put(IS_FILE_VALUE, isFileValue);
    }

    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }

    public String getFileColumnName() {
        return fileColumnName;
    }

    public void setFileColumnName(String fileColumnName) {
        this.fileColumnName = fileColumnName;
    }

    /** Clone a object */
    public PstmtParameter clone() {
        try {
            return (PstmtParameter) super.clone();
        } catch (CloneNotSupportedException e) {
        }
        return null;
    }
}
