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
package com.cubrid.common.ui.query.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.util.regex.Pattern;

/**
 * Parse sql,provide the below function (1)add rownum(where rownum between
 * $start and $end) for paginating (2)Convert the comment(\\) to the comment(--)
 * and (*\) to (\n\*)
 *
 * @author pangqiren
 *
 */
public final class SqlParser { // FIXME move to core module

	public static final String ROWNUM_CONDITION_MARK = "${__rownum_condition__}";
	//when the below key word appear in sql,do not add rownum clause
	private static final String[] OUTER_CLAUSE_KEYWORD = {"where", "union",
			"difference", "intersect" };
	//left outer join,right outer join key word
	private static final String[] OUTER_JOIN_KEYWORD = {"left", "right" };
	//order by,group by connect by key word
	private static final String[] BY_CLAUSE_KEYWORD = {"order", "group",
			"connect" };
	private static final String[][] MULTI_CLAUSE_KEYWORD = {
			{"with", "increment", "for" }, {"with", "decrement", "for" },
			{"start", "with" } };
	private static String[] functionArr = {"sum", "count", "min", "max", "avg",
			"stddev", "variance", "unique", "distinct" };
	private static String[] keyWordArr = {"unique", "distinct", "limit" };

	private SqlParser() {

	}

	/**
	 *
	 * Get Token List
	 *
	 * @param sql The String
	 * @return List<SqlToken>
	 * @throws Exception the exception
	 */
	private static List<SqlToken> getTokenList(String sql) throws Exception {
		List<SqlToken> tokenList = new ArrayList<SqlToken>();
		char[] charArray = sql.toCharArray();
		int length = charArray.length;
		for (int i = 0; i < length; i++) {
			if (i - 1 > 0 && charArray[i] == '\'' && charArray[i - 1] != '\\') {
				// single quotation('')
				int start = i;
				i = getSingleQuoteEndPos(charArray, i + 1);
				if (i < 0) {
					throw new Exception("The SQL is not valid.");
				}
				tokenList.add(new SqlToken(SqlTokenType.SINGLE_QUOTE, start, i));
			} else if (i - 1 > 0 && charArray[i] == '"'
					&& charArray[i - 1] != '\\') {
				// double quotation(")
				int start = i;
				i = getDoubleQuoteEndPos(charArray, i + 1);
				if (i < 0) {
					throw new Exception("The SQL is not valid.");
				}
				tokenList.add(new SqlToken(SqlTokenType.DOUBLE_QUOTE, start, i));
			}
		}
		return tokenList;
	}

	/**
	 *
	 * Convert the comment(//) to the comment(--)
	 *
	 * @param sql String
	 * @return string converted comment
	 */
	public static String convertComment(String sql) {
		if (sql == null || sql.length() < 0) {
			return null;
		}
		if (sql.indexOf("//") < 0) {
			return sql;
		}
		List<SqlToken> tokenList;
		try {
			tokenList = getTokenList(sql);
		} catch (Exception e) {
			return sql;
		}
		char[] charArray = sql.toCharArray();
		int length = charArray.length;
		StringBuffer filteredSqlBuf = new StringBuffer();
		for (int j = 0; j < length; j++) {
			if (j + 1 < length && charArray[j] == '/'
					&& charArray[j + 1] == '/') {
				boolean isCommented = true;
				for (int k = 0; k < tokenList.size(); k++) {
					SqlToken token = tokenList.get(k);
					if (j > token.start && j < token.end) {
						isCommented = false;
						break;
					}
				}
				if (isCommented) {
					filteredSqlBuf.append("--");
					j = j + 1;
				} else {
					filteredSqlBuf.append(charArray[j]);
				}
			} else {
				filteredSqlBuf.append(charArray[j]);
			}
		}
		return filteredSqlBuf.toString();
	}

