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
package com.cubrid.cubridmanager.ui.cubrid.dbspace.dialog;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.progress.CommonTaskJobExec;
import com.cubrid.common.ui.spi.progress.ITaskExecutorInterceptor;
import com.cubrid.common.ui.spi.progress.JobFamily;
import com.cubrid.common.ui.spi.progress.TaskJobExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.model.ConfConstants;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.task.CommonQueryTask;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.cubrid.database.task.CheckDirTask;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.AddVolumeDbInfo;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoList;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.GetAddVolumeStatusInfo;
import com.cubrid.cubridmanager.core.cubrid.dbspace.task.AddVolumeDbTask;
import com.cubrid.cubridmanager.ui.cubrid.database.dialog.CreateDirDialog;
import com.cubrid.cubridmanager.ui.cubrid.dbspace.Messages;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import java.math.BigDecimal;
import java.text.NumberFormat;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TreeViewer;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A dialog that show up when a user click the add volume context menu.
 *
 * @author lizhiqiang 2009-4-17
 */
public class AddVolumeDialog extends CMTitleAreaDialog implements ITaskExecutorInterceptor {
    /*No nl*/
    private static final String TEMP = "temp";
    private static final String INDEX = "index";
    private static final String DATA = "data";
    private static final String GENERIC = "generic";

    private Text volumeSizetext;
    private Text pathText;
    public static final BigDecimal MEGABYTES = new BigDecimal(1024 * 1024);
    private GetAddVolumeStatusInfo getAddVolumeStatusInfo;
    private BigDecimal pageSize;
    private DefaultSchemaNode selection;
    private AddVolumeDbInfo addVolumeDbInfo;
    private Combo purposeCombo;
    private String purpose;
    private boolean purposeEnable;
    private static final String[] itemsOfPurpose = new String[] {GENERIC, DATA, INDEX, TEMP};
    private String jobName;
    private TreeViewer treeViewer;
    private boolean isCreateDir;

    public AddVolumeDialog(Shell parentShell) {
        super(parentShell);
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     * @param parent The parent composite to contain the dialog area
     * @return the dialog area control
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);
        setTitle(Messages.dialogTitle);
        setMessage(Messages.dialogMsg);

        final Composite composite = new Composite(parentComp, SWT.RESIZE);
        final GridData gdComposite = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gdComposite.widthHint = 500;
        composite.setLayoutData(gdComposite);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        gridLayout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        gridLayout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        gridLayout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(gridLayout);

        final Group group = new Group(composite, SWT.NONE);
        group.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
        final GridLayout gdGroup = new GridLayout();
        gdGroup.verticalSpacing = 0;
        group.setLayout(gdGroup);

        final Composite pathComp = new Composite(group, SWT.RESIZE);
        final GridLayout pathLayout = new GridLayout(3, false);
        pathLayout.marginBottom = 0;
        final GridData gdPathComp = new GridData(SWT.FILL, SWT.CENTER, true, false);
        pathComp.setLayout(pathLayout);
        pathComp.setLayoutData(gdPathComp);

        final GridData gdLabel = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
        gdLabel.widthHint = 100;

        final Label pathLbl = new Label(pathComp, SWT.NONE);
        pathLbl.setText(Messages.pathLblName);
        pathLbl.setLayoutData(gdLabel);

        pathText = new Text(pathComp, SWT.BORDER);
        final GridData gdPathText = new GridData(SWT.FILL, SWT.CENTER, true, false);
        pathText.setLayoutData(gdPathText);
        pathText.setToolTipText(Messages.pathToolTip);

