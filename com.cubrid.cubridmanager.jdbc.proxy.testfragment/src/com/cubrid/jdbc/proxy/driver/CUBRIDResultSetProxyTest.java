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
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Test CUBRIDResultSetProxy
 *
 * @author pangqiren
 * @version 1.0 - 2010-1-18 created by pangqiren
 */
@SuppressWarnings("deprecation")
public class CUBRIDResultSetProxyTest extends SetupEnv {

    private static final String testTableName = "testCUBRIDResultSetProxy";

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
                CUBRIDStatementProxy stmt =
                        (CUBRIDStatementProxy)
                                conn.createStatement(
                                        ResultSet.TYPE_SCROLL_SENSITIVE,
                                        ResultSet.CONCUR_UPDATABLE);
                String sql = "select * from \"" + testTableName + "\"";
                CUBRIDResultSetProxy rs = (CUBRIDResultSetProxy) stmt.executeQuery(sql);
                methodGetJdbcVersion(rs);
                methodSetJdbcVersion(rs);
                methodCUBRIDResultSetProxy(rs);
                methodAbsolute(rs);
                methodAfterLast(rs);
                methodBeforeFirst(rs);
                methodFindColumn(rs);
                methodFirst(rs);
                methodGetArrayInt(rs);
                methodGetArrayString(rs);
                methodGetAsciiStreamInt(rs);
                methodGetAsciiStreamString(rs);
                methodGetBigDecimalInt(rs);
                methodGetBigDecimalString(rs);
                methodGetBigDecimalIntInt(rs);
                methodGetBigDecimalStringInt(rs);
                methodGetBinaryStreamInt(rs);
                methodGetBinaryStreamString(rs);
                methodGetBlobInt(rs);
                methodGetBlobString(rs);
                methodGetBooleanInt(rs);
                methodGetBooleanString(rs);
                methodGetByteInt(rs);
                methodGetByteString(rs);
                methodGetBytesInt(rs);
                methodGetBytesString(rs);
                methodGetCharacterStreamInt(rs);
                methodGetCharacterStreamString(rs);
                methodGetClobInt(rs);
                methodGetClobString(rs);
                methodGetConcurrency(rs);
                methodGetCursorName(rs);
                methodGetDateInt(rs);
                methodGetDateString(rs);
                methodGetDateIntCalendar(rs);
                methodGetDateStringCalendar(rs);
                methodGetDoubleInt(rs);
                methodGetDoubleString(rs);
                methodGetFetchDirection(rs);
                methodGetFetchSize(rs);
                methodGetFloatInt(rs);
                methodGetFloatString(rs);
                methodGetIntInt(rs);
                methodGetIntString(rs);
                methodGetLongInt(rs);
                methodGetLongString(rs);
                methodGetMetaData(rs);
                methodGetObjectInt(rs);
                methodGetObjectString(rs);
                methodGetObjectIntMapOfStringClassOfQ(rs);
                methodGetObjectStringMapOfStringClassOfQ(rs);
                methodGetRefInt(rs);
                methodGetRefString(rs);
                methodGetRow(rs);
                methodGetShortInt(rs);
                methodGetShortString(rs);
                methodGetStatement(rs);
                methodGetStringInt(rs);
                methodGetStringString(rs);
                methodGetTimeInt(rs);
                methodGetTimeString(rs);
                methodGetTimeIntCalendar(rs);
                methodGetTimeStringCalendar(rs);
                methodGetTimestampInt(rs);
                methodGetTimestampString(rs);
                methodGetTimestampIntCalendar(rs);
                methodGetTimestampStringCalendar(rs);
                methodGetType(rs);
                methodGetURLInt(rs);
                methodGetURLString(rs);
                methodGetUnicodeStreamInt(rs);
                methodGetUnicodeStreamString(rs);
                methodGetWarnings(rs);
                methodInsertRow(rs);
                methodIsAfterLast(rs);

                methodIsBeforeFirst(rs);

                methodIsFirst(rs);

                methodIsLast(rs);

                methodLast(rs);

                methodMoveToCurrentRow(rs);

                methodMoveToInsertRow(rs);

                methodNext(rs);

                methodPrevious(rs);

                methodRefreshRow(rs);

                methodRelative(rs);

                methodRowDeleted(rs);

                methodRowInserted(rs);

                methodRowUpdated(rs);
                methodCancelRowUpdates(rs);
                methodClearWarnings(rs);
                methodDeleteRow(rs);
                methodSetFetchDirection(rs);

                methodSetFetchSize(rs);

                methodUpdateArrayIntArray(rs);

                methodUpdateArrayStringArray(rs);

                methodUpdateAsciiStreamIntInputStreamInt(rs);

                methodUpdateAsciiStreamStringInputStreamInt(rs);

                methodUpdateBigDecimalIntBigDecimal(rs);

                methodUpdateBigDecimalStringBigDecimal(rs);

                methodUpdateBinaryStreamIntInputStreamInt(rs);

                methodUpdateBinaryStreamStringInputStreamInt(rs);

                methodUpdateBlobIntBlob(rs);

                methodUpdateBlobStringBlob(rs);

                methodUpdateBooleanIntBoolean(rs);

                methodUpdateBooleanStringBoolean(rs);

                methodUpdateByteIntByte(rs);

