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
package com.cubrid.common.core.util;

import junit.framework.TestCase;

/**
 * PageUtilTest Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-6-5 created by Kevin.Wang
 */
public class PageUtilTest extends TestCase {

    public void testPageUtil() {
        PageUtil pageUtil = new PageUtil(120, 10);
        pageUtil.setTotalRs(120);
        assertEquals(120, pageUtil.getTotalRs());
        assertEquals(12, pageUtil.getPages());
        assertEquals(10, pageUtil.getPageSize());
        assertEquals(1, pageUtil.getCurrentPage());

        pageUtil.setTotalRs(249);
        pageUtil.setPageSize(20);
        pageUtil.setCurrentPage(2);
        assertEquals(249, pageUtil.getTotalRs());
        assertEquals(13, pageUtil.getPages());
        assertEquals(20, pageUtil.getPageSize());
        assertEquals(2, pageUtil.getCurrentPage());

        pageUtil = new PageUtil(249, 10);
        assertEquals(249, pageUtil.getTotalRs());
        assertEquals(25, pageUtil.getPages());
        assertEquals(10, pageUtil.getPageSize());
        assertEquals(1, pageUtil.getCurrentPage());

        pageUtil = new PageUtil(250, 10);
        pageUtil.setPageSize(10);
        assertEquals(25, pageUtil.getPages());
    }
}
