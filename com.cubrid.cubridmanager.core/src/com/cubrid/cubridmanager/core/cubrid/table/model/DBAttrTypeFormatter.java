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

import com.cubrid.common.core.util.DateUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.Messages;
import com.cubrid.jdbc.proxy.driver.CUBRIDBlobProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDClobProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDConnectionProxy;
import com.cubrid.jdbc.proxy.manage.CUBRIDProxyException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;

/**
 * Format and check data by CUBRID table attribute type
 *
 * @author pangqiren
 * @version 1.0 - 2010-7-22 created by pangqiren
 */
public final class DBAttrTypeFormatter {

    private static final Logger LOGGER = LogUtil.getLogger(DBAttrTypeFormatter.class);
    public static final String FILE_URL_PREFIX = "file:";
    // The size for data type, determine whether can import data by file or by big t
    public static final long MUCH_VALUE_TYPE_LENGTH = 50;
    /** the standard format of time, date, timestamp and datetime used in SQL */
    private static final String TIME_FORMAT = "HH:mm:ss";

    private static final String DATE_FORMAT = "MM/dd/yyyy";
    //	private static final String TIMESTAMP_FORMAT = "MM/dd/yyyy HH:mm:ss";
    public static final String TIMESTAMP_FORMAT = DateUtil.TIMESTAMP_FORMAT;
    public static final String DATETIME_FORMAT = DateUtil.DATETIME_FORMAT;

    private static final String[] OTHER_DATETIME_FORMATS = {
        "MM/dd",
        "MM/dd/yyyy",
        "MM/dd HH:mm",
        "MM/dd/yyyy HH:mm",
        "MM/dd HH:mm:ss",
        "MM/dd HH:mm:ss.SSS",
        "MM/dd/yyyy HH:mm:ss",
        "MM/dd a",
        "MM/dd/yyyy a",
        "MM/dd hh:mm a",
        "MM/dd/yyyy hh:mm a",
        "MM/dd HH:mm:ss a",
        "MM/dd hh:mm:ss.SSS a",
        "MM/dd/yyyy hh:mm:ss a",
        "MM/dd/yyyy hh:mm:ss.SSS a",
        "MM-dd",
        "yyyy-MM-dd",
        "MM-dd HH:mm",
        "yyyy-MM-dd HH:mm",
        "MM-dd HH:mm:ss",
        "MM-dd HH:mm:ss.SSS",
        "yyyy-MM-dd HH:mm:ss",
        "MM-dd a",
        "yyyy-MM-dd a",
        "MM-dd hh:mm a",
        "yyyy-MM-dd hh:mm a",
        "MM-dd hh:mm:ss a",
        "MM-dd hh:mm:ss.SSS a",
        "HH:mm:ss MM/dd",
        "hh:mm:ss a MM/dd",
        "HH:mm MM/dd",
        "HH:mm MM/dd/yyyy",
        "HH:mm:ss.SSS MM/dd",
        "hh:mm a MM/dd",
        "HH:mm:ss a MM/dd",
        "hh:mm:ss.SSS a MM/dd",
        "HH:mm MM-dd",
        "HH:mm yyyy-MM-dd",
        "HH:mm:ss MM-dd",
        "HH:mm:ss.SSS MM-dd",
        "HH:mm:ss yyyy-MM-dd",
        "hh:mm a MM-dd",
        "hh:mm a yyyy-MM-dd",
        "hh:mm:ss a MM-dd",
        "hh:mm:ss.SSS a MM-dd",
        "hh:mm:ss a yyyy-MM-dd",
        "hh:mm:ss.SSS a yyyy-MM-dd"
    };

    private static final String[] OTHER_TIMESTAMP_FORMATS = {
        "HH:mm:ss MM/dd", "MM/dd HH:mm:ss", "hh:mm:ss a MM/dd", "MM/dd hh:mm:ss a"
    };

    private DBAttrTypeFormatter() {}

    /**
     * Format the given attribute
     *
     * @param attrType String The given attribute type
     * @param attrValue String The given attribute value
     * @param isUseNULLValueSetting
     * @return String the formated result
     */
    public static String formatValue(
            String attrType, String attrValue, boolean isUseNULLValueSetting) {
        return format(attrType, attrValue, isUseNULLValueSetting).getFormatResult();
    }

    /**
     * Format the given attribute for input/edit data
     *
     * @param attrType String The given attribute type
     * @param attrValue String The given attribute value
     * @param isUseNULLValueSetting
     * @return String the formated result
     */
    public static String formatValueForInput(
            String attrType, String attrValue, boolean isUseNULLValueSetting) {
        return formatForInput(attrType, attrValue, isUseNULLValueSetting).getFormatResult();
    }

    /**
     * try to validate whether attribute value is aligned with the given data type
     *
     * @param attrType String The attribute type
     * @param attrValue String The attribute value
     * @param isUseNULLValueSetting
     * @return boolean true if the attribute value is aligned,false if otherwise.
     */
    public static boolean validateAttributeValue(
            String attrType, String attrValue, boolean isUseNULLValueSetting) {
        return formatForInput(attrType, attrValue, isUseNULLValueSetting).isSuccess();
    }

