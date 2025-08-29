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
package com.cubrid.common.ui.er.layout;

import com.cubrid.common.ui.er.router.Ray;
import com.cubrid.common.ui.er.utils.LayoutUtil;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.graph.NodeList;

/**
 * Layout ERD tables for a minimum joined directed graph.
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-12-17 created by Yu Guojia
 */
@SuppressWarnings("rawtypes")
public class ERMinJoinDirectedGraphLayout implements Comparable {
    private ExDirectedGraph joinDirectedGraph;
    public static int DEFAULT_SPACE_UP = 30;
    public static int DEFAULT_SPACE_LEFT = 30;
    public static int MAX_SPACE_TWO_TABLES = 150;
    public static int MIN_SPACE_TWO_TABLES = 40;

    public ERMinJoinDirectedGraphLayout(ExDirectedGraph g) {
        joinDirectedGraph = g;
    }

    public void layout() {
        int count = joinDirectedGraph.getNodeCount();
        switch (count) {
            case 1:
                layout1();
                break;
            case 2:
                layout2(Ray.RIGHT);
                break;
            case 3:
                layout3();
                break;
            case 4:
                layout4();
                break;
            default:
                layoutMultiNodes();
        }

        adjust();
    }

    public int getNodeCount() {
        return joinDirectedGraph.getNodes().size();
    }

    public ERTableNode getNode(int i) {
        return (ERTableNode) joinDirectedGraph.getNodes().get(i);
    }

    public void initNodeVetexCoordinate(ERTableNode node) {
        node.x = 0;
        node.y = 0;
    }

    public void adjust(int offsetX, int offsetY) {
        if (offsetX == 0 && offsetY == 0) {
            return;
        }

        NodeList nodes = joinDirectedGraph.getNodes();
        Iterator it = nodes.iterator();
        it = nodes.iterator();
        while (it.hasNext()) {
            ERTableNode node = (ERTableNode) it.next();
            node.setXoffset(offsetX);
            node.setYoffset(offsetY);
        }
    }

    private void adjust() {
        Rectangle fullRec = getLayoutedRec();
        int adjustX = DEFAULT_SPACE_LEFT + Math.abs(fullRec.x);
        int adjustY = DEFAULT_SPACE_UP + Math.abs(fullRec.y);
        adjust(adjustX, adjustY);
    }

    public Rectangle getLayoutedRec() {
        NodeList nodes = joinDirectedGraph.getNodes();
        Rectangle fullRec = new Rectangle(0, 0, 0, 0);
        Iterator it = nodes.iterator();
        while (it.hasNext()) {
            ERTableNode node = (ERTableNode) it.next();
            LayoutUtil.unionAndExpand(fullRec, node.getRectangle());
        }
        return fullRec;
    }

    public List<ERTableNode> getNodesByDegree(int degree) {
        return getNodesByDegree(toERTableNode(joinDirectedGraph.getNodes()), degree);
    }

    public List<ERTableNode> getNodesByDegree(List<ERTableNode> allNodes, int degree) {
        List<ERTableNode> nodes = new LinkedList<ERTableNode>();
        for (int i = 0; i < allNodes.size(); i++) {
            ERTableNode node = allNodes.get(i);
            if (node.getAllRelationNodesCount() == degree) {
                nodes.add(node);
            }
        }
        return nodes;
    }

    private ERTableNode getHighestNode(List<ERTableNode> nodes) {
        ERTableNode result = null;
        int highest = 0;
        if (nodes.size() == 1) {
            return nodes.get(0);
        }

        for (ERTableNode node : nodes) {
            Dimension d = node.getDefaultPreferredSize();
            if (d.height > highest) {
                highest = d.height;
                result = node;
            }
        }
        return result;
    }

    private List<ERTableNode> getDirectedConnNodes(
            ERTableNode basicNode, List<ERTableNode> others) {
        List<ERTableNode> relationNodes = basicNode.getAllNewRelationNodesList();
        relationNodes.retainAll(others);
        return relationNodes;
    }

    private List<ERTableNode> getNewExceptNodeList(
            Collection<ERTableNode> allNodes, Collection<ERTableNode> exceptNodes) {
        List<ERTableNode> result = new LinkedList<ERTableNode>();
        for (ERTableNode node : allNodes) {
            if (!exceptNodes.contains(node)) {
                result.add(node);
            }
        }

        return result;
    }

