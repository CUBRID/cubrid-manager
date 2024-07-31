/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.cubridmanager.ui.mondashboard.action;

import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.cubridmanager.ui.mondashboard.Messages;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HANode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.BrokerDBListMonitorPart;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.BrokerMonitorPart;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.ClientMonitorPart;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.DatabaseMonitorPart;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.HostMonitorPart;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

/**
 * Edit nick name action
 *
 * @author pangqiren
 * @version 1.0 - 2010-8-21 created by pangqiren
 */
public class EditAliasNameAction extends SelectionAction {

    public static final String ID = EditAliasNameAction.class.getName();

    /**
     * EditNickNameAction constructor.
     *
     * @param shell window.getShell()
     * @param text action text
     * @param icon ImageDescriptor
     */
    public EditAliasNameAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    /**
     * EditNickNameAction constructor.
     *
     * @param shell window.getShell()
     * @param provider ISelectionProvider
     * @param text action text
     * @param icon ImageDescriptor
     */
    protected EditAliasNameAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    /**
     * not allow multi selctions
     *
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#allowMultiSelections()
     * @return boolean false
     */
    public boolean allowMultiSelections() {
        return false;
    }

    /**
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#isSupported(java.lang.Object)
     * @param obj Object
     * @return is supported.
     */
    public boolean isSupported(Object obj) {
        if (obj instanceof DatabaseMonitorPart) {
            return true;
        } else if (obj instanceof HostMonitorPart) {
            return true;
        } else if (obj instanceof BrokerMonitorPart) {
            return true;
        } else if (obj instanceof ClientMonitorPart) {
            return true;
        } else if (obj instanceof BrokerDBListMonitorPart) {
            return true;
        }
        return false;
    }

    /**
     * Edit alias name
     *
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run() {
        Object[] objArr = this.getSelectedObj();
        if (objArr == null || objArr.length <= 0 || !isSupported(objArr[0])) {
            setEnabled(false);
            return;
        }
        HANode haNode = null;
        if (objArr[0] instanceof HostMonitorPart) {
            HostMonitorPart hostMonPart = (HostMonitorPart) objArr[0];
            haNode = (HANode) hostMonPart.getModel();
        } else if (objArr[0] instanceof DatabaseMonitorPart) {
            DatabaseMonitorPart dbMonPart = (DatabaseMonitorPart) objArr[0];
            haNode = (HANode) dbMonPart.getModel();
        } else if (objArr[0] instanceof BrokerMonitorPart) {
            BrokerMonitorPart brokerMonPart = (BrokerMonitorPart) objArr[0];
            haNode = (HANode) brokerMonPart.getModel();
        } else if (objArr[0] instanceof ClientMonitorPart) {
            ClientMonitorPart clientMonitorPart = (ClientMonitorPart) objArr[0];
            haNode = (HANode) clientMonitorPart.getModel();
        } else if (objArr[0] instanceof BrokerDBListMonitorPart) {
            BrokerDBListMonitorPart brokerDBListMonitorPart = (BrokerDBListMonitorPart) objArr[0];
            haNode = (HANode) brokerDBListMonitorPart.getModel();
        }
        if (haNode == null) {
            return;
        }
        InputDialog dialog =
                new InputDialog(
                        getShell(),
                        Messages.titleEditNickNameDialog,
                        Messages.msgEditNickNameDialog,
                        haNode.getName(),
                        new IInputValidator() {
                            public String isValid(String newText) {
                                if (newText == null || newText.trim().length() == 0) {
                                    return Messages.errEditNickName;
                                }
                                return null;
                            }
                        });
        if (IDialogConstants.OK_ID == dialog.open()) {
            String aliasName = dialog.getValue();
            haNode.setName(aliasName);
        }
    }
}
