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
package com.cubrid.common.ui.cubrid.table.dialog;

import com.cubrid.common.core.common.model.Constraint;
import com.cubrid.common.core.common.model.Constraint.ConstraintType;
import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import com.cubrid.cubridmanager.core.cubrid.table.model.SuperClassUtil;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
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
import org.eclipse.swt.widgets.Text;

/**
 * The dialog of Set PK
 *
 * @author pangqiren 2009-6-4
 */
public class SetPKDialog extends CMTitleAreaDialog {

    private Group group = null;
    private Text pkNameText = null;
    private Table constraintTable = null;
    private Button upBtn = null;
    private Button downBtn = null;
    private final Color black;
    private final Color gray;

    SchemaInfo schema;
    private Constraint oldPK;
    private Constraint newPK = null;
    private String operation = null;
    private boolean isNewTable = false;
    private Button changeNameButton = null;

    /**
     * @param parentShell
     * @param newSchema
     * @param database
     */
    public SetPKDialog(
            Shell parentShell, CubridDatabase database, SchemaInfo schema, boolean isNewTable) {
        super(parentShell);
        this.schema = schema;
        List<SchemaInfo> supers =
                SuperClassUtil.getSuperClasses(database.getDatabaseInfo(), schema);
        oldPK = schema.getPK(supers);
        if (oldPK == null || oldPK.getAttributes() == null || oldPK.getAttributes().size() == 0) {
            oldPK = null;
        }
        black = ResourceManager.getColor(0, 0, 0);
        gray = ResourceManager.getColor(128, 128, 128);
        this.isNewTable = isNewTable;
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

        group = new Group(composite, SWT.NONE);
        {
            group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
            GridLayout gridLayout = new GridLayout();
            gridLayout.numColumns = 3;
            group.setLayout(gridLayout);
        }

        Label pkNameLabel = new Label(group, SWT.NONE);
        pkNameLabel.setText(Messages.lblPKName);
        pkNameText = new Text(group, SWT.BORDER);
        {
            pkNameText.setTextLimit(ValidateUtil.MAX_SCHEMA_NAME_LENGTH);
            GridData gridData = new GridData();
            gridData.horizontalAlignment = GridData.FILL;
            gridData.grabExcessHorizontalSpace = true;
            gridData.verticalAlignment = GridData.CENTER;
            pkNameText.setLayoutData(gridData);
        }
        pkNameText.setEnabled(false);

        changeNameButton = new Button(group, SWT.CHECK);
        changeNameButton.setText(Messages.btnChangeName);
        changeNameButton.addSelectionListener(
                new SelectionListener() {
                    public void widgetDefaultSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetSelected(SelectionEvent e) {
                        if (changeNameButton.getSelection()) {
                            pkNameText.setEnabled(true);
                        } else {
                            pkNameText.setEnabled(false);
                        }
                    }
                });

        Label colInfoLabel = new Label(group, SWT.NONE);
        {
            colInfoLabel.setText(Messages.lblSelectColumns);
            GridData gridData6 = new GridData();
            gridData6.verticalSpan = 1;
            gridData6.horizontalSpan = 3;
            colInfoLabel.setLayoutData(gridData6);
        }
        createTable();
        createComposite();

        setInfo();
        setTitle(Messages.msgTitleSetPK);
        setMessage(Messages.msgSetPK);
        return parentComp;
    }

