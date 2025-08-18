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
package com.cubrid.common.ui.common.sqlrunner.dialog;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.common.sqlrunner.ViewFailedSQLTableContentProvider;
import com.cubrid.common.ui.common.sqlrunner.ViewFailedSQLTableLabelProvider;
import com.cubrid.common.ui.common.sqlrunner.model.SqlRunnerFailed;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

/**
 * ViewSQLLogDialog Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-5-9 created by Kevin.Wang
 */
public class ViewSQLLogDialog extends TrayDialog {

    private final int SAVE_ID = 1000;
    private String sqlFileName;
    private List<SqlRunnerFailed> failedList;
    private StyledText detailText;

    /**
     * The constructor
     *
     * @param shell
     * @param sqlFileName
     * @param failedList
     */
    public ViewSQLLogDialog(Shell shell, String sqlFileName, List<SqlRunnerFailed> failedList) {
        super(shell);
        this.sqlFileName = sqlFileName;
        this.failedList = failedList;
    }

    /**
     * Create dialog area content
     *
     * @param parent the parent composite
     * @return the control
     */
    protected Control createDialogArea(Composite parent) {
        Composite container = new Composite(parent, SWT.None);
        container.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        container.setLayout(new FormLayout());

        final TableViewer tableViewer =
                new TableViewer(
                        container, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
        tableViewer
                .getTable()
                .setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        tableViewer.getTable().setLinesVisible(true);
        tableViewer.getTable().setHeaderVisible(true);
        tableViewer.getTable().setToolTipText("");
        ColumnViewerToolTipSupport.enableFor(tableViewer, ToolTip.NO_RECREATE);

        FormData tableData = new FormData();
        tableData.top = new FormAttachment(0, 0);
        tableData.bottom = new FormAttachment(60, 0);
        tableData.left = new FormAttachment(0, 0);
        tableData.right = new FormAttachment(100, 0);
        tableViewer.getTable().setLayoutData(tableData);

        final TableViewerColumn lineIndex = new TableViewerColumn(tableViewer, SWT.NONE);
        lineIndex.getColumn().setWidth(40);
        lineIndex.getColumn().setText(Messages.failedSQLlineNumber);

        final TableViewerColumn sqlColumn = new TableViewerColumn(tableViewer, SWT.NONE);
        sqlColumn.getColumn().setWidth(400);
        sqlColumn.getColumn().setText(Messages.failedSQL);

        final TableViewerColumn errColumn = new TableViewerColumn(tableViewer, SWT.NONE);
        errColumn.getColumn().setWidth(400);
        errColumn.getColumn().setText(Messages.failedErrorMessage);

        tableViewer.setContentProvider(new ViewFailedSQLTableContentProvider());
        tableViewer.setLabelProvider(new ViewFailedSQLTableLabelProvider());
        tableViewer.setInput(failedList);

        sqlColumn.setLabelProvider(
                new ColumnLabelProvider() {
                    public String getToolTipText(Object element) {
                        SqlRunnerFailed failedObj = (SqlRunnerFailed) element;
                        return failedObj.getSql();
                    }
                });

        errColumn.setLabelProvider(
                new ColumnLabelProvider() {
                    public String getToolTipText(Object element) {
                        SqlRunnerFailed failedObj = (SqlRunnerFailed) element;
                        return failedObj.getErrorMessage();
                    }
                });

        tableViewer
                .getTable()
                .addSelectionListener(
                        new SelectionAdapter() {
                            public void widgetSelected(SelectionEvent e) {
                                TableItem[] items = tableViewer.getTable().getSelection();
                                if (items.length == 1) {
                                    updateDetialInfo((SqlRunnerFailed) items[0].getData());
                                }
                            }
                        });

        detailText = new StyledText(container, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.WRAP);
        detailText.setEditable(false);
        FormData detailData = new FormData();
        detailData.top = new FormAttachment(60, 5);
        detailData.bottom = new FormAttachment(100, -5);
        detailData.left = new FormAttachment(0, 0);
        detailData.right = new FormAttachment(100, 0);
        detailText.setLayoutData(detailData);

        return container;
    }

    private void updateDetialInfo(SqlRunnerFailed runSQLFaild) {
        String strLine = Messages.lblLine;
        String strSQL = Messages.lblSql;
        String strErr = Messages.lblError;

        StyleRange lineLabelStyle = new StyleRange();
        lineLabelStyle.fontStyle = SWT.BOLD;
        StyleRange sqlLabelStyle = new StyleRange();
        sqlLabelStyle.fontStyle = SWT.BOLD;
        StyleRange errorLabelStyle = new StyleRange();
        errorLabelStyle.fontStyle = SWT.BOLD;

        StringBuilder sb = new StringBuilder();
        lineLabelStyle.start = sb.length();
        lineLabelStyle.length = strLine.length();

        sb.append(strLine).append(StringUtil.NEWLINE);
        sb.append(runSQLFaild.getLineIndex()).append(StringUtil.NEWLINE);

        boolean isShowSql = StringUtil.isNotEmpty(runSQLFaild.getSql());
        if (isShowSql) {
            sqlLabelStyle.start = sb.length();
            sqlLabelStyle.length = strSQL.length();

            sb.append(strSQL).append(StringUtil.NEWLINE);
            sb.append(runSQLFaild.getSql()).append(StringUtil.NEWLINE);
        }

        errorLabelStyle.start = sb.length();
        errorLabelStyle.length = strErr.length();
        errorLabelStyle.foreground = ResourceManager.getColor(SWT.COLOR_RED);

        sb.append(strErr).append(StringUtil.NEWLINE);
        sb.append(runSQLFaild.getErrorMessage());

        detailText.setText(sb.toString());
        detailText.setStyleRange(lineLabelStyle);
        if (isShowSql) {
            detailText.setStyleRange(sqlLabelStyle);
        }
        detailText.setStyleRange(errorLabelStyle);
    }

    /**
     * When press button,call it
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == SAVE_ID) {
            Map<String, List<SqlRunnerFailed>> dataMap =
                    new HashMap<String, List<SqlRunnerFailed>>();
            dataMap.put(sqlFileName, failedList);
            new ExportErrorDataDialog(getShell(), dataMap, sqlFileName).open();
        }

        setReturnCode(buttonId);
        close();
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setText(Messages.titleViewSQLLog);
        getShell().setMinimumSize(900, 600);
        CommonUITool.centerShell(getShell());
    }

    /**
     * Create buttons for button bar
     *
     * @param parent the parent composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, SAVE_ID, Messages.btnSave, false);
        createButton(parent, IDialogConstants.OK_ID, Messages.btnOK, true);
    }
}
