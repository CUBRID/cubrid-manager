/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.cubrid.table.dialog.imp;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TableItem;

/**
 * The Import Setting Page
 *
 * @author Kevin.Wang
 * @version 1.0 - Jul 30, 2012 created by Kevin.Wang
 */
public class ImportSettingSQLPage extends AbsImportSettingPage
        implements ISelectionChangedListener {
    public static final String PAGE_NAME = ImportSettingSQLPage.class.getName();
    private TableViewer sqlFileTableViewer;
    private List<TableConfig> fileList = new ArrayList<TableConfig>();
    private CubridDatabase database;

    static final String[] TYPE_ITEMS = {"Schema", "Data", "Index"};
    static final String PROP_NAME = "filePath";
    static final String PROP_TYPE = "fileType";
    static final String[] PROPS = {PROP_NAME, PROP_TYPE};
    private Combo dbCharsetCombo;
    private Combo fileCharsetCombo;
    private Button delFileButton;
    private Spinner threadCountSpinner;
    private Spinner commitCountSpinner;

    protected ImportSettingSQLPage(CubridDatabase database) {
        super(PAGE_NAME, Messages.titleImportSettingPage, null);
        setTitle(Messages.titleImportStep2);
        setMessage(Messages.msgImportSettingPage);

        this.database = database;
    }

    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FormLayout());
        setControl(container);

        Composite leftComposite = new Composite(container, SWT.NONE);
        leftComposite.setLayout(new GridLayout(1, false));
        FormData leftData = new FormData();
        leftData.top = new FormAttachment(0, 5);
        leftData.bottom = new FormAttachment(100, 0);
        leftData.left = new FormAttachment(0, 5);
        leftData.right = new FormAttachment(55, 0);
        leftComposite.setLayoutData(leftData);

        Group grpButtons = new Group(leftComposite, SWT.NONE);
        {
            grpButtons.setLayout(new GridLayout(4, false));
            grpButtons.setLayoutData(
                    CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
            grpButtons.setText(Messages.grpAddRemove);
        }

        Button sqlSchemaFileButton = new Button(grpButtons, SWT.NONE);
        sqlSchemaFileButton.setText(Messages.btnAddSchemaFile);
        sqlSchemaFileButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        sqlSchemaFileButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        addFile(TableConfig.TYPE_SCHEMA);
                    }
                });

        Button sqlDataFileButton = new Button(grpButtons, SWT.NONE);
        sqlDataFileButton.setText(Messages.btnAddDataFile);
        sqlDataFileButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        sqlDataFileButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        addFile(TableConfig.TYPE_DATA);
                    }
                });

        Button sqlIndexFileButton = new Button(grpButtons, SWT.NONE);
        sqlIndexFileButton.setText(Messages.btnAddIndexFile);
        sqlIndexFileButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        sqlIndexFileButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        addFile(TableConfig.TYPE_INDEX);
                    }
                });

        delFileButton = new Button(grpButtons, SWT.NONE);
        delFileButton.setText(Messages.btnDelFiles);
        delFileButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        delFileButton.setEnabled(false);
        delFileButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        deleteSelectedFile();
                    }
                });

        sqlFileTableViewer =
                new TableViewer(leftComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        sqlFileTableViewer
                .getTable()
                .setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 4, 1, -1, -1));
        sqlFileTableViewer.getTable().setLinesVisible(true);
        sqlFileTableViewer.getTable().setHeaderVisible(true);

        final TableViewerColumn fileNameCol = new TableViewerColumn(sqlFileTableViewer, SWT.NONE);
        fileNameCol.getColumn().setWidth(300);
        fileNameCol.getColumn().setText(Messages.columnImportFileName);

        final TableViewerColumn fileTypeCol = new TableViewerColumn(sqlFileTableViewer, SWT.NONE);
        fileTypeCol.getColumn().setWidth(100);
        fileTypeCol.getColumn().setText(Messages.columnImportFileType);

        CellEditor[] editors = new CellEditor[4];
        editors[0] = new TextCellEditor(sqlFileTableViewer.getTable());
        editors[1] =
                new ComboBoxCellEditor(sqlFileTableViewer.getTable(), TYPE_ITEMS, SWT.READ_ONLY);

        sqlFileTableViewer.setColumnProperties(PROPS);
        sqlFileTableViewer.setContentProvider(new SQLFileTableContentProvider());
        sqlFileTableViewer.setLabelProvider(new SQLFileTableLabelProvider());
        sqlFileTableViewer.setCellModifier(new TableConfigCellEditor(sqlFileTableViewer, this));
        sqlFileTableViewer.setCellEditors(editors);
        sqlFileTableViewer
                .getTable()
                .addSelectionListener(
                        new SelectionListener() {
                            public void widgetSelected(SelectionEvent e) {
                                widgetDefaultSelected(e);
                            }

                            public void widgetDefaultSelected(SelectionEvent e) {
                                delFileButton.setEnabled(delCanEnable());
                            }
                        });
        Composite rightComposite = new Composite(container, SWT.NONE);
        FormData rightData = new FormData();
        rightData.top = new FormAttachment(0, 5);
        rightData.bottom = new FormAttachment(100, 0);
        rightData.left = new FormAttachment(55, 0);
        rightData.right = new FormAttachment(100, -5);
        rightComposite.setLayoutData(rightData);
        GridLayout rightCompositeLayout = new GridLayout();
        rightCompositeLayout.verticalSpacing = 10;
        rightComposite.setLayout(rightCompositeLayout);

        Group enCodingOptionGroup = new Group(rightComposite, SWT.None);
        enCodingOptionGroup.setText(Messages.grpEncodingOption);
        enCodingOptionGroup.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        enCodingOptionGroup.setLayout(new GridLayout(4, false));

        Label dbCharsetLabel = new Label(enCodingOptionGroup, SWT.None);
        dbCharsetLabel.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));
        dbCharsetLabel.setText(Messages.lblDBCharset);

        dbCharsetCombo = new Combo(enCodingOptionGroup, SWT.BORDER);
        dbCharsetCombo.setLayoutData(CommonUITool.createGridData(1, 1, 50, 21));
        dbCharsetCombo.setEnabled(false);

        Label fileCharsetLabel = new Label(enCodingOptionGroup, SWT.None);
        fileCharsetLabel.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));
        fileCharsetLabel.setText(Messages.lblFileCharset);

        fileCharsetCombo = new Combo(enCodingOptionGroup, SWT.BORDER);
        fileCharsetCombo.setLayoutData(CommonUITool.createGridData(1, 1, 50, 21));
        fileCharsetCombo.setItems(QueryOptions.getAllCharset(null));
        fileCharsetCombo.select(0);
        fileCharsetCombo.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        getImportDataWizard()
                                .getImportConfig()
                                .setFilesCharset(fileCharsetCombo.getText());
                    }
                });

        Group importOptionGroup = new Group(rightComposite, SWT.None);
        importOptionGroup.setText(Messages.grpImportOptions);
        importOptionGroup.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        importOptionGroup.setLayout(new GridLayout(2, false));

        Label threadCountLabel = new Label(importOptionGroup, SWT.None);
        threadCountLabel.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));
        threadCountLabel.setText(Messages.lblThreadNum);

        threadCountSpinner = new Spinner(importOptionGroup, SWT.BORDER);
        threadCountSpinner.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        threadCountSpinner.setMaximum(ImportConfig.MAX_IMPORT_THREAD_COUNT);
        threadCountSpinner.setMinimum(ImportConfig.MIN_IMPORT_THREAD_COUNT);
        threadCountSpinner.setSelection(ImportConfig.DEFAULT_IMPORT_THREAD_COUNT);
        threadCountSpinner.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        validate();
                    }
                });

        Label commitCountLabel = new Label(importOptionGroup, SWT.None);
        commitCountLabel.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));
        commitCountLabel.setText(Messages.lblCommitCount);

        commitCountSpinner = new Spinner(importOptionGroup, SWT.BORDER);
        commitCountSpinner.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        commitCountSpinner.setMaximum(ImportConfig.MAX_IMPORT_COMMIT_COUNT);
        commitCountSpinner.setMinimum(ImportConfig.MIN_IMPORT_COMMIT_COUNT);
        commitCountSpinner.setIncrement(ImportConfig.IMPORT_COMMIT_STEP);
        commitCountSpinner.setSelection(ImportConfig.DEFAULT_IMPORT_COMMIT_COUNT);

        final Button haModeButton = new Button(rightComposite, SWT.CHECK);
        haModeButton.setText(Messages.btnInHaMode);
        haModeButton.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_END, 1, 1, -1, -1));
        haModeButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
                        ImportConfig importConfig = getImportDataWizard().getImportConfig();
                        importConfig.setHaMode(haModeButton.getSelection());

                        Collections.sort(
                                fileList,
                                new TableConfigComparator(getImportDataWizard().getImportConfig()));
                        sqlFileTableViewer.refresh();
                    }
                });

        registerContextMenu();
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

        Menu menu = new Menu(getShell(), SWT.POP_UP);

        final MenuItem addSchemaItem = new MenuItem(menu, SWT.PUSH);
        addSchemaItem.setText(Messages.btnAddSchemaFile);
        addSchemaItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        addFile(TableConfig.TYPE_SCHEMA);
                    }
                });

        final MenuItem addDataItem = new MenuItem(menu, SWT.PUSH);
        addDataItem.setText(Messages.btnAddDataFile);
        addDataItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        addFile(TableConfig.TYPE_DATA);
                    }
                });

        final MenuItem addIndexItem = new MenuItem(menu, SWT.PUSH);
        addIndexItem.setText(Messages.btnAddIndexFile);
        addIndexItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        addFile(TableConfig.TYPE_INDEX);
                    }
                });

        new MenuItem(menu, SWT.SEPARATOR);

        final MenuItem delItem = new MenuItem(menu, SWT.PUSH);
        delItem.setText(Messages.btnDelFiles);
        delItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        deleteSelectedFile();
                    }
                });

        menu.addMenuListener(
                new MenuListener() {
                    public void menuHidden(org.eclipse.swt.events.MenuEvent e) {}

                    public void menuShown(org.eclipse.swt.events.MenuEvent e) {
                        delItem.setEnabled(delCanEnable());
                    }
                });

        sqlFileTableViewer.getTable().setMenu(menu);
    }

    /** Delete the selected files */
    private void deleteSelectedFile() {
        TableItem[] items = sqlFileTableViewer.getTable().getSelection();
        for (TableItem item : items) {
            fileList.remove(item.getData());
        }

        Collections.sort(
                fileList, new TableConfigComparator(getImportDataWizard().getImportConfig()));
        sqlFileTableViewer.refresh();
        validate();
    }

    /**
     * Judge the delete button is can enable
     *
     * @return
     */
    private boolean delCanEnable() {
        TableItem[] items = sqlFileTableViewer.getTable().getSelection();
        if (items.length > 0) {
            return true;
        } else {
            return false;
        }
    }

    /** Add sql files */
    private void addFile(String fileType) {
        FileDialog dialog = new FileDialog(getShell(), SWT.OPEN | SWT.MULTI);
        dialog.setText(Messages.titleImportSelectSqlFile);
        dialog.setFilterExtensions(new String[] {"*.sql"});
        dialog.setOverwrite(false);
        String filePath = dialog.open();
        if (filePath != null) {
            String[] files = dialog.getFileNames();
            // Add the selected files
            for (int i = 0; i < files.length; i++) {
                String fileName = dialog.getFilterPath() + File.separator + files[i];
                TableConfig fileConfig = new TableConfig(files[i]);
                fileConfig.setFilePath(fileName);
                fileConfig.setFileType(fileType);
                fileConfig.setLineCount(TableConfig.LINT_COUNT_UNKNOW);
                fileList.add(fileConfig);
            }
            Collections.sort(
                    fileList, new TableConfigComparator(getImportDataWizard().getImportConfig()));
            sqlFileTableViewer.refresh();
            validate();
        }
    }

    /** Init the data */
    protected void init() {
        ImportConfig importConfig = getImportDataWizard().getImportConfig();

        String charset = "";
        if (database.getDatabaseInfo().getCharSet() != null) {
            charset = database.getDatabaseInfo().getCharSet();
        }
        dbCharsetCombo.setText(charset);
        fileCharsetCombo.setText(importConfig.getFilesCharset());
        threadCountSpinner.setSelection(importConfig.getThreadCount());
        commitCountSpinner.setSelection(importConfig.getCommitLine());

        fileList.clear();
        fileList.addAll(importConfig.getSelectedMap().values());
        sqlFileTableViewer.setInput(fileList);

        validate();
    }

    public boolean validate() {
        setErrorMessage(null);
        setPageComplete(false);

        if (fileList.size() == 0) {
            setErrorMessage(Messages.titleImportSelectSqlFile);
            return false;
        }

        Set<String> fileSet = new HashSet<String>();
        for (TableConfig config : fileList) {
            /*Check the file exist*/
            File file = new File(config.getFilePath());
            if (!file.exists()) {
                setErrorMessage(Messages.bind(Messages.errFileNotExist, config.getFilePath()));
                return false;
            }
            /*Check the file repeat*/
            if (!fileSet.contains(config.getFilePath())) {
                fileSet.add(config.getFilePath());
            } else {
                setErrorMessage(Messages.bind(Messages.errFileRepeat, config.getFilePath()));
                return false;
            }
        }

        setPageComplete(true);
        return true;
    }

    protected void handlePageLeaving(PageChangingEvent event) {
        if ((event.getTargetPage() instanceof ImportTypePage)) {
            if (!CommonUITool.openConfirmBox(Messages.importWizardBackConfirmMsg)) {
                event.doit = false;
            } else {
                clearOptions();
            }
            return;
        }

        ImportConfig importConfig = getImportDataWizard().getImportConfig();
        importConfig.setFilesCharset(fileCharsetCombo.getText());
        importConfig.setThreadCount(threadCountSpinner.getSelection());
        importConfig.setCommitLine(commitCountSpinner.getSelection());
        importConfig.getSelectedMap().clear();

        for (TableConfig config : fileList) {
            importConfig.addTableConfig(config);
        }
    }

    /* (non-Javadoc)
     * @see com.cubrid.common.ui.cubrid.table.dialog.imp.ISelectionChangedListener#selectionChanged()
     */
    public void selectionChanged() {}

    /** clear all options */
    public void clearOptions() {
        this.setInited(false);
    }
    
    @Override
    protected void afterShowCurrentPage(PageChangedEvent event) {
        super.afterShowCurrentPage(event);
        getShell().setMinimumSize(880, 420);
    }
}

