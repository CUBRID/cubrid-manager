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
package com.cubrid.cubridmanager.ui.cubrid.database.dialog;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.FileUtil;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.progress.CommonTaskJobExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.ITaskExecutorInterceptor;
import com.cubrid.common.ui.spi.progress.JobFamily;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.progress.TaskJobExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.database.task.CheckDirTask;
import com.cubrid.cubridmanager.core.cubrid.database.task.CheckFileTask;
import com.cubrid.cubridmanager.core.cubrid.database.task.GetBackupVolInfoTask;
import com.cubrid.cubridmanager.core.cubrid.database.task.RestoreDbTask;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;
import java.io.File;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

/**
 * Restore database will use this dialog to fill in the information
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class RestoreDatabaseDialog extends CMTitleAreaDialog
        implements SelectionListener, ModifyListener, ITaskExecutorInterceptor {

    private Text databaseNameText = null;
    private CubridDatabase database = null;
    private Button restoredDataTimeButton;
    private Spinner yearSpn;
    private Spinner monthSpn;
    private Spinner daySpn;
    private Spinner hourSpn;
    private Spinner minuteSpn;
    private Spinner secondSpn;
    private Button level2Button;
    private Text level2Text;
    private Text level1Text;
    private Button level1Button;
    private Button level0Button;
    private Text level0Text;
    private Button partialButton;
    private List<String> backupList = null;
    private Button showBackupInfoButton;
    private Button backupTimeButton;
    private Button selectTimeButton;
    private Button level1BrowseBtn;
    private Button level0BrowseBtn;
    private Button level2BrowseBtn;
    private Button selectBackupButton;
    private Button dbPathButton;
    private Text dbPathText;
    private Button dbPathBrowseBtn;

    private boolean isLocalServer = false;
    private static final String KEY_LEVEL_FILE = "RestoreDatabaseDialog.LEVEL_FILE_";
    private static final String KEY_RECOVERY_PATH = "RestoreDatabaseDialog.RECOVERY_PATH_";

    /**
     * The constructor
     *
     * @param parentShell
     */
    public RestoreDatabaseDialog(Shell parentShell) {
        super(parentShell);
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
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        isLocalServer = database.getServer().getServerInfo().isLocalServer();

        createDatabseNameGroup(composite);
        createRestoreDataGroup(composite);
        if (CompatibleUtil.isSupportRestorePath(database.getServer().getServerInfo())) {
            createRestorePathGroup(composite);
        }
        createPartialGroup(composite);

        setTitle(Messages.titleRestoreDbDialog);
        setMessage(Messages.msgRestoreDbDialog);
        initial();
        return parentComp;
    }

    /**
     * Create database name group
     *
     * @param parent the parent composite
     */
    private void createDatabseNameGroup(Composite parent) {
        Group databaseNameGroup = new Group(parent, SWT.NONE);
        databaseNameGroup.setText(Messages.grpDbName);
        databaseNameGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        databaseNameGroup.setLayout(layout);

        Label databaseNameLabel = new Label(databaseNameGroup, SWT.LEFT | SWT.WRAP);
        databaseNameLabel.setText(Messages.lblDbNameRestore);
        databaseNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        databaseNameText = new Text(databaseNameGroup, SWT.BORDER);
        if (database != null) {
            databaseNameText.setText(database.getLabel());
        }
        databaseNameText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        databaseNameText.setEditable(false);
    }

    /**
     * Create restore data group
     *
     * @param parent the parent composite
     */
    private void createRestoreDataGroup(Composite parent) {
        Group restoreDataGroup = new Group(parent, SWT.NONE);
        restoreDataGroup.setText(Messages.grpRestoredData);
        restoreDataGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout layout = new GridLayout();
        restoreDataGroup.setLayout(layout);
        createDataTimeComp(restoreDataGroup);
        createBackupInfoComp(restoreDataGroup);
    }

    /**
     * Create restored date and time information composite
     *
     * @param parent the parent composite
     */
    private void createDataTimeComp(Composite parent) {
        Composite dataTimeComp = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 9;
        layout.horizontalSpacing = 2;
        dataTimeComp.setLayout(layout);
        dataTimeComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        selectTimeButton = new Button(dataTimeComp, SWT.LEFT | SWT.CHECK);
        selectTimeButton.setText(Messages.btnSelectDateAndTime);
        selectTimeButton.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 9, 1, -1, -1));
        selectTimeButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (selectTimeButton.getSelection()) {
                            changeDateTimeBtnStatus(true);
                        } else {
                            changeDateTimeBtnStatus(false);
                        }
                    }
                });

        backupTimeButton = new Button(dataTimeComp, SWT.LEFT | SWT.RADIO);
        backupTimeButton.setText(Messages.btnBackupTime);
        backupTimeButton.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 9, 1, -1, -1));
        backupTimeButton.addSelectionListener(this);

        restoredDataTimeButton = new Button(dataTimeComp, SWT.LEFT | SWT.RADIO);
        restoredDataTimeButton.setText(Messages.btnRestoreDate);
        restoredDataTimeButton.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 9, 1, -1, -1));
        restoredDataTimeButton.addSelectionListener(this);

        Label dateLabel = new Label(dataTimeComp, SWT.LEFT);
        dateLabel.setText(Messages.lblDate);
        dateLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        yearSpn = new Spinner(dataTimeComp, SWT.BORDER);
        yearSpn.setMinimum(1);
        Calendar cal = Calendar.getInstance();
        yearSpn.setMaximum(cal.get(Calendar.YEAR));
        yearSpn.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        yearSpn.addSelectionListener(this);
        yearSpn.addModifyListener(this);

        monthSpn = new Spinner(dataTimeComp, SWT.BORDER);
        monthSpn.setMinimum(1);
        monthSpn.setMaximum(12);
        monthSpn.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        monthSpn.addSelectionListener(this);
        monthSpn.addModifyListener(this);

        daySpn = new Spinner(dataTimeComp, SWT.BORDER);
        daySpn.setMinimum(1);
        daySpn.setMaximum(31);
        daySpn.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        daySpn.addSelectionListener(this);
        daySpn.addModifyListener(this);

        Label timeLabel = new Label(dataTimeComp, SWT.LEFT);
        timeLabel.setText(Messages.lblTime);
        timeLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        hourSpn = new Spinner(dataTimeComp, SWT.BORDER);
        hourSpn.setMinimum(0);
        hourSpn.setMaximum(23);
        hourSpn.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        hourSpn.addSelectionListener(this);
        hourSpn.addModifyListener(this);

        minuteSpn = new Spinner(dataTimeComp, SWT.BORDER);
        minuteSpn.setMinimum(0);
        minuteSpn.setMaximum(59);
        minuteSpn.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        minuteSpn.addSelectionListener(this);
        minuteSpn.addModifyListener(this);

        secondSpn = new Spinner(dataTimeComp, SWT.BORDER);
        secondSpn.setMinimum(0);
        secondSpn.setMaximum(59);
        secondSpn.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        secondSpn.addSelectionListener(this);
        secondSpn.addModifyListener(this);
        Label label = new Label(dataTimeComp, SWT.NONE);
        label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    /**
     * Create available backup information composite
     *
     * @param parent the parent composite
     */
    private void createBackupInfoComp(Composite parent) {
        Composite backupInfoComp = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        backupInfoComp.setLayout(layout);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        backupInfoComp.setLayoutData(gridData);

        selectBackupButton = new Button(backupInfoComp, SWT.LEFT | SWT.CHECK);
        selectBackupButton.setText(Messages.btnSelectBackupInfo);
        selectBackupButton.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 3, 1, -1, -1));
        selectBackupButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (selectBackupButton.getSelection()) {
                            changeBackupBtnStatus(true);
                        } else {
                            changeBackupBtnStatus(false);
                        }
                    }
                });
        level2Button = new Button(backupInfoComp, SWT.LEFT | SWT.RADIO);
        level2Button.setText(Messages.btnLevel2File);
        level2Button.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        level2Button.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        changeBackupBtnStatus(true);
                    }
                });

        level2Text = new Text(backupInfoComp, SWT.BORDER);
        level2Text.setLayoutData(
                CommonUITool.createGridData(
                        GridData.FILL_HORIZONTAL, isLocalServer ? 1 : 2, 1, -1, -1));
        level2Text.addModifyListener(this);
        level2Text.setEnabled(false);

        if (isLocalServer) {
            level2BrowseBtn = new Button(backupInfoComp, SWT.NONE);
            level2BrowseBtn.setText(Messages.btnBrowse);
            level2BrowseBtn.setLayoutData(CommonUITool.createGridData(1, 1, 80, -1));
            level2BrowseBtn.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            selectFile(2);
                        }
                    });
        }

        level1Button = new Button(backupInfoComp, SWT.LEFT | SWT.RADIO);
        level1Button.setText(Messages.btnLevel1File);
        level1Button.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        level1Button.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        changeBackupBtnStatus(true);
                    }
                });

        level1Text = new Text(backupInfoComp, SWT.BORDER);
        level1Text.setLayoutData(
                CommonUITool.createGridData(
                        GridData.FILL_HORIZONTAL, isLocalServer ? 1 : 2, 1, -1, -1));
        level1Text.addModifyListener(this);
        level1Text.setEnabled(false);

        if (isLocalServer) {
            level1BrowseBtn = new Button(backupInfoComp, SWT.NONE);
            level1BrowseBtn.setText(Messages.btnBrowse);
            level1BrowseBtn.setLayoutData(CommonUITool.createGridData(1, 1, 80, -1));
            level1BrowseBtn.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            selectFile(1);
                        }
                    });
        }

        level0Button = new Button(backupInfoComp, SWT.LEFT | SWT.RADIO);
        level0Button.setText(Messages.btnLevel0File);
        level0Button.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        level0Button.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        changeBackupBtnStatus(true);
                    }
                });

        level0Text = new Text(backupInfoComp, SWT.BORDER);
        level0Text.setLayoutData(
                CommonUITool.createGridData(
                        GridData.FILL_HORIZONTAL, isLocalServer ? 1 : 2, 1, -1, -1));
        level0Text.addModifyListener(this);
        level0Text.setEnabled(false);

        if (isLocalServer) {
            level0BrowseBtn = new Button(backupInfoComp, SWT.NONE);
            level0BrowseBtn.setText(Messages.btnBrowse);
            level0BrowseBtn.setLayoutData(CommonUITool.createGridData(1, 1, 80, -1));
            level0BrowseBtn.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            selectFile(0);
                        }
                    });
        }

        showBackupInfoButton = new Button(backupInfoComp, SWT.CENTER | SWT.PUSH);
        showBackupInfoButton.setText(Messages.btnShowBackupInfo);
        showBackupInfoButton.setLayoutData(
                CommonUITool.createGridData(
                        GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_END, 3, 1, -1, -1));
        showBackupInfoButton.setEnabled(false);
        showBackupInfoButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        String level = "";
                        String path = "none";
                        int size = 0;
                        if (backupList == null) {
                            return;
                        }
                        if (backupList != null) {
                            size = backupList.size();
                        }
                        if (level0Button.getSelection()) {
                            level = "0";
                            if (size > 0 && level0Text.getText().equals(backupList.get(0))) {
                                path = level0Text.getText();
                            }
                        } else if (level1Button.getSelection()) {
                            level = "1";
                            if (size > 1 && level1Text.getText().equals(backupList.get(1))) {
                                path = level1Text.getText();
                            }
                        } else if (level2Button.getSelection()) {
                            level = "2";
                            if (size > 2 && level2Text.getText().equals(backupList.get(2))) {
                                path = level2Text.getText();
                            }
                        }
                        showBackupVolumeInfo(level, path);
                    }
                });
    }

    /**
     * Select the file by browse
     *
     * @param level The backup level
     */
    private void selectFile(int level) {
        String filePath = null;
        if (level == 0) {
            filePath = level0Text.getText();
        } else if (level == 1) {
            filePath = level1Text.getText();
        } else if (level == 2) {
            filePath = level2Text.getText();
        }
        if (filePath == null || filePath.trim().length() == 0) {
            filePath =
                    CubridManagerUIPlugin.getPluginDialogSettings()
                            .get(KEY_LEVEL_FILE + database.getId());
        }
        FileDialog dlg = new FileDialog(getShell(), SWT.OPEN | SWT.APPLICATION_MODAL);
        if (filePath != null && filePath.trim().length() > 0) {
            dlg.setFilterPath(filePath);
        }
        String newFilePath = dlg.open();
        if (newFilePath != null && newFilePath.trim().length() > 0) {
            if (level == 0) {
                level0Text.setText(newFilePath);
            } else if (level == 1) {
                level1Text.setText(newFilePath);
            } else if (level == 2) {
                level2Text.setText(newFilePath);
            }
            CubridManagerUIPlugin.getPluginDialogSettings()
                    .put(KEY_LEVEL_FILE + database.getId(), newFilePath);
        }
    }

    /**
     * Create partial recovery information group
     *
     * @param parent the parent composite
     */
    private void createPartialGroup(Composite parent) {
        Group partialGroup = new Group(parent, SWT.NONE);
        partialGroup.setText(Messages.grpPartialRecovery);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        partialGroup.setLayout(layout);
        partialGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        partialButton = new Button(partialGroup, SWT.LEFT | SWT.CHECK);
        partialButton.setText(Messages.btnPerformPartialRecovery);
        partialButton.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
    }

    /**
     * Create restore path information group
     *
     * @param parent the parent composite
     */
    private void createRestorePathGroup(Composite parent) {
        Group restorePathGroup = new Group(parent, SWT.NONE);
        restorePathGroup.setText(Messages.grpDbPath);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        restorePathGroup.setLayout(layout);
        restorePathGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        dbPathButton = new Button(restorePathGroup, SWT.LEFT | SWT.CHECK);
        dbPathButton.setText(Messages.btnDbPath);
        dbPathButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        dbPathButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (dbPathButton.getSelection()) {
                            dbPathText.setEnabled(true);
                            String dbDir =
                                    CubridManagerUIPlugin.getPluginDialogSettings()
                                            .get(KEY_RECOVERY_PATH + database.getId());
                            ServerInfo serverInfo = database.getServer().getServerInfo();
                            if (dbDir == null || dbDir.trim().length() == 0) {
                                dbDir = database.getDatabaseInfo().getDbDir();
                                if (serverInfo != null) {
                                    dbDir =
                                            FileUtil.changeSeparatorByOS(
                                                    dbDir, serverInfo.getServerOsInfo());
                                }
                            }
                            dbPathText.setText(dbDir);
                            if (dbPathBrowseBtn != null) {
                                boolean isLocalServer = serverInfo.isLocalServer();
                                dbPathBrowseBtn.setEnabled(isLocalServer);
                            }
                        } else {
                            dbPathText.setText("");
                            dbPathText.setEnabled(false);
                            if (dbPathBrowseBtn != null) {
                                dbPathBrowseBtn.setEnabled(false);
                            }
                        }
                    }
                });

        dbPathText = new Text(restorePathGroup, SWT.BORDER);
        dbPathText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        dbPathText.addModifyListener(this);
        dbPathText.setEnabled(false);

        if (isLocalServer) {
            dbPathBrowseBtn = new Button(restorePathGroup, SWT.NONE);
            dbPathBrowseBtn.setText(Messages.btnBrowse);
            dbPathBrowseBtn.setLayoutData(CommonUITool.createGridData(1, 1, 80, -1));
            dbPathBrowseBtn.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            String text = dbPathText.getText();
                            if (text == null || text.trim().length() == 0) {
                                text =
                                        CubridManagerUIPlugin.getPluginDialogSettings()
                                                .get(KEY_RECOVERY_PATH + database.getId());
                            }
                            DirectoryDialog dlg = new DirectoryDialog(getShell());
                            if (text != null) {
                                dlg.setFilterPath(text);
                            }
                            dlg.setText(Messages.msgSelectDir);
                            dlg.setMessage(Messages.msgSelectDir);
                            String newDir = dlg.open();
                            if (newDir != null && newDir.trim().length() > 0) {
                                dbPathText.setText(newDir);
                                CubridManagerUIPlugin.getPluginDialogSettings()
                                        .put(KEY_RECOVERY_PATH + database.getId(), newDir);
                            }
                        }
                    });
            dbPathBrowseBtn.setEnabled(false);
        }
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.titleRestoreDbDialog);
    }

    /**
     * Create buttons for button bar
     *
     * @param parent the parent composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(
                parent,
                IDialogConstants.OK_ID,
                com.cubrid.cubridmanager.ui.common.Messages.btnOK,
                true);
        if (backupList == null || backupList.isEmpty()) {
            getButton(IDialogConstants.OK_ID).setEnabled(false);
        }
        createButton(
                parent,
                IDialogConstants.CANCEL_ID,
                com.cubrid.cubridmanager.ui.common.Messages.btnCancel,
                false);
    }

    /**
     * When button press,call it
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            restoreDb();
        } else {
            super.buttonPressed(buttonId);
        }
    }

    /** Initial data */
    private void initial() {
        selectTimeButton.setSelection(true);
        backupTimeButton.setSelection(true);
        changeDateTimeBtnStatus(true);
        changeBackupBtnStatus(false);
        Calendar cal = Calendar.getInstance();
        yearSpn.setSelection(cal.get(Calendar.YEAR));
        monthSpn.setSelection(cal.get(Calendar.MONTH) + 1);
        daySpn.setSelection(cal.get(Calendar.DATE));
        hourSpn.setSelection(cal.get(Calendar.HOUR_OF_DAY));
        minuteSpn.setSelection(cal.get(Calendar.MINUTE));
        secondSpn.setSelection(cal.get(Calendar.SECOND));
        if (this.backupList != null && !this.backupList.isEmpty()) {
            selectBackupButton.setSelection(true);
            level0Button.setSelection(true);
            String path = backupList.get(0);
            path =
                    FileUtil.changeSeparatorByOS(
                            path, database.getServer().getServerInfo().getServerOsInfo());
            level0Text.setText(path);

            if (backupList.size() > 1) {
                level1Button.setSelection(false);
                path = backupList.get(1);
                path =
                        FileUtil.changeSeparatorByOS(
                                path, database.getServer().getServerInfo().getServerOsInfo());
                level1Text.setText(path);
            }
            if (backupList.size() > 2) {
                level2Button.setSelection(false);
                path = backupList.get(2);
                path =
                        FileUtil.changeSeparatorByOS(
                                path, database.getServer().getServerInfo().getServerOsInfo());
                level2Text.setText(path);
            }
            changeBackupBtnStatus(true);
        }
    }

    /**
     * Change date and time related button status
     *
     * @param isEnabled The boolean
     */
    private void changeDateTimeBtnStatus(boolean isEnabled) {
        if (isEnabled) {
            backupTimeButton.setEnabled(true);
            restoredDataTimeButton.setEnabled(true);
        } else {
            backupTimeButton.setEnabled(false);
            restoredDataTimeButton.setEnabled(false);
            yearSpn.setEnabled(false);
            monthSpn.setEnabled(false);
            daySpn.setEnabled(false);
            hourSpn.setEnabled(false);
            minuteSpn.setEnabled(false);
            secondSpn.setEnabled(false);
        }

        if (!isEnabled) {
            valid();
            return;
        }

        if (selectTimeButton.getSelection()) {
            backupTimeButton.setEnabled(true);
            restoredDataTimeButton.setEnabled(true);
            if (restoredDataTimeButton.getSelection()) {
                yearSpn.setEnabled(true);
                monthSpn.setEnabled(true);
                daySpn.setEnabled(true);
                hourSpn.setEnabled(true);
                minuteSpn.setEnabled(true);
                secondSpn.setEnabled(true);
            } else {
                yearSpn.setEnabled(false);
                monthSpn.setEnabled(false);
                daySpn.setEnabled(false);
                hourSpn.setEnabled(false);
                minuteSpn.setEnabled(false);
                secondSpn.setEnabled(false);
            }
            if (!backupTimeButton.getSelection() && !restoredDataTimeButton.getSelection()) {
                backupTimeButton.setSelection(true);
            }
        } else {
            backupTimeButton.setEnabled(false);
            restoredDataTimeButton.setEnabled(false);
            yearSpn.setEnabled(false);
            monthSpn.setEnabled(false);
            daySpn.setEnabled(false);
            hourSpn.setEnabled(false);
            minuteSpn.setEnabled(false);
            secondSpn.setEnabled(false);
        }
        valid();
    }

    /**
     * Change backup information button status
     *
     * @param isEnabled The boolean
     */
    private void changeBackupBtnStatus(boolean isEnabled) {

        if (isEnabled) {
            level0Button.setEnabled(true);
            level1Button.setEnabled(true);
            level2Button.setEnabled(true);
        } else {
            level0Button.setEnabled(false);
            level0Text.setEnabled(false);
            if (level0BrowseBtn != null) {
                level0BrowseBtn.setEnabled(false);
            }
            level1Button.setEnabled(false);
            level1Text.setEnabled(false);
            if (level1BrowseBtn != null) {
                level1BrowseBtn.setEnabled(false);
            }
            level2Button.setEnabled(false);
            level2Text.setEnabled(false);
            if (level2BrowseBtn != null) {
                level2BrowseBtn.setEnabled(false);
            }
            showBackupInfoButton.setEnabled(false);
        }
        if (!isEnabled) {
            valid();
            return;
        }

        ServerInfo serverInfo = database.getServer().getServerInfo();
        boolean isLocalServer = serverInfo != null && serverInfo.isLocalServer();
        String path = null;
        if (level0Button.getSelection()) {
            path = level0Text.getText();
            level0Text.setEnabled(true);
            if (level0BrowseBtn != null) {
                level0BrowseBtn.setEnabled(isLocalServer);
            }
            level1Text.setEnabled(false);
            if (level1BrowseBtn != null) {
                level1BrowseBtn.setEnabled(false);
            }
            level2Text.setEnabled(false);
            if (level2BrowseBtn != null) {
                level2BrowseBtn.setEnabled(false);
            }
        } else if (level1Button.getSelection()) {
            path = level1Text.getText();
            level1Text.setEnabled(true);
            if (level1BrowseBtn != null) {
                level1BrowseBtn.setEnabled(isLocalServer);
            }
            level0Text.setEnabled(false);
            if (level0BrowseBtn != null) {
                level0BrowseBtn.setEnabled(false);
            }
            level2Text.setEnabled(false);
            if (level2BrowseBtn != null) {
                level2BrowseBtn.setEnabled(false);
            }
        } else if (level2Button.getSelection()) {
            path = level2Text.getText();
            level2Text.setEnabled(true);
            if (level2BrowseBtn != null) {
                level2BrowseBtn.setEnabled(isLocalServer);
            }
            level1Text.setEnabled(false);
            if (level1BrowseBtn != null) {
                level1BrowseBtn.setEnabled(false);
            }
            level0Text.setEnabled(false);
            if (level0BrowseBtn != null) {
                level0BrowseBtn.setEnabled(false);
            }
        }
        if (path == null || path.trim().length() == 0) {
            showBackupInfoButton.setEnabled(false);
        } else {
            showBackupInfoButton.setEnabled(true);
        }
        valid();
    }

    /**
     * Listen to the widget select event
     *
     * @param event the selection event
     */
    public void widgetSelected(SelectionEvent event) {
        changeDateTimeBtnStatus(true);
    }

    /**
     * Check the validation
     *
     * @return <code>true</code> if it is valid;<code>false</code> otherwise
     */
    private boolean valid() {
        boolean isValidYear = true;
        boolean isValidMonth = true;
        boolean isValidDay = true;
        boolean isValidHour = true;
        boolean inValidMinute = true;
        boolean isValidSecond = true;
        boolean isValidLevel0Text = true;
        boolean isValidLevel1Text = true;
        boolean isValidLevel2Text = true;
        boolean isValidRestorePath = true;
        boolean isValidData = false;
        if (restoredDataTimeButton.getSelection()) {
            Calendar cal = Calendar.getInstance();
            int year = yearSpn.getSelection();
            isValidYear = year > 0 && year <= cal.get(Calendar.YEAR);
            int month = monthSpn.getSelection();
            isValidMonth = month > 0 && month <= 12;
            int day = daySpn.getSelection();
            isValidDay = day > 0 && day <= 31;
            int hour = hourSpn.getSelection();
            isValidHour = hour >= 0 && hour <= 23;
            int minute = minuteSpn.getSelection();
            inValidMinute = minute >= 0 && minute < 60;
            int second = secondSpn.getSelection();
            isValidSecond = second >= 0 && second < 60;
        }

        if (selectBackupButton.getSelection() && level2Button.getSelection()) {
            String path = level2Text.getText();
            isValidLevel2Text = ValidateUtil.isValidPathName(path);
            if (isValidLevel2Text && database.getServer().getServerInfo().isLocalServer()) {
                File file = new File(path);
                if (!file.exists()) {
                    isValidLevel2Text = false;
                }
            }
            isValidData = isValidLevel2Text;
        } else if (selectBackupButton.getSelection() && level1Button.getSelection()) {
            String path = level1Text.getText();
            isValidLevel1Text = ValidateUtil.isValidPathName(path);
            if (isValidLevel1Text && database.getServer().getServerInfo().isLocalServer()) {
                File file = new File(path);
                if (!file.exists()) {
                    isValidLevel1Text = false;
                }
            }
            isValidData = isValidLevel1Text;
        } else if (selectBackupButton.getSelection() && level0Button.getSelection()) {
            String path = level0Text.getText();
            isValidLevel0Text = ValidateUtil.isValidPathName(path);
            if (isValidLevel0Text && database.getServer().getServerInfo().isLocalServer()) {
                File file = new File(path);
                if (!file.exists()) {
                    isValidLevel0Text = false;
                }
            }
            isValidData = isValidLevel0Text;
        }
        showBackupInfoButton.setEnabled(isValidData);
        if (dbPathButton != null && dbPathButton.getSelection()) {
            String path = dbPathText.getText();
            isValidRestorePath = ValidateUtil.isValidPathName(path);
            if (isValidRestorePath && database.getServer().getServerInfo().isLocalServer()) {
                File file = new File(path);
                if (!file.exists()) {
                    isValidRestorePath = false;
                }
            }
        }
        if (!isValidYear) {
            setErrorMessage(Messages.errYear);
            changeOkButtonStatus(false);
            return false;
        }
        if (!isValidMonth) {
            setErrorMessage(Messages.errMonth);
            changeOkButtonStatus(false);
            return false;
        }
        if (!isValidDay) {
            setErrorMessage(Messages.errDay);
            changeOkButtonStatus(false);
            return false;
        }
        if (!isValidHour) {
            setErrorMessage(Messages.errHour);
            changeOkButtonStatus(false);
            return false;
        }
        if (!inValidMinute) {
            setErrorMessage(Messages.errMinute);
            changeOkButtonStatus(false);
            return false;
        }
        if (!isValidSecond) {
            setErrorMessage(Messages.errSecond);
            changeOkButtonStatus(false);
            return false;
        }
        if (!isValidLevel2Text) {
            setErrorMessage(Messages.errLevel2File);
            changeOkButtonStatus(false);
            return false;
        }
        if (!isValidLevel1Text) {
            setErrorMessage(Messages.errLevel1File);
            changeOkButtonStatus(false);
            return false;
        }
        if (!isValidLevel0Text) {
            setErrorMessage(Messages.errLevel0File);
            changeOkButtonStatus(false);
            return false;
        }
        if (!isValidRestorePath) {
            setErrorMessage(Messages.errRecoveryPath);
            changeOkButtonStatus(false);
            return false;
        }
        if (!isValidData) {
            setErrorMessage(Messages.errNoSelectBackupInfo);
            changeOkButtonStatus(false);
            return false;
        }
        setErrorMessage(null);
        changeOkButtonStatus(true);
        return true;
    }

    /**
     * Listen to the widget default selection event
     *
     * @param event the selection event
     */
    public void widgetDefaultSelected(SelectionEvent event) {
        // empty
    }

    /**
     * Listen to the modify text event
     *
     * @param event the modify event
     */
    public void modifyText(ModifyEvent event) {
        valid();
    }

    /**
     * Change button stauts(enabled or disabled)
     *
     * @param isEnabled whether it is enabled
     */
    private void changeOkButtonStatus(boolean isEnabled) {
        if (getButton(IDialogConstants.OK_ID) != null) {
            getButton(IDialogConstants.OK_ID).setEnabled(isEnabled);
        }
    }

    /**
     * Show backup volume information
     *
     * @param level the level
     * @param path the path
     */
    private void showBackupVolumeInfo(String level, String path) {
        TaskExecutor taskExcutor =
                new TaskExecutor() {
                    public boolean exec(final IProgressMonitor monitor) {
                        Display display = Display.getDefault();
                        if (monitor.isCanceled()) {
                            return false;
                        }
                        monitor.beginTask(Messages.loadBackupVolInfo, IProgressMonitor.UNKNOWN);
                        for (ITask task : taskList) {
                            task.execute();
                            final String msg = task.getErrorMsg();
                            if (openErrorBox(getShell(), msg, monitor)) {
                                return false;
                            }
                            if (monitor.isCanceled()) {
                                return false;
                            }
                            if (task instanceof GetBackupVolInfoTask) {
                                GetBackupVolInfoTask getBackupVolInfoTask =
                                        (GetBackupVolInfoTask) task;
                                final String backupVolInfo =
                                        getBackupVolInfoTask.getDbBackupVolInfo();
                                if (backupVolInfo != null && backupVolInfo.length() > 0) {
                                    display.syncExec(
                                            new Runnable() {
                                                public void run() {
                                                    BackupDbVolumeInfoDialog
                                                            backupDbResultInfoDialog =
                                                                    new BackupDbVolumeInfoDialog(
                                                                            getShell());
                                                    backupDbResultInfoDialog.setResultInfoStr(
                                                            backupVolInfo);
                                                    backupDbResultInfoDialog.open();
                                                }
                                            });
                                }
                            }
                        }
                        return true;
                    }
                };
        String databaseName = databaseNameText.getText();
        GetBackupVolInfoTask getBackupVolInfoTask =
                new GetBackupVolInfoTask(database.getServer().getServerInfo());
        getBackupVolInfoTask.setDbName(databaseName);
        getBackupVolInfoTask.setLevel(level);
        getBackupVolInfoTask.setPath(path);
        taskExcutor.addTask(getBackupVolInfoTask);
        new ExecTaskWithProgress(taskExcutor).exec(true, true);
    }

    /** Restore database */
    private void restoreDb() {
        if (!valid()) {
            return;
        }
        TaskJobExecutor taskExec = new CommonTaskJobExec(this);
        String databaseName = databaseNameText.getText();
        ServerInfo serverInfo = database.getServer().getServerInfo();
        boolean isLocalServer = serverInfo.isLocalServer();
        RestoreDbTask restoreDbTask = new RestoreDbTask(database.getServer().getServerInfo());
        restoreDbTask.setDbName(databaseName);
        String level = "0";
        String path = "none";
        if (selectBackupButton.getSelection()) {
            if (level0Button.getSelection()) {
                level = "0";
                path = level0Text.getText();
            } else if (level1Button.getSelection()) {
                level = "1";
                path = level1Text.getText();
            } else if (level2Button.getSelection()) {
                level = "2";
                path = level2Text.getText();
            }
            if (!isLocalServer) {
                CheckFileTask checkFileTask = new CheckFileTask(serverInfo);
                checkFileTask.setFile(new String[] {path});
                checkFileTask.putData("filePath", path);
                taskExec.addTask(checkFileTask);
            }
        }
        if (selectTimeButton.getSelection()) {
            if (backupTimeButton.getSelection()) {
                restoreDbTask.setDate("backuptime");
            } else if (restoredDataTimeButton.getSelection()) {
                NumberFormat nf = NumberFormat.getInstance();
                nf.setMinimumIntegerDigits(2);
                String timeStr =
                        nf.format(hourSpn.getSelection())
                                + ":"
                                + nf.format(minuteSpn.getSelection())
                                + ":"
                                + nf.format(secondSpn.getSelection());
                String dateStr =
                        nf.format(daySpn.getSelection())
                                + "-"
                                + nf.format(monthSpn.getSelection())
                                + "-"
                                + yearSpn.getSelection();
                dateStr += ":" + timeStr;
                restoreDbTask.setDate(dateStr);
            }
        } else {
            restoreDbTask.setDate("none");
        }
        if (!CompatibleUtil.isSupportRestorePath(database.getServer().getServerInfo())) {
            path = "none";
        }
        restoreDbTask.setLevel(level);
        restoreDbTask.setPathName(path);

        if (partialButton.getSelection()) {
            restoreDbTask.setPartial(true);
        } else {
            restoreDbTask.setPartial(false);
        }
        if (dbPathButton == null || !dbPathButton.getSelection()) {
            restoreDbTask.setRecoveryPath("none");
        } else {
            restoreDbTask.setRecoveryPath(dbPathText.getText());
            if (!isLocalServer) {
                CheckDirTask checkDirTask = new CheckDirTask(serverInfo);
                checkDirTask.setDirectory(new String[] {dbPathText.getText()});
                taskExec.addTask(checkDirTask);
            }
        }

        taskExec.addTask(restoreDbTask);
        JobFamily jobFamily = new JobFamily();

        String serverName = database.getServer().getName();
        String dbName = database.getName();
        jobFamily.setServerName(serverName);
        jobFamily.setDbName(dbName);

        String jobName = Messages.msgRestoreDBRearJobName + " - " + dbName + "@" + serverName;
        taskExec.schedule(jobName, jobFamily, true, Job.SHORT);
    }

    /** @see com.cubrid.common.ui.spi.progress.ITaskExecutorInterceptor#completeAll() */
    public void completeAll() {
        CommonUITool.openInformationBox(
                getShell(), Messages.titleSuccess, Messages.msgRestoreSuccess);
    }

    /**
     * After a task has been executed, do some thing such as refresh.
     *
     * @param task the task
     * @return IStatus if complete refresh false if run into error
     */
    public IStatus postTaskFinished(ITask task) {
        if (task instanceof CheckFileTask) {
            CheckFileTask checkFileTask = (CheckFileTask) task;
            final String[] files = checkFileTask.getExistFiles();
            if (files == null || files.length == 0) {
                String filePath = (String) checkFileTask.getData("filePath");
                return new Status(
                        IStatus.ERROR,
                        CubridManagerUIPlugin.PLUGIN_ID,
                        Messages.bind(Messages.errBackupFileNoExist, filePath));
            }
        } else if (task instanceof CheckDirTask) {
            CheckDirTask checkDirTask = (CheckDirTask) task;
            final String[] dirs = checkDirTask.getNoExistDirectory();
            if (dirs != null && dirs.length > 0) {
                CreateDirDialog dialog = new CreateDirDialog(getShell());
                dialog.setDirs(dirs);
                if (dialog.open() != IDialogConstants.OK_ID) {
                    return Status.CANCEL_STATUS;
                }
            }
        }
        return Status.OK_STATUS;
    }

    /**
     * Get added CubridDatabase
     *
     * @return the CubridDatabase object
     */
    public CubridDatabase getDatabase() {
        return database;
    }

    /**
     * Set edited CubridDatabase
     *
     * @param database the CubridDatabase object
     */
    public void setDatabase(CubridDatabase database) {
        this.database = database;
    }

    /**
     * Set backup list
     *
     * @param backupList the backup list
     */
    public void setBackupList(List<String> backupList) {
        this.backupList = backupList;
    }
}
