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

package com.cubrid.common.ui.cubrid.procedure.action;

import com.cubrid.common.ui.cubrid.procedure.dialog.EditProcedureDialog;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ICubridNodeLoader;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.loader.sp.CubridProcedureFolderLoader;
import com.cubrid.common.ui.spi.model.loader.sp.CubridSPFolderLoader;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.sp.model.SPInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import java.util.Locale;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

/**
 * This action is responsible to add Procedure
 *
 * @author robin 2009-3-18
 */
public class AddProcedureAction extends SelectionAction {

    public static final String ID = AddProcedureAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param enabledIcon
     * @param disabledIcon
     */
    public AddProcedureAction(
            Shell shell, String text, ImageDescriptor enabledIcon, ImageDescriptor disabledIcon) {
        this(shell, null, text, enabledIcon, disabledIcon);
    }

    /**
     * The constructor
     *
     * @param shell
     * @param provider
     * @param text
     * @param enabledIcon
     * @param disabledIcon
     */
    public AddProcedureAction(
            Shell shell,
            ISelectionProvider provider,
            String text,
            ImageDescriptor enabledIcon,
            ImageDescriptor disabledIcon) {
        super(shell, provider, text, enabledIcon);
        this.setId(ID);
        this.setToolTipText(text);
        this.setDisabledImageDescriptor(disabledIcon);
    }

    /**
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#allowMultiSelections ()
     * @return false
     */
    public boolean allowMultiSelections() {
        return true;
    }

    /**
     * Sets this action support this object
     *
     * @see org.eclipse.jface.action.IAction.ISelectionAction
     * @param obj Object
     * @return boolean
     */
    public boolean isSupported(Object obj) {
        return ActionSupportUtil.isSupportMultiSelection(obj, null, false);
    }

    /** Open AddProcedureDialog */
    public void run() { // FIXME logic code move to core module
        Shell shell = getShell();
        Object[] obj = this.getSelectedObj();
        if (!isSupported(obj)) {
            setEnabled(false);
            return;
        }
        ISchemaNode node = (ISchemaNode) obj[0];
        CubridDatabase database = node.getDatabase();

        EditProcedureDialog dlg = new EditProcedureDialog(shell);
        dlg.setDatabase(database);
        dlg.setNewFlag(true);
        ISelectionProvider provider = getSelectionProvider();
        if (dlg.open() == IDialogConstants.OK_ID && (provider instanceof TreeViewer)) {
            ICubridNode folderNode =
                    database.getChild(
                            database.getId()
                                    + ICubridNodeLoader.NODE_SEPARATOR
                                    + CubridSPFolderLoader.SP_FOLDER_ID);
            folderNode =
                    folderNode.getChild(
                            folderNode.getId()
                                    + ICubridNodeLoader.NODE_SEPARATOR
                                    + CubridSPFolderLoader.PROCEDURE_FOLDER_ID);
            TreeViewer treeViewer = (TreeViewer) provider;
            if (folderNode == null || !folderNode.getLoader().isLoaded()) {
                return;
            }
            String procedureName = dlg.getProcedureName().toLowerCase(Locale.getDefault());
            String id = folderNode.getId() + ICubridNodeLoader.NODE_SEPARATOR + procedureName;
            DbUserInfo userInfo = database.getDatabaseInfo().getAuthLoginedDbUserInfo();
            SPInfo spInfo = new SPInfo(procedureName);
            spInfo.setOwner(userInfo.getName());
            ICubridNode newNode = CubridProcedureFolderLoader.createProcedureNode(id, spInfo);
            CommonUITool.addNodeToTree(treeViewer, folderNode, newNode);
        }
    }
}
