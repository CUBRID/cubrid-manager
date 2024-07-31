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

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.er.router.Ray;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.draw2d.graph.Subgraph;
import org.eclipse.gef.NodeEditPart;

/**
 * Extends the structure of {@link Node}, add and enhance its functions.
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-12-17 created by Yu Guojia
 */
public class ERTableNode extends Node {
    /** The nodes started node on a fk line */
    private List<ERTableNode> sourceNodes = new LinkedList<ERTableNode>();
    /** The nodes ended node on a fk line */
    private List<ERTableNode> targetNodes = new LinkedList<ERTableNode>();

    private List<Ray> occupiedDirections = new LinkedList<Ray>();
    private String name;
    private boolean isArranged = false;

    public ERTableNode() {
        super();
        this.x = 0;
        this.y = 0;
    }

    public ERTableNode(NodeEditPart tablePart, String name) {
        this(tablePart, null, name);
    }

    public ERTableNode(Subgraph parent, String name) {
        this(null, parent, name);
    }

    public ERTableNode(NodeEditPart tablePart, Subgraph parent, String name) {
        super(tablePart, parent);
        this.x = 0;
        this.y = 0;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Rectangle getRectangle() {
        Dimension d = getDefaultPreferredSize();
        return new Rectangle(this.x, this.y, d.width, d.height);
    }

    public void setStartPointByCenter(int centerX, int centerY) {
        Dimension d = getDefaultPreferredSize();
        this.x = centerX - d.width / 2;
        this.y = centerY - d.height / 2;
    }

    public void setStartPointByCenter(Point point) {
        setStartPointByCenter(point.x, point.y);
    }

    public void setStartX(int x) {
        this.x = x;
    }

    public void setStartY(int y) {
        this.y = y;
    }

    public void setCoordinateOffset(int xOffset, int yOffset) {
        setXoffset(xOffset);
        setYoffset(yOffset);
    }

    public void setXoffset(int offset) {
        this.x += offset;
    }

    public void setYoffset(int offset) {
        this.y += offset;
    }

    public void addOccupiedDirections(Ray ray) {
        occupiedDirections.add(ray);
    }

    public Ray getOneEmptyDirect() {
        List<Ray> occupiedDirections = getOccupiedDirections();
        if (occupiedDirections.size() == 0) {
            return Ray.UP;
        }
        if (occupiedDirections.size() == 1) {
            return occupiedDirections.get(0).getReverseRay();
        }
        if (occupiedDirections.size() == 2 || occupiedDirections.size() == 3) {
            List<Ray> unOccupiedDirections = getUnOccupiedDirections();
            return unOccupiedDirections.get(0);
        }

        return new Ray(2, 2);
    }

    public List<Ray> getUnOccupiedDirections() {
        List<Ray> results = new LinkedList<Ray>();
        if (!occupiedDirections.contains(Ray.UP)) {
            results.add(Ray.UP);
        }
        if (!occupiedDirections.contains(Ray.RIGHT)) {
            results.add(Ray.RIGHT);
        }
        if (!occupiedDirections.contains(Ray.DOWN)) {
            results.add(Ray.DOWN);
        }
        if (!occupiedDirections.contains(Ray.LEFT)) {
            results.add(Ray.LEFT);
        }
        return results;
    }

    public List<Ray> getUnOccupiedAdjacentRays(Ray basicRay) {
        Ray oppositeRay = basicRay.getReverseRay();
        List<Ray> result = getUnOccupiedDirections();
        result.remove(basicRay);
        result.remove(oppositeRay);
        return result;
    }

    public Dimension getDefaultPreferredSize() {
        NodeEditPart tablePart = (NodeEditPart) data;
        return tablePart.getFigure().getPreferredSize(400, 300);
    }

    public static ERTableNode getMaxSizeNode(Collection<ERTableNode> allNodes, boolean horizon) {
        int max = 0;
        ERTableNode result = null;
        for (ERTableNode node : allNodes) {
            Dimension d = node.getDefaultPreferredSize();
            if (horizon && d.width > max) {
                result = node;
                max = d.width;
            } else if (!horizon && d.height > max) {
                result = node;
                max = d.height;
            }
        }

        return result;
    }

    public boolean isDirectConnected(ERTableNode otherNode) {
        boolean conn = false;
        List<ERTableNode> relationNodes = getAllRelationNodes();
        if (relationNodes.contains(otherNode)) {
            conn = true;
        }

        return conn;
    }

    public List<Ray> getOccupiedDirections() {
        return occupiedDirections;
    }

    public void setOccupiedDirections(List<Ray> occupiedDirections) {
        this.occupiedDirections = occupiedDirections;
    }

    public int hashCode() {
        NodeEditPart tablePart = (NodeEditPart) data;
        return tablePart.getModel().hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (getClass() != obj.getClass()) {
            return false;
        }

        ERTableNode other = (ERTableNode) obj;
        if (!StringUtil.isEqual(getName(), other.getName())) {
            return false;
        } else if (sourceNodes == null && other.sourceNodes != null) {
            return false;
        } else if (sourceNodes != null && other.sourceNodes == null) {
            return false;
        } else if (sourceNodes.size() != other.sourceNodes.size()) {
            return false;
        }

        return this.hashCode() == other.hashCode();
    }

    public void addSourceNode(ERTableNode node) {
        sourceNodes.add(node);
    }

    public void addTargetNode(ERTableNode node) {
        targetNodes.add(node);
    }

    public List<ERTableNode> getSourceNodes() {
        return sourceNodes;
    }

    public List<ERTableNode> getTargetNodes() {
        return targetNodes;
    }

    public List<ERTableNode> getAllRelationNodes() {
        List<ERTableNode> all = new LinkedList<ERTableNode>(sourceNodes);
        all.addAll(targetNodes);
        return all;
    }

    public List<ERTableNode> getAllRelationNodesIn(List<ERTableNode> others) {
        List<ERTableNode> all = new LinkedList<ERTableNode>();
        if (others == null || others.isEmpty()) {
            return all;
        }
        for (ERTableNode node : sourceNodes) {
            if (others.contains(node)) {
                all.add(node);
            }
        }
        for (ERTableNode node : targetNodes) {
            if (others.contains(node)) {
                all.add(node);
            }
        }
        return all;
    }

    public List<ERTableNode> getAllNewRelationNodesList() {
        List<ERTableNode> all = new LinkedList<ERTableNode>();
        for (ERTableNode node : sourceNodes) {
            all.add(node);
        }
        for (ERTableNode node : targetNodes) {
            all.add(node);
        }
        return all;
    }

    public static void SortNodesByBigDegree(List<ERTableNode> allNodes) {
        Collections.sort(
                allNodes,
                new Comparator<ERTableNode>() {
                    public int compare(ERTableNode arg0, ERTableNode arg1) {
                        return arg0.getAllRelationNodesCount() - arg1.getAllRelationNodesCount();
                    }
                });
    }

    public List<ERTableNode> getAllRelationNodesExcept(ERTableNode node) {
        List<ERTableNode> all = new LinkedList<ERTableNode>();
        for (ERTableNode n : sourceNodes) {
            if (n.equals(node)) {
                continue;
            }
            all.add(n);
        }
        for (ERTableNode n : targetNodes) {
            if (n.equals(node)) {
                continue;
            }
            all.add(n);
        }
        return all;
    }

    public int getAllRelationNodesCount() {
        return sourceNodes.size() + targetNodes.size();
    }

    public int getSourceNodeCount() {
        return sourceNodes.size();
    }

    public int getTargetNodeCount() {
        return targetNodes.size();
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isArranged() {
        return isArranged;
    }

    public void setArranged(boolean isArranged) {
        this.isArranged = isArranged;
    }
}
