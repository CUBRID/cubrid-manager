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
package com.cubrid.cubridmanager.ui.host.dialog;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.ui.host.Messages;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;

/**
 * Import the property file
 *
 * @author lizhiqiang
 * @version 1.0 - 2011-3-28 created by lizhiqiang
 */
public class ImportConfigDialog extends CMTitleAreaDialog {

    private static final Logger LOGGER = LogUtil.getLogger(ImportConfigDialog.class);
    private static final String SESSION_IMPORT_KEY = null;
    private Text fileNameTxt;
    private final ConfigType configType;
    private Combo fileCharsetCombo;
    private List<String> importFileContent;
    private boolean isOpen;
    private String defaultFileName;
    private String defaultCharset;

    public ImportConfigDialog(Shell parentShell, ConfigType configType) {
        super(parentShell);
        this.configType = configType;
    }

    public ImportConfigDialog(Shell parentShell, ConfigType configType, boolean isOpen) {
        this(parentShell, configType);
        this.isOpen = isOpen;
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
        createFileComp(composite);
        if (isOpen) {
            getShell().setText(Messages.ttlOpenShell);
            setTitle(Messages.bind(Messages.ttlOpenDialog, configType.getValue()));
            setMessage(Messages.bind(Messages.dscOpenDialog, configType.getValue()));
        } else {
            getShell().setText(Messages.ttlImportShell);
            setTitle(Messages.bind(Messages.ttlImportDialog, configType.getValue()));
            setMessage(Messages.bind(Messages.dscImportDialog, configType.getValue()));
        }

        return parent;
    }

