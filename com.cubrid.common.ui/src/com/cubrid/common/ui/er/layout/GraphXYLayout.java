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

import com.cubrid.common.ui.er.part.SchemaDiagramPart;
import com.cubrid.common.ui.er.part.TablePart;
import java.util.Collection;
import java.util.List;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * Subclass of XYLayout which can use the child figures actual bounds as a constraint when doing
 * manual layout (XYLayout)
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-7-12 created by Yu Guojia
 */
public class GraphXYLayout extends FreeformLayout {
    private final SchemaDiagramPart diagram;

    public GraphXYLayout(SchemaDiagramPart diagram) {
        this.diagram = diagram;
    }

    @Override
    public void layout(IFigure container) {
        layoutPartitionNodes();
        super.layout(container);
        diagram.setTableModelBounds();
    }

    private void layoutPartitionNodes() {
        List<TablePart> tables = diagram.getNeedPartitionLayoutTables();
        if (tables.isEmpty()) {
            return;
        }
        Rectangle rec = diagram.getRectangle();
        layout(tables, 0, rec.y + rec.height);
        for (TablePart table : tables) { // set the bound constraints
            table.refreshVisuals();
        }
        List<TablePart> allTables = diagram.getAllTables();
        if (tables.size() == allTables.size()) { // all new tables, to focus to
            // (0,0) location
            diagram.moveTopLeftFocus();
        } else {
            diagram.moveLastTableLocationFocus();
        }
        diagram.clearNeedPartitionLayoutState();
    }

    private void layout(Collection<TablePart> tables, int startX, int startY) {
        new ERGraphLayoutVisitor().layout(tables, startX, startY);
    }

    @Override
    public Object getConstraint(IFigure child) {
        Object constraint = constraints.get(child);
        if (constraint != null || constraint instanceof Rectangle) {
            return constraint;
        } else {
            Rectangle currentBounds = child.getBounds();
            return new Rectangle(currentBounds.x, currentBounds.y, -1, -1);
        }
    }
}
