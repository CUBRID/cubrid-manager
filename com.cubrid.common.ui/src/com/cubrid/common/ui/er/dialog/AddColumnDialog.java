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

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.er.Messages;
import com.cubrid.common.ui.er.model.ERSchema;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Add a simple Column for ERD
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-8-12 created by Yu Guojia
 */
public class AddColumnDialog extends CMTitleAreaDialog {
    private String columnValue = "";
    private String dataTypeValue = "";
    private List<String> columnList;
    private Text newColumnText = null;
    private Combo dataTypeCombo;
    private String newColumnName;
    private String tableOrViewKey;
    private String tableOrViewName;
    private ERSchema erSchema;

    public AddColumnDialog(
            Shell parentShell,
            String tableOrViewName,
            boolean isTable,
            List<String> columnList,
            ERSchema es) {
        super(parentShell);
        this.columnList = columnList;
        this.tableOrViewName = tableOrViewName;
        this.erSchema = es;
        if (isTable) {
            tableOrViewKey = Messages.keyTable;
        } else {
            tableOrViewKey = Messages.keyView;
        }
    }

    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setSize(420, 255);
        CommonUITool.centerShell(getShell());
        getShell()
                .setText(
                        Messages.titleAddColumn
                                + ", "
                                + tableOrViewKey
                                + "\""
                                + erSchema.getTable(tableOrViewName).getShownName()
                                + "\"");
    }

    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.okBTN, false);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.cancelBTN, false);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
    }

    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            newColumnName = newColumnText.getText().trim();
            dataTypeValue = dataTypeCombo.getText().trim();
        }
        super.buttonPressed(buttonId);
    }

    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);

        Composite composite = new Composite(parentComp, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);

        // new name
        Label label1 = new Label(composite, SWT.LEFT);
        label1.setText(Messages.lbAddColumnName);
        GridData data = new GridData();
        data.horizontalSpan = 1;
        data.verticalSpan = 1;
        label1.setLayoutData(data);
        newColumnText = new Text(composite, SWT.BORDER);
        data = new GridData();
        data.horizontalSpan = 2;
        data.verticalSpan = 1;
        data.grabExcessHorizontalSpace = true;
        data.verticalAlignment = GridData.FILL;
        data.horizontalAlignment = GridData.FILL;

        // data type
        Label labelType = new Label(composite, SWT.LEFT);
        labelType.setText(Messages.lbDataType);
        GridData data2 = new GridData();
        data2.horizontalSpan = 1;
        data2.verticalSpan = 1;
        labelType.setLayoutData(data2);
        dataTypeCombo = new Combo(composite, SWT.LEFT | SWT.READ_ONLY | SWT.BORDER);
        dataTypeCombo.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, 100, -1));
        dataTypeCombo.setItems(this.listDataTypes());
        dataTypeCombo.setText("STRING");
        dataTypeValue = dataTypeCombo.getText();

        newColumnText.setLayoutData(data);
        newColumnText.setText(columnValue);
        newColumnText.selectAll();
        newColumnText.setFocus();
        newColumnText.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        setErrorMessage(null);
                        getButton(IDialogConstants.OK_ID).setEnabled(false);
                        String newcolumn = newColumnText.getText();
                        if (!ValidateUtil.isValidIdentifier(newcolumn)) {
                            setErrorMessage(
                                    Messages.bind(
                                            Messages.errInvalidColumnName,
                                            tableOrViewKey,
                                            newcolumn));
                            return;
                        }
                        if (-1 != columnList.indexOf(newcolumn.toLowerCase(Locale.getDefault()))) {
                            setErrorMessage(Messages.bind(Messages.errExistColumnName, newcolumn));
                            return;
                        }
                        getButton(IDialogConstants.OK_ID).setEnabled(true);
                    }
                });

        newColumnText.addListener(
                SWT.KeyDown,
                new Listener() {
                    public void handleEvent(Event e) {
                        if (e.type == SWT.KeyDown && e.character == SWT.CR) {
                            /* For bug TOOLS-2698 */
                            String newSchemaName = newColumnText.getText().trim();
                            if (StringUtil.isEqualNotIgnoreNull(columnValue, newSchemaName)) {
                                buttonPressed(IDialogConstants.CANCEL_ID);
                            } else {
                                buttonPressed(IDialogConstants.OK_ID);
                            }
                        }
                    }
                });

        setTitle(Messages.bind(Messages.titleAddColumn, tableOrViewKey));
        setMessage(Messages.bind(Messages.titleAddColumn, tableOrViewKey));
        return parent;
    }

    private String[] listDataTypes() {
        if (erSchema.getCubridDatabase() == null
                || erSchema.getCubridDatabase().getDatabaseInfo() == null) {
            return new String[] {};
        }

        List<String> list = new ArrayList<String>();
        String[][] typeMapping =
                DataType.getTypeMapping(
                        erSchema.getCubridDatabase().getDatabaseInfo(), false, true);
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

    public String getNewColumnName() {
        return newColumnName;
    }

    public String getDataType() {
        return dataTypeValue;
    }
}
