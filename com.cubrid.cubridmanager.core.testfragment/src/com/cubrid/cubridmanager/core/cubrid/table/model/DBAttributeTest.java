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
package com.cubrid.cubridmanager.core.cubrid.table.model;

import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SerialInfo;
import com.cubrid.common.core.util.DateUtil;
import com.cubrid.cubridmanager.core.SetupJDBCTestCase;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * test DBAttribute model
 *
 * @author wuyingshi
 * @version 1.0 - 2010-1-4 created by wuyingshi
 */
public class DBAttributeTest extends SetupJDBCTestCase {
    String name = "name";
    String type = "type";
    String inherit = "inherit"; // it belongs to which class
    boolean indexed = true;
    boolean notNull = true;
    boolean shared = true;
    boolean unique = true;
    String defaultValue = "defaultValue";
    SerialInfo autoIncrement = null;
    String domainClassName = "domainClassName";
    boolean isClassAttribute = true;

    /** test shown type */
    public void testGetShownType() {
        DBAttribute dbAttribute =
                new DBAttribute(
                        name,
                        type,
                        inherit,
                        indexed,
                        notNull,
                        shared,
                        unique,
                        defaultValue,
                        "iso88591_bin");
        DBAttribute dbAttribute2 =
                new DBAttribute(
                        name,
                        type,
                        inherit,
                        indexed,
                        notNull,
                        shared,
                        unique,
                        defaultValue,
                        "iso88591_bin");
        // test public boolean equals(Object obj)
        assertTrue(dbAttribute.equals(dbAttribute));
        dbAttribute.equals(dbAttribute2);
        assertFalse(dbAttribute.equals(null));
        assertFalse(dbAttribute.equals("other object"));
        DBAttribute dbAttribute3 =
                new DBAttribute(
                        name,
                        type,
                        inherit,
                        indexed,
                        notNull,
                        shared,
                        unique,
                        defaultValue,
                        "iso88591_bin");
        dbAttribute3.setName(null);
        dbAttribute3.setType("atttype");
        dbAttribute3.setInherit("inherit");
        dbAttribute3.setDefault("defaultValue");
        dbAttribute3.setAutoIncrement(new SerialInfo());
        dbAttribute.equals(dbAttribute3);
        // test public int hashCode()
        dbAttribute.hashCode();
        // test public SerialInfo clone()
        DBAttribute cloned = dbAttribute.clone();
        DBAttribute cloned3 = dbAttribute3.clone();
        assertEquals(dbAttribute, cloned);
        assertEquals(dbAttribute3, cloned3);
        String type;
        String shownType;
        String expectedShownType;
        //		{"CHAR","character"},
        //		{"VARCHAR","character varying(1073741823)"},
        //		{"VARCHAR","character varying"},
        type = "character(1)";
        expectedShownType = "CHAR(1)";
        shownType = DataType.getShownType(type);
        assertEquals(expectedShownType, shownType);

        type = "character(4)";
        expectedShownType = "CHAR(4)";
        shownType = DataType.getShownType(type);
        assertEquals(expectedShownType, shownType);

        type = "character varying(1073741823)";
        expectedShownType = "VARCHAR(1073741823)";
        shownType = DataType.getShownType(type);
        assertEquals(expectedShownType, shownType);

        type = "character varying(30)";
        expectedShownType = "VARCHAR(30)";
        shownType = DataType.getShownType(type);
        assertEquals(expectedShownType, shownType);

        //		{"NCHAR","national character"},
        //		{"NCHAR VARYING","national character varying"},
        type = "national character(1)";
        expectedShownType = "NCHAR(1)";
        shownType = DataType.getShownType(type);
        assertEquals(expectedShownType, shownType);

        type = "national character varying(4)";
        expectedShownType = "NCHAR VARYING(4)";
        shownType = DataType.getShownType(type);
        assertEquals(expectedShownType, shownType);

        //		{"BIT","bit"},
        //		{"BIT VARYING","bit varying"},
        type = "bit(10)";
        expectedShownType = "BIT(10)";
        shownType = DataType.getShownType(type);
        assertEquals(expectedShownType, shownType);

        type = "bit varying(30)";
        expectedShownType = "BIT VARYING(30)";
        shownType = DataType.getShownType(type);
        assertEquals(expectedShownType, shownType);

        //		{"NUMERIC","numeric"},
        //		{"INTEGER","integer"},
        //		{"SMALLINT","smallint"},
        //		{"MONETARY","monetary"},
        //		{"FLOAT","float"},
        //		{"DOUBLE","double"},
        type = "numeric(15,0)";
        expectedShownType = "NUMERIC(15,0)";
        shownType = DataType.getShownType(type);
        assertEquals(expectedShownType, shownType);

        type = "integer";
        expectedShownType = "INTEGER";
        shownType = DataType.getShownType(type);
        assertEquals(expectedShownType, shownType);

        type = "smallint";
        expectedShownType = "SMALLINT";
        shownType = DataType.getShownType(type);
        assertEquals(expectedShownType, shownType);

        type = "monetary";
        expectedShownType = "MONETARY";
        shownType = DataType.getShownType(type);
        assertEquals(expectedShownType, shownType);

        type = "float";
        expectedShownType = "FLOAT";
        shownType = DataType.getShownType(type);
        assertEquals(expectedShownType, shownType);

        type = "double";
        expectedShownType = "DOUBLE";
        shownType = DataType.getShownType(type);
        assertEquals(expectedShownType, shownType);
        //		{"DATE","date"},
        //		{"TIME","time"},
        //		{"TIMESTAMP","timestamp"},

        type = "date";
        expectedShownType = "DATE";
        shownType = DataType.getShownType(type);
        assertEquals(expectedShownType, shownType);

        type = "time";
        expectedShownType = "TIME";
        shownType = DataType.getShownType(type);
        assertEquals(expectedShownType, shownType);

        type = "timestamp";
        expectedShownType = "TIMESTAMP";
        shownType = DataType.getShownType(type);
        assertEquals(expectedShownType, shownType);

        //		{"SET","set_of"},
        //		{"MULTISET","multiset_of"},
        //		{"SEQUENCE","sequence_of"}

        type = "set_of(numeric(15,0))";
        expectedShownType = "SET(NUMERIC(15,0))";
        shownType = DataType.getShownType(type);
        assertEquals(expectedShownType, shownType);

        type = "multiset_of(numeric(15,0))";
        expectedShownType = "MULTISET(NUMERIC(15,0))";
        shownType = DataType.getShownType(type);
        assertEquals(expectedShownType, shownType);

        type = "sequence_of(numeric(15,0))";
        expectedShownType = "SEQUENCE(NUMERIC(15,0))";
        shownType = DataType.getShownType(type);
        assertEquals(expectedShownType, shownType);

        type = "set_of(multiset_of(numeric(15,0)))";
        expectedShownType = "SET(MULTISET(NUMERIC(15,0)))";
        shownType = DataType.getShownType(type);
        assertEquals(expectedShownType, shownType);
    }

