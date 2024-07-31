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
package com.cubrid.cubridmanager.ui.logs.editor;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.PageUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.part.CubridEditorPart;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.progress.TaskJobExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.logs.model.LogContentInfo;
import com.cubrid.cubridmanager.core.logs.model.ManagerLogInfo;
import com.cubrid.cubridmanager.core.logs.model.ManagerLogInfos;
import com.cubrid.cubridmanager.core.logs.task.ErrorTraceTask;
import com.cubrid.cubridmanager.core.logs.task.GetLogListTask;
import com.cubrid.cubridmanager.core.logs.task.GetManagerLogListTask;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.logs.Messages;
import com.cubrid.cubridmanager.ui.logs.dialog.LogContentDetailDialog;
import com.cubrid.cubridmanager.ui.logs.dialog.ShowErrorTraceInfoDialog;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.slf4j.Logger;

/**
 * This query editor part is used to view log.
 *
 * @author wuyingshi
 * @version 1.0 - 2009-3-10 created by wuyingshi
 */
public class LogEditorPart extends CubridEditorPart {

    private static final Logger LOGGER = LogUtil.getLogger(LogEditorPart.class);
    public static final String ID = "com.cubrid.cubridmanager.ui.logs.editor.LogEditorPart";
    private Text pageText = null;
    private Button buttonFirst = null;
    private Button buttonNext = null;
    private Button buttonPrev = null;
    private Button buttonEnd = null;
    private Combo charsetCombo = null;
    private String charsetName = StringUtil.getDefaultCharset();
    private Table table = null;

    private long lineStart = 1;
    private long lineEnd = 100;
    private long lineTot = 0;
    // start page info
    private static final int PAGESIZE = 100;
    private static final int CNTRECORD = 0;
    private PageUtil pageInfo = null;
    private String path = "";
    // end page info
    private static String blank = "  ";
    private final LogComparator comparator = new LogComparator();
    private List<ManagerLogInfo> accesslogList = new ArrayList<ManagerLogInfo>();
    private List<ManagerLogInfo> errorlogList = new ArrayList<ManagerLogInfo>();

