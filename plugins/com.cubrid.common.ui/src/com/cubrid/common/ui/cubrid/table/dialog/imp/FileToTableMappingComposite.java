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
package com.cubrid.common.ui.cubrid.table.dialog.imp;

import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.ConstantsUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.cubrid.table.dialog.PstmtParameter;
import com.cubrid.common.ui.cubrid.table.importhandler.ImportFileDescription;
import com.cubrid.common.ui.cubrid.table.importhandler.ImportFileHandler;
import com.cubrid.common.ui.cubrid.table.importhandler.ImportFileHandlerFactory;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.FieldHandlerUtils;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.slf4j.Logger;

/**
 * The File To Table Mapping Composite
 *
 * @author Kevin.Wang
 * @version 1.0 - Aug 1, 2012 created by Kevin.Wang
 */
public class FileToTableMappingComposite extends Composite {
    private static final Logger LOGGER = LogUtil.getLogger(FileToTableMappingComposite.class);
    public static final String NEW_ATTR_TYPE = "Varchar(4096)";
    private List<ISelectionChangedListener> listenerList =
            new ArrayList<ISelectionChangedListener>();
    private CubridDatabase database;
    private CheckboxTreeViewer treeViewer;
    private Button createTableButton;
    private int lineCount = 0;
    private List<ICubridNode> allTableList;
    private List<String> failedFileList = new ArrayList<String>();
    private ICubridNode classNode;
    private ImportFileDescription importFileDescription;
    private AbsImportSettingPage importSettingPage;

