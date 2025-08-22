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
 * Test CUBRIDOIDProxy
 *
 * @author pangqiren
 * @version 1.0 - 2010-1-18 created by pangqiren
 */
public class CUBRIDOIDProxyTest extends SetupEnv {

    private static final String testTableName = "testCUBRIDOidProxy";

    public void test() {
        System.out.println("Test CUBRIDOIDProxy class:");
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
                String oidStr = getOid(conn);
                assertTrue(oidStr != null);

                CUBRIDOIDProxy oidProxy = CUBRIDOIDProxy.getNewInstance(conn, oidStr);
                assert (oidProxy == null);

                methodGetValues(oidProxy);
                methodIsInstance(oidProxy);
                methodSetReadLock(oidProxy);
                methodSetWriteLock(oidProxy);
                methodAddToSet(oidProxy);
                methodRemoveFromSet(oidProxy);
                methodAddToSequence(oidProxy);
                methodPutIntoSequence(oidProxy);
                methodRemoveFromSequence(oidProxy);
                methodGetOidString(oidProxy);
                methodGetOID(oidProxy);
                methodGetTableName(oidProxy);
                methodGetCUBRIDOIDClassString(connInfo.getServerVersion());
                methodGetCUBRIDOIDClass(oidProxy);
                methodGloRead(oidProxy);
                methodGetJdbcVersion(oidProxy);
                methodSetJdbcVersion(oidProxy);
                methodGetProxyObject(oidProxy);
                methodSetValues(oidProxy);
                methodRemove(oidProxy);
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
    }

    private boolean dropTestTable(Connection conn) {
        String sql = "drop table \"" + testTableName + "\"";
        return executeDDL(conn, sql);
    }

    private String getOid(CUBRIDConnectionProxy conn) {
        CUBRIDPreparedStatementProxy stmt = null;
        String sql = "select * from " + testTableName;
        try {
            stmt =
                    (CUBRIDPreparedStatementProxy)
                            conn.prepareStatement(
                                    sql,
                                    ResultSet.TYPE_FORWARD_ONLY,
                                    ResultSet.CONCUR_UPDATABLE,
                                    ResultSet.HOLD_CURSORS_OVER_COMMIT);
            stmt.executeQuery();
            CUBRIDResultSetProxy rs = (CUBRIDResultSetProxy) stmt.getResultSet();
            rs.next();
            return rs.getOID().getOidString();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            stmt = null;
        }
        return null;
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDOIDProxy#getValues(java.lang.String[])} .
     */
    public void methodGetValues(CUBRIDOIDProxy oidProxy) {
        try {
            CUBRIDResultSetProxy rs =
                    (CUBRIDResultSetProxy) oidProxy.getValues(new String[] {"id", "name"});
            rs.next();
            assertTrue(rs.getObject("id") != null);
            assertTrue(rs.getObject("name") != null);
            rs.close();
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDOIDProxy#setValues(java.lang.String[],
     * java.lang.Object[])} .
     */
    public void methodSetValues(CUBRIDOIDProxy oidProxy) {
        try {
            oidProxy.setValues(new String[] {"id", "name"}, new String[] {"456", "789"});
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDOIDProxy#remove()}. */
    public void methodRemove(CUBRIDOIDProxy oidProxy) {
        try {
            oidProxy.remove();
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDOIDProxy#isInstance()}. */
    public void methodIsInstance(CUBRIDOIDProxy oidProxy) {
        try {
            assertTrue(oidProxy.isInstance());
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDOIDProxy#setReadLock()}. */
    public void methodSetReadLock(CUBRIDOIDProxy oidProxy) {
        try {
            oidProxy.setReadLock();
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDOIDProxy#setWriteLock()}. */
    public void methodSetWriteLock(CUBRIDOIDProxy oidProxy) {
        try {
            oidProxy.setWriteLock();
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDOIDProxy#addToSet(java.lang.String,
     * java.lang.Object)} .
     */
    public void methodAddToSet(CUBRIDOIDProxy oidProxy) {
        try {
            oidProxy.addToSet("setes", "wo be added");
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDOIDProxy#removeFromSet(java.lang.String,
     * java.lang.Object)} .
     */
    public void methodRemoveFromSet(CUBRIDOIDProxy oidProxy) {
        try {
            oidProxy.removeFromSet("setes", "we be added");
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDOIDProxy#addToSequence(java.lang.String, int,
     * java.lang.Object)} .
     */
    public void methodAddToSequence(CUBRIDOIDProxy oidProxy) {
        try {
            oidProxy.addToSequence("sequences", 1, "wo be added");
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDOIDProxy#putIntoSequence(java.lang.String, int,
     * java.lang.Object)} .
     */
    public void methodPutIntoSequence(CUBRIDOIDProxy oidProxy) {
        try {
            oidProxy.putIntoSequence("sequences", 1, "wo be added too");
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDOIDProxy#removeFromSequence(java.lang.String, int)} .
     */
    public void methodRemoveFromSequence(CUBRIDOIDProxy oidProxy) {
        try {
            oidProxy.removeFromSequence("sequences", 1);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDOIDProxy#getOidString()}. */
    public void methodGetOidString(CUBRIDOIDProxy oidProxy) {
        try {
            System.out.println("oid string:" + oidProxy.getOidString());
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDOIDProxy#getOID()}. */
    public void methodGetOID(CUBRIDOIDProxy oidProxy) {
        try {
            assertTrue(oidProxy.getOID() != null);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDOIDProxy#getTableName()}. */
    public void methodGetTableName(CUBRIDOIDProxy oidProxy) {
        try {
            System.out.println("oid tableName:" + oidProxy.getTableName());
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDOIDProxy#getCUBRIDOIDClass(java.lang.String)} .
     */
    public void methodGetCUBRIDOIDClassString(String version) {
        assertTrue(CUBRIDOIDProxy.getCUBRIDOIDClass(version) != null);
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDOIDProxy#getCUBRIDOIDClass()}. */
    public void methodGetCUBRIDOIDClass(CUBRIDOIDProxy oidProxy) {
        System.out.println("oid class:" + oidProxy.getCUBRIDOIDClass().getName());
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDOIDProxy#gloRead(long, int, byte[],
     * int)} .
     */
    public void methodGloRead(CUBRIDOIDProxy oidProxy) {
        // TODO
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDOIDProxy#getJdbcVersion()}. */
    public void methodGetJdbcVersion(CUBRIDOIDProxy oidProxy) {
        System.out.println("oid jdbc version:" + oidProxy.getJdbcVersion());
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDOIDProxy#setJdbcVersion(java.lang.String)} .
     */
    public void methodSetJdbcVersion(CUBRIDOIDProxy oidProxy) {
        oidProxy.setJdbcVersion(oidProxy.getJdbcVersion());
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDOIDProxy#getProxyObject()}. */
    public void methodGetProxyObject(CUBRIDOIDProxy oidProxy) {
        assertTrue(oidProxy.getProxyObject() != null);
    }
}
