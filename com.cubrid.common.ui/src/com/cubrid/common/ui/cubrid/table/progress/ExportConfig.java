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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.cubrid.table.dialog.PstmtParameter;
import com.cubrid.common.ui.cubrid.table.export.ResultSetDataCache;

/**
 * ExportConfig Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-5-28 created by Kevin.Wang
 */
public class ExportConfig implements
		Cloneable {

	private static final Logger LOGGER = LogUtil.getLogger(ExportConfig.class);
	public static final int EXPORT_TO_FILE = 1;
	public static final int EXPORT_TO_LOADDB = 2;
	public static final int EXPORT_FROM_HISTORY = 3;

	public static final int FILE_TYPE_SQL = 1;
	public static final int FILE_TYPE_CSV = 2;
	public static final int FILE_TYPE_XLS = 3;
	public static final int FILE_TYPE_XLSX = 4;
	public static final int FILE_TYPE_OBS = 5;
	public static final int FILE_TYPE_TXT = 6;
	public static final int FILE_TYPE_LOADDB = 7;
	public static final String LOADDB_DATAFILEKEY = "_LOADDBDATAFILENAME";
	public static final String LOADDB_SCHEMAFILEKEY = "_LOADDBSCHEMAFILENAME";
	public static final String LOADDB_INDEXFILEKEY = "_LOADDBINDEXFILENAME";
	public static final String LOADDB_TRIGGERFILEKEY = "_LOADDBTRIGGERFILENAME";

	public static final String TASK_NAME_SCHEMA = "schema.sql";
	public static final String TASK_NAME_INDEX = "index.sql";
	public static final String TASK_NAME_SERIAL = "serial.sql";
	public static final String TASK_NAME_VIEW = "view.sql";
	public static final String TASK_NAME_TRIGGER = "trigger.sql";

	public static final int COUNT_UNKNOW = -1;

	private String name;
	private boolean isHistory = false;
	private int exportType;
	private int exportFileType;
	private boolean isExportSchema;
	private boolean isExportData;
	private boolean isExportIndex;
	private boolean isExportSerial;
	private boolean isExportView;
	private boolean isExportTrigger;
	private String dataFolderPath;
	private String indexFilePath;
	private String schemaFilePath;
	private String triggerFilePath;
	private String fileCharset;
	private boolean isFirstRowAsColumnName;
	private String nullValueTranslation;
	private boolean isExportSerialValue;
	private String columnDelimeter;
	private String rowDelimeter;
	private int threadCount;
	private boolean usePagination = true;
	private boolean exportFromCache = false;

	private List<String> tableNameList = new ArrayList<String>();
	private Map<String, List<String>> columnNameListMap = new HashMap<String, List<String>>();
	private Map<String, String> whereConditionMap = new HashMap<String, String>();
	private Map<String, String> dataFilePath = new HashMap<String, String>();
	private Map<String, Long> recordCountMap = new HashMap<String, Long>();
	private Map<String, String> sqlMap = new HashMap<String, String>();
	// Don't clone pstmListMap
	private Map<String, List<PstmtParameter>> pstmListMap = new HashMap<String, List<PstmtParameter>>();

	private ResultSetDataCache resultSetDataCache;

	private boolean isExportLob = true;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setExportType(int type) {
		this.exportType = type;
	}

	public void setExportSchema(boolean isExportSchema) {
		this.isExportSchema = isExportSchema;
	}

	public void setExportData(boolean isExportData) {
		this.isExportData = isExportData;
	}

	public void setExportIndex(boolean isExportIndex) {
		this.isExportIndex = isExportIndex;
	}

	public void setExportSerial(boolean isExportSerial) {
		this.isExportSerial = isExportSerial;
	}

	public void setExportView(boolean isExportView) {
		this.isExportView = isExportView;
	}

	public void setExportTrigger(boolean isExportTrigger) {
		this.isExportTrigger = isExportTrigger;
	}

	public void setTableNameList(List<String> tableNameList) {
		this.tableNameList = tableNameList;
	}

	public void setColumnNameList(String tableName, List<String> columnList) {
		this.columnNameListMap.put(tableName, columnList);
	}

	public void setWhereCondition(String tableName, String whereCondition) {
		this.whereConditionMap.put(tableName, whereCondition);
	}

	public void setDataFileFolder(String folder) {
		this.dataFolderPath = folder;
	}

	public void setDataFilePath(String tableName, String path) {
		this.dataFilePath.put(tableName, path);
	}

	public void setSchemaFilePath(String path) {
		this.schemaFilePath = path;
	}

	public void setIndexFilePath(String path) {
		this.indexFilePath = path;
	}

	public void setTriggerFilePath(String path) {
		this.triggerFilePath = path;
	}

	public void setFileCharset(String charset) {
		this.fileCharset = charset;
	}

	public void setFirstRowAsColumnName(boolean isFirstRowAsColumnName) {
		this.isFirstRowAsColumnName = isFirstRowAsColumnName;
	}

	public void setNULLValueTranslation(String nullValueTranslation) {
		this.nullValueTranslation = nullValueTranslation;
	}

	public void setExportSerialStartValue(boolean isExportSerialStartValue) {
		this.isExportSerialValue = isExportSerialStartValue;
	}

	public int getExportFileType() {
		return exportFileType;
	}

	public void setExportFileType(int type) {
		this.exportFileType = type;
	}

	public int getExportType() {
		return exportType;
	}

	public boolean isExportSchema() {
		return isExportSchema;
	}

	public boolean isExportData() {
		return isExportData;
	}

	public boolean isExportIndex() {
		return isExportIndex;
	}

	public boolean isExportSerial() {
		return isExportSerial;
	}

	public boolean isExportTrigger() {
		return isExportTrigger;
	}

	public boolean isExportSerialValue() {
		return isExportSerialValue;
	}

	public boolean isExportView() {
		return isExportView;
	}

	public void setTotalCount(String tableName, long count) {
		recordCountMap.put(tableName, count);
	}

	public long getTotalCount(String tableName) {
		if (recordCountMap.containsKey(tableName)) {
			return recordCountMap.get(tableName);
		}
		return ExportConfig.COUNT_UNKNOW;
	}

	public String getColumnDelimeter() {
		return columnDelimeter;
	}

	public void setColumnDelimeter(String columnDelimeter) {
		this.columnDelimeter = columnDelimeter;
	}

	public String getRowDelimeter() {
		return rowDelimeter;
	}

	public void setRowDelimeter(String rowDelimeter) {
		this.rowDelimeter = rowDelimeter;
	}

	public List<String> getTableNameList() {
		return tableNameList;
	}

	public List<String> getColumnNameList(String tableName) {
		if (columnNameListMap.containsKey(tableName)) {
			return columnNameListMap.get(tableName);
		}
		return null;
	}

	public String getWhereCondition(String tableName) {
		return whereConditionMap.get(tableName);
	}

	public String getDataFileFolder() {
		return dataFolderPath;
	}

	public String getDataFilePath(String tableName) {
		return dataFilePath.get(tableName);
	}

	public String getSchemaFilePath() {
		return schemaFilePath;
	}

	public String getIndexFilePath() {
		return indexFilePath;
	}

	public String getTriggerFilePath() {
		return triggerFilePath;
	}

	public String getFileCharset() {
		return fileCharset;
	}

	public boolean isFirstRowAsColumnName() {
		return isFirstRowAsColumnName;
	}

	public String getNULLValueTranslation() {
		return nullValueTranslation;
	}

	public boolean isExportSerialStartValue() {
		return isExportSerialValue;
	}

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int count) {
		this.threadCount = count;
	}

	public String getSQL(String name) {
		return sqlMap.get(name);
	}

	public void setSQL(String name, String sql) {
		sqlMap.put(name, sql);
	}

	public List<String> getSQLNameList() {
		List<String> sqlNameList = new ArrayList<String>();
		for (String name : sqlMap.keySet()) {
			sqlNameList.add(name);
		}
		return sqlNameList;
	}

	public List<PstmtParameter> getParameterList(String name) {
		return pstmListMap.get(name);
	}

	public void setParameterList(String name, List<PstmtParameter> parameterList) {
		pstmListMap.put(name, parameterList);
	}

	public boolean isHistory() {
		return isHistory;
	}

	public void setHistory(boolean isHistory) {
		this.isHistory = isHistory;
	}

	public boolean isExportLob() {
		return isExportLob;
	}

	public void setExportLob(boolean isExportLob) {
		this.isExportLob = isExportLob;
	}

	public boolean isUsePagination() {
		return usePagination;
	}

	public void setUsePagination(boolean usePagination) {
		this.usePagination = usePagination;
	}

	public boolean isExportFromCache() {
		return exportFromCache;
	}

	public void setExportFromCache(boolean exportFromCache) {
		this.exportFromCache = exportFromCache;
	}

	public ResultSetDataCache getResultSetDataCache() {
		return resultSetDataCache;
	}

	public void setResultSetDataCache(ResultSetDataCache resultSetDataCache) {
		this.resultSetDataCache = resultSetDataCache;
	}

	public void removeTable(String tableName) {
		tableNameList.remove(tableName);
		columnNameListMap.remove(tableName);
		whereConditionMap.remove(tableName);
		recordCountMap.remove(tableName);
	}

	/**
	 * Clone a new object
	 */
	public ExportConfig clone() {
		ExportConfig newObj = null;
		try {
			newObj = (ExportConfig) super.clone();
		} catch (CloneNotSupportedException e) {
			LOGGER.debug("", e);
		}
		if (newObj == null) {
			return null;
		}

		List<String> newTableNameList = new ArrayList<String>();
		newTableNameList.addAll(tableNameList);
		newObj.setTableNameList(newTableNameList);

		for (String table : columnNameListMap.keySet()) {
			if (columnNameListMap.get(table) != null) {
				List<String> list = new ArrayList<String>();
				list.addAll(columnNameListMap.get(table));
				newObj.setColumnNameList(table, list);
			}
		}

		Map<String, String> newWhereConditionMap = new HashMap<String, String>();
		newWhereConditionMap.putAll(whereConditionMap);
		newObj.whereConditionMap = newWhereConditionMap;

		Map<String, String> newDataFilePath = new HashMap<String, String>();
		newDataFilePath.putAll(dataFilePath);
		newObj.dataFilePath = newDataFilePath;

		Map<String, Long> newRecordCountMa = new HashMap<String, Long>();
		newRecordCountMa.putAll(recordCountMap);
		newObj.recordCountMap = newRecordCountMa;

		Map<String, String> newSqlMap = new HashMap<String, String>();
		newSqlMap.putAll(sqlMap);
		newObj.sqlMap = newSqlMap;

		return newObj;
	}
}
