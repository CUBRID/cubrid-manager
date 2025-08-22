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
package com.cubrid.common.ui.er.editor;

import com.cubrid.common.core.common.model.Constraint;
import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.common.model.SerialInfo;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.cubrid.table.TableEditorAdaptor;
import com.cubrid.common.ui.cubrid.table.editor.IAttributeColumn;
import com.cubrid.common.ui.er.model.ERSchema;
import com.cubrid.common.ui.er.model.ERTable;
import com.cubrid.common.ui.er.model.ERTableColumn;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import java.util.List;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * Editing cell modifier for ERD physical and logical column data.
 *
 * @author Yu Guojia
 * @version 1.0 - 2014-7-11 created by Yu Guojia
 */
public class ERAttributeCellModifier implements ICellModifier {
    private TableEditorAdaptor editor;
    private boolean isPhysical;

    public ERAttributeCellModifier(TableEditorAdaptor editor, boolean isPhysical) {
        super();
        this.editor = editor;
        this.isPhysical = isPhysical;
    }

    public boolean canModify(
            Object element, String property) { // FIXME move this logic to core module
        ERTableColumn erColumn = (ERTableColumn) element;
        DBAttribute attr = erColumn.getAttr();
        if (!editor.isNewTableFlag()
                && !editor.isSupportChange()
                && editor.getOldSchemaInfo().getDBAttributeByName(attr.getName(), false) != null) {
            return false;
        }

        if (StringUtil.isEqual(property, IAttributeColumn.COL_AUTO_INCREMENT)) {
            DBAttribute aiAttr = editor.getNewSchemaInfo().getAutoIncrementColumn();
            if (aiAttr != null && aiAttr != attr) {
                return false;
            } else if (!DataType.isIntegerType(attr.getType())) {
                return false;
            } else if (attr != null && StringUtil.isNotEmpty(attr.getDefault())) {
                CommonUITool.openErrorBox(Messages.errCanNotSetAIOnDefault);
                return false;
            }
        } else if (StringUtil.isEqual(property, IAttributeColumn.COL_NOT_NULL)
                || StringUtil.isEqual(property, IAttributeColumn.COL_UK)) {
            Constraint pk = editor.getNewSchemaInfo().getPK();
            if (pk != null && pk.getAttributes().contains(attr.getName())) {
                return false;
            }
        } else if (StringUtil.isEqual(property, IAttributeColumn.COL_COLLATION)) {
            if (attr.isNew() && DataType.canUseCollation(attr.getType())) {
                return true;
            } else {
                return false;
            }
        } else if (StringUtil.isEqual(property, IAttributeColumn.COL_MEMO)) {
            return editor.isSupportTableComment();
        }

        return true;
    }

