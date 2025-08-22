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
package com.cubrid.common.core.queryplan;

import com.cubrid.common.core.queryplan.model.PlanCost;
import com.cubrid.common.core.queryplan.model.PlanNode;
import com.cubrid.common.core.queryplan.model.PlanResult;
import com.cubrid.common.core.queryplan.model.PlanTable;
import com.cubrid.common.core.queryplan.model.PlanTerm;
import com.cubrid.common.core.queryplan.model.PlanTerm.PlanTermType;
import com.cubrid.common.core.queryplan.model.PlanTermItem;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;

/**
 * This class is responsible to parse a raw execution plan string.
 *
 * <p>PlanParser Description
 *
 * @author pcraft
 * @version 1.0 - 2009. 06. 06 created by pcraft
 */
public class PlanParser {
    private static final Logger LOGGER = LogUtil.getLogger(PlanParser.class);

    private List<PlanResult> planTreeList = null;

    /**
     * Get query plan tree
     *
     * @param index the index
     * @return the root node
     */
    public PlanResult getPlanTree(int index) {
        if (planTreeList == null) {
            return null;
        }

        if (planTreeList.size() <= index) {
            return null;
        }

        return planTreeList.get(index);
    }

    /**
     * Get count of this plan tree
     *
     * @return the count
     */
    public int countPlanTree() {
        if (planTreeList == null) {
            return 0;
        }

        return planTreeList.size();
    }

    /**
     * Parse the query plan string
     *
     * @param string the query plan string
     * @return <code>true</code> if successfully;<code>false</code>otherwise
     */
    public boolean doParse(String string) {
        if (StringUtil.isEmpty(string)) {
            return false;
        }

        String newString = string;
        newString = StringUtil.replace(newString, "\r\n", "\n"); // for Win OS
        if (newString == null) {
            return false;
        }

        newString = StringUtil.replace(newString, "\r", "\n"); // for old Mac OS
        if (newString == null) {
            return false;
        }

        List<Integer> eachPlanStartPointList = new ArrayList<Integer>();

        // count subplans
        for (int sp = 0; ; ) {
            sp = newString.indexOf("Join graph segments (f indicates final):", sp);
            if (sp == -1) {
                break;
            }

            eachPlanStartPointList.add(sp);

            sp += 1;
        }

        if (eachPlanStartPointList.isEmpty()) {
            return false;
        }

        LOGGER.debug("<subplanCount>{}</subplanCount>", eachPlanStartPointList.size());

        eachPlanStartPointList.add(newString == null ? 0 : newString.length());

        for (int i = 0, len = eachPlanStartPointList.size() - 1; i < len; i++) {

            int sp = eachPlanStartPointList.get(i);
            int ep = eachPlanStartPointList.get(i + 1);

            String partOfPlanString = newString.substring(sp, ep).trim();
            if (!doParseEachPlan(partOfPlanString)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Parse each query plan string
     *
     * @param string the query plan string
     * @return <code>true</code> if successfully;<code>false</code>otherwise
     */
    private boolean doParseEachPlan(String string) {
        Map<String, String> dic = getReplacementMap(string);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("<query plan raw>{}</query plan raw>", string);
        }

        int sp = string.indexOf("Query stmt:");
        if (sp == -1) {
            return false;
        }

        sp = string.indexOf('\n', sp);
        if (sp == -1) {
            return false;
        }

        String sql = string.substring(sp).trim();

        sp = string.indexOf("Query plan:");
        if (sp == -1) {
            return false;
        }

        sp = string.indexOf("\n\n", sp);
        if (sp == -1) {
            return false;
        }

        sp += 2;

        int ep = string.indexOf("\n\n", sp);
        if (ep == -1) {
            return false;
        }

        StringBuilder planString = new StringBuilder(string.substring(sp, ep));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("<query plan string>{}</query plan string>", planString);
        }

        if (dic != null) {
            Iterator<Entry<String, String>> iter = dic.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, String> entry = iter.next();
                String key = entry.getKey();
                String val = entry.getValue();
                StringUtil.replace(planString, key, val);
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("<query plan binding>{}</query plan binding>", planString);
            }
        }

        String refinedRawPlan = refineRawPlanString(planString.toString());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("<refinedRawPlan>{}</refinedRawPlan>", refinedRawPlan);
        }

