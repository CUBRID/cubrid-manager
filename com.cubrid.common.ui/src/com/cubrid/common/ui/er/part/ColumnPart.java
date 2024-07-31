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

import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.er.ValidationGraphicalViewer;
import com.cubrid.common.ui.er.ValidationMessageHandler;
import com.cubrid.common.ui.er.directedit.ColumnNameTypeCellEditorValidator;
import com.cubrid.common.ui.er.directedit.ERDirectEditManager;
import com.cubrid.common.ui.er.directedit.LabelCellEditorLocator;
import com.cubrid.common.ui.er.figures.EditableLabel;
import com.cubrid.common.ui.er.model.ERTableColumn;
import com.cubrid.common.ui.er.policy.ColumnDirectEditPolicy;
import com.cubrid.common.ui.er.policy.ColumnEditPolicy;
import java.beans.PropertyChangeEvent;
import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.graphics.Image;

/**
 * Represents an editable Column object in the model
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-7-12 created by Yu Guojia
 */
public class ColumnPart extends AbstractBasicPart {
    protected ERDirectEditManager manager;
    private static Image pkImage =
            CommonUIPlugin.getImageDescriptor("icons/er/primary_key.png").createImage();
    private static Insets iconBorderInsets =
            new Insets(0, 2, 0, 0); // when has icon, 2 px space for icon to left
    private static Insets textBorderInsets =
            new Insets(0, 20, 0, 0); // when no icon, 20 px space for column text to left

    @Override
    protected IFigure createFigure() {
        ERTableColumn column = (ERTableColumn) getModel();
        String label = column.getLabelText();

        EditableLabel columnLabel = new EditableLabel(label);
        columnLabel.setPK(column.isPrimaryKey());

        if (column.isPrimaryKey()) {
            columnLabel.setIcon(pkImage);
            columnLabel.setBorder(new ColumnLabelBorder(InsetsType.ICON));
        } else {
            columnLabel.setIcon(null);
            columnLabel.setBorder(new ColumnLabelBorder(InsetsType.TEXT));
        }

        return columnLabel;
    }

    private class ColumnLabelBorder extends AbstractBorder {
        private InsetsType type;

        private ColumnLabelBorder(InsetsType type) {
            this.type = type;
        }

        private InsetsType getType() {
            return type;
        }

        public Insets getInsets(IFigure figure) {
            if (type == InsetsType.ICON) {
                return iconBorderInsets;
            } else if (type == InsetsType.TEXT) {
                return textBorderInsets;
            }
            return iconBorderInsets;
        }

        public void paint(IFigure figure, Graphics graphics, Insets insets) {}
    }

    private static enum InsetsType {
        ICON,
        TEXT
    }

