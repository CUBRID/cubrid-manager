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
import com.cubrid.common.ui.spi.TableViewerSorter;
import com.cubrid.common.ui.spi.dialog.CMTrayDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.progress.JobFamily;
import com.cubrid.common.ui.spi.progress.TaskJobExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.cubrid.database.model.DbBackupHistoryInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DbBackupInfo;
import com.cubrid.cubridmanager.core.cubrid.database.task.BackupDbTask;
import com.cubrid.cubridmanager.core.cubrid.database.task.CheckDirTask;
import com.cubrid.cubridmanager.core.cubrid.database.task.CheckFileTask;
import com.cubrid.cubridmanager.core.cubrid.database.task.GetBackupVolInfoTask;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

/**
 * Backup database will use this dialog to fill in the information
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class BackupDatabaseDialog extends CMTrayDialog {

    private Text databaseNameText = null;
    private Text volumeNameText = null;
    private Text backupDirText = null;
    private CubridDatabase database = null;
    private CTabFolder tabFolder;
    private Combo backupLevelCombo;
    private Spinner spnThreadNum;
    private Button consistentButton;
    private Button archiveLogButton;
    private Button useCompressButton;
    private DbBackupInfo dbBackupInfo = null;
    private boolean isCanFinished = true;
    private String backupDir;
    private Button safeBackupButton;
    private boolean isReplication = false;

    private boolean isLocalServer = false;
    private static final String KEY_BACKUP_DB_DIR = "BackupDatabaseDialog.BACKUP_DB_DIR_";

    /**
     * The constructor
     *
     * @param parentShell
     */
    public BackupDatabaseDialog(Shell parentShell) {
        super(parentShell);
    }

    /**
     * Create the dialog area content
     *
     * @param parent the parent composite
     * @return the control
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);
        tabFolder = new CTabFolder(parentComp, SWT.NONE);
        tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        tabFolder.setLayout(layout);

        isLocalServer = database.getServer().getServerInfo().isLocalServer();

        CTabItem item = new CTabItem(tabFolder, SWT.NONE);
        item.setText(Messages.grpBackuInfo);
        Composite composite = createBackupInfoComp();
        item.setControl(composite);

        item = new CTabItem(tabFolder, SWT.NONE);
        item.setText(Messages.grpBackupHistoryInfo);
        composite = createBackupHistoryComp();
        item.setControl(composite);
        initial();
        return parentComp;
    }

    /**
     * Create backup information tab composite
     *
     * @return the composite
     */
    private Composite createBackupInfoComp() {
        /*For [TOOLS-3372], in CUBRID 9.2 or CUBRID 8.4.4, cannot specify the backup volume name*/
        String msgVolumeName =
                CompatibleUtil.isSupportBackupVolumeName(database.getDatabaseInfo())
                        ? Messages.lblVolName
                        : Messages.lblVolPath;

        Composite composite = new Composite(tabFolder, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        composite.setLayout(layout);

        Label databaseNameLabel = new Label(composite, SWT.LEFT);
        databaseNameLabel.setText(Messages.lblDbName);
        databaseNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        databaseNameText = new Text(composite, SWT.LEFT | SWT.BORDER);
        databaseNameText.setEditable(false);
        if (database == null) {
            return composite;
        }
        databaseNameText.setText(database.getLabel());
        databaseNameText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 3, 1, -1, -1));

        Label volumeNameLabel = new Label(composite, SWT.LEFT);
        volumeNameLabel.setText(msgVolumeName);
        volumeNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        volumeNameText = new Text(composite, SWT.LEFT | SWT.BORDER);
        volumeNameText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 3, 1, -1, -1));

        Label backupLevelLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        backupLevelLabel.setText(Messages.lblBackupLevel);
        backupLevelLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        backupLevelCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
        backupLevelCombo.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 3, 1, -1, -1));
        backupLevelCombo.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent event) {
                        changeVolumeName();
                    }

                    public void widgetDefaultSelected(SelectionEvent event) {
                        changeVolumeName();
                    }
                });

        Label backupDirLabel = new Label(composite, SWT.LEFT);
        backupDirLabel.setText(Messages.lblBackupDir);
        backupDirLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        backupDirText = new Text(composite, SWT.LEFT | SWT.BORDER);
        backupDirText.setLayoutData(
                CommonUITool.createGridData(
                        GridData.FILL_HORIZONTAL, isLocalServer ? 2 : 3, 1, -1, -1));

        if (isLocalServer) {
            Button selectTargetDirectoryButton = new Button(composite, SWT.NONE);
            selectTargetDirectoryButton.setText(Messages.btnBrowse);
            selectTargetDirectoryButton.setLayoutData(CommonUITool.createGridData(1, 1, 80, -1));
            selectTargetDirectoryButton.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            DirectoryDialog dlg = new DirectoryDialog(getShell());
                            String text = backupDirText.getText();
                            if (text == null || text.trim().length() == 0) {
                                text =
                                        CubridManagerUIPlugin.getPluginDialogSettings()
                                                .get(KEY_BACKUP_DB_DIR + database.getId());
                            }
                            if (text == null || text.trim().length() == 0) {
                                text = backupDir;
                            }
                            File file = new File(text);
                            if (!file.exists()) {
                                text = file.getParent();
                            }
                            dlg.setFilterPath(text);

                            dlg.setText(Messages.msgSelectDir);
                            dlg.setMessage(Messages.msgSelectDir);
                            String dir = dlg.open();
                            if (dir != null) {
                                backupDirText.setText(dir);
                                CubridManagerUIPlugin.getPluginDialogSettings()
                                        .put(KEY_BACKUP_DB_DIR + database.getId(), dir);
                            }
                        }
                    });
        }

        Label threadNumLabel = new Label(composite, SWT.LEFT);
        threadNumLabel.setText(Messages.lblParallelBackup);
        threadNumLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        spnThreadNum = new Spinner(composite, SWT.BORDER);
        spnThreadNum.setMaximum(Integer.MAX_VALUE);
        spnThreadNum.setLayoutData(CommonUITool.createGridData(2, 1, -1, -1));
        new Label(composite, SWT.NONE);

        consistentButton = new Button(composite, SWT.NONE | SWT.CHECK);
        consistentButton.setText(Messages.btnCheckConsistency);
        consistentButton.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 4, 1, -1, -1));

        archiveLogButton = new Button(composite, SWT.NONE | SWT.CHECK);
        archiveLogButton.setText(Messages.btnDeleteLog);
        archiveLogButton.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 4, 1, -1, -1));
        archiveLogButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (CompatibleUtil.isSupportHA(database.getServer().getServerInfo())
                                && isReplication()
                                && archiveLogButton.getSelection()) {
                            safeBackupButton.setSelection(true);
                        } else {
                            safeBackupButton.setSelection(false);
                        }
                    }
                });

        useCompressButton = new Button(composite, SWT.NONE | SWT.CHECK);
        useCompressButton.setText(Messages.btnCompressVol);
        useCompressButton.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 4, 1, -1, -1));

        safeBackupButton = new Button(composite, SWT.NONE | SWT.CHECK);
        safeBackupButton.setText(Messages.btnSafeBackup);
        safeBackupButton.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 4, 1, -1, -1));
        safeBackupButton.setEnabled(false);
        if (CompatibleUtil.isSupportHA(database.getServer().getServerInfo())
                || CompatibleUtil.isAfter840(database.getServer().getServerInfo())) {
            safeBackupButton.setVisible(false);
        }
        return composite;
    }

    /**
     * Create backup history information tab composite
     *
     * @return the composite
     */
    private Composite createBackupHistoryComp() {
        Composite composite = new Composite(tabFolder, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        composite.setLayout(layout);

        Label tipLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        tipLabel.setText(Messages.msgBackupHistoryList);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        tipLabel.setLayoutData(gridData);

        final String[] columnNameArr =
                new String[] {
                    Messages.tblColumnBackupLevel, Messages.tblColumnBackupDate,
                    Messages.tblColumnSize, Messages.tblColumnBackupPath
                };
        TableViewer historyTableViewer =
                CommonUITool.createCommonTableViewer(
                        composite,
                        new TableViewerSorter(),
                        columnNameArr,
                        CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, 400, 200));
        historyTableViewer.setInput(getBackupHistoryInfoList());
        Table historyTable = historyTableViewer.getTable();
        for (int i = 0; i < historyTable.getColumnCount(); i++) {
            historyTable.getColumn(i).pack();
        }
        return composite;
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setMinimumSize(500, 450);
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.titleBackupDbDialog);
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
        createButton(
                parent,
                IDialogConstants.CANCEL_ID,
                com.cubrid.cubridmanager.ui.common.Messages.btnCancel,
                false);
    }

    /**
     * Call this method when button is pressed
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            if (!CommonUITool.openConfirmBox(null, Messages.msgConfirmBackupDb)) {
                return;
            }
            if (valid(true)) {
                backupDb(buttonId);
            }
        } else {
            super.buttonPressed(buttonId);
        }
    }

    /**
     * Execute task and backup database
     *
     * @param buttonId the button id
     */
    private void backupDb(final int buttonId) {
        isCanFinished = true;

        TaskJobExecutor taskExcutor =
                new TaskJobExecutor() {
                    private String backupVolInfo;

                    public IStatus exec(final IProgressMonitor monitor) {
                        Display display = Display.getDefault();
                        display.syncExec(
                                new Runnable() {
                                    public void run() {
                                        getShell().setVisible(false);
                                    }
                                });

                        if (monitor.isCanceled()) {
                            cancel();
                            display.syncExec(
                                    new Runnable() {
                                        public void run() {
                                            setReturnCode(buttonId);
                                            close();
                                        }
                                    });
                            return Status.CANCEL_STATUS;
                        }
                        for (ITask task : taskList) {
                            if (!(task instanceof GetBackupVolInfoTask)
                                    || database.getRunningType() != DbRunningType.CS) {
                                task.execute();
                                final String msg = task.getErrorMsg();
                                if (msg != null
                                        && msg.length() > 0
                                        && !monitor.isCanceled()
                                        && !isCanceled()) {
                                    display.syncExec(
                                            new Runnable() {
                                                public void run() {
                                                    getShell().setVisible(true);
                                                }
                                            });
                                    return new Status(
                                            IStatus.ERROR, CubridManagerUIPlugin.PLUGIN_ID, msg);
                                }
                            }
                            if (isCanceled()) {
                                return Status.CANCEL_STATUS;
                            }
                            if (task instanceof CheckDirTask) {
                                CheckDirTask checkDirTask = (CheckDirTask) task;
                                final String[] dirs = checkDirTask.getNoExistDirectory();
                                if (dirs != null && dirs.length > 0) {
                                    display.syncExec(
                                            new Runnable() {
                                                public void run() {
                                                    CreateDirDialog dialog =
                                                            new CreateDirDialog(getShell());
                                                    dialog.setDirs(dirs);
                                                    if (dialog.open() != IDialogConstants.OK_ID) {
                                                        isCanFinished = false;
                                                        getShell().setVisible(true);
                                                    }
                                                }
                                            });
                                }
                            } else if (task instanceof CheckFileTask) {
                                CheckFileTask checkFileTask = (CheckFileTask) task;
                                final String[] files = checkFileTask.getExistFiles();
                                if (files != null && files.length > 0) {
                                    display.syncExec(
                                            new Runnable() {
                                                public void run() {
                                                    OverrideFileDialog dialog =
                                                            new OverrideFileDialog(getShell());
                                                    dialog.setFiles(files);
                                                    if (dialog.open() != IDialogConstants.OK_ID) {
                                                        isCanFinished = false;
                                                        getShell().setVisible(true);
                                                    }
                                                }
                                            });
                                }
                            } else if (task instanceof GetBackupVolInfoTask
                                    && database.getRunningType() == DbRunningType.STANDALONE) {
                                GetBackupVolInfoTask getBackupVolInfoTask =
                                        (GetBackupVolInfoTask) task;
                                backupVolInfo = getBackupVolInfoTask.getDbBackupVolInfo();
                            }
                            if (!isCanFinished) {
                                return Status.CANCEL_STATUS;
                            }
                            if (monitor.isCanceled()) {
                                cancel();
                                Display.getDefault()
                                        .syncExec(
                                                new Runnable() {
                                                    public void run() {
                                                        close();
                                                    }
                                                });

                                return Status.CANCEL_STATUS;
                            }
                        }
                        return Status.OK_STATUS;
                    }

                    public void done(IJobChangeEvent event) {
                        if (event.getResult() == Status.OK_STATUS) {
                            Display.getDefault()
                                    .syncExec(
                                            new Runnable() {
                                                public void run() {
                                                    if (database.getRunningType()
                                                            == DbRunningType.CS) {
                                                        Display.getDefault()
                                                                .syncExec(
                                                                        new Runnable() {
                                                                            public void run() {
                                                                                CommonUITool
                                                                                        .openInformationBox(
                                                                                                getShell(),
                                                                                                Messages
                                                                                                        .titleSuccess,
                                                                                                Messages
                                                                                                        .msgBackupSuccess);
                                                                            }
                                                                        });
                                                    } else {
                                                        if (backupVolInfo != null
                                                                && backupVolInfo.length() > 0) {
                                                            Display.getDefault()
                                                                    .syncExec(
                                                                            new Runnable() {
                                                                                public void run() {
                                                                                    BackupDbVolumeInfoDialog
                                                                                            backupDbResultInfoDialog =
                                                                                                    new BackupDbVolumeInfoDialog(
                                                                                                            getShell());
                                                                                    backupDbResultInfoDialog
                                                                                            .setResultInfoStr(
                                                                                                    backupVolInfo);
                                                                                    backupDbResultInfoDialog
                                                                                            .open();
                                                                                }
                                                                            });
                                                        }
                                                    }
                                                    close();
                                                }
                                            });
                        }
                    }
                };
        String backupDir = backupDirText.getText();
        CheckDirTask checkDirTask = new CheckDirTask(database.getServer().getServerInfo());
        checkDirTask.setDirectory(new String[] {backupDir});

        CheckFileTask checkFileTask = new CheckFileTask(database.getServer().getServerInfo());
        String fileName =
                backupDirText.getText()
                        + database.getServer().getServerInfo().getPathSeparator()
                        + volumeNameText.getText();
        checkFileTask.setFile(new String[] {fileName});

        String databaseName = databaseNameText.getText();
        String level = backupLevelCombo.getText().replaceAll("Level", "");
        String volName = volumeNameText.getText();
        boolean isRemoveLog = archiveLogButton.getSelection();
        boolean isCheckDbCons = consistentButton.getSelection();
        boolean isZip = useCompressButton.getSelection();
        boolean isSafeReplication = safeBackupButton.getSelection();
        int threadNum = spnThreadNum.getSelection();

        BackupDbTask backupDbTask = new BackupDbTask(database.getServer().getServerInfo());
        backupDbTask.setDbName(databaseName);
        backupDbTask.setLevel(level);
        backupDbTask.setVolumeName(volName);
        backupDbTask.setBackupDir(backupDir);
        backupDbTask.setRemoveLog(isRemoveLog);
        backupDbTask.setCheckDatabaseConsist(isCheckDbCons);
        backupDbTask.setThreadCount(String.valueOf(threadNum));
        backupDbTask.setZiped(isZip);
        backupDbTask.setSafeReplication(isSafeReplication);

        GetBackupVolInfoTask getBackupVolInfoTask =
                new GetBackupVolInfoTask(database.getServer().getServerInfo());
        getBackupVolInfoTask.setDbName(databaseName);

        taskExcutor.addTask(checkDirTask);
        taskExcutor.addTask(checkFileTask);
        taskExcutor.addTask(backupDbTask);
        taskExcutor.addTask(getBackupVolInfoTask);

        JobFamily jobFamily = new JobFamily();
        String serverName = database.getServer().getName();
        String dbName = database.getName();
        jobFamily.setServerName(serverName);
        jobFamily.setDbName(dbName);

        String jobName = Messages.msgBackupDBRearJobName + " - " + dbName + "@" + serverName;
        taskExcutor.schedule(jobName, jobFamily, true, Job.SHORT);
    }

    /** Initial data */
    private void initial() {
        backupLevelCombo.add("Level0");
        if (dbBackupInfo != null) {
            List<DbBackupHistoryInfo> dbBackupHistoryInfoList = dbBackupInfo.getBackupHistoryList();
            if (dbBackupHistoryInfoList != null) {
                int size = dbBackupHistoryInfoList.size();
                for (int i = 1; i < size + 1 && i < 3; i++) {
                    backupLevelCombo.add("Level" + i);
                }
            }
            backupDir =
                    FileUtil.changeSeparatorByOS(
                            dbBackupInfo.getDbDir(),
                            database.getServer().getServerInfo().getServerOsInfo());
            backupDirText.setText(backupDir);
        }
        String dir =
                CubridManagerUIPlugin.getPluginDialogSettings()
                        .get(KEY_BACKUP_DB_DIR + database.getId());
        if (dir != null && dir.trim().length() > 0) {
            backupDir = dir;
            backupDirText.setText(backupDir);
        }
        backupLevelCombo.select(backupLevelCombo.getItemCount() - 1);
        consistentButton.setSelection(true);
        useCompressButton.setSelection(true);
        if (CompatibleUtil.isSupportHA(database.getServer().getServerInfo())) {
            safeBackupButton.setSelection(false);
        } else {
            if (isReplication() && archiveLogButton.getSelection()) {
                safeBackupButton.setSelection(true);
            } else {
                safeBackupButton.setSelection(false);
            }
        }
        changeVolumeName();
    }

    /**
     * Get backup history information list
     *
     * @return the backup history information list
     */
    private List<Map<String, String>> getBackupHistoryInfoList() {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if (dbBackupInfo != null) {
            List<DbBackupHistoryInfo> dbBackupHistoryInfoList = dbBackupInfo.getBackupHistoryList();
            if (dbBackupHistoryInfoList != null) {
                int size = dbBackupHistoryInfoList.size();
                for (int i = 0; i < size; i++) {
                    DbBackupHistoryInfo historyInfo = dbBackupHistoryInfoList.get(i);
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("0", historyInfo.getLevel());
                    String dateStr = historyInfo.getDate();
                    if (dateStr != null && dateStr.trim().length() > 0) {
                        String[] dateStrArr = dateStr.split("\\.");
                        if (dateStrArr.length == 5) {
                            dateStr =
                                    dateStrArr[0]
                                            + "."
                                            + dateStrArr[1]
                                            + "."
                                            + dateStrArr[2]
                                            + " "
                                            + dateStrArr[3]
                                            + ":"
                                            + dateStrArr[4];
                        }
                        map.put("1", dateStr);
                    }
                    String sizeStr = historyInfo.getSize();
                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setMaximumFractionDigits(2);
                    if (sizeStr != null && sizeStr.matches("^\\d+$")) {
                        map.put("2", nf.format(Integer.parseInt(sizeStr) / (1024 * 1024)));
                    }
                    String path =
                            FileUtil.changeSeparatorByOS(
                                    historyInfo.getPath(),
                                    database.getServer().getServerInfo().getServerOsInfo());
                    map.put("3", path);
                    list.add(map);
                }
            }
        }
        return list;
    }

    /** Change volume name */
    private void changeVolumeName() {
        String databaseName = databaseNameText.getText();
        String level = backupLevelCombo.getText().replaceAll("Level", "");
        volumeNameText.setText(databaseName + "_backup_lv" + level);
    }

    /**
     * Check the data validation
     *
     * @param isShowDialog whether show dialog
     * @return <code>true</code> if valid;<code>false</code> otherwise
     */
    public boolean valid(boolean isShowDialog) {

        String volumeName = volumeNameText.getText();
        boolean isValidVolumeNameLength =
                volumeName.trim().length() > 0 && volumeName.indexOf(" ") < 0;
        if (volumeName.trim().length() <= 0 || !isValidVolumeNameLength) {
            if (isShowDialog) {
                CommonUITool.openErrorBox(getShell(), Messages.errVolumeName);
            }
            return false;
        }
        String backupDir = backupDirText.getText();
        boolean isValidBackupDir = ValidateUtil.isValidPathName(backupDir);
        if (!isValidBackupDir) {
            if (isShowDialog) {
                CommonUITool.openErrorBox(getShell(), Messages.errBackupDir);
            }
            return false;
        }
        int threadNum = spnThreadNum.getSelection();
        if (threadNum < 0) {
            if (isShowDialog) {
                CommonUITool.openErrorBox(getShell(), Messages.errParallerBackup);
            }
            return false;
        }
        return true;
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
     * @param database the database object
     */
    public void setDatabase(CubridDatabase database) {
        this.database = database;
    }

    /**
     * Set database backup information
     *
     * @param dbBackupInfo the DbBackupInfo
     */
    public void setDbBackupInfo(DbBackupInfo dbBackupInfo) {
        this.dbBackupInfo = dbBackupInfo;
    }

    /**
     * Return replication status in cubrid.conf parameter file
     *
     * @return <code>true</code> whether have replication;<code>false</code> otherwise
     */
    public boolean isReplication() {
        return isReplication;
    }

    /**
     * Set replication status in cubrid.conf parameter file
     *
     * @param isReplication whether is replication
     */
    public void setReplication(boolean isReplication) {
        this.isReplication = isReplication;
    }
}
