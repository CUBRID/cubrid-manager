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
package com.cubrid.common.ui.cubrid.serial.editor;

import com.cubrid.common.core.common.model.SerialInfo;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.cubrid.serial.Messages;
import com.cubrid.common.ui.cubrid.serial.action.CreateSerialAction;
import com.cubrid.common.ui.cubrid.serial.action.DeleteSerialAction;
import com.cubrid.common.ui.cubrid.serial.action.EditSerialAction;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.part.CubridEditorPart;
import com.cubrid.common.ui.spi.progress.OpenSerialDetailInfoPartProgress;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * All serial information editor part
 *
 * @author fulei
 * @version 1.0 - 2013-1-9 created by fulei
 */
public class SerialDashboardEditorPart extends CubridEditorPart {
    private static final Logger LOGGER = LogUtil.getLogger(SerialDashboardEditorPart.class);
    public static final String ID = SerialDashboardEditorPart.class.getName();
    private boolean serialChangeFlag = false;
    private CubridDatabase database;
    private TableViewer serialsDetailInfoTable;
    private List<SerialInfo> serialList;

    private static final String SERIAL_MIN = "-1000000000000000000000000000000000000";
    private static final String SERIAL_MIN_AFTER_120 = "-99999999999999999999999999999999999999";
    private static final String SERIAL_MAX = "10000000000000000000000000000000000000";
    private static final String SERIAL_MAX_AFTER_120 = "99999999999999999999999999999999999999";

