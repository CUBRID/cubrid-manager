/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.cubrid.table.dialog.imp;

import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * Choose Import Type Page
 *
 * @author Kevin.Wang
 * @version 1.0 - Jul 30, 2012 created by Kevin.Wang
 */
public class ImportTypePage extends AbsImportSettingPage {

    public static final String PAGE_NAME = ImportTypePage.class.getName();

    private Button sqlButton;
    private Button excelButton;
    private Button txtButton;
    private Button historyButton;

    private Combo historyCombo;
    private Button renameButton;
    private Button deleteButton;

    /**
     * The constructor
     *
     * @param database
     */
    protected ImportTypePage() {
        super(PAGE_NAME, Messages.importShellTitle, null);
        setTitle(Messages.titleImportStep1);
        setMessage(Messages.msgChooseImportType);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FormLayout());
        setControl(container);

        Composite leftComposite = new Composite(container, SWT.NONE);
        FormData leftData = new FormData();
        leftData.top = new FormAttachment(0, 5);
        leftData.bottom = new FormAttachment(100, 0);
        leftData.left = new FormAttachment(0, 5);
        leftData.right = new FormAttachment(50, 0);
        GridLayout leftLayout = new GridLayout();
        leftLayout.verticalSpacing = 0;
        leftComposite.setLayout(leftLayout);
        leftComposite.setLayoutData(leftData);

        Composite rightComposite = new Composite(container, SWT.NONE);
        FormData rightData = new FormData();
        rightData.top = new FormAttachment(0, 5);
        rightData.bottom = new FormAttachment(100, 0);
        rightData.left = new FormAttachment(50, 0);
        rightData.right = new FormAttachment(100, -5);
        GridLayout rightLayout = new GridLayout();
        rightLayout.verticalSpacing = 0;
        rightComposite.setLayout(rightLayout);
        rightComposite.setLayoutData(rightData);

        sqlButton = new Button(leftComposite, SWT.RADIO);
        sqlButton.setText(Messages.btnImportSQL);
        sqlButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        sqlButton.addSelectionListener(
                new SelectionListener() {
                    public void widgetDefaultSelected(SelectionEvent e) {
                        widgetSelected(e);
                    }

                    public void widgetSelected(SelectionEvent e) {
                        excelButton.setSelection(!sqlButton.getSelection());
                        txtButton.setSelection(!sqlButton.getSelection());
                        historyButton.setSelection(!sqlButton.getSelection());
                        historyCombo.setEnabled(false);
                        deleteButton.setEnabled(false);
                        renameButton.setEnabled(false);
                        validate();
                    }
                });

        Group sqlLabelGroup = new Group(leftComposite, SWT.None);
        sqlLabelGroup.setLayoutData(CommonUITool.createGridData(1, 1, 370, 80));
        sqlLabelGroup.setLayout(new FillLayout());

        Label sqlLabel = new Label(sqlLabelGroup, SWT.WRAP);
        sqlLabel.setText(Messages.lblImportSQL);

        Label separator1Label = new Label(leftComposite, SWT.None);
        separator1Label.setLayoutData(CommonUITool.createGridData(1, 1, 0, 15));

        {
            Label separator4Label = new Label(leftComposite, SWT.None);
            separator4Label.setLayoutData(CommonUITool.createGridData(1, 1, 0, 15));

            excelButton = new Button(leftComposite, SWT.RADIO);
            excelButton.setText(Messages.btnImportExcel);
            excelButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
            excelButton.addSelectionListener(
                    new SelectionListener() {
                        public void widgetDefaultSelected(SelectionEvent e) {
                            widgetSelected(e);
                        }

                        public void widgetSelected(SelectionEvent e) {
                            sqlButton.setSelection(!excelButton.getSelection());
                            txtButton.setSelection(!excelButton.getSelection());
                            historyButton.setSelection(!excelButton.getSelection());
                            historyCombo.setEnabled(false);
                            deleteButton.setEnabled(false);
                            renameButton.setEnabled(false);
                            validate();
                        }
                    });

            Group excelLabelGroup = new Group(leftComposite, SWT.None);
            excelLabelGroup.setLayoutData(CommonUITool.createGridData(1, 1, 370, 80));
            excelLabelGroup.setLayout(new FillLayout());

            Label excelLabel = new Label(excelLabelGroup, SWT.WRAP);
            excelLabel.setText(Messages.lblImportExcel);

            Label separator2Label = new Label(leftComposite, SWT.None);
            separator2Label.setLayoutData(CommonUITool.createGridData(1, 1, 0, 15));
        }

