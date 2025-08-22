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
package com.cubrid.common.ui.compare.data.control;

import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.FileUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.compare.Messages;
import com.cubrid.common.ui.compare.data.model.CompareViewData;
import com.cubrid.common.ui.compare.data.model.HashedCompareData;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.SQLGenerateUtils;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.slf4j.Logger;

public class DataCompareDetailEditorPart extends EditorPart {
    public static final String ID = DataCompareDetailEditorPart.class.getName();
    private static final Logger LOGGER = LogUtil.getLogger(DataCompareDetailEditorPart.class);
    private TableViewer compareTableViewer;
    private List<CompareViewData> dataList;
    private List<String> columnList;
    private DataCompareDetailEditorInput compInput;
    private Button btnPrev;
    private Button btnNext;
    private int page = 1;
    private static final int ROW_LIMIT = 50;

    public void createPartControl(Composite parent) {
        Composite container = createContainer(parent);
        createTopPanel(container);
        createContent(container);
    }

    private Composite createContainer(Composite parent) {
        final Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        return container;
    }

    private void createTopPanel(Composite parent) {
        final Composite container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        CommonUITool.createGridLayout(container, 2, 0, 0, 0, 0, 0, 0, 0, 0);

        final Composite left = new Composite(container, SWT.NONE);
        left.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false));
        CommonUITool.createGridLayout(left, 1, 0, 0, 0, 0, 0, 0, 0, 0);

        String srcDb =
                compInput.getSourceDB().getDbName() + "@" + compInput.getSourceDB().getBrokerIP();
        String tgtDb =
                compInput.getTargetDB().getDbName() + "@" + compInput.getTargetDB().getBrokerIP();
        String statusText =
                Messages.bind(
                        Messages.msgStatusText,
                        new String[] {
                            compInput.getSourceSchemaInfo().getUniqueName(), srcDb, tgtDb
                        });
        Label lblStatus = new Label(left, SWT.NONE);
        lblStatus.setText(statusText);

        final Composite right = new Composite(container, SWT.NONE);
        right.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
        CommonUITool.createGridLayout(right, 2, 0, 0, 0, 0, 0, 0, 0, 0);

        btnPrev = new Button(right, SWT.PUSH);
        btnPrev.setToolTipText(Messages.btnPrev);
        btnPrev.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_page_previous.png"));
        btnPrev.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        showPage(-1);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {}
                });

        btnNext = new Button(right, SWT.PUSH);
        btnNext.setToolTipText(Messages.btnNext);
        btnNext.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_page_next.png"));
        btnNext.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        showPage(1);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {}
                });
    }

    private void createContent(Composite parent) {
        compareTableViewer = new TableViewer(parent, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
        compareTableViewer.setUseHashlookup(true);
        CommonUITool.createGridLayout(compareTableViewer.getTable(), 1, 0, 10, 0, 10, 0, 0, 0, 0);
        compareTableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        compareTableViewer.getTable().setLinesVisible(true);
        compareTableViewer.getTable().setHeaderVisible(true);

        try {
            new ProgressMonitorDialog(this.getSite().getShell())
                    .run(
                            true,
                            false,
                            new IRunnableWithProgress() {
                                public void run(IProgressMonitor monitor) {
                                    monitor.beginTask(Messages.msgLoadingData, 1);
                                    loadData(1);
                                    monitor.done();

                                    Display.getDefault()
                                            .syncExec(
                                                    new Runnable() {
                                                        public void run() {
                                                            initTableViewer();
                                                            compareTableViewer.setInput(dataList);
                                                            CommonUITool.packTable(
                                                                    compareTableViewer.getTable(),
                                                                    20,
                                                                    200);
                                                        }
                                                    });
                                }
                            });
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    private void initTableViewer() {
        TableViewerColumn col = null;
        for (int i = 0, len = columnList.size(); i < len; i++) {
            String columnName = columnList.get(i);
            col = new TableViewerColumn(compareTableViewer, SWT.NONE);
            col.getColumn().setWidth(100);
            col.getColumn().setText(columnName);
        }

        if (dataList == null) {
            dataList = new ArrayList<CompareViewData>();
        }

        compareTableViewer.setContentProvider(new DataCompareViewContentProvider());
        compareTableViewer.setLabelProvider(new DataCompareViewLabelProvider());
        compareTableViewer.setInput(dataList);
    }

    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        setSite(site);
        setInput(input);

        compInput = (DataCompareDetailEditorInput) input;
    }

    private boolean loadData(int beginPage) { // FIXME logic code move to core module
        List<CompareViewData> newList = new ArrayList<CompareViewData>();
        List<String> pkColumns = new ArrayList<String>();
        List<String> pkTypes = new ArrayList<String>();

        if (beginPage <= 0) {
            beginPage = 1;
        }

        int rowCount = 0;
        int beginPos = (beginPage - 1) * ROW_LIMIT;
        int endPos = beginPos + ROW_LIMIT;

        String filepath = compInput.getDiffFilePath();
        BufferedReader in = null;
        Connection connSrc = null;
        Connection connTgt = null;

        try {
            String charset = compInput.getSourceDB().getCharSet();
            boolean preparedColumnMeta = false;

            synchronized (this) {
                in =
                        new BufferedReader(
                                new InputStreamReader(new FileInputStream(filepath), charset));

                String row = null;
                while ((row = in.readLine()) != null) {
                    String trimmedRow = row.trim();
                    if (trimmedRow.length() == 0) {
                        continue;
                    }

                    rowCount++;
                    if (rowCount < beginPos) {
                        continue;
                    }

                    if (rowCount > endPos) {
                        break;
                    }

                    String[] values = row.split("\t");
                    List<String> pkValues = new ArrayList<String>();
                    for (int i = 0; i < values.length; i += 3) {
                        if (!preparedColumnMeta) {
                            String columnName = values[0];
                            pkColumns.add(columnName);

                            String columnType = values[1];
                            pkTypes.add(columnType);
                        }

                        String columnValue = values[2];
                        pkValues.add(columnValue);
                    }

                    preparedColumnMeta = true;

                    CompareViewData src = new CompareViewData();
                    src.setIndex(rowCount);
                    src.setSource(true);
                    src.setPkColumnValues(pkValues);
                    newList.add(src);

                    CompareViewData tgt = new CompareViewData();
                    tgt.setIndex(rowCount);
                    tgt.setSource(false);
                    tgt.setPkColumnValues(pkValues);
                    newList.add(tgt);

                    src.setReferer(tgt);
                    tgt.setReferer(src);
                }
            }

            // if it's page is 1, it might allow no contents.
            if (newList.size() > 0 || page == 1 && beginPage == 1) {
                connSrc = JDBCConnectionManager.getConnection(compInput.getSourceDB(), true);
                connTgt = JDBCConnectionManager.getConnection(compInput.getTargetDB(), true);

                for (CompareViewData compViewData : newList) {
                    if (compViewData.isSource()) {
                        fetchData(
                                connSrc,
                                compViewData,
                                compInput.getSourceSchemaInfo(),
                                pkColumns,
                                pkTypes,
                                true);
                    } else {
                        fetchData(
                                connTgt,
                                compViewData,
                                compInput.getSourceSchemaInfo(),
                                pkColumns,
                                pkTypes,
                                false);
                    }
                }

                dataList = newList;
                return true;
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        } finally {
            FileUtil.close(in);
            QueryUtil.freeQuery(connSrc);
            QueryUtil.freeQuery(connTgt);
        }

        return false;
    }

    private void fetchData(
            Connection conn,
            CompareViewData obj,
            SchemaInfo schemaInfo,
            List<String> pkColumns,
            List<String> pkTypes,
            boolean isSource) { // FIXME logic code move to core module
        StringBuilder sql = new StringBuilder();
        sql.append(
                SQLGenerateUtils.getSelectSQLNoWhere(
                        schemaInfo.getUniqueName(), schemaInfo.getAttributes(), false));
        sql.append(" WHERE ");
        for (int i = 0; i < pkColumns.size(); i++) {
            String columnName = pkColumns.get(i);
            String columnType = pkTypes.get(i);
            String columnValue = obj.getPkColumnValues().get(i);

            sql.append(QuerySyntax.escapeKeyword(columnName)).append(" = ");
            if (HashedCompareData.NUMBER_TYPE.equals(columnType)) {
                sql.append(columnValue);
            } else {
                sql.append("'").append(columnValue).append("'");
            }
        }

        List<String> values = new ArrayList<String>();
        values.add(String.valueOf(obj.getIndex()));
        obj.setData(values);

        boolean makeColumnInfo = true;
        if (columnList == null) {
            columnList = new ArrayList<String>();
            columnList.add("NO");
            makeColumnInfo = false;
        }

        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql.toString());
            while (rs.next()) {
                ResultSetMetaData meta = rs.getMetaData();
                int columnCount = meta.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    if (!makeColumnInfo) {
                        String column = meta.getColumnName(i);
                        columnList.add(column);
                    }

                    String value = rs.getString(i);
                    values.add(value);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("", e);
        } finally {
            QueryUtil.freeQuery(stmt, rs);
        }
    }

    private void showPage(int direction) {
        int newPage = page;
        if (direction == -1) {
            newPage -= 1;
        } else if (direction == 1) {
            newPage += 1;
        } else {
            newPage = 1;
        }

        if (newPage < 1) {
            page = 1;
            CommonUITool.openWarningBox(Messages.msgStartPageAlert);
            return;
        }

        final int newPageParam = newPage;
        try {
            new ProgressMonitorDialog(this.getSite().getShell())
                    .run(
                            true,
                            false,
                            new IRunnableWithProgress() {
                                public void run(IProgressMonitor monitor) {
                                    monitor.beginTask(Messages.msgLoadingData, 1);
                                    if (loadData(newPageParam)) {
                                        Display.getDefault()
                                                .syncExec(
                                                        new Runnable() {
                                                            public void run() {
                                                                compareTableViewer.setInput(
                                                                        dataList);
                                                                CommonUITool.packTable(
                                                                        compareTableViewer
                                                                                .getTable(),
                                                                        20,
                                                                        200);
                                                            }
                                                        });
                                        page = newPageParam;
                                    } else {
                                        Display.getDefault()
                                                .syncExec(
                                                        new Runnable() {
                                                            public void run() {
                                                                CommonUITool.openWarningBox(
                                                                        Messages.msgLastPageAlert);
                                                            }
                                                        });
                                    }
                                    monitor.done();
                                }
                            });
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    public void doSave(IProgressMonitor monitor) {}

    public void doSaveAs() {}

    public boolean isDirty() {
        return false;
    }

    public boolean isSaveAsAllowed() {
        return false;
    }

    public void setFocus() {}
}
