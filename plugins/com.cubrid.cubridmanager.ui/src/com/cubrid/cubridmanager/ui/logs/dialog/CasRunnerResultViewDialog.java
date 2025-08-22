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
package com.cubrid.cubridmanager.ui.logs.dialog;

import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.DefaultCubridNode;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.logs.model.LogContentInfo;
import com.cubrid.cubridmanager.core.logs.task.GetLogListTask;
import com.cubrid.cubridmanager.core.logs.task.RemoveCasRunnerTmpFileTask;
import com.cubrid.cubridmanager.ui.logs.Messages;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * The dialog is used to show CasRunnerResult.
 *
 * @author wuyingshi
 * @version 1.0 - 2009-3-18 created by wuyingshi
 */
public class CasRunnerResultViewDialog extends CMTitleAreaDialog {

    private Text textArea = null;
    private Text textFiles = null;
    private Button buttonFirst = null;
    private Button buttonNext = null;
    private Button buttonPrev = null;
    private Button buttonEnd = null;
    private static final long LINE_NUM_TO_DISPLAY = 1000;
    private long lineStart = 1;
    private long lineEnd = LINE_NUM_TO_DISPLAY;
    private long lineTot = 0;
    private Button buttonFileBefore = null;
    private Button buttonFileNext = null;
    private int totalResultNum = 1;

    private int currentResultFileIndex = 1;
    private Composite composite;
    private String path = "";
    private String dbName = "";
    private DefaultCubridNode selection;

