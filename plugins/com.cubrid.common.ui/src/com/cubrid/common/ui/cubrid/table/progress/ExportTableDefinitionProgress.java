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
package com.cubrid.common.ui.cubrid.table.progress;

import com.cubrid.common.core.common.model.Constraint;
import com.cubrid.common.core.common.model.IDatabaseSpec;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.schemacomment.SchemaCommentHandler;
import com.cubrid.common.core.schemacomment.model.SchemaComment;
import com.cubrid.common.core.util.ConstantsUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.SchemaChangeManager;
import com.cubrid.cubridmanager.core.cubrid.table.model.SchemaDDL;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.WritableWorkbook;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * Pprogress to export table definition to excel
 *
 * @author fulei 2012-12-06
 */
public class ExportTableDefinitionProgress implements IRunnableWithProgress {
    private static final Logger LOGGER = LogUtil.getLogger(ExportTableDefinitionProgress.class);

    private List<String> exportTableList;
    private boolean exportAllTables;
    private boolean success = false;
    private final CubridDatabase database;
    private final String exlFullPath;
    private final String fileCharset;
    private final SchemaDDL schemaDDL;
    private boolean isInstalledMetaTable = false;
    private Map<String, SchemaComment> schemaCommentMap;
    private ExportTableDefinitionLayoutType tableDefinitionLayout;

    public ExportTableDefinitionProgress(
            CubridDatabase database,
            String exlFullPath,
            String fileCharset,
            boolean exportAllTables,
            List<String> exportTableList,
            int exportLayoutType) {
        this.database = database;
        this.exlFullPath = exlFullPath;
        this.fileCharset = fileCharset;
        this.exportAllTables = exportAllTables;
        this.exportTableList = exportTableList;
        schemaDDL =
                new SchemaDDL(
                        new SchemaChangeManager(database.getDatabaseInfo(), true),
                        database.getDatabaseInfo());
        if (exportLayoutType == 1) {
            tableDefinitionLayout = new ExportTableDefinitionLayoutType1(this);
        } else {
            tableDefinitionLayout = new ExportTableDefinitionLayoutType2(this);
        }
    }

    /**
     * createDatabaseWithProgress return database name
     *
     * @return Catalog
     */
    public boolean export() {
        Display display = Display.getDefault();
        display.syncExec(
                new Runnable() {
                    public void run() {
                        try {
                            new ProgressMonitorDialog(null)
                                    .run(true, false, ExportTableDefinitionProgress.this);
                        } catch (Exception e) {
                            LOGGER.error("", e);
                        }
                    }
                });

        return success;
    }

    public void run(IProgressMonitor monitor)
            throws InvocationTargetException,
                    InterruptedException { // FIXME move this logic to core module
        WritableWorkbook wwb = null;
        Connection conn = null;
        try {
            conn = JDBCConnectionManager.getConnection(database.getDatabaseInfo(), true);

            List<String> exportTableNames = getExportTables(conn);
            List<SchemaInfo> exportSchemaInfoList = getExportSchemaInfoList(conn, exportTableNames);
            loadSchemaCommentData(conn);

            monitor.beginTask(
                    Messages.exportTableDefinitionProgressTaskWrite, exportTableNames.size() + 1);
            WorkbookSettings workbookSettings = new WorkbookSettings();
            workbookSettings.setEncoding(fileCharset);
            wwb = Workbook.createWorkbook(new File(exlFullPath), workbookSettings);

            monitor.subTask(Messages.exportTableDefinitionProgressTaskTableList);
            tableDefinitionLayout.generateTableNamesSheet(wwb, exportTableNames);
            monitor.worked(1);

            tableDefinitionLayout.generateTableDetailSheets(
                    wwb, conn, exportSchemaInfoList, monitor);

            wwb.write();
            openSuccessDialog(Messages.exportTableDefinitionExportSuccess);
            success = true;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            openErrorDialog(e.getMessage());
        } finally {
            QueryUtil.freeQuery(conn);
            if (wwb != null) {
                try {
                    wwb.close();
                } catch (Exception ex) {
                    LOGGER.error("close excel stream error", ex);
                }
            }
        }
    }

    /**
     * get export Table names
     *
     * @param conn
     * @return
     */
    public List<String> getExportTables(Connection conn) { // FIXME move this logic to core module
        if (!exportAllTables) {
            return exportTableList;
        }

        ArrayList<String> tableList = new ArrayList<String>();
        Statement stmt = null;
        ResultSet rs = null;
        StringBuilder sql = new StringBuilder();
        if (database.getDatabaseInfo().isSupportUserSchema()) {
            sql.append("SELECT c.class_name, c.class_type, c.owner_name ");
            sql.append("FROM db_class c, db_attribute a ");
            sql.append("WHERE c.class_name=a.class_name AND c.is_system_class='NO' ");
            sql.append("AND c.owner_name=a.owner_name ");
            sql.append("AND a.from_class_name IS NULL AND c.class_type='CLASS' ");
            sql.append("GROUP BY c.owner_name, c.class_name, c.class_type ");
            sql.append("ORDER BY c.owner_name, c.class_type, c.class_name");
            String query = sql.toString();
        } else {
            sql.append("SELECT c.class_name, c.class_type ");
            sql.append("FROM db_class c, db_attribute a ");
            sql.append("WHERE c.class_name=a.class_name AND c.is_system_class='NO' ");
            sql.append("AND a.from_class_name IS NULL AND c.class_type='CLASS' ");
            sql.append("GROUP BY c.class_name, c.class_type ");
            sql.append("ORDER BY c.class_type, c.class_name");
        }
        String query = sql.toString();

        // [TOOLS-2425]Support shard broker
        if (CubridDatabase.hasValidDatabaseInfo(database)) {
            query = database.getDatabaseInfo().wrapShardQuery(query);
        }

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                String tableName = rs.getString(1);
                if (database.getDatabaseInfo().isSupportUserSchema()) {
                    String ownerName = rs.getString(3);
                    tableName = ownerName + "." + tableName;
                }
                if (ConstantsUtil.isExtensionalSystemTable(tableName)) {
                    continue;
                }
                tableList.add(tableName);
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            QueryUtil.freeQuery(stmt, rs);
        }