	/**
	 *
	 * Convert the \\s char to " "
	 *
	 * @param sql String
	 * @return string converted sql
	 */
	public static String convertSql(String sql) {
		if (sql == null || sql.trim().length() < 0) {
			return null;
		}

		List<SqlToken> tokenList;
		try {
			tokenList = getTokenList(sql);
		} catch (Exception e) {
			return sql;
		}

		char[] charArray = sql.toCharArray();
		int length = charArray.length;

		StringBuffer filteredSqlBuf = new StringBuffer();
		for (int j = 0; j < length; j++) {
			if (String.valueOf(charArray[j]).matches("\\s")) {
				boolean isEmpty = true;
				for (int k = 0; k < tokenList.size(); k++) {
					SqlToken token = tokenList.get(k);
					if (j > token.start && j < token.end) {
						isEmpty = false;
						break;
					}
				}
				if (isEmpty) {
					filteredSqlBuf.append(" ");
				} else {
					filteredSqlBuf.append(charArray[j]);
				}
			} else {
				filteredSqlBuf.append(charArray[j]);
			}
		}
		return filteredSqlBuf.toString();
	}

	/**
	 * Add rownum(where rownum between $start and $end) clause to sql for
	 * paginating, if return null,this sql clause do not support paginating.
	 *
	 * @param sql String
	 * @return String
	 */
	public static String getPaginatingSqlClause(String sql) {
		String sqlStr = sql;
		if (sqlStr == null || sqlStr.length() < 0) {
			return null;
		}
		sqlStr = sqlStr.trim();
		if (sqlStr.lastIndexOf(';') == sqlStr.length() - 1) {
			sqlStr = sqlStr.substring(0, sqlStr.length() - 1);
		}
		// there maybe something wrong in server parser, so replace "//" to "--"
		sqlStr = convertComment(sqlStr);
		String lowerSql = sqlStr.toLowerCase(Locale.getDefault());
		if (lowerSql.length() < 7
				|| !lowerSql.substring(0, 7).matches("select\\s")
				|| !isHasFromClause(lowerSql, null)) {
			return null;
		}
		List<SqlToken> tokenList = new ArrayList<SqlToken>();
		Stack<Integer> bracketStack = new Stack<Integer>();
		char[] charArray = lowerSql.toCharArray();
		int length = charArray.length;
		for (int i = 0; i < length; i++) {
			if (charArray[i] == '(') { // left bracket
				bracketStack.push(i);
			} else if (charArray[i] == ')') { // right bracket
				if (bracketStack.size() <= 0) {
					return null;
				}
				Integer start = (Integer) bracketStack.pop();
				tokenList.add(new SqlToken(SqlTokenType.BRACKET, start, i));
			} else if (charArray[i] == '-') { // comment
				if (i + 1 < length && charArray[i + 1] == '-') {
					// comment(//)
					int start = i;
					i = getCommentEndPos(charArray, i + 2);
					if (i < 0) {
						return null;
					}
					tokenList.add(new SqlToken(
							SqlTokenType.DOUBLE_VERTICAL_LINE_COMMENT, start, i));
				} else if (i + 1 < length && charArray[i + 1] == '*') {
					// block comment(/*...*/)
					int start = i;
					i = getBlockCommentEndPos(charArray, i + 2);
					if (i < 0) {
						return null;
					}
					tokenList.add(new SqlToken(SqlTokenType.BLOCK_COMMENT,
							start, i));
				}
			} else if (i + 1 < length && charArray[i] == '-'
					&& charArray[i + 1] == '-') {
				// comment(--)
				int start = i;
				i = getCommentEndPos(charArray, i + 2);
				if (i < 0) {
					return null;
				}
				tokenList.add(new SqlToken(
						SqlTokenType.DOUBLE_HORIZONTAL_LINE_COMMENT, start, i));
			} else if (i - 1 > 0 && charArray[i] == '\''
					&& charArray[i - 1] != '\\') {
				// single quotation('')
				int start = i;
				i = getSingleQuoteEndPos(charArray, i + 1);
				if (i < 0) {
					return null;
				}
				tokenList.add(new SqlToken(SqlTokenType.SINGLE_QUOTE, start, i));
			} else if (i - 1 > 0 && charArray[i] == '"'
					&& charArray[i - 1] != '\\') {
				// double quotation(")
				int start = i;
				i = getDoubleQuoteEndPos(charArray, i + 1);
				if (i < 0) {
					return null;
				}
				tokenList.add(new SqlToken(SqlTokenType.DOUBLE_QUOTE, start, i));
			}
		}
		if (bracketStack.size() > 0 || isHasOuterClause(lowerSql, tokenList)
				|| isHasOuterByClause(lowerSql, tokenList)
				|| isHasOuterJoinClause(lowerSql, tokenList)
				|| isHasAggregationFunction(lowerSql)
				|| isHasOuterMultiClause(lowerSql, tokenList)
				|| isHasKeyWord(lowerSql, tokenList)
				|| !isHasFromClause(lowerSql, tokenList)) {
			return null;
		}
		int insertPos = lowerSql.length();
		int usingPos = getOuterUsingClausePos(lowerSql, tokenList);
		if (usingPos > 0 && usingPos < insertPos) {
			insertPos = usingPos - 1;
		}
		String prePartSql = sqlStr.substring(0, insertPos);
		String afterPartSql = "";
		if (insertPos < sqlStr.length() - 1) {
			afterPartSql = sqlStr.substring(insertPos + 1);
		}
		// the constant ROWNUM_CONDITION_MARK is mark to be replaced in
		// QueryExcuter.makeTable(int start)
		String parsedSql = prePartSql + ROWNUM_CONDITION_MARK;
		if (afterPartSql.length() == 0) {
			parsedSql += ";";
		} else {
			String[] afterPartSqlArr = afterPartSql.split("\\n");
			String lastLine = afterPartSqlArr[afterPartSqlArr.length - 1];
			if (lastLine.indexOf("//") >= 0 || lastLine.indexOf("--") >= 0) {
				parsedSql += "\r\n" + afterPartSql + "\r\n" + ";";
			} else {
				parsedSql += "\r\n" + afterPartSql + ";";
			}
		}
		return parsedSql;
	}

