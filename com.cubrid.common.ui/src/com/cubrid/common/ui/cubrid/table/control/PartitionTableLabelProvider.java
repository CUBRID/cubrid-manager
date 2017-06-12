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

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.cubrid.common.core.common.model.PartitionInfo;
import com.cubrid.common.core.common.model.PartitionType;
import com.cubrid.common.core.util.PartitionUtil;

/**
 * 
 * Partition Table Label Provider
 * 
 * @author pangqiren
 * @version 1.0 - 2010-3-16 created by pangqiren
 */
public class PartitionTableLabelProvider implements
		ITableLabelProvider {

	public PartitionTableLabelProvider() {
		//empty
	}

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object,
	 *      int)
	 * @param element the object representing the entire row, or
	 *        <code>null</code> indicating that no input object is set in the
	 *        viewer
	 * @param columnIndex the zero-based index of the column in which the label
	 *        appears
	 * @return Image or <code>null</code> if there is no image for the given
	 *         object at columnIndex
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object,
	 *      int)
	 * @param element the object representing the entire row, or
	 *        <code>null</code> indicating that no input object is set in the
	 *        viewer
	 * @param columnIndex the zero-based index of the column in which the label
	 *        appears
	 * @return String or or <code>null</code> if there is no text for the given
	 *         object at columnIndex
	 */
	public String getColumnText(Object element, int columnIndex) {

		PartitionInfo item = (PartitionInfo) element;
		switch (columnIndex) {
		case 0:
			return item.getPartitionClassName();
		case 1:
			return item.getPartitionName();
		case 2:
			return item.getPartitionType().getText().toUpperCase();
		case 3:
			return item.getPartitionExpr();
		case 4:
			String partType = item.getPartitionType().getText().toUpperCase();
			PartitionType partitionType = PartitionType.valueOf(partType);
			if (partitionType == PartitionType.HASH) {
				return "";
			} else if (partitionType == PartitionType.RANGE) {
				boolean isUsingQuote = PartitionUtil.isUsingQuoteForExprValue(item.getPartitionExprType());
				String str = "{";
				if (item.getPartitionValues().get(0) != null) {
					str += (isUsingQuote ? "'" : "")
							+ item.getPartitionValues().get(0)
							+ (isUsingQuote ? "'" : "");
				}
				if (item.getPartitionValues().get(1) == null) {
					str += ",MAXVALUE}";
				} else {
					str += "," + (isUsingQuote ? "'" : "")
							+ item.getPartitionValues().get(1)
							+ (isUsingQuote ? "'" : "") + "}";
				}
				return str;
			} else {
				return "{"
						+ item.getPartitionValuesString(PartitionUtil.isUsingQuoteForExprValue(item.getPartitionExprType()))
						+ "}";
			}
		case 5:
			if (item.getRows() < 0) {
				return "";
			} else {
				return String.valueOf(item.getRows());
			}
		case 6:
			return item.getDescription() != null ? item.getDescription() : "";
		default:
			break;
		}

		return "";
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 * @param listener a label provider listener
	 */
	public void addListener(ILabelProviderListener listener) {
		//empty
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		//empty
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object,
	 *      java.lang.String)
	 * @param element the element
	 * @param property the property
	 * @return <code>true</code> if the label would be affected, and
	 *         <code>false</code> if it would be unaffected
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 * @param listener a label provider listener
	 */
	public void removeListener(ILabelProviderListener listener) {
		//empty
	}

}
