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
package com.cubrid.cubridmanager.ui.monstatistic.action;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.MonitorStatistic;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.ui.common.navigator.CubridMonitorNavigatorView;
import com.cubrid.cubridmanager.ui.monstatistic.Messages;
import com.cubrid.cubridmanager.ui.monstatistic.dialog.AddStatisticPageDialog;
import com.cubrid.cubridmanager.ui.monstatistic.editor.MonitorStatisticEditor;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import com.cubrid.cubridmanager.ui.spi.persist.MonitorStatisticPersistManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Create a new monitor statistic page.
 *
 * @author Santiago Wang
 * @version 1.0 - 2013-6-25 created by Santiago Wang
 */
public class AddMonitorStatisticPageAction extends SelectionAction {
    public static final String ID = AddMonitorStatisticPageAction.class.getName();

    /**
     * Constructor
     *
     * @param shell the current shell
     * @param text the used text
     * @param icon the used icon
     */
    public AddMonitorStatisticPageAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    /**
     * Constructor
     *
     * @param shell the current shell
     * @param provider the selected provider
     * @param text the used text
     * @param icon the used icon
     */
    protected AddMonitorStatisticPageAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    /** Override the run method in order to open an instance of status monitor dialog */
    public void run() {
        Object[] obj = this.getSelectedObj();
        ICubridNode node = null;
        if (obj.length > 0) {
            node = (ICubridNode) obj[0];
        }

        String hostId = null;
        boolean isSingleHost = node != null && CubridNodeType.MONITOR_FOLDER.equals(node.getType());
        if (isSingleHost) {
            ServerInfo serverInfo = node.getServer().getServerInfo();
            hostId = serverInfo.getServerName();
            if (!serverInfo.isSupportMonitorStatistic()) {
                CommonUITool.openWarningBox(Messages.needEnableMonitorStatisticMsg);
                return;
            }
        }

        AddStatisticPageDialog dialog = new AddStatisticPageDialog(getShell());
        dialog.setCubridServerId(hostId);
        if (dialog.open() == Dialog.OK) {
            final String iconPath = "icons/navigator/status_item.png";
            String labelName = dialog.getName();
            String pageName = labelName;
            if (hostId != null) {
                pageName += "@" + hostId;
            }
            MonitorStatistic monitorStatisticNode =
                    new MonitorStatistic(pageName, labelName, iconPath);
            monitorStatisticNode.setType(CubridNodeType.MONITOR_STATISTIC_PAGE);
            monitorStatisticNode.setEditorId(MonitorStatisticEditor.ID);

            if (isSingleHost) {
                monitorStatisticNode.setMultiHost(false);
                ServerInfo serverInfo = node.getServer().getServerInfo();
                monitorStatisticNode.setIp(serverInfo.getHostAddress());
                monitorStatisticNode.setPort(serverInfo.getHostMonPort());
            } else {
                monitorStatisticNode.setMultiHost(true);
            }

            MonitorStatisticPersistManager persistManager =
                    MonitorStatisticPersistManager.getInstance();
            persistManager.addMonitorStatistic(monitorStatisticNode, hostId);
            persistManager.saveStatistic();
            // refresh TreeViewer
            TreeViewer treeViewer = (TreeViewer) this.getSelectionProvider();
            if (isSingleHost) {
                node.addChild(monitorStatisticNode);
                treeViewer.add(node, monitorStatisticNode);
            } else {
                /*TOOLS-3665 Refresh the input of TreeViewer*/
                treeViewer.setInput(CubridMonitorNavigatorView.getTreeViewerInput());
            }
            CubridNodeManager.getInstance()
                    .fireCubridNodeChanged(
                            new CubridNodeChangedEvent(
                                    monitorStatisticNode, CubridNodeChangedEventType.NODE_ADD));

            try {
                IWorkbench workbench = PlatformUI.getWorkbench();
                IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
                workbenchWindow
                        .getActivePage()
                        .openEditor(monitorStatisticNode, MonitorStatisticEditor.ID);
            } catch (Exception ignore) {
            }
        } else {
            // TODO
        }
    }

    /**
     * Makes this action not support for select multiple object
     *
     * @return boolean true if allowed ,false otherwise
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#allowMultiSelections ()
     */
    public boolean allowMultiSelections() {
        return false;
    }

    /**
     * Return whether this action support this object,if not support,this action will be disabled
     *
     * @param obj Object the given object
     * @return boolean true if is supported , false otherwise
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#isSupported(java .lang.Object)
     */
    public boolean isSupported(Object obj) {
        if (obj instanceof ICubridNode) {
            ICubridNode node = (ICubridNode) obj;
            if (CubridNodeType.MONITOR_FOLDER.equals(node.getType())) {
                ServerInfo serverInfo = node.getServer().getServerInfo();
                if (serverInfo == null || !CompatibleUtil.isSupportMonitorStatistic(serverInfo)) {
                    return false;
                }
                return true;
            } else if (CubridNodeType.MONITOR_DASHBOARD.equals(node.getType())) {
                return true;
            } else if (CubridNodeType.MONITOR_STATISTIC_PAGE.equals(node.getType())) {
                return true;
            } else {
                return false;
            }
        } else if (obj == null) {
            return true;
        }
        return false;
    }
}
