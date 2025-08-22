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

import com.cubrid.common.ui.common.navigator.NodeFilterManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.ICubridNode;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

/**
 * Hidden the navigator element action
 *
 * @author pangqiren
 * @version 1.0 - 2010-12-2 created by pangqiren
 */
public class HiddenElementAction extends SelectionAction {
    public static final String ID = HiddenElementAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public HiddenElementAction(Shell shell, String text, ImageDescriptor icon) {
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
    public HiddenElementAction(
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
        if (obj == null) {
            return false;
        }
        if (obj instanceof ICubridNode) {
            return true;
        } else if (obj instanceof Object[]) {
            Object[] objs = (Object[]) obj;
            for (Object node : objs) {
                if (!(node instanceof ICubridNode)) {
                    return false;
                }
            }
            return objs.length > 0;
        }
        return false;
    }

    /** Hidden the elements */
    public void run() {
        Object[] objArr = this.getSelectedObj();
        if (!isSupported(objArr)) {
            setEnabled(false);
            return;
        }
        ISelectionProvider provider = this.getSelectionProvider();
        if (!(provider instanceof TreeViewer)) {
            return;
        }
        List<String> idGrayList = new ArrayList<String>();
        List<String> idList = new ArrayList<String>();
        for (Object obj : objArr) {
            if (obj instanceof ICubridNode) {
                ICubridNode node = (ICubridNode) obj;
                idList.add(node.getId());
                addParentToGrayFilter(idGrayList, node.getParent());
            }
        }
        NodeFilterManager.getInstance().addIdFilterList(idList);
        NodeFilterManager.getInstance().addIdGrayFilterList(idGrayList);
        TreeViewer viewer = (TreeViewer) provider;
        viewer.setFilters(NodeFilterManager.getInstance().getViewerFilter());
    }

    /**
     * Add the parent node to filter
     *
     * @param idGrayList List<String>
     * @param node ICubridNode
     */
    private void addParentToGrayFilter(List<String> idGrayList, ICubridNode node) {
        if (node == null) {
            return;
        }
        idGrayList.add(node.getId());
        addParentToGrayFilter(idGrayList, node.getParent());
    }
}
