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
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.progress.TaskJobExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.common.task.CommonTaskName;
import com.cubrid.cubridmanager.core.common.task.CommonUpdateTask;
import com.cubrid.cubridmanager.core.cubrid.table.model.ClassItem;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;

/**
 * Show the Optimize database dialog
 *
 * @author robin 2009-3-11
 */
public class OptimizeDialog extends CMTitleAreaDialog {

    private static final Logger LOGGER = LogUtil.getLogger(OptimizeDialog.class);

    private Combo className;
    private Text databaseName;
    private CubridDatabase database = null;
    private Table volumeTable;
    private boolean isRunning = false;
    private List<ClassItem> userClassList = null;

    public OptimizeDialog() {
        super(null);
    }

    public OptimizeDialog(Shell parentShell) {
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
        final Composite composite = new Composite(parentComp, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        layout.marginWidth = 10;
        layout.marginHeight = 10;
        composite.setLayout(layout);

        final Group dbNameGroup = new Group(composite, SWT.NONE);
        layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginWidth = 10;
        layout.marginHeight = 10;
        dbNameGroup.setLayout(layout);
        final GridData gdDbNameGroup = new GridData(GridData.FILL_HORIZONTAL);
        dbNameGroup.setLayoutData(gdDbNameGroup);

        final Label dbNameLabel = new Label(dbNameGroup, SWT.LEFT | SWT.WRAP);
        dbNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        dbNameLabel.setText(Messages.lblOptimizeDbName);

        databaseName = new Text(dbNameGroup, SWT.BORDER);
        databaseName.setEnabled(false);
        final GridData gdDatabaseName = new GridData(SWT.FILL, SWT.FILL, true, false);
        databaseName.setLayoutData(gdDatabaseName);

        final Label optimizeLabel = new Label(dbNameGroup, SWT.WRAP);
        optimizeLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        optimizeLabel.setText(Messages.lblOptimizeClassName);

        GridData gdClassName = new org.eclipse.swt.layout.GridData(SWT.FILL, SWT.FILL, true, false);
        className = new Combo(dbNameGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
        className.setLayoutData(gdClassName);
        className.setVisibleItemCount(15);
        final Group descGroup = new Group(composite, SWT.NONE);
        layout = new GridLayout();
        layout.marginWidth = 10;
        layout.marginHeight = 10;
        descGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
        descGroup.setLayout(layout);
        descGroup.setText(Messages.grpOptimizeDesc);

        final Label descriptionLabel = new Label(descGroup, SWT.WRAP);
        descriptionLabel.setText(Messages.lblOptimizeDesc);
        descriptionLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        volumeTable =
                new Table(
                        composite,
                        SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.READ_ONLY);
        volumeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        volumeTable.setLinesVisible(false);
        volumeTable.setHeaderVisible(false);

        TableLayout tableLayout = new TableLayout();

        volumeTable.setLayout(tableLayout);

        final TableColumn currentVolumeColumn = new TableColumn(volumeTable, SWT.LEFT);
        currentVolumeColumn.setText("");
        currentVolumeColumn.pack();
        setMessage(Messages.msgOptimizeDbInformation);

        setTitle(Messages.titleOptimizeDbDialog);
        initial();
        return parentComp;
    }

    /** Initial the data */
    private void initial() {
        databaseName.setText(database.getName());
        className.add(Messages.msgAllClass, 0);
        int i = 0;
        for (ClassItem item : userClassList) {
            className.add(item.getClassname(), ++i);
        }
        className.select(0);
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();

        getShell().setSize(400, 520);
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.titleOptimizeDbDialog);
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
     * When button press,call it
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            if (verify()) {
                CommonUpdateTask task =
                        new CommonUpdateTask(
                                CommonTaskName.OPTIMIZE_DATABASE_TASK_NAME,
                                database.getServer().getServerInfo(),
                                CommonSendMsg.getOptimizeDbSendMsg(),
                                database.getDatabaseInfo().getCharSet());
                task.setDbName(database.getName());
                task.setClassName((className.getSelectionIndex() <= 0) ? "" : className.getText());
                exec(IDialogConstants.OK_ID, task, false, getShell());
            }
            return;
        }
        super.buttonPressed(buttonId);
    }

    /**
     * Verify the text
     *
     * @return <code>true</code> if it is valid;<code>false</code> otherwise
     */
    private boolean verify() {
        setErrorMessage(null);
        return true;
    }

    @Override
    protected int getShellStyle() {
        return SWT.MODELESS | SWT.RESIZE | SWT.TITLE | SWT.MAX | SWT.MIN;
    }

    public CubridDatabase getDatabase() {
        return database;
    }

