package com.cubrid.cubridmanager.core.cubrid.table.model;

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
import com.cubrid.cubridmanager.core.SetupJDBCTestCase;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

/**
 * test DataType
 *
 * @author wuyingshi
 * @version 1.0 - 2010-1-4 created by wuyingshi
 */
public class DataTypeTest extends SetupJDBCTestCase {

    String testTableName = "supertabletest";
    String testSuperTableName = "SuperTable";
    String sql = null;

    /** test DataType */
    private boolean createTestSuperTable() {
        String sql =
                "create table \""
                        + testSuperTableName
                        + "\"("
                        + "code integer,"
                        + "name character varying(40) NOT NULL,"
                        + "gender character(1),"
                        + "nation_code character(3)"
                        + ")";
        ;
        return executeDDL(sql);
    }

    private boolean createTestTable() {
        String sql =
                "create table \""
                        + testTableName
                        + "\" under \""
                        + testSuperTableName
                        + "\"("
                        + "a integer DEFAULT 1,"
                        + "b character varying(1073741823),"
                        + "aaa set_of(integer) DEFAULT {1}"
                        + ")"
                        + " INHERIT "
                        + "gender OF "
                        + testSuperTableName
                        + ","
                        + "code OF "
                        + testSuperTableName
                        + ","
                        + "name OF "
                        + testSuperTableName
                        + ","
                        + "nation_code OF "
                        + testSuperTableName
                        + ";";
        ;
        return executeDDL(sql);
    }

    private boolean dropTestTable() {
        String sql = "drop table \"" + testTableName + "\"";
        return executeDDL(sql);
    }

    private boolean dropTestSuperTable() {
        String sql = "drop table \"" + testSuperTableName + "\"";
        return executeDDL(sql);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        createTestSuperTable();
        createTestTable();
    }

    public void testNumeric() {
        String numeric38Max = DataType.getNumericMaxValue(38);
        String numeric38Min = DataType.getNumericMinValue(38);
        assertEquals(38, numeric38Max.length());
        assertEquals(39, numeric38Min.length());
        BigInteger bigInteger38Max = new BigInteger(numeric38Max);
        BigInteger bigInteger38Min = new BigInteger(numeric38Min);

        assertEquals(1, bigInteger38Max.compareTo(bigInteger38Min));
        System.out.println(bigInteger38Max.subtract(bigInteger38Min));
        System.out.println(bigInteger38Max.add(bigInteger38Min));
    }

