/*
 * Copyright (C) 2008 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.query.control;

import com.cubrid.common.ui.cubrid.table.control.FKTableViewerContentProvider;
import com.cubrid.common.ui.cubrid.table.control.FKTableViewerLabelProvider;
import com.cubrid.common.ui.cubrid.table.control.IndexTableViewerContentProvider;
import com.cubrid.common.ui.cubrid.table.control.IndexTableViewerLabelProvider;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.query.format.SqlFormattingStrategy;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.TableContentProvider;
import com.cubrid.common.ui.spi.TableLabelProvider;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.progress.CommonTaskJobExec;
import com.cubrid.common.ui.spi.progress.TaskJob;
import com.cubrid.common.ui.spi.progress.TaskJobExecutor;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.SQLGenerateUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

/**
 * The table or view information composite
 *
 * @author Kevin.Wang
 * @version 1.0 - Apr 13, 2012 created by Kevin.Wang
 */
public class ObjectInfoComposite extends Composite implements ISubTabSelection {
    private static final String NEWLINE = com.cubrid.common.core.util.StringUtil.NEWLINE;

    /*Selected Object*/
    private DefaultSchemaNode schemaNode;
    private CTabFolder objInfoFolder;

    /*Tool buttons*/
    private Button selectButton;
    private Button selectColumnButton;
    private Button insertButton;
    private Button updateButton;
    private Button deleteButton;

    /*Quick changing buttons*/
    private Button dataTabButton;
    private Button ddlTabButton;
    private Button columnTabButton;
    private Button indexTabButton;

    /*Data components*/
    private Table demoDataTable;
    private StyledText sqlText;
    private TableViewer tableColTableViewer;
    private TableViewer viewColTableViewer;
    private TableViewer fkTableViewer;
    private TableViewer indexTableViewer;

    /*Get data job*/
    private GetInfoDataTask getInfoDataTask;
    /*Mark is table*/
    private boolean isTable = false;

    public ObjectInfoComposite(Composite parent, int style, DefaultSchemaNode schemaNode) {
        super(parent, style);

        GridLayout tLayout = new GridLayout(1, false);
        tLayout.verticalSpacing = 0;
        tLayout.horizontalSpacing = 0;
        tLayout.marginWidth = 0;
        tLayout.marginHeight = 0;
        setLayout(tLayout);

        this.schemaNode = schemaNode;

        /*Judge the schemaNode is table*/
        if (ActionSupportUtil.isSupportSingleSelection(
                schemaNode,
                new String[] {
                    NodeType.USER_TABLE,
                    NodeType.SYSTEM_TABLE,
                    NodeType.USER_PARTITIONED_TABLE_FOLDER
                })) {
            isTable = true;
        }
    }

    public String getTargetName() {
        if (schemaNode == null) {
            return null;
        }

        return schemaNode.getName();
    }

