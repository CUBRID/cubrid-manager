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
package com.cubrid.common.ui.query.control;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.DateUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.dialog.ExportQueryResultDialog;
import com.cubrid.common.ui.query.editor.InfoWindowManager;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.query.editor.QueryResultTableCalcInfo;
import com.cubrid.common.ui.query.editor.QueryTableTabItem;
import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.contribution.StatusLineContrItem;
import com.cubrid.common.ui.spi.table.CellValue;
import com.cubrid.common.ui.spi.table.ISelectionChangeListener;
import com.cubrid.common.ui.spi.table.SelectionChangeEvent;
import com.cubrid.common.ui.spi.table.TableSelectSupport;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.TabContextMenuManager;
import com.cubrid.common.ui.spi.util.paramSetter.ParamSetException;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * A composite to show the query area and result in same view.
 *
 * @author wangsl 2009-3-11
 */
public class QueryResultComposite extends Composite implements ISubTabSelection {
    private static final int SASH_WIDTH = 2;
    private static final long STOP_CONNECT_TIMEOUT_MSEC = 30000;

    private final QueryEditorPart editor;
    private final CTabFolder resultTabFolder;
    private final CTabFolder queryResultTabFolder;

    private CTabItem logResultTabItem = null;
    private StyledText logMessagesArea;
    private QueryTableTabItem queryResultTabItem;
    private boolean isMutliQuery = false;

    public boolean multiResultsCompare = false;
    public QueryExecuter baseQueryExecuter = null;
    public Color[][] colorSets = null;
    public boolean showHighlight = true;
    private boolean isCancelling = false;
    private long startMillis = 0;
    private Button stopButton;

    private ToolItem delRecordItem;
    private ToolItem insertRecordItem;
    private ToolItem insertSaveItem;
    private ToolItem swRecordItem;
    private ToolItem rollbackModifiedItem;
    private ToolItem copyInsertSqlFromRecordsItem;
    private ToolItem copyUpdateSqlFromRecordsItem;

    private Color BACKGROUND_DIRTY = ResourceManager.getColor(SWT.COLOR_GRAY);
    private Color BACKGROUND_NORMAL = ResourceManager.getColor(SWT.COLOR_WHITE);

    /**
     * The constructor
     *
     * @param parent
     * @param style
     * @param queryEditorPart
     */
    public QueryResultComposite(CTabFolder parent, int style, QueryEditorPart queryEditorPart) {
        this(parent, style, queryEditorPart, true);
    }

    public QueryResultComposite(
            CTabFolder parent, boolean isMutliQuery, QueryEditorPart queryEditorPart) {
        this(parent, SWT.None, queryEditorPart, true);
        this.isMutliQuery = isMutliQuery;
    }

