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
package com.cubrid.cubridmanager.ui.cubrid.database.control;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.progress.ITaskExecutorInterceptor;
import com.cubrid.common.ui.spi.progress.JobFamily;
import com.cubrid.common.ui.spi.progress.TaskJobExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.common.task.CommonTaskName;
import com.cubrid.cubridmanager.core.common.task.CommonUpdateTask;
import com.cubrid.cubridmanager.core.common.task.GetCubridConfParameterTask;
import com.cubrid.cubridmanager.core.common.task.SetCubridConfParameterTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.UserSendObj;
import com.cubrid.cubridmanager.core.cubrid.database.task.CheckDirTask;
import com.cubrid.cubridmanager.core.cubrid.database.task.CheckFileTask;
import com.cubrid.cubridmanager.core.cubrid.database.task.CreateDbTask;
import com.cubrid.cubridmanager.core.cubrid.database.task.GetDatabaseListTask;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.GetAutoAddVolumeInfo;
import com.cubrid.cubridmanager.core.cubrid.dbspace.task.SetAutoAddVolumeTask;
import com.cubrid.cubridmanager.core.cubrid.user.task.UpdateAddUserTask;
import com.cubrid.cubridmanager.core.utils.CoreUtils;
import com.cubrid.cubridmanager.core.utils.ModelUtil.YesNoType;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;
import com.cubrid.cubridmanager.ui.cubrid.database.dialog.CreateDirDialog;
import com.cubrid.cubridmanager.ui.cubrid.database.dialog.OverrideFileDialog;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