    public void createPartControl(Composite parent) {
        parent.setLayout(new GridLayout(1, false));

        ToolBar toolBar = new ToolBar(parent, SWT.LEFT_TO_RIGHT | SWT.FLAT);
        toolBar.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        ToolItem refreshItem = new ToolItem(toolBar, SWT.PUSH);
        refreshItem.setText(
                com.cubrid.common.ui.cubrid.table.Messages.tablesDetailInfoPartRefreshBtn);
        refreshItem.setImage(CommonUIPlugin.getImage("icons/action/refresh.png"));
        refreshItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        refresh();
                    }
                });

        new ToolItem(toolBar, SWT.SEPARATOR);
        ToolItem addSerailItem = new ToolItem(toolBar, SWT.NONE);
        addSerailItem.setText(Messages.serialsDetailInfoPartCreateSerialBtn);
        addSerailItem.setImage(CommonUIPlugin.getImage("icons/action/serial_add.png"));
        addSerailItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        addSerial();
                    }
                });

        ToolItem editSerailItem = new ToolItem(toolBar, SWT.NONE);
        editSerailItem.setText(Messages.serialsDetailInfoPartEditSerialBtn);
        editSerailItem.setImage(CommonUIPlugin.getImage("icons/action/serial_edit.png"));
        editSerailItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        editSerial();
                    }
                });

        ToolItem dropSerailItem = new ToolItem(toolBar, SWT.NONE);
        dropSerailItem.setText(Messages.serialsDetailInfoPartDropSerialBtn);
        dropSerailItem.setImage(CommonUIPlugin.getImage("icons/action/serial_delete.png"));
        dropSerailItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        dropSerial();
                    }
                });

        createSerialsDetailInfoTable(parent);
        this.setInputs();
    }

    public void createSerialsDetailInfoTable(Composite parent) {
        final Composite tableComposite = new Composite(parent, SWT.NONE);
        tableComposite.setLayout(new FillLayout());
        tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        serialsDetailInfoTable =
                new TableViewer(
                        tableComposite,
                        SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
        serialsDetailInfoTable.getTable().setHeaderVisible(true);
        serialsDetailInfoTable.getTable().setLinesVisible(true);
        CommonUITool.hackForYosemite(serialsDetailInfoTable.getTable());

        final TableViewerColumn ownerColumn =
                new TableViewerColumn(serialsDetailInfoTable, SWT.LEFT);
        ownerColumn.getColumn().setWidth(80);
        ownerColumn.getColumn().setText(Messages.serialsDetailInfoPartTableOwnerCol);

        final TableViewerColumn nameColumn =
                new TableViewerColumn(serialsDetailInfoTable, SWT.LEFT);
        nameColumn.getColumn().setWidth(150);
        nameColumn.getColumn().setText(Messages.serialsDetailInfoPartTableNameCol);

        final TableViewerColumn curValColumn =
                new TableViewerColumn(serialsDetailInfoTable, SWT.LEFT);
        curValColumn.getColumn().setWidth(120);
        curValColumn.getColumn().setText(Messages.serialsDetailInfoPartTableCurValCol);

        final TableViewerColumn increValColumn =
                new TableViewerColumn(serialsDetailInfoTable, SWT.LEFT);
        increValColumn.getColumn().setWidth(120);
        increValColumn.getColumn().setText(Messages.serialsDetailInfoPartTableIncreValCol);

        final TableViewerColumn minValColumn =
                new TableViewerColumn(serialsDetailInfoTable, SWT.LEFT);
        minValColumn.getColumn().setWidth(100);
        minValColumn.getColumn().setText(Messages.serialsDetailInfoPartTableMinValCol);

        final TableViewerColumn maxValColumn =
                new TableViewerColumn(serialsDetailInfoTable, SWT.LEFT);
        maxValColumn.getColumn().setWidth(100);
        maxValColumn.getColumn().setText(Messages.serialsDetailInfoPartTableMaxValCol);

        final TableViewerColumn cacheNumColumn =
                new TableViewerColumn(serialsDetailInfoTable, SWT.LEFT);
        cacheNumColumn.getColumn().setWidth(70);
        cacheNumColumn.getColumn().setText(Messages.serialsDetailInfoPartTableCacheNumCol);

        final TableViewerColumn cycleColumn =
                new TableViewerColumn(serialsDetailInfoTable, SWT.LEFT);
        cycleColumn.getColumn().setWidth(50);
        cycleColumn.getColumn().setText(Messages.serialsDetailInfoPartTableCycleCol);

        serialsDetailInfoTable.setComparator(new ColumnViewerSorter());
        serialsDetailInfoTable.setContentProvider(new SerialsDetailTableViewerContentProvider());
        serialsDetailInfoTable.setLabelProvider(new SerialTableViewerLabelProvider());
        serialsDetailInfoTable.addDoubleClickListener(
                new IDoubleClickListener() {
                    public void doubleClick(DoubleClickEvent event) {
                        StructuredSelection sel = (StructuredSelection) event.getSelection();
                        if (sel == null) {
                            return;
                        }
                        SerialInfo serialInfo = (SerialInfo) sel.getFirstElement();
                        if (serialInfo == null) {
                            return;
                        }
                        openEditSerialDialog(serialInfo);
                    }
                });
        registerContextMenu();
    }

    private void registerContextMenu() {
        serialsDetailInfoTable
                .getTable()
                .addFocusListener(
                        new FocusAdapter() {
                            public void focusGained(FocusEvent event) {
                                ActionManager.getInstance()
                                        .changeFocusProvider(serialsDetailInfoTable.getTable());
                            }
                        });

        MenuManager menuManager = new MenuManager();
        menuManager.setRemoveAllWhenShown(true);

        Menu contextMenu = menuManager.createContextMenu(serialsDetailInfoTable.getTable());
        serialsDetailInfoTable.getTable().setMenu(contextMenu);

        Menu menu =
                new Menu(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        SWT.POP_UP);

        final MenuItem editSerialItem = new MenuItem(menu, SWT.PUSH);
        editSerialItem.setText(Messages.serialsDetailInfoPartEditSerialBtn);
        editSerialItem.setImage(CommonUIPlugin.getImage("icons/action/serial_edit.png"));
        editSerialItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        editSerial();
                    }
                });

        final MenuItem dropSerialItem = new MenuItem(menu, SWT.PUSH);
        dropSerialItem.setText(Messages.serialsDetailInfoPartDropSerialBtn);
        dropSerialItem.setImage(CommonUIPlugin.getImage("icons/action/serial_delete.png"));
        dropSerialItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        dropSerial();
                    }
                });

        new MenuItem(menu, SWT.SEPARATOR);

        final MenuItem addSerialItem = new MenuItem(menu, SWT.PUSH);
        addSerialItem.setText(Messages.serialsDetailInfoPartCreateSerialBtn);
        addSerialItem.setImage(CommonUIPlugin.getImage("icons/action/serial_add.png"));
        addSerialItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        addSerial();
                    }
                });

        serialsDetailInfoTable.getTable().setMenu(menu);
    }

    public void addSerial() {
        CreateSerialAction action =
                (CreateSerialAction) ActionManager.getInstance().getAction(CreateSerialAction.ID);
        action.run(database);
        refresh();
    }

    public void editSerial() {
        TableItem[] items = serialsDetailInfoTable.getTable().getSelection();
        if (items.length != 0) {
            TableItem item = items[0];
            SerialInfo serialInfo = (SerialInfo) item.getData();
            openEditSerialDialog(serialInfo);
        } else {
            CommonUITool.openWarningBox(Messages.errSerialNoSelection);
        }
    }

    private void openEditSerialDialog(SerialInfo serialInfo) {
        Set<String> typeSet = new HashSet<String>();
        typeSet.add(NodeType.SERIAL);
        ICubridNode serialNode =
                CommonUITool.findNode(
                        database, typeSet, serialInfo.getUniqueName(isSupportUserSchema()));
        if (serialNode != null) {
            EditSerialAction action =
                    (EditSerialAction) ActionManager.getInstance().getAction(EditSerialAction.ID);
            if (action.run(database, (ISchemaNode) serialNode) == IDialogConstants.OK_ID) {
                refresh();
            }
        }
    }

    public void dropSerial() {
        TableItem[] items = serialsDetailInfoTable.getTable().getSelection();
        if (items.length > 0) {
            List<ISchemaNode> selectNodeList = new ArrayList<ISchemaNode>();
            for (TableItem item : items) {
                SerialInfo serialInfo = (SerialInfo) item.getData();
                Set<String> typeSet = new HashSet<String>();
                typeSet.add(NodeType.SERIAL);

                ICubridNode serialNode =
                        CommonUITool.findNode(
                                database, typeSet, serialInfo.getUniqueName(isSupportUserSchema()));
                selectNodeList.add((ISchemaNode) serialNode);
            }

            if (selectNodeList.size() > 0) {
                DeleteSerialAction action =
                        (DeleteSerialAction)
                                ActionManager.getInstance().getAction(DeleteSerialAction.ID);
                if (action == null) {
                    LOGGER.error("DeleteSerialAction is a null.");
                    return;
                }
                ISchemaNode[] nodeArr = new ISchemaNode[selectNodeList.size()];
                action.run(selectNodeList.toArray(nodeArr));
                refresh();
            }
        } else {
            CommonUITool.openWarningBox(Messages.errSerialNoSelection);
        }
    }

    public void refresh() {
        OpenSerialDetailInfoPartProgress progress = new OpenSerialDetailInfoPartProgress(database);
        progress.loadSerialInfoList();
        if (progress.isSuccess()) {
            serialList = progress.getSerialList();
            setInputs();
        }
        serialChangeFlag = false;
    }

    public void select(String serialName) {
        if (serialList == null) {
            return;
        }
        Table table = serialsDetailInfoTable.getTable();
        for (int i = 0, len = table.getItemCount(); i < len; i++) {
            TableItem item = table.getItem(i);
            if (StringUtil.isEqual(item.getText(), serialName)) {
                table.setSelection(i);
            }
        }
    }

    public void setInputs() {
        serialsDetailInfoTable.setInput(serialList);
        serialsDetailInfoTable.refresh();
        pack();
    }

    public void pack() {
        for (int i = 0; i < serialsDetailInfoTable.getTable().getColumnCount(); i++) {
            TableColumn column = serialsDetailInfoTable.getTable().getColumn(i);
            if (column.getWidth() > 600) {
                column.setWidth(600);
            }
            if (column.getWidth() < 100) {
                column.setWidth(100);
            }
        }
    }

    public void nodeChanged(CubridNodeChangedEvent event) {
        if (event.getSource() instanceof DefaultSchemaNode) {
            DefaultSchemaNode node = (DefaultSchemaNode) event.getSource();
            if ((node.getType().equals(NodeType.SERIAL_FOLDER)
                    || node.getType().equals(NodeType.SERIAL)
                            && node.getDatabase().equals(database))) {
                serialChangeFlag = true;
            }
        }
        if (CubridNodeChangedEventType.SERVER_DISCONNECTED.equals(event.getType())) {
            close(event, database.getServer());
        }

        if (CubridNodeChangedEventType.DATABASE_LOGOUT.equals(event.getType())
                || CubridNodeChangedEventType.DATABASE_STOP.equals(event.getType())) {
            close(event, database);
        }
    }

    public void setFocus() {
        // if view info chaned, ask whether refresh
        if (serialChangeFlag) {
            if (CommonUITool.openConfirmBox(
                    com.cubrid.common.ui.common.Messages.dashboardConfirmRefreshDataMsg)) {
                refresh();
            }
            serialChangeFlag = false;
        }
    }

    @SuppressWarnings("unchecked")
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        super.init(site, input);

        this.database = (CubridDatabase) input.getAdapter(CubridDatabase.class);
        if (!CubridDatabase.hasValidDatabaseInfo(database)) {
            LOGGER.error("There is an invalid database object.");
            return;
        }

        this.serialList = (List<SerialInfo>) input.getAdapter(List.class);
        if (serialList == null) {
            LOGGER.error("There is an invalid serialList.");
            return;
        }

        StringBuilder partName = new StringBuilder(Messages.serialsDetailInfoPartTitle);
        partName.append(" [")
                .append(database.getUserName())
                .append("@")
                .append(database.getName())
                .append(":")
                .append(database.getDatabaseInfo().getBrokerIP())
                .append("]");
        setPartName(partName.toString());
    }

    /**
     * Serial table label provider
     *
     * @author Administrator
     */
    public class SerialTableViewerLabelProvider extends LabelProvider
            implements ITableLabelProvider {
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            if (!CubridDatabase.hasValidDatabaseInfo(database)) {
                LOGGER.error("The database is invalid.");
                return null;
            }

            if (element instanceof SerialInfo) {
                SerialInfo serialInfo = (SerialInfo) element;
                if (serialInfo != null) {
                    String incrValue = serialInfo.getIncrementValue();
                    boolean isSupportCache =
                            CompatibleUtil.isSupportCache(database.getDatabaseInfo());
                    switch (columnIndex) {
                        case 0:
                            return serialInfo.getOwner();
                        case 1:
                            return serialInfo.getName();
                        case 2:
                            return serialInfo.getCurrentValue();
                        case 3:
                            return serialInfo.getIncrementValue();
                        case 4:
                            String minValue = serialInfo.getMinValue();

                            if (incrValue.indexOf("-") >= 0 && serialMinValue().equals(minValue)) {
                                return "NOMINVALUE";
                            } else {
                                return minValue;
                            }
                        case 5:
                            String maxValue = serialInfo.getMaxValue();
                            if (incrValue.indexOf("-") < 0 && serialMaxValue().equals(maxValue)) {
                                return "NOMAXVALUE";
                            } else {
                                return maxValue;
                            }
                        case 6:
                            String cacheCount = serialInfo.getCacheCount();
                            if (isSupportCache && cacheCount == null
                                    || (cacheCount.trim().matches("\\d+")
                                            && Integer.parseInt(cacheCount.trim()) <= 0)) {
                                return "NOCACHE";
                            } else {
                                return cacheCount;
                            }
                        case 7:
                            return serialInfo.isCyclic() ? "YES" : "NO";
                    }
                }
            }

            return null;
        }
    }

    /**
     * Serial table content provider
     *
     * @author fulei
     */
    public class SerialsDetailTableViewerContentProvider implements IStructuredContentProvider {
        @SuppressWarnings("unchecked")
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof List) {
                List<SerialInfo> list = (List<SerialInfo>) inputElement;
                SerialInfo[] nodeArr = new SerialInfo[list.size()];
                return list.toArray(nodeArr);
            }

            return new Object[] {};
        }

        public void dispose() {}

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
    }

    /**
     * Column Viewer Sorter
     *
     * @author fulei
     * @version 1.0 - 2013-1-9 fulei
     */
    class ColumnViewerSorter extends ViewerSorter {
        public int compare(Viewer viewer, Object e1, Object e2) {
            SerialInfo s1 = (SerialInfo) e1;
            SerialInfo s2 = (SerialInfo) e2;
            return s1.getOwner().compareTo(s2.getOwner());
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

    public CubridDatabase getDatabase() {
        return database;
    }

    private String serialMinValue() {
        if (database != null && database.getDatabaseInfo().isSupportUserSchema()) {
            return SERIAL_MIN_AFTER_120;
        }
        return SERIAL_MIN;
    }

    private String serialMaxValue() {
        if (database != null && database.getDatabaseInfo().isSupportUserSchema()) {
            return SERIAL_MAX_AFTER_120;
        }
        return SERIAL_MAX;
    }

    private boolean isSupportUserSchema() {
        return database.getDatabaseInfo().isSupportUserSchema();
    }
}