    private ERTableNode getMaxDimensionNode(Collection<ERTableNode> allNodes, boolean horizon) {
        int max = -1;
        ERTableNode feedBackNode = null;
        for (ERTableNode node : allNodes) {
            Dimension d = node.getDefaultPreferredSize();
            if (horizon) {
                if (d.width > max) {
                    max = d.width;
                    feedBackNode = node;
                }
            } else {
                if (d.height > max) {
                    max = d.height;
                    feedBackNode = node;
                }
            }
        }

        return feedBackNode;
    }

    public int getMaxDegree(Collection<ERTableNode> nodes) {
        int max = 0;
        for (ERTableNode node : nodes) {
            if (node.getAllRelationNodesCount() > max) {
                max = node.getAllRelationNodesCount();
            }
        }
        return max;
    }

    private void layout1() {
        ERTableNode node = joinDirectedGraph.getNode(0);
        initNodeVetexCoordinate(node);
    }

    private void layout2(Ray ray) {
        ERTableNode node1 = joinDirectedGraph.getNode(0);
        ERTableNode node2 = joinDirectedGraph.getNode(1);
        Dimension dimension1 = node1.getDefaultPreferredSize();
        Dimension dimension2 = node2.getDefaultPreferredSize();
        if (dimension2.height > dimension1.height) {
            initNodeVetexCoordinate(node2);
            buildRelationNodeXY(node2, node1, ray);
        } else {
            initNodeVetexCoordinate(node1);
            buildRelationNodeXY(node1, node2, ray);
        }
    }

    private void buildRelationNodeXY(ERTableNode basicNode, ERTableNode otherNode, Ray ray) {
        Dimension offsetD = buildDefaultCenterOffset(basicNode, otherNode, ray);
        buildRelationNodeXY(basicNode, otherNode, ray, offsetD);
    }

    private void buildRelationNodeXY(
            ERTableNode basicNode, ERTableNode otherNode, Ray ray, Dimension offsetD) {
        int centerX = basicNode.getRectangle().getCenter().x + offsetD.width;
        int centerY = basicNode.getRectangle().getCenter().y + offsetD.height;

        // the start x/y is less half width than the center x/y
        otherNode.setStartPointByCenter(centerX, centerY);
        otherNode.setArranged(true);

        basicNode.addOccupiedDirections(ray);
        otherNode.addOccupiedDirections(ray.getReverseRay());
    }

    private Dimension buildDefaultCenterOffset(
            ERTableNode basicNode, ERTableNode otherNode, Ray ray) {
        Dimension basicD = basicNode.getDefaultPreferredSize();
        Dimension otherD = otherNode.getDefaultPreferredSize();

        Dimension offsetD =
                new Dimension(
                        (basicD.width + otherD.width) / 4, (basicD.height + otherD.height) / 4);

        // space offset
        offsetD.width = offsetD.width * ray.x;
        offsetD.height = offsetD.height * ray.y;

        // add the space of the two rectangle
        offsetD.width += (basicD.width / 2) * ray.x + (otherD.width / 2) * ray.x;
        offsetD.height += (basicD.height / 2) * ray.y + (otherD.height / 2) * ray.y;
        return offsetD;
    }

    public void BuildTriangleThirdNode(
            ERTableNode one, ERTableNode two, ERTableNode targetNode, Ray ray) {

        int distance = MIN_SPACE_TWO_TABLES;
        Dimension d1 = buildDefaultCenterOffset(one, targetNode, ray);
        Dimension d2 = buildDefaultCenterOffset(two, targetNode, ray);
        if (ray.equals(Ray.UP) || ray.equals(Ray.DOWN)) {
            distance = (d1.height + d2.height) / 2;
            Point center = getCenterPoint(one, two);
            center.y += distance;
            targetNode.setStartPointByCenter(center);
        } else if (ray.equals(Ray.LEFT) || ray.equals(Ray.RIGHT)) {
            distance = (d1.width + d2.width) / 2;
            Point center = getCenterPoint(one, two);
            center.x += distance;
            targetNode.setStartPointByCenter(center);
        }
    }

