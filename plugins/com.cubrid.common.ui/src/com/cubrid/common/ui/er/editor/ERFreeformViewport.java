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
package com.cubrid.common.ui.er.editor;

import com.cubrid.common.ui.er.ERDefaultRangeModel;
import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformFigure;
import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ViewportLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;

/**
 * ERFreeformViewport for ERD
 *
 * @author Yu Guojia
 * @version 1.0 - 2014-3-3 created by Yu Guojia
 */
public class ERFreeformViewport extends FreeformViewport {
    class ERFreeformViewportLayout extends ViewportLayout {
        protected Dimension calculatePreferredSize(IFigure parent, int wHint, int hHint) {
            getContents().validate();
            wHint = Math.max(0, wHint);
            hHint = Math.max(0, hHint);
            Dimension dim =
                    ((FreeformFigure) getContents())
                            .getFreeformExtent()
                            .getExpanded(getInsets())
                            .union(0, 0)
                            .union(wHint - 1, hHint - 1)
                            .getSize();
            return dim;
        }

        protected boolean isSensitiveHorizontally(IFigure parent) {
            return true;
        }

        protected boolean isSensitiveVertically(IFigure parent) {
            return true;
        }

        public void layout(IFigure figure) {}
    }

    public ERFreeformViewport() {
        super();
        setLayoutManager(new ERFreeformViewportLayout());
        setHorizontalRangeModel(new ERDefaultRangeModel());
        setVerticalRangeModel(new ERDefaultRangeModel());
    }

    public Border getBorder() {
        return super.getBorder();
    }

    class ERFreeformViewportBorder extends AbstractBorder {
        private final Insets insets = new Insets(35, 35, 300, 300);
        private Figure figure;

        protected ERFreeformViewportBorder(Figure figure) {
            this.figure = figure;
        }

        public Insets getInsets(IFigure figure) {
            return insets;
        }

        public void paint(IFigure figure, Graphics graphics, Insets insets) {}
    }
}
