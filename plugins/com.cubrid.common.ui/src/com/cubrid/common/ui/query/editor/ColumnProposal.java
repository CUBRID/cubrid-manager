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
package com.cubrid.common.ui.query.editor;

import com.cubrid.common.core.common.model.SchemaInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ColumnProposal Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-4-8 created by Kevin.Wang
 */
public class ColumnProposal { // Move to core module
    private List<String> tableNameList = new ArrayList<String>();
    private Map<String, SchemaInfo> schemaInfoMap = new HashMap<String, SchemaInfo>();
    private Map<String, List<ColumnProposalDetailInfo>> columnListMap =
            new HashMap<String, List<ColumnProposalDetailInfo>>();

    public List<String> getTableNames() {
        return tableNameList;
    }

    public void setTableNames(List<String> tableNames) {
        this.tableNameList = tableNames;
    }

    public SchemaInfo getSchemaInfos(String tableName) {
        return schemaInfoMap.get(tableName);
    }

    public Map<String, List<ColumnProposalDetailInfo>> getColumns() {
        return columnListMap;
    }

    public void setColumns(Map<String, List<ColumnProposalDetailInfo>> columns) {
        this.columnListMap = columns;

        schemaInfoMap = new HashMap<String, SchemaInfo>();
        for (String tableName : tableNameList) {
            List<ColumnProposalDetailInfo> infos = columns.get(tableName);
            ColumnProposalDetailInfo info = infos.get(0);
            schemaInfoMap.put(tableName, info.getSchemaInfo());
        }
    }

    /**
     * Remove schema info
     *
     * @param tableName
     */
    public void removeSchemaInfo(String tableName) {
        tableNameList.remove(tableName);
        schemaInfoMap.remove(tableName);
        columnListMap.remove(tableName);
    }

    /**
     * Add schema info
     *
     * @param tableName
     * @param schemaInfo
     * @param columnList
     */
    public void addSchemaInfo(
            String tableName, SchemaInfo schemaInfo, List<ColumnProposalDetailInfo> columnList) {
        tableNameList.add(tableName);
        schemaInfoMap.put(tableName, schemaInfo);
        columnListMap.put(tableName, columnList);
    }
}