        return tableList;
    }

    /**
     * @param schemaInfo
     * @return list
     */
    public List<Constraint> getIndexList(
            SchemaInfo schemaInfo) { // FIXME move this logic to core module
        List<Constraint> list = new ArrayList<Constraint>();
        List<Constraint> constraints = schemaInfo.getConstraints();
        for (Constraint constraint : constraints) {
            if (constraint.getType().equals(Constraint.ConstraintType.INDEX.getText())
                    || constraint.getType().equals(Constraint.ConstraintType.UNIQUE.getText())
                    || constraint.getType().equals(Constraint.ConstraintType.REVERSEINDEX.getText())
                    || constraint
                            .getType()
                            .equals(Constraint.ConstraintType.REVERSEUNIQUE.getText())
                    || constraint
                            .getType()
                            .equals(Constraint.ConstraintType.PRIMARYKEY.getText())) {
                list.add(constraint);
            }
        }

        return list;
    }

    /**
     * get exprot schema info
     *
     * @param conn
     * @param tableNameList
     * @return List<SchemaInfo>
     */
    public List<SchemaInfo> getExportSchemaInfoList(
            Connection conn, List<String> tableNameList) { // FIXME move this logic to core module
        List<SchemaInfo> schemaInfoList = new ArrayList<SchemaInfo>();
        DatabaseInfo dbInfo = database.getDatabaseInfo();
        if (dbInfo == null) {
            return schemaInfoList;
        }

        for (String tableName : tableNameList) {
            SchemaInfo schemaInfo = dbInfo.getSchemaInfo(conn, tableName);
            if (schemaInfo == null) {
                continue;
            }
            schemaInfoList.add(schemaInfo);
        }

        return schemaInfoList;
    }

    /**
     * return ddl script
     *
     * @param schemaInfo
     * @return
     */
    public String getDDL(SchemaInfo schemaInfo) { // FIXME move this logic to core module
        StringBuilder ddlBuilder = new StringBuilder(schemaDDL.getSchemaDDL(schemaInfo, false));
        ddlBuilder.append(StringUtil.NEWLINE);

        try {
            String pkDDL = schemaDDL.getPKsDDL(schemaInfo);
            if (pkDDL != null && pkDDL.trim().length() > 0) {
                ddlBuilder.append(pkDDL).append(StringUtil.NEWLINE);
            }
        } catch (Exception e) {
            LOGGER.error("get index ddl error", e);
        }

        try {
            String indexDDL = schemaDDL.getIndexsDDL(schemaInfo);
            if (indexDDL != null && indexDDL.trim().length() > 0) {
                ddlBuilder.append(indexDDL).append(StringUtil.NEWLINE);
            }
        } catch (Exception e) {
            LOGGER.error("get index ddl error", e);
        }

        try {
            String fkDDL = schemaDDL.getFKsDDL(schemaInfo);
            if (fkDDL != null && fkDDL.trim().length() > 0) {
                ddlBuilder.append(fkDDL).append(StringUtil.NEWLINE);
            }
        } catch (Exception e) {
            LOGGER.error("get index ddl error", e);
        }

        return ddlBuilder.toString();
    }

    /**
     * load schema comment data(
     *
     * @param conn
     * @return
     */
    public void loadSchemaCommentData(Connection conn) { // FIXME move this logic to core module
        IDatabaseSpec dbSpec = database.getDatabaseInfo();
        isInstalledMetaTable = SchemaCommentHandler.isInstalledMetaTable(dbSpec, conn);
        if (isInstalledMetaTable) {
            try {
                schemaCommentMap =
                        SchemaCommentHandler.loadDescriptions(
                                dbSpec, conn, database.getDatabaseInfo().isSupportUserSchema());
            } catch (Exception e) {
                LOGGER.error("load schema comment error", e);
            }
        }
    }

    /**
     * show success message to users
     *
     * @param showMess String
     */
    private void openSuccessDialog(final String showMess) { // FIXME move to common ui module
        Display display = Display.getDefault();
        display.asyncExec(
                new Runnable() {
                    public void run() {
                        Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
                        CommonUITool.openInformationBox(
                                shell, com.cubrid.common.ui.spi.Messages.titleConfirm, showMess);
                    }
                });
    }

    /**
     * show error message to users
     *
     * @param showMess String
     */
    private void openErrorDialog(final String showMess) { // FIXME move to common ui module
        Display display = Display.getDefault();
        display.asyncExec(
                new Runnable() {
                    public void run() {
                        CommonUITool.openErrorBox(
                                PlatformUI.getWorkbench().getDisplay().getActiveShell(), showMess);
                    }
                });
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isInstalledMetaTable() {
        return isInstalledMetaTable;
    }

    public Map<String, SchemaComment> getSchemaCommentMap() {
        return schemaCommentMap;
    }

    public CubridDatabase getDatabase() {
        return database;
    }
}
