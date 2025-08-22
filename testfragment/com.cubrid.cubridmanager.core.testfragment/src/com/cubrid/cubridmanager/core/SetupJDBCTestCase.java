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
package com.cubrid.cubridmanager.core;

import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Set up the test env. All test cases which test the tasks extends from JDBCTask will extend it.
 *
 * @author pangqiren
 * @version 1.0 - 2010-1-5 created by pangqiren
 */
public abstract class SetupJDBCTestCase extends SetupEnvTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        loginTestDatabase();
        loginTestDatabase831();
        loginTestDatabase930();
        startTestDatabase();
    }

    /**
     * execute DDL statement like "create table","drop table"
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    public boolean executeDDL(String sql) {
        boolean success = false;
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = JDBCConnectionManager.getConnection(databaseInfo, true);
            conn.setAutoCommit(true);
            stmt = conn.createStatement();
            boolean isMultiResult = stmt.execute(sql);
            assert (isMultiResult == false);
            success = true;
            conn.commit();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            QueryUtil.freeQuery(conn, stmt);
            stmt = null;
            conn = null;
        }
        return success;
    }

    /**
     * execute DML statement like "insert into ","delete from table"
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    public int executeUpdate(String sql) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = JDBCConnectionManager.getConnection(databaseInfo, false);
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            int count = stmt.executeUpdate(sql);
            conn.commit();
            return count;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            QueryUtil.freeQuery(conn, stmt);
            stmt = null;
            conn = null;
        }
        return -1;
    }
}
