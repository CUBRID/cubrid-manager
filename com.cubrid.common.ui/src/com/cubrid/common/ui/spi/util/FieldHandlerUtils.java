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
package com.cubrid.common.ui.spi.util;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WriteException;

import org.slf4j.Logger;

import com.cubrid.common.core.util.DateUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.cubrid.table.dialog.PstmtParameter;
import com.cubrid.cubridmanager.core.cubrid.table.model.DBAttrTypeFormatter;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import com.cubrid.cubridmanager.core.cubrid.table.model.FormatDataResult;
import com.cubrid.jdbc.proxy.driver.CUBRIDOIDProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy;

/**
 * Different field type has different handler.
 * 
 * @author SC13425
 * @version 1.0 - 2010-8-10 created by SC13425
 */
public final class FieldHandlerUtils {

	private static final Logger LOGGER = LogUtil.getLogger(FieldHandlerUtils.class);

	public static final String PATTEN_NUMBER = "[0-9\\-]+(\\.[0-9]+)?";
	public static final String PATTEN_INTEGER = "-?\\d+";
	public static final String PATTEN_INTEGER2 = "^\\d+$";

	public static final String FORMAT_TIME = "HH:mm:ss";
	public static final String FORMAT_TIMESTAMP = "yyyy-MM-dd HH:mm:ss";
	public static final String FORMAT_DATE = "yyyy-MM-dd";
	public static final String FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss.SSS";

	public static final String MAX_FLOAT_DIGITNUM = "38";
	// VARCHAR Max Size
	public static final String MAXSIZE = "1073741823";
	// NCHAR Max Size
	public static final String NCHARMAXSIZE = "536870911";

	// When bit type value length > BIT_TYPE_MUCH_VALUE_LENGTH, do not show in
	// query editor
	public static final long BIT_TYPE_MUCH_VALUE_LENGTH = 100;
	public static final int MAX_DISPLAY_CLOB_LENGTH = 1024;
	// file path URL format
	public static final String FILE_URL_PREFIX = DBAttrTypeFormatter.FILE_URL_PREFIX;

	private FieldHandlerUtils() {

	}

	/**
	 * Check this value whether valid
	 * 
	 * @param dataType The string
	 * @param value The string
	 * @return boolean If value is empty string,return "";if no errors return
	 *         null.
	 */
	public static String isValidData(String dataType, String value) {
		String resultMsg = null;
		if (StringUtil.isEmpty(value)) {
			resultMsg = "";
		} else if ((DataType.DATATYPE_INTEGER.equalsIgnoreCase(dataType)
				|| DataType.DATATYPE_SMALLINT.equalsIgnoreCase(dataType))
				&& !value.trim().matches(PATTEN_INTEGER)) {
			resultMsg = Messages.bind(Messages.errInvalidRangeValue,
					new String[]{dataType, PATTEN_INTEGER });
		} else if (DataType.DATATYPE_DATE.equalsIgnoreCase(dataType)
				&& !ValidateUtil.isDate(value)) {
			resultMsg = Messages.bind(Messages.errInvalidRangeValue,
					new String[]{dataType, FORMAT_DATE });
		} else if (DataType.DATATYPE_TIME.equalsIgnoreCase(dataType)
				&& !ValidateUtil.isTime(value)) {
			resultMsg = Messages.bind(Messages.errInvalidRangeValue,
					new String[]{dataType, FORMAT_TIME });
		} else if (DataType.DATATYPE_TIMESTAMP.equalsIgnoreCase(dataType)
				&& !ValidateUtil.isTimeStamp(value)) {
			resultMsg = Messages.bind(Messages.errInvalidRangeValue,
					new String[]{dataType, FORMAT_TIMESTAMP });
		}
		return resultMsg;
	}

	/**
	 * Compare the two expression value
	 * 
	 * @param dataType the string
	 * @param str1 the first object to be compared.
	 * @param str2 the second object to be compared.
	 * @return a negative integer, zero, or a positive integer as the first
	 *         argument is less than, equal to, or greater than the second.
	 */
	public static int compareData(String dataType, String str1, String str2) {
		if (str1 == null && str2 == null) {
			return 0;
		}
		if (str1 == null) {
			return 1;
		}
		if (str2 == null) {
			return -1;
		}
		if (DataType.DATATYPE_INTEGER.equalsIgnoreCase(dataType)
				|| DataType.DATATYPE_SMALLINT.equalsIgnoreCase(dataType)
				|| DataType.DATATYPE_BIGINT.equalsIgnoreCase(dataType)) {
			Long int1 = StringUtil.longValue(str1);
			Long int2 = StringUtil.longValue(str2);
			return int1.compareTo(int2);

		} else if (DataType.DATATYPE_DATE.equalsIgnoreCase(dataType)
				|| DataType.DATATYPE_TIME.equalsIgnoreCase(dataType)
				|| DataType.DATATYPE_TIMESTAMP.equalsIgnoreCase(dataType)) {
			DateFormat dateFormat = null;
			if (DataType.DATATYPE_DATE.equalsIgnoreCase(dataType)) {
				dateFormat = new SimpleDateFormat(FORMAT_DATE,
						Locale.getDefault());
			} else if (DataType.DATATYPE_TIME.equalsIgnoreCase(dataType)) {
				dateFormat = new SimpleDateFormat(FORMAT_TIME,
						Locale.getDefault());
			} else {
				dateFormat = new SimpleDateFormat(FORMAT_TIMESTAMP,
						Locale.getDefault());
			}
			try {
				Date date1 = dateFormat.parse(str1);
				Date date2 = dateFormat.parse(str2);
				return date1.compareTo(date2);
			} catch (ParseException e) {
				return 0;
			}
		} else {
			return str1.compareTo(str2);
		}
	}

	/**
	 * Validate smallint,integer,numberic and bigint's range.
	 * 
	 * @param dataType String
	 * @param digitalNum int
	 * @param numberStr String
	 * @return error messages.
	 */
	public static String validateSeed(String dataType, int digitalNum, String numberStr) {
		BigInteger minValue = null;
		BigInteger maxValue = null;
		if (DataType.DATATYPE_SMALLINT.equals(dataType)) {
			minValue = new BigInteger(Short.MIN_VALUE + "");
			maxValue = new BigInteger(Short.MAX_VALUE + "");
		} else if (DataType.DATATYPE_INTEGER.equals(dataType)) {
			minValue = new BigInteger(Integer.MIN_VALUE + "");
			maxValue = new BigInteger(Integer.MAX_VALUE + "");
		} else if (DataType.DATATYPE_NUMERIC.equals(dataType)) {
			minValue = new BigInteger(DataType.getNumericMinValue(digitalNum));
			maxValue = new BigInteger(DataType.getNumericMaxValue(digitalNum));
		} else if (DataType.DATATYPE_BIGINT.equals(dataType)) {
			minValue = new BigInteger(Long.MIN_VALUE + "");
			maxValue = new BigInteger(Long.MAX_VALUE + "");
		}

		BigInteger value = new BigInteger(numberStr);
		if (minValue == null || maxValue == null) {
			String[] strs = new String[]{numberStr, " ", " " };
			return Messages.bind(Messages.errRange, strs);
		} else if (value.compareTo(maxValue) > 0
				|| value.compareTo(minValue) < 0) {
			String[] strs = new String[]{numberStr, minValue + "",
					maxValue + "" };
			return Messages.bind(Messages.errRange, strs);
		}
		return null;
	}

