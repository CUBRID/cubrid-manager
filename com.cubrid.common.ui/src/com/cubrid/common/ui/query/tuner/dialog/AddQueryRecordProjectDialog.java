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
package com.cubrid.common.ui.query.tuner.dialog;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.tuner.QueryRecordProject;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.persist.ApplicationPersistUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * AddQueryRecordListDialog Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-4-19 created by Kevin.Wang
 */
public class AddQueryRecordProjectDialog extends CMTitleAreaDialog {

    private Text newText = null;
    private String name;
    private boolean newFlag;
    private DatabaseInfo databaseInfo;
    private QueryRecordProject queryRecordProect;

    public AddQueryRecordProjectDialog(
            Shell parentShell,
            QueryRecordProject queryRecordProect,
            boolean newFlag,
            DatabaseInfo databaseInfo) {
        super(parentShell);
        this.queryRecordProect = queryRecordProect;
        this.newFlag = newFlag;
        this.databaseInfo = databaseInfo;
    }

    /** @see com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog#constrainShellSize() */
    protected void constrainShellSize() {
        super.constrainShellSize();
        CommonUITool.centerShell(getShell());
        if (newFlag) {
            getShell().setText(Messages.titleAddQueryTunerList);
        } else {
            getShell()
                    .setText(
                            Messages.bind(
                                    Messages.titleRenameQueryTunerList,
                                    queryRecordProect.getName()));
        }
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
        if (buttonId == IDialogConstants.OK_ID) {
            if (!validate()) {
                return;
            }
        }
        super.buttonPressed(buttonId);
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
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);

        Label label1 = new Label(composite, SWT.LEFT);
        label1.setText(Messages.lblQueryName);
        label1.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        newText = new Text(composite, SWT.BORDER);
        newText.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        newText.setText(queryRecordProect.getName());
        newText.selectAll();
        newText.setFocus();
        newText.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        validate();
                    }
                });

        newText.addListener(
                SWT.KeyDown,
                new Listener() {
                    public void handleEvent(Event e) {
                        if (e.type == SWT.KeyDown && e.character == SWT.CR) {
                            buttonPressed(IDialogConstants.OK_ID);
                        }
                    }
                });

        if (newFlag) {
            setTitle(Messages.titleAddQueryTunerList);
            setMessage(Messages.titleAddQueryTunerList);
        } else {
            setTitle(
                    Messages.bind(Messages.titleRenameQueryTunerList, queryRecordProect.getName()));
            setMessage(
                    Messages.bind(Messages.titleRenameQueryTunerList, queryRecordProect.getName()));
        }

        return parent;
    }

    private boolean validate() {
        setErrorMessage(null);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
        name = newText.getText();

        if (StringUtil.isEmpty(name)) {
            setErrorMessage(Messages.msgQueryTunerNameEmpty);
        }

        List<QueryRecordProject> queryRecordProjectList =
                ApplicationPersistUtil.getInstance().getQueryRecordProject(databaseInfo);
        if (newFlag) {
            for (QueryRecordProject list : queryRecordProjectList) {
                if (StringUtil.isEqual(name, list.getName())) {
                    setErrorMessage(Messages.bind(Messages.msgQueryTunerNameExist, name));
                    return false;
                }
            }
        } else {
            for (QueryRecordProject list : queryRecordProjectList) {
                if (StringUtil.isEqual(name, list.getName()) && list != queryRecordProect) {
                    setErrorMessage(Messages.msgQueryTunerNameExist);
                    return false;
                }
            }
        }
        getButton(IDialogConstants.OK_ID).setEnabled(true);
        return true;
    }

    /** @return the name */
    public String getName() {
        return name;
    }
}
