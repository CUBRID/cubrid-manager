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
package com.cubrid.jdbc.proxy.driver;

import com.cubrid.jdbc.proxy.ConnectionInfo;
import com.cubrid.jdbc.proxy.SetupEnv;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Test CUBRIDStatementProxy
 *
 * @author pangqiren
 * @version 1.0 - 2010-1-18 created by pangqiren
 */
public class CUBRIDStatementProxyTest extends SetupEnv {

    private static final String testTableName = "testCUBRIDStmtProxy";

    public void test() {
        System.out.println("Test CUBRIDStatementProxy class:");
        for (ConnectionInfo connInfo : connInfoList) {
            CUBRIDConnectionProxy conn = null;
            System.out.println("Test connection url:" + connInfo.getConnectionUrl());
            try {
                CUBRIDDriverProxy driver = new CUBRIDDriverProxy(connInfo.getServerVersion());
                Properties props = new Properties();
                props.put("user", connInfo.getDbUser());
                props.put(
                        "password",
                        connInfo.getDbUserPass() == null ? "" : connInfo.getDbUserPass());
                conn = (CUBRIDConnectionProxy) driver.connect(connInfo.getConnectionUrl(), props);
                createTestTable(conn);
                CUBRIDStatementProxy stmt = (CUBRIDStatementProxy) conn.createStatement();
                methodCUBRIDStatementProxy(stmt);
                methodAddBatch(stmt);
                methodExecuteString(stmt);
                methodExecuteStringInt(stmt);
                methodExecuteStringIntArray(stmt);
                methodExecuteStringStringArray(stmt);
                methodExecuteBatch(stmt);
                methodExecuteQuery(stmt);
                methodExecuteUpdateString(stmt);
                methodExecuteUpdateStringInt(stmt);
                methodExecuteUpdateStringIntArray(stmt);
                methodExecuteUpdateStringStringArray(stmt);
                methodGetConnection(stmt);
                methodGetFetchDirection(stmt);
                methodGetFetchSize(stmt);
                methodGetGeneratedKeys(stmt);
                methodGetMaxFieldSize(stmt);
                methodGetMaxRows(stmt);
                methodGetMoreResults(stmt);
                methodGetMoreResultsInt(stmt);
                methodGetQueryTimeout(stmt);
                methodGetResultSet(stmt);
                methodGetResultSetConcurrency(stmt);
                methodGetResultSetHoldability(stmt);
                methodGetResultSetType(stmt);
                methodGetUpdateCount(stmt);
                methodGetWarnings(stmt);
                methodSetCursorName(stmt);
                methodSetEscapeProcessing(stmt);
                methodSetFetchDirection(conn);
                methodSetFetchSize(stmt);
                methodSetMaxFieldSize(stmt);
                methodSetMaxRows(stmt);
                methodSetQueryTimeout(stmt);
                methodExecuteInsert(stmt);
                methodGetQueryplan(stmt);
                methodGetQueryplanString(stmt);
                methodGetStatementType(stmt);
                methodSetOnlyQueryPlan(stmt);
                methodSetQueryInfo(stmt);
                methodGetServerVersion(stmt);
                methodSetServerVersion(stmt);
                methodCancel(stmt);
                methodClearBatch(stmt);
                methodClearWarnings(stmt);
                // new test cases for jdk 1.6 jdbc driver
                methodIsClosed(stmt);

                methodIsPoolable(stmt);

                methodSetPoolable(stmt);

                methodIsWrapperFor(stmt);

                methodUnwrap(stmt);

                methodClose(stmt);
                dropTestTable(conn);
            } catch (Exception e) {
                assertFalse(true);
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                    }
                }
            }
        }
    }

    private void createTestTable(Connection conn) {
        String sql =
                "create table \""
                        + testTableName
                        + "\" ("
                        + "id character varying(40)  NOT NULL ,"
                        + "name character varying(40)  NOT NULL,"
                        + "setes set_of(character varying(20)),"
                        + "sequences sequence_of(character varying(20))"
                        + ")";
        executeDDL(conn, sql);
        sql =
                "insert into \""
                        + testTableName
                        + "\" values('123','456',{'445','567'},{'445','56'})";
        executeDDL(conn, sql);
        sql =
                "insert into \""
                        + testTableName
                        + "\" values('123','456',{'445','567'},{'445','56'})";
        executeDDL(conn, sql);
    }

    private boolean dropTestTable(Connection conn) {
        String sql = "drop table \"" + testTableName + "\"";
        return executeDDL(conn, sql);
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#CUBRIDStatementProxy(java.sql.Statement)} .
     */
    public void methodCUBRIDStatementProxy(CUBRIDStatementProxy stmt) {
        assertTrue(new CUBRIDStatementProxy(stmt) != null);
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#addBatch(java.lang.String)} .
     */
    public void methodAddBatch(CUBRIDStatementProxy stmt) {
        try {
            String sql = "select * from " + testTableName;
            stmt.addBatch(sql);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#cancel()}. */
    public void methodCancel(CUBRIDStatementProxy stmt) {
        try {
            stmt.cancel();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#clearBatch()}. */
    public void methodClearBatch(CUBRIDStatementProxy stmt) {
        try {
            stmt.clearBatch();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#clearWarnings()} .
     */
    public void methodClearWarnings(CUBRIDStatementProxy stmt) {
        try {
            stmt.clearWarnings();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#close()}. */
    public void methodClose(CUBRIDStatementProxy stmt) {
        try {
            stmt.close();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#execute(java.lang.String)} .
     */
    public void methodExecuteString(CUBRIDStatementProxy stmt) {
        try {
            String sql = "select * from " + testTableName;
            stmt.execute(sql);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#execute(java.lang.String, int)} .
     */
    public void methodExecuteStringInt(CUBRIDStatementProxy stmt) {
        try {
            String sql = "select * from " + testTableName;
            stmt.execute(sql, 1);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#execute(java.lang.String, int[])} .
     */
    public void methodExecuteStringIntArray(CUBRIDStatementProxy stmt) {
        try {
            String sql = "select * from " + testTableName;
            int a[] = {1, 2, 3, 4};
            stmt.execute(sql, a);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#execute(java.lang.String,
     * java.lang.String[])} .
     */
    public void methodExecuteStringStringArray(CUBRIDStatementProxy stmt) {
        try {
            String sql = "select * from " + testTableName;
            String a[] = {"id", "name", "setes", "sequences"};
            stmt.execute(sql, a);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#executeBatch()}. */
    public void methodExecuteBatch(CUBRIDStatementProxy stmt) {
        try {
            stmt.executeBatch();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#executeQuery(java.lang.String)} .
     */
    public void methodExecuteQuery(CUBRIDStatementProxy stmt) {
        try {
            String sql = "select * from " + testTableName;
            stmt.executeQuery(sql);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#executeUpdate(java.lang.String)} .
     */
    public void methodExecuteUpdateString(CUBRIDStatementProxy stmt) {
        try {
            String sql = "update \"" + testTableName + "\" set name = 'name'";
            System.out.println("executeUpdate(String sql):" + stmt.executeUpdate(sql));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#executeUpdate(java.lang.String, int)} .
     */
    public void methodExecuteUpdateStringInt(CUBRIDStatementProxy stmt) {
        try {
            String sql = "update \"" + testTableName + "\" set name = 'name'";
            System.out.println(
                    "executeUpdate(String sql, int autoGeneratedKeys):"
                            + stmt.executeUpdate(sql, 1));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#executeUpdate(java.lang.String, int[])} .
     */
    public void methodExecuteUpdateStringIntArray(CUBRIDStatementProxy stmt) {
        try {
            String sql = "update \"" + testTableName + "\" set name = 'name'";
            int a[] = {1, 2, 3, 4};
            System.out.println(
                    "executeUpdate(String sql, int[] columnIndexes):" + stmt.executeUpdate(sql, a));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#executeUpdate(java.lang.String,
     * java.lang.String[])} .
     */
    public void methodExecuteUpdateStringStringArray(CUBRIDStatementProxy stmt) {
        try {
            String sql = "update \"" + testTableName + "\" set name = 'name'";
            String a[] = {"id", "name", "setes", "sequences"};
            System.out.println(
                    "executeUpdate(String sql, String[] columnNames):"
                            + stmt.executeUpdate(sql, a));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#getConnection()} .
     */
    public void methodGetConnection(CUBRIDStatementProxy stmt) {
        try {
            stmt.getConnection();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#getFetchDirection()}
     * .
     */
    public void methodGetFetchDirection(CUBRIDStatementProxy stmt) {
        try {
            stmt.getFetchDirection();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#getFetchSize()}. */
    public void methodGetFetchSize(CUBRIDStatementProxy stmt) {
        try {
            stmt.getFetchSize();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#getGeneratedKeys()}
     * .
     */
    public void methodGetGeneratedKeys(CUBRIDStatementProxy stmt) {
        try {
            stmt.getGeneratedKeys();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#getMaxFieldSize()} .
     */
    public void methodGetMaxFieldSize(CUBRIDStatementProxy stmt) {
        try {
            stmt.getMaxFieldSize();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#getMaxRows()}. */
    public void methodGetMaxRows(CUBRIDStatementProxy stmt) {
        try {
            stmt.getMaxRows();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#getMoreResults()} .
     */
    public void methodGetMoreResults(CUBRIDStatementProxy stmt) {
        try {
            stmt.getMoreResults();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#getMoreResults(int)}
     * .
     */
    public void methodGetMoreResultsInt(CUBRIDStatementProxy stmt) {
        try {
            stmt.getMoreResults(1);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#getQueryTimeout()} .
     */
    public void methodGetQueryTimeout(CUBRIDStatementProxy stmt) {
        try {
            stmt.getQueryTimeout();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#getResultSet()}. */
    public void methodGetResultSet(CUBRIDStatementProxy stmt) {
        try {
            stmt.getResultSet();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#getResultSetConcurrency()} .
     */
    public void methodGetResultSetConcurrency(CUBRIDStatementProxy stmt) {
        try {
            stmt.getResultSetConcurrency();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#getResultSetHoldability()} .
     */
    public void methodGetResultSetHoldability(CUBRIDStatementProxy stmt) {
        try {
            stmt.getResultSetHoldability();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#getResultSetType()}
     * .
     */
    public void methodGetResultSetType(CUBRIDStatementProxy stmt) {
        try {
            stmt.getResultSetType();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#getUpdateCount()} .
     */
    public void methodGetUpdateCount(CUBRIDStatementProxy stmt) {
        try {
            stmt.getUpdateCount();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#getWarnings()}. */
    public void methodGetWarnings(CUBRIDStatementProxy stmt) {
        try {
            stmt.getWarnings();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#setCursorName(java.lang.String)} .
     */
    public void methodSetCursorName(CUBRIDStatementProxy stmt) {
        try {
            stmt.setCursorName("testCursor");
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#setEscapeProcessing(boolean)} .
     */
    public void methodSetEscapeProcessing(CUBRIDStatementProxy stmt) {
        try {
            stmt.setEscapeProcessing(true);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#setFetchDirection(int)} .
     */
    public void methodSetFetchDirection(CUBRIDConnectionProxy conn) {
        try {
            CUBRIDStatementProxy stmt =
                    (CUBRIDStatementProxy)
                            conn.createStatement(
                                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            stmt.setFetchDirection(ResultSet.FETCH_FORWARD);
            assertTrue(true);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#setFetchSize(int)} .
     */
    public void methodSetFetchSize(CUBRIDStatementProxy stmt) {
        try {
            stmt.setFetchSize(1);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#setMaxFieldSize(int)} .
     */
    public void methodSetMaxFieldSize(CUBRIDStatementProxy stmt) {
        try {
            stmt.setMaxFieldSize(5);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#setMaxRows(int)} .
     */
    public void methodSetMaxRows(CUBRIDStatementProxy stmt) {
        try {
            stmt.setMaxRows(5);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#setQueryTimeout(int)} .
     */
    public void methodSetQueryTimeout(CUBRIDStatementProxy stmt) {
        try {
            stmt.setQueryTimeout(5);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#executeInsert(java.lang.String)} .
     */
    public void methodExecuteInsert(CUBRIDStatementProxy stmt) {
        try {
            String sql =
                    "insert into \""
                            + testTableName
                            + "\" values('123','456',{'445','567'},{'445','56'})";
            stmt.executeInsert(sql);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#getQueryplan()}. */
    public void methodGetQueryplan(CUBRIDStatementProxy stmt) {
        try {
            stmt.getQueryplan();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#getQueryplan(java.lang.String)} .
     */
    public void methodGetQueryplanString(CUBRIDStatementProxy stmt) {
        try {
            String sql = "select * from " + testTableName;
            stmt.getQueryplan(sql);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#getStatementType()}
     * .
     */
    public void methodGetStatementType(CUBRIDStatementProxy stmt) {
        try {
            stmt.getStatementType();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#setOnlyQueryPlan(boolean)} .
     */
    public void methodSetOnlyQueryPlan(CUBRIDStatementProxy stmt) {
        try {
            stmt.setOnlyQueryPlan(true);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#setQueryInfo(boolean)} .
     */
    public void methodSetQueryInfo(CUBRIDStatementProxy stmt) {
        try {
            stmt.setQueryInfo(true);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#getJdbcVersion()} .
     */
    public void methodGetServerVersion(CUBRIDStatementProxy stmt) {
        stmt.getJdbcVersion();
        assertTrue(true);
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#setJdbcVersion(java.lang.String)} .
     */
    public void methodSetServerVersion(CUBRIDStatementProxy stmt) {
        stmt.setJdbcVersion("8.3.1");
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#isClosed()} . */
    public void methodIsClosed(CUBRIDStatementProxy stmt) throws SQLException {
        // TODO:stmt.isClosed();
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#isPoolable()} . */
    public void methodIsPoolable(CUBRIDStatementProxy stmt) throws SQLException {
        // TODO:stmt.isPoolable();
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#setPoolable(boolean)} .
     */
    public void methodSetPoolable(CUBRIDStatementProxy stmt) throws SQLException {
        // TODO:stmt.setPoolable(poolable);
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#isWrapperFor(Class)}
     * .
     */
    public void methodIsWrapperFor(CUBRIDStatementProxy stmt) throws SQLException {
        // TODO:stmt.isWrapperFor(iface);
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy#unwrap(Class)} . */
    public void methodUnwrap(CUBRIDStatementProxy stmt) throws SQLException {
        // TODO:stmt.unwrap(iface);
    }
}
