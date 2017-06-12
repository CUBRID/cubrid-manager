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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.cubrid.common.core.common.model.PartitionInfo;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.PartitionUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.FieldHandlerUtils;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;

/**
 * 
 * Range Partition Edit Page
 * 
 * @author pangqiren
 * @version 1.0 - 2010-3-15 created by pangqiren
 */
public class PartitionEditRangePage extends
		WizardPage implements
		ModifyListener,
		IPageChangedListener {

	public final static String PAGENAME = "CreatePartitionWizard/PartitionEditRangePage";

	private final List<PartitionInfo> partitionInfoList;
	private boolean isCanFinished = false;
	private boolean isCommentSupport = false;
	private PartitionInfo editedPartitionInfo = null;

	private Text partitionNameText;
	private Text partitionTypeText;
	private Text partitionRangeText;
	private Text partitionExprText;
	private Text partitionDescriptionText;
	private Button maxValueButton;
	private Combo partitionExprTypeCombo;

	protected PartitionEditRangePage(DatabaseInfo dbInfo, List<PartitionInfo> partitionInfoList) {
		super(PAGENAME);
		this.partitionInfoList = partitionInfoList;
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

		createRangePartitionGroup(composite);
		init();
		setTitle(Messages.titleRangePage);
		setMessage(Messages.msgRangePage);
		setControl(composite);
	}

	/**
	 * create hash partition
	 * 
	 * @param parent Composite
	 */
	private void createRangePartitionGroup(Composite parent) {
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
		layout.numColumns = 3;
		composite.setLayout(layout);

		Label partitionNameLabel = new Label(composite, SWT.NONE);
		partitionNameLabel.setText(Messages.lblPartitionName);
		partitionNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1,
				-1));

		partitionNameText = new Text(composite, SWT.BORDER);
		partitionNameText.setTextLimit(ValidateUtil.MAX_SCHEMA_NAME_LENGTH);
		partitionNameText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, -1, -1));

		if (isCommentSupport) {
			Label partitionDescriptionLabel = new Label(composite, SWT.NONE);
			partitionDescriptionLabel.setText(Messages.lblPartitionDescription);
			partitionDescriptionLabel.setLayoutData(
					CommonUITool.createGridData(1, 1, -1, -1));

			partitionDescriptionText = new Text(composite, SWT.BORDER);
			partitionDescriptionText.setTextLimit(ValidateUtil.MAX_DB_OBJECT_COMMENT);
			partitionDescriptionText.setLayoutData(
					CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
		}

		Label partitionTypeLabel = new Label(composite, SWT.NONE);
		partitionTypeLabel.setText(Messages.lblPartitionType);
		partitionTypeLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1,
				-1));

		partitionTypeText = new Text(composite, SWT.BORDER);
		partitionTypeText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
		partitionTypeText.setEnabled(false);

		Label partitionExprLabel = new Label(composite, SWT.NONE);
		partitionExprLabel.setText(Messages.lblPartitionExpr);
		partitionExprLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1,
				-1));

		partitionExprText = new Text(composite, SWT.BORDER);
		partitionExprText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
		partitionExprText.setEnabled(false);

		Label exprTypeLabel = new Label(composite, SWT.NONE);
		exprTypeLabel.setText(Messages.lblExprDataType);
		exprTypeLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

		partitionExprTypeCombo = new Combo(composite, SWT.READ_ONLY);
		partitionExprTypeCombo.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, -1, -1));

		final Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.lblRange);
		label.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

		partitionRangeText = new Text(composite, SWT.BORDER);
		partitionRangeText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

		maxValueButton = new Button(composite, SWT.CHECK);
		maxValueButton.setText(Messages.btnMaxValue);
		maxValueButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		maxValueButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (maxValueButton.getSelection()) {
					partitionRangeText.setText("");
					partitionRangeText.setEnabled(false);
				} else {
					partitionRangeText.setText("");
					partitionRangeText.setEnabled(true);
				}
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
		}
		if (editedPartitionInfo != null) {	// edit partition
			partitionNameText.setText(editedPartitionInfo.getPartitionName());
			String str = editedPartitionInfo.getPartitionValues().get(1);
			if (str == null) {
				maxValueButton.setSelection(true);
				partitionRangeText.setEnabled(false);
			} else {
				partitionRangeText.setText(str);
			}
			String description = editedPartitionInfo.getDescription();
			if (StringUtil.isNotEmpty(description)) {
				partitionDescriptionText.setText(description);
			}
		}
		partitionNameText.addModifyListener(this);
		partitionRangeText.addModifyListener(this);
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
			setPageComplete(validate());
			partitionNameText.setFocus();
		}
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

		if (maxValueButton.getSelection()) {
			String value = getMaxRangeValue();
			if (value != null
					&& value.equals("MAXVALUE")
					&& (this.editedPartitionInfo == null || editedPartitionInfo.getPartitionValues().get(
							1) != null)) {
				setErrorMessage(Messages.errMaxValueExist);
				isCanFinished = false;
				return false;
			}
		} else {
			String newValue = partitionRangeText.getText().trim();
			if (newValue.length() == 0) {
				setErrorMessage(Messages.errNoRangeValue);
				isCanFinished = false;
				return false;
			}
			if (isExistRangeValue(newValue)) {
				setErrorMessage(Messages.errRangeValueExist);
				isCanFinished = false;
				return false;
			}
			String exprDataType = partitionExprTypeCombo.getText();
			String resultMsg = FieldHandlerUtils.isValidData(exprDataType,
					newValue);
			if (StringUtil.isNotEmpty(resultMsg)) {
				setErrorMessage(resultMsg);
				isCanFinished = false;
				return false;
			}

			if (!checkValueRange(exprDataType, newValue)) {
				isCanFinished = false;
				return false;
			}
		}
		isCanFinished = true;
		setErrorMessage(null);
		return true;
	}

	public boolean isCanFinished() {
		return this.isCanFinished;
	}

	/**
	 * 
	 * Get range value
	 * 
	 * @return the string
	 */
	public String getRangeValue() {
		if (maxValueButton.getSelection()) {
			return "MAXVALUE";
		} else {
			return partitionRangeText.getText().trim();
		}
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
	 * Get the max range value
	 * 
	 * @return the string
	 */
	private String getMaxRangeValue() {
		if (partitionInfoList.isEmpty()) {
			return null;
		} else {
			String maxValue = partitionInfoList.get(
					partitionInfoList.size() - 1).getPartitionValues().get(1);
			if (maxValue == null) {
				return "MAXVALUE";
			} else {
				return maxValue;
			}
		}
	}

	/**
	 * 
	 * Check the range value whether exist
	 * 
	 * @param value the range value
	 * @return <code>true</code> if exist;otherwise <code>false</code>
	 */
	private boolean isExistRangeValue(String value) {
		if (partitionInfoList.isEmpty()) {
			return false;
		} else {
			String editedValue = null;
			if (this.editedPartitionInfo != null) {
				editedValue = editedPartitionInfo.getPartitionValues().get(1);
			}
			RangePartitionComparator comparator = new RangePartitionComparator(
					partitionExprTypeCombo.getText());
			for (int i = 0; i < partitionInfoList.size(); i++) {
				PartitionInfo info = partitionInfoList.get(i);
				String str = info.getPartitionValues().get(1);
				if (comparator.compareData(str, value) == 0
						&& (editedValue == null || comparator.compareData(
								editedValue, value) != 0)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check the value range
	 * 
	 * @param type
	 * @param value
	 * @return
	 */
	private boolean checkValueRange(String type, String value) {
		setErrorMessage(null);
		
		if (DataType.DATATYPE_SMALLINT.equalsIgnoreCase(type)
				|| DataType.DATATYPE_INTEGER.equalsIgnoreCase(type)
				|| DataType.DATATYPE_INT.equalsIgnoreCase(type)
				|| DataType.DATATYPE_BIGINT.equalsIgnoreCase(type)) {
			long rangeValue = 0;
			boolean isValidate = true;
			try {
				rangeValue = Long.parseLong(value);
			} catch (NumberFormatException ex) {
				isValidate = false;
			}
			if (!isValidate) {
				setErrorMessage(Messages.errInvalidRangeValue);
				return false;
			}

			if (DataType.DATATYPE_SMALLINT.equalsIgnoreCase(type)) {
				if (rangeValue < DataType.SMALLINT_MIN_VALUE
						|| rangeValue > DataType.SMALLINT_MAX_VALUE) {
					setErrorMessage(Messages.bind(Messages.errRangeInvalidate,
							DataType.SMALLINT_MIN_VALUE,
							DataType.SMALLINT_MAX_VALUE));
					return false;
				}
			} else if (DataType.DATATYPE_INTEGER.equalsIgnoreCase(type)
					|| DataType.DATATYPE_INT.equalsIgnoreCase(type)) {
				if (rangeValue < DataType.INT_MIN_VALUE
						|| rangeValue > DataType.INT_MAX_VALUE) {
					setErrorMessage(Messages.bind(Messages.errRangeInvalidate,
							DataType.INT_MIN_VALUE, DataType.INT_MAX_VALUE));
					return false;
				}
			} else if (DataType.DATATYPE_BIGINT.equalsIgnoreCase(type)) {
				if (rangeValue < DataType.BIGINT_MIN_VALUE
						|| rangeValue > DataType.BIGINT_MAX_VALUE) {
					setErrorMessage(Messages.bind(Messages.errRangeInvalidate,
							DataType.BIGINT_MIN_VALUE,
							DataType.BIGINT_MAX_VALUE));
					return false;
				}
			}
		}

		return true;
	}

	public void setEditedPartitionInfo(PartitionInfo editedPartitionInfo) {
		this.editedPartitionInfo = editedPartitionInfo;
	}
}
