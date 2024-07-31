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
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.cubrid.table.dialog.PstmtParameter;
import com.cubrid.common.ui.cubrid.table.importhandler.ImportFileDescription;
import com.cubrid.common.ui.cubrid.table.importhandler.ImportFileHandler;
import com.cubrid.common.ui.cubrid.table.importhandler.ImportFileHandlerFactory;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.FieldHandlerUtils;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * The Add Table File Dialog
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-8-10 created by Kevin.Wang
 */
public class AddTableFileDialog extends CMTitleAreaDialog {
    private CubridDatabase database;
    private ImportConfig configModel;
    private final FileToTableMappingComposite mappingComposite;
    private String tableName;
    private Text fileNameTxt;
    private Button firstRowAsColumnBtn;
    private SashForm sashForm;
    private org.eclipse.swt.widgets.List fromList = null;
    private org.eclipse.swt.widgets.List toList = null;
    private final List<String> colNameList = new ArrayList<String>();
    private final List<String> colTypeList = new ArrayList<String>();
    private final List<String> fileColumnList = new ArrayList<String>();
    private final List<String> firstRowColsLst = new ArrayList<String>();
    private int columnCount = 0;
    private int totalLine;
    private List<Integer> itemsNumberOfSheets;
    private boolean isOpenedFile = false;

