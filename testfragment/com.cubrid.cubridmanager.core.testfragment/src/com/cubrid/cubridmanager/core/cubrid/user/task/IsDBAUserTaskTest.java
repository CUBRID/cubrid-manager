/*
 * Copyright (C) 2008 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.cubridmanager.core.cubrid.user.task;

import com.cubrid.cubridmanager.core.SetupJDBCTestCase;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Test the type of IsDBAUserTask
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-12-22 created by lizhiqiang
 */
public class IsDBAUserTaskTest extends SetupJDBCTestCase {
    IsDBAUserTask checkTask;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        checkTask = new IsDBAUserTask(databaseInfo);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.user.task.IsDBAUserTask#execute()} .
     */
    public void testExecuteOne() {
        checkTask = new IsDBAUserTask(databaseInfo);
        checkTask.execute();
        assertTrue(checkTask.isDBAUser());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.user.task.IsDBAUserTask#execute()} .
     *
     * @throws SQLException
     */
    public void testExecuteTwo() {
        DbUserInfo userInfo = databaseInfo.getAuthLoginedDbUserInfo();
        String userName = userInfo.getName();
        userInfo.setName("other");
        checkTask.execute();
        assertTrue(checkTask.isDBAUser());
        userInfo.setName(userName);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.user.task.IsDBAUserTask#execute()} .
     *
     * @throws SQLException
     */
    public void testExecuteThree() throws SQLException {
        Connection conn = checkTask.getConnection();
        if (conn != null) {
            conn.close();
        }
        DbUserInfo userInfo = databaseInfo.getAuthLoginedDbUserInfo();
        String userName = userInfo.getName();
        userInfo.setName("other");
        checkTask.execute();
        assertFalse(checkTask.isDBAUser());
        userInfo.setName(userName);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.user.task.IsDBAUserTask#execute()} .
     *
     * @throws SQLException
     */
    public void testExecuteFour() {
        DbUserInfo userInfo = databaseInfo.getAuthLoginedDbUserInfo();
        String userName = userInfo.getName();
        userInfo.setName("other");
        checkTask.setErrorMsg("error");
        checkTask.execute();
        assertFalse(checkTask.isDBAUser());
        userInfo.setName(userName);
    }
}
