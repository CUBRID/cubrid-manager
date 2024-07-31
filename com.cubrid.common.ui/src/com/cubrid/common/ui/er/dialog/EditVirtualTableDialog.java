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
package com.cubrid.common.ui.er.dialog;

import com.cubrid.common.core.common.model.Constraint;
import com.cubrid.common.core.common.model.Constraint.ConstraintType;
import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.ConstraintNamingUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.cubrid.table.TableEditorAdaptor;
import com.cubrid.common.ui.cubrid.table.action.EditTableAction;
import com.cubrid.common.ui.cubrid.table.control.FKTableViewerContentProvider;
import com.cubrid.common.ui.cubrid.table.control.FKTableViewerLabelProvider;
import com.cubrid.common.ui.cubrid.table.control.IndexTableViewerContentProvider;
import com.cubrid.common.ui.cubrid.table.control.IndexTableViewerLabelProvider;
import com.cubrid.common.ui.cubrid.table.dialog.AddFKDialog;
import com.cubrid.common.ui.cubrid.table.dialog.AddIndexDialog;
import com.cubrid.common.ui.cubrid.table.dialog.SetPKDialog;
import com.cubrid.common.ui.cubrid.table.editor.AttributeContentProvider;
import com.cubrid.common.ui.cubrid.table.editor.AutoIncrementCellEditor;
import com.cubrid.common.ui.cubrid.table.editor.DataTypeCellEditor;
import com.cubrid.common.ui.cubrid.table.editor.IAttributeColumn;
import com.cubrid.common.ui.cubrid.table.editor.TableEditorPart;
import com.cubrid.common.ui.er.editor.ERAttributeCellModifier;
import com.cubrid.common.ui.er.editor.ERAttributeLabelProvider;
import com.cubrid.common.ui.er.model.CubridTableParser;
import com.cubrid.common.ui.er.model.ERSchema;
import com.cubrid.common.ui.er.model.ERTable;
import com.cubrid.common.ui.er.model.ERTableColumn;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.progress.ITaskExecutorInterceptor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.common.ui.spi.util.WidgetUtil;
import com.cubrid.cubridmanager.core.cubrid.database.model.Collation;
import com.cubrid.cubridmanager.core.cubrid.table.model.DBAttrTypeFormatter;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import com.cubrid.cubridmanager.core.cubrid.table.model.FormatDataResult;
import com.cubrid.cubridmanager.core.cubrid.table.model.SchemaChangeLog;
import com.cubrid.cubridmanager.core.cubrid.table.model.SchemaChangeLog.SchemeInnerType;
import com.cubrid.cubridmanager.core.cubrid.table.model.SchemaChangeManager;
import com.cubrid.cubridmanager.core.cubrid.table.model.SuperClassUtil;
import com.cubrid.cubridmanager.core.cubrid.table.task.CheckSubClassTask;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolTip;
import org.slf4j.Logger;

/**
 * The dialog of creating or editing table including column editing and foreigner key editing who's
 * table is not in database.
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-8-13 created by Yu Guojia
 */
public class EditVirtualTableDialog extends CMTitleAreaDialog implements ITaskExecutorInterceptor {
    public static final String ID = TableEditorPart.class.getName();
    private static final Logger LOGGER = LogUtil.getLogger(EditVirtualTableDialog.class);
    private Table indexTable;
    private Table fkTable;
    private Text tableNameText;
    private String logicalName;
    private Text tableDescText;
    private CubridDatabase database;
    private TableColumn sharedColumn;
    private TableViewer columnTableView;
    private Table columnsTable;
    private AttributeContentProvider attrContentProvider;
    private ERAttributeLabelProvider attrLabelProvider;
    private boolean isNewTableFlag;
    private SchemaInfo oldSchemaInfo;
    private ERTable oldERTable;
    private ERTable newERTable;
    private SchemaChangeManager schemaChangeMgr;
    private ERSchema erSchema;
    private TableViewer fkTableView;
    private TableViewer indexTableView;
    private String jobName;
    private List<Collation> collationList;
    private Button upColumnBtn;
    private Button downColumnBtn;
    private Button deleteColumnBtn;
    private Boolean isHasSubClass;
    private boolean isSupportChange;
    private ToolTip toolTip;
    private ToolTip errorBaloon;
    private Button okBtn;
    private Combo collationCombo;
    private TabFolder tabFolder;
    private Composite tableNameComp;
    private String[] columnProperites = null;
    private List<ERTableColumn> tempERColumnList = new ArrayList<ERTableColumn>();
    private List<Constraint> originalConstraints = new ArrayList<Constraint>();
    private int showDefaultType = EditTableAction.MODE_TABLE_EDIT;
    private TableEditorAdaptor editorAdaptor;
    private boolean supportCharset = true;
    private boolean isPhysical;
    public static String LOGICAL_PHYSICAL_SPLIT = "::";

    private Set<String> deletedColumns = new HashSet<String>();
    private Map<String, String> modifiedColumns = new HashMap<String, String>(); // old <--> new
    private Set<String> addedColumns = new HashSet<String>();

    /**
     * The constructor
     *
     * @param parentShell
     * @param tv
     * @param database
     * @param isNewTable
     */
    public EditVirtualTableDialog(
            Shell parentShell, CubridDatabase database, boolean isNewTable, ERTable oldTable) {
        super(parentShell);
        this.database = database;
        this.isNewTableFlag = isNewTable;
        this.oldERTable = oldTable;
        this.oldSchemaInfo = oldTable.getSchemaInfo();
        this.erSchema = oldTable.getERSchema();
        isPhysical = erSchema.isPhysicModel();
        if (isNewTable) {
            SchemaInfo newSchemaInfo = new SchemaInfo();
            newSchemaInfo.setClassname(""); // $NON-NLS-1$
            newSchemaInfo.setUniqueName(""); // $NON-NLS-1$
            newSchemaInfo.setOwner(database.getUserName());
            newSchemaInfo.setDbname(database.getName());
            newSchemaInfo.setType(Messages.userSchema);
            newSchemaInfo.setVirtual(Messages.schemaTypeClass);
            newERTable = new ERTable(newSchemaInfo, erSchema);
        } else {
            this.newERTable = oldTable.clone();
        }

        if (supportCharset) {
            columnProperites =
                    new String[] {
                        IAttributeColumn.COL_EMPTY,
                        IAttributeColumn.COL_FLAG,
                        IAttributeColumn.COL_NAME,
                        IAttributeColumn.COL_DATATYPE,
                        IAttributeColumn.COL_DEFAULT,
                        IAttributeColumn.COL_AUTO_INCREMENT,
                        IAttributeColumn.COL_NOT_NULL,
                        IAttributeColumn.COL_PK,
                        IAttributeColumn.COL_UK,
                        IAttributeColumn.COL_SHARED,
                        IAttributeColumn.COL_COLLATION,
                        IAttributeColumn.COL_MEMO
                    };
        } else {
            columnProperites =
                    new String[] {
                        IAttributeColumn.COL_EMPTY,
                        IAttributeColumn.COL_FLAG,
                        IAttributeColumn.COL_NAME,
                        IAttributeColumn.COL_DATATYPE,
                        IAttributeColumn.COL_DEFAULT,
                        IAttributeColumn.COL_AUTO_INCREMENT,
                        IAttributeColumn.COL_NOT_NULL,
                        IAttributeColumn.COL_PK,
                        IAttributeColumn.COL_UK,
                        IAttributeColumn.COL_SHARED,
                        IAttributeColumn.COL_MEMO
                    };
        }

        if (database != null) {
            isSupportChange = CompatibleUtil.isSupportChangeColumn(database.getDatabaseInfo());
        }
        schemaChangeMgr = new SchemaChangeManager(database.getDatabaseInfo(), isNewTableFlag);
        editorAdaptor = new TableEditorAdaptor(this);
    }

    public void showToolTip(Rectangle rect, String title, String message) {
        CommonUITool.showToolTip(columnsTable, toolTip, rect, title, message);
    }

    public void hideToolTip() {
        CommonUITool.hideToolTip(toolTip);
    }

    public CubridDatabase getCubridDatabase() {
        return database;
    }

