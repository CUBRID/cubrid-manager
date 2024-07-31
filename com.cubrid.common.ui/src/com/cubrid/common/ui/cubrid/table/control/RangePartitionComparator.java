/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.cubrid.table.control;

import com.cubrid.common.core.common.model.PartitionInfo;
import com.cubrid.common.ui.spi.util.FieldHandlerUtils;
import java.io.Serializable;
import java.util.Comparator;

/**
 * The range partition expression value comparator
 *
 * @author pangqiren
 * @version 1.0 - 2010-3-12 created by pangqiren
 */
public class RangePartitionComparator
        implements Comparator<PartitionInfo>, Serializable { // FIXME move this logic to core module

    private static final long serialVersionUID = -8678700348438262739L;
    private final String columnType;

    public RangePartitionComparator(String dataType) {
        this.columnType = dataType;
    }

    /**
     * Compare the two partition information
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the first argument is less than,
     *     equal to, or greater than the second.
     */
    public int compare(PartitionInfo o1, PartitionInfo o2) {
        String str1 = o1.getPartitionValues() == null ? null : o1.getPartitionValues().get(1);
        String str2 = o2.getPartitionValues() == null ? null : o2.getPartitionValues().get(1);
        return compareData(str1, str2);
    }

    /**
     * Compare the two expression value
     *
     * @param str1 the first object to be compared.
     * @param str2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the first argument is less than,
     *     equal to, or greater than the second.
     */
    public int compareData(String str1, String str2) {
        return FieldHandlerUtils.compareData(columnType, str1, str2);
    }
}
