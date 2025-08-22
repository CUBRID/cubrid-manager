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

import com.cubrid.common.ui.common.dialog.GroupEditDialog;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.spi.ICubridGroupNodeManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridGroupNode;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

/**
 * Filter setting action for tree
 *
 * @author Kevin
 * @version 1.0 - 2011-3-29 created by Kevin
 */
public class GroupPropertyAction extends SelectionAction {

    public static final String ID = GroupPropertyAction.class.getName();

    private String navigatorViewId = "";

    public GroupPropertyAction(
            Shell shell, String text, ImageDescriptor enabledIcon, ImageDescriptor disabledIcon) {
        this(shell, null, text, enabledIcon, disabledIcon);
    }

    public GroupPropertyAction(
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

    public void setNavigatorViewId(String id) {
        navigatorViewId = id;
    }

    /** Filter */
    public void run() {
        Object[] selected = this.getSelectedObj();
        if (selected == null || selected.length == 0) {
            return;
        }
        if (!(selected[0] instanceof CubridGroupNode)) {
            return;
        }
        CubridNavigatorView cubridNavigatorView =
                CubridNavigatorView.getNavigatorView(navigatorViewId);
        if (cubridNavigatorView == null) {
            return;
        }
        TreeViewer tv = cubridNavigatorView.getViewer();
        CubridGroupNode group = (CubridGroupNode) selected[0];
        GroupEditDialog dialog =
                new GroupEditDialog(
                        shell,
                        cubridNavigatorView.getGroupNodeManager(),
                        cubridNavigatorView.getGroupNodeManager().getAllGroupNodes(),
                        group);
        if (dialog.open() == Dialog.OK) {
            Object[] objs = tv.getExpandedElements();
            cubridNavigatorView.setShowGroup(true);
            if (objs != null) {
                tv.setExpandedElements(objs);
            }
        }
    }

    /**
     * Return whether allow multi-selection
     *
     * @return boolean
     */
    public boolean allowMultiSelections() {
        return false;
    }

    /**
     * Support CubridGroupNode except Default Group Node
     *
     * @param obj Object that selected.
     * @return support or not.
     */
    public boolean isSupported(Object obj) {
        boolean ins = obj instanceof CubridGroupNode;
        if (!ins) {
            return false;
        }
        CubridGroupNode group = (CubridGroupNode) obj;

        return !group.getId().equals(ICubridGroupNodeManager.DEFAULT_GROUP_NODE.getId());
    }
}