        Button selectTargetDirBtn = new Button(pathComp, SWT.NONE);
        selectTargetDirBtn.setText(Messages.btnBrowse);
        selectTargetDirBtn.setLayoutData(CommonUITool.createGridData(1, 1, 80, -1));
        selectTargetDirBtn.addSelectionListener(
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
        ServerInfo serverInfo = selection.getServer().getServerInfo();
        if (serverInfo != null && !serverInfo.isLocalServer()) {
            selectTargetDirBtn.setEnabled(false);
        }
        final Composite purpComp = new Composite(group, SWT.RESIZE);
        final GridLayout purpLayout = new GridLayout(2, false);
        purpLayout.marginTop = 0;
        purpLayout.verticalSpacing = 10;
        final GridData gdPurtComp = new GridData(SWT.FILL, SWT.CENTER, true, false);
        purpComp.setLayout(purpLayout);
        purpComp.setLayoutData(gdPurtComp);

        final Label purposeLbl = new Label(purpComp, SWT.NONE);
        purposeLbl.setText(Messages.purposeLbllName);
        purposeLbl.setLayoutData(gdLabel);

        purposeCombo = new Combo(purpComp, SWT.BORDER | SWT.RIGHT | SWT.READ_ONLY);
        if (purposeEnable) {
            purposeCombo.setEnabled(true);
        } else {
            purposeCombo.setEnabled(false);
        }

        final GridData gdPurposeText = new GridData(SWT.FILL, SWT.CENTER, true, false);
        purposeCombo.setLayoutData(gdPurposeText);
        purposeCombo.setItems(itemsOfPurpose);

        final Label volumeSizeLbl = new Label(purpComp, SWT.NONE);
        volumeSizeLbl.setText(Messages.volumeSizeLblName);
        volumeSizeLbl.setLayoutData(gdLabel);

        volumeSizetext = new Text(purpComp, SWT.BORDER | SWT.RIGHT);
        final GridData gdVolumeSizetext = new GridData(SWT.FILL, SWT.CENTER, true, false);
        volumeSizetext.setLayoutData(gdVolumeSizetext);
        volumeSizetext.setToolTipText(Messages.volumeSizeToolTip);

        /*Initial the volume size*/
        String volumeSize = ConfConstants.DEFAULT_DATA_VOLUME_SIZE;
        String configVolumeSize =
                CompatibleUtil.getConfigGenericVolumeSize(
                        selection.getServer().getServerInfo(), null);
        if (!StringUtil.isEmpty(configVolumeSize)) {
            Long bytes = StringUtil.getByteNumber(configVolumeSize);
            if (bytes > -1) {
                double value = StringUtil.convertToM(bytes);

                NumberFormat nf = NumberFormat.getInstance();
                nf.setGroupingUsed(false);
                nf.setMaximumFractionDigits(3);
                nf.setMinimumFractionDigits(3);

                volumeSize = nf.format(value);
            }
        }
        volumeSizetext.setText(volumeSize);
        volumeSizetext.addModifyListener(
                new ModifyListener() {

                    public void modifyText(ModifyEvent e) {
                        verify();
                    }
                });

        // Sets the initial value
        pathText.setText(getAddVolumeStatusInfo.getVolpath());
        purposeCombo.setText(purpose);
        // Sets listener
        pathText.addModifyListener(
                new ModifyListener() {

                    public void modifyText(ModifyEvent event) {
                        verify();
                    }
                });

        return parentComp;
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.dialogTitle);
    }

    /** When press ok button,call it */
    public void okPressed() {
        if (!verify()) {
            return;
        }

        String sPathText = pathText.getText().trim();
        String sizeNeedMb = volumeSizetext.getText().trim();
        purpose = purposeCombo.getText().trim();

        String msg = Messages.bind(Messages.msgConfirmAdd, sizeNeedMb, purpose);
        if (!CommonUITool.openConfirmBox(null, msg)) {
            return;
        }

        addVolumeDbInfo = new AddVolumeDbInfo();
        addVolumeDbInfo.setPath(sPathText);
        addVolumeDbInfo.setNumberofpage(String.valueOf(calcVolumePageNum()));
        addVolumeDbInfo.setSize_need_mb(sizeNeedMb + "(MB)");
        addVolumeDbInfo.setPurpose(purpose);
        addVolumeDbInfo.setVolname("");
        addVolumeDbInfo.setDbname(selection.getDatabase().getName());
        performTask(addVolumeDbInfo);
    }

    /**
     * Set volume status information
     *
     * @param getAddVolumeStatusInfo the getAddVolumeStatusInfo to set
     */
    public void setGetAddVolumeStatusInfo(GetAddVolumeStatusInfo getAddVolumeStatusInfo) {
        this.getAddVolumeStatusInfo = getAddVolumeStatusInfo;
    }

    /**
     * Initial the parameter
     *
     * @param selection the selection to set
     */
    public void initPara(DefaultSchemaNode selection) {
        this.selection = selection;
        String type = selection.getType();
        if (CubridNodeType.GENERIC_VOLUME_FOLDER.equals(type)) {
            purpose = GENERIC;
            purposeEnable = false;
        } else if (CubridNodeType.DATA_VOLUME_FOLDER.equals(type)) {
            purpose = DATA;
            purposeEnable = false;
        } else if (CubridNodeType.INDEX_VOLUME_FOLDER.equals(type)) {
            purpose = INDEX;
            purposeEnable = false;
        } else if (CubridNodeType.TEMP_VOLUME_FOLDER.equals(type)) {
            purpose = TEMP;
            purposeEnable = false;
        } else if (CubridNodeType.DBSPACE_FOLDER.equals(type)) {
            purpose = GENERIC;
            purposeEnable = true;
        }
    }

    private boolean verify() {

        setErrorMessage(null);
        if (getButton(IDialogConstants.OK_ID) != null) {
            getButton(IDialogConstants.OK_ID).setEnabled(false);
        }

        if (!ValidateUtil.isValidPathName(pathText.getText())) {
            setErrorMessage(Messages.errorPathMsg);
            return false;
        }
        if (!ValidateUtil.isPositiveDouble(volumeSizetext.getText())) {
            setErrorMessage(Messages.errorVolumeMsg);
            return false;
        }

        BigDecimal volume = new BigDecimal(volumeSizetext.getText());
        if (volume.compareTo(BigDecimal.ONE) < 0) {
            setErrorMessage(Messages.errorVolumeMsg);
            return false;
        }

        long pageNum = calcVolumePageNum();
        if (pageNum < 1) {
            setErrorMessage(Messages.errorPageMsg);
            return false;
        }

        if (getButton(IDialogConstants.OK_ID) != null) {
            getButton(IDialogConstants.OK_ID).setEnabled(true);
        }

        return true;
    }

    /**
     * Gets the instance of AddVolumeDbInfo
     *
     * @return the addVolumeDbInfo
     */
    public AddVolumeDbInfo getAddVolumeDbInfo() {
        return addVolumeDbInfo;
    }

    /**
     * Set the page size
     *
     * @param pageSize the pageSize to set
     */
    public void setPageSize(int pageSize) {
        this.pageSize = new BigDecimal(pageSize);
    }

    /** Calculate volume page number */
    private long calcVolumePageNum() {
        String volumeSizeStr = volumeSizetext.getText();
        boolean isValidVolumeSize = ValidateUtil.isPositiveDouble(volumeSizeStr);
        if (pageSize != null) {
            double size = pageSize.doubleValue();
            if (isValidVolumeSize) {
                double volumeSize = Double.parseDouble(volumeSizeStr);
                double pageNumber = (1024 * 1024 / size) * volumeSize;

                return Math.round(pageNumber);
            }
        }
        return -1;
    }

    /**
     * perform the task
     *
     * @param addVolumeDbInfo AddVolumeDbInfo
     */
    private void performTask(AddVolumeDbInfo addVolumeDbInfo) {
        // Checks the path
        String sPathText = pathText.getText().trim();
        ServerInfo serverInfo = selection.getServer().getServerInfo();
        CheckDirTask checkDirTask = new CheckDirTask(serverInfo);
        checkDirTask.setDirectory(new String[] {sPathText});

        CubridDatabase database = selection.getDatabase();
        AddVolumeDbTask addVolumeTask = new AddVolumeDbTask(database.getServer().getServerInfo());
        addVolumeTask.setDbname(database.getName());
        addVolumeTask.setVolname(addVolumeDbInfo.getVolname());
        addVolumeTask.setPurpose(addVolumeDbInfo.getPurpose());
        addVolumeTask.setPath(addVolumeDbInfo.getPath());
        addVolumeTask.setNumberofpages(addVolumeDbInfo.getNumberofpage());
        addVolumeTask.setSizeNeedMb(addVolumeDbInfo.getSize_need_mb());
        // Gets the database space info
        DbSpaceInfoList dbSpaceInfo = new DbSpaceInfoList();
        final CommonQueryTask<DbSpaceInfoList> dbSpaceInfoTask =
                new CommonQueryTask<DbSpaceInfoList>(
                        database.getServer().getServerInfo(),
                        CommonSendMsg.getCommonDatabaseSendMsg(),
                        dbSpaceInfo);
        dbSpaceInfoTask.setDbName(database.getLabel());

        JobFamily jobFamily = new JobFamily();
        String serverName = selection.getServer().getName();
        String dbName = selection.getDatabase().getName();
        jobFamily.setServerName(serverName);
        jobFamily.setDbName(dbName);

        jobName = Messages.msgAddVolRearJobName + " - " + dbName + "@" + serverName;
        TaskJobExecutor taskExec = new CommonTaskJobExec(this);
        taskExec.addTask(checkDirTask);
        taskExec.addTask(addVolumeTask);
        taskExec.addTask(dbSpaceInfoTask);
        taskExec.schedule(dbName, jobFamily, true, Job.SHORT);
    }

    /** Complete the progress */
    public void completeAll() {
        if (null != jobName) {
            CommonUITool.openInformationBox(
                    Messages.ttlSuccessDlg, Messages.bind(Messages.msgAddVolumeComplete, jobName));
        }
        if (CubridNodeType.DBSPACE_FOLDER.equals(selection.getType())) {
            CommonUITool.refreshNavigatorTree(treeViewer, selection);
        } else {
            CommonUITool.refreshNavigatorTree(treeViewer, selection.getParent());
        }
    }

    /**
     * After a task has been executed, do some thing such as refresh.
     *
     * @param task the task
     * @return IStatus if complete refresh false if run into error
     */
    public IStatus postTaskFinished(ITask task) {
        if (task instanceof CheckDirTask) {
            CheckDirTask checkDirTask = (CheckDirTask) task;
            final String[] dirs = checkDirTask.getNoExistDirectory();
            if (dirs != null && dirs.length > 0) {
                isCreateDir = true;
                Display.getDefault()
                        .syncExec(
                                new Runnable() {
                                    public void run() {
                                        CreateDirDialog dialog = new CreateDirDialog(getShell());
                                        dialog.setDirs(dirs);
                                        if (dialog.open() != IDialogConstants.OK_ID) {
                                            isCreateDir = false;
                                        }
                                    }
                                });
                if (!isCreateDir) {
                    return Status.CANCEL_STATUS;
                }
            }
        }
        return Status.OK_STATUS;
    }

    /** @param treeViewer the treeViewer to set */
    public void setTreeViewer(TreeViewer treeViewer) {
        this.treeViewer = treeViewer;
    }
}