    public void testGetCollectionValues() throws NumberFormatException, ParseException {
        String type = null;
        String value = null;
        Object[] values = null;
        Object[] expected = null;

        value = "{1,2}";
        type = "set_of(char(10))";
        expected = new String[] {"1", "2"};
        values = DataType.getCollectionValues(type, value, true);
        assertTrue(Arrays.equals(values, expected));

        value = "1,2";
        type = "set_of(integer)";
        expected = new Integer[] {1, 2};
        values = DataType.getCollectionValues(type, value, true);
        assertTrue(Arrays.equals(values, expected));

        value = "1";
        type = "set_of(char(10))";
        expected = new String[] {"1"};
        values = DataType.getCollectionValues(type, value, true);
        assertTrue(Arrays.equals(values, expected));

        value = "{1}";
        type = "set_of(integer)";
        expected = new Integer[] {1};
        values = DataType.getCollectionValues(type, value, true);
        assertTrue(Arrays.equals(values, expected));

        value = "1";
        type = "INTEGER";
        DataType.isBasicType(type);
        DataType.isBasicType("integer");
        DataType.isBasicType("INTEGER(8)");
        DataType.makeType("INTEGER", null, 1, 2);
        DataType.makeType("BIGINT", null, 1, 2);
        DataType.makeType("FLOAT", null, 1, 2);
        DataType.makeType("DOUBLE", null, 1, 2);
        DataType.makeType("TIME", null, 1, 2);
        DataType.makeType("DATE", null, 1, 2);
        DataType.makeType("TIMESTAMP", null, 1, 2);
        DataType.makeType("DATETIME", null, 1, 2);
        DataType.makeType("CHAR", null, 1, 2);
        DataType.makeType("VARCHAR", null, 1, 2);
        DataType.makeType("NCHAR", null, 1, 2);
        DataType.makeType("NCHAR VARYING", null, 1, 2);
        DataType.makeType("BIT", null, 1, 2);
        DataType.makeType("BIT VARYING", null, 1, 2);
        DataType.makeType("NUMERIC", null, 1, 2);
        DataType.makeType("SET", null, 1, 2);
        DataType.makeType("MULTISET", null, 1, 2);
        DataType.makeType("SEQUENCE", null, 1, 2);

        String jdbcType = "jdbcType";
        DataType.getElemType(jdbcType);
        DataType.getElemType("SET(VARCHAR(20), INTEGER)");
        DataType.getElemType("MULTISET(CHAR(5))");
        DataType.getElemType("SEQUENCE(INTEGER)");
        DataType.getScale(jdbcType);
        DataType.getScale("SET(VARCHAR(20))");
        DataType.getScale("MULTISET(CHAR(5))");
        DataType.getScale("SEQUENCE(INTEGER(2,12)");
        DataType.getSize(jdbcType);
        DataType.getSize("SET(VARCHAR(20))");
        DataType.getSize("MULTISET(CHAR(5))");
        DataType.getSize("SEQUENCE(INTEGER(2,12)");
        DataType.getSuperClasses(databaseInfo, "supertabletest");
        // DataType.isCompatibleType(databaseInfo, "INTEGER", "INTEGER");
        //		DataType.isCompatibleType(databaseInfo, "INTEGER", "BIGINT");
        //		DataType.isCompatibleType(databaseInfo, "INTEGER", "SET(INTEGER)");
        //		DataType.isCompatibleType(databaseInfo, "SET(INTEGER)", "SET(BIGINT)");
        DataType.getTypePart(type);
        DataType.getTypeRemain(jdbcType);
        DataType.getShownType(jdbcType);
        DataType.getShownType("integer");
        DataType.getShownType("SET(VARCHAR(20))");
        DataType.getShownType("set_of(integer)");
        DataType.getShownType("character(20)");
        DataType.getRealType("shownType");
        DataType.getRealType("(shownType)");
        DataType.getRealType("INTEGER");
        DataType.getRealType("integer");
        DataType.getRealType("SET(VARCHAR(20))");
        DataType.getRealType("set_of(integer)");
        DataType.getRealType("character(20)");
        //		DataType.getTypeMapping(serverInfo, true, true);
        //		DataType.getTypeMapping(serverInfo, true, false);
        //		DataType.getTypeMapping(serverInfo, false, true);
        //		DataType.getTypeMapping(serverInfo, false, false);
        DataType.getCollectionValues("set_of(integer)", "{10, 20, 80}", true);
        DataType.getCollectionValues("set_of(bigint)", "{20263,22222}", true);
        DataType.getCollectionValues("set_of(numeric(20,0))", "{20263,67}", true);
        DataType.getCollectionValues("set_of(numeric(20,4))", "{20263,67}", true);
        DataType.getCollectionValues("set_of(float)", "{20263,67}", true);
        DataType.getCollectionValues("set_of(double)", "{20263,67}", true);
        DataType.getCollectionValues("set_of(string)", "{20263,67}", true);
        DataType.getCollectionValues("set_of(time)", "{1:15:00,1:16:00}", true);
        DataType.getCollectionValues("set_of(date)", "{1994-11-11,1994-12-11}", true);
        DataType.getCollectionValues(
                "set_of(timestamp)", "{1994-11-11 1:15:00,1994-11-11 1:16:00}", true);
        DataType.getCollectionValues(
                "set_of(datetime)", "{1994-11-11 1:15:00,1994-11-11 1:16:00}", true);
        DataType.getCollectionValues("set_of(bit(8))", "{B'0001',B'0002'}", true);

        assertTrue(Arrays.equals(values, expected));
    }

