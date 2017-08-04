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

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;

import com.cubrid.common.core.CubridCommonCorePlugin;
import com.cubrid.common.core.Messages;

/**
 * This class contains the CUBRID syntax keywords
 * 
 * @author wangsl
 * @version 1.0 - 2009-06-24 created by wangsl
 */
public final class QuerySyntax {
	public static Properties keywordsDescriptionProperties;
	private static final Logger LOGGER = LogUtil.getLogger(QuerySyntax.class);
	static {
		keywordsDescriptionProperties = new Properties();
		try {
			InputStream in = new BufferedInputStream(
					CubridCommonCorePlugin.class.getResourceAsStream(Messages.keywordFilename));
			keywordsDescriptionProperties.load(in);
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}

	public final static String[] KEYWORDS = new String[]{"ABS", "ABSOLUTE",
			"ACTION", "ACTIVE", "ADD", "ADDTIME", "ADD_MONTHS", "AFTER", "ALIAS", "ALL",
			"ALLOCATE", "ALTER", "ANALYZE", "AND", "ANY", "ARE", "AS", "ASC",
			"ASCII", "ASSERTION", "ASYNC", "AT", "ATTACH", "ATTRIBUTE",
			"AUDIT", "AUTHORIZATION", "AUTOCOMMIT", "AUTO_INCREMENT", "AVG",
			"BEFORE", "BEGIN", "BETWEEN", "BIGINT", "BIT", "BIT_LENGTH",
			"BIT_TO_BLOB", "BLOB", "BLOB_FROM_FILE", "BLOB_LENGTH",
			"BLOB_TO_BIT", "CLOB_FROM_FILE", "CLOB_LENGTH", "CLOB_TO_CHAR",
			"BOOLEAN", "BOTH", "BREADTH", "BY", "CACHE", "CALL", "CASCADE",
			"CASCADED", "CASE", "CAST", "CATALOG", "CEIL", "CHANGE", "CHAR",
			"CHARACTER", "CHARACTER_LENGTH", "CHAR_LENGTH", "CHAR_TO_BLOB",
			"CHAR_TO_CLOB", "CHECK", "CHR", "CLASS", "CLOB", "CLASSES",
			"COLUMNS", "CLOSE", "CLUSTER", "COALESCE", "COLLATE", "COLLATION",
			"COLUMN", "COMMIT", "COMMITTED", "COMPLETION", "CONNECT",
			"CONNECTION", "CONNECT_BY_ISCYCLE", "CONNECT_BY_ISLEAF",
			"CONNECTION_BY_ROOT", "CONSTRAINT", "CONSTRAINTS", "CONTINUE",
			"CONV", "CONVERT", "CORRESPONDING", "COST", "COUNT", "CREATE",
			"CROSS", "CURRENT", "CURRENT_DATE", "CURRENT_DATETIME",
			"CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER",
			"CURRENT_VALUE", "CURRVAL", "CURSOR", "CYCLE", "DATA", "DATA_TYPE",
			"DATE", "DAYOFWEEK", "DAYOFMONTH", "DAYOFYEAR", "DATETIME", "DAY",
			"DAY_MILLISECOND", "DEALLOCATE", "DEC", "DECIMAL", "DECLARE",
			"DECODE", "DECREMENT", "DEFAULT", "DEFERRABLE", "DEFERRED",
			"DELETE", "DEPTH", "DESC", "DESCRIBE", "DESCRIPTOR", "DIAGNOSTICS",
			"DICTIONARY", "DIFFERENCE", "DISCONNECT", "DISTINCT", "DOMAIN",
			"DOUBLE", "DROP", "EACH", "ELSE", "ELSEIF", "ELT", "END", "ENUM",
			"EQUALS", "ESCAPE", "EVALUATE", "EXCEPT", "EXCEPTION", "EXCLUDE",
			"EXEC", "EXECUTE", "EXISTS", "EXP", "EXPLAIN", "EXTERNAL",
			"EXTRACT", "FALSE", "FETCH", "FILE", "FIRST", "FLOAT", "FLOOR",
			"FOR", "FROM", "FROM_DAYS", "FOREIGN", "FOUND", "FROM_UNIXTIME",
			"FULL", "FUNCTION", "GENERAL", "GET", "GE_INF", "GE_LE", "GE_LT",
			"GLOBAL", "GO", "GOTO", "GRANT", "GRANTS", "GREATEST", "GROUP",
			"GROUP_CONCAT", "GROUPBY_NUM", "GROUPS", "GT_INF", "GT_LE",
			"GT_LT", "HASH", "HAVING", "HOUR", "HOUR_MILLISECOND", "IDENTITY",
			"IF", "IGNORE", "IMMEDIATE", "IN", "INACTIVE", "INCREMENT",
			"INDEX", "INDEXES", "INDEX_CARDINALITY", "INDICATOR", "INFINITE",
			"INF_LE", "INF_LT", "INHERIT", "INET_ATON", "INITIALLY", "INNER",
			"INOUT", "INPUT", "INSERT", "INSTANCES", "INET_NTOA", "INSTR",
			"INSTRB", "INST_NUM", "INT", "INTEGER", "INTERSECT",
			"INTERSECTION", "INTERVAL", "INTO", "INVALIDATE", "IS",
			"ISOLATION", "JAVA", "JOIN", "KEY", "KEYLIMIT", "KEYS", "LANGUAGE",
			"LAST", "LAST_DAY", "LDB", "LEADING", "LEAST", "LEAVE", "LEFT",
			"LENGTH", "LENGTHB", "LESS", "LEVEL", "LIKE",
			"LIKE_MATCH_LOWER_BOUND", "LIKE_MATCH_UPPER_BOUND", "LIMIT",
			"LIST", "LOCAL", "LOCAL_TRANSACTION_ID", "LOCK", "LOOP", "LOWER",
			"LPAD", "LTRIM", "MAKEDATE", "MAKETIME", "MATCH", "MAX", "MAXIMUM",
			"MAXVALUE", "MD5", "MEDIUMINT", "MEMBERS", "METHOD", "MILLISECOND",
			"MIN", "MINUTE", "MINUTE_MILLISECOND", "MINVALUE", "MOD", "MODIFY",
			"MODULE", "MONETARY", "MONTH", "MONTHS_BETWEEN", "MULTISET",
			"MULTISET_OF", "NA", "NAME", "NAMES", "NATIONAL", "NATURAL",
			"NCHAR", "NEW", "NEXT", "NEXTVAL", "NEXT_VALUE", "NO", "NOCACHE",
			"NOCYCLE", "NOMAXVALUE", "NOMINVALUE", "NONE", "NOT", "NULL",
			"NULLIF", "NUMERIC", "NVL", "NVL2", "OBJECT", "OCTET_LENGTH", "OF",
			"OFF", "OID", "OLD", "ON", "ONLY", "OPEN", "OPERATION",
			"OPERATORS", "OPTIMIZATION", "OPTION", "OR", "ORDER",
			"ORDERBY_NUM", "OTHERS", "OUT", "OUTER", "OUTPUT", "OVERLAPS",
			"PARAMETERS", "PARTIAL", "PARTITION", "PARTITIONING", "PARTITIONS",
			"PASSWORD", "PENDANT", "POSITION", "POWER", "PRECISION",
			"PREORDER", "PREPARE", "PRESERVE", "PRIMARY", "PRINT", "PRIOR",
			"PRIORITY", "PRIVATE", "PRIVILEGES", "PROCEDURE", "PROTECTED",
			"PROXY", "QUERY", "RANDOM", "RANGE", "REPEAT", "READ", "REAL",
			"RECURSIVE", "REF", "REFERENCES", "REFERENCING", "REGISTER",
			"REGEXP", "REJECT", "RELATIVE", "REMOVE", "RENAME", "REORGANIZE",
			"REPEATABLE", "REPLACE", "RESIGNAL", "RESTRICT", "RETAIN",
			"RETURN", "RETURNS", "REVERSE", "REVOKE", "REUSE_OID", "RIGHT",
			"ROLE", "ROLLBACK", "ROUND", "ROUTINE", "ROW", "ROWNUM", "ROWS",
			"RPAD", "RTRIM", "SAVEPOINT", "SCHEMA", "SCOPE", "SCROLL",
			"SEARCH", "SECOND", "SECOND_MILLISECOND", "SEC_TO_TIME", "SECTION",
			"SELECT", "SENSITIVE", "SEPARATOR", "SEQUENCE", "SEQUENCE_OF",
			"SERIAL", "SERIALIZABLE", "SESSION", "SESSION_USER", "SET",
			"SETEQ", "SETNEQ", "SET_OF", "SHARED", "SHORT", "SHOW", "SIBLINGS",
			"SIGN", "SIGNAL", "SIMILAR", "SIZE", "SMALLINT", "SOME", "SPACE",
			"SQL", "SQLCODE", "SQLERROR", "SQLEXCEPTION", "SQLSTATE",
			"SQLWARNING", "STABILITY", "START", "STATEMENT", "STATISTICS",
			"STATUS", "STDDEV", "STDDEV_POP", "STDDEV_SAMP", "STRING",
			"STRUCTURE", "SUBCLASS", "SUBSET", "SUBSETEQ", "SUBSTR", "SUBSTRB",
			"SUBSTRING", "SUBSTRING_INDEX", "SUM", "SUPERCLASS", "SUPERSET",
			"SUPERSETEQ", "SYSDATE", "SYSDATETIME", "SYSTEM", "SYSTEM_USER",
			"SYSTIME", "SYSTIMESTAMP", "SYS_CONNECT_BY_PATH", "SYS_DATE",
			"SYS_DATETIME", "SYS_TIME", "SYS_TIMESTAMP", "SYS_USER", "TABLE",
			"TABLES", "TEMPORARY", "TEST", "THAN", "THEN", "THERE", "TIME",
			"TIME_TO_SEC", "TIMEDIFF", "TIMEOUT", "TIMESTAMP", "TIMEZONE_HOUR",
			"TIMEZONE_MINUTE", "TINYINT", "TO", "TO_CHAR", "TO_DATE",
			"TO_DATETIME", "TO_DAYS", "TO_NUMBER", "TO_TIME", "TO_TIMESTAMP",
			"TRACE", "TRAILING", "TRANSACTION", "TRANSLATE", "TRANSLATION",
			"TRIGGER", "TRIGGERS", "TRIM", "TRUE", "TRUNC", "TYPE", "TYPEOF",
			"UNCOMMITTED", "UNDER", "UNION", "UNIQUE", "UNKNOWN", "UPDATE",
			"UPPER", "USAGE", "USE", "USER", "USING", "UTC_TIME", "UTC_DATE",
			"UTIME", "VALUE", "VALUES", "VARCHAR", "VARIABLE", "VARIANCE",
			"VAR_POP", "VAR_SAMP", "VARYING", "VCLASS", "VERSION", "VIEW",
			"VIRTUAL", "VISIBLE",
			"WHERE",
			"WAIT",
			"WEEKDAY",
			"WHEN",
			"WHENEVER",
			"WHILE",
			"WITH",
			"WITHOUT",
			"WORK",
			"WORKSPACE",
			"WRITE",
			"YEAR",
			"ZONE",
			"PROMOTE",
			//MySQL keywords
			"ACOS", "ADDDATE", "ASIN", "ATAN", "ATAN2", "BIT_AND", "BIT_OR",
			"BIT_XOR", "BIT_COUNT", "CEILING", "CONCAT", "CONCAT_WS", "COS",
			"COT", "CURDATE", "CURRENT_DATE", "CURTIME", "CURRENT_TIME",
			"CURRENT_TIMESTAMP", "DATABASE", "DATE_ADD", "DATEDIFF",
			"DATE_FORMAT", "DATE_SUB", "DAY_HOUR", "DAY_MINUTE", "DAY_SECOND",
			"DEGREES", "DISTINCTROW", "DIV", "DO", "DUPLICATE", "FIELD",
			"FIND_IN_SET", "FORMAT", "HOUR_MINUTE", "HOUR_SECOND", "IFNULL",
			"ISNULL", "LAST_INSERT_ID", "LCASE", "LN", "LOCALTIME",
			"LOCALTIMESTAMP", "LIST_DBS", "LOG2", "LOG10", "LOCATE", "MID",
			"MINUTE_SECOND", "NOW", "PI", "POW", "QUARTER", "RADIANS", "RAND",
			"ROW_COUNT", "ROLLUP", "SIN", "SQRT", "STRCMP", "STR_TO_DATE",
			"SUBDATE", "TAN", "TIME_FORMAT", "WIDTH_BUCKET", "TRUNCATE",
			"UCASE", "UNIX_TIMESTAMP", "WEEK", "XOR", "YEAR_MONTH", "&&", "||",
			"&", "|", "~", "^", "<=>", "<<", ">>", "%", "!=", "!" };

	public final static String[] KEYWORDS_CONTENTS_ASSIST = new String[]{
			"ABS",
			"ABSOLUTE",
			"ADDTIME",
			"ACTION",
			"ACTIVE",
			"ADD",
			"ADD_MONTHS",
			"AFTER",
			"ALIAS",
			"ALL",
			"ALLOCATE",
			"ALTER",
			"ANALYZE",
			"AND",
			"ANY",
			"ARE",
			"AS",
			"ASC",
			"ASCII",
			"ASSERTION",
			"ASYNC",
			"AT",
			"ATTACH",
			"ATTRIBUTE",
			"AUDIT",
			"AUTHORIZATION",
			"AUTOCOMMIT",
			"INCR",
			"AUTO_INCREMENT",
			"AVG",
			"BEFORE",
			"BEGIN",
			"BETWEEN ",
			"BIGINT",
			"BIN",
			"BIT",
			"BIT_LENGTH",
			"BIT_TO_BLOB",
			"BLOB",
			"BLOB_FROM_FILE",
			"BLOB_LENGTH",
			"BLOB_TO_BIT",
			"CLOB_FROM_FILE",
			"CLOB_LENGTH",
			"CLOB_TO_CHAR",
			"BOOLEAN",
			"BOTH",
			"BREADTH",
			"BY",
			"CACHE",
			"CALL",
			"CASCADE",
			"CASCADED",
			"CASE",
			"CAST",
			"CATALOG",
			"CEIL",
			"CHANGE",
			"CHAR",
			"CHARACTER",
			"CHARACTER_LENGTH",
			"CHAR_LENGTH",
			"CHECK",
			"CHR",
			"CLASS",
			"CLOB",
			"CLASSES",
			"COLUMNS",
			"CLOSE",
			"CLUSTER",
			"COALESCE",
			"COLLATE",
			"COLLATION",
			"COLUMN",
			"COMMIT",
			"COMMITTED",
			"COMPLETION",
			"CONNECT",
			"CONNECTION",
			"CONNECT_BY_ISCYCLE",
			"CONNECT_BY_ISLEAF",
			"CONNECTION_BY_ROOT",
			"CONSTRAINT",
			"CONSTRAINTS",
			"CONTINUE",
			"CONV",
			"CONVERT",
			"CORRESPONDING",
			"COST",
			"COUNT",
			//"CREATE",
			"CREATE TABLE ",
			//"CREATE TABLE \"test1\"(\n\t\"col1\" INTEGER AUTO_INCREMENT,\n\t\"col2\" VARCHAR(100) DEFAULT 'default' NOT NULL UNIQUE,\n\t\"col3\" DATETIME UNIQUE,\n\tCONSTRAINT pk_test1_col1 PRIMARY KEY(\"col1\"));",
			"CREATE VIEW ",
			"CREATE INDEX ",
			"CREATE SERIAL ",
			"CREATE TRIGGER ",
			"CREATE FUNCTION ",
			"CREATE PROCEDURE ",
			"CHARSET",
			//"CROSS",
			"CURRENT",
			"CURRENT_DATE",
			"CURRENT_DATETIME",
			"CURRENT_TIME",
			"CURRENT_TIMESTAMP",
			"CURRENT_USER",
			"CURRENT_VALUE",
			"CURRVAL",
			"CURSOR",
			"CYCLE",
			"DATA",
			"DATA_TYPE",
			"DATE",
			"DAYOFWEEK",
			"DAYOFMONTH",
			"DAYOFYEAR",
			"DATETIME",
			"DAY",
			"DAY_MILLISECOND",
			"DEALLOCATE",
			"DEC",
			"DECIMAL",
			"DECLARE",
			"DECODE",
			"DECR",
			"DECREMENT",
			"DEFAULT",
			"DEFERRABLE",
			"DEFERRED",
			"DELETE FROM ",
			"DENSE_RANK",
			//"DELETE FROM # WHERE #=#;", 
			"DEPTH", "DESC", "DESCRIBE", "DESCRIPTOR", "DIAGNOSTICS",
			"DICTIONARY", "DIFFERENCE", "DISCONNECT", "DISTINCT", "DOMAIN",
			"DOUBLE", "DRANDOM",
			"DRAND",
			"DROP",
			"EACH",
			"ELSE",
			"ELSEIF",
			"ELT",
			"END",
			"ENUM",
			"EQUALS",
			"ESCAPE",
			"EVALUATE",
			"EXCEPT",
			"EXCEPTION",
			"EXCLUDE",
			"EXEC",
			"EXECUTE",
			"EXISTS",
			"EXP",
			"EXPLAIN",
			"EXTERNAL",
			"EXTRACT",
			"FALSE",
			"FETCH",
			"FILE",
			"FIRST",
			"FLOAT",
			"FLOOR",
			"FOR",
			"FROM ",
			"FROM_DAYS",
			"FOREIGN",
			"FOUND",
			"FROM_UNIXTIME",
			"FULL",
			"FUNCTION",
			"GENERAL",
			"GET",
			"GE_INF",
			"GE_LE",
			"GE_LT",
			"GLOBAL",
			"GO",
			"GOTO",
			"GRANT",
			"GRANTS",
			"GREATEST",
			"GROUP BY ",
			"GROUP_CONCAT",
			"GROUPBY_NUM",
			"GROUPS",
			"GT_INF",
			"GT_LE",
			"GT_LT",
			"HASH",
			"HAVING",
			"HEX",
			"HOUR",
			"HOUR_MILLISECOND",
			"IDENTITY",
			"IF",
			"IGNORE",
			"IMMEDIATE",
			"IN",
			"INACTIVE",
			"INCREMENT",
			"INDEX",
			"INDEXES",
			"INDEX_CARDINALITY",
			"INDICATOR",
			"INFINITE",
			"INF_LE",
			"INF_LT",
			"INHERIT",
			"INITIALLY",
			"INNER",
			"INOUT",
			"INPUT",
			"INSERT INTO ",
			//"INSERT INTO # (#, #) VALUES (#, #);",
			"INSTANCES", "COERCIBILITY", "INSTR", "INSTRB", "INST_NUM", "INT",
			"INTEGER", "INTERSECT", "INTERSECTION", "INTERVAL", "INTO",
			"INVALIDATE", "IS", "ISOLATION", "JAVA", "JOIN", "KEY", "KEYLIMIT",
			"KEYS", "LANGUAGE", "LAG", "LAST", "LAST_DAY", "LDB", "LEAD",
			"LEADING", "LEAST", "LEAVE", "LEFT", "LENGTH", "LENGTHB", "LESS",
			"LEVEL", "LIKE", "LIKE_MATCH_LOWER_BOUND",
			"LIKE_MATCH_UPPER_BOUND", "LIMIT ", "LIST", "LOCAL",
			"LOCAL_TRANSACTION_ID", "LOCK", "LOOP", "LOWER", "LPAD", "LTRIM",
			"MAKEDATE", "MAKETIME", "MATCH", "MAX", "MAXIMUM", "MAXVALUE",
			"MD5", "MEDIUMINT", "MEMBERS", "METHOD", "MILLISECOND", "MIN",
			"MINUTE", "MINUTE_MILLISECOND", "MINVALUE", "MOD", "MODIFY",
			"MODULE", "MONETARY", "MONTH", "MONTHS_BETWEEN", "MULTISET",
			"MULTISET_OF", "NA", "NAME", "NAMES", "NATIONAL", "NATURAL",
			"NTILE", "NCHAR", "NEW", "NEXT", "NEXTVAL", "NEXT_VALUE", "NO",
			"NOCACHE", "NOCYCLE", "NOMAXVALUE", "NOMINVALUE", "NONE", "NOT",
			"NULL", "NULLIF", "NUMERIC", "NVL", "NVL2", "OBJECT",
			"OCTET_LENGTH", "OF", "OFF", "OID", "OLD", "ON", "ONLY", "OPEN",
			"OPERATION", "OPERATORS", "OPTIMIZATION", "OPTION", "OR",
			"ORDER BY ", "ORDERBY_NUM", "OTHERS", "OUT", "OUTER", "OUTPUT",
			"OVERLAPS", "PARAMETERS", "PARTIAL", "PARTITION", "PARTITIONING",
			"PARTITIONS", "PASSWORD", "PENDANT", "POSITION", "POWER",
			"PRECISION", "PREORDER", "PREPARE", "PRESERVE", "PRIMARY", "PRINT",
			"PRIOR", "PRIORITY", "PRIVATE", "PRIVILEGES", "PROCEDURE",
			"PROTECTED", "PROXY", "QUERY", "RANDOM", "RANGE", "REPEAT", "READ",
			"REAL", "RANK", "RECURSIVE", "REF",
			"REFERENCES",
			"REFERENCING",
			"REGISTER",
			"REGEXP",
			"REJECT",
			"RELATIVE",
			"RLIKE",
			"REMOVE",
			"RENAME",
			"REORGANIZE",
			"REPEATABLE",
			"REPLACE",
			"RESIGNAL",
			"RESTRICT",
			"RETAIN",
			"RETURN",
			"RETURNS",
			"REVERSE",
			"REVOKE",
			"REUSE_OID",
			"RIGHT",
			"ROLE",
			"ROLLBACK",
			"ROUND",
			"ROUTINE",
			"ROW",
			"ROWNUM",
			"ROWS",
			"ROW_NUMBER",
			"RPAD",
			"RTRIM",
			"SAVEPOINT",
			"SCHEMA",
			"SCOPE",
			"SCROLL",
			"SEARCH",
			"SECOND",
			"SECOND_MILLISECOND",
			"SEC_TO_TIME",
			"SECTION",
			"SELECT ",
			"SELECT \n\t* \nFROM \n\t",
			//"SELECT * FROM # WHERE # LIMIT #, #;",
			"SENSITIVE", "SEPARATOR", "SEQUENCE", "SEQUENCE_OF", "SERIAL",
			"SERIALIZABLE", "SESSION", "SESSION_USER", "SET", "SETEQ",
			"SETNEQ", "SET_OF", "SHARED", "SHORT", "SHOW", "SIBLINGS", "SIGN",
			"SIGNAL", "SIMILAR", "SIZE", "SMALLINT", "SOME", "SPACE", "SQL",
			"SQLCODE", "SQLERROR", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING",
			"STABILITY", "START", "STATEMENT", "STATISTICS", "STATUS",
			"STDDEV", "STDDEV_POP", "STDDEV_SAMP", "STRING", "STRUCTURE",
			"SUBCLASS", "SUBSET", "SUBSETEQ", "SUBSTR", "SUBSTRB", "SUBSTRING",
			"SUBSTRING_INDEX", "SUM", "SUPERCLASS", "SUPERSET", "SUPERSETEQ",
			"SYSDATE", "SYSDATETIME", "SYSTEM", "SYSTEM_USER", "SYSTIME",
			"SYSTIMESTAMP", "SYS_CONNECT_BY_PATH", "SYS_DATE", "SYS_DATETIME",
			"SYS_TIME", "SYS_TIMESTAMP", "SYS_USER", "TABLE", "TABLES",
			"TEMPORARY", "TEST", "THAN", "THEN", "THERE", "TIME",
			"TIME_TO_SEC", "TIMEDIFF",
			"TIMEOUT",
			"TIMESTAMP",
			"TIMEZONE_HOUR",
			"TIMEZONE_MINUTE",
			"TINYINT",
			"TO",
			"TO_CHAR",
			"TO_DATE",
			"TO_DATETIME",
			"TO_DAYS",
			"TO_NUMBER",
			"TO_TIME",
			"TO_TIMESTAMP",
			"TRACE",
			"TRAILING",
			"TRANSACTION",
			"TRANSLATE",
			"TRANSLATION",
			"TRIGGER",
			"TRIGGERS",
			"TRIM",
			"TRUE",
			"TRUNC",
			"TYPE",
			"TYPEOF",
			"UNCOMMITTED",
			"UNDER",
			"UNION",
			"UNIQUE",
			"UNKNOWN",
			"UPDATE",
			//"UPDATE #table# SET #=# WHERE #=#;",
			"UPPER",
			"USAGE",
			"USE",
			"USER",
			"USING",
			"UTC_TIME",
			"UTC_DATE",
			"UTIME",
			//"VALUE",
			"VALUES (", "VARCHAR", "VARIABLE", "VARIANCE", "VAR_POP",
			"VAR_SAMP", "VARYING", "VCLASS", "VERSION", "VIEW", "VIRTUAL",
			"VISIBLE", "WHERE ",
			"WAIT",
			"WEEKDAY",
			"WHEN",
			"WHENEVER",
			"WHILE",
			"WITH",
			"WITHOUT",
			"WORK",
			"WORKSPACE",
			"WRITE",
			"YEAR",
			"ZONE",
			"PROMOTE",
			//MySQL keywords
			"ACOS", "ADDDATE", "ASIN", "ATAN", "ATAN2", "BIT_AND", "BIT_OR",
			"BIT_XOR", "BIT_COUNT", "CEILING", "CONCAT", "CONCAT_WS", "COS",
			"COT", "CURDATE", "CURRENT_DATE", "CURTIME", "CURRENT_TIME",
			"CURRENT_TIMESTAMP", "DATABASE", "DATE_ADD", "DATEDIFF",
			"DATE_FORMAT", "DATE_SUB", "DAY_HOUR", "DAY_MINUTE", "DAY_SECOND",
			"DEGREES", "DISTINCTROW", "DIV", "DO", "DUPLICATE", "FIELD",
			"FIND_IN_SET", "FORMAT", "HOUR_MINUTE", "HOUR_SECOND", "IFNULL",
			"ISNULL", "LAST_INSERT_ID", "LCASE", "LN", "LOCALTIME",
			"LOCALTIMESTAMP", "LOG2", "LOG10", "LOCATE", "MID",
			"MINUTE_SECOND", "NOW", "PI", "POW", "QUARTER", "RADIANS", "RAND",
			"ROW_COUNT", "ROLLUP", "SIN", "SQRT", "STRCMP", "STR_TO_DATE",
			"SUBDATE", "TAN", "TIME_FORMAT", "TRUNCATE", "UCASE",
			"UNIX_TIMESTAMP", "WEEK", "XOR", "YEAR_MONTH" };

	public final static String[] KEYWORDS_AUTO_UPPER = new String[]{"abs",
			"absolute", "action", "active", "add", "add_months", "after",
			"alias", "all", "allocate", "alter", "analyze", "and", "any",
			"are", "as", "asc", "ascii", "assertion", "async", "at", "attach",
			"attribute", "audit", "authorization", "autocommit",
			"auto_increment", "avg", "before", "begin", "between", "bigint",
			"bit", "bit_length", "bit_to_blob", "blob", "blob_from_file",
			"blob_length", "blob_to_bit", "clob_from_file", "clob_length",
			"clob_to_char", "boolean", "both", "breadth", "by", "cache",
			"call", "cascade", "cascaded", "case", "cast", "catalog", "ceil",
			"change", "char", "character", "character_length", "char_length",
			"check", "chr", "class", "clob", "classes", "columns", "close",
			"cluster", "coalesce", "collate", "collation", "column", "commit",
			"committed", "completion", "connect", "connection",
			"connect_by_iscycle", "connect_by_isleaf", "connection_by_root",
			"constraint", "constraints", "continue", "conv", "convert",
			"corresponding", "cost", "count", "create", "cross", "current",
			"current_date", "current_datetime", "current_time",
			"current_timestamp", "current_user", "current_value", "currval",
			"cursor", "cycle", "data", "data_type", "date", "dayofweek",
			"dayofmonth", "dayofyear", "datetime", "day", "day_millisecond",
			"deallocate", "dec", "decimal", "declare", "decode", "decrement",
			"default", "deferrable", "deferred", "delete", "depth", "desc",
			"describe", "descriptor", "diagnostics", "dictionary",
			"difference", "disconnect", "distinct", "domain", "double", "drop",
			"each", "else", "elseif", "elt", "end", "equals", "escape",
			"evaluate", "except", "exception", "exclude", "exec", "execute",
			"exists", "exp", "explain", "external", "extract", "false",
			"fetch", "file", "first", "float", "floor", "for", "from",
			"from_days", "foreign", "found", "from_unixtime", "full",
			"function", "general", "get", "ge_inf", "ge_le", "ge_lt", "global",
			"go", "goto", "grant", "grants", "greatest", "group",
			"group_concat", "groupby_num", "groups", "gt_inf", "gt_le",
			"gt_lt", "hash", "having", "hour", "hour_millisecond", "identity",
			"if", "ignore", "immediate", "in", "inactive", "increment",
			"index", "indexes", "index_cardinality", "indicator", "infinite",
			"inf_le", "inf_lt", "inherit", "initially", "inner", "inout",
			"input", "insert", "instances", "instr", "instrb", "inst_num",
			"int", "integer", "intersect", "intersection", "interval", "into",
			"invalidate", "is", "isolation", "java", "join", "key", "keylimit",
			"keys", "language", "last", "last_day", "ldb", "leading", "least",
			"leave", "left", "length", "lengthb", "less", "level", "like",
			"like_match_lower_bound", "like_match_upper_bound", "limit",
			"list", "local", "local_transaction_id", "lock", "loop", "lower",
			"lpad", "ltrim", "makedate", "maketime", "match", "max", "maximum",
			"maxvalue", "md5", "mediumint", "members", "method", "millisecond",
			"min", "minute", "minute_millisecond", "minvalue", "mod", "modify",
			"module", "monetary", "month", "months_between", "multiset",
			"multiset_of", "na", "name", "names", "national", "natural",
			"nchar", "new", "next", "nextval", "next_value", "no", "nocache",
			"nocycle", "nomaxvalue", "nominvalue", "none", "not", "null",
			"nullif", "numeric", "nvl", "nvl2", "object", "octet_length", "of",
			"off", "oid", "old", "on", "only", "open", "operation",
			"operators", "optimization", "option", "or", "order",
			"orderby_num", "others", "out", "outer", "output", "overlaps",
			"parameters", "partial", "partition", "partitioning", "partitions",
			"password", "pendant", "position", "power", "precision",
			"preorder", "prepare", "preserve", "primary", "print", "prior",
			"priority", "private", "privileges", "procedure", "protected",
			"proxy", "query", "random", "range", "repeat", "read", "real",
			"recursive", "ref", "references", "referencing", "register",
			"regexp", "reject", "relative", "remove", "rename", "reorganize",
			"repeatable", "replace", "resignal", "restrict", "retain",
			"return", "returns", "reverse", "revoke", "reuse_oid", "right",
			"role", "rollback", "round", "routine", "row", "rownum", "rows",
			"rpad", "rtrim", "savepoint", "schema", "scope", "scroll",
			"search", "second", "second_millisecond", "sec_to_time", "section",
			"select", "sensitive", "separator", "sequence", "sequence_of",
			"serial", "serializable", "session", "session_user", "set",
			"seteq", "setneq", "set_of", "shared", "short", "show", "siblings",
			"sign", "signal", "similar", "size", "smallint", "some", "space",
			"sql", "sqlcode", "sqlerror", "sqlexception", "sqlstate",
			"sqlwarning", "stability", "start", "statement", "statistics",
			"status", "stddev", "stddev_pop", "stddev_samp", "string",
			"structure", "subclass", "subset", "subseteq", "substr", "substrb",
			"substring", "substring_index", "sum", "superclass", "superset",
			"superseteq", "sysdate", "sysdatetime", "system", "system_user",
			"systime", "systimestamp", "sys_connect_by_path", "sys_date",
			"sys_datetime", "sys_time", "sys_timestamp", "sys_user", "table",
			"tables", "temporary", /*"test", */"than", "then", "there",
			"time", "time_to_sec", "timediff", "timeout", "timestamp",
			"timezone_hour", "timezone_minute", "tinyint", "to", "to_char",
			"to_date", "to_datetime", "to_days", "to_number", "to_time", "to_timestamp",
			"trace", "trailing", "transaction", "translate", "translation",
			"trigger", "triggers", "trim", "true", "trunc", "type", "typeof",
			"uncommitted", "under", "union", "unique", "unknown", "update",
			"upper", "usage", "use", "user", "using", "utc_time", "utc_date",
			"utime", "value", "values", "varchar", "variable", "variance",
			"var_pop", "var_samp", "varying",
			"vclass",
			"version",
			"view",
			"virtual",
			"visible",
			"where",
			"wait",
			"weekday",
			"when",
			"whenever",
			"while",
			"with",
			"without",
			"work",
			"workspace",
			"write",
			"year",
			"zone",
			//mysql keywords
			"acos", "adddate", "asin", "atan", "atan2", "bit_and", "bit_or",
			"bit_xor", "bit_count", "ceiling", "concat", "concat_ws", "cos",
			"cot", "curdate", "current_date", "curtime", "current_time",
			"current_timestamp", "database", "date_add", "datediff",
			"date_format", "date_sub", "day_hour", "day_minute", "day_second",
			"degrees", "distinctrow", "div", "do", "duplicate", "field",
			"find_in_set", "format", "hour_minute", "hour_second", "ifnull",
			"isnull", "last_insert_id", "lcase", "ln", "localtime",
			"localtimestamp", "log2", "log10", "locate", "mid",
			"minute_second", "now", "pi", "pow", "quarter", "radians", "rand",
			"row_count", "rollup", "sin", "sqrt", "strcmp", "str_to_date",
			"subdate", "tan", "time_format", "truncate", "ucase",
			"unix_timestamp", "week", "xor", "year_month", "char_to_blob",
			"char_to_clob", "inet_aton", "inet_ntoa", "to_datetime",
			"list_dbs", "width_bucket", "addtime", "incr", "bin", "charset",
			"decr", "dense_rank", "drandom", "drand", "hex", "coercibility",
			"lead", "ntile", "rank", "rlike", "row_number"
	};

	public final static Map<Character, List<String>> KEYWORDINDEXMAP = new HashMap<Character, List<String>>();
	public final static Set<String> KEYWORDSET = new HashSet<String>();
	static {
		for (String keyword : KEYWORDS) {
			char startChar = keyword.charAt(0);
			List<String> keyWordList = KEYWORDINDEXMAP.get(startChar);
			if (keyWordList == null) {
				keyWordList = new ArrayList<String>();
				KEYWORDINDEXMAP.put(startChar, keyWordList);
			}
			keyWordList.add(keyword);
			KEYWORDSET.add(keyword);
		}
	}

	public static boolean isKeyword(String name) {
		if (name == null) {
			return false;
		}
		return KEYWORDSET.contains(name.toUpperCase());
	}

	public static String escapeKeyword(String text) {
		if (text == null) {
			return null;
		}

		if (text.length() == 0) {
			return "";
		}

		boolean isKeyword = QuerySyntax.isKeyword(text);
		if (!isKeyword) {
			/*For bug [TOOLS-2992]*/
			if (text.indexOf(']') > -1) {
				return text;
			}
			boolean isValidate = checkIdentifier(text);
			if (isValidate) {
				return text;
			} else {
				return "[" + text + "]";
			}
		}

		return "[" + text + "]";
	}

	/**
	 * Get the Key words map.
	 * 
	 * @return Map<keyword, keyword>
	 */
	public static Map<String, String> getKeywordsMap() {
		Map<String, String> keywordsMap = new HashMap<String, String>();
		String[] keyWords = QuerySyntax.KEYWORDS;
		for (String k : keyWords) {
			keywordsMap.put(k, k);
		}
		return keywordsMap;
	}

	private QuerySyntax() {
		// empty
	}

	public static String getKeywordContent(String name) {
		return keywordsDescriptionProperties.getProperty(name);
	}

	/**
	 * Check the table name and column name and procedure related name and
	 * serial name.
	 * 
	 * @param identifier the identifier string
	 * @return the identifier
	 */
	public static boolean checkIdentifier(String identifier) {
		if (StringUtil.isEmpty(identifier)) {
			return false;
		}

		if (identifier.indexOf(" ") >= 0) {
			return false;
		}
		if (identifier.indexOf("\t") >= 0) {
			return false;
		}
		if (identifier.indexOf("/") >= 0) {
			return false;
		}
		if (identifier.indexOf(".") >= 0) {
			return false;
		}
		if (identifier.indexOf("~") >= 0) {
			return false;
		}
		if (identifier.indexOf(",") >= 0) {
			return false;
		}
		if (identifier.indexOf("\\") >= 0) {
			return false;
		}
		if (identifier.indexOf("\"") >= 0) {
			return false;
		}
		if (identifier.indexOf("|") >= 0) {
			return false;
		}
		if (identifier.indexOf("]") >= 0) {
			return false;
		}
		if (identifier.indexOf("[") >= 0) {
			return false;
		}
		if (identifier.indexOf("}") >= 0) {
			return false;
		}
		if (identifier.indexOf("{") >= 0) {
			return false;
		}
		if (identifier.indexOf(")") >= 0) {
			return false;
		}
		if (identifier.indexOf("(") >= 0) {
			return false;
		}
		if (identifier.indexOf("=") >= 0) {
			return false;
		}
		if (identifier.indexOf("-") >= 0) {
			return false;
		}
		if (identifier.indexOf("+") >= 0) {
			return false;
		}
		if (identifier.indexOf("?") >= 0) {
			return false;
		}
		if (identifier.indexOf("<") >= 0) {
			return false;
		}
		if (identifier.indexOf(">") >= 0) {
			return false;
		}
		if (identifier.indexOf(":") >= 0) {
			return false;
		}
		if (identifier.indexOf(";") >= 0) {
			return false;
		}
		if (identifier.indexOf("!") >= 0) {
			return false;
		}
		if (identifier.indexOf("'") >= 0) {
			return false;
		}
		if (identifier.indexOf("@") >= 0) {
			return false;
		}
		if (identifier.indexOf("$") >= 0) {
			return false;
		}
		if (identifier.indexOf("^") >= 0) {
			return false;
		}
		if (identifier.indexOf("&") >= 0) {
			return false;
		}
		if (identifier.indexOf("*") >= 0) {
			return false;
		}
		if (identifier.indexOf("`") >= 0) {
			return false;
		}
		/*Check first char*/
		char firstChar = identifier.charAt(0);
		if (firstChar >= 'a' && firstChar <= 'z' || firstChar >= 'A'
				&& firstChar <= 'Z') {
			return true;
		} else {
			return false;
		}
	}

}
