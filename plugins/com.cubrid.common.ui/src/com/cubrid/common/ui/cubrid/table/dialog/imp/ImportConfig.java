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

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.preference.ImportPreferencePage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * The Import Config Model
 *
 * @author Kevin.Wang
 * @version 1.0 - Aug 2, 2012 created by Kevin.Wang
 */
public class ImportConfig implements Cloneable {
    public static final int DEFAULT_IMPORT_COMMIT_COUNT = 1000;
    public static final int MIN_IMPORT_COMMIT_COUNT = 1;
    public static final int MAX_IMPORT_COMMIT_COUNT = 1000000;
    public static final int IMPORT_COMMIT_STEP = 1000;

    public static final int DEFAULT_IMPORT_THREAD_COUNT = 1;
    public static final int MIN_IMPORT_THREAD_COUNT = 1;
    public static final int MAX_IMPORT_THREAD_COUNT = 10;

    public static final int IMPORT_FROM_SQL = 1;
    public static final int IMPORT_FROM_EXCEL = 2;
    public static final int IMPORT_FROM_TXT = 4;
    public static final int IMPORT_FROM_LOADDB = 8;

    public static final int ERROR_HANDLE_BREAK = 1;
    public static final int ERROR_HANDLE_IGNORE = 2;

    private String id;
    private String name;
    private int importType;
    private int errorHandle = ERROR_HANDLE_IGNORE;
    private String filesCharset = "UTF-8";
    private boolean isCreateTableAccordingData = false;
    private int threadCount = DEFAULT_IMPORT_THREAD_COUNT;
    private int commitLine = DEFAULT_IMPORT_COMMIT_COUNT;
    private String rowDelimiter = null;
    private String columnDelimiter = null;
    private List<String> nullValueList = new ArrayList<String>();
    private LinkedHashMap<String, TableConfig> selectedMap =
            new LinkedHashMap<String, TableConfig>();

    private String errorLogFolderPath;
    private boolean isHaMode = false;

    private boolean isCheckMap = false;
    private boolean isHistory = false;
    private boolean isImportCLobData = true;
    private boolean isImportBLobData = true;

    public ImportConfig() {
        for (String str : ImportPreferencePage.NULL_LIST) {
            nullValueList.add(str);
        }
    }

    public int getImportType() {
        return importType;
    }

    public void setType(int type) {
        this.importType = type;
    }

    public List<String> getNullValueList() {
        return nullValueList;
    }

    public void setNullValueList(List<String> nullValueList) {
        this.nullValueList = nullValueList;
    }

    public String getFilesCharset() {
        return filesCharset;
    }

    public void setFilesCharset(String filesCharset) {
        this.filesCharset = filesCharset;
    }

    public boolean isCreateTableAccordingData() {
        return isCreateTableAccordingData;
    }

    public void setCreateTableAccordingData(boolean isCreateTableAccordingData) {
        this.isCreateTableAccordingData = isCreateTableAccordingData;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public int getCommitLine() {
        return commitLine;
    }

    public void setCommitLine(int commitLine) {
        this.commitLine = commitLine;
    }

    public String getRowDelimiter() {
        return rowDelimiter;
    }

    public void setRowDelimiter(String rowDelimiter) {
        this.rowDelimiter = rowDelimiter;
    }

    public String getColumnDelimiter() {
        return columnDelimiter;
    }

    public void setColumnDelimiter(String columnDelimiter) {
        this.columnDelimiter = columnDelimiter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getErrorHandle() {
        return errorHandle;
    }

    public void setErrorHandle(int errorHandle) {
        this.errorHandle = errorHandle;
    }

    public LinkedHashMap<String, TableConfig> getSelectedMap() {
        return selectedMap;
    }

    public void setSelectedMap(LinkedHashMap<String, TableConfig> selectedMap) {
        this.selectedMap = selectedMap;
    }

    public void addTableConfig(TableConfig tableConfig) {
        if (tableConfig == null) {
            return;
        }
        selectedMap.put(tableConfig.getName(), tableConfig);
    }

    public void deleteTableConfig(TableConfig tableConfig) {
        if (tableConfig == null) {
            return;
        }
        if (selectedMap.containsKey(tableConfig.getName())) {
            selectedMap.remove(tableConfig.getName());
        }
    }

    public void deleteTableConfig(String tableName) {
        if (tableName == null) {
            return;
        }
        if (selectedMap.containsKey(tableName)) {
            selectedMap.remove(tableName);
        }
    }

    public TableConfig getTableConfig(String tableName) {
        return selectedMap.get(tableName);
    }

    public boolean isCheckMap() {
        return isCheckMap;
    }

    public void setCheckMap(boolean isCheckMap) {
        this.isCheckMap = isCheckMap;
    }

    public void setImportType(int importType) {
        this.importType = importType;
    }

    public boolean isHistory() {
        return isHistory;
    }

    public void setHistory(boolean isHistory) {
        this.isHistory = isHistory;
    }

    /** @return the errorFolderPath */
    public String getErrorLogFolderPath() {
        return errorLogFolderPath;
    }

    /** @param errorFolderPath the errorFolderPath to set */
    public void setErrorLogFolderPath(String errorLogFolderPath) {
        this.errorLogFolderPath = errorLogFolderPath;
    }

    public boolean isHaMode() {
        return isHaMode;
    }

    public void setHaMode(boolean isHaMode) {
        this.isHaMode = isHaMode;
    }

    public boolean isImportCLobData() {
        return isImportCLobData;
    }

    public void setImportCLobData(boolean isImportCLobData) {
        this.isImportCLobData = isImportCLobData;
    }

    public boolean isImportBLobData() {
        return isImportBLobData;
    }

    public void setImportBLobData(boolean isImportBLobData) {
        this.isImportBLobData = isImportBLobData;
    }

    public LinkedHashMap<String, TableConfig> getTableConfigByType(String type) {
        LinkedHashMap<String, TableConfig> map = new LinkedHashMap<String, TableConfig>();
        for (String key : selectedMap.keySet()) {
            if (StringUtil.isEqual(type, selectedMap.get(key).getFileType())) {
                map.put(key, selectedMap.get(key));
            }
        }
        return map;
    }

    /**
     * Clone a object
     *
     * @return ImportConfig
     */
    public ImportConfig clone() {
        ImportConfig importConfig = null;
        try {
            importConfig = (ImportConfig) super.clone();
        } catch (CloneNotSupportedException e) {
        }

        LinkedHashMap<String, TableConfig> selectedMap = new LinkedHashMap<String, TableConfig>();
        for (Entry<String, TableConfig> entry : importConfig.getSelectedMap().entrySet()) {
            selectedMap.put(entry.getKey(), entry.getValue().clone());
        }
        importConfig.setSelectedMap(selectedMap);

        List<String> nullList = new ArrayList<String>();
        for (String value : nullValueList) {
            nullList.add(value);
        }
        importConfig.setNullValueList(nullList);

        return importConfig;
    }
}
