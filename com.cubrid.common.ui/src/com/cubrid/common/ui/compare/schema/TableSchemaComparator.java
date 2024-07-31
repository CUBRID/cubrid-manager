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
package com.cubrid.common.ui.compare.schema;

import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.common.model.TableDetailInfo;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.compare.schema.model.TableSchema;
import com.cubrid.common.ui.compare.schema.model.TableSchemaCompareModel;
import com.cubrid.common.ui.compare.schema.model.TableSchemaModel;
import com.cubrid.common.ui.cubrid.database.erwin.ERXmlDatabaseInfoMapper;
import com.cubrid.common.ui.cubrid.database.erwin.WrappedDatabaseInfo;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.SchemaDDL;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetAllSchemaTask;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

/**
 * Table Schema Comparator Algorithm
 *
 * @author Ray Yin
 * @version 1.0 - 2012.10.12 created by Ray Yin
 */
public class TableSchemaComparator {
    private static final Logger LOGGER = LogUtil.getLogger(TableSchemaComparator.class);
    public CubridDatabase sourceDB;
    public CubridDatabase targetDB;
    private Map<String, SchemaInfo> sourceClasses = null;
    private Map<String, SchemaInfo> targetClasses = null;
    private Map<String, List<String>> duplicateNameMap = new HashMap<String, List<String>>();

    /**
     * The constructor
     *
     * @param sourceDB
     * @param targetDB
     */
    public TableSchemaComparator(CubridDatabase sourceDB, CubridDatabase targetDB) {
        this.sourceDB = sourceDB;
        this.targetDB = targetDB;
        this.sourceClasses = new HashMap<String, SchemaInfo>();
        this.targetClasses = new HashMap<String, SchemaInfo>();
    }

    /**
     * Returns a list of table schema compare model
     *
     * @param left
     * @param right
     * @return TableSchemaCompareModel
     */
    public TableSchemaCompareModel compare(TableSchemaModel left, TableSchemaModel right) {
        TableSchemaCompareModel compareModel =
                new TableSchemaCompareModel(left, right, sourceClasses, targetClasses);
        compareModel.setTitle(left != null ? left.getName() : null);

        List<TableSchemaCompareModel> tableCompareModelList = compareDetail(left, right);
        if (tableCompareModelList == null) {
            tableCompareModelList = new ArrayList<TableSchemaCompareModel>();
        }
        compareModel.setTableCompareList(tableCompareModelList);
        compareModel.setDuplicateNameMap(duplicateNameMap);
        return compareModel;
    }

    private List<TableSchemaCompareModel> compareDetail(
            TableSchemaModel left, TableSchemaModel right) {
        List<TableSchemaCompareModel> diffs = new ArrayList<TableSchemaCompareModel>();
        diffs.addAll(
                compareTableSchema(
                        left.getTableSchemaMap(),
                        right.getTableSchemaMap(),
                        left.getTableDetailInfoMap(),
                        right.getTableDetailInfoMap()));

        return diffs;
    }

