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

import com.cubrid.common.core.queryplan.model.PlanCost;
import com.cubrid.common.core.queryplan.model.PlanNode;
import com.cubrid.common.core.util.StringUtil;
import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.TextUtilities;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;

public class GraphPlanStyleProvider extends GraphPlanLabelProviderAdapter {
    private final int MINUMS_WIDTH = 100;
    private final int MIN_CHAR_PER_LINE = 15;

    public void selfStyleConnection(Object element, GraphConnection connection) {
        connection.setConnectionStyle(ZestStyles.CONNECTIONS_SOLID); // CONNECTIONS_DIRECTED
    }

    public void selfStyleNode(Object element, GraphNode node) {
        if (element instanceof PlanNode) {
            PlanNode planNode = (PlanNode) element;
            CompartmentFigure figure = (CompartmentFigure) node.getNodeFigure();
            String title = planNode.getMethod();

            if (planNode.getTable() != null) {
                title += "(" + planNode.getTable().getName() + ")";
            }

            figure.setTitle(title);

            GraphPlanTooltipFigure tooltip = new GraphPlanTooltipFigure();
            figure.setToolTip(tooltip);
            Dimension dim = tooltip.getPreferredSize();

            tooltip.setTitle(planNode.getMethod());

            if (planNode.getDepth() == 0) {
                figure.setImage(GraphPlanImageSupport.getDefaultImage());
            } else {
                figure.setImage(GraphPlanImageSupport.getImage(planNode));
            }

            PlanCost cost = planNode.getCost();
            if (cost != null) {
                String costAndCardinality = "";
                costAndCardinality += "cost: " + cost.getTotal();
                tooltip.addKeyValueItem(
                        "cost", wrapText(dim.width, String.valueOf(cost.getTotal())));

                if (cost.getCard() > 0) {
                    costAndCardinality += ", card: " + cost.getCard();
                    tooltip.addKeyValueItem(
                            "cardinality", wrapText(dim.width, String.valueOf(cost.getCard())));
                }
                figure.setInfo(costAndCardinality);
            }

            if (planNode.getTable() != null && planNode.getTable().getName() != null) {
                tooltip.addKeyValueItem(
                        "table", wrapText(dim.width, planNode.getTable().getName()));
            }
            if (planNode.getEdge() != null && planNode.getEdge().getTermString() != null) {
                tooltip.addKeyValueItem(
                        "edge", wrapText(dim.width, planNode.getEdge().getTermString()));
            }
            if (planNode.getSargs() != null && planNode.getSargs().getTermString() != null) {
                tooltip.addKeyValueItem(
                        "sargs", wrapText(dim.width, planNode.getSargs().getTermString()));
            }
            if (planNode.getIndex() != null && planNode.getIndex().getTermString() != null) {
                tooltip.addKeyValueItem(
                        "index", wrapText(dim.width, planNode.getIndex().getTermString()));
            }
            if (planNode.getOrder() != null) {
                tooltip.addKeyValueItem("sort", wrapText(dim.width, planNode.getOrder()));
            }

            figure.show();
        }
    }

    /**
     * wrap the too long text
     *
     * @param width
     * @param str
     * @return
     */
    private String wrapText(int width, String str) {
        Dimension dim1 =
                TextUtilities.INSTANCE.getStringExtents(str, GraphPlanTooltipFigure.normalFont);
        // the width is big enough to display the String
        if (dim1.width < width) {
            return str;
        }

        // Calculate how many character to display per line
        int pixPerChar = dim1.width / str.length();
        int charPerLine =
                (MIN_CHAR_PER_LINE > width / pixPerChar) ? MIN_CHAR_PER_LINE : width / pixPerChar;
        StringBuilder sb = new StringBuilder();
        int startpos = 0;
        int endpos = charPerLine;
        while (startpos < str.length()) {
            if (endpos >= str.length()) {
                endpos = str.length();
            }
            sb.append(str.substring(startpos, endpos)).append("\n");
            startpos += charPerLine;
            endpos += charPerLine;
        }

        return sb.toString();
    }

    public IFigure getFigure(Object element) {
        return new CompartmentFigure();
    }

    class CompartmentFigure extends Figure {
        private Dimension DEFAULT = new Dimension(100, 65);
        private Label imgLbl = new Label();
        private Label infoLbl = new Label();
        private ToolbarLayout layout;
        private String title;
        private String info;

        public class CompartmentFigureBorder extends AbstractBorder {
            public Insets getInsets(IFigure figure) {
                return new Insets(0, 0, 0, 0);
            }

            public void paint(IFigure figure, Graphics graphics, Insets insets) {
                graphics.drawRectangle(getPaintRectangle(figure, insets));
            }
        }

        public String getInfo() {
            return info;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setImage(Image img) {
            this.imgLbl.setIcon(img);
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public CompartmentFigure() {
            layout = new ToolbarLayout(false);
            layout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
            layout.setStretchMinorAxis(false);
            layout.setSpacing(0);
            setLayoutManager(layout);

            imgLbl.setIconTextGap(0);
            imgLbl.setTextPlacement(PositionConstants.SOUTH);
            Font f = Display.getCurrent().getSystemFont();
            FontData[] datas = f.getFontData();
            for (FontData data : datas) {
                data.setHeight(1);
            }
            infoLbl.setTextAlignment(PositionConstants.CENTER);
            infoLbl.setVisible(true);
            setSize(DEFAULT);
        }

        public void show() {
            if (getTitle() != null) {
                imgLbl.setText(getTitle());
                Dimension dim =
                        TextUtilities.INSTANCE.getTextExtents(
                                title, Display.getCurrent().getSystemFont());
                if (dim.width > MINUMS_WIDTH) {
                    Dimension curdim = getSize();
                    curdim.width = dim.width;
                    setSize(curdim);
                }
                add(imgLbl);
            }

            if (StringUtil.isNotEmpty(getInfo())) {
                Dimension dim =
                        TextUtilities.INSTANCE.getTextExtents(
                                info, Display.getCurrent().getSystemFont());
                if (dim.width > MINUMS_WIDTH) {
                    Dimension curdim = getSize();
                    curdim.width = dim.width;
                    setSize(curdim);
                }
                infoLbl.setText(getInfo());
                add(infoLbl);
            }
        }
    }
}
