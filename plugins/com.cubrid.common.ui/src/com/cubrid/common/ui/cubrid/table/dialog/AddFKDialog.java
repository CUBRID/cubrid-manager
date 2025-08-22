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

import com.cubrid.common.core.common.model.Constraint;
import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.ConstraintNamingUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import com.cubrid.cubridmanager.core.cubrid.table.model.SuperClassUtil;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetTablesTask;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * The dialog of add fk
 *
 * @author pangqiren 2009-6-4
 */
public class AddFKDialog extends CMTitleAreaDialog {

    private Text newColumnNameText;
    private Combo foreignTableCombo;
    private Table pkForeignTable;
    private Table fkTable;
    private final CubridDatabase database;
    private final SchemaInfo schema;
    private List<String> tableList;
    private Constraint retFK;
    private CCombo oldCombo;
    private Button onCacheObjectButton;
    private Text fkNameText;
    private Button[] updateBTNs;
    private Button[] deleteBTNs;
    private SchemaInfo refSchema;
    private HashMap<Button, String> buttonMap;
    private Constraint editedFK;
    private String defaultTableName;
    private boolean canChangeTable;
    private Map<String, SchemaInfo> constraintTablesMap;

    private static int fkTableColCount = 3;

    /**
     * The constructor
     *
     * @param parentShell
     * @param newSchema
     * @param database
     * @param editedFK
     */
    public AddFKDialog(
            Shell parentShell, CubridDatabase database, SchemaInfo newSchema, Constraint editedFK) {
        this(parentShell, database, newSchema, editedFK, true, null);
    }

    /**
     * The constructor
     *
     * @param parentShell
     * @param newSchema
     * @param database
     * @param editedFK
     */
    public AddFKDialog(
            Shell parentShell,
            CubridDatabase database,
            SchemaInfo newSchema,
            Constraint editedFK,
            boolean canChangeTable,
            Map<String, SchemaInfo> constraintTablesMap) {
        super(parentShell);
        this.database = database;
        this.schema = newSchema;
        this.editedFK = editedFK;
        this.canChangeTable = canChangeTable;
        this.constraintTablesMap = constraintTablesMap;
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     * @param parent The parent composite to contain the dialog area
     * @return the dialog area control
     */
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

        createComposite(composite);

        init();
        if (editedFK == null) {
            setTitle(Messages.msgTitleAddFK);
            setMessage(Messages.msgAddFK);
        } else {
            setTitle(Messages.msgTitleEditFK);
            setMessage(Messages.msgEditFK);
        }
        return parentComp;
    }