    /**
     * Validate the type
     *
     * @param type The String
     * @return boolean
     */
    public static boolean validateAttributeType(String type) {
        if (type == null || type.trim().length() == 0) {
            return false;
        }

        String attrType = type.trim().toUpperCase();

        //		if (attrType.equals(DataType.DATATYPE_STRING)
        //				||attrType.equals(DataType.DATATYPE_VARCHAR)
        //				||attrType.equals(DataType.DATATYPE_CHAR)
        //				||attrType.equals(DataType.DATATYPE_CHARACTER)
        //				||attrType.equals(DataType.DATATYPE_CHARACTER_VARYING)
        //				||attrType.equals(DataType.DATATYPE_INTEGER)
        //				||attrType.equals(DataType.DATATYPE_SHORT)
        //				||attrType.equals(DataType.DATATYPE_TINYINT)
        //				||attrType.equals(DataType.DATATYPE_DATETIME)
        //				||attrType.equals(DataType.DATATYPE_BIGINT)
        //				||attrType.equals(DataType.DATATYPE_DECIMAL)
        //				||attrType.equals(DataType.DATATYPE_REAL)
        //				||attrType.equals(DataType.DATATYPE_FLOAT)
        //				||attrType.equals(DataType.DATATYPE_TIMESTAMP)
        //				||attrType.equals(DataType.DATATYPE_TIME)
        //				||attrType.equals(DataType.DATATYPE_DATE)
        //				||attrType.equals(DataType.DATATYPE_SMALLINT)
        //				||attrType.equals(DataType.DATATYPE_BLOB)
        //				||attrType.equals(DataType.DATATYPE_CLOB)
        //				||attrType.equals(DataType.DATATYPE_CURSOR)
        //				||attrType.equals(DataType.BLOB_EXPORT_FORMAT)
        //				||attrType.equals(DataType.BLOB_EXPORT_FORMAT)
        //				||attrType.equals(DataType.CLOB_EXPORT_FORMAT)
        //				||attrType.equals(DataType.BIT_EXPORT_FORMAT)
        //				||attrType.equals(DataType.DATATYPE_CURRENCY)
        //				||attrType.equals(DataType.DATATYPE_DOUBLE)
        //				||attrType.equals(DataType.DATATYPE_BIT)
        //				||attrType.equals(DataType.DATATYPE_BIT_VARYING)
        //				||attrType.equals(DataType.DATATYPE_MONETARY)
        //				||attrType.equals(DataType.DATATYPE_OBJECT)) {
        //			return true;
        //		}

        if (attrType.startsWith(DataType.DATATYPE_NUMERIC)) {
            Pattern pattern = Pattern.compile("^NUMERIC\\((\\d+),(\\d+)\\)$");
            Matcher matcher = pattern.matcher(attrType);
            if (matcher.matches()) {
                int presion = Integer.parseInt(matcher.group(1));
                int scale = Integer.parseInt(matcher.group(2));
                if (presion - scale <= 0) {
                    return false;
                }
                return true;
            } else {
                return false;
            }
        }
        String[] numTypes = {
            DataType.DATATYPE_CHAR,
            DataType.DATATYPE_VARCHAR,
            DataType.DATATYPE_BIT_VARYING,
            DataType.DATATYPE_BIT,
            DataType.DATATYPE_NCHAR_VARYING,
            DataType.DATATYPE_NCHAR
        };
        for (String numType : numTypes) {
            if (attrType.startsWith(numType)) {
                Pattern pattern = Pattern.compile("^" + numType + "\\((\\d+)\\)$");
                Matcher matcher = pattern.matcher(attrType);
                if (matcher.matches()) {
                    int presion = Integer.parseInt(matcher.group(1));
                    if (presion <= 0) {
                        return false;
                    }
                    return true;
                } else {
                    return false;
                }
            }
        }
        String[] setTypes = {
            DataType.DATATYPE_SET, DataType.DATATYPE_MULTISET, DataType.DATATYPE_SEQUENCE
        };
        for (String setType : setTypes) {
            if (attrType.startsWith(setType)) {
                int index = attrType.indexOf("(");
                int lastIndex = attrType.lastIndexOf(")");
                if (index == -1 || lastIndex == -1 || index >= lastIndex) {
                    return false;
                }
                String subTypeStr = attrType.substring(index + 1, lastIndex);
                String[] subTypes = subTypeStr.split(",");
                int length = subTypes.length;
                for (int i = 0; i < length; i++) {
                    String subType = subTypes[i];
                    if (subType.trim().matches("^NUMERIC\\(\\d+$")
                            && i + 1 < length
                            && subTypes[i + 1].trim().matches("^\\d+\\$")) {
                        subType = subType.trim() + "," + subTypes[i + 1].trim();
                        i++;
                    }
                    if (!validateAttributeType(subType)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * to format customs' many types of attribute default value into standard attribute default
     * value
     *
     * @param attrType String attribute type
     * @param attrValue String attribute default value
     * @param isUseNULLValueSetting
     * @return FormatDataResult
     * @throws ParseException
     */
    public static FormatDataResult format(
            String attrType, String attrValue, boolean isUseNULLValueSetting) {
        return format(
                attrType, attrValue, true, StringUtil.getDefaultCharset(), isUseNULLValueSetting);
    }

    /**
     * to format customs' many types of attribute default value into standard attribute default
     * value for input/edit data
     *
     * @param attrType String attribute type
     * @param attrValue String attribute default value
     * @param isUseNULLValueSetting
     * @return FormatDataResult
     * @throws ParseException
     */
    public static FormatDataResult formatForInput(
            String attrType, String attrValue, boolean isUseNULLValueSetting) {
        return formatForInput(
                attrType, attrValue, true, StringUtil.getDefaultCharset(), isUseNULLValueSetting);
    }

    public static FormatDataResult format(
            String attrType,
            String attrValue,
            String pattern,
            boolean isIncludeKeyWord,
            String charSet,
            boolean isUseNULLValueSetting) {
        // Format use pattern
        if (!StringUtil.isEmpty(pattern) && DataType.DATATYPE_DATE.equalsIgnoreCase(attrType)) {
            FormatDataResult result = new FormatDataResult();
            String finAttrValue = attrValue;
            formatDate(finAttrValue, pattern, result, isIncludeKeyWord);

            return result;
        }

        return format(attrType, attrValue, isIncludeKeyWord, charSet, isUseNULLValueSetting);
    }
    /**
     * to format customs' many types of attribute default value into standard attribute default
     * value
     *
     * @param attrType String attribute type
     * @param attrValue String attribute default value
     * @param isIncludeKeyWord boolean whether allow to include keyword
     * @param charSet String
     * @param isUseNULLValueSetting
     * @return FormatDataResult
     * @throws ParseException
     */
    public static FormatDataResult format(
            String attrType,
            String attrValue,
            boolean isIncludeKeyWord,
            String charSet,
            boolean isUseNULLValueSetting) {
        FormatDataResult result = new FormatDataResult();
        String upperType = attrType.toUpperCase(Locale.getDefault());
        String finAttrValue = attrValue;

        if (isUseNULLValueSetting && DataType.isNullValueForImport(upperType, finAttrValue)) {
            finAttrValue = null;
        }

        if (upperType.startsWith(DataType.DATATYPE_DATETIME)) {
            formatDateTime(finAttrValue, result, isIncludeKeyWord);
        } else if (upperType.startsWith(DataType.DATATYPE_TIMESTAMP)) {
            formatTimeStamp(finAttrValue, result, isIncludeKeyWord);
        } else if (upperType.startsWith(DataType.DATATYPE_DATE)) {
            formatDate(finAttrValue, result, isIncludeKeyWord);
        } else if (upperType.startsWith(DataType.DATATYPE_TIME)) {
            formatTime(finAttrValue, result, isIncludeKeyWord);
        } else if (upperType.startsWith(DataType.DATATYPE_CHAR)
                || upperType.startsWith(DataType.DATATYPE_VARCHAR)) {
            formatChar(upperType, finAttrValue, result);
        } else if (upperType.equals(DataType.DATATYPE_STRING)) {
            formatString(finAttrValue, result);
        } else if (upperType.equals(DataType.DATATYPE_ENUM)) {
            formatEnum(finAttrValue, result);
        } else if (upperType.startsWith(DataType.DATATYPE_INTEGER)) {
            formatInteger(finAttrValue, result);
        } else if (upperType.startsWith(DataType.DATATYPE_SMALLINT)) {
            formatSmallInt(finAttrValue, result);
        } else if (upperType.startsWith(DataType.DATATYPE_BIGINT)) {
            formatBigInt(finAttrValue, result);
        } else if (upperType.startsWith(DataType.DATATYPE_NUMERIC)) {
            formatNumeric(upperType, finAttrValue, result);
        } else if (upperType.startsWith(DataType.DATATYPE_FLOAT)) {
            formatFloat(finAttrValue, result);
        } else if (upperType.startsWith(DataType.DATATYPE_DOUBLE)) {
            formatDouble(finAttrValue, result);
        } else if (upperType.startsWith(DataType.DATATYPE_MONETARY)) {
            formatMonetary(finAttrValue, result);
        } else if (upperType.startsWith(DataType.DATATYPE_NCHAR)
                || upperType.startsWith(DataType.DATATYPE_NATIONAL_CHARACTER)) {
            formatNchar(upperType, finAttrValue, result, charSet);
        } else if (upperType.startsWith(DataType.DATATYPE_BIT)
                || upperType.startsWith(DataType.DATATYPE_BIT_VARYING)) {
            formatBit(upperType, finAttrValue, result, charSet);
        } else if (upperType.startsWith(DataType.DATATYPE_SET)
                || upperType.startsWith(DataType.DATATYPE_MULTISET)
                || upperType.startsWith(DataType.DATATYPE_SEQUENCE)) {
            formatSet(upperType, finAttrValue, result, isUseNULLValueSetting);
        } else if (upperType.startsWith(DataType.DATATYPE_BLOB)) {
            formatBLob(finAttrValue, result);
        } else if (upperType.startsWith(DataType.DATATYPE_CLOB)) {
            formatCLob(finAttrValue, result);
        } else {
            result.formatedString = finAttrValue;
            result.success = true;
        }
        return result;
    }

    /**
     * to format customs' many types of attribute default value into standard attribute default
     * value for input/edit data
     *
     * @param attrType String attribute type
     * @param attrValue String attribute default value
     * @param isIncludeKeyWord boolean whether allow to include keyword
     * @param charSet String
     * @param isUseNULLValueSetting
     * @return FormatDataResult
     * @throws ParseException
     */
    public static FormatDataResult formatForInput(
            String attrType,
            String attrValue,
            boolean isIncludeKeyWord,
            String charSet,
            boolean isUseNULLValueSetting) {
        FormatDataResult result = null;
        String upperType = attrType.toUpperCase(Locale.getDefault());
        String finAttrValue = attrValue;

        if (isUseNULLValueSetting && DataType.isNullValueForImport(upperType, finAttrValue)) {
            finAttrValue = null;
        }

        if (upperType.startsWith(DataType.DATATYPE_CHAR)
                || upperType.startsWith(DataType.DATATYPE_VARCHAR)) {
            result = new FormatDataResult();
            formatCharForInput(upperType, finAttrValue, result);
        } else if (upperType.equals(DataType.DATATYPE_STRING)) {
            result = new FormatDataResult();
            formatStringForInput(finAttrValue, result);
        } else if (upperType.startsWith(DataType.DATATYPE_NCHAR)
                || upperType.startsWith(DataType.DATATYPE_NATIONAL_CHARACTER)) {
            result = new FormatDataResult();
            formatNcharForInput(upperType, finAttrValue, result, charSet);
        } else {
            result = format(attrType, attrValue, isIncludeKeyWord, charSet, isUseNULLValueSetting);
        }
        return result;
    }

    /**
     * Format the double value
     *
     * @param attrValue String The attribute value
     * @param result FormatDataResult the given instance of FormatDataResult
     */
    public static void formatDouble(String attrValue, FormatDataResult result) {
        if (attrValue == null || "".equals(attrValue)) {
            result.success = true;
            result.formatedString = null;
            result.formatedJavaObj = null;
        } else {
            try {
                double parseDouble = Double.parseDouble(attrValue);
                result.success = !Double.isInfinite(parseDouble);
                result.formatedString = result.success ? attrValue : null;
                result.formatedJavaObj = result.success ? parseDouble : null;
            } catch (NumberFormatException e) {
                result.success = false;
                result.formatedString = attrValue;
            }
        }
    }

    /**
     * * Format the float value
     *
     * @param attrValue String The attribute value
     * @param result FormatDataResult the given instance of FormatDataResult
     */
    public static void formatFloat(String attrValue, FormatDataResult result) {
        if (attrValue == null || "".equals(attrValue)) {
            result.success = true;
            result.formatedString = null;
            result.formatedJavaObj = null;
        } else {
            try {
                Float.parseFloat(attrValue);
                result.success = true;
                result.formatedString = attrValue;
                result.formatedJavaObj = new Float(attrValue);
            } catch (NumberFormatException e) {
                result.success = false;
                result.formatedString = attrValue;
            }
        }
    }

    /**
     * Format the Big int value
     *
     * @param attrValue String The attribute value
     * @param result FormatDataResult the given instance of FormatDataResult
     */
    public static void formatBigInt(String attrValue, FormatDataResult result) {
        if (attrValue == null || "".equals(attrValue)) {
            result.success = true;
            result.formatedString = null;
            result.formatedJavaObj = null;
        } else {
            try {
                Long.parseLong(attrValue);
                result.success = true;
                result.formatedString = attrValue;
                result.formatedJavaObj = new Long(attrValue);
            } catch (NumberFormatException e) {
                result.success = false;
                result.formatedString = attrValue;
            }
        }
    }

    /**
     * Format small int value
     *
     * @param attrValue String The attribute value
     * @param result FormatDataResult the given instance of FormatDataResult
     */
    public static void formatSmallInt(String attrValue, FormatDataResult result) {
        if (attrValue == null || "".equals(attrValue)) {
            result.success = true;
            result.formatedString = null;
            result.formatedJavaObj = null;
        } else {
            try {
                Short.parseShort(attrValue);
                result.success = true;
                result.formatedString = attrValue;
                result.formatedJavaObj = new Short(attrValue);
            } catch (NumberFormatException e) {
                result.success = false;
                result.formatedString = attrValue;
            }
        }
    }

    /**
     * Format Integer value
     *
     * @param attrValue String The attribute value
     * @param result FormatDataResult the given instance of FormatDataResult
     */
    public static void formatInteger(String attrValue, FormatDataResult result) {
        if (attrValue == null || "".equals(attrValue)) {
            result.success = true;
            result.formatedString = null;
            result.formatedJavaObj = null;
        } else {
            try {
                Integer.parseInt(attrValue);
                result.success = true;
                result.formatedString = attrValue;
                result.formatedJavaObj = new Integer(attrValue);
            } catch (NumberFormatException e) {
                result.success = false;
                result.formatedString = attrValue;
            }
        }
    }

    /**
     * Format Nchar value
     *
     * @param attType String The attribute type
     * @param attrValue String The attribute value
     * @param charSet String
     * @param result FormatDataResult the given instance of FormatDataResult
     */
    public static void formatNchar(
            String attType, String attrValue, FormatDataResult result, String charSet) {
        String size = null;

        try {
            size = attType.substring(attType.indexOf("(") + 1, attType.indexOf(")"));
        } catch (Exception e) {
            size = "" + DataType.STRING_MAX_SIZE;
        }
        if (attrValue == null) {
            result.success = true;
            result.formatedString = null;
        } else if ("".equals(attrValue)) {
            result.success = true;
            result.formatedString = attrValue;
            result.formatedJavaObj = new byte[] {};
        } else {
            String contain = "";
            String javaObjStr = attrValue;
            if (attrValue.startsWith("N'")
                    && attrValue.endsWith("'")
                    && attrValue.indexOf("'") != attrValue.lastIndexOf("'")) {
                contain =
                        attrValue.substring(attrValue.indexOf("'") + 1, attrValue.lastIndexOf("'"));
                javaObjStr = contain;
            } else {
                contain = attrValue.replaceAll("'", "''");
            }
            int valueSize = javaObjStr.getBytes().length;
            if (Integer.parseInt(size) >= valueSize) {
                result.success = true;
                try {
                    result.formatedJavaObj = javaObjStr.getBytes(charSet);
                } catch (UnsupportedEncodingException e) {
                    LOGGER.error("", e);
                }
            } else {
                result.success = false;
            }
            result.formatedString = "N'" + contain + "'";
        }
    }

    /**
     * Format String value
     *
     * @param attrValue String The attribute value
     * @param result FormatDataResult the given instance of FormatDataResult
     */
    public static void formatString(String attrValue, FormatDataResult result) {
        String containValue = "";
        if (attrValue == null || "".equals(attrValue)) {
            result.success = true;
            result.formatedString = attrValue;
            result.formatedJavaObj = attrValue;
        } else {
            String javaObjStr = attrValue;
            if (attrValue.startsWith("'") && attrValue.endsWith("'") && attrValue.length() > 1) {
                containValue =
                        attrValue.substring(attrValue.indexOf("'") + 1, attrValue.lastIndexOf("'"));
                javaObjStr = containValue;
            } else {
                containValue = attrValue.replaceAll("'", "''");
            }
            result.success = true;
            result.formatedString = "'" + containValue + "'";
            result.formatedJavaObj = javaObjStr;
        }
    }

    /**
     * Format numeric value
     *
     * @param attrType String The attribute type
     * @param attrValue String The attribute value
     * @param result FormatDataResult the given instance of FormatDataResult
     */
    public static void formatNumeric(String attrType, String attrValue, FormatDataResult result) {
        String precision = null;
        try {
            precision = attrType.substring(attrType.indexOf("(") + 1, attrType.indexOf(","));
        } catch (Exception e) {
            precision = "" + DataType.NUMERIC_DEFAULT_PRECISION;
        }

        if (attrValue == null || "".equals(attrValue)) {
            result.success = true;
            result.formatedString = null;
            result.formatedJavaObj = null;
            return;
        }
        if (-1 == attrType.indexOf(",0)")) {
            String scale = attrType.substring(attrType.indexOf(",") + 1, attrType.indexOf(")"));
            int diff = Integer.parseInt(precision) - Integer.parseInt(scale);
            String reg = "^[\\+-]?\\d{1," + diff + "}(\\.\\d{1," + scale + "})?$";
            if (diff == 0) {
                reg = "^[\\+-]?\\d{0,1}(\\.\\d{1," + scale + "})?$";
            }
            result.success = attrValue.matches(reg) ? true : false;
        } else {
            String reg = "^[\\+-]?\\d{1," + precision + "}$";
            result.success = attrValue.matches(reg) ? true : false;
        }
        result.formatedString = attrValue;
        if (result.success) {
            result.formatedJavaObj = new Double(attrValue);
        }
    }

    /**
     * Format date time value
     *
     * @param attrValue String The attribute value
     * @param result FormatDataResult the given instance of FormatDataResult
     * @param isIncludeKeyWord boolean whether allow to include keyword
     */
    public static void formatDateTime(
            String attrValue, FormatDataResult result, boolean isIncludeKeyWord) {
        if (attrValue == null || "".equals(attrValue)) {
            result.success = true;
            result.formatedString = attrValue;
        } else if (isIncludeKeyWord) {
            if (attrValue.equalsIgnoreCase("sysdatetime")
                    || attrValue.equalsIgnoreCase("sys_datetime")) {
                result.success = true;
                result.formatedString = "sysdatetime";
            } else if (attrValue.equalsIgnoreCase("currentdatetime")
                    || attrValue.equalsIgnoreCase("current_datetime")) {
                result.success = true;
                result.formatedString = "current_datetime";
            } else if (attrValue.trim().toUpperCase().startsWith(DataType.DATATYPE_DATETIME)) {
                String str = attrValue.trim().substring("datetime".length()).trim();
                if (str.startsWith("'") && str.endsWith("'") && str.length() > 2) {
                    str = str.substring(1, str.length() - 1);
                    formatDateTime(str, result, isIncludeKeyWord);
                }
            }
        }
        if (result.isSuccess()) {
            return;
        }
        try {
            String formatValue = DateUtil.formatDateTime(attrValue, DATETIME_FORMAT);
            if (formatValue == null) {
                formatValue = attrValue;
            }
            long datetime = DateUtil.getDatetime(formatValue);
            result.success = true;
            result.formatedString =
                    DataType.DATATYPE_DATETIME
                            + "'"
                            + DateUtil.getDatetimeString(datetime, DATETIME_FORMAT)
                            + "'";
            result.formatedJavaObj = new java.sql.Timestamp(datetime);
        } catch (ParseException e) {
            result.success = false;
            for (int i = 0; i < OTHER_DATETIME_FORMATS.length; i++) {
                if (DateUtil.validateTimestamp(attrValue, OTHER_DATETIME_FORMATS[i])) {
                    result.success = true;
                    result.formatedString = DataType.DATATYPE_DATETIME + "'" + attrValue + "'";
                    try {
                        result.formatedJavaObj =
                                new java.sql.Timestamp(
                                        DateUtil.getTimestamp(
                                                attrValue, OTHER_DATETIME_FORMATS[i]));
                    } catch (ParseException e1) {
                        LOGGER.error("", e1);
                    }
                    break;
                }
            }
        }
    }

    /**
     * Format date value
     *
     * @param attrValue String The attribute value
     * @param result FormatDataResult the given instance of FormatDataResult
     * @param isIncludeKeyWord boolean whether allow to include keyword
     */
    public static void formatDate(
            String attrValue, FormatDataResult result, boolean isIncludeKeyWord) {
        if (attrValue == null || "".equals(attrValue)) {
            result.success = true;
            result.formatedString = attrValue;
        } else if (isIncludeKeyWord) {
            if (attrValue.equalsIgnoreCase("sysdate") || attrValue.equalsIgnoreCase("sys_date")) {
                result.success = true;
                result.formatedString = "sysdate";
            } else if (attrValue.equalsIgnoreCase("currentdate")
                    || attrValue.equalsIgnoreCase("current_date")) {
                result.success = true;
                result.formatedString = "current_date";
            } else if (attrValue
                    .toUpperCase(Locale.getDefault())
                    .startsWith(DataType.DATATYPE_DATE)) {
                String str = attrValue.trim().substring("date".length()).trim();
                if (str.startsWith("'") && str.endsWith("'") && str.length() > 2) {
                    str = str.substring(1, str.length() - 1);
                    formatDate(str, result, isIncludeKeyWord);
                }
            }
        }
        if (result.isSuccess()) {
            return;
        }
        try {
            long timestamp = DateUtil.getDate(attrValue);
            result.success = true;
            result.formatedString =
                    DataType.DATATYPE_DATE
                            + "'"
                            + DateUtil.getDatetimeString(timestamp, DATE_FORMAT)
                            + "'";
            result.formatedJavaObj = new java.sql.Date(timestamp);
        } catch (ParseException e) {
            // useless now, because DateUtil.SUPPORTED_DATE_PATTERNS already support this type
            if (DateUtil.validateTimestamp(attrValue, "MM/dd")) {
                result.success = true;
                result.formatedString = DataType.DATATYPE_DATE + "'" + attrValue + "'";
                try {
                    result.formatedJavaObj =
                            new java.sql.Date(DateUtil.getTimestamp("MM/dd", attrValue));
                } catch (ParseException e1) {
                    LOGGER.error("", e1);
                }
            } else {
                result.success = false;
            }
        }
    }

    /**
     * Format date value
     *
     * @param attrValue String The attribute value
     * @param pattern
     * @param result FormatDataResult the given instance of FormatDataResult
     * @param isIncludeKeyWord boolean whether allow to include keyword
     */
    public static void formatDate(
            String attrValue, String pattern, FormatDataResult result, boolean isIncludeKeyWord) {
        if (attrValue == null || "".equals(attrValue)) {
            result.success = true;
            result.formatedString = attrValue;
        } else if (isIncludeKeyWord) {
            if (attrValue.equalsIgnoreCase("sysdate") || attrValue.equalsIgnoreCase("sys_date")) {
                result.success = true;
                result.formatedString = "sysdate";
            } else if (attrValue.equalsIgnoreCase("currentdate")
                    || attrValue.equalsIgnoreCase("current_date")) {
                result.success = true;
                result.formatedString = "current_date";
            } else if (attrValue
                    .toUpperCase(Locale.getDefault())
                    .startsWith(DataType.DATATYPE_DATE)) {
                String str = attrValue.trim().substring("date".length()).trim();
                if (str.startsWith("'") && str.endsWith("'") && str.length() > 2) {
                    str = str.substring(1, str.length() - 1);
                    formatDate(str, result, isIncludeKeyWord);
                }
            }
        }
        if (result.isSuccess()) {
            return;
        }
        try {
            long timestamp = DateUtil.getDate(attrValue, pattern);
            result.success = true;
            result.formatedString =
                    DataType.DATATYPE_DATE
                            + "'"
                            + DateUtil.getDatetimeString(timestamp, DATE_FORMAT)
                            + "'";
            result.formatedJavaObj = new java.sql.Date(timestamp);
        } catch (ParseException e) {
            if (DateUtil.validateTimestamp(attrValue, "MM/dd")) {
                result.success = true;
                result.formatedString = DataType.DATATYPE_DATE + "'" + attrValue + "'";
                try {
                    result.formatedJavaObj =
                            new java.sql.Date(DateUtil.getTimestamp("MM/dd", attrValue));
                } catch (ParseException e1) {
                    LOGGER.error("", e1);
                }
            } else {
                result.success = false;
            }
        }
    }

    /**
     * Format TimeStamp value
     *
     * @param attrValue String The attribute value
     * @param result FormatDataResult the given instance of FormatDataResult
     * @param isIncludeKeyWord boolean whether allow to include keyword
     */
    public static void formatTimeStamp(
            String attrValue, FormatDataResult result, boolean isIncludeKeyWord) {
        if (attrValue == null || "".equals(attrValue)) {
            result.success = true;
            result.formatedString = attrValue;
        } else if (isIncludeKeyWord) {
            if (attrValue.equalsIgnoreCase("systimestamp")
                    || attrValue.equalsIgnoreCase("sys_timestamp")) {
                result.success = true;
                result.formatedString = "systimestamp";
            } else if (attrValue.equalsIgnoreCase("currenttimestamp")
                    || attrValue.equalsIgnoreCase("current_timestamp")) {
                result.success = true;
                result.formatedString = "current_timestamp";

            } else if (attrValue
                    .toUpperCase(Locale.getDefault())
                    .startsWith(DataType.DATATYPE_TIMESTAMP)) {
                String str = attrValue.trim().substring("timestamp".length()).trim();
                if (str.startsWith("'") && str.endsWith("'") && str.length() > 2) {
                    str = str.substring(1, str.length() - 1);
                    formatTimeStamp(str, result, isIncludeKeyWord);
                }
            }
        }
        if (result.isSuccess()) {
            return;
        }
        try {
            int time = Integer.parseInt(attrValue);
            result.success = true;
            result.formatedString = attrValue;
            result.formatedJavaObj = new java.sql.Timestamp(time);
        } catch (NumberFormatException nfe) {
            try {
                long timestamp = DateUtil.getTimestamp(attrValue);
                result.success = true;
                result.formatedString =
                        DataType.DATATYPE_TIMESTAMP
                                + "'"
                                + DateUtil.getDatetimeString(timestamp, TIMESTAMP_FORMAT)
                                + "'";
                result.formatedJavaObj = new java.sql.Timestamp(timestamp);
            } catch (ParseException e) {
                result.success = false;
                for (int i = 0; i < OTHER_TIMESTAMP_FORMATS.length; i++) {
                    if (DateUtil.validateTimestamp(attrValue, OTHER_TIMESTAMP_FORMATS[i])) {
                        result.success = true;
                        result.formatedString = "TIMESTAMP'" + attrValue + "'";
                        try {
                            result.formatedJavaObj =
                                    new java.sql.Timestamp(
                                            DateUtil.getTimestamp(
                                                    attrValue, OTHER_TIMESTAMP_FORMATS[i]));
                        } catch (ParseException e1) {
                            LOGGER.error(e.getMessage());
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * Format Time value
     *
     * @param attrValue String The attribute value
     * @param result FormatDataResult the given instance of FormatDataResult
     * @param isIncludeKeyWord boolean whether allow to include keyword
     */
    public static void formatTime(
            String attrValue, FormatDataResult result, boolean isIncludeKeyWord) {
        if (attrValue == null
                || "".equals(attrValue)
                || DataType.NULL_EXPORT_FORMAT.equals(attrValue)) {
            result.success = true;
            result.formatedString = attrValue;
        } else if (isIncludeKeyWord) {
            if (attrValue.equalsIgnoreCase("systime") || attrValue.equalsIgnoreCase("sys_time")) {
                result.success = true;
                result.formatedString = "systime";
            } else if (attrValue.equalsIgnoreCase("currenttime")
                    || attrValue.equalsIgnoreCase("current_time")) {
                result.success = true;
                result.formatedString = "current_time";
            } else if (attrValue
                    .toUpperCase(Locale.getDefault())
                    .startsWith(DataType.DATATYPE_TIME)) {
                String str = attrValue.trim().substring("time".length()).trim();
                if (str.startsWith("'") && str.endsWith("'") && str.length() > 2) {
                    str = str.substring(1, str.length() - 1);
                    formatTime(str, result, isIncludeKeyWord);
                }
            }
        }

        if (result.isSuccess()) {
            return;
        }

        try {
            long time = Long.parseLong(attrValue);
            result.success = true;
            result.formatedString = attrValue;
            result.formatedJavaObj = new java.sql.Time(time);
        } catch (NumberFormatException nfe) {
            long timestamp;
            try {
                timestamp = DateUtil.getTime(attrValue);
                result.success = true;
                result.formatedString =
                        "TIME'" + DateUtil.getDatetimeString(timestamp, TIME_FORMAT) + "'";
                result.formatedJavaObj = new java.sql.Time(timestamp);
            } catch (ParseException e) {
                result.success = false;
                LOGGER.error(e.getMessage());
            }
        }
    }

    /**
     * Format Char value
     *
     * @param attrType String The attribute type
     * @param attrValue String The attribute value
     * @param result FormatDataResult the given instance of FormatDataResult
     */
    public static void formatChar(String attrType, String attrValue, FormatDataResult result) {
        String size = null;

        try {
            size = attrType.substring(attrType.indexOf("(") + 1, attrType.indexOf(")"));
        } catch (Exception e) {
            size = "" + DataType.STRING_MAX_SIZE;
        }

        if (attrValue == null) {
            result.success = true;
            result.formatedString = attrValue;
            result.formatedJavaObj = attrValue;
        } else {
            String containStr = "";
            String javaObjStr = attrValue;
            if (attrValue.startsWith("'") && attrValue.endsWith("'") && attrValue.length() > 1) {
                containStr =
                        attrValue.substring(attrValue.indexOf("'") + 1, attrValue.lastIndexOf("'"));
                javaObjStr = containStr;
            } else {
                containStr = attrValue.replaceAll("'", "''");
            }
            if (Integer.parseInt(size) >= javaObjStr.length()) {
                result.success = true;
            } else {
                result.success = false;
            }
            result.formatedString = "'" + containStr + "'";
            result.formatedJavaObj = javaObjStr;
        }
    }

    /**
     * Format Char value
     *
     * @param attrType String The attribute type
     * @param attrValue String The attribute value
     * @param result FormatDataResult the given instance of FormatDataResult
     */
    public static void formatCharForInput(
            String attrType, String attrValue, FormatDataResult result) {
        String size = null;

        try {
            size = attrType.substring(attrType.indexOf("(") + 1, attrType.indexOf(")"));
        } catch (Exception e) {
            size = "" + DataType.STRING_MAX_SIZE;
        }

        if (attrValue == null) {
            result.success = true;
            result.formatedString = attrValue;
            result.formatedJavaObj = attrValue;
        } else {
            /*Get the real value*/
            if ((attrValue.startsWith("'")
                            && attrValue.endsWith("'")
                            && attrValue.indexOf("'") != attrValue.lastIndexOf("'"))
                    || (attrValue.startsWith("\"")
                            && attrValue.endsWith("\"")
                            && attrValue.indexOf("\"") != attrValue.lastIndexOf("\""))) {
                attrValue = attrValue.substring(1, attrValue.length() - 1);
            }
            String containStr = attrValue;
            String javaObjStr = attrValue;
            if (Integer.parseInt(size) >= javaObjStr.length()) {
                result.success = true;
            } else {
                result.success = false;
            }
            result.formatedString = "'" + containStr + "'";
            result.formatedJavaObj = javaObjStr;
        }
    }

    /**
     * Format Nchar value for input/edit data
     *
     * @param attType String The attribute type
     * @param attrValue String The attribute value
     * @param charSet String
     * @param result FormatDataResult the given instance of FormatDataResult
     */
    public static void formatNcharForInput(
            String attType, String attrValue, FormatDataResult result, String charSet) {
        String size = null;

        try {
            size = attType.substring(attType.indexOf("(") + 1, attType.indexOf(")"));
        } catch (Exception e) {
            size = "" + DataType.STRING_MAX_SIZE;
        }
        if (attrValue == null) {
            result.success = true;
            result.formatedString = null;
        } else if ("".equals(attrValue)) {
            result.success = true;
            result.formatedString = "N''";
            result.formatedJavaObj = new byte[] {};
        } else {
            String contain = attrValue;
            String javaObjStr = attrValue;
            /*Get the real value*/
            if (attrValue.startsWith("N'")
                    && attrValue.endsWith("'")
                    && attrValue.indexOf("'") != attrValue.lastIndexOf("'")) {
                contain = attrValue.substring(2, attrValue.lastIndexOf("'"));
            } else if (attrValue.startsWith("N\"")
                    && attrValue.endsWith("\"")
                    && attrValue.indexOf("\"") != attrValue.lastIndexOf("\"")) {
                contain = attrValue.substring(2, attrValue.lastIndexOf("'"));
            } else if ((attrValue.startsWith("'")
                            && attrValue.endsWith("'")
                            && attrValue.indexOf("'") != attrValue.lastIndexOf("'"))
                    || (attrValue.startsWith("\"")
                            && attrValue.endsWith("\"")
                            && attrValue.indexOf("\"") != attrValue.lastIndexOf("\""))) {
                contain = attrValue.substring(1, attrValue.length() - 1);
            }
            int valueSize = contain.getBytes().length;
            // contain = contain.replaceAll("'", "''");
            if (Integer.parseInt(size) >= valueSize) {
                result.success = true;
                try {
                    result.formatedJavaObj = javaObjStr.getBytes(charSet);
                } catch (UnsupportedEncodingException e) {
                    LOGGER.error("", e);
                }
            } else {
                result.success = false;
            }
            result.formatedString = "N'" + contain + "'";
        }
    }

    /**
     * Format String value for input/edit data
     *
     * @param attrValue String The attribute value
     * @param result FormatDataResult the given instance of FormatDataResult
     */
    public static void formatStringForInput(String attrValue, FormatDataResult result) {
        String containValue = "";
        if (attrValue == null || "".equals(attrValue)) {
            result.success = true;
            result.formatedString = attrValue;
            result.formatedJavaObj = attrValue;
        } else {
            String javaObjStr = attrValue;
            /*Get the real value*/
            if ((attrValue.startsWith("'")
                            && attrValue.endsWith("'")
                            && attrValue.indexOf("'") != attrValue.lastIndexOf("'"))
                    || (attrValue.startsWith("\"")
                            && attrValue.endsWith("\"")
                            && attrValue.indexOf("\"") != attrValue.lastIndexOf("\""))) {
                attrValue = attrValue.substring(1, attrValue.length() - 1);
            }
            containValue = attrValue;
            result.success = true;
            result.formatedString = "'" + containValue + "'";
            result.formatedJavaObj = javaObjStr;
        }
    }
    /**
     * Format Enum value
     *
     * @param attrValue String The attribute value
     * @param result FormatDataResult the given instance of FormatDataResult
     */
    public static void formatEnum(String attrValue, FormatDataResult result) {
        int size = DataType.STRING_MAX_SIZE;

        if (attrValue == null) {
            result.success = true;
            result.formatedString = attrValue;
            result.formatedJavaObj = attrValue;
        } else {
            String containStr = "";
            String javaObjStr = attrValue;
            if (attrValue.startsWith("'") && attrValue.endsWith("'") && attrValue.length() > 1) {
                containStr =
                        attrValue.substring(attrValue.indexOf("'") + 1, attrValue.lastIndexOf("'"));
                javaObjStr = containStr;
            } else {
                containStr = attrValue.replaceAll("'", "''");
            }
            if (size >= javaObjStr.length()) {
                result.success = true;
            } else {
                result.success = false;
            }
            result.formatedString = "'" + containStr + "'";
            result.formatedJavaObj = javaObjStr;
        }
    }

    /**
     * Format Monetary value
     *
     * @param attrValue String The attribute value
     * @param result FormatDataResult the given instance of FormatDataResult
     */
    public static void formatMonetary(String attrValue, FormatDataResult result) {
        if (attrValue == null) {
            result.success = true;
            result.formatedString = null;
            result.formatedJavaObj = null;
        } else if (attrValue.startsWith("$")) {
            String containValue = attrValue.substring(attrValue.indexOf("$") + 1);
            formatMonetary(containValue, result);
        } else {
            try {
                Double.parseDouble(attrValue);
                result.success = true;
                result.formatedString = "$" + attrValue;
                result.formatedJavaObj = new Double(attrValue);
            } catch (NumberFormatException e) {
                result.success = false;
                result.formatedString = attrValue;
            }
        }
    }

    /**
     * Format Bit value
     *
     * @param attrType String The attribute type
     * @param attrValue String The attribute value
     * @param result FormatDataResult the given instance of FormatDataResult
     * @param charSet String
     */
    public static void formatBit(
            String attrType, String attrValue, FormatDataResult result, String charSet) {
        String size = null;
        try {
            size = attrType.substring(attrType.indexOf("(") + 1, attrType.indexOf(")"));
        } catch (Exception e) {
            size = "" + DataType.BIT_DEFAULT_LENGTH;
        }
        if (attrValue == null) {
            result.success = true;
            result.formatedString = null;
            result.formatedJavaObj = null;
        } else if (attrValue.startsWith("B'")
                && attrValue.endsWith("'")
                && attrValue.indexOf("'") != attrValue.lastIndexOf("'")) {
            String sValue =
                    attrValue.substring(attrValue.indexOf("'") + 1, attrValue.lastIndexOf("'"));
            String reg = "^[0-1]+$";
            if (sValue.matches(reg) && Integer.parseInt(size) >= sValue.length()) {
                result.success = true;
            } else {
                result.success = false;
            }
            result.formatedString = attrValue;
            if (result.success) {
                result.formatedJavaObj = getBytes(sValue, 2);
            }
        } else if (attrValue.startsWith("0b")) {
            String sValue = attrValue.substring(2);
            String reg = "^[0-1]+$";
            if (sValue.matches(reg) && Integer.parseInt(size) >= sValue.length()) {
                result.success = true;
            } else {
                result.success = false;
            }
            result.formatedString = attrValue;
            if (result.success) {
                result.formatedJavaObj = getBytes(sValue, 2);
            }
        } else if (attrValue.startsWith("X'")
                && attrValue.endsWith("'")
                && attrValue.indexOf("'") != attrValue.lastIndexOf("'")) {
            String sValue =
                    attrValue.substring(attrValue.indexOf("'") + 1, attrValue.lastIndexOf("'"));
            String reg = "^[0-9A-Fa-f]+$";
            if (sValue.matches(reg) && Integer.parseInt(size) >= sValue.length() * 4) {
                result.success = true;
                result.formatedString = attrValue;
                result.formatedJavaObj = getBytes(sValue, 16);
            } else {
                result.success = false;
                result.formatedString = attrValue;
            }
        } else if ("".equals(attrValue)) {
            result.success = true;
            result.formatedString = attrValue;
            result.formatedJavaObj = new byte[] {};
        } else if (isFilePath(attrValue)) {
            result.success = true;
            result.formatedString = attrValue;
            result.formatedJavaObj = attrValue;
        } else {
            try {
                byte[] bArr = attrValue.getBytes(charSet);
                String hexStr = getHexString(bArr);
                if (Integer.parseInt(size) >= hexStr.length() * 4) {
                    result.success = true;
                    result.formatedString = "X'" + hexStr + "'";
                    result.formatedJavaObj = bArr;
                } else {
                    result.success = false;
                    result.formatedString = attrValue;
                }
            } catch (UnsupportedEncodingException e) {
                result.success = false;
                result.formatedString = attrValue;
                LOGGER.error("", e);
            }
        }
    }

    /**
     * Return whether this str is file path
     *
     * @param str String
     * @return boolean
     */
    public static boolean isFilePath(String str) {
        return str != null
                && str.startsWith(FILE_URL_PREFIX)
                && new File(str.replaceFirst(FILE_URL_PREFIX, "")).exists();
    }

    /**
     * Return hex String
     *
     * @param bytes byte[]
     * @return string
     */
    public static String getHexString(byte[] bytes) {
        StringBuffer bf = new StringBuffer();
        for (byte b : bytes) {
            int value = b & 0x00ff;
            // TOOLS-601 (but, it has a bug yet. X'0102340506071' --> 01 02 34 05 06 07 1. The last
            // 1 has a problem.)
            // whether 10 or 01?
            String formattedValue = "00" + Integer.toHexString(value);
            bf.append(
                    formattedValue.substring(formattedValue.length() - 2, formattedValue.length()));
        }
        return bf.toString();
    }

    /**
     * Get byte array of this string
     *
     * @param value the <code>String</code> containing the <code>byte</code> representation to be
     *     parsed
     * @param radix the radix to be used while parsing <code>value</code>
     * @return byte[]
     */
    public static byte[] getBytes(String value, int radix) {
        String sValue = value;
        int length = value.length();
        int mode = 8;
        if (radix == 2) {
            mode = 8;
        } else if (radix == 16) {
            mode = 2;
        }
        if (radix == 2 && length % 8 != 0) {
            for (int i = 0; i < (8 - length % 8); i++) {
                sValue = "0" + sValue;
            }
        } else if (radix == 16 && length % 2 != 0) {
            sValue = "0" + sValue;
        }
        int count = sValue.length() / mode;
        byte[] bytArr = new byte[count];
        for (int i = 0; i < count; i++) {
            String str = sValue.substring(i * mode, i * mode + mode);
            bytArr[i] = Byte.parseByte(str, radix);
        }
        return bytArr;
    }

    /**
     * Format Set value
     *
     * @param attrType String The attribute type
     * @param attrValue String The attribute value
     * @param result FormatDataResult the given instance of FormatDataResult
     * @param isUseNULLValueSetting
     */
    public static void formatSet(
            String attrType,
            String attrValue,
            FormatDataResult result,
            boolean isUseNULLValueSetting) {
        if (attrValue == null) {
            result.success = true;
            result.formatedString = null;
            result.formatedJavaObj = null;
        } else if ("".equals(attrValue) || "{}".equals(attrValue)) {
            result.success = true;
            result.formatedString = attrValue;
        } else if (attrValue.startsWith("{") && attrValue.endsWith("}")) {
            String containValue = attrValue.substring(1, attrValue.length() - 1);
            formatSet(attrType, containValue, result, isUseNULLValueSetting);
        } else {
            int index = attrType.indexOf("(");
            int lastIndex = attrType.lastIndexOf(")");
            if (index == -1 || index >= lastIndex) {
                result.success = true;
                result.formatedString = attrValue;
                result.formatedJavaObj = attrValue.split(",");
            } else {
                String subTypes = attrType.substring(index + 1, lastIndex);
                formatSetSubType(subTypes, attrValue, result, isUseNULLValueSetting);
            }
        }
    }

    /**
     * Format the set sub type
     *
     * @param subTypeStrs The String
     * @param attrValue The String
     * @param result The FormatDataResult
     * @param isUseNULLValueSetting
     */
    private static void formatSetSubType(
            String subTypeStrs,
            String attrValue,
            FormatDataResult result,
            boolean isUseNULLValueSetting) {
        String[] subTypes = subTypeStrs.split(",");
        int length = subTypes.length;
        List<String> typeList = new ArrayList<String>();
        for (int i = 0; i < length; i++) {
            String subType = subTypes[i];
            if (subType.trim().matches("^NUMERIC\\(\\d+$")
                    && i + 1 < length
                    && subTypes[i + 1].trim().matches("^\\d+\\$")) {
                subType = subType.trim() + "," + subTypes[i + 1].trim();
                i++;
            }
            typeList.add(subType);
        }

        List<Object> objList = new ArrayList<Object>();
        StringBuffer bf = new StringBuffer();
        String[] values = attrValue.split(",");
        boolean isFinalSuccess = true;
        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            if (value.trim().length() > 0) {
                value = value.trim();
            }
            boolean isSuccess = false;
            for (String subType : typeList) {
                FormatDataResult formatDataResult = format(subType, value, isUseNULLValueSetting);
                if (formatDataResult.success) {
                    if (i > 0) {
                        bf.append(",");
                    }
                    bf.append(formatDataResult.getFormatResult());
                    objList.add(formatDataResult.getFormatedJavaObj());
                    isSuccess = true;
                    break;
                }
            }
            isFinalSuccess = isFinalSuccess && isSuccess;
            if (!isSuccess) {
                break;
            }
        }
        result.success = isFinalSuccess;
        result.formatedString = "{" + bf.toString() + "}";
        result.formatedJavaObj = objList.toArray();
    }

    /**
     * Format the BLOB
     *
     * @param attrValue String The attribute value
     * @param result FormatDataResult the given instance of FormatDataResult
     */
    private static void formatBLob(String attrValue, FormatDataResult result) {
        result.success = true;
        result.formatedString = attrValue;
        result.formatedJavaObj = attrValue;
    }

    /**
     * Format the CLOB
     *
     * @param attrValue String The attribute value
     * @param result FormatDataResult the given instance of FormatDataResult
     */
    private static void formatCLob(String attrValue, FormatDataResult result) {
        result.success = true;
        result.formatedString = attrValue;
        result.formatedJavaObj = attrValue;
    }

    /**
     * Check whether this type need input many values, these type can use file
     *
     * @param type The String
     * @return boolean
     */
    public static boolean isMuchValueType(String type) {
        return isMuchValueType(type, MUCH_VALUE_TYPE_LENGTH);
    }

    /**
     * Check whether this type need input many values, these type can use file
     *
     * @param type The String
     * @param size long
     * @return boolean
     */
    public static boolean isMuchValueType(String type, long size) {
        String upperType = type.trim().toUpperCase();
        if (upperType.startsWith(DataType.DATATYPE_BLOB)
                || upperType.startsWith(DataType.DATATYPE_CLOB)
                || upperType.startsWith(DataType.DATATYPE_STRING)) {
            return true;
        }
        if (upperType.startsWith(DataType.DATATYPE_VARCHAR)
                || upperType.startsWith(DataType.DATATYPE_CHAR)
                || upperType.startsWith(DataType.DATATYPE_BIT_VARYING)
                || upperType.startsWith(DataType.DATATYPE_BIT)
                || upperType.startsWith(DataType.DATATYPE_NCHAR_VARYING)
                || upperType.startsWith(DataType.DATATYPE_NCHAR)) {
            if (size < 0) {
                return true;
            }
            if (upperType.indexOf("(") != -1 && upperType.indexOf(")") > upperType.indexOf("(")) {
                String number =
                        upperType
                                .substring(upperType.indexOf("(") + 1, upperType.indexOf(")"))
                                .trim();
                return number.matches("^\\d+$") ? Long.parseLong(number) > size : false;
            }
            return true;
        }
        return false;
    }

    /**
     * Format much value to JDBC object, this value probably is from file, if it is file, convert it
     * for JDBC object according to JDBC type
     *
     * @param str The much value
     * @param type The JDBC type
     * @param conn The Connection
     * @param dbCharSet The database charset
     * @param fileCharSet The file charset
     * @param isUseNULLValueSetting
     * @return the real JDBC object
     */
    public static Object formatMuchValue(
            String str,
            String type,
            Connection conn,
            String dbCharSet,
            String fileCharSet,
            boolean isUseNULLValueSetting) {
        String upperType = type.trim().toUpperCase();
        boolean isString =
                upperType.startsWith(DataType.DATATYPE_VARCHAR)
                        || upperType.startsWith(DataType.DATATYPE_CHAR)
                        || upperType.startsWith(DataType.DATATYPE_STRING);
        boolean isByte =
                upperType.startsWith(DataType.DATATYPE_BIT_VARYING)
                        || upperType.startsWith(DataType.DATATYPE_BIT)
                        || upperType.startsWith(DataType.DATATYPE_NCHAR)
                        || upperType.startsWith(DataType.DATATYPE_NCHAR_VARYING);
        int size = 0;
        if (isString || isByte) {
            size = DataType.getSize(type);
        }
        Object realObj = null;
        String errorMsg = null;
        File file = new File(str.replaceFirst(FILE_URL_PREFIX, ""));
        if (upperType.startsWith(DataType.DATATYPE_BLOB)) {
            try {
                CUBRIDBlobProxy blob = new CUBRIDBlobProxy((CUBRIDConnectionProxy) conn);
                if (str.startsWith(FILE_URL_PREFIX) && file.exists()) {
                    InputStream fin = null;
                    OutputStream out = null;
                    try {
                        fin = new FileInputStream(file);
                        out = blob.setBinaryStream(1);
                        byte[] data = new byte[512];
                        int count = -1;
                        while ((count = fin.read(data)) != -1) {
                            out.write(data, 0, count);
                        }
                        realObj = blob.getProxyObj();
                    } catch (IOException e) {
                        errorMsg = e.getMessage();
                        LOGGER.error("", e);
                    } finally {
                        try {
                            fin.close();
                        } catch (IOException e) {
                            LOGGER.error("", e);
                        }
                        try {
                            out.close();
                        } catch (IOException e) {
                            LOGGER.error("", e);
                        }
                    }
                } else {
                    try {
                        byte[] byteArr = str.getBytes(dbCharSet);
                        blob.setBytes(1, byteArr);
                        realObj = blob.getProxyObj();
                    } catch (UnsupportedEncodingException e) {
                        errorMsg = e.getMessage();
                        LOGGER.error("", e);
                    }
                }
            } catch (CUBRIDProxyException e) {
                errorMsg = e.getMessage();
                LOGGER.error("", e);
            } catch (SQLException e) {
                errorMsg = e.getMessage();
                LOGGER.error("", e);
            }
        } else if (upperType.startsWith(DataType.DATATYPE_CLOB)) {
            try {
                CUBRIDClobProxy clob = new CUBRIDClobProxy((CUBRIDConnectionProxy) conn, dbCharSet);
                if (str.startsWith(FILE_URL_PREFIX) && file.exists()) {
                    BufferedReader reader = null;
                    Writer writer = null;
                    try {
                        reader =
                                new BufferedReader(
                                        new InputStreamReader(
                                                new FileInputStream(file), fileCharSet));
                        writer = clob.setCharacterStream(1);
                        char[] charArr = new char[512];
                        int count = reader.read(charArr);
                        while (count > 0) {
                            writer.write(charArr, 0, count);
                            count = reader.read(charArr);
                        }
                    } catch (IOException e) {
                        errorMsg = e.getMessage();
                        LOGGER.error("", e);
                    } finally {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            LOGGER.error("", e);
                        }
                        try {
                            writer.close();
                        } catch (IOException e) {
                            LOGGER.error("", e);
                        }
                    }
                } else {
                    clob.setString(1, str);
                }
                realObj = clob.getProxyObj();
            } catch (CUBRIDProxyException e) {
                errorMsg = e.getMessage();
                LOGGER.error("", e);
            } catch (SQLException e) {
                errorMsg = e.getMessage();
                LOGGER.error("", e);
            }
        } else if (isString) {
            if (str.startsWith(FILE_URL_PREFIX) && file.exists()) {
                BufferedReader reader = null;
                try {
                    reader =
                            new BufferedReader(
                                    new InputStreamReader(new FileInputStream(file), fileCharSet));
                    StringBuffer strBuffer = new StringBuffer();
                    char[] charArr = new char[512];
                    int count = reader.read(charArr);
                    int totalCount = count;
                    while (count > 0) {
                        if (count == 512) {
                            strBuffer.append(charArr);
                        } else {
                            char[] tmpChar = new char[count];
                            System.arraycopy(charArr, 0, tmpChar, 0, count);
                            strBuffer.append(tmpChar);
                        }
                        count = reader.read(charArr);
                        totalCount += count;
                    }
                    if (totalCount > size) {
                        errorMsg = Messages.bind(Messages.fileTooLongMsg, new String[] {str, type});
                    } else {
                        realObj = strBuffer.toString();
                    }
                } catch (IOException e) {
                    errorMsg = e.getMessage();
                    LOGGER.error("", e);
                } finally {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        LOGGER.error("", e);
                    }
                }
            } else {
                realObj = str;
            }
        } else if (isByte) {
            if (str.startsWith(FILE_URL_PREFIX) && file.exists()) {
                InputStream fin = null;
                try {
                    fin = new FileInputStream(file);
                    int length = fin.available();
                    if (length * 8 > size) {
                        errorMsg = Messages.bind(Messages.fileTooLongMsg, new String[] {str, type});
                    } else {
                        byte[] data = new byte[length];
                        if (fin.read(data) != -1) {
                            realObj = data;
                        }
                    }
                } catch (IOException e) {
                    errorMsg = e.getMessage();
                    LOGGER.error("", e);
                } finally {
                    try {
                        fin.close();
                    } catch (IOException e) {
                        LOGGER.error("", e);
                    }
                }
            } else {
                FormatDataResult result =
                        DBAttrTypeFormatter.format(
                                type, str, false, dbCharSet, isUseNULLValueSetting);
                byte[] byteArr = (byte[]) result.getFormatedJavaObj();
                realObj = byteArr;
            }
        }
        if (errorMsg != null) {
            realObj = new Exception(errorMsg);
        }
        return realObj;
    }

    /**
     * Return whether this string is binary
     *
     * @param str String
     * @return boolean
     */
    public static boolean isBinaryString(String str) {
        boolean isBinaryStr =
                (str.startsWith("B'") || str.startsWith("b'"))
                        && str.endsWith("'")
                        && str.indexOf("'") != str.lastIndexOf("'");
        if (isBinaryStr) {
            return getInnerString(str).matches("^[0-1]+$");
        }
        return false;
    }

    /**
     * Return whether this string is hex
     *
     * @param str String
     * @return boolean
     */
    public static boolean isHexString(String str) {
        boolean isHexStr =
                (str.startsWith("X'") || str.startsWith("x'"))
                        && str.endsWith("'")
                        && str.indexOf("'") != str.lastIndexOf("'");
        if (isHexStr) {
            return getInnerString(str).matches("^[0-9a-fA-F]+$");
        }
        return false;
    }

    /**
     * Get the inner string for binary string or hex string
     *
     * @param str String
     * @return String
     */
    public static String getInnerString(String str) {
        int startPos = str.indexOf("'");
        int endPos = str.lastIndexOf("'");
        if (startPos >= 0 && startPos < endPos) {
            return str.substring(startPos + 1, endPos);
        }
        return "";
    }
    /**
     * Return binary String
     *
     * @param bytes byte[]
     * @return string
     */
    public static String getBinaryString(byte[] bytes) {
        return getBitString(bytes, 0, true);
    }

    /**
     * Return binary String
     *
     * @param bytes byte[]
     * @param bitLength int a length of bit to display, 0 is all data of bytes
     * @param reverse boolean for CUBRID bit(true), others(false)
     * @return string
     */
    public static String getBitString(byte[] bytes, int bitLength, boolean reverse) {
        StringBuffer bf = new StringBuffer();
        for (byte b : bytes) {
            String sl = Integer.toBinaryString(b & 0xf);
            String sh = Integer.toBinaryString(b >> 4 & 0xf);
            int length = 0;
            length = 4 - sh.length();
            for (int i = 0; i < length; i++) {
                sh = "0" + sh;
            }
            bf.append(sh);
            length = 4 - sl.length();
            for (int i = 0; i < length; i++) {
                sl = "0" + sl;
            }
            bf.append(sl);
        }
        if (bitLength > 0) {
            int sp = reverse ? 0 : bf.length() - bitLength;
            int ep = reverse ? bitLength : bf.length();
            return bf.substring(sp, ep);
        }
        return bf.toString();
    }
    /**
     * Return hex String
     *
     * @param bytes byte[]
     * @param bitLength int a length of bit to display, 0 is all data of bytes
     * @return string
     */
    public static String getHexString(byte[] bytes, int bitLength) {
        StringBuffer bf = new StringBuffer();
        int processLength = 0;
        for (byte b : bytes) {
            String sl = Integer.toBinaryString(b & 0xf);
            String sh = Integer.toBinaryString(b >> 4 & 0xf);
            int length = 0;
            length = 4 - sh.length();
            for (int i = 0; i < length; i++) {
                sh = "0" + sh;
            }
            int valueLow = Integer.valueOf(sh, 2);
            length = 4 - sl.length();
            for (int i = 0; i < length; i++) {
                sl = "0" + sl;
            }
            int valueHigh = Integer.valueOf(sl, 2);
            if (processLength < bitLength) {
                bf.append(Integer.toHexString(valueLow));
                processLength += 4;
            }
            if (processLength < bitLength) {
                bf.append(Integer.toHexString(valueHigh));
                processLength += 4;
            }
        }
        return bf.toString();
    }

    /**
     * Get byte array of this string for cubrid bit
     *
     * @param value the <code>String</code> containing the <code>byte</code> representation to be
     *     parsed
     * @param radix the radix to be used while parsing <code>value</code>
     * @return byte[]
     * @throws Exception the exception
     */
    public static byte[] getBitBytes(String value, int radix) throws Exception {
        String sValue = value;
        int length = value.length();
        int mode = 8;
        if (radix == 2) {
            mode = 8;
        } else if (radix == 16) {
            mode = 2;
        }
        if (radix == 2 && length % 8 != 0) {
            for (int i = 0; i < (8 - length % 8); i++) {
                sValue = sValue + "0";
            }
        } else if (radix == 16 && length % 2 != 0) {
            sValue = sValue + "0";
        }
        int count = sValue.length() / mode;
        byte[] bytArr = new byte[count];
        for (int i = 0; i < count; i++) {
            String str = sValue.substring(i * mode, i * mode + mode);
            try {
                bytArr[i] = (byte) (Integer.parseInt(str, radix) & 0xff);
            } catch (NumberFormatException ex) {
                throw new Exception(ex.getMessage());
            }
        }
        return bytArr;
    }
    /**
     * Get the size of the string
     *
     * @param str String
     * @param charset String
     * @return long
     */
    public static long getStringByteSize(String str, String charset) {
        if (DBAttrTypeFormatter.isBinaryString(str)) {
            return DBAttrTypeFormatter.getInnerString(str).length();
        } else if (DBAttrTypeFormatter.isHexString(str)) {
            return DBAttrTypeFormatter.getInnerString(str).length() * 4;
        } else {
            return DBAttrTypeFormatter.getStringToByteSize(str, charset);
        }
    }

    /**
     * Get the string byte size
     *
     * @param str String
     * @param charset String
     * @return long
     */
    public static long getStringToByteSize(String str, String charset) {
        if (str == null || str.length() == 0) {
            return 0;
        }
        if (charset == null || charset.trim().length() == 0) {
            return str.getBytes().length;
        } else {
            try {
                return str.getBytes(charset).length;
            } catch (UnsupportedEncodingException e) {
                return str.getBytes().length;
            }
        }
    }
}
