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

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.cubrid.common.core.common.model.Constraint;

/**
 * IndexTable Viewer Label Provider
 * 
 * @author robin 2009-6-4
 */
public class IndexTableViewerLabelProvider implements
		ITableLabelProvider {

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

		switch (columnIndex) {
		case 0:
			return null;
		case 1:
			return null;
		case 2:
			return null;
		case 3:
			return null;
		default:
			break;
		}
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
		Constraint a = (Constraint) element;
		switch (columnIndex) {
		case 0:
			return a.getName();
		case 1:
			return a.getType();
		case 2:
			List<String> columns = a.getAttributes();
			StringBuffer bf = new StringBuffer();
			int count = 0;

			for (String column : columns) {
				if (count != 0) {
					bf.append(",");
				}
				bf.append(column);
				count++;
			}

			return bf.toString();
		case 3:
			List<String> rules = a.getRules();
			StringBuffer rulebf = new StringBuffer();
			int count2 = 0;

			for (String rule : rules) {
				if (count2 != 0) {
					rulebf.append(",");
				}
				rulebf.append(rule);
				count2++;
			}

			return rulebf.toString();
		case 4:
			return a.getDescription();
		default:
			break;
		}
		return null;
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
