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
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * Collapse the selected TreeViewer
 *
 * @author pangqiren
 * @version 1.0 - 2010-11-30 created by pangqiren
 * @version 1.1 - 2012-09-05 updated by Isaiah Choe
 */
public class CollapseAllAction extends Action {
    public static final String ID = CollapseAllAction.class.getName();
    private TreeViewer treeViewer;

    public CollapseAllAction(String text, ImageDescriptor image, TreeViewer treeViewer) {
        super(text);
        setId(ID);
        this.setToolTipText(text);
        this.setImageDescriptor(image);
        this.treeViewer = treeViewer;
    }

    public void setTargetTreeViewer(TreeViewer treeViewer) {
        this.treeViewer = treeViewer;
    }

    public void run() {
        if (treeViewer != null) {
            // You can use TreeViewer.collapseAll(), but this will influence the search operation.
            // After call it, then search, it will not be able to expand the tree.
            // Hence, you should use the below method.
            Object inputObj = treeViewer.getInput();
            if (!(inputObj instanceof List<?>)) {
                return;
            }
            List<?> list = (List<?>) inputObj;
            for (Object obj : list) {
                if (!(obj instanceof ICubridNode)) {
                    continue;
                }
                treeViewer.collapseToLevel(obj, 1);
            }
        }
    }
}