	/**
	 *
	 * Get some string count in SQL string
	 *
	 * @param sql The String
	 * @param str The String
	 * @return int
	 */
	public static int getStrCount(String sql, String str) {
		if (sql == null || sql.length() < 0 || sql.indexOf(str) == -1) {
			return 0;
		}
		int count = 0;
		List<SqlToken> tokenList;
		try {
			tokenList = getTokenList(sql);
		} catch (Exception e) {
			return count;
		}

		int start = 0;
		while ((start = sql.indexOf(str, start)) != -1) {
			boolean isValid = true;
			for (int k = 0; k < tokenList.size(); k++) {
				SqlToken token = tokenList.get(k);
				if (start > token.start && start < token.end) {
					isValid = false;
					break;
				}
			}
			if (isValid) {
				count++;
			}
			start = start + str.length();
		}
		return count;
	}

	/**
	 * Return whether this sql clause including from clause
	 *
	 * @param sql String
	 * @param tokenList List<SqlToken>
	 * @return boolean
	 */
	private static boolean isHasFromClause(String sql, List<SqlToken> tokenList) {
		String clause = "from";
		int i = 0;
		int pos = 0;
		while (i < sql.length() && (pos = sql.indexOf(clause, i)) > 0) {
			if (tokenList == null) {
				return true;
			}
			i = pos + clause.length();
			String preStr = " ";
			if (pos > 1) {
				preStr = String.valueOf(sql.charAt(pos - 1));
			}
			String afterStr = " ";
			if (i < sql.length()) {
				afterStr = String.valueOf(sql.charAt(i));
			}
			if (preStr.matches("\\s") && afterStr.matches("\\s")) {
				boolean isOuterSql = true;
				for (int j = 0; j < tokenList.size(); j++) {
					SqlToken sqlToken = tokenList.get(j);
					if (sqlToken.start < pos && pos < sqlToken.end) {
						isOuterSql = false;
						break;
					}
				}
				if (isOuterSql) {
					return true;
				}
			}
		}
		return false;

	}

