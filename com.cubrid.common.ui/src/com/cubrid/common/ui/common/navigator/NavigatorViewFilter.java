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
package com.cubrid.common.ui.common.navigator;

import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import java.util.List;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Navigator viewer filter
 *
 * @author pangqiren
 * @version 1.0 - 2010-11-29 created by pangqiren
 */
public class NavigatorViewFilter extends ViewerFilter {

    /**
     * Returns whether the given element makes it through this filter.
     *
     * @param viewer the viewer
     * @param parentElement the parent element
     * @param element the element
     * @return <code>true</code> if element is included in the filtered set, and <code>false</code>
     *     if excluded
     */
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        ICubridNode parent = null;
        if (parentElement instanceof ICubridNode) {
            parent = (ICubridNode) parentElement;
        }
        ICubridNode filteredNode = null;
        if (element instanceof ICubridNode) {
            filteredNode = (ICubridNode) element;
        }
        if (filteredNode == null) {
            return true;
        }
        return select(viewer, parent, filteredNode);
    }

    /**
     * Returns whether the given node makes it through this filter.
     *
     * @param viewer the viewer
     * @param parent the parent node
     * @param node the node
     * @return <code>true</code> if node is included in the filtered set, and <code>false</code> if
     *     excluded
     */
    protected boolean select(Viewer viewer, ICubridNode parent, ICubridNode node) {
        if (NodeFilterManager.getInstance().isExistIdFilter(node.getId())) {
            return false;
        } else if (parent == null && node.getType().equals(NodeType.GROUP)) {
            int matched = 0;
            List<ICubridNode> nodes = node.getChildren();
            if (nodes == null || nodes.size() == 0) {
                return !NodeFilterManager.getInstance().isMatch(node.getLabel());
            }
            for (ICubridNode cnode : nodes) {
                if (cnode == null) {
                    continue;
                }
                if (!NodeFilterManager.getInstance().isMatch(cnode.getLabel())
                        && cnode instanceof ICubridNode
                        && (cnode.getType().equals(NodeType.DATABASE)
                                || cnode.getType().equals(NodeType.SERVER))) {
                    matched++;
                    break;
                }
            }

            if (matched == 0) {
                return false;
            }
        } else if (parent != null && parent.getType().equals(NodeType.GROUP)) {
            return !NodeFilterManager.getInstance().isMatch(node.getLabel());
        } else if (parent == null && node.getType().equals(NodeType.MONITOR_DASHBOARD)
                || parent == null && node.getType().equals(NodeType.MONITOR_STATISTIC_PAGE)) {
            return !NodeFilterManager.getInstance().isMatch(node.getLabel());
        } else if (NodeFilterManager.getInstance().isMatch(node.getLabel())
                && node instanceof ICubridNode
                && (parent == null && node.getType().equals(NodeType.DATABASE)
                        || parent == null && node.getType().equals(NodeType.SERVER))) {
            return false;
        }

        return true;
    }
}
