/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.cubrid.table.dashboard.control;

import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SchemaInfo;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Attribute Table Viewer Content Provider
 *
 * @author robin 2009-6-4
 */
public class TableDashboardDetailContentProvider implements IStructuredContentProvider {
    boolean showClassAttribute = false;

    /**
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     * @param inputElement the input element
     * @return the array of elements to display in the viewer
     */
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof SchemaInfo) {
            SchemaInfo schema = (SchemaInfo) inputElement;
            List<DBAttribute> list = new ArrayList<DBAttribute>();
            if (showClassAttribute) {
                list.addAll(schema.getClassAttributes());
            }
            list.addAll(schema.getAttributes());

            return list.toArray();
        } else {
            return new Object[0];
        }
    }

    /** @see org.eclipse.jface.viewers.IContentProvider#dispose() */
    public void dispose() {
        // empty
    }

    /**
     * @see
     *     org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
     *     java.lang.Object, java.lang.Object)
     * @param viewer the viewer
     * @param oldInput the old input element, or <code>null</code> if the viewer did not previously
     *     have an input
     * @param newInput the new input element, or <code>null</code> if the viewer does not have an
     *     input
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

    public boolean isShowClassAttribute() {
        return showClassAttribute;
    }

    public void setShowClassAttribute(boolean showClassAttribute) {
        this.showClassAttribute = showClassAttribute;
    }
}
