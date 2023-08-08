/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.common.navigator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;

import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.query.format.SqlFormattingStrategy;
import com.cubrid.common.ui.spi.util.GetInfoDataUtil;
import com.cubrid.common.ui.spi.util.SQLGenerateUtils;
import com.cubrid.cubridmanager.core.Messages;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.ClassInfo;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetAllAttrTask;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetAllClassListTask;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetViewAllColumnsTask;
import com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy;

/**
 *
 *
 * The GetSchemaDDLTask Description :
 * Author : Kevin.Wang
 * Create date : 2014-2-18
 *
 */
public class GetSchemaDDLTask extends
		JDBCTask { // FIXME move to core module
	private static final Logger LOGGER = LogUtil.getLogger(GetSchemaDDLTask.class);

	/*It may be null*/
	private IProgressMonitor monitor;
	private String schemaName;
	private boolean isTable;
	private String ddl;

	public GetSchemaDDLTask(DatabaseInfo dbInfo, String schemaName, boolean isTable, IProgressMonitor monitor) {
		super("GetSchemaDDLTask", dbInfo);
		this.schemaName = schemaName;
		this.isTable = isTable;
		this.monitor = monitor;
	}

	/**
	 * Execute the tasks
	 */
	public void execute() {
		try {
			if (CompatibleUtil.isAfter900(databaseInfo)) {
				String sql = QueryUtil.getShowCreateSQL(schemaName, isTable);
				sql = DatabaseInfo.wrapShardQuery(databaseInfo, sql);
				StringBuilder sb = new StringBuilder();

				if (errorMsg != null && errorMsg.trim().length() > 0) {
					return;
				}

				if (connection == null || connection.isClosed()) {
					errorMsg = Messages.error_getConnection;
					return;
				}

				String viewName = null;
				stmt = connection.createStatement();
				rs = (CUBRIDResultSetProxy) stmt.executeQuery(sql);
				while (rs.next()) {
					if (isTable) {
						sb.append(rs.getString(2));
					} else {
						viewName = QuerySyntax.escapeKeyword(rs.getString(1));
						if (sb.length() > 0) {
							sb.append(" UNION ALL ");
						}
						sb.append(" ").append(rs.getString(2)).append(" ");
					}
				}
				if (isTable) {
					ddl = sb.toString();
				} else {
					ddl = "CREATE OR REPLACE VIEW " + viewName + " AS " + sb.toString() + ";";
				}
			} else {
				StringBuilder sqlScript = new StringBuilder();
				if (!isTable) {
					// Get class info
					GetAllClassListTask getAllClassListTask = new GetAllClassListTask(databaseInfo, connection);
					getAllClassListTask.setTableName(schemaName);
					getAllClassListTask.getClassInfoTaskExcute();
					// If failed
					if (getAllClassListTask.getErrorMsg() != null || getAllClassListTask.isCancel()) {
						errorMsg = getAllClassListTask.getErrorMsg();
						LOGGER.error(errorMsg);
						return;
					}
					/*Check user cancel*/
					if (monitor != null && monitor.isCanceled()) {
						errorMsg = "The user canceled.";
						return;
					}
					ClassInfo classInfo = getAllClassListTask.getClassInfo();

					// Get view column
					GetViewAllColumnsTask getAllDBVclassTask = new GetViewAllColumnsTask(databaseInfo, connection);
					getAllDBVclassTask.setClassName(schemaName);
					getAllDBVclassTask.getAllVclassListTaskExecute();
					// If failed
					if (getAllDBVclassTask.getErrorMsg() != null || getAllDBVclassTask.isCancel()) {
						errorMsg = getAllDBVclassTask.getErrorMsg();
						LOGGER.error(errorMsg);
						return;
					}
					/*Check user cancel*/
					if (monitor != null && monitor.isCanceled()) {
						errorMsg = "The user canceled.";
						return;
					}
					// Get query list
					List<String> vclassList = getAllDBVclassTask.getAllVclassList();
					List<Map<String, String>> queryListData = new ArrayList<Map<String, String>>();
					for (String sql : vclassList) {
						Map<String, String> map = new HashMap<String, String>();
						map.put("0", sql);
						queryListData.add(map);
					}

					/*Check user cancel*/
					if (monitor != null && monitor.isCanceled()) {
						errorMsg = "The user canceled.";
						return;
					}

					// Get all attribute
					GetAllAttrTask getAllAttrTask = new GetAllAttrTask(databaseInfo, connection);
					getAllAttrTask.setClassName(schemaName);
					getAllAttrTask.getAttrList();
					// If failed
					if (getAllAttrTask.getErrorMsg() != null) {
						errorMsg = getAllAttrTask.getErrorMsg();
						LOGGER.error(errorMsg);
						return;
					}
					List<DBAttribute> attrList = getAllAttrTask.getAllAttrList();

					List<Map<String, String>> viewColListData = GetInfoDataUtil.getViewColMapList(attrList);
					sqlScript.append(GetInfoDataUtil.getViewCreateSQLScript(false, databaseInfo, classInfo, schemaName,
							viewColListData, queryListData));
				} else {
					String ddl = SQLGenerateUtils.getCreateSQL(databaseInfo, schemaName);
					sqlScript.append(ddl == null ? "" : ddl);
				}

				ddl = sqlScript.toString();
			}

		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			errorMsg = e.getMessage();
		} finally {
			finish();
			if (StringUtil.isNotEmpty(ddl)) {
				SqlFormattingStrategy formator = new SqlFormattingStrategy();
				String formated = formator.format(ddl);
				ddl = formated;
			}
		}
	}

	public String getCreateDDL() {
		return ddl;
	}

}
