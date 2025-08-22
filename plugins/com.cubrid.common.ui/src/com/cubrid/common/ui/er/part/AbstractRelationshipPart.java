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

import com.cubrid.common.ui.er.model.PropertyChangeProvider;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;

/**
 * An ConnectionEditPart base class which is property aware, that is, can handle property change
 * notification events All our ConnectionEditPart are subclasses of this
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-7-10 created by Yu Guojia
 */
public abstract class AbstractRelationshipPart extends AbstractConnectionEditPart
        implements PropertyChangeListener {
    public void activate() {
        super.activate();
        PropertyChangeProvider propertyChangeProvider = (PropertyChangeProvider) getModel();
        propertyChangeProvider.addPropertyChangeListener(this);
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

    public void propertyChange(PropertyChangeEvent evt) {
        String property = evt.getPropertyName();
        if (PropertyChangeProvider.CHILD_CHANGE.equals(property)) {
            refreshChildren();
        } else if (PropertyChangeProvider.INPUT_CHANGE.equals(property)) {
            refreshTargetConnections();
        } else if (PropertyChangeProvider.OUTPUT_CHANGE.equals(property)) {
            refreshSourceConnections();
        }

        ((GraphicalEditPart) (getViewer().getContents())).getFigure().revalidate();
    }
}
