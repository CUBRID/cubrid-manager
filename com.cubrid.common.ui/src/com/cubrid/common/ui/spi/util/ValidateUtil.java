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
package com.cubrid.common.ui.spi.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cubrid.common.core.util.FileUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.cubridmanager.core.common.model.ConfConstants;

/**
 * 
 * This class include common validation method and check data validation
 * 
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public final class ValidateUtil {

	public final static int MAX_DB_NAME_LENGTH = 17;
	public final static int MAX_NAME_LENGTH = 32; // CM USER
	public final static int MAX_PASSWORD_LENGTH = 31; //db user PASSWORD max length
	public final static int MIN_PASSWORD_LENGTH = 4; //db user PASSWORD min length
	public final static int MAX_SCHEMA_NAME_LENGTH = 254; // (254byte)table,view,column,stored procedure,index, method, method args,fk,partition,partition expr
	public final static int MAX_DB_OBJECT_COMMENT = 1024;

	private ValidateUtil() {
	}

	/**
	 * validate the db name
	 * 
	 * @param dbName the database name
	 * @return <code>true</code> if it is valid;<code>false</code> otherwise
	 */
	public static boolean isValidDBName(String dbName) {
		if (dbName == null || dbName.equals("")) {
			return false;
		}
		/*
		 * it is better that unix file name does not contain space(" ")
		 * character
		 */
		if (dbName.indexOf(" ") >= 0) {
			return false;
		}
		/* Unix file name is not allowed to begin with "#" character */
		if (dbName.charAt(0) == '#') {
			return false;
		}
		/* Unix file name is not allowed to begin with "-" character */
		if (dbName.charAt(0) == '-') {
			return false;
		}
		/*
		 * 9 character(*&%$|^/~\) are not allowed in Unix file name if
		 * (dbName.matches(".*[*&%$\\|^/~\\\\].*")) { return false; } Unix file
		 * name is not allowed to be named as "." or ".."
		 */
		if (".".equals(dbName) || "..".equals(dbName)) {
			return false;
		}
		return dbName.matches("[\\w\\-]*");

	}

	/**
	 * Validate the path name
	 * 
	 * @param pathName the path name
	 * @return <code>true</code> if it is valid;<code>false</code> otherwise
	 */
	public static boolean isValidPathName(String pathName) {
		return isValidPathName(pathName, false);
	}

	/**
	 * Validate the path name
	 * 
	 * @param pathName the path name
	 * @param isAllowBlank boolean
	 * @return <code>true</code> if it is valid;<code>false</code> otherwise
	 */
	public static boolean isValidPathName(String pathName, boolean isAllowBlank) {
		if (pathName == null || pathName.equals("")) {
			return false;
		}

		/*
		 * Currently,due to char set problem,only target path only can be ascii
		 * character
		 */
		if (!isASCII(pathName)) {
			return false;
		}
		/*
		 * it is better that unix file name does not contain space(" ")
		 * character
		 */
		if (!isAllowBlank && pathName.indexOf(" ") >= 0) {
			return false;
		}
		/* Unix file name is not allowed to begin with "#" character */
		if (pathName.charAt(0) == '#') {
			return false;
		}
		/* Unix file name is not allowed to begin with "-" character */
		if (pathName.charAt(0) == '-') {
			return false;
		}
		/* 9 character(*&%$|^~) are not allowed in Unix file name */
		if (pathName.matches(".*[*&%$|^].*")) {
			return false;
		}
		/* Unix file name is not allowed to be named as "." or ".." */
		if (".".equals(pathName) || "..".equals(pathName)) {
			return false;
		}

		return true;
	}

	/**
	 * validate the database name in the system
	 * 
	 * @param pathName the path name
	 * @return <code>true</code> if it is valid;<code>false</code> otherwise
	 */
	public static boolean isValidDbNameLength(String pathName) {
		if (pathName.length() > MAX_DB_NAME_LENGTH) {
			return false;
		}
		return true;
	}

	/**
	 * validate the path name in the system
	 * 
	 * @param pathName the path name
	 * @return <code>true</code> if it is valid;<code>false</code> otherwise
	 */
	public static boolean isValidPathNameLength(String pathName) {
		String name = FileUtil.separatorsToWindows(pathName);
		String[] path = name.split("\\\\");
		for (String tmp : path) {
			if (tmp != null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Return whether the string is double type
	 * 
	 * @param str the str
	 * @return <code>true</code> if it is double;<code>false</code> otherwise
	 */
	public static boolean isDouble(String str) {
		if (StringUtil.isEmpty(str)) {
			return false;
		}
		String reg = "^[-\\+]?\\d+(\\.\\d+)?$";
		return str.matches(reg);
	}

	/**
	 * Return whether the string is positive double type
	 * 
	 * @param str the str
	 * @return <code>true</code> if it is positive double;<code>false</code>
	 *         otherwise
	 */
	public static boolean isPositiveDouble(String str) {
		if (StringUtil.isEmpty(str)) {
			return false;
		}
		String reg = "^\\d+(\\.\\d+)?$";
		return str.matches(reg);
	}

	/**
	 * Return whether the string is integer type
	 * 
	 * @param str the str
	 * @return <code>true</code> if it is integer;<code>false</code> otherwise
	 */
	public static boolean isInteger(String str) {
		if (StringUtil.isEmpty(str)) {
			return false;
		}
		String reg = "^[-\\+]?\\d+$";
		return str.matches(reg);
	}

	/**
	 * Return whether the string is number type
	 * 
	 * @param str the str
	 * @return <code>true</code> if it is number;<code>false</code> otherwise
	 */
	public static boolean isNumber(String str) {
		if (StringUtil.isEmpty(str)) {
			return false;
		}
		String reg = "^\\d+$";
		return str.matches(reg);
	}

	/**
	 * Return whether the string is validate ip address
	 * 
	 * @param str the str
	 * @return <code>true</code> if it is ip;<code>false</code> otherwise
	 */
	public static boolean isIP(String str) {
		if (StringUtil.isEmpty(str)) {
			return false;
		}
		String reg = "^([\\d]{1,3})\\.([\\d]{1,3})\\.([\\d]{1,3})\\.([\\d]{1,3})$";
		if (!str.matches(reg)) {
			return false;
		}
		String[] ipArray = str.split("\\.");
		if (ipArray == null) {
			return false;
		}
		for (int i = 0; i < ipArray.length; i++) {
			if (Integer.parseInt(ipArray[i]) > 255) {
				return false;
			}
			if (ipArray[i].length() != 1 && ipArray[i].indexOf(0) == 0) {
				return false;
			}
		}
		if (Integer.parseInt(ipArray[0]) > 223) {
			return false;
		}
		return true;
	}

	/**
	 * Return whether the string is validate double or scientific notation
	 * 
	 * @param str the str
	 * @return <code>true</code> if it is science double;<code>false</code>
	 *         otherwise
	 */
	public static boolean isSciDouble(String str) {
		if (StringUtil.isEmpty(str)) {
			return false;
		}
		String reg = "^[-||+]?\\d+(\\.\\d+)?([e||E]\\d+)?$";
		return str.matches(reg);
	}

	/**
	 * Check the table name and column name and procedure related name and
	 * serial name.
	 * 
	 * @param identifier the identifier string
	 * @return boolean true if there is a special char in the identifier
	 */
	public static boolean isValidIdentifier(String identifier) {
		if (StringUtil.isEmpty(identifier)) {
			return false;
		}

		String regex = "[\\s\\,\\/\\.\\~\\\\\"\\|\\]\\[\\}\\{\\)\\(\\=\\-\\+\\?\\<\\>:\\;\\!\\'@\\%\\$\\^\\&\\*`]";

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(identifier);

		return !matcher.find();
	}

	/**
	 * Check whether this database support multi bytes.
	 * 
	 * @param database The CubridDatabase object
	 * @return <code>true</code> if support;otherwise <code>false</code>
	 */
	public static boolean isSupportMultiByte(CubridDatabase database) {
		String intlMbsSupport = database.getServer().getServerInfo().getCubridConfPara(
				ConfConstants.INTL_MBS_SUPPORT, database.getLabel());
		return intlMbsSupport != null && intlMbsSupport.equalsIgnoreCase("yes"); //$NON-NLS-1$
	}

	/**
	 * Check whether this database didn't be supported multi bytes.
	 * 
	 * @param database The CubridDatabase object
	 * @return <code>true</code> if support;otherwise <code>false</code>
	 */
	public static boolean notSupportMB(CubridDatabase database) {
		return !isSupportMultiByte(database);
	}
	
	/**
	 * Return true if a string s is a ascii string.
	 * 
	 * @param str the string
	 * @return <code>true</code> if it is ascii;<code>false</code> otherwise
	 */
	public static boolean isASCII(String str) {
		for (int i = 0, len = str.length(); i < len; i++) {
			if (!java.lang.Character.UnicodeBlock.of(str.charAt(i)).equals(
					java.lang.Character.UnicodeBlock.BASIC_LATIN)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Return true if a string s isn't a ascii string.
	 * 
	 * @param str the string
	 * @return <code>true</code> if it is ascii;<code>false</code> otherwise
	 */
	public static boolean notASCII(String str) {
		return !isASCII(str);
	}

	/**
	 * Check string whether valid date (yyyy-MM-dd)
	 * 
	 * @param str The string
	 * @return <code>true</code> if valid;otherwise <code>false</code>
	 */
	public static boolean isDate(String str) {
		if (StringUtil.isEmpty(str)) {
			return false;
		}
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",
				Locale.getDefault());
		try {
			Date date = dateFormat.parse(str);
			if (date == null) {
				return false;
			}
			String formatStr = dateFormat.format(date);
			if (!str.equals(formatStr)) {
				return false;
			}
		} catch (ParseException e) {
			return false;
		}
		return true;
	}

	/**
	 * Check string whether valid time (HH:mm:ss)
	 * 
	 * @param str The string
	 * @return <code>true</code> if valid;otherwise <code>false</code>
	 */
	public static boolean isTime(String str) {
		if (StringUtil.isEmpty(str)) {
			return false;
		}
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss",
				Locale.getDefault());
		try {
			Date date = dateFormat.parse(str);
			if (date == null) {
				return false;
			}
			String formatStr = dateFormat.format(date);
			if (!str.equals(formatStr)) {
				return false;
			}
		} catch (ParseException e) {
			return false;
		}
		return true;
	}

	/**
	 * Check string whether valid time stamp (yyyy-MM-dd HH:mm:ss)
	 * 
	 * @param str The string
	 * @return <code>true</code> if valid;otherwise <code>false</code>
	 */
	public static boolean isTimeStamp(String str) {
		if (StringUtil.isEmpty(str)) {
			return false;
		}
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.getDefault());
		try {
			Date date = dateFormat.parse(str);
			if (date == null) {
				return false;
			}
			String formatStr = dateFormat.format(date);
			if (!str.equals(formatStr)) {
				return false;
			}
		} catch (ParseException e) {
			return false;
		}
		return true;
	}

	/**
	 * If comparingValue is a value between fromValue and toValue, it will be
	 * returned true. The fromValue and the toValue are more than(>=)/less
	 * than(<=) conditions.
	 * 
	 * @param comparingValue to compare string value
	 * @param fromValue begin value to compare
	 * @param toValue end value to compare
	 * @return
	 */
	public static boolean betweenValues(String comparingValue, int fromValue, int toValue) {
		if (!ValidateUtil.isInteger(comparingValue)) {
			return false;
		}

		int comparingIntValue = StringUtil.intValue(comparingValue);
		return comparingIntValue >= fromValue && comparingIntValue <= toValue;
	}
	
	/**
	 *Judge the host name is validate 
	 * 
	 * @param hostName
	 * @return
	 */
	public static boolean isValidHostName(String hostName) {
		if (StringUtil.isEmpty(hostName)
				|| hostName.length() >= ValidateUtil.MAX_NAME_LENGTH) {
			return false;
		}
		return true;
	}
}
