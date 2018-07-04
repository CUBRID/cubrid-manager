/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.core.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import com.cubrid.common.core.common.model.QueryTypeCounts;
import com.cubrid.common.core.common.model.SQLContextType;
import com.cubrid.common.core.common.model.SerialInfo;

/**
 * JDBC SQL utility
 *
 * QueryUtil Description
 *
 * @author pcraft
 * @version 1.0 - 2009. 06. 06 created by pcraft
 */
public final class QueryUtil {
	private static final Logger LOGGER = LogUtil.getLogger(QueryUtil.class);

	public static final String REGEX_PATTERN_SHARD_ID = "/\\*\\+[ \\t\\r\\n]*shard_id[ \\t\\r\\n]*\\([0-9 \\t\\r\\n]+\\)[ \\t\\r\\n]*\\*/";
	public static final String REGEX_PATTERN_SHARD_VAL = "/\\*\\+[ \\t\\r\\n]*shard_val[ \\t\\r\\n]*\\([0-9 \\t\\r\\n]+\\)[ \\t\\r\\n]*\\*/";
	public static final String SPLIT_LINE_FOR_QUERY_RESULT = "--------------------------------------------------";

	private QueryUtil() {
	}

	/**
	 * free the object of query
	 *
	 * @param conn Connection
	 * @param stmt Statement
	 * @param rs ResultSet
	 */
	public static void freeQuery(Connection conn, Statement stmt, ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (Exception ignored) {
		}
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (Exception ignored) {
		}
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (Exception ignored) {
		}
	}

	/**
	 * free the object of query
	 *
	 * @param conn Connection
	 * @param stmt PreparedStatement
	 * @param rs ResultSet
	 */
	public static void freeQuery(Connection conn, PreparedStatement pStmt, ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (Exception ignored) {
		}
		try {
			if (pStmt != null) {
				pStmt.close();
			}
		} catch (Exception ignored) {
		}
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (Exception ignored) {
		}
	}

	/**
	 * free the object of query
	 *
	 * @param conn Connection
	 * @param stmt Statement
	 */
	public static void freeQuery(Connection conn, Statement stmt) {
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (Exception ignored) {
		}
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (Exception ignored) {
		}
	}

	/**
	 * free the object of query
	 *
	 * @param stmt Statement
	 * @param rs ResultSet
	 */
	public static void freeQuery(Statement stmt, ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (Exception ignored) {
		}
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (Exception ignored) {
		}
	}

	/**
	 * free the object of query
	 *
	 * @param conn Connection
	 */
	public static void freeQuery(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (Exception ignored) {
		}
	}

	/**
	 * free the object of query
	 *
	 * @param stmt Statement
	 */
	public static void freeQuery(Statement stmt) {
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (Exception ignored) {
		}
	}

	/**
	 * free the object of query
	 *
	 * @param rs ResultSet
	 */
	public static void freeQuery(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (Exception ignored) {
		}
	}

	/**
	 * Commit a transaction without an exception even if it has an error.
	 *
	 * @param conn
	 */
	public static void commit(Connection conn) {
		try {
			if (conn != null) {
				conn.commit();
			}
		} catch (Exception ignored) {
		}
	}

	/**
	 * Rollback a transaction without an exception even if it has an error.
	 *
	 * @param conn
	 */
	public static void rollback(Connection conn) {
		try {
			if (conn != null) {
				conn.rollback();
			}
		} catch (Exception ignored) {
		}
	}

