/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search
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
import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.schemacomment.SchemaCommentHandler;
import com.cubrid.common.core.schemacomment.model.SchemaComment;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import com.cubrid.cubridmanager.core.cubrid.table.model.SuperClassUtil;
import java.sql.Connection;
import java.util.List;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.eclipse.core.runtime.IProgressMonitor;

/** @author CHOE JUNGYEON */
public class ExportTableDefinitionLayoutType1
        extends ExportTableDefinitionLayoutType { // FIXME move this logic to core module
    public ExportTableDefinitionLayoutType1(ExportTableDefinitionProgress progress) {
        super(progress);
    }

    /**
     * Generate table name sheet
     *
     * @param wwb
     * @param exportTableNames
     * @throws Exception
     */
    public void generateTableNamesSheet(WritableWorkbook wwb, List<String> exportTableNames)
            throws Exception {
        WritableSheet ws = wwb.createSheet(Messages.exportTableDefinitionCell1, 0);

        // Tables
        ws.addCell(new jxl.write.Label(0, 0, Messages.exportTableDefinitionCell2, boldCellStyle));
        ws.mergeCells(0, 0, 5, 0);

        // Project
        ws.addCell(new jxl.write.Label(0, 1, Messages.exportTableDefinitionCell3, boldCellStyle));
        ws.addCell(new jxl.write.Label(1, 1, "", normalCellStyle));
        // Date
        ws.addCell(new jxl.write.Label(2, 1, Messages.exportTableDefinitionCell4, boldCellStyle));
        ws.addCell(new jxl.write.Label(3, 1, dateString, normalCellStyle));
        // Author
        ws.addCell(new jxl.write.Label(4, 1, Messages.exportTableDefinitionCell5, boldCellStyle));
        ws.addCell(new jxl.write.Label(5, 1, "", normalCellStyle));
        // Table Name
        ws.addCell(new jxl.write.Label(0, 2, Messages.exportTableDefinitionCell6, boldCellStyle));
        // Table Description
        ws.addCell(new jxl.write.Label(1, 2, Messages.exportTableDefinitionCell27, boldCellStyle));
        // Description
        //		ws.addCell(new jxl.write.Label(2, 2, Messages.exportTableDefinitionCell8,
        // boldCellStyle));
        // Memo
        //		ws.addCell(new jxl.write.Label(5, 2, Messages.exportTableDefinitionCell9,
        // boldCellStyle));
        ws.mergeCells(1, 2, 5, 2);

        // table name data
        int rowIndex = 3;
        for (String tableName : exportTableNames) {
            String tableColumnText = "";
            if (getProgressObject().isInstalledMetaTable()) {
                SchemaComment tableComment =
                        SchemaCommentHandler.find(
                                getProgressObject().getSchemaCommentMap(), tableName, null);
                if (tableComment != null) {
                    tableColumnText =
                            tableComment.getDescription() == null
                                    ? ""
                                    : tableComment.getDescription();
                }
            }
            ws.addCell(new jxl.write.Label(0, rowIndex, tableName, normalLeftAlignCellStyle));
            ws.addCell(new jxl.write.Label(1, rowIndex, tableColumnText, normalLeftAlignCellStyle));
            ws.addCell(new jxl.write.Label(2, rowIndex, "", normalCellStyle));
            ws.addCell(new jxl.write.Label(5, rowIndex, "", normalCellStyle));
            ws.mergeCells(1, rowIndex, 5, rowIndex);
            rowIndex++;
        }

        ws.setRowView(0, 500);

        // column width
        ws.setColumnView(0, 25);
        ws.setColumnView(1, 28);
        ws.setColumnView(2, 15);
        ws.setColumnView(3, 18);
        ws.setColumnView(4, 15);
        ws.setColumnView(5, 20);
    }

    /**
     * generate table name sheet
     *
     * @param wwb
     * @param conn
     * @param exportSchemaInfoList
     * @param monitor
     * @throws Exception
     */
    public void generateTableDetailSheets(
            WritableWorkbook wwb,
            Connection conn,
            List<SchemaInfo> exportSchemaInfoList,
            IProgressMonitor monitor)
            throws Exception {
        int sheetIndex = 1;

        for (SchemaInfo schemaInfo : exportSchemaInfoList) {
            String tableName = schemaInfo.getUniqueName();
            monitor.subTask(
                    Messages.bind(Messages.exportTableDefinitionProgressTaskWriteTable, tableName));

            List<SchemaInfo> supers =
                    SuperClassUtil.getSuperClasses(
                            getProgressObject().getDatabase().getDatabaseInfo(), schemaInfo);
            WritableSheet ws = wwb.createSheet(tableName, sheetIndex++);
            int rowIndex = 0;

            // Title
            ws.addCell(
                    new jxl.write.Label(
                            0, rowIndex, Messages.exportTableDefinitionCell10, boldCellStyle));
            ws.mergeCells(0, 0, 7, 0);
            rowIndex++;

            // System name
            ws.addCell(
                    new jxl.write.Label(
                            0, rowIndex, Messages.exportTableDefinitionCell11, boldCellStyle));
            ws.addCell(new jxl.write.Label(1, rowIndex, "", normalCellStyle));
            // Date
            ws.addCell(
                    new jxl.write.Label(
                            2, rowIndex, Messages.exportTableDefinitionCell4, boldCellStyle));
            ws.addCell(new jxl.write.Label(3, rowIndex, dateString, normalCellStyle));
            // Author
            ws.addCell(
                    new jxl.write.Label(
                            5, rowIndex, Messages.exportTableDefinitionCell5, boldCellStyle));
            ws.addCell(new jxl.write.Label(7, rowIndex, "", normalCellStyle));

            ws.mergeCells(3, 1, 4, 1);
            ws.mergeCells(5, 1, 6, 1);
            rowIndex++;

            String tableColumnText = "";
            if (getProgressObject().isInstalledMetaTable()) {
                SchemaComment tableComment =
                        SchemaCommentHandler.find(
                                getProgressObject().getSchemaCommentMap(), tableName, null);
                if (tableComment != null) {
                    tableColumnText =
                            tableComment.getDescription() == null
                                    ? ""
                                    : tableComment.getDescription();
                }
            }

            // Table Name
            ws.addCell(
                    new jxl.write.Label(
                            0, rowIndex, Messages.exportTableDefinitionCell6, boldCellStyle));
            ws.addCell(new jxl.write.Label(1, rowIndex, tableName, normalLeftAlignCellStyle));
            ws.mergeCells(1, 2, 7, 2);
            rowIndex++;

            // Table Description
            ws.addCell(
                    new jxl.write.Label(
                            0, rowIndex, Messages.exportTableDefinitionCell27, boldCellStyle));
            ws.addCell(new jxl.write.Label(1, rowIndex, tableColumnText, normalLeftAlignCellStyle));
            ws.mergeCells(1, 3, 7, 3);
            rowIndex++;

            // Column ID
            ws.addCell(
                    new jxl.write.Label(
                            0, rowIndex, Messages.exportTableDefinitionCell12, boldCellStyle));
            // Data type
            ws.addCell(
                    new jxl.write.Label(
                            1, rowIndex, Messages.exportTableDefinitionCell14, boldCellStyle));
            // Size
            ws.addCell(
                    new jxl.write.Label(
                            2, rowIndex, Messages.exportTableDefinitionCell15, boldCellStyle));
            // Null
            ws.addCell(
                    new jxl.write.Label(
                            3, rowIndex, Messages.exportTableDefinitionCell16, boldCellStyle));
            // PK
            ws.addCell(
                    new jxl.write.Label(
                            4, rowIndex, Messages.exportTableDefinitionCell17, boldCellStyle));
            // FK
            ws.addCell(
                    new jxl.write.Label(
                            5, rowIndex, Messages.exportTableDefinitionCell18, boldCellStyle));
            // Default
            ws.addCell(
                    new jxl.write.Label(
                            6, rowIndex, Messages.exportTableDefinitionCell26, boldCellStyle));
            // Column description
            ws.addCell(
                    new jxl.write.Label(
                            7, rowIndex, Messages.exportTableDefinitionCell25, boldCellStyle));
            rowIndex++;

            // column info
            for (DBAttribute columnAtt : schemaInfo.getAttributes()) {
                String attrName = columnAtt.getName();
                String defaultValue = columnAtt.getDefault();
                String columnText = "";
                if (getProgressObject().isInstalledMetaTable()) {
                    SchemaComment columnComment =
                            SchemaCommentHandler.find(
                                    getProgressObject().getSchemaCommentMap(), tableName, attrName);
                    if (columnComment != null) {
                        columnText =
                                columnComment.getDescription() == null
                                        ? ""
                                        : columnComment.getDescription();
                    }
                }

                ws.addCell(new jxl.write.Label(0, rowIndex, attrName, normalLeftAlignCellStyle));
                String showType = DataType.getShownType((columnAtt.getType()));
                if (showType.indexOf("(") > -1 && showType.endsWith("")) {
                    showType = showType.substring(0, showType.indexOf("("));
                }

                ws.addCell(new jxl.write.Label(1, rowIndex, showType, normalLeftAlignCellStyle));

                int size = DataType.getSize(columnAtt.getType());
                int scale = DataType.getScale(columnAtt.getType());
                if (size < 0 && scale < 0) {
                    ws.addCell(new jxl.write.Label(2, rowIndex, "", normalRightAlignCellStyle));
                } else if (scale < 0) {
                    ws.addCell(new jxl.write.Number(2, rowIndex, size, normalRightAlignCellStyle));
                } else {
                    ws.addCell(
                            new jxl.write.Label(
                                    2,
                                    rowIndex,
                                    Integer.toString(size) + "," + Integer.toString(scale),
                                    normalRightAlignCellStyle));
                }

                // get nullable
                boolean isNULL = true;
                if (!columnAtt.isClassAttribute()) {
                    if (columnAtt.getInherit().equals(tableName)) {
                        Constraint pk = schemaInfo.getPK(supers);
                        if (null != pk && pk.getAttributes().contains(attrName)) {
                            isNULL = false;
                        }
                    } else {
                        List<Constraint> pkList = schemaInfo.getInheritPK(supers);
                        for (Constraint inheritPK : pkList) {
                            if (inheritPK.getAttributes().contains(attrName)) {
                                isNULL = false;
                            }
                        }
                    }
                }
                if (columnAtt.isNotNull()) {
                    isNULL = false;
                }
                ws.addCell(new jxl.write.Label(3, rowIndex, isNULL ? "Y" : "", normalCellStyle));

                // get pk
                boolean isPk = false;
                if (!columnAtt.isClassAttribute()) {
                    if (columnAtt.getInherit().equals(tableName)) {
                        Constraint pk = schemaInfo.getPK(supers);
                        if (null != pk && pk.getAttributes().contains(attrName)) {
                            isPk = true;
                        }
                    } else {
                        List<Constraint> pkList = schemaInfo.getInheritPK(supers);
                        for (Constraint inheritPK : pkList) {
                            if (inheritPK.getAttributes().contains(attrName)) {
                                isPk = true;
                            }
                        }
                    }
                }
                ws.addCell(new jxl.write.Label(4, rowIndex, isPk ? "Y" : "", normalCellStyle));

                // get fk
                boolean isFk = false;
                for (Constraint fk : schemaInfo.getFKConstraints()) {
                    for (String columns : fk.getAttributes()) {
                        if (columns.equals(attrName)) {
                            isFk = true;
                            break;
                        }
                    }
                }

                ws.addCell(new jxl.write.Label(5, rowIndex, isFk ? "Y" : "", normalCellStyle));
                ws.addCell(new jxl.write.Label(6, rowIndex, defaultValue, normalCellStyle));
                ws.addCell(new jxl.write.Label(7, rowIndex, columnText, normalLeftAlignCellStyle));
                rowIndex++;
            }

            // blank
            for (int i = 0; i < 8; i++) {
                ws.addCell(new jxl.write.Label(i, rowIndex, "", normalCellStyle));
            }
            rowIndex++;

            // index
            ws.addCell(
                    new jxl.write.Label(
                            0, rowIndex, Messages.exportTableDefinitionCell20, boldCellStyle));
            ws.mergeCells(0, rowIndex, 7, rowIndex);
            rowIndex++;

            // NO
            ws.addCell(
                    new jxl.write.Label(
                            0, rowIndex, Messages.exportTableDefinitionCell21, boldCellStyle));
            // Index name
            ws.addCell(
                    new jxl.write.Label(
                            1, rowIndex, Messages.exportTableDefinitionCell22, boldCellStyle));
            // Column ID
            ws.addCell(
                    new jxl.write.Label(
                            3, rowIndex, Messages.exportTableDefinitionCell13, boldCellStyle));
            // Order
            ws.addCell(
                    new jxl.write.Label(
                            5, rowIndex, Messages.exportTableDefinitionCell23, boldCellStyle));
            // Memo
            ws.addCell(
                    new jxl.write.Label(
                            6, rowIndex, Messages.exportTableDefinitionCell19, boldCellStyle));

            ws.mergeCells(1, rowIndex, 2, rowIndex);
            ws.mergeCells(3, rowIndex, 4, rowIndex);
            ws.mergeCells(6, rowIndex, 7, rowIndex);
            rowIndex++;

            List<Constraint> constraints = getProgressObject().getIndexList(schemaInfo);
            for (int i = 0; i < constraints.size(); i++) {
                Constraint constraint = constraints.get(i);
                int columnSize = constraint.getAttributes().size();
                int currentRowIndex = rowIndex; // mark current row index
                for (int j = 0; j < columnSize; j++) {
                    String columnName = constraint.getAttributes().get(j);
                    ws.addCell(new jxl.write.Number(0, rowIndex, i + 1, normalCellStyle));
                    ws.addCell(
                            new jxl.write.Label(
                                    1, rowIndex, constraint.getName(), normalLeftAlignCellStyle));
                    ws.addCell(
                            new jxl.write.Label(3, rowIndex, columnName, normalLeftAlignCellStyle));
                    ws.addCell(new jxl.write.Number(5, rowIndex, j + 1, normalCellStyle));
                    ws.addCell(new jxl.write.Label(6, rowIndex, "", normalCellStyle));

                    if (columnSize == 1) {
                        ws.mergeCells(1, rowIndex, 2, rowIndex);
                    }
                    ws.mergeCells(3, rowIndex, 4, rowIndex);
                    ws.mergeCells(6, rowIndex, 7, rowIndex);

                    rowIndex++;
                }

                // if multiple colulmn merge NO/Index Name CELL by vertical logic
                if (columnSize > 1) {
                    ws.mergeCells(0, currentRowIndex, 0, currentRowIndex + columnSize - 1);
                    ws.mergeCells(1, currentRowIndex, 2, currentRowIndex + columnSize - 1);
                }
            }

            // blank
            ws.addCell(new jxl.write.Label(0, rowIndex, "", normalCellStyle));
            ws.addCell(new jxl.write.Label(1, rowIndex, "", normalCellStyle));
            ws.addCell(new jxl.write.Label(3, rowIndex, "", normalCellStyle));
            ws.addCell(new jxl.write.Label(5, rowIndex, "", normalCellStyle));
            ws.addCell(new jxl.write.Label(6, rowIndex, "", normalCellStyle));
            ws.mergeCells(1, rowIndex, 2, rowIndex);
            ws.mergeCells(3, rowIndex, 4, rowIndex);
            ws.mergeCells(6, rowIndex, 7, rowIndex);
            rowIndex++;

            // DDL
            ws.addCell(
                    new jxl.write.Label(
                            0, rowIndex, Messages.exportTableDefinitionCell24, boldCellStyle));
            ws.mergeCells(0, rowIndex, 7, rowIndex);
            rowIndex++;
            String ddl = getProgressObject().getDDL(schemaInfo);
            ws.addCell(new jxl.write.Label(0, rowIndex, ddl, normalLeftAlignCellStyle));
            ws.mergeCells(0, rowIndex, 7, rowIndex);
            ws.setRowView(0, 500);
            int lineNumbner = ddl.split(StringUtil.NEWLINE).length;
            ws.setRowView(rowIndex, lineNumbner * 350);

            // column width
            ws.setColumnView(0, 18);
            ws.setColumnView(1, 20);
            ws.setColumnView(2, 13);
            ws.setColumnView(3, 9);
            ws.setColumnView(4, 9);
            ws.setColumnView(5, 9);
            ws.setColumnView(6, 10);
            ws.setColumnView(7, 29);
            monitor.worked(1);
        }
    }
}
