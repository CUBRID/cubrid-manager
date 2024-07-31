/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.query.sqlmap;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.query.editor.QueryUnit;
import com.cubrid.common.ui.query.format.SqlFormattingStrategy;
import com.cubrid.common.ui.query.sqlmap.BindParameter.BindParameterType;
import com.cubrid.common.ui.spi.TableContentProvider;
import com.cubrid.common.ui.spi.TableLabelProvider;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.navercorp.dbtools.sqlmap.parser.MapperFile;
import com.navercorp.dbtools.sqlmap.parser.MapperParserImpl;
import com.navercorp.dbtools.sqlmap.parser.QueryCondition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;

/**
 * SQLMap Navigator View
 *
 * <p>This view is listing conditions and bind parameters in order to generate the query.
 *
 * @author CHOE JUNGYEON
 */
public class SqlmapNavigatorView extends ViewPart {

    private static final Logger LOGGER = LogUtil.getLogger(SqlmapNavigatorView.class);
    public static final String ID = "com.cubrid.common.navigator.sqlmapview";

    private Combo selector;
    private TableViewer condView;
    private TableViewer paramView;
    private Text sqlView;

    private static SqlFormattingStrategy formator = new SqlFormattingStrategy();

    @Override
    public void createPartControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createQueryIdView(composite);
        createCondView(composite);
        createParamView(composite);
        createSqlView(composite);
    }

    @Override
    public void setFocus() {}

    /**
     * Create the queryId selector view.
     *
     * @param parent
     */
    private void createQueryIdView(Composite parent) {
        Composite wrapper = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        wrapper.setLayout(layout);
        wrapper.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        selector = new Combo(wrapper, SWT.READ_ONLY);
        selector.setLayout(new GridLayout());
        selector.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    /**
     * Create the main view.
     *
     * @param parent {@link Composite} the parent composite
     */
    private void createCondView(Composite parent) {
        String[] columnNames = {Messages.lblSqlmapUse, Messages.lblSqlmapCondition};

        Label lblCondTitle = new Label(parent, SWT.NONE);
        lblCondTitle.setText(Messages.lblSqlmapConditions);

        condView =
                CommonUITool.createCommonTableViewer(
                        parent,
                        null,
                        columnNames,
                        CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, 80));

        condView.setColumnProperties(columnNames);
        condView.setContentProvider(new TableContentProvider());
        condView.setLabelProvider(new SqlmapLabelProvider(this));
        condView.setCellModifier(new SqlmapCellModifier(this));

        CellEditor[] cellEditor = new CellEditor[columnNames.length];
        cellEditor[0] = new CheckboxCellEditor(condView.getTable());
        condView.setCellEditors(cellEditor);

        TableLayout tableLayout = new TableLayout();
        tableLayout.addColumnData(new ColumnPixelData(30));
        tableLayout.addColumnData(new ColumnPixelData(160));
        condView.getTable().setLayout(tableLayout);

        condView.getTable()
                .addKeyListener(
                        new KeyAdapter() {
                            public void keyPressed(KeyEvent e) {
                                if (e.character == ' ') {
                                    toggleUsedBySelectedCondition();
                                }
                                super.keyPressed(e);
                            }
                        });
    }

    private void createParamView(Composite parent) {
        String[] columnNames = {
            Messages.lblSqlmapName, Messages.lblSqlmapValue, Messages.lblSqlmapType
        };

        Label lblCondTitle = new Label(parent, SWT.NONE);
        lblCondTitle.setText(Messages.lblSqlmapParameters);

        paramView =
                CommonUITool.createCommonTableViewer(
                        parent,
                        null,
                        columnNames,
                        CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, 120));

        paramView.setColumnProperties(columnNames);
        paramView.setContentProvider(new TableContentProvider());
        paramView.setLabelProvider(new TableLabelProvider());

        TableLayout tableLayout = new TableLayout();
        tableLayout.addColumnData(new ColumnPixelData(100));
        tableLayout.addColumnData(new ColumnPixelData(80));
        tableLayout.addColumnData(new ColumnPixelData(70));
        paramView.getTable().setLayout(tableLayout);

        paramView.addDoubleClickListener(
                new IDoubleClickListener() {
                    @SuppressWarnings("unchecked")
                    public void doubleClick(DoubleClickEvent event) {
                        StructuredSelection sel = (StructuredSelection) event.getSelection();
                        Map<String, String> data = (Map<String, String>) sel.getFirstElement();
                        modifyParam(data);
                    }
                });

        Menu menu = new Menu(paramView.getTable().getShell(), SWT.POP_UP);
        paramView.getTable().setMenu(menu);

        final MenuItem addMenuItem = new MenuItem(menu, SWT.PUSH);
        addMenuItem.setText(Messages.mnuSqlmapAdd);
        addMenuItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
                        addParameter();
                    }
                });

        final MenuItem modifyMenuItem = new MenuItem(menu, SWT.PUSH);
        modifyMenuItem.setText(Messages.mnuSqlmapModify);
        modifyMenuItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
                        modifyParameterByCurrentSelected();
                    }
                });

        final MenuItem delMenuItem = new MenuItem(menu, SWT.PUSH);
        delMenuItem.setText(Messages.mnuSqlmapRemove);
        delMenuItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
                        if (CommonUITool.openConfirmBox(Messages.msgSqlmapRemove)) {
                            removeParameterByCurrentSelected();
                        }
                    }
                });
    }

    private void createSqlView(Composite parent) {
        sqlView = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
        sqlView.setLayoutData(new GridData(GridData.FILL_BOTH));
        sqlView.setEditable(false);

        createButtons(parent);
    }

    private void createButtons(Composite parent) {
        Composite wrapper = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.numColumns = 3;
        wrapper.setLayout(layout);
        wrapper.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Button pasteBtn = new Button(wrapper, SWT.BORDER);
        pasteBtn.setText(Messages.btnSqlmapPaste);
        pasteBtn.addSelectionListener(
                new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        pasteSqlToQueryEditor(sqlView.getText());
                    }
                });
    }

    private void pasteSqlToQueryEditor(String sql) {
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

        QueryEditorPart queryEditor = (QueryEditorPart) editor;
        if (!StringUtil.isEmpty(queryEditor.getCurrentQuery())) {
            queryEditor.addEditorTab();
        }
        queryEditor.setQuery(sql, false, false, false);
        queryEditor.setFocus();
    }

    private void openModifyDialog(String queryId, String name, String value, String type) {
        final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

        SqlmapInputValueDialog dialog = new SqlmapInputValueDialog(shell, name, value, type);
        if (dialog.open() == IDialogConstants.OK_ID) {
            String parameterName = dialog.getName();
            String parameterValue = dialog.getValue();
            String parameterType = dialog.getType();
            SqlmapPersistUtil.getInstance()
                    .addOrModifyBindParameter(
                            queryId, parameterName, parameterValue, parameterType);
            refreshView();
        }
    }

    public void refreshView() {
        String queryId = getSelectedQueryId();
        List<String> condListForQuery = new ArrayList<String>();

        List<QueryCondition> condList = SqlmapPersistUtil.getInstance().getConditions(queryId);
        List<Map<String, String>> condData = new ArrayList<Map<String, String>>();
        for (QueryCondition cond : condList) {
            Map<String, String> item = new HashMap<String, String>();
            String condNameValue = cond.getConditionKey() + ":" + cond.getConditionBody();
            item.put("1", condNameValue);
            condData.add(item);

            if (SqlmapPersistUtil.getInstance().isUsedCondition(queryId, condNameValue)) {
                condListForQuery.add(condNameValue);
            }
        }
        condView.setInput(condData);

        List<Map<String, String>> paramData = new ArrayList<Map<String, String>>();
        Map<String, BindParameter> bindParams =
                SqlmapPersistUtil.getInstance().getBindParameters(queryId);
        for (Map.Entry<String, BindParameter> entry : bindParams.entrySet()) {
            Map<String, String> item = new HashMap<String, String>();
            item.put("0", entry.getValue().getName());
            item.put("1", entry.getValue().getValue());
            item.put("2", entry.getValue().getType().name());
            paramData.add(item);
        }
        paramView.setInput(paramData);

        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }

        IEditorPart editorPart = window.getActivePage().getActiveEditor();
        if (!(editorPart instanceof QueryEditorPart)) {
            return;
        }

        QueryEditorPart queryEditorPart = (QueryEditorPart) editorPart;

        MapperFile mapperFile = null;
        try {
            mapperFile = new MapperParserImpl().parse(queryEditorPart.getAllQueries());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return;
        }

        String generatedQuery = mapperFile.generateQuery(queryId, condListForQuery);

        List<String> bindParamList =
                QueryUtil.extractBindParameters(mapperFile.generateRawQuery(queryId));
        for (String bindParam : bindParamList) {
            String paramRawName = bindParam;
            String paramName = QueryUtil.extractBindParameterName(paramRawName);
            BindParameter bindValue = bindParams.get(paramName);
            if (bindValue != null) {
                String value =
                        bindValue.getType() == BindParameterType.STRING
                                ? "'" + bindValue.getValue() + "'"
                                : bindValue.getValue();
                generatedQuery = generatedQuery.replace(paramRawName, value);
            }
        }

        sqlView.setText(formator.format(generatedQuery));
    }

    public void updateQueryIdList(List<String> queryIdList, String defaultQueryId) {
        selector.removeAll();
        if (queryIdList == null) {
            return;
        }
        selector.add(defaultQueryId);
        selector.select(0);
    }

    /**
     * Return current SqlmapNavigatorView instance.
     *
     * @return
     */
    public static SqlmapNavigatorView getInstance() {
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
            return viewPart instanceof SqlmapNavigatorView ? (SqlmapNavigatorView) viewPart : null;
        }

        return null;
    }

    public void changeUseCondition(String condKey, boolean use) {
        String queryId = getSelectedQueryId();
        SqlmapPersistUtil.getInstance().changeUsedCondition(queryId, condKey, use);
    }

    public boolean isUseCondition(String condKey) {
        String queryId = getSelectedQueryId();
        return SqlmapPersistUtil.getInstance().isUsedCondition(queryId, condKey);
    }

    public String getSelectedQueryId() {
        return selector.getItem(selector.getSelectionIndex());
    }

    private void addParameter() {
        String queryId = getSelectedQueryId();
        openModifyDialog(queryId, null, null, BindParameterType.STRING.name());
    }

    @SuppressWarnings("unchecked")
    private void modifyParameterByCurrentSelected() {
        String queryId = getSelectedQueryId();

        int selectedIndex = paramView.getTable().getSelectionIndex();
        TableItem tableItem = paramView.getTable().getItem(selectedIndex);
        Map<String, String> data = (Map<String, String>) tableItem.getData();
        String parameterName = data.get("0");
        String parameterValue = data.get("1");
        String parameterType = data.get("2");

        openModifyDialog(queryId, parameterName, parameterValue, parameterType);
    }

    @SuppressWarnings("unchecked")
    private void removeParameterByCurrentSelected() {
        String queryId = getSelectedQueryId();

        int selectedIndex = paramView.getTable().getSelectionIndex();
        TableItem tableItem = paramView.getTable().getItem(selectedIndex);
        Map<String, String> data = (Map<String, String>) tableItem.getData();
        String parameterName = data.get("0");
        SqlmapPersistUtil.getInstance().removeBindParameter(queryId, parameterName);

        refreshView();
    }

    @SuppressWarnings("unchecked")
    private void toggleUsedBySelectedCondition() {
        TableItem[] tableItems = condView.getTable().getSelection();
        if (tableItems == null || tableItems.length == 0) {
            return;
        }

        String queryId = getSelectedQueryId();
        for (TableItem tableItem : tableItems) {
            Map<String, String> data = (Map<String, String>) tableItem.getData();
            String condition = data.get("1");
            SqlmapPersistUtil.getInstance().toggleUsedCondition(queryId, condition);
        }

        refreshView();
    }

    private void modifyParam(Map<String, String> data) {
        final String name = data.get("0");
        final String value = data.get("1");
        final String type = data.get("2");
        final String queryId = getSelectedQueryId();

        openModifyDialog(queryId, name, value, type);
    }
}