    public void setDatabase(CubridDatabase database) {
        this.database = database;
    }

    /**
     * Execute to optimize the table
     *
     * @param buttonId the button id
     * @param task the task
     * @param cancelable whether it is cancelable
     * @param shell the shell
     */
    public void exec(final int buttonId, final SocketTask task, boolean cancelable, Shell shell) {

        TaskJobExecutor taskJobExecutor =
                new TaskJobExecutor() {
                    @Override
                    public IStatus exec(IProgressMonitor monitor) {

                        if (monitor.isCanceled()) {
                            return Status.CANCEL_STATUS;
                        }

                        for (final ITask t : taskList) {
                            t.execute();
                            final String msg = t.getErrorMsg();

                            if (monitor.isCanceled()) {
                                return Status.CANCEL_STATUS;
                            }
                            if (msg != null && msg.length() > 0 && !monitor.isCanceled()) {
                                return new Status(
                                        IStatus.ERROR, CubridManagerUIPlugin.PLUGIN_ID, msg);
                            } else {
                                Display.getDefault()
                                        .syncExec(
                                                new Runnable() {
                                                    public void run() {
                                                        TableItem item =
                                                                new TableItem(
                                                                        volumeTable, SWT.NONE);
                                                        if (t.getErrorMsg() == null) {
                                                            item.setText(
                                                                    Messages.bind(
                                                                            Messages
                                                                                    .errOptimizeSuccess,
                                                                            className.getText()));
                                                        } else {
                                                            CommonUITool.openInformationBox(
                                                                    getShell(),
                                                                    Messages.titleFailure,
                                                                    Messages.bind(
                                                                            Messages
                                                                                    .errOptimizeFail,
                                                                            className.getText(),
                                                                            task.getErrorMsg()));
                                                            item.setText(
                                                                    Messages.errOptimizeFail
                                                                            + className.getText()
                                                                            + "-"
                                                                            + task.getErrorMsg());
                                                        }
                                                        volumeTable.getColumn(0).pack();
                                                    }
                                                });
                            }
                            if (monitor.isCanceled()) {
                                return Status.CANCEL_STATUS;
                            }
                        }
                        return Status.OK_STATUS;
                    }
                };
        taskJobExecutor.addTask(task);

        String serverName = database.getServer().getName();
        String dbName = database.getName();
        String jobName = Messages.titleOptimizeDbDialog + " - " + dbName + "@" + serverName;
        taskJobExecutor.schedule(jobName, null, false, Job.SHORT);
    }

    /**
     * Execute task that is responsible to get class list
     *
     * @param buttonId the button id
     * @param task the task
     * @param cancelable whether it is cancelable
     * @param shell the shell
     */
    public void executeGetClassListTask(
            final int buttonId, final ITask task, boolean cancelable, Shell shell) {
        final Display display = shell.getDisplay();
        try {
            new ProgressMonitorDialog(getShell())
                    .run(
                            true,
                            cancelable,
                            new IRunnableWithProgress() {
                                public void run(final IProgressMonitor monitor)
                                        throws InvocationTargetException, InterruptedException {
                                    monitor.beginTask(null, IProgressMonitor.UNKNOWN);

                                    if (monitor.isCanceled()) {
                                        return;
                                    }
                                    isRunning = true;
                                    Thread thread =
                                            new Thread() {
                                                public void run() {
                                                    if (monitor.isCanceled()) {
                                                        return;
                                                    }
                                                    task.execute();
                                                    if (monitor.isCanceled()) {
                                                        return;
                                                    }
                                                    final String msg = task.getErrorMsg();
                                                    if (msg != null
                                                            && msg.length() > 0
                                                            && !monitor.isCanceled()) {
                                                        display.syncExec(
                                                                new Runnable() {
                                                                    public void run() {
                                                                        CommonUITool.openErrorBox(
                                                                                getShell(), msg);
                                                                    }
                                                                });
                                                        isRunning = false;
                                                        return;
                                                    }

                                                    isRunning = false;
                                                    // display.syncExec(new Runnable() {
                                                    // public void run() {
                                                    // setReturnCode(buttonId);
                                                    // close();
                                                    // }
                                                    // });
                                                }
                                            };
                                    thread.start();
                                    while (!monitor.isCanceled() && isRunning) {
                                        Thread.sleep(1);
                                    }
                                    if (monitor.isCanceled()) {
                                        task.cancel();
                                    }
                                    monitor.done();
                                }
                            });
        } catch (InvocationTargetException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public List<ClassItem> getUserClassList() {
        return userClassList;
    }

    public void setUserClassList(List<ClassItem> userClassList) {
        this.userClassList = userClassList;
    }
}
