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
package com.cubrid.common.ui.spi.util;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * TableViewer Utility class
 *
 * @author pangqiren
 * @version 1.0 - 2009-12-23 created by pangqiren
 */
public final class TableViewUtil {

    private TableViewUtil() {
        // empty
    }

    /**
     * Create TableLayout object
     *
     * @param weights the weights array
     * @return the TableLayout object
     */
    public static TableLayout createTableViewLayout(int[] weights) {
        if (weights == null || weights.length == 0) {
            return null;
        }
        TableLayout lay = new TableLayout();
        for (int i = 0, len = weights.length; i < len; i++) {
            lay.addColumnData(new ColumnWeightData(weights[i]));
        }
        return lay;
    }

    /**
     * Create TableColumn object
     *
     * @param parent the table object
     * @param align the align style
     * @param label the string value
     * @return the TableColumn object
     */
    public static TableColumn createTableColumn(Table parent, int align, String label) {
        final TableColumn col = new TableColumn(parent, SWT.NONE);
        col.setAlignment(align);
        col.setText(label);
        return col;
    }
}
