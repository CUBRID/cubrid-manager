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
package com.cubrid.cubridmanager.ui.mondashboard.action;

import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.util.LayoutUtil;
import com.cubrid.cubridmanager.ui.mondashboard.editor.MonitorDashboardEditor;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Open monitor dash board action
 *
 * @author pangqiren
 * @version 1.0 - 2010-6-3 created by pangqiren
 */
public class OpenMonitorDashboardAction extends SelectionAction {

    public static final String ID = OpenMonitorDashboardAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public OpenMonitorDashboardAction(Shell shell, String text, ImageDescriptor icon) {
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
    public OpenMonitorDashboardAction(
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
        if (obj instanceof ICubridNode) {
            ICubridNode node = (ICubridNode) obj;
            if (CubridNodeType.MONITOR_DASHBOARD.equals(node.getType())) {
                return true;
            }
        }
        return false;
    }

    /** Open the selected monitoring dash board */
    public void run() {
        Object[] objArr = this.getSelectedObj();
        if (objArr == null || objArr.length <= 0) {
            setEnabled(false);
            return;
        }
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }
        ICubridNode node = (ICubridNode) objArr[0];
        IEditorPart editorPart = LayoutUtil.getEditorPart(node, MonitorDashboardEditor.ID);
        if (editorPart == null) {
            LayoutManager.getInstance().getWorkbenchContrItem().openEditorOrView(node);
        } else {
            window.getActivePage().bringToTop(editorPart);
        }
    }
}
