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
package com.cubrid.cubridquery.ui.common.action;

import com.cubrid.common.ui.common.dialog.ShardIdSelectionDialog;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

public class ChangeShardAction extends SelectionAction {
    public static final String ID = ChangeShardAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public ChangeShardAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    /**
     * The constructor
     *
     * @param shell
     * @param provider
     * @param text
     * @param icon
     */
    public ChangeShardAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    /**
     * Return whether this action support to select multi object,if not support,this action will be
     * disabled
     *
     * @return <code>true</code> if allow multi selection;<code>false</code> otherwise
     */
    public boolean allowMultiSelections() {
        return false;
    }

    /**
     * Return whether this action support this object,if not support,this action will be disabled
     *
     * @param obj the Object
     * @return <code>true</code> if support this obj;<code>false</code> otherwise
     */
    public boolean isSupported(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof ICubridNode)) {
            return false;
        }

        ICubridNode cubridNode = (ICubridNode) obj;
        String nodeType = cubridNode.getType();
        if (!NodeType.DATABASE.equals(nodeType)) {
            return false;
        }

        ISchemaNode schemaNode = (ISchemaNode) cubridNode;
        DatabaseInfo dbInfo = schemaNode.getDatabase().getDatabaseInfo();
        if (!schemaNode.getDatabase().isLogined()) {
            return false;
        }

        if (!dbInfo.isShard()) {
            return false;
        }

        return true;
    }

    /** Reload the selected CUBRID node */
    public void run() {
        final Object[] obj = this.getSelectedObj();
        if (obj == null || obj.length == 0 || !isSupported(obj[0])) {
            return;
        }

        ICubridNode cubridNode = (ICubridNode) obj[0];
        if (cubridNode == null) {
            return;
        }

        String nodeType = cubridNode.getType();
        if (!NodeType.DATABASE.equals(nodeType)) {
            return;
        }

        ISchemaNode schemaNode = (ISchemaNode) cubridNode;
        DatabaseInfo dbInfo = schemaNode.getDatabase().getDatabaseInfo();
        if (!dbInfo.isShard()) {
            return;
        }

        ShardIdSelectionDialog dialog = new ShardIdSelectionDialog(getShell());
        dialog.setDatabaseInfo(dbInfo);
        dialog.setShardId(dbInfo.getCurrentShardId());
        dialog.setShardVal(dbInfo.getCurrentShardVal());
        dialog.setShardQueryType(dbInfo.getShardQueryType());
        if (dialog.open() != IDialogConstants.OK_ID) {
            return;
        }

        int shardId = dialog.getShardId();
        int shardVal = dialog.getShardVal();
        int shardQueryType = dialog.getShardQueryType();

        dbInfo.setCurrentShardId(shardId);
        dbInfo.setCurrentShardVal(shardVal);
        dbInfo.setShardQueryType(shardQueryType);

        ISelectionProvider provider = this.getSelectionProvider();
        if ((provider instanceof TreeViewer) && cubridNode != null && cubridNode.isContainer()) {
            TreeViewer viewer = (TreeViewer) provider;
            CommonUITool.refreshNavigatorTree(viewer, cubridNode);
        }
    }
}
