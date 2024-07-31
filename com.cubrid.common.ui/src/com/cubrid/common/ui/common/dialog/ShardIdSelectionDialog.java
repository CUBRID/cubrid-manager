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
package com.cubrid.common.ui.common.dialog;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ShardIdSelectionDialog extends CMTitleAreaDialog {
    private int shardId = 0;
    private int shardVal = 0;
    private int shardQueryType = DatabaseInfo.SHARD_QUERY_TYPE_VAL;
    private DatabaseInfo databaseInfo;
    private Text txtShardId;
    private Text txtShardVal;
    private Button btnUseShardId;
    private Button btnUseShardVal;

    public DatabaseInfo getDatabaseInfo() {
        return databaseInfo;
    }

    public void setDatabaseInfo(DatabaseInfo databaseInfo) {
        this.databaseInfo = databaseInfo;
    }

    public ShardIdSelectionDialog(Shell parentShell) {
        super(parentShell);
    }

    public int getShardId() {
        return shardId;
    }

    public void setShardId(int shardId) {
        this.shardId = shardId;
    }

    public int getShardVal() {
        return shardVal;
    }

    public void setShardVal(int shardVal) {
        this.shardVal = shardVal;
    }

    public int getShardQueryType() {
        return shardQueryType;
    }

    public void setShardQueryType(int shardQueryType) {
        this.shardQueryType = shardQueryType;
    }

    /**
     * Create dialog area content
     *
     * @param parent the parent composite
     * @return the control
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);
        Composite composite = new Composite(parentComp, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        {
            GridLayout layout = new GridLayout();
            layout.numColumns = 1;
            layout.marginHeight = 5;
            layout.marginWidth = 5;
            layout.verticalSpacing = 5;
            layout.horizontalSpacing = 5;
            composite.setLayout(layout);
        }

        setTitle(Messages.titleChooseShardIdDialog);
        setMessage(Messages.msgChooseShardIdDialog);

        Composite inputComposite = new Composite(composite, SWT.NONE);
        inputComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        {
            GridLayout layout = new GridLayout();
            layout.numColumns = 3;
            layout.marginHeight = 10;
            layout.marginWidth = 5;
            layout.verticalSpacing = 10;
            layout.horizontalSpacing = 5;
            inputComposite.setLayout(layout);
        }

        // SHARD ID
        btnUseShardId = new Button(inputComposite, SWT.RADIO);
        btnUseShardId.setText(Messages.btnUseShardIdHint);

        txtShardId = new Text(inputComposite, SWT.BORDER);
        txtShardId.setText(String.valueOf(shardId));
        txtShardId.setLayoutData(CommonUITool.createGridData(GridData.BEGINNING, 1, 1, 100, -1));
        if (shardQueryType == DatabaseInfo.SHARD_QUERY_TYPE_ID) {
            // txtShardVal.setEnabled(false);
            // txtShardId.setEnabled(true);
            txtShardId.selectAll();
        }

        final Label lblHintId = new Label(inputComposite, SWT.NONE);
        lblHintId.setText("/*+shard_id(" + shardId + ")*/");
        lblHintId.setLayoutData(CommonUITool.createGridData(GridData.BEGINNING, 1, 1, 200, -1));

        txtShardId.addKeyListener(
                new KeyListener() {
                    public void keyReleased(KeyEvent e) {
                        lblHintId.setText("/*+shard_id(" + txtShardId.getText() + ")*/");
                    }

                    public void keyPressed(KeyEvent e) {}
                });

        // SHARD VAL
        btnUseShardVal = new Button(inputComposite, SWT.RADIO);
        btnUseShardVal.setText(Messages.btnUseShardValHint);

        txtShardVal = new Text(inputComposite, SWT.BORDER);
        txtShardVal.setText(String.valueOf(shardVal));
        txtShardVal.setLayoutData(CommonUITool.createGridData(GridData.BEGINNING, 1, 1, 100, -1));
        if (shardQueryType == DatabaseInfo.SHARD_QUERY_TYPE_VAL) {
            txtShardVal.selectAll();
        }

        final Label lblHintVal = new Label(inputComposite, SWT.NONE);
        lblHintVal.setText("/*+shard_val(" + shardVal + ")*/");
        lblHintVal.setLayoutData(CommonUITool.createGridData(GridData.BEGINNING, 1, 1, 200, -1));

        txtShardVal.addKeyListener(
                new KeyListener() {
                    public void keyReleased(KeyEvent e) {
                        lblHintVal.setText("/*+shard_val(" + txtShardVal.getText() + ")*/");
                    }

                    public void keyPressed(KeyEvent e) {}
                });

        btnUseShardId.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        shardQueryType = DatabaseInfo.SHARD_QUERY_TYPE_ID;
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {}
                });

        btnUseShardVal.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        shardQueryType = DatabaseInfo.SHARD_QUERY_TYPE_VAL;
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {}
                });

        if (shardQueryType == DatabaseInfo.SHARD_QUERY_TYPE_VAL) {
            btnUseShardId.setSelection(false);
            btnUseShardVal.setSelection(true);
        } else {
            btnUseShardId.setSelection(true);
            btnUseShardVal.setSelection(false);
        }

        return parentComp;
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        this.getShell().setSize(450, 250);
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.titleChooseShardIdDialog);
    }

    /**
     * Create buttons for button bar
     *
     * @param parent the parent composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.btnOK, true);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.btnCancel, false);
    }

    /**
     * Call this method when the button in button bar is pressed
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            setShardId(StringUtil.intValue(txtShardId.getText()));
            setShardVal(StringUtil.intValue(txtShardVal.getText()));
            setShardQueryType(
                    btnUseShardId.getSelection()
                            ? DatabaseInfo.SHARD_QUERY_TYPE_ID
                            : DatabaseInfo.SHARD_QUERY_TYPE_VAL);
        }

        super.buttonPressed(buttonId);
    }
}
