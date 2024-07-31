/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: -
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. - Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. - Neither the name of the <ORGANIZATION> nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.cubrid.cubridmanager.ui.mondashboard.editor.parts;

import com.cubrid.cubridmanager.core.common.model.PropertyChangeProvider;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

/**
 * Base class of edit parts used in ha monitor.
 *
 * @see java.beans.PropertyChangeListener
 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart
 * @author cyl
 * @version 1.0 - 2010-6-2 created by cyl
 */
public abstract class AbstractMonitorEditPart extends AbstractGraphicalEditPart
        implements PropertyChangeListener {

    /**
     * Method to be executed when model's property changed.
     *
     * @param evt PropertyChangeEvent
     */
    public abstract void propertyChange(PropertyChangeEvent evt);

    /** register self as a PropertyChangeListener to Model. */
    public void activate() {
        if (isActive()) {
            return;
        }
        super.activate();
        getPropertyChangeProviderModel().addPropertyChangeListener(this);
    }

    /**
     * get model as PropertyChangeProvider
     *
     * @return model cast to PropertyChangeProvider
     */
    protected PropertyChangeProvider getPropertyChangeProviderModel() {
        return (PropertyChangeProvider) getModel();
    }

    /** deregister self as a PropertyChangeListener from Model. */
    public void deactivate() {
        if (!isActive()) {
            return;
        }
        super.deactivate();
        getPropertyChangeProviderModel().removePropertyChangeListener(this);
    }
}