    /** Compare table schemas between source and target databases */
    private List<TableSchemaCompareModel> compareTableSchema(
            Map<String, TableSchema> leftTableSchema,
            Map<String, TableSchema> rightTableSchema,
            Map<String, TableDetailInfo> leftTableDetail,
            Map<String, TableDetailInfo> rightTableDetail) { // FIXME logic code move to core module
        List<TableSchemaCompareModel> models = new ArrayList<TableSchemaCompareModel>();

        /** Setup databases connections */
        DatabaseInfo sourceDBInfo = sourceDB.getDatabaseInfo();
        DatabaseInfo targetDBInfo = null;
        if (targetDB.isVirtual()) {
            targetDBInfo =
                    ERXmlDatabaseInfoMapper.getWrappedDatabaseInfo(targetDB.getDatabaseInfo());
        } else {
            targetDBInfo = targetDB.getDatabaseInfo();
        }
        SchemaDDL sourceSchemaDDL = new SchemaDDL(null, sourceDB.getDatabaseInfo());
        SchemaDDL targetSchemaDDL = new SchemaDDL(null, targetDB.getDatabaseInfo());

        // collect schemas info left db
        // collect sources
        GetAllSchemaTask task = new GetAllSchemaTask(sourceDB.getDatabaseInfo());
        task.execute();
        sourceClasses.clear();
        sourceClasses.putAll(task.getSchemas());

        // collect target
        if (!targetDB.isVirtual()) {
            task = new GetAllSchemaTask(targetDB.getDatabaseInfo());
            task.execute();
            targetClasses.clear();
            targetClasses.putAll(task.getSchemas());
        } else {
            WrappedDatabaseInfo info = (WrappedDatabaseInfo) targetDBInfo;
            targetClasses.putAll(info.getSchemaInfos());
        }

        int compareStatus = TableSchemaCompareModel.SCHEMA_EQUAL;

        /** Compare table schemas from left to right */
        Iterator<String> leftkeys = leftTableSchema.keySet().iterator();

        while (leftkeys.hasNext()) {
            compareStatus = TableSchemaCompareModel.SCHEMA_EQUAL;
            String key = (String) leftkeys.next();

            TableSchema lTableSchema = leftTableSchema.get(key);
            TableDetailInfo lTableDetail = leftTableDetail.get(key);

            List<String> names = findDuplication(rightTableSchema, key);
            TableSchema rTableSchema = null;
            TableDetailInfo rTableDetail = null;
            if (names != null) {
                if (names.size() == 1) {
                    rTableSchema = rightTableSchema.get(names.get(0));
                    rTableDetail = rightTableDetail.get(names.get(0));
                } else {
                    duplicateNameMap.put(key, names);
                    for (String tableName : names) {
                        rightTableSchema.remove(tableName);
                    }
                    leftkeys.remove();
                    continue;
                }
            }

            if (rTableSchema == null) {
                rTableSchema = new TableSchema(null, null);
                compareStatus = TableSchemaCompareModel.SCHEMA_TMISS;
            } else {
                String left = lTableSchema.getName();
                String right = rTableSchema.getName();
                if (valueEqual(left, right)) { // TODO refactoring
                    boolean compScheInfo =
                            compareSchemaInfo(
                                    sourceDBInfo,
                                    targetDBInfo,
                                    sourceSchemaDDL,
                                    targetSchemaDDL,
                                    lTableSchema,
                                    rTableSchema);
                    if (compScheInfo == false) {
                        compareStatus = TableSchemaCompareModel.SCHEMA_DIFF;
                    }
                }
            }
            leftkeys.remove();
            rightTableSchema.remove(rTableSchema.getName());
            TableSchemaCompareModel cm =
                    new TableSchemaCompareModel(
                            lTableSchema, rTableSchema, sourceClasses, targetClasses);
            cm.setCompareStatus(compareStatus);
            cm.setSourceTableDetailInfo(lTableDetail);
            cm.setTargetTableDetailInfo(rTableDetail);

            models.add(cm);
        }

        /** Compare schemas from right to left */
        Iterator<String> rightkeys = rightTableSchema.keySet().iterator();
        Map<String, TableSchemaCompareModel> tempCompareMap =
                new HashMap<String, TableSchemaCompareModel>();
        String RIGHT_PATTERN = "__RIGHT_PATTERN";
        while (rightkeys.hasNext()) {
            compareStatus = TableSchemaCompareModel.SCHEMA_EQUAL;
            String key = (String) rightkeys.next();

            TableSchema lTableSchema = leftTableSchema.get(key);
            TableDetailInfo lTableDetail = leftTableDetail.get(key);

            if (!duplicateNameMap.containsKey(key + RIGHT_PATTERN)) {
                duplicateNameMap.put(key + RIGHT_PATTERN, new ArrayList<String>());
            }
            duplicateNameMap.get(key + RIGHT_PATTERN).add(key);
            TableSchema rTableSchema = rightTableSchema.get(key);
            TableDetailInfo rTableDetail = rightTableDetail.get(key);

            if (lTableSchema == null) {
                lTableSchema = new TableSchema();
                compareStatus = TableSchemaCompareModel.SCHEMA_SMISS;
            }

            rightkeys.remove();
            leftTableSchema.remove(key);
            TableSchemaCompareModel cm =
                    new TableSchemaCompareModel(
                            lTableSchema, rTableSchema, sourceClasses, targetClasses);
            cm.setCompareStatus(compareStatus);
            cm.setSourceTableDetailInfo(lTableDetail);
            cm.setTargetTableDetailInfo(rTableDetail);
            tempCompareMap.put(key, cm);
        }
        for (List<String> listKey : duplicateNameMap.values()) {
            if (listKey.size() > 1) {
                for (String string : listKey) {
                    tempCompareMap.remove(string);
                }
            }
        }
        models.addAll(tempCompareMap.values());
        if (models.size() <= 0) {
            return new ArrayList<TableSchemaCompareModel>();
        }
        return models;
    }