    /** @see com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog#constrainShellSize() */
    protected void constrainShellSize() {
        super.constrainShellSize();
        if (editedFK == null) {
            getShell().setText(Messages.msgTitleAddFK);
        } else {
            getShell().setText(Messages.msgTitleEditFK);
        }
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     * @param parent the button bar composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.btnOK, true);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.btnCancel, false);
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     * @param buttonId the id of the button that was pressed (see <code>IDialogConstants.*_ID</code>
     *     constants)
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            if (checkFields() == 1) {
                return;
            }
            retFK = new Constraint(true);
            retFK.setType(Constraint.ConstraintType.FOREIGNKEY.getText());

            int itemCount = fkTable.getItemCount();
            Map<String, String> map = new HashMap<String, String>();
            for (int i = 0; i < itemCount; i++) {
                TableItem item = fkTable.getItem(i);
                String pkColumn = item.getText(fkTableColCount - 1).trim();
                if (!("").equals(pkColumn)) { // $NON-NLS-1$
                    map.put(pkColumn, item.getText(0));
                }
            }
            itemCount = pkForeignTable.getItemCount();
            for (int i = 0; i < itemCount; i++) {
                TableItem item = pkForeignTable.getItem(i);
                String pkColumn = item.getText(0).trim();
                assert (StringUtil.isNotEmpty(pkColumn));
                String refColumn = map.get(pkColumn);
                assert (refColumn != null);
                retFK.addAttribute(refColumn);
            }

            String fkName = fkNameText.getText().trim();
            if (StringUtil.isEmpty(fkName)) { // $NON-NLS-1$
                fkName =
                        ConstraintNamingUtil.getFKName(
                                schema.getUniqueName(), retFK.getAttributes());
            }
            retFK.setName(fkName);

            String foreignTable = foreignTableCombo.getText();
            retFK.addRule("REFERENCES " + foreignTable); // $NON-NLS-1$

            List<Constraint> fkList = schema.getFKConstraints();
            if (editedFK != null) {
                fkList.remove(editedFK);
            }
            for (Constraint fk : fkList) {
                if (fk.getAttributes().equals(retFK.getAttributes())) {
                    setErrorMessage(Messages.errColumnExistInFK);
                    return;
                }
            }

            for (int i = 0; i < deleteBTNs.length; i++) {
                if (deleteBTNs[i].getSelection()) {
                    retFK.addRule("ON DELETE " + buttonMap.get(deleteBTNs[i])); // $NON-NLS-1$
                    break;
                }
            }

            for (int i = 0; i < updateBTNs.length; i++) {
                if (updateBTNs[i].getSelection()) {
                    retFK.addRule("ON UPDATE " + buttonMap.get(updateBTNs[i])); // $NON-NLS-1$
                    break;
                }
            }
            if (onCacheObjectButton.getSelection()) {
                retFK.addRule(
                        "ON CACHE OBJECT " + newColumnNameText.getText().trim()); // $NON-NLS-1$
            }
        }
        super.buttonPressed(buttonId);
    }

    /**
     * Create composite
     *
     * @param parent Composite
     */
    private void createComposite(Composite parent) {
        Composite compositeFK = new Composite(parent, SWT.NONE);
        {
            compositeFK.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            final GridLayout gridLayout = new GridLayout();
            gridLayout.numColumns = 2;
            compositeFK.setLayout(gridLayout);
        }

        Label fkNameLabel = new Label(compositeFK, SWT.NONE);
        fkNameLabel.setLayoutData(new GridData(170, SWT.DEFAULT));
        fkNameLabel.setText(Messages.lblFKName);
        fkNameText = new Text(compositeFK, SWT.BORDER);
        fkNameText.setTextLimit(ValidateUtil.MAX_SCHEMA_NAME_LENGTH);
        fkNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Label targetLabel = new Label(compositeFK, SWT.NONE);
        targetLabel.setText(Messages.lblFTableName);
        foreignTableCombo = new Combo(compositeFK, SWT.READ_ONLY);
        foreignTableCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        foreignTableCombo.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(org.eclipse.swt.events.SelectionEvent event) {
                        getPKTableData();
                    }
                });

        Label fkDescLabel = new Label(compositeFK, SWT.NONE);
        fkDescLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        fkDescLabel.setText(Messages.lblFTablePK);

        pkForeignTable = new Table(compositeFK, SWT.FULL_SELECTION | SWT.BORDER | SWT.SINGLE);
        final GridData gdPkForeignTable = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        gdPkForeignTable.heightHint = 70;
        pkForeignTable.setLayoutData(gdPkForeignTable);
        pkForeignTable.setHeaderVisible(true);
        pkForeignTable.setLinesVisible(true);
        CommonUITool.hackForYosemite(pkForeignTable);

        TableColumn tblcol = new TableColumn(pkForeignTable, SWT.LEFT);
        tblcol.setWidth(120);
        tblcol.setText(Messages.colName);
        tblcol = new TableColumn(pkForeignTable, SWT.LEFT);
        tblcol.setWidth(221);
        tblcol.setText(Messages.colDataType);

        Label descLable = new Label(compositeFK, SWT.NONE);
        descLable.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        descLable.setText(Messages.lblSelectColumns);
        fkTable = new Table(compositeFK, SWT.FULL_SELECTION | SWT.BORDER | SWT.SINGLE);
        final GridData gdFkTable = new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1);
        gdFkTable.heightHint = 100;
        fkTable.setLayoutData(gdFkTable);
        fkTable.setHeaderVisible(true);
        fkTable.setLinesVisible(true);
        CommonUITool.hackForYosemite(fkTable);
        fkTable.addSelectionListener(
                new org.eclipse.swt.events.SelectionAdapter() {
                    public void widgetSelected(org.eclipse.swt.events.SelectionEvent event) {
                        final TableItem item = (TableItem) event.item;
                        setErrorMessage(null);
                        String oldpkName = item.getText(fkTableColCount - 1);
                        if (oldCombo != null) {
                            oldCombo.dispose();
                        }
                        final CCombo combo = new CCombo(fkTable, SWT.NONE);
                        TableEditor editor = new TableEditor(fkTable);
                        combo.setEditable(false);
                        combo.setText(oldpkName);

                        combo.add(""); // $NON-NLS-1$
                        if (pkForeignTable.getItemCount() != 0) {
                            for (int i = 0, n = pkForeignTable.getItemCount(); i < n; i++) {
                                combo.add(pkForeignTable.getItem(i).getText(0));
                            }
                        }

                        editor.grabHorizontal = true;
                        editor.setEditor(combo, item, fkTableColCount - 1);
                        combo.addSelectionListener(
                                new org.eclipse.swt.events.SelectionAdapter() {
                                    public void widgetSelected(
                                            org.eclipse.swt.events.SelectionEvent event) {
                                        String pkColumn = combo.getText();
                                        if (("").equals(pkColumn)) {
                                            item.setText(fkTableColCount - 1, pkColumn);
                                            return;
                                        }
                                        String dataType =
                                                DataType.getShownType(
                                                        refSchema
                                                                .getDBAttributeByName(
                                                                        pkColumn, false)
                                                                .getType());

                                        if (dataType.equals(item.getText(1))) {
                                            item.setText(fkTableColCount - 1, pkColumn);
                                        } else {
                                            setErrorMessage(Messages.errDataTypeInCompatible);
                                            combo.setFocus();
                                        }
                                    }
                                });
                        oldCombo = combo;
                    }
                });
        tblcol = new TableColumn(fkTable, SWT.LEFT);
        tblcol.setWidth(122);
        tblcol.setText(Messages.colName);
        tblcol = new TableColumn(fkTable, SWT.LEFT);
        tblcol.setWidth(220);
        tblcol.setText(Messages.colDataType);
        TableColumn tblcombocol = new TableColumn(fkTable, SWT.LEFT);
        tblcombocol.setWidth(171);
        tblcombocol.setText(Messages.colRefColumn);

        final Composite composite = new Composite(compositeFK, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        GridLayout gridLayout = new GridLayout();
        gridLayout.makeColumnsEqualWidth = true;
        gridLayout.numColumns = 2;
        composite.setLayout(gridLayout);

        final Group onUpdateGroup = new Group(composite, SWT.NONE);
        onUpdateGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        onUpdateGroup.setText(Messages.grpOnUpdate);
        onUpdateGroup.setLayout(new GridLayout());

        boolean isSupportSetNull = CompatibleUtil.isSupportSetNull(database.getDatabaseInfo());
        // database.getServer().getServerInfo().compareVersionKey("8.3.0") >= 0;
        if (isSupportSetNull) {
            updateBTNs = new Button[4];
        } else {
            updateBTNs = new Button[3];
        }
        buttonMap = new HashMap<Button, String>();

        updateBTNs[0] = new Button(onUpdateGroup, SWT.RADIO);
        updateBTNs[0].setLayoutData(new GridData(100, SWT.DEFAULT));
        updateBTNs[0].setText("CASCADE"); // $NON-NLS-1$
        updateBTNs[0].setEnabled(false);

        updateBTNs[1] = new Button(onUpdateGroup, SWT.RADIO);
        updateBTNs[1].setSelection(true);
        updateBTNs[1].setText("RESTRICT"); // $NON-NLS-1$

        updateBTNs[2] = new Button(onUpdateGroup, SWT.RADIO);
        updateBTNs[2].setText("NO ACTION"); // $NON-NLS-1$
        if (isSupportSetNull) {
            updateBTNs[3] = new Button(onUpdateGroup, SWT.RADIO);
            updateBTNs[3].setText("SET NULL"); // $NON-NLS-1$
        }

        buttonMap.put(updateBTNs[0], "CASCADE"); // $NON-NLS-1$
        buttonMap.put(updateBTNs[1], "RESTRICT"); // $NON-NLS-1$
        buttonMap.put(updateBTNs[2], "NO ACTION"); // $NON-NLS-1$
        if (isSupportSetNull) {
            buttonMap.put(updateBTNs[3], "SET NULL"); // $NON-NLS-1$
        }

        final Group onDeleteGroup = new Group(composite, SWT.NONE);
        final GridData gdOnDeleteGroup = new GridData(SWT.FILL, SWT.CENTER, false, false);
        onDeleteGroup.setLayoutData(gdOnDeleteGroup);
        onDeleteGroup.setLayout(new GridLayout());
        onDeleteGroup.setText(Messages.grpOnDelete);

        if (isSupportSetNull) {
            deleteBTNs = new Button[4];
        } else {
            deleteBTNs = new Button[3];
        }
        deleteBTNs[0] = new Button(onDeleteGroup, SWT.RADIO);
        deleteBTNs[0].setLayoutData(new GridData(100, SWT.DEFAULT));
        deleteBTNs[0].setText("CASCADE"); // $NON-NLS-1$

        deleteBTNs[1] = new Button(onDeleteGroup, SWT.RADIO);
        deleteBTNs[1].setSelection(true);
        deleteBTNs[1].setText("RESTRICT"); // $NON-NLS-1$

        deleteBTNs[2] = new Button(onDeleteGroup, SWT.RADIO);
        deleteBTNs[2].setText("NO ACTION"); // $NON-NLS-1$
        if (isSupportSetNull) {
            deleteBTNs[3] = new Button(onDeleteGroup, SWT.RADIO);
            deleteBTNs[3].setText("SET NULL"); // $NON-NLS-1$
        }

        buttonMap.put(deleteBTNs[0], "CASCADE"); // $NON-NLS-1$
        buttonMap.put(deleteBTNs[1], "RESTRICT"); // $NON-NLS-1$
        buttonMap.put(deleteBTNs[2], "NO ACTION"); // $NON-NLS-1$
        if (isSupportSetNull) {
            buttonMap.put(deleteBTNs[3], "SET NULL"); // $NON-NLS-1$
        }

        final Group group = new Group(composite, SWT.NONE);
        final GridData gdGroup = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
        group.setLayoutData(gdGroup);
        gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        group.setLayout(gridLayout);

        onCacheObjectButton = new Button(group, SWT.CHECK);

        onCacheObjectButton.setLayoutData(new GridData());
        onCacheObjectButton.setText(Messages.btnOnCacheObject);

        final Label cacheObjectColumnLabel = new Label(group, SWT.NONE);
        {
            final GridData gdCacheObjectColumnLabel = new GridData();
            gdCacheObjectColumnLabel.horizontalIndent = 20;
            cacheObjectColumnLabel.setLayoutData(gdCacheObjectColumnLabel);
            cacheObjectColumnLabel.setText(Messages.lblCacheColumnName);
        }
        newColumnNameText = new Text(group, SWT.BORDER);
        newColumnNameText.setEnabled(false);
        final GridData gdNewColumnNameText = new GridData(SWT.FILL, SWT.CENTER, true, false);
        newColumnNameText.setLayoutData(gdNewColumnNameText);

        onCacheObjectButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        if (onCacheObjectButton.getSelection()) {
                            newColumnNameText.setEnabled(true);
                        } else {
                            newColumnNameText.setText(""); // $NON-NLS-1$
                            newColumnNameText.setEnabled(false);
                        }
                    }
                });
    }

    private SchemaInfo getSchemaInfo(String tableName) {
        SchemaInfo refSchema;
        if (constraintTablesMap != null) {
            refSchema = constraintTablesMap.get(tableName);
        } else {
            refSchema = database.getDatabaseInfo().getSchemaInfo(tableName);
        }

        return refSchema;
    }

    /**
     * Get supper table. If the table come from ER, return empty.
     *
     * @param refSchema
     * @return
     */
    private List<SchemaInfo> getRefedSupper(SchemaInfo refSchema) {
        if (this.constraintTablesMap != null) {
            return new ArrayList<SchemaInfo>(0);
        }
        return SuperClassUtil.getSuperClasses(database.getDatabaseInfo(), refSchema);
    }

    /** initializes some values. */
    private void init() {
        List<String> list = getTableList();
        // allow a table has one or more FK referencing to another table
        // List<String> foreignTablelist = schema.getForeignTables();
        for (String table : list) {
            // if (!foreignTablelist.contains(table)) {
            foreignTableCombo.add(table);
            // }
        }
        if (editedFK == null) {
            if (defaultTableName != null) {
                foreignTableCombo.setText(defaultTableName);
            } else {
                foreignTableCombo.select(0);
            }
            getPKTableData();
            List<DBAttribute> attrList = schema.getLocalAttributes();
            for (int i = 0, n = attrList.size(); i < n; i++) {
                DBAttribute attr = attrList.get(i);
                TableItem item = new TableItem(fkTable, SWT.NONE);
                item.setText(0, attr.getName());
                item.setText(1, DataType.getShownType(attr.getType()));
                item.setText(2, ""); // $NON-NLS-1$
            }
        } else {
            fkNameText.setText(editedFK.getName());

            String refTable = "";
            String delRule = "RESTRICT";
            String updateRule = "RESTRICT";
            String cacheRule = "";
            List<String> rules = editedFK.getRules();
            for (String rule : rules) {
                String refStr = "REFERENCES ";
                String delStr = "ON DELETE ";
                String updStr = "ON UPDATE ";
                String cacheStr = "ON CACHE OBJECT ";

                if (rule.startsWith(refStr)) {
                    refTable = rule.replace(refStr, "");
                } else if (rule.startsWith(delStr)) {
                    delRule = rule.replace(delStr, "");
                } else if (rule.startsWith(updStr)) {
                    updateRule = rule.replace(updStr, "");
                } else if (rule.startsWith(cacheStr)) {
                    cacheRule = rule.replace(cacheStr, "");
                }
            }
            List<String> refPKAttrs = new ArrayList<String>();
            SchemaInfo refSchema = getSchemaInfo(refTable);
            List<SchemaInfo> refSupers = getRefedSupper(refSchema);
            Constraint refPK = refSchema.getPK(refSupers);
            if (refPK != null) {
                refPKAttrs = refPK.getAttributes();
            }
            // Referenced foreign table name
            foreignTableCombo.setText(refTable);
            getPKTableData();

            // Foreign table columns
            List<String> fkColumns = editedFK.getAttributes();
            List<DBAttribute> attrList = schema.getLocalAttributes();
            for (int i = 0, n = attrList.size(); i < n; i++) {
                DBAttribute attr = attrList.get(i);
                TableItem item = new TableItem(fkTable, SWT.NONE);
                item.setText(0, attr.getName());
                item.setText(1, DataType.getShownType(attr.getType()));
                if (fkColumns.contains(attr.getName())) {
                    int index = getPKMatchIndexInFK(attr.getName());
                    if (index == -1) {
                        index = 0;
                    }
                    item.setText(2, refPKAttrs.isEmpty() ? "" : refPKAttrs.get(index));
                } else {
                    item.setText(2, "");
                }
            }

            for (Button btn : updateBTNs) {
                String rule = buttonMap.get(btn);
                if (updateRule.equalsIgnoreCase(rule)) {
                    btn.setSelection(true);
                } else {
                    btn.setSelection(false);
                }
            }

            for (Button btn : deleteBTNs) {
                String rule = buttonMap.get(btn);
                if (delRule.equalsIgnoreCase(rule)) {
                    btn.setSelection(true);
                } else {
                    btn.setSelection(false);
                }
            }

            if (cacheRule != null && cacheRule.trim().length() > 0) {
                onCacheObjectButton.setSelection(true);
                newColumnNameText.setEnabled(true);
                newColumnNameText.setText(cacheRule);
            }
        }

        foreignTableCombo.setEnabled(canChangeTable);
    }

    /**
     * Get the pk column index comparing with the column in the fk
     *
     * @param refColName
     * @return
     */
    private int getPKMatchIndexInFK(String refColName) {
        List<String> refColList = editedFK.getAttributes();
        if (refColList == null) {
            return -1;
        }
        for (int i = 0; i < refColList.size(); i++) {
            if (refColList.get(i).equals(refColName)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * get table list.
     *
     * @return tableList
     */
    private List<String> getTableList() {

        if (null == tableList) {
            if (constraintTablesMap != null) {
                Set<String> set = constraintTablesMap.keySet();
                tableList = new LinkedList<String>();
                for (String name : set) {
                    tableList.add(name);
                }
            } else {
                CubridDatabase db = database;
                DatabaseInfo dbInfo = db.getDatabaseInfo();
                GetTablesTask task = new GetTablesTask(dbInfo);
                tableList = task.getUserTablesNotContainSubPartitionTable();
            }
        }
        return tableList;
    }

    /** @return the defaultTableName */
    public String getDefaultTableName() {
        return defaultTableName;
    }

    /** @param defaultTableName the defaultTableName to set */
    public void setDefaultTableName(String defaultTableName) {
        this.defaultTableName = defaultTableName;
    }

    /** @return the canChangeTable */
    public boolean isCanChangeTable() {
        return canChangeTable;
    }

    /** @param canChangeTable the canChangeTable to set */
    public void setCanChangeTable(boolean canChangeTable) {
        this.canChangeTable = canChangeTable;
    }

    /**
     * check the fields
     *
     * @return int
     */
    public int checkFields() {
        int ret = 0;
        setErrorMessage(null);
        int pkItemCount = pkForeignTable.getItemCount();
        if (pkItemCount == 0) {
            setErrorMessage(Messages.errSelectTableWithPK);
            return 1;
        }
        int fkItemCount = fkTable.getItemCount();
        if (fkItemCount == 0) {
            setErrorMessage(Messages.errNoColumnInTable + schema.getUniqueName());
            return 1;
        }
        Map<String, String> pk2fkMap = new HashMap<String, String>();
        Map<String, String> fk2pkMap = new HashMap<String, String>();
        for (int i = 0; i < fkItemCount; i++) {
            TableItem item = fkTable.getItem(i);
            if (StringUtil.isNotEmpty(item.getText(fkTableColCount - 1))) { // $NON-NLS-1$
                pk2fkMap.put(item.getText(fkTableColCount - 1), item.getText(0));
                fk2pkMap.put(item.getText(0), item.getText(fkTableColCount - 1));
            }
        }
        if (pk2fkMap.size() < pkItemCount) {
            int diff = pkItemCount - pk2fkMap.size();
            if (diff == 1) {
                setErrorMessage(Messages.errOneColumnNotSet);
            } else {
                String msg = Messages.bind(Messages.errMultColumnsNotSet, diff);
                setErrorMessage(msg);
            }
            return 1;
        }
        if (fk2pkMap.size() > pkItemCount) {
            setErrorMessage(Messages.errSelectMoreColumn);
            return 1;
        }
        if (onCacheObjectButton.getSelection() && ("").equals(newColumnNameText.getText().trim())) {
            setErrorMessage(Messages.errNoNameForCacheColumn);
            return 1;
        }
        return ret;
    }

    /** Get PK Table Data */
    private void getPKTableData() {
        pkForeignTable.removeAll();

        if (oldCombo != null) {
            oldCombo.dispose();
        }

        String refTable = foreignTableCombo.getText();
        refSchema = getSchemaInfo(refTable);
        if (refSchema == null) {
            return;
        }
        List<SchemaInfo> supers = getRefedSupper(refSchema);
        Constraint pk = refSchema.getPK(supers);
        if (pk != null) {
            List<String> pkAttrs = pk.getAttributes();
            for (String attr : pkAttrs) {
                DBAttribute da = (DBAttribute) refSchema.getDBAttributeByName(attr, false);
                if (da == null) {
                    continue;
                }
                TableItem item = new TableItem(pkForeignTable, SWT.NONE);
                item.setText(0, da.getName());
                item.setText(1, DataType.getShownType(da.getType()));
            }
        }

        if (fkTable.getItemCount() > 0) {
            TableItem items[] = fkTable.getItems();
            for (int i = 0, n = items.length; i < n; i++) {
                items[i].setText(fkTableColCount - 1, ""); // $NON-NLS-1$
            }
        }
    }

    public Constraint getRetFK() {
        return retFK;
    }
}
