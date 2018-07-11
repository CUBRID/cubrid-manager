/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.cubrid.table.dialog.exp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.cubrid.table.progress.ExportConfig;
import com.cubrid.common.ui.spi.persist.PersistUtils;
import com.cubrid.cubridmanager.core.common.xml.IXMLMemento;
import com.cubrid.cubridmanager.core.common.xml.XMLMemento;

/**
 *
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-9-6 created by Kevin.Wang
 */
public class ExportConfigManager {

	public static final String COM_CUBRID_EXPORT_SETTING = "com.cubrid.export.setting";

	private final List<ExportConfig> exportConfigList = new ArrayList<ExportConfig>();;
	private static ExportConfigManager instance;

	/**
	 * The constructor
	 */
	private ExportConfigManager() {
		loadExportConfigs();
	}

	/**
	 * Return the only ExportConfigManager
	 *
	 * @return ExportConfigManager
	 */
	public static ExportConfigManager getInstance() {
		synchronized (ExportConfigManager.class) {
			if (instance == null) {
				instance = new ExportConfigManager();
			}
		}
		return instance;
	}

	/**
	 *
	 * Load the added export configs from plug-in preference
	 *
	 */
	protected void loadExportConfigs() {
		synchronized (this) {
			exportConfigList.clear();
			IXMLMemento memento = PersistUtils.getXMLMemento(CommonUIPlugin.PLUGIN_ID,
					COM_CUBRID_EXPORT_SETTING);

			IXMLMemento[] children = memento == null ? null : memento.getChildren("config");
			for (int i = 0; children != null && i < children.length; i++) {
				String historyName = children[i].getString("name");
				int exportType = children[i].getInteger("exportType");
				int fileType = children[i].getInteger("fileType");
				boolean isExportSchema = children[i].getBoolean("exportSchema");
				boolean isExportIndex = children[i].getBoolean("exportIndex");
				boolean isExportData = children[i].getBoolean("exportData");
				boolean isExportSerial = children[i].getBoolean("exportSerial");
				boolean isExportView = children[i].getBoolean("exportView");
				boolean isExportTrigger = children[i].getBoolean("exportTrigger");
				boolean isExportStartValue = children[i].getBoolean("exportStartValue");
				boolean isExportLobData = children[i].getBoolean("exportLob");
				String schemaFilePath = children[i].getString("schemaFile");
				String indexFilePath = children[i].getString("indexFile");
				String dataFilePath = children[i].getString("dataFile");
				String fileCharset = children[i].getString("fileCharset");
				int threadCount = children[i].getInteger("threadCount");
				boolean useFirstAsColumnBtn = children[i].getBoolean("useFirstAsColumn");
				String nullValue = children[i].getString("nullValue");
				String rowDelimiter = children[i].getString("rowDelimiter");
				String colDelimiter = children[i].getString("colDelimiter");

				Map<String, List<String>> exportTableColumnMap = new LinkedHashMap<String, List<String>>();
				Map<String, String> tableWhereConditionMap = new HashMap<String, String>();

				IXMLMemento[] tables = children[i].getChildren("table");
				List<String> tableList = new ArrayList<String>();
				for (int j = 0; tables != null && j < tables.length; j++) {
					String tableName = tables[j].getString("name");
					tableList.add(tableName);
					String whereCondition = tables[j].getString("where");
					if (whereCondition != null && whereCondition.trim().length() > 0) {
						tableWhereConditionMap.put(tableName, whereCondition);
					}
				}

				ExportConfig model = new ExportConfig();
				model.setHistory(true);
				model.setName(historyName);
				model.setExportType(exportType);
				model.setExportFileType(fileType);
				model.setExportData(isExportData);
				model.setExportIndex(isExportIndex);
				model.setExportSchema(isExportSchema);
				model.setExportSerial(isExportSerial);
				model.setExportView(isExportView);
				model.setExportTrigger(isExportTrigger);
				model.setExportSerialStartValue(isExportStartValue);
				model.setExportLob(isExportLobData);
				model.setSchemaFilePath(schemaFilePath);
				model.setIndexFilePath(indexFilePath);
				model.setDataFileFolder(dataFilePath);
				model.setFileCharset(fileCharset);
				model.setThreadCount(threadCount);

				model.setFirstRowAsColumnName(useFirstAsColumnBtn);
				model.setNULLValueTranslation(nullValue);
				model.setRowDelimeter(rowDelimiter);
				model.setColumnDelimeter(colDelimiter);
				model.setTableNameList(tableList);
				for (String tableName : tableList) {
					List<String> columnList = exportTableColumnMap.get(tableName);
					model.setColumnNameList(tableName, columnList);
					String whereCondition = tableWhereConditionMap.get(tableName);
					if (StringUtil.isNotEmpty(whereCondition)) {
						model.setWhereCondition(tableName, whereCondition);
					}
				}
				exportConfigList.add(model);
			}
		}
	}

