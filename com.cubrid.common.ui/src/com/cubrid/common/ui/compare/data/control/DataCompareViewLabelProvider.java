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
package com.cubrid.common.ui.compare.data.control;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.compare.data.model.CompareViewData;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class DataCompareViewLabelProvider extends LabelProvider
        implements ITableLabelProvider, ITableColorProvider {
    private static final Color DIFFERENT_COLOR = new Color(Display.getCurrent(), 255, 32, 32);
    private static final Color SOURCE_BG_COLOR = new Color(Display.getCurrent(), 255, 255, 255);
    private static final Color TARGET_BG_COLOR = new Color(Display.getCurrent(), 250, 250, 250);

    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof CompareViewData) {
            CompareViewData comp = (CompareViewData) element;
            if (comp.getData() != null && comp.getData().size() > columnIndex) {
                if (columnIndex != 0 || comp.isSource()) {
                    return comp.getData().get(columnIndex);
                }
            }
        }

        return "";
    }

    public Color getForeground(Object element, int columnIndex) {
        if (element instanceof CompareViewData) {
            CompareViewData comp = (CompareViewData) element;

            String curr = null;
            if (comp.getData() != null && comp.getData().size() > columnIndex) {
                curr = comp.getData().get(columnIndex);
            }
            String prev = null;
            if (comp.getReferer() != null
                    && comp.getReferer().getData() != null
                    && comp.getReferer().getData().size() > columnIndex) {
                prev = comp.getReferer().getData().get(columnIndex);
            }

            if (!StringUtil.isEqual(curr, prev)) {
                return DIFFERENT_COLOR;
            }
        }

        return null;
    }

    public Color getBackground(Object element, int columnIndex) {
        if (element instanceof CompareViewData) {
            CompareViewData comp = (CompareViewData) element;
            if (comp.isSource()) {
                return SOURCE_BG_COLOR;
            } else {
                return TARGET_BG_COLOR;
            }
        }

        return null;
    }
}
