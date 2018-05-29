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
package com.cubrid.cubridmanager.core.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.common.model.Trigger;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.DBClasses;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import com.cubrid.cubridmanager.core.cubrid.table.model.SuperClass;

/**
 * To parse TreeNode object and return all kinds of model object
 * 
 * @author moulinwang 2009-3-2
 */
public final class ModelUtil {
	private static final Logger LOGGER = LogUtil.getLogger(ModelUtil.class);

	private ModelUtil() {
	}

	/**
	 * This enum that indicates the attribute category
	 * 
	 * @author sq
	 * @version 1.0 - 2009-12-29 created by sq
	 */
	public enum AttributeCategory {
		INSTANCE("instance"), CLASS("class");
		String text;

		AttributeCategory(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}

		/**
		 * Get the instance of AttributeCategory whose text is equals the
		 * specific text
		 * 
		 * @param text String
		 * @return AttributeCategory
		 */
		public static AttributeCategory eval(String text) {
			AttributeCategory[] array = AttributeCategory.values();
			for (AttributeCategory a : array) {
				if (a.getText().equals(text)) {
					return a;
				}
			}
			return null;
		}
	}

	/**
	 * This enum indicates the status of trigger
	 * 
	 * @author sq
	 * @version 1.0 - 2009-12-29 created by sq
	 */
	public enum TriggerStatus {
		ACTIVE("ACTIVE"), INACTIVE("INACTIVE");
		String text;

		TriggerStatus(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}

		/**
		 * Get the instance of TriggerStatus whose text is equals the given text
		 * 
		 * @param text String
		 * @return TriggerStatus
		 */
		public static TriggerStatus eval(String text) {
			return TriggerStatus.valueOf(text);
		}
	}

	/**
	 * This enum indicates the eight event of trigger
	 * 
	 * @author sq
	 * @version 1.0 - 2009-12-29 created by sq
	 */
	public enum TriggerEvent {
		INSERT("INSERT"), UPDATE("UPDATE"), DELETE("DELETE"), STATEMENTINSERT(
				"STATEMENT INSERT"), STATEMENTUPDATE("STATEMENT UPDATE"), STATEMENTDELETE(
				"STATEMENT DELETE"), COMMIT("COMMIT"), ROLLBACK("ROLLBACK");

		String text;

		TriggerEvent(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}

		/**
		 * Get the instance of TriggerEvent whose text is equals the given text
		 * 
		 * @param text String
		 * @return TriggerEvent
		 */
		public static TriggerEvent eval(String text) {
			TriggerEvent[] array = TriggerEvent.values();
			for (TriggerEvent a : array) {
				if (a.getText().equals(text)) {
					return a;
				}
			}
			return null;
		}
	}

	/**
	 * This enum indicates the three condition times of trigger
	 * 
	 * @author sq
	 * @version 1.0 - 2009-12-29 created by sq
	 */
	public enum TriggerConditionTime {
		BEFORE("BEFORE"), AFTER("AFTER"), DEFERRED("DEFERRED");

		String text;

		TriggerConditionTime(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}

		/**
		 * Get the instance of TriggerConditionTime whose text is equals the
		 * given text
		 * 
		 * @param text String
		 * @return TriggerConditionTime
		 */
		public static TriggerConditionTime eval(String text) {
			return TriggerConditionTime.valueOf(text);
		}
	}

	/**
	 * This enum indicates the two action times of trigger
	 * 
	 * @author sq
	 * @version 1.0 - 2009-12-29 created by sq
	 */
	public enum TriggerActionTime {
		AFTER("AFTER"), DEFERRED("DEFERRED");

		String text;

		TriggerActionTime(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}

		/**
		 * Get the instance of TriggerActionTime whose text is equals the given
		 * text
		 * 
		 * @param text String
		 * @return TriggerActionTime
		 */
		public static TriggerActionTime eval(String text) {
			return TriggerActionTime.valueOf(text);
		}
	}

	/**
	 * This enum indicates the two class type
	 * 
	 * @author sq
	 * @version 1.0 - 2009-12-29 created by sq
	 */
	public enum ClassType {
		NORMAL("normal"), VIEW("view");

		String text;

		ClassType(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}

		/**
		 * Get the instance of ClassType whose text is equals the given text
		 * 
		 * @param text String
		 * @return ClassType
		 */
		public static ClassType eval(String text) {
			ClassType[] array = ClassType.values();
			for (ClassType a : array) {
				if (a.getText().equals(text)) {
					return a;
				}
			}
			return null;
		}
	}

	/**
	 * This enum indicates the two type of y or n
	 * 
	 * @author sq
	 * @version 1.0 - 2009-12-29 created by sq
	 */
	public enum YesNoType {
		Y("y"), N("n");

		String text = null;

		YesNoType(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}

		/**
		 * Get the instance of YesNoType whose text is equals the the given
		 * string
		 * 
		 * @param text String
		 * @return YesNoType
		 */
		public static YesNoType eval(String text) {
			YesNoType[] array = YesNoType.values();
			for (YesNoType a : array) {
				if (a.getText().equals(text)) {
					return a;
				}
			}
			return null;
		}
	}

