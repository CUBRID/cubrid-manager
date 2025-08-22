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
package com.cubrid.cubridmanager.ui.mondashboard.action;

import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HANode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.DashboardPart;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.HANodePart;
import java.util.Iterator;
import java.util.List;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;

/**
 * Refresh dashboard action
 *
 * @author cyl
 * @version 1.0 - 2010-6-25 created by cyl
 */
public class DashboardRefreshAction extends SelectionAction {

    public static final String ID = DashboardRefreshAction.class.getName();

    /**
     * refresh dashboard action constructor.
     *
     * @param shell window.getShell()
     * @param text action text
     * @param icon ImageDescriptor
     */
    public DashboardRefreshAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    /**
     * refresh dashboard constructor.
     *
     * @param shell window.getShell()
     * @param Iprovider SelectionProvider
     * @param text action text
     * @param icon ImageDescriptor
     */
    protected DashboardRefreshAction(
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
     * Is action support the selected object
     *
     * @param obj first selection.
     * @return true support; false not support
     */
    public boolean isSupported(Object obj) {
        if (obj instanceof DashboardPart) {
            DashboardPart dp = (DashboardPart) obj;
            return dp.getChildren().size() > 0;
        }
        return true;
    }

    /**
     * refresh dashboard run.
     *
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run() {
        Object[] objArr = this.getSelectedObj();
        if (objArr == null || objArr.length <= 0 || !isSupported(objArr[0])) {
            setEnabled(false);
            return;
        }
        DashboardPart dp = null;
        if (objArr[0] instanceof DashboardPart) {
            dp = (DashboardPart) objArr[0];
        } else {
            dp = (DashboardPart) ((EditPart) objArr[0]).getParent();
        }
        // Clear error messages
        IStructuredSelection selection = (IStructuredSelection) getSelection();
        Iterator<?> iterator = selection.iterator();
        while (iterator.hasNext()) {
            Object objSelected = iterator.next();
            if (objSelected instanceof HANodePart) {
                HANodePart ep = (HANodePart) objSelected;
                ((HANode) ep.getModel()).clearErrorMessages();
            }
        }
        dp.refresh();
        List<?> childEditPart = dp.getChildren();
        for (Object child : childEditPart) {
            ((EditPart) child).refresh();
        }
    }
}
