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

import static com.cubrid.common.core.util.NoOp.noOp;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;

/**
 * A Java bean indicates the info of partition
 *
 * @author sq
 * @version 1.0 - 2009-12-29 created by sq
 */
public class PartitionInfo implements Cloneable {
	private static final Logger LOGGER = LogUtil.getLogger(PartitionInfo.class);
	private String className = null;
	private String partitionName = null;
	private String partitionClassName = null;
	private PartitionType partitionType = null;
	private String partitionExpr = null;
	private String partitionExprType = null;
	private List<String> partitionValues = new ArrayList<String>();
	private int rows = -1;
	private String description;

	/**
	 * @return String A string indicates the info of this object
	 */
	@Override
	public String toString() { // FIXME ToStringBuilder
		StringBuilder sb = new StringBuilder();
		sb.append("PartitionInfo[className=").append(className);
		sb.append(",partitionName=").append(partitionName);
		sb.append(",partitionClassName=").append(partitionClassName);
		sb.append(",partitionType=").append(partitionType);
		sb.append(",partitionExpr=").append(partitionExpr);
		sb.append(",partitionValues=").append(partitionValues);
		sb.append(",rows=").append(rows).append("]");
		return sb.toString();
	}

	public PartitionInfo() {
		noOp();
	}

	public PartitionInfo(String className, PartitionType partitionType) {
		this.className = className;
		this.partitionType = partitionType;
	}

	public PartitionInfo(String className, String partitionName,
			PartitionType partitionType, String partitionExpr,
			List<String> partitionValues, int rows) {
		this.className = className;
		this.partitionName = partitionName;
		this.partitionType = partitionType;
		this.partitionExpr = partitionExpr;
		this.setPartitionValues(partitionValues);
		this.rows = rows;
	}

	public PartitionInfo(String className, String partitionName,
			String partitionClassName, PartitionType partitionType,
			String partitionExpr, List<String> partitionValues, int rows) {
		this.className = className;
		this.partitionName = partitionName;
		this.partitionClassName = partitionClassName;
		this.partitionType = partitionType;
		this.partitionExpr = partitionExpr;
		this.setPartitionValues(partitionValues);
		this.rows = rows;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getPartitionName() {
		return partitionName;
	}

	/**
	 * Set the partition name
	 *
	 * @param partitionName String the partition name
	 */
	public void setPartitionName(String partitionName) {
		this.partitionName = partitionName;
	}

	/**
	 *
	 * Get partition class name
	 *
	 * @return partitionClassName
	 */
	public String getPartitionClassName() {
		if (partitionClassName == null
				|| !partitionClassName.equals(className + "__p__"
						+ partitionName)) {
			partitionClassName = className + "__p__" + partitionName;
		}
		return partitionClassName;
	}

	public PartitionType getPartitionType() {
		return partitionType;
	}

	public void setPartitionType(PartitionType partitionType) {
		this.partitionType = partitionType;
	}

	public List<String> getPartitionValues() {
		return partitionValues;
	}

	public String getPartitionExpr() {
		return partitionExpr;
	}

	public void setPartitionExpr(String partitionExpr) {
		this.partitionExpr = partitionExpr;
	}

	public String getPartitionExprType() {
		return partitionExprType;
	}

	public void setPartitionExprType(String partitionExprType) {
		this.partitionExprType = partitionExprType;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	/**
	 * Set the partition values
	 *
	 * @param partitionValues List<String> a list that includes the partition
	 *        values
	 */
	public final void setPartitionValues(List<String> partitionValues) {
		this.partitionValues.clear();
		if (partitionValues == null) {
			return;
		}

		for (String val : partitionValues) {
			this.partitionValues.add(val);
		}
	}

	/**
	 * Remove the partition value from partitionValues(List)
	 *
	 * @param value String The given string includes the info of partition value
	 */
	public void removePartitionValue(String value) {
		for (int i = partitionValues.size() - 1; i >= 0; i--) {
			if (partitionValues.get(i).equals(value)) {
				partitionValues.remove(i);
			}
		}
	}

	/**
	 * Add a partition value into partitionValues
	 *
	 * @param value String The given string includes the info of partition value
	 */
	public void addPartitionValue(String value) {
		this.partitionValues.add(value);
	}

	/**
	 * Get a string indicates the partition values
	 *
	 * @return String a string that indicates the partition values
	 */
	public String getPartitionValuesString() {
		StringBuilder sb = new StringBuilder();
		for (String str : this.partitionValues) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append("'").append(str).append("'");
		}

		return sb.toString();
	}

	/**
	 * Get a string indicates the partition values
	 *
	 * @param useSingleQuote whether using single quote
	 * @return String a string that indicates the partition values
	 */
	public String getPartitionValuesString(boolean useSingleQuote) {
		StringBuilder sb = new StringBuilder();
		for (String str : this.partitionValues) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			if (useSingleQuote) {
				sb.append("'").append(str).append("'");
			} else {
				sb.append(str);
			}
		}

		return sb.toString();
	}

	/**
	 * @return PartitionInfo a clone of this instance
	 */
	@Override
	public PartitionInfo clone() {
		try {
			PartitionInfo partitionInfo = (PartitionInfo) super.clone();
			List<String> partitionValueList = new ArrayList<String>();
			for (int i = 0; this.partitionValues != null
					&& i < this.partitionValues.size(); i++) {
				partitionValueList.add(this.partitionValues.get(i));
			}
			partitionInfo.partitionValues = partitionValueList;
			return partitionInfo;
		} catch (CloneNotSupportedException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Check if this is equal to object
	 *
	 * @param obj the object
	 * @return <code>true</code> if equal;otherwise <code>false</code>
	 */
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof PartitionInfo)) {
			return false;
		}
		PartitionInfo partitionInfo = (PartitionInfo) obj;
		if (partitionInfo.partitionType != partitionType) {
			return false;
		}
		if (!compareStr(partitionInfo.className, this.className)) {
			return false;
		}
		if (!compareStr(partitionInfo.partitionName, this.partitionName)) {
			return false;
		}

		if (!compareStr(partitionInfo.partitionExpr, this.partitionExpr)) {
			return false;
		}
		if (partitionType == PartitionType.RANGE
				|| partitionType == PartitionType.LIST) {
			if (!compareStr(partitionInfo.getPartitionValuesString(),
					getPartitionValuesString())) {
				return false;
			}
		} else if (partitionType == PartitionType.HASH
				&& partitionInfo.getPartitionValues().size() != this.getPartitionValues().size()) {
			return false;
		}

		return true;
	}

	/**
	 * Override the hashCode method of Object
	 *
	 * @return int
	 */
	@Override
	public int hashCode() {
		return partitionName.hashCode();
	}

	/**
	 * Check if str1 is equal to str2
	 *
	 * @param str1 the string
	 * @param str2 the string
	 * @return <code>true</code> if equal;otherwise <code>false</code>
	 */
	private boolean compareStr(String str1, String str2) { // FIXME can be replaced commons utility
		if (str1 == null) {
			if (str2 != null) {
				return false;
			}
			return true;
		} else {
			if (str2 != null) {
				return str1.equals(str2);
			}
			return false;
		}
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
