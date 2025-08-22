/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the <ORGANIZATION> nor the names of its contributors
 *   may be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */
package com.cubrid.common.ui.query.tuner.dialog;

import com.cubrid.common.core.util.DateUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.preference.GeneralPreference;
import com.cubrid.common.ui.compare.schema.control.TextCompareInput;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.control.ColumnComparator;
import com.cubrid.common.ui.query.control.ColumnInfo;
import com.cubrid.common.ui.query.control.TextViewerOperationHandler;
import com.cubrid.common.ui.query.control.jface.text.contentassist.IContentAssistant;
import com.cubrid.common.ui.query.control.queryplan.PLAN_DISPLAY_MODE;
import com.cubrid.common.ui.query.control.queryplan.QueryPlanComposite;
import com.cubrid.common.ui.query.editor.IDatabaseProvider;
import com.cubrid.common.ui.query.editor.ISQLPartitions;
import com.cubrid.common.ui.query.editor.SQLContentAssistProcessor;
import com.cubrid.common.ui.query.editor.SQLDocument;
import com.cubrid.common.ui.query.editor.SQLPartitionScanner;
import com.cubrid.common.ui.query.editor.SQLTextViewer;
import com.cubrid.common.ui.query.editor.SQLViewerConfiguration;
import com.cubrid.common.ui.query.tuner.IQueryChangeListener;
import com.cubrid.common.ui.query.tuner.IQueryJob;
import com.cubrid.common.ui.query.tuner.QueryEvent;
import com.cubrid.common.ui.query.tuner.QueryRecord;
import com.cubrid.common.ui.query.tuner.QueryRecordProject;
import com.cubrid.common.ui.query.tuner.QueryTunerJob;
import com.cubrid.common.ui.query.tuner.TextRecordProcessor;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.persist.ApplicationPersistUtil;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.table.TableSelectSupport;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.tool.editor.TextEditorFindReplaceMediator;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.draw2d.GridData;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.TextViewerUndoManager;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.slf4j.Logger;

/**
 * QueryTunerDialog Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-4-9 created by Kevin.Wang
 */
