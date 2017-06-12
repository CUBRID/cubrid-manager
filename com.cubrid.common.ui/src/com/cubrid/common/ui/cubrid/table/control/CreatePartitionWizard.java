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
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;

import com.cubrid.common.core.common.model.PartitionInfo;
import com.cubrid.common.core.common.model.PartitionType;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;

/**
 *
 * Create partition Wizard
 *
 * @author pangqiren
 * @version 1.0 - 2010-3-15 created by pangqiren
 */
public class CreatePartitionWizard extends
		Wizard {

	private final DatabaseInfo dbInfo;
	private final SchemaInfo schemaInfo;
	private final List<PartitionInfo> partitionInfoList;
	private final boolean isNewTable;

	private PartitionTypePage typePage = null;
	private PartitionEditHashPage hashPage = null;
	private PartitionEditListPage listPage = null;
	private PartitionEditRangePage rangePage = null;

	private PartitionInfo editedPartitionInfo = null;

	public CreatePartitionWizard(DatabaseInfo dbInfo, SchemaInfo schemaInfo,
			List<PartitionInfo> partList, boolean isNewTable,
			PartitionInfo editedPartitionInfo) {
		this.dbInfo = dbInfo;
		this.schemaInfo = schemaInfo;
		this.partitionInfoList = partList;
		this.isNewTable = isNewTable;
		if (editedPartitionInfo == null) {
			setWindowTitle(Messages.titleAddPartition);
		} else {
			this.editedPartitionInfo = editedPartitionInfo;
			setWindowTitle(Messages.titleEditPartition);
		}
	}

	/**
	 * Add wizard pages
	 */
	public void addPages() {
		if (partitionInfoList.isEmpty()) {
			typePage = new PartitionTypePage(dbInfo, schemaInfo, isNewTable);
			addPage(typePage);

			hashPage = new PartitionEditHashPage(partitionInfoList);
			addPage(hashPage);

			listPage = new PartitionEditListPage(dbInfo, schemaInfo,
					partitionInfoList, isNewTable);
			addPage(listPage);

			rangePage = new PartitionEditRangePage(dbInfo, partitionInfoList);
			addPage(rangePage);

			WizardDialog dialog = (WizardDialog) getContainer();
			dialog.addPageChangedListener(hashPage);
			dialog.addPageChangedListener(listPage);
			dialog.addPageChangedListener(rangePage);
		} else {
			if (editedPartitionInfo != null) {
				typePage = new PartitionTypePage(dbInfo, schemaInfo, isNewTable);
				typePage.setEditedPartitionInfo(editedPartitionInfo);
				addPage(typePage);
			}
			WizardDialog dialog = (WizardDialog) getContainer();
			PartitionType partitionType = partitionInfoList.get(0).getPartitionType();
			if (partitionType == PartitionType.HASH) {
				hashPage = new PartitionEditHashPage(partitionInfoList);
				hashPage.setEditedPartitionInfo(editedPartitionInfo);
				addPage(hashPage);
				if (typePage != null) {
					dialog.addPageChangedListener(hashPage);
				}
			} else if (partitionType == PartitionType.LIST) {
				listPage = new PartitionEditListPage(dbInfo, schemaInfo,
						partitionInfoList, isNewTable);
				listPage.setEditedPartitionInfo(editedPartitionInfo);
				addPage(listPage);
				if (typePage != null) {
					dialog.addPageChangedListener(listPage);
				}
			} else {
				rangePage = new PartitionEditRangePage(dbInfo, partitionInfoList);
				rangePage.setEditedPartitionInfo(editedPartitionInfo);
				addPage(rangePage);
				if (typePage != null) {
					dialog.addPageChangedListener(rangePage);
				}
			}
		}

	}

	/**
	 * Get the next page
	 *
	 * @param page the current page
	 * @return the next page
	 */
	public IWizardPage getNextPage(IWizardPage page) {
		if (partitionInfoList.isEmpty()) {
			PartitionType partitionType = PartitionType.valueOf(typePage.getPartitionType());
			if (page instanceof PartitionTypePage
					&& getContainer().getCurrentPage() == typePage) {
				if (partitionType == PartitionType.HASH) {
					return hashPage;
				} else if (partitionType == PartitionType.LIST) {
					return listPage;
				} else {
					return rangePage;
				}
			}
			return null;
		} else {
			return super.getNextPage(page);
		}
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#canFinish()
	 * @return boolean
	 */
	public boolean canFinish() {
		if (getContainer().getCurrentPage() == listPage) {
			return listPage.isCanFinished();
		}
		if (getContainer().getCurrentPage() == rangePage) {
			return rangePage.isCanFinished();
		}
		if (getContainer().getCurrentPage() == hashPage) {
			return hashPage.isCanFinished();
		}
		return false;
	}

	/**
	 * Perform finish
	 *
	 * @return <code>true</code> if successfully;<code>false</code>otherwise
	 */
	public boolean performFinish() { // FIXME move this logic to core module
		PartitionType partitionType = null;
		String expr = null;

		if (partitionInfoList.isEmpty()) {
			partitionType = PartitionType.valueOf(typePage.getPartitionType());
			expr = typePage.getPartitionExpr();
		} else {
			partitionType = partitionInfoList.get(0).getPartitionType();
			if (this.editedPartitionInfo == null) {
				expr = partitionInfoList.get(0).getPartitionExpr();
			} else {
				expr = typePage.getPartitionExpr();
			}
		}

		if (partitionType == PartitionType.HASH) {
			String partitionNum = hashPage.getNumberOfPartitions();
			if (this.editedPartitionInfo != null) {
				if (partitionInfoList.size() == Integer.parseInt(partitionNum)) {
					return true;
				} else {
					partitionInfoList.clear();
				}
			}
			for (int i = 0; i < Integer.parseInt(partitionNum); i++) {
				PartitionInfo partitonInfo = new PartitionInfo(
						schemaInfo.getClassname(), "p" + i, partitionType,
						expr, null, -1);
				partitionInfoList.add(partitonInfo);
			}
		} else if (partitionType == PartitionType.LIST) {
			String partitionName = listPage.getPartitionName();
			String exprDataType = listPage.getPartitionExprDataType();
			String partitionDescription = listPage.getPartitionDescription();
			List<String> valuesList = listPage.getListValues();
			if (this.editedPartitionInfo == null) {
				PartitionInfo partitonInfo = new PartitionInfo(
						schemaInfo.getClassname(), partitionName,
						partitionType, expr, valuesList, -1);
				partitonInfo.setPartitionExprType(exprDataType);
				partitonInfo.setDescription(partitionDescription);
				partitionInfoList.add(partitonInfo);
			} else {
				editedPartitionInfo.setPartitionName(partitionName);
				editedPartitionInfo.setPartitionExprType(exprDataType);
				editedPartitionInfo.setDescription(partitionDescription);
				if (isChangeListValues(
						editedPartitionInfo.getPartitionValues(), valuesList)) {
					editedPartitionInfo.setPartitionValues(valuesList);
					editedPartitionInfo.setRows(-1);
				}
				if (!expr.equals(editedPartitionInfo.getPartitionExpr())) {
					changePartitionExpr(expr);
				}
				changePartitionExprDataType(exprDataType);
			}
		} else {
			String partitionName = rangePage.getPartitionName();
			String exprDataType = rangePage.getPartitionExprDataType();
			String newValue = rangePage.getRangeValue();
			String partitionDescription = rangePage.getPartitionDescription();
			if (this.editedPartitionInfo == null) {
				List<String> rangeList = new ArrayList<String>();
				rangeList.add(null);
				if ("MAXVALUE".equals(newValue)) {
					rangeList.add(null);
				} else {
					rangeList.add(newValue);
				}
				PartitionInfo partitonInfo = new PartitionInfo(
						schemaInfo.getClassname(), partitionName,
						partitionType, expr, rangeList, -1);
				partitonInfo.setPartitionExprType(exprDataType);
				partitonInfo.setDescription(partitionDescription);
				partitionInfoList.add(partitonInfo);
				resetRangePartitionInfoList(partitionInfoList);
			} else {
				editedPartitionInfo.setPartitionExprType(exprDataType);
				changePartitionExprDataType(exprDataType);

				if (!partitionName.equals(editedPartitionInfo.getPartitionName())) {
					editedPartitionInfo.setPartitionName(partitionName);
				}
				if (!expr.equals(editedPartitionInfo.getPartitionExpr())) {
					changePartitionExpr(expr);
				}
				if (StringUtil.isNotEmpty(partitionDescription) &&
						!partitionDescription.equals(editedPartitionInfo.getDescription())) {
					editedPartitionInfo.setDescription(partitionDescription);
				}
				String oldValue = editedPartitionInfo.getPartitionValues().get(
						1);
				if ("MAXVALUE".equals(newValue)) {
					newValue = null;
				}
				RangePartitionComparator comparator = new RangePartitionComparator(
						exprDataType);
				if (comparator.compareData(newValue, oldValue) != 0) {
					String preValue = editedPartitionInfo.getPartitionValues().get(
							0);
					editedPartitionInfo.getPartitionValues().clear();
					editedPartitionInfo.getPartitionValues().add(preValue);
					editedPartitionInfo.getPartitionValues().add(newValue);
					editedPartitionInfo.setRows(-1);
					resetRangePartitionInfoList(partitionInfoList);
				}
			}
			Collections.sort(partitionInfoList, new RangePartitionComparator(
					exprDataType));
		}
		return true;
	}

	/**
	 *
	 * Reset the range value for every partition,sort by partition value
	 *
	 * @param partitionInfoList Partition List
	 */
	public static void resetRangePartitionInfoList(
			List<PartitionInfo> partitionInfoList) { // FIXME move this logic to core module
		if (partitionInfoList.isEmpty()) {
			return;
		}
		RangePartitionComparator comparator = new RangePartitionComparator(
				partitionInfoList.get(0).getPartitionExprType());
		Collections.sort(partitionInfoList, comparator);

		String preValue = partitionInfoList.get(0).getPartitionValues().get(0);
		if (preValue != null) {
			partitionInfoList.get(0).getPartitionValues().remove(0);
			partitionInfoList.get(0).getPartitionValues().add(0, null);
		}
		String afterValue = partitionInfoList.get(0).getPartitionValues().get(1);
		for (int i = 1; i < partitionInfoList.size(); i++) {
			PartitionInfo info = partitionInfoList.get(i);
			preValue = info.getPartitionValues().get(0);
			if (comparator.compareData(preValue, afterValue) != 0) {
				info.getPartitionValues().remove(0);
				info.getPartitionValues().add(0, afterValue);
				info.setRows(-1);
			}
			afterValue = info.getPartitionValues().get(1);
		}
	}

	/**
	 *
	 * Change all partition information expression
	 *
	 * @param expr the partition expression
	 */
	private void changePartitionExpr(String expr) { // FIXME move this logic to core module
		for (int i = 0; i < partitionInfoList.size(); i++) {
			PartitionInfo info = partitionInfoList.get(i);
			info.setPartitionExpr(expr);
			info.setRows(-1);
		}
	}

	/**
	 *
	 * Change all partition expression data type
	 *
	 * @param type the partition expression
	 */
	private void changePartitionExprDataType(String type) { // FIXME move this logic to core module
		for (int i = 0; i < partitionInfoList.size(); i++) {
			PartitionInfo info = partitionInfoList.get(i);
			info.setPartitionExprType(type);
		}
	}

	/**
	 *
	 * Check whether equal
	 *
	 * @param oldValuesList the value list
	 * @param newValuesList the value list
	 * @return <code>true</code> if equal;otherwise <code>false</code>
	 */
	private boolean isChangeListValues(List<String> oldValuesList,
			List<String> newValuesList) { // FIXME move this logic to core module
		if (oldValuesList == null || oldValuesList.isEmpty()) {
			if (newValuesList == null || newValuesList.isEmpty()) {
				return false;
			}
			return true;
		} else {
			if (newValuesList == null || newValuesList.isEmpty()) {
				return true;
			}
			if (oldValuesList.size() != newValuesList.size()) {
				return true;
			}
			for (int i = 0; i < oldValuesList.size(); i++) {
				String oldInfo = oldValuesList.get(i);
				boolean isEqual = false;
				for (int j = 0; j < newValuesList.size(); j++) {
					String newInfo = newValuesList.get(j);
					if (oldInfo.equals(newInfo)) {
						isEqual = true;
						break;
					}
				}
				if (!isEqual) {
					return true;
				}
			}
		}
		return false;
	}
}
