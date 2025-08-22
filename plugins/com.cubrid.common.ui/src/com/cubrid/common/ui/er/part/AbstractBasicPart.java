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
import com.cubrid.common.ui.er.editor.ERSchemaEditor;
import com.cubrid.common.ui.er.model.PropertyChangeProvider;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.List;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * An abstract EditPart implementation which is property aware and responds to PropertyChangeEvents
 * fired from the model.
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-5-15 created by Yu Guojia
 */
public abstract class AbstractBasicPart extends AbstractGraphicalEditPart
        implements PropertyChangeListener {
    public String getName() {
        PropertyChangeProvider propertyChangeProvider = (PropertyChangeProvider) getModel();
        return propertyChangeProvider.getName();
    }

    public void activate() {
        super.activate();
        PropertyChangeProvider propertyChangeProvider = (PropertyChangeProvider) getModel();
        if (propertyChangeProvider != null) {
            propertyChangeProvider.addPropertyChangeListener(this);
        }
    }

    public void deactivate() {
        super.deactivate();
        PropertyChangeProvider propertyChangeProvider = (PropertyChangeProvider) getModel();
        propertyChangeProvider.removePropertyChangeListener(this);
    }

    public boolean isSelected() {
        if (this.getSelected() != EditPart.SELECTED_NONE) {
            return true;
        }
        return false;
    }

    protected abstract void handleTmpAutoLayout(PropertyChangeEvent evt);

    protected abstract void handleLayoutChange(PropertyChangeEvent evt);

    protected abstract void handleBoundsChange(PropertyChangeEvent evt);

    protected abstract void handleLabelChange(PropertyChangeEvent evt);

    protected abstract void handleViewModelChange(PropertyChangeEvent evt);

    protected abstract void handleRelationMapChange(PropertyChangeEvent evt);

    public void propertyChange(PropertyChangeEvent evt) {
        String property = evt.getPropertyName();

        if (PropertyChangeProvider.CHILD_CHANGE.equals(property)) {
            handleChildChange(evt);
        } else if (PropertyChangeProvider.REORDER_CHANGE.equals(property)) {
            handleReorderChange(evt);
        } else if (PropertyChangeProvider.OUTPUT_CHANGE.equals(property)) {
            handleOutputChange(evt);
        } else if (PropertyChangeProvider.INPUT_CHANGE.equals(property)) {
            handleInputChange(evt);
        } else if (PropertyChangeProvider.TEXT_CHANGE.equals(property)) {
            handleLabelChange(evt);
        } else if (PropertyChangeProvider.BOUNDS_CHANGE.equals(property)) {
            handleBoundsChange(evt);
        } else if (PropertyChangeProvider.LAYOUT_CHANGE.equals(property)) {
            handleLayoutChange(evt);
        } else if (PropertyChangeProvider.AUTO_LAYOUT_TEMP.equals(property)) {
            handleTmpAutoLayout(evt);
        } else if (PropertyChangeProvider.VIEW_MODEL_CHANGE.equals(property)) {
            handleViewModelChange(evt);
        } else if (PropertyChangeProvider.RELATION_MAP_CHANGE.equals(property)) {
            handleRelationMapChange(evt);
        }

        if (PropertyChangeProvider.TEXT_CHANGE.equals(property)) {
            GraphicalEditPart graphicalEditPart = (GraphicalEditPart) (getViewer().getContents());
            IFigure partFigure = graphicalEditPart.getFigure();
            partFigure.getUpdateManager().performUpdate();
        }

        postSchemaDataChanged(evt);
    }

    private void handleInputChange(PropertyChangeEvent evt) {
        Object newValue = evt.getNewValue();
        Object oldValue = evt.getOldValue();

        if (oldValue == null && newValue == null) {
            throw new IllegalStateException(Messages.errOldNewValueBothNull);
        }

        if (newValue != null) {
            // add new connection
            ConnectionEditPart connPart = createOrFindConnection(newValue);
            int modelIndex = getModelTargetConnections().indexOf(newValue);
            addTargetConnection(connPart, modelIndex < 0 ? 0 : modelIndex);

        } else {
            // remove connection
            List children = getTargetConnections();
            ConnectionEditPart partToRemove = null;
            for (Iterator iter = children.iterator(); iter.hasNext(); ) {
                ConnectionEditPart part = (ConnectionEditPart) iter.next();
                if (part.getModel() == oldValue) {
                    partToRemove = part;
                    break;
                }
            }
            if (partToRemove != null) {
                // the connection part should be removed both in source part and in target part
                removeTargetConnection(partToRemove);
                EditPart sourcePart = partToRemove.getSource();
                if (sourcePart instanceof AbstractBasicPart) {
                    ((AbstractBasicPart) sourcePart).removeSourceConnection(partToRemove);
                }
            }
        }
        getContentPane().revalidate();
    }

    private void handleOutputChange(PropertyChangeEvent evt) {
        Object newValue = evt.getNewValue();
        Object oldValue = evt.getOldValue();
        if ((oldValue == null) && (newValue == null)) {
            throw new IllegalStateException(Messages.errOldNewValueBothNull);
        }

        if (oldValue == null && newValue != null) {
            // add new connection
            ConnectionEditPart connPart = createOrFindConnection(newValue);
            int modelIndex = getModelSourceConnections().indexOf(newValue);
            addSourceConnection(connPart, modelIndex < 0 ? 0 : modelIndex);
        } else if (oldValue != null && newValue == null) {
            // remove connection
            List children = getSourceConnections();
            ConnectionEditPart partToRemove = null;
            for (Iterator iter = children.iterator(); iter.hasNext(); ) {
                ConnectionEditPart part = (ConnectionEditPart) iter.next();
                if (part.getModel() == oldValue) {
                    partToRemove = part;
                    break;
                }
            }
            if (partToRemove != null) {
                // the connection part should be removed both in source part and in target part
                removeSourceConnection(partToRemove);
                EditPart targetPart = partToRemove.getTarget();
                if (targetPart instanceof AbstractBasicPart) {
                    ((AbstractBasicPart) targetPart).removeTargetConnection(partToRemove);
                }
            }
            getViewer().getEditPartRegistry().remove(oldValue);
            /*
            ConnectionEditPart connPart = (ConnectionEditPart) getViewer().getEditPartRegistry().get(oldValue);
            IFigure figure = connPart.getFigure();
            if(figure instanceof ConnectionFigure){
            	ConnectionFigure connFigure = (ConnectionFigure)figure;
            	if(connFigure.getConnectionRouter() == ConnectionRouter.NULL){
            		connFigure.setConnectionRouter(new ERConnectionRouter());
            	}
            }
            */
        }
        getContentPane().revalidate();
    }

    protected void handleChildChange(PropertyChangeEvent evt) {
        Object newValue = evt.getNewValue();
        Object oldValue = evt.getOldValue();
        if ((oldValue == null) && (newValue == null)) {
            throw new IllegalStateException(Messages.errOldNewValueBothNull);
        }

        if (oldValue != null) {
            List children = getChildren();
            EditPart partToRemove = null;
            for (Iterator iter = children.iterator(); iter.hasNext(); ) {
                EditPart part = (EditPart) iter.next();
                if (part.getModel() instanceof PropertyChangeProvider
                        && oldValue instanceof PropertyChangeProvider) {
                    PropertyChangeProvider model = (PropertyChangeProvider) part.getModel();
                    PropertyChangeProvider old = (PropertyChangeProvider) oldValue;
                    PropertyChangeProvider newV = (PropertyChangeProvider) newValue;
                    if (newV != null && model.getName().equals(newV.getName())) {
                        return;
                    }
                    if (model.getName().equals(old.getName())) {
                        partToRemove = part;
                        break;
                    }
                } else if (part.getModel().equals(oldValue)) {
                    partToRemove = part;
                    break;
                }
            }
            if (partToRemove != null) {
                removeChild(partToRemove);
            }
        }
        if (newValue != null) {
            EditPart editPart = createChild(newValue);
            int modelIndex = getModelChildren().indexOf(newValue);
            addChild(editPart, modelIndex);
        }
    }

    protected void handleReorderChange(PropertyChangeEvent evt) {
        refreshChildren();
        refreshVisuals();
    }

    public IWorkbenchPage getActivePage() {
        IWorkbench workbench = PlatformUI.getWorkbench();
        if (workbench == null) {
            return null;
        }
        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        if (window == null) {
            return null;
        }
        return window.getActivePage();
    }

    public void postSchemaDataChanged(PropertyChangeEvent evt) {
        String property = evt.getPropertyName();
        boolean isDataChanged =
                PropertyChangeProvider.CHILD_CHANGE.equals(property)
                        | PropertyChangeProvider.OUTPUT_CHANGE.equals(property)
                        | PropertyChangeProvider.INPUT_CHANGE.equals(property)
                        | PropertyChangeProvider.TEXT_CHANGE.equals(property);
        if (!isDataChanged) {
            return;
        }

        IWorkbenchPage page = getActivePage();
        if (page == null) {
            return;
        }
        IEditorPart editor = page.getActiveEditor();
        if (editor instanceof ERSchemaEditor) {
            ERSchemaEditor erEditor = (ERSchemaEditor) editor;
            erEditor.setDirty(true);
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!super.equals(obj)) {
            return false;
        } else if (getClass() != obj.getClass()) {
            return false;
        }

        AbstractBasicPart other = (AbstractBasicPart) obj;
        return this.hashCode() == other.hashCode();
    }

    public int hashCode() {
        PropertyChangeProvider propertyChangeProvider = (PropertyChangeProvider) getModel();
        if (propertyChangeProvider == null) {
            return -1;
        }

        return propertyChangeProvider.hashCode();
    }
}
