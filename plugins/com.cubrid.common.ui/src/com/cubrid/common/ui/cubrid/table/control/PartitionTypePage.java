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

import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.PartitionInfo;
import com.cubrid.common.core.common.model.PartitionType;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.PartitionUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.TableViewUtil;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.DBAttributeStatistic;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetPartitionedClassListTask;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

/**
 * Partition type wizard page
 *
 * @author pangqiren
 * @version 1.0 - 2010-3-16 created by pangqiren
 */
public class PartitionTypePage extends WizardPage implements ModifyListener {

    public static final String PAGENAME = "CreatePartitionWizard/PartitionTypePage";

    private final DatabaseInfo dbInfo;
    private final SchemaInfo schemaInfo;
    private final boolean isNewTable;
    private Combo partitionTypeCombo = null;
    private Table attributeTable = null;
    private Text partitionExprText = null;
    private Button useExprButton;
    private Button useColumnButton;
    private PartitionInfo editedPartitionInfo = null;
    private final List<DBAttribute> attrList = new ArrayList<DBAttribute>();

    private TableViewer attrTableView;

    protected PartitionTypePage(DatabaseInfo dbInfo, SchemaInfo schemaInfo, boolean isNewTable) {
        super(PAGENAME);
        this.dbInfo = dbInfo;
        this.schemaInfo = schemaInfo;
        this.isNewTable = isNewTable;
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @param parent the parent composite
     */
    public void createControl(Composite parent) {

        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 10;
        layout.marginWidth = 10;
        composite.setLayout(layout);

        GridData gridData = new GridData(GridData.FILL_BOTH);
        composite.setLayoutData(gridData);

        createTypeCombo(composite);
        createExprGroup(composite);
        init();
        setTitle(Messages.titleTypePage);
        setMessage(Messages.msgTypePage);
        setControl(composite);
    }

    /** Initial the page content */
    private void init() {
        if (editedPartitionInfo == null) {
            final PartitionType[] partTypes =
                    new PartitionType[] {
                        PartitionType.RANGE, PartitionType.LIST, PartitionType.HASH
                    };
            for (int i = 0; i < partTypes.length; i++) {
                partitionTypeCombo.add(partTypes[i].getText().toUpperCase());
            }
            partitionTypeCombo.select(0);

            useColumnButton.setSelection(true);
            attributeTable.setEnabled(true);

            useExprButton.setSelection(false);
            partitionExprText.setEnabled(false);
            setPageComplete(false);
        } else {
            partitionTypeCombo.add(editedPartitionInfo.getPartitionType().getText().toUpperCase());
            partitionTypeCombo.select(0);

            useColumnButton.setSelection(false);
            attributeTable.setEnabled(false);

            String expr = editedPartitionInfo.getPartitionExpr();
            expr = formatPartitionExpr(expr);
            for (int i = 0; i < attrList.size(); i++) {
                String name = attrList.get(i).getName();
                if (expr.trim().equals(name)) {
                    attrTableView.setSelection(new StructuredSelection(attrList.get(i)));
                    useColumnButton.setSelection(true);
                    attributeTable.setEnabled(true);
                    break;
                }
            }
            if (useColumnButton.getSelection()) {
                useExprButton.setSelection(false);
                partitionExprText.setText("");
                partitionExprText.setEnabled(false);
            } else {
                useExprButton.setSelection(true);
                partitionExprText.setText(expr);
                partitionExprText.setEnabled(true);
                partitionExprText.setFocus();
            }
            setPageComplete(true);
        }
        partitionTypeCombo.addModifyListener(this);
        partitionExprText.addModifyListener(this);
    }

    /**
     * Create type combo
     *
     * @param parent Composite
     */
    private void createTypeCombo(Composite parent) {

        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginHeight = 10;
        layout.marginWidth = 10;
        composite.setLayout(layout);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        composite.setLayoutData(gridData);

        final Label typeLabel = new Label(composite, SWT.NONE);
        typeLabel.setText(Messages.lblPartitionType);
        typeLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        partitionTypeCombo = new Combo(composite, SWT.READ_ONLY);
        partitionTypeCombo.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
    }

    /**
     * Create the expression group
     *
     * @param parent the parent composite
     */
    private void createExprGroup(Composite parent) {

        Group partitionExprGroup = new Group(parent, SWT.NONE);
        partitionExprGroup.setText(Messages.grpPartitionExpr);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        partitionExprGroup.setLayoutData(gridData);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        partitionExprGroup.setLayout(layout);

        createAttrTable(partitionExprGroup);
        createExprComp(partitionExprGroup);
    }

    /**
     * Create attribute table information
     *
     * @param parent Composite
     */
    private void createAttrTable(Composite parent) {

        useColumnButton = new Button(parent, SWT.RADIO | SWT.LEFT);
        useColumnButton.setText(Messages.btnUseColumn);
        useColumnButton.setLayoutData(CommonUITool.createGridData(2, 1, -1, -1));
        useColumnButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (useColumnButton.getSelection()) {
                            useExprButton.setSelection(false);
                            partitionExprText.setText("");
                            partitionExprText.setEnabled(false);
                            setErrorMessage(Messages.errNoSelectColumn);
                            setPageComplete(false);
                            attributeTable.setEnabled(true);
                        }
                    }
                });

        List<DBAttributeStatistic> dbAttrStatList = null;
        if (!isNewTable) {
            GetPartitionedClassListTask task = new GetPartitionedClassListTask(dbInfo);
            dbAttrStatList = task.getColumnStatistics(schemaInfo.getUniqueName());
        }
        attrList.addAll(schemaInfo.getAttributes());
        for (int i = 0; dbAttrStatList != null && i < dbAttrStatList.size(); i++) {
            DBAttributeStatistic dbAttributeStatics = dbAttrStatList.get(i);
            for (int j = 0; j < attrList.size(); j++) {
                DBAttribute dbAttribute = attrList.get(j);
                if (dbAttribute.getName().equals(dbAttributeStatics.getName())) {
                    attrList.remove(j);
                    attrList.add(dbAttributeStatics);
                    break;
                }
            }
        }

        attrTableView =
                new TableViewer(
                        parent,
                        SWT.FULL_SELECTION | SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);

        attributeTable = attrTableView.getTable();
        attributeTable.setLayout(
                TableViewUtil.createTableViewLayout(new int[] {20, 20, 20, 20, 20}));
        attributeTable.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, 200));

        attributeTable.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        validate();
                    }
                });
        attributeTable.setHeaderVisible(true);
        attributeTable.setLinesVisible(true);

        TableViewUtil.createTableColumn(attributeTable, SWT.CENTER, Messages.tblColColumnName);
        TableViewUtil.createTableColumn(attributeTable, SWT.CENTER, Messages.tblColDataType);
        TableViewUtil.createTableColumn(attributeTable, SWT.CENTER, Messages.tblColMiniValue);
        TableViewUtil.createTableColumn(attributeTable, SWT.CENTER, Messages.tblColMaxValue);
        TableViewUtil.createTableColumn(attributeTable, SWT.CENTER, Messages.tblColValueCount);

        attrTableView.setContentProvider(new PartitionTypeContentProvider());
        attrTableView.setLabelProvider(new PartitionTypeLabelProvider());
        attrTableView.setInput(attrList);
    }

    /**
     * Create the expression composite
     *
     * @param parent the parent composite
     */
    private void createExprComp(Composite parent) {

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        composite.setLayout(layout);

        useExprButton = new Button(composite, SWT.RADIO | SWT.LEFT);
        useExprButton.setText(Messages.btnUseExpr);
        useExprButton.setLayoutData(CommonUITool.createGridData(2, 1, -1, -1));
        useExprButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (useExprButton.getSelection()) {
                            useColumnButton.setSelection(false);
                            attributeTable.setEnabled(false);
                            partitionExprText.setEnabled(true);
                            setPageComplete(false);
                            setErrorMessage(Messages.errNoExpression);
                        }
                    }
                });

        Label exprLabel = new Label(composite, SWT.NONE);
        exprLabel.setText(Messages.lblPartitionExpr);
        exprLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        partitionExprText = new Text(composite, SWT.BORDER);
        partitionExprText.setTextLimit(ValidateUtil.MAX_SCHEMA_NAME_LENGTH);
        partitionExprText.setTextLimit(ValidateUtil.MAX_SCHEMA_NAME_LENGTH);
        partitionExprText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
    }

    /**
     * Check the page content validity
     *
     * @return <code>true</code> if valid;otherwise <code>false</code>
     */
    private boolean validate() {
        if (useColumnButton.getSelection()
                && (attributeTable.getItemCount() == 0
                        || attributeTable.getSelectionCount() == 0)) {
            setErrorMessage(Messages.errNoSelectColumn);
            setPageComplete(false);
            return false;
        }

        if (useExprButton.getSelection()
                && StringUtil.isEmpty(partitionExprText.getText().trim())) {
            setErrorMessage(Messages.errInvalidExpr);
            setPageComplete(false);
            return false;
        }
        setErrorMessage(null);
        setPageComplete(true);
        return true;
    }

    /**
     * When modify text and check the information validity
     *
     * @param event the modify event
     */
    public void modifyText(ModifyEvent event) {
        validate();
    }

    /**
     * Get Partition Type
     *
     * @return string
     */
    public String getPartitionType() {
        int selectedIndex = this.partitionTypeCombo.getSelectionIndex();
        return this.partitionTypeCombo.getItem(selectedIndex);
    }

    /**
     * Get partition expression
     *
     * @return the string
     */
    public String getPartitionExpr() {
        if (useColumnButton.getSelection()) {
            return attributeTable.getSelection()[0].getText(0);
        } else {
            return partitionExprText.getText();
        }
    }

    /**
     * Get partition expression data type
     *
     * @return the string
     */
    public String getPartitionExprDataType() {
        if (useColumnButton.getSelection()) {
            return attributeTable.getSelection()[0].getText(1);
        } else {
            String expr = getPartitionExpr();
            for (int i = 0; i < attrList.size(); i++) {
                String name = attrList.get(i).getName();
                if (expr.trim().equals(name)) {
                    return attrList.get(i).getType();
                }
            }
            return null;
        }
    }

    public void setEditedPartitionInfo(PartitionInfo editedPartitionInfo) {
        this.editedPartitionInfo = editedPartitionInfo;
    }

    protected String formatPartitionExpr(String expr) {
        if (expr == null) {
            return expr;
        }
        String result = null;
        if (expr.startsWith("[") && expr.endsWith("]")) {
            result = expr.substring(1, expr.length() - 1);
        } else {
            result = expr;
        }
        return result;
    }

    /** Partition Type Content Provider */
    private static final class PartitionTypeContentProvider implements IStructuredContentProvider {
        /**
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         * @param element the input element
         * @return the array of elements to display in the viewer
         */
        @SuppressWarnings("unchecked")
        public Object[] getElements(Object element) {
            List<DBAttribute> attrList = new ArrayList<DBAttribute>();
            for (DBAttribute attr : (List<DBAttribute>) element) {
                if (PartitionUtil.isMatchType(attr.getType())) {
                    attrList.add(attr);
                }
            }
            return attrList.toArray(new DBAttribute[attrList.size()]);
        }

        /** @see org.eclipse.jface.viewers.IContentProvider#dispose() */
        public void dispose() {
            // empty
        }

        /**
         * @see
         *     org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
         *     java.lang.Object, java.lang.Object)
         * @param viewer the viewer
         * @param oldInput the old input element, or <code>null</code> if the viewer did not
         *     previously have an input
         * @param newInput the new input element, or <code>null</code> if the viewer does not have
         *     an input
         */
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            // empty
        }
    }

    /** Partition Type Label Provider */
    private static final class PartitionTypeLabelProvider implements ITableLabelProvider {
        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         * @param element the object representing the entire row, or <code>null</code> indicating
         *     that no input object is set in the viewer
         * @param columnIndex the zero-based index of the column in which the label appears
         * @return String or or <code>null</code> if there is no text for the given object at
         *     columnIndex
         */
        public String getColumnText(Object element, int columnIndex) {

            DBAttribute attr = (DBAttribute) element;
            switch (columnIndex) {
                case 0:
                    return attr.getName();
                case 1:
                    return attr.getType();
                case 2:
                    if (attr instanceof DBAttributeStatistic) {
                        return ((DBAttributeStatistic) attr).getMinValue();
                    } else {
                        return null;
                    }
                case 3:
                    if (attr instanceof DBAttributeStatistic) {
                        return ((DBAttributeStatistic) attr).getMaxValue();
                    } else {
                        return null;
                    }
                case 4:
                    if (attr instanceof DBAttributeStatistic) {
                        return String.valueOf(
                                ((DBAttributeStatistic) attr).getValueDistinctCount());
                    } else {
                        return null;
                    }
                default:
                    break;
            }
            return null;
        }

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         * @param element the object representing the entire row, or <code>null</code> indicating
         *     that no input object is set in the viewer
         * @param columnIndex the zero-based index of the column in which the label appears
         * @return Image or <code>null</code> if there is no image for the given object at
         *     columnIndex
         */
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        /**
         * @see
         *     org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
         * @param listener a label provider listener
         */
        public void addListener(ILabelProviderListener listener) {
            // empty
        }

        /** @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose() */
        public void dispose() {
            // empty
        }

        /**
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object,
         *     java.lang.String)
         * @param element the element
         * @param property the property
         * @return <code>true</code> if the label would be affected, and <code>false</code> if it
         *     would be unaffected
         */
        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        /**
         * @see
         *     org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
         * @param listener a label provider listener
         */
        public void removeListener(ILabelProviderListener listener) {
            // empty
        }
    }
}
