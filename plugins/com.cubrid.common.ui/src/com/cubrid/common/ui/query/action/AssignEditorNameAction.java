/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: -
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. - Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. - Neither the name of the <ORGANIZATION> nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.cubrid.common.ui.query.action;

import com.cubrid.common.ui.common.dialog.AssignEditorNameDialog;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Assign editor name action
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-05-08 created by Kevin.Wang
 */
public class AssignEditorNameAction extends Action implements IMenuListener {
    public static final String ID = AssignEditorNameAction.class.getName();

    public AssignEditorNameAction(String title, ImageDescriptor aboutImageDescriptor) {
        super(title, aboutImageDescriptor);
        setId(ID);
    }

    public void run() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }
        IEditorPart editor = window.getActivePage().getActiveEditor();

        if (editor != null && editor instanceof QueryEditorPart) {
            QueryEditorPart queryEditorPart = (QueryEditorPart) editor;
            AssignEditorNameDialog dialog =
                    new AssignEditorNameDialog(
                            Display.getCurrent().getActiveShell(), queryEditorPart);
            dialog.open();
        }
    }

    /**
     * When menu show, update the action's status.
     *
     * @param manager IMenuManager
     */
    public void menuAboutToShow(IMenuManager manager) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            manager.remove(ID);
            return;
        }
        IEditorPart editor = window.getActivePage().getActiveEditor();
        if (editor instanceof QueryEditorPart) {
            if (manager.find(ID) == null) {
                manager.add(this);
            }
        } else {
            manager.remove(ID);
        }
    }
}
