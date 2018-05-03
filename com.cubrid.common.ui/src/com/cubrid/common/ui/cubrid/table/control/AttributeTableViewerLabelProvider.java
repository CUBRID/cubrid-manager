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
package com.cubrid.common.ui.cubrid.table.control;

import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.cubrid.common.core.common.model.Constraint;
import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.common.model.SerialInfo;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import com.cubrid.cubridmanager.core.cubrid.table.model.SuperClassUtil;

/**
 * Attribute Table Viewer Label Provider
 * 
 * @author robin 2009-6-4
 */
public class AttributeTableViewerLabelProvider implements ITableLabelProvider, ITableColorProvider {
	private final static Image PK_IMAGE = CommonUIPlugin.getImage("icons/primary_key.png");
	private final static Image CHECK_IMAGE = CommonUIPlugin.getImage("icons/checked.gif");
	private final static Image UNCHECK_IMAGE = CommonUIPlugin.getImage("icons/unchecked.gif");
	private final static Image DISABLED_CHECK_IMAGE = CommonUIPlugin.getImage("icons/disabled_checked.gif");
	private final static Color DISABLED_COLOR =  Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
	private SchemaInfo schema;
	private List<SchemaInfo> supers;
	private DatabaseInfo database;
	private boolean editableMode = false;

	public AttributeTableViewerLabelProvider(DatabaseInfo database, SchemaInfo schema, boolean editableMode) {
		this.schema = schema;
		this.database = database;
		this.editableMode = editableMode;
		this.supers = SuperClassUtil.getSuperClasses(database, schema);
	}

	public AttributeTableViewerLabelProvider(DatabaseInfo database, SchemaInfo schema) {
		this(database, schema, false);
	}

	public Image getColumnImage(Object element, int columnIndex) {
		if (element == null) {
			return null;
		}

		DBAttribute dbAttribute = (DBAttribute) element;
		if (dbAttribute == null || dbAttribute.getInherit() == null || schema == null) {
			return null;
		}

		switch (columnIndex) {
		// PK
		case 0: {
			String attrName = dbAttribute.getName();
			Constraint pk = schema.getPK(supers);
			if (null != pk && pk.getAttributes().contains(attrName)) {
				return PK_IMAGE;
			}
			return editableMode ? UNCHECK_IMAGE : null;
		}

		// NAME
		case 1:
			return null;

		// DATATYPE
		case 2:
			return null;

		// DEFAULT
		case 3:
			return null;

		// AUTO INCREMENT
		case 4:
//			SerialInfo autoIncrement = dbAttribute.getAutoIncrement();
//			if (null != autoIncrement) {
//				return CHECK_IMAGE;
//			}
			return null;

		// NOT NULL
		case 5: {
			String attrName = dbAttribute.getName();
			Constraint pk = schema.getPK(supers);
			if (null != pk && pk.getAttributes().contains(attrName)) {
				return editableMode ? CHECK_IMAGE : DISABLED_CHECK_IMAGE;
			}
			if (dbAttribute.isNotNull()) {
				return editableMode ? CHECK_IMAGE : DISABLED_CHECK_IMAGE;
			} else {
				return editableMode ? UNCHECK_IMAGE : null;
			}
		}

		// UK
		case 6: {
			String attrName = dbAttribute.getName();
			Constraint pk = schema.getPK(supers);
			if (null != pk && pk.getAttributes().contains(attrName)) {
				return DISABLED_CHECK_IMAGE;
			}
			if (dbAttribute.isUnique() && schema.isAttributeUnique(dbAttribute, supers)) {
				return editableMode ? UNCHECK_IMAGE : null;
			} else {
				return null;
			}
		}

		// SHARED
		case 7:
			if (dbAttribute.isShared()) {
				return editableMode ? CHECK_IMAGE : DISABLED_CHECK_IMAGE;
			} else {
				return editableMode ? UNCHECK_IMAGE : null;
			}

		// INHERIT
		case 8:
			return null;

		default:
			break;
		}

		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		if (element == null) {
			return null;
		}

		DBAttribute dbAttribute = (DBAttribute) element;
		if (dbAttribute == null || dbAttribute.getInherit() == null || schema == null) {
			return null;
		}

		switch (columnIndex) {
		// PK
		case 0:
			return null;
		// NAME
		case 1:
			return dbAttribute.getName();
		// DATATYPE
		case 2:
			if (DataType.DATATYPE_ENUM.equalsIgnoreCase(dbAttribute.getType())) {
				String type = StringUtil.toUpper(dbAttribute.getType()) + dbAttribute.getEnumeration();
				return DataType.getShownType(type);
			}
			return DataType.getShownType(dbAttribute.getType());
		// DEFAULT
		case 3:
			String defaultValue = dbAttribute.getDefault();
			if (defaultValue == null) {
				return "";
			}

			if (defaultValue.length() == 0 && DataType.isStringType(dbAttribute.getType())) {
				return "''";
			}
			return defaultValue;
		// AUTO INCREMENT
		case 4:
			SerialInfo serial = dbAttribute.getAutoIncrement();
			if (serial == null) {
				return "";
			}
			return serial.getMinValue() + "," + serial.getIncrementValue();
		// NOT NULL
		case 5:
			return null;
		// UK
		case 6:
			return null;
		// SHARED
		case 7:
			return null;
		// INHERIT
		case 8:
			return dbAttribute.getInherit();
		default:
			break;
		}
		return null;
	}

	public void addListener(ILabelProviderListener listener) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
	}

	public SchemaInfo getSchema() {
		return schema;
	}
	
	public void setSchema(SchemaInfo schema) {
		this.schema = schema;
		supers = SuperClassUtil.getSuperClasses(database, schema);
	}

	public void setDatabase(DatabaseInfo database) {
		this.database = database;
	}

	public Color getForeground(Object element, int columnIndex) {
		return null;
	}

	public Color getBackground(Object element, int columnIndex) {
		if (schema == null || element == null || !editableMode) {
			return null;
		}
		DBAttribute attr = (DBAttribute) element;
		if (columnIndex == 4) {
			DBAttribute aiAttr = schema.getAutoIncrementColumn();
			if (aiAttr != null && aiAttr != attr) {
				return DISABLED_COLOR;
			} else if (!DataType.isIntegerType(attr.getType())) {
				return DISABLED_COLOR;
			}
		}

		return null;
	}
}
