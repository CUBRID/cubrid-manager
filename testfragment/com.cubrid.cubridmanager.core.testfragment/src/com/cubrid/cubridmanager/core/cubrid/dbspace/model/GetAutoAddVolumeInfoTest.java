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
package com.cubrid.cubridmanager.core.cubrid.dbspace.model;

import junit.framework.TestCase;

/**
 * Test GetAutoAddVolumeInfo
 *
 * @author lizhiqiang
 * @version 1.0 - 2009-12-30 created by lizhiqiang
 */
public class GetAutoAddVolumeInfoTest extends TestCase {

    public void testModelGetAutoAddVolumeInfo() {
        GetAutoAddVolumeInfo bean = new GetAutoAddVolumeInfo();
        bean.setDbname("dbname");
        assertEquals(bean.getDbname(), "dbname");
        bean.setData("data");
        assertEquals(bean.getData(), "data");
        bean.setData_warn_outofspace("data_warn_outofspace");
        assertEquals(bean.getData_warn_outofspace(), "data_warn_outofspace");
        bean.setData_ext_page("data_ext_page");
        assertEquals(bean.getData_ext_page(), "data_ext_page");
        bean.setIndex("index");
        assertEquals(bean.getIndex(), "index");
        bean.setIndex_warn_outofspace("index_warn_outofspace");
        assertEquals(bean.getIndex_warn_outofspace(), "index_warn_outofspace");
        bean.setIndex_ext_page("index_ext_page");
        assertEquals(bean.getIndex_ext_page(), "index_ext_page");
        assertEquals(bean.getTaskName(), "getautoaddvol");
    }
}
