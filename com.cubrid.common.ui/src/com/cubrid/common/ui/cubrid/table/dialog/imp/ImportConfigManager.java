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
package com.cubrid.common.ui.cubrid.table.dialog.imp;

import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.cubrid.table.dialog.PstmtParameter;
import com.cubrid.common.ui.spi.persist.PersistUtils;
import com.cubrid.cubridmanager.core.common.xml.IXMLMemento;
import com.cubrid.cubridmanager.core.common.xml.XMLMemento;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The ImportConfigManager Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-8-15 created by Kevin.Wang
 */
public class ImportConfigManager {

    public static final String COM_CUBRID_IMPORT_SETTING = "com.cubrid.import.setting";

    private List<ImportConfig> importConfigList;
    private static ImportConfigManager instance;

    /** The constructor */
    private ImportConfigManager() {
        init();
    }

    /**
     * Return the only ImportConfigManager
     *
     * @return ImportConfigManager
     */
    public static ImportConfigManager getInstance() {
        synchronized (ImportConfigManager.class) {
            if (instance == null) {
                instance = new ImportConfigManager();
            }
        }
        return instance;
    }

    /** Initial the configuration manager */
    protected void init() {
        synchronized (this) {
            importConfigList = new ArrayList<ImportConfig>();
            loadImportConfigs();
        }
    }

    /** Load the added import configs from plug-in preference */
    protected void loadImportConfigs() {
        synchronized (this) {
            importConfigList.clear();

            IXMLMemento memento =
                    PersistUtils.getXMLMemento(CommonUIPlugin.PLUGIN_ID, COM_CUBRID_IMPORT_SETTING);

            IXMLMemento[] children = memento == null ? null : memento.getChildren("config");
            for (int i = 0; children != null && i < children.length; i++) {
                String name = children[i].getString("name");
                String id = children[i].getString("id");

                int importType = children[i].getInteger("importType");
                int errorHandle = children[i].getInteger("errorHandle");
                String fileCharset = children[i].getString("fileCharset");
                boolean isCreateTable = children[i].getBoolean("isCreateTable");
                int threadCount = children[i].getInteger("threadCount");
                int commitCount = children[i].getInteger("commitCount");
                String rowDelimiter = children[i].getString("rowDelimiter");
                String columnDelimiter = children[i].getString("columnDelimiter");
                boolean isHaMode = children[i].getBoolean("isHaMode");

                List<String> nullValues = new ArrayList<String>();
                IXMLMemento nullValuesMemento = children[i].getChild("nullValues");
                IXMLMemento[] nullValueArray = nullValuesMemento.getChildren("nullValue");
                for (IXMLMemento nullValueMemento : nullValueArray) {
                    if (nullValueMemento.getTextData() != null) {
                        nullValues.add(nullValueMemento.getTextData());
                    }
                }

                ImportConfig importConfig = new ImportConfig();
                importConfig.setName(name);
                importConfig.setId(id);
                importConfig.setType(importType);
                importConfig.setErrorHandle(errorHandle);
                importConfig.setFilesCharset(fileCharset);
                importConfig.setCreateTableAccordingData(isCreateTable);
                importConfig.setThreadCount(threadCount);
                importConfig.setCommitLine(commitCount);
                importConfig.setRowDelimiter(rowDelimiter);
                importConfig.setColumnDelimiter(columnDelimiter);
                importConfig.setNullValueList(nullValues);
                importConfig.setHistory(true);
                importConfig.setHaMode(isHaMode);

                IXMLMemento tableConfigsMemento = children[i].getChild("tableConfigs");
                IXMLMemento[] tableConfigArray = tableConfigsMemento.getChildren("tableConfig");
                for (IXMLMemento configMemento : tableConfigArray) {
                    String tableName = configMemento.getString("name");
                    String filePath = configMemento.getString("filePath");
                    String fileType = configMemento.getString("fileType");
                    String createDDL = configMemento.getString("createDDL");
                    String insertDML = configMemento.getString("insertDML");
                    Integer lineCount = configMemento.getInteger("lineCount");
                    boolean isFirstRowAsColumn = configMemento.getBoolean("isFirstRowAsColumn");

                    List<PstmtParameter> pstmList = new ArrayList<PstmtParameter>();
                    IXMLMemento[] pstmArray = configMemento.getChildren("pstm");
                    for (IXMLMemento pstmMemento : pstmArray) {
                        String paramName = pstmMemento.getString("paramName");
                        String paramType = pstmMemento.getString("paramType");
                        String paramValue = pstmMemento.getString("paramValue");
                        int paramIndex = pstmMemento.getInteger("paramIndex");
                        String fileColumnName = pstmMemento.getString("fileColumnName");
                        PstmtParameter pstm =
                                new PstmtParameter(paramName, paramIndex, paramType, paramValue);
                        pstm.setFileColumnName(fileColumnName);
                        pstmList.add(pstm);
                    }

                    TableConfig tableConfig = new TableConfig(tableName);
                    tableConfig.setFilePath(filePath);
                    tableConfig.setFileType(fileType);
                    tableConfig.setCreateDDL(createDDL);
                    tableConfig.setInsertDML(insertDML);
                    tableConfig.setFirstRowAsColumn(isFirstRowAsColumn);
                    tableConfig.setPstmList(pstmList);
                    if (lineCount != null) {
                        tableConfig.setLineCount(lineCount);
                    }
                    importConfig.addTableConfig(tableConfig);
                }

                importConfigList.add(importConfig);
            }
        }
    }

