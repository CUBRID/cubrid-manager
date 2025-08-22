/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *  - Neither the name of the <ORGANIZATION> nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
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
package com.cubrid.common.ui.spi.table;

import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Widget;

/**
 * Editable table viewer
 *
 * @author pangqiren
 * @version 1.0 - 2012-7-23 created by pangqiren
 */
public class EditableTableViewer extends TableViewer {

    /**
     * The constructor
     *
     * @param parent the parent control
     */
    public EditableTableViewer(Composite parent) {
        this(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, null);
    }

    /**
     * The constructor
     *
     * @param parent the parent control
     * @param style SWT style bits
     */
    public EditableTableViewer(Composite parent, int style) {
        this(parent, style, null);
    }

    /**
     * The constructor
     *
     * @param parent the parent control
     * @param style SWT style bits
     * @param editor TableViewerEditor
     */
    public EditableTableViewer(Composite parent, int style, TableViewerEditor editor) {
        super(new Table(parent, style));

        if (editor == null) {
            TableViewerEditor defaultEditor =
                    new TableViewerEditor(
                            this,
                            new TableFocusCellManager(this, new TableFocusHighLighter(this)),
                            new ColumnViewerEditorActivationStrategy(this),
                            ColumnViewerEditor.TABBING_HORIZONTAL
                                    | ColumnViewerEditor.TABBING_VERTICAL
                                    | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR);
            setColumnViewerEditor(defaultEditor);
        } else {
            setColumnViewerEditor(editor);
        }
        // set the default table font
        Table table = getTable();
        if (parent.getFont() != null
                && parent.getFont().getFontData() != null
                && parent.getFont().getFontData().length > 0) {
            FontData[] fontDatas = parent.getFont().getFontData();
            for (FontData fontData : fontDatas) {
                fontData.setHeight(fontData.getHeight() + 5);
            }
            Font font = new Font(Display.getDefault(), fontDatas);
            table.setFont(font);
        }
    }

    /**
     * Get the viewer row by item
     *
     * @param item Widget
     * @return ViewerRow
     */
    public ViewerRow getViewerRowByItem(Widget item) {
        return super.getViewerRowFromItem(item);
    }
}
