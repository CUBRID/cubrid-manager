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

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.er.Messages;
import com.cubrid.common.ui.er.part.ColumnPart;
import com.cubrid.common.ui.er.part.RelationshipPart;
import com.cubrid.common.ui.er.part.TablePart;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Delete part(s) on ERD.
 *
 * @author Yu Guojia
 * @version 1.0 - 2014-4-18 created by Yu Guojia
 */
public class DeleteAction extends AbstractSelectionAction {
    public static String ID = DeleteAction.class.getName();
    public static String NAME = Messages.actionDelete;

    public DeleteAction(IWorkbenchPart part) {
        super(part);
        setLazyEnablementCalculation(false);
    }

    protected void init() {
        setText(NAME);
        setToolTipText(NAME);
        setId(ID);

        ImageDescriptor icon =
                CommonUIPlugin.getImageDescriptor("icons/action/table_record_delete.png");
        if (icon != null) {
            setImageDescriptor(icon);
            setEnabled(true);
        }
    }

    public Command buildDeleteCommands(List<EditPart> parts) {
        if (parts == null || parts.size() == 0) {
            return null;
        }

        GroupRequest delRequest = new GroupRequest(RequestConstants.REQ_DELETE);
        delRequest.setEditParts(parts);

        CompoundCommand compCommands = new CompoundCommand(RequestConstants.REQ_DELETE);
        for (EditPart part : parts) {
            Command cmd = part.getCommand(delRequest);
            if (cmd != null) {
                compCommands.add(cmd);
            }
        }

        return compCommands;
    }

    /**
     * 1. First, delete relationship connection line; <br>
     * 2. Then, delete column; <br>
     * 3. Delete table at last.
     */
    public void run() {
        List objects = getSelectedObjects();
        if (objects.isEmpty()) {
            return;
        }

        List<EditPart> lineParts = new LinkedList<EditPart>();
        List<EditPart> columnParts = new LinkedList<EditPart>();
        List<EditPart> tableParts = new LinkedList<EditPart>();

        List<String> tableNames = new LinkedList<String>();
        Set<String> columnNames = new HashSet<String>();
        int lineCount = 0;

        for (Object obj : objects) {
            if (obj == null) {
                continue;
            }

            if (obj instanceof RelationshipPart) {
                lineParts.add((RelationshipPart) obj);
                lineCount++;
            } else if (obj instanceof ColumnPart) {
                columnParts.add((ColumnPart) obj);
                columnNames.add(((ColumnPart) obj).getName());
            } else if (obj instanceof TablePart) {
                tableParts.add((TablePart) obj);
                tableNames.add(((TablePart) obj).getName());
            }
        }
        List<EditPart> allParts = new LinkedList<EditPart>(lineParts);
        allParts.addAll(columnParts);
        allParts.addAll(tableParts);

        StringBuilder msg = new StringBuilder(Messages.msgConfirmDelete);
        if (!tableNames.isEmpty()) {
            msg.append(StringUtil.NEWLINE)
                    .append(Messages.msgConfirmDeleteTableList)
                    .append(tableNames.toString());
        }
        if (!columnNames.isEmpty()) {
            msg.append(StringUtil.NEWLINE)
                    .append(Messages.msgConfirmDeleteColumnList)
                    .append(columnNames.toString());
        }
        if (lineCount != 0) {
            msg.append(StringUtil.NEWLINE)
                    .append(Messages.msgConfirmDeleteLineCount)
                    .append(lineCount);
        }
        boolean delete =
                CommonUITool.openConfirmBox(
                        getWorkbenchPart().getSite().getShell(), msg.toString());

        if (delete) {
            execute(buildDeleteCommands(allParts));
        }
    }
}
