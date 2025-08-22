/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.common.action;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.query.dialog.SetFileEncodingDialog;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.query.editor.QueryUnit;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * Show new query editor
 *
 * @author wangsl
 * @version 1.0 - 2009-03-17 created by wangsl
 * @version 1.1 - 2012-09-05 created by Isaiah Choe
 */
public class OpenQueryAction extends SelectionAction {

    public static final String ID = OpenQueryAction.class.getName();
    private static final Logger LOGGER = LogUtil.getLogger(OpenQueryAction.class);

    protected OpenQueryAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        setId(ID);
        setToolTipText(text);
        setEnabled(true);
    }

    public OpenQueryAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    protected void selectionChanged(ISelection selection) {
        if (selection == null || selection.isEmpty()) {
            setEnabled(true);
            return;
        }
        super.selectionChanged(selection);
    }

    public boolean allowMultiSelections() {
        return true;
    }

    public boolean isSupported(Object obj) {
        return true;
    }

    private CubridDatabase[] handleSelectionObj(Object[] objs) {
        List<CubridDatabase> returnArray = new ArrayList<CubridDatabase>();
        for (Object obj : objs) {
            if (obj instanceof ISchemaNode) {
                CubridDatabase database = ((ISchemaNode) obj).getDatabase();
                if (database != null) {
                    returnArray.add(database);
                }
            }
        }

        return returnArray.toArray(new CubridDatabase[0]);
    }

    public void run() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }

        String fileName = null;

        try {
            SetFileEncodingDialog dialog =
                    new SetFileEncodingDialog(getShell(), StringUtil.getDefaultCharset(), true);
            if (IDialogConstants.OK_ID != dialog.open()) {
                return;
            }

            fileName = dialog.getFilePath();

            IEditorPart editor =
                    window.getActivePage().openEditor(new QueryUnit(), QueryEditorPart.ID);
            if (editor == null) {
                return;
            }

            QueryEditorPart queryEditor = (QueryEditorPart) editor;
            queryEditor
                    .getCombinedQueryComposite()
                    .getSqlEditorComp()
                    .open(dialog.getFilePath(), dialog.getEncoding());

            Object[] obj = this.getSelectedObj();
            CubridDatabase[] cubridDatabases = handleSelectionObj(obj);
            if (cubridDatabases.length > 0 && cubridDatabases[0] != null) {
                ((QueryEditorPart) editor).connect(cubridDatabases[0]);
            }
        } catch (PartInitException e) {
            LOGGER.error("Can not initialize the query editor UI.", e);
        } catch (IOException e) {
            LOGGER.error("Can not open the {} file.", fileName, e);
            CommonUITool.openErrorBox(e.getMessage()); // TODO: message localizing
        }
    }
}
