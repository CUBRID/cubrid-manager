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
package com.cubrid.common.ui.cubrid.table.control;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.cubrid.common.core.common.model.PartitionInfo;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.PartitionUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.FieldHandlerUtils;
import com.cubrid.common.ui.spi.util.TableViewUtil;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetPartitionedClassListTask;

/**
 * 
 * List Partition Edit Page
 * 
 * @author pangqiren
 * @version 1.0 - 2010-3-15 created by pangqiren
 */
public class PartitionEditListPage extends
		WizardPage implements
		ModifyListener,
		IPageChangedListener {

	public final static String PAGENAME = "CreatePartitionWizard/PartitionEditListPage";

	private final DatabaseInfo dbInfo;
	private final SchemaInfo schemaInfo;
	private final boolean isNewTable;
	private PartitionInfo editedPartitionInfo = null;
	private Table listValueTable;
	private final List<PartitionInfo> partitionInfoList;
	private Text partitionTypeText;
	private Text partitionExprText;
	private Text partitionNameText;
	private Text partitionDescriptionText;
	private boolean isCanFinished = false;
	private boolean isCommentSupport = false;

	private Combo partitionValueCombo;

	private Combo partitionExprTypeCombo;

	protected PartitionEditListPage(DatabaseInfo dbInfo, SchemaInfo schemaInfo,
			List<PartitionInfo> partitionInfoList, boolean isNewTable) {
		super(PAGENAME);
		this.dbInfo = dbInfo;
		this.schemaInfo = schemaInfo;
		this.partitionInfoList = partitionInfoList;
		this.isNewTable = isNewTable;
		setPageComplete(false);
		isCommentSupport = CompatibleUtil.isCommentSupports(dbInfo);
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 * @param parent the parent composite
	 */
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 10;
		layout.marginWidth = 10;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		createPartitionInfoGroup(composite);
		createPartitionListValueGroup(composite);
		init();
		setTitle(Messages.titleListPage);
		setMessage(Messages.msgListPage);
		setControl(composite);
	}

	/**
	 * 
	 * Create partition information group
	 * 
	 * @param parent the parent composite
	 */
	private void createPartitionInfoGroup(Composite parent) {
		Group partitionInfoGroup = new Group(parent, SWT.NONE);
		partitionInfoGroup.setText(Messages.grpPartitionInfo);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		partitionInfoGroup.setLayoutData(gridData);
		GridLayout layout = new GridLayout();
		partitionInfoGroup.setLayout(layout);

		Composite composite = new Composite(partitionInfoGroup, SWT.NONE);
		composite.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
		layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		Label partitionNameLabel = new Label(composite, SWT.NONE);
		partitionNameLabel.setText(Messages.lblPartitionName);
		partitionNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

		partitionNameText = new Text(composite, SWT.BORDER);
		partitionNameText.setTextLimit(ValidateUtil.MAX_SCHEMA_NAME_LENGTH);
		partitionNameText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

		if (isCommentSupport) {
			Label partitionDescriptionLabel = new Label(composite, SWT.NONE);
			partitionDescriptionLabel.setText(Messages.lblPartitionDescription);
			partitionDescriptionLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

			partitionDescriptionText = new Text(composite, SWT.BORDER);
			partitionDescriptionText.setTextLimit(ValidateUtil.MAX_DB_OBJECT_COMMENT);
			partitionDescriptionText.setLayoutData(CommonUITool.createGridData(
					GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
		}

		Label partitionTypeLabel = new Label(composite, SWT.NONE);
		partitionTypeLabel.setText(Messages.lblPartitionType);
		partitionTypeLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

		partitionTypeText = new Text(composite, SWT.BORDER);
		partitionTypeText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
		partitionTypeText.setEnabled(false);

		Label partitionExprLabel = new Label(composite, SWT.NONE);
		partitionExprLabel.setText(Messages.lblPartitionExpr);
		partitionExprLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

		partitionExprText = new Text(composite, SWT.BORDER);
		partitionExprText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
		partitionExprText.setEnabled(false);

		Label exprTypeLabel = new Label(composite, SWT.NONE);
		exprTypeLabel.setText(Messages.lblExprDataType);
		exprTypeLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

		partitionExprTypeCombo = new Combo(composite, SWT.READ_ONLY);
		partitionExprTypeCombo.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

		partitionNameText.setFocus();
	}

	/**
	 * 
	 * Create the partition value list group
	 * 
	 * @param parent the parent composite
	 */
	private void createPartitionListValueGroup(Composite parent) {
		Group partitionListValueGroup = new Group(parent, SWT.NONE);
		partitionListValueGroup.setText(Messages.grpExpressionValue);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		partitionListValueGroup.setLayoutData(gridData);
		GridLayout layout = new GridLayout();
		partitionListValueGroup.setLayout(layout);

		Composite composite = new Composite(partitionListValueGroup, SWT.NONE);
		composite.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH,
				1, 1, -1, -1));
		layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		Label partitionValueLabel = new Label(composite, SWT.NONE);
		partitionValueLabel.setText(Messages.lblPartitionValue);
		partitionValueLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1,
				-1));

		partitionValueCombo = new Combo(composite, SWT.BORDER | SWT.MULTI);
		partitionValueCombo.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

		Button addButton = new Button(composite, SWT.NONE);
		addButton.setText(Messages.btnAdd);
		addButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				String value = partitionValueCombo.getText();
				if (!isValidData(value)) {
					partitionValueCombo.setFocus();
					return;
				}
				if (isValueExist(value)) {
					CommonUITool.openErrorBox(Messages.bind(
							Messages.errValueExist, value));
					partitionValueCombo.setText("");
					partitionValueCombo.setFocus();
					return;
				}
				new TableItem(listValueTable, SWT.NONE).setText(value);
				partitionValueCombo.setText("");
				setPageComplete(validate());
				partitionValueCombo.setFocus();
			}

			/**
			 * Check this value whether already exist
			 * 
			 * @param value the value
			 * @return <code>true</code> if exist;otherwise <code>false</code>
			 */
			private boolean isValueExist(String value) {
				for (int i = 0; i < listValueTable.getItemCount(); i++) {
					String val = listValueTable.getItem(i).getText();
					if (value.equals(val)) {
						return true;
					}
				}
				for (int i = 0; i < partitionInfoList.size(); i++) {
					List<String> valuesList = partitionInfoList.get(i).getPartitionValues();
					for (int j = 0; j < valuesList.size(); j++) {
						if (value.equals(valuesList.get(j))) {
							return true;
						}
					}
				}
				return false;
			}

			/**
			 * 
			 * Check this value whether valid
			 * 
			 * @param newValue The string
			 * @return boolean
			 */
			private boolean isValidData(String newValue) {
				String exprDataType = partitionExprTypeCombo.getText();
				String resultMsg = FieldHandlerUtils.isValidData(exprDataType,
						newValue);
				if (null == resultMsg) {
					return true;
				} else if (resultMsg.length() > 0) {
					CommonUITool.openErrorBox(resultMsg);
				}
				return false;
			}
		});

		Label listValueLabel = new Label(composite, SWT.NONE);
		listValueLabel.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 3, 1, -1, -1));
		listValueLabel.setText(Messages.lblExprValueInfo);

		listValueTable = new Table(composite, SWT.FULL_SELECTION | SWT.BORDER
				| SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		listValueTable.setLayout(TableViewUtil.createTableViewLayout(new int[]{100 }));
		listValueTable.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_BOTH, 3, 1, -1, 200));

		listValueTable.setHeaderVisible(true);
		listValueTable.setLinesVisible(false);
		
		TableViewUtil.createTableColumn(listValueTable, SWT.LEFT,
				Messages.tblColExprValue);

		Composite bottomComp = new Composite(composite, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		rowLayout.spacing = 5;
		bottomComp.setLayout(rowLayout);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		gridData.horizontalAlignment = GridData.END;
		bottomComp.setLayoutData(gridData);

		final Button deleteValueButton = new Button(bottomComp, SWT.NONE);
		deleteValueButton.setText(Messages.btnDel);
		deleteValueButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				int[] indices = listValueTable.getSelectionIndices();
				if (indices == null || indices.length == 0) {
					return;
				}
				listValueTable.remove(indices);
				setPageComplete(validate());
			}
		});
		deleteValueButton.setEnabled(false);
		listValueTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				int[] indices = listValueTable.getSelectionIndices();
				if (indices == null || indices.length == 0) {
					deleteValueButton.setEnabled(false);
					return;
				}
				deleteValueButton.setEnabled(true);
			}
		});

	}

	/**
	 * 
	 * Initial the page content
	 * 
	 */
	private void init() {
		partitionExprTypeCombo.setItems(PartitionUtil.getSupportedDateTypes());
		if (!partitionInfoList.isEmpty() && editedPartitionInfo == null) {	// create partition
			PartitionInfo partitonInfo = partitionInfoList.get(0);
			String partitionType = partitonInfo.getPartitionType().getText().toUpperCase();
			String partitionExpr = partitonInfo.getPartitionExpr();
			String exprDataType = partitonInfo.getPartitionExprType();
			if (exprDataType == null) {
				partitionExprTypeCombo.setEnabled(true);
				partitionExprTypeCombo.select(0);
			} else {
				partitionExprTypeCombo.setText(PartitionUtil.getMatchType(exprDataType));
				partitionExprTypeCombo.setEnabled(false);
			}
			partitionTypeText.setText(partitionType);
			partitionExprText.setText(partitionExpr);
			if (this.editedPartitionInfo == null) {
				initValuesCombo();
			}
		}
		if (editedPartitionInfo != null) {	// edit partition
			partitionNameText.setText(editedPartitionInfo.getPartitionName());
			String description = editedPartitionInfo.getDescription();
			if (StringUtil.isNotEmpty(description)) {
				partitionDescriptionText.setText(description);
			}
			for (int i = 0; i < editedPartitionInfo.getPartitionValues().size(); i++) {
				String value = editedPartitionInfo.getPartitionValues().get(i);
				new TableItem(listValueTable, SWT.NONE).setText(value);
			}
		}
		partitionNameText.addModifyListener(this);
		if (isCommentSupport) {
			partitionDescriptionText.addModifyListener(this);
		}
	}

	/**
	 * Call this method when page changed
	 * 
	 * @param event the page changed event
	 */
	public void pageChanged(PageChangedEvent event) {
		IWizardPage page = (IWizardPage) event.getSelectedPage();
		if (page.getName().equals(PAGENAME)) {
			PartitionTypePage partitionTypePage = (PartitionTypePage) getWizard().getPage(
					PartitionTypePage.PAGENAME);
			String partitionType = partitionTypePage.getPartitionType();
			String partitionExpr = partitionTypePage.getPartitionExpr();
			String exprDataType = partitionTypePage.getPartitionExprDataType();
			String partitionDescription = partitionTypePage.getDescription();
			if (exprDataType == null) {
				partitionExprTypeCombo.setEnabled(true);
				if (editedPartitionInfo == null) {
					partitionExprTypeCombo.select(0);
				} else {
					partitionExprTypeCombo.setText(PartitionUtil.getMatchType(editedPartitionInfo.getPartitionExprType()));
				}
			} else {
				partitionExprTypeCombo.setText(PartitionUtil.getMatchType(exprDataType));
				partitionExprTypeCombo.setEnabled(false);
			}
			partitionTypeText.setText(partitionType);
			partitionExprText.setText(partitionExpr);
			if (StringUtil.isNotEmpty(partitionDescription)) {
				partitionDescriptionText.setText(partitionDescription);
			}
			initValuesCombo();
			setPageComplete(validate());
			partitionNameText.setFocus();
		}
	}

	/**
	 * 
	 * Fill in the values into Combo
	 * 
	 */
	private void initValuesCombo() {
		if (isNewTable) {
			return;
		}
		GetPartitionedClassListTask task = new GetPartitionedClassListTask(
				dbInfo);
		String expr = partitionExprText.getText();
		String[] distinctValues = task.getDistinctValuesInAttribute(
				schemaInfo.getClassname(), expr);
		if (distinctValues == null || distinctValues.length <= 0) {
			return;
		}
		partitionValueCombo.setItems(distinctValues);
		if (distinctValues.length > 1024) {
			setMessage(Messages.bind(Messages.errValueTooMany,
					distinctValues.length));
		}
		partitionValueCombo.select(0);
	}

	/**
	 * When modify text and check the information validity
	 * 
	 * @param event the modify event
	 */
	public void modifyText(ModifyEvent event) {
		setPageComplete(validate());
	}

	/**
	 * 
	 * Check the page content validity
	 * 
	 * @return <code>true</code> if valid;otherwise <code>false</code>
	 */
	private boolean validate() {

		String partitionName = partitionNameText.getText().trim();
		if (partitionName.trim().length() == 0) {
			setErrorMessage(Messages.errNoPartitionName);
			isCanFinished = false;
			return false;
		}
		for (int i = 0; i < partitionInfoList.size(); i++) {
			if (partitionName.trim().equalsIgnoreCase(
					partitionInfoList.get(i).getPartitionName())) {
				if (this.editedPartitionInfo != null
						&& partitionName.trim().equalsIgnoreCase(
								editedPartitionInfo.getPartitionName())) {
					continue;
				}
				setErrorMessage(Messages.errPartitionNameExist);
				isCanFinished = false;
				return false;
			}
		}
		if (listValueTable.getItemCount() <= 0) {
			setErrorMessage(Messages.errNoExprValue);
			isCanFinished = false;
			return false;
		}
		isCanFinished = true;
		setErrorMessage(null);
		return true;
	}

	public boolean isCanFinished() {
		return this.isCanFinished;
	}

	public String getPartitionName() {
		return partitionNameText.getText().trim();
	}

	public String getPartitionExprDataType() {
		return partitionExprTypeCombo.getText();
	}

	public String getPartitionDescription() {
		return partitionDescriptionText.getText();
	}

	/**
	 * 
	 * Get values list
	 * 
	 * @return the values list
	 */
	public List<String> getListValues() {
		List<String> valuesList = new ArrayList<String>();
		for (int i = 0; i < listValueTable.getItemCount(); i++) {
			valuesList.add(listValueTable.getItem(i).getText(0));
		}
		return valuesList;
	}

	public void setEditedPartitionInfo(PartitionInfo editedPartitionInfo) {
		this.editedPartitionInfo = editedPartitionInfo;
	}
}
