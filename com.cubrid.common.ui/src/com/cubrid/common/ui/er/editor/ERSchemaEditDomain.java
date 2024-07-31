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
package com.cubrid.common.ui.er.editor;

import com.cubrid.common.ui.er.ERCanvasDragTool;
import com.cubrid.common.ui.er.part.SchemaDiagramPart;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.editparts.FreeformGraphicalRootEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.ui.IEditorPart;

/**
 * ERSchema Edit Domain
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-11-28 created by Yu Guojia
 */
public class ERSchemaEditDomain extends DefaultEditDomain {
    private ERCanvasDragTool erDragTool;

    public ERSchemaEditDomain(IEditorPart editorPart) {
        super(editorPart);
        erDragTool = new ERCanvasDragTool();
    }

    /**
     * the button in the para of mouseEvent, that was pressed or released; 1 for the left click, 2
     * for the double left-click, and 3 for the right click, etc.
     */
    public void mouseDown(MouseEvent mouseEvent, EditPartViewer viewer) {
        ERSchemaEditor erschemaEditor = (ERSchemaEditor) this.getEditorPart();
        Point location = new Point(mouseEvent.x, mouseEvent.y);
        EditPart part = erschemaEditor.getGraphicalViewer().findObjectAt(location);
        if (part != null
                && ((part instanceof SchemaDiagramPart)
                        || (part instanceof FreeformGraphicalRootEditPart))) {
            if (getDefaultTool().equals(getActiveTool())) {
                setActiveTool(erDragTool);
                // when click on "FreeformGraphicalRootEditPart", its on the
                // extending space of "EXDefaultRangeModel"
            }
        }

        if (mouseEvent.button == 3 && !getDefaultTool().equals(getActiveTool())) {
            // mouseEvent.button == 3 : right click
            setActiveTool(getDefaultTool());
        }

        super.mouseDown(mouseEvent, viewer);

        if (part != null && part instanceof SchemaDiagramPart) {
            erschemaEditor.setAllFiguresOrigin();
        }
    }

    /**
     * Called when the mouse button has been released on a Viewer.
     *
     * @param mouseEvent The SWT mouse event
     * @param viewer The source of the event.
     */
    public void mouseUp(MouseEvent mouseEvent, EditPartViewer viewer) {
        super.mouseUp(mouseEvent, viewer);
        if (erDragTool.equals(getActiveTool())) {
            setActiveTool(getDefaultTool());
        }
    }

    public void keyUp(KeyEvent keyEvent, EditPartViewer viewer) {
        super.keyUp(keyEvent, viewer);
        if (!(this.getEditorPart() instanceof ERSchemaEditor)) {
            return;
        }
        if (keyEvent.keyCode == 97 && keyEvent.stateMask == SWT.CTRL) {
            ERSchemaEditor erschemaEditor = (ERSchemaEditor) this.getEditorPart();
            erschemaEditor.setAllTableSelected();
        }
    }
}
