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

import com.cubrid.common.ui.er.Messages;
import com.cubrid.common.ui.er.ValidationGraphicalViewer;
import com.cubrid.common.ui.er.ValidationMessageHandler;
import com.cubrid.common.ui.er.dialog.EditVirtualTableDialog;
import com.cubrid.common.ui.er.directedit.ERDirectEditManager;
import com.cubrid.common.ui.er.directedit.LabelCellEditorLocator;
import com.cubrid.common.ui.er.directedit.TableNameCellEditorValidator;
import com.cubrid.common.ui.er.editor.TableNameCellEditor;
import com.cubrid.common.ui.er.figures.ConnectionFigure;
import com.cubrid.common.ui.er.figures.EditableLabel;
import com.cubrid.common.ui.er.figures.TableFigure;
import com.cubrid.common.ui.er.model.ERTable;
import com.cubrid.common.ui.er.model.PropertyChangeProvider;
import com.cubrid.common.ui.er.policy.TableContainerEditPolicy;
import com.cubrid.common.ui.er.policy.TableDirectEditPolicy;
import com.cubrid.common.ui.er.policy.TableEditPolicy;
import com.cubrid.common.ui.er.policy.TableLayoutEditPolicy;
import com.cubrid.common.ui.er.policy.TableNodeEditPolicy;
import com.cubrid.common.ui.er.router.ERConnectionAnchor;
import java.beans.PropertyChangeEvent;
import java.util.List;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;

/**
 * Represents the editable and resizable table which can have columns added, removed, renamed etc.
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-7-10 created by Yu Guojia
 */
public class TablePart extends AbstractBasicPart implements NodeEditPart {
    protected DirectEditManager manager;
    protected MouseMotionListener hoverListener;

    public void activate() {
        super.activate();
        addHoverListener();
    }

    public void deactivate() {
        super.deactivate();
        setSelected(EditPart.SELECTED_NONE);
        removeHoverListener();
    }

