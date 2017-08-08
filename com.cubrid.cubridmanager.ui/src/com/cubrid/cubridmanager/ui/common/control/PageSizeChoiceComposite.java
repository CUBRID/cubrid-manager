/*
 * Copyright (C) 2008 Search Solution Corporation. All rights reserved by Search Solution. 
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met: 
 *
 * - Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer. 
 *
 * - Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution. 
 *
 * - Neither the name of the <ORGANIZATION> nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software without 
 *   specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE. 
 *
 */
package com.cubrid.cubridmanager.ui.common.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * Page size choice composite
 * 
 * @author lizhiqiang
 * @version 1.0 - 2011-4-25created by lizhiqiang
 */
public class PageSizeChoiceComposite {

	private String groupTxt;
	private String pageBtnTxt;
	private Text pageText;
	private Text sizeText;
	private String sizeBtnTxt;
	private Button sizeBtn;
	private Button pageBtn;
	private final String[] unitItems = new String[]{"KB", "MB", "GB", "TB" };
	private Combo unitCombo;

	/**
	 * Create the content
	 * 
	 * @param composite the Composite
	 * @param isEditable the boolean
	 * @return the Composite
	 */
	public Composite createContent(final Composite composite, boolean isEditable) {
		Group group = new Group(composite, SWT.NONE);
		group.setText(groupTxt);

		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout lbLayout = new GridLayout();
		lbLayout.numColumns = 3;
		group.setLayout(lbLayout);

		pageBtn = new Button(group, SWT.RADIO);
		pageBtn.setText(pageBtnTxt);
		pageBtn.setLayoutData(new GridData());

		pageText = new Text(group, SWT.LEFT | SWT.BORDER);
		pageText.setTextLimit(8);
		GridData pageGd = new GridData(GridData.FILL_HORIZONTAL);
		pageGd.horizontalSpan = 2;
		pageText.setLayoutData(pageGd);
		pageText.setEditable(isEditable);

		sizeBtn = new Button(group, SWT.RADIO);
		sizeBtn.setText(sizeBtnTxt);
		sizeBtn.setLayoutData(new GridData());

		sizeText = new Text(group, SWT.LEFT | SWT.BORDER);
		sizeText.setTextLimit(8);
		sizeText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		sizeText.setEditable(isEditable);

		unitCombo = new Combo(group, SWT.READ_ONLY);
		unitCombo.setItems(unitItems);
		unitCombo.select(1);

		listenBtnSelection();
		return composite;
	}

	/**
	 * Init the state of button
	 * 
	 */
	public void initBtnState() {
		if (pageText != null && !"".equals(pageText.getText().trim())) {
			sizeBtn.setSelection(false);
			pageBtn.setSelection(true);
			sizeText.setEnabled(false);
			pageText.setEnabled(true);
			return;
		}
		if (sizeText != null && !"".equals(sizeText.getText().trim())) {
			sizeBtn.setSelection(true);
			pageBtn.setSelection(false);
			sizeText.setEnabled(true);
			pageText.setEnabled(false);
		}
	}

	/**
	 * Listen to the Button selection
	 * 
	 */
	private void listenBtnSelection() {
		pageBtn.addSelectionListener(new SelectionAdapter() {

			/**
			 * Sent when selection occurs in the control.
			 * 
			 * @param event
			 */
			public void widgetSelected(SelectionEvent event) {
				if (pageBtn.getSelection()) {
					sizeText.setEnabled(false);
					pageText.setEnabled(true);
				}

			}
		});
		sizeBtn.addSelectionListener(new SelectionAdapter() {
			/**
			 * Sent when selection occurs in the control.
			 * 
			 * @param event
			 */
			public void widgetSelected(SelectionEvent event) {
				if (sizeBtn.getSelection()) {
					sizeText.setEnabled(true);
					pageText.setEnabled(false);
				}

			}
		});
	}

	/**
	 * Get the page button state
	 * 
	 * @return boolean
	 */
	public boolean getPageBtnState() {
		return pageBtn.getSelection();
	}

	/**
	 * Get the size button state
	 * 
	 * @return boolean
	 */
	public boolean getSizeBtnState() {
		return sizeBtn.getSelection();
	}

	/**
	 * Set the page button state
	 * 
	 */
	public void setPageBtnSelected() {
		pageBtn.setSelection(true);
		sizeBtn.setSelection(false);
		sizeText.setEnabled(false);
		pageText.setEnabled(true);
	}

	/**
	 * Set the size button state
	 * 
	 */
	public void setSizeBtnSelected() {
		pageBtn.setSelection(false);
		sizeBtn.setSelection(true);
		sizeText.setEnabled(true);
		pageText.setEnabled(false);
	}

	/**
	 * Get the groupTxt
	 * 
	 * @return the groupTxt
	 */
	public String getGroupTxt() {
		return groupTxt;
	}

	/**
	 * @param groupTxt the groupTxt to set
	 */
	public void setGroupTxt(String groupTxt) {
		this.groupTxt = groupTxt;
	}

	/**
	 * Get the pageText
	 * 
	 * @return the pageText
	 */
	public Text getPageText() {
		return pageText;
	}

	/**
	 * @param pageText the pageText to set
	 */
	public void setPageText(Text pageText) {
		this.pageText = pageText;
	}

	/**
	 * Get the sizeText
	 * 
	 * @return the sizeText
	 */
	public Text getSizeText() {
		return sizeText;
	}

	/**
	 * @param sizeText the sizeText to set
	 */
	public void setSizeText(Text sizeText) {
		this.sizeText = sizeText;
	}

	/**
	 * @param pageBtnTxt the pageBtnTxt to set
	 */
	public void setPageBtnTxt(String pageBtnTxt) {
		this.pageBtnTxt = pageBtnTxt;
	}

	/**
	 * @param sizeBtnTxt the sizeBtnTxt to set
	 */
	public void setSizeBtnTxt(String sizeBtnTxt) {
		this.sizeBtnTxt = sizeBtnTxt;
	}

	/**
	 * Get the text of unitCombo
	 * 
	 * @return the String
	 */
	public String getUnitOfSize() {
		return unitCombo.getText().trim();
	}

	/**
	 * Set the text of unitCombo
	 * 
	 * @param str the String
	 */
	public void setUnitOfSize(String str) {
		boolean isItem = false;
		String regex = String.format("%s\\D*", str);
		for (int i = 0; i < unitItems.length; i++) {
			if (unitItems[i].matches(regex)) {
				unitCombo.select(i);
				isItem = true;
				break;
			}
		}
		if (!isItem) {
			unitCombo.setText(str);
		}
	}

}
