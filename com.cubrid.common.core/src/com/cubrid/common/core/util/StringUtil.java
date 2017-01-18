/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search Solution. 
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
package com.cubrid.common.core.util;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;

/**
 * String manipulation utilities
 * 
 * @author Isaiah Choe
 * @version 1.0 - 2009-4-19 created by Isaiah Choe
 */
public final class StringUtil {
	private static final Logger LOGGER = LogUtil.getLogger(StringUtil.class);
	public static final String NEWLINE = System.getProperty("line.separator");

	private StringUtil() {
	}

	/**
	 * join a 'pieces' string array to a string
	 * 
	 * @param glue String the source string
	 * @param pieces String[] need to be appended string
	 * @return String
	 */
	public static String implode(String glue, String[] pieces) {
		return implode(glue, pieces, 0);
	}

	/**
	 * join a 'pieces' string array to a string
	 * 
	 * @param glue String the source string
	 * @param pieces String[] need to be appended string
	 * @param beginIndex int the start index to implode (begin at 0)
	 * @return String
	 */
	public static String implode(String glue, String[] pieces, int beginIndex) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < pieces.length; i++) {
			if (beginIndex != -1 && i < beginIndex) {
				continue;
			}
			if (sb.length() > 0) {
				sb.append(glue);
			}
			sb.append(pieces[i]);
		}
		return sb.toString();
	}

	/**
	 * returning a MD5 hash string
	 * 
	 * @param text String
	 * @return String
	 */
	public static String md5(String text) {
		try {
			if (text == null) {
				return "";
			}

			byte[] digest = MessageDigest.getInstance("MD5").digest(text.getBytes());
			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < digest.length; i++) {
				sb.append(Integer.toString((digest[i] & 0xf0) >> 4, 16));
				sb.append(Integer.toString(digest[i] & 0x0f, 16));
			}

			return sb.toString();
		} catch (NoSuchAlgorithmException nsae) {
			return null;
		}
	}

	/**
	 * String replacement for all range on a string.
	 * 
	 * @param string String the source string
	 * @param oldString String the need to be replaced string
	 * @param newString String a new string will replace the old string
	 * @return String
	 */
	public static String replace(String string, String oldString, String newString) {
		if (string == null) {
			return null;
		}

		if (oldString == null || oldString.length() == 0 || newString == null) {
			return string;
		}

		int i = string.lastIndexOf(oldString);
		if (i < 0) {
			return string;
		}

		StringBuffer sb = new StringBuffer(string);
		while (i >= 0) {
			sb.replace(i, (i + oldString.length()), newString);
			i = string.lastIndexOf(oldString, i - 1);
		}

		return sb.toString();
	}

	/**
	 * String replacement for all range on a string. (StringBuilder version)
	 * 
	 * @param string StringBuilder the source StringBuilder
	 * @param oldString String the need to be replaced string
	 * @param newString String a new string will replace the old string
	 */
	public static void replace(StringBuilder string, String oldString, String newString) {
		if (string == null || string.length() == 0) {
			return;
		}

		if (oldString == null || oldString.length() == 0 || newString == null || newString.length() == 0) {
			return;
		}

		int i = string.lastIndexOf(oldString);
		if (i < 0) {
			return;
		}

		while (i >= 0) {
			string.replace(i, (i + oldString.length()), newString);
			i = string.lastIndexOf(oldString, i - 1);
		}
	}

	/**
	 * Repeat a string
	 * 
	 * @param str String the source string
	 * @param length int the times that need to be appended
	 * @return String
	 */
	public static String repeat(String str, int length) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sb.append(str);
		}
		return sb.toString();
	}

	/**
	 * This method is to convert a long value with a string.
	 * 
	 * @param string String a source string
	 * @return long
	 */
	public static long longValue(String string) {
		if (string == null) {
			return 0;
		}

		try {
			return Long.parseLong(string);
		} catch (Exception ex) {
			return 0;
		}
	}
	
	/**
	 * This method is to convert a long value with a string.If convert
	 * failed,return the defaultValue
	 * 
	 * @param string String The string to be convert
	 * @return long
	 */
	public static long longValue(String string, long defaultValue) {
		if (string == null) {
			return defaultValue;
		}

		try {
			return Long.valueOf(string);
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	/**
	 * This method is to convert a float value with a string.
	 * 
	 * @param string String a source string
	 * @return float
	 */
	public static float floatValue(String string) {
		if (string == null) {
			return 0;
		}
		try {
			return Float.parseFloat(string);
		} catch (Exception ex) {
			return 0;
		}
	}

	
	/**
	 * This method is to convert a double value with a string.If convert
	 * failed,return the defaultValue
	 * 
	 * @param string String The string to be convert
	 * @return int
	 */
	public static double doubleValue(String string, double defaultValue) {
		if (string == null) {
			return defaultValue;
		}

		try {
			return Double.valueOf(string);
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	/**
	 * This method is to convert a boolean value with a string.
	 * 
	 * @param string The string to be convert
	 * @return boolean
	 */
	public static boolean booleanValue(String string) {
		if (string == null) {
			return false;
		}

		try {
			return Integer.parseInt(string) > 0;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * This method is to convert a boolean value with a y or n string.
	 * 
	 * @param string String The string to be judged
	 * @return boolean
	 */
	public static boolean booleanValueWithYN(String string) {
		if (string == null) {
			return false;
		}
		return string.equalsIgnoreCase("YES") || string.equalsIgnoreCase("y");
	}

	/**
	 * This method is to convert a int value with a string.
	 * 
	 * @param string String The string to be convert
	 * @return int
	 */
	public static int intValue(String string) {
		if (string == null) {
			return 0;
		}

		try {
			return Integer.parseInt(string);
		} catch (Exception ex) {
			return 0;
		}
	}

	/**
	 * This method is to convert a int value with a string.If convert
	 * failed,return the defaultValue
	 * 
	 * @param string String The string to be convert
	 * @return int
	 */
	public static int intValue(String string, int defaultValue) {
		if (string == null) {
			return defaultValue;
		}

		try {
			return Integer.valueOf(string);
		} catch (Exception ex) {
			return defaultValue;
		}
	}
	
	/**
	 * Returning a number of character counts for a string.
	 * 
	 * @param string String The source string
	 * @return int
	 */
	public static int countSpace(String string) {

		int count = 0;

		for (int i = 0, len = string.length(); i < len; i++) {
			char c = string.charAt(i);
			if (c != ' ') {
				break;
			}

			count++;
		}

		return count;

	}

	/**
	 * Returning a exception stacktrace like string.
	 * 
	 * @param ex Exception The given Exception
	 * @param delimiter String To be used delimiter in return string
	 * @return String
	 */
	public static String getStackTrace(Exception ex, String delimiter) {
		StringBuilder sb = new StringBuilder();
		sb.append(ex);
		StackTraceElement[] ste = ex.getStackTrace();
		for (int i = 0, len = ste.length; i < len; i++) {
			sb.append(delimiter).append("\tat " + ste[i]);
		}

		return sb.toString();
	}

	/**
	 * return true value whether string is empty.
	 * 
	 * @param string String The source string
	 * @return boolean
	 */
	public static boolean isEmpty(String string) {

		return string == null || string.trim().length() == 0;

	}

	/**
	 * return false value whether string is empty.
	 * 
	 * @param string String The source string
	 * @return boolean
	 */
	public static boolean isNotEmpty(String string) {

		return string != null && string.trim().length() > 0;

	}

	/**
	 * return a true if a string A and B is equal.
	 * 
	 * @param sourceA String one of string to be compared
	 * @param sourceB String another string to be compared
	 * @return boolean
	 */
	public static boolean isEqual(String sourceA, String sourceB) {

		if (sourceA == null || sourceB == null) {
			return false;
		}

		return sourceA.equals(sourceB);

	}

	/**
	 * return a true if a string A and B is equal.
	 * 
	 * @param sourceA String one of string to be compared
	 * @param sourceB String another string to be compared
	 * @return boolean
	 */
	public static boolean isEqual(String sourceA, String sourceB, boolean useTrim) {
		if (!useTrim) {
			return isEqual(sourceA, sourceB);
		}

		if (sourceA == null || sourceB == null) {
			return false;
		}

		return sourceA.trim().equals(sourceB.trim());
	}

	/**
	 * return a true if a string A and B is equal(Not ignore null).
	 * 
	 * @param sourceA String one of string to be compared
	 * @param sourceB String another string to be compared
	 * @return boolean
	 */
	public static boolean isEqualNotIgnoreNull(String sourceA, String sourceB) {

		if (sourceA == null && sourceB == null) {
			return true;
		}

		if (sourceA != null) {
			return sourceA.equals(sourceB);
		}

		return sourceB.equals(sourceA);
	}
	
	/**
	 * return a true if a string A and B is equal(ignore case but not ignore null ).
	 * 
	 * @param sourceA String one of string to be compared
	 * @param sourceB String another string to be compared
	 * @return boolean
	 */
	public static boolean isEqualNotIgnoreNullIgnoreCase(String sourceA, String sourceB) {

		if (sourceA == null && sourceB == null) {
			return true;
		}

		if (sourceA != null) {
			return sourceA.equalsIgnoreCase(sourceB);
		}

		return sourceB.equalsIgnoreCase(sourceA);
	}

	/**
	 * return a true if a string A and B is equal. (with a except space
	 * characters)
	 * 
	 * @param sourceA String one of string to be compared
	 * @param sourceB String another string to be compared
	 * @return boolean
	 */
	public static boolean isTrimEqual(String sourceA, String sourceB) {

		if (sourceA == null || sourceB == null) {
			return false;
		}

		return sourceA.trim().equals(sourceB.trim());

	}

	/**
	 * return a true if a string A and B is equal without case sensitive.
	 * 
	 * @param sourceA String one of string to be compared
	 * @param sourceB String another string to be compared
	 * @return boolean
	 */
	public static boolean isEqualIgnoreCase(String sourceA, String sourceB) {

		if (sourceA == null || sourceB == null) {
			return false;
		}

		return sourceA.equalsIgnoreCase(sourceB);

	}

	/**
	 * If the parameter is null, it will return replaceWhenNull string
	 * 
	 * @param string String The source string
	 * @param replacementWhenNull String A string used for replacing null
	 * @return String
	 */
	public static String nvl(String string, String replacementWhenNull) {

		if (string == null) {
			return replacementWhenNull;
		}

		return string;

	}

	/**
	 * If the parameter is null, it will return no-string value(eg. "")
	 * 
	 * @param string String The source string
	 * @return String
	 */
	public static String nvl(String string) {

		return nvl(string, "");

	}

	/**
	 * This method is to convert a y or n string with a boolean value.
	 * 
	 * @param isYes boolean The source value
	 * @return String
	 */
	public static String yn(boolean isYes) {

		return isYes ? "y" : "n";

	}

	/**
	 * This method is to convert a YES or NO string with a boolean value.
	 * 
	 * @param isYes boolean The source value
	 * @return String
	 */
	public static String yesno(boolean isYes) {

		return isYes ? "YES" : "NO";

	}

	/**
	 * URLDecoder
	 * 
	 * @param url String The source url
	 * @param charset String The charset
	 * @return String
	 */
	public static String urldecode(String url, String charset) {

		String result = null;
		if (url == null) {
			return null;
		}

		try {
			result = URLDecoder.decode(url, charset);
		} catch (UnsupportedEncodingException uee) {
			LOGGER.error(uee.getMessage());
			return null;
		}

		return result;

	}

	/**
	 * URLEncoder
	 * 
	 * @param url String The source url
	 * @param charset String The charset
	 * @return String
	 */
	public static String urlencode(String url, String charset) {

		String result = null;
		if (url == null) {
			return null;
		}

		try {
			result = URLEncoder.encode(url, charset);
		} catch (UnsupportedEncodingException uee) {
			LOGGER.error(uee.getMessage());
			return null;
		}

		return result;
	}

	/**
	 * Get the byte value. e.g. "1K" return 1024,"1M" return 1024 * 1024
	 * 
	 * @param value
	 * @return byte value - e.g. "1K" return 1024,"1M" return 1024 * 1024
	 */
	public static long getByteNumber(String value) {
		long byteNum = -1;

		char unit = value.charAt(value.length() - 1);
		if (Character.isDigit(unit)) {
			try {
				byteNum = Long.parseLong(value);
			} catch (Exception ex) {
				byteNum = -1;
			}
		} else if ('k' == unit || 'K' == unit) {
			try {
				byteNum = Long.parseLong(value.substring(0, value.length() - 1)) * 1024;
			} catch (Exception ex) {
				byteNum = -1;
			}
		} else if ('m' == unit || 'M' == unit) {
			try {
				byteNum = Long.parseLong(value.substring(0, value.length() - 1)) * 1024 * 1024;
			} catch (Exception ex) {
				byteNum = -1;
			}
		} else if ('g' == unit || 'G' == unit) {
			try {
				byteNum = Long.parseLong(value.substring(0, value.length() - 1)) * 1024 * 1024 * 1024;
			} catch (Exception ex) {
				byteNum = -1;
			}
		} else if ('t' == unit || 'T' == unit) {
			try {
				byteNum = Long.parseLong(value.substring(0, value.length() - 1)) * 1024 * 1024 * 1024 * 1024;
			} catch (Exception ex) {
				byteNum = -1;
			}
		}
		return byteNum;
	}
	
	/**
	 * 
	 * Get the size String
	 * 
	 * @param size long
	 * @return String
	 */
	public static String getSizeString(long size) {
		String sizeStr = "";
		double cSize = 0;
		if (size > 1024 * 1024 * 1024) {
			cSize = size / (1024 * 1024 * 1024);
			sizeStr = cSize + "GB";
		} else if (size > 1024 * 1024) {
			cSize = size / (1024 * 1024);
			sizeStr = cSize + "MB";
		} else if (size > 1024) {
			cSize = size / 1024;
			sizeStr = cSize + "KB";
		} else {
			sizeStr = size + "B";
		}
		return sizeStr;
	}

	/**
	 * Convert the byte value to KB value
	 * 
	 * @param bytes
	 * @return
	 */
	public static double convertToK(long bytes) {
		if (bytes < 0) {
			return -1;
		}

		return bytes * 1.0 / 1024;
	}

	/**
	 * Convert the byte value to MB value
	 * 
	 * @param bytes
	 * @return
	 */
	public static double convertToM(long bytes) {
		if (bytes < 0) {
			return -1;
		}

		return bytes * 1.0 / 1024 / 1024;
	}

	/**
	 * Convert the byte value to GB value
	 * 
	 * @param bytes
	 * @return
	 */
	public static double convertToG(long bytes) {
		if (bytes < 0) {
			return -1;
		}

		return bytes * 1.0 / 1024 / 1024 / 1024;
	}

	public static boolean isSomeEmpty(String... strings) {
		for (String string : strings) {
			if (isEmpty(string)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isNotEmptyAll(String... strings) {
		for (String string : strings) {
			if (isEmpty(string)) {
				return false;
			}
		}

		return true;
	}
	
	/**
	 * 
	 * Compare this two list whether equal
	 * 
	 * @param strList1 List<String>
	 * @param strList2 List<String>
	 * @return boolean
	 */
	@Deprecated
	public static boolean compare(List<String> strList1, List<String> strList2) {
		List<String> list1 = strList1 == null ? new ArrayList<String>()
				: strList1;
		List<String> list2 = strList2 == null ? new ArrayList<String>()
				: strList2;

		if (list1.size() != list2.size()) {
			return false;
		}
		for (String str1 : list1) {
			boolean isFound = false;
			int index = 0;
			for (String str2 : list2) {
				if (str1.equals(str2)) {
					isFound = true;
					break;
				}
				index++;
			}
			if (isFound) {
				list2.remove(index);
			} else {
				return false;
			}
		}

		if (list2.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * Convert string to int
	 * 
	 * @param str String need to be convert
	 * @return int
	 */
	public static int str2Int(String str) {
		String reg = "^[-\\+]?\\d+$";
		if (str.matches(reg)) {
			return Integer.parseInt(str);
		}
		return 0;
	}

	/**
	 * Convert string to double
	 * 
	 * @param inval String need to be convert
	 * @return double
	 */
	public static double str2Double(String inval) {
		// TODO: if inval == null, will be return 0 or exception?
		String sciReg = "^[-||+]?\\d+(\\.\\d+)?([e||E]\\d+)?$";
		String plainReg = "^[-\\+]?\\d+(\\.\\d+)?$";
		if (inval.matches(sciReg) || inval.matches(plainReg)) {
			return Double.parseDouble(inval);
		}
		return 0.0;
	}

	/**
	 * Convert string Y or N value to boolean
	 * 
	 * @param inval String need to be convert
	 * @return boolean
	 */
	public static boolean strYN2Boolean(String inval) {
		if (inval == null) {
			return false;
		}
		return inval.equalsIgnoreCase("y") ? true : false;
	}

	/**
	 * Get ip
	 * 
	 * @param hostName String
	 * @return String
	 */
	public static String getIp(String hostName) {
		List<String> ipList = getIpList(hostName);
		for (String ip : ipList) {
			if ("127.0.0.1".equals(ip)) {
				continue;
			}
			return ip;
		}
		return hostName;
	}

	/**
	 * Get whether this hostname is equal with ip
	 * 
	 * @param hostName String
	 * @param ip String
	 * @return boolean
	 */
	public static boolean isIpEqual(String hostName, String ip) {
		List<String> ipList = getIpList(hostName);
		for (String ipAddress : ipList) {
			if (ip.equals(ipAddress)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the all ip address
	 * 
	 * @param hostName String
	 * @return List<String>
	 */
	public static List<String> getIpList(String hostName) {
		List<String> ipList = new ArrayList<String>();
		if (hostName == null || hostName.trim().length() == 0) {
			return ipList;
		}
	
		try {
			if ("localhost".equalsIgnoreCase(hostName) || "127.0.0.1".equals(hostName)) {
				ipList.add(InetAddress.getLocalHost().getHostAddress());
			}
	
			InetAddress[] addrs = InetAddress.getAllByName(hostName);
			for (int i = 0; i < addrs.length; i++) {
				ipList.add(addrs[i].getHostAddress());
			}
		} catch (UnknownHostException e) {
			LOGGER.debug("", e);
		}
		return ipList;
	}

	/**
	 * format the number
	 * 
	 * 
	 * @param num the float number value
	 * @param format "##,###.##","##.##"
	 * @return <code>true</code> if it is ascii;<code>false</code> otherwise
	 */
	public static String formatNumber(float num, String format) {
		DecimalFormat df = new DecimalFormat(format);
		return df.format(num);
	}

	/**
	 * return true if it has a space character (spc, tab, cr, lf).
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isSpaceChar(char c) {
		return (c == ' ' || c == '\t' || c == '\r' || c == '\n');
	}

	/**
	 * Append a prefix on the columns list as the query's column clause.
	 * 
	 * @param columnsOnQuery
	 * @param prefix
	 * @return
	 */
	public static String appendPrefixOnColumns(String columnsOnQuery, String prefix) {
		StringBuilder newColumns = new StringBuilder();
		boolean colMode = false;
		boolean commentArea = false;
		boolean commentLine = false;
		boolean commentLine2 = false;
		boolean hasTableAlias = false;
		int sp = 0;
		int ep = 0;
		char[] array = columnsOnQuery.toCharArray(); 
		for (int i = 0, len = array.length; i < len; i++) {
			char c = array[i];
			if (!colMode) {
				if (!commentLine2 && !commentLine && !commentArea && c == '/' && len > i + 1 && array[i + 1] == '*') {
					commentArea = true;
					newColumns.append(c);
					newColumns.append(array[i + 1]);
					i++;
					continue;
				}
				
				if (commentArea && c == '*' && len > i + 1 && array[i + 1] == '/') {
					commentArea = false;
					newColumns.append(c);
					newColumns.append(array[i + 1]);
					i++;
					continue;
				}
				
				if (!commentArea && !commentLine2 && !commentLine && c == '-' && len > i + 1 && array[i + 1] == '-') {
					commentLine = true;
					newColumns.append(c);
					newColumns.append(array[i + 1]);
					i++;
					continue;
				}
				
				if (!commentArea && !commentLine2 && !commentLine && c == '/' && len > i + 1 && array[i + 1] == '/') {
					commentLine2 = true;
					newColumns.append(c);
					newColumns.append(array[i + 1]);
					i++;
					continue;
				}
				
				if ((commentLine || commentLine2) && (c == '\n' || c == '\r')) {
					commentLine = false;
					commentLine2 = false;
					newColumns.append(c);
					if (c == '\r' && len > i + 1 && array[i + 1] == '\n') {
						newColumns.append(array[i + 1]);
						i++;
					}
					continue;
				}
	
				if (commentArea || commentLine || commentLine2) {
					newColumns.append(c);
					continue;
				}
			}
			
			if (isSpaceChar(c)) {
				if (!colMode) {
					newColumns.append(c);
					continue;
				}
			} else {
				if (!colMode) {
					colMode = true;
					sp = i;
				}
			}
			
			if (c == '.') {
				ep = i + 1;
				hasTableAlias = true;
			}
			
			if (c == ',' || len == i + 1) {
				if (!hasTableAlias) {
					ep = sp;
				}

				if (notStartWithColumnName(columnsOnQuery, ep, i + 1)) {
					newColumns.append(columnsOnQuery.substring(ep, i + 1));
				} else {
					newColumns.append(prefix).append(columnsOnQuery.substring(ep, i + 1));
				}
				
				colMode = false;
				hasTableAlias = false;
				sp = ep = -1;
			}
		}
		
		return newColumns.toString();
	}

	/**
	 * return true if the string has not start with column name.
	 *
	 * @param columnsRawText
	 * @param sp
	 * @param ep
	 * @return
	 */
	private static boolean notStartWithColumnName(String columnsRawText, int sp, int ep) {
		char e = columnsRawText.charAt(sp);
		if (e >= '0' && e <= '9' || e == '(') {
			return true;
		}
		
		String tempString = columnsRawText.substring(sp, ep);
		int sp0 = tempString.indexOf("/*");
		if (sp0 >= 0) {
			tempString = tempString.substring(0, sp0);
		}
		int sp1 = tempString.indexOf('(');
		int sp2 = tempString.indexOf(')');
		if (sp1 != -1 && sp2 != -1) {
			return true;
		}
		
		return false;
	}
	
	public static List<String[]> extractQueries(String queries) {
		List<String[]> extracted = new ArrayList<String[]>();
		char[] buffer = queries.toCharArray();
		boolean sglQuote = false;
		boolean dblQuote = false;
		boolean isLineComment = false;
		boolean isBlockComment = false;
		char prevChar = '\0';
		Vector<String> qVector = new Vector<String>();
		int start = 0;
		int end = 0;

		for (int i = 0; i < buffer.length; i++) {
			if (buffer[i] == '\'' && !dblQuote && !isLineComment
					&& !isBlockComment) {
				sglQuote = !sglQuote;
			}
			if (buffer[i] == '"' && !sglQuote && !isLineComment
					&& !isBlockComment) {
				dblQuote = !dblQuote;
			}

			if (!dblQuote && !sglQuote) {
				if (!isLineComment && prevChar == '-' && buffer[i] == '-'
						&& !isBlockComment) {
					isLineComment = true;
				} else if (!isLineComment && prevChar == '/'
						&& buffer[i] == '/' && !isBlockComment) {
					isLineComment = true;
				}

				if (isLineComment && buffer[i] == '\n') {
					isLineComment = false;
				}

				if (!isBlockComment && prevChar == '/' && buffer[i] == '*'
						&& !isLineComment) {
					isBlockComment = true;
				}

				if (isBlockComment && prevChar == '*' && buffer[i] == '/') {
					isBlockComment = false;
				}
			}

			prevChar = buffer[i];

			if (!isLineComment && !isBlockComment && !dblQuote && !sglQuote
					&& buffer[i] == ';') {
				start = end;
				end = i + 1;
				String aQuery = queries.substring(start, end).trim();

				if (isNotEmptyQuery(aQuery)) {
					String query = aQuery.endsWith(";") ? aQuery : aQuery + ";";
					extracted.add(new String[]{query,
							Integer.toString(end) });
				}
			}
		}
		if (end < queries.length() - 1) {
			String aQuery = queries.substring(end, queries.length()).trim();

			if (isNotEmptyQuery(aQuery)) {
				qVector.addElement(aQuery);
			}
		}
		return extracted;
	}

	/**
	 * Check whether the empty or not
	 * 
	 * @param query String
	 * @return boolean
	 */
	public static boolean isNotEmptyQuery(String query) {
		String[] queryOneLine = query.split("\n");
		StringBuffer tempQuery = new StringBuffer("");
		boolean skipLine = false;
		boolean inComment = false;

		for (int j = 0; j < queryOneLine.length; j++) {
			queryOneLine[j] = queryOneLine[j].trim();
			int position = queryOneLine[j].length();
			if (queryOneLine[j].indexOf("--") > -1) {
				position = Math.min(position, queryOneLine[j].indexOf("--"));
			}
			if (queryOneLine[j].indexOf("/*") > -1) {
				if (queryOneLine[j].indexOf("*/") > -1) {
					String pre = queryOneLine[j].substring(0,
							queryOneLine[j].indexOf("/*"));
					String pst = queryOneLine[j].substring(queryOneLine[j].indexOf("*/") + 2);
					queryOneLine[j] = pre + pst;
					position = queryOneLine[j].length();
					inComment = false;
				} else {
					position = Math.min(position, queryOneLine[j].indexOf("/*"));
					inComment = true;
				}
			}
			if (queryOneLine[j].indexOf("//") > -1) {
				position = Math.min(position, queryOneLine[j].indexOf("//"));
			}
			queryOneLine[j] = queryOneLine[j].substring(0, position);
			if (queryOneLine[j].indexOf("*/") > -1) {
				queryOneLine[j] = queryOneLine[j].substring(queryOneLine[j].indexOf("*/") + 2);
				inComment = false;
				skipLine = false;
			}
			if (!skipLine) {
				tempQuery.append(queryOneLine[j]);
			}
			if (inComment) {
				skipLine = true;
			}
		}
		if (tempQuery.toString().trim().length() > 0
				&& tempQuery.toString().compareTo(";") != 0) {
			return true;
		}

		return false;
	}

	public static boolean isValidNameLength(String string, int max) {
		return isValidNameLength(string, 0, max);
	}

	public static boolean isValidNameLength(String string, int min, int max) {
		if (string == null) {
			return false;
		}

		int len = string.length();
		if (len > max || len < min) {
			return false;
		}

		return true;
	}
	
	public static String getEnumeration(String value) {
		String enumeration = null;
		int index = value.toUpperCase().indexOf("ENUM");
		if(index >= 0) {
			enumeration = value.substring(index + 4);
		}
		return enumeration;
	}

	/**
	 * check whether a string starts with anotherString (ignore case)
	 *
	 * @param startString
	 * @param anotherString
	 * @return
	 */
	public static boolean startsWithIgnoreCase(String startString, String anotherString) {
		int length = startString.length();
		if (length > anotherString.length()) {
			return false;
		}
		if (startString.equalsIgnoreCase(anotherString.substring(0, length))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * cut the annotation before a SQL off
	 *
	 * @param sql
	 * @return
	 */
	public static String cutAnnotationBeforeSQL(String sql) {
		if (StringUtil.isEmpty(sql)) {
			return "";
		}

		sql = sql.trim();
		while(sql.startsWith("--") || sql.startsWith("//") || sql.startsWith("/*")) {
			if (sql.startsWith("--") || sql.startsWith("//")) {
				if (sql.indexOf(NEWLINE) >= 0) {
					sql = sql.substring(sql.indexOf(NEWLINE)).trim();
				} else {
					sql = "";
				}
			} else {
				if (sql.indexOf("*/") >= 0) {
					sql = sql.substring(sql.indexOf("*/") + 2).trim();
				} else {
					sql = "";
				}
			}
		}

		return sql;
	}

	public static String toUpper(String string) {
		if (string == null) {
			return null;
		}

		return string.toUpperCase();
	}

	public static String trim(String string) {
		if (string == null) {
			return null;
		}

		return string.trim();
	}

	/**
	 * Return whether it is a number or not
	 *
	 * @param numberString
	 * @return
	 */
	public static boolean isNumber(String numberString) {
		if (numberString == null) {
			return false;
		}
		return numberString.matches("[0-9]+");
	}
	
	/**
	 * 
	 * Convert the byte to string
	 * 
	 * @param bytes byte[]
	 * @param charset String
	 * @return String
	 */
	public static String converByteToString(byte[] bytes, String charset) {
		String shownValue = null;
		if (charset == null || charset.trim().length() == 0) {
			shownValue = new String(bytes);
		} else {
			try {
				shownValue = new String(bytes, charset);
			} catch (UnsupportedEncodingException e) {
				shownValue = new String(bytes);
			}
		}
		return shownValue;
	}
	
	/**
	 * Convert String to byte[]
	 * 
	 * @param strValue
	 * @return
	 */
	public static byte[] stringToByte(String strValue) {
		if (strValue == null) {
			return null;
		}
		return strValue.getBytes();
	}

	/**
	 * Return the system default character encoding set.
	 *
	 * @return
	 */
	public static String getDefaultCharset() {
		return System.getProperty("file.encoding");
	}

	/**
	 * Return the padded left of a string by padding character.
	 *
	 * @param string
	 * @param pad
	 * @param size
	 * @return
	 */
	public static String leftPad(String string, char pad, int size) {
		if (string == null || size < 0) {
			return null;
		} else if (string.length() == 0 || size == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			sb.append(pad);
		}
		sb.append(string);
		int lengthOfInterimString = sb.length();
		return sb.toString().substring(lengthOfInterimString - size, lengthOfInterimString);
	}

	public static String getOrdinalFromCardinalNumber(int number) {
		String order;
		LocaleUtil.LocaleEnum locale = LocaleUtil.JVM_LOCALE;

		switch (locale) {
		case EN_US:
		case EN_UK:
		case KM_KH:
		case ZH_CN:
			switch (number) {
			case 1:
				order = "1'st";
				break;
			case 2:
				order = "2'nd";
				break;
			case 3:
				order = "3'rd";
				break;
			default:
				order = number + "'th";
				break;
			}
			break;
		case JP_JP:
			order = number + "'\u756a\u76ee";
			break;
		case TR_TR:
			order = number + "'ci";
			break;
		case KO_KR:
			order = number + "\uBC88\uC9F8";
			break;
		default:
			order = "" + number;
		}
		
		return order;
	}

	public static StringBuilder data = new StringBuilder();

	/**
	 * Escaping single quotation or double quotation.
	 *
	 * @param value
	 * @return
	 */
	public static String escapeQuotes(String value) {
		if (value == null) {
			return value;
		}
		data.setLength(0);
		char quote = value.charAt(0);

		eliminateQuotesAndAppendToData(value);
		if (data.length() == 0) {
			return value;
		}

		replaceQuotes(quote);
		wrapQuotesAndAppendToData(quote);

		return data.toString();
	}

	private static void eliminateQuotesAndAppendToData(String value) {
		data.append(value.substring(1, value.length() - 1));
	}

	private static void replaceQuotes(char quote) {
		String quotes = quote + "" + quote;
		String oneQuote = Character.toString(quote);

		int index = data.indexOf(oneQuote);
		if (index == -1) {
			return;
		}

		do {
			data.insert(index, quote);
		} while ((index = data.indexOf(quotes, index)) != -1
				&& (index = data.indexOf(oneQuote, index + 2)) != -1);
	}

	private static void wrapQuotesAndAppendToData(char quote) {
		data.insert(0, quote).insert(data.length(), quote);
	}
}