    public Object getValue(
            Object element, String property) { // FIXME move this logic to core module
        ERTableColumn erColumn = (ERTableColumn) element;
        DBAttribute attr = erColumn.getAttr();
        if (StringUtil.isEqual(property, IAttributeColumn.COL_PK)) {
            SchemaInfo schemaInfo = editor.getNewSchemaInfo();
            if (schemaInfo == null) {
                return false;
            }

            Constraint constraint = schemaInfo.getPK();
            if (constraint == null) {
                return false;
            }

            List<String> columns = constraint.getAttributes();
            if (columns == null || columns.size() == 0) {
                return false;
            }

            return columns.contains(attr.getName());
        } else if (StringUtil.isEqual(property, IAttributeColumn.COL_NAME)) {
            return erColumn.getName(isPhysical);
        } else if (StringUtil.isEqual(property, IAttributeColumn.COL_DATATYPE)) {

            if (isPhysical) {
                String dataType = attr.getType();
                if (dataType.trim().toLowerCase().startsWith("enum")) {
                    return DataType.getShownType(attr.getType()) + attr.getEnumeration();
                }
                return DataType.getShownType(attr.getType());
            } else {
                return erColumn.getShowType(false);
            }
        } else if (StringUtil.isEqual(property, IAttributeColumn.COL_DEFAULT)) {
            String defaultValue = attr.getDefault();
            if (defaultValue == null
                    || attr.getAutoIncrement() != null
                    || (StringUtil.isEmpty(defaultValue)
                            && !DataType.isStringType(attr.getType()))) {
                return DataType.NULL_EXPORT_FORMAT;
            } else {
                return defaultValue;
            }
        } else if (StringUtil.isEqual(property, IAttributeColumn.COL_AUTO_INCREMENT)) {
            DBAttribute aiAttr = editor.getNewSchemaInfo().getAutoIncrementColumn();
            if (aiAttr != null && aiAttr != attr) {
                CommonUITool.openErrorBox(Messages.errCanNotAddAutoincrementAlreadyExists);
                return "";
            }

            SerialInfo serial = attr.getAutoIncrement();
            if (serial == null) {
                return "";
            }

            String defaultValue = attr.getDefault();
            if (StringUtil.isNotEmpty(defaultValue)) {
                return "";
            }

            return serial.getTableAutoIncrementString();
        } else if (StringUtil.isEqual(property, IAttributeColumn.COL_NOT_NULL)) {
            return attr.isNotNull();
        } else if (StringUtil.isEqual(property, IAttributeColumn.COL_UK)) {
            return attr.isUnique();
        } else if (StringUtil.isEqual(property, IAttributeColumn.COL_SHARED)) {
            return attr.isShared();
        } else if (StringUtil.isEqual(property, IAttributeColumn.COL_COLLATION)) {
            if (DataType.canUseCollation(attr.getType())) {
                String collation = attr.getCollation();
                return editor.getCollationIndex(collation);
            }
            return "";
        } else if (StringUtil.isEqual(property, IAttributeColumn.COL_MEMO)) {
            return attr.getDescription();
        }

        return null;
    }

