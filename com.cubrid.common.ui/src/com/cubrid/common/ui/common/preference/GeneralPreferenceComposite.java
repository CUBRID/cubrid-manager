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
package com.cubrid.common.ui.common.preference;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.cubrid.common.core.util.ApplicationType;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.perspective.PerspectiveManager;
import com.cubrid.common.ui.spi.util.CommonUITool;

/**
 * 
 * General perference page composite
 * 
 * @author pangqiren
 * @version 1.0 - 2009-12-23 created by pangqiren
 */
public class GeneralPreferenceComposite extends
		Composite {

	private Button maximizeWindowBtn = null;
//	private Button checkNewInfoBtn = null;
	private Button isAlwaysExitBtn = null;
	private Button isAutoCheckUpdateBtn = null;
	private Button autoCompleteKeywordBtn = null;
	private Button autoCompleteTablesOrColumnsBtn = null;
	private Button autoShowSchemaMiniInfoBtn = null;
	private Button dashboardHostBtn = null;
	private Button dashboardDatabaseBtn = null;
	/*
	private Button confirmRunModQueryAutoCommitBtn = null;
	*/

	/**
	 * The constructor
	 *
	 * @param parent
	 */
	public GeneralPreferenceComposite(Composite parent) {
		super(parent, SWT.NONE);

		createContent();
	}

	/**
	 * Create the page content
	 */
	private void createContent() {

		setLayout(new GridLayout());
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		isAlwaysExitBtn = new Button(this, SWT.CHECK);
		final GridData isAlwaysExitGd = new GridData(SWT.FILL, SWT.CENTER,
				true, false);
		isAlwaysExitBtn.setLayoutData(isAlwaysExitGd);
		isAlwaysExitBtn.setText(Messages.msgToggleExitConfirm);

//		checkNewInfoBtn = new Button(this, SWT.CHECK);
//		final GridData checkNewInfoPageGd = new GridData(SWT.FILL, SWT.CENTER,
//				true, false);
//		checkNewInfoBtn.setLayoutData(checkNewInfoPageGd);
//		checkNewInfoBtn.setText(Messages.btnCheckNewInfo);

		isAutoCheckUpdateBtn = new Button(this, SWT.CHECK);
		final GridData autoCheckUpdateBtnGd = new GridData(SWT.FILL,
				SWT.CENTER, true, false);
		isAutoCheckUpdateBtn.setLayoutData(autoCheckUpdateBtnGd);
		isAutoCheckUpdateBtn.setText(Messages.auoCheckUpdate);

		
		final Group layoutGroup = new Group(this, SWT.NONE);
		layoutGroup.setText(Messages.grpLayoutApplication);
		layoutGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		layoutGroup.setLayout(new GridLayout(2, false));
		
		maximizeWindowBtn = new Button(layoutGroup, SWT.CHECK);
		final GridData maximizeWindowBtnGd = new GridData(SWT.FILL, SWT.CENTER,
				true, false);
		maximizeWindowBtn.setLayoutData(maximizeWindowBtnGd);
		maximizeWindowBtn.setText(Messages.maximizeWindowOnStartUp);
		
		autoShowSchemaMiniInfoBtn = new Button(layoutGroup, SWT.CHECK);
		final GridData autoShowSchemaMiniInfoGd = new GridData(SWT.FILL, SWT.CENTER,
				true, false);
		autoShowSchemaMiniInfoBtn.setLayoutData(autoShowSchemaMiniInfoGd);
		autoShowSchemaMiniInfoBtn.setText(Messages.autoShowSchemaMiniInfo);
		autoShowSchemaMiniInfoBtn.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				CommonUITool.openWarningBox(Messages.warnAutoShowSchemaMiniInfo);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		final Group autoCompleteGroup = new Group(this, SWT.NONE);
		autoCompleteGroup.setText(Messages.grpIsAutoCompleteOnQueryeditor);
		autoCompleteGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		autoCompleteGroup.setLayout(new GridLayout(2, false));
		
		autoCompleteKeywordBtn = new Button(autoCompleteGroup, SWT.CHECK);
		autoCompleteKeywordBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		autoCompleteKeywordBtn.setText(Messages.btnIsAutoCompleteKeyword);
		
		autoCompleteTablesOrColumnsBtn = new Button(autoCompleteGroup, SWT.CHECK);
		autoCompleteTablesOrColumnsBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		autoCompleteTablesOrColumnsBtn.setText(Messages.btnIsAutoCompleteTablesOrColumns);

		// safe mode settings
		/*
		final Group safeModeGroup = new Group(this, SWT.NONE);
		safeModeGroup.setText(Messages.grpSafeMode);
		safeModeGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		safeModeGroup.setLayout(new GridLayout(1, false));
		confirmRunModQueryAutoCommitBtn = new Button(safeModeGroup, SWT.CHECK);
		confirmRunModQueryAutoCommitBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		confirmRunModQueryAutoCommitBtn.setText(Messages.btnConfirmRunModQueryAutoCommit);
		*/

		if (ApplicationType.CUBRID_MANAGER.equals(PerspectiveManager.getInstance().getCurrentMode())) {
			final Group openDashboardGrp = new Group(this, SWT.NONE);
			final GridData openDashboardGrpGd = new GridData(SWT.FILL, SWT.CENTER, true, false);
			openDashboardGrp.setLayoutData(openDashboardGrpGd);
			openDashboardGrp.setLayout(new GridLayout(2, false));
			openDashboardGrp.setText(Messages.grpUseDashboard);

			dashboardHostBtn = new Button(openDashboardGrp, SWT.CHECK);
			dashboardHostBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
			dashboardHostBtn.setText(Messages.btnUseDashboardHost);

			dashboardDatabaseBtn = new Button(openDashboardGrp, SWT.CHECK);
			dashboardDatabaseBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
			dashboardDatabaseBtn.setText(Messages.btnUseDashboardDatabase);
		}
	}

	/**
	 * load query option from preference store
	 */
	public void loadPreference() {
		boolean isMax = GeneralPreference.isMaximizeWindowOnStartUp();
		maximizeWindowBtn.setSelection(isMax);

		boolean isAlwaysExit = GeneralPreference.isAlwaysExit();
		isAlwaysExitBtn.setSelection(isAlwaysExit);

//		boolean isShowWelcomePage = GeneralPreference.isCheckNewInfoOnStartUp();
//		checkNewInfoBtn.setSelection(isShowWelcomePage);

		boolean isAutoCheckUpdate = GeneralPreference.isAutoCheckUpdate();
		isAutoCheckUpdateBtn.setSelection(isAutoCheckUpdate);

		boolean isAutoCompleteKeyword = GeneralPreference.isAutoCompleteKeyword();
		autoCompleteKeywordBtn.setSelection(isAutoCompleteKeyword);
		
		boolean isAutoCompleteTablesOrColumns = GeneralPreference.isAutoCompleteTablesOrColumns();
		autoCompleteTablesOrColumnsBtn.setSelection(isAutoCompleteTablesOrColumns);
		
		boolean isAutoShowSchemaInfo = GeneralPreference.isAutoShowSchemaInfo();
		autoShowSchemaMiniInfoBtn.setSelection(isAutoShowSchemaInfo);

		/*
		boolean isConfirmModifyingQuery = GeneralPreference.isShowAlertModifiedQueryOnAutoCommit();
		confirmRunModQueryAutoCommitBtn.setSelection(isConfirmModifyingQuery);
		*/

		if (ApplicationType.CUBRID_MANAGER.equals(PerspectiveManager.getInstance().getCurrentMode())) {
			boolean isUseHostDashboard = GeneralPreference.isUseHostDashboard();
			dashboardHostBtn.setSelection(isUseHostDashboard);

			boolean isUseDatabaseDashboard = GeneralPreference.isUseDatabaseDashboard();
			dashboardDatabaseBtn.setSelection(isUseDatabaseDashboard);
		}
	}

	/**
	 * 
	 * Save options
	 */
	public void save() {
		boolean isMax = maximizeWindowBtn.getSelection();
		GeneralPreference.setMaximizeWindowOnStartUp(isMax);

		boolean isAlwaysExit = isAlwaysExitBtn.getSelection();
		GeneralPreference.setAlwaysExit(isAlwaysExit);

//		boolean isShowWelcomePage = checkNewInfoBtn.getSelection();
//		GeneralPreference.setCheckNewInfoOnStartUp(isShowWelcomePage);

		boolean isAutoCheckUpdate = isAutoCheckUpdateBtn.getSelection();
		GeneralPreference.setAutoCheckUpdate(isAutoCheckUpdate);

		boolean isAutoCompleteKeyword = autoCompleteKeywordBtn.getSelection();
		GeneralPreference.setAutoCompleteKeyword(isAutoCompleteKeyword);

		boolean isAutoCompleteTablesOrColumns = autoCompleteTablesOrColumnsBtn.getSelection();
		GeneralPreference.setAutoCompleteTablesOrColumns(isAutoCompleteTablesOrColumns);

		boolean isAutoShowSchemaInfo = autoShowSchemaMiniInfoBtn.getSelection();
		GeneralPreference.setAutoShowSchemaInfo(isAutoShowSchemaInfo);

		/*
		boolean isConfirmModifyingQuery = confirmRunModQueryAutoCommitBtn.getSelection();
		GeneralPreference.setShowAlertModifiedQueryOnAutoCommit(isConfirmModifyingQuery);
		*/

		if (ApplicationType.CUBRID_MANAGER.equals(PerspectiveManager.getInstance().getCurrentMode())) {
			boolean isUseHostDashboard = dashboardHostBtn.getSelection();
			GeneralPreference.setUseHostDashboard(isUseHostDashboard);

			boolean isUseDatabaseDashboard = dashboardDatabaseBtn.getSelection();
			GeneralPreference.setUseDatabaseDashboard(isUseDatabaseDashboard);
		}
	}
}
