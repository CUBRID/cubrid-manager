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
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * A customized Label based on the label used in the flow example. Primary selection is denoted by
 * highlight and focus rectangle. Normal selection is denoted by highlight only. Borrowed from the
 * Flow Editor example
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-7-10 created by Yu Guojia
 */
public class EditableLabel extends Label {
    private boolean isHeader = false;
    private boolean isPK = false;
    private boolean selected;
    public static int HEADER_LABEL_HEIGHT = 35;
    public static String HEADER_FONT_KEY = "ERD_TABLT_HEADER_FONT";

    private static Font protrudeFont =
            ResourceManager.getFont(
                    ColumnsFigure.COLUMN_FONT_KEY,
                    ColumnsFigure.DEFAULT_FONT_HEIGHT,
                    ColumnsFigure.PROTRUDE_FONT_STYLE);
    private static Font nomalFont =
            ResourceManager.getFont(
                    ColumnsFigure.COLUMN_FONT_KEY,
                    ColumnsFigure.DEFAULT_FONT_HEIGHT,
                    ColumnsFigure.DEFAULT_FONT_STYLE);

    public EditableLabel(String text) {
        super(text);
        this.setFont(nomalFont);
    }

    public EditableLabel(String text, Image image) {
        super(text, image);
    }

    private Rectangle getSelectionRectangle() {
        Rectangle bounds = getTextBounds().getCopy();
        bounds.expand(new Insets(2, 2, 0, 0));
        translateToParent(bounds);
        bounds.intersect(getBounds());
        return bounds;
    }

    /**
     * If isBold is true, set a bold style font to the column figure.
     *
     * @param isBold
     */
    public void setFontProtrude(boolean isBold) {
        if (isProtrude() ^ (!isBold)) {
            return;
        }
        if (isBold) {
            setFont(protrudeFont);
        } else {
            setFont(nomalFont);
        }
    }

    public boolean isProtrude() {
        return getFont().equals(protrudeFont);
    }

    /** sets the text of the label */
    @Override
    public void setText(String s) {
        super.setText(s);
    }

    /** sets the image of the label */
    @Override
    public void setIcon(Image image) {
        super.setIcon(image);
    }

    @Override
    protected void paintFigure(Graphics graphics) {
        if (selected) {
            graphics.pushState();
            graphics.setBackgroundColor(ColorConstants.menuBackgroundSelected);
            graphics.fillRectangle(getSelectionRectangle());
            graphics.popState();
            graphics.setForegroundColor(ColorConstants.white);
        }
        super.paintFigure(graphics);
    }

    class TableHeaderTextBorder extends AbstractBorder {
        private final Insets insets = new Insets(5, 0, 5, 0);
        private Figure figure;

        protected TableHeaderTextBorder(Figure figure) {
            this.figure = figure;
        }

        public Insets getInsets(IFigure figure) {
            return insets;
        }

        public void paint(IFigure figure, Graphics graphics, Insets insets) {}
    }

    public void setSelected(boolean b) {
        selected = b;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean isHeader) {
        this.isHeader = isHeader;
        this.setBorder(new TableHeaderTextBorder(this));
    }

    public boolean isPK() {
        return isPK;
    }

    public void setPK(boolean isPK) {
        this.isPK = isPK;
    }

    public boolean isSelected() {
        return selected;
    }
}
