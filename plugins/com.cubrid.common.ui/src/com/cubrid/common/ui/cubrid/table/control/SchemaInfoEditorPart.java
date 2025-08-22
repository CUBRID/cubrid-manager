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
package com.cubrid.common.ui.cubrid.table.control;

import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.cubrid.table.action.ShowSchemaEditorAction;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.query.editor.QueryUnit;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.MenuProvider;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.part.CubridEditorPart;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * Schema editor
 *
 * @author pangqiren 2009-6-4
 */
public class SchemaInfoEditorPart extends CubridEditorPart {
    public static final String ID = SchemaInfoEditorPart.class.getName(); // $NON-NLS-1$
    private final Logger LOGGER = LogUtil.getLogger(getClass());

    private Composite topComposite = null;
    private Table generalInfoTable = null;
    private StyledText txtViewSpec = null;
    private TableViewer columnTableView;
    private SchemaInfo schemaInfo;
    private TableViewer fkTableView;
    private TableViewer indexTableView;

    private static final int WIDTH_UNIQUECOLUMN = 70;
    private static final int WIDTH_NOTNULLCOLUMN = 70;
    private static final int WIDTH_DATATYPECOLUMN = 120;
    private static final int WIDTH_NAMECOLUMN = 160;
    private static final int WIDTH_PKCOLUMN = 30;
    private static final int WIDTH_SHAREDCOLUMN = 70;

    private String nodeName;
    private DatabaseInfo database;
    private DefaultSchemaNode schemaNode;
    private ICubridNode cubridNode;

    private final Color color = ResourceManager.getColor(230, 230, 230);