                methodUpdateByteStringByte(rs);

                methodUpdateBytesIntByteArray(rs);

                methodUpdateBytesStringByteArray(rs);

                methodUpdateCharacterStreamIntReaderInt(rs);

                methodUpdateCharacterStreamStringReaderInt(rs);

                methodUpdateClobIntClob(rs);

                methodUpdateClobStringClob(rs);

                methodUpdateDateIntDate(rs);

                methodUpdateDateStringDate(rs);

                methodUpdateDoubleIntDouble(rs);

                methodUpdateDoubleStringDouble(rs);

                methodUpdateFloatIntFloat(rs);

                methodUpdateFloatStringFloat(rs);

                methodUpdateIntIntInt(rs);

                methodUpdateIntStringInt(rs);

                methodUpdateLongIntLong(rs);

                methodUpdateLongStringLong(rs);

                methodUpdateNullInt(rs);

                methodUpdateNullString(rs);

                methodUpdateObjectIntObject(rs);

                methodUpdateObjectStringObject(rs);

                methodUpdateObjectIntObjectInt(rs);

                methodUpdateObjectStringObjectInt(rs);

                methodUpdateRefIntRef(rs);

                methodUpdateRefStringRef(rs);

                methodUpdateRow(rs);

                methodUpdateShortIntShort(rs);

                methodUpdateShortStringShort(rs);

                methodUpdateStringIntString(rs);

                methodUpdateStringStringString(rs);

                methodUpdateTimeIntTime(rs);

                methodUpdateTimeStringTime(rs);

                methodUpdateTimestampIntTimestamp(rs);

                methodUpdateTimestampStringTimestamp(rs);

                methodWasNull(rs);

                methodGetCollectionInt(rs);

                methodGetCollectionString(rs);

                methodGetOID(rs);

                methodGetOIDInt(rs);

                methodGetOIDString(rs);

                methodGetServerHandle(rs);

                methodSetReturnable(rs);

                // new test cases for jdk1.6 jdbc driver
                methodGetHoldability(rs);

                methodGetNCharacterStream(rs);

                methodgetNCharacterStream2(rs);

                methodGetNClob(rs);

                methodGetNClob2(rs);

                methodGetNString(rs);

                methodGetNString2(rs);

                methodGetRowId(rs);

                methodGetRowId2(rs);

                methodGetSQLXML(rs);

                methodGetSQLXML2(rs);

                methodIsClosed(rs);

                methodUpdateAsciiStream(rs);

                methodUpdateAsciiStream2(rs);

                methodUpdateAsciiStream3(rs);

                methodUpdateAsciiStream4(rs);

                methodUpdateBinaryStream(rs);

                methodUpdateBinaryStream2(rs);

                methodUpdateBinaryStream3(rs);

                methodUpdateBinaryStream4(rs);

                methodUpdateBlob(rs);

                methodUpdateBlob2(rs);

                methodUpdateBlob3(rs);

                methodUpdateBlob4(rs);

                methodUpdateCharacterStream(rs);

                methodUpdateCharacterStream2(rs);

                methodUpdateCharacterStream3(rs);

                methodUpdateCharacterStream4(rs);

                methodUpdateClob(rs);

                methodUpdateClob2(rs);

                methodUpdateClob3(rs);

                methodUpdateClob4(rs);

                methodUpdateNCharacterStream(rs);

                methodUpdateNCharacterStream2(rs);

                methodUpdateNCharacterStream3(rs);

                methodUpdateNCharacterStream4(rs);

                methodUpdateNClob(rs);

                methodUpdateNClob2(rs);

                methodUpdateNClob3(rs);

                methodUpdateNClob4(rs);

                methodUpdateNClob5(rs);

                methodUpdateNClob6(rs);

                methodUpdateNString(rs);

                methodUpdateNString2(rs);

                methodUpdateRowId(rs);

                methodUpdateRowId2(rs);

                methodUpdateSQLXML(rs);

                methodUpdateSQLXML2(rs);

                methodIsWrapperFor(rs);

