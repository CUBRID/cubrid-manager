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
package com.cubrid.common.ui.cubrid.table.dialog.imp.progress;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.cubrid.table.dialog.imp.ImportConfig;
import com.cubrid.common.ui.cubrid.table.dialog.imp.event.ImportDataFailedEvent;
import com.cubrid.common.ui.cubrid.table.dialog.imp.event.ImportDataSuccessEvent;
import com.cubrid.common.ui.cubrid.table.dialog.imp.handler.ImportDataEventHandler;
import com.cubrid.common.ui.cubrid.table.dialog.imp.model.ImportRowData;
import com.cubrid.common.ui.spi.model.CubridDatabase;

/**
 *
 * The ImportFromSQLThread Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-8-9 created by Kevin.Wang
 */
public class ImportFromSQLRunnable extends
		AbsImportRunnable {

	private static final Logger LOGGER = LogUtil.getLogger(ImportFromSQLRunnable.class);
	private static final String NULL_CHARACTER_ASCII_CODE = "\u0000";
	private final String fileName;
	private boolean end = false;
	private int lineNumber = 1;
	private int commitCount = 1;

	/**
	 * The constructor
	 *
	 * @param database
	 * @param parameterList
	 * @param fileName
	 * @param importConfig
	 * @param importDataEventHandler
	 */
	public ImportFromSQLRunnable(CubridDatabase database, String fileName,
			ImportConfig importConfig, ImportDataEventHandler importDataEventHandler,
			ImportDataProgressManager progressManager) {
		super(database, fileName, importConfig, progressManager, importDataEventHandler);
		this.fileName = tableConfig.getName();
		this.commitCount = importConfig.getCommitLine();
		setTableName(tableConfig.getName());//set super class table name to file name
	}

	@Override
	public void doRun() throws Exception { // FIXME move this logic to core module
		BufferedReader reader = null;
		try {
			File file = new File(tableConfig.getFilePath());
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(
					file.getAbsoluteFile()), importConfig.getFilesCharset()));
			while (!end) {
				List<ImportRowData> sqlList = loadSQL(reader);
				if (sqlList.size() == 0) {
					continue;
				}
				executeSQL(conn, sqlList);

				if (isCanceled) {
					return;
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
					LOGGER.error("", e);
				}
			}
		}
	}

	/**
	 * execute SQL
	 *
	 * @param conn
	 * @param sqlList
	 */
	public void executeSQL(Connection conn, List<ImportRowData> sqlList) {
		commit(sqlList);
	}

	/**
	 * Commit the data
	 *
	 * @param importDataEventHandler IImportDataEventHandler
	 * @param rowList List<RowData>
	 */
	protected void commit(List<ImportRowData> rowList) { // FIXME move this logic to core module
		if (stmt == null || conn == null) {
			return;
		}

		String sql = "";
		int importCount = 0;
		long currentRunCount = 0;
		int totalWorkedSize = 0;
		List<ImportRowData> batchDataList = new ArrayList<ImportRowData>();
		for (ImportRowData rowData : rowList) {
			batchDataList.add(rowData);
			sql = rowData.getSql();
			try {
				importCount++;
				currentRunCount++;
				totalWorkedSize += rowData.getWorkSize();
				stmt.execute(sql);

				if (importCount % commitCount == 0) {
					QueryUtil.commit(conn);
					ImportDataSuccessEvent successEvt = new ImportDataSuccessEvent(fileName,
							importCount);
					successEvt.setWorkedSize(totalWorkedSize);
					handleEvent(successEvt);
					LOGGER.debug("Committed : currentRunCount={}, commitCount={}", currentRunCount,
							importCount);
					importCount = 0;
					totalWorkedSize = 0;
					batchDataList.clear();
				}
			} catch (Exception e) {
				for (ImportRowData batchData : batchDataList) {
					String tempSql = batchData.getSql();
					String errMessage = Messages.msgFailedByRollback;
					if (StringUtil.isEqual(tempSql, sql)) {
						errMessage = e.getMessage();
					}
					ImportDataFailedEvent failedEvt = new ImportDataFailedEvent(fileName, 1, tempSql,
							errMessage);
					failedEvt.setWorkedSize(batchData.getWorkSize());
					handleEvent(failedEvt);

					writeErrorLog(batchData);
				}

				QueryUtil.rollback(conn);
				LOGGER.debug("Execute SQL from SQL file sql : {}, error message: {}", sql, e);

				importCount = 0;
				totalWorkedSize = 0;
				batchDataList.clear();
			}
		}
		if (importCount > 0) {
			QueryUtil.commit(conn);
			ImportDataSuccessEvent successEvt = new ImportDataSuccessEvent(fileName, importCount);
			successEvt.setWorkedSize(totalWorkedSize);
			handleEvent(successEvt);
			LOGGER.debug("Committed : currentRunCount={}, commitCount={}", currentRunCount,
					importCount);
			importCount = 0;
			totalWorkedSize = 0;
			batchDataList.clear();
		}
	}

	/**
	 * load SQL file by bufferedReader
	 *
	 * @param reader
	 * @return
	 */
	protected List<ImportRowData> loadSQL(BufferedReader reader) { // FIXME move this logic to core module
		List<ImportRowData> sqlList = new ArrayList<ImportRowData>();
		try {
			String tempString = null;
			int seprateSQLLineNumber = lineNumber;//the first line which sql doesnt'in one line
			String preString = "";
			boolean parseCommentFlag = false;//parse /**/ comment
			int sizeCount = 0;
			while ((tempString = reader.readLine()) != null) {
				sizeCount += tempString.getBytes().length;
				tempString = tempString.trim();

				if (parseCommentFlag && tempString.endsWith("*/")) {
					parseCommentFlag = false;
					lineNumber++;
					continue;
				} else if (parseCommentFlag) {
					lineNumber++;
					continue;
				}
				if (tempString.trim().startsWith("/*")) {
					if (tempString.trim().endsWith("*/")) {
						lineNumber++;
						continue;
					} else {
						parseCommentFlag = true;
						lineNumber++;
						continue;
					}
				}
				if (StringUtil.isEmpty(tempString) || tempString.trim().startsWith("--")
						|| tempString.trim().startsWith("//")) {
					lineNumber++;
					continue;
				}
				if (StringUtil.isNotEmpty(preString)) {
					tempString = preString + tempString;
				}

				List<String[]> qList = StringUtil.extractQueries(tempString);
				if (qList.isEmpty()) {
					if (StringUtil.isEmpty(preString)) {
						preString = preString + tempString + "\n";
						seprateSQLLineNumber = lineNumber;
					} else {
						preString = tempString + "\n";
					}
				} else {
					for (int i = 0; i < qList.size(); i++) {
						if (StringUtil.isEmpty(preString)) {
							seprateSQLLineNumber = lineNumber;
						}
						String[] o = qList.get(i);
						String sql = o[0].toString();
						if (sql.endsWith(";")) {
							ImportRowData rowData = new ImportRowData(seprateSQLLineNumber);
							rowData.setSql(sql.replaceAll(NULL_CHARACTER_ASCII_CODE, ""));
							rowData.setWorkSize((sizeCount / qList.size()));
							sqlList.add(rowData);
							if (i == qList.size() - 1
									&& (Integer.valueOf(o[1]) + 1 < tempString.length())) {
								preString = tempString.substring(Integer.valueOf(o[1]) + 1,
										tempString.length());
							} else {
								preString = "";
							}
						} else {
							preString = preString.substring(Integer.valueOf(o[1]),
									tempString.length());
							break;
						}
					}
					sizeCount = 0;
					if (sqlList.size() + 1 > commitCount) {
						lineNumber++;
						return sqlList;
					}
				}

				lineNumber++;
			}

		} catch (Exception e) {
			LOGGER.error("", e);
		}
		end = true;

		return sqlList;
	}

	protected void setCommitCount(int commitCount) {
		this.commitCount = commitCount;
	}

}
