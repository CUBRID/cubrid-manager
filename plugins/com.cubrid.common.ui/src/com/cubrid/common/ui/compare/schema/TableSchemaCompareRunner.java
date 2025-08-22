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
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.ui.compare.Messages;
import com.cubrid.common.ui.compare.schema.control.TableSchemaCompareEditorInput;
import com.cubrid.common.ui.compare.schema.dialog.SchemaCompareDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.progress.OpenTablesDetailInfoPartProgress;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;

/**
 * Table Schema Compare Runner (Thread)
 *
 * @author Ray Yin
 * @version 1.0 - 2012.10.20 created by Ray Yin
 */
public class TableSchemaCompareRunner extends Thread {
    private static final Logger LOGGER = LogUtil.getLogger(TableSchemaCompareRunner.class);

    private SchemaCompareDialog dialog;
    private CubridDatabase sourceDb;
    private CubridDatabase targetDb;
    private TableSchemaCompareEditorInput input;
    private List<TableDetailInfo> sourceTableInfoList;

    /**
     * The constructor
     *
     * @param sourceDb
     * @param targetDb
     * @param sourceTableInfoList
     */
    public TableSchemaCompareRunner(
            SchemaCompareDialog dialog,
            CubridDatabase sourceDb,
            CubridDatabase targetDb,
            List<TableDetailInfo> sourceTableInfoList) {
        this.dialog = dialog;
        this.sourceDb = sourceDb;
        this.targetDb = targetDb;
        this.sourceTableInfoList = sourceTableInfoList;
    }

    public void setInput(TableSchemaCompareEditorInput input) {
        this.input = input;
    }

    public TableSchemaCompareEditorInput getInput() {
        return this.input;
    }

    public void run() {
        try {
            TableSchemaCompareEditorInput editorInput = compareTableSchema(sourceDb, targetDb);
            setInput(editorInput);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            CommonUITool.openErrorBox(
                    Display.getDefault().getActiveShell(), Messages.fetchSchemaErrorFromDB);
        }
    }

    /**
     * Create a schema compare editor input
     *
     * @param sourceDatabase
     * @param targetDatabase
     * @return
     */
    private TableSchemaCompareEditorInput compareTableSchema(
            CubridDatabase sourceDatabase, CubridDatabase targetDatabase) {
        List<TableDetailInfo> lTableInfoList;
        if (sourceTableInfoList != null) {
            lTableInfoList = sourceTableInfoList;
        } else {
            lTableInfoList = TableSchemaCompareUtil.getTableInfoList(sourceDatabase);
        }
        if (dialog.isCanceled()) {
            return null;
        }

        List<TableDetailInfo> rTableInfoList = getTableInfoList(targetDatabase);
        if (dialog.isCanceled()) {
            return null;
        }

        TableSchemaCompareEditorInput CompareInput =
                new TableSchemaCompareEditorInput(
                        sourceDatabase, targetDatabase, lTableInfoList, rTableInfoList);
        return CompareInput;
    }

    /**
     * Returns all tables detail of a database
     *
     * @param db
     * @return
     */
    private List<TableDetailInfo> getTableInfoList(
            CubridDatabase db) { // FIXME logic code move to core module
        OpenTablesDetailInfoPartProgress progress = new OpenTablesDetailInfoPartProgress(db);

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = JDBCConnectionManager.getConnection(db.getDatabaseInfo(), true);
            Map<String, TableDetailInfo> map = new HashMap<String, TableDetailInfo>();

            if (!progress.loadUserSchemaList(conn, map)) {
                return null;
            }

            Set<String> tableNameSet = map.keySet();
            List<TableDetailInfo> tableList = new ArrayList<TableDetailInfo>();

            if (tableNameSet != null) {
                List<String> tableNames = new ArrayList<String>();
                for (String tableName : tableNameSet) {
                    tableNames.add(tableName);
                }

                Collections.sort(tableNames);
                List<String> partitionClasses = new ArrayList<String>();

                for (String tableName : tableNames) {
                    TableDetailInfo info = map.get(tableName);

                    if (dialog.isCanceled()) {
                        return null;
                    }

                    if ("YES".equals(info.getPartitioned())) {
                        String sql =
                                "SELECT b.* FROM db_partition a, db_class b "
                                        + "WHERE a.class_name='"
                                        + tableName.toLowerCase(Locale.getDefault())
                                        + "' AND LOWER(b.class_name)=LOWER(a.partition_class_name)";
                        stmt = conn.createStatement();
                        rs = stmt.executeQuery(sql);
                        while (rs.next()) {
                            if (dialog.isCanceled()) {
                                return null;
                            }

                            String className = rs.getString("class_name");
                            partitionClasses.add(className);
                        }

                        QueryUtil.freeQuery(stmt, rs);
                    }

                    if ("CLASS".equals(info.getClassType())
                            && !partitionClasses.contains(tableName)) {
                        info.setRecordsCount(-1);
                        tableList.add(info);
                    }
                }
            }

            return tableList;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            QueryUtil.freeQuery(conn, stmt, rs);
        }

        return null;
    }
}