        boolean parsed = false;
        PlanNode planNode = new PlanNode();
        try {
            parseTree(planNode, refinedRawPlan, 0, refinedRawPlan.length());
            parsed = true;
        } catch (Exception e) {
            LOGGER.error("Error of plan parser", e);
            planNode = new PlanNode();
        }

        if (planTreeList == null) {
            planTreeList = new ArrayList<PlanResult>();
        }

        PlanResult planRoot = new PlanResult();
        planRoot.setPlanNode(planNode);
        planRoot.setSql(sql);
        planRoot.setRaw(string);
        planRoot.setParsedRaw(refinedRawPlan);

        planTreeList.add(planRoot);

        return parsed;
    }

    /**
     * Refine the raw plan string
     *
     * @param planString the raw plan string
     * @return the refined string
     */
    private String refineRawPlanString(String planString) {

        int lastSpc = 0;
        int depth = 0;

        StringBuilder result = new StringBuilder();
        StringTokenizer token = new StringTokenizer(planString, "\n");
        while (token.hasMoreTokens()) {
            String row = token.nextToken();
            int spc = StringUtil.countSpace(row);

            if (lastSpc < spc) {
                depth++;
            } else if (lastSpc > spc) {
                depth--;
            }

            for (int i = 0; i < depth; i++) {
                result.append("    "); // add for depth spaces
            }

            result.append(row.trim()).append('\n');
            lastSpc = spc;
        }

        return result.toString().replaceAll("\\[[0-9]+\\]", ""); // column + [1] => column
    }

    /**
     * Get replacement map
     *
     * @param string the string
     * @return the map
     */
    private Map<String, String> getReplacementMap(String string) {

        Map<String, String> dic = new HashMap<String, String>();

        int type = 0;

        String[] rows = string.trim().split("\\\n");

        for (int i = 0, len = rows.length; i < len; i++) {

            String row = rows[i].trim();
            if (row.length() == 0) {
                continue;
            }

            String[] arr = row.split(": ");
            if (arr.length == 1) {
                String idx = arr[0];

                type = getTypeValue(idx);

                continue;
            }

            switch (type) {
                case 1:
                case 3:
                    continue;
                case 6:
                case 7:
                    break;
                case 2:
                case 4:
                case 5:
                    if (arr.length == 2) {
                        dic.put(arr[0].trim(), arr[1].trim());
                    }
                    break;
                default:
            }
        }

        return dic;
    }

    /**
     * set type value
     *
     * @param idx the index
     * @return the type
     */
    private int getTypeValue(String idx) {
        int type = 0;
        if (idx.indexOf("Join graph segments (f indicates final)") != -1) {
            type = 1;
            return type;
        }
        if (idx.indexOf("Join graph nodes") != -1) {
            type = 2;
            return type;
        }
        if (idx.indexOf("Join graph equivalence classes") != -1) {
            type = 3;
            return type;
        }
        if (idx.indexOf("Join graph edges") != -1) {
            type = 4;
            return type;
        }
        if (idx.indexOf("Join graph terms") != -1) {
            type = 5;
            return type;
        }
        if (idx.indexOf("Query plan") != -1) {
            type = 6;
            return type;
        }
        if (idx.indexOf("Query stmt") != -1) {
            type = 7;
            return type;
        }
        return type;
    }

    /**
     * Parse the tree
     *
     * @param parent the parent PlanNode
     * @param string the string
     * @param sp the position
     * @param ep the position
     */
    private void parseTree(PlanNode parent, String string, int sp, int ep) {
        int newSp = string.indexOf('\n', sp);
        if (newSp == -1) {
            return;
        }

        parent.setMethod(string.substring(sp, newSp).trim());

        int anotherSp = newSp + 1;

        for (; ; ) {
            int eol = string.indexOf("\n", anotherSp);
            if (eol == -1) {
                eol = ep;
            }

            String row = string.substring(anotherSp, eol);
            int nvSplitPos = row.indexOf(':');
            if (nvSplitPos == -1) {
                break;
            }

            String name = row.substring(0, nvSplitPos).trim();
            if ("outer".equals(name)
                    || "inner".equals(name)
                    || "subplan".equals(name)
                    || "follow".equals(name)
                    || "head".equals(name)) {

                PlanNode child = parent.newChild();
                child.setPosition(name);

                int childSp = string.indexOf(':', anotherSp) + 1;
                int childEp = getEndPositionOfChildNodeString(string, childSp, ep);
                if (childEp == -1) {
                    break;
                }

                String area = string.substring(childSp, childEp).trim();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("<area>" + area + "</area>");
                }

                parseTree(child, string, childSp, childEp);
                eol = childEp;
            } else if ("cost".equals(name)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("cost");
                }
                String partString = row.substring(nvSplitPos + 1).trim();
                parent.setCost(parseCost(partString));
            } else if ("class".equals(name)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("class");
                }
                String partString = row.substring(nvSplitPos + 1).trim();
                parent.setTable(parseClass(partString));
            } else if ("index".equals(name)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("index");
                }
                String partString = row.substring(nvSplitPos + 1).trim();
                PlanTerm planTerm = parseIndex(partString);
                if (planTerm != null) {
                    LOGGER.warn("Can't parse a partString = {}", partString);
                    planTerm.setType(PlanTermType.INDEX);
                    parent.setIndex(planTerm);
                }
            } else if ("edge".equals(name)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("edge");
                }
                String partString = row.substring(nvSplitPos + 1).trim();
                PlanTerm planTerm = parseTerm(partString);
                planTerm.setType(PlanTermType.EDGE);
                parent.setEdge(planTerm);
            } else if ("sargs".equals(name)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("sargs");
                }
                String partString = row.substring(nvSplitPos + 1).trim();
                PlanTerm planTerm = parseTerm(partString);
                planTerm.setType(PlanTermType.SARGS);
                parent.setSargs(planTerm);
            } else if ("filtr".equals(name)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("filtr");
                }
                String partString = row.substring(nvSplitPos + 1).trim();
                PlanTerm planTerm = parseTerm(partString);
                planTerm.setType(PlanTermType.FILTER);
                parent.setFilter(planTerm);
                // filtr: x.c<>'a' (sel 0.999)(sargterm)(not-joineligible)(loc0)
            } else if ("order".equals(name)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("order");
                }
                String partString = row.substring(nvSplitPos + 1).trim();
                parent.setOrder(partString);
            } else if ("others".equals(name)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("others");
                }
            } else if ("sort".equals(name)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("sort");
                }
                String partString = row.substring(nvSplitPos + 1).trim();
                parent.setSort(partString);
            }

            // subplan: m-join(inner join)

            anotherSp = eol + 1;

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("anotherSp=" + anotherSp);
            }

            if (anotherSp >= ep) {
                break;
            }
        }
    }

    /**
     * Get end position of child node string
     *
     * @param string the string
     * @param sp the position
     * @param ep the position
     * @return the end position
     */
    private int getEndPositionOfChildNodeString(String string, int sp, int ep) {

        int anotherSp = string.indexOf('\n', sp);
        if (anotherSp == -1) {
            return -1;
        }

        int spc = 0;
        for (int i = anotherSp + 1, len = string.length(); i < len; i++) {
            if (string.charAt(i) != ' ') {
                break;
            }
            spc++;
        }

        String spcString = "\n" + StringUtil.repeat(" ", spc);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("<spc>" + spc + "</spc>");
        }

        for (; ; ) {
            int lsp = string.indexOf('\n', anotherSp);
            if (lsp == -1) {
                break;
            }

            int lep = string.indexOf('\n', lsp + 1);
            if (lep == -1) {
                lep = ep;
            }

            String s = string.substring(lsp, lep);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("<s>" + s + "</s>");
            }

            if (s.indexOf(spcString) == -1) {
                break;
            }

            anotherSp = lep;
        }

        return anotherSp;
    }

    /**
     * Returns the cost while a query execution.
     *
     * @param raw A raw text with a query execution cost informations.
     * @return PlanCost object
     */
    private PlanCost parseCost(String raw) {
        // the style before CUBRID 9.0 : fixed 0(0.0/0.0) var 281(16.7/264.0) card 6677
        PlanCost planCost = null;
        String pattenString =
                "fixed[ ]+([0-9]+)\\(([0-9\\.]+)/([0-9\\.]+)\\)[ ]+var[ ]+([0-9]+)\\(([0-9\\.]+)/([0-9\\.]+)\\)[ ]+card[ ]+([0-9]+)";
        Matcher matcher = Pattern.compile(pattenString).matcher(raw.trim());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("<groupCount>" + matcher.groupCount() + "</groupCount>");
            LOGGER.debug("<parseCost-str>" + raw + "</parseCost-str>");
        }

        if (matcher.matches() && matcher.groupCount() == 7) {
            planCost = new PlanCost();
            planCost.setFixedTotal(StringUtil.intValue(matcher.group(1)));
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(
                        "<parseCost-match-group1>"
                                + matcher.group(1)
                                + "</parseCost-match-group1>");
            }
            planCost.setFixedCpu(StringUtil.floatValue(matcher.group(2)));
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(
                        "<parseCost-match-group2>"
                                + matcher.group(2)
                                + "</parseCost-match-group2>");
            }
            planCost.setFixedDisk(StringUtil.floatValue(matcher.group(3)));
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(
                        "<parseCost-match-group3>"
                                + matcher.group(3)
                                + "</parseCost-match-group3>");
            }
            planCost.setVarTotal(StringUtil.intValue(matcher.group(4)));
            planCost.setVarCpu(StringUtil.floatValue(matcher.group(5)));
            planCost.setVarDisk(StringUtil.floatValue(matcher.group(6)));
            planCost.setCard(StringUtil.intValue(matcher.group(7)));
            planCost.setTotal((int) (planCost.getFixedTotal() + planCost.getVarTotal()));

            return planCost;
        }

        // the style after CUBRID 9.0 : 230 card 6677
        pattenString = "([0-9]+)[ ]+card[ ]+([0-9]+)";
        matcher = Pattern.compile(pattenString).matcher(raw.trim());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("<groupCount>" + matcher.groupCount() + "</groupCount>");
            LOGGER.debug("<parseCost-str>" + raw + "</parseCost-str>");
        }

        if (matcher.matches() && matcher.groupCount() == 2) {
            planCost = new PlanCost();
            planCost.setCard(StringUtil.intValue(matcher.group(2)));
            planCost.setTotal(StringUtil.intValue(matcher.group(1)));
        }

        return planCost;
    }

    /**
     * split with a partition info and a table name
     *
     * <p>index == 0 - table name
     *
     * <p>index > 0 - partition name
     *
     * @param raw the raw string
     * @return the partitioned table array
     */
    private String[] splitPartitionedTable(String raw) {

        if (raw == null || raw.length() <= 2) {
            return null;
        }

        int sp = raw.indexOf("(");
        if (sp == -1) {
            return null;
        }

        int ep = raw.indexOf(")");
        if (ep == -1) {
            return null;
        }

        String newRaw = raw.substring(sp + 1, ep);

        String[] arr = newRaw.split(",");
        if (arr == null || arr.length == 0) {
            return null;
        }

        String[] res = new String[arr.length];
        for (int i = 0, len = arr.length; i < len; i++) {
            res[i] = arr[i].trim();
        }

        return res;
    }

    /**
     * Parse classes
     *
     * @param raw the raw string
     * @return the PlanTable object
     */
    private PlanTable parseClass(String raw) {

        // C nation C(215/6)

        int sp = raw.indexOf(' ');
        if (sp == -1) {
            return null;
        }

        String newRaw = raw.substring(sp + 1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("<raw>" + newRaw + "</raw>");
        }

        // for partitioned table : (game, game__p__medal1) as game(2833/196) (sargs 0)
        // eg. general table : athlete A(6677/264)
        sp = 0;
        boolean partitioned = false;
        if (newRaw.charAt(0) == '(') {
            sp = 1;
            partitioned = true;
        }

        int ep = newRaw.indexOf('(', sp);
        if (ep == -1) {
            return null;
        }

        String className = newRaw.substring(0, ep);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("<className>" + className + "</className>");
        }

        // for partitioned table
        String[] partitions = null;
        if (partitioned) {
            String[] tmpArr = splitPartitionedTable(className);
            if (tmpArr != null && tmpArr.length > 1) {
                className = tmpArr[0];
                partitions = new String[tmpArr.length - 1];
                for (int i = 1, len = tmpArr.length; i < len; i++) {
                    partitions[i - 1] = tmpArr[i].trim();
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("<partition>" + partitions[i - 1] + "</partition>");
                    }
                }
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("<className-new>" + className + "</className-new>");
            }
        }

        sp = ep;
        ep = newRaw.indexOf(')', sp);

        if (ep == -1) {
            return null;
        }
        newRaw = newRaw.substring(sp, ep + 1);

        String pattenString = "\\(([0-9]+)/([0-9]+)\\)";
        Matcher matcher = Pattern.compile(pattenString).matcher(newRaw);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("<PlanClass:matches>" + matcher.matches() + "</PlanClass:matches>");
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                    "<PlanClass:groupCount>" + matcher.groupCount() + "</PlanClass:groupCount>");
        }

        if (!matcher.matches() || matcher.groupCount() != 2) {
            return null;
        }

        if (className.indexOf(' ') != -1) {
            String[] splitClassName = className.split(" ");
            if (splitClassName != null && splitClassName.length == 2) {
                String className1 = StringUtil.trim(splitClassName[0]);
                String className2 = StringUtil.trim(splitClassName[1]);
                if (StringUtil.isEqual(className1, className2)) {
                    className = className1;
                }
            }
        }

        PlanTable planClass = new PlanTable();
        planClass.setName(className);
        planClass.setCard(StringUtil.intValue(matcher.group(1)));
        planClass.setPage(StringUtil.intValue(matcher.group(2)));
        planClass.setPartitions(partitions);

        return planClass;
    }

    /**
     * Parse the index
     *
     * @param indexRaw the raw index string
     * @return the PlanTerm Object
     */
    private PlanTerm parseIndex(String indexRaw) {
        if (StringUtil.isEmpty(indexRaw)) {
            return null;
        }

        String indexName = indexRaw.trim();
        String indexCond = indexName;

        int sp = indexRaw.indexOf(' ');
        if (sp != -1) {
            indexName = indexRaw.substring(0, sp++).trim();
            indexCond = indexRaw.substring(sp, indexRaw.length()).trim();
        }

        PlanTerm planIndex = parseTerm(indexCond);
        if (planIndex == null) {
            planIndex = new PlanTerm();
        }
        planIndex.setName(indexName);

        return planIndex;
    }

    /**
     * Parse term
     *
     * @param raw the raw string
     * @return the PlanTerm Object
     */
    private PlanTerm parseTerm(String raw) {
        // sargs: rownum range (min inf_lt 10) (sel 0.1) (rank 3) (instnum term) (not-join eligible)
        // (loc 0)
        // sargs: (rownum range (1 ge_le 100)) (sel 0.1) (rank 3) (instnum term) (not-join eligible)
        // (loc 0)
        // sargs: A.gender=B.s_name (sel 0.001) (join term) (mergeable) (inner-join) (loc 0)
        // sargs: y.j range (min inf_lt10) (sel 1) (rank 2) (sargterm) (not-join eligible) (loc 0)
        // sargs: x.i range ((select max(z.i) from z zwhere z.c=x.c) gt_inf max) (sel 0.1) (rank 10)
        // (sarg term) (not-join eligible) (loc 0)
        // sargs: x.vc range ('b' gt_inf max) (sel 0.1) (rank 2) (sarg term) (not-join eligible)
        // (loc 0)
        // edge:  A.gender=B.s_name (sel 0.001) (join term) (mergeable) (inner-join) (loc 0)
        // sargs: table(0) -> t t(5/1)

        PlanTerm term = new PlanTerm();
        term.setName("");

        Pattern patternTable = Pattern.compile("table\\([0-9]+\\) \\-\\> ");

        int sp = 0;
        String[] arr = raw.split(" AND ");
        for (int i = 0, len = arr.length; i < len; i++) {
            String eachTerm = arr[i].trim();
            String condition = "";
            String attribute = "";
            boolean passed = false;

            // (sel #.#)
            if (!passed) {
                sp = eachTerm.indexOf("(sel ");
                if (sp != -1) {
                    condition = eachTerm.substring(0, sp).trim();
                    attribute = eachTerm.substring(sp).trim();
                    passed = true;
                }
            }

            // sargs: table(0) -> t node[1]
            if (!passed) {
                Matcher m = patternTable.matcher(eachTerm);
                if (m.find()) {
                    condition = eachTerm;
                    passed = true;
                }
            }

            if (!passed) {
                attribute = eachTerm;
            }

            PlanTermItem item = new PlanTermItem();
            item.setCondition(condition);
            item.setAttribute(attribute);
            term.addTermItem(item);
        }

        return term;
    }
}
