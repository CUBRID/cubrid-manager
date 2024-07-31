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
package com.cubrid.cubridmanager.ui.host.dialog;

import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.ui.host.Messages;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;
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
 * Rename Host Dialog
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-1-15 created by Kevin.Wang
 */
public class RenameHostDialog extends CMTitleAreaDialog {
    private CubridServer server;
    private String newName;

    private Text newNameText = null;

    public RenameHostDialog(Shell parentShell, CubridServer server) {
        super(parentShell);
        this.server = server;
    }

    /** @see com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog#constrainShellSize() */
    protected void constrainShellSize() {
        super.constrainShellSize();
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.bind(Messages.renameShellTitle, server.getServerName()));
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     * @param parent the button bar composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.renameOKBTN, false);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.renameCancelBTN, false);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     * @param buttonId the id of the button that was pressed (see <code>IDialogConstants.*_ID</code>
     *     constants)
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            if (!valid()) {
                newNameText.setFocus();
                return;
            }

            if (!CommonUITool.openConfirmBox(getShell(), Messages.renameHostDialogConfirmMsg)) {
                newNameText.setFocus();
                return;
            }

            newName = newNameText.getText().trim();
        }
        super.buttonPressed(buttonId);
    }

    /**
     * Judge the name is validate
     *
     * @return
     */
    private boolean valid() {
        setErrorMessage(null);

        boolean isValidHostName = ValidateUtil.isValidHostName(newNameText.getText());
        if (!isValidHostName) {
            setErrorMessage(Messages.errHostName);
            return false;
        }

        boolean isHostExist =
                CMHostNodePersistManager.getInstance()
                        .isContainedByName(newNameText.getText(), server);
        if (isHostExist) {
            setErrorMessage(Messages.errHostExist);
            newNameText.setFocus();
            return false;
        }

        return true;
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
        label1.setText(Messages.lblName);
        GridData data = new GridData();
        data.widthHint = 60;
        data.horizontalSpan = 1;
        data.verticalSpan = 1;
        label1.setLayoutData(data);

        newNameText = new Text(composite, SWT.BORDER);
        data = new GridData();
        data.horizontalSpan = 2;
        data.verticalSpan = 1;
        data.grabExcessHorizontalSpace = true;
        data.verticalAlignment = GridData.FILL;
        data.horizontalAlignment = GridData.FILL;

        newNameText.setLayoutData(data);
        newNameText.setText(server.getName());
        newNameText.selectAll();
        newNameText.setFocus();
        newNameText.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        setErrorMessage(null);
                        getButton(IDialogConstants.OK_ID).setEnabled(false);
                        if (!valid()) {
                            return;
                        }
                        getButton(IDialogConstants.OK_ID).setEnabled(true);
                    }
                });

        newNameText.addListener(
                SWT.KeyDown,
                new Listener() {
                    public void handleEvent(Event e) {
                        if (e.type == SWT.KeyDown && e.character == SWT.CR) {
                            buttonPressed(IDialogConstants.OK_ID);
                        }
                    }
                });

        setTitle(Messages.bind(Messages.renameMSGTitle, server.getName()));
        setMessage(Messages.bind(Messages.renameDialogMSG, server.getName()));
        return parent;
    }

    public String getNewName() {
        return newName;
    }
}
