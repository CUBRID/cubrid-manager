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
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.er.model.ERTable;
import com.cubrid.common.ui.spi.util.CommonUITool;
import org.eclipse.gef.commands.Command;

/**
 * Command to change the name field
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-5-16 created by Yu Guojia
 */
public class ModifyTableNameCommand extends Command {
    private ERTable table;
    private String newName, oldName;

    @Override
    public void execute() {
        if (StringUtil.isEqual(newName, oldName)) {
            return;
        }
        boolean isPhysical = table.getERSchema().isPhysicModel();
        if (!StringUtil.isEqual(oldName, newName)
                && table.getERSchema().isContainsTable(newName, isPhysical)) {
            CommonUITool.openErrorBox(Messages.bind(Messages.errExistTable, newName));
            return;
        }
        table.modifyNameAndFire(newName, isPhysical);
        if (!isPhysical) { // set logical name to desc
            if (StringUtil.isEqual(newName, table.getName(true))) {
                table.setDescription("");
            } else {
                table.setDescription(newName);
            }
        }
    }

    @Override
    public boolean canExecute() {
        if (newName != null) {
            return true;
        } else {
            newName = oldName;
            return false;
        }
    }

    /**
     * Sets the new Column name
     *
     * @param string the new name
     */
    public void setName(String string) {
        this.newName = string;
    }

    /**
     * Sets the old Column name
     *
     * @param string the old name
     */
    public void setOldName(String string) {
        oldName = string;
    }

    /** @param erTable The table to set. */
    public void setTable(ERTable erTable) {
        this.table = erTable;
    }
}
