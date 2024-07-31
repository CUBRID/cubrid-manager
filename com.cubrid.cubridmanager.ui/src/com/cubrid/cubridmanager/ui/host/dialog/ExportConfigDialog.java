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

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.ui.host.Messages;
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
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Export the property file.
 *
 * @author lizhiqiang
 * @version 1.0 - 2011-3-28 created by lizhiqiang
 */
public class ExportConfigDialog extends CMTitleAreaDialog {
    public static final String EXPORT_FILE_PATH_KEY = "Export-ExportFilePath";
    private Text filePathText;
    private Combo fileTypeCombo;
    private Combo fileCharsetCombo;
    private Text fileNameText;
    private String outputFileCharset;
    private String outputFileFullName;
    private final ConfigType configType;
    private String defaultFilePath;
    private String defaultFileName;
    private String defaultFileExtName;
    private boolean isSaveAs;

    public ExportConfigDialog(Shell parentShell, ConfigType configType) {
        super(parentShell);
        this.configType = configType;
        defaultFileExtName = ".conf";
        switch (configType) {
            case CUBRID:
                defaultFileName = "cubrid";
                break;
            case CUBRID_MANAGER:
                defaultFileName = "cm";
                break;
            case CUBRID_BROKER:
                defaultFileName = "cubrid_broker";
                break;
            case HA:
                defaultFileName = "cubrid_ha";
                break;
            default:
        }
    }