    /** test type */
    public void testGetType() {
        String expectedType;
        String type;
        String shownType;
        //		{"CHAR","character"},
        //		{"VARCHAR","character varying(1073741823)"},
        //		{"VARCHAR","character varying"},
        expectedType = "character(1)";
        shownType = "CHAR(1)";
        type = DataType.getRealType(shownType);
        assertEquals(expectedType, type);

        expectedType = "character(4)";
        shownType = "CHAR(4)";
        type = DataType.getRealType(shownType);
        assertEquals(expectedType, type);

        expectedType = "character varying(1073741823)";
        shownType = "VARCHAR(1073741823)";
        type = DataType.getRealType(shownType);
        assertEquals(expectedType, type);

        expectedType = "character varying(30)";
        shownType = "VARCHAR(30)";
        type = DataType.getRealType(shownType);
        assertEquals(expectedType, type);

        //		{"NCHAR","national character"},
        //		{"NCHAR VARYING","national character varying"},
        expectedType = "national character(1)";
        shownType = "NCHAR(1)";
        type = DataType.getRealType(shownType);
        assertEquals(expectedType, type);

        expectedType = "national character varying(4)";
        shownType = "NCHAR VARYING(4)";
        type = DataType.getRealType(shownType);
        assertEquals(expectedType, type);

        //		{"BIT","bit"},
        //		{"BIT VARYING","bit varying"},
        expectedType = "bit(10)";
        shownType = "BIT(10)";
        type = DataType.getRealType(shownType);
        assertEquals(expectedType, type);

        expectedType = "bit varying(30)";
        shownType = "BIT VARYING(30)";
        type = DataType.getRealType(shownType);
        assertEquals(expectedType, type);

        //		{"NUMERIC","numeric"},
        //		{"INTEGER","integer"},
        //		{"SMALLINT","smallint"},
        //		{"MONETARY","monetary"},
        //		{"FLOAT","float"},
        //		{"DOUBLE","double"},
        expectedType = "numeric(15,0)";
        shownType = "NUMERIC(15,0)";
        type = DataType.getRealType(shownType);
        assertEquals(expectedType, type);

        expectedType = "integer";
        shownType = "INTEGER";
        type = DataType.getRealType(shownType);
        assertEquals(expectedType, type);

        expectedType = "smallint";
        shownType = "SMALLINT";
        type = DataType.getRealType(shownType);
        assertEquals(expectedType, type);

        expectedType = "monetary";
        shownType = "MONETARY";
        type = DataType.getRealType(shownType);
        assertEquals(expectedType, type);

        expectedType = "float";
        shownType = "FLOAT";
        type = DataType.getRealType(shownType);
        assertEquals(expectedType, type);

        expectedType = "double";
        shownType = "DOUBLE";
        type = DataType.getRealType(shownType);
        assertEquals(expectedType, type);
        //		{"DATE","date"},
        //		{"TIME","time"},
        //		{"TIMESTAMP","timestamp"},

        expectedType = "date";
        shownType = "DATE";
        type = DataType.getRealType(shownType);
        assertEquals(expectedType, type);

        expectedType = "time";
        shownType = "TIME";
        type = DataType.getRealType(shownType);
        assertEquals(expectedType, type);

        expectedType = "timestamp";
        shownType = "TIMESTAMP";
        type = DataType.getRealType(shownType);
        assertEquals(expectedType, type);

        //		{"SET","set_of"},
        //		{"MULTISET","multiset_of"},
        //		{"SEQUENCE","sequence_of"}

        expectedType = "set_of(numeric(15,0))";
        shownType = "SET(NUMERIC(15,0))";
        type = DataType.getRealType(shownType);
        assertEquals(expectedType, type);

        expectedType = "multiset_of(numeric(15,0))";
        shownType = "MULTISET(NUMERIC(15,0))";
        type = DataType.getRealType(shownType);
        assertEquals(expectedType, type);

        expectedType = "sequence_of(numeric(15,0))";
        shownType = "SEQUENCE(NUMERIC(15,0))";
        type = DataType.getRealType(shownType);
        assertEquals(expectedType, type);
    }

