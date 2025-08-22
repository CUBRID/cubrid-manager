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

import com.cubrid.common.core.reader.CSVReader;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.DateUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;

/**
 * to provide methods and constants of data type in CUBRID
 *
 * @author moulinwang
 * @version 1.0 - 2009-6-4 created by moulinwang
 */
public final class DataType {

    public static final String DATATYPE_NATIONAL_CHARACTER = "NATIONAL CHARACTER";
    public static final String DATATYPE_NCHAR_VARYING = "NCHAR VARYING";
    public static final String DATATYPE_CURRENCY = "CURRENCY";
    public static final String DATATYPE_STRING = "STRING";
    public static final String DATATYPE_CURSOR = "CURSOR";
    public static final String DATATYPE_OBJECT = "OBJECT";
    public static final String DATATYPE_INT = "INT";
    public static final String DATATYPE_SHORT = "SHORT";
    public static final String DATATYPE_OID = "OID";
    public static final String DATATYPE_VARCHAR = "VARCHAR";
    public static final String DATATYPE_CHAR = "CHAR";
    public static final String DATATYPE_NCHAR = "NCHAR";
    public static final String DATATYPE_MONETARY = "MONETARY";
    public static final String DATATYPE_TINYINT = "TINYINT";
    public static final String DATATYPE_CLOB = "CLOB";
    public static final String DATATYPE_BLOB = "BLOB";
    public static final String DATATYPE_SEQUENCE = "SEQUENCE";
    public static final String DATATYPE_MULTISET = "MULTISET";
    public static final String DATATYPE_SET = "SET";
    public static final String DATATYPE_ENUM = "ENUM";
    public static final String DATATYPE_DATETIME = "DATETIME";
    public static final String DATATYPE_CLASS = "CLASS";
    public static final String DATATYPE_BIGINT = "BIGINT";
    public static final String DATATYPE_DECIMAL = "DECIMAL";
    public static final String DATATYPE_NUMERIC = "NUMERIC";
    public static final String DATATYPE_REAL = "REAL";
    public static final String DATATYPE_FLOAT = "FLOAT";
    public static final String DATATYPE_DOUBLE = "DOUBLE";
    public static final String DATATYPE_BIT_VARYING = "BIT VARYING";
    public static final String DATATYPE_BIT = "BIT";
    public static final String DATATYPE_TIMESTAMP = "TIMESTAMP";
    public static final String DATATYPE_TIME = "TIME";
    public static final String DATATYPE_DATE = "DATE";
    public static final String DATATYPE_SMALLINT = "SMALLINT";
    public static final String DATATYPE_INTEGER = "INTEGER";

    public static final String DATATYPE_CHARACTER = "CHARACTER";
    public static final String DATATYPE_CHARACTER_VARYING = "CHARACTER VARYING";
    public static final String DATATYPE_NATIONAL_CHARACTER_VARYING = "NATIONAL CHARACTER VARYING";
    // BLOB export format for CSV and XLS and show in Query editor result
    public static final String BLOB_EXPORT_FORMAT = "(BLOB)";
    // CLOB export format for CSV and XLS and show in Query editor result
    public static final String CLOB_EXPORT_FORMAT = "(CLOB)";
    // NULL object export format for CSV and XLS and show in Query editor result
    public static final String NULL_EXPORT_FORMAT = "(NULL)";
    // BIT object export format for show in Query editor
    public static final String BIT_EXPORT_FORMAT = "(BIT)";
    // Export format for CLASS and NULL object in SET, mark NULL object in table
    // item column
    public static final String VALUE_NULL = "NULL";
    public static final String VALUE_NONE = "NONE";

    public static final int STRING_MAX_SIZE = 1073741823;
    public static final int NUMERIC_DEFAULT_PRECISION = 15;
    public static final int BIT_DEFAULT_LENGTH = 1;
    public static final int BIT_VARYING_DEFAULT_LENGTH = STRING_MAX_SIZE;
    public static final int CHAR_DEFAULT_LENGTH = 1;
    public static final int NCHAR_DEFAULT_LENGTH = 1;
    public static final int NCHAR_VARYING_DEFAULT_LENGTH = STRING_MAX_SIZE / 2;
    public static final int VARCHAR_DEFAULT_LENGTH = STRING_MAX_SIZE;
    public static final String ENUM_DAFAULT_VALUE = "DEFAULT";
    public static final int NUMERIC_MAX_PRECISION = 255; // and the format: NUMERIC [(p[, s])]
    public static final int FLOAT_MAX_PRECISION = 38;

    /*The range*/
    public static final int SMALLINT_MIN_VALUE = Short.MIN_VALUE;
    public static final int SMALLINT_MAX_VALUE = Short.MAX_VALUE;
    public static final int INT_MIN_VALUE = Integer.MIN_VALUE;
    public static final int INT_MAX_VALUE = Integer.MAX_VALUE;
    public static final long BIGINT_MIN_VALUE = Long.MIN_VALUE;
    public static final long BIGINT_MAX_VALUE = Long.MAX_VALUE;

    private static final List<String> NULLVALUESLISTFORIMPORT = new ArrayList<String>();
    // Constructor
    private DataType() {
        // empty
    }

