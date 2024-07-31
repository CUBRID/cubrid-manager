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

import com.cubrid.common.ui.er.figures.TableFigure;
import com.cubrid.common.ui.er.part.RelationshipPart;
import com.cubrid.common.ui.er.part.SchemaDiagramPart;
import com.cubrid.common.ui.er.part.TablePart;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.Node;

/**
 * A visitor to support populating nodes and edges location for arranging.
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-7-10 created by Yu Guojia
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ERGraphLayoutVisitor {
    private Map partNodesMap = new HashMap();
    private ExDirectedGraph erGraph = new ExDirectedGraph();

    public void layout(Collection<TablePart> tables, int startX, int startY) {
        if (tables == null || tables.isEmpty()) {
            return;
        }

        for (TablePart table : tables) {
            ERTableNode node = buildNode(table);
            erGraph.addNode(node);
            partNodesMap.put(table, node);
        }

        if (erGraph.getNodeCount() < 1) {
            return;
        }

        for (TablePart table : tables) {
            List conns = table.getSourceConnections();
            for (int i = 0; i < conns.size(); i++) {
                RelationshipPart relationshipPart =
                        (RelationshipPart) table.getSourceConnections().get(i);
                Edge edge = buildEdgeRelation(relationshipPart);
                erGraph.addEdge(edge);
                partNodesMap.put(relationshipPart, edge);
            }
        }

        ERDirectedGraphLayout erLayoutor = new ERDirectedGraphLayout(erGraph);
        erLayoutor.layout();
        for (TablePart table : tables) {
            setFigureBounds(table, startX, startY);
        }
    }

    public void layout(SchemaDiagramPart schemaDiagram) {
        initGraph(schemaDiagram);
        if (erGraph.getNodeCount() > 0) {
            for (int i = 0; i < schemaDiagram.getChildren().size(); i++) {
                TablePart tablePart = (TablePart) schemaDiagram.getChildren().get(i);
                List conns = tablePart.getSourceConnections();
                for (int j = 0; j < conns.size(); j++) {
                    RelationshipPart relationshipPart =
                            (RelationshipPart) tablePart.getSourceConnections().get(j);
                    Edge edge = buildEdgeRelation(relationshipPart);
                    erGraph.addEdge(edge);
                    partNodesMap.put(relationshipPart, edge);
                }
            }
            ERDirectedGraphLayout gLayout = new ERDirectedGraphLayout(erGraph);
            gLayout.layout();
            setFiguresBound(schemaDiagram);
        }
    }

    private void initGraph(SchemaDiagramPart schemaDiagram) {
        for (int i = 0; i < schemaDiagram.getChildren().size(); i++) {
            TablePart part = (TablePart) schemaDiagram.getChildren().get(i);
            ERTableNode node = buildNode(part);
            erGraph.addNode(node);
            partNodesMap.put(part, node);
        }
    }

    private ERTableNode buildNode(TablePart tablePart) {
        ERTableNode node = new ERTableNode(tablePart, tablePart.getName());
        if (tablePart.getFigure() != null) {
            node.setWidth(tablePart.getFigure().getPreferredSize().width);
            node.setHeight(tablePart.getFigure().getPreferredSize().height);
        }

        return node;
    }

    private Edge buildEdgeRelation(RelationshipPart relationshipPart) {
        ERTableNode sourceNode = (ERTableNode) partNodesMap.get(relationshipPart.getSource());
        ERTableNode targetNode = (ERTableNode) partNodesMap.get(relationshipPart.getTarget());
        if (sourceNode == null || targetNode == null) {
            return null;
        }
        sourceNode.addTargetNode(targetNode);
        targetNode.addSourceNode(sourceNode);

        Edge edge = new Edge(relationshipPart, sourceNode, targetNode);
        edge.weight = 2;
        return edge;
    }

    private void setFiguresBound(SchemaDiagramPart diagram) {
        for (int i = 0; i < diagram.getChildren().size(); i++) {
            TablePart tablePart = (TablePart) diagram.getChildren().get(i);
            setFigureBounds(tablePart, 0, 0);
        }
    }

    private void setFigureBounds(TablePart tablePart, int absStartX, int absStartY) {
        Node node = (Node) partNodesMap.get(tablePart);
        TableFigure tableFigure = (TableFigure) tablePart.getFigure();
        Rectangle bounds =
                new Rectangle(
                        absStartX + node.x,
                        absStartY + node.y,
                        tableFigure.getPreferredSize().width,
                        tableFigure.getPreferredSize().height);
        tableFigure.setBounds(bounds);
        for (int i = 0; i < tablePart.getSourceConnections().size(); i++) {
            RelationshipPart relationship =
                    (RelationshipPart) tablePart.getSourceConnections().get(i);
            setFigureConstraint(relationship);
        }
    }

    private void setFigureConstraint(RelationshipPart relationshipPart) {
        Edge edge = (Edge) partNodesMap.get(relationshipPart);
        if (edge == null) {
            return;
        }
        PolylineConnection conn = (PolylineConnection) relationshipPart.getConnectionFigure();
        conn.setRoutingConstraint(Collections.EMPTY_LIST);
    }
}