/**
 * SQLFileTableContentProvider
 *
 * @author fulei
 */
class SQLFileTableContentProvider implements IStructuredContentProvider {

    public void dispose() {}

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

    @SuppressWarnings("all")
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof List) {
            return ((List) inputElement).toArray();
        }
        return new Object[0];
    }
}

/**
 * SQLFileTableLabelProvider
 *
 * @author fulei
 */
class SQLFileTableLabelProvider extends LabelProvider implements ITableLabelProvider {

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
        TableConfig config = (TableConfig) element;
        switch (columnIndex) {
            case 0:
                return config.getName();
            case 1:
                if (StringUtil.isEqual(TableConfig.TYPE_DATA, config.getFileType())) {
                    return "Data";
                } else if (StringUtil.isEqual(TableConfig.TYPE_SCHEMA, config.getFileType())) {
                    return "Schema";
                } else if (StringUtil.isEqual(TableConfig.TYPE_INDEX, config.getFileType())) {
                    return "Index";
                }
                return "";
        }
        return null;
    }
}

class TableConfigComparator implements Comparator<TableConfig> {
    private ImportConfig importConfig;

    public TableConfigComparator(ImportConfig importConfig) {
        this.importConfig = importConfig;
    }

    public int compare(TableConfig o1, TableConfig o2) {
        Integer o1Type = getTypeValue(o1);
        Integer o2Type = getTypeValue(o2);

        return o1Type.compareTo(o2Type);
    }

