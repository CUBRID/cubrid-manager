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

import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.TableUtil;
import java.io.File;
import java.io.UnsupportedEncodingException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Export result data dialog
 *
 * @author pangqiren
 * @version 1.0 - 2010-9-2 created by pangqiren
 */
public class ExportResultDialog extends CMTitleAreaDialog implements ModifyListener {

    private static final String[] SUPPORT_FILTER_EXTS = new String[] {"*.csv", "*.xls", "*.xlsx"};
    private static final String[] SUPPORT_FILTER_NAMES =
            new String[] {Messages.csvFileType, Messages.xlsFileType, Messages.xlsxFileType};

    private Combo fileCharsetCombo;
    private final CubridDatabase database;
    private Text filePathText;
    private File file = null;
    private String fileCharset = null;
    private String[] filterExts = SUPPORT_FILTER_EXTS;
    private String[] filterNames = SUPPORT_FILTER_NAMES;
    private final String message;

    /**
     * The constructor
     *
     * @param parentShell
     * @param database
     * @param message
     */
    public ExportResultDialog(Shell parentShell, CubridDatabase database, String message) {
        super(parentShell);
        this.database = database;
        this.message = message;
    }

    /**
     * The constructor
     *
     * @param parentShell
     * @param database
     * @param filterExts
     * @param filterNames
     * @param message
     */
    public ExportResultDialog(
            Shell parentShell,
            CubridDatabase database,
            String[] filterExts,
            String[] filterNames,
            String message) {
        super(parentShell);
        this.database = database;
        if (filterExts == null) {
            this.filterExts = null;
        } else {
            this.filterExts = filterExts.clone();
        }
        if (filterNames == null) {
            this.filterNames = null;
        } else {
            this.filterNames = filterNames.clone();
        }
        this.message = message;
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     * @param parent The parent composite to contain the dialog area
     * @return the dialog area control
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);

        Composite composite = new Composite(parentComp, SWT.NONE);
        {
            composite.setLayoutData(new GridData(GridData.FILL_BOTH));

            GridLayout layout = new GridLayout();
            layout.numColumns = 1;
            layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
            layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
            layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
            layout.horizontalSpacing =
                    convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
            composite.setLayout(layout);
        }

        createGroup(composite);

        setTitle(Messages.exportDataMsgTitle);
        setMessage(message);
        return parent;
    }

    /**
     * This method initializes grpTop
     *
     * @param composite Composite
     */
    private void createGroup(Composite composite) {
        Group group = new Group(composite, SWT.NONE);
        {
            group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            final GridLayout gridLayout = new GridLayout();
            gridLayout.numColumns = 3;
            group.setLayout(gridLayout);
        }

        Label lblExportFile = new Label(group, SWT.NONE);
        lblExportFile.setText(Messages.importFileNameLBL);
        lblExportFile.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        filePathText = new Text(group, SWT.BORDER);
        filePathText.setEnabled(false);
        filePathText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        filePathText.addModifyListener(this);

        Button btnOpen = new Button(group, SWT.NONE);
        {
            btnOpen.setText(Messages.btnBrowse);
            btnOpen.setLayoutData(CommonUITool.createGridData(GridData.END, 1, 1, -1, -1));
            btnOpen.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            File file =
                                    TableUtil.getSavedFile(
                                            getShell(),
                                            filterExts,
                                            filterNames,
                                            null,
                                            ".csv",
                                            filePathText.getText());
                            if (file != null) {
                                filePathText.setText(file.getAbsolutePath());
                            }
                        }
                    });
        }

        final Label fileCharsetLabel = new Label(group, SWT.NONE);
        fileCharsetLabel.setText(Messages.lblFileCharset);

        fileCharsetCombo = new Combo(group, SWT.NONE);
        {
            fileCharsetCombo.setLayoutData(
                    CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
            fileCharsetCombo.setItems(QueryOptions.getAllCharset(null));
            String charset = database.getDatabaseInfo().getCharSet();
            if (charset != null) {
                fileCharsetCombo.setText(charset);
            }
            fileCharsetCombo.addModifyListener(this);
        }
    }

    /**
     * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
     * @param event an event containing information about the modify
     */
    public void modifyText(ModifyEvent event) {

        setErrorMessage(null);
        getButton(IDialogConstants.OK_ID).setEnabled(false);

        if (filePathText.getText() == null || "".equals(filePathText.getText().trim())) {
            setErrorMessage(Messages.exportSelectFileERRORMSG);
            return;
        }

        String charsetName = fileCharsetCombo.getText();
        try {
            "".getBytes(charsetName);
        } catch (UnsupportedEncodingException e) {
            setErrorMessage(Messages.errUnsupportedCharset);
            return;
        }

        getButton(IDialogConstants.OK_ID).setEnabled(true);
    }

    /** @see com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog#constrainShellSize() */
    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setText(Messages.exportShellTitle);
        getShell().setMinimumSize(550, 280);
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     * @param parent the button bar composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.exportButtonName, false);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.closeButtonName, false);
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     * @param buttonId the id of the button that was pressed (see <code>IDialogConstants.*_ID</code>
     *     constants)
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            String filePath = filePathText.getText();
            file = new File(filePath);
            fileCharset = fileCharsetCombo.getText();
        }
        super.buttonPressed(buttonId);
    }

    public File getFile() {
        return file;
    }

    public String getFileCharset() {
        return fileCharset;
    }
}
