/*
 * Copyright (C) 2008 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *  - Neither the name of the <ORGANIZATION> nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
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
package com.cubrid.cubridmanager.ui.cubrid.jobauto.action;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultCubridNode;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.cubridmanager.ui.cubrid.jobauto.dialog.BackupErrLogDialog;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;

/**
 * show the error info of back up database
 *
 * @author robin 2009-4-14
 */
public class BackupErrLogAction extends SelectionAction {

    public static final String ID = BackupErrLogAction.class.getName();
    private static final Logger LOGGER = LogUtil.getLogger(BackupErrLogAction.class);

    /**
     * The Constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public BackupErrLogAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    /**
     * The Constructor
     *
     * @param shell
     * @param provider
     * @param text
     * @param icon
     */
    public BackupErrLogAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    /**
     * Sets this action support to select multi-object
     *
     * @see org.eclipse.jface.action.IAction.ISelectionAction
     * @return boolean
     */
    public boolean allowMultiSelections() {
        return false;
    }

    /**
     * Sets this action support this object
     *
     * @see org.eclipse.jface.action.IAction.ISelectionAction
     * @param obj Object
     * @return boolean
     */
    public boolean isSupported(Object obj) {
        if (obj instanceof DefaultCubridNode
                && CubridNodeType.BACKUP_PLAN_FOLDER.equals(((DefaultCubridNode) obj).getType())) {
            return true;
        }
        return false;
    }

    /** @see org.eclipse.jface.action.Action#run() */
    public void run() {
        Shell shell = getShell();
        Object[] obj = this.getSelectedObj();
        DefaultSchemaNode node = null;
        if (obj.length > 0 && obj[0] instanceof DefaultSchemaNode) {
            node = (DefaultSchemaNode) obj[0];
        }
        if (node == null) {
            return;
        }
        BackupErrLogDialog dlg = new BackupErrLogDialog(shell);
        dlg.setDatabase(node.getDatabase());
        try {
            if (dlg.loadData(shell)) {
                dlg.open();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * open from job dashboard
     *
     * @param CubridDatabase database
     */
    public void run(CubridDatabase database) {
        Shell shell = getShell();

        BackupErrLogDialog dlg = new BackupErrLogDialog(shell);
        dlg.setDatabase(database);
        try {
            if (dlg.loadData(shell)) {
                dlg.open();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
