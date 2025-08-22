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
package com.cubrid.cubridmanager.ui.host.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author fulei
 * @version 1.0 - 2013-3-4 created by fulei
 */
public class UnifyHostCubridConfTableContentProvider implements IStructuredContentProvider {
    /**
     * Returns the elements to display in the viewer when its input is set to the given element.
     *
     * @param inputElement the input element
     * @return the array of elements to display in the viewer
     */
    @SuppressWarnings("unchecked")
    public Object[] getElements(Object inputElement) {
        if (!(inputElement instanceof List)) {
            return new Object[] {};
        }
        List<Map<String, String>> newList = new ArrayList<Map<String, String>>();
        // do not display the uniform cubrid property
        for (Map<String, String> valueMap : (ArrayList<Map<String, String>>) inputElement) {
            newList.add(valueMap);
        }

        return newList.toArray();
    }

    /**
     * Notifies this content provider that the given viewer's input has been switched to a different
     * element.
     *
     * @param viewer the viewer
     * @param oldInput the old input element, or <code>null</code> if the viewer did not previously
     *     have an input
     * @param newInput the new input element, or <code>null</code> if the viewer does not have an
     *     input
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // empty
    }

    /** Disposes of this content provider. This is called by the viewer when it is disposed. */
    public void dispose() {
        // empty
    }
}
