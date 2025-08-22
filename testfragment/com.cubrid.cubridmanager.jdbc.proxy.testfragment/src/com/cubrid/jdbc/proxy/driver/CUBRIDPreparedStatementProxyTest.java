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

// import java.sql.Array;
import com.cubrid.jdbc.proxy.ConnectionInfo;
import com.cubrid.jdbc.proxy.SetupEnv;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Properties;

/**
 * Test CUBRIDPreparedStatementProxy
 *
 * @author pangqiren
 * @version 1.0 - 2010-1-18 created by pangqiren
 */
@SuppressWarnings("deprecation")
public class CUBRIDPreparedStatementProxyTest extends SetupEnv {

    private static final String testTableName = "testCUBRIDPStmtProxy";
    private CUBRIDConnectionProxy conn = null;;

    public void test() {
        System.out.println("Test CUBRIDStatementProxy class:");
        for (ConnectionInfo connInfo : connInfoList) {
            System.out.println("Test connection url:" + connInfo.getConnectionUrl());
            try {
                CUBRIDDriverProxy driver = new CUBRIDDriverProxy(connInfo.getServerVersion());
                Properties props = new Properties();
                props.put("user", connInfo.getDbUser());
                props.put(
                        "password",
                        connInfo.getDbUserPass() == null ? "" : connInfo.getDbUserPass());
                conn = (CUBRIDConnectionProxy) driver.connect(connInfo.getConnectionUrl(), props);
                dropTestTable(conn);
                createTestTable(conn);
                String sql = "select * from " + testTableName;
                CUBRIDPreparedStatementProxy pstmt =
                        (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
                methodClearBatch(pstmt);

                methodExecuteBatch(pstmt);

                methodCUBRIDPreparedStatementProxy(pstmt);

                methodClearParameters(pstmt);

                methodExecute(pstmt);

                methodExecuteQuery(pstmt);

                methodExecuteUpdate(pstmt);

                methodGetMetaData(pstmt);

                methodGetParameterMetaData(pstmt);

                methodSetArray(pstmt);

                methodSetAsciiStreamIntInputStream(pstmt);

                methodSetAsciiStreamIntInputStreamInt(pstmt);

                methodSetAsciiStreamIntInputStreamLong(pstmt);

                methodSetBigDecimal(pstmt);

                methodSetBinaryStreamIntInputStream(pstmt);

                methodSetBinaryStreamIntInputStreamInt(pstmt);

                methodSetBlob(pstmt);

                methodSetBoolean(pstmt);

                methodSetByte(pstmt);

                methodSetBytes(pstmt);

                methodSetCharacterStream(pstmt);

                methodSetClob(pstmt);

                methodSetDateIntDate(pstmt);

                methodSetDateIntDateCalendar(pstmt);

                methodSetDouble(pstmt);

                methodSetFloat(pstmt);

                methodSetInt(pstmt);

                methodSetLong(pstmt);

                methodSetNullIntInt(pstmt);

                methodSetNullIntIntString(pstmt);

                methodSetObjectIntObject(pstmt);

                methodSetObjectIntObjectInt(pstmt);

                methodSetObjectIntObjectIntInt(pstmt);

                methodSetRef(pstmt);

                methodSetShort(pstmt);

                methodSetString(pstmt);

                methodSetTimeIntTime(pstmt);

                methodSetTimeIntTimeCalendar(pstmt);

                methodSetTimestampIntTimestamp(pstmt);

                methodSetTimestampIntTimestampCalendar(pstmt);

                methodSetURL(pstmt);

                methodSetUnicodeStream(pstmt);

                methodAddBatch(pstmt);

                methodExecuteInsert(pstmt);

                methodHasResultSet(pstmt);

                methodSetCollection(pstmt);

                methodSetOID(pstmt);

                // new test cases for jdk 1.6 jdbc drivers
                methodSetBinaryStream(pstmt);

                methodSetBlob1(pstmt);

                methodSetBlob2(pstmt);

                methodSetCharacterStream1(pstmt);

                methodSetCharacterStream2(pstmt);

                methodSetClob1(pstmt);

                methodSetClob2(pstmt);

                methodSetNCharacterStream(pstmt);

                methodSetNCharacterStream2(pstmt);

                methodSetNClob(pstmt);

                methodSetNClob2(pstmt);

                methodSetNClob3(pstmt);

                methodSetNString(pstmt);

                methodSetRowId(pstmt);

                methodSetSQLXML(pstmt);

                methodIsClosed(pstmt);

                methodIsPoolable(pstmt);

                methodSetPoolable(pstmt);

                methodIsWrapperFor(pstmt);

                methodUnwrap(pstmt);

                dropTestTable(conn);

                methodClose(pstmt);
            } catch (Exception e) {
                e.printStackTrace();
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
                        + "bonus numeric(15,2) NOT NULL, "
                        + "isman bit(1) NOT NULL,"
                        + "bytes set_of(bit(4)),"
                        + "dates date,"
                        + "intdata integer,"
                        + "doubleData double,"
                        + "longData bigint,"
                        + "url character varying(1073741823),"
                        + "setes set_of(character varying(20)),"
                        + "sequences sequence_of(character varying(20)),"
                        + "bit_varying bit varying(1073741823),"
                        + "blobs blob,"
                        + "clobs clob"
                        + ")";

        executeDDL(conn, sql);
        sql =
                "insert into \""
                        + testTableName
                        + "\" values('123','456',3.21,B'0',{X'2'},DATE'02/23/2009',"
                        + "300, 3.5, 1000,'abc',{'445','567'},{'445','56'},null,null,null)";
        executeDDL(conn, sql);
    }

    private boolean dropTestTable(Connection conn) {
        String sql = "drop table \"" + testTableName + "\"";
        return executeDDL(conn, sql);
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#clearBatch()} .
     */
    public void methodClearBatch(CUBRIDPreparedStatementProxy pstmt) {
        try {
            pstmt.clearBatch();
        } catch (SQLException e) {
            assertTrue(false);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#close()} .
     */
    public void methodClose(CUBRIDPreparedStatementProxy pstmt) {
        try {
            pstmt.close();
        } catch (SQLException e) {
            assertTrue(false);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#executeBatch()} .
     */
    public void methodExecuteBatch(CUBRIDPreparedStatementProxy pstmt) {
        try {
            int[] result = pstmt.executeBatch();
            System.out.println(result);
        } catch (SQLException e) {
            assertTrue(false);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#CUBRIDPreparedStatementProxy(java.sql.PreparedStatement)}
     * .
     */
    public void methodCUBRIDPreparedStatementProxy(CUBRIDPreparedStatementProxy pstmt) {
        assertTrue(new CUBRIDPreparedStatementProxy(pstmt) instanceof CUBRIDPreparedStatementProxy);
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#clearParameters()} .
     */
    public void methodClearParameters(CUBRIDPreparedStatementProxy pstmt) {
        try {
            pstmt.clearParameters();
        } catch (SQLException e) {
            assertTrue(false);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#execute()} .
     */
    public void methodExecute(CUBRIDPreparedStatementProxy pstmt) {
        try {
            pstmt.execute();
        } catch (SQLException e) {
            assertTrue(false);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#executeQuery()} .
     */
    public void methodExecuteQuery(CUBRIDPreparedStatementProxy pstmt) {
        try {
            pstmt.executeQuery();
        } catch (SQLException e) {
            assertTrue(false);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#executeUpdate()} .
     */
    public void methodExecuteUpdate(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "update " + testTableName + " set name='name'";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException e1) {
            e1.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#getMetaData()} .
     */
    public void methodGetMetaData(CUBRIDPreparedStatementProxy pstmt) {
        try {
            ResultSetMetaData rsmd = pstmt.getMetaData();
            assertTrue(rsmd != null);
            System.out.println(rsmd.getColumnCount());
        } catch (SQLException e) {
            assertTrue(false);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#getParameterMetaData()} .
     */
    public void methodGetParameterMetaData(CUBRIDPreparedStatementProxy pstmt) {
        try {
            pstmt.getParameterMetaData();
        } catch (UnsupportedOperationException e) {
            assertTrue(true);
        } catch (SQLException e1) {
            assertTrue(false);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setArray(int, java.sql.Array)} .
     */
    public void methodSetArray(CUBRIDPreparedStatementProxy pstmt) {
        Array array = null;
        try {
            array = conn.createArrayOf("ArrayMetaType", new Object[] {"a", "b"});
        } catch (SQLException e1) {
            assertTrue(false);
        }
        try {
            pstmt.setArray(1, array);
        } catch (SQLException e) {
            assertTrue(false);
        } catch (UnsupportedOperationException e1) {
            assertTrue(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setAsciiStream(int,
     * java.io.InputStream)} .
     */
    public void methodSetAsciiStreamIntInputStream(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "select * from " + testTableName + " where name = ?";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setAsciiStream(1, new StringBufferInputStream("string buffer input string"));
        } catch (SQLException e) {
            assertTrue(false);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setAsciiStream(int,
     * java.io.InputStream, int)} .
     */
    public void methodSetAsciiStreamIntInputStreamInt(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "select * from " + testTableName + " where name = ?";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setAsciiStream(1, new StringBufferInputStream("string buffer input string"), 3);
        } catch (SQLException e) {
            assertTrue(false);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setAsciiStream(int,
     * java.io.InputStream, long)} .
     */
    public void methodSetAsciiStreamIntInputStreamLong(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "select * from " + testTableName + " where name = ?";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setAsciiStream(
                    1, new StringBufferInputStream("string buffer input string for long"), 3L);
        } catch (SQLException e) {
            assertTrue(false);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setBigDecimal(int,
     * java.math.BigDecimal)} .
     */
    public void methodSetBigDecimal(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "select * from " + testTableName + " where bonus = ?";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setBigDecimal(1, new BigDecimal("100.111"));
        } catch (SQLException e) {
            assertTrue(false);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setBinaryStream(int,
     * java.io.InputStream)} .
     */
    public void methodSetBinaryStreamIntInputStream(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "select * from " + testTableName + " where name = ?";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setBinaryStream(5, new StringBufferInputStream("string buffer input stream"));
        } catch (SQLException e) {
            assertTrue(false);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setBinaryStream(int,
     * java.io.InputStream, int)} .
     */
    public void methodSetBinaryStreamIntInputStreamInt(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "select * from " + testTableName + " where name = ?";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setBinaryStream(1, new StringBufferInputStream("string buffer input stream"), 2);
        } catch (SQLException e) {
            assertTrue(false);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setBlob(int,
     * java.sql.Blob)} .
     */
    public void methodSetBlob(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "update " + testTableName + " set blobs=? ";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setBlob(1, conn.createBlob());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setBoolean(int, boolean)} .
     */
    public void methodSetBoolean(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "select * from " + testTableName + " where isman = ?";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setBoolean(1, true);
        } catch (SQLException e) {
            assertTrue(false);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setByte(int,
     * byte)} .
     */
    public void methodSetByte(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "select * from " + testTableName + " where isman = ?";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setByte(1, (byte) 1);
        } catch (SQLException e) {
            assertTrue(false);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setBytes(int, byte[])} .
     */
    public void methodSetBytes(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "select * from " + testTableName + " where bytes = ?";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setBytes(1, new byte[] {(byte) 4, (byte) 5});
        } catch (SQLException e) {
            assertTrue(false);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setCharacterStream(int,
     * java.io.Reader, int)} .
     */
    public void methodSetCharacterStream(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "select * from " + testTableName + " where name = ?";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setCharacterStream(1, new StringReader("string reader"), 2);
        } catch (SQLException e) {
            assertTrue(false);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setClob(int,
     * java.sql.Clob)} .
     */
    public void methodSetClob(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "update " + testTableName + " set clobs=? ";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setClob(1, conn.createClob());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setDate(int,
     * java.sql.Date)} .
     */
    public void methodSetDateIntDate(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "select * from " + testTableName + " where dates = ?";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setDate(1, new Date(0));
        } catch (SQLException e) {
            assertTrue(false);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setDate(int,
     * java.sql.Date, java.util.Calendar)} .
     */
    public void methodSetDateIntDateCalendar(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "select * from " + testTableName + " where dates = ?";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setDate(1, new Date(0), Calendar.getInstance());
        } catch (SQLException e) {
            assertTrue(false);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setDouble(int, double)} .
     */
    public void methodSetDouble(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "select * from " + testTableName + " where doubleData = ?";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setDouble(1, 13d);
        } catch (SQLException e) {
            assertTrue(false);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setFloat(int, float)} .
     */
    public void methodSetFloat(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "select * from " + testTableName + " where doubleData = ?";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setFloat(1, 343f);
        } catch (SQLException e) {
            assertTrue(false);
        } finally {
            if (ps != null)
                try {
                    ps.close();
                } catch (SQLException e) {
                }
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setInt(int,
     * int)} .
     */
    public void methodSetInt(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "select * from " + testTableName + " where intData = ?";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setInt(1, 343);
        } catch (SQLException e) {
            assertTrue(false);
        } finally {
            if (ps != null)
                try {
                    ps.close();
                } catch (SQLException e) {
                }
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setLong(int,
     * long)} .
     */
    public void methodSetLong(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "select * from " + testTableName + " where longData = ?";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setLong(1, 343L);
        } catch (SQLException e) {
            assertTrue(false);
        } finally {
            if (ps != null)
                try {
                    ps.close();
                } catch (SQLException e) {
                }
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setNull(int,
     * int)} .
     */
    public void methodSetNullIntInt(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "select * from " + testTableName + " where longData = ?";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setNull(1, Types.LONGVARCHAR);
        } catch (SQLException e) {
            assertTrue(false);
        } finally {
            if (ps != null)
                try {
                    ps.close();
                } catch (SQLException e) {
                }
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setNull(int,
     * int, java.lang.String)} .
     */
    public void methodSetNullIntIntString(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "select * from " + testTableName + " where longData = ?";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setNull(1, Types.LONGVARCHAR, "long null");
        } catch (SQLException e) {
            assertTrue(false);
        } finally {
            if (ps != null)
                try {
                    ps.close();
                } catch (SQLException e) {
                }
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setObject(int, java.lang.Object)} .
     */
    public void methodSetObjectIntObject(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "select * from " + testTableName + " where longData = ?";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setObject(1, new Object());
        } catch (SQLException e) {
            assertTrue(false);
        } catch (IllegalArgumentException e1) {
            assertTrue(true);
        } finally {
            if (ps != null)
                try {
                    ps.close();
                } catch (SQLException e) {
                }
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setObject(int, java.lang.Object,
     * int)} .
     */
    public void methodSetObjectIntObjectInt(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "select * from " + testTableName + " where longData = ?";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setObject(1, new Object(), 3);
        } catch (SQLException e) {
            assertTrue(false);
        } catch (IllegalArgumentException e1) {
            assertTrue(true);
        } finally {
            if (ps != null)
                try {
                    ps.close();
                } catch (SQLException e) {
                }
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setObject(int, java.lang.Object,
     * int, int)} .
     */
    public void methodSetObjectIntObjectIntInt(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "select * from " + testTableName + " where bonus = ?";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setObject(1, new Double(15.2), Types.DECIMAL, 2);
        } catch (SQLException e) {
            assertTrue(false);
        } catch (IllegalArgumentException e1) {
            assertTrue(true);
        } finally {
            if (ps != null)
                try {
                    ps.close();
                } catch (SQLException e) {
                }
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setRef(int,
     * java.sql.Ref)} .
     */
    public void methodSetRef(CUBRIDPreparedStatementProxy pstmt) {
        ResultSet rs;
        try {
            rs = pstmt.getResultSet();
            Ref ref = rs.getRef(1);
            pstmt.setRef(1, ref);
        } catch (SQLException e) {
            assertTrue(false);
        } catch (UnsupportedOperationException e1) {
            assertTrue(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setShort(int, short)} .
     */
    public void methodSetShort(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "select * from " + testTableName + " where intData = ?";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setShort(1, (short) 3);
        } catch (SQLException e) {
            assertTrue(false);
        } finally {
            if (ps != null)
                try {
                    ps.close();
                } catch (SQLException e) {
                }
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setString(int, java.lang.String)} .
     */
    public void methodSetString(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "select * from " + testTableName + " where name = ?";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setString(1, "a string");
        } catch (SQLException e) {
            assertTrue(false);
        } finally {
            if (ps != null)
                try {
                    ps.close();
                } catch (SQLException e) {
                }
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setTime(int,
     * java.sql.Time)} .
     */
    public void methodSetTimeIntTime(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "select * from " + testTableName + " where dates = ?";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setTime(1, new Time(0));
        } catch (SQLException e) {
            assertTrue(false);
        } finally {
            if (ps != null)
                try {
                    ps.close();
                } catch (SQLException e) {
                }
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setTime(int,
     * java.sql.Time, java.util.Calendar)} .
     */
    public void methodSetTimeIntTimeCalendar(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "select * from " + testTableName + " where dates = ?";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setTime(1, new Time(0), Calendar.getInstance());
        } catch (SQLException e) {
            assertTrue(false);
        } finally {
            if (ps != null)
                try {
                    ps.close();
                } catch (SQLException e) {
                }
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setTimestamp(int,
     * java.sql.Timestamp)} .
     */
    public void methodSetTimestampIntTimestamp(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "select * from " + testTableName + " where dates = ?";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setTimestamp(1, new Timestamp(0));
        } catch (SQLException e) {
            assertTrue(false);
        } finally {
            if (ps != null)
                try {
                    ps.close();
                } catch (SQLException e) {
                }
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setTimestamp(int,
     * java.sql.Timestamp, java.util.Calendar)} .
     */
    public void methodSetTimestampIntTimestampCalendar(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "select * from " + testTableName + " where dates = ?";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setTimestamp(1, new Timestamp(0), Calendar.getInstance());
        } catch (SQLException e) {
            assertTrue(false);
        } finally {
            if (ps != null)
                try {
                    ps.close();
                } catch (SQLException e) {
                }
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setURL(int,
     * java.net.URL)} .
     */
    public void methodSetURL(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "select * from " + testTableName + " where url = ?";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setURL(1, new URL("http://127.0.0.1"));
        } catch (SQLException e) {
            assertTrue(false);
        } catch (MalformedURLException e) {
            assertTrue(false);
        } catch (UnsupportedOperationException e1) {
            assertTrue(true);
        } finally {
            if (ps != null)
                try {
                    ps.close();
                } catch (SQLException e) {
                }
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setUnicodeStream(int,
     * java.io.InputStream, int)} .
     */
    public void methodSetUnicodeStream(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "select * from " + testTableName + " where url = ?";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setUnicodeStream(30, new StringBufferInputStream("string buffer input stream"), 3);
        } catch (SQLException e) {
            assertTrue(false);
        } catch (UnsupportedOperationException e1) {
            assertTrue(true);
        } finally {
            if (ps != null)
                try {
                    ps.close();
                } catch (SQLException e) {
                }
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#addBatch()}
     * .
     */
    public void methodAddBatch(CUBRIDPreparedStatementProxy pstmt) {
        try {
            pstmt.addBatch();
        } catch (SQLException e) {
            assertTrue(false);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#executeInsert()} .
     */
    public void methodExecuteInsert(CUBRIDPreparedStatementProxy pstmt) {
        String sql =
                "insert into \""
                        + testTableName
                        + "\" values('789','456',4.21,B'0',{X'2'},DATE'02/23/2009',"
                        + "400, 3.5, 1000,'abc',{'556','678'},{'556','78'},null,null,null)";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.executeInsert();
        } catch (SQLException e) {
            assertTrue(false);
        } finally {
            if (ps != null)
                try {
                    ps.close();
                } catch (SQLException e) {
                }
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#hasResultSet()} .
     */
    public void methodHasResultSet(CUBRIDPreparedStatementProxy pstmt) {
        try {
            System.out.println(pstmt.hasResultSet());
        } catch (SQLException e) {
            assertTrue(false);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setCollection(int,
     * java.lang.Object[])} .
     */
    public void methodSetCollection(CUBRIDPreparedStatementProxy pstmt) {
        String sql = "select * from " + testTableName + " where setes = ?";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setCollection(1, new String[] {"a"});
        } catch (SQLException e) {
            assertTrue(false);
        } finally {
            if (ps != null)
                try {
                    ps.close();
                } catch (SQLException e) {
                }
        }
    }

    /**
     * Need test forward
     *
     * <p>Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setOID(int,
     * com.cubrid.jdbc.proxy.driver.CUBRIDOIDProxy)} .
     */
    public void methodSetOID(CUBRIDPreparedStatementProxy pstmt) {
        CUBRIDOIDProxy oidProxy = null;
        try {
            oidProxy = CUBRIDOIDProxy.getNewInstance(conn, getOid(conn));
        } catch (SQLException e2) {
        }
        try {
            oidProxy.setValues(new String[] {"id", "name"}, new String[] {"456", "789"});
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        String sql = "select * from " + testTableName + " where setes = ?";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setOID(1, oidProxy);
        } catch (SQLException e) {
            assertTrue(false);
        } finally {
            if (ps != null)
                try {
                    ps.close();
                } catch (SQLException e) {
                }
        }
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
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setBinaryStream(int parameterIndex,
     * InputStream stream, int length)} .
     */
    public void methodSetBinaryStream(CUBRIDPreparedStatementProxy pstmt) throws SQLException {
        String sql = "update " + testTableName + " set bit_varying=? ";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setBinaryStream(1, new ByteArrayInputStream(new byte[] {0, 1}), 1);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy#setBinaryStream(int parameterIndex,
     * InputStream inputStream)} .
     */
    public void methodSetBlob1(CUBRIDPreparedStatementProxy pstmt) throws SQLException {
        String sql = "update " + testTableName + " set blobs=? ";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setBlob(1, new ByteArrayInputStream(new byte[] {0}));
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void methodSetBlob2(CUBRIDPreparedStatementProxy pstmt) throws SQLException {
        String sql = "update " + testTableName + " set blobs=? ";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setBlob(1, new ByteArrayInputStream(new byte[] {0}), 1);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void methodSetCharacterStream1(CUBRIDPreparedStatementProxy pstmt) throws SQLException {
        // TODO:pstmt.setCharacterStream(parameterIndex, reader)
    }

    public void methodSetCharacterStream2(CUBRIDPreparedStatementProxy pstmt) throws SQLException {
        // TODO:pstmt.setCharacterStream(parameterIndex, reader, length);
    }

    public void methodSetClob1(CUBRIDPreparedStatementProxy pstmt) throws SQLException {
        String sql = "update " + testTableName + " set clobs=? ";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setClob(1, new StringReader("test"));
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            assertTrue(false);
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        }
    }

    public void methodSetClob2(CUBRIDPreparedStatementProxy pstmt) throws SQLException {
        String sql = "update " + testTableName + " set clobs=? ";
        CUBRIDPreparedStatementProxy ps = null;
        try {
            ps = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            ps.setClob(1, new StringReader("test"), 4);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            assertTrue(false);
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        }
    }

    public void methodSetNCharacterStream(CUBRIDPreparedStatementProxy pstmt) throws SQLException {
        // TODO:pstmt.setNCharacterStream(parameterIndex, value);
    }

    public void methodSetNCharacterStream2(CUBRIDPreparedStatementProxy pstmt) throws SQLException {
        // TODO:pstmt.setNCharacterStream(parameterIndex, value, length);
    }

    public void methodSetNClob(CUBRIDPreparedStatementProxy pstmt) throws SQLException {
        // TODO:pstmt.setNClob(parameterIndex, value);

    }

    public void methodSetNClob2(CUBRIDPreparedStatementProxy pstmt) throws SQLException {
        // TODO:pstmt.setNClob(parameterIndex, reader);
    }

    public void methodSetNClob3(CUBRIDPreparedStatementProxy pstmt) throws SQLException {
        // TODO:pstmt.setNClob(parameterIndex, reader, length);
    }

    public void methodSetNString(CUBRIDPreparedStatementProxy pstmt) throws SQLException {
        // TODO:pstmt.setNString(parameterIndex, value);
    }

    public void methodSetRowId(CUBRIDPreparedStatementProxy pstmt) throws SQLException {
        // TODO:pstmt.setRowId(parameterIndex, x);
    }

    public void methodSetSQLXML(CUBRIDPreparedStatementProxy pstmt) throws SQLException {
        // TODO:pstmt.setSQLXML(parameterIndex, xmlObject);
    }

    public void methodIsClosed(CUBRIDPreparedStatementProxy pstmt) throws SQLException {
        // TODO:pstmt.isClosed();
    }

    public void methodIsPoolable(CUBRIDPreparedStatementProxy pstmt) throws SQLException {
        // TODO:pstmt.isPoolable();
    }

    public void methodSetPoolable(CUBRIDPreparedStatementProxy pstmt) throws SQLException {
        // TODO:pstmt.setPoolable(poolable);
    }

    public void methodIsWrapperFor(CUBRIDPreparedStatementProxy pstmt) throws SQLException {
        // TODO:pstmt.isWrapperFor(iface);
    }

    public void methodUnwrap(CUBRIDPreparedStatementProxy pstmt) throws SQLException {
        // TODO:pstmt.unwrap(iface);
    }
}