    private static Logger logger = LogUtil.getLogger(DataType.class);
    private static String[][] typeMapping = {
        // Sort by alphabet order
        {DATATYPE_BIGINT, DATATYPE_BIGINT.toLowerCase(Locale.getDefault())},
        {DATATYPE_BIT, DATATYPE_BIT.toLowerCase(Locale.getDefault())},
        {DATATYPE_BIT_VARYING, DATATYPE_BIT_VARYING.toLowerCase(Locale.getDefault())},
        {DATATYPE_BLOB, DATATYPE_BLOB.toLowerCase(Locale.getDefault())},
        {DATATYPE_CHAR, "character"},
        {DATATYPE_CLOB, DATATYPE_CLOB.toLowerCase(Locale.getDefault())},
        {DATATYPE_DATE, DATATYPE_DATE.toLowerCase(Locale.getDefault())},
        {DATATYPE_DATETIME, DATATYPE_DATETIME.toLowerCase(Locale.getDefault())},
        {DATATYPE_DOUBLE, DATATYPE_DOUBLE.toLowerCase(Locale.getDefault())},
        {DATATYPE_FLOAT, DATATYPE_FLOAT.toLowerCase(Locale.getDefault())},
        {DATATYPE_INTEGER, DATATYPE_INTEGER.toLowerCase(Locale.getDefault())},
        {DATATYPE_MONETARY, DATATYPE_MONETARY.toLowerCase(Locale.getDefault())},
        {DATATYPE_NCHAR, "national character"},
        {DATATYPE_NCHAR_VARYING, "national character varying"},
        {DATATYPE_MULTISET, "multiset_of"},
        {DATATYPE_NUMERIC, DATATYPE_NUMERIC.toLowerCase(Locale.getDefault())},
        {DATATYPE_OBJECT, DATATYPE_OBJECT.toLowerCase(Locale.getDefault())},
        {DATATYPE_SEQUENCE, "sequence_of"},
        {DATATYPE_SET, "set_of"},
        {DATATYPE_SMALLINT, DATATYPE_SMALLINT.toLowerCase(Locale.getDefault())},
        {DATATYPE_STRING, DATATYPE_STRING.toLowerCase(Locale.getDefault())},
        {DATATYPE_TIME, DATATYPE_TIME.toLowerCase(Locale.getDefault())},
        {DATATYPE_TIMESTAMP, DATATYPE_TIMESTAMP.toLowerCase(Locale.getDefault())},
        {DATATYPE_VARCHAR, "character varying"}
    };
    /*Support for CUBRID 9.0*/
    private static String[] enumMapping = {DATATYPE_ENUM, "enum"};
    // Sort by categories
    // {DATATYPE_SMALLINT, DATATYPE_SMALLINT.toLowerCase(Locale.getDefault()) },
    // {DATATYPE_INTEGER, DATATYPE_INTEGER.toLowerCase(Locale.getDefault()) },
    // {DATATYPE_BIGINT, DATATYPE_BIGINT.toLowerCase(Locale.getDefault()) },
    // {DATATYPE_NUMERIC, DATATYPE_NUMERIC.toLowerCase(Locale.getDefault()) },
    // {DATATYPE_FLOAT, DATATYPE_FLOAT.toLowerCase(Locale.getDefault()) },
    // {DATATYPE_DOUBLE, DATATYPE_DOUBLE.toLowerCase(Locale.getDefault()) },
    // {DATATYPE_CHAR, "character" },
    // {DATATYPE_VARCHAR, "character varying" },
    // {DATATYPE_NCHAR, "national character" },
    // {DATATYPE_NCHAR_VARYING, "national character varying" },
    // {DATATYPE_TIME, DATATYPE_TIME.toLowerCase(Locale.getDefault()) },
    // {DATATYPE_DATE, DATATYPE_DATE.toLowerCase(Locale.getDefault()) },
    // {DATATYPE_TIMESTAMP, DATATYPE_TIMESTAMP.toLowerCase(Locale.getDefault())
    // },
    // {DATATYPE_DATETIME, DATATYPE_DATETIME.toLowerCase(Locale.getDefault()) },
    // {DATATYPE_BIT, DATATYPE_BIT.toLowerCase(Locale.getDefault()) },
    // {DATATYPE_BIT_VARYING,
    // DATATYPE_BIT_VARYING.toLowerCase(Locale.getDefault()) },
    // {DATATYPE_MONETARY, DATATYPE_MONETARY.toLowerCase(Locale.getDefault()) },
    // {DATATYPE_STRING, DATATYPE_STRING.toLowerCase(Locale.getDefault()) },
    // {DATATYPE_BLOB, DATATYPE_BLOB.toLowerCase(Locale.getDefault()) },
    // {DATATYPE_CLOB, DATATYPE_CLOB.toLowerCase(Locale.getDefault()) },
    // {DATATYPE_SET, "set_of" },
    // {DATATYPE_MULTISET, "multiset_of" },
    // {DATATYPE_SEQUENCE, "sequence_of" },
    // {DATATYPE_OBJECT, DATATYPE_OBJECT.toLowerCase(Locale.getDefault()) }

    /**
     * O return whether a given type is basic data type in CUBRID
     *
     * @param type String The given type
     * @return boolean whether is the basic type
     */
    public static boolean isBasicType(String type) {
        String typePart = DataType.getTypePart(type);
        for (String[] item : typeMapping) {
            if (typePart.equals(item[1])) {
                return true;
            }
        }
        return false;
    }

    /**
     * generate data type via JDBC meta data information of CUBRID
     *
     * @param colType data type
     * @param elemType element type in collection if colType is SET,MULTISET or SEQUENCE
     * @param precision valid digital number
     * @param scale float digital number
     * @return String the data type
     */
    public static String makeType(String colType, String elemType, int precision, int scale) {
        if (DATATYPE_SMALLINT.equals(colType)
                || DATATYPE_INTEGER.equals(colType)
                || DATATYPE_BIGINT.equals(colType)
                || DATATYPE_FLOAT.equals(colType)
                || DATATYPE_DOUBLE.equals(colType)
                || DATATYPE_MONETARY.equals(colType)
                || DATATYPE_TIME.equals(colType)
                || DATATYPE_DATE.equals(colType)
                || DATATYPE_TIMESTAMP.equals(colType)
                || DATATYPE_DATETIME.equals(colType)) {
            return colType;
        } else if (DATATYPE_CHAR.equals(colType)
                || DATATYPE_VARCHAR.equals(colType)
                || DATATYPE_NCHAR.equals(colType)
                || DATATYPE_NCHAR_VARYING.equals(colType)
                || DATATYPE_BIT.equals(colType)
                || DATATYPE_BIT_VARYING.equals(colType)) {
            return colType + "(" + precision + ")";
        } else if (DATATYPE_NUMERIC.equals(colType)) {
            return colType + "(" + precision + "," + scale + ")";
        } else if (DATATYPE_SET.equals(colType)
                || DATATYPE_MULTISET.equals(colType)
                || DATATYPE_SEQUENCE.equals(colType)) {
            return colType + "(" + makeType(elemType, null, precision, scale) + ")";
        }
        return colType;
    }