    String atttype = null;
    String attdeft = null;
    String retattdeft = null;

    /**
     * test time
     *
     * @throws ParseException if failed
     */
    public void testTime() throws ParseException {
        atttype = "time";

        attdeft = "1245";
        retattdeft = "1245";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "09:53:06";
        retattdeft = "TIME'09:53:06'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "09:53:06";
        retattdeft = "TIME'09:53:06'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "19:53:06";
        retattdeft = "TIME'19:53:06'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "09:53";
        retattdeft = "TIME'09:53:00'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "09:53";
        retattdeft = "TIME'09:53:00'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "19:53";
        retattdeft = "TIME'19:53:00'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "sysTime";
        retattdeft = "systime";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "currentTime";
        retattdeft = "current_time";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "";
        retattdeft = "";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "ddd";
        retattdeft = "ddd";
        assertEquals(false, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
    }

    /**
     * test date
     *
     * @throws ParseException if failed
     */
    public void testDate() throws ParseException {
        atttype = "Date";

        attdeft = "12/31/9999";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        attdeft = "01/01/0001";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));

        attdeft = "02/23/2009";
        retattdeft = "DATE'02/23/2009'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "2009/02/23";
        retattdeft = "DATE'02/23/2009'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "2009-02-23";
        retattdeft = "DATE'02/23/2009'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "02/23";
        retattdeft = "DATE'02/23/1970'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "sysDate";
        retattdeft = "sysdate";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "currentdATe";
        retattdeft = "current_date";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "";
        retattdeft = "";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "date'";
        retattdeft = "date'";
        assertEquals(false, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertNotSame(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));
    }

    /**
     * test set
     *
     * @throws ParseException if failed
     */
    public void testSet() throws ParseException {
        atttype = "set_of(tImestamp)";

        attdeft = "2009/02/23 09:53:08,2009-02-23 09:53:09,2009/02/23 09:53:10";
        retattdeft =
                "{TIMESTAMP'2009-02-23 09:53:08',"
                        + "TIMESTAMP'2009-02-23 09:53:09',"
                        + "TIMESTAMP'2009-02-23 09:53:10'}";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        atttype = "set_of(datetime)";

        attdeft =
                "2009/02/23 09:53:08.333,"
                        + "2009/02/23  09:53:09.333,"
                        + "2009/02/23 09:53:10.333";
        retattdeft =
                "{DATETIME'2009-02-23 09:53:08.333',"
                        + "DATETIME'2009-02-23 09:53:09.333',"
                        + "DATETIME'2009-02-23 09:53:10.333'}";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));
    }

    /**
     * test timestamp
     *
     * @throws ParseException if failed
     */
    public void testTimestamp() throws ParseException {
        atttype = "tImestamp";

        attdeft = "2009/02/23 09:53:08";
        retattdeft = "TIMESTAMP'2009-02-23 09:53:08'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "2009-02-23 09:53:08";
        retattdeft = "TIMESTAMP'2009-02-23 09:53:08'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "2009/02/23 09:53:08";
        retattdeft = "TIMESTAMP'2009-02-23 09:53:08'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "2009-02-23 09:53:08";
        retattdeft = "TIMESTAMP'2009-02-23 09:53:08'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "09:53:08  02/23/2009";
        retattdeft = "TIMESTAMP'2009-02-23 09:53:08'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "09:53:08 02/23/2009";
        retattdeft = "TIMESTAMP'2009-02-23 09:53:08'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "2009/02/23  09:53";
        retattdeft = "TIMESTAMP'2009-02-23 09:53:00'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "2009-02-23  09:53";
        retattdeft = "TIMESTAMP'2009-02-23 09:53:00'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "2009/02/23 09:53";
        retattdeft = "TIMESTAMP'2009-02-23 09:53:00'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "2009-02-23 09:53";
        retattdeft = "TIMESTAMP'2009-02-23 09:53:00'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "09:53 02/23/2009";
        retattdeft = "TIMESTAMP'2009-02-23 09:53:00'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "09:53 02/23/2009";
        retattdeft = "TIMESTAMP'2009-02-23 09:53:00'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "systImestamp";
        retattdeft = "systimestamp";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "currenttImestamp";
        retattdeft = "current_timestamp";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "09:53:05 02/23";
        retattdeft = "TIMESTAMP'09:53:05 02/23'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "02/23 09:53:05";
        retattdeft = "TIMESTAMP'1970-02-23 00:00:00'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "";
        retattdeft = "";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "timestamp";
        retattdeft = "timestamp";
        assertEquals(false, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertNotSame(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));
    }

    /** test formate datetime */
    public void testFormatDatetime() {
        attdeft = "2009/02/23 09:53:08.0";
        retattdeft = "2009/02/23 09:53:08.000";
        assertEquals(retattdeft, DateUtil.formatDateTime(attdeft, "yyyy/MM/dd hh:mm:ss.SSS"));

        attdeft = "2009/02/23  09:53:08.3";
        retattdeft = "2009/02/23  09:53:08.300";
        assertEquals(retattdeft, DateUtil.formatDateTime(attdeft, "yyyy/MM/dd  hh:mm:ss.SSS"));

        attdeft = "2009/02/23  09:53:08.33";
        retattdeft = "2009/02/23  09:53:08.330";
        assertEquals(retattdeft, DateUtil.formatDateTime(attdeft, "yyyy/MM/dd  hh:mm:ss.SSS"));
    }

    /**
     * test date time
     *
     * @throws ParseException if failed
     */
    public void testDatetime() throws ParseException {
        atttype = "datetime";

        attdeft = "2009/02/23  09:53:08.3";
        retattdeft = "DATETIME'2009-02-23 09:53:08.300'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "2009/02/23  09:53:08.333";
        retattdeft = "DATETIME'2009-02-23 09:53:08.333'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "2009-02-23  09:53:08.333";
        retattdeft = "DATETIME'2009-02-23 09:53:08.333'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "2009/02/23 09:53:08.333";
        retattdeft = "DATETIME'2009-02-23 09:53:08.333'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "2009-02-23 09:53:08.333";
        retattdeft = "DATETIME'2009-02-23 09:53:08.333'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "09:53:08.333 02/23/2009";
        retattdeft = "DATETIME'2009-02-23 09:53:08.333'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "09:53:08.333 02/23/2009";
        retattdeft = "DATETIME'2009-02-23 09:53:08.333'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "2009/02/23 09:53";
        retattdeft = "DATETIME'2009-02-23 09:53:00.000'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "2009-02-23  09:53";
        retattdeft = "DATETIME'2009-02-23 09:53:00.000'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "2009/02/23 09:53";
        retattdeft = "DATETIME'2009-02-23 09:53:00.000'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "2009-02-23 09:53";
        retattdeft = "DATETIME'2009-02-23 09:53:00.000'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "09:53 02/23/2009";
        retattdeft = "DATETIME'2009-02-23 09:53:00.000'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "09:53 02/23/2009";
        retattdeft = "DATETIME'2009-02-23 09:53:00.000'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "sysDatetime";
        retattdeft = "sysdatetime";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "sys_Datetime";
        retattdeft = "sysdatetime";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "current_Datetime";
        retattdeft = "current_datetime";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "09:53:05 02/23";
        retattdeft = "DATETIME'09:53:05 02/23'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "09:53:05  02/23";
        retattdeft = "DATETIME'09:53:05  02/23'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "02/23 09:53:05";
        retattdeft = "DATETIME'02/23 09:53:05'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "02/23 09:53:05";
        retattdeft = "DATETIME'02/23 09:53:05'";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "";
        retattdeft = "";
        assertEquals(true, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));

        attdeft = "DATETIME'";
        retattdeft = "";
        assertEquals(false, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        //		assertEquals(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft));

        attdeft = "DATETIME''";
        retattdeft = "DATETIME''";
        assertNotSame(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));
        assertEquals(false, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
        attdeft = "TIME''";
        retattdeft = "TIME''";
        assertNotSame(retattdeft, DBAttrTypeFormatter.formatValue(atttype, attdeft, true));
        assertEquals(false, DBAttrTypeFormatter.validateAttributeValue(atttype, attdeft, true));
    }

    /** test data */
    public void testData() {
        assertNotSame("string", DBAttrTypeFormatter.formatValue("string", "string", true));
        assertEquals("1", DBAttrTypeFormatter.formatValue("integer", "1", true));
        assertEquals("1", DBAttrTypeFormatter.formatValue("smallint", "1", true));
        assertEquals("1", DBAttrTypeFormatter.formatValue("bigint", "1", true));
        assertNotSame(
                "8.999876543113200e+01",
                DBAttrTypeFormatter.formatValue("double", "89.998765431132", true));
        assertNotSame("$8,934.00", DBAttrTypeFormatter.formatValue("monetary", "8934", true));
        DBAttrTypeFormatter.formatValue("integer", "aaa", true);
        DBAttrTypeFormatter.formatValue("smallint", "aaa", true);
        DBAttrTypeFormatter.formatValue("monetary", "$8934", true);
        DBAttrTypeFormatter.formatValue("monetary", "ss8934", true);
        DBAttrTypeFormatter.formatValue("monetary", "$8934", true);
        DBAttrTypeFormatter.formatValue("numeric(2,0)", "ss8934", true);
        DBAttrTypeFormatter.formatValue("numeric(2,0)", "8934", true);
        DBAttrTypeFormatter.formatValue("numeric(5,2)", "89341.22", true);
        DBAttrTypeFormatter.formatValue("string", "ssss", true);
        DBAttrTypeFormatter.formatValue("string", "ss~ss", true);
        DBAttrTypeFormatter.formatValue("string", "", true);
        DBAttrTypeFormatter.formatValue("string", "'aaa'", true);
        DBAttrTypeFormatter.formatValue("double", "sss", true);
        DBAttrTypeFormatter.formatValue("double", "99999999999999", true);
        DBAttrTypeFormatter.formatValue("double", "9999", true);
        DBAttrTypeFormatter.formatValue("float", "99999999999999", true);
        DBAttrTypeFormatter.formatValue("float", "sss", true);
        DBAttrTypeFormatter.formatValue("NCHAR(8)", "N'aaa'", true);
        DBAttrTypeFormatter.formatValue("NCHAR(8)", "", true);
        DBAttrTypeFormatter.formatValue("NCHAR(8)", "aaa", true);
        DBAttrTypeFormatter.formatValue("BIT(8)", "B'0001'", true);
        DBAttrTypeFormatter.formatValue("BIT(8)", "X'12c34A'", true);
        DBAttrTypeFormatter.formatValue("BIT(8)", "", true);
        DBAttrTypeFormatter.formatValue("BIT(8)", "0", true);
        DBAttrTypeFormatter.formatValue("BIT(8)", "aaa", true);
        DBAttrTypeFormatter.formatValue("BIT(8)", "!@D", true);
        DBAttrTypeFormatter.formatValue("bigint", "sss", true);
        DBAttrTypeFormatter.formatValue("char(8)", "sss", true);
        DBAttrTypeFormatter.formatValue("char(2)", "sss", true);
        DBAttrTypeFormatter.formatValue("sequence(int)", "{2,3}", true);
        DBAttrTypeFormatter.formatValue("blob", "bolb", true);
        DBAttrTypeFormatter.formatValue("clob", "colb", true);
    }

    public void testType() {
        assertFalse(DBAttrTypeFormatter.validateAttributeType(null));
        assertFalse(DBAttrTypeFormatter.validateAttributeType(""));
        assertTrue(DBAttrTypeFormatter.validateAttributeType("NUMERIC(5,2)"));
        assertFalse(DBAttrTypeFormatter.validateAttributeType("NUMERIC"));
        assertFalse(DBAttrTypeFormatter.validateAttributeType("NUMERIC(2,2)"));
        assertTrue(DBAttrTypeFormatter.validateAttributeType("VARCHAR(30)"));
        assertFalse(DBAttrTypeFormatter.validateAttributeType("VARCHAR"));
        assertFalse(DBAttrTypeFormatter.validateAttributeType("VARCHAR(-1)"));
        assertTrue(DBAttrTypeFormatter.validateAttributeType("SET(VARCHAR(30))"));
        assertFalse(DBAttrTypeFormatter.validateAttributeType("SET(VARCHAR)"));
        assertFalse(DBAttrTypeFormatter.validateAttributeType("SET"));

        // assertTrue(DBAttrTypeFormatter.validateAttributeType("INT"));

        assertTrue(DBAttrTypeFormatter.isMuchValueType("STRING"));
        assertFalse(DBAttrTypeFormatter.isMuchValueType("int"));
        assertTrue(DBAttrTypeFormatter.isMuchValueType("NCHAR VARYING", -1));
        assertTrue(DBAttrTypeFormatter.isMuchValueType("NCHAR VARYING(200)", 100));
        assertFalse(DBAttrTypeFormatter.isMuchValueType("NCHAR VARYING(6)", 10));

        Connection conn = null;
        try {
            conn = JDBCConnectionManager.getConnection(databaseInfo, false);
            DBAttrTypeFormatter.formatMuchValue(
                    "file:c:\\noexist.xls", "STRING", conn, "UTF-8", "UTF-8", true);
            DBAttrTypeFormatter.formatMuchValue(
                    "file:c:\\noexist.xls", "NCHAR VARYING(100)", conn, "UTF-8", "UTF-8", true);
            DBAttrTypeFormatter.formatMuchValue(
                    "file:c:\\noexist.xls", "BLOB", conn, "UTF-8", "UTF-8", true);
            DBAttrTypeFormatter.formatMuchValue(
                    "file:c:\\noexist.xls", "CLOB", conn, "UTF-8", "UTF-8", true);
        } catch (SQLException e) {

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
