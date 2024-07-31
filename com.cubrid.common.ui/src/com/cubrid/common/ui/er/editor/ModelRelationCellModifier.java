/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.er.editor;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.er.Messages;
import com.cubrid.common.ui.er.logic.PhysicalLogicRelation;
import com.cubrid.common.ui.er.model.ERTableColumn;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;

/**
 * Modifier for physical and logical relation cell
 *
 * @author Yu Guojia
 * @version 1.0 - 2014-5-20 created by Yu Guojia
 */
public class ModelRelationCellModifier implements ICellModifier {

    private IPhysicalLogicalEditComposite editComposite;
    private PhysicalLogicRelation.MapType mapType;

    public ModelRelationCellModifier(
            IPhysicalLogicalEditComposite editComposite, PhysicalLogicRelation.MapType mapType) {
        this.editComposite = editComposite;
        this.mapType = mapType;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object, java.lang.String)
     */
    @Override
    public boolean canModify(Object element, String property) {
        if (Messages.tblcolumnPhysical.equals(property)
                || Messages.tblcolumnLogical.equals(property)) {
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
     */
    @Override
    public Object getValue(Object element, String property) {
        if (element == null) {
            return null;
        }
        Map.Entry<String, String> entry = (Map.Entry<String, String>) element;

        if (Messages.tblcolumnPhysical.equals(property)) {
            return entry.getKey();
        } else if (Messages.tblcolumnLogical.equals(property)) {
            return entry.getValue();
        }

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object, java.lang.String, java.lang.Object)
     */
    @Override
    public void modify(Object element, String property, Object value) {
        final TableItem item = (TableItem) element;
        if (item == null) {
            return;
        }

        Map.Entry<String, String> oldEntry = (Map.Entry<String, String>) item.getData();
        String newName = (String) value;
        if (mapType.equals(PhysicalLogicRelation.MapType.DATATYPE)) {
            newName = newName.trim();
            if (newName.toLowerCase().startsWith(DataType.getLowerEnumType())) {
                newName =
                        newName.replaceFirst(
                                DataType.getLowerEnumType(), DataType.getUpperEnumType());
            } else {
                newName = newName.toUpperCase(); // all logical and physical data type to upper case
            }
        }
        Map<String, String> map = editComposite.getMapData(mapType);

        if (StringUtil.isEqual(property, Messages.tblcolumnPhysical)) {
            if (StringUtil.isEqual(newName, oldEntry.getKey())
                    || !checkName(newName, property, false)) {
                return;
            }

            if (map.containsKey(newName)) {
                String err =
                        com.cubrid.common.ui.cubrid.table.Messages.errSameNameOnEditTableColumn;
                if (mapType.equals(PhysicalLogicRelation.MapType.DATATYPE)) {
                    err = Messages.errDuplicateDataType;
                }
                CommonUITool.openErrorBox(err);
                return;
            }

            Map<String, String> orderMap = new LinkedHashMap<String, String>(map.size() * 2);
            Iterator<String> it = map.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                String oldvalue = map.get(key);
                if (StringUtil.isEqual(key, oldEntry.getKey())) {
                    key = newName;
                }
                orderMap.put(key, oldvalue);
            }

            map.clear();
            map.putAll(orderMap);
        } else if (StringUtil.isEqual(property, Messages.tblcolumnLogical)) {
            if (StringUtil.isEqual(newName, oldEntry.getValue())
                    || !checkName(newName, property, true)) {
                return;
            }
            if (map.containsValue(newName)) {
                String err =
                        com.cubrid.common.ui.cubrid.table.Messages.errSameNameOnEditTableColumn;
                if (mapType.equals(PhysicalLogicRelation.MapType.DATATYPE)) {
                    err = Messages.errDuplicateDataType;
                }
                CommonUITool.openErrorBox(err);
                return;
            }

            Map<String, String> orderMap = new LinkedHashMap<String, String>(map.size() * 2);
            Iterator<String> it = map.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                String oldvalue = map.get(key);
                if (StringUtil.isEqual(oldvalue, oldEntry.getValue())) {
                    oldvalue = newName;
                }
                orderMap.put(key, oldvalue);
            }

            map.clear();
            map.putAll(orderMap);
        }

        editComposite.loadTableInput(mapType);
    }

    private boolean checkName(String name, String property, boolean isLogical) {

        if (StringUtil.isEmpty(name)) {
            CommonUITool.openErrorBox(Messages.errEmptyName);
            return false;
        }
        if (!mapType.equals(PhysicalLogicRelation.MapType.DATATYPE)
                && !isLogical
                && !ValidateUtil.isValidIdentifier(name)) {
            CommonUITool.openErrorBox(Messages.bind(Messages.errColumnName, name));
            return false;
        }
        if (mapType.equals(PhysicalLogicRelation.MapType.DATATYPE)
                && StringUtil.isEqual(property, Messages.tblcolumnPhysical)) {
            String showType = PhysicalLogicRelation.getShownDataTypeInERTable(name);
            String err = ERTableColumn.checkDataShowType(showType);
            if (StringUtil.isNotEmpty(err)) {
                CommonUITool.openErrorBox(err);
                return false;
            }
        }
        return true;
    }
}
