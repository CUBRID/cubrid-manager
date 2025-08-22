/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search Solution.
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

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.editor.AttributeContentProvider;
import com.cubrid.common.ui.cubrid.table.editor.IAttributeColumn;
import com.cubrid.common.ui.er.ERException;
import com.cubrid.common.ui.er.Messages;
import com.cubrid.common.ui.er.editor.IPhysicalLogicalEditComposite;
import com.cubrid.common.ui.er.editor.ModelRelationCellModifier;
import com.cubrid.common.ui.er.editor.RelationMapColumnLabelProvider;
import com.cubrid.common.ui.er.logic.PhysicalLogicRelation;
import com.cubrid.common.ui.er.logic.PhysicalLogicRelation.MapType;
import com.cubrid.common.ui.er.model.ERSchema;
import com.cubrid.common.ui.er.model.ERTable;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * The dialog for setting global physical and logical model relationship.
 *
 * @author Yu Guojia
 * @version 1.0 - 2014-5-19 created by Yu Guojia
 */
public class SetPhysicalLogicaMapDialog extends CMTitleAreaDialog
        implements IPhysicalLogicalEditComposite {

    private ERSchema erSchema;
    private TableViewer columnDataTableView;
    private String[] columnProperites;
    private AttributeContentProvider attrContentProvider;
    private RelationMapColumnLabelProvider attrLabelProvider;
    /** copied structurer */
    private Map<String, String> newColumnTypeMap;

    /**
     * Create the dialog.
     *
     * @param parentShell
     */
    public SetPhysicalLogicaMapDialog(Shell parentShell, ERSchema erSchema) {
        super(parentShell);
        this.erSchema = erSchema;
        columnProperites =
                new String[] {
                    IAttributeColumn.COL_EMPTY,
                    IAttributeColumn.COL_FLAG,
                    Messages.tblcolumnPhysical,
                    Messages.tblcolumnLogical
                };
    }

    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setMinimumSize(370, 420);
        getShell().setText(Messages.namePhysicalLogicalMapDlg);
    }

    /**
     * Create contents of the dialog.
     *
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);
        Composite composite = new Composite(parentComp, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        composite.setLayout(layout);

        Label exampleLabel = new Label(composite, SWT.NONE);
        exampleLabel.setText(Messages.lblMsgPhysicalLogicalExample);
        createColumnTypeView(composite);

        setTitle(Messages.titlePhysicalLogicalMapDlg);
        setMessage(Messages.msgPhysicalLogicalMapDlg);
        return composite;
    }

    private void createColumnTypeView(Composite composite) {
        Group group = new Group(composite, SWT.NONE);
        createTableComposite(composite, group, PhysicalLogicRelation.MapType.DATATYPE);
    }

    private void createTableComposite(
            Composite composite, Group group, PhysicalLogicRelation.MapType type) {
        group.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        group.setLayout(layout);

        ScrolledComposite scrolledComposite =
                new ScrolledComposite(group, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);
        final TableViewer tableViewer =
                new TableViewer(scrolledComposite, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
        columnDataTableView = tableViewer;

        tableViewer.setColumnProperties(columnProperites);
        final Table widgetTable = tableViewer.getTable();
        scrolledComposite.setContent(widgetTable);
        scrolledComposite.setMinSize(widgetTable.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        final GridData gdFkTable = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
        widgetTable.setLayoutData(gdFkTable);
        widgetTable.setLinesVisible(true);
        widgetTable.setHeaderVisible(true);
        // empty column
        final TableColumn emptyColumn = new TableColumn(widgetTable, SWT.NONE);
        emptyColumn.setWidth(0);
        // flag column
        final TableColumn flagColumn = new TableColumn(widgetTable, SWT.LEFT_TO_RIGHT);
        flagColumn.setWidth(20);
        // physical column
        TableColumn tblCol = new TableColumn(widgetTable, SWT.NONE);
        tblCol.setWidth(120);
        tblCol.setText(Messages.tblcolumnPhysical);
        // logical column
        tblCol = new TableColumn(widgetTable, SWT.NONE);
        tblCol.setWidth(120);
        tblCol.setText(Messages.tblcolumnLogical);

        attrContentProvider = new AttributeContentProvider();
        attrLabelProvider = new RelationMapColumnLabelProvider(this);
        tableViewer.setContentProvider(attrContentProvider);
        tableViewer.setLabelProvider(attrLabelProvider);

        CellEditor[] cellEditor = new CellEditor[columnProperites.length];
        {
            int index = 0;
            cellEditor[index++] = null; // Empty
            cellEditor[index++] = null; // Flag
            cellEditor[index++] = new TextCellEditor(widgetTable); // Physical
            cellEditor[index++] = new TextCellEditor(widgetTable); // Logical
        }
        tableViewer.setCellEditors(cellEditor);
        tableViewer.setCellModifier(new ModelRelationCellModifier(this, type));
        loadTableInput(type);

        final Button delBtn = new Button(group, SWT.PUSH);
        delBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        delBtn.setText(Messages.btnDelItem);
        delBtn.setData(type);
        delBtn.setEnabled(false);
        delBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
                        deleteColumn(delBtn);
                    }
                });

        widgetTable.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        handleSelectionChangeInTable(widgetTable, delBtn);
                    }
                });
    }

    private void handleSelectionChangeInTable(Table widgetTable, Button delBtn) {
        int selectionCount = widgetTable.getSelectionCount();
        if (selectionCount > 0) {
            if (selectionCount == 1) {
                String key = widgetTable.getSelection()[0].getText(2);
                String value = widgetTable.getSelection()[0].getText(3);
                if (StringUtil.isEmpty(key) && StringUtil.isEmpty(value)) {
                    delBtn.setEnabled(false);
                    return;
                }
            }
            delBtn.setEnabled(true);
        } else {
            delBtn.setEnabled(false);
        }
    }

    private TableViewer getCurrentTableView(PhysicalLogicRelation.MapType type) {
        return columnDataTableView;
    }

    public void loadTableInput(PhysicalLogicRelation.MapType type) {
        Map<String, String> map = getMapData(type);
        if (!map.containsKey("") && !map.containsValue("")) {
            map.put("", ""); // for add new relationship
        }
        TableViewer tableViewer = getCurrentTableView(type);
        tableViewer.setInput(new ArrayList(map.entrySet()));
    }

    /**
     * Load the map data, if the map is loaded firstly. Init the map by the map in global physical
     * logical relation object.
     *
     * @param type
     * @return
     * @return Map<String,String>
     */
    public Map<String, String> getMapData(PhysicalLogicRelation.MapType type) {
        Map<String, String> map = erSchema.getPhysicalLogicRelation().getMapData(type);
        if (type.equals(MapType.DATATYPE)) {
            if (newColumnTypeMap == null) {
                newColumnTypeMap = new LinkedHashMap<String, String>(map);
            }
            return newColumnTypeMap;
        }
        return null;
    }

    public String getPropertyName(int index) {
        return columnProperites[index];
    }

    private void deleteColumn(Button delBtn) {

        if (!CommonUITool.openConfirmBox(
                com.cubrid.common.ui.cubrid.table.Messages.msgDeleteColumnConfirm)) {
            return;
        }
        PhysicalLogicRelation.MapType type = (PhysicalLogicRelation.MapType) delBtn.getData();
        TableViewer tableViewer = getCurrentTableView(type);
        TableItem[] tblItems = tableViewer.getTable().getSelection();
        if (tblItems == null || tblItems.length == 0) {
            return;
        }
        for (int i = 0; i < tblItems.length; i++) {
            String key = tblItems[i].getText(2);
            Map<String, String> mapData = getMapData(type);
            mapData.remove(key);
        }
        loadTableInput(type);
        handleSelectionChangeInTable(tableViewer.getTable(), delBtn);
    }

    /**
     * Create contents of the button bar.
     *
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    protected void buttonPressed(int buttonId) {
        super.buttonPressed(buttonId);
    }

    protected void okPressed() {
        if (!CommonUITool.openConfirmBox(Messages.msgConfirmChangePhysicalLogicalMap)) {
            return;
        }
        PhysicalLogicRelation.delEmptyEntry(newColumnTypeMap);

        // check all column physical data types whether are valid by the new column type map
        if (!erSchema.isPhysicModel()) {
            try {
                checkNewMapValid();
            } catch (ERException e) {
                CommonUITool.openErrorBox(getShell(), e.getMessage());
                return;
            }
        }

        PhysicalLogicRelation relation = erSchema.getPhysicalLogicRelation();
        relation.setDataTypeMap(newColumnTypeMap);
        erSchema.setPhysicalLogicRelation(
                relation); // refresh all columns physical/logical data type by map
        super.okPressed();
    }

    private void checkNewMapValid() throws ERException {
        ERSchema copySchema = erSchema.clone();
        PhysicalLogicRelation relation = copySchema.getPhysicalLogicRelation();
        relation.setDataTypeMap(newColumnTypeMap);
        copySchema.setPhysicalLogicRelation(
                relation); // refresh all columns physical/logical data type by map
        List<ERTable> tables = copySchema.getTables();
        for (ERTable table : tables) {
            table.checkValidate();
        }
    }
}