    /**
     * Create the dialog area
     *
     * @param parent The parent composite to contain the dialog area
     * @return the dialog area control
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);
        Composite composite = new Composite(parentComp, SWT.NONE);

        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 400;
        gd.widthHint = 710;
        composite.setLayoutData(gd);

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        composite.setLayout(layout);

        tabFolder = new TabFolder(composite, SWT.NONE);
        {
            final GridData tgd = new GridData(SWT.FILL, SWT.FILL, true, true);
            tgd.heightHint = 469;
            tgd.widthHint = 621;
            tabFolder.setLayoutData(tgd);
        }

        collationList = erSchema.getCollections();
        createGeneralTabItem(tabFolder);
        createFkIndexTabItem(tabFolder);
        toolTip = new ToolTip(columnsTable.getShell(), SWT.NONE);
        toolTip.setAutoHide(true);

        errorBaloon = new ToolTip(tabFolder.getShell(), SWT.NONE);
        errorBaloon.setAutoHide(true);

        init();

        if (isNewTableFlag) {
            tableNameText.setFocus();
        }

        addNewColumn();

        if (showDefaultType == EditTableAction.MODE_INDEX_EDIT
                || showDefaultType == EditTableAction.MODE_FK_EDIT) {
            tabFolder.setSelection(1);
        }

        return composite;
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

    protected void buttonPressed(int buttonId) {
        super.buttonPressed(buttonId);
    }

    protected void okPressed() {

        if (!checkValid()) {
            return;
        }

        String message =
                (oldSchemaInfo == null)
                        ? Messages.msgCreateTableConfirm
                        : Messages.msgAlterTableConfirm;
        if (!CommonUITool.openConfirmBox(message)) {
            return;
        }

        SchemaInfo newSchemaInfo = getNewSchemaInfo();
        newSchemaInfo.removeInvalidPKAndIndex(true);
        newERTable.setName(tableNameText.getText().trim(), isPhysical);
        if (erSchema.isPhysicModel()) {
            newSchemaInfo.setDescription(tableDescText.getText().trim());
        }

        // remove empty column
        List<ERTableColumn> columns = newERTable.getColumns();
        for (int i = columns.size() - 1; i >= 0; i--) {
            if (StringUtil.isEmpty(columns.get(i).getName())) {
                columns.remove(i);
            }
        }

        // check
        ERSchema tmpErSchema = new ERSchema(erSchema.getName() + "_tmp", erSchema.getInput());
        Map<String, SchemaInfo> schemaInfos = erSchema.getAllSchemaInfo();
        schemaInfos.put(newSchemaInfo.getUniqueName(), newSchemaInfo);

        CubridTableParser tableParser = new CubridTableParser(tmpErSchema);
        tableParser.buildERTables(schemaInfos.values(), -1, -1, false);
        Map<String, Exception> failedTables = tableParser.getFailedTables();
        Map<String, List<Constraint>> removedFKs = tableParser.getRemovedFKConstraints();
        if (failedTables.size() > 0) {
            Set<String> tables = failedTables.keySet();
            String tableName = tables.iterator().next();
            CommonUITool.openErrorBox(failedTables.get(tableName).getMessage());
            return;
        }
        if (removedFKs.size() > 0) {
            Set<String> tables = removedFKs.keySet();
            String tableName = tables.iterator().next();
            List<Constraint> constraints = removedFKs.get(tableName);
            CommonUITool.openErrorBox(
                    "Foreign relation is error. Please check the relation of "
                            + constraints.get(0).getName()
                            + ", in table of "
                            + tableName);
            return;
        }

        try {
            if (isTableNameChanged()
                    && erSchema.isContainsTable(newERTable.getName(isPhysical), isPhysical)) {
                throw new Exception(
                        Messages.bind(Messages.errExistTable, newERTable.getName(isPhysical)));
            }
            newERTable.checkValidate();
        } catch (Exception e) {
            CommonUITool.openErrorBox(e.getMessage());
            return;
        }

        super.okPressed();
    }

    /**
     * Where info is right, if not valid, pop-up dialog.
     *
     * @return if all are correct
     */
    private boolean checkValid() {
        if (!verifyTableName()) {
            return false;
        }

        if (columnsTable.getItemCount() == 0) {
            CommonUITool.openErrorBox(Messages.noAttributes);
            return false;
        }

        // auto increment
        SchemaInfo newSchemaInfo = getNewSchemaInfo();
        DBAttribute attr = newSchemaInfo.getAutoIncrementColumn();
        if (attr != null && StringUtil.isNotEmpty(attr.getDefault())) {
            CommonUITool.openErrorBox(Messages.errCanNotSetAIOnDefault);
            return false;
        }

        // default value
        List<DBAttribute> attrList = newSchemaInfo.getAttributes();
        for (DBAttribute attribute : attrList) {
            String defaultValue = attribute.getDefault();
            if (StringUtil.isEmpty(defaultValue)) {
                continue;
            }
            String revisedType = DataType.reviseDataType(attribute.getType());
            FormatDataResult formatDataResult =
                    DBAttrTypeFormatter.format(
                            revisedType,
                            defaultValue,
                            true,
                            database.getDatabaseInfo().getCharSet(),
                            false);
            if (!formatDataResult.isSuccess()) {
                CommonUITool.openErrorBox(
                        Messages.bind(
                                com.cubrid.common.ui.er.Messages.errMatchDefaultValue,
                                defaultValue,
                                attribute.getType()));
                return false;
            }
        }

        return true;
    }

