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
package com.cubrid.common.ui.cubrid.table.dialog;

import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.query.control.DateTimeComponent;
import com.cubrid.common.ui.query.control.SqlParser;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.query.editor.QueryUnit;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.progress.CommonTaskJobExec;
import com.cubrid.common.ui.spi.progress.ITaskExecutorInterceptor;
import com.cubrid.common.ui.spi.progress.JobFamily;
import com.cubrid.common.ui.spi.progress.TaskJobExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.TableUtil;
import com.cubrid.cubridmanager.core.cubrid.table.model.DBAttrTypeFormatter;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * new prepared date dialog can deal with multiple date in one execution
 *
 * @author fulei
 * @version 8.4.1 - 2012-04-13 created by fulei
 */
public class PstmtSQLDialog extends CMTitleAreaDialog implements ITaskExecutorInterceptor {
    protected final CubridDatabase database;
    protected String tableName;
    protected boolean isInsert = false;
    protected StyledText sqlTxt = null;
    protected Button analyzeButton;
    protected TableViewer parameterTable = null;
    protected Table parameterTypeTable = null;
    protected TableEditor tableEditor = null;
    protected SchemaInfo schemaInfo;
    private Group parameterGroup = null;
    protected boolean isChanging = false;
    private int columnCount = 0;
    protected QueryUnit editorInput = new QueryUnit();
    private ArrayList<ParamValueObject> valueList = new ArrayList<ParamValueObject>();
    private long beginTimestamp;
    private final String[] HOTTYPE = {
        "DATE",
        "DATETIME",
        "TIMESTAMP",
        "DOUBLE",
        "FLOAT",
        "SMALLINT",
        "BIGINT",
        "INTEGER",
        "CHAR",
        "VARCHAR"
    };
    private final String[] IGNOREYPE = {
        "NCHAR VARYING",
        "MONETARY",
        "BIT VARYING",
        "MULTISET",
        "SEQUENCE",
        "SET",
        "TIME",
        "BIT",
        "NCHAR",
        "STRING",
        "NUMERIC"
    };
    private final String EXCELCOLUMNSEPRATOR = "	";
    private final String EXCELDATASEPRATOR = System.getProperty("line.separator");
    private LinkedHashMap<String, String> columnTypeMap = new LinkedHashMap<String, String>();
    private String charSet = null;
    private Point clickPoint;

    public PstmtSQLDialog(Shell parentShell, CubridDatabase database) {
        this(parentShell, database, null, false);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);

        Composite composite = new Composite(parentComp, SWT.NONE);
        {
            composite.setLayoutData(new GridData(GridData.FILL_BOTH));
            GridLayout layout = new GridLayout();
            layout.numColumns = 1;
            layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
            layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
            layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
            layout.horizontalSpacing =
                    convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
            composite.setLayout(layout);
        }

        SashForm sashForm = new SashForm(composite, SWT.NONE);
        {
            sashForm.setOrientation(SWT.VERTICAL);
            GridData gridData = new GridData(GridData.FILL_BOTH);
            gridData.widthHint = 600;
            gridData.heightHint = 600;
            sashForm.setLayoutData(gridData);
            GridLayout layout = new GridLayout();
            layout.horizontalSpacing =
                    convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
            sashForm.setLayout(layout);

            createSqlTextComposite(sashForm);
            createBottomComposite(sashForm);

            sashForm.setWeights(new int[] {50, 50});
        }
        if (isInsert) {
            initial();
        }