    public Point getCenterPoint(ERTableNode one, ERTableNode two) {
        Point p1 = one.getRectangle().getCenter();
        Point p2 = two.getRectangle().getCenter();

        return new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
    }

    public Dimension buildAverageOffset(ERTableNode basicNode, List<ERTableNode> allNode, Ray ray) {
        int count = allNode.size();
        Dimension sumD = new Dimension(0, 0);
        for (int i = 0; i < count; i++) {
            Dimension tmpD = buildDefaultCenterOffset(basicNode, allNode.get(i), ray);
            sumD.width += tmpD.width;
            sumD.height += tmpD.height;
        }
        sumD.width = sumD.width / count;
        sumD.height = sumD.height / count;

        return sumD;
    }

    public int buildLayoutSumLenth(List<ERTableNode> allNode, boolean horizon) {
        if (allNode == null || allNode.isEmpty()) {
            return 0;
        }

        int sum = 0;
        int count = allNode.size();
        ERTableNode firstNode = allNode.get(0);
        ERTableNode endNode = allNode.get(count - 1);

        if (horizon) {
            sum += firstNode.width / 2;
            sum += endNode.width / 2;
            for (int i = 0; i < count - 1; i++) {
                Dimension tmpD =
                        buildDefaultCenterOffset(allNode.get(i), allNode.get(i + 1), Ray.RIGHT);
                sum += Math.abs(tmpD.width);
            }
        } else {
            sum += firstNode.height / 2;
            sum += endNode.height / 2;
            for (int i = 0; i < count - 1; i++) {
                Dimension tmpD =
                        buildDefaultCenterOffset(allNode.get(i), allNode.get(i + 1), Ray.DOWN);
                sum += Math.abs(tmpD.height);
            }
        }
        return sum;
    }

    private void layout3() {
        ERTableNode node1 = joinDirectedGraph.getNode(0);
        ERTableNode node2 = joinDirectedGraph.getNode(1);
        ERTableNode node3 = joinDirectedGraph.getNode(2);
        if (node1.getAllRelationNodesCount() == 2 && node2.getAllRelationNodesCount() == 2) {
            layout2(Ray.RIGHT);
            buildRelationNodeXY(node2, node3, Ray.DOWN);
        } else {
            if (node1.getAllRelationNodesCount() == 2) {
                buildRelationNodeXY(node1, node2, Ray.LEFT);
                buildRelationNodeXY(node1, node3, Ray.RIGHT);
            } else if (node2.getAllRelationNodesCount() == 2) {
                buildRelationNodeXY(node2, node1, Ray.LEFT);
                buildRelationNodeXY(node2, node3, Ray.RIGHT);
            } else {
                buildRelationNodeXY(node3, node2, Ray.LEFT);
                buildRelationNodeXY(node3, node1, Ray.RIGHT);
            }
        }
    }

