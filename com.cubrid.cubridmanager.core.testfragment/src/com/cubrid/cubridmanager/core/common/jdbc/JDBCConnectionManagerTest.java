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
package com.cubrid.cubridmanager.core.common.jdbc;

import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.cubridmanager.core.SetupJDBCTestCase;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

/**
 * test JDBCConnectionManager
 *
 * @author wuyingshi
 * @version 1.0 - 2010-1-5 created by wuyingshi
 */
public class JDBCConnectionManagerTest extends SetupJDBCTestCase {
    /** test normal case */
    public void testNormalCase() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            databaseInfo.setJdbcAttrs("debug=true");
            conn = JDBCConnectionManager.getConnection(databaseInfo, false);
            conn.setAutoCommit(true);
            stmt = conn.createStatement();
            String sql = "select * from db_user";
            rs = stmt.executeQuery(sql);
            assertTrue(rs.next());
            conn.commit();
        } catch (Exception e) {
            assertFalse(true);
        } finally {
            QueryUtil.freeQuery(conn, stmt, rs);
            rs = null;
            stmt = null;
            conn = null;
        }
    }

    /** test exception case */
    public void testExceptionCase() {
        // exception case 1
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = JDBCConnectionManager.getConnection(null, false);
            assertTrue(false);
            conn.setAutoCommit(true);
            stmt = conn.createStatement();
            String sql = "select * from db_user";
            stmt.execute(sql);
            conn.commit();
        } catch (Exception e) {
        } finally {
            QueryUtil.freeQuery(conn, stmt);
            stmt = null;
            conn = null;
        }
        // exception case 2
        try {
            conn = JDBCConnectionManager.getConnection(new DatabaseInfo("demodb", null), false);
            assertTrue(false);
            conn.setAutoCommit(true);
            stmt = conn.createStatement();
            String sql = "select * from db_user";
            stmt.execute(sql);
            conn.commit();
        } catch (Exception e) {
        } finally {
            QueryUtil.freeQuery(conn, stmt);
            stmt = null;
            conn = null;
        }
    }

    public void testGetConnection() {
        // exception case 1
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = JDBCConnectionManager.getConnection(null, false);
            assertTrue(false);
            conn.setAutoCommit(true);
            stmt = conn.createStatement();
            String sql = "select * from db_user";
            stmt.execute(sql);
            conn.commit();
        } catch (Exception e) {
            assertTrue(true);
        } finally {
            QueryUtil.freeQuery(conn, stmt);
            stmt = null;
            conn = null;
        }
    }

    public void testParseJdbcOptions() {
        Properties prop = JDBCConnectionManager.parseJdbcOptions("debug=true&charset=utf-8", true);
        assertTrue(prop != null);
    }

    public void testIsConnectable() {
        assertTrue(JDBCConnectionManager.isConnectable(databaseInfo));
        try {
            JDBCConnectionManager.isConnectable(null);
        } catch (Exception ex) {

        }
    }

    public void testShardBroker() {
        assertTrue(JDBCConnectionManager.testShardBroker(databaseInfo, 1));
        assertFalse(
                JDBCConnectionManager.testShardBroker(new DatabaseInfo("", new ServerInfo()), 1));
    }
}
