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
package com.cubrid.common.ui.cubrid.table.dialog.imp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The RowData Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-8-10 created by Kevin.Wang
 */
public class ImportRowData {

    private int status;
    private int rowIndex = 0;
    private String sql;
    private int workSize = 0; // sql file need

    public ImportRowData(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    private List<ImportColumnData> columnList = new ArrayList<ImportColumnData>();

    public List<ImportColumnData> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<ImportColumnData> columnList) {
        this.columnList = columnList;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public int getWorkSize() {
        return workSize;
    }

    public void setWorkSize(int workSize) {
        this.workSize = workSize;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public String toString() { // FIXME using ToStringBuilder
        StringBuilder sb = new StringBuilder();

        sb.append("RowNum:" + rowIndex);
        if (columnList.size() > 0) {
            sb.append("[");
            for (int i = 0; i < columnList.size(); i++) {
                sb.append(columnList.get(i).toString());
                if (i + 1 < columnList.size()) {
                    sb.append(", ");
                }
            }
            sb.append("]");
        }
        for (int i = 0; i < columnList.size(); i++) {
            sb.append(columnList.get(i).toString());
            if (i + 1 < columnList.size()) {
                sb.append(", ");
            }
        }

        if (sql != null) {
            sb.append("[").append(sql).append("]");
        }
        return sb.toString();
    }
}
