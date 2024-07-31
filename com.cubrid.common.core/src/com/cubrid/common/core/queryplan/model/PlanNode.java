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
package com.cubrid.common.core.queryplan.model;

import com.cubrid.common.core.util.StringUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * Plan Node model class
 *
 * <p>PlanNode Description
 *
 * @author pcraft
 * @version 1.0 - 2009. 06. 06 created by pcraft
 */
public class PlanNode {
    private int depth = 1;
    private String method = null;
    private String position = null;
    private PlanCost cost = null;
    private PlanTable table = null;
    private PlanTerm index = null;
    private PlanTerm edge = null;
    private PlanTerm sargs = null;
    private PlanTerm filter = null;
    private String sort = null;
    private String order = null;
    private List<PlanNode> children = null;

    /**
     * Convert to string
     *
     * @return string
     */
    public String toString() { // FIXME use ToStringBuilder
        StringBuilder tabs = new StringBuilder();
        for (int i = 0; i < depth - 1; i++) {
            tabs.append("\t");
        }
        StringBuilder out = new StringBuilder();
        out.append("PlanNode[");
        out.append("\n").append(tabs).append("\tmethod=").append(method);
        out.append(", \n").append(tabs).append("\tdepth=").append(depth);
        out.append(", \n").append(tabs).append("\tcost=").append(cost);
        out.append(", \n").append(tabs).append("\ttable=").append(table);
        out.append(", \n").append(tabs).append("\tindex=").append(PlanTerm.toString(index, depth));
        out.append(", \n").append(tabs).append("\tedge=").append(PlanTerm.toString(edge, depth));
        out.append(", \n").append(tabs).append("\tsargs=").append(PlanTerm.toString(sargs, depth));
        out.append(", \n")
                .append(tabs)
                .append("\tfilter=")
                .append(PlanTerm.toString(filter, depth));
        out.append(", \n").append(tabs).append("\tsort=").append(sort);
        out.append(", \n").append(tabs).append("\torder=").append(order);
        out.append(", \n")
                .append(tabs)
                .append("\tchildren=")
                .append(PlanNode.toString(children, depth));
        out.append("\n").append(tabs).append("]\n");
        return out.toString();
    }

    /** For debugging to display for more pretty */
    public static String toString(List<PlanNode> planNodes, int depth) {
        if (planNodes == null) {
            return null;
        }

        StringBuilder tabs = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            tabs.append("\t");
        }

        StringBuilder out = new StringBuilder();
        for (PlanNode planNode : planNodes) {
            if (out.length() > 0) {
                out.append(",\n");
                out.append(tabs);
            }
            out.append(StringUtil.trim(planNode.toString()));
        }

        return out.toString();
    }

    public String getDebugString() {
        StringBuilder out = new StringBuilder();

        out.append("PlanNode[");
        out.append("method=").append(method);
        out.append(", depth=").append(depth);
        out.append(", cost=").append(cost);
        out.append(", table=").append(table);
        out.append(", index=").append(index);
        out.append(", edge=").append(edge);
        out.append(", sargs=").append(sargs);
        out.append(", filter=").append(filter);
        out.append(", sort=").append(sort);
        out.append(", order=").append(order);
        out.append("]");

        return out.toString();
    }

    /**
     * Create the new child plan node
     *
     * @return the created PlanNode object
     */
    public PlanNode newChild() {

        if (children == null) {
            children = new ArrayList<PlanNode>();
        }

        PlanNode planTree = new PlanNode();
        planTree.depth = this.depth + 1;

        this.children.add(planTree);

        return planTree;
    }

    public void addChild(PlanNode planNode) {
        if (children == null) {
            children = new ArrayList<PlanNode>();
        }

        children.add(planNode);
    }

    public List<PlanNode> getChildren() {
        return children;
    }

    public PlanTerm getFilter() {
        return filter;
    }

    public void setFilter(PlanTerm filter) {
        this.filter = filter;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public PlanCost getCost() {
        return cost;
    }

    public void setCost(PlanCost cost) {
        this.cost = cost;
    }

    public PlanTable getTable() {
        return table;
    }

    public void setTable(PlanTable table) {
        this.table = table;
    }

    public PlanTerm getIndex() {
        return index;
    }

    public void setIndex(PlanTerm index) {
        this.index = index;
    }

    public PlanTerm getEdge() {
        return edge;
    }

    public void setEdge(PlanTerm edge) {
        this.edge = edge;
    }

    public PlanTerm getSargs() {
        return sargs;
    }

    public void setSargs(PlanTerm sargs) {
        this.sargs = sargs;
    }

    public String getMethodTitle() {
        if (method == null) {
            return null;

        } else if (method.equals("iscan")) {
            return "Index Scan";

        } else if (method.equals("sscan")) {
            return "Full Scan";

        } else if (method.equals("temp(group by)")) {
            return "Group by Temp";

        } else if (method.equals("temp(order by)")) {
            return "Order by Temp";

        } else if (method.equals("nl-join (inner join)")) {
            return "Nested Loop - Inner Join";

        } else if (method.equals("nl-join (cross join)")) {
            return "Nested Loop - Cross Join";

        } else if (method.equals("idx-join (inner join)")) {
            return "Index Join - Inner Join";

        } else if (method.equals("m-join (inner join)")) {
            return "Merged - Inner Join";

        } else if (method.equals("temp")) {
            return "Temp";

        } else if (method.equals("follow")) {
            return "Follow";

        } else {
            return method;
        }
    }
}
