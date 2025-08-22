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
package com.cubrid.common.ui.er.action;

import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.er.Messages;
import com.cubrid.common.ui.er.dialog.AddColumnDialog;
import com.cubrid.common.ui.er.model.ERTable;
import com.cubrid.common.ui.er.model.ERTableColumn;
import com.cubrid.cubridmanager.core.cubrid.database.model.Collation;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

/**
 * Add table column action
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-8-12 created by Yu Guojia
 */
public class AddColumnAction extends AbstractSelectionAction {
    public static String ID = AddColumnAction.class.getName();
    public static String NAME = Messages.actionAddColumnName;

    public AddColumnAction(IWorkbenchPart part) {
        super(part);
        setLazyEnablementCalculation(false);
    }

    protected void init() {
        setText(NAME);
        setToolTipText(NAME);
        setId(ID);
        ImageDescriptor icon =
                CommonUIPlugin.getImageDescriptor("icons/action/table_record_insert.png");
        if (icon != null) {
            setImageDescriptor(icon);
            setEnabled(true);
        }
    }

    protected boolean calculateEnabled() {
        if (!super.calculateEnabled()) {
            return false;
        }
        return true;
    }

    public void run() {
        ERTable table = this.getERTable();
        List<String> names = new ArrayList<String>();
        List<ERTableColumn> columns = table.getColumns();
        for (ERTableColumn column : columns) {
            names.add(column.getName());
        }

        AddColumnDialog dlg =
                new AddColumnDialog(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        table.getName(),
                        true,
                        names,
                        getERSchema());
        int ret = dlg.open();
        if (ret == IDialogConstants.OK_ID) {
            boolean isPhysical = table.getERSchema().isPhysicModel();
            String newName = dlg.getNewColumnName();
            String type = dlg.getDataType();
            String realType = null;
            String enumeration = null;
            if (isPhysical) {
                realType = DataType.getRealType(type);
            } else {
                String upPhysicalShowType = type;
                realType = DataType.getRealType(upPhysicalShowType);
            }
            if (DataType.DATATYPE_ENUM.equals(type)) {
                realType = DataType.getUpperEnumType().toLowerCase();
                enumeration = "('" + DataType.ENUM_DAFAULT_VALUE + "')";
            }

            DBAttribute addAttribute =
                    new DBAttribute(
                            newName,
                            realType,
                            table.getName(),
                            false,
                            false,
                            false,
                            false,
                            null,
                            Collation.DEFAULT_COLLATION);
            addAttribute.setEnumeration(enumeration);
            ERTableColumn col = new ERTableColumn(table, addAttribute, false);
            if (DataType.DATATYPE_STRING.equals(type)) {
                col.setLogicalType(DataType.DATATYPE_STRING);
            }
            table.addColumnAndFire(col);
        }
    }

    public IStatus postTaskFinished(ITask task) {
        return null;
    }

    public void completeAll() {}
}