    public QueryResultComposite(
            CTabFolder parent, int style, QueryEditorPart queryEditorPart, boolean defaultTabInit) {
        super(parent, style);
        resultTabFolder = parent;
        this.editor = queryEditorPart;
        setLayout(new FillLayout());

        if (defaultTabInit) {
            queryResultTabItem = new QueryTableTabItem(parent, SWT.NONE);
            queryResultTabItem.setText(Messages.qedit_result_folder);
            queryResultTabItem.setControl(this);
        }

        queryResultTabFolder = new CTabFolder(this, SWT.BOTTOM | SWT.CLOSE);
        queryResultTabFolder.setSimple(false);
        queryResultTabFolder.setUnselectedImageVisible(true);
        queryResultTabFolder.setUnselectedCloseVisible(true);
        queryResultTabFolder.setSelectionBackground(CombinedQueryEditorComposite.BACK_COLOR);
        queryResultTabFolder.setSelectionForeground(ResourceManager.getColor(SWT.COLOR_BLACK));
        queryResultTabFolder.setLayout(new GridLayout(1, true));
        queryResultTabFolder.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        InfoWindowManager.getInstance().updateContent(editor);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        widgetSelected(e);
                    }
                });

        TabContextMenuManager tabContextMenuManager =
                new TabContextMenuManager(queryResultTabFolder);
        tabContextMenuManager.createContextMenu();

        makeEmptyResult();
    }

    /** Make empty result contents. */
    public void makeEmptyResult() {
        resultTabFolder.setSelection(queryResultTabItem);

        SashForm bottomSash = new SashForm(queryResultTabFolder, SWT.VERTICAL);
        bottomSash.SASH_WIDTH = SASH_WIDTH;
        bottomSash.setBackground(CombinedQueryEditorComposite.BACK_COLOR);

        Table table =
                new Table(bottomSash, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
        setDropTraget(table);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        TableColumn tableColumn = new TableColumn(table, SWT.NONE);
        tableColumn.setWidth(60);

        SashForm tailSash = new SashForm(bottomSash, SWT.HORIZONTAL);
        tailSash.SASH_WIDTH = SASH_WIDTH;
        tailSash.setBackground(CombinedQueryEditorComposite.BACK_COLOR);

        new StyledText(tailSash, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY);

        bottomSash.setWeights(new int[] {70, 30});

        CTabItem tabItem = new CTabItem(queryResultTabFolder, SWT.NONE);
        tabItem.setText(Messages.qedit_result);
        tabItem.setControl(bottomSash);

        queryResultTabFolder.setSelection(tabItem);
    }

    /**
     * Make the log result contents.
     *
     * @param sqlStr String
     * @param messageStr String
     */
    public void makeLogResult(String sqlStr, String messageStr) {
        resultTabFolder.setSelection(queryResultTabItem);

        SashForm tailSash = new SashForm(queryResultTabFolder, SWT.NONE);
        tailSash.SASH_WIDTH = SASH_WIDTH;
        tailSash.setBackground(CombinedQueryEditorComposite.BACK_COLOR);
        String text = messageStr;

        logMessagesArea =
                new StyledText(
                        tailSash,
                        SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY | SWT.WRAP);
        CommonUITool.registerCopyPasteContextMenu(logMessagesArea, false);
        logMessagesArea.setText(text);

        int splitLineLength = QueryUtil.SPLIT_LINE_FOR_QUERY_RESULT.length();
        for (int sp = 0; ; ) {
            int redStringLength = splitLineLength;
            sp = text.indexOf(QueryUtil.SPLIT_LINE_FOR_QUERY_RESULT, sp);
            if (sp == -1) {
                break;
            }

            if (sp != 0) {
                int lastSp = text.lastIndexOf("\n", sp - 2);
                if (lastSp == -1) {
                    redStringLength += sp;
                    sp = 0;
                } else {
                    redStringLength += (sp - lastSp);
                    sp = lastSp + 1;
                }
            }

            boolean isErrorMsg = false;
            try {
                if (text.substring(sp, sp + redStringLength).indexOf(Messages.queryFail) != -1) {
                    isErrorMsg = true;
                }
            } catch (Exception e) {
            }

            StyleRange eachStyle = new StyleRange();
            eachStyle.start = sp;
            eachStyle.length = redStringLength;
            eachStyle.fontStyle = SWT.NORMAL;
            eachStyle.foreground =
                    Display.getDefault()
                            .getSystemColor(isErrorMsg ? SWT.COLOR_RED : SWT.COLOR_BLUE);
            logMessagesArea.setStyleRange(eachStyle);

            sp += redStringLength;
        }

        logResultTabItem = new CTabItem(queryResultTabFolder, SWT.NONE);
        logResultTabItem.setText(Messages.qedit_logsresult);
        logResultTabItem.setControl(tailSash);
        editor.getCombinedQueryComposite()
                .getRecentlyUsedSQLComposite()
                .refreshRecentlyUsedSQLList();
    }

    public void makeMultiQueryResult(QueryExecuter result) {
        makeResult(result, resultTabFolder);
    }

    public void makeSingleQueryResult(QueryExecuter result) {
        makeResult(result, queryResultTabFolder);
        queryResultTabFolder.setSelection(queryResultTabItem);
        queryResultTabItem.setData(result);
    }
    /**
     * make the result contents.
     *
     * @param result QueryExecuter
     */
    private void makeResult(final QueryExecuter result, CTabFolder parentFolder) {
        editor.getCombinedQueryComposite()
                .getRecentlyUsedSQLComposite()
                .refreshRecentlyUsedSQLList();

        // final CTabFolder queryResultTabFolder =
        // ((QueryResultComposite)queryResultTabItem.getControl()).queryResultTabFolder;
        ViewForm viewForm = new ViewForm(parentFolder, SWT.NONE);

        // create the bottom sash
        final SashForm tableLogSash = new SashForm(viewForm, SWT.VERTICAL);
        tableLogSash.SASH_WIDTH = SASH_WIDTH;
        tableLogSash.setBackground(CombinedQueryEditorComposite.BACK_COLOR);

        final Composite resultContainer = new Composite(tableLogSash, SWT.None);
        resultContainer.setLayout(new FormLayout());

        final Table resultTable =
                new Table(
                        resultContainer,
                        SWT.H_SCROLL
                                | SWT.V_SCROLL
                                | SWT.FULL_SELECTION
                                | SWT.HIDE_SELECTION /* | SWT.MULTI*/);
        setDropTraget(resultTable);
        resultTable.setHeaderVisible(true);
        resultTable.setLinesVisible(true);
        resultTable.setBackground(BACKGROUND_NORMAL);
        CommonUITool.hackForYosemite(resultTable);
        // display data compare label for multiple queries
        if (this.multiResultsCompare == true) {
            Composite compareButtonComposite = new Composite(resultContainer, SWT.None);
            compareButtonComposite.setLayout(new FillLayout());
            displayDataCompareLabel(compareButtonComposite, resultTable);

            FormData tableData = new FormData();
            tableData.top = new FormAttachment(0, 0);
            tableData.bottom = new FormAttachment(100, -28);
            tableData.left = new FormAttachment(0, 0);
            tableData.right = new FormAttachment(100, 0);
            resultTable.setLayoutData(tableData);

            FormData compareData = new FormData();
            compareData.top = new FormAttachment(100, -28);
            compareData.bottom = new FormAttachment(100, 0);
            compareData.left = new FormAttachment(0, 0);
            compareData.right = new FormAttachment(100, 0);
            compareButtonComposite.setLayoutData(compareData);
        } else {
            FormData tableData = new FormData();
            tableData.top = new FormAttachment(0, 0);
            tableData.bottom = new FormAttachment(100, 0);
            tableData.left = new FormAttachment(0, 0);
            tableData.right = new FormAttachment(100, 0);
            resultTable.setLayoutData(tableData);
        }

        final SashForm logSash = new SashForm(tableLogSash, SWT.HORIZONTAL);
        logSash.SASH_WIDTH = SASH_WIDTH;
        logSash.setBackground(CombinedQueryEditorComposite.BACK_COLOR);
        logSash.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));

        StyledText messagesArea =
                new StyledText(
                        logSash,
                        SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY | SWT.WRAP);
        CommonUITool.registerCopyPasteContextMenu(messagesArea, false, false);

        tableLogSash.setWeights(new int[] {8, 2});

        messagesArea.setToolTipText(Messages.tooltipHowToExpandLogPane);
        messagesArea.addFocusListener(
                new FocusListener() {
                    public void focusLost(FocusEvent e) {
                        tableLogSash.setWeights(new int[] {8, 2});
                    }

                    public void focusGained(FocusEvent e) {
                        tableLogSash.setWeights(new int[] {2, 8});
                    }
                });
        TableCursor cursor = new TableCursor(resultTable, SWT.NONE);
        TableSelectSupport tableSelectSupport = new TableSelectSupport(resultTable, cursor);

        if (this.multiResultsCompare == true) {
            result.setMultiResultsCompare(true);
            result.setBaseQueryExecuter(this.baseQueryExecuter);
        }
        result.makeResult(tableSelectSupport, messagesArea, isMutliQuery);

        // Auto set column size, maximum is 300px,minimum is 50px
        for (int i = 1; i < resultTable.getColumnCount(); i++) {
            resultTable.getColumns()[i].pack();
            if (resultTable.getColumns()[i].getWidth() > 300) {
                resultTable.getColumns()[i].setWidth(300);
            }
            if (resultTable.getColumns()[i].getWidth() < 50) {
                resultTable.getColumns()[i].setWidth(50);
            }
        }

        // fill the view form action on top right corner
        ToolBar topRightToolBar = new ToolBar(viewForm, SWT.FLAT);
        ToolBarManager toolBarManager = new ToolBarManager(topRightToolBar);
        result.makeActions(toolBarManager, resultTable);

        // fill the view form action on top right corner
        ToolBar topLeftToolBar = new ToolBar(viewForm, SWT.FLAT);

        swRecordItem = new ToolItem(topLeftToolBar, SWT.CHECK);
        result.swRecordItem = swRecordItem;
        swRecordItem.setToolTipText(Messages.getOidOn);
        swRecordItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_update.png"));
        swRecordItem.setEnabled(true);
        swRecordItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (editor.isCollectExecStats()) {
                            result.swRecordItem.setSelection(false);
                            CommonUITool.openErrorBox(getShell(), Messages.errNotEditableOnStat);
                            return;
                        }

                        if (!result.isContainPrimayKey()) {
                            result.swRecordItem.setSelection(false);
                            CommonUITool.openErrorBox(getShell(), Messages.errNoPrimaryKey);
                            return;
                        }

                        if (!result.isSingleTableQuery()) {
                            result.swRecordItem.setSelection(false);
                            CommonUITool.openErrorBox(getShell(), Messages.errNotInOneTable);
                            return;
                        }

                        if (!checkConnection()) {
                            result.swRecordItem.setSelection(false);
                            CommonUITool.openErrorBox(getShell(), Messages.errMsgExecuteInResult);
                            return;
                        }

                        result.tblResult.forceFocus();
                        if (result.swRecordItem.getSelection()) {
                            result.swRecordItem.setToolTipText(Messages.getOidOn);
                            result.setEditMode(true);
                            result.insertRecordItem.setEnabled(true);
                            if (result.tblResult.getSelectionCount() > 0) {
                                result.delRecordItem.setEnabled(true);
                            }
                        } else {
                            if (result.isModifiedResult()) {
                                result.swRecordItem.setSelection(true);
                                CommonUITool.openErrorBox(getShell(), Messages.errHasNoCommit);
                                return;
                            }

                            result.swRecordItem.setToolTipText(Messages.getOidOff);
                            result.setEditMode(false);
                            result.insertRecordItem.setEnabled(false);
                            result.delRecordItem.setEnabled(false);
                        }
                    }
                });

        // added by kevin, for insert record
        insertRecordItem = new ToolItem(topLeftToolBar, SWT.PUSH);
        result.insertRecordItem = insertRecordItem;
        insertRecordItem.setToolTipText(
                com.cubrid.common.ui.cubrid.table.Messages.insertInstanceMsgTitle);
        insertRecordItem.setImage(
                CommonUIPlugin.getImage("icons/queryeditor/table_record_insert.png"));
        insertRecordItem.setDisabledImage(
                CommonUIPlugin.getImage("icons/queryeditor/table_record_insert_disabled.png"));
        insertRecordItem.setEnabled(false);
        insertRecordItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (!result.getEditable()) {
                            CommonUITool.openErrorBox(getShell(), Messages.errNotEditable);
                            return;
                        }

                        if (!checkConnection()) {
                            CommonUITool.openErrorBox(getShell(), Messages.errMsgExecuteInResult);
                            return;
                        }
                        result.insertSaveItem.setEnabled(result.getEditable());
                        result.rollbackModifiedItem.setEnabled(result.getEditable());
                        result.addNewItem();
                    }
                });

        delRecordItem = new ToolItem(topLeftToolBar, SWT.PUSH);
        result.delRecordItem = delRecordItem;
        delRecordItem.setToolTipText(Messages.delete);
        delRecordItem.setImage(
                CommonUIPlugin.getImage("icons/queryeditor/table_record_delete.png"));
        delRecordItem.setDisabledImage(
                CommonUIPlugin.getImage("icons/queryeditor/table_record_delete_disabled.png"));
        delRecordItem.setEnabled(false);
        delRecordItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (!result.getEditable()) {
                            CommonUITool.openErrorBox(getShell(), Messages.errNotEditable);
                            return;
                        }

                        if (!checkConnection()) {
                            CommonUITool.openErrorBox(getShell(), Messages.errMsgExecuteInResult);
                            return;
                        }
                        result.tblResult.forceFocus();
                        result.deleteRecord(result.tblResult, null);
                    }
                });

        insertSaveItem = new ToolItem(topLeftToolBar, SWT.PUSH);
        result.insertSaveItem = insertSaveItem;
        insertSaveItem.setToolTipText(Messages.insertCommit);
        insertSaveItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_commit.png"));
        insertSaveItem.setDisabledImage(
                CommonUIPlugin.getImage("icons/queryeditor/query_commit_disabled.png"));
        insertSaveItem.setEnabled(false);
        insertSaveItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (!checkConnection()) {
                            CommonUITool.openErrorBox(getShell(), Messages.errMsgExecuteInResult);
                            return;
                        }

                        if (result.getQueryEditor().isActive()) {
                            CommonUITool.openWarningBox(Messages.msgActiveTran);
                            return;
                        }

                        if (!CommonUITool.openConfirmBox(getShell(), (Messages.msgCommitEdited))) {
                            return;
                        }

                        try {
                            if (result.saveInsertedUpdatedDeletedRecords()) {
                                insertSaveItem.setEnabled(false);
                                result.rollbackModifiedItem.setEnabled(false);
                                result.swRecordItem.setSelection(false);
                            }

                            result.swRecordItem.setSelection(false);
                            result.insertRecordItem.setEnabled(false);
                            result.delRecordItem.setEnabled(false);
                            result.setEditMode(false);
                        } catch (ParamSetException e) {
                            if (e.getParameter() != null
                                    && !StringUtil.isEmpty(e.getParameter().getDataType())) {
                                CommonUITool.openErrorBox(
                                        Messages.bind(
                                                        Messages.errTextTypeNotMatch,
                                                        e.getParameter().getDataType())
                                                + StringUtil.NEWLINE
                                                + e.getLocalizedMessage());
                            } else {
                                CommonUITool.openErrorBox(getShell(), e.getLocalizedMessage());
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                            CommonUITool.openErrorBox(
                                    getShell(),
                                    e.getErrorCode() + StringUtil.NEWLINE + e.getMessage());
                            /*Can't edit any data if necessary*/
                            //					result.setEditMode(false);
                            //				    delRecordItem.setEnabled(false);
                            //					insertRecordItem.setEnabled(false);
                            //					insertSaveItem.setEnabled(false) ;
                            //					swRecordItem.setEnabled(false);
                            //					rollbackModifiedItem.setEnabled(false);
                            result.getTblResult().setBackground(BACKGROUND_DIRTY);
                        }
                    }
                });

        rollbackModifiedItem = new ToolItem(topLeftToolBar, SWT.PUSH);
        result.rollbackModifiedItem = rollbackModifiedItem;
        rollbackModifiedItem.setToolTipText(Messages.insertRollback);
        rollbackModifiedItem.setImage(
                CommonUIPlugin.getImage("icons/queryeditor/query_rollback.png"));
        rollbackModifiedItem.setDisabledImage(
                CommonUIPlugin.getImage("icons/queryeditor/query_rollback_disabled.png"));
        rollbackModifiedItem.setEnabled(false);
        rollbackModifiedItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (!CommonUITool.openConfirmBox(
                                getShell(), (Messages.msgRollbackEdited))) {
                            return;
                        }

                        result.tblResult.forceFocus();

                        insertSaveItem.setEnabled(false);
                        rollbackModifiedItem.setEnabled(false);
                        result.clearModifiedLog();

                        result.makeItem();

                        result.swRecordItem.setSelection(false);
                        result.insertRecordItem.setEnabled(false);
                        result.delRecordItem.setEnabled(false);
                        result.setEditMode(false);
                        result.tblResult.setBackground(BACKGROUND_NORMAL);
                    }
                });

        // add item selection listener
        //		result.tblResult.addSelectionListener(new SelectionListener(){
        //			public void widgetSelected(SelectionEvent event) {
        //				int selectedItemCount = result.tblResult.getSelectionCount();
        //				if (selectedItemCount > 0 && result.getEditable() && result.isEditMode()) {
        //					result.delRecordItem.setEnabled(true);
        //				} else {
        //					result.delRecordItem.setEnabled(false);
        //				}
        //			}
        //
        //			public void widgetDefaultSelected(SelectionEvent event) {
        //			}
        //		});

        if (!isMutliQuery) {
            new ToolItem(topLeftToolBar, SWT.SEPARATOR);

            copyInsertSqlFromRecordsItem = new ToolItem(topLeftToolBar, SWT.PUSH);
            copyInsertSqlFromRecordsItem.setImage(
                    CommonUIPlugin.getImage("icons/queryeditor/record_to_insert.png"));
            copyInsertSqlFromRecordsItem.setToolTipText(Messages.makeInsertFromSelectedRecord);
            copyInsertSqlFromRecordsItem.addSelectionListener(
                    new SelectionListener() {
                        public void widgetSelected(SelectionEvent e) {
                            if (editor == null) {
                                return;
                            }
                            String text =
                                    editor.getQueryExecuter().makeInsertQueryWithSelectedRecords();
                            if (StringUtil.isEmpty(text)) {
                                CommonUITool.openErrorBox(
                                        getShell(), Messages.canNotMakeQueryBecauseNoSelected);
                                return;
                            }
                            CommonUITool.copyContentToClipboard(text);
                        }

                        public void widgetDefaultSelected(SelectionEvent e) {}
                    });

            copyUpdateSqlFromRecordsItem = new ToolItem(topLeftToolBar, SWT.PUSH);
            copyUpdateSqlFromRecordsItem.setImage(
                    CommonUIPlugin.getImage("icons/queryeditor/record_to_update.png"));
            copyUpdateSqlFromRecordsItem.setToolTipText(Messages.makeUpdateFromSelectedRecord);
            copyUpdateSqlFromRecordsItem.addSelectionListener(
                    new SelectionListener() {
                        public void widgetSelected(SelectionEvent e) {
                            if (editor == null) {
                                return;
                            }
                            String text =
                                    editor.getQueryExecuter().makeUpdateQueryWithSelectedRecords();
                            if (StringUtil.isEmpty(text)) {
                                CommonUITool.openErrorBox(
                                        getShell(), Messages.canNotMakeQueryBecauseNoSelected);
                                return;
                            }
                            CommonUITool.copyContentToClipboard(text);
                        }

                        public void widgetDefaultSelected(SelectionEvent e) {}
                    });
        }

        new ToolItem(topLeftToolBar, SWT.SEPARATOR);

        final ToolItem exportDataItem = new ToolItem(topLeftToolBar, SWT.PUSH);
        exportDataItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/table_data_export.png"));
        exportDataItem.setToolTipText(Messages.msgExportAllQueryResults);
        exportDataItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        CTabItem[] items = queryResultTabFolder.getItems();
                        if (items == null || items.length == 0) {
                            return;
                        }

                        List<QueryExecuter> qeList = new ArrayList<QueryExecuter>();
                        for (CTabItem item : items) {
                            if (!(item.getData() instanceof QueryExecuter)) {
                                continue;
                            }
                            QueryExecuter qe = (QueryExecuter) item.getData();
                            qeList.add(qe);
                        }
                        if (qeList.isEmpty()) {
                            return;
                        }

                        ExportQueryResultDialog dialog =
                                new ExportQueryResultDialog(getShell(), qeList);
                        dialog.open();
                    }
                });

        new ToolItem(topLeftToolBar, SWT.SEPARATOR);

        final ToolItem showLogItem = new ToolItem(topLeftToolBar, SWT.PUSH);
        showLogItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/qe_panel_down.png"));
        showLogItem.setToolTipText(Messages.tooltip_qedit_log_show_hide);
        showLogItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (tableLogSash.getMaximizedControl() == null) {
                            tableLogSash.setMaximizedControl(resultContainer);
                            showLogItem.setImage(
                                    CommonUIPlugin.getImage("icons/queryeditor/qe_panel_up.png"));
                            tableLogSash.layout(true);
                        } else {
                            tableLogSash.setMaximizedControl(null);
                            showLogItem.setImage(
                                    CommonUIPlugin.getImage("icons/queryeditor/qe_panel_down.png"));
                            tableLogSash.layout(true);
                        }
                    }
                });

        new ToolItem(topLeftToolBar, SWT.SEPARATOR);

        viewForm.setContent(tableLogSash);
        viewForm.setTopRight(topRightToolBar);
        viewForm.setTopLeft(topLeftToolBar);

        final QueryTableTabItem queryTableTabItem = new QueryTableTabItem(parentFolder, SWT.NONE);
        queryTableTabItem.setText(Messages.qedit_result + (result.idx + 1));
        queryTableTabItem.setControl(viewForm);
        queryTableTabItem.setData(result);
        queryTableTabItem.addDisposeListener(
                new DisposeListener() {
                    public void widgetDisposed(DisposeEvent e) {
                        InfoWindowManager.getInstance().updateContent(editor);
                    }
                });

        parentFolder.setSelection(queryTableTabItem);

        tableSelectSupport.addSelectChangeListener(
                new ISelectionChangeListener() {
                    @SuppressWarnings("unchecked")
                    public void selectionChanged(SelectionChangeEvent event) {
                        boolean active = event.selectedArray.length > 0;
                        boolean canEdit = active && result.getEditable() && result.isEditMode();
                        if (delRecordItem != null) {
                            delRecordItem.setEnabled(canEdit);
                        }
                        if (copyInsertSqlFromRecordsItem != null) {
                            copyInsertSqlFromRecordsItem.setEnabled(active);
                        }
                        if (copyUpdateSqlFromRecordsItem != null) {
                            copyUpdateSqlFromRecordsItem.setEnabled(active);
                        }
                        /*TOOLS-3632 Add calculation info*/
                        int count = event.selectedArray.length;
                        BigDecimal sum = new BigDecimal(0);
                        BigDecimal average = null;
                        int numericCount = 0;
                        QueryResultTableCalcInfo queryResultTableCalcInfo = null;
                        StatusLineContrItem statusCont =
                                LayoutManager.getInstance().getStatusLineContrItem();
                        if (count > 1) {
                            for (Point p : event.selectedArray) {
                                TableColumn column = result.tblResult.getColumn(p.x);
                                if (column == null) {
                                    continue;
                                }
                                ColumnInfo columnInfo = (ColumnInfo) column.getData();
                                if (columnInfo == null) {
                                    continue;
                                }
                                String dataType = columnInfo.getType();
                                if (!DataType.isNumberType(dataType)) {
                                    continue;
                                }

                                TableItem item = result.tblResult.getItem(p.y);
                                if (item == null || item.getData() == null) {
                                    continue;
                                }
                                Map<String, CellValue> dataMap =
                                        (Map<String, CellValue>) item.getData();
                                CellValue cellValue = dataMap.get(Integer.toString(p.x));
                                if (cellValue != null && cellValue.getValue() != null) {
                                    numericCount++;
                                    Object value = cellValue.getValue();
                                    if (value instanceof Integer) {
                                        sum = sum.add(new BigDecimal((Integer) value));
                                    } else if (value instanceof Short) {
                                        sum = sum.add(new BigDecimal((Short) value));
                                    } else if (value instanceof Long) {
                                        sum = sum.add(new BigDecimal((Long) value));
                                    } else if (value instanceof Float) {
                                        sum = sum.add(new BigDecimal((Float) value));
                                    } else if (value instanceof Double) {
                                        sum = sum.add(new BigDecimal((Double) value));
                                    } else if (value instanceof BigDecimal) {
                                        sum = sum.add((BigDecimal) value);
                                    }
                                }
                            }
                            if (numericCount > 0) {
                                average =
                                        sum.divide(
                                                new BigDecimal(numericCount),
                                                3,
                                                RoundingMode.HALF_UP);
                                queryResultTableCalcInfo =
                                        new QueryResultTableCalcInfo(count, average, sum);
                            } else {
                                queryResultTableCalcInfo = new QueryResultTableCalcInfo(count);
                            }
                        }
                        queryTableTabItem.setQueryResultTableCalcInfo(queryResultTableCalcInfo);
                        InfoWindowManager.getInstance().updateContent(editor);
                        statusCont.changeStuatusLineForViewOrEditPart(
                                editor.getSelectedDatabase(), editor);
                    }
                });
    }

    /** Display Data Compare Label */
    public void displayDataCompareLabel(final Composite composite, final Table resultTable) {
        Color redColor = ResourceManager.getColor(255, 0, 0);
        Color greenColor = ResourceManager.getColor(0, 154, 33);
        Color blueColor = ResourceManager.getColor(0, 0, 255);

        Composite labelComposite = new Composite(composite, SWT.NONE);
        labelComposite.setLayout(new GridLayout(6, false));

        if (this.baseQueryExecuter != null) {
            Label labelRedIconMsg = new Label(labelComposite, SWT.NONE);
            labelRedIconMsg.setText(Messages.diffData);
            labelRedIconMsg.setForeground(redColor);

            Label sepWithResult =
                    new Label(labelComposite, SWT.SEPARATOR | SWT.VERTICAL | SWT.SHADOW_OUT);
            sepWithResult.setLayoutData(new GridData(GridData.FILL_VERTICAL));

            Label labelGreenIconMsg = new Label(labelComposite, SWT.NONE);
            labelGreenIconMsg.setText(Messages.extraColumns);
            labelGreenIconMsg.setForeground(greenColor);

            Label sepWithResult2 =
                    new Label(labelComposite, SWT.SEPARATOR | SWT.VERTICAL | SWT.SHADOW_OUT);
            sepWithResult2.setLayoutData(new GridData(GridData.FILL_VERTICAL));

            Label labelBlueIconMsg = new Label(labelComposite, SWT.NONE);
            labelBlueIconMsg.setText(Messages.extraRows);
            labelBlueIconMsg.setForeground(blueColor);

            final Button viewComparisonBtn = new Button(labelComposite, SWT.NONE);
            viewComparisonBtn.setText(Messages.hideHighlight);
            viewComparisonBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, true, 1, 2));
            viewComparisonBtn.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(final SelectionEvent event) {
                            if (showHighlight == true) {
                                hideCompareHighlight(resultTable);
                                viewComparisonBtn.setText(Messages.showHighlight);
                                showHighlight = false;
                            } else {
                                showCompareHighlight(resultTable);
                                viewComparisonBtn.setText(Messages.hideHighlight);
                                showHighlight = true;
                            }
                        }
                    });
        } else {
            Label baseLabel = new Label(labelComposite, SWT.NONE);
            baseLabel.setText(Messages.baseTableMsg);
            baseLabel.setFont(new Font(Display.getCurrent(), "Arial", 8, SWT.BOLD));
        }
    }

    /** Hide Data Compare Highlight */
    public void hideCompareHighlight(Table resultTable) {
        Color darkColor = ResourceManager.getColor(0, 0, 0);

        TableItem[] rowItems = resultTable.getItems();
        colorSets = new Color[rowItems.length][resultTable.getColumnCount()];
        for (int i = 0; i < rowItems.length; i++) {
            TableItem item = rowItems[i];
            for (int j = 0; j < resultTable.getColumnCount(); j++) {
                colorSets[i][j] = item.getForeground(j + 1);
                item.setForeground(j + 1, darkColor);
            }
        }
    }

    /** Show Data Compare Highlight */
    public void showCompareHighlight(Table resultTable) {
        TableItem[] rowItems = resultTable.getItems();
        for (int i = 0; i < rowItems.length; i++) {
            TableItem item = rowItems[i];
            for (int j = 0; j < resultTable.getColumnCount(); j++) {
                item.setForeground(j + 1, colorSets[i][j]);
            }
        }
    }

    /**
     * checkConnection
     *
     * @return boolean
     */
    public boolean checkConnection() {
        return editor.isConnected();
    }

    /**
     * Make the progress bar
     *
     * @param runnable
     * @param serverInfo
     * @param isMultiQuery
     */
    public void makeProgressBar(
            final Runnable runnable, ServerInfo serverInfo, boolean isMultiQuery) {
        isCancelling = false;
        startMillis = System.currentTimeMillis();

        Composite comp = new Composite(queryResultTabFolder, SWT.NONE);
        comp.setLayout(new GridLayout(6, true));
        ProgressBar runQueryPb = new ProgressBar(comp, SWT.HORIZONTAL | SWT.INDETERMINATE);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 5;
        runQueryPb.setLayoutData(gridData);

        stopButton = new Button(comp, SWT.PUSH);
        stopButton.setText(Messages.stopBtn);
        stopButton.setToolTipText(Messages.stopBtnTip);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 1;
        stopButton.setLayoutData(gridData);

        // show elapsed time
        Label timeLabel = new Label(comp, SWT.LEFT);
        timeLabel.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 6, 1, -1, -1));
        timeLabel.setText(getElapsedTimeStr(0));
        timeLabel.setForeground(ResourceManager.getColor(255, 0, 0));
        setTimer(runQueryPb, timeLabel);

        boolean isSupported = false;
        if (isMultiQuery) {
            isSupported = CompatibleUtil.isSupportJDBCCancel(serverInfo);
        } else {
            ServerInfo selectedServerInfo =
                    editor.getSelectedServer() == null
                            ? null
                            : editor.getSelectedServer().getServerInfo();
            isSupported = CompatibleUtil.isSupportJDBCCancel(selectedServerInfo);
        }
        stopButton.setEnabled(isSupported);
        if (isSupported) {
            stopButton.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            if (isCancelling) {
                                if (!CommonUITool.openConfirmBox(Messages.msgDisConnect)) {
                                    return;
                                }
                                editor.disconnectAndDisposeResults();
                                stopButton.setEnabled(false);
                            } else {
                                isCancelling = true;
                                startMillis = System.currentTimeMillis();
                                stopButton.setEnabled(false);
                                Thread thread = new Thread(runnable);
                                thread.start();
                            }
                        }
                    });
        }
        CTabItem tab = new CTabItem(queryResultTabFolder, SWT.NONE);
        tab.setText(Messages.proRunQuery);
        tab.setControl(comp);
        queryResultTabFolder.setSelection(isMultiQuery ? 1 : 0);
    }

    /**
     * Make the progress bar
     *
     * @param runnable Runnable
     */
    public void makeProgressBar(final Runnable runnable) {
        makeProgressBar(runnable, null, false);
    }

    /**
     * Make the progress bar
     *
     * @param runnable
     * @param serverInfo
     */
    public void makeProgressBar(final Runnable runnable, ServerInfo serverInfo) {
        makeProgressBar(runnable, serverInfo, true);
    }

    /**
     * Get the elapsed time string
     *
     * @param mills long
     * @return String
     */
    private String getElapsedTimeStr(long mills) {
        return Messages.bind(
                isCancelling ? Messages.lblElapsedCancelTime : Messages.lblElapsedTime,
                DateUtil.getDatetimeString(mills, "mm:ss"));
    }

    /**
     * Set timer for the label showing
     *
     * @param queryThread QueryThread
     * @param runQueryPb ProgressBar
     * @param timeLabel Label
     */
    private void setTimer(final ProgressBar runQueryPb, final Label timeLabel) {
        final Timer timer = new Timer();
        TimerTask timerTask =
                new TimerTask() {
                    public void run() {
                        if (null == runQueryPb || runQueryPb.isDisposed()) {
                            timer.cancel();
                            return;
                        }
                        runQueryPb
                                .getDisplay()
                                .asyncExec(
                                        new Runnable() {
                                            public void run() {
                                                if (null == timeLabel || timeLabel.isDisposed()) {
                                                    timer.cancel();
                                                    return;
                                                }
                                                long elapMillis =
                                                        System.currentTimeMillis() - startMillis;
                                                timeLabel.setText(getElapsedTimeStr(elapMillis));
                                                if (!stopButton.getEnabled()
                                                        && isCancelling
                                                        && elapMillis > STOP_CONNECT_TIMEOUT_MSEC) {
                                                    stopButton.setText(Messages.lblDisConnect);
                                                    stopButton.setEnabled(true);
                                                }
                                            }
                                        });
                    };
                };
        timer.scheduleAtFixedRate(timerTask, new Date(), 1000);
    }

    /**
     * Set drop target for table
     *
     * @param table Table
     */
    private void setDropTraget(Table table) {
        editor.getDragController().addTableDropTarget(table);
    }

    public CTabFolder getQueryResultTabFolder() {
        return queryResultTabFolder;
    }

    /**
     * this value used to determined tab items in multiTabs are still creating or have been created
     * already. false = creating tab items task is not finished. true = all tab items have been
     * created. next time to invoke <i>disposeTabResult</i> will dispose all tab items
     */
    private boolean canDispose = false;

    public void setCanDispose(boolean canDispose) {
        this.canDispose = canDispose;
    }

    public void disposeTabResult() {
        if (!canDispose) return;
        super.dispose();
    }

    /** Dispose all query results */
    public void disposeAllResult() {
        if (queryResultTabFolder == null || queryResultTabFolder.isDisposed()) {
            return;
        }
        while (queryResultTabFolder.getItemCount() > 0) {
            if (!queryResultTabFolder.getItem(0).getControl().isDisposed()) {
                queryResultTabFolder.getItem(0).getControl().dispose();
            }
            queryResultTabFolder.getItem(0).dispose();
        }
        // disposeTabResult();
    }

    /** Set selection and bring to top */
    public void setSelection() {
        resultTabFolder.setSelection(queryResultTabItem);
    }

    public void setQueryResultTabItemName(String text) {
        queryResultTabItem.setText(text);
    }

    public StyledText getLogMessagesArea() {
        return logMessagesArea;
    }

    public Table getResultTable() {
        return null;
        //		return resultTable;
    }

    /**
     * Select the sub tab by the tab index
     *
     * @param subTabIndex
     */
    public void select(int subTabIndex) {
        if (queryResultTabFolder == null) {
            return;
        }

        int total = queryResultTabFolder.getItemCount();
        if (subTabIndex >= total) {
            return;
        }

        queryResultTabFolder.setSelection(subTabIndex);
    }

    public void setMultiResultsCompare(boolean multiResultsCompare) {
        this.multiResultsCompare = multiResultsCompare;
    }

    public boolean getMultiResultsCompare() {
        return this.multiResultsCompare;
    }

    public void setBaseQueryExecuter(QueryExecuter baseQueryExecuter) {
        this.baseQueryExecuter = baseQueryExecuter;
    }

    public QueryExecuter getBaseQueryExecuter() {
        return this.baseQueryExecuter;
    }
}
