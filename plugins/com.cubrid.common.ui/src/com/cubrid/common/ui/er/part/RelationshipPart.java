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

import com.cubrid.common.ui.er.figures.ConnectionFigure;
import com.cubrid.common.ui.er.figures.EditableLabel;
import com.cubrid.common.ui.er.figures.TableFigure;
import com.cubrid.common.ui.er.model.ERTableColumn;
import com.cubrid.common.ui.er.model.Relationship;
import com.cubrid.common.ui.er.policy.RelationshipEditPolicy;
import com.cubrid.common.ui.er.router.ERConnectionRouter;
import java.util.List;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.swt.graphics.Color;

/**
 * Represents the editable primary key/foreign key relationship
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-7-10 created by Yu Guojia
 */
public class RelationshipPart extends AbstractRelationshipPart {
    protected MouseMotionListener hoverListener;

    @Override
    public void activate() {
        super.activate();
        addHoverListener();
    }

    @Override
    public void deactivate() {
        super.deactivate();
        handleDeleteConnection();
        removeHoverListener();
    }

    /** Add hovering listener for the figure */
    protected void addHoverListener() {
        final IFigure figure = this.getFigure();
        hoverListener =
                new MouseMotionListener() {
                    public void mouseEntered(MouseEvent me) {
                        setHoverState(true);
                        setRelationLinesFocus(true);
                    }

                    public void mouseExited(MouseEvent me) {
                        setHoverState(false);
                        setRelationLinesFocus(false);
                        setSelectedTableColor();
                    }

                    public void mouseHover(MouseEvent me) {}

                    public void mouseDragged(MouseEvent me) {}

                    public void mouseMoved(MouseEvent me) {}
                };
        figure.addMouseMotionListener(hoverListener);
    }

    /** remove hovering listener for the figure */
    protected void removeHoverListener() {
        final IFigure figure = this.getFigure();
        figure.removeMouseMotionListener(hoverListener);
    }

