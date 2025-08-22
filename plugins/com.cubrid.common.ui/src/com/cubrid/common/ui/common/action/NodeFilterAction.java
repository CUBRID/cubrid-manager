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
import com.cubrid.common.ui.spi.model.ICubridNode;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * Node filter action
 *
 * @author pangqiren
 * @version 1.0 - 2010-12-1 created by pangqiren
 */
public class NodeFilterAction extends Action {

    private final TreeViewer tv;
    private final ICubridNode cubridNode;

    public NodeFilterAction(
            String text, ImageDescriptor image, TreeViewer treeViewer, ICubridNode cubridNode) {
        super(text, IAction.AS_CHECK_BOX);
        setId(text);
        this.setToolTipText(text);
        this.setImageDescriptor(image);
        tv = treeViewer;
        this.cubridNode = cubridNode;
    }

    /** Filter */
    public void run() {
        if (tv == null || cubridNode == null) {
            return;
        }
        if (isChecked()) {
            NodeFilterManager.getInstance().addIdFilter(cubridNode.getId());
            tv.setFilters(NodeFilterManager.getInstance().getViewerFilter());
        } else {
            NodeFilterManager.getInstance().removeIdFilter(cubridNode.getId());
            tv.setFilters(NodeFilterManager.getInstance().getViewerFilter());
            tv.expandToLevel(cubridNode, 1);
        }
    }
}
