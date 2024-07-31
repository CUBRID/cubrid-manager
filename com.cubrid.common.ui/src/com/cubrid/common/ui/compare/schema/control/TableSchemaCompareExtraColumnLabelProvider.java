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
package com.cubrid.common.ui.compare.schema.control;

import com.cubrid.common.core.common.model.Constraint;
import com.cubrid.common.core.common.model.Constraint.ConstraintType;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.common.model.TableDetailInfo;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.compare.Messages;
import com.cubrid.common.ui.compare.schema.model.TableSchema;
import com.cubrid.common.ui.compare.schema.model.TableSchemaCompareModel;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;

/**
 * Table Schema Compare Extra Column Label Provider
 *
 * @author Ray Yin
 * @version 1.0 - 2012.10.18 created by Ray Yin
 */
public class TableSchemaCompareExtraColumnLabelProvider extends ColumnLabelProvider {
    @SuppressWarnings("unused")
    private static final Logger LOGGER =
            LogUtil.getLogger(TableSchemaCompareExtraColumnLabelProvider.class);

    public static int RECORDS_COUNT = 0;
    public static int ATTRIBUTES_COUNT = 1;
    public static int INDEX_COUNT = 2;
    public static int PK_STATUS = 3;

    private final Device device = Display.getCurrent();
    private final Color diffschema_color = new Color(device, 249, 222, 223);
    private final Color targetMiss_color = new Color(device, 255, 241, 198);
    private final Color sourceMiss_color = new Color(device, 229, 230, 249);

    private int type;
    private int focus = 0;
    //	private DatabaseInfo dbInfo;
    //	private Map<String, SchemaInfo> schemas;

    /**
     * The constructor
     *
     * @param type
     * @param db
     * @param focus
     */
    public TableSchemaCompareExtraColumnLabelProvider(int type, int focus) {
        this.type = type;
        this.focus = focus;
        //		this.dbInfo = db.getDatabaseInfo();
        //		this.schemas = schemas;
    }

    public Color getBackground(Object element) {
        TableSchemaCompareModel cm = (TableSchemaCompareModel) element;

        switch (cm.getCompareStatus()) {
            case TableSchemaCompareModel.SCHEMA_DIFF:
                return diffschema_color;
            case TableSchemaCompareModel.SCHEMA_TMISS:
                return targetMiss_color;
            case TableSchemaCompareModel.SCHEMA_SMISS:
                return sourceMiss_color;
        }

        return null;
    }

    public String getText(Object element) {
        TableSchemaCompareModel cm = (TableSchemaCompareModel) element;

        TableDetailInfo tableInfo = null;
        if (focus == 0) {
            if (cm.getCompareStatus() != TableSchemaCompareModel.SCHEMA_SMISS) {
                tableInfo = cm.getSourceTableDetailInfo();
            }
        } else if (focus == 1) {
            if (cm.getCompareStatus() != TableSchemaCompareModel.SCHEMA_TMISS) {
                tableInfo = cm.getTargetTableDetailInfo();

                if (tableInfo == null) {
                    TableSchema tableSchema = (TableSchema) cm.getRight();
                    SchemaInfo schemaInfo = cm.getTargetSchemas().get(tableSchema.getName());
                    int columnCount = schemaInfo.getAttributes().size();

                    int idxCount = 0;
                    int pk = 0;

                    for (Constraint constraint : schemaInfo.getConstraints()) {
                        if (constraint.getType().equals(ConstraintType.PRIMARYKEY.getText())) {
                            pk++;
                            continue;
                        } else {
                            idxCount++;
                        }
                    }
                    tableInfo = new TableDetailInfo();
                    tableInfo.setPkCount(pk);
                    tableInfo.setColumnsCount(columnCount);
                    tableInfo.setIndexCount(idxCount);
                }
            }
        }

        if (type == RECORDS_COUNT) {
            long tableRecordCount = 0;

            if (tableInfo != null) {
                tableRecordCount = tableInfo.getRecordsCount();
            }

            if (focus == 0) {
                cm.setSourceRecords(tableRecordCount);
            } else if (focus == 1) {
                cm.setTargetRecords(tableRecordCount);
            }

            if (tableRecordCount < 0) {
                return Messages.notCount;
            }

            return String.valueOf(tableRecordCount);
        } else if (type == ATTRIBUTES_COUNT) {
            int attributeCount = 0;

            if (tableInfo != null) {
                attributeCount = tableInfo.getColumnsCount();
            }

            return String.valueOf(attributeCount);
        } else if (type == INDEX_COUNT) {
            int indexCount = 0;

            if (tableInfo != null) {
                indexCount = tableInfo.getIndexCount();
            }

            return String.valueOf(indexCount);
        } else if (type == PK_STATUS) {
            int pkCount = 0;

            if (tableInfo != null) {
                pkCount = tableInfo.getPkCount();
            }

            if (pkCount > 0) {
                return "Y";
            }

            return "";
        }

        return "";
    }
}