    /**
     * Create the file composite
     *
     * @param composite Composite
     */
    private void createFileComp(Composite composite) {

        Composite fileComp = new Composite(composite, SWT.NONE);
        {
            GridLayout gridLayout = new GridLayout();
            gridLayout.numColumns = 3;

            GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
            fileComp.setLayoutData(gridData);
            fileComp.setLayout(gridLayout);
        }

        Label lblImportFile = new Label(fileComp, SWT.NONE);
        lblImportFile.setText(Messages.lblImportFileName);

        fileNameTxt = new Text(fileComp, SWT.BORDER | SWT.READ_ONLY);
        {
            GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
            fileNameTxt.setLayoutData(gridData);
            if (defaultFileName != null && !"".equals(defaultFileName)) {
                fileNameTxt.setText(defaultFileName);
            }
            fileNameTxt.addModifyListener(
                    new ModifyListener() {
                        public void modifyText(ModifyEvent event) {
                            validate();
                        }
                    });
        }

        Button btnOpen = new Button(fileComp, SWT.NONE);
        {
            GridData gridData = new GridData();
            gridData.widthHint = 66;
            btnOpen.setLayoutData(gridData);
            btnOpen.setText(Messages.btnBrowse);

            btnOpen.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            openFileDialog();
                        }
                    });
        }

        final Label fileCharsetLabel = new Label(fileComp, SWT.NONE);
        fileCharsetLabel.setText(Messages.lblFileCharset);

        fileCharsetCombo = new Combo(fileComp, SWT.NONE);
        {
            GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
            gridData.horizontalSpan = 2;
            fileCharsetCombo.setLayoutData(gridData);
            fileCharsetCombo.setItems(QueryOptions.getAllCharset(null));
            if (defaultCharset == null || "".equals(defaultCharset)) {
                String charset = StringUtil.getDefaultCharset();
                if (charset != null) {
                    fileCharsetCombo.setText(charset);
                }
            } else {
                fileCharsetCombo.setText(defaultCharset);
            }
            fileCharsetCombo.addModifyListener(
                    new ModifyListener() {
                        public void modifyText(ModifyEvent event) {
                            validate();
                        }
                    });
        }
    }

    /**
     * Notifies that the ok button of this dialog has been pressed.
     *
     * <p>The <code>Dialog</code> implementation of this framework method sets this dialog's return
     * code to <code>Window.OK</code> and closes the dialog. Subclasses may override.
     */
    protected void okPressed() {
        if (!validate()) {
            return;
        }
        if (!isOpen) {
            boolean isConfirm = CommonUITool.openConfirmBox(Messages.msgConfirmImport);
            if (!isConfirm) {
                return;
            }
        }
        defaultFileName = fileNameTxt.getText().trim();
        defaultCharset = fileCharsetCombo.getText().trim();
        File file = new File(defaultFileName);
        FileInputStream fis = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        importFileContent = new ArrayList<String>();
        try {
            fis = new FileInputStream(file);
            inputStreamReader = new InputStreamReader(fis, defaultCharset);
            bufferedReader = new BufferedReader(inputStreamReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                importFileContent.add(line);
            }
        } catch (FileNotFoundException ex) {
            LOGGER.error(ex.getMessage());
            CommonUITool.openErrorBox(Messages.msgImportFileNoFound);
            return;
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    LOGGER.error(ex.getMessage());
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException ex) {
                    LOGGER.error(ex.getMessage());
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                    LOGGER.error(ex.getMessage());
                }
            }
        }
        super.okPressed();
    }

    /** open file dialog */
    private void openFileDialog() {
        FileDialog dialog = new FileDialog(getShell(), SWT.OPEN | SWT.APPLICATION_MODAL);
        dialog.setFilterExtensions(new String[] {"*.conf", "*.txt"});
        dialog.setFilterNames(
                new String[] {
                    Messages.bind(Messages.confFileType, "(*.conf)"),
                    Messages.bind(Messages.txtFileType, "(*.txt)")
                });

        String filepath = CommonUIPlugin.getSettingValue(SESSION_IMPORT_KEY);
        if (null != filepath) {
            dialog.setFilterPath(filepath);
        }
        String fileName = dialog.open();
        if (fileName == null) {
            return;
        }
        fileNameTxt.setText(fileName);
    }

    /**
     * validate the data
     *
     * @return boolean
     */
    protected boolean validate() {
        setErrorMessage(null);
        Button okBtn = getButton(IDialogConstants.OK_ID);
        if (okBtn != null) {
            okBtn.setEnabled(false);
        }

        if (fileNameTxt != null && fileNameTxt.getText().length() < 1) {
            setErrorMessage(Messages.errImportSelectFile);
            fileNameTxt.setFocus();
            return false;
        }
        if (fileCharsetCombo != null) {
            String charsetName = fileCharsetCombo.getText();
            try {
                "".getBytes(charsetName);
            } catch (UnsupportedEncodingException e) {
                setErrorMessage(Messages.errUnsupportedCharset);
                return false;
            }
        }
        if (okBtn != null) {
            okBtn.setEnabled(true);
        }
        return true;
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        CommonUITool.centerShell(getShell());
    }

    /**
     * Create buttons for button bar
     *
     * @param parent the parent composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(IDialogConstants.OK_ID).setText(Messages.btnOk);
        if (fileNameTxt == null
                || "".equals(fileNameTxt.getText().trim())
                || fileCharsetCombo == null
                || "".equals(fileCharsetCombo.getText().trim())) {
            getButton(IDialogConstants.OK_ID).setEnabled(false);
        } else {
            getButton(IDialogConstants.OK_ID).setEnabled(true);
        }
    }

    /**
     * Get the importFileContent
     *
     * @return the importFileContent
     */
    public List<String> getImportFileContent() {
        return importFileContent;
    }

    /**
     * Get the defaultFileName
     *
     * @return the defaultFileName
     */
    public String getDefaultFileName() {
        return defaultFileName;
    }

    /** @param defaultFileName the defaultFileName to set */
    public void setDefaultFileName(String defaultFileName) {
        this.defaultFileName = defaultFileName;
    }

    /**
     * Get the defaultCharset
     *
     * @return the defaultCharset
     */
    public String getDefaultCharset() {
        return defaultCharset;
    }

    /** @param defaultCharset the defaultCharset to set */
    public void setDefaultCharset(String defaultCharset) {
        this.defaultCharset = defaultCharset;
    }
}