    private boolean isTableNameChanged() {
        if (oldERTable != null && newERTable != null) {
            if (!StringUtil.isEqual(oldERTable.getShownName(), newERTable.getShownName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Parse the table and add new table to the er schema
     *
     * @param erSchema ERSchema
     */
    public void postEdittedTable(ERSchema erSchema) {

        ERTable tmpTable = (ERTable) oldERTable.clone();
        SchemaInfo newSchemaInfo = getNewSchemaInfo();

        // update table name
        if (!oldERTable.getName(isPhysical).equals(newERTable.getName(isPhysical))) {
            oldERTable.modifyNameAndFire(newERTable.getName(isPhysical), isPhysical);
        }

        // update table collation
        oldERTable.getSchemaInfo().setCollation(newSchemaInfo.getCollation());

        // deleted columns
        for (String oldColName : deletedColumns) {
            oldERTable.removeColumnAndFire(oldERTable.getColumn(oldColName, isPhysical));
        }

        // modified columns
        Set<String> oldNames = modifiedColumns.keySet();
        for (String oldColName : oldNames) {
            String newName = modifiedColumns.get(oldColName);
            ERTableColumn oldColumn = oldERTable.getColumn(oldColName, isPhysical);
            ERTableColumn newCol = newERTable.getColumn(newName, isPhysical);
            if (oldColumn == null) {
                continue;
            }
            ERTableColumn firedOldColumn = oldColumn.clone();
            if (newCol == null) {
                continue;
            }
            oldERTable.modifyColumn(
                    oldColName, isPhysical, newCol); // will modify the old column to new
            oldColumn.firePropertyChange(ERTableColumn.TEXT_CHANGE, firedOldColumn, newCol);
        }

        // added columns
        for (String addedColumn : addedColumns) {
            ERTableColumn newColumn = newERTable.getColumn(addedColumn, isPhysical);
            if (newColumn == null) {
                continue;
            }
            newColumn.setIsNew(false); // from new to old now
            if (oldERTable.getColumn(addedColumn, isPhysical) != null) {
                continue;
            }
            oldERTable.addColumnAndFire(newColumn);
        }

        // update pk
        Constraint newPK = newSchemaInfo.getPK();
        Constraint oldPK = oldERTable.getSchemaInfo().getPK();
        if (oldPK != null) {
            oldERTable
                    .getSchemaInfo()
                    .removeConstraintByName(oldPK.getName(), ConstraintType.PRIMARYKEY.getText());
        }
        if (newPK != null) {
            oldERTable.getSchemaInfo().addConstraint(newPK);
        }

        // update fk
        List<Constraint> oldFKs = oldERTable.getSchemaInfo().getFKConstraints();
        oldERTable.deleteAllFKShipsAndFire();
        for (Constraint fk : oldFKs) {
            oldERTable.getSchemaInfo().addConstraint(fk);
        }
        CubridTableParser tableParser = new CubridTableParser(erSchema);
        try {
            tableParser.buildReferenceShip(oldERTable, newSchemaInfo);
        } catch (Exception e) {
            CommonUITool.openErrorBox(e.getMessage());
            oldERTable = tmpTable;
            return;
        }

        syncLogicalNameAndPhysicalComment();
    }

    /**
     * If the logical name is not the same with physical comment, then synchronize the logical name
     * with physical comment on table and columns.
     *
     * @return void
     */
    private void syncLogicalNameAndPhysicalComment() {
        boolean isPhysical = erSchema.isPhysicModel();
        SchemaInfo newSchemaInfo = getNewSchemaInfo();
        if (isPhysical) {
            // sync table desc to table logical name
            String newTableDesc = newSchemaInfo.getDescription();
            String oldTableDesc = oldSchemaInfo.getDescription();
            oldSchemaInfo.setDescription(newTableDesc);
            if (StringUtil.isEmpty(newTableDesc)) {
                oldERTable.setName(newSchemaInfo.getUniqueName(), false);
            }
            if (StringUtil.isNotEmpty(newTableDesc)
                    && !StringUtil.isEqual(newTableDesc, oldTableDesc)) {
                oldERTable.setName(newTableDesc, false);
            }

            // sync column desc to column logical name
            List<DBAttribute> attrs = newSchemaInfo.getAttributes();
            for (DBAttribute attr : attrs) {
                String newColumnDesc = attr.getDescription();
                ERTableColumn column = oldERTable.getColumn(attr.getName());
                column.getAttr().setDescription(newColumnDesc);
                if (StringUtil.isEmpty(newColumnDesc)) {
                    column.setName(attr.getName(), false); // set physical name to logical name
                } else {
                    column.setName(newColumnDesc, false);
                }
            }
        } else {
            // sync table logical name to table desc
            String logicalName = oldERTable.getLogicalName();
            if (StringUtil.isEqual(logicalName, oldERTable.getName())) {
                oldERTable.setDescription("");
            } else {
                oldERTable.setDescription(logicalName);
            }

            // sync column logical name to column desc
            List<ERTableColumn> columns = oldERTable.getColumns();
            for (ERTableColumn column : columns) {
                String logicalColName = column.getName(false);
                String physicalColName = column.getName(true);
                if (StringUtil.isEqual(logicalColName, physicalColName)) {
                    column.setDescription("");
                } else {
                    column.setDescription(logicalColName);
                }
            }
        }
    }

    /**
     * Create general tab item
     *
     * @param tabFolder the object of TabFolder
     */
    private void createGeneralTabItem(final TabFolder tabFolder) {
        final TabItem generalTabItem = new TabItem(tabFolder, SWT.NONE);
        generalTabItem.setText(Messages.infoGeneralTab);

        final Composite compositeGenaral = new Composite(tabFolder, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        compositeGenaral.setLayout(gridLayout);
        generalTabItem.setControl(compositeGenaral);

        createTableInformationGroup(compositeGenaral);

        if (database == null) {
            return;
        }

        final Label columnsLabel = new Label(compositeGenaral, SWT.NONE);
        columnsLabel.setText(Messages.lblColumn);

        // create attribute table
        columnTableView = new TableViewer(compositeGenaral, SWT.FULL_SELECTION | SWT.BORDER);
        columnTableView.setUseHashlookup(true);
        columnTableView.setColumnProperties(columnProperites);

        columnsTable = columnTableView.getTable();

        final GridData gdColumnsTable = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdColumnsTable.heightHint = 189;
        columnsTable.setLayoutData(gdColumnsTable);
        columnsTable.setLinesVisible(true);
        columnsTable.setHeaderVisible(true);

        // The empty column
        final TableColumn emptyColumn = new TableColumn(columnsTable, SWT.NONE);
        emptyColumn.setWidth(0);
        // The flag column
        final TableColumn flagColumn = new TableColumn(columnsTable, SWT.RIGHT_TO_LEFT);
        flagColumn.setWidth(28);
        // NAME
        final TableColumn nameColumn = new TableColumn(columnsTable, SWT.NONE);
        nameColumn.setWidth(140);
        nameColumn.setText(Messages.tblColumnName);

        // DATATYPE
        final TableColumn dataTypeColumn = new TableColumn(columnsTable, SWT.NONE);
        dataTypeColumn.setWidth(120);
        dataTypeColumn.setText(Messages.tblColumnDataType);

        // DEFAULT
        final TableColumn defaultColumn = new TableColumn(columnsTable, SWT.NONE);
        defaultColumn.setWidth(98);
        defaultColumn.setText(Messages.tblColumnDefault);
        defaultColumn.setToolTipText(Messages.tblColumnDefaultHint);

        // AUTO INCREMENT
        final TableColumn autoIncrTableColumn = new TableColumn(columnsTable, SWT.NONE);
        autoIncrTableColumn.setAlignment(SWT.LEFT);
        autoIncrTableColumn.setWidth(100);
        autoIncrTableColumn.setText(Messages.tblColumnAutoIncr);
        autoIncrTableColumn.setToolTipText(Messages.tblColumnAutoIncrHint);

        // NOT NULL
        final TableColumn notNullColumn = new TableColumn(columnsTable, SWT.NONE);
        notNullColumn.setWidth(60);
        notNullColumn.setText(Messages.tblColumnNotNull);
        notNullColumn.setAlignment(SWT.LEFT);
        notNullColumn.setToolTipText(Messages.tblColumnNotNullHint);

        // PK
        final TableColumn pkColumn = new TableColumn(columnsTable, SWT.NONE);
        pkColumn.setAlignment(SWT.CENTER);
        pkColumn.setWidth(75);
        pkColumn.setText(Messages.tblColumnPK);

        // UK
        final TableColumn uniqueColumn = new TableColumn(columnsTable, SWT.NONE);
        uniqueColumn.setWidth(55);
        uniqueColumn.setText(Messages.tblColumnUnique);
        uniqueColumn.setAlignment(SWT.LEFT);
        uniqueColumn.setToolTipText(Messages.tblColumnUniqueHint);

        // SHARED
        sharedColumn = new TableColumn(columnsTable, SWT.NONE);
        sharedColumn.setWidth(50);
        sharedColumn.setResizable(false);
        sharedColumn.setText(Messages.tblColumnShared);
        sharedColumn.setAlignment(SWT.LEFT);
        sharedColumn.setToolTipText(Messages.tblColumnSharedHint);
        if (supportCharset) {
            final TableColumn collationColumn = new TableColumn(columnsTable, SWT.NONE);
            collationColumn.setWidth(120);
            collationColumn.setText(Messages.tblColumnColumnCollation);
            collationColumn.setAlignment(SWT.LEFT);
        }

        if (erSchema.isPhysicModel()) {
            final TableColumn descColumn = new TableColumn(columnsTable, SWT.NONE);
            descColumn.setWidth(180);
            descColumn.setText(Messages.tblColumnColumnDesc);
            descColumn.setAlignment(SWT.LEFT);
        }

        boolean canEdit = true;
        if (!this.isNewTableFlag() && !this.isSupportChange()) {
            canEdit = false;
        }

        attrContentProvider = new AttributeContentProvider();
        attrLabelProvider =
                new ERAttributeLabelProvider(
                        database.getDatabaseInfo(),
                        getNewSchemaInfo(),
                        canEdit,
                        editorAdaptor,
                        erSchema.isPhysicModel());
        columnTableView.setContentProvider(attrContentProvider);
        columnTableView.setLabelProvider(attrLabelProvider);

        CellEditor[] cellEditor = new CellEditor[columnProperites.length];
        {
            int index = 0;
            // Empty
            cellEditor[index++] = null;
            // Flag
            cellEditor[index++] = null;
            // NAME
            cellEditor[index++] = new TextCellEditor(columnsTable);
            // DATATYPE
            cellEditor[index++] =
                    new DataTypeCellEditor(
                            (Composite) columnsTable, listDataTypes(), editorAdaptor);
            // DEFAULT
            cellEditor[index++] = new TextCellEditor(columnsTable);
            // AUTO INCREMENT
            cellEditor[index++] = new AutoIncrementCellEditor(columnsTable, editorAdaptor);
            // NOT NULL
            cellEditor[index++] = new CheckboxCellEditor(columnsTable);
            // PK
            cellEditor[index++] = new CheckboxCellEditor(columnsTable);
            // UK
            cellEditor[index++] = new CheckboxCellEditor(columnsTable);
            // SHARD
            cellEditor[index++] = new CheckboxCellEditor(columnsTable);
            // COLLATION
            if (supportCharset) {
                cellEditor[index++] =
                        new ComboBoxCellEditor(columnsTable, getCollationArray(), SWT.READ_ONLY);
            }
            // MEMO
            cellEditor[index++] =
                    erSchema.isPhysicModel() ? new TextCellEditor(columnsTable) : null;
        }

        columnTableView.setCellEditors(cellEditor);
        ERAttributeCellModifier modifier =
                new ERAttributeCellModifier(editorAdaptor, erSchema.isPhysicModel());
        columnTableView.setCellModifier(modifier);

        loadColumnData();

        // Create button
        final Composite btnRowComposite = new Composite(compositeGenaral, SWT.NONE);
        btnRowComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        {
            GridLayout layout = new GridLayout();
            layout.numColumns = 2;
            layout.marginWidth = 5;
            btnRowComposite.setLayout(layout);
        }

        final Composite optComposite = new Composite(btnRowComposite, SWT.NONE);
        optComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        {
            GridLayout layout = new GridLayout();
            layout.numColumns = 1;
            layout.marginWidth = 0;
            optComposite.setLayout(layout);
        }

        final Composite btnComposite = new Composite(btnRowComposite, SWT.NONE);
        btnComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        {
            GridLayout layout = new GridLayout();
            layout.marginRight = 0;
            layout.numColumns = 6;
            layout.marginWidth = 0;
            btnComposite.setLayout(layout);
        }

        final Button setPkButton = new Button(btnComposite, SWT.NONE);
        setPkButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        setPkButton.setText(Messages.btnPK);
        setPkButton.addSelectionListener(new PkBtnListenerOnGeneTab(isNewTableFlag));

        final GridData gdUpDown = new GridData(60, SWT.DEFAULT);
        upColumnBtn = new Button(btnComposite, SWT.NONE);
        upColumnBtn.setLayoutData(gdUpDown);
        upColumnBtn.setEnabled(false);
        upColumnBtn.setText(Messages.btnUp);
        upColumnBtn.addSelectionListener(new UpBtnListenerOnGeneTab());

        downColumnBtn = new Button(btnComposite, SWT.DOWN);
        downColumnBtn.setLayoutData(gdUpDown);
        downColumnBtn.setEnabled(false);
        downColumnBtn.setText(Messages.btnDown);
        downColumnBtn.addSelectionListener(new DownBtnListenerOnGeneTab());

        final Button addButton = new Button(btnComposite, SWT.NONE);
        final GridData gdAddButton = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        gdAddButton.horizontalIndent = 10;
        addButton.setLayoutData(gdAddButton);
        addButton.setText(Messages.btnAddColumn);
        addButton.addSelectionListener(new AddBtnListenerOnGeneTab());

        deleteColumnBtn = new Button(btnComposite, SWT.NONE);
        final GridData gdDeleteButton = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        gdDeleteButton.horizontalIndent = 10;
        deleteColumnBtn.setLayoutData(gdDeleteButton);
        deleteColumnBtn.setEnabled(false);
        deleteColumnBtn.setText(Messages.btnDelColumn);
        deleteColumnBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
                        deleteColumn();
                    }
                });

        columnsTable.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        handleSelectionChangeInColumnTable();
                    }
                });

