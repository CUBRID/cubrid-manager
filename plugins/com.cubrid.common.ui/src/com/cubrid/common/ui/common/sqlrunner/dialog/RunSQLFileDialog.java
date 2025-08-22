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
package com.cubrid.common.ui.common.sqlrunner.dialog;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.common.sqlrunner.part.RunSQLFileEditorInput;
import com.cubrid.common.ui.common.sqlrunner.part.RunSQLFileViewPart;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.persist.PersistUtils;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * @author fulei
 * @version 1.0 - 2012-7-19 created by fulei
 */
public class RunSQLFileDialog extends CMTitleAreaDialog {
    private static final Logger LOGGER = LogUtil.getLogger(RunSQLFileDialog.class);

    public static final String SQLFILEPATH = "";
    private Spinner commitCountSpinner;
    private Spinner threadCountSpinner;
    private Combo fileCharsetCombo;
    private List<String> filesList = new ArrayList<String>();
    private CubridDatabase[] cubridDatabases;
    private Text saveErrExcelPath;
    private Button saveErrExcelBtn;
    private TableViewer sqlFileTableViewer;
    private TableViewer databaseTableViewer;
    private Composite databaseTableComp;
    private RunSQLFileDialogDNDController runSQLFileDialogDNDController;

    public RunSQLFileDialog(Shell parentShell, CubridDatabase[] cubridDatabases) {
        super(parentShell);
        setShellStyle(SWT.SHELL_TRIM | SWT.RESIZE | SWT.MAX);
        this.cubridDatabases = cubridDatabases;
    }

