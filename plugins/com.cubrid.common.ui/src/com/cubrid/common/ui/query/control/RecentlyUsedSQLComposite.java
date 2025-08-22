package com.cubrid.common.ui.query.control;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.persist.RecentlyUsedSQLDetailPersistUtils;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.text.Collator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;

/**
 * Display recently used SQL on the query result in the Query Editor
 *
 * @author fulei
 * @version 1.0 - 2012. 04. 27 created by fulei
 */
public class RecentlyUsedSQLComposite extends Composite {

    public static final String ID = RecentlyUsedSQLComposite.class.getName();

    private static final int SASH_WIDTH = 2;
    private final CTabFolder resultTabFolder;
    private final QueryEditorPart editor;
    private CTabFolder recentlyUsedSQLTabFolder;
    private CTabItem recentlyUsedSQLTabItem;

    private TableViewer sqlHistoryTable;

    private StyledText logMessageArea;

    private HistorySQLLabelProvider provider;

    public void disposeAll() {
        if (!recentlyUsedSQLTabFolder.isDisposed()) {
            recentlyUsedSQLTabFolder.dispose();
        }
        if (!recentlyUsedSQLTabItem.isDisposed()) {
            recentlyUsedSQLTabItem.dispose();
        }
    }
    /**
     * The constructor
     *
     * @param parent
     * @param style
     * @param queryEditorPart
     */
    public RecentlyUsedSQLComposite(CTabFolder parent, int style, QueryEditorPart queryEditorPart) {
        super(parent, style);
        resultTabFolder = parent;
        this.editor = queryEditorPart;

        GridLayout tLayout = new GridLayout(1, true);
        tLayout.verticalSpacing = 0;
        tLayout.horizontalSpacing = 0;
        tLayout.marginWidth = 0;
        tLayout.marginHeight = 0;

        setLayout(tLayout);
    }