    public ExportConfigDialog(Shell parentShell, ConfigType configType, boolean isSaveAs) {
        this(parentShell, configType);
        this.isSaveAs = isSaveAs;
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
            layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
            layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
            layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
            layout.horizontalSpacing =
                    convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
            composite.setLayout(layout);
        }
        createFileComp(composite);
        if (isSaveAs) {
            getShell().setText(Messages.ttlSaveAsShell);
            setTitle(Messages.bind(Messages.ttlSaveAsDialog, configType.getValue()));
            setMessage(Messages.bind(Messages.dscSaveAsDialog, configType.getValue()));
        } else {
            getShell().setText(Messages.ttlExportShell);
            setTitle(Messages.bind(Messages.ttlExportDialog, configType.getValue()));
            setMessage(Messages.bind(Messages.dscExportDialog, configType.getValue()));
        }
        return parent;
    }

    /**
     * Create the file Composite
     *
     * @param parent the Composite
     */
    private void createFileComp(final Composite parent) {
        Composite textComp = new Composite(parent, SWT.NONE);
        {
            textComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            final GridLayout gridLayout = new GridLayout();
            gridLayout.numColumns = 3;
            textComp.setLayout(gridLayout);
        }

        Label lblExportFile = new Label(textComp, SWT.NONE);
        lblExportFile.setText(Messages.lblExportFilePath);

        filePathText = new Text(textComp, SWT.BORDER);
        if (defaultFilePath == null || "".equals(defaultFilePath)) {
            filePathText.setEnabled(false);
        } else {
            filePathText.setText(defaultFilePath);
            filePathText.setEnabled(true);
        }

        filePathText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        filePathText.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        validate();
                    }
                });

        Button btnOpen = new Button(textComp, SWT.NONE);
        {
            btnOpen.setText(Messages.btnBrowse);
            btnOpen.setLayoutData(CommonUITool.createGridData(1, 1, 66, -1));
            btnOpen.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            File savedDirFile = getSavedDir(getShell());
                            if (savedDirFile != null) {
                                filePathText.setText(savedDirFile.getAbsolutePath());
                            }
                        }
                    });
        }

        final Label fileNameLabel = new Label(textComp, SWT.NONE);
        fileNameLabel.setText(Messages.lblExportFileName);

        fileNameText = new Text(textComp, SWT.BORDER);
        {
            fileNameText.setLayoutData(
                    CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
            if (defaultFileName != null) {
                fileNameText.setText(defaultFileName);
            }
            fileNameText.addModifyListener(
                    new ModifyListener() {
                        public void modifyText(ModifyEvent event) {
                            validate();
                        }
                    });
        }

        final Label fileTypeLabel = new Label(textComp, SWT.NONE);
        fileTypeLabel.setText(Messages.lblExportFileType);

        fileTypeCombo = new Combo(textComp, SWT.NONE | SWT.READ_ONLY);
        {
            fileTypeCombo.setLayoutData(
                    CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
            fileTypeCombo.setItems(
                    new String[] {
                        Messages.bind(Messages.confFileType, "(*.conf)"),
                        Messages.bind(Messages.txtFileType, "(*.txt)")
                    });
            fileTypeCombo.select(0);
            fileTypeCombo.addModifyListener(
                    new ModifyListener() {
                        public void modifyText(ModifyEvent event) {
                            validate();
                        }
                    });
        }
        if (defaultFileExtName != null) {
            if (".conf".equals(defaultFileExtName)) {
                fileTypeCombo.select(0);
            } else if (".txt".equals(defaultFileExtName)) {
                fileTypeCombo.select(1);
            } else {
                fileNameText.setText(defaultFileExtName);
            }
        }

        final Label fileCharsetLabel = new Label(textComp, SWT.NONE);
        fileCharsetLabel.setText(Messages.lblFileCharset);

        fileCharsetCombo = new Combo(textComp, SWT.NONE);
        {
            fileCharsetCombo.setLayoutData(
                    CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
            fileCharsetCombo.setItems(QueryOptions.getAllCharset(null));
            if (outputFileCharset == null || "".equals(outputFileCharset)) {
                String charset = StringUtil.getDefaultCharset();
                if (charset != null) {
                    fileCharsetCombo.setText(charset);
                }
            } else {
                fileCharsetCombo.setText(outputFileCharset);
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
     * Get saved file
     *
     * @param shell Shell
     * @return File
     */
    private static File getSavedDir(Shell shell) {

        DirectoryDialog dialog = new DirectoryDialog(shell, SWT.SAVE | SWT.APPLICATION_MODAL);
        String filepath = CommonUIPlugin.getSettingValue(EXPORT_FILE_PATH_KEY);
        if (null != filepath) {
            dialog.setFilterPath(filepath);
        }

        String filePath = dialog.open();
        if (filePath == null) {
            return null;
        } else {
            File file = new File(filePath);
            if (file != null) {
                CommonUIPlugin.putSettingValue(EXPORT_FILE_PATH_KEY, file.getParent());
            }
            return file;
        }
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

        if (filePathText != null && filePathText.getText().length() < 1) {
            setErrorMessage(Messages.errExportSelectFile);
            filePathText.setFocus();
            return false;
        }

        if (fileTypeCombo != null && fileTypeCombo.getText().length() < 1) {
            setErrorMessage(Messages.errExportFileType);
            fileTypeCombo.setFocus();
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
        defaultFilePath = filePathText.getText().trim();
        defaultFileName = fileNameText.getText().trim();
        String fileType = fileTypeCombo.getText().trim();
        defaultFileExtName = fileType.substring(fileType.indexOf("."), fileType.indexOf(")"));
        outputFileFullName =
                defaultFilePath + File.separator + defaultFileName + defaultFileExtName;
        outputFileCharset = fileCharsetCombo.getText().trim();
        File file = new File(outputFileFullName);
        if (file.exists() && !CommonUITool.openConfirmBox(Messages.msgConfirmOverrideFile)) {
            return;
        }
        super.okPressed();
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
        if (filePathText == null
                || "".equals(filePathText.getText().trim())
                || fileTypeCombo == null
                || "".equals(fileTypeCombo.getText().trim())
                || fileCharsetCombo == null
                || "".equals(fileCharsetCombo.getText().trim())) {
            getButton(IDialogConstants.OK_ID).setEnabled(false);
        } else {
            getButton(IDialogConstants.OK_ID).setEnabled(true);
        }
    }

    /**
     * Get the outputFileCharset
     *
     * @return the outputFileCharset
     */
    public String getOutputFileCharset() {
        return outputFileCharset;
    }

    /**
     * Get the outputFileFullName
     *
     * @return the String
     */
    public String getOutputFileFullName() {
        return outputFileFullName;
    }

    /** @param defaultFileName the defaultFileName to set */
    public void setDefaultFileName(String defaultFileName) {
        this.defaultFileName = defaultFileName;
    }

    /** @param defaultFilePath the defaultFilePath to set */
    public void setDefaultFilePath(String defaultFilePath) {
        this.defaultFilePath = defaultFilePath;
    }

    /** @param defaultFileExtName the defaultFileExtName to set */
    public void setDefaultFileExtName(String defaultFileExtName) {
        this.defaultFileExtName = defaultFileExtName;
    }

    /**
     * Get the defaultFilePath
     *
     * @return the defaultFilePath
     */
    public String getDefaultFilePath() {
        return defaultFilePath;
    }

    /**
     * Get the defaultFileName
     *
     * @return the defaultFileName
     */
    public String getDefaultFileName() {
        return defaultFileName;
    }

    /**
     * Get the defaultFileExtName
     *
     * @return the defaultFileExtName
     */
    public String getDefaultFileExtName() {
        return defaultFileExtName;
    }

    /** @param outputFileCharset the outputFileCharset to set */
    public void setOutputFileCharset(String outputFileCharset) {
        this.outputFileCharset = outputFileCharset;
    }
}
