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

import com.cubrid.common.ui.spi.table.CellValue;
import com.cubrid.common.ui.spi.util.FieldHandlerUtils;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;

/**
 * comparator for query editor column sorter
 *
 * @author pangqiren
 */
@SuppressWarnings("rawtypes")
public class ColumnComparator implements Comparator, Serializable { // FIXME reuse

    private static final long serialVersionUID = 6519024461474907900L;
    private final String columnIndex;
    private final String columnType;
    private boolean isAsc = true;

    /**
     * @param columnIndex
     * @param columnType
     * @param isAsc
     */
    public ColumnComparator(String columnIndex, String columnType, boolean isAsc) {
        this.columnIndex = columnIndex;
        this.columnType = columnType.trim().toUpperCase();
        this.isAsc = isAsc;
    }

    /**
     * set the isAsc
     *
     * @param isAsc boolean
     */
    public void setAsc(boolean isAsc) {
        this.isAsc = isAsc;
    }

    /** @return boolean */
    public boolean isAsc() {
        return this.isAsc;
    }

    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the first argument is less than,
     *     equal to, or greater than the second.
     */
    public int compare(Object o1, Object o2) {
        CellValue value1 = null, value2 = null;
        Map map1 = (Map) o1;
        Map map2 = (Map) o2;
        if (map1 != null) {
            value1 = (CellValue) map1.get(columnIndex);
        }

        if (map2 != null) {
            value2 = (CellValue) map2.get(columnIndex);
        }
        String str1 = getStrValue(value1);
        String str2 = getStrValue(value2);

        return FieldHandlerUtils.comparedDBValues(columnType, str1, str2, isAsc);
    }

    private String getStrValue(CellValue value) {
        if (value == null || value.getStringValue() == null) {
            return "";
        }

        return value.getStringValue();
    }
}
