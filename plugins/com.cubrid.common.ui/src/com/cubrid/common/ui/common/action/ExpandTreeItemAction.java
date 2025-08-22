/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search
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

import com.cubrid.common.ui.spi.model.ICubridNode;
import java.util.Iterator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * Filter setting action for tree
 *
 * @author pangqiren
 * @version 1.0 - 2010-12-1 created by pangqiren
 * @version 1.1 - 2012-09-05 updated by Isaiah Choe
 */
public class ExpandTreeItemAction extends Action {
    public static final String ID = ExpandTreeItemAction.class.getName();
    private TreeViewer treeViewer;

    public ExpandTreeItemAction(String text, ImageDescriptor image, TreeViewer treeViewer) {
        super(text);
        setId(ID);
        this.setToolTipText(text);
        this.setImageDescriptor(image);
        this.treeViewer = treeViewer;
    }

    public void setTargetTreeViewer(TreeViewer treeViewer) {
        this.treeViewer = treeViewer;
    }

    @SuppressWarnings("rawtypes")
    public void run() {
        if (treeViewer != null) {
            ITreeSelection selectedNodes = (ITreeSelection) treeViewer.getSelection();
            Iterator iter = selectedNodes.iterator();
            while (iter.hasNext()) {
                Object node = iter.next();
                if (!(node instanceof ICubridNode)) {
                    continue;
                }

                treeViewer.expandToLevel(node, 1);
            }
        }
    }
}
