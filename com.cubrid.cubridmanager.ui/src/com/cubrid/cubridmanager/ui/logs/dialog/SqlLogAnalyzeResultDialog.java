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

import static org.apache.commons.lang.StringUtils.capitalize;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultCubridNode;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfos;
import com.cubrid.cubridmanager.core.logs.model.AnalyzeCasLogResultInfo;
import com.cubrid.cubridmanager.core.logs.model.AnalyzeCasLogResultList;
import com.cubrid.cubridmanager.core.logs.model.AnalyzeCasLogTopResultInfo;
import com.cubrid.cubridmanager.core.logs.model.GetExecuteCasRunnerResultInfo;
import com.cubrid.cubridmanager.core.logs.model.LogInfo;
import com.cubrid.cubridmanager.core.logs.task.GetCasLogTopResultTask;
import com.cubrid.cubridmanager.core.logs.task.GetExecuteCasRunnerContentResultTask;
import com.cubrid.cubridmanager.ui.logs.Messages;
import com.cubrid.cubridmanager.ui.logs.action.LogViewAction;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * The dialog is used to Analyze sql log.
 *
 * @author wuyingshi
 * @version 1.0 - 2009-3-18 created by wuyingshi
 */
public class SqlLogAnalyzeResultDialog extends CMTitleAreaDialog {
    private static final Logger LOGGER = LogUtil.getLogger(LogViewAction.class);

    private Group groupAnalyzeResult = null;
    private Label label = null;
    private Table table = null;
    private Text textQuery = null;
    private Text textQueryResult = null;
    private Button buttonRunOriginalQuery = null;
    private Button buttonSaveToFile = null;
    private String resultFile = null;
    private boolean option = false;
    private int currentResultIndex;
    private Composite composite;
    private CubridDatabase database = null;
    private AnalyzeCasLogResultList analyzeCasLogResultList = null;
    private DefaultCubridNode node = null;
    private static final String NEXT_LINE = "\r\n";

    /**
     * The constructor
     *
     * @param parentShell
     */
    public SqlLogAnalyzeResultDialog(Shell parentShell) {
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
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        layout.numColumns = 2;
        composite.setLayout(layout);

        createLogFileGroup();
        createGroupQuery();
        createAnalyzeResultGroup();

        setTitle(Messages.titleSqlLogAnalyzeResultDialog);
        setMessage(Messages.msgSqlLogAnalyzeResultDialog);
        return parentComp;
    }

