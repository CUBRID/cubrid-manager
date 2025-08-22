/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.cubridmanager.ui.replication.editor.figures;

import com.cubrid.common.ui.spi.ResourceManager;
import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.graphics.Image;

/**
 * This figure is a container figure,it can comprise the other figures
 *
 * @author pangqiren
 * @version 1.0 - 2009-8-26 created by pangqiren
 */
public class ContainerNodeFigure extends Figure {

    private final Label label = new Label();
    private final AttributesFigure attributesFigure = new AttributesFigure();

    public ContainerNodeFigure() {
        ToolbarLayout layout = new ToolbarLayout();
        layout.setVertical(true);
        layout.setStretchMinorAxis(true);
        setLayoutManager(layout);
        setBorder(new LineBorder());
        setBackgroundColor(ResourceManager.getColor(255, 255, 206));
        setOpaque(true);

        add(label);
        add(attributesFigure);
    }

    public AttributesFigure getAttributeFigure() {
        return attributesFigure;
    }

    /**
     * set the name
     *
     * @param name String
     */
    public void setName(String name) {
        this.label.setText(name);
    }

    /**
     * set the icon
     *
     * @param icon Image
     */
    public void setIcon(Image icon) {
        label.setIcon(icon);
    }

    /**
     * Attributes Figure
     *
     * @author panqiren
     * @version 1.0 - 2009-11-23 created by pangqiren
     */
    static class AttributesFigure extends Figure {
        public AttributesFigure() {
            XYLayout layout = new XYLayout();
            setLayoutManager(layout);
            setBorder(new AttributesFigureBorder());
            setOpaque(true);
        }
    }

    /**
     * Attributes Figure Border
     *
     * @author pangqiren
     * @version 1.0 - 2009-12-23 created by pangqiren
     */
    static class AttributesFigureBorder extends AbstractBorder {

        /**
         * @see org.eclipse.draw2d.Border#getInsets(org.eclipse.draw2d.IFigure)
         * @param figure IFigure
         * @return new Insets(1, 0, 0, 0)
         */
        public Insets getInsets(IFigure figure) {
            return new Insets(1, 0, 0, 0);
        }

        /**
         * @see org.eclipse.draw2d.Border#paint(org.eclipse.draw2d.IFigure,
         *     org.eclipse.draw2d.Graphics, org.eclipse.draw2d.geometry.Insets)
         * @param figure IFigure
         * @param graphics Graphics
         * @param insets Insets
         */
        public void paint(IFigure figure, Graphics graphics, Insets insets) {
            graphics.drawLine(
                    getPaintRectangle(figure, insets).getTopLeft(),
                    getPaintRectangle(figure, insets).getTopRight());
        }
    }
}
