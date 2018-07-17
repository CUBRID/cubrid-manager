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
package com.cubrid.common.ui.cubrid.table.progress;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.jface.dialogs.ProgressIndicator;
import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.event.ExportDataFinishAllTableEvent;
import com.cubrid.common.ui.cubrid.table.event.handler.ExportDataEventHandler;
import com.cubrid.common.ui.cubrid.table.event.handler.IExportDataEventHandler;
import com.cubrid.common.ui.cubrid.table.export.AbsExportThread;
import com.cubrid.common.ui.cubrid.table.export.ExportDataThread;
import com.cubrid.common.ui.cubrid.table.export.ExportLoadDBThread;
import com.cubrid.common.ui.cubrid.table.export.ExportSchemaThread;
import com.cubrid.common.ui.cubrid.table.export.IJobListener;
import com.cubrid.common.ui.cubrid.table.export.JobEvent;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;

/**
 * Common Export Data Process Manager
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-5-24 created by Kevin.Wang
 */
public class CommonExportDataProcessManager implements
		IExportDataProcessManager,
		IJobListener {
	private static final Logger LOGGER = LogUtil.getLogger(CommonExportDataProcessManager.class);

	private final IExportDataEventHandler exportDataEventHandler;
	private final DatabaseInfo dbInfo;
	private final ExportConfig exportConfig;

	private volatile boolean initSuccess = false;
	public final static int COMMIT_LINES = 1000;
	public static final int RSPAGESIZE = 49999;

	public volatile boolean stop = false;
	private volatile List<AbsExportThread> threadList = new ArrayList<AbsExportThread>();

	protected ProgressIndicator progressIndicator;
	private ThreadPoolExecutor executor = null;
	private int totalTaskCount = 0;



	public CommonExportDataProcessManager(DatabaseInfo dbInfo, ExportConfig exportConfig,
			IExportDataMonitor dataMonitor, ProgressIndicator progressIndicator) {
		this.dbInfo = dbInfo;
		this.exportConfig = exportConfig;
		this.exportDataEventHandler = new ExportDataEventHandler(dataMonitor);
		this.progressIndicator = progressIndicator;
		executor = new ThreadPoolExecutor(exportConfig.getThreadCount(),
				exportConfig.getThreadCount(), 1, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>());
		init();
	}

	/**
	 * Init(Calculate record count)
	 *
	 */
	private void init() {
		initSuccess = setMaximumProgress();
	}

	private boolean setMaximumProgress() { // FIXME move this logic to core module
		long totalRecordsCount = 0;
		long totalSchemasCount = 0;
		boolean isCalculateAble = true;

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		if (exportConfig.getExportType() == ExportConfig.EXPORT_TO_FILE) {
			if (exportConfig.isExportSchema()) {
				totalSchemasCount++;
				totalTaskCount++;
				if (exportConfig.isExportIndex()) {
					totalSchemasCount++;
				}
				if (exportConfig.isExportSerial()) {
					totalSchemasCount++;
				}
				if (exportConfig.isExportTrigger()) {
					totalSchemasCount++;
				}
				if (exportConfig.isExportView()) {
					totalSchemasCount++;
				}
			}
		} else {
			totalTaskCount++;
		}
		if (exportConfig.isExportData()) {
			try {
				conn = JDBCConnectionManager.getConnection(dbInfo, true);
				stmt = conn.createStatement();
				for (String tableName : exportConfig.getTableNameList()) {
					if (exportConfig.getExportType() == ExportConfig.EXPORT_TO_FILE) {
						totalTaskCount++;
					}
					StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM ");
					sb.append(QuerySyntax.escapeKeyword(tableName)).append(" ");
					String whereCondition = exportConfig.getWhereCondition(tableName);
					if (whereCondition != null) {
						sb.append(whereCondition);
					}

					String sql = sb.toString();

					sql = dbInfo.wrapShardQuery(sql);
					try {
						rs = stmt.executeQuery(sql);
						if (rs.next()) {
							int count = rs.getInt(1);
							totalRecordsCount += count;
							exportConfig.setTotalCount(tableName, count);
						}
					} catch (Exception e) {
						LOGGER.error("count table records count error : ", e);
						exportConfig.setTotalCount(tableName, ExportConfig.COUNT_UNKNOW);
						isCalculateAble = false;
					} finally {
						QueryUtil.freeQuery(rs);
					}
				}

				for (String sqlName : exportConfig.getSQLNameList()) {
					String namedSQL = exportConfig.getSQL(sqlName);
					if (!StringUtil.isEmpty(namedSQL)) {
						if (exportConfig.getExportType() == ExportConfig.EXPORT_TO_FILE) {
							totalTaskCount++;
						}
						StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM (");
						while (namedSQL.trim().endsWith(";")
								|| namedSQL.trim().endsWith(StringUtil.NEWLINE)) {
							if (namedSQL.length() > 1) {
								namedSQL = namedSQL.substring(0, namedSQL.length() - 1);
							}
						}
						sb.append(namedSQL);
						sb.append(")").append("[cal_count]");

						String sql = sb.toString();

						sql = dbInfo.wrapShardQuery(sql);
						try {
							rs = stmt.executeQuery(sql);
							if (rs.next()) {
								int count = rs.getInt(1);
								totalRecordsCount += count;
								exportConfig.setTotalCount(sqlName, count);
							}
						} catch (Exception e) {
							LOGGER.error("count table records count error : ", e);
							exportConfig.setTotalCount(sqlName, ExportConfig.COUNT_UNKNOW);
							isCalculateAble = false;
						} finally {
							QueryUtil.freeQuery(rs);
						}
					}

				}
			} catch (Exception e) {
				LOGGER.error("", e);
			} finally {
				QueryUtil.freeQuery(conn, stmt, rs);
			}

			//load db need all count
			if (exportConfig.getExportType() == ExportConfig.EXPORT_TO_LOADDB) {
				exportConfig.setTotalCount(ExportConfig.LOADDB_DATAFILEKEY, totalRecordsCount);
			}
		}

		if (isCalculateAble) {
			int pmLength = (int) (totalRecordsCount + totalSchemasCount);
			progressIndicator.beginTask(pmLength);
		} else {
			progressIndicator.beginAnimatedTask();
		}

		return true;
	}

	public void startProcess() {
		//load db
		if (exportConfig.getExportType() == ExportConfig.EXPORT_TO_LOADDB) {
			AbsExportThread thread = new ExportLoadDBThread(dbInfo, exportConfig,
					exportDataEventHandler, this);
			executor.execute(thread);
			threadList.add(thread);
		} else { //file
			//schema etc.
			if (exportConfig.isExportSchema()) {
				AbsExportThread thread = new ExportSchemaThread(dbInfo, exportConfig,
						exportDataEventHandler, this);
				executor.execute(thread);
				threadList.add(thread);
			}
			//data
			if (exportConfig.isExportData()) {
				for (String tableName : exportConfig.getTableNameList()) {
					AbsExportThread thread = new ExportDataThread(dbInfo, tableName, exportConfig,
							exportDataEventHandler, this);
					executor.execute(thread);
					threadList.add(thread);
				}
				for (String sqlName : exportConfig.getSQLNameList()) {
					AbsExportThread thread = new ExportDataThread(dbInfo, sqlName, exportConfig,
							exportDataEventHandler, this);
					executor.execute(thread);
					threadList.add(thread);
				}
			}
		}
	}

	public void stopProcess() {
		stop = true;
		for(AbsExportThread thread : threadList) {
			thread.performStop();
		}
		executor.shutdownNow();
	}

	public boolean isInitSuccess() {
		return initSuccess;
	}

	public void jobStart(JobEvent event) {
	}

	public void jobStoped(JobEvent event) {
		if (executor.getCompletedTaskCount() == totalTaskCount - 1) {
			exportDataEventHandler.handleEvent(new ExportDataFinishAllTableEvent());
			executor.shutdown();
		}
	}

}
