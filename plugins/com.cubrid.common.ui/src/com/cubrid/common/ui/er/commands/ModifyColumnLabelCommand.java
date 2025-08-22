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
package com.cubrid.common.ui.er.commands;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.er.directedit.ColumnLabelHandler;
import com.cubrid.common.ui.er.model.ERTableColumn;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import org.eclipse.gef.commands.Command;

/**
 * Command to change the name and type text field
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-7-12 created by Yu Guojia
 */
public class ModifyColumnLabelCommand extends Command {
    private ERTableColumn source;
    private String newName, oldName;
    private String newType, oldType;
    private String changedLogicalShowType = null;
    private String changedPhysicalShowType = null;
    private ColumnLabelHandler colHandler;
    private boolean isPhysical;

    public ModifyColumnLabelCommand(ERTableColumn source) {
        this.source = source;
        colHandler = new ColumnLabelHandler(source);
        isPhysical = source.getTable().getERSchema().isPhysicModel();
        oldName = source.getName(isPhysical);
        oldType = source.getShowType(isPhysical);
    }

    @Override
    public void execute() {
        source.setShowTypeAndFire(newType, isPhysical);
        source.modifyNameAndFire(newName, isPhysical);
        if (!isPhysical) { // set logical name to desc
            if (StringUtil.isEqual(newName, source.getName(true))) {
                source.setDescription("");
            } else {
                source.setDescription(newName);
            }
        }
    }

    @Override
    public boolean canExecute() {
        if (newName != null && newType != null) {
            return true;
        } else {
            newName = oldName;
            newType = oldType;
            return false;
        }
    }

    /**
     * Sets the new Column name
     *
     * @param labelString the new name
     */
    public void changeNameType(String labelString) {
        if (labelString != null) {
            ERTableColumn newColumn = source.clone();
            String name = ERTableColumn.getName(labelString);
            String type = ERTableColumn.getType(labelString);
            newColumn.setName(name, isPhysical);
            newColumn.setShowType(type, isPhysical);

            if (isPhysical) {
                String upperShowType = DataType.getUpperShowType(type);
                String realType = DataType.getRealType(upperShowType);
                if (realType.startsWith(DataType.getUpperEnumType())) {
                    realType =
                            realType.replaceFirst(
                                    DataType.getUpperEnumType(), DataType.getLowerEnumType());
                }
                if (source.getERSchema().hasPhysicalTypeInMap(realType)) {
                    changedLogicalShowType = source.getERSchema().convert2LogicalShowType(realType);
                    newColumn.setShowType(changedLogicalShowType, !isPhysical);
                }
            } else if (source.getERSchema().hasLogicalTypeInMap(type)) {
                changedPhysicalShowType = source.getERSchema().convert2UpPhysicalShowType(type);
                newColumn.setPhysicalDataType(changedPhysicalShowType);
            }
            String err = source.getTable().checkColumnChange(source, newColumn);
            if (!StringUtil.isEmpty(err)) {
                CommonUITool.openErrorBox(err);
            } else {
                newName = name; // physical name
                newType = type; // physical data type
                colHandler.setLatestData(labelString);
            }
        }
        if (this.newType == null || newName == null) {
            this.newName = oldName;
            this.newType = oldType;
            changedLogicalShowType = null;
            changedPhysicalShowType = null;
        }
        if (StringUtil.isNotEmpty(changedLogicalShowType)) {
            source.setShowType(changedLogicalShowType, false);
        } else if (StringUtil.isNotEmpty(changedPhysicalShowType)) {
            source.setPhysicalDataType(changedPhysicalShowType);
        }
        execute();
    }
}
