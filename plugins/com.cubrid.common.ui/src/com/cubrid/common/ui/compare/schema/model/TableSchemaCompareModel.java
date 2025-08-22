/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: -
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. - Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. - Neither the name of the <ORGANIZATION> nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.cubrid.common.ui.compare.schema.model;

import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.common.model.TableDetailInfo;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DiffNode;

/**
 * Table Schema Compare Model
 *
 * @author Ray Yin
 * @version 1.0 - 2012.10.08 created by Ray Yin
 */
public class TableSchemaCompareModel extends DiffNode {
    protected ITypedElement left = null;
    protected ITypedElement right = null;
    protected List<TableSchemaCompareModel> tableSchemaCompareList =
            new ArrayList<TableSchemaCompareModel>();

    public static final int SCHEMA_EQUAL = 0;
    public static final int SCHEMA_DIFF = 2;
    public static final int SCHEMA_TMISS = 3;
    public static final int SCHEMA_SMISS = 4;
    public static final int RECORDS_DIFF = 1;
    public static final int DUPLICATE_NAME = 5;

    private String title = "";
    private int compareStatus;
    private CubridDatabase sourceDB;
    private CubridDatabase targetDB;
    private TableDetailInfo sourceTableDetailInfo;
    private TableDetailInfo targetTableDetailInfo;
    private long sourceRecords;
    private long targetRecords;
    private Map<String, SchemaInfo> sourceSchemas;
    private Map<String, SchemaInfo> targetSchemas;
    private Map<String, List<String>> duplicateNameMap;

    /**
     * The constructor
     *
     * @param left
     * @param right
     */
    public TableSchemaCompareModel(
            ITypedElement left,
            ITypedElement right,
            Map<String, SchemaInfo> leftSchemas,
            Map<String, SchemaInfo> rightSchema) {
        super(left, right);
        this.compareStatus = SCHEMA_EQUAL;
        this.sourceSchemas = leftSchemas;
        this.targetSchemas = rightSchema;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTableCompareList(List<TableSchemaCompareModel> tableList) {
        this.tableSchemaCompareList = tableList;
    }

    public List<TableSchemaCompareModel> getTableCompareList() {
        return tableSchemaCompareList;
    }

    /**
     * Set table schema compare status 0 SCHEMA_EQUAL: schema equal 1 SCHEMA_DIFF: schema difference
     * 2 SCHEMA_TMISS: schema is missed on the target database 3 SCHEMA_SMISS: schema is missed on
     * the source database
     *
     * @param compareStatus
     */
    public void setCompareStatus(int compareStatus) {
        this.compareStatus = compareStatus;
    }

    public int getCompareStatus() {
        return compareStatus;
    }

    public void setSourceDB(CubridDatabase sourceDB) {
        this.sourceDB = sourceDB;
    }

    public CubridDatabase getSourceDB() {
        return sourceDB;
    }

    public void setTargetDB(CubridDatabase targetDB) {
        this.targetDB = targetDB;
    }

    public CubridDatabase getTargetDB() {
        return targetDB;
    }

    public void setSourceTableDetailInfo(TableDetailInfo sourceTableDetailInfo) {
        this.sourceTableDetailInfo = sourceTableDetailInfo;
    }

    public TableDetailInfo getSourceTableDetailInfo() {
        return sourceTableDetailInfo;
    }

    public void setTargetTableDetailInfo(TableDetailInfo targetTableDetailInfo) {
        this.targetTableDetailInfo = targetTableDetailInfo;
    }

    public TableDetailInfo getTargetTableDetailInfo() {
        return targetTableDetailInfo;
    }

    public void setSourceRecords(long sourceRecords) {
        this.sourceRecords = sourceRecords;
    }

    public long getSourceRecords() {
        return sourceRecords;
    }

    public void setTargetRecords(long targetRecords) {
        this.targetRecords = targetRecords;
    }

    public long getTargetRecords() {
        return targetRecords;
    }

    public Map<String, SchemaInfo> getSourceSchemas() {
        return sourceSchemas;
    }

    public Map<String, SchemaInfo> getTargetSchemas() {
        return targetSchemas;
    }

    /** @param duplicateNameMap */
    public void setDuplicateNameMap(Map<String, List<String>> duplicateNameMap) {
        this.duplicateNameMap = duplicateNameMap;
    }

    /** @return the duplicateNameMap */
    public Map<String, List<String>> getDuplicateNameMap() {
        return duplicateNameMap;
    }
}
