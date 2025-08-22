/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.er.editor;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.editor.IAttributeColumn;
import com.cubrid.common.ui.er.Messages;
import java.util.Map;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * RelationMapColumnLabelProvider Description
 *
 * @author Yu Guojia
 * @version 1.0 - 2014-5-21 created by Yu Guojia
 */
public class RelationMapColumnLabelProvider implements ITableLabelProvider, ITableColorProvider {

    private IPhysicalLogicalEditComposite editComposite;

    public RelationMapColumnLabelProvider(IPhysicalLogicalEditComposite editComposite) {
        this.editComposite = editComposite;
    }

    public Image getColumnImage(Object element, int columnIndex) {

        return null;
    }

    public String getColumnText(Object element, int columnIndex) {
        if (element == null) {
            return null;
        }
        Map.Entry<String, String> entry = (Map.Entry<String, String>) element;

        String property = editComposite.getPropertyName(columnIndex);
        if (IAttributeColumn.COL_FLAG.equals(property)) {
            if (StringUtil.isEmpty(entry.getKey())) {
                return "*";
            }
        } else if (Messages.tblcolumnPhysical.equals(property)) {
            return entry.getKey();
        } else if (Messages.tblcolumnLogical.equals(property)) {
            return entry.getValue();
        }

        return null;
    }

    public void addListener(ILabelProviderListener listener) {}

    public void dispose() {}

    public boolean isLabelProperty(Object element, String property) {

        return false;
    }

    public void removeListener(ILabelProviderListener listener) {}

    public Color getForeground(Object element, int columnIndex) {

        return null;
    }

    public Color getBackground(Object element, int columnIndex) {

        return null;
    }
}
