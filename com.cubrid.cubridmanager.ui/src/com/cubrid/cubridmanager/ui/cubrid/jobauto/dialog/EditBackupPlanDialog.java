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
package com.cubrid.cubridmanager.ui.cubrid.jobauto.dialog;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.FileUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.model.AddEditType;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.common.model.OnOffType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.jobauto.model.BackupPlanInfo;
import com.cubrid.cubridmanager.core.cubrid.jobauto.task.BackupPlanTask;
import com.cubrid.cubridmanager.core.utils.ModelUtil.YesNoType;
import com.cubrid.cubridmanager.ui.cubrid.jobauto.Messages;
import com.cubrid.cubridmanager.ui.cubrid.jobauto.control.PeriodGroup;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import org.eclipse.core.runtime.NullProgressMonitor;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

/**
 * A dialog that show up when a user click the backup plan context menu.
 *
 * @author lizhiqiang 2009-3-12
 */
public class EditBackupPlanDialog extends CMTitleAreaDialog implements Observer {

    private static final int MAX_THREAD = 64;
    private static final String ZERO_LEVER = Messages.zeroLever;
    private static final String ONE_LEVER = Messages.oneLever;
    private static final String TWO_LEVER = Messages.twoLever;
    private Combo leverCombo;
    private Text idText;
    private Text pathText;
    private Button storeButton;
    private Button deleteButton;
    private Button updateButton;
    private Button checkingButton;
    private Button useCompressButton;
    private Spinner numKeepBackups;
    private Spinner numThreadspinner;

    private CubridDatabase database;
    private String opBackupInfo;
    private AddEditType operation;
    private PeriodGroup periodGroup;
    private Button onlineButton;
    private Button offlineButton;
    private String defaultPath;

    private boolean isOkenable[];
    private BackupPlanInfo backupPlanInfo;
    private List<String> childrenLabel;

    private boolean isEditAble;
    private boolean isBkNumSupports;

    /**
     * The Constructor
     *
     * @param parentShell
     */
    public EditBackupPlanDialog(Shell parentShell, boolean isEditAble) {
        super(parentShell);
        isOkenable = new boolean[6];
        for (int k = 0; k < isOkenable.length; k++) {
            isOkenable[k] = true;
        }
        this.isEditAble = isEditAble;
    }

    /**
     * Create the dialog area
     *
     * @param parent the parent composite
     * @return the composite
     */
    protected Control createDialogArea(Composite parent) {
        this.isBkNumSupports = CompatibleUtil.isBackupNumSupports(database.getDatabaseInfo());

        Composite parentComp = (Composite) super.createDialogArea(parent);
        final Composite composite = new Composite(parentComp, SWT.RESIZE);
        final GridData gdComposite = new GridData(SWT.FILL, SWT.CENTER, true, false);
        composite.setLayoutData(gdComposite);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        gridLayout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        gridLayout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        gridLayout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(gridLayout);

        createBasicGroup(composite);
        periodGroup = new PeriodGroup(this, isEditAble);
        periodGroup.addObserver(this);
        final boolean isSupportPeriodic =
                CompatibleUtil.isSupportPeriodicAutoJob(database.getServer().getServerInfo());
        periodGroup.setSupportPeriodic(isSupportPeriodic);
        periodGroup.setTimeSplitByColon(false);
        if (operation == AddEditType.EDIT) {
            // Sets the edit title and message
            setMessage(Messages.editBackupPlanMsg);
            setTitle(Messages.editBackupPlanTitle);
            getShell().setText(Messages.editBackupPlanTitle);
            // Sets the initial value in periodGroup
            periodGroup.setTypeValue((backupPlanInfo.getPeriod_type()));
            periodGroup.setDetailValue(backupPlanInfo.getPeriod_date());
            String time = backupPlanInfo.getTime();

            if (!time.startsWith("i")) {
                periodGroup.setTimeValue(time);
            } else {
                int interval = Integer.parseInt(time.substring(1));
                periodGroup.setIntervalValue(interval);
            }

            isOkenable[0] = true;
        } else {
            setMessage(Messages.addBackupPlanMsg);
            setTitle(Messages.addBackupPlanTitle);
            getShell().setText(Messages.addBackupPlanTitle);
            isOkenable[0] = false;
        }
        periodGroup.createPeriodGroup(composite);
        createOptionsGroup(composite);

        if (!isEditAble) {
            leverCombo.setEnabled(false);
            idText.setEditable(false);
            pathText.setEditable(false);
            if (isBkNumSupports) {
                numKeepBackups.setEnabled(false);
            } else {
                storeButton.setEnabled(false);
            }
            deleteButton.setEnabled(false);
            updateButton.setEnabled(false);
            checkingButton.setEnabled(false);
            useCompressButton.setEnabled(false);
            numThreadspinner.setEnabled(false);
            onlineButton.setEnabled(false);
            offlineButton.setEnabled(false);
        }
        return parentComp;
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
        if (operation == AddEditType.ADD || !isEditAble) {
            getButton(IDialogConstants.OK_ID).setEnabled(false);
        }
    }

