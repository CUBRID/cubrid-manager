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

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.SQLGenerateUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * The dialog of copy SQL to a file.
 *
 * @author Kevin Cao 2011-2-28
 */
public class CopySQLToFileDialog extends CMTitleAreaDialog {
    private static final Logger LOGGER = LogUtil.getLogger(CopySQLToFileDialog.class);
    private static final String KEY_FILE = "CopySQLToFileDialog.SQL_PATH";
    private Button btnCreateSQL;
    private Button btnGrantSQL;
    private Button btnSelectSQL;
    private Button btnInsertSQL;
    private Button btnUpdateSQL;
    private Button btnDeleteSQL;
    private Text txtFile;
    private Button okButton;
    private Object[] objects;
    // protected ISelectionProvider provider;

    /**
     * Constructor
     *
     * @param parentShell Shell
     * @param provider the tree nodes selection provider.
     */
    public CopySQLToFileDialog(Shell parentShell, Object[] obj) {
        super(parentShell);
        this.objects = obj;
    }

    /**
     * Validate the input values.
     *
     * @return is the inputs useful.
     */
    private boolean validate() {

        return (btnCreateSQL.getSelection()
                        || btnGrantSQL.getSelection()
                        || btnSelectSQL.getSelection()
                        || btnInsertSQL.getSelection()
                        || btnUpdateSQL.getSelection()
                        || btnDeleteSQL.getSelection())
                && (txtFile.getText().length() > 0);
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
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);

        createComposite(composite);

        setTitle(Messages.msgCopySQLToFile);
        setMessage(Messages.msgCopySQLToFileDes);
        getShell().setText(Messages.msgCopySQLToFile);