	/**
	 * Get values to export from resultset.
	 * 
	 * @param colType column data type
	 * @param rs ResultSet
	 * @param colNumber column number
	 * @return the value to be set. String or number.
	 * @throws SQLException The exception
	 */
	public static Object getRsValueForExport(String colType,
			CUBRIDResultSetProxy rs, int colNumber, String nullValue) throws SQLException {
		Object result = null;
		Object data = rs.getObject(colNumber);
		if (data == null || DataType.isNullValueForExport(colType, data)) {
			result = nullValue;
		} else if (DataType.DATATYPE_INTEGER.equals(colType)
				|| DataType.DATATYPE_TINYINT.equals(colType)
				|| DataType.DATATYPE_SMALLINT.equals(colType)
				|| DataType.DATATYPE_BIGINT.equals(colType)) {
			result = rs.getLong(colNumber);
		} else if (DataType.DATATYPE_BIT.equals(colType)
				|| DataType.DATATYPE_BIT_VARYING.equals(colType)) {
			byte[] bytes = (byte[]) data;
			if (bytes.length > BIT_TYPE_MUCH_VALUE_LENGTH
					&& BIT_TYPE_MUCH_VALUE_LENGTH > 0) {
				result = DataType.BIT_EXPORT_FORMAT;
			} else {
				result = "X'" + DBAttrTypeFormatter.getHexString(bytes) + "'";
			}
		} else if (DataType.DATATYPE_DOUBLE.equals(colType)
				|| DataType.DATATYPE_FLOAT.equals(colType)
				|| DataType.DATATYPE_REAL.equals(colType)) {
			result = rs.getDouble(colNumber);
		} else if (DataType.DATATYPE_NUMERIC.equals(colType)
				|| DataType.DATATYPE_DECIMAL.equals(colType)
				|| DataType.DATATYPE_MONETARY.equals(colType)) {
			result = rs.getBigDecimal(colNumber).toString();
		} else if (DataType.DATATYPE_DATETIME.equals(colType)) {
			result = rs.getTimestamp(colNumber);
		} else if (DataType.DATATYPE_TIMESTAMP.equals(colType)) {
			result = rs.getTimestamp(colNumber);
		} else if (DataType.DATATYPE_TIME.equals(colType)) {
			result = rs.getTime(colNumber);
		} else if (DataType.DATATYPE_DATE.equals(colType)) {
			result = rs.getDate(colNumber);
		} else if (DataType.DATATYPE_CLASS.equals(colType)) {
			result = DataType.DATATYPE_CLASS;
		} else if (DataType.DATATYPE_SET.equals(colType)
				|| DataType.DATATYPE_MULTISET.equals(colType)
				|| DataType.DATATYPE_SEQUENCE.equals(colType)) {
			Object[] set = (Object[]) rs.getCollection(colNumber);
			StringBuffer value = new StringBuffer("{");
			for (int k = 0; k < set.length; k++) {
				CUBRIDResultSetMetaDataProxy rsmt = (CUBRIDResultSetMetaDataProxy) rs.getMetaData();
				String elemType = rsmt.getElementTypeName(colNumber);
				if (k > 0) {
					value.append(",");
				}
				Object setk = set[k];
				if (setk == null) {
					value.append(DataType.VALUE_NULL);
				} else if (setk.getClass() == CUBRIDOIDProxy.getCUBRIDOIDClass(rs.getJdbcVersion())) {
					value.append(DataType.VALUE_NULL);
				} else {
					if (DataType.DATATYPE_DATETIME.equals(elemType)) {
						Timestamp datetime = (Timestamp) setk;
						String datetimeStr = formatDateTime(datetime);
						value.append(getCVSValueInSet(elemType, datetimeStr));
					} else {
						value.append(getCVSValueInSet(elemType, setk.toString()));
					}
				}
			}
			value.append("}");
			result = value.toString();
		} else if (DataType.DATATYPE_BLOB.equals(colType)) {
			result = DataType.BLOB_EXPORT_FORMAT;
		} else if (DataType.DATATYPE_CLOB.equals(colType)) {
			result = DataType.CLOB_EXPORT_FORMAT;
		} else {
			result = rs.getString(colNumber);
		}
		return result;
	}

	/**
	 * Get values to export from resultset.
	 * 
	 * @param colType column data type
	 * @param rs ResultSet
	 * @param colNumber column number
	 * @return the value to be set. String or number.
	 * @throws SQLException The exception
	 */
	public static Object getRsValueForExportSQL(String colType,
			CUBRIDResultSetProxy rs, int colNumber) throws SQLException {
		StringBuffer values = new StringBuffer();
		Object data = rs.getObject(colNumber);
		if (data == null || DataType.isNullValueForExport(colType, data)) {
			values.append(DataType.VALUE_NULL);
		} else if (DataType.DATATYPE_MONETARY.equals(colType)
				|| DataType.DATATYPE_INTEGER.equals(colType)
				|| DataType.DATATYPE_TINYINT.equals(colType)
				|| DataType.DATATYPE_SMALLINT.equals(colType)
				|| DataType.DATATYPE_BIGINT.equals(colType)
				|| DataType.DATATYPE_DOUBLE.equals(colType)
				|| DataType.DATATYPE_FLOAT.equals(colType)
				|| DataType.DATATYPE_REAL.equals(colType)
				|| DataType.DATATYPE_NUMERIC.equals(colType)
				|| DataType.DATATYPE_DECIMAL.equals(colType)) {
			values.append(rs.getString(colNumber));
		} else if (DataType.DATATYPE_BIT.equals(colType)
				|| DataType.DATATYPE_BIT_VARYING.equals(colType)) {
			byte[] bytes = (byte[]) data;
			if (bytes.length > BIT_TYPE_MUCH_VALUE_LENGTH
					&& BIT_TYPE_MUCH_VALUE_LENGTH > 0) {
				values.append(DataType.VALUE_NULL);
			} else {
				String bitString = DBAttrTypeFormatter.getHexString(bytes);
				values.append("X'");
				values.append(bitString);
				values.append("'");
			}
		} else if (colType.startsWith(DataType.DATATYPE_NCHAR)) {
			values.append("N'");
			values.append(rs.getString(colNumber).replaceAll("'", "''"));
			values.append("'");
		} else if (colType.equalsIgnoreCase(DataType.DATATYPE_DATETIME)) {
			String datetimeStr = formatDateTime(rs.getTimestamp(colNumber));
			String formatValue = DBAttrTypeFormatter.formatValue(
					DataType.DATATYPE_DATETIME, datetimeStr, true);
			values.append(formatValue);
		} else if (colType.equalsIgnoreCase(DataType.DATATYPE_TIMESTAMP)) {
			String datetime = rs.getString(colNumber);
			String formatValue = DBAttrTypeFormatter.formatValue(
					DataType.DATATYPE_TIMESTAMP, datetime, true);
			values.append(formatValue);
		} else if (colType.equalsIgnoreCase(DataType.DATATYPE_DATE)) {
			String datetime = rs.getString(colNumber);
			String formatValue = DBAttrTypeFormatter.formatValue(
					DataType.DATATYPE_DATE, datetime, true);
			values.append(formatValue);
		} else if (colType.equalsIgnoreCase(DataType.DATATYPE_TIME)) {
			String datetime = rs.getString(colNumber);
			String formatValue = DBAttrTypeFormatter.formatValue(
					DataType.DATATYPE_TIME, datetime, true);
			values.append(formatValue);
		} else if (colType.startsWith(DataType.DATATYPE_CHAR)
				|| colType.startsWith(DataType.DATATYPE_VARCHAR)) {
			values.append("'");
			values.append(rs.getString(colNumber).replaceAll("'", "''"));
			values.append("'");
		} else if (DataType.DATATYPE_CLASS.equals(colType)) {
			values.append(DataType.VALUE_NULL);
		} else if (DataType.DATATYPE_SET.equals(colType)
				|| DataType.DATATYPE_MULTISET.equals(colType)
				|| DataType.DATATYPE_SEQUENCE.equals(colType)) {
			Object[] set = (Object[]) rs.getCollection(colNumber);
			StringBuffer value = new StringBuffer("{");
			CUBRIDResultSetMetaDataProxy rsmt = (CUBRIDResultSetMetaDataProxy) rs.getMetaData();
			String elemType = rsmt.getElementTypeName(colNumber);
			int s = rsmt.getScale(colNumber);
			int p = rsmt.getPrecision(colNumber);
			elemType = DataType.makeType(elemType, null, p, s);
			elemType = DataType.getRealType(elemType);
			for (int k = 0; k < set.length; k++) {
				if (k > 0) {
					value.append(", ");
				}
				Object setK = set[k];
				if (setK == null) {
					value.append(DataType.VALUE_NULL);
				} else if (setK.getClass() == CUBRIDOIDProxy.getCUBRIDOIDClass(rs.getJdbcVersion())) {
					value.append(DataType.VALUE_NULL);
				} else {
					String elem = setK.toString();
					if (elemType.equalsIgnoreCase(DataType.DATATYPE_DATETIME)) {
						Timestamp datetime = (Timestamp) setK;
						elem = formatDateTime(datetime);
					}
					elem = DBAttrTypeFormatter.formatValue(elemType, elem, true);
					value.append(elem);
				}
			}
			value.append("}");
			values.append(value);
		} else {
			values.append(DataType.VALUE_NULL);
		}
		return values.toString();
	}

