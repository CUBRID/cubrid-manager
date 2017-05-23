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

package com.cubrid.common.core.common.model;

import java.util.ArrayList;
import java.util.List;

import com.cubrid.common.core.util.ConstraintNamingUtil;
import com.cubrid.common.core.util.StringUtil;

/**
 *
 * This type indicates the parameters of constraint
 *
 * @author sq
 * @version 1.0 - 2009-12-28 created by sq
 */
public class Constraint implements
		Cloneable {
	private String name;
	private String type;
	private int keyCount;
	private List<String> classAttributes = null; // String
	private List<String> attributes = null; // String
	private List<String> rules = null; // String
	private String description;

	private boolean newFlag;

	/**
	 * if this constraint is FK type, return its referenced table; otherwise return
	 * null
	 *
	 * @return String The referenced table
	 */
	public String getReferencedTable() {
		if (getType().equals(ConstraintType.FOREIGNKEY.getText())) {
			List<String> rules = getRules();
			for (String rule : rules) {
				if (rule.startsWith("REFERENCES ")) {
					return rule.replace("REFERENCES ", "");
				}
			}
		}
		return null;
	}

	/**
	 * Update the foreign table name in the constraint.
	 *
	 * @param oldName
	 * @param newName
	 */
	public void updateReferenceName(String oldName, String newName) {
		String oleRule = "REFERENCES " + oldName;
		String newRule = "REFERENCES " + newName;
		if (rules != null && getType().equals(ConstraintType.FOREIGNKEY.getText())) {
			for (int i = 0; i < rules.size(); i++) {
				if (rules.get(i).equals(oleRule)) {
					rules.remove(i);
					rules.add(i, newRule);
				}
			}
		}
	}

	/**
	 * Whether the constraint contains the attribute name in attributes or
	 * classAttributes
	 *
	 * @param attrName
	 * @param isClass if true, find in classAttributes
	 * @return
	 */
	public boolean contains(String attrName, boolean isClass) {
		if (isClass) {
			return classAttributes == null ? false
					: classAttributes.contains(attrName);
		}
		return attributes == null ? false : attributes.contains(attrName);
	}

	/**
	 * return the system default name for constraint when creating this
	 * constraint without constraint name
	 *
	 * @param tableName String
	 * @return String The default name
	 */
	public String getDefaultName(String tableName) {
		if (ConstraintType.PRIMARYKEY.getText().equals(type)) {
			return ConstraintNamingUtil.getPKName(tableName, getAttributes());
		} else if (ConstraintType.FOREIGNKEY.getText().equals(type)) {
			return ConstraintNamingUtil.getFKName(tableName, getAttributes());
		} else if (ConstraintType.INDEX.getText().equals(type)) {
			return ConstraintNamingUtil.getIndexName(tableName, getRules());
		} else if (ConstraintType.REVERSEINDEX.getText().equals(type)) {
			return ConstraintNamingUtil.getReverseIndexName(tableName,
					getAttributes());
		} else if (ConstraintType.UNIQUE.getText().equals(type)) {
			return ConstraintNamingUtil.getUniqueName(tableName, getRules());
		} else if (ConstraintType.REVERSEUNIQUE.getText().equals(type)) {
			return ConstraintNamingUtil.getReverseUniqueName(tableName,
					getAttributes());
		}
		return null;
	}

	/**
	 * Whether the constraint is a type of index
	 *
	 * @return if the type is "INDEX", "UNIQUE",
	 *         "REVERSE INDEX","REVERSE UNIQUE", return true.
	 */
	public boolean isIndex() {
		if (Constraint.ConstraintType.INDEX.getText().equals(getType())
				|| Constraint.ConstraintType.UNIQUE.getText().equals(getType())
				|| Constraint.ConstraintType.REVERSEINDEX.getText().equals(getType())
				|| Constraint.ConstraintType.REVERSEUNIQUE.getText().equals(getType())) {
			return true;
		}
		return false;
	}

	public boolean isFK() {
		if (Constraint.ConstraintType.FOREIGNKEY.getText().equals(getType())) {
			return true;
		}
		return false;
	}

	public boolean isPK() {
		if (Constraint.ConstraintType.PRIMARYKEY.getText().equals(getType())) {
			return true;
		}
		return false;
	}

	/**
	 * Whether the attribute list is empty
	 *
	 * @return true if the attribute list is null or empty
	 */
	public boolean isEmptyAttrList() {
		if (attributes == null || attributes.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * return constraint short name
	 *
	 * @return String The short name of constraint
	 */
	public String getShortTypeName() {
		if (ConstraintType.PRIMARYKEY.getText().equals(type)) {
			return "PK";
		} else if (ConstraintType.FOREIGNKEY.getText().equals(type)) {
			return "FK";
		} else if (ConstraintType.INDEX.getText().equals(type)) {
			return "I";
		} else if (ConstraintType.REVERSEINDEX.getText().equals(type)) {
			return "RI";
		} else if (ConstraintType.UNIQUE.getText().equals(type)) {
			return "U";
		} else if (ConstraintType.REVERSEUNIQUE.getText().equals(type)) {
			return "RU";
		}
		return "";
	}

	/**
	 * clone current constraint instance, and return
	 *
	 * @return Constraint The instance of Constraint
	 */
	public Constraint clone() {
		Constraint newConstraint = null;
		try {
			newConstraint = (Constraint) super.clone();
		} catch (CloneNotSupportedException e) {
		}
		if (newConstraint == null) {
			return null;
		}
		if (classAttributes == null) {
			newConstraint.classAttributes = null;
		} else {
			newConstraint.classAttributes = new ArrayList<String>();
			for (String a : classAttributes) {
				newConstraint.classAttributes.add(a);
			}
		}
		if (attributes == null) {
			newConstraint.attributes = null;
		} else {
			newConstraint.attributes = new ArrayList<String>();
			for (String a : attributes) {
				newConstraint.attributes.add(a);
			}
		}
		if (rules == null) {
			newConstraint.rules = null;
		} else {
			newConstraint.rules = new ArrayList<String>();
			for (String a : rules) {
				newConstraint.rules.add(a);
			}
		}
		return newConstraint;
	}

	/**
	 * return description string of this for debug
	 *
	 * @return String A string includes the
	 */
	public String toString() {
		StringBuffer bf = new StringBuffer();
		bf.append("constraint name:" + this.name + "\n");
		bf.append("\ttype:" + this.type + "\n");
		List<String> list = this.getClassAttributes();
		bf.append("\tClassAttributes:\n");
		for (String str : list) {
			bf.append(str + ",");
		}

		list = this.getAttributes();
		bf.append("\tAttributes:\n");
		for (String str : list) {
			bf.append(str + ",");
		}

		list = this.getRules();
		bf.append("\tRules:\n");
		for (String str : list) {
			bf.append(str + ",");
		}
		return bf.toString();
	}

	/**
	 * Constructor
	 *
	 * @param name
	 * @param type
	 */
	public Constraint(String name, String type) {
		this.name = name;
		this.type = type;
	}

	/**
	 * The constructor
	 */
	public Constraint(boolean newFlag) {
		this.newFlag = newFlag;
	}

	/**
	 * add a rule to list
	 *
	 * @param ruleName String The given rule name
	 */
	public void addRule(String ruleName) {
		if (null == rules) {
			rules = new ArrayList<String>();
		}
		rules.add(ruleName);
	}

	/**
	 * add a class attribute to list
	 *
	 * @param classAttributeName String The give class attribute name
	 */
	public void addClassAttribute(String classAttributeName) {
		if (null == classAttributes) {
			classAttributes = new ArrayList<String>();
		}
		classAttributes.add(classAttributeName);
	}

	/**
	 * add an attribute name to list
	 *
	 * @param attributename String The given attribute name
	 */
	public void addAttribute(String attributename) {
		if (null == attributes) {
			attributes = new ArrayList<String>();
		}
		attributes.add(attributename);
	}

	public void addAllAttribute(List<String> attributename) {
		if (null == attributes) {
			attributes = new ArrayList<String>();
		}
		attributes.addAll(attributename);
	}

	/**
	 * Update the column name in the index type constraint.
	 *
	 * @param oldName
	 * @param newName
	 */
	public void updateAttrNameInIndex(String oldName, String newName) {
		if (!isIndex()) {
			return;
		}
		replaceAttribute(oldName, newName);
		replaceIndexRuleAttrName(oldName, newName);
	}

	private void replaceIndexRuleAttrName(String oldAttributeName, String newAttributeName) {

		if (rules == null) {
			return;
		}
		String pre = oldAttributeName + " ";
		List<String> rules = getRules();
		for (int i = 0, len = rules.size(); i < len; i++) {
			if (rules.get(i).startsWith(pre)) {
				String newRule = rules.get(i).replace(pre, newAttributeName + " ");
				rules.remove(i);
				rules.add(i, newRule);
			}
		}
	}

	public void removeAttribute(String name) {
		if (attributes == null || name == null) {
			return;
		}
		for (int i = attributes.size() - 1; i >= 0; i--) {
			if (StringUtil.isEqual(attributes.get(i), name)) {
				attributes.remove(i);
			}
		}

		List<String> rules = getRules();
		String rulePre = name + " ";
		for (int i = rules.size() - 1; i >= 0; i--) {
			if (!StringUtil.isEmpty(rules.get(i)) && rules.get(i).startsWith(rulePre)) {
				rules.remove(i);
			}
		}
	}

	/**
	 * If the attribute name is "" or null, remove it in the attribute list
	 */
	public void removeEmptyAttrName() {
		if (attributes == null) {
			return;
		}
		for (int i = attributes.size() - 1; i >= 0; i--) {
			if (StringUtil.isEmpty(attributes.get(i))) {
				attributes.remove(i);
			}
		}
	}

	public void replaceAttribute(String oldAttributeName, String newAttributeName) {
		if (attributes == null) {
			return;
		}

		for (int i = 0, len = attributes.size(); i < len; i++) {
			if (StringUtil.isEqualIgnoreCase(attributes.get(i), oldAttributeName)) {
				attributes.set(i, newAttributeName);
			}
		}
	}

	/**
	 * Replace the class attribute
	 */
	public void replaceClassAttribute(String oldAttributeName, String newAttributeName) {
		if (classAttributes == null) {
			return;
		}

		for (int i = 0, len = classAttributes.size(); i < len; i++) {
			if (StringUtil.isEqualIgnoreCase(classAttributes.get(i), oldAttributeName)) {
				classAttributes.set(i, newAttributeName);
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Get the class attributes
	 *
	 * @return List<String> The list of classAttributes
	 */
	public List<String> getClassAttributes() {
		if (null == classAttributes) {
			return new ArrayList<String>(0);
		}
		return classAttributes;
	}

	/**
	 * Get the attributes
	 *
	 * @return List<String> The list of attributes
	 */
	public List<String> getAttributes() {
		if (null == attributes) {
			return new ArrayList<String>(0);
		}
		return attributes;
	}

	/**
	 * Whether the constraint include the attribute.
	 *
	 * @param attrName
	 * @return
	 */
	public boolean isIncludeAttr(String attrName) {
		if (attributes == null) {
			return false;
		}

		for (int i = 0, len = attributes.size(); i < len; i++) {
			if (StringUtil.isEqual(attributes.get(i), attrName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the rules
	 *
	 * @return List<String> This list of rules
	 */
	public List<String> getRules() {
		if (null == rules) {
			return new ArrayList<String>(0);
		}
		return rules;
	}

	/**
	 * Override the hashCode method of Object.
	 *
	 * @return int A hash code value for this class this class.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attributes == null) ? 0 : attributes.hashCode());
		result = prime * result
				+ ((classAttributes == null) ? 0 : classAttributes.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((rules == null) ? 0 : rules.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/**
	 * Override the equlas method of Object.
	 *
	 * @param obj Object the reference object with which to compare
	 * @return boolean true if this object is the same as the obj argument;
	 *         false otherwise.
	 */
	@Override
	public boolean equals(Object obj) { // FIXME
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Constraint other = (Constraint) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.toLowerCase().equals(other.name.toLowerCase())) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		if (attributes == null) {
			if (other.attributes != null) {
				return false;
			}
		} else if (!attributes.equals(other.attributes)) {
			return false;
		}
		if (classAttributes == null) {
			if (other.classAttributes != null) {
				return false;
			}
		} else if (!classAttributes.equals(other.classAttributes)) {
			return false;
		}

		if (rules == null) {
			if (other.rules != null) {
				return false;
			}
		} else if (!rules.equals(other.rules)) {
			return false;
		}

		return true;
	}

	public int getKeyCount() {
		return keyCount;
	}

	public void setKeyCount(int keyCount) {
		this.keyCount = keyCount;
	}

	public void setAttributes(List<String> attributes) {
		this.attributes = attributes;
	}

	public void setRules(List<String> rules) {
		this.rules = rules;
	}

	/**
	 * String constant in request message
	 */
	// four Constraint type
	public enum ConstraintType {
		INDEX("INDEX"), UNIQUE("UNIQUE"), REVERSEINDEX("REVERSE INDEX"), REVERSEUNIQUE(
				"REVERSE UNIQUE"), FOREIGNKEY("FOREIGN KEY"), PRIMARYKEY(
				"PRIMARY KEY");

		String text = null;

		ConstraintType(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}

		/**
		 * Get the constraint type whose name is equals the given text
		 *
		 * @param text String the given constraint name
		 * @return ConstraintType
		 */
		public static ConstraintType eval(String text) {
			ConstraintType[] array = ConstraintType.values();
			for (ConstraintType a : array) {
				if (a.getText().equals(text)) {
					return a;
				}
			}
			return null;
		}
	}

	public boolean isNewFlag() {
		return newFlag;
	}

	public void setNewFlag(boolean newFlag) {
		this.newFlag = newFlag;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