        this.getShell().pack();
        return parentComp;
    }

    /** @see com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog#constrainShellSize() */
    protected void constrainShellSize() {
        getShell().pack();
        super.constrainShellSize();
        CommonUITool.centerShell(getShell());
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     * @param parent the button bar composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        okButton = createButton(parent, IDialogConstants.OK_ID, Messages.btnOK, true);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.btnCancel, false); // $NON-NLS-1$
        updateButtons();
    }

    /** OK button pressed. Save SQL to file. */
    protected void okPressed() { // FIXME move this logic to core module

        final int len = objects.length;
        final Display display = PlatformUI.getWorkbench().getDisplay();
        BusyIndicator.showWhile(
                display,
                new Runnable() {
                    public void run() {
                        StringBuffer allTableSql = new StringBuffer();
                        if (btnCreateSQL.getSelection()) {
                            for (int i = 0; i < len; i++) {
                                DefaultSchemaNode table = (DefaultSchemaNode) objects[i];
                                String sql = SQLGenerateUtils.getCreateSQL(table);
                                if (sql != null && sql.trim().length() > 0) {
                                    allTableSql.append(sql);
                                    allTableSql.append(StringUtil.NEWLINE);
                                    allTableSql.append(StringUtil.NEWLINE);
                                }
                            }
                        }
                        if (btnGrantSQL.getSelection()) {
                            for (int i = 0; i < len; i++) {
                                DefaultSchemaNode table = (DefaultSchemaNode) objects[i];
                                String sql = SQLGenerateUtils.getGrantSQL(table);
                                if (sql != null && sql.trim().length() > 0) {
                                    allTableSql.append(sql);
                                    allTableSql.append(StringUtil.NEWLINE);
                                    allTableSql.append(StringUtil.NEWLINE);
                                }
                            }
                        }
                        if (btnInsertSQL.getSelection()) {
                            for (int i = 0; i < len; i++) {
                                DefaultSchemaNode table = (DefaultSchemaNode) objects[i];
                                String sql = SQLGenerateUtils.getInsertSQL(table);
                                if (sql != null && sql.trim().length() > 0) {
                                    allTableSql.append(sql);
                                    allTableSql.append(StringUtil.NEWLINE);
                                    allTableSql.append(StringUtil.NEWLINE);
                                }
                            }
                        }
                        if (btnUpdateSQL.getSelection()) {
                            for (int i = 0; i < len; i++) {
                                DefaultSchemaNode table = (DefaultSchemaNode) objects[i];
                                String sql = SQLGenerateUtils.getUpdateSQL(table);
                                if (sql != null && sql.trim().length() > 0) {
                                    allTableSql.append(sql);
                                    allTableSql.append(StringUtil.NEWLINE);
                                    allTableSql.append(StringUtil.NEWLINE);
                                }
                            }
                        }
                        if (btnDeleteSQL.getSelection()) {
                            for (int i = 0; i < len; i++) {
                                DefaultSchemaNode table = (DefaultSchemaNode) objects[i];
                                String sql = SQLGenerateUtils.getDeleteSQL(table);
                                if (sql != null && sql.trim().length() > 0) {
                                    allTableSql.append(sql);
                                    allTableSql.append(StringUtil.NEWLINE);
                                    allTableSql.append(StringUtil.NEWLINE);
                                }
                            }
                        }

                        if (btnSelectSQL.getSelection()) {
                            for (int i = 0; i < len; i++) {
                                DefaultSchemaNode table = (DefaultSchemaNode) objects[i];
                                String sql = SQLGenerateUtils.getSelectSQL(table);
                                if (sql != null && sql.trim().length() > 0) {
                                    allTableSql.append(sql);
                                    allTableSql.append(StringUtil.NEWLINE);
                                    allTableSql.append(StringUtil.NEWLINE);
                                }
                            }
                        }

                        if (allTableSql.length() == 0) {
                            return;
                        }
                        File file = new File(txtFile.getText());
                        if (file.isDirectory()) {
                            return;
                        }
                        try {
                            if (!file.delete() && !file.createNewFile()) {
                                return;
                            }
                            FileOutputStream fos = new FileOutputStream(file);
                            fos.write(allTableSql.toString().getBytes("utf-8"));
                            fos.flush();
                            fos.close();
                        } catch (IOException e) {
                            LOGGER.error("", e);
                        }
                    }
                });
        super.okPressed();
    }

    /**
     * Create Composite
     *
     * @param parent Composite
     */
    private void createComposite(final Composite parent) {

        Group group = new Group(parent, SWT.NONE);
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        group.setLayout(new GridLayout(2, true));

        btnCreateSQL = new Button(group, SWT.CHECK);
        btnCreateSQL.setSelection(true);
        SelectionAdapter listener =
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        updateButtons();
                    }
                };
        btnCreateSQL.addSelectionListener(listener);
        btnCreateSQL.setText(Messages.btnCreateSQL);
        btnGrantSQL = new Button(group, SWT.CHECK);
        btnGrantSQL.setSelection(false);
        btnGrantSQL.setText(Messages.btnGrantSQL);
        btnGrantSQL.addSelectionListener(listener);

        Group group2 = new Group(parent, SWT.NONE);
        group2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        group2.setLayout(new GridLayout(2, true));

        btnInsertSQL = new Button(group2, SWT.CHECK);
        btnInsertSQL.setSelection(false);
        btnInsertSQL.setText(Messages.btnInsertSQL);
        btnInsertSQL.addSelectionListener(listener);

        btnUpdateSQL = new Button(group2, SWT.CHECK);
        btnUpdateSQL.setSelection(false);
        btnUpdateSQL.setText(Messages.btnUpdateSQL);
        btnUpdateSQL.addSelectionListener(listener);

        btnDeleteSQL = new Button(group2, SWT.CHECK);
        btnDeleteSQL.setSelection(false);
        btnDeleteSQL.setText(Messages.btnDeleteSQL);
        btnDeleteSQL.addSelectionListener(listener);

        btnSelectSQL = new Button(group2, SWT.CHECK);
        btnSelectSQL.setSelection(false);
        btnSelectSQL.setText(Messages.btnSelectSQL);
        btnSelectSQL.addSelectionListener(listener);

        Group group3 = new Group(parent, SWT.NONE);
        group3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        group3.setLayout(new GridLayout(3, false));

        Label lblFile = new Label(group3, SWT.LEFT);
        lblFile.setText(Messages.lblFile);

        txtFile = new Text(group3, SWT.SINGLE | SWT.FILL | SWT.BORDER);
        txtFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
        String oldFileName = CommonUIPlugin.getPluginDialogSettings().get(KEY_FILE);
        txtFile.setText(oldFileName == null ? "" : oldFileName);
        txtFile.addModifyListener(
                new ModifyListener() {

                    public void modifyText(ModifyEvent event) {
                        updateButtons();
                    }
                });

        Button btnSelectFile = new Button(group3, SWT.RIGHT);
        btnSelectFile.setText(Messages.btnBrowse);
        btnSelectFile.addSelectionListener(
                new SelectionAdapter() {

                    /**
                     * Select file to be saved.
                     *
                     * @param event SelectionEvent
                     */
                    public void widgetSelected(SelectionEvent event) {
                        FileDialog fd =
                                new FileDialog(CopySQLToFileDialog.this.getShell(), SWT.SAVE);
                        String text = txtFile.getText();
                        if (text == null || text.trim().length() == 0) {
                            text = CommonUIPlugin.getPluginDialogSettings().get(KEY_FILE);
                        }
                        if (text != null) {
                            fd.setFilterPath(text);
                        }
                        fd.setFilterExtensions(new String[] {"*.sql", "*.*"});
                        fd.setFilterNames(new String[] {"*.sql", "*.*"});
                        fd.setOverwrite(true);
                        text = fd.open();
                        if (text == null || text.trim().length() == 0) {
                            return;
                        }
                        txtFile.setText(text);
                        CommonUIPlugin.getPluginDialogSettings().put(KEY_FILE, text);
                        txtFile.setFocus();
                        txtFile.selectAll();
                    }
                });
    }

    /** Update buttons' status. */
    private void updateButtons() {
        okButton.setEnabled(validate());
    }
}