        return parentComp;
    }

    private void createSqlTextComposite(Composite parent) {
        Group sqlGroup = new Group(parent, SWT.NONE);
        {
            sqlGroup.setText(Messages.grpSql);
            GridData gridData = new GridData(GridData.FILL_BOTH);
            gridData.heightHint = 160;
            sqlGroup.setLayoutData(gridData);
            GridLayout layout = new GridLayout();
            sqlGroup.setLayout(layout);
        }

        sqlTxt = new StyledText(sqlGroup, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        {
            sqlTxt.setLayoutData(new GridData(GridData.FILL_BOTH));
            sqlTxt.addModifyListener(
                    new ModifyListener() {
                        public void modifyText(ModifyEvent event) {
                            if (validSql()) {
                                analyzeButton.setEnabled(true);
                            } else {
                                analyzeButton.setEnabled(false);
                            }
                        }
                    });
            CommonUITool.registerContextMenu(sqlTxt, true);
        }

        Composite composite = new Composite(sqlGroup, SWT.NONE);
        {
            RowLayout rowLayout = new RowLayout();
            rowLayout.spacing = 5;
            composite.setLayout(rowLayout);
            GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
            gridData.horizontalAlignment = GridData.END;
            composite.setLayoutData(gridData);
        }

        Button clearButton = new Button(composite, SWT.NONE);
        {
            clearButton.setText(Messages.btnClear);
            clearButton.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            sqlTxt.setText("");
                            removeColumn();
                            validate();
                        }
                    });
        }

        analyzeButton = new Button(composite, SWT.NONE);
        {
            analyzeButton.setText(Messages.btnAnalyze);
            analyzeButton.setEnabled(false);
            analyzeButton.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            analyzeSql();
                        }
                    });
        }
    }

    public PstmtSQLDialog(
            Shell parentShell, CubridDatabase database, String tableName, boolean isInsert) {
        super(parentShell);
        this.tableName = tableName;
        this.database = database;
        this.schemaInfo =
                this.database == null || this.tableName == null
                        ? null
                        : this.database.getDatabaseInfo().getSchemaInfo(tableName);
        this.isInsert = isInsert;
    }

    protected void initial() {
        String sql = "";
        if (tableName != null && isInsert) {
            sql = createInsertPstmtSQL();
        }
        sqlTxt.setText(sql);
        initialSQL();
        int n = schemaInfo == null ? 0 : schemaInfo.getAttributes().size();

        TableItem item = parameterTypeTable.getItem(0);
        for (int i = 0; i < n; i++) {
            DBAttribute da = (DBAttribute) schemaInfo.getAttributes().get(i);
            item.setText(i + 1, DataType.getShownType(da.getType()));
        }
    }

    /**
     * Create inserted prepared statement SQL
     *
     * @return String
     */
    protected String createInsertPstmtSQL() { // FIXME move this logic to core module
        StringBuffer columns = new StringBuffer("");
        StringBuffer values = new StringBuffer("");

        int n = schemaInfo == null ? 0 : schemaInfo.getAttributes().size();
        for (int i = 0; i < n; i++) {
            DBAttribute da = (DBAttribute) schemaInfo.getAttributes().get(i);
            if (values.length() > 0) {
                columns.append(", ");
                values.append(", ");
            }
            columns.append(QuerySyntax.escapeKeyword(da.getName()));
            values.append("?");
        }

        StringBuffer sql = new StringBuffer("");
        if (columns.length() > 0) {
            sql.append("INSERT INTO ");
            sql.append(QuerySyntax.escapeKeyword(tableName));
            sql.append(" (");
            sql.append(columns);
            sql.append(") VALUES (");
            sql.append(values);
            sql.append(");");
        }
        return sql.toString();
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
        getShell().setText(Messages.titlePstmtDataDialog);
        return parent;
    }

    /**
     * Create the parameter table
     *
     * @param parent Composite
     */
    protected void createParameterTable(Composite parent) {
        parameterGroup = new Group(parent, SWT.NONE);
        {
            parameterGroup.setText(Messages.grpParameters);
            GridData gridData = new GridData(GridData.FILL_BOTH);
            parameterGroup.setLayoutData(gridData);
            parameterGroup.setLayout(new GridLayout());
        }
        createEmptyTable(parameterGroup);
    }

    /**
     * Set the executed SQL
     *
     * @param sql The String
     */
    public void setSql(String sql) { // FIXME move this logic to core module
        sqlTxt.setText(sql);
        analyzeSql();
    }

    /**
     * Create the empty parameter table
     *
     * @param parent Composite
     * @param isMulti boolean
     */
    protected void createEmptyTable(Composite parent) {
        Group typeGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
        GridData groupGd = new GridData(GridData.FILL_HORIZONTAL);
        groupGd.heightHint = 70;
        typeGroup.setLayoutData(groupGd);

        typeGroup.setLayout(new GridLayout());
        typeGroup.setText(Messages.colParaType);
        parameterTypeTable = new Table(typeGroup, SWT.BORDER | SWT.H_SCROLL);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 45;
        parameterTypeTable.setLayoutData(gd);
        parameterTypeTable.setHeaderVisible(true);
        parameterTypeTable.setLinesVisible(false);
        parameterTypeTable.setDragDetect(false);

        TableColumn columnNO = new TableColumn(parameterTypeTable, SWT.CENTER);
        columnNO.setWidth(40);
        columnNO.setText("");

        tableEditor = new TableEditor(parameterTypeTable);
        tableEditor.horizontalAlignment = SWT.LEFT;
        tableEditor.grabHorizontal = true;

        parameterTypeTable.addListener(
                SWT.MouseUp,
                new Listener() {
                    public void handleEvent(Event event) {
                        if (event.button != 1) {
                            return;
                        }
                        if (isChanging) {
                            return;
                        }
                        Point pt = new Point(event.x, event.y);
                        final TableItem item = parameterTypeTable.getItem(0);
                        for (int i = 1; i < parameterTypeTable.getColumnCount(); i++) {
                            Rectangle rect = item.getBounds(i);
                            if (rect.contains(pt)) {
                                handleType(item, i);
                            }
                        }
                    }
                });

        Group parameterGroup = new Group(parent, SWT.SHADOW_IN);
        parameterGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
        parameterGroup.setLayout(new GridLayout(2, false));
        parameterGroup.setText(Messages.colParaValue);

        ToolBar toolBar = new ToolBar(parameterGroup, SWT.FLAT);

        ToolItem addRecordItem = new ToolItem(toolBar, SWT.PUSH);

        addRecordItem.setImage(CommonUIPlugin.getImage("icons/action/table_record_insert.png"));
        addRecordItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        addData();
                    }
                });
        ToolItem delRecordItem = new ToolItem(toolBar, SWT.PUSH);

        delRecordItem.setImage(CommonUIPlugin.getImage("icons/action/table_record_delete.png"));
        delRecordItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (parameterTable.getTable().getSelectionIndices().length == 0) {
                            setErrorMessage(Messages.errDeleteMsg);
                            return;
                        }

                        List<Integer> deleteIndex = new ArrayList<Integer>();

                        for (int i = 0;
                                i < parameterTable.getTable().getSelectionIndices().length;
                                i++) {
                            deleteIndex.add(parameterTable.getTable().getSelectionIndices()[i]);
                        }

                        int lastSelectedIndex = 0;

                        for (int i = 0; i < deleteIndex.size(); i++) {
                            int seletectIndex = deleteIndex.get(i);
                            int newIndex = seletectIndex - i;
                            valueList.remove(newIndex);
                            lastSelectedIndex = newIndex;
                        }

                        // reset the index in data
                        for (int i = 0; i < valueList.size(); i++) {
                            ParamValueObject paramValueObject = valueList.get(i);
                            paramValueObject.getValue().set(0, String.valueOf(i + 1));
                        }

                        parameterTable.setInput(valueList);
                        parameterTable.refresh();
                        if (parameterTable.getTable().getItemCount() > 0) {
                            parameterTable
                                    .getTable()
                                    .setSelection(
                                            lastSelectedIndex < 1 ? 0 : lastSelectedIndex - 1);
                            parameterTable.getTable().setFocus();
                        }
                        validate();
                    }
                });
        parameterTable =
                new TableViewer(
                        parameterGroup,
                        SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);

        // press the tab key, it is moved next input area
        TableViewerFocusCellManager focusCellManager =
                new TableViewerFocusCellManager(
                        parameterTable, new FocusCellOwnerDrawHighlighter(parameterTable));
        ColumnViewerEditorActivationStrategy actSupport =
                new ColumnViewerEditorActivationStrategy(parameterTable) {
                    protected boolean isEditorActivationEvent(
                            ColumnViewerEditorActivationEvent event) {
                        return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
                                || (event.eventType
                                                == ColumnViewerEditorActivationEvent
                                                        .MOUSE_CLICK_SELECTION
                                        && ((MouseEvent) event.sourceEvent).button == 1)
                                || (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED
                                        && event.keyCode == SWT.CR);
                        //				|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
                    }
                };
        TableViewerEditor.create(
                parameterTable,
                focusCellManager,
                actSupport,
                ColumnViewerEditor.TABBING_HORIZONTAL
                        | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
                        | ColumnViewerEditor.TABBING_VERTICAL
                        | ColumnViewerEditor.KEYBOARD_ACTIVATION);

        //		new Table(parameterGroup, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        parameterTable.getTable().setLayoutData(gridData);
        parameterTable.getTable().setHeaderVisible(true);
        parameterTable.getTable().setLinesVisible(true);
        parameterTable.setUseHashlookup(true);
        final TableViewerColumn columnNO2 = new TableViewerColumn(parameterTable, SWT.CENTER);
        columnNO2.getColumn().setWidth(40);
        columnNO2.getColumn().setText("");

        parameterTable.setContentProvider(new ParamValueContentProvider());
        parameterTable.setLabelProvider(new ParamValueLabelProvider(parameterTable.getTable()));

        parameterTable
                .getTable()
                .addKeyListener(
                        new KeyAdapter() {
                            public void keyPressed(KeyEvent event) {
                                if ((event.stateMask & SWT.CTRL) != 0 && event.keyCode == 'c') {
                                    final Clipboard cb = new Clipboard(getShell().getDisplay());
                                    StringBuffer clipboardDataString = new StringBuffer();
                                    List<Integer> replaceIndex = new ArrayList<Integer>();
                                    for (int i = 0;
                                            i
                                                    < parameterTable
                                                            .getTable()
                                                            .getSelectionIndices()
                                                            .length;
                                            i++) {
                                        replaceIndex.add(
                                                parameterTable.getTable().getSelectionIndices()[i]);
                                    }
                                    for (int i = 0; i < replaceIndex.size(); i++) {
                                        if (i != 0) {
                                            clipboardDataString.append(EXCELDATASEPRATOR);
                                        }
                                        ParamValueObject paramValueObject =
                                                valueList.get(replaceIndex.get(i));
                                        for (int j = 1;
                                                j < parameterTypeTable.getColumnCount();
                                                j++) {
                                            if (j != 1) {
                                                clipboardDataString.append(EXCELCOLUMNSEPRATOR);
                                            }
                                            String value = paramValueObject.getValue().get(j);
                                            clipboardDataString.append(value);
                                        }
                                    }

                                    TextTransfer textTransfer = TextTransfer.getInstance();
                                    Transfer[] transfers = new Transfer[] {textTransfer};
                                    Object[] data = new Object[] {clipboardDataString.toString()};
                                    cb.setContents(data, transfers);

                                    cb.dispose();
                                } else if ((event.stateMask & SWT.CTRL) != 0
                                        && event.keyCode == 'v') {
                                    final Clipboard cb = new Clipboard(getShell().getDisplay());

                                    //					boolean supportFlag = false;
                                    //					TransferData[] transferDatas = cb.getAvailableTypes();
                                    //					for(int i=0; i<transferDatas.length; i++) {
                                    //						// Checks whether RTF format is available.
                                    //
                                    //	if(RTFTransfer.getInstance().isSupportedType(transferDatas[i])) {
                                    //							supportFlag = true;
                                    //							break;
                                    //						}
                                    //					}
                                    //					if (!supportFlag) {
                                    //						setErrorMessage(Messages.pstmtSQLUnsupportPasteType);
                                    //						return;
                                    //					}
                                    String plainText =
                                            (String) cb.getContents(TextTransfer.getInstance());
                                    List<ParamValueObject> list =
                                            generateParamValueObjectListFromClipboardString(
                                                    plainText);
                                    if (list.size() == 0
                                            || list.get(0).getValue().size()
                                                    != parameterTypeTable.getColumnCount()) {
                                        setErrorMessage(Messages.pstmtSQLUnsupportPasteType);
                                        return;
                                    }
                                    //					String rtfText =
                                    // (String)cb.getContents(RTFTransfer.getInstance());
                                    int startIndex = parameterTable.getTable().getSelectionIndex();
                                    // if select one line , replay the data from the start line,
                                    // if the copy line bigger than the value list, add new
                                    // ParamValueObject to the end
                                    if (parameterTable.getTable().getSelectionCount() <= 1) {
                                        if (startIndex < 0 || startIndex > valueList.size()) {
                                            for (ParamValueObject copyParamValueObject : list) {
                                                valueList.add(copyParamValueObject);
                                            }
                                        } else {
                                            for (ParamValueObject copyParamValueObject : list) {
                                                if (startIndex > valueList.size() - 1) {
                                                    valueList.add(copyParamValueObject);
                                                } else {
                                                    ParamValueObject paramValueObject =
                                                            valueList.get(startIndex);
                                                    List<String> oldValue =
                                                            paramValueObject.getValue();
                                                    for (int i = 1; i < oldValue.size(); i++) {
                                                        List<String> newValue =
                                                                copyParamValueObject.getValue();
                                                        if (i > newValue.size() - 1) {
                                                            break;
                                                        }
                                                        oldValue.set(i, newValue.get(i));
                                                    }
                                                }
                                                startIndex++;
                                            }
                                        }
                                    } else {
                                        // replay the select line
                                        List<Integer> replaceIndex = new ArrayList<Integer>();
                                        for (int i = 0;
                                                i
                                                        < parameterTable
                                                                .getTable()
                                                                .getSelectionIndices()
                                                                .length;
                                                i++) {
                                            replaceIndex.add(
                                                    parameterTable.getTable()
                                                            .getSelectionIndices()[i]);
                                        }

                                        for (int i = 0; i < replaceIndex.size(); i++) {
                                            ParamValueObject paramValueObject =
                                                    valueList.get(replaceIndex.get(i));
                                            List<String> oldValue = paramValueObject.getValue();
                                            if (i > list.size()) {
                                                break;
                                            }
                                            List<String> newValue = list.get(i).getValue();
                                            for (int j = 1; j < oldValue.size(); j++) {
                                                if (j > newValue.size()) {
                                                    break;
                                                }
                                                oldValue.set(j, newValue.get(j));
                                            }
                                        }
                                    }
                                    cb.dispose();
                                    refreshValueListIndex();
                                    parameterTable.refresh();
                                    validate();
                                }
                            }
                        });

        // use to mark click point, the right click menu use this point
        parameterTable
                .getTable()
                .addListener(
                        SWT.MouseDown,
                        new Listener() {
                            public void handleEvent(Event event) {
                                clickPoint = new Point(event.x, event.y);
                            }
                        });
        registerContextMenu();
        parameterTable.setInput(valueList);
    }

    private void registerContextMenu() {
        parameterTable
                .getTable()
                .addFocusListener(
                        new FocusAdapter() {
                            @Override
                            public void focusGained(FocusEvent event) {
                                ActionManager.getInstance()
                                        .changeFocusProvider(parameterTable.getTable());
                            }
                        });

        MenuManager menuManager = new MenuManager();
        menuManager.setRemoveAllWhenShown(true);

        Menu contextMenu = menuManager.createContextMenu(parameterTable.getTable());
        parameterTable.getTable().setMenu(contextMenu);

        Menu menu = new Menu(getShell(), SWT.POP_UP);
        final MenuItem itemShowMuchValue = new MenuItem(menu, SWT.PUSH);
        itemShowMuchValue.setText(Messages.pstmtSQLMuchItem);
        itemShowMuchValue.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        // seems like MenuEvent can't get the mouse click Point
                        // so use the point which table MouseDown event marked
                        Point pt = clickPoint;
                        int selectIndex = parameterTable.getTable().getSelectionIndex();
                        final TableItem item = parameterTable.getTable().getItem(selectIndex);
                        if (item == null) {
                            return;
                        }
                        for (int i = 1; i < parameterTable.getTable().getColumnCount(); i++) {
                            Rectangle rect = item.getBounds(i);
                            if (rect.contains(pt)) {
                                String type = parameterTypeTable.getItem(0).getText(i);
                                SetPstmtValueDialog dialog =
                                        new SetPstmtValueDialog(
                                                getShell(), item, database, i, type);
                                if (IDialogConstants.OK_ID == dialog.open()) {
                                    charSet =
                                            (String) item.getData(SetPstmtValueDialog.FILE_CHARSET);
                                    valueList.get(selectIndex).getValue().set(i, item.getText(i));
                                    packTable();

                                    addTableItemToLast(selectIndex, item);
                                }
                                validate();
                            }
                        }
                    }
                });

        menu.addMenuListener(
                new MenuAdapter() {
                    public void menuShown(MenuEvent event) {
                        // seems like MenuEvent can't get the mouse click Point
                        // so use the point which table MouseDown event marked
                        Point pt = clickPoint;
                        int selectIndex = parameterTable.getTable().getSelectionIndex();
                        if (selectIndex < 0) {
                            itemShowMuchValue.setEnabled(false);
                            return;
                        }
                        final TableItem item = parameterTable.getTable().getItem(selectIndex);
                        if (item == null) {
                            itemShowMuchValue.setEnabled(false);
                            return;
                        }
                        for (int i = 1; i < parameterTable.getTable().getColumnCount(); i++) {
                            Rectangle rect = item.getBounds(i);
                            if (rect.contains(pt)) {
                                String type = parameterTypeTable.getItem(0).getText(i);
                                // if type is nut much value type ,set the menu disable
                                if (DBAttrTypeFormatter.isMuchValueType(type)) {
                                    itemShowMuchValue.setEnabled(true);
                                } else {
                                    itemShowMuchValue.setEnabled(false);
                                }
                            }
                        }
                    }
                });
        parameterTable.getTable().setMenu(menu);
    }

    public void addData() {
        // add one data to parameterTable
        ParamValueObject paramValueObject = new ParamValueObject();
        // index
        paramValueObject.getValue().add("1");
        for (int i = 0; i < columnCount; i++) {
            paramValueObject.getValue().add("");
        }

        valueList.add(paramValueObject);
        paramValueObject.getValue().set(0, String.valueOf(valueList.size()));
        parameterTable.setInput(valueList);
        parameterTable.refresh();
    }

    class ParamValueContentProvider implements IStructuredContentProvider {
        @SuppressWarnings("unchecked")
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof List) {
                List<ParamValueObject> list = (List<ParamValueObject>) inputElement;
                ParamValueObject[] nodeArr = new ParamValueObject[list.size()];
                return list.toArray(nodeArr);
            }

            return new Object[] {};
        }

        public void dispose() {}

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
    }

    public class ParamValueObject {
        private ArrayList<String> value = new ArrayList<String>();

        public ArrayList<String> getValue() {
            return value;
        }

        public void setValue(ArrayList<String> value) {
            this.value = value;
        }

        // check whether all parameter ,first value is index ,so start at 1
        public boolean isEmpty() {
            for (int i = 1; i < value.size(); i++) {
                if (!value.get(i).equals("")) {
                    return false;
                }
            }
            return true;
        }
    }

    class ParamValueLabelProvider extends LabelProvider implements ITableLabelProvider {
        Table parameterTable = null;

        public ParamValueLabelProvider(Table parameterTable) {
            super();
            this.parameterTable = parameterTable;
        }

        public final Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            if (element instanceof ParamValueObject) {
                ParamValueObject paramValueObject = (ParamValueObject) element;
                if (columnIndex >= paramValueObject.getValue().size()) {
                    return "";
                } else {
                    return paramValueObject.getValue().get(columnIndex);
                }
            }
            return null;
        }
    }

    protected void removeColumn() {
        if (parameterTypeTable.getItemCount() > 0) {
            parameterTypeTable.remove(0);
        }
        int oldCount = parameterTypeTable.getColumnCount();
        for (int i = 0; i < oldCount; i++) {
            parameterTypeTable.getColumn(0).dispose();
            parameterTable.getTable().getColumn(0).dispose();
        }

        TableColumn columnNO = new TableColumn(parameterTypeTable, SWT.NONE);
        columnNO.setWidth(40);
        columnNO.setText("");

        final TableViewerColumn columnNO2 = new TableViewerColumn(parameterTable, SWT.CENTER);
        columnNO2.getColumn().setWidth(40);
        columnNO2.getColumn().setText("");
        /*For bug TOOLS-3115*/
        parameterTable.setLabelProvider(new ParamValueLabelProvider(parameterTable.getTable()));

        valueList.clear();
        parameterTable.refresh();
    }

    /**
     * Create the empty parameter table
     *
     * @param parent Composite
     * @param isMulti boolean
     */
    protected void createColumn(int columnCount) {
        TableItem item = new TableItem(parameterTypeTable, SWT.NONE);
        item.setText(0, Messages.msgPstmtType);
        for (int i = 0; i < columnCount; i++) {
            TableColumn typeColumn = new TableColumn(parameterTypeTable, SWT.NONE);
            typeColumn.setWidth(90);
            typeColumn.setText(String.valueOf(i + 1));

            final TableViewerColumn paraColumn = new TableViewerColumn(parameterTable, SWT.LEFT);
            paraColumn.getColumn().setWidth(90);
            paraColumn.getColumn().setText(String.valueOf(i + 1));
            paraColumn.setEditingSupport(new ParamValueEditingSupport(parameterTable, i + 1));
        }

        parameterTable.setLabelProvider(new ParamValueLabelProvider(parameterTable.getTable()));
        // add one data to parameterTable
        ParamValueObject paramValueObject = new ParamValueObject();
        // index
        paramValueObject.getValue().add("1");
        for (int i = 0; i < columnCount; i++) {
            paramValueObject.getValue().add("");
        }
        valueList.add(paramValueObject);
        parameterTable.setInput(valueList);

        parameterTable.refresh();
    }

    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.btnExecute, false);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.closeButtonName, false);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
    }

    public void setEditorInput(QueryUnit editorInput) {
        this.editorInput = editorInput;
    }

    protected boolean validSql() { // FIXME move this logic to core module
        String sql = sqlTxt.getText();
        if (sql.trim().length() == 0) {
            return false;
        }
        if (sql.indexOf("?") == -1) {
            return false;
        }
        return true;
    }

    protected void analyzeSql() { // FIXME move this logic to core module
        if (!validSql()) {
            return;
        }
        String sql = sqlTxt.getText();
        columnCount = SqlParser.getStrCount(sql, "?");
        analyzeTable(sql);
        removeColumn();
        createColumn(columnCount);
        packTable();
        validate();
    }

    /**
     * Analyse table name and the table's column name and type
     *
     * @param sql
     */
    public void analyzeTable(String sql) { // FIXME move this logic to core module
        if (sql == null) {
            return;
        }

        String upperSQL = sql.trim().toUpperCase();

        String tableNameStartString = "";
        if (upperSQL.startsWith("SELECT") && upperSQL.indexOf("FROM") > -1) {
            tableNameStartString =
                    upperSQL.substring(upperSQL.indexOf("FROM") + "FROM".length()).trim();
        } else if (upperSQL.startsWith("UPDATE") && upperSQL.indexOf("SET") > -1) {
            tableNameStartString =
                    upperSQL.substring(upperSQL.indexOf("UPDATE") + "UPDATE".length()).trim();
        } else if (upperSQL.startsWith("DELETE") && upperSQL.indexOf("FROM") > -1) {
            tableNameStartString =
                    upperSQL.substring(upperSQL.indexOf("FROM") + "FROM".length()).trim();
        } else if (upperSQL.startsWith("INSERT INTO")) {
            tableNameStartString =
                    upperSQL.substring(upperSQL.indexOf("INSERT INTO") + "INSERT INTO".length())
                            .trim();
        }

        // can't analyse it's table name
        if (tableNameStartString.length() == 0) {
            this.tableName = "";
            columnTypeMap.clear();
        }

        int index = 0;
        char currChar = '\0';
        String tempTableName = "";
        StringBuilder sb = new StringBuilder();
        while (index < tableNameStartString.length()
                && !isKeywordSeparator(currChar = tableNameStartString.charAt(index))) {
            sb.append(Character.toLowerCase(currChar));
            index++;
        }
        tempTableName = sb.toString();

        // if the table name is embraced in "" ,cut it
        if (tempTableName.startsWith("\"")) {
            tempTableName = tempTableName.substring(1);
        }

        if (tempTableName.endsWith("\"")) {
            tempTableName = tempTableName.substring(0, tempTableName.length() - 1);
        }

        this.schemaInfo =
                this.database == null
                        ? null
                        : this.database.getDatabaseInfo().getSchemaInfo(tableName);
        int n = schemaInfo == null ? 0 : schemaInfo.getAttributes().size();
        if (n > 0) {
            tableName = tempTableName;
        } else {
            this.tableName = "";
            columnTypeMap.clear();
        }

        for (int i = 0; i < n; i++) {
            DBAttribute da = (DBAttribute) schemaInfo.getAttributes().get(i);
            columnTypeMap.put(da.getName(), da.getType());
        }
    }

    /**
     * Return whether the previous character is keyword separator
     *
     * @param ch char
     * @return boolean
     */
    private boolean isKeywordSeparator(char ch) { // FIXME move this logic to core module
        boolean isSeparator =
                (Character.isWhitespace(ch)
                        || ch == ICharacterScanner.EOF
                        || ch == ' '
                        || ch == '('
                        || ch == '\r'
                        || ch == '\n'
                        || ch == '\t'
                        || ch == ';'
                        || ch == ','
                        || ch == ')');
        return isSeparator;
    }

    protected void initialSQL() { // FIXME move this logic to core module
        String sql = sqlTxt.getText();
        columnCount = SqlParser.getStrCount(sql, "?");
        removeColumn();
        createColumn(columnCount);
        analyzeTable(sql);
        packTable();
    }

    protected void packTable() { // FIXME can be modulation by utility class
        for (int i = 0; i < parameterTable.getTable().getColumnCount(); i++) {
            if (parameterTable.getTable().getColumns()[i].getWidth() > 200) {
                parameterTable.getTable().getColumns()[i].setWidth(200);
            }
        }
    }

    /**
     * Handle type
     *
     * @param item TableItem
     */
    protected void handleType(final TableItem item, final int editColumn) {
        String[][] typeMapping = DataType.getTypeMapping(database.getDatabaseInfo(), true, true);
        LinkedList<String> typeList = new LinkedList<String>();
        for (int i = 0; i < typeMapping.length; i++) {
            String type = typeMapping[i][0];
            if (!type.startsWith(DataType.DATATYPE_OBJECT)) {
                typeList.add(type);
            }
        }
        typeList.addFirst("NUMERIC(38,12)"); // add default precision
        sortTypeList(typeList);
        addColumnToList(typeList);
        String[] items = typeList.toArray(new String[] {});

        final Combo typeCombo = new Combo(parameterTypeTable, SWT.BORDER | SWT.FULL_SELECTION);
        typeCombo.setItems(items);
        typeCombo.setVisibleItemCount(20);

        final String paraName = item.getText(0);
        typeCombo.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        validateType(paraName, typeCombo.getText(), editColumn);
                    }
                });

        typeCombo.addFocusListener(
                new FocusAdapter() {
                    public void focusLost(FocusEvent event) {
                        if (isChanging) {
                            return;
                        }
                        isChanging = true;
                        //				if (validateType(paraName, typeCombo.getText(), editColumn)) {
                        item.setText(editColumn, typeCombo.getText());
                        //				}
                        typeCombo.dispose();
                        isChanging = false;
                        validate();
                    }
                });

        // add listener for key pressed
        typeCombo.addTraverseListener(
                new TraverseListener() {
                    public void keyTraversed(TraverseEvent event) {
                        if (event.detail == SWT.TRAVERSE_RETURN) {
                            if (isChanging) {
                                return;
                            }
                            isChanging = true;
                            //					if (validateType(paraName, typeCombo.getText(), editColumn)) {
                            item.setText(editColumn, typeCombo.getText());
                            //					}
                            typeCombo.dispose();
                            isChanging = false;
                            validate();
                            event.doit = true;
                        } else if (event.detail == SWT.TRAVERSE_ESCAPE) {
                            if (isChanging) {
                                return;
                            }
                            isChanging = true;
                            typeCombo.dispose();
                            event.doit = false;
                            isChanging = false;
                        }
                    }
                });

        tableEditor.setEditor(typeCombo, item, editColumn);
        typeCombo.setText(item.getText(editColumn));
        typeCombo.setFocus();
    }

    /**
     * remove ignore date type then sort type list ,move the type used frequently to the top
     *
     * @param list
     */
    public void sortTypeList(LinkedList<String> list) {
        for (String ignoreType : IGNOREYPE) {
            if (list.contains(ignoreType)) {
                list.remove(ignoreType);
            }
        }
        for (String hotType : HOTTYPE) {
            if (list.contains(hotType)) {
                list.remove(hotType);
                list.addFirst(hotType);
            }
        }
    }

    /**
     * add table column to List
     *
     * @param list
     */
    public void addColumnToList(LinkedList<String> list) {
        for (String columnName : columnTypeMap.keySet()) {
            list.add(columnName);
        }
    }

    /**
     * Validate the type
     *
     * @param paraName The String
     * @param type The String
     * @return boolean
     */
    protected boolean validateType(String paraName, String type, final int editColumn) {
        setErrorMessage(null);
        if (type == null || type.trim().length() == 0) {
            setErrorMessage(Messages.bind(Messages.msgParaType, editColumn));
            return false;
        }
        // column
        if (columnTypeMap.get(type) != null) {
            return true;
        }
        //		if (!DBAttrTypeFormatter.validateAttributeType(type)) {
        //			setErrorMessage(Messages.bind(Messages.errInvalidType, editColumn));
        //			return false;
        //		}
        return true;
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

        getButton(IDialogConstants.OK_ID).setEnabled(true);
        return true;
    }

    public IStatus postTaskFinished(ITask task) {
        return Status.OK_STATUS;
    }

    public void completeAll() {
        long endTimestamp = System.currentTimeMillis();
        String spendTime = calcSpendTime(beginTimestamp, endTimestamp);
        CommonUITool.openInformationBox(
                getShell(),
                Messages.titleExecuteResult,
                Messages.bind(Messages.msgExecuteResult, spendTime));
    }

    /**
     * Calculate the spend time
     *
     * @param beginTimestamp long
     * @param endTimestamp long
     * @return String
     */
    public static String calcSpendTime(
            long beginTimestamp, long endTimestamp) { // FIXME move this logic to core module
        double elapsedTime = (endTimestamp - beginTimestamp) * 0.001;
        final NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(3);
        String elapsedTimeStr = nf.format(elapsedTime);
        if (elapsedTime < 0.001) {
            elapsedTimeStr = "0.000";
        }
        return elapsedTimeStr;
    }

    //	/**
    //	 * Validate data
    //	 *
    //	 * @param data The String
    //	 * @param item The TableItem
    //	 * @return boolean
    //	 */
    //	private boolean validate(String data, TableItem paramItem, int line, final int editColumn) {
    //		setErrorMessage(null);
    //		String paraType = paramItem.getText(editColumn);
    //		//get the column type if the type is column name
    //		if (columnTypeMap.get(paraType) != null) {
    //			paraType = columnTypeMap.get(paraType);
    //		}
    //		if (DBAttrTypeFormatter.isMuchValueType(paraType)
    //				&& DBAttrTypeFormatter.isFilePath(data)) {
    //			return true;
    //		}
    //
    //		if (data.length() > 0) {
    //			FormatDataResult formatDataResult = DBAttrTypeFormatter.format(
    //					DataType.getType(paraType),
    //					DataType.NULL_EXPORT_FORMAT.equals(data) ? null : data,
    //					false, database.getDatabaseInfo().getCharSet());
    //			if (!formatDataResult.isSuccess()) {
    //				setErrorMessage(Messages.bind(
    //						Messages.errParaTypeValueMapping,
    //						new String[]{String.valueOf(line),
    //								String.valueOf(editColumn),
    //								DataType.getType(paraType)}));
    //				return false;
    //			}
    //		} else if (line != valueList.size()){//last record can be empty
    //			setErrorMessage(Messages.bind(Messages.msgParaValue,
    //					new String[]{String.valueOf(editColumn),
    //							String.valueOf(line)}));
    //			return false;
    //		}
    //		return true;
    //	}

    public class ParamValueEditingSupport extends EditingSupport {
        private TableViewer parameterTableViewer;
        private TextCellEditor parameterTextCellEditor;
        private MyCellEditorValidator validator = null;
        private final int columnIndex;

        public ParamValueEditingSupport(TableViewer parameterTableViewer, int columnIndex) {
            super(parameterTableViewer);
            this.parameterTableViewer = parameterTableViewer;
            this.columnIndex = columnIndex;
        }

        class MyCellEditorValidator implements ICellEditorValidator {
            public String isValid(Object value) {
                // TODO: it is not needed, because the user want input data at first.
                //				String paramValue = (String) value;
                //				TableItem paramItem = parameterTypeTable.getItem(0);
                //
                //				if (paramItem.getText(columnIndex) == null ||
                // paramItem.getText(columnIndex).equals("")) {
                //					setErrorMessage(Messages.bind(Messages.msgParaType, columnIndex));
                //					return "error";
                //				}
                //				if (!validate(paramValue, parameterTypeTable.getItem(0),
                //						parameterTableViewer.getTable().getSelectionIndex() + 1, columnIndex)) {
                //					return "error";
                //				}
                return null;
            }
        }

        protected boolean canEdit(Object element) {
            if (columnIndex == 0) {
                return false;
            }
            return true;
        }

        protected CellEditor getCellEditor(Object element) {
            String type = parameterTypeTable.getItem(0).getText(columnIndex);
            int selectIndex = parameterTable.getTable().getSelectionIndex();
            TableItem tableItem = parameterTable.getTable().getItem(selectIndex);
            if (columnTypeMap.get(type) != null) {
                type = columnTypeMap.get(type);
            }
            // if date
            if (type.equalsIgnoreCase("DATE")
                    || type.equalsIgnoreCase("DATETIME")
                    || type.equalsIgnoreCase("TIMESTAMP")
                    || type.equalsIgnoreCase("TIME")) {
                Shell shell =
                        new Shell(
                                Display.getDefault().getActiveShell(),
                                SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
                shell.setText("");
                shell.setLayout(new GridLayout());
                shell.setLayoutData(new GridData(GridData.FILL_BOTH));

                DateTimeComponent dateTimeComponent = new DateTimeComponent(shell, SWT.BORDER);
                dateTimeComponent.setLayout(new GridLayout());
                dateTimeComponent.setLayoutData(new GridData(GridData.FILL_BOTH));
                Point dateTimeComponentSize = dateTimeComponent.componentSize();
                int dateTimeComponentWidth = dateTimeComponentSize.x;
                int dateTimeComponentHeight = dateTimeComponentSize.y;
                shell.setSize(dateTimeComponentWidth, dateTimeComponentHeight);

                // compute location
                Point p = Display.getDefault().getCursorLocation();
                Rectangle screenSize = Display.getDefault().getClientArea();
                if (p.x + dateTimeComponentWidth > screenSize.width) {
                    p.x = screenSize.width - dateTimeComponentWidth - 50;
                }
                if (p.y + dateTimeComponentHeight > screenSize.height) {
                    p.y = screenSize.height - dateTimeComponentHeight - 50;
                }

                shell.setLocation(p);
                shell.open();
                while (!shell.isDisposed()) {
                    if (!Display.getDefault().readAndDispatch()) Display.getDefault().sleep();
                }
                // if select a date
                if (dateTimeComponent.getReturnDateValue() != null) {
                    if (type.equalsIgnoreCase("DATE")) {
                        tableItem.setText(columnIndex, dateTimeComponent.getReturnDateValue());
                    } else if (type.equalsIgnoreCase("TIMESTAMP")) {
                        tableItem.setText(columnIndex, dateTimeComponent.getReturnTimestampValue());
                    } else if (type.equalsIgnoreCase("TIME")) {
                        tableItem.setText(columnIndex, dateTimeComponent.getReturnTimeValue());
                    } else {
                        tableItem.setText(columnIndex, dateTimeComponent.getReturnDateTimeValue());
                    }
                    valueList
                            .get(selectIndex)
                            .getValue()
                            .set(columnIndex, tableItem.getText(columnIndex));
                    addTableItemToLast(selectIndex, tableItem);
                }
                validate();
                return null;
            }
            // normal type use textCellEditor
            if (parameterTextCellEditor == null) {
                Composite table = (Composite) parameterTableViewer.getControl();
                parameterTextCellEditor = new TextCellEditor(table);
                validator = new MyCellEditorValidator();
                parameterTextCellEditor.setValidator(validator);
                parameterTextCellEditor.addListener(
                        new ICellEditorListener() {

                            public void applyEditorValue() {}

                            public void cancelEditor() {}

                            public void editorValueChanged(
                                    boolean oldValidState, boolean newValidState) {}
                        });
            }
            return parameterTextCellEditor;
        }

        protected Object getValue(Object element) {
            ParamValueObject paramValueObject = (ParamValueObject) element;
            return paramValueObject.getValue().get(columnIndex);
        }

        protected void setValue(Object element, Object value) {
            ParamValueObject paramValueObject = (ParamValueObject) element;
            paramValueObject.getValue().set(columnIndex, value == null ? "" : (String) value);
            // if this is the last data and all parameter has value ,auto add a value
            if (valueList.get(valueList.size() - 1) == paramValueObject) {
                boolean editFinish = true;
                for (String paramValue : paramValueObject.getValue()) {
                    if (paramValue.equals("")) {
                        editFinish = false;
                        break;
                    }
                }
                if (editFinish) {
                    addData();
                }
            }
            parameterTableViewer.refresh();
            validate();
        }
    }

    /**
     * add a tableItem to last if all value of last tableItem be set and the tableItem itself is
     * last
     *
     * @param selectIndex
     * @param tableItem
     */
    public void addTableItemToLast(int selectIndex, TableItem tableItem) {
        // if this is the last data and all parameter has value ,auto add a value
        if (valueList.size() - 1 == selectIndex) {
            boolean editFinish = true;
            for (int i = 0; i < parameterTypeTable.getColumnCount(); i++) {
                String value = tableItem.getText(i);
                if (value.equals("")) {
                    editFinish = false;
                    break;
                }
            }
            if (editFinish) {
                addData();
            }
        }
    }

    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            if (validate()) { // FIXME move this logic to core module
                List<List<PstmtParameter>> jobList = new ArrayList<List<PstmtParameter>>();
                for (int j = 0; j < valueList.size(); j++) {
                    ParamValueObject paramValueObject = valueList.get(j);
                    // don't execute the empty line
                    if (paramValueObject.isEmpty()) {
                        continue;
                    }
                    List<PstmtParameter> parameterList = new ArrayList<PstmtParameter>();
                    for (int i = 1; i < parameterTypeTable.getColumnCount(); i++) {
                        String type = parameterTypeTable.getItem(0).getText(i);
                        ;

                        if (columnTypeMap.get(type) != null) {
                            type = columnTypeMap.get(type);
                        }
                        type = type.toUpperCase();
                        String name = parameterTypeTable.getColumn(i).getText();
                        String value = paramValueObject.getValue().get(i);
                        // parse default value
                        if (isInsert
                                && (value.equals("")
                                        || value.equals(DataType.NULL_EXPORT_FORMAT))) {
                            DBAttribute da = (DBAttribute) schemaInfo.getAttributes().get(i - 1);
                            value = da.getDefault() == null ? "" : da.getDefault();
                        }
                        if (DataType.NULL_EXPORT_FORMAT.equals(value)) {
                            value = null;
                        }
                        // parse nchar nvchar null value
                        if ((type.startsWith(DataType.DATATYPE_NCHAR)
                                        || type.startsWith(DataType.DATATYPE_NATIONAL_CHARACTER))
                                && "".equals(value)) {
                            value = null;
                        }

                        PstmtParameter pstmtParameter = new PstmtParameter(name, i, type, value);

                        boolean isFile = DBAttrTypeFormatter.isFilePath(value);
                        if (isFile) {
                            pstmtParameter.setCharSet(charSet);
                        }
                        parameterList.add(pstmtParameter);
                    }
                    jobList.add(parameterList);
                }

                if (jobList.size() == 0) {
                    setErrorMessage(Messages.errRunPstmtNoJob);
                    return;
                }

                if (TableUtil.isHasResultSet(database, sqlTxt.getText())) {
                    // only execute one select sql
                    showResultSet(jobList.get(0));
                } else {
                    updateData(jobList);
                }
            }
        } else if (buttonId == IDialogConstants.CANCEL_ID) {
            super.buttonPressed(buttonId);
        }
    }

    /**
     * Update the data
     *
     * @param parameterList List<PstmtParameter>
     */
    private void updateData(List<List<PstmtParameter>> jobList) {
        beginTimestamp = System.currentTimeMillis();
        String jobName = Messages.executeSqlJobName;
        JobFamily jobFamily = new JobFamily();
        String serverName = database.getServer().getServerInfo().getServerName();
        String dbName = database.getName();
        jobFamily.setServerName(serverName);
        jobFamily.setDbName(dbName);
        TaskJobExecutor taskExec = new CommonTaskJobExec(this);
        for (int i = 0; i < jobList.size(); i++) {
            List<PstmtParameter> parameterList = jobList.get(i);
            PstmtDataTask task =
                    new PstmtDataTask(sqlTxt.getText(), database, parameterList, null, i + 1);
            taskExec.addTask(task);
        }
        taskExec.schedule(jobName, jobFamily, false, Job.SHORT);
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
            } else {
                queryEditor.setQuery(allInputSql, true, false, false);
            }
        }
        close();
    }

    /**
     * Get comments SQL value
     *
     * @param parameterList List<PstmtParameter>
     * @return String
     */
    protected String getCommentSqlValue(
            List<PstmtParameter> parameterList) { // FIXME move this logic to core module
        StringBuffer valueComments = new StringBuffer("--");
        for (PstmtParameter parameter : parameterList) {
            if (parameter.getStringParamValue() == null) {
                valueComments.append("NULL");
                valueComments.append(",");
            } else {
                valueComments.append(parameter.getStringParamValue());
                valueComments.append(",");
            }
        }
        valueComments = valueComments.deleteCharAt(valueComments.length() - 1);
        valueComments.append(StringUtil.NEWLINE);
        return valueComments.toString();
    }

    /**
     * generate ParamValueObject list from clipboard string
     *
     * @param plainText
     * @return
     */
    public List<ParamValueObject> generateParamValueObjectListFromClipboardString(
            String plainText) { // FIXME move this logic to core module
        List<ParamValueObject> paramValueObjectList = new ArrayList<ParamValueObject>();

        for (String oneData : plainText.split(EXCELDATASEPRATOR)) {

            ParamValueObject paramValueObject = new ParamValueObject();
            paramValueObject.getValue().add(String.valueOf(valueList.size()));
            int index = 1;
            for (String oneColumn : oneData.split(EXCELCOLUMNSEPRATOR)) {
                paramValueObject.getValue().add(oneColumn);
                index++;
            }
            // if some data of excel column less than the parameterTypeTable column, add "" value
            while (index++ < parameterTypeTable.getColumnCount()) {
                paramValueObject.getValue().add("");
            }
            if (!paramValueObject.isEmpty()) {
                paramValueObjectList.add(paramValueObject);
            }
        }
        return paramValueObjectList;
    }

    public void refreshValueListIndex() {
        for (int i = 0; i < valueList.size(); i++) {
            ParamValueObject paramValueObject = valueList.get(i);
            paramValueObject.getValue().set(0, Integer.toString(i + 1));
        }
    }
}