    protected Control createDialogArea(Composite parent) {
        getShell().setText(Messages.runSQLDialogTitle);

        setTitle(Messages.runSQLDialogTitle);
        setMessage(Messages.runSQLDialogMessage);

        Composite comp = new Composite(parent, SWT.BORDER);
        comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        comp.setLayout(new GridLayout(6, false));

        Composite sqlFileComp = new Composite(comp, SWT.BORDER);
        GridData sqlFileCompGd = new GridData(GridData.FILL_BOTH);
        sqlFileCompGd.horizontalSpan = 6;
        sqlFileComp.setLayoutData(sqlFileCompGd);
        sqlFileComp.setLayout(new GridLayout(2, false));

        new Label(sqlFileComp, SWT.NONE).setText(Messages.runSQLDialogFilePathLabel);
        Button sqlFileButton = new Button(sqlFileComp, SWT.NONE);
        sqlFileButton.setText(Messages.btnAdd);
        sqlFileButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        FileDialog dialog = new FileDialog(getShell(), SWT.OPEN | SWT.MULTI);
                        dialog.setFilterPath(
                                PersistUtils.getPreferenceValue(
                                        CommonUIPlugin.PLUGIN_ID, SQLFILEPATH));
                        dialog.setText(Messages.runSQLSelectFiles);
                        dialog.setFilterExtensions(new String[] {"*.sql"});
                        dialog.setOverwrite(false);
                        String filePath = dialog.open();
                        if (filePath != null) {
                            String[] files = dialog.getFileNames();

                            for (int i = 0; i < files.length; i++) {
                                String fileName =
                                        dialog.getFilterPath() + File.separator + files[i];
                                if (!filesList.contains(fileName)) {
                                    filesList.add(fileName);
                                }
                            }

                            PersistUtils.setPreferenceValue(
                                    CommonUIPlugin.PLUGIN_ID, SQLFILEPATH, filePath);

                            saveErrExcelPath.setText(dialog.getFilterPath());
                            sqlFileTableViewer.setInput(filesList);
                        }
                    }
                });

        Composite textComp = new Composite(sqlFileComp, SWT.NONE);
        GridData textCompGd = new GridData(GridData.FILL_BOTH);
        textCompGd.horizontalSpan = 2;
        textCompGd.heightHint = 100;
        textComp.setLayoutData(textCompGd);
        textComp.setLayout(new GridLayout(1, true));

        sqlFileTableViewer = new TableViewer(textComp, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        sqlFileTableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
        sqlFileTableViewer.getTable().setLinesVisible(true);
        sqlFileTableViewer.getTable().setHeaderVisible(false);

        final TableViewerColumn fileNameCol = new TableViewerColumn(sqlFileTableViewer, SWT.NONE);
        fileNameCol.getColumn().setWidth(500);
        fileNameCol.getColumn().setText("");

        sqlFileTableViewer.setContentProvider(new SQLFileTableContentProvider());
        sqlFileTableViewer.setLabelProvider(new SQLFileTableLabelProvider());

        Composite databaseComp = new Composite(comp, SWT.BORDER);
        GridData databaseCompGd = new GridData(GridData.FILL_BOTH);
        databaseCompGd.horizontalSpan = 6;
        databaseCompGd.heightHint = 100;
        databaseComp.setLayoutData(databaseCompGd);
        databaseComp.setLayout(new GridLayout(1, false));

        new Label(databaseComp, SWT.NONE).setText(Messages.runSQLDialogDatabaseLabel);

        databaseTableComp = new Composite(databaseComp, SWT.NONE);
        GridData databaseTableCompGd = new GridData(GridData.FILL_BOTH);
        databaseTableCompGd.horizontalSpan = 1;
        databaseTableCompGd.heightHint = 100;
        databaseTableComp.setLayoutData(databaseTableCompGd);
        databaseTableComp.setLayout(new GridLayout(1, false));

        databaseTableViewer =
                new TableViewer(databaseTableComp, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        databaseTableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
        databaseTableViewer.getTable().setLinesVisible(true);
        databaseTableViewer.getTable().setHeaderVisible(false);

        final TableViewerColumn databaseCol = new TableViewerColumn(databaseTableViewer, SWT.NONE);
        databaseCol.getColumn().setWidth(500);
        databaseCol.getColumn().setText("");

        databaseTableViewer.setContentProvider(new DatabaseTableContentProvider());
        databaseTableViewer.setLabelProvider(new DatabaseTableLabelProvider());

        new Label(comp, SWT.NONE)
                .setText(com.cubrid.common.ui.cubrid.table.Messages.lblFileCharset);
        fileCharsetCombo = new Combo(comp, SWT.NONE);
        {
            GridData gridData = new GridData(GridData.BEGINNING);
            fileCharsetCombo.setLayoutData(gridData);
            fileCharsetCombo.setItems(QueryOptions.getAllCharset(null));
            fileCharsetCombo.select(0);
        }

        Label threadCountLabel = new Label(comp, SWT.NONE);
        threadCountLabel.setText(Messages.runSQLDialogLabelThreadCount);
        threadCountLabel.setToolTipText(Messages.runSQLDialogLabelThreadCountTooltip);
        threadCountSpinner = new Spinner(comp, SWT.BORDER | SWT.LEFT);
        threadCountSpinner.setValues(1, 1, 50, 0, 1, 1);
        threadCountSpinner.setToolTipText(Messages.runSQLDialogLabelThreadCountTooltip);

        Label commitCountLabel = new Label(comp, SWT.NONE);
        commitCountLabel.setText(Messages.runSQLDialogLabelCommitCount);
        commitCountLabel.setToolTipText(Messages.runSQLDialogLabelCommitCountTooltip);
        commitCountSpinner = new Spinner(comp, SWT.BORDER | SWT.LEFT);
        commitCountSpinner.setValues(1000, 1, 10000, 0, 1, 1000);
        commitCountSpinner.setToolTipText(Messages.runSQLDialogLabelCommitCountTooltip);

        Composite autoSaveComp = new Composite(comp, SWT.BORDER);
        GridData autoSaveCompGd = new GridData(GridData.FILL_BOTH);
        autoSaveCompGd.horizontalSpan = 6;
        autoSaveComp.setLayoutData(autoSaveCompGd);
        autoSaveComp.setLayout(new GridLayout(3, false));

        Composite autoSaveBtnComp = new Composite(autoSaveComp, SWT.NONE);
        GridData autoSaveBtnCompGd = new GridData(GridData.FILL_BOTH);
        autoSaveBtnCompGd.horizontalSpan = 3;
        autoSaveBtnComp.setLayoutData(autoSaveBtnCompGd);
        autoSaveBtnComp.setLayout(new GridLayout(2, false));

        new Label(autoSaveBtnComp, SWT.NONE).setText(Messages.runSQLDialogCheckBtnDescription);
        new Label(autoSaveComp, SWT.NONE).setText(Messages.runSQLDialogExcelPathLabel);

        saveErrExcelPath = new Text(autoSaveComp, SWT.BORDER);
        saveErrExcelPath.setLayoutData(new GridData(GridData.FILL_BOTH));
        saveErrExcelPath.setEditable(false);

        saveErrExcelBtn = new Button(autoSaveComp, SWT.NONE);
        saveErrExcelBtn.setText(Messages.brokerLogTopMergeOpenBtn);
        saveErrExcelBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        DirectoryDialog dialog =
                                new DirectoryDialog(
                                        PlatformUI.getWorkbench().getDisplay().getActiveShell());
                        dialog.setFilterPath(saveErrExcelPath.getText());

                        String dir = dialog.open();
                        if (dir != null) {
                            if (!dir.endsWith(File.separator)) {
                                dir += File.separator;
                            }
                            saveErrExcelPath.setText(dir);
                        }
                    }
                });

        registerContextMenu();
        setInput();

        return parent;
    }

    public void setInput() {
        databaseTableViewer.setInput(cubridDatabases);
    }

    public void addDatabase(Object[] selectedNodes) {
        List<CubridDatabase> dbList = new ArrayList<CubridDatabase>();
        for (CubridDatabase db : cubridDatabases) {
            dbList.add(db);
        }

        for (Object o : selectedNodes) {
            if (!(o instanceof ISchemaNode)) {
                continue;
            }

            ICubridNode node = (ICubridNode) o;
            if (node.getType() != NodeType.DATABASE) {
                continue;
            }

            CubridDatabase database = ((ISchemaNode) o).getDatabase();
            if (!dbList.contains(database)) {
                dbList.add(database);
            }
        }
        cubridDatabases = dbList.toArray(new CubridDatabase[dbList.size()]);
        databaseTableViewer.setInput(cubridDatabases);
    }

    /** register context menu */
    private void registerContextMenu() {
        sqlFileTableViewer
                .getTable()
                .addFocusListener(
                        new FocusAdapter() {
                            public void focusGained(FocusEvent event) {
                                ActionManager.getInstance()
                                        .changeFocusProvider(sqlFileTableViewer.getTable());
                            }
                        });

        MenuManager menuManager = new MenuManager();
        menuManager.setRemoveAllWhenShown(true);

        Menu contextMenu = menuManager.createContextMenu(sqlFileTableViewer.getTable());
        sqlFileTableViewer.getTable().setMenu(contextMenu);

        Menu menu = new Menu(getShell(), SWT.POP_UP);
        final MenuItem itemDelete = new MenuItem(menu, SWT.PUSH);
        itemDelete.setText("delete");
        itemDelete.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        List<String> deleteFiles = new ArrayList<String>();
                        for (int i = 0;
                                i < sqlFileTableViewer.getTable().getSelectionIndices().length;
                                i++) {
                            int index = sqlFileTableViewer.getTable().getSelectionIndices()[i];
                            deleteFiles.add(filesList.get(index));
                        }

                        filesList.removeAll(deleteFiles);
                        sqlFileTableViewer.setInput(filesList);
                        sqlFileTableViewer.refresh();
                    }
                });

        sqlFileTableViewer.getTable().setMenu(menu);

        Menu dbTablecontextMenu = menuManager.createContextMenu(databaseTableViewer.getTable());
        databaseTableViewer.getTable().setMenu(dbTablecontextMenu);

        Menu dbTableMenu = new Menu(getShell(), SWT.POP_UP);
        final MenuItem dbTableItemDelete = new MenuItem(dbTableMenu, SWT.PUSH);
        dbTableItemDelete.setText("delete");
        dbTableItemDelete.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        List<CubridDatabase> deleteCubridDatabase = new ArrayList<CubridDatabase>();
                        for (int i = 0;
                                i < databaseTableViewer.getTable().getSelectionIndices().length;
                                i++) {
                            int index = databaseTableViewer.getTable().getSelectionIndices()[i];
                            deleteCubridDatabase.add(cubridDatabases[index]);
                        }

                        List<CubridDatabase> dbList = Arrays.asList(cubridDatabases);

                        ArrayList<CubridDatabase> newDbList = new ArrayList<CubridDatabase>();
                        for (CubridDatabase cubridDatabase : dbList) {
                            if (deleteCubridDatabase.contains(cubridDatabase)) {
                                continue;
                            }
                            newDbList.add(cubridDatabase);
                        }
                        cubridDatabases = newDbList.toArray(new CubridDatabase[newDbList.size()]);
                        databaseTableViewer.setInput(cubridDatabases);
                        databaseTableViewer.refresh();
                    }
                });

        sqlFileTableViewer.getTable().setMenu(menu);
        databaseTableViewer.getTable().setMenu(dbTableMenu);

        runSQLFileDialogDNDController = new RunSQLFileDialogDNDController(this);
        runSQLFileDialogDNDController.registerDropTarget();
    }

    /**
     * When press button,call it
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            if (!validate()) {
                return;
            }

            for (CubridDatabase database : cubridDatabases) {
                RunSQLFileEditorInput input =
                        new RunSQLFileEditorInput(
                                database,
                                filesList,
                                fileCharsetCombo.getText(),
                                threadCountSpinner.getSelection(),
                                commitCountSpinner.getSelection(),
                                saveErrExcelPath.getText());
                try {
                    IWorkbench workbench = PlatformUI.getWorkbench();
                    IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
                    workbenchWindow.getActivePage().openEditor(input, RunSQLFileViewPart.ID);
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            }
        }

        setReturnCode(buttonId);
        close();
    }

    public boolean validate() {
        if (filesList.size() == 0) {
            setErrorMessage(Messages.runSQLDialogErrMsg1);
            return false;
        }
        return true;
    }

    /**
     * SQLFileTableContentProvider
     *
     * @author fulei
     */
    static class SQLFileTableContentProvider implements IStructuredContentProvider {
        public void dispose() {}

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

        @SuppressWarnings("all")
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof List) {
                List<String> list = (List<String>) inputElement;
                String[] filesArray = new String[list.size()];
                return list.toArray(filesArray);
            }

            return new Object[] {};
        }
    }

    /**
     * SQLFileTableLabelProvider
     *
     * @author fulei
     */
    static class SQLFileTableLabelProvider extends LabelProvider implements ITableLabelProvider {

        /**
         * Default return null
         *
         * @param element to be display.
         * @param columnIndex is the index of column. Begin with 0.
         * @return null
         */
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        /**
         * Retrieves the column's text by column index
         *
         * @param element to be displayed.
         * @param columnIndex is the index of column. Begin with 0.
         * @return String to be filled in the column.
         */
        public String getColumnText(Object element, int columnIndex) {
            String fileName = (String) element;
            switch (columnIndex) {
                case 0:
                    return fileName;
                default:
                    return null;
            }
        }
    }

    /**
     * DatabaseTableContentProvider
     *
     * @author fulei
     */
    static class DatabaseTableContentProvider implements IStructuredContentProvider {
        public void dispose() {}

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

        @SuppressWarnings("all")
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof CubridDatabase[]) {
                CubridDatabase[] cubridDatabases = (CubridDatabase[]) inputElement;
                return cubridDatabases;
            }

            return new Object[] {};
        }
    }

    /**
     * DatabaseTableLabelProvider
     *
     * @author fulei
     */
    static class DatabaseTableLabelProvider extends LabelProvider implements ITableLabelProvider {
        /**
         * Default return null
         *
         * @param element to be display.
         * @param columnIndex is the index of column. Begin with 0.
         * @return null
         */
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        /**
         * Retrieves the column's text by column index
         *
         * @param element to be displayed.
         * @param columnIndex is the index of column. Begin with 0.
         * @return String to be filled in the column.
         */
        public String getColumnText(Object element, int columnIndex) {
            CubridDatabase database = (CubridDatabase) element;
            if (database == null) {
                LOGGER.error("The database is a null.");
                return "";
            }

            DatabaseInfo dbInfo = database.getDatabaseInfo();
            if (dbInfo == null) {
                LOGGER.error("The database.getDatabaseInfo() is a null.");
                return "";
            }

            if (columnIndex != 0) {
                return null;
            }

            return database.getName() + "@" + dbInfo.getBrokerIP();
        }
    }

    public Spinner getCommitCountSpinner() {
        return commitCountSpinner;
    }

    public Combo getFileCharsetCombo() {
        return fileCharsetCombo;
    }

    public Text getSaveErrExcelPath() {
        return saveErrExcelPath;
    }

    public Composite getDatabaseTableComp() {
        return databaseTableComp;
    }

    public Spinner getThreadCountSpinner() {
        return threadCountSpinner;
    }
}
