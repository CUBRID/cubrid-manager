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
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.cubrid.table.TableEditorAdaptor;
import com.cubrid.common.ui.cubrid.table.editor.IAttributeColumn;
import com.cubrid.common.ui.er.model.ERTableColumn;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import com.cubrid.cubridmanager.core.cubrid.table.model.SuperClassUtil;
import java.util.List;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * Attribute Table Viewer Label Provider
 *
 * @author robin 2009-6-4
 */
public class ERAttributeLabelProvider implements ITableLabelProvider, ITableColorProvider {
    private static final Image PK_IMAGE = CommonUIPlugin.getImage("icons/primary_key.png");
    private static final Image CHECK_IMAGE = CommonUIPlugin.getImage("icons/checked.gif");
    private static final Image UNCHECK_IMAGE = CommonUIPlugin.getImage("icons/unchecked.gif");
    private static final Image DISABLED_CHECK_IMAGE =
            CommonUIPlugin.getImage("icons/disabled_checked.gif");
    private static final Image DISABLED_UNCHECK_IMAGE =
            CommonUIPlugin.getImage("icons/disabled_unchecked.gif");
    private static final Color DISABLED_COLOR = ResourceManager.getColor(215, 215, 215);
    private SchemaInfo schema;
    private List<SchemaInfo> supers;
    private DatabaseInfo database;
    private boolean editableMode = false;
    private boolean isPhysical;
    private TableEditorAdaptor editorAdaptor;

    public ERAttributeLabelProvider(
            DatabaseInfo database,
            SchemaInfo schema,
            boolean editableMode,
            TableEditorAdaptor tableEditor,
            boolean isPhysical) {
        this.schema = schema;
        this.database = database;
        this.editableMode = editableMode;
        this.supers = SuperClassUtil.getSuperClasses(database, schema);
        this.editorAdaptor = tableEditor;
        this.isPhysical = isPhysical;
    }

    public ERAttributeLabelProvider(
            DatabaseInfo database,
            SchemaInfo schema,
            TableEditorAdaptor tableEditor,
            boolean isPhysical) {
        this(database, schema, false, tableEditor, isPhysical);
    }

    public Image getColumnImage(Object element, int columnIndex) {
        if (element == null) {
            return null;
        }

        ERTableColumn erColumn = (ERTableColumn) element;
        DBAttribute dbAttribute = erColumn.getAttr();
        if (dbAttribute == null || dbAttribute.getInherit() == null || schema == null) {
            return null;
        }

        String property = editorAdaptor.getColumnProperty(columnIndex);
        if (StringUtil.isEqual(property, IAttributeColumn.COL_PK)) {
            String attrName = dbAttribute.getName();
            Constraint pk = schema.getPK(supers);
            if (null != pk && pk.getAttributes().contains(attrName)) {
                return PK_IMAGE;
            }
            return editableMode ? UNCHECK_IMAGE : null;
        } else if (StringUtil.isEqual(property, IAttributeColumn.COL_NOT_NULL)) {
            String attrName = dbAttribute.getName();
            Constraint pk = schema.getPK(supers);
            if (null != pk && pk.getAttributes().contains(attrName)) {
                return DISABLED_CHECK_IMAGE;
            }
            if (dbAttribute.isNotNull()) {
                return editableMode ? CHECK_IMAGE : DISABLED_CHECK_IMAGE;
            } else {
                return editableMode ? UNCHECK_IMAGE : null;
            }
        } else if (StringUtil.isEqual(property, IAttributeColumn.COL_UK)) {
            String attrName = dbAttribute.getName();
            Constraint pk = schema.getPK(supers);
            if (null != pk && pk.getAttributes().contains(attrName)) {
                return DISABLED_CHECK_IMAGE;
            }
            if (dbAttribute.isUnique() && schema.isAttributeUnique(dbAttribute, supers)) {
                return editableMode ? CHECK_IMAGE : DISABLED_CHECK_IMAGE;
            } else {
                return editableMode ? UNCHECK_IMAGE : DISABLED_UNCHECK_IMAGE;
            }
        } else if (StringUtil.isEqual(property, IAttributeColumn.COL_SHARED)) {
            if (dbAttribute.isShared()) {
                return editableMode ? CHECK_IMAGE : DISABLED_CHECK_IMAGE;
            } else {
                return editableMode ? UNCHECK_IMAGE : null;
            }
        }

        return null;
    }