    /** Save the import configs to plug-in preference */
    public void saveConfigs() {
        synchronized (this) {
            XMLMemento memento = XMLMemento.createWriteRoot("databases");
            Iterator<ImportConfig> iterator = importConfigList.iterator();
            while (iterator.hasNext()) {
                ImportConfig model = (ImportConfig) iterator.next();
                model.setHistory(true);

                IXMLMemento configMemento = memento.createChild("config");
                configMemento.putString("name", model.getName());
                configMemento.putString("id", model.getId());
                configMemento.putInteger("importType", model.getImportType());
                configMemento.putInteger("errorHandle", model.getErrorHandle());
                configMemento.putString("fileCharset", model.getFilesCharset());
                configMemento.putBoolean("isCreateTable", model.isCreateTableAccordingData());
                configMemento.putInteger("threadCount", model.getThreadCount());
                configMemento.putInteger("commitCount", model.getCommitLine());
                configMemento.putString("rowDelimiter", model.getRowDelimiter());
                configMemento.putString("columnDelimiter", model.getColumnDelimiter());
                configMemento.putBoolean("isHaMode", model.isHaMode());

                IXMLMemento nullValuesMemento = configMemento.createChild("nullValues");
                for (String nullValue : model.getNullValueList()) {
                    IXMLMemento nullValueMemento = nullValuesMemento.createChild("nullValue");
                    nullValueMemento.putTextData(nullValue);
                }
                IXMLMemento tableConfigsMemento = configMemento.createChild("tableConfigs");
                for (TableConfig tableConfig : model.getSelectedMap().values()) {
                    IXMLMemento tableConfigMemento = tableConfigsMemento.createChild("tableConfig");

                    for (PstmtParameter pstm : tableConfig.getPstmList()) {
                        IXMLMemento pstmMemento = tableConfigMemento.createChild("pstm");
                        pstmMemento.putString("paramName", pstm.getParamName());
                        pstmMemento.putString("paramType", pstm.getDataType());
                        pstmMemento.putString("paramValue", pstm.getStringParamValue());
                        pstmMemento.putInteger("paramIndex", pstm.getParamIndex());
                        pstmMemento.putString("fileColumnName", pstm.getFileColumnName());
                    }

                    tableConfigMemento.putString("name", tableConfig.getName());
                    tableConfigMemento.putString("filePath", tableConfig.getFilePath());
                    tableConfigMemento.putString(
                            "fileType",
                            tableConfig.getFileType() == null ? "" : tableConfig.getFileType());
                    tableConfigMemento.putInteger("lineCount", tableConfig.getLineCount());
                    tableConfigMemento.putString(
                            "createDDL",
                            tableConfig.getCreateDDL() == null ? "" : tableConfig.getCreateDDL());
                    tableConfigMemento.putString(
                            "insertDML",
                            tableConfig.getInsertDML() == null ? "" : tableConfig.getInsertDML());
                    tableConfigMemento.putBoolean(
                            "isFirstRowAsColumn", tableConfig.isFirstRowAsColumn());
                }
            }
            PersistUtils.saveXMLMemento(
                    CommonUIPlugin.PLUGIN_ID, COM_CUBRID_IMPORT_SETTING, memento);
        }
    }

    /**
     * Add the import config
     *
     * @param model ImportConfigModel
     */
    public void addConfig(ImportConfig model) {
        synchronized (this) {
            if (model != null) {
                importConfigList.add(model);
                saveConfigs();
                loadImportConfigs();
            }
        }
    }

    /**
     * Remove the import config
     *
     * @param configName
     */
    public boolean removeConfig(String configName) {
        synchronized (this) {
            if (configName != null) {
                for (ImportConfig config : importConfigList) {
                    if (configName.equals(config.getName())) {
                        importConfigList.remove(config);
                        saveConfigs();
                        loadImportConfigs();
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * Get the import config by history name
     *
     * @param name String
     * @return the ImportConfig object
     */
    public ImportConfig getConfig(String name) {
        for (int i = 0; i < importConfigList.size(); i++) {
            ImportConfig model = importConfigList.get(i);
            if (model.getName().equals(name)) {
                return model;
            }
        }
        return null;
    }

    /**
     * Get all configs
     *
     * @return all ImportConfigModel objects
     */
    public List<ImportConfig> getAllConfigs() {
        return importConfigList;
    }
}