    public AddTableFileDialog(
            FileToTableMappingComposite mappingComposite,
            CubridDatabase database,
            String tableName,
            ImportConfig configModel) {
        super(mappingComposite.getShell());
        this.mappingComposite = mappingComposite;
        this.database = database;
        this.tableName = tableName;
        this.configModel = configModel;
    }

    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);

        Composite composite = new Composite(parentComp, SWT.NONE);
        composite.setLayout(new GridLayout(3, false));
        composite.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));

        Label lblImportFile = new Label(composite, SWT.NONE);
        lblImportFile.setText(Messages.importFileNameLBL);
        lblImportFile.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        fileNameTxt = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
        fileNameTxt.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

        Button btnOpen = new Button(composite, SWT.NONE);
        btnOpen.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        btnOpen.setText(Messages.btnBrowse);
        btnOpen.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        String fileName = openFileDialog(fileNameTxt.getText());
                        fileNameTxt.setText(fileName);
                        openFile();
                        fillInFromList();
                        validate();
                    }
                });

        Composite mappingComposite = new Composite(composite, SWT.None);
        mappingComposite.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_BOTH, 3, 1, -1, -1));
        mappingComposite.setLayout(new GridLayout());

        Label lblMappingMessage = new Label(mappingComposite, SWT.NONE);
        {
            lblMappingMessage.setText(Messages.lblImportMapping);
            GridData gridData = new GridData();
            gridData.grabExcessHorizontalSpace = true;
            lblMappingMessage.setLayoutData(gridData);
        }

        firstRowAsColumnBtn = new Button(mappingComposite, SWT.CHECK);
        {
            firstRowAsColumnBtn.setText(Messages.btnFirstAsColumn);
            GridData gridData = new GridData();
            gridData.grabExcessHorizontalSpace = true;
            gridData.horizontalIndent = 10;
            firstRowAsColumnBtn.setLayoutData(gridData);

            firstRowAsColumnBtn.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            handleSelectEventForFirstRowAsColBtn();
                        }
                    });
        }

        sashForm = new SashForm(mappingComposite, SWT.NONE);
        {
            sashForm.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
            createFromComposite();
            createToComposte();
        }

        init();
        return composite;
    }

    private void init() {
        TableConfig config = configModel.getTableConfig(tableName);
        if (config != null && config.getFilePath() != null && config.getFilePath().length() > 0) {
            fileNameTxt.setText(config.getFilePath());
            totalLine = config.getLineCount();

            List<PstmtParameter> psmtList = config.getPstmList();

            fromList.removeAll();
            toList.removeAll();
            if (psmtList != null && psmtList.size() > 0) {
                for (int i = 0; i < psmtList.size(); i++) {
                    fromList.add(psmtList.get(i).getFileColumnName());
                    toList.add(psmtList.get(i).getParamName());
                }
            }

            if (config.isFirstRowAsColumn()) {
                firstRowAsColumnBtn.setSelection(true);
            }
            if (firstRowColsLst.size() == 0) {
                firstRowAsColumnBtn.setEnabled(false);
            }

        } else {
            fillInToList(tableName);
        }
    }

    /** Create the from composite */
    private void createFromComposite() {
        Composite fromComposite = new Composite(sashForm, SWT.NONE);
        {
            GridLayout gridLayout = new GridLayout();
            gridLayout.horizontalSpacing = 0;
            gridLayout.marginWidth = 0;
            gridLayout.verticalSpacing = 5;
            gridLayout.marginHeight = 0;
            fromComposite.setLayout(gridLayout);
        }

        Label lblFrom = new Label(fromComposite, SWT.NONE);
        lblFrom.setText(Messages.colFileColumn);

        fromList = new org.eclipse.swt.widgets.List(fromComposite, SWT.BORDER | SWT.V_SCROLL);
        {
            GridData gridData = new GridData();
            gridData.grabExcessHorizontalSpace = true;
            gridData.grabExcessVerticalSpace = true;
            gridData.horizontalAlignment = GridData.FILL;
            gridData.heightHint = 200;
            fromList.setLayoutData(gridData);
        }

        ToolBar fromToolbar = new ToolBar(fromComposite, SWT.FLAT);
        fromToolbar.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_END, 1, 1, -1, -1));

        ToolItem itemDel = new ToolItem(fromToolbar, SWT.PUSH);
        itemDel.setText(Messages.importDeleteExcelColumnBTN);
        itemDel.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (fromList.getItemCount() > 0 && fromList.getSelectionCount() > 0) {
                            int index = fromList.getSelectionIndex();
                            if (index == fromList.getItemCount() - 1) {
                                fromList.setSelection(index - 1);
                                fromList.remove(index);
                            } else {
                                fromList.remove(index);
                                fromList.setSelection(index);
                            }
                            validate();
                        }
                    }
                });
    }

    /** Create the to composite */
    private void createToComposte() {
        Composite toComposite = new Composite(sashForm, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.horizontalSpacing = 0;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        toComposite.setLayout(gridLayout);

        Label lblTo = new Label(toComposite, SWT.NONE);
        lblTo.setText(Messages.importTableColumns);

        toList = new org.eclipse.swt.widgets.List(toComposite, SWT.BORDER | SWT.V_SCROLL);
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.heightHint = 200;
        toList.setLayoutData(gridData);

        /* Create toolbar */
        ToolBar toToolbar = new ToolBar(toComposite, SWT.FLAT);
        toToolbar.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_END, 1, 1, -1, -1));

        ToolItem itemUp = new ToolItem(toToolbar, SWT.PUSH);
        itemUp.setText(Messages.importUpTableColumnBTN);
        itemUp.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (toList.getItemCount() > 0 && toList.getSelectionCount() > 0) {
                            int index = toList.getSelectionIndex();
                            String item = toList.getItem(index);
                            if (index == 0) {
                                return;
                            }

                            toList.remove(index);
                            toList.add(item, index - 1);
                            toList.setSelection(index - 1);
                            validate();
                        }
                    }
                });

        ToolItem itemDown = new ToolItem(toToolbar, SWT.PUSH);
        itemDown.setText(Messages.importDownTableColumnBTN);
        itemDown.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (toList.getItemCount() > 0 && toList.getSelectionCount() > 0) {
                            int index = toList.getSelectionIndex();
                            String item = toList.getItem(index);
                            if (index == toList.getItemCount() - 1) {
                                return;
                            }

                            toList.remove(index);
                            toList.add(item, index + 1);
                            toList.setSelection(index + 1);
                            validate();
                        }
                    }
                });

        ToolItem itemDel = new ToolItem(toToolbar, SWT.PUSH | SWT.BORDER);
        itemDel.setText(Messages.importDeleteTableColumnBTN);
        itemDel.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (toList.getItemCount() > 0 && toList.getSelectionCount() > 0) {
                            int index = toList.getSelectionIndex();

                            if (index == toList.getItemCount() - 1) {
                                toList.setSelection(index - 1);
                                toList.remove(index);
                            } else {
                                toList.remove(index);
                                toList.setSelection(index);
                            }
                            validate();
                        }
                    }
                });

        fillInToList(tableName);
    }

    /** Handle with the selection event for button of first row as column */
    private void handleSelectEventForFirstRowAsColBtn() {
        ICubridNode classNode = mappingComposite.getTableNode(tableName);
        int noEmptySheetNum = 0;
        if (itemsNumberOfSheets != null) {
            for (Integer itemsNum : itemsNumberOfSheets) {
                if (itemsNum != 0) {
                    noEmptySheetNum++;
                }
            }
        }
        if (firstRowAsColumnBtn.getSelection()) {
            totalLine = totalLine - noEmptySheetNum;
        } else {
            totalLine = totalLine + noEmptySheetNum;
        }

        if (mappingComposite.isNewClassNode(classNode)) {
            mappingComposite.removeClassNode(classNode);
            ICubridNode node =
                    mappingComposite.createClassNode(
                            classNode.getName(),
                            firstRowAsColumnBtn.getSelection(),
                            firstRowColsLst,
                            new ArrayList<String>(),
                            new ArrayList<String>());
            node.setData(ImportObjectLabelProvider.IS_MAPPED, true);
            node.setData(ImportObjectLabelProvider.FILE_PAHT, fileNameTxt.getText());
            node.setData(ImportObjectLabelProvider.ROW_COUNT, totalLine);
            refreshInToList(node);

            mappingComposite.refreshTable();
        }
        fillInFromList();
        validate();
    }

    /** set the "from" column */
    private void fillInFromList() {
        fromList.removeAll();
        fileColumnList.clear();
        if (firstRowAsColumnBtn.getSelection()) {
            for (String col : firstRowColsLst) {
                fromList.add(col == null ? "" : col);
                fileColumnList.add(col == null ? "" : col);
            }
        } else {
            for (int i = 0; i < columnCount; i++) {
                fromList.add("Column " + i); // $NON-NLS-1$
                fileColumnList.add("Column " + i); // $NON-NLS-1$
            }
        }
    }

    /**
     * Fill in the to list
     *
     * @param tableName String
     */
    private void fillInToList(String tableName) {
        colNameList.clear();
        colTypeList.clear();
        toList.removeAll();

        ICubridNode classNode = mappingComposite.getTableNode(tableName);
        if (!mappingComposite.isNewClassNode(classNode)) {
            SchemaInfo schemaInfo = database.getDatabaseInfo().getSchemaInfo(tableName);

            List<DBAttribute> attributes = schemaInfo == null ? null : schemaInfo.getAttributes();
            if (attributes == null) {
                return;
            }
            for (DBAttribute attr : attributes) {
                String column = attr.getName();
                String dataType = attr.getType();
                toList.add(column);
                colNameList.add(column);
                colTypeList.add(dataType);
            }
        } else {
            List<ICubridNode> columnList = classNode.getChildren();
            for (ICubridNode node : columnList) {
                toList.add(node.getName());
                colNameList.add(node.getName());
                colTypeList.add(node.getData(ImportObjectLabelProvider.DATE_TYPE).toString());
            }
        }
    }

    /**
     * Fill in the to list
     *
     * @param classNode ICubridNode
     */
    private void refreshInToList(ICubridNode classNode) {
        colNameList.clear();
        colTypeList.clear();
        toList.removeAll();

        List<String> columnNames;
        if (firstRowAsColumnBtn.getSelection()) {
            columnNames = firstRowColsLst;
        } else {
            columnNames = new ArrayList<String>();
            for (int i = 0; i < columnCount; i++) {
                columnNames.add("Column_" + String.valueOf(i));
            }
        }
        for (String name : columnNames) {
            toList.add(name);
            colNameList.add(name);
            colTypeList.add(FileToTableMappingComposite.NEW_ATTR_TYPE);
        }

        List<ICubridNode> columnList = classNode.getChildren();
        for (ICubridNode node : columnList) {
            toList.add(node.getName());
            colNameList.add(node.getName());
            colTypeList.add(node.getData(ImportObjectLabelProvider.DATE_TYPE).toString());
        }
    }

    private boolean validate() {
        if (getButton(IDialogConstants.OK_ID) == null) {
            return false;
        }
        setErrorMessage(null);
        getButton(IDialogConstants.OK_ID).setEnabled(false);

        if (fromList.getItemCount() != toList.getItemCount()) { // columns not matched
            setErrorMessage(Messages.importColumnCountMatchERRORMSG);
            fromList.setFocus();
            return false;
        }
        if (fromList.getItemCount() < 1) { // no from column
            setErrorMessage(Messages.importNoExcelColumnERRORMSG);
            fromList.setFocus();
            return false;
        }
        if (toList.getItemCount() < 1) { // no to column
            setErrorMessage(Messages.importNoTableColumnERRORMSG);
            toList.setFocus();
            return false;
        }

        getButton(IDialogConstants.OK_ID).setEnabled(true);
        return true;
    }

    /** Open the file */
    private void openFile() {
        if (getButton(IDialogConstants.OK_ID) != null) {
            getButton(IDialogConstants.OK_ID).setEnabled(false);
        }

        String filePath = fileNameTxt.getText();
        if (filePath.trim().length() == 0) {
            return;
        }

        isOpenedFile = true;
        firstRowAsColumnBtn.setEnabled(isOpenedFile);

        boolean isHaveException = false;
        String errorMsg = null;
        try {
            ImportFileHandler importFileHandler =
                    ImportFileHandlerFactory.getHandler(filePath, configModel);
            ImportFileDescription ifd = importFileHandler.getSourceFileInfo();

            firstRowColsLst.clear();
            firstRowColsLst.addAll(ifd.getFirstRowCols());
            totalLine = ifd.getTotalCount();
            columnCount = firstRowColsLst.size();
            itemsNumberOfSheets = ifd.getItemsNumberOfSheets();

            if (firstRowAsColumnBtn.getSelection()) {
                handleSelectEventForFirstRowAsColBtn();
            }
            File file = new File(filePath);
            CommonUIPlugin.putSettingValue(ImportDataWizard.SESSION_IMPORT_KEY, file.getParent());
        } catch (InvocationTargetException ex) {
            isHaveException = true;
            Throwable targetException = ((InvocationTargetException) ex).getTargetException();
            if (targetException instanceof OutOfMemoryError) {
                CommonUITool.openErrorBox(Messages.errNoAvailableMemory);
            } else {
                errorMsg = ex.getMessage();
            }
        } catch (InterruptedException ex) {
            isHaveException = true;
            errorMsg = ex.getMessage();
        } catch (Exception ex) {
            isHaveException = true;
            errorMsg = ex.getMessage();
        }

        String supportExtString = null;
        if (configModel.getImportType() == ImportConfig.IMPORT_FROM_EXCEL) {
            supportExtString = "csv, xls";
        } else if (configModel.getImportType() == ImportConfig.IMPORT_FROM_TXT) {
            supportExtString = "txt";
        } else if (configModel.getImportType() == ImportConfig.IMPORT_FROM_SQL) {
            supportExtString = "sql";
        } else if (configModel.getImportType() == ImportConfig.IMPORT_FROM_LOADDB) {
            supportExtString = "LoadDB";
        }

        if (errorMsg != null && errorMsg.trim().length() > 0) {
            String msg = Messages.errorOpenFile;
            if (supportExtString != null) {
                msg += Messages.bind(Messages.errorOpenFileDetail, supportExtString);
            }
            msg += StringUtil.NEWLINE + StringUtil.NEWLINE + errorMsg;
            CommonUITool.openErrorBox(msg);
        }

        if (isHaveException) {
            fileNameTxt.setText("");
        }
    }

    protected void okPressed() {
        if (isOpenedFile) { // FIXME move this logic to core module
            int fromListCount = fromList.getItemCount();
            String[] fromListItems = fromList.getItems();
            String[] toListItems = new String[fromListCount];
            for (int i = 0; i < fromListCount; i++) {
                toListItems[i] = toList.getItem(i);
            }
            List<PstmtParameter> parameterList = new ArrayList<PstmtParameter>();
            for (int i = 0; i < fromListCount; i++) {
                String name = toListItems[i];
                if (name.trim().length() == 0) {
                    continue;
                }
                String type = colTypeList.get(colNameList.indexOf(name));
                String excelColumn = fromListItems[i];
                String value = "0";
                for (int j = 0; j < fileColumnList.size(); j++) {
                    if (excelColumn.equals(fileColumnList.get(j))) {
                        value = j + "";
                        break;
                    }
                }
                PstmtParameter pstmtParameter = new PstmtParameter(name, i + 1, type, value);
                pstmtParameter.setFileColumnName(excelColumn);
                parameterList.add(pstmtParameter);
            }

            ICubridNode classNode = mappingComposite.getTableNode(tableName);

            classNode.setData(ImportObjectLabelProvider.ROW_COUNT, totalLine);
            File file = new File(fileNameTxt.getText());
            classNode.setData(ImportObjectLabelProvider.FILE_PAHT, file.getName());
            classNode.setData(ImportObjectLabelProvider.IS_MAPPED, true);

            mappingComposite.refreshTable();
            TableConfig tableConfig = configModel.getTableConfig(classNode.getName());
            if (tableConfig == null) {
                tableConfig = new TableConfig(classNode.getName());
            }

            tableConfig.setPstmList(parameterList);
            tableConfig.setFilePath(fileNameTxt.getText());
            tableConfig.setInsertDML(getInsertDML());
            tableConfig.setLineCount(totalLine);
            tableConfig.setFirstRowAsColumn(firstRowAsColumnBtn.getSelection());
            tableConfig.setMapped(true);
            Object isNew = classNode.getData(ImportObjectLabelProvider.IS_NEW);
            if (isNew != null && (Boolean) isNew) {
                tableConfig.setCreateDDL(
                        classNode.getData(ImportObjectLabelProvider.CREATE_DDL).toString());
            }

            configModel.addTableConfig(tableConfig);
        }

        super.okPressed();
    }

    /**
     * Return insert SQL for prepared statement
     *
     * @return string
     */
    private String getInsertDML() {
        String insert = "INSERT INTO " + QuerySyntax.escapeKeyword(tableName) + " (";
        String values = "VALUES (";
        StringBuffer bfSQL = new StringBuffer();
        StringBuffer bfValue = new StringBuffer();
        bfSQL.append(insert);
        bfValue.append(values);
        for (int i = 0; i < toList.getItemCount(); i++) {
            if (i > 0) {
                bfSQL.append(", ");
                bfValue.append(", ");
            }

            bfSQL.append(QuerySyntax.escapeKeyword(toList.getItem(i)));

            String type = colTypeList.get(colNameList.indexOf(toList.getItem(i)));
            String paramString = FieldHandlerUtils.getParamString(type);
            bfValue.append(paramString);
        }
        bfSQL.append(") ");
        bfValue.append(")");
        bfSQL.append(bfValue.toString());
        return bfSQL.toString();
    }

    /** open file dialog */
    private String openFileDialog(String filePath) {
        FileDialog dialog = new FileDialog(getShell(), SWT.OPEN | SWT.APPLICATION_MODAL);

        if (filePath == null || filePath.trim().length() == 0) {
            filePath = CommonUIPlugin.getSettingValue(ImportDataWizard.SESSION_IMPORT_KEY);
        }

        if (configModel.getImportType() == ImportConfig.IMPORT_FROM_EXCEL) {
            dialog.setFilterExtensions(new String[] {"*.*" /*, "*.xlsx"*/, "*.xls", "*.csv"});
            dialog.setFilterNames(
                    new String[] {
                        Messages.allFileType /*, Messages.xlsxFileType*/,
                        Messages.xlsFileType,
                        Messages.csvFileType
                    });
        } else if (configModel.getImportType() == ImportConfig.IMPORT_FROM_TXT) {
            dialog.setFilterExtensions(new String[] {"*.txt", "*.*"});
            dialog.setFilterNames(new String[] {Messages.txtFileType, Messages.allFileType});
        } else if (configModel.getImportType() == ImportConfig.IMPORT_FROM_SQL) {
            dialog.setFilterExtensions(new String[] {"*.sql", "*.*"});
            dialog.setFilterNames(new String[] {Messages.sqlFileType, Messages.allFileType});
        } else if (configModel.getImportType() == ImportConfig.IMPORT_FROM_LOADDB) {
            dialog.setFilterExtensions(new String[] {"*.txt", "*.*"});
            dialog.setFilterNames(new String[] {Messages.txtFileType, Messages.allFileType});
        }

        String fileName = dialog.open();
        if (fileName == null) {
            return "";
        }

        return fileName;
    }

    protected void constrainShellSize() {
        super.constrainShellSize();
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.titleAddTableFileDialog);
        setMessage(Messages.msgAddTableFileDialog);
    }
}
