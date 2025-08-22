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

import com.cubrid.common.core.common.model.TableDetailInfo;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.compare.schema.model.TableSchemaCompareModel;
import com.cubrid.common.ui.compare.schema.model.TableSchemaModel;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.progress.OpenTablesDetailInfoPartProgress;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;

/**
 * Table Schema Compare Util
 *
 * @author Ray Yin
 * @version 1.0 - 2012.10.10 created by Ray Yin
 */
public class TableSchemaCompareUtil {
    private static final Logger LOGGER = LogUtil.getLogger(TableSchemaCompareUtil.class);

    /** The constructor */
    private TableSchemaCompareUtil() {}

    /**
     * Create a TableSchemaCompareModel for two database tables
     *
     * @param sourceDB
     * @param targetDB
     * @param sourceTableInfoList
     * @param targetTableInfoList
     * @return
     */
    public static TableSchemaCompareModel createTableSchemaCompareModel(
            CubridDatabase sourceDB,
            CubridDatabase targetDB,
            List<TableDetailInfo> sourceTableInfoList,
            List<TableDetailInfo> targetTableInfoList) {
        TableSchemaModel left = createTableSchemaModel(sourceTableInfoList);
        TableSchemaModel right = createTableSchemaModel(targetTableInfoList);

        TableSchemaComparator comparator = new TableSchemaComparator(sourceDB, targetDB);
        TableSchemaCompareModel root = comparator.compare(left, right);
        root.setSourceDB(sourceDB);
        root.setTargetDB(targetDB);

        return root;
    }

    public static TableSchemaModel createTableSchemaModel(List<TableDetailInfo> TableInfoList) {
        TableSchemaModel tableSchemaModel = new TableSchemaModel();

        for (TableDetailInfo tableInfo : TableInfoList) {
            tableSchemaModel.setTableSchemaMap(tableInfo.getTableName(), "");
            tableSchemaModel.setTableDetailInfoMap(tableInfo.getTableName(), tableInfo);
        }

        return tableSchemaModel;
    }

    /**
     * Returns all tables detail of a database
     *
     * @param db
     * @return
     */
    public static List<TableDetailInfo> getTableInfoList(
            CubridDatabase db) { // FIXME logic code move to core module
        if (db.isVirtual()) {
            return new ArrayList<TableDetailInfo>();
        }

        OpenTablesDetailInfoPartProgress progress = new OpenTablesDetailInfoPartProgress(db);

        List<TableDetailInfo> tableList = null;
        Connection conn = null;
        try {
            conn = JDBCConnectionManager.getConnection(db.getDatabaseInfo(), true);
            Map<String, TableDetailInfo> map = new HashMap<String, TableDetailInfo>();

            if (!progress.loadUserSchemaList(conn, map)) {
                return null;
            }

            tableList = new ArrayList<TableDetailInfo>();
            Set<String> tableNameSet = map.keySet();
            if (tableNameSet != null) {
                List<String> tableNames = new ArrayList<String>();
                for (String tableName : tableNameSet) {

                    tableNames.add(tableName);
                }

                Collections.sort(tableNames);

                for (String tableName : tableNames) {
                    TableDetailInfo info = map.get(tableName);
                    String classType = info.getClassType();

                    if (classType.equals("CLASS") || classType.equals("VCLASS")) {
                        info.setRecordsCount(-1);
                        tableList.add(info);
                    }
                }
            }
        } catch (Exception e) {
            tableList = null;
            LOGGER.error(e.getMessage(), e);
        }

        return tableList;
    }
}
