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

package com.cubrid.common.ui.query.dialog;

import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.control.SQLEditorComposite;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Replace/Append column's alias dialog
 *
 * @author Isaiah Choe 2012-10-19
 */
public final class ReformatColumnsAliasDialog extends Dialog {
    private Text aliasText;
    private String resultAliasText;
    private Shell shell;
    private static ReformatColumnsAliasDialog dialog = null;
    private SQLEditorComposite sqlComp = null;

    public static String openDialog() {
        String result = null;
        synchronized (ReformatColumnsAliasDialog.class) {
            if (dialog == null) {
                Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                dialog = new ReformatColumnsAliasDialog(shell, SWT.NONE);
                result = dialog.open();
                dialog = null;
            } else {
                if (dialog.getShell() != null && !dialog.getShell().isDisposed()) {
                    dialog.getShell().forceFocus();
                }
            }
        }

        return result;
    }

    private ReformatColumnsAliasDialog(Shell parent, int style) {
        super(parent, style);
    }

    public String open() {
        createContents();
        CommonUITool.centerShell(shell);
        shell.open();
        shell.layout();
        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        return resultAliasText;
    }

    private SQLEditorComposite getActiveSQLEditorComposite() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IEditorPart editor = window.getActivePage().getActiveEditor();
        if (editor instanceof QueryEditorPart) {
            QueryEditorPart queryEditor = (QueryEditorPart) editor;
            return queryEditor.getCombinedQueryComposite().getSqlEditorComp();
        }
        return null;
    }

    public Shell getShell() {
        return shell;
    }

    protected void createContents() {
        shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        shell.setLayout(gridLayout);
        shell.setSize(450, 100);
        shell.setText(Messages.reformatTableAliasTitle);

        final Composite composite = new Composite(shell, SWT.NONE);
        final GridData gdComposite = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdComposite.heightHint = 0;
        gdComposite.widthHint = 296;
        composite.setLayoutData(gdComposite);
        composite.setLayout(new GridLayout());

        sqlComp = getActiveSQLEditorComposite();
        if (sqlComp == null) {
            close();
            return;
        }

        final Composite group = new Composite(composite, SWT.NONE);
        group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        final GridLayout gridLayoutGroup = new GridLayout();
        gridLayoutGroup.marginHeight = 0;
        gridLayoutGroup.numColumns = 1;
        group.setLayout(gridLayoutGroup);
        {
            final Label findWhatLabel = new Label(group, SWT.NONE);
            String msg = Messages.reformatTableAliasMessage;
            findWhatLabel.setText(msg);

            aliasText = new Text(group, SWT.BORDER);
            final GridData gdFind = new GridData(SWT.FILL, SWT.CENTER, true, false);
            aliasText.setLayoutData(gdFind);
            aliasText.addKeyListener(
                    new KeyListener() {
                        public void keyReleased(KeyEvent e) {}

                        public void keyPressed(KeyEvent e) {
                            if (e.keyCode == SWT.CR) {
                                submit();
                            }
                        }
                    });
        }
        final Composite composite1 = new Composite(shell, SWT.NONE);
        final GridData gdComposite1 = new GridData(SWT.RIGHT, SWT.FILL, false, false);
        composite1.setLayoutData(gdComposite1);
        final GridLayout gridLayoutGroup2 = new GridLayout();
        gridLayoutGroup2.marginHeight = 0;
        gridLayoutGroup2.numColumns = 2;
        composite1.setLayout(gridLayoutGroup2);

        new Label(composite1, SWT.NONE);
        new Label(composite1, SWT.NONE);

        final Button findBtn = new Button(composite1, SWT.NONE);
        final GridData gdFindBtn = new GridData(SWT.FILL, SWT.CENTER, false, false);
        findBtn.setLayoutData(gdFindBtn);
        findBtn.setText(Messages.reformatTableAliasBtn);
        findBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        submit();
                    }
                });

        final Button closeBtn = new Button(composite1, SWT.NONE);
        final GridData gdCloseBtn = new GridData(SWT.FILL, SWT.CENTER, false, false);
        closeBtn.setLayoutData(gdCloseBtn);
        closeBtn.setText(Messages.reformatTableAliasCancelBtn);
        closeBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        close();
                    }
                });

        aliasText.addKeyListener(
                new KeyListener() {
                    public void keyReleased(KeyEvent e) {
                        if (e.keyCode == SWT.CR) {
                            submit();
                        }
                    }

                    public void keyPressed(KeyEvent e) {}
                });
    }

    private void submit() {
        String textForCheck = aliasText.getText().trim();
        if (textForCheck.length() == 0) {
            CommonUITool.openErrorBox(Messages.reformatTableAliasError);
            aliasText.forceFocus();
            aliasText.selectAll();
            return;
        }

        char c = textForCheck.charAt(0);
        if ((c >= '0' && c <= '9') || !ValidateUtil.isValidIdentifier(textForCheck)) {
            CommonUITool.openErrorBox(Messages.reformatTableAliasError2);
            aliasText.forceFocus();
            aliasText.selectAll();
            return;
        }

        resultAliasText = aliasText.getText().trim();
        close();
    }

    private void close() {
        dialog = null;
        shell.dispose();
    }

    public static ReformatColumnsAliasDialog getDialog() {
        return dialog;
    }
}