    /**
     * create the basic group in the Dialog
     *
     * @param composite Composite
     */
    private void createBasicGroup(Composite composite) {
        final Group generalInfoGroup = new Group(composite, SWT.RESIZE);
        generalInfoGroup.setText(Messages.basicGroupName);
        GridLayout groupLayout = new GridLayout();
        groupLayout.verticalSpacing = 0;
        generalInfoGroup.setLayout(groupLayout);
        final GridData gdGeneralInfoGroup = new GridData(SWT.FILL, SWT.CENTER, true, false);
        generalInfoGroup.setLayoutData(gdGeneralInfoGroup);

        Composite idComposite = new Composite(generalInfoGroup, SWT.RESIZE);
        final GridLayout idGridLayout = new GridLayout(4, false);
        idComposite.setLayout(idGridLayout);
        idComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        final Label idLabel = new Label(idComposite, SWT.RESIZE);
        final GridData gdIdLabel = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
        gdIdLabel.widthHint = 80;
        idLabel.setLayoutData(gdIdLabel);

        idLabel.setText(Messages.msgIdLbl);
        idText = new Text(idComposite, SWT.BORDER | SWT.RESIZE);
        idText.setTextLimit(ValidateUtil.MAX_NAME_LENGTH);
        final GridData gdIdText = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdIdText.widthHint = 140;
        idText.setLayoutData(gdIdText);

        final Label levelLabel = new Label(idComposite, SWT.RESIZE);
        final GridData gdLevelLabel = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
        gdLevelLabel.widthHint = 80;
        levelLabel.setLayoutData(gdLevelLabel);
        levelLabel.setText(Messages.msgLevelLbl);

        leverCombo = new Combo(idComposite, SWT.NONE | SWT.READ_ONLY);
        leverCombo.setItems(new String[] {ZERO_LEVER, ONE_LEVER, TWO_LEVER});
        final GridData gdLeverCombo = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdLeverCombo.widthHint = 135;
        leverCombo.setLayoutData(gdLeverCombo);
        leverCombo.select(0);

        Composite pathComposite = new Composite(generalInfoGroup, SWT.RESIZE);
        pathComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        final GridLayout pathGridLayout = new GridLayout(3, false);
        pathComposite.setLayout(pathGridLayout);
        pathComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        final Label pathLabel = new Label(pathComposite, SWT.RESIZE);
        final GridData gdPathLabel = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
        gdPathLabel.widthHint = 80;
        pathLabel.setLayoutData(gdPathLabel);
        pathLabel.setText(Messages.msgPathLbl);

        pathText = new Text(pathComposite, SWT.BORDER);
        final GridData gdPathText = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdPathText.widthHint = 240;
        pathText.setLayoutData(gdPathText);

        Button selectTargetDirectoryButton = new Button(pathComposite, SWT.NONE);
        selectTargetDirectoryButton.setText(Messages.btnBrowse);
        selectTargetDirectoryButton.setLayoutData(CommonUITool.createGridData(1, 1, 80, -1));
        selectTargetDirectoryButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        DirectoryDialog dlg = new DirectoryDialog(getShell());
                        String backupDir = pathText.getText();
                        if (backupDir != null && backupDir.trim().length() > 0) {
                            dlg.setFilterPath(backupDir);
                        }
                        dlg.setText(Messages.msgSelectDir);
                        dlg.setMessage(Messages.msgSelectDir);
                        String dir = dlg.open();
                        if (dir != null) {
                            pathText.setText(dir);
                        }
                    }
                });
        ServerInfo serverInfo = database.getServer().getServerInfo();
        if (serverInfo != null && !serverInfo.isLocalServer()) {
            selectTargetDirectoryButton.setEnabled(false);
        }
        // sets the initial value
        if (operation == AddEditType.EDIT) {
            idText.setText(backupPlanInfo.getBackupid());
            int selected = Integer.parseInt(backupPlanInfo.getLevel());
            leverCombo.select(selected);
            pathText.setText(backupPlanInfo.getPath());
            idText.setEditable(false);
        } else {
            idText.setEditable(true);
            pathText.setText(defaultPath);
        }
        idText.addModifyListener(new IdTextModifyListener());
        pathText.addModifyListener(new PathTextModifyListener());
    }

    /**
     * create the options group in the Dialog
     *
     * @param composite Composite
     */
    private void createOptionsGroup(Composite composite) {
        final Group optionsGroup = new Group(composite, SWT.NONE);
        final GridData gdOptionsGroup = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);

        optionsGroup.setLayoutData(gdOptionsGroup);
        GridLayout groupLayout = new GridLayout(1, true);
        optionsGroup.setLayout(groupLayout);

        final Group checkGroup = new Group(optionsGroup, SWT.NONE);

        checkGroup.setText(Messages.optionGroupName);
        checkGroup.setLayout(new GridLayout(2, true));
        final GridData gdCheckGroup = new GridData(SWT.FILL, SWT.TOP, true, false);
        checkGroup.setLayoutData(gdCheckGroup);

        if (!isBkNumSupports) {
            storeButton = new Button(checkGroup, SWT.CHECK);
            storeButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            storeButton.setText(Messages.msgStroreBtn);
        }

        deleteButton = new Button(checkGroup, SWT.CHECK);
        deleteButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        deleteButton.setText(Messages.msgDeleteBtn);

        updateButton = new Button(checkGroup, SWT.CHECK);
        updateButton.setText(Messages.msgUpdateBtn);

        checkingButton = new Button(checkGroup, SWT.CHECK);
        checkingButton.setText(Messages.msgCheckingBtn);

        useCompressButton = new Button(checkGroup, SWT.CHECK);
        useCompressButton.setText(Messages.msgUseCompressBtn);

        final Composite threadComposite = new Composite(checkGroup, SWT.NONE);
        final GridData gdThreadComposite = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        gdThreadComposite.minimumHeight = 1;
        threadComposite.setLayoutData(gdThreadComposite);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        threadComposite.setLayout(gridLayout);

        final Label numThreadLabel = new Label(threadComposite, SWT.NONE);
        numThreadLabel.setText(Messages.msgNumThreadLbl);

        numThreadspinner = new Spinner(threadComposite, SWT.BORDER);
        numThreadspinner.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, true));
        numThreadspinner.setMaximum(MAX_THREAD);
        final Group radioGroup = new Group(optionsGroup, SWT.None);
        radioGroup.setText(Messages.msgComboGroup);
        final GridData gdComboGroup = new GridData(SWT.FILL, SWT.TOP, true, false);
        radioGroup.setLayoutData(gdComboGroup);
        radioGroup.setLayout(new GridLayout());

        onlineButton = new Button(radioGroup, SWT.RADIO);
        onlineButton.setText(Messages.msgOnlineBtn);

        offlineButton = new Button(radioGroup, SWT.RADIO);
        offlineButton.setText(Messages.msgOfflineBtn);

        if (isBkNumSupports) {
            final Composite bkNumComposite = new Composite(checkGroup, SWT.NONE);
            final GridData gdBkNumComposite = new GridData(SWT.LEFT, SWT.CENTER, false, false);
            gdBkNumComposite.minimumHeight = 1;
            bkNumComposite.setLayoutData(gdBkNumComposite);
            final GridLayout bkNumGridLayout = new GridLayout();
            bkNumGridLayout.numColumns = 2;
            bkNumComposite.setLayout(bkNumGridLayout);

            final Label lblNumKeepBackups = new Label(bkNumComposite, SWT.NONE);
            lblNumKeepBackups.setText(Messages.msgBkNumLbl);
            numKeepBackups = new Spinner(bkNumComposite, SWT.BORDER);
            numKeepBackups.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, true));
            numKeepBackups.setSelection(StringUtil.intValue(backupPlanInfo.getBknum(), 0));
            numKeepBackups.setMaximum(255);
        }

        if (operation == AddEditType.EDIT) {
            if (isBkNumSupports) {
                String bkNum = backupPlanInfo.getBknum();
                if (StringUtil.isEmpty(bkNum)) {
                    bkNum = "0";
                }
            } else {
                storeButton.setSelection(CommonUITool.str2Boolean(backupPlanInfo.getStoreold()));
            }
            deleteButton.setSelection(CommonUITool.str2Boolean(backupPlanInfo.getArchivedel()));
            updateButton.setSelection(CommonUITool.str2Boolean(backupPlanInfo.getUpdatestatus()));
            checkingButton.setSelection(CommonUITool.str2Boolean(backupPlanInfo.getCheck()));
            useCompressButton.setSelection(CommonUITool.str2Boolean(backupPlanInfo.getZip()));
            numThreadspinner.setSelection(Integer.valueOf(backupPlanInfo.getMt()));

            boolean originalOnline = CommonUITool.str2Boolean(backupPlanInfo.getOnoff());
            if (originalOnline) {
                onlineButton.setSelection(true);
                offlineButton.setSelection(false);
                if (database.getRunningType() == DbRunningType.STANDALONE) {
                    onlineButton.setEnabled(false);
                }
            } else {
                onlineButton.setSelection(false);
                offlineButton.setSelection(true);
            }
        } else { // sets the status of onlineButton and offlineButton if
            if (database.getRunningType() == DbRunningType.CS) {
                onlineButton.setSelection(true);
                offlineButton.setSelection(false);
            } else {
                onlineButton.setSelection(false);
                offlineButton.setSelection(true);
                onlineButton.setEnabled(false);
            }
        }
    }

    /**
     * A class that response the change of idText
     *
     * @author lizhiqing
     * @version 1.0 - 2009-12-28 created by lizhiqing
     */
    private class IdTextModifyListener implements ModifyListener {
        /**
         * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
         * @param event an event containing information about the modify
         */
        public void modifyText(ModifyEvent event) {
            String id = idText.getText().trim();
            if (id.length() <= 0) {
                isOkenable[0] = false;
            } else if (id.length() > 0 && !ValidateUtil.isValidDBName(id)) {
                isOkenable[3] = false;
            } else if (id.length() > 0 && childrenLabel.contains(id)) {
                isOkenable[4] = false;
            } else if (id.length() > Integer.valueOf(Messages.backplanIdMaxLen)) {
                isOkenable[5] = false;
            } else {
                isOkenable[0] = true;
                isOkenable[3] = true;
                isOkenable[4] = true;
                isOkenable[5] = true;
            }
            enableOk();
        }
    }

    /**
     * A class that response the change of pathText
     *
     * @author lizhiqing
     * @version 1.0 - 2009-12-28 created by lizhiqing
     */
    private class PathTextModifyListener implements ModifyListener {
        /**
         * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
         * @param event an event containing information about the modify
         */
        public void modifyText(ModifyEvent event) {
            String path = pathText.getText();
            if (path.length() > 0
                    && !ValidateUtil.isValidPathName(path)
                    && !ValidateUtil.isValidPathNameLength(path)) {
                isOkenable[2] = false;
            } else {
                isOkenable[2] = true;
            }
            enableOk();
        }
    }

    /** When press ok button,call it */
    public void okPressed() {
        // Gets the data of dialog

        int intLever = leverCombo.getSelectionIndex();
        if (intLever == 1 && !CommonUITool.openConfirmBox(Messages.msgLevelOneWarning)) {
            return;
        }
        if (intLever == 2 && !CommonUITool.openConfirmBox(Messages.msgLevelTwoWarning)) {
            return;
        }

        String newBackupid = idText.getText().trim();
        String newPath = pathText.getText().trim();
        String newPeriodType = periodGroup.getTextOfTypeCombo();
        String newPeriodDate = periodGroup.getDetailValue();
        String newTime = periodGroup.getTime();
        String newLever = Integer.toString(intLever);
        OnOffType newArchivedel = deleteButton.getSelection() ? OnOffType.ON : OnOffType.OFF;
        OnOffType newUpdatestatus = updateButton.getSelection() ? OnOffType.ON : OnOffType.OFF;
        String bkNum = null;
        OnOffType newStroreold = null;
        if (isBkNumSupports) {
            newStroreold = OnOffType.OFF;
            bkNum = String.valueOf(numKeepBackups.getSelection());
            if (StringUtil.isEmpty(bkNum)) {
                bkNum = "0";
            }
        } else {
            newStroreold = storeButton.getSelection() ? OnOffType.ON : OnOffType.OFF;
            bkNum = "0";
        }
        OnOffType newOnoff = onlineButton.getSelection() ? OnOffType.ON : OnOffType.OFF;

        YesNoType newZip = useCompressButton.getSelection() ? YesNoType.Y : YesNoType.N;
        YesNoType newCheck = checkingButton.getSelection() ? YesNoType.Y : YesNoType.N;
        String newMt = Integer.valueOf(numThreadspinner.getSelection()).toString();
        // Sets the object of backupPlanInfo
        backupPlanInfo.setBackupid(newBackupid);
        backupPlanInfo.setPath(newPath);
        backupPlanInfo.setPeriod_type(newPeriodType);
        backupPlanInfo.setPeriod_date(newPeriodDate);
        backupPlanInfo.setTime(newTime);
        backupPlanInfo.setLevel(newLever);
        backupPlanInfo.setArchivedel(newArchivedel.getText());
        backupPlanInfo.setUpdatestatus(newUpdatestatus.getText());
        backupPlanInfo.setStoreold(newStroreold.getText());
        backupPlanInfo.setOnoff(newOnoff.getText());
        backupPlanInfo.setZip(newZip.getText());
        backupPlanInfo.setCheck(newCheck.getText());
        backupPlanInfo.setMt(newMt);
        backupPlanInfo.setBknum(bkNum);
        // Executes the task
        ServerInfo serverInfo = database.getServer().getServerInfo();
        BackupPlanTask backupPlanTask = new BackupPlanTask(opBackupInfo, serverInfo);
        backupPlanTask.setDbname(database.getName());
        backupPlanTask.setBackupid(newBackupid);
        backupPlanTask.setPath(newPath);
        backupPlanTask.setPeriodType(newPeriodType);
        backupPlanTask.setPeriodDate(newPeriodDate);
        backupPlanTask.setTime(newTime);
        backupPlanTask.setLevel(newLever);
        backupPlanTask.setArchivedel(newArchivedel);
        backupPlanTask.setUpdatestatus(newUpdatestatus);
        backupPlanTask.setStoreold(newStroreold);
        backupPlanTask.setOnoff(newOnoff);
        backupPlanTask.setZip(newZip);
        backupPlanTask.setCheck(newCheck);
        backupPlanTask.setMt(newMt);
        backupPlanTask.setBknum(bkNum);

        String taskName = Messages.bind(Messages.editBackupPlanTaskName, newBackupid);
        TaskExecutor taskExecutor = new CommonTaskExec(taskName);
        taskExecutor.addTask(backupPlanTask);
        new ExecTaskWithProgress(taskExecutor).exec();
        if (taskExecutor.isSuccess()) {
            super.okPressed();
        }
    }

    /**
     * Initials the backupPlanInfo,database and childrenLabel
     *
     * @param selection the selection to set
     */
    public void initPara(DefaultSchemaNode selection) {
        childrenLabel = new ArrayList<String>();
        ICubridNode[] childrenNode = null;
        database = selection.getDatabase();
        if (operation == AddEditType.EDIT) {
            backupPlanInfo = (BackupPlanInfo) selection.getAdapter(BackupPlanInfo.class);
            childrenNode = selection.getParent().getChildren(new NullProgressMonitor());
        } else {
            backupPlanInfo = new BackupPlanInfo();
            String dbPath = database.getDatabaseInfo().getDbDir();
            dbPath =
                    FileUtil.changeSeparatorByOS(
                            dbPath, database.getServer().getServerInfo().getServerOsInfo());
            defaultPath =
                    dbPath + selection.getServer().getServerInfo().getPathSeparator() + "backup";
            childrenNode = selection.getChildren(new NullProgressMonitor());
        }
        for (ICubridNode childNode : childrenNode) {
            childrenLabel.add(childNode.getLabel());
        }
    }

    /** @param operation the operation to set */
    public void setOperation(AddEditType operation) {
        this.operation = operation;
        if (operation == AddEditType.ADD) {
            opBackupInfo = BackupPlanTask.ADD_BACKUP_INFO;
        } else if (operation.equals(AddEditType.EDIT)) {
            opBackupInfo = BackupPlanTask.SET_BACKUP_INFO;
        }
    }

    /**
     * Gets the instance of BackupPlanInfo
     *
     * @return the backupPlanInfo
     */
    public BackupPlanInfo getBackupPlanInfo() {
        return backupPlanInfo;
    }

    /**
     * Observer the change of instance of the type Period
     *
     * @param ob the observable object.
     * @param arg an argument passed to the <code>notifyObservers</code> method.
     */
    public void update(Observable ob, Object arg) {
        boolean isAllow = (Boolean) arg;
        isOkenable[1] = isAllow;
        enableOk();
    }

    /** Enable the "OK" button */
    private void enableOk() {
        boolean is = true;
        for (int i = 0; i < isOkenable.length; i++) {
            is = is && isOkenable[i];
        }
        if (is) {
            getButton(IDialogConstants.OK_ID).setEnabled(true);
        } else {
            getButton(IDialogConstants.OK_ID).setEnabled(false);
        }
        if (!isOkenable[0]) {
            setErrorMessage(Messages.errBackupPlanIdEmpty);
            return;
        }
        if (!isOkenable[3]) {
            setErrorMessage(Messages.errIdTextMsg);
            return;
        }
        if (!isOkenable[4]) {
            setErrorMessage(Messages.errIdRepeatMsg);
            return;
        }
        if (!isOkenable[5]) {
            setErrorMessage(Messages.errBackplanIdLen);
            return;
        }
        if (!isOkenable[2]) {
            setErrorMessage(Messages.errPathTextMsg);
            return;
        }
        if (!isOkenable[1]) {
            periodGroup.enableOk();
            return;
        }
        setErrorMessage(null);
    }
}
