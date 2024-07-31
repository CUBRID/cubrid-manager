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

package com.cubrid.common.core.queryplan;

import com.cubrid.common.core.queryplan.model.PlanNode;
import com.cubrid.common.core.queryplan.model.PlanResult;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Query Plan model class
 *
 * <p>StructQueryPlan Description
 *
 * @author pcraft
 * @version 1.0 - 2009. 06. 06 created by pcraft
 */
public class StructQueryPlan implements Cloneable {
    private String sql = null;
    private String planRaw = null;
    private final PlanParser parser;
    private final Date created;

    public StructQueryPlan(String query, String plan, Date created) {
        this.sql = query;
        this.planRaw = plan;
        this.created = created == null ? null : (Date) created.clone();
        this.parser = new PlanParser();
        this.parser.doParse(plan);
    }

    public Date getCreated() {
        return created == null ? null : (Date) created.clone();
    }

    /**
     * get date string
     *
     * @return string
     */
    public String getCreatedDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return sdf.format(getCreated());
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getPlanRaw() {
        return planRaw;
    }

    public void setPlanRaw(String planRaw) {
        this.planRaw = planRaw;
    }

    /**
     * count sub plan
     *
     * @return int
     */
    public int countSubPlan() {
        return parser.countPlanTree();
    }

    /**
     * get the sub plan
     *
     * @param index int
     * @return planRoot
     */
    public PlanResult getSubPlan(int index) {
        return parser.getPlanTree(index);
    }

    /**
     * Calculate the cost
     *
     * @return
     */
    public int calCost() {
        int costVal = 0;
        for (int i = 0, len = countSubPlan(); i < len; i++) {
            PlanResult result = getSubPlan(i);
            if (result == null) {
                continue;
            }

            PlanNode node = result.getPlanNode();
            if (node != null) {
                costVal += node.getCost().getTotal();
            }
        }
        return costVal;
    }
    /**
     * @see java.lang.Object#toString()
     * @return a string representation of the object.
     */
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("StructQueryPlan[");
        out.append("\tplanRaw=").append(getPlanRaw()).append("\n");
        out.append("\tcreated=").append(getCreated()).append("\n");
        out.append("\tplanNode=").append(parser).append("\n");
        out.append("]");

        return out.toString();
    }

    /**
     * set to xml
     *
     * @return string
     */
    public String toXML() {
        StringBuilder out = new StringBuilder();
        out.append("<plan created=\"").append(getCreatedDateString()).append("\">\n");
        out.append("\t<sql><![CDATA[").append(sql).append("]]></sql>\n");
        out.append("\t<res><![CDATA[").append(planRaw).append("]]></res>\n");
        out.append("</plan>\n");

        return out.toString();
    }

    /**
     * serialize
     *
     * @param sqArray List<StructQueryPlan>
     * @return string
     */
    public static String serialize(List<StructQueryPlan> sqArray) {
        StringBuilder out = new StringBuilder();
        out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        out.append("<plans version=\"2013\">\n");
        for (StructQueryPlan sq : sqArray) {
            out.append(sq.toXML());
        }
        out.append("</plans>\n");

        return out.toString();
    }

    /**
     * unserialize
     *
     * @param xml String
     * @return List<StructQueryPlan>
     */
    public static List<StructQueryPlan> unserialize(String xml) {
        if (xml == null || xml.length() == 0) {
            return null;
        }

        int len = xml.length();

        int sp = xml.indexOf("<plans ");
        if (sp == -1) {
            return null;
        }

        int ep = xml.indexOf("</plans>", sp);
        if (ep == -1) {
            return null;
        }

        String patternDate =
                "<plan[ ]+created[ ]*=[ ]*\"([0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2})\">";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

        List<StructQueryPlan> sqList = new ArrayList<StructQueryPlan>();

        for (; ; ) {
            if (sp >= len - 1) {
                break;
            }

            sp = xml.indexOf("<plan", sp + 1);
            if (sp == -1) {
                break;
            }

            ep = xml.indexOf('>', sp);
            if (ep == -1) {
                break;
            }

            // <plan created="2009-04-01 11:11:11">
            String row = xml.substring(sp, ep + 1);

            sp = ep + 1;
            Matcher matcher = Pattern.compile(patternDate).matcher(row.trim());
            if (!matcher.matches() || matcher.groupCount() < 1) {
                continue;
            }

            String created = matcher.group(1);

            Date createdDate = null;
            try {
                createdDate = sdf.parse(created);
            } catch (Exception ex) {
                break;
            }

            sp = xml.indexOf("<sql>", sp);
            if (sp == -1) {
                break;
            }
            sp = xml.indexOf("<![CDATA[", sp); // need a XML parser
            sp = sp + 9;

            ep = xml.indexOf("]]>", sp);
            if (ep == -1) {
                break;
            }

            String sql = xml.substring(sp, ep);

            sp = xml.indexOf("<res>", sp);
            if (sp == -1) {
                break;
            }
            sp = xml.indexOf("<![CDATA[", sp);
            sp = sp + 9;

            ep = xml.indexOf("]]>", sp);
            if (ep == -1) {
                break;
            }

            String plan = xml.substring(sp, ep);

            sp = ep + 1;

            sqList.add(new StructQueryPlan(sql, plan, createdDate));
        }

        if (sqList.isEmpty()) {
            return null;
        }

        return sqList;
    }

    /**
     * Clone a object
     *
     * @return StructQueryPlan
     */
    public StructQueryPlan clone() {
        StructQueryPlan structQueryPlan = null;
        try {
            structQueryPlan = (StructQueryPlan) super.clone();
        } catch (CloneNotSupportedException e) {
        }
        return structQueryPlan;
    }
}
