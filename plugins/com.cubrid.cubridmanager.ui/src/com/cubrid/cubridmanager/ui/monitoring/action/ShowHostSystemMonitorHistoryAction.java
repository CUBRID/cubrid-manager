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

import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.DefaultCubridNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.common.model.StatusMonitorAuthType;
import com.cubrid.cubridmanager.ui.monitoring.Messages;
import com.cubrid.cubridmanager.ui.monitoring.editor.HostSystemMonitorHistoryViewPart;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

/**
 * This class is an action in order to control how to add a monitor template
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-8-4 created by lizhiqiang
 */
public class ShowHostSystemMonitorHistoryAction extends SelectionAction {

    public static final String ID = ShowHostSystemMonitorHistoryAction.class.getName();
    public static final String NODE_SEPARATOR = "/";
    private static final String HOST_SYSMON_HISTORY_ID = "hostSysMonHistoryId";

    /**
     * @param shell
     * @param provider
     * @param text
     * @param icon
     */
    public ShowHostSystemMonitorHistoryAction(
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
    public ShowHostSystemMonitorHistoryAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    /** Override the run method in order to complete adding a monitor template */
    public void run() {
        Object[] obj = this.getSelectedObj();
        ICubridNode parent = (ICubridNode) obj[0];

        String hostSysMonHistoryId = parent.getId() + NODE_SEPARATOR + HOST_SYSMON_HISTORY_ID;
        ICubridNode hostSysMonHistoryNode =
                new DefaultCubridNode(
                        hostSysMonHistoryId,
                        Messages.msgBrokerHistoryStatusName,
                        "icons/navigator/status_item.png");
        hostSysMonHistoryNode.setType(CubridNodeType.SYSTEM_MONITOR_TEMPLATE);
        hostSysMonHistoryNode.setViewId(HostSystemMonitorHistoryViewPart.ID);
        hostSysMonHistoryNode.setContainer(false);
        LayoutManager.getInstance().getWorkbenchContrItem().openEditorOrView(hostSysMonHistoryNode);
    }

    /**
     * Return whether this action support this object,if not support,this action will be disabled
     *
     * @param obj Object
     * @return boolean true if supported , false otherwise
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#isSupported(java .lang.Object)
     */
    public boolean isSupported(Object obj) {
        if (obj instanceof ICubridNode) {
            ICubridNode node = (ICubridNode) obj;
            if (!CubridNodeType.MONITOR_FOLDER.equals(node.getType())) {
                return false;
            }
            ServerUserInfo userInfo = node.getServer().getServerInfo().getLoginedUserInfo();
            if (userInfo == null
                    || (StatusMonitorAuthType.AUTH_ADMIN != userInfo.getStatusMonitorAuth()
                            && StatusMonitorAuthType.AUTH_MONITOR
                                    != userInfo.getStatusMonitorAuth())) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Makes this action not support to select multiple object
     *
     * @return boolean true if allowed ,false otherwise
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#allowMultiSelections ()
     */
    public boolean allowMultiSelections() {
        return false;
    }
}