    /**
     * TODO: how to write document Document why lazy initialization was used, if applicable The
     * purpose How it should and shouldn't be used
     *
     * @return the duplicateNameMap
     */
    public Map<String, List<String>> getDuplicateNameMap() { // FIXME
        return duplicateNameMap;
    }

    private boolean compareSchemaInfo(
            DatabaseInfo sourceDBInfo,
            DatabaseInfo targetDBInfo,
            SchemaDDL sourceSchemaDDL,
            SchemaDDL targetSchemaDDL,
            TableSchema lTableSchema,
            TableSchema rTableSchema) {
        String leftSchema = lTableSchema.getSchemaInfo();
        if (StringUtil.isEmpty(leftSchema)) {
            leftSchema = getTableSchema(sourceSchemaDDL, lTableSchema.getName(), true);
        }
        if (StringUtil.isEmpty(leftSchema)) {
            leftSchema = "";
        }

        String rightSchema = rTableSchema.getSchemaInfo();
        if (StringUtil.isEmpty(rightSchema)) {
            rightSchema = getTableSchema(targetSchemaDDL, rTableSchema.getName(), false);
        }
        if (StringUtil.isEmpty(rightSchema)) {
            rightSchema = "";
        }

        return valueEqual(leftSchema.toLowerCase(), rightSchema.toLowerCase());
    }

    private String getTableSchema(SchemaDDL schemaDDL, String tableName, boolean isLeftDb) {
        String tableSchemaInfo = null;
        SchemaInfo schemaInfo = null;

        try {
            if (isLeftDb) {
                schemaInfo = sourceClasses.get(tableName);
            } else {
                schemaInfo = targetClasses.get(tableName);
            }
            tableSchemaInfo = schemaDDL.getSchemaDDL(schemaInfo);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return tableSchemaInfo;
    }

    // TODO Need to check whether it can be replaced with StringUtil.isEqual().
    private boolean valueEqual(String str1, String str2) {
        if (str1 == str2 || str1 != null && str1.equals(str2)) return true;
        if (str1 == null && str2.trim().length() == 0) return true;
        if (str2 == null && str1.trim().length() == 0) return true;
        return false;
    }

    @SuppressWarnings("unchecked")
    private List<String> findDuplication(Object tableMap, String name) {
        if (!(tableMap instanceof Map)) {
            return null;
        }
        List<String> keyName = null;
        Map<Object, Object> keyMap = (Map<Object, Object>) tableMap;
        for (Object key : keyMap.keySet()) {
            if (key.toString().toLowerCase().equals(name.toLowerCase())) {
                if (keyName == null) {
                    keyName = new ArrayList<String>();
                }
                keyName.add(key.toString());
            }
        }

        return keyName;
    }
}