    public void testGetCollectionValues2() throws NumberFormatException, ParseException {
        String type = null;
        Object[] value = null;
        Object[] values = null;
        Object[] expected = null;

        value = new String[] {"1", "2"};
        type = "set_of(char(10))";
        expected = new String[] {"1", "2"};
        values = DataType.getCollectionValues(type, value, true);
        assertTrue(Arrays.equals(values, expected));

        value = new Integer[] {1, 2};
        type = "set_of(integer)";
        expected = new Integer[] {1, 2};
        values = DataType.getCollectionValues(type, value, true);
        assertTrue(Arrays.equals(values, expected));

        value = new String[] {"1"};
        type = "set_of(char(10))";
        expected = new String[] {"1"};
        values = DataType.getCollectionValues(type, value, true);
        assertTrue(Arrays.equals(values, expected));

        value = new Integer[] {1};
        type = "set_of(integer)";
        expected = new Integer[] {1};
        values = DataType.getCollectionValues(type, value, true);
        assertTrue(Arrays.equals(values, expected));

        values =
                DataType.getCollectionValues(
                        "set_of(integer)", new Object[] {"10", "20", "80"}, true);
        expected = new Integer[] {10, 20, 80};
        assertTrue(Arrays.equals(values, expected));

        values = DataType.getCollectionValues("set_of(bigint)", new Object[] {20263, 22222}, true);
        expected = new Long[] {20263L, 22222L};
        assertTrue(Arrays.equals(values, expected));

        values =
                DataType.getCollectionValues(
                        "set_of(numeric(20,0))", new Object[] {20263, 67}, true);
        expected = new Long[] {20263L, 67L};
        assertTrue(Arrays.equals(values, expected));

        values =
                DataType.getCollectionValues(
                        "set_of(numeric(20,4))", new Object[] {20263, 67}, true);
        expected = new Double[] {(double) 20263, (double) 67};
        assertTrue(Arrays.equals(values, expected));

        values = DataType.getCollectionValues("set_of(float)", new Object[] {20263, 67}, true);
        expected = new Double[] {(double) 20263, (double) 67};
        assertTrue(Arrays.equals(values, expected));

        values = DataType.getCollectionValues("set_of(double)", new Object[] {20263, 67}, true);
        expected = new Double[] {(double) 20263, (double) 67};
        assertTrue(Arrays.equals(values, expected));

        values = DataType.getCollectionValues("set_of(string)", new Object[] {"20263", "67"}, true);
        expected = new String[] {"20263", "67"};
        assertTrue(Arrays.equals(values, expected));

        values =
                DataType.getCollectionValues(
                        "set_of(time)", new Object[] {"1:15:00", "1:16:00"}, true);
        expected = new Time[] {Time.valueOf("1:15:00"), Time.valueOf("1:16:00")};
        assertTrue(Arrays.equals(values, expected));

        values =
                DataType.getCollectionValues(
                        "set_of(date)", new Object[] {"1994-11-11", "1994-12-11"}, true);
        expected = new Date[] {Date.valueOf("1994-11-11"), Date.valueOf("1994-12-11")};
        assertTrue(Arrays.equals(values, expected));

        values =
                DataType.getCollectionValues(
                        "set_of(timestamp)",
                        new Object[] {"1994-11-11 11:15:00", "1994-11-11 11:16:00"},
                        true);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        long time1 = df.parse("1994-11-11 11:15:00").getTime();
        java.sql.Timestamp timestamp1 = new java.sql.Timestamp(time1);

        long time2 = df.parse("1994-11-11 11:16:00").getTime();
        java.sql.Timestamp timestamp2 = new java.sql.Timestamp(time2);

        expected = new java.sql.Timestamp[] {timestamp1, timestamp2};
        assertTrue(Arrays.equals(values, expected));

        values =
                DataType.getCollectionValues(
                        "set_of(datetime)",
                        new Object[] {"1994-11-11 11:15:00", "1994-11-11 11:16:00"},
                        true);
        expected = new java.sql.Timestamp[] {timestamp1, timestamp2};
        assertTrue(Arrays.equals(values, expected));

        values =
                DataType.getCollectionValues(
                        "set_of(bit(8))", new Object[] {"B'0001'", "B'0002'"}, true);
        expected = new String[] {"B'0001'", "B'0002'"};
        assertTrue(Arrays.equals(values, expected));
    }

    public void testIsNullValueForImport() {
        DataType.setNULLValuesForImport(new String[] {"NULL", "(NULL)", "\\N"});
        boolean result = DataType.isNullValueForImport("integer", "null");
        assertTrue(result);
        result = DataType.isNullValueForImport("integer", "(null)");
        assertTrue(result);
        result = DataType.isNullValueForImport("integer", "NULL");
        assertTrue(result);
        result = DataType.isNullValueForImport("integer", "(NULL)");
        assertTrue(result);
        result = DataType.isNullValueForImport("BLOB", "null");
        assertTrue(result);
        result = DataType.isNullValueForImport("BLOB", "(null)");
        assertTrue(result);
        result = DataType.isNullValueForImport("BLOB", "(BLOB)");
        assertTrue(result);
        result = DataType.isNullValueForImport("CLOB", "null");
        assertTrue(result);
        result = DataType.isNullValueForImport("CLOB", "(null)");
        assertTrue(result);
        result = DataType.isNullValueForImport("CLOB", "(CLOB)");
        assertTrue(result);
        result = DataType.isNullValueForImport("string", "test");
        assertFalse(result);
    }

    public void testIsNullValueForExport() {

        DataType.getTypeMapping(databaseInfo, true, true);
        DataType.getTypeMapping(databaseInfo, true, false);
        DataType.getTypeMapping(databaseInfo, false, false);
        DataType.getTypeMapping(databaseInfo, false, true);

        boolean result = DataType.isNullValueForExport(null, null);
        assertTrue(result);
        result = DataType.isNullValueForExport(DataType.DATATYPE_BLOB, DataType.BLOB_EXPORT_FORMAT);
        assertTrue(result);

        result = DataType.isNullValueForExport(DataType.DATATYPE_CLOB, DataType.CLOB_EXPORT_FORMAT);
        assertTrue(result);

        result = DataType.isNullValueForExport(DataType.DATATYPE_BIT, DataType.BIT_EXPORT_FORMAT);
        assertTrue(result);

        result =
                DataType.isNullValueForExport(
                        DataType.DATATYPE_BIT_VARYING, DataType.BIT_EXPORT_FORMAT);
        assertTrue(result);
    }
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        dropTestTable();
        dropTestSuperTable();
    }
}
