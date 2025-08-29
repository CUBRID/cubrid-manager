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
package com.cubrid.common.ui.er.part;

import com.cubrid.common.ui.er.GraphAnimation;
import com.cubrid.common.ui.er.ValidationGraphicalViewer;
import com.cubrid.common.ui.er.editor.ERSchemaEditDomain;
import com.cubrid.common.ui.er.editor.ERSchemaEditor;
import com.cubrid.common.ui.er.figures.SchemaFigure;
import com.cubrid.common.ui.er.figures.TableFigure;
import com.cubrid.common.ui.er.layout.DelegatingLayoutManager;
import com.cubrid.common.ui.er.layout.ERGraphLayoutManager;
import com.cubrid.common.ui.er.model.ERSchema;
import com.cubrid.common.ui.er.model.ERTable;
import com.cubrid.common.ui.er.policy.SchemaContainerEditPolicy;
import com.cubrid.common.ui.er.utils.LayoutUtil;
import java.beans.PropertyChangeEvent;
import java.util.EventObject;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;

/**
 * Edit part for ERSchema object, and uses a SchemaDiagram figure as the container for all graphical
 * objects
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-5-10 created by Yu Guojia
 */
@SuppressWarnings("rawtypes")
public class SchemaDiagramPart extends AbstractBasicPart {
    private DelegatingLayoutManager delegatingLayoutManager;

    CommandStackListener stackListener =
            new CommandStackListener() {
                public void commandStackChanged(EventObject event) {
                    if (delegatingLayoutManager.getActiveLayoutManager()
                            instanceof ERGraphLayoutManager) {
                        if (!GraphAnimation.captureLayout(getFigure())) {
                            return;
                        }
                        while (GraphAnimation.step()) {
                            getFigure().getUpdateManager().performUpdate();
                        }
                        GraphAnimation.end();
                    } else {
                        getFigure().getUpdateManager().performUpdate();
                    }
                }
            };

    public void activate() {
        super.activate();
        getViewer().getEditDomain().getCommandStack().addCommandStackListener(stackListener);
    }

    public void deactivate() {
        getViewer().getEditDomain().getCommandStack().removeCommandStackListener(stackListener);
        super.deactivate();
    }

    public void addChild(EditPart child, int index) {
        super.addChild(child, index);
    }

    public void removeChild(EditPart child) {
        super.removeChild(child);
    }

    protected IFigure createFigure() {
        Figure figure = new SchemaFigure();
        delegatingLayoutManager = new DelegatingLayoutManager(this);
        figure.setLayoutManager(delegatingLayoutManager);
        return figure;
    }

    public ERSchema getSchema() {
        return (ERSchema) getModel();
    }

    protected List getModelChildren() {
        return getSchema().getTables();
    }

