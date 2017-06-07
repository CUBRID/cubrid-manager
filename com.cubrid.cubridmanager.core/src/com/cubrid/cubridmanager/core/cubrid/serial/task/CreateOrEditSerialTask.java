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
package com.cubrid.cubridmanager.core.cubrid.serial.task;

import java.sql.SQLException;

import org.slf4j.Logger;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.Messages;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;

/**
 * This task is responsible to create serial
 * 
 * @author pangqiren
 * @version 1.0 - 2009-5-11 created by pangqiren
 */
public class CreateOrEditSerialTask extends JDBCTask {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogUtil.getLogger(CreateOrEditSerialTask.class);

	/**
	 * The constructor
	 *
	 * @param dbInfo
	 */
	public CreateOrEditSerialTask(DatabaseInfo dbInfo) {
		super("CreateOrEditSerial", dbInfo);
	}

	/**
	 * Create serial by JDBC
	 * 
	 * @param serialName String The given serial name
	 * @param startVal String The given start value
	 * @param incrementVal String The given incremental vlaue
	 * @param maxVal String The given maximal value
	 * @param minVal String The given minimal value
	 * @param isCycle boolean Whether is cycle
	 * @param isNoMinVal boolean whether is NOMINVALUE
	 * @param isNoMaxVal boolean whether is NOMAXVALUE
	 * @param cacheCount string the cache count
	 * @param isNoCache boolean whether is NOCACHE
	 * @param description string the Serial's comment
	 */
	public void createSerial(String serialName, String startVal,
			String incrementVal, String maxVal, String minVal, boolean isCycle,
			boolean isNoMinVal, boolean isNoMaxVal, String cacheCount,
			boolean isNoCache, String description) {
		if (StringUtil.isNotEmpty(errorMsg)) {
			return;
		}

		//databaseInfo.getServerInfo().compareVersionKey("8.2.2") >= 0;
		boolean isSupportCache = CompatibleUtil.isSupportCache(databaseInfo);

		String sql = "CREATE SERIAL " + QuerySyntax.escapeKeyword(serialName);
		if (StringUtil.isNotEmpty(startVal)) {
			sql += " START WITH " + startVal;
		}

		if (StringUtil.isNotEmpty(incrementVal)) {
			sql += " INCREMENT BY " + incrementVal;
		}

		if (isNoMinVal) {
			sql += " NOMINVALUE ";
		} else if (StringUtil.isNotEmpty(minVal)) {
			sql += " MINVALUE " + minVal;
		}

		if (isNoMaxVal) {
			sql += " NOMAXVALUE ";
		} else if (StringUtil.isNotEmpty(maxVal)) {
			sql += " MAXVALUE " + maxVal;
		}

		if (isCycle) {
			sql += " CYCLE";
		} else {
			sql += " NOCYCLE";
		}

		if (isSupportCache) {
			if (isNoCache) {
				sql += " NOCACHE";
			} else if (StringUtil.isNotEmpty(cacheCount)) {
				sql += " CACHE " + cacheCount;
			}
		}

		if (StringUtil.isNotEmpty(description)) {
			description = String.format("'%s'", description);
			sql += String.format(" COMMENT %s", StringUtil.escapeQuotes(description));
		}

		try {
			if (connection == null || connection.isClosed()) {
				if (StringUtil.isEmpty(errorMsg)) {
					errorMsg = Messages.error_getConnection;
				}
				return;
			}

			// [TOOLS-2425]Support shard broker
			sql = DatabaseInfo.wrapShardQuery(databaseInfo, sql);

			stmt = connection.createStatement();
			stmt.execute(sql);
		} catch (SQLException e) {
			errorMsg = e.getMessage();
		} finally {
			finish();
		}
	}

	/**
	 * Edit serial by JDBC
	 * 
	 * @param serialName String The given serial name
	 * @param startVal String The given start value
	 * @param incrementVal String The given incremental value
	 * @param maxVal String The given maximal value
	 * @param minVal String The given minimal value
	 * @param isCycle Whether is cycle
	 * @param isNoMinVal boolean whether is NOMINVALUE
	 * @param isNoMaxVal boolean whether is NOMAXVALUE
	 * @param cacheCount string the cache count
	 * @param isNoCache boolean whether is NOCACHE
	 * @param description String Serial's comment
	 */
	public void editSerial(String serialName, String startVal,
			String incrementVal, String maxVal, String minVal, boolean isCycle,
			boolean isNoMinVal, boolean isNoMaxVal, String cacheCount,
			boolean isNoCache, String description) {
		if (errorMsg != null && errorMsg.trim().length() > 0) {
			return;
		}

		//databaseInfo.getServerInfo().compareVersionKey("8.2.2") >= 0;
		boolean isSupportCache = CompatibleUtil.isSupportCache(databaseInfo);

		String dropSerialSql = "DROP SERIAL " + QuerySyntax.escapeKeyword(serialName);

		String sql = "CREATE SERIAL " + QuerySyntax.escapeKeyword(serialName);
		if (StringUtil.isNotEmpty(startVal)) {
			sql += " START WITH " + startVal;
		}

		if (StringUtil.isNotEmpty(incrementVal)) {
			sql += " INCREMENT BY " + incrementVal;
		}

		if (isNoMinVal) {
			sql += " NOMINVALUE ";
		} else if (StringUtil.isNotEmpty(minVal)) {
			sql += " MINVALUE " + minVal;
		}

		if (isNoMaxVal) {
			sql += " NOMAXVALUE ";
		} else if (StringUtil.isNotEmpty(maxVal)) {
			sql += " MAXVALUE " + maxVal;
		}

		if (isCycle) {
			sql += " CYCLE";
		} else {
			sql += " NOCYCLE";
		}

		if (isSupportCache) {
			if (isNoCache) {
				sql += " NOCACHE";
			} else if (StringUtil.isNotEmpty(cacheCount)) {
				sql += " CACHE " + cacheCount;
			}
		}

		if (StringUtil.isNotEmpty(description)) {
			description = String.format("'%s'", description);
			sql += String.format(" COMMENT %s", StringUtil.escapeQuotes(description));
		}

		try {
			if (connection == null || connection.isClosed()) {
				if (StringUtil.isEmpty(errorMsg)) {
					errorMsg = Messages.error_getConnection;
				}
				return;
			}

			// [TOOLS-2425]Support shard broker
			dropSerialSql = DatabaseInfo.wrapShardQuery(databaseInfo, dropSerialSql);
			sql = DatabaseInfo.wrapShardQuery(databaseInfo, sql);

			connection.setAutoCommit(false);
			stmt = connection.createStatement();
			stmt.execute(dropSerialSql);
			stmt.execute(sql);
			connection.commit();
		} catch (SQLException e) {
			QueryUtil.rollback(connection);
			errorMsg = e.getMessage();
		} finally {
			finish();
		}
	}
}
