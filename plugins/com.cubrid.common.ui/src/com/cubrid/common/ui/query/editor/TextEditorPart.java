/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.query.editor;

import com.cubrid.common.core.util.CubridUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.cubrid.table.dialog.PstmtParameter;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.action.CopyAction;
import com.cubrid.common.ui.query.action.CutAction;
import com.cubrid.common.ui.query.action.FindReplaceAction;
import com.cubrid.common.ui.query.action.PasteAction;
import com.cubrid.common.ui.query.action.QueryOpenAction;
import com.cubrid.common.ui.query.action.RedoAction;
import com.cubrid.common.ui.query.action.UndoAction;
import com.cubrid.common.ui.query.control.CombinedQueryEditorComposite;
import com.cubrid.common.ui.query.control.EditorToolBar;
import com.cubrid.common.ui.query.control.QueryExecuter;
import com.cubrid.common.ui.query.control.SQLEditorComposite;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.part.CubridEditorPart;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.TabContextMenuManager;
import com.cubrid.cubridmanager.core.common.jdbc.DBConnection;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.slf4j.Logger;

/**
 * This query editor part is responsible to execute sql
 *
 * @author pangqiren 2009-3-2
 */
public class TextEditorPart extends CubridEditorPart
        implements ICopiableFromTable,
                ISaveablePart2,
                ITextListener,
                IDatabaseProvider,
                IInformationWindowNotifier {

    private static final Logger LOGGER = LogUtil.getLogger(TextEditorPart.class);
    public static final String ID = TextEditorPart.class.getName();

    public static final String SQL_FORMAT = "com.cubrid.query.command.format";
    public static final String CONTENT_ASSIST_PROPOSALS =
            "com.cubrid.query.command.contentAssist.proposals";

    /*The composite*/
    private Composite topComposite;
    private CombinedQueryEditorComposite combinedQueryComposite;
    private CTabFolder combinedQueryEditortabFolder;
    /*Connection*/
    private DBConnection connection = new DBConnection();;
    public CUBRIDPreparedStatementProxy pStmt;
    /*Editor toolbar*/
    private EditorToolBar qeToolBar;
    private boolean isActive;
    /*Query thread*/
    private Thread queryThread;
    private int line;
    private boolean isRunning = false;

    private QueryExecuter result = null;
    private boolean isIncludeOidInfo = false;
    protected boolean showStatistical = false;

    private Label noticeMessageArea;

    private GridData hiddenGridDataForNotice;

    private boolean collectExecStats = false;

    private boolean willClose = false;

    /*Record the editor index*/
    private static int lastEditorIndex = 1;
    private int currentEditorIndex = 1;
    private int sqlEditorCounter = 1;

    private ToolTip tooltip;

        public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        super.init(site, input);
        currentEditorIndex = lastEditorIndex++;
        this.setSite(site);
        this.setInput(input);
        this.setPartName(input.getName());
        this.setTitleToolTip(input.getToolTipText());
        if (input.getImageDescriptor() != null) {
            this.setTitleImage(input.getImageDescriptor().createImage());
        }
        hookRetragetActions();
    }

    public void showToolTip(
            Control baseControl, ToolItem toolItem, String title, String message, int timeoutSec) {
        if (tooltip == null) {
            tooltip = new ToolTip(Display.getCurrent().getActiveShell(), SWT.None);
            tooltip.setAutoHide(true);
        } else {
            tooltip.setVisible(false);
        }

        Point pt = baseControl.toDisplay(topComposite.getLocation());
        pt.x += toolItem.getBounds().x;
        pt.y += toolItem.getBounds().height;
        tooltip.setText(title);
        tooltip.setMessage(message);
        tooltip.setLocation(pt);
        tooltip.setVisible(true);

        if (timeoutSec > 0) {
            final Long eventTime = new Long(System.currentTimeMillis());
            tooltip.setData(eventTime);
            tooltip.getDisplay()
                    .timerExec(
                            timeoutSec * 1000,
                            new Runnable() {
                                public void run() {
                                    if (tooltip != null && tooltip.getData() instanceof Long) {
                                        Long eventTimeTmp = (Long) tooltip.getData();
                                        if (eventTimeTmp.longValue() == eventTime.longValue()) {
                                            tooltip.setVisible(false);
                                        }
                                    }
                                }
                            });
        }
    }

    public void showToolTip(Control baseControl, ToolItem toolItem, String title, String message) {
        showToolTip(baseControl, toolItem, title, message, 0);
    }

    public void hideToolTip() {
        if (tooltip != null) {
            tooltip.setVisible(false);
        }
    }

    public QueryExecuter getQueryExecuter() {
        return result;
    }

    public int promptToSaveOnClose() {
        CTabItem[] items = combinedQueryEditortabFolder.getItems();
        if (items.length == 0) {
            willClose = true;
            return ISaveablePart2.NO;
        }
        if (items.length > 1) {
            String msg = Messages.bind(Messages.msgConfirmEditorClose, items.length);
            if (!CommonUITool.openConfirmBox(msg)) {
                return ISaveablePart2.CANCEL;
            }
        }

        int dirtyCount = 0;
        for (CombinedQueryEditorComposite combinedQueryEditorComposite :
                getAllCombinedQueryEditorComposite()) {
            if (combinedQueryEditorComposite.isDirty()) {
                dirtyCount++;
            }
        }

        String msg = Messages.bind(Messages.msgConfirmEditorSave, dirtyCount);
        if (dirtyCount > 0 && !CommonUITool.openConfirmBox(msg)) {
            return ISaveablePart2.NO;
        }

        int cancelCount = 0;
        for (CTabItem item : items) {
            if (item instanceof SubQueryEditorTabItem) {
                CombinedQueryEditorComposite combinedQueryEditorComposite =
                        ((SubQueryEditorTabItem) item).getControl();
                try {
                    if (combinedQueryEditorComposite.isDirty()) {
                        if (combinedQueryEditorComposite.getSqlEditorComp().save()) {
                            combinedQueryEditorComposite.dispose();
                            item.dispose();
                        } else {
                            cancelCount++;
                        }
                    }
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
        }

        if (cancelCount > 0) {
            return ISaveablePart2.CANCEL;
        }

        return ISaveablePart2.NO;
    }

    /**
     * When dispose query editor, interrupt query thread, clear result and query plan, reset query
     * connection
     */
    public void dispose() {
        try {
            if (queryThread != null && !queryThread.isInterrupted()) {
                queryThread.interrupt();
                queryThread = null;
            }
            for (CombinedQueryEditorComposite combinedQueryEditorComposite :
                    getAllCombinedQueryEditorComposite()) {
                combinedQueryEditorComposite.getSqlEditorComp().release();
            }

        } catch (Exception event) {
            LOGGER.error("", event);
        } finally {
            connection.close();
            connection = null;
        }
        if (result != null) {
            result.dispose();
        }

        super.dispose();
    }

    /**
     * Set the query editor database connection
     *
     * @param database CubridDatabase
     */
    public void connect(CubridDatabase database) {
        if (database != null && database.getDatabaseInfo() != null) {
            connection.changeDatabaseInfo(database.getDatabaseInfo());
        }
        if (qeToolBar == null) {
            return;
        }
    }

    protected void hookRetragetActions() {
        ActionManager actionManager = ActionManager.getInstance();
        IActionBars bar = this.getEditorSite().getActionBars();

        bar.setGlobalActionHandler(UndoAction.ID, actionManager.getAction(UndoAction.ID));
        bar.setGlobalActionHandler(RedoAction.ID, actionManager.getAction(RedoAction.ID));

        IAction action = actionManager.getAction(CutAction.ID);
        action.setEnabled(true);
        bar.setGlobalActionHandler(CutAction.ID, action);

        IAction copyAction = actionManager.getAction(CopyAction.ID);
        bar.setGlobalActionHandler(CopyAction.ID, copyAction);
        copyAction.setEnabled(true);

        IAction pasteAction = actionManager.getAction(PasteAction.ID);
        bar.setGlobalActionHandler(PasteAction.ID, pasteAction);
        pasteAction.setEnabled(true);

        bar.setGlobalActionHandler(
                FindReplaceAction.ID, actionManager.getAction(FindReplaceAction.ID));
        bar.setGlobalActionHandler(QueryOpenAction.ID, actionManager.getAction(QueryOpenAction.ID));
        bar.updateActionBars();
    }

    public void createPartControl(Composite parent) {
        ScrolledComposite scrolledComp = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        FillLayout flayout = new FillLayout();
        scrolledComp.setLayout(flayout);

        topComposite = new Composite(scrolledComp, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.verticalSpacing = 0;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        gridLayout.horizontalSpacing = 0;
        topComposite.setLayout(gridLayout);
        topComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        scrolledComp.setContent(topComposite);
        scrolledComp.setExpandHorizontal(true);
        scrolledComp.setExpandVertical(true);

        hiddenGridDataForNotice = new GridData(0, 0);
        noticeMessageArea = new Label(topComposite, SWT.None);
        noticeMessageArea.setText("");
        noticeMessageArea.setLayoutData(hiddenGridDataForNotice);

        // create tool bar
        createToolBar();
        // create SQL editor tab folder
        createCombinedQueryEditorCTabFolder();
    }

    public void createToolBar() {
        final Composite toolBarComposite = new Composite(topComposite, SWT.NONE);
        toolBarComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 0;
        gridLayout.horizontalSpacing = 0;
        gridLayout.marginWidth = 0;
        toolBarComposite.setLayout(gridLayout);
        qeToolBar = new EditorToolBar(toolBarComposite, this);
        fillInToolbar();
    }

    public void fillInToolbar() {
        final ToolBar toolBar = qeToolBar;

        final ToolItem addEditorItem = new ToolItem(toolBar, SWT.PUSH);
        addEditorItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/tab_item_add.png"));
        addEditorItem.setToolTipText(Messages.queryEditorAddTabItemTooltip + "(Ctrl+Shift+T)");
        addEditorItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        hideToolTip();
                        addEditorTab();
                    }
                });

        new ToolItem(toolBar, SWT.SEPARATOR);
        ToolItem itemFormatterr = new ToolItem(toolBar, SWT.PUSH);
        itemFormatterr.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_format.png"));
        itemFormatterr.setToolTipText(Messages.formatTip + "(Ctrl+Shift+F)");
        itemFormatterr.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        hideToolTip();
                        combinedQueryComposite.getSqlEditorComp().format();
                    }
                });

        ToolItem itemIndent = new ToolItem(toolBar, SWT.PUSH);
        itemIndent.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_indent_add.png"));
        itemIndent.setToolTipText(Messages.indentTip + "(Tab)");
        itemIndent.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        hideToolTip();
                        if (combinedQueryComposite.getSqlEditorComp().isDisposed()) {
                            return;
                        }
                        combinedQueryComposite.getSqlEditorComp().indent();
                    }
                });

        ToolItem itemUnindent = new ToolItem(toolBar, SWT.PUSH);
        itemUnindent.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_indent_delete.png"));
        itemUnindent.setToolTipText(Messages.unIndentTip + "(Shift+Tab)");
        itemUnindent.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        hideToolTip();
                        if (combinedQueryComposite.getSqlEditorComp().isDisposed()) {
                            return;
                        }
                        combinedQueryComposite.getSqlEditorComp().unindent();
                    }
                });

        ToolItem clearItem = new ToolItem(toolBar, SWT.PUSH);
        clearItem.setImage(CommonUIPlugin.getImage("/icons/queryeditor/clear_sql.png"));
        clearItem.setToolTipText(Messages.clear);
        clearItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        hideToolTip();
                        if (combinedQueryComposite.getSqlEditorComp().isDisposed()) {
                            return;
                        }

                        if (!CommonUITool.openConfirmBox(Messages.msgClear)) {
                            return;
                        }

                        combinedQueryComposite.getSqlEditorComp().setQueries("");
                    }
                });

        new ToolItem(toolBar, SWT.SEPARATOR);

        ToolItem itemComment = new ToolItem(toolBar, SWT.PUSH);
        itemComment.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_comment_add.png"));
        itemComment.setToolTipText(Messages.commentTip + "(Ctrl+/)");
        itemComment.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        hideToolTip();
                        if (combinedQueryComposite.getSqlEditorComp().isDisposed()) {
                            return;
                        }
                    }
                });

        ToolItem itemUncomment = new ToolItem(toolBar, SWT.PUSH);
        itemUncomment.setImage(
                CommonUIPlugin.getImage("icons/queryeditor/query_comment_delete.png"));
        itemUncomment.setToolTipText(Messages.unCommentTip + "(Ctrl+/)");
        itemUncomment.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        hideToolTip();
                        if (combinedQueryComposite.getSqlEditorComp().isDisposed()) {
                            return;
                        }
                    }
                });

        new ToolItem(toolBar, SWT.SEPARATOR);

        ToolItem openItem = new ToolItem(toolBar, SWT.PUSH);
        openItem.setImage(CommonUIPlugin.getImage("/icons/queryeditor/file_open.png"));
        openItem.setToolTipText(Messages.open);
        openItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        hideToolTip();
                        doOpen();
                    }
                });

        ToolItem saveItem = new ToolItem(toolBar, SWT.PUSH);
        saveItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/file_save.png"));
        saveItem.setToolTipText(Messages.save);
        saveItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        hideToolTip();
                        doSave(new NullProgressMonitor());
                    }
                });

        ToolItem saveAsItem = new ToolItem(toolBar, SWT.PUSH);
        saveAsItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/file_saveas.png"));
        saveAsItem.setToolTipText(Messages.saveAs);
        saveAsItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        hideToolTip();
                        doSaveAs();
                    }
                });

        topComposite.pack();
        packToolBar();
    }

    /** create editor CTabFolder all sql editor tab will add to this */
    public void createCombinedQueryEditorCTabFolder() {
        combinedQueryEditortabFolder = new CTabFolder(topComposite, SWT.TOP);
        combinedQueryEditortabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        combinedQueryEditortabFolder.setUnselectedImageVisible(true);
        combinedQueryEditortabFolder.setUnselectedCloseVisible(false);
        combinedQueryEditortabFolder.setBorderVisible(true);
        combinedQueryEditortabFolder.setSimple(false);
        combinedQueryEditortabFolder.setSelectionBackground(
                CombinedQueryEditorComposite.BACK_COLOR);
        combinedQueryEditortabFolder.setSelectionForeground(
                ResourceManager.getColor(SWT.COLOR_BLACK));
        combinedQueryEditortabFolder.setMinimizeVisible(false);
        combinedQueryEditortabFolder.setMaximizeVisible(false);
        combinedQueryEditortabFolder.setTabHeight(22);
        combinedQueryEditortabFolder.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
                        CTabItem item = combinedQueryEditortabFolder.getSelection();
                        if (item instanceof SubQueryEditorTabItem) {
                            SubQueryEditorTabItem queryResultTabItem = (SubQueryEditorTabItem) item;
                            combinedQueryComposite = queryResultTabItem.getControl();
                            combinedQueryComposite.refreshEditorComposite();
                        }
                    }
                });

        TabContextMenuManager ctxmenu = new TabContextMenuManager(combinedQueryEditortabFolder);
        ctxmenu.createContextMenu();

        // add a default SQL Tab item
        addEditorTab();
    }

    public void updateTabName(SubQueryEditorTabItem tabItem, boolean dirty) {
        String tabName =
                (dirty ? "*" : "") + Messages.queryEditorTabItemName + " " + tabItem.getTabIndex();
        tabItem.setText(tabName);
    }

    public void updateTabName(SubQueryEditorTabItem tabItem, String tabName, boolean dirty) {
        tabItem.setText((dirty ? "*" : "") + tabName);
    }

    public CombinedQueryEditorComposite addEditorTab() {
        final SubQueryEditorTabItem subQueryEditorTabItem =
                new SubQueryEditorTabItem(combinedQueryEditortabFolder, SWT.NONE);
        subQueryEditorTabItem.setTabIndex(sqlEditorCounter++);

        updateTabName(subQueryEditorTabItem, false);
        subQueryEditorTabItem.setToolTipText(Messages.queryEditorTabItemTooltip);
        subQueryEditorTabItem.setShowClose(true);

        // When clicking the close button on a tab
        subQueryEditorTabItem.addDisposeListener(
                new DisposeListener() {
                    public void widgetDisposed(DisposeEvent e) {
                        if (!willClose && combinedQueryEditortabFolder.getItemCount() == 0) {
                            sqlEditorCounter = 1;
                            addEditorTab();
                        }
                    }
                });
        combinedQueryComposite =
                new CombinedQueryEditorComposite(
                        combinedQueryEditortabFolder, SWT.None, this, subQueryEditorTabItem);
        subQueryEditorTabItem.setControl(combinedQueryComposite);
        combinedQueryComposite.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        combinedQueryComposite.refreshEditorComposite();
        combinedQueryEditortabFolder.setSelection(subQueryEditorTabItem);
        return combinedQueryComposite;
    }
    /**
     * Return current editing query
     *
     * @return
     */
    public String getCurrentQuery() {
        if (combinedQueryComposite == null || combinedQueryComposite.getSqlEditorComp() == null) {
            return null;
        }
        return combinedQueryComposite.getSqlEditorComp().getText().getText();
    }

    /**
     * Set query
     *
     * @param query
     * @param isAppend
     */
    public void setQuery(
            final String query,
            final boolean isAppend) {
        /* Update queries in the UI JOB */
        UIJob job =
                new UIJob(Messages.settingQueries) {
                    public IStatus runInUIThread(IProgressMonitor monitor) {
                        if (isAvilableEditor()) {
                            if (isAppend) {
                                combinedQueryComposite.getSqlEditorComp().getText().append(query);
                                combinedQueryComposite.getSqlEditorComp().gotoButtom();
                            } else {
                                combinedQueryComposite.getSqlEditorComp().setQueries(query);
                            }
                        }

                        return Status.OK_STATUS;
                    }
                };
        job.setPriority(UIJob.SHORT);
        job.schedule();
    }

    /**
     * Set with prepared parameters
     *
     * @param displayQueries
     * @param runQueries
     * @param rowParameterList
     * @param isAppend
     */
    public void setQuery(
            final String displayQueries,
            final String runQueries,
            final List<List<PstmtParameter>> rowParameterList,
            final boolean isAppend) {
        // Update queries in the UI JOB
        UIJob job =
                new UIJob(Messages.settingQueries) {
                    public IStatus runInUIThread(IProgressMonitor monitor) {
                        if (isAvilableEditor()) {
                            if (isAppend) {
                                String allQuery = getAllQueries();
                                int start = allQuery.indexOf(displayQueries);
                                if (start == -1) {
                                    combinedQueryComposite
                                            .getSqlEditorComp()
                                            .getText()
                                            .append(displayQueries);
                                } else {
                                    setSelection(start, start + displayQueries.length());
                                }
                            } else {
                                combinedQueryComposite
                                        .getSqlEditorComp()
                                        .setQueries(displayQueries);
                            }
                        }

                        return Status.OK_STATUS;
                    }
                };
        job.setPriority(UIJob.SHORT);
        job.schedule();
    }

    public void newQueryTab(final String query) {
        UIJob job =
                new UIJob(Messages.settingQueries) {
                    public IStatus runInUIThread(IProgressMonitor monitor) {
                        if (isAvilableEditor()) {
                            String sql =
                                    combinedQueryComposite.getSqlEditorComp().getText().getText();
                            if (!StringUtil.isEmpty(sql)) {
                                addEditorTab();
                            }
                            combinedQueryComposite.getSqlEditorComp().setQueries(query);
                        }

                        return Status.OK_STATUS;
                    }
                };
        job.setPriority(UIJob.SHORT);
        job.schedule();
    }

    private boolean isAvilableEditor() {
        return combinedQueryComposite != null
                && combinedQueryComposite.getSqlEditorComp() != null
                && !combinedQueryComposite.getSqlEditorComp().isDisposed();
    }

    public void setSelection(int startPos, int endPos) {
        if (startPos >= 0 && endPos > startPos) {
            getSqlTextEditor().setSelection(startPos);
            getSqlTextEditor().setFocus();
        }
    }

    public String getAllQueries() {
        return combinedQueryComposite.getSqlEditorComp().getText().getText();
    }

    /** On the editor focused, show the selected server name on window title */
    public void setFocus() {
        combinedQueryComposite.getSqlEditorComp().getText().setFocus();
    }

    /** Open SQL script */
    public void doOpen() {
        try {
            combinedQueryComposite.getSqlEditorComp().doOpen();
        } catch (Exception e) {
            LOGGER.error("", e);
            CommonUITool.openErrorBox(getSite().getShell(), e.getMessage());
        }
    }

    /**
     * Save SQL script
     *
     * @param monitor IProgressMonitor
     */
    public void doSave(IProgressMonitor monitor) {
        try {
            boolean success = combinedQueryComposite.getSqlEditorComp().save();
            if (!success) {
                if (monitor != null) {
                    monitor.setCanceled(true);
                }
                return;
            }
        } catch (IOException event) {
            LOGGER.error(event.getMessage());
            CommonUITool.openErrorBox(getSite().getShell(), event.getMessage());
        }
    }

    public void doSaveAs() {
        try {
            combinedQueryComposite.getSqlEditorComp().doSaveAs();
        } catch (IOException event) {
            LOGGER.error(event.getMessage());
            CommonUITool.openErrorBox(getSite().getShell(), event.getMessage());
        }
    }

    /**
     * Get saved file
     *
     * @return file File
     */
    public static File getSavedFile() {
        String filterPath = CommonUIPlugin.getSettingValue("SAVE_SQL_SCRIPT_FILE");
        FileDialog dialog =
                new FileDialog(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        SWT.SAVE | SWT.APPLICATION_MODAL);
        dialog.setFilterExtensions(new String[] {"*.sql", "*.txt", "*.*"});
        dialog.setFilterNames(new String[] {"SQL File", "Text File", "All"});
        if (null != filterPath) {
            dialog.setFilterPath(filterPath);
        }
        String filePath = dialog.open();
        if (filePath == null) {
            return null;
        } else {
            File file = new File(filePath);
            if (file != null) {
                CommonUIPlugin.putSettingValue("SAVE_SQL_SCRIPT_FILE", file.getParent());
            }

            return file;
        }
    }

    /**
     * Open a dialog to set the save file
     *
     * @return dialog FileDialog
     */
    public static FileDialog openFileSavePlanDialog() {
        FileDialog dialog =
                new FileDialog(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        SWT.SAVE | SWT.APPLICATION_MODAL);
        dialog.setFilterExtensions(new String[] {"*.xml"});
        dialog.setFilterNames(new String[] {"XML File"});
        File curdir = new File(".");
        try {
            dialog.setFilterPath(curdir.getCanonicalPath());
        } catch (Exception event) {
            dialog.setFilterPath(".");
        }

        return dialog;
    }

    /**
     * Get opened file
     *
     * @return File File
     */
    public static File getOpenedSQLFile() {
        String filterPath = CommonUIPlugin.getSettingValue("OPEN_SQL_SCRIPT_FILE");
        FileDialog dialog =
                new FileDialog(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        SWT.OPEN | SWT.APPLICATION_MODAL);
        dialog.setFilterExtensions(new String[] {"*.sql", "*.txt", "*.*"});
        dialog.setFilterNames(new String[] {"SQL File", "Text File", "All"});
        if (null != filterPath) {
            dialog.setFilterPath(filterPath);
        }
        String filePath = dialog.open();
        if (filePath == null) {
            return null;
        } else {
            File file = new File(filePath);
            if (file != null) {
                CommonUIPlugin.putSettingValue("OPEN_SQL_SCRIPT_FILE", file.getParent());
            }

            return file;
        }
    }

    /**
     * Open a dialog to open plan contents
     *
     * @return dialog FileDialog
     */
    public static FileDialog openFileOpenPlanDialog() {
        FileDialog dialog =
                new FileDialog(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        SWT.OPEN | SWT.APPLICATION_MODAL);
        dialog.setFilterExtensions(new String[] {"*.xml"});
        dialog.setFilterNames(new String[] {"XML File"});
        File curdir = new File(".");
        try {
            dialog.setFilterPath(curdir.getCanonicalPath());
        } catch (Exception event) {
            dialog.setFilterPath(".");
        }

        return dialog;
    }

    /**
     * Is the query editor not save
     *
     * @return boolean
     */
    public boolean isDirty() {
        return true;
        //		if (saveFileMap.get(combinedQueryComposite) != null) {
        //			return dirty;
        //		}
        //
        //		if (combinedQueryComposite.isDirty()) {
        //			return true;
        //		}
        //		if (editorsMap.size() > 0) {
        //			CTabItem[] items = combinedQueryEditortabFolder.getItems();
        //			for (CTabItem item : items) {
        //				CombinedQueryEditorComposite comp = editorsMap.get(item);
        //				if (comp.isDirty()) {
        //					return true;
        //				}
        //			}
        //
        //			return false;
        //		}
        //
        //		ServerInfo serverInfo = getSelectedServer() == null ? null :
        // getSelectedServer().getServerInfo();
        //		boolean isWithoutPrompt = QueryOptions.getWithoutPromptSave(serverInfo);
        //		if (isWithoutPrompt) {
        //			return false;
        //		} else {
        //			return dirty;
        //		}
    }

    public boolean isSaveAsAllowed() {
        return true;
    }

    /** Find action */
    public void find() {
        combinedQueryComposite.getSqlEditorComp().find();
    }

    /** Pack the tool bar */
    private void packToolBar() {
        qeToolBar.pack();
    }

    public boolean isActive() {
        return isActive;
    }

    public static boolean isNotNeedQuote(String type) {
        if (DataType.DATATYPE_BIGINT.equals(type)
                //				|| DataType.DATATYPE_BIT.equals(type)
                //				|| DataType.DATATYPE_BIT_VARYING.equals(type)
                //				|| DataType.DATATYPE_BLOB.equals(type)
                //				|| DataType.DATATYPE_CLASS.equals(type)
                //				|| DataType.DATATYPE_CLOB.equals(type)
                || DataType.DATATYPE_CURRENCY.equals(type)
                //				|| DataType.DATATYPE_DATE.equals(type)
                //				|| DataType.DATATYPE_DATETIME.equals(type)
                || DataType.DATATYPE_DECIMAL.equals(type)
                || DataType.DATATYPE_DOUBLE.equals(type)
                || DataType.DATATYPE_FLOAT.equals(type)
                || DataType.DATATYPE_INT.equals(type)
                || DataType.DATATYPE_INTEGER.equals(type)
                || DataType.DATATYPE_MONETARY.equals(type)
                //				|| DataType.DATATYPE_MULTISET.equals(type)
                || DataType.DATATYPE_NATIONAL_CHARACTER.equals(type)
                || DataType.DATATYPE_NATIONAL_CHARACTER_VARYING.equals(type)
                || DataType.DATATYPE_NCHAR.equals(type)
                || DataType.DATATYPE_NCHAR_VARYING.equals(type)
                || DataType.DATATYPE_NUMERIC.equals(type)
                //				|| DataType.DATATYPE_OBJECT.equals(type)
                //				|| DataType.DATATYPE_OID.equals(type)
                || DataType.DATATYPE_REAL.equals(type)
                //				|| DataType.DATATYPE_SEQUENCE.equals(type)
                //				|| DataType.DATATYPE_SET.equals(type)
                || DataType.DATATYPE_SHORT.equals(type)
                || DataType.DATATYPE_SMALLINT.equals(type)
                //				|| DataType.DATATYPE_TIME.equals(type)
                //				|| DataType.DATATYPE_TIMESTAMP.equals(type)
                || DataType.DATATYPE_TINYINT.equals(type)) {
            return true;
        }

        return false;
    }

    public static boolean isNullEmpty(String type, String value) {
        if ((value == null || value.length() == 0)
                && (DataType.DATATYPE_BIGINT.equals(type)
                        || DataType.DATATYPE_BIT.equals(type)
                        || DataType.DATATYPE_BIT_VARYING.equals(type)
                        || DataType.DATATYPE_BLOB.equals(type)
                        || DataType.DATATYPE_CHAR.equals(type)
                        || DataType.DATATYPE_CLASS.equals(type)
                        || DataType.DATATYPE_CLOB.equals(type)
                        || DataType.DATATYPE_CURRENCY.equals(type)
                        || DataType.DATATYPE_DATE.equals(type)
                        || DataType.DATATYPE_DATETIME.equals(type)
                        || DataType.DATATYPE_DECIMAL.equals(type)
                        || DataType.DATATYPE_DOUBLE.equals(type)
                        || DataType.DATATYPE_FLOAT.equals(type)
                        || DataType.DATATYPE_INT.equals(type)
                        || DataType.DATATYPE_INTEGER.equals(type)
                        || DataType.DATATYPE_MONETARY.equals(type)
                        || DataType.DATATYPE_MULTISET.equals(type)
                        || DataType.DATATYPE_NATIONAL_CHARACTER.equals(type)
                        || DataType.DATATYPE_NATIONAL_CHARACTER_VARYING.equals(type)
                        || DataType.DATATYPE_NCHAR.equals(type)
                        || DataType.DATATYPE_NCHAR_VARYING.equals(type)
                        || DataType.DATATYPE_NUMERIC.equals(type)
                        || DataType.DATATYPE_OBJECT.equals(type)
                        || DataType.DATATYPE_OID.equals(type)
                        || DataType.DATATYPE_REAL.equals(type)
                        || DataType.DATATYPE_SEQUENCE.equals(type)
                        || DataType.DATATYPE_SET.equals(type)
                        || DataType.DATATYPE_SHORT.equals(type)
                        || DataType.DATATYPE_SMALLINT.equals(type)
                        || DataType.DATATYPE_TIME.equals(type)
                        || DataType.DATATYPE_TIMESTAMP.equals(type)
                        || DataType.DATATYPE_TINYINT.equals(type))) {
            return true;
        }

        return false;
    }

    /**
     * the type can't deal on result type
     *
     * @param type
     * @return boolean
     */
    public boolean isIgnoreType(String type) {
        if (type == null || type.equals("")) {
            return false;
        }

        if (DataType.DATATYPE_CURSOR.equals(type)
                || DataType.DATATYPE_OID.equals(type)
                || DataType.DATATYPE_CLASS.equals(type)
                || DataType.DATATYPE_OBJECT.equals(type)
                || DataType.DATATYPE_CURSOR.equals(type)) {
            return true;
        }

        return false;
    }

    public boolean isCollectExecStats() {
        return collectExecStats;
    }

    public DBConnection getConnection() {
        return connection;
    }

    /**
     * Return whether the query is TCL or not.
     *
     * @param sql String
     * @return boolean
     */
    private boolean hasResolvedTransactionQuery(String sql) {
        if (sql == null) {
            return false;
        }
        String tcl = sql.toLowerCase();
        return tcl.startsWith("commit") || tcl.startsWith("rollback");
    }

    public void hideTuneModeResult() {}

    /**
     * Get the SQL string on editor
     *
     * @return SQL String
     */
    public String getSelectText() {
        return combinedQueryComposite.getSqlEditorComp().getText().getSelectionText();
    }

    /**
     * Set SQL string to editor
     *
     * @param contents String
     */
    public void setSelectText(String contents) {
        StyledText text = combinedQueryComposite.getSqlEditorComp().getText();
        Point range = text.getSelectionRange();
        text.replaceTextRange(range.x, range.y, contents);
        text.setSelection(range.x + contents.length());
    }

    /** Delete the selected text */
    public void deleteSelectedText() {
        setSelectText("");
    }

    /**
     * When navigator node change ,refresh the database list on query editor
     *
     * @param event CubridNodeChangedEvent
     */
    public void nodeChanged(CubridNodeChangedEvent event) {
        ICubridNode cubridNode = event.getCubridNode();
        CubridNodeChangedEventType eventType = event.getType();
        if (cubridNode == null || eventType == null) {
            return;
        }
        String type = cubridNode.getType();
        if (!NodeType.SERVER.equals(type)
                && !NodeType.DATABASE_FOLDER.equals(type)
                && !NodeType.DATABASE.equals(type)) {
            return;
        }

        /* Judge the event type */
        if (!CubridNodeChangedEventType.SERVER_CONNECTED.equals(eventType)
                && !CubridNodeChangedEventType.SERVER_DISCONNECTED.equals(eventType)
                && !CubridNodeChangedEventType.DATABASE_LOGIN.equals(eventType)
                && !CubridNodeChangedEventType.DATABASE_LOGOUT.equals(eventType)
                && !CubridNodeChangedEventType.DATABASE_STOP.equals(eventType)
                && !CubridNodeChangedEventType.DATABASE_START.equals(eventType)) {
            return;
        }

        synchronized (this) {
            qeToolBar.refresh();
        }
    }

    /** Format the SQL script */
    public void format() {
        if (combinedQueryComposite != null && !combinedQueryComposite.isDisposed()) {
            combinedQueryComposite.getSqlEditorComp().format();
        }
    }

    private StyledText getSqlTextEditor() {
        return combinedQueryComposite.getSqlEditorComp().getText();
    }

    /**
     * Get sql editor widget, can't use to set/append query
     *
     * @return
     */
    public StyledText getSqlEditorWidget() {
        return combinedQueryComposite.getSqlEditorComp().getText();
    }

    public boolean isIncludeOidInfo() {
        return isIncludeOidInfo;
    }

    /**
     * Open a message dialog to confirm skip query error or not.
     *
     * @param ee SQLException
     * @param errorSql String
     * @return value boolean[]
     */
    public boolean[] queryErrSkipOrNot(final SQLException ee, String errorSql) {
        final boolean[] value = new boolean[2];
        SQLEditorComposite sqlEditorComp = combinedQueryComposite.getSqlEditorComp();
        StyledText txaEdit = sqlEditorComp.getText();
        if (txaEdit != null && !txaEdit.isDisposed()) {
            sqlEditorComp.txtFind(errorSql, 0, false, false, true, false);
            line = txaEdit.getLineAtOffset(txaEdit.getSelection().x) + 1;
        }
        String errorLineMsg = Messages.bind(Messages.errWhere, line);
        String errMsg =
                Messages.skipOrNot
                        + StringUtil.NEWLINE
                        + StringUtil.NEWLINE
                        + Messages.runError
                        + ee.getErrorCode()
                        + StringUtil.NEWLINE
                        + errorLineMsg
                        + StringUtil.NEWLINE
                        + Messages.errorHead
                        + ee.getMessage();

        MessageDialog dialog =
                new MessageDialog(
                        TextEditorPart.this.getEditorSite().getShell(),
                        Messages.warning,
                        null,
                        errMsg,
                        MessageDialog.QUESTION,
                        new String[] {Messages.btnYes, Messages.btnNo},
                        1) {
                    Button btn = null;

                    protected Control createCustomArea(Composite parent) {
                        btn = new Button(parent, SWT.CHECK);
                        btn.setText(Messages.showOneTimeTip);
                        return btn;
                    }

                    protected void buttonPressed(int buttonId) {
                        value[0] = btn.getSelection();
                        if (buttonId == IDialogConstants.CANCEL_ID) {
                            value[1] = false;
                        } else {
                            value[1] = true;
                        }
                        close();
                    }
                };
        dialog.open();

        return value;
    }

    /**
     * get current select combinedQueryComposite
     *
     * @return
     */
    public CombinedQueryEditorComposite getCombinedQueryComposite() {
        return combinedQueryComposite;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setIsRunning(boolean status) {
        isRunning = status;
    }

    public void beginCollectExecStats() {
        Connection conn = null;
        try {
            conn = connection.getConnection(false);
        } catch (SQLException ex) {
            CommonUITool.openErrorBox(ex.getLocalizedMessage());
            return;
        }
        CubridUtil.beginCollectExecStats(conn);
    }

    public void setCombinedQueryEditortabFolderSelecton(int index) {
        combinedQueryEditortabFolder.setSelection(index);
    }

    public EditorToolBar getQeToolBar() {
        return qeToolBar;
    }

    public void copySelectedItems() {
        result.copySelectedItems();
    }

    public void copyAllItems() {
        result.copyAllItems();
    }

    public static String makeSqlErrorOnResult(int index, String sql, Exception ee) {
        StringBuilder logs = new StringBuilder();
        logs.append(
                        Messages.bind(
                                Messages.querySeq,
                                StringUtil.getOrdinalFromCardinalNumber(index + 1)))
                .append(" ");
        logs.append(Messages.queryFail);
        logs.append(StringUtil.NEWLINE);
        logs.append(makeSqlLogOnResult(sql));

        if (ee instanceof SQLException) {
            logs.append(Messages.runError)
                    .append(" ")
                    .append(((SQLException) ee).getErrorCode())
                    .append(StringUtil.NEWLINE);
        }

        if (ee.getMessage() != null) {
            logs.append(ee.getMessage()).append(StringUtil.NEWLINE);
        }

        logs.append(StringUtil.NEWLINE).append(StringUtil.NEWLINE);

        return logs.toString();
    }

    public List<CombinedQueryEditorComposite> getAllCombinedQueryEditorComposite() {
        List<CombinedQueryEditorComposite> combinedQueryEditorCompositeList =
                new ArrayList<CombinedQueryEditorComposite>();

        CTabItem[] items = combinedQueryEditortabFolder.getItems();
        for (CTabItem item : items) {
            if (item instanceof SubQueryEditorTabItem) {
                CombinedQueryEditorComposite combinedQueryEditorComposite =
                        ((SubQueryEditorTabItem) item).getControl();
                combinedQueryEditorCompositeList.add(combinedQueryEditorComposite);
            }
        }

        return combinedQueryEditorCompositeList;
    }

    public static String makeSqlLogOnResult(String sql) {
        StringBuilder logs = new StringBuilder();
        logs.append(QueryUtil.SPLIT_LINE_FOR_QUERY_RESULT);
        logs.append(StringUtil.NEWLINE);
        logs.append(sql).append(StringUtil.NEWLINE);
        logs.append(StringUtil.NEWLINE).append(StringUtil.NEWLINE);
        return logs.toString();
    }

    /** properm text changed */
    public void textChanged(TextEvent event) {
    }
    
    public String getSelectedText() {
        return combinedQueryComposite.getSqlEditorComp().getText().getSelectionText();
    }

    @Override
    public String getMessages() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<String> getDecoratorWords() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CubridDatabase getDatabase() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DatabaseInfo getDatabaseInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ServerInfo getServerInfo() {
        // TODO Auto-generated method stub
        return null;
    }
}