    /** Creats EditPolicies for the column label */
    @Override
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new ColumnEditPolicy());
        installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new ColumnDirectEditPolicy());
        installEditPolicy(EditPolicy.LAYOUT_ROLE, null);
    }

    @Override
    public void performRequest(Request request) {
        if (request.getType() == RequestConstants.REQ_DIRECT_EDIT) {
            if (request instanceof DirectEditRequest
                    && !directEditHitTest(((DirectEditRequest) request).getLocation().getCopy())) {
                return;
            }
            performDirectEdit();
        } else if ((request.getType() == RequestConstants.REQ_OPEN)) {
            performDirectEdit();
        }
    }

    private boolean directEditHitTest(Point requestLoc) {
        IFigure figure = getFigure();
        figure.translateToRelative(requestLoc);
        if (figure.containsPoint(requestLoc)) {
            return true;
        }
        return false;
    }

    private void createDirectEditManager() {
        if (manager == null) {
            ValidationGraphicalViewer viewer = (ValidationGraphicalViewer) getViewer();
            ValidationMessageHandler handler = viewer.getValidationHandler();
            Label label = (Label) getFigure();
            ColumnNameTypeCellEditorValidator columnNameTypeCellEditorValidator =
                    new ColumnNameTypeCellEditorValidator(handler, (ERTableColumn) getModel());
            manager =
                    new ERDirectEditManager(
                            label,
                            columnNameTypeCellEditorValidator,
                            this,
                            TextCellEditor.class,
                            new LabelCellEditorLocator(label));
        }
    }

    protected void performDirectEdit() {
        createDirectEditManager();
        manager.show();
    }

    /** Sets the view of column label when it is selected */
    @Override
    public void setSelected(int value) {
        super.setSelected(value);
        EditableLabel columnLabel = (EditableLabel) getFigure();
        if (value != EditPart.SELECTED_NONE) {
            columnLabel.setSelected(true);
        } else {
            columnLabel.setSelected(false);
        }
        columnLabel.repaint();
        if (value == EditPart.SELECTED_PRIMARY && !columnLabel.isPK()) {}
    }

    /** @param Handles name change during direct edit */
    public void handleNameChange(String textValue) {
        EditableLabel label = (EditableLabel) getFigure();
        label.setVisible(false);
        setSelected(EditPart.SELECTED_NONE);
        label.revalidate();
    }

    /** Handles when successfully applying direct edit */
    @Override
    protected void handleLabelChange(PropertyChangeEvent evt) {
        ERTableColumn newColumn = getColumn();
        EditableLabel label = (EditableLabel) getFigure();
        label.setText(newColumn.getLabelText());
        label.setPK(newColumn.isPrimaryKey());
        freshIcon();
        freshBorder();
        label.revalidate();
    }

    private void freshIcon() {
        EditableLabel label = (EditableLabel) getFigure();
        if (label.isPK()) {
            if (label.getIcon() != pkImage) {
                label.setIcon(pkImage);
            }
        } else {
            if (label.getIcon() != null) {
                label.setIcon(null);
            }
        }
    }

    private void freshBorder() {
        EditableLabel label = (EditableLabel) getFigure();
        ColumnLabelBorder border = (ColumnLabelBorder) label.getBorder();
        if (label.isPK()) {
            if (border.getType() != InsetsType.ICON) {
                label.setBorder(new ColumnLabelBorder(InsetsType.ICON));
            }
        } else {
            if (border.getType() != InsetsType.TEXT) {
                label.setBorder(new ColumnLabelBorder(InsetsType.TEXT));
            }
        }
    }

    /** Reverts state back to prior edit state */
    public void revertNameChange(String oldValue) {
        EditableLabel label = (EditableLabel) getFigure();
        label.setVisible(true);
        setSelected(EditPart.SELECTED_PRIMARY);
        label.revalidate();
    }

    /**
     * We don't need to explicitly handle refresh visuals because the times when this needs to be
     * done it is handled by the table e.g. handleNameChange()
     */
    @Override
    protected void refreshVisuals() {
        ERTableColumn column = (ERTableColumn) getModel();
        EditableLabel columnLabel = (EditableLabel) getFigure();
        columnLabel.setText(column.getLabelText());
        columnLabel.setPK(column.isPrimaryKey());
        freshIcon();
        freshBorder();
    }

    public ERTableColumn getColumn() {
        ERTableColumn col = (ERTableColumn) getModel();
        ERTableColumn ref = col.getTable().getColumn(col.getName());
        if (col != ref) {
            setModel(ref);
        }
        return ref;
    }

    public void showTargetFeedback(Request request) {
        if (getParent() == null) {
            return;
        }
        getParent().showTargetFeedback(request);
    }

    @Override
    protected void handleLayoutChange(PropertyChangeEvent evt) {}

    @Override
    protected void handleBoundsChange(PropertyChangeEvent evt) {}

    protected void handleTmpAutoLayout(PropertyChangeEvent evt) {}

    @Override
    protected void handleViewModelChange(PropertyChangeEvent evt) {
        EditableLabel label = (EditableLabel) getFigure();
        label.setText(getColumn().getLabelText());
        label.revalidate();
    }

    /* (non-Javadoc)
     * @see com.cubrid.common.ui.er.part.BasicPart#handleRelationMapChange(java.beans.PropertyChangeEvent)
     */
    @Override
    protected void handleRelationMapChange(PropertyChangeEvent evt) {
        // do not need to refresh any data.
    }
}
