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

import com.cubrid.common.ui.spi.ResourceManager;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

public class GraphPlanTooltipFigure extends Figure {
    public static final int STYLE_SINGLE_LINE = 1;
    public static final int STYLE_MULTI_LINE = 2;

    private Figure keyValueFigure;
    private CompartmentFigure textFigure = new CompartmentFigure();
    private final Map<String, IFigure[]> keyValueFigures = new HashMap<String, IFigure[]>();
    private final Map<String, IFigure[]> textFigures = new HashMap<String, IFigure[]>();
    private static final int FONT_SIZE = 9;
    public static final Font bolderFont =
            ResourceManager.getFont(
                    Display.getCurrent().getSystemFont().toString(), FONT_SIZE, SWT.BOLD);
    public static final Font italicFont =
            ResourceManager.getFont(
                    Display.getCurrent().getSystemFont().toString(), FONT_SIZE, SWT.ITALIC);
    public static final Font normalFont =
            ResourceManager.getFont(
                    Display.getCurrent().getSystemFont().toString(), FONT_SIZE, SWT.NONE);
    private Label title;
    private FreeformLayout layout;

    public GraphPlanTooltipFigure() {
        ToolbarLayout layout = new ToolbarLayout();
        layout.setMinorAlignment(ToolbarLayout.ALIGN_TOPLEFT);
        layout.setStretchMinorAxis(false);
        layout.setSpacing(2);
        setLayoutManager(layout);
        setOpaque(true);
        initTitle();
        add(getKeyValueCompartment());
        add(textFigure);
    }

    private Figure getKeyValueCompartment() {
        if (keyValueFigure == null) {
            keyValueFigure = new Panel();
            keyValueFigure.setBorder(new CompartmentFigureBorder());
            GridLayout layout = new GridLayout(2, true);
            layout.verticalSpacing = 0;
            keyValueFigure.setLayoutManager(layout);
        }

        return keyValueFigure;
    }

    private CompartmentFigure getTextCompartment() {
        return textFigure;
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public class CompartmentFigure extends Figure {
        public CompartmentFigure() {
            ToolbarLayout layout = new ToolbarLayout();
            layout.setMinorAlignment(ToolbarLayout.ALIGN_TOPLEFT);
            layout.setStretchMinorAxis(false);
            layout.setSpacing(0);
            setLayoutManager(layout);
            setBorder(new CompartmentFigureBorder());
        }
    }

    public class CompartmentFigureBorder extends AbstractBorder {
        public Insets getInsets(IFigure figure) {
            return new Insets(1, 0, 0, 0);
        }

        public void paint(IFigure figure, Graphics graphics, Insets insets) {
            graphics.drawLine(
                    getPaintRectangle(figure, insets).getTopLeft(), tempRect.getTopRight());
        }
    }

    private void initTitle() {
        title = new Label();
        title.setLabelAlignment(PositionConstants.CENTER);
        title.setTextAlignment(PositionConstants.CENTER);
        title.setFont(bolderFont);

        add(title);
    }

    public LayoutManager getLayoutManager() {
        if (layout == null) {
            layout = new FreeformLayout();
        }

        return super.getLayoutManager();
    }

    public void addKeyValueItem(String key, String value) {
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.horizontalSpacing = 5;
        layout.verticalSpacing = 0;
        layout.makeColumnsEqualWidth = true;

        Label namelbl = new Label();
        namelbl.setFont(bolderFont);
        namelbl.setTextAlignment(PositionConstants.LEFT);
        namelbl.setText(key);

        GridData nameData = new GridData(SWT.NONE);
        nameData.grabExcessHorizontalSpace = true;
        nameData.horizontalSpan = 0;
        getKeyValueCompartment().add(namelbl, nameData);

        Label valueLbl = new Label();
        valueLbl.setFont(normalFont);
        valueLbl.setTextAlignment(PositionConstants.RIGHT);
        valueLbl.setText(value);

        GridData valueData = new GridData(SWT.NONE);
        valueData.grabExcessHorizontalSpace = true;
        valueData.horizontalSpan = 0;
        getKeyValueCompartment().add(valueLbl, valueData);

        updateMap(key + value, keyValueFigures, namelbl, valueLbl);
    }

    private void updateMap(String key, Map<String, IFigure[]> maps, IFigure... values) {
        maps.put(key, values);
    }

    public void addTextItem(String name, String value) {
        ToolbarLayout layout = new ToolbarLayout(false);
        Panel panel = new Panel();
        panel.setLayoutManager(layout);
        getTextCompartment().add(panel);

        Label namelbl = new Label();
        namelbl.setFont(bolderFont);
        namelbl.setTextAlignment(PositionConstants.LEFT);
        namelbl.setText(name);

        panel.add(namelbl);
        Label outputLbl = new Label();
        outputLbl.setText(value);
        outputLbl.setFont(normalFont);
        panel.add(outputLbl);

        updateMap(name, textFigures, namelbl, outputLbl);
    }
}