/**
 * This wizard is provided for creating database
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class CreateDatabaseWizard extends Wizard implements ITaskExecutorInterceptor {
    private GeneralInfoPage generalInfoPage = null;
    private VolumeInfoPage volumeInfoPage = null;
    private SetAutoAddVolumeInfoPage setAutoAddVolumeInfoPage = null;
    private DatabaseInfoPage databaseInfoPage = null;
    private SetDbaPasswordPage setDbaPasswordPage = null;

    private final CubridServer server;
    private boolean isCanFinished = true;
    private boolean isOverride = true;

    private TreeViewer viewer;
    private ICubridNode node;

    public CreateDatabaseWizard(CubridServer server, TreeViewer viewer, ICubridNode node) {
        setWindowTitle(com.cubrid.cubridmanager.ui.cubrid.database.Messages.titleCreateDbDialog);
        this.server = server;
        this.viewer = viewer;
        this.node = node;
    }

    public void addPages() {
        generalInfoPage = new GeneralInfoPage(server);
        addPage(generalInfoPage);
        volumeInfoPage = new VolumeInfoPage(server);
        addPage(volumeInfoPage);
        setAutoAddVolumeInfoPage = new SetAutoAddVolumeInfoPage(server);
        addPage(setAutoAddVolumeInfoPage);
        setDbaPasswordPage = new SetDbaPasswordPage();
        addPage(setDbaPasswordPage);
        databaseInfoPage = new DatabaseInfoPage(server);
        addPage(databaseInfoPage);
        WizardDialog dialog = (WizardDialog) getContainer();
        dialog.addPageChangedListener(volumeInfoPage);
        dialog.addPageChangedListener(setAutoAddVolumeInfoPage);
        dialog.addPageChangedListener(setDbaPasswordPage);
        dialog.addPageChangedListener(databaseInfoPage);
    }

    /**
     * Return whether can finish
     *
     * @return <code>true</code>if can finish;<code>false</code> otherwise
     */
    public boolean canFinish() {
        return getContainer().getCurrentPage() == databaseInfoPage;
    }

    /**
     * Called when user clicks Finish
     *
     * @return boolean
     */
    public boolean performFinish() {
        isCanFinished = true;
        isOverride = true;

        final String databaseName = generalInfoPage.getDatabaseName();
        String pageSize = generalInfoPage.getPageSize();
        String logPageSize = generalInfoPage.getLogPageSize();
        String generalPageNum = generalInfoPage.getGenericPageNum();
        String generalVolumePath = generalInfoPage.getGenericVolumePath();
        String charset = generalInfoPage.getCharset();

        final boolean isAutoStart = generalInfoPage.isAutoStart();
        TaskJobExecutor taskExcutor =
                new TaskJobExecutor() {
                    public IStatus exec(final IProgressMonitor monitor) {
                        Display.getDefault()
                                .syncExec(
                                        new Runnable() {
                                            public void run() {
                                                getShell().setVisible(false);
                                            }
                                        });

                        if (monitor.isCanceled()) {
                            isCanFinished = true;
                            Display.getDefault()
                                    .syncExec(
                                            new Runnable() {
                                                public void run() {
                                                    cancel();
                                                    close();
                                                }
                                            });

                            return Status.CANCEL_STATUS;
                        }

                        List<String> cubridConfContentList = null;
                        for (ITask task : taskList) {
                            if (task instanceof CreateDbTask) {
                                CreateDbTask createDbTask = (CreateDbTask) task;
                                createDbTask.setOverwriteConfigFile(isOverride);
                            } else if (task instanceof SetCubridConfParameterTask) {
                                if (cubridConfContentList == null) {
                                    continue;
                                } else {
                                    SetCubridConfParameterTask setParaTask =
                                            (SetCubridConfParameterTask) task;
                                    setParaTask.setConfContents(cubridConfContentList);
                                }
                            }
                            task.execute();

                            final String msg = task.getErrorMsg();
                            if (monitor.isCanceled()) {
                                Display.getDefault()
                                        .syncExec(
                                                new Runnable() {
                                                    public void run() {
                                                        cancel();
                                                        close();
                                                    }
                                                });
                                isCanFinished = true;

                                return Status.CANCEL_STATUS;
                            }

                            if (msg != null
                                    && msg.length() > 0
                                    && !monitor.isCanceled()
                                    && !isCanceled()) {
                                // start database failed
                                if (task instanceof CommonUpdateTask
                                        && task.getTaskname()
                                                .equals(CommonTaskName.START_DB_TASK_NAME)) {
                                    Display.getDefault()
                                            .syncExec(
                                                    new Runnable() {
                                                        public void run() {
                                                            CommonUITool.openErrorBox(
                                                                    getShell(),
                                                                    msg
                                                                            + StringUtil.NEWLINE
                                                                            + Messages
                                                                                    .createDBFailedMsg);
                                                        }
                                                    });
                                    return Status.OK_STATUS;
                                }
                                Display.getDefault()
                                        .syncExec(
                                                new Runnable() {
                                                    public void run() {
                                                        CommonUITool.openErrorBox(getShell(), msg);

                                                        isCanFinished = false;
                                                        getShell().setVisible(true);
                                                    }
                                                });

                                return Status.CANCEL_STATUS;
                            }

                            if (isCanceled()) {
                                return Status.CANCEL_STATUS;
                            }

                            if (task instanceof CheckDirTask) {
                                CheckDirTask checkDirTask = (CheckDirTask) task;
                                final String[] dirs = checkDirTask.getNoExistDirectory();
                                if (dirs != null && dirs.length > 0) {
                                    Display.getDefault()
                                            .syncExec(
                                                    new Runnable() {
                                                        public void run() {
                                                            CreateDirDialog dialog =
                                                                    new CreateDirDialog(getShell());
                                                            dialog.setDirs(dirs);
                                                            if (dialog.open()
                                                                    != IDialogConstants.OK_ID) {
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
                                    Display.getDefault()
                                            .syncExec(
                                                    new Runnable() {
                                                        public void run() {
                                                            OverrideFileDialog dialog =
                                                                    new OverrideFileDialog(
                                                                            getShell());
                                                            dialog.setFiles(files);
                                                            isOverride =
                                                                    dialog.open()
                                                                            == IDialogConstants
                                                                                    .OK_ID;
                                                            getShell().setVisible(!isOverride);
                                                        }
                                                    });
                                }
                            } else if (task instanceof GetCubridConfParameterTask) {
                                GetCubridConfParameterTask getCubridConfParameterTask =
                                        (GetCubridConfParameterTask) task;
                                List<String> cubridConfContentListCandidate1 = null;
                                if (isAutoStart) {
                                    cubridConfContentListCandidate1 =
                                            CoreUtils.addDatabaseToServiceServer(
                                                    getCubridConfParameterTask, null, databaseName);
                                }

                                List<String> cubridConfContentListCandidate2 = null;
                                if (CompatibleUtil.isNeedCheckHAModeOnNewDb(
                                        server.getServerInfo())) {
                                    cubridConfContentListCandidate2 =
                                            CoreUtils.changeHAModeFromCubridConf(
                                                    getCubridConfParameterTask,
                                                    cubridConfContentListCandidate1,
                                                    databaseName);
                                }

                                if (cubridConfContentListCandidate2 == null) {
                                    cubridConfContentList = cubridConfContentListCandidate1;
                                } else {
                                    cubridConfContentList = cubridConfContentListCandidate2;
                                }
                            }

                            if (!isCanFinished) {
                                return Status.CANCEL_STATUS;
                            }

                            if (monitor.isCanceled()) {
                                Display.getDefault()
                                        .syncExec(
                                                new Runnable() {
                                                    public void run() {
                                                        completeAll();
                                                        close();
                                                        cancel();
                                                    }
                                                });

                                return Status.CANCEL_STATUS;
                            }
                        }

                        return Status.OK_STATUS;
                    }

                    public void cancel() {
                        super.cancel();

                        GetDatabaseListTask getDatabaseListTask =
                                new GetDatabaseListTask(server.getServerInfo());
                        getDatabaseListTask.execute();

                        boolean isExist = false;
                        List<DatabaseInfo> databaseInfoList =
                                getDatabaseListTask.loadDatabaseInfo();
                        if (databaseInfoList != null) {
                            for (int i = 0; i < databaseInfoList.size(); i++) {
                                DatabaseInfo dbInfo = databaseInfoList.get(i);
                                String dbName = dbInfo.getDbName();
                                if (dbName.equals(databaseName)) {
                                    isExist = true;
                                }
                            }
                        }

                        if (isExist) {
                            CommonUpdateTask deleteTask =
                                    new CommonUpdateTask(
                                            CommonTaskName.DELETE_DATABASE_TASK_NAME,
                                            server.getServerInfo(),
                                            CommonSendMsg.getDeletedbSendMsg());
                            deleteTask.setDbName(databaseName);
                            deleteTask.setDelbackup(YesNoType.Y);
                            deleteTask.execute();
                        }
                    }

                    public void done(IJobChangeEvent event) {
                        if (event.getResult() == Status.OK_STATUS) {
                            Display.getDefault()
                                    .syncExec(
                                            new Runnable() {
                                                public void run() {
                                                    completeAll();
                                                    close();
                                                }
                                            });
                        }
                    }
                };

        CheckDirTask checkDirTask = new CheckDirTask(server.getServerInfo());
        CheckFileTask checkFileTask = new CheckFileTask(server.getServerInfo());
        CreateDbTask createDbTask = new CreateDbTask(server.getServerInfo());

        List<String> checkedDirsList = new ArrayList<String>();
        List<String> checkedFilesList = new ArrayList<String>();

        // add checked directory(general volume path)
        addVolumePath(checkedDirsList, generalVolumePath);

        String logPageNum = generalInfoPage.getLogPageNum();
        String logVolumePath = generalInfoPage.getLogVolumePath();
        // add checked directory(log volume path)
        addVolumePath(checkedDirsList, logVolumePath);

        List<Map<String, String>> volumeList = volumeInfoPage.getVolumeList();
        for (int i = 0; i < volumeList.size(); i++) {
            Map<String, String> map = volumeList.get(i);
            String volumeName = map.get("0");
            String volumePath = map.get("4");
            // add checked directory(additional volume path)
            addVolumePath(checkedDirsList, volumePath);
            // add checked file(additional volume)
            addVolumePath(
                    checkedFilesList,
                    volumePath + server.getServerInfo().getPathSeparator() + volumeName);
        }

        String[] dirs = new String[checkedDirsList.size()];
        checkDirTask.setDirectory(checkedDirsList.toArray(dirs));

        String[] files = new String[checkedFilesList.size()];
        checkFileTask.setFile(checkedFilesList.toArray(files));

        createDbTask.setDbName(databaseName);
        createDbTask.setPageSize(pageSize);
        if (logPageSize != null) {
            createDbTask.setLogPageSize(logPageSize);
        }
        if (charset != null) {
            createDbTask.setCharset(charset);
        }
        createDbTask.setNumPage(generalPageNum);
        createDbTask.setGeneralVolumePath(generalVolumePath);
        createDbTask.setLogSize(logPageNum);
        createDbTask.setLogVolumePath(logVolumePath);
        createDbTask.setExVolumes(volumeList);

        if (!checkedDirsList.isEmpty()) {
            taskExcutor.addTask(checkDirTask);
        }

        if (!checkedFilesList.isEmpty()) {
            taskExcutor.addTask(checkFileTask);
        }

        taskExcutor.addTask(createDbTask);

        // add set auto added volume
        GetAutoAddVolumeInfo returnInfo = setAutoAddVolumeInfoPage.getAutoAddVolumeInfo();
        if (returnInfo != null) {
            SetAutoAddVolumeTask setTask = new SetAutoAddVolumeTask(server.getServerInfo());
            setTask.setDbname(databaseName);
            setTask.setData(returnInfo.getData());
            setTask.setDataWarnOutofspace(returnInfo.getData_warn_outofspace());
            setTask.setDataExtPage(returnInfo.getData_ext_page());
            setTask.setIndex(returnInfo.getIndex());
            setTask.setIndexWarnOutofspace(returnInfo.getIndex_warn_outofspace());
            setTask.setIndexExtPage(returnInfo.getIndex_ext_page());
            taskExcutor.addTask(setTask);
        }

        GetCubridConfParameterTask getCubridConfParameterTask =
                new GetCubridConfParameterTask(server.getServerInfo());
        taskExcutor.addTask(getCubridConfParameterTask);
        SetCubridConfParameterTask setCubridConfParameterTask =
                new SetCubridConfParameterTask(server.getServerInfo());
        taskExcutor.addTask(setCubridConfParameterTask);

        // start database
        CommonUpdateTask startDbTask =
                new CommonUpdateTask(
                        CommonTaskName.START_DB_TASK_NAME,
                        server.getServerInfo(),
                        CommonSendMsg.getCommonDatabaseSendMsg());
        startDbTask.setDbName(databaseName);

        taskExcutor.addTask(startDbTask);

        // set dba password
        UpdateAddUserTask updateUserTask = new UpdateAddUserTask(server.getServerInfo(), false);
        UserSendObj userSendObj = new UserSendObj();
        userSendObj.setDbname(databaseName);
        userSendObj.setUsername("dba");
        String password = setDbaPasswordPage.getPassword();
        userSendObj.setUserpass(password);
        updateUserTask.setUserSendObj(userSendObj);
        taskExcutor.addTask(updateUserTask);

        JobFamily jobFamily = new JobFamily();
        String serverName = server.getName();
        jobFamily.setServerName(serverName);
        String jobName = Messages.msgCreateDbrearJobName + " - " + serverName;
        taskExcutor.schedule(jobName, jobFamily, true, Job.SHORT);

        return false; // the close is in the job
    }

    /**
     * Add volume path to list
     *
     * @param checkedList the checked list
     * @param volumePath the volume path
     */
    private void addVolumePath(List<String> checkedList, String volumePath) {
        boolean isExist = false;
        for (int i = 0; i < checkedList.size(); i++) {
            String volPath = checkedList.get(i);
            if (volumePath.equals(volPath)) {
                isExist = true;
                break;
            }
        }

        if (!isExist) {
            checkedList.add(volumePath);
        }
    }

    /** Close this wizard dialog */
    private void close() {
        if (getContainer() instanceof Dialog) {
            ((Dialog) getContainer()).close();
        }
    }

    /**
     * calculate the page num
     *
     * @param totalSize - unit(MB)
     * @param pageSize - unit(KB)
     * @return
     */
    public static long calcVolumePageNum(String totalSize, String pageSize) {
        boolean isValidVolumeSize =
                ValidateUtil.isNumber(totalSize) || ValidateUtil.isPositiveDouble(totalSize);

        if (pageSize != null && pageSize.trim().length() > 0) {
            double size = Double.parseDouble(pageSize);
            if (isValidVolumeSize) {
                double volumeSize = Double.parseDouble(totalSize);
                double pageNumber = (1024 * 1024 / size) * volumeSize;

                return Math.round(pageNumber);
            }
        }

        return -1;
    }

    public IStatus postTaskFinished(ITask task) {
        return Status.OK_STATUS;
    }

    public void completeAll() {
        CommonUITool.refreshNavigatorTree(viewer, node);
        String serverName = server.getName();
        final String jobName =
                Messages.msgCreateDbrearJobName
                        + " - "
                        + generalInfoPage.getDatabaseName()
                        + "@"
                        + serverName;
        CommonUITool.openInformationBox(
                Messages.titleSuccess, Messages.bind(Messages.msgCreateDBComplete, jobName));
    }
}
