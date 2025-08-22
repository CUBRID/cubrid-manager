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
package com.cubrid.common.core.model;

import com.cubrid.common.core.common.model.QueryTypeCounts;
import junit.framework.TestCase;

/**
 * QueryTypeCountsTest Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-6-7 created by Kevin.Wang
 */
public class QueryTypeCountsTest extends TestCase {
    public void testQueryTypeCounts() {
        QueryTypeCounts queryTypeCounts = new QueryTypeCounts();
        queryTypeCounts.setDrops(1);

        assertTrue(queryTypeCounts.existModifyingQuery());

        queryTypeCounts.setAlters(1);
        queryTypeCounts.setCreates(1);
        queryTypeCounts.setDeletes(1);
        queryTypeCounts.setDrops(1);
        queryTypeCounts.setExtras(1);
        queryTypeCounts.setInserts(1);
        queryTypeCounts.setSelects(1);
        queryTypeCounts.setUpdates(1);

        queryTypeCounts.increaseAlters();
        queryTypeCounts.increaseCreates();
        queryTypeCounts.increaseDeletes();
        queryTypeCounts.increaseDrops();
        queryTypeCounts.increaseExtras();
        queryTypeCounts.increaseInserts();
        queryTypeCounts.increaseSelects();
        queryTypeCounts.increaseUpdates();

        assertEquals(2, queryTypeCounts.getAlters());
        assertEquals(2, queryTypeCounts.getCreates());
        assertEquals(2, queryTypeCounts.getDeletes());
        assertEquals(2, queryTypeCounts.getDrops());
        assertEquals(2, queryTypeCounts.getExtras());
        assertEquals(2, queryTypeCounts.getInserts());
        assertEquals(2, queryTypeCounts.getSelects());
        assertEquals(2, queryTypeCounts.getUpdates());
    }
}
