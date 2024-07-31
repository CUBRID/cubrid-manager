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
package com.cubrid.common.ui.common.action;

import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.common.dialog.SelectWorkspaceDialog;
import com.cubrid.common.ui.spi.persist.PersistUtils;
import com.cubrid.common.ui.spi.util.CommonUITool;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Switch the workspace action
 *
 * @author pangqiren
 * @version 1.0 - 2011-8-31 created by pangqiren
 */
public class SwitchWorkspaceAction extends Action {

    public static final String KEY_IS_SWITCH_WORKSPACE = "IS_SWTICH_WORKSPACE";

    private final String productName;
    private final String productVersion;
    private final boolean isOther;

    /**
     * The constructor
     *
     * @param text String
     * @param productName String
     * @param productVersion String
     * @param isOther boolean
     */
    public SwitchWorkspaceAction(
            String text, String productName, String productVersion, boolean isOther) {
        super(text);
        this.productName = productName;
        this.productVersion = productVersion;
        this.isOther = isOther;
    }

    /** Run */
    public void run() {
        if (isOther) {
            Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
            SelectWorkspaceDialog pwd =
                    new SelectWorkspaceDialog(shell, true, productName, productVersion);
            int ret = pwd.open();
            if (ret != IDialogConstants.OK_ID) {
                return;
            }
        } else {
            boolean isConfirm = CommonUITool.openConfirmBox(Messages.msgConfirmSwitch);
            if (!isConfirm) {
                return;
            }
            String workspace = this.getText();
            SelectWorkspaceDialog.setLastSetWorkspaceDirectory(workspace);
        }

        PersistUtils.setGlobalPreferenceValue(
                CommonUIPlugin.PLUGIN_ID, KEY_IS_SWITCH_WORKSPACE, "true");
        PlatformUI.getWorkbench().restart();
    }
}