    /**
     * @see com.cubrid.common.ui.spi.part.CubridEditorPart#init(org.eclipse.ui.IEditorSite,
     *     org.eclipse.ui.IEditorInput)
     * @param site the editor site
     * @param input the editor input
     * @exception PartInitException if this editor was not initialized successfully
     */
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        super.init(site, input);
        if (input instanceof DefaultSchemaNode) {
            cubridNode = (DefaultSchemaNode) input;
            if (null == cubridNode
                    || (!NodeType.USER_PARTITIONED_TABLE_FOLDER.equals(cubridNode.getType())
                            && !NodeType.USER_TABLE.equals(cubridNode.getType())
                            && !NodeType.USER_VIEW.equals(cubridNode.getType())
                            && !NodeType.SYSTEM_TABLE.equals(cubridNode.getType())
                            && !NodeType.SYSTEM_VIEW.equals(cubridNode.getType())
                            && !NodeType.USER_PARTITIONED_TABLE.equals(cubridNode.getType()))) {
                return;
            }

            nodeName = cubridNode.getLabel().trim();
            schemaNode = (DefaultSchemaNode) cubridNode;
            database = schemaNode.getDatabase().getDatabaseInfo();
            schemaInfo = database.getSchemaInfo(nodeName);
            setTitleImage(cubridNode.getImageDescriptor().createImage());
        }
    }

    /** Load the data */
    private void refresh() {
        schemaInfo = database.getSchemaInfo(nodeName);
        if (schemaInfo == null) {
            com.cubrid.common.ui.spi.util.CommonUITool.openErrorBox(database.getErrorMessage());
        } else {
            fillGeneralTableInfo();
            AttributeTableViewerLabelProvider provider =
                    (AttributeTableViewerLabelProvider) columnTableView.getLabelProvider();
            provider.setSchema(schemaInfo);
            columnTableView.setInput(schemaInfo);
            fkTableView.setInput(schemaInfo);
            indexTableView.setInput(schemaInfo);
            fillTextViewSpec();
        }
    }

    /**
     * @see
     *     com.cubrid.common.ui.spi.event.ICubridNodeChangedListener#nodeChanged(com.cubrid.common.ui.spi.event.CubridNodeChangedEvent)
     * @param event the CubridNodeChangedEvent object
     */
    public void nodeChanged(CubridNodeChangedEvent event) {
        ICubridNode eventNode = event.getCubridNode();
        if (eventNode == null) {
            return;
        }
        CubridNodeChangedEventType type = event.getType();
        ICubridNode node = null;
        if (type == CubridNodeChangedEventType.CONTAINER_NODE_REFRESH) {
            node = eventNode.getChild(cubridNode == null ? "" : cubridNode.getId());
        } else if (type == CubridNodeChangedEventType.NODE_REFRESH
                && eventNode.getId().equals(cubridNode.getId())) {
            node = eventNode;
        } else {
            return;
        }
        if (node == null) {
            return;
        }
        cubridNode = node;
        synchronized (this) {
            database.clearSchemas();
            refresh();
        }
    }

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     * @param parent the parent control
     * @see IWorkbenchPart
     */
    public void createPartControl(Composite parent) {

        ScrolledComposite scrolledComp = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        FillLayout flayout = new FillLayout();
        scrolledComp.setLayout(flayout);

        topComposite = new Composite(scrolledComp, SWT.NONE);
        {
            topComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
            topComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
            GridLayout gridLayout = new GridLayout();
            gridLayout.marginWidth = 10;
            gridLayout.marginHeight = 10;
            topComposite.setLayout(gridLayout);
            createContextMenu(topComposite);
        }

        if (schemaInfo != null) {
            createTitle();

            createColumnsTable();

            if ("normal".equals(schemaInfo.getVirtual())) {
                createIndexTable();
            }

            if ("normal".equals(schemaInfo.getVirtual())) { // $NON-NLS-1$
                createFKTable();
            } else if ("view".equals(schemaInfo.getVirtual())) { // $NON-NLS-1$
                createTextViewSpec();
            }

            createGeneralInfoTable();
        }
        scrolledComp.setContent(topComposite);
        scrolledComp.setMinHeight(800);
        // scrolledComp.setMinWidth(800);
        scrolledComp.setExpandHorizontal(true);
        scrolledComp.setExpandVertical(true);
    }

    /**
     * Create the context menu
     *
     * @param control Control
     */
    private void createContextMenu(final Control control) {
        MenuManager contextMenuManager = new MenuManager();
        contextMenuManager.setRemoveAllWhenShown(true);
        contextMenuManager.addMenuListener(
                new IMenuListener() {
                    public void menuAboutToShow(IMenuManager manager) {
                        ISelectionProvider provider =
                                ActionManager.getInstance().getSelectionProvider();
                        if (!(provider instanceof TreeViewer)) {
                            return;
                        }
                        TreeViewer viewer = (TreeViewer) provider;
                        viewer.setSelection(new StructuredSelection(schemaNode), true);
                        viewer.getTree().setFocus();
                        MenuProvider menuProvider = new MenuProvider();
                        String type = schemaNode.getType();
                        if (NodeType.SYSTEM_TABLE.equals(type)) {
                            menuProvider.buildSystemTableMenu(manager);
                        } else if (NodeType.SYSTEM_VIEW.equals(type)) {
                            menuProvider.buildSystemViewMenu(manager);
                        } else if (NodeType.USER_PARTITIONED_TABLE_FOLDER.equals(type)) {
                            menuProvider.buildUserTableMenu(manager, schemaNode);
                        } else if (NodeType.USER_PARTITIONED_TABLE.equals(type)) {
                            menuProvider.buildPartitionedTableMenu(manager);
                        } else if (NodeType.USER_TABLE.equals(type)) {
                            menuProvider.buildUserTableMenu(manager, schemaNode);
                        } else if (NodeType.USER_VIEW.equals(type)) {
                            menuProvider.buildUserViewMenu(manager);
                        }
                        manager.remove(ShowSchemaEditorAction.ID);
                    }
                });
        Menu contextMenu = contextMenuManager.createContextMenu(control);
        control.setMenu(contextMenu);
    }

    /** Create the general information table */
    private void createGeneralInfoTable() {
        Label colslabel = new Label(topComposite, SWT.LEFT | SWT.WRAP);
        colslabel.setText(Messages.lblGeneral);
        colslabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

        generalInfoTable =
                new Table(
                        topComposite, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION | SWT.NO_SCROLL);
        {
            GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
            generalInfoTable.setLayoutData(gridData);
            new TableColumn(generalInfoTable, SWT.LEFT);
            new TableColumn(generalInfoTable, SWT.LEFT);

            TableLayout tlayout = new TableLayout();
            tlayout.addColumnData(new ColumnWeightData(20, true));
            tlayout.addColumnData(new ColumnWeightData(100, true));
            generalInfoTable.setLayout(tlayout);

            generalInfoTable.setLinesVisible(true);
            generalInfoTable.setHeaderVisible(false);

            setTableEditor(generalInfoTable, 1);
            createContextMenu(generalInfoTable);
        }
        fillGeneralTableInfo();
    }

    /** Create a title area */
    private void createTitle() {
        Label schemaNameLabel = new Label(topComposite, SWT.LEFT | SWT.WRAP);
        schemaNameLabel.setText(schemaInfo.getClassname());
        schemaNameLabel.setFont(
                ResourceManager.getFont(schemaNameLabel.getFont().toString(), 16, SWT.BOLD));
        schemaNameLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    }

    /** Fill the general table information */
    private void fillGeneralTableInfo() {
        if (schemaInfo == null || generalInfoTable == null || generalInfoTable.isDisposed()) {
            return;
        }
        generalInfoTable.removeAll();

        TableItem item;
        item = new TableItem(generalInfoTable, SWT.NONE);
        item.setText(0, Messages.colSchemaType);
        item.setText(
                1,
                "system".equals(schemaInfo.getType())
                        ? Messages.infoSystemSchema // $NON-NLS-1$
                        : Messages.infoUserSchema);

        item = new TableItem(generalInfoTable, SWT.NONE);
        item.setText(0, Messages.infoOwner);
        item.setText(1, schemaInfo.getOwner());

        item = new TableItem(generalInfoTable, SWT.NONE);
        item.setText(0, Messages.infoSuperClasses);

        String superstr = ""; // $NON-NLS-1$
        List<String> superClasses = schemaInfo.getSuperClasses();
        for (int si = 0; si < superClasses.size(); si++) {
            if (si > 0) {
                superstr = superstr.concat(", "); // $NON-NLS-1$
            }
            superstr = superstr.concat(superClasses.get(si));
        }
        item.setText(1, superstr);

        item = new TableItem(generalInfoTable, SWT.NONE);
        item.setText(0, Messages.infoType);
        item.setText(
                1,
                "normal".equals(schemaInfo.getVirtual())
                        ? Messages.typeTable // $NON-NLS-1$
                        : Messages.typeView);

        if (generalInfoTable != null && !generalInfoTable.isDisposed()) {
            for (int i = 0; i < generalInfoTable.getColumnCount(); i++) {
                generalInfoTable.getColumn(i).pack();
            }
            for (int i = 0; i < (1 + generalInfoTable.getItemCount()) / 2; i++) {
                generalInfoTable.getItem(i * 2).setBackground(color);
            }
        }
    }

    /** Create the column information table */
    private void createColumnsTable() {
        Label colslabel = new Label(topComposite, SWT.LEFT | SWT.WRAP);
        colslabel.setText(Messages.lblColumns);
        colslabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

        columnTableView =
                new TableViewer(topComposite, SWT.FULL_SELECTION | SWT.SIMPLE | SWT.BORDER);
        Table columnsTable = columnTableView.getTable();
        {
            createContextMenu(columnsTable);

            columnsTable.setLinesVisible(true);
            columnsTable.setHeaderVisible(true);

            final GridData gdColumnsTable = new GridData(SWT.FILL, SWT.FILL, true, true);
            gdColumnsTable.heightHint = 189;
            columnsTable.setLayoutData(gdColumnsTable);

            // PK
            TableColumn tblCol = new TableColumn(columnsTable, SWT.NONE);
            tblCol.setAlignment(SWT.CENTER);
            tblCol.setWidth(WIDTH_PKCOLUMN);
            tblCol.setText(Messages.tblColumnPK);

            // NAME
            tblCol = new TableColumn(columnsTable, SWT.NONE);
            tblCol.setWidth(WIDTH_NAMECOLUMN);
            tblCol.setText(Messages.tblColumnName);

            // DATATYPE
            tblCol = new TableColumn(columnsTable, SWT.NONE);
            tblCol.setWidth(WIDTH_DATATYPECOLUMN);
            tblCol.setText(Messages.tblColumnDataType);

            // DEFAULT
            tblCol = new TableColumn(columnsTable, SWT.NONE);
            tblCol.setWidth(98);
            tblCol.setText(Messages.tblColumnDefault);

            // AUTO INCREMENT
            tblCol = new TableColumn(columnsTable, SWT.NONE);
            tblCol.setAlignment(SWT.CENTER);
            tblCol.setWidth(100);
            tblCol.setText(Messages.tblColumnAutoIncr);

            // NOT NULL
            tblCol = new TableColumn(columnsTable, SWT.NONE);
            tblCol.setWidth(WIDTH_NOTNULLCOLUMN);
            tblCol.setText(Messages.tblColumnNotNull);
            tblCol.setAlignment(SWT.CENTER);

            // UK
            tblCol = new TableColumn(columnsTable, SWT.NONE);
            tblCol.setWidth(WIDTH_UNIQUECOLUMN);
            tblCol.setText(Messages.tblColumnUnique);
            tblCol.setAlignment(SWT.CENTER);

            // SHARED
            tblCol = new TableColumn(columnsTable, SWT.NONE);
            tblCol.setWidth(WIDTH_SHAREDCOLUMN);
            tblCol.setResizable(true);
            tblCol.setText(Messages.tblColumnShared);
            tblCol.setAlignment(SWT.CENTER);

            // INHERIT
            tblCol = new TableColumn(columnsTable, SWT.NONE);
            tblCol.setAlignment(SWT.CENTER);
            tblCol.setWidth(90);
            tblCol.setResizable(true);
            tblCol.setText(Messages.tblColumnInherit);

            // CLASS
            tblCol = new TableColumn(columnsTable, SWT.NONE);
            tblCol.setWidth(90);
            tblCol.setResizable(true);
            tblCol.setText(Messages.tblColumnClass);
            tblCol.setAlignment(SWT.CENTER);
        }

        columnTableView.setContentProvider(new AttributeTableViewerContentProvider());
        columnTableView.setLabelProvider(
                new AttributeTableViewerLabelProvider(database, schemaInfo));
        CommonUITool.hackForYosemite(columnTableView.getTable());
        columnTableView.setInput(schemaInfo);

        setTableEditor(columnsTable, 1);
        setTableEditor(columnsTable, 2);
    }

    /**
     * Set table editor
     *
     * @param table Table
     * @param columnIndex index
     */
    private void setTableEditor(final Table table, final int columnIndex) {
        final TableEditor editor = new TableEditor(table);
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal = true;
        table.addListener(
                SWT.MouseUp,
                new Listener() {
                    public void handleEvent(Event event) {
                        if (event.button != 1) {
                            return;
                        }
                        Point pt = new Point(event.x, event.y);
                        int topIndex = table.getTopIndex();
                        int selectedIndex = table.getSelectionIndex();
                        if (selectedIndex < 0 || topIndex > selectedIndex) {
                            return;
                        }
                        final TableItem item = table.getItem(selectedIndex);
                        if (item == null) {
                            return;
                        }
                        Rectangle rect = item.getBounds(columnIndex);
                        if (rect.contains(pt)) {
                            Control oldEditor = editor.getEditor();
                            if (oldEditor != null) {
                                oldEditor.dispose();
                            }

                            final StyledText newEditor = new StyledText(table, SWT.READ_ONLY);
                            newEditor.addListener(
                                    SWT.FocusOut,
                                    new Listener() {
                                        public void handleEvent(final Event event) {
                                            if (event.type == SWT.FocusOut) {
                                                newEditor.dispose();
                                            }
                                        }
                                    });
                            com.cubrid.common.ui.spi.util.CommonUITool.registerContextMenu(
                                    newEditor, false);
                            newEditor.setText(item.getText(columnIndex));
                            newEditor.selectAll();
                            newEditor.setFocus();
                            editor.setEditor(newEditor, item, columnIndex);
                        }
                    }
                });
    }

    /** Create foreign key information table */
    private void createFKTable() {
        Label fkLabel = new Label(topComposite, SWT.LEFT | SWT.WRAP);
        fkLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        fkLabel.setText(Messages.lblFK);

        fkTableView = new TableViewer(topComposite, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
        Table fkTable = fkTableView.getTable();
        {
            createContextMenu(fkTable);
            final GridData gdFkTable = new GridData(SWT.FILL, SWT.FILL, true, true);
            fkTable.setLayoutData(gdFkTable);
            fkTable.setLinesVisible(true);
            fkTable.setHeaderVisible(true);
            CommonUITool.hackForYosemite(fkTable);

            TableColumn tblCol = new TableColumn(fkTable, SWT.NONE);
            tblCol.setWidth(160);
            tblCol.setText(Messages.tblColumnFK);

            tblCol = new TableColumn(fkTable, SWT.NONE);
            tblCol.setWidth(119);
            tblCol.setText(Messages.tblColumnColumnName);

            tblCol = new TableColumn(fkTable, SWT.NONE);
            tblCol.setWidth(93);
            tblCol.setText(Messages.tblColumnForeignTable);

            tblCol = new TableColumn(fkTable, SWT.NONE);
            tblCol.setWidth(143);
            tblCol.setText(Messages.tblColumnForeignColumnName);

            tblCol = new TableColumn(fkTable, SWT.NONE);
            tblCol.setWidth(84);
            tblCol.setText(Messages.tblColumnUpdateRule);

            tblCol = new TableColumn(fkTable, SWT.NONE);
            tblCol.setWidth(86);
            tblCol.setText(Messages.tblColumnDeleteRule);

            tblCol = new TableColumn(fkTable, SWT.NONE);
            tblCol.setWidth(100);
            tblCol.setText(Messages.tblColumnCacheColumn);
        }
        fkTableView.setContentProvider(new FKTableViewerContentProvider());
        fkTableView.setLabelProvider(new FKTableViewerLabelProvider(database));
        fkTableView.setInput(schemaInfo);
        int colNum = fkTable.getColumnCount();
        for (int i = 0; i < colNum; i++) {
            setTableEditor(fkTable, i);
        }
    }

    /** Create index information table */
    private void createIndexTable() {
        int colNum;
        Label indexLabel = new Label(topComposite, SWT.LEFT | SWT.WRAP);
        indexLabel.setText(Messages.lblIndexes);
        indexLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

        indexTableView = new TableViewer(topComposite, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
        Table indexTable = indexTableView.getTable();
        {
            createContextMenu(indexTable);
            indexTable.setLinesVisible(true);
            indexTable.setHeaderVisible(true);
            CommonUITool.hackForYosemite(indexTable);

            final GridData gdIndexTable = new GridData(SWT.FILL, SWT.FILL, true, true);
            indexTable.setLayoutData(gdIndexTable);

            TableColumn tblCol = new TableColumn(indexTable, SWT.NONE);
            tblCol.setWidth(180);
            tblCol.setText(Messages.tblColumnIndexName);

            tblCol = new TableColumn(indexTable, SWT.NONE);
            tblCol.setWidth(90);
            tblCol.setText(Messages.tblColumnIndexType);

            tblCol = new TableColumn(indexTable, SWT.NONE);
            tblCol.setWidth(240);
            tblCol.setText(Messages.tblColumnOnColumns);

            tblCol = new TableColumn(indexTable, SWT.NONE);
            tblCol.setWidth(332);
            tblCol.setText(Messages.tblColumnIndexRule);
        }
        indexTableView.setContentProvider(new IndexTableViewerContentProvider());
        indexTableView.setLabelProvider(new IndexTableViewerLabelProvider());
        indexTableView.setInput(schemaInfo);

        colNum = indexTable.getColumnCount();
        for (int i = 0; i < colNum; i++) {
            setTableEditor(indexTable, i);
        }
    }

    /** Create Text View Spec */
    private void createTextViewSpec() {
        Label specLabel = new Label(topComposite, SWT.LEFT | SWT.WRAP);
        specLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        specLabel.setText(Messages.lblQuerySpec);

        txtViewSpec = new StyledText(topComposite, SWT.WRAP | SWT.BORDER);
        txtViewSpec.setLayoutData(new GridData(GridData.FILL_BOTH));
        txtViewSpec.setEditable(false);
        txtViewSpec.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        com.cubrid.common.ui.spi.util.CommonUITool.registerContextMenu(txtViewSpec, false);
        fillTextViewSpec();
    }

    /** fill Text View Spec */
    private void fillTextViewSpec() {
        if (txtViewSpec == null || txtViewSpec.isDisposed()) {
            return;
        }
        txtViewSpec.setText(""); // $NON-NLS-1$
        List<String> querySpecs = schemaInfo.getQuerySpecs();
        for (int i = 0, n = querySpecs.size(); i < n; i++) {
            txtViewSpec.append(querySpecs.get(i));
            txtViewSpec.append(StringUtil.NEWLINE);
        }
    }

    /**
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     * @param monitor IProgressMonitor
     */
    public void doSave(IProgressMonitor monitor) {
        firePropertyChange(PROP_DIRTY);
    }

    /** @see org.eclipse.ui.part.EditorPart#doSaveAs() */
    public void doSaveAs() {
        // empty
    }

    /**
     * @see org.eclipse.ui.part.EditorPart#isDirty()
     * @return boolean
     */
    public boolean isDirty() {
        return false;
    }

    /**
     * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
     * @return boolean
     */
    public boolean isSaveAsAllowed() {
        return false;
    }

    @Override
    public void dispose() {

        if (cubridNode.getType().equals(NodeType.USER_TABLE)
                || cubridNode.getType().equals(NodeType.USER_VIEW)
                || cubridNode.getType().equals(NodeType.SYSTEM_TABLE)
                || cubridNode.getType().equals(NodeType.SYSTEM_VIEW)) {
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            QueryUnit unit = new QueryUnit();
            unit.setDatabase(((DefaultSchemaNode) cubridNode).getDatabase());

            try {
                IEditorPart editor = window.getActivePage().openEditor(unit, QueryEditorPart.ID);
                if (editor != null) ((QueryEditorPart) editor).connect(unit.getDatabase());
            } catch (PartInitException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        super.dispose();
    }
}
