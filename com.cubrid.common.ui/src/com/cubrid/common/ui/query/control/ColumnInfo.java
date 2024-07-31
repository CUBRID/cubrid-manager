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
package com.cubrid.common.ui.query.control;

import com.cubrid.common.ui.spi.util.FieldHandlerUtils;

/**
 * each column information including name and type in query editor table result
 *
 * @author pangqiren
 */
public class ColumnInfo implements Cloneable {

    private String index;
    private String name;
    private String type;
    private int precision;
    private int scale;
    private String childElementType;

    public ColumnInfo(
            String index,
            String name,
            String type,
            String childElementType,
            int precision,
            int scale) {
        super();
        this.index = index;
        this.name = name;
        this.type = type;
        this.childElementType = childElementType;
        this.precision = precision;
        this.scale = scale;
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

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public String getChildElementType() {
        return childElementType;
    }

    public void setChildElementType(String childElementType) {
        this.childElementType = childElementType;
    }

    /**
     * Get complete type
     *
     * @return String
     */
    public String getComleteType() { // FIXME move this logic to core module
        String res = FieldHandlerUtils.getComleteType(type, childElementType, precision, scale);
        return res == null ? "" : res;
    }

    public String getShortType() { // FIXME move this logic to core module
        String res =
                FieldHandlerUtils.getComleteType(type, childElementType, precision, scale, true);
        return res == null ? "" : res;
    }

    /**
     * Clone a object
     *
     * @return ColumnInfo
     */
    public ColumnInfo clone() {
        ColumnInfo columnInfo = null;
        try {
            columnInfo = (ColumnInfo) super.clone();
        } catch (CloneNotSupportedException e) {
        }
        return columnInfo;
    }
}
