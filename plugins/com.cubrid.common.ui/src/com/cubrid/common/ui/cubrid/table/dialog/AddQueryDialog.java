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

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.query.format.SqlFormattingStrategy;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * The Dialog of add query
 *
 * @author robin 2009-3-11
 */
public class AddQueryDialog extends CMTitleAreaDialog {

    private Text sqlSpecText = null;
    private final int index;
    private final boolean newFlag;
    private final CreateViewDialog parentDialog;

    private static SqlFormattingStrategy formator = new SqlFormattingStrategy();

    public AddQueryDialog(
            Shell parentShell, boolean newFlag, int index, CreateViewDialog parentDialog) {
        super(parentShell);
        this.newFlag = newFlag;
        this.index = index;
        this.parentDialog = parentDialog;
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     * @param parent The parent composite to contain the dialog area
     * @return the dialog area control
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);

        final Composite composite = new Composite(parentComp, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        createQueryComposite(composite);
        if (newFlag) {
            setTitle(Messages.titleAddQueryDialog);
            setMessage(Messages.titleAddQueryDialog);
        } else {
            setTitle(Messages.titleEditQueryDialog);
            setMessage(Messages.titleEditQueryDialog);
        }
        initial();
        return parentComp;
    }

    /**
     * Create query SQL composite
     *
     * @param composite Composite
     */
    private void createQueryComposite(Composite composite) {

        final Composite dbnameGroup = new Composite(composite, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        // layout.numColumns = 2;
        GridData gdDbnameGroup = new GridData(GridData.FILL_BOTH);
        dbnameGroup.setLayoutData(gdDbnameGroup);
        dbnameGroup.setLayout(layout);
        layout = new GridLayout();
        layout.marginWidth = 10;
        layout.marginHeight = 10;
        gdDbnameGroup = new GridData(GridData.FILL_BOTH);
        final Group group = new Group(dbnameGroup, SWT.NONE);
        group.setLayoutData(gdDbnameGroup);
        group.setLayout(layout);
        group.setText(Messages.grpQuerySpecification);
        sqlSpecText = new Text(group, SWT.BORDER | SWT.WRAP | SWT.MULTI | SWT.V_SCROLL);
        sqlSpecText.setLayoutData(new GridData(GridData.FILL_BOTH));
    }

    /** initializes some values */
    private void initial() {
        if (!newFlag && parentDialog.queryListData.size() > index) {
            sqlSpecText.setText(formatSql(parentDialog.queryListData.get(index).get("0")));
        }
    }

    /** @see com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog#constrainShellSize() */
    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setMinimumSize(400, 480);
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.msgAddQueryDialog);
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     * @param parent the button bar composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(
                parent, IDialogConstants.OK_ID, com.cubrid.common.ui.common.Messages.btnOK, false);
        getButton(IDialogConstants.OK_ID).setEnabled(true);
        createButton(
                parent,
                IDialogConstants.CANCEL_ID,
                com.cubrid.common.ui.common.Messages.btnCancel,
                false);
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     * @param buttonId the id of the button that was pressed (see <code>IDialogConstants.*_ID</code>
     *     constants)
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID && !editQuerySpec()) {
            return;
        }
        super.buttonPressed(buttonId);
    }

    /**
     * Edit the query specification
     *
     * @return boolean <code>true</code> if can edit;otherwise <code>false</code>
     */
    private boolean editQuerySpec() {
        String sql = sqlSpecText.getText();
        if (parentDialog.validateResult(sql, newFlag, index)) {
            if (StringUtil.isNotEmpty(sql)) {
                if (newFlag) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("0", unFormatSql(sql));
                    parentDialog.queryListData.add(map);
                } else {
                    Map<String, String> map = parentDialog.queryListData.get(index);
                    map.put("0", unFormatSql(sql));
                    parentDialog.queryListData.set(index, map);
                }
            }
            parentDialog.queryTableViewer.refresh();
            return true;
        }
        return false;
    }

    /**
     * Format the sql script
     *
     * @param sql String
     * @return String
     */
    private String formatSql(String sql) { // FIXME move this logic to core module
        String sqlStr = formator.format(sql + ";");
        sqlStr =
                sqlStr.trim().endsWith(";")
                        ? sqlStr.trim().substring(0, sqlStr.trim().length() - 1)
                        : "";
        return sqlStr;
    }

    /**
     * UnFormat the sql script
     *
     * @param sql String
     * @return String
     */
    private String unFormatSql(String sql) { // FIXME move this logic to core module
        return sql.replaceAll(System.getProperty("line.separator"), " ");
    }
}
