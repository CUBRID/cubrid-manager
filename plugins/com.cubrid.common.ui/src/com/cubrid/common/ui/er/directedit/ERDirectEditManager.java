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
package com.cubrid.common.ui.er.directedit;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * Initialize and handle the action when user edit a label
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-7-12 created by Yu Guojia
 */
public class ERDirectEditManager extends DirectEditManager {
    protected Label label;
    private ICellEditorValidator validator;
    private boolean isCommitting = false;

    public ERDirectEditManager(
            Label label,
            ICellEditorValidator validator,
            GraphicalEditPart source,
            Class<?> editorType,
            CellEditorLocator locator) {
        super(source, editorType, locator);
        this.label = label;
        this.validator = validator;
    }

    public void bringDown() {
        super.bringDown();
    }

    protected void initCellEditor() {
        getCellEditor().setValue(label.getText());
        getCellEditor().setValidator(validator);

        FontData fontData = label.getFont().getFontData()[0];
        Dimension fontSize = new Dimension(0, fontData.getHeight());
        label.translateToAbsolute(fontSize);
        fontData.setHeight(fontSize.height);

        Text text = (Text) getCellEditor().getControl();
        text.setFont(new Font(null, fontData));
        text.setRedraw(true);
        text.setVisible(true);
    }

    public void commit() {
        if (isCommitting || !preCommitCheck()) {
            return;
        }

        try {
            isCommitting = true;
            if (isDirty()) {
                executeOnCommandStack(getCommand());
            }
        } finally {
            bringDown();
            isCommitting = false;
        }
    }

    private boolean preCommitCheck() {
        String error = getCellEditor() == null ? null : getCellEditor().getErrorMessage();
        if (!StringUtil.isEmpty(error)) {
            bringDown();
            CommonUITool.openErrorBox(error);
            return false;
        }
        return true;
    }

    public CellEditor createCellEditorOn(Composite composite) {
        return super.createCellEditorOn(composite);
    }

    public CellEditor getCellEditor() {
        return super.getCellEditor();
    }

    public void setCellEditor(CellEditor editor) {
        super.setCellEditor(editor);
    }

    private void executeOnCommandStack(Command command) {
        if (command == null || !command.canExecute()) {
            return;
        }
        CommandStack commandStack = getEditPart().getViewer().getEditDomain().getCommandStack();
        commandStack.execute(command);
    }

    private Command getCommand() {
        return getEditPart().getCommand(getDirectEditRequest());
    }
}
