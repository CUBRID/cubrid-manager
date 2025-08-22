package com.cubrid.common.ui.query.control;

import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class DatabaseSelectionDialog extends Dialog implements SelectionListener {

    private final String KEY_ID = "ID";
    private final String KEY_VALUE = "VALUE";

    public DatabaseSelectionDialog(Shell parentShell) {
        this(parentShell, null);
    }

    private CubridDatabase[] lastSelectedDbs;

    public DatabaseSelectionDialog(Shell parentShell, CubridDatabase[] databases) {
        super(parentShell);
        this.lastSelectedDbs = databases;
    }

    private Tree dbTree;
    private Button btnUnExpandAll;
    private Button btnExpandAll;
    private Button btnUnSelectAll;
    private Button btnSelectAll;
    private Composite buttonComp;

    private static final String KEY_DATABASE = "DATA";

    private enum TYPE {
        SELECT,
        EXPAND
    };

    @Override
    protected int getShellStyle() {
        return super.getShellStyle() | SWT.RESIZE | SWT.MAX;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);
        Composite newComp = new Composite(parentComp, SWT.NONE);

        GridLayout dialogShellLayout = new GridLayout();
        dialogShellLayout.makeColumnsEqualWidth = true;
        newComp.setLayout(dialogShellLayout);

        GridData compData = new GridData();
        compData.grabExcessHorizontalSpace = true;
        compData.grabExcessVerticalSpace = true;
        compData.verticalAlignment = GridData.FILL;
        compData.horizontalAlignment = GridData.FILL;
        newComp.setLayoutData(compData);

        try {

            {
                GridData dbTreeLData = new GridData();
                dbTreeLData.grabExcessHorizontalSpace = true;
                dbTreeLData.grabExcessVerticalSpace = true;
                dbTreeLData.verticalAlignment = GridData.FILL;
                dbTreeLData.horizontalAlignment = GridData.FILL;
                dbTree =
                        new Tree(
                                newComp,
                                SWT.BORDER
                                        | SWT.LINE_SOLID
                                        | SWT.MULTI
                                        | SWT.CHECK
                                        | SWT.H_SCROLL
                                        | SWT.V_SCROLL);
                dbTree.setLayoutData(dbTreeLData);
            }
            {
                buttonComp = new Composite(newComp, SWT.NONE);
                RowLayout buttonCompLayout = new RowLayout(org.eclipse.swt.SWT.HORIZONTAL);
                GridData buttonCompLData = new GridData();
                buttonComp.setLayoutData(buttonCompLData);
                buttonComp.setLayout(buttonCompLayout);
                {
                    btnSelectAll = new Button(buttonComp, SWT.PUSH | SWT.CENTER);
                    RowData btnSelectAllLData = new RowData();
                    btnSelectAll.setLayoutData(btnSelectAllLData);
                    btnSelectAll.setText(Messages.lblSelectAll);
                    btnSelectAll.addSelectionListener(this);
                    btnSelectAll.setData(KEY_ID, TYPE.SELECT);
                    btnSelectAll.setData(KEY_VALUE, true);
                    btnSelectAll.setVisible(false);
                }
                {
                    btnUnSelectAll = new Button(buttonComp, SWT.PUSH | SWT.CENTER);
                    RowData btnUnSelectAllLData = new RowData();
                    btnUnSelectAll.setLayoutData(btnUnSelectAllLData);
                    btnUnSelectAll.setText(Messages.lblDeSelectAll);
                    //					btnUnSelectAll.setImage(CommonUIPlugin.getImage("icons/unchecked.gif"));
                    btnUnSelectAll.addSelectionListener(this);
                    btnUnSelectAll.setData(KEY_ID, TYPE.SELECT);
                    btnUnSelectAll.setData(KEY_VALUE, false);
                    btnUnSelectAll.setVisible(false);
                }
                {
                    btnExpandAll = new Button(buttonComp, SWT.PUSH | SWT.CENTER);
                    RowData btnExpandAllLData = new RowData();
                    btnExpandAll.setLayoutData(btnExpandAllLData);
                    btnExpandAll.setText(Messages.lblExpandAll);
                    btnExpandAll.addSelectionListener(this);
                    btnExpandAll.setData(KEY_ID, TYPE.EXPAND);
                    btnExpandAll.setData(KEY_VALUE, true);
                    btnExpandAll.setVisible(false);
                }
                {
                    btnUnExpandAll = new Button(buttonComp, SWT.PUSH | SWT.CENTER);
                    RowData btnUnExpandAllLData = new RowData();
                    btnUnExpandAll.setLayoutData(btnUnExpandAllLData);
                    btnUnExpandAll.setText(Messages.lblUnExpandAll);
                    //
                    //	btnUnExpandAll.setImage(CommonUIPlugin.getImage("icons/action/collapseall.gif"));
                    btnUnExpandAll.addSelectionListener(this);
                    btnUnExpandAll.setData(KEY_ID, TYPE.EXPAND);
                    btnUnExpandAll.setData(KEY_VALUE, false);
                    btnUnExpandAll.setVisible(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbTree.addSelectionListener(
                new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        TreeItem item = (TreeItem) e.item;
                        boolean checked = item.getChecked();
                        checkItems(item.getItems(), checked);
                        if (item.getParentItem() != null) {
                            if (checked) item.getParentItem().setChecked(checked);
                            checkParentChecked(item.getParentItem(), checked);
                        }
                    }

                    private void checkItems(TreeItem[] items, boolean value) {
                        if (items == null || items.length == 0) return;
                        for (TreeItem item : items) {
                            item.setChecked(value);
                        }
                    }

                    private void checkParentChecked(TreeItem pitem, boolean value) {
                        TreeItem[] items = pitem.getItems();
                        for (TreeItem item : items) {
                            if (item.getChecked() == value) {
                                continue;
                            } else {
                                return;
                            }
                        }
                        pitem.setChecked(value);
                    }
                });
        initTreeData();
        if (this.lastSelectedDbs != null) initSelection(this.lastSelectedDbs);
        return parentComp;
    }

    List<CubridDatabase> database = new ArrayList<CubridDatabase>();

    @Override
    protected void okPressed() {
        for (TreeItem pItem : dbTree.getItems()) {
            if (pItem.getItemCount() == 0) {
                continue;
            }

            for (TreeItem citem : pItem.getItems()) {
                if (citem.getChecked()) database.add((CubridDatabase) citem.getData(KEY_DATABASE));
            }
        }

        super.okPressed();
    }

    private void initTreeData() {
        if (initDatabase == null) return;
        Map<String, TreeItem> treeMap = new HashMap<String, TreeItem>();
        for (CubridDatabase item : initDatabase) {
            CubridDatabase db = item;
            if (db.getServer() == null) continue;
            TreeItem pItem = treeMap.get(db.getServer().getServerName());
            if (pItem == null) {
                pItem = new TreeItem(dbTree, SWT.NONE);
                pItem.setImage(CommonUIPlugin.getImage("icons/navigator/database_group.png"));
                pItem.setText(db.getServer().getServerName());
                treeMap.put(db.getServer().getServerName(), pItem);
            }
            if (!db.isLogined()) return;
            TreeItem treeItem = new TreeItem(pItem, SWT.NONE);
            treeItem.setText(db.getLabel());
            treeItem.setImage(CommonUIPlugin.getImage(db.getStartAndLoginIconPath().trim()));
            treeItem.setData(KEY_DATABASE, db);
        }
    }

    public Tree getDbTree() {
        return dbTree;
    }

    public void setItems(MenuItem[] items) {
        List<CubridDatabase> database = new ArrayList<CubridDatabase>();
        for (MenuItem item : items) {
            DatabaseMenuItem dbItem = (DatabaseMenuItem) item;
            if (dbItem.getStyle() != SWT.CHECK) {
                continue;
            }
            database.add(dbItem.getDatabase());
        }
        initDatabase = database.toArray(new CubridDatabase[0]);
    }

    public CubridDatabase[] getSelectedDbItem() {
        return database.toArray(new CubridDatabase[0]);
    }

    public void widgetSelected(SelectionEvent e) {
        if (!(e.widget instanceof Button)) return;
        TYPE type = (TYPE) e.widget.getData(KEY_ID);
        switch (type) {
            case SELECT:
                boolean select = Boolean.parseBoolean(e.widget.getData(KEY_VALUE).toString());
                for (TreeItem item : dbTree.getItems()) {
                    item.setChecked(select);
                    checkItemChild(item, select);
                }
                break;
            case EXPAND:
                boolean expand = Boolean.parseBoolean(e.widget.getData(KEY_VALUE).toString());
                for (TreeItem item : dbTree.getItems()) {
                    item.setExpanded(expand);
                }
                break;
        }
    }

    private void checkItemChild(TreeItem item, boolean select) {
        if (item.getItems() == null || item.getItemCount() == 0) return;
        else {
            for (TreeItem cItem : item.getItems()) {
                cItem.setChecked(select);
                checkItemChild(cItem, select);
            }
        }
    }

    public void widgetDefaultSelected(SelectionEvent e) {}

    private CubridDatabase[] initDatabase = null;

    public void setItems(CubridDatabase[] databaseOnMenu) {
        initDatabase = databaseOnMenu;
    }

    public void initSelection(CubridDatabase[] lastSelected) {
        if (lastSelected.length == 0) return;

        if (dbTree.getItemCount() == 0) return;
        List<CubridDatabase> databases = new ArrayList<CubridDatabase>();

        for (CubridDatabase db : lastSelected) {
            databases.add(db);
        }

        for (TreeItem pItem : dbTree.getItems()) {
            selectChild(pItem, databases);
        }
    }

    private void selectChild(TreeItem item, List<CubridDatabase> databases) {
        if (item.getData(KEY_DATABASE) != null && databases.contains(item.getData(KEY_DATABASE))) {
            item.getParentItem().setChecked(true);
            item.setChecked(true);
            checkItemChild(item, true);
            databases.remove(item.getData(KEY_DATABASE));
        }

        if (item.getItemCount() == 0) return;

        for (TreeItem cItem : item.getItems()) {
            selectChild(cItem, databases);
        }
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.btnRunQuery, true);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.btnCancel, true);
    }

    @Override
    protected Point getInitialSize() {
        return new Point(400, 300);
    }
}
