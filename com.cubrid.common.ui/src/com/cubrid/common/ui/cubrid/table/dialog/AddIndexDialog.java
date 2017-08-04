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
package com.cubrid.common.ui.cubrid.table.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.cubrid.common.core.common.model.Constraint;
import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.FieldHandlerUtils;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;

/**
 * The dialog of add index
 * 
 * @author pangqiren 2009-6-4
 */
public class AddIndexDialog extends
		CMTitleAreaDialog {

	private final static String CUB_UNIQUE = "UNIQUE"; //$NON-NLS-1$
	private final static String CUB_INDEX = "INDEX"; //$NON-NLS-1$
	private final static String CUB_RUNIQUE = "REVERSE UNIQUE"; //$NON-NLS-1$
	private final static String CUB_RINDEX = "REVERSE INDEX"; //$NON-NLS-1$
	private final SchemaInfo schemaInfo;
	private Button upBTN;
	private Button downBTN;
	private Table columnTable;
	private Group group;
	private Text indexNameText;
	private Text indexDescriptionText;
	private Constraint indexConstraint;
	private Constraint editedIndex;
	private int newIndex = -1;
	private final boolean isSupportPrefixLength;
	private String errorMsg;
	private Button btnIndex;
	private Button btnUnique;
	private Button btnRevIndex;
	private Button btnRevUnique;
	private boolean isNewConstraint;
	private boolean isCommentSupport;

	/**
	 * The constructor
	 * 
	 * @param parentShell
	 * @param newSchema
	 * @param database
	 * @param editedIndex
	 */
	public AddIndexDialog(Shell parentShell, SchemaInfo newSchema,
			CubridDatabase database, Constraint editedIndex, boolean isNewConstraint) {
		super(parentShell);
		this.schemaInfo = newSchema;
		this.editedIndex = editedIndex;
		this.isSupportPrefixLength = CompatibleUtil.isSupportPrefixIndexLength(database.getDatabaseInfo());
		this.isNewConstraint = isNewConstraint;
		this.isCommentSupport = CompatibleUtil.isCommentSupports(database.getDatabaseInfo());
	}

	/**
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 * @param parent The parent composite to contain the dialog area
	 * @return the dialog area control
	 */
	protected Control createDialogArea(Composite parent) {
		Composite parentComp = (Composite) super.createDialogArea(parent);

		Composite composite = new Composite(parentComp, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);

		createComposite(composite);

		setInfo();
		if (editedIndex == null) {
			setTitle(Messages.msgTitleAddIndex);
			setMessage(Messages.msgAddIndex);
		} else {
			setTitle(Messages.msgTitleEditIndex);
			setMessage(Messages.msgEditIndex);
		}

		return parentComp;
	}

	/**
	 * Set the information
	 * 
	 */
	private void setInfo() {
		indexNameText.setEnabled(true);
		Map<String, String[]> ruleMap = new HashMap<String, String[]>();
		if (editedIndex == null) {
			setSelectIndexType(CUB_INDEX);
		} else {
			indexNameText.setText(editedIndex.getName());
			String indexType = editedIndex.getType();
			setSelectIndexType(indexType);

			List<String> rules = editedIndex.getRules();
			for (String rule : rules) {
				String[] strs = rule.trim().split(" ");
				String columnName = strs[0];
				String order = strs[1];
				String prefixLength = "";
				if (strs[0].indexOf("(") > 0) {
					columnName = strs[0].substring(0, strs[0].indexOf("("));
					prefixLength = strs[0].substring(strs[0].indexOf("(") + 1,
							strs[0].indexOf(")"));
				}
				String[] ruleArr = {columnName, order, prefixLength };
				ruleMap.put(columnName.toUpperCase(), ruleArr);
			}
		}

		List<DBAttribute> attrList = schemaInfo.getLocalAttributes();
		for (int i = 0, n = attrList.size(); i < n; i++) {
			DBAttribute attr = attrList.get(i);
			String[] ruleArr = ruleMap.get(attr.getName().toUpperCase());
			TableItem item = new TableItem(columnTable, SWT.NONE);
			item.setText(1, attr.getName());
			item.setText(2, DataType.getShownType(attr.getType()));
			item.setText(3, ruleArr == null ? IndexTableItemEditor.ORDER_ASC
					: ruleArr[1].toUpperCase());
			item.setText(4, ruleArr == null ? "" : ruleArr[2]);
			item.setForeground(ResourceManager.getColor(128, 128, 128));
			item.setChecked(ruleArr != null);
		}
		setBtnEnable();
	}

	/**
	 * @see com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog#constrainShellSize()
	 */
	protected void constrainShellSize() {
		super.constrainShellSize();
		if (editedIndex == null) {
			getShell().setText(Messages.msgTitleAddIndex);
		} else {
			getShell().setText(Messages.msgTitleEditIndex);
		}
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 * @param parent the button bar composite
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Messages.btnOK, true);
		createButton(parent, IDialogConstants.CANCEL_ID, Messages.btnCancel,
				false);
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 * @param buttonId the id of the button that was pressed (see
	 *        <code>IDialogConstants.*_ID</code> constants)
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			errorMsg = null;
			setErrorMessage(null);
			int count = columnTable.getItemCount();

			indexConstraint = new Constraint(true);
			String indexType = getSelectedIndexType();
			indexConstraint.setType(indexType);

			int indexColumnCount = 0;
			boolean isHasPrefixLength = false;
			for (int i = 0; i < count; i++) {
				TableItem item = columnTable.getItem(i);
				if (!item.getChecked()) {
					continue;
				}
				indexColumnCount++;
				indexConstraint.addAttribute(item.getText(1));
				String indexPrefixLength = getPrefixLength(item);
				if (errorMsg != null) {
					setErrorMessage(errorMsg);
					return;
				}
				if (indexPrefixLength.length() > 0) {
					isHasPrefixLength = true;
				}
				indexConstraint.addRule(item.getText(1) + indexPrefixLength
						+ " " + item.getText(3));

			}
			if (indexConstraint.getAttributes().size() == 0) {
				setErrorMessage(Messages.errSelectMoreColumns);
				return;
			}
			if (indexColumnCount > 1 && isHasPrefixLength) {
				setErrorMessage(Messages.errMultColIndexPrefixLength);
				return;
			}
			String indexName = indexNameText.getText().trim();
			String tableName = schemaInfo.getClassname();
			if (("").equals(indexName)) { //$NON-NLS-1$
				indexName = indexConstraint.getDefaultName(tableName);
			}
			indexConstraint.setName(indexName);
			if (isCommentSupport) {
				String description = indexDescriptionText.getText().trim();
				indexConstraint.setDescription(description);
			}
			List<Constraint> constraintList = new ArrayList<Constraint>();
			constraintList.addAll(schemaInfo.getConstraints());
			if (editedIndex != null) {
				constraintList.remove(editedIndex);
			}
			
			/*For bug TOOLS-2394 Unique index can't be added again*/
			if (editedIndex == null
					&& CUB_UNIQUE.equals(indexConstraint.getType())
					&& indexConstraint.getAttributes().size() == 1
					&& schemaInfo.getUniqueByAttrName(indexConstraint.getAttributes().get(
							0)) != null) {
				setErrorMessage(Messages.errExistUniqueSameRule);
				return;
			}

			for (Constraint constraint : constraintList) {
				if (CUB_INDEX.equals(constraint.getType())
						&& constraint.getType().equals(indexType)) {
					List<String> rules = constraint.getRules();
					if (rules.equals(indexConstraint.getRules())) {
						setErrorMessage(Messages.errExistIndex);
						return;
					}
				} else if (CUB_RINDEX.equals(constraint.getType())
						&& constraint.getType().equals(indexType)) {
					List<String> attrs = constraint.getAttributes();
					if (attrs.equals(indexConstraint.getAttributes())) {
						setErrorMessage(Messages.errExistReverseIndex);
						return;
					}
				} else if (CUB_UNIQUE.equals(constraint.getType())
						&& constraint.getType().equals(indexType)) {
					if (constraint.getName().equals(indexConstraint.getName())) {
						setErrorMessage(Messages.bind(Messages.errExistUniqueIndex, constraint.getName()));
						return;
					}
				} else if (CUB_RUNIQUE.equals(constraint.getType())
						&& constraint.getType().equals(indexType)
						&& constraint.getName().equals(
								indexConstraint.getName())) {
					setErrorMessage(Messages.errExistReverseUniqueIndex);
					return;
				}
			}
		}
		super.buttonPressed(buttonId);
	}

	/**
	 * 
	 * Get index prefix length
	 * 
	 * @param item TableItem
	 * @return String
	 */
	private String getPrefixLength(TableItem item) {
		String indexPrefixLength = "";
		String indexType = getSelectedIndexType();
		String columnName = item.getText(1);
		String dataType = item.getText(2);
		if (isSupportPrefixLength
				&& FieldHandlerUtils.isSupportPrefixIndex(dataType)
				&& CUB_INDEX.equals(indexType)) {

			String dataTypeLength = FieldHandlerUtils.MAXSIZE;
			int start = dataType.indexOf("(");
			int end = dataType.indexOf(")");
			if (start > 0 && end > start) {
				dataTypeLength = dataType.substring(start + 1, end);
			}

			indexPrefixLength = item.getText(4) == null ? ""
					: item.getText(4).trim();
			if (indexPrefixLength.length() > 0) {
				if (Long.parseLong(dataTypeLength) < Long.parseLong(indexPrefixLength)) {
					errorMsg = Messages.bind(Messages.errIndexPrefixLength,
							columnName);
				} else {
					indexPrefixLength = "(" + indexPrefixLength + ")";
				}
			}
		}
		return indexPrefixLength;
	}

	/**
	 * Create Composite
	 * 
	 * @param parent Composite
	 */
	private void createComposite(Composite parent) {

		group = new Group(parent, SWT.NONE);
		{
			group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 2;
			group.setLayout(gridLayout);
		}

		Label indexNameLabel = new Label(group, SWT.NONE);
		indexNameLabel.setText(Messages.lblIndexName);
		indexNameText = new Text(group, SWT.BORDER);
		{
			indexNameText.setTextLimit(ValidateUtil.MAX_SCHEMA_NAME_LENGTH);
			GridData gdConstraintNameText = new GridData();
			gdConstraintNameText.grabExcessHorizontalSpace = true;
			gdConstraintNameText.verticalAlignment = GridData.CENTER;
			gdConstraintNameText.horizontalAlignment = GridData.FILL;
			indexNameText.setLayoutData(gdConstraintNameText);
		}

		Label indexTypeLabel = new Label(group, SWT.NONE);
		indexTypeLabel.setLayoutData(new GridData(150, SWT.DEFAULT));
		indexTypeLabel.setText(Messages.lblIndexType);
		createIndexTypeCombo();

		Label titleLabel = new Label(group, SWT.NONE);
		{
			titleLabel.setText(Messages.lblSelectColumns);
			GridData gridData = new GridData();
			gridData.horizontalSpan = 2;
			gridData.verticalSpan = 1;
			titleLabel.setLayoutData(gridData);
		}
		createColumnTable();

		if (isCommentSupport) {
			Label indexDescriptionLabel = new Label(group, SWT.NONE);
			indexDescriptionLabel.setText(Messages.lblIndexDescription);
			indexDescriptionText = new Text(group, SWT.BORDER);
			{
				indexDescriptionText.setTextLimit(ValidateUtil.MAX_DB_OBJECT_COMMENT);
				GridData gdConstraintDescText = new GridData();
				gdConstraintDescText.grabExcessHorizontalSpace = true;
				gdConstraintDescText.verticalAlignment = GridData.CENTER;
				gdConstraintDescText.horizontalAlignment = GridData.FILL;
				indexDescriptionText.setLayoutData(gdConstraintDescText);
			}
		}

		Composite btnComposite = new Composite(parent, SWT.NONE);
		{
			GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 2;
			GridData gridData = new GridData();
			gridData.horizontalSpan = 2;
			gridData.horizontalAlignment = GridData.FILL;
			gridData.verticalAlignment = GridData.CENTER;
			gridData.grabExcessHorizontalSpace = true;
			btnComposite.setLayoutData(gridData);
			btnComposite.setLayout(gridLayout);
		}

		upBTN = new Button(btnComposite, SWT.NONE);
		{
			upBTN.setText(Messages.btnUp);
			GridData gdUpBTN = new GridData();
			gdUpBTN.horizontalAlignment = GridData.END;
			gdUpBTN.grabExcessHorizontalSpace = true;
			gdUpBTN.verticalAlignment = GridData.CENTER;
			upBTN.setLayoutData(gdUpBTN);
		}
		upBTN.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (columnTable.getSelectionCount() < 1) {
					return;
				}

				int selectionIndex = columnTable.getSelectionIndex();
				if (selectionIndex == 0) {
					return;
				}

				boolean tmpCheck;
				String tmpName, tmpDomain, tmpOrder;
				String tmpPrefixLength = null;
				Color tmpColor;
				TableItem selectedItem = columnTable.getSelection()[0];

				TableItem targetItem = columnTable.getItem(selectionIndex - 1);
				tmpCheck = targetItem.getChecked();
				tmpName = targetItem.getText(1);
				tmpDomain = targetItem.getText(2);
				tmpOrder = targetItem.getText(3);
				if (isSupportPrefixLength) {
					tmpPrefixLength = targetItem.getText(4);
				}
				tmpColor = targetItem.getForeground();

				targetItem.setChecked(selectedItem.getChecked());
				targetItem.setText(1, selectedItem.getText(1));
				targetItem.setText(2, selectedItem.getText(2));
				targetItem.setText(3, selectedItem.getText(3));
				if (isSupportPrefixLength) {
					targetItem.setText(4, selectedItem.getText(4));
				}
				targetItem.setForeground(selectedItem.getForeground());

				selectedItem.setChecked(tmpCheck);
				selectedItem.setText(1, tmpName);
				selectedItem.setText(2, tmpDomain);
				selectedItem.setText(3, tmpOrder);
				if (tmpPrefixLength != null) {
					selectedItem.setText(4, tmpPrefixLength);
				}
				selectedItem.setForeground(tmpColor);

				columnTable.setSelection(selectionIndex - 1);
				setBtnEnable();
			}
		});

		downBTN = new Button(btnComposite, SWT.NONE);
		{
			downBTN.setText(Messages.btnDown);
			GridData gdDownBTN = new GridData();
			gdDownBTN.grabExcessHorizontalSpace = true;
			downBTN.setLayoutData(gdDownBTN);
		}
		downBTN.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {

				if (columnTable.getSelectionCount() < 1) {
					return;
				}

				int selectionIndex = columnTable.getSelectionIndex();
				if (selectionIndex == columnTable.getItemCount() - 1) {
					return;
				}

				boolean tmpCheck;
				String tmpName, tmpDomain, tmpOrder;
				String tmpPrefixLength = null;
				Color tmpColor;
				TableItem selectedItem = columnTable.getSelection()[0];
				TableItem targetItem = columnTable.getItem(selectionIndex + 1);
				tmpCheck = targetItem.getChecked();
				tmpName = targetItem.getText(1);
				tmpDomain = targetItem.getText(2);
				tmpOrder = targetItem.getText(3);
				if (isSupportPrefixLength) {
					tmpPrefixLength = targetItem.getText(4);
				}
				tmpColor = targetItem.getForeground();

				targetItem.setChecked(selectedItem.getChecked());
				targetItem.setText(1, selectedItem.getText(1));
				targetItem.setText(2, selectedItem.getText(2));
				targetItem.setText(3, selectedItem.getText(3));
				if (isSupportPrefixLength) {
					targetItem.setText(4, selectedItem.getText(4));
				}
				targetItem.setForeground(selectedItem.getForeground());

				selectedItem.setChecked(tmpCheck);
				selectedItem.setText(1, tmpName);
				selectedItem.setText(2, tmpDomain);
				selectedItem.setText(3, tmpOrder);
				if (tmpPrefixLength != null) {
					selectedItem.setText(4, tmpPrefixLength);
				}
				selectedItem.setForeground(tmpColor);
				columnTable.setSelection(selectionIndex + 1);
				setBtnEnable();
			}
		});

	}

	/**
	 * Change directions of all index columns
	 *
	 * @param reverse
	 */
	private void changeDirection(boolean reverse) {
		int count = columnTable.getItemCount();
		for (int i = 0; i < count; i++) {
			TableItem item = columnTable.getItem(i);
			if (reverse) {
				item.setText(3, IndexTableItemEditor.ORDER_DESC);
			} else {
				item.setText(3, IndexTableItemEditor.ORDER_ASC);
			}
		}
	}

	/**
	 * Change to enable or diable index type buttons
	 *
	 * @param enabled
	 */
	private void changeEnabled(boolean enabled) {
		btnIndex.setEnabled(enabled);
		btnUnique.setEnabled(enabled);
		btnRevIndex.setEnabled(enabled);
		btnRevUnique.setEnabled(enabled);
	}

	/**
	 * Return selected index type from selected button
	 *
	 * @return
	 */
	private String getSelectedIndexType() {
		if (btnIndex.getSelection()) {
			return CUB_INDEX;
		} else if (btnUnique.getSelection()) {
			return CUB_UNIQUE;
		} else if (btnRevIndex.getSelection()) {
			return CUB_RINDEX;
		} else if (btnRevUnique.getSelection()) {
			return CUB_RUNIQUE;
		} else {
			return null;
		}
	}

	/**
	 * Select a button by indexType
	 * 
	 * @param indexType
	 */
	private void setSelectIndexType(String indexType) {
		btnIndex.setSelection(false);
		btnUnique.setSelection(false);
		btnRevIndex.setSelection(false);
		btnRevUnique.setSelection(false);

		if (indexType == null || CUB_INDEX.equals(indexType)) {
			btnIndex.setSelection(true);
		} else if (CUB_UNIQUE.equals(indexType)) {
			btnUnique.setSelection(true);
		} else if (CUB_RINDEX.equals(indexType)) {
			btnRevIndex.setSelection(true);
		} else if (CUB_RUNIQUE.equals(indexType)) {
			btnRevUnique.setSelection(true);
		} else {
			// TODO need to handle an error
		}
	}

	/**
	 * Create the index type combo
	 *
	 */
	private void createIndexTypeCombo() {
		Composite indexTypeComp = new Composite(group, SWT.NONE);
		{
			GridLayout gl = new GridLayout(4, false);
			indexTypeComp.setLayout(gl);
		}
		btnIndex = new Button(indexTypeComp, SWT.RADIO);
		btnIndex.setText("Index");
		btnIndex.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				changeDirection(false);
			}
		});
		btnUnique = new Button(indexTypeComp, SWT.RADIO);
		btnUnique.setText("Unique");
		btnUnique.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				changeDirection(false);
			}
		});

		btnRevIndex = new Button(indexTypeComp, SWT.RADIO);
		btnRevIndex.setText("Reverse Index");
		btnRevIndex.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				changeDirection(true);
			}
		});

		btnRevUnique = new Button(indexTypeComp, SWT.RADIO);
		btnRevUnique.setText("Reverse Unique");
		btnRevUnique.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				changeDirection(true);
			}
		});

		changeEnabled(editedIndex == null || isNewConstraint);
	}

	/**
	 * Create the column table
	 * 
	 */
	private void createColumnTable() {
		columnTable = new Table(group, SWT.FULL_SELECTION | SWT.BORDER
				| SWT.SINGLE | SWT.CHECK);
		{
			columnTable.setHeaderVisible(true);
			GridData gdColumnTable = new GridData(SWT.FILL, SWT.FILL, true,
					true, 2, 1);
			gdColumnTable.heightHint = 200;
			columnTable.setLayoutData(gdColumnTable);
			columnTable.setLinesVisible(true);
		}
		columnTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				final TableItem item = (TableItem) event.item;
				if (event.detail == SWT.CHECK) {
					if (item.getChecked()) {
						item.setForeground(ResourceManager.getColor(0, 0, 0));
					} else {
						item.setForeground(ResourceManager.getColor(128, 128,
								128));
					}
					columnTable.setSelection(new TableItem[]{item });
				}
				setBtnEnable();
				setHintMessage();
			}
		});
		CommonUITool.hackForYosemite(columnTable);
		
		TableColumn tblCol = new TableColumn(columnTable, SWT.LEFT);
		tblCol.setWidth(83);
		tblCol.setText(Messages.colUseColumn);

		tblCol = new TableColumn(columnTable, SWT.LEFT);
		tblCol.setWidth(123);
		tblCol.setText(Messages.colColumnName);

		tblCol = new TableColumn(columnTable, SWT.LEFT);
		tblCol.setWidth(196);
		tblCol.setText(Messages.colDataType);

		tblCol = new TableColumn(columnTable, SWT.LEFT);
		tblCol.setWidth(86);
		tblCol.setText(Messages.colOrder);

		if (isSupportPrefixLength) {
			tblCol = new TableColumn(columnTable, SWT.LEFT);
			tblCol.setWidth(196);
			tblCol.setText(Messages.colPrefixLength);
		}

		setTableEditor();
	}

	/**
	 * Set table editor
	 */
	private void setTableEditor() {
		columnTable.addListener(SWT.MouseUp, new Listener() {
			public void handleEvent(Event event) {
				if (event.button != 1) {
					return;
				}
				Point pt = new Point(event.x, event.y);
				int topIndex = columnTable.getTopIndex();
				int curIndex = newIndex;
				newIndex = columnTable.getSelectionIndex();
				if (curIndex < 0 || newIndex < 0 || topIndex > newIndex
						|| curIndex != newIndex) {
					return;
				}
				final TableItem item = columnTable.getItem(newIndex);
				if (item == null) {
					return;
				}
				String dataType = item.getText(2);
				String indexType = getSelectedIndexType();
				for (int i = 3; i < columnTable.getColumnCount(); i++) {
					Rectangle rect = item.getBounds(i);
					if (rect.contains(pt)) {
						boolean isCanEditOrder = i == 3
								&& (indexType.equals(CUB_INDEX) || indexType.equals(CUB_UNIQUE));
						boolean isCanEditPrefixLength = i == 4
								&& indexType.equals(CUB_INDEX)
								&& isSupportPrefixLength
								&& FieldHandlerUtils.isSupportPrefixIndex(dataType);
						if (isCanEditOrder) {
							new IndexTableItemEditor(
									columnTable,
									item,
									i,
									IndexTableItemEditor.COLUMN_EDITOR_TYPE_CCOMBO);
							return;
						}
						if (isCanEditPrefixLength) {
							new IndexTableItemEditor(
									columnTable,
									item,
									i,
									IndexTableItemEditor.COLUMN_EDITOR_TYPE_TEXT);
						}
					}
				}
			}
		});
	}

	/**
	 * Set button status
	 */
	private void setBtnEnable() {
		if (columnTable.getSelectionCount() > 0) {
			downBTN.setEnabled(true);
			upBTN.setEnabled(true);
		} else {
			downBTN.setEnabled(false);
			upBTN.setEnabled(false);
		}

		if (columnTable.getSelectionIndex() <= 0) {
			upBTN.setEnabled(false);
		}

		if (columnTable.getSelectionIndex() >= columnTable.getItemCount() - 1) {
			downBTN.setEnabled(false);
		}
	}

	/**
	 * Set hint message
	 */
	private void setHintMessage() {
		TableItem[] items = columnTable.getItems();
		for(TableItem item : items){
			if(item != null && item.getChecked()){
				setErrorMessage(null);
				if (editedIndex == null) {
					setMessage(Messages.msgAddIndex);
				} else {
					setMessage(Messages.msgEditIndex);
				}
				return;
			}
		}
		setErrorMessage(Messages.errSelectMoreColumns);
	}
	
	public Constraint getIndexConstraint() {
		return indexConstraint;
	}
}