    /**
     * return element type in collection if colType is SET,MULTISET or SEQUENCE else return null;
     *
     * @param jdbcType String The jdbc type
     * @return String The element type
     */
    public static String getElemType(String jdbcType) {
        String outType = getTypePart(jdbcType);
        if (isSetDataType(outType)) {
            String innerType = getTypeRemain(jdbcType);
            return innerType;
        } else {
            return null;
        }
    }

    /**
     * Get the scale of type.Caution:The collection type will return -1.
     *
     * @param jdbcType String The jdbc type
     * @return int The scale
     */
    public static int getScale(String jdbcType) {
        String type = getTypePart(jdbcType);
        if (isSetDataType(type)) {
            return -1;
        }

        String typeRemain = getTypeRemain(jdbcType);
        if (null == typeRemain) {
            return -1;
        } else {
            int beginIndex = typeRemain.indexOf("(");
            int endIndex = typeRemain.indexOf(")");

            if (beginIndex >= 0 && endIndex > beginIndex) {
                typeRemain = typeRemain.substring(beginIndex + 1, endIndex);
            }

            int index = typeRemain.indexOf(",");

            if (index == -1) {
                return -1;
            } else {
                return Integer.parseInt(typeRemain.substring(index + 1, typeRemain.length()));
            }
        }
    }

    /**
     * Get the size of data type.Caution:The collection type will return -1.
     *
     * @param jdbcType String The jdbc type
     * @return int The size
     */
    public static int getSize(String jdbcType) {
        String type = getTypePart(jdbcType);
        if (isSetDataType(type)) {
            return -1;
        }
        String typeRemain = getTypeRemain(jdbcType);
        if (null == typeRemain) {
            return -1;
        } else {
            int beginIndex = typeRemain.indexOf("(");
            int endIndex = typeRemain.indexOf(")");

            if (beginIndex >= 0 && endIndex > beginIndex) {
                typeRemain = typeRemain.substring(beginIndex + 1, endIndex);
            }

            int index = typeRemain.indexOf(",");
            if (index == -1) {
                return Integer.parseInt(typeRemain);
            } else {
                return Integer.parseInt(typeRemain.substring(0, index));
            }
        }
    }

    /**
     * return whether a data type is set type
     *
     * @param dataType String
     * @return boolean
     */
    public static boolean isSetDataType(String dataType) {
        return "set".equalsIgnoreCase(dataType)
                || "set_of".equalsIgnoreCase(dataType)
                || "multiset_of".equalsIgnoreCase(dataType)
                || "multiset".equalsIgnoreCase(dataType)
                || "sequence_of".equalsIgnoreCase(dataType)
                || "sequence".equalsIgnoreCase(dataType);
    }

    /**
     * get a list of super classes to a table
     *
     * @param database DatabaseInfo The given instance of DatabaseInfo
     * @param table String The given talbe name
     * @return Set<String> the set that includes super classes
     */
    public static Set<String> getSuperClasses(DatabaseInfo database, String table) {
        Set<String> set = new HashSet<String>();
        if (database == null) {
            return set;
        }

        List<String> supers = database.getSchemaInfo(table).getSuperClasses();
        set.addAll(supers);
        for (String sup : supers) {
            set.addAll(getSuperClasses(database, sup));
        }
        return set;
    }

