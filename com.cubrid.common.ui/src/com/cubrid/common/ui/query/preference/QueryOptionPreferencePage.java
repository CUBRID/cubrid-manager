/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution. 
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
package com.cubrid.common.ui.query.preference;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.util.Util;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.cubrid.common.ui.perspective.PerspectiveManager;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.spi.model.CubridServer;

/**
 * Preference page for query options
 * 
 * @author wangsl 2009-3-24
 */
public class QueryOptionPreferencePage extends
		PreferencePage implements
		IWorkbenchPreferencePage {

	public final static String ID = "com.cubrid.common.ui.preference.queryoption";
	private QueryPropertyComposite container;
	private CubridServer server = null;

	public QueryOptionPreferencePage() {
		super(Messages.queryTitle, null);
		noDefaultAndApplyButton();
	}

	public QueryOptionPreferencePage(CubridServer server) {
		super(Messages.queryTitle, null);
		noDefaultAndApplyButton();
		this.server = server;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 * @param workbench the workbench
	 */
	public void init(IWorkbench workbench) {
		//empty
	}

	/**
	 * load the preference data
	 * 
	 */
	private void loadPreference() {
		container.loadPreference();
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 *@return boolean
	 */
	public boolean performOk() {
		if (container == null || container.isDisposed()) {
			return true;
		}
		if (checkValues()) {
			container.save();
		}
		return true;
	}

	/**
	 * check input value
	 * 
	 * @return boolean
	 */
	private boolean checkValues() {
		return container.checkValid();
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 * @param parent the parent composite
	 * @return the new control
	 */
	protected Control createContents(Composite parent) {
		if (Util.isWindows()
				&& PerspectiveManager.getInstance().isManagerMode()) {
			return getBlankComposite(parent);
		}
		container = new QueryPropertyComposite(parent, server);
		loadPreference();
		return container;
	}

	/**
	 * getBlankComposite
	 * @param parent
	 * @return
	 */
	private Composite getBlankComposite(Composite parent) {
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		createInformationLabel(parent, getFontData());
		return parent;
	}

	/**
	 * createInformationLabel
	 * @param parent
	 * @param fontData
	 */
	private void createInformationLabel(Composite parent, FontData fontData) {
		Label informationLabel = new Label(parent, SWT.None);
		informationLabel.setText("Query Options are not supported on Admin mode (for Windows Platform)");
		informationLabel.setLayoutData(new GridData(GridData.FILL_BOTH));
		informationLabel.setFont(getFont(fontData));
	}

	/**
	 * getFont
	 * @param fontData
	 * @return
	 */
	private Font getFont(FontData fontData) {
		return new Font(null, fontData);
	}

	/**
	 * getFontData
	 * @return
	 */
	private FontData getFontData() {
		FontData fontData = new FontData();
		fontData.setStyle(SWT.BOLD);
		fontData.setHeight(9);
		return fontData;
	}
}