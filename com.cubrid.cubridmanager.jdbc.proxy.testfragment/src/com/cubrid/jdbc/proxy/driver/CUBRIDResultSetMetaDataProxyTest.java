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
import java.sql.SQLException;
import java.util.Properties;

/**
 * Test CUBRIDResultSetMetaDataProxy
 *
 * @author pangqiren
 * @version 1.0 - 2010-1-18 created by pangqiren
 */
public class CUBRIDResultSetMetaDataProxyTest extends SetupEnv {

    private static final String testTableName = "testCUBRIDRsMetaDataProxy";

    public void test() {
        System.out.println("Test CUBRIDResultSetProxy class:");
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
                String sql = "select * from " + testTableName;
                CUBRIDResultSetProxy rs = (CUBRIDResultSetProxy) stmt.executeQuery(sql);
                CUBRIDResultSetMetaDataProxy rsMetaData =
                        (CUBRIDResultSetMetaDataProxy) rs.getMetaData();

                methodCUBRIDResultSetMetaDataProxy(rsMetaData);

                methodGetCatalogName(rsMetaData);

                methodGetColumnClassName(rsMetaData);

                methodGetColumnCount(rsMetaData);

                methodGetColumnDisplaySize(rsMetaData);

                methodGetColumnLabel(rsMetaData);

                methodGetColumnName(rsMetaData);

                methodGetColumnType(rsMetaData);

                methodGetColumnTypeName(rsMetaData);

                methodGetPrecision(rsMetaData);

                methodGetScale(rsMetaData);

                methodGetSchemaName(rsMetaData);

                methodGetTableName(rsMetaData);

                methodIsAutoIncrement(rsMetaData);

                methodIsCaseSensitive(rsMetaData);

                methodIsCurrency(rsMetaData);

                methodIsDefinitelyWritable(rsMetaData);

                methodIsNullable(rsMetaData);

                methodIsReadOnly(rsMetaData);

                methodIsSearchable(rsMetaData);

                methodIsSigned(rsMetaData);

                methodIsWritable(rsMetaData);

                methodGetElementType(rsMetaData);

                methodGetElementTypeName(rsMetaData);

                // new test cases for jdb 1.6 jdbc drivers
                methodIsWrapperFor(rsMetaData);

                methodUnwrap(rsMetaData);

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

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy#CUBRIDResultSetMetaDataProxy(java.sql.ResultSetMetaData)}
     * .
     */
    public void methodCUBRIDResultSetMetaDataProxy(CUBRIDResultSetMetaDataProxy rsMetaData) {
        assertTrue(new CUBRIDResultSetMetaDataProxy(rsMetaData) != null);
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy#getCatalogName(int)} .
     */
    public void methodGetCatalogName(CUBRIDResultSetMetaDataProxy rsMetaData) {
        try {
            System.out.println("CatalogName:" + rsMetaData.getCatalogName(1));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy#getColumnClassName(int)} .
     */
    public void methodGetColumnClassName(CUBRIDResultSetMetaDataProxy rsMetaData) {
        try {
            System.out.println("ColumnClassName:" + rsMetaData.getColumnClassName(1));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy#getColumnCount()} .
     */
    public void methodGetColumnCount(CUBRIDResultSetMetaDataProxy rsMetaData) {
        try {
            System.out.println("ColumnCount:" + rsMetaData.getColumnCount());
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy#getColumnDisplaySize(int)} .
     */
    public void methodGetColumnDisplaySize(CUBRIDResultSetMetaDataProxy rsMetaData) {
        try {
            System.out.println("ColumnDisplaySize:" + rsMetaData.getColumnDisplaySize(1));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy#getColumnLabel(int)} .
     */
    public void methodGetColumnLabel(CUBRIDResultSetMetaDataProxy rsMetaData) {
        try {
            System.out.println("ColumnLabel:" + rsMetaData.getColumnLabel(1));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy#getColumnName(int)} .
     */
    public void methodGetColumnName(CUBRIDResultSetMetaDataProxy rsMetaData) {
        try {
            System.out.println("ColumnName:" + rsMetaData.getColumnName(1));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy#getColumnType(int)} .
     */
    public void methodGetColumnType(CUBRIDResultSetMetaDataProxy rsMetaData) {
        try {
            System.out.println("ColumnType:" + rsMetaData.getColumnType(1));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy#getColumnTypeName(int)} .
     */
    public void methodGetColumnTypeName(CUBRIDResultSetMetaDataProxy rsMetaData) {
        try {
            System.out.println("ColumnTypeName:" + rsMetaData.getColumnTypeName(1));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy#getPrecision(int)} .
     */
    public void methodGetPrecision(CUBRIDResultSetMetaDataProxy rsMetaData) {
        try {
            System.out.println("Precision:" + rsMetaData.getPrecision(1));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy#getScale(int)} .
     */
    public void methodGetScale(CUBRIDResultSetMetaDataProxy rsMetaData) {
        try {
            System.out.println("Scale:" + rsMetaData.getScale(1));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy#getSchemaName(int)} .
     */
    public void methodGetSchemaName(CUBRIDResultSetMetaDataProxy rsMetaData) {
        try {
            System.out.println("SchemaName:" + rsMetaData.getSchemaName(1));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy#getTableName(int)} .
     */
    public void methodGetTableName(CUBRIDResultSetMetaDataProxy rsMetaData) {
        try {
            System.out.println("TableName:" + rsMetaData.getTableName(1));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy#isAutoIncrement(int)} .
     */
    public void methodIsAutoIncrement(CUBRIDResultSetMetaDataProxy rsMetaData) {
        try {
            System.out.println("isAutoIncrement:" + rsMetaData.isAutoIncrement(1));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy#isCaseSensitive(int)} .
     */
    public void methodIsCaseSensitive(CUBRIDResultSetMetaDataProxy rsMetaData) {
        try {
            System.out.println("IsCaseSensitive:" + rsMetaData.isCaseSensitive(1));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy#isCurrency(int)} .
     */
    public void methodIsCurrency(CUBRIDResultSetMetaDataProxy rsMetaData) {
        try {
            System.out.println("IsCurrency:" + rsMetaData.isCurrency(1));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy#isDefinitelyWritable(int)} .
     */
    public void methodIsDefinitelyWritable(CUBRIDResultSetMetaDataProxy rsMetaData) {
        try {
            System.out.println("IsDefinitelyWritable:" + rsMetaData.isDefinitelyWritable(1));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy#isNullable(int)} .
     */
    public void methodIsNullable(CUBRIDResultSetMetaDataProxy rsMetaData) {
        try {
            System.out.println("IsNullable:" + rsMetaData.isNullable(1));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy#isReadOnly(int)} .
     */
    public void methodIsReadOnly(CUBRIDResultSetMetaDataProxy rsMetaData) {
        try {
            System.out.println("IsReadOnly:" + rsMetaData.isReadOnly(1));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy#isSearchable(int)} .
     */
    public void methodIsSearchable(CUBRIDResultSetMetaDataProxy rsMetaData) {
        try {
            System.out.println("IsSearchable:" + rsMetaData.isSearchable(1));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy#isSigned(int)} .
     */
    public void methodIsSigned(CUBRIDResultSetMetaDataProxy rsMetaData) {
        try {
            System.out.println("IsSigned:" + rsMetaData.isSigned(1));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy#isWritable(int)} .
     */
    public void methodIsWritable(CUBRIDResultSetMetaDataProxy rsMetaData) {
        try {
            System.out.println("IsWritable:" + rsMetaData.isWritable(1));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy#getElementType(int)} .
     */
    public void methodGetElementType(CUBRIDResultSetMetaDataProxy rsMetaData) {
        try {
            System.out.println("ElementType:" + rsMetaData.getElementType(3));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy#getElementTypeName(int)} .
     */
    public void methodGetElementTypeName(CUBRIDResultSetMetaDataProxy rsMetaData) {
        try {
            System.out.println("ElementTypeName:" + rsMetaData.getElementTypeName(3));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    public void methodIsWrapperFor(CUBRIDResultSetMetaDataProxy rsMetaData) throws SQLException {
        // TODO:rsMetaData.isWrapperFor(iface);
    }

    public void methodUnwrap(CUBRIDResultSetMetaDataProxy rsMetaData) throws SQLException {
        // TODO:rsMetaData.unwrap(iface);
    }
}
