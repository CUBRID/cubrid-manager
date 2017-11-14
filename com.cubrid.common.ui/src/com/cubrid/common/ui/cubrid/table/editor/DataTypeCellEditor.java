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
package com.cubrid.common.ui.cubrid.table.editor;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.cubrid.table.TableEditorAdaptor;

public final class DataTypeCellEditor extends CellEditor {
	private String[] items;
	private DataTypeCombo comboBox;
	private ModifyListener modifyListener;
	private Object value = null;
	private static final int DEFAULTSTYLE = SWT.NONE;
	private TableEditorAdaptor editorAdaptor = null;

	public DataTypeCellEditor() {
		setStyle(DEFAULTSTYLE);
	}

	public DataTypeCellEditor(Composite parent) {
		super(parent, DEFAULTSTYLE);
	}

	public DataTypeCellEditor(Composite parent, String[] items,
			TableEditorAdaptor editor) {
		super(parent, DEFAULTSTYLE);
		setItems(items);
		this.editorAdaptor = editor;
	}

	public String[] getItems() {
		return items.clone();
	}

	public void setItems(String[] items) {
		Assert.isNotNull(items);
		this.items = items.clone();
		populateComboBoxItems();
	}

	private void populateComboBoxItems() {
		if (comboBox != null && items != null) {
			comboBox.removeAll();
			for (int i = 0; i < items.length; i++) {
				comboBox.add(items[i], i);
			}
			comboBox.setText("");
		}
	}

	protected Control createControl(Composite parent) {
		comboBox = new DataTypeCombo(parent, getStyle());
		comboBox.setFont(parent.getFont());

		populateComboBoxItems();

		comboBox.addModifyListener(getModifyListener());

		comboBox.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent event) {
				applyEditorValueAndDeactivate();
			}

