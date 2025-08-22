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
package com.cubrid.common.ui.cubrid.table;

import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.editor.TableEditorPart;
import com.cubrid.common.ui.er.dialog.EditVirtualTableDialog;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Table;

/**
 * Table Editor Adaptor for editor part and editor dialog
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-8-15 created by Yu Guojia
 */
public class TableEditorAdaptor {
    private TableEditorPart editorPart = null;
    private EditVirtualTableDialog editorDialog = null;
    private boolean isRealTableEditor = true;

    public TableEditorAdaptor(TableEditorPart editorPart) {
        this.editorPart = editorPart;
    }

    public TableEditorAdaptor(EditVirtualTableDialog editorDialog) {
        this.editorDialog = editorDialog;
        isRealTableEditor = false;
    }

    public EditVirtualTableDialog getDialog() {
        return editorDialog;
    }

    public boolean isLogicalViewModel() {
        if (editorDialog != null) {
            return editorDialog.isLogicalViewModel();
        }
        return false;
    }

    public void hideToolTip() {
        if (isRealTableEditor) {
            editorPart.hideToolTip();
        } else {
            editorDialog.hideToolTip();
        }
    }

    public void showToolTip(Rectangle rect, String title, String message) {
        if (isRealTableEditor) {
            editorPart.showToolTip(rect, title, message);
        } else {
            editorDialog.showToolTip(rect, title, message);
        }
    }

    public String getColumnProperty(int index) {
        if (isRealTableEditor) {
            return editorPart.getColumnProperty(index);
        } else {
            return editorDialog.getColumnProperty(index);
        }
    }

    public SchemaInfo getNewSchemaInfo() {
        if (isRealTableEditor) {
            return editorPart.getNewSchemaInfo();
        } else {
            return editorDialog.getNewSchemaInfo();
        }
    }

    public SchemaInfo getOldSchemaInfo() {
        if (isRealTableEditor) {
            return editorPart.getOldSchemaInfo();
        } else {
            return editorDialog.getOldSchemaInfo();
        }
    }

    public boolean isNewTableFlag() {
        if (isRealTableEditor) {
            return editorPart.isNewTableFlag();
        } else {
            return editorDialog.isNewTableFlag();
        }
    }

    public boolean isSupportChange() {
        if (isRealTableEditor) {
            return editorPart.isSupportChange();
        } else {
            return editorDialog.isSupportChange();
        }
    }

    public boolean isSupportTableComment() {
        if (isRealTableEditor) {
            return editorPart.isSupportTableComment();
        } else {
            return true;
        }
    }

    public boolean changeForEditElement(
            String attrName, DBAttribute editAttr, DBAttribute origAttr) {
        if (isRealTableEditor) {
            return editorPart.changeForEditAttribute(attrName, editAttr, origAttr);
        }
        return true;
    }

    /**
     * Remove temporary element by name
     *
     * @param name
     */
    public void removeElementByName(String name) {
        if (isRealTableEditor) {
            editorPart.removeTempDBAttributeByName(name);
        } else {
            editorDialog.removeTmpElementByName(name);
        }
    }

    /**
     * Add a log of adding an attribute to change list.
     *
     * @param newAttrName String
     * @param isClassAttribute boolean
     */
    public void addNewAttrLog(String newAttrName, boolean isClassAttribute) {
        if (isRealTableEditor) {
            editorPart.addNewAttrLog(newAttrName, isClassAttribute);
        }
    }

    public boolean makeChangeLogForIndex(
            String attrName, DBAttribute editAttr, DBAttribute origAttr) {
        if (isRealTableEditor) {
            return editorPart.makeChangeLogForIndex(attrName, editAttr, origAttr);
        } else {
            return editorDialog.makeChangeLogForIndex(attrName, editAttr, origAttr);
        }
    }

    /** @return the columnsTable */
    public Table getColumnsTable() {
        if (isRealTableEditor) {
            return editorPart.getColumnsTable();
        } else {
            return editorDialog.getColumnsTable();
        }
    }

    /**
     * Get collation index by name
     *
     * @param collationName
     * @return
     */
    public int getCollationIndex(String collationName) {
        if (isRealTableEditor) {
            return editorPart.getCollationIndex(collationName);
        } else {
            return editorDialog.getCollationIndex(collationName);
        }
    }

    public void addNewColumn() {
        if (isRealTableEditor) {
            editorPart.addNewColumn();
        } else {
            editorDialog.addNewColumn();
        }
    }

    /**
     * Get collation array
     *
     * @return
     */
    public String[] getCollationArray() {
        if (isRealTableEditor) {
            return editorPart.getCollationArray();
        } else {
            return editorDialog.getCollationArray();
        }
    }

    public void loadColumnData() {
        if (isRealTableEditor) {
            editorPart.loadColumnData();
        } else {
            editorDialog.loadColumnData();
        }
    }

    /**
     * Get physical and logical data type relationship in current table and global relationship.
     *
     * @return Map<String,String>
     */
    private Map<String, String> getDataTypeMap() {
        if (editorDialog != null) {
            return editorDialog.getErSchema().getPhysicalLogicRelation().getDataTypeMap();
        }
        return new HashMap<String, String>(0);
    }

    /**
     * Get logical data type relation with the physical data type in current table and global
     * relationship.
     *
     * @return String if donot contains. return null.
     */
    public String getLogicalDataType(String physicalType) {
        return getDataTypeMap().get(physicalType);
    }

    /**
     * Get physical data type relation with the logical data type in current table and global
     * relationship.
     *
     * @return String if donot contains. return null.
     */
    public String getPhysicalDataType(String logicalType) {
        Map<String, String> map = getDataTypeMap();
        if (!map.containsValue(logicalType)) {
            return null;
        }
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (StringUtil.isEqual(logicalType, map.get(key))) {
                return key;
            }
        }
        return null;
    }

    public String[] listDataTypes() {
        if (editorDialog != null) {
            return editorDialog.listDataTypes();
        }
        return new String[0];
    }

    /**
     * When new a column, the old column is null or "" name column
     *
     * @param oldName
     * @param newName
     */
    public void afterModifyColumn(String oldName, String newName) {
        if (!isRealTableEditor) {
            editorDialog.afterModifyColumn(oldName, newName);
        }
    }
}