    public void init() {

        /*Tool bar composite*/
        Composite toolBarComposite = new Composite(this, SWT.NONE);
        RowLayout rowLayout = new RowLayout();
        toolBarComposite.setLayout(rowLayout);
        toolBarComposite.setLayoutData(CommonUITool.createGridData(-1, -1, -1, -1));

        dataTabButton = new Button(toolBarComposite, SWT.None);
        dataTabButton.setText(Messages.tabTitleData);
        dataTabButton.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        if (objInfoFolder == null) {
                            return;
                        }

                        objInfoFolder.setSelection(0);
                    }
                });

        ddlTabButton = new Button(toolBarComposite, SWT.None);
        ddlTabButton.setText(Messages.tabTitleDDL);
        ddlTabButton.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        if (objInfoFolder == null) {
                            return;
                        }

                        objInfoFolder.setSelection(1);
                    }
                });

        columnTabButton = new Button(toolBarComposite, SWT.None);
        columnTabButton.setText(Messages.tabTitleColumn);
        columnTabButton.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        if (objInfoFolder == null) {
                            return;
                        }

                        objInfoFolder.setSelection(2);
                    }
                });

        indexTabButton = new Button(toolBarComposite, SWT.None);
        indexTabButton.setText(Messages.tabTitleIndex);
        indexTabButton.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        if (objInfoFolder == null) {
                            return;
                        }

                        objInfoFolder.setSelection(3);
                    }
                });

        new Label(toolBarComposite, SWT.None).setText("  ");

        /*Select * button*/
        selectButton = new Button(toolBarComposite, SWT.None);
        selectButton.setText(Messages.txtSelect);
        selectButton.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        processSelectAction();
                    }
                });

        /*Select column button*/
        selectColumnButton = new Button(toolBarComposite, SWT.None);
        selectColumnButton.setText(Messages.txtSelectColumn);
        selectColumnButton.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        processSelectColumnAction();
                    }
                });

        /*Insert Button*/
        insertButton = new Button(toolBarComposite, SWT.None);
        insertButton.setText(Messages.txtInsert);
        insertButton.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        processInsertAction();
                    }
                });

        /*Update button*/
        updateButton = new Button(toolBarComposite, SWT.None);
        updateButton.setText(Messages.txtUpdate);
        updateButton.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        processUpdateAction();
                    }
                });

        /*Delete button*/
        deleteButton = new Button(toolBarComposite, SWT.None);
        deleteButton.setText(Messages.txtDelete);
        deleteButton.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        processDeleteAction();
                    }
                });

        /*Database object information*/
        objInfoFolder = new CTabFolder(this, SWT.BOTTOM);
        objInfoFolder.setSimple(false);
        objInfoFolder.setUnselectedImageVisible(true);
        objInfoFolder.setUnselectedCloseVisible(true);
        objInfoFolder.setSelectionBackground(CombinedQueryEditorComposite.BACK_COLOR);
        objInfoFolder.setSelectionForeground(ResourceManager.getColor(SWT.COLOR_BLACK));
        objInfoFolder.setLayout(new GridLayout(1, true));
        objInfoFolder.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_BOTH, -1, -1, -1, -1));

        /*Demo data tab item*/
        initDataTabItem();
        /*DDL tab item*/
        initDDLTabItem();
        /*Column tab item*/
        initColumnTabItem();
        /*If is table, create index tab item*/
        if (isTable) {
            initIndexTabItem();
        }

        /*Initial the data*/
        TaskJobExecutor taskExec =
                new CommonTaskJobExec() {
                    public IStatus exec(IProgressMonitor monitor) {
                        IStatus status = super.exec(monitor);
                        if (Status.CANCEL_STATUS == status) {
                            return status;
                        }
                        return Status.OK_STATUS;
                    }

                    public void done(IJobChangeEvent event) {
                        getInfoDataTask = null;
                    }
                };

        GetInfoDataTask getInfoDataTask =
                new GetInfoDataTask(Messages.getInfoJobName, this, schemaNode, isTable);
        taskExec.addTask(getInfoDataTask);

        /*Get data job*/
        TaskJob job =
                new TaskJob(Messages.bind(Messages.getInfoJobName, schemaNode.getName()), taskExec);
        job.setPriority(Job.LONG);
        job.setUser(false);
        job.schedule();
    }

    /** Initial demo data table setting */
    private void initDataTabItem() {
        CTabItem dataTabItem = new CTabItem(objInfoFolder, SWT.NONE);
        dataTabItem.setText(Messages.titleData);
        dataTabItem.setShowClose(false);

        Composite composite = new Composite(objInfoFolder, SWT.None);
        dataTabItem.setControl(composite);
        objInfoFolder.setSelection(dataTabItem);
        composite.setLayout(new FillLayout());

        demoDataTable =
                new Table(composite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        demoDataTable.setHeaderVisible(true);
        demoDataTable.setLinesVisible(true);
        CommonUITool.hackForYosemite(demoDataTable);
    }

    /** Initial ddl tab item */
    private void initDDLTabItem() {
        CTabItem ddlTabItem = new CTabItem(objInfoFolder, SWT.NONE);
        ddlTabItem.setText(Messages.titleDDL);
        ddlTabItem.setShowClose(false);

        Composite composite = new Composite(objInfoFolder, SWT.NONE);
        ddlTabItem.setControl(composite);
        composite.setLayout(new FillLayout());

        sqlText =
                new StyledText(
                        composite,
                        SWT.V_SCROLL
                                | SWT.READ_ONLY
                                | SWT.H_SCROLL
                                | SWT.BORDER
                                | SWT.WRAP
                                | SWT.MULTI);
        /*For bug TOOLS-996*/
        CommonUITool.registerCopyPasteContextMenu(sqlText, false);
    }

    /** Initial column tab item */
    private void initColumnTabItem() {
        CTabItem columnTabItem = new CTabItem(objInfoFolder, SWT.NONE);
        columnTabItem.setText(Messages.titleColumn);
        columnTabItem.setShowClose(false);

        Composite composite = new Composite(objInfoFolder, SWT.None);
        columnTabItem.setControl(composite);
        composite.setLayout(new FillLayout());

        if (isTable) {
            initTableColumn(composite);
        } else {
            initViewColumn(composite);
        }
    }

    /**
     * Initial Table column setting
     *
     * @param composite
     */
    private void initTableColumn(Composite composite) {
        tableColTableViewer = new TableViewer(composite, SWT.FULL_SELECTION | SWT.BORDER);

        Table columnsTable = tableColTableViewer.getTable();
        columnsTable.setLinesVisible(true);
        columnsTable.setHeaderVisible(true);

        // PK
        final TableColumn pkColumn = new TableColumn(columnsTable, SWT.NONE);
        pkColumn.setAlignment(SWT.CENTER);
        pkColumn.setWidth(30);
        pkColumn.setText(Messages.tblColumnPK);

        // NAME
        final TableColumn nameColumn = new TableColumn(columnsTable, SWT.NONE);
        nameColumn.setWidth(90);
        nameColumn.setText(Messages.tblColumnName);

        // DATATYPE
        final TableColumn dataTypeColumn = new TableColumn(columnsTable, SWT.NONE);
        dataTypeColumn.setWidth(120);
        dataTypeColumn.setText(Messages.tblColumnDataType);

        // DEFAULT
        final TableColumn defaultColumn = new TableColumn(columnsTable, SWT.NONE);
        defaultColumn.setWidth(98);
        defaultColumn.setText(Messages.tblColumnDefault);

        // AUTO INCREMENT
        final TableColumn autoIncrTableColumn = new TableColumn(columnsTable, SWT.NONE);
        autoIncrTableColumn.setAlignment(SWT.CENTER);
        autoIncrTableColumn.setWidth(100);
        autoIncrTableColumn.setText(Messages.tblColumnAutoIncr);

        // NOT NULL
        final TableColumn notNullColumn = new TableColumn(columnsTable, SWT.NONE);
        notNullColumn.setWidth(70);
        notNullColumn.setText(Messages.tblColumnNotNull);
        notNullColumn.setAlignment(SWT.CENTER);

        // UK
        final TableColumn uniqueColumn = new TableColumn(columnsTable, SWT.NONE);
        uniqueColumn.setWidth(70);
        uniqueColumn.setText(Messages.tblColumnUnique);
        uniqueColumn.setAlignment(SWT.CENTER);

        // SHARED
        final TableColumn sharedColumn = new TableColumn(columnsTable, SWT.NONE);
        sharedColumn.setWidth(60);
        sharedColumn.setResizable(false);
        sharedColumn.setText(Messages.tblColumnShared);
        sharedColumn.setAlignment(SWT.CENTER);

        // INHERIT
        final TableColumn inheritColumn = new TableColumn(columnsTable, SWT.NONE);
        inheritColumn.setAlignment(SWT.CENTER);
        inheritColumn.setWidth(90);
        inheritColumn.setResizable(false);
        inheritColumn.setText(Messages.tblColumnInherit);

        // CLASS
        final TableColumn classColumn = new TableColumn(columnsTable, SWT.NONE);
        classColumn.setWidth(90);
        classColumn.setResizable(false);
        classColumn.setText(Messages.tblColumnClass);
        classColumn.setAlignment(SWT.CENTER);
    }

    /**
     * Initial view column setting
     *
     * @param composite
     */
    private void initViewColumn(Composite composite) {
        viewColTableViewer =
                new TableViewer(
                        composite, SWT.V_SCROLL | SWT.BORDER | SWT.H_SCROLL | SWT.FULL_SELECTION);
        viewColTableViewer.setContentProvider(new TableContentProvider());
        viewColTableViewer.setLabelProvider(new TableLabelProvider());

        viewColTableViewer.getTable().setLinesVisible(true);
        viewColTableViewer.getTable().setHeaderVisible(true);

        final TableColumn nameColumn = new TableColumn(viewColTableViewer.getTable(), SWT.NONE);
        nameColumn.setText(Messages.tblColViewName);
        nameColumn.pack();

        final TableColumn dataTypeColumn = new TableColumn(viewColTableViewer.getTable(), SWT.NONE);
        dataTypeColumn.setText(Messages.tblColViewDataType);
        dataTypeColumn.pack();

        final TableColumn defaultColumn = new TableColumn(viewColTableViewer.getTable(), SWT.NONE);
        defaultColumn.setText(Messages.tblColViewDefaultType);
        defaultColumn.pack();

        final TableColumn defaultValueColumn =
                new TableColumn(viewColTableViewer.getTable(), SWT.NONE);
        defaultValueColumn.setText(Messages.tblColViewDefaultValue);
        defaultValueColumn.pack();
    }

    /** Initial index tab index */
    private void initIndexTabItem() {
        CubridDatabase database = schemaNode.getDatabase();

        CTabItem indexTabItem = new CTabItem(objInfoFolder, SWT.NONE);
        indexTabItem.setText(Messages.titleIndex);
        indexTabItem.setShowClose(false);

        Composite composite = new Composite(objInfoFolder, SWT.None);
        indexTabItem.setControl(composite);
        composite.setLayout(new FillLayout());

        final SashForm sashForm = new SashForm(composite, SWT.VERTICAL);
        sashForm.setBackground(CombinedQueryEditorComposite.BACK_COLOR);

        /*Index table composite*/
        Composite indexComposite = new Composite(sashForm, SWT.None);
        indexComposite.setLayout(new GridLayout());
        /*FK table composite*/
        Composite fkComposite = new Composite(sashForm, SWT.None);
        fkComposite.setLayout(new GridLayout());
        /*Set the sashform*/
        sashForm.setWeights(new int[] {1, 1});

        // create index table view
        final Label indexLabel = new Label(indexComposite, SWT.NONE);
        indexLabel.setText(Messages.lblIndexes);

        indexTableViewer =
                new TableViewer(indexComposite, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
        Table indexTable = indexTableViewer.getTable();
        indexTable.setLinesVisible(true);
        indexTable.setHeaderVisible(true);
        indexTable.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));

        TableColumn tblCol = new TableColumn(indexTable, SWT.NONE);
        tblCol.setWidth(150);
        tblCol.setText(Messages.tblColumnIndexName);

        TableColumn indexNameColumn = new TableColumn(indexTable, SWT.NONE);
        indexNameColumn.setWidth(78);
        indexNameColumn.setText(Messages.tblColumnIndexType);

        TableColumn indexTypeColumn = new TableColumn(indexTable, SWT.NONE);
        indexTypeColumn.setWidth(218);
        indexTypeColumn.setText(Messages.tblColumnOnColumns);

        TableColumn ruleColumn = new TableColumn(indexTable, SWT.NONE);
        ruleColumn.setWidth(282);
        ruleColumn.setText(Messages.tblColumnIndexRule);

        IndexTableViewerContentProvider indexContentProvider =
                new IndexTableViewerContentProvider();
        IndexTableViewerLabelProvider indexLabelProvider = new IndexTableViewerLabelProvider();
        indexTableViewer.setContentProvider(indexContentProvider);
        indexTableViewer.setLabelProvider(indexLabelProvider);

        // create the fk table viewer
        final Label fkLabel = new Label(fkComposite, SWT.NONE);
        fkLabel.setText(Messages.lblFK);

        fkTableViewer = new TableViewer(fkComposite, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
        Table fkTable = fkTableViewer.getTable();

        fkTable.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        fkTable.setLinesVisible(true);
        fkTable.setHeaderVisible(true);

        TableColumn fkNameColumn = new TableColumn(fkTable, SWT.NONE);
        fkNameColumn.setWidth(100);
        fkNameColumn.setText(Messages.tblColumnFK);

        TableColumn fkColumnName = new TableColumn(fkTable, SWT.NONE);
        fkColumnName.setWidth(119);
        fkColumnName.setText(Messages.tblColumnColumnName);

        TableColumn fTableColumn = new TableColumn(fkTable, SWT.NONE);
        fTableColumn.setWidth(93);
        fTableColumn.setText(Messages.tblColumnForeignTable);

        TableColumn fkColumnNameColumn = new TableColumn(fkTable, SWT.NONE);
        fkColumnNameColumn.setWidth(143);
        fkColumnNameColumn.setText(Messages.tblColumnForeignColumnName);

        TableColumn updateRuletblColumn = new TableColumn(fkTable, SWT.NONE);
        updateRuletblColumn.setWidth(84);
        updateRuletblColumn.setText(Messages.tblColumnUpdateRule);

        TableColumn deleteRuleColumn = new TableColumn(fkTable, SWT.NONE);
        deleteRuleColumn.setWidth(86);
        deleteRuleColumn.setText(Messages.tblColumnDeleteRule);

        TableColumn cacheColumn = new TableColumn(fkTable, SWT.NONE);
        cacheColumn.setWidth(100);
        cacheColumn.setText(Messages.tblColumnCacheColumn);

        fkTableViewer.setContentProvider(new FKTableViewerContentProvider());
        fkTableViewer.setLabelProvider(new FKTableViewerLabelProvider(database.getDatabaseInfo()));
    }

    /** Process select action */
    private void processSelectAction() {
        String res = SQLGenerateUtils.getSelectSQLSimple(schemaNode);
        appendToEditor(new SqlFormattingStrategy().format(res).trim() + NEWLINE + NEWLINE);
    }

    /** Process select columns action */
    private void processSelectColumnAction() {
        String res = SQLGenerateUtils.getSelectSQL(schemaNode);
        appendToEditor(new SqlFormattingStrategy().format(res).trim() + NEWLINE + NEWLINE);
    }

    /** Process insert action */
    private void processInsertAction() {
        String res = SQLGenerateUtils.getInsertSQL(schemaNode);
        appendToEditor(new SqlFormattingStrategy().format(res).trim() + NEWLINE + NEWLINE);
    }

    /** Process update action */
    private void processUpdateAction() {
        String res = SQLGenerateUtils.getUpdateSQL(schemaNode);
        appendToEditor(new SqlFormattingStrategy().format(res).trim() + NEWLINE + NEWLINE);
    }

    /** Process delete action */
    private void processDeleteAction() {
        String res = SQLGenerateUtils.getDeleteSQL(schemaNode);
        appendToEditor(new SqlFormattingStrategy().format(res).trim() + NEWLINE + NEWLINE);
    }

    /**
     * Append the sql to the sqlEditor and select it
     *
     * @param sql
     */
    private void appendToEditor(String sql) {
        StringBuffer queries = new StringBuffer();
        IEditorPart part =
                PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow()
                        .getActivePage()
                        .getActiveEditor();

        if (part instanceof QueryEditorPart) {
            QueryEditorPart editorPart = (QueryEditorPart) part;
            SQLEditorComposite sqlEditor =
                    editorPart.getCombinedQueryComposite().getSqlEditorComp();
            queries.append(sqlEditor.getAllQueries());

            int index = queries.length();

            if (index > 0) {
                String last = queries.substring(index - 1);
                if (!(NEWLINE.equals(last) || "\n".equals(last))) {
                    queries.append(NEWLINE);
                    index += NEWLINE.length();
                }
            }

            queries.append(sql);
            sqlEditor.setQueries(queries.toString());
            sqlEditor.forcusCursor(index, sql.length());
        }
    }

    /** Perform dispose */
    public void dispose() {
        if (getInfoDataTask != null) {
            getInfoDataTask.cancel();
            getInfoDataTask = null;
        }
        super.dispose();
    }

    /**
     * Get the demo data table
     *
     * @return demoDataTable - Table
     */
    public Table getDemoDataTable() {
        return demoDataTable;
    }

    /**
     * Get the DDL text
     *
     * @return scriptText - Text
     */
    public StyledText getSqlText() {
        return sqlText;
    }

    /**
     * Get Table column table viewer
     *
     * @return columnTableViewer - TableViewer
     */
    public TableViewer getTableColViewer() {
        return tableColTableViewer;
    }

    /**
     * Get the view column table viewer
     *
     * @return
     */
    public TableViewer getViewColTableViewer() {
        return viewColTableViewer;
    }

    /**
     * Get the FK table viewer
     *
     * @return fkTableViewer - TableViewer
     */
    public TableViewer getFkTableViewer() {
        return fkTableViewer;
    }

    /**
     * Get the index Table Viewer
     *
     * @return indexTableViewer - TableViewer
     */
    public TableViewer getIndexTableViewer() {
        return indexTableViewer;
    }

    /**
     * Get the DefaultSchemaNode
     *
     * @return
     */
    public DefaultSchemaNode getSchemaNode() {
        return schemaNode;
    }

    /**
     * Select the sub tab by the tab index
     *
     * @param subTabIndex
     */
    public void select(int subTabIndex) {
        if (objInfoFolder == null) {
            return;
        }

        int total = objInfoFolder.getItemCount();
        if (subTabIndex >= total) {
            return;
        }

        objInfoFolder.setSelection(subTabIndex);
    }
}