    /**
     * check whether two types are compatible in CUBRID
     *
     * @param database DatabaseInfo the given instance of DatabaseInfo
     * @param type1 String
     * @param type2 String
     * @return Integer the value indicates the state of result
     */
    public static Integer isCompatibleType(DatabaseInfo database, String type1, String type2) {
        if (type1.equals(type2)) {
            return 0;
        }
        if (DataType.isBasicType(type1) == DataType.isBasicType(type2)) {
            if (DataType.isBasicType(type1)) {
                if (type1.equals(type2)) {
                    return 0;
                } else {
                    return null;
                }
            } else {
                Set<String> set1 = getSuperClasses(database, type1);
                if (set1.contains(type2)) {
                    return 1;
                }
                Set<String> set2 = getSuperClasses(database, type2);
                if (set2.contains(type1)) {
                    return -1;
                }
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * return the type part of a special data type,eg:
     * <li>character(10), return "character"
     * <li>set(integer), return DATATYPE_SET
     * <li>integer, return "integer"
     *
     * @param type String a string includes the info of type
     * @return String the type
     */
    public static String getTypePart(String type) {
        int index = type.indexOf("(");
        if (-1 == index) { // the simplest case
            return type;
        } else { // the case like: set_of(bit) ,numeric(4,2)
            return type.substring(0, index);
        }
    }

    /**
     * return the remained part of a special data type,eg:
     * <li>character(10), return "10"
     * <li>set(integer), return "integer"
     * <li>integer, return null
     *
     * @see #getTypePart(String)
     * @param type String a string includes the info of type
     * @return String the type remain
     */
    public static String getTypeRemain(String type) {
        int index = type.indexOf("(");
        if (-1 == index) { // the simplest case
            return null;
        } else { // the case like: set_of(bit) ,numeric(4,2)
            return type.substring(index + 1, type.length() - 1);
        }
    }

    /**
     * To change CUBRID type string to upper case and shorter CUBRID type should be shown short and
     * upper case
     *
     * @param type String a string includes the info of type
     * @return String the upper type used for display
     */
    public static String getShownType(String type) {
        int index = type.indexOf("(");
        String typepart = null;
        String typedesc = null;

        if (-1 == index || type.indexOf(")") == -1) { // the simplest case
            for (String[] item : typeMapping) {
                if (type.equals(item[1])) {
                    return item[0];
                }
            }
            if (type.equals(enumMapping[1])) {
                return enumMapping[0];
            }
        } else { // the case like: set_of(bit) ,numeric(4,2)
            typepart = type.substring(0, index);
            typedesc = type.substring(index + 1, type.length() - 1);
            for (String[] item : typeMapping) {
                if (typepart.equals(item[1])) {
                    if (-1 == typepart.indexOf("_of")) {
                        return item[0] + "(" + typedesc + ")";
                    } else {
                        return item[0] + "(" + getShownType(typedesc) + ")";
                    }
                }
            }
        }
        return type;
    }

    /**
     * To change upper case and shorter shown type to CUBRID database type. CUBRID data type should
     * be shown short and lower case, sometime needing to be reversed cast.
     *
     * @param String the upper case shown type used for displaying.
     * @return String the lower case real database type.
     */
    public static String getRealType(String upperShownType) {
        int index = upperShownType.indexOf("(");
        String typepart = null;
        String typedesc = null;

        if (-1 == index) { // the simplest case
            for (String[] item : typeMapping) {
                if (upperShownType.equals(item[0])) {
                    return item[1];
                }
            }
        } else { // the case like: set_of(bit) ,numeric(4,2), set_of(numeric(15,0))
            int leftBracketCnt = 0;
            int rightBracketCnt = 0;
            int lastRightBracketIdx = -1;
            for (int i = 0, n = upperShownType.length(); i < n; i++) {
                char ch = upperShownType.charAt(i);
                if (ch == '(') {
                    leftBracketCnt++;
                } else if (ch == (')')) {
                    rightBracketCnt++;
                    lastRightBracketIdx = i;
                }
            }

            if (leftBracketCnt != rightBracketCnt
                    || leftBracketCnt == 0
                    || leftBracketCnt > 2
                    || lastRightBracketIdx != upperShownType.length() - 1) {
                return null;
            }

            typepart = upperShownType.substring(0, index);
            typedesc = upperShownType.substring(index + 1, upperShownType.length() - 1);
            for (String[] item : typeMapping) {
                if (typepart.equals(item[0])) {
                    if (DATATYPE_SET.equals(typepart)
                            || DATATYPE_MULTISET.equals(typepart)
                            || DATATYPE_SEQUENCE.equals(typepart)) {
                        String subType = getRealType(typedesc);
                        return subType == null ? null : item[1] + "(" + subType + ")";
                    } else if (leftBracketCnt == 1) { // only set/sequence can have sub type
                        return item[1] + "(" + typedesc + ")";
                    } else {
                        return null;
                    }
                }
            }
        }
        return upperShownType;
    }

    /**
     * Get the type mapping
     *
     * @param database DatabaseInfo if version lower then 8.3.1,dont return BLOB CLOB
     * @param showObjectConcept boolean if contain OBJECT
     * @param showSetType boolean if contain SET SEQUENCE MULTISET
     * @param database
     * @return String[][] the type mapping
     */
    public static String[][] getTypeMapping(
            DatabaseInfo database, boolean showObjectConcept, boolean showSetType) {
        String[][] temp = null;
        int resultLength = typeMapping.length;
        // isSupportLobVersion(serverInfo);
        boolean supportLobVersion = CompatibleUtil.isSupportLobVersion(database);
        boolean supportEnumVersion = CompatibleUtil.isSupportEnumVersion(database);

        if (!supportLobVersion) {
            resultLength = resultLength - 2;
        }
        if (supportEnumVersion) {
            resultLength++;
        }
        if (!showObjectConcept) {
            resultLength = resultLength - 1;
        }
        if (!showSetType) {
            resultLength = resultLength - 3;
        }
        temp = new String[resultLength][];
        int j = 0;
        for (int i = 0; i < typeMapping.length; i++) {
            if (!supportLobVersion
                    && (typeMapping[i][0].equals(DATATYPE_BLOB)
                            || typeMapping[i][0].equals(DATATYPE_CLOB))) {
                continue;
            }
            if (!showObjectConcept && typeMapping[i][0].equals(DATATYPE_OBJECT)) {
                continue;
            }
            if (!showSetType
                    && (typeMapping[i][0].equals(DATATYPE_SET)
                            || typeMapping[i][0].equals(DATATYPE_SEQUENCE)
                            || typeMapping[i][0].equals(DATATYPE_MULTISET))) {
                continue;
            }
            temp[j++] = (String[]) typeMapping[i].clone();
        }
        if (supportEnumVersion) {
            temp[j++] = (String[]) enumMapping.clone();
        }
        return temp;
    }

    /**
     * Return the max integer when the data domain is like Numeric(k,0)
     *
     * @param digitalNum int the given digital number
     * @return String The maximal integer
     */
    public static String getNumericMaxValue(int digitalNum) {
        assert (digitalNum > 0);
        StringBuffer bf = new StringBuffer();
        for (int i = 0; i < digitalNum; i++) {
            bf.append("9");
        }
        return bf.toString();
    }

    /**
     * return the min integer when the data domain is like Numeric(k,0)
     *
     * @param digitalNum int the given digital number
     * @return String The minimal integer
     */
    public static String getNumericMinValue(int digitalNum) {
        assert (digitalNum > 0);
        StringBuffer bf = new StringBuffer();
        bf.append("-");
        for (int i = 0; i < digitalNum; i++) {
            bf.append("9");
        }
        return bf.toString();
    }

    /**
     * Return Object[] array value from a collection value based the given data type, eg: data type:
     * integer, collection value: {1,2,3} return Object[]: Integer[]{1,2,3}
     *
     * @param type String The given type
     * @param value String The given value
     * @param isUseNULLValueSetting
     * @return Object[]
     * @throws ParseException a possible exception
     * @throws NumberFormatException a possible exception
     */
    public static Object[] getCollectionValues(
            String type, String value, boolean isUseNULLValueSetting)
            throws NumberFormatException, ParseException {
        String strs = value;
        String innerType = getTypeRemain(type);
        assert (innerType != null);
        if (innerType == null) {
            return null;
        }

        if (value.startsWith("{") && value.endsWith("}")) {
            strs = value.substring(1, value.length() - 1);
        }
        CSVReader reader = new CSVReader(new StringReader(strs));
        String[] values = new String[0];
        try {
            values = reader.readNext();
        } catch (IOException ignored) {
            logger.error("", ignored);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.error("", e);
                }
            }
        }
        Object[] ret = null;
        if (innerType.equalsIgnoreCase(DATATYPE_SMALLINT)
                || innerType.equalsIgnoreCase(DATATYPE_INTEGER)) {
            ret = new Integer[values.length];
        } else if (innerType.equalsIgnoreCase(DATATYPE_BIGINT)) {
            ret = new Long[values.length];
        } else if (innerType.startsWith("numeric(") && innerType.endsWith(",0)")) {
            ret = new Long[values.length];
        } else if (innerType.equalsIgnoreCase(DATATYPE_FLOAT)) {
            ret = new Double[values.length];
        } else if (innerType.equalsIgnoreCase(DATATYPE_DOUBLE)
                || innerType.equalsIgnoreCase(DATATYPE_MONETARY)) {
            ret = new Double[values.length];
        } else if (innerType.startsWith("numeric(") && !innerType.endsWith(",0)")) {
            ret = new Double[values.length];
        } else if (innerType.startsWith("character")
                || innerType.equalsIgnoreCase(DATATYPE_STRING)) {
            ret = new String[values.length];
        } else if (innerType.equalsIgnoreCase(DATATYPE_TIME)) {
            ret = new java.sql.Time[values.length];
        } else if (innerType.equalsIgnoreCase(DATATYPE_DATE)) {
            ret = new java.sql.Date[values.length];
        } else if (innerType.equalsIgnoreCase(DATATYPE_TIMESTAMP)) {
            ret = new java.sql.Timestamp[values.length];
        } else if (innerType.equalsIgnoreCase(DATATYPE_DATETIME)) {
            ret = new java.sql.Timestamp[values.length];
        } else {
            ret = new String[values.length];
        }

        for (int i = 0; i < values.length; i++) {
            if (innerType.equalsIgnoreCase(DATATYPE_SMALLINT)
                    || innerType.equalsIgnoreCase(DATATYPE_INTEGER)) {
                ret[i] = new Integer(values[i].trim());
            } else if (innerType.equalsIgnoreCase(DATATYPE_BIGINT)) {
                ret[i] = new Long(values[i].trim());
            } else if (innerType.startsWith("numeric(") && innerType.endsWith(",0)")) {
                ret[i] = new Long(values[i].trim());
            } else if (innerType.equalsIgnoreCase(DATATYPE_FLOAT)) {
                ret[i] = new Double(values[i].trim());
            } else if (innerType.equalsIgnoreCase(DATATYPE_DOUBLE)
                    || innerType.equalsIgnoreCase(DATATYPE_MONETARY)) {
                ret[i] = new Double(values[i].trim());
            } else if (innerType.startsWith("numeric(") && !innerType.endsWith(",0)")) {
                ret[i] = new Double(values[i].trim());
            } else if (innerType.startsWith("character")
                    || innerType.equalsIgnoreCase(DATATYPE_STRING)) {
                ret[i] = values[i];
            } else if (innerType.equalsIgnoreCase(DATATYPE_TIME)) {
                ret[i] = java.sql.Time.valueOf(values[i].trim());
            } else if (innerType.equalsIgnoreCase(DATATYPE_DATE)) {
                ret[i] = java.sql.Date.valueOf(values[i].trim());
            } else if (innerType.equalsIgnoreCase(DATATYPE_TIMESTAMP)) {
                long time = DateUtil.getDatetime(values[i].trim());
                java.sql.Timestamp timestamp = new java.sql.Timestamp(time);
                ret[i] = timestamp;
            } else if (innerType.equalsIgnoreCase(DATATYPE_DATETIME)) {
                String formatValue =
                        DateUtil.formatDateTime(values[i], DBAttrTypeFormatter.DATETIME_FORMAT);
                if (formatValue == null) {
                    formatValue = values[i];
                }
                long time = DateUtil.getDatetime(formatValue);
                java.sql.Timestamp timestamp = new java.sql.Timestamp(time);
                ret[i] = timestamp;
            } else if (innerType.startsWith("bit(") || innerType.startsWith("bit varying(")) {
                ret[i] =
                        DBAttrTypeFormatter.formatValue(
                                innerType, values[i].trim(), isUseNULLValueSetting);
            } else {
                ret[i] = values[i].trim();
            }
        }
        return ret;
    }

    /**
     * Return Object[] array value from a collection value based the given data type, eg: data type:
     * integer, collection value: {1,2,3} return Object[]: Integer[]{1,2,3}
     *
     * @param type String The given type
     * @param values String The given value
     * @param isUseNULLValueSetting
     * @return Object[]
     */
    public static Object[] getCollectionValues(
            String type, Object[] values, boolean isUseNULLValueSetting) {
        String innerType = getTypeRemain(type);
        assert (innerType != null);
        if (innerType == null) {
            return null;
        }

        Object[] ret = null;
        if (innerType.equalsIgnoreCase(DATATYPE_SMALLINT)
                || innerType.equalsIgnoreCase(DATATYPE_INTEGER)) {
            ret = new Integer[values.length];
        } else if (innerType.equalsIgnoreCase(DATATYPE_BIGINT)) {
            ret = new Long[values.length];
        } else if (innerType.startsWith("numeric(") && innerType.endsWith(",0)")) {
            ret = new Long[values.length];
        } else if (innerType.equalsIgnoreCase(DATATYPE_FLOAT)) {
            ret = new Double[values.length];
        } else if (innerType.equalsIgnoreCase(DATATYPE_DOUBLE)
                || innerType.equalsIgnoreCase(DATATYPE_MONETARY)) {
            ret = new Double[values.length];
        } else if (innerType.startsWith("numeric(") && !innerType.endsWith(",0)")) {
            ret = new Double[values.length];
        } else if (innerType.startsWith("character")
                || innerType.equalsIgnoreCase(DATATYPE_STRING)) {
            ret = new String[values.length];
        } else if (innerType.equalsIgnoreCase(DATATYPE_TIME)) {
            ret = new java.sql.Time[values.length];
        } else if (innerType.equalsIgnoreCase(DATATYPE_DATE)) {
            ret = new java.sql.Date[values.length];
        } else if (innerType.equalsIgnoreCase(DATATYPE_TIMESTAMP)) {
            ret = new java.sql.Timestamp[values.length];
        } else if (innerType.equalsIgnoreCase(DATATYPE_DATETIME)) {
            ret = new java.sql.Timestamp[values.length];
        } else {
            ret = new String[values.length];
        }

        for (int i = 0; i < values.length; i++) {
            String value = values[i].toString().trim();
            if (innerType.equalsIgnoreCase(DATATYPE_SMALLINT)
                    || innerType.equalsIgnoreCase(DATATYPE_INTEGER)) {
                ret[i] = new Integer(value);
            } else if (innerType.equalsIgnoreCase(DATATYPE_BIGINT)) {
                ret[i] = new Long(value);
            } else if (innerType.startsWith("numeric(") && innerType.endsWith(",0)")) {
                ret[i] = new Long(value);
            } else if (innerType.equalsIgnoreCase(DATATYPE_FLOAT)) {
                ret[i] = new Double(value);
            } else if (innerType.equalsIgnoreCase(DATATYPE_DOUBLE)
                    || innerType.equalsIgnoreCase(DATATYPE_MONETARY)) {
                ret[i] = new Double(value);
            } else if (innerType.startsWith("numeric(") && !innerType.endsWith(",0)")) {
                ret[i] = new Double(value);
            } else if (innerType.startsWith("character")
                    || innerType.equalsIgnoreCase(DATATYPE_STRING)) {
                ret[i] = values[i];
            } else if (innerType.equalsIgnoreCase(DATATYPE_TIME)) {
                ret[i] = java.sql.Time.valueOf(value);
            } else if (innerType.equalsIgnoreCase(DATATYPE_DATE)) {
                ret[i] = java.sql.Date.valueOf(value);
            } else if (innerType.equalsIgnoreCase(DATATYPE_TIMESTAMP)) {
                long time;
                try {
                    time = DateUtil.getDatetime(value);
                    java.sql.Timestamp timestamp = new java.sql.Timestamp(time);
                    ret[i] = timestamp;
                } catch (ParseException e) {
                    ret[i] = null;
                }
            } else if (innerType.equalsIgnoreCase(DATATYPE_DATETIME)) {
                String formatValue =
                        DateUtil.formatDateTime(value, DBAttrTypeFormatter.DATETIME_FORMAT);
                if (formatValue == null) {
                    formatValue = value;
                }
                long time;
                try {
                    time = DateUtil.getDatetime(formatValue);
                    java.sql.Timestamp timestamp = new java.sql.Timestamp(time);
                    ret[i] = timestamp;
                } catch (ParseException e) {
                    ret[i] = null;
                }

            } else if (innerType.startsWith("bit(") || innerType.startsWith("bit varying(")) {
                ret[i] = DBAttrTypeFormatter.formatValue(innerType, value, isUseNULLValueSetting);
            } else {
                ret[i] = value;
            }
        }
        return ret;
    }

    /**
     * Retrieve whether the value is null when importing.
     *
     * @param type String
     * @param value Object value of field
     * @return is or not.
     */
    public static boolean isNullValueForImport(String type, Object value) {
        return (value == null)
                || (NULLVALUESLISTFORIMPORT.indexOf(
                                value.toString().toUpperCase(Locale.getDefault()).trim())
                        >= 0)
                || (DATATYPE_BLOB.equalsIgnoreCase(type)
                        && BLOB_EXPORT_FORMAT.equalsIgnoreCase(value.toString()))
                || (DATATYPE_CLOB.equalsIgnoreCase(type)
                        && CLOB_EXPORT_FORMAT.equalsIgnoreCase(value.toString()))
                || ((type.startsWith(DataType.DATATYPE_BIT)
                                || type.startsWith(DataType.DATATYPE_BIT_VARYING))
                        && BIT_EXPORT_FORMAT.equals(value.toString()));
    }

    /**
     * Set null values. Case ignore.
     *
     * @param values of null strings
     */
    public static void setNULLValuesForImport(String[] values) {
        NULLVALUESLISTFORIMPORT.clear();
        if (values == null) {
            return;
        }
        for (String value : values) {
            String result = value.toUpperCase(Locale.getDefault());
            if (NULLVALUESLISTFORIMPORT.indexOf(result) < 0) {
                NULLVALUESLISTFORIMPORT.add(result);
            }
        }
    }

    /**
     * Retrieve whether the value is null when exporting.
     *
     * @param type String
     * @param value Object value of field
     * @return is or not.
     */
    public static boolean isNullValueForExport(String type, Object value) {
        return (value == null)
                || (DATATYPE_BLOB.equalsIgnoreCase(type)
                        && BLOB_EXPORT_FORMAT.equalsIgnoreCase(value.toString()))
                || (DATATYPE_CLOB.equalsIgnoreCase(type)
                        && CLOB_EXPORT_FORMAT.equalsIgnoreCase(value.toString()))
                || ((type.startsWith(DataType.DATATYPE_BIT)
                                || type.startsWith(DataType.DATATYPE_BIT_VARYING))
                        && BIT_EXPORT_FORMAT.equals(value.toString()));
    }

    /**
     * Return this value whether self-defined
     *
     * @param value String
     * @return boolean
     */
    public static boolean isSelfDefinedData(String value) {
        return NULL_EXPORT_FORMAT.equals(value)
                || BLOB_EXPORT_FORMAT.equals(value)
                || CLOB_EXPORT_FORMAT.equals(value)
                || BIT_EXPORT_FORMAT.equals(value);
    }

    /**
     * The data type support size attribute.
     *
     * @param dataType String
     * @return true:support;
     */
    public static boolean isNotSupportSizeOrPrecision(String dataType) {
        if (DATATYPE_STRING.equals(dataType)
                || DATATYPE_OBJECT.equals(dataType)
                || DATATYPE_INT.equals(dataType)
                || DATATYPE_SHORT.equals(dataType)
                || DATATYPE_MONETARY.equals(dataType)
                || DATATYPE_TINYINT.equals(dataType)
                || DATATYPE_BIGINT.equals(dataType)
                || DATATYPE_SMALLINT.equals(dataType)
                || DATATYPE_INTEGER.equals(dataType)
                || DATATYPE_REAL.equals(dataType)
                || DATATYPE_FLOAT.equals(dataType)
                || DATATYPE_DOUBLE.equals(dataType)
                || DATATYPE_TIMESTAMP.equals(dataType)
                || DATATYPE_TIME.equals(dataType)
                || DATATYPE_DATE.equals(dataType)) {
            return true;
        }
        return false;
    }

    public static boolean isNumberType(String dataType) {
        String shownType = DataType.getShownType(DataType.getTypePart(dataType));
        if (DataType.DATATYPE_INTEGER.equals(shownType)
                || DataType.DATATYPE_TINYINT.equals(shownType)
                || DataType.DATATYPE_SMALLINT.equals(shownType)
                || DataType.DATATYPE_BIGINT.equals(shownType)
                || DataType.DATATYPE_DOUBLE.equals(shownType)
                || DataType.DATATYPE_FLOAT.equals(shownType)
                || DataType.DATATYPE_REAL.equals(shownType)
                || DataType.DATATYPE_NUMERIC.equals(shownType)
                || DataType.DATATYPE_DECIMAL.equals(shownType)
                || DataType.DATATYPE_MONETARY.equals(shownType)) {
            return true;
        }

        return false;
    }

    public static boolean isIntegerType(String dataType) {
        String shownType = DataType.getShownType(DataType.getTypePart(dataType));
        if (DataType.DATATYPE_INTEGER.equals(shownType)
                || DataType.DATATYPE_TINYINT.equals(shownType)
                || DataType.DATATYPE_SMALLINT.equals(shownType)
                || DataType.DATATYPE_BIGINT.equals(shownType)) {
            return true;
        }

        return false;
    }

    public static boolean canUseCollation(String dataType) {
        String shownType = DataType.getShownType(DataType.getTypePart(dataType));
        if (DataType.DATATYPE_STRING.equals(shownType)
                || DataType.DATATYPE_CHAR.equals(shownType)
                || DataType.DATATYPE_VARCHAR.equals(shownType)
                || DataType.DATATYPE_CHARACTER.equals(shownType)
                || DataType.DATATYPE_CHARACTER_VARYING.equals(shownType)
                || DataType.DATATYPE_NATIONAL_CHARACTER.equals(shownType)
                || DataType.DATATYPE_NATIONAL_CHARACTER_VARYING.equals(shownType)
                || DataType.DATATYPE_NCHAR.equals(shownType)
                || DataType.DATATYPE_NCHAR_VARYING.equals(shownType)) {
            return true;
        }

        return false;
    }

    public static boolean isStringType(String dataType) {
        return canUseCollation(dataType);
    }

    /**
     * Convert two single quotes('') to one single quote(') in default value. <br>
     * <b>Note: </b>Only suitable for convert default value returned from system table
     * <i>db_attribute</i>.
     *
     * @param dataType
     * @param origDefaultVal
     * @return
     */
    public static String convertDefaultValue(
            String dataType, String origDefaultVal, DatabaseInfo databaseInfo) {
        if (origDefaultVal != null && DataType.isStringType(dataType)) {
            if (CompatibleUtil.isAfter900(databaseInfo)) {
                if (isNationlStringType(dataType)) {
                    return "N'" + origDefaultVal + "'";
                } else {
                    return "'" + origDefaultVal + "'";
                }
            }
        }

        return origDefaultVal;
    }

    private static boolean isNationlStringType(String dataType) {
        String shownType = DataType.getShownType(DataType.getTypePart(dataType));
        if (DataType.DATATYPE_NATIONAL_CHARACTER.equals(shownType)
                || DataType.DATATYPE_NATIONAL_CHARACTER_VARYING.equals(shownType)
                || DataType.DATATYPE_NCHAR.equals(shownType)
                || DataType.DATATYPE_NCHAR_VARYING.equals(shownType)) {
            return true;
        }
        return false;
    }
    /**
     * Convert column type.
     *
     * @param dataType
     * @param prec
     * @param scale
     * @return
     */
    public static String convertAttrTypeString(String dataType, String prec, String scale) {
        String dt = dataType;
        if ("SHORT".equals(dt)) {
            dt = ("smallint");
        } else if ("STRING".equals(dt)) {
            dt = ("character varying" + "(" + prec + ")");
        } else if ("CHAR".equals(dt)) {
            dt = ("character(" + prec + ")");
        } else if ("VARCHAR".equals(dt)) {
            dt = ("character varying(" + prec + ")");
        } else if ("NCHAR".equals(dt)) {
            dt = ("national character(" + prec + ")");
        } else if ("VARNCHAR".equals(dt)) {
            dt = ("national character varying(" + prec + ")");
        } else if ("BIT".equals(dt)) {
            dt = ("bit(" + prec + ")");
        } else if ("VARBIT".equals(dt)) {
            dt = ("bit varying(" + prec + ")");
        } else if ("NUMERIC".equals(dt)) {
            dt = ("numeric(" + prec + "," + scale + ")");
        } else if ("SET".equals(dt)) {
            dt = ("set_of");
        } else if ("MULTISET".equals(dt)) {
            dt = ("multiset_of");
        } else if ("SEQUENCE".equals(dt)) {
            dt = ("sequence_of");
        } else {
            dt = (dt.toLowerCase(Locale.getDefault()));
        }
        return dt;
    }

    public static boolean isClobDataType(String type) {
        if (StringUtil.isEmpty(type)) {
            return false;
        }
        String upperType = StringUtil.toUpper(type);
        if (upperType.startsWith(DataType.DATATYPE_CLOB)) {
            return true;
        }
        return false;
    }

    public static boolean isTimeDataType(String type) {
        if (StringUtil.isEmpty(type)) {
            return false;
        }
        String upperType = StringUtil.toUpper(type);
        if (upperType.startsWith(DataType.DATATYPE_TIME)) {
            return true;
        }
        return false;
    }

    public static boolean isDateDataType(String type) {
        if (StringUtil.isEmpty(type)) {
            return false;
        }
        String upperType = StringUtil.toUpper(type);
        if (upperType.startsWith(DataType.DATATYPE_DATE)) {
            return true;
        }
        return false;
    }

    public static boolean isTimeStampDataType(String type) {
        if (StringUtil.isEmpty(type)) {
            return false;
        }
        String upperType = StringUtil.toUpper(type);
        if (upperType.startsWith(DataType.DATATYPE_TIMESTAMP)) {
            return true;
        }
        return false;
    }

    public static boolean isDateTimeDataType(String type) {
        if (StringUtil.isEmpty(type)) {
            return false;
        }
        String upperType = StringUtil.toUpper(type);
        if (upperType.startsWith(DataType.DATATYPE_DATETIME)) {
            return true;
        }
        return false;
    }

    public static boolean isStringDataType(String type) {
        if (StringUtil.isEmpty(type)) {
            return false;
        }
        String upperType = StringUtil.toUpper(type);
        if (upperType.startsWith(DataType.DATATYPE_STRING)) {
            return true;
        }
        return false;
    }

    /**
     * If the data type does not have length or precise , modify the type and return new. <br>
     * BIT->BIT(1), BIT VARYING(1073741823)<br>
     * <br>
     * CHAR->CHAR(1), NCHAR->NCHAR(1), NCHAR VARYING-> NCHAR VARYING(536870911),
     * VARCHAR->VARCHAR(1073741823)<br>
     * <br>
     * STRING->VARCHAR(1073741823)<br>
     *
     * @param type show type.
     * @return String revised show type.
     */
    public static String reviseDataType(String type) {

        if (DATATYPE_CHAR.equalsIgnoreCase(type)
                || DATATYPE_NCHAR.equalsIgnoreCase(type)
                || DATATYPE_BIT.equalsIgnoreCase(type)) {
            type += "(" + CHAR_DEFAULT_LENGTH + ")";
        } else if (DATATYPE_NCHAR_VARYING.equalsIgnoreCase(type)) {
            type += "(" + NCHAR_VARYING_DEFAULT_LENGTH + ")";
        } else if (DATATYPE_VARCHAR.equalsIgnoreCase(type)) {
            type += "(" + VARCHAR_DEFAULT_LENGTH + ")";
        } else if (DATATYPE_BIT_VARYING.equalsIgnoreCase(type)) {
            type += "(" + BIT_VARYING_DEFAULT_LENGTH + ")";
        } else if (DATATYPE_STRING.equalsIgnoreCase(type)) {
            type = reviseDataType(DATATYPE_VARCHAR);
        }

        return type;
    }

    public static boolean isBlobDataType(String type) {
        if (StringUtil.isEmpty(type)) {
            return false;
        }
        String upperType = StringUtil.toUpper(type);
        if (upperType.startsWith(DataType.DATATYPE_BLOB)) {
            return true;
        }
        return false;
    }

    public static boolean isBitDataType(String type) {
        if (StringUtil.isEmpty(type)) {
            return false;
        }
        String upperType = StringUtil.toUpper(type);
        if (upperType.startsWith(DataType.DATATYPE_BIT)) {
            return true;
        }
        return false;
    }

    public static boolean isBitVaryingDataType(String type) {
        if (StringUtil.isEmpty(type)) {
            return false;
        }
        String upperType = StringUtil.toUpper(type);
        if (upperType.startsWith(DataType.DATATYPE_BIT_VARYING)) {
            return true;
        }
        return false;
    }

    public static boolean isNumericType(String dataType) {
        String shownType = DataType.getShownType(DataType.getTypePart(dataType));
        if (DataType.DATATYPE_NUMERIC.equals(shownType)
                || DataType.DATATYPE_DECIMAL.equals(shownType)
                || DataType.DATATYPE_MONETARY.equals(shownType)) {
            return true;
        }

        return false;
    }

    public static boolean isFloatType(String dataType) {
        String shownType = DataType.getShownType(DataType.getTypePart(dataType));
        if (DataType.DATATYPE_FLOAT.equals(shownType)) {
            return true;
        }

        return false;
    }

    /**
     * <real_enum_type> : ENUM '(' <char_string_literal_list> ')' <char_string_literal_list> :
     * <char_string_literal_list> ',' CHAR_STRING | CHAR_STRING
     *
     * @param showType as : enum('...', '...')
     * @return
     */
    public static boolean isValidEnumShowType(String showType) {

        if (StringUtil.isEmpty(showType)) {
            return false;
        }

        showType = showType.trim();
        if (!showType.startsWith(enumMapping[1])) { // "enum"
            return false;
        }
        String regex =
                "^"
                        + enumMapping[1]
                        + "\\((\\s*\\'[a-zA-Z0-9_]+\\'\\s*\\,)*(\\s*\\'[a-zA-Z0-9_]+\\'\\s*){1}\\)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(showType);
        if (!matcher.find()) {
            return false;
        }

        return true;
    }

    /**
     * If the type is enum, then return values behind the string of "enum". <br>
     * If not, return null.
     *
     * @param showType
     * @return
     */
    public static String getEnumeration(String showType) {
        if (isValidEnumShowType(showType)) {
            return showType.replaceFirst(enumMapping[1], "");
        }

        return null;
    }

    /**
     * Convert the lower shown type to upper shown type.
     *
     * @param lowerShowType
     * @return String
     */
    public static String getUpperShowType(String lowerShowType) {
        if (lowerShowType.startsWith(enumMapping[1])) {
            return lowerShowType.replaceFirst(enumMapping[1], enumMapping[0]);
        } else {
            return lowerShowType.toUpperCase();
        }
    }

    /** @return String lower string of "enum" */
    public static String getLowerEnumType() {
        return enumMapping[1];
    }

    /** @return String upper string of "ENUM" */
    public static String getUpperEnumType() {
        return enumMapping[0];
    }
}