    private void layout4() {
        int maxDegree = getMaxDegree(toERTableNode(joinDirectedGraph.nodes));
        List<ERTableNode> maxDegreeNodes = getNodesByDegree(maxDegree);

        if (maxDegree == 2 && maxDegreeNodes.size() == 2) {
            buildRelationNodeXY(maxDegreeNodes.get(0), maxDegreeNodes.get(1), Ray.RIGHT);

            // build the right node
            List<ERTableNode> node =
                    maxDegreeNodes.get(1).getAllRelationNodesExcept(maxDegreeNodes.get(0));
            buildRelationNodeXY(maxDegreeNodes.get(1), node.get(0), Ray.RIGHT);

            // build the left node
            node = maxDegreeNodes.get(0).getAllRelationNodesExcept(maxDegreeNodes.get(1));
            buildRelationNodeXY(maxDegreeNodes.get(0), node.get(0), Ray.LEFT);
        } else if (maxDegree == 2 && maxDegreeNodes.size() == 4) {
            buildRelationNodeXY(maxDegreeNodes.get(0), maxDegreeNodes.get(1), Ray.RIGHT);
            boolean isConn = maxDegreeNodes.get(0).isDirectConnected(maxDegreeNodes.get(2));
            if (isConn) {
                buildRelationNodeXY(maxDegreeNodes.get(0), maxDegreeNodes.get(2), Ray.DOWN);
                buildRelationNodeXY(maxDegreeNodes.get(1), maxDegreeNodes.get(3), Ray.DOWN);
            } else {
                buildRelationNodeXY(maxDegreeNodes.get(0), maxDegreeNodes.get(3), Ray.DOWN);
                buildRelationNodeXY(maxDegreeNodes.get(1), maxDegreeNodes.get(2), Ray.DOWN);
            }
        } else if (maxDegree == 3 && maxDegreeNodes.size() >= 2) {
            // for triangle
            List<ERTableNode> exceptNodes = new LinkedList<ERTableNode>();
            exceptNodes.add(maxDegreeNodes.get(0));
            exceptNodes.add(maxDegreeNodes.get(1));
            List<ERTableNode> upDownNodes =
                    getNewExceptNodeList(toERTableNode(joinDirectedGraph.nodes), exceptNodes);

            ERTableNode maxWidthNode = ERTableNode.getMaxSizeNode(upDownNodes, true);
            Dimension offsetD1 =
                    buildDefaultCenterOffset(maxDegreeNodes.get(0), maxWidthNode, Ray.RIGHT);
            Dimension offsetD2 =
                    buildDefaultCenterOffset(maxWidthNode, maxDegreeNodes.get(1), Ray.RIGHT);
            Dimension offsetD =
                    new Dimension(
                            offsetD1.width + offsetD2.width, offsetD1.height + offsetD2.height);

            buildRelationNodeXY(maxDegreeNodes.get(0), maxDegreeNodes.get(1), Ray.RIGHT, offsetD);
            BuildTriangleThirdNode(
                    maxDegreeNodes.get(0), maxDegreeNodes.get(1), upDownNodes.get(0), Ray.UP);
            BuildTriangleThirdNode(
                    maxDegreeNodes.get(0), maxDegreeNodes.get(1), upDownNodes.get(1), Ray.DOWN);
        } else {
            layoutMultiNodes();
        }
    }

    public void layoutMultiNodes() {
        int maxDegree = getMaxDegree(toERTableNode(joinDirectedGraph.nodes));
        List<ERTableNode> maxDegreeNodes = getNodesByDegree(maxDegree);
        maxDegreeNodes.get(0).setArranged(true);
        layoutOneCenterGraph(maxDegreeNodes.get(0));
    }

