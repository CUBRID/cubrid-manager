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

package com.cubrid.common.ui.query.builder.quickbuilder;

import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.control.SQLEditorComposite;
import com.cubrid.common.ui.query.editor.ColumnProposal;
import com.cubrid.common.ui.query.editor.ColumnProposalAdvisor;
import com.cubrid.common.ui.query.editor.ColumnProposalDetailInfo;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * SQL Quick Builder Dialog
 *
 * @author Isaiah Choe 2012-10-18
 */
public final class QuickBuilderDialog extends Dialog {
    private Text inputText;
    private TableViewer searchView;
    private Shell shell;
    private SQLEditorComposite sqlComp;
    private ColumnProposal proposal;
    private boolean isSupportLimit = false;
    private Timer proposalUpdateTimer;
    private DatabaseInfo databaseInfo;
    private Label updateNoticeBanner;

    public QuickBuilderDialog(Shell parent, int style) {
        super(parent, style);
    }

    public void open() {
        createContents();
        CommonUITool.centerShell(shell);
        shell.open();
        shell.layout();
        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }

        if (proposalUpdateTimer != null) {
            proposalUpdateTimer.cancel();
        }
    }

    private void startTimerForUpdateProposal() {
        TimerTask proposalUpdateNotifyTask =
                new TimerTask() {
                    public void run() {
                        ColumnProposal proposalTemp =
                                ColumnProposalAdvisor.getInstance().findProposal(databaseInfo);
                        if (proposalTemp == null) {
                            return;
                        }

                        if (proposalUpdateTimer != null) {
                            proposalUpdateTimer.cancel();
                            proposalUpdateTimer = null;
                        }

                        proposal = proposalTemp;
                        Display.getDefault()
                                .syncExec(
                                        new Runnable() {
                                            public void run() {
                                                if (updateNoticeBanner != null) {
                                                    updateNoticeBanner.dispose();
                                                    updateNoticeBanner = null;
                                                }
                                                if (searchView != null) {
                                                    searchView.setInput(proposal);
                                                }
                                                shell.layout(true, true);
                                            }
                                        });
                    }
                };

        proposalUpdateTimer = new Timer(true);
        proposalUpdateTimer.schedule(proposalUpdateNotifyTask, 500);
    }

    protected void createContents() {
        sqlComp = CommonUITool.getActiveSQLEditorComposite();
        if (sqlComp == null) {
            closeThisDialog();
            return;
        }

        CubridDatabase cubridDatabase = sqlComp.getQueryEditorPart().getSelectedDatabase();
        if (CubridDatabase.hasValidDatabaseInfo(cubridDatabase)) {
            databaseInfo = cubridDatabase.getDatabaseInfo();
        }

        boolean loadedProposal = true;
        proposal = ColumnProposalAdvisor.getInstance().findProposal(databaseInfo);
        if (proposal == null) {
            proposal = new ColumnProposal();

            if (databaseInfo != null) {
                startTimerForUpdateProposal();
            }

            loadedProposal = false;
        }

        isSupportLimit = CompatibleUtil.isSupportLimit(cubridDatabase.getDatabaseInfo());

        shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        {
            GridLayout gl = new GridLayout();
            gl.numColumns = 1;
            shell.setLayout(gl);
        }
        shell.setSize(450, 300);
        shell.setText(Messages.quickQueryBuilderTitle);

        final Composite composite = new Composite(shell, SWT.NONE);
        {
            GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
            composite.setLayoutData(gd);
            GridLayout gl = new GridLayout();
            composite.setLayout(gl);
        }

        final Label findWhatLabel = new Label(composite, SWT.NONE);
        findWhatLabel.setText(Messages.quickQueryBuilderLabel);

        inputText = new Text(composite, SWT.BORDER);
        {
            GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
            inputText.setLayoutData(gd);
        }
        inputText.setEditable(true);
        inputText.addKeyListener(inputTextKeyListener);

        searchView = new TableViewer(composite, SWT.BORDER);
        {
            GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
            searchView.getTable().setLayoutData(gd);
        }
        searchView.setContentProvider(searchViewContentProvider);
        searchView.setLabelProvider(searchViewLabelProvider);
        searchView.setInput(proposal);
        searchView
                .getTable()
                .addKeyListener(
                        new KeyListener() {
                            public void keyReleased(KeyEvent e) {}

                            public void keyPressed(KeyEvent e) {
                                if (e.keyCode == SWT.CR) {
                                    makeQueryAndClose(0);
                                }
                            }
                        });

        TableColumn col1 = new TableColumn(searchView.getTable(), SWT.NONE);
        col1.setWidth(200);
        TableColumn col2 = new TableColumn(searchView.getTable(), SWT.NONE);
        col2.setWidth(200);

        if (!loadedProposal) {
            updateNoticeBanner = new Label(composite, SWT.BORDER);
            {
                GridData gd = new GridData(SWT.FILL, SWT.BOTTOM, true, false);
                updateNoticeBanner.setLayoutData(gd);
            }
            updateNoticeBanner.setText(Messages.quickQueryBuilderLoading);
            updateNoticeBanner.setBackground(ResourceManager.getColor(255, 255, 255));
        }

        Composite bottomPanel = new Composite(composite, SWT.NONE);
        {
            GridLayout gl = new GridLayout();
            gl.numColumns = 4;
            bottomPanel.setLayout(gl);
            GridData gd = new GridData(SWT.FILL, SWT.BOTTOM, true, false);
            bottomPanel.setLayoutData(gd);
        }

        createButtons(bottomPanel);
    }

    private void createButtons(Composite bottomPanel) {
        Button btnSelectSql = new Button(bottomPanel, SWT.PUSH);
        btnSelectSql.setText(Messages.quickQueryBuilderBtnSelect1);
        btnSelectSql.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        makeQueryAndClose(0);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {}
                });

        Button btnSelectSql2 = new Button(bottomPanel, SWT.PUSH);
        btnSelectSql2.setText(Messages.quickQueryBuilderBtnSelect2);
        btnSelectSql2.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        makeQueryAndClose(1);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {}
                });

        Button btnInsertSql = new Button(bottomPanel, SWT.PUSH);
        btnInsertSql.setText(Messages.quickQueryBuilderBtnInsert);
        btnInsertSql.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        makeQueryAndClose(2);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {}
                });

        Button btnUpdateSql = new Button(bottomPanel, SWT.PUSH);
        btnUpdateSql.setText(Messages.quickQueryBuilderBtnUpdate);
        btnUpdateSql.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        makeQueryAndClose(3);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {}
                });
    }

    private IStructuredContentProvider searchViewContentProvider =
            new IStructuredContentProvider() {
                public void dispose() {}

                public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

                public Object[] getElements(Object inputElement) {
                    ColumnProposal columnProposal = (ColumnProposal) inputElement;
                    if (columnProposal.getTableNames() == null) {
                        return new Object[0];
                    }

                    String keyword = inputText.getText().trim().toLowerCase();
                    if (keyword.length() == 0) {
                        return columnProposal.getTableNames().toArray(new String[0]);
                    }

                    List<String> searchedTableNames = new ArrayList<String>();
                    List<String> tableNames = columnProposal.getTableNames();
                    for (String tableName : tableNames) {
                        String tableNameLower = tableName.toLowerCase();
                        if (tableNameLower.startsWith(keyword)) {
                            searchedTableNames.add(tableName);
                        }
                    }

                    return searchedTableNames.toArray(new String[0]);
                }
            };

    private ITableLabelProvider searchViewLabelProvider =
            new ITableLabelProvider() {
                public void removeListener(ILabelProviderListener listener) {}

                public boolean isLabelProperty(Object element, String property) {
                    return false;
                }

                public void dispose() {}

                public void addListener(ILabelProviderListener listener) {}

                public String getColumnText(Object element, int columnIndex) {
                    String tableName = (String) element;
                    if (columnIndex == 0) {
                        return tableName;
                    } else {
                        SchemaInfo info = proposal.getSchemaInfos(tableName);
                        if (info == null) {
                            return "1";
                        }

                        return info.getDescription();
                    }
                }

                public Image getColumnImage(Object element, int columnIndex) {
                    return null;
                }
            };

    private KeyListener inputTextKeyListener =
            new KeyListener() {
                public void keyReleased(KeyEvent e) {
                    searchView.setInput(proposal);
                    searchView.getTable().select(0);
                }

                public void keyPressed(KeyEvent e) {
                    if ((e.stateMask & SWT.CTRL) != 0
                            && (e.stateMask & SWT.SHIFT) == 0
                            && (e.stateMask & SWT.ALT) == 0
                            && e.keyCode == ',') {
                        closeThisDialog();
                    } else if (e.keyCode == SWT.TAB || e.keyCode == SWT.ESC) {
                        closeThisDialog();
                    } else if (
                    /*e.keyCode == ' ' || */ e.keyCode == SWT.CR) {
                        makeQueryAndClose(0);
                    } else if (e.keyCode == SWT.ARROW_DOWN) {
                        searchView.getTable().setFocus();
                    }
                }
            };

    private String getSelectedTable() {
        TableItem[] tableItems = searchView.getTable().getSelection();
        if (tableItems == null || tableItems.length == 0) {
            return null;
        }

        return tableItems[0].getText();
    }

    private void makeQueryAndClose(int type) { // FIXME move this logic to core module
        String tableName = getSelectedTable().trim();
        List<ColumnProposalDetailInfo> columns = null;
        String query = null;

        ColumnProposal proposal =
                ColumnProposalAdvisor.getInstance()
                        .findProposal(
                                sqlComp.getQueryEditorPart()
                                        .getSelectedDatabase()
                                        .getDatabaseInfo());
        if (proposal != null) {
            columns = proposal.getColumns().get(tableName);
        }
        if (columns == null) {
            columns = new ArrayList<ColumnProposalDetailInfo>();
        }

        int cursorPosition = 0;

        if (type == 0) {
            query =
                    "SELECT * FROM "
                            + QuerySyntax.escapeKeyword(tableName)
                            + " "
                            + appendLimit()
                            + ";"
                            + StringUtil.NEWLINE;
            cursorPosition = query.length() - 1;
        } else if (type == 1) {
            StringBuilder col = new StringBuilder();
            if (columns != null) {
                for (ColumnProposalDetailInfo info : columns) {
                    String column = info.getColumnName();
                    if (col.length() > 0) {
                        col.append(", ");
                    }
                    col.append(QuerySyntax.escapeKeyword(column));
                }
            }
            query =
                    "SELECT "
                            + col.toString()
                            + " FROM "
                            + QuerySyntax.escapeKeyword(tableName)
                            + " "
                            + appendLimit()
                            + ";"
                            + StringUtil.NEWLINE;
            cursorPosition = query.length() - 1;
        } else if (type == 2) {
            StringBuilder col = new StringBuilder();
            StringBuilder col2 = new StringBuilder();
            if (columns != null) {
                for (ColumnProposalDetailInfo info : columns) {
                    String column = info.getColumnName();
                    if (col.length() > 0) {
                        col.append(", ");
                        col2.append(", ");
                    }
                    col.append(QuerySyntax.escapeKeyword(column));
                    col2.append(column);
                }
            }
            query =
                    "INSERT INTO "
                            + QuerySyntax.escapeKeyword(tableName)
                            + " ("
                            + col.toString()
                            + ") VALUES ("
                            + col2.toString()
                            + ");\n";
            cursorPosition = 14 + tableName.length() + col.length() + 10;
        } else if (type == 3) {
            StringBuilder col = new StringBuilder();
            if (columns != null) {
                for (ColumnProposalDetailInfo info : columns) {
                    String column = info.getColumnName();
                    if (col.length() > 0) {
                        col.append(", ");
                    }
                    col.append(QuerySyntax.escapeKeyword(column)).append("=");
                }
            }
            query =
                    "UPDATE "
                            + QuerySyntax.escapeKeyword(tableName)
                            + " SET "
                            + col.toString()
                            + " WHERE ;"
                            + StringUtil.NEWLINE;
            cursorPosition = 12 + tableName.length();
        }

        pasteIntoQueryEditor(query, cursorPosition);
        closeThisDialog();
    }

    private String appendLimit() { // FIXME move this logic to core module
        if (isSupportLimit) {
            return " LIMIT 0, 100";
        } else {
            return " WHERE ROWNUM BETWEEN 1 AND 100";
        }
    }

    private void pasteIntoQueryEditor(String query, int cursor) {
        StyledText text = sqlComp.getText();
        int cursorOffset = text.getCaretOffset();
        int pos = cursorOffset + cursor - 1;
        if (pos < 0) {
            pos = 0;
        }
        text.getContent().replaceTextRange(cursorOffset, 0, query);
        try {
            text.setSelectionRange(pos, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeThisDialog() {
        shell.dispose();
    }
}
