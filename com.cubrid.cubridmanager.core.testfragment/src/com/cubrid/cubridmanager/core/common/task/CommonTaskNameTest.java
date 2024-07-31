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
package com.cubrid.cubridmanager.core.common.task;

import junit.framework.TestCase;

/**
 * Test CommonTaskName class
 *
 * @author pangqiren
 * @version 1.0 - 2010-1-5 created by pangqiren
 */
public class CommonTaskNameTest extends TestCase {

    public void testTaskName() throws Exception {
        assertEquals(CommonTaskName.STOP_DB_TASK_NAME, "stopdb");
        assertEquals(CommonTaskName.START_DB_TASK_NAME, "startdb");
        assertEquals(CommonTaskName.ADD_USER_TASK_NAME, "createuser");
        assertEquals(CommonTaskName.UPDATE_USER_TASK_NAME, "updateuser");
        assertEquals(CommonTaskName.DELETE_DATABASE_TASK_NAME, "deletedb");
        assertEquals(CommonTaskName.CHECK_DATABASE_TASK_NAME, "checkdb");
        assertEquals(CommonTaskName.DELETE_USER_TASK_NAME, "deleteuser");
        assertEquals(CommonTaskName.OPTIMIZE_DATABASE_TASK_NAME, "optimizedb");
        assertEquals(CommonTaskName.COMPACT_DATABASE_TASK_NANE, "compactdb");
        CommonTaskName ctn = new CommonTaskName();
        assertNotNull(ctn);
    }
}