	/**
	 * Get values to export from resultset.
	 * 
	 * @param colType column data type
	 * @param rs ResultSet
	 * @param colNumber column number
	 * @return the value to be set. String or number.
	 * @throws SQLException The exception
	 */
	public static Object getRsValueForExportOBS(String colType,
			CUBRIDResultSetProxy rs, int colNumber) throws SQLException {
		StringBuffer values = new StringBuffer();
		if (rs.getObject(colNumber) == null || DataType.isNullValueForExport(colType, rs.getObject(colNumber))) {
			values.append(" ").append(DataType.VALUE_NULL);
		} else if (DataType.DATATYPE_MONETARY.equals(colType)
				|| DataType.DATATYPE_INTEGER.equals(colType)
				|| DataType.DATATYPE_TINYINT.equals(colType)
				|| DataType.DATATYPE_SMALLINT.equals(colType)
				|| DataType.DATATYPE_BIGINT.equals(colType)
				|| DataType.DATATYPE_DOUBLE.equals(colType)
				|| DataType.DATATYPE_FLOAT.equals(colType)
				|| DataType.DATATYPE_REAL.equals(colType)
				|| DataType.DATATYPE_NUMERIC.equals(colType)
				|| DataType.DATATYPE_DECIMAL.equals(colType)) {
			values.append(" ");
			values.append(rs.getString(colNumber));
		} else if (DataType.DATATYPE_BIT.equals(colType)
				|| DataType.DATATYPE_BIT_VARYING.equals(colType)) {
			byte[] bytes = rs.getBytes(colNumber);
			String bitString = DBAttrTypeFormatter.getHexString(bytes);
			values.append(" X'");
			values.append(bitString);
			values.append("'");
		} else if (colType.startsWith(DataType.DATATYPE_NCHAR)) {
			values.append(" N'");
			values.append(rs.getString(colNumber).replaceAll("'", "''"));
			values.append("'");
		} else if (colType.startsWith(DataType.DATATYPE_CHAR)
				|| colType.startsWith(DataType.DATATYPE_VARCHAR)) {
			values.append(" '");
			values.append(rs.getString(colNumber).replaceAll("'", "''"));
			values.append("'");
		} else if (DataType.DATATYPE_TIME.equals(colType)) {
			values.append(" ").append(
					DataType.DATATYPE_TIME.toLowerCase(Locale.getDefault())).append(
					"' ");
			values.append(rs.getString(colNumber));
			values.append("'");
		} else if (DataType.DATATYPE_DATE.equals(colType)) {
			values.append(" ").append(
					DataType.DATATYPE_DATE.toLowerCase(Locale.getDefault())).append(
					"' ");
			values.append(rs.getString(colNumber));
			values.append("'");
		} else if (DataType.DATATYPE_TIMESTAMP.equals(colType)) {
			values.append(" ").append(
					DataType.DATATYPE_TIMESTAMP.toLowerCase(Locale.getDefault())).append(
					"' ");
			values.append(rs.getString(colNumber));
			values.append("'");
		} else if (DataType.DATATYPE_DATETIME.equals(colType)) {
			String datetime = formatDateTime(rs.getTimestamp(colNumber));
			values.append(" ").append(
					DataType.DATATYPE_DATETIME.toLowerCase(Locale.getDefault())).append(
					"' ");
			values.append(datetime);
			values.append("'");
		} else if (DataType.DATATYPE_CLASS.equals(colType)) {
			values.append(" ").append(DataType.VALUE_NULL);
		} else if (DataType.DATATYPE_SET.equals(colType)
				|| DataType.DATATYPE_MULTISET.equals(colType)
				|| DataType.DATATYPE_SEQUENCE.equals(colType)) {
			Object[] set = (Object[]) rs.getCollection(colNumber);
			StringBuffer value = new StringBuffer(" {");
			for (int k = 0; k < set.length; k++) {
				if (k > 0) {
					value.append(", ");
				}
				Object setK = set[k];
				if (setK == null) {
					value.append(DataType.VALUE_NULL);
				} else if (setK.getClass() == CUBRIDOIDProxy.getCUBRIDOIDClass(rs.getJdbcVersion())) {
					value.append(DataType.VALUE_NULL);
				} else {
					CUBRIDResultSetMetaDataProxy rsmt = (CUBRIDResultSetMetaDataProxy) rs.getMetaData();
					String elemType = rsmt.getElementTypeName(colNumber);
					String elem = null;
					if (DataType.DATATYPE_DATETIME.equals(elemType)) {
						Timestamp datetime = (Timestamp) setK;
						elem = formatDateTime(datetime);
					} else {
						elem = setK.toString();
					}
					value.append("\"").append(elem.replaceAll("'", "''")).append(
							"\"");
				}
			}
			value.append("}");
			values.append(value);
		} else {
			values.append(" ").append(DataType.VALUE_NULL);
		}
		return values.toString();
	}

	/**
	 * Get value from grid for exporting.
	 * 
	 * @param type column data type
	 * @param dataValue data value
	 * @return the string value.
	 */
	public static String getValueForExport(String type, Object dataValue) {
		if (dataValue == null) {
			return null;
			//value.put(columnInfo.getIndex(), null);
		}
		String data = null;
		if (DataType.DATATYPE_BLOB.equalsIgnoreCase(type)) {
			data = DataType.BLOB_EXPORT_FORMAT;
			//					value.put(columnInfo.getIndex(),
			//							FieldHandlerUtils.BLOB_EXPORT_FORMAT);
		} else if (DataType.DATATYPE_CLOB.equalsIgnoreCase(type)) {
			data = DataType.CLOB_EXPORT_FORMAT;
			//					value.put(columnInfo.getIndex(),
			//							FieldHandlerUtils.CLOB_EXPORT_FORMAT);
		} else {
			data = dataValue.toString();
			//value.put(columnInfo.getIndex(), dataValue.toString());
		}
		return data;
	}

