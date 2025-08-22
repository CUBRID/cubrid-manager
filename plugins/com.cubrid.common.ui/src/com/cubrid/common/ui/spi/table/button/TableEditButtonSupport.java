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
package com.cubrid.common.ui.spi.table.button;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TableItem;

/**
 * Support an edit button on the tableviewer
 *
 * @author Isaiah Choe
 * @version 1.0 - 2013-07-13 created by Isaiah Choe
 */
public class TableEditButtonSupport {
    private Button editSchemaCommentBtn;
    private TableEditor editSchemaCommentEditor;

    public TableEditButtonSupport(
            final TableViewer tableViewer,
            final ITableButtonSupportEvent eventHandler,
            final int buttonIndex) {
        tableViewer.addSelectionChangedListener(
                new ISelectionChangedListener() {
                    public void selectionChanged(SelectionChangedEvent event) {
                        TableItem[] items = tableViewer.getTable().getSelection();
                        if (items.length != 1) {
                            return;
                        }
                        TableItem item = items[0];
                        final int row = tableViewer.getTable().getSelectionIndex();
                        if (editSchemaCommentBtn != null && !editSchemaCommentBtn.isDisposed()) {
                            editSchemaCommentBtn.dispose();
                            editSchemaCommentBtn = null;
                        }
                        if (editSchemaCommentBtn == null) {
                            editSchemaCommentBtn = new Button(tableViewer.getTable(), SWT.None);
                            editSchemaCommentBtn.setText("...");
                            Rectangle location = item.getBounds();
                            editSchemaCommentBtn.setBounds(0, 0, 20, location.height);
                            editSchemaCommentBtn.addSelectionListener(
                                    new SelectionAdapter() {
                                        public void widgetSelected(SelectionEvent e) {
                                            eventHandler.showEditDialog(
                                                    tableViewer.getTable(), row);
                                        }
                                    });
                        }
                        if (editSchemaCommentEditor != null) {
                            editSchemaCommentEditor.dispose();
                            editSchemaCommentEditor = null;
                        }
                        editSchemaCommentEditor = new TableEditor(tableViewer.getTable());
                        editSchemaCommentEditor.minimumWidth = editSchemaCommentBtn.getSize().x;
                        editSchemaCommentEditor.horizontalAlignment = SWT.RIGHT;
                        editSchemaCommentEditor.setEditor(editSchemaCommentBtn, item, buttonIndex);
                    }
                });
    }
}