                methodUnwrap(rs);
                //
                methodClose(rs);

            } catch (Exception e) {
                e.printStackTrace();
                assertFalse(true);
            } finally {
                dropTestTable(conn);
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void createTestTable(Connection conn) {
        String sql =
                "CREATE TABLE \""
                        + testTableName
                        + "\"("
                        + "\"typeint\" integer,"
                        + "\"typesmallint\" smallint,"
                        + "\"typebigint\" bigint,"
                        + "\"typenumeric\" numeric(15,0),"
                        + "\"typefloat\" float,"
                        + "\"typedouble\" double,"
                        + "\"typechar\" character(1),"
                        + "\"typevarchar\" character varying(1073741823),"
                        + "\"typenchar\" national character(1),"
                        + "\"typenvarchar\" national character varying(536870911),"
                        + "\"typetime\" time,"
                        + "\"typetimestamp\" timestamp,"
                        + "\"typedate\" date,"
                        + "\"typedatetime\" datetime,"
                        + "\"typebit\" bit(1),"
                        + "\"typevbit\" bit varying(1073741823),"
                        + "\"typemonetary\" monetary,"
                        + "\"typestring\" string,"
                        + "\"typeset\" set_of(character varying(1073741823)),"
                        + "\"typemultiset\" multiset_of(character varying(1073741823)),"
                        + "\"typesequence\" sequence_of(character varying(1073741823)),"
                        + "\"typeblob\" blob,"
                        + "\"typeclob\" clob"
                        + ");";
        executeDDL(conn, sql);
        sql =
                "insert into\""
                        + testTableName
                        + "\" (\"typeint\", \"typesmallint\", \"typebigint\", \"typenumeric\", \"typefloat\", \"typedouble\", \"typechar\", \"typevarchar\", \"typenchar\", \"typenvarchar\", \"typetime\", \"typetimestamp\", \"typedate\", \"typedatetime\", \"typebit\", \"typevbit\", \"typemonetary\", \"typestring\", \"typeset\", \"typemultiset\", \"typesequence\") values (1, 123, 3456, 6789, 11.45, 12.345, 'c', 'I am varchar', N'n', N'I am nvarchar', TIME'10:49:00', TIMESTAMP'01/31/1994 08:15:00', DATE'11/11/1994', DATETIME'2009-10-10 12:00:00.000', B'0', X'110', $6985, 'I am string', {'set1','set2'}, {'multiset1','multiset2'}, {'seq1','seq2'},null,null)";
        for (int i = 0; i < 5; i++) {
            executeDDL(conn, sql);
        }
    }

    private boolean dropTestTable(Connection conn) {
        String sql = "drop table \"" + testTableName + "\"";
        return executeDDL(conn, sql);
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getServerVersion()}
     * .
     */
    public void methodGetJdbcVersion(CUBRIDResultSetProxy rs) {
        System.out.println("serverVersion:" + rs.getJdbcVersion());
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#setServerVersion(java.lang.String)} .
     */
    public void methodSetJdbcVersion(CUBRIDResultSetProxy rs) {
        rs.setJdbcVersion(rs.getJdbcVersion());
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#CUBRIDResultSetProxy(java.sql.ResultSet)} .
     */
    public void methodCUBRIDResultSetProxy(CUBRIDResultSetProxy rs) {
        new CUBRIDResultSetProxy(null);
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#absolute(int)}. */
    public void methodAbsolute(CUBRIDResultSetProxy rs) {
        try {
            rs.absolute(1);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#afterLast()}. */
    public void methodAfterLast(CUBRIDResultSetProxy rs) {
        try {
            rs.afterLast();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#beforeFirst()}. */
    public void methodBeforeFirst(CUBRIDResultSetProxy rs) {
        try {
            rs.beforeFirst();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#cancelRowUpdates()}
     * .
     */
    public void methodCancelRowUpdates(CUBRIDResultSetProxy rs) {
        try {
            rs.cancelRowUpdates();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#clearWarnings()} .
     */
    public void methodClearWarnings(CUBRIDResultSetProxy rs) {
        try {
            rs.clearWarnings();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#close()}. */
    public void methodClose(CUBRIDResultSetProxy rs) {
        try {
            rs.close();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#deleteRow()}. */
    public void methodDeleteRow(CUBRIDResultSetProxy rs) {
        try {
            rs.deleteRow();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#findColumn(java.lang.String)} .
     */
    public void methodFindColumn(CUBRIDResultSetProxy rs) {
        try {
            System.out.println("type int column index:" + rs.findColumn("typeint"));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#first()}. */
    public void methodFirst(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getArray(int)}. */
    public void methodGetArrayInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getArray(1);
        } catch (SQLException e) {
            assertFalse(true);
        } catch (UnsupportedOperationException e) {

        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getArray(java.lang.String)} .
     */
    public void methodGetArrayString(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getArray("typeint");
        } catch (SQLException e) {
            assertFalse(true);
        } catch (UnsupportedOperationException e) {

        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getAsciiStream(int)}
     * .
     */
    public void methodGetAsciiStreamInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getAsciiStream(8);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getAsciiStream(java.lang.String)} .
     */
    public void methodGetAsciiStreamString(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getAsciiStream("typevarchar");
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getBigDecimal(int)}
     * .
     */
    public void methodGetBigDecimalInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getBigDecimal(6);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getBigDecimal(java.lang.String)} .
     */
    public void methodGetBigDecimalString(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getBigDecimal("typedouble");
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getBigDecimal(int,
     * int)} .
     */
    public void methodGetBigDecimalIntInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getBigDecimal(6, 2);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        } catch (UnsupportedOperationException e1) {

        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getBigDecimal(java.lang.String, int)} .
     */
    public void methodGetBigDecimalStringInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getBigDecimal("typedouble", 2);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        } catch (UnsupportedOperationException e1) {

        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getBinaryStream(int)} .
     */
    public void methodGetBinaryStreamInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getBinaryStream(16);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getBinaryStream(java.lang.String)} .
     */
    public void methodGetBinaryStreamString(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getBinaryStream("typevbit");
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getBlob(int)}. */
    public void methodGetBlobInt(CUBRIDResultSetProxy rs) {
        // TODO
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getBlob(java.lang.String)} .
     */
    public void methodGetBlobString(CUBRIDResultSetProxy rs) {
        // TODO
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getBoolean(int)} .
     */
    public void methodGetBooleanInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getBoolean(15);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getBoolean(java.lang.String)} .
     */
    public void methodGetBooleanString(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getBoolean("typebit");
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getByte(int)}. */
    public void methodGetByteInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getByte(15);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getByte(java.lang.String)} .
     */
    public void methodGetByteString(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getByte("typebit");
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getBytes(int)}. */
    public void methodGetBytesInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getBytes(16);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getBytes(java.lang.String)} .
     */
    public void methodGetBytesString(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getBytes("typevbit");
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getCharacterStream(int)} .
     */
    public void methodGetCharacterStreamInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getCharacterStream(8);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getCharacterStream(java.lang.String)} .
     */
    public void methodGetCharacterStreamString(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getCharacterStream("typevarchar");
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getClob(int)}. */
    public void methodGetClobInt(CUBRIDResultSetProxy rs) {
        // TODO
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getClob(java.lang.String)} .
     */
    public void methodGetClobString(CUBRIDResultSetProxy rs) {
        // TODO
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getConcurrency()} .
     */
    public void methodGetConcurrency(CUBRIDResultSetProxy rs) {
        try {
            assertTrue(ResultSet.CONCUR_UPDATABLE == rs.getConcurrency());
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getCursorName()} .
     */
    public void methodGetCursorName(CUBRIDResultSetProxy rs) {
        try {
            System.out.println("Cursor name:" + rs.getCursorName());
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getDate(int)}. */
    public void methodGetDateInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getDate(13);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getDate(java.lang.String)} .
     */
    public void methodGetDateString(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getDate("typedate");
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getDate(int,
     * java.util.Calendar)} .
     */
    public void methodGetDateIntCalendar(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getDate(13, Calendar.getInstance());
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getDate(java.lang.String,
     * java.util.Calendar)} .
     */
    public void methodGetDateStringCalendar(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getDate("typedate", Calendar.getInstance());
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getDouble(int)}. */
    public void methodGetDoubleInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getDouble(6);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getDouble(java.lang.String)} .
     */
    public void methodGetDoubleString(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getDouble("typedouble");
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getFetchDirection()}
     * .
     */
    public void methodGetFetchDirection(CUBRIDResultSetProxy rs) {
        try {
            rs.getFetchDirection();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getFetchSize()}. */
    public void methodGetFetchSize(CUBRIDResultSetProxy rs) {
        try {
            rs.getFetchSize();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getFloat(int)}. */
    public void methodGetFloatInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getFloat(5);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getFloat(java.lang.String)} .
     */
    public void methodGetFloatString(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getFloat("typefloat");
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getInt(int)}. */
    public void methodGetIntInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getInt(1);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getInt(java.lang.String)} .
     */
    public void methodGetIntString(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getInt("typeint");
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getLong(int)}. */
    public void methodGetLongInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getLong(2);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getLong(java.lang.String)} .
     */
    public void methodGetLongString(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getLong("typesmallint");
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getMetaData()}. */
    public void methodGetMetaData(CUBRIDResultSetProxy rs) {
        try {
            rs.getMetaData();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getObject(int)}. */
    public void methodGetObjectInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getObject(8);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getObject(java.lang.String)} .
     */
    public void methodGetObjectString(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getObject("typevarchar");
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getObject(int,
     * java.util.Map)} .
     */
    public void methodGetObjectIntMapOfStringClassOfQ(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            Map<String, Class<?>> strMap = new HashMap<String, Class<?>>();
            strMap.put("varchar", String.class);
            rs.getObject(8, strMap);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        } catch (UnsupportedOperationException e1) {

        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getObject(java.lang.String, java.util.Map)}
     * .
     */
    public void methodGetObjectStringMapOfStringClassOfQ(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            Map<String, Class<?>> strMap = new HashMap<String, Class<?>>();
            strMap.put("varchar", String.class);
            rs.getObject("typevarchar", strMap);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        } catch (UnsupportedOperationException e1) {

        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getRef(int)}. */
    public void methodGetRefInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getRef(8);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        } catch (UnsupportedOperationException e1) {

        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getRef(java.lang.String)} .
     */
    public void methodGetRefString(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getRef("typevarchar");
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        } catch (UnsupportedOperationException e1) {

        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getRow()}. */
    public void methodGetRow(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getRow();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getShort(int)}. */
    public void methodGetShortInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getShort(1);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getShort(java.lang.String)} .
     */
    public void methodGetShortString(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getShort("typeint");
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getStatement()}. */
    public void methodGetStatement(CUBRIDResultSetProxy rs) {
        try {
            rs.getStatement();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getString(int)}. */
    public void methodGetStringInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getString(8);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getString(java.lang.String)} .
     */
    public void methodGetStringString(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getString("typevarchar");
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getTime(int)}. */
    public void methodGetTimeInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getTime(11);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getTime(java.lang.String)} .
     */
    public void methodGetTimeString(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getTime("typetime");
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getTime(int,
     * java.util.Calendar)} .
     */
    public void methodGetTimeIntCalendar(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getTime(11, Calendar.getInstance());
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getTime(java.lang.String,
     * java.util.Calendar)} .
     */
    public void methodGetTimeStringCalendar(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getTime("typetime", Calendar.getInstance());
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getTimestamp(int)} .
     */
    public void methodGetTimestampInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getTimestamp(12);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getTimestamp(java.lang.String)} .
     */
    public void methodGetTimestampString(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getTime("typetimestamp");
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getTimestamp(int,
     * java.util.Calendar)} .
     */
    public void methodGetTimestampIntCalendar(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getTimestamp(12, Calendar.getInstance());
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getTimestamp(java.lang.String,
     * java.util.Calendar)} .
     */
    public void methodGetTimestampStringCalendar(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getTimestamp("typetimestamp", Calendar.getInstance());
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getType()}. */
    public void methodGetType(CUBRIDResultSetProxy rs) {
        try {
            assertTrue(ResultSet.TYPE_SCROLL_SENSITIVE == rs.getType());
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getURL(int)}. */
    public void methodGetURLInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getURL(8);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getURL(java.lang.String)} .
     */
    public void methodGetURLString(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getURL("typevarchar");
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getUnicodeStream(int)} .
     */
    public void methodGetUnicodeStreamInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getUnicodeStream(8);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        } catch (UnsupportedOperationException e1) {

        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getUnicodeStream(java.lang.String)} .
     */
    public void methodGetUnicodeStreamString(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getUnicodeStream("typevarchar");
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        } catch (UnsupportedOperationException e1) {

        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getWarnings()}. */
    public void methodGetWarnings(CUBRIDResultSetProxy rs) {
        try {
            rs.getWarnings();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#insertRow()}. */
    public void methodInsertRow(CUBRIDResultSetProxy rs) {
        // TODO
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#isAfterLast()}. */
    public void methodIsAfterLast(CUBRIDResultSetProxy rs) {
        try {
            rs.isAfterLast();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#isBeforeFirst()} .
     */
    public void methodIsBeforeFirst(CUBRIDResultSetProxy rs) {
        try {
            rs.beforeFirst();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#isFirst()}. */
    public void methodIsFirst(CUBRIDResultSetProxy rs) {
        try {
            rs.isFirst();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#isLast()}. */
    public void methodIsLast(CUBRIDResultSetProxy rs) {
        try {
            rs.isLast();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#last()}. */
    public void methodLast(CUBRIDResultSetProxy rs) {
        try {
            rs.last();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#moveToCurrentRow()}
     * .
     */
    public void methodMoveToCurrentRow(CUBRIDResultSetProxy rs) {
        try {
            rs.moveToCurrentRow();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#moveToInsertRow()} .
     */
    public void methodMoveToInsertRow(CUBRIDResultSetProxy rs) {
        try {
            rs.moveToInsertRow();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#next()}. */
    public void methodNext(CUBRIDResultSetProxy rs) {
        try {
            rs.next();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#previous()}. */
    public void methodPrevious(CUBRIDResultSetProxy rs) {
        try {
            rs.previous();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#refreshRow()}. */
    public void methodRefreshRow(CUBRIDResultSetProxy rs) {
        try {
            rs.refreshRow();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#relative(int)}. */
    public void methodRelative(CUBRIDResultSetProxy rs) {
        try {
            rs.relative(0);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#rowDeleted()}. */
    public void methodRowDeleted(CUBRIDResultSetProxy rs) {
        try {
            rs.rowDeleted();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#rowInserted()}. */
    public void methodRowInserted(CUBRIDResultSetProxy rs) {
        try {
            rs.rowInserted();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#rowUpdated()}. */
    public void methodRowUpdated(CUBRIDResultSetProxy rs) {
        try {
            rs.rowUpdated();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#setFetchDirection(int)} .
     */
    public void methodSetFetchDirection(CUBRIDResultSetProxy rs) {
        try {
            rs.setFetchDirection(rs.getFetchDirection());
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#setFetchSize(int)} .
     */
    public void methodSetFetchSize(CUBRIDResultSetProxy rs) {
        try {
            rs.setFetchSize(rs.getFetchSize());
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateArray(int,
     * java.sql.Array)} .
     */
    public void methodUpdateArrayIntArray(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.updateArray(8, null);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateArray(java.lang.String,
     * java.sql.Array)} .
     */
    public void methodUpdateArrayStringArray(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.updateArray("typevarchar", null);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateAsciiStream(int, java.io.InputStream,
     * int)} .
     */
    public void methodUpdateAsciiStreamIntInputStreamInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            byte[] bArray = "nihao".getBytes();
            ByteArrayInputStream stream = new ByteArrayInputStream(bArray);
            rs.updateAsciiStream(8, stream, bArray.length);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateAsciiStream(java.lang.String,
     * java.io.InputStream, int)} .
     */
    public void methodUpdateAsciiStreamStringInputStreamInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            byte[] bArray = "nihao".getBytes();
            ByteArrayInputStream stream = new ByteArrayInputStream(bArray);
            rs.updateAsciiStream("typenvarchar", stream, bArray.length);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateBigDecimal(int,
     * java.math.BigDecimal)} .
     */
    public void methodUpdateBigDecimalIntBigDecimal(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.updateBigDecimal(3, new BigDecimal(123));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateBigDecimal(java.lang.String,
     * java.math.BigDecimal)} .
     */
    public void methodUpdateBigDecimalStringBigDecimal(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.updateBigDecimal("typebigint", new BigDecimal(123));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateBinaryStream(int,
     * java.io.InputStream, int)} .
     */
    public void methodUpdateBinaryStreamIntInputStreamInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            byte[] bArray = "nihao".getBytes();
            ByteArrayInputStream stream = new ByteArrayInputStream(bArray);
            rs.updateBinaryStream(8, stream, bArray.length);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateBinaryStream(java.lang.String,
     * java.io.InputStream, int)} .
     */
    public void methodUpdateBinaryStreamStringInputStreamInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            byte[] bArray = "nihao".getBytes();
            ByteArrayInputStream stream = new ByteArrayInputStream(bArray);
            rs.updateBinaryStream("typevarchar", stream, bArray.length);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateBlob(int,
     * java.sql.Blob)} .
     */
    public void methodUpdateBlobIntBlob(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getBlob(21);
            rs.updateBlob(21, rs.getBlob(21));
            assertTrue(true);
        } catch (SQLException e) {
            e.printStackTrace();
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateBlob(java.lang.String,
     * java.sql.Blob)} .
     */
    public void methodUpdateBlobStringBlob(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.updateBlob("typebigint", rs.getBlob("typebigint"));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateBoolean(int,
     * boolean)} .
     */
    public void methodUpdateBooleanIntBoolean(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.updateBoolean(15, true);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateBoolean(java.lang.String, boolean)} .
     */
    public void methodUpdateBooleanStringBoolean(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.updateBoolean("typebit", true);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateByte(int,
     * byte)} .
     */
    public void methodUpdateByteIntByte(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.updateByte(15, (byte) 0);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateByte(java.lang.String, byte)} .
     */
    public void methodUpdateByteStringByte(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.updateByte("typebit", (byte) 0);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateBytes(int,
     * byte[])} .
     */
    public void methodUpdateBytesIntByteArray(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            byte[] bArray = "1111".getBytes();
            rs.updateBytes(15, bArray);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateBytes(java.lang.String, byte[])} .
     */
    public void methodUpdateBytesStringByteArray(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            byte[] bArray = "1111".getBytes();
            rs.updateBytes("typebit", bArray);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateCharacterStream(int, java.io.Reader,
     * int)} .
     */
    public void methodUpdateCharacterStreamIntReaderInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            byte[] bArray = "1111".getBytes();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bArray)));
            rs.updateCharacterStream(8, reader, bArray.length);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateCharacterStream(java.lang.String,
     * java.io.Reader, int)} .
     */
    public void methodUpdateCharacterStreamStringReaderInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            byte[] bArray = "1111".getBytes();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bArray)));
            rs.updateCharacterStream("typevarchar", reader, bArray.length);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateClob(int,
     * java.sql.Clob)} .
     */
    public void methodUpdateClobIntClob(CUBRIDResultSetProxy rs) {
        // TODO
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateClob(java.lang.String,
     * java.sql.Clob)} .
     */
    public void methodUpdateClobStringClob(CUBRIDResultSetProxy rs) {
        // TODO
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateDate(int,
     * java.sql.Date)} .
     */
    public void methodUpdateDateIntDate(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.updateDate(13, new Date(System.currentTimeMillis()));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateDate(java.lang.String,
     * java.sql.Date)} .
     */
    public void methodUpdateDateStringDate(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.updateDate("typedate", new Date(System.currentTimeMillis()));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateDouble(int,
     * double)} .
     */
    public void methodUpdateDoubleIntDouble(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.updateDouble(6, 10.36);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateDouble(java.lang.String, double)} .
     */
    public void methodUpdateDoubleStringDouble(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.updateDouble("typedouble", 10.36);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateFloat(int,
     * float)} .
     */
    public void methodUpdateFloatIntFloat(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.updateFloat(5, (float) 10.36);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateFloat(java.lang.String, float)} .
     */
    public void methodUpdateFloatStringFloat(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.updateFloat("typefloat", (float) 10.36);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateInt(int, int)}
     * .
     */
    public void methodUpdateIntIntInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.updateInt(1, 123);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateInt(java.lang.String, int)} .
     */
    public void methodUpdateIntStringInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.updateInt("typeint", 123);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateLong(int,
     * long)} .
     */
    public void methodUpdateLongIntLong(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.updateLong(2, 123456);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateLong(java.lang.String, long)} .
     */
    public void methodUpdateLongStringLong(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.updateLong("typesmallint", 123456);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateNull(int)} .
     */
    public void methodUpdateNullInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.updateNull(8);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateNull(java.lang.String)} .
     */
    public void methodUpdateNullString(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.updateNull("typevarchar");
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateObject(int,
     * java.lang.Object)} .
     */
    public void methodUpdateObjectIntObject(CUBRIDResultSetProxy rs) {
        // TODO
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateObject(java.lang.String,
     * java.lang.Object)} .
     */
    public void methodUpdateObjectStringObject(CUBRIDResultSetProxy rs) {
        // TODO
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateObject(int,
     * java.lang.Object, int)} .
     */
    public void methodUpdateObjectIntObjectInt(CUBRIDResultSetProxy rs) {
        // TODO
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateObject(java.lang.String,
     * java.lang.Object, int)} .
     */
    public void methodUpdateObjectStringObjectInt(CUBRIDResultSetProxy rs) {
        // TODO
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateRef(int,
     * java.sql.Ref)} .
     */
    public void methodUpdateRefIntRef(CUBRIDResultSetProxy rs) {
        // TODO
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateRef(java.lang.String, java.sql.Ref)}
     * .
     */
    public void methodUpdateRefStringRef(CUBRIDResultSetProxy rs) {
        // TODO
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateRow()}. */
    public void methodUpdateRow(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.updateRow();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateShort(int,
     * short)} .
     */
    public void methodUpdateShortIntShort(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.updateShort(1, (short) 34);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateShort(java.lang.String, short)} .
     */
    public void methodUpdateShortStringShort(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.updateShort("typeint", (short) 34);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateString(int,
     * java.lang.String)} .
     */
    public void methodUpdateStringIntString(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.updateString(8, "I am string");
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateString(java.lang.String,
     * java.lang.String)} .
     */
    public void methodUpdateStringStringString(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.updateString("typevarchar", "I am string");
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateTime(int,
     * java.sql.Time)} .
     */
    public void methodUpdateTimeIntTime(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.updateTime(11, new Time(System.currentTimeMillis()));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateTime(java.lang.String,
     * java.sql.Time)} .
     */
    public void methodUpdateTimeStringTime(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.updateTime("typetime", new Time(System.currentTimeMillis()));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateTimestamp(int,
     * java.sql.Timestamp)} .
     */
    public void methodUpdateTimestampIntTimestamp(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.updateTimestamp(12, new Timestamp(System.currentTimeMillis()));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateTimestamp(java.lang.String,
     * java.sql.Timestamp)} .
     */
    public void methodUpdateTimestampStringTimestamp(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.updateTimestamp("typetimestamp", new Timestamp(System.currentTimeMillis()));
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#wasNull()}. */
    public void methodWasNull(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.wasNull();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getCollection(int)}
     * .
     */
    public void methodGetCollectionInt(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getCollection(19);
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getCollection(java.lang.String)} .
     */
    public void methodGetCollectionString(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getCollection("typeset");
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getOID()}. */
    public void methodGetOID(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getOID();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getOID(int)}. */
    public void methodGetOIDInt(CUBRIDResultSetProxy rs) {
        // TODO
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getOID(java.lang.String)} .
     */
    public void methodGetOIDString(CUBRIDResultSetProxy rs) {
        // TODO
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getServerHandle()} .
     */
    public void methodGetServerHandle(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.getServerHandle();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#setReturnable()} .
     */
    public void methodSetReturnable(CUBRIDResultSetProxy rs) {
        try {
            rs.first();
            rs.setReturnable();
            assertTrue(true);
        } catch (SQLException e) {
            assertFalse(true);
        }
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getHoldability()} .
     */
    public void methodGetHoldability(CUBRIDResultSetProxy rs) throws SQLException {
        rs.getHoldability();
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getNCharacterStream(String columnLabel)} .
     */
    public void methodGetNCharacterStream(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.getNCharacterStream(columnIndex);
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getNClob(String
     * columnLabel)} .
     */
    public void methodgetNCharacterStream2(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.getNCharacterStream(columnLabel);
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#setReturnable()} .
     */
    public void methodGetNClob(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.getNClob(columnIndex);
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getNString(int
     * columnIndex)} .
     */
    public void methodGetNClob2(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.getNClob(columnLabel);
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getNString(String
     * columnLabel)} .
     */
    public void methodGetNString(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.getNString(columnIndex);
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#setReturnable()} .
     */
    public void methodGetNString2(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.getNString(columnLabel);
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getRowId(int
     * columnIndex)} .
     */
    public void methodGetRowId(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.getRowId(columnIndex);
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getRowId(String
     * columnLabel)} .
     */
    public void methodGetRowId2(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.getRowId(columnLabel);
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getSQLXML(int
     * columnIndex)} .
     */
    public void methodGetSQLXML(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.getSQLXML(columnIndex);
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#getSQLXML(String
     * columnLabel)} .
     */
    public void methodGetSQLXML2(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.getSQLXML(columnLabel);
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#isClosed()} . */
    public void methodIsClosed(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.isClosed();
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateAsciiStream(int columnIndex,
     * InputStream x, long length)} .
     */
    public void methodUpdateAsciiStream(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateAsciiStream(columnIndex, x);
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateBinaryStream(int columnIndex,
     * InputStream x)} .
     */
    public void methodUpdateAsciiStream2(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateAsciiStream(columnLabel, x);
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateAsciiStream(String columnLabel,
     * InputStream x, long length)} .
     */
    public void methodUpdateAsciiStream3(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateAsciiStream(columnIndex, x, length);
    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateBinaryStream(int columnIndex,
     * InputStream x, long length)} .
     */
    public void methodUpdateAsciiStream4(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateAsciiStream(columnLabel, x, length);

    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateBinaryStream(String columnLabel,
     * InputStream x)} .
     */
    public void methodUpdateBinaryStream(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateBinaryStream(columnIndex, x);

    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateBinaryStream(String columnLabel,
     * InputStream x, long length)} .
     */
    public void methodUpdateBinaryStream2(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateBinaryStream(columnLabel, x);

    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#setReturnable()} .
     */
    public void methodUpdateBinaryStream3(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateBinaryStream(columnIndex, x, length);

    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateBlob(int
     * columnIndex, InputStream inputStream)} .
     */
    public void methodUpdateBinaryStream4(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateBinaryStream(columnLabel, x, length);

    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateBlob(String
     * columnLabel, InputStream inputStream)} .
     */
    public void methodUpdateBlob(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateBlob(columnIndex, inputStream);

    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateBlob(int
     * columnIndex, InputStream inputStream, long length)} .
     */
    public void methodUpdateBlob2(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateBlob(columnLabel, inputStream);
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateBlob(String
     * columnLabel, InputStream inputStream, long length)} .
     */
    public void methodUpdateBlob3(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateBlob(columnIndex, inputStream, length);

    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#setReturnable()} .
     */
    public void methodUpdateBlob4(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateBlob(columnLabel, inputStream, length);

    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateCharacterStream(int columnIndex,
     * Reader x)} .
     */
    public void methodUpdateCharacterStream(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateCharacterStream(columnIndex, x);

    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateCharacterStream(String columnLabel,
     * Reader reader)} .
     */
    public void methodUpdateCharacterStream2(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateCharacterStream(columnLabel, reader);

    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateCharacterStream(int columnIndex,
     * Reader x, long length)} .
     */
    public void methodUpdateCharacterStream3(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateCharacterStream(columnIndex, x, length);

    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateCharacterStream(String columnLabel,
     * Reader reader, long length)} .
     */
    public void methodUpdateCharacterStream4(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateCharacterStream(columnLabel, reader, length);

    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateClob(int
     * columnIndex, Reader reader)} .
     */
    public void methodUpdateClob(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateClob(columnIndex, reader);

    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateClob(String
     * columnLabel, Reader reader)} .
     */
    public void methodUpdateClob2(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateClob(columnLabel, reader);

    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateClob(int
     * columnIndex, Reader reader, long length)} .
     */
    public void methodUpdateClob3(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateClob(columnIndex, reader, length);
    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateClob(String
     * columnLabel, Reader reader, long length)} .
     */
    public void methodUpdateClob4(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateClob(columnLabel, reader, length);

    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateNCharacterStream(int columnIndex,
     * Reader x)} .
     */
    public void methodUpdateNCharacterStream(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateNCharacterStream(columnIndex, x);

    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateNCharacterStream(String columnLabel,
     * Reader reader)} .
     */
    public void methodUpdateNCharacterStream2(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateNCharacterStream(columnLabel, reader);

    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateNCharacterStream(int columnIndex,
     * Reader x, long length)} .
     */
    public void methodUpdateNCharacterStream3(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateNCharacterStream(columnIndex, x, length);

    }

    /**
     * Test method for {@link
     * com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateNCharacterStream(String columnLabel,
     * Reader reader, long length)} .
     */
    public void methodUpdateNCharacterStream4(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateNCharacterStream(columnLabel, reader, length);

    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateNClob(int
     * columnIndex, NClob nClob)} .
     */
    public void methodUpdateNClob(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:	rs.updateNClob(columnIndex, nClob);

    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateNClob(String
     * columnLabel, NClob nClob)} .
     */
    public void methodUpdateNClob2(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateNClob(columnLabel, nClob);

    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateNClob(int
     * columnIndex, Reader reader)} .
     */
    public void methodUpdateNClob3(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateNClob(columnIndex, reader);

    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateNClob(String
     * columnLabel, Reader reader)} .
     */
    public void methodUpdateNClob4(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateNClob(columnLabel, reader);

    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateNClob(int
     * columnIndex, Reader reader, long length)} .
     */
    public void methodUpdateNClob5(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateNClob(columnIndex, reader, length);

    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateNClob(String
     * columnLabel, Reader reader, long length)} .
     */
    public void methodUpdateNClob6(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateNClob(columnLabel, reader, length);

    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateNString(int
     * columnIndex, String nString)} .
     */
    public void methodUpdateNString(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateNString(columnIndex, nString);

    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateNString(String
     * columnLabel, String nString)} .
     */
    public void methodUpdateNString2(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateNString(columnLabel, nString);

    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateRowId(int
     * columnIndex, RowId x)} .
     */
    public void methodUpdateRowId(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateRowId(columnIndex, x);

    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateRowId(String
     * columnLabel, RowId x)} .
     */
    public void methodUpdateRowId2(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateRowId(columnLabel, x);

    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateSQLXML(int
     * columnIndex, SQLXML xmlObject)} .
     */
    public void methodUpdateSQLXML(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateSQLXML(columnIndex, xmlObject);

    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#updateSQLXML(String
     * columnLabel, SQLXML xmlObject)} .
     */
    public void methodUpdateSQLXML2(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.updateSQLXML(columnLabel, xmlObject);

    }

    /**
     * Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#isWrapperFor(Class)}
     * .
     */
    public void methodIsWrapperFor(CUBRIDResultSetProxy rs) throws SQLException {

        // TODO:rs.isWrapperFor(iface);
    }

    /** Test method for {@link com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy#unwrap(Class)} . */
    public void methodUnwrap(CUBRIDResultSetProxy rs) throws SQLException {
        // TODO:rs.unwrap(iface);
    }
}
