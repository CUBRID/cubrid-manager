/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search Solution.
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

import com.cubrid.common.ui.spi.ResourceManager;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * The ERDThumbnailViewPart Description : ERDThumbnailViewPart Author : Kevin.Wang Create date :
 * 2014-3-3
 */
public class ERDThumbnailViewPart extends ViewPart {
    public static final String ID = "com.cubrid.common.ui.er.part.ERDThumbnailViewPart";
    private Composite container;
    private Canvas canvas;
    private LightweightSystem liSystem;
    private ScrollableThumbnail thumbnail;
    private ScalableFreeformRootEditPart rootEditPart;

    public void createPartControl(Composite parent) {
        container = new Composite(parent, SWT.BORDER);
        container.setLayout(new FillLayout());
    }

    /**
     * Redraw the thumbnail
     *
     * @param rootEditPart
     */
    public void redraw(ScalableFreeformRootEditPart rootEditPart) {
        if (rootEditPart == null) {
            if (canvas != null && !canvas.isDisposed()) {
                closeThumbnailWidget();
            }
        } else {
            if (rootEditPart != this.rootEditPart) {
                closeThumbnailWidget();
                drawThumbnail(rootEditPart);
            } else if (isClosedThumbnail()) {
                drawThumbnail(rootEditPart);
            }
        }
        this.rootEditPart = rootEditPart;
    }

    /**
     * Judge is closed the thumbnail
     *
     * @return
     */
    private boolean isClosedThumbnail() {
        if (canvas != null && !canvas.isDisposed()) {
            return false;
        }
        return true;
    }

    /** Close the thumbnail widget */
    private void closeThumbnailWidget() {
        if (isClosedThumbnail()) {
            return;
        }
        canvas.dispose();
        canvas = null;
        liSystem = null;
        thumbnail = null;

        container.layout();
        this.rootEditPart = null;
    }

    /**
     * Close the thumbnail widget
     *
     * @param rootEditPart
     */
    public void closeThumbnailWidget(ScalableFreeformRootEditPart rootEditPart) {
        if (rootEditPart != null && rootEditPart == this.rootEditPart) {
            closeThumbnailWidget();
        }
    }

    /**
     * Draw the thumbnail
     *
     * @param rootEditPart
     */
    private void drawThumbnail(ScalableFreeformRootEditPart rootEditPart) {
        canvas = new Canvas(container, SWT.None);
        canvas.setLayout(new FillLayout());
        canvas.setBackground(ResourceManager.getColor(SWT.COLOR_WHITE));
        liSystem = new LightweightSystem(canvas);

        thumbnail = new ScrollableThumbnail((Viewport) rootEditPart.getFigure());
        thumbnail.setViewport((Viewport) rootEditPart.getFigure());
        thumbnail.setSource(rootEditPart.getLayer(LayerConstants.SCALABLE_LAYERS));
        liSystem.setContents(thumbnail);

        container.layout();
    }

    public void setFocus() {}
}