    public void modify(
            Object element, String property, Object value) { // FIXME move this logic to core module
        final TableItem item = (TableItem) element;
        if (item == null) {
            return;
        }

        ERTableColumn erColumn = (ERTableColumn) item.getData();
        ERTable table = editor.getDialog().getNewERTable();
        if (!StringUtil.isEmpty(erColumn.getName())) {
            erColumn = table.getColumn(erColumn.getName());
        }
        DBAttribute attr = erColumn.getAttr();
        String oldAttrName = attr.getName();
        DBAttribute oldAttribute = null;
        if (editor.getOldSchemaInfo() != null) {
            oldAttribute = editor.getOldSchemaInfo().getDBAttributeByName(oldAttrName, false);
        }
        ERTableColumn oldColumn = editor.getDialog().getOldERTable().getColumn(oldAttrName);

        if (StringUtil.isEqual(property, IAttributeColumn.COL_PK)) {
            SchemaInfo schemaInfo = editor.getNewSchemaInfo();
            if (schemaInfo == null) {
                return;
            }

            boolean on = ((Boolean) value).booleanValue();
            erColumn.setIsPrimaryKey(on);
            if (on) {
                Constraint constraint = schemaInfo.getPK();
                if (constraint == null) {
                    constraint =
                            new Constraint("pk", Constraint.ConstraintType.PRIMARYKEY.getText());
                    schemaInfo.addConstraint(constraint);
                }
                constraint.addAttribute(attr.getName());
            } else {
                Constraint constraint = schemaInfo.getPK();
                if (constraint == null) {
                    return;
                }

                List<String> columns = constraint.getAttributes();
                if (columns == null || columns.size() == 0) {
                    return;
                }

                boolean isContain = columns.remove(attr.getName());
                /*For bug TOOLS-3972 The collumn's setting in Edit Table Inconsistent with the setting in Set Primary Key*/
                if (isContain && columns.size() == 0) {
                    schemaInfo.removeConstraintByName(constraint.getName(), constraint.getType());
                }
                /*For bug TOOLS-3046 : deal with edit column*/
                if (oldAttribute != null && isContain) {
                    attr.setNotNull(false);
                }
            }
            editor.makeChangeLogForIndex(oldAttrName, attr, oldAttribute);
        } else if (StringUtil.isEqual(property, IAttributeColumn.COL_NAME)) {
            String newName = (String) value;
            SchemaInfo schemaInfo = editor.getNewSchemaInfo();
            if (schemaInfo == null) { // TODO Improve error message
                CommonUITool.openErrorBox(Messages.errEmptyNameOnEditTableColumn);
                return;
            }
            if (!StringUtil.isEmpty(newName)
                    && isPhysical
                    && !ValidateUtil.isValidIdentifier(newName)) {
                CommonUITool.openErrorBox(Messages.errColumnName);
                return;
            }

            List<ERTableColumn> columns = table.getColumns();
            for (ERTableColumn col : columns) {
                if (StringUtil.isEqualIgnoreCase(col.getName(isPhysical), newName)
                        && erColumn != col) {
                    CommonUITool.openErrorBox(Messages.errSameNameOnEditTableColumn);
                    return;
                }
            }

            if (!StringUtil.isEqualIgnoreCase(erColumn.getName(isPhysical), newName)) {
                if (isPhysical) {
                    replaceNewConstraintAttributeName(schemaInfo, attr.getName(), newName);
                }

                String oldName = erColumn.getName(isPhysical);
                erColumn.setName(newName, isPhysical);
                if (erColumn.isNew()) {
                    erColumn.setName(newName, !isPhysical);
                    for (ERTableColumn col : columns) {
                        if (StringUtil.isEqualIgnoreCase(col.getName(!isPhysical), newName)
                                && erColumn != col) {
                            CommonUITool.openErrorBox(Messages.errSameNameOnEditTableColumn);
                            erColumn.setName(oldName, isPhysical);
                            erColumn.setName(oldName, !isPhysical);
                            return;
                        }
                    }
                }

                if (!hasAddedToSchemaInfo(attr)) {
                    if (!StringUtil.isEmpty(newName)) {
                        if (!columns.contains(erColumn)) {
                            erColumn.getAttr().setInherit(editor.getNewSchemaInfo().getClassname());
                            table.addColumn(erColumn);
                            editor.removeElementByName(newName);
                        }
                        if (!StringUtil.isEmpty(oldAttrName)) {
                            editor.makeChangeLogForIndex(
                                    oldAttrName, erColumn.getAttr(), oldAttribute);
                        }
                    }
                } else {
                    editor.getDialog().changeForEditElement(oldAttrName, erColumn, oldColumn);
                }
            }
            ERTableColumn col = null;
            if (columns.size() > 0) {
                Table columnsTable = editor.getColumnsTable();
                col =
                        (ERTableColumn)
                                columnsTable.getItem(columnsTable.getItemCount() - 1).getData();
            }
            if (!StringUtil.isEmpty(newName)
                    && (col == null || !StringUtil.isEmpty(col.getName(isPhysical)))) {
                editor.addNewColumn();
            }
        } else if (StringUtil.isEqual(property, IAttributeColumn.COL_DATATYPE)) {
            String dataTypeRaw = (String) value;
            ERSchema schema = erColumn.getERSchema();
            if (isPhysical) {
                if (dataTypeRaw.equalsIgnoreCase(DataType.getUpperEnumType())) {
                    dataTypeRaw += "('" + DataType.ENUM_DAFAULT_VALUE + "')";
                }
                erColumn.setPhysicalDataType(DataType.reviseDataType(dataTypeRaw));
                if (!DataType.isIntegerType(attr.getType())) {
                    attr.setAutoIncrement(null);
                }
                if (!DataType.canUseCollation(attr.getType())) {
                    attr.setCollation("");
                }
                String physicalRealType = erColumn.getRealType();
                if (erColumn.getERSchema().hasPhysicalTypeInMap(physicalRealType)
                        || erColumn.isNew()) {
                    String logicalType = schema.convert2LogicalShowType(physicalRealType);
                    erColumn.setShowType(logicalType, false);
                }
            } else {
                if (!DataType.DATATYPE_STRING.equalsIgnoreCase(dataTypeRaw)) {
                    dataTypeRaw = DataType.reviseDataType(dataTypeRaw);
                }
                erColumn.setShowType(dataTypeRaw, false);
                if (erColumn.getERSchema().hasLogicalTypeInMap(dataTypeRaw) || erColumn.isNew()) {
                    String physicalType = schema.convert2UpPhysicalShowType(dataTypeRaw);
                    if (DataType.DATATYPE_STRING.equalsIgnoreCase(dataTypeRaw)) {
                        physicalType = DataType.reviseDataType(physicalType);
                    }
                    erColumn.setPhysicalDataType(physicalType);
                }
            }

            editor.getDialog().changeForEditElement(oldAttrName, erColumn, oldColumn);
        } else if (StringUtil.isEqual(property, IAttributeColumn.COL_DEFAULT)) {
            String defaultVal = (String) value;
            boolean isStringType = DataType.isStringType(attr.getType());
            boolean isEmpty = StringUtil.isEmpty(defaultVal);
            boolean isNull = false;
            if (defaultVal == null
                    || DataType.NULL_EXPORT_FORMAT.equals(defaultVal)
                    || DataType.VALUE_NULL.equals(defaultVal)
                    || (isEmpty && !isStringType)) {
                isNull = true;
            }

            if (isNull) {
                attr.setDefault(null);
            } else {
                if (attr.getAutoIncrement() != null) {
                    attr.setDefault(null);
                    CommonUITool.openErrorBox(Messages.errCanNotSetDefaultOnAI);
                    return;
                }
                boolean isConfirmReset =
                        "".equals(defaultVal)
                                && oldAttribute != null
                                && !"".equals(oldAttribute.getDefault());
                if (isConfirmReset) {
                    String confirmResetDef = Messages.confirmResetDef;
                    if (CommonUITool.openConfirmBox(confirmResetDef)) {
                        attr.setDefault(null);
                    } else {
                        attr.setDefault(defaultVal);
                    }
                } else {
                    attr.setDefault(defaultVal);
                }
            }
            editor.getDialog().changeForEditElement(oldAttrName, erColumn, oldColumn);
        } else if (StringUtil.isEqual(property, IAttributeColumn.COL_AUTO_INCREMENT)) {
            DBAttribute aiAttr = editor.getNewSchemaInfo().getAutoIncrementColumn();
            if (aiAttr != null && aiAttr != attr) {
                attr.setAutoIncrement(null);
                return;
            }

            String param = (String) value;
            if (StringUtil.isNotEmpty(param)) {
                if (!param.matches("\\s*[0-9]+\\s*,\\s*[0-9]+\\s*")) {
                    CommonUITool.openErrorBox(Messages.errInvalidAutoIncrForm);
                    return;
                }

                String defaultValue = attr.getDefault();
                if (StringUtil.isNotEmpty(defaultValue)) {
                    CommonUITool.openErrorBox(Messages.errCanNotSetAIOnDefault);
                    return;
                }

                String[] params = param.split(",");
                String startVal = params[0].trim();
                String incrVal = params[1].trim();

                SchemaInfo schemaInfo = editor.getNewSchemaInfo();
                SerialInfo serial = new SerialInfo();
                serial.setOwner(schemaInfo.getOwner());
                serial.setClassName(schemaInfo.getClassname());
                serial.setAttName(oldAttrName);
                serial.setCacheCount("1");
                serial.setCurrentValue(startVal);
                serial.setCyclic(false);
                serial.setIncrementValue(incrVal);
                serial.setMaxValue(String.valueOf(Integer.MAX_VALUE));
                serial.setMinValue(startVal);
                serial.setStartedValue(startVal);
                if (attr.getAutoIncrement() != null
                        && schemaInfo != null
                        && schemaInfo.getAutoIncrementColumn() != null
                        && schemaInfo.getAutoIncrementColumn().getAutoIncrement() != null) {
                    String oldAI = attr.getAutoIncrement().getTableAutoIncrementString();
                    String newAI = serial.getTableAutoIncrementString();
                    if (StringUtil.isEqual(oldAI, newAI)) {
                        return;
                    }
                }
                attr.setAutoIncrement(serial);
            } else {
                attr.setAutoIncrement(null);
            }
            editor.changeForEditElement(oldAttrName, attr, oldAttribute);
        } else if (StringUtil.isEqual(property, IAttributeColumn.COL_NOT_NULL)) {
            boolean on = ((Boolean) value).booleanValue();
            attr.setNotNull(on);
            editor.getDialog().changeForEditElement(oldAttrName, erColumn, oldColumn);
        } else if (StringUtil.isEqual(property, IAttributeColumn.COL_UK)) {
            boolean on = ((Boolean) value).booleanValue();
            if (on && attr.isShared()) {
                CommonUITool.openErrorBox(Messages.errCanNotUseUkAndSharedOnce);
                return;
            }
            attr.setUnique(on);
            editor.getDialog().changeForEditElement(oldAttrName, erColumn, oldColumn);
        } else if (StringUtil.isEqual(property, IAttributeColumn.COL_SHARED)) {
            boolean on = ((Boolean) value).booleanValue();
            String defaultValue = attr.getDefault();
            if (on && StringUtil.isEmpty(defaultValue)) {
                CommonUITool.openErrorBox(Messages.msgInputSharedValue);
                return;
            }
            if (on && attr.isUnique()) {
                CommonUITool.openErrorBox(Messages.errCanNotUseUkAndSharedOnce);
                return;
            }
            attr.setShared(on);
            editor.getDialog().changeForEditElement(oldAttrName, erColumn, oldColumn);
        } else if (StringUtil.isEqual(property, IAttributeColumn.COL_COLLATION)) {
            String orignCollation = attr.getCollation();
            Integer selection = StringUtil.intValue(value.toString(), 0);
            if (selection > -1) {
                String newCollation = editor.getCollationArray()[selection];
                if (!StringUtil.isEqualNotIgnoreNull(orignCollation, newCollation)) {
                    attr.setCollation(newCollation);
                }
            }
        } else if (StringUtil.isEqual(property, IAttributeColumn.COL_MEMO)) {
            attr.setDescription((String) value);
        }
        editor.loadColumnData();
        editor.afterModifyColumn(
                oldColumn == null ? null : oldColumn.getName(isPhysical),
                erColumn.getName(isPhysical));
    }