    /** @see com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog#constrainShellSize() */
    protected void constrainShellSize() {
        super.constrainShellSize();
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.titleSqlLogAnalyzeResultDialog);
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     * @param parent the button bar composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.buttonClose, true);
    }

    /** This method initializes analyze result group */
    private void createAnalyzeResultGroup() {
        GridData gridData5 = new GridData(GridData.FILL_BOTH);
        gridData5.heightHint = -1;
        gridData5.widthHint = -1;
        groupAnalyzeResult = new Group(composite, SWT.NONE);
        groupAnalyzeResult.setText(Messages.labelAnalysisResult);
        groupAnalyzeResult.setLayout(new GridLayout());
        groupAnalyzeResult.setLayoutData(gridData5);
        createAnalyzeResultTable();
    }

    /** This method initializes log file group */
    private void createLogFileGroup() {
        GridData gridData2 = new GridData(GridData.FILL_BOTH);
        gridData2.heightHint = 18;
        gridData2.horizontalSpan = 3;
        gridData2.widthHint = 467;

        GridLayout gridLayout1 = new GridLayout();
        gridLayout1.numColumns = 3;

        Group groupBroker = new Group(composite, SWT.NONE);
        groupBroker.setText(Messages.labelLogFile);
        groupBroker.setLayout(gridLayout1);
        groupBroker.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        label = new Label(groupBroker, SWT.NONE);
        label.setText(Messages.labelCasLogFile);
        label.setLayoutData(gridData2);
    }

    /** This method initializes table */
    private void createAnalyzeResultTable() {
        GridData gridData6 = new GridData(GridData.FILL_BOTH);
        gridData6.heightHint = 432;
        gridData6.widthHint = 450;
        table = new Table(groupAnalyzeResult, SWT.FULL_SELECTION | SWT.MULTI);
        table.setHeaderVisible(true);
        table.setLayoutData(gridData6);
        table.setLinesVisible(true);
        TableLayout tlayout = new TableLayout();

        if (option) {
            createAnalyzeResultTableOptionY(tlayout);
        } else {
            createAnalyzeResultTableOptionN(tlayout);
        }
    }

    /**
     * This method initializes table when option is no
     *
     * @param tlayout TableLayout
     */
    private void createAnalyzeResultTableOptionN(TableLayout tlayout) {
        tlayout.addColumnData(new ColumnWeightData(13, 60, true));
        tlayout.addColumnData(new ColumnWeightData(14, 60, true));
        tlayout.addColumnData(new ColumnWeightData(14, 60, true));
        tlayout.addColumnData(new ColumnWeightData(14, 60, true));
        tlayout.addColumnData(new ColumnWeightData(20, 60, true));
        tlayout.addColumnData(new ColumnWeightData(20, 60, true));

        table.setLayout(tlayout);
        table.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (analyzeCasLogResultList == null
                                || analyzeCasLogResultList.getLogFileInfoList() == null) {
                            return;
                        }

                        int selectioncount = table.getSelectionCount();
                        int resultCount = analyzeCasLogResultList.getLogFileInfoList().size();
                        StringBuilder queryString = new StringBuilder();
                        AnalyzeCasLogResultInfo logResult;
                        for (int j = 0; j < selectioncount; j++) {
                            String qindex = table.getSelection()[j].getText(0);
                            for (int i = 0; i < resultCount; i++) {
                                logResult =
                                        (AnalyzeCasLogResultInfo)
                                                (analyzeCasLogResultList
                                                        .getLogFileInfoList()
                                                        .get(i));
                                if (qindex.equals(logResult.getQindex())) {
                                    currentResultIndex = i;
                                    queryString = connect(logResult.getQindex(), queryString);
                                    queryString.append(logResult.getQueryString());
                                    queryString.append(NEXT_LINE);
                                    buttonRunOriginalQuery.setEnabled(true);
                                    buttonSaveToFile.setEnabled(true);
                                    break;
                                }
                            }
                        }
                        textQuery.setText(queryString.toString());
                    }
                });

        TableColumn qindex = makeQindexColumn();
        TableColumn max = makeMaxColumn();
        TableColumn min = makeMinColumn();
        TableColumn avg = makeAvgColumn();
        TableColumn cnt = makeCntColumn();
        TableColumn err = makeErrColumn();

        qindex.setText(Messages.tableIndex);
        max.setText(Messages.tableMax);
        max.setAlignment(SWT.RIGHT);
        min.setText(Messages.tableMin);
        min.setAlignment(SWT.RIGHT);
        avg.setText(Messages.tableAvg);
        avg.setAlignment(SWT.RIGHT);
        cnt.setText(Messages.tableTotalCount);
        cnt.setAlignment(SWT.RIGHT);
        err.setText(Messages.tableErrCount);
        err.setAlignment(SWT.RIGHT);

        qindex.setWidth(60);
        max.setWidth(60);
        min.setWidth(60);
        avg.setWidth(60);
        cnt.setWidth(50);
        err.setWidth(50);
    }

    /**
     * make err table column
     *
     * @return tableColumn
     */
    private TableColumn makeErrColumn() {
        TableColumn err = new TableColumn(table, SWT.LEFT);
        err.addSelectionListener(new DirectionAwareSelectionAdapter("err"));
        return err;
    }

    /**
     * make cnt table column
     *
     * @return tableColumn
     */
    private TableColumn makeCntColumn() {
        TableColumn cnt = new TableColumn(table, SWT.LEFT);
        cnt.addSelectionListener(new DirectionAwareSelectionAdapter("cnt"));
        return cnt;
    }

    /**
     * make avg table column
     *
     * @return tableColumn
     */
    private TableColumn makeAvgColumn() {
        TableColumn avg = new TableColumn(table, SWT.LEFT);
        avg.addSelectionListener(new DirectionAwareSelectionAdapter("avg"));
        return avg;
    }

    /**
     * make min table column
     *
     * @return tableColumn
     */
    private TableColumn makeMinColumn() {
        TableColumn min = new TableColumn(table, SWT.LEFT);
        min.addSelectionListener(new DirectionAwareSelectionAdapter("min"));
        return min;
    }

    /**
     * make max table column
     *
     * @return tableColumn
     */
    private TableColumn makeMaxColumn() {
        TableColumn max = new TableColumn(table, SWT.LEFT);
        max.addSelectionListener(new DirectionAwareSelectionAdapter("max"));
        return max;
    }

    /**
     * make qindex table column
     *
     * @return tableColumn
     */
    private TableColumn makeQindexColumn() {
        TableColumn qindex = new TableColumn(table, SWT.LEFT);
        qindex.addSelectionListener(new DirectionAwareSelectionAdapter("qindex"));
        return qindex;
    }

    /**
     * This method initializes table when option is yes
     *
     * @param tlayout TableLayout
     */
    private void createAnalyzeResultTableOptionY(TableLayout tlayout) {
        tlayout.addColumnData(new ColumnWeightData(20, 60, true));
        tlayout.addColumnData(new ColumnWeightData(20, 60, true));
        table.setLayout(tlayout);
        table.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (analyzeCasLogResultList == null
                                || analyzeCasLogResultList.getLogFileInfoList() == null) {
                            return;
                        }

                        int selectioncount = table.getSelectionCount();
                        int resultCount = analyzeCasLogResultList.getLogFileInfoList().size();
                        StringBuilder queryString = new StringBuilder();
                        AnalyzeCasLogResultInfo logResult;
                        for (int j = 0; j < selectioncount; j++) {
                            String qindex = table.getSelection()[j].getText(0);
                            for (int i = 0; i < resultCount; i++) {
                                logResult =
                                        (AnalyzeCasLogResultInfo)
                                                (analyzeCasLogResultList
                                                        .getLogFileInfoList()
                                                        .get(i));
                                if (qindex.equals(logResult.getQindex())) {
                                    currentResultIndex = i;
                                    queryString = connect(logResult.getQindex(), queryString);
                                    queryString.append(logResult.getQueryString());
                                    queryString.append(NEXT_LINE);
                                    buttonRunOriginalQuery.setEnabled(true);
                                    buttonSaveToFile.setEnabled(true);
                                    break;
                                }
                            }
                        }
                        textQuery.setText(queryString.toString());
                    }
                });

        TableColumn qindex = new TableColumn(table, SWT.LEFT);
        qindex.addSelectionListener(new DirectionAwareSelectionAdapter("qindex"));
        qindex.setText(Messages.tableIndex);
        qindex.setWidth(100);

        TableColumn execTime = new TableColumn(table, SWT.LEFT);
        execTime.addSelectionListener(new DirectionAwareSelectionAdapter("execTime"));
        execTime.setText(Messages.tableTransactionExeTime);
        execTime.setWidth(100);
    }

    /** This method initializes groupQuery */
    private void createGroupQuery() {
        GridData gridData22 = new GridData(GridData.FILL_HORIZONTAL);
        gridData22.heightHint = 26;
        gridData22.widthHint = 122;

        GridData gridData21 = new GridData(GridData.FILL_BOTH);
        gridData21.horizontalSpan = 3;
        gridData21.widthHint = 432;
        gridData21.heightHint = 150;

        GridData gridData11 = new GridData(GridData.FILL_BOTH);
        gridData11.horizontalSpan = 3;
        gridData11.widthHint = 432;
        gridData11.heightHint = 250;

        GridLayout gridLayout2 = new GridLayout();
        gridLayout2.numColumns = 3;
        gridLayout2.horizontalSpacing = 45;

        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.verticalSpan = 2;

        Group groupQuery = new Group(composite, SWT.NONE);
        groupQuery.setText("");
        groupQuery.setLayout(gridLayout2);
        groupQuery.setLayoutData(gridData);

        Label label1 = new Label(groupQuery, SWT.NONE);
        label1.setText(Messages.labelLogContents);

        textQuery = new Text(groupQuery, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL);
        textQuery.setEditable(true);
        textQuery.setLayoutData(gridData11);

        Label label2 = new Label(groupQuery, SWT.NONE);
        label2.setText(Messages.labelExecuteResult);

        textQueryResult =
                new Text(groupQuery, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL);
        textQueryResult.setLayoutData(gridData21);

        buttonRunOriginalQuery = new Button(groupQuery, SWT.NONE);
        buttonRunOriginalQuery.setEnabled(false);
        buttonRunOriginalQuery.setText(Messages.buttonExecuteOriginalQuery);

        Label dummy5 = new Label(groupQuery, SWT.NONE);
        dummy5.setLayoutData(gridData22);

        buttonRunOriginalQuery.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        String queryString = textQuery.getText();
                        LogInfo logInfo = null;

                        String targetBroker = null;
                        if (node.getId().indexOf("Sql log/") < 0) {
                            targetBroker = node.getParent().getLabel();
                        } else {
                            targetBroker = node.getParent().getParent().getLabel();
                        }
                        logInfo = (LogInfo) node.getAdapter(LogInfo.class);
                        List<String> allDatabaseList =
                                node.getServer().getServerInfo().getAllDatabaseList();
                        BrokerInfos brokerInfos = node.getServer().getServerInfo().getBrokerInfos();
                        CasRunnerConfigDialog casRunnerConfigDialog =
                                new CasRunnerConfigDialog(getShell());
                        casRunnerConfigDialog.setLogstring(queryString);
                        casRunnerConfigDialog.setBrokerInfos(brokerInfos);
                        casRunnerConfigDialog.setAllDatabaseList(allDatabaseList);
                        casRunnerConfigDialog.setLogInfo(logInfo);
                        casRunnerConfigDialog.setTargetBroker(targetBroker);
                        casRunnerConfigDialog.create();
                        if (casRunnerConfigDialog.open() == Dialog.OK) {
                            final GetExecuteCasRunnerContentResultTask task =
                                    new GetExecuteCasRunnerContentResultTask(
                                            node.getServer().getServerInfo());
                            task.setBrokerName(CasRunnerConfigDialog.getBrokerName());
                            task.setUserName(CasRunnerConfigDialog.getUserName());
                            task.setPasswd(CasRunnerConfigDialog.getPassword());
                            task.setNumThread(CasRunnerConfigDialog.getNumThread());
                            task.setRepeatCount(casRunnerConfigDialog.getNumRepeatCount());
                            String isShowqueryresult = "";
                            String isShowqueryplan = "";
                            if (casRunnerConfigDialog.isShowqueryresult()) {
                                isShowqueryresult = "yes";
                                isShowqueryplan =
                                        casRunnerConfigDialog.isShowqueryresult() ? "yes" : "no";
                            } else {
                                isShowqueryresult = "no";
                                isShowqueryplan = "no";
                            }
                            task.setShowQueryResult(isShowqueryresult);
                            task.setShowQueryResult(isShowqueryplan);
                            task.setDbName(CasRunnerConfigDialog.getDbname());
                            task.setExecuteLogFile("no");
                            String[] queryStringArr = queryString.split("\\r\\n");
                            task.setLogstring(queryStringArr);
                            TaskExecutor taskExecutor =
                                    new CommonTaskExec(Messages.loadSqlLogExecResultTaskName);
                            taskExecutor.addTask(task);
                            new ExecTaskWithProgress(taskExecutor).exec();
                            if (taskExecutor.isSuccess()) {
                                GetExecuteCasRunnerResultInfo getExecuteCasRunnerResultInfo =
                                        (GetExecuteCasRunnerResultInfo) task.getContent();
                                StringBuffer result = new StringBuffer("");
                                for (int i = 0,
                                                n =
                                                        getExecuteCasRunnerResultInfo
                                                                .getResult()
                                                                .size();
                                        i < n;
                                        i++) {
                                    result.append(
                                            getExecuteCasRunnerResultInfo.getResult().get(i)
                                                    + "\n");
                                }
                                String logPath = null;
                                String totalResultNum = null;
                                logPath = getExecuteCasRunnerResultInfo.getQueryResultFile();
                                totalResultNum =
                                        getExecuteCasRunnerResultInfo.getQueryResultFileNum();
                                if (queryString.length() > 0) {
                                    textQueryResult.setText(result.toString());
                                    if (casRunnerConfigDialog.isShowqueryresult()) {
                                        CasRunnerResultViewDialog casRunnerResultViewDialog =
                                                new CasRunnerResultViewDialog(getShell());
                                        casRunnerResultViewDialog.create();
                                        casRunnerResultViewDialog.connectInit(
                                                logPath, CasRunnerConfigDialog.getDbname(), node);
                                        casRunnerResultViewDialog.setTotalResultNum(
                                                Integer.parseInt(totalResultNum));
                                        casRunnerResultViewDialog.getShell().setSize(665, 555);
                                        casRunnerResultViewDialog.open();
                                    }
                                }
                            }
                        }
                    }
                });

        buttonSaveToFile = new Button(groupQuery, SWT.NONE);
        buttonSaveToFile.setEnabled(false);
        buttonSaveToFile.setText(Messages.buttonSaveLogString);
        buttonSaveToFile.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        String savedFilename = saveAsFile();
                        if (isBlank(savedFilename)) {
                            return;
                        }
                        if (analyzeCasLogResultList == null
                                || analyzeCasLogResultList.getLogFileInfoList() == null) {
                            return;
                        }
                        AnalyzeCasLogResultInfo analyzeCasLogResultInfo =
                                (AnalyzeCasLogResultInfo)
                                        (analyzeCasLogResultList
                                                .getLogFileInfoList()
                                                .get(currentResultIndex));
                        if (savedFilename.length() > 0) {
                            analyzeCasLogResultInfo.setSavedFileName(savedFilename);
                        }
                    }
                });
    }

    /**
     * save as file
     *
     * @return fileName
     */
    private String saveAsFile() {
        String fileName = "";
        Shell sShell = null;
        FileDialog dialog =
                new FileDialog(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        SWT.SAVE | SWT.APPLICATION_MODAL);
        dialog.setFilterExtensions(new String[] {"*.clg", "*.txt", "*.*"});
        dialog.setFilterNames(new String[] {"CAS Log File(*.clg)", "Text File(*.txt)", "All"});
        String result = dialog.open();
        if (result == null) {
            return "";
        }

        File file = new File(result);
        if (file.exists() && !CommonUITool.openConfirmBox(result + Messages.msgOverwriteFile)) {
            return "";
        }
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file));
            String text = textQuery.getText();
            bw.write(text);
            fileName = result;
        } catch (FileNotFoundException e1) {
            CommonUITool.openErrorBox(sShell, e1.getMessage());
            return "";
        } catch (IOException e1) {
            CommonUITool.openErrorBox(sShell, e1.getMessage());
            return "";
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }

        return fileName;
    }

    /**
     * open a dialog to set the save file
     *
     * @return dialog
     */
    public static FileDialog openFileSaveDialog() {
        FileDialog dialog =
                new FileDialog(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        SWT.SAVE | SWT.APPLICATION_MODAL);
        dialog.setFilterExtensions(new String[] {"*.sql", "*.txt", "*.*"});
        dialog.setFilterNames(new String[] {"SQL File", "Text File", "All"});
        File curdir = new File(".");
        try {
            dialog.setFilterPath(curdir.getCanonicalPath());
        } catch (IOException e) {
            dialog.setFilterPath(".");
        }
        return dialog;
    }

    /**
     * insert AnalyzeCasLogResult information to table.
     *
     * @param analyzeCasLogResultList AnalyzeCasLogResultList
     */
    public void insertArrayToTable(AnalyzeCasLogResultList analyzeCasLogResultList) {
        if (analyzeCasLogResultList == null
                || analyzeCasLogResultList.getLogFileInfoList() == null) {
            return;
        }

        for (AnalyzeCasLogResultInfo logResult : analyzeCasLogResultList.getLogFileInfoList()) {
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(0, logResult.getQindex());
            if (option) {
                item.setText(1, logResult.getExecTime());
            } else {
                item.setText(1, logResult.getMax());
                item.setText(2, logResult.getMin());
                item.setText(3, logResult.getAvg());
                item.setText(4, logResult.getCnt());
                item.setText(5, logResult.getErr());
            }
        }
    }

    /**
     * insert sorted AnalyzeCasLogResult information to table.
     *
     * @param obj Object[]
     */
    public void insertArrayToTableSort(Object[] obj) {
        int itemcount = obj.length;
        for (int i = 0; i < itemcount; i++) {
            AnalyzeCasLogResultInfo logResult = (AnalyzeCasLogResultInfo) (obj[i]);
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(0, logResult.getQindex());
            if (option) {
                item.setText(1, logResult.getExecTime());
            } else {
                item.setText(1, logResult.getMax());
                item.setText(2, logResult.getMin());
                item.setText(3, logResult.getAvg());
                item.setText(4, logResult.getCnt());
                item.setText(5, logResult.getErr());
            }
        }
    }

    /**
     * Get time of time_format:"%H:%M:%S"}
     *
     * @param timestring String
     * @return retVal
     */
    private String timeToSeconds(String timestring) {
        String sec = null, min = null, hour = null;
        float resultSec = (float) 0.0;

        String[] arrayTime = timestring.split(":");
        if (arrayTime.length == 1) {
            sec = arrayTime[0];
        } else if (arrayTime.length == 2) {
            min = arrayTime[0];
            sec = arrayTime[1];
        } else if (arrayTime.length == 3) {
            hour = arrayTime[0];
            min = arrayTime[1];
            sec = arrayTime[2];
        } else {
            assert false : "The timeToSeconds has an invalid parameter=" + timestring;
            return "";
        }

        if (isNotBlank(hour)) {
            resultSec += Float.parseFloat(hour) * 3600;
        }
        if (isNotBlank(min)) {
            resultSec += Float.parseFloat(min) * 60;
        }
        if (isNotBlank(sec)) {
            resultSec += Float.parseFloat(sec);
        }

        return String.valueOf(resultSec);
    }

    /**
     * each page of log connect
     *
     * @param qindex String
     * @param queryString StringBuffer
     * @return queryString
     */
    public StringBuilder connect(String qindex, StringBuilder queryString) {
        final GetCasLogTopResultTask task =
                new GetCasLogTopResultTask(node.getServer().getServerInfo());
        task.setFileName(resultFile);
        task.setQindex(qindex);
        TaskExecutor taskExecutor = new CommonTaskExec(Messages.loadLogTaskName);
        taskExecutor.addTask(task);
        new ExecTaskWithProgress(taskExecutor).exec();
        if (taskExecutor.isSuccess() && task.getAnalyzeCasLogTopResultList() != null) {
            AnalyzeCasLogTopResultInfo resultInfo =
                    (AnalyzeCasLogTopResultInfo) task.getAnalyzeCasLogTopResultList();
            if (resultInfo != null && resultInfo.getLogString() != null) {
                for (String logString : resultInfo.getLogString()) {
                    queryString.append(logString + "\n");
                }
            }
        }
        return queryString;
    }

    /**
     * Set label.
     *
     * @param selectedStringList List<String>
     */
    public void setLabel(List<String> selectedStringList) {
        if (selectedStringList.size() > 1) {
            String str = (String) selectedStringList.get(0);
            int i = str.lastIndexOf("_");
            str = str.substring(0, i);
            label.setText(str);
        } else {
            label.setText((String) selectedStringList.get(0));
        }
    }

    /**
     * get database.
     *
     * @return database
     */
    public CubridDatabase getDatabase() {
        return database;
    }

    /**
     * set database.
     *
     * @param database CubridDatabase
     */
    public void setDatabase(CubridDatabase database) {
        this.database = database;
    }

    /**
     * get the analyzeCasLogResultList.
     *
     * @return analyzeCasLogResultList
     */
    public AnalyzeCasLogResultList getAnalyzeCasLogResultList() {
        return analyzeCasLogResultList;
    }

    /**
     * set the analyzeCasLogResultList.
     *
     * @param analyzeCasLogResultList AnalyzeCasLogResultList
     */
    public void setAnalyzeCasLogResultList(AnalyzeCasLogResultList analyzeCasLogResultList) {
        this.analyzeCasLogResultList = analyzeCasLogResultList;
    }

    /**
     * get the resultFile.
     *
     * @return resultFile
     */
    public String getResultFile() {
        return resultFile;
    }

    /**
     * set the resultFile.
     *
     * @param resultFile String
     */
    public void setResultFile(String resultFile) {
        this.resultFile = resultFile;
    }

    /**
     * get the node.
     *
     * @return node
     */
    public DefaultCubridNode getNode() {
        return node;
    }

    /**
     * set the node.
     *
     * @param node DefaultCubridNode
     */
    public void setNode(DefaultCubridNode node) {
        this.node = node;
    }

    /**
     * Return whether is option
     *
     * @return option
     */
    public boolean isOption() {
        return option;
    }

    /**
     * Set whether is option
     *
     * @param option boolean
     */
    public void setOption(boolean option) {
        this.option = option;
    }

    /**
     * Ordering indicator provided selection adapter.
     *
     * @author CHOE JUNGYEON
     */
    private class DirectionAwareSelectionAdapter extends SelectionAdapter {
        private String getterName;
        private String dataKey;
        boolean desc = false;

        public DirectionAwareSelectionAdapter(String dataKey) {
            super();
            this.dataKey = dataKey;
            this.getterName = "get" + capitalize(dataKey);
        }

        public void widgetSelected(SelectionEvent event) {
            if (analyzeCasLogResultList == null
                    || analyzeCasLogResultList.getLogFileInfoList() == null
                    || isBlank(dataKey)) {
                return;
            }

            TableColumn column = (TableColumn) event.widget;
            String selectedRowIndex = "";
            if (table.getSelectionCount() > 0) {
                TableItem item = table.getSelection()[0];
                selectedRowIndex = item.getText(0);
            }

            Object[] obj = analyzeCasLogResultList.getLogFileInfoList().toArray();
            Arrays.sort(
                    obj,
                    new Comparator<Object>() {
                        public int compare(Object o1, Object o2) {
                            int retVal = 0;
                            String t1 = null, t2 = null;

                            try {
                                t1 = (String) o1.getClass().getMethod(getterName).invoke(o1);
                                t2 = (String) o2.getClass().getMethod(getterName).invoke(o2);
                                if ("qindex".equals(dataKey)) {
                                    t1 = t1.substring(2, t1.indexOf(']'));
                                    t2 = t2.substring(2, t2.indexOf(']'));
                                } else if ("min".equals(dataKey)
                                        || "max".equals(dataKey)
                                        || "avg".equals(dataKey)) {
                                    t1 = timeToSeconds(t1);
                                    t2 = timeToSeconds(t2);
                                }
                            } catch (Exception e) {
                                LOGGER.error(e.getMessage(), e);
                                return retVal;
                            }

                            try {
                                float ft1 = Float.parseFloat(t1);
                                float ft2 = Float.parseFloat(t2);

                                if (desc) {
                                    if (ft1 > ft2) {
                                        return 1;
                                    } else if (ft1 == ft2) {
                                        return 0;
                                    } else {
                                        return -1;
                                    }
                                } else {
                                    if (ft1 > ft2) {
                                        return -1;
                                    } else if (ft1 == ft2) {
                                        return 0;
                                    } else {
                                        return 1;
                                    }
                                }
                            } catch (NumberFormatException e) {
                                retVal = desc ? t1.compareTo(t2) : t2.compareTo(t1);
                            }

                            return retVal;
                        }
                    });
            table.removeAll();
            insertArrayToTableSort(obj);

            if (isNotBlank(selectedRowIndex)) {
                for (int i = 0; i < table.getItemCount(); i++) {
                    if (table.getItem(i).getText(1).equals(selectedRowIndex)) {
                        table.setSelection(i);
                        break;
                    }
                }
            }

            desc = !desc;
            table.setSortColumn(column);
            table.setSortDirection(desc ? SWT.DOWN : SWT.UP);
        }
    }
}