	/**
	 * Set value to xls cell
	 * 
	 * @param sheet WritableSheet
	 * @param col column number
	 * @param row row number
	 * @param colType column data type
	 * @param value value
	 */
	public static void setValue2XlsCell(WritableSheet sheet, int col, int row,
			String colType, String value) {
		try {
			if (value == null) {
				sheet.addCell(new Label(col, row, DataType.NULL_EXPORT_FORMAT));
			} else if (DataType.DATATYPE_INTEGER.equals(colType)
					|| DataType.DATATYPE_TINYINT.equals(colType)
					|| DataType.DATATYPE_SMALLINT.equals(colType)
					|| DataType.DATATYPE_BIGINT.equals(colType)) {
				sheet.addCell(new Number(col, row, Integer.parseInt(value)));
			} else if (DataType.DATATYPE_DOUBLE.equals(colType)
					|| DataType.DATATYPE_FLOAT.equals(colType)
					|| DataType.DATATYPE_REAL.equals(colType)) {
				sheet.addCell(new Number(col, row, Double.parseDouble(value)));
			} else if (DataType.DATATYPE_NUMERIC.equals(colType)
					|| DataType.DATATYPE_DECIMAL.equals(colType)
					|| DataType.DATATYPE_MONETARY.equals(colType)) {
				sheet.addCell(new Number(col, row,
						(new BigDecimal(value)).doubleValue()));
			} else {
				sheet.addCell(new Label(col, row, value.toString()));
			}
		} catch (WriteException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Get data string to write to csv file.
	 * 
	 * @param colType column data type
	 * @param data data value
	 * @return for csv string.
	 */
	public static String getData2WriteCSV(String colType, String data) {
		if (data == null) {
			return DataType.NULL_EXPORT_FORMAT;
			//csvWriter.write(FieldHandlerUtils.NULL_EXPORT_FORMAT);
		}
		String result = DataType.NULL_EXPORT_FORMAT;
		if (DataType.DATATYPE_MONETARY.equals(colType)
				|| DataType.DATATYPE_INTEGER.equals(colType)
				|| DataType.DATATYPE_TINYINT.equals(colType)
				|| DataType.DATATYPE_SMALLINT.equals(colType)
				|| DataType.DATATYPE_BIGINT.equals(colType)
				|| DataType.DATATYPE_DOUBLE.equals(colType)
				|| DataType.DATATYPE_FLOAT.equals(colType)
				|| DataType.DATATYPE_REAL.equals(colType)
				|| DataType.DATATYPE_NUMERIC.equals(colType)
				|| DataType.DATATYPE_DECIMAL.equals(colType)) {
			result = data;
			//csvWriter.write(data);
		} else {
			result = "\"" + data.replaceAll("\"", "\"\"") + "\"";
			//csvWriter.write(("\"" + data.replaceAll("\"", "\"\"") + "\""));
		}
		return result;
	}

	/**
	 * Get time stamp string
	 * 
	 * @param datetime java.sql.Timestamp
	 * @return String
	 */
	public static String formatDateTime(Timestamp datetime) {
		long time = datetime.getTime();
		return DateUtil.getDatetimeString(time, FORMAT_DATETIME);
	}

	/**
	 * Get CVS Value in Set type
	 * 
	 * @param colType String
	 * @param value String
	 * @return value
	 * @throws SQLException if failed
	 */
	private static String getCVSValueInSet(String colType, String value) throws SQLException {
		if (DataType.DATATYPE_CHAR.equals(colType)
				|| DataType.DATATYPE_VARCHAR.equals(colType)
				|| DataType.DATATYPE_NCHAR.equals(colType)
				|| DataType.DATATYPE_NCHAR_VARYING.equals(colType)) {
			return "\'" + value + "\'";
		} else {
			return value;
		}
	}

	/**
	 * 
	 * Get formated real value from imported content
	 * 
	 * @param dataType String
	 * @param content String
	 * @param parent File
	 * @return String
	 */
	public static String getRealValueForImport(String dataType, String content,
			File parent) {
		if (content == null) {
			return null;
		}
		String upperType = dataType.trim().toUpperCase();
		if (DataType.isNullValueForImport(dataType, content)) {
			return null;
		}

		boolean isMuchValue = DBAttrTypeFormatter.isMuchValueType(dataType);
		if (isMuchValue
				&& content.startsWith(FieldHandlerUtils.FILE_URL_PREFIX)) {
			String str = content;
			File file = new File(str.replaceFirst(
					FieldHandlerUtils.FILE_URL_PREFIX, ""));
			if (!file.exists()) {
				file = new File(parent, str.replaceFirst(
						FieldHandlerUtils.FILE_URL_PREFIX, ""));
			}
			if (file.exists()) {
				return FieldHandlerUtils.FILE_URL_PREFIX
						+ file.getAbsolutePath();
			}
		}

		// trim the content
		String[] trimedTypes = {DataType.DATATYPE_INTEGER,
				DataType.DATATYPE_BIGINT, DataType.DATATYPE_SMALLINT,
				DataType.DATATYPE_FLOAT, DataType.DATATYPE_DOUBLE,
				DataType.DATATYPE_NUMERIC, DataType.DATATYPE_SHORT,
				DataType.DATATYPE_DECIMAL, DataType.DATATYPE_REAL,
				DataType.DATATYPE_DATETIME, DataType.DATATYPE_TIMESTAMP,
				DataType.DATATYPE_TIME, DataType.DATATYPE_DATE,
				DataType.DATATYPE_BIT_VARYING, DataType.DATATYPE_BIT,
				DataType.DATATYPE_SEQUENCE, DataType.DATATYPE_MULTISET,
				DataType.DATATYPE_SET, DataType.DATATYPE_MONETARY,
				DataType.DATATYPE_MONETARY, DataType.DATATYPE_INT,
				DataType.DATATYPE_TINYINT, DataType.DATATYPE_CURRENCY,
				DataType.DATATYPE_OID };
		for (String trimedType : trimedTypes) {
			if (upperType.startsWith(trimedType)) {
				return content.trim();
			}
		}
		return content;
	}

	/**
	 * validate whether Default Or Shared
	 * 
	 * @param dataType String
	 * @param value String
	 * @return boolean
	 */
	public static String validateDefaultOrShared(String dataType, String value) {
		String errorMsg = null;
		if (!("").equals(value) && !DBAttrTypeFormatter.validateAttributeValue(dataType, value, true)) { //$NON-NLS-1$		
			if (dataType.equalsIgnoreCase(DataType.DATATYPE_TIMESTAMP)) { //$NON-NLS-1$		
				errorMsg = Messages.invalidTimestamp;
			} else if (dataType.equalsIgnoreCase(DataType.DATATYPE_DATE)) { //$NON-NLS-1$
				errorMsg = Messages.invalidDate;
			} else if (dataType.equalsIgnoreCase(DataType.DATATYPE_TIME)) { //$NON-NLS-1$
				errorMsg = Messages.invalidTime;
			} else if (dataType.equalsIgnoreCase(DataType.DATATYPE_DATETIME)) { //$NON-NLS-1$
				errorMsg = Messages.invalidDatetime;
			} else if (dataType.toLowerCase(Locale.getDefault()).startsWith(
					DataType.DATATYPE_BIT_VARYING)) { //$NON-NLS-1$
				errorMsg = Messages.invalidBitVarying;
			} else if (dataType.toLowerCase(Locale.getDefault()).startsWith(
					DataType.DATATYPE_BIT)) { //$NON-NLS-1$
				errorMsg = Messages.invalidBit;
			} else {
				errorMsg = Messages.bind(Messages.errParseValue2DataType, dataType);
			}
		}
		return errorMsg;
	}

	/**
	 * 
	 * Return the field type can support auto increment.
	 * 
	 * @param dataType Field type
	 * @param numSize int
	 * @param precision int
	 * @return true or false.
	 */
	public static boolean canAutoIncrement(String dataType, int numSize,
			int precision) {
		boolean show = false;
		if (DataType.DATATYPE_SMALLINT.equals(dataType)
				|| DataType.DATATYPE_INTEGER.equals(dataType)
				|| DataType.DATATYPE_BIGINT.equals(dataType)) { //$NON-NLS-1$ //$NON-NLS-2$
			show = true;
		} else if (DataType.DATATYPE_NUMERIC.equals(dataType) && numSize > 0
				&& precision == 0) { //$NON-NLS-1$
			show = true;
		}
		return show;
	}

	/**
	 * Make data type by recursion
	 * 
	 * @param dataType String
	 * @param size String
	 * @param precision String
	 * @param subdateType String
	 * @return string
	 */
	public static String interMakeType(String dataType, String size,
			String precision, String subdateType) {
		// 0 : direct input, 1 ~ 6 : need size, 7 : need precision, scale, 17 ~
		// 19 : set type.
		if (DataType.DATATYPE_CHAR.equals(dataType)
				|| DataType.DATATYPE_BIT.equals(dataType) //$NON-NLS-1$ //$NON-NLS-2$
				|| DataType.DATATYPE_VARCHAR.equals(dataType)
				|| DataType.DATATYPE_BIT_VARYING.equals(dataType) //$NON-NLS-1$ //$NON-NLS-2$
				|| DataType.DATATYPE_NCHAR.equals(dataType)
				|| DataType.DATATYPE_NCHAR_VARYING.equals(dataType)) { //$NON-NLS-1$ //$NON-NLS-2$
			//			hidePrecision();
			//			showSize();
			return dataType + "(" + size + ")"; //$NON-NLS-1$ //$NON-NLS-2$

		} else if (DataType.DATATYPE_NUMERIC.equals(dataType)) { //$NON-NLS-1$
			//			showSize();
			//			showPrecision();
			return dataType + "(" + size + "," //$NON-NLS-1$ //$NON-NLS-2$
					+ precision + ")"; //$NON-NLS-1$
		} else if (DataType.DATATYPE_SET.equals(dataType)
				|| DataType.DATATYPE_MULTISET.equals(dataType) //$NON-NLS-1$ //$NON-NLS-2$
				|| DataType.DATATYPE_SEQUENCE.equals(dataType)) { //$NON-NLS-1$
			return dataType
					+ "(" + interMakeType(subdateType, size, precision, subdateType) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			return dataType;
		}
	}

	//	/**
	//	 * validate Increment Field
	//	 * 
	//	 * @param dataType String
	//	 * @param numberStr String
	//	 * @param digitalNum int
	//	 * @return error messages.if it has no errors,returns null.
	//	 */
	//	public static String validateIncrementField(String dataType,
	//			String numberStr, int digitalNum) {
	//		boolean validFormat = ValidateUtil.isInteger(numberStr);
	//		if (!validFormat) {
	//			return Messages.bind(Messages.errNumber, numberStr);
	//		}
	//		BigInteger minValue = BigInteger.ONE;
	//		BigInteger maxValue = BigInteger.ZERO;
	//		if (DataType.DATATYPE_SMALLINT.equals(dataType)) {
	//			maxValue = new BigInteger(Short.MAX_VALUE + "");
	//		} else if (DataType.DATATYPE_INTEGER.equals(dataType)) {
	//			maxValue = new BigInteger(Integer.MAX_VALUE + "");
	//		} else if (DataType.DATATYPE_NUMERIC.equals(dataType)) {
	//			maxValue = new BigInteger(DataType.getNumericMaxValue(digitalNum));
	//		} else if (DataType.DATATYPE_BIGINT.equals(dataType)) {
	//			maxValue = new BigInteger(Long.MAX_VALUE + "");
	//		}
	//		BigInteger value = new BigInteger(numberStr);
	//		if (value.compareTo(BigInteger.ZERO) <= 0) {
	//			return Messages.bind(Messages.errIncrement, numberStr);
	//		}
	//		if (value.compareTo(maxValue) > 0 || value.compareTo(minValue) < 0) {
	//			String[] strs = new String[]{numberStr, minValue + "",
	//					maxValue + "" };
	//			return Messages.bind(Messages.errRange, strs);
	//		}
	//		return null;
	//	}

	//	/**
	//	 * Validate Current value of auto increments.
	//	 * 
	//	 * @param dataType String
	//	 * @param numberStr String
	//	 * @param seed String
	//	 * @param digitalNum int
	//	 * @return true:is valid;false:is not valid.
	//	 */
	//	public static String validateCurVal(String dataType, String numberStr,
	//			String seed, int digitalNum) {
	//		boolean validFormat = ValidateUtil.isInteger(numberStr);
	//		if (!validFormat) {
	//			return Messages.bind(Messages.errNumber, numberStr);
	//		}
	//		BigInteger minValue = null;
	//		BigInteger maxValue = null;
	//		boolean seedIsEmpty = seed == null || seed.equals("");
	//		if (DataType.DATATYPE_SMALLINT.equals(dataType)) {
	//			short minV = 0;
	//			if (seedIsEmpty) {
	//				minV = Short.MIN_VALUE;
	//			} else {
	//				minV = Short.valueOf(seed);
	//			}
	//			minValue = new BigInteger(minV + "");
	//			maxValue = new BigInteger(Short.MAX_VALUE + "");
	//		} else if (DataType.DATATYPE_INTEGER.equals(dataType)) {
	//			int minV = 0;
	//			if (seedIsEmpty) {
	//				minV = Integer.MIN_VALUE;
	//			} else {
	//				minV = Integer.valueOf(seed);
	//			}
	//			minValue = new BigInteger(minV + "");
	//			maxValue = new BigInteger(Integer.MAX_VALUE + "");
	//		} else if (DataType.DATATYPE_NUMERIC.equals(dataType)) {
	//			minValue = new BigInteger(DataType.getNumericMinValue(digitalNum));
	//			maxValue = new BigInteger(DataType.getNumericMaxValue(digitalNum));
	//			if (!seedIsEmpty) {
	//				BigInteger bigSeed = new BigInteger(seed.toString());
	//				minValue = minValue.compareTo(bigSeed) >= 0 ? minValue
	//						: bigSeed;
	//			}
	//		} else if (DataType.DATATYPE_BIGINT.equals(dataType)) {
	//			long minV = 0;
	//			if (seedIsEmpty) {
	//				minV = Long.MIN_VALUE;
	//			} else {
	//				minV = Long.valueOf(seed);
	//			}
	//			minValue = new BigInteger(minV + "");
	//			maxValue = new BigInteger(Long.MAX_VALUE + "");
	//		}
	//
	//		BigInteger value = new BigInteger(numberStr);
	//
	//		if (minValue == null || maxValue == null) {
	//			String[] strs = new String[]{numberStr, " ", " " };
	//			return Messages.bind(Messages.errRange, strs);
	//		} else if (value.compareTo(maxValue) > 0
	//				|| value.compareTo(minValue) < 0) {
	//			String[] strs = new String[]{numberStr, minValue + "",
	//					maxValue + "" };
	//			return Messages.bind(Messages.errRange, strs);
	//		}
	//		return null;
	//	}

	/**
	 * 
	 * Fill in PreparedStatement parameter value
	 * 
	 * @param parameter PstmtParameter
	 * @param pstmt PreparedStatement
	 * @param dbCharSet String
	 * @throws SQLException The exception
	 */
	public static void setPreparedStatementValue(PstmtParameter parameter,
			PreparedStatement pstmt, String dbCharSet) throws SQLException {
		String newDbCharSet = dbCharSet;
		String fileCharSet = parameter.getCharSet();
		if (dbCharSet == null) {
			newDbCharSet = StringUtil.getDefaultCharset();
		}
		Object realObj = null;
		String type = DataType.getRealType(parameter.getDataType());
		boolean isMuchValue = DBAttrTypeFormatter.isMuchValueType(type);
		FormatDataResult formatDataResult = DBAttrTypeFormatter.format(type,
				parameter.getStringParamValue(), false, newDbCharSet, true);
		String errorMsg = null;
		if (formatDataResult.isSuccess()) {
			Object obj = formatDataResult.getFormatedJavaObj();
			if (isMuchValue && obj instanceof String) {
				realObj = DBAttrTypeFormatter.formatMuchValue((String) obj,
						type, pstmt.getConnection(), newDbCharSet, fileCharSet, true);
			} else {
				realObj = obj;
			}
			if (realObj instanceof Exception) {
				errorMsg = ((Exception) realObj).getMessage();
			} else {
				if (realObj instanceof Object[]) {
					Object[] objs = DataType.getCollectionValues(type,
							(Object[]) realObj, true);
					((CUBRIDPreparedStatementProxy) pstmt).setCollection(
							parameter.getParamIndex(), objs);
				} else {
					pstmt.setObject(parameter.getParamIndex(), realObj);
				}
			}
		} else {
			errorMsg = Messages.bind(Messages.errParaTypeValueMapping,
					new String[]{parameter.getStringParamValue(), type });
		}
		if (errorMsg != null) {
			throw new SQLException(errorMsg, "", -10000);
		}
	}

	/**
	 * The data type support size attribute.
	 * 
	 * @param dataType String
	 * @return true:support;
	 */
	public static boolean isSupportSize(String dataType) {
		boolean result = false;
		if (DataType.DATATYPE_CHAR.equals(dataType)
				|| DataType.DATATYPE_BIT.equals(dataType)) { //$NON-NLS-1$ //$NON-NLS-2$
			result = true;
		} else if (DataType.DATATYPE_VARCHAR.equals(dataType)
				|| DataType.DATATYPE_BIT_VARYING.equals(dataType)) { //$NON-NLS-1$ //$NON-NLS-2$
			result = true;
		} else if (DataType.DATATYPE_NCHAR.equals(dataType)) { //$NON-NLS-1$
			result = true;
		} else if (DataType.DATATYPE_NCHAR_VARYING.equals(dataType)) { //$NON-NLS-1$
			result = true;
		} else if (DataType.DATATYPE_NUMERIC.equals(dataType)) { //$NON-NLS-1$
			result = false;
		} else if (DataType.DATATYPE_SET.equals(dataType)
				|| DataType.DATATYPE_MULTISET.equals(dataType) //$NON-NLS-1$ //$NON-NLS-2$
				|| DataType.DATATYPE_SEQUENCE.equals(dataType)) { //$NON-NLS-1$
			result = false;
		}

		return result;
	}

	/**
	 * 
	 * Return whether support prefix index length
	 * 
	 * @param dataType String
	 * @return boolean
	 */
	public static boolean isSupportPrefixIndex(String dataType) {
		String type = dataType.toUpperCase(Locale.getDefault());
		return type.startsWith(DataType.DATATYPE_VARCHAR)
				|| type.startsWith(DataType.DATATYPE_CHAR)
				|| type.startsWith(DataType.DATATYPE_STRING)
				|| type.startsWith(DataType.DATATYPE_BIT)
				|| type.startsWith(DataType.DATATYPE_BIT_VARYING)
				|| type.startsWith(DataType.DATATYPE_NCHAR)
				|| type.startsWith(DataType.DATATYPE_NCHAR_VARYING);
	}

	/**
	 * The data type support sub data type attribute.
	 * 
	 * @param dataType String
	 * @return true:support;
	 */
	public static boolean isSupportSubDataType(String dataType) {
		if (DataType.DATATYPE_SET.equals(dataType)
				|| DataType.DATATYPE_MULTISET.equals(dataType) //$NON-NLS-1$ //$NON-NLS-2$
				|| DataType.DATATYPE_SEQUENCE.equals(dataType)) { //$NON-NLS-1$
			return true;
		}
		return false;
	}

	/**
	 * The data type support scale attribute.
	 * 
	 * @param dataType String
	 * @return true:support;
	 */
	public static boolean isSupportScale(String dataType) {
		return DataType.DATATYPE_NUMERIC.equals(dataType);
	}

	/**
	 * The data type support enum values.
	 * 
	 * @param dataType String
	 * @return true:support;
	 */
	public static boolean isSupportEnum(String dataType) {
		return DataType.DATATYPE_ENUM.equalsIgnoreCase(dataType);
	}
	/**
	 * The data type support precision attribute.
	 * 
	 * @param dataType String
	 * @return true:support;
	 */
	public static boolean isSupportPrecision(String dataType) {
		return DataType.DATATYPE_NUMERIC.equals(dataType);
	}

	/**
	 * The data type support auto increments attribute.
	 * 
	 * @param isInstance boolean
	 * @param isShared boolean
	 * @param defaultValue String
	 * @param dataType String
	 * @param precision precision
	 * @param scale String
	 * @return ture: support
	 */
	public static boolean isSupportAutoInc(boolean isInstance,
			boolean isShared, String defaultValue, String dataType,
			String precision, String scale) {
		if (!isInstance) {
			return false;
		}
		if (isShared) {
			return false;
		}
		if (StringUtil.isNotEmpty(defaultValue)) {
			return false;
		}
		if (DataType.DATATYPE_SMALLINT.equals(dataType)
				|| DataType.DATATYPE_INTEGER.equals(dataType)
				|| DataType.DATATYPE_BIGINT.equals(dataType)) { //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		} else if (DataType.DATATYPE_NUMERIC.equals(dataType)
				&& (null != precision && null != scale)) { //$NON-NLS-1$
			String sizeStr = precision;
			String scaleStr = scale;
			if (sizeStr.matches(PATTEN_INTEGER2)
					&& scaleStr.matches(PATTEN_INTEGER2)) {
				int size = Integer.parseInt(sizeStr);
				int sc = Integer.parseInt(scale);
				if (size > 0 && sc == 0) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * The data type support default value attribute.
	 * 
	 * @param dataType String
	 * @return true:support;
	 */
	public static boolean isSupportDefaultValue(String dataType) {
		if (DataType.DATATYPE_BLOB.equals(dataType)) {
			return false;
		} else if (DataType.DATATYPE_CLOB.equals(dataType)) {
			return false;
		}
		return true;
	}

	/**
	 * The data type support shared attribute.
	 * 
	 * @param dataType String
	 * @return true:support;
	 */
	public static boolean isSupportShare(String dataType) {
		if (DataType.DATATYPE_BLOB.equals(dataType)) {
			return false;
		} else if (DataType.DATATYPE_CLOB.equals(dataType)) {
			return false;
		}
		return true;
	}

	/**
	 * The data type support unique attribute.
	 * 
	 * @param isInstance boolean
	 * @param isShared boolean
	 * @param dataType String
	 * @return true:support.
	 */
	public static boolean isSupportUnique(boolean isInstance, boolean isShared,
			String dataType) {
		boolean shouldEnable = true;
		if (isInstance) { //instance
			if (isShared) {
				shouldEnable = false;
			}
			if (DataType.DATATYPE_SET.equals(dataType)
					|| DataType.DATATYPE_MULTISET.equals(dataType) //$NON-NLS-1$ //$NON-NLS-2$
					|| DataType.DATATYPE_SEQUENCE.equals(dataType)
					|| DataType.DATATYPE_BLOB.equals(dataType)
					|| DataType.DATATYPE_CLOB.equals(dataType)) { //$NON-NLS-1$
				shouldEnable = false;
			}
		} else {
			shouldEnable = false;
		}
		return shouldEnable;
	}

	/**
	 * If support max size,return max size of the field
	 * 
	 * @param dataType String
	 * @return max size
	 */
	public static String getMaxSize(String dataType) {
		if (DataType.DATATYPE_CHAR.equals(dataType)
				|| DataType.DATATYPE_BIT.equals(dataType)) { //$NON-NLS-1$ //$NON-NLS-2$
			return MAXSIZE;
		} else if (DataType.DATATYPE_VARCHAR.equals(dataType)
				|| DataType.DATATYPE_BIT_VARYING.equals(dataType)) { //$NON-NLS-1$ //$NON-NLS-2$
			return MAXSIZE;
		} else if (DataType.DATATYPE_NCHAR.equals(dataType)) { //$NON-NLS-1$
			return NCHARMAXSIZE;
		} else if (DataType.DATATYPE_NCHAR_VARYING.equals(dataType)) { //$NON-NLS-1$
			return NCHARMAXSIZE;
		} else if (DataType.DATATYPE_NUMERIC.equals(dataType)) { //$NON-NLS-1$
			return MAX_FLOAT_DIGITNUM;
		}
		return null;
	}

	/**
	 * Get the min size if the field support size attribute.
	 * 
	 * @param dataType String
	 * @return min size
	 */
	public static String getMinSize(String dataType) {
		return "1";
	}

	/**
	 * Get the max scale if the field support scale attribute.
	 * 
	 * @param dataType String
	 * @return max scale
	 */
	public static String getMaxScale(String dataType) {
		if (DataType.DATATYPE_NUMERIC.equals(dataType)) { //$NON-NLS-1$
			return MAX_FLOAT_DIGITNUM;
		}
		return null;
	}

	/**
	 * Get the min scale if the field support scale attribute.
	 * 
	 * @param dataType String
	 * @return min scale
	 */
	public static String getMinScale(String dataType) {
		return "0";
	}

	/**
	 * Get default size
	 * 
	 * @param dataType String
	 * @return default size.
	 */
	public static String getDefaultSize(String dataType) {
		if (DataType.DATATYPE_CHAR.equals(dataType)
				|| DataType.DATATYPE_BIT.equals(dataType)) { //$NON-NLS-1$ //$NON-NLS-2$
			return "1";
		} else if (DataType.DATATYPE_VARCHAR.equals(dataType)
				|| DataType.DATATYPE_BIT_VARYING.equals(dataType)) { //$NON-NLS-1$ //$NON-NLS-2$
			return "4096";
		} else if (DataType.DATATYPE_NCHAR.equals(dataType)) { //$NON-NLS-1$
			return "1";
		} else if (DataType.DATATYPE_NCHAR_VARYING.equals(dataType)) { //$NON-NLS-1$
			return "4096";
		} else if (DataType.DATATYPE_NUMERIC.equals(dataType)) { //$NON-NLS-1$
			return "15";
		}
		return null;
	}

	/**
	 * Get the default scale if the field support scale attribute.
	 * 
	 * @param dataType String
	 * @return default scale
	 */
	public static String getDefaultScale(String dataType) {
		if (DataType.DATATYPE_NUMERIC.equals(dataType)) { //$NON-NLS-1$
			return "0";
		}
		return null;
	}

	/**
	 * Get max value.
	 * 
	 * @param dataType String
	 * @param size String
	 * @return max value
	 */
	public static BigInteger getMaxValue(String dataType, String size) {
		if (DataType.DATATYPE_SMALLINT.equals(dataType)) {
			return new BigInteger(Short.MAX_VALUE + "");
		} else if (DataType.DATATYPE_INTEGER.equals(dataType)) {
			return new BigInteger(Integer.MAX_VALUE + "");
		} else if (DataType.DATATYPE_NUMERIC.equals(dataType)) {
			int digitalNum = Integer.parseInt(size);
			return new BigInteger(DataType.getNumericMaxValue(digitalNum));
		} else if (DataType.DATATYPE_BIGINT.equals(dataType)) {
			return new BigInteger(Long.MAX_VALUE + "");
		}
		return null;
	}

	/**
	 * Get min value.
	 * 
	 * @param dataType String
	 * @param seed String
	 * @param size String
	 * @return min value
	 */
	public static BigInteger getMinValue(String dataType, String seed,
			String size) {
		boolean seedIsEmpty = seed == null || seed.equals("");
		if (DataType.DATATYPE_SMALLINT.equals(dataType)) {
			short minV = 0;
			if (seedIsEmpty) {
				minV = Short.MIN_VALUE;
			} else {
				minV = Short.valueOf(seed);
			}
			return new BigInteger(minV + "");
		} else if (DataType.DATATYPE_INTEGER.equals(dataType)) {
			int minV = 0;
			if (seedIsEmpty) {
				minV = Integer.MIN_VALUE;
			} else {
				minV = Integer.valueOf(seed);
			}
			return new BigInteger(minV + "");
		} else if (DataType.DATATYPE_NUMERIC.equals(dataType)) {
			int digitalNum = Integer.parseInt(size);
			BigInteger minValue = new BigInteger(
					DataType.getNumericMinValue(digitalNum));
			if (!seedIsEmpty) {
				BigInteger bigSeed = new BigInteger(seed.toString());
				minValue = minValue.compareTo(bigSeed) >= 0 ? minValue
						: bigSeed;
			}
			return minValue;
		} else if (DataType.DATATYPE_BIGINT.equals(dataType)) {
			long minV = 0;
			if (seedIsEmpty) {
				minV = Long.MIN_VALUE;
			} else {
				minV = Long.valueOf(seed);
			}
			return new BigInteger(minV + "");
		}
		return null;
	}

	/**
	 * If the default value is invalidate, return the error message by data
	 * type.
	 * 
	 * @param dataType String
	 * @param value String
	 * @return error message
	 */
	public static String getErrorValueMsg(String dataType, String value) {
		if (dataType.equalsIgnoreCase(DataType.DATATYPE_TIMESTAMP)) { //$NON-NLS-1$		
			return (Messages.invalidTimestamp);
		} else if (dataType.equalsIgnoreCase(DataType.DATATYPE_DATE)) { //$NON-NLS-1$
			return (Messages.invalidDate);
		} else if (dataType.equalsIgnoreCase(DataType.DATATYPE_TIME)) { //$NON-NLS-1$
			return (Messages.invalidTime);
		} else if (dataType.equalsIgnoreCase(DataType.DATATYPE_DATETIME)) { //$NON-NLS-1$
			return (Messages.invalidDatetime);
		} else if (dataType.toLowerCase(Locale.getDefault()).startsWith(
				DataType.DATATYPE_BIT_VARYING)) { //$NON-NLS-1$
			return (Messages.invalidBitVarying);
		} else if (dataType.toLowerCase(Locale.getDefault()).startsWith(
				DataType.DATATYPE_BIT)) { //$NON-NLS-1$
			return (Messages.invalidBit);
		} else {
			String msg = Messages.bind(Messages.errParseValue2DataType, dataType);
			return (msg);
		}
	}

	/**
	 * 
	 * Init the sql type map
	 * 
	 * @param sqlTypeMap Map<String, String>
	 */
	public static void initSqlTypeMap(Map<String, String> sqlTypeMap) {
		sqlTypeMap.put(DataType.DATATYPE_CHAR, "1");
		sqlTypeMap.put(DataType.DATATYPE_VARCHAR, "1");
		sqlTypeMap.put(DataType.DATATYPE_STRING, "1");
		sqlTypeMap.put(DataType.DATATYPE_NUMERIC, "2");
		sqlTypeMap.put(DataType.DATATYPE_BIGINT, "2");
		sqlTypeMap.put(DataType.DATATYPE_SHORT, "2");
		sqlTypeMap.put(DataType.DATATYPE_INT, "2");
		sqlTypeMap.put(DataType.DATATYPE_FLOAT, "2");
		sqlTypeMap.put(DataType.DATATYPE_DOUBLE, "2");
		sqlTypeMap.put(DataType.DATATYPE_CURRENCY, "2");
		sqlTypeMap.put(DataType.DATATYPE_DATE, "3");
		sqlTypeMap.put(DataType.DATATYPE_DATETIME, "3");
		sqlTypeMap.put(DataType.DATATYPE_TIME, "3");
		sqlTypeMap.put(DataType.DATATYPE_TIMESTAMP, "3");
		sqlTypeMap.put(DataType.DATATYPE_SET, "4");
		sqlTypeMap.put(DataType.DATATYPE_MULTISET, "4");
		sqlTypeMap.put(DataType.DATATYPE_SEQUENCE, "4");
		sqlTypeMap.put(DataType.DATATYPE_OBJECT, "5");
		sqlTypeMap.put(DataType.DATATYPE_CURSOR, "6");
	}

	/**
	 * Return the value's string according to the type
	 * 
	 * @param value Object
	 * @param type String
	 * @return String value
	 */
	public static String getValidDefaultValue(Object value, String type) {
		if (value == null) {
			return null;
		}
		String val = String.valueOf(value);
		if ((DataType.DATATYPE_INTEGER.equalsIgnoreCase(type)
				|| DataType.DATATYPE_FLOAT.equalsIgnoreCase(type)
				|| type.startsWith(DataType.DATATYPE_NUMERIC)
				|| DataType.DATATYPE_BIGINT.equalsIgnoreCase(type)
				|| DataType.DATATYPE_SMALLINT.equalsIgnoreCase(type) || DataType.DATATYPE_DOUBLE.equalsIgnoreCase(type))
				&& !val.matches(PATTEN_NUMBER)) {
			val = null;
		}
		return val;
	}

	/**
	 * 
	 * Get complete type
	 * 
	 * @param type String
	 * @param elementType String
	 * @param precision int
	 * @param scale int
	 * 
	 * @return String
	 */
	public static String getComleteType(String type, String elementType,
			int precision, int scale) {
		return getComleteType(type, elementType, precision, scale, false);
	}

	/**
	 * 
	 * Get complete type
	 * 
	 * @param type String
	 * @param elementType String
	 * @param precision int
	 * @param scale int
	 * 
	 * @return String
	 */
	public static String getComleteType(String type, String elementType,
			int precision, int scale, boolean isShort) {
		
		String precisionStr = String.valueOf(precision);

		if (isShort && type != null && type.length() > 3) {
			type = type.substring(0, 3);
		}
		
		if (isShort && precisionStr != null) {
			if (precisionStr.length() > 6) {
				precisionStr = String.valueOf((int)(precision / 1000000)) + "m";
			}
			else if (precisionStr.length() > 3) {
				precisionStr = String.valueOf((int)(precision / 1000)) + "k";
			}
		}
		
		if (DataType.DATATYPE_SEQUENCE.equalsIgnoreCase(type)
				|| DataType.DATATYPE_MULTISET.equalsIgnoreCase(type)
				|| DataType.DATATYPE_SET.equalsIgnoreCase(type)) {
			StringBuffer typeBuffer = new StringBuffer(type);
			if (StringUtil.isNotEmpty(elementType)) {
				typeBuffer.append("(").append(elementType);
				if (precision > 0) {
					typeBuffer.append("(").append(precision);
					if ((DataType.DATATYPE_NUMERIC.equalsIgnoreCase(elementType) || DataType.DATATYPE_DECIMAL.equalsIgnoreCase(elementType))
							&& scale >= 0) {
						typeBuffer.append(",").append(scale);
					}
					typeBuffer.append(")");
				}
				typeBuffer.append(")");
			}
			return typeBuffer.toString();
		}
		if ((DataType.DATATYPE_NUMERIC.equalsIgnoreCase(type) || DataType.DATATYPE_DECIMAL.equalsIgnoreCase(type))
				&& precision > 0 && scale >= 0) {
			return type + "(" + precision + "," + scale + ")";
		}
		if (precision > 0) {
			return type + "(" + precisionStr + ")";
		}
		return type;
	}

	/**
	 * 
	 * Compare database datas
	 * 
	 * @param columnType column Type
	 * @param str1 String
	 * @param str2 String
	 * @param isAsc is Asc
	 * @return -1,1,0
	 */
	public static int comparedDBValues(String columnType, String str1,
			String str2, boolean isAsc) {
		if ((str1.length() == 0 && str2.length() > 0)
				|| (DataType.VALUE_NULL.equals(str1) && !DataType.VALUE_NULL.equals(str2))) {
			return isAsc ? -1 : 1;
		} else if ((str1.length() == 0 && str2.length() == 0)
				|| (DataType.VALUE_NULL.equals(str1) && DataType.VALUE_NULL.equals(str2))) {
			return 0;
		} else if ((str1.length() > 0 && str2.length() == 0)
				|| (!DataType.VALUE_NULL.equals(str1) && DataType.VALUE_NULL.equals(str2))) {
			return isAsc ? 1 : -1;
		}
		if (DataType.DATATYPE_OID.equals(columnType)) {
			if (DataType.VALUE_NONE.equals(str1)
					&& !DataType.VALUE_NONE.equals(str2)) {
				return isAsc ? -1 : 1;
			} else if (DataType.VALUE_NONE.equals(str1)
					&& DataType.VALUE_NONE.equals(str2)) {
				return 0;
			} else if (!DataType.VALUE_NONE.equals(str1)
					&& DataType.VALUE_NONE.equals(str2)) {
				return isAsc ? 1 : -1;
			}
			String[] str1Arr = str1.split("\\|");
			String[] str2Arr = str2.split("\\|");
			if (str1Arr.length < 3 || str2Arr.length < 3) {
				return 0;
			}
			String str11 = str1Arr[0].replace("@", "").trim();
			String str21 = str2Arr[0].replace("@", "").trim();
			Integer int11 = Integer.valueOf(str11);
			Integer int21 = Integer.valueOf(str21);
			if (int11.compareTo(int21) == 0) {
				Integer int12 = Integer.valueOf(str1Arr[1].trim());
				Integer int22 = Integer.valueOf(str2Arr[1].trim());
				if (int12.compareTo(int22) == 0) {
					Integer int13 = Integer.valueOf(str1Arr[2].trim());
					Integer int23 = Integer.valueOf(str2Arr[2].trim());
					return isAsc ? int13.compareTo(int23)
							: int23.compareTo(int13);
				} else {
					return isAsc ? int12.compareTo(int22)
							: int22.compareTo(int12);
				}
			} else {
				return isAsc ? int11.compareTo(int21) : int21.compareTo(int11);
			}

		} else if (DataType.DATATYPE_BIGINT.equals(columnType)) {
			Long long1 = Long.valueOf(str1);
			Long long2 = Long.valueOf(str2);
			return isAsc ? long1.compareTo(long2) : long2.compareTo(long1);

		} else if (DataType.DATATYPE_INTEGER.equals(columnType)
				|| DataType.DATATYPE_SMALLINT.equals(columnType)) {
			Integer int1 = Integer.valueOf(str1);
			Integer int2 = Integer.valueOf(str2);
			return isAsc ? int1.compareTo(int2) : int2.compareTo(int1);

		} else if (DataType.DATATYPE_NUMERIC.equals(columnType)
				|| DataType.DATATYPE_FLOAT.equals(columnType)
				|| DataType.DATATYPE_DOUBLE.equals(columnType)
				|| DataType.DATATYPE_REAL.equals(columnType)) {
			Double double1 = Double.valueOf(str1);
			Double dobule2 = Double.valueOf(str2);
			return isAsc ? double1.compareTo(dobule2)
					: dobule2.compareTo(double1);

		} else if (DataType.DATATYPE_DATE.equals(columnType)
				|| DataType.DATATYPE_TIME.equals(columnType)
				|| DataType.DATATYPE_TIMESTAMP.equals(columnType)
				|| DataType.DATATYPE_DATETIME.equals(columnType)) {
			DateFormat dateFormat = null;
			if (DataType.DATATYPE_DATE.equals(columnType)) {
				dateFormat = new SimpleDateFormat(FORMAT_DATE,
						Locale.getDefault());
			} else if (DataType.DATATYPE_TIME.equals(columnType)) {
				dateFormat = new SimpleDateFormat(FORMAT_TIME,
						Locale.getDefault());
			} else if (DataType.DATATYPE_TIMESTAMP.equals(columnType)) {
				dateFormat = new SimpleDateFormat(FORMAT_TIMESTAMP,
						Locale.getDefault());
			} else {
				dateFormat = new SimpleDateFormat(FORMAT_DATETIME,
						Locale.getDefault());
			}
			try {
				Date date1 = dateFormat.parse(str1);
				Date date2 = dateFormat.parse(str2);
				return isAsc ? date1.compareTo(date2) : date2.compareTo(date1);
			} catch (ParseException e) {
				LOGGER.error("", e);
			}
			return 0;
		} else {
			return isAsc ? str1.compareTo(str2) : str2.compareTo(str1);
		}
	}

	/**
	 * Is the type is bit type.
	 * 
	 * @param columnType String
	 * @param precision int
	 * @return true: is byte array.
	 */
	public static boolean isBitValue(String columnType, int precision) {
		if ((DataType.DATATYPE_BIT.equals(columnType) || DataType.DATATYPE_BIT_VARYING.equals(columnType))
				&& precision > FieldHandlerUtils.BIT_TYPE_MUCH_VALUE_LENGTH) {
			return true;
		}
		if (DataType.DATATYPE_BLOB.equals(columnType)
				|| DataType.DATATYPE_CLOB.equals(columnType)) {
			return true;
		}
		return false;
	}

	/**
	 * Get the parameter string for sql statement.
	 * 
	 * @param type String data type
	 * @return parameter string ? or cast(? as bit varying)
	 */
	public static String getParamString(String type) {
		String paramString = "?";
		if (type.startsWith(DataType.DATATYPE_BIT)) { //$NON-NLS-1$
			paramString = "cast(? as bit varying)"; //$NON-NLS-1$
		}
		return paramString;
	}

	/**
	 * JDBC has some errors that cannot get BLOB and CLOB type correctly, So we
	 * have to get BLOB and CLOB type from result object.
	 * 
	 * @param rs ResultSet
	 * @param colIndex column index
	 * @param columnType Data Type
	 * @return BLOB or CLOB type or the origin type
	 * @throws SQLException SQLException
	 */
	public static String amendDataTypeByResult(ResultSet rs, int colIndex,
			String columnType) throws SQLException {
		String result = columnType == null ? "" : columnType;
		Object obj = rs.getObject(colIndex);
		if (obj instanceof Blob) {
			result = DataType.DATATYPE_BLOB;
		} else if (obj instanceof Clob) {
			result = DataType.DATATYPE_CLOB;
		}
		return result;
	}

	/**
	 * Get the data type in excel based on the data type in database for xlsx
	 * format
	 * 
	 * @param dataType String the data type
	 * @param cellValue String
	 * @return int -1,0,1,2,3,4,5,10
	 */
	public static int getCellType(String dataType, String cellValue) {
		int result = -1;
		if (dataType == null || cellValue == null
				|| DataType.isNullValueForImport(dataType, cellValue)) {
			return result;
		}
		String shownType = DataType.getShownType(dataType);
		if (DataType.DATATYPE_INTEGER.endsWith(shownType)
				|| DataType.DATATYPE_TINYINT.equals(shownType)
				|| DataType.DATATYPE_SMALLINT.equals(shownType)
				|| DataType.DATATYPE_BIGINT.equals(shownType)) {
			result = 0;
		} else if (DataType.DATATYPE_DOUBLE.equals(shownType)
				|| DataType.DATATYPE_FLOAT.equals(shownType)
				|| DataType.DATATYPE_REAL.equals(shownType)) {
			result = 1;
		} else if (DataType.DATATYPE_NUMERIC.equals(shownType)
				|| DataType.DATATYPE_DECIMAL.equals(shownType)
				|| DataType.DATATYPE_MONETARY.equals(shownType)) {
			result = 1;
		} else if (DataType.DATATYPE_DATETIME.equals(shownType)) {
			result = 2;
		} else if (DataType.DATATYPE_TIMESTAMP.equals(shownType)) {
			result = 3;
		} else if (DataType.DATATYPE_DATE.equals(shownType)) {
			result = 4;
		} else if (DataType.DATATYPE_TIME.equals(shownType)) {
			result = 5;
		} else {
			result = 10;
		}
		return result;
	}
}
