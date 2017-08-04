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
package com.cubrid.common.ui.cubrid.table.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * Table column editor
 * 
 * @author pangqiren
 * @version 1.0 - 2011-3-7 created by pangqiren
 */
public class IndexTableItemEditor implements
		Listener {

	public static final int COLUMN_EDITOR_TYPE_TEXT = 1;
	public static final int COLUMN_EDITOR_TYPE_CCOMBO = 2;
	public final static String ORDER_ASC = "ASC"; //$NON-NLS-1$
	public final static String ORDER_DESC = "DESC"; //$NON-NLS-1$

	protected boolean isRunning = false;
	protected final TableItem item;
	protected final int column;
	protected Text text;
	protected CCombo combo;
	protected Table table;
	protected int columnEditorType;

	/**
	 * The constructor
	 * 
	 * @param table Table
	 * @param item TableItem
	 * @param column int
	 */
	public IndexTableItemEditor(Table table, final TableItem item, int column,
			int columnEditorType) {
		this.table = table;
		this.item = item;
		this.column = column;
		this.columnEditorType = columnEditorType;
		init();
	}

	/**
	 * 
	 * Initial the table column editor
	 * 
	 */
	private void init() {
		final TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;

		if (columnEditorType == COLUMN_EDITOR_TYPE_CCOMBO) {
			combo = new CCombo(table, SWT.NONE);
			combo.setEditable(false);

			combo.addListener(SWT.Selection, this);
			combo.addListener(SWT.FocusOut, this);
			combo.addListener(SWT.Traverse, this);

			editor.setEditor(combo, item, column);

			combo.add("ASC"); //$NON-NLS-1$
			combo.add("DESC"); //$NON-NLS-1$

			if (item.getText(column).equals("ASC")) { //$NON-NLS-1$
				combo.select(0);
			} else {
				combo.select(1);
			}
			combo.setFocus();

		} else if (columnEditorType == COLUMN_EDITOR_TYPE_TEXT) {
			text = new Text(table, SWT.MULTI | SWT.WRAP);
			text.setTextLimit(10);
			text.addListener(SWT.FocusOut, this);
			text.addListener(SWT.Traverse, this);

			editor.setEditor(text, item, column);
			text.setText(item.getText(column));
			text.selectAll();
			text.setFocus();

			text.addVerifyListener(new VerifyListener() {
				public void verifyText(VerifyEvent event) {
					event.doit = false;
					char ch = event.character;
					if (Character.isDigit(ch)) {
						event.doit = true;
					}
					if (ch == '\b' || ch == SWT.DEL) {
						event.doit = true;
					}
				}
			});
		}
	}

	/**
	 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
	 * @param event the event which occurred
	 */
	public void handleEvent(final Event event) {
		if (columnEditorType == COLUMN_EDITOR_TYPE_CCOMBO) {
			handleCombo(event);
		} else if (columnEditorType == COLUMN_EDITOR_TYPE_TEXT) {
			handleText(event);
		}
	}

	/**
	 * 
	 * Handle the text related event
	 * 
	 * @param event Event
	 */
	private void handleText(final Event event) {
		if (event.type == SWT.FocusOut) {
			if (isRunning) {
				return;
			}
			isRunning = true;
			item.setText(column, text.getText());
			text.dispose();
			isRunning = false;
		} else if (event.type == SWT.Traverse
				&& event.detail == SWT.TRAVERSE_ESCAPE) {
			if (isRunning) {
				return;
			}
			isRunning = true;
			text.dispose();
			event.doit = false;
			isRunning = false;
		}
	}

	/**
	 * 
	 * Handle combo related event
	 * 
	 * @param event Event
	 */
	private void handleCombo(final Event event) {
		if (event.type == SWT.SELECTED) {
			if (isRunning) {
				return;
			}
			isRunning = true;
			item.setText(3, combo.getText());
			isRunning = false;
		} else if (event.type == SWT.FocusOut) {
			if (isRunning) {
				return;
			}
			isRunning = true;
			item.setText(3, combo.getText());
			combo.dispose();
			isRunning = false;
		} else if (event.type == SWT.Traverse
				&& event.detail == SWT.TRAVERSE_ESCAPE) {
			if (isRunning) {
				return;
			}
			isRunning = true;
			combo.dispose();
			event.doit = false;
			isRunning = false;
		}
	}
}
