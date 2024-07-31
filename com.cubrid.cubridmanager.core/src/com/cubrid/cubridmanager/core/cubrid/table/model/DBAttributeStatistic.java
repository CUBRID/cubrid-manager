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
package com.cubrid.cubridmanager.core.cubrid.table.model;

import com.cubrid.common.core.common.model.DBAttribute;

/**
 * This class indicates the info of statistic of DBAttribute
 *
 * @author pangqiren
 * @version 1.0 - 2009-12-28 created by pangqiren
 */
public class DBAttributeStatistic extends DBAttribute {

    private String minValue = null;
    private String maxValue = null;
    private int valueDistinctCount = 0;

    public String getMinValue() {
        return minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    public int getValueDistinctCount() {
        return valueDistinctCount;
    }

    public void setValueDistinctCount(int valueDistinctCount) {
        this.valueDistinctCount = valueDistinctCount;
    }

    /**
     * Override the equals method of Object
     *
     * @param obj Object the reference object with which to compare.
     * @return boolean true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DBAttributeStatistic)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        DBAttributeStatistic dbAttribute = (DBAttributeStatistic) obj;
        boolean equal =
                dbAttribute.minValue == null
                        ? this.minValue == null
                        : dbAttribute.minValue.equals(this.minValue);
        equal =
                equal
                        && (dbAttribute.maxValue == null
                                ? this.maxValue == null
                                : dbAttribute.maxValue.equals(this.maxValue));
        equal = equal && dbAttribute.valueDistinctCount == this.valueDistinctCount;
        return equal;
    }

    /** @return int a hash code value for this object */
    public int hashCode() {
        int resultMin = minValue == null ? 0 : minValue.hashCode();
        int resultMax = maxValue == null ? 0 : maxValue.hashCode();
        return 29 * resultMin + 31 * resultMax;
    }
}
