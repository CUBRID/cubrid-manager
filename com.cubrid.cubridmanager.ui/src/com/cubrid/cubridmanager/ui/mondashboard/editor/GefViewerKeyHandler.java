/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.cubridmanager.ui.mondashboard.editor;

import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HANode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.DashboardPart;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.HANodePart;
import java.util.List;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

/**
 * Move figures by keyboard.
 *
 * @author SC13425
 * @version 1.0 - 2010-6-23 created by SC13425
 */
public class GefViewerKeyHandler extends GraphicalViewerKeyHandler {
    public GefViewerKeyHandler(GraphicalViewer viewer) {
        super(viewer);
    }

    /**
     * @see org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler.keyPressed
     * @param event KeyEvent
     * @return true or false.
     */
    public boolean keyPressed(KeyEvent event) {
        GraphicalEditPart editPart = getFocusEditPart();
        if (editPart instanceof HANodePart) {
            if (moveHANodesByKey(event)) {
                return true;
            }
            if (deleteHANodeByKey(event)) {
                return true;
            }
        }
        if (editPart instanceof DashboardPart && event.keyCode == SWT.F5) {
            DashboardPart dp = (DashboardPart) editPart;
            dp.refresh();
            List<?> childEditPart = dp.getChildren();
            for (Object child : childEditPart) {
                ((EditPart) child).refresh();
            }
        }
        return super.keyPressed(event);
    }

    /**
     * delete node by delete key,do not suppurt multi selected.
     *
     * @param event KeyEvent
     * @return if delete node return true,else return false
     */
    private boolean deleteHANodeByKey(KeyEvent event) {
        if (event.keyCode == SWT.DEL && getViewer().getSelectedEditParts().size() == 1) {
            GroupRequest deleteRequest = new GroupRequest(RequestConstants.REQ_DELETE);
            GraphicalEditPart editPart = getFocusEditPart();
            deleteRequest.setEditParts(editPart);
            Command command = editPart.getCommand(deleteRequest);
            if (command != null && !(command instanceof UnexecutableCommand)) {
                command.execute();
                return true;
            }
        }
        return false;
    }

    /**
     * move figures by keyboard.
     *
     * @param event KeyEvent
     * @return if handled the event return true else return false.
     */
    private boolean moveHANodesByKey(KeyEvent event) {
        List<?> selectedEditParts = getViewer().getSelectedEditParts();
        boolean flag = false;
        for (Object obj : selectedEditParts) {
            if (!(obj instanceof HANodePart)) {
                continue;
            }
            GraphicalEditPart editPart = (GraphicalEditPart) obj;
            Command command = null;
            if ((event.stateMask & SWT.ALT) == 0
                    && (event.stateMask & SWT.CTRL) == 0
                    && (event.stateMask & SWT.SHIFT) == 0
                    && (event.keyCode == SWT.ARROW_DOWN
                            || event.keyCode == SWT.ARROW_LEFT
                            || event.keyCode == SWT.ARROW_RIGHT
                            || event.keyCode == SWT.ARROW_UP)) {

                HANode node = (HANode) editPart.getModel();

                ChangeBoundsRequest request = new ChangeBoundsRequest(RequestConstants.REQ_MOVE);
                request.setLocation(node.getLocation());
                request.setEditParts(editPart);
                // get move delta
                Point moveDelta = null;
                switch (event.keyCode) {
                    case SWT.ARROW_LEFT:
                        moveDelta = new Point(-1, 0);
                        break;

                    case SWT.ARROW_RIGHT:
                        moveDelta = new Point(1, 0);
                        break;

                    case SWT.ARROW_UP:
                        moveDelta = new Point(0, -1);
                        break;

                    case SWT.ARROW_DOWN:
                        moveDelta = new Point(0, 1);
                        break;
                    default:
                        return true;
                }
                request.setMoveDelta(moveDelta);
                command = editPart.getCommand(request);
            }
            if (command != null && !(command instanceof UnexecutableCommand)) {
                command.execute();
                flag = true;
            }
        }
        return flag;
    }
}
