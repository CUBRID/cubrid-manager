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

import java.util.List;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.cubrid.common.core.common.model.Constraint;
import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.common.model.SerialInfo;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.cubrid.table.TableEditorAdaptor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;

public class AttributeCellModifier implements
		ICellModifier {
	private TableEditorAdaptor editor;

	public AttributeCellModifier(TableEditorAdaptor editor) {
		super();
		this.editor = editor;
	}

	public boolean canModify(Object element, String property) { // FIXME move this logic to core module
		DBAttribute attr = (DBAttribute) element;
		if (!editor.isNewTableFlag()
				&& !editor.isSupportChange()
				&& editor.getOldSchemaInfo().getDBAttributeByName(
						attr.getName(), false) != null) {
			return false;
		}

		if (StringUtil.isEqual(property, IAttributeColumn.COL_AUTO_INCREMENT)) {
			DBAttribute aiAttr = editor.getNewSchemaInfo().getAutoIncrementColumn();
			if (aiAttr != null && aiAttr != attr) {
				return false;
			} else if (!DataType.isIntegerType(attr.getType())) {
				return false;
			} else if (attr != null && StringUtil.isNotEmpty(attr.getDefault())) {
				CommonUITool.openErrorBox(Messages.errCanNotSetAIOnDefault);
				return false;
			}
		} else if (StringUtil.isEqual(property, IAttributeColumn.COL_NOT_NULL) || StringUtil.isEqual(property, IAttributeColumn.COL_UK)) {
			Constraint pk = editor.getNewSchemaInfo().getPK();
			if (pk != null && pk.getAttributes().contains(attr.getName())) {
				return false;
			}
		} else if (StringUtil.isEqual(property, IAttributeColumn.COL_COLLATION)) {
			if (attr.isNew() && DataType.canUseCollation(attr.getType())) {
				return true;
			} else {
				return false;
			}
		} else if (StringUtil.isEqual(property, IAttributeColumn.COL_MEMO)) {
			return editor.isSupportTableComment();
		}

		return true;
	}

	public Object getValue(Object element, String property) { // FIXME move this logic to core module
		DBAttribute attr = (DBAttribute) element;
		if (StringUtil.isEqual(property, IAttributeColumn.COL_PK)) {
			SchemaInfo schemaInfo = editor.getNewSchemaInfo();
			if (schemaInfo == null) {
				return false;
			}

			Constraint constraint = schemaInfo.getPK();
			if (constraint == null) {
				return false;
			}

			List<String> columns = constraint.getAttributes();
			if (columns == null || columns.size() == 0) {
				return false;
			}

			return columns.contains(attr.getName());
		} else if (StringUtil.isEqual(property, IAttributeColumn.COL_NAME)) {
			return attr.getName();
		} else if (StringUtil.isEqual(property, IAttributeColumn.COL_DATATYPE)) {

			String dataType = attr.getType();
			if (dataType == null) {
				return "";
			}
			String physicalType = getShownPhysicalType(attr);
			return physicalType;
		} else if (StringUtil.isEqual(property, IAttributeColumn.COL_DEFAULT)) {
			String defaultValue = attr.getDefault();
			if (defaultValue == null
					|| attr.getAutoIncrement() != null
					|| (StringUtil.isEmpty(defaultValue) && !DataType.isStringType(attr.getType()))) {
				return DataType.NULL_EXPORT_FORMAT;
			} else {
				return defaultValue;
			}
		} else if (StringUtil.isEqual(property, IAttributeColumn.COL_AUTO_INCREMENT)) {
			DBAttribute aiAttr = editor.getNewSchemaInfo().getAutoIncrementColumn();
			if (aiAttr != null && aiAttr != attr) {
				CommonUITool.openErrorBox(Messages.errCanNotAddAutoincrementAlreadyExists);
				return "";
			}

			SerialInfo serial = attr.getAutoIncrement();
			if (serial == null) {
				return "";
			}

			String defaultValue = attr.getDefault();
			if (StringUtil.isNotEmpty(defaultValue)) {
				return "";
			}

			return serial.getTableAutoIncrementString();
		} else if (StringUtil.isEqual(property, IAttributeColumn.COL_NOT_NULL)) {
			return attr.isNotNull();
		} else if (StringUtil.isEqual(property, IAttributeColumn.COL_UK)) {
			return attr.isUnique();
		} else if (StringUtil.isEqual(property, IAttributeColumn.COL_SHARED)) {
			return attr.isShared();
		} else if (StringUtil.isEqual(property, IAttributeColumn.COL_COLLATION)) {
			if (DataType.canUseCollation(attr.getType())) {
				String collation = attr.getCollation();
				return editor.getCollationIndex(collation);
			}
			return "";
		} else if (StringUtil.isEqual(property, IAttributeColumn.COL_MEMO)) {
			return attr.getDescription();
		}

		return null;
	}

	private String getShownPhysicalType(DBAttribute attr) {
		String dataType = attr.getType();
		if (dataType.trim().toLowerCase().startsWith("enum")) {
			return DataType.getShownType(attr.getType()) + attr.getEnumeration();
		}
		return DataType.getShownType(attr.getType());
	}

	public void modify(Object element, String property, Object value) { // FIXME move this logic to core module
		final TableItem item = (TableItem) element;
		if (item == null) {
			return;
		}

		DBAttribute attr = (DBAttribute) item.getData();
		String attrName = attr.getName();
		DBAttribute oldAttribute = null;
		if (editor.getOldSchemaInfo() != null) {
			oldAttribute = editor.getOldSchemaInfo().getDBAttributeByName(
					attrName, false);
		}

		if (StringUtil.isEqual(property, IAttributeColumn.COL_PK)) {
			SchemaInfo schemaInfo = editor.getNewSchemaInfo();
			if (schemaInfo == null) {
				return;
			}

			boolean on = ((Boolean) value).booleanValue();
			if (on) {
				Constraint constraint = schemaInfo.getPK();
				if (constraint == null) {
					constraint = new Constraint("pk",
							Constraint.ConstraintType.PRIMARYKEY.getText());
					schemaInfo.addConstraint(constraint);
				}
				constraint.addAttribute(attr.getName());
			} else {
				Constraint constraint = schemaInfo.getPK();
				if (constraint == null) {
					return;
				}

				List<String> columns = constraint.getAttributes();
				if (columns == null || columns.size() == 0) {
					return;
				}

				boolean isContain = columns.remove(attr.getName());
				/*For bug TOOLS-3972 The collumn's setting in Edit Table Inconsistent with the setting in Set Primary Key*/
				if (isContain && columns.size() == 0) {
					schemaInfo.removeConstraintByName(constraint.getName(), constraint.getType());
				}
				/*For bug TOOLS-3046 : deal with edit column*/
				if (oldAttribute != null && isContain) {
					attr.setNotNull(false);
					editor.changeForEditElement(attrName, attr,
							oldAttribute);
				}
			}
			editor.makeChangeLogForIndex(attrName, attr, oldAttribute);
		} else if (StringUtil.isEqual(property, IAttributeColumn.COL_NAME)) {
			String newName = (String) value;
			SchemaInfo schemaInfo = editor.getNewSchemaInfo();
			if (schemaInfo == null) { // TODO Improve error message
				CommonUITool.openErrorBox(Messages.errEmptyNameOnEditTableColumn);
				return;
			}
			if (!StringUtil.isEmpty(newName) && !ValidateUtil.isValidIdentifier(newName)) {
				CommonUITool.openErrorBox(Messages.errColumnName);
				return;
			}
			List<DBAttribute> lastAttrs = schemaInfo.getAttributes();
			if (StringUtil.isEmpty(newName) && lastAttrs.contains(attr)) {
				CommonUITool.openErrorBox(Messages.errEmptyNameOnEditTableColumn);
				return;
			}
			for (DBAttribute lastAttr : lastAttrs) {
				if (StringUtil.isEqualIgnoreCase(lastAttr.getName(), newName) && attr != lastAttr) {
					CommonUITool.openErrorBox(Messages.errSameNameOnEditTableColumn);
					return;
				}
			}

			if (!StringUtil.isEqualIgnoreCase(attr.getName(), newName)) {
				replaceNewConstraintAttributeName(schemaInfo, attr.getName(), newName);

				DBAttribute newAttribute = attr.clone();
				if (newAttribute != null) {
					newAttribute.setName(newName);
				}
				if (attr != null) {
					attr.setName(newName);
				}

				if (!hasAddedToSchemaInfo(attr)) {
					attr.setName(newName);
					if (!StringUtil.isEmpty(newName)) {
						if (!lastAttrs.contains(newAttribute)) {
							newAttribute.setInherit(editor.getNewSchemaInfo().getClassname());
							schemaInfo.addAttribute(newAttribute);
							editor.removeElementByName(newName);
						}
						editor.addNewAttrLog(newName, newAttribute.isClassAttribute());
						if (!StringUtil.isEmpty(newAttribute.getName())) {
							editor.makeChangeLogForIndex(attrName, newAttribute, oldAttribute);
						}
					}
				} else {
					editor.changeForEditElement(attrName, newAttribute, oldAttribute);
				}
			}
			DBAttribute lastDBAttribute = null;
			if (lastAttrs.size() > 0) {
				Table columnsTable = editor.getColumnsTable();
				lastDBAttribute = (DBAttribute)columnsTable.getItem(columnsTable.getItemCount() -1).getData();
			}
			if (!StringUtil.isEmpty(newName)
					&& (lastDBAttribute == null ||!StringUtil.isEmpty(lastDBAttribute.getName()))) {
				editor.addNewColumn();
			}
		} else if (StringUtil.isEqual(property, IAttributeColumn.COL_DATATYPE)) {
			String dataTypeRaw = (String) value;
			if (dataTypeRaw != null && dataTypeRaw.trim().toLowerCase().startsWith("enum")) {
				int sp = dataTypeRaw.indexOf("(");
				if (sp != -1) {
					String dataType = dataTypeRaw.substring(0, sp).toLowerCase().trim();
					attr.setType(dataType);
					String enumeration = dataTypeRaw.substring(sp).trim();
					attr.setEnumeration(enumeration);
				}
			} else {
				attr.setType(dataTypeRaw);
			}
			if (!DataType.isIntegerType(attr.getType())) {
				attr.setAutoIncrement(null);
			}
			if (!DataType.canUseCollation(attr.getType())) {
				attr.setCollation("");
			}
			editor.changeForEditElement(attrName, attr, oldAttribute);
		} else if (StringUtil.isEqual(property, IAttributeColumn.COL_DEFAULT)) {
			String defaultVal = (String) value;
			boolean isStringType = DataType.isStringType(attr.getType());
			boolean isEmpty = StringUtil.isEmpty(defaultVal);
			boolean isNull = false;
			if (defaultVal == null
					|| DataType.NULL_EXPORT_FORMAT.equals(defaultVal)
					|| DataType.VALUE_NULL.equals(defaultVal)
					|| (isEmpty && !isStringType)) {
				isNull = true;
			}

			if (isNull) {
				attr.setDefault(null);
			} else {
				if (attr.getAutoIncrement() != null) {
					attr.setDefault(null);
					CommonUITool.openErrorBox(Messages.errCanNotSetDefaultOnAI);
					return;
				}
				boolean isConfirmReset = "".equals(defaultVal)
						&& oldAttribute != null
						&& !"".equals(oldAttribute.getDefault());
				if (isConfirmReset) {
					String confirmResetDef = Messages.confirmResetDef;
					if (CommonUITool.openConfirmBox(confirmResetDef)) {
						attr.setDefault(null);
					} else {
						attr.setDefault(defaultVal);
					}
				} else {
					attr.setDefault(defaultVal);
				}
			}
			editor.changeForEditElement(attrName, attr, oldAttribute);
		} else if (StringUtil.isEqual(property, IAttributeColumn.COL_AUTO_INCREMENT)) {
			DBAttribute aiAttr = editor.getNewSchemaInfo().getAutoIncrementColumn();
			if (aiAttr != null && aiAttr != attr) {
				attr.setAutoIncrement(null);
				return;
			}

			String param = (String) value;
			if (StringUtil.isNotEmpty(param)) {
				if (!param.matches("\\s*[0-9]+\\s*,\\s*[0-9]+\\s*")) {
					CommonUITool.openErrorBox(Messages.errInvalidAutoIncrForm);
					return;
				}

				String defaultValue = attr.getDefault();
				if (StringUtil.isNotEmpty(defaultValue)) {
					CommonUITool.openErrorBox(Messages.errCanNotSetAIOnDefault);
					return;
				}

				String[] params = param.split(",");
				String startVal = params[0].trim();
				String incrVal = params[1].trim();

				SchemaInfo schemaInfo = editor.getNewSchemaInfo();
				SerialInfo serial = new SerialInfo();
				serial.setOwner(schemaInfo.getOwner());
				serial.setClassName(schemaInfo.getClassname());
				serial.setAttName(attrName);
				serial.setCacheCount("1");
				serial.setCurrentValue(startVal);
				serial.setCyclic(false);
				serial.setIncrementValue(incrVal);
				serial.setMaxValue(String.valueOf(Integer.MAX_VALUE));
				serial.setMinValue(startVal);
				serial.setStartedValue(startVal);
				if (attr.getAutoIncrement() != null
						&& schemaInfo != null
						&& schemaInfo.getAutoIncrementColumn() != null
						&& schemaInfo.getAutoIncrementColumn().getAutoIncrement() != null) {
					String oldAI = attr.getAutoIncrement().getTableAutoIncrementString();
					String newAI = serial.getTableAutoIncrementString();
					if (StringUtil.isEqual(oldAI, newAI)) {
						return;
					}
				}
				attr.setAutoIncrement(serial);
			} else {
				attr.setAutoIncrement(null);
			}
			editor.changeForEditElement(attrName, attr, oldAttribute);
		} else if (StringUtil.isEqual(property, IAttributeColumn.COL_NOT_NULL)) {
			boolean on = ((Boolean) value).booleanValue();
			attr.setNotNull(on);
			editor.changeForEditElement(attrName, attr, oldAttribute);
		} else if (StringUtil.isEqual(property, IAttributeColumn.COL_UK)) {
			boolean on = ((Boolean) value).booleanValue();
			if (on && attr.isShared()) {
				CommonUITool.openErrorBox(Messages.errCanNotUseUkAndSharedOnce);
				return;
			}
			attr.setUnique(on);
			editor.changeForEditElement(attrName, attr, oldAttribute);
		} else if (StringUtil.isEqual(property, IAttributeColumn.COL_SHARED)) {
			boolean on = ((Boolean) value).booleanValue();
			String defaultValue = attr.getDefault();
			if (on && StringUtil.isEmpty(defaultValue)) {
				CommonUITool.openErrorBox(Messages.msgInputSharedValue);
				return;
			}
			if (on && attr.isUnique()) {
				CommonUITool.openErrorBox(Messages.errCanNotUseUkAndSharedOnce);
				return;
			}
			attr.setShared(on);
			editor.changeForEditElement(attrName, attr, oldAttribute);
		} else if (StringUtil.isEqual(property, IAttributeColumn.COL_COLLATION)) {
			String orignCollation = attr.getCollation();
			Integer selection = StringUtil.intValue(value.toString(), 0);
			if (selection > -1) {
				String newCollation = editor.getCollationArray()[selection];
				if (!StringUtil.isEqualNotIgnoreNull(orignCollation, newCollation)) {
					attr.setCollation(newCollation);
				}
			}
		} else if (StringUtil.isEqual(property, IAttributeColumn.COL_MEMO)) {
			attr.setDescription((String) value);
		}
		editor.loadColumnData();
	}

	/**
	 * Replace new constraint attribute name
	 */
	private void replaceNewConstraintAttributeName(SchemaInfo schemaInfo, String oldAttr,
			String newAttr) { // FIXME move this logic to core module
		for (Constraint constraint : schemaInfo.getConstraints()) {
			constraint.replaceAttribute(oldAttr, newAttr);
			constraint.replaceClassAttribute(oldAttr, newAttr);
			/*Replace the rule*/
			if (!Constraint.ConstraintType.FOREIGNKEY.equals(constraint.getType())) {
				List<String> rulesList = constraint.getRules();
				for (int i = 0; i < rulesList.size(); i++) {
					String rule = rulesList.get(i);
					int index = rule.indexOf(" ");
					String attrName = rule.substring(0, index);
					String rulePart = rule.substring(index);
					if (StringUtil.isEqualNotIgnoreNull(attrName, oldAttr)) {
						rulesList.set(i, newAttr + rulePart);
					}
				}
			}
		}
	}
	/**
	 * Judge the attribute is add to schemaInfo
	 *
	 * @param attribute
	 * @return
	 */
	private boolean hasAddedToSchemaInfo(DBAttribute attribute) { // FIXME move this logic to core module
		SchemaInfo schemaInfo = editor.getNewSchemaInfo();
		for(DBAttribute attr : schemaInfo.getAttributes()) {
			if(attr == attribute) {
				return true;
			}
		}
		return false;
	}
}
