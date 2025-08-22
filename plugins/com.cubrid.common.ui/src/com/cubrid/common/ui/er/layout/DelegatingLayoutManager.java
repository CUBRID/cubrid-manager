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

import com.cubrid.common.ui.er.model.ERSchema;
import com.cubrid.common.ui.er.part.SchemaDiagramPart;
import com.cubrid.common.ui.er.policy.SchemaXYLayoutPolicy;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;

/**
 * Used to delegate between the GraphyLayoutManager and the GraphXYLayout classes
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-7-10 created by Yu Guojia
 */
public class DelegatingLayoutManager implements LayoutManager {
    private final SchemaDiagramPart diagram;
    private LayoutManager activeLayoutManager;
    private final ERGraphLayoutManager erGraphLayoutManager;
    private final GraphXYLayout xyLayoutManager;

    public DelegatingLayoutManager(SchemaDiagramPart diagram) {
        this.diagram = diagram;
        this.erGraphLayoutManager = new ERGraphLayoutManager(diagram);
        this.xyLayoutManager = new GraphXYLayout(diagram);
        this.activeLayoutManager = this.erGraphLayoutManager;
    }

    public void layout(IFigure container) {
        ERSchema erSchema = diagram.getSchema();
        if (erSchema.isLayoutManualDesired()) {
            if (activeLayoutManager != xyLayoutManager) {
                if (erSchema.isLayoutManualAllowed()) {
                    setLayoutManager(container, xyLayoutManager);
                    activeLayoutManager.layout(container);
                } else {
                    if (diagram.setTableFigureBounds(true)) {
                        setLayoutManager(container, xyLayoutManager);
                        activeLayoutManager.layout(container);
                    } else {
                        activeLayoutManager.layout(container);
                        setLayoutManager(container, xyLayoutManager);
                    }
                }
            } else {
                setLayoutManager(container, xyLayoutManager);
                activeLayoutManager.layout(container);
            }
        } else {
            setLayoutManager(container, erGraphLayoutManager);
            activeLayoutManager.layout(container);
        }
    }

    public Object getConstraint(IFigure child) {
        return activeLayoutManager.getConstraint(child);
    }

    public Dimension getMinimumSize(IFigure container, int wHint, int hHint) {
        return activeLayoutManager.getMinimumSize(container, wHint, hHint);
    }

    public Dimension getPreferredSize(IFigure container, int wHint, int hHint) {
        return activeLayoutManager.getPreferredSize(container, wHint, hHint);
    }

    public void invalidate() {
        activeLayoutManager.invalidate();
    }

    public void remove(IFigure child) {
        activeLayoutManager.remove(child);
    }

    public void setConstraint(IFigure child, Object constraint) {
        activeLayoutManager.setConstraint(child, constraint);
    }

    public void setXYLayoutConstraint(IFigure child, Rectangle constraint) {
        xyLayoutManager.setConstraint(child, constraint);
    }

    private void setLayoutManager(IFigure container, LayoutManager layoutManager) {
        container.setLayoutManager(layoutManager);
        this.activeLayoutManager = layoutManager;
        if (layoutManager == xyLayoutManager) {
            diagram.installEditPolicy(EditPolicy.LAYOUT_ROLE, new SchemaXYLayoutPolicy());
        } else {
            diagram.installEditPolicy(EditPolicy.LAYOUT_ROLE, null);
        }
    }

    public LayoutManager getActiveLayoutManager() {
        return activeLayoutManager;
    }
}