    /** @see com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog#constrainShellSize() */
    protected void constrainShellSize() {
        super.constrainShellSize();
        String msg = Messages.bind(Messages.titleSetPK, schema.getClassname());
        getShell().setText(msg);
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     * @param parent the button bar composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.btnOK, false);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.btnCancel, false);
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     * @param buttonId the id of the button that was pressed (see <code>IDialogConstants.*_ID</code>
     *     constants)
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) { // FIXME move this logic to core module
            setErrorMessage(null);

            List<String> selected = new ArrayList<String>();

            for (int i = 0, n = constraintTable.getItemCount(); i < n; i++) {
                if (constraintTable.getItem(i).getChecked()) {
                    String columnName = constraintTable.getItem(i).getText(1);
                    selected.add(columnName);
                }
            }

            String pkName = null;
            if (changeNameButton.getSelection()) {
                pkName = pkNameText.getText().trim();
                if (pkName.length() == 0) {
                    setErrorMessage(Messages.errPKNameEmpty);
                    return;
                }
            } else {
                StringBuffer pkNameSB = new StringBuffer();
                pkNameSB.append("pk_");
                pkNameSB.append(schema.getClassname() == null ? "" : schema.getClassname());
                for (int i = 0, n = constraintTable.getItemCount(); i < n; i++) {
                    if (constraintTable.getItem(i).getChecked()) {
                        String columnName = constraintTable.getItem(i).getText(1);
                        pkNameSB.append("_" + columnName);
                    }
                }
                pkName = pkNameSB.toString();
            }

            newPK = new Constraint(true);
            newPK.setName(pkName);
            newPK.setType(ConstraintType.PRIMARYKEY.getText()); // $NON-NLS-1$
            for (String s : selected) {
                newPK.addAttribute(s);
            }

            boolean isNew = false;
            if (oldPK == null && !selected.isEmpty()) {
                isNew = true;
                operation = "ADD"; // $NON-NLS-1$
            } else if (oldPK != null && selected.isEmpty()) {
                operation = "DEL"; // $NON-NLS-1$
                isNew = false;
            } else if (oldPK != null && !selected.isEmpty()) {
                isNew =
                        oldPK.getAttributes().equals(selected)
                                        && StringUtil.isEqualNotIgnoreNull(
                                                oldPK.getName(), newPK.getName())
                                ? false
                                : true;
                if (isNew) {
                    operation = "MODIFY"; // $NON-NLS-1$
                } else {
                    operation = "NO Change"; // $NON-NLS-1$
                }
            }
        }
        super.buttonPressed(buttonId);
    }

    /** This method initializes table */
    private void createTable() {
        constraintTable =
                new Table(group, SWT.FULL_SELECTION | SWT.BORDER | SWT.SINGLE | SWT.CHECK);
        {
            constraintTable.setHeaderVisible(true);
            GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 2);
            gridData.heightHint = 140;
            constraintTable.setLayoutData(gridData);
            constraintTable.setLinesVisible(true);
        }
        CommonUITool.hackForYosemite(constraintTable);

        TableColumn tblcol = new TableColumn(constraintTable, SWT.LEFT);
        tblcol.setText(Messages.tblColUseColumn);
        tblcol = new TableColumn(constraintTable, SWT.LEFT);
        tblcol.setText(Messages.tblColColumnName);
        tblcol = new TableColumn(constraintTable, SWT.LEFT);
        tblcol.setText(Messages.tblColDataType);

        constraintTable.addSelectionListener(
                new org.eclipse.swt.events.SelectionAdapter() {
                    public void widgetSelected(org.eclipse.swt.events.SelectionEvent event) {
                        if (event.detail == SWT.CHECK) {
                            TableItem item = (TableItem) event.item;
                            if (item.getChecked()) {
                                item.setForeground(black);
                            } else {
                                item.setForeground(gray);
                            }
                            constraintTable.setSelection(new TableItem[] {item});
                        }
                        setBtnEnable();
                    }
                });
    }

    /** This method initializes composite */
    private void createComposite() {

        Composite groupComposite = new Composite(group, SWT.NONE);
        {
            GridData gridData = new GridData();
            gridData.horizontalAlignment = GridData.FILL;
            gridData.horizontalSpan = 2;
            gridData.grabExcessHorizontalSpace = true;
            gridData.verticalAlignment = GridData.CENTER;
            groupComposite.setLayoutData(gridData);
            GridLayout gridLayout = new GridLayout();
            gridLayout.numColumns = 2;
            groupComposite.setLayout(gridLayout);
        }

        upBtn = new Button(groupComposite, SWT.NONE);
        {
            upBtn.setText(Messages.btnUp);
            GridData gridData = new GridData();
            gridData.horizontalAlignment = GridData.END;
            gridData.grabExcessHorizontalSpace = true;
            gridData.verticalAlignment = GridData.CENTER;
            upBtn.setLayoutData(gridData);
        }
        upBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(org.eclipse.swt.events.SelectionEvent event) {
                        if (constraintTable.getSelectionCount() < 1) {
                            return;
                        }

                        int selectionIndex = constraintTable.getSelectionIndex();
                        if (selectionIndex == 0) {
                            return;
                        }

                        boolean tmpCheck;
                        String tmpName, tmpDomain;
                        Color tmpColor;
                        TableItem selectedItem = constraintTable.getSelection()[0];
                        TableItem targetItem = constraintTable.getItem(selectionIndex - 1);
                        tmpCheck = targetItem.getChecked();
                        tmpName = targetItem.getText(1);
                        tmpDomain = targetItem.getText(2);
                        tmpColor = targetItem.getForeground();
                        targetItem.setChecked(selectedItem.getChecked());
                        targetItem.setText(1, selectedItem.getText(1));
                        targetItem.setText(2, selectedItem.getText(2));
                        targetItem.setForeground(selectedItem.getForeground());
                        selectedItem.setChecked(tmpCheck);
                        selectedItem.setText(1, tmpName);
                        selectedItem.setText(2, tmpDomain);
                        selectedItem.setForeground(tmpColor);
                        constraintTable.setSelection(selectionIndex - 1);
                        setBtnEnable();
                    }
                });

        downBtn = new Button(groupComposite, SWT.NONE);
        {
            downBtn.setText(Messages.btnDown);
            GridData gridData = new GridData();
            gridData.grabExcessHorizontalSpace = true;
            downBtn.setLayoutData(gridData);
        }
        downBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(org.eclipse.swt.events.SelectionEvent event) {
                        if (constraintTable.getSelectionCount() < 1) {
                            return;
                        }

                        int selectionIndex = constraintTable.getSelectionIndex();
                        if (selectionIndex == constraintTable.getItemCount() - 1) {
                            return;
                        }

                        boolean tmpCheck;
                        String tmpName, tmpDomain;
                        Color tmpColor;
                        TableItem selectedItem = constraintTable.getSelection()[0];
                        TableItem targetItem = constraintTable.getItem(selectionIndex + 1);
                        tmpCheck = targetItem.getChecked();
                        tmpName = targetItem.getText(1);
                        tmpDomain = targetItem.getText(2);
                        tmpColor = targetItem.getForeground();
                        targetItem.setChecked(selectedItem.getChecked());
                        targetItem.setText(1, selectedItem.getText(1));
                        targetItem.setText(2, selectedItem.getText(2));
                        targetItem.setForeground(selectedItem.getForeground());
                        selectedItem.setChecked(tmpCheck);
                        selectedItem.setText(1, tmpName);
                        selectedItem.setText(2, tmpDomain);
                        selectedItem.setForeground(tmpColor);
                        constraintTable.setSelection(selectionIndex + 1);
                        setBtnEnable();
                    }
                });
    }

    /** Set button to enabled */
    private void setBtnEnable() {
        if (constraintTable.getSelectionCount() > 0) {
            downBtn.setEnabled(true);
            upBtn.setEnabled(true);
        } else {
            downBtn.setEnabled(false);
            upBtn.setEnabled(false);
        }

        if (constraintTable.getSelectionIndex() <= 0) {
            upBtn.setEnabled(false);
        }

        if (constraintTable.getSelectionIndex() >= constraintTable.getItemCount() - 1) {
            downBtn.setEnabled(false);
        }
    }

    /** Set information */
    private void setInfo() { // FIXME move this logic to core module
        List<String> pkColumns = new ArrayList<String>();
        if (oldPK != null) {
            pkNameText.setText(oldPK.getName());
            pkColumns.addAll(oldPK.getAttributes());
            List<TableItem> pkTableItem = new ArrayList<TableItem>();
            for (String s : pkColumns) {
                DBAttribute da = schema.getDBAttributeByName(s, false);
                if (da == null) {
                    continue;
                }
                TableItem item = new TableItem(constraintTable, SWT.NONE);
                item.setText(1, da.getName());
                item.setText(2, da.getType());
                item.setForeground(this.black);
                item.setChecked(true);
                pkTableItem.add(item);
            }
            constraintTable.setSelection(pkTableItem.toArray(new TableItem[pkTableItem.size()]));
        }

        List<DBAttribute> list = schema.getAttributes();
        for (int i = 0, n = list.size(); i < n; i++) {
            DBAttribute da = list.get(i);
            if (!pkColumns.contains(da.getName())
                    && da.getInherit().equals(schema.getClassname())) {
                TableItem item = new TableItem(constraintTable, SWT.NONE);
                item.setText(1, da.getName());
                item.setText(2, DataType.getShownType(da.getType()));
                item.setForeground(gray);
            }
        }

        for (int i = 0, n = constraintTable.getColumnCount(); i < n; i++) {
            constraintTable.getColumn(i).pack();
        }

        setBtnEnable();
    }

    public Constraint getOldPK() {
        return oldPK;
    }

    public Constraint getNewPK() {
        return newPK;
    }

    public String getOperation() {
        return operation;
    }
}
