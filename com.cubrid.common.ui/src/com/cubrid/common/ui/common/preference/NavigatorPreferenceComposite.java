/*
 * Copyright (C) 2019 CUBRID Corporation. All rights reserved by CUBRID.
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
package com.cubrid.common.ui.common.preference;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.cubrid.common.ui.common.Messages;

public class NavigatorPreferenceComposite extends Composite {

	private static final String[]   TABLES_FETCH_SIZE_ITEMS   = new String[] { "100", "200", "300", "500", "1000", "2000", "3000", "5000", "ALL" };
	
	private Label                   tablesFetchSizeLbl        = null;
	private Combo                   tablesFetchSizeCmb        = null;

	public NavigatorPreferenceComposite(Composite parent) {
	    super(parent, SWT.None);
	    
	    createContent();
    }

	private void createContent() {
		setLayout(new GridLayout());
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		final Group navigatorGroup = new Group(this, SWT.NONE);
		navigatorGroup.setText(Messages.grpNavigator);
		navigatorGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		navigatorGroup.setLayout(new GridLayout(2, false));

		tablesFetchSizeLbl = new Label(navigatorGroup, SWT.NONE);
		tablesFetchSizeLbl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		tablesFetchSizeLbl.setText(Messages.lblTablesFetchSize);

		tablesFetchSizeCmb = new Combo(navigatorGroup, SWT.NONE | SWT.BORDER | SWT.READ_ONLY);
		tablesFetchSizeCmb.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		tablesFetchSizeCmb.setItems(TABLES_FETCH_SIZE_ITEMS);
    }
	
	/**
	 * load
	 */
	public void load() {
		String tablesFetchSize = NavigatorPreference.getTablesFetchSize();
		tablesFetchSizeCmb.setText(tablesFetchSize);
	}
	
	/**
	 * save
	 */
	public void save() {
		String tablesFetchSize = tablesFetchSizeCmb.getText();
		NavigatorPreference.setTablesFetchSize(tablesFetchSize);
	}
}