    public String getColumnText(Object element, int columnIndex) {
        if (element == null) {
            return null;
        }
        ERTableColumn erColumn = (ERTableColumn) element;
        DBAttribute dbAttribute = erColumn.getAttr();
        if (dbAttribute == null || dbAttribute.getInherit() == null || schema == null) {
            return null;
        }

        String property = editorAdaptor.getColumnProperty(columnIndex);
        if (StringUtil.isEqual(property, IAttributeColumn.COL_FLAG)) {
            if (StringUtil.isEmpty(dbAttribute.getName())) {
                return "*";
            }
        } else if (StringUtil.isEqual(property, IAttributeColumn.COL_NAME)) {
            return erColumn.getName(isPhysical);
        } else if (StringUtil.isEqual(property, IAttributeColumn.COL_DATATYPE)) {
            if (isPhysical) {
                if (DataType.DATATYPE_ENUM.equalsIgnoreCase(dbAttribute.getType())) {
                    String type =
                            StringUtil.toUpper(dbAttribute.getType())
                                    + dbAttribute.getEnumeration();
                    return DataType.getShownType(type);
                } else {
                    return DataType.getShownType(dbAttribute.getType());
                }
            } else {
                return erColumn.getShowType(false).toUpperCase();
            }
        } else if (StringUtil.isEqual(property, IAttributeColumn.COL_DEFAULT)) {
            String defaultValue = dbAttribute.getDefault();
            if (defaultValue == null) {
                return DataType.NULL_EXPORT_FORMAT;
            }

            if (defaultValue.length() == 0 && DataType.isStringType(dbAttribute.getType())) {
                return "";
            }
            return defaultValue;
        } else if (StringUtil.isEqual(property, IAttributeColumn.COL_AUTO_INCREMENT)) {
            SerialInfo serial = dbAttribute.getAutoIncrement();
            if (serial == null) {
                return "";
            }
            return serial.getMinValue() + "," + serial.getIncrementValue();
        } else if (StringUtil.isEqual(property, IAttributeColumn.COL_MEMO)) {
            return dbAttribute.getDescription();
        } else if (StringUtil.isEqual(property, IAttributeColumn.COL_COLLATION)) {
            return dbAttribute.getCollation();
        }
        return null;
    }

    public void addListener(ILabelProviderListener listener) {}

    public void dispose() {}

    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    public void removeListener(ILabelProviderListener listener) {}

    public SchemaInfo getSchema() {
        return schema;
    }

    public void setSchema(SchemaInfo schema) {
        this.schema = schema;
        supers = SuperClassUtil.getSuperClasses(database, schema);
    }

    public void setDatabase(DatabaseInfo database) {
        this.database = database;
    }

    public Color getForeground(Object element, int columnIndex) {
        return null;
    }

    public Color getBackground(Object element, int columnIndex) {
        if (schema == null || element == null || !editableMode) {
            return null;
        }

        ERTableColumn erColumn = (ERTableColumn) element;
        DBAttribute attr = erColumn.getAttr();
        String property = editorAdaptor.getColumnProperty(columnIndex);
        if (StringUtil.isEqual(property, IAttributeColumn.COL_AUTO_INCREMENT)) {
            DBAttribute aiAttr = schema.getAutoIncrementColumn();
            if (aiAttr != null && aiAttr != attr) {
                return DISABLED_COLOR;
            } else if (!DataType.isIntegerType(attr.getType())) {
                return DISABLED_COLOR;
            }
        } else if (StringUtil.isEqual(property, IAttributeColumn.COL_MEMO)) {
            return editorAdaptor.isSupportTableComment() ? null : DISABLED_COLOR;
        }
        return null;
    }
}
