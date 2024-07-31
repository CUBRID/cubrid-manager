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

import com.cubrid.common.core.common.model.Constraint;
import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.common.model.SerialInfo;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetAllSchemaTask;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

/**
 * Column proposal detail information
 *
 * @author pangqiren
 */
public class ColumnProposalDetailInfo { // move to core module
    private static final Logger LOGGER = LogUtil.getLogger(ColumnProposalDetailInfo.class);
    private SchemaInfo schemaInfo;
    private DBAttribute attributeInfo;

    /**
     * The constructor
     *
     * @param schemaInfo
     * @param attributeInfo
     */
    public ColumnProposalDetailInfo(SchemaInfo schemaInfo, DBAttribute attributeInfo) {
        this.schemaInfo = schemaInfo;
        this.attributeInfo = attributeInfo;
    }

    public SchemaInfo getSchemaInfo() {
        return schemaInfo;
    }

    //	public void setSchemaInfo(SchemaInfo schemaInfo) {
    //		this.schemaInfo = schemaInfo;
    //	}

    public DBAttribute getAttributeInfo() {
        return attributeInfo;
    }
    //
    //	public void setAttributeInfo(DBAttribute attributeInfo) {
    //		this.attributeInfo = attributeInfo;
    //	}

    /**
     * Get the column name
     *
     * @return String
     */
    public String getColumnName() {
        return attributeInfo.getName();
    }

    /**
     * Get column proposal additional information
     *
     * @return String
     */
    public String getColumnAdditionalInfo() {
        StringBuilder additionalInfoBf = new StringBuilder();

        additionalInfoBf
                .append(Messages.quickColInfoColumnName)
                .append(": ")
                .append(attributeInfo.getName())
                .append(StringUtil.NEWLINE);

        additionalInfoBf
                .append(Messages.quickColInfoTableName)
                .append(": ")
                .append(schemaInfo.getClassname())
                .append(StringUtil.NEWLINE);

        //		boolean isClassAttr = attributeInfo.isClassAttribute();
        //		additionalInfoBf.append(
        //				com.cubrid.common.ui.cubrid.table.Messages.grpColumnType).append(
        //				": ").append(isClassAttr ? "CLASS" : "INSTANCE").append(
        //				StringUtil.NEWLINE);

        String dataType = attributeInfo.getType();
        additionalInfoBf
                .append(Messages.quickColInfoDataType)
                .append(": ")
                .append(dataType)
                .append(StringUtil.NEWLINE);

        boolean isNotNull = attributeInfo.isNotNull();
        additionalInfoBf
                .append(Messages.quickColInfoNotNull)
                .append(": ")
                .append(isNotNull ? "Y" : "N")
                .append(StringUtil.NEWLINE);

        boolean isUnique = attributeInfo.isUnique();
        additionalInfoBf
                .append(Messages.quickColInfoUnique)
                .append(": ")
                .append(isUnique ? "Y" : "N")
                .append(StringUtil.NEWLINE);

        String defaultValue = attributeInfo.getDefault();
        if (defaultValue != null && defaultValue.length() > 0) {
            additionalInfoBf
                    .append(Messages.quickColInfoDefaultValue)
                    .append(": ")
                    .append(defaultValue)
                    .append(StringUtil.NEWLINE);
        }

        SerialInfo autoInc = attributeInfo.getAutoIncrement();
        if (autoInc != null) {
            String currValue = autoInc.getCurrentValue();
            String increValue = autoInc.getIncrementValue();
            additionalInfoBf
                    .append(Messages.quickColInfoAutoIncr)
                    .append(": (")
                    .append(currValue)
                    .append(",")
                    .append(increValue)
                    .append(")")
                    .append(StringUtil.NEWLINE);
        }

        List<Constraint> constraintList = schemaInfo.getConstraints();
        boolean isPrimaryKey = false;
        String foreignTable = null;
        String indexType = null;
        for (Constraint constraint : constraintList) {
            if (constraint.getAttributes().contains(attributeInfo.getName())
                    || constraint.getClassAttributes().contains(attributeInfo.getName())) {
                String type = constraint.getType();
                if (Constraint.ConstraintType.PRIMARYKEY.getText().equals(type)) {
                    isPrimaryKey = true;
                } else if (Constraint.ConstraintType.FOREIGNKEY.getText().equals(type)) {
                    foreignTable = constraint.getReferencedTable();
                } else if (Constraint.ConstraintType.INDEX.getText().equals(type)
                        || Constraint.ConstraintType.UNIQUE.getText().equals(type)
                        || Constraint.ConstraintType.REVERSEINDEX.getText().equals(type)
                        || Constraint.ConstraintType.REVERSEUNIQUE.getText().equals(type)) {
                    indexType = type;
                }
                break;
            }
        }
        additionalInfoBf
                .append(Messages.quickColInfoPk)
                .append(": ")
                .append(isPrimaryKey ? "Y" : "N")
                .append(StringUtil.NEWLINE);
        if (foreignTable != null && foreignTable.trim().length() > 0) {
            additionalInfoBf
                    .append(Messages.lblFK)
                    .append(" REFERENCES ")
                    .append(foreignTable)
                    .append(StringUtil.NEWLINE);
        }
        if (indexType != null) {
            additionalInfoBf
                    .append(Messages.quickColInfoIndex)
                    .append(": ")
                    .append(indexType)
                    .append(StringUtil.NEWLINE);
        }

        return additionalInfoBf.toString();
    }

    /**
     * Fill in the table column information
     *
     * @param dbInfo DatabaseInfo
     * @param tableNames List<String>
     * @param columns Map<String, List<ColumnProposalDetailInfo>>
     */
    public static void fillInTableColumnInfo(
            DatabaseInfo dbInfo,
            List<String> tableNames,
            Map<String, List<ColumnProposalDetailInfo>> columns) {
        try {
            GetAllSchemaTask task = new GetAllSchemaTask(dbInfo);
            task.setNeedCollationInfo(false);

            task.execute();

            Map<String, SchemaInfo> schemas = task.getSchemas();

            List<String> fetchedTableNames = new ArrayList<String>();
            for (SchemaInfo schemaInfo : schemas.values()) {
                String tableName = schemaInfo.getUniqueName();
                fetchedTableNames.add(tableName);
            }

            Collections.sort(fetchedTableNames);

            for (String tableName : fetchedTableNames) {
                if (!tableNames.contains(tableName)) {
                    tableNames.add(tableName);
                }

                if (columns.containsKey(tableName)) {
                    continue;
                }

                SchemaInfo schemaInfo = schemas.get(tableName);
                if (schemaInfo == null) {
                    continue;
                }

                List<ColumnProposalDetailInfo> colInfoList =
                        new ArrayList<ColumnProposalDetailInfo>();
                columns.put(tableName, colInfoList);

                List<DBAttribute> dbClassAttrList = schemaInfo.getClassAttributes();
                for (DBAttribute attr : dbClassAttrList) {
                    ColumnProposalDetailInfo colInfo =
                            new ColumnProposalDetailInfo(schemaInfo, attr);
                    colInfoList.add(colInfo);
                }

                List<DBAttribute> attrList = schemaInfo.getAttributes();
                for (DBAttribute attr : attrList) {
                    ColumnProposalDetailInfo colInfo =
                            new ColumnProposalDetailInfo(schemaInfo, attr);
                    colInfoList.add(colInfo);
                }

                columns.put(schemaInfo.getUniqueName(), colInfoList);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
