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
import com.cubrid.common.ui.er.model.ERTable;
import com.cubrid.common.ui.er.model.ERTableColumn;
import com.cubrid.common.ui.spi.util.CommonUITool;
import org.eclipse.gef.commands.Command;

/**
 * Command to delete a column object
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-7-12 created by Yu Guojia
 */
public class DeleteColumnCommand extends Command {
    private ERTable erTable;
    private ERTableColumn column;
    private int index = -1;

    @Override
    public boolean canExecute() {
        if (erTable.getColumns().size() > 1) {
            return true;
        }
        return true;
    }

    @Override
    public void execute() {

        String err = erTable.checkColumnChange(column, null);
        if (!StringUtil.isEmpty(err)) {
            CommonUITool.openErrorBox(err);
            return;
        }
        primExecute();
    }

    public void check() {}

    /** Invokes the execution of this command. */
    protected void primExecute() {
        index = erTable.getColumns().indexOf(column);
        erTable.removeColumnAndFire(column);
    }

    @Override
    public void redo() {
        primExecute();
    }

    /**
     * Sets the Table parent to the column to be deleted
     *
     * @param table the parent
     */
    public void setTable(ERTable table) {
        erTable = table;
    }

    /**
     * Sets the parent to the passed Schema
     *
     * @param column the child
     */
    public void setColumn(ERTableColumn column) {
        this.column = column;
    }

    @Override
    public void undo() {
        erTable.addColumnAndFire(column, index);
    }
}