    public boolean isSelectable() {
        return false;
    }

    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.CONTAINER_ROLE, new SchemaContainerEditPolicy());
        installEditPolicy(EditPolicy.LAYOUT_ROLE, null);
    }

    public List<TablePart> getAllTables() {
        List<TablePart> result = new LinkedList<TablePart>();
        List tableParts = getChildren();
        Iterator iter = tableParts.iterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (obj instanceof TablePart) {
                result.add((TablePart) obj);
            }
        }

        return result;
    }

    public List<TablePart> getNeedPartitionLayoutTables() {
        List<TablePart> result = new LinkedList<TablePart>();
        List tableParts = getChildren();
        for (Iterator iter = tableParts.iterator(); iter.hasNext(); ) {
            Object obj = iter.next();
            if (!(obj instanceof TablePart)) {
                continue;
            }
            TablePart tablePart = (TablePart) obj;
            ERTable erTable = tablePart.getTable();
            if (erTable.isNeedPartitionLayout()) {
                result.add(tablePart);
            }
        }

        return result;
    }

    public void clearNeedPartitionLayoutState() {
        List tableParts = getChildren();
        for (Iterator iter = tableParts.iterator(); iter.hasNext(); ) {
            TablePart tablePart = (TablePart) iter.next();
            ERTable erTable = tablePart.getTable();
            if (erTable.isNeedPartitionLayout()) {
                erTable.setNeedPartitionLayout(false);
            }
        }
    }

    public TablePart getBottomRightTable() {
        List tableParts = getChildren();

        TablePart result = null;
        int maxY = 0;
        int maxX = 0;

        for (Iterator iter = tableParts.iterator(); iter.hasNext(); ) {
            Object obj = iter.next();
            if (!(obj instanceof TablePart)) {
                continue;
            }

            TablePart tablePart = (TablePart) obj;
            IFigure figure = tablePart.getFigure();
            if (figure == null) {
                continue;
            }

            Point bottomRightPoint = figure.getBounds().getBottomRight();
            if (bottomRightPoint.y > maxY) {
                result = tablePart;
            } else if (bottomRightPoint.y == maxY && bottomRightPoint.x > maxX) {
                result = tablePart;
            }
        }

        return result;
    }

    public boolean moveTopLeftFocus() {
        ERSchemaEditor editor = getEditor();
        if (editor != null) {
            editor.setLocatePoint(0, 0);
            return true;
        }

        return false;
    }

    public boolean moveLastTableLocationFocus() {
        TablePart bottomRightT = getBottomRightTable();
        ERSchemaEditor editor = getEditor();
        if (bottomRightT != null && editor != null) {
            IFigure figure = bottomRightT.getFigure();
            Point point = figure.getBounds().getBottomRight();
            editor.setLocatePoint(point.x, point.y + 160);
            // 160 is the distance between ERD canvas top and the CM/CQB app top
            return true;
        }

        return false;
    }

    public ERSchemaEditor getEditor() {
        EditPart parentP = getParent();
        if (parentP instanceof ScalableFreeformRootEditPart) {
            ScalableFreeformRootEditPart parentEditor = (ScalableFreeformRootEditPart) parentP;
            EditPartViewer viewer = parentEditor.getViewer();
            if (viewer instanceof ValidationGraphicalViewer) {
                ValidationGraphicalViewer gViewer = (ValidationGraphicalViewer) viewer;
                EditDomain domain = gViewer.getEditDomain();
                if (domain instanceof ERSchemaEditDomain) {
                    ERSchemaEditDomain erEditorDomain = (ERSchemaEditDomain) domain;
                    return (ERSchemaEditor) erEditorDomain.getEditorPart();
                }
            }
        }

        return null;
    }

    public Rectangle getRectangle() {

        Rectangle rec = new Rectangle(0, 0, 0, 0);
        List tableParts = getChildren();
        for (Iterator iter = tableParts.iterator(); iter.hasNext(); ) {
            Object obj = iter.next();
            if (!(obj instanceof TablePart)) {
                continue;
            }
            TablePart tablePart = (TablePart) obj;
            TableFigure tableFigure = (TableFigure) tablePart.getFigure();
            if (tableFigure == null) {
                continue;
            }
            LayoutUtil.unionAndExpand(rec, tableFigure.getBounds());
        }

        return rec;
    }

    public boolean setTableModelBounds() {
        List tableParts = getChildren();

        for (Iterator iter = tableParts.iterator(); iter.hasNext(); ) {
            Object obj = iter.next();
            if (!(obj instanceof TablePart)) {
                continue;
            }
            TablePart tablePart = (TablePart) obj;
            TableFigure tableFigure = (TableFigure) tablePart.getFigure();
            if (tableFigure == null) {
                continue;
            }
            Rectangle bounds = tableFigure.getBounds().getCopy();
            ERTable erTable = tablePart.getTable();
            erTable.setBounds(bounds);
        }

        return true;
    }

    public boolean setTableFigureBounds(boolean updateConstraint) {
        List tableParts = getChildren();

        for (Iterator iter = tableParts.iterator(); iter.hasNext(); ) {
            TablePart tablePart = (TablePart) iter.next();
            ERTable erTable = tablePart.getTable();

            Rectangle bounds = erTable.getBounds();
            if (bounds == null) {
                return false;
            } else {
                TableFigure tableFigure = (TableFigure) tablePart.getFigure();
                if (tableFigure == null) {
                    return false;
                } else if (updateConstraint) {
                    delegatingLayoutManager.setXYLayoutConstraint(
                            tableFigure, new Rectangle(bounds.x, bounds.y, -1, -1));
                }
            }
        }
        return true;
    }

    public void setFocus(boolean hasFocus) {
        List<EditPart> children = (List<EditPart>) getChildren();
        for (EditPart child : children) {
            child.setFocus(hasFocus);
        }
    }

    protected void handleLayoutChange(PropertyChangeEvent evt) {
        getFigure().setLayoutManager(delegatingLayoutManager);
    }

    public void setLayoutConstraint(EditPart child, IFigure childFigure, Object constraint) {
        super.setLayoutConstraint(child, childFigure, constraint);
    }

    protected void handleChildChange(PropertyChangeEvent evt) {
        super.handleChildChange(evt);
    }

    protected void handleBoundsChange(PropertyChangeEvent evt) {}

    protected void handleLabelChange(PropertyChangeEvent evt) {}

    protected void handleTmpAutoLayout(PropertyChangeEvent evt) {
        Boolean layoutType = (Boolean) evt.getNewValue();
        boolean tmpAutoLayout = layoutType.booleanValue();
        if (tmpAutoLayout) {
            new ERGraphLayoutManager(this).tmpAutoLayout();
        }
    }

    @Override
    protected void handleViewModelChange(PropertyChangeEvent evt) {
        List<EditPart> children = (List<EditPart>) getChildren();
        for (EditPart part : children) {
            if (part instanceof TablePart) {
                TablePart tablePart = (TablePart) part;
                tablePart.handleViewModelChange(evt);
            }
        }
    }

    /* (non-Javadoc)
     * @see com.cubrid.common.ui.er.part.BasicPart#handleRelationMapChange(java.beans.PropertyChangeEvent)
     */
    @Override
    protected void handleRelationMapChange(PropertyChangeEvent evt) {
        List<EditPart> children = (List<EditPart>) getChildren();
        for (EditPart part : children) {
            if (part instanceof TablePart) {
                TablePart tablePart = (TablePart) part;
                tablePart.handleRelationMapChange(evt);
            }
        }
    }
}
