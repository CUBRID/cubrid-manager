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
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

/**
 * Table name header figuer
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-9-12 created by Yu Guojia
 */
public class TableHeaderFiguer extends Label {
    public static String HEADER_FONT_KEY = "ERD_HEADER_FONT";
    public static int HEADER_FONT_HEIGHT = 18;
    public static int HEADER_FONT_STYLE = SWT.NORMAL;
    public static Color HEADER_FONT_COLOR = new Color(null, 0, 165, 81);

    public TableHeaderFiguer() {
        FlowLayout layout = new FlowLayout();
        layout.setMinorAlignment(FlowLayout.ALIGN_CENTER);
        layout.setStretchMinorAxis(false);
        layout.setHorizontal(false);
        setLayoutManager(layout);
        setBorder(new HeaderFigureBorder(this));
        setFont(ResourceManager.getFont(HEADER_FONT_KEY, HEADER_FONT_HEIGHT, HEADER_FONT_STYLE));
        setForegroundColor(ColorConstants.white);
        setOpaque(true);
    }

    class HeaderFigureBorder extends AbstractBorder {
        private Figure figure;

        protected HeaderFigureBorder(Figure figure) {
            this.figure = figure;
        }

        public Insets getInsets(IFigure figure) {
            return new Insets(5, 0, 5, 0);
        }

        public void paint(IFigure figure, Graphics graphics, Insets insets) {
            graphics.drawLine(
                    getPaintRectangle(figure, insets).getTopLeft(), tempRect.getTopRight());
        }
    }
}
