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

import com.cubrid.common.ui.common.dialog.GroupSettingDialog;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.spi.ICubridGroupNodeManager;
import com.cubrid.common.ui.spi.util.CommonUITool;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;

/**
 * Filter setting action for tree
 *
 * @author pangqiren
 * @version 1.0 - 2010-12-1 created by pangqiren
 */
public abstract class GroupSettingAction extends Action {

    public static final String ID = GroupSettingAction.class.getName();
    private CubridNavigatorView cubridNavigatorView;
    private final Shell shell;

    public GroupSettingAction(
            Shell shell,
            String text,
            ImageDescriptor image,
            ImageDescriptor imageDisabled,
            CubridNavigatorView cubridNavigatorView) {
        super(text);
        setId(ID);
        this.shell = shell;
        this.setToolTipText(text);
        this.setImageDescriptor(image);
        this.setDisabledImageDescriptor(imageDisabled);
        this.cubridNavigatorView = cubridNavigatorView;
    }

    public void setNavigatorView(CubridNavigatorView cubridNavigatorView) {
        this.cubridNavigatorView = cubridNavigatorView;
    }

    /** Filter */
    public void run() {
        GroupSettingDialog dialog = new GroupSettingDialog(shell, getGroupManager());
        if (dialog.open() == Dialog.OK && cubridNavigatorView.isShowGroup()) {
            Object[] objs = cubridNavigatorView.getViewer().getExpandedElements();
            CommonUITool.refreshNavigatorTree(cubridNavigatorView.getViewer(), null, objs);
        }
        cubridNavigatorView.setFocus();
    }

    /**
     * Get group manager
     *
     * @return ICubridGroupNodeManager
     */
    protected abstract ICubridGroupNodeManager getGroupManager();
}