    @Override
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new ConnectionEndpointEditPolicy());
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new RelationshipEditPolicy());
    }

    @Override
    protected IFigure createFigure() {
        TablePart sourceTablePart = (TablePart) this.getSource();
        TablePart targetTablePart = (TablePart) this.getTarget();
        IFigure source = sourceTablePart == null ? null : sourceTablePart.getFigure();
        IFigure target = targetTablePart == null ? null : targetTablePart.getFigure();
        ConnectionFigure conn = new ConnectionFigure(source, target);
        conn.setConnectionRouter(new ERConnectionRouter());
        conn.setTargetDecoration(new PolygonDecoration());
        return conn;
    }

    /**
     * Sets the <i>source</i> of this connection.
     *
     * @param source the source of this connection
     */
    public void setSource(EditPart source) {
        TablePart tablePart = (TablePart) source;
        ConnectionFigure connFigure = (ConnectionFigure) this.getFigure();
        IFigure figure = tablePart == null ? null : tablePart.getFigure();
        connFigure.setSourceFigure(figure);

        super.setSource(source);
    }

    /**
     * Sets the<i>target</i> of this connection.
     *
     * @param target the target of this connection
     */
    public void setTarget(EditPart target) {
        TablePart tablePart = (TablePart) target;
        ConnectionFigure connFigure = (ConnectionFigure) this.getFigure();
        IFigure figure = tablePart == null ? null : tablePart.getFigure();
        connFigure.setTargetFigure(figure);

        super.setTarget(target);
    }

    /**
     * Set hover state to the figure, change the figure color or border
     *
     * @param isHovered
     */
    public void setHoverState(boolean isHovered) {
        IFigure figure = this.getFigure();
        if (figure instanceof ConnectionFigure) {
            ConnectionFigure conFigure = (ConnectionFigure) figure;
            if (isHovered) {
                conFigure.setHoverEnter();
            } else {
                conFigure.setHoverExist();
            }
        }
    }

    /** Sets the width of the line when selected */
    public void setSelected(int value) {
        super.setSelected(value);
        ConnectionFigure line = (ConnectionFigure) this.getFigure();
        line.setSelected(isSelected());
        setRelationLinesFocus(isSelected());
    }

    public void handleDeleteConnection() {
        Relationship relations = (Relationship) this.getModel();
        TablePart sourceTablePart = (TablePart) this.getSource();
        TablePart targetTablePart = (TablePart) this.getTarget();

        // set source and target columns
        if (sourceTablePart != null) {
            List<EditPart> children = (List<EditPart>) sourceTablePart.getChildren();
            for (EditPart child : children) {
                if (!(child instanceof ColumnPart)) {
                    continue;
                }
                ColumnPart columnPart = (ColumnPart) child;
                ERTableColumn column = (ERTableColumn) columnPart.getModel();
                if (relations.getReferenceColumns().contains(column.getName())) {
                    EditableLabel columnLable = (EditableLabel) columnPart.getFigure();
                    columnLable.setFontProtrude(false);
                }
            }
        }

        // target
        if (targetTablePart != null) {
            List<EditPart> children = (List<EditPart>) targetTablePart.getChildren();
            for (EditPart child : children) {
                if (!(child instanceof ColumnPart)) {
                    continue;
                }
                ColumnPart columnPart = (ColumnPart) child;
                ERTableColumn column = (ERTableColumn) columnPart.getModel();
                if (relations.getReferencedPKs().contains(column.getName())) {
                    EditableLabel columnLable = (EditableLabel) columnPart.getFigure();
                    columnLable.setFontProtrude(false);
                }
            }
        }
    }

    /**
     * Set all of lines relation with current table to be focused and relation columns to be
     * protruded.
     *
     * @param focus
     */
    protected void setRelationLinesFocus(boolean focus) {
        setFocus(focus);
        setRelationColumnProtrude(focus);
    }

    /**
     * Set the connection color by the table border color,if the table border color.
     *
     * @param tablePart
     */
    public void setSyncColorWithTable(TablePart tablePart) {
        TableFigure tableFigure = (TableFigure) tablePart.getFigure();
        ConnectionFigure line = (ConnectionFigure) this.getFigure();
        if (!tableFigure.isNormalState()) {
            Color color = tableFigure.getBorderColor();
            this.getFigure().setForegroundColor(color);
        } else if (this.isSelected()) {
            line.setSelected(true);
        } else {
            line.setDefaultState();
        }
    }

    /**
     * If there is any table(relation with the line) that is selected state, then set the color with
     * the selected table border color
     */
    public void setSelectedTableColor() {
        if (getSource() == null || getTarget() == null) {
            return;
        }
        if (((TablePart) getSource()).isSelected()) {
            TablePart table = (TablePart) getSource();
            setSyncColorWithTable(table);
        } else if (((TablePart) getTarget()).isSelected()) {
            TablePart table = (TablePart) getTarget();
            setSyncColorWithTable(table);
        }
    }

    public void setDefaulTAppearance() {}

    /**
     * Set foreign columns relation with the line to be protruded.
     *
     * @param focus
     */
    @SuppressWarnings("unchecked")
    public void setRelationColumnProtrude(boolean focus) {
        Relationship relations = (Relationship) this.getModel();
        TablePart sourceTablePart = (TablePart) this.getSource();
        TablePart targetTablePart = (TablePart) this.getTarget();

        if (sourceTablePart == null || targetTablePart == null) {
            return;
        }
        // set source and target columns
        List<EditPart> children = (List<EditPart>) sourceTablePart.getChildren();
        for (EditPart child : children) {
            if (!(child instanceof ColumnPart)) {
                continue;
            }
            ColumnPart columnPart = (ColumnPart) child;
            ERTableColumn column = (ERTableColumn) columnPart.getModel();
            if (relations.getReferenceColumns().contains(column.getName())) {
                EditableLabel columnLable = (EditableLabel) columnPart.getFigure();
                if (this.isSelected()
                        || sourceTablePart.isSelected()
                        || targetTablePart.isSelected()) {
                    columnLable.setFontProtrude(true);
                } else {
                    columnLable.setFontProtrude(focus);
                }
            }
        }

        // target
        children = (List<EditPart>) targetTablePart.getChildren();
        for (EditPart child : children) {
            if (!(child instanceof ColumnPart)) {
                continue;
            }
            ColumnPart columnPart = (ColumnPart) child;
            ERTableColumn column = (ERTableColumn) columnPart.getModel();
            if (relations.getReferencedPKs().contains(column.getName())) {
                EditableLabel columnLable = (EditableLabel) columnPart.getFigure();
                if (this.isSelected()
                        || sourceTablePart.isSelected()
                        || targetTablePart.isSelected()) {
                    columnLable.setFontProtrude(true);
                } else {
                    columnLable.setFontProtrude(focus);
                }
            }
        }
    }

    @Override
    public void setFocus(boolean value) {
        if (value) {
            ((PolylineConnection) getFigure()).setLineWidth(2);
        } else {
            ((PolylineConnection) getFigure()).setLineWidth(1);
        }
    }
}
