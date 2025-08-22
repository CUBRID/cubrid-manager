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
package com.cubrid.cubridmanager.ui.monitoring.action;

import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.LayoutUtil;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.common.model.StatusMonitorAuthType;
import com.cubrid.cubridmanager.ui.monitoring.Messages;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.CubridStatusMonitorInstance;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

/**
 * This class is an action in order to listen to selection and delete relevant monitor instance.
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-4-7 created by lizhiqiang
 */
public class DelMonitorInstanceAction extends SelectionAction {

    public static final String ID = DelMonitorInstanceAction.class.getName();

    /**
     * The Constructor
     *
     * @param shell
     * @param provider
     * @param text
     * @param icon
     */
    protected DelMonitorInstanceAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    /**
     * The Constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public DelMonitorInstanceAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    /** Deletes the selected status template */
    public void run() {
        Object[] objs = this.getSelectedObj();
        assert (objs != null);
        List<String> nodeNames = new ArrayList<String>();
        for (Object obj : objs) {
            ICubridNode selection = (ICubridNode) obj;
            nodeNames.add(selection.getLabel());
        }
        if (!CommonUITool.openConfirmBox(
                Messages.bind(Messages.delStatusMonitorConfirmContent, nodeNames))) {
            return;
        }

        for (Object obj : objs) {
            ICubridNode selection = (ICubridNode) obj;
            if (CubridNodeType.STATUS_MONITOR_TEMPLATE.equals(selection.getType())) {
                LayoutUtil.closeEditorAndView(selection);
                removeInstance(selection);
            }
        }

        TreeViewer treeViewer = (TreeViewer) this.getSelectionProvider();
        CommonUITool.refreshNavigatorTree(treeViewer, ((ICubridNode) objs[0]).getParent());
    }

    /**
     * Remove the instance from CubridStatusMonitorInstance
     *
     * @param selection the instance of ICubridNode
     */
    private void removeInstance(ICubridNode selection) {
        String selectLbl = selection.getLabel();
        ServerInfo serverInfo = selection.getServer().getServerInfo();
        String prefix = QueryOptions.getPrefix(serverInfo);
        String selectionKey = prefix + QueryOptions.MONITOR_FOLDER_NAME + selectLbl;
        CubridStatusMonitorInstance instance = CubridStatusMonitorInstance.getInstance();
        instance.removeData(selectionKey);
        instance.removeSetting(selectionKey);
        selection.getParent().removeChild(selection);
    }

    /**
     * Makes this action not support to select multiple object
     *
     * @return boolean true if allowed , false otherwise
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#allowMultiSelections ()
     */
    public boolean allowMultiSelections() {
        return true;
    }

    /**
     * Return whether this action support this object,if not support,this action will be disabled
     *
     * @param obj Object
     * @return boolean true if supported , false otherwise
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#isSupported(java .lang.Object)
     */
    public boolean isSupported(Object obj) {
        boolean isTrue = false;
        if (obj instanceof ICubridNode) {
            ICubridNode node = (ICubridNode) obj;
            isTrue = isSupportedSingleNode(node);
        } else if (obj instanceof ICubridNode[]) {
            isTrue = true;
        }
        return isTrue;
    }

    /**
     * Return whether this action support this CUBRID node
     *
     * @param node the ICubridNode object
     * @return true if supported , false otherwise
     */
    private boolean isSupportedSingleNode(ICubridNode node) {
        if (!CubridNodeType.STATUS_MONITOR_TEMPLATE.equals(node.getType())) {
            return false;
        }
        if (com.cubrid.cubridmanager.ui.spi.Messages.msgDbStatusMonitorName.equals(node.getLabel())
                || com.cubrid.cubridmanager.ui.spi.Messages.msgBrokerStatusMonitorName.equals(
                        node.getLabel())) {
            return false;
        }
        ServerUserInfo userInfo = node.getServer().getServerInfo().getLoginedUserInfo();
        if (userInfo == null
                || StatusMonitorAuthType.AUTH_ADMIN != userInfo.getStatusMonitorAuth()) {
            return false;
        }
        return true;
    }
}
