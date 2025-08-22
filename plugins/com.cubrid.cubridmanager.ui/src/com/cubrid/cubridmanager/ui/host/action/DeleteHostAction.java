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
package com.cubrid.cubridmanager.ui.host.action;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridGroupNode;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.ui.common.navigator.CubridHostNavigatorView;
import com.cubrid.cubridmanager.ui.host.Messages;
import com.cubrid.cubridmanager.ui.spi.persist.CMGroupNodePersistManager;
import com.cubrid.cubridmanager.ui.spi.util.HostUtils;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;

/**
 * This action is responsible to delete host from navigator
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class DeleteHostAction extends SelectionAction {
    private static final Logger LOGGER = LogUtil.getLogger(DeleteHostAction.class);
    public static final String ID = DeleteHostAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public DeleteHostAction(Shell shell, String text, ImageDescriptor icon) {
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
    public DeleteHostAction(
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
        return true;
    }

    /**
     * Return whether this action support this object,if not support,this action will be disabled
     *
     * @param obj the Object
     * @return <code>true</code> if support this obj;<code>false</code> otherwise
     */
    public boolean isSupported(Object obj) {
        if (obj instanceof CubridServer || obj instanceof Object[]) {
            return true;
        }
        return false;
    }

    /** Delete the selected hosts */
    public void run() {
        Object[] objArr = this.getSelectedObj();
        if (objArr == null || objArr.length <= 0) {
            setEnabled(false);
            return;
        }

        doRun(objArr);
    }

    /**
     * Perform do run
     *
     * @param objArr
     */
    public void doRun(Object[] objArr) {
        CubridNavigatorView navigatorView =
                CubridNavigatorView.getNavigatorView(CubridHostNavigatorView.ID);
        if (navigatorView == null) {
            return;
        }

        StringBuilder hostNames = new StringBuilder();
        for (int i = 0; objArr != null && i < objArr.length; i++) {
            if (!isSupported(objArr[i])) {
                setEnabled(false);
                return;
            }
            ICubridNode node = (ICubridNode) objArr[i];
            hostNames.append(node.getLabel());
            if (i != objArr.length - 1) {
                hostNames.append(",");
            }
        }

        String msg = Messages.bind(Messages.msgConfirmDeleteHost, hostNames.toString());
        boolean isDelete = CommonUITool.openConfirmBox(getShell(), msg);
        if (!isDelete) {
            return;
        }

        TreeViewer viewer = navigatorView.getViewer();
        if (objArr == null) {
            LOGGER.error("objArr is null.");
            return;
        }
        for (int i = 0; i < objArr.length; i++) {
            CubridServer server = (CubridServer) objArr[i];
            boolean isContinue = HostUtils.processHostDeleted(server);
            List<CubridGroupNode> groups =
                    CMGroupNodePersistManager.getInstance().getAllGroupNodes();
            for (CubridGroupNode grp : groups) {
                grp.removeChild(server);
            }
            if (isContinue) {
                viewer.remove(server);
            }
        }
    }
}
