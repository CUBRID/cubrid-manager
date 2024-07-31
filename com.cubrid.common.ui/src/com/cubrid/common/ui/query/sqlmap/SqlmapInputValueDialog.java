/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.query.sqlmap;

import static com.cubrid.common.core.util.StringUtil.isEmpty;
import static com.cubrid.common.core.util.StringUtil.nvl;
import static com.cubrid.common.ui.spi.util.CommonUITool.openErrorBox;

import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * SQLMaps input value dialog.
 *
 * @author CHOE JUNGYEON
 */
public class SqlmapInputValueDialog extends CMTitleAreaDialog {

    private Text txtName;
    private Text txtValue;
    private Combo cbDataTypes;

    private String name;
    private String value;
    private String type;

    public SqlmapInputValueDialog(Shell parentShell, String name, String value, String type) {
        super(parentShell);

        this.name = name;
        this.value = value;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        getShell().setText(Messages.titleSqlmapDefineBindValue);

        setTitle(Messages.msgSqlmapDefineBindValue);
        setMessage("");

        Composite comp = new Composite(parent, SWT.BORDER);
        comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        comp.setLayout(new GridLayout(2, false));

        createInputForm(comp);

        return parent;
    }

    private void createInputForm(Composite parent) {
        Label lblName = new Label(parent, SWT.NONE);
        lblName.setText(Messages.lblSqlmapInputVariableName);

        txtName = new Text(parent, SWT.BORDER);
        txtName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        if (name != null) {
            txtName.setEditable(false);
            txtName.setEnabled(false);
        }
        txtName.setText(nvl(name));

        Label lblValue = new Label(parent, SWT.NONE);
        lblValue.setText(Messages.lblSqlmapInputVariableValue);

        txtValue = new Text(parent, SWT.BORDER);
        txtValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        txtValue.setText(nvl(value));

        Label lblDataTypes = new Label(parent, SWT.NONE);
        lblDataTypes.setText(Messages.lblSqlmapInputVariableType);

        cbDataTypes = new Combo(parent, SWT.READ_ONLY);
        cbDataTypes.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        cbDataTypes.add("STRING");
        cbDataTypes.add("NUMBER");
        cbDataTypes.add("FUNCTION");

        int index = 0;
        for (String dataType : cbDataTypes.getItems()) {
            if (dataType.equals(type)) {
                cbDataTypes.select(index);
            }
            index++;
        }
        if (cbDataTypes.getSelectionIndex() == -1) {
            cbDataTypes.select(0);
        }
    }

    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            this.name = txtName.getText();
            this.value = txtValue.getText();
            this.type = cbDataTypes.getText();

            String errorMsg = null;
            if (isEmpty(name)) {
                errorMsg = Messages.msgSqlmapInputVariableName;
            } else if (isEmpty(type)) {
                errorMsg = Messages.msgSqlmapInputVariableType;
            }

            if (errorMsg != null) {
                openErrorBox(errorMsg);
                return;
            }
        }

        super.buttonPressed(buttonId);
    }
}
