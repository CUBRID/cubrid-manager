/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.cubrid.table.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.common.model.SerialInfo;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.FileUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.event.ExportDataBeginOneTableEvent;
import com.cubrid.common.ui.cubrid.table.event.ExportDataFailedOneTableEvent;
import com.cubrid.common.ui.cubrid.table.event.ExportDataFinishOneTableEvent;
import com.cubrid.common.ui.cubrid.table.event.ExportDataSuccessEvent;
import com.cubrid.common.ui.cubrid.table.event.handler.IExportDataEventHandler;
import com.cubrid.common.ui.cubrid.table.export.handler.ExprotToOBSHandler;
import com.cubrid.common.ui.cubrid.table.progress.ExportConfig;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.serial.task.GetSerialInfoListTask;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy;

/**
 * Export Schema Thread
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-5-24 created by Kevin.Wang
 */
public class ExportSchemaThread extends
		AbsExportThread {
	private static final Logger LOGGER = LogUtil.getLogger(ExportSchemaThread.class);

	protected final DatabaseInfo dbInfo;
	protected final ExportConfig exportConfig;
	protected final IExportDataEventHandler exportDataEventHandler;

	public ExportSchemaThread(DatabaseInfo dbInfo, ExportConfig exportConfig,
			IExportDataEventHandler exportDataEventHandler, IJobListener jobListener) {
		super(jobListener);
		this.dbInfo = dbInfo;
		this.exportConfig = exportConfig;
		this.exportDataEventHandler = exportDataEventHandler;
	}

	protected void doRun() { // FIXME move this logic to core module

		File dirFile = null;
		try {
			dirFile = new File(exportConfig.getDataFileFolder() + File.separator + "ddl");
			if (!dirFile.exists()) {
				dirFile.mkdir();
			}
		} catch (Exception e) {
			LOGGER.error("create schema dir error : ", e);
			return;
		}

		try {
			String schemaFile = null;
			String indexFile = null;
			String triggerFile = null;
			if (exportConfig.isExportSchema()) {
				schemaFile = dirFile + File.separator + "schema.sql";
				exportDataEventHandler.handleEvent(new ExportDataBeginOneTableEvent(
						ExportConfig.TASK_NAME_SCHEMA));
			}
			if (exportConfig.isExportIndex()) {
				indexFile = dirFile + File.separator + "index.sql";
				exportDataEventHandler.handleEvent(new ExportDataBeginOneTableEvent(
						ExportConfig.TASK_NAME_INDEX));
			}
			if (exportConfig.isExportTrigger()) {
				triggerFile = dirFile + File.separator + "trigger.sql";
				exportDataEventHandler.handleEvent(new ExportDataBeginOneTableEvent(
						ExportConfig.TASK_NAME_TRIGGER));
			}

			Set<String> tableSet = new HashSet<String>();
			tableSet.addAll(exportConfig.getTableNameList());
			ExprotToOBSHandler.exportSchemaToOBSFile(dbInfo, exportDataEventHandler, tableSet,
					schemaFile, indexFile, triggerFile, exportConfig.getFileCharset(),
					exportConfig.isExportSerialStartValue(), false);

			if (exportConfig.isExportSchema()) {
				exportDataEventHandler.handleEvent(new ExportDataSuccessEvent(
						ExportConfig.TASK_NAME_SCHEMA));
				exportDataEventHandler.handleEvent(new ExportDataFinishOneTableEvent(
						ExportConfig.TASK_NAME_SCHEMA));
			}
			if (exportConfig.isExportIndex()) {
				exportDataEventHandler.handleEvent(new ExportDataSuccessEvent(
						ExportConfig.TASK_NAME_INDEX));
				exportDataEventHandler.handleEvent(new ExportDataFinishOneTableEvent(
						ExportConfig.TASK_NAME_INDEX));
			}
			if (exportConfig.isExportTrigger()) {
				exportDataEventHandler.handleEvent(new ExportDataSuccessEvent(
						ExportConfig.TASK_NAME_TRIGGER));
				exportDataEventHandler.handleEvent(new ExportDataFinishOneTableEvent(
						ExportConfig.TASK_NAME_TRIGGER));
			}
		} catch (Exception e) {
			if (exportConfig.isExportSchema()) {
				exportDataEventHandler.handleEvent(new ExportDataFailedOneTableEvent(
						ExportConfig.TASK_NAME_SCHEMA));
			}
			if (exportConfig.isExportIndex()) {
				exportDataEventHandler.handleEvent(new ExportDataFailedOneTableEvent(
						ExportConfig.TASK_NAME_INDEX));
			}
			if (exportConfig.isExportTrigger()) {
				exportDataEventHandler.handleEvent(new ExportDataFailedOneTableEvent(
						ExportConfig.TASK_NAME_TRIGGER));
			}
			LOGGER.error("create schema index trigger error : ", e);
		}

		try {
			if (exportConfig.isExportSerial()) {
				exportDataEventHandler.handleEvent(new ExportDataBeginOneTableEvent(
						ExportConfig.TASK_NAME_SERIAL));
				String serialFile = dirFile + File.separator + "serial.sql";
				exportSerial(serialFile);
				exportDataEventHandler.handleEvent(new ExportDataSuccessEvent(
						ExportConfig.TASK_NAME_SERIAL));
				exportDataEventHandler.handleEvent(new ExportDataFinishOneTableEvent(
						ExportConfig.TASK_NAME_SERIAL));
			}
		} catch (Exception e) {
			exportDataEventHandler.handleEvent(new ExportDataFailedOneTableEvent(
					ExportConfig.TASK_NAME_SERIAL));
			LOGGER.error("create serial.sql error : ", e);
		}

		try {
			if (exportConfig.isExportView()) {
				exportDataEventHandler.handleEvent(new ExportDataBeginOneTableEvent(
						ExportConfig.TASK_NAME_VIEW));
				String viewFile = dirFile + File.separator + "view.sql";
				exportDataEventHandler.handleEvent(new ExportDataSuccessEvent(
						ExportConfig.TASK_NAME_VIEW));
				exportDataEventHandler.handleEvent(new ExportDataFinishOneTableEvent(
						ExportConfig.TASK_NAME_VIEW));
				exportView(viewFile);
			}
		} catch (Exception e) {
			exportDataEventHandler.handleEvent(new ExportDataFailedOneTableEvent(
					ExportConfig.TASK_NAME_VIEW));
			LOGGER.error("create view.sql error : ", e);
		}
	}

	public void performStop() {
		if (!isFinished) {
			isStoped = true;
		}
	}

	/**
	 * exportView
	 *
	 * @param filePath view file path
	 * @throws Exception
	 */
	private void exportView(String filePath) throws Exception { // FIXME move this logic to core module
		BufferedWriter fs = null;
		boolean hasView = false;
		File viewFile = null;
		try {
			fs = FileUtil.getBufferedWriter(filePath, exportConfig.getFileCharset());
			for (String ddl : getAllViewsDDL()) {
				fs.write(ddl);
				fs.write(StringUtil.NEWLINE);
				hasView = true;
			}
			fs.flush();
		} finally {
			try {
				if (fs != null) {
					fs.close();
				}
				if (!hasView) {
					if (viewFile != null) {
						viewFile.delete();
					}
				}
			} catch (IOException e) {
				LOGGER.error("", e);
			}
		}

	}

	/**
	 * exportSerial
	 *
	 * @param serialFilePath
	 * @throws Exception
	 */
	private void exportSerial(String serialFilePath) throws Exception { // FIXME move this logic to core module
		BufferedWriter fs = null;
		boolean hasSerial = false;
		File serialFile = null;
		try {
			fs = FileUtil.getBufferedWriter(serialFilePath, exportConfig.getFileCharset());
			if (exportConfig.isExportSerial()) {
				GetSerialInfoListTask task = new GetSerialInfoListTask(dbInfo);
				task.execute();
				boolean isSupportCache = CompatibleUtil.isSupportCache(dbInfo);
				for (SerialInfo serial : task.getSerialInfoList()) {
					fs.write(QueryUtil.createSerialSQLScript(serial, isSupportCache));
					fs.write(StringUtil.NEWLINE);
					hasSerial = true;
				}
				fs.flush();
			}
		} finally {
			try {
				if (fs != null) {
					fs.close();
				}
			} catch (IOException e) {
				LOGGER.error("", e);
			}
			if (!hasSerial) {
				if (serialFile != null) {
					serialFile.delete();
				}
			}
		}
	}

	private List<String> getAllViewsDDL() { // FIXME move this logic to core module
		List<String> resultList = new ArrayList<String>();
		List<String> viewNameList = new ArrayList<String>();
		LinkedHashMap<String, String> viewQuerySpecMap = new LinkedHashMap<String, String>();
		Connection conn = null;
		Statement stmt = null;
		CUBRIDResultSetProxy rs = null;

		String sql = "SELECT c.class_name, c.class_type" + " FROM db_class c, db_attribute a"
				+ " WHERE c.class_name=a.class_name AND c.is_system_class='NO'"
				+ " AND c.class_type='VCLASS'" + " GROUP BY c.class_name, c.class_type"
				+ " ORDER BY c.class_type, c.class_name";

		// [TOOLS-2425]Support shard broker
		sql = dbInfo.wrapShardQuery(sql);
		try {
			conn = JDBCConnectionManager.getConnection(dbInfo, false);
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);

			rs = (CUBRIDResultSetProxy) stmt.executeQuery(sql);
			while (rs.next()) {
				viewNameList.add(rs.getString(1));
			}
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			QueryUtil.freeQuery(stmt, rs);
		}

		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);

			for (String viewName : viewNameList) {
				String querySpecSql = "SELECT vclass_def FROM db_vclass WHERE vclass_name='"
						+ viewName + "'";

				// [TOOLS-2425]Support shard broker
				querySpecSql = dbInfo.wrapShardQuery(querySpecSql);

				try {
					rs = (CUBRIDResultSetProxy) stmt.executeQuery(querySpecSql);
					if (rs.next()) {
						viewQuerySpecMap.put(viewName, rs.getString(1));
					}
				} finally {
					QueryUtil.freeQuery(rs);
				}
			}

		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			QueryUtil.freeQuery(conn, stmt);
		}

		for (Map.Entry<String, String> entry : viewQuerySpecMap.entrySet()) {
			SchemaInfo viewInfo = dbInfo.getSchemaInfo(entry.getKey());
			String ddl = getViewDDL(viewInfo, dbInfo, entry.getValue());
			resultList.add(ddl);
		}

		return resultList;
	}

	/**
	 * Return DDL of a view
	 *
	 * @param viewInfo SchemaInfo the given reference of a SChemaInfo object
	 * @param getDatabaseInfo DatabaseInfo
	 * @param querySpec String the view querySpec
	 * @return
	 */

	private String getViewDDL(SchemaInfo viewInfo, DatabaseInfo getDatabaseInfo, String querySpec) { // FIXME move this logic to core module
		StringBuffer sb = new StringBuffer();
		if (CompatibleUtil.isSupportReplaceView(getDatabaseInfo)) {
			sb.append("CREATE OR REPLACE VIEW ");
		} else {
			sb.append("CREATE VIEW ");
		}
		sb.append(QuerySyntax.escapeKeyword(viewInfo.getClassname()));
		sb.append("(");

		for (DBAttribute addr : viewInfo.getAttributes()) { // "Name", "Data
			// type", "Shared",
			// "Default","Default value"
			String type = addr.getType();
			sb.append(StringUtil.NEWLINE).append(QuerySyntax.escapeKeyword(addr.getName())).append(
					" ").append(type);
			String defaultType = addr.isShared() ? "shared" : "default";
			String defaultValue = addr.getDefault();

			if (defaultType != null && !"".equals(defaultType) && defaultValue != null
					&& !"".equals(defaultValue)) {
				if (type != null
						&& (DataType.DATATYPE_CHAR.equalsIgnoreCase(type)
								|| DataType.DATATYPE_STRING.equalsIgnoreCase(type) || DataType.DATATYPE_VARCHAR.equalsIgnoreCase(type))) {
					sb.append(" " + defaultType).append(" '" + defaultValue + "'");
				} else {
					sb.append(" " + defaultType).append(" " + defaultValue);
				}
			}
			sb.append(",");
		}

		if (!viewInfo.getAttributes().isEmpty() && sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append(")").append(StringUtil.NEWLINE);
		sb.append("    AS ").append(StringUtil.NEWLINE);
		if (querySpec != null) {
			sb.append(querySpec);
		}

		sb.append(";").append(StringUtil.NEWLINE);
		return sb.toString();
	}
}
