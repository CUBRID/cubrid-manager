/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.cubridmanager.ui.cubrid.database.control;

import java.util.Map;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * The VolumeInfoTableProvider class
 *
 * @author Kevin.Wang
 * @version 1.0 - Mar 26, 2012 created by Kevin.Wang
 */
public class VolumeInfoTableProvider implements ITableLabelProvider, IColorProvider {
    /**
     * Returns the label image for the given column of the given element.
     *
     * @param element the object representing the entire row, or <code>null</code> indicating that
     *     no input object is set in the viewer
     * @param columnIndex the zero-based index of the column in which the label appears
     * @return Image or <code>null</code> if there is no image for the given object at columnIndex
     */
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    /**
     * Returns the label text for the given column of the given element.
     *
     * @param element the object representing the entire row, or <code>null</code> indicating that
     *     no input object is set in the viewer
     * @param columnIndex the zero-based index of the column in which the label appears
     * @return String or or <code>null</code> if there is no text for the given object at
     *     columnIndex
     */
    @SuppressWarnings("unchecked")
    public String getColumnText(Object element, int columnIndex) {
        if (!(element instanceof Map)) {
            return "";
        }
        Map<String, Object> map = (Map<String, Object>) element;

        Object obj = null;
        switch (columnIndex) {
            case 0:
                obj = map.get("0");
                return obj == null ? "" : obj.toString();
            case 1:
                obj = map.get("1");
                return obj == null ? "" : obj.toString();
            case 2:
                obj = map.get("2");
                return obj == null ? "" : obj.toString();
            case 3:
                obj = map.get("4");
                return obj == null ? "" : obj.toString();
        }
        return null;
    }

    /**
     * Returns whether the label would be affected by a change to the given property of the given
     * element. This can be used to optimize a non-structural viewer update. If the property
     * mentioned in the update does not affect the label, then the viewer need not update the label.
     *
     * @param element the element
     * @param property the property
     * @return <code>true</code> if the label would be affected, and <code>false</code> if it would
     *     be unaffected
     */
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    /**
     * Disposes of this label provider. When a label provider is attached to a viewer, the viewer
     * will automatically call this method when the viewer is being closed. When label providers are
     * used outside of the context of a viewer, it is the client's responsibility to ensure that
     * this method is called when the provider is no longer needed.
     */
    public void dispose() {}

    /**
     * Adds a listener to this label provider. Has no effect if an identical listener is already
     * registered.
     *
     * <p>Label provider listeners are informed about state changes that affect the rendering of the
     * viewer that uses this label provider.
     *
     * @param listener a label provider listener
     */
    public void addListener(ILabelProviderListener listener) {}

    /**
     * Removes a listener to this label provider. Has no affect if an identical listener is not
     * registered.
     *
     * @param listener a label provider listener
     */
    public void removeListener(ILabelProviderListener listener) {}

    public Color getForeground(Object element) {
        return null;
    }

    public Color getBackground(Object element) {
        return null;
    }
}