        buildColumnTableMenu();
    }

    private void createTableInformationGroup(Composite compositeGenaral) {
        final Group group = new Group(compositeGenaral, SWT.NONE);
        group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        group.setLayout(gridLayout);
        group.setText(Messages.lblTableInfo);

        final Label tableNameLabel = new Label(group, SWT.NONE);
        tableNameLabel.setData(Messages.dataNewKey, null);
        tableNameLabel.setText(Messages.lblTableName);

        final SchemaInfo newSchemaInfo = getNewSchemaInfo();
        tableNameComp = new Composite(group, SWT.NONE);
        {
            GridLayout gl = new GridLayout();
            gl.numColumns = 2;
            gl.marginWidth = 0;
            tableNameComp.setLayout(gl);
            tableNameComp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

            tableNameText = new Text(tableNameComp, SWT.BORDER);
            {
                GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
                tableNameText.setLayoutData(gd);
            }
            tableNameText.addModifyListener(
                    new ModifyListener() {
                        public void modifyText(ModifyEvent event) {
                            if (tableNameText.getText().length() == 0) {
                                CommonUITool.hideErrorBaloon(errorBaloon);
                            } else if (verifyTableName()) {
                                String tableName = tableNameText.getText();
                                newSchemaInfo.setClassname(tableName);
                                newSchemaInfo.setOwner(database.getUserName());
                                if (isSupportUserSchema()) {
                                    newSchemaInfo.setUniqueName(
                                            database.getUserName() + "." + tableName);
                                } else {
                                    newSchemaInfo.setUniqueName(tableName);
                                }
                            }
                        }
                    });
            tableNameText.addFocusListener(
                    new FocusAdapter() {
                        public void focusLost(FocusEvent e) {
                            CommonUITool.hideErrorBaloon(errorBaloon);
                        }
                    });
        }

        // char set
        if (supportCharset) {
            Composite collationComposite = new Composite(tableNameComp, SWT.NONE);
            collationComposite.setLayout(new GridLayout(2, false));

            final Label collationLabel = new Label(collationComposite, SWT.NONE);
            collationLabel.setText(Messages.lblCollation);
            collationCombo = new Combo(collationComposite, SWT.READ_ONLY);
            collationCombo.setLayout(new FillLayout());
            collationCombo.setVisibleItemCount(10);
            fillCollationCombo();
            String collation = collationCombo.getText();
            newSchemaInfo.setCollation(collation);
            collationCombo.addSelectionListener(
                    new SelectionListener() {
                        public void widgetSelected(SelectionEvent e) {
                            String collation = collationCombo.getText();
                            newSchemaInfo.setCollation(collation);
                        }

                        public void widgetDefaultSelected(SelectionEvent e) {}
                    });

            if (!isNewTableFlag && newSchemaInfo.getCollation() != null) {
                collationCombo.setText(newSchemaInfo.getCollation());
            }
        } else {
            new Label(tableNameComp, SWT.NONE);
        }

        if (erSchema.isPhysicModel()) { // desc info
            final Label tableDescLabel = new Label(group, SWT.NONE);
            tableDescLabel.setText(Messages.lblTableDesc);
            tableDescText = new Text(group, SWT.BORDER);
            tableDescText.setTextLimit(612);
            tableDescText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            //		tableDescText.setLayoutData(new FillLayout());
            if (newSchemaInfo != null && newSchemaInfo.getDescription() != null) {
                tableDescText.setText(newSchemaInfo.getDescription());
            }
            tableDescText.addFocusListener(
                    new FocusAdapter() {
                        public void focusGained(FocusEvent e) {}

                        public void focusLost(FocusEvent e) {
                            CommonUITool.hideErrorBaloon(errorBaloon);
                        }
                    });
            tableDescText.setEditable(true);
        }
    }

    public String[] listDataTypes() {
        if (database == null || database.getDatabaseInfo() == null) {
            return new String[] {};
        }
        List<String> list = new ArrayList<String>();

        String[][] typeMapping = DataType.getTypeMapping(database.getDatabaseInfo(), false, true);
        for (int j = 0; j < typeMapping.length; j++) {
            if (typeMapping[j][0] != null && typeMapping[j][0].startsWith("VARCHAR")) {
                list.add("VARCHAR(255)");
                list.add("VARCHAR(4096)");
            }
            list.add(typeMapping[j][0]);
        }

        Collections.sort(list);
        return list.toArray(new String[list.size()]);
    }

    public void loadColumnData() {
        List<ERTableColumn> list = new ArrayList<ERTableColumn>();
        list.addAll(newERTable.getColumns());
        list.addAll(tempERColumnList);
        getNewSchemaInfo().removeInvalidPKAndIndex(false);
        columnTableView.setInput(list);
    }

    private void buildColumnTableMenu() {
        Menu menu = new Menu(columnsTable.getShell(), SWT.POP_UP);
        columnsTable.setMenu(menu);

        final MenuItem deleteItem = new MenuItem(menu, SWT.PUSH);
        deleteItem.setText(Messages.itemDeleteColumn);
        deleteItem.setImage(
                CommonUIPlugin.getImageDescriptor("icons/action/table_record_delete.png")
                        .createImage());
        deleteItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
                        deleteColumn();
                    }
                });

        menu.addMenuListener(
                new MenuListener() {
                    public void menuShown(MenuEvent e) {
                        TableItem[] tblItems = columnsTable.getSelection();
                        if (tblItems.length > 0) {
                            DBAttribute attr = (DBAttribute) tblItems[0].getData();
                            List<DBAttribute> items = getNewSchemaInfo().getAttributes();
                            if (!items.contains(attr)) {
                                deleteItem.setEnabled(false);
                            } else {
                                deleteItem.setEnabled(true);
                            }
                        }
                    }

                    public void menuHidden(MenuEvent e) {}
                });
    }

    /** Handle selection change event in column table */
    private void handleSelectionChangeInColumnTable() {
        int selectionCount = columnsTable.getSelectionCount();
        if (selectionCount <= 0) {
            deleteColumnBtn.setEnabled(false);
            downColumnBtn.setEnabled(false);
            upColumnBtn.setEnabled(false);
        } else {
            TableItem[] tblItems = columnsTable.getSelection();
            ERTableColumn erColumn = (ERTableColumn) tblItems[0].getData();
            boolean isInheritAttr =
                    erColumn.getAttr().getInherit() != null
                            && !tableNameText
                                    .getText()
                                    .trim()
                                    .equalsIgnoreCase(erColumn.getAttr().getInherit().trim());
            deleteColumnBtn.setEnabled(!isInheritAttr);

            if (selectionCount > 1) {
                upColumnBtn.setEnabled(false);
                downColumnBtn.setEnabled(false);
            } else {
                if (database == null || database.getDatabaseInfo() == null) {
                    return;
                }

                boolean isSupportReorderColumn =
                        CompatibleUtil.isSupportReorderColumn(database.getDatabaseInfo());
                if (isSupportReorderColumn && !isNewTableFlag) {
                    // class attribute do not support to reorder
                    if (erColumn.getAttr().isClassAttribute() || isInheritAttr) {
                        isSupportReorderColumn = false;
                    }
                }

                int count = 0;
                SchemaInfo newSchemaInfo = getNewSchemaInfo();
                if (newSchemaInfo.getClassAttributes() != null) {
                    count = newSchemaInfo.getClassAttributes().size();
                }
                int index = columnsTable.getSelectionIndex();
                if (!erColumn.getAttr().isClassAttribute()) {
                    List<DBAttribute> attrList = newSchemaInfo.getAttributes();
                    count = attrList.size();
                    int inheritAttrCount = 0;
                    for (DBAttribute dbAttr : attrList) {
                        boolean isInheritDbAttr =
                                dbAttr.getInherit() != null
                                        && dbAttr.getInherit().trim().length() > 0
                                        && !tableNameText
                                                .getText()
                                                .trim()
                                                .equalsIgnoreCase(dbAttr.getInherit().trim());
                        if (isInheritDbAttr) {
                            inheritAttrCount++;
                        }
                    }
                    index = index - inheritAttrCount;
                    count = count - inheritAttrCount;
                }
                if (index == 0) {
                    upColumnBtn.setEnabled(false);
                } else {
                    upColumnBtn.setEnabled(
                            isNewTableFlag ? !isInheritAttr : isSupportReorderColumn);
                }
                if (index == count - 1) {
                    downColumnBtn.setEnabled(false);
                } else {
                    downColumnBtn.setEnabled(
                            isNewTableFlag ? !isInheritAttr : isSupportReorderColumn);
                }
                List<ERTableColumn> items = newERTable.getColumns();
                if (!items.contains(erColumn)) {
                    deleteColumnBtn.setEnabled(false);
                } else {
                    deleteColumnBtn.setEnabled(true);
                }
            }
        }
    }

    /**
     * Judge the current schemaInfo is has sub class
     *
     * @return
     */
    public boolean isHasSubClass() {
        if (isHasSubClass == null) {
            CheckSubClassTask task = new CheckSubClassTask(database.getDatabaseInfo());
            isHasSubClass =
                    task.checkSubClass(oldSchemaInfo.getOwner(), oldSchemaInfo.getClassname());
        }

        return isHasSubClass;
    }

    /**
     * A class which listens the down button on the general tab item
     *
     * @author lizhiqiang
     * @version 1.0 - 2010-1-7 created by lizhiqiang
     */
    final class PkBtnListenerOnGeneTab extends SelectionAdapter {
        private boolean isNewTable;

        PkBtnListenerOnGeneTab(boolean isNewTable) {
            this.isNewTable = isNewTable;
        }

        /**
         * Sent when selection occurs in the control.
         *
         * @param event an event containing information about the selection
         */
        public void widgetSelected(SelectionEvent event) {
            SchemaInfo newSchemaInfo = getNewSchemaInfo();
            SetPKDialog dlg = new SetPKDialog(getShell(), database, newSchemaInfo, isNewTable);
            int ret = dlg.open();
            if (ret == SetPKDialog.OK) {
                Constraint oldPK = dlg.getOldPK();
                Constraint newPK = dlg.getNewPK();
                String op = dlg.getOperation();
                if (("ADD").equals(op)) { // $NON-NLS-1$
                    List<Constraint> cons = newSchemaInfo.getConstraints();
                    if (!cons.contains(newPK)) {
                        newSchemaInfo.addConstraint(newPK);
                    }
                    firePKAdded(newSchemaInfo, newPK);
                } else if (("DEL").equals(op)) { // $NON-NLS-1$
                    newSchemaInfo.getConstraints().remove(oldPK);
                    firePKRemoved(newSchemaInfo, oldPK);
                } else if (("MODIFY").equals(op)) { // $NON-NLS-1$
                    newSchemaInfo.getConstraints().remove(oldPK);
                    firePKRemoved(newSchemaInfo, oldPK);
                    newSchemaInfo.addConstraint(newPK);
                    firePKAdded(newSchemaInfo, newPK);
                }

                List<String> pkAttrs = newPK.getAttributes();
                List<ERTableColumn> columns = newERTable.getColumns();
                for (ERTableColumn column : columns) {
                    String name = column.getName();
                    if (pkAttrs.contains(name)) {
                        column.setIsPrimaryKey(true);
                    } else {
                        column.setIsPrimaryKey(false);
                    }
                }

                attrLabelProvider.setSchema(newSchemaInfo);
                loadColumnData();
                indexTableView.setInput(newSchemaInfo);
            }
        }

        /**
         * Fire the PK added.
         *
         * @param newSchema the object of SchemaInfo
         * @param newPK the object of Constraint
         */
        private void firePKAdded(SchemaInfo newSchema, Constraint newPK) {
            if (newSchema == null || newPK == null) {
                return;
            }

            List<String> attrList = newPK.getAttributes();
            if (attrList.size() == 1) {
                String attrName = attrList.get(0);
                Constraint index = newSchema.removeUniqueByAttrName(attrName);
                if (index != null) {
                    String key =
                            index.getDefaultName(newSchema.getClassname()) + "$" + index.getName();
                    SchemaChangeLog changeLog =
                            new SchemaChangeLog(key, null, SchemeInnerType.TYPE_INDEX);
                    schemaChangeMgr.addSchemeChangeLog(changeLog);
                }

                DBAttribute attr = newSchema.getDBAttributeByName(attrName, false);
                if (attr == null) {
                    return;
                }

                if (!attr.isNotNull()) {
                    attr.setNotNull(true);
                }
                if (!attr.isUnique()) {
                    attr.setUnique(true);
                }
                afterModifyAttr(attrName, attrName);
            } else {
                for (String attrName : attrList) {
                    DBAttribute attr = newSchema.getDBAttributeByName(attrName, false);
                    if (attr != null && !attr.isNotNull()) {
                        attr.setNotNull(true);
                        afterModifyAttr(attrName, attrName);
                    }
                }
            }
        }

        /**
         * Fire the PK removed
         *
         * @param newSchema the object of SchemaInfo
         * @param oldPK the object of Constraint
         */
        public void firePKRemoved(SchemaInfo newSchema, Constraint oldPK) {
            if (newSchema == null || oldPK == null) {
                return;
            }

            List<String> attrList = oldPK.getAttributes();
            if (attrList.size() == 1) {
                String attrName = attrList.get(0);
                Constraint index = newSchema.removeUniqueByAttrName(attrName);
                if (index != null) {
                    String key =
                            index.getDefaultName(newSchema.getClassname()) + "$" + index.getName();
                    SchemaChangeLog changeLog =
                            new SchemaChangeLog(key, null, SchemeInnerType.TYPE_INDEX);
                    schemaChangeMgr.addSchemeChangeLog(changeLog);
                }
                DBAttribute attr = newSchema.getDBAttributeByName(attrName, false);
                if (attr == null) {
                    return;
                }
                attr.setNotNull(false);
                attr.setUnique(false);
                afterModifyAttr(attrName, attrName);
            } else {
                for (String attrName : attrList) {
                    DBAttribute attr = newSchema.getDBAttributeByName(attrName, false);
                    if (attr == null) {
                        continue;
                    }
                    attr.setNotNull(false);
                    afterModifyAttr(attrName, attrName);
                }
            }
        }
    }

    /**
     * A class which listens the down button on the general tab item.
     *
     * @author lizhiqiang
     * @version 1.0 - 2010-1-7 created by lizhiqiang
     */
    private final class UpBtnListenerOnGeneTab extends SelectionAdapter {
        public void widgetSelected(SelectionEvent event) {
            TableItem[] selection = columnsTable.getSelection();
            int selectionIndex = columnsTable.getSelectionIndex();
            if (selection != null && selection.length >= 1) {
                ERTableColumn erColumn = (ERTableColumn) selection[0].getData();
                boolean isClassAttr = erColumn.getAttr().isClassAttribute();
                List<DBAttribute> attrList = newERTable.getSchemaInfo().getClassAttributes();
                if (!isClassAttr) {
                    attrList = newERTable.getSchemaInfo().getAttributes();
                }

                if (selectionIndex > 0) {
                    DBAttribute attribute = attrList.get(selectionIndex);
                    attrList.remove(selectionIndex);
                    attrList.add(selectionIndex - 1, attribute);
                    newERTable.getColumns().remove(selectionIndex);
                    newERTable.getColumns().add(selectionIndex - 1, erColumn);
                    loadColumnData();
                    columnTableView.setSelection(new StructuredSelection(erColumn.getAttr()), true);
                    columnsTable.setFocus();
                    handleSelectionChangeInColumnTable();
                }
            }
        }
    }

    /**
     * A class which listens the down button on the general tab item
     *
     * @author lizhiqiang
     * @version 1.0 - 2010-1-7 created by lizhiqiang
     */
    private final class DownBtnListenerOnGeneTab extends SelectionAdapter {
        public void widgetSelected(SelectionEvent event) {
            TableItem[] selection = columnsTable.getSelection();
            int selectionIndex = columnsTable.getSelectionIndex();
            if (selection != null && selection.length >= 1) {
                ERTableColumn erColumn = (ERTableColumn) selection[0].getData();
                boolean isClassAttr = erColumn.getAttr().isClassAttribute();
                List<DBAttribute> attrList = newERTable.getSchemaInfo().getClassAttributes();
                if (!isClassAttr) {
                    attrList = newERTable.getSchemaInfo().getAttributes();
                }

                if (!(selectionIndex == attrList.size() - 1)) {
                    DBAttribute attribute = attrList.get(selectionIndex);
                    attrList.remove(selectionIndex);
                    attrList.add(selectionIndex + 1, attribute);
                    newERTable.getColumns().remove(selectionIndex);
                    newERTable.getColumns().add(selectionIndex + 1, erColumn);
                    loadColumnData();
                    columnTableView.setSelection(new StructuredSelection(erColumn.getAttr()), true);
                    columnsTable.setFocus();
                    handleSelectionChangeInColumnTable();
                }
            }
        }
    }

    /**
     * A class which listens the edit button on the general tab item
     *
     * @author lizhiqiang
     * @version 1.0 - 2010-1-7 created by lizhiqiang
     */
    private final class AddBtnListenerOnGeneTab extends SelectionAdapter {
        public void widgetSelected(SelectionEvent event) {
            addNewColumn();
        }
    }

    public void addNewColumn() {
        SchemaInfo newSchemaInfo = getNewSchemaInfo();
        if (newSchemaInfo == null) {
            return;
        }

        // boolean hasNotFinishedColumn = false;
        boolean hasDuplicatedColumn = false;

        List<ERTableColumn> items = newERTable.getColumns();
        if (items != null && items.size() > 0) {
            Set<String> matches = new HashSet<String>();

            // check whether there is no name column
            for (ERTableColumn col : items) {
                if (col == null) {
                    continue;
                }

                if (StringUtil.isEmpty(col.getName(erSchema.isPhysicModel()))) {
                    continue;
                }

                if (matches.contains(col.getName(erSchema.isPhysicModel()))) {
                    hasDuplicatedColumn = true;
                    break;
                }

                matches.add(col.getName(erSchema.isPhysicModel()));
            }
        }

        if (hasDuplicatedColumn) {
            CommonUITool.openErrorBox(Messages.errSameNameOnEditTableAddColumn);
            return;
        }

        String collation = null;
        if (newSchemaInfo != null && newSchemaInfo.getCollation() != null) {
            collation = newSchemaInfo.getCollation();
        } else {
            collation = Collation.DEFAULT_COLLATION;
        }

        DBAttribute addAttribute =
                new DBAttribute(
                        "",
                        DataType.DATATYPE_CHAR,
                        newSchemaInfo.getUniqueName(),
                        false,
                        false,
                        false,
                        false,
                        null,
                        collation);
        ERTableColumn column = new ERTableColumn(newERTable, addAttribute, false);
        column.setIsNew(true);
        tempERColumnList.add(column);
        loadColumnData();

        columnTableView.setSelection(new StructuredSelection(addAttribute), true);
        columnsTable.setFocus();
        handleSelectionChangeInColumnTable();
    }

    /**
     * Make a change log for editing attribute
     *
     * @param attrName
     * @param editingCol ERD column to be changed by the user
     * @param oldCol ERD column
     * @return
     */
    public boolean changeForEditElement(
            String attrName,
            ERTableColumn editingCol,
            ERTableColumn oldCol) { // FIXME move this logic to core module
        SchemaInfo newSchemaInfo = getNewSchemaInfo();
        if (database == null || editingCol == null) {
            return false;
        }

        editingCol.getAttr().setInherit(newSchemaInfo.getClassname());
        String newAttrName = editingCol.getAttr().getName();

        if (oldCol == null) {
            oldCol = new ERTableColumn(newERTable, editingCol.getAttr(), false);
            oldCol.setIsNew(true);
        }
        DBAttribute oldAttr = oldCol.getAttr();
        DBAttribute editingAttr = editingCol.getAttr();

        if (!oldAttr.isUnique()
                && newSchemaInfo.getUniqueByAttrName(editingAttr.getName()) == null
                && editingAttr.isUnique()) {
            Constraint unique = new Constraint(true);
            unique.setType(Constraint.ConstraintType.UNIQUE.getText());
            unique.addAttribute(newAttrName);
            unique.addRule(newAttrName + " ASC");
            unique.setName(
                    ConstraintNamingUtil.getUniqueName(
                            newSchemaInfo.getClassname(), unique.getRules()));
            newSchemaInfo.addConstraint(unique);
        } else if (oldAttr.isUnique() && !editingAttr.isUnique()) {
            newSchemaInfo.removeUniqueByAttrName(attrName);
        } else if (!oldAttr.isUnique() && !editingAttr.isUnique()) {
            newSchemaInfo.removeUniqueByAttrName(attrName);
        }

        indexTableView.setInput(newSchemaInfo);
        fkTableView.setInput(newSchemaInfo);

        if (database != null && database.getDatabaseInfo() != null && newSchemaInfo != null) {
            SuperClassUtil.fireSuperClassChanged(
                    database.getDatabaseInfo(),
                    oldSchemaInfo,
                    newSchemaInfo,
                    newSchemaInfo.getSuperClasses());
        }
        attrLabelProvider.setSchema(newSchemaInfo);
        loadColumnData();
        columnTableView.setSelection(new StructuredSelection(editingCol), true);
        columnsTable.setFocus();
        handleSelectionChangeInColumnTable();

        return true;
    }

    public boolean makeChangeLogForIndex(
            String attrName,
            DBAttribute editAttr,
            DBAttribute origAttr) { // FIXME move this logic to core module
        List<Constraint> constrainList = newERTable.getSchemaInfo().getConstraints();
        if (constrainList.size() == 0 || StringUtil.isEmpty(attrName)) {
            return false;
        }

        List<Constraint> removedConstrainList = new ArrayList<Constraint>();
        List<Constraint> addedConstrainList = new ArrayList<Constraint>();
        for (Constraint cons : constrainList) {
            if (cons == null) {
                continue;
            }
            if (ConstraintType.INDEX.getText().equals(cons.getType())
                    || ConstraintType.REVERSEINDEX.getText().equals(cons.getType())
                    || ConstraintType.UNIQUE.getText().equals(cons.getType())
                    || ConstraintType.REVERSEUNIQUE.getText().equals(cons.getType())) {
                List<String> attrs = cons.getAttributes();
                if (!(attrs != null && attrs.contains(attrName))) {
                    continue;
                }

                removedConstrainList.add(cons);
                Constraint newCons = new Constraint(true);
                newCons.setType(cons.getType());
                newCons.setName(cons.getName());
                cons.replaceAttribute(attrName, editAttr.getName());
                newCons.setAttributes(cons.getAttributes());
                for (String origRule : cons.getRules()) {
                    if (cons.getRules().size() == 1) {
                        newCons.addRule(editAttr.getName() + origRule.substring(attrName.length()));
                        break;
                    }
                    int spaceIndex = origRule.indexOf(" ");
                    String attrNameFromRule = origRule.substring(0, spaceIndex);
                    if (attrName.equals(attrNameFromRule)) {
                        newCons.addRule(editAttr.getName() + origRule.substring(attrName.length()));
                    } else {
                        newCons.addRule(origRule);
                    }
                }
                addedConstrainList.add(newCons);

                String key =
                        cons.getDefaultName(newERTable.getSchemaInfo().getClassname())
                                + "$"
                                + cons.getName();
                SchemaChangeLog changeLog =
                        new SchemaChangeLog(key, key, SchemeInnerType.TYPE_INDEX);
                schemaChangeMgr.addSchemeChangeLog(changeLog);
            }
        }

        if (removedConstrainList.size() == 0) {
            return false;
        }
        constrainList.removeAll(removedConstrainList);
        constrainList.addAll(addedConstrainList);
        indexTableView.setInput(newERTable.getSchemaInfo());

        return true;
    }

    private void deleteColumn() { // FIXME move this logic to core module

        if (!CommonUITool.openConfirmBox(Messages.msgDeleteColumnConfirm)) {
            return;
        }

        TableItem[] tblItems = columnsTable.getSelection();
        if (tblItems.length > 0) {
            ERTableColumn column = (ERTableColumn) tblItems[0].getData();
            List<ERTableColumn> items = newERTable.getColumns();
            if (!items.contains(column)) {
                return;
            }
        }

        TableItem[] selection = columnsTable.getSelection();
        int selectionIndex = columnsTable.getSelectionIndex();
        if (selection != null && selection.length >= 1) {
            List<String> physicalNames = new ArrayList<String>();
            List<String> columnNames = new ArrayList<String>();
            for (int m = 0; m < selection.length; m++) {
                columnNames.add(m, selection[m].getText(2));
                physicalNames.add(m, ((ERTableColumn) selection[m].getData()).getName());
            }
            List<SchemaInfo> allSupers =
                    SuperClassUtil.getSuperClasses(
                            database.getDatabaseInfo(), newERTable.getSchemaInfo());
            Constraint pk = newERTable.getSchemaInfo().getPK(allSupers);
            List<String> pkAttributes = pk == null ? new ArrayList<String>() : pk.getAttributes();
            boolean hasPk = false;
            for (String pkAttribute : pkAttributes) {
                if (physicalNames.contains(pkAttribute)) {
                    hasPk = true;
                    break;
                }
            }
            if (hasPk && physicalNames.containsAll(pkAttributes)) {
                newERTable
                        .getSchemaInfo()
                        .removeConstraintByName(
                                pk.getName(), Constraint.ConstraintType.PRIMARYKEY.getText());
            }

            SchemaInfo newSchemaInfo = getNewSchemaInfo();
            for (TableItem selec : selection) {
                ERTableColumn oldColumn = (ERTableColumn) selec.getData();
                if (oldColumn == null) {
                    continue;
                }

                if (oldColumn.getAttr().isClassAttribute()) {
                    newSchemaInfo.getClassAttributes().remove(oldColumn.getAttr());
                } else {
                    newSchemaInfo.getAttributes().remove(oldColumn.getAttr());
                    newSchemaInfo.removeAttrInConstraints(oldColumn.getAttr().getName());
                    indexTableView.setInput(newSchemaInfo);
                    fkTableView.setInput(newSchemaInfo);
                }
                afterDeleteColumn(oldColumn.getName(isPhysical));
                newERTable.removeColumn(oldColumn.getName(isPhysical), isPhysical);
            }

            attrLabelProvider.setSchema(newSchemaInfo);
            loadColumnData();

            int itemCount = columnsTable.getItemCount();
            columnsTable.select(selectionIndex < itemCount ? selectionIndex : selectionIndex - 1);
            columnsTable.setFocus();
        }
    }

    /**
     * Create foreign key/Index tab item.
     *
     * @param tabFolder the object of TabFolder
     */
    private void createFkIndexTabItem(final TabFolder tabFolder) {
        final TabItem foreignKeyTabItem = new TabItem(tabFolder, SWT.NONE);
        foreignKeyTabItem.setText(Messages.infoIndexesTab);

        final Composite composite = new Composite(tabFolder, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        composite.setLayout(gridLayout);
        foreignKeyTabItem.setControl(composite);

        // create the fk table viewer
        final Label fkLabel = new Label(composite, SWT.NONE);
        fkLabel.setText(Messages.lblFK);

        fkTableView = new TableViewer(composite, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
        fkTable = fkTableView.getTable();

        final GridData gdFkTable = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
        fkTable.setLayoutData(gdFkTable);
        fkTable.setLinesVisible(true);
        fkTable.setHeaderVisible(true);

        TableColumn tblCol = new TableColumn(fkTable, SWT.NONE);
        tblCol.setWidth(100);
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

        FKTableViewerContentProvider fkContentProvider = new FKTableViewerContentProvider();
        FKTableViewerLabelProvider fkLabelProvider =
                new FKTableViewerLabelProvider(database.getDatabaseInfo(), erSchema);
        fkTableView.setContentProvider(fkContentProvider);
        fkTableView.setLabelProvider(fkLabelProvider);
        fkTableView.setInput(getNewSchemaInfo());

        final Composite fkBtnComposite = new Composite(composite, SWT.NONE);
        final GridData gdBtnComposite = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        fkBtnComposite.setLayoutData(gdBtnComposite);
        gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        fkBtnComposite.setLayout(gridLayout);

        final GridData gdFKBTN = new GridData(SWT.LEFT, SWT.CENTER, false, false);

        final Button addFKBTN = new Button(fkBtnComposite, SWT.NONE);
        addFKBTN.setLayoutData(gdFKBTN);
        addFKBTN.setText(Messages.btnAddFk);
        addFKBTN.addSelectionListener(new AddFkBtnListenerOnFkIndexTab());

        final Button editFKBTN = new Button(fkBtnComposite, SWT.NONE);
        editFKBTN.setLayoutData(gdFKBTN);
        editFKBTN.setEnabled(false);
        editFKBTN.setText(Messages.btnEditFk);
        editFKBTN.addSelectionListener(new EditFkBtnListenerOnFkIndexTab());

        final Button deleteFKBTN = new Button(fkBtnComposite, SWT.NONE);
        deleteFKBTN.setLayoutData(gdFKBTN);
        deleteFKBTN.setEnabled(false);
        deleteFKBTN.setText(Messages.btnDelFk);
        deleteFKBTN.addSelectionListener(new DelFkBtnListenerOnFkIndexTab());

        fkTable.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        SchemaInfo newSchemaInfo = getNewSchemaInfo();
                        if (database == null
                                || database.getDatabaseInfo() == null
                                || newSchemaInfo == null) {
                            return;
                        }

                        TableItem[] items = fkTable.getSelection();
                        if (items == null || items.length == 0) {
                            deleteFKBTN.setEnabled(false);
                            editFKBTN.setEnabled(false);
                        } else {
                            deleteFKBTN.setEnabled(true);
                            editFKBTN.setEnabled(items.length == 1);
                            for (TableItem item : items) {
                                String fkName = item.getText(0);
                                List<SchemaInfo> superList =
                                        SuperClassUtil.getSuperClasses(
                                                database.getDatabaseInfo(),
                                                newSchemaInfo.getSuperClasses());
                                if (newSchemaInfo.isInSuperClasses(superList, fkName)) {
                                    deleteFKBTN.setEnabled(false);
                                    editFKBTN.setEnabled(false);
                                }
                            }
                        }
                    }
                });

        // create index table view
        final Label indexLabel = new Label(composite, SWT.NONE);
        indexLabel.setText(Messages.lblIndexes);

        indexTableView = new TableViewer(composite, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
        indexTable = indexTableView.getTable();
        indexTable.setLinesVisible(true);
        indexTable.setHeaderVisible(true);
        final GridData gdIndexTable = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
        indexTable.setLayoutData(gdIndexTable);

        tblCol = new TableColumn(indexTable, SWT.NONE);
        tblCol.setWidth(150);
        tblCol.setText(Messages.tblColumnIndexName);

        tblCol = new TableColumn(indexTable, SWT.NONE);
        tblCol.setWidth(78);
        tblCol.setText(Messages.tblColumnIndexType);

        tblCol = new TableColumn(indexTable, SWT.NONE);
        tblCol.setWidth(218);
        tblCol.setText(Messages.tblColumnOnColumns);

        tblCol = new TableColumn(indexTable, SWT.NONE);
        tblCol.setWidth(282);
        tblCol.setText(Messages.tblColumnIndexRule);

        IndexTableViewerContentProvider indexContentProvider =
                new IndexTableViewerContentProvider();
        IndexTableViewerLabelProvider indexLabelProvider = new IndexTableViewerLabelProvider();
        indexTableView.setContentProvider(indexContentProvider);
        indexTableView.setLabelProvider(indexLabelProvider);
        indexTableView.setInput(getNewSchemaInfo());
        indexTableView.addDoubleClickListener(
                new IDoubleClickListener() {
                    public void doubleClick(DoubleClickEvent event) {
                        StructuredSelection selected = (StructuredSelection) event.getSelection();
                        if (selected == null) {
                            return;
                        }
                        Constraint constraint = (Constraint) selected.getFirstElement();
                        openEditIndexDialog(constraint);
                    }
                });

        final Composite indexBtnComposite = new Composite(composite, SWT.NONE);
        final GridData gdIndexBtnComposite = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        indexBtnComposite.setLayoutData(gdIndexBtnComposite);
        gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        indexBtnComposite.setLayout(gridLayout);

        final GridData gdIndexBTN = new GridData(SWT.LEFT, SWT.CENTER, false, false);

        final Button addIndexBTN = new Button(indexBtnComposite, SWT.NONE);
        addIndexBTN.setLayoutData(gdIndexBTN);
        addIndexBTN.setText(Messages.btnAddIndex);
        addIndexBTN.addSelectionListener(new AddIndexBtnListenerOnFkIndexTab());

        final Button editIndexBTN = new Button(indexBtnComposite, SWT.NONE);
        editIndexBTN.setLayoutData(gdIndexBTN);
        editIndexBTN.setEnabled(false);
        editIndexBTN.setText(Messages.btnEditIndex);
        editIndexBTN.addSelectionListener(new EditIndexBtnListenerOnFkIndexTab());

        final Button deleteIndexBTN = new Button(indexBtnComposite, SWT.NONE);
        deleteIndexBTN.setLayoutData(gdIndexBTN);
        deleteIndexBTN.setEnabled(false);
        deleteIndexBTN.setText(Messages.btnDelIndex);
        deleteIndexBTN.addSelectionListener(new DelIndexBtnListenerOnFkIndexTab());

        indexTable.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        TableItem[] items = indexTable.getSelection();
                        if (items == null || items.length == 0) {
                            deleteIndexBTN.setEnabled(false);
                            editIndexBTN.setEnabled(false);
                        } else {
                            deleteIndexBTN.setEnabled(true);
                            editIndexBTN.setEnabled(items.length == 1);
                        }
                    }
                });
    }

    /**
     * A class which listens the add button of FK part on the Fk/Index tab.
     *
     * @author lizhiqiang
     * @version 1.0 - 2010-1-7 created by lizhiqiang
     */
    private final class AddFkBtnListenerOnFkIndexTab extends SelectionAdapter {
        public void widgetSelected(SelectionEvent event) {
            SchemaInfo newSchemaInfo = getNewSchemaInfo();
            AddFKDialog dlg =
                    new AddFKDialog(
                            getShell(),
                            database,
                            newSchemaInfo,
                            null,
                            true,
                            erSchema.getAllSchemaInfo());
            int returnCode = dlg.open();
            if (returnCode == AddFKDialog.OK) {
                Constraint fk = dlg.getRetFK();
                if (fk == null) {
                    return;
                }
                newSchemaInfo.addConstraint(fk);
                fkTableView.setInput(newSchemaInfo);
                fkTableView.refresh();
            }
        }
    }

    /**
     * A class which listens the edit button of FK part on the Fk/Index tab.
     *
     * @author pangqiren
     * @version 1.0 - 2012-7-9 created by pangqiren
     */
    private final class EditFkBtnListenerOnFkIndexTab extends SelectionAdapter {
        public void widgetSelected(SelectionEvent event) {
            SchemaInfo newSchemaInfo = getNewSchemaInfo();
            TableItem[] selection = fkTable.getSelection();
            if (selection == null || selection.length == 0) {
                return;
            }
            Constraint editedFk = (Constraint) selection[0].getData();

            AddFKDialog dlg =
                    new AddFKDialog(
                            getShell(),
                            database,
                            newSchemaInfo,
                            editedFk,
                            true,
                            erSchema.getAllSchemaInfo());
            int returnCode = dlg.open();
            if (returnCode == AddFKDialog.OK) {
                Constraint newFk = dlg.getRetFK();
                if (newFk == null) {
                    return;
                }

                newSchemaInfo.removeFKConstraint(editedFk.getName());
                newSchemaInfo.addConstraint(newFk);
                SchemaChangeLog changeLog =
                        new SchemaChangeLog(
                                editedFk.getName(), newFk.getName(), SchemeInnerType.TYPE_FK);
                schemaChangeMgr.addSchemeChangeLog(changeLog);
                fkTableView.setInput(newSchemaInfo);
                fkTableView.refresh();
            }
        }
    }

    /**
     * A class which listens the delete button of FK part on the Fk/Index tab.
     *
     * @author lizhiqiang
     * @version 1.0 - 2010-1-7 created by lizhiqiang
     */
    private final class DelFkBtnListenerOnFkIndexTab extends SelectionAdapter {
        public void widgetSelected(SelectionEvent event) {
            SchemaInfo newSchemaInfo = getNewSchemaInfo();
            if (database == null || database.getDatabaseInfo() == null || newSchemaInfo == null) {
                return;
            }

            TableItem[] selection = fkTable.getSelection();
            if (selection != null && selection.length >= 1) {
                String fkName = selection[0].getText(0);

                List<SchemaInfo> superList =
                        SuperClassUtil.getSuperClasses(
                                database.getDatabaseInfo(), newSchemaInfo.getSuperClasses());
                if (newSchemaInfo.isInSuperClasses(superList, fkName)) {
                    CommonUITool.openErrorBox(Messages.errFKNotDrop);
                    return;
                }

                newSchemaInfo.removeFKConstraint(fkName);
                SchemaChangeLog changeLog =
                        new SchemaChangeLog(fkName, null, SchemeInnerType.TYPE_FK);
                schemaChangeMgr.addSchemeChangeLog(changeLog);
                fkTableView.setInput(newSchemaInfo);
            }
        }
    }

    /**
     * A class which listens the add button of Index part on the Fk/Index tab.
     *
     * @author lizhiqiang
     * @version 1.0 - 2010-1-7 created by lizhiqiang
     */
    private final class AddIndexBtnListenerOnFkIndexTab extends SelectionAdapter {
        public void widgetSelected(SelectionEvent event) {
            SchemaInfo newSchemaInfo = getNewSchemaInfo();
            AddIndexDialog dlg =
                    new AddIndexDialog(getShell(), newSchemaInfo, database, null, true);
            int returnCode = dlg.open();
            if (returnCode == AddIndexDialog.OK) {
                Constraint constraint = dlg.getIndexConstraint();
                if (constraint == null) {
                    return;
                }

                newSchemaInfo.addConstraint(constraint);
                // For bug TOOLS-2394 Unique index can't be added again
                if (Constraint.ConstraintType.UNIQUE.getText().equals(constraint.getType())
                        && constraint.getAttributes().size() == 1) {
                    DBAttribute attr =
                            newSchemaInfo.getDBAttributeByName(
                                    constraint.getAttributes().get(0), false);
                    attr.setUnique(true);
                    loadColumnData();
                }

                String key =
                        constraint.getDefaultName(newSchemaInfo.getClassname())
                                + "$"
                                + constraint.getName();
                schemaChangeMgr.addSchemeChangeLog(
                        new SchemaChangeLog(null, key, SchemeInnerType.TYPE_INDEX));
                indexTableView.setInput(newSchemaInfo);
            }
        }
    }

    /**
     * A class which listens the edit button of Index part on the Fk/Index tab.
     *
     * @author pangqiren
     * @version 1.0 - 2012-7-9 created by pangqiren
     */
    private final class EditIndexBtnListenerOnFkIndexTab extends SelectionAdapter {
        public void widgetSelected(SelectionEvent event) {
            TableItem[] selection = indexTable.getSelection();
            if (selection == null || selection.length == 0) {
                return;
            }
            Constraint editedIndex = (Constraint) selection[0].getData();
            openEditIndexDialog(editedIndex);
        }
    }

    /**
     * Open the index edit dialog with the constraint object
     *
     * @param editedIndex Constraint
     */
    private void openEditIndexDialog(Constraint editedIndex) {
        boolean isNewConstraint = true;
        for (int i = 0, len = originalConstraints.size(); i < len; i++) {
            if (originalConstraints.get(i) == editedIndex) {
                isNewConstraint = false;
            }
        }

        SchemaInfo newSchemaInfo = getNewSchemaInfo();
        AddIndexDialog dlg =
                new AddIndexDialog(
                        getShell(), newSchemaInfo, database, editedIndex, isNewConstraint);
        int returnCode = dlg.open();
        if (returnCode == AddIndexDialog.OK) {
            Constraint newIndex = dlg.getIndexConstraint();
            if (newIndex == null) {
                return;
            }

            newSchemaInfo.removeConstraintByName(editedIndex.getName(), editedIndex.getType());
            newSchemaInfo.addConstraint(newIndex);

            // For bug TOOLS-2394 Unique index can't be added again
            if (Constraint.ConstraintType.UNIQUE.getText().equals(newIndex.getType())
                    && newIndex.getAttributes().size() == 1) {
                DBAttribute attr =
                        newSchemaInfo.getDBAttributeByName(newIndex.getAttributes().get(0), false);
                attr.setUnique(true);
                loadColumnData();
            }

            boolean modifiedUK =
                    Constraint.ConstraintType.UNIQUE.getText().equals(editedIndex.getType())
                            && editedIndex.getAttributes().size() == 1;
            boolean noNewUK =
                    !Constraint.ConstraintType.UNIQUE.getText().equals(newIndex.getType())
                            || newIndex.getAttributes().size() != 1;

            if (modifiedUK && noNewUK) {
                String attrName = editedIndex.getAttributes().get(0);
                DBAttribute attr = newSchemaInfo.getDBAttributeByName(attrName, false);
                if (attr != null) {
                    attr.setUnique(false);
                    loadColumnData();
                }
            }

            String key1 =
                    editedIndex.getDefaultName(newSchemaInfo.getClassname())
                            + "$"
                            + editedIndex.getName();
            String key2 =
                    newIndex.getDefaultName(newSchemaInfo.getClassname())
                            + "$"
                            + newIndex.getName();
            SchemaChangeLog changeLog = new SchemaChangeLog(key1, key2, SchemeInnerType.TYPE_INDEX);
            schemaChangeMgr.addSchemeChangeLog(changeLog);
            indexTableView.setInput(newSchemaInfo);
        }
    }

    /**
     * A class which listens the delete button of Index part on the Fk/Index tab item.
     *
     * @author lizhiqiang
     * @version 1.0 - 2010-1-7 created by lizhiqiang
     */
    private final class DelIndexBtnListenerOnFkIndexTab extends SelectionAdapter {
        public void widgetSelected(SelectionEvent event) {
            SchemaInfo newSchemaInfo = getNewSchemaInfo();
            if (database == null || database.getDatabaseInfo() == null || newSchemaInfo == null) {
                return;
            }

            TableItem[] selection = indexTable.getSelection();
            if (selection != null
                    && selection.length >= 1) { // FIXME move this logic to core module
                String indexName = selection[0].getText(0);
                String indexType = selection[0].getText(1);
                Constraint index = newSchemaInfo.getConstraintByName(indexName, indexType);
                if (index == null) {
                    CommonUITool.openErrorBox(Messages.errCanNotFindIndex);
                    return;
                }

                List<SchemaInfo> superList =
                        SuperClassUtil.getSuperClasses(
                                database.getDatabaseInfo(), newSchemaInfo.getSuperClasses());
                if (newSchemaInfo.isInSuperClasses(superList, indexName)) {
                    CommonUITool.openErrorBox(Messages.errIndexNotDrop);
                    return;
                }
                if (indexType.equals(Constraint.ConstraintType.PRIMARYKEY.getText())) {
                    newSchemaInfo.getConstraints().remove(index);
                    new PkBtnListenerOnGeneTab(isNewTableFlag).firePKRemoved(newSchemaInfo, index);
                } else {
                    // For bug TOOLS-2394 Unique index can't be added again
                    if (Constraint.ConstraintType.UNIQUE.getText().equals(indexType)) {
                        Constraint constraint =
                                newSchemaInfo.getConstraintByName(indexName, indexType);
                        if (constraint != null && constraint.getAttributes().size() == 1) {
                            DBAttribute attr =
                                    newSchemaInfo.getDBAttributeByName(
                                            constraint.getAttributes().get(0), false);
                            if (attr.isUnique()) {
                                attr.setUnique(false);
                            }
                        }
                    }
                    newSchemaInfo.removeConstraintByName(indexName, indexType);

                    String key =
                            index.getDefaultName(newSchemaInfo.getClassname())
                                    + "$"
                                    + index.getName();
                    SchemaChangeLog changeLog =
                            new SchemaChangeLog(key, null, SchemeInnerType.TYPE_INDEX);
                    schemaChangeMgr.addSchemeChangeLog(changeLog);
                }

                indexTableView.setInput(newSchemaInfo);
                loadColumnData();
            }
        }
    }

    private void init() {
        String name = oldERTable.getShownName();
        tableNameText.setText(name);

        setTitle(Messages.bind(Messages.editTableMsgTitle, name));
        setMessage(Messages.editTableMsg);
        String title = Messages.bind(Messages.editTableShellTitle, name);
        getShell().setText(title);
    }

    private void buildVirtualDBCollationList() {
        collationList = new ArrayList<Collation>();
        Collation emptyCollation = new Collation();
        emptyCollation.setCharset("");
        emptyCollation.setName("");
        collationList.add(emptyCollation);
        collationList.addAll(Collation.getDefaultCollations());
    }

    private void fillCollationCombo() {
        if (collationList == null || WidgetUtil.disposed(collationCombo)) {
            return;
        }

        String searchCol = null;
        SchemaInfo newSchemaInfo = getNewSchemaInfo();
        if (oldSchemaInfo != null) {
            searchCol = oldSchemaInfo.getCollation();
        } else if (newSchemaInfo != null) {
            searchCol = newSchemaInfo.getCollation();
        } else {
            searchCol = Collation.DEFAULT_COLLATION;
        }

        for (Collation collation : collationList) {
            if (collation == null) {
                continue;
            }

            String collationName = collation.getName();
            if (collationName == null) {
                continue;
            }

            collationCombo.add(collationName);
            if (collationName.equals(searchCol)) {
                collationCombo.select(collationCombo.getItemCount() - 1);
            }
        }

        if (collationCombo.getItemCount() > 0 && collationCombo.getSelectionIndex() == -1) {
            collationCombo.select(0);
        }
    }

    /**
     * Verify the table name.
     *
     * @return
     */
    private boolean verifyTableName() { // FIXME move this logic to core module
        if (WidgetUtil.disposed(tableNameText)) {
            return false;
        }

        if (tableNameText.getEnabled()) {
            String tableName = tableNameText.getText();
            String msg = null;
            if (StringUtil.isEmpty(tableName)) {
                msg = Messages.errNoTableName;
            } else {
                if (erSchema.isPhysicModel() && !ValidateUtil.isValidIdentifier(tableName)) {
                    msg = Messages.bind(Messages.renameInvalidTableNameMSG, "table", tableName);
                } else {
                    return true;
                }
            }

            CommonUITool.showErrorBaloon(tableNameComp, tableNameText, errorBaloon, "", msg);
            tableNameText.setFocus();
            return false;
        }

        return true;
    }

    /**
     * After a task has been executed, do some thing such as refresh.
     *
     * @param task the task
     * @return IStatus if complete refresh false if run into error
     */
    public IStatus postTaskFinished(ITask task) {
        return Status.OK_STATUS;
    }

    public void completeAll() {
        CommonUITool.openInformationBox(
                Messages.titleSuccess, Messages.bind(Messages.msgNull2DefComplete, jobName));
        super.close();
    }

    public boolean isDirty() {
        return false;
    }

    public boolean isSaveAsAllowed() {
        return false;
    }

    public void setFocus() {
        if (isNewTableFlag) {
            tableNameText.setFocus();
        } else {
            okBtn.setFocus();
        }
    }

    public void nodeChanged(CubridNodeChangedEvent event) {
        ICubridNode cubridNode = event.getCubridNode();
        CubridNodeChangedEventType eventType = event.getType();
        if (cubridNode == null || eventType == null) {
            return;
        }

        if (CubridNodeChangedEventType.SERVER_DISCONNECTED.equals(event.getType())) {
            super.close();
        }

        if (CubridNodeChangedEventType.DATABASE_LOGOUT.equals(event.getType())
                || CubridNodeChangedEventType.DATABASE_STOP.equals(event.getType())) {
            super.close();
        }
    }

    public String getColumnProperty(int index) {
        if (index >= 0 && index < columnProperites.length) {
            return columnProperites[index];
        }
        return "";
    }

    public SchemaInfo getNewSchemaInfo() {
        return newERTable.getSchemaInfo();
    }

    public SchemaInfo getOldSchemaInfo() {
        return oldSchemaInfo;
    }

    public ERTable getOldERTable() {
        return oldERTable;
    }

    public ERTable getNewERTable() {
        return newERTable;
    }

    /** @return the deletedColumns */
    public Set<String> getDeletedColumns() {
        return deletedColumns;
    }

    /** @return the modifiedColumns */
    public Map<String, String> getModifiedColumns() {
        return modifiedColumns;
    }

    /** @return the addedColumns */
    public Set<String> getAddedColumns() {
        return addedColumns;
    }

    public boolean isNewTableFlag() {
        return isNewTableFlag;
    }

    public boolean isSupportChange() {
        return isSupportChange;
    }

    /**
     * Get collation array
     *
     * @return
     */
    public String[] getCollationArray() { // FIXME move this logic to core module
        if (collationList == null) {
            buildVirtualDBCollationList();
        }
        String[] array = new String[collationList.size()];
        for (int i = 0; i < collationList.size(); i++) {
            array[i] = collationList.get(i).getName();
        }

        return array;
    }

    /**
     * Get collation index by name
     *
     * @param collationName
     * @return
     */
    public int getCollationIndex(String collationName) { // FIXME move this logic to core module
        if (collationList == null) {
            return -1;
        }

        for (int i = 0; i < collationList.size(); i++) {
            if (StringUtil.isEqualNotIgnoreNull(collationName, collationList.get(i).getName())) {
                return i;
            }
        }

        return -1;
    }

    /** @return the columnsTable */
    public Table getColumnsTable() {
        return columnsTable;
    }

    /**
     * Remove temporary ER column by name
     *
     * @param name physical or logical name
     */
    public void removeTmpElementByName(String name) { // FIXME move this logic to core module
        List<ERTableColumn> removeList = new ArrayList<ERTableColumn>();
        for (ERTableColumn col : tempERColumnList) {
            if (!StringUtil.isEmpty(name)
                    && StringUtil.isEqual(name, col.getName(erSchema.isPhysicModel()))) {
                removeList.add(col);
            }
        }
        tempERColumnList.removeAll(removeList);
    }

    /**
     * When new a column, the old column is null or "" name column
     *
     * @param oldName old physical or logical column name
     * @param newName new physical or logical column name
     */
    public void afterModifyColumn(String oldName, String newName) {

        if (StringUtil.isEmpty(newName)) {
            return;
        }

        // add new
        if (StringUtil.isEmpty(oldName)) {
            addedColumns.add(newName);
            return;
        }

        if (addedColumns.contains(oldName)) {
            addedColumns.remove(oldName);
            addedColumns.add(newName);
            return;
        }

        // as: "a"->"b", then "b"->"c", the key-value should be updated to "a"->"c"!
        Set<String> keys = modifiedColumns.keySet();
        for (String key : keys) {
            if (StringUtil.isEqual(modifiedColumns.get(key), oldName)) {
                modifiedColumns.remove(key);
                modifiedColumns.put(key, newName);
                return;
            }
        }

        modifiedColumns.put(oldName, newName);

        // valid
        Iterator<String> it = addedColumns.iterator();
        while (it.hasNext()) {
            String name = it.next();
            if (newERTable.getColumn(name, isPhysical) == null) {
                it.remove();
            }
        }
        Iterator<Entry<String, String>> entryIt = modifiedColumns.entrySet().iterator();
        while (entryIt.hasNext()) {
            String name = entryIt.next().getValue();
            if (newERTable.getColumn(name, isPhysical) == null) {
                entryIt.remove();
            }
        }
    }

    /**
     * @param oldAttrName old physical column name
     * @param newAttrName new physical column name
     * @return void
     */
    private void afterModifyAttr(String oldAttrName, String newAttrName) {
        if (isPhysical) {
            afterModifyColumn(oldAttrName, newAttrName);
        } else {
            ERTableColumn oldColumn = oldERTable.getColumn(oldAttrName);
            ERTableColumn newColumn = newERTable.getColumn(newAttrName);
            String oldName = oldColumn.getName(false);
            String newName = newColumn.getName(false);
            afterModifyColumn(oldName, newName);
        }
    }

    /**
     * physical or logical column name
     *
     * @param colName
     * @return void
     */
    public void afterDeleteColumn(String colName) {
        if (StringUtil.isEmpty(colName)) {
            return;
        }

        if (addedColumns.contains(colName)) {
            addedColumns.remove(colName);
            return;
        }

        Set<String> keys = modifiedColumns.keySet();
        for (String key : keys) {
            if (StringUtil.isEqual(modifiedColumns.get(key), colName)) {
                modifiedColumns.remove(key);
                deletedColumns.add(key);
                return;
            }
        }

        if (oldERTable.getColumn(colName, isPhysical) == null) {
            LOGGER.debug("Can not find the deleted column : " + colName);
            return;
        }
        deletedColumns.add(colName);
    }

    public boolean isLogicalViewModel() {
        return !erSchema.isPhysicModel();
    }

    /** @return the showDefaultType */
    public int getShowDefaultType() {
        return showDefaultType;
    }

    /**
     * Set to show default tab
     *
     * @param showDefaultType the showDefaultType to set
     */
    public void setShowDefaultType(int showDefaultType) {
        this.showDefaultType = showDefaultType;
    }

    public ERSchema getErSchema() {
        return erSchema;
    }

    public void setErSchema(ERSchema erSchema) {
        this.erSchema = erSchema;
    }

    private boolean isSupportUserSchema() {
        if (database != null) {
            return database.getDatabaseInfo().isSupportUserSchema();
        }
        return false;
    }
}
