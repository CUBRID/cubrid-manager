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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.eclipse.draw2d.FreeformLayeredPane;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayeredPane;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.editparts.GridLayer;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;

/**
 * ERD scalable free form root editPart to create inner layers and print layer.
 *
 * @author Yu Guojia
 * @version 1.0 - 2014-1-13 created by Yu Guojia
 */
public class ERScalableFreeformRootEditPart extends ScalableFreeformRootEditPart {
    private LayeredPane innerLayers;

    public ERScalableFreeformRootEditPart() {
        super();
    }

    protected IFigure createFigure() {
        ERFreeformViewport viewport = new ERFreeformViewport();
        innerLayers = new FreeformLayeredPane();
        createLayers(innerLayers);
        viewport.setContents(innerLayers);
        return viewport;
    }

    public IFigure getLayer(Object key) {
        IFigure innerLayer = innerLayers == null ? null : innerLayers.getLayer(key);
        if (innerLayer != null) {
            return innerLayer;
        }

        LayeredPane scaledLayers = getScaledLayers();
        IFigure layer = scaledLayers == null ? null : scaledLayers.getLayer(key);
        if (layer != null) {
            return layer;
        }

        if (getPrintableLayers() == null) {
            return null;
        }
        return getPrintableLayers().getLayer(key);
    }

    protected PropertyChangeListener erGridListener =
            new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    String property = evt.getPropertyName();
                    Object value = evt.getNewValue();
                    GridLayer grid = (GridLayer) getLayer(GRID_LAYER);
                    if (property.equals(SnapToGrid.PROPERTY_GRID_ORIGIN)) {
                        grid.setOrigin((Point) value);
                    } else if (property.equals(SnapToGrid.PROPERTY_GRID_SPACING)) {
                        grid.setSpacing((Dimension) value);
                    } else if (property.equals(SnapToGrid.PROPERTY_GRID_VISIBLE)) {
                        grid.setVisible((Boolean) value);
                    }
                }
            };

    protected void register() {
        super.register();
        if (getLayer(GRID_LAYER) != null) {
            getViewer().addPropertyChangeListener(erGridListener);
        }
    }

    protected void unregister() {
        getViewer().removePropertyChangeListener(erGridListener);
        super.unregister();
    }
}