	/**
	 * Put queries into query of vector object
	 *
	 * @param queries String
	 * @return qVector Vector<String>
	 */
	public static Vector<String> queriesToQuery(String queries) {
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
			if (buffer[i] == '\'' && !dblQuote && !isLineComment && !isBlockComment) {
				sglQuote = !sglQuote;
			}
			if (buffer[i] == '"' && !sglQuote && !isLineComment && !isBlockComment) {
				dblQuote = !dblQuote;
			}

			if (!dblQuote && !sglQuote) {
				if (!isLineComment && prevChar == '-' && buffer[i] == '-' && !isBlockComment) {
					isLineComment = true;
				} else if (!isLineComment && prevChar == '/' && buffer[i] == '/' && !isBlockComment) {
					isLineComment = true;
				}

				if (isLineComment && buffer[i] == '\n') {
					isLineComment = false;
				}

				if (!isBlockComment && prevChar == '/' && buffer[i] == '*' && !isLineComment) {
					isBlockComment = true;
				}

				if (isBlockComment && prevChar == '*' && buffer[i] == '/') {
					isBlockComment = false;
				}
			}

			prevChar = buffer[i];

			if (!isLineComment && !isBlockComment && !dblQuote && !sglQuote && buffer[i] == ';') {
				start = end;
				end = i + 1;
				String aQuery = queries.substring(start, end).trim();

				if (isNotEmptyQuery(aQuery)) {
					qVector.addElement(aQuery);
				}
			}
		}
		if (end < queries.length() - 1) {
			String aQuery = queries.substring(end, queries.length()).trim();
			if (isNotEmptyQuery(aQuery)) {
				qVector.addElement(aQuery);
			}
		}

		return qVector;
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
					String pre = queryOneLine[j].substring(0, queryOneLine[j].indexOf("/*"));
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

		if (tempQuery.toString().trim().length() > 0 && tempQuery.toString().compareTo(";") != 0) {
			return true;
		}

