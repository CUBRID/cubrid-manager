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
package com.cubrid.common.ui.cubrid.serial.action;

import com.cubrid.common.core.common.model.SerialInfo;
import com.cubrid.common.ui.cubrid.serial.dialog.CreateOrEditSerialDialog;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ICubridNodeLoader;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.loader.CubridSerialFolderLoader;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import java.util.Locale;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

/**
 * This action is responsible to create serial
 *
 * @author pangqiren
 * @version 1.0 - 2009-5-8 created by pangqiren
 */
public class CreateSerialAction extends SelectionAction {

    public static final String ID = CreateSerialAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param enabledIcon
     * @param disabledIcon
     */
    public CreateSerialAction(
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
    public CreateSerialAction(
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

    /** Open the createSerial dialog and create serial */
    public void run() {
        Object[] objArr = this.getSelectedObj();
        if (!isSupported(objArr)) {
            setEnabled(false);
            return;
        }
        ISchemaNode schemaNode = (ISchemaNode) objArr[0];
        CubridDatabase database = schemaNode.getDatabase();
        run(database);
    }

    /**
     * run create serial
     *
     * @param database
     */
    public void run(CubridDatabase database) {
        CreateOrEditSerialDialog dialog = new CreateOrEditSerialDialog(getShell(), true);
        dialog.setDatabase(database);
        ISelectionProvider provider = getSelectionProvider();
        if (dialog.open() == IDialogConstants.OK_ID && (provider instanceof TreeViewer)) {
            TreeViewer treeViewer = (TreeViewer) provider;
            ICubridNode folderNode =
                    database.getChild(
                            database.getId()
                                    + ICubridNodeLoader.NODE_SEPARATOR
                                    + CubridSerialFolderLoader.SERIAL_FOLDER_ID);
            if (folderNode == null || !folderNode.getLoader().isLoaded()) {
                return;
            } // FIXME move this logic to core module
            String serialName = dialog.getSerialName().toLowerCase(Locale.getDefault());
            DbUserInfo userInfo = database.getDatabaseInfo().getAuthLoginedDbUserInfo();
            String id =
                    folderNode.getId()
                            + ICubridNodeLoader.NODE_SEPARATOR
                            + userInfo.getName()
                            + "."
                            + serialName;
            SerialInfo serialInfo = new SerialInfo();
            serialInfo.setName(serialName);
            serialInfo.setOwner(userInfo.getName());
            ICubridNode newNode =
                    CubridSerialFolderLoader.createSerialNode(
                            database.getDatabaseInfo(), id, serialInfo);
            CommonUITool.addNodeToTree(treeViewer, folderNode, newNode);
            CommonUITool.updateFolderNodeLabelIncludingChildrenCount(treeViewer, folderNode);
            CubridNodeManager.getInstance()
                    .fireCubridNodeChanged(
                            new CubridNodeChangedEvent(
                                    newNode, CubridNodeChangedEventType.NODE_ADD));
        }
    }
}
