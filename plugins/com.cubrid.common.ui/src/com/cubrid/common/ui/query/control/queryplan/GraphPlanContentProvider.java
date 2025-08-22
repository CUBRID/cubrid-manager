/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.query.control.queryplan;

import com.cubrid.common.core.queryplan.model.PlanNode;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.zest.core.viewers.IGraphContentProvider;

public class GraphPlanContentProvider<K, V> implements IGraphContentProvider {
    public Object[] getElements(Object inputElement) {
        if (!(inputElement instanceof PlanNode)) {
            return null;
        }

        List<GraphPlanNodePair> list = new ArrayList<GraphPlanNodePair>();
        makeList(list, null, (PlanNode) inputElement);
        return list.toArray();
    }

    private void makeList(List<GraphPlanNodePair> list, PlanNode parentNode, PlanNode planNode) {
        if (planNode == null) {
            return;
        }

        if (parentNode != null) {
            GraphPlanNodePair pair = new GraphPlanNodePair();
            list.add(pair);

            // if you need to change directions, you should change following both.
            pair.setSource(parentNode);
            pair.setDest(planNode);
        }

        List<PlanNode> children = planNode.getChildren();
        if (children == null) {
            return;
        }

        for (PlanNode child : children) {
            makeList(list, planNode, child);
        }
    }

    public Object getSource(Object rel) {
        if (!(rel instanceof GraphPlanNodePair)) {
            return null;
        }
        GraphPlanNodePair kv = (GraphPlanNodePair) rel;
        return kv.getSource();
    }

    public Object getDestination(Object rel) {
        if (!(rel instanceof GraphPlanNodePair)) {
            return null;
        }
        GraphPlanNodePair kv = (GraphPlanNodePair) rel;
        return kv.getDest();
    }

    public Object[] getConnectedTo(Object entity) {
        return null;
    }

    public void dispose() {}

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
}
