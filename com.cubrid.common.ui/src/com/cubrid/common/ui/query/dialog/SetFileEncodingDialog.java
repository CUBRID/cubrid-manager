/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *  - Neither the name of the <ORGANIZATION> nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.io.File;
import java.io.UnsupportedEncodingException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Set encoding dialog
 *
 * @author pangqiren
 * @version 1.0 - 2011-1-17 created by pangqiren
 */
public class SetFileEncodingDialog extends CMTitleAreaDialog {
    private Combo charsetCombo;
    private String encoding;
    private Text filePathText;
    private final boolean isOpened;
    private String filePath = null;

    /**
     * Constructor
     *
     * @param parent Shell
     * @param encoding String
     * @param isOpened boolean
     */
    public SetFileEncodingDialog(Shell parent, String encoding, boolean isOpened) {
        super(parent);
        this.encoding = encoding;
        this.isOpened = isOpened;
    }

    /**
     * Constructor
     *
     * @param parent Shell
     * @param encoding String
     * @param isOpened boolean
     * @param filePath filePath
     */
    public SetFileEncodingDialog(Shell parent, String encoding, boolean isOpened, String filePath) {
        super(parent);
        this.encoding = encoding;
        this.isOpened = isOpened;
        this.filePath = filePath;
    }

    /**
     * Create dialog area
     *
     * @param parent Composite
     * @return Control
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);
        Composite composite = new Composite(parentComp, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);

        Label lblExportFile = new Label(composite, SWT.NONE);
        lblExportFile.setText(Messages.lblFilePath);
        lblExportFile.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        filePathText = new Text(composite, SWT.BORDER);
        filePathText.setEditable(false);
        filePathText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, 250, -1));
        if (filePath != null) {
            filePathText.setText(filePath);
        }
        Button btnOpen = new Button(composite, SWT.NONE);
        {
            btnOpen.setText(com.cubrid.common.ui.cubrid.table.Messages.btnBrowse);
            btnOpen.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
            btnOpen.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            openFile();
                        }
                    });
        }

        Label lblCharset = new Label(composite, SWT.NONE);
        lblCharset.setText(Messages.lblCharSet);
        lblCharset.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        String defaultCharset = StringUtil.getDefaultCharset();
        if (encoding == null) {
            encoding = defaultCharset;
        }
        charsetCombo = new Combo(composite, SWT.LEFT | SWT.BORDER);
        charsetCombo.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 2, 1, -1, -1));
        charsetCombo.setItems(QueryOptions.getAllCharset(encoding));
        charsetCombo.setText(encoding);
        // For bug TOOLS-3313
        // openFile();
        return composite;
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setSize(430, 240);
        if (isOpened) {
            getShell().setText(Messages.titleOpenFileDialog);
            setTitle(Messages.titleOpenFileDialogDetail);
            setMessage(Messages.msgOpenFileDialogDetail);
        } else {
            getShell().setText(Messages.titleSaveFileDialog);
            setTitle(Messages.titleSaveFileDialogDetail);
            setMessage(Messages.msgSaveFileDialogDetail);
        }
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     * @param parent the button bar composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(
                parent, IDialogConstants.OK_ID, com.cubrid.common.ui.common.Messages.btnOK, true);
        createButton(
                parent,
                IDialogConstants.CANCEL_ID,
                com.cubrid.common.ui.common.Messages.btnCancel,
                false);
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     * @param buttonId the id of the button that was pressed (see <code>IDialogConstants.*_ID</code>
     *     constants)
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            filePath = filePathText.getText();
            if (filePath == null || filePath.trim().length() == 0) {
                CommonUITool.openErrorBox(
                        com.cubrid.common.ui.cubrid.table.Messages.errInvalidFile);
                return;
            }
            encoding = charsetCombo.getText();
            try {
                "".getBytes(encoding);
            } catch (UnsupportedEncodingException e) {
                CommonUITool.openErrorBox(
                        com.cubrid.common.ui.cubrid.table.Messages.errUnsupportedCharset);
                return;
            }
        }
        super.buttonPressed(buttonId);
    }

    public String getEncoding() {
        return encoding;
    }

    public String getFilePath() {
        return filePath;
    }

    private void openFile() {
        String filePath = null;
        if (isOpened) {
            File file = QueryEditorPart.getOpenedSQLFile();
            if (file != null) {
                filePath = file.getAbsolutePath();
            }
        } else {
            File savedDirFile = QueryEditorPart.getSavedFile();
            if (savedDirFile != null) {
                filePath = savedDirFile.getAbsolutePath();
            }
        }
        if (filePath != null) {
            filePathText.setText(filePath);
        }
    }
}