    /**
     * @see com.cubrid.common.ui.spi.part.CubridEditorPart#init(org.eclipse.ui.IEditorSite,
     *     org.eclipse.ui.IEditorInput)
     * @param site the editor site
     * @param input the editor input
     * @exception PartInitException if this editor was not initialized successfully
     */
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        super.init(site, input);
        if (this.cubridNode != null) {
            String nodeType = this.cubridNode.getType();
            String partName = "";

            if (CubridNodeType.BROKER_SQL_LOG.equals(nodeType)) {
                partName = cubridNode.getParent().getLabel() + " - " + input.getName();
            } else if (CubridNodeType.LOGS_BROKER_ACCESS_LOG.equals(nodeType)
                    || CubridNodeType.LOGS_BROKER_ERROR_LOG.equals(nodeType)
                    || CubridNodeType.LOGS_BROKER_ADMIN_LOG.equals(nodeType)) {
                partName = cubridNode.getParent().getParent().getLabel();
                partName =
                        partName
                                + "/"
                                + cubridNode.getParent().getLabel()
                                + " - "
                                + input.getName();
            } else if (CubridNodeType.LOGS_MANAGER_ACCESS_LOG.equals(nodeType)
                    || CubridNodeType.LOGS_MANAGER_ERROR_LOG.equals(nodeType)
                    || CubridNodeType.LOGS_SERVER_DATABASE_LOG.equals(nodeType)) {
                if (cubridNode.getParent() == null) {
                    partName = input.getName();
                } else {
                    partName = cubridNode.getParent().getLabel() + " - " + input.getName();
                }
            } else if (CubridNodeType.LOGS_APPLY_DATABASE_LOG.equals(nodeType)
                    || CubridNodeType.LOGS_COPY_DATABASE_LOG.equals(nodeType)) {
                partName = input.getName();
            } else if (CubridNodeType.DATABASE.equals(nodeType)) {
                partName = Messages.replErrorLogViewName + " - " + input.getName();
            } else {
                partName = "";
            }

            if (cubridNode.getServer() != null) {
                partName +=
                        "@"
                                + cubridNode.getServer().getLabel()
                                + ":"
                                + cubridNode.getServer().getMonPort();
            }
            setPartName(partName);
        }
        if (input.getImageDescriptor() != null) {
            this.setTitleImage(input.getImageDescriptor().createImage());
        }
    }

    /**
     * set part name
     *
     * @param partName
     */
    public void setShowLogPartName(String partName) {
        setPartName(partName);
    }
    /**
     * Build the popup menu
     *
     * @param menuManager IMenuManager
     * @param type String
     */
    private void buildPopupMenu(final IMenuManager menuManager, final String type) {
        // copy action
        Action copyAction =
                new Action(Messages.bind(Messages.contextCopy, "Ctrl+C")) {
                    public void run() {
                        BusyIndicator.showWhile(
                                Display.getDefault(),
                                new Runnable() {
                                    public void run() {
                                        copyDataToClipboard(type);
                                    }
                                });
                    }
                };
        menuManager.add(copyAction);
        // Add error trace action
        if (CubridNodeType.LOGS_BROKER_ERROR_LOG.equals(type)) {
            // trace error action
            Action errorTraceAction =
                    new Action(Messages.errorTraceActionName) {
                        public void run() {
                            BusyIndicator.showWhile(
                                    Display.getDefault(),
                                    new Runnable() {
                                        public void run() {
                                            TableItem[] items = table.getSelection();
                                            if (items != null && items.length == 1) {
                                                final String time = items[0].getText(1);
                                                final String errorId = items[0].getText(5);
                                                showErrorTraceResult(errorId, time);
                                            }
                                        }
                                    });
                        }
                    };
            errorTraceAction.setEnabled(false);
            menuManager.add(errorTraceAction);

            TableItem[] items = table.getSelection();
            if (items != null && items.length == 1) {
                final String errorType = items[0].getText(2);
                if ("SYNTAX ERROR".equalsIgnoreCase(errorType.trim())) {
                    errorTraceAction.setEnabled(true);
                }
            }
        }
    }

    /**
     * Show the error trace log dialog
     *
     * @param errorId String
     * @param time String
     */
    private void showErrorTraceResult(String errorId, String time) {
        ErrorTraceTask task = new ErrorTraceTask(cubridNode.getServer().getServerInfo());
        String logPath = path;
        logPath = logPath.replaceAll("error_log", "sql_log").replaceAll("\\.err", "\\.sql\\.log");
        task.setLogPath(logPath);
        task.setErrId(errorId);
        String errorTime = time.trim().replaceAll("/\\d{2} ", " ");
        task.setErrTime(errorTime);
        task.execute();
        if (task.getErrorMsg() != null && task.getErrorMsg().trim().length() > 0) {
            CommonUITool.openErrorBox(task.getErrorMsg());
            return;
        }
        StringBuffer strBuffer = new StringBuffer();
        List<String> errorLogList = task.getErrorLogs();
        for (int i = 0; errorLogList != null && i < errorLogList.size(); i++) {
            strBuffer.append(errorLogList.get(i) + "\r\n");
        }
        ShowErrorTraceInfoDialog dialog = new ShowErrorTraceInfoDialog(getEditorSite().getShell());
        dialog.setResultInfoStr(strBuffer.toString());
        dialog.open();
    }

    /**
     * Copy the selected data to clipboard
     *
     * @param type String
     */
    private void copyDataToClipboard(String type) {
        TextTransfer textTransfer = TextTransfer.getInstance();
        Clipboard clipboard = CommonUITool.getClipboard();
        StringBuilder content = new StringBuilder();

        TableItem[] items = table.getSelection();
        for (int i = 0; i < items.length; i++) {
            if (CubridNodeType.BROKER_SQL_LOG.equals(type)) {
                content.append(items[i].getText(1) + System.getProperty("line.separator"));
            }
            if (CubridNodeType.LOGS_BROKER_ERROR_LOG.equals(type)) {
                content.append(
                        items[i].getText(1)
                                + blank
                                + items[i].getText(2)
                                + blank
                                + items[i].getText(3)
                                + blank
                                + items[i].getText(4)
                                + blank
                                + items[i].getText(5)
                                + blank
                                + items[i].getText(6)
                                + blank
                                + System.getProperty("line.separator"));
            }
            if (CubridNodeType.LOGS_MANAGER_ERROR_LOG.equals(type)) {
                content.append(
                        items[i].getText(1)
                                + blank
                                + items[i].getText(2)
                                + blank
                                + items[i].getText(3)
                                + blank
                                + items[i].getText(4)
                                + blank
                                + System.getProperty("line.separator"));
            }
            if (CubridNodeType.LOGS_SERVER_DATABASE_LOG.equals(type)
                    || CubridNodeType.LOGS_APPLY_DATABASE_LOG.equals(type)
                    || CubridNodeType.LOGS_COPY_DATABASE_LOG.equals(type)) {
                content.append(
                        items[i].getText(1)
                                + blank
                                + items[i].getText(2)
                                + blank
                                + items[i].getText(3)
                                + blank
                                + items[i].getText(4)
                                + blank
                                + items[i].getText(5)
                                + blank
                                + items[i].getText(6)
                                + blank
                                + System.getProperty("line.separator"));
            }
        }

        String data = content.toString();
        if (data != null && !data.equals("")) {
            clipboard.setContents(new Object[] {data}, new Transfer[] {textTransfer});
        }
    }

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     * @param parent the parent control
     * @see IWorkbenchPart
     */
    public void createPartControl(Composite parent) {
        final Composite compositeLog = new Composite(parent, SWT.NONE);
        {
            GridLayout gridLayoutLog = new GridLayout();
            gridLayoutLog.verticalSpacing = 0;
            gridLayoutLog.marginWidth = 0;
            gridLayoutLog.marginHeight = 0;
            gridLayoutLog.horizontalSpacing = 5;
            gridLayoutLog.numColumns = 8;
            compositeLog.setLayout(gridLayoutLog);
        }

        final String type = this.cubridNode.getType();

        table = new Table(compositeLog, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
        {
            table.setHeaderVisible(true);
            GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
            gridData.horizontalSpan = 8;
            gridData.verticalSpan = 10;
            gridData.heightHint = 600;
            table.setLayoutData(gridData);
            table.setLinesVisible(true);
        }

        // fill in context menu
        if (CubridNodeType.BROKER_SQL_LOG.equals(type)
                || CubridNodeType.LOGS_BROKER_ERROR_LOG.equals(type)
                || CubridNodeType.LOGS_MANAGER_ERROR_LOG.equals(type)
                || CubridNodeType.LOGS_SERVER_DATABASE_LOG.equals(type)
                || CubridNodeType.LOGS_APPLY_DATABASE_LOG.equals(type)
                || CubridNodeType.LOGS_COPY_DATABASE_LOG.equals(type)) {

            // Create the context menu
            MenuManager contextMenuManager = new MenuManager();
            contextMenuManager.setRemoveAllWhenShown(true);
            contextMenuManager.addMenuListener(
                    new IMenuListener() {
                        public void menuAboutToShow(IMenuManager manager) {
                            buildPopupMenu(manager, type);
                        }
                    });
            Menu contextMenu = contextMenuManager.createContextMenu(table);
            table.setMenu(contextMenu);
            // Add listener
            table.addKeyListener(
                    new KeyAdapter() {
                        public void keyPressed(KeyEvent event) {
                            if ((event.stateMask & SWT.CTRL) != 0
                                    && (event.stateMask & SWT.SHIFT) == 0
                                    && event.keyCode == 'c') {
                                copyDataToClipboard(type);
                            }
                        }
                    });
        }

        table.addListener(
                SWT.MouseDoubleClick,
                new Listener() {
                    public void handleEvent(Event event) {
                        if (CubridNodeType.BROKER_SQL_LOG.equals(cubridNode.getType())) {
                            TableItem[] items = table.getSelection();
                            if (items != null && items.length == 1) {
                                final String content = items[0].getText(1);
                                new LogContentDetailDialog(getSite().getShell(), content).open();
                            }
                        } else if (CubridNodeType.LOGS_MANAGER_ERROR_LOG.equals(
                                cubridNode.getType())) {
                            TableItem[] items = table.getSelection();
                            if (items != null && items.length == 1) {
                                final String content = items[0].getText(4);
                                new LogContentDetailDialog(getSite().getShell(), content).open();
                            }
                        } else if (CubridNodeType.LOGS_BROKER_ERROR_LOG.equals(
                                cubridNode.getType())) {
                            TableItem[] items = table.getSelection();
                            if (items != null && items.length == 1) {
                                final String content = items[0].getText(6);
                                new LogContentDetailDialog(getSite().getShell(), content).open();
                            }
                        }
                    }
                });

        // add bottom composite for page
        Label labelCharset = new Label(compositeLog, SWT.NONE);
        {
            labelCharset.setText(Messages.labelCharset);
            charsetCombo = new Combo(compositeLog, SWT.BORDER);
            final GridData gdCharsetText = CommonUITool.createGridData(1, 1, -1, -1);
            charsetCombo.setLayoutData(gdCharsetText);
            charsetCombo.setItems(QueryOptions.getAllCharset(null));
            charsetCombo.setText(charsetName);
        }

        Button viewLogBtn = new Button(compositeLog, SWT.NONE);
        {
            viewLogBtn.setText(Messages.viewLogJobName);
            viewLogBtn.setLayoutData(CommonUITool.createGridData(1, 1, 100, -1));
            viewLogBtn.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            String charsetName = charsetCombo.getText();
                            try {
                                "".getBytes(charsetName);
                            } catch (UnsupportedEncodingException e) {
                                CommonUITool.openErrorBox(Messages.errCharset);
                                charsetCombo.setFocus();
                                return;
                            }
                            setCharsetName(charsetName);
                            if (!CubridNodeType.LOGS_MANAGER_ERROR_LOG.equals(type)
                                    && !CubridNodeType.LOGS_MANAGER_ACCESS_LOG.equals(type)) {
                                connect(false);
                            }
                            if (CubridNodeType.LOGS_MANAGER_ERROR_LOG.equals(type)
                                    || CubridNodeType.LOGS_MANAGER_ACCESS_LOG.equals(type)) {
                                managerConnect();
                                updateManagerLogTable();
                            }
                        }
                    });
        }
        // page button
        if (!CubridNodeType.LOGS_MANAGER_ERROR_LOG.equals(type)
                && !CubridNodeType.LOGS_MANAGER_ACCESS_LOG.equals(type)) {

            pageText = new Text(compositeLog, SWT.BORDER);
            {
                pageText.setVisible(true);
                pageText.setEditable(false);
                GridData gridData = createPageBtnGridData(150);
                gridData.grabExcessHorizontalSpace = true;
                pageText.setLayoutData(gridData);
            }

            buttonFirst = new Button(compositeLog, SWT.NONE);
            {
                buttonFirst.setVisible(true);
                buttonFirst.setText("|<");
                buttonFirst.setLayoutData(createPageBtnGridData(60));
                buttonFirst.addSelectionListener(
                        new SelectionAdapter() {
                            public void widgetSelected(SelectionEvent event) {
                                lineStart = 1;
                                lineEnd = 100;
                                connect(false);
                            }
                        });
            }
            buttonPrev = new Button(compositeLog, SWT.NONE);
            {
                buttonPrev.setVisible(true);
                buttonPrev.setText("<");
                buttonPrev.setLayoutData(createPageBtnGridData(60));
                buttonPrev.addSelectionListener(
                        new SelectionAdapter() {
                            public void widgetSelected(SelectionEvent event) {
                                lineStart -= 100;
                                if (lineStart < 1) {
                                    lineStart = 1;
                                }
                                lineEnd = lineStart + 99;
                                connect(false);
                            }
                        });
            }
            buttonNext = new Button(compositeLog, SWT.NONE);
            {
                buttonNext.setVisible(true);
                buttonNext.setText(">");
                buttonNext.setLayoutData(createPageBtnGridData(60));
                buttonNext.addSelectionListener(
                        new SelectionAdapter() {
                            public void widgetSelected(SelectionEvent event) {
                                lineStart += 100;
                                if (lineStart > lineTot) {
                                    lineStart = lineTot;
                                }
                                lineEnd = lineStart + 99;
                                if (lineEnd > lineTot) {
                                    lineEnd = lineTot;
                                }
                                connect(false);
                            }
                        });
            }
            buttonEnd = new Button(compositeLog, SWT.NONE);
            {
                buttonEnd.setVisible(true);
                buttonEnd.setText(">|");
                buttonEnd.setLayoutData(createPageBtnGridData(60));
                buttonEnd.addSelectionListener(
                        new SelectionAdapter() {
                            public void widgetSelected(SelectionEvent event) {
                                lineEnd = lineTot;
                                lineStart = lineEnd - lineTot % 100 + 1;
                                connect(false);
                            }
                        });
            }
        }
        // manager log page button
        if (CubridNodeType.LOGS_MANAGER_ERROR_LOG.equals(type)
                || CubridNodeType.LOGS_MANAGER_ACCESS_LOG.equals(type)) {

            pageText = new Text(compositeLog, SWT.BORDER);
            {
                pageText.setVisible(true);
                pageText.setEditable(false);
                GridData gridData = createPageBtnGridData(150);
                gridData.grabExcessHorizontalSpace = true;
                pageText.setLayoutData(gridData);
            }

            buttonFirst = new Button(compositeLog, SWT.NONE);
            buttonFirst.setVisible(true);
            buttonFirst.setText("|<");
            buttonFirst.setLayoutData(createPageBtnGridData(60));
            buttonFirst.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            pageInfo.setCurrentPage(1);
                            updateManagerLogTable();
                        }
                    });
            buttonPrev = new Button(compositeLog, SWT.NONE);
            buttonPrev.setVisible(true);
            buttonPrev.setText("<");
            buttonPrev.setLayoutData(createPageBtnGridData(60));
            buttonPrev.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            pageInfo.setCurrentPage(pageInfo.getCurrentPage() - 1);
                            updateManagerLogTable();
                        }
                    });
            buttonNext = new Button(compositeLog, SWT.NONE);
            buttonNext.setVisible(true);
            buttonNext.setText(">");
            buttonNext.setLayoutData(createPageBtnGridData(60));
            buttonNext.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            pageInfo.setCurrentPage(pageInfo.getCurrentPage() + 1);
                            updateManagerLogTable();
                        }
                    });
            buttonEnd = new Button(compositeLog, SWT.NONE);
            buttonEnd.setVisible(true);
            buttonEnd.setText(">|");
            buttonEnd.setLayoutData(createPageBtnGridData(60));
            buttonEnd.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            pageInfo.setCurrentPage(pageInfo.getPages());
                            updateManagerLogTable();
                        }
                    });
        }
        compositeLog.pack();
    }

    /**
     * Create the button gridData
     *
     * @param widthHint int
     * @return GridData
     */
    private GridData createPageBtnGridData(int widthHint) {
        GridData gridData = CommonUITool.createGridData(1, 1, widthHint, -1);
        gridData.horizontalAlignment = GridData.END;
        gridData.verticalAlignment = GridData.CENTER;
        return gridData;
    }

    /**
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     * @param monitor IProgressMonitor
     */
    public void doSave(IProgressMonitor monitor) {
        //
    }

    /** @see org.eclipse.ui.part.EditorPart#doSaveAs() */
    public void doSaveAs() {
        //
    }

    /**
     * @see org.eclipse.ui.part.EditorPart#isDirty()
     * @return boolean
     */
    public boolean isDirty() {
        return false;
    }

    /**
     * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
     * @return boolean
     */
    public boolean isSaveAsAllowed() {
        return false;
    }

    /**
     * Each page of log connect
     *
     * @param isCreateColumn boolean
     */
    public void connect(final boolean isCreateColumn) {
        GetLogListTask task = null;
        if (charsetName == null) {
            task = new GetLogListTask(this.cubridNode.getServer().getServerInfo());
        } else {
            task = new GetLogListTask(this.cubridNode.getServer().getServerInfo(), charsetName);
        }
        task.setPath(path);
        task.setStart(Long.toString(lineStart));
        task.setEnd(Long.toString(lineEnd));

        TaskJobExecutor taskJobExecutor =
                new TaskJobExecutor() {
                    public IStatus exec(IProgressMonitor monitor) {
                        if (monitor.isCanceled()) {
                            return Status.CANCEL_STATUS;
                        }
                        for (final ITask task : taskList) {
                            task.execute();
                            final String msg = task.getErrorMsg();
                            if (monitor.isCanceled()) {
                                return Status.CANCEL_STATUS;
                            }
                            if (msg != null && msg.length() > 0 && !monitor.isCanceled()) {
                                return new Status(
                                        IStatus.ERROR, CubridManagerUIPlugin.PLUGIN_ID, msg);
                            } else {
                                Display.getDefault()
                                        .asyncExec(
                                                new Runnable() {
                                                    public void run() {
                                                        if (task instanceof GetLogListTask) {
                                                            GetLogListTask getLogListTask =
                                                                    (GetLogListTask) task;
                                                            LogContentInfo logContentInfo =
                                                                    (LogContentInfo)
                                                                            getLogListTask
                                                                                    .getLogContent();
                                                            setTableInfo(
                                                                    logContentInfo, isCreateColumn);
                                                        }
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
        String jobName =
                Messages.viewLogJobName
                        + " - "
                        + cubridNode.getName()
                        + "@"
                        + cubridNode.getServer().getName();
        taskJobExecutor.schedule(jobName, null, false, Job.SHORT);
    }

    /** each page of manager log connect */
    public void managerConnect() {
        GetManagerLogListTask task = null;
        if (charsetName == null) {
            task = new GetManagerLogListTask(this.cubridNode.getServer().getServerInfo());
        } else {
            task =
                    new GetManagerLogListTask(
                            this.cubridNode.getServer().getServerInfo(), charsetName);
        }

        TaskJobExecutor taskJobExecutor =
                new TaskJobExecutor() {
                    public IStatus exec(IProgressMonitor monitor) {
                        if (monitor.isCanceled()) {
                            return Status.CANCEL_STATUS;
                        }
                        for (final ITask task : taskList) {
                            task.execute();
                            final String msg = task.getErrorMsg();
                            if (monitor.isCanceled()) {
                                return Status.CANCEL_STATUS;
                            }
                            if (msg != null && msg.length() > 0 && !monitor.isCanceled()) {
                                return new Status(
                                        IStatus.ERROR, CubridManagerUIPlugin.PLUGIN_ID, msg);
                            } else {
                                Display.getDefault()
                                        .asyncExec(
                                                new Runnable() {
                                                    public void run() {
                                                        GetManagerLogListTask getLogListTask =
                                                                (GetManagerLogListTask) task;
                                                        ManagerLogInfos managerLogInfos =
                                                                (ManagerLogInfos)
                                                                        getLogListTask
                                                                                .getLogContent();
                                                        setManagerLogInfo(managerLogInfos, false);
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
        String jobName =
                Messages.viewLogJobName
                        + " - "
                        + cubridNode.getName()
                        + "@"
                        + cubridNode.getServer().getName();
        taskJobExecutor.schedule(jobName, null, false, Job.SHORT);
    }

    /**
     * Initialize some values of common logs.
     *
     * @param logContentInfo LogContentInfo
     * @param isCreateColumn boolean
     */
    public void setTableInfo(LogContentInfo logContentInfo, boolean isCreateColumn) {
        if (table == null || table.isDisposed()) {
            return;
        }
        table.removeAll();
        if (logContentInfo == null) {
            buttonFirst.setEnabled(false);
            buttonPrev.setEnabled(false);
            buttonEnd.setEnabled(false);
            buttonNext.setEnabled(false);

            disposeTableColumn();
            TableColumn tblColumn = new TableColumn(table, SWT.LEFT);
            tblColumn.setText(Messages.msgNullLogFile);
            tblColumn.setWidth(500);
        } else {
            path = logContentInfo.getPath();
            lineStart = Integer.parseInt(logContentInfo.getStart());
            lineEnd = Integer.parseInt(logContentInfo.getEnd());
            lineTot = Integer.parseInt(logContentInfo.getTotal());
            pageText.setText(lineStart + "-" + lineEnd + " (" + lineTot + ")");
            if (lineStart <= 100) {
                buttonFirst.setEnabled(false);
                buttonPrev.setEnabled(false);
                buttonEnd.setEnabled(false);
                buttonNext.setEnabled(false);
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
            TableItem item;
            if (lineStart <= 0 && lineEnd <= 0) {
                disposeTableColumn();
                TableColumn tblColumn = new TableColumn(table, SWT.LEFT);
                tblColumn.setText(Messages.msgNullLogFile);
                tblColumn.setWidth(300);
                item = new TableItem(table, SWT.NONE);
                item.setText(Messages.msgNullLogFile);
            } else {
                int j = 0;
                if (lineStart >= 1) {
                    j = (int) lineStart;
                }
                if (CubridNodeType.LOGS_BROKER_ACCESS_LOG.equals(this.cubridNode.getType())) {
                    if (isCreateColumn) {
                        String[] columnNames = {
                            Messages.tableNumber,
                            Messages.tableCasId,
                            Messages.tableIp,
                            Messages.tableStartTime,
                            Messages.tableEndTime,
                            Messages.tableElapsedTime,
                            Messages.tableProcessId,
                            Messages.tableErrorInfo
                        };
                        int[] widths = {50, 60, 100, 150, 150, 100, 80, 250};
                        int[] aligns = {
                            SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT,
                            SWT.LEFT
                        };
                        createTableColumn(columnNames, widths, aligns);
                    }

                    String str = "";
                    for (int i = 0, n = logContentInfo.getLine().size(); i < n; i++) {
                        str = (String) logContentInfo.getLine().get(i);
                        item = new TableItem(table, SWT.NONE);
                        item.setText(0, Integer.toString(j + i));
                        item.setText(1, str.substring(0, 1));
                        item.setText(2, str.substring(2, str.indexOf(" - -")));
                        item.setText(3, str.substring(str.indexOf(" ", 40) + 1, str.indexOf(" ~")));
                        item.setText(
                                4,
                                str.substring(
                                        str.indexOf(" ~") + 3,
                                        str.indexOf(" ", str.indexOf(" ~") + 15)));
                        item.setText(
                                5,
                                calDate(
                                        str.substring(str.indexOf(" ", 40) + 1, str.indexOf(" ~")),
                                        str.substring(
                                                str.indexOf(" ~") + 3,
                                                str.indexOf(" ", str.indexOf(" ~") + 15))));
                        item.setText(
                                6,
                                str.substring(
                                        str.indexOf(" ", str.indexOf(" ~") + 15) + 1,
                                        str.indexOf(
                                                " ",
                                                str.indexOf(" ", str.indexOf(" ~") + 15) + 1)));
                        if (("- -1"
                                .equals(
                                        str.substring(
                                                str.indexOf(
                                                                " ",
                                                                str.indexOf(
                                                                                " ",
                                                                                str.indexOf(" ~")
                                                                                        + 15)
                                                                        + 1)
                                                        + 1)))) {
                            item.setText(7, " ");
                        } else {
                            item.setText(
                                    7,
                                    str.substring(
                                            str.indexOf(
                                                            " ",
                                                            str.indexOf(" ", str.indexOf(" ~") + 15)
                                                                    + 1)
                                                    + 1));
                        }
                    }
                } else if (CubridNodeType.LOGS_BROKER_ERROR_LOG.equals(cubridNode.getType())
                        || CubridNodeType.LOGS_SERVER_DATABASE_LOG.equals(cubridNode.getType())
                        || CubridNodeType.LOGS_APPLY_DATABASE_LOG.equals(cubridNode.getType())
                        || CubridNodeType.LOGS_COPY_DATABASE_LOG.equals(cubridNode.getType())) {
                    if (isCreateColumn) {
                        String[] columnNames = {
                            Messages.tableNumber,
                            Messages.tableTime,
                            Messages.tableErrorType,
                            Messages.tableErrorCode,
                            Messages.tableTranId,
                            Messages.tableErrorId,
                            Messages.tableErrorMsg
                        };
                        int[] widths = {50, 150, 100, 75, 60, 70, 450};
                        int[] aligns = {
                            SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT,
                            SWT.LEFT
                        };
                        createTableColumn(columnNames, widths, aligns);
                    }

                    String str = "";
                    int errorNo = 1; // aim at errorlog NO
                    String errMsg = "";
                    for (int i = 0, n = logContentInfo.getLine().size(); i < n; i++) {
                        str = (String) logContentInfo.getLine().get(i);
                        if (str.trim().length() > 0 && !"***".equals(str.substring(0, 3))) {
                            // if ("Time".equals(str.substring(0, 4))) {
                            if (str.toLowerCase(Locale.getDefault()).startsWith("time:")) {
                                item = new TableItem(table, SWT.NONE);
                                item.setText(0, Integer.toString(errorNo));
                                item.setText(1, str.substring(6, str.indexOf(" - ")));
                                item.setText(
                                        2,
                                        str.substring(
                                                str.indexOf(" - ") + 3, str.indexOf(" *** ")));
                                if (str.indexOf("*** ERROR CODE = ") >= 0) {
                                    item.setText(
                                            3,
                                            str.substring(
                                                    str.indexOf("*** ERROR CODE = ") + 17,
                                                    str.indexOf(", Tran = ")));
                                    if (str.indexOf(", EID = ") == -1) {
                                        item.setText(
                                                4, str.substring(str.indexOf(", Tran = ") + 9));
                                        item.setText(5, "");
                                    } else {
                                        item.setText(
                                                4,
                                                str.substring(
                                                        str.indexOf(", Tran = ") + 9,
                                                        str.indexOf(
                                                                ", ",
                                                                str.indexOf(", Tran = ") + 9)));
                                        item.setText(5, str.substring(str.indexOf(", EID = ") + 8));
                                    }
                                    if (i + 1 < n
                                            && !(((String) logContentInfo.getLine().get(i + 1))
                                                    .toLowerCase()
                                                    .startsWith("time:"))) {
                                        item.setText(
                                                6, (String) logContentInfo.getLine().get(i + 1));
                                        i++;
                                    }
                                } else {
                                    if (i + 1 < n
                                            && !(((String) logContentInfo.getLine().get(i + 1))
                                                    .toLowerCase()
                                                    .startsWith("time:"))) {
                                        errMsg = (String) logContentInfo.getLine().get(i + 1);
                                        i++;
                                    } else {
                                        errMsg = "";
                                    }
                                    item.setText(
                                            6,
                                            str.substring(str.indexOf("*** file ") + 4)
                                                    + " : "
                                                    + errMsg);
                                }
                                errorNo++;
                            } else {
                                item = new TableItem(table, SWT.NONE);
                                item.setText(6, (String) logContentInfo.getLine().get(i));
                            }
                        }
                    }

                } else if (CubridNodeType.LOGS_BROKER_ADMIN_LOG.equals(cubridNode.getType())) {
                    if (isCreateColumn) {
                        String[] columnNames = {
                            Messages.tableNumber, Messages.tableTime, Messages.tableStatus
                        };
                        int[] widths = {50, 150, 150};
                        int[] aligns = {SWT.LEFT, SWT.LEFT, SWT.LEFT};
                        createTableColumn(columnNames, widths, aligns);
                    }

                    String str = "";
                    for (int i = 0, n = logContentInfo.getLine().size(); i < n; i++) {
                        str = (String) logContentInfo.getLine().get(i);
                        item = new TableItem(table, SWT.NONE);
                        item.setText(0, Integer.toString(j + i));
                        item.setText(1, str.substring(0, 20));
                        item.setText(2, str.substring(20));
                    }
                } else if (this.cubridNode.getType() == NodeType.DATABASE) {
                    if (isCreateColumn) {
                        String[] columnNames = {
                            Messages.tableNumber,
                            Messages.tableTime,
                            Messages.tableErrorType,
                            Messages.tableErrorMsg
                        };
                        int[] widths = {50, 150, 100, 320};
                        int[] aligns = {SWT.LEFT, SWT.CENTER, SWT.CENTER, SWT.CENTER};
                        createTableColumn(columnNames, widths, aligns);
                    }

                    String str = "";
                    for (int i = 0, n = logContentInfo.getLine().size(); i < n; i++) {
                        str = (String) logContentInfo.getLine().get(i);
                        item = new TableItem(table, SWT.NONE);
                        item.setText(0, Integer.toString(j + i));
                        item.setText(1, str.substring(str.indexOf("[") + 1, str.indexOf("]")));
                        item.setText(2, str.substring(str.indexOf(": ") + 2, str.lastIndexOf(":")));
                        item.setText(3, str.substring(str.lastIndexOf(":") + 2));
                    }
                } else {
                    if (isCreateColumn) {
                        String[] columnNames = {Messages.tableNumber, Messages.tableContent};
                        int[] widths = {50, 900};
                        int[] aligns = {SWT.LEFT, SWT.LEFT};
                        createTableColumn(columnNames, widths, aligns);
                    }

                    for (int i = 0, n = logContentInfo.getLine().size(); i < n; i++) {
                        item = new TableItem(table, SWT.NONE);
                        item.setText(0, Integer.toString(j + i));
                        item.setText(1, (String) logContentInfo.getLine().get(i));
                    }
                }
            }
        }
    }

    /**
     * Create the table column
     *
     * @param columnNames String[]
     * @param widths int[]
     * @param aligns int[]
     */
    private void createTableColumn(String[] columnNames, int[] widths, int[] aligns) {
        for (int i = 0; i < columnNames.length; i++) {
            TableColumn tblColumn = new TableColumn(table, aligns[i]);
            tblColumn.setText(columnNames[i]);
            tblColumn.setWidth(widths[i]);
        }
    }

    /** Dispose the table column */
    private void disposeTableColumn() {
        while (table.getColumnCount() > 0) {
            if (!table.getColumn(0).isDisposed()) {
                table.getColumn(0).dispose();
            }
        }
    }

    /**
     * initialize some values of manager logs.
     *
     * @param managerLogInfos ManagerLogInfos
     * @param isCreateColumn boolean
     */
    public void setManagerLogInfo(ManagerLogInfos managerLogInfos, boolean isCreateColumn) {

        table.removeAll();
        boolean isAccessLog = CubridNodeType.LOGS_MANAGER_ACCESS_LOG.equals(cubridNode.getType());
        boolean isErrorLog = CubridNodeType.LOGS_MANAGER_ERROR_LOG.equals(cubridNode.getType());
        accesslogList =
                managerLogInfos == null
                        ? null
                        : managerLogInfos.getAccessLog().getManagerLogInfoList();
        errorlogList =
                managerLogInfos == null
                        ? null
                        : managerLogInfos.getErrorLog().getManagerLogInfoList();
        boolean isDisposed =
                managerLogInfos == null
                        || (isAccessLog && (accesslogList == null || accesslogList.isEmpty()));
        isDisposed = isDisposed || (isErrorLog && (errorlogList == null || errorlogList.isEmpty()));
        if (isDisposed) {
            disposeTableColumn();
            TableColumn tblColumn = new TableColumn(table, SWT.LEFT);
            tblColumn.setText(Messages.msgNullLogFile);
            tblColumn.setWidth(500);
            return;
        }
        if (CubridNodeType.LOGS_MANAGER_ACCESS_LOG.equals(cubridNode.getType())) {
            pageInfo = new PageUtil(accesslogList.size(), PAGESIZE);
            pageInfo.setCurrentPage(1);
        } else if (CubridNodeType.LOGS_MANAGER_ERROR_LOG.equals(cubridNode.getType())) {
            pageInfo = new PageUtil(errorlogList.size(), PAGESIZE);
            pageInfo.setCurrentPage(1);
        }
        if (!isCreateColumn) {
            updateManagerLogTable();
            return;
        }
        TableColumn col = new TableColumn(table, SWT.LEFT);
        col.setText(Messages.tableNumber);
        col.setWidth(50);

        col = new TableColumn(table, SWT.LEFT);
        col.setText(Messages.tableUser);
        col.setWidth(100);
        col.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        TableColumn column = (TableColumn) event.widget;
                        comparator.setColumn(0);
                        comparator.reverseDirection();
                        table.setSortColumn(column);
                        table.setSortDirection(comparator.getDirection() == 0 ? SWT.UP : SWT.DOWN);
                        updateManagerLogTable();
                    }
                });

        col = new TableColumn(table, SWT.LEFT);
        col.setText(Messages.tableTaskName);
        col.setWidth(180);
        col.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        TableColumn column = (TableColumn) event.widget;
                        comparator.setColumn(1);
                        comparator.reverseDirection();
                        table.setSortColumn(column);
                        table.setSortDirection(comparator.getDirection() == 0 ? SWT.UP : SWT.DOWN);
                        updateManagerLogTable();
                    }
                });

        col = new TableColumn(table, SWT.LEFT);
        col.setText(Messages.tableTime);
        col.setWidth(150);
        col.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        TableColumn column = (TableColumn) event.widget;
                        comparator.setColumn(2);
                        comparator.reverseDirection();
                        table.setSortColumn(column);
                        table.setSortDirection(comparator.getDirection() == 0 ? SWT.UP : SWT.DOWN);
                        updateManagerLogTable();
                    }
                });
        if (CubridNodeType.LOGS_MANAGER_ERROR_LOG.equals(this.cubridNode.getType())) {
            col = new TableColumn(table, SWT.LEFT);
            col.setText(Messages.tableDescription);
            col.setWidth(400);
            col.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            TableColumn column = (TableColumn) event.widget;
                            comparator.setColumn(3);
                            comparator.reverseDirection();
                            table.setSortColumn(column);
                            table.setSortDirection(
                                    comparator.getDirection() == 0 ? SWT.UP : SWT.DOWN);
                            updateManagerLogTable();
                        }
                    });
        }
        updateManagerLogTable();
    }

    /** update manager log to table. */
    public void updateManagerLogTable() {
        table.removeAll();
        if (CubridNodeType.LOGS_MANAGER_ERROR_LOG.equals(this.cubridNode.getType())) {
            if (errorlogList.size() > 0) {
                Collections.sort(errorlogList, comparator);
                int begin = (pageInfo.getCurrentPage() - 1) * pageInfo.getPageSize();
                int last = begin + pageInfo.getPageSize();
                int index = (pageInfo.getCurrentPage() - 1) * pageInfo.getPageSize() + 1;

                if (begin + 1 <= 1) {
                    buttonFirst.setEnabled(false);
                    buttonPrev.setEnabled(false);
                } else {
                    buttonFirst.setEnabled(true);
                    buttonPrev.setEnabled(true);
                }
                if ((pageInfo.getTotalRs() < last ? pageInfo.getTotalRs() : last)
                        >= pageInfo.getTotalRs()) {
                    buttonEnd.setEnabled(false);
                    buttonNext.setEnabled(false);
                } else {
                    buttonEnd.setEnabled(true);
                    buttonNext.setEnabled(true);
                }

                pageText.setText(
                        (begin + 1)
                                + "-"
                                + (pageInfo.getTotalRs() < last ? pageInfo.getTotalRs() : last)
                                + " ("
                                + pageInfo.getTotalRs()
                                + ")");
                for (int i = begin; i < last && i < pageInfo.getTotalRs(); i++) {
                    ManagerLogInfo lfi = (ManagerLogInfo) errorlogList.get(i);
                    TableItem item = new TableItem(table, SWT.NONE);
                    item.setText(0, Integer.toString(index + i - begin));
                    item.setText(1, lfi.getUser());
                    item.setText(2, lfi.getTaskName());
                    item.setText(3, lfi.getTime());
                    item.setText(4, lfi.getErrorNote());
                }
            } else {
                buttonFirst.setEnabled(false);
                buttonPrev.setEnabled(false);
                buttonEnd.setEnabled(false);
                buttonNext.setEnabled(false);
                pageInfo = new PageUtil(CNTRECORD, PAGESIZE);
                pageInfo.setCurrentPage(1);
                TableItem item = new TableItem(table, SWT.NONE);
                item.setText(0, Messages.msgNullLogFile);
            }
        } else {
            if (accesslogList.size() > 0) {
                Collections.sort(accesslogList, comparator);

                int begin = (pageInfo.getCurrentPage() - 1) * pageInfo.getPageSize();
                int last = begin + pageInfo.getPageSize();
                int index = (pageInfo.getCurrentPage() - 1) * pageInfo.getPageSize() + 1;

                if (begin + 1 <= 1) {
                    buttonFirst.setEnabled(false);
                    buttonPrev.setEnabled(false);
                } else {
                    buttonFirst.setEnabled(true);
                    buttonPrev.setEnabled(true);
                }
                if ((pageInfo.getTotalRs() < last ? pageInfo.getTotalRs() : last)
                        >= pageInfo.getTotalRs()) {
                    buttonEnd.setEnabled(false);
                    buttonNext.setEnabled(false);
                } else {
                    buttonEnd.setEnabled(true);
                    buttonNext.setEnabled(true);
                }

                pageText.setText(
                        (begin + 1)
                                + "-"
                                + (pageInfo.getTotalRs() < last ? pageInfo.getTotalRs() : last)
                                + " ("
                                + pageInfo.getTotalRs()
                                + ")");
                for (int i = begin; i < last && i < pageInfo.getTotalRs(); i++) {
                    ManagerLogInfo lfi = (ManagerLogInfo) accesslogList.get(i);
                    TableItem item = new TableItem(table, SWT.NONE);
                    item.setText(0, Integer.toString(index + i - begin));
                    item.setText(1, lfi.getUser());
                    item.setText(2, lfi.getTaskName());
                    item.setText(3, lfi.getTime());
                }
            } else {
                buttonFirst.setEnabled(false);
                buttonPrev.setEnabled(false);
                buttonEnd.setEnabled(false);
                buttonNext.setEnabled(false);
                pageInfo = new PageUtil(CNTRECORD, PAGESIZE);
                pageInfo.setCurrentPage(1);
                TableItem item = new TableItem(table, SWT.NONE);
                item.setText(0, Messages.msgNullLogFile);
            }
        }
    }

    /**
     * calculate dates difference
     *
     * @param beginstr String
     * @param endstr String
     * @return result
     */
    public String calDate(String beginstr, String endstr) {
        String result = "";
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        SimpleDateFormat myTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());

        Date temp1, temp2;
        long l1 = 0, l2 = 0, l = 0;
        int h = 0, m = 0, s = 0;
        try {
            temp1 = myTime.parse(beginstr);
            temp2 = myTime.parse(endstr);
            c1.setTime(temp1);
            c2.setTime(temp2);
            l1 = c1.getTimeInMillis();
            l2 = c2.getTimeInMillis();
            l = l2 - l1;
            h = (int) (l / (60 * 60 * 1000));
            m = (int) ((l % (60 * 60 * 1000)) / (60 * 1000));
            s = (int) (((l % (60 * 60 * 1000)) % (60 * 1000)) / 1000);
            result = String.valueOf(h) + ":" + String.valueOf(m) + ":" + String.valueOf(s);
        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
            result = "";
        }
        return result;
    }

    /**
     * send when CUBRID node object
     *
     * @param event CubridNodeChangedEvent
     */
    public void nodeChanged(CubridNodeChangedEvent event) {
        //
    }

    /**
     * log of compare class
     *
     * @author wuyingshi 2009-3-10
     */
    static class LogComparator implements Comparator<ManagerLogInfo>, Serializable {

        private static final long serialVersionUID = -7155019374329278232L;
        private int column = 2; // time
        private int direction = 0; // asc

        /**
         * compare two object.
         *
         * @param obj1 ManagerLogInfo
         * @param obj2 ManagerLogInfo
         * @return rc result of compared
         */
        public int compare(ManagerLogInfo obj1, ManagerLogInfo obj2) {
            int rc = 0;
            switch (column) {
                case 0:
                    rc = obj1.getUser().compareTo(obj2.getUser());
                    break;
                case 1:
                    rc = obj1.getTaskName().compareTo(obj2.getTaskName());
                    break;
                case 2:
                    SimpleDateFormat dateFormat =
                            new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                    try {
                        Date date1 = dateFormat.parse(obj1.getTime());
                        Date date2 = dateFormat.parse(obj2.getTime());
                        rc = date1.compareTo(date2);
                    } catch (ParseException e) {
                        rc = 0;
                    }
                    break;
                case 3:
                    rc = obj1.getErrorNote().compareTo(obj2.getErrorNote());
                    break;
                default:
                    break;
            }

            if (direction == 1) {
                rc = -rc;
            }
            return rc;
        }

        /**
         * set the column.
         *
         * @param column int
         */
        public void setColumn(int column) {
            this.column = column;
        }

        /**
         * set the direction.
         *
         * @param direction int
         */
        public void setDirection(int direction) {
            this.direction = direction;
        }

        /**
         * get the direction.
         *
         * @return this.direction
         */
        public int getDirection() {
            return this.direction;
        }

        /** reverse the direction */
        public void reverseDirection() {
            direction = 1 - direction;
        }
    }

    public String getCharsetName() {
        return charsetName;
    }

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }
}
