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
package com.cubrid.common.ui.query.editor;

import java.math.BigDecimal;

/**
 * CUBRID Server node
 *
 * @author Santiago Wang
 * @version 1.0 - 2013-10-22 created by Santiago Wang
 */
public class QueryResultTableCalcInfo { // move ot core module

    private int count = -1;
    private BigDecimal average = null;
    private BigDecimal summary = null;
    private boolean isHasSum = false;

    public QueryResultTableCalcInfo(int count, BigDecimal average, BigDecimal summary) {
        this.count = count;
        this.average = average;
        this.summary = summary;
        this.isHasSum = true;
    }

    public QueryResultTableCalcInfo(int count) {
        this.count = count;
        this.isHasSum = false;
    }

    public int getCount() {
        return count;
    }

    public BigDecimal getAverage() {
        return average;
    }

    public BigDecimal getSummary() {
        return summary;
    }

    public boolean isHasSum() {
        return isHasSum;
    }
}