    /** Replace new constraint attribute name */
    private void replaceNewConstraintAttributeName(
            SchemaInfo schemaInfo,
            String oldAttr,
            String newAttr) { // FIXME move this logic to core module
        for (Constraint constraint : schemaInfo.getConstraints()) {
            constraint.replaceAttribute(oldAttr, newAttr);
            constraint.replaceClassAttribute(oldAttr, newAttr);
            /*Replace the rule*/
            if (!Constraint.ConstraintType.FOREIGNKEY.equals(constraint.getType())) {
                List<String> rulesList = constraint.getRules();
                for (int i = 0; i < rulesList.size(); i++) {
                    String rule = rulesList.get(i);
                    int index = rule.indexOf(" ");
                    String attrName = rule.substring(0, index);
                    String rulePart = rule.substring(index);
                    if (StringUtil.isEqualNotIgnoreNull(attrName, oldAttr)) {
                        rulesList.set(i, newAttr + rulePart);
                    }
                }
            }
        }
    }

    /**
     * Judge the attribute is add to schemaInfo
     *
     * @param attribute
     * @return
     */
    private boolean hasAddedToSchemaInfo(
            DBAttribute attribute) { // FIXME move this logic to core module
        SchemaInfo schemaInfo = editor.getNewSchemaInfo();
        for (DBAttribute attr : schemaInfo.getAttributes()) {
            if (attr.equals(attribute)) {
                return true;
            }
        }
        return false;
    }
}
