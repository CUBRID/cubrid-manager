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
package com.cubrid.cubridmanager.ui.host.action;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.ui.host.Messages;
import com.cubrid.cubridmanager.ui.spi.util.HostUtils;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;

/**
 * This action is responsible to disconnect host from navigator
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class DisConnectHostAction extends SelectionAction {
    private static final Logger LOGGER = LogUtil.getLogger(DisConnectHostAction.class);
    public static final String ID = DisConnectHostAction.class.getName();

    public DisConnectHostAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    public DisConnectHostAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    public boolean allowMultiSelections() {
        return true;
    }

    public boolean isSupported(Object obj) {
        return isSupportedNode(obj);
    }

    public static boolean isSupportedNode(Object obj) {
        if (obj instanceof CubridServer) {
            CubridServer server = (CubridServer) obj;
            return server.isConnected();
        } else if (obj instanceof Object[]) {
            return true;
        }
        return false;
    }

    public void run() {
        Object[] objArr = this.getSelectedObj();
        if (objArr == null || objArr.length <= 0) {
            setEnabled(false);
            return;
        }

        doRun(objArr);
    }

    public void doRun(Object[] objArr) {
        if (CubridNavigatorView.findNavigationView() == null) {
            LOGGER.error("CubridNavigatorView.findNavigationView() is null.");
            return;
        }

        ISelectionProvider provider = CubridNavigatorView.findNavigationView().getViewer();
        if (!(provider instanceof TreeViewer)) {
            return;
        }

        List<CubridServer> connectedServers = new ArrayList<CubridServer>();
        StringBuffer hostNames = new StringBuffer();
        for (int i = 0; objArr != null && i < objArr.length; i++) {
            if (!isSupported(objArr[i])) {
                setEnabled(false);
                continue;
            }
            ICubridNode node = (ICubridNode) objArr[i];
            if (node instanceof CubridServer) {
                connectedServers.add((CubridServer) node);
                hostNames.append(node.getLabel());
                if (i != objArr.length - 1) {
                    hostNames.append(",");
                }
            }
        }

        String msg = Messages.bind(Messages.msgConfirmDisconnectHost, hostNames.toString());
        boolean isDisconnectHost = CommonUITool.openConfirmBox(getShell(), msg);
        if (!isDisconnectHost) {
            return;
        }

        for (CubridServer server : connectedServers) {
            boolean isContinue = HostUtils.processHostDisconnected(server);
            if (isContinue) {
                TreeViewer viewer = (TreeViewer) provider;
                viewer.refresh(server, true);
                viewer.setSelection(null, true);
                viewer.setSelection(new StructuredSelection(server), true);

                ActionManager.getInstance().fireSelectionChanged(getSelection());
                LayoutManager.getInstance().fireSelectionChanged(getSelection());
                CubridNodeManager.getInstance()
                        .fireCubridNodeChanged(
                                new CubridNodeChangedEvent(
                                        server, CubridNodeChangedEventType.SERVER_DISCONNECTED));
            }
        }
    }
}
