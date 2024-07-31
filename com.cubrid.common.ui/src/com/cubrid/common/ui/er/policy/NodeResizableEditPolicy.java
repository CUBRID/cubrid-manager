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
package com.cubrid.common.ui.er.policy;

import com.cubrid.common.ui.er.model.PropertyChangeProvider;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;

/**
 * Table and column node resizable edit policy, override {@link ResizableEditPolicy}
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-10-29 created by Yu Guojia
 */
public class NodeResizableEditPolicy extends ResizableEditPolicy {

    /** @see org.eclipse.gef.editpolicies.SelectionHandlesEditPolicy#createSelectionHandles() */
    @Override
    protected List createSelectionHandles() {
        return new ArrayList();
    }

    /**
     * Returns true if this EditPolicy allows its EditPart to be dragged.
     *
     * @return true if the EditPart can be dragged.
     */
    @Override
    public boolean isDragAllowed() {
        EditPart part = this.getHost();
        Object obj = part.getModel();
        if (obj instanceof PropertyChangeProvider) {
            PropertyChangeProvider model = (PropertyChangeProvider) obj;
            return model.getERSchema().isLayoutManualDesired();
        }
        return super.isDragAllowed();
    }

    /**
     * Sets the dragability of the EditPolicy to the given value. If the value is false, the
     * EditPolicy should not allow its EditPart to be dragged.
     *
     * @param isDragAllowed whether or not the EditPolicy can be dragged.
     */
    public void setDragAllowed(boolean isDragAllowed) {
        super.setDragAllowed(isDragAllowed);
    }
}
