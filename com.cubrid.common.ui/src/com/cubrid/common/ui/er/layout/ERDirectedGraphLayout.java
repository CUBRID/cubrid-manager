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

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * ER Directed Graph Layout. Arrange all sub graphs
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-12-17 created by Yu Guojia
 */
public class ERDirectedGraphLayout {
    private ExDirectedGraph originalGraph;
    private List<ERMinJoinDirectedGraphLayout> joinDirectedGraphList =
            new LinkedList<ERMinJoinDirectedGraphLayout>();
    public static int DEFAULT_DISTANCE_SUBGRAPH = 100;
    public static int MAX_COUNT_ALONE_NODES_LINE = 5;
    public static int MAX_LENGTH_ALONE_NODES_LINE = 800;

    public ERDirectedGraphLayout(ExDirectedGraph g) {
        originalGraph = g;
        init();
    }

    @SuppressWarnings("unchecked")
    public void layout() {
        Collections.sort(joinDirectedGraphList);
        int offsetX = 0;
        int offsetY = 0;
        int aloneNodeCount = 0;
        Set<ERTableNode> upLineAloneNodes = new HashSet<ERTableNode>();
        for (ERMinJoinDirectedGraphLayout graph : joinDirectedGraphList) {
            graph.layout();
            graph.adjust(offsetX, offsetY);
            Rectangle fullRec = graph.getLayoutedRec();
            if (graph.getNodeCount() == 1) {
                aloneNodeCount++;
                upLineAloneNodes.add(graph.getNode(0));
                if (aloneNodeCount == MAX_COUNT_ALONE_NODES_LINE
                        || offsetX > MAX_LENGTH_ALONE_NODES_LINE) {
                    ERTableNode maxHNode = ERTableNode.getMaxSizeNode(upLineAloneNodes, false);
                    offsetY = maxHNode.y + maxHNode.height;

                    // clear for next line
                    aloneNodeCount = 0;
                    offsetX = 0;
                    upLineAloneNodes.clear();
                } else {
                    offsetX = fullRec.x + fullRec.width + DEFAULT_DISTANCE_SUBGRAPH;
                }
            } else {
                offsetY = fullRec.y + fullRec.height;
            }
        }
    }

    private void init() {
        Set<ERTableNode> visitedNodes = new HashSet<ERTableNode>(originalGraph.getNodeCount());

        for (int i = 0; i < originalGraph.getNodeCount(); i++) {
            ERTableNode node = (ERTableNode) originalGraph.getNode(i);
            if (visitedNodes.contains(node)) {
                continue;
            }
            ExDirectedGraph subGraph = new ExDirectedGraph();
            buildJoinGraph(originalGraph, visitedNodes, node, subGraph);
            joinDirectedGraphList.add(new ERMinJoinDirectedGraphLayout(subGraph));
        }
    }

    private void buildJoinGraph(
            final ExDirectedGraph fullGraph,
            Set<ERTableNode> visitedNodes,
            final ERTableNode startNode,
            ExDirectedGraph subJoinGraph) {
        subJoinGraph.addNode(startNode);
        subJoinGraph.addEdges(startNode.outgoing);
        visitedNodes.add(startNode);
        List<ERTableNode> relationedNodes = startNode.getAllRelationNodes();
        for (ERTableNode node : relationedNodes) {
            if (!visitedNodes.contains(node)) {
                buildJoinGraph(fullGraph, visitedNodes, node, subJoinGraph);
            }
        }
    }
}