    private void layoutOneCenterGraph(ERTableNode centerNode) {
        List<ERTableNode> relationNodes = centerNode.getAllRelationNodes();
        int maxDegree = getMaxDegree(relationNodes);
        List<ERTableNode> maxDegreeNodes = getNodesByDegree(relationNodes, maxDegree);

        if (relationNodes.size() < 5) {
            final Ray firstNodeRay = Ray.RIGHT;
            buildRelationNodeXY(centerNode, maxDegreeNodes.get(0), firstNodeRay);
            List<ERTableNode> relationNodes2MaxNode =
                    maxDegreeNodes.get(0).getAllRelationNodesIn(relationNodes);
            List<Ray> adjacentRays = centerNode.getUnOccupiedAdjacentRays(firstNodeRay);
            Set<ERTableNode> relationedNodes2MaxNode = new HashSet();
            int count = Math.min(relationNodes2MaxNode.size(), adjacentRays.size());

            for (int i = 0; i < count; i++) {
                buildRelationNodeXY(centerNode, relationNodes2MaxNode.get(i), adjacentRays.get(i));
                relationedNodes2MaxNode.add(relationNodes2MaxNode.get(i));
            }

            for (int i = 0; i < relationNodes.size(); i++) {
                if (!relationedNodes2MaxNode.contains(relationNodes.get(i))
                        && !relationNodes.get(i).isArranged()) {
                    buildRelationNodeXY(
                            centerNode, relationNodes.get(i), firstNodeRay.getReverseRay());
                    relationedNodes2MaxNode.add(relationNodes.get(i));
                    break;
                }
            }

            List<Ray> otherRays = centerNode.getUnOccupiedDirections();
            int rayCount = 0;
            for (int i = 0; i < relationNodes.size(); i++) {
                if (!relationNodes.get(i).isArranged()) {
                    buildRelationNodeXY(
                            centerNode, relationNodes.get(i), otherRays.get(rayCount++));
                }
            }
        } else {
            if (maxDegreeNodes.size() < 2) {
                List<ERTableNode> newRelationNodes =
                        getNewExceptNodeList(relationNodes, maxDegreeNodes);
                int secodeMaxDegree = getMaxDegree(newRelationNodes);
                maxDegreeNodes.add(getNodesByDegree(relationNodes, secodeMaxDegree).get(0));
            }

            int maxNodehorizonOffset = MIN_SPACE_TWO_TABLES;
            int maxNodeVerticalOffset = MIN_SPACE_TWO_TABLES;
            if (maxDegree > 1) {
                maxNodehorizonOffset += buildLayoutSumLenth(relationNodes, true) / 4;
                maxNodeVerticalOffset += buildLayoutSumLenth(relationNodes, false) / 4;
            }
            boolean MaxDegreeNodesConnected =
                    maxDegreeNodes.get(0).isDirectConnected(maxDegreeNodes.get(1));
            buildRelationNodeXY(
                    centerNode,
                    maxDegreeNodes.get(1),
                    Ray.RIGHT,
                    new Dimension(maxNodehorizonOffset, 0));
            if (MaxDegreeNodesConnected) {
                buildRelationNodeXY(
                        centerNode,
                        maxDegreeNodes.get(0),
                        Ray.DOWN,
                        new Dimension(0, maxNodeVerticalOffset));
            } else {
                buildRelationNodeXY(
                        centerNode,
                        maxDegreeNodes.get(0),
                        Ray.LEFT,
                        new Dimension(-1 * maxNodehorizonOffset, 0));
            }

            Set<ERTableNode> builtNodes = new HashSet<ERTableNode>();
            builtNodes.add(maxDegreeNodes.get(0));
            builtNodes.add(maxDegreeNodes.get(1));
            List<ERTableNode> newRelationNodes = getNewExceptNodeList(relationNodes, builtNodes);
            ERTableNode maxHightNode = getMaxDimensionNode(newRelationNodes, false);

            List upLineNodes = new LinkedList();
            List leftOrDownLineNodes = new LinkedList();
            newRelationNodes.remove(maxHightNode);
            LayoutUtil.splitSimpleTwoHalf(newRelationNodes, upLineNodes, leftOrDownLineNodes);

            int avgUpOffset = Math.abs(buildAverageOffset(centerNode, upLineNodes, Ray.UP).height);
            int avgLeftOrDownOffset = 0;
            if (MaxDegreeNodesConnected) {
                avgLeftOrDownOffset =
                        Math.abs(
                                buildAverageOffset(centerNode, leftOrDownLineNodes, Ray.RIGHT)
                                        .width);
            } else {
                avgLeftOrDownOffset =
                        Math.abs(
                                buildAverageOffset(centerNode, leftOrDownLineNodes, Ray.DOWN)
                                        .height);
            }

            int upnodeCenterX = centerNode.getRectangle().getCenter().x;
            int upnodeCenterY = centerNode.getRectangle().getCenter().y - avgUpOffset;
            maxHightNode.setStartPointByCenter(upnodeCenterX, upnodeCenterY);
            maxHightNode.setArranged(true);
            layoutNodesOnLine(upLineNodes, maxHightNode, true);
            if (MaxDegreeNodesConnected) {
                int leftnodeCenterX = centerNode.getRectangle().getCenter().x - avgLeftOrDownOffset;
                int leftnodeCenterY = centerNode.getRectangle().getCenter().y;
                if (leftOrDownLineNodes != null && leftOrDownLineNodes.size() > 0) {
                    ((ERTableNode) leftOrDownLineNodes.get(0))
                            .setStartPointByCenter(leftnodeCenterX, leftnodeCenterY);
                    leftOrDownLineNodes.remove(0);
                    layoutNodesOnLine(leftOrDownLineNodes, maxHightNode, false);
                }
            } else {
                int downnodeCenterX = centerNode.getRectangle().getCenter().x;
                int downnodeCenterY = centerNode.getRectangle().getCenter().y + avgLeftOrDownOffset;
                if (leftOrDownLineNodes != null && leftOrDownLineNodes.size() > 0) {
                    ((ERTableNode) leftOrDownLineNodes.get(0))
                            .setStartPointByCenter(downnodeCenterX, downnodeCenterY);
                    leftOrDownLineNodes.remove(0);
                    layoutNodesOnLine(leftOrDownLineNodes, maxHightNode, true);
                }
            }
        }

        for (ERTableNode node : relationNodes) {
            layoutRecursionDirectedSideLine(
                    node.getAllRelationNodes(), node, node.getOneEmptyDirect());
        }
    }