    private int getTypeValue(TableConfig config) {

        if (StringUtil.isEqual(TableConfig.TYPE_SCHEMA, config.getFileType())) {
            return 0;
        }

        if (importConfig.isHaMode()
                && StringUtil.isEqual(TableConfig.TYPE_INDEX, config.getFileType())) {
            return 1;
        }
        if (StringUtil.isEqual(TableConfig.TYPE_DATA, config.getFileType())) {
            return 2;
        }

        if (StringUtil.isEqual(TableConfig.TYPE_INDEX, config.getFileType())) {
            return 4;
        }
        return -1;
    }
}

class TableConfigCellEditor implements ICellModifier {
    private ImportSettingSQLPage importSettingSQLPage;
    private TableViewer tableViewer;

    public TableConfigCellEditor(
            TableViewer tableViewer, ImportSettingSQLPage importSettingSQLPage) {
        this.tableViewer = tableViewer;
        this.importSettingSQLPage = importSettingSQLPage;
    }

    public boolean canModify(Object element, String property) {
        if (ImportSettingSQLPage.PROP_TYPE.equals(property)) {
            return true;
        }
        return false;
    }

    public Object getValue(Object element, String property) {
        TableConfig fileConfig = (TableConfig) element;

        if (ImportSettingSQLPage.PROP_NAME.equals(property)) {
            return fileConfig.getFilePath();
        }
        if (ImportSettingSQLPage.PROP_TYPE.equals(property)) {
            if (StringUtil.isEqual(TableConfig.TYPE_SCHEMA, fileConfig.getFileType())) {
                return 0;
            } else if (StringUtil.isEqual(TableConfig.TYPE_DATA, fileConfig.getFileType())) {
                return 1;
            } else if (StringUtil.isEqual(TableConfig.TYPE_INDEX, fileConfig.getFileType())) {
                return 2;
            }

            return 0;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public void modify(Object element, String property, Object value) {
        TableConfig config = (TableConfig) ((Item) element).getData();

        if (ImportSettingSQLPage.PROP_NAME.equals(property)) {
            config.setFilePath(value.toString());
        }
        if (ImportSettingSQLPage.PROP_TYPE.equals(property)) {
            if (StringUtil.isEqualNotIgnoreNullIgnoreCase(value.toString(), "0")) {
                config.setFileType(TableConfig.TYPE_SCHEMA);
            } else if (StringUtil.isEqualNotIgnoreNullIgnoreCase(value.toString(), "1")) {
                config.setFileType(TableConfig.TYPE_DATA);
            } else if (StringUtil.isEqualNotIgnoreNullIgnoreCase(value.toString(), "2")) {
                config.setFileType(TableConfig.TYPE_INDEX);
            }
            List<TableConfig> fileList = (List<TableConfig>) tableViewer.getInput();
            Collections.sort(
                    fileList,
                    new TableConfigComparator(
                            importSettingSQLPage.getImportDataWizard().getImportConfig()));
            importSettingSQLPage.validate();
        }
        tableViewer.refresh();
    }
}