    /** Add hovering listener for the figure */
    protected void addHoverListener() {
        final TablePart tablePart = this;
        final IFigure figure = this.getFigure();
        hoverListener =
                new MouseMotionListener() {
                    public void mouseEntered(MouseEvent me) {
                        if (hasChildPartSelected(tablePart) || tablePart.isDisableState()) {
                            return;
                        }
                        setHoverState(true);
                        setRelationLinesFocus(true);
                        setRelationLinesSyncColor(false);
                        figure.revalidate();
                    }

                    public void mouseExited(MouseEvent me) {
                        if (hasChildPartSelected(tablePart) || tablePart.isDisableState()) {
                            return;
                        }
                        setHoverState(false);
                        setRelationLinesFocus(false);
                        setRelationLinesSyncColor(true);
                        figure.revalidate();
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

    /** Returns the Table model object represented by this EditPart */
    public ERTable getTable() {
        return (ERTable) getModel();
    }

    @SuppressWarnings("rawtypes")
    protected List getModelChildren() {
        return getTable().getColumns();
    }

    @SuppressWarnings("rawtypes")
    protected List getModelSourceConnections() {
        return getTable().getForeignKeyRelationships();
    }

    @SuppressWarnings("rawtypes")
    protected List getModelTargetConnections() {
        return getTable().getTargetedRelationships();
    }

    /** Creates edit policies and associates these with roles */
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new TableNodeEditPolicy());
        installEditPolicy(EditPolicy.LAYOUT_ROLE, new TableLayoutEditPolicy());
        installEditPolicy(EditPolicy.CONTAINER_ROLE, new TableContainerEditPolicy());
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new TableEditPolicy());
        installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new TableDirectEditPolicy());
    }

    @Override
    public void showSourceFeedback(Request request) {
        super.showSourceFeedback(request);
    }

    @Override
    public void showTargetFeedback(Request request) {
        super.showTargetFeedback(request);
    }

    @Override
    public void setFocus(boolean hasFocus) {
        TableFigure tableFigure = (TableFigure) getFigure();
        LineBorder lineBorder = (LineBorder) tableFigure.getBorder();
        if (hasFocus) {
            lineBorder.setWidth(2);
        } else {
            lineBorder.setWidth(1);
        }
    }

    public void performRequest(Request request) {
        if (request.getType() == RequestConstants.REQ_DIRECT_EDIT) {
            if (request instanceof DirectEditRequest
                    && !directEditHitTest(((DirectEditRequest) request).getLocation().getCopy())) {
                return;
            }
            performDirectEdit();
        } else if ((request.getType() == RequestConstants.REQ_OPEN)) {
            ERTable table = getTable();
            EditVirtualTableDialog editDlg =
                    new EditVirtualTableDialog(
                            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                            table.getERSchema().getCubridDatabase(),
                            false,
                            table);

            int ret = editDlg.open();
            if (ret == IDialogConstants.OK_ID) {
                editDlg.postEdittedTable(table.getERSchema());
            }
        }
    }

    private boolean directEditHitTest(Point requestLoc) {
        TableFigure figure = (TableFigure) getFigure();
        EditableLabel nameLabel = figure.getNameLabel();
        nameLabel.translateToRelative(requestLoc);
        if (nameLabel.containsPoint(requestLoc)) {
            return true;
        }
        return false;
    }

    protected void performDirectEdit() {
        if (manager == null) {
            ValidationGraphicalViewer viewer = (ValidationGraphicalViewer) getViewer();
            ValidationMessageHandler handler = viewer.getValidationHandler();

            TableFigure figure = (TableFigure) getFigure();
            EditableLabel nameLabel = figure.getNameLabel();
            manager =
                    new ERDirectEditManager(
                            nameLabel,
                            new TableNameCellEditorValidator(handler),
                            this,
                            TableNameCellEditor.class,
                            new LabelCellEditorLocator(nameLabel));
        }
        manager.show();
    }

    public void handleNameChange(String value) {
        TableFigure tableFigure = (TableFigure) getFigure();
        EditableLabel label = tableFigure.getNameLabel();
        label.setVisible(false);
        refreshVisuals();
    }

    public void revertNameChange() {
        TableFigure tableFigure = (TableFigure) getFigure();
        EditableLabel label = tableFigure.getNameLabel();
        ERTable erTable = getTable();
        label.setText(erTable.getShownName());
        label.setVisible(true);
        refreshVisuals();
    }

    public String toString() {
        return getModel().toString();
    }

    private void setName(String name) {
        TableFigure tableFigure = (TableFigure) getFigure();
        EditableLabel label = tableFigure.getNameLabel();
        label.setText(name);
        label.setVisible(true);
    }

    protected void handleLabelChange(PropertyChangeEvent evt) {
        setName(getTable().getShownName());
        refreshVisuals();
    }

    protected void handleBoundsChange(PropertyChangeEvent evt) {
        TableFigure tableFigure = (TableFigure) getFigure();
        Rectangle constraint = (Rectangle) evt.getNewValue();
        SchemaDiagramPart parent = (SchemaDiagramPart) getParent();
        parent.setLayoutConstraint(this, tableFigure, constraint);
    }

    protected IFigure createFigure() {
        ERTable erTable = getTable();
        EditableLabel label = new EditableLabel(erTable.getShownName());
        TableFigure tableFigure = new TableFigure(label);
        Rectangle rec = erTable.getBounds();
        if (rec != null) {
            tableFigure.setBounds(rec);
        }
        return tableFigure;
    }

    public void refreshVisuals() {
        TableFigure tableFigure = (TableFigure) getFigure();
        Point location = tableFigure.getLocation();
        SchemaDiagramPart parent = (SchemaDiagramPart) getParent();
        Rectangle constraint = new Rectangle(location.x, location.y, -1, -1);
        parent.setLayoutConstraint(this, tableFigure, constraint);
        super.refreshVisuals();
    }

    public IFigure getContentPane() {
        TableFigure figure = (TableFigure) getFigure();
        return figure.getColumnsFigure();
    }

    public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
        ConnectionFigure conn = (ConnectionFigure) connection.getFigure();
        return conn.getTargetAnchor();
    }

    /** When creating a connection line with palette, return a down anchor. */
    public ConnectionAnchor getSourceConnectionAnchor(Request request) {
        return new ERConnectionAnchor(getFigure(), ERConnectionAnchor.DOWN);
    }

    public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
        ConnectionFigure conn = (ConnectionFigure) connection.getFigure();
        return conn.getSourceAnchor();
    }

    /** When creating a connection line with palette, return an up anchor. */
    public ConnectionAnchor getTargetConnectionAnchor(Request request) {
        return new ERConnectionAnchor(getFigure(), ERConnectionAnchor.UP);
    }

    /**
     * Is any child be selected.
     *
     * @param parent
     * @return
     */
    private boolean hasChildPartSelected(EditPart parent) {
        List<EditPart> children = parent.getChildren();
        for (EditPart child : children) {
            if (child.getSelected() != EditPart.SELECTED_NONE) {
                return true;
            }
            boolean selected = hasChildPartSelected(child);
            if (selected) {
                return true;
            }
        }
        return false;
    }

    private boolean isDisableState() {
        final IFigure figure = this.getFigure();
        if (figure instanceof TableFigure) {
            TableFigure tableFigure = (TableFigure) figure;
            return tableFigure.isDisabled();
        }
        return false;
    }

    /**
     * Set hover state to the figure
     *
     * @param isHovered
     */
    public void setHoverState(boolean isHovered) {
        final IFigure figure = this.getFigure();
        if (figure instanceof TableFigure) {
            TableFigure tableFigure = (TableFigure) figure;
            if (isHovered) {
                tableFigure.setHoverEnterState();
            } else {
                tableFigure.setHoverExistState();
            }
        }
    }

    /** Sets the width of the line when selected */
    public void setSelected(int value) {
        super.setSelected(value);
        TableFigure tableFigure = (TableFigure) getFigure();
        if (isSelected()) {
            tableFigure.setSelected(true);
            setRelationLinesFocus(true);
        } else {
            tableFigure.setSelected(false);
            setRelationLinesFocus(false);
        }
        boolean checkRelatedSelectedTable = !isSelected();
        setRelationLinesSyncColor(checkRelatedSelectedTable);
    }

    /**
     * Set all relation line same color by the table border color,if the table border color is not
     * its default.
     *
     * @param checkRelatedSelectedTable if it is true, the line color should be effected by related
     *     table border color.
     */
    public void setRelationLinesSyncColor(boolean checkRelatedSelectedTable) {
        List<EditPart> sourceConnPartList = (List<EditPart>) getSourceConnections();
        for (EditPart connPart : sourceConnPartList) {
            RelationshipPart linePart = (RelationshipPart) connPart;
            linePart.setSyncColorWithTable(this);
            if (checkRelatedSelectedTable) {
                linePart.setSelectedTableColor();
            }
        }
        List<EditPart> targetConnPartList = (List<EditPart>) getTargetConnections();
        for (EditPart connPart : targetConnPartList) {
            RelationshipPart linePart = (RelationshipPart) connPart;
            linePart.setSyncColorWithTable(this);
            if (checkRelatedSelectedTable) {
                linePart.setSelectedTableColor();
            }
        }
    }

    /**
     * Set all of lines relation with current table to be focused and relation columns to be
     * protruded.
     *
     * @param focus
     */
    @SuppressWarnings("unchecked")
    protected void setRelationLinesFocus(boolean focus) {
        List<EditPart> sourceConnPartList = (List<EditPart>) getSourceConnections();
        setRelationLinesFocus(sourceConnPartList, focus, true);
        List<EditPart> targetConnPartList = (List<EditPart>) getTargetConnections();
        setRelationLinesFocus(targetConnPartList, focus, true);
    }

    /**
     * If the checkRelatedTableState is true, then check other end table whether is selected, if
     * selected, the connection line should not be set "false" focus.
     *
     * @param connPartList
     * @param focus
     * @param checkRelatedSelectedTable
     */
    private void setRelationLinesFocus(
            List<EditPart> connPartList, boolean focus, boolean checkRelatedSelectedTable) {
        for (EditPart connPart : connPartList) {
            RelationshipPart linePart = (RelationshipPart) connPart;
            if (linePart.getSource() == null || linePart.getTarget() == null) {
                continue;
            }
            connPart.setFocus(focus);
            if (!focus && checkRelatedSelectedTable) {
                if (((TablePart) linePart.getSource()).isSelected()
                        || ((TablePart) linePart.getTarget()).isSelected()) {
                    continue;
                }
            }
            linePart.setRelationColumnProtrude(focus);
        }
    }

    /**
     * If "set" is true, set the table figure to be disable state
     *
     * @param set
     */
    public void setGrayBackground(boolean set) {
        if (set) {
            TableFigure tableFigure = (TableFigure) getFigure();
            tableFigure.setBackgroundColor(TableFigure.disableBackgroundColor);
        } else {
            TableFigure tableFigure = (TableFigure) getFigure();
            tableFigure.setBackgroundColor(TableFigure.defaultBackgroundColor);
        }
    }

    protected void handleLayoutChange(PropertyChangeEvent evt) {}

    public void removeEditPartListener(EditPartListener listener) {
        if (listener != null) {
            super.removeEditPartListener(listener);
        }
    }

    protected void handleTmpAutoLayout(PropertyChangeEvent evt) {}

    @Override
    protected void handleViewModelChange(PropertyChangeEvent evt) {
        Object newValue = evt.getNewValue();
        Object oldValue = evt.getOldValue();

        if (oldValue == null || newValue == null) {
            throw new IllegalStateException(Messages.errOldNewValueBothNull);
        }

        if (newValue.equals(oldValue)) {
            return;
        }

        ERTable table = getTable();
        if (newValue.equals(PropertyChangeProvider.LOGIC_MODEL)) {
            setName(table.getLogicName());
        } else if (newValue.equals(PropertyChangeProvider.PHYSICAL_MODEL)) {
            setName(table.getName());
        }

        List<EditPart> children = getChildren();
        for (EditPart part : children) {
            if (part instanceof ColumnPart) {
                ColumnPart columnPart = (ColumnPart) part;
                columnPart.handleViewModelChange(evt);
            }
        }
        refreshVisuals();
    }

    /* (non-Javadoc)
     * @see com.cubrid.common.ui.er.part.BasicPart#handleRelationMapChange(java.beans.PropertyChangeEvent)
     */
    @Override
    protected void handleRelationMapChange(PropertyChangeEvent evt) {
        List<EditPart> children = getChildren();
        for (EditPart part : children) {
            if (part instanceof ColumnPart) {
                ColumnPart columnPart = (ColumnPart) part;
                columnPart.handleRelationMapChange(evt);
            }
        }
        refreshVisuals();
    }
}