        // Txt
        {
            txtButton = new Button(rightComposite, SWT.RADIO);
            txtButton.setText(Messages.btnImportTxt);
            txtButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
            txtButton.addSelectionListener(
                    new SelectionListener() {
                        public void widgetDefaultSelected(SelectionEvent e) {
                            widgetSelected(e);
                        }

                        public void widgetSelected(SelectionEvent e) {
                            sqlButton.setSelection(!txtButton.getSelection());
                            excelButton.setSelection(!txtButton.getSelection());
                            historyButton.setSelection(!txtButton.getSelection());
                            historyCombo.setEnabled(false);
                            deleteButton.setEnabled(false);
                            renameButton.setEnabled(false);
                            validate();
                        }
                    });

            Group txtLabelGroup = new Group(rightComposite, SWT.None);
            txtLabelGroup.setLayoutData(CommonUITool.createGridData(1, 1, 370, 80));
            txtLabelGroup.setLayout(new FillLayout());

            Label txtLabel = new Label(txtLabelGroup, SWT.WRAP);
            txtLabel.setText(Messages.lblImportTxt);

            Label separator3Label = new Label(rightComposite, SWT.None);
            separator3Label.setLayoutData(CommonUITool.createGridData(1, 1, 0, 15));
        }

        // History Button
        {
            Label separator4Label = new Label(rightComposite, SWT.None);
            separator4Label.setLayoutData(CommonUITool.createGridData(1, 1, 0, 15));

            historyButton = new Button(rightComposite, SWT.RADIO);
            historyButton.setText(Messages.btnImportHistory);
            historyButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
            historyButton.addSelectionListener(
                    new SelectionListener() {
                        public void widgetDefaultSelected(SelectionEvent e) {
                            widgetSelected(e);
                        }

                        public void widgetSelected(SelectionEvent e) {
                            sqlButton.setSelection(!historyButton.getSelection());
                            excelButton.setSelection(!historyButton.getSelection());
                            txtButton.setSelection(!historyButton.getSelection());
                            historyCombo.setEnabled(historyButton.getSelection());

                            if (historyButton.getSelection()) {
                                loadHistory();
                            }
                            validate();
                        }
                    });

            Group historyLabelGroup = new Group(rightComposite, SWT.None);
            historyLabelGroup.setLayoutData(CommonUITool.createGridData(1, 1, 370, 80));
            historyLabelGroup.setLayout(new FillLayout());

            Label historyLabel = new Label(historyLabelGroup, SWT.WRAP);
            historyLabel.setText(Messages.lblImportHistory);

            Composite historyComposite = new Composite(rightComposite, SWT.None);
            historyComposite.setLayout(new GridLayout(3, false));
            historyComposite.setLayoutData(
                    CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

            historyCombo = new Combo(historyComposite, SWT.READ_ONLY);
            historyCombo.setLayoutData(
                    CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
            historyCombo.setEnabled(false);
            historyCombo.addSelectionListener(
                    new SelectionListener() {
                        public void widgetSelected(SelectionEvent e) {
                            widgetDefaultSelected(e);
                        }

                        public void widgetDefaultSelected(SelectionEvent e) {
                            validate();
                        }
                    });
            loadHistory();

            final ImportConfigManager configManager = ImportConfigManager.getInstance();

            renameButton = new Button(historyComposite, SWT.None);
            renameButton.setText(Messages.btnRenameHistory);
            renameButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
            renameButton.setEnabled(false);
            renameButton.addSelectionListener(
                    new SelectionListener() {
                        public void widgetDefaultSelected(SelectionEvent e) {
                            widgetSelected(e);
                        }

                        public void widgetSelected(SelectionEvent e) {
                            String historyName = historyCombo.getText();
                            if (historyName == null || historyName.trim().length() == 0) {
                                return;
                            }
                            ImportConfig model = configManager.getConfig(historyName);
                            if (model == null) {
                                return;
                            }
                            InputDialog dialog =
                                    new InputDialog(
                                            getShell(),
                                            Messages.titleRenameDialog,
                                            Messages.descRenameDialog,
                                            historyName,
                                            new IInputValidator() {
                                                public String isValid(String newText) {
                                                    if (newText == null
                                                            || newText.trim().length() == 0) {
                                                        return Messages.msgRenamePleaseInputNewName;
                                                    }
                                                    if (configManager.getConfig(newText) != null) {
                                                        return Messages.msgRenameAlreadyExists;
                                                    }
                                                    return null;
                                                }
                                            });
                            if (dialog.open() == IDialogConstants.OK_ID) {
                                String newName = dialog.getValue();
                                model.setName(newName);
                                configManager.saveConfigs();
                                historyCombo.remove(historyName);
                                historyCombo.add(newName);
                                historyCombo.setText(newName);
                            }
                        }
                    });
            deleteButton = new Button(historyComposite, SWT.None);
            deleteButton.setText(Messages.btnDeleteHistory);
            deleteButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
            deleteButton.setEnabled(false);
            deleteButton.addSelectionListener(
                    new SelectionListener() {
                        public void widgetSelected(SelectionEvent e) {
                            if (!CommonUITool.openConfirmBox(Messages.confirmDeleteImportHistory)) {
                                return;
                            }

                            boolean result =
                                    ImportConfigManager.getInstance()
                                            .removeConfig(historyCombo.getText());
                            if (result) {
                                historyCombo.remove(historyCombo.getText());
                                validate();
                            }
                        }

                        public void widgetDefaultSelected(SelectionEvent e) {
                            widgetDefaultSelected(e);
                        }
                    });
        }
        getShell().setMinimumSize(880, 550);
    }

    /** Verify the setting */
    protected boolean validate() {
        setPageComplete(false);

        if (historyButton.getSelection()) {
            if (historyCombo.getSelectionIndex() < 0) {
                deleteButton.setEnabled(false);
                renameButton.setEnabled(false);
                return false;
            } else {
                ImportConfig importConfig =
                        ImportConfigManager.getInstance().getConfig(historyCombo.getText());
                if (importConfig != null) {
                    /*Clone a new object*/
                    ImportConfig clonedObj = importConfig.clone();
                    getImportDataWizard().setImportConfig(clonedObj);
                }
                deleteButton.setEnabled(true);
                renameButton.setEnabled(true);
            }
        } else {
            ImportConfig importConfig = new ImportConfig();
            importConfig.setImportType(getImportType());
            getImportDataWizard().setImportConfig(importConfig);
        }

        setPageComplete(true);
        return true;
    }

    protected void afterShowCurrentPage(PageChangedEvent event) {
        sqlButton.setSelection(true);
        historyButton.setSelection(false);
        excelButton.setSelection(false);
        txtButton.setSelection(false);
        historyCombo.select(0);
        historyCombo.setEnabled(false);
        renameButton.setEnabled(false);
        deleteButton.setEnabled(false);

        validate();
        
        getShell().setMinimumSize(880, 550);
    }

    protected void handlePageLeaving(PageChangingEvent event) {
        if (!validate()) {
            event.doit = false;
            return;
        }
    }

    /**
     * Get the import type
     *
     * @return
     */
    private int getImportType() {
        if (sqlButton.getSelection()) {
            return ImportConfig.IMPORT_FROM_SQL;
        } else if (excelButton.getSelection()) {
            return ImportConfig.IMPORT_FROM_EXCEL;
        } else if (txtButton.getSelection()) {
            return ImportConfig.IMPORT_FROM_TXT;
        }
        return ImportConfig.IMPORT_FROM_EXCEL;
    }

    private void loadHistory() {
        historyCombo.removeAll();
        List<ImportConfig> importConfigList = ImportConfigManager.getInstance().getAllConfigs();
        for (int i = importConfigList.size() - 1; i >= 0; i--) {
            ImportConfig importConfig = importConfigList.get(i);
            historyCombo.add(importConfig.getName());
        }
        historyCombo.select(0);
    }
}
