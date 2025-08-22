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
package com.cubrid.common.ui.cubrid.table.dialog;

import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.cubrid.table.control.ImportErrorControlPanel;
import com.cubrid.common.ui.cubrid.table.importhandler.ImportFileConstants;
import com.cubrid.common.ui.cubrid.table.importhandler.ImportFileDescription;
import com.cubrid.common.ui.cubrid.table.importhandler.ImportFileHandler;
import com.cubrid.common.ui.cubrid.table.importhandler.ImportFileHandlerFactory;
import com.cubrid.common.ui.cubrid.table.importhandler.handler.XLSImportFileHandler;
import com.cubrid.common.ui.cubrid.table.importhandler.handler.XLSXImportFileHandler;
import com.cubrid.common.ui.query.control.SqlParser;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.query.editor.QueryUnit;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.progress.CommonTaskJobExec;
import com.cubrid.common.ui.spi.progress.JobFamily;
import com.cubrid.common.ui.spi.progress.TaskJob;
import com.cubrid.common.ui.spi.progress.TaskJobExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.TableUtil;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * The dialog for doing with multi table data by prepared statement
 *
 * @author pangqiren
 * @version 1.0 - 2010-7-27 created by pangqiren
 */
public class ImportDataFromFileDialog extends PstmtDataDialog {

    private static final String SESSION_IMPORT_KEY = "PstmtMultiDataDialog-FilePath";

    private Text fileNameTxt;
    private Combo fileCharsetCombo;
    private Text totalLinesText;
    private Spinner threadCountSpinner;
    private Spinner commitLineSpinner;
    private Button firstRowAsColumnBtn;

    private final List<String> fileColumnList = new ArrayList<String>();
    private final List<PstmtDataTask> taskList = new ArrayList<PstmtDataTask>();
    private long beginTimestamp;
    private Text jdbcCharsetText;
    private final List<String> firstRowColsLst = new ArrayList<String>();

    private final String[] filterExts = ImportFileConstants.getFilterExts();
    private final String[] filterNames = ImportFileConstants.getFilterNames();