	/**
	 * This enum indicates the four type kill tran
	 * 
	 * @author sq
	 * @version 1.0 - 2009-12-29 created by sq
	 */
	public enum KillTranType {
		//for [TOOLS-3354]/[TOOLS-3496], add [INDEX("i"), PROGRAM("p")] for CUBRID 9.2 and later version 
		T("t"), U("u"), H("h"), PG("pg"), INDEX("i"), PROGRAM("p");
		String text = null;

		KillTranType(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}

		/**
		 * Get the instance of KillTranType whose text is equals the the given
		 * string
		 * 
		 * @param text String
		 * @return KillTranType
		 */
		public static KillTranType eval(String text) {
			KillTranType[] array = KillTranType.values();
			for (KillTranType a : array) {
				if (a.getText().equals(text)) {
					return a;
				}
			}
			return null;
		}
	}

	/**
	 * Parse triggerlist message TreeNode and return Trigger Object
	 * 
	 * @param triggerlistnode TreeNode
	 * @return List<Trigger>
	 */
	public static List<Trigger> getTriggerList(TreeNode triggerlistnode) {
		List<TreeNode> nodelist = triggerlistnode.getChildren();
		List<Trigger> triggerlist = new ArrayList<Trigger>();
		if (nodelist == null) {
			return triggerlist;
		}
		for (TreeNode triggerinfo : nodelist) {
			Trigger trigger = new Trigger();
			SocketTask.setFieldValue(triggerinfo, trigger);
			correctTriggerValues(trigger);
			triggerlist.add(trigger);
		}
		return triggerlist;
	}
	
	/**
	 * correctTriggerValues
	 * @param trigger
	 */
	private static void correctTriggerValues(Trigger trigger) {
		String actionTime = trigger.getActionTime();
		String conditionTime = trigger.getConditionTime();
		
		if (conditionTime == null) {
			trigger.setConditionTime(actionTime);
			trigger.setActionTime("DEFAULT");
		}
	}

	/**
	 * Parse SchemaInfo message TreeNode and return SchemaInfo Object
	 * 
	 * @param classinfo TreeNode
	 * @return SchemaInfo
	 */
	public static SchemaInfo getSchemaInfo(TreeNode classinfo,
			DatabaseInfo dbInfo) {
		SchemaInfo schema = new SchemaInfo();
		SocketTask.setFieldValue(classinfo, schema);

		/*Process the type for CMS 9.0, If the CMS fixed the bug. It can be remove.*/
		if (CompatibleUtil.isSupportEnumVersion(dbInfo)) {
			List<String> enumColumnList = new ArrayList<String>();
			for (DBAttribute attribute : schema.getAttributes()) {
				String type = attribute.getType();
				type = processFiledTypeForCMS9(type);
				if (DataType.DATATYPE_ENUM.equalsIgnoreCase(type)) {
					enumColumnList.add(attribute.getName());
				}
				attribute.setType(type);
			}
			if (enumColumnList.size() > 0) {
				Connection connection = null;
				Statement stmt = null;
				ResultSet rs = null;
				try {
					connection = JDBCConnectionManager.getConnection(dbInfo, true);
					String escapedTableName = QuerySyntax.escapeKeyword(schema.getClassname());
					StringBuilder sb = new StringBuilder();
					sb.append("SHOW COLUMNS FROM ").append(escapedTableName).append(" WHERE FIELD IN (");
					stmt = connection.createStatement();
					for (int i = 0; i < enumColumnList.size(); i++) {
						sb.append("'").append(enumColumnList.get(i)).append("'");
						if (i + 1 < enumColumnList.size()) {
							sb.append(",");
						}
					}
					sb.append(");");
					rs = stmt.executeQuery(sb.toString());
					while (rs.next()) {
						String name = rs.getString("Field");
						String type = rs.getString("Type");

						DBAttribute attr = schema.getDBAttributeByName(name, false);
						attr.setEnumeration(StringUtil.getEnumeration(type));
					}
				} catch (Exception e) {
					LOGGER.error("", e);
				} finally {
					QueryUtil.freeQuery(connection, stmt, rs);
				}
			}
		}
		return schema;
	}

	/**
	 * Process the filed type for cms 9.0 .e.g integer(10) return integer
	 * 
	 * @param type
	 * @return
	 */
	private static String processFiledTypeForCMS9(String type) {
		if (!StringUtil.isEmpty(type)) {
			int bracketIndex = type.indexOf("(");
			if (bracketIndex > 0) {
				String baseType = type.substring(0, bracketIndex);
				if (DataType.isNotSupportSizeOrPrecision(baseType.toUpperCase())) {
					return baseType;
				}
			}
		}
		return type;
	}
	
	
	/**
	 * Parse SchemaInfo message TreeNode and return SchemaInfo Object
	 * 
	 * @param classesinfo TreeNode
	 * @return DBClasses
	 */
	public static DBClasses getClassList(TreeNode classesinfo) {
		DBClasses schema = new DBClasses();
		SocketTask.setFieldValue(classesinfo, schema);
		return schema;
	}

	/**
	 * Parse SuperClass message TreeNode and return SuperClass Object
	 * 
	 * @param superclassnode TreeNode
	 * @return SuperClass
	 */
	public static SuperClass getSuperClass(TreeNode superclassnode) {
		SuperClass superclass = new SuperClass();
		SocketTask.setFieldValue(superclassnode, superclass);
		return superclass;
	}
}
