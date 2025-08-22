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

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.query.editor.QueryUnit;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.progress.CommonTaskJobExec;
import com.cubrid.common.ui.spi.progress.ITaskExecutorInterceptor;
import com.cubrid.common.ui.spi.progress.JobFamily;
import com.cubrid.common.ui.spi.progress.TaskJobExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.TableUtil;
import com.cubrid.cubridmanager.core.cubrid.table.model.DBAttrTypeFormatter;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import com.cubrid.cubridmanager.core.cubrid.table.model.FormatDataResult;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * The dialog for doing with a data by prepared statement
 *
 * @author pangqiren 2009-6-4
 */
public class PstmtOneDataDialog extends PstmtDataDialog implements ITaskExecutorInterceptor {

    protected QueryUnit editorInput = new QueryUnit();
    private long beginTimestamp;

    /**
     * The constructor
     *
     * @param parentShell
     * @param database
     */
    public PstmtOneDataDialog(Shell parentShell, CubridDatabase database) {
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
    public PstmtOneDataDialog(
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
        createParameterTable(parent);
        setTitle(Messages.titlePstmtDataDialog);
        setMessage(Messages.msgPstmtOneDataDialog);
        return parent;
    }

    /**
     * Create the parameter table
     *
     * @param parent Composite
     */
    protected void createParameterTable(Composite parent) {

        Group parameterGroup = new Group(parent, SWT.NONE);
        {
            parameterGroup.setText(Messages.grpParameters);
            GridData gridData = new GridData(GridData.FILL_BOTH);
            parameterGroup.setLayoutData(gridData);
            parameterGroup.setLayout(new GridLayout());
        }

        createParameterTable(parameterGroup, false);
    }

    /**
     * Handle value modify event
     *
     * @param item TableItem
     */
    protected void handleValue(final TableItem item) {
        String type = item.getText(1);
        if (DBAttrTypeFormatter.isMuchValueType(type)) {
            handleValueOutPlace(item);
        } else {
            handleValueInPlace(item);
        }
    }

    /**
     * Edit value out place
     *
     * @param item TableItem
     */
    private void handleValueOutPlace(final TableItem item) {
        SetPstmtValueDialog dialog = new SetPstmtValueDialog(getShell(), item, database, 2);
        if (IDialogConstants.OK_ID == dialog.open()) {
            packTable();
        }
        validate();
    }

    /**
     * Edit value in place
     *
     * @param item TableItem
     */
    private void handleValueInPlace(final TableItem item) {

        final Text newEditor = new Text(parameterTable, SWT.MULTI | SWT.WRAP);
        final int editColumn = 2;

        newEditor.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        if (!validate(newEditor.getText(), item)) {
                            getButton(IDialogConstants.OK_ID).setEnabled(false);
                        }
                    }
                });

        newEditor.addFocusListener(
                new FocusAdapter() {
                    public void focusLost(FocusEvent event) {
                        if (isChanging) {
                            return;
                        }
                        isChanging = true;
                        if (validate(newEditor.getText(), item)) {
                            item.setText(editColumn, newEditor.getText());
                        }
                        newEditor.dispose();
                        isChanging = false;
                        packTable();
                        validate();
                    }
                });

        // add listener for key pressed
        newEditor.addTraverseListener(
                new TraverseListener() {
                    public void keyTraversed(TraverseEvent event) {
                        if (event.detail == SWT.TRAVERSE_RETURN) {
                            if (isChanging) {
                                return;
                            }
                            isChanging = true;
                            if (validate(newEditor.getText(), item)) {
                                item.setText(editColumn, newEditor.getText());
                            }
                            newEditor.dispose();
                            isChanging = false;
                            packTable();
                            validate();
                            event.doit = true;
                            int selItem = 0;
                            for (int i = 0; i < parameterTable.getItemCount(); i++) {
                                if (item == parameterTable.getItem(i)) {
                                    selItem = i;
                                    break;
                                }
                            }
                            selItem = selItem + 1;
                            if (selItem >= parameterTable.getItemCount()) {
                                getButton(IDialogConstants.OK_ID).setFocus();
                            } else {
                                handleType(parameterTable.getItem(selItem));
                            }
                        } else if (event.detail == SWT.TRAVERSE_ESCAPE) {
                            if (isChanging) {
                                return;
                            }
                            isChanging = true;
                            newEditor.dispose();
                            event.doit = false;
                            isChanging = false;
                        }
                    }
                });

        tableEditor.setEditor(newEditor, item, editColumn);
        newEditor.setText(item.getText(editColumn));
        newEditor.selectAll();
        newEditor.setFocus();
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
        getButton(IDialogConstants.OK_ID).setEnabled(false);
        for (int i = 0; i < parameterTable.getItemCount(); i++) {
            TableItem item = parameterTable.getItem(i);
            String value = item.getText(2);
            if (!validate(value, item)) {
                return false;
            }
        }
        getButton(IDialogConstants.OK_ID).setEnabled(true);
        return true;
    }

    /**
     * Validate data
     *
     * @param data The String
     * @param item The TableItem
     * @return boolean
     */
    private boolean validate(String data, TableItem item) {
        setErrorMessage(null);
        String paraName = item.getText(0);
        String paraType = item.getText(1);
        if (!validateType(paraName, paraType)) {
            return false;
        }
        if (DBAttrTypeFormatter.isMuchValueType(paraType) && DBAttrTypeFormatter.isFilePath(data)) {
            return true;
        }
        /*For bug TOOLS-3119*/
        int index = parameterTable.indexOf(item);
        if (data.length() > 0) {
            FormatDataResult formatDataResult =
                    DBAttrTypeFormatter.format(
                            DataType.getRealType(paraType),
                            DataType.NULL_EXPORT_FORMAT.equals(data) ? null : data,
                            false,
                            database.getDatabaseInfo().getCharSet(),
                            true);
            if (!formatDataResult.isSuccess()) {
                setErrorMessage(
                        Messages.bind(
                                Messages.errParaTypeValueMapping,
                                new String[] {
                                    String.valueOf(index + 1),
                                    item.getText(0),
                                    DataType.getRealType(paraType)
                                }));
                return false;
            }
        } else {
            setErrorMessage(
                    Messages.bind(
                            Messages.msgParaValue,
                            new String[] {paraName, String.valueOf(index + 1)}));
            return false;
        }
        return true;
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     * @param buttonId the id of the button that was pressed (see <code>IDialogConstants.*_ID</code>
     *     constants)
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            if (validate()) {
                List<PstmtParameter> parameterList = new ArrayList<PstmtParameter>();
                for (int i = 0; i < parameterTable.getItemCount(); i++) {
                    String name = parameterTable.getItem(i).getText(0);
                    String type = parameterTable.getItem(i).getText(1);
                    String value = parameterTable.getItem(i).getText(2);
                    if (DataType.NULL_EXPORT_FORMAT.equals(value)) {
                        value = null;
                    }
                    PstmtParameter pstmtParameter = new PstmtParameter(name, i + 1, type, value);

                    boolean isFile = DBAttrTypeFormatter.isFilePath(value);
                    if (isFile) {
                        String charSet =
                                (String)
                                        parameterTable
                                                .getItem(i)
                                                .getData(SetPstmtValueDialog.FILE_CHARSET);
                        pstmtParameter.setCharSet(charSet);
                    }
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
     * Open the query editor and show result set
     *
     * @param parameterList List<PstmtParameter>
     */
    private void showResultSet(List<PstmtParameter> parameterList) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }
        String querySql = sqlTxt.getText();

        close();

        IEditorPart editor = window.getActivePage().findEditor(editorInput);
        if (editor == null) {
            try {
                editor = window.getActivePage().openEditor(editorInput, QueryEditorPart.ID);
            } catch (PartInitException e) {
                editor = null;
            }
        }
        if (editor != null) {
            window.getActivePage().bringToTop(editor);
            QueryEditorPart queryEditor = ((QueryEditorPart) editor);
            if (!queryEditor.isConnected() && database != null) {
                queryEditor.connect(database);
            }
            String allInputSql = getCommentSqlValue(parameterList) + querySql;

            List<List<PstmtParameter>> rowParameterList = new ArrayList<List<PstmtParameter>>();
            rowParameterList.add(parameterList);
            if (queryEditor.isConnected()) {
                queryEditor.setQuery(allInputSql, querySql, rowParameterList, true, true, false);
            }
        }
    }

    /**
     * Update the data
     *
     * @param parameterList List<PstmtParameter>
     */
    private void updateData(List<PstmtParameter> parameterList) {
        beginTimestamp = System.currentTimeMillis();
        String jobName = Messages.executeSqlJobName;
        JobFamily jobFamily = new JobFamily();
        String serverName = database.getServer().getServerInfo().getServerName();
        String dbName = database.getName();
        jobFamily.setServerName(serverName);
        jobFamily.setDbName(dbName);

        TaskJobExecutor taskExec = new CommonTaskJobExec(this);

        PstmtDataTask task = new PstmtDataTask(sqlTxt.getText(), database, parameterList, null);
        taskExec.addTask(task);
        taskExec.schedule(jobName, jobFamily, false, Job.SHORT);
    }

    public void setEditorInput(QueryUnit editorInput) {
        this.editorInput = editorInput;
    }

    /** After finish import data, pop up the information */
    public void completeAll() {
        long endTimestamp = System.currentTimeMillis();
        String spendTime = calcSpendTime(beginTimestamp, endTimestamp);
        CommonUITool.openInformationBox(
                getShell(),
                Messages.titleExecuteResult,
                Messages.bind(Messages.msgExecuteResult, spendTime));
    }

    /**
     * After task finished, refresh UI
     *
     * @param task ITask
     * @return IStatus
     */
    public IStatus postTaskFinished(ITask task) {
        return Status.OK_STATUS;
    }
}