			public void widgetSelected(SelectionEvent event) {
				value = comboBox.getText();
			}
		});

		comboBox.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent event) {
				if (event.detail == SWT.TRAVERSE_ESCAPE
						|| event.detail == SWT.TRAVERSE_RETURN) {
					event.doit = false;
				}
			}
		});

		comboBox.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent event) {
				applyEditorValueAndDeactivate();
				DataTypeCellEditor.this.focusLost();
			}
		});

		return comboBox;
	}

	private void applyEditorValueAndDeactivate() {
		if (editorAdaptor != null) {
			editorAdaptor.hideToolTip();
		}

		Object newValue = comboBox.getText();
		if (newValue != null && !newValue.equals(value == null ? "" : value.toString())) {
			boolean newValidState = isCorrect(newValue);
			if (newValidState) {
				markDirty();
				doSetValue(newValue);
			} else {
				setErrorMessage("Error value has been set."); //$NON-NLS-1$
			}
		}
		fireApplyEditorValue();
		deactivate();
	}

	protected Object doGetValue() {
		return value;
	}

	protected void doSetFocus() {
		if (comboBox != null) {
			comboBox.setFocus();
			showDataTypeAdvisor();
		}
	}

	private void showDataTypeAdvisor() { // FIXME move this logic to core module
		if (editorAdaptor != null && comboBox != null) {
			String title = "";
			String message = "";
			String dataType = comboBox.getText();
			if (dataType.startsWith("BIT VARYING")) {
				title = "BIT VARYING";
				message = Messages.tipsDataType1;
			} else if (dataType.startsWith("BIT")) {
				title = "BIT";
				message = Messages.tipsDataType2;
			} else if (dataType.startsWith("BLOB")) {
				title = "BLOB";
				message = Messages.tipsDataType3;
			} else if (dataType.startsWith("CHAR")) {
				title = "CHAR";
				message = Messages.tipsDataType4;
			} else if (dataType.startsWith("CLOB")) {
				title = "CLOB";
				message = Messages.tipsDataType5;
			} else if (dataType.startsWith("TIMESTAMP")) {
				title = "TIMESTAMP";
				message = Messages.tipsDataType6;
			} else if (dataType.startsWith("DATETIME")) {
				title = "DATETIME";
				message = Messages.tipsDataType7;
			} else if (dataType.startsWith("DATE")) {
				title = "DATE";
				message = Messages.tipsDataType8;
			} else if (dataType.startsWith("TIME")) {
				title = "TIME";
				message = Messages.tipsDataType9;
			} else if (dataType.startsWith("MONETARY")) {
				title = "MONETARY";
				message = Messages.tipsDataType10;
			} else if (dataType.startsWith("NCHAR VARYING")) {
				title = "NCHAR VARYING";
				message = Messages.tipsDataType11;
			} else if (dataType.startsWith("NCHAR")) {
				title = "NCHAR";
				message = Messages.tipsDataType12;
			} else if (dataType.startsWith("LIST")) {
				title = "LIST";
				message = Messages.tipsDataType13;
			} else if (dataType.startsWith("SEQUENCE")) {
				title = "SEQUENCE";
				message = Messages.tipsDataType14;
			} else if (dataType.startsWith("SET")) {
				title = "SET";
				message = Messages.tipsDataType15;
			} else if (dataType.startsWith("MULTISET")) {
				title = "MULTISET";
				message = Messages.tipsDataType16;
			} else if (dataType.startsWith("INTEGER")) {
				title = "INTEGER";
				message = Messages.tipsDataType17;
			} else if (dataType.startsWith("SMALLINT")) {
				title = "SMALLINT";
				message = Messages.tipsDataType18;
			} else if (dataType.startsWith("STRING")) {
				title = "STRING";
				message = Messages.tipsDataType19;
			} else if (dataType.startsWith("BIGINT")) {
				title = "BIGINT";
				message = Messages.tipsDataType20;
			} else if (dataType.startsWith("FLOAT")) {
				title = "FLOAT";
				message = Messages.tipsDataType21;
			} else if (dataType.startsWith("DOUBLE")) {
				title = "DOUBLE";
				message = Messages.tipsDataType22;
			} else if (dataType.startsWith("NUMERIC")) {
				title = "NUMERIC";
				message = Messages.tipsDataType23;
			} else if (dataType.startsWith("VARCHAR")) {
				title = "VARCHAR";
				message = Messages.tipsDataType24;
			} else if (dataType.startsWith("ENUM")) {
				title = "ENUM";
				message = Messages.tipsDataType25;
			}

			Rectangle rect = comboBox.getBounds();
			title = Messages.bind(Messages.tipsDataTypeTitle, title);
			editorAdaptor.showToolTip(rect, title, message);
		}
	}

	protected void doSetValue(Object value) {
		String stringValue = (String) value;
		if (stringValue != null && !stringValue.trim().toUpperCase().startsWith("ENUM")) {
			stringValue = stringValue.toUpperCase();
		}
		this.value = stringValue;
		updateContents(stringValue);
	}

	private void updateContents(Object value) {
		if (comboBox != null && value instanceof String) {
			comboBox.removeModifyListener(getModifyListener());
			comboBox.setText((String) value);
			comboBox.addModifyListener(getModifyListener());
		}
	}

	protected void editOccured(ModifyEvent event) {
		String value = comboBox.getText();
		if (value == null) {
			value = ""; //$NON-NLS-1$
		}
		Object typedValue = value;
		boolean oldValidState = isValueValid();
		boolean newValidState = isCorrect(typedValue);
		if (!newValidState) {
			setErrorMessage("The value you input is invalid"); //$NON-NLS-1$
		}
		valueChanged(oldValidState, newValidState);

		showDataTypeAdvisor();
	}

	private ModifyListener getModifyListener() {
		if (modifyListener == null) {
			modifyListener = new ModifyListener() {
				public void modifyText(ModifyEvent event) {
					editOccured(event);
				}
			};
		}

		return modifyListener;
	}

	protected void focusLost() {
		if (isActivated()) {
			applyEditorValueAndDeactivate();
		}
	}
}
