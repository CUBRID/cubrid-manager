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
package com.cubrid.common.ui.cubrid.table.dialog.exp;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.cubrid.table.progress.ExportConfig;
import com.cubrid.common.ui.spi.StatusInfo;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

/**
 * Choose Export Type Page
 *
 * @author Kevin.Wang
 * @version 1.0 - Jul 30, 2012 created by Kevin.Wang
 */
public class ExportTypePage extends ExportWizardPage {

    public static final String PAGE_NAME = ExportTypePage.class.getName();

    private Button fileButton;
    private Button historyButton;
    private Button loadDBButton;

    private Combo historyCombo;
    private Button renameButton;
    private Button deleteButton;

    /**
     * The constructor
     *
     * @param database
     */
    protected ExportTypePage() {
        super(PAGE_NAME, Messages.exportShellTitle, null);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FormLayout());
        setTitle(Messages.titleExportStep1);
        setDescription(Messages.exportWizardTypeDescription);
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

        fileButton = new Button(leftComposite, SWT.RADIO);
        fileButton.setText(Messages.exportWizardType1);
        fileButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        fileButton.addSelectionListener(
                new SelectionListener() {
                    public void widgetDefaultSelected(SelectionEvent e) {
                        widgetSelected(e);
                    }

                    public void widgetSelected(SelectionEvent e) {
                        historyButton.setSelection(!fileButton.getSelection());
                        loadDBButton.setSelection(!fileButton.getSelection());
                        changeHistoryCompStatus();
                    }
                });

        Group fileLabelGroup = new Group(leftComposite, SWT.None);
        fileLabelGroup.setLayoutData(CommonUITool.createGridData(1, 1, 370, 100));
        fileLabelGroup.setLayout(new FillLayout());

        Label fileLabel = new Label(fileLabelGroup, SWT.WRAP);
        fileLabel.setText(Messages.exportWizardTypeDescription1);

        Label separator1Label = new Label(leftComposite, SWT.None);
        separator1Label.setLayoutData(CommonUITool.createGridData(1, 1, 0, 20));

        historyButton = new Button(leftComposite, SWT.RADIO);
        historyButton.setText(Messages.exportWizardType3);
        historyButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        historyButton.addSelectionListener(
                new SelectionListener() {
                    public void widgetDefaultSelected(SelectionEvent e) {
                        widgetSelected(e);
                    }

                    public void widgetSelected(SelectionEvent e) {
                        fileButton.setSelection(!historyButton.getSelection());
                        loadDBButton.setSelection(!historyButton.getSelection());
                        changeHistoryCompStatus();
                        setPageComplete(true);
                    }
                });

        Group historyLabelGroup = new Group(leftComposite, SWT.None);
        historyLabelGroup.setLayoutData(CommonUITool.createGridData(1, 1, 370, 100));
        historyLabelGroup.setLayout(new FillLayout());

        Label historyLabel = new Label(historyLabelGroup, SWT.WRAP);
        historyLabel.setText(Messages.exportWizardTypeDescription3);

        Composite historyComposite = new Composite(leftComposite, SWT.None);
        historyComposite.setLayout(new GridLayout(3, false));
        historyComposite.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

        historyCombo = new Combo(historyComposite, SWT.READ_ONLY);
        historyCombo.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        historyCombo.setEnabled(false);
        historyCombo.addListener(
                SWT.Modify,
                new Listener() {
                    public void handleEvent(Event event) {
                        ExportConfig historyExportConfigModel =
                                ExportConfigManager.getInstance().getConfig(historyCombo.getText());
                        if (historyExportConfigModel != null) {
                            ExportConfig exportConfigModel =
                                    cloneExportConfigModel(historyExportConfigModel);
                            getExportDataWizardWizard().setConfigModel(exportConfigModel);
                        }
                        firePageStatusChanged(new StatusInfo(IStatus.INFO, ""));
                        setPageComplete(true);
                    }
                });

