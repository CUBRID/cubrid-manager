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

import com.cubrid.common.ui.er.model.ERTable;
import com.cubrid.common.ui.er.model.ERTableColumn;
import org.eclipse.gef.commands.Command;

/**
 * Moves column to a different table
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-7-12 created by Yu Guojia
 */
public class TransferColumnCommand extends Command {
    private final ERTableColumn columnToMove;
    private final ERTable originalTable;
    private final ERTable newTable;
    private final int oldIndex, newIndex;

    public TransferColumnCommand(
            ERTableColumn columnToMove,
            ERTableColumn columnAfter,
            ERTable originalTable,
            ERTable newTable,
            int oldIndex,
            int newIndex) {
        super();
        this.columnToMove = columnToMove;
        this.originalTable = originalTable;
        this.newTable = newTable;
        this.oldIndex = oldIndex;
        this.newIndex = newIndex;
    }

    /** allows for transfer only if there are one or more columns in the same table. */
    public boolean canExecute() {
        if (originalTable.getColumns().size() > 1 && originalTable == newTable) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void execute() {
        originalTable.removeColumnAndFire(columnToMove);
        newTable.addColumnAndFire(columnToMove, newIndex);
    }

    @Override
    public void undo() {
        newTable.removeColumnAndFire(columnToMove);
        originalTable.addColumnAndFire(columnToMove, oldIndex);
    }
}