	/**
	 *
	 * Save the export configs to plug-in preference
	 *
	 */
	public void saveConfigs() {
		synchronized (this) {
			XMLMemento memento = XMLMemento.createWriteRoot("databases");
			Iterator<ExportConfig> iterator = exportConfigList.iterator();
			while (iterator.hasNext()) {
				ExportConfig model = (ExportConfig) iterator.next();
				IXMLMemento configMemento = memento.createChild("config");
				configMemento.putString("name", model.getName());
				configMemento.putInteger("exportType", model.getExportType());
				configMemento.putInteger("fileType", model.getExportFileType());
				configMemento.putBoolean("exportData", model.isExportData());
				configMemento.putBoolean("exportIndex", model.isExportIndex());
				configMemento.putBoolean("exportSchema", model.isExportSchema());
				configMemento.putBoolean("exportSerial", model.isExportSerial());
				configMemento.putBoolean("exportView", model.isExportView());
				configMemento.putBoolean("exportTrigger", model.isExportTrigger());
				configMemento.putBoolean("exportStartValue", model.isExportSerialStartValue());
				configMemento.putBoolean("exportLob", model.isExportLob());
				configMemento.putString("schemaFile", model.getSchemaFilePath());
				configMemento.putString("indexFile", model.getIndexFilePath());
				configMemento.putString("dataFile", model.getDataFileFolder());
				configMemento.putString("fileCharset", model.getFileCharset());
				configMemento.putInteger("threadCount", model.getThreadCount());
				configMemento.putBoolean("useFirstAsColumn", model.isFirstRowAsColumnName());
				configMemento.putString("nullValue", model.getNULLValueTranslation());
				configMemento.putString("rowDelimiter", model.getRowDelimeter());
				configMemento.putString("colDelimiter", model.getColumnDelimeter());

				List<String> tableList = model.getTableNameList();
				for (String tableName : tableList) {
					IXMLMemento tableMemento = configMemento.createChild("table");
					tableMemento.putString("name", tableName);
					String whereCondition = model.getWhereCondition(tableName);
					if (StringUtil.isNotEmpty(whereCondition)) {
						tableMemento.putString("where", whereCondition);
					}
				}
			}
			PersistUtils.saveXMLMemento(CommonUIPlugin.PLUGIN_ID, COM_CUBRID_EXPORT_SETTING,
					memento);
		}
	}

	/**
	 *
	 * Add the export config
	 *
	 * @param model ExportConfigModel
	 */
	public void addConfig(ExportConfig model) {
		synchronized (this) {
			if (model != null) {
				model.setHistory(true);
				exportConfigList.add(model);
				saveConfigs();
			}
		}
	}

	/**
	 *
	 * Remove the export config
	 *
	 * @param database the DatabaseNode object
	 */
	public void removeConfig(ExportConfig model) {
		synchronized (this) {
			if (model != null) {
				exportConfigList.remove(model);
				saveConfigs();
			}
		}
	}

	/**
	 *
	 * Remove all configs
	 *
	 */
	public void removeAllConfigs() {
		synchronized (this) {
			exportConfigList.clear();
			saveConfigs();
		}
	}

	/**
	 *
	 * Get the export config by history name
	 *
	 * @param name String
	 * @return the ExportConfigModel object
	 */
	public ExportConfig getConfig(String name) {
		for (int i = 0; i < exportConfigList.size(); i++) {
			ExportConfig model = exportConfigList.get(i);
			if (model.getName().equals(name)) {
				return model;
			}
		}
		return null;
	}

	/**
	 *
	 * Get all configs
	 *
	 * @return all ExportConfigModel objects
	 */
	public List<ExportConfig> getAllConfigs() {
		return exportConfigList;
	}
}