        final ExportConfigManager configManager = ExportConfigManager.getInstance();
        List<ExportConfig> configList = configManager.getAllConfigs();
        for (ExportConfig model : configList) {
            historyCombo.add(model.getName());
        }

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
                        ExportConfig model = configManager.getConfig(historyName);
                        if (model == null) {
                            return;
                        }
                        InputDialog dialog =
                                new InputDialog(
                                        getShell(),
                                        Messages.titleExportRenameDialog,
                                        Messages.descExportRenameDialog,
                                        historyName,
                                        new IInputValidator() {
                                            public String isValid(String newText) {
                                                if (newText == null
                                                        || newText.trim().length() == 0) {
                                                    return Messages
                                                            .msgExportRenamePleaseInputNewName;
                                                }
                                                if (configManager.getConfig(newText) != null) {
                                                    return Messages.msgExportRenameAlreadyExists;
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
                    public void widgetDefaultSelected(SelectionEvent e) {
                        widgetSelected(e);
                    }

                    public void widgetSelected(SelectionEvent e) {
                        if (!CommonUITool.openConfirmBox(Messages.confirmDeleteExportHistory)) {
                            return;
                        }

                        String historyName = historyCombo.getText();
                        if (historyName == null || historyName.trim().length() == 0) {
                            return;
                        }
                        configManager.removeConfig(configManager.getConfig(historyName));
                        configManager.saveConfigs();
                        historyCombo.remove(historyName);
                    }
                });

        loadDBButton = new Button(rightComposite, SWT.RADIO);
        loadDBButton.setText(Messages.exportWizardType2);
        loadDBButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        loadDBButton.addSelectionListener(
                new SelectionListener() {
                    public void widgetDefaultSelected(SelectionEvent e) {
                        widgetSelected(e);
                    }

                    public void widgetSelected(SelectionEvent e) {
                        fileButton.setSelection(!loadDBButton.getSelection());
                        historyButton.setSelection(!loadDBButton.getSelection());
                        changeHistoryCompStatus();

                        historyButton.setSelection(false);
                        fileButton.setSelection(false);
                    }
                });

        Group loadDBLabelGroup = new Group(rightComposite, SWT.None);
        loadDBLabelGroup.setLayoutData(CommonUITool.createGridData(1, 1, 370, 100));
        loadDBLabelGroup.setLayout(new FillLayout());

        Label loadDBLabel = new Label(loadDBLabelGroup, SWT.WRAP);
        loadDBLabel.setText(Messages.exportWizardTypeDescription2);
    }

    @Override
    protected void afterShowCurrentPage(PageChangedEvent event) {
        fileButton.setSelection(true);
        historyButton.setSelection(false);
        loadDBButton.setSelection(false);
        historyCombo.select(0);
        historyCombo.setEnabled(false);
        renameButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    /**
     * When leave current page
     *
     * @param event PageChangingEvent
     */
    protected void handlePageLeaving(PageChangingEvent event) {
        if (historyButton.getSelection() && StringUtil.isEmpty(historyCombo.getText())) {
            event.doit = false;
            firePageStatusChanged(
                    new StatusInfo(IStatus.ERROR, Messages.exportWizardTypePageErrMsg1));
            return;
        }
        if (fileButton.getSelection() || loadDBButton.getSelection()) {
            getExportDataWizardWizard().setConfigModel(new ExportConfig());
            getExportConfig().setExportType(getExportType());
        } else {
            ExportConfig historyExportConfigModel =
                    ExportConfigManager.getInstance().getConfig(historyCombo.getText());
            if (historyExportConfigModel != null) {
                ExportConfig exportConfigModel = cloneExportConfigModel(historyExportConfigModel);
                getExportDataWizardWizard().setConfigModel(exportConfigModel);
            }
        }
    }

    /** Change the history composite status */
    private void changeHistoryCompStatus() {
        if (historyButton.getSelection()) {
            historyCombo.setEnabled(true);
            if (historyCombo.getItemCount() > 0) {
                historyCombo.select(0);
                renameButton.setEnabled(true);
                deleteButton.setEnabled(true);
            } else {
                renameButton.setEnabled(false);
                deleteButton.setEnabled(false);
            }
        } else {
            historyCombo.setEnabled(false);
            renameButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }
    }

    /**
     * fire page changed
     *
     * @param status IStatus
     */
    private void firePageStatusChanged(IStatus status) {
        if (status.getSeverity() == IStatus.INFO) {
            setErrorMessage(null);
            setMessage(status.getMessage());
            setPageComplete(true);
        } else {
            setErrorMessage(status.getMessage());
            setPageComplete(false);
        }
    }

    /**
     * Get the Export type
     *
     * @return
     */
    public int getExportType() {
        if (fileButton.getSelection()) {
            return ExportConfig.EXPORT_TO_FILE;
        } else if (historyButton.getSelection()) {
            return ExportConfig.EXPORT_FROM_HISTORY;
        } else {
            return ExportConfig.EXPORT_TO_LOADDB;
        }
    }

    /**
     * clone ExportConfigModel from history
     *
     * @return
     */
    private ExportConfig cloneExportConfigModel(ExportConfig exportConfig) {
        return exportConfig.clone();
    }
}
