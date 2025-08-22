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

import com.cubrid.common.ui.cubrid.table.dialog.PstmtParameter;
import java.util.ArrayList;
import java.util.List;

/**
 * The TableConfig Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-8-15 created by Kevin.Wang
 */
public class TableConfig implements Cloneable {

    public static final String TYPE_SCHEMA = "Schema";
    public static final String TYPE_DATA = "Data";
    public static final String TYPE_INDEX = "Index";
    public static final int LINT_COUNT_UNKNOW = -1;
    private String name;
    private String filePath;
    private String fileType = TYPE_DATA;
    private String createDDL;
    private String insertDML;
    private boolean isFirstRowAsColumn;
    private List<PstmtParameter> pstmList = new ArrayList<PstmtParameter>();
    /*Not persist*/
    private int lineCount;
    private boolean isMapped = false;

    public TableConfig() {}

    public TableConfig(String tableName) {
        this.name = tableName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public List<PstmtParameter> getPstmList() {
        return pstmList;
    }

    public void setPstmList(List<PstmtParameter> pstmList) {
        this.pstmList = pstmList;
    }

    public String getCreateDDL() {
        return createDDL;
    }

    public void setCreateDDL(String createDDL) {
        this.createDDL = createDDL;
    }

    public String getInsertDML() {
        return insertDML;
    }

    public void setInsertDML(String insertDML) {
        this.insertDML = insertDML;
    }

    public int getLineCount() {
        return lineCount;
    }

    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
    }

    public boolean isFirstRowAsColumn() {
        return isFirstRowAsColumn;
    }

    public void setFirstRowAsColumn(boolean isFirstRowAsColumn) {
        this.isFirstRowAsColumn = isFirstRowAsColumn;
    }

    public boolean isMapped() {
        return isMapped;
    }

    public void setMapped(boolean isMapped) {
        this.isMapped = isMapped;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    /** Clone a object */
    public TableConfig clone() {
        TableConfig tableConfig = null;
        try {
            tableConfig = (TableConfig) super.clone();
        } catch (CloneNotSupportedException e) {
        }

        List<PstmtParameter> pstmList = new ArrayList<PstmtParameter>();
        for (PstmtParameter pstm : tableConfig.getPstmList()) {
            pstmList.add(pstm.clone());
        }
        tableConfig.setPstmList(pstmList);

        return tableConfig;
    }
}
