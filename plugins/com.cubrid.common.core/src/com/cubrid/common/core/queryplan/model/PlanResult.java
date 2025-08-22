/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
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

import java.util.ArrayList;
import java.util.List;

/**
 * Plan Root model class
 *
 * <p>PlanRoot Description
 *
 * @author pcraft
 * @version 1.0 - 2009. 06. 06 created by pcraft
 */
public class PlanResult {
    private String sql = null;
    private String raw = null;
    private String parsedRaw = null;
    private PlanNode planNode = null;

    /**
     * Get plain sql
     *
     * @return the string
     */
    public String getPlainSql() {
        if (sql == null) {
            return "";
        }
        return sql.replaceAll("[\\\r\\\n\\\t]", " ").trim();
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public PlanNode getPlanNode() {
        return planNode;
    }

    public void setPlanNode(PlanNode planNode) {
        this.planNode = planNode;
    }

    public String getParsedRaw() {
        return parsedRaw;
    }

    public void setParsedRaw(String parsedRaw) {
        this.parsedRaw = parsedRaw;
    }

    public String toString() { // FIXME use ToStringBuilder
        if (planNode == null) {
            return null;
        }

        return planNode.toString();
    }

    public List<PlanNode> getOrderedPlanNodes() {
        List<PlanNode> queue = new ArrayList<PlanNode>();
        fetch(queue, getPlanNode());
        return queue;
    }

    private void fetch(List<PlanNode> queue, PlanNode planNode) {
        if (planNode == null) {
            return;
        }

        if (planNode.getChildren() != null) {
            for (PlanNode child : planNode.getChildren()) {
                fetch(queue, child);
            }
        }

        queue.add(planNode);
    }
}