    private void layoutRecursionDirectedSideLine(
            List<ERTableNode> relationNodes, ERTableNode basicNode, Ray ray) {
        if (relationNodes == null || relationNodes.isEmpty()) {
            return;
        }
        List<ERTableNode> unVisitedNodes = getUnVisitedVodes(relationNodes);
        if (unVisitedNodes.isEmpty()) {
            return;
        }

        ERTableNode maxDimensionNode = getMaxDimensionNode(unVisitedNodes, ray.isHorizontal());
        buildRelationNodeXY(basicNode, maxDimensionNode, ray);
        unVisitedNodes.remove(maxDimensionNode);
        layoutNodesOnLine(unVisitedNodes, maxDimensionNode, !ray.isHorizontal());
        unVisitedNodes.add(maxDimensionNode);
        for (ERTableNode node : unVisitedNodes) {
            layoutRecursionDirectedSideLine(
                    node.getAllRelationNodes(), node, node.getOneEmptyDirect());
        }
    }

    private List<ERTableNode> getUnVisitedVodes(List<ERTableNode> relationNodes) {
        List<ERTableNode> result = new LinkedList<ERTableNode>();
        for (ERTableNode node : relationNodes) {
            if (!node.isArranged()) {
                result.add(node);
            }
        }
        return result;
    }

    private void layoutNodesOnLine(
            final List<ERTableNode> otherNodes, ERTableNode centerNode, boolean horizon) {

        if (otherNodes == null || otherNodes.size() == 0) {
            return;
        }

        int count = otherNodes.size();
        int halfCount = count / 2;
        if (count < 1) {
            return;
        }
        ERTableNode basicNode = centerNode;
        if (horizon) {
            for (int i = 0; i < halfCount; i++) {
                buildRelationNodeXY(basicNode, otherNodes.get(i), Ray.RIGHT);
                basicNode = otherNodes.get(i);
            }

            basicNode = centerNode;
            for (int i = halfCount; i < count; i++) {
                buildRelationNodeXY(basicNode, otherNodes.get(i), Ray.LEFT);
                basicNode = otherNodes.get(i);
            }
        } else {
            for (int i = 1; i < halfCount; i++) {
                buildRelationNodeXY(basicNode, otherNodes.get(i), Ray.DOWN);
                basicNode = otherNodes.get(i);
            }

            basicNode = centerNode;
            for (int i = halfCount; i < count; i++) {
                buildRelationNodeXY(basicNode, otherNodes.get(i), Ray.UP);
                basicNode = otherNodes.get(i);
            }
        }
    }

    public int compareTo(Object arg0) {
        ERMinJoinDirectedGraphLayout other = (ERMinJoinDirectedGraphLayout) arg0;

        int count1 = joinDirectedGraph.getNodeCount();
        int count2 = other.joinDirectedGraph.getNodeCount();
        if (count1 != count2) {
            return count2 - count1;
        }

        count1 = joinDirectedGraph.getEdgeCount();
        count2 = other.joinDirectedGraph.getEdgeCount();
        if (count1 != count2) {
            return count2 - count1;
        }

        String name1 = ((ERTableNode) joinDirectedGraph.getNode(0)).getName();
        String name2 = ((ERTableNode) other.joinDirectedGraph.getNode(0)).getName();
        return name1.compareTo(name2);
    }

    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public int hashCode() {
        return super.hashCode();
    }

    private List<ERTableNode> toERTableNode(NodeList nodeList) {
        List<ERTableNode> retList;
        retList =
                nodeList.stream()
                        .toList()
                        .stream()
                        .filter(e -> e instanceof ERTableNode)
                        .map(e -> (ERTableNode) e)
                        .toList();
        return retList;
    }
}
