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
package com.cubrid.common.ui.er.figures;

import com.cubrid.common.ui.spi.ResourceManager;
import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

/**
 * Figure used to hold the column labels
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-7-4 created by Yu Guojia
 */
public class ColumnsFigure extends Figure {
    public static String COLUMN_FONT_KEY = "Meiryo UI";
    public static int DEFAULT_FONT_HEIGHT = 10;
    public static int HOVER_FONT_HEIGHT = 13;
    public static int DEFAULT_FONT_STYLE = SWT.BORDER;
    public static int PROTRUDE_FONT_STYLE = SWT.BOLD;
    public static Color COLUMN_FONT_COLOR = new Color(null, 107, 107, 107);

    public ColumnsFigure() {
        FlowLayout layout = new FlowLayout();
        layout.setMinorAlignment(FlowLayout.ALIGN_LEFTTOP);
        layout.setStretchMinorAxis(false);
        layout.setHorizontal(false);
        setLayoutManager(layout);
        setBorder(new ColumnFigureBorder(this));
        setFont(ResourceManager.getFont(COLUMN_FONT_KEY, DEFAULT_FONT_HEIGHT, DEFAULT_FONT_STYLE));
        setBackgroundColor(ColorConstants.white);
        setForegroundColor(COLUMN_FONT_COLOR);
        setOpaque(true);
    }

    /**
     * If isBold is true, set a bold style font to the column figure.
     *
     * @param isBold
     */
    public void setFontProtrude(boolean isBold) {
        if (isBold) {
            setFont(
                    ResourceManager.getFont(
                            COLUMN_FONT_KEY, DEFAULT_FONT_HEIGHT, PROTRUDE_FONT_STYLE));
        } else {
            setFont(
                    ResourceManager.getFont(
                            COLUMN_FONT_KEY, DEFAULT_FONT_HEIGHT, DEFAULT_FONT_STYLE));
        }
        this.revalidate();
    }

    @Override
    public Border getBorder() {
        return super.getBorder();
    }

    class ColumnFigureBorder extends AbstractBorder {
        private final Insets insets = new Insets(5, 5, 3, 30);
        private Figure figure;

        protected ColumnFigureBorder(Figure figure) {
            this.figure = figure;
        }

        public Insets getInsets(IFigure figure) {
            return insets;
        }

        public void paint(IFigure figure, Graphics graphics, Insets insets) {
            graphics.drawLine(
                    getPaintRectangle(figure, insets).getTopLeft(), tempRect.getTopRight());
        }
    }
}