    public FileToTableMappingComposite(
            Composite parent,
            int style,
            CubridDatabase database,
            final AbsImportSettingPage importSettingPage) {
        super(parent, style);
        this.database = database;
        this.importSettingPage = importSettingPage;

        GridLayout layout = new GridLayout(1, false);
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = 0;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        setLayout(layout);

        Button addFileButton = new Button(this, SWT.None);
        addFileButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        addFileButton.setText(Messages.btnAddFiles);
        addFileButton.addSelectionListener(
                new SelectionListener() {
                    public void widgetDefaultSelected(SelectionEvent e) {
                        widgetSelected(e);
                    }

                    public void widgetSelected(SelectionEvent arg0) {
                        openFileDialog(null);
                    }
                });

        Label tableInfoLabel = new Label(this, SWT.None);
        tableInfoLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        tableInfoLabel.setText(Messages.lblImportMapping);

        treeViewer = new CheckboxTreeViewer(this, SWT.BORDER | SWT.FULL_SELECTION | SWT.CHECK);
        treeViewer
                .getTree()
                .setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));

        treeViewer.getTree().setLinesVisible(true);
        treeViewer.getTree().setHeaderVisible(true);
        treeViewer.setLabelProvider(new ImportObjectLabelProvider());

        /*Table Name*/
        TreeViewerColumn tableNameColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
        tableNameColumn.getColumn().setText(Messages.columnTableName);
        tableNameColumn.getColumn().setWidth(200);
        tableNameColumn.setLabelProvider(new ImportObjectLabelProvider());

        /*Path*/
        TreeViewerColumn fileColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
        fileColumn.getColumn().setText(Messages.columnFileName);
        fileColumn.getColumn().setWidth(90);
        fileColumn.setLabelProvider(new ImportObjectLabelProvider());

        /*Rows*/
        TreeViewerColumn rowColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
        rowColumn.getColumn().setText(Messages.columnRowCount);
        rowColumn.getColumn().setWidth(60);
        rowColumn.setLabelProvider(new ImportObjectLabelProvider());

        /*Mapped*/
        TreeViewerColumn mappedColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
        mappedColumn.getColumn().setText(Messages.columnMapped);
        mappedColumn.getColumn().setWidth(60);
        mappedColumn.setLabelProvider(new ImportObjectLabelProvider());

        /*Is use first line as column name*/
        TreeViewerColumn firstLineColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
        firstLineColumn.getColumn().setText(Messages.columnDiscardFirstLine);
        firstLineColumn.getColumn().setWidth(90);
        firstLineColumn.setLabelProvider(new ImportObjectLabelProvider());

        treeViewer.setContentProvider(new ImportTreeContentProvider());
        treeViewer.addDoubleClickListener(
                new IDoubleClickListener() {
                    public void doubleClick(DoubleClickEvent e) {
                        StructuredSelection section =
                                (StructuredSelection) e.getViewer().getSelection();
                        ICubridNode classNode = (ICubridNode) section.getFirstElement();
                        addFile(classNode);
                    }
                });

        treeViewer.addCheckStateListener(
                new ICheckStateListener() {
                    public void checkStateChanged(CheckStateChangedEvent event) {
                        computeLineCount();
                        /*Fire the selection changed*/
                        fireSelectionChanged();
                    }
                });

        createTableButton = new Button(this, SWT.CHECK);
        createTableButton.setText(Messages.btnCreateTable);
        createTableButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        createTableButton.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        ImportConfig importConfig =
                                importSettingPage.getImportDataWizard().getImportConfig();
                        importConfig.setCreateTableAccordingData(createTableButton.getSelection());
                    }
                });
    }

    public List<ICubridNode> loadClassInfo() { // FIXME move this logic to core module
        allTableList = new ArrayList<ICubridNode>();
        try {
            ProgressMonitorDialog progress =
                    new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
            progress.setCancelable(true);
            progress.run(
                    true,
                    true,
                    new IRunnableWithProgress() {
                        public void run(IProgressMonitor monitor) throws InvocationTargetException {
                            monitor.beginTask(Messages.taskLoading, 2);
                            Connection conn = null;
                            Statement stmt = null;
                            ResultSet rsTable = null;
                            try {
                                monitor.subTask(Messages.taskLoadingTable);
                                monitor.worked(1);

                                StringBuilder sql = new StringBuilder();

                                if (isSupportUserSchema()) {
                                    sql.append("SELECT c.class_name, c.owner_name ");
                                    sql.append("FROM db_class c, db_attribute a ");
                                    sql.append(
                                            "WHERE c.class_name = a.class_name AND c.is_system_class = 'NO' ");
                                    sql.append("AND c.owner_name = a.owner_name ");
                                    sql.append("AND a.from_class_name IS NULL ");
                                    sql.append("AND c.class_type = 'CLASS' ");
                                    sql.append("GROUP BY c.owner_name, c.class_name ");
                                    sql.append("ORDER BY c.owner_name, c.class_name");
                                } else {
                                    sql.append("SELECT c.class_name ");
                                    sql.append("FROM db_class c, db_attribute a ");
                                    sql.append(
                                            "WHERE c.class_name = a.class_name AND c.is_system_class = 'NO' ");
                                    sql.append("AND a.from_class_name IS NULL ");
                                    sql.append("AND c.class_type = 'CLASS' ");
                                    sql.append("GROUP BY c.class_name ");
                                    sql.append("ORDER BY c.class_name");
                                }

                                conn =
                                        JDBCConnectionManager.getConnection(
                                                database.getDatabaseInfo(), true);
                                stmt = conn.createStatement();
                                rsTable = stmt.executeQuery(sql.toString());

                                while (rsTable.next()) {
                                    String className = rsTable.getString(1); // $NON-NLS-1$
                                    String tableName;

                                    if (ConstantsUtil.isExtensionalSystemTable(className)) {
                                        continue;
                                    }

                                    if (isSupportUserSchema()) {
                                        tableName = rsTable.getString(2) + "." + className;
                                    } else {
                                        tableName = className;
                                    }

                                    String iconPath = "icons/navigator/schema_table_item.png";

                                    ICubridNode classNode =
                                            new DefaultSchemaNode(
                                                    tableName, tableName, tableName, iconPath);
                                    classNode.setContainer(true);
                                    allTableList.add(classNode);
                                }

                                monitor.subTask(Messages.taskLoadingColumn);
                                monitor.worked(1);
                            } catch (SQLException e) {
                                String msg =
                                        e.getErrorCode()
                                                + StringUtil.NEWLINE
                                                + Messages.importErrorHead
                                                + e.getMessage();
                                CommonUITool.openErrorBox(getShell(), msg);
                                LOGGER.error("", e);
                            } finally {
                                QueryUtil.freeQuery(conn, stmt, rsTable);
                            }
                        }
                    });
        } catch (Exception e) {
            LOGGER.error("", e);
        }

        return allTableList;
    }

    /** Init the data */
    public void init() {
        ImportConfig importConfig = importSettingPage.getImportDataWizard().getImportConfig();

        loadClassInfo();
        treeViewer.setInput(allTableList);
        treeViewer.setAllChecked(false);

        if (importConfig.isHistory() && !importConfig.isCheckMap()) {
            /* init tableViewer data */
            for (TableConfig config : importConfig.getSelectedMap().values()) {
                for (ICubridNode node : allTableList) {
                    if (config.getName().equals(node.getName())) {
                        node.setData(ImportObjectLabelProvider.IS_MAPPED, true);
                        File file = new File(config.getFilePath());
                        node.setData(ImportObjectLabelProvider.FILE_PAHT, file.getName());
                        node.setData(ImportObjectLabelProvider.ROW_COUNT, config.getLineCount());
                        node.setData(
                                ImportObjectLabelProvider.IS_USE_FRIST_LINE_AS_COLUMN_NAME,
                                config.isFirstRowAsColumn());
                        treeViewer.setChecked(node, true);
                    }
                }
            }
            importConfig.setCheckMap(true);
            computeLineCount();
            /*Fire the selection changed*/
            fireSelectionChanged();
        }
        treeViewer.refresh();
    }

    /** open file dialog */
    private String openFileDialog(String fileDir) {
        ImportConfig importConfig = importSettingPage.getImportDataWizard().getImportConfig();
        if (!checkDelimiter(importConfig)) {
            return "";
        }

        FileDialog dialog = new FileDialog(getShell(), SWT.MULTI | SWT.APPLICATION_MODAL);

        if (fileDir == null || fileDir.trim().length() == 0) {
            fileDir = CommonUIPlugin.getSettingValue(ImportDataWizard.SESSION_IMPORT_KEY);
        }

        String supportExtString = null; // FIXME can be duplicated to handle file extensions
        if (importConfig.getImportType() == ImportConfig.IMPORT_FROM_EXCEL) {
            dialog.setFilterExtensions(new String[] {"*.*", "*.csv" /*, "*.xlsx"*/, "*.xls"});
            dialog.setFilterNames(
                    new String[] {
                        Messages.allFileType, Messages.csvFileType /*,
					Messages.xlsxFileType*/, Messages.xlsFileType
                    });
            supportExtString = "csv, xls";
        } else if (importConfig.getImportType() == ImportConfig.IMPORT_FROM_TXT) {
            dialog.setFilterExtensions(new String[] {"*.txt", "*.*"});
            dialog.setFilterNames(new String[] {Messages.txtFileType, Messages.allFileType});
            supportExtString = "txt";
        } else if (importConfig.getImportType() == ImportConfig.IMPORT_FROM_SQL) {
            dialog.setFilterExtensions(new String[] {"*.sql", "*.*"});
            dialog.setFilterNames(new String[] {Messages.sqlFileType, Messages.allFileType});
            supportExtString = "sql";
        } else if (importConfig.getImportType() == ImportConfig.IMPORT_FROM_LOADDB) {
            dialog.setFilterExtensions(new String[] {"*.txt", "*.*"});
            dialog.setFilterNames(new String[] {Messages.txtFileType, Messages.allFileType});
            supportExtString = "LoadDB";
        }
        dialog.setFilterPath(fileDir);
        dialog.open();

        Set<String> tableSet = new HashSet<String>(); // FIXME move this logic to core module
        for (ICubridNode node : allTableList) {
            tableSet.add(node.getName());
        }
        StringBuilder newTables = new StringBuilder();

        String[] fileNames = dialog.getFileNames();
        String path = dialog.getFilterPath();
        Map<String, String> tableToFileMap = new HashMap<String, String>();
        if (fileNames.length > 0) {
            try {
                for (String fileName : fileNames) {
                    String tableName = fileName;
                    int dotIndex = fileName.lastIndexOf(".");
                    int schemaDotIndex = fileName.indexOf(".");
                    
                    if (dotIndex >= 0) {
                        tableName = fileName.substring(0, dotIndex);
                    }
                    
                    if (schemaDotIndex != dotIndex) {
                        String schema = tableName.substring(0, schemaDotIndex);
                        String table = tableName.substring(schemaDotIndex, tableName.length());
                        tableName = schema.toUpperCase() + table;
                    }
                    
                    String filePath = path + File.separator + fileName;

                    tableToFileMap.put(tableName, filePath);

                    if (!tableSet.contains(tableName)) {
                        if (newTables.length() > 0) {
                            newTables.append(", ");
                        }
                        newTables.append(tableName);
                    }
                }

                parseData(tableToFileMap);
            } catch (Exception ex) {
                String msg = Messages.errorOpenFile;
                if (supportExtString != null) {
                    msg += Messages.bind(Messages.errorOpenFileDetail, supportExtString);
                }
                CommonUITool.openErrorBox(msg);
                LOGGER.error("Open file failed:" + ex.getMessage(), ex);
            }

            computeLineCount();
            fireSelectionChanged();
        }

        if (newTables.length() > 0) {
            if (importConfig.isCreateTableAccordingData()) {
                CommonUITool.openWarningBox(
                        Messages.bind(Messages.warnImportNewTableDetectedCreate, newTables));
            } else {
                CommonUITool.openWarningBox(
                        Messages.bind(Messages.warnImportNewTableDetectedSkip, newTables));
            }
        }
        return "";
    }

    /**
     * Check the delimiter setting for ImportSettingTxtPage
     *
     * @param importConfig
     * @return
     */
    private boolean checkDelimiter(ImportConfig importConfig) {
        if (importSettingPage instanceof ImportSettingTxtPage) {
            if (importConfig.getRowDelimiter() == null
                    || importConfig.getRowDelimiter().length() == 0) {
                CommonUITool.openErrorBox(Messages.errRowDelimiterEmpty);
                return false;
            }

            if (importConfig.getColumnDelimiter() == null
                    || importConfig.getColumnDelimiter().length() == 0) {
                CommonUITool.openErrorBox(Messages.errColumnDelimiterEmpty);
                return false;
            }
        }
        return true;
    }

    /** Add a data file for the table */
    private void addFile(ICubridNode classNode) {
        ImportConfig importConfig = importSettingPage.getImportDataWizard().getImportConfig();

        if (!checkDelimiter(importConfig)) {
            return;
        }

        String tableName = classNode.getName();
        AddTableFileDialog addfileDialog =
                new AddTableFileDialog(this, database, tableName, importConfig);
        if (IDialogConstants.OK_ID == addfileDialog.open()) {
            TableConfig tableConfig = importConfig.getTableConfig(tableName);
            classNode.setData(
                    ImportObjectLabelProvider.IS_USE_FRIST_LINE_AS_COLUMN_NAME,
                    tableConfig.isFirstRowAsColumn());
            treeViewer.setChecked(classNode, true);
            treeViewer.refresh(classNode);

            computeLineCount();
            fireSelectionChanged();
        }
    }

    /** Compute all line count */
    private void computeLineCount() {
        ImportConfig importConfig = importSettingPage.getImportDataWizard().getImportConfig();
        /*Compute all the record count*/
        lineCount = 0;
        Object[] itemArray = treeViewer.getCheckedElements();
        for (Object object : itemArray) {
            DefaultSchemaNode node = (DefaultSchemaNode) object;
            TableConfig tableConfig = importConfig.getTableConfig(node.getName());
            if (tableConfig != null) {
                lineCount += tableConfig.getLineCount();
            }
        }
    }

    /**
     * Get the selected table's count
     *
     * @return
     */
    public int getSelectedTableCount() {
        return treeViewer.getCheckedElements().length;
    }

    /**
     * Get all line count
     *
     * @return
     */
    public int getLineCount() {
        return lineCount;
    }

    /**
     * Get is create table
     *
     * @return
     */
    public boolean isCreateTable() {
        return false;
    }

    /**
     * Add a listener
     *
     * @param listener
     */
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        listenerList.add(listener);
    }

    /**
     * Remove the listener
     *
     * @param listener
     */
    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        listenerList.remove(listener);
    }

    /** Fire the selection changed */
    public void fireSelectionChanged() {
        for (ISelectionChangedListener listener : listenerList) {
            listener.selectionChanged();
        }
    }

    public void resetMapResult() {
        if (allTableList == null) {
            return;
        }
        
        for (ICubridNode node : allTableList) {
            Object value = node.getData(ImportObjectLabelProvider.IS_MAPPED);
            if (value != null) {
                node.setData(ImportObjectLabelProvider.IS_MAPPED, false);
            }
        }
        treeViewer.refresh();
    }

    public List<DefaultSchemaNode> getSelectedTableNode() {
        List<DefaultSchemaNode> result = new ArrayList<DefaultSchemaNode>();

        Object[] itemArray = treeViewer.getCheckedElements();
        for (Object object : itemArray) {
            DefaultSchemaNode node = (DefaultSchemaNode) object;
            result.add(node);
        }

        return result;
    }

    public boolean validate() {
        List<DefaultSchemaNode> result = getSelectedTableNode();
        for (DefaultSchemaNode node : result) {
            Object value = node.getData(ImportObjectLabelProvider.IS_MAPPED);
            if (value == null || !Boolean.parseBoolean(value.toString())) {
                return false;
            }
        }
        return true;
    }

    private boolean isFirstLineAsColumnName(String tableName) {
        ImportConfig importConfig = importSettingPage.getImportDataWizard().getImportConfig();
        TableConfig tableConfig = importConfig.getSelectedMap().get(tableName);
        if (tableConfig != null) {
            return tableConfig.isFirstRowAsColumn();
        }
        return false;
    }

    /**
     * Judge is new class node
     *
     * @param node
     * @return
     */
    public boolean isNewClassNode(ICubridNode node) {
        Object isNew = node.getData(ImportObjectLabelProvider.IS_NEW);
        if (isNew == null || !(Boolean) isNew) {
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private void parseData(final Map<String, String> tableToFile) throws Exception {
        allTableList = (List<ICubridNode>) treeViewer.getInput();
        final List<ICubridNode> newMappedNodeList = new ArrayList<ICubridNode>();
        final ImportConfig importConfig = importSettingPage.getImportDataWizard().getImportConfig();

        ProgressMonitorDialog progress =
                new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
        progress.setCancelable(true);

        progress.run(
                true,
                true,
                new IRunnableWithProgress() {
                    public void run(IProgressMonitor monitor) throws InvocationTargetException {
                        monitor.beginTask(Messages.taskParsingData, tableToFile.size());
                        for (Entry<String, String> entry :
                                tableToFile.entrySet()) { // FIXME move this logic to core module
                            String tableName = entry.getKey();
                            String filePath = entry.getValue();

                            File file = new File(filePath);
                            String fileName = file.getName();

                            monitor.subTask(Messages.bind(Messages.taskParsingFile, fileName));

                            List<String> fileColumnList = new ArrayList<String>();
                            List<String> firstRowColsLst = new ArrayList<String>();
                            List<String> colNameList = new ArrayList<String>();
                            List<String> colTypeList = new ArrayList<String>();
                            int totalLine = 0;
                            List<Integer> itemsNumberOfSheets = null;

                            /*Find the class node*/
                            ICubridNode classNode = getTableNode(tableName);

                            /*Process file*/
                            ImportFileHandler importFileHandler =
                                    ImportFileHandlerFactory.getHandler(
                                            file.getAbsolutePath(), importConfig);
                            ImportFileDescription ifd = getFileDescription(importFileHandler);

                            firstRowColsLst.addAll(ifd.getFirstRowCols());
                            totalLine = ifd.getTotalCount();
                            itemsNumberOfSheets = ifd.getItemsNumberOfSheets();

                            boolean isFirstLineAsColumnName = isFirstLineAsColumnName(tableName);
                            fillInFromList(
                                    fileColumnList, firstRowColsLst, isFirstLineAsColumnName);

                            if (isFirstLineAsColumnName) {
                                handleSelectEventForFirstRowAsColBtn(
                                        itemsNumberOfSheets,
                                        fileColumnList,
                                        firstRowColsLst,
                                        totalLine,
                                        isFirstLineAsColumnName);
                            }

                            if (classNode != null) {
                                Object isNew = classNode.getData(ImportObjectLabelProvider.IS_NEW);
                                if (isNew == null || !(Boolean) isNew) {
                                    SchemaInfo schemaInfo =
                                            database.getDatabaseInfo().getSchemaInfo(tableName);
                                    List<DBAttribute> attributes =
                                            schemaInfo == null ? null : schemaInfo.getAttributes();
                                    if (attributes == null) {
                                        return;
                                    }
                                    for (DBAttribute attr : attributes) {
                                        String column = attr.getName();
                                        String dataType = attr.getType();
                                        colNameList.add(column);
                                        colTypeList.add(dataType);
                                    }
                                } else {
                                    removeClassNode(classNode);
                                    classNode =
                                            createClassNode(
                                                    tableName,
                                                    isFirstLineAsColumnName(tableName),
                                                    firstRowColsLst,
                                                    colNameList,
                                                    colTypeList);
                                }
                            } else if (importConfig.isCreateTableAccordingData()) {
                                classNode =
                                        createClassNode(
                                                tableName,
                                                isFirstLineAsColumnName(tableName),
                                                firstRowColsLst,
                                                colNameList,
                                                colTypeList);
                            } else {
                                failedFileList.add(file.getAbsolutePath());
                                continue;
                            }

                            if (fileColumnList.size() == colNameList.size()) {
                                classNode.setData(ImportObjectLabelProvider.IS_MAPPED, true);
                                classNode.setData(ImportObjectLabelProvider.FILE_PAHT, fileName);
                                classNode.setData(ImportObjectLabelProvider.ROW_COUNT, totalLine);

                                List<PstmtParameter> parameterList =
                                        new ArrayList<PstmtParameter>();
                                for (int i = 0; i < fileColumnList.size(); i++) {

                                    PstmtParameter pstmtParameter =
                                            new PstmtParameter(
                                                    colNameList.get(i),
                                                    i + 1,
                                                    colTypeList.get(i),
                                                    String.valueOf(i));
                                    pstmtParameter.setFileColumnName(fileColumnList.get(i));
                                    parameterList.add(pstmtParameter);
                                }

                                TableConfig tableConfig = new TableConfig(classNode.getName());
                                tableConfig.setPstmList(parameterList);
                                tableConfig.setFilePath(file.getAbsolutePath());
                                tableConfig.setInsertDML(
                                        getInsertDML(classNode, colNameList, colTypeList));
                                tableConfig.setLineCount(totalLine);
                                tableConfig.setMapped(true);
                                Object isNew = classNode.getData(ImportObjectLabelProvider.IS_NEW);
                                if (isNew != null && (Boolean) isNew) {
                                    tableConfig.setCreateDDL(
                                            classNode
                                                    .getData(ImportObjectLabelProvider.CREATE_DDL)
                                                    .toString());
                                }

                                importConfig.addTableConfig(tableConfig);

                                newMappedNodeList.add(classNode);
                            } else {
                                classNode.setData(ImportObjectLabelProvider.IS_MAPPED, false);
                                classNode.setData(ImportObjectLabelProvider.FILE_PAHT, fileName);
                                classNode.setData(ImportObjectLabelProvider.ROW_COUNT, totalLine);

                                TableConfig tableConfig =
                                        importConfig.getSelectedMap().get(classNode.getName());
                                if (tableConfig != null) {
                                    tableConfig.setMapped(false);
                                    tableConfig.setFilePath(filePath);
                                    tableConfig.setLineCount(totalLine);
                                    tableConfig.getPstmList().clear();
                                    tableConfig.setInsertDML("");
                                }

                                failedFileList.add(fileName);
                            }
                            monitor.worked(1);
                            if (monitor.isCanceled()) {
                                return;
                            }
                        }
                    }
                });

        for (ICubridNode node : allTableList) {
            if (newMappedNodeList.contains(node)) {
                treeViewer.setChecked(node, true);
            }
        }
        treeViewer.refresh();
    }

    public ICubridNode createClassNode(
            String tableName,
            boolean isFirstRowAsColumnName,
            List<String> firstRowColsLst,
            List<String> colNameList,
            List<String> colTypeList) { // FIXME move this logic to core module
        int columnCount = firstRowColsLst.size();
        String iconPath = "icons/navigator/schema_table_item.png";
        ICubridNode classNode = new DefaultSchemaNode(tableName, tableName, tableName, iconPath);
        classNode.setContainer(true);
        List<String> columnNames;
        if (isFirstRowAsColumnName) {
            columnNames = firstRowColsLst;
        } else {
            columnNames = new ArrayList<String>();
            for (int i = 0; i < columnCount; i++) {
                columnNames.add("Column_" + String.valueOf(i));
            }
        }

        for (String columnName : columnNames) {
            colNameList.add(columnName);
            colTypeList.add(NEW_ATTR_TYPE);
        }

        classNode.setData(ImportObjectLabelProvider.IS_NEW, true);
        classNode.setData(
                ImportObjectLabelProvider.CREATE_DDL,
                getCreateTableDDL(classNode, colNameList, colTypeList));

        allTableList.add(classNode);
        refreshTable();
        return classNode;
    }

    public void removeClassNode(ICubridNode classNode) {
        allTableList.remove(classNode);
        refreshTable();
    }

    /**
     * Return insert SQL for prepared statement
     *
     * @return string
     */
    private String getInsertDML(
            ICubridNode classNode,
            List<String> colNameList,
            List<String> colTypeList) { // FIXME move this logic to core module
        String insert =
                "INSERT INTO "
                        + QuerySyntax.escapeKeyword(classNode.getName())
                        + " ("; //$NON-NLS-1$ //$NON-NLS-2$
        String values = "VALUES ("; // $NON-NLS-1$
        StringBuffer bfSQL = new StringBuffer();
        StringBuffer bfValue = new StringBuffer();
        bfSQL.append(insert);
        bfValue.append(values);
        for (int i = 0; i < colNameList.size(); i++) {
            if (i > 0) {
                bfSQL.append(", "); // $NON-NLS-1$
                bfValue.append(", "); // $NON-NLS-1$
            }

            bfSQL.append(QuerySyntax.escapeKeyword(colNameList.get(i)));

            String type = colTypeList.get(i);
            String paramString = FieldHandlerUtils.getParamString(type);
            bfValue.append(paramString); // $NON-NLS-1$
        }
        bfSQL.append(") "); // $NON-NLS-1$
        bfValue.append(")"); // $NON-NLS-1$
        bfSQL.append(bfValue.toString());
        return bfSQL.toString();
    }

    private String getCreateTableDDL(
            ICubridNode classNode,
            List<String> colNameList,
            List<String> colTypeList) { // FIXME move this logic to core module
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE " + QuerySyntax.escapeKeyword(classNode.getName()) + " (");
        for (int i = 0; i < colNameList.size(); i++) {
            sb.append(QuerySyntax.escapeKeyword(colNameList.get(i))).append(" ");
            sb.append(colTypeList.get(i));
            if (i + 1 < colNameList.size()) {
                sb.append(", ");
            }
        }
        sb.append(");");

        return sb.toString();
    }

    /** Handle with the selection event for button of first row as column */
    private void handleSelectEventForFirstRowAsColBtn(
            List<Integer> itemsNumberOfSheets,
            List<String> fileColumnList,
            List<String> firstRowColsLst,
            int totalLine,
            boolean isFirstLineAsColumnName) {
        int noEmptySheetNum = 0;
        if (itemsNumberOfSheets != null) {
            for (Integer itemsNum : itemsNumberOfSheets) {
                if (itemsNum != 0) {
                    noEmptySheetNum++;
                }
            }
        }
        if (isFirstLineAsColumnName) {
            totalLine = totalLine - noEmptySheetNum;
        } else {
            totalLine = totalLine + noEmptySheetNum;
        }

        fillInFromList(fileColumnList, firstRowColsLst, isFirstLineAsColumnName);
    }

    private ImportFileDescription getFileDescription(final ImportFileHandler importFileHandler) {
        importFileDescription = null;
        Display.getDefault()
                .syncExec(
                        new Runnable() {
                            public void run() {
                                try {
                                    importFileDescription = importFileHandler.getSourceFileInfo();
                                } catch (Exception e) {
                                    LOGGER.error("Open file error:" + e.getMessage());
                                    CommonUITool.openErrorBox(Messages.errorOpenFile);
                                }
                            }
                        });
        return importFileDescription;
    }

    public void refreshTable() {
        Display.getDefault()
                .syncExec(
                        new Runnable() {
                            public void run() {
                                treeViewer.setInput(allTableList);
                                treeViewer.refresh();
                            }
                        });
    }

    public ICubridNode getTableNode(final String tableName) {
        classNode = null;
        Display.getDefault()
                .syncExec(
                        new Runnable() {
                            public void run() {
                                @SuppressWarnings("unchecked")
                                List<ICubridNode> nodeList =
                                        (List<ICubridNode>) treeViewer.getInput();
                                for (ICubridNode node : nodeList) {
                                    if (tableName.equals(node.getName())) {
                                        classNode = node;
                                    }
                                }
                            }
                        });

        return classNode;
    }

    /** set the "from" column */
    private void fillInFromList(
            List<String> fileColumnList,
            List<String> firstRowColsLst,
            boolean isFirstLineAsColumnName) {
        fileColumnList.clear();
        if (isFirstLineAsColumnName) {
            for (String col : firstRowColsLst) {
                fileColumnList.add(col == null ? "" : col);
            }
        } else {
            for (int i = 0; i < firstRowColsLst.size(); i++) {
                fileColumnList.add("Column " + i); // $NON-NLS-1$
            }
        }
    }

    private boolean isSupportUserSchema() {
        return database.getDatabaseInfo().isSupportUserSchema();
    }
}