public class QueryTunerDialog extends TrayDialog
        implements ITextListener, IQueryChangeListener, IDatabaseProvider {
    private static final Logger LOGGER = LogUtil.getLogger(QueryTunerDialog.class);
    private static final Color BACK_COLOR = ResourceManager.getColor(204, 204, 204);
    private static final Color SASH_COLOR = ResourceManager.getColor(128, 128, 128);
    private static final DateFormat formater =
            DateUtil.getDateFormat("yyyy-MM-dd a hh:mm:ss", Locale.ENGLISH);
    private static final String SQL_EDITOR_FLAG = TextEditorFindReplaceMediator.SQL_EDITOR_FLAG;
    private static final int SASH_WIDTH = 2;
    private static final int DISPLAY_SQL = 1;
    private static final int DISPLAY_TEXT = 2;
    private static final int DISPLAY_TREE = 4;
    private static final int DISPLAY_GRAPH = 8;

    private CubridDatabase database;
    private Composite topComposite;
    private Composite buttomComposite;
    private ToolItem addItem;
    private ToolItem renameItem;
    private ToolItem deleteItem;
    private Combo historyCombo;
    private ToolItem runItem;
    private ToolItem runPlanItem;
    private ToolItem saveQueryItem;
    private Composite queryResultContainer;
    private Composite queryResultComposite;
    private SQLTextViewer sqlTextViewer;
    private SQLViewerConfiguration viewerConfig;
    private TextViewerUndoManager undoManager;
    private TextViewerOperationHandler formatHandler;
    private TextViewerOperationHandler contentAssistHandler;
    private Composite queryPlanContainer;
    private Composite queryPlanComposite;
    private QueryRecord lastRecord;
    private QueryRecord currentRecord;
    private ToolItem hideItem;
    private boolean isHiden;
    private ToolItem dispModeTextItem;
    private ToolItem dispModeTreeItem;
    private ToolItem dispModeGraphItem;
    private TableViewer compareLeftTableViewer, compareRightTableViewer;
    private TextMergeViewer textMergeViewer;
    private CompareConfiguration textMergeConfig;
    private Combo queryRecordLeftCombo;
    private Combo queryRecordRightCombo;
    private QueryPlanComposite leftComparePlanComposite;
    private QueryPlanComposite rightComparePlanComposite;
    private SashForm queryPlanCompareSashForm;
    private ToolItem compareItem;
    private ToolItem delQueryRecordItem;
    private ToolItem multiRunItem;
    private ToolItem sqlModeItem;
    private ToolItem textModelItem;
    private ToolItem treeModelItem;
    private ToolItem graphModelItem;
    private int tunerDisplayModel = DISPLAY_TREE;
    private IContentAssistant contentAssistant;
    private IContentAssistant recentlyUsedSQLcontentAssistant;
    private QueryRecordProject queryRecordProject;
    private String query;
    private CTabFolder tabFolder;

    public QueryTunerDialog(Shell parentShell, CubridDatabase database) {
        super(parentShell);
        this.database = database;
    }

    public QueryTunerDialog(Shell parentShell, CubridDatabase database, String query) {
        super(parentShell);
        this.database = database;
        this.query = query;
    }

    /**
     * Create dialog area content
     *
     * @param parent the parent composite
     * @return the control
     */
    protected Control createDialogArea(Composite parent) {
        Composite container = new Composite(parent, SWT.None);
        container.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        container.setLayout(new FormLayout());

        topComposite = new Composite(container, SWT.None);

        FormData topData = new FormData();
        topData.top = new FormAttachment(0, 0);
        topData.bottom = new FormAttachment(0, 30);
        topData.left = new FormAttachment(0, 0);
        topData.right = new FormAttachment(100, 0);
        topComposite.setLayoutData(topData);
        topComposite.setLayout(new GridLayout(6, false));

        Label historyLable = new Label(topComposite, SWT.None);
        historyLable.setText(Messages.lblProject);
        historyLable.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        historyCombo = new Combo(topComposite, SWT.READ_ONLY);
        historyCombo.setLayoutData(CommonUITool.createGridData(1, 1, 200, -1));
        historyCombo.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        String name = historyCombo.getText();
                        switchQueryRecordProject(name);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        widgetSelected(e);
                    }
                });

        ToolBar toolBar = new ToolBar(topComposite, SWT.None);
        toolBar.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));

        addItem = new ToolItem(toolBar, SWT.PUSH);
        addItem.setImage(CommonUIPlugin.getImage("icons/queryplan/add_query.gif"));
        addItem.setToolTipText(Messages.itemTooltipAdd);
        addItem.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        addQueryRecordProject();
                    }
                });

        deleteItem = new ToolItem(toolBar, SWT.PUSH);
        deleteItem.setImage(CommonUIPlugin.getImage("icons/queryplan/delete_query.gif"));
        deleteItem.setToolTipText(Messages.itemTooltipRemove);
        deleteItem.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                        if (queryRecordLeftCombo.getItemCount() == 0) {
                            CommonUITool.openErrorBox(Messages.msgQueryTunerNotSavedProject);
                            tabFolder.setSelection(0);
                        }
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        if (CommonUITool.openConfirmBox(Messages.confirmDeleteTuningProject)) {
                            ApplicationPersistUtil.getInstance()
                                    .removeQueryRecordProject(
                                            database.getDatabaseInfo(), historyCombo.getText());
                            ApplicationPersistUtil.getInstance().save();
                            switchQueryRecordProject("");
                        }
                    }
                });

        renameItem = new ToolItem(toolBar, SWT.PUSH);
        renameItem.setImage(CommonUIPlugin.getImage("icons/queryplan/rename_query.gif"));
        renameItem.setToolTipText(Messages.itemTooltipRename);
        renameItem.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        renameQueryRecordProject();
                    }
                });

        Label placeHolderLabel = new Label(topComposite, SWT.None);
        placeHolderLabel.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

        buttomComposite = new Composite(container, SWT.None);
        buttomComposite.setLayout(new FillLayout());

        FormData buttomData = new FormData();
        buttomData.top = new FormAttachment(0, 30);
        buttomData.bottom = new FormAttachment(100, 0);
        buttomData.left = new FormAttachment(0, 0);
        buttomData.right = new FormAttachment(100, 0);
        buttomComposite.setLayoutData(buttomData);
        buttomComposite.setLayout(new FillLayout());

        tabFolder = new CTabFolder(buttomComposite, SWT.BORDER);
        tabFolder.setTabHeight(20);
        tabFolder.marginHeight = 5;
        tabFolder.marginWidth = 5;
        tabFolder.setMaximizeVisible(false);
        tabFolder.setMinimizeVisible(false);
        tabFolder.setSimple(false);
        tabFolder.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        if (tabFolder.getSelectionIndex() == 1) {
                            List<QueryRecordProject> projectList = loadProjectList();
                            if (projectList.size() == 0) {
                                CommonUITool.openErrorBox(Messages.msgQueryTunerNotSavedProject);
                                tabFolder.setSelection(0);
                            } else {
                                QueryRecordProject usingProject = null;
                                String projectName = historyCombo.getText();
                                for (int i = 0; i < projectList.size(); i++) {
                                    QueryRecordProject temp = projectList.get(i);
                                    if (StringUtil.isEqual(projectName, temp.getName())) {
                                        usingProject = temp;
                                        break;
                                    }
                                }
                                if (usingProject != null) {
                                    if (usingProject.getQueryRecordList().size() == 0) {
                                        CommonUITool.openErrorBox(Messages.errNoQueryInProject);
                                        return;
                                    }

                                    switchQueryRecordProject(historyCombo.getText());
                                }
                            }
                        }
                    }
                });

        createQueryTunerTab(tabFolder);
        createQueryCompareTab(tabFolder);

        init();
        return parent;
    }

    private void createQueryTunerTab(CTabFolder tabFolder) {
        CTabItem item = new CTabItem(tabFolder, SWT.None | SWT.MULTI | SWT.V_SCROLL);
        item.setText(Messages.tabItemQueryTuner);
        tabFolder.setSelection(item);

        SashForm form = new SashForm(tabFolder, SWT.HORIZONTAL);
        item.setControl(form);
        form.setLayout(new FillLayout());
        /*Left composite*/
        Composite leftComposite = new Composite(form, SWT.BORDER);
        leftComposite.setLayout(new GridLayout());
        /*Right composite*/
        queryPlanContainer = new Composite(form, SWT.BORDER);
        queryPlanContainer.setLayout(new FillLayout());

        form.setWeights(new int[] {40, 60});

        ToolBar toolBar = new ToolBar(leftComposite, SWT.None);

        runItem = new ToolItem(toolBar, SWT.None);
        runItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_run.png"));
        runItem.setDisabledImage(
                CommonUIPlugin.getImage("icons/queryeditor/query_run_disabled.png"));
        runItem.setToolTipText(Messages.run);
        runItem.setEnabled(false);
        runItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        runQuery();
                    }
                });

        runPlanItem = new ToolItem(toolBar, SWT.None);
        runPlanItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_execution_plan.png"));
        runPlanItem.setToolTipText(Messages.queryPlanTip);
        runPlanItem.setEnabled(false);
        runPlanItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        runQueryPlan();
                    }
                });

        saveQueryItem = new ToolItem(toolBar, SWT.None);
        saveQueryItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/file_save.png"));
        saveQueryItem.setToolTipText(Messages.ttSaveQueryTuning);
        saveQueryItem.setEnabled(false);
        saveQueryItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        saveQueryRecord();
                    }
                });

        SashForm sashForm = new SashForm(leftComposite, SWT.VERTICAL);
        sashForm.setBackground(SASH_COLOR);
        sashForm.setLayout(new FillLayout());
        sashForm.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));

        CompositeRuler ruler = new CompositeRuler();
        LineNumberRulerColumn lineCol = new LineNumberRulerColumn();
        lineCol.setBackground(ResourceManager.getColor(new RGB(236, 233, 216)));
        ruler.addDecorator(0, lineCol);

        sqlTextViewer =
                new SQLTextViewer(sashForm, ruler, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER, this);
        viewerConfig = new SQLViewerConfiguration(this);

        sqlTextViewer.configure(viewerConfig);

        SQLDocument document = new SQLDocument();
        IDocumentPartitioner partitioner =
                new FastPartitioner(new SQLPartitionScanner(), SQLPartitionScanner.getAllTypes());
        document.setDocumentPartitioner(ISQLPartitions.SQL_PARTITIONING, partitioner);
        partitioner.connect(document);
        sqlTextViewer.setDocument(document);

        undoManager = new TextViewerUndoManager(50);
        undoManager.connect(sqlTextViewer);

        contentAssistant = viewerConfig.getContentAssistant(sqlTextViewer);
        contentAssistant.install(sqlTextViewer);

        recentlyUsedSQLcontentAssistant =
                viewerConfig.getRecentlyUsedContentAssistant(sqlTextViewer);
        recentlyUsedSQLcontentAssistant.install(sqlTextViewer);
        formatHandler = new TextViewerOperationHandler(sqlTextViewer, ISourceViewer.FORMAT);
        contentAssistHandler =
                new TextViewerOperationHandler(
                        sqlTextViewer, ISourceViewer.CONTENTASSIST_PROPOSALS);

        StyledText text = (StyledText) sqlTextViewer.getTextWidget();
        text.setIndent(1);
        text.setData(SQL_EDITOR_FLAG, sqlTextViewer);
        addTextViewerListener(text);

        queryResultContainer = new Composite(sashForm, SWT.None);
        queryResultContainer.setLayout(new FillLayout());

        displayQuery(null);
        displayQueryPlan(null);
    }

    /** Run query */
    private void runQuery() {
        String query = getFirstSelectedQuery();
        if (StringUtil.isEmpty(query)) {
            return;
        }

        runItem.setEnabled(false);
        runPlanItem.setEnabled(false);

        List<String> queryList = new ArrayList<String>();
        queryList.add(query);

        QueryTunerJob job =
                new QueryTunerJob(
                        IQueryJob.RUN_QUERY
                                | IQueryJob.RUN_PLAN
                                | IQueryJob.COLLECT_STAT
                                | IQueryJob.AUTO_COMMIT,
                        database.getDatabaseInfo(),
                        queryList,
                        new TextRecordProcessor(),
                        QueryTunerDialog.this);

        job.schedule();
        try {
            job.join();
            List<QueryRecord> queryRecordList = job.getQueryRecordList();
            if (queryRecordList.size() > 0) {
                lastRecord = currentRecord;
                currentRecord = queryRecordList.get(0);
                displayQuery(currentRecord);
                displayQueryPlan(currentRecord);
            }
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage());
        }

        runItem.setEnabled(true);
        runPlanItem.setEnabled(true);
    }

    /** Run query plan */
    private void runQueryPlan() {
        String query = getFirstSelectedQuery();
        if (StringUtil.isEmpty(query)) {
            return;
        }

        runItem.setEnabled(false);
        runPlanItem.setEnabled(false);

        List<String> queryList = new ArrayList<String>();
        queryList.add(query);

        QueryTunerJob job =
                new QueryTunerJob(
                        IQueryJob.RUN_PLAN | /*IQueryJob.COLLECT_STAT | */ IQueryJob.AUTO_COMMIT,
                        database.getDatabaseInfo(),
                        queryList,
                        new TextRecordProcessor(),
                        QueryTunerDialog.this);
        job.schedule();
        try {
            job.join();
            List<QueryRecord> queryRecordList = job.getQueryRecordList();
            if (queryRecordList.size() > 0) {
                lastRecord = currentRecord;
                currentRecord = queryRecordList.get(0);

                displayQueryPlan(currentRecord);
            }
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage());
        }

        runItem.setEnabled(true);
        runPlanItem.setEnabled(true);
    }

    /**
     * Run query plan for compare tab
     *
     * @param leftQuery
     * @param rightQuery
     */
    private void runQueryPlanAgain(String leftQuery, String rightQuery) {
        multiRunItem.setEnabled(false);

        List<String> queryList = new ArrayList<String>();

        if (!StringUtil.isEmpty(leftQuery)) {
            queryList.add(leftQuery);
        }

        if (!StringUtil.isEmpty(rightQuery)) {
            queryList.add(rightQuery);
        }

        QueryTunerJob job =
                new QueryTunerJob(
                        IQueryJob.RUN_PLAN | IQueryJob.COLLECT_STAT | IQueryJob.AUTO_COMMIT,
                        database.getDatabaseInfo(),
                        queryList,
                        new TextRecordProcessor(),
                        QueryTunerDialog.this);
        job.schedule();
        try {
            job.join();
            List<QueryRecord> queryRecordList = job.getQueryRecordList();
            QueryRecord leftRecord = null, rightRecord = null;

            if (queryRecordList.size() == 2) {
                leftRecord = queryRecordList.get(0);
                rightRecord = queryRecordList.get(1);
            } else if (queryRecordList.size() == 1) {
                if (!StringUtil.isEmpty(leftQuery)) {
                    leftRecord = queryRecordList.get(0);
                } else {
                    rightRecord = queryRecordList.get(0);
                }
            }
            displayComparePlan(leftRecord, rightRecord);

        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage());
        }

        multiRunItem.setEnabled(true);
    }

    /**
     * Get first selected query
     *
     * @return
     */
    private String getFirstSelectedQuery() {
        List<String> queryList = QueryUtil.queriesToQuery(getSelectedQueries());

        if (queryList.size() >= 1) {
            return queryList.get(0);
        }
        return "";
    }

    protected boolean useCompletions = true;
    private boolean pendingCompletionsListener = false;

    /**
     * Add SQL viewer listener
     *
     * @param text
     */
    private void addTextViewerListener(final StyledText text) {

        text.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        ServerInfo serverInfo = getServerInfo();
                        boolean isLowerCase = QueryOptions.getKeywordLowercase(serverInfo);
                        boolean isNoAutoUpperCase =
                                QueryOptions.getNoAutoUppercaseKeyword(serverInfo);
                        if (!isLowerCase && !isNoAutoUpperCase) {
                            autoReplaceKeyword();
                        }
                    }

                    // replace keyword to upper case automatically
                    public void autoReplaceKeyword() { // FIXME extract tokenize code to utility
                        if (pendingCompletionsListener) {
                            return;
                        }

                        int pos = text.getCaretOffset() - 1;
                        if (pos <= 0) {
                            return;
                        }

                        String currentKey = text.getText(pos, pos);
                        if (currentKey == null || currentKey.length() <= 0) {
                            return;
                        }

                        char cur = currentKey.charAt(0);
                        if (cur != ' '
                                && cur != '('
                                && cur != '\t'
                                && cur != '\n'
                                && cur != '\r'
                                && cur != ',') {
                            return;
                        }

                        pos--;
                        if (pos < 0) {
                            return;
                        }

                        int spos = pos - 20;
                        if (spos < 0) {
                            spos = 0;
                        }

                        String txt = text.getText(spos, pos);
                        spos = pos + 1;
                        for (int i = txt.length() - 1; i >= 0; i--) {
                            char c = txt.charAt(i);
                            if (c == ' ' || c == '\t' || c == '\n' || c == '(') {
                                break;
                            }

                            spos--;
                        }

                        int epos = pos;
                        if (spos < 0 || epos < 0 || spos > epos) {
                            return;
                        }

                        String currentKeyword = text.getText(spos, epos);
                        if (currentKeyword == null) {
                            return;
                        }

                        int len = currentKeyword.length();
                        for (int i = 0; i < QuerySyntax.KEYWORDS_AUTO_UPPER.length; i++) {
                            String keyword = QuerySyntax.KEYWORDS_AUTO_UPPER[i];
                            if (keyword.equalsIgnoreCase(currentKeyword)) {
                                pendingCompletionsListener = true;
                                text.replaceTextRange(spos, len, keyword.toUpperCase());
                                pendingCompletionsListener = false;
                                break;
                            }
                        }
                    }
                });

        text.addKeyListener(
                new KeyAdapter() {
                    public void keyPressed(KeyEvent event) {
                        if ((event.stateMask & SWT.COMMAND) != 0) { // for Mac
                            if ((event.stateMask & SWT.SHIFT) != 0) {
                                if (event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR) {
                                    runQuery();
                                    return;
                                }
                            }
                        }

                        if (event.keyCode == SWT.F5
                                || (event.stateMask & SWT.CTRL) != 0 && event.keyCode == 'e') {
                            runQuery();
                        } else if (event.keyCode == SWT.F6
                                || (event.stateMask & SWT.CTRL) != 0 && event.keyCode == 'l') {
                            runQueryPlan();
                        } else if (event.keyCode == SWT.F3) {
                            if ((event.stateMask & SWT.SHIFT) == 0) {
                                TextEditorFindReplaceMediator.findNext();
                            } else {
                                TextEditorFindReplaceMediator.findPrevious();
                            }
                        } else if ((event.stateMask & SWT.CTRL) != 0 && event.keyCode == ' ') {
                            contentAssistant.showPossibleCompletions();
                        } else if ((event.stateMask & SWT.CTRL) != 0 && event.keyCode == 'r') {
                            recentlyUsedSQLcontentAssistant.showPossibleCompletions();
                        } else if ((event.stateMask & SWT.CTRL) != 0
                                && (event.stateMask & SWT.SHIFT) == 0) {
                            if (event.keyCode == 'z' || event.keyCode == 'Z') {
                                undoManager.undo();
                            } else if (event.keyCode == 'y' || event.keyCode == 'Y') {
                                undoManager.redo();
                            }
                        } else if ((event.stateMask & SWT.CTRL) != 0
                                && (event.stateMask & SWT.SHIFT) != 0) {
                            if (event.keyCode == 'f' || event.keyCode == 'F') {
                                try {
                                    formatHandler.execute(null);
                                } catch (Exception ex) {
                                    CommonUITool.openErrorBox(ex.getMessage());
                                }
                            }
                        } else if ((event.stateMask & SWT.ALT) != 0 && event.keyCode == '/') {
                            try {
                                contentAssistHandler.execute(null);
                            } catch (Exception ex) {
                                CommonUITool.openErrorBox(ex.getMessage());
                            }
                        }

                        if (SQLContentAssistProcessor.isShowProposal(event.character)) {
                            contentAssistant.showPossibleCompletions();
                            useCompletions = true;
                        } else if ((event.character >= 'A' && event.character <= 'Z')
                                || (event.character >= 'a' && event.character <= 'z')) {
                            if (useCompletions) {
                                contentAssistant.showPossibleCompletions();
                            }
                            useCompletions = false;
                        } else if (event.character == ' '
                                || event.character == '\t'
                                || event.keyCode == SWT.KEYPAD_CR
                                || event.keyCode == SWT.CR
                                || event.keyCode == SWT.BS
                                || (text.getText().trim().length() < 1)) {
                            useCompletions = true;
                        }
                    }
                });

        TextEditorFindReplaceMediator editorDialogMediator = new TextEditorFindReplaceMediator();
        text.addFocusListener(editorDialogMediator);
    }

    private void createQueryCompareTab(CTabFolder tabFolder) {
        CTabItem item = new CTabItem(tabFolder, SWT.None | SWT.MULTI | SWT.V_SCROLL);
        item.setText(Messages.tabItemQueryCompare);

        Composite composite = new Composite(tabFolder, SWT.None);
        composite.setLayout(new GridLayout());
        item.setControl(composite);

        Composite topComposite = new Composite(composite, SWT.None);
        topComposite.setLayout(new FormLayout());
        topComposite.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, 25));

        Composite leftTopComposite = new Composite(topComposite, SWT.None);
        FormData leftTopData = new FormData();
        leftTopData.top = new FormAttachment(0, 0);
        leftTopData.bottom = new FormAttachment(100, 0);
        leftTopData.left = new FormAttachment(0, 0);
        leftTopData.right = new FormAttachment(50, 0);
        leftTopComposite.setLayoutData(leftTopData);
        leftTopComposite.setLayout(new GridLayout(2, false));

        Composite rightTopComposite = new Composite(topComposite, SWT.None);
        FormData rightTopData = new FormData();
        rightTopData.top = new FormAttachment(0, 0);
        rightTopData.bottom = new FormAttachment(100, 0);
        rightTopData.left = new FormAttachment(50, 0);
        rightTopData.right = new FormAttachment(100, 0);
        rightTopComposite.setLayoutData(rightTopData);
        rightTopComposite.setLayout(new GridLayout(5, false));

        Label leftLabel = new Label(leftTopComposite, SWT.None);
        leftLabel.setText(Messages.lblQuery);
        leftLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        queryRecordLeftCombo = new Combo(leftTopComposite, SWT.READ_ONLY);
        queryRecordLeftCombo.setLayoutData(CommonUITool.createGridData(1, 1, 200, -1));
        queryRecordLeftCombo.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        displayCompareQueryRecord();
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {}
                });

        Label rightLabel = new Label(rightTopComposite, SWT.None);
        rightLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        rightLabel.setText(Messages.lblQuery);

        queryRecordRightCombo = new Combo(rightTopComposite, SWT.READ_ONLY);
        queryRecordRightCombo.setLayoutData(CommonUITool.createGridData(1, 1, 200, -1));
        queryRecordRightCombo.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        displayCompareQueryRecord();
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {}
                });

        ToolBar leftToolBar = new ToolBar(rightTopComposite, SWT.None);
        leftToolBar.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        compareItem = new ToolItem(leftToolBar, SWT.None);
        compareItem.setImage(CommonUIPlugin.getImage("icons/action/refresh_tuning.png"));
        compareItem.setToolTipText(Messages.itemTooltipCompare);
        compareItem.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        displayCompareQueryRecord();
                    }
                });

        delQueryRecordItem = new ToolItem(leftToolBar, SWT.None);
        delQueryRecordItem.setImage(CommonUIPlugin.getImage("icons/queryplan/delete_query.gif"));
        delQueryRecordItem.setToolTipText(Messages.itemTooltipRemoveQuery);
        delQueryRecordItem.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        if (CommonUITool.openConfirmBox(Messages.confirmDeleteQueryPlanOnTuner)) {
                            removeQueryRecord();
                        }
                    }
                });

        Composite folderPlaceComposite = new Composite(rightTopComposite, SWT.None);
        folderPlaceComposite.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));

        ToolBar rightToolBar = new ToolBar(rightTopComposite, SWT.None);
        rightToolBar.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_END, 1, 1, -1, -1));

        multiRunItem = new ToolItem(rightToolBar, SWT.None);
        multiRunItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_multi_run.png"));
        multiRunItem.setToolTipText(Messages.itemTooltipRunAgain);
        multiRunItem.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        QueryRecord leftRecord = getSelectedLeftQueryRecord();
                        QueryRecord rightRecord = getSelectedRightQueryRecord();

                        if (leftRecord == null || rightRecord == null) {
                            CommonUITool.openErrorBox(Messages.errCompareQueryEmpty);
                            return;
                        }

                        runQueryPlanAgain(leftRecord.getQuery(), rightRecord.getQuery());
                    }
                });

        sqlModeItem = new ToolItem(rightToolBar, SWT.CHECK);
        sqlModeItem.setImage(CommonUIPlugin.getImage("icons/queryplan/sql_compare.gif"));
        sqlModeItem.setToolTipText(Messages.itemTooltipQuery);
        sqlModeItem.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        updateCompareDisplayModel(DISPLAY_SQL);
                    }
                });

        textModelItem = new ToolItem(rightToolBar, SWT.CHECK);
        textModelItem.setImage(
                CommonUIPlugin.getImage("icons/queryeditor/qe_explain_mode_raw.png"));
        textModelItem.setToolTipText(Messages.tooltip_qedit_explain_display_mode);
        textModelItem.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        updateCompareDisplayModel(DISPLAY_TEXT);
                    }
                });

        treeModelItem = new ToolItem(rightToolBar, SWT.CHECK);
        treeModelItem.setImage(
                CommonUIPlugin.getImage("icons/queryeditor/qe_explain_mode_tree.png"));
        treeModelItem.setToolTipText(Messages.tooltip_qedit_explain_display_mode);
        treeModelItem.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        updateCompareDisplayModel(DISPLAY_TREE);
                    }
                });

        graphModelItem = new ToolItem(rightToolBar, SWT.CHECK);
        graphModelItem.setImage(
                CommonUIPlugin.getImage("icons/queryeditor/qe_explain_mode_graph.png"));
        graphModelItem.setToolTipText(Messages.tooltip_qedit_explain_display_mode);
        graphModelItem.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        updateCompareDisplayModel(DISPLAY_GRAPH);
                    }
                });

        Composite tablesComposite = new Composite(composite, SWT.None);
        tablesComposite.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, 60));
        tablesComposite.setLayout(new FormLayout());

        /*Left composite*/
        Composite leftTableComposite = new Composite(tablesComposite, SWT.BORDER);
        leftTableComposite.setLayout(new GridLayout());
        FormData leftTableData = new FormData();
        leftTableData.top = new FormAttachment(0, 0);
        leftTableData.bottom = new FormAttachment(100, 0);
        leftTableData.left = new FormAttachment(0, 0);
        leftTableData.right = new FormAttachment(50, -2);
        leftTableComposite.setLayoutData(leftTableData);

        /*Right composite*/
        Composite rightTableComposite = new Composite(tablesComposite, SWT.BORDER);
        rightTableComposite.setLayout(new GridLayout());
        FormData rightTabelData = new FormData();
        rightTabelData.top = new FormAttachment(0, 0);
        rightTabelData.bottom = new FormAttachment(100, 0);
        rightTabelData.left = new FormAttachment(50, 2);
        rightTabelData.right = new FormAttachment(100, 0);
        rightTableComposite.setLayoutData(rightTabelData);

        compareLeftTableViewer = new TableViewer(leftTableComposite, SWT.FULL_SELECTION);
        compareLeftTableViewer
                .getTable()
                .setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        compareLeftTableViewer.getTable().setHeaderVisible(true);
        compareLeftTableViewer.getTable().setLinesVisible(false);

        compareLeftTableViewer.setContentProvider(new QueryPlanContentProvider());
        compareLeftTableViewer.setLabelProvider(
                new QueryPlanLabelPrivoder(compareLeftTableViewer, false));

        TableColumn fetchColumnLeft = new TableColumn(compareLeftTableViewer.getTable(), SWT.None);
        fetchColumnLeft.setText(Messages.columnFetches);
        fetchColumnLeft.setWidth(60);

        TableColumn dirtyColumnLeft = new TableColumn(compareLeftTableViewer.getTable(), SWT.None);
        dirtyColumnLeft.setText(Messages.columnDirties);
        dirtyColumnLeft.setWidth(60);

        TableColumn ioReadColumnLeft = new TableColumn(compareLeftTableViewer.getTable(), SWT.None);
        ioReadColumnLeft.setText(Messages.columnIORead);
        ioReadColumnLeft.setWidth(80);

        TableColumn ioWriteColumnLeft =
                new TableColumn(compareLeftTableViewer.getTable(), SWT.None);
        ioWriteColumnLeft.setText(Messages.columnIOWrite);
        ioWriteColumnLeft.setWidth(80);

        TableColumn costColumnLeft = new TableColumn(compareLeftTableViewer.getTable(), SWT.None);
        costColumnLeft.setText(Messages.columnCost);
        costColumnLeft.setWidth(60);

        compareRightTableViewer = new TableViewer(rightTableComposite, SWT.FULL_SELECTION);
        compareRightTableViewer
                .getTable()
                .setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        compareRightTableViewer.getTable().setHeaderVisible(true);
        compareRightTableViewer.getTable().setLinesVisible(false);

        compareRightTableViewer.setContentProvider(new QueryPlanContentProvider());
        compareRightTableViewer.setLabelProvider(
                new QueryPlanLabelPrivoder(compareRightTableViewer, false));

        TableColumn fetchColumnRight =
                new TableColumn(compareRightTableViewer.getTable(), SWT.None);
        fetchColumnRight.setText(Messages.columnFetches);
        fetchColumnRight.setWidth(60);

        TableColumn dirtyColumnRight =
                new TableColumn(compareRightTableViewer.getTable(), SWT.None);
        dirtyColumnRight.setText(Messages.columnDirties);
        dirtyColumnRight.setWidth(60);

        TableColumn ioReadColumnRight =
                new TableColumn(compareRightTableViewer.getTable(), SWT.None);
        ioReadColumnRight.setText(Messages.columnIORead);
        ioReadColumnRight.setWidth(80);

        TableColumn ioWriteColumnRight =
                new TableColumn(compareRightTableViewer.getTable(), SWT.None);
        ioWriteColumnRight.setText(Messages.columnIOWrite);
        ioWriteColumnRight.setWidth(80);

        TableColumn costColumnRight = new TableColumn(compareRightTableViewer.getTable(), SWT.None);
        costColumnRight.setText(Messages.columnCost);
        costColumnRight.setWidth(60);

        queryPlanCompareSashForm = new SashForm(composite, SWT.VERTICAL);
        queryPlanCompareSashForm.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        queryPlanCompareSashForm.setBackground(SASH_COLOR);
        queryPlanCompareSashForm.setLayout(new GridLayout());
        queryPlanCompareSashForm.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));

        Composite textMergeComposite = new Composite(queryPlanCompareSashForm, SWT.None);
        textMergeComposite.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        textMergeComposite.setLayout(new FillLayout());

        textMergeConfig = new CompareConfiguration();
        textMergeConfig.setProperty(CompareConfiguration.SHOW_PSEUDO_CONFLICTS, Boolean.FALSE);
        textMergeConfig.setProperty(CompareConfiguration.IGNORE_WHITESPACE, Boolean.TRUE);
        textMergeConfig.setLeftEditable(false);
        textMergeConfig.setRightEditable(false);
        textMergeViewer = new TextMergeViewer(textMergeComposite, SWT.BORDER, textMergeConfig);
        DiffNode queryDiffNode =
                new DiffNode(
                        null,
                        Differencer.CHANGE,
                        null,
                        new TextCompareInput(""),
                        new TextCompareInput(""));
        textMergeViewer.setInput(queryDiffNode);

        Composite compareQueryPlanComposite = new Composite(queryPlanCompareSashForm, SWT.None);
        compareQueryPlanComposite.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        compareQueryPlanComposite.setLayout(new FormLayout());

        DatabaseInfo databaseInfo = database == null ? null : database.getDatabaseInfo();

        leftComparePlanComposite =
                new QueryPlanComposite(compareQueryPlanComposite, SWT.BORDER, null, databaseInfo);
        FormData leftData = new FormData();
        leftData.top = new FormAttachment(0, 0);
        leftData.bottom = new FormAttachment(100, 0);
        leftData.left = new FormAttachment(0, 0);
        leftData.right = new FormAttachment(50, 0);
        leftComparePlanComposite.setLayoutData(leftData);

        rightComparePlanComposite =
                new QueryPlanComposite(compareQueryPlanComposite, SWT.BORDER, null, databaseInfo);
        FormData rightData = new FormData();
        rightData.top = new FormAttachment(0, 0);
        rightData.bottom = new FormAttachment(100, 0);
        rightData.left = new FormAttachment(50, 0);
        rightData.right = new FormAttachment(100, 0);
        rightComparePlanComposite.setLayoutData(rightData);

        queryPlanCompareSashForm.setWeights(new int[] {100, 0});
    }

    private void init() {
        List<QueryRecordProject> queryRecordProjectList = loadProjectList();
        initQueryProject(queryRecordProjectList, 0);
        if (this.query != null) {
            sqlTextViewer.getTextWidget().setText(query);
            runQueryPlan();
        }
    }

    /**
     * Load history list
     *
     * @param index
     */
    private void initQueryProject(List<QueryRecordProject> queryRecordProjectList, int index) {
        historyCombo.removeAll();

        QueryRecordProject selectProject = null;
        if (queryRecordProjectList != null && queryRecordProjectList.size() > 0) {
            String[] items = new String[queryRecordProjectList.size()];
            for (int i = 0; i < queryRecordProjectList.size(); i++) {
                QueryRecordProject queryRecordProject = queryRecordProjectList.get(i);
                items[i] = queryRecordProject.getName();

                if (i == index) {
                    selectProject = queryRecordProject;
                }
            }
            historyCombo.setItems(items);
            if (items.length > index) {
                historyCombo.select(index);
                //				infoText.setText(formater.format(queryRecordProjectList.get(
                //						index).getCreateDate()));
            }
        }
        /*If change project, clear the buffer data*/
        if (selectProject != null
                && queryRecordProject != null
                && !StringUtil.isEqual(selectProject.getName(), queryRecordProject.getName())) {

            clearBufferRecord();
        }

        queryRecordProject = selectProject;

        initQueryRecord(selectProject);
    }

    /** Clear buffer query record */
    private void clearBufferRecord() {
        lastRecord = null;
        currentRecord = null;
        displayQuery(null);
        displayQueryPlan(null);
        displayComparePlan(null, null);
        saveQueryItem.setEnabled(false);
    }

    private List<QueryRecordProject> loadProjectList() {
        List<QueryRecordProject> queryRecordProjectList =
                ApplicationPersistUtil.getInstance()
                        .getQueryRecordProject(database.getDatabaseInfo());

        Collections.sort(queryRecordProjectList, new QueryRecordListComparator());

        return queryRecordProjectList;
    }

    /** Add new query record list */
    private void addQueryRecordProject() {
        QueryRecordProject queryRecordProject = new QueryRecordProject();
        queryRecordProject.setCreateDate(new Date());
        queryRecordProject.setName(formater.format(queryRecordProject.getCreateDate()));

        AddQueryRecordProjectDialog dialog =
                new AddQueryRecordProjectDialog(
                        getShell(), queryRecordProject, true, database.getDatabaseInfo());

        if (dialog.open() == IDialogConstants.OK_ID) {
            queryRecordProject.setName(dialog.getName());

            ApplicationPersistUtil.getInstance()
                    .addQueryRecordProject(database.getDatabaseInfo(), queryRecordProject);
            ApplicationPersistUtil.getInstance().save();

            switchQueryRecordProject(queryRecordProject.getName());
        }
    }

    /** Save current query record */
    private void saveQueryRecord() {
        QueryRecordProject queryRecordProject = null;

        if (ApplicationPersistUtil.getInstance()
                        .getQueryRecordProject(database.getDatabaseInfo())
                        .size()
                == 0) {
            queryRecordProject = new QueryRecordProject();
            queryRecordProject.setCreateDate(new Date());
            queryRecordProject.setName(formater.format(queryRecordProject.getCreateDate()));

            ApplicationPersistUtil.getInstance()
                    .addQueryRecordProject(database.getDatabaseInfo(), queryRecordProject);
            ApplicationPersistUtil.getInstance().save();

            switchQueryRecordProject(queryRecordProject.getName());
        } else {
            queryRecordProject =
                    ApplicationPersistUtil.getInstance()
                            .findQueryRecordProject(
                                    database.getDatabaseInfo(), historyCombo.getText());
        }

        if (queryRecordProject == null) {
            CommonUITool.openErrorBox(Messages.errUnselectHistory);
            return;
        }

        if (currentRecord == null) {
            CommonUITool.openErrorBox(Messages.errCurrentQueryEmpty);
            return;
        }

        AddQueryRecordDialog dialog =
                new AddQueryRecordDialog(getShell(), currentRecord, true, queryRecordProject);
        if (dialog.open() == IDialogConstants.OK_ID) {
            currentRecord.setName(dialog.getName());
            queryRecordProject.addQueryRecord(currentRecord);

            ApplicationPersistUtil.getInstance()
                    .removeQueryRecordProject(database.getDatabaseInfo(), historyCombo.getText());
            ApplicationPersistUtil.getInstance()
                    .addQueryRecordProject(database.getDatabaseInfo(), queryRecordProject);
            ApplicationPersistUtil.getInstance().save();

            initQueryRecord(queryRecordProject);
            displayCompareQueryRecord();
        }
    }

    /** Remove selected query record */
    private void removeQueryRecord() {
        QueryRecordProject queryRecordProject =
                ApplicationPersistUtil.getInstance()
                        .findQueryRecordProject(database.getDatabaseInfo(), historyCombo.getText());

        if (queryRecordProject == null) {
            CommonUITool.openErrorBox(Messages.errUnselectHistory);
        }

        String recordName = queryRecordRightCombo.getText();
        queryRecordProject.removeQueryRecord(recordName);

        ApplicationPersistUtil.getInstance()
                .removeQueryRecordProject(database.getDatabaseInfo(), queryRecordProject.getName());
        ApplicationPersistUtil.getInstance()
                .addQueryRecordProject(database.getDatabaseInfo(), queryRecordProject);
        ApplicationPersistUtil.getInstance().save();

        initQueryRecord(queryRecordProject);
        if (queryRecordLeftCombo.getItemCount() == 0) {
            CommonUITool.openErrorBox(Messages.msgQueryTunerNotSavedProject);
            tabFolder.setSelection(0);
        }
    }

    /** Rename selected query record list */
    private void renameQueryRecordProject() {
        String name = historyCombo.getText();
        QueryRecordProject queryRecordProject =
                ApplicationPersistUtil.getInstance()
                        .findQueryRecordProject(database.getDatabaseInfo(), name);
        if (queryRecordProject != null) {
            AddQueryRecordProjectDialog dialog =
                    new AddQueryRecordProjectDialog(
                            getShell(), queryRecordProject, false, database.getDatabaseInfo());
            if (dialog.open() == IDialogConstants.OK_ID) {
                /*Remove old list*/
                ApplicationPersistUtil.getInstance()
                        .removeQueryRecordProject(database.getDatabaseInfo(), name);
                /*Add new list*/
                queryRecordProject.setName(dialog.getName());
                ApplicationPersistUtil.getInstance()
                        .addQueryRecordProject(database.getDatabaseInfo(), queryRecordProject);
                /*Save*/
                ApplicationPersistUtil.getInstance().save();

                switchQueryRecordProject(queryRecordProject.getName());
            }
        }
    }

    /** Change query record list */
    private void switchQueryRecordProject(String name) {
        List<QueryRecordProject> projectList = loadProjectList();

        int index = -1;
        if (projectList != null && projectList.size() > 0) {
            for (int i = 0; i < projectList.size(); i++) {
                QueryRecordProject temp = projectList.get(i);
                if (StringUtil.isEqual(name, temp.getName())) {
                    index = i;
                    break;
                }
            }
        }

        if (index >= 0) {
            initQueryProject(projectList, index);
        } else {
            initQueryProject(projectList, 0);
        }
    }

    /**
     * Load query record
     *
     * @param queryRecordProject
     * @param selectIndex
     */
    private void initQueryRecord(QueryRecordProject queryRecordProject) {
        String left = queryRecordLeftCombo.getText();
        String right = queryRecordRightCombo.getText();

        queryRecordLeftCombo.removeAll();
        queryRecordRightCombo.removeAll();

        if (queryRecordProject == null || queryRecordProject.getQueryRecordList().size() == 0) {
            return;
        }

        Collections.sort(queryRecordProject.getQueryRecordList(), new QueryRecordComparator());

        List<String> itemList = new ArrayList<String>();
        for (QueryRecord record : queryRecordProject.getQueryRecordList()) {
            itemList.add(record.getName());
        }

        String[] items = new String[itemList.size()];
        itemList.toArray(items);

        queryRecordLeftCombo.setItems(items);
        queryRecordRightCombo.setItems(items);

        int leftIndex = itemList.indexOf(left);
        if (leftIndex == -1) {
            leftIndex = 0;
        }
        queryRecordLeftCombo.select(leftIndex);

        int rightIndex = itemList.indexOf(right);
        if (rightIndex == -1) {
            rightIndex = 0;
        }
        queryRecordRightCombo.select(rightIndex);
    }

    /** Display compare query record */
    private void displayCompareQueryRecord() {
        QueryRecord leftRecord = getSelectedLeftQueryRecord();
        QueryRecord rightRecord = getSelectedRightQueryRecord();

        if (rightRecord == null || leftRecord == null) {
            CommonUITool.openErrorBox(Messages.errCompareQueryEmpty);
            return;
        }

        displayComparePlan(leftRecord, rightRecord);
    }

    /**
     * Get left selected history query record
     *
     * @return
     */
    private QueryRecord getSelectedLeftQueryRecord() {
        QueryRecordProject queryRecordProject =
                ApplicationPersistUtil.getInstance()
                        .findQueryRecordProject(database.getDatabaseInfo(), historyCombo.getText());

        if (queryRecordProject == null) {
            CommonUITool.openErrorBox(Messages.errUnselectHistory);
        }

        String historyName = queryRecordLeftCombo.getText();
        return queryRecordProject.findQueryRecord(historyName);
    }

    /**
     * Get selected history query record
     *
     * @return
     */
    private QueryRecord getSelectedRightQueryRecord() {
        QueryRecordProject queryRecordProject =
                ApplicationPersistUtil.getInstance()
                        .findQueryRecordProject(database.getDatabaseInfo(), historyCombo.getText());

        if (queryRecordProject == null) {
            CommonUITool.openErrorBox(Messages.errUnselectHistory);
        }

        String historyName = queryRecordRightCombo.getText();
        return queryRecordProject.findQueryRecord(historyName);
    }

    /**
     * Display query record for tuner tab
     *
     * @param queryRecord
     */
    private void displayQueryPlan(final QueryRecord queryRecord) {

        if (queryPlanComposite != null && !queryPlanComposite.isDisposed()) {
            queryPlanComposite.dispose();
        }
        queryPlanComposite = new Composite(queryPlanContainer, SWT.BORDER);
        queryPlanComposite.setLayout(new GridLayout());

        ToolBar queryPlanToolBar = new ToolBar(queryPlanComposite, SWT.None);
        queryPlanToolBar.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        hideItem = new ToolItem(queryPlanToolBar, SWT.CHECK);
        hideItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/qe_panel_down.png"));
        hideItem.setToolTipText(Messages.tooltip_qedit_explain_display_mode);

        dispModeTextItem = new ToolItem(queryPlanToolBar, SWT.CHECK);
        dispModeTextItem.setImage(
                CommonUIPlugin.getImage("icons/queryeditor/qe_explain_mode_raw.png"));
        dispModeTextItem.setToolTipText(Messages.tooltip_qedit_explain_display_mode);

        dispModeTreeItem = new ToolItem(queryPlanToolBar, SWT.CHECK);
        dispModeTreeItem.setImage(
                CommonUIPlugin.getImage("icons/queryeditor/qe_explain_mode_tree.png"));
        dispModeTreeItem.setToolTipText(Messages.tooltip_qedit_explain_display_mode);

        dispModeGraphItem = new ToolItem(queryPlanToolBar, SWT.CHECK);
        dispModeGraphItem.setImage(
                CommonUIPlugin.getImage("icons/queryeditor/qe_explain_mode_graph.png"));
        dispModeGraphItem.setToolTipText(Messages.tooltip_qedit_explain_display_mode);

        TableViewer statisticsViewer = new TableViewer(queryPlanComposite, SWT.FULL_SELECTION);
        statisticsViewer
                .getTable()
                .setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, 50));
        statisticsViewer.getTable().setHeaderVisible(true);
        statisticsViewer.getTable().setLinesVisible(false);

        TableColumn itemColumn = new TableColumn(statisticsViewer.getTable(), SWT.None);
        itemColumn.setText(Messages.columnItem);
        itemColumn.setWidth(60);

        TableColumn fetchColumn = new TableColumn(statisticsViewer.getTable(), SWT.None);
        fetchColumn.setText(Messages.columnFetches);
        fetchColumn.setWidth(60);

        TableColumn dirtyColumn = new TableColumn(statisticsViewer.getTable(), SWT.None);
        dirtyColumn.setText(Messages.columnDirties);
        dirtyColumn.setWidth(60);

        TableColumn ioReadColumn = new TableColumn(statisticsViewer.getTable(), SWT.None);
        ioReadColumn.setText(Messages.columnIORead);
        ioReadColumn.setWidth(80);

        TableColumn ioWriteColumn = new TableColumn(statisticsViewer.getTable(), SWT.None);
        ioWriteColumn.setText(Messages.columnIOWrite);
        ioWriteColumn.setWidth(80);

        TableColumn costColumn = new TableColumn(statisticsViewer.getTable(), SWT.None);
        costColumn.setText(Messages.columnCost);
        costColumn.setWidth(60);

        statisticsViewer.setContentProvider(new QueryPlanContentProvider());
        statisticsViewer.setLabelProvider(new QueryPlanLabelPrivoder(statisticsViewer, true));

        List<QueryRecord> recordList = new ArrayList<QueryRecord>();
        if (queryRecord != null) {
            recordList.add(queryRecord);
        }
        if (lastRecord != null) {
            recordList.add(lastRecord);
        }
        statisticsViewer.setInput(recordList);

        final SashForm queryPlanSashForm = new SashForm(queryPlanComposite, SWT.VERTICAL);
        queryPlanSashForm.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        queryPlanSashForm.setBackground(SASH_COLOR);
        queryPlanSashForm.setLayout(new GridLayout());
        queryPlanSashForm.SASH_WIDTH = 2;

        DatabaseInfo databaseInfo = database == null ? null : database.getDatabaseInfo();

        final QueryPlanComposite nowPlanComposite =
                new QueryPlanComposite(
                        queryPlanSashForm,
                        SWT.None,
                        queryRecord == null ? null : queryRecord.getQueryPlan(),
                        databaseInfo);
        final QueryPlanComposite beforePlanComposite =
                new QueryPlanComposite(
                        queryPlanSashForm,
                        SWT.None,
                        lastRecord == null ? null : lastRecord.getQueryPlan(),
                        databaseInfo);

        queryPlanSashForm.setWeights(new int[] {500, 500});

        hideItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        isHiden = !isHiden;
                        performHideOperation(queryPlanSashForm, isHiden);
                    }
                });

        dispModeTextItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        updateTunerPlanDisplayModel(
                                nowPlanComposite, beforePlanComposite, DISPLAY_TEXT);
                    }
                });

        dispModeTreeItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        updateTunerPlanDisplayModel(
                                nowPlanComposite, beforePlanComposite, DISPLAY_TREE);
                    }
                });

        dispModeGraphItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        updateTunerPlanDisplayModel(
                                nowPlanComposite, beforePlanComposite, DISPLAY_GRAPH);
                    }
                });

        performHideOperation(queryPlanSashForm, isHiden);
        updateTunerPlanDisplayModel(nowPlanComposite, beforePlanComposite, tunerDisplayModel);

        queryPlanContainer.layout();
    }

    private void performHideOperation(SashForm queryPlanSashForm, boolean isHide) {
        if (isHide) {
            hideItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/qe_panel_up.png"));
            queryPlanSashForm.setWeights(new int[] {1000, 0});
        } else {
            hideItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/qe_panel_down.png"));
            queryPlanSashForm.setWeights(new int[] {500, 500});
        }
    }

    /**
     * Update tuner toolbar
     *
     * @param type
     */
    private void updateTunerPlanDisplayModel(
            QueryPlanComposite nowPlanComposite, QueryPlanComposite beforePlanComposite, int type) {
        if (type == DISPLAY_TEXT) {
            tunerDisplayModel = DISPLAY_TEXT;

            nowPlanComposite.useDisplayMode(PLAN_DISPLAY_MODE.TEXT.getInt());
            beforePlanComposite.useDisplayMode(PLAN_DISPLAY_MODE.TEXT.getInt());

            dispModeTextItem.setSelection(true);
            dispModeTreeItem.setSelection(false);
            dispModeGraphItem.setSelection(false);
        }

        if (type == DISPLAY_TREE) {
            tunerDisplayModel = DISPLAY_TREE;

            nowPlanComposite.useDisplayMode(PLAN_DISPLAY_MODE.TREE.getInt());
            beforePlanComposite.useDisplayMode(PLAN_DISPLAY_MODE.TREE.getInt());

            dispModeTextItem.setSelection(false);
            dispModeTreeItem.setSelection(true);
            dispModeGraphItem.setSelection(false);
        }

        if (type == DISPLAY_GRAPH) {
            tunerDisplayModel = DISPLAY_GRAPH;

            nowPlanComposite.useDisplayMode(PLAN_DISPLAY_MODE.GRAPH.getInt());
            beforePlanComposite.useDisplayMode(PLAN_DISPLAY_MODE.GRAPH.getInt());

            dispModeTextItem.setSelection(false);
            dispModeTreeItem.setSelection(false);
            dispModeGraphItem.setSelection(true);
        }
    }

    /**
     * Update compare display model
     *
     * @param model
     */
    private void updateCompareDisplayModel(int model) {
        if (model == DISPLAY_TEXT) {
            // compareDisplayModel = DISPLAY_TEXT;
            queryPlanCompareSashForm.setWeights(new int[] {0, 100});

            leftComparePlanComposite.useDisplayMode(PLAN_DISPLAY_MODE.TEXT.getInt());
            rightComparePlanComposite.useDisplayMode(PLAN_DISPLAY_MODE.TEXT.getInt());

            sqlModeItem.setSelection(false);
            textModelItem.setSelection(true);
            treeModelItem.setSelection(false);
            graphModelItem.setSelection(false);
        }

        if (model == DISPLAY_TREE) {
            // compareDisplayModel = DISPLAY_TREE;
            queryPlanCompareSashForm.setWeights(new int[] {0, 100});

            leftComparePlanComposite.useDisplayMode(PLAN_DISPLAY_MODE.TREE.getInt());
            rightComparePlanComposite.useDisplayMode(PLAN_DISPLAY_MODE.TREE.getInt());

            sqlModeItem.setSelection(false);
            textModelItem.setSelection(false);
            treeModelItem.setSelection(true);
            graphModelItem.setSelection(false);
        }

        if (model == DISPLAY_GRAPH) {
            // compareDisplayModel = DISPLAY_GRAPH;
            queryPlanCompareSashForm.setWeights(new int[] {0, 100});

            leftComparePlanComposite.useDisplayMode(PLAN_DISPLAY_MODE.GRAPH.getInt());
            rightComparePlanComposite.useDisplayMode(PLAN_DISPLAY_MODE.GRAPH.getInt());

            sqlModeItem.setSelection(false);
            textModelItem.setSelection(false);
            treeModelItem.setSelection(false);
            graphModelItem.setSelection(true);
        }

        if (model == DISPLAY_SQL) {
            // compareDisplayModel = DISPLAY_SQL;
            queryPlanCompareSashForm.setWeights(new int[] {100, 0});

            sqlModeItem.setSelection(true);
            textModelItem.setSelection(false);
            treeModelItem.setSelection(false);
            graphModelItem.setSelection(false);
        }
    }

    /**
     * Display compare query plan
     *
     * @param leftRecord
     * @param rightRecord
     */
    private void displayComparePlan(QueryRecord leftRecord, QueryRecord rightRecord) {
        String leftQuery = "", rightQuery = "";

        if (leftRecord != null) {
            leftQuery = leftRecord.getQuery();
            textMergeConfig.setLeftLabel(leftRecord.getName());
            List<QueryRecord> list = new ArrayList<QueryRecord>();
            list.add(leftRecord);
            compareLeftTableViewer.setInput(list);
            compareLeftTableViewer.refresh();
            leftComparePlanComposite.setQueryRecord(leftRecord.getQueryPlan());
        } else {
            textMergeConfig.setLeftLabel("");
            compareLeftTableViewer.setInput(new ArrayList<QueryRecord>());
            compareLeftTableViewer.refresh();
            leftComparePlanComposite.setQueryRecord(null);
        }

        if (rightRecord != null) {
            rightQuery = rightRecord.getQuery();
            textMergeConfig.setRightLabel(rightRecord.getName());
            List<QueryRecord> list = new ArrayList<QueryRecord>();
            list.add(rightRecord);
            compareRightTableViewer.setInput(list);
            compareRightTableViewer.refresh();
            rightComparePlanComposite.setQueryRecord(rightRecord.getQueryPlan());
        } else {
            textMergeConfig.setRightLabel("");
            compareRightTableViewer.setInput(new ArrayList<QueryRecord>());
            compareRightTableViewer.refresh();
            rightComparePlanComposite.setQueryRecord(null);
        }

        DiffNode queryDiffNode =
                new DiffNode(
                        null,
                        Differencer.CHANGE,
                        null,
                        new TextCompareInput(leftQuery),
                        new TextCompareInput(rightQuery));
        textMergeViewer.setInput(queryDiffNode);
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell()
                .setText(
                        Messages.bind(
                                Messages.titleQueryTuner,
                                new String[] {
                                    database.getDatabaseInfo().getDbName(),
                                    database.getDatabaseInfo().getBrokerIP(),
                                    database.getDatabaseInfo().getBrokerPort()
                                }));
        if (GeneralPreference.isMaxQueryTunerWindow()) {
            getShell().setMaximized(true);
        } else {
            getShell().setSize(900, 600);
            CommonUITool.centerShell(getShell());
        }
    }

    public boolean close() {
        boolean isMax = getShell().getMaximized();
        GeneralPreference.setMaxQueryTunerWindow(isMax);

        return super.close();
    }

    protected int getShellStyle() {
        return SWT.DIALOG_TRIM | SWT.MODELESS | SWT.MAX | SWT.MIN | SWT.RESIZE;
    }

    protected Control createButtonBar(Composite parent) {
        return parent;
    }

    /**
     * get the selected query
     *
     * @return query
     */
    private String getSelectedQueries() {
        if (sqlTextViewer.getTextWidget().getSelectionCount() > 0) {
            return sqlTextViewer.getTextWidget().getSelectionText();
        }

        return sqlTextViewer.getTextWidget().getText();
    }

    public void textChanged(TextEvent event) {
        if (StringUtil.isEmpty(sqlTextViewer.getTextWidget().getText())) {
            runItem.setEnabled(false);
            runPlanItem.setEnabled(false);
        } else {
            runItem.setEnabled(true);
            runPlanItem.setEnabled(true);
        }
        /*For bug TOOLS-3010*/
        saveQueryItem.setEnabled(false);
    }

    /**
     * Display the query data
     *
     * @param queryRecord
     */
    private void displayQuery(final QueryRecord queryRecord) {
        if (queryResultComposite != null && !queryResultComposite.isDisposed()) {
            queryResultComposite.dispose();
        }

        queryResultComposite = new Composite(queryResultContainer, SWT.None);
        queryResultComposite.setLayout(new FillLayout());

        ViewForm viewForm = new ViewForm(queryResultComposite, SWT.NONE);
        viewForm.setLayout(new FillLayout());

        final SashForm tableLogSash = new SashForm(viewForm, SWT.VERTICAL);
        tableLogSash.SASH_WIDTH = SASH_WIDTH;
        tableLogSash.setBackground(BACK_COLOR);

        /*Create table view*/
        final TableViewer resultTableViewer =
                new TableViewer(
                        tableLogSash,
                        SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
        TableCursor cursor = new TableCursor(resultTableViewer.getTable(), SWT.NONE);
        TableSelectSupport tableSelectSupport =
                new TableSelectSupport(resultTableViewer.getTable(), cursor);
        String fontString = QueryOptions.getFontString(database.getDatabaseInfo().getServerInfo());
        Font tmpFont = ResourceManager.getFont(fontString);
        if (tmpFont == null) {
            String[] fontData = QueryOptions.getDefaultFont();
            tmpFont =
                    ResourceManager.getFont(
                            fontData[0],
                            Integer.valueOf(fontData[1]),
                            Integer.valueOf(fontData[2]));
        }
        Font font = tmpFont;
        resultTableViewer.getTable().setFont(font);
        int[] fontColor = QueryOptions.getFontColor(database.getDatabaseInfo().getServerInfo());
        Color color = ResourceManager.getColor(fontColor[0], fontColor[1], fontColor[2]);
        resultTableViewer.getTable().setForeground(color);
        /*Set font and foreground*/
        tableSelectSupport.getTableCursor().setFont(font);
        tableSelectSupport.getTableCursor().setForeground(color);

        final SashForm logSash = new SashForm(tableLogSash, SWT.HORIZONTAL);
        logSash.SASH_WIDTH = SASH_WIDTH;
        logSash.setBackground(BACK_COLOR);
        logSash.setLayout(new FillLayout());

        StyledText messagesArea =
                new StyledText(
                        logSash,
                        SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY | SWT.WRAP);
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

        tableLogSash.setWeights(new int[] {8, 2});
        viewForm.setContent(tableLogSash);

        resultTableViewer.getTable().setHeaderVisible(true);
        resultTableViewer.getTable().setLinesVisible(true);

        if (queryRecord != null) {
            QueryResultLabelProvider labelProvider = new QueryResultLabelProvider(queryRecord);
            QueryResultContentProvider contentProvider = new QueryResultContentProvider();
            resultTableViewer.setLabelProvider(labelProvider);
            resultTableViewer.setContentProvider(contentProvider);

            createResultColumn(resultTableViewer, queryRecord);
            resultTableViewer.setInput(queryRecord.getPageData());

            setQueryMessage(messagesArea, queryRecord);
        }

        CommonUITool.packTable(resultTableViewer.getTable(), 10, 150);
        queryResultContainer.layout();
    }

    private void createResultColumn(final TableViewer tableViewer, final QueryRecord queryRecord) {
        if (queryRecord != null) {
            TableColumn[] tblColumn =
                    new TableColumn
                            [(queryRecord.getColumnInfoList() == null
                                            ? 0
                                            : queryRecord.getColumnInfoList().size())
                                    + 1];
            tblColumn[0] = new TableColumn(tableViewer.getTable(), SWT.NONE);
            tblColumn[0].setText("NO");
            tblColumn[0].setWidth(40);
            if (queryRecord.getColumnInfoList() == null) {
                return;
            }
            final Map<String, ColumnComparator> colComparatorMap =
                    new HashMap<String, ColumnComparator>();
            for (int j = 0; j < queryRecord.getColumnInfoList().size(); j++) {
                tblColumn[j + 1] = new TableColumn(tableViewer.getTable(), SWT.NONE);
                ColumnInfo columnInfo = (ColumnInfo) queryRecord.getColumnInfoList().get(j);
                String name = columnInfo.getName();
                String type = columnInfo.getType();
                tblColumn[j + 1].setText(name);
                tblColumn[j + 1].setToolTipText(columnInfo.getComleteType());
                tblColumn[j + 1].setData(columnInfo);
                tblColumn[j + 1].pack();
                ColumnComparator comparator =
                        new ColumnComparator(columnInfo.getIndex(), type, true);
                if (colComparatorMap != null) {
                    colComparatorMap.put(columnInfo.getIndex(), comparator);
                }
                tblColumn[j + 1].addSelectionListener(
                        new SelectionListener() {
                            @SuppressWarnings("unchecked")
                            public void widgetSelected(SelectionEvent event) {
                                TableColumn column = (TableColumn) event.widget;
                                if (column == null
                                        || column.getText() == null
                                        || column.getText().trim().length() == 0) {
                                    return;
                                }

                                TableColumn sortedColumn = tableViewer.getTable().getSortColumn();
                                int width = column.getWidth();

                                ColumnInfo columnInfo = (ColumnInfo) column.getData();
                                ColumnComparator comparator =
                                        colComparatorMap.get(columnInfo.getIndex());
                                tableViewer.getTable().setSortColumn(column);
                                tableViewer
                                        .getTable()
                                        .setSortDirection(comparator.isAsc() ? SWT.UP : SWT.DOWN);
                                Collections.sort(queryRecord.getPageData(), comparator);
                                comparator.setAsc(!comparator.isAsc());
                                column.pack();
                                if (column.equals(sortedColumn)) {
                                    column.setWidth(width);
                                } else {
                                    column.setWidth(width + 25);
                                }
                                tableViewer.refresh();
                            }

                            public void widgetDefaultSelected(SelectionEvent event) {}
                        });
            }
        }
    }

    /**
     * Set query result message
     *
     * @param messageText
     * @param queryRecord
     */
    private void setQueryMessage(StyledText messageText, QueryRecord queryRecord) {
        StringBuilder sb = new StringBuilder();

        /*Query message*/
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(3);
        double elapsedTime = (queryRecord.getStopTime() - queryRecord.getStartTime()) * 0.001;
        String elapsedTimeStr = nf.format(elapsedTime);
        if (elapsedTime < 0.001) {
            elapsedTimeStr = "0.000";
        }
        /*elapsed time*/
        sb.append(Messages.queryWithoutSeq)
                .append("[ ")
                .append(elapsedTimeStr)
                .append(Messages.second);
        /*total row*/
        if (queryRecord.getQueryInfo() != null) {
            sb.append(" , ")
                    .append(Messages.totalRows)
                    .append(" : ")
                    .append(queryRecord.getQueryInfo().getTotalRs())
                    .append(" ]")
                    .append(StringUtil.NEWLINE);
        }

        //		sb.append(QueryUtil.SPLIT_LINE_FOR_QUERY_RESULT);
        //		sb.append("Statistics");
        //		sb.append(StringUtil.NEWLINE);
        //		sb.append(queryRecord.getStatisticsWithRawText().trim());
        //		sb.append(StringUtil.NEWLINE);

        //		String indexInfo = queryRecord.getIndexesOnQueryWithText();
        //		if (StringUtil.isNotEmpty(indexInfo)) {
        //			sb.append(QueryUtil.SPLIT_LINE_FOR_QUERY_RESULT);
        //			sb.append("Indexes");
        //			sb.append(StringUtil.NEWLINE);
        //			sb.append(indexInfo);
        //			sb.append(StringUtil.NEWLINE);
        //		}

        sb.append(QueryUtil.SPLIT_LINE_FOR_QUERY_RESULT);
        sb.append(StringUtil.NEWLINE);
        sb.append(queryRecord.getQuery());
        sb.append(StringUtil.NEWLINE);

        String result = sb.toString();
        messageText.setText(result);

        int splitterLen = QueryUtil.SPLIT_LINE_FOR_QUERY_RESULT.length();
        {
            StyleRange eachStyle = new StyleRange();
            eachStyle.start = 0;
            eachStyle.length = result.indexOf(QueryUtil.SPLIT_LINE_FOR_QUERY_RESULT);
            eachStyle.fontStyle = SWT.NORMAL;
            eachStyle.foreground = ResourceManager.getColor(SWT.COLOR_BLUE);
            messageText.setStyleRange(eachStyle);
        }

        for (int sp = 0; ; ) {
            StyleRange eachStyle = new StyleRange();
            sp = result.indexOf(QueryUtil.SPLIT_LINE_FOR_QUERY_RESULT, sp);
            if (sp == -1) {
                break;
            }
            int ep = result.indexOf(StringUtil.NEWLINE, sp);
            if (ep != -1) {
                splitterLen = ep - sp;
            }

            eachStyle.start = sp;
            eachStyle.length = splitterLen;
            eachStyle.fontStyle = SWT.NORMAL;
            eachStyle.foreground = ResourceManager.getColor(SWT.COLOR_BLUE);
            messageText.setStyleRange(eachStyle);
            sp += splitterLen;
        }
    }

    public void queryChanged(final QueryEvent queryEvent) {
        Display.getDefault()
                .syncExec(
                        new Runnable() {
                            public void run() {
                                if (QueryEvent.QUERY_FINISH_ALL == queryEvent.getEvent()) {
                                    saveQueryItem.setEnabled(true);
                                } else {
                                    saveQueryItem.setEnabled(false);
                                }
                            }
                        });
    }

    public CubridDatabase getDatabase() {
        return database;
    }

    public DatabaseInfo getDatabaseInfo() {
        return database == null ? null : database.getDatabaseInfo();
    }

    public ServerInfo getServerInfo() {
        return getDatabaseInfo() == null ? null : getDatabaseInfo().getServerInfo();
    }
}

class QueryRecordComparator implements Comparator<QueryRecord> {
    public int compare(QueryRecord o1, QueryRecord o2) {
        if (o1 == null || o1.getCreateDate() == null || o2 == null || o2.getCreateDate() == null) {
            return 0;
        }

        if (o1.getCreateDate().getTime() < o2.getCreateDate().getTime()) {
            return 1;
        } else if (o1.getCreateDate().getTime() > o2.getCreateDate().getTime()) {
            return -1;
        }
        return 0;
    }
}

class QueryRecordListComparator implements Comparator<QueryRecordProject> {
    public int compare(QueryRecordProject o1, QueryRecordProject o2) {
        if (o1 == null || o1.getCreateDate() == null || o2 == null || o2.getCreateDate() == null) {
            return 0;
        }

        if (o1.getCreateDate().getTime() < o2.getCreateDate().getTime()) {
            return 1;
        } else if (o1.getCreateDate().getTime() > o2.getCreateDate().getTime()) {
            return -1;
        }
        return 0;
    }
}