		return false;
	}

	public static QueryTypeCounts analyzeQueryTypes(Vector<String> queries) {
		QueryTypeCounts counts = new QueryTypeCounts();
		if (queries == null || queries.size() == 0) {
			return counts;
		}

		for (String query : queries) {
			if (query == null) {
				continue;
			}

			SQLContextType queryType = getQueryType(query);
			if (queryType == SQLContextType.SELECT) {
				counts.increaseSelects();
			} else if (queryType == SQLContextType.INSERT) {
				counts.increaseInserts();
			} else if (queryType == SQLContextType.UPDATE) {
				counts.increaseUpdates();
			} else if (queryType == SQLContextType.DELETE) {
				counts.increaseDeletes();
			} else if (queryType == SQLContextType.CREATE) {
				counts.increaseCreates();
			} else if (queryType == SQLContextType.ALTER) {
				counts.increaseAlters();
			} else if (queryType == SQLContextType.DROP) {
				counts.increaseDrops();
			} else {
				counts.increaseExtras();
			}
		}

		return counts;
	}

	/**
	 * Get the context type
	 *
	 * @param strBf StringBuffer
	 * @return SQLContextType
	 */
	public static SQLContextType getContextType(String context) {
		String str = context.trim().toUpperCase();
		if (str.matches("^CREATE\\s+TABLE\\s+.*") || str.matches("^CREATE\\s+CLASS\\s+.*")) {
			return SQLContextType.CREATE_TABLE;
		} else if (str.lastIndexOf("SELECT") >= 0) {
			return SQLContextType.SELECT;
		} else if (str.lastIndexOf("VALUES") >= 0) {
			return SQLContextType.INSERT_VALUES;
		} else if (str.startsWith("INSERT")) {
			return SQLContextType.INSERT;
		} else if (str.startsWith("UPDATE")) {
			return SQLContextType.UPDATE;
		}

		return SQLContextType.NONE;
	}

	public static SQLContextType getQueryType(String query) {
		String[] rows = query.split("\n");
		StringBuilder sb = new StringBuilder();
		for (String row : rows) {
			row = row.trim();
			if (row.length() == 0 || row.startsWith("--") || row.startsWith("//")) {
				continue;
			}

			sb.append(row.trim()).append(" ");
		}

		int sp = 0, ep = 0;
		while (sp != -1 && ep != -1) {
			sp = sb.indexOf("/*", sp);
			if (sp != -1) {
				ep = sb.indexOf("*/", sp);
				if (ep != -1) {
					ep += 2;
					sb.delete(sp, ep);
				}
			}
		}

		String str = sb.toString().trim().toUpperCase().replaceAll("\\s+", " ");
		if (str.matches("SHOW\\s+VIEW\\s+.*")) {
			return SQLContextType.SELECT;

		} else if (str.matches("CREATE\\s+TABLE\\s*.*") || str.matches("CREATE\\s+CLASS\\s*.*")
				|| str.matches("CREATE\\s+VIEW\\s*.*") || str.matches("CREATE\\s+TRIGGER\\s*.*")
				|| str.matches("CREATE\\s+SERIAL\\s*.*") || str.matches("CREATE\\s+INDEX\\s*.*")) {
			return SQLContextType.CREATE;

		} else if (str.matches("DROP\\s+TABLE\\s*.*") || str.matches("DROP\\s+CLASS\\s*.*")
				|| str.matches("DROP\\s+VIEW\\s*.*") || str.matches("DROP\\s+TRIGGER\\s*.*")
				|| str.matches("DROP\\s+SERIAL\\s*.*") || str.matches("DROP\\s+INDEX\\s*.*")) {
			return SQLContextType.DROP;

		} else if (str.matches("ALTER\\s+TABLE\\s*.*") || str.matches("ALTER\\s+CLASS\\s*.*")
				|| str.matches("ALTER\\s+VIEW\\s*.*") || str.matches("ALTER\\s+TRIGGER\\s*.*")
				|| str.matches("ALTER\\s+SERIAL\\s*.*") || str.matches("ALTER\\s+INDEX\\s*.*")
				|| str.matches("RENAME\\s+TABLE\\s*.*")) {
			return SQLContextType.ALTER;

		} else if (str.matches("SELECT\\s*.*")) {
			return SQLContextType.SELECT;

		} else if (str.matches("INSERT\\s*.*")) {
			return SQLContextType.INSERT;

		} else if (str.matches("UPDATE\\s*.*")) {
			return SQLContextType.UPDATE;

		} else if (str.matches("DELETE\\s*FROM\\s*.*")) {
			return SQLContextType.DELETE;

		}

		return SQLContextType.NONE;
	}

	public static String wrapShardQueryWithId(String sql, int shardId) {
		return "/*+ shard_id(" + shardId + ") */ " + sql;
	}

	public static String wrapShardQueryWithVal(String sql, int shardVal) {
		return "/*+ shard_val(" + shardVal + ") */ " + sql;
	}

	/**
	 * Count records on a table
	 *
	 * @param conn
	 * @param tableName
	 * @return if it is failed, it is going to return -1.
	 */
	public static long countRecords(Connection conn, String tableName) {
		Statement stmt = null;
		ResultSet rs = null;

		String sql = "SELECT COUNT(*) FROM " + QuerySyntax.escapeKeyword(tableName);
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				return rs.getLong(1);
			}
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			QueryUtil.freeQuery(stmt, rs);
		}

		return -1;
	}

	public static String getShowCreateSQL(String schemaName, boolean isTable) {
		if (isTable) {
			return "SHOW CREATE TABLE " + QuerySyntax.escapeKeyword(schemaName);
		} else {
			return "SHOW CREATE VIEW " + QuerySyntax.escapeKeyword(schemaName);
		}
	}

	/**
	 * Return whether the text is consists of the xml format.
	 *
	 * @param text
	 * @return
	 */
	public static boolean isXml(String text) {
		if (StringUtils.isBlank(text)) {
			return false;
		}

		// TODO #664 improve this method using tokenize or regex
		String textToMatch = text.toLowerCase();
		if (textToMatch.indexOf("<select") != -1 || textToMatch.indexOf("<update") != -1
				|| textToMatch.indexOf("<insert") != -1 || textToMatch.indexOf("<delete") != -1) {
			return true;
		}
		return false;
	}

	/**
	 * Extract the queryId from the sqlmap xml text.
	 *
	 * @param text
	 * @param cursorPosition cursor position
	 * @return
	 */
	public static String findNearbyQueryId(String text, int cursorPosition) {
		String foundQueryId = null;

		Pattern pattern = Pattern.compile("<(select|update|insert|delete)\\s+id\\s*=\\s*[\"']{1}([^\"']+)[\"']{1}", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = pattern.matcher(text);
		while (m.find()) {
			int start = m.start();
			if (cursorPosition >= start) {
				foundQueryId = m.group(2);
				break;
			}
		}

		return foundQueryId;
	}

	public static String findNearbyQuery(String text, int cursorPosition) {
		int startPosition = -1;
		int endPosition = -1;
		String queryType = null;

		Pattern pattern = Pattern.compile("<(select|update|insert|delete)\\s+id\\s*=\\s*[\"']{1}([^\"']+)[\"']{1}", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = pattern.matcher(text);
		while (m.find()) {
			int start = m.start();
			if (cursorPosition >= start) {
				startPosition = start;
				queryType = m.group(1);
			}
		}

		if (startPosition == -1) {
			return null;
		}

		pattern = Pattern.compile("</" + queryType + ">", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		m = pattern.matcher(text);
		while (m.find()) {
			int end = m.end();
			if (startPosition < end) {
				endPosition = end;
				break;
			}
		}

		if (endPosition == -1) {
			return null;
		}

		return text.substring(startPosition, endPosition);
	}

	public static List<String> extractBindParameters(String query) {
		List<String> params = new ArrayList<String>();

		// TODO #664 extract bind parameters
		Pattern ptn = Pattern.compile("([#|$]{1}\\{([^\\}]+)\\})", Pattern.MULTILINE);
		Matcher m = ptn.matcher(query);
		while (m.find()) {
			String paramName = m.group(1);
			params.add(paramName);
		}

		// for static query
		ptn = Pattern.compile("(#{1}([^#]+)#{1})", Pattern.MULTILINE);
		m = ptn.matcher(query);
		while (m.find()) {
			String paramName = m.group(1);
			params.add(paramName);
		}

		// for dynamic query
		ptn = Pattern.compile("(\\${1}([^$]+)\\${1})", Pattern.MULTILINE);
		m = ptn.matcher(query);
		while (m.find()) {
			String paramName = m.group(1);
			params.add(paramName);
		}

		return params;
	}

	public static String extractBindParameterName(String bindParameter) {
		Pattern ptn = Pattern.compile("([#|$]{1}\\{([^\\}]+)\\})", Pattern.MULTILINE);
		Matcher m = ptn.matcher(bindParameter);
		if (m.find()) {
			return m.group(2);
		}

		ptn = Pattern.compile("([#|$]{1}([^#$]+)[#|$]{1})", Pattern.MULTILINE);
		m = ptn.matcher(bindParameter);
		if (!m.find()) {
			return null;
		}

		return m.group(2);
	}
	
	public static String getSelectSQL(Connection conn, String name) {
		String sql = null;
		List<String> columnList = getColumnList(conn, name);
		StringBuilder columns = new StringBuilder();
		int size = columnList.size();
		for (int i = 0; i < columnList.size(); i++) {
			columns.append(QuerySyntax.escapeKeyword(columnList.get(i)));
			if (i != size - 1) {
				columns.append(',');
			}
		}
		sql = "SELECT " + columns + " FROM " + QuerySyntax.escapeKeyword(name);
		return sql;
	}

	private static List<String> getColumnList(Connection conn, String tableName) {
		List<String> columnList = new ArrayList<String>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT attr_name FROM db_attribute WHERE class_name = ? ORDER BY def_order";

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, tableName);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				columnList.add(QuerySyntax.escapeKeyword(rs.getString(1)));
			}
		} catch (SQLException e) {
			LOGGER.error(e.getLocalizedMessage());
		} finally {
			QueryUtil.freeQuery(pstmt, rs);
		}
		
		return columnList;
	}
	
	public static List<String> getPrimaryKeys(Connection conn, String tableName) {
		String sql = "SELECT key_attr_name " +
				"FROM db_index_key " +
				"WHERE class_name= ? AND index_name = 'pk'";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<String> pkColumns = new ArrayList<String>();
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, tableName);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				pkColumns.add(rs.getString(1));
			}
		} catch (SQLException e) {
			LOGGER.error(e.getLocalizedMessage());
		} finally {
			QueryUtil.freeQuery(pstmt, rs);
		}
		
		return pkColumns;
	}

	public static boolean isStringDataType(String type) {
		if (type.equals("CHAR") || type.equals("VARCHAR")
				|| type.equals("CHARACTER") || type.equals("CHARACTER VARYING")
				|| type.equals("STRING") || type.equals("NCHAR")
				|| type.equals("NATIONAL CHARACTER")
				|| type.equals("NATIONAL CHARACTER VARYING")
				|| type.equals("NCHAR VARYING")) {
			return true;
		} else {
			return false;
		}
	}

	public static String getColumnDescSql(Connection con, String tableName,
			String columnName) {
		StringBuilder sql = new StringBuilder();
		sql.append("SHOW CREATE TABLE " + tableName);

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = con.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();

			sql.setLength(0);
			sql.append("ALTER TABLE " + tableName + " MODIFY ");

			if (rs.next()) {
				sql.append(parseColumnDefinition(rs.getString(2), columnName));
			}
		} catch (SQLException e) {
			LOGGER.error(e.getLocalizedMessage());
		} finally {
			QueryUtil.freeQuery(pstmt, rs);
		}

		return sql.toString();
	}

	private static String parseColumnDefinition(String createSql,
			String columnName) {
		int index = 0;

		Pattern pattern = Pattern.compile(
				String.format("\\[%s\\].[\\w\\s\\d\\(\\,\\)\\.]*", columnName));
		Matcher matcher = pattern.matcher(createSql);
		matcher.find();
		String data = matcher.group();

		String[] sqlEndWord = {" COMMENT ", ") COLLATE ", ", "};
		int tempIndex = 0;
		index = data.length();

		for (String s : sqlEndWord) {
			if ((tempIndex = data.lastIndexOf(s)) > 0) {
				index = tempIndex;
				break;
			}
		}
		return data.substring(0, index) + " COMMENT %s";
	}

	/**
	 * create serial sql
	 *
	 * @param SerialInfo serial
	 * @param DatabaseInfo databaseInfo
	 */
	public static String createSerialSQLScript(SerialInfo serial, boolean isSupportCache) {
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE SERIAL ");
		sql.append(QuerySyntax.escapeKeyword(serial.getName()));
		String startVal = serial.getStartedValue();
		String currentVal = serial.getCurrentValue();
		String minVal = serial.getMinValue();
		String incrementVal = serial.getIncrementValue();

		startVal = getSerialStartValue(currentVal, startVal, minVal, incrementVal);

		if (startVal != null && startVal.trim().length() > 0) {
			sql.append(" START WITH " + startVal);
		}

		if (incrementVal != null && incrementVal.trim().length() > 0) {
			sql.append(" INCREMENT BY " + incrementVal);
		}

		if (minVal == null || minVal.equals("")) {
			sql.append(" NOMINVALUE ");
		} else if (minVal != null && minVal.trim().length() > 0) {
			sql.append(" MINVALUE " + minVal);
		}
		String maxVal = serial.getMaxValue();
		if (maxVal == null || maxVal.equals("")) {
			sql.append(" NOMAXVALUE ");
		} else if (maxVal != null && maxVal.trim().length() > 0) {
			sql.append(" MAXVALUE " + maxVal);
		}

		if (serial.isCyclic()) {
			sql.append(" CYCLE");
		} else {
			sql.append(" NOCYCLE");
		}
		if (isSupportCache) {
			String cacheCount = serial.getCacheCount();
			if (cacheCount == null || cacheCount.equals("0")) {
				sql.append(" NOCACHE");
			} else if (cacheCount != null && cacheCount.length() > 0) {
				sql.append(" CACHE " + cacheCount);
			}
		}
		sql.append(";");
		return sql.toString();
	}

	/**
	 * get start value, start value should bigger than the min value,if started
	 * value is 1 ,add increment value
	 *
	 * @param currentVal current value of the serial
	 * @param startVal whether start value
	 * @param minVal minimum value
	 * @param incrementVal increment value
	 * @return
	 */
	public static String getSerialStartValue(String currentVal, String startVal, String minVal,
			String incrementVal) { // FIXME move this logic to core module
		if (StringUtil.isEmpty(currentVal) || StringUtil.isEmpty(startVal)) {
			return startVal;
		}

		double current = Double.parseDouble(currentVal);
		double min = Double.parseDouble(minVal);

		if (min > current) {
			return minVal;
		}
		if ("1".equals(startVal) && StringUtil.isNotEmpty(incrementVal)) {
			double increment = Double.parseDouble(incrementVal);
			current += increment;
		}
		return new DecimalFormat("#").format(current);
	}
}
