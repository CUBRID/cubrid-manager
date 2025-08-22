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
package com.cubrid.common.ui.query.dialog;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.common.sqlrunner.part.RunSQLFileEditorInput;
import com.cubrid.common.ui.common.sqlrunner.part.RunSQLFileViewPart;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.control.BatchRunComposite;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.query.editor.QueryUnit;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * the run batch sql dialog
 *
 * @author Isaiah Choe 2012-05-18
 */
public class BatchRunDialog extends CMTitleAreaDialog {
    private static final Logger LOGGER = LogUtil.getLogger(BatchRunDialog.class);

    private BatchRunComposite container;
    private Button btnRun;
    private Button btnBatchPaste;
    private CubridDatabase cubridDatabase;
    public static final int RUN_ID = Integer.MAX_VALUE - 1;
    public static final int PASTE_ID = Integer.MAX_VALUE - 2;

    /** @param parentShell */
    public BatchRunDialog(Shell parentShell, CubridDatabase cubridDatabase) {
        super(parentShell);
        this.cubridDatabase = cubridDatabase;
    }

    /**
     * Create dialog area content
     *
     * @param parent the parent composite
     * @return the control
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);

        Composite composite = new Composite(parentComp, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        container = new BatchRunComposite(composite);
        container.setCubridDatabase(cubridDatabase);

        setTitle(Messages.titleBatchRunMessage);
        setMessage(Messages.msgBatchRunMessage);

        return parentComp;
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setMinimumSize(700, 450);
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.titleBatchRunMessage);
    }

    /**
     * Create buttons for button bar
     *
     * @param parent the parent composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        btnBatchPaste = createButton(parent, PASTE_ID, Messages.btnBatchPaste, false);
        btnBatchPaste.setEnabled(false);
        container.setPasteButton(btnBatchPaste);

        btnRun = createButton(parent, RUN_ID, Messages.btnBatchRun, false);
        btnRun.setEnabled(false);
        container.setRunButton(btnRun);
        createButton(parent, IDialogConstants.OK_ID, Messages.btnBatchClose, false);
    }

    /**
     * Call this method when the button in button bar is pressed
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == RUN_ID) {
            if (!MessageDialog.openConfirm(
                    PlatformUI.getWorkbench().getDisplay().getActiveShell(),
                    Messages.titleBatchRunConfirm,
                    Messages.msgBatchRunConfirm)) {
                return;
            }

            List<String> fileList = container.getFileList();
            RunSQLFileEditorInput input = new RunSQLFileEditorInput(cubridDatabase, fileList);
            try {
                PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow()
                        .getActivePage()
                        .openEditor(input, RunSQLFileViewPart.ID);
            } catch (Exception e) {
                LOGGER.error(e.getLocalizedMessage());
            }

            super.buttonPressed(IDialogConstants.OK_ID);
        } else if (buttonId == PASTE_ID) {
            //			if (!MessageDialog.openConfirm(
            //					PlatformUI.getWorkbench().getDisplay().getActiveShell(),
            //					Messages.titleBatchRunConfirm, Messages.msgBatchRunPasteConfirm)) {
            //				return;
            //			}
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            if (window == null || window.getActivePage() == null) {
                return;
            }
            IEditorPart editor = window.getActivePage().getActiveEditor();
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

            QueryEditorPart oldEditor = (QueryEditorPart) editor;

            try {
                QueryEditorPart queryEditor = (QueryEditorPart) editor;
                String encoding =
                        queryEditor
                                .getCombinedQueryComposite()
                                .getSqlEditorComp()
                                .getDocument()
                                .getEncoding();
                StringBuilder sb = new StringBuilder();
                List<String> fileList = container.getFileList();
                for (int i = 0; i < fileList.size(); i++) {
                    sb.delete(0, sb.length());
                    sb.append("/* SQL Filename: ")
                            .append(fileList.get(i))
                            .append(" */")
                            .append(StringUtil.NEWLINE);

                    BufferedReader in = null;
                    try {
                        in =
                                new BufferedReader(
                                        new InputStreamReader(
                                                new FileInputStream(new File(fileList.get(i))),
                                                encoding));
                        String line = in.readLine();
                        while (line != null) {
                            sb.append(line + StringUtil.NEWLINE);
                            line = in.readLine();
                        }
                    } finally {
                        try {
                            if (in != null) {
                                in.close();
                            }
                        } catch (IOException e) {
                        }
                    }

                    try {
                        QueryUnit input = new QueryUnit();
                        QueryEditorPart newEditor =
                                (QueryEditorPart)
                                        window.getActivePage()
                                                .openEditor(input, QueryEditorPart.ID);
                        newEditor.setQuery(sb.toString(), false, false, false);
                        newEditor.connect(oldEditor.getSelectedDatabase());
                    } catch (Exception e) {
                        CommonUITool.openErrorBox(e.getMessage());
                    }
                }
            } catch (IOException e) {
                CommonUITool.openErrorBox(e.getMessage());
            }

            super.buttonPressed(IDialogConstants.OK_ID);
        } else {
            super.buttonPressed(buttonId);
        }
    }
}