    /**
     * The constructor
     *
     * @param parentShell
     */
    public CasRunnerResultViewDialog(Shell parentShell) {
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

        composite = new Composite(parentComp, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = 0;
        layout.numColumns = 7;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        GridData gridData41 = CommonUITool.createGridData(1, 1, 110, -1);
        gridData41.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
        gridData41.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
        GridData gridData31 = CommonUITool.createGridData(1, 1, 130, -1);
        gridData31.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
        gridData31.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
        GridData gridData6 = CommonUITool.createGridData(1, 1, 60, -1);
        gridData6.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
        gridData6.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
        GridData gridData5 = CommonUITool.createGridData(1, 1, 60, -1);
        gridData5.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
        gridData5.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
        GridData gridData4 = CommonUITool.createGridData(1, 1, 60, -1);
        gridData4.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
        gridData4.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
        GridData gridData3 = CommonUITool.createGridData(1, 1, 60, -1);
        gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
        gridData3.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
        GridData gridData2 = CommonUITool.createGridData(1, 1, 60, -1);
        gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
        gridData2.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
        GridData gridData1 = CommonUITool.createGridData(1, 1, 145, -1);
        gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
        gridData1.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
        gridData1.grabExcessHorizontalSpace = true;
        GridData gridData = new GridData();
        gridData.heightHint = -1;
        gridData.widthHint = -1;
        gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData.grabExcessVerticalSpace = true;
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalSpan = 7;

        composite.addDisposeListener(
                new org.eclipse.swt.events.DisposeListener() {
                    public void widgetDisposed(org.eclipse.swt.events.DisposeEvent event) {
                        final RemoveCasRunnerTmpFileTask task =
                                new RemoveCasRunnerTmpFileTask(
                                        selection.getServer().getServerInfo());
                        task.setFileName(path + "." + String.valueOf(currentResultFileIndex - 1));
                        task.execute();
                    }
                });

        composite.addControlListener(
                new org.eclipse.swt.events.ControlAdapter() {
                    public void controlResized(org.eclipse.swt.events.ControlEvent event) {
                        if (composite.getSize().x < 504) {
                            composite.setSize(504, composite.getSize().y);
                        }
                        if (composite.getSize().y < 409) {
                            composite.setSize(composite.getSize().x, 409);
                        }
                    }
                });
        textArea =
                new Text(
                        composite, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.WRAP);
        textArea.setLayoutData(gridData);
        textFiles = new Text(composite, SWT.BORDER);
        textFiles.setEditable(false);
        textFiles.setLayoutData(gridData1);
        buttonFirst = new Button(composite, SWT.NONE);
        buttonFirst.setText("|<");
        buttonFirst.setLayoutData(gridData2);
        buttonFirst.addSelectionListener(
                new org.eclipse.swt.events.SelectionAdapter() {
                    public void widgetSelected(org.eclipse.swt.events.SelectionEvent event) {
                        lineStart = 1;
                        lineEnd = LINE_NUM_TO_DISPLAY;
                        connect();
                    }
                });
        buttonPrev = new Button(composite, SWT.NONE);
        buttonPrev.setText("<");
        buttonPrev.setLayoutData(gridData3);
        buttonPrev.addSelectionListener(
                new org.eclipse.swt.events.SelectionAdapter() {
                    public void widgetSelected(org.eclipse.swt.events.SelectionEvent event) {
                        lineStart -= LINE_NUM_TO_DISPLAY;
                        if (lineStart < 1) {
                            lineStart = 1;
                        }
                        lineEnd = lineStart + (LINE_NUM_TO_DISPLAY - 1);
                        connect();
                    }
                });
        buttonNext = new Button(composite, SWT.NONE);
        buttonNext.setText(">");
        buttonNext.setLayoutData(gridData4);
        buttonNext.addSelectionListener(
                new org.eclipse.swt.events.SelectionAdapter() {
                    public void widgetSelected(org.eclipse.swt.events.SelectionEvent event) {
                        lineStart = lineEnd + 1;
                        lineEnd += LINE_NUM_TO_DISPLAY;
                        connect();
                    }
                });
        buttonEnd = new Button(composite, SWT.NONE);
        buttonEnd.setText(">|");
        buttonEnd.setLayoutData(gridData5);
        buttonEnd.addSelectionListener(
                new org.eclipse.swt.events.SelectionAdapter() {
                    public void widgetSelected(org.eclipse.swt.events.SelectionEvent event) {
                        lineEnd = lineTot;
                        lineStart = ((lineTot - 1) / 1000) * 1000 + 1;
                        if (lineStart < 1) {
                            lineStart = 1;
                        }
                        connect();
                    }
                });

        buttonFileBefore = new Button(composite, SWT.NONE);
        buttonFileBefore.setText(Messages.buttonBeforeResultFile);
        buttonFileBefore.setLayoutData(gridData31);
        buttonFileBefore.addSelectionListener(
                new org.eclipse.swt.events.SelectionAdapter() {
                    public void widgetSelected(org.eclipse.swt.events.SelectionEvent event) {
                        if (currentResultFileIndex == 1) {
                            return;
                        }
                        if (currentResultFileIndex == 2) {
                            buttonFileBefore.setEnabled(false);
                        }
                        String title = "";
                        buttonFileNext.setEnabled(true);
                        currentResultFileIndex--;
                        lineStart = 1;
                        lineEnd = LINE_NUM_TO_DISPLAY;
                        title =
                                Messages.titleCasRunnerResult
                                        + currentResultFileIndex
                                        + "/"
                                        + totalResultNum;
                        setTitle(title);
                        connect();
                    }
                });
        buttonFileNext = new Button(composite, SWT.NONE);
        buttonFileNext.setText(Messages.buttonNextResultFile);
        buttonFileNext.setLayoutData(gridData41);
        buttonFileNext.addSelectionListener(
                new org.eclipse.swt.events.SelectionAdapter() {
                    public void widgetSelected(org.eclipse.swt.events.SelectionEvent event) {
                        if (currentResultFileIndex == totalResultNum) {
                            return;
                        }
                        if (currentResultFileIndex == totalResultNum - 1) {
                            buttonFileNext.setEnabled(false);
                        }
                        String title = "";
                        buttonFileBefore.setEnabled(true);
                        currentResultFileIndex++;
                        lineStart = 1;
                        lineEnd = LINE_NUM_TO_DISPLAY;
                        title =
                                Messages.titleCasRunnerResult
                                        + currentResultFileIndex
                                        + "/"
                                        + totalResultNum;
                        setTitle(title);
                        connect();
                    }
                });
        composite.pack();

        setTitle(Messages.titleCasRunnerResultDialog);
        setMessage(Messages.msgCasRunnerResultDialog);
        return parentComp;
    }

    /** each page of log connect */
    public void connect() {

        final GetLogListTask task = new GetLogListTask(selection.getServer().getServerInfo());

        task.setPath(path + "." + String.valueOf(currentResultFileIndex - 1));
        task.setDbName(dbName);
        task.setStart(Long.toString(lineStart));
        task.setEnd(Long.toString(lineEnd));
        task.execute();
        TaskExecutor taskExecutor = new CommonTaskExec(Messages.loadLogTaskName);
        taskExecutor.addTask(task);
        new ExecTaskWithProgress(taskExecutor).exec();
        if (taskExecutor.isSuccess()) {
            LogContentInfo logContentInfo = (LogContentInfo) task.getLogContent();
            this.setinfo(logContentInfo, selection);
        }
    }

    /**
     * initialize log view table.
     *
     * @param logPath String
     * @param dbName String
     * @param node DefaultCubridNode
     */
    public void connectInit(String logPath, String dbName, DefaultCubridNode node) {
        this.selection = node;
        final GetLogListTask taskGetLog = new GetLogListTask(node.getServer().getServerInfo());
        path = logPath;
        this.dbName = dbName;
        taskGetLog.setPath(path + "." + String.valueOf(currentResultFileIndex - 1));
        taskGetLog.setDbName(dbName);
        taskGetLog.setStart("1");
        taskGetLog.setEnd(Long.toString(LINE_NUM_TO_DISPLAY));
        TaskExecutor taskExecutor = new CommonTaskExec(Messages.loadLogTaskName);
        taskExecutor.addTask(taskGetLog);
        new ExecTaskWithProgress(taskExecutor).exec();
        if (taskExecutor.isSuccess()) {
            LogContentInfo logContentInfo = (LogContentInfo) taskGetLog.getLogContent();
            this.setinfo(logContentInfo, selection);
        }
    }

    /**
     * initialize some values
     *
     * @param logContentInfo LogContentInfo
     * @param node DefaultCubridNode
     */
    public void setinfo(LogContentInfo logContentInfo, DefaultCubridNode node) {
        if (logContentInfo == null) {
            textArea.setText(Messages.msgNullLogFile);
        } else {
            lineStart = Integer.parseInt(logContentInfo.getStart());
            lineEnd = Integer.parseInt(logContentInfo.getEnd());
            lineTot = Integer.parseInt(logContentInfo.getTotal());

            if (lineStart <= 0 && lineEnd <= 0) {
                textArea.setText(Messages.msgNullLogFile);
            } else {
                StringBuffer lines = new StringBuffer();
                for (int i = 0, n = logContentInfo.getLine().size(); i < n; i++) {
                    lines.append((String) logContentInfo.getLine().get(i) + "\n");
                }
                textArea.setText(lines.toString());
            }

            textFiles.setText(lineStart + "-" + lineEnd + " (" + lineTot + ")");

            if (lineStart <= 1) {
                buttonFirst.setEnabled(false);
                buttonPrev.setEnabled(false);
            } else {
                buttonFirst.setEnabled(true);
                buttonPrev.setEnabled(true);
            }

            if (lineEnd >= lineTot) {
                buttonEnd.setEnabled(false);
                buttonNext.setEnabled(false);
            } else {
                buttonEnd.setEnabled(true);
                buttonNext.setEnabled(true);
            }
            if (totalResultNum > 1) {
                buttonFileNext.setEnabled(true);
            } else {
                buttonFileNext.setEnabled(false);
            }
            buttonFileBefore.setEnabled(false);
        }
    }

    /** @see com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog#constrainShellSize() */
    protected void constrainShellSize() {
        super.constrainShellSize();
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.titleCasRunnerResultDialog);
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     * @param parent the button bar composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.buttonClose, true);
    }

    /**
     * get the totalResultNum
     *
     * @return the totalResultNum
     */
    public int getTotalResultNum() {
        return totalResultNum;
    }

    /**
     * set the totalResultNum
     *
     * @param totalResultNum int
     */
    public void setTotalResultNum(int totalResultNum) {
        this.totalResultNum = totalResultNum;
    }
}