    private ImportErrorControlPanel importErrorControl;
    private boolean startShowResult = false;
    private List<Integer> itemsNumberOfSheets;
    private String errorLogDir;
    private ImportFileHandler importFileHandler;

    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setMinimumSize(640, 770);
    }

    /**
     * Retrieve the Show Result is in process.
     *
     * @return is in process.
     */
    private boolean getStartShowResult() {
        synchronized (this) {
            return startShowResult;
        }
    }

    /**
     * The finish process is already started.
     *
     * @param value boolean
     */
    public void setStartShowResult(boolean value) {
        synchronized (this) {
            startShowResult = value;
        }
    }

    /**
     * The constructor
     *
     * @param parentShell
     * @param database
     */
    public ImportDataFromFileDialog(Shell parentShell, CubridDatabase database) {
        super(parentShell, database, null, false);
    }

    /**
     * The constructor
     *
     * @param parentShell
     * @param database
     * @param tableName
     * @param isInsert
     */
    public ImportDataFromFileDialog(
            Shell parentShell, CubridDatabase database, String tableName, boolean isInsert) {
        super(parentShell, database, tableName, isInsert);
    }

    /**
     * Create the bottom composite
     *
     * @param parent Composite
     * @return Composite
     */
    protected Composite createBottomComposite(Composite parent) {

        Composite bottomComp = new Composite(parent, SWT.NONE);
        {
            bottomComp.setLayoutData(new GridData(GridData.FILL_BOTH));
            GridLayout layout = new GridLayout();
            layout.marginWidth = 0;
            bottomComp.setLayout(layout);

            createFilePane(bottomComp);
            createParamPane(bottomComp);

            importErrorControl = new ImportErrorControlPanel(bottomComp, SWT.NONE);
        }

        setTitle(Messages.titlePstmtDataDialog);
        setMessage(Messages.msgPstmtMultiDataDialog);
        return bottomComp;
    }

    /**
     * Create the file composite
     *
     * @param parent Composite
     */
    private void createFilePane(Composite parent) {
        Group fileGroup = new Group(parent, SWT.NONE);
        {
            fileGroup.setText(Messages.grpSelectFile);
            GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
            fileGroup.setLayoutData(gridData);

            GridLayout layout = new GridLayout();
            layout.numColumns = 4;
            fileGroup.setLayout(layout);
        }

        Label fileNameLbl = new Label(fileGroup, SWT.NONE);
        fileNameLbl.setText(Messages.importFileNameLBL);
        fileNameLbl.setLayoutData(CommonUITool.createGridData(1, 1, 80, -1));

        Composite filePathComp = new Composite(fileGroup, SWT.NONE);
        {
            GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
            gridData.horizontalSpan = 3;
            filePathComp.setLayoutData(gridData);

            GridLayout layout = new GridLayout();
            layout.numColumns = 3;
            layout.marginWidth = 0;
            filePathComp.setLayout(layout);
        }

        fileNameTxt = new Text(filePathComp, SWT.BORDER | SWT.READ_ONLY);
        {
            GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
            gridData.horizontalSpan = 2;
            fileNameTxt.setLayoutData(gridData);

            fileNameTxt.addModifyListener(
                    new ModifyListener() {
                        public void modifyText(ModifyEvent event) {
                            openFile();
                            fillParameterTableFileColumn();
                            validate();
                        }
                    });
        }

        Button browseBtn = new Button(filePathComp, SWT.NONE);
        {
            GridData gridData = new GridData();
            browseBtn.setLayoutData(gridData);
            browseBtn.setText(Messages.btnBrowse);

            browseBtn.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            openFileDialog();
                        }
                    });
        }

        final Label fileCharsetLabel = new Label(fileGroup, SWT.NONE);
        fileCharsetLabel.setText(Messages.lblFileCharset);
        fileCharsetLabel.setLayoutData(CommonUITool.createGridData(1, 1, 80, -1));

        fileCharsetCombo = new Combo(fileGroup, SWT.NONE);
        {
            GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
            fileCharsetCombo.setLayoutData(gridData);
            fileCharsetCombo.setItems(QueryOptions.getAllCharset(null));
            fileCharsetCombo.select(0);
            fileCharsetCombo.addModifyListener(
                    new ModifyListener() {
                        public void modifyText(ModifyEvent event) {
                            if (validate()) {
                                openFile();
                                fillParameterTableFileColumn();
                            }
                        }
                    });
        }

        Label jdbcCharsetLabel = new Label(fileGroup, SWT.NONE);
        jdbcCharsetLabel.setText(Messages.lblJDBCCharset);

        jdbcCharsetText = new Text(fileGroup, SWT.BORDER | SWT.READ_ONLY);
        {
            GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
            jdbcCharsetText.setLayoutData(gridData);
        }

        Composite importAttrComp = new Composite(fileGroup, SWT.NONE);
        {
            GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
            gridData.horizontalSpan = 4;
            gridData.horizontalIndent = 0;
            importAttrComp.setLayoutData(gridData);
            GridLayout layout = new GridLayout();
            layout.numColumns = 6;
            layout.marginWidth = 0;
            importAttrComp.setLayout(layout);
        }

        Label totalRowsLabel = new Label(importAttrComp, SWT.NONE);
        totalRowsLabel.setText(Messages.lblTotalLines);
        totalRowsLabel.setLayoutData(CommonUITool.createGridData(1, 1, 80, -1));

        totalLinesText = new Text(importAttrComp, SWT.BORDER | SWT.READ_ONLY);
        {
            GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
            totalLinesText.setLayoutData(gridData);
        }

        Label threadCountLabel = new Label(importAttrComp, SWT.NONE);
        threadCountLabel.setText(Messages.lblThreadCount);

        threadCountSpinner = new Spinner(importAttrComp, SWT.BORDER);
        {
            threadCountSpinner.setMaximum(Integer.MAX_VALUE);
            threadCountSpinner.setMinimum(1);
            threadCountSpinner.setSelection(1);
            GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
            threadCountSpinner.setLayoutData(gridData);
            threadCountSpinner.addModifyListener(
                    new ModifyListener() {
                        public void modifyText(ModifyEvent event) {
                            validate();
                        }
                    });
        }

        Label lblCommitLine = new Label(importAttrComp, SWT.NONE);
        lblCommitLine.setText(Messages.lblCommitLines);

        commitLineSpinner = new Spinner(importAttrComp, SWT.BORDER);
        {
            commitLineSpinner.setMaximum(Integer.MAX_VALUE);
            commitLineSpinner.setMinimum(1);
            commitLineSpinner.setSelection(1000);
            GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
            commitLineSpinner.setLayoutData(gridData);
            commitLineSpinner.addModifyListener(
                    new ModifyListener() {
                        public void modifyText(ModifyEvent event) {
                            validate();
                        }
                    });
        }
    }

    /**
     * Create the parameter table
     *
     * @param parent Composite
     */
    protected void createParamPane(Composite parent) {
        Group parameterGroup = new Group(parent, SWT.NONE);
        {
            parameterGroup.setText(Messages.grpMapping);
            GridData gridData = new GridData(GridData.FILL_BOTH);
            parameterGroup.setLayoutData(gridData);
            parameterGroup.setLayout(new GridLayout());
        }

        createParameterTable(parameterGroup, true);

        firstRowAsColumnBtn = new Button(parameterGroup, SWT.CHECK);
        firstRowAsColumnBtn.setText(Messages.btnFirstAsColumn);
        {
            GridData gridData = new GridData();
            gridData.grabExcessHorizontalSpace = true;
            gridData.horizontalIndent = 5;
            firstRowAsColumnBtn.setLayoutData(gridData);
        }
        firstRowAsColumnBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        handleSelectEventForFirstRowAsColBtn();
                    }
                });
    }

    /** Handle with the selection event for button of first row as column */
    private void handleSelectEventForFirstRowAsColBtn() {
        String totalLines = totalLinesText.getText();
        if (!totalLines.matches("^\\d+$")) {
            return;
        }
        int lines = Integer.parseInt(totalLines);

        int noEmptySheetNum = 0;
        if (itemsNumberOfSheets != null) {
            for (Integer itemsNum : itemsNumberOfSheets) {
                if (itemsNum != 0) {
                    noEmptySheetNum++;
                }
            }
        }

        if (firstRowAsColumnBtn.getSelection()) {
            lines = lines - noEmptySheetNum;
        } else {
            lines = lines + noEmptySheetNum;
        }
        lines = lines < 0 ? 0 : lines;
        totalLinesText.setText(String.valueOf(lines));
        fillParameterTableFileColumn();
        validate();
    }

    /**
     * Create JDBC group
     *
     * @param parent Composite
     */
    protected void createJdbcPane(Composite parent) {}

    /** Initial the data */
    protected void initial() {
        super.initial();
        String charset = database.getDatabaseInfo().getCharSet();
        if (charset != null) {
            jdbcCharsetText.setText(charset);
        }
    }

    /** Open file dialog */
    private void openFileDialog() {
        FileDialog dialog = new FileDialog(getShell(), SWT.OPEN | SWT.APPLICATION_MODAL);
        dialog.setFilterExtensions(filterExts);
        dialog.setFilterNames(filterNames);

        String filepath = fileNameTxt.getText();
        if (filepath.trim().length() == 0) {
            filepath = CommonUIPlugin.getSettingValue(SESSION_IMPORT_KEY);
        }
        if (null != filepath) {
            dialog.setFilterPath(filepath);
        }
        String fileName = dialog.open();
        if (fileName == null) {
            return;
        }
        fileNameTxt.setText(fileName);
    }

    /** Open the file */
    private void openFile() {
        String fileName = fileNameTxt.getText();
        if (fileName.trim().length() == 0) {
            return;
        }
        boolean isHaveException = false;
        String errorMsg = null;
        try {
            String fileCharset = fileCharsetCombo.getText();
            importFileHandler = ImportFileHandlerFactory.getHandler(fileName, fileCharset);

            ImportFileDescription ifd = importFileHandler.getSourceFileInfo();
            itemsNumberOfSheets = ifd.getItemsNumberOfSheets();
            firstRowColsLst.clear();
            firstRowColsLst.addAll(ifd.getFirstRowCols());
            totalLinesText.setText(String.valueOf(ifd.getTotalCount()));
            if (firstRowAsColumnBtn.getSelection()) {
                handleSelectEventForFirstRowAsColBtn();
            }
            File file = new File(fileName);
            CommonUIPlugin.putSettingValue(SESSION_IMPORT_KEY, file.getParent());
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
        if (errorMsg != null && errorMsg.trim().length() > 0) {
            CommonUITool.openErrorBox(errorMsg);
        }
        if (isHaveException) {
            fileNameTxt.setText("");
            totalLinesText.setText("");
        }
    }

    /** Fill in the parameter table file column */
    private void fillParameterTableFileColumn() {
        String fileName = fileNameTxt.getText();

        if (fileName == null) {
            return;
        }
        if (fileName.trim().length() < 1) {
            return;
        }
        fileColumnList.clear();
        if (firstRowAsColumnBtn.getSelection()) {
            for (String col : firstRowColsLst) {
                fileColumnList.add(col == null ? "" : col);
            }
        } else {
            int columnCount = firstRowColsLst.size();
            for (int i = 0; i < columnCount; i++) {
                fileColumnList.add("Column " + i); // $NON-NLS-1$
            }
        }
        int columnCount = parameterTable.getItemCount();
        int fileColumnSize = fileColumnList.size();
        int count = Math.min(columnCount, fileColumnSize);
        for (int i = 0; i < count; i++) {
            TableItem item = parameterTable.getItem(i);
            item.setText(2, fileColumnList.get(i));
        }
        packTable();
        validate();
    }

    /**
     * Handle value modify event
     *
     * @param item TableItem
     */
    protected void handleValue(final TableItem item) {

        String[] items = fileColumnList.toArray(new String[] {});
        final int editColumn = 2;
        final Combo fileColumnCombo = new Combo(parameterTable, SWT.BORDER | SWT.FULL_SELECTION);
        fileColumnCombo.setItems(items);
        fileColumnCombo.setVisibleItemCount(20);

        final String paraName = item.getText(0);

        fileColumnCombo.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        validateFileColumn(paraName, fileColumnCombo.getText());
                    }
                });

        fileColumnCombo.addFocusListener(
                new FocusAdapter() {
                    public void focusLost(FocusEvent event) {
                        if (isChanging) {
                            return;
                        }
                        isChanging = true;
                        if (validateFileColumn(paraName, fileColumnCombo.getText())) {
                            item.setText(editColumn, fileColumnCombo.getText());
                        }
                        fileColumnCombo.dispose();
                        isChanging = false;
                        validate();
                    }
                });

        // add listener for key pressed
        fileColumnCombo.addTraverseListener(
                new TraverseListener() {
                    public void keyTraversed(TraverseEvent event) {
                        if (event.detail == SWT.TRAVERSE_RETURN) {
                            if (isChanging) {
                                return;
                            }
                            isChanging = true;
                            if (validateFileColumn(paraName, fileColumnCombo.getText())) {
                                item.setText(editColumn, fileColumnCombo.getText());
                            }
                            fileColumnCombo.dispose();
                            isChanging = false;
                            validate();
                            event.doit = true;
                            handleValue(item);
                        } else if (event.detail == SWT.TRAVERSE_ESCAPE) {
                            if (isChanging) {
                                return;
                            }
                            isChanging = true;
                            fileColumnCombo.dispose();
                            event.doit = false;
                            isChanging = false;
                        }
                    }
                });

        tableEditor.setEditor(fileColumnCombo, item, editColumn);
        fileColumnCombo.setText(item.getText(editColumn));
        fileColumnCombo.setFocus();
    }

    /**
     * Validate the file column
     *
     * @param paraName String
     * @param columnValue String
     * @return boolean
     */
    private boolean validateFileColumn(String paraName, String columnValue) {
        setErrorMessage(null);
        for (String fileColumn : fileColumnList) {
            if (fileColumn.equals(columnValue)) {
                return true;
            }
        }
        setErrorMessage(Messages.bind(Messages.errNoMappingParaColumn, paraName));
        return false;
    }

    /**
     * validate the data
     *
     * @return boolean
     */
    protected boolean validate() {
        setErrorMessage(null);
        getButton(IDialogConstants.OK_ID).setEnabled(false);

        if (!validSql()) {
            setErrorMessage(Messages.errInvalidSql);
            return false;
        }

        String sql = sqlTxt.getText();
        int count = SqlParser.getStrCount(sql, "?");
        int itemCount = parameterTable.getItemCount();
        int parameterCount = 0;
        for (int i = 0; i < itemCount; i++) {
            String parameter = parameterTable.getItem(i).getText(0);
            if (parameter.trim().length() > 0) {
                parameterCount++;
            }
        }
        if (count != parameterCount) {
            setErrorMessage(Messages.errMappingParaColumn);
            return false;
        }

        String filePath = fileNameTxt.getText();
        if (filePath.trim().length() == 0) {
            setErrorMessage(Messages.msgSelectFile);
            return false;
        }

        String charsetName = fileCharsetCombo.getText();
        try {
            "".getBytes(charsetName);
        } catch (UnsupportedEncodingException e) {
            setErrorMessage(Messages.errUnsupportedCharset);
            return false;
        }

        String totalLines = totalLinesText.getText();
        if (totalLines.trim().length() == 0 || Integer.parseInt(totalLines) <= 0) {
            setErrorMessage(Messages.errNoDataFile);
            return false;
        }

        int threadCount = threadCountSpinner.getSelection();
        if (threadCount <= 0) {
            setErrorMessage(Messages.errThreadCount);
            return false;
        }

        int lines = Integer.parseInt(totalLines);
        if (threadCount > 1 && lines < threadCount) {
            setErrorMessage(Messages.errNoFitThreadCount);
            return false;
        }

        for (int i = 0; i < parameterTable.getItemCount(); i++) {
            TableItem item = parameterTable.getItem(i);
            String paraName = item.getText(0);
            if (paraName == null || paraName.trim().length() == 0) {
                continue;
            }
            String type = item.getText(1);
            if (!validateType(paraName, type)) {
                return false;
            }
            String column = item.getText(2);
            if (!validateFileColumn(paraName, column)) {
                return false;
            }
        }
        getButton(IDialogConstants.OK_ID).setEnabled(true);
        return true;
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     * @param buttonId the id of the button that was pressed (see <code>IDialogConstants.*_ID</code>
     *     constants)
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            if (validate()) { // FIXME move this logic to core module
                List<PstmtParameter> parameterList = new ArrayList<PstmtParameter>();
                for (int i = 0; i < parameterTable.getItemCount(); i++) {
                    String name = parameterTable.getItem(i).getText(0);
                    if (name.trim().length() == 0) {
                        continue;
                    }
                    String type = parameterTable.getItem(i).getText(1);
                    String excelColumn = parameterTable.getItem(i).getText(2);
                    String value = "0";
                    for (int j = 0; j < fileColumnList.size(); j++) {
                        if (excelColumn.equals(fileColumnList.get(j))) {
                            value = j + "";
                            break;
                        }
                    }
                    PstmtParameter pstmtParameter = new PstmtParameter(name, i + 1, type, value);
                    parameterList.add(pstmtParameter);
                }

                if (TableUtil.isHasResultSet(database, sqlTxt.getText())) {
                    showResultSet(parameterList);
                } else {
                    updateData(parameterList);
                }
            }
        } else if (buttonId == IDialogConstants.CANCEL_ID) {
            super.buttonPressed(buttonId);
        }
    }

    /**
     * Show the result set by query editor
     *
     * @param parameterList List<PstmtParameter>
     */
    private void showResultSet(
            List<PstmtParameter> parameterList) { // FIXME move this logic to core module

        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }
        String showedSql = sqlTxt.getText();

        String executedSql = showedSql;
        while (executedSql.endsWith(";")) {
            executedSql = executedSql.substring(0, executedSql.length() - 1);
        }
        executedSql += ";";

        int threadCount = threadCountSpinner.getSelection();
        String fileName = fileNameTxt.getText();
        String charset = fileCharsetCombo.getText();
        int totalLine = Integer.parseInt(totalLinesText.getText());
        int commitedLineOnce = commitLineSpinner.getSelection();

        ImportFileHandler handler = ImportFileHandlerFactory.getHandler(fileName, charset);

        PstmtDataTask task =
                new PstmtDataTask(
                        sqlTxt.getText(),
                        database,
                        fileName,
                        parameterList,
                        0,
                        totalLine,
                        commitedLineOnce,
                        charset,
                        firstRowAsColumnBtn.getSelection(),
                        false,
                        null,
                        handler);
        List<List<PstmtParameter>> rowParameterList = null;
        try {
            rowParameterList = task.getRowParameterList();
        } catch (RuntimeException ex) {
            String errorMsg = null;
            if (ex.getCause() == null) {
                errorMsg = ex.getMessage();
            } else {
                if (ex.getCause() instanceof OutOfMemoryError) {
                    errorMsg = Messages.errNoAvailableMemory;
                } else {
                    errorMsg = ex.getCause().getMessage();
                }
            }
            errorMsg =
                    errorMsg == null || errorMsg.trim().length() == 0 ? "Unknown error." : errorMsg;
            CommonUITool.openErrorBox(getShell(), errorMsg);
            return;
        } catch (Exception ex) {
            String errorMsg = ex.getMessage();
            errorMsg =
                    errorMsg == null || errorMsg.trim().length() == 0 ? "Unknown error." : errorMsg;
            CommonUITool.openErrorBox(getShell(), errorMsg);
            return;
        } finally {
            if (importFileHandler instanceof XLSImportFileHandler) {
                XLSImportFileHandler xlsHandler = (XLSImportFileHandler) importFileHandler;
                xlsHandler.dispose();
            } else if (importFileHandler instanceof XLSXImportFileHandler) {
                XLSXImportFileHandler xlsHandler = (XLSXImportFileHandler) importFileHandler;
                xlsHandler.dispose();
            }
        }

        close();

        int rows = rowParameterList == null ? 0 : rowParameterList.size();
        int rowsOfThread = rows;
        if (threadCount > 1) {
            rowsOfThread = rows / threadCount;
        }
        int rowsOfLastThread = rowsOfThread + (rows % threadCount == 0 ? 0 : rows % threadCount);
        int currentRow = 0;
        for (int i = 0; i < threadCount; i++) {
            QueryUnit editorInput = new QueryUnit();
            IEditorPart editor = null;
            try {
                editor = window.getActivePage().openEditor(editorInput, QueryEditorPart.ID);
            } catch (PartInitException e) {
                editor = null;
            }

            if (editor != null) {
                QueryEditorPart queryEditor = ((QueryEditorPart) editor);

                int endRow = currentRow + rowsOfThread;
                if (i == threadCount - 1) {
                    endRow = currentRow + rowsOfLastThread;
                }

                List<List<PstmtParameter>> paraList = new ArrayList<List<PstmtParameter>>();
                StringBuffer showedSqlBuffer = new StringBuffer();
                StringBuffer executeSqlBuffer = new StringBuffer();
                for (int j = currentRow; j < endRow; j++) {
                    showedSqlBuffer.append(getCommentSqlValue(rowParameterList.get(j)));
                    paraList.add(rowParameterList.get(j));
                    executeSqlBuffer.append(executedSql);
                }
                showedSqlBuffer.append(showedSql);
                currentRow = endRow;

                if (!queryEditor.isConnected() && database != null) {
                    queryEditor.connect(database);
                }
                if (queryEditor.isConnected()) {
                    queryEditor.setQuery(
                            showedSqlBuffer.toString(),
                            executedSql,
                            rowParameterList,
                            true,
                            true,
                            false);
                } else {
                    queryEditor.setQuery(showedSqlBuffer.toString(), true, false, false);
                }
            }
        }
    }

    /**
     * Update data
     *
     * @param parameterList List<PstmtParameter>
     */
    private void updateData(List<PstmtParameter> parameterList) {

        taskList.clear();

        String fileName = fileNameTxt.getText();
        String charset = fileCharsetCombo.getText();
        int threadCount = threadCountSpinner.getSelection();
        int commitedLineOnce = commitLineSpinner.getSelection();
        int totalLine = Integer.parseInt(totalLinesText.getText());
        int threadLine = totalLine / threadCount;
        int line = totalLine % threadCount;
        int startRow = 0;
        int totalProgress = 0;

        boolean isIgnoreError = importErrorControl.isIgoreOrBreak();
        if (isIgnoreError) {
            errorLogDir = PstmtDataTask.makeErrorLogDir();
        } else {
            errorLogDir = null;
        }
        if (importFileHandler == null) {
            importFileHandler = ImportFileHandlerFactory.getHandler(fileName, charset);
        }

        for (int i = 0; i < threadCount; i++) { // FIXME move this logic to core module
            int rowCount = threadLine;
            if (i == threadCount - 1) {
                rowCount = threadLine + line;
            }
            int commitCount =
                    rowCount / commitedLineOnce + (rowCount % commitedLineOnce > 0 ? 1 : 0);
            int taskProgress =
                    commitCount * PstmtDataTask.PROGRESS_COMMIT
                            + rowCount * PstmtDataTask.PROGRESS_ROW;
            totalProgress = totalProgress + taskProgress + PstmtDataTask.PROGRESS_ROW;
            PstmtDataTask task =
                    new PstmtDataTask(
                            sqlTxt.getText(),
                            database,
                            fileNameTxt.getText(),
                            parameterList,
                            startRow,
                            rowCount,
                            commitedLineOnce,
                            charset,
                            firstRowAsColumnBtn.getSelection(),
                            isIgnoreError,
                            errorLogDir,
                            importFileHandler);
            task.setTotalProgress(taskProgress);
            taskList.add(task);
            startRow += rowCount;
        }

        getShell().setVisible(false);
        beginTimestamp = System.currentTimeMillis();

        String jobName = Messages.executeSqlJobName;
        JobFamily jobFamily = new JobFamily();
        String serverName = database.getServer().getServerInfo().getServerName();
        String dbName = database.getName();
        jobFamily.setServerName(serverName);
        jobFamily.setDbName(dbName);

        final IProgressMonitor pmGroup = Job.getJobManager().createProgressGroup();
        pmGroup.beginTask(jobName, totalProgress);
        setStartShowResult(false);
        final CountDownLatch cdl = new CountDownLatch(taskList.size());
        final List<PstmtDataTask> threadTaskList = taskList;
        for (PstmtDataTask task : taskList) {
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
                            if (event.getResult() == Status.CANCEL_STATUS) {
                                for (PstmtDataTask task : threadTaskList) {
                                    if (!task.isCancel()) {
                                        task.cancel();
                                    }
                                }
                            }
                            countDownAndAwait(cdl);
                            Display.getDefault()
                                    .syncExec(
                                            new Runnable() {
                                                public void run() {
                                                    pmGroup.done();
                                                    finish();
                                                }
                                            });
                        }
                    };
            taskExec.addTask(task);

            TaskJob job = new TaskJob(jobName, taskExec);
            if (jobFamily != null) {
                job.setJobFamily(jobFamily);
            }
            job.setPriority(Job.LONG);
            job.setUser(false);
            job.setProgressGroup(pmGroup, task.getTotalProgress());
            job.schedule();
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {

            }
            pmGroup.worked(PstmtDataTask.PROGRESS_ROW);
        }
    }

    /**
     * Count down latch and wait for other thread.
     *
     * @param cdl CountDownLatch
     */
    public void countDownAndAwait(CountDownLatch cdl) {
        try {
            cdl.countDown();
            cdl.await();
        } catch (InterruptedException e) {
            // Do nothing here.
        }
    }

    /** After task finished, call it */
    private void finish() {
        if (getStartShowResult()) {
            return;
        } else {
            setStartShowResult(true);
        }

        if (importFileHandler instanceof XLSImportFileHandler) {
            XLSImportFileHandler xlsHandler = (XLSImportFileHandler) importFileHandler;
            xlsHandler.dispose();
        } else if (importFileHandler instanceof XLSXImportFileHandler) {
            XLSXImportFileHandler xlsHandler = (XLSXImportFileHandler) importFileHandler;
            xlsHandler.dispose();
        }

        long endTimestamp = System.currentTimeMillis();
        String spendTime = calcSpendTime(beginTimestamp, endTimestamp);

        List<Status> allStatusList = new ArrayList<Status>();
        int commitedCount = 0;
        boolean isHasError = false;
        boolean isCancel = false;
        List<String> errorList = new ArrayList<String>();
        int totalErrorCount = 0;
        for (PstmtDataTask task : taskList) {
            commitedCount += task.getCommitedCount();
            isCancel = isCancel || task.isCancel();
            if (!task.isSuccess() && task.getErrorMsg() != null) {
                Status status =
                        new Status(IStatus.ERROR, CommonUIPlugin.PLUGIN_ID, task.getErrorMsg());
                allStatusList.add(status);
                isHasError = true;
            }

            errorList.addAll(task.getErrorMsgList());
            totalErrorCount = totalErrorCount + task.getTotalErrorCount();
            if (!errorList.isEmpty()) {
                isHasError = true;
            }
        }

        if (!isHasError && isCancel) {
            String msg =
                    Messages.bind(
                            Messages.msgCancelExecSql,
                            new String[] {String.valueOf(commitedCount), spendTime});
            CommonUITool.openInformationBox(getShell(), Messages.titleExecuteResult, msg);
            close();
            deleteLog();
            return;
        }

        String successMsg =
                Messages.bind(
                        Messages.msgSuccessExecSql,
                        new String[] {String.valueOf(commitedCount), spendTime});
        if (!isHasError) {
            CommonUITool.openInformationBox(getShell(), Messages.titleExecuteResult, successMsg);
            close();
            deleteLog();
            return;
        }

        if (errorList.isEmpty()) { // break
            Status status = new Status(IStatus.INFO, CommonUIPlugin.PLUGIN_ID, successMsg + "\r\n");
            allStatusList.add(0, status);

            IStatus[] errors = new IStatus[allStatusList.size()];
            allStatusList.toArray(errors);
            MultiStatus multiStatus =
                    new MultiStatus(
                            CommonUIPlugin.PLUGIN_ID,
                            IStatus.OK,
                            errors,
                            Messages.msgDetailCause,
                            null);

            String errorMsg =
                    Messages.bind(
                            Messages.errExecSql,
                            new String[] {String.valueOf(commitedCount), spendTime});

            ErrorDialog errorDialog =
                    new ErrorDialog(
                            getShell(),
                            Messages.titleExecuteResult,
                            errorMsg,
                            multiStatus,
                            IStatus.INFO | IStatus.ERROR);
            errorDialog.open();
            if (isHasError) {
                getShell().setVisible(true);
            } else {
                close();
            }
        } else { // ignore the error
            String msg =
                    Messages.bind(Messages.importColumnNOTotal, String.valueOf(totalErrorCount))
                            + "\r\n"
                            + successMsg;
            ImportResultDialog dialog =
                    new ImportResultDialog(getShell(), msg, errorList, errorLogDir);
            dialog.open();
            close();
        }
        deleteLog();
    }

    /** Delete log */
    private void deleteLog() {
        if (errorLogDir != null) {
            File file = new File(errorLogDir);
            PstmtDataTask.deleteAllFile(file);
        }
    }
}
