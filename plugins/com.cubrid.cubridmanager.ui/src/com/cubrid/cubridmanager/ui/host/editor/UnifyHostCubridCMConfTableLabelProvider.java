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

import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.TableLabelProvider;
import java.util.HashMap;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

/**
 * @author fulei
 * @version 1.0 - 2013-3-19 created by fulei
 */
public class UnifyHostCubridCMConfTableLabelProvider extends TableLabelProvider
        implements ITableColorProvider, ITableFontProvider {

    private Color[] bg =
            new Color[] {new Color(null, 255, 255, 255), new Color(null, 247, 247, 240)};
    private Color[] force = new Color[] {new Color(null, 0, 0, 0), new Color(null, 0, 0, 0)};
    private Object current = null;
    private int currentColor = 0;
    private Font propertyNameRowFont = new Font(Display.getCurrent(), "SansSerif", 10, SWT.BOLD);

    public Color getForeground(Object element, int columnIndex) {
        if (element instanceof HashMap) {
            HashMap<String, String> valueMap = (HashMap<String, String>) element;
            if (valueMap.get("0").equals(UnifyHostConfigEditor.CUBRIDNAMECOLUMNTITLE)
                    || valueMap.get("0").equals(UnifyHostConfigEditor.SERVERNAMECOLUMNTITLE)) {
                return ResourceManager.getColor(SWT.COLOR_BLUE);
            }
        }
        return force[currentColor];
    }

    public Color getBackground(Object element, int columnIndex) {
        if (current != element) {
            currentColor = 1 - currentColor;
            current = element;
        }
        return bg[currentColor];
    }

    public Font getFont(Object element, int columnIndex) {
        if (element instanceof HashMap) {
            HashMap<String, String> valueMap = (HashMap<String, String>) element;
            if (valueMap.get("0").equals(UnifyHostConfigEditor.SERVERNAMECOLUMNTITLE)
                    && columnIndex == 0) {
                return propertyNameRowFont;
            }
        }
        return null;
    }
}
