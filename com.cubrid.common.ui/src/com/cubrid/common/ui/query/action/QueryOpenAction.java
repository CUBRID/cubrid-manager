/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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

import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.control.SQLEditorComposite;
import com.cubrid.common.ui.query.dialog.SetFileEncodingDialog;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.query.editor.QueryUnit;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.io.IOException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * action for open a query file
 *
 * @author wangsl 2009-3-17
 */
public class QueryOpenAction extends SelectionAction {

    public static final String ID = QueryOpenAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param provider
     * @param text
     * @param icon
     */
    protected QueryOpenAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        setId(ID);
        setToolTipText(text);
        setEnabled(true);
    }

    public QueryOpenAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    /**
     * @see
     *     com.cubrid.common.ui.spi.action.SelectionAction#selectionChanged(org.eclipse.jface.viewers.ISelection)
     * @param selection the ISelection object
     */
    protected void selectionChanged(ISelection selection) {
        if (selection == null || selection.isEmpty()) {
            setEnabled(true);
            return;
        }
        super.selectionChanged(selection);
    }

    /**
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#allowMultiSelections()
     * @return <code>true</code> if allow multi selection;<code>false</code> otherwise
     */
    public boolean allowMultiSelections() {
        return true;
    }

    /**
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#isSupported(java.lang.Object)
     * @param obj the Object
     * @return <code>true</code> if support this obj;<code>false</code> otherwise
     */
    public boolean isSupported(Object obj) {
        return true;
    }

    /** @see org.eclipse.jface.action.Action#run() */
    public void run() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null || window.getActivePage() == null) {
            return;
        }

        IEditorPart editor = window.getActivePage().getActiveEditor();
        if (editor != null && editor.isDirty()) {
            int confirm =
                    CommonUITool.openMsgBox(
                            editor.getSite().getShell(),
                            MessageDialog.WARNING,
                            Messages.saveResource,
                            Messages.bind(Messages.saveConfirm, editor.getTitle()),
                            new String[] {Messages.btnYes, Messages.btnNo, Messages.cancel});
            switch (confirm) {
                case 0:
                    editor.doSave(null);
                    break;
                case 1:
                    break;
                default:
                    return;
            }
        }

        try {
            if (editor == null) {
                IEditorInput input = new QueryUnit();
                editor = window.getActivePage().openEditor(input, QueryEditorPart.ID);
            }
        } catch (PartInitException e) {
            CommonUITool.openErrorBox(e.getMessage());
        }

        if (editor == null) {
            return;
        }

        try {
            QueryEditorPart queryEditor = (QueryEditorPart) editor;
            SQLEditorComposite editorComp =
                    queryEditor.getCombinedQueryComposite().getSqlEditorComp();
            String encoding = editorComp.getDocument().getEncoding();
            SetFileEncodingDialog dialog = new SetFileEncodingDialog(getShell(), encoding, true);
            if (IDialogConstants.OK_ID == dialog.open()) {
                editorComp.open(dialog.getFilePath(), dialog.getEncoding());
            }
        } catch (IOException e) {
            CommonUITool.openErrorBox(e.getMessage());
        }
    }
}