    /** Create the SQL history composite */
    public void initialize() {

        Composite toolBarComposite = new Composite(this, SWT.NONE);
        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.verticalSpacing = 0;
        gridLayout.horizontalSpacing = 10;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        gridLayout.numColumns = 2;
        toolBarComposite.setLayout(gridLayout);
        toolBarComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        ToolBar delHistoryToolBar = new ToolBar(toolBarComposite, SWT.FLAT | SWT.RIGHT);
        delHistoryToolBar.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));

        ToolItem delHistory = new ToolItem(delHistoryToolBar, SWT.PUSH);
        delHistory.setImage(CommonUIPlugin.getImage("icons/action/table_record_delete.png"));
        delHistory.setDisabledImage(
                CommonUIPlugin.getImage("icons/action/table_record_delete_disabled.png"));
        delHistory.setToolTipText(Messages.tooltip_qedit_sql_history_delete);
        delHistory.setText(Messages.btn_qedit_sql_history_delete);
        delHistory.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (sqlHistoryTable.getTable().getSelectionIndices().length == 0) {
                            MessageDialog.openError(
                                    PlatformUI.getWorkbench().getDisplay().getActiveShell(),
                                    Messages.error,
                                    Messages.sql_history_delete_error);
                            return;
                        }
                        MessageBox messageBox =
                                new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
                        messageBox.setText(Messages.tooltip_qedit_sql_history_delete);
                        messageBox.setMessage(Messages.sql_history_delete_message);
                        // remove data ,both view and model
                        int buttonID = messageBox.open();
                        if (buttonID == SWT.YES) {
                            deleteHistory();
                        }
                    }
                });

        // help messages
        Label helpMsg = new Label(toolBarComposite, SWT.None);
        helpMsg.setText(Messages.recentlyUsedSQLHelp);
        helpMsg.setLayoutData(new GridData(SWT.TRAIL, SWT.CENTER, true, false));

        // create the query result tab folder
        recentlyUsedSQLTabFolder = new CTabFolder(this, SWT.BOTTOM);
        recentlyUsedSQLTabFolder.setSimple(false);
        recentlyUsedSQLTabFolder.setUnselectedImageVisible(true);
        recentlyUsedSQLTabFolder.setUnselectedCloseVisible(true);
        recentlyUsedSQLTabFolder.setSelectionBackground(CombinedQueryEditorComposite.BACK_COLOR);
        recentlyUsedSQLTabFolder.setSelectionForeground(ResourceManager.getColor(SWT.COLOR_BLACK));
        recentlyUsedSQLTabFolder.setLayout(new GridLayout(1, true));
        recentlyUsedSQLTabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
        // TabContextMenuManager tabContextMenuManager = new
        // TabContextMenuManager(recentlyUsedSQLTabFolder);
        // tabContextMenuManager.createContextMenu();

        recentlyUsedSQLTabItem = new CTabItem(resultTabFolder, SWT.NONE);
        recentlyUsedSQLTabItem.setText(Messages.qedit_sql_history_folder);
        recentlyUsedSQLTabItem.setControl(this);
        recentlyUsedSQLTabItem.setShowClose(false);

        final SashForm bottomSash = new SashForm(recentlyUsedSQLTabFolder, SWT.VERTICAL);
        bottomSash.SASH_WIDTH = SASH_WIDTH;
        bottomSash.setBackground(CombinedQueryEditorComposite.BACK_COLOR);

        createHistoryTable(bottomSash);

        SashForm tailSash = new SashForm(bottomSash, SWT.HORIZONTAL);
        tailSash.SASH_WIDTH = SASH_WIDTH;
        tailSash.setBackground(CombinedQueryEditorComposite.BACK_COLOR);

        logMessageArea =
                new StyledText(tailSash, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY);
        CommonUITool.registerCopyPasteContextMenu(logMessageArea, false);

        bottomSash.setWeights(new int[] {80, 20});

        logMessageArea.setToolTipText(Messages.tooltipHowToExpandLogPane);
        logMessageArea.addFocusListener(
                new FocusListener() {
                    public void focusLost(FocusEvent e) {
                        bottomSash.setWeights(new int[] {80, 20});
                    }

                    public void focusGained(FocusEvent e) {
                        bottomSash.setWeights(new int[] {20, 80});
                    }
                });

        CTabItem tabItem = new CTabItem(recentlyUsedSQLTabFolder, SWT.NONE);
        tabItem.setText(Messages.qedit_sql_history);
        tabItem.setControl(bottomSash);
        tabItem.setShowClose(false);

        recentlyUsedSQLTabFolder.setSelection(tabItem);
    }

    /**
     * createHistoryTable
     *
     * @param bottomSash
     */
    public void createHistoryTable(SashForm bottomSash) {
        sqlHistoryTable =
                new TableViewer(
                        bottomSash, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
        sqlHistoryTable.getTable().setHeaderVisible(true);
        sqlHistoryTable.getTable().setLinesVisible(true);

        //		final TableViewerColumn columnNO = new TableViewerColumn(
        //				sqlHistoryTable, SWT.CENTER);
        //		columnNO.getColumn().setWidth(40);
        //		columnNO.getColumn().setText("NO");

        final TableViewerColumn columnRunTime = new TableViewerColumn(sqlHistoryTable, SWT.LEFT);
        columnRunTime.getColumn().setWidth(155);
        columnRunTime.getColumn().setText(Messages.recentlyUsedSQLColumnRunTime);

        columnRunTime
                .getColumn()
                .addSelectionListener(
                        new SelectionAdapter() {
                            boolean asc = true;

                            @Override
                            public void widgetSelected(SelectionEvent e) {
                                sqlHistoryTable.setSorter(
                                        asc ? Sorter.EXECUTETIME_ASC : Sorter.EXECUTETIME_DESC);
                                sqlHistoryTable
                                        .getTable()
                                        .setSortDirection(asc ? SWT.DOWN : SWT.UP);
                                asc = !asc;
                            }
                        });

        final TableViewerColumn columnSQL = new TableViewerColumn(sqlHistoryTable, SWT.LEFT);
        columnSQL.getColumn().setWidth(250);
        columnSQL.getColumn().setText(Messages.recentlyUsedSQLColumn);

        columnSQL
                .getColumn()
                .addSelectionListener(
                        new SelectionAdapter() {
                            boolean asc = true;

                            @Override
                            public void widgetSelected(SelectionEvent e) {
                                sqlHistoryTable.setSorter(asc ? Sorter.SQL_ASC : Sorter.SQL_DEC);
                                sqlHistoryTable
                                        .getTable()
                                        .setSortDirection(asc ? SWT.DOWN : SWT.UP);
                                asc = !asc;
                            }
                        });

        final TableViewerColumn columnElapseTime = new TableViewerColumn(sqlHistoryTable, SWT.LEFT);
        columnElapseTime.getColumn().setWidth(100);
        columnElapseTime.getColumn().setText(Messages.recentlyUsedSQLColumnElapseTime);

        columnElapseTime
                .getColumn()
                .addSelectionListener(
                        new SelectionAdapter() {
                            boolean asc = true;

                            @Override
                            public void widgetSelected(SelectionEvent e) {
                                sqlHistoryTable.setSorter(
                                        asc ? Sorter.ELAPSETIME_ASC : Sorter.ELAPSETIME_DESC);
                                sqlHistoryTable
                                        .getTable()
                                        .setSortDirection(asc ? SWT.DOWN : SWT.UP);
                                asc = !asc;
                            }
                        });

        final TableViewerColumn columnInfo = new TableViewerColumn(sqlHistoryTable, SWT.LEFT);
        columnInfo.getColumn().setWidth(300);
        columnInfo.getColumn().setText(Messages.recentlyUsedSQLColumnLOG);

        columnInfo
                .getColumn()
                .addSelectionListener(
                        new SelectionAdapter() {
                            boolean asc = true;

                            @Override
                            public void widgetSelected(SelectionEvent e) {
                                sqlHistoryTable.setSorter(asc ? Sorter.LOG_ASC : Sorter.LOG_DESC);
                                sqlHistoryTable
                                        .getTable()
                                        .setSortDirection(asc ? SWT.DOWN : SWT.UP);
                                asc = !asc;
                            }
                        });

        sqlHistoryTable.setContentProvider(new HistorySQLContentProvider());
        provider = new HistorySQLLabelProvider();
        sqlHistoryTable.setLabelProvider(provider);
        sqlHistoryTable.addSelectionChangedListener(
                new ISelectionChangedListener() {
                    public void selectionChanged(final SelectionChangedEvent event) {
                        SQLHistoryDetail history =
                                (SQLHistoryDetail)
                                        sqlHistoryTable.getElementAt(
                                                sqlHistoryTable.getTable().getSelectionIndex());
                        if (history != null) {
                            logMessageArea.setText(
                                    history.getExecuteInfo()
                                            + StringUtil.NEWLINE
                                            + history.getSql());
                        }
                    }
                });

        sqlHistoryTable.addDoubleClickListener(
                new IDoubleClickListener() {
                    public void doubleClick(DoubleClickEvent event) {
                        IStructuredSelection selection =
                                (IStructuredSelection) event.getSelection();
                        SQLHistoryDetail history = (SQLHistoryDetail) selection.getFirstElement();
                        logMessageArea.setText(
                                history.getExecuteInfo()
                                        + StringUtil.NEWLINE
                                        + StringUtil.NEWLINE
                                        + history.getSql());

                        int start = editor.getAllQueries().length();
                        editor.setQuery(StringUtil.NEWLINE + history.getSql(), true, false, false);

                        int end = start + history.getSql().length();
                        // "/n" take a postion ,so both start and end should + 1
                        editor.setSelection(start + 1, end + 1);
                    }
                });
    }

    @SuppressWarnings("all")
    public void deleteHistory() {
        List<Integer> deleteIndex = new ArrayList<Integer>();
        for (int i = 0; i < sqlHistoryTable.getTable().getSelectionIndices().length; i++) {
            deleteIndex.add(sqlHistoryTable.getTable().getSelectionIndices()[i]);
        }
        List<SQLHistoryDetail> deleteList = new ArrayList<SQLHistoryDetail>();
        for (int i = 0; i < deleteIndex.size(); i++) {
            SQLHistoryDetail history =
                    (SQLHistoryDetail) sqlHistoryTable.getElementAt(deleteIndex.get(i));
            deleteList.add(history);
        }

        RecentlyUsedSQLDetailPersistUtils.remove(editor.getSelectedDatabase(), deleteList);
        refreshRecentlyUsedSQLList();

        if (((List) sqlHistoryTable.getInput()).size() != 0) {
            sqlHistoryTable.getTable().setSelection(0);
            SQLHistoryDetail history = (SQLHistoryDetail) sqlHistoryTable.getElementAt(0);
            if (history != null) {
                logMessageArea.setText(
                        history.getExecuteInfo()
                                + StringUtil.NEWLINE
                                + StringUtil.NEWLINE
                                + history.getSql());
            } else {
                logMessageArea.setText("");
            }
        } else {
            logMessageArea.setText("");
        }

        //		MessageDialog.openInformation(
        //				PlatformUI.getWorkbench().getDisplay().getActiveShell(),
        //				Messages.info, Messages.sql_history_delete_success);
    }
    /** @author fulei */
    static class HistorySQLContentProvider implements IStructuredContentProvider {

        /**
         * getElements
         *
         * @param inputElement Object
         * @return Object[]
         */
        @SuppressWarnings("unchecked")
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof List) {
                List<SQLHistoryDetail> list = (List<SQLHistoryDetail>) inputElement;
                SQLHistoryDetail[] nodeArr = new SQLHistoryDetail[list.size()];
                return list.toArray(nodeArr);
            }

            return new Object[] {};
        }

        /** dispose */
        public void dispose() {
            // do nothing
        }

        /**
         * inputChanged
         *
         * @param viewer Viewer
         * @param oldInput Object
         * @param newInput Object
         */
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            // do nothing
        }
    }

    /**
     * table viewer sorter
     *
     * @author fulei
     */
    static class Sorter extends ViewerSorter {
        Collator comparator = Collator.getInstance(Locale.getDefault());
        private static final int SQL = 1;
        private static final int ELAPSETIME = 2;
        private static final int EXECUTETIME = 3;
        private static final int LOG = 4;

        public static final Sorter SQL_ASC = new Sorter(SQL);
        public static final Sorter SQL_DEC = new Sorter(-SQL);
        public static final Sorter ELAPSETIME_ASC = new Sorter(ELAPSETIME);
        public static final Sorter ELAPSETIME_DESC = new Sorter(-ELAPSETIME);
        public static final Sorter EXECUTETIME_ASC = new Sorter(EXECUTETIME);
        public static final Sorter EXECUTETIME_DESC = new Sorter(-EXECUTETIME);
        public static final Sorter LOG_ASC = new Sorter(LOG);
        public static final Sorter LOG_DESC = new Sorter(-LOG);

        private int sortType;

        private Sorter(int sortType) {
            this.sortType = sortType;
        }

        public int compare(Viewer viewer, Object e1, Object e2) {
            SQLHistoryDetail d1 = (SQLHistoryDetail) e1;
            SQLHistoryDetail d2 = (SQLHistoryDetail) e2;
            float time1 = Float.valueOf(d1.getElapseTime());
            float time2 = Float.valueOf(d2.getElapseTime());
            switch (sortType) {
                case SQL:
                    return comparator.compare(d1.getSql(), d2.getSql());
                case -SQL:
                    return comparator.compare(d2.getSql(), d1.getSql());
                case EXECUTETIME:
                    return comparator.compare(d1.getExecuteTime(), d2.getExecuteTime());
                case -EXECUTETIME:
                    return comparator.compare(d2.getExecuteTime(), d1.getExecuteTime());
                case LOG:
                    return comparator.compare(d1.getExecuteInfo(), d2.getExecuteInfo());
                case -LOG:
                    return comparator.compare(d2.getExecuteInfo(), d1.getExecuteInfo());
                case ELAPSETIME:
                    if (time1 == time2) {
                        return 0;
                    } else {
                        return time1 > time2 ? 1 : -1;
                    }
                case -ELAPSETIME:
                    if (time1 == time2) {
                        return 0;
                    } else {
                        return time1 < time2 ? 1 : -1;
                    }
                default:
                    return 0;
            }
        }
    }

    /** fillRecentlyUsedSQLList */
    public void fillRecentlyUsedSQLList() {
        if (editor.isConnected()) {
            RecentlyUsedSQLDetailPersistUtils.load(editor.getSelectedDatabase());
            sqlHistoryTable.setInput(
                    RecentlyUsedSQLDetailPersistUtils.getLog(editor.getSelectedDatabase()));
        } else {
            sqlHistoryTable.setInput(new LinkedList<SQLHistoryDetail>());
        }
    }

    public void refreshRecentlyUsedSQLList() {
        if (editor.isConnected()) {
            RecentlyUsedSQLDetailPersistUtils.load(editor.getSelectedDatabase());
            sqlHistoryTable.setInput(
                    RecentlyUsedSQLDetailPersistUtils.getLog(editor.getSelectedDatabase()));
        } else {
            sqlHistoryTable.setInput(new LinkedList<SQLHistoryDetail>());
        }
        sqlHistoryTable.setSelection(sqlHistoryTable.getSelection());
    }
    /** @author fulei */
    static class HistorySQLLabelProvider extends LabelProvider implements ITableLabelProvider {

        public HistorySQLLabelProvider() {
            super();
        }

        /**
         * getColumnImage
         *
         * @param element Object
         * @param columnIndex int
         * @return Image
         */
        public final Image getColumnImage(Object element, int columnIndex) {

            return null;
        }
        /**
         * getColumnText
         *
         * @param element Object
         * @param columnIndex int
         * @return String
         */
        public String getColumnText(Object element, int columnIndex) {

            if (element instanceof SQLHistoryDetail) {
                SQLHistoryDetail history = (SQLHistoryDetail) element;

                if (columnIndex == 5) {
                    return String.valueOf(history.getIndex());
                } else if (columnIndex == 0) {
                    return history.getExecuteTime();
                } else if (columnIndex == 1) {
                    return history.getSql();
                } else if (columnIndex == 2) {
                    return history.getElapseTime();
                } else if (columnIndex == 3) {
                    return history.getExecuteInfo();
                }
            }
            return null;
        }
    }
}
