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
package com.cubrid.cubridmanager.core.utils;

import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.cubridmanager.core.SetupJDBCTestCase;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * QueryUtil Test Cases
 *
 * @author Kevin Cao
 * @version 1.0 - 2010-12-22 created by Kevin Cao
 */
public class QueryUtilTest extends SetupJDBCTestCase {

    public static void testfreeQuery3() {
        try {
            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;
            QueryUtil.freeQuery(conn, stmt, rs);
            conn = JDBCConnectionManager.getConnection(databaseInfo, false);
            conn.setAutoCommit(true);
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select count(*) from db_auth");
            QueryUtil.freeQuery(conn, stmt, rs);
            assertTrue(conn.isClosed());
            // assertTrue(stmt.isClosed());
            // assertTrue(rs.isClosed());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void testfreeQuery21() {
        try {
            Connection conn = null;
            Statement stmt = null;
            QueryUtil.freeQuery(conn, stmt);
            conn = JDBCConnectionManager.getConnection(databaseInfo, false);
            conn.setAutoCommit(true);
            stmt = conn.createStatement();
            QueryUtil.freeQuery(conn, stmt);
            assertTrue(conn.isClosed());
            // assertTrue(stmt.isClosed());
            // assertTrue(rs.isClosed());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void testfreeQuery22() {
        try {
            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;
            QueryUtil.freeQuery(stmt, rs);
            conn = JDBCConnectionManager.getConnection(databaseInfo, false);
            conn.setAutoCommit(true);
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select count(*) from db_auth");
            QueryUtil.freeQuery(stmt, rs);
            // assertTrue(stmt.isClosed());
            // assertTrue(rs.isClosed());
            QueryUtil.freeQuery(conn);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void testfreeQuery11() {
        try {
            Connection conn = null;
            QueryUtil.freeQuery(conn);
            conn = JDBCConnectionManager.getConnection(databaseInfo, false);
            QueryUtil.freeQuery(conn);
            assertTrue(conn.isClosed());
            // assertTrue(stmt.isClosed());
            // assertTrue(rs.isClosed());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void testfreeQuery12() {
        try {
            Connection conn = null;
            Statement stmt = null;
            QueryUtil.freeQuery(stmt);
            conn = JDBCConnectionManager.getConnection(databaseInfo, false);
            conn.setAutoCommit(true);
            stmt = conn.createStatement();
            QueryUtil.freeQuery(stmt);
            // assertTrue(stmt.isClosed());
            QueryUtil.freeQuery(conn);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void testfreeQuery13() {
        try {
            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;
            QueryUtil.freeQuery(rs);
            conn = JDBCConnectionManager.getConnection(databaseInfo, false);
            conn.setAutoCommit(true);
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select count(*) from db_auth");
            QueryUtil.freeQuery(rs);
            // assertTrue(rs.isClosed());
            QueryUtil.freeQuery(conn);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
