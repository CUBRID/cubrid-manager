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
package com.cubrid.common.ui.common.navigator;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.cubrid.table.dialog.imp.ImportConfig;
import com.cubrid.common.ui.cubrid.table.dialog.imp.ImportDataEditorInput;
import com.cubrid.common.ui.cubrid.table.dialog.imp.ImportDataViewPart;
import com.cubrid.common.ui.cubrid.table.dialog.imp.TableConfig;
import com.cubrid.common.ui.query.dialog.SetFileEncodingDialog;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.query.editor.QueryUnit;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.persist.FavoriteQueryPersistUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class FavoriteQueryNavigatorView extends ViewPart {
    public static final String ID = "com.cubrid.common.navigator.favoritequery";

    private TableViewer tv;
    private TableEditor editor = null;
    private Button addButton = null;
    private Button delButton = null;
    private Button runButton = null;
    private Button openFileButton = null;
    private Button runFileButton = null;
    private String lastDirectory = null;

    public void createPartControl(Composite parent) {
        FavoriteQueryPersistUtil.getInstance().loadBatchRunList();
        Composite comp = new Composite(parent, SWT.NONE);
        {
            GridLayout gl = new GridLayout(1, false);
            comp.setLayout(gl);
            GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            comp.setLayoutData(gd);
        }
        createTableGroup(comp);
        createButtonComp(comp);
    }

    private void createTableGroup(Composite composite) {
        final String[] columnNames =
                new String[] {
                    "",
                    com.cubrid.common.ui.query.Messages.msgBatchRunSqlFile,
                    com.cubrid.common.ui.query.Messages.msgBatchRunMemo,
                    com.cubrid.common.ui.query.Messages.msgBatchRunRegdate
                };
        tv =
                CommonUITool.createCommonTableViewer(
                        composite,
                        null,
                        columnNames,
                        CommonUITool.createGridData(GridData.FILL_BOTH, 3, 1, -1, 200));
        tv.setInput(FavoriteQueryPersistUtil.getInstance().getListData());
        {
            TableLayout tableLayout = new TableLayout();
            tableLayout.addColumnData(new ColumnPixelData(0));
            tableLayout.addColumnData(new ColumnPixelData(209));
            tableLayout.addColumnData(new ColumnPixelData(80));
            tableLayout.addColumnData(new ColumnPixelData(118));
            tv.getTable().setLayout(tableLayout);
        }

        editor = new TableEditor(tv.getTable());
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal = true;

        tv.getTable()
                .addSelectionListener(
                        new SelectionListener() {
                            public void widgetSelected(SelectionEvent e) {
                                widgetDefaultSelected(e);
                            }

                            public void widgetDefaultSelected(SelectionEvent e) {
                                updateButtonStatus();
                            }
                        });

        tv.getTable()
                .addListener(
                        SWT.MouseUp,
                        new Listener() {
                            public void handleEvent(Event event) {
                                if (event.button != 1) {
                                    return;
                                }

                                updateButtonStatus();

                                Point pt = new Point(event.x, event.y);
                                int newIndex = tv.getTable().getSelectionIndex();
                                if (tv.getTable().getItemCount() <= newIndex || newIndex < 0) {
                                    return;
                                }
                                final TableItem item = tv.getTable().getItem(newIndex);
                                if (item == null) {
                                    return;
                                }
                                Rectangle rect = item.getBounds(2);
                                if (rect.contains(pt)) {
                                    focusCell(item, newIndex, 2);
                                }
                            }
                        });

        tv.addDoubleClickListener(
                new IDoubleClickListener() {
                    public void doubleClick(DoubleClickEvent event) {
                        ISelection selection = event.getSelection();
                        openSelectedFile(selection);
                    }
                });

        createContextMenu();
    }

    private void createContextMenu() {
        final MenuManager menuManager = new MenuManager();
        menuManager.setRemoveAllWhenShown(true);

        final Menu contextMenu = menuManager.createContextMenu(tv.getTable());
        tv.getTable().setMenu(contextMenu);

        final Menu popupMenu =
                new Menu(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        SWT.POP_UP);
        tv.getTable().setMenu(popupMenu);

        final MenuItem openMenuItem = new MenuItem(popupMenu, SWT.PUSH);
        openMenuItem.setText(com.cubrid.common.ui.query.Messages.lblOpenSqlFavorite);
        openMenuItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        ISelection selection = tv.getSelection();
                        openSelectedFile(selection);
                    }
                });

        final MenuItem runMenuItem = new MenuItem(popupMenu, SWT.PUSH);
        runMenuItem.setText(com.cubrid.common.ui.query.Messages.lblRunSqlFavorite);
        runMenuItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        executeSql();
                    }
                });

        new MenuItem(popupMenu, SWT.SEPARATOR);

        final MenuItem addMenuItem = new MenuItem(popupMenu, SWT.PUSH);
        addMenuItem.setText(Messages.btnAdd);
        addMenuItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        addFile();
                    }
                });

        new MenuItem(popupMenu, SWT.SEPARATOR);

        final MenuItem removeMenuItem = new MenuItem(popupMenu, SWT.PUSH);
        removeMenuItem.setText(com.cubrid.common.ui.query.Messages.lblRemoveFromSqlFavorite);
        removeMenuItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (!CommonUITool.openConfirmBox(
                                com.cubrid.common.ui.query.Messages.errBatchRunDel)) {
                            return;
                        }
                        removeFavorite();
                    }
                });

        final MenuItem delMenuItem = new MenuItem(popupMenu, SWT.PUSH);
        delMenuItem.setText(com.cubrid.common.ui.query.Messages.lblDeleteFromSqlFavorite);
        delMenuItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (!CommonUITool.openConfirmBox(
                                com.cubrid.common.ui.query.Messages.errBatchRunDelWithFile)) {
                            return;
                        }
                        removeFavoriteWithFileDeletion();
                    }
                });

        popupMenu.addMenuListener(
                new MenuListener() {
                    public void menuShown(MenuEvent e) {
                        if (tv.getTable().getSelection().length == 0) {
                            openMenuItem.setEnabled(false);
                            runMenuItem.setEnabled(false);
                            removeMenuItem.setEnabled(false);
                            delMenuItem.setEnabled(false);
                        } else {
                            openMenuItem.setEnabled(true);
                            runMenuItem.setEnabled(true);
                            removeMenuItem.setEnabled(true);
                            delMenuItem.setEnabled(true);
                        }
                    }

                    public void menuHidden(MenuEvent e) {}
                });
    }

    private void updateButtonStatus() {
        int checkedCount = tv.getTable().getSelectionCount();
        if (delButton != null) {
            delButton.setEnabled(checkedCount > 0);
        }
        if (runButton != null) {
            runButton.setEnabled(checkedCount > 0);
        }
        if (openFileButton != null) {
            openFileButton.setEnabled(checkedCount > 0);
        }
        if (runFileButton != null) {
            runFileButton.setEnabled(checkedCount > 0);
        }
    }

    /**
     * Create button composite
     *
     * @param composite the composite
     */
    private void createButtonComp(Composite composite) {
        Composite buttonComposite = new Composite(composite, SWT.NONE);
        {
            GridLayout layout = new GridLayout();
            layout.numColumns = 5;
            buttonComposite.setLayout(layout);
            buttonComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        }

        runFileButton = new Button(buttonComposite, SWT.PUSH);
        runFileButton.setEnabled(false);
        runFileButton.setToolTipText(Messages.lblRunFileFromFavorite);
        runFileButton.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_run.png"));
        runFileButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        executeSql();
                    }
                });

        openFileButton = new Button(buttonComposite, SWT.PUSH);
        openFileButton.setEnabled(false);
        openFileButton.setToolTipText(Messages.lblOpenFileFromFavorite);
        openFileButton.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_run_file.png"));
        openFileButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        ISelection selection = tv.getSelection();
                        openSelectedFile(selection);
                    }
                });

        addButton = new Button(buttonComposite, SWT.PUSH);
        addButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        addButton.setToolTipText(Messages.lblAddFileFromFavorite);
        addButton.setImage(CommonUIPlugin.getImage("icons/queryplan/add_query.gif"));
        addButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        int res =
                                CommonUITool.openConfirmBoxWithThreeButton(
                                        Messages.msgConfirmAddFavorite,
                                        Messages.btnConfirmAddFile,
                                        Messages.btnConfirmSaveFile,
                                        Messages.btnCancel);
                        if (res == 0) {
                            addFile();
                        } else if (res == 1) {
                            saveFile();
                        }
                    }
                });

        delButton = new Button(buttonComposite, SWT.PUSH);
        delButton.setEnabled(false);
        delButton.setToolTipText(Messages.lblDeleteFileFromFavorite);
        delButton.setImage(CommonUIPlugin.getImage("icons/queryplan/delete_query.gif"));
        delButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        int selectedIndex =
                                CommonUITool.openConfirmBoxWithThreeButton(
                                        Messages.msgDeleteFavorite,
                                        Messages.btnRemoveFavorite,
                                        Messages.btnDeleteFavoriteFile,
                                        Messages.btnCancel);
                        if (selectedIndex == 2) {
                            return;
                        } else if (selectedIndex == 0) {
                            removeFavorite();
                        } else if (selectedIndex == 1) {
                            removeFavoriteWithFileDeletion();
                        }
                    }
                });
    }

    public void focusCell(final TableItem item, final int row, final int col) {
        final StyledText text = new StyledText(tv.getTable(), SWT.SINGLE);
        Listener textListener = new TableItemEditor(text, item, row, col);
        text.addListener(SWT.FocusOut, textListener);
        text.addListener(SWT.Traverse, textListener);
        text.addListener(SWT.FocusIn, textListener);
        text.addTraverseListener(
                new TraverseListener() {
                    public void keyTraversed(TraverseEvent e) {
                        if (e.detail == SWT.TRAVERSE_TAB_NEXT
                                || e.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
                            e.doit = false;
                            int newColumn = col == 0 ? 1 : 0;
                            focusCell(item, row, newColumn);
                        } else if (e.detail == SWT.TRAVERSE_RETURN) {
                            e.doit = false;
                            addButton.setFocus();
                        }
                    }
                });
        text.setEditable(true);
        editor.setEditor(text, item, col);
        text.setText(item.getText(col));
        text.selectAll();
        try {
            text.setFocus();
        } catch (Exception e) {
        }
    }

    public static FavoriteQueryNavigatorView getInstance() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return null;
        }
        IWorkbenchPage page = window.getActivePage();
        if (page == null) {
            return null;
        }
        IViewReference viewReference = page.findViewReference(ID);
        if (viewReference != null) {
            IViewPart viewPart = viewReference.getView(false);
            return viewPart instanceof FavoriteQueryNavigatorView
                    ? (FavoriteQueryNavigatorView) viewPart
                    : null;
        }
        return null;
    }

    public void setFocus() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }
        IWorkbenchPage page = window.getActivePage();
        if (page == null) {
            return;
        }
        IViewReference viewReference = page.findViewReference(ID);
        if (viewReference != null) {
            IViewPart viewPart = viewReference.getView(false);
            getSite().getPage().activate(viewPart);
        }
    }

    private void pasteToEditor(
            String filename, String filepath, String charset, boolean isOpenFileMode) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null || window.getActivePage() == null) {
            return;
        }
        IEditorPart editor = window.getActivePage().getActiveEditor();
        try {
            if (editor == null || !(editor instanceof QueryEditorPart)) {
                editor = window.getActivePage().openEditor(new QueryUnit(), QueryEditorPart.ID);
            }
        } catch (PartInitException e) {
            editor = null;
        }
        if (editor == null) {
            return;
        }

        String fullpath = filepath + File.separator + filename;
        if (StringUtil.isEmpty(charset)) {
            charset = StringUtil.getDefaultCharset();
        }

        try {
            QueryEditorPart queryEditor = (QueryEditorPart) editor;
            StringBuilder sb = new StringBuilder();
            if (!isOpenFileMode) {
                sb.append("/* SQL Filename: ")
                        .append(filename)
                        .append(" */")
                        .append(StringUtil.NEWLINE);
            }

            BufferedReader in = null;
            try {
                File file = new File(fullpath);
                in = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
                String line = in.readLine();
                while (line != null) {
                    sb.append(line + StringUtil.NEWLINE);
                    line = in.readLine();
                }
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                }
            }

            if (!StringUtil.isEmpty(queryEditor.getCurrentQuery())) {
                queryEditor.addEditorTab();
            }
            if (isOpenFileMode) {
                queryEditor.getCombinedQueryComposite().getSqlEditorComp().open(fullpath, charset);
            } else {
                queryEditor.setQuery(sb.toString(), false, false, false);
            }
            queryEditor.setFocus();
        } catch (IOException e) {
            CommonUITool.openErrorBox(e.getMessage());
        }
    }

    public void addFavoriteQuery(String sql) {
        setFocus();
        FavoriteQueryPersistUtil.getInstance().addFavoriteQuery(sql);
        reloadFavoriteQuery();
    }

    private void addFavoriteByFileLink(
            String basepath, String filename, String memo, String charset) {
        FavoriteQueryPersistUtil.getInstance()
                .addFavorite(basepath, filename, memo, charset, false);
        reloadFavoriteQuery();

        Table table = tv.getTable();
        table.deselectAll();
        if (table.getItemCount() > 0) {
            table.select(table.getItemCount() - 1);
        }
    }

    private void reloadFavoriteQuery() {
        int newIndex = tv.getTable().getItemCount() - 1;
        if (newIndex >= 0) {
            tv.getTable().select(newIndex);
        }
        tv.setInput(FavoriteQueryPersistUtil.getInstance().getListData());
    }

    @SuppressWarnings("unchecked")
    private void openSelectedFile(ISelection selection) {
        StructuredSelection structuredSelection = (StructuredSelection) selection;
        for (Iterator<Map<String, String>> iter = structuredSelection.iterator();
                iter.hasNext(); ) {
            Map<String, String> item = iter.next();
            pasteToEditor(item.get("1"), item.get("4"), item.get("5"), true);
        }
    }

    private void removeFavorite() {
        int[] indexes = tv.getTable().getSelectionIndices();
        for (int i = indexes.length - 1; i >= 0; i--) {
            FavoriteQueryPersistUtil.getInstance().remove(indexes[i], false);
        }
        FavoriteQueryPersistUtil.getInstance().saveBatchRunList();
        tv.setInput(FavoriteQueryPersistUtil.getInstance().getListData());
    }

    private void removeFavoriteWithFileDeletion() {
        int[] indexes = tv.getTable().getSelectionIndices();
        for (int i = indexes.length - 1; i >= 0; i--) {
            FavoriteQueryPersistUtil.getInstance().remove(indexes[i], true);
        }
        FavoriteQueryPersistUtil.getInstance().saveBatchRunList();
        tv.setInput(FavoriteQueryPersistUtil.getInstance().getListData());
    }

    /** Add a favorite by external sql file(s). */
    private void addFile() {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        String charset = StringUtil.getDefaultCharset();
        SetFileEncodingDialog dialog = new SetFileEncodingDialog(shell, charset, true);
        if (dialog.open() != IDialogConstants.OK_ID) {
            return;
        }

        String memo = null;
        try {
            CubridDatabase cubridDatabase = getCurrentDatabase();
            memo =
                    cubridDatabase.getDatabaseInfo().getDbName()
                            + "@"
                            + cubridDatabase.getServer().getServerInfo().getHostAddress();
        } catch (Exception e) {
            memo = "";
        }

        String filenameToSave = dialog.getFilePath();
        File file = new File(filenameToSave);
        String pathname = file.getPath();
        String filename = file.getName();
        int sp = pathname.indexOf(filename);
        if (sp != -1) {
            pathname = pathname.substring(0, sp);
        }
        lastDirectory = pathname;
        charset = dialog.getEncoding();
        addFavoriteByFileLink(lastDirectory, filename, memo, charset);
    }

    /** Save query editor's sql into locally managed file. */
    private void saveFile() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null || window.getActivePage() == null) {
            CommonUITool.openErrorBox(Messages.errCanNotFindQueryEditor);
            return;
        }
        IEditorPart editor = window.getActivePage().getActiveEditor();
        if (editor == null || !(editor instanceof QueryEditorPart)) {
            CommonUITool.openErrorBox(Messages.errCanNotFindQueryEditor);
            return;
        }
        QueryEditorPart queryEditor = (QueryEditorPart) editor;
        String query = queryEditor.getCurrentQuery();
        addFavoriteQuery(query);
    }

    /**
     * Table item editor
     *
     * @author Isaiah Choe
     * @version 1.0 - 2013-07-10 created by Isaiah Choe
     */
    private class TableItemEditor implements Listener {
        private boolean isRunning = false;
        private final TableItem item;
        private final int row;
        private final int column;
        private final StyledText text;
        private Shell shell;

        public TableItemEditor(StyledText text, TableItem item, int row, int column) {
            this.text = text;
            this.item = item;
            this.row = row;
            this.column = column;
            shell =
                    new Shell(
                            Display.getDefault().getActiveShell(),
                            SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
            shell.setText("");
            shell.setLayout(new GridLayout());
            shell.setLayoutData(new GridData(GridData.FILL_BOTH));
        }

        /**
         * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
         * @param event the event which occurred
         */
        public void handleEvent(final Event event) {
            if (event.type == SWT.FocusOut) {
                if (isRunning) {
                    return;
                }
                isRunning = true;
                boolean isChanged = !text.getText().equals(item.getText(column));
                if (isChanged) {
                    item.setText(column, text.getText());
                    Map<String, String> data =
                            FavoriteQueryPersistUtil.getInstance().getListData().get(row);
                    data.put("2", text.getText());
                    FavoriteQueryPersistUtil.getInstance().saveBatchRunList();
                } else if ("".equals(text.getText())) {
                    item.setText(column, "");
                }
                text.dispose();
                isRunning = false;
            } else if (event.type == SWT.Traverse && event.detail == SWT.TRAVERSE_ESCAPE) {
                if (isRunning) {
                    return;
                }
                isRunning = true;
                text.dispose();
                event.doit = false;
                isRunning = false;
            } else if (event.type == SWT.FocusIn) {
            }
        }
    }

    /** Execute selected queries */
    private void executeSql() {
        CubridDatabase cubridDatabase = getCurrentDatabase();
        if (cubridDatabase == null || cubridDatabase.getDatabaseInfo() == null) {
            CommonUITool.openErrorBox(com.cubrid.common.ui.query.Messages.errNoConnectionBatchRun);
            return;
        }
        // Judge the file is exist
        int[] indexes = tv.getTable().getSelectionIndices();
        List<Map<String, String>> list = FavoriteQueryPersistUtil.getInstance().getListData();
        for (int i = 0; i < indexes.length; i++) {
            Map<String, String> item = list.get(indexes[i]);
            String filename = item.get("1");
            String fullpath = item.get("4") + File.separator + filename;
            if (!new File(fullpath).exists()) {
                String msg =
                        Messages.bind(
                                com.cubrid.common.ui.query.Messages.errFileNotExist, fullpath);
                CommonUITool.openErrorBox(msg);
                return;
            }
        }

        String targetDbName =
                cubridDatabase.getDatabaseInfo().getDbName()
                        + "@"
                        + cubridDatabase.getServer().getServerInfo().getHostAddress();
        String msg =
                Messages.bind(
                        com.cubrid.common.ui.query.Messages.msgDoYouWantExecuteSql, targetDbName);
        if (!CommonUITool.openConfirmBox(msg)) {
            return;
        }

        String charset = StringUtil.getDefaultCharset();
        ImportConfig importConfig = new ImportConfig();
        importConfig.setImportType(ImportConfig.IMPORT_FROM_SQL);
        importConfig.setThreadCount(1);
        importConfig.setCommitLine(100);

        Set<String> charsets = new HashSet<String>();
        for (int i = 0; i < indexes.length; i++) {
            Map<String, String> item = list.get(indexes[i]);
            String filename = item.get("1");
            String fullpath = item.get("4") + File.separator + filename;
            charset = item.get("5");
            if (StringUtil.isEmpty(charset)) {
                charset = StringUtil.getDefaultCharset();
            } else {
                charsets.add(charset);
            }
            TableConfig fileConfig = new TableConfig(filename);
            fileConfig.setFilePath(fullpath);
            fileConfig.setFileType(TableConfig.TYPE_DATA);
            importConfig.addTableConfig(fileConfig);
        }
        importConfig.setFilesCharset(charset);

        if (charsets.size() > 1) {
            msg =
                    Messages.bind(
                            com.cubrid.common.ui.query.Messages
                                    .msgDoYouWantExecuteWithNotSameCharset,
                            charset);
            if (!CommonUITool.openConfirmBox(msg)) {
                return;
            }
        }

        ImportDataEditorInput input = new ImportDataEditorInput();
        input.setDatabase(cubridDatabase);
        input.setImportConfig(importConfig);
        try {
            PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow()
                    .getActivePage()
                    .openEditor(input, ImportDataViewPart.ID);
        } catch (Exception e) {
            CommonUITool.openErrorBox(Display.getCurrent().getActiveShell(), e.getMessage());
        }
    }

    /**
     * Return current selected database from the navigation tree.
     *
     * @return CubridDatabase
     */
    private CubridDatabase getCurrentDatabase() {
        CubridDatabase cubridDatabase = null;
        CubridNavigatorView nav = CubridNavigatorView.findNavigationView();
        if (nav != null) {
            TreeItem[] items = nav.getSelectedItems();
            if (items.length > 0 && items[0] != null && items[0].getData() instanceof ISchemaNode) {
                ISchemaNode node = (ISchemaNode) items[0].getData();
                CubridDatabase tempDatabase = node.getDatabase();
                if (tempDatabase != null && tempDatabase.isLogined()) {
                    cubridDatabase = tempDatabase;
                }
            }
        }
        return cubridDatabase;
    }
}