	/**
	 * Return whether this sql clause including these string from
	 * outerClauseKeyWord array
	 *
	 * @param sql String
	 * @param tokenList List<SqlToken>
	 * @return boolean
	 */
	private static boolean isHasOuterClause(String sql, List<SqlToken> tokenList) {
		for (int k = 0; k < OUTER_CLAUSE_KEYWORD.length; k++) {
			String clause = OUTER_CLAUSE_KEYWORD[k];
			int i = 0;
			int pos = 0;
			while (i < sql.length() && (pos = sql.indexOf(clause, i)) > 0) {
				i = pos + clause.length();
				String preStr = " ";
				if (pos > 1) {
					preStr = String.valueOf(sql.charAt(pos - 1));
				}
				String afterStr = " ";
				if (i < sql.length()) {
					afterStr = String.valueOf(sql.charAt(i));
				}
				if (preStr.matches("\\s") && afterStr.matches("\\s")) {
					boolean isOuterSql = true;
					for (int j = 0; j < tokenList.size(); j++) {
						SqlToken sqlToken = tokenList.get(j);
						if (sqlToken.start < pos && pos < sqlToken.end) {
							isOuterSql = false;
							break;
						}
					}
					if (isOuterSql) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Return whether this sql clause including these string from
	 * multiClauseKeyWord array
	 *
	 * @param sql String
	 * @param tokenList List<SqlToken>
	 * @return boolean
	 */
	private static boolean isHasOuterMultiClause(String sql,
			List<SqlToken> tokenList) {
		for (int k = 0; k < MULTI_CLAUSE_KEYWORD.length; k++) {
			String[] clauses = MULTI_CLAUSE_KEYWORD[k];
			int i = 0;
			int pos = 0;
			while (i < sql.length() && (pos = sql.indexOf(clauses[0], i)) > 0) {
				StringBuffer sb = new StringBuffer();
				for (String clause : clauses) {
					sb.append(clause).append("\\s+");
				}
				sb.append(".+");
				if (!sql.substring(pos).matches(sb.toString())) {
					i = pos + clauses[0].length();
					continue;
				}
				i = sql.indexOf(clauses[clauses.length - 1], pos)
						+ clauses[clauses.length - 1].length();
				String preStr = " ";
				if (pos > 1) {
					preStr = String.valueOf(sql.charAt(pos - 1));
				}
				String afterStr = " ";
				if (i < sql.length()) {
					afterStr = String.valueOf(sql.charAt(i));
				}
				if (preStr.matches("\\s") && afterStr.matches("\\s")) {
					boolean isHasMultiClauseSql = true;
					for (int j = 0; j < tokenList.size(); j++) {
						SqlToken sqlToken = tokenList.get(j);
						if (sqlToken.start < pos && pos < sqlToken.end) {
							isHasMultiClauseSql = false;
							break;
						}
					}
					if (isHasMultiClauseSql) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 *
	 * Return whether this sql clause including these string from
	 * byClauseKeyWord array
	 *
	 * @param sql String
	 * @param tokenList List<SqlToken>
	 * @return boolean
	 */
	private static boolean isHasOuterByClause(String sql,
			List<SqlToken> tokenList) {
		for (int k = 0; k < BY_CLAUSE_KEYWORD.length; k++) {
			String clause = BY_CLAUSE_KEYWORD[k];
			int i = 0;
			int pos = 0;
			while (i < sql.length() && (pos = sql.indexOf(clause, i)) >= 0) {
				i = pos + clause.length();
				int byPos = -1;
				if (i < sql.length()) {
					byPos = sql.indexOf("by", i);
				}
				if (byPos > 0 && sql.substring(i, byPos).matches("\\s+")) {
					i = byPos + 2;
					String preStr = " ";
					if (pos > 1) {
						preStr = String.valueOf(sql.charAt(pos - 1));
					}
					String afterStr = " ";
					if (i < sql.length()) {
						afterStr = String.valueOf(sql.charAt(i));
					}
					if (preStr.matches("\\s") && afterStr.matches("\\s")) {
						boolean isHasByClause = true;
						for (int j = 0; j < tokenList.size(); j++) {
							SqlToken sqlToken = tokenList.get(j);
							if (sqlToken.start < pos && pos < sqlToken.end) {
								isHasByClause = false;
								break;
							}
						}
						if (isHasByClause) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Return whether this sql clause including these string from
	 * outerJoinKeyWord array
	 *
	 * @param sql String
	 * @param tokenList List<SqlToken>
	 * @return boolean
	 */
	private static boolean isHasOuterJoinClause(String sql,
			List<SqlToken> tokenList) {
		for (int k = 0; k < OUTER_JOIN_KEYWORD.length; k++) {
			String clause = OUTER_JOIN_KEYWORD[k];
			int i = 0;
			int pos = 0;
			while (i < sql.length() && (pos = sql.indexOf(clause, i)) >= 0) {
				i = pos + clause.length();
				int outerPos = -1;
				if (i < sql.length()) {
					outerPos = sql.indexOf("outer", i);
				}
				if (outerPos > 0 && sql.substring(i, outerPos).matches("\\s+")) {
					i = outerPos + 5;
					int joinPos = -1;
					if (i < sql.length()) {
						joinPos = sql.indexOf("join", i);
					}
					if (joinPos > 0
							&& sql.substring(i, joinPos).matches("\\s+")) {
						i = joinPos + 4;
						String preStr = " ";
						if (pos > 1) {
							preStr = String.valueOf(sql.charAt(pos - 1));
						}
						String afterStr = " ";
						if (i < sql.length()) {
							afterStr = String.valueOf(sql.charAt(i));
						}
						if (preStr.matches("\\s") && afterStr.matches("\\s")) {
							boolean isHasOuterJoinClause = true;
							for (int j = 0; j < tokenList.size(); j++) {
								SqlToken sqlToken = tokenList.get(j);
								if (sqlToken.start < pos && pos < sqlToken.end) {
									isHasOuterJoinClause = false;
									break;
								}
							}
							if (isHasOuterJoinClause) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 *
	 * Return whether this sql clause including these string from functionArr
	 * array
	 *
	 * @param sql String
	 * @param tokenList List<SqlToken>
	 * @return boolean
	 */
	private static boolean isHasAggregationFunction(String sql) {
		for (int k = 0; k < functionArr.length; k++) {
			String functionName = functionArr[k];
			String regex = String.format("\\W%s\\s*\\(", functionName);
			if (Pattern.compile(regex).matcher(sql).find()) {
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * Return whether this sql clause including these string from keyword array
	 *
	 * @param sql String
	 * @param tokenList List<SqlToken>
	 * @return boolean
	 */
	private static boolean isHasKeyWord(String sql, List<SqlToken> tokenList) {
		for (int k = 0; k < keyWordArr.length; k++) {
			String keyWord = keyWordArr[k];
			int i = 0;
			int pos = 0;
			while (i < sql.length() && (pos = sql.indexOf(keyWord, i)) > 0) {
				i = pos + keyWord.length();
				String preStr = " ";
				if (pos > 1) {
					preStr = String.valueOf(sql.charAt(pos - 1));
				}
				String afterStr = " ";
				if (i < sql.length()) {
					afterStr = String.valueOf(sql.charAt(i));
				}
				if (preStr.matches("\\s")
						&& (afterStr.matches("\\s") || afterStr.matches("\\*") || afterStr.matches("\""))) {
					boolean isKeyWordSql = true;
					for (int j = 0; j < tokenList.size(); j++) {
						SqlToken sqlToken = tokenList.get(j);
						if (sqlToken.start < pos && pos < sqlToken.end) {
							isKeyWordSql = false;
							break;
						}
					}
					if (isKeyWordSql) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * get the position of OuterUsingClause
	 *
	 * @param sql String
	 * @param tokenList List<SqlToken>
	 * @return position
	 */
	private static int getOuterUsingClausePos(String sql,
			List<SqlToken> tokenList) {
		int i = 0;
		int pos = 0;
		while (i < sql.length() && (pos = sql.indexOf("using", i)) >= 0) {
			i = pos + 5;
			int indexPos = -1;
			if (i < sql.length()) {
				indexPos = sql.indexOf("index", i);
			}
			if (indexPos > 0 && sql.substring(i, indexPos).matches("\\s+")) {
				i = indexPos + 5;
				String preStr = " ";
				if (pos > 1) {
					preStr = String.valueOf(sql.charAt(pos - 1));
				}
				String afterStr = " ";
				if (i < sql.length()) {
					afterStr = String.valueOf(sql.charAt(i));
				}
				if (preStr.matches("\\s") && afterStr.matches("\\s")) {
					boolean isOuterUsingClause = true;
					for (int j = 0; j < tokenList.size(); j++) {
						SqlToken sqlToken = tokenList.get(j);
						if (sqlToken.start < pos && pos < sqlToken.end) {
							isOuterUsingClause = false;
							break;
						}
					}
					if (isOuterUsingClause) {
						return pos;
					}
				}
			}
		}
		return -1;
	}

	/**
	 * get the position of comment end
	 *
	 * @param charArray char[]
	 * @param start int
	 * @return position
	 */
	private static int getCommentEndPos(char[] charArray, int start) {
		for (int i = start; i < charArray.length; i++) {
			if (charArray[i] == '\n' || i == charArray.length - 1) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * get the position of BlockCommentEnd
	 *
	 * @param charArray char[]
	 * @param start int
	 * @return position
	 */
	private static int getBlockCommentEndPos(char[] charArray, int start) {
		for (int i = start; i < charArray.length - 1; i++) {
			if (charArray[i] == '*' && charArray[i + 1] == '/') {
				return i + 1;
			}
		}
		return -1;
	}

	/**
	 * get the position of SingleQuoteEnd
	 *
	 * @param charArray char[]
	 * @param start int
	 * @return position
	 */
	private static int getSingleQuoteEndPos(char[] charArray, int start) {
		for (int i = start; i < charArray.length; i++) {
			if (charArray[i] == '\'' && charArray[i - 1] != '\\') {
				return i;
			}
		}
		return -1;
	}

	/**
	 * get the position of DoubleQuoteEnd
	 *
	 * @param charArray char[]
	 * @param start int
	 * @return position
	 */
	private static int getDoubleQuoteEndPos(char[] charArray, int start) {
		for (int i = start; i < charArray.length; i++) {
			if (charArray[i] == '"' && charArray[i - 1] != '\\') {
				return i;
			}
		}
		return -1;
	}

	/**
	 * record the token start postion and end positon
	 *
	 * @author pangqiren
	 *
	 */
	public static class SqlToken {
		private final SqlTokenType tokenType;
		private final int start;
		private final int end;

		public SqlToken(SqlTokenType tokenType, int start, int end) {
			this.tokenType = tokenType;
			this.start = start;
			this.end = end;
		}

		public SqlTokenType getTokenType() {
			return this.tokenType;
		}
	}

	/**
	 * token type
	 *
	 * @author pangqiren
	 *
	 */
	public enum SqlTokenType {
		DOUBLE_VERTICAL_LINE_COMMENT, BLOCK_COMMENT, DOUBLE_HORIZONTAL_LINE_COMMENT, SINGLE_QUOTE, DOUBLE_QUOTE, BRACKET
	}
